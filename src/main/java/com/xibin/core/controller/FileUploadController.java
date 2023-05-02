package com.xibin.core.controller;

import com.xibin.core.costants.CodeMaster;
import com.xibin.core.pojo.Message;
import com.xibin.core.security.pojo.MyUserDetails;
import com.xibin.core.security.util.SecurityUtil;
import com.xibin.core.utils.ImageUtils;
import com.xibin.wms.pojo.BdFittingSkuPic;
import com.xibin.wms.service.BdFittingSkuPicService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping(value = "/file", produces = {"application/json;charset=UTF-8"})
public class FileUploadController {
	@Value("${file.webPicUploadUrl}")
	private String webPicUploadUrl;
	@Value("${file.webPicUploadWithOutHost}")
	private String webPicUploadWithOutHost;
	@Autowired
	HttpSession session;
	@Autowired
	BdFittingSkuPicService bdFittingSkuPicService;
	@RequestMapping("/uploadFittingSkuPics")  
	@ResponseBody
    public Message uploadFittingSkuPics(@RequestParam MultipartFile[] pics, HttpServletRequest request) throws IOException{
		Message message = new Message();
		String fittingSkuCode = request.getParameter("fittingSkuCode");
		//MyUserDetails userDetails = SecurityUtil.getMyUserDetails();
		String realPath = request.getSession().getServletContext().getRealPath("/WEB-INF/upload/FittingSkuPic/"+fittingSkuCode);
		for(MultipartFile pic:pics){
			FileUtils.copyInputStreamToFile(pic.getInputStream(), new File(realPath, pic.getOriginalFilename()));
		}
		return message;
	}
	
	@RequestMapping(value="/uploadFittingSkuPic",consumes="multipart/form-data",method = RequestMethod.POST)  
	@ResponseBody
    public Message uploadFittingSkuPic(HttpServletRequest request,@RequestParam("file") MultipartFile pic){
		String webPicUploadUrl = this.webPicUploadUrl;
		String webPicUploadWithOutHost = this.webPicUploadWithOutHost;
		Message message = new Message();
		String fittingSkuCode = request.getParameter("fittingSkuCode");
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		//String realPath = request.getSession().getServletContext().getRealPath("/WEB-INF/upload/FittingSkuPic/"+fittingSkuCode+"-"+myUserDetails.getCompanyId());
		String realPath = webPicUploadUrl+"/fittingSku/"+fittingSkuCode+"-"+ myUserDetails.getCompanyId().toString();
		System.out.println("图片上传路径："+realPath);
		String weburl = webPicUploadWithOutHost+"/fittingSku/"+fittingSkuCode+"-"+ myUserDetails.getCompanyId().toString();
		try {
			String fileName = System.currentTimeMillis()+"";
			File originFile = new File(realPath, fileName +".jpg");
			//String fileName = pic.getOriginalFilename().substring(0,pic.getOriginalFilename().lastIndexOf("."));
			String destFileName = realPath+"/"+fileName+"-zip"+".jpg";
			FileUtils.copyInputStreamToFile(pic.getInputStream(), originFile);
			BufferedImage sourceImg = ImageIO.read(new FileInputStream(originFile));
			BdFittingSkuPic modelPic = new BdFittingSkuPic();
			modelPic.setCompanyId(myUserDetails.getCompanyId());
			modelPic.setFittingSkuPicName(fileName);
			modelPic.setFittingSkuCode(fittingSkuCode);
			modelPic.setFittingSkuPicUrl(weburl+"/"+fileName +".jpg");
			modelPic.setType(CodeMaster.PIC_TYPE_NORMAL);
			modelPic.setWidth(sourceImg.getWidth());
			modelPic.setHeight(sourceImg.getHeight());
			bdFittingSkuPicService.saveFittingSkuPic(modelPic);
			BufferedImage zipImg = ImageIO.read(new FileInputStream(ImageUtils.resizeAndSave(250, 250, originFile, destFileName)));
			BdFittingSkuPic modelPicZip = new BdFittingSkuPic();
			modelPicZip.setCompanyId(myUserDetails.getCompanyId());
			modelPicZip.setFittingSkuPicName(fileName);
			modelPicZip.setFittingSkuCode(fittingSkuCode);
			modelPicZip.setFittingSkuPicUrl(weburl+"/"+fileName+"-zip"+".jpg");
			modelPicZip.setType(CodeMaster.PIC_TYPE_ZIP);
			modelPicZip.setWidth(zipImg.getWidth(null));
			modelPicZip.setHeight(zipImg.getHeight(null));
			bdFittingSkuPicService.saveFittingSkuPic(modelPicZip);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg("文件["+pic.getOriginalFilename()+"]上传失败!");
			return message;
		} 
		message.setCode(200);
		return message;
	}
	
	@RequestMapping(value="/uploadFittingSkuPicx",consumes="multipart/form-data",method = RequestMethod.POST)  
	@ResponseBody
    public Message uploadFittingSkuPicx(HttpServletRequest request){
		Message message = new Message();
		MultipartHttpServletRequest muti = (MultipartHttpServletRequest) request;
		MultiValueMap<String, MultipartFile> map = muti.getMultiFileMap();
		String fittingSkuCode = request.getParameter("fittingSkuCode");
		String realPath = request.getSession().getServletContext().getRealPath("/WEB-INF/upload/FittingSkuPic/"+fittingSkuCode);
		for (Map.Entry<String, List<MultipartFile>> entry : map.entrySet()) {
			 
            List<MultipartFile> list = entry.getValue();
            for (MultipartFile multipartFile : list) {
            	try{
            		FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), new File(realPath, multipartFile.getOriginalFilename()));
            	}
            	catch (IOException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        			message.setCode(0);
        			message.setMsg("文件["+multipartFile.getOriginalFilename()+"]上传失败!");
        			return message;
        		} 
            }
        }
		message.setCode(200);
		message.setMsg("上传成功!");
		return message;
		
	}
	
}

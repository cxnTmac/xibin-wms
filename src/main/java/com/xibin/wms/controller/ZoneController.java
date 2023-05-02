package com.xibin.wms.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.page.pojo.Page;
import com.xibin.core.page.pojo.PageEntity;
import com.xibin.core.pojo.Message;
import com.xibin.core.security.pojo.MyUserDetails;
import com.xibin.core.security.util.SecurityUtil;
import com.xibin.wms.pojo.BdZone;
import com.xibin.wms.query.BdZoneQueryItem;
import com.xibin.wms.service.BdZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/zone", produces = {"application/json;charset=UTF-8"})
public class ZoneController {
	@Resource
	private BdZoneService bdZoneService;
	@Autowired  
	private HttpSession session;
	@RequestMapping("/showAllZone")
	@ResponseBody
	public PageEntity<BdZoneQueryItem> showAllZone(HttpServletRequest request,Model model){ 
	    // 开始分页  
		MyUserDetails userDetails = SecurityUtil.getMyUserDetails();
		PageEntity<BdZoneQueryItem> pageEntity = new PageEntity<BdZoneQueryItem>();
		Page<?> page = new Page();
		//配置分页参数
		page.setPageNo(Integer.parseInt(request.getParameter("page")));
		page.setPageSize(Integer.parseInt(request.getParameter("size")));
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		map.put("page", page);
		if(userDetails != null){
			map.put("companyId", userDetails.getCompanyId());
			map.put("warehouseId", userDetails.getWarehouseId());
		}
		List<BdZoneQueryItem> list = bdZoneService.getAllZoneByPage(map);
		pageEntity.setList(list);
		pageEntity.setSize(page.getTotalRecord());
	    return  pageEntity;
	}
	
	
	@RequestMapping("/getZoneByZoneCode")
	@ResponseBody
	public BdZoneQueryItem getZoneByZoneCode(HttpServletRequest request,Model model){
		String areaCode = request.getParameter("areaCode");
		List<BdZoneQueryItem> list = bdZoneService.selectByKey(areaCode);
		if(!list.isEmpty()){
			return list.get(0);
		}
		return new BdZoneQueryItem();
	}
	
	
	@RequestMapping("/removeZone")
	@ResponseBody
	public Message removeZone(HttpServletRequest request,Model model){
		Message message = new Message();
		int id = Integer.parseInt(request.getParameter("id"));
		String zoneCode = request.getParameter("zoneCode");
	    try {
			this.bdZoneService.removeZone(id,zoneCode);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setMsg(e.getMessage());
			message.setCode(0);
			return message;
		}
		message.setCode(200);
		message.setMsg("删除成功");
		return message;
	}

	  @RequestMapping("/batchRemove")
	  @ResponseBody
	  public Message batchRemove(@RequestParam("ids") int [] ids,@RequestParam("zoneCodes") String [] zoneCodes){
		Message message = new Message();
		StringBuffer stringBuffer = new StringBuffer();
		for(int i = 0;i<ids.length;i++){
			try {
				this.bdZoneService.removeZone(ids[i],zoneCodes[i]);
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				stringBuffer.append(e.getMessage()+"</br>");
			}
		}
		if(stringBuffer.length()>0){
			message.setMsg(stringBuffer.toString());
			message.setCode(0);
			return message;
		}
		message.setCode(200);
		message.setMsg("全部删除成功");
	    return message;
	  }
	  
	  @RequestMapping("/saveZone")
	  @ResponseBody
	  public Message saveZone(HttpServletRequest request,Model model){
		Message message = new Message();
		String str = request.getParameter("zone");
		BdZone bean = JSON.parseObject(str, BdZone.class);
		BdZone result = new BdZone();
		try {
			result = this.bdZoneService.saveZone(bean);
			message.setCode(200);
			message.setData(result);
			message.setMsg("操作成功！");
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
		}
		return message;
	  }
	  
	  
}

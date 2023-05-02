package com.xibin.wms.controller;

import com.alibaba.fastjson.JSONObject;
import com.xibin.core.page.pojo.Page;
import com.xibin.core.page.pojo.PageEntity;
import com.xibin.core.pojo.AccessToken;
import com.xibin.core.pojo.Message;
import com.xibin.core.utils.CheckUtil;
import com.xibin.core.utils.WXUtil;
import com.xibin.wms.pojo.BdFittingSkuPic;
import com.xibin.wms.pojo.BdFittingType;
import com.xibin.wms.pojo.BdModel;
import com.xibin.wms.query.BdFittingSkuQueryItem;
import com.xibin.wms.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.cors.CorsConfiguration;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
@CrossOrigin(origins = CorsConfiguration.ALL, maxAge = 3600,allowCredentials = "false")
@Controller
@RequestMapping(value = "/wx", produces = { "application/json;charset=UTF-8" })
public class WxProgramController {
	@Resource
	private BdFittingSkuService fittingSkuService;
	@Resource
	private BdFittingSkuPicService fittingSkuPicService;
	@Resource
	private BdFittingTypeService fittingTypeService;
	@Resource
	private BdModelService bdModelService;
	@Resource
	private UserService userService;
	@Autowired
	private HttpSession session;

	@RequestMapping("/showAllFittingSku")
	@ResponseBody
	public PageEntity<BdFittingSkuQueryItem> showAllFittingSku(HttpServletRequest request, Model model) {
		// 开始分页
		PageEntity<BdFittingSkuQueryItem> pageEntity = new PageEntity<BdFittingSkuQueryItem>();
		Page<?> page = new Page();
		// 配置分页参数
		page.setPageNo(Integer.parseInt(request.getParameter("page")));
		page.setPageSize(Integer.parseInt(request.getParameter("size")));
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		map.put("page", page);
		map.put("companyId", "1");
		map.put("isShow", "Y");
		List<BdFittingSkuQueryItem> list = fittingSkuService.MgetAllFittingSkuByPageWithOnePic(map);
		pageEntity.setList(list);
		pageEntity.setSize(page.getTotalRecord());
		return pageEntity;
	}

	@RequestMapping("/showAllFittingTypeWithOutPage")
	@ResponseBody
	public List<JSONObject> showAllFittingTypeWithOutPage(HttpServletRequest request, Model model) {
		Map map = new HashMap<>();
		map.put("companyId", "1");
		List<BdFittingType> typeList = fittingTypeService.getAllFittingTypeByPage(map);
		List<JSONObject> jsonList = new ArrayList<JSONObject>();
		for (BdFittingType type : typeList) {
			JSONObject jsonObject = new JSONObject();
			// 补丁代码，去除掉类别 “配件”以及“毛坯/加工费/组装配件”
			if (type.getFittingTypeCode().equals("ZZ")||type.getFittingTypeCode().equals("SP1")) {
				continue;
			}
			jsonObject.put("fittingTypeCode", type.getFittingTypeCode());
			jsonObject.put("fittingTypeName", type.getFittingTypeName());
			jsonList.add(jsonObject);
		}
		return jsonList;
	}

	@RequestMapping("/getWx")
	public void getWx(HttpServletRequest request, HttpServletResponse response, Model model) {
		PrintWriter print;
		// 微信加密签名
		String signature = request.getParameter("signature");
		// 时间戳
		String timestamp = request.getParameter("timestamp");
		// 随机数
		String nonce = request.getParameter("nonce");
		// 随机字符串
		String echostr = request.getParameter("echostr");
		System.out.println(
				"signature:" + signature + "  timestamp:" + timestamp + "  nonce:" + nonce + "  echostr:" + echostr);
		// 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
		if (signature != null && CheckUtil.checkSignature(signature, timestamp, nonce)) {
			try {
				print = response.getWriter();
				print.write(echostr);
				print.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@RequestMapping("/getFittingTypeSkuBySkuCode")
	@ResponseBody
	public BdFittingSkuQueryItem getFittingTypeSkuBySkuCode(HttpServletRequest request, Model model) {
		String fittingSkuCode = request.getParameter("fittingSkuCode");
		List<BdFittingSkuQueryItem> list = fittingSkuService.selectByKey(fittingSkuCode, "1");
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return new BdFittingSkuQueryItem();
	}

	@RequestMapping("/getFittingSkuPic")
	@ResponseBody
	public List<BdFittingSkuPic> getFittingSkuPic(HttpServletRequest request, Model model) {
		String fittingSkuCode = request.getParameter("fittingSkuCode");
		return fittingSkuPicService.selectByFittingSkuCode(fittingSkuCode, "1");
	}

	@RequestMapping("/showAllModel")
	@ResponseBody
	public List<JSONObject> MshowAllModel(HttpServletRequest request, Model model) {
		// 开始分页
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		map.put("companyId", "1");
		List<BdModel> modelList = bdModelService.getAllModelByPage(map);
		// 对车型数据做处理
		HashSet<String> set = new HashSet<String>();
		for (BdModel bdModel : modelList) {
			String modelCode = bdModel.getModelCode();

			String codes[] = modelCode.replaceAll("\\\\", "/").split("/");
			for (String code : codes) {
				set.add(code);
			}
		}
		List<JSONObject> jsonList = new ArrayList<JSONObject>();
		for (String code : set) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("modelCode", code);
			jsonObject.put("modelName", code);
			jsonList.add(jsonObject);
		}
		return jsonList;
	}

//	@RequestMapping("/getAccessTokenForLogin")
//	@ResponseBody
//	public Message getAccessTokenForLogin(HttpServletRequest request, Model model) {
//		Message message = new Message();
//		String code = request.getParameter("code");
//		System.out.println("code = " + code);
//		try {
//			AccessToken token = WXUtil.getAccessToken(code);
//			if (token.getOpenid() == null) {
//				message.setMsg("获取微信账号失败，请重新从公众号进入或点击微信登陆按钮");
//				message.setCode(0);
//			} else {
//				// 登陆流程
//				return loginByOpenId(token.getOpenid());
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			message.setMsg(e.getMessage());
//			message.setCode(0);
//		}
//		return message;
//	}

	@RequestMapping("/getAccessToken")
	@ResponseBody
	public Message getAccessToken(HttpServletRequest request, Model model) {
		Message message = new Message();
		String code = request.getParameter("code");
		System.out.println("code = " + code);
		try {
			AccessToken token = WXUtil.getAccessToken(code);
			if (token.getOpenid() == null) {
				message.setMsg("获取微信账号失败，请重新从公众号进入或点击微信登陆按钮");
				message.setCode(0);
				return message;
			} else {
				// 只返回openid
				message.setMsg(token.getOpenid());
				message.setCode(200);
				return message;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setMsg(e.getMessage());
			message.setCode(0);
			return message;
		}
	}

//	private Message loginByOpenId(String openId) {
//		Message message = new Message();
//		List<SysUser> list = this.userService.selectByWXOpenId(openId);
//		if (list.size() > 0) {
//			if (list.get(0).getIsEnable().equals("Y")) {
//				UserDetails userDetails = new UserDetails();
//				userDetails.setUserName(list.get(0).getUserName());
//				userDetails.setCompanyId(list.get(0).getCompanyId());
//				userDetails.setWarehouseId(1);
//				FiBook defaultBook = getDefaultBook(list.get(0).getCompanyId());
//				if (defaultBook != null) {
//					userDetails.setBookId(defaultBook.getId());
//					userDetails.setBookName(defaultBook.getBookName());
//					userDetails.setBeginYear(defaultBook.getBeginYear());
//					userDetails.setPeriod(defaultBook.getPeriod());
//				}
//				userDetails.setCurrentPeriod(getCurrentPeriod(list.get(0).getCompanyId()));
//				userDetails.setUserId(list.get(0).getId());
//				session.setAttribute(Constants.SESSION_USER_KEY, userDetails);
//				message.setCode(200);
//				message.setData(userDetails);
//				message.setMsg("登陆成功！");
//			} else {
//				message.setCode(0);
//				message.setMsg("用户未启用！");
//			}
//		} else {
//			message.setCode(0);
//			message.setMsg("你的微信尚未绑定账号，openId:" + openId);
//		}
//		return message;
//	}


}

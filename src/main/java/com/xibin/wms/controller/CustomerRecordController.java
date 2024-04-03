package com.xibin.wms.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.page.pojo.Page;
import com.xibin.core.page.pojo.PageEntity;
import com.xibin.core.pojo.Message;
import com.xibin.core.security.pojo.MyUserDetails;
import com.xibin.core.security.util.SecurityUtil;
import com.xibin.wms.pojo.BsCustomerRecord;
import com.xibin.wms.query.BsCustomerRecordQueryItem;
import com.xibin.wms.service.BsCustomerRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/customerRecord", produces = {"application/json;charset=UTF-8"})
public class CustomerRecordController {
	@Resource
	private BsCustomerRecordService bsCustomerRecordService;
	@Autowired  
	private HttpSession session;
	
	@RequestMapping("/showAllCustomerRecord")
	@ResponseBody
	public PageEntity<BsCustomerRecordQueryItem> showAllCustomerRecord(HttpServletRequest request, Model model){
	    // 开始分页  
		MyUserDetails userDetails = SecurityUtil.getMyUserDetails();
		PageEntity<BsCustomerRecordQueryItem> pageEntity = new PageEntity<BsCustomerRecordQueryItem>();
		Page<?> page = new Page();
		//配置分页参数
		page.setPageNo(Integer.parseInt(request.getParameter("page")));
		page.setPageSize(Integer.parseInt(request.getParameter("size")));
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		map.put("page", page);
		if(userDetails != null){
			map.put("companyId", userDetails.getCompanyId());
		}
		List<BsCustomerRecordQueryItem> list = bsCustomerRecordService.getAllBsCustomerRecordByPage(map);
		pageEntity.setList(list);
		pageEntity.setSize(page.getTotalRecord());
	    return  pageEntity;
	}

	@RequestMapping("/showAllCustomerRecordByCustomer")
	@ResponseBody
	public Message showAllCustomerRecordByCustomer(HttpServletRequest request, Model model){
		Message msg = new Message();
		MyUserDetails userDetails = SecurityUtil.getMyUserDetails();
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		if(userDetails != null){
			map.put("companyId", userDetails.getCompanyId());
		}
		try {
			Map data = bsCustomerRecordService.getAllBsCustomerRecordAndBalance(map);
			msg.setCode(200);
			msg.setData(data);
			return msg;
		} catch (BusinessException e) {
			e.printStackTrace();
			msg.setCode(0);
			msg.setMsg(e.getMessage());
			return msg;
		}
	}

	@RequestMapping("/monthReport")
	@ResponseBody
	public List<Map> monthReport(HttpServletRequest request, Model model){
		Message msg = new Message();
		MyUserDetails userDetails = SecurityUtil.getMyUserDetails();
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		if(userDetails != null){
			map.put("companyId", userDetails.getCompanyId());
		}
		return bsCustomerRecordService.monthReport(map);
	}
	@RequestMapping("/removeCustomerRecord")
	@ResponseBody
	public Message removeCustomerRecord(HttpServletRequest request,Model model){

		int id = Integer.parseInt(request.getParameter("id"));
	    try {
			return this.bsCustomerRecordService.removeBsCustomerRecord(id);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			Message message = new Message();
			e.printStackTrace();
			message.setMsg(e.getMessage());
			message.setCode(0);
			return message;
		}
	}

	  
	  @RequestMapping("/saveCustomerRecord")
	  @ResponseBody
	  public Message saveCustomerRecord(HttpServletRequest request,Model model){
		Message message = new Message();
		String str = request.getParameter("customerRecord");
		BsCustomerRecord bean = JSON.parseObject(str, BsCustomerRecord.class);
		BsCustomerRecord result = new BsCustomerRecord();
		try {
			result = this.bsCustomerRecordService.saveBsCustomerRecord(bean);
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
	@RequestMapping("/queryForVoucher")
	@ResponseBody
	public PageEntity<Map> queryForVoucher(HttpServletRequest request, Model model){
		// 开始分页
		MyUserDetails userDetails = SecurityUtil.getMyUserDetails();
		PageEntity<Map> pageEntity = new PageEntity<Map>();
		//配置分页参数
		Integer page = Integer.parseInt(request.getParameter("page"));
		Integer size = Integer.parseInt(request.getParameter("size"));
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		map.put("indexStart", (page-1)*size);
		map.put("indexEnd", page*size);
		return bsCustomerRecordService.queryForVoucher(map);
	}
	  
}

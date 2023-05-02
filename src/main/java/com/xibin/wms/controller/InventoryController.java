package com.xibin.wms.controller;

import com.alibaba.fastjson.JSONObject;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.page.pojo.Page;
import com.xibin.core.page.pojo.PageEntity;
import com.xibin.core.pojo.Message;
import com.xibin.core.security.pojo.MyUserDetails;
import com.xibin.core.security.util.SecurityUtil;
import com.xibin.wms.query.WmInventoryQueryItem;
import com.xibin.wms.service.WmInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/inventory", produces = { "application/json;charset=UTF-8" })
public class InventoryController {
	@Resource
	private WmInventoryService wmInventoryService;
	@Autowired
	private HttpSession session;

	@RequestMapping("/showAllInventory")
	@ResponseBody
	public PageEntity<WmInventoryQueryItem> showAllInventory(HttpServletRequest request, Model model) {
		// 开始分页
		MyUserDetails userDetails = SecurityUtil.getMyUserDetails();
		PageEntity<WmInventoryQueryItem> pageEntity = new PageEntity<WmInventoryQueryItem>();
		Page<?> page = new Page();
		// 配置分页参数
		page.setPageNo(Integer.parseInt(request.getParameter("page")));
		page.setPageSize(Integer.parseInt(request.getParameter("size")));
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		map.put("page", page);
		if (userDetails != null) {
			map.put("companyId", userDetails.getCompanyId());
			map.put("warehouseId", userDetails.getWarehouseId());
		}
		List<WmInventoryQueryItem> userList = wmInventoryService.getAllInventoryByPage(map);
		pageEntity.setList(userList);
		pageEntity.setSize(page.getTotalRecord());
		return pageEntity;
	}
	@RequestMapping("/queryAvaiableInventorySum")
	@ResponseBody
	public PageEntity<Map<String,Object>> queryAvaiableInventorySum(HttpServletRequest request, Model model) {
		// 开始分页
		MyUserDetails userDetails = SecurityUtil.getMyUserDetails();
		PageEntity<Map<String,Object>> pageEntity = new PageEntity<Map<String,Object>>();
		Page<?> page = new Page();
		// 配置分页参数
		page.setPageNo(Integer.parseInt(request.getParameter("page")));
		page.setPageSize(Integer.parseInt(request.getParameter("size")));
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		map.put("page", page);
		if (userDetails != null) {
			map.put("companyId", userDetails.getCompanyId().toString());
			map.put("warehouseId", userDetails.getWarehouseId().toString());
		}
		List<Map<String,Object>> userList = wmInventoryService.queryAvaiableInventorySum(map);
		pageEntity.setList(userList);
		pageEntity.setSize(page.getTotalRecord());
		return pageEntity;
	}
	@RequestMapping("/queryAvaiableInventorySumBySkuCode")
	@ResponseBody
	public Map<String,Object> queryAvaiableInventorySumBySkuCode(HttpServletRequest request, Model model) {
		// 开始分页
		MyUserDetails userDetails = SecurityUtil.getMyUserDetails();
		// 配置分页参数
		Map map = new HashMap();
		String skuCode = request.getParameter("skuCode");
		if(null!=skuCode&&!skuCode.equals("")){
			map.put("skuCode", skuCode);
			if (userDetails != null) {
				map.put("companyId", userDetails.getCompanyId().toString());
				map.put("warehouseId", userDetails.getWarehouseId().toString());
			}
			List<Map<String,Object>> list = wmInventoryService.queryAvaiableInventorySum(map);
			if(list.size()>0){
				return list.get(0);
			}else{
				return null;
			}
		}
		return null;
	}
	@RequestMapping("/showAllAvailbleInventory")
	@ResponseBody
	public PageEntity<WmInventoryQueryItem> showAllAvailbleInventory(HttpServletRequest request, Model model) {
		// 开始分页
		MyUserDetails userDetails = SecurityUtil.getMyUserDetails();
		PageEntity<WmInventoryQueryItem> pageEntity = new PageEntity<WmInventoryQueryItem>();
		Page<?> page = new Page();
		// 配置分页参数
		page.setPageNo(Integer.parseInt(request.getParameter("page")));
		page.setPageSize(Integer.parseInt(request.getParameter("size")));
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		map.put("page", page);
		if (userDetails != null) {
			map.put("companyId", userDetails.getCompanyId());
			map.put("warehouseId", userDetails.getWarehouseId());
		}
		List<WmInventoryQueryItem> userList = wmInventoryService.getAvailableInvByPage(map);
		pageEntity.setList(userList);
		pageEntity.setSize(page.getTotalRecord());
		return pageEntity;
	}

	@RequestMapping("/move")
	@ResponseBody
	public Message move(HttpServletRequest request, Model model) {
		String skuCode = request.getParameter("skuCode");
		String locCode = request.getParameter("locCode");
		String toLoc = request.getParameter("toLoc");
		double moveNum = Double.parseDouble(request.getParameter("moveNum"));
		try {
			return wmInventoryService.move(skuCode, locCode, toLoc, moveNum);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Message message = new Message();
			message.setCode(0);
			message.setMsg(e.getMessage());
			return message;
		}
	}

	@RequestMapping("/transfer")
	@ResponseBody
	public Message transfer(HttpServletRequest request, Model model) {
		String skuCode = request.getParameter("skuCode");
		String locCode = request.getParameter("locCode");
		String toLoc = request.getParameter("toLoc");
		String toSku = request.getParameter("toSku");
		double transferNum = Double.parseDouble(request.getParameter("transferNum"));
		try {
			return wmInventoryService.transfer(skuCode, locCode, toSku, toLoc, transferNum);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Message message = new Message();
			message.setCode(0);
			message.setMsg(e.getMessage());
			return message;
		}
	}

	@RequestMapping("/add")
	@ResponseBody
	public Message add(HttpServletRequest request, Model model) {
		String skuCode = request.getParameter("skuCode");
		String locCode = request.getParameter("locCode");
		double invAvailableNum = Double.parseDouble(request.getParameter("invAvailableNum"));
		double price = Double.parseDouble(request.getParameter("price"));
		try {
			return wmInventoryService.add(skuCode, locCode, invAvailableNum, price);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Message message = new Message();
			message.setCode(0);
			message.setMsg(e.getMessage());
			return message;
		}
	}
}

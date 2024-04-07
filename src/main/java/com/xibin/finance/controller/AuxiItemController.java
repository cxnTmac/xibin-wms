package com.xibin.finance.controller;

import com.alibaba.fastjson.JSONObject;
import com.xibin.finance.pojo.FItem;
import com.xibin.finance.service.FItemService;
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
@RequestMapping(value = "/auxiItem", produces = {"application/json;charset=UTF-8"})
public class AuxiItemController {
	@Resource
	private FItemService fItemService;
	@Autowired  
	private HttpSession session;
	
	@RequestMapping("/showAllAuxiItem")
	@ResponseBody
	public List<FItem> showAllAuxiItem(HttpServletRequest request, Model model){
		//配置分页参数
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		return fItemService.getAllItem(map);
	}
}

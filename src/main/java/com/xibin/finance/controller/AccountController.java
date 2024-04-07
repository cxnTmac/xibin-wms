package com.xibin.finance.controller;

import com.alibaba.fastjson.JSONObject;
import com.xibin.core.pojo.Message;
import com.xibin.finance.pojo.FAccount;
import com.xibin.finance.service.FAccountService;
import com.xibin.finance.service.SettleAccountsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/account", produces = {"application/json;charset=UTF-8"})
public class AccountController {
	@Resource
	private FAccountService fAccountService;
	@Resource
	private SettleAccountsService settleAccountsService;
	@Autowired  
	private HttpSession session;
	
	@RequestMapping("/showAllAccount")
	@ResponseBody
	public List<FAccount> showAllAccount(HttpServletRequest request, Model model){
		//配置分页参数
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		return fAccountService.getAllAccount(map);
	}
	@PostMapping("/findIsJzList")
	@ResponseBody
	public Message findIsJzList(String fdate) {
		return settleAccountsService.findIsJzList(fdate);
	}
}

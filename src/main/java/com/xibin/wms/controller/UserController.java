package com.xibin.wms.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.xibin.wms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


import com.xibin.wms.pojo.SysUser;


@Controller
@RequestMapping(value = "/user", produces = { "application/json;charset=UTF-8" })
public class UserController {
	@Resource
	private UserService userService;


	@Autowired
	private HttpSession session;

	@RequestMapping("/showUser")
	@ResponseBody
	public SysUser toIndex(HttpServletRequest request, Model model) {
		int userId = Integer.parseInt(request.getParameter("id"));
		return this.userService.getUserById(userId);
	}


}
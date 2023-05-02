package com.xibin.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.xibin.core.page.pojo.Page;
import com.xibin.core.page.pojo.PageEntity;
import com.xibin.core.pojo.Message;
import com.xibin.core.security.pojo.MyUserDetails;
import com.xibin.wms.pojo.SysCompany;
import com.xibin.wms.pojo.SysFunction;
import com.xibin.wms.pojo.SysRole;
import com.xibin.wms.pojo.SysUser;
import com.xibin.wms.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/sys", produces = { "application/json;charset=UTF-8" })
public class SysController {
	@Autowired
	private HttpSession session;
	@Autowired
	private SysCompanyService sysCompanyService;
	@Autowired
	private UserService userService;
	@Autowired
	private SysRoleService sysRoleService;
	@Autowired
	private SysFunctionService sysFunctionService;
	@Autowired
	private SysRole2FunctionService sysRole2FunctionService;

	@RequestMapping("/showAllCompanyListPage")
	@ResponseBody
	public PageEntity<SysCompany> showAllCompanyListPage(HttpServletRequest request, Model model) {
		// 开始分页
		PageEntity<SysCompany> pageEntity = new PageEntity<SysCompany>();
		Page page = new Page();
		page.setPageSize(Integer.parseInt(request.getParameter("size")));
		page.setPageNo(Integer.parseInt(request.getParameter("page")));
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		map.put("page", page);
		List<SysCompany> userList = sysCompanyService.getAllCompanyByPage(map);
		pageEntity.setList(userList);
		// pageEntity.setSize(page.getTotalRecord());
		return pageEntity;
	}

	@RequestMapping("/showAllRoleListPage")
	@ResponseBody
	public PageEntity<SysRole> showAllRoleListPage(HttpServletRequest request, Model model) {
		// 开始分页
		PageEntity<SysRole> pageEntity = new PageEntity<SysRole>();
		Page page = new Page();
		page.setPageSize(Integer.parseInt(request.getParameter("size")));
		page.setPageNo(Integer.parseInt(request.getParameter("page")));
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		map.put("page", page);
		List<SysRole> userList = sysRoleService.getAllRoleByPage(map);
		pageEntity.setList(userList);
		// pageEntity.setSize(page.getTotalRecord());
		return pageEntity;
	}

	@RequestMapping("/showAllFunctionListPage")
	@ResponseBody
	public PageEntity<SysFunction> showAllFunctionListPage(HttpServletRequest request, Model model) {
		// 开始分页
		PageEntity<SysFunction> pageEntity = new PageEntity<SysFunction>();
		Page page = new Page();
		page.setPageSize(Integer.parseInt(request.getParameter("size")));
		page.setPageNo(Integer.parseInt(request.getParameter("page")));
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		map.put("page", page);
		List<SysFunction> userList = sysFunctionService.getAllFunctionByPage(map);
		pageEntity.setList(userList);
		// pageEntity.setSize(page.getTotalRecord());
		return pageEntity;
	}

	@RequestMapping("/showAllMenus")
	@ResponseBody
	public List<SysFunction> showAllMenus(HttpServletRequest request, Model model) {
		return sysFunctionService.selectAllMenus();
	}

	@RequestMapping("/getAllRoleFunctions")
	@ResponseBody
	public List<Map<String, Object>> getAllRoleFunctions(HttpServletRequest request, Model model) {
		Message message = new Message();
		SecurityContext ctx = SecurityContextHolder.getContext();
		Authentication auth = ctx.getAuthentication();
		MyUserDetails users = (MyUserDetails) auth.getPrincipal();
		//MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		SysUser user = userService.getUserById(users.getUserId());
		return sysRole2FunctionService.selectRoleFunctionsByRoleCode(user.getRoleCode());
	}

}

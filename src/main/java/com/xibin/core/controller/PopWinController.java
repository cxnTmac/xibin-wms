package com.xibin.core.controller;

import com.alibaba.fastjson.JSON;
import com.xibin.core.page.pojo.Page;
import com.xibin.core.page.pojo.PageEntity;
import com.xibin.core.security.pojo.MyUserDetails;
import com.xibin.core.security.util.SecurityUtil;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/popWin", produces = { "application/json;charset=UTF-8" })
public class PopWinController {
	@Autowired
	@Qualifier("primarySqlSessionFactory")
	SqlSessionFactory primarySqlSessionFactory;
	@Qualifier("backSqlSessionFactory")
	SqlSessionFactory backSqlSessionFactory;

	/**
	 * 带分页的放大镜弹出框公用查询方法
	 * 
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping("/query")
	@ResponseBody
	public PageEntity<?> query(HttpServletRequest request, Model model) {
		PageEntity pageEntity = new PageEntity();
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		Map map = JSON.parseObject(request.getParameter("queryConditions"));
		Page<?> page = new Page();
		if ("Y".equals(request.getParameter("isPage"))) {

			// 配置分页参数
			page.setPageNo(Integer.parseInt(request.getParameter("page")));
			page.setPageSize(Integer.parseInt(request.getParameter("size")));
			map.put("page", page);
		}
		map.put("companyId", myUserDetails.getCompanyId());
		SqlSession sqlsession = null;
		if (request.getParameter("sys").equals("fin")) {
			// map.put("bookId", myUserDetails.getBookId());
			sqlsession = backSqlSessionFactory.openSession();
		}else{
			sqlsession = primarySqlSessionFactory.openSession();
		}
		List<?> list = sqlsession
				.selectList(getClassPath(request.getParameter("sys")) + request.getParameter("queryType"), map);
		sqlsession.close();
		pageEntity.setList(list);
		if ("Y".equals(request.getParameter("isPage"))) {
			pageEntity.setSize(page.getTotalRecord());
		}
		return pageEntity;
	}

	private String getClassPath(String sys) {
		return "com.xibin." + sys + ".dao.";
	}
}

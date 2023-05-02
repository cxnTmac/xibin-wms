package com.xibin.wms.controller;

import com.xibin.core.page.pojo.Page;
import com.xibin.core.page.pojo.PageEntity;
import com.xibin.core.security.pojo.MyUserDetails;
import com.xibin.wms.pojo.PieChartForCurrentMonthSaleByType;
import com.xibin.wms.service.WmChartsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.xibin.core.security.util.SecurityUtil;
import com.alibaba.fastjson.JSONObject;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/charts", produces = { "application/json;charset=UTF-8" })
public class ChartsController {
	@Resource
	private WmChartsService wmChartsService;

	@RequestMapping("/pieChartForCurrentMonthSaleByType")
	@ResponseBody
	public List<PieChartForCurrentMonthSaleByType> pieChartForCurrentMonthSaleByType(HttpServletRequest request,
			Model model) {
		String day = request.getParameter("day");
		String month = request.getParameter("month");
		Map map = new HashMap();
		map.put("day",day);
		map.put("month",month);
		return wmChartsService.pieChartForCurrentMonthSaleByType(map);
	}
	@RequestMapping("/lineChartForCurrentAndPreMonthSaleByType")
	@ResponseBody
	public Map<String,Object> lineChartForCurrentAndPreMonthSaleByType(HttpServletRequest request,
																					 Model model) {
		String month = request.getParameter("month");
		String preMonth = request.getParameter("preMonth");
		Map map = new HashMap();
		map.put("month",month);
		List<PieChartForCurrentMonthSaleByType> currentMonthData = wmChartsService.pieChartForCurrentMonthSaleByType(map);
		map.put("month",preMonth);
		List<PieChartForCurrentMonthSaleByType> preMonthData = wmChartsService.pieChartForCurrentMonthSaleByType(map);
		Map<String,Object> result = new HashMap();
		result.put("currentMonth",currentMonthData);
		result.put("preMonth",preMonthData);
		return result;
	}
	@RequestMapping("/pieChartAndMapForMonthSaleByProvince")
	@ResponseBody
	public List<Map<String, Object>> pieChartAndMapForMonthSaleByProvince(HttpServletRequest request, Model model) {
		String year = request.getParameter("year");
		String month = request.getParameter("month");
		Map map = new HashMap();
		map.put("year",year);
		map.put("month",month);
		return wmChartsService.pieChartAndMapForMonthSaleByProvince(map);
	}
	@RequestMapping("/lineChartAndMapForCurrentAndPreMonthSaleByProvince")
	@ResponseBody
	public Map<String, Object> lineChartAndMapForCurrentAndPreMonthSaleByProvince(HttpServletRequest request, Model model) {
		String month = request.getParameter("month");
		String preMonth = request.getParameter("preMonth");
		Map map = new HashMap();
		map.put("month",month);
		List<Map<String, Object>> currentMonthData = wmChartsService.pieChartAndMapForMonthSaleByProvince(map);
		map.put("month",preMonth);
		List<Map<String, Object>> preMonthData = wmChartsService.pieChartAndMapForMonthSaleByProvince(map);
		Map<String,Object> result = new HashMap();
		result.put("currentMonth",currentMonthData);
		result.put("preMonth",preMonthData);
		return result;
	}
	@RequestMapping("/monthSaleByDate")
	@ResponseBody
	public List<Map<String, Object>> monthSaleByDate(HttpServletRequest request, Model model) {
		String month = request.getParameter("month");
		Map map = new HashMap();
		map.put("month",month);
		return wmChartsService.monthSaleByDate(map);
	}
	@RequestMapping("/yearSaleByMonth")
	@ResponseBody
	public List<Map<String, Object>> yearSaleByMonth(HttpServletRequest request, Model model) {
		String year = request.getParameter("year");
		Map map = new HashMap();
		map.put("month",year);
		return wmChartsService.yearSaleByMonth(map);
	}

	@RequestMapping("/turnoverRate")
	@ResponseBody
	public PageEntity<Map<String, Object>> turnoverRate(HttpServletRequest request, Model model) {
		// 开始分页
		MyUserDetails userDetails = SecurityUtil.getMyUserDetails();
		PageEntity<Map<String, Object>> pageEntity = new PageEntity<>();
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
		List<Map<String, Object>> list = wmChartsService.turnoverRate(map);
		pageEntity.setList(list);
		pageEntity.setSize(page.getTotalRecord());
		return  pageEntity;
	}
}

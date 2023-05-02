package com.xibin.wms.service;

import com.xibin.wms.pojo.PieChartForCurrentMonthSaleByType;

import java.util.List;
import java.util.Map;

public interface WmChartsService {

	public List<PieChartForCurrentMonthSaleByType> pieChartForCurrentMonthSaleByType(Map map);

	public List<Map<String, Object>> pieChartAndMapForMonthSaleByProvince(Map map);

	public List<Map<String, Object>> monthSaleByDate(Map map);

	public List<Map<String, Object>> yearSaleByMonth(Map map);

	public List<Map<String, Object>> turnoverRate(Map map);
}

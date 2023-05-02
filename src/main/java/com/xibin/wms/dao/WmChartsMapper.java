package com.xibin.wms.dao;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.wms.pojo.PieChartForCurrentMonthSaleByType;

import java.util.List;
import java.util.Map;

public interface WmChartsMapper extends BaseMapper {

	List<PieChartForCurrentMonthSaleByType> pieChartForCurrentMonthSaleByType(Map map);

	List<Map<String, Object>> pieChartAndMapForMonthSaleByProvince(Map map);

	List<Map<String, Object>> monthSaleByDate(Map map);

	List<Map<String, Object>> yearSaleByMonth(Map map);

	List<Map<String, Object>> turnoverRate(Map map);
}
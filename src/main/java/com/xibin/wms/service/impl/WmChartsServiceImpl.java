package com.xibin.wms.service.impl;

import com.xibin.core.daosupport.BaseManagerImpl;
import com.xibin.core.daosupport.BaseMapper;
import com.xibin.core.security.pojo.MyUserDetails;
import com.xibin.core.security.util.SecurityUtil;
import com.xibin.wms.dao.WmChartsMapper;
import com.xibin.wms.pojo.PieChartForCurrentMonthSaleByType;
import com.xibin.wms.service.WmChartsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@Service
public class WmChartsServiceImpl extends BaseManagerImpl implements WmChartsService {
	@Autowired
	private HttpSession session;
	@Autowired
	private WmChartsMapper wmChartsMapper;

	@Override
	public List<PieChartForCurrentMonthSaleByType> pieChartForCurrentMonthSaleByType(Map map) {
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		map.put("companyId", myUserDetails.getCompanyId().toString());
		map.put("warehouseId", myUserDetails.getWarehouseId().toString());
		return wmChartsMapper.pieChartForCurrentMonthSaleByType(map);
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> pieChartAndMapForMonthSaleByProvince(Map map) {
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		map.put("companyId", myUserDetails.getCompanyId().toString());
		map.put("warehouseId", myUserDetails.getWarehouseId().toString());
		return wmChartsMapper.pieChartAndMapForMonthSaleByProvince(map);
	}

	@Override
	public List<Map<String, Object>> monthSaleByDate(Map map) {
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		map.put("companyId", myUserDetails.getCompanyId().toString());
		map.put("warehouseId", myUserDetails.getWarehouseId().toString());
		return wmChartsMapper.monthSaleByDate(map);
	}

	@Override
	public List<Map<String, Object>> yearSaleByMonth(Map map) {
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		map.put("companyId", myUserDetails.getCompanyId().toString());
		map.put("warehouseId", myUserDetails.getWarehouseId().toString());
		return wmChartsMapper.yearSaleByMonth(map);
	}

	@Override
	public List<Map<String, Object>> turnoverRate(Map map) {
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		map.put("companyId", myUserDetails.getCompanyId().toString());
		map.put("warehouseId", myUserDetails.getWarehouseId().toString());
		return wmChartsMapper.turnoverRate(map);
	}

}

package com.xibin.wms.dao;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.wms.pojo.WmOutboundHeader;
import com.xibin.wms.query.WmOutboundHeaderQueryItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface WmOutboundHeaderMapper extends BaseMapper {
	WmOutboundHeader selectByPrimaryKey(Integer id);

	List<WmOutboundHeaderQueryItem> selectAllByPage(Map map);

	List<WmOutboundHeaderQueryItem> selectByKey(@Param("orderNo") String orderNo, @Param("companyId") String companyId,
                                                @Param("warehouseId") String warehouseId);

	List<WmOutboundHeader> selectByExample(WmOutboundHeader example);

	List<String> queryOrderNosByStatus(Map map);

	List<Map> queryForOutboundDaily(Map map);

	void updateCalculateByOrderNos(Map map);

	void updateCostCalculateByOrderNos(Map map);

	String selectNextOrderNo(Map map);

	String selectPreOrderNo(Map map);

	Map selectRecentOrderHeaderByBuyerCode(Map map);
}
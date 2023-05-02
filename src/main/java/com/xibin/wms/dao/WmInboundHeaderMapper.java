package com.xibin.wms.dao;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.wms.pojo.WmInboundHeader;
import com.xibin.wms.query.WmInboundHeaderQueryItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface WmInboundHeaderMapper extends BaseMapper {
	WmInboundHeader selectByPrimaryKey(Integer id);

	List<WmInboundHeaderQueryItem> selectAllByPage(Map map);

	List<WmInboundHeaderQueryItem> selectByKey(@Param("orderNo") String orderNo, @Param("companyId") String companyId,
                                               @Param("warehouseId") String warehouseId);

	List<WmInboundHeader> selectByExample(WmInboundHeader example);

	List<String> queryOrderNosByStatus(Map map);

	void updateStatusByOrderNos(Map map);

	void updateCostCalculateByOrderNos(Map map);

	String selectNextOrderNo(Map map);

	String selectPreOrderNo(Map map);
}
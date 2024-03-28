package com.xibin.wms.dao;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.wms.pojo.WmOutboundAlloc;
import com.xibin.wms.query.WmOutboundAllocQueryItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface WmOutboundAllocMapper extends BaseMapper {
	WmOutboundAlloc selectByPrimaryKey(Integer id);

	List<WmOutboundAllocQueryItem> selectAllByPage(Map map);

	List<WmOutboundAllocQueryItem> selectForMobileAlloc(Map map);

	List<Map> selectForReAlloc(Map map);

	List<WmOutboundAllocQueryItem> selectByKey(@Param("orderNo") String orderNo, @Param("lineNo") String lineNo,
                                               @Param("companyId") String companyId, @Param("warehouseId") String warehouseId);

	List<WmOutboundAlloc> selectByExample(WmOutboundAlloc example);

	Double querySumCostForAccount(Map map);

	Double queryLCLSumByOrderNo(Map map);

	Double queryFCLSumByOrderNo(Map map);

	Integer selectUniqueKey(@Param("allocId") String allocId, @Param("companyId") String companyId,
                            @Param("warehouseId") String warehouseId);

	void changeLocForNotClosedOrder(Map map);
}
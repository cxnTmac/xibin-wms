package com.xibin.wms.dao;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.wms.pojo.WmInventory;
import com.xibin.wms.query.WmInventoryQueryItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface WmInventoryMapper extends BaseMapper {
	WmInventory selectByPrimaryKey(Integer id);

	List<WmInventoryQueryItem> selectAllByPage(Map map);

	List<WmInventoryQueryItem> selectByKey(@Param("skuCode") String orderNo, @Param("locCode") String locCode,
                                           @Param("lot") String lot, @Param("companyId") String companyId, @Param("warehouseId") String warehouseId);

	List<WmInventory> selectByExample(WmInventory example);

	List<WmInventory> getAvailableInvByExample(WmInventory example);

	List<WmInventory> getStorageInvByExample(WmInventory example);

	List<WmInventory> getVirtualInvByExample(WmInventory example);

	List<WmInventoryQueryItem> getAvailableInvByPage(Map map);

	Double getCostBySkuCode(Map map);

	Map<String, Object> getMaxInventoryBySkuCode(Map map);
}
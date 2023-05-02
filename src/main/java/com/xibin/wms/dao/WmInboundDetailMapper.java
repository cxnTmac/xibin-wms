package com.xibin.wms.dao;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.wms.pojo.WmInboundDetail;
import com.xibin.wms.query.WmInboundDetailQueryItem;
import com.xibin.wms.query.WmInboundDetailSaleHistoryQueryItem;
import com.xibin.wms.query.WmInboundDetailSkuQueryItem;
import com.xibin.wms.query.WmInboundDetailSumPriceQueryItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface WmInboundDetailMapper extends BaseMapper {
	WmInboundDetail selectByPrimaryKey(Integer id);

	int updateStatusByKey(@Param("orderNo") String orderNo, @Param("lineNos") String lineNos,
                          @Param("status") String status, @Param("companyId") String companyId,
                          @Param("warehouseId") String warehouseId);

	List<WmInboundDetailQueryItem> selectAllByPage(Map map);

	List<WmInboundDetailQueryItem> selectClosedOrderDetail(Map map);

	List<WmInboundDetailQueryItem> selectByKey(@Param("orderNo") String orderNo, @Param("lineNo") String lineNo,
                                               @Param("companyId") String companyId, @Param("warehouseId") String warehouseId);

	List<WmInboundDetail> selectByExample(WmInboundDetail example);

	List<Integer> selectLastLineNo(@Param("orderNo") String orderNo, @Param("companyId") String companyId,
                                   @Param("warehouseId") String warehouseId);

	List<WmInboundDetailSumPriceQueryItem> querySumPriceGroupBySupplierForAccount(Map map);
	List<WmInboundDetailSumPriceQueryItem> querySumPriceGroupByOrderNoForAccount(Map map);

	List<WmInboundDetailSkuQueryItem> queryWmInboundDetailByPage(Map map);

	List<WmInboundDetailSaleHistoryQueryItem> queryHistorySale(Map map);

	void deleteByOrderNo(@Param("orderNo") String orderNo, @Param("companyId") String companyId,
						 @Param("warehouseId") String warehouseId);
}
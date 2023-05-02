package com.xibin.wms.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.xibin.core.exception.BusinessException;
import com.xibin.core.pojo.Message;
import com.xibin.wms.pojo.WmInboundDetail;
import com.xibin.wms.query.WmInboundDetailQueryItem;
import com.xibin.wms.query.WmInboundDetailSaleHistoryQueryItem;
import com.xibin.wms.query.WmInboundDetailSkuQueryItem;

public interface WmInboundDetailService {
	public WmInboundDetail getInboundDetailById(int userId);

	public List<WmInboundDetail> getInboundDetailByIds(String[] ids);

	public List<WmInboundDetailQueryItem> getAllInboundDetailByPage(Map map);

	public List<WmInboundDetailQueryItem> selectByKey(String orderNo, String lineNo);

	public List<WmInboundDetail> selectByExample(WmInboundDetail model);

	public WmInboundDetail saveInboundDetail(WmInboundDetail model) throws BusinessException;

	public int removeInboundDetail(int[] ids, String orderNo) throws BusinessException;

	public int updateStatusByKey(String orderNo, String lineNos, String status);

	public List<WmInboundDetailQueryItem> selectClosedOrderDetail(Map map);

	public List<WmInboundDetailSkuQueryItem> queryWmInboundDetailByPage(Map map);

	public Message importByExcel(MultipartFile file, String orderNo, String skuCodeColumnName, String priceColumnName,
                                 String numColumnName, String locCodeColumnName) throws BusinessException;

	public List<WmInboundDetailSaleHistoryQueryItem> queryHistorySale(Map map);

	WmInboundDetail saveInboundDetailWithOutRecieve(WmInboundDetail model, boolean isCheck) throws BusinessException;
}

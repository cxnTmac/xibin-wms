package com.xibin.wms.service;

import com.xibin.core.exception.BusinessException;
import com.xibin.wms.pojo.WmOutboundHeader;
import com.xibin.wms.query.WmOutboundHeaderQueryItem;

import java.util.List;
import java.util.Map;

public interface WmOutboundHeaderService {
	public WmOutboundHeader getOutboundOrderById(int userId);

	public List<WmOutboundHeaderQueryItem> getAllOutboundOrderByPage(Map map);

	public List<WmOutboundHeaderQueryItem> selectByKey(String orderNo);

	public List<WmOutboundHeader> selectByExample(WmOutboundHeader model);

	public WmOutboundHeaderQueryItem saveOutbound(WmOutboundHeader model) throws BusinessException;

	public int remove(String orderNo) throws BusinessException;

	public WmOutboundHeaderQueryItem audit(String orderNo) throws BusinessException;

	public WmOutboundHeaderQueryItem cancelAudit(String orderNo) throws BusinessException;

	public WmOutboundHeaderQueryItem close(String orderNo) throws BusinessException;

	public WmOutboundHeaderQueryItem account(String orderNo) throws BusinessException;

	public WmOutboundHeaderQueryItem newAccount(String orderNo) throws BusinessException;

	public WmOutboundHeaderQueryItem cancelAccount(String orderNo) throws BusinessException;

	public String selectNextOrderNo(String orderTime);

	public String selectPreOrderNo(String orderTime);

	public List<Map> queryForOutboundDaily(Map map);

	public Map selectRecentOrderHeaderByBuyerCode(String buyerCode);
}

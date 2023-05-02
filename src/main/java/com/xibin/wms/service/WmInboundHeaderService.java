package com.xibin.wms.service;

import com.xibin.core.exception.BusinessException;
import com.xibin.wms.pojo.WmInboundHeader;
import com.xibin.wms.query.WmInboundHeaderQueryItem;

import java.util.List;
import java.util.Map;

public interface WmInboundHeaderService {
	public WmInboundHeader getInboundOrderById(int userId);

	public List<WmInboundHeaderQueryItem> getAllInboundOrderByPage(Map map);

	public List<WmInboundHeaderQueryItem> selectByKey(String orderNo);

	public List<WmInboundHeader> selectByExample(WmInboundHeader model);

	public WmInboundHeaderQueryItem saveInbound(WmInboundHeader model) throws BusinessException;

	public int remove(String orderNo) throws BusinessException;

	public WmInboundHeaderQueryItem audit(String orderNo) throws BusinessException;

	public WmInboundHeaderQueryItem close(String orderNo) throws BusinessException;

	public WmInboundHeaderQueryItem cancelAudit(String orderNo) throws BusinessException;

	public String selectNextOrderNo(String orderNo);

	public String selectPreOrderNo(String orderNo);

	public WmInboundHeaderQueryItem account(String orderNo) throws BusinessException;

	public WmInboundHeaderQueryItem cancelAccount(String orderNo) throws BusinessException;
}

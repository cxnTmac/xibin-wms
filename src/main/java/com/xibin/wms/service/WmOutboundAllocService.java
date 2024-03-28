package com.xibin.wms.service;

import com.xibin.core.exception.BusinessException;
import com.xibin.core.pojo.Message;
import com.xibin.wms.pojo.WmOutboundAlloc;
import com.xibin.wms.query.WmOutboundAllocQueryItem;

import java.util.List;
import java.util.Map;

public interface WmOutboundAllocService {
	public WmOutboundAlloc getOutboundAllocById(int userId);

	public List<WmOutboundAllocQueryItem> getAllOutboundAllocByPage(Map map);

	public List<WmOutboundAllocQueryItem> selectForMobileAlloc(Map map);

	public List<Map> selectForReAlloc(Map map);

	public List<WmOutboundAllocQueryItem> selectByKey(String orderNo, String linNo);

	public List<WmOutboundAlloc> selectByExample(WmOutboundAlloc model);

	public void pickByAlloc(WmOutboundAlloc alloc, double pickNum,boolean isPackedSku,int packageNum) throws BusinessException;

	public void cancelPickByAlloc(WmOutboundAlloc alloc) throws BusinessException;

	public void shipByAlloc(WmOutboundAlloc alloc, boolean isUpdateOrder) throws BusinessException;

	public void cancelShipByAlloc(WmOutboundAlloc alloc, boolean isUpdateOrder) throws BusinessException;

	public WmOutboundAlloc saveOutboundAlloc(WmOutboundAlloc model) throws BusinessException;

	public WmOutboundAlloc saveOutboundAllocForEditCost(WmOutboundAlloc model) throws BusinessException;

	public Message shipByHeader(String orderNo) throws BusinessException;

	public Message cancelShipByHeader(String orderNo) throws BusinessException;

	public Message pickByOrderNo(String orderNo) throws BusinessException;

	public void packByAlloc(WmOutboundAlloc alloc) throws BusinessException;

	public void cancelPackByAlloc(WmOutboundAlloc alloc) throws BusinessException;

	public Double getTotalPackageNumByOrderNo(String orderNo);

	public void reAllocByAlloc(WmOutboundAlloc alloc) throws BusinessException;

	public void reAllocByAllocId(int allocId) throws BusinessException;

}

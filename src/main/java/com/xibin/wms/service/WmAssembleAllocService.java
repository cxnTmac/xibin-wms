package com.xibin.wms.service;

import java.util.List;
import java.util.Map;

import com.xibin.core.exception.BusinessException;
import com.xibin.core.pojo.Message;
import com.xibin.wms.pojo.WmAssembleAlloc;
import com.xibin.wms.query.WmAssembleAllocQueryItem;

public interface WmAssembleAllocService {
	public WmAssembleAlloc getAssembleAllocById(int id);

	public List<WmAssembleAllocQueryItem> getAllAssembleAllocByOrderNo(Map map);

	public List<WmAssembleAlloc> selectByKey(String allocId);

	public List<WmAssembleAlloc> selectByExample(WmAssembleAlloc model);

	public WmAssembleAlloc saveAssembleAlloc(WmAssembleAlloc model) throws BusinessException;

	public Message pickByAlloc(WmAssembleAlloc alloc) throws BusinessException;

	public Message cancelPickByAlloc(WmAssembleAlloc alloc) throws BusinessException;

	Message pickByAllocId(String allocId) throws BusinessException;

}

package com.xibin.finance.service;

import com.xibin.core.exception.BusinessException;
import com.xibin.finance.pojo.FItem;
import com.xibin.wms.pojo.BdArea;

import java.util.List;
import java.util.Map;

public interface FItemService {
	public FItem getItemById(int id);
	
	public List<FItem> getAllItemByPage(Map map);

	List<FItem> getAllItem(Map map);

	public int removeItem(int id, String areaCode) throws BusinessException;
	
	public FItem saveItem(BdArea model) throws BusinessException;
	
	public List<FItem> selectByKey(String itemID);
}

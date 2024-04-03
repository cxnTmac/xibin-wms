package com.xibin.wms.service;

import com.xibin.core.daosupport.BaseModel;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.page.pojo.PageEntity;
import com.xibin.core.pojo.Message;
import com.xibin.wms.pojo.BsCustomerRecord;
import com.xibin.wms.query.BsCustomerRecordQueryItem;

import java.util.List;
import java.util.Map;

public interface BsCustomerRecordService {
	public BsCustomerRecord getBsCustomerRecordById(int id);

	PageEntity<Map> queryForVoucher(Map map);

    public List<BsCustomerRecordQueryItem> getAllBsCustomerRecordByPage(Map map);
	
	public Message removeBsCustomerRecord(int id) throws BusinessException;

	public BsCustomerRecord saveBsCustomerRecord(BsCustomerRecord model) throws BusinessException;

	public BaseModel save(BaseModel baseModel);

	public int delete(int id);

	public int delete(int []ids);

	public List<BsCustomerRecord> selectByExample(BsCustomerRecord model);

	public Map getAllBsCustomerRecordAndBalance(Map map) throws BusinessException;

	public List<Map> monthReport(Map map);

}

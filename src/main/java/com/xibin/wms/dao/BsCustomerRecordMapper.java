package com.xibin.wms.dao;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.wms.pojo.BsCustomerRecord;
import com.xibin.wms.query.BsCustomerRecordQueryItem;

import java.util.List;
import java.util.Map;

public interface BsCustomerRecordMapper extends BaseMapper{
    BsCustomerRecord selectByPrimaryKey(Integer id);
    
    List<BsCustomerRecordQueryItem> selectAllByPage(Map map);
    
    List<BsCustomerRecord> selectByExample(BsCustomerRecord example);

    Double  getBalanceByCustomerCode(Map map);

    List<Map> monthReport(Map map);
}
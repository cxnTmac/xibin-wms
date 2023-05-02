package com.xibin.wms.dao;

import com.xibin.core.daosupport.BaseMapper;

import java.util.List;
import java.util.Map;

public interface WmAvaiableInventoryViewMapper extends BaseMapper {

	List<Map<String, Object>> selectAllByPage(Map map);
}
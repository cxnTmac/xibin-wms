package com.xibin.wms.service;

import java.util.List;
import java.util.Map;

import com.xibin.wms.pojo.SysRole2Function;

public interface SysRole2FunctionService {
	public SysRole2Function getRole2FunctionById(int userId);

	public List<SysRole2Function> getAllRole2FunctionByPage(Map map);

	public List<SysRole2Function> selectByExample(SysRole2Function example);

	public List<SysRole2Function> selectByKey(String roleCode, String functionCode);

	public List<Map<String, Object>> selectRoleFunctionsByRoleCode(String roleCode);

}

package com.xibin.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.wms.pojo.SysRole2Function;

public interface SysRole2FunctionMapper extends BaseMapper {
	SysRole2Function selectByPrimaryKey(Integer id);

	List<SysRole2Function> selectAllByPage(Map map);

	List<SysRole2Function> selectByKey(@Param("roleCode") String roleCode, @Param("functionCode") String functionCode,
                                       @Param("companyId") String companyId);

	List<SysRole2Function> selectByExample(SysRole2Function example);

	List<Map<String, Object>> selectRoleFunctionsByRoleCode(@Param("roleCode") String roleCode,
                                                            @Param("companyId") String companyId);
}
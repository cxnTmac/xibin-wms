package com.xibin.wms.service.impl;

import com.xibin.core.daosupport.BaseManagerImpl;
import com.xibin.core.daosupport.BaseMapper;
import com.xibin.core.security.pojo.MyUserDetails;
import com.xibin.core.security.util.SecurityUtil;
import com.xibin.wms.dao.SysRole2FunctionMapper;
import com.xibin.wms.pojo.SysRole2Function;
import com.xibin.wms.service.SysRole2FunctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@Service
public class SysRole2FunctionServiceImpl extends BaseManagerImpl implements SysRole2FunctionService {
	@Autowired
	private HttpSession session;
	@Resource
	private SysRole2FunctionMapper sysRole2FunctionMapper;

	@Override
	public SysRole2Function getRole2FunctionById(int userId) {
		return this.sysRole2FunctionMapper.selectByPrimaryKey(userId);
	}

	@Override
	public List<SysRole2Function> getAllRole2FunctionByPage(Map map) {
		// TODO Auto-generated method stub
		return this.sysRole2FunctionMapper.selectAllByPage(map);
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return sysRole2FunctionMapper;
	}

	@Override
	public List<SysRole2Function> selectByExample(SysRole2Function example) {
		return this.sysRole2FunctionMapper.selectByExample(example);
	}

	@Override
	public List<SysRole2Function> selectByKey(String roleCode, String functionCode) {
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		return this.sysRole2FunctionMapper.selectByKey(roleCode, functionCode, myUserDetails.getCompanyId().toString());
	}

	@Override
	public List<Map<String, Object>> selectRoleFunctionsByRoleCode(String roleCode) {
		// TODO Auto-generated method stub
		// MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		SecurityContext ctx = SecurityContextHolder.getContext();
		Authentication auth = ctx.getAuthentication();
		MyUserDetails myUserDetails = (MyUserDetails) auth.getPrincipal();
		return this.sysRole2FunctionMapper.selectRoleFunctionsByRoleCode(roleCode,
				myUserDetails.getCompanyId().toString());
	}

}

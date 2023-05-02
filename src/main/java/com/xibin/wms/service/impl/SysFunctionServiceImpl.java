package com.xibin.wms.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.xibin.core.daosupport.BaseManagerImpl;
import com.xibin.core.daosupport.BaseMapper;
import com.xibin.wms.constants.WmsCodeMaster;
import com.xibin.wms.dao.SysFunctionMapper;
import com.xibin.wms.pojo.SysFunction;
import com.xibin.wms.service.SysFunctionService;

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@Service
public class SysFunctionServiceImpl extends BaseManagerImpl implements SysFunctionService {
	@Autowired
	private HttpSession session;
	@Resource
	private SysFunctionMapper sysFunctionMapper;

	@Override
	public SysFunction getFunctionById(int userId) {
		return this.sysFunctionMapper.selectByPrimaryKey(userId);
	}

	@Override
	public List<SysFunction> getAllFunctionByPage(Map map) {
		// TODO Auto-generated method stub
		return this.sysFunctionMapper.selectAllByPage(map);
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return sysFunctionMapper;
	}

	@Override
	public List<SysFunction> selectByExample(SysFunction example) {
		return this.sysFunctionMapper.selectByExample(example);
	}

	@Override
	public List<SysFunction> selectAllMenus() {
		SysFunction queryExample = new SysFunction();
		queryExample.setType(WmsCodeMaster.FUNCTION_M.getCode());
		return this.selectByExample(queryExample);
	}

}

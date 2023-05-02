package com.xibin.wms.service.impl;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.xibin.core.security.pojo.MyUserDetails;
import com.xibin.core.security.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.xibin.core.costants.Constants;
import com.xibin.core.daosupport.BaseManagerImpl;
import com.xibin.core.daosupport.BaseMapper;
import com.xibin.core.exception.BusinessException;
import com.xibin.wms.dao.BdLocMapper;
import com.xibin.wms.pojo.BdLoc;
import com.xibin.wms.query.BdLocQueryItem;
import com.xibin.wms.service.BdLocService;

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@Service
public class BdLocServiceImpl extends BaseManagerImpl implements BdLocService {
	@Autowired
	private HttpSession session;
	@Autowired
	private BdLocMapper bdLocMapper;

	@Override
	public BdLoc getLocById(int id) {
		// TODO Auto-generated method stub
		return bdLocMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<BdLocQueryItem> getAllLocByPage(Map map) {
		// TODO Auto-generated method stub
		return bdLocMapper.selectAllByPage(map);
	}

	@Override
	public int removeLoc(int id, String locCde) throws BusinessException {
		// TODO Auto-generated method stub
		if (!deleteBeforeCheck(locCde)) {
			throw new BusinessException("该库位编码[" + locCde + "]已被库区引用，不能删除");
		} else {
			int[] ids = { id };
			return this.delete(ids);
		}
	}

	@Override
	public BdLoc saveLoc(BdLoc model) throws BusinessException {
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		model.setCompanyId(myUserDetails.getCompanyId());
		model.setWarehouseId(myUserDetails.getWarehouseId());
		List<BdLocQueryItem> list = bdLocMapper.selectByKey(model.getLocCode(), model.getCompanyId().toString(),
				model.getWarehouseId().toString());
		if (list.size() > 0 && model.getId() == 0) {
			throw new BusinessException("编码：[" + model.getLocCode() + "] 已存在，不能重复！");
		}
		return (BdLoc) this.save(model);
	}

	@Override
	public List<BdLocQueryItem> selectByKey(String locCode) {
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		return bdLocMapper.selectByKey(locCode, myUserDetails.getCompanyId().toString(),
				myUserDetails.getWarehouseId().toString());
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return bdLocMapper;
	}

	private boolean deleteBeforeCheck(String areaCode) {
		return true;
	}
}

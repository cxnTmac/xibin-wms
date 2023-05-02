package com.xibin.wms.service.impl;

import com.xibin.core.daosupport.BaseManagerImpl;
import com.xibin.core.daosupport.BaseMapper;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.security.pojo.MyUserDetails;
import com.xibin.core.security.util.SecurityUtil;
import com.xibin.wms.dao.BdFittingSkuMapper;
import com.xibin.wms.dao.BdFittingTypeMapper;
import com.xibin.wms.pojo.BdFittingSku;
import com.xibin.wms.pojo.BdFittingType;
import com.xibin.wms.service.BdFittingTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@Service
public class BdFittingTypeServiceImpl extends BaseManagerImpl implements BdFittingTypeService {
	@Autowired
	HttpSession session;
	@Resource
	private BdFittingTypeMapper bdFittingTypeMapper;

	@Resource
	private BdFittingSkuMapper bdFittingSkuMapper;

	@Override
	public BdFittingType getFittingTypeById(int id) {
		// TODO Auto-generated method stub
		return bdFittingTypeMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<BdFittingType> getAllFittingTypeByPage(Map map) {
		// TODO Auto-generated method stub
		return bdFittingTypeMapper.selectAllByPage(map);
	}

	@Override
	public int removeFittingType(int id, String fittingTypeCode) throws BusinessException {
		// TODO Auto-generated method stub
		if (!deleteBeforeCheck(fittingTypeCode)) {
			throw new BusinessException("该配件类别编码[" + fittingTypeCode + "]已被引用，不能删除");
		} else {
			int[] ids = { id };
			return this.delete(ids);
		}
	}

	@Override
	public BdFittingType saveFittingType(BdFittingType model) throws BusinessException {
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		model.setCompanyId(myUserDetails.getCompanyId());
		List<BdFittingType> list = bdFittingTypeMapper.selectByKey(model.getFittingTypeCode(),
				model.getCompanyId().toString());
		if (list.size() > 0 && model.getId() == 0) {
			throw new BusinessException("编码：[" + model.getFittingTypeCode() + "] 已存在，不能重复！");
		}
		return (BdFittingType) this.save(model);
	}

	@Override
	public List<BdFittingType> selectByKey(String fittingTypeCode) {
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		return bdFittingTypeMapper.selectByKey(fittingTypeCode, myUserDetails.getCompanyId().toString());
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return bdFittingTypeMapper;
	}

	private boolean deleteBeforeCheck(String fittingTypeCode) {
		BdFittingSku bdFittingSku = new BdFittingSku();
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		bdFittingSku.setCompanyId(myUserDetails.getCompanyId());
		bdFittingSku.setFittingTypeCode(fittingTypeCode);
		List<BdFittingSku> results = bdFittingSkuMapper.selectByExample(bdFittingSku);
		if (results.size() > 0) {
			return false;
		} else {
			return true;
		}
	}
}

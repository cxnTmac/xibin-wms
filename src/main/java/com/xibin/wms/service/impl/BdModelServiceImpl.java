package com.xibin.wms.service.impl;

import com.xibin.core.daosupport.BaseManagerImpl;
import com.xibin.core.daosupport.BaseMapper;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.security.pojo.MyUserDetails;
import com.xibin.core.security.util.SecurityUtil;
import com.xibin.wms.dao.BdFittingSkuMapper;
import com.xibin.wms.dao.BdModelMapper;
import com.xibin.wms.pojo.BdFittingSku;
import com.xibin.wms.pojo.BdModel;
import com.xibin.wms.service.BdModelService;
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
public class BdModelServiceImpl extends BaseManagerImpl implements BdModelService {
	@Autowired
	HttpSession session;
	@Resource
	private BdModelMapper bdModelMapper;
	@Resource
	private BdFittingSkuMapper bdFittingSkuMapper;

	@Override
	public BdModel getModelById(int id) {
		// TODO Auto-generated method stub
		return bdModelMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<BdModel> getAllModelByPage(Map map) {
		// TODO Auto-generated method stub
		return bdModelMapper.selectAllByPage(map);
	}

	@Override
	public int removeModel(int id, String modelCode) throws BusinessException {
		// TODO Auto-generated method stub
		if (!deleteBeforeCheck(modelCode)) {
			throw new BusinessException("该车型编码[" + modelCode + "]已被引用，不能删除");
		} else {
			int[] ids = { id };
			return this.delete(ids);
		}
	}

	@Override
	public BdModel saveModel(BdModel model) throws BusinessException {
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		model.setCompanyId(myUserDetails.getCompanyId());
		List<BdModel> list = bdModelMapper.selectByKey(model.getModelCode(), model.getCompanyId().toString());
		if (list.size() > 0 && model.getId() == 0) {
			throw new BusinessException("编码：[" + model.getModelCode() + "] 已存在，不能重复！");
		}
		return (BdModel) this.save(model);
	}

	@Override
	public List<BdModel> selectByKey(String modelCode) {
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		return bdModelMapper.selectByKey(modelCode, myUserDetails.getCompanyId().toString());
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return bdModelMapper;
	}

	private boolean deleteBeforeCheck(String modelCode) {
		BdFittingSku bdFittingSku = new BdFittingSku();
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		bdFittingSku.setCompanyId(myUserDetails.getCompanyId());
		bdFittingSku.setModelCode(modelCode);
		List<BdFittingSku> results = bdFittingSkuMapper.selectByExample(bdFittingSku);
		if (results.size() > 0) {
			return false;
		} else {
			return true;
		}
	}

}

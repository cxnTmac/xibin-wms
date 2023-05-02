package com.xibin.wms.service.impl;

import com.xibin.core.daosupport.BaseManagerImpl;
import com.xibin.core.daosupport.BaseMapper;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.security.pojo.MyUserDetails;
import com.xibin.core.security.util.SecurityUtil;
import com.xibin.wms.dao.BdAreaMapper;
import com.xibin.wms.pojo.BdArea;
import com.xibin.wms.service.BdAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@Service
public class BdAreaServiceImpl extends BaseManagerImpl implements BdAreaService {
	@Autowired
	private HttpSession session;
	@Autowired
	private BdAreaMapper bdAreaMapper;

	@Override
	public BdArea getAreaById(int id) {
		// TODO Auto-generated method stub
		return bdAreaMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<BdArea> getAllAreaByPage(Map map) {
		// TODO Auto-generated method stub
		return bdAreaMapper.selectAllByPage(map);
	}

	@Override
	public int removeArea(int id, String areaCode) throws BusinessException {
		// TODO Auto-generated method stub
		if (!deleteBeforeCheck(areaCode)) {
			throw new BusinessException("该区域编码[" + areaCode + "]已被库区引用，不能删除");
		} else {
			int[] ids = { id };
			return this.delete(ids);
		}
	}

	@Override
	public BdArea saveArea(BdArea model) throws BusinessException {
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		model.setCompanyId(myUserDetails.getCompanyId());
		model.setWarehouseId(myUserDetails.getWarehouseId());
		List<BdArea> list = bdAreaMapper.selectByKey(model.getAreaCode(), model.getCompanyId().toString(),
				model.getWarehouseId().toString());
		if (list.size() > 0 && model.getId() == 0) {
			throw new BusinessException("编码：[" + model.getAreaCode() + "] 已存在，不能重复！");
		}
		return (BdArea) this.save(model);
	}

	@Override
	public List<BdArea> selectByKey(String areaCode) {
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		return bdAreaMapper.selectByKey(areaCode, myUserDetails.getCompanyId().toString(),
				myUserDetails.getWarehouseId().toString());
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return bdAreaMapper;
	}

	private boolean deleteBeforeCheck(String areaCode) {
		return true;
	}
}

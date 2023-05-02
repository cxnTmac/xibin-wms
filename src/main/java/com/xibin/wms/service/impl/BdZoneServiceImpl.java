package com.xibin.wms.service.impl;

import com.xibin.core.daosupport.BaseManagerImpl;
import com.xibin.core.daosupport.BaseMapper;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.security.pojo.MyUserDetails;
import com.xibin.core.security.util.SecurityUtil;
import com.xibin.wms.dao.BdZoneMapper;
import com.xibin.wms.pojo.BdZone;
import com.xibin.wms.query.BdZoneQueryItem;
import com.xibin.wms.service.BdZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@Service
public class BdZoneServiceImpl extends BaseManagerImpl implements BdZoneService {
	@Autowired
	private HttpSession session;
	@Autowired
	private BdZoneMapper bdZoneMapper;

	@Override
	public BdZone getZoneById(int id) {
		// TODO Auto-generated method stub
		return bdZoneMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<BdZoneQueryItem> getAllZoneByPage(Map map) {
		// TODO Auto-generated method stub
		return bdZoneMapper.selectAllByPage(map);
	}

	@Override
	public int removeZone(int id, String zoneCode) throws BusinessException {
		// TODO Auto-generated method stub
		if (!deleteBeforeCheck(zoneCode)) {
			throw new BusinessException("该区域编码[" + zoneCode + "]已被库区引用，不能删除");
		} else {
			int[] ids = { id };
			return this.delete(ids);
		}
	}

	@Override
	public BdZone saveZone(BdZone model) throws BusinessException {
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		model.setCompanyId(myUserDetails.getCompanyId());
		model.setWarehouseId(myUserDetails.getWarehouseId());
		List<BdZoneQueryItem> list = bdZoneMapper.selectByKey(model.getZoneCode(), model.getCompanyId().toString(),
				model.getWarehouseId().toString());
		if (list.size() > 0 && model.getId() == 0) {
			throw new BusinessException("编码：[" + model.getZoneCode() + "] 已存在，不能重复！");
		}
		return (BdZone) this.save(model);
	}

	@Override
	public List<BdZoneQueryItem> selectByKey(String zoneCode) {
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		return bdZoneMapper.selectByKey(zoneCode, myUserDetails.getCompanyId().toString(),
				myUserDetails.getWarehouseId().toString());
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return bdZoneMapper;
	}

	private boolean deleteBeforeCheck(String areaCode) {
		return true;
	}
}

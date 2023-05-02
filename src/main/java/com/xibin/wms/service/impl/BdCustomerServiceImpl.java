package com.xibin.wms.service.impl;

import com.xibin.core.daosupport.BaseManagerImpl;
import com.xibin.core.daosupport.BaseMapper;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.security.pojo.MyUserDetails;
import com.xibin.core.security.util.SecurityUtil;
import com.xibin.finance.dao.FItemDao;
import com.xibin.finance.pojo.FItem;
import com.xibin.wms.dao.BdCustomerMapper;
import com.xibin.wms.pojo.BdCustomer;
import com.xibin.wms.query.BdCustomerQueryItem;
import com.xibin.wms.service.BdCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@Service
public class BdCustomerServiceImpl extends BaseManagerImpl implements BdCustomerService {
	@Autowired
	private HttpSession session;
	@Autowired
	private BdCustomerMapper bdCustomerMapper;
	@Autowired
	private FItemDao fItemDao;
	@Override
	public BdCustomer getCustomerById(int id) {
		// TODO Auto-generated method stub
		return bdCustomerMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<BdCustomerQueryItem> getAllCustomerByPage(Map map) {
		// TODO Auto-generated method stub
		return bdCustomerMapper.selectAllByPage(map);
	}

	@Override
	public int removeCustomer(int id, String customerCode) throws BusinessException {
		// TODO Auto-generated method stub
		if (!deleteBeforeCheck(customerCode)) {
			throw new BusinessException("该客户编码[" + customerCode + "]已被库区引用，不能删除");
		} else {
			int[] ids = { id };
			BdCustomer bc = bdCustomerMapper.selectByPrimaryKey(id);
			FItem item = fItemDao.getById(bc.getAuxiId());
			item.setIsDelete(1);
			fItemDao.updateFItem(item);
			bc.setStatus("DELETE");
			this.save(bc);
			return 1;
		}
	}

	@Override
	public BdCustomer saveCustomer(BdCustomer model) throws BusinessException {
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		model.setCompanyId(myUserDetails.getCompanyId());
		List<BdCustomerQueryItem> list = bdCustomerMapper.selectByKey(model.getCustomerCode(),
				model.getCompanyId().toString());
		if (list.size() > 0 && model.getId() == 0) {
			throw new BusinessException("编码：[" + model.getCustomerCode() + "] 已存在，不能重复！");
		}
		FItem item = new FItem();
		if(model.getAuxiId()!=null){
			item = fItemDao.getById(model.getAuxiId());
		}
		if(model.getCustomerType().contains("销售客户")){
			item.setItemClassID((long)18);
			item.setNumber(model.getCustomerCode());
			item.setName(model.getCustomerName());
			item.setShortNumber(model.getCustomerCode());
			item.setFullName(model.getCustomerName());
			if(item.getItemID()!=null){
				fItemDao.updateFItem(item);
			}else{
				item.setParentID((long)0);
				item.setLevel(1);
				item.setDetail(0);
				item.setIsDelete(0);
				item.setItemClassID((long)18);
				fItemDao.saveFItem(item);
			}
			model.setAuxiId(item.getItemID().intValue());
		}else if(model.getCustomerType().contains("供应商")){
			item.setItemClassID((long)8);
			item.setNumber(model.getCustomerCode());
			item.setName(model.getCustomerName());
			item.setShortNumber(model.getCustomerCode());
			item.setFullName(model.getCustomerName());
			if(item.getItemID()!=null){
				fItemDao.updateFItem(item);
			}else{
				item.setParentID((long)0);
				item.setLevel(1);
				item.setDetail(0);
				item.setIsDelete(0);
				item.setItemClassID((long)8);
				fItemDao.saveFItem(item);
			}
			model.setAuxiId(item.getItemID().intValue());
		}
		return (BdCustomer) this.save(model);
	}

	@Override
	public List<BdCustomerQueryItem> selectByKey(String customerCode) {
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		return bdCustomerMapper.selectByKey(customerCode, myUserDetails.getCompanyId().toString());
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return bdCustomerMapper;
	}

	private boolean deleteBeforeCheck(String areaCode) {
		return true;
	}
}

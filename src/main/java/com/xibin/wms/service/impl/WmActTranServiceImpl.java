package com.xibin.wms.service.impl;

import com.xibin.core.daosupport.BaseManagerImpl;
import com.xibin.core.daosupport.BaseMapper;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.security.pojo.MyUserDetails;
import com.xibin.core.security.util.SecurityUtil;
import com.xibin.core.utils.CodeGenerator;
import com.xibin.wms.dao.WmActTranMapper;
import com.xibin.wms.pojo.WmActTran;
import com.xibin.wms.query.WmActTranQueryItem;
import com.xibin.wms.service.WmActTranService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@Service
public class WmActTranServiceImpl extends BaseManagerImpl implements WmActTranService {
	@Autowired
	private HttpSession session;
	@Autowired
	private WmActTranMapper wmActTranMapper;

	@Override
	public WmActTran getActTranById(int id) {
		// TODO Auto-generated method stub
		return wmActTranMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<WmActTranQueryItem> getAllActTranByPage(Map map) {
		// TODO Auto-generated method stub
		return wmActTranMapper.selectAllByPage(map);
	}

	@Override
	public WmActTran saveActTran(WmActTran model) throws BusinessException {
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		model.setCompanyId(myUserDetails.getCompanyId());
		model.setWarehouseId(myUserDetails.getWarehouseId());
		model.setTranOp(myUserDetails.getUserId());
		model.setTranId(CodeGenerator.getCodeByCurrentTimeAndRandomNum("TRA"));
		return (WmActTran) this.save(model);
	}

	@Override
	public List<WmActTranQueryItem> selectByKey(String tranId) {
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		return wmActTranMapper.selectByKey(tranId, myUserDetails.getCompanyId().toString(),
				myUserDetails.getWarehouseId().toString());
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return wmActTranMapper;
	}

}

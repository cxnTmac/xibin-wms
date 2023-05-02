package com.xibin.wms.service.impl;

import com.xibin.core.daosupport.BaseManagerImpl;
import com.xibin.core.daosupport.BaseMapper;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.security.pojo.MyUserDetails;
import com.xibin.core.security.util.SecurityUtil;
import com.xibin.core.utils.CodeGenerator;
import com.xibin.wms.constants.WmsCodeMaster;
import com.xibin.wms.dao.WmAssembleHeaderMapper;
import com.xibin.wms.pojo.WmAssembleHeader;
import com.xibin.wms.service.WmAssembleHeaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@Service
public class WmAssembleHeaderServiceImpl extends BaseManagerImpl implements WmAssembleHeaderService {
	@Autowired
	private HttpSession session;
	@Resource
	private WmAssembleHeaderMapper wmAssembleHeaderMapper;

	@Override
	public List<WmAssembleHeader> getAllAssembleOrderByPage(Map map) {
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		map.put("compayId", myUserDetails.getCompanyId());
		map.put("warehouseId", myUserDetails.getWarehouseId());
		return wmAssembleHeaderMapper.selectAllByPage(map);
	}

	@Override
	public List<WmAssembleHeader> selectByKey(String orderNo) {
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		return wmAssembleHeaderMapper.selectByKey(orderNo, myUserDetails.getCompanyId().toString(),
				myUserDetails.getWarehouseId().toString());
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return wmAssembleHeaderMapper;
	}

	@Override
	public WmAssembleHeader saveAssembleOrder(WmAssembleHeader model) throws BusinessException {
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		model.setCompanyId(myUserDetails.getCompanyId());
		model.setWarehouseId(myUserDetails.getWarehouseId());
		if (null == model.getId() || model.getId() == 0) {
			model.setOrderNo(CodeGenerator.getCodeByCurrentTimeAndRandomNum("ASS"));
		}
		this.save(model);
		List<WmAssembleHeader> list = selectByKey(model.getOrderNo());
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public int remove(String orderNo) throws BusinessException {
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		List<WmAssembleHeader> queryResult = this.selectByKey(orderNo);
		if (queryResult.size() > 0) {
			WmAssembleHeader assembleHeader = queryResult.get(0);
			if (WmsCodeMaster.AUDIT_CLOSE.getCode().equals(assembleHeader.getAuditStatus())) {
				throw new BusinessException("组装单[" + assembleHeader.getOrderNo() + "]已审核");
			} else if (!"00".equals(assembleHeader.getStatus())) {
				throw new BusinessException("组装单[" + assembleHeader.getOrderNo() + "]不是创建状态");
			}
			return this.delete(assembleHeader.getId());
		} else {
			throw new BusinessException("组装单[" + orderNo + "]不存在");
		}
	}

	@Override
	public WmAssembleHeader getAssembleOrderById(int id) {
		// TODO Auto-generated method stub
		return wmAssembleHeaderMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<WmAssembleHeader> selectByExample(WmAssembleHeader model) {
		// TODO Auto-generated method stub
		return wmAssembleHeaderMapper.selectByExample(model);
	}

	@Override
	public WmAssembleHeader audit(String orderNo) throws BusinessException {
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		List<WmAssembleHeader> queryResult = this.selectByKey(orderNo);
		if (queryResult.size() > 0) {
			WmAssembleHeader wmAssembleHeader = new WmAssembleHeader();
			wmAssembleHeader = queryResult.get(0);
			if (WmsCodeMaster.AUDIT_CLOSE.getCode().equals(wmAssembleHeader.getAuditStatus())) {
				throw new BusinessException("组装单[" + wmAssembleHeader.getOrderNo() + "]已审核");
			} else if (!WmsCodeMaster.INB_NEW.getCode().equals(wmAssembleHeader.getStatus())) {
				throw new BusinessException("组装单[" + wmAssembleHeader.getOrderNo() + "]不是创建状态");
			}
			wmAssembleHeader.setAuditStatus(WmsCodeMaster.AUDIT_CLOSE.getCode());
			wmAssembleHeader.setAuditor(myUserDetails.getUserId());
			wmAssembleHeader.setAuditDate(new Date());
			return this.saveAssembleOrder(wmAssembleHeader);
		} else {
			throw new BusinessException("组装单[" + orderNo + "]不存在");
		}
	}

	@Override
	public WmAssembleHeader cancelAudit(String orderNo) throws BusinessException {
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		List<WmAssembleHeader> queryResult = this.selectByKey(orderNo);
		if (queryResult.size() > 0) {
			WmAssembleHeader wmAssembleHeader = new WmAssembleHeader();
			wmAssembleHeader = queryResult.get(0);
			if (WmsCodeMaster.AUDIT_NEW.getCode().equals(wmAssembleHeader.getAuditStatus())) {
				throw new BusinessException("组装单[" + wmAssembleHeader.getOrderNo() + "]未审核");
			} else if (!WmsCodeMaster.INB_NEW.getCode().equals(wmAssembleHeader.getStatus())) {
				throw new BusinessException("组装单[" + wmAssembleHeader.getOrderNo() + "]不是创建状态");
			}
			wmAssembleHeader.setAuditStatus(WmsCodeMaster.AUDIT_NEW.getCode());
			wmAssembleHeader.setAuditor(myUserDetails.getUserId());
			wmAssembleHeader.setAuditDate(new Date());
			return this.saveAssembleOrder(wmAssembleHeader);
		} else {
			throw new BusinessException("组装单[" + orderNo + "]不存在");
		}
	}
}

package com.xibin.wms.service.impl;

import com.xibin.core.daosupport.BaseManagerImpl;
import com.xibin.core.daosupport.BaseMapper;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.pojo.Message;
import com.xibin.core.security.pojo.MyUserDetails;
import com.xibin.core.security.util.SecurityUtil;
import com.xibin.wms.dao.BdFittingSkuGroupMapper;
import com.xibin.wms.dao.BdFittingSkuMapper;
import com.xibin.wms.pojo.BdFittingSku;
import com.xibin.wms.pojo.BdFittingSkuGroup;
import com.xibin.wms.query.BdFittingSkuGroupQueryItem;
import com.xibin.wms.service.BdFittingSkuGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@Service
public class BdFittingSkuGroupServiceImpl extends BaseManagerImpl implements BdFittingSkuGroupService {
	@Autowired
	private HttpSession session;
	@Autowired
	private BdFittingSkuGroupMapper bdFittingSkuGroupMapper;
	@Autowired
	private BdFittingSkuMapper bdFittingSkuMapper;

	@Override
	public BdFittingSkuGroup getSkuGroupById(int id) {
		// TODO Auto-generated method stub
		return bdFittingSkuGroupMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<BdFittingSkuGroupQueryItem> getAllSkuGropuByPage(Map map) {
		// TODO Auto-generated method stub
		return bdFittingSkuGroupMapper.selectAllByPage(map);
	}

	@Override
	public int removeSkuGroup(int id, String groupCode) throws BusinessException {
		// TODO Auto-generated method stub
		Message message = deleteBeforeCheck(groupCode);
		if (message.getCode() == 0) {
			throw new BusinessException(message.getMsg());
		} else {
			int[] ids = { id };
			return this.delete(ids);
		}
	}

	@Override
	public BdFittingSkuGroup saveSkuGroup(BdFittingSkuGroup model) throws BusinessException {
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		model.setCompanyId(myUserDetails.getCompanyId());
		List<BdFittingSkuGroup> list = bdFittingSkuGroupMapper.selectByKey(model.getGroupCode(),
				model.getCompanyId().toString());
		if (list.size() > 0 && model.getId() == 0) {
			throw new BusinessException("编码：[" + model.getGroupCode() + "] 已存在，不能重复！");
		}
		return (BdFittingSkuGroup) this.save(model);
	}

	@Override
	public List<BdFittingSkuGroup> selectByKey(String groupCode) {
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		return bdFittingSkuGroupMapper.selectByKey(groupCode, myUserDetails.getCompanyId().toString());
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return bdFittingSkuGroupMapper;
	}

	private Message deleteBeforeCheck(String groupCode) {
		Message message = new Message();
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		BdFittingSku example = new BdFittingSku();
		example.setGroupCode(groupCode);
		example.setCompanyId(myUserDetails.getCompanyId());
		List<BdFittingSku> results = bdFittingSkuMapper.selectByExample(example);
		if (results.size() == 0) {
			message.setCode(200);
		} else {
			StringBuffer stringBuffer = new StringBuffer();
			for (BdFittingSku sku : results) {
				stringBuffer.append('[' + sku.getFittingSkuCode() + ']');
			}
			message.setCode(0);
			message.setMsg("通用组编码已经被以下产品" + stringBuffer.toString() + "使用，无法删除！");
		}
		return message;
	}

	@Override
	public List<BdFittingSkuGroupQueryItem> queryItemByKey(String groupCode) {
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		return bdFittingSkuGroupMapper.queryItemByKey(groupCode, myUserDetails.getCompanyId().toString());
	}
}

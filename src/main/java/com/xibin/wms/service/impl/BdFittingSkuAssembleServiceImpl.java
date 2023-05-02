package com.xibin.wms.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
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
import com.xibin.core.pojo.Message;
import com.xibin.wms.dao.BdFittingSkuAssembleMapper;
import com.xibin.wms.pojo.BdFittingSkuAssemble;
import com.xibin.wms.query.BdFittingSkuAssembleQueryItem;
import com.xibin.wms.service.BdFittingSkuAssembleService;

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@Service
public class BdFittingSkuAssembleServiceImpl extends BaseManagerImpl implements BdFittingSkuAssembleService {
	@Autowired
	private HttpSession session;

	@Resource
	private BdFittingSkuAssembleMapper bdFittingSkuAssembleMapper;

	@Override
	public BdFittingSkuAssemble getFittingSkuById(int id) {
		// TODO Auto-generated method stub
		return bdFittingSkuAssembleMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<BdFittingSkuAssembleQueryItem> getAllFittingSkuByFSkuCode(Map map) {
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		map.put("companyId", myUserDetails.getCompanyId());
		return bdFittingSkuAssembleMapper.selectAllByFSkuCode(map);
	}

	@Override
	public Message saveFittingSkuAssemble(List<BdFittingSkuAssemble> details,
			List<BdFittingSkuAssemble> removeDetails) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		if (removeDetails.size() > 0) {
			int[] ids = new int[removeDetails.size()];
			for (int i = 0; i < ids.length; i++) {
				ids[i] = removeDetails.get(i).getId();
			}
			// 把原来的分录全部删除
			this.delete(ids);
		}
		if (details.size() > 0) {
			for (BdFittingSkuAssemble detail : details) {
				detail.setCompanyId(myUserDetails.getCompanyId());
			}
			// 一次性多条保存
			this.save(details);
		}
		msg.setMsg("保存成功！");
		msg.setCode(200);
		return msg;
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return bdFittingSkuAssembleMapper;
	}

}

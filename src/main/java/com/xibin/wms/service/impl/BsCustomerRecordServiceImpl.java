package com.xibin.wms.service.impl;

import com.xibin.core.daosupport.BaseManagerImpl;
import com.xibin.core.daosupport.BaseMapper;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.pojo.Message;
import com.xibin.core.security.pojo.MyUserDetails;
import com.xibin.core.security.util.SecurityUtil;
import com.xibin.wms.dao.BdCustomerMapper;
import com.xibin.wms.dao.BsCustomerRecordMapper;
import com.xibin.wms.pojo.BsCustomerRecord;
import com.xibin.wms.query.BdCustomerQueryItem;
import com.xibin.wms.query.BsCustomerRecordQueryItem;
import com.xibin.wms.service.BsCustomerRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@Service
public class BsCustomerRecordServiceImpl extends BaseManagerImpl implements BsCustomerRecordService {
	@Autowired
	private HttpSession session;
	@Autowired
	private BsCustomerRecordMapper bsCustomerRecordMapper;
	@Autowired
	private BdCustomerMapper bdCustomerMapper;
	public BsCustomerRecordServiceImpl() {
	}

	@Override
	public BsCustomerRecord getBsCustomerRecordById(int id) {
		// TODO Auto-generated method stub
		return bsCustomerRecordMapper.selectByPrimaryKey(id);
	}


	@Override
	public List<BsCustomerRecordQueryItem> getAllBsCustomerRecordByPage(Map map) {
		// TODO Auto-generated method stub
		return bsCustomerRecordMapper.selectAllByPage(map);
	}
	@Override
	public List<Map> monthReport(Map map) {
		// TODO Auto-generated method stub
		return bsCustomerRecordMapper.monthReport(map);
	}
	@Override
	public Map getAllBsCustomerRecordAndBalance(Map map) throws BusinessException{
		// TODO Auto-generated method stub
		Map  data = new HashMap();
 		List<BsCustomerRecordQueryItem> list = bsCustomerRecordMapper.selectAllByPage(map);
		data.put("list",list);
		List<BdCustomerQueryItem> customers = bdCustomerMapper.selectByKey(map.get("customerCode").toString(),map.get("companyId").toString());
		if(customers == null || customers.size()<=0){
			throw new BusinessException("客户["+map.get("customerCode").toString()+"]不存在，数据错误，请联系管理员！");
		}
        // 查询初始余额需要改变条件
        Double balance = 0.0;
		if(map.get("dateFm")!=null&&map.get("dateFm").toString().length()>0){
            String dateFm = map.get("dateFm").toString();
            map.put("dataFm",null);
            map.put("dataTo",dateFm);
            Double duringBalance = bsCustomerRecordMapper.getBalanceByCustomerCode(map);
            balance =duringBalance==null?0.0:duringBalance+customers.get(0).getBalance();
        }else{
            balance =customers.get(0).getBalance();
        }
		data.put("balance",balance);
		return data;
	}
	@Override
	public Message removeBsCustomerRecord(int id) throws BusinessException {
		// TODO Auto-generated method stub
		Message msg = new Message();
		BsCustomerRecord bsCustomerRecord = bsCustomerRecordMapper.selectByPrimaryKey(id);
		if(deleteBeforeCheck(bsCustomerRecord)){
			int[] ids = { id };
			this.delete(ids);
			msg.setMsg("已删除！");
			msg.setCode(200);
		}else {
			msg.setCode(0);
			msg.setMsg("请前往单号["+bsCustomerRecord.getOrderNo()+"]执行取消核算操作，取消后会删除所有相关纪录！");
		}
		return msg;
	}

		@Override
	public BsCustomerRecord saveBsCustomerRecord(BsCustomerRecord model) throws BusinessException {
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		model.setCompanyId(myUserDetails.getCompanyId());
		return (BsCustomerRecord) this.save(model);
	}
	public List<BsCustomerRecord> selectByExample(BsCustomerRecord model){
		return this.bsCustomerRecordMapper.selectByExample(model);
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return bsCustomerRecordMapper;
	}

	private boolean deleteBeforeCheck(BsCustomerRecord bsCustomerRecord) {
		if(!"".equals(bsCustomerRecord.getOrderNo())&&null!=bsCustomerRecord.getOrderNo()){
			return false;
		}else {
			return true;
		}
	}
}

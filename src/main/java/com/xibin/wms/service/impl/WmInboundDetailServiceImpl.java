package com.xibin.wms.service.impl;

import com.xibin.core.daosupport.BaseManagerImpl;
import com.xibin.core.daosupport.BaseMapper;
import com.xibin.core.daosupport.DaoUtil;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.pojo.Message;
import com.xibin.core.security.pojo.MyUserDetails;
import com.xibin.core.security.util.SecurityUtil;
import com.xibin.core.utils.ReadExcelTools;
import com.xibin.wms.constants.WmsCodeMaster;
import com.xibin.wms.dao.WmInboundDetailMapper;
import com.xibin.wms.dao.WmInboundRecieveMapper;
import com.xibin.wms.entity.InboundDetailQuickImportEntity;
import com.xibin.wms.pojo.WmInboundDetail;
import com.xibin.wms.pojo.WmInboundRecieve;
import com.xibin.wms.query.*;
import com.xibin.wms.service.WmInboundDetailService;
import com.xibin.wms.service.WmInboundHeaderService;
import com.xibin.wms.service.WmInboundReceiveService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@Service
public class WmInboundDetailServiceImpl extends BaseManagerImpl implements WmInboundDetailService {
	@Autowired
	private HttpSession session;
	@Autowired
	private WmInboundHeaderService wmInboundHeaderService;
	@Autowired
	private WmInboundReceiveService wmInboundReceiveService;
	@Resource
	private WmInboundDetailMapper wmInboundDetailMapper;
	@Resource
	private WmInboundRecieveMapper wmInboundRecieveMapper;

	@Override
	public WmInboundDetail getInboundDetailById(int id) {
		// TODO Auto-generated method stub
		return wmInboundDetailMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<WmInboundDetail> getInboundDetailByIds(String[] ids) {
		// TODO Auto-generated method stub
		return (List<WmInboundDetail>) this.getById(ids);
	}

	@Override
	public List<WmInboundDetailQueryItem> getAllInboundDetailByPage(Map map) {
		// TODO Auto-generated method stub
		return wmInboundDetailMapper.selectAllByPage(map);
	}

	@Override
	public List<WmInboundDetailQueryItem> selectClosedOrderDetail(Map map) {
		// TODO Auto-generated method stub
		return wmInboundDetailMapper.selectClosedOrderDetail(map);
	}

	@Override
	public List<WmInboundDetailQueryItem> selectByKey(String orderNo, String lineNo) {
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		return wmInboundDetailMapper.selectByKey(orderNo, lineNo, myUserDetails.getCompanyId().toString(),
				myUserDetails.getWarehouseId().toString());
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return wmInboundDetailMapper;
	}

	@Override
	public int removeInboundDetail(int[] ids, String orderNo) throws BusinessException {
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		Message message = operateBeforeCheck(orderNo);
		if (message.getCode() == 0) {
			throw new BusinessException(message.getMsg());
		} else {
			int deleteSum = 0;
			for (int id : ids) {
				WmInboundDetail detail = getInboundDetailById(id);
				WmInboundRecieve receiveQueryExample = new WmInboundRecieve();
				receiveQueryExample.setOrderNo(orderNo);
				receiveQueryExample.setLineNo(detail.getLineNo());
				receiveQueryExample.setCompanyId(myUserDetails.getCompanyId());
				receiveQueryExample.setWarehouseId(myUserDetails.getWarehouseId());
				List<WmInboundRecieve> recieves = wmInboundReceiveService.selectByExample(receiveQueryExample);
				int[] receiveIds = new int[recieves.size()];
				for (int i = 0; i < recieves.size(); i++) {
					receiveIds[i] = recieves.get(i).getId();
				}
				DaoUtil.delete(receiveIds, wmInboundRecieveMapper);
				deleteSum += this.delete(id);
			}
			return deleteSum;
		}
	}

	private Message operateBeforeCheck(String orderNo) {
		Message message = new Message();
		List<WmInboundHeaderQueryItem> headerList = wmInboundHeaderService.selectByKey(orderNo);

		if (headerList.size() > 0) {
			WmInboundHeaderQueryItem header = headerList.get(0);
			// 已审核或不审核
			if (WmsCodeMaster.AUDIT_CLOSE.getCode().equals(header.getAuditStatus())) {
				message.setCode(0);
				message.setMsg("入库单[" + orderNo + "]已审核,不能对明细进行编辑");
				return message;
			} else if (!WmsCodeMaster.INB_NEW.getCode().equals(header.getStatus())) {
				message.setCode(0);
				message.setMsg("入库单[" + orderNo + "]不是创建状态,不能对明细进行编辑");
				return message;
			} else {
				message.setData(header);
				message.setCode(200);
				return message;
			}
		}
		message.setCode(0);
		message.setMsg("入库单[" + orderNo + "]数据丢失,请联系管理员");
		return message;
	}

	@Override
	public WmInboundDetail saveInboundDetailWithOutRecieve(WmInboundDetail model, boolean isCheckHeader)
			throws BusinessException {
		if (isCheckHeader) {
			Message message = operateBeforeCheck(model.getOrderNo());
			if (message.getCode() == 0) {
				throw new BusinessException(message.getMsg());
			}
		} else {
			Message message = new Message();
		}
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		model.setCompanyId(myUserDetails.getCompanyId());
		model.setWarehouseId(myUserDetails.getWarehouseId());
		if (null == model.getId() || 0 == model.getId()) {
			List<Integer> lastLineNo = selectLastLineNo(model.getOrderNo(), model.getCompanyId().toString(),
					model.getWarehouseId().toString());
			Integer num = lastLineNo.get(0);
			if (num == null) {
				model.setLineNo("1");
			} else {
				num++;
				model.setLineNo(num + "");
			}
		}
		WmInboundDetail saveResult = (WmInboundDetail) this.save(model);
		return saveResult;
	}

	@Override
	public WmInboundDetail saveInboundDetail(WmInboundDetail model) throws BusinessException {
		Message message = operateBeforeCheck(model.getOrderNo());
		if (message.getCode() == 0) {
			throw new BusinessException(message.getMsg());
		}
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		model.setCompanyId(myUserDetails.getCompanyId());
		model.setWarehouseId(myUserDetails.getWarehouseId());
		if (null == model.getId() || 0 == model.getId()) {
			List<Integer> lastLineNo = selectLastLineNo(model.getOrderNo(), model.getCompanyId().toString(),
					model.getWarehouseId().toString());
			Integer num = lastLineNo.get(0);
			if (num == null) {
				model.setLineNo("1");
			} else {
				num++;
				model.setLineNo(num + "");
			}
		}
		WmInboundDetail saveResult = (WmInboundDetail) this.save(model);
		if (null == model.getId() || 0 == model.getId()) {
			WmInboundRecieve inboundRecieve = new WmInboundRecieve();
			inboundRecieve.setStatus(WmsCodeMaster.INB_NEW.getCode());
			inboundRecieve.setOrderNo(saveResult.getOrderNo());
			inboundRecieve.setLineNo(saveResult.getLineNo());
			inboundRecieve.setSupplierCode(saveResult.getSupplierCode());
			inboundRecieve.setSkuCode(saveResult.getSkuCode());
			inboundRecieve.setInboundPreNum(saveResult.getInboundPreNum());
			inboundRecieve.setInboundNum(saveResult.getInboundPreNum());
			inboundRecieve.setInboundPrice(saveResult.getInboundPrice());
			inboundRecieve.setCost(saveResult.getCost());
			inboundRecieve.setPlanLoc(saveResult.getPlanLoc());
			inboundRecieve.setInboundLocCode(saveResult.getPlanLoc());
			wmInboundReceiveService.saveInboundRec(inboundRecieve);
		} else {
			List<WmInboundRecieveQueryItem> inboundRecieves = wmInboundReceiveService
					.selectByOrderNoAndLineNo(saveResult.getOrderNo(), saveResult.getLineNo());
			for (int i = 0; i < inboundRecieves.size(); i++) {
				WmInboundRecieve inboundRecieve = new WmInboundRecieve();
				BeanUtils.copyProperties(inboundRecieves.get(i), inboundRecieve);
				inboundRecieve.setStatus(WmsCodeMaster.INB_NEW.getCode());
				inboundRecieve.setSkuCode(saveResult.getSkuCode());
				inboundRecieve.setInboundPreNum(saveResult.getInboundPreNum());
				inboundRecieve.setInboundNum(saveResult.getInboundPreNum());
				inboundRecieve.setInboundPrice(saveResult.getInboundPrice());
				inboundRecieve.setCost(saveResult.getCost());
				inboundRecieve.setPlanLoc(saveResult.getPlanLoc());
				inboundRecieve.setInboundLocCode(saveResult.getPlanLoc());
				wmInboundReceiveService.saveInboundRec(inboundRecieve);
			}
		}
		return saveResult;
	}

	private List<Integer> selectLastLineNo(String orderNo, String companyId, String warehouseId) {
		// TODO Auto-generated method stub
		return wmInboundDetailMapper.selectLastLineNo(orderNo, companyId, warehouseId);
	}

	@Override
	public List<WmInboundDetail> selectByExample(WmInboundDetail model) {
		// TODO Auto-generated method stub
		return wmInboundDetailMapper.selectByExample(model);
	}

	@Override
	public int updateStatusByKey(String orderNo, String lineNos, String status) {
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		return wmInboundDetailMapper.updateStatusByKey(orderNo, lineNos, status, myUserDetails.getCompanyId().toString(),
				myUserDetails.getWarehouseId().toString());
	}

	@Override
	public List<WmInboundDetailSkuQueryItem> queryWmInboundDetailByPage(Map map) {
		// TODO Auto-generated method stub
		return wmInboundDetailMapper.queryWmInboundDetailByPage(map);
	}

	@Override
	public Message importByExcel(MultipartFile file, String orderNo, String skuCodeColumnName, String priceColumnName,
			String numColumnName, String locCodeColumnName) throws BusinessException {
		Message message = new Message();
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		Message checkMsg = operateBeforeCheck(orderNo);
		if (checkMsg.getCode() == 0) {
			return checkMsg;
		}
		WmInboundHeaderQueryItem header = (WmInboundHeaderQueryItem) checkMsg.getData();
		List<Integer> lastLineNo = selectLastLineNo(header.getOrderNo(), header.getCompanyId().toString(),
				header.getWarehouseId().toString());
		Integer num = lastLineNo.get(0);
		if (num == null) {
			num = 1;
		} else {
			num++;
		}
		try {
			Message readMessge = new Message();
			readMessge = ReadExcelTools.readExcelForOutboundDetailImport(file, skuCodeColumnName, priceColumnName,
					numColumnName);
			List<InboundDetailQuickImportEntity> results = (List<InboundDetailQuickImportEntity>) readMessge.getData();
			List<WmInboundDetail> details = new ArrayList<WmInboundDetail>();
			List<WmInboundRecieve> recDetails = new ArrayList<WmInboundRecieve>();
			for (InboundDetailQuickImportEntity entity : results) {
				WmInboundDetail detail = new WmInboundDetail();
				detail.setSupplierCode(header.getSupplierCode());
				detail.setCompanyId(myUserDetails.getCompanyId());
				detail.setCost(0.0);
				detail.setLineNo(num + "");
				detail.setOrderNo(header.getOrderNo());
				detail.setInboundPreNum(entity.getInboundNum());
				detail.setInboundPrice(entity.getInboundPrice());
				detail.setInboundNum(0.0);
				detail.setPlanLoc(entity.getLocCode());
				detail.setSkuCode(entity.getFittingSkuCode());
				detail.setStatus(WmsCodeMaster.INB_NEW.getCode());
				detail.setWarehouseId(myUserDetails.getWarehouseId());
				details.add(detail);
				num++;
				WmInboundRecieve inboundRecieve = new WmInboundRecieve();
				inboundRecieve.setStatus(WmsCodeMaster.INB_NEW.getCode());
				inboundRecieve.setOrderNo(detail.getOrderNo());
				inboundRecieve.setLineNo(detail.getLineNo());
				inboundRecieve.setSupplierCode(detail.getSupplierCode());
				inboundRecieve.setSkuCode(detail.getSkuCode());
				inboundRecieve.setInboundPreNum(detail.getInboundPreNum());
				inboundRecieve.setInboundNum(detail.getInboundPreNum());
				inboundRecieve.setInboundPrice(detail.getInboundPrice());
				inboundRecieve.setCost(detail.getCost());
				inboundRecieve.setPlanLoc(detail.getPlanLoc());
				inboundRecieve.setInboundLocCode(detail.getPlanLoc());
				recDetails.add(inboundRecieve);
			}
			DaoUtil.save(details, wmInboundDetailMapper, session);
			DaoUtil.save(recDetails, wmInboundRecieveMapper, session);
			message.setCode(200);
			message.setMsg(readMessge.getMsg());
			message.setMsg("导入了" + details.size() + "行记录，请与源文件进行核对！");
			return message;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
			return message;

		} catch (BusinessException e) {
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
			return message;
		}
	}

	@Override
	public List<WmInboundDetailSaleHistoryQueryItem> queryHistorySale(Map map) {
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		map.put("companyId", myUserDetails.getCompanyId().toString());
		map.put("warehouseId", myUserDetails.getWarehouseId().toString());
		return wmInboundDetailMapper.queryHistorySale(map);
	}
}

package com.xibin.wms.service.impl;

import com.xibin.core.daosupport.BaseManagerImpl;
import com.xibin.core.daosupport.BaseMapper;
import com.xibin.core.daosupport.DaoUtil;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.pojo.Message;
import com.xibin.core.security.pojo.MyUserDetails;
import com.xibin.core.security.util.SecurityUtil;
import com.xibin.core.utils.ComputeUtil;
import com.xibin.wms.constants.WmsCodeMaster;
import com.xibin.wms.dao.WmAssembleAllocMapper;
import com.xibin.wms.dao.WmAssembleSDetailMapper;
import com.xibin.wms.entity.InventoryUpdateEntity;
import com.xibin.wms.pojo.*;
import com.xibin.wms.query.BdFittingSkuAssembleQueryItem;
import com.xibin.wms.query.BdFittingSkuQueryItem;
import com.xibin.wms.query.WmAssembleFDetailQueryItem;
import com.xibin.wms.query.WmAssembleSDetailQueryItem;
import com.xibin.wms.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.*;

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@Service
public class WmAssembleSDetailServiceImpl extends BaseManagerImpl implements WmAssembleSDetailService {
	@Autowired
	private HttpSession session;
	@Resource
	private WmAssembleHeaderService wmAssembleHeaderService;
	@Resource
	private WmAssembleFDetailService wmAssembleFDetailService;
	@Resource
	private WmAssembleAllocService wmAssembleAllocService;
	@Resource
	private BdFittingSkuService bdFittingSkuService;
	@Resource
	private BdFittingSkuAssembleService bdFittingSkuAssembleService;
	@Resource
	private WmAssembleSDetailMapper wmAssembleSDetailMapper;
	@Resource
	private WmAssembleAllocMapper wmAssembleAllocMapper;
	@Resource
	private WmInventoryService wmInventoryService;
	@Resource
	private WmAssembleUpdateService wmAssembleUpdateService;

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return wmAssembleSDetailMapper;
	}

	@Override
	public WmAssembleSDetail getAssembleOrderById(int id) {
		// TODO Auto-generated method stub
		return wmAssembleSDetailMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<WmAssembleSDetailQueryItem> getAllAssembleSDetailByOrderNo(Map map) {
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		map.put("companyId", myUserDetails.getCompanyId());
		map.put("warehouseId", myUserDetails.getWarehouseId());
		return wmAssembleSDetailMapper.selectAllByOrderNo(map);
	}

	@Override
	public List<WmAssembleSDetail> selectByKey(String orderNo, String lineNo) {
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		return wmAssembleSDetailMapper.selectByKey(orderNo, lineNo, myUserDetails.getCompanyId().toString(),
				myUserDetails.getWarehouseId().toString());
	}

	@Override
	public List<WmAssembleSDetail> selectByExample(WmAssembleSDetail model) {
		// TODO Auto-generated method stub
		return wmAssembleSDetailMapper.selectByExample(model);
	}

	@Override
	public Message createAssembleSDetailByOrderNo(String orderNo) throws BusinessException {
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		Message checkMessage = operateBeforeCreate(orderNo);
		if (checkMessage.getCode() == 0) {
			return checkMessage;
		}
		Map map = new HashMap<>();
		map.put("orderNo", orderNo);
		List<WmAssembleSDetail> sDetails = new ArrayList<WmAssembleSDetail>();
		List<WmAssembleFDetailQueryItem> fDetails = wmAssembleFDetailService.getAllAssembleFDetailByOrderNo(map);
		int slineNo = 1;
		for (WmAssembleFDetailQueryItem fDetail : fDetails) {
			List<BdFittingSkuQueryItem> fSkus = bdFittingSkuService.selectByKey(fDetail.getFittingSkuCode());
			if (fSkus.size() == 0) {
				throw new BusinessException("父件[" + fDetail.getFittingSkuCode() + "]不存在，请修改组装单父件数据，并联系管理员");
			}
			BdFittingSkuQueryItem fSku = fSkus.get(0);
			Map assmebleMap = new HashMap<>();
			assmebleMap.put("fSkuCode", fSku.getFittingSkuCode());
			List<BdFittingSkuAssembleQueryItem> assembleQueryItems = bdFittingSkuAssembleService
					.getAllFittingSkuByFSkuCode(assmebleMap);
			for (BdFittingSkuAssembleQueryItem assembleQueryItem : assembleQueryItems) {
				WmAssembleSDetail assembleSDetail = new WmAssembleSDetail();
				assembleSDetail.setLineNo(slineNo + "");
				assembleSDetail.setOrderNo(fDetail.getOrderNo());
				assembleSDetail.setStatus(WmsCodeMaster.ASS_NEW.getCode());
				assembleSDetail.setfLineNo(fDetail.getLineNo());

				assembleSDetail.setAssembleLoc(fDetail.getAssembleLoc());
				assembleSDetail.setFittingSkuCode(assembleQueryItem.getSSkuCode());
				assembleSDetail.setNum(ComputeUtil.mul(assembleQueryItem.getNum(), fDetail.getPreNum()));
				assembleSDetail.setAllocNum(0.0);
				assembleSDetail.setPickNum(0.0);
				assembleSDetail.setAssembleNum(0.0);
				assembleSDetail.setCompanyId(myUserDetails.getCompanyId());
				assembleSDetail.setWarehouseId(myUserDetails.getWarehouseId());
				sDetails.add(assembleSDetail);
				slineNo++;
			}
		}
		if (sDetails.size() > 0) {
			this.save(sDetails);
		}
		WmAssembleHeader header = (WmAssembleHeader) checkMessage.getData();
		header.setStatus(WmsCodeMaster.ASS_CREATE_S.getCode());
		WmAssembleHeader savedHeader = wmAssembleHeaderService.saveAssembleOrder(header);
		Message returnMessage = new Message();
		returnMessage.setCode(200);
		returnMessage.setData(savedHeader);
		returnMessage.setMsg("操作成功");
		return returnMessage;
	}

	@Override
	public Message cancelCreateByOrderNo(String orderNo) throws BusinessException {
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		Message checkMessage = operateBeforeCancelCreate(orderNo);
		if (checkMessage.getCode() == 0) {
			return checkMessage;
		}
		Map map = new HashMap<>();
		map.put("orderNo", orderNo);
		List<WmAssembleSDetailQueryItem> list = getAllAssembleSDetailByOrderNo(map);
		if (list.size() > 0) {
			int[] idIntegers = new int[list.size()];
			for (int i = 0; i < idIntegers.length; i++) {
				idIntegers[i] = list.get(i).getId();
			}
			this.delete(idIntegers);
		}
		WmAssembleHeader header = (WmAssembleHeader) checkMessage.getData();
		header.setStatus(WmsCodeMaster.ASS_NEW.getCode());
		WmAssembleHeader savedHeader = wmAssembleHeaderService.saveAssembleOrder(header);
		Message returnMessage = new Message();
		returnMessage.setCode(200);
		returnMessage.setData(savedHeader);
		returnMessage.setMsg("操作成功");
		return returnMessage;
	}

	@Override
	public WmAssembleSDetail saveAssembleSDetail(WmAssembleSDetail model) throws BusinessException {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		model.setCompanyId(myUserDetails.getCompanyId());
		model.setWarehouseId(myUserDetails.getWarehouseId());
		this.save(model);
		return selectByKey(model.getOrderNo(), model.getLineNo()).get(0);
	}

	private Message operateBeforeCreate(String orderNo) {
		Message message = new Message();
		List<WmAssembleHeader> headerList = wmAssembleHeaderService.selectByKey(orderNo);

		if (headerList.size() > 0) {
			WmAssembleHeader header = headerList.get(0);
			// 已审核或不审核
			if (WmsCodeMaster.AUDIT_NEW.getCode().equals(header.getAuditStatus())) {
				message.setCode(0);
				message.setMsg("组装单[" + orderNo + "]未审核,不能生成子件明细");
				return message;
			} else if (!WmsCodeMaster.ASS_NEW.getCode().equals(header.getStatus())) {
				message.setCode(0);
				message.setMsg("组装单[" + orderNo + "]不是创建状态,不能生成子件明细");
				return message;
			} else {
				message.setData(header);
				message.setCode(200);
				return message;
			}
		}
		message.setCode(0);
		message.setMsg("组装单[" + orderNo + "]数据丢失,请联系管理员");
		return message;
	}

	private Message operateBeforeCancelCreate(String orderNo) {
		Message message = new Message();
		List<WmAssembleHeader> headerList = wmAssembleHeaderService.selectByKey(orderNo);

		if (headerList.size() > 0) {
			WmAssembleHeader header = headerList.get(0);
			// 已审核或不审核
			if (WmsCodeMaster.AUDIT_NEW.getCode().equals(header.getAuditStatus())) {
				message.setCode(0);
				message.setMsg("组装单[" + orderNo + "]未审核,不能取消生成子件明细");
				return message;
			} else if (!WmsCodeMaster.ASS_CREATE_S.getCode().equals(header.getStatus())) {
				message.setCode(0);
				message.setMsg("组装单[" + orderNo + "]没有生成子件明细,不能取消生成子件明细");
				return message;
			} else {
				message.setData(header);
				message.setCode(200);
				return message;
			}
		}
		message.setCode(0);
		message.setMsg("组装单[" + orderNo + "]数据丢失,请联系管理员");
		return message;
	}

	@Override
	public Message allocByKey(String orderNo, String lineNo) throws BusinessException {
		// TODO Auto-generated method stub
		Message message = new Message();
		List<WmAssembleSDetail> detailList = this.selectByKey(orderNo, lineNo);
		if (detailList.size() > 0) {
			WmAssembleSDetail sDetail = detailList.get(0);
			if (sDetail.getAllocNum().doubleValue() == sDetail.getNum()) {
				throw new BusinessException("组装单号[" + orderNo + "]行号[" + lineNo + "]已经全部分配！");
			}
			MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();

			WmInventory example = new WmInventory();
			example.setSkuCode(sDetail.getFittingSkuCode());
			example.setCompanyId(myUserDetails.getCompanyId());
			example.setWarehouseId(myUserDetails.getWarehouseId());
			List<WmInventory> inventoryRecords = wmInventoryService.getAvailableInvByExample(example);
			// 对库存记录进行排序，按照库存可用数升序排序
			Collections.sort(inventoryRecords, new Comparator<WmInventory>() {
				@Override
				public int compare(WmInventory o1, WmInventory o2) {
					return o1.getInvAvailableNum().compareTo(o2.getInvAvailableNum());
				}
			});
			// 获得订货数
			double outboundNumForCalculate = sDetail.getNum();
			for (WmInventory inventory : inventoryRecords) {
				WmAssembleAlloc alloc = new WmAssembleAlloc();

				alloc.setOrderNo(sDetail.getOrderNo());
				alloc.setSLineNo(sDetail.getLineNo());
				alloc.setFittingSkuCode(inventory.getSkuCode());
				alloc.setToLoc(sDetail.getAssembleLoc());
				alloc.setAllocLoc(inventory.getLocCode());
				alloc.setStatus(WmsCodeMaster.ASS_FULL_ALLOC.getCode());
				InventoryUpdateEntity entity = new InventoryUpdateEntity();
				entity.setActionCode(WmsCodeMaster.ACT_ALLOC.getCode());
				entity.setLineNo(sDetail.getLineNo());
				entity.setLocCode(inventory.getLocCode());
				entity.setOrderNo(orderNo);
				entity.setOrderType(WmsCodeMaster.ORDER_ASS.getCode());
				entity.setSkuCode(inventory.getSkuCode());
				// 库存数量足够
				if (inventory.getInvAvailableNum().doubleValue() >= outboundNumForCalculate) {
					alloc.setAllocNum(outboundNumForCalculate);

					entity.setQtyOp(outboundNumForCalculate);
					entity.setQtyOpBefore(inventory.getInvAvailableNum());
					entity.setQtyOpAfter(ComputeUtil.sub(inventory.getInvAvailableNum(), outboundNumForCalculate));
					// 更新库存
					WmActTran actTran = wmInventoryService.updateInventory(entity);
					// 回填计算出的成本数据
					alloc.setCost(actTran.getCost());
					wmAssembleAllocService.saveAssembleAlloc(alloc);
					outboundNumForCalculate = 0.0;
					break;
				} else {
					alloc.setAllocNum(inventory.getInvAvailableNum());

					entity.setQtyOp(inventory.getInvAvailableNum());
					entity.setQtyOpBefore(inventory.getInvAvailableNum());
					entity.setQtyOpAfter(0.0);
					// 更新库存
					WmActTran actTran = wmInventoryService.updateInventory(entity);
					// 回填计算出的成本数据
					alloc.setCost(actTran.getCost());
					wmAssembleAllocService.saveAssembleAlloc(alloc);
					outboundNumForCalculate = ComputeUtil.sub(outboundNumForCalculate,
							inventory.getInvAvailableNum().doubleValue());
				}
			}
			sDetail.setAllocNum(ComputeUtil.sub(sDetail.getNum().doubleValue(), outboundNumForCalculate));

			// 订货数已经全部分配
			if (outboundNumForCalculate == 0.0) {
				// 未进行过拣货则可更新为 部分/完全 分配
				if (sDetail.getPickNum().doubleValue() == 0.0) {
					sDetail.setStatus(WmsCodeMaster.ASS_FULL_ALLOC.getCode());
				}
				DaoUtil.save(sDetail, wmAssembleSDetailMapper, session);

				// //根据出库单明细更新出库单的状态
				// wmAssembleUpdateService.updataAssembleStatusByAssembleSDetail(orderNo);
				message.setCode(200);
				message.setMsg("操作成功！");
			} else if (outboundNumForCalculate > 0.0 && outboundNumForCalculate < sDetail.getNum().doubleValue()) {
				// 未进行过拣货则可更新为 部分/完全 分配
				if (sDetail.getPickNum().doubleValue() == 0.0) {
					sDetail.setStatus(WmsCodeMaster.ASS_PART_ALLOC.getCode());
				}
				DaoUtil.save(sDetail, wmAssembleSDetailMapper, session);
				// //根据出库单明细更新出库单的状态
				// wmAssembleUpdateService.updataAssembleStatusByAssembleSDetail(orderNo);
				// 订货数还有剩余
				String surplusOutboundNum = (ComputeUtil.sub(sDetail.getNum().doubleValue(), outboundNumForCalculate))
						+ "";
				message.setCode(100);
				message.setMsg("组装单号[" + orderNo + "]行号[" + lineNo + "]的子件明细已分配" + surplusOutboundNum + "剩余"
						+ outboundNumForCalculate + "");
			} else {
				// 一件都没分配
				throw new BusinessException("组装单号[" + orderNo + "]行号[" + lineNo + "]的子件明细在仓库中没有库存!");
			}
		} else {
			throw new BusinessException("组装单号[" + orderNo + "]行号[" + lineNo + "]的子件明细已不存在");
		}
		return message;
	}

	@Override
	public Message cancelAllocByKey(String orderNo, String lineNo) throws BusinessException {
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		Message message = new Message();
		List<WmAssembleSDetail> detailList = this.selectByKey(orderNo, lineNo);
		if (detailList.size() > 0) {
			WmAssembleSDetail detail = detailList.get(0);
			if (WmsCodeMaster.ASS_FULL_ALLOC.getCode().equals(detail.getStatus())
					|| WmsCodeMaster.ASS_PART_ALLOC.getCode().equals(detail.getStatus())) {
				WmAssembleAlloc allocQueryExample = new WmAssembleAlloc();
				allocQueryExample.setOrderNo(orderNo);
				allocQueryExample.setSLineNo(lineNo);
				allocQueryExample.setCompanyId(myUserDetails.getCompanyId());
				allocQueryExample.setWarehouseId(myUserDetails.getWarehouseId());
				List<WmAssembleAlloc> allocList = wmAssembleAllocService.selectByExample(allocQueryExample);
				double allocNumSum = 0.0;
				int[] allocIds = new int[allocList.size()];
				for (int i = 0; i < allocList.size(); i++) {
					allocNumSum += allocList.get(i).getAllocNum();
					allocIds[i] = allocList.get(i).getId();
					// 更新库存
					InventoryUpdateEntity entity = new InventoryUpdateEntity();
					entity.setActionCode(WmsCodeMaster.ACT_CANCEL_ALLOC.getCode());
					entity.setLineNo(detail.getLineNo());
					entity.setLocCode(allocList.get(i).getAllocLoc());
					entity.setOrderNo(orderNo);
					entity.setOrderType(WmsCodeMaster.ORDER_ASS.getCode());
					entity.setSkuCode(allocList.get(i).getFittingSkuCode());
					entity.setQtyOp(allocList.get(i).getAllocNum());
					entity.setCost(allocList.get(i).getCost());
					// 更新库存
					wmInventoryService.updateInventory(entity);
				}
				DaoUtil.delete(allocIds, wmAssembleAllocMapper);
				detail.setAllocNum(0.0);
				detail.setStatus(WmsCodeMaster.ASS_NEW.getCode());
				DaoUtil.save(detail, wmAssembleSDetailMapper, session);
				message.setMsg("操作成功！");
				message.setCode(200);
				return message;
			} else {
				throw new BusinessException("组装单号[" + orderNo + "]子件行号[" + lineNo + "]的子件明细不是完全分配或者部分分配状态");
			}
		} else {
			throw new BusinessException("组装单号[" + orderNo + "]子件行号[" + lineNo + "]的子件明细已不存在");
		}

	}
}

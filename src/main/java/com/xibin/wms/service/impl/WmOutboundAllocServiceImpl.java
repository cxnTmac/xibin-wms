package com.xibin.wms.service.impl;

import com.xibin.core.daosupport.BaseManagerImpl;
import com.xibin.core.daosupport.BaseMapper;
import com.xibin.core.daosupport.DaoUtil;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.pojo.Message;
import com.xibin.core.security.pojo.MyUserDetails;
import com.xibin.core.security.util.SecurityUtil;
import com.xibin.core.utils.CodeGenerator;
import com.xibin.core.utils.ComputeUtil;
import com.xibin.wms.constants.BsCodeMaster;
import com.xibin.wms.constants.WmsCodeMaster;
import com.xibin.wms.dao.WmOutboundAllocMapper;
import com.xibin.wms.dao.WmOutboundDetailMapper;
import com.xibin.wms.dao.WmOutboundHeaderMapper;
import com.xibin.wms.entity.InventoryUpdateEntity;
import com.xibin.wms.pojo.*;
import com.xibin.wms.query.WmOutboundAllocQueryItem;
import com.xibin.wms.query.WmOutboundDetailQueryItem;
import com.xibin.wms.query.WmOutboundDetailSumPriceQueryItem;
import com.xibin.wms.query.WmOutboundHeaderQueryItem;
import com.xibin.wms.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.*;

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@Service
public class WmOutboundAllocServiceImpl extends BaseManagerImpl implements WmOutboundAllocService {
	@Autowired
	private HttpSession session;
	@Resource
	private WmOutboundHeaderMapper wmOutboundHeaderMapper;
	@Resource
	private WmOutboundAllocMapper wmOutboundAllocMapper;
	@Resource
	private WmOutboundDetailMapper wmOutboundDetailMapper;
	@Autowired
	private WmOutboundDetailService wmOutboundDetailService;
	@Autowired
	private WmInventoryService wmInventoryService;
	@Autowired
	private WmOutboundHeaderService wmOutboundHeaderService;

	@Autowired
	private WmOutboundUpdateService wmOutboundUpdateService;
	@Autowired
	private BdFittingSkuService bdFittingSkuService;
	@Autowired
	private  BsCustomerRecordService bsCustomerRecordService;
	@Override
	public WmOutboundAlloc getOutboundAllocById(int id) {
		// TODO Auto-generated method stub

		return wmOutboundAllocMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<WmOutboundAllocQueryItem> getAllOutboundAllocByPage(Map map) {
		// TODO Auto-generated method stub
		return wmOutboundAllocMapper.selectAllByPage(map);
	}
	@Override
	public List<WmOutboundAllocQueryItem> selectForMobileAlloc(Map map) {
		// TODO Auto-generated method stub
		return wmOutboundAllocMapper.selectForMobileAlloc(map);
	}
	@Override
	public List<Map> selectForReAlloc(Map map) {
		// TODO Auto-generated method stub
		return wmOutboundAllocMapper.selectForReAlloc(map);
	}
	@Override
	public List<WmOutboundAllocQueryItem> selectByKey(String orderNo, String lineNo) {
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		return wmOutboundAllocMapper.selectByKey(orderNo, lineNo, myUserDetails.getCompanyId().toString(),
				myUserDetails.getWarehouseId().toString());
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return wmOutboundAllocMapper;
	}

	@Override
	public WmOutboundAlloc saveOutboundAlloc(WmOutboundAlloc model) {
		// TODO Auto-generated method stub
		// 只用于更新 成本
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		model.setCompanyId(myUserDetails.getCompanyId());
		model.setWarehouseId(myUserDetails.getWarehouseId());
		Integer num = 0;
		if (null == model.getId() || 0 == model.getId()) {
			model.setAllocId(CodeGenerator.getCodeByCurrentTimeAndRandomNum("ACT"));
		}
		return (WmOutboundAlloc) this.save(model);
	}

	private Message newOperateBeforeCheck(String orderNo) {
		Message message = new Message();
		List<WmOutboundHeaderQueryItem> headerList = wmOutboundHeaderService.selectByKey(orderNo);
		if (headerList.size() > 0) {
			WmOutboundHeaderQueryItem header = headerList.get(0);
			// 关闭定单不能编辑明细
			if (WmsCodeMaster.INB_CLOSE.getCode().equals(header.getStatus())) {
				message.setCode(0);
				message.setMsg("出库单[" + orderNo + "]不是创建状态,不能对明细进行编辑");
				return message;
			} else {
				message.setCode(200);
				return message;
			}
		}
		message.setCode(0);
		message.setMsg("出库单[" + orderNo + "]数据丢失,请联系管理员");
		return message;
	}

	@Override
	public WmOutboundAlloc saveOutboundAllocForEditCost(WmOutboundAlloc model) throws BusinessException {
		// TODO Auto-generated method stub
		// 只用于更新 成本
		Message message = newOperateBeforeCheck(model.getOrderNo());
		if (message.getCode() == 0) {
			throw new BusinessException(message.getMsg());
		}
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		model.setCompanyId(myUserDetails.getCompanyId());
		model.setWarehouseId(myUserDetails.getWarehouseId());
		this.save(model);
		// 分配明细成本被修改后，更新出库明细的成本数据
		updateOutboundDetailForEditCost(model);
		return this.getOutboundAllocById(model.getId());
	}

	private void updateOutboundDetailForEditCost(WmOutboundAlloc model) throws BusinessException {
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		WmOutboundDetail queryExample = new WmOutboundDetail();
		queryExample.setCompanyId(myUserDetails.getCompanyId());
		queryExample.setWarehouseId(myUserDetails.getWarehouseId());
		queryExample.setOrderNo(model.getOrderNo());
		queryExample.setLineNo(model.getLineNo());
		List<WmOutboundDetail> queryResults = wmOutboundDetailService.selectByExample(queryExample);
		if (queryResults.size() > 0) {
			WmOutboundDetail detail = queryResults.get(0);
			WmOutboundAlloc allocQueryExample = new WmOutboundAlloc();
			allocQueryExample.setCompanyId(myUserDetails.getCompanyId());
			allocQueryExample.setWarehouseId(myUserDetails.getWarehouseId());
			allocQueryExample.setOrderNo(model.getOrderNo());
			allocQueryExample.setLineNo(model.getLineNo());
			List<WmOutboundAlloc> allocQueryResults = this.selectByExample(allocQueryExample);
			double sumCost = 0.0;
			for (WmOutboundAlloc alloc : allocQueryResults) {
				sumCost = ComputeUtil.add(sumCost, ComputeUtil.mul(alloc.getCost(), alloc.getOutboundNum()));
			}
			double cost = ComputeUtil.div(sumCost, detail.getOutboundAllocNum(), 2);
			detail.setCost(cost);
			DaoUtil.save(detail, wmOutboundDetailMapper, session);
		} else {
			throw new BusinessException("订单号[" + model.getOrderNo() + "]行号[" + model.getLineNo() + "]的出库明细不存在，数据有误！");
		}
	}

	private int[] idListToArray(List<Integer> ids) {
		int[] idArray = new int[ids.size()];
		for (int i = 0; i < ids.size(); i++) {
			idArray[i] = ids.get(i);
		}
		return idArray;
	}

	@Override
	public List<WmOutboundAlloc> selectByExample(WmOutboundAlloc model) {
		// TODO Auto-generated method stub
		return wmOutboundAllocMapper.selectByExample(model);
	}
	private void saveSaleRecord(WmOutboundHeader wmOutboundHeader,Integer auxiId,Double totalPrice){
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		BsCustomerRecord bsCustomerRecordSale = new BsCustomerRecord();
		bsCustomerRecordSale.setCompanyId(myUserDetails.getCompanyId());
		bsCustomerRecordSale.setOrderNo(wmOutboundHeader.getOrderNo());
		bsCustomerRecordSale.setCustomerCode(wmOutboundHeader.getBuyerCode());
		bsCustomerRecordSale.setAuxiId(auxiId);
		if (wmOutboundHeader.getOutboundType().equals(WmsCodeMaster.OUB_XX.getCode())) {
			//现销
			bsCustomerRecordSale.setType(BsCodeMaster.TYPE_X_SALE.getCode());
			bsCustomerRecordSale.setAbstractt("销售出库");
		}
		if (wmOutboundHeader.getOutboundType().equals(WmsCodeMaster.OUB_PO.getCode())) {
			//奢销
			bsCustomerRecordSale.setType(BsCodeMaster.TYPE_S_SALE.getCode());
			bsCustomerRecordSale.setAbstractt("销售出库");
		}
		if (wmOutboundHeader.getOutboundType().equals(WmsCodeMaster.OUB_RO.getCode())) {
			//退货
			bsCustomerRecordSale.setType(BsCodeMaster.TYPE_RETURN_OUTBOUND.getCode());
			bsCustomerRecordSale.setAbstractt("退货出库");
		}
		if (wmOutboundHeader.getOutboundType().equals(WmsCodeMaster.OUB_CO.getCode())) {
			//盘亏
			bsCustomerRecordSale.setType(BsCodeMaster.TYPE_LOSS.getCode());
			bsCustomerRecordSale.setAbstractt("盘亏出库");
		}
		List<BsCustomerRecord> queryResults = bsCustomerRecordService.selectByExample(bsCustomerRecordSale);
		if(queryResults.size()>0){
			bsCustomerRecordSale = queryResults.get(0);
		}
		bsCustomerRecordSale.setPay(totalPrice);
		bsCustomerRecordSale.setDate(wmOutboundHeader.getOrderTime());
		bsCustomerRecordService.save(bsCustomerRecordSale);
	}
	@Override
	public Message shipByHeader(String orderNo) throws BusinessException {
		Message message = new Message();
		List<String> errors = new ArrayList<String>();
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		WmOutboundHeader headerQueryExample = new WmOutboundHeader();
		headerQueryExample.setOrderNo(orderNo);
		headerQueryExample.setCompanyId(myUserDetails.getCompanyId());
		headerQueryExample.setWarehouseId(myUserDetails.getWarehouseId());
		List<WmOutboundHeader> headers = wmOutboundHeaderService.selectByExample(headerQueryExample);
		if (headers.size() > 0) {
			WmOutboundHeader header = headers.get(0);
			if (header.getStatus().equals(WmsCodeMaster.SO_NEW.getCode())
					|| header.getStatus().equals(WmsCodeMaster.SO_PART_ALLOC.getCode())
					|| header.getStatus().equals(WmsCodeMaster.SO_FULL_ALLOC.getCode())) {
				throw new BusinessException("出库单[" + orderNo + "]没有拣货，不能发货");
			}
			WmOutboundAlloc allocQueryExample = new WmOutboundAlloc();
			allocQueryExample.setOrderNo(orderNo);
			allocQueryExample.setStatus(WmsCodeMaster.SO_FULL_PICKING.getCode());
			allocQueryExample.setCompanyId(myUserDetails.getCompanyId());
			allocQueryExample.setWarehouseId(myUserDetails.getWarehouseId());
			List<WmOutboundAlloc> allocs = selectByExample(allocQueryExample);
			//double totalPrice = 0.0;
			//销售金额纪录
			for (WmOutboundAlloc alloc : allocs) {
				try {
					//totalPrice=ComputeUtil.add(totalPrice,ComputeUtil.mul(alloc.getOutboundNum(),alloc.getOutboundPrice()));
					shipByAlloc(alloc, false);
					// 更新产品最后出库时间
					if (WmsCodeMaster.OUB_PO.getCode().equals(header.getOutboundType())||WmsCodeMaster.OUB_XX.getCode().equals(header.getOutboundType())) {
						BdFittingSku skuQueryExample = new BdFittingSku();
						skuQueryExample.setCompanyId(alloc.getCompanyId());
						skuQueryExample.setFittingSkuCode(alloc.getSkuCode());
						List<BdFittingSku> skus = bdFittingSkuService.selectByExample(skuQueryExample);
						if(skus.size()>0){
							BdFittingSku sku =  skus.get(0);
							sku.setLastInboundTime(new Date());
							bdFittingSkuService.saveFittingSku(sku);
						}
					}
				} catch (BusinessException e) {
					// TODO: handle exception
					errors.add(e.getMessage());
				}
			}
			// 增加 销售明细账
			Map queryMap = new HashMap();
			queryMap.put("orderNo", header.getOrderNo());
			queryMap.put("companyId", myUserDetails.getCompanyId());
			queryMap.put("warehouseId", myUserDetails.getWarehouseId());
			List<WmOutboundDetailSumPriceQueryItem> totalPriceForOrder = wmOutboundDetailMapper.querySumPriceGroupByOrderNoForAccount(queryMap);
			this.saveSaleRecord(header,totalPriceForOrder.get(0).getAuxiId(),totalPriceForOrder.get(0).getTotal());
			header.setTotalPrice(totalPriceForOrder.get(0).getTotal());
			DaoUtil.save(header,this.wmOutboundHeaderMapper,session);
			wmOutboundUpdateService.updateOutboundStatusByOrderNo(orderNo);
			if (errors.size() > 0) {
				message.setCode(100);
				message.setMsgs(errors);
				message.converMsgsToMsg("");
			} else {
				message.setCode(200);
				message.setMsg("操作成功");
			}
			return message;
		} else {
			throw new BusinessException("数据有误，出库单[" + orderNo + "]不存在");
		}
	}
	private void delateSaleRecord(WmOutboundHeader header){
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		BsCustomerRecord queryModel = new BsCustomerRecord();
		queryModel.setCompanyId(myUserDetails.getCompanyId());
		queryModel.setOrderNo(header.getOrderNo());
		if(WmsCodeMaster.OUB_PO.getCode().equals(header.getOutboundType())){
			// 赊销
			queryModel.setType(BsCodeMaster.TYPE_S_SALE.getCode());
		}else if(WmsCodeMaster.OUB_XX.getCode().equals(header.getOutboundType())){
			// 现销
			queryModel.setType(BsCodeMaster.TYPE_X_SALE.getCode());
		}else if(WmsCodeMaster.OUB_RO.getCode().equals(header.getOutboundType())){
			// 退货
			queryModel.setType(BsCodeMaster.TYPE_RETURN_OUTBOUND.getCode());
		}else if(WmsCodeMaster.OUB_CO.getCode().equals(header.getOutboundType())){
			// 盘盈出库和盘亏出库保留原样
			queryModel.setType(BsCodeMaster.TYPE_LOSS.getCode());
		}
		List<BsCustomerRecord> bsCustomerRecords = bsCustomerRecordService.selectByExample(queryModel);
		int[] ids = new int[bsCustomerRecords.size()];
		int index = 0;
		for (BsCustomerRecord record : bsCustomerRecords) {
			ids[index] = record.getId();
			index++;
		}
		if(ids.length>0){
			bsCustomerRecordService.delete(ids);
		}
	}
	@Override
	public Message cancelShipByHeader(String orderNo) throws BusinessException {
		Message message = new Message();
		List<String> errors = new ArrayList<String>();
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		WmOutboundHeader headerQueryExample = new WmOutboundHeader();
		headerQueryExample.setOrderNo(orderNo);
		headerQueryExample.setCompanyId(myUserDetails.getCompanyId());
		headerQueryExample.setWarehouseId(myUserDetails.getWarehouseId());
		List<WmOutboundHeader> headers = wmOutboundHeaderService.selectByExample(headerQueryExample);
		if (headers.size() > 0) {
			WmOutboundHeader header = headers.get(0);
			if (header.getStatus().equals(WmsCodeMaster.SO_PART_SHIPPING.getCode())
					|| header.getStatus().equals(WmsCodeMaster.SO_FULL_SHIPPING.getCode())) {
				WmOutboundAlloc allocQueryExample = new WmOutboundAlloc();
				allocQueryExample.setOrderNo(orderNo);
				allocQueryExample.setStatus(WmsCodeMaster.SO_FULL_SHIPPING.getCode());
				allocQueryExample.setCompanyId(myUserDetails.getCompanyId());
				allocQueryExample.setWarehouseId(myUserDetails.getWarehouseId());
				List<WmOutboundAlloc> allocs = selectByExample(allocQueryExample);
				for (WmOutboundAlloc alloc : allocs) {
					try {
						cancelShipByAlloc(alloc, false);
					} catch (BusinessException e) {
						// TODO: handle exception
						errors.add(e.getMessage());
					}
				}
				// 删除销售（出库）明细账
				this.delateSaleRecord(header);
				header.setTotalPrice(0.0);
				DaoUtil.save(header,this.wmOutboundHeaderMapper,session);
				wmOutboundUpdateService.updateOutboundStatusByOrderNo(orderNo);
				if (errors.size() > 0) {
					message.setCode(100);
					message.setMsgs(errors);
					message.converMsgsToMsg("");
				} else {
					message.setCode(200);
					message.setMsg("操作成功");
				}
				return message;

			} else {
				throw new BusinessException("出库单[" + orderNo + "]没有发货，不能取消发货");
			}
		} else {
			throw new BusinessException("数据有误，出库单[" + orderNo + "]不存在");
		}
	}

	@Override
	public void shipByAlloc(WmOutboundAlloc alloc, boolean isUpdateOrder) throws BusinessException {
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		if (!alloc.getStatus().equals(WmsCodeMaster.SO_FULL_PICKING.getCode())
				&& !alloc.getStatus().equals(WmsCodeMaster.SO_OVER_PICKING.getCode())) {
			throw new BusinessException(
					"出库单[" + alloc.getOrderNo() + "]分配明细[" + alloc.getAllocId() + "]不是完全拣货/超量状态，不能发货！");
		}
		String targetSkuCode = null;
		if(null!=alloc.getGenericSkuCode()&&!"".equals(alloc.getGenericSkuCode())){
			targetSkuCode = alloc.getGenericSkuCode();
		}else{
			targetSkuCode = alloc.getSkuCode();
		}
		InventoryUpdateEntity entityFm = new InventoryUpdateEntity();
		entityFm.setActionCode(WmsCodeMaster.ACT_SHIP.getCode());
		entityFm.setLineNo(alloc.getLineNo());
		entityFm.setLocCode(alloc.getToLocCode());
		entityFm.setOrderNo(alloc.getOrderNo());
		entityFm.setOrderType(WmsCodeMaster.ORDER_OUB.getCode());
		entityFm.setSkuCode(targetSkuCode);
		entityFm.setQtyOp(alloc.getOutboundNum());
		entityFm.setPrice(alloc.getOutboundPrice());
		entityFm.setCost(alloc.getCost());
		// 更新库存
		wmInventoryService.updateInventory(entityFm);
		// 更新分配明细状态
		alloc.setStatus(WmsCodeMaster.SO_FULL_SHIPPING.getCode());
		alloc.setShipOp(myUserDetails.getUserId());
		alloc.setShipTime(new Date());
		WmOutboundAlloc currentRecord = this.saveOutboundAlloc(alloc);
		if (isUpdateOrder) {
			wmOutboundUpdateService.updateOutboundStatusByAlloc(currentRecord);
		}
	}

	@Override
	public void cancelShipByAlloc(WmOutboundAlloc alloc, boolean isUpdateOrder) throws BusinessException {
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		if (!alloc.getStatus().equals(WmsCodeMaster.SO_FULL_SHIPPING.getCode())) {
			throw new BusinessException(
					"出库单[" + alloc.getOrderNo() + "]分配明细[" + alloc.getAllocId() + "]不是完全发货状态，不能取消发货！");
		}
		String targetSkuCode = null;
		if(null!=alloc.getGenericSkuCode()&&!"".equals(alloc.getGenericSkuCode())){
			targetSkuCode = alloc.getGenericSkuCode();
		}else{
			targetSkuCode = alloc.getSkuCode();
		}
		InventoryUpdateEntity entityFm = new InventoryUpdateEntity();
		entityFm.setActionCode(WmsCodeMaster.ACT_CANCEL_SHIP.getCode());
		entityFm.setLineNo(alloc.getLineNo());
		entityFm.setLocCode(alloc.getToLocCode());
		entityFm.setOrderNo(alloc.getOrderNo());
		entityFm.setOrderType(WmsCodeMaster.ORDER_OUB.getCode());
		entityFm.setSkuCode(targetSkuCode);
		entityFm.setQtyOp(alloc.getOutboundNum());
		entityFm.setPrice(alloc.getOutboundPrice());
		entityFm.setCost(alloc.getCost());
		// 更新库存
		wmInventoryService.updateInventory(entityFm);
		// 更新分配明细状态
		alloc.setStatus(WmsCodeMaster.SO_FULL_PICKING.getCode());
		alloc.setShipOp(myUserDetails.getUserId());
		alloc.setShipTime(new Date());
		WmOutboundAlloc currentRecord = this.saveOutboundAlloc(alloc);
		if (isUpdateOrder) {
			wmOutboundUpdateService.updateOutboundStatusByAlloc(currentRecord);
		}
	}

	@Override
	public Message pickByOrderNo(String orderNo) throws BusinessException {
		Message message = new Message();
		List<String> errors = new ArrayList<String>();
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		WmOutboundHeader headerQueryExample = new WmOutboundHeader();
		headerQueryExample.setOrderNo(orderNo);
		headerQueryExample.setCompanyId(myUserDetails.getCompanyId());
		headerQueryExample.setWarehouseId(myUserDetails.getWarehouseId());
		List<WmOutboundHeader> headers = wmOutboundHeaderService.selectByExample(headerQueryExample);
		if (headers.size() > 0) {
			WmOutboundHeader header = headers.get(0);
			if (header.getStatus().equals(WmsCodeMaster.SO_NEW.getCode())
					|| header.getStatus().equals(WmsCodeMaster.SO_FULL_PICKING.getCode())
					|| header.getStatus().equals(WmsCodeMaster.SO_FULL_SHIPPING.getCode())) {
				throw new BusinessException("数据有误，出库单[" + orderNo + "]为创建/完全拣货/完全发货状态,不能拣货");
			} else {
				WmOutboundAlloc queryExample = new WmOutboundAlloc();
				queryExample.setOrderNo(orderNo);
				queryExample.setCompanyId(myUserDetails.getCompanyId());
				queryExample.setStatus(WmsCodeMaster.SO_FULL_ALLOC.getCode());
				List<WmOutboundAlloc> queryResults = this.selectByExample(queryExample);
				for (WmOutboundAlloc alloc : queryResults) {
					try {
						// 整单拣货默认是整件包装拣货,整单拣货无法精确统计件数，所以件数均设置为0
						this.pickByAlloc(alloc, alloc.getPickNum(),true,0);
					} catch (BusinessException e) {
						// TODO: handle exception
						errors.add(e.getMessage());
					}
				}
				// wmOutboundUpdateService.updateOutboundStatusByOrderNo(orderNo);
				if (errors.size() == 0) {
					message.setCode(200);
					message.setMsg("操作成功！");
				} else {
					message.setMsgs(errors);
					message.converMsgsToMsg("</br>");
					message.setCode(10);
				}
			}
		} else {
			throw new BusinessException("数据有误，出库单[" + orderNo + "]不存在");
		}

		return message;
	}
	@Override
	public void packByAlloc(WmOutboundAlloc alloc) throws BusinessException {
		if (!alloc.getStatus().equals(WmsCodeMaster.SO_FULL_PICKING.getCode())) {
			throw new BusinessException(
					"出库单[" + alloc.getOrderNo() + "]分配明细[" + alloc.getAllocId() + "]不是完全拣货状态，不能打包！");
		}
		this.saveOutboundAlloc(alloc);
	}
	@Override
	public void cancelPackByAlloc(WmOutboundAlloc alloc) throws BusinessException {
		if (!alloc.getStatus().equals(WmsCodeMaster.SO_FULL_PICKING.getCode())) {
			throw new BusinessException(
					"出库单[" + alloc.getOrderNo() + "]分配明细[" + alloc.getAllocId() + "]不是完全拣货状态，不能取消打包！");
		}
		alloc.setPackageNo(-1);
		this.saveOutboundAlloc(alloc);
	}
	@Override
	public void pickByAlloc(WmOutboundAlloc alloc, double pickNum,boolean isPackedSku,int packageNum) throws BusinessException {
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		// 判断 超量或者缺量 拣货
		double overNum = ComputeUtil.sub(pickNum, alloc.getOutboundNum().doubleValue());
		if (!alloc.getStatus().equals(WmsCodeMaster.SO_FULL_ALLOC.getCode())) {
			throw new BusinessException(
					"出库单[" + alloc.getOrderNo() + "]分配明细[" + alloc.getAllocId() + "]不是完全分配状态，不能拣货！");
		}
		String targetSkuCode = null;
		if(null!=alloc.getGenericSkuCode()&&!"".equals(alloc.getGenericSkuCode())){
			targetSkuCode = alloc.getGenericSkuCode();
		}else{
			targetSkuCode = alloc.getSkuCode();
		}
		InventoryUpdateEntity entityFm = new InventoryUpdateEntity();
		// 根据分配类别，分别设置不同的动作，普通拣货和预组装分配拣货
		if (WmsCodeMaster.ALLOC_AUTO.getCode().equals(alloc.getAllocType())
				||WmsCodeMaster.ALLOC_VIRTUAL.getCode().equals(alloc.getAllocType())
				||WmsCodeMaster.ALLOC_GENERIC.getCode().equals(alloc.getAllocType())){
			entityFm.setActionCode(WmsCodeMaster.ACT_PICK.getCode());
		} else if (WmsCodeMaster.ALLOC_ASS.getCode().equals(alloc.getAllocType())) {
			entityFm.setActionCode(WmsCodeMaster.ACT_PRE_ASSEMBLE_PICK.getCode());
		}
		entityFm.setLineNo(alloc.getLineNo());
		entityFm.setLocCode(alloc.getAllocLocCode());
		entityFm.setOrderNo(alloc.getOrderNo());
		entityFm.setOrderType(WmsCodeMaster.ORDER_OUB.getCode());
		entityFm.setSkuCode(targetSkuCode);
		entityFm.setQtyOp(pickNum);
		entityFm.setCost(alloc.getCost());
		entityFm.setPrice(alloc.getOutboundPrice());
		InventoryUpdateEntity entityTo = new InventoryUpdateEntity();
		// 根据分配类别，分别设置不同的动作，普通拣货和预组装分配拣货
		if (WmsCodeMaster.ALLOC_AUTO.getCode().equals(alloc.getAllocType())) {
			entityTo.setActionCode(WmsCodeMaster.ACT_PICK.getCode());
		} else if (WmsCodeMaster.ALLOC_ASS.getCode().equals(alloc.getAllocType())) {
			entityTo.setActionCode(WmsCodeMaster.ACT_PRE_ASSEMBLE_PICK.getCode());
		}
		entityTo.setLineNo(alloc.getLineNo());
		entityTo.setLocCode(alloc.getToLocCode());
		entityTo.setOrderNo(alloc.getOrderNo());
		entityTo.setOrderType(WmsCodeMaster.ORDER_OUB.getCode());
		entityTo.setSkuCode(targetSkuCode);
		entityTo.setQtyOp(pickNum);
		entityTo.setCost(alloc.getCost());
		entityTo.setPrice(alloc.getOutboundPrice());
		// 更新库存
		wmInventoryService.updateInventory(entityFm, entityTo);
		// 更新分配明细状态
		alloc.setStatus(WmsCodeMaster.SO_FULL_PICKING.getCode());
		//重新设置一下分配数，因为缺量拣货会需要拆分出一条新的分配明细，原分配明细的分配数要减少，只为实际缺量拣货的数量
		alloc.setOutboundNum(pickNum);
		alloc.setPickNum(pickNum);
		alloc.setPickOp(myUserDetails.getUserId());
		if(isPackedSku){
			alloc.setPackageNo(0);
			// 整件拣货需要统计件数
			alloc.setPackageNum(packageNum);
		}else{
			alloc.setPackageNo(-1);
		}
		this.saveOutboundAlloc(alloc);
		// 更新明细的拣货数
		WmOutboundDetail queryExample = new WmOutboundDetail();
		queryExample.setOrderNo(alloc.getOrderNo());
		queryExample.setLineNo(alloc.getLineNo());
		queryExample.setCompanyId(myUserDetails.getCompanyId());
		queryExample.setWarehouseId(myUserDetails.getWarehouseId());
		List<WmOutboundDetail> wmOutboundDetails = wmOutboundDetailService.selectByExample(queryExample);
		if (wmOutboundDetails.size() == 0) {
			throw new BusinessException(
					"出库单[" + alloc.getOrderNo() + "]分配明细[" + alloc.getAllocId() + "]没有对应的出库明细，数据错误！");
		}
		// 如果是缺量拣货，则拆分分配明细
		if (overNum < 0) {
			WmOutboundAlloc wmOutboundAlloc = new WmOutboundAlloc();
			wmOutboundAlloc.setAllocType(alloc.getAllocType());
			wmOutboundAlloc.setAllocLocCode(alloc.getAllocLocCode());
			wmOutboundAlloc.setBuyerCode(alloc.getBuyerCode());
			wmOutboundAlloc.setLineNo(wmOutboundDetails.get(0).getLineNo());
			wmOutboundAlloc.setOrderNo(alloc.getOrderNo());
			wmOutboundAlloc.setOutboundNum(-overNum);
			// 初始化打包信息
			wmOutboundAlloc.setPackageNo(-2);
			wmOutboundAlloc.setPackageNum(0);
			// 生成的分配明细还未拣货
			wmOutboundAlloc.setPickNum(0.0);
			wmOutboundAlloc.setOutboundPrice(alloc.getOutboundPrice());
			wmOutboundAlloc.setSkuCode(alloc.getSkuCode());
			wmOutboundAlloc.setGenericSkuCode(alloc.getGenericSkuCode());
			wmOutboundAlloc.setToLocCode(alloc.getToLocCode());
			wmOutboundAlloc.setWarehouseId(myUserDetails.getWarehouseId());
			wmOutboundAlloc.setCompanyId(myUserDetails.getCompanyId());
//			InventoryUpdateEntity entityFmForOverPick = new InventoryUpdateEntity();
//			// 生成的分配明细尚未拣货，只执行到自动分配步骤
//			entityFmForOverPick.setActionCode(WmsCodeMaster.ACT_ALLOC.getCode());
//			entityFmForOverPick.setLineNo(wmOutboundAlloc.getLineNo());
//			entityFmForOverPick.setLocCode(wmOutboundAlloc.getAllocLocCode());
//			entityFmForOverPick.setOrderNo(wmOutboundAlloc.getOrderNo());
//			entityFmForOverPick.setOrderType(WmsCodeMaster.ORDER_OUB.getCode());
//			entityFmForOverPick.setSkuCode(wmOutboundAlloc.getSkuCode());
//			entityFmForOverPick.setQtyOp(-overNum);
//			entityFmForOverPick.setPrice(wmOutboundAlloc.getOutboundPrice());
//			InventoryUpdateEntity entityToForOverPick = new InventoryUpdateEntity();
//			entityToForOverPick.setActionCode(WmsCodeMaster.ACT_ALLOC.getCode());
//			entityToForOverPick.setLineNo(wmOutboundAlloc.getLineNo());
//			entityToForOverPick.setLocCode(wmOutboundAlloc.getToLocCode());
//			entityToForOverPick.setOrderNo(wmOutboundAlloc.getOrderNo());
//			entityToForOverPick.setOrderType(WmsCodeMaster.ORDER_OUB.getCode());
//			entityToForOverPick.setSkuCode(wmOutboundAlloc.getSkuCode());
//			entityToForOverPick.setQtyOp(-overNum);
//			entityToForOverPick.setPrice(wmOutboundAlloc.getOutboundPrice());
//			// 更新库存
//			WmActTran actTran = wmInventoryService.updateInventory(entityFmForOverPick, entityToForOverPick);
			wmOutboundAlloc.setCost(alloc.getCost());
			wmOutboundAlloc.setStatus(WmsCodeMaster.SO_FULL_ALLOC.getCode());
			this.saveOutboundAlloc(wmOutboundAlloc);
		}
		// 更新出库明细
		WmOutboundDetail wmOutboundDetail = wmOutboundDetails.get(0);
		wmOutboundDetail.setOutboundPickNum(ComputeUtil.add(wmOutboundDetail.getOutboundPickNum(), alloc.getPickNum()));
		wmOutboundDetailService.saveOutboundDetailWithOutCheck(wmOutboundDetail);
		wmOutboundUpdateService.updateOutboundStatusByOrderNo(alloc.getOrderNo());
	}

	@Override
	public void cancelPickByAlloc(WmOutboundAlloc alloc) throws BusinessException {
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		if (!alloc.getStatus().equals(WmsCodeMaster.SO_FULL_PICKING.getCode())
				&& !alloc.getStatus().equals(WmsCodeMaster.SO_OVER_PICKING.getCode())) {
			throw new BusinessException(
					"出库单[" + alloc.getOrderNo() + "]分配明细[" + alloc.getAllocId() + "]不是完全拣货状态，不能取消拣货！");
		}
		String targetSkuCode = null;
		if(null!=alloc.getGenericSkuCode()&&!"".equals(alloc.getGenericSkuCode())){
			targetSkuCode = alloc.getGenericSkuCode();
		}else{
			targetSkuCode = alloc.getSkuCode();
		}
		if (alloc.getStatus().equals(WmsCodeMaster.SO_FULL_PICKING.getCode())) {
			InventoryUpdateEntity entityFm = new InventoryUpdateEntity();
			// 根据分配类别，分别设置不同的动作，普通拣货和预组装分配拣货
			if (WmsCodeMaster.ALLOC_AUTO.getCode().equals(alloc.getAllocType())||WmsCodeMaster.ALLOC_VIRTUAL.getCode().equals(alloc.getAllocType())
					||WmsCodeMaster.ALLOC_GENERIC.getCode().equals(alloc.getAllocType())) {
				entityFm.setActionCode(WmsCodeMaster.ACT_CANCEL_PICK.getCode());
			} else if (WmsCodeMaster.ALLOC_ASS.getCode().equals(alloc.getAllocType())) {
				entityFm.setActionCode(WmsCodeMaster.ACT_CANCEL_PRE_ASSEMBLE_PICK.getCode());
			}

			entityFm.setLineNo(alloc.getLineNo());
			entityFm.setLocCode(alloc.getToLocCode());
			entityFm.setOrderNo(alloc.getOrderNo());
			entityFm.setOrderType(WmsCodeMaster.ORDER_OUB.getCode());
			entityFm.setSkuCode(targetSkuCode);
			entityFm.setQtyOp(alloc.getOutboundNum());
			entityFm.setCost(alloc.getCost());
			entityFm.setPrice(alloc.getOutboundPrice());
			InventoryUpdateEntity entityTo = new InventoryUpdateEntity();
			// 根据分配类别，分别设置不同的动作，普通拣货和预组装分配拣货
			if (WmsCodeMaster.ALLOC_AUTO.getCode().equals(alloc.getAllocType())
					||WmsCodeMaster.ALLOC_VIRTUAL.getCode().equals(alloc.getAllocType())
					||WmsCodeMaster.ALLOC_GENERIC.getCode().equals(alloc.getAllocType())) {
				entityTo.setActionCode(WmsCodeMaster.ACT_CANCEL_PICK.getCode());
			} else if (WmsCodeMaster.ALLOC_ASS.getCode().equals(alloc.getAllocType())) {
				entityTo.setActionCode(WmsCodeMaster.ACT_CANCEL_PRE_ASSEMBLE_PICK.getCode());
			}
			entityTo.setLineNo(alloc.getLineNo());
			entityTo.setLocCode(alloc.getAllocLocCode());
			entityTo.setOrderNo(alloc.getOrderNo());
			entityTo.setOrderType(WmsCodeMaster.ORDER_OUB.getCode());
			entityTo.setSkuCode(targetSkuCode);
			entityTo.setQtyOp(alloc.getOutboundNum());
			entityTo.setCost(alloc.getCost());
			entityTo.setPrice(alloc.getOutboundPrice());
			// 更新库存
			wmInventoryService.updateInventory(entityFm, entityTo);
			// 更新分配明细状态
			alloc.setStatus(WmsCodeMaster.SO_FULL_ALLOC.getCode());
			alloc.setPickOp(null);
			alloc.setPickTime(null);
			alloc.setPackageNo(-2);
			alloc.setPackageNum(0);
			this.saveOutboundAlloc(alloc);
			// 更新明细的拣货数
			WmOutboundDetail queryExample = new WmOutboundDetail();
			queryExample.setOrderNo(alloc.getOrderNo());
			queryExample.setLineNo(alloc.getLineNo());
			queryExample.setCompanyId(myUserDetails.getCompanyId());
			queryExample.setWarehouseId(myUserDetails.getWarehouseId());
			List<WmOutboundDetail> wmOutboundDetails = wmOutboundDetailService.selectByExample(queryExample);
			if (wmOutboundDetails.size() > 0) {
				WmOutboundDetail wmOutboundDetail = wmOutboundDetails.get(0);
				wmOutboundDetail.setOutboundPickNum(
						ComputeUtil.sub(wmOutboundDetail.getOutboundPickNum(), alloc.getOutboundNum()));
				wmOutboundDetailService.saveOutboundDetailWithOutCheck(wmOutboundDetail);
			}
		} else if (alloc.getStatus().equals(WmsCodeMaster.SO_OVER_PICKING.getCode())) {
			InventoryUpdateEntity entityFm = new InventoryUpdateEntity();
			entityFm.setActionCode(WmsCodeMaster.ACT_CANCEL_OVER_PICK.getCode());
			entityFm.setLineNo(alloc.getLineNo());
			entityFm.setLocCode(alloc.getToLocCode());
			entityFm.setOrderNo(alloc.getOrderNo());
			entityFm.setOrderType(WmsCodeMaster.ORDER_OUB.getCode());
			entityFm.setSkuCode(targetSkuCode);
			entityFm.setQtyOp(alloc.getOutboundNum());
			entityFm.setCost(alloc.getCost());
			entityFm.setPrice(alloc.getOutboundPrice());
			InventoryUpdateEntity entityTo = new InventoryUpdateEntity();
			entityTo.setActionCode(WmsCodeMaster.ACT_CANCEL_OVER_PICK.getCode());
			entityTo.setLineNo(alloc.getLineNo());
			entityTo.setLocCode(alloc.getAllocLocCode());
			entityTo.setOrderNo(alloc.getOrderNo());
			entityTo.setOrderType(WmsCodeMaster.ORDER_OUB.getCode());
			entityTo.setSkuCode(targetSkuCode);
			entityTo.setQtyOp(alloc.getOutboundNum());
			entityTo.setCost(alloc.getCost());
			entityTo.setPrice(alloc.getOutboundPrice());
			// 更新库存
			wmInventoryService.updateInventory(entityFm, entityTo);
			WmOutboundDetail queryExample = new WmOutboundDetail();
			queryExample.setOrderNo(alloc.getOrderNo());
			queryExample.setLineNo(alloc.getLineNo());
			List<WmOutboundDetail> queryResults = wmOutboundDetailService.selectByExample(queryExample);
			if (queryResults.size() > 0) {
				// DaoUtil.delete(queryResults.get(0).getId(),
				// wmOutboundDetailMapper);
				// 更新出库明细
				WmOutboundDetail wmOutboundDetail = queryResults.get(0);
				wmOutboundDetail
						.setOutboundPickNum(ComputeUtil.sub(wmOutboundDetail.getOutboundPickNum(), alloc.getPickNum()));
				wmOutboundDetail.setOutboundNum(ComputeUtil.sub(wmOutboundDetail.getOutboundNum(), alloc.getPickNum()));
				wmOutboundDetailService.saveOutboundDetailWithOutCheck(wmOutboundDetail);
			}
			DaoUtil.delete(alloc.getId(), wmOutboundAllocMapper);
		}

		wmOutboundUpdateService.updateOutboundStatusByOrderNo(alloc.getOrderNo());
	}
	@Override
	public Double getTotalPackageNumByOrderNo(String orderNo){
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		Map map = new HashMap();
		map.put("orderNo",orderNo);
		map.put("companyId",myUserDetails.getCompanyId());
		map.put("warehouseId",myUserDetails.getWarehouseId());
		Double sum1 = this.wmOutboundAllocMapper.queryFCLSumByOrderNo(map);
		Double sum2 = this.wmOutboundAllocMapper.queryLCLSumByOrderNo(map);
		if(sum1== null){
			sum1 = 0.0;
		}
		if(sum2== null){
			sum2=0.0;
		}
		return sum1+sum2;
	}
	// 重新分配 虚拟分配的分配记录，抵消虚拟库存的负数
	@Override
	public void reAllocByAllocId(int allocId) throws BusinessException {
		this.reAllocByAlloc(this.getOutboundAllocById(allocId));
	}
	// 重新分配 虚拟分配的分配记录，抵消虚拟库存的负数
	@Override
	public void reAllocByAlloc(WmOutboundAlloc alloc) throws BusinessException {
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		if (!alloc.getStatus().equals(WmsCodeMaster.SO_FULL_SHIPPING.getCode())) {
			throw new BusinessException(
					"出库单[" + alloc.getOrderNo() + "]分配明细[" + alloc.getAllocId() + "]不是完全发货状态，不能重新分配！");
		}
		if (!WmsCodeMaster.ALLOC_VIRTUAL.getCode().equals(alloc.getAllocType())){
			throw new BusinessException(
					"出库单[" + alloc.getOrderNo() + "]分配明细[" + alloc.getAllocId() + "]不是虚拟分配，不能重新分配！");
		}
		List<WmOutboundDetailQueryItem> details = this.wmOutboundDetailService.selectByKey(alloc.getOrderNo(),alloc.getLineNo());
		WmOutboundDetailQueryItem detail = details.get(0);
		WmInventory example = new WmInventory();
		example.setSkuCode(alloc.getSkuCode());
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
		// 计算已经分配的成本，用于后续累计总成本
		double totalCost = ComputeUtil.mul(detail.getCost() != null ? detail.getCost() : 0.0,
				detail.getOutboundAllocNum());
		// 获得需要分配的数量
		double outboundNumForCalculate = alloc.getOutboundNum();
		if(outboundNumForCalculate>inventoryRecords.stream().mapToDouble(WmInventory::getInvAvailableNum).sum()){
			throw new BusinessException(
					"出库单[" + alloc.getOrderNo() + "]分配明细[" + alloc.getAllocId() + "]所需要的库存不足，不能重新分配！");
		}else {
			for (WmInventory inventory : inventoryRecords) {
				WmOutboundAlloc newAlloc = new WmOutboundAlloc();
//				BeanUtils.copyProperties(alloc,newAlloc);
				newAlloc.setBuyerCode(alloc.getBuyerCode());
				newAlloc.setOrderNo(alloc.getOrderNo());
				newAlloc.setLineNo(alloc.getLineNo());
				// 设置分配类型
				newAlloc.setAllocType(WmsCodeMaster.ALLOC_AUTO.getCode());
				newAlloc.setSkuCode(inventory.getSkuCode());
				newAlloc.setOutboundPrice(alloc.getOutboundPrice());
				newAlloc.setToLocCode(alloc.getToLocCode());
				newAlloc.setAllocLocCode(inventory.getLocCode());
				newAlloc.setPackageNo(alloc.getPackageNo());
				newAlloc.setPackageNum(alloc.getPackageNum());
				newAlloc.setPickOp(alloc.getPickOp());
				newAlloc.setPickTime(alloc.getPickTime());
				newAlloc.setShipTime(alloc.getShipTime());
				newAlloc.setShipOp(alloc.getShipOp());
				// 执行重新分配的分配明细通常都是 完全发货状态
				newAlloc.setStatus(alloc.getStatus());
				// 1.重新分配库存
				InventoryUpdateEntity entity = new InventoryUpdateEntity();
				entity.setActionCode(WmsCodeMaster.ACT_RE_ALLOC.getCode());
				entity.setLineNo(alloc.getLineNo());
				entity.setLocCode(inventory.getLocCode());
				entity.setOrderNo(alloc.getOrderNo());
				entity.setOrderType(WmsCodeMaster.ORDER_OUB.getCode());
				entity.setSkuCode(inventory.getSkuCode());
				entity.setPrice(alloc.getOutboundPrice());
				InventoryUpdateEntity entityTo = new InventoryUpdateEntity();
				//  2.抵消虚拟库存的负数
				entityTo.setActionCode(WmsCodeMaster.ACT_RE_ALLOC.getCode());
				entityTo.setLineNo(alloc.getLineNo());
				entityTo.setLocCode(alloc.getAllocLocCode());
				entityTo.setOrderNo(alloc.getOrderNo());
				entityTo.setOrderType(WmsCodeMaster.ORDER_OUB.getCode());
				entityTo.setSkuCode(alloc.getSkuCode());
				entityTo.setPrice(alloc.getOutboundPrice());
				// 库存数量足够
				if (inventory.getInvAvailableNum().doubleValue() >= outboundNumForCalculate) {
					newAlloc.setOutboundNum(outboundNumForCalculate);
					newAlloc.setPickNum(outboundNumForCalculate);
					entity.setQtyOp(outboundNumForCalculate);
					entity.setQtyOpBefore(inventory.getInvAvailableNum());
					entity.setQtyOpAfter(ComputeUtil.sub(inventory.getInvAvailableNum(), outboundNumForCalculate));
					entityTo.setQtyOp(outboundNumForCalculate);
					// 更新库存
					WmActTran actTran = wmInventoryService.updateInventory(entity,entityTo);
					// 回填计算出的成本数据
					newAlloc.setCost(actTran.getCost());
					totalCost = ComputeUtil.add(totalCost, actTran.getCost() * outboundNumForCalculate);
					this.saveOutboundAlloc(newAlloc);
					outboundNumForCalculate = 0.0;
					break;
				} else {
					newAlloc.setOutboundNum(inventory.getInvAvailableNum());
					newAlloc.setPickNum(inventory.getInvAvailableNum());
					entity.setQtyOp(inventory.getInvAvailableNum());
					entity.setQtyOpBefore(inventory.getInvAvailableNum());
					entity.setQtyOpAfter(0.0);
					entityTo.setQtyOp(inventory.getInvAvailableNum());
					// 更新库存
					WmActTran actTran = wmInventoryService.updateInventory(entity,entityTo);
					// 回填计算出的成本数据
					newAlloc.setCost(actTran.getCost());
					totalCost = ComputeUtil.add(totalCost, actTran.getCost() * entity.getQtyOp());
					this.saveOutboundAlloc(newAlloc);
					outboundNumForCalculate = ComputeUtil.sub(outboundNumForCalculate,
							inventory.getInvAvailableNum().doubleValue());
				}
			}
			WmOutboundDetail targetDetail = new WmOutboundDetail();
			BeanUtils.copyProperties(detail, targetDetail);
//			targetDetail.setOutboundAllocNum(
			// 明细只有 成本会发生变化
			targetDetail.setCost(ComputeUtil.div(totalCost, targetDetail.getOutboundAllocNum(), 2));
//			targetDetail.setStatus(WmsCodeMaster.SO_FULL_ALLOC.getCode());
			DaoUtil.save(targetDetail, wmOutboundDetailMapper, session);
			// 删掉原来的分配明细
			this.delete(alloc.getId());
		}
	}
}

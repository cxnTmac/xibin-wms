package com.xibin.wms.service.impl;

import com.xibin.core.costants.Constants;
import com.xibin.core.daosupport.BaseManagerImpl;
import com.xibin.core.daosupport.BaseMapper;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.security.pojo.MyUserDetails;
import com.xibin.core.security.util.SecurityUtil;
import com.xibin.core.utils.CodeGenerator;
import com.xibin.wms.constants.BsCodeMaster;
import com.xibin.wms.constants.WmsCodeMaster;
import com.xibin.wms.dao.WmOutboundAllocMapper;
import com.xibin.wms.dao.WmOutboundDetailMapper;
import com.xibin.wms.dao.WmOutboundHeaderMapper;
import com.xibin.wms.pojo.BsCustomerRecord;
import com.xibin.wms.pojo.WmOutboundAlloc;
import com.xibin.wms.pojo.WmOutboundHeader;
import com.xibin.wms.query.WmOutboundDetailSumPriceQueryItem;
import com.xibin.wms.query.WmOutboundHeaderQueryItem;
import com.xibin.wms.service.BsCustomerRecordService;
import com.xibin.wms.service.WmOutboundHeaderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@Service
public class WmOutboundHeaderServiceImpl extends BaseManagerImpl implements WmOutboundHeaderService {
    @Autowired
    private HttpSession session;
    @Resource
    private WmOutboundHeaderMapper wmOutboundHeaderMapper;
    @Resource
    private WmOutboundDetailMapper wmOutboundDetailMapper;
    @Resource
    private WmOutboundAllocMapper wmOutboundAllocMapper;
    @Resource
    private BsCustomerRecordService bsCustomerRecordService;

    @Override
    public WmOutboundHeader getOutboundOrderById(int id) {
        // TODO Auto-generated method stub
        return wmOutboundHeaderMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<WmOutboundHeaderQueryItem> getAllOutboundOrderByPage(Map map) {
        // TODO Auto-generated method stub
        int maxInactiveInterval = (int) session.getMaxInactiveInterval();
        return wmOutboundHeaderMapper.selectAllByPage(map);
    }
    @Override
    public List<Map> queryForOutboundDaily(Map map) {
        // TODO Auto-generated method stub
        return wmOutboundHeaderMapper.queryForOutboundDaily(map);
    }

    @Override
    public List<WmOutboundHeaderQueryItem> selectByKey(String orderNo) {
        // TODO Auto-generated method stub
        MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
        return wmOutboundHeaderMapper.selectByKey(orderNo, myUserDetails.getCompanyId().toString(),
                myUserDetails.getWarehouseId().toString());
    }

    @Override
    protected BaseMapper getMapper() {
        // TODO Auto-generated method stub
        return wmOutboundHeaderMapper;
    }

    @Override
    public WmOutboundHeaderQueryItem saveOutbound(WmOutboundHeader model) throws BusinessException {
        // TODO Auto-generated method stub
        MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
        model.setCompanyId(myUserDetails.getCompanyId());
        model.setWarehouseId(myUserDetails.getWarehouseId());
        if (null == model.getId() || model.getId() == 0) {
            model.setOrderNo(CodeGenerator.getCodeByCurrentTimeAndRandomNum("OUB"));
        }
        this.save(model);
        List<WmOutboundHeaderQueryItem> list = selectByKey(model.getOrderNo());
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public int remove(String orderNo) throws BusinessException {
        // TODO Auto-generated method stub
        MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
        List<WmOutboundHeaderQueryItem> queryResult = this.selectByKey(orderNo);
        if (queryResult.size() > 0) {
            WmOutboundHeader wmOutboundHeader = new WmOutboundHeader();
            BeanUtils.copyProperties(queryResult.get(0), wmOutboundHeader);
            if (WmsCodeMaster.AUDIT_CLOSE.getCode().equals(wmOutboundHeader.getAuditStatus())) {
                throw new BusinessException("出库单[" + wmOutboundHeader.getOrderNo() + "]已审核");
            } else if (!WmsCodeMaster.INB_NEW.getCode().equals(wmOutboundHeader.getStatus())) {
                throw new BusinessException("出库单[" + wmOutboundHeader.getOrderNo() + "]不是创建状态");
            }
            // 删除明细
            wmOutboundDetailMapper.deleteByOrderNo(orderNo, myUserDetails.getCompanyId().toString(), myUserDetails.getWarehouseId().toString());
            return this.delete(wmOutboundHeader.getId());
        } else {
            throw new BusinessException("入库单[" + orderNo + "]不存在");
        }
    }

    @Override
    public List<WmOutboundHeader> selectByExample(WmOutboundHeader model) {
        // TODO Auto-generated method stub
        return wmOutboundHeaderMapper.selectByExample(model);
    }

    @Override
    public WmOutboundHeaderQueryItem audit(String orderNo) throws BusinessException {
        // TODO Auto-generated method stub
        MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
        List<WmOutboundHeaderQueryItem> queryResult = this.selectByKey(orderNo);
        if (queryResult.size() > 0) {
            WmOutboundHeader wmOutboundHeader = new WmOutboundHeader();
            BeanUtils.copyProperties(queryResult.get(0), wmOutboundHeader);
            if (WmsCodeMaster.AUDIT_CLOSE.getCode().equals(wmOutboundHeader.getAuditStatus())) {
                throw new BusinessException("出库单[" + wmOutboundHeader.getOrderNo() + "]已审核");
            } else if (!WmsCodeMaster.INB_NEW.getCode().equals(wmOutboundHeader.getStatus())) {
                throw new BusinessException("出库单[" + wmOutboundHeader.getOrderNo() + "]不是创建状态");
            }
            wmOutboundHeader.setAuditStatus(WmsCodeMaster.AUDIT_CLOSE.getCode());
            wmOutboundHeader.setAuditOp(myUserDetails.getUserId());
            wmOutboundHeader.setAuditTime(new Date());
            return this.saveOutbound(wmOutboundHeader);
        } else {
            throw new BusinessException("出库单[" + orderNo + "]不存在");
        }
    }

    @Override
    public WmOutboundHeaderQueryItem cancelAudit(String orderNo) throws BusinessException {
        // TODO Auto-generated method stub
        List<WmOutboundHeaderQueryItem> queryResult = this.selectByKey(orderNo);
        if (queryResult.size() > 0) {
            WmOutboundHeader wmOutboundHeader = new WmOutboundHeader();
            BeanUtils.copyProperties(queryResult.get(0), wmOutboundHeader);
            if (WmsCodeMaster.AUDIT_NEW.getCode().equals(wmOutboundHeader.getAuditStatus())) {
                throw new BusinessException("出库单[" + wmOutboundHeader.getOrderNo() + "]未审核");
            } else if (!WmsCodeMaster.INB_NEW.getCode().equals(wmOutboundHeader.getStatus())) {
                throw new BusinessException("出库单[" + wmOutboundHeader.getOrderNo() + "]不是创建状态");
            }
            wmOutboundHeader.setAuditStatus(WmsCodeMaster.AUDIT_NEW.getCode());
            wmOutboundHeader.setAuditOp(null);
            wmOutboundHeader.setAuditTime(null);
            return this.saveOutbound(wmOutboundHeader);
        } else {
            throw new BusinessException("出库单[" + orderNo + "]不存在");
        }
    }

    @Override
    public WmOutboundHeaderQueryItem close(String orderNo) throws BusinessException {
        // TODO Auto-generated method stub
        MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
        List<WmOutboundHeaderQueryItem> queryResult = this.selectByKey(orderNo);
        if (queryResult.size() > 0) {
            WmOutboundHeader wmOutboundHeader = new WmOutboundHeader();
            BeanUtils.copyProperties(queryResult.get(0), wmOutboundHeader);
            // 查询是否有虚拟分配明细
            WmOutboundAlloc allocQueryExample = new WmOutboundAlloc();
            allocQueryExample.setAllocType(WmsCodeMaster.ALLOC_VIRTUAL.getCode());
            allocQueryExample.setOrderNo(orderNo);
            allocQueryExample.setCompanyId(myUserDetails.getCompanyId());
            allocQueryExample.setWarehouseId(myUserDetails.getWarehouseId());
            List<WmOutboundAlloc> virtualAllocList = wmOutboundAllocMapper.selectByExample(allocQueryExample);
            if(virtualAllocList.size()>0){
                throw new BusinessException("出库单[" + wmOutboundHeader.getOrderNo() + "]存在虚拟分配纪录，不能关闭订单！");
            }
            if (WmsCodeMaster.OUB_CO.getCode().equals(wmOutboundHeader.getOutboundType())) {
                if (!WmsCodeMaster.SO_FULL_SHIPPING.getCode().equals(wmOutboundHeader.getStatus())) {
                    throw new BusinessException("出库单[" + wmOutboundHeader.getOrderNo() + "]不是完全发货状态");
                }
            } else {
                if ("N".equals(wmOutboundHeader.getIsCalculated())) {
                    throw new BusinessException("出库单[" + wmOutboundHeader.getOrderNo() + "]还未销售核算");
                }
            }
            wmOutboundHeader.setStatus(WmsCodeMaster.SO_CLOSE.getCode());
            return this.saveOutbound(wmOutboundHeader);
        } else {
            throw new BusinessException("出库单[" + orderNo + "]不存在");
        }
    }

    @Override
    public WmOutboundHeaderQueryItem newAccount(String orderNo) throws BusinessException {
        MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
        List<WmOutboundHeaderQueryItem> queryResult = this.selectByKey(orderNo);
        if (queryResult.size() > 0) {
            WmOutboundHeader wmOutboundHeader = new WmOutboundHeader();
            String buyerName = queryResult.get(0).getBuyerName();
            BeanUtils.copyProperties(queryResult.get(0), wmOutboundHeader);

            if (!WmsCodeMaster.SO_FULL_SHIPPING.getCode().equals(wmOutboundHeader.getStatus())) {
                throw new BusinessException("出库单[" + wmOutboundHeader.getOrderNo() + "]不是完全发货状态");
            }
            if ("Y".equals(wmOutboundHeader.getIsCalculated())) {
                throw new BusinessException("出库单[" + wmOutboundHeader.getOrderNo() + "]已完成销售核算！");
            }
            //销售金额纪录
            Map queryMap = new HashMap();
            queryMap.put("orderNo", wmOutboundHeader.getOrderNo());
            queryMap.put("companyId", myUserDetails.getCompanyId());
            queryMap.put("warehouseId", myUserDetails.getWarehouseId());
            List<WmOutboundDetailSumPriceQueryItem> totalPriceForOrder = wmOutboundDetailMapper.querySumPriceGroupByOrderNoForAccount(queryMap);
            if (totalPriceForOrder.size() == 0) {
                throw new BusinessException("出库单[" + wmOutboundHeader.getOrderNo() + "]数据有误！");
            }
            BsCustomerRecord bsCustomerRecordSale = new BsCustomerRecord();
            bsCustomerRecordSale.setCompanyId(myUserDetails.getCompanyId());
            bsCustomerRecordSale.setAbstractt("销售配件");
            bsCustomerRecordSale.setDate(wmOutboundHeader.getOrderTime());
            bsCustomerRecordSale.setOrderNo(wmOutboundHeader.getOrderNo());
            bsCustomerRecordSale.setCustomerCode(wmOutboundHeader.getBuyerCode());
            if (wmOutboundHeader.getOutboundType().equals(WmsCodeMaster.OUB_XX.getCode())) {
                //现销
                bsCustomerRecordSale.setType(BsCodeMaster.TYPE_X_SALE.getCode());
            }
            if (wmOutboundHeader.getOutboundType().equals(WmsCodeMaster.OUB_PO.getCode())) {
                //奢销
                bsCustomerRecordSale.setType(BsCodeMaster.TYPE_S_SALE.getCode());
            }
            if (wmOutboundHeader.getOutboundType().equals(WmsCodeMaster.OUB_RO.getCode())) {
                //退货
                bsCustomerRecordSale.setType(BsCodeMaster.TYPE_RETURN_OUTBOUND.getCode());
            }
            if (wmOutboundHeader.getOutboundType().equals(WmsCodeMaster.OUB_CO.getCode())) {
                //盘亏
                bsCustomerRecordSale.setType(BsCodeMaster.TYPE_LOSS.getCode());
            }
            bsCustomerRecordSale.setPay(totalPriceForOrder.get(0).getTotal());
            bsCustomerRecordSale.setAuxiId(totalPriceForOrder.get(0).getAuxiId());
            bsCustomerRecordService.save(bsCustomerRecordSale);
            if (wmOutboundHeader.getFreight() > 0.0) {
                //运费纪录
                BsCustomerRecord bsCustomerRecordFreight = new BsCustomerRecord();
                bsCustomerRecordFreight.setCompanyId(myUserDetails.getCompanyId());

                bsCustomerRecordFreight.setDate(wmOutboundHeader.getOrderTime());
                bsCustomerRecordFreight.setOrderNo(wmOutboundHeader.getOrderNo());

                if(WmsCodeMaster.FREIGHT_PAID.getCode().equals(wmOutboundHeader.getFreightType())){
                    // 我方已付运费
                    bsCustomerRecordFreight.setType(BsCodeMaster.TYPE_FREIGHT_PAID.getCode());
                    //  客户设置位我方虚拟客户
                    bsCustomerRecordFreight.setCustomerCode(Constants.XIBIN_USER_CODE);
                    bsCustomerRecordFreight.setAbstractt("为["+buyerName+"]支付运费");
                }else{
                    bsCustomerRecordFreight.setAbstractt("支付运费");
                    bsCustomerRecordFreight.setType(BsCodeMaster.TYPE_FREIGHT_COLLECT.getCode());
                    bsCustomerRecordFreight.setCustomerCode(wmOutboundHeader.getBuyerCode());
                }
                bsCustomerRecordFreight.setPay(wmOutboundHeader.getFreight());
                bsCustomerRecordFreight.setAuxiId(totalPriceForOrder.get(0).getAuxiId());
                bsCustomerRecordService.save(bsCustomerRecordFreight);
            }
            wmOutboundHeader.setIsCalculated("Y");
            return this.saveOutbound(wmOutboundHeader);
        } else {
            throw new BusinessException("出库单[" + orderNo + "]不存在");
        }
    }
    @Override
    public WmOutboundHeaderQueryItem account(String orderNo) throws BusinessException {
        MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
        List<WmOutboundHeaderQueryItem> queryResult = this.selectByKey(orderNo);
        if (queryResult.size() > 0) {
            WmOutboundHeaderQueryItem wmOutboundHeader = queryResult.get(0);
            String buyerName = wmOutboundHeader.getBuyerName();
            WmOutboundHeader wmOutboundHeaderModel = new WmOutboundHeader();
            BeanUtils.copyProperties(queryResult.get(0), wmOutboundHeaderModel);
            if (!WmsCodeMaster.SO_FULL_SHIPPING.getCode().equals(wmOutboundHeader.getStatus())) {
                throw new BusinessException("出库单[" + wmOutboundHeader.getOrderNo() + "]不是完全发货状态");
            }
            if ("Y".equals(wmOutboundHeader.getIsCalculated())) {
                throw new BusinessException("出库单[" + wmOutboundHeader.getOrderNo() + "]已完成销售核算！");
            }
            //销售金额纪录
            Map queryMap = new HashMap();
            queryMap.put("orderNo", wmOutboundHeader.getOrderNo());
            queryMap.put("companyId", myUserDetails.getCompanyId());
            queryMap.put("warehouseId", myUserDetails.getWarehouseId());
            List<WmOutboundDetailSumPriceQueryItem> totalPriceForOrder = wmOutboundDetailMapper.querySumPriceGroupByOrderNoForAccount(queryMap);
            if (totalPriceForOrder.size() == 0) {
                throw new BusinessException("出库单[" + wmOutboundHeader.getOrderNo() + "]数据有误！");
            }
            // 收款明细账增加
            if (wmOutboundHeader.getOutboundType().equals(WmsCodeMaster.OUB_XX.getCode())||wmOutboundHeader.getOutboundType().equals(WmsCodeMaster.OUB_PO.getCode())) {
                // 已打款
                if (WmsCodeMaster.IS_RECEIVE_CASH_Y.getCode().equals(wmOutboundHeader.getIsRecievedCash())) {
                    // 计算差价
                    double discount = wmOutboundHeader.getPriceDifferent()==null?0.0:wmOutboundHeader.getPriceDifferent();
                    if (discount != 0) {
                        // 增加 差价账
                        BsCustomerRecord bsCustomerRecordDiscount = new BsCustomerRecord();
                        bsCustomerRecordDiscount.setCompanyId(myUserDetails.getCompanyId());
                        bsCustomerRecordDiscount.setAbstractt("销售打折/差价");
                        bsCustomerRecordDiscount.setDate(wmOutboundHeader.getPaymentTime() == null ? wmOutboundHeader.getOrderTime() : wmOutboundHeader.getPaymentTime());
                        bsCustomerRecordDiscount.setOrderNo(wmOutboundHeader.getOrderNo());
                        bsCustomerRecordDiscount.setCustomerCode(wmOutboundHeader.getBuyerCode());
                        bsCustomerRecordDiscount.setPay(discount);
                        bsCustomerRecordDiscount.setAuxiId(totalPriceForOrder.get(0).getAuxiId());
                        bsCustomerRecordDiscount.setType(BsCodeMaster.TYPE_PRICE_DIFFRENCT.getCode());
                        bsCustomerRecordService.save(bsCustomerRecordDiscount);
                    }
                    // 增加收款账
                    BsCustomerRecord bsCustomerRecordPay = new BsCustomerRecord();
                    bsCustomerRecordPay.setCompanyId(myUserDetails.getCompanyId());
                    bsCustomerRecordPay.setAbstractt("收到现金");
                    if (null != wmOutboundHeader.getPaymentPicUrl() && !"".equals(wmOutboundHeader.getPaymentPicUrl())) {
                        bsCustomerRecordPay.setPicUrl(wmOutboundHeader.getPaymentPicUrl());
                    }
                    bsCustomerRecordPay.setDate(wmOutboundHeader.getPaymentTime() == null ? wmOutboundHeader.getOrderTime() : wmOutboundHeader.getPaymentTime());
                    bsCustomerRecordPay.setOrderNo(wmOutboundHeader.getOrderNo());
                    bsCustomerRecordPay.setCustomerCode(wmOutboundHeader.getBuyerCode());
                    bsCustomerRecordPay.setPay(wmOutboundHeader.getCash());
                    bsCustomerRecordPay.setAuxiId(totalPriceForOrder.get(0).getAuxiId());
                    bsCustomerRecordPay.setType(BsCodeMaster.TYPE_RECEIVE_CASH.getCode());
                    bsCustomerRecordService.save(bsCustomerRecordPay);
                }
            }
            // 运费账增加
            if (null!=wmOutboundHeader.getFreight()&&wmOutboundHeader.getFreight() > 0.0) {
                //运费纪录
                BsCustomerRecord bsCustomerRecordFreight = new BsCustomerRecord();
                bsCustomerRecordFreight.setCompanyId(myUserDetails.getCompanyId());
                bsCustomerRecordFreight.setOrderNo(wmOutboundHeader.getOrderNo());
                bsCustomerRecordFreight.setDate(wmOutboundHeader.getOrderTime());
                if(WmsCodeMaster.FREIGHT_CASH_PAID.getCode().equals(wmOutboundHeader.getFreightType())){
                    // 我方现金付款
                    bsCustomerRecordFreight.setType(BsCodeMaster.TYPE_FREIGHT_CASH_PAID.getCode());
                    //  客户设置位我方虚拟客户
                    bsCustomerRecordFreight.setCustomerCode(Constants.XIBIN_USER_CODE);
                    bsCustomerRecordFreight.setAbstractt("为["+buyerName+"]支付运费");
                    bsCustomerRecordFreight.setPay(wmOutboundHeader.getFreight());
                    bsCustomerRecordFreight.setAuxiId(totalPriceForOrder.get(0).getAuxiId());
                    bsCustomerRecordService.save(bsCustomerRecordFreight);
                }else if (WmsCodeMaster.FREIGHT_DEBIT_ON_ACCOUNT.getCode().equals(wmOutboundHeader.getFreightType())){
                    // 账上扣款
                    bsCustomerRecordFreight.setAbstractt("支付运费");
                    bsCustomerRecordFreight.setType(BsCodeMaster.TYPE_FREIGHT_DEBIT_ON_ACCOUNT.getCode());
                    bsCustomerRecordFreight.setCustomerCode(wmOutboundHeader.getBuyerCode());
                    bsCustomerRecordFreight.setPay(wmOutboundHeader.getFreight());
                    bsCustomerRecordFreight.setAuxiId(totalPriceForOrder.get(0).getAuxiId());
                    bsCustomerRecordService.save(bsCustomerRecordFreight);
                }
                // 客户付款不处理
            }
            wmOutboundHeaderModel.setIsCalculated("Y");
            return this.saveOutbound(wmOutboundHeaderModel);
        } else {
            throw new BusinessException("出库单[" + orderNo + "]不存在");
        }
    }
    @Override
    public WmOutboundHeaderQueryItem cancelAccount(String orderNo) throws BusinessException {
        MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
        List<WmOutboundHeaderQueryItem> queryResult = this.selectByKey(orderNo);
        if (queryResult.size() > 0) {
            WmOutboundHeader wmOutboundHeader = new WmOutboundHeader();
            BeanUtils.copyProperties(queryResult.get(0), wmOutboundHeader);
                if (!WmsCodeMaster.SO_FULL_SHIPPING.getCode().equals(wmOutboundHeader.getStatus())) {
                    throw new BusinessException("出库单[" + wmOutboundHeader.getOrderNo() + "]不是完全发货状态");
                }
                if ("N".equals(wmOutboundHeader.getIsCalculated())) {
                    throw new BusinessException("出库单[" + wmOutboundHeader.getOrderNo() + "]未完成销售核算！");
                }
                BsCustomerRecord queryModel = new BsCustomerRecord();
                queryModel.setCompanyId(myUserDetails.getCompanyId());
                queryModel.setOrderNo(orderNo);
                List<BsCustomerRecord> bsCustomerRecords = bsCustomerRecordService.selectByExample(queryModel);
                int[] ids = new int[bsCustomerRecords.size()];
                int index = 0;
                // 只删除运费和收款账，不删除 销售（出库）账
                for (BsCustomerRecord record : bsCustomerRecords) {
                    if(!BsCodeMaster.TYPE_LOSS.getCode().equals(record.getType())
                            &&!BsCodeMaster.TYPE_RETURN_OUTBOUND.getCode().equals(record.getType())
                            &&!BsCodeMaster.TYPE_S_SALE.getCode().equals(record.getType())
                            &&!BsCodeMaster.TYPE_X_SALE.getCode().equals(record.getType()))
                    ids[index] = record.getId();
                    index++;
                }
                if(ids.length>0){
                    bsCustomerRecordService.delete(ids);
                }
            wmOutboundHeader.setIsCalculated("N");
            return this.saveOutbound(wmOutboundHeader);
        } else {
            throw new BusinessException("出库单[" + orderNo + "]不存在");
        }
    }

    @Override
    public String selectNextOrderNo(String orderTime) {
        // TODO Auto-generated method stub
        Map map = new HashMap<>();
        MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
        map.put("companyId", myUserDetails.getCompanyId());
        map.put("warehouseId", myUserDetails.getWarehouseId());
        map.put("orderTime", orderTime);
        return wmOutboundHeaderMapper.selectNextOrderNo(map);
    }

    @Override
    public String selectPreOrderNo(String orderTime) {
        // TODO Auto-generated method stub
        Map map = new HashMap<>();
        MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
        map.put("companyId", myUserDetails.getCompanyId());
        map.put("warehouseId", myUserDetails.getWarehouseId());
        map.put("orderTime", orderTime);
        return wmOutboundHeaderMapper.selectPreOrderNo(map);
    }

    @Override
    public Map selectRecentOrderHeaderByBuyerCode(String buyerCode) {
        // TODO Auto-generated method stub
        Map map = new HashMap<>();
        MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
        map.put("companyId", myUserDetails.getCompanyId());
        map.put("warehouseId", myUserDetails.getWarehouseId());
        map.put("buyerCode", buyerCode);
        return wmOutboundHeaderMapper.selectRecentOrderHeaderByBuyerCode(map);
    }

}

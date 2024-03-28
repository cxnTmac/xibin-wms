package com.xibin.wms.service.impl;

import com.xibin.core.daosupport.BaseManagerImpl;
import com.xibin.core.daosupport.BaseMapper;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.security.pojo.MyUserDetails;
import com.xibin.core.security.util.SecurityUtil;
import com.xibin.core.utils.CodeGenerator;
import com.xibin.wms.constants.BsCodeMaster;
import com.xibin.wms.constants.WmsCodeMaster;
import com.xibin.wms.dao.WmInboundDetailMapper;
import com.xibin.wms.dao.WmInboundHeaderMapper;
import com.xibin.wms.dao.WmInboundRecieveMapper;
import com.xibin.wms.pojo.BsCustomerRecord;
import com.xibin.wms.pojo.WmInboundHeader;
import com.xibin.wms.query.WmInboundDetailSumPriceQueryItem;
import com.xibin.wms.query.WmInboundHeaderQueryItem;
import com.xibin.wms.service.BsCustomerRecordService;
import com.xibin.wms.service.WmInboundHeaderService;
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
public class WmInboundHeaderServiceImpl extends BaseManagerImpl implements WmInboundHeaderService {
    @Autowired
    private HttpSession session;
    @Resource
    private WmInboundHeaderMapper wmInboundHeaderMapper;
    @Resource
    private WmInboundDetailMapper wmInboundDetailMapper;
    @Resource
    private WmInboundRecieveMapper wmInboundRecieveMapper;
    @Resource
    private BsCustomerRecordService bsCustomerRecordService;

    @Override
    public WmInboundHeader getInboundOrderById(int id) {
        // TODO Auto-generated method stub
        return wmInboundHeaderMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<WmInboundHeaderQueryItem> getAllInboundOrderByPage(Map map) {
        // TODO Auto-generated method stub
        return wmInboundHeaderMapper.selectAllByPage(map);
    }

    @Override
    public List<WmInboundHeaderQueryItem> selectByKey(String orderNo) {
        // TODO Auto-generated method stub
        MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
        return wmInboundHeaderMapper.selectByKey(orderNo, myUserDetails.getCompanyId().toString(),
                myUserDetails.getWarehouseId().toString());
    }

    @Override
    protected BaseMapper getMapper() {
        // TODO Auto-generated method stub
        return wmInboundHeaderMapper;
    }

    @Override
    public WmInboundHeaderQueryItem saveInbound(WmInboundHeader model) throws BusinessException {
        // TODO Auto-generated method stub
        MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
        model.setCompanyId(myUserDetails.getCompanyId());
        model.setWarehouseId(myUserDetails.getWarehouseId());
        if (null == model.getId() || model.getId() == 0) {
            model.setOrderNo(CodeGenerator.getCodeByCurrentTimeAndRandomNum("INB"));
        }
        this.save(model);
        List<WmInboundHeaderQueryItem> list = selectByKey(model.getOrderNo());
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public List<WmInboundHeader> selectByExample(WmInboundHeader model) {
        // TODO Auto-generated method stub
        return wmInboundHeaderMapper.selectByExample(model);
    }

    @Override
    public int remove(String orderNo) throws BusinessException {
        // TODO Auto-generated method stub
        MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
        List<WmInboundHeaderQueryItem> queryResult = this.selectByKey(orderNo);
        if (queryResult.size() > 0) {
            WmInboundHeader wmInboundHeader = new WmInboundHeader();
            BeanUtils.copyProperties(queryResult.get(0), wmInboundHeader);
            if (WmsCodeMaster.AUDIT_CLOSE.getCode().equals(wmInboundHeader.getAuditStatus())) {
                throw new BusinessException("入库单[" + wmInboundHeader.getOrderNo() + "]已审核");
            } else if (!WmsCodeMaster.INB_NEW.getCode().equals(wmInboundHeader.getStatus())) {
                throw new BusinessException("入库单[" + wmInboundHeader.getOrderNo() + "]不是创建状态");
            }
            // 删除明细
            wmInboundDetailMapper.deleteByOrderNo(orderNo, myUserDetails.getCompanyId().toString(), myUserDetails.getWarehouseId().toString());
            return this.delete(wmInboundHeader.getId());
        } else {
            throw new BusinessException("入库单[" + orderNo + "]不存在");
        }
    }

    @Override
    public WmInboundHeaderQueryItem audit(String orderNo) throws BusinessException {
        // TODO Auto-generated method stub
        MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
        List<WmInboundHeaderQueryItem> queryResult = this.selectByKey(orderNo);
        if (queryResult.size() > 0) {
            WmInboundHeader wmInboundHeader = new WmInboundHeader();
            BeanUtils.copyProperties(queryResult.get(0), wmInboundHeader);
            if (WmsCodeMaster.AUDIT_CLOSE.getCode().equals(wmInboundHeader.getAuditStatus())) {
                throw new BusinessException("入库单[" + wmInboundHeader.getOrderNo() + "]已审核");
            } else if (!WmsCodeMaster.INB_NEW.getCode().equals(wmInboundHeader.getStatus())) {
                throw new BusinessException("入库单[" + wmInboundHeader.getOrderNo() + "]不是创建状态");
            }
            wmInboundHeader.setAuditStatus(WmsCodeMaster.AUDIT_CLOSE.getCode());
            wmInboundHeader.setAuditOp(myUserDetails.getUserId());
            wmInboundHeader.setAuditTime(new Date());
            return this.saveInbound(wmInboundHeader);
        } else {
            throw new BusinessException("入库单[" + orderNo + "]不存在");
        }
    }

    @Override
    public WmInboundHeaderQueryItem cancelAudit(String orderNo) throws BusinessException {
        // TODO Auto-generated method stub
        List<WmInboundHeaderQueryItem> queryResult = this.selectByKey(orderNo);
        if (queryResult.size() > 0) {
            WmInboundHeader wmInboundHeader = new WmInboundHeader();
            BeanUtils.copyProperties(queryResult.get(0), wmInboundHeader);
            if (WmsCodeMaster.AUDIT_NEW.getCode().equals(wmInboundHeader.getAuditStatus())) {
                throw new BusinessException("入库单[" + wmInboundHeader.getOrderNo() + "]未审核");
            } else if (!WmsCodeMaster.INB_NEW.getCode().equals(wmInboundHeader.getStatus())) {
                throw new BusinessException("入库单[" + wmInboundHeader.getOrderNo() + "]不是创建状态");
            }
            wmInboundHeader.setAuditStatus(WmsCodeMaster.AUDIT_NEW.getCode());
            wmInboundHeader.setAuditOp(null);
            wmInboundHeader.setAuditTime(null);
            return this.saveInbound(wmInboundHeader);
        } else {
            throw new BusinessException("入库单[" + orderNo + "]不存在");
        }
    }

    @Override
    public WmInboundHeaderQueryItem close(String orderNo) throws BusinessException {
        // TODO Auto-generated method stub
        MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
        List<WmInboundHeaderQueryItem> queryResult = this.selectByKey(orderNo);
        if (queryResult.size() > 0) {
            WmInboundHeader wmInboundHeader = new WmInboundHeader();
            BeanUtils.copyProperties(queryResult.get(0), wmInboundHeader);
            if (WmsCodeMaster.INB_PI.getCode().equals(wmInboundHeader.getInboundType())) {
                if (!WmsCodeMaster.INB_FULL_RECEIVING.getCode().equals(wmInboundHeader.getStatus())) {
                    throw new BusinessException("入库单[" + wmInboundHeader.getOrderNo() + "]不是完全收货状态");
                }
            } else {
                if ("N".equals(wmInboundHeader.getIsCalculated())) {
                    throw new BusinessException("入库单[" + wmInboundHeader.getOrderNo() + "]还未入库核算");
                }
            }
            wmInboundHeader.setStatus(WmsCodeMaster.INB_CLOSE.getCode());
            return this.saveInbound(wmInboundHeader);
        } else {
            throw new BusinessException("入库单[" + orderNo + "]不存在");
        }
    }


    @Override
    public String selectNextOrderNo(String id) {
        // TODO Auto-generated method stub
        Map map = new HashMap<>();
        MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
        map.put("companyId", myUserDetails.getCompanyId());
        map.put("warehouseId", myUserDetails.getWarehouseId());
        map.put("id", id);
        return wmInboundHeaderMapper.selectNextOrderNo(map);
    }

    @Override
    public String selectPreOrderNo(String id) {
        // TODO Auto-generated method stub
        Map map = new HashMap<>();
        MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
        map.put("companyId", myUserDetails.getCompanyId());
        map.put("warehouseId", myUserDetails.getWarehouseId());
        map.put("id", id);
        return wmInboundHeaderMapper.selectPreOrderNo(map);
    }


    @Override
    public WmInboundHeaderQueryItem account(String orderNo) throws BusinessException {
        MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
        List<WmInboundHeaderQueryItem> queryResult = this.selectByKey(orderNo);
        if (queryResult.size() > 0) {
            WmInboundHeader wmInboundHeader = new WmInboundHeader();
            BeanUtils.copyProperties(queryResult.get(0), wmInboundHeader);
            if (!WmsCodeMaster.INB_FULL_RECEIVING.getCode().equals(wmInboundHeader.getStatus())) {
                throw new BusinessException("入库单[" + wmInboundHeader.getOrderNo() + "]不是完全收货状态");
            }
            if ("Y".equals(wmInboundHeader.getIsCalculated())) {
                throw new BusinessException("入库单[" + wmInboundHeader.getOrderNo() + "]已完成采购核算！");
            }
            //销售金额纪录
            Map queryMap = new HashMap();
            queryMap.put("orderNo", wmInboundHeader.getOrderNo());
            queryMap.put("companyId", myUserDetails.getCompanyId());
            queryMap.put("warehouseId", myUserDetails.getWarehouseId());
            List<WmInboundDetailSumPriceQueryItem> totalPriceForOrder = wmInboundDetailMapper.querySumPriceGroupByOrderNoForAccount(queryMap);
            if (totalPriceForOrder.size() == 0) {
                throw new BusinessException("出库单[" + wmInboundHeader.getOrderNo() + "]数据有误！");
            }
            BsCustomerRecord bsCustomerRecordSale = new BsCustomerRecord();
            bsCustomerRecordSale.setCompanyId(myUserDetails.getCompanyId());
            bsCustomerRecordSale.setAbstractt("采购配件");
            bsCustomerRecordSale.setDate(wmInboundHeader.getOrderTime());
            bsCustomerRecordSale.setOrderNo(wmInboundHeader.getOrderNo());
            bsCustomerRecordSale.setCustomerCode(wmInboundHeader.getSupplierCode());
            if (wmInboundHeader.getInboundType().equals(WmsCodeMaster.INB_XG.getCode())) {
                //现购
                bsCustomerRecordSale.setType(BsCodeMaster.TYPE_X_PURCHASE.getCode());
            }
            if (wmInboundHeader.getInboundType().equals(WmsCodeMaster.INB_CI.getCode())) {
                //奢购
                bsCustomerRecordSale.setType(BsCodeMaster.TYPE_S_PURCHASE.getCode());
            }
            if (wmInboundHeader.getInboundType().equals(WmsCodeMaster.INB_RI.getCode())) {
                //退货
                bsCustomerRecordSale.setType(BsCodeMaster.TYPE_RETURN_INBOUND.getCode());
            }
            if (wmInboundHeader.getInboundType().equals(WmsCodeMaster.INB_PI.getCode())) {
                //盘盈
                bsCustomerRecordSale.setType(BsCodeMaster.TYPE_SURPLUS.getCode());
            }
            bsCustomerRecordSale.setPay(totalPriceForOrder.get(0).getTotal());
            bsCustomerRecordSale.setAuxiId(totalPriceForOrder.get(0).getAuxiId());
            bsCustomerRecordService.save(bsCustomerRecordSale);
            wmInboundHeader.setIsCalculated("Y");
            return this.saveInbound(wmInboundHeader);
        } else {
            throw new BusinessException("入库单[" + orderNo + "]不存在");
        }
    }

    @Override
    public WmInboundHeaderQueryItem cancelAccount(String orderNo) throws BusinessException {
        MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
        List<WmInboundHeaderQueryItem> queryResult = this.selectByKey(orderNo);
        if (queryResult.size() > 0) {
            WmInboundHeader wmInboundHeader = new WmInboundHeader();
            BeanUtils.copyProperties(queryResult.get(0), wmInboundHeader);
                if (!WmsCodeMaster.INB_FULL_RECEIVING.getCode().equals(wmInboundHeader.getStatus())) {
                    throw new BusinessException("入库单[" + wmInboundHeader.getOrderNo() + "]不是完全收货状态");
                }
                if ("N".equals(wmInboundHeader.getIsCalculated())) {
                    throw new BusinessException("入库单[" + wmInboundHeader.getOrderNo() + "]未完成销售核算！");
                }
                BsCustomerRecord queryModel = new BsCustomerRecord();
                queryModel.setCompanyId(myUserDetails.getCompanyId());
                queryModel.setOrderNo(orderNo);
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
            wmInboundHeader.setIsCalculated("N");
            return this.saveInbound(wmInboundHeader);
        } else {
            throw new BusinessException("出库单[" + orderNo + "]不存在");
        }
    }
}

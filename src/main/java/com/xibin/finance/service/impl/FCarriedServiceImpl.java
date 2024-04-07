package com.xibin.finance.service.impl;

import com.xibin.core.utils.DateUtil;
import com.xibin.finance.constants.AccountConstants;
import com.xibin.finance.dao.FCarriedDao;
import com.xibin.finance.dao.FVoucherDao;
import com.xibin.finance.dao.FVoucherentryDao;
import com.xibin.finance.service.FCarriedService;
import com.xibin.wms.constants.WmsCodeMaster;
import com.xibin.wms.constants.WmsConstants;
import com.xibin.wms.dao.WmOutboundAllocMapper;
import com.xibin.wms.dao.WmOutboundHeaderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@Service
public class FCarriedServiceImpl implements FCarriedService {

    @Resource
    private FCarriedDao fCarriedDao;
    @Resource
    private FVoucherDao fVoucherDao;
    @Resource
    private FVoucherentryDao fVoucherentryDao;
    @Resource
    private WmOutboundHeaderMapper wmOutboundHeaderMapper;
    @Resource
    private WmOutboundAllocMapper wmOutboundAllocMapper;

    /**
     * 列表条件查询
     */
    @Override
    public List<Map<String, Object>> findFCarriedList(Map<String, Object> params) {
        return fCarriedDao.findFCarriedList(params);
    }


    /**
     * 成本结转
     */
    @Override
    public Map<String, Object> findtranTypeAddVoucheerList(Map<String, Object> params) {
        // 获取日期的当前月 起止时间
        long currentTimenum = 0;
        if (params.get("ymDate") != null || params.get("ymDate") != "") {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date date = formatter.parse(params.get("ymDate").toString());
                currentTimenum = date.getTime();
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            currentTimenum = new Date().getTime();
        }
        Date orderTimeFm = new Date(DateUtil.getMonthStartTime(currentTimenum, "GMT+8:00"));
        Date orderTimeTo = new Date(DateUtil.getMonthEndTime(currentTimenum, "GMT+8:00"));
        String orderTimeFmStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(orderTimeFm);
        String orderTimeToStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(orderTimeTo);
        // 查询业务信息
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> queryOrderNosMap = new HashMap<String, Object>();
        queryOrderNosMap.put("status", WmsCodeMaster.SO_CLOSE.getCode());
        queryOrderNosMap.put("isCostCalculated", "N");
        queryOrderNosMap.put("companyId", WmsConstants.DEFAULT_COMPANY_ID + "");
        queryOrderNosMap.put("warehouseId", WmsConstants.DEFAULT_WAREHOUSE_ID + "");
        List<String> outboundTypes = new ArrayList<String>();
        outboundTypes.add(WmsCodeMaster.OUB_PO.getCode());
        outboundTypes.add(WmsCodeMaster.OUB_XX.getCode());
        outboundTypes.add(WmsCodeMaster.OUB_RO.getCode());
        queryOrderNosMap.put("outboundTypes", outboundTypes);
        queryOrderNosMap.put("orderTimeFm", orderTimeFmStr);
        queryOrderNosMap.put("orderTimeTo", orderTimeToStr);
        List<String> orderNos = wmOutboundHeaderMapper.queryOrderNosByStatus(queryOrderNosMap);
        if (orderNos.size() == 0) {
            return result;
        }
        Map queryDetailsMap = new HashMap<>();
        queryDetailsMap.put("orderNos", orderNos);
        // queryDetailsMap.put("outboundType", WmsCodeMaster.OUB_PO.getCode());
        queryDetailsMap.put("companyId", WmsConstants.DEFAULT_COMPANY_ID);
        queryDetailsMap.put("warehouseId", WmsConstants.DEFAULT_WAREHOUSE_ID);
        Double sumOfMoney = wmOutboundAllocMapper.querySumCostForAccount(queryDetailsMap);
        // 构建凭证
        List<Map<String, Object>> list = new ArrayList<>();
        // 构建贷方库存商品
        Map<String, Object> map = new HashMap<>();
        // 编辑状态
        map.put("editState", "add");
        // 摘要
        map.put("explanation", "库存商品");
        // 科目ID
        map.put("accountID", AccountConstants.KUCUNSHANGPIN_ID);
        // 科目名称文本
        map.put("accountIDText", "库存商品");
        // 设置贷方金额
        BigDecimal bg = new BigDecimal(sumOfMoney.toString());
        map.put("dAmount", bg);
        list.add(map);
        // 构建借方主营业务成本
        map = new HashMap<>();
        map.put("editState", "add");
        map.put("explanation", "主营业务成本");
        map.put("accountID", AccountConstants.ZHUYINGYEWUCHENGBEN_ID);
        map.put("accountIDText", "主营业务成本");
        // 设置借方金额
        map.put("jAmount", bg);
        list.add(map);
        result.put("list", list);
        result.put("orderNos", orderNos);
        return result;
    }

    /**
     * 成本反结转
     */
    @Override
    public int fanVoucheer(Map<String, Object> params) {
        if (params.get("voucherID").toString() != null) {
            fVoucherDao.deleteFVoucherByIds(params.get("voucherID").toString().split(","));
            fVoucherentryDao.deleteFVoucherentryByIds(params.get("voucherID").toString().split(","));
            Map outboundHeaderUpdateMap = new HashMap<>();
            outboundHeaderUpdateMap.put("toCostVoucherId", 0);
            outboundHeaderUpdateMap.put("isCostCalculated", "N");
            outboundHeaderUpdateMap.put("costVoucherId", params.get("voucherID").toString());
            outboundHeaderUpdateMap.put("companyId", WmsConstants.DEFAULT_COMPANY_ID);
            outboundHeaderUpdateMap.put("warehouseId", WmsConstants.DEFAULT_WAREHOUSE_ID);
            wmOutboundHeaderMapper.updateCostCalculateByOrderNos(outboundHeaderUpdateMap);
        }
        return fCarriedDao.deleteVoucheerByIds(params.get("id").toString().split(","));
    }
}

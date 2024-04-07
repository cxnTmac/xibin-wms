package com.xibin.finance.service.impl;

import com.xibin.core.pojo.Message;
import com.xibin.core.utils.DateUtil;
import com.xibin.finance.dao.*;
import com.xibin.finance.pojo.FProfitLoss;
import com.xibin.finance.pojo.FVoucher;
import com.xibin.finance.pojo.FVoucherGroup;
import com.xibin.finance.pojo.FVoucherentry;
import com.xibin.finance.service.FProfitLossService;
import com.xibin.wms.constants.WmsConstants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@Service
public class FProfitLossServiceImpl implements FProfitLossService {

    @Resource
    private FProfitLossDao fProfitLossDao;
    @Resource
    private FAccountDao fAccountDao;
    @Resource
    private FAcctGroupDao fAcctGroupDao;

    @Resource
    private FVoucherDao fVoucherDao;
    @Resource
    private FVoucherentryDao fVoucherentryDao;
    @Resource
    private FVoucherGroupDao fVoucherGroupDao;

    /**
     * 列表条件查询
     */
    @Override
    public List<Map<String, Object>> findFProfitLossList(Map<String, Object> params) {
        return fProfitLossDao.findFProfitLossList(params);
    }

    /**
     * 结转
     */
    @Override
    public Message findtranTypeAddVoucheerList(Map<String, Object> params) {
        Message msg = new Message();
        params.put("carried", "0");
        List<Map<String, Object>> returnList = findFProfitLossList(params);
        if (returnList != null && returnList.size() > 0) {
            msg.setCode(0);
            msg.setMsg("本期已结转,请反结转后再次结转!");
            return msg;
        }
        // 查询 5损益类下科目id
        params.put("ids", fAcctGroupDao.getByParentIDs(5));
        String zihao = "";
        // 凭证添加
        FVoucher fVoucher = new FVoucher();
        fVoucher.setFdate(DateUtil.parseDate(params.get("fdate").toString()));
        fVoucher.setFyear(Long.valueOf(params.get("fyear").toString()));
        fVoucher.setFperiod(Long.valueOf(params.get("fperiod").toString()));
        fVoucher.setGroupID(Long.valueOf(params.get("groupID").toString()));
        Map<String, Object> fVoucherMap = fVoucherDao.getByFperiod(fVoucher.getFperiod(), fVoucher.getFyear());
        fVoucher.setNumber(Long.valueOf(fVoucherMap.get("maxNumber").toString()) + 1);
        zihao = Long.valueOf(fVoucherMap.get("maxNumber").toString()) + 1 + "";
        fVoucher.setExplanation("结转损益");
        fVoucher.setIsProfitLoss(1);
        fVoucher.setSerialNum(Long.valueOf(fVoucherMap.get("maxSerialNum").toString()) + 1);
        fVoucher.setChecked(2);
        fVoucher.setPosted(2);
        fVoucher.setPreparerID(WmsConstants.DEFAULT_FINANCE_USER_ID);
        fVoucher.setPosterID(WmsConstants.DEFAULT_FINANCE_USER_ID);
        fVoucher.setEntryCount(0L);
        fVoucher.setDebitTotal(0D);
        fVoucher.setCreditTotal(0D);
        fVoucher.setCashierID(WmsConstants.DEFAULT_FINANCE_USER_ID);
        fVoucher.setTransDate(new Date());
        fVoucherDao.saveFVoucher(fVoucher);
        long voucherId = fVoucher.getVoucherID();
        // 凭证明细添加
        List<Map<String, Object>> list = fVoucherentryDao.findFVoucherentryAccouintIDList(params);
        // 借方金额
        List<FVoucherentry> jList = new ArrayList<>();
        // 贷方金额
        List<FVoucherentry> dList = new ArrayList<>();
        // 分录号
        long entryID = 0L;
        double zongJMoney = 0D;
        double zongDMoney = 0D;
        double jlirun = 0;
        double dlirun = 0;
        for (Map<String, Object> map : list) {
            double money = Double.valueOf(map.get("money").toString());
            Map<String, Object> mapCashFlowItem = fAccountDao.getById(Long.valueOf(map.get("accountID").toString()));
            FVoucherentry fVoucherentry = new FVoucherentry();
            fVoucherentry.setVoucherID(voucherId);
            fVoucherentry.setExplanation("结转损益");
            fVoucherentry.setAccountID(Long.valueOf(map.get("accountID").toString()));
            if (mapCashFlowItem != null) {
                if ("1".equals(mapCashFlowItem.get("isCash").toString())
                        || "1".equals(mapCashFlowItem.get("isBank").toString())
                        || "1".equals(mapCashFlowItem.get("isContact").toString())) {
                    fVoucherentry.setCashFlowItem(1L);
                } else {
                    fVoucherentry.setCashFlowItem(0L);
                }
            } else {
                fVoucherentry.setCashFlowItem(0L);
            }
            if ("-1".equals(map.get("dc") + "")) {
                if ("1089".equals(map.get("accountID").toString())) {
                    money = 0;
                }
                fVoucherentry.setDc(1);
                // else if (money > 0) {
                // fVoucherentry.setDc(1);
                // }
                // // else if(money < 0){
                // // money = -money;
                // // fVoucherentry.setDc(0);
                // // }
                if (money != 0) {
                    jList.add(fVoucherentry);
                }
                zongJMoney += (money);
                jlirun += (money);
            } else if ("1".equals(map.get("dc") + "")) {
                zongDMoney += (money);
                dlirun += (money);
                if ("1135".equals(map.get("accountID").toString())) {
                    fVoucherentry.setDc(0);
                }
                fVoucherentry.setDc(0);
                // else if (money > 0) {
                // fVoucherentry.setDc(0);
                // }
                // else if(money < 0){
                // money = -money;
                // fVoucherentry.setDc(1);
                // }
                if (money != 0) {
                    dList.add(fVoucherentry);
                }
            }
            fVoucherentry.setAmount(money);
        }
        FVoucherentry fVoucherentry = new FVoucherentry();
        fVoucherentry.setVoucherID(voucherId);
        fVoucherentry.setExplanation("结转损益");
        fVoucherentry.setAccountID(1068L);
        fVoucherentry.setCashFlowItem(0L);
        for (FVoucherentry jFVoucherentry : jList) {
            jFVoucherentry.setEntryID(entryID);
            fVoucherentryDao.saveFVoucherentry(jFVoucherentry);
            entryID++;
        }
        // 本年利润借
        if (jlirun != 0) {
            fVoucherentry.setDc(0);
            fVoucherentry.setAmount(jlirun);
            fVoucherentry.setEntryID(entryID);
            fVoucherentryDao.saveFVoucherentry(fVoucherentry);
            entryID++;
        }
        // 本年利润贷
        if (dlirun != 0) {
            fVoucherentry.setDc(1);
            fVoucherentry.setAmount(dlirun);
            fVoucherentry.setEntryID(entryID);
            fVoucherentryDao.saveFVoucherentry(fVoucherentry);
            entryID++;
        }

        for (FVoucherentry dFVoucherentry : dList) {
            dFVoucherentry.setEntryID(entryID);
            fVoucherentryDao.saveFVoucherentry(dFVoucherentry);
            entryID++;
        }
        // 添加凭证分录数,借方金额合计,贷方金额合计
        fVoucher = new FVoucher();
        fVoucher.setVoucherID(voucherId);
        fVoucher.setEntryCount(entryID);
        fVoucher.setDebitTotal(zongJMoney + dlirun);
        fVoucher.setCreditTotal(zongDMoney + jlirun);
        fVoucherDao.updateFVoucher(fVoucher);

        // 添加结转损益记录
        FProfitLoss fProfitLoss = new FProfitLoss();
        fProfitLoss.setFyear(Long.valueOf(params.get("fyear").toString()));
        fProfitLoss.setFperiod(Long.valueOf(params.get("fperiod").toString()));
        fProfitLoss.setCarried(0L);
        fProfitLoss.setVoucherID(voucherId);
        fProfitLossDao.saveFProfitLoss(fProfitLoss);
        FVoucherGroup fVoucherGroup = fVoucherGroupDao.getById(params.get("groupID"));
        msg.setCode(200);
        msg.setMsg("结转损益成功,生成凭证:" + fVoucherGroup.getName() + "-" + zihao);
        return msg;
    }

    /**
     * 成本反结转
     */
    @Override
    public int fanVoucheer(Map<String, Object> params) {
        if (params.get("voucherID").toString() != null) {
            fVoucherDao.deleteFVoucherByIds(params.get("voucherID").toString().split(","));
            fVoucherentryDao.deleteFVoucherentryByIds(params.get("voucherID").toString().split(","));
        }
        return fProfitLossDao.deleteFProfitLossByIds(params.get("id").toString().split(","));
    }
}

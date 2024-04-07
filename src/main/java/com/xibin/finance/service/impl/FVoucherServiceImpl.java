package com.xibin.finance.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.xibin.core.pojo.Message;
import com.xibin.finance.dao.FAccountDao;
import com.xibin.finance.dao.FIncomeDao;
import com.xibin.finance.dao.FVoucherDao;
import com.xibin.finance.dao.FVoucherentryDao;
import com.xibin.finance.pojo.FIncome;
import com.xibin.finance.pojo.FVoucher;
import com.xibin.finance.pojo.FVoucherentry;
import com.xibin.finance.pojo.FVoucherentryVo;
import com.xibin.finance.service.FVoucherService;
import com.xibin.finance.service.SettleAccountsService;
import com.xibin.wms.constants.WmsConstants;
import com.xibin.wms.dao.BsCustomerRecordMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@Service
public class FVoucherServiceImpl implements FVoucherService {
    @Autowired
    private HttpSession session;
    @Autowired
    private FVoucherDao fVoucherDao;
    @Autowired
    private FVoucherentryDao fVoucherentryDao;
    @Autowired
    private BsCustomerRecordMapper bsCustomerRecordMapper;
    @Autowired
    private FIncomeDao fIncomeDao;
    @Autowired
    private SettleAccountsService settleAccountsService;
    @Autowired
    private FAccountDao fAccountDao;
    @Override
    public int deleteFVoucherByIds(String ids) {
        fVoucherentryDao.deleteFVoucherentryByIds(ids.split(","));
        return fVoucherDao.deleteFVoucherByIds(ids.split(","));
    }
    /**
     * 通过fperiod查询
     */
    public Map<String, Object> getByFperiod(Long fperiod, Long fyear) {
        return fVoucherDao.getByFperiod(fperiod, fyear);
    }
    /**
     * 订单详情
     */
    public void setFVoucherentry(FVoucher fVoucher, String cardData) {
        String data = HtmlUtils.htmlUnescape(cardData);
        List<FVoucherentryVo> list = JSONArray.parseArray(data, FVoucherentryVo.class);
        // List<FVoucherentryVo> list = JsonUtil.toList(data, FVoucherentryVo.class);
        String maxEntryID = fVoucherentryDao.selectMaxEntryID(fVoucher.getVoucherID());
        int entryID = 0;
        if (null!=maxEntryID&&!maxEntryID.isEmpty()) {
            entryID = Integer.valueOf(maxEntryID) + 1;
        }
        for (FVoucherentryVo vo : list) {
            String editState = vo.getEditState();
            FVoucherentry fVoucherentry = new FVoucherentry();
            BeanUtils.copyProperties(vo, fVoucherentry);
            if ("add".equals(editState)) {
                fVoucherentry.setVoucherID(fVoucher.getVoucherID());
                fVoucherentry.setEntryID(Long.valueOf(entryID));
                fVoucherentry.setExplanation(vo.getExplanation());
                fVoucherentry.setAccountID(vo.getAccountID());
                Map<String, Object> map = fAccountDao.getById(vo.getAccountID());
                String dc = null;
                if (vo.getJAmount() == null) {
                    dc = "0";
                    fVoucherentry.setAmount(vo.getDAmount());
                } else {
                    dc = "1";
                    fVoucherentry.setAmount(vo.getJAmount());
                }
                fVoucherentry.setDc(Integer.valueOf(dc));
                fVoucherentry.setItemID(vo.getItemID());
                fVoucherentry.setUnitPrice(vo.getUnitPrice());
                // if(vo.getUnitPrice() != null){
                // if(vo.getUnitPrice() == 0){
                // fVoucherentry.setQuantity(0D);
                // }else{
                // fVoucherentry.setQuantity(fVoucherentry.getAmount() /
                // vo.getUnitPrice());
                // }
                // }else{
                // fVoucherentry.setQuantity(0D);
                // }
                fVoucherentry.setMeasureUnitID(vo.getMeasureUnitID());
                if (map != null) {
                    if ("1".equals(map.get("isCash").toString()) || "1".equals(map.get("isBank").toString())
                            || "1".equals(map.get("isContact").toString())) {
                        fVoucherentry.setCashFlowItem(1L);
                    } else {
                        fVoucherentry.setCashFlowItem(0L);
                    }
                } else {
                    fVoucherentry.setCashFlowItem(0L);
                }
                fVoucherentryDao.saveFVoucherentry(fVoucherentry);
                entryID++;
            } else if ("update".equals(editState)) {
                if (!vo.getVoucherID().toString().isEmpty()) {
                    String dc = null;
                    if (vo.getJAmount() == null) {
                        dc = "0";
                        fVoucherentry.setAmount(vo.getDAmount());
                    } else {
                        dc = "1";
                        fVoucherentry.setAmount(vo.getJAmount());
                    }
                    fVoucherentry.setItemID(vo.getItemID());
                    fVoucherentry.setUnitPrice(vo.getUnitPrice());
                    fVoucherentry.setDc(Integer.valueOf(dc));
                    fVoucherentryDao.updateFVoucherentry(fVoucherentry);
                }
            } else if ("delete".equals(editState)) {
                if (vo.getVoucherID() != null) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("voucherID", vo.getVoucherID());
                    map.put("entryID", vo.getEntryID());
                    fVoucherentryDao.deleteFVoucherentry(map);
                }
            }
        }
    }
    public Message saveFVoucher(FVoucher fVoucher, String cardData) {
        Message returnMsg = new Message();
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = sdf2.format(fVoucher.getFdate());
        Message JZmsg = settleAccountsService.findIsJzList(dateString);
        if(JZmsg.getCode()==0){
            return JZmsg;
        }
        Map<String, Object> map = getByFperiod(fVoucher.getFperiod(), fVoucher.getFyear());
        fVoucher.setNumber(Long.valueOf(map.get("maxNumber").toString()) + 1);
        fVoucher.setSerialNum(Long.valueOf(map.get("maxSerialNum").toString()) + 1);
        // 凭证添加
        fVoucherDao.saveFVoucher(fVoucher);
        // 分录
        if (cardData != null) {
            setFVoucherentry(fVoucher, cardData);
        }
        returnMsg.setCode(200);
        returnMsg.setMsg("操作成功");
        returnMsg.setData(fVoucher.getVoucherID().toString());
        return returnMsg;
    }
    @Override
    public Message updateFVoucher(FVoucher fVoucher, String cardData) {
        Message returnMsg = new Message();
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = sdf2.format(fVoucher.getFdate());
        Message JZmsg = settleAccountsService.findIsJzList(dateString);
        if(JZmsg.getCode()==0){
            return JZmsg;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("number", fVoucher.getNumber());
        map.put("fdate", fVoucher.getFdate());
        map.put("voucherID", fVoucher.getVoucherID());
        Long num = fVoucherDao.fVoucherIsNumber(map);
        if (num > 0) {
            returnMsg.setCode(0);
            returnMsg.setMsg("凭证号已存在！");
            return returnMsg;
        }
        // 分录
        if (cardData != null) {
            setFVoucherentry(fVoucher, cardData);
        }
        fVoucherDao.updateFVoucher(fVoucher);
        returnMsg.setCode(200);
        returnMsg.setMsg("操作成功");
        return returnMsg;
    }
    @Override
    public FVoucher getById(Long id) {
        return fVoucherDao.getById(id);
    }
    /**
     * 列表条件查询
     */
    @Override
    public List<Map<String, Object>> findFVoucherentryList1(Map<String, Object> params) {
        List<Map<String, Object>> list = fVoucherentryDao.findFVoucherentryList1(params);
        String accountName = "";
        String num = "0";
        for (Map<String, Object> map : list) {
            num = "0";
            String groupID = "";
            String parentID = map.get("parentID").toString();
            String number = map.get("number").toString() + "-";
            accountName = map.get("accountIDText").toString();
            if(!"0".equals(parentID)){
                while("0".equals(num)){
                    Map<String, Object> fAccountMap = fAccountDao.getById(parentID);
                    num = fAccountMap.get("parentID").toString();
                    parentID = fAccountMap.get("parentID").toString();
                    groupID = fAccountMap.get("groupID").toString();
                    accountName = fAccountMap.get("name")+ "-" + accountName;
                    if("0".equals(num)){
                        num = fAccountMap.get("groupID").toString();
                    }
                }
            }else{
                groupID = map.get("groupID").toString();
            }
            map.put("accountIDText", number + accountName);
        }
        return list;
    }

    /**
     * 批量审批
     */
    @Override
    public void updateChecked(Map<String, Object> params) {
        String ids = params.get("voucherID").toString();
        for (String id : ids.split(",")) {
            FVoucher fVoucher = new FVoucher();
            fVoucher.setVoucherID(Long.valueOf(id));
            fVoucher.setChecked(Integer.valueOf(params.get("checked").toString()));
            fVoucher.setCheckerID(Integer.valueOf(params.get("checkerID").toString()));
            fVoucherDao.updateFVoucher(fVoucher);
        }
    }

    /**
     * 批量反审批
     */
    @Override
    public void updateBackChecked(Map<String, Object> params) {
        String ids = params.get("voucherID").toString();
        for (String id : ids.split(",")) {
            FVoucher fVoucher = new FVoucher();
            fVoucher.setVoucherID(Long.valueOf(id));
            fVoucher.setChecked(Integer.valueOf(params.get("checked").toString()));
            fVoucherDao.updateFVoucher(fVoucher);
        }
    }

    /**
     * 批量过账
     */
    @Override
    public void updatePosted(Map<String, Object> params) {
        String ids = params.get("voucherID").toString();
        for (String id : ids.split(",")) {
            FVoucher fVoucher = new FVoucher();
            fVoucher.setVoucherID(Long.valueOf(id));
            fVoucher.setPosted(Integer.valueOf(params.get("posted").toString()));
            fVoucherDao.updateFVoucher(fVoucher);
        }
    }

    /**
     * 收入结转添加
     */
    @Override
    public int saveFIncome(FVoucher fVoucher, String cardData, String fVoucherType,List<String> ids) {
        Map<String, Object> map = getByFperiod(fVoucher.getFperiod(), fVoucher.getFyear());
        fVoucher.setNumber(Long.valueOf(map.get("maxNumber").toString()) + 1);
        fVoucher.setSerialNum(Long.valueOf(map.get("maxSerialNum").toString()) + 1);
        // 凭证添加
        fVoucherDao.saveFVoucher(fVoucher);
        // 分录
        if (cardData != null) {
            setFVoucherentry(fVoucher, cardData);
        }

        // 成本结转订单添加凭证id
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int Y = cal.get(Calendar.YEAR);
        int M = cal.get(Calendar.MONTH) + 1;
        String payTime = null;
        if (M < 10) {
            payTime = Y + "-0" + M;
        } else {
            payTime = Y + "-" + M;
        }
        Map<String, Object> ordersMap = new HashMap<>();
        boolean hasIds = !ids.isEmpty();
        ordersMap.put("ids", ids);
        ordersMap.put("toVoucherId", fVoucher.getVoucherID());
        ordersMap.put("companyId", WmsConstants.DEFAULT_COMPANY_ID);
        ordersMap.put("warehouseId", WmsConstants.DEFAULT_WAREHOUSE_ID);
        if (hasIds) {
            bsCustomerRecordMapper.updateVoucherIdByOrderNos(ordersMap);
        }
        // 收入结转记录添加
        if (fVoucherType != null || fVoucherType != "") {
            FIncome fIncome = new FIncome();
            fIncome.setFyear(fVoucher.getFyear());
            fIncome.setFperiod(fVoucher.getFperiod());
            fIncome.setFdate(fVoucher.getFdate());
            fIncome.setType(Long.valueOf(fVoucherType));
            fIncome.setVoucherID(fVoucher.getVoucherID());
            fIncome.setCarried(0L);
            fIncomeDao.saveFIncome(fIncome);
        }
        return Integer.valueOf(fVoucher.getVoucherID().toString());
    }
}

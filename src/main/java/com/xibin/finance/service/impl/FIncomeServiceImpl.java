package com.xibin.finance.service.impl;

import com.xibin.core.utils.DateUtil;
import com.xibin.finance.constants.AccountConstants;
import com.xibin.finance.dao.FAccountDao;
import com.xibin.finance.dao.FIncomeDao;
import com.xibin.finance.dao.FVoucherDao;
import com.xibin.finance.dao.FVoucherentryDao;
import com.xibin.finance.service.FIncomeService;
import com.xibin.wms.constants.BsCodeMaster;
import com.xibin.wms.dao.*;
import com.xibin.wms.entity.BsCustomerRecordSumPriceQueryItem;
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
public class FIncomeServiceImpl implements FIncomeService {
    public static final String  DEFAULT_COMPANY_ID = "1";

    public static final String  DEFAULT_WAREHOUSE_ID = "1";
    @Resource
    private FIncomeDao fIncomeDao;
    @Resource
    private FAccountDao fAccountDao;
    @Resource
    private FVoucherDao fVoucherDao;
    @Resource
    private FVoucherentryDao fVoucherentryDao;
    @Resource
    private BsCustomerRecordMapper bsCustomerRecordMapper;

    /**
     * 列表条件查询
     */
    @Override
    public List<Map<String, Object>> findFIncomeList(Map<String, Object> params) {
        return fIncomeDao.findFIncomeList(params);
    }



    // 构建不同类型的凭证
    // 1.销售出库
    private Map<String, Object> createSaleVoucher(String timeFm, String timeTo) {
        // 查询业务信息
        Map<String, Object> result = new HashMap<String, Object>();
        Map queryIdsMap = new HashMap<>();
        List<String> types = new ArrayList<String>();
        types.add(BsCodeMaster.TYPE_S_SALE.getCode());
        types.add(BsCodeMaster.TYPE_X_SALE.getCode());
        queryIdsMap.put("types", types);
        queryIdsMap.put("companyId", DEFAULT_COMPANY_ID);
        queryIdsMap.put("noVoucherId", 1);
        queryIdsMap.put("timeFm", timeFm);
        queryIdsMap.put("timeTo", timeTo);
        List<String> idStrings = bsCustomerRecordMapper.queryIdsByForAccount(queryIdsMap);
        Map queryDetailsMap = new HashMap<>();
        queryDetailsMap.put("companyId", DEFAULT_COMPANY_ID);
        queryDetailsMap.put("warehouseId", DEFAULT_WAREHOUSE_ID);
        queryDetailsMap.put("types", types);
        queryDetailsMap.put("timeFm", timeFm);
        queryDetailsMap.put("timeTo", timeTo);
        queryDetailsMap.put("noVoucherId", 1);
        List<BsCustomerRecordSumPriceQueryItem> detailQueryItems = bsCustomerRecordMapper
                .querySumPriceGroupByCustomerForAccount(queryDetailsMap);
        // 构建凭证
        List<Map<String, Object>> list = new ArrayList<>();
        // 构建应收账款
        Map<String, Object> map = new HashMap<>();
        BigDecimal sumOfMoney = new BigDecimal("0");
        for (BsCustomerRecordSumPriceQueryItem detailQueryItem : detailQueryItems) {
            map = new HashMap<>();
            // 编辑状态
            map.put("editState", "add");
            // 摘要
            // map.put("explanation", "应收账款-" +
            // detailQueryItem.getCustomerName());
            map.put("explanation", "销售配件["+detailQueryItem.getCustomerName()+"]");
            // 科目ID
            map.put("accountID", AccountConstants.YINGSHOUZHANGKUAN_ID);
            // 核算项目ID（客户辅助核算ID）
            map.put("itemID", detailQueryItem.getAuxiId());
            // 科目名称文本
            map.put("accountIDText", "应收账款");
            // 辅助核算文本
            map.put("itemIDTxt", detailQueryItem.getCustomerCode()+"-"+detailQueryItem.getCustomerName());
            // 设置借方金额
            BigDecimal bg = new BigDecimal(detailQueryItem.getTotal().toString());
            map.put("jAmount", bg);
            sumOfMoney = sumOfMoney.add(bg);
            list.add(map);
        }
        // 构建主营业务收入
        map = new HashMap<>();
        map.put("editState", "add");
        map.put("explanation", "主营业务收入");
        map.put("accountID", AccountConstants.ZHUYINGYEWUSHOURU_ID);
        map.put("accountIDText", "主营业务收入");
        // 设置贷方金额
        // BigDecimal bg = new BigDecimal(sumOfMoney).setScale(2,
        // RoundingMode.UP);
        map.put("dAmount", sumOfMoney);
        list.add(map);
        result.put("list", list);
        // result.put("orderNos", orderNos);
        result.put("ids", idStrings);
        return result;
    }

    // 3.退货给供应商（退货出库）
    private Map<String, Object> createTuihuoChukuVoucher2(String timeFm, String timeTo) {
        // 查询业务信息
        Map<String, Object> result = new HashMap<String, Object>();
        Map queryIdsMap = new HashMap<>();
        queryIdsMap.put("companyId", DEFAULT_COMPANY_ID);
        queryIdsMap.put("type", BsCodeMaster.TYPE_RETURN_OUTBOUND.getCode());
        queryIdsMap.put("noVoucherId", 1);
        queryIdsMap.put("timeFm", timeFm);
        queryIdsMap.put("timeTo", timeTo);
        List<String> idStrings = bsCustomerRecordMapper.queryIdsByForAccount(queryIdsMap);
        Map queryDetailsMap = new HashMap<>();
        queryDetailsMap.put("companyId", DEFAULT_COMPANY_ID);
        queryDetailsMap.put("warehouseId", DEFAULT_WAREHOUSE_ID);
        queryDetailsMap.put("type", BsCodeMaster.TYPE_RETURN_OUTBOUND.getCode());
        queryDetailsMap.put("timeFm", timeFm);
        queryDetailsMap.put("timeTo", timeTo);
        queryDetailsMap.put("noVoucherId", 1);
        List<BsCustomerRecordSumPriceQueryItem> detailQueryItems = bsCustomerRecordMapper
                .querySumPriceGroupByCustomerForAccount(queryDetailsMap);
        // 构建凭证
        List<Map<String, Object>> list = new ArrayList<>();
        // 构建借方应付账款
        Map<String, Object> map = new HashMap<>();
        BigDecimal sumOfMoney = new BigDecimal("0");
        for (BsCustomerRecordSumPriceQueryItem detailQueryItem : detailQueryItems) {
            map = new HashMap<>();
            // 编辑状态
            map.put("editState", "add");
            map.put("explanation", "退货出库["+detailQueryItem.getCustomerName()+"]");
            // 科目ID
            map.put("accountID", AccountConstants.YINGFUZHANGKUAN_ID);
            // 科目名称文本
            map.put("accountIDText", "应付账款");
            // 核算项目ID（客户辅助核算ID）
            map.put("itemID", detailQueryItem.getAuxiId());
            map.put("itemIDTxt", detailQueryItem.getCustomerCode()+"-"+detailQueryItem.getCustomerName());
            // 设置贷方金额
            BigDecimal bg = new BigDecimal(detailQueryItem.getTotal().toString());
            map.put("jAmount", bg);
            sumOfMoney = sumOfMoney.add(bg);
            list.add(map);
        }
        // 构建贷方库存商品
        map = new HashMap<>();
        map.put("editState", "add");
        map.put("explanation", "库存商品");
        map.put("accountID", AccountConstants.KUCUNSHANGPIN_ID);
        map.put("accountIDText", "库存商品");
        // 设置贷方金额
        map.put("dAmount", sumOfMoney);
        list.add(map);
        result.put("list", list);
        result.put("ids", idStrings);
        return result;
    }

    // 4.盘亏
    private Map<String, Object> createPankuiVoucher2(String timeFm, String timeTo) {
        // 查询业务信息
        Map<String, Object> result = new HashMap<String, Object>();
        Map queryIdsMap = new HashMap<>();
        queryIdsMap.put("companyId", DEFAULT_COMPANY_ID);
        queryIdsMap.put("type", BsCodeMaster.TYPE_LOSS.getCode());
        queryIdsMap.put("noVoucherId", 1);
        queryIdsMap.put("timeFm", timeFm);
        queryIdsMap.put("timeTo", timeTo);
        List<String> idStrings = bsCustomerRecordMapper.queryIdsByForAccount(queryIdsMap);
        Map queryDetailsMap = new HashMap<>();
        queryDetailsMap.put("companyId", DEFAULT_COMPANY_ID);
        queryDetailsMap.put("warehouseId", DEFAULT_WAREHOUSE_ID);
        queryDetailsMap.put("type", BsCodeMaster.TYPE_LOSS.getCode());
        queryDetailsMap.put("timeFm", timeFm);
        queryDetailsMap.put("timeTo", timeTo);
        queryDetailsMap.put("noVoucherId", 1);
        List<BsCustomerRecordSumPriceQueryItem> detailQueryItems = bsCustomerRecordMapper
                .querySumPriceGroupByCustomerForAccount(queryDetailsMap);
        // 构建凭证
        List<Map<String, Object>> list = new ArrayList<>();
        // 构建贷方库存商品
        Map<String, Object> map = new HashMap<>();
        BigDecimal sumOfMoney = new BigDecimal("0");
        for (BsCustomerRecordSumPriceQueryItem detailQueryItem : detailQueryItems) {
            BigDecimal bg = new BigDecimal(detailQueryItem.getTotal().toString());
            sumOfMoney = sumOfMoney.add(bg);
        }
        map = new HashMap<>();
        // 编辑状态
        map.put("editState", "add");
        // 摘要
        map.put("explanation", "盘亏");
        // 科目ID
        map.put("accountID", AccountConstants.KUCUNSHANGPIN_ID);
        // 科目名称文本
        map.put("accountIDText", "库存商品");
        // 设置贷方金额
        map.put("dAmount", sumOfMoney);
        list.add(map);
        // 构建借方待处理财产损溢
        map = new HashMap<>();
        map.put("editState", "add");
        map.put("explanation", "待处理财产损溢");
        map.put("accountID", AccountConstants.DAICHULICAICHANSUNYI_ID);
        map.put("accountIDText", "待处理财产损溢");
        // 设置借方金额
        map.put("jAmount", sumOfMoney);
        list.add(map);
        result.put("list", list);
        // result.put("orderNos", orderNos);
        result.put("ids", idStrings);
        return result;
    }

    // 5.供应商（赊购）
    private Map<String, Object> createShegouVoucher2(String timeFm, String timeTo) {
        // 查询业务信息
        Map<String, Object> result = new HashMap<String, Object>();
        Map queryIdsMap = new HashMap<>();
        queryIdsMap.put("companyId", DEFAULT_COMPANY_ID);
        queryIdsMap.put("type", BsCodeMaster.TYPE_S_PURCHASE.getCode());
        queryIdsMap.put("noVoucherId", 1);
        queryIdsMap.put("timeFm", timeFm);
        queryIdsMap.put("timeTo", timeTo);
        List<String> idStrings = bsCustomerRecordMapper.queryIdsByForAccount(queryIdsMap);
        Map queryDetailsMap = new HashMap<>();
        queryDetailsMap.put("companyId", DEFAULT_COMPANY_ID);
        queryDetailsMap.put("warehouseId", DEFAULT_WAREHOUSE_ID);
        queryDetailsMap.put("type", BsCodeMaster.TYPE_S_PURCHASE.getCode());
        queryDetailsMap.put("timeFm", timeFm);
        queryDetailsMap.put("timeTo", timeTo);
        queryDetailsMap.put("noVoucherId", 1);
        List<BsCustomerRecordSumPriceQueryItem> detailQueryItems = bsCustomerRecordMapper
                .querySumPriceGroupByCustomerForAccount(queryDetailsMap);
        // 构建凭证
        List<Map<String, Object>> list = new ArrayList<>();
        // 构建贷方应付账款
        Map<String, Object> map = new HashMap<>();
        BigDecimal sumOfMoney = new BigDecimal("0");
        for (BsCustomerRecordSumPriceQueryItem detailQueryItem : detailQueryItems) {
            map = new HashMap<>();
            // 编辑状态
            map.put("editState", "add");
            // 摘要
            // map.put("explanation", "应付账款-" +
            // detailQueryItem.getCustomerName());
            // 科目ID
            map.put("accountID", AccountConstants.YINGFUZHANGKUAN_ID);
            // 核算项目ID（客户辅助核算ID）
            map.put("itemID", detailQueryItem.getAuxiId());
            // 科目名称文本
            map.put("accountIDText", "应付账款");
            map.put("explanation", "采购配件["+detailQueryItem.getCustomerName()+"]");
            map.put("itemIDTxt", detailQueryItem.getCustomerCode()+"-"+detailQueryItem.getCustomerName());
            // 设置贷方金额
            BigDecimal bg = new BigDecimal(detailQueryItem.getTotal().toString());
            map.put("dAmount", bg);
            sumOfMoney = sumOfMoney.add(bg);
            list.add(map);
        }
        // 构建借方库存商品
        map = new HashMap<>();
        map.put("editState", "add");
        map.put("explanation", "库存商品");
        map.put("accountID", AccountConstants.KUCUNSHANGPIN_ID);
        map.put("accountIDText", "库存商品");
        // 设置借方金额
        map.put("jAmount", sumOfMoney);
        list.add(map);
        result.put("list", list);
        // result.put("orderNos", orderNos);
        result.put("ids", idStrings);
        return result;
    }

    // 6.供应商（现购）
    private Map<String, Object> createXiangouVoucher2(String timeFm, String timeTo) {
        // 查询业务信息
        Map<String, Object> result = new HashMap<String, Object>();
        Map queryIdsMap = new HashMap<>();
        queryIdsMap.put("companyId", DEFAULT_COMPANY_ID);
        queryIdsMap.put("type", BsCodeMaster.TYPE_X_PURCHASE.getCode());
        queryIdsMap.put("noVoucherId", 1);
        queryIdsMap.put("timeFm", timeFm);
        queryIdsMap.put("timeTo", timeTo);
        List<String> idStrings = bsCustomerRecordMapper.queryIdsByForAccount(queryIdsMap);
        Map queryDetailsMap = new HashMap<>();
        queryDetailsMap.put("companyId", DEFAULT_COMPANY_ID);
        queryDetailsMap.put("warehouseId", DEFAULT_WAREHOUSE_ID);
        queryDetailsMap.put("type", BsCodeMaster.TYPE_X_PURCHASE.getCode());
        queryDetailsMap.put("timeFm", timeFm);
        queryDetailsMap.put("timeTo", timeTo);
        queryDetailsMap.put("noVoucherId", 1);
        List<BsCustomerRecordSumPriceQueryItem> detailQueryItems = bsCustomerRecordMapper
                .querySumPriceGroupByCustomerForAccount(queryDetailsMap);
        // 构建凭证
        List<Map<String, Object>> list = new ArrayList<>();
        // 构建贷方库存现金-现购付款
        Map<String, Object> map = new HashMap<>();
        BigDecimal sumOfMoney = new BigDecimal("0");
        for (BsCustomerRecordSumPriceQueryItem detailQueryItem : detailQueryItems) {
            map = new HashMap<>();
            // 编辑状态
            map.put("editState", "add");
            // 摘要
            map.put("explanation", "现购付款[" + detailQueryItem.getCustomerName()+"]");
            // 科目ID
            map.put("accountID", AccountConstants.KUCUNXIANJINFUKUAN_ID);
            // 核算项目ID（客户辅助核算ID）
            map.put("itemID", detailQueryItem.getAuxiId());
            // 科目名称文本
            map.put("accountIDText", "库存现金-现购付款");
            map.put("itemIDTxt", detailQueryItem.getCustomerCode()+"-"+detailQueryItem.getCustomerName());
            // 设置贷方金额
            BigDecimal bg = new BigDecimal(detailQueryItem.getTotal().toString());
            map.put("dAmount", bg);
            sumOfMoney = sumOfMoney.add(bg);
            list.add(map);
        }
        // 构建借方库存现金-现购付款
        for (BsCustomerRecordSumPriceQueryItem detailQueryItem : detailQueryItems) {
            map = new HashMap<>();
            // 编辑状态
            map.put("editState", "add");
            // 摘要
            map.put("explanation", "库存现金-现购付款-" + detailQueryItem.getCustomerName());
            // 科目ID
            map.put("accountID", AccountConstants.KUCUNXIANJINFUKUAN_ID);
            // 核算项目ID（客户辅助核算ID）
            map.put("itemID", detailQueryItem.getAuxiId());
            // 科目名称文本
            map.put("accountIDText", "库存现金-现购付款");
            // 设置贷方金额
            BigDecimal bg = new BigDecimal(detailQueryItem.getTotal().toString());
            map.put("jAmount", bg);
            list.add(map);
        }
        // 构建借方库存商品
        map = new HashMap<>();
        map.put("editState", "add");
        map.put("explanation", "库存商品");
        map.put("accountID", AccountConstants.KUCUNSHANGPIN_ID);
        map.put("accountIDText", "库存商品");
        // 设置借方金额
        map.put("jAmount", sumOfMoney);
        list.add(map);
        // 构建贷方库存现金
        map = new HashMap<>();
        map.put("editState", "add");
        map.put("explanation", "库存现金-蔡璇璇");
        map.put("accountID", AccountConstants.KUCUNXIANJINXUANXUAN_ID);
        map.put("accountIDText", "库存现金-蔡璇璇");
        // 设置贷方金额
        map.put("dAmount", sumOfMoney);
        list.add(map);
        result.put("list", list);
        // result.put("orderNos", orderNos);
        result.put("ids", idStrings);
        return result;
    }

    // 7.客户退货（退货入库）
    private Map<String, Object> createTuihuoRukuVoucher2(String timeFm, String timeTo) {
        // 查询业务信息
        Map<String, Object> result = new HashMap<String, Object>();
        Map queryIdsMap = new HashMap<>();
        queryIdsMap.put("companyId", DEFAULT_COMPANY_ID);
        queryIdsMap.put("type", BsCodeMaster.TYPE_RETURN_INBOUND.getCode());
        queryIdsMap.put("noVoucherId", 1);
        queryIdsMap.put("timeFm", timeFm);
        queryIdsMap.put("timeTo", timeTo);
        List<String> idStrings = bsCustomerRecordMapper.queryIdsByForAccount(queryIdsMap);
        Map queryDetailsMap = new HashMap<>();
        // queryDetailsMap.put("outboundType", WmsCodeMaster.OUB_CO.getCode());
        queryDetailsMap.put("companyId", DEFAULT_COMPANY_ID);
        queryDetailsMap.put("warehouseId", DEFAULT_WAREHOUSE_ID);
        queryDetailsMap.put("type", BsCodeMaster.TYPE_RETURN_INBOUND.getCode());
        queryDetailsMap.put("timeFm", timeFm);
        queryDetailsMap.put("timeTo", timeTo);
        queryDetailsMap.put("noVoucherId", 1);
        List<BsCustomerRecordSumPriceQueryItem> detailQueryItems = bsCustomerRecordMapper
                .querySumPriceGroupByCustomerForAccount(queryDetailsMap);
        // 构建凭证
        List<Map<String, Object>> list = new ArrayList<>();
        // 构建贷方应收账款
        Map<String, Object> map = new HashMap<>();
        BigDecimal sumOfMoney = new BigDecimal("0");
        for (BsCustomerRecordSumPriceQueryItem detailQueryItem : detailQueryItems) {
            map = new HashMap<>();
            // 编辑状态
            map.put("editState", "add");
            // 科目ID
            map.put("accountID", AccountConstants.YINGSHOUZHANGKUAN_ID);
            // 核算项目ID（客户辅助核算ID）
            map.put("itemID", detailQueryItem.getAuxiId());
            // 科目名称文本
            map.put("accountIDText", "应收账款");
            map.put("explanation", "退货入库["+detailQueryItem.getCustomerName()+"]");
            map.put("itemIDTxt", detailQueryItem.getCustomerCode()+"-"+detailQueryItem.getCustomerName());
            // 设置贷方金额
            BigDecimal bg = new BigDecimal(detailQueryItem.getTotal().toString());
            map.put("dAmount", bg);
            sumOfMoney = sumOfMoney.add(bg);
            list.add(map);
        }
        // 构建借方主营业务收入
        map = new HashMap<>();
        map.put("editState", "add");
        map.put("explanation", "主营业务收入");
        map.put("accountID", AccountConstants.ZHUYINGYEWUSHOURU_ID);
        map.put("accountIDText", "主营业务收入");
        // 设置借方金额
        map.put("jAmount", sumOfMoney);
        list.add(map);
        result.put("list", list);
        // result.put("orderNos", orderNos);
        result.put("ids", idStrings);
        return result;
    }

    // 8.盘盈
    private Map<String, Object> createPanyingVoucher(String timeFm, String timeTo) {
        // 查询业务信息
        Map<String, Object> result = new HashMap<String, Object>();
        Map queryIdsMap = new HashMap<>();
        queryIdsMap.put("companyId", DEFAULT_COMPANY_ID);
        queryIdsMap.put("type", BsCodeMaster.TYPE_SURPLUS.getCode());
        queryIdsMap.put("noVoucherId", 1);
        queryIdsMap.put("timeFm", timeFm);
        queryIdsMap.put("timeTo", timeTo);
        List<String> idStrings = bsCustomerRecordMapper.queryIdsByForAccount(queryIdsMap);
        Map queryDetailsMap = new HashMap<>();
        // queryDetailsMap.put("outboundType", WmsCodeMaster.OUB_CO.getCode());
        queryDetailsMap.put("companyId", DEFAULT_COMPANY_ID);
        queryDetailsMap.put("warehouseId", DEFAULT_WAREHOUSE_ID);
        queryDetailsMap.put("type", BsCodeMaster.TYPE_SURPLUS.getCode());
        queryDetailsMap.put("timeFm", timeFm);
        queryDetailsMap.put("timeTo", timeTo);
        queryDetailsMap.put("noVoucherId", 1);
        List<BsCustomerRecordSumPriceQueryItem> detailQueryItems = bsCustomerRecordMapper
                .querySumPriceGroupByCustomerForAccount(queryDetailsMap);
        // 构建凭证
        List<Map<String, Object>> list = new ArrayList<>();
        // 构建贷方待处理财产损溢
        Map<String, Object> map = new HashMap<>();
        BigDecimal sumOfMoney = new BigDecimal("0");
        for (BsCustomerRecordSumPriceQueryItem detailQueryItem : detailQueryItems) {
            BigDecimal bg = new BigDecimal(detailQueryItem.getTotal().toString());
            sumOfMoney = sumOfMoney.add(bg);
        }
        map = new HashMap<>();
        // 编辑状态
        map.put("editState", "add");
        // 摘要
        map.put("explanation", "待处理财产损溢");
        // 科目ID
        map.put("accountID", AccountConstants.DAICHULICAICHANSUNYI_ID);
        // 科目名称文本
        map.put("accountIDText", "待处理财产损溢");
        // 设置贷方金额
        map.put("dAmount", sumOfMoney);
        list.add(map);
        // 构建借方库存商品
        map = new HashMap<>();
        map.put("editState", "add");
        map.put("explanation", "盘盈");
        map.put("accountID", AccountConstants.KUCUNSHANGPIN_ID);
        map.put("accountIDText", "库存商品");
        // 设置借方金额
        map.put("jAmount", sumOfMoney);
        list.add(map);
        result.put("list", list);
        // result.put("orderNos", orderNos);
        result.put("ids", idStrings);
        return result;
    }

    // 9.运费凭证
    private Map<String, Object> createYunfeiVoucher(String timeFm, String timeTo) {
        Map<String, Object> result = new HashMap<String, Object>();
        Map queryIdsMap = new HashMap<>();
        List<String> types = new ArrayList<>();
        // 运费现金支付
        types.add(BsCodeMaster.TYPE_FREIGHT_CASH_PAID.getCode());
        // 运费账上扣款
        types.add(BsCodeMaster.TYPE_FREIGHT_DEBIT_ON_ACCOUNT.getCode());
        queryIdsMap.put("companyId", DEFAULT_COMPANY_ID);
        queryIdsMap.put("types", types);
        queryIdsMap.put("noVoucherId", 1);
        queryIdsMap.put("timeFm", timeFm);
        queryIdsMap.put("timeTo", timeTo);
        List<String> idStrings = bsCustomerRecordMapper.queryIdsByForAccount(queryIdsMap);
        // 查询现金付款运费来往账
        Map queryDetailsMap = new HashMap<>();
        // queryDetailsMap.put("outboundType", WmsCodeMaster.OUB_CO.getCode());
        queryDetailsMap.put("companyId", DEFAULT_COMPANY_ID);
        queryDetailsMap.put("timeFm", timeFm);
        queryDetailsMap.put("timeTo", timeTo);
        queryDetailsMap.put("type", BsCodeMaster.TYPE_FREIGHT_CASH_PAID.getCode());
        queryDetailsMap.put("noVoucherId", 1);
        List<BsCustomerRecordSumPriceQueryItem> detailQueryItems = bsCustomerRecordMapper
                .querySumPriceGroupByCustomerForAccount(queryDetailsMap);
        // 构建凭证
        List<Map<String, Object>> list = new ArrayList<>();
        // 构建贷方 库存现金-蔡璇璇
        Map<String, Object> map = new HashMap<>();
        BigDecimal sumOfMoney = new BigDecimal("0");
        for (BsCustomerRecordSumPriceQueryItem detailQueryItem : detailQueryItems) {
            BigDecimal bg = new BigDecimal(detailQueryItem.getTotal().toString());
            map = new HashMap<>();
            // 编辑状态
            map.put("editState", "add");
            // 摘要
            map.put("explanation", "运费现金支付[" + detailQueryItem.getCustomerName() + "]");
            // 科目ID
            map.put("accountID", AccountConstants.KUCUNXIANJINXUANXUAN_ID);
            // 科目名称文本
            map.put("accountIDText", "库存现金-蔡璇璇");
            // 设置贷方金额
            map.put("dAmount", bg);
            list.add(map);
            sumOfMoney = sumOfMoney.add(bg);
        }
        // 构建借方销售费用
        map = new HashMap<>();
        map.put("editState", "add");
        map.put("explanation", "销售费用");
        map.put("accountID", AccountConstants.XIAOSHOUFEIYONG_ID);
        map.put("accountIDText", "销售费用");
        // 设置借方金额
        map.put("jAmount", sumOfMoney);
        list.add(map);
        // 查询账上扣款运费来往账
        queryDetailsMap.put("type", BsCodeMaster.TYPE_FREIGHT_DEBIT_ON_ACCOUNT.getCode());
        List<BsCustomerRecordSumPriceQueryItem> detailQueryItems2 = bsCustomerRecordMapper
                .querySumPriceGroupByCustomerForAccount(queryDetailsMap);
        BigDecimal sumOfMoney2 = new BigDecimal("0");
        for (BsCustomerRecordSumPriceQueryItem detailQueryItem2 : detailQueryItems2) {
            BigDecimal bg = new BigDecimal(detailQueryItem2.getTotal().toString());
            map = new HashMap<>();
            // 编辑状态
            map.put("editState", "add");
            // 摘要
            map.put("explanation", "运费账上扣款[" + detailQueryItem2.getCustomerName() + "]");
            // 科目ID
            map.put("accountID", AccountConstants.YINGSHOUZHANGKUAN_ID);
            // 核算项目ID（客户辅助核算ID）
            map.put("itemID", detailQueryItem2.getAuxiId());
            // 科目名称文本
            map.put("accountIDText", "应收账款-" + detailQueryItem2.getCustomerName());
            map.put("itemIDTxt", detailQueryItem2.getCustomerCode()+"-"+detailQueryItem2.getCustomerName());
            // 设置贷方金额
            map.put("dAmount", bg);
            list.add(map);
            sumOfMoney2 = sumOfMoney2.add(bg);
        }
        // 构建借方销售费用
        map = new HashMap<>();
        map.put("editState", "add");
        map.put("explanation", "运费费用");
        map.put("accountID", AccountConstants.XIAOSHOUFEIYONG_ID);
        map.put("accountIDText", "销售费用");
        // 设置借方金额
        map.put("jAmount", sumOfMoney2);
        list.add(map);
        result.put("list", list);
        // result.put("orderNos", orderNos);
        result.put("ids", idStrings);
        return result;
    }


    // 10.供应商差价/下浮凭证
    private Map<String, Object> createSupplierDiscountVoucher(String timeFm, String timeTo) {
        Map<String, Object> result = new HashMap<String, Object>();
        List<String> types = new ArrayList<>();
        types.add(BsCodeMaster.TYPE_SUPPLIER_DISCOUNT.getCode());
        types.add(BsCodeMaster.TYPE_SUPPLIER_PRICE_DIFFRENCT.getCode());
        Map queryIdsMap = new HashMap<>();
        queryIdsMap.put("companyId", DEFAULT_COMPANY_ID);
        queryIdsMap.put("types", types);
        queryIdsMap.put("noVoucherId", 1);
        queryIdsMap.put("timeFm", timeFm);
        queryIdsMap.put("timeTo", timeTo);
        List<String> idStrings = bsCustomerRecordMapper.queryIdsByForAccount(queryIdsMap);
        // 查询供应商下浮差价往来记录
        Map queryDetailsMap = new HashMap<>();
        // queryDetailsMap.put("outboundType", WmsCodeMaster.OUB_CO.getCode());
        queryDetailsMap.put("companyId", DEFAULT_COMPANY_ID);
        queryDetailsMap.put("types", types);
        queryDetailsMap.put("noVoucherId", 1);
        queryDetailsMap.put("isGroupByType", 1);
        queryDetailsMap.put("timeFm", timeFm);
        queryDetailsMap.put("timeTo", timeTo);
        List<BsCustomerRecordSumPriceQueryItem> detailQueryItems = bsCustomerRecordMapper
                .querySumPriceGroupByCustomerForAccount(queryDetailsMap);
        // 构建凭证
        List<Map<String, Object>> list = new ArrayList<>();
        // 构建借方 应付货款
        Map<String, Object> map = new HashMap<>();
        BigDecimal sumOfMoney = new BigDecimal("0");
        for (BsCustomerRecordSumPriceQueryItem detailQueryItem : detailQueryItems) {
            BigDecimal bg = new BigDecimal(detailQueryItem.getTotal().toString());
            map = new HashMap<>();
            // 编辑状态
            map.put("editState", "add");
            // 摘要
            if (BsCodeMaster.TYPE_SUPPLIER_DISCOUNT.getCode().equals(detailQueryItem.getType())) {
                map.put("explanation", "下浮");
            }
            if (BsCodeMaster.TYPE_SUPPLIER_PRICE_DIFFRENCT.getCode().equals(detailQueryItem.getType())) {
                map.put("explanation", "差价");
            }
            // 核算项目ID（客户辅助核算ID）
            map.put("itemID", detailQueryItem.getAuxiId());
            // 科目ID
            map.put("accountID", AccountConstants.YINGFUZHANGKUAN_ID);
            // 科目名称文本
            map.put("accountIDText", "应付账款");
            map.put("itemIDTxt", detailQueryItem.getCustomerCode()+"-"+detailQueryItem.getCustomerName());
            // 设置贷方金额
            map.put("jAmount", bg);
            list.add(map);
            sumOfMoney = sumOfMoney.add(bg);
        }
        // 构建借方销售费用
        map = new HashMap<>();
        map.put("editState", "add");
        map.put("explanation", "");
        map.put("accountID", AccountConstants.ZHUYINGYEWUCHENGBEN_ID);
        map.put("accountIDText", "主营业务成本");
        // 设置借方金额
        map.put("dAmount", sumOfMoney);
        list.add(map);
        result.put("list", list);
        // result.put("orderNos", orderNos);
        result.put("ids", idStrings);
        return result;
    }

    // 11.客户差价/下浮凭证
    private Map<String, Object> createBuyerDiscountVoucher(String timeFm, String timeTo) {
        Map<String, Object> result = new HashMap<String, Object>();
        List<String> types = new ArrayList<>();
        types.add(BsCodeMaster.TYPE_ABSENCE_PAY.getCode());
        types.add(BsCodeMaster.TYPE_DISCOUNT.getCode());
        types.add(BsCodeMaster.TYPE_PRICE_DIFFRENCT.getCode());
        Map queryIdsMap = new HashMap<>();
        queryIdsMap.put("companyId", DEFAULT_COMPANY_ID);
        queryIdsMap.put("types", types);
        queryIdsMap.put("noVoucherId", 1);
        queryIdsMap.put("timeFm", timeFm);
        queryIdsMap.put("timeTo", timeTo);
        List<String> idStrings = bsCustomerRecordMapper.queryIdsByForAccount(queryIdsMap);
        // 查询供应商下浮差价往来记录
        Map queryDetailsMap = new HashMap<>();
        // queryDetailsMap.put("outboundType", WmsCodeMaster.OUB_CO.getCode());
        queryDetailsMap.put("companyId", DEFAULT_COMPANY_ID);
        queryDetailsMap.put("types", types);
        queryDetailsMap.put("noVoucherId", 1);
        queryDetailsMap.put("isGroupByType", 1);
        queryDetailsMap.put("timeFm", timeFm);
        queryDetailsMap.put("timeTo", timeTo);
        List<BsCustomerRecordSumPriceQueryItem> detailQueryItems = bsCustomerRecordMapper
                .querySumPriceGroupByCustomerForAccount(queryDetailsMap);
        // 构建凭证
        List<Map<String, Object>> list = new ArrayList<>();
        // 构建贷方 应收账款
        Map<String, Object> map = new HashMap<>();
        BigDecimal sumOfMoney = new BigDecimal("0");
        for (BsCustomerRecordSumPriceQueryItem detailQueryItem : detailQueryItems) {
            BigDecimal bg = new BigDecimal(detailQueryItem.getTotal().toString());
            map = new HashMap<>();
            // 编辑状态
            map.put("editState", "add");
            // 摘要
            if (BsCodeMaster.TYPE_DISCOUNT.getCode().equals(detailQueryItem.getType())) {
                map.put("explanation", "下浮");
                // 科目ID
                map.put("accountID", AccountConstants.YINGSHOUZHANGKUAN_ID);
                // 核算项目ID（客户辅助核算ID）
                map.put("itemID", detailQueryItem.getAuxiId());
            }
            if (BsCodeMaster.TYPE_PRICE_DIFFRENCT.getCode().equals(detailQueryItem.getType())) {
                map.put("explanation", "差价");
                // 科目ID
                map.put("accountID", AccountConstants.YINGSHOUZHANGKUAN_ID);
                // 核算项目ID（客户辅助核算ID）
                map.put("itemID", detailQueryItem.getAuxiId());
            }
            if (BsCodeMaster.TYPE_ABSENCE_PAY.getCode().equals(detailQueryItem.getType())) {
                map.put("explanation", "现销少付款");
                // 科目ID
                map.put("accountID", AccountConstants.YINGSHOUZHANGKUAN_ID);
            }
            // 科目名称文本
            map.put("accountIDText", "应收账款");
            map.put("itemIDTxt", detailQueryItem.getCustomerCode()+"-"+detailQueryItem.getCustomerName());
            // 设置贷方金额
            map.put("dAmount", bg);
            list.add(map);
            sumOfMoney = sumOfMoney.add(bg);
        }
        // 构建借方销售费用
        map = new HashMap<>();
        map.put("editState", "add");
        map.put("explanation", "");
        // ?科目为库存现金/销售费用？
        map.put("accountID", AccountConstants.XIAOSHOUFEIYONG_ID);
        map.put("accountIDText", "销售费用-差价/打折/抹零");
        // 设置借方金额
        map.put("jAmount", sumOfMoney);
        list.add(map);
        result.put("list", list);
        // result.put("orderNos", orderNos);
        result.put("ids", idStrings);
        return result;
    }



    // 12.付款
    private Map<String, Object> createFukuanVoucher(String timeFm, String timeTo) {
        // 查询业务信息
        Map<String, Object> result = new HashMap<String, Object>();
        Map queryIdsMap = new HashMap<>();
        queryIdsMap.put("companyId", DEFAULT_COMPANY_ID);
        queryIdsMap.put("type", BsCodeMaster.TYPE_PAY.getCode());
        queryIdsMap.put("noVoucherId", 1);
        queryIdsMap.put("timeFm", timeFm);
        queryIdsMap.put("timeTo", timeTo);
        List<String> idStrings = bsCustomerRecordMapper.queryIdsByForAccount(queryIdsMap);
        Map queryDetailsMap = new HashMap<>();
        queryDetailsMap.put("companyId", DEFAULT_COMPANY_ID);
        queryDetailsMap.put("warehouseId", DEFAULT_WAREHOUSE_ID);
        queryDetailsMap.put("type", BsCodeMaster.TYPE_PAY.getCode());
        queryDetailsMap.put("timeFm", timeFm);
        queryDetailsMap.put("timeTo", timeTo);
        queryDetailsMap.put("noVoucherId", 1);
        List<BsCustomerRecordSumPriceQueryItem> detailQueryItems = bsCustomerRecordMapper
                .querySumPriceGroupByCustomerForAccount(queryDetailsMap);
        // 构建凭证
        List<Map<String, Object>> list = new ArrayList<>();
        // 构建应付账款
        Map<String, Object> map = new HashMap<>();
        BigDecimal sumOfMoney = new BigDecimal("0");
        List<String> customerRecordIds = new ArrayList<>();
        for (BsCustomerRecordSumPriceQueryItem detailQueryItem : detailQueryItems) {
            map = new HashMap<>();
            // 编辑状态
            map.put("editState", "add");
            // 摘要
            map.put("explanation", "支付货款["+detailQueryItem.getCustomerName()+"]");
            // 科目ID
            map.put("accountID", AccountConstants.YINGFUZHANGKUAN_ID);
            // 核算项目ID（客户辅助核算ID）
            map.put("itemID", detailQueryItem.getAuxiId());
            // 科目名称文本
            map.put("accountIDText", "应付账款");
            map.put("itemIDTxt", detailQueryItem.getCustomerCode()+"-"+detailQueryItem.getCustomerName());
            // 设置借方金额
            BigDecimal bg = new BigDecimal(detailQueryItem.getTotal().toString());
            map.put("jAmount", bg);
            sumOfMoney = sumOfMoney.add(bg);
            list.add(map);
        }
        // 构建库存现金-蔡清雄
        map = new HashMap<>();
        map.put("editState", "add");
        map.put("explanation", "支付货款");
        map.put("accountID", AccountConstants.KUCUNXIANJINQINGXIONG_ID);
        map.put("accountIDText", "库存现金-蔡清雄");
        // 设置贷方金额
        // BigDecimal bg = new BigDecimal(sumOfMoney).setScale(2,
        // RoundingMode.UP);
        map.put("dAmount", sumOfMoney);
        list.add(map);
        result.put("list", list);
        // result.put("orderNos", orderNos);
        result.put("ids", idStrings);
        return result;
    }

    // 13.现金收款（现金/货到付款客户）
    private Map<String, Object> createCashVoucher(String timeFm, String timeTo) {
        // 查询业务信息
        Map<String, Object> result = new HashMap<String, Object>();
        Map queryIdsMap = new HashMap<>();
        queryIdsMap.put("companyId", DEFAULT_COMPANY_ID);
        queryIdsMap.put("type", BsCodeMaster.TYPE_RECEIVE_CASH.getCode());
        queryIdsMap.put("noVoucherId", 1);
        queryIdsMap.put("timeFm", timeFm);
        queryIdsMap.put("timeTo", timeTo);
        List<String> idStrings = bsCustomerRecordMapper.queryIdsByForAccount(queryIdsMap);
        Map queryDetailsMap = new HashMap<>();
        queryDetailsMap.put("companyId", DEFAULT_COMPANY_ID);
        queryDetailsMap.put("warehouseId", DEFAULT_WAREHOUSE_ID);
        queryDetailsMap.put("type", BsCodeMaster.TYPE_RECEIVE_CASH.getCode());
        queryDetailsMap.put("timeFm", timeFm);
        queryDetailsMap.put("timeTo", timeTo);
        queryDetailsMap.put("noVoucherId", 1);
        List<BsCustomerRecordSumPriceQueryItem> detailQueryItems = bsCustomerRecordMapper
                .querySumPriceGroupByCustomerForAccount(queryDetailsMap);
        // 构建凭证
        List<Map<String, Object>> list = new ArrayList<>();
        // 构建应收账款
        Map<String, Object> map = new HashMap<>();
        BigDecimal sumOfMoney = new BigDecimal("0");
        BigDecimal sumOfMoneyForBank = new BigDecimal("0");
        List<String> customerRecordIds = new ArrayList<>();
        for (BsCustomerRecordSumPriceQueryItem detailQueryItem : detailQueryItems) {
            map = new HashMap<>();
            // 编辑状态
            map.put("editState", "add");
            // 摘要
            map.put("explanation", "收到现金["+detailQueryItem.getCustomerName()+"]");
            // 科目ID
            map.put("accountID", AccountConstants.YINGSHOUZHANGKUAN_ID);
            // 核算项目ID（客户辅助核算ID）
            map.put("itemID", detailQueryItem.getAuxiId());
            // 科目名称文本
            map.put("accountIDText", "应收账款");
            map.put("itemIDTxt", detailQueryItem.getCustomerCode()+"-"+detailQueryItem.getCustomerName());
            // 设置贷方金额
            BigDecimal bg = new BigDecimal(detailQueryItem.getTotal().toString());
            map.put("dAmount", bg);
            if (BsCodeMaster.TYPE_BANK_TRANSFER.getCode().equals(detailQueryItem.getType())) {
                sumOfMoneyForBank = sumOfMoneyForBank.add(bg);
            } else {
                sumOfMoney = sumOfMoney.add(bg);
            }
            list.add(map);
        }
        // 构建库存现金-蔡清雄
        map = new HashMap<>();
        map.put("editState", "add");
        map.put("explanation", "收到货款");
        map.put("accountID", AccountConstants.KUCUNXIANJINQINGXIONG_ID);
        map.put("accountIDText", "库存现金-蔡清雄");
        map.put("jAmount", sumOfMoney);
        list.add(map);
        result.put("list", list);
        // result.put("orderNos", orderNos);
        result.put("ids", idStrings);
        return result;
    }
    // 14.转账收款（账期客户）
    private Map<String, Object> createShoukuanVoucher(String timeFm, String timeTo) {
        // 查询业务信息
        Map<String, Object> result = new HashMap<String, Object>();
        Map queryIdsMap = new HashMap<>();
        queryIdsMap.put("companyId", DEFAULT_COMPANY_ID);
        List<String> types = new ArrayList<String>();
        types.add(BsCodeMaster.TYPE_RECEIVE.getCode());
        types.add(BsCodeMaster.TYPE_BANK_TRANSFER.getCode());
        queryIdsMap.put("types", types);
        queryIdsMap.put("noVoucherId", 1);
        queryIdsMap.put("timeFm", timeFm);
        queryIdsMap.put("timeTo", timeTo);
        List<String> idStrings = bsCustomerRecordMapper.queryIdsByForAccount(queryIdsMap);
        Map queryDetailsMap = new HashMap<>();
        queryDetailsMap.put("companyId", DEFAULT_COMPANY_ID);
        queryDetailsMap.put("warehouseId", DEFAULT_WAREHOUSE_ID);
        queryDetailsMap.put("types", types);
        queryDetailsMap.put("timeFm", timeFm);
        queryDetailsMap.put("timeTo", timeTo);
        queryDetailsMap.put("noVoucherId", 1);
        List<BsCustomerRecordSumPriceQueryItem> detailQueryItems = bsCustomerRecordMapper
                .querySumPriceGroupByCustomerForAccount(queryDetailsMap);
        // 构建凭证
        List<Map<String, Object>> list = new ArrayList<>();
        // 构建应收账款
        Map<String, Object> map = new HashMap<>();
        BigDecimal sumOfMoney = new BigDecimal("0");
        BigDecimal sumOfMoneyForBank = new BigDecimal("0");
        List<String> customerRecordIds = new ArrayList<>();
        for (BsCustomerRecordSumPriceQueryItem detailQueryItem : detailQueryItems) {
            map = new HashMap<>();
            // 编辑状态
            map.put("editState", "add");

            // 科目ID
            map.put("accountID", AccountConstants.YINGSHOUZHANGKUAN_ID);
            // 核算项目ID（客户辅助核算ID）
            map.put("itemID", detailQueryItem.getAuxiId());
            // 科目名称文本
            map.put("accountIDText", "应收账款");
            map.put("itemIDTxt", detailQueryItem.getCustomerCode()+"-"+detailQueryItem.getCustomerName());
            // 设置贷方金额
            BigDecimal bg = new BigDecimal(detailQueryItem.getTotal().toString());
            map.put("dAmount", bg);
            if (BsCodeMaster.TYPE_BANK_TRANSFER.getCode().equals(detailQueryItem.getType())) {
                sumOfMoneyForBank = sumOfMoneyForBank.add(bg);
                // 摘要
                map.put("explanation", "公户转账["+detailQueryItem.getCustomerName()+"]");
            } else {
                sumOfMoney = sumOfMoney.add(bg);
                // 摘要
                map.put("explanation", "收到货款["+detailQueryItem.getCustomerName()+"]");
            }
            list.add(map);
        }
        // 构建库存现金-蔡清雄
        map = new HashMap<>();
        map.put("editState", "add");
        map.put("explanation", "收到货款");
        map.put("accountID", AccountConstants.KUCUNXIANJINQINGXIONG_ID);
        map.put("accountIDText", "库存现金-蔡清雄");
        map.put("jAmount", sumOfMoney);
        list.add(map);
        if(sumOfMoneyForBank.doubleValue()>0.0){
            map = new HashMap<>();
            map.put("editState", "add");
            map.put("explanation", "收到银行转账");
            map.put("accountID", AccountConstants.YINHANGCUNKUN_ID);
            map.put("accountIDText", "银行存款");
            map.put("jAmount", sumOfMoneyForBank);
            list.add(map);
        }
        result.put("list", list);
        result.put("ids", idStrings);
        return result;
    }
    // 收入结转
    @Override
    public Map<String, Object> findtranTypeAddVoucheerList(Map<String, Object> params) {
        // 获取日期的当前月 起止时间
        long currentTimenum = 0;
        if (params.get("fdate") != null || params.get("fdate") != "") {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date date = formatter.parse(params.get("fdate").toString());
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
        String type = params.get("type").toString();
        // 销售出库
        if ("1".equals(type)) {
            return this.createSaleVoucher(orderTimeFmStr, orderTimeToStr);
        } // 退货出库（退回给供应商）
        else if ("3".equals(type)) {
            return this.createTuihuoChukuVoucher2(orderTimeFmStr, orderTimeToStr);
        } // 盘亏出库
        else if ("4".equals(type)) {
            return this.createPankuiVoucher2(orderTimeFmStr, orderTimeToStr);
        } // 赊购入库
        else if ("5".equals(type)) {
            return this.createShegouVoucher2(orderTimeFmStr, orderTimeToStr);
        }
        // 现购入库
        else if ("6".equals(type)) {
            return this.createXiangouVoucher2(orderTimeFmStr, orderTimeToStr);
        }
        // 客户退货（退货入库）
        else if ("7".equals(type)) {
            return this.createTuihuoRukuVoucher2(orderTimeFmStr, orderTimeToStr);
        }
        // 盘盈入库
        else if ("8".equals(type)) {
            return this.createPanyingVoucher(orderTimeFmStr, orderTimeToStr);
        } // 运费
        else if ("9".equals(type)) {
            return this.createYunfeiVoucher(orderTimeFmStr, orderTimeToStr);
        } // 供应商差价/下浮
        else if ("10".equals(type)) {
            return this.createSupplierDiscountVoucher(orderTimeFmStr, orderTimeToStr);
        } // 客户差价/下浮
        else if ("11".equals(type)) {
            return this.createBuyerDiscountVoucher(orderTimeFmStr, orderTimeToStr);
        } // 付款
        else if ("12".equals(type)) {
            return this.createFukuanVoucher(orderTimeFmStr, orderTimeToStr);
        } // 现金收款（现金/货到付款客户）
        else if ("13".equals(type)) {
            return this.createCashVoucher(orderTimeFmStr, orderTimeToStr);
        }// 转账/银行打款（对账客户）
        else if ("14".equals(type)) {
            return this.createShoukuanVoucher(orderTimeFmStr, orderTimeToStr);
        }
        return null;
    }




    /**
     * 收入反结转
     */
    @Override
    public int fanVoucheer(Map<String, Object> params) {
        if (params.get("voucherID").toString() != null) {
            fVoucherDao.deleteFVoucherByIds(params.get("voucherID").toString().split(","));
            fVoucherentryDao.deleteFVoucherentryByIds(params.get("voucherID").toString().split(","));
            // 更新订单状态
            String type = params.get("type").toString();
            Map ordersUpdateMap = new HashMap<>();
            ordersUpdateMap.put("toVoucherId", 0);
            ordersUpdateMap.put("voucherId", params.get("voucherID").toString());
            ordersUpdateMap.put("companyId", DEFAULT_COMPANY_ID);
            ordersUpdateMap.put("warehouseId", DEFAULT_WAREHOUSE_ID);
            bsCustomerRecordMapper.updateVoucherIdByOrderNos(ordersUpdateMap);
        }
        return fIncomeDao.deleteFIncomeByIds(params.get("id").toString().split(","));
    }
}

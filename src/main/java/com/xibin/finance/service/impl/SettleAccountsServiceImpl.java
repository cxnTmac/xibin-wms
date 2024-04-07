package com.xibin.finance.service.impl;

import com.xibin.core.pojo.Message;
import com.xibin.finance.service.SettleAccountsService;
import com.xibin.finance.pojo.SettleAccounts;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.xibin.finance.dao.SettleAccountsDao;
import com.xibin.finance.dao.FVoucherDao;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;





    /**
     * 结账biz
     *
     * @copyright 大连骏骁网络科技有限公司
     * @author 骏骁(cxmail@qq.com)
     * @createDate 2019年10月09日
     * @version: V1.0.0
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Service
    public class SettleAccountsServiceImpl implements SettleAccountsService {

        @Resource
        private SettleAccountsDao settleAccountsDao;
        @Resource
        private FVoucherDao fVoucherDao;
        /**
         * 列表条件查询
         */
        @Override
        public List<Map<String , Object>> findSettleAccountsList(Map<String, Object> params) {
            return settleAccountsDao.findSettleAccountsList(params);
        }
        /**
         * 查询是否结账
         */
        @Override
        public Message findIsJzList(String fdate) {
            Message msg = new Message();
            int year = Integer.valueOf(fdate.substring(0, 4));
            int month = Integer.valueOf(fdate.substring(5, 7));
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("fyear", year);
            params.put("fperiod", month);
            Long length = settleAccountsDao.findIsJzList(params);
            if(length>0) {
                msg.setCode(0);
                msg.setMsg("请求失败，该期已结账！");
                return msg;
            }else {
                msg.setCode(200);
                msg.setMsg("操作成功");
                return msg;
            }
        }
        /**
         * 通过id查询
         */
        @Override
        public SettleAccounts getById(Long id) {
            return settleAccountsDao.getById(id);
        }

        /**
         * 删除
         */
        @Override
        public int deleteSettleAccountsByIds(String id, String fyear, String fperiod) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("fyear", fyear);
            params.put("fperiod", fperiod);
            params.put("jz", "0");
            settleAccountsDao.updateSettleAccounts(params);
            return settleAccountsDao.deleteSettleAccountsByIds(id);
        }

        /**
         * 添加
         */
        @Override
        public Message saveSettleAccounts(String ymDate) {
            Message msg = new Message();
            int year = Integer.valueOf(ymDate.substring(0, 4));
            int month = Integer.valueOf(ymDate.substring(5, 7));
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("fyear", year);
            params.put("fperiod", month);
            List<Map<String, Object>> returnList = findSettleAccountsList(params);
            if(returnList != null && returnList.size()>0){
                msg.setCode(0);
                msg.setMsg("本期已结账,请反结账后再次结账!");
                return msg;
            }
            params.put("jz", "1");
            settleAccountsDao.updateSettleAccounts(params);
            settleAccountsDao.saveSettleAccounts(params);
            msg.setCode(200);
            msg.setMsg("操作成功");
            return msg;
        }

    }


package com.xibin.finance.service;

import com.xibin.core.pojo.Message;
import com.xibin.finance.pojo.SettleAccounts;

import java.util.List;
import java.util.Map;

public interface SettleAccountsService {
    List<Map<String , Object>> findSettleAccountsList(Map<String, Object> params);

    Message findIsJzList(String fdate);

    SettleAccounts getById(Long id);

    int deleteSettleAccountsByIds(String id, String fyear, String fperiod);

    Message saveSettleAccounts(String ymDate);
}

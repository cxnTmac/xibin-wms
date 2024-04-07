package com.xibin.finance.service;

import java.util.List;
import java.util.Map;

public interface FIncomeService {
    List<Map<String, Object>> findFIncomeList(Map<String, Object> params);

    // 收入结转
    Map<String, Object> findtranTypeAddVoucheerList(Map<String, Object> params);

    int fanVoucheer(Map<String, Object> params);
}

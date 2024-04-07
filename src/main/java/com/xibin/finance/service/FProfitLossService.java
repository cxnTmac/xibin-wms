package com.xibin.finance.service;

import com.xibin.core.pojo.Message;

import java.util.List;
import java.util.Map;

public interface FProfitLossService {
    List<Map<String, Object>> findFProfitLossList(Map<String, Object> params);

    Message findtranTypeAddVoucheerList(Map<String, Object> params);

    int fanVoucheer(Map<String, Object> params);
}

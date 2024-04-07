package com.xibin.finance.service;

import java.util.List;
import java.util.Map;

public interface FCarriedService {
    List<Map<String, Object>> findFCarriedList(Map<String, Object> params);

    Map<String, Object> findtranTypeAddVoucheerList(Map<String, Object> params);

    int fanVoucheer(Map<String, Object> params);
}

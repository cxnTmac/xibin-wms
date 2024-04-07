package com.xibin.finance.service;

import com.xibin.core.pojo.Message;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.xibin.finance.pojo.FVoucher;

import java.util.List;
import java.util.Map;

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@Service
public interface FVoucherService {
    int deleteFVoucherByIds(String ids);

    public Message saveFVoucher(FVoucher fVoucher, String cardData);

    Message updateFVoucher(FVoucher fVoucher, String cardData);

    FVoucher getById(Long id);

    List<Map<String, Object>> findFVoucherentryList1(Map<String, Object> params);

    void updateChecked(Map<String, Object> params);

    void updateBackChecked(Map<String, Object> params);

    void updatePosted(Map<String, Object> params);

    int saveFIncome(FVoucher fVoucher, String cardData, String fVoucherType, List<String> ids);
}

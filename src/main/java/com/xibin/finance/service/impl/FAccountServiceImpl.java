package com.xibin.finance.service.impl;

import com.xibin.core.exception.BusinessException;
import com.xibin.finance.dao.FAccountDao;
import com.xibin.finance.pojo.FAccount;
import com.xibin.finance.service.FAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@Service
public class FAccountServiceImpl implements FAccountService {
    @Autowired
    private HttpSession session;
    @Autowired
    private FAccountDao fAccountDao;
    @Override
    public FAccount getAccountById(int id) {
        return null;
    }

    @Override
    public List<FAccount> getAllAccountByPage(Map map) {
        return null;
    }

    @Override
    public List<FAccount> getAllAccount(Map map) {
        return fAccountDao.findFAccountList(map);
    }

    @Override
    public int removeAccount(int id, String areaCode) throws BusinessException {
        return 0;
    }

    @Override
    public FAccount saveAccount(FAccount model) throws BusinessException {
        return null;
    }

    @Override
    public List<FAccount> selectByKey(String accountID) {
        return null;
    }
}

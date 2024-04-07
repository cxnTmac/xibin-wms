package com.xibin.finance.service;

import com.xibin.core.exception.BusinessException;
import com.xibin.finance.pojo.FAccount;

import java.util.List;
import java.util.Map;

public interface FAccountService {
	public FAccount getAccountById(int id);
	
	public List<FAccount> getAllAccountByPage(Map map);

	List<FAccount> getAllAccount(Map map);

	public int removeAccount(int id, String areaCode) throws BusinessException;
	
	public FAccount saveAccount(FAccount model) throws BusinessException;
	
	public List<FAccount> selectByKey(String accountID);
}

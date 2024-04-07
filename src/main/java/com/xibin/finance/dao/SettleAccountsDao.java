package com.xibin.finance.dao;


import com.xibin.finance.pojo.SettleAccounts;

import java.util.List;
import java.util.Map;


/**
 * 结账dao实现
 * 
 * @copyright 大连骏骁网络科技有限公司
 * @author 骏骁(cxmail@qq.com)
 * @createDate 2019年10月09日
 * @version: V1.0.0
 */
public interface SettleAccountsDao {

	/**
	 * 列表条件查询
	 */
	public List<Map<String , Object>> findSettleAccountsList(Map<String, Object> params);
	/**
	 * 查询是否结账
	 */
	public Long findIsJzList(Map<String, Object> params);

	/**
	 * 通过id查询
	 */
	public SettleAccounts getById(Object id);

	/**
	 * 删除
	 */
	public int deleteSettleAccountsByIds(String id);

	/**
	 * 修改凭证结账状态
	 */
	public int updateSettleAccounts(Map<String, Object> params);
	/**
	 * 添加
	 */
	public int saveSettleAccounts(Map<String, Object> params);

}

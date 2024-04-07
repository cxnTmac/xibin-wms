package com.xibin.finance.dao;

import com.xibin.finance.pojo.FIncome;
import java.util.List;
import java.util.Map;

/**
 * 科目类别dao实现
 * 
 * @copyright 大连骏骁网络科技有限公司
 * @author 骏骁(cxmail@qq.com)
 * @createDate 2019年04月23日
 * @version: V1.0.0
 */
public interface FIncomeDao {

	/**
	 * 列表条件查询
	 */
	public List<Map<String, Object>> findFIncomeList(Map<String, Object> params);
	
	/**
	 * 成本反结转
	 */
	public int fanVoucheer(Map<String, Object> params);
	
	/**
	 * 订单金额
	 */
	public Map<String, Object> findtranTypeAddVoucheer(Map<String, Object> params);
	
	/**
	 * 微信银联金额
	 */
	public Double selectYlWx(Map<String, Object> params);

	/**
	 * 添加
	 */
	public int saveFIncome(FIncome fIncome);

	/**
	 * 删除
	 */
	public int deleteFIncomeByIds(String[] ids);
}

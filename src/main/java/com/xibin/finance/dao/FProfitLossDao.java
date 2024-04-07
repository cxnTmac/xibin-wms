package com.xibin.finance.dao;

import com.xibin.finance.pojo.FProfitLoss;

import java.util.List;
import java.util.Map;

/**
 * 结转损益dao实现
 * 
 * @copyright 大连骏骁网络科技有限公司
 * @author 骏骁(cxmail@qq.com)
 * @createDate 2019年04月23日
 * @version: V1.0.0
 */
public interface FProfitLossDao {

	/**
	 * 列表条件查询
	 */
	public List<Map<String, Object>> findFProfitLossList(Map<String, Object> params);
	
	/**
	 * 反结转
	 */
	public int fanVoucheer(Map<String, Object> params);
	
	/**
	 * 添加
	 */
	public int saveFProfitLoss(FProfitLoss fProfitLoss);

	/**
	 * 删除
	 */
	public int deleteFProfitLossByIds(String[] ids);
}

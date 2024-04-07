package com.xibin.finance.dao;


import com.xibin.finance.pojo.FVoucherentry;

import java.util.List;
import java.util.Map;

/**
 * 凭证分录dao实现
 * 
 * @copyright 大连骏骁网络科技有限公司
 * @author 骏骁(cxmail@qq.com)
 * @createDate 2019年04月25日
 * @version: V1.0.0
 */
public interface FVoucherentryDao {

	/**
	 * 列表条件查询
	 */
	public List<Map<String, Object>> findFVoucherentryList1(Map<String, Object> params);
	
	/**
	 * 列表条件查询AccouintID
	 */
	public List<Map<String, Object>> findFVoucherentryAccouintIDList(Map<String, Object> params);

	/**
	 * 通过id查询
	 */
	public FVoucherentry getById(Object id);
	
	/**
	 * 通过科目查询金额
	 */
	public Double getAccountID(Map<String, Object> params);

	/**
	 * 删除
	 */
	public int deleteFVoucherentryByIds(String[] ids);
	
	/**
	 * 删除
	 */
	public int deleteFVoucherentry(Map<String, Object> params);

	/**
	 * 修改
	 */
	public int updateFVoucherentry(FVoucherentry fVoucherentry);
	
	/**
	 * 添加
	 */
	public int saveFVoucherentry(FVoucherentry fVoucherentry);
	
	/**
	 * 查询最大凭证号
	 */
	public String selectMaxEntryID(Object voucherID);
	/**
	 * 科目凭证批量替换
	 */
	public int exchangefVoucherentry(Map<String, Object> parame);
}

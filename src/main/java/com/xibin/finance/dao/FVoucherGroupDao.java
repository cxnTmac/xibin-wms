package com.xibin.finance.dao;

import com.xibin.finance.pojo.FVoucherGroup;

import java.util.List;
import java.util.Map;

/**
 * 凭证字dao实现
 * 
 * @copyright 大连骏骁网络科技有限公司
 * @author 骏骁(cxmail@qq.com)
 * @createDate 2019年04月23日
 * @version: V1.0.0
 */
public interface FVoucherGroupDao {

	/**
	 * 列表条件查询
	 */
	public List<FVoucherGroup> findFVoucherGroupList(Map<String, Object> params);

	/**
	 * 通过id查询
	 */
	public FVoucherGroup getById(Object id);

	/**
	 * 删除
	 */
	public int deleteFVoucherGroupByIds(String ids);

	/**
	 * 修改
	 */
	public int updateFVoucherGroup(FVoucherGroup fVoucherGroup);
	
	/**
	 * 添加
	 */
	public int saveFVoucherGroup(FVoucherGroup fVoucherGroup);

}

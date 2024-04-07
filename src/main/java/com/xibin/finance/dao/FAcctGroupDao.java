package com.xibin.finance.dao;

import com.xibin.finance.pojo.FAcctGroup;

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
public interface FAcctGroupDao {

	/**
	 * 列表条件查询
	 */
	public List<FAcctGroup> findFAcctGroupList(Map<String, Object> params);

	/**
	 * 通过id查询
	 */
	public FAcctGroup getById(Object id);
	
	/**
	 * 通过getByParentID查询
	 */
	public String getByParentIDs(Object id);

	/**
	 * 删除
	 */
	public int deleteFAcctGroupByIds(String[] ids);

	/**
	 * 修改
	 */
	public int updateFAcctGroup(FAcctGroup fAcctGroup);
	
	/**
	 * 添加
	 */
	public int saveFAcctGroup(FAcctGroup fAcctGroup);

}

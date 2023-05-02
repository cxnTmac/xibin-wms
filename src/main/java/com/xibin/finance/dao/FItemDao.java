package com.xibin.finance.dao;

import com.xibin.finance.pojo.FItem;

import java.util.List;
import java.util.Map;



/**
 * 核算项目dao实现
 * 
 * @copyright 大连骏骁网络科技有限公司
 * @author 骏骁(cxmail@qq.com)
 * @createDate 2019年04月23日
 * @version: V1.0.0
 */
public interface FItemDao {

	/**
	 * 列表条件查询
	 */
	public List<Map<String, Object>> findFItemList(Map<String, Object> params);

	/**
	 * 查询直属下级
	 */
	public List<Map<String, Object>> findItemClass(Map<String, Object> map);

	/**
	 * 查询别表直属下级
	 */
	public List<Map<String, Object>> findItem(Map<String, Object> map);
	
	/**
	 * 判断有没有下级
	 */
	public List<Map<String, Object>> findTreeDataTrueFalse(Map<String, Object> map);

	/**
	 * 查询别表下一级num
	 */
	public Map<String, Object> findItemNum(String ids);
	
	/**
	 * 通过id查询
	 */
	public FItem getById(Object id);
	
	/**
	 * 通过ItemClassID查询
	 */
	public FItem getByItemClassID(Object id);

	/**
	 * 删除
	 */
	public int deleteFItemByIds(String[] ids);

	/**
	 * 修改
	 */
	public int updateFItem(FItem fItem);
	
	/**
	 * 添加
	 */
	public int saveFItem(FItem fItem);
	
	/**
	 * 客户列表条件查询
	 */
	public List<Map<String,Object>> findMemberList(Map<String, Object> params);

}

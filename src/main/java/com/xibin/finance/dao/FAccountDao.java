package com.xibin.finance.dao;

import com.xibin.finance.pojo.FAccount;

import java.util.List;
import java.util.Map;

/**
 * 会计科目 dao实现
 * 
 * @copyright 大连骏骁网络科技有限公司
 * @author 骏骁(cxmail@qq.com)
 * @createDate 2019年04月23日
 * @version: V1.0.0
 */
public interface FAccountDao {

	/**
	 * 列表条件查询
	 */
	public List<Map<String, Object>> findFAccountList(Map<String, Object> params);

	/**
	 * 通过id查询
	 */
	public Map<String, Object> getById(Object id);

	/**
	 * 通过parentID查询
	 */
	public List<Map<String, Object>> getByParentID(Object parentID);

	/**
	 * 通过getByGroupID查询
	 */
	public List<Map<String, Object>> getByGroupID(Object groupID);

	/**
	 * 通过number查询
	 */
	public Map<String, Object> getByNumber(Object number);

	/**
	 * 通过name查询
	 */
	public Map<String, Object> getByName(Object name);

	/**
	 * 删除
	 */
	public int deleteFAccountByIds(String[] ids);

	/**
	 * 修改
	 */
	public int updateFAccount(FAccount fAccount);

	/**
	 * 添加
	 */
	public int saveFAccount(FAccount fAccount);

	/**
	 * 查询直属下级
	 */
	public List<Map<String, Object>> findGroup(Map<String, Object> map);

	/**
	 * 查询别表直属下级
	 */
	public List<Map<String, Object>> findAccount(Map<String, Object> map);

	/**
	 * 查询id
	 */
	public String selectId(String ids);

	/**
	 * 根据name获取科目id
	 */
	public Map<String, Object> selectName(String name);

	/**
	 * 科目下拉
	 */
	public List<Map<String, Object>> selectAccountName(Map<String, Object> params);

	/**
	 * 总分类账 科目下拉
	 */
	public List<Map<String, Object>> selectAccount(Map<String, Object> params);
	/**
	 * 凭证科目下拉
	 */
	public List<Map<String, Object>> selectAccountFv(Map<String, Object> params);
	/**
	 * 科目下拉
	 */
	public List<Map<String, Object>> selectAccountLevel();
}

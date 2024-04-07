package com.xibin.finance.dao;

import com.xibin.finance.pojo.FVoucher;
import org.apache.ibatis.annotations.Param;

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
public interface FVoucherDao {

	List<Map> queryForVoucher(Map map);

	Long queryForVoucherCount(Map map);


	/**
	 * 列表条件查询
	 */
	public List<Map<String, Object>> findFVoucherList(Map<String, Object> params);

	/**
	 * 凭证字类型
	 */
	public List<Map<String, Object>> findtranTypeList();

	/**
	 * 通过id查询
	 */
	public FVoucher getById(Object id);

	/**
	 * 查询第一个凭证字
	 */
	public String findtranTypeMin();

	/**
	 * 通过fperiod查询
	 */
	public Map<String, Object> getByFperiod(@Param("fperiod") Object fperiod, @Param("fyear") Object fyear);

	/**
	 * 删除
	 */
	public int deleteFVoucherByIds(String[] ids);

	/**
	 * 修改
	 */
	public int updateFVoucher(FVoucher fVoucher);

	/**
	 * 添加
	 */
	public int saveFVoucher(FVoucher fVoucher);

	/**
	 * 成本结转修改订单
	 */
	public int updateOrders(Map<String, Object> params);
	/**
	 *凭证号是否存在
	 */
	public Long fVoucherIsNumber(Map<String, Object> params);
}

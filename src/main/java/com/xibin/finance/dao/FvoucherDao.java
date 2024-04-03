package com.xibin.finance.dao;

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
public interface FvoucherDao {

	List<Map> queryForVoucher(Map map);

	Long queryForVoucherCount(Map map);
}

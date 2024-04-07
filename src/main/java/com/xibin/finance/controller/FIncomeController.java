package com.xibin.finance.controller;

import com.xibin.core.pojo.Message;
import com.xibin.finance.service.FIncomeService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 收入结转action
 * 
 * @copyright 大连骏骁网络科技有限公司
 * @author 骏骁(cxmail@qq.com)
 * @createDate 2019年04月23日
 * @version: V1.0.0
 */
@Controller
@RequestMapping(value = "/income", produces = {"application/json;charset=UTF-8"})
public class FIncomeController{

	@Resource
	private FIncomeService fIncomeService;




	@RequestMapping("/findFIncome")
	@ResponseBody
	public List<Map<String, Object>>  findFIncome(@RequestParam Map<String, Object> params) {
		List<Map<String, Object>> list = fIncomeService.findFIncomeList(params);
		return list;
	}
	/**
	 * 收入结转
	 */
	@RequestMapping("/findtranTypeAddVoucheerList")
	@ResponseBody
	public Map<String, Object> findtranTypeAddVoucheerList(@RequestParam Map<String, Object> params) {
		return fIncomeService.findtranTypeAddVoucheerList(params);
	}


	/**
	 * 反结转
	 */
	@RequestMapping("/fanVoucheer")
	@ResponseBody
	public Message fanVoucheer(@RequestParam Map<String, Object> params) {
		Message msg = new Message();
		fIncomeService.fanVoucheer(params);
		msg.setMsg("操作成功！");
		msg.setCode(200);
		return msg;
	}
}
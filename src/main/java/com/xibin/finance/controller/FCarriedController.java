package com.xibin.finance.controller;

import com.xibin.core.pojo.Message;
import com.xibin.finance.service.FCarriedService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 费用摊销action
 * 
 * @copyright 大连骏骁网络科技有限公司
 * @author 骏骁(cxmail@qq.com)
 * @createDate 2019年04月23日
 * @version: V1.0.0
 */
@Controller
@RequestMapping(value = "/carried", produces = {"application/json;charset=UTF-8"})
public class FCarriedController {

	@Resource
	private FCarriedService fCarriedService;


	/**
	 * 分页查询
	 */
	@RequestMapping("/findFCarried")
	@ResponseBody
	public List<Map<String, Object>> findFCarried(@RequestParam Map<String, Object> params) {
		List<Map<String, Object>> list = fCarriedService.findFCarriedList(params);
		return list;
	}

	/**
	 * 成本结转
	 */
	@RequestMapping("/findtranTypeAddVoucheerList")
	@ResponseBody
	public Map<String, Object> findtranTypeAddVoucheerList(@RequestParam Map<String, Object> params) {
		return fCarriedService.findtranTypeAddVoucheerList(params);
	}

	/**
	 * 成本反结转
	 */
	@RequestMapping("/fanVoucheer")
	@ResponseBody
	public Message fanVoucheer(@RequestParam Map<String, Object> params) {
		Message msg = new Message();
		fCarriedService.fanVoucheer(params);
		msg.setMsg("操作成功！");
		msg.setCode(200);
		return msg;
	}
}
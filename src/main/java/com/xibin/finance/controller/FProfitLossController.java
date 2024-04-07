package com.xibin.finance.controller;

import com.xibin.core.pojo.Message;
import com.xibin.finance.service.FProfitLossService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 结转损益action
 * 
 * @copyright 大连骏骁网络科技有限公司
 * @author 骏骁(cxmail@qq.com)
 * @createDate 2019年04月23日
 * @version: V1.0.0
 */
@Controller
@RequestMapping(value = "/profitLoss", produces = {"application/json;charset=UTF-8"})
public class FProfitLossController{

	@Resource
	private FProfitLossService fProfitLossService;


	/**
	 * 分页查询
	 */
	@RequestMapping("/findFProfitLoss")
	@ResponseBody
	public List<Map<String, Object>> findFProfitLoss(@RequestParam Map<String, Object> params) {
        List<Map<String, Object>> list = fProfitLossService.findFProfitLossList(params);
		return list;
	}

	/**
	 * 结转
	 */
	@RequestMapping("/updateFProfitLoss")
	@ResponseBody
	public Message updateFProfitLoss(@RequestParam Map<String, Object> params) {
		/*
		 * params.put("fyear", params.get("ymDate").toString().substring(0,4));
		 * params.put("fperiod", params.get("ymDate").toString().substring(5,7));
		 */
		return fProfitLossService.findtranTypeAddVoucheerList(params);
	}
	
	/**
	 * 反结转
	 */
	@RequestMapping("/fanVoucheer")
	@ResponseBody
	public Message fanVoucheer(@RequestParam Map<String, Object> params) {
		Message msg = new Message();
		fProfitLossService.fanVoucheer(params);
		msg.setCode(200);
		msg.setMsg("操作成功！");
        return msg;
	}
}
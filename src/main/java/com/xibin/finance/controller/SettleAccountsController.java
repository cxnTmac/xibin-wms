package com.xibin.finance.controller;

import com.xibin.core.pojo.Message;
import com.xibin.finance.service.SettleAccountsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 结账action
 * 
 * @copyright 大连骏骁网络科技有限公司
 * @author 骏骁(cxmail@qq.com)
 * @createDate 2019年10月09日
 * @version: V1.0.0
 */
@Controller
@RequestMapping(value = "/settleAccounts", produces = {"application/json;charset=UTF-8"})
public class SettleAccountsController{

	@Resource
	private SettleAccountsService settleAccountsService;


	/**
	 * 分页查询
	 */
	@RequestMapping("/findSettleAccounts")
	@ResponseBody
	public List<Map<String , Object>> findSettleAccounts(@RequestParam Map<String, Object> params) {
        List<Map<String , Object>> list = settleAccountsService.findSettleAccountsList(params);
		return list;
	}
	/**
	 * 查询是否结账
	 */
	@RequestMapping("/findIsJzList")
	@ResponseBody
	public Message findIsJzList(String fdate) {
		return settleAccountsService.findIsJzList(fdate);
	}
	/**
	 * 删除
	 */
	@RequestMapping("/deleteSettleAccounts")
	@ResponseBody
	public Message deleteSettleAccounts(String id, String fyear, String fperiod) {
		Message msg = new Message();
		settleAccountsService.deleteSettleAccountsByIds(id,fyear,fperiod);
		msg.setCode(200);
		msg.setMsg("操作成功！");
        return msg;
	}


	/**
	 * 添加
	 */
	@RequestMapping("/saveSettleAccounts")
	@ResponseBody
	public Message saveSettleAccounts(String ymDate) {
        return settleAccountsService.saveSettleAccounts(ymDate);
	}
}
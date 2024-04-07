package com.xibin.finance.controller;

import com.alibaba.fastjson.JSON;
import com.xibin.core.pojo.Message;
import com.xibin.finance.pojo.FVoucher;
import com.xibin.finance.service.FVoucherService;
import com.xibin.wms.constants.WmsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/voucher", produces = {"application/json;charset=UTF-8"})
public class FVoucherController {
	@Resource
	private FVoucherService fVoucherService;

	@Autowired  
	private HttpSession session;

	@RequestMapping("/saveFVoucher")
	@ResponseBody
	public Message saveFVoucher(@RequestParam("fVoucher") String fVoucherStr, @RequestParam("cardData") String cardData) {
		FVoucher fVoucher = JSON.parseObject(fVoucherStr, FVoucher.class);
		fVoucher.setChecked(2);
		fVoucher.setPosted(2);
		// 暂时处理
		fVoucher.setPreparerID(1);
		fVoucher.setPosterID(1);
		fVoucher.setCashierID(1);
		return fVoucherService.saveFVoucher(fVoucher,cardData);
	}
	@RequestMapping("/updateFVoucher")
	@ResponseBody
	public Message updateFVoucher(@RequestParam("fVoucher") String fVoucherStr, @RequestParam("cardData") String cardData) {
		FVoucher fVoucher = JSON.parseObject(fVoucherStr, FVoucher.class);
		if (fVoucher.getChecked() != null) {
			fVoucher.setCheckerID(1);
		}
		return fVoucherService.updateFVoucher(fVoucher, cardData);
	}
	@RequestMapping("/deleteFVoucher")
	@ResponseBody
	public Message deleteFVoucher(String ids) {
		Message msg = new Message();
		fVoucherService.deleteFVoucherByIds(ids);
		msg.setCode(200);
		msg.setMsg("操作成功！");
		return msg;
	}
	/**
	 * 分页查询
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public FVoucher getById(@RequestParam("voucherID") String voucherID) {
		return fVoucherService.getById(Long.valueOf(voucherID));
	}
	/**
	 * 列表查询
	 */
	@RequestMapping("/findFVoucherentryList1")
	@ResponseBody
	public List<Map<String, Object>> findFVoucherentryList1(@RequestParam Map<String, Object> params) {
		return fVoucherService.findFVoucherentryList1(params);
	}


	/**
	 * 批量审批
	 */
	@RequestMapping("/updateChecked")
	@ResponseBody
	public Message updateChecked(@RequestParam Map<String, Object> params) {
		Message msg = new Message();
		params.put("checkerID", 1);
		fVoucherService.updateChecked(params);
		msg.setCode(200);
		msg.setMsg("操作成功！");
		return msg;
	}

	/**
	 * 批量反审批
	 */
	@RequestMapping("/updateBackChecked")
	@ResponseBody
	public Message updateBackChecked(@RequestParam Map<String, Object> params) {
		Message msg = new Message();
		fVoucherService.updateBackChecked(params);
		msg.setCode(200);
		msg.setMsg("操作成功！");
		return msg;
	}

	/**
	 * 批量过账
	 */
	@RequestMapping("/updatePosted")
	@ResponseBody
	public Message updatePosted(@RequestParam Map<String, Object> params) {
		Message msg = new Message();
		fVoucherService.updatePosted(params);
		msg.setCode(200);
		msg.setMsg("操作成功！");
		return msg;
	}

	/**
	 * 收入结转添加
	 */
	@RequestMapping("/saveFIncome")
	@ResponseBody
	public Message saveFIncome(@RequestParam("fVoucher") String fVoucherStr, @RequestParam("cardData") String cardData,@RequestParam("fVoucherType")String fVoucherType,@RequestParam("ids") List<String>  ids) {
		Message msg = new Message();
		FVoucher fVoucher = JSON.parseObject(fVoucherStr, FVoucher.class);
		fVoucher.setChecked(2);
		fVoucher.setPosted(2);
		fVoucher.setPreparerID(WmsConstants.DEFAULT_FINANCE_USER_ID);
		fVoucher.setPosterID(WmsConstants.DEFAULT_FINANCE_USER_ID);
		fVoucher.setCashierID(WmsConstants.DEFAULT_FINANCE_USER_ID);
		fVoucherService.saveFIncome(fVoucher, cardData, fVoucherType,ids);
		msg.setMsg("操作成功！");
		msg.setCode(200);
		return msg;
	}
}

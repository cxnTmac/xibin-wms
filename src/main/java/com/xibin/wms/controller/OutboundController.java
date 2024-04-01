package com.xibin.wms.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.page.pojo.Page;
import com.xibin.core.page.pojo.PageEntity;
import com.xibin.core.pojo.Message;
import com.xibin.core.security.pojo.MyUserDetails;
import com.xibin.core.security.util.SecurityUtil;
import com.xibin.wms.constants.WmsCodeMaster;
import com.xibin.wms.pojo.WmOutboundAlloc;
import com.xibin.wms.pojo.WmOutboundDetail;
import com.xibin.wms.pojo.WmOutboundHeader;
import com.xibin.wms.query.*;
import com.xibin.wms.service.WmOutboundAllocService;
import com.xibin.wms.service.WmOutboundDetailService;
import com.xibin.wms.service.WmOutboundHeaderService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/outbound", produces = { "application/json;charset=UTF-8" })
public class OutboundController {
	@Value("${file.webPicUploadUrl}")
	private String webPicUploadUrl;
	@Autowired
	private HttpSession session;
	@Resource
	private WmOutboundHeaderService outboundHeaderService;
	@Resource
	private WmOutboundDetailService outboundDetailService;
	@Resource
	private WmOutboundAllocService outboundAllocService;

	@RequestMapping("/showAllOutboundOrder")
	@ResponseBody
	public PageEntity<WmOutboundHeaderQueryItem> showAllOutboundOrder(HttpServletRequest request, Model model) {
		MyUserDetails userDetails = SecurityUtil.getMyUserDetails();
		// 开始分页
		PageEntity<WmOutboundHeaderQueryItem> pageEntity = new PageEntity<WmOutboundHeaderQueryItem>();
		Page<?> page = new Page();
		Map map = new HashMap<>();
		if (request.getParameter("page") != null && request.getParameter("size") != null) {
			// 配置分页参数
			page.setPageNo(Integer.parseInt(request.getParameter("page")));
			page.setPageSize(Integer.parseInt(request.getParameter("size")));
			map = JSONObject.parseObject(request.getParameter("conditions"));
			map.put("page", page);
		} else {
			map = JSONObject.parseObject(request.getParameter("conditions"));
		}
		if (userDetails != null) {
			map.put("companyId", userDetails.getCompanyId());
			map.put("warehouseId", userDetails.getWarehouseId());
		}
		List<WmOutboundHeaderQueryItem> list = outboundHeaderService.getAllOutboundOrderByPage(map);
		pageEntity.setList(list);
		pageEntity.setSize(page.getTotalRecord());
		return pageEntity;
	}

	@RequestMapping("/showAllOutboundDetail")
	@ResponseBody
	public PageEntity<WmOutboundDetailQueryItem> showAllOutboundDetail(HttpServletRequest request, Model model) {
		// 开始分页
		MyUserDetails userDetails = SecurityUtil.getMyUserDetails();
		PageEntity<WmOutboundDetailQueryItem> pageEntity = new PageEntity<WmOutboundDetailQueryItem>();
		Page<?> page = new Page();
		// 配置分页参数
		page.setPageNo(Integer.parseInt(request.getParameter("page")));
		page.setPageSize(Integer.parseInt(request.getParameter("size")));
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		map.put("page", page);
		if (userDetails != null) {
			map.put("companyId", userDetails.getCompanyId());
			map.put("warehouseId", userDetails.getWarehouseId());
		}
		List<WmOutboundDetailQueryItem> list = outboundDetailService.getAllOutboundDetailByPage(map);
		pageEntity.setList(list);
		pageEntity.setSize(page.getTotalRecord());
		return pageEntity;
	}

	@RequestMapping("/showAllClosedOrderOutboundDetail")
	@ResponseBody
	public List<WmOutboundDetailQueryItem> showAllClosedOrderOutboundDetail(HttpServletRequest request, Model model) {
		// 开始分页
		MyUserDetails userDetails = SecurityUtil.getMyUserDetails();
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		if (userDetails != null) {
			map.put("companyId", userDetails.getCompanyId());
			map.put("warehouseId", userDetails.getWarehouseId());
		}
		return outboundDetailService.selectClosedOrderDetail(map);

	}

	@RequestMapping("/selectForMobileAlloc")
	@ResponseBody
	public List<WmOutboundAllocQueryItem> selectForMobileAlloc(HttpServletRequest request, Model model) {
		// 开始分页
		MyUserDetails userDetails = SecurityUtil.getMyUserDetails();
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		if (userDetails != null) {
			map.put("companyId", userDetails.getCompanyId());
			map.put("warehouseId", userDetails.getWarehouseId());
		}
		return outboundAllocService.selectForMobileAlloc(map);
	}
	@RequestMapping("/showAllOutboundAlloc")
	@ResponseBody
	public PageEntity<WmOutboundAllocQueryItem> showAllOutboundAlloc(HttpServletRequest request, Model model) {
		// 开始分页
		MyUserDetails userDetails = SecurityUtil.getMyUserDetails();
		PageEntity<WmOutboundAllocQueryItem> pageEntity = new PageEntity<WmOutboundAllocQueryItem>();
		Page<?> page = new Page();
		// 配置分页参数
		page.setPageNo(Integer.parseInt(request.getParameter("page")));
		page.setPageSize(Integer.parseInt(request.getParameter("size")));
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		map.put("page", page);
		if (userDetails != null) {
			map.put("companyId", userDetails.getCompanyId());
			map.put("warehouseId", userDetails.getWarehouseId());
		}
		List<WmOutboundAllocQueryItem> list = outboundAllocService.getAllOutboundAllocByPage(map);
		pageEntity.setList(list);
		pageEntity.setSize(page.getTotalRecord());
		return pageEntity;
	}

	@RequestMapping("/saveOutboundOrder")
	@ResponseBody
	public Message saveOutboundOrder(HttpServletRequest request, Model model) {
		Message message = new Message();
		String str = request.getParameter("order");
		WmOutboundHeader bean = JSON.parseObject(str, WmOutboundHeader.class);
		try {
			WmOutboundHeaderQueryItem queryItem = this.outboundHeaderService.saveOutbound(bean);
			message.setCode(200);
			message.setData(queryItem);
			message.setMsg("操作成功！");

		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
		}
		return message;
	}

	@RequestMapping("/remove")
	@ResponseBody
	public Message remove(@RequestParam("orderNos") String[] orderNos) {
		Message message = new Message();
		List<String> errors = new ArrayList<String>();
		try {
			for (String orderNo : orderNos) {
				outboundHeaderService.remove(orderNo);
			}
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errors.add(e.getMessage());
		}
		if (errors.size() == 0) {
			message.setCode(200);
			message.setMsg("操作成功！");
		} else {
			message.setCode(100);
			message.setMsgs(errors);
		}
		message.converMsgsToMsg("</br>");
		return message;
	}

	@RequestMapping("/removeOutboundDetail")
	@ResponseBody
	public Message removeOutboundDetail(@RequestParam("ids") String idsStr, @RequestParam("orderNo") String orderNo) {
		Message message = new Message();
		String[] idsStrs = idsStr.split(",");
		if (idsStrs.length == 0) {
			message.setCode(0);
			message.setMsg("参数有误，请联系管理员");
			return message;
		}
		int[] ids = new int[idsStrs.length];
		for (int i = 0; i < idsStrs.length; i++) {
			ids[i] = Integer.parseInt(idsStrs[i]);
		}
		try {
			return this.outboundDetailService.removeOutboundDetail(ids, orderNo);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			message.setCode(0);
			message.setMsg(e.getMessage());
			return message;
		}
	}

	@RequestMapping("/removeOutboundDetailAndCreateNewOrder")
	@ResponseBody
	public Message removeOutboundDetailAndCreateNewOrder(@RequestParam("ids") String idsStr,
			@RequestParam("orderNo") String orderNo) {
		Message message = new Message();
		String[] idsStrs = idsStr.split(",");
		if (idsStrs.length == 0) {
			message.setCode(0);
			message.setMsg("参数有误，请联系管理员");
			return message;
		}
		int[] ids = new int[idsStrs.length];
		for (int i = 0; i < idsStrs.length; i++) {
			ids[i] = Integer.parseInt(idsStrs[i]);
		}
		try {
			return this.outboundDetailService.removeOutboundDetailAndCreateNewOrder(ids, orderNo);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			message.setCode(0);
			message.setMsg(e.getMessage());
			return message;
		}
	}

	@RequestMapping("/saveOutboundDetail")
	@ResponseBody
	public Message saveOutboundDetail(HttpServletRequest request, Model model) {
		Message message = new Message();
		String str = request.getParameter("detail");

		WmOutboundDetail bean = JSON.parseObject(str, WmOutboundDetail.class);
		try {
			WmOutboundDetailQueryItem item = this.outboundDetailService.saveOutboundDetail(bean);
			message.setData(item);
			message.setCode(200);
			message.setMsg("操作成功！");
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
		}
		return message;
	}

	// 只用于更新分配明细的成本
	@RequestMapping("/saveOutboundDetailAlloc")
	@ResponseBody
	public Message saveOutboundDetailAlloc(HttpServletRequest request, Model model) {
		Message message = new Message();
		String str = request.getParameter("detailAlloc");

		WmOutboundAlloc bean = JSON.parseObject(str, WmOutboundAlloc.class);
		try {
			WmOutboundAlloc item = this.outboundAllocService.saveOutboundAllocForEditCost(bean);
			message.setData(item);
			message.setCode(200);
			message.setMsg("操作成功！");
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
		}
		return message;
	}

	@RequestMapping("/close")
	@ResponseBody
	public Message audit(@RequestParam("orderNos") String orderNos) {
		Message message = new Message();
		String[] orderArray = orderNos.split(",");
		List<String> errors = new ArrayList<String>();
		WmOutboundHeaderQueryItem queryItem = new WmOutboundHeaderQueryItem();
		try {
			for (String orderNo : orderArray) {
				queryItem = outboundHeaderService.close(orderNo);
			}
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errors.add(e.getMessage());
		}
		if (errors.size() == 0) {
			message.setCode(200);
			message.setMsg("操作成功！");
			if (orderArray.length == 1) {
				message.setData(queryItem);
			}
		} else {
			message.setCode(100);
			message.setMsgs(errors);
		}
		message.converMsgsToMsg("</br>");
		return message;
	}

	private boolean hasSkuCodeInDetails(List<WmOutboundDetail> details, String skuCode) {
		for (WmOutboundDetail detail : details) {
			if (detail.getSkuCode().equals(skuCode)) {
				return true;
			}
		}
		return false;
	}

	@RequestMapping("/batchSaveOutboundDetail")
	@ResponseBody
	public Message batchSaveOutboundDetail(@RequestParam("skuCodes") String skuCodes,
			@RequestParam("prices") String prices, @RequestParam("orderNo") String orderNo,
			@RequestParam("buyerCode") String buyerCode) {
		Message message = new Message();
		String[] skuArray = skuCodes.split(",");
		String[] priceArray = prices.split(",");
		MyUserDetails userDetails = SecurityUtil.getMyUserDetails();
		WmOutboundDetail queryExample = new WmOutboundDetail();
		queryExample.setOrderNo(orderNo);
		queryExample.setCompanyId(userDetails.getCompanyId());
		queryExample.setWarehouseId(userDetails.getWarehouseId());
		List<WmOutboundDetail> details = this.outboundDetailService.selectByExample(queryExample);
		List<String> errors = new ArrayList<String>();
		try {
			for (int i = 0; i < skuArray.length; i++) {
				if (!hasSkuCodeInDetails(details, skuArray[i])) {
					WmOutboundDetail bean = new WmOutboundDetail();
					bean.setOrderNo(orderNo);
					bean.setBuyerCode(buyerCode);
					bean.setOutboundAllocNum(0.0);
					bean.setOutboundNum(0.0);
					bean.setOutboundPickNum(0.0);
					bean.setOutboundShipNum(0.0);
					bean.setCost(0.0);
					bean.setOutboundPrice(Double.parseDouble(priceArray[i]));
					bean.setPlanShipLoc("SORTATION");
					bean.setStatus(WmsCodeMaster.SO_NEW.getCode());
					bean.setSkuCode(skuArray[i]);
					this.outboundDetailService.saveOutboundDetail(bean);
				} else {
					errors.add("已存在产品编码[" + skuArray[i] + "]的出库明细");
				}
			}
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errors.add(e.getMessage());
		}
		if (errors.size() > 0) {
			message.setMsgs(errors);
			message.setCode(100);
			message.converMsgsToMsg("");
		} else {
			message.setCode(200);
			message.setMsg("操作成功！");
		}
		return message;
	}

	@RequestMapping("/getOutboundHeaderByOderNo")
	@ResponseBody
	public WmOutboundHeaderQueryItem getOutboundHeaderByOderNo(HttpServletRequest request, Model model) {
		String orderNo = request.getParameter("orderNo");
		List<WmOutboundHeaderQueryItem> list = outboundHeaderService.selectByKey(orderNo);
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return new WmOutboundHeaderQueryItem();
	}

	@RequestMapping("/allocByOrderNo")
	@ResponseBody
	public Message allocByOrderNo(@RequestParam("orderNo") String orderNo) {
		return outboundDetailService.allocByOrderNo(orderNo);
	}

	@RequestMapping("/cancelAllocByOrderNo")
	@ResponseBody
	public Message cancelAllocByOrderNo(@RequestParam("orderNo") String orderNo) {
		return outboundDetailService.cancelAllocByOrderNo(orderNo);
	}

	@RequestMapping("/createAssembleByDetails")
	@ResponseBody
	public Message createAssembleByDetails(@RequestParam("orderNo") String orderNo,
			@RequestParam("lineNos") String lineNos) {
		Message message = new Message();
		String[] lineNoArray = lineNos.split(",");
		try {
			message = outboundDetailService.createAssembleByDetails(orderNo, lineNoArray);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
		}
		return message;
	}

	@RequestMapping("/createCrossDockInboundByDetails")
	@ResponseBody
	public Message createCrossDockInboundByDetails(@RequestParam("supplierCode") String supplierCode,
			@RequestParam("orderNo") String orderNo, @RequestParam("lineNos") String lineNos) {
		Message message = new Message();
		String[] lineNoArray = lineNos.split(",");
		try {
			message = outboundDetailService.createCrossDockInboundByDetails(supplierCode, orderNo, lineNoArray);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
		}
		return message;
	}

	@RequestMapping("/alloc")
	@ResponseBody
	public Message alloc(@RequestParam("orderNo") String orderNo, @RequestParam("lineNos") String[] lineNos,
						 @RequestParam("type") String type) {
		Message message = new Message();
		List<String> errors = new ArrayList<String>();
		for (String lineNo : lineNos) {
			try {
				Message singleMessage = outboundDetailService.allocByKey(orderNo, lineNo, type);
				if (singleMessage.getCode() != 200) {
					errors.add(singleMessage.getMsg());
				}
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				errors.add(e.getMessage());
			}
		}
		if (errors.size() == 0) {
			message.setCode(200);
			message.setMsg("操作成功");
		} else {
			message.setCode(100);
			message.setMsgs(errors);
			message.converMsgsToMsg("");
		}
		return message;
	}
	@RequestMapping("/virtualAlloc")
	@ResponseBody
	public Message virtualAlloc(@RequestParam("orderNo") String orderNo, @RequestParam("lineNo") String lineNo,
						 @RequestParam("type") String type) {
		Message message = new Message();
		List<String> errors = new ArrayList<String>();
			try {
				Message singleMessage = outboundDetailService.virtualAllocByKey(orderNo, lineNo, type);
				if (singleMessage.getCode() != 200) {
					errors.add(singleMessage.getMsg());
				}
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				errors.add(e.getMessage());
			}
		if (errors.size() == 0) {
			message.setCode(200);
			message.setMsg("操作成功");
		} else {
			message.setCode(100);
			message.setMsgs(errors);
			message.converMsgsToMsg("");
		}
		return message;
	}

	@RequestMapping("/cancelAlloc")
	@ResponseBody
	public Message cancelAlloc(@RequestParam("orderNo") String orderNo, @RequestParam("lineNos") String[] lineNos) {
		Message message = new Message();
		List<String> errors = new ArrayList<String>();
		for (String lineNo : lineNos) {
			try {
				Message singleMessage = outboundDetailService.cancelAllocByKey(orderNo, lineNo);
				if (singleMessage.getCode() != 200) {
					errors.add(singleMessage.getMsg());
				}
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				errors.add(e.getMessage());
			}
		}
		if (errors.size() == 0) {
			message.setCode(200);
			message.setMsg("操作成功");
		} else {
			message.setCode(100);
			message.setMsgs(errors);
			message.converMsgsToMsg("");
		}
		return message;
	}

	@RequestMapping("/audit")
	@ResponseBody
	public Message audit(@RequestParam("orderNos") String[] orderNos) {
		Message message = new Message();
		List<String> errors = new ArrayList<String>();
		WmOutboundHeaderQueryItem queryItem = new WmOutboundHeaderQueryItem();
		try {
			for (String orderNo : orderNos) {
				queryItem = outboundHeaderService.audit(orderNo);
			}
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errors.add(e.getMessage());
		}
		if (errors.size() == 0) {
			message.setCode(200);
			message.setMsg("操作成功！");
			if (orderNos.length == 1) {
				message.setData(queryItem);
			}
		} else {
			message.setCode(100);
			message.setMsgs(errors);
		}
		message.converMsgsToMsg("</br>");
		return message;
	}

	@RequestMapping("/cancelAudit")
	@ResponseBody
	public Message cancelAudit(@RequestParam("orderNos") String[] orderNos) {
		Message message = new Message();
		List<String> errors = new ArrayList<String>();
		WmOutboundHeaderQueryItem queryItem = new WmOutboundHeaderQueryItem();
		try {
			for (String orderNo : orderNos) {
				queryItem = outboundHeaderService.cancelAudit(orderNo);
			}
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errors.add(e.getMessage());
		}
		if (errors.size() == 0) {
			message.setCode(200);
			message.setMsg("操作成功！");
			if (orderNos.length == 1) {
				message.setData(queryItem);
			}
		} else {
			message.setCode(100);
			message.setMsgs(errors);
		}
		message.converMsgsToMsg("</br>");
		return message;
	}
	@RequestMapping("/packByAlloc")
	@ResponseBody
	public Message packByAlloc(HttpServletRequest request, Model model) {
		Message message = new Message();
		String str = request.getParameter("alloc");
		WmOutboundAlloc bean = JSON.parseObject(str, WmOutboundAlloc.class);
		try {
			this.outboundAllocService.packByAlloc(bean);
			message.setCode(200);
			message.setMsg("操作成功！");
		} catch (BusinessException e) {
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
		}
		return message;
	}
	@RequestMapping("/cancelPackByAlloc")
	@ResponseBody
	public Message cancelPackByAlloc(HttpServletRequest request, Model model) {
		Message message = new Message();
		String str = request.getParameter("alloc");
		WmOutboundAlloc bean = JSON.parseObject(str, WmOutboundAlloc.class);
		try {
			this.outboundAllocService.cancelPackByAlloc(bean);
			message.setCode(200);
			message.setMsg("操作成功！");
		} catch (BusinessException e) {
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
		}
		return message;
	}
	@RequestMapping("/pickByAlloc")
	@ResponseBody
	public Message pickByAlloc(HttpServletRequest request, Model model) {
		Message message = new Message();
		String str = request.getParameter("alloc");
		String pickNumStr = request.getParameter("pickNum");
		String isPackedSku = request.getParameter("isPackedSku");
		String packageNum = request.getParameter("packageNum");
		double pickNum = Double.parseDouble(pickNumStr);
		WmOutboundAlloc bean = JSON.parseObject(str, WmOutboundAlloc.class);
		try {
			if(isPackedSku==null||isPackedSku.equals("")||isPackedSku.equals("true")){
				if(packageNum==null||packageNum.equals("")){
					this.outboundAllocService.pickByAlloc(bean, pickNum,true,0);
				}else{
					this.outboundAllocService.pickByAlloc(bean, pickNum,true,Integer.parseInt(packageNum));
				}
			}else{
				this.outboundAllocService.pickByAlloc(bean, pickNum,false,0);
			}
			message.setCode(200);
			message.setMsg("操作成功！");
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
		}
		return message;
	}
	@RequestMapping("/reAllocByAllocId")
	@ResponseBody
	public Message reAllocByAllocId(HttpServletRequest request, Model model) {
		Message message = new Message();
		String str = request.getParameter("id");
		int allocId = Integer.parseInt(str);
		try {
			this.outboundAllocService.reAllocByAllocId(allocId);
			message.setCode(200);
			message.setMsg("操作成功！");
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
		}
		return message;
	}
	@RequestMapping("/cancelPickByAlloc")
	@ResponseBody
	public Message cancelPickByAlloc(HttpServletRequest request, Model model) {
		Message message = new Message();
		String str = request.getParameter("alloc");
		WmOutboundAlloc bean = JSON.parseObject(str, WmOutboundAlloc.class);
		try {
			this.outboundAllocService.cancelPickByAlloc(bean);
			message.setCode(200);
			message.setMsg("操作成功！");
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
		}
		return message;
	}

	@RequestMapping("/shipByAlloc")
	@ResponseBody
	public Message shipByAlloc(HttpServletRequest request, Model model) {
		Message message = new Message();
		String str = request.getParameter("alloc");
		WmOutboundAlloc bean = JSON.parseObject(str, WmOutboundAlloc.class);
		try {
			this.outboundAllocService.shipByAlloc(bean, true);
			message.setCode(200);
			message.setMsg("操作成功！");
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
		}
		return message;
	}

	@RequestMapping("/cancelShipByAlloc")
	@ResponseBody
	public Message cancelShipByAlloc(HttpServletRequest request, Model model) {
		Message message = new Message();
		String str = request.getParameter("alloc");
		WmOutboundAlloc bean = JSON.parseObject(str, WmOutboundAlloc.class);
		try {
			this.outboundAllocService.cancelShipByAlloc(bean, true);
			message.setCode(200);
			message.setMsg("操作成功！");
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
		}
		return message;
	}

	@RequestMapping("/shipByHeader")
	@ResponseBody
	public Message shipByHeader(HttpServletRequest request, Model model) {
		Message message = new Message();
		String orderNo = request.getParameter("orderNo");
		try {
			message = this.outboundAllocService.shipByHeader(orderNo);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
			return message;
		}
		return message;
	}

	@RequestMapping("/cancelShipByHeader")
	@ResponseBody
	public Message cancelShipByHeader(HttpServletRequest request, Model model) {
		Message message = new Message();
		String orderNo = request.getParameter("orderNo");
		try {
			message = this.outboundAllocService.cancelShipByHeader(orderNo);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
			return message;
		}
		return message;
	}

	@RequestMapping("/pickByHeader")
	@ResponseBody
	public Message pickByHeader(HttpServletRequest request, Model model) {
		Message message = new Message();
		String orderNo = request.getParameter("orderNo");
		try {
			message = this.outboundAllocService.pickByOrderNo(orderNo);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
			return message;
		}
		return message;
	}
	@RequestMapping("/accountByOrderNo")
	@ResponseBody
	public Message accountByOrderNo(HttpServletRequest request, Model model) {
		Message message = new Message();
		String orderNo = request.getParameter("orderNo");
		WmOutboundHeaderQueryItem queryItem = new WmOutboundHeaderQueryItem();
		try {
			queryItem = this.outboundHeaderService.account(orderNo);
			message.setCode(200);
			message.setMsg("操作成功，已生成客户往来账");
			message.setData(queryItem);
			return message;
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
			message.setData(queryItem);
			return message;
		}
	}

	@RequestMapping("/cancelAccountByOrderNo")
	@ResponseBody
	public Message cancelAccountByOrderNo(HttpServletRequest request, Model model) {
		Message message = new Message();
		String orderNo = request.getParameter("orderNo");
		WmOutboundHeaderQueryItem queryItem = new WmOutboundHeaderQueryItem();
		try {
			queryItem = this.outboundHeaderService.cancelAccount(orderNo);
			message.setCode(200);
			message.setMsg("操作成功，已删除客户往来账");
			message.setData(queryItem);
			return message;
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
			message.setData(queryItem);
			return message;
		}
	}
	@RequestMapping("/queryHistoryPrice")
	@ResponseBody
	public PageEntity<WmOutboundDetailPriceQueryItem> queryHistoryPrice(HttpServletRequest request, Model model) {
		// 开始分页
		PageEntity<WmOutboundDetailPriceQueryItem> pageEntity = new PageEntity<WmOutboundDetailPriceQueryItem>();
		Page<?> page = new Page();
		// 配置分页参数
		page.setPageNo(Integer.parseInt(request.getParameter("page")));
		page.setPageSize(Integer.parseInt(request.getParameter("size")));
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		map.put("page", page);
		List<WmOutboundDetailPriceQueryItem> list = outboundDetailService.queryHistoryPrice(map);
		pageEntity.setList(list);
		pageEntity.setSize(page.getTotalRecord());
		return pageEntity;
	}

	@RequestMapping("/queryHistorySale")
	@ResponseBody
	public PageEntity<WmOutboundDetailSaleHistoryQueryItem> queryHistorySale(HttpServletRequest request, Model model) {
		// 开始分页
		PageEntity<WmOutboundDetailSaleHistoryQueryItem> pageEntity = new PageEntity<WmOutboundDetailSaleHistoryQueryItem>();
		Page<?> page = new Page();
		// 配置分页参数
		page.setPageNo(Integer.parseInt(request.getParameter("page")));
		page.setPageSize(Integer.parseInt(request.getParameter("size")));
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		map.put("page", page);
		List<WmOutboundDetailSaleHistoryQueryItem> list = outboundDetailService.queryHistorySale(map);
		pageEntity.setList(list);
		pageEntity.setSize(page.getTotalRecord());
		return pageEntity;
	}


	@RequestMapping(value = "/uploadOrderPic", consumes = "multipart/form-data", method = RequestMethod.POST)
	@ResponseBody
	public Message uploadOrderPic(HttpServletRequest request, @RequestParam("file") MultipartFile pic) {
		Message message = new Message();
		// String realPath =
		// request.getSession().getServletContext().getRealPath("/WEB-INF/upload/FittingSkuPic/"+fittingSkuCode+"-"+userDetails.getCompanyId());
		String realPath = webPicUploadUrl + "/outboundOrderPic/";
		System.out.println("图片上传路径：" + realPath);
		try {
			String fileName = System.currentTimeMillis() + "";
			File originFile = new File(realPath, fileName + ".jpg");
			FileUtils.copyInputStreamToFile(pic.getInputStream(), originFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg("文件[" + pic.getOriginalFilename() + "]上传失败!");
			return message;
		}
		message.setCode(200);
		return message;
	}

	@RequestMapping(value = "/importOutboundDetailByExcel", consumes = "multipart/form-data", method = RequestMethod.POST)
	@ResponseBody
	public Message importOutboundDetailByExcel(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
		String orderNo = request.getParameter("orderNo");
		String loc = request.getParameter("loc");
		String skuCodeColumnName = request.getParameter("skuCodeColumnName");
		String priceColumnName = request.getParameter("priceColumnName");
		String numColumnName = request.getParameter("numColumnName");
		String isQueryRecentPrice = request.getParameter("isQueryRecentPrice");
        String buyerCode = request.getParameter("buyerCode");
		try {
			return outboundDetailService.importByExcel(file, orderNo, loc, skuCodeColumnName, priceColumnName,
					numColumnName,isQueryRecentPrice,buyerCode);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Message message = new Message();
			message.setCode(0);
			message.setMsg(e.getMessage());
			return message;
		}
	}

	@RequestMapping("/queryWmOutboundDetailByPage")
	@ResponseBody
	public PageEntity<WmOutboundDetailSkuQueryItem> queryWmOutboundDetailByPage(@RequestParam("conditions") String conditions) {
		// 开始分页
		MyUserDetails userDetails = SecurityUtil.getMyUserDetails();
		PageEntity<WmOutboundDetailSkuQueryItem> pageEntity = new PageEntity<WmOutboundDetailSkuQueryItem>();
		Page<?> page = new Page();
		Map map = new HashMap<>();
		// 配置分页参数
		map = JSONObject.parseObject(conditions);
		if (userDetails != null) {
			map.put("companyId", userDetails.getCompanyId());
			map.put("warehouseId", userDetails.getWarehouseId());
		}
		List<WmOutboundDetailSkuQueryItem> list = outboundDetailService.queryWmOutboundDetailByPage(map);
		pageEntity.setList(list);
		pageEntity.setSize(page.getTotalRecord());
		return pageEntity;
	}

	@RequestMapping("/selectNextOrderNo")
	@ResponseBody
	public String selectNextOrderNo(@RequestParam("orderTime") String orderTime) {
		return outboundHeaderService.selectNextOrderNo(orderTime);
	}

	@RequestMapping("/selectPreOrderNo")
	@ResponseBody
	public String selectPreOrderNo(@RequestParam("orderTime") String orderTime) {
		return outboundHeaderService.selectPreOrderNo(orderTime);
	}

	@RequestMapping("/selectRecentOrderHeaderByBuyerCode")
	@ResponseBody
	public Map selectRecentOrderHeaderByBuyerCode(@RequestParam("buyerCode") String buyerCode) {
		return outboundHeaderService.selectRecentOrderHeaderByBuyerCode(buyerCode);
	}

	@RequestMapping("/queryForOutboundDaily")
	@ResponseBody
	public List<Map> queryForOutboundDaily(HttpServletRequest request, Model model) {
		MyUserDetails userDetails = SecurityUtil.getMyUserDetails();
		Map map = new HashMap<>();
		map = JSONObject.parseObject(request.getParameter("conditions"));
		if (userDetails != null) {
			map.put("companyId", userDetails.getCompanyId());
			map.put("warehouseId", userDetails.getWarehouseId());
		}
		List<Map> list = outboundHeaderService.queryForOutboundDaily(map);
		return list;
	}

	@RequestMapping("/getTotalPackageNumByOrderNo")
	@ResponseBody
	public Message getTotalPackageNumByOrderNo(HttpServletRequest request, Model model) {
		Message message = new Message();
		String orderNo = request.getParameter("orderNo");
		message.setData(this.outboundAllocService.getTotalPackageNumByOrderNo(orderNo));
		message.setCode(200);
		return message;

	}


	@RequestMapping("/selectForReAlloc")
	@ResponseBody
	public List<Map> selectForReAlloc(HttpServletRequest request, Model model) {
		// 开始分页
		MyUserDetails userDetails = SecurityUtil.getMyUserDetails();
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		if (userDetails != null) {
			map.put("companyId", userDetails.getCompanyId());
			map.put("warehouseId", userDetails.getWarehouseId());
		}
		return outboundAllocService.selectForReAlloc(map);
	}

	@RequestMapping("/mobileScanSaveOutboundDetail")
	@ResponseBody
	public Message mobileScanSaveOutboundDetail(HttpServletRequest request, Model model) {
		Message message = new Message();
		String orderNo = request.getParameter("orderNo");
		String skuCode = request.getParameter("skuCode");
		String outboundNumStr = request.getParameter("outboundNum");
		Double outboundNum = Double.parseDouble(outboundNumStr);
		try {
			return this.outboundDetailService.mobileScanSaveOutboundDetail(orderNo,skuCode,outboundNum);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
			return message;
		}
	}

	@RequestMapping("/updateOutboundDetailAndReAlloc")
	@ResponseBody
	public Message updateOutboundDetailAndReAlloc(HttpServletRequest request, Model model) {
		Message message = new Message();
		String str = request.getParameter("detail");
		String newOutboundNumStr = request.getParameter("newOutboundNum");
		double newOutboundNum = Double.parseDouble(newOutboundNumStr);
		WmOutboundDetail bean = JSON.parseObject(str, WmOutboundDetail.class);
		try {
			this.outboundDetailService.updateOutboundDetailAndReAlloc(bean,newOutboundNum);
			message.setCode(200);
			message.setMsg("操作成功！");
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
		}
		return message;
	}

	@RequestMapping("/updateOutboundDetailAndReAllocByLineNo")
	@ResponseBody
	public Message updateOutboundDetailAndReAllocByLineNo(HttpServletRequest request, Model model) {
		Message message = new Message();
		String orderNo = request.getParameter("orderNo");
		String lineNo = request.getParameter("lineNo");
		String newOutboundNumStr = request.getParameter("newOutboundNum");
		double newOutboundNum = Double.parseDouble(newOutboundNumStr);
		try {
			this.outboundDetailService.updateOutboundDetailAndReAlloc(orderNo,lineNo,newOutboundNum);
			message.setCode(200);
			message.setMsg("操作成功！");
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
		}
		return message;
	}
}

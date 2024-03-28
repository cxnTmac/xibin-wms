package com.xibin.wms.pojo;

import com.xibin.core.daosupport.BaseModel;

import java.util.Date;

public class WmOutboundDetail extends BaseModel {
	private Integer id;

	private String orderNo;

	private String lineNo;

	private String buyerCode;

	private String skuCode;

	private String genericSkuCode;

	private String status;

	private Double outboundNum;

	private Double outboundOriginNum;

	private Double outboundAllocNum;

	private Double outboundPickNum;

	private Double outboundShipNum;

	private Double outboundPrice;

	private Double cost;

	private String isCreateVoucher;

	private String voucherNo;

	private String isCreateCostVoucher;

	private String costVoucherNo;

	private String planShipLoc;

	private String isCrossDock;

	private String inboundOrderNo;

	private String inboundLineNo;

	private String remark;

	private Integer creator;

	private Date createTime;

	private Integer modifier;

	private Date modifyTime;

	private Integer recVer;

	private Integer companyId;

	private Integer warehouseId;

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getLineNo() {
		return lineNo;
	}

	public void setLineNo(String lineNo) {
		this.lineNo = lineNo;
	}

	public String getSkuCode() {
		return skuCode;
	}

	public void setSkuCode(String skuCode) {
		this.skuCode = skuCode;
	}

	public Double getOutboundNum() {
		return outboundNum;
	}

	public void setOutboundNum(Double outboundNum) {
		this.outboundNum = outboundNum;
	}

	public Double getOutboundPrice() {
		return outboundPrice;
	}

	public void setOutboundPrice(Double outboundPrice) {
		this.outboundPrice = outboundPrice;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public String getIsCreateVoucher() {
		return isCreateVoucher;
	}

	public void setIsCreateVoucher(String isCreateVoucher) {
		this.isCreateVoucher = isCreateVoucher;
	}

	public String getVoucherNo() {
		return voucherNo;
	}

	public void setVoucherNo(String voucherNo) {
		this.voucherNo = voucherNo;
	}

	public String getIsCreateCostVoucher() {
		return isCreateCostVoucher;
	}

	public void setIsCreateCostVoucher(String isCreateCostVoucher) {
		this.isCreateCostVoucher = isCreateCostVoucher;
	}

	public String getCostVoucherNo() {
		return costVoucherNo;
	}

	public void setCostVoucherNo(String costVoucherNo) {
		this.costVoucherNo = costVoucherNo;
	}

	public String getPlanShipLoc() {
		return planShipLoc;
	}

	public void setPlanShipLoc(String planShipLoc) {
		this.planShipLoc = planShipLoc;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
	public Integer getCreator() {
		return creator;
	}

	@Override
	public void setCreator(Integer creator) {
		this.creator = creator;
	}

	@Override
	public Date getCreateTime() {
		return createTime;
	}

	@Override
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Override
	public Integer getModifier() {
		return modifier;
	}

	@Override
	public void setModifier(Integer modifier) {
		this.modifier = modifier;
	}

	@Override
	public Date getModifyTime() {
		return modifyTime;
	}

	@Override
	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	@Override
	public Integer getRecVer() {
		return recVer;
	}

	@Override
	public void setRecVer(Integer recVer) {
		this.recVer = recVer;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getBuyerCode() {
		return buyerCode;
	}

	public void setBuyerCode(String buyerCode) {
		this.buyerCode = buyerCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Double getOutboundAllocNum() {
		return outboundAllocNum;
	}

	public void setOutboundAllocNum(Double outboundAllocNum) {
		this.outboundAllocNum = outboundAllocNum;
	}

	public Double getOutboundPickNum() {
		return outboundPickNum;
	}

	public void setOutboundPickNum(Double outboundPickNum) {
		this.outboundPickNum = outboundPickNum;
	}

	public Double getOutboundShipNum() {
		return outboundShipNum;
	}

	public void setOutboundShipNum(Double outboundShipNum) {
		this.outboundShipNum = outboundShipNum;
	}

	public Double getOutboundOriginNum() {
		return outboundOriginNum;
	}

	public void setOutboundOriginNum(Double outboundOriginNum) {
		this.outboundOriginNum = outboundOriginNum;
	}

	public String getIsCrossDock() {
		return isCrossDock;
	}

	public void setIsCrossDock(String isCrossDock) {
		this.isCrossDock = isCrossDock;
	}

	public String getInboundOrderNo() {
		return inboundOrderNo;
	}

	public void setInboundOrderNo(String inboundOrderNo) {
		this.inboundOrderNo = inboundOrderNo;
	}

	public String getInboundLineNo() {
		return inboundLineNo;
	}

	public void setInboundLineNo(String inboundLineNo) {
		this.inboundLineNo = inboundLineNo;
	}

	public String getGenericSkuCode() {
		return genericSkuCode;
	}

	public void setGenericSkuCode(String genericSkuCode) {
		this.genericSkuCode = genericSkuCode;
	}
}
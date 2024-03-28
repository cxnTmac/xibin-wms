package com.xibin.wms.pojo;

import com.xibin.core.daosupport.BaseModel;

import java.util.Date;

public class WmOutboundHeader extends BaseModel {
	private Integer id;

	private String orderNo;

	private String buyerCode;

	private String status;

	private Date orderTime;

	private Integer auditOp;

	private String auditStatus;

	private Date auditTime;

	private String outboundType;

	private String isCalculated;

	private String isCostCalculated;

	private Integer voucherId;

	private Integer costVoucherId;

	private String trackingNo;

	private String logisticsNo;

	private Double freight;

	private String freightType;

	private String isRecievedCash;

	private Double totalPrice;

	private Double cash;

	private Double priceDifferent;

	private Date paymentTime;

	private String paymentPicUrl;

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

	public String getBuyerCode() {
		return buyerCode;
	}

	public void setBuyerCode(String buyerCode) {
		this.buyerCode = buyerCode;
	}

	public Date getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
	}

	public Integer getAuditOp() {
		return auditOp;
	}

	public void setAuditOp(Integer auditOp) {
		this.auditOp = auditOp;
	}

	public Date getAuditTime() {
		return auditTime;
	}

	public void setAuditTime(Date auditTime) {
		this.auditTime = auditTime;
	}

	public String getOutboundType() {
		return outboundType;
	}

	public void setOutboundType(String outboundType) {
		this.outboundType = outboundType;
	}

	public String getIsCalculated() {
		return isCalculated;
	}

	public void setIsCalculated(String isCalculated) {
		this.isCalculated = isCalculated;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}

	public Integer getVoucherId() {
		return voucherId;
	}

	public void setVoucherId(Integer voucherId) {
		this.voucherId = voucherId;
	}

	public Integer getCostVoucherId() {
		return costVoucherId;
	}

	public void setCostVoucherId(Integer costVoucherId) {
		this.costVoucherId = costVoucherId;
	}

	public String getIsCostCalculated() {
		return isCostCalculated;
	}

	public void setIsCostCalculated(String isCostCalculated) {
		this.isCostCalculated = isCostCalculated;
	}

	public String getTrackingNo() {
		return trackingNo;
	}

	public void setTrackingNo(String trackingNo) {
		this.trackingNo = trackingNo;
	}

	public String getLogisticsNo() {
		return logisticsNo;
	}

	public void setLogisticsNo(String logisticsNo) {
		this.logisticsNo = logisticsNo;
	}

	public Double getFreight() {
		return freight;
	}

	public void setFreight(Double freight) {
		this.freight = freight;
	}

	public String getFreightType() {
		return freightType;
	}

	public void setFreightType(String freightType) {
		this.freightType = freightType;
	}

	public String getIsRecievedCash() {
		return isRecievedCash;
	}

	public void setIsRecievedCash(String isRecievedCash) {
		this.isRecievedCash = isRecievedCash;
	}

	public Double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public Double getCash() {
		return cash;
	}

	public void setCash(Double cash) {
		this.cash = cash;
	}

	public Date getPaymentTime() {
		return paymentTime;
	}

	public void setPaymentTime(Date paymentTime) {
		this.paymentTime = paymentTime;
	}

	public String getPaymentPicUrl() {
		return paymentPicUrl;
	}

	public void setPaymentPicUrl(String paymentPicUrl) {
		this.paymentPicUrl = paymentPicUrl;
	}

	public Double getPriceDifferent() {
		return priceDifferent;
	}

	public void setPriceDifferent(Double priceDifferent) {
		this.priceDifferent = priceDifferent;
	}
}
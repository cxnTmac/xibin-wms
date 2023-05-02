package com.xibin.wms.query;

import java.io.Serializable;
import java.util.Date;

public class WmInboundDetailSaleHistoryQueryItem implements Serializable {

	private String fittingSkuName;

	private String modelCode;

	private String skuCode;

	private String supplierCode;

	private String customerName;

	private Date modifyTime;

	private Double inboundNum;

	private Double inboundPrice;

	public String getFittingSkuName() {
		return fittingSkuName;
	}

	public void setFittingSkuName(String fittingSkuName) {
		this.fittingSkuName = fittingSkuName;
	}

	public String getModelCode() {
		return modelCode;
	}

	public void setModelCode(String modelCode) {
		this.modelCode = modelCode;
	}

	public String getSkuCode() {
		return skuCode;
	}

	public void setSkuCode(String skuCode) {
		this.skuCode = skuCode;
	}

	public String getSupplierCode() {
		return supplierCode;
	}

	public void setSupplierCode(String supplierCode) {
		this.supplierCode = supplierCode;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	public Double getInboundNum() {
		return inboundNum;
	}

	public void setInboundNum(Double inboundNum) {
		this.inboundNum = inboundNum;
	}

	public Double getInboundPrice() {
		return inboundPrice;
	}

	public void setInboundPrice(Double inboundPrice) {
		this.inboundPrice = inboundPrice;
	}

}
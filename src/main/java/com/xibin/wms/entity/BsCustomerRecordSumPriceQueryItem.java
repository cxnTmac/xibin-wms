package com.xibin.wms.entity;

import java.io.Serializable;

public class BsCustomerRecordSumPriceQueryItem implements Serializable {

	private Double total;

	private String customerCode;

	private String customerName;

	private String type;

	private Integer auxiId;

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public String getCustomerCode() {
		return customerCode;
	}

	public void setSupplierCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public Integer getAuxiId() {
		return auxiId;
	}

	public void setAuxiId(Integer auxiId) {
		this.auxiId = auxiId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

}
package com.xibin.wms.query;

import com.xibin.wms.pojo.BsCustomerRecord;

import java.io.Serializable;

public class BsCustomerRecordQueryItem extends BsCustomerRecord implements Serializable {

	private String customerName;


	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}


}
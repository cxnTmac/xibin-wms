package com.xibin.wms.pojo;

import com.xibin.core.daosupport.BaseModel;

import java.util.Date;

public class BsCustomerRecord extends BaseModel{
    /**
	 * 
	 */
	private static final long serialVersionUID = 33040803802413473L;

	private Integer id;

    private String customerCode;

    private String abstractt;

    private Integer auxiId;

    private String orderNo;

	private Double pay;

	private String type;

	private Integer voucherId;

	private Date date;

	private String picUrl;

	private String remark;

    private Integer creator;

    private Date createTime;

    private Integer modifier;

    private Date modifyTime;

    private Integer recVer;

    private Integer companyId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public String getAbstractt() {
		return abstractt;
	}

	public void setAbstractt(String abstractt) {
		this.abstractt = abstractt;
	}

	public Double getPay() {
		return pay;
	}

	public void setPay(Double pay) {
		this.pay = pay;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getVoucherId() {
		return voucherId;
	}

	public void setVoucherId(Integer voucherId) {
		this.voucherId = voucherId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getCreator() {
		return creator;
	}

	public void setCreator(Integer creator) {
		this.creator = creator;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getModifier() {
		return modifier;
	}

	public void setModifier(Integer modifier) {
		this.modifier = modifier;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	public Integer getRecVer() {
		return recVer;
	}

	public void setRecVer(Integer recVer) {
		this.recVer = recVer;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public Integer getAuxiId() {
		return auxiId;
	}

	public void setAuxiId(Integer auxiId) {
		this.auxiId = auxiId;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
}
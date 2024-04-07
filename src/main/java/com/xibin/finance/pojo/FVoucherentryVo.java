package com.xibin.finance.pojo;
import  com.xibin.core.page.pojo.BaseEntity;

/**
 * 凭证分录实体
 * 
 * @copyright 大连骏骁网络科技有限公司
 * @author 骏骁(cxmail@qq.com)
 * @createDate 2019年04月25日
 * @version: V1.0.0
 */
public class FVoucherentryVo extends BaseEntity {

	//凭证内码	
	private Long voucherID;
	//分录号	
	private Long entryID;
	//摘要	
	private String explanation;
	//科目内码	
	private Long accountID;
	//核算项目	
	private String itemID;
	//余额方向（1借、0贷）	
	private Integer dc;
	//本位币金额	
	private Double jAmount;
	//本位币金额	
	private Double dAmount;
	//数量	
	private Double quantity;
	//项目资源内码	
	private Long measureUnitID;
	//单价	
	private String unitPrice;
	//对方科目	
	private Long accountID2;
	//现金流量	
	private Long cashFlowItem;

	public Long getVoucherID() {
		return voucherID;
	}
	public void setVoucherID(Long voucherID) {
		this.voucherID = voucherID;
	}
	public Long getEntryID() {
		return entryID;
	}
	public void setEntryID(Long entryID) {
		this.entryID = entryID;
	}
	public String getExplanation() {
		return explanation;
	}
	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}
	public Long getAccountID() {
		return accountID;
	}
	public void setAccountID(Long accountID) {
		this.accountID = accountID;
	}
	public String getItemID() {
		return itemID;
	}
	public void setItemID(String itemID) {
		this.itemID = itemID;
	}
	public Integer getDc() {
		return dc;
	}
	public void setDc(Integer dc) {
		this.dc = dc;
	}

	public Double getJAmount() {
		return jAmount;
	}

	public void setJAmount(Double jAmount) {
		this.jAmount = jAmount;
	}

	public Double getDAmount() {
		return dAmount;
	}

	public void setDAmount(Double dAmount) {
		this.dAmount = dAmount;
	}

	public Double getQuantity() {
		return quantity;
	}
	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}
	public Long getMeasureUnitID() {
		return measureUnitID;
	}
	public void setMeasureUnitID(Long measureUnitID) {
		this.measureUnitID = measureUnitID;
	}
	public String getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(String unitPrice) {
		this.unitPrice = unitPrice;
	}
	public Long getAccountID2() {
		return accountID2;
	}
	public void setAccountID2(Long accountID2) {
		this.accountID2 = accountID2;
	}
	public Long getCashFlowItem() {
		return cashFlowItem;
	}
	public void setCashFlowItem(Long cashFlowItem) {
		this.cashFlowItem = cashFlowItem;
	}

}
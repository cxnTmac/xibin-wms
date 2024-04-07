package com.xibin.finance.pojo;

import java.util.Date;

/**
 * 凭证表实体
 * 
 * @copyright 大连骏骁网络科技有限公司
 * @author 骏骁(cxmail@qq.com)
 * @createDate 2019年04月25日
 * @version: V1.0.0
 */
public class FVoucher {

	//凭证内码	
	private Long voucherID;
	//凭证日期	
	private Date fdate;
	//会计年度	
	private Long fyear;
	//会计期间	
	private Long fperiod;
	//凭证字内码	
	private Long groupID;
	//凭证号	
	private Long number;
	//备注	
	private String explanation;
	//分录数	
	private Long entryCount;
	//借方金额合计	
	private Double debitTotal;
	//贷方金额合计	
	private Double creditTotal;
	//是否审核（1是、0否）	
	private Integer checked;
	//是否过账（1是、0否）	
	private Integer posted;
	//制单人	
	private Integer preparerID;
	//审核人	
	private Integer checkerID;
	//记账人	
	private Integer posterID;
	//出纳员	
	private Integer cashierID;
	//凭证序号	
	private Long serialNum;
	//单据类型	
	private Long tranType;
	//业务日期	
	private Date transDate;
	//是否结转损益类型	
	private Integer isProfitLoss;
	
	public Integer getIsProfitLoss() {
		return isProfitLoss;
	}
	public void setIsProfitLoss(Integer isProfitLoss) {
		this.isProfitLoss = isProfitLoss;
	}
	public Long getVoucherID() {
		return voucherID;
	}
	public void setVoucherID(Long voucherID) {
		this.voucherID = voucherID;
	}
	public Date getFdate() {
		return fdate;
	}
	public void setFdate(Date fdate) {
		this.fdate = fdate;
	}
	public Long getFyear() {
		return fyear;
	}
	public void setFyear(Long fyear) {
		this.fyear = fyear;
	}
	public Long getFperiod() {
		return fperiod;
	}
	public void setFperiod(Long fperiod) {
		this.fperiod = fperiod;
	}
	public Long getGroupID() {
		return groupID;
	}
	public void setGroupID(Long groupID) {
		this.groupID = groupID;
	}
	public Long getNumber() {
		return number;
	}
	public void setNumber(Long number) {
		this.number = number;
	}
	public String getExplanation() {
		return explanation;
	}
	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}
	public Long getEntryCount() {
		return entryCount;
	}
	public void setEntryCount(Long entryCount) {
		this.entryCount = entryCount;
	}
	public Double getDebitTotal() {
		return debitTotal;
	}
	public void setDebitTotal(Double debitTotal) {
		this.debitTotal = debitTotal;
	}
	public Double getCreditTotal() {
		return creditTotal;
	}
	public void setCreditTotal(Double creditTotal) {
		this.creditTotal = creditTotal;
	}
	public Integer getChecked() {
		return checked;
	}
	public void setChecked(Integer checked) {
		this.checked = checked;
	}
	public Integer getPosted() {
		return posted;
	}
	public void setPosted(Integer posted) {
		this.posted = posted;
	}
	public Integer getPreparerID() {
		return preparerID;
	}
	public void setPreparerID(Integer preparerID) {
		this.preparerID = preparerID;
	}
	public Integer getCheckerID() {
		return checkerID;
	}
	public void setCheckerID(Integer checkerID) {
		this.checkerID = checkerID;
	}
	public Integer getPosterID() {
		return posterID;
	}
	public void setPosterID(Integer posterID) {
		this.posterID = posterID;
	}
	public Integer getCashierID() {
		return cashierID;
	}
	public void setCashierID(Integer cashierID) {
		this.cashierID = cashierID;
	}
	public Long getSerialNum() {
		return serialNum;
	}
	public void setSerialNum(Long serialNum) {
		this.serialNum = serialNum;
	}
	public Long getTranType() {
		return tranType;
	}
	public void setTranType(Long tranType) {
		this.tranType = tranType;
	}
	public Date getTransDate() {
		return transDate;
	}
	public void setTransDate(Date transDate) {
		this.transDate = transDate;
	}

}
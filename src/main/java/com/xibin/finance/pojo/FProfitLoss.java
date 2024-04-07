package com.xibin.finance.pojo;


/**
 * 结转损益实体
 * 
 * @copyright 大连骏骁网络科技有限公司
 * @author 骏骁(cxmail@qq.com)
 * @createDate 2019年04月23日
 * @version: V1.0.0
 */
public class FProfitLoss {

	//id	
	private Long id;
	//年
	private Long fyear;
	//月
	private Long fperiod;
	//凭证id
	private Long voucherID;
	//凭证ids
	private Long voucherIDs;
	//是否反结转(1是, 0否)
	private Long carried;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public Long getVoucherID() {
		return voucherID;
	}
	public void setVoucherID(Long voucherID) {
		this.voucherID = voucherID;
	}
	public Long getCarried() {
		return carried;
	}
	public void setCarried(Long carried) {
		this.carried = carried;
	}
	public Long getVoucherIDs() {
		return voucherIDs;
	}
	public void setVoucherIDs(Long voucherIDs) {
		this.voucherIDs = voucherIDs;
	}
}
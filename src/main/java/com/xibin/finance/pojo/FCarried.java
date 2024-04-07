package com.xibin.finance.pojo;

/**
 * 费用摊销实体
 * 
 * @copyright 大连骏骁网络科技有限公司
 * @author 骏骁(cxmail@qq.com)
 * @createDate 2019年04月23日
 * @version: V1.0.0
 */
public class FCarried {

	//id	
	private Long id;
	//年
	private Long fyear;
	//月
	private Long fperiod;
	//凭证id
	private Long voucherID;
	//类型(1客户, 2供应商)
	private Long type;
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
	public Long getType() {
		return type;
	}
	public void setType(Long type) {
		this.type = type;
	}
	public Long getCarried() {
		return carried;
	}
	public void setCarried(Long carried) {
		this.carried = carried;
	}
}
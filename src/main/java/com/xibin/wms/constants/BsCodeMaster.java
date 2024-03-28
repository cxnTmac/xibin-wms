package com.xibin.wms.constants;

public enum BsCodeMaster {
	/****************************************
	 * TYPE客户资金往来类别 BS_CUSTOMER_RECORD_TYPE *
	 ****************************************
	 */
	/**
	 * 现销
	 */
	TYPE_X_SALE("X_SALE"),
	/**
	 * 奢销
	 */
	TYPE_S_SALE("S_SALE"),
	/**
	 * 现购
	 */
	TYPE_X_PURCHASE("X_PURCHASE"),
	/**
	 * 奢购
	 */
	TYPE_S_PURCHASE("S_PURCHASE"),
	/**
	 * 收到货款
	 */
	TYPE_RECEIVE("RECEIVE"),
	/**
	 * 收到现金
	 */
	TYPE_RECEIVE_CASH("RECEIVE_CASH"),
	/**
	 * 银行转账
	 */
	TYPE_BANK_TRANSFER("BANK_TRANSFER"),
	/**
	 * 付款
	 */
	TYPE_PAY("PAY"),
	/**
	 * 退货
	 */
	TYPE_RETURN_INBOUND("RETURN_INBOUND"),

	/**
	 * 退货
	 */
	TYPE_RETURN_OUTBOUND("RETURN_OUTBOUND"),
	/**
	 * 下浮
	 */
	TYPE_DISCOUNT("DISCOUNT"),
	/**
	 * 差价
	 */
	TYPE_PRICE_DIFFRENCT("PRICE_DIFFRENCT"),
	/**
	 * 供应商下浮
	 */
	TYPE_SUPPLIER_DISCOUNT("SUPPLIER_DISCOUNT"),
	/**
	 * 供应商差价
	 */
	TYPE_SUPPLIER_PRICE_DIFFRENCT("SUPPLIER_PRICE_DIFFRENCT"),
	/**
	 * 现销少付款
	 */
	TYPE_ABSENCE_PAY("ABSENCE_PAY"),
	/**
	 * 盘盈
	 */
	TYPE_SURPLUS("SURPLUS"),
	/**
	 * 盘亏
	 */
	TYPE_LOSS("LOSS"),
	/**
	 * 运费已付
	 */
	TYPE_FREIGHT_PAID("FREIGHT_PAID"),
	/**
	 * 运费到付
	 */
	TYPE_FREIGHT_COLLECT("FREIGHT_COLLECT"),
	/**
	 * 运费客户付款
	 */
	TYPE_FREIGHT_CUSTOMER_PAID("FREIGHT_CUSTOMER_PAID"),
	/**
	 * 运费现金付款
	 */
	TYPE_FREIGHT_CASH_PAID("FREIGHT_CASH_PAID"),
	/**
	 * 运费账上扣款
	 */
	TYPE_FREIGHT_DEBIT_ON_ACCOUNT("FREIGHT_DEBIT_ON_ACCOUNT");
	private String code;

	public String getCode() {
		return this.code;
	}

	private BsCodeMaster(String code) {
		this.code = code;
	}

}

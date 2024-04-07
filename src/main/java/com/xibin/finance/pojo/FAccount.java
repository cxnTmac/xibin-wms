package com.xibin.finance.pojo;

/**
 * 会计科目
实体
 * 
 * @copyright 大连骏骁网络科技有限公司
 * @author 骏骁(cxmail@qq.com)
 * @createDate 2019年04月23日
 * @version: V1.0.0
 */
public class FAccount {

	//科目内码	
	private Long accountID;
	//科目代码	
	private String number;
	//科目名称	
	private String name;
	//科目级次	
	private Integer level;
	//明细科目	
	private Integer detail;
	//上级科目内码	
	private Long parentID;
	//根科目内码	
	private Long rootID;
	//科目类别内码	
	private Long groupID;
	//核算项目类别	
	private Long itemClassID;
	//借贷方向（1借、-1贷）	
	private Long dc;
	//现金类科目（1是、0否）	
	private Integer isCash;
	//银行类科目（1是、0否）	
	private Integer isBank;
	//往来核算（1是、0否）	
	private Integer isContact;
	//是否禁用（1是、0否）	
	private Integer isDelete;
	//期初余额
	private Double initialMoney;
	//本年累计借
	private Double initialJMoney;
	//本年累计贷
	private Double initialDMoney;

	public Long getAccountID() {
		return accountID;
	}
	public void setAccountID(Long accountID) {
		this.accountID = accountID;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public Integer getDetail() {
		return detail;
	}
	public void setDetail(Integer detail) {
		this.detail = detail;
	}
	public Long getParentID() {
		return parentID;
	}
	public void setParentID(Long parentID) {
		this.parentID = parentID;
	}
	public Long getRootID() {
		return rootID;
	}
	public void setRootID(Long rootID) {
		this.rootID = rootID;
	}
	public Long getGroupID() {
		return groupID;
	}
	public void setGroupID(Long groupID) {
		this.groupID = groupID;
	}
	public Long getItemClassID() {
		return itemClassID;
	}
	public void setItemClassID(Long itemClassID) {
		this.itemClassID = itemClassID;
	}
	public Long getDc() {
		return dc;
	}
	public void setDc(Long dc) {
		this.dc = dc;
	}
	public Integer getIsCash() {
		return isCash;
	}
	public void setIsCash(Integer isCash) {
		this.isCash = isCash;
	}
	public Integer getIsBank() {
		return isBank;
	}
	public void setIsBank(Integer isBank) {
		this.isBank = isBank;
	}
	public Integer getIsContact() {
		return isContact;
	}
	public void setIsContact(Integer isContact) {
		this.isContact = isContact;
	}
	public Integer getIsDelete() {
		return isDelete;
	}
	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}
	public Double getInitialMoney() {
		return initialMoney;
	}
	public void setInitialMoney(Double initialMoney) {
		this.initialMoney = initialMoney;
	}
	public Double getInitialJMoney() {
		return initialJMoney;
	}
	public void setInitialJMoney(Double initialJMoney) {
		this.initialJMoney = initialJMoney;
	}
	public Double getInitialDMoney() {
		return initialDMoney;
	}
	public void setInitialDMoney(Double initialDMoney) {
		this.initialDMoney = initialDMoney;
	}

}
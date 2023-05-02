package com.xibin.finance.pojo;

import java.util.Date;

/**
 * 核算项目实体
 * 
 * @copyright 大连骏骁网络科技有限公司
 * @author 骏骁(cxmail@qq.com)
 * @createDate 2019年04月23日
 * @version: V1.0.0
 */
public class FItem {

	//ID号	
	private Long itemID;
	//类型ID号	
	private Long itemClassID;
	//编码	
	private String number;
	//父ID	
	private Long parentID;
	//级别	
	private Integer level;
	//是否明细（1是、0否）	
	private Integer detail;
	//名称	
	private String name;
	//是否删除（1是、0否）	
	private Integer isDelete;
	//短代码	
	private String shortNumber;
	//全名	
	private String fullName;
	//修改时间	
	private Date updateTime;

	public Long getItemID() {
		return itemID;
	}
	public void setItemID(Long itemID) {
		this.itemID = itemID;
	}
	public Long getItemClassID() {
		return itemClassID;
	}
	public void setItemClassID(Long itemClassID) {
		this.itemClassID = itemClassID;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public Long getParentID() {
		return parentID;
	}
	public void setParentID(Long parentID) {
		this.parentID = parentID;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getIsDelete() {
		return isDelete;
	}
	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}
	public String getShortNumber() {
		return shortNumber;
	}
	public void setShortNumber(String shortNumber) {
		this.shortNumber = shortNumber;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

}
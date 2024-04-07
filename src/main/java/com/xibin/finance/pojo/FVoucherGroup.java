package com.xibin.finance.pojo;

/**
 * 凭证字实体
 * 
 * @copyright 大连骏骁网络科技有限公司
 * @author 骏骁(cxmail@qq.com)
 * @createDate 2019年04月23日
 * @version: V1.0.0
 */
public class FVoucherGroup {

	//凭证字ID	
	private Long groupID;
	//凭证字名称	
	private String name;

	public Long getGroupID() {
		return groupID;
	}
	public void setGroupID(Long groupID) {
		this.groupID = groupID;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
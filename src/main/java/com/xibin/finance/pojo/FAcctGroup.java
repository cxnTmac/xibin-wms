package com.xibin.finance.pojo;

/**
 * 科目类别实体
 * 
 * @copyright 大连骏骁网络科技有限公司
 * @author 骏骁(cxmail@qq.com)
 * @createDate 2019年04月23日
 * @version: V1.0.0
 */
public class FAcctGroup {

	//科目类别id	
	private Long groupID;
	//上级id	
	private Long parentID;
	//类别名称	
	private String name;

	public Long getGroupID() {
		return groupID;
	}
	public void setGroupID(Long groupID) {
		this.groupID = groupID;
	}
	public Long getParentID() {
		return parentID;
	}
	public void setParentID(Long parentID) {
		this.parentID = parentID;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
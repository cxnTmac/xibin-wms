package com.xibin.core.page.pojo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Entity基类
 * 
 * @author 骏骁
 */
public class BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	// 搜索值
	private String search;

	// 操作类型add/update/delete
	private String editState;

	// 请求参数
	private Map<String, Object> ps;

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public String getEditState() {
		return editState;
	}

	public void setEditState(String editState) {
		this.editState = editState;
	}

	public Map<String, Object> getPs() {
		if (ps == null) {
			ps = new HashMap<String, Object>();
		}
		return ps;
	}

	public void setPs(Map<String, Object> ps) {
		this.ps = ps;
	}
}

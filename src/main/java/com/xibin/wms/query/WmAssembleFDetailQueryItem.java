package com.xibin.wms.query;

import com.xibin.wms.pojo.WmAssembleFDetail;

public class WmAssembleFDetailQueryItem extends WmAssembleFDetail {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String fittingSkuName;
	private String modelCode;

	public String getFittingSkuName() {
		return fittingSkuName;
	}

	public void setFittingSkuName(String fittingSkuName) {
		this.fittingSkuName = fittingSkuName;
	}

	public String getModelCode() {
		return modelCode;
	}

	public void setModelCode(String modelCode) {
		this.modelCode = modelCode;
	}

}

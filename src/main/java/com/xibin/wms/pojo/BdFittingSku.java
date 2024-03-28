package com.xibin.wms.pojo;

import com.xibin.core.daosupport.BaseModel;

import java.util.Date;

public class BdFittingSku extends BaseModel {
	private Integer id;

	private String fittingSkuCode;

	private String quickCode;

	private String fittingSkuName;

	private String fittingSkuStatus;

	private String fittingSkuRemark;

	private String manufacturer;

	private String materialquality;

	private String packageCode;

	private String uomDesc;

	private Double price;

	private String modelCode;

	private String series;

	private String fittingTypeCode;

	private String needToAssemble;

	private String assembleType;

	private String type;

	private String isShow;

	private String groupCode;

	private String defaultLoc;

	private Double minInventory;

	private String def1;

	private String def2;

	private String def3;

	private String def4;

	private String def5;

	private Date lastInboundTime;

	private Date lastOutboundTime;
	private String labelSkuName;

	private String labelModelCode;

	private String labelSpecification;

	private String labelDrawingNo;

	private String labelPackageNum;

	private Date createTime;

	private Date releaseDate;

	private Integer creator;

	private Date modifyTime;

	private Integer modifier;

	private Integer recVer;

	private Integer companyId;

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public String getFittingSkuCode() {
		return fittingSkuCode;
	}

	public void setFittingSkuCode(String fittingSkuCode) {
		this.fittingSkuCode = fittingSkuCode == null ? null : fittingSkuCode.trim();
	}

	public String getFittingSkuName() {
		return fittingSkuName;
	}

	public void setFittingSkuName(String fittingSkuName) {
		this.fittingSkuName = fittingSkuName == null ? null : fittingSkuName.trim();
	}

	public String getFittingSkuStatus() {
		return fittingSkuStatus;
	}

	public void setFittingSkuStatus(String fittingSkuStatus) {
		this.fittingSkuStatus = fittingSkuStatus == null ? null : fittingSkuStatus.trim();
	}

	public String getFittingSkuRemark() {
		return fittingSkuRemark;
	}

	public void setFittingSkuRemark(String fittingSkuRemark) {
		this.fittingSkuRemark = fittingSkuRemark == null ? null : fittingSkuRemark.trim();
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer == null ? null : manufacturer.trim();
	}

	public String getMaterialquality() {
		return materialquality;
	}

	public void setMaterialquality(String materialquality) {
		this.materialquality = materialquality == null ? null : materialquality.trim();
	}

	public String getPackageCode() {
		return packageCode;
	}

	public void setPackageCode(String packageCode) {
		this.packageCode = packageCode == null ? null : packageCode.trim();
	}

	public String getUomDesc() {
		return uomDesc;
	}

	public void setUomDesc(String uomDesc) {
		this.uomDesc = uomDesc == null ? null : uomDesc.trim();
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getModelCode() {
		return modelCode;
	}

	public void setModelCode(String modelCode) {
		this.modelCode = modelCode == null ? null : modelCode.trim();
	}

	public String getFittingTypeCode() {
		return fittingTypeCode;
	}

	public void setFittingTypeCode(String fittingTypeCode) {
		this.fittingTypeCode = fittingTypeCode == null ? null : fittingTypeCode.trim();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type == null ? null : type.trim();
	}

	public String getDef1() {
		return def1;
	}

	public void setDef1(String def1) {
		this.def1 = def1 == null ? null : def1.trim();
	}

	public String getDef2() {
		return def2;
	}

	public void setDef2(String def2) {
		this.def2 = def2 == null ? null : def2.trim();
	}

	public String getDef3() {
		return def3;
	}

	public void setDef3(String def3) {
		this.def3 = def3 == null ? null : def3.trim();
	}

	public String getDef4() {
		return def4;
	}

	public void setDef4(String def4) {
		this.def4 = def4 == null ? null : def4.trim();
	}

	public String getDef5() {
		return def5;
	}

	public void setDef5(String def5) {
		this.def5 = def5 == null ? null : def5.trim();
	}

	@Override
	public Date getCreateTime() {
		return createTime;
	}

	@Override
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Override
	public Integer getCreator() {
		return creator;
	}

	@Override
	public void setCreator(Integer creator) {
		this.creator = creator;
	}

	@Override
	public Date getModifyTime() {
		return modifyTime;
	}

	@Override
	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	@Override
	public Integer getModifier() {
		return modifier;
	}

	@Override
	public void setModifier(Integer modifier) {
		this.modifier = modifier;
	}

	@Override
	public Integer getRecVer() {
		return recVer;
	}

	@Override
	public void setRecVer(Integer recVer) {
		this.recVer = recVer;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public String getNeedToAssemble() {
		return needToAssemble;
	}

	public void setNeedToAssemble(String needToAssemble) {
		this.needToAssemble = needToAssemble;
	}

	public String getAssembleType() {
		return assembleType;
	}

	public void setAssembleType(String assembleType) {
		this.assembleType = assembleType;
	}

	public String getIsShow() {
		return isShow;
	}

	public void setIsShow(String isShow) {
		this.isShow = isShow;
	}

	public String getQuickCode() {
		return quickCode;
	}

	public void setQuickCode(String quickCode) {
		this.quickCode = quickCode;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public String getDefaultLoc() {
		return defaultLoc;
	}

	public void setDefaultLoc(String defaultLoc) {
		this.defaultLoc = defaultLoc;
	}

	public Double getMinInventory() {
		return minInventory;
	}

	public void setMinInventory(Double minInventory) {
		this.minInventory = minInventory;
	}

	public Date getLastInboundTime() {
		return lastInboundTime;
	}

	public void setLastInboundTime(Date lastInboundTime) {
		this.lastInboundTime = lastInboundTime;
	}

	public Date getLastOutboundTime() {
		return lastOutboundTime;
	}

	public void setLastOutboundTime(Date lastOutboundTime) {
		this.lastOutboundTime = lastOutboundTime;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}

	public String getLabelSkuName() {
		return labelSkuName;
	}

	public void setLabelSkuName(String labelSkuName) {
		this.labelSkuName = labelSkuName;
	}

	public String getLabelModelCode() {
		return labelModelCode;
	}

	public void setLabelModelCode(String labelModelCode) {
		this.labelModelCode = labelModelCode;
	}

	public String getLabelSpecification() {
		return labelSpecification;
	}

	public void setLabelSpecification(String labelSpecification) {
		this.labelSpecification = labelSpecification;
	}

	public String getLabelDrawingNo() {
		return labelDrawingNo;
	}

	public void setLabelDrawingNo(String labelDrawingNo) {
		this.labelDrawingNo = labelDrawingNo;
	}

	public String getLabelPackageNum() {
		return labelPackageNum;
	}

	public void setLabelPackageNum(String labelPackageNum) {
		this.labelPackageNum = labelPackageNum;
	}
}
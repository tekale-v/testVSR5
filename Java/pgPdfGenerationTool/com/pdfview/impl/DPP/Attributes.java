//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.04.10 at 10:09:09 AM EDT 
//

package com.pdfview.impl.DPP;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"title",
		"description",
		"type",
		"specificationSubType",
		"originator",
		"lastUpdateUser",
		"revision",
		"originated",
		"segment",
		"phase",
		"owner",
		"releaseDate",
		"effectivityDate",
		"expirationDate",
		"previousRevisionObsoleteDate",
		"reasonforChange",
		"productDoseperUse",
		"productDoseperUseUoM",
		"expectedFrequencyofUse",
		"expectedFrequencyofUseUoM",
		"isProductCertification",
		"modeofProductDisposal",
		"lifeCycleStatus",
		"brand",
		"productExtraVariant",
		"classdata",
		"subClassdata",
		"replacedProductName",
		"reportedFunction",
		"businessArea",
		"productCategoryPlatform",
		"productTechnologyPlatform",
		"productTechnologyChassis",
		"franchise",
		"structuredReleaseCriteriaRequired",
		"onshelfProductDensity",
		"densityUOM",
		"baseQuantity",
		"sAPBOMBaseQTY",
		"localDescription",
		"otherNames",
		"intendedMarkets",
		"replacedByProductID",
		"uniqueFormulaIdentifier",
		"doesthisDeviceContainFlammableLiquid",
		"comment",
		"archiveDate",
		"archiveComment"

})

@XmlRootElement(name = "Attributes", namespace = "")
public class Attributes {

	@XmlElement(name = "Title", namespace = "") 
	protected String title;
	@XmlElement(name = "Description", namespace = "") 
	protected String description;
	@XmlElement(name = "Type", namespace = "") 
	protected String type;
	@XmlElement(name = "SpecificationSubType", namespace = "") 
	protected String specificationSubType;
	@XmlElement(name = "Originator", namespace = "") 
	protected String originator;
	@XmlElement(name = "LastUpdateUser", namespace = "") 
	protected String lastUpdateUser;
	@XmlElement(name = "Revision", namespace = "") 
	protected String revision;
	@XmlElement(name = "Originated", namespace = "") 
	protected String originated;
	@XmlElement(name = "Segment", namespace = "") 
	protected String segment;
	@XmlElement(name = "Phase", namespace = "") 
	protected String phase;
	@XmlElement(name = "Owner", namespace = "") 
	protected String owner;
	@XmlElement(name = "ReleaseDate", namespace = "") 
	protected String releaseDate;
	@XmlElement(name = "EffectivityDate", namespace = "") 
	protected String effectivityDate;
	@XmlElement(name = "ExpirationDate", namespace = "") 
	protected String expirationDate;
	@XmlElement(name = "PreviousRevisionObsoleteDate", namespace = "") 
	protected String previousRevisionObsoleteDate;
	@XmlElement(name = "ReasonforChange", namespace = "") 
	protected String reasonforChange;
	@XmlElement(name = "ProductDoseperUse", namespace = "") 
	protected String productDoseperUse;
	@XmlElement(name = "ProductDoseperUseUoM", namespace = "") 
	protected String productDoseperUseUoM;
	@XmlElement(name = "ExpectedFrequencyofUse", namespace = "") 
	protected String expectedFrequencyofUse;
	@XmlElement(name = "ExpectedFrequencyofUseUoM", namespace = "") 
	protected String expectedFrequencyofUseUoM;
	@XmlElement(name = "IsProductCertification", namespace = "") 
	protected String isProductCertification;
	@XmlElement(name = "ModeofProductDisposal", namespace = "") 
	protected String modeofProductDisposal;
	@XmlElement(name = "LifeCycleStatus", namespace = "") 
	protected String lifeCycleStatus;
	@XmlElement(name = "Brand", namespace = "") 
	protected String brand;
	@XmlElement(name = "ProductExtraVariant", namespace = "") 
	protected String productExtraVariant;
	@XmlElement(name = "Classdata", namespace = "") 
	protected String classdata;
	@XmlElement(name = "SubClassdata", namespace = "") 
	protected String subClassdata;
	@XmlElement(name = "ReplacedProductName", namespace = "") 
	protected String replacedProductName;
	@XmlElement(name = "ReportedFunction", namespace = "") 
	protected String reportedFunction;
	@XmlElement(name = "BusinessArea", namespace = "") 
	protected String businessArea;
	@XmlElement(name = "ProductCategoryPlatform", namespace = "") 
	protected String productCategoryPlatform;
	@XmlElement(name = "ProductTechnologyPlatform", namespace = "") 
	protected String productTechnologyPlatform;
	@XmlElement(name = "ProductTechnologyChassis", namespace = "") 
	protected String productTechnologyChassis;
	@XmlElement(name = "Franchise", namespace = "") 
	protected String franchise;
	@XmlElement(name = "StructuredReleaseCriteriaRequired", namespace = "") 
	protected String structuredReleaseCriteriaRequired;
	@XmlElement(name = "OnshelfProductDensity", namespace = "") 
	protected String onshelfProductDensity;
	@XmlElement(name = "DensityUOM", namespace = "") 
	protected String densityUOM;
	@XmlElement(name = "BaseQuantity", namespace = "") 
	protected String baseQuantity;
	@XmlElement(name = "SAPBOMBaseQTY", namespace = "") 
	protected String sAPBOMBaseQTY;
	@XmlElement(name = "LocalDescription", namespace = "") 
	protected String localDescription;
	@XmlElement(name = "OtherNames", namespace = "") 
	protected String otherNames;
	@XmlElement(name = "IntendedMarkets", namespace = "") 
	protected String intendedMarkets;
	@XmlElement(name = "ReplacedByProductID", namespace = "") 
	protected String replacedByProductID;
	@XmlElement(name = "UniqueFormulaIdentifier", namespace = "") 
	protected String uniqueFormulaIdentifier;
	@XmlElement(name = "DoesthisDeviceContainFlammableLiquid", namespace = "") 
	protected String doesthisDeviceContainFlammableLiquid;
	@XmlElement(name = "Comment", namespace = "") 
	protected String comment;
	@XmlElement(name = "ArchiveDate", namespace = "") 
	protected String archiveDate;
	@XmlElement(name = "ArchiveComment", namespace = "") 
	protected String archiveComment;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSpecificationSubType() {
		return specificationSubType;
	}
	public void setSpecificationSubType(String specificationSubType) {
		this.specificationSubType = specificationSubType;
	}
	public String getOriginator() {
		return originator;
	}
	public void setOriginator(String originator) {
		this.originator = originator;
	}
	public String getLastUpdateUser() {
		return lastUpdateUser;
	}
	public void setLastUpdateUser(String lastUpdateUser) {
		this.lastUpdateUser = lastUpdateUser;
	}
	public String getRevision() {
		return revision;
	}
	public void setRevision(String revision) {
		this.revision = revision;
	}
	public String getOriginated() {
		return originated;
	}
	public void setOriginated(String originated) {
		this.originated = originated;
	}
	public String getSegment() {
		return segment;
	}
	public void setSegment(String segment) {
		this.segment = segment;
	}
	public String getPhase() {
		return phase;
	}
	public void setPhase(String phase) {
		this.phase = phase;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getReleaseDate() {
		return releaseDate;
	}
	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}
	public String getEffectivityDate() {
		return effectivityDate;
	}
	public void setEffectivityDate(String effectivityDate) {
		this.effectivityDate = effectivityDate;
	}
	public String getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}
	public String getPreviousRevisionObsoleteDate() {
		return previousRevisionObsoleteDate;
	}
	public void setPreviousRevisionObsoleteDate(String previousRevisionObsoleteDate) {
		this.previousRevisionObsoleteDate = previousRevisionObsoleteDate;
	}
	public String getReasonforChange() {
		return reasonforChange;
	}
	public void setReasonforChange(String reasonforChange) {
		this.reasonforChange = reasonforChange;
	}
	public String getProductDoseperUse() {
		return productDoseperUse;
	}
	public void setProductDoseperUse(String productDoseperUse) {
		this.productDoseperUse = productDoseperUse;
	}
	public String getProductDoseperUseUoM() {
		return productDoseperUseUoM;
	}
	public void setProductDoseperUseUoM(String productDoseperUseUoM) {
		this.productDoseperUseUoM = productDoseperUseUoM;
	}
	public String getExpectedFrequencyofUse() {
		return expectedFrequencyofUse;
	}
	public void setExpectedFrequencyofUse(String expectedFrequencyofUse) {
		this.expectedFrequencyofUse = expectedFrequencyofUse;
	}
	public String getExpectedFrequencyofUseUoM() {
		return expectedFrequencyofUseUoM;
	}
	public void setExpectedFrequencyofUseUoM(String expectedFrequencyofUseUoM) {
		this.expectedFrequencyofUseUoM = expectedFrequencyofUseUoM;
	}
	
	public String getModeofProductDisposal() {
		return modeofProductDisposal;
	}
	public void setModeofProductDisposal(String modeofProductDisposal) {
		this.modeofProductDisposal = modeofProductDisposal;
	}
	public String getLifeCycleStatus() {
		return lifeCycleStatus;
	}
	public void setLifeCycleStatus(String lifeCycleStatus) {
		this.lifeCycleStatus = lifeCycleStatus;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getProductExtraVariant() {
		return productExtraVariant;
	}
	public void setProductExtraVariant(String productExtraVariant) {
		this.productExtraVariant = productExtraVariant;
	}
	public String getClassdata() {
		return classdata;
	}
	public void setClassdata(String classdata) {
		this.classdata = classdata;
	}
	public String getSubClassdata() {
		return subClassdata;
	}
	public void setSubClassdata(String subClassdata) {
		this.subClassdata = subClassdata;
	}
	public String getReplacedProductName() {
		return replacedProductName;
	}
	public void setReplacedProductName(String replacedProductName) {
		this.replacedProductName = replacedProductName;
	}
	public String getReportedFunction() {
		return reportedFunction;
	}
	public void setReportedFunction(String reportedFunction) {
		this.reportedFunction = reportedFunction;
	}
	public String getBusinessArea() {
		return businessArea;
	}
	public void setBusinessArea(String businessArea) {
		this.businessArea = businessArea;
	}
	public String getProductCategoryPlatform() {
		return productCategoryPlatform;
	}
	public void setProductCategoryPlatform(String productCategoryPlatform) {
		this.productCategoryPlatform = productCategoryPlatform;
	}
	public String getProductTechnologyPlatform() {
		return productTechnologyPlatform;
	}
	public void setProductTechnologyPlatform(String productTechnologyPlatform) {
		this.productTechnologyPlatform = productTechnologyPlatform;
	}
	public String getProductTechnologyChassis() {
		return productTechnologyChassis;
	}
	public void setProductTechnologyChassis(String productTechnologyChassis) {
		this.productTechnologyChassis = productTechnologyChassis;
	}
	public String getFranchise() {
		return franchise;
	}
	public void setFranchise(String franchise) {
		this.franchise = franchise;
	}
	public String getStructuredReleaseCriteriaRequired() {
		return structuredReleaseCriteriaRequired;
	}
	public void setStructuredReleaseCriteriaRequired(
			String structuredReleaseCriteriaRequired) {
		this.structuredReleaseCriteriaRequired = structuredReleaseCriteriaRequired;
	}
	public String getOnshelfProductDensity() {
		return onshelfProductDensity;
	}
	public void setOnshelfProductDensity(String onshelfProductDensity) {
		this.onshelfProductDensity = onshelfProductDensity;
	}
	public String getDensityUOM() {
		return densityUOM;
	}
	public void setDensityUOM(String densityUOM) {
		this.densityUOM = densityUOM;
	}
	public String getBaseQuantity() {
		return baseQuantity;
	}
	public void setBaseQuantity(String baseQuantity) {
		this.baseQuantity = baseQuantity;
	}
	public String getsAPBOMBaseQTY() {
		return sAPBOMBaseQTY;
	}
	public void setsAPBOMBaseQTY(String sAPBOMBaseQTY) {
		this.sAPBOMBaseQTY = sAPBOMBaseQTY;
	}
	public String getLocalDescription() {
		return localDescription;
	}
	public void setLocalDescription(String localDescription) {
		this.localDescription = localDescription;
	}
	public String getOtherNames() {
		return otherNames;
	}
	public void setOtherNames(String otherNames) {
		this.otherNames = otherNames;
	}
	public String getIntendedMarkets() {
		return intendedMarkets;
	}
	public void setIntendedMarkets(String intendedMarkets) {
		this.intendedMarkets = intendedMarkets;
	}
	public String getReplacedByProductID() {
		return replacedByProductID;
	}
	public void setReplacedByProductID(String replacedByProductID) {
		this.replacedByProductID = replacedByProductID;
	}
	public String getUniqueFormulaIdentifier() {
		return uniqueFormulaIdentifier;
	}
	public void setUniqueFormulaIdentifier(String uniqueFormulaIdentifier) {
		this.uniqueFormulaIdentifier = uniqueFormulaIdentifier;
	}
	public String getDoesthisDeviceContainFlammableLiquid() {
		return doesthisDeviceContainFlammableLiquid;
	}
	public void setDoesthisDeviceContainFlammableLiquid(
			String doesthisDeviceContainFlammableLiquid) {
		this.doesthisDeviceContainFlammableLiquid = doesthisDeviceContainFlammableLiquid;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getArchiveDate() {
		return archiveDate;
	}
	public void setArchiveDate(String archiveDate) {
		this.archiveDate = archiveDate;
	}
	public String getArchiveComment() {
		return archiveComment;
	}
	public void setArchiveComment(String archiveComment) {
		this.archiveComment = archiveComment;
	}
	public String getIsProductCertification() {
		return isProductCertification;
	}
	public void setIsProductCertification(String isProductCertification) {
		this.isProductCertification = isProductCertification;
	}
	
}

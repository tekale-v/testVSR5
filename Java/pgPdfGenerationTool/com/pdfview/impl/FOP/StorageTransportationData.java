package com.pdfview.impl.FOP;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"technology",
		"isProductMarketedasChildrensProduct",
		"doestheProductRequireChildSafeDesign",
		"isProductExposedtoChildren",
		"warehousingClassification",
		"shippingHazardClassification",
		"shippingInformation",
		"storageConditions",
		"intendedMarkets",
		"businessArea",
		"productCategoryPlatform",
		"standardCost",
		"storageTemperatureLimits",
		"storageHumidityLimits"

})


public class StorageTransportationData {

	@XmlElement(name = "Technology", namespace = "") 
	protected String technology;
	@XmlElement(name = "IsProductMarketedasChildrensProduct", namespace = "") 
	protected String isProductMarketedasChildrensProduct;
	@XmlElement(name = "DoestheProductRequireChildSafeDesign", namespace = "") 
	protected String doestheProductRequireChildSafeDesign;
	@XmlElement(name = "IsProductExposedtoChildren", namespace = "") 
	protected String isProductExposedtoChildren;
	@XmlElement(name = "WarehousingClassification", namespace = "") 
	protected String warehousingClassification;
	@XmlElement(name = "ShippingHazardClassification", namespace = "") 
	protected String shippingHazardClassification;
	@XmlElement(name = "ShippingInformation", namespace = "") 
	protected String shippingInformation;
	@XmlElement(name = "StorageConditions", namespace = "") 
	protected String storageConditions;
	@XmlElement(name = "IntendedMarkets", namespace = "") 
	protected String intendedMarkets;
	@XmlElement(name = "BusinessArea", namespace = "") 
	protected String businessArea;
	@XmlElement(name = "ProductCategoryPlatform", namespace = "") 
	protected String productCategoryPlatform;
	@XmlElement(name = "StandardCost", namespace = "") 
	protected String standardCost;
	@XmlElement(name = "StorageTemperatureLimits", namespace = "") 
	protected String storageTemperatureLimits;
	@XmlElement(name = "StorageHumidityLimits", namespace = "") 
	protected String storageHumidityLimits;
	public String getTechnology() {
		return technology;
	}
	public void setTechnology(String technology) {
		this.technology = technology;
	}
	public String getIsProductMarketedasChildrensProduct() {
		return isProductMarketedasChildrensProduct;
	}
	public void setIsProductMarketedasChildrensProduct(
			String isProductMarketedasChildrensProduct) {
		this.isProductMarketedasChildrensProduct = isProductMarketedasChildrensProduct;
	}
	public String getDoestheProductRequireChildSafeDesign() {
		return doestheProductRequireChildSafeDesign;
	}
	public void setDoestheProductRequireChildSafeDesign(
			String doestheProductRequireChildSafeDesign) {
		this.doestheProductRequireChildSafeDesign = doestheProductRequireChildSafeDesign;
	}
	public String getIsProductExposedtoChildren() {
		return isProductExposedtoChildren;
	}
	public void setIsProductExposedtoChildren(String isProductExposedtoChildren) {
		this.isProductExposedtoChildren = isProductExposedtoChildren;
	}
	public String getWarehousingClassification() {
		return warehousingClassification;
	}
	public void setWarehousingClassification(String warehousingClassification) {
		this.warehousingClassification = warehousingClassification;
	}
	public String getShippingHazardClassification() {
		return shippingHazardClassification;
	}
	public void setShippingHazardClassification(String shippingHazardClassification) {
		this.shippingHazardClassification = shippingHazardClassification;
	}
	public String getShippingInformation() {
		return shippingInformation;
	}
	public void setShippingInformation(String shippingInformation) {
		this.shippingInformation = shippingInformation;
	}
	public String getStorageConditions() {
		return storageConditions;
	}
	public void setStorageConditions(String storageConditions) {
		this.storageConditions = storageConditions;
	}
	public String getIntendedMarkets() {
		return intendedMarkets;
	}
	public void setIntendedMarkets(String intendedMarkets) {
		this.intendedMarkets = intendedMarkets;
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
	public String getStandardCost() {
		return standardCost;
	}
	public void setStandardCost(String standardCost) {
		this.standardCost = standardCost;
	}
	public String getStorageTemperatureLimits() {
		return storageTemperatureLimits;
	}
	public void setStorageTemperatureLimits(String storageTemperatureLimits) {
		this.storageTemperatureLimits = storageTemperatureLimits;
	}
	public String getStorageHumidityLimits() {
		return storageHumidityLimits;
	}
	public void setStorageHumidityLimits(String storageHumidityLimits) {
		this.storageHumidityLimits = storageHumidityLimits;
	}
	

}

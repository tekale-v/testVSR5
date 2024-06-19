package com.pdfview.impl.DPP;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"technology",
		"powerSource",
		"batteryType",
		"batteryweight",
		"batteryweightUnitofMeasure",
		"batterychemicalcomposition",
		"gramsofLithiumpercellbattery",
		"gramsofLithiumpercellbatteryUnitofMeasure",
		"lithiumBatteryEnergy",
		"lithiumBatteryEnergyUnitofMeasure",
		"lithiumBatteryVoltage",
		"lithiumBatteryVoltageUnitofMeasure",
		"batterySize",
		"isabuttonrequiredforLiMetal",
		"numberofCellsrequiredforLiMetal",
		"typicalCapacitymAh",
		"typicalCapacityUnitofMeasure",
		"istheproductabattery",
		"doestheproductcontainabattery",
		"numberofcellsbatteriesshippedinsideDevice",
		"numberofcellsbatteriesshippedoutsideDevice",
		"arebatteriesrequired",
		"isProductMarketedasChildrensProduct",
		"doestheProductRequireChildSafeDesign",
		"isProductExposedtoChildren",
		"warehousingClassification",
		"evaporationRate",
		"reserveAcidity",
		"reserveAlkalinity",
		"shippingHazardClassification",
		"shippingInformation",
		"storageConditions",
		"intendedMarkets",
		"nominalBatteryWeight",
		"nominalBatteryVoltage",
		"batteryVoltageUoM",
		"numberofcells",
		"gramsofLithiumpercell",
		"gramsofLithiumUoM",
		"isthisaButtonBattery"

})


public class StorageTransportationData {

	@XmlElement(name = "Technology", namespace = "") 
	protected String technology;
	@XmlElement(name = "PowerSource", namespace = "") 
	protected String powerSource;
	@XmlElement(name = "BatteryType", namespace = "") 
	protected String batteryType;
	@XmlElement(name = "Batteryweight", namespace = "") 
	protected String batteryweight;
	@XmlElement(name = "BatteryweightUnitofMeasure", namespace = "") 
	protected String batteryweightUnitofMeasure;
	@XmlElement(name = "Batterychemicalcomposition", namespace = "") 
	protected String batterychemicalcomposition;
	@XmlElement(name = "GramsofLithiumpercellbattery", namespace = "") 
	protected String gramsofLithiumpercellbattery;
	@XmlElement(name = "GramsofLithiumpercellbatteryUnitofMeasure", namespace = "") 
	protected String gramsofLithiumpercellbatteryUnitofMeasure;
	@XmlElement(name = "LithiumBatteryEnergy", namespace = "") 
	protected String lithiumBatteryEnergy;
	@XmlElement(name = "LithiumBatteryEnergyUnitofMeasure", namespace = "") 
	protected String lithiumBatteryEnergyUnitofMeasure;
	@XmlElement(name = "LithiumBatteryVoltage", namespace = "") 
	protected String lithiumBatteryVoltage;
	@XmlElement(name = "LithiumBatteryVoltageUnitofMeasure", namespace = "") 
	protected String lithiumBatteryVoltageUnitofMeasure;
	@XmlElement(name = "BatterySize", namespace = "") 
	protected String batterySize;
	@XmlElement(name = "IsabuttonrequiredforLiMetal", namespace = "") 
	protected String isabuttonrequiredforLiMetal;
	@XmlElement(name = "NumberofCellsrequiredforLiMetal", namespace = "") 
	protected String numberofCellsrequiredforLiMetal;
	@XmlElement(name = "TypicalCapacitymAh", namespace = "") 
	protected String typicalCapacitymAh;
	@XmlElement(name = "TypicalCapacityUnitofMeasure", namespace = "") 
	protected String typicalCapacityUnitofMeasure;
	@XmlElement(name = "Istheproductabattery", namespace = "") 
	protected String istheproductabattery;
	@XmlElement(name = "Doestheproductcontainabattery", namespace = "") 
	protected String doestheproductcontainabattery;
	@XmlElement(name = "NumberofcellsbatteriesshippedinsideDevice", namespace = "") 
	protected String numberofcellsbatteriesshippedinsideDevice;
	@XmlElement(name = "NumberofcellsbatteriesshippedoutsideDevice", namespace = "") 
	protected String numberofcellsbatteriesshippedoutsideDevice;
	@XmlElement(name = "Arebatteriesrequired", namespace = "") 
	protected String arebatteriesrequired;
	@XmlElement(name = "IsProductMarketedasChildrensProduct", namespace = "") 
	protected String isProductMarketedasChildrensProduct;
	@XmlElement(name = "DoestheProductRequireChildSafeDesign", namespace = "") 
	protected String doestheProductRequireChildSafeDesign;
	@XmlElement(name = "IsProductExposedtoChildren", namespace = "") 
	protected String isProductExposedtoChildren;
	@XmlElement(name = "WarehousingClassification", namespace = "") 
	protected String warehousingClassification;
	@XmlElement(name = "EvaporationRate", namespace = "") 
	protected String evaporationRate;
	@XmlElement(name = "ReserveAcidity", namespace = "") 
	protected String reserveAcidity;
	@XmlElement(name = "ReserveAlkalinity", namespace = "") 
	protected String reserveAlkalinity;
	@XmlElement(name = "ShippingHazardClassification", namespace = "") 
	protected String shippingHazardClassification;
	@XmlElement(name = "ShippingInformation", namespace = "") 
	protected String shippingInformation;
	@XmlElement(name = "StorageConditions", namespace = "") 
	protected String storageConditions;
	@XmlElement(name = "IntendedMarkets", namespace = "") 
	protected String intendedMarkets;
	@XmlElement(name = "NominalBatteryWeight", namespace = "") 
	 protected String nominalBatteryWeight;
	@XmlElement(name = "NominalBatteryVoltage", namespace = "") 
	 protected String nominalBatteryVoltage;
	@XmlElement(name = "BatteryVoltageUoM", namespace = "") 
	 protected String batteryVoltageUoM;
	@XmlElement(name = "Numberofcells", namespace = "") 
	 protected String numberofcells;
	@XmlElement(name = "GramsofLithiumpercell", namespace = "") 
	 protected String gramsofLithiumpercell;
	@XmlElement(name = "GramsofLithiumUoM", namespace = "") 
	 protected String gramsofLithiumUoM;
	@XmlElement(name = "IsthisaButtonBattery", namespace = "") 
	 protected String isthisaButtonBattery;
	public String getTechnology() {
		return technology;
	}
	public void setTechnology(String technology) {
		this.technology = technology;
	}
	public String getPowerSource() {
		return powerSource;
	}
	public void setPowerSource(String powerSource) {
		this.powerSource = powerSource;
	}
	public String getBatteryType() {
		return batteryType;
	}
	public void setBatteryType(String batteryType) {
		this.batteryType = batteryType;
	}
	public String getBatteryweight() {
		return batteryweight;
	}
	public void setBatteryweight(String batteryweight) {
		this.batteryweight = batteryweight;
	}
	public String getBatteryweightUnitofMeasure() {
		return batteryweightUnitofMeasure;
	}
	public void setBatteryweightUnitofMeasure(String batteryweightUnitofMeasure) {
		this.batteryweightUnitofMeasure = batteryweightUnitofMeasure;
	}
	public String getBatterychemicalcomposition() {
		return batterychemicalcomposition;
	}
	public void setBatterychemicalcomposition(String batterychemicalcomposition) {
		this.batterychemicalcomposition = batterychemicalcomposition;
	}
	public String getGramsofLithiumpercellbattery() {
		return gramsofLithiumpercellbattery;
	}
	public void setGramsofLithiumpercellbattery(String gramsofLithiumpercellbattery) {
		this.gramsofLithiumpercellbattery = gramsofLithiumpercellbattery;
	}
	public String getGramsofLithiumpercellbatteryUnitofMeasure() {
		return gramsofLithiumpercellbatteryUnitofMeasure;
	}
	public void setGramsofLithiumpercellbatteryUnitofMeasure(
			String gramsofLithiumpercellbatteryUnitofMeasure) {
		this.gramsofLithiumpercellbatteryUnitofMeasure = gramsofLithiumpercellbatteryUnitofMeasure;
	}
	public String getLithiumBatteryEnergy() {
		return lithiumBatteryEnergy;
	}
	public void setLithiumBatteryEnergy(String lithiumBatteryEnergy) {
		this.lithiumBatteryEnergy = lithiumBatteryEnergy;
	}
	public String getLithiumBatteryEnergyUnitofMeasure() {
		return lithiumBatteryEnergyUnitofMeasure;
	}
	public void setLithiumBatteryEnergyUnitofMeasure(
			String lithiumBatteryEnergyUnitofMeasure) {
		this.lithiumBatteryEnergyUnitofMeasure = lithiumBatteryEnergyUnitofMeasure;
	}
	public String getLithiumBatteryVoltage() {
		return lithiumBatteryVoltage;
	}
	public void setLithiumBatteryVoltage(String lithiumBatteryVoltage) {
		this.lithiumBatteryVoltage = lithiumBatteryVoltage;
	}
	public String getLithiumBatteryVoltageUnitofMeasure() {
		return lithiumBatteryVoltageUnitofMeasure;
	}
	public void setLithiumBatteryVoltageUnitofMeasure(
			String lithiumBatteryVoltageUnitofMeasure) {
		this.lithiumBatteryVoltageUnitofMeasure = lithiumBatteryVoltageUnitofMeasure;
	}
	public String getBatterySize() {
		return batterySize;
	}
	public void setBatterySize(String batterySize) {
		this.batterySize = batterySize;
	}
	public String getIsabuttonrequiredforLiMetal() {
		return isabuttonrequiredforLiMetal;
	}
	public void setIsabuttonrequiredforLiMetal(String isabuttonrequiredforLiMetal) {
		this.isabuttonrequiredforLiMetal = isabuttonrequiredforLiMetal;
	}
	public String getNumberofCellsrequiredforLiMetal() {
		return numberofCellsrequiredforLiMetal;
	}
	public void setNumberofCellsrequiredforLiMetal(
			String numberofCellsrequiredforLiMetal) {
		this.numberofCellsrequiredforLiMetal = numberofCellsrequiredforLiMetal;
	}
	public String getTypicalCapacitymAh() {
		return typicalCapacitymAh;
	}
	public void setTypicalCapacitymAh(String typicalCapacitymAh) {
		this.typicalCapacitymAh = typicalCapacitymAh;
	}
	public String getTypicalCapacityUnitofMeasure() {
		return typicalCapacityUnitofMeasure;
	}
	public void setTypicalCapacityUnitofMeasure(String typicalCapacityUnitofMeasure) {
		this.typicalCapacityUnitofMeasure = typicalCapacityUnitofMeasure;
	}
	public String getIstheproductabattery() {
		return istheproductabattery;
	}
	public void setIstheproductabattery(String istheproductabattery) {
		this.istheproductabattery = istheproductabattery;
	}
	public String getDoestheproductcontainabattery() {
		return doestheproductcontainabattery;
	}
	public void setDoestheproductcontainabattery(
			String doestheproductcontainabattery) {
		this.doestheproductcontainabattery = doestheproductcontainabattery;
	}
	public String getNumberofcellsbatteriesshippedinsideDevice() {
		return numberofcellsbatteriesshippedinsideDevice;
	}
	public void setNumberofcellsbatteriesshippedinsideDevice(
			String numberofcellsbatteriesshippedinsideDevice) {
		this.numberofcellsbatteriesshippedinsideDevice = numberofcellsbatteriesshippedinsideDevice;
	}
	public String getNumberofcellsbatteriesshippedoutsideDevice() {
		return numberofcellsbatteriesshippedoutsideDevice;
	}
	public void setNumberofcellsbatteriesshippedoutsideDevice(
			String numberofcellsbatteriesshippedoutsideDevice) {
		this.numberofcellsbatteriesshippedoutsideDevice = numberofcellsbatteriesshippedoutsideDevice;
	}
	public String getArebatteriesrequired() {
		return arebatteriesrequired;
	}
	public void setArebatteriesrequired(String arebatteriesrequired) {
		this.arebatteriesrequired = arebatteriesrequired;
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
	public String getEvaporationRate() {
		return evaporationRate;
	}
	public void setEvaporationRate(String evaporationRate) {
		this.evaporationRate = evaporationRate;
	}
	public String getReserveAcidity() {
		return reserveAcidity;
	}
	public void setReserveAcidity(String reserveAcidity) {
		this.reserveAcidity = reserveAcidity;
	}
	public String getReserveAlkalinity() {
		return reserveAlkalinity;
	}
	public void setReserveAlkalinity(String reserveAlkalinity) {
		this.reserveAlkalinity = reserveAlkalinity;
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
	public String getNominalBatteryWeight() {
		return nominalBatteryWeight;
	}
	public void setNominalBatteryWeight(String nominalBatteryWeight) {
		this.nominalBatteryWeight = nominalBatteryWeight;
	}
	public String getNominalBatteryVoltage() {
		return nominalBatteryVoltage;
	}
	public void setNominalBatteryVoltage(String nominalBatteryVoltage) {
		this.nominalBatteryVoltage = nominalBatteryVoltage;
	}
	public String getBatteryVoltageUoM() {
		return batteryVoltageUoM;
	}
	public void setBatteryVoltageUoM(String batteryVoltageUoM) {
		this.batteryVoltageUoM = batteryVoltageUoM;
	}
	public String getNumberofcells() {
		return numberofcells;
	}
	public void setNumberofcells(String numberofcells) {
		this.numberofcells = numberofcells;
	}
	public String getGramsofLithiumpercell() {
		return gramsofLithiumpercell;
	}
	public void setGramsofLithiumpercell(String gramsofLithiumpercell) {
		this.gramsofLithiumpercell = gramsofLithiumpercell;
	}
	public String getGramsofLithiumUoM() {
		return gramsofLithiumUoM;
	}
	public void setGramsofLithiumUoM(String gramsofLithiumUoM) {
		this.gramsofLithiumUoM = gramsofLithiumUoM;
	}
	public String getIsthisaButtonBattery() {
		return isthisaButtonBattery;
	}
	public void setIsthisaButtonBattery(String isthisaButtonBattery) {
		this.isthisaButtonBattery = isthisaButtonBattery;
	}
	

}

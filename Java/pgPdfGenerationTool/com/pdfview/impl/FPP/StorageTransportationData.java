//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.04.10 at 10:09:09 AM EDT 
//

package com.pdfview.impl.FPP;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"powerSource",
"batteryType",
"customerUnitLabeling",
"consumerUnitLabeling",
"shippingInformation",
"labelingInformation",
"storageConditions",
"storageTemperatureLimits",
"storageHumidityLimits"

})
@XmlRootElement(name = "StorageTransportationData", namespace = "")
public class StorageTransportationData {
	@XmlElement(name = "PowerSource", namespace = "") 
	 protected String powerSource;
	@XmlElement(name = "BatteryType", namespace = "") 
	 protected String batteryType;
	@XmlElement(name = "CustomerUnitLabeling", namespace = "") 
	 protected String customerUnitLabeling;
	@XmlElement(name = "ConsumerUnitLabeling", namespace = "") 
	 protected String consumerUnitLabeling;
	@XmlElement(name = "ShippingInformation", namespace = "") 
	 protected String shippingInformation;
	@XmlElement(name = "LabelingInformation", namespace = "") 
	 protected String labelingInformation;
	@XmlElement(name = "StorageConditions", namespace = "") 
	 protected String storageConditions;
	@XmlElement(name = "StorageTemperatureLimits", namespace = "") 
	 protected String storageTemperatureLimits;
	@XmlElement(name = "StorageHumidityLimits", namespace = "") 
	 protected String storageHumidityLimits;
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
	public String getCustomerUnitLabeling() {
		return customerUnitLabeling;
	}
	public void setCustomerUnitLabeling(String customerUnitLabeling) {
		this.customerUnitLabeling = customerUnitLabeling;
	}
	public String getConsumerUnitLabeling() {
		return consumerUnitLabeling;
	}
	public void setConsumerUnitLabeling(String consumerUnitLabeling) {
		this.consumerUnitLabeling = consumerUnitLabeling;
	}
	public String getShippingInformation() {
		return shippingInformation;
	}
	public void setShippingInformation(String shippingInformation) {
		this.shippingInformation = shippingInformation;
	}
	public String getLabelingInformation() {
		return labelingInformation;
	}
	public void setLabelingInformation(String labelingInformation) {
		this.labelingInformation = labelingInformation;
	}
	public String getStorageConditions() {
		return storageConditions;
	}
	public void setStorageConditions(String storageConditions) {
		this.storageConditions = storageConditions;
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
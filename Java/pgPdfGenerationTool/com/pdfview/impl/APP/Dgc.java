//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.04.10 at 10:09:18 AM EDT 
//

package com.pdfview.impl.APP;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"no",
"dgdescription",
"unnumber",
"properShippingName",
"hazardClass",
"packingGroup",
"limitedQuantitybyGround",
"unspecification",
"MaxConsumerUnitPart",
"maxCustomerUnitPart",
"otherPackagingRequirements",
"dgmarksforConsumerUnitPart",
"dgmarksforCustomerUnitPart"

})

public class Dgc {
	@XmlElement(name = "No", namespace = "") 
	 protected String no;
	@XmlElement(name = "Dgdescription", namespace = "") 
	 protected String dgdescription;
	@XmlElement(name = "Unnumber", namespace = "") 
	 protected String unnumber;
	@XmlElement(name = "ProperShippingName", namespace = "") 
	 protected String properShippingName;
	@XmlElement(name = "HazardClass", namespace = "") 
	 protected String hazardClass;
	@XmlElement(name = "PackingGroup", namespace = "") 
	 protected String packingGroup;
	@XmlElement(name = "LimitedQuantitybyGround", namespace = "") 
	 protected String limitedQuantitybyGround;
	@XmlElement(name = "Unspecification", namespace = "") 
	 protected String unspecification;
	@XmlElement(name = "MaxConsumerUnitPart", namespace = "") 
	 protected String MaxConsumerUnitPart;
	@XmlElement(name = "MaxCustomerUnitPart", namespace = "") 
	 protected String maxCustomerUnitPart;
	@XmlElement(name = "OtherPackagingRequirements", namespace = "") 
	 protected String otherPackagingRequirements;
	@XmlElement(name = "DgmarksforConsumerUnitPart", namespace = "") 
	 protected String dgmarksforConsumerUnitPart;
	@XmlElement(name = "DgmarksforCustomerUnitPart", namespace = "") 
	 protected String dgmarksforCustomerUnitPart;
	public String getNo() {
		return no;
	}
	public void setNo(String no) {
		this.no = no;
	}
	public String getDgdescription() {
		return dgdescription;
	}
	public void setDgdescription(String dgdescription) {
		this.dgdescription = dgdescription;
	}
	public String getUnnumber() {
		return unnumber;
	}
	public void setUnnumber(String unnumber) {
		this.unnumber = unnumber;
	}
	public String getProperShippingName() {
		return properShippingName;
	}
	public void setProperShippingName(String properShippingName) {
		this.properShippingName = properShippingName;
	}
	public String getHazardClass() {
		return hazardClass;
	}
	public void setHazardClass(String hazardClass) {
		this.hazardClass = hazardClass;
	}
	public String getPackingGroup() {
		return packingGroup;
	}
	public void setPackingGroup(String packingGroup) {
		this.packingGroup = packingGroup;
	}
	public String getLimitedQuantitybyGround() {
		return limitedQuantitybyGround;
	}
	public void setLimitedQuantitybyGround(String limitedQuantitybyGround) {
		this.limitedQuantitybyGround = limitedQuantitybyGround;
	}
	public String getUnspecification() {
		return unspecification;
	}
	public void setUnspecification(String unspecification) {
		this.unspecification = unspecification;
	}
	public String getMaxConsumerUnitPart() {
		return MaxConsumerUnitPart;
	}
	public void setMaxConsumerUnitPart(String maxConsumerUnitPart) {
		MaxConsumerUnitPart = maxConsumerUnitPart;
	}
	public String getMaxCustomerUnitPart() {
		return maxCustomerUnitPart;
	}
	public void setMaxCustomerUnitPart(String maxCustomerUnitPart) {
		this.maxCustomerUnitPart = maxCustomerUnitPart;
	}
	public String getOtherPackagingRequirements() {
		return otherPackagingRequirements;
	}
	public void setOtherPackagingRequirements(String otherPackagingRequirements) {
		this.otherPackagingRequirements = otherPackagingRequirements;
	}
	public String getDgmarksforConsumerUnitPart() {
		return dgmarksforConsumerUnitPart;
	}
	public void setDgmarksforConsumerUnitPart(String dgmarksforConsumerUnitPart) {
		this.dgmarksforConsumerUnitPart = dgmarksforConsumerUnitPart;
	}
	public String getDgmarksforCustomerUnitPart() {
		return dgmarksforCustomerUnitPart;
	}
	public void setDgmarksforCustomerUnitPart(String dgmarksforCustomerUnitPart) {
		this.dgmarksforCustomerUnitPart = dgmarksforCustomerUnitPart;
	}
	
}

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
@XmlType(name = "", propOrder = {"name",
"type",
"grossWeight",
"weightUoM",
"comments"

})
public class WeightCharacteristic {
	
	@XmlElement(name = "Name", namespace = "") 
	 protected String name;
	@XmlElement(name = "Type", namespace = "") 
	 protected String type;
	@XmlElement(name = "GrossWeight", namespace = "") 
	 protected String grossWeight;
	@XmlElement(name = "WeightUoM", namespace = "") 
	 protected String weightUoM;
	@XmlElement(name = "Comments", namespace = "") 
	 protected String comments;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getGrossWeight() {
		return grossWeight;
	}
	public void setGrossWeight(String grossWeight) {
		this.grossWeight = grossWeight;
	}
	public String getWeightUoM() {
		return weightUoM;
	}
	public void setWeightUoM(String weightUoM) {
		this.weightUoM = weightUoM;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	
}
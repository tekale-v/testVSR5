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
@XmlType(name = "", propOrder = { 
	"wDStatus", 
	"unitofMeasureSystem"
})
@XmlRootElement(name = "WeightsAndDimensions", namespace = "")
public class WeightsAndDimensions {

	@XmlElement(name = "WDStatus", namespace = "")
	protected String wDStatus;
	@XmlElement(name = "UnitofMeasureSystem", namespace = "")
	protected String unitofMeasureSystem;
	public String getwDStatus() {
		return wDStatus;
	}
	public void setwDStatus(String wDStatus) {
		this.wDStatus = wDStatus;
	}
	public String getUnitofMeasureSystem() {
		return unitofMeasureSystem;
	}
	public void setUnitofMeasureSystem(String unitofMeasureSystem) {
		this.unitofMeasureSystem = unitofMeasureSystem;
	}

	

}
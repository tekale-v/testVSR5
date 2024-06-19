//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.04.10 at 10:09:18 AM EDT 
//

package com.pdfview.impl.FPP;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "derivedFromPartsData", "derivedToPartsData" })
@XmlRootElement(name = "DerivedParts", namespace = "")
public class DerivedParts {

	@XmlElement(name = "DerivedFromPartsData", namespace = "")
	protected List<DerivedFromPartsData> derivedFromPartsData;
	@XmlElement(name = "DerivedToPartsData", namespace = "")
	protected List<DerivedToPartsData> derivedToPartsData;

	/**
	 * Gets the value of the rule property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot.
	 * Therefore any modification you make to the returned list will be present
	 * inside the JAXB object. This is why there is not a <CODE>set</CODE> method
	 * for the rule property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getRule().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link Ghs }
	 * 
	 * 
	 */

	public List<DerivedFromPartsData> getDerivedFromPartsData() {
		if (derivedFromPartsData == null) {
			derivedFromPartsData = new ArrayList<DerivedFromPartsData>();
		}
		return this.derivedFromPartsData;
	}

	public List<DerivedToPartsData> getDerivedToPartsData() {
		if (derivedToPartsData == null) {
			derivedToPartsData = new ArrayList<DerivedToPartsData>();
		}
		return this.derivedToPartsData;
	}

	public void setDerivedToPartsData(List<DerivedToPartsData> derivedToPartsData) {
		this.derivedToPartsData = derivedToPartsData;
	}

	public void setDerivedFromPartsData(List<DerivedFromPartsData> derivedFromPartsData) {
		this.derivedFromPartsData = derivedFromPartsData;
	}

}
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
@XmlType(name = "", propOrder = {
		"stabilityResult"
	})
@XmlRootElement(name = "StabilityResults", namespace = "")
public class StabilityResults {

	@XmlElement(name = "StabilityResult", namespace = "") 
	 protected List<StabilityResult> stabilityResult;

  
    public List<StabilityResult> getStabilityResults() {
    	 if(stabilityResult == null) {
    		 stabilityResult = new ArrayList<StabilityResult>();
         }
         return this.stabilityResult;
	}
}

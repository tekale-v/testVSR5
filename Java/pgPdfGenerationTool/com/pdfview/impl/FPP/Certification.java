//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.04.10 at 10:09:18 AM EDT 
//


package com.pdfview.impl.FPP;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"productPart",
"certificationClaim",
"country"
})
public class Certification {

	@XmlElement(name = "ProductPart", namespace = "") 
	 protected String productPart;
	@XmlElement(name = "CertificationClaim", namespace = "") 
	 protected String certificationClaim;
	@XmlElement(name = "Country", namespace = "") 
	 protected String country;
	public String getProductPart() {
		return productPart;
	}
	public void setProductPart(String productPart) {
		this.productPart = productPart;
	}
	public String getCertificationClaim() {
		return certificationClaim;
	}
	public void setCertificationClaim(String certificationClaim) {
		this.certificationClaim = certificationClaim;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	
	
	
}
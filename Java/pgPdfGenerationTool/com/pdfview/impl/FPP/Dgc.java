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

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="State" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Policy" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Owner" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Originator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Created" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Modified" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LeftExpressions" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="LeftExpression" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element ref="{http://www.pg.com/Rules_v1}Feature" minOccurs="0"/>
 *                             &lt;element ref="{http://www.pg.com/Rules_v1}Option" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Compatibility" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RightExpressions" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="RightExpression" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element ref="{http://www.pg.com/Rules_v1}Feature" minOccurs="0"/>
 *                             &lt;element ref="{http://www.pg.com/Rules_v1}Option" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "productPartName", 
		"productPartRevision", 
		"productPartTitle", 
		"dGDescription",
		"uNNumber", 
		"properShippingName", 
		"hazardClass", 
		"packingGroup", 
		"shippedLimitedQuantity",
		"uNSpecificationRequired", 
		"maxConsumerUnit", 
		"maxCustomerUnit", 
		"otherPackagingRequirements", 
		"dGConsumerUnit",
		"dGCustomerUnit" })
public class Dgc {
	@XmlElement(name = "ProductPartName", namespace = "")
	protected String productPartName;
	@XmlElement(name = "ProductPartRevision", namespace = "")
	protected String productPartRevision;
	@XmlElement(name = "ProductPartTitle", namespace = "")
	protected String productPartTitle;
	@XmlElement(name = "DGDescription", namespace = "")
	protected String dGDescription;
	@XmlElement(name = "UNNumber", namespace = "")
	protected String uNNumber;
	@XmlElement(name = "ProperShippingName", namespace = "")
	protected String properShippingName;
	@XmlElement(name = "HazardClass", namespace = "")
	protected String hazardClass;
	@XmlElement(name = "PackingGroup", namespace = "")
	protected String packingGroup;
	@XmlElement(name = "ShippedLimitedQuantity", namespace = "")
	protected String shippedLimitedQuantity;
	@XmlElement(name = "UNSpecificationRequired", namespace = "")
	protected String uNSpecificationRequired;
	@XmlElement(name = "MaxConsumerUnit", namespace = "")
	protected String maxConsumerUnit;
	@XmlElement(name = "MaxCustomerUnit", namespace = "")
	protected String maxCustomerUnit;
	@XmlElement(name = "OtherPackagingRequirements", namespace = "")
	protected String otherPackagingRequirements;
	@XmlElement(name = "DGConsumerUnit", namespace = "")
	protected String dGConsumerUnit;
	@XmlElement(name = "DGCustomerUnit", namespace = "")
	protected String dGCustomerUnit;

	public String getProductPartName() {
		return productPartName;
	}

	public void setProductPartName(String productPartName) {
		this.productPartName = productPartName;
	}

	public String getProductPartRevision() {
		return productPartRevision;
	}

	public void setProductPartRevision(String productPartRevision) {
		this.productPartRevision = productPartRevision;
	}

	public String getProductPartTitle() {
		return productPartTitle;
	}

	public void setProductPartTitle(String productPartTitle) {
		this.productPartTitle = productPartTitle;
	}

	public String getdGDescription() {
		return dGDescription;
	}

	public void setdGDescription(String dGDescription) {
		this.dGDescription = dGDescription;
	}

	public String getuNNumber() {
		return uNNumber;
	}

	public void setuNNumber(String uNNumber) {
		this.uNNumber = uNNumber;
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

	public String getShippedLimitedQuantity() {
		return shippedLimitedQuantity;
	}

	public void setShippedLimitedQuantity(String shippedLimitedQuantity) {
		this.shippedLimitedQuantity = shippedLimitedQuantity;
	}

	public String getuNSpecificationRequired() {
		return uNSpecificationRequired;
	}

	public void setuNSpecificationRequired(String uNSpecificationRequired) {
		this.uNSpecificationRequired = uNSpecificationRequired;
	}

	public String getMaxConsumerUnit() {
		return maxConsumerUnit;
	}

	public void setMaxConsumerUnit(String maxConsumerUnit) {
		this.maxConsumerUnit = maxConsumerUnit;
	}

	public String getMaxCustomerUnit() {
		return maxCustomerUnit;
	}

	public void setMaxCustomerUnit(String maxCustomerUnit) {
		this.maxCustomerUnit = maxCustomerUnit;
	}

	public String getOtherPackagingRequirements() {
		return otherPackagingRequirements;
	}

	public void setOtherPackagingRequirements(String otherPackagingRequirements) {
		this.otherPackagingRequirements = otherPackagingRequirements;
	}

	public String getdGConsumerUnit() {
		return dGConsumerUnit;
	}

	public void setdGConsumerUnit(String dGConsumerUnit) {
		this.dGConsumerUnit = dGConsumerUnit;
	}

	public String getdGCustomerUnit() {
		return dGCustomerUnit;
	}

	public void setdGCustomerUnit(String dGCustomerUnit) {
		this.dGCustomerUnit = dGCustomerUnit;
	}
}

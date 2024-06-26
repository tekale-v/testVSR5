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
@XmlType(name = "", propOrder = {
"Type"
})

public class ClassificationPowerView {
	@XmlElement(name = "Name", namespace = "") 
	 protected String name;
	@XmlElement(name = "Chg", namespace = "") 
	 protected String chg;
	@XmlElement(name = "FN", namespace = "") 
	 protected String fN;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getChg() {
		return chg;
	}
	public void setChg(String chg) {
		this.chg = chg;
	}
	public String getfN() {
		return fN;
	}
	public void setfN(String fN) {
		this.fN = fN;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSubstitution() {
		return substitution;
	}
	public void setSubstitution(String substitution) {
		this.substitution = substitution;
	}
	public String getQty() {
		return qty;
	}
	public void setQty(String qty) {
		this.qty = qty;
	}
	public String getBaseUnitofMeasure() {
		return baseUnitofMeasure;
	}
	public void setBaseUnitofMeasure(String baseUnitofMeasure) {
		this.baseUnitofMeasure = baseUnitofMeasure;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getPhase() {
		return phase;
	}
	public void setPhase(String phase) {
		this.phase = phase;
	}
	public String getRefDesOptional() {
		return refDesOptional;
	}
	public void setRefDesOptional(String refDesOptional) {
		this.refDesOptional = refDesOptional;
	}
	public String getComponents() {
		return components;
	}
	public void setComponents(String components) {
		this.components = components;
	}
	@XmlElement(name = "Title", namespace = "") 
	 protected String title;
	@XmlElement(name = "Type", namespace = "") 
	 protected String type;
	@XmlElement(name = "Substitution", namespace = "") 
	 protected String substitution;
	@XmlElement(name = "Qty", namespace = "") 
	 protected String qty;
	@XmlElement(name = "BaseUnitofMeasure", namespace = "") 
	 protected String baseUnitofMeasure;
	@XmlElement(name = "Comments", namespace = "") 
	 protected String comments;
	@XmlElement(name = "State", namespace = "") 
	 protected String state;
	@XmlElement(name = "Phase", namespace = "") 
	 protected String phase;
	@XmlElement(name = "RefDesOptional", namespace = "") 
	 protected String refDesOptional;
	@XmlElement(name = "Components", namespace = "") 
	 protected String components;	
	@XmlElement(name = "Revision", namespace = "") 
	 protected String revision;	
	@XmlElement(name = "ReleaseDate", namespace = "") 
	 protected String releaseDate;
	public String getRevision() {
		return revision;
	}
	public void setRevision(String revision) {
		this.revision = revision;
	}
	public String getReleaseDate() {
		return releaseDate;
	}
	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}	
}

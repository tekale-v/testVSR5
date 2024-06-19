/*
Java File Name: DetailedElement
Clone From/Reference: NA
Purpose:  This File is used for XML Binding(JAXB) Reference Implementation
*/
package com.pdfview.combinedelement.definition;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "enoviaSelectable",
    "enoviaSelectableTarget",
    "enoviaExpandLevel",
    "enoviaHelperMethod",
    "jaxbElementName",
    "subcomponent",
    "elementIsADate",
    "accessExpression"
})
@XmlRootElement(name = "detailedElement")
public class DetailedElement {

    @XmlElement(name = "EnoviaSelectable", required = true)
    protected String enoviaSelectable;
    @XmlElement(name = "EnoviaSelectableTarget", required = true)
    protected String enoviaSelectableTarget;
    @XmlElement(name = "EnoviaExpandLevel", required = true)
    protected String enoviaExpandLevel;
    @XmlElement(name = "EnoviaHelperMethod", required = true)
    protected String enoviaHelperMethod;
    @XmlElement(required = true)
    protected String jaxbElementName;
    @XmlElement(required = true)
    protected String subcomponent;
    @XmlElement(required = true)
    protected String elementIsADate;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlElement(name = "AccessExpression", required = true)
    protected String accessExpression;

    /**
     * Gets the value of the enoviaTargetObjectSelectable property.
     * @return String
     */
    public String getEnoviaSelectable() {
        return enoviaSelectable;
    }

    /**
     * Sets the value of the enoviaSelectable property.
     * @param value String 
     */
    public void setEnoviaSelectable(String value) {
        this.enoviaSelectable = value;
    }

    /**
     * Gets the value of the enoviaSelectableTarget property.
     * @return String
     */
    public String getEnoviaSelectableTarget() {
        return enoviaSelectableTarget;
    }

    /**
     * Sets the value of the enoviaSelectableTarget property.
     * @param value String 
     */
    public void setEnoviaSelectableTarget(String value) {
        this.enoviaSelectableTarget = value;
    }

    /**
     * Gets the value of the enoviaExpandLevel property.
     * @return String
     */
    public String getEnoviaExpandLevel() {
        return enoviaExpandLevel;
    }

    /**
     * Sets the value of the enoviaExpandLevel property.
     * @param value String 
     */
    public void setEnoviaExpandLevel(String value) {
        this.enoviaExpandLevel = value;
    }

    /**
     * Gets the value of the enoviaHelperMethod property.
     * @return String
     */
    public String getEnoviaHelperMethod() {
        return enoviaHelperMethod;
    }

    /**
     * Sets the value of the enoviaHelperMethod property.
     * @param value String 
     */
    public void setEnoviaHelperMethod(String value) {
        this.enoviaHelperMethod = value;
    }

    /**
     * Gets the value of the jaxbElementName property.
     * @return String
     */
    public String getJaxbElementName() {
        return jaxbElementName;
    }

    /**
     * Sets the value of the jaxbElementName property.
     * @param value String 
     */
    public void setJaxbElementName(String value) {
        this.jaxbElementName = value;
    }

    /**
     * Gets the value of the subcomponent property.
     * @return String
     */
    public String getSubcomponent() {
        return subcomponent;
    }

    /**
     * Sets the value of the subcomponent property.
     * @param value String 
     */
    public void setSubcomponent(String value) {
        this.subcomponent = value;
    }

    /**
     * Gets the value of the elementIsADate property.
     * @return String
     */
    public String getElementIsADate() {
        return elementIsADate;
    }

    /**
     * Sets the value of the elementIsADate property.
     * @param value String 
     */
    public void setElementIsADate(String value) {
        this.elementIsADate = value;
    }

    /**
     * Gets the value of the name property.
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * @param value String 
     */
    public void setName(String value) {
        this.name = value;
    }

	public String getAccessExpression() {
		return accessExpression;
	}

	public void setAccessExpression(String accessExpression) {
		this.accessExpression = accessExpression;
	}

}

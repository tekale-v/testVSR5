/*
Java File Name: Component
Clone From/Reference: NA
Purpose:  This File is used for XML Binding(JAXB) Reference Implementation
*/
package com.pdfview.combinedcomponent.definition;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;



@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "enoviaTargetObjectSelectable",
    "enoviaMultiValuedSelectable",
    "enoviaExpandRelationship",
    "enoviaExpandDirection",
    "enoviaExpandType",
    "enoviaExpandRecurseLevel",
    "jaxbclass",
    "enoviaHelperClass",
    "elements"
})
@XmlRootElement(name = "component")
public class Component {
	
    @XmlElement(name = "EnoviaTargetObjectSelectable", required = true)
    protected String enoviaTargetObjectSelectable;
    @XmlElement(name = "EnoviaMultiValuedSelectable", required = true)
    protected String enoviaMultiValuedSelectable;
    @XmlElement(name = "EnoviaExpandRelationship", required = true)
    protected String enoviaExpandRelationship;
    @XmlElement(name = "EnoviaExpandDirection", required = true)
    protected String enoviaExpandDirection;
    @XmlElement(name = "EnoviaExpandType", required = true)
    protected String enoviaExpandType;
    @XmlElement(name = "EnoviaExpandRecurseLevel", required = true)
    protected String enoviaExpandRecurseLevel;
    @XmlElement(required = true)
    protected String jaxbclass;
    @XmlElement(name = "EnoviaHelperClass", required = true)
    protected String enoviaHelperClass;
    @XmlElement(required = true)
    protected Elements elements;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "componentType")
    protected String componentType;
    @XmlAttribute(name = "hint")
    protected String hint;
    

    /**
     * Gets the value of the enoviaTargetObjectSelectable property.
     * @return String
     */
    public String getEnoviaTargetObjectSelectable() {
        return enoviaTargetObjectSelectable;
    }

    /**
     * Sets the value of the enoviaTargetObjectSelectable property.
     * @param value String 
     */
    public void setEnoviaTargetObjectSelectable(String value) {
        this.enoviaTargetObjectSelectable = value;
    }

    /**
     * Gets the value of the enoviaMultiValuedSelectable property.
     * @return String
     */
    public String getEnoviaMultiValuedSelectable() {
        return enoviaMultiValuedSelectable;
    }

    /**
     * Sets the value of the enoviaMultiValuedSelectable property.
     * @param value String 
     */
    public void setEnoviaMultiValuedSelectable(String value) {
        this.enoviaMultiValuedSelectable = value;
    }

    /**
     * Gets the value of the enoviaExpandRelationship property.
     * @return String
     */
    public String getEnoviaExpandRelationship() {
        return enoviaExpandRelationship;
    }
    /**
     * Sets the value of the enoviaExpandRelationship property.
     * @param value String 
     */
    public void setEnoviaExpandRelationship(String value) {
        this.enoviaExpandRelationship = value;
    }
    /**
     * Gets the value of the enoviaExpandDirection property.
     * @return String
     */
    public String getEnoviaExpandDirection() {
        return enoviaExpandDirection;
    }

    /**
     * Sets the value of the enoviaExpandDirection property.
     * @param value String 
     */
    public void setEnoviaExpandDirection(String value) {
        this.enoviaExpandDirection = value;
    }

    /**
     * Gets the value of the enoviaExpandType property.
     * @return String
     */
    public String getEnoviaExpandType() {
        return enoviaExpandType;
    }

    /**
     * Sets the value of the enoviaExpandType property.
     * @param value String 
     */    
    public void setEnoviaExpandType(String value) {
        this.enoviaExpandType = value;
    }
    /**
     * Gets the value of the enoviaExpandRecurseLevel property.
     * @return String
     */
    public String getEnoviaExpandRecurseLevel() {
        return enoviaExpandRecurseLevel;
    }

    /**
     * Sets the value of the enoviaExpandRecurseLevel property.
     * @param value String 
     */
    public void setEnoviaExpandRecurseLevel(String value) {
        this.enoviaExpandRecurseLevel = value;
    }

    /**
     * Gets the value of the jaxbclass property.
     * @return String
     */
    public String getJaxbclass() {
        return jaxbclass;
    }

    /**
     * Sets the value of the jaxbclass property.
     * @param value String 
     */
    public void setJaxbclass(String value) {
        this.jaxbclass = value;
    }

    /**
     * Gets the value of the enoviaHelperClass property.
     * @return String
     */
    public String getEnoviaHelperClass() {
        return enoviaHelperClass;
    }

    /**
     * Sets the value of the enoviaHelperClass property.
     * @param value String 
     */
    public void setEnoviaHelperClass(String value) {
        this.enoviaHelperClass = value;
    }

    /**
     * Gets the value of the elements property.
     * @return String
     */
    public Elements getElements() {
        return elements;
    }

    /**
     * Sets the value of the elements property.
     * @param value String 
     */
    public void setElements(Elements value) {
        this.elements = value;
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

    /**
     * Gets the value of the componentType property.
     * @return String
     */
    public String getComponentType() {
        return componentType;
    }

    /**
     * Sets the value of the componentType property.
     * @param value String 
     */
    public void setComponentType(String value) {
        this.componentType = value;
    }

    /**
     * Gets the value of the hint property.
     * @return String
     */
    public String getHint() {
        return hint;
    }
    
    /**
     * Sets the value of the hint property.
     * @param value String 
     */
    public void setHint(String value) {
        this.hint = value;
    }

}

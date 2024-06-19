/*
Java File Name: Components
Clone From/Reference: NA
Purpose:  This File is used for XML Binding(JAXB) Reference Implementation
*/
package com.pdfview.combinedcomponent.definition;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "elementConfigurationFiles",
    "enoviaVault",
    "component"
})
@XmlRootElement(name = "components")
public class Components {

    @XmlElement(required = true)
    protected String elementConfigurationFiles;
    @XmlElement(required = true)
    protected String enoviaVault;
    @XmlElement(required = true)
    protected List<Component> component;

    /**
     * Gets the value of the elementConfigurationFiles property.
     * @return String
     */
    public String getElementConfigurationFiles() {
        return elementConfigurationFiles;
    }

    /**
     * Sets the value of the elementConfigurationFiles property.
     * @param value String 
     */
    public void setElementConfigurationFiles(String value) {
        this.elementConfigurationFiles = value;
    }

    
    /**
     * Gets the value of the enoviaVault property.
     * @return String
     */
    public String getEnoviaVault() {
        return enoviaVault;
    }

    /**
     * Sets the value of the enoviaVault property.
     * @param value String 
     */
    public void setEnoviaVault(String value) {
        this.enoviaVault = value;
    }

    /**
     * Gets the value of the component property.
     * @return List
     */
    public List<Component> getComponent() {
        if (component == null) {
            component = new ArrayList<Component>();
        }
        return this.component;
    }

}

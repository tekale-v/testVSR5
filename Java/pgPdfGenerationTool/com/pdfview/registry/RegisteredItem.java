/*
Java File Name: RegisteredItem
Clone From/Reference: NA
Purpose: This file is used to set & get Helper Class
*/

package com.pdfview.registry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "configurationPacketName",
    "xsdFile",
    "componentConfigurationFile",
    "detailedElementConfigurationFile",
    "jaxbCLass",
    "modifiedDate"
})
@XmlRootElement(name = "RegisteredItem")
public class RegisteredItem {

    @XmlElement(name = "ConfigurationPacketName", required = true)
    protected String configurationPacketName;
    @XmlElement(name = "XSDFile", required = true)
    protected String xsdFile;
    @XmlElement(name = "ComponentConfigurationFile", required = true)
    protected String componentConfigurationFile;
    @XmlElement(name = "DetailedElementConfigurationFile", required = true)
    protected String detailedElementConfigurationFile;
    @XmlElement(name = "JaxbCLass", required = true)
    protected String jaxbCLass;
    @XmlElement(name = "ModifiedDate", required = true)
    protected String modifiedDate;
   
    /**
     * Gets the value of the configurationPacketName property.
     * @return String
     */
    public String getConfigurationPacketName() {
        return configurationPacketName;
    }

    
   /**
    * Sets the value of the configurationPacketName property.
    * @param value String 
    */
    public void setConfigurationPacketName(String value) {
        this.configurationPacketName = value;
    }

    /**
     * Gets the value of the xsdFile property.
     * @return String
     */
    public String getXSDFile() {
        return xsdFile;
    }

    /**
     * Sets the value of the xsdFile property.
     * @param value String 
     */
    public void setXSDFile(String value) {
        this.xsdFile = value;
    }

    /**
     * Gets the value of the componentConfigurationFile property.
     * @return String
     */
    public String getComponentConfigurationFile() {
        return componentConfigurationFile;
    }

    /**
     * Sets the value of the componentConfigurationFile property.
     * @param value String 
     */
    public void setComponentConfigurationFile(String value) {
        this.componentConfigurationFile = value;
    }

    /**
     * Gets the value of the detailedElementConfigurationFile property.
     * @return String
     */
    public String getDetailedElementConfigurationFile() {
        return detailedElementConfigurationFile;
    }

    /**
     * Sets the value of the detailedElementConfigurationFile property.
     * @param value String 
     */
    public void setDetailedElementConfigurationFile(String value) {
        this.detailedElementConfigurationFile = value;
    }

    /**
     * Gets the value of the jaxbCLass property.
     * @return String
     */
    public String getJaxbCLass() {
        return jaxbCLass;
    }

    /**
     * Sets the value of the jaxbCLass property.
     * @param value String 
     */
    public void setJaxbCLass(String value) {
        this.jaxbCLass = value;
    }

    /**
     * Gets the value of the modifiedDate property.
     * @return String
     */
    public String getModifiedDate() {
        return modifiedDate;
    }

    /**
     * Sets the value of the modifiedDate property.
     * @param value String 
     */
    public void setModifiedDate(String value) {
        this.modifiedDate = value;
    }

    
}

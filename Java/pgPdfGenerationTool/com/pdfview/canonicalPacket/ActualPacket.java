/*
Java File Name: ActualPacket
Clone From/Reference: NA
Purpose:  This File is used for XML Binding(JAXB) Reference Implementation
*/

package com.pdfview.canonicalPacket;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "actualPacketName",
    "objectTypes"
})
@XmlRootElement(name = "ActualPacket")
public class ActualPacket {

    @XmlElement(name = "ActualPacketName", required = true)
    protected String actualPacketName;
    @XmlElement(name = "ObjectTypes", required = true)
    protected ObjectTypes objectTypes;

    /**
     * Gets the value of the actualPacketName property.
     * @return String
     */
    public String getActualPacketName() {
        return actualPacketName;
    }
    /**
     * Sets the value of the actualPacketName property.
     * @param value String 
     */
    public void setActualPacketName(String value) {
        this.actualPacketName = value;
    }
    /**
     * Gets the value of the ObjectTypes property.
     * @return String
     */
    public ObjectTypes getObjectTypes() {
        return objectTypes;
    }
    /**
     * Sets the value of the objectTypes property.
     * @param value String 
     */
    public void setObjectTypes(ObjectTypes value) {
        this.objectTypes = value;
    }

}

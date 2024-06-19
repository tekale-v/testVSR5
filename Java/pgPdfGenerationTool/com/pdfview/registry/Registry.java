/*
Java File Name: Registry
Clone From/Reference: NA
Purpose: This file is used for XML Binding(JAXB) Reference Implementation
*/

package com.pdfview.registry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "registeredItems"
})
@XmlRootElement(name = "Registry")
public class Registry {

    @XmlElement(name = "RegisteredItems", required = true)
    protected RegisteredItems registeredItems;

    /**
     * Gets the value of the registeredItems property.
     * @return RegisteredItems
     */
    public RegisteredItems getRegisteredItems() {
        return registeredItems;
    }

    /**
     * Sets the value of the registeredItems property.
     * @param value RegisteredItems 
     */
    public void setRegisteredItems(RegisteredItems value) {
        this.registeredItems = value;
    }

}

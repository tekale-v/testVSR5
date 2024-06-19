/*Java File Name: RegisteredItems
Clone From/Reference: NA
Purpose: This file is used for XML Binding(JAXB) Reference Implementation
*/
package com.pdfview.registry;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "registeredItem"
})
@XmlRootElement(name = "RegisteredItems")
public class RegisteredItems {

    @XmlElement(name = "RegisteredItem")
    protected List<RegisteredItem> registeredItem;
    /**
     * Gets the value of the registeredItem property.
     * @return List
     */
    public List<RegisteredItem> getRegisteredItem() {
        if (registeredItem == null) {
            registeredItem = new ArrayList<RegisteredItem>();
        }
        return this.registeredItem;
    }

}

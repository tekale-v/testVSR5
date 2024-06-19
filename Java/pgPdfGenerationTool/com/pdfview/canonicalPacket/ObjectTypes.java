/*
Java File Name: ObjectTypes
Clone From/Reference: NA
Purpose:  This File is used for XML Binding(JAXB) Reference Implementation
*/
package com.pdfview.canonicalPacket;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "objectType"
})
@XmlRootElement(name = "ObjectTypes")
public class ObjectTypes {

    @XmlElement(name = "ObjectType")
    protected List<String> objectType;

   /**
     * Gets the value of the objectType property.
     * @return List
     */
    public List<String> getObjectType() {
        if (objectType == null) {
            objectType = new ArrayList<String>();
        }
        return this.objectType;
    }

}

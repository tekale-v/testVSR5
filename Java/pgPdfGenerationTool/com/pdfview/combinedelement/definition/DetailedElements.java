/*
Java File Name: DetailedElements
Clone From/Reference: NA
Purpose:  This File is used for XML Binding(JAXB) Reference Implementation
*/
package com.pdfview.combinedelement.definition;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "detailedElement"
})
@XmlRootElement(name = "detailedElements")
public class DetailedElements {

    @XmlElement(required = true)
    protected List<DetailedElement> detailedElement;
    
    /**
     * Gets the value of the detailedElement property.
     * @return List
     */
    public List<DetailedElement> getDetailedElement() {
        if (detailedElement == null) {
            detailedElement = new ArrayList<DetailedElement>();
        }
        return this.detailedElement;
    }

}

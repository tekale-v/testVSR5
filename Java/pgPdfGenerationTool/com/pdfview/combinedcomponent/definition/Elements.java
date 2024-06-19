/*
Java File Name: Elements
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
    "element"
})
@XmlRootElement(name = "elements")
public class Elements {

    @XmlElement(required = true)
    protected List<String> element;
    /**
     * Gets the value of the element property.
     * @return List
     */
    public List<String> getElement() {
        if (element == null) {
            element = new ArrayList<String>();
        }
        return this.element;
    }
	public void setElement(List<String> element) {
		this.element = element;
	}

}

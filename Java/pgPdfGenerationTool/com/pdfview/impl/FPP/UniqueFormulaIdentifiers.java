//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.04.10 at 10:09:18 AM EDT 
//


package com.pdfview.impl.FPP;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "uniqueFormulaIdentifier"
})
@XmlRootElement(name = "UniqueFormulaIdentifiers", namespace = "")
public class UniqueFormulaIdentifiers {

    @XmlElement(name = "UniqueFormulaIdentifier", namespace = "")
    protected List<UniqueFormulaIdentifier> uniqueFormulaIdentifier;
    /**
     * Gets the value of the rule property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rule property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRule().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Ghs }
     * 
     * 
     */
    public List<UniqueFormulaIdentifier> getUniqueFormulaIdentifier() {
        if(uniqueFormulaIdentifier == null) {
        	uniqueFormulaIdentifier = new ArrayList<UniqueFormulaIdentifier>();
        }
        return this.uniqueFormulaIdentifier;
    }
    

}

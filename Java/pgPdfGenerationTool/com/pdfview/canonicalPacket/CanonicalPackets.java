/*
Java File Name: CanonicalPackets
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
    "canonicalPacket"
})
@XmlRootElement(name = "CanonicalPackets")
public class CanonicalPackets {

    @XmlElement(name = "CanonicalPacket")
    protected List<CanonicalPacket> canonicalPacket;

    /**
     * Gets the value of the canonicalPacket property.
     * @return String
     */
    public List<CanonicalPacket> getCanonicalPacket() {
        if (canonicalPacket == null) {
            canonicalPacket = new ArrayList<CanonicalPacket>();
        }
        return this.canonicalPacket;
    }

}

/*
Java File Name: ActualPackets
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
    "actualPacket"
})
@XmlRootElement(name = "ActualPackets")
public class ActualPackets {

    @XmlElement(name = "ActualPacket")
    protected List<ActualPacket> actualPacket;
    /**
     * Gets the value of the actualPacket property.
     * @return List
     */
    public List<ActualPacket> getActualPacket() {
        if (actualPacket == null) {
            actualPacket = new ArrayList<ActualPacket>();
        }
        return this.actualPacket;
    }

}

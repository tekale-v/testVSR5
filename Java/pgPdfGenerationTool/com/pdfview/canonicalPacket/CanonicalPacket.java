/*
Java File Name: CanonicalPacket
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
    "canonicalPacketName",
    "packetTarget",
    "enoviaVault",
    "actualPackets",
    "authApplicationUser",
    "enableEmail",
    "normalizeResponse"
})
@XmlRootElement(name = "CanonicalPacket")
public class CanonicalPacket {

    @XmlElement(name = "CanonicalPacketName", required = true)
    protected String canonicalPacketName;
    @XmlElement(name = "PacketTarget", required = true)
    protected String packetTarget;
    @XmlElement(name = "EnoviaVault", required = true)
    protected String enoviaVault;
    @XmlElement(name = "ActualPackets", required = true)
    protected ActualPackets actualPackets;
    @XmlElement(name = "AuthApplicationUser", required = true)
    protected String authApplicationUser;
    @XmlElement(name = "EnableEmail", required = true)
    protected String enableEmail;
    @XmlElement(name = "NormalizeResponse", required = true)
    protected String normalizeResponse;

    /**
     * Gets the value of the canonicalPacketName property.
     * @return String
     */
    public String getCanonicalPacketName() {
        return canonicalPacketName;
    }

    /**
     * Sets the value of the canonicalPacketName property.
     * @param value String 
     */
    public void setCanonicalPacketName(String value) {
        this.canonicalPacketName = value;
    }
    /**
     * Gets the value of the packetTarget property.
     * @return String
     */
    public String getPacketTarget() {
        return packetTarget;
    }

    /**
     * Sets the value of the packetTarget property.
     * @param value String 
     */
    public void setPacketTarget(String value) {
        this.packetTarget = value;
    }
    /**
     * Gets the value of the enoviaVault property.
     * @return String
     */
    public String getEnoviaVault() {
        return enoviaVault;
    }

    /**
     * Sets the value of the enoviaVault property.
     * @param value String 
     */
    public void setEnoviaVault(String value) {
        this.enoviaVault = value;
    }
    /**
     * Gets the value of the actualPackets property.
     * @return String
     */
    public ActualPackets getActualPackets() {
        return actualPackets;
    }

    /**
     * Sets the value of the actualPackets property.
     * @param value String 
     */
    public void setActualPackets(ActualPackets value) {
        this.actualPackets = value;
    }
    /**
     * Gets the value of the authApplicationUser property.
     * @return String
     */
    public String getAuthApplicationUser() {
        return authApplicationUser;
    }
    /**
     * Sets the value of the authApplicationUser property.
     * @param value String 
     */
    public void setAuthApplicationUser(String value) {
        this.authApplicationUser = value;
    }
    /**
     * Gets the value of the enableEmail property.
     * @return String
     */
    public String getEnableEmail() {
        return enableEmail;
    }
    /**
     * Sets the value of the enableEmail property.
     * @param value String 
     */
    public void setEnableEmail(String value) {
        this.enableEmail = value;
    }
    /**
     * Gets the value of the normalizeResponse property.
     * @return String
     */
	public String getNormalizeResponse() {
		return normalizeResponse;
	}
	/**
     * Sets the value of the normalizeResponse property.
     * @param value String 
     */
	public void setNormalizeResponse(String normalizeResponse) {
		this.normalizeResponse = normalizeResponse;
	}

}

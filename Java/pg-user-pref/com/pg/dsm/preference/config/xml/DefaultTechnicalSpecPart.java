package com.pg.dsm.preference.config.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "defaultPlant",
        "defaultSharingMember"
})
@XmlRootElement(name = "defaultTechnicalSpecPart")
public class DefaultTechnicalSpecPart {
    @XmlAttribute(name = "types")
    protected String types;
    @XmlElement(required = true, name = "defaultPlant")
    protected DefaultPlant defaultPlant;
    @XmlElement(required = true, name = "defaultSharingMember")
    protected DefaultSharingMember defaultSharingMember;

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public DefaultPlant getDefaultPlant() {
        return defaultPlant;
    }

    public void setDefaultPlant(DefaultPlant defaultPlant) {
        this.defaultPlant = defaultPlant;
    }

    public DefaultSharingMember getDefaultSharingMember() {
        return defaultSharingMember;
    }

    public void setDefaultSharingMember(DefaultSharingMember defaultSharingMember) {
        this.defaultSharingMember = defaultSharingMember;
    }

    @Override
    public String toString() {
        return "DefaultTechnicalSpecPart{" +
                "types='" + types + '\'' +
                ", defaultPlant=" + defaultPlant +
                ", defaultSharingMember=" + defaultSharingMember +
                '}';
    }
}
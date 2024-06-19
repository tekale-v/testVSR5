package com.pg.dsm.preference.config.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "defaultSharingMember"
})
@XmlRootElement(name = "defaultCreatePartFamily")
public class DefaultCreatePartFamily {
    @XmlAttribute(name = "types")
    protected String types;
    @XmlElement(required = true, name = "defaultSharingMember")
    protected DefaultSharingMember defaultSharingMember;

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public DefaultSharingMember getDefaultSharingMember() {
        return defaultSharingMember;
    }

    public void setDefaultSharingMember(DefaultSharingMember defaultSharingMember) {
        this.defaultSharingMember = defaultSharingMember;
    }

    @Override
    public String toString() {
        return "DefaultPartFamily{" +
                "types='" + types + '\'' +
                ", defaultSharingMember=" + defaultSharingMember +
                '}';
    }
}

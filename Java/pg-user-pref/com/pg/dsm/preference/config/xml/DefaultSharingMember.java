package com.pg.dsm.preference.config.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        ""
})
@XmlRootElement(name = "defaultSharingMember")
public class DefaultSharingMember {
    @XmlAttribute(name = "allowedTypes")
    protected String allowedTypes;

    @XmlAttribute(name = "allowedPolicies")
    protected String allowedPolicies;

    public String getAllowedTypes() {
        return allowedTypes;
    }

    public void setAllowedTypes(String allowedTypes) {
        this.allowedTypes = allowedTypes;
    }

    public String getAllowedPolicies() {
        return allowedPolicies;
    }

    public void setAllowedPolicies(String allowedPolicies) {
        this.allowedPolicies = allowedPolicies;
    }

    @Override
    public String toString() {
        return "DefaultSharingMember{" +
                "allowedTypes='" + allowedTypes + '\'' +
                ", allowedPolicies='" + allowedPolicies + '\'' +
                '}';
    }
}

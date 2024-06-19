package com.pg.dsm.preference.config.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "businessUseClass",
        "highlyRestrictedClass",
        "primaryOrg"
})
@XmlRootElement(name = "ipSecurityControlPreference")
public class IPSecurityControlPreference {
    @XmlElement(required = true, name = "businessUseClass")
    protected BusinessUseClass businessUseClass;
    @XmlElement(required = true, name = "highlyRestrictedClass")
    protected HighlyRestrictedClass highlyRestrictedClass;
    @XmlElement(required = true, name = "primaryOrg")
    protected PrimaryOrg primaryOrg;

    public BusinessUseClass getBusinessUseClass() {
        return businessUseClass;
    }

    public void setBusinessUseClass(BusinessUseClass businessUseClass) {
        this.businessUseClass = businessUseClass;
    }

    public HighlyRestrictedClass getHighlyRestrictedClass() {
        return highlyRestrictedClass;
    }

    public void setHighlyRestrictedClass(HighlyRestrictedClass highlyRestrictedClass) {
        this.highlyRestrictedClass = highlyRestrictedClass;
    }

    public PrimaryOrg getPrimaryOrg() {
        return primaryOrg;
    }

    public void setPrimaryOrg(PrimaryOrg primaryOrg) {
        this.primaryOrg = primaryOrg;
    }

    @Override
    public String toString() {
        return "IPSecurityControlPreference{" +
                "businessUseClass=" + businessUseClass +
                ", highlyRestrictedClass=" + highlyRestrictedClass +
                ", primaryOrg=" + primaryOrg +
                '}';
    }
}

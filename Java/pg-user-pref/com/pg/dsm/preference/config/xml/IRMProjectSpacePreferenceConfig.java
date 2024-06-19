package com.pg.dsm.preference.config.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "businessArea"
})
@XmlRootElement(name = "irmProjectSpacePreferenceConfig")
public class IRMProjectSpacePreferenceConfig {
    @XmlAttribute(name = "types")
    protected String types;
    @XmlElement(required = true, name = "businessArea")
    protected BusinessArea businessArea;

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public BusinessArea getBusinessArea() {
        return businessArea;
    }

    public void setBusinessArea(BusinessArea businessArea) {
        this.businessArea = businessArea;
    }

    @Override
    public String toString() {
        return "IRMProjectSpacePreferenceConfig{" +
                "types='" + types + '\'' +
                ", businessArea=" + businessArea +
                '}';
    }
}

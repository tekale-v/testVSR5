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
@XmlRootElement(name = "productComplianceRequired")
public class ProductComplianceRequired {
    @XmlAttribute(name = "allowedTypes")
    protected String allowedTypes;
    @XmlAttribute(name = "show")
    boolean show;

    public String getAllowedTypes() {
        return allowedTypes;
    }

    public void setAllowedTypes(String allowedTypes) {
        this.allowedTypes = allowedTypes;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    @Override
    public String toString() {
        return "ProductComplianceRequired{" +
                "allowedTypes='" + allowedTypes + '\'' +
                ", show=" + show +
                '}';
    }
}

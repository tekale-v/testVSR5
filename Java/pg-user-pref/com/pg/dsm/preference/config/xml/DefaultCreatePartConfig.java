package com.pg.dsm.preference.config.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "defaultEquivalentPart",
        "defaultProductPart",
        "defaultTechnicalSpecPart",
})
@XmlRootElement(name = "defaultCreatePartConfig")
public class DefaultCreatePartConfig {
    @XmlElement(required = true, name = "defaultEquivalentPart")
    protected DefaultEquivalentPart defaultEquivalentPart;
    @XmlElement(required = true, name = "defaultProductPart")
    protected DefaultProductPart defaultProductPart;
    @XmlElement(required = true, name = "defaultTechnicalSpecPart")
    protected DefaultTechnicalSpecPart defaultTechnicalSpecPart;

    public DefaultEquivalentPart getDefaultEquivalentPart() {
        return defaultEquivalentPart;
    }

    public void setDefaultEquivalentPart(DefaultEquivalentPart defaultEquivalentPart) {
        this.defaultEquivalentPart = defaultEquivalentPart;
    }

    public DefaultProductPart getDefaultProductPart() {
        return defaultProductPart;
    }

    public void setDefaultProductPart(DefaultProductPart defaultProductPart) {
        this.defaultProductPart = defaultProductPart;
    }

    public DefaultTechnicalSpecPart getDefaultTechnicalSpecPart() {
        return defaultTechnicalSpecPart;
    }

    public void setDefaultTechnicalSpecPart(DefaultTechnicalSpecPart defaultTechnicalSpecPart) {
        this.defaultTechnicalSpecPart = defaultTechnicalSpecPart;
    }

    @Override
    public String toString() {
        return "DefaultCreatePartConfig{" +
                "defaultEquivalentPart=" + defaultEquivalentPart +
                ", defaultProductPart=" + defaultProductPart +
                ", defaultTechnicalSpecPart=" + defaultTechnicalSpecPart +
                '}';
    }
}

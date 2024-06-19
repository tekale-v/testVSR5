package com.pg.dsm.preference.config.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "defaultAuthorizedToUse",
        "defaultAuthorizedToProduce",
        "defaultAuthorizedToView",
        "defaultActivated"
})
@XmlRootElement(name = "defaultPlant")
public class DefaultPlant {
    @XmlAttribute(name = "allowedTypes")
    protected String allowedTypes;
    @XmlElement(required = true, name = "defaultAuthorizedToUse")
    protected DefaultAuthorizedToUse defaultAuthorizedToUse;
    @XmlElement(required = true, name = "defaultAuthorizedToProduce")
    protected DefaultAuthorizedToProduce defaultAuthorizedToProduce;
    @XmlElement(required = true, name = "defaultAuthorizedToView")
    protected DefaultAuthorizedToView defaultAuthorizedToView;
    @XmlElement(required = true, name = "defaultActivated")
    protected DefaultActivated defaultActivated;

    public String getAllowedTypes() {
        return allowedTypes;
    }

    public void setAllowedTypes(String allowedTypes) {
        this.allowedTypes = allowedTypes;
    }

    public DefaultAuthorizedToUse getDefaultAuthorizedToUse() {
        return defaultAuthorizedToUse;
    }

    public void setDefaultAuthorizedToUse(DefaultAuthorizedToUse defaultAuthorizedToUse) {
        this.defaultAuthorizedToUse = defaultAuthorizedToUse;
    }

    public DefaultAuthorizedToProduce getDefaultAuthorizedToProduce() {
        return defaultAuthorizedToProduce;
    }

    public void setDefaultAuthorizedToProduce(DefaultAuthorizedToProduce defaultAuthorizedToProduce) {
        this.defaultAuthorizedToProduce = defaultAuthorizedToProduce;
    }

    public DefaultAuthorizedToView getDefaultAuthorizedToView() {
        return defaultAuthorizedToView;
    }

    public void setDefaultAuthorizedToView(DefaultAuthorizedToView defaultAuthorizedToView) {
        this.defaultAuthorizedToView = defaultAuthorizedToView;
    }

    public DefaultActivated getDefaultActivated() {
        return defaultActivated;
    }

    public void setDefaultActivated(DefaultActivated defaultActivated) {
        this.defaultActivated = defaultActivated;
    }

    @Override
    public String toString() {
        return "DefaultPlant{" +
                "allowedTypes='" + allowedTypes + '\'' +
                ", defaultAuthorizedToUse=" + defaultAuthorizedToUse +
                ", defaultAuthorizedToProduce=" + defaultAuthorizedToProduce +
                ", defaultAuthorizedToView=" + defaultAuthorizedToView +
                ", defaultActivated=" + defaultActivated +
                '}';
    }
}

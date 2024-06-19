package com.pg.dsm.preference.config.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "defaultPlantAttributes"
})
@XmlRootElement(name = "defaultAuthorizedToView")
public class DefaultAuthorizedToView {
    @XmlAttribute(name = "name")
    protected String name;
    @XmlElement(required = true, name = "defaultPlantAttribute")
    protected List<DefaultPlantAttribute> defaultPlantAttributes;

    public List<DefaultPlantAttribute> getDefaultPlantAttributes() {
        if (defaultPlantAttributes == null) {
            defaultPlantAttributes = new ArrayList<>();
        }
        return this.defaultPlantAttributes;
    }

    public void setDefaultPlantAttributes(List<DefaultPlantAttribute> defaultPlantAttributes) {
        this.defaultPlantAttributes = defaultPlantAttributes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "DefaultAuthorizedToView{" +
                "name='" + name + '\'' +
                ", defaultPlantAttributes=" + defaultPlantAttributes +
                '}';
    }
}

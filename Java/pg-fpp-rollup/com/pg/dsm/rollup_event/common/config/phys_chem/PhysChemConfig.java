package com.pg.dsm.rollup_event.common.config.phys_chem;

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
        "physChemAttribute"
})
@XmlRootElement(name = "physChemConfig")
public class PhysChemConfig {
    @XmlAttribute(name = "expandType")
    protected String expandType;
    @XmlAttribute(name = "expandRelationship")
    protected String expandRelationship;
    @XmlAttribute(name = "expandLevel")
    protected short expandLevel;
    @XmlElement(required = true)
    List<PhysChemAttribute> physChemAttribute;

    public List<PhysChemAttribute> getPhysChemAttributes() {
        if (physChemAttribute == null) {
            physChemAttribute = new ArrayList<>();
        }
        return this.physChemAttribute;
    }

    public String getExpandType() {
        return expandType;
    }

    public void setExpandType(String expandType) {
        this.expandType = expandType;
    }

    public String getExpandRelationship() {
        return expandRelationship;
    }

    public void setExpandRelationship(String expandRelationship) {
        this.expandRelationship = expandRelationship;
    }

    public short getExpandLevel() {
        return expandLevel;
    }

    public void setExpandLevel(short expandLevel) {
        this.expandLevel = expandLevel;
    }
}

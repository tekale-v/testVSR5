/*
 **   MaterialGroups.java
 **   Description - Introduced as part June CW 2022 for Material Group Code (MGC) - Requirement (39763, 39765, 39767, 39764)
 **   About - Bean class.
 **
 */
package com.pg.dsm.sap.mgc.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "materialGroup",
        "inclusionType"
})
@XmlRootElement(name = "materialGroups")
public class MaterialGroups {
    @XmlAttribute(name = "applicableTypes")
    protected String applicableTypes;
    @XmlElement(required = true)
    protected List<MaterialGroup> materialGroup;
    @XmlElement(required = true)
    protected List<InclusionType> inclusionType;

    public String getApplicableTypes() {
        return applicableTypes;
    }

    public void setApplicableTypes(String applicableTypes) {
        this.applicableTypes = applicableTypes;
    }

    public List<MaterialGroup> getMaterialGroup() {
        if (materialGroup == null) {
            materialGroup = new ArrayList<>();
        }
        return this.materialGroup;
    }

    public List<InclusionType> getInclusionType() {
        if (inclusionType == null) {
            inclusionType = new ArrayList<>();
        }
        return this.inclusionType;
    }

    public void setInclusionType(List<InclusionType> inclusionType) {
        this.inclusionType = inclusionType;
    }

    public void setMaterialGroup(List<MaterialGroup> materialGroup) {
        this.materialGroup = materialGroup;
    }
}

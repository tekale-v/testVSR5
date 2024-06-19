/*
 **   MaterialGroup.java
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

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "classType",
        "subClass",
        "reportFunction",
        "packagingComponentType",
        "packagingMaterialType",
        "packagingTechnology",
        "primaryOrganization", // Added by DSM (Sogeti) 22x.04 for REQ 48068
        "chemicalGroup",
        "cas"
})
@XmlRootElement(name = "materialGroup")
public class MaterialGroup {
    @XmlElement(required = true)
    protected String classType;
    @XmlElement(required = true)
    protected String subClass;
    @XmlElement(required = true)
    protected String reportFunction;
    @XmlElement(required = true)
    protected String packagingComponentType;
    @XmlElement(required = true)
    protected String packagingMaterialType;
    @XmlElement(required = true)
    protected String packagingTechnology;
    @XmlElement(required = true)          // Added by DSM (Sogeti) 22x.04 for REQ 48068
    protected String primaryOrganization; // Added by DSM (Sogeti) 22x.04 for REQ 48068
    @XmlElement(required = true)
    protected String chemicalGroup;
    @XmlElement(required = true)
    protected String cas;
    @XmlAttribute(name = "code")
    protected String code;

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public String getSubClass() {
        return subClass;
    }

    public void setSubClass(String subClass) {
        this.subClass = subClass;
    }

    public String getReportFunction() {
        return reportFunction;
    }

    public void setReportFunction(String reportFunction) {
        this.reportFunction = reportFunction;
    }

    public String getPackagingComponentType() {
        return packagingComponentType;
    }

    public void setPackagingComponentType(String packagingComponentType) {
        this.packagingComponentType = packagingComponentType;
    }

    public String getPackagingMaterialType() {
        return packagingMaterialType;
    }

    public void setPackagingMaterialType(String packagingMaterialType) {
        this.packagingMaterialType = packagingMaterialType;
    }

    public String getPackagingTechnology() {
        return packagingTechnology;
    }

    public void setPackagingTechnology(String packagingTechnology) {
        this.packagingTechnology = packagingTechnology;
    }

    public String getChemicalGroup() {
        return chemicalGroup;
    }

    public void setChemicalGroup(String chemicalGroup) {
        this.chemicalGroup = chemicalGroup;
    }

    public String getCas() {
        return cas;
    }

    public void setCas(String cas) {
        this.cas = cas;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPrimaryOrganization() { // Added by DSM (Sogeti) 22x.04 for REQ 48068
        return primaryOrganization;
    }

    public void setPrimaryOrganization(String primaryOrganization) { // Added by DSM (Sogeti) 22x.04 for REQ 48068
        this.primaryOrganization = primaryOrganization;
    }

    @Override
    public String toString() {
        return "MaterialGroup{" +
                "classType='" + classType + '\'' +
                ", subClass='" + subClass + '\'' +
                ", reportFunction='" + reportFunction + '\'' +
                ", packagingComponentType='" + packagingComponentType + '\'' +
                ", packagingMaterialType='" + packagingMaterialType + '\'' +
                ", packagingTechnology='" + packagingTechnology + '\'' +
                ", primaryOrganization='" + primaryOrganization + '\'' +
                ", chemicalGroup='" + chemicalGroup + '\'' +
                ", cas='" + cas + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}

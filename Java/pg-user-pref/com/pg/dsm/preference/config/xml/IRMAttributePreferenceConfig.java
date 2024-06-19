package com.pg.dsm.preference.config.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "irmTitle",
        "irmDescription",
        "irmPolicy",
        "irmClassification",
        "irmSecurityCategory",
        "irmRegion",
        "irmSharingMember",
        "businessArea"
})
@XmlRootElement(name = "irmAttributePreferenceConfig")
public class IRMAttributePreferenceConfig {
    @XmlAttribute(name = "types")
    protected String types;

    @XmlElement(required = true, name = "irmTitle")
    protected IRMTitle irmTitle;
    @XmlElement(required = true, name = "irmDescription")
    protected IRMDescription irmDescription;
    @XmlElement(required = true, name = "irmPolicy")
    protected IRMPolicy irmPolicy;
    @XmlElement(required = true, name = "irmClassification")
    protected IRMClassification irmClassification;
    @XmlElement(required = true, name = "irmSecurityCategory")
    protected IRMSecurityCategory irmSecurityCategory;
    @XmlElement(required = true, name = "irmRegion")
    protected IRMRegion irmRegion;
    @XmlElement(required = true, name = "irmSharingMember")
    protected IRMSharingMember irmSharingMember;

    @XmlElement(required = true, name = "businessArea")
    protected BusinessArea businessArea;

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public IRMTitle getIrmTitle() {
        return irmTitle;
    }

    public void setIrmTitle(IRMTitle irmTitle) {
        this.irmTitle = irmTitle;
    }

    public IRMDescription getIrmDescription() {
        return irmDescription;
    }

    public void setIrmDescription(IRMDescription irmDescription) {
        this.irmDescription = irmDescription;
    }

    public IRMPolicy getIrmPolicy() {
        return irmPolicy;
    }

    public void setIrmPolicy(IRMPolicy irmPolicy) {
        this.irmPolicy = irmPolicy;
    }

    public IRMClassification getIrmClassification() {
        return irmClassification;
    }

    public void setIrmClassification(IRMClassification irmClassification) {
        this.irmClassification = irmClassification;
    }

    public IRMSecurityCategory getIrmSecurityCategory() {
        return irmSecurityCategory;
    }

    public void setIrmSecurityCategory(IRMSecurityCategory irmSecurityCategory) {
        this.irmSecurityCategory = irmSecurityCategory;
    }

    public IRMRegion getIrmRegion() {
        return irmRegion;
    }

    public void setIrmRegion(IRMRegion irmRegion) {
        this.irmRegion = irmRegion;
    }

    public IRMSharingMember getIrmSharingMember() {
        return irmSharingMember;
    }

    public void setIrmSharingMember(IRMSharingMember irmSharingMember) {
        this.irmSharingMember = irmSharingMember;
    }

    public BusinessArea getBusinessArea() {
        return businessArea;
    }

    public void setBusinessArea(BusinessArea businessArea) {
        this.businessArea = businessArea;
    }

    @Override
    public String toString() {
        return "IRMAttributePreferenceConfig{" +
                "types='" + types + '\'' +
                ", irmTitle=" + irmTitle +
                ", irmDescription=" + irmDescription +
                ", irmPolicy=" + irmPolicy +
                ", irmClassification=" + irmClassification +
                ", irmSecurityCategory=" + irmSecurityCategory +
                ", irmRegion=" + irmRegion +
                ", irmSharingMember=" + irmSharingMember +
                ", businessArea=" + businessArea +
                '}';
    }
}

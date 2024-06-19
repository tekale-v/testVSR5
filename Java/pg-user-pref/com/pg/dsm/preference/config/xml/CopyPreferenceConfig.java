package com.pg.dsm.preference.config.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "copyPackagingSearchTypes",
        "copyProductSearchTypes",
        "copyRawMaterialSearchTypes",
        "copyTechnicalSpecSearchTypes"
})
@XmlRootElement(name = "copyPreferenceConfig")
public class CopyPreferenceConfig {
    @XmlElement(required = true, name = "copyPackagingSearchTypes")
    protected CopyPackagingSearchTypes copyPackagingSearchTypes;

    @XmlElement(required = true, name = "copyProductSearchTypes")
    protected CopyProductSearchTypes copyProductSearchTypes;

    @XmlElement(required = true, name = "copyRawMaterialSearchTypes")
    protected CopyRawMaterialSearchTypes copyRawMaterialSearchTypes;

    @XmlElement(required = true, name = "copyTechnicalSpecSearchTypes")
    protected CopyTechnicalSpecSearchTypes copyTechnicalSpecSearchTypes;

    public CopyPackagingSearchTypes getCopyPackagingSearchTypes() {
        return copyPackagingSearchTypes;
    }

    public void setCopyPackagingSearchTypes(CopyPackagingSearchTypes copyPackagingSearchTypes) {
        this.copyPackagingSearchTypes = copyPackagingSearchTypes;
    }

    public CopyProductSearchTypes getCopyProductSearchTypes() {
        return copyProductSearchTypes;
    }

    public void setCopyProductSearchTypes(CopyProductSearchTypes copyProductSearchTypes) {
        this.copyProductSearchTypes = copyProductSearchTypes;
    }

    public CopyRawMaterialSearchTypes getCopyRawMaterialSearchTypes() {
        return copyRawMaterialSearchTypes;
    }

    public void setCopyRawMaterialSearchTypes(CopyRawMaterialSearchTypes copyRawMaterialSearchTypes) {
        this.copyRawMaterialSearchTypes = copyRawMaterialSearchTypes;
    }

    public CopyTechnicalSpecSearchTypes getCopyTechnicalSpecSearchTypes() {
        return copyTechnicalSpecSearchTypes;
    }

    public void setCopyTechnicalSpecSearchTypes(CopyTechnicalSpecSearchTypes copyTechnicalSpecSearchTypes) {
        this.copyTechnicalSpecSearchTypes = copyTechnicalSpecSearchTypes;
    }

    @Override
    public String toString() {
        return "CopyPreferenceConfig{" +
                "copyPackagingSearchTypes=" + copyPackagingSearchTypes +
                ", copyProductSearchTypes=" + copyProductSearchTypes +
                ", copyRawMaterialSearchTypes=" + copyRawMaterialSearchTypes +
                ", copyTechnicalSpecSearchTypes=" + copyTechnicalSpecSearchTypes +
                '}';
    }
}

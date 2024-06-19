package com.pg.dsm.preference.config.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "packagingPreferenceConfig",
        "productPreferenceConfig",
        "rawMaterialPreferenceConfig",
        "technicalSpecPreferenceConfig",
        "copyPreferenceConfig",
        "defaultCreatePartConfig",
        "irmAttributePreferenceConfig",
        "irmProjectSpacePreferenceConfig",
        "defaultCreateChangeAction",
        "defaultCreatePartFamily",
        "ipSecurityControlPreference"
})
@XmlRootElement(name = "preferenceConfig")
public class PreferenceConfig {
    @XmlElement(required = true, name = "packagingPreferenceConfig")
    protected PackagingPreferenceConfig packagingPreferenceConfig;
    @XmlElement(required = true, name = "productPreferenceConfig")
    protected ProductPreferenceConfig productPreferenceConfig;
    @XmlElement(required = true, name = "rawMaterialPreferenceConfig")
    protected RawMaterialPreferenceConfig rawMaterialPreferenceConfig;

    @XmlElement(required = true, name = "technicalSpecPreferenceConfig")
    protected TechnicalSpecPreferenceConfig technicalSpecPreferenceConfig;
    @XmlElement(required = true, name = "copyPreferenceConfig")
    protected CopyPreferenceConfig copyPreferenceConfig;

    @XmlElement(required = true, name = "defaultCreatePartConfig")
    protected DefaultCreatePartConfig defaultCreatePartConfig;

    @XmlElement(required = true, name = "irmAttributePreferenceConfig")
    protected IRMAttributePreferenceConfig irmAttributePreferenceConfig;

    @XmlElement(required = true, name = "irmProjectSpacePreferenceConfig")
    protected IRMProjectSpacePreferenceConfig irmProjectSpacePreferenceConfig;

    @XmlElement(required = true, name = "defaultCreateChangeAction")
    protected DefaultCreateChangeAction defaultCreateChangeAction;

    @XmlElement(required = true, name = "defaultCreatePartFamily")
    protected DefaultCreatePartFamily defaultCreatePartFamily;

    @XmlElement(required = true, name = "ipSecurityControlPreference")
    protected IPSecurityControlPreference ipSecurityControlPreference;

    public PackagingPreferenceConfig getPackagingPreferenceConfig() {
        return packagingPreferenceConfig;
    }

    public void setPackagingPreferenceConfig(PackagingPreferenceConfig packagingPreferenceConfig) {
        this.packagingPreferenceConfig = packagingPreferenceConfig;
    }

    public ProductPreferenceConfig getProductPreferenceConfig() {
        return productPreferenceConfig;
    }

    public void setProductPreferenceConfig(ProductPreferenceConfig productPreferenceConfig) {
        this.productPreferenceConfig = productPreferenceConfig;
    }

    public RawMaterialPreferenceConfig getRawMaterialPreferenceConfig() {
        return rawMaterialPreferenceConfig;
    }

    public void setRawMaterialPreferenceConfig(RawMaterialPreferenceConfig rawMaterialPreferenceConfig) {
        this.rawMaterialPreferenceConfig = rawMaterialPreferenceConfig;
    }

    public CopyPreferenceConfig getCopyPreferenceConfig() {
        return copyPreferenceConfig;
    }

    public void setCopyPreferenceConfig(CopyPreferenceConfig copyPreferenceConfig) {
        this.copyPreferenceConfig = copyPreferenceConfig;
    }

    public DefaultCreatePartConfig getDefaultCreatePartConfig() {
        return defaultCreatePartConfig;
    }

    public void setDefaultCreatePartConfig(DefaultCreatePartConfig defaultCreatePartConfig) {
        this.defaultCreatePartConfig = defaultCreatePartConfig;
    }

    public IRMAttributePreferenceConfig getIrmAttributePreferenceConfig() {
        return irmAttributePreferenceConfig;
    }

    public void setIrmAttributePreferenceConfig(IRMAttributePreferenceConfig irmAttributePreferenceConfig) {
        this.irmAttributePreferenceConfig = irmAttributePreferenceConfig;
    }

    public IRMProjectSpacePreferenceConfig getIrmProjectSpacePreferenceConfig() {
        return irmProjectSpacePreferenceConfig;
    }

    public void setIrmProjectSpacePreferenceConfig(IRMProjectSpacePreferenceConfig irmProjectSpacePreferenceConfig) {
        this.irmProjectSpacePreferenceConfig = irmProjectSpacePreferenceConfig;
    }

    public DefaultCreateChangeAction getDefaultCreateChangeAction() {
        return defaultCreateChangeAction;
    }

    public void setDefaultCreateChangeAction(DefaultCreateChangeAction defaultCreateChangeAction) {
        this.defaultCreateChangeAction = defaultCreateChangeAction;
    }

    public DefaultCreatePartFamily getDefaultCreatePartFamily() {
        return defaultCreatePartFamily;
    }

    public void setDefaultCreatePartFamily(DefaultCreatePartFamily defaultCreatePartFamily) {
        this.defaultCreatePartFamily = defaultCreatePartFamily;
    }

    public TechnicalSpecPreferenceConfig getTechnicalSpecPreferenceConfig() {
        return technicalSpecPreferenceConfig;
    }

    public void setTechnicalSpecPreferenceConfig(TechnicalSpecPreferenceConfig technicalSpecPreferenceConfig) {
        this.technicalSpecPreferenceConfig = technicalSpecPreferenceConfig;
    }

    public IPSecurityControlPreference getIpSecurityControlPreference() {
        return ipSecurityControlPreference;
    }

    public void setIpSecurityControlPreference(IPSecurityControlPreference ipSecurityControlPreference) {
        this.ipSecurityControlPreference = ipSecurityControlPreference;
    }

    @Override
    public String toString() {
        return "PreferenceConfig{" +
                "packagingPreferenceConfig=" + packagingPreferenceConfig +
                ", productPreferenceConfig=" + productPreferenceConfig +
                ", rawMaterialPreferenceConfig=" + rawMaterialPreferenceConfig +
                ", technicalSpecPreferenceConfig=" + technicalSpecPreferenceConfig +
                ", copyPreferenceConfig=" + copyPreferenceConfig +
                ", defaultCreatePartConfig=" + defaultCreatePartConfig +
                ", irmAttributePreferenceConfig=" + irmAttributePreferenceConfig +
                ", irmProjectSpacePreferenceConfig=" + irmProjectSpacePreferenceConfig +
                ", defaultCreateChangeAction=" + defaultCreateChangeAction +
                ", defaultCreatePartFamily=" + defaultCreatePartFamily +
                ", ipSecurityControlPreference=" + ipSecurityControlPreference +
                '}';
    }
}

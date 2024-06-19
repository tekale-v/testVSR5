package com.pg.dsm.preference.config.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "includeSearchType",
        "excludeSearchType"
})
@XmlRootElement(name = "copyTechnicalSpecSearchTypes")
public class CopyTechnicalSpecSearchTypes {
    @XmlElement(required = true, name = "includeSearchType")
    protected String includeSearchType;
    @XmlElement(required = true, name = "excludeSearchType")
    protected String excludeSearchType;

    public String getIncludeSearchType() {
        return includeSearchType;
    }

    public void setIncludeSearchType(String includeSearchType) {
        this.includeSearchType = includeSearchType;
    }

    public String getExcludeSearchType() {
        return excludeSearchType;
    }

    public void setExcludeSearchType(String excludeSearchType) {
        this.excludeSearchType = excludeSearchType;
    }

    @Override
    public String toString() {
        return "CopyTechnicalSpecSearchTypes{" +
                "includeSearchType='" + includeSearchType + '\'' +
                ", excludeSearchType='" + excludeSearchType + '\'' +
                '}';
    }
}

package com.pg.dsm.rollup_event.common.config.rule;

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
        "rollup"
})
@XmlRootElement(name = "config")
public class Config {
    @XmlAttribute(name = "allowedSubstituteTypes")
    public String allowedSubstituteTypes;
    @XmlAttribute(name = "performCircularCheck")
    public String performCircularCheck;
    @XmlAttribute(name = "performMarketCheck")
    public String performMarketCheck;
    @XmlAttribute(name = "type")
    protected String type;
    @XmlAttribute(name = "relationship")
    protected String relationship;
    @XmlAttribute(name = "allowedStateForManual")
    protected String allowedStateForManual;
    @XmlAttribute(name = "allowedStateForJob")
    protected String allowedStateForJob;
    @XmlElement(required = true)
    List<Rollup> rollup;


    public List<Rollup> getRollup() {
        if (rollup == null) {
            rollup = new ArrayList<>();
        }
        return this.rollup;
    }

    public void setRollup(List<Rollup> rollup) {
        this.rollup = rollup;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getAllowedStateForManual() {
        return allowedStateForManual;
    }

    public void setAllowedStateForManual(String allowedStateForManual) {
        this.allowedStateForManual = allowedStateForManual;
    }

    public String getAllowedStateForJob() {
        return allowedStateForJob;
    }

    public void setAllowedStateForJob(String allowedStateForJob) {
        this.allowedStateForJob = allowedStateForJob;
    }

    public String getAllowedSubstituteTypes() {
        return allowedSubstituteTypes;
    }

    public void setAllowedSubstituteTypes(String allowedSubstituteTypes) {
        this.allowedSubstituteTypes = allowedSubstituteTypes;
    }

    public String getPerformCircularCheck() {
        return performCircularCheck;
    }

    public void setPerformCircularCheck(String performCircularCheck) {
        this.performCircularCheck = performCircularCheck;
    }

    public String getPerformMarketCheck() {
        return performMarketCheck;
    }

    public void setPerformMarketCheck(String performMarketCheck) {
        this.performMarketCheck = performMarketCheck;
    }


}

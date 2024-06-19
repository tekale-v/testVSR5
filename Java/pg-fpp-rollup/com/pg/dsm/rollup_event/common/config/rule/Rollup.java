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
        "rule"
})
@XmlRootElement(name = "rollup")
public class Rollup {
    @XmlAttribute(name = "identifier")
    public String identifier;
    @XmlAttribute(name = "eventName")
    public String eventName;
    @XmlAttribute(name = "allowedTypesInFPPTraversion")
    public String allowedTypesInFPPTraversion;
    @XmlAttribute(name = "allowedRanges")
    public String allowedRanges;
    @XmlAttribute(name = "attributeName")
    public String attributeName;
    @XmlAttribute(name = "nLevel")
    public String nLevel;
    @XmlAttribute(name = "allowedRelationships")
    public String allowedRelationships;
    @XmlAttribute(name = "allowedIntermediateSubstitutes")
    public String allowedIntermediateSubstitutes;
    @XmlAttribute(name = "enableRollup")
    public String enableRollup;
    @XmlAttribute(name = "allowCircularDataForRollup")
    public String allowCircularDataForRollup;
    @XmlElement(required = true)
    List<Rule> rule;


    public List<Rule> getRules() {
        if (rule == null) {
            rule = new ArrayList<>();
        }
        return this.rule;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getAllowedTypesInFPPTraversion() {
        return allowedTypesInFPPTraversion;
    }

    public Rollup setAllowedTypesInFPPTraversion(String allowedTypesInFPPTraversion) {
        this.allowedTypesInFPPTraversion = allowedTypesInFPPTraversion;
        return this;
    }

    public String getAllowedRanges() {
        return allowedRanges;
    }

    public void setAllowedRanges(String allowedRanges) {
        this.allowedRanges = allowedRanges;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getnLevel() {
        return nLevel;
    }

    public void setnLevel(String nLevel) {
        this.nLevel = nLevel;
    }

    public String getAllowedRelationships() {
        return allowedRelationships;
    }

    public void setAllowedRelationships(String allowedRelationships) {
        this.allowedRelationships = allowedRelationships;
    }

    public String getAllowedIntermediateSubstitutes() {
        return allowedIntermediateSubstitutes;
    }

    public void setAllowedIntermediateSubstitutes(String allowedIntermediateSubstitutes) {
        this.allowedIntermediateSubstitutes = allowedIntermediateSubstitutes;
    }

    public String getEnableRollup() {
        return enableRollup;
    }

    public void setEnableRollup(String enableRollup) {
        this.enableRollup = enableRollup;
    }

    public String getAllowCircularDataForRollup() {
        return allowCircularDataForRollup;
    }

    public void setAllowCircularDataForRollup(String allowCircularDataForRollup) {
        this.allowCircularDataForRollup = allowCircularDataForRollup;
    }


}

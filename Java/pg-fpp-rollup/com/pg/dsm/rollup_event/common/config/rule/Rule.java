package com.pg.dsm.rollup_event.common.config.rule;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        ""
})
@XmlRootElement(name = "rule")
public class Rule {
    @XmlAttribute(name = "name")
    public String name;
    @XmlAttribute(name = "identifier")
    public String identifier;
    @XmlAttribute(name = "flag")
    public String flag;
    @XmlAttribute(name = "inclusionType")
    public String inclusionType;

    @XmlAttribute(name = "fromType")
    public String fromType;

    @XmlAttribute(name = "toType")
    public String toType;

    @XmlAttribute(name = "relationshipName")
    public String relationshipName;

    @XmlAttribute(name = "childrenAllowed")
    public String childrenAllowed;

    @XmlAttribute(name = "substituteAllowed")
    public String substituteAllowed;

    @XmlAttribute(name = "substituteInclusionType")
    public String substituteInclusionType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getInclusionType() {
        return inclusionType;
    }

    public void setInclusionType(String inclusionType) {
        this.inclusionType = inclusionType;
    }

    public String getFromType() {
        return fromType;
    }

    public void setFromType(String fromType) {
        this.fromType = fromType;
    }

    public String getToType() {
        return toType;
    }

    public void setToType(String toType) {
        this.toType = toType;
    }

    public String getRelationshipName() {
        return relationshipName;
    }

    public void setRelationshipName(String relationshipName) {
        this.relationshipName = relationshipName;
    }

    public String getChildrenAllowed() {
        return childrenAllowed;
    }

    public void setChildrenAllowed(String childrenAllowed) {
        this.childrenAllowed = childrenAllowed;
    }

    public String getSubstituteAllowed() {
        return substituteAllowed;
    }

    public void setSubstituteAllowed(String substituteAllowed) {
        this.substituteAllowed = substituteAllowed;
    }

    public String getSubstituteInclusionType() {
        return substituteInclusionType;
    }

    public void setSubstituteInclusionType(String substituteInclusionType) {
        this.substituteInclusionType = substituteInclusionType;
    }
}

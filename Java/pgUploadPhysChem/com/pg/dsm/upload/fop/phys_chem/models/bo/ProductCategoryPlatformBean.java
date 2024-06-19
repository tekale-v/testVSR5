/*
 **   ProductCategoryPlatformBean.java
 **   Description - Introduced as part of update Physical Chemical Properties (physChem) - 18x.5.
 **   About - Product Category Platform Bean class.
 **
 */
package com.pg.dsm.upload.fop.phys_chem.models.bo;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlTransient;

public class ProductCategoryPlatformBean {
    @JsonProperty("type")
    protected String type;
    @JsonProperty("name")
    protected String name;
    @JsonProperty("revision")
    protected String revision;
    @JsonProperty("current")
    protected String current;
    @JsonProperty("id")
    protected String id;

    @XmlTransient
    protected String relationship;
    @XmlTransient
    protected String level;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}

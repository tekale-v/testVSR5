/*
 **   ProductFormBean.java
 **   Description - Introduced as part of update Physical Chemical Properties (physChem) - 18x.5.
 **   About - Product Form Bean class.
 **
 */

package com.pg.dsm.upload.fop.phys_chem.models.bo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductFormBean {
    @JsonProperty("type")
    protected String type;
    @JsonProperty("name")
    protected String name;
    @JsonProperty("current")
    protected String current;
    @JsonProperty("revision")
    protected String revision;
    @JsonProperty("id")
    protected String id;
    @JsonProperty("relationship")
    protected String relationship;
    @JsonProperty("level")
    protected String level;
    @JsonProperty("id[connection]")
    protected String relId;

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

    public String getRelId() {
        return relId;
    }

    public void setRelId(String relId) {
        this.relId = relId;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }
}

/*
 * Name: PartBean.java
 * About: Corresponding bean to map a HashMap. 
 * Since: 18x.5
 */
package com.pg.v4.util.circular.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PartBean {
    @JsonProperty("type")
    String type;
    @JsonProperty("name")
    String name;
    @JsonProperty("revision")
    String revision;
    @JsonProperty("id")
    String id;
    @JsonProperty("relationship")
    String relationship;
    @JsonProperty("level")
    String level;

    @JsonIgnore
    boolean childExist;
    @JsonIgnore
    boolean parentExist;
    @JsonIgnore
    List<PartBean> children;
    @JsonIgnore
    PartBean parent;

   public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isChildExist() {
        return childExist;
    }

    public void setChildExist(boolean childExist) {
        this.childExist = childExist;
    }

    public boolean isParentExist() {
        return parentExist;
    }

    public void setParentExist(boolean parentExist) {
        this.parentExist = parentExist;
    }

    public List<PartBean> getChildren() {
        return children;
    }

    public void setChildren(List<PartBean> children) {
        this.children = children;
    }

    public PartBean getParent() {
        return parent;
    }

    public void setParent(PartBean parent) {
        this.parent = parent;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

}

package com.pg.dra.structure_copy.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.matrixone.apps.domain.DomainConstants;

public class Part {
    private String id;
    private String type;
    private String name;
    private String revision;
    private String relId;
    private String level;
    private Part parent;
    private List<Part> children;
    private boolean parentExist;
    private boolean childExist;

    public Part(Map<Object, Object> objectMap) {
        this.id = (String) objectMap.get(DomainConstants.SELECT_ID);
        this.type = (String) objectMap.get(DomainConstants.SELECT_TYPE);
        this.name = (String) objectMap.get(DomainConstants.SELECT_NAME);
        this.revision = (String) objectMap.get(DomainConstants.SELECT_REVISION);
        this.relId = objectMap.containsKey(DomainConstants.SELECT_RELATIONSHIP_ID) ? (String) objectMap.get(DomainConstants.SELECT_RELATIONSHIP_ID) : DomainConstants.EMPTY_STRING;
        this.level = objectMap.containsKey(DomainConstants.SELECT_LEVEL) ? (String) objectMap.get(DomainConstants.SELECT_LEVEL) : "0";
        this.children = new ArrayList<>();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getRelId() {
        return relId;
    }

    public void setRelId(String relId) {
        this.relId = relId;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Part getParent() {
        return parent;
    }

    public void setParent(Part parent) {
        this.parent = parent;
    }

    public List<Part> getChildren() {
        return children;
    }

    public void setChildren(List<Part> children) {
        this.children = children;
    }

    public boolean isParentExist() {
        return parentExist;
    }

    public void setParentExist(boolean parentExist) {
        this.parentExist = parentExist;
    }

    public boolean isChildExist() {
        return childExist;
    }

    public void setChildExist(boolean childExist) {
        this.childExist = childExist;
    }

    public void addChild(Part part) {
        children.add(part);
    }
}

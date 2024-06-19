package com.pg.dsm.sapview.beans.bo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Part {
    @JsonProperty("type")
    String type;
    @JsonProperty("name")
    String name;
    @JsonProperty("revision")
    String revision;
    @JsonProperty("current")
    String current;
    @JsonProperty("policy")
    String policy;
    @JsonProperty("id")
    String id;
    @JsonProperty("assemblyType")
    String assemblyType;

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

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAssemblyType() {
        return assemblyType;
    }

    public void setAssemblyType(String assemblyType) {
        this.assemblyType = assemblyType;
    }
}

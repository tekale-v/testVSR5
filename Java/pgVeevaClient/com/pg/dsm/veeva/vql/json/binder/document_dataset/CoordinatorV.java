/*
 **   CoordinatorV.java
 **   Description - Introduced as part of Veeva integration.      
 **   (jackson bean) 
 **
 */
package com.pg.dsm.veeva.vql.json.binder.document_dataset;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@JsonPropertyOrder({
    "groups",
    "users"
})
public class CoordinatorV {

    @JsonProperty("groups")
    private List<Object> groups = null;
    @JsonProperty("users")
    private List<Object> users = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("groups")
    public List<Object> getGroups() {
        return groups;
    }

    @JsonProperty("groups")
    public void setGroups(List<Object> groups) {
        this.groups = groups;
    }

    public CoordinatorV withGroups(List<Object> groups) {
        this.groups = groups;
        return this;
    }

    @JsonProperty("users")
    public List<Object> getUsers() {
        return users;
    }

    @JsonProperty("users")
    public void setUsers(List<Object> users) {
        this.users = users;
    }

    public CoordinatorV withUsers(List<Object> users) {
        this.users = users;
        return this;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public CoordinatorV withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}

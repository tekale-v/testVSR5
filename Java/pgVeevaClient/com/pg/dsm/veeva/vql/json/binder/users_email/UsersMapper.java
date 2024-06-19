/*
 **   UsersMapper.java
 **   Description - Introduced as part of Veeva integration.      
 **   (jackson bean) 
 **
 */
package com.pg.dsm.veeva.vql.json.binder.users_email;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
    "responseStatus",
    "responseDetails",
    "data"
})
public class UsersMapper {

    @JsonProperty("responseStatus")
    private String responseStatus;
    @JsonProperty("responseDetails")
    private UsersEmailResponseDetails responseDetails;
    @JsonProperty("data")
    private List<UsersEmail> data = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("responseStatus")
    public String getResponseStatus() {
        return responseStatus;
    }

    @JsonProperty("responseStatus")
    public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }

    @JsonProperty("responseDetails")
    public UsersEmailResponseDetails getResponseDetails() {
        return responseDetails;
    }

    @JsonProperty("responseDetails")
    public void setResponseDetails(UsersEmailResponseDetails responseDetails) {
        this.responseDetails = responseDetails;
    }

    @JsonProperty("data")
    public List<UsersEmail> getUsersEmail() {
        return data;
    }

    @JsonProperty("data")
    public void setUsersEmail(List<UsersEmail> data) {
        this.data = data;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}

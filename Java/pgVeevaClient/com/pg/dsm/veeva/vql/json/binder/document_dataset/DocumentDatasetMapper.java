/*
 **   DocumentDatasetMapper.java
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
    "responseStatus",
    "document",
    "renditions",
    "versions",
    "attachments"
})
public class DocumentDatasetMapper {

    @JsonProperty("responseStatus")
    private String responseStatus;
    @JsonProperty("document")
    private Document document;
    @JsonProperty("renditions")
    private Renditions renditions;
    @JsonProperty("versions")
    private List<Version> versions = null;
    @JsonProperty("attachments")
    private List<Attachment> attachments = null;
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

    public DocumentDatasetMapper withResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
        return this;
    }

    @JsonProperty("document")
    public Document getDocument() {
        return document;
    }

    @JsonProperty("document")
    public void setDocument(Document document) {
        this.document = document;
    }

    public DocumentDatasetMapper withDocument(Document document) {
        this.document = document;
        return this;
    }

    @JsonProperty("renditions")
    public Renditions getRenditions() {
        return renditions;
    }

    @JsonProperty("renditions")
    public void setRenditions(Renditions renditions) {
        this.renditions = renditions;
    }

    public DocumentDatasetMapper withRenditions(Renditions renditions) {
        this.renditions = renditions;
        return this;
    }

    @JsonProperty("versions")
    public List<Version> getVersions() {
        return versions;
    }

    @JsonProperty("versions")
    public void setVersions(List<Version> versions) {
        this.versions = versions;
    }

    public DocumentDatasetMapper withVersions(List<Version> versions) {
        this.versions = versions;
        return this;
    }

    @JsonProperty("attachments")
    public List<Attachment> getAttachments() {
        return attachments;
    }

    @JsonProperty("attachments")
    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public DocumentDatasetMapper withAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
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

    public DocumentDatasetMapper withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}

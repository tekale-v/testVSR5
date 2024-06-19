/*
 **   Renditions.java
 **   Description - Introduced as part of Veeva integration.      
 **   (jackson bean) 
 **
 */
package com.pg.dsm.veeva.vql.json.binder.document_dataset;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
	"production_proof__c",
	"panoramic_thumbnail__v",
	"portal_rendition__v",
	"viewable_rendition__v"
})
public class Renditions {

    @JsonProperty("viewable_rendition__v")
    private String viewableRenditionV;
    
    @JsonProperty("production_proof__c")
    private String productionProofC;
    
    @JsonProperty("production_proof__c")
    @JsonGetter("production_proof__c")
    public String getProductionProofC() {
        return productionProofC;
    }
    @JsonProperty("production_proof__c")
    public void setProductionProofC(String productionProofC) {
        this.productionProofC = productionProofC;
    }
    @JsonProperty("portal_rendition__v")
    private String portalRenditionV;
    
    @JsonProperty("portal_rendition__v")
    @JsonGetter("portal_rendition__v")
    public String getPortalRenditionV() {
        return portalRenditionV;
    }
    @JsonProperty("portal_rendition__v")
    public void setPortalRenditionV(String portalRenditionV) {
        this.portalRenditionV = portalRenditionV;
    }
    @JsonProperty("panoramic_thumbnail__v")
    private String panoramicThumbnailV;
    
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("viewable_rendition__v")
    @JsonGetter("viewable_rendition__v")
    public String getViewableRenditionV() {
        return viewableRenditionV;
    }

    @JsonProperty("viewable_rendition__v")
    public void setViewableRenditionV(String viewableRenditionV) {
        this.viewableRenditionV = viewableRenditionV;
    }
    public Renditions withViewableRenditionV(String viewableRenditionV) {
        this.viewableRenditionV = viewableRenditionV;
        return this;
    }
    @JsonProperty("panoramic_thumbnail__v")
    @JsonGetter("panoramic_thumbnail__v")
    public String getPanoramicThumbnailV() {
        return panoramicThumbnailV;
    }
    @JsonProperty("panoramic_thumbnail__v")
    public void setPanoramicThumbnailV(String panoramicThumbnailV) {
        this.panoramicThumbnailV = panoramicThumbnailV;
    }

    public Renditions withPanoramicThumbnailV(String panoramicThumbnailV) {
        this.panoramicThumbnailV = panoramicThumbnailV;
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

    public Renditions withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}

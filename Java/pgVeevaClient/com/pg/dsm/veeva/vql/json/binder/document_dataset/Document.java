/*
 **   Document.java
 **   Description - Introduced as part of Veeva integration.      
 **   (jackson bean) 
 **
 */
package com.pg.dsm.veeva.vql.json.binder.document_dataset;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Document {
    @JsonProperty("owner__v")
    private OwnerV ownerV;
    public OwnerV getOwnerV() {
        return ownerV;
    }
    public void setOwnerV(OwnerV ownerV) {
        this.ownerV = ownerV;
    }
}

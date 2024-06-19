/*
 **   Document.java
 **   Description - Introduced as part of Veeva integration.      
 **   (jackson bean) 
 **   Modified for 2018x.5 Requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
 */
package com.pg.dsm.veeva.vql.json.binder.documents_query;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pg.dsm.veeva.vql.json.binder.document_dataset.Renditions;
import com.pg.dsm.veeva.vql.xml.binder.Rendition;

public class Document {
	String id;
	
	@JsonProperty("document_number__v")
	String documentNumber;
	
	@JsonIgnore
	private Renditions renditions;
	
	@JsonIgnore
	private List<Rendition> configuredRenditions;

	public Renditions getRenditions() {
		return renditions;
	}
	public void setRenditions(Renditions renditions) {
		this.renditions = renditions;
	}
	public List<Rendition> getConfiguredRenditions() {
		return configuredRenditions;
	}
	public void setConfiguredRenditions(List<Rendition> configuredRenditions) {
		this.configuredRenditions = configuredRenditions;
	}

	public String getDocumentNumber() {
		return documentNumber;
	}
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

}

/*
 **   DocumentProperty.java
 **   Description - Introduced as part of Veeva integration.      
 **   (jackson bean) 
 **
 */
package com.pg.dsm.veeva.vql.json.binder.document_property;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DocumentProperty {

	@JsonProperty( "id" )
	String id;

	@JsonProperty( "name__v" )
	String name;

	@JsonProperty( "document_number__v" )
	String documentNumber;

	@JsonProperty( "pmp__c" )
	String pmp;

	@JsonProperty( "description__v" )
	String description;

	@JsonProperty( "title__v" )
	String title;
	
	@JsonProperty( "rights_language__v" )
	String language;


	@JsonProperty( "document_country__vr" )
	DocumentCountries documentCountries;
	
	@JsonProperty( "status__v" )
	String status;
	
	@JsonGetter("status__v")
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	@JsonGetter("id")
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@JsonGetter("name__v")
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@JsonGetter("document_number__v")
	public String getDocumentNumber() {
		return documentNumber;
	}
	
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}
	@JsonGetter("pmp__c")
	public String getPmp() {
		return pmp;
	}
	
	public void setPmp(String pmp) {
		this.pmp = pmp;
	}
	@JsonGetter("description__v")
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	@JsonGetter("rights_language__v")
	public String getLanguage() {
		return language;
	}
	
	public void setLanguage(String language) {
		this.language = language;
	}
	@JsonGetter("title__v")
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}	

	@JsonGetter("document_country__vr")
	public DocumentCountries getCountries() {
		return documentCountries;
	}
	
	public void setCountries(DocumentCountries documentCountries) {
		this.documentCountries = documentCountries;
	}
}

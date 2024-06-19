/*
 **   DocumentPropertyMapper.java
 **   Description - Introduced as part of Veeva integration.      
 **   (jackson bean) 
 **
 */
package com.pg.dsm.veeva.vql.json.binder.document_property;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;


public class DocumentPropertyMapper {
	String responseStatus;
	Map<String, String> responseDetails;
	
	@JsonIgnore
	String limit;
	@JsonIgnore
	String offset;
	@JsonIgnore
	String size;
	@JsonIgnore
	String total;
	
	List<DocumentProperty> documentProperty;

	public String getResponseStatus() {
		return responseStatus;
	}

	@JsonSetter("responseStatus")
	public void setResponseStatus(String responseStatus) {
		this.responseStatus = responseStatus;
	}

	public Map<String, String> getResponseDetails() {
		return responseDetails;
	}

	@JsonSetter("responseDetails")
	public void setResponseDetails(Map<String, String> responseDetails) {
		this.responseDetails = responseDetails;
	}

	public String getLimit() {
		return limit;
	}
	public String getOffset() {
		return offset;
	}

	public String getSize() {
		return size;
	}

	public String getTotal() {
		return total;
	}
	public List<DocumentProperty> getDocumentProperty() {
		return documentProperty;
	}

	@JsonSetter("data")
	public void setListArtworks(List<DocumentProperty> documentProperty) {
		this.documentProperty = documentProperty;
	}
}

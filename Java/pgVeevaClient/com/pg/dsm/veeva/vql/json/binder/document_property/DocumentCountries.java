/*
 **   DocumentCountries.java
 **   Description - Introduced as part of Veeva integration.      
 **   (jackson bean) 
 **
 */
package com.pg.dsm.veeva.vql.json.binder.document_property;

import java.util.List;
import java.util.Map;



public class DocumentCountries {
	Map<String, String> responseDetails;
	List<DocumentCountry> data;
	public Map<String, String> getResponseDetails() {
		return responseDetails;
	}
	public void setResponseDetails(Map<String, String> responseDetails) {
		this.responseDetails = responseDetails;
	}
	public List<DocumentCountry> getDocumentCountry() {
		return data;
	}
	public void setData(List<DocumentCountry> data) {
		this.data = data;
	}
}

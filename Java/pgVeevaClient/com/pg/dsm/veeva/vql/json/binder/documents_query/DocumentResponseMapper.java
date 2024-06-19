/*
 **   DocumentResponseMapper.java
 **   Description - Introduced as part of Veeva integration.      
 **   (jackson bean) 
 **
 */
package com.pg.dsm.veeva.vql.json.binder.documents_query;

import java.util.List;
import java.util.Map;

public class DocumentResponseMapper {
	String responseStatus;
	Map<String, String> responseDetails;
	List<Document> documents;
	
	String limit;
	String offset;
	String size; 
	String total; 
	String next_page; 
	
	public String getResponseStatus() {
		return responseStatus;
	}
	public void setResponseStatus(String responseStatus) {
		this.responseStatus = responseStatus;
	}
	public Map<String, String> getResponseDetails() {
		return responseDetails;
	}
	public void setResponseDetails(Map<String, String> responseDetails) {
		this.responseDetails = responseDetails;
	}
	public String getLimit() {
		return responseDetails.get("limit");
	}
	public String getOffset() {
		return responseDetails.get("offset");
	}
	public String getSize() {
		return responseDetails.get("size");
	}
	public String getTotal() {
		return responseDetails.get("total");
	}
	public String getNextPage() {
		return responseDetails.get("next_page");
	}
	public List<Document> getDocuments() {
		return documents;
	}
	public void setData(List<Document> documents) {
		this.documents = documents;
	}
}

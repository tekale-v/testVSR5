/*
 **   DocumentPropertyResponse.java
 **   Description - Introduced as part of Veeva integration.      
 **   (Jackson) Document Property Response bean
 **
 */
package com.pg.dsm.veeva.vql.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.pg.dsm.veeva.util.Utility;

public class DocumentPropertyResponse extends Response {

	String jString;
	String responseStatus;
	String limit = "";
	String offset = "";
	String size = "";
	String total = "";

	String type;
	String message;

	public DocumentPropertyResponse(String jString) throws JSONException {
		this.jString = jString;
		if(Utility.isNotNullEmpty(jString)) {
			JSONObject jsonObj = new JSONObject(jString);
			this.responseStatus = jsonObj.getString("responseStatus");

			if("SUCCESS".equals(responseStatus)) {
				JSONObject jsonDetails = (JSONObject)jsonObj.get("responseDetails");
				//Start modified for 22x changes - handled for Object types of values fetched from json.get method
				/*this.limit = jsonDetails.getString("limit");
				this.offset = jsonDetails.getString("offset");
				this.size = jsonDetails.getString("size");
				this.total = jsonDetails.getString("total");*/
				this.limit = String.valueOf(jsonDetails.get("limit"));
				this.offset = String.valueOf(jsonDetails.get("offset"));
				this.size = String.valueOf(jsonDetails.get("size"));
				this.total = String.valueOf(jsonDetails.get("total"));
				//End modified for 22x changes - handled for Object types of values fetched from json.get method
			} else {
				JSONObject jsonErrorObj = (JSONObject)jsonObj.getJSONArray("errors").get(0);
				this.type = jsonErrorObj.getString("type");
				this.message = jsonErrorObj.getString("message");
			}
		}
	}
	public String getjString() {
		return jString;
	}
	public void setjString(String jString) {
		this.jString = jString;
	}
	public String getResponseStatus() {
		return responseStatus;
	}
	public void setResponseStatus(String responseStatus) {
		this.responseStatus = responseStatus;
	}
	public String getLimit() {
		return limit;
	}
	public void setLimit(String limit) {
		this.limit = limit;
	}
	public String getOffset() {
		return offset;
	}
	public void setOffset(String offset) {
		this.offset = offset;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return null;
	}

}

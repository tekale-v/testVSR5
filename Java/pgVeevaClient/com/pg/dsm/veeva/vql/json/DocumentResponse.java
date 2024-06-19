/*
 **   DocumentResponse.java
 **   Description - Introduced as part of Veeva integration.      
 **   (Jackson) Document Response bean
 **
 */
package com.pg.dsm.veeva.vql.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.pg.dsm.veeva.util.Utility;

public class DocumentResponse extends Response {

	String jString;
	String responseStatus;
	String type;
	String message;
	public DocumentResponse(String jString) throws JSONException {
		this.jString = jString;
		if(Utility.isNotNullEmpty(jString)) {
			JSONObject jsonObj = new JSONObject(jString);
			this.responseStatus = jsonObj.getString("responseStatus");
			if(!"SUCCESS".equals(responseStatus)) {
				JSONObject jsonErrorObj = (JSONObject)jsonObj.getJSONArray("errors").get(0);
				this.type = jsonErrorObj.getString("type");
				this.message = jsonErrorObj.getString("message");
			}	
		}
	}
	public String getResponseStatus() {
		return responseStatus;
	}
	public void setResponseStatus(String responseStatus) {
		this.responseStatus = responseStatus;
	}
	public String getjString() {
		return jString;
	}
	public void setjString(String jString) {
		this.jString = jString;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
/*
 **   RenditionResponse.java
 **   Description - Introduced as part of Veeva integration.      
 **   (Jackson) Rendition Response bean
 **
 */
package com.pg.dsm.veeva.vql.json;

import org.json.JSONException;
import org.json.JSONObject;

public class RenditionResponse extends Response {
	
	String jString;
	String responseStatusCode;
	boolean hasFile;
	boolean fileDownloaded;
	String fileName;
	
	public RenditionResponse(String jString) throws JSONException {
		this.jString = jString;
		JSONObject jsonObj = new JSONObject(jString);
		if(null != jsonObj) {
			//Start modified for 22x changes - handled for Object types of values fetched from json.get method
			//this.responseStatusCode = jsonObj.getString("responseStatusCode");
			this.responseStatusCode = String.valueOf(jsonObj.get("responseStatusCode"));
			//End modified for 22x changes - handled for Object types of values fetched from json.get method
			this.hasFile = (boolean) jsonObj.get("hasFile");
			this.fileDownloaded = (boolean) jsonObj.get("fileDownloaded");
			this.fileName = jsonObj.getString("fileName");
		}
	}
	public String getjString() {
		return jString;
	}
	public void setjString(String jString) {
		this.jString = jString;
	}
	public String getResponseStatusCode() {
		return responseStatusCode;
	}
	public void setResponseStatusCode(String responseStatusCode) {
		this.responseStatusCode = responseStatusCode;
	}
	@Override
	public String getResponseStatus() {
		// TODO Auto-generated method stub
		return null;
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
	public boolean hasFile() {
		return hasFile;
	}
	public boolean isFileDownloaded() {
		return fileDownloaded;
	}
	public String getFileName() {
		return fileName;
	}
}

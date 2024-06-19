/*
 **   AuthenticationResponse.java
 **   Description - Introduced as part of Veeva integration.      
 **   (Jackson) Authentication response bean
 **
 */
package com.pg.dsm.veeva.vql.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.pg.dsm.veeva.util.Utility;

public class AuthenticationResponse extends Response {

	String jString;
	String responseStatus;
	String type;
	String message;
	public AuthenticationResponse(String jString) throws JSONException {
		this.jString = jString;
		if(Utility.isNotNullEmpty(jString)) {
			JSONObject jsonObj = new JSONObject(jString);
			this.responseStatus = jsonObj.getString("responseStatus");
			if(!"SUCCESS".equals(responseStatus)) {
				JSONObject jsonErrorObj = (JSONObject)jsonObj.getJSONArray("errors").get(0);
				this.type = jsonErrorObj.getString("type");
				this.message = jsonErrorObj.getString("message");
			} 
			//{"responseStatus":"SUCCESS","sessionId":"272A0C6CA077CA026EB8437E73C96A2A59485BEAC0BE51989294550406C75FC8FD65F46F398842D65527A03AF426C2B6519519383254A7D0D67AF324364F0790BC68A29D515881E0F21C7DC527EC69C7","userId":4145729,"vaultIds":[{"id":36557,"name":"PromoMats Sandbox","url":"https://sb-pg-promomats.veevavault.com/api"}],"vaultId":36557}
			//{"responseStatus":"FAILURE","errors":[{"type":"INVALID_SESSION_ID","message":"Invalid or expired session ID."}]}
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
	public String getMessage() {
		return message;
	}
}

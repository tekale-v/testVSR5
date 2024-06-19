/*
 **   AuthenticationResponseMapper.java
 **   Description - Introduced as part of Veeva integration.      
 **   (jackson bean) 
 **
 */
package com.pg.dsm.veeva.vql.json.binder.authentication;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonSetter;

public class AuthenticationResponseMapper {

	String responseStatus;
	String sessionId;
	String userId;
	String vaultId;
	
	List<AuthorizedVaults> authorizedVaults;

	public String getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(String responseStatus) {
		this.responseStatus = responseStatus;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getVaultId() {
		return vaultId;
	}

	public void setVaultId(String vaultId) {
		this.vaultId = vaultId;
	}

	public List<AuthorizedVaults> getAuthorizedVaults() {
		return authorizedVaults;
	}

	@JsonSetter("vaultIds")
	public void setVaultIds(List<AuthorizedVaults> authorizedVaults) {
		this.authorizedVaults = authorizedVaults;
	}
}


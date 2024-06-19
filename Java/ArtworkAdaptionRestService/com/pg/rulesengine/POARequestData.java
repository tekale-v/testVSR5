package com.pg.rulesengine;

public class POARequestData {

	private String applicationUser;
	private String applicationUserPass;
	private String poaNumber;
	
	public POARequestData() {
		super();
	}

	public String getApplicationUser() {
		return applicationUser;
	}

	public void setApplicationUser(String applicationUser) {
		this.applicationUser = applicationUser;
	}

	public String getApplicationUserPass() {
		return applicationUserPass;
	}

	public void setApplicationUserPass(String applicationUserPass) {
		this.applicationUserPass = applicationUserPass;
	}

	public String getPoaNumber() {
		return poaNumber;
	}

	public void setPoaNumber(String poaNumber) {
		this.poaNumber = poaNumber;
	}

}

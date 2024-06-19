package com.pg.beans;

import java.io.Serializable;

public class Member implements Serializable{
	private String sName;
	private String sRole;
	
	public String getsName() {
		return sName;
	}
	public void setsName(String sName) {
		this.sName = sName;
	}
	public String getsRole() {
		return sRole;
	}
	public void setsRole(String sRole) {
		this.sRole = sRole;
	}
}

/*
 **   VeevaConfigXML.java
 **   Description - Introduced as part of Veeva integration.      
 **   JAXB bean 
 **
 */
package com.pg.dsm.veeva.vql.xml.binder;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="", propOrder = {
		"queries",
		"errors"
}) 
@XmlRootElement(name = "config") 
public class VeevaConfigXML {
	
	@XmlAttribute(name = "userkey")
    protected String userkey;
	
	@XmlAttribute(name = "uservalue")
    protected String uservalue;
	
	@XmlAttribute(name = "passkey")
    protected String passkey;
	
	@XmlAttribute(name = "passvalue")
    protected String passvalue;
	
	@XmlAttribute(name = "folder")
    protected String folder;
	
	@XmlElement(required = true)
	protected List<Queries> queries;

	public List<Queries> getQueries() {
		if (queries == null) {
			queries = new ArrayList<Queries>();
		}
		return this.queries;
	}
	@XmlElement(required = true)
	protected List<Errors> errors;

	public List<Errors> getErrors() {
		if (errors == null) {
			errors = new ArrayList<Errors>();
		}
		return this.errors;
	}

	public String getUserkey() {
		return userkey;
	}

	public void setUserkey(String userkey) {
		this.userkey = userkey;
	}

	public String getUservalue() {
		return uservalue;
	}

	public void setUservalue(String uservalue) {
		this.uservalue = uservalue;
	}

	public String getPasskey() {
		return passkey;
	}

	public void setPasskey(String passkey) {
		this.passkey = passkey;
	}

	public String getPassvalue() {
		return passvalue;
	}

	public void setPassvalue(String passvalue) {
		this.passvalue = passvalue;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}
	
}

/*
 **   DocumentCountry.java
 **   Description - Introduced as part of Veeva integration.      
 **   (jackson bean) 
 **
 */
package com.pg.dsm.veeva.vql.json.binder.document_property;

import com.fasterxml.jackson.annotation.JsonSetter;

public class DocumentCountry {
	String name;

	public String getName() {
		return name;
	}
	@JsonSetter("name__v")
	public void setName(String name) {
		this.name = name;
	}
	
}

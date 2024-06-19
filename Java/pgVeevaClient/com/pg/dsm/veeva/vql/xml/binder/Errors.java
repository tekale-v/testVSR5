/*
 **   Errors.java
 **   Description - Introduced as part of Veeva integration.      
 **   JAXB bean 
 **
 */
package com.pg.dsm.veeva.vql.xml.binder;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="", propOrder = {
		"error"
}) 
@XmlRootElement(name = "errors") 
public class Errors {
	@XmlElement(required = true)
	protected List<Error> error;
	
	public List<Error> getError() {
		if (error == null) {
			error = new ArrayList<Error>();
		}
		return this.error;
	}
}

/*
 **   NotEquals.java
 **   Description - Introduced as part of Veeva integration.      
 **   JAXB bean 
 **
 */
package com.pg.dsm.veeva.vql.xml.binder.conditional;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="", propOrder = {
		"notequal"
}) 
@XmlRootElement(name = "notequals")
public class NotEquals {
	
	@XmlElement(required = true)
	protected List<NotEqual> notequal;

	public List<NotEqual> getNotEquals() {
		if (notequal == null) {
			notequal = new ArrayList<NotEqual>();
		}
		return notequal;
	}

}

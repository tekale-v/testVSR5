/*
 **   Queries.java
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
		"query"
}) 
@XmlRootElement(name = "queries") 
public class Queries {
	
	@XmlElement(required = true)
	protected List<Query> query;
	
	public List<Query> getQuery() {
		if (query == null) {
			query = new ArrayList<Query>();
		}
		return this.query;
	}

}

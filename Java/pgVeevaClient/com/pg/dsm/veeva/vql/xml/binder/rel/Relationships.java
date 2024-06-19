/*
 **   Relationships.java
 **   Description - Introduced as part of Veeva integration.      
 **   JAXB bean 
 **
 */
package com.pg.dsm.veeva.vql.xml.binder.rel;


import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="", propOrder = {
		"relationship",
}) 
@XmlRootElement(name = "relationships")
public class Relationships {
	
	@XmlElement(required = true)
	protected List<Relationship> relationship;

	public List<Relationship> getRelationship() {
		if (relationship == null) {
			relationship = new ArrayList<Relationship>();
		}
		return relationship;
	}

}

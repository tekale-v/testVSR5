/*
 **   BusinessObject.java
 **   Description - Introduced as part of Veeva integration.      
 **   JAXB bean 
 **
 */
package com.pg.dsm.veeva.vql.xml.binder.bo;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="", propOrder = {
		"selectable",
}) 
@XmlRootElement(name = "businessobject")
public class BusinessObject {
	
	@XmlElement(required = true)
	protected List<Selectable> selectable;

	public List<Selectable> getSelectable() {
		if (selectable == null) {
			selectable = new ArrayList<Selectable>();
		}
		return selectable;
	}
	
	

}

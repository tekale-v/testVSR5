/*
 **   Where.java
 **   Description - Introduced as part of Veeva integration.      
 **   JAXB bean 
 **
 */
package com.pg.dsm.veeva.vql.xml.binder.conditional;

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
		"logical",
		"historical"
}) 
@XmlRootElement(name = "where")
public class Where {
	
	@XmlElement(required = true)
	protected List<Logical> logical;
	
	@XmlElement(required = true)
	protected List<Historical> historical;
	
	@XmlAttribute(name = "key")
	protected String key;
	
	public List<Logical> getLogical() {
		if (logical == null) {
			logical = new ArrayList<Logical>();
		}
		return logical;
	}
	public List<Historical> getHistorical() {
		if (historical == null) {
			historical = new ArrayList<Historical>();
		}
		return historical;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}

}

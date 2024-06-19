/*
 **   Rendition.java
 **   Description - Introduced as part of Veeva integration.      
 **   JAXB bean 
 **
 */
package com.pg.dsm.veeva.vql.xml.binder;

import javax.xml.bind.annotation.XmlAttribute;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="", propOrder = {
		""
}) 
@XmlRootElement(name = "rendition")
public class Rendition {
	@XmlAttribute(name = "key")
	protected String key;

	@XmlAttribute(name = "value")
	protected String value;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}

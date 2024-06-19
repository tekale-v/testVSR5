/*
 **   Renditions.java
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
		"rendition"
}) 
@XmlRootElement(name = "renditions") 
public class Renditions {
	@XmlAttribute(name = "key")
	protected String key;
	
	@XmlElement(required = true)
	protected List<Rendition> rendition;

	public List<Rendition> getRendition() {
		if (rendition == null) {
			rendition = new ArrayList<Rendition>();
		}
		return rendition;
	}
}

/*
 **   Headers.java
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
		"header"
}) 
@XmlRootElement(name = "headers") 
public class Headers {
	
	@XmlElement(required = true)
	protected List<Header> header;

	public List<Header> getHeader() {
		if (header == null) {
			header = new ArrayList<Header>();
		}
		return header;
	}

}

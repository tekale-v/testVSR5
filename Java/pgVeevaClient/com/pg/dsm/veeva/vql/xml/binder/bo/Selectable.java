/*
 **   Selectable.java
 **   Description - Introduced as part of Veeva integration.      
 **   JAXB bean 
 **
 */
package com.pg.dsm.veeva.vql.xml.binder.bo;


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
		"select",
}) 
@XmlRootElement(name = "selectable")
public class Selectable {
	
	@XmlElement(required = true)
	protected List<Select> select;
	
	@XmlAttribute(name = "selects")
	protected String selects;

	@XmlAttribute(name = "from")
	protected String from;
	
	@XmlAttribute(name = "type")
	protected String type;

	public List<Select> getSelect() {
		if (select == null) {
			select = new ArrayList<Select>();
		}
		return select;
	}

	public String getSelects() {
		return selects;
	}

	public void setSelects(String selects) {
		this.selects = selects;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	

}

/*
 **   XML.java
 **   Description - Introduced as part of Upload Market Clearance fature - 18x.5.
 **   JAXB Bean with getter/setter
 **
 */
package com.pg.pdf.bean.input.xml;

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
		"sections"
})
@XmlRootElement(name = "xml") 
public class XML {
	@XmlAttribute(name = "name")
    protected String name;
	@XmlElement(required = true)
	protected List<Sections> sections;
	public List<Sections> getSections() {
		if (sections == null) {
			sections = new ArrayList<Sections>();
		}
		return this.sections;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}	
}

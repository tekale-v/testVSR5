//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.04.10 at 10:09:18 AM EDT 
//


package com.pdfview.impl.FPP;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"name",
"title",
"source",
"rev",
"type",
"description",
"state",
"language"

})
public class ReferenceDocument {
	@XmlElement(name = "Name", namespace = "") 
	 protected String name;
	@XmlElement(name = "Title", namespace = "") 
	 protected String title;
	@XmlElement(name = "Source", namespace = "") 
	 protected String source;
	@XmlElement(name = "Rev", namespace = "") 
	 protected String rev;
	@XmlElement(name = "Type", namespace = "") 
	 protected String type;
	@XmlElement(name = "Description", namespace = "") 
	 protected String description;
	@XmlElement(name = "State", namespace = "") 
	 protected String state;
	@XmlElement(name = "Language", namespace = "") 
	 protected String language;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getRev() {
		return rev;
	}
	public void setRev(String rev) {
		this.rev = rev;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	
}
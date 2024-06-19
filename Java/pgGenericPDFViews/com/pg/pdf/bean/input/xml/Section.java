/*
 **   Checks.java
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
		"table"
})
@XmlRootElement(name = "section") 
public class Section {
	@XmlElement(required = true)
	protected List<Table> table;

	public List<Table> getTable() {
		if (table == null) {
			table = new ArrayList<Table>();
		}
		return this.table;
	}
	
	@XmlAttribute(name = "display")
    protected String fieldDisplay;
	@XmlAttribute(name = "name")
    protected String fieldName;
	@XmlAttribute(name = "view")
    protected String fieldView;
	public String getFieldView() {
		return fieldView;
	}
	public void setFieldView(String fieldView) {
		this.fieldView = fieldView;
	}
	@XmlAttribute(name = "hide")
	protected String fieldHide;
	public String getFieldDisplay() {
		return fieldDisplay;
	}
	public void setFieldDisplay(String fieldDisplay) {
		this.fieldDisplay = fieldDisplay;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	public String getFieldHide() {
		return fieldHide;
	}
	public void setFieldHide(String fieldHide) {
		this.fieldHide = fieldHide;
	}
	

}

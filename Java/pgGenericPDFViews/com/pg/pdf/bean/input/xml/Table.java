/*
 **   Columns.java
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
		"fields"
})
@XmlRootElement(name = "table")

public class Table {
	@XmlElement(required = true)
	protected List<Fields> fields;
	@XmlAttribute(name = "type")
    protected String fieldType;
	@XmlAttribute(name = "program")
    protected String fieldProgram;
	@XmlAttribute(name = "method")
    protected String fieldMethod;
	@XmlAttribute(name = "category")
    protected String tableCategory;
	
	public String getTableCategory() {
		return tableCategory;
	}

	public void setTableCategory(String tableCategory) {
		this.tableCategory = tableCategory;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public String getFieldProgram() {
		return fieldProgram;
	}

	public void setFieldProgram(String fieldProgram) {
		this.fieldProgram = fieldProgram;
	}

	public String getFieldMethod() {
		return fieldMethod;
	}

	public void setFieldMethod(String fieldMethod) {
		this.fieldMethod = fieldMethod;
	}

	public void setFields(List<Fields> fields) {
		this.fields = fields;
	}

	public List<Fields> getFields() {
		if (fields == null) {
			fields = new ArrayList<Fields>();
		}
		return this.fields;
	}
}

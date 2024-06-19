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
		"field"
})
@XmlRootElement(name = "fields")
public class Fields {
	@XmlElement(required = true)
	protected List<Field> field;
	@XmlAttribute(name = "name")
    protected String fieldName;
	@XmlAttribute(name = "filter")
	protected String filter;
	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public List<Field> getFields() {
		if (field == null) {
			field = new ArrayList<Field>();
		}
		return this.field;
	}
}

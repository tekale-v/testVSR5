/*
 **   QueryBuilder.java
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

import com.pg.dsm.veeva.vql.xml.binder.bo.BusinessObject;
import com.pg.dsm.veeva.vql.xml.binder.rel.Relationships;
import com.pg.dsm.veeva.vql.xml.binder.conditional.Where;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="", propOrder = {
		"businessobject",
		"relationships",
		"where"
}) 
@XmlRootElement(name = "querybuilder")
public class QueryBuilder {
	
	@XmlAttribute(name = "key")
	protected String key;

	@XmlAttribute(name = "operand")
	protected String operand;
	
	@XmlElement(required = true)
	protected List<BusinessObject> businessobject;
	
	@XmlElement(required = true)
	protected List<Relationships> relationships;
	
	@XmlElement(required = true)
	protected List<Where> where;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getOperand() {
		return operand;
	}

	public void setOperand(String operand) {
		this.operand = operand;
	}

	public List<BusinessObject> getBusinessobject() {
		if (businessobject == null) {
			businessobject = new ArrayList<BusinessObject>();
		}
		return businessobject;
	}
	
	public List<Relationships> getRelationships() {
		if (relationships == null) {
			relationships = new ArrayList<Relationships>();
		}
		return relationships;
	}

	public List<Where> getWhere() {
		if (where == null) {
			where = new ArrayList<Where>();
		}
		return where;
	}

}

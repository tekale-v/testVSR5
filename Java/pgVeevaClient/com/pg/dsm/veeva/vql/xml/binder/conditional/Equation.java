/*
 **   Equation.java
 **   Description - Introduced as part of Veeva integration.      
 **   JAXB bean 
 **
 */
package com.pg.dsm.veeva.vql.xml.binder.conditional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="", propOrder = {
		"",
}) 
@XmlRootElement(name = "equation")
public class Equation {
	
	@XmlAttribute(name = "key")
	protected String key;

	@XmlAttribute(name = "operator")
	protected String operator;
	
	@XmlAttribute(name = "value")
	protected String value;
	
	@XmlAttribute(name = "postoperand")
	protected String postoperand;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getPostoperand() {
		return postoperand;
	}

	public void setPostoperand(String postoperand) {
		this.postoperand = postoperand;
	}

}

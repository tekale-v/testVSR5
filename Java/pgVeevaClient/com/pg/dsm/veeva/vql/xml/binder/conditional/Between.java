/*
 **   Between.java
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
@XmlRootElement(name = "between")
public class Between {
	
	@XmlAttribute(name = "key")
	protected String key;

	@XmlAttribute(name = "operator")
	protected String operator;
	
	@XmlAttribute(name = "start_date")
	protected String start_date;
	
	@XmlAttribute(name = "operand")
	protected String operand;
	
	@XmlAttribute(name = "end_date")
	protected String end_date;
	
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

	public String getStartDate() {
		return start_date;
	}

	public void setStartDate(String start_date) {
		this.start_date = start_date;
	}

	public String getOperand() {
		return operand;
	}

	public void setOperand(String operand) {
		this.operand = operand;
	}

	public String getEndDate() {
		return end_date;
	}

	public void setEndDate(String end_date) {
		this.end_date = end_date;
	}

	public String getPostoperand() {
		return postoperand;
	}

	public void setPostoperand(String postoperand) {
		this.postoperand = postoperand;
	}
}

/*
 **   Logical.java
 **   Description - Introduced as part of Veeva integration.      
 **   JAXB bean 
 **
 */
package com.pg.dsm.veeva.vql.xml.binder.conditional;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="", propOrder = {
		"equation"
}) 
@XmlRootElement(name = "logical")
public class Logical {
	
	@XmlElement(required = true)
	protected List<Equation> equation;
	
	public List<Equation> getEquations() {
		if (equation == null) {
			equation = new ArrayList<Equation>();
		}
		return equation;
	}
}

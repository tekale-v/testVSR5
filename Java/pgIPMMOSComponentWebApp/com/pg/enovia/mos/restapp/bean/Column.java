
package com.pg.enovia.mos.restapp.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"name",
		"expr",
		"accessExpr",
		"program",
		"dispExpr"
})
public class Column {
	@XmlElement(name = "Name", namespace = "") 
	 protected String name;
	@XmlElement(name = "Expr", namespace = "") 
	 protected String expr;
	@XmlElement(name = "AccessExpr", namespace = "") 
	 protected String accessExpr;
	@XmlElement(name = "Program", namespace = "") 
	 protected String program;
	@XmlElement(name = "DispExpr", namespace = "") 
	 protected String dispExpr;
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the expr
	 */
	public String getExpr() {
		return expr;
	}
	/**
	 * @param expr the expr to set
	 */
	public void setExpr(String expr) {
		this.expr = expr;
	}
	/**
	 * @return the accessExpr
	 */
	public String getAccessExpr() {
		return accessExpr;
	}
	/**
	 * @param accessExpr the accessExpr to set
	 */
	public void setAccessExpr(String accessExpr) {
		this.accessExpr = accessExpr;
	}
	/**
	 * @return the program
	 */
	public String getProgram() {
		return program;
	}
	/**
	 * @param program the program to set
	 */
	public void setProgram(String program) {
		this.program = program;
	}
	/**
	 * @return the dispExpr
	 */
	public String getDispExpr() {
		return dispExpr;
	}
	/**
	 * @param dispExpr the dispExpr to set
	 */
	public void setDispExpr(String dispExpr) {
		this.dispExpr = dispExpr;
	}
}


package com.pg.enovia.mos.restapp.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "whereExpression", "tableMode", "defaultExpand","columns"

})
public class Preference {
	@XmlElement(name = "WhereExpression", namespace = "")
	protected String whereExpression;
	@XmlElement(name = "TableMode", namespace = "")
	protected String tableMode;
	@XmlElement(name = "DefaultExpand", namespace = "")
	protected String defaultExpand;

	@XmlElement(name = "Columns", namespace = "")
	protected Columns columns;

	/**
	 * @return the whereExpression
	 */
	public String getWhereExpression() {
		return whereExpression;
	}

	/**
	 * @param whereExpression the whereExpression to set
	 */
	public void setWhereExpression(String whereExpression) {
		this.whereExpression = whereExpression;
	}

	/**
	 * @return the tableMode
	 */
	public String getTableMode() {
		return tableMode;
	}

	/**
	 * @param tableMode the tableMode to set
	 */
	public void setTableMode(String tableMode) {
		this.tableMode = tableMode;
	}

	/**
	 * @return the defaultExpand
	 */
	public String getDefaultExpand() {
		return defaultExpand;
	}

	/**
	 * @param defaultExpand the defaultExpand to set
	 */
	public void setDefaultExpand(String defaultExpand) {
		this.defaultExpand = defaultExpand;
	}

	/**
	 * @return the columns
	 */
	public Columns getColumns() {
		return columns;
	}

	/**
	 * @param columns the columns to set
	 */
	public void setColumns(Columns columns) {
		this.columns = columns;
	}

	

}

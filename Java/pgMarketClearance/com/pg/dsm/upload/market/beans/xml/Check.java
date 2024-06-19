/*
 **   Check.java
 **   Description - Introduced as part of Upload Market Clearance feature - 18x.5.
 **   JAXB Bean with getter/setter
 **
 */
package com.pg.dsm.upload.market.beans.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        ""
})
@XmlRootElement(name = "check")
public class Check {
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "method")
    protected String method;
    @XmlAttribute(name = "description")
    protected String description;
    @XmlAttribute(name = "error")
    protected String error;
    protected String setter;
    @XmlAttribute(name = "column")
    protected String column;
    @XmlAttribute(name = "disabled")
    protected boolean disabled;
    @XmlTransient
    protected int rowNumber;
    @XmlTransient
    protected int columnNumber;
    @XmlTransient
    protected String columnName;
    @XmlTransient
    protected boolean isRequired;

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean isRequired) {
        this.isRequired = isRequired;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getSetter() {
        return setter;
    }

    public void setSetter(String setter) {
        this.setter = setter;
    }
}

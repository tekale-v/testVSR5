/*
 **   Column.java
 **   Description - Introduced as part of Upload Market Clearance feature - 18x.5.
 **   JAXB Bean with getter/setter
 **
 */
package com.pg.dsm.upload.market.beans.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "checks"
})
@XmlRootElement(name = "column")
public class Column {
    @XmlAttribute(name = "name")
    protected String columnName;
    @XmlAttribute(name = "required")
    protected boolean columnRequired;
    @XmlAttribute(name = "order")
    protected int columnNumber;
    @XmlAttribute(name = "type")
    protected String columnType;
    @XmlAttribute(name = "description")
    protected String columnDescription;
    @XmlAttribute(name = "setter")
    protected String columnSetter;
    @XmlAttribute(name = "range")
    protected String columnRange;
    @XmlAttribute(name = "error")
    protected String columnError;
    @XmlTransient
    protected String columnValue;
    @XmlTransient
    protected int rowNumber;
    @XmlAttribute(name = "schema")
    protected String schema;
    @XmlAttribute(name = "format")
    protected String columnFormat;
    @XmlAttribute(name = "formats")
    protected String columnFormats;
    @XmlElement(required = true)
    protected List<Checks> checks;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public boolean isColumnRequired() {
        return columnRequired;
    }

    public void setColumnRequired(boolean columnRequired) {
        this.columnRequired = columnRequired;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public String getColumnDescription() {
        return columnDescription;
    }

    public void setColumnDescription(String columnDescription) {
        this.columnDescription = columnDescription;
    }

    public String getColumnSetter() {
        return columnSetter;
    }

    public void setColumnSetter(String columnSetter) {
        this.columnSetter = columnSetter;
    }

    public String getColumnRange() {
        return columnRange;
    }

    public void setColumnRange(String columnRange) {
        this.columnRange = columnRange;
    }

    public String getColumnError() {
        return columnError;
    }

    public void setColumnError(String columnError) {
        this.columnError = columnError;
    }

    public String getColumnValue() {
        return columnValue;
    }

    public void setColumnValue(String columnValue) {
        this.columnValue = columnValue;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public List<Checks> getChecks() {
        if (checks == null) {
            checks = new ArrayList<>();
        }
        return this.checks;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getColumnFormat() {
        return columnFormat;
    }

    public void setColumnFormat(String columnFormat) {
        this.columnFormat = columnFormat;
    }

    public String getColumnFormats() {
        return columnFormats;
    }

    public void setColumnFormats(String columnFormats) {
        this.columnFormats = columnFormats;
    }
}

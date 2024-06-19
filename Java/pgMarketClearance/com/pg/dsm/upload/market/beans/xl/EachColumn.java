/*
 **   EachColumn.java
 **   Description - Introduced as part of Upload Market Clearance feature - 18x.5.
 **   Bean with getter/setter
 **
 */
package com.pg.dsm.upload.market.beans.xl;

import java.util.List;

public class EachColumn {
    private String columnName;
    private String columnDescription;
    private String columnType;
    private String columnError;
    private String columnRange;
    private String columnValue;
    private String columnSetter;
    private String columnFormat;
    private String columnFormats;
    private int rowNumber;
    private int columnNumber;
    private boolean columnRequired;
    private boolean isRequiredInputCheckPassed;
    private List<EachColumnCheck> columnCheckList;
    private List<String> validationMessageList;

    public List<EachColumnCheck> getColumnCheckList() {
        return columnCheckList;
    }

    public void setColumnCheckList(List<EachColumnCheck> columnCheckList) {
        this.columnCheckList = columnCheckList;
    }

    public List<String> getValidationMessageList() {
        return validationMessageList;
    }

    public void setValidationMessageList(List<String> validationMessageList) {
        this.validationMessageList = validationMessageList;
    }

    public String getColumnSetter() {
        return columnSetter;
    }

    public void setColumnSetter(String columnSetter) {
        this.columnSetter = columnSetter;
    }

    public boolean isRequiredInputCheckPassed() {
        return isRequiredInputCheckPassed;
    }

    public void setRequiredInputCheckPassed(boolean isRequiredInputCheckPassed) {
        this.isRequiredInputCheckPassed = isRequiredInputCheckPassed;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnValue() {
        return columnValue;
    }

    public void setColumnValue(String columnValue) {
        this.columnValue = columnValue;
    }

    public String getColumnDescription() {
        return columnDescription;
    }

    public void setColumnDescription(String columnDescription) {
        this.columnDescription = columnDescription;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }

    public boolean isColumnRequired() {
        return columnRequired;
    }

    public void setColumnRequired(boolean columnRequired) {
        this.columnRequired = columnRequired;
    }

    public String getColumnError() {
        return columnError;
    }

    public void setColumnError(String columnError) {
        this.columnError = columnError;
    }

    public String getColumnRange() {
        return columnRange;
    }

    public void setColumnRange(String columnRange) {
        this.columnRange = columnRange;
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

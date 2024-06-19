/*
 **   Checks.java
 **   Description - Introduced as part of Upload Market Clearance fature - 18x.5.
 **   JAXB Bean with getter/setter
 **
 */
package com.pg.pdf.bean.input.xml;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import javax.xml.bind.annotation.XmlRootElement;

import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="", propOrder = {
		""
})
@XmlRootElement(name = "fields") 
public class Field {
	
	@XmlAttribute(name = "label")
    protected String fieldLabel;
	@XmlAttribute(name = "name")
    protected String fieldName;
	@XmlAttribute(name = "relationship")
    protected String fieldrelationship;
	
	@XmlAttribute(name = "selectable")
    protected String fieldSelectable;
	@XmlAttribute(name = "program")
    protected String fieldProgram;
	@XmlAttribute(name = "method")
    protected String fieldMethod;
	@XmlAttribute(name = "value")
	protected String fieldValue;
	@XmlAttribute(name = "view")
	protected String fieldView;
	@XmlAttribute(name = "PDFViewDisplayMethod")
	protected String pdfViewDisplayMethod;
        
	//Modified by IRM pdf views 2018x.6 Feb_CW for Requirements 40810--Starts
	@XmlAttribute(name = "usePushContext")
	protected boolean usePushContext;
	@XmlAttribute(name = "usePushContextOnWhichView")
	protected String usePushContextOnWhichView;
	//Modified by IRM pdf views 2018x.6 Feb_CW for Requirements 40810--Ends

	public String getPdfViewDisplayMethod() {
		return pdfViewDisplayMethod;
	}
	public void setPdfViewDisplayMethod(String pdfViewDisplayMethod) {
		this.pdfViewDisplayMethod = pdfViewDisplayMethod;
	}
	public String getFieldrelationship() {
		return fieldrelationship;
	}
	public void setFieldrelationship(String fieldrelationship) {
		this.fieldrelationship = fieldrelationship;
	}
	
	public String getFieldView() {
		return fieldView;
	}
	public void setFieldView(String fieldView) {
		this.fieldView = fieldView;
	}
	public String getFieldLabel() {
		return fieldLabel;
	}
	public void setFieldLabel(String fieldLabel) {
		this.fieldLabel = fieldLabel;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getFieldSelectable() {
		return fieldSelectable;
	}
	public void setFieldSelectable(String fieldSelectable) {
		this.fieldSelectable = fieldSelectable;
	}
	public String getFieldProgram() {
		return fieldProgram;
	}
	public void setFieldProgram(String fieldProgram) {
		this.fieldProgram = fieldProgram;
	}
	public String getFieldMethod() {
		return fieldMethod;
	}
	public void setFieldMethod(String fieldMethod) {
		this.fieldMethod = fieldMethod;
	}
	public String getFieldValue() {
		return fieldValue;
	}
	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}
	//Modified by IRM pdf views 2018x.6 Feb_CW for Requirements 40810--Starts
	public boolean isUsePushContext() {
		return usePushContext;
	}

	public void setUsePushContext(boolean usePushContext) {
		this.usePushContext = usePushContext;
	}
	
	public String getUsePushContextOnWhichView() {
		return usePushContextOnWhichView;
	}

	public void setUsePushContextOnWhichView(String usePushContextOnWhichView) {
		this.usePushContextOnWhichView = usePushContextOnWhichView;
	}
	//Modified by IRM pdf views 2018x.6 Feb_CW for Requirements 40810--Ends
	}

/*
Java File Name: FaultBean
Clone From/Reference: NA
Purpose:  This File is used for Exception Handling
*/

package com.pdfview.exception;

public class FaultBean {

	private String errorMessage = "";
	private String errorCode = "";
	private String errorDetails="";
	
	/**
     * Gets the value of the errorMessage property.
     * @return String
     */
	public String getErrorMessage() {
		return errorMessage;
	}
	
	/**
     * Gets the value of the errorDetails property.
     * @return String
     */
	public String getErrorDetails() {
		return errorDetails;
	}
	
	/**
     * Sets the value of the errorDetails property.
     * @param value String 
     */
	public void setErrorDetails(String errorDetails) {
		this.errorDetails = errorDetails;
	}
	
	/**
     * Sets the value of the errorMessage property.
     * @param value String 
     */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	/**
     * Gets the value of the errorCode property.
     * @return String
     */
	public String getErrorCode() {
		return errorCode;
	}
	
	/**
     * Sets the value of the errorCode property.
     * @param value String 
     */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	
	
}

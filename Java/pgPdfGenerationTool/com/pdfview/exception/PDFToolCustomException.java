/*
Java File Name: DataHandlerCustomException
Clone From/Reference: NA
Purpose:  This File is used for Exception Handling
*/

package com.pdfview.exception;

import com.pdfview.canonicalPacket.CanonicalPacket;

public class PDFToolCustomException extends Exception {
	
	String message;
	String errorCode;
	String errorDetails;
	CanonicalPacket cp = null;
	int level = 4;
	
	public PDFToolCustomException(String message, String errorCode) {
		this.message = message;
		this.errorCode = errorCode;
	}
	
	public PDFToolCustomException(String message, String errorCode, String errorDetails ) {
		this.message = message;
		this.errorCode = errorCode;
		this.errorDetails =errorDetails;
	}
	
	public PDFToolCustomException(String message, String errorCode, CanonicalPacket cp, int level) {
		this.message = message;
		this.errorCode = errorCode;
		this.cp = cp;
		this.level = level;
	}
	
	public PDFToolCustomException(String message) {
		this.message = message;
	}
	
	/**
     * Gets the value of the message property.
     * @return String
     */
	@Override
	public String getMessage() {
		return this.message;
	}
	
	/**
     * Gets the value of the errorCode property.
     * @return String
     */
	public String getErrorCode() {
		return this.errorCode;
	}
	/**
     * Gets the value of the cp property.
     * @return CanonicalPacket
     */
	public CanonicalPacket getCanonicalPacket() {
		return this.cp;
	}
	/**
     * Gets the value of the level property.
     * @return int
     */
	public int getLevel() {
		return this.level;
	}
	/**
     * Gets the value of the errorDetails property.
     * @return String
     */
	public String getErrorDetails() {
		return this.errorDetails;
	}
	
}

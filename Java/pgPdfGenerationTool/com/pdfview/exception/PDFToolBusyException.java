/*
Java File Name: PDFToolBusyException
Clone From/Reference: NA
Purpose:  This File is used for Exception Handling
*/

package com.pdfview.exception;


@SuppressWarnings("serial")
public class PDFToolBusyException extends Exception {
	
	String message;
	String errorCode;
    public PDFToolBusyException(Throwable root) {
        super(root);
    }   
    
    public PDFToolBusyException(String string, Throwable root) {
        super(string, root);
    }
    
    public PDFToolBusyException(String s) {
        super(s);
    }
    
	public PDFToolBusyException(String message,String errorCode) {
		this.message=message;
		this.errorCode=errorCode;
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
	public String getErrorCode(){
		return this.errorCode;
	}
	  
}

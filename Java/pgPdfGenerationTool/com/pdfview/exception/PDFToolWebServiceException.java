/*
Java File Name: DataHandlerWebServiceException
Clone From/Reference: NA
Purpose:  This File is used for Exception Handling
*/

package com.pdfview.exception;

import javax.xml.ws.WebFault;

@WebFault(faultBean = "com.pdfview.exception.FaultBean")
public class PDFToolWebServiceException extends Exception {
	private FaultBean fb;
    public PDFToolWebServiceException(Throwable root) {
        super(root);
    }   
    
    public PDFToolWebServiceException(String strMessage, Throwable root) {
        super(strMessage, root);
    }
    
    public PDFToolWebServiceException(String strMessage) {
        super(strMessage);
    }
    public PDFToolWebServiceException(String strMessage, String strCode) {
        super(strMessage);
        FaultBean fb = new FaultBean();
        fb.setErrorCode(strCode);
        fb.setErrorMessage(strMessage);
        this.fb = fb;
    }
    
    public PDFToolWebServiceException(StringBuffer sbMessage, String strCode) {
        super(sbMessage.toString());
        FaultBean fb = new FaultBean();
        fb.setErrorCode(strCode);
        fb.setErrorMessage(sbMessage.toString());
        this.fb = fb;
    }
    
    /**
     * Gets the value of the fb property.
     * @return String
     */
    
    public String getErrorMessage() {
    	return fb.getErrorMessage();
    }
    
    /**
     * Gets the value of the fb property.
     * @return String
     */
    
    public String getErrorCode() {
    	return fb.getErrorCode();
    }
    
    /**
     * Gets the value of the fb property.
     * @return String
     */
    
    public String getErrorDetails(){
    	return fb.getErrorDetails();
    }
    
    public PDFToolWebServiceException(String strMessage, String strCode, String strErrorDetails) {
        super(strErrorDetails);
        FaultBean fb = new FaultBean();
        fb.setErrorCode(strCode);
        fb.setErrorDetails(strErrorDetails);
        fb.setErrorMessage(strMessage);
   
        this.fb = fb;
    }
}

/*
Java File Name: DataHandlerFieldIsListException
Clone From/Reference: NA
Purpose:  This File is used for Exception Handling
*/

package com.pdfview.exception;

public class PDFToolFieldIsListException extends Exception {
    public PDFToolFieldIsListException(Throwable root) {
        super(root);
    }   
    
    public PDFToolFieldIsListException(String string, Throwable root) {
        super(string, root);
    }
    
    public PDFToolFieldIsListException(String s) {
        super(s);
    }
}

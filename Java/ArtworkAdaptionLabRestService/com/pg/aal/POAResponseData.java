package com.pg.aal;

import java.util.ArrayList;
import java.util.HashMap;
public class POAResponseData {
	
	private String zipFileName;
	private String successCode;	
	private String errorCode;
	private String errorMessage;
	private String successMessage;
	private ArrayList<String> fileNames;
	private HashMap existingFileNames;
	
	public POAResponseData(String errorCode,String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public String getZipFileName() {
		return zipFileName;
	}

	public void setZipFileName(String zipFileName) {
		this.zipFileName = zipFileName;
	}
	
	public String getSuccessCode() {
		return successCode;
	}

	public void setSuccessCode(String successCode) {
		this.successCode = successCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public POAResponseData(String successCode, String successMessage, ArrayList<String> fileNames) {
		this.successCode = successCode;
		this.successMessage = successMessage;
		this.fileNames = fileNames;
	}
	
	public POAResponseData(String errorCode, String errorMessage, HashMap<String, String> existingFileNames, ArrayList<String> fileNames) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		this.existingFileNames = existingFileNames;
		this.fileNames = fileNames;
	}

}

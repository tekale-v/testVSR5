package com.pg.rulesengine;

public class POAResponseData {
	
	
	private String zipFileByteContent;
	private String zipFileName;
	private String successCode;	
	private String errorCode;
	private String errorMessage;
	

	
	public POAResponseData(String successCode,String  zipFileByteContent,String zipFileName) {
		this.zipFileByteContent = zipFileByteContent;
		this.zipFileName = zipFileName;
		this.successCode = successCode;
	}
	
	public POAResponseData(String errorCode,String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}


	
	public String getZipFileByteContent() {
		return zipFileByteContent;
	}



	public void setZipFileByteContent(String zipFileByteContent) {
		this.zipFileByteContent = zipFileByteContent;
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

}

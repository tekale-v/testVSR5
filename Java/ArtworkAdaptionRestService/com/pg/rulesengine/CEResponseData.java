package com.pg.rulesengine;

public class CEResponseData {
	
	
	private String zipFileByteContent;
	private String zipFileName;
	private String successCode;	
	private String successMessage;	
	private String errorCode;
	private String errorMessage;
	
	public CEResponseData(String successCode, String  zipFileByteContent, String zipFileName, String successMessage) {
		this.zipFileByteContent = zipFileByteContent;
		this.zipFileName = zipFileName;
		this.successCode = successCode;
		this.successMessage = successMessage;
	}
	
	public CEResponseData(String errorCode,String errorMessage, boolean error) {
		if(error){
			this.errorCode = errorCode;
			this.errorMessage = errorMessage;
		} else {
			this.successCode = errorCode;
			this.successMessage = errorMessage;
		}
	}
	//Added by RTA Capgemini Offshore for 22x.6 AUG_24_CW Defect 57579 Starts
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

	public String getSuccessMessage() {
		return successMessage;
	}

	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
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
	//Added by RTA Capgemini Offshore for 22x.6 AUG_24_CW Defect 57579 End
	
	
}

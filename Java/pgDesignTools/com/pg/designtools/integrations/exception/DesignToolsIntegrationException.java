/**
 * 
 */
package com.pg.designtools.integrations.exception;

import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.designtools.datamanagement.DataConstants;

/**
 * @author PTE2
 *
 */
// Make class name more generic = DesignToolsIntegrationException
public class DesignToolsIntegrationException extends RuntimeException {
	final int nErrorCode;
	final String strErrorMessage;

	public DesignToolsIntegrationException(int inputErrorCode, String inputErrorMessage) {
		super();
		nErrorCode = inputErrorCode;
		strErrorMessage = inputErrorMessage;
	}
	
	public DesignToolsIntegrationException(String inputErrorMessage) {
		super();
		strErrorMessage = inputErrorMessage;
		nErrorCode = 0;
	}

	public int getnErrorCode() {
		return nErrorCode;
	}

	public String getStrErrorMessage() {
		return strErrorMessage;
	}

	/**
	 * 
	 */
	public DesignToolsIntegrationException() {
		super();
		nErrorCode = 0;
		strErrorMessage = null;
	}
	
	/**
	 * This method would separate the actual message the exception string.
	 * @param strMessage
	 * @return String
	 */
	public String formatExceptionMessage(String strMessage) {
		if(UIUtil.isNotNullAndNotEmpty(strMessage)) {
			if(strMessage.contains("Exception")) {
				int iIndex=strMessage.lastIndexOf("Exception:");
				strMessage=strMessage.substring(iIndex+10,strMessage.length());
			}
			if(strMessage.contains("Severity")) {
				strMessage=strMessage.substring(0,strMessage.indexOf("Severity"));
			}
		}
		return strMessage.trim();
	}
	
	/**
	 * This method would return the exception code for customWorkProcessD2SExceptions enum
	 * @param strMessage
	 * @return int
	 */
	public int getD2SExceptionMessageCode(String strMessage) {
		int iCode=500;
		for(DataConstants.customWorkProcessD2SExceptions strException : DataConstants.customWorkProcessD2SExceptions.values()) { 
			if(strMessage.contains(strException.getExceptionMessage())) {
				iCode=strException.getExceptionCode();
				break;
			}
		}
		return iCode;
	}
	
	/**
	 * This method would return the exception code for customWorkProcessD2SExceptions enum
	 * @param strMessage
	 * @return int
	 */
	public String getD2SExceptionMessage(int iCode) {
		String strMessage="";
		for(DataConstants.customWorkProcessD2SExceptions strException : DataConstants.customWorkProcessD2SExceptions.values()) { 
			if(iCode==strException.getExceptionCode()) {
				strMessage=strException.getExceptionMessage();
				break;
			}
		}
		return strMessage;
	}
}

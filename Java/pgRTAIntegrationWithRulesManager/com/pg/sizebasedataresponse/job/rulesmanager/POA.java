package com.pg.sizebasedataresponse.job.rulesmanager;

public class POA {
	  private String poaNumber;
	  private String marketingName;
	  private String errorCode;
	  private String errorMessage;
	  private String warningMessage;
	  private String ruleManagerRequestID;
	public String getPoaNumber() {
		return poaNumber;
	}
	public void setPoaNumber(String poaNumber) {
		this.poaNumber = poaNumber;
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
	public String getWarningMessage() {
		return warningMessage;
	}
	public void setWarningMessage(String warningMessage) {
		this.warningMessage = warningMessage;
	}
	public String getMarketingName() {
		return marketingName;
	}
	public void setMarketingName(String marketingName) {
		this.marketingName = marketingName;
	}
	public String getRuleManagerRequestID() {
		return ruleManagerRequestID;
	}
	public void setRuleManagerRequestID(String ruleManagerRequestID) {
		this.ruleManagerRequestID = ruleManagerRequestID;
	}
	  
}

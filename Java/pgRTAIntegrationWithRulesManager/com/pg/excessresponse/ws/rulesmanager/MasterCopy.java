package com.pg.excessresponse.ws.rulesmanager;

import java.util.List;
//Added by RTA Capgemini Offshore for 18x.6 early-SIT Req 40330
public class MasterCopy {
//Added by by RTA Capgemini Offshore for 22x.3 Aug_23_CW Req 46916 - Starts	
	private String gs1copytype;
	private String mcnumber;
	private String marketingName;
	private String translate;
	private String inLineTranslation;
	private  String ilCopyListNumber ;
	public String getIlCopyListNumber() {
		return ilCopyListNumber;
	}
	public void setIlCopyListNumber(String ilCopyListNumber) {
		this.ilCopyListNumber = ilCopyListNumber;
	}
	public String getInLineTranslation() {
		return inLineTranslation;
	}
	public void setInLineTranslation(String inLineTranslation) {
		this.inLineTranslation = inLineTranslation;
	}
	private String instanceSequence;
	private String copyContentRTE;
	public String getCopyContentRTE() {
		return copyContentRTE;
	}
	public void setCopyContentRTE(String copyContentRTE) {
		this.copyContentRTE = copyContentRTE;
	}
	private String mcILType;
	private String mcPassType;
	private String mcRegulatoryClassification;
	private List<LocalCopy> localCopies;
	
	public String getGs1copytype() {
		return gs1copytype;
	}
	public void setGs1copytype(String gs1copytype) {
		this.gs1copytype = gs1copytype;
	}
	public String getMcnumber() {
		return mcnumber;
	}
	public void setMcnumber(String mcnumber) {
		this.mcnumber = mcnumber;
	}
	public String getMarketingName() {
		return marketingName;
	}
	public void setMarketingName(String marketingName) {
		this.marketingName = marketingName;
	}
	public String getTranslate() {
		return translate;
	}
	public void setTranslate(String translate) {
		this.translate = translate;
	}
	
	public String getInstanceSequence() {
		return instanceSequence;
	}
	public void setInstanceSequence(String instanceSequence) {
		this.instanceSequence = instanceSequence;
	}
	public String getMcILType() {
		return mcILType;
	}
	public void setMcILType(String mcILType) {
		this.mcILType = mcILType;
	}
	public String getMcPassType() {
		return mcPassType;
	}
	public void setMcPassType(String mcPassType) {
		this.mcPassType = mcPassType;
	}
	public String getMcRegulatoryClassification() {
		return mcRegulatoryClassification;
	}
	public void setMcRegulatoryClassification(String mcRegulatoryClassification) {
		this.mcRegulatoryClassification = mcRegulatoryClassification;
	}
	
	public List<LocalCopy> getLocalCopies() {
		return localCopies;
	}
	public void setLocalCopies(List<LocalCopy> localCopies) {
		this.localCopies = localCopies;
	}
//Added by by RTA Capgemini Offshore for 22x.3 Aug_23_CW Req 46916 - End
}

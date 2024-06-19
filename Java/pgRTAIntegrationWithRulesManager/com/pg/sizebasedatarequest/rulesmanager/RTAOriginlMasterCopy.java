package com.pg.sizebasedatarequest.rulesmanager;

import java.util.List;

public class RTAOriginlMasterCopy {
	//Modified by RTA Capgemini Offshore for 22x.5 April_24_CW Req 48844 Starts
	//Added by RTA Capgemini Offshore for 22x.6 AUG_24_CW Req 49949 Starts
	private String gs1copytype;
	private String mcnumber;
	private String marketingName;
	private String translate;
	private String inLineTranslation;
	private String copycontent;
	private String instanceSequence;
	private String sortId;
	private String subCopyType;
	private String mcValidityDate;
	private String mcRevision;
	private String mcILType;
	private String mcPassType;
	private String mcRegulatoryClassification;

	
	private List<com.pg.sizebasedatarequest.rulesmanager.RTAOriginlLocalCopy> rtaoriginlocalcopy;


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

	public String getInLineTranslation() {
		return inLineTranslation;
	}


	public void setInLineTranslation(String inLineTranslation) {
		this.inLineTranslation = inLineTranslation;
	}


	public String getCopycontent() {
		return copycontent;
	}


	public void setCopycontent(String copycontent) {
		this.copycontent = copycontent;
	}


	public String getInstanceSequence() {
		return instanceSequence;
	}


	public void setInstanceSequence(String instanceSequence) {
		this.instanceSequence = instanceSequence;
	}
	
	public String getSortId() {
		return sortId;
	}


	public void setSortId(String sortId) {
		this.sortId = sortId;
	}


	public String getSubCopyType() {
		return subCopyType;
	}


	public void setSubCopyType(String subCopyType) {
		this.subCopyType = subCopyType;
	}


	public String getMcValidityDate() {
		return mcValidityDate;
	}


	public void setMcValidityDate(String mcValidityDate) {
		this.mcValidityDate = mcValidityDate;
	}
	
	public String getMcRevision() {
		return mcRevision;
	}


	public void setMcRevision(String mcRevision) {
		this.mcRevision = mcRevision;
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


	public List<com.pg.sizebasedatarequest.rulesmanager.RTAOriginlLocalCopy> getRtaoriginlocalcopy() {
		return rtaoriginlocalcopy;
	}
	
	public void setRtaoriginlocalcopy(
			List<com.pg.sizebasedatarequest.rulesmanager.RTAOriginlLocalCopy> rtaoriginlocalcopy) {
		this.rtaoriginlocalcopy = rtaoriginlocalcopy;
	}
	//Modified by RTA Capgemini Offshore for 22x.5 April_24_CW Req 48844 Ends
	//Added by RTA Capgemini Offshore for 22x.6 AUG_24_CW Req 49949 Ends
	
	
}

package com.pg.addressdataresponse.ws.rulesmanager;

import java.util.List;

public class MasterCopy {
//Added by RTA Capgemini Offshore for 22x.4 Dec_CW Req 48239 Starts
	private String gs1copytype;
	private String mcnumber;
	private String marketingName;
	private String translate;
	//Added by RTA Capgemini Offshore for Dec CW 22x.4 Defect 55905   - Starts
	private String inLineTranslation;
	private String copyContentRTE;
	//Added by RTA Capgemini Offshore for Dec CW 22x.4 Defect 55905   - Ends
	private String instanceSequence;
	private String category;
	private String brand;
	private String plant;
	private String classification;
	private String sortId;
	private String subCopyType;
	private String euOrNoEuClassification;
	//Added by RTA Capgemini Offshore for Dec CW 22x.4 Defect 55402   - Starts
	private String mcValidityDate;
	private String country;
	//Added by RTA Capgemini Offshore for Dec CW 22x.4 Defect 55402   - End
    //Added by RTA Capgemini Offshore for Apr CW 22x.5 Defect 56049   - Starts
	private String mcRevision;
	

	public String getMcRevision() {
		return mcRevision;
	}
	public void setMcRevision(String mcRevision) {
		this.mcRevision = mcRevision;
	}
   //Added by RTA Capgemini Offshore for Apr CW 22x.5 Defect 56049 - End
	
	//Added by RTA Capgemini Offshore for Dec CW 22x.4 Defect 55905   - Starts
	private List<com.pg.addressdataresponse.ws.rulesmanager.LocalCopy> localCopies;
	
	public String getInLineTranslation() {
		return inLineTranslation;
	}

	public void setInLineTranslation(String inLineTranslation) {
		this.inLineTranslation = inLineTranslation;
	}
	
	public String getCopyContentRTE() {
		return copyContentRTE;
	}

	public void setCopyContentRTE(String copyContentRTE) {
		this.copyContentRTE = copyContentRTE;
	}
	//Added by RTA Capgemini Offshore for Dec CW 22x.4 Defect 55905   - Ends
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
	
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getPlant() {
		return plant;
	}

	public void setPlant(String plant) {
		this.plant = plant;
	}
	public String getClassification() {
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
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

	public String getEuOrNoEuClassification() {
		return euOrNoEuClassification;
	}

	public void setEuOrNoEuClassification(String euOrNoEuClassification) {
		this.euOrNoEuClassification = euOrNoEuClassification;
	}
	//Added by RTA Capgemini Offshore for Dec CW 22x.4 Defect 55402   - Starts
	public String getMcValidityDate() {
		return mcValidityDate;
	}

	public void setMcValidityDate(String mcValidityDate) {
		this.mcValidityDate = mcValidityDate;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	//Added by RTA Capgemini Offshore for Dec CW 22x.4 Defect 55402   - End
	//Added by RTA Capgemini Offshore for Dec CW 22x.4 Defect 55905   - Starts
	public List<com.pg.addressdataresponse.ws.rulesmanager.LocalCopy> getLocalCopies() {
		return localCopies;
	}

	public void setLocalCopies(List<com.pg.addressdataresponse.ws.rulesmanager.LocalCopy> localCopies) {
		this.localCopies = localCopies;
	}
	//Added by RTA Capgemini Offshore for Dec CW 22x.4 Defect 55905   - Ends
//Added by RTA Capgemini Offshore for 22x.4 Dec_CW Req 48239 End
}

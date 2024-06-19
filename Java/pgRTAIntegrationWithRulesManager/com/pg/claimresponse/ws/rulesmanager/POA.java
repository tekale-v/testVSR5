package com.pg.claimresponse.ws.rulesmanager;

import java.util.List;

//Added by RTA Capgemini Offshore for 18x.6 June_CW Requirement 42530

public class POA {
	  private String ruleManagerRule;
	  private String ruleManagerRequestID;
	  private String poaNumber;
	  private String poaCountries;
	  private String poaLanguages;
	  //Modified by RTA Capgemini Offshore for 22x.6 AUG_24_CW Req 49854 Starts
	  private String poaLanguageSequences;
	  //Modified by RTA Capgemini Offshore for 22x.6 AUG_24_CW Req 49854 End
	  private String claimsRequestId;
	 
	  private String region;
	  private String businessArea;
	  private String poaBrand;
	  private String poaProduct;
	  private String artworkUsage;	  
	  private String gcas;
	  private String fpc;
	  private String pmp;
	  private String dpp;
	  //Modified by RTA Capgemini Offshore for 22x.6 AUG_24_CW Req 49854 Starts
	  private String ilCopyListNumber; 
	  //Modified by RTA Capgemini Offshore for 22x.6 AUG_24_CW Req 49854 End
	  private String ilCopyListCountries;
	  private String ilCopyListLanguages;
	  private String longMarketingName;	  
	  private List<MasterCopy> masterCopy;
	  public String getRuleManagerRule() {
		return ruleManagerRule;
	}
	public void setRuleManagerRule(String ruleManagerRule) {
		this.ruleManagerRule = ruleManagerRule;
	}
	public String getRuleManagerRequestID() {
		return ruleManagerRequestID;
	}
	public void setRuleManagerRequestID(String ruleManagerRequestID) {
		this.ruleManagerRequestID = ruleManagerRequestID;
	}
	public String getPoaNumber() {
		return poaNumber;
	}
	public void setPoaNumber(String poaNumber) {
		this.poaNumber = poaNumber;
	}
	public String getPoaCountries() {
		return poaCountries;
	}
	public void setPoaCountries(String poaCountries) {
		this.poaCountries = poaCountries;
	}
	public String getPoaLanguages() {
		return poaLanguages;
	}
	public void setPoaLanguages(String poaLanguages) {
		this.poaLanguages = poaLanguages;
	}
	 //Modified by RTA Capgemini Offshore for 22x.6 AUG_24_CW Req 49854 Starts
	public String getPoaLanguageSequences() {
		return poaLanguageSequences;
	}

	public void setPoaLanguageSequences(String poaLanguageSequences) {
		this.poaLanguageSequences = poaLanguageSequences;
	}
	 //Modified by RTA Capgemini Offshore for 22x.6 AUG_24_CW Req 49854 End
	public String getClaimsRequestId() {
		return claimsRequestId;
	}
	public void setClaimsRequestId(String claimsRequestId) {
		this.claimsRequestId = claimsRequestId;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getBusinessArea() {
		return businessArea;
	}
	public void setBusinessArea(String businessArea) {
		this.businessArea = businessArea;
	}
	public String getPoaBrand() {
		return poaBrand;
	}
	public void setPoaBrand(String poaBrand) {
		this.poaBrand = poaBrand;
	}
	public String getPoaProduct() {
		return poaProduct;
	}
	public void setPoaProduct(String poaProduct) {
		this.poaProduct = poaProduct;
	}
	public String getArtworkUsage() {
		return artworkUsage;
	}
	public void setArtworkUsage(String artworkUsage) {
		this.artworkUsage = artworkUsage;
	}
	public String getGcas() {
		return gcas;
	}
	public void setGcas(String gcas) {
		this.gcas = gcas;
	}
	public String getFpc() {
		return fpc;
	}
	public void setFpc(String fpc) {
		this.fpc = fpc;
	}
	public String getPmp() {
		return pmp;
	}
	public void setPmp(String pmp) {
		this.pmp = pmp;
	}
	public String getDpp() {
		return dpp;
	}
	public void setDpp(String dpp) {
		this.dpp = dpp;
	}
	//Modified by RTA Capgemini Offshore for 22x.6 AUG_24_CW Req 49854 Starts
	public String getIlCopyListNumber() {
		return ilCopyListNumber;
	}
	public void setIlCopyListNumber(String ilCopyListNumber) {
		this.ilCopyListNumber = ilCopyListNumber;
	}
	//Modified by RTA Capgemini Offshore for 22x.6 AUG_24_CW Req 49854 End
	public void setIlCopyListCountries(String ilCopyListCountries) {
		this.ilCopyListCountries = ilCopyListCountries;
	}
	public String getIlCopyListLanguages() {
		return ilCopyListLanguages;
	}
	public void setIlCopyListLanguages(String ilCopyListLanguages) {
		this.ilCopyListLanguages = ilCopyListLanguages;
	}
	public String getLongMarketingName() {
		return longMarketingName;
	}
	public void setLongMarketingName(String longMarketingName) {
		this.longMarketingName = longMarketingName;
	}
	
	
	 
	
	public List<MasterCopy> getMasterCopy() {
		return masterCopy;
	}
	public void setMasterCopy(List<MasterCopy> masterCopy) {
		this.masterCopy = masterCopy;
	}
	
}
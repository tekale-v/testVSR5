package com.pg.excessresponse.ws.rulesmanager;

import java.util.List;
//Added by by RTA Capgemini Offshore for 22x.3 Aug_23_CW Req 46916 - Starts
import com.pg.response.ws.rulesmanager.MasterCopy;
//Added by by RTA Capgemini Offshore for 22x.3 Aug_23_CW Req 46916 - End

//Added by RTA Capgemini Offshore for 18x.6 early-SIT Req 40330


public class POA {
	//Added by by RTA Capgemini Offshore for 22x.3 Aug_23_CW Req 46916 - Starts
	  private String gcas;
	  private String ilCopyListNumber ;
	  private String ruleManagerRule ;
	  private String ruleManagerRequestID;
	  private String region;
	  private String businessArea;
	  private String poaNumber;
	  private String poaDescription;
	  private String poaLanguages;
	  private String poaBrand;
	  private String poaProduct;
	  private String artworkUsage;
	  private String poaRegulatoryClassification;
	  private String poaLanguageSequences;
	  private String longMarketingName;
	  private String integrationType;
	  private String environmentSource;
	  private List<com.pg.excessresponse.ws.rulesmanager.MasterCopy> masterCopy;
	  
	  public String getPoaRegulatoryClassification() {
		return poaRegulatoryClassification;
	}
	public void setPoaRegulatoryClassification(String poaRegulatoryClassification) {
		this.poaRegulatoryClassification = poaRegulatoryClassification;
	}
	public String getPoaLanguageSequences() {
		return poaLanguageSequences;
	}
	public void setPoaLanguageSequences(String poaLanguageSequences) {
		this.poaLanguageSequences = poaLanguageSequences;
	}
	public String getLongMarketingName() {
		return longMarketingName;
	}
	public void setLongMarketingName(String longMarketingName) {
		this.longMarketingName = longMarketingName;
	}
	public String getIntegrationType() {
		return integrationType;
	}
	public void setIntegrationType(String integrationType) {
		this.integrationType = integrationType;
	}
	public String getEnvironmentSource() {
		return environmentSource;
	}
	public void setEnvironmentSource(String environmentSource) {
		this.environmentSource = environmentSource;
	}
	public String getRuleManagerRequestID() {
		return ruleManagerRequestID;
	}
	public void setRuleManagerRequestID(String ruleManagerRequestID) {
		this.ruleManagerRequestID = ruleManagerRequestID;
	} 
	public String getGcas() {
		return gcas;
	}
	public void setGcas(String gcas) {
		this.gcas = gcas;
	}
	public String getIlCopyListNumber() {
		return ilCopyListNumber ;
	}
	public void setIlCopyListNumber(String ilCopyListNumber ) {
		this.ilCopyListNumber  = ilCopyListNumber ;
	}
	public String getRuleManagerRule() {
		return ruleManagerRule;
	}
	public void setRuleManagerRule(String ruleManagerRule) {
		this.ruleManagerRule = ruleManagerRule;
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
	public String getPoaNumber() {
		return poaNumber;
	}
	public void setPoaNumber(String poaNumber) {
		this.poaNumber = poaNumber;
	}
	public String getPoaDescription() {
		return poaDescription;
	}
	public void setPoaDescription(String poaDescription) {
		this.poaDescription = poaDescription;
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
	
	public String getPoaLanguages() {
		return poaLanguages;
	}
	public void setPoaLanguages(String poaLanguages) {
		this.poaLanguages = poaLanguages;
	}
	 public List<com.pg.excessresponse.ws.rulesmanager.MasterCopy> getMasterCopy() {
		return masterCopy;
	}
	public void setMasterCopy(List<com.pg.excessresponse.ws.rulesmanager.MasterCopy> masterCopy) {
		this.masterCopy = masterCopy;
	}
	//Added by by RTA Capgemini Offshore for 22x.3 Aug_23_CW Req 46916 - End
}
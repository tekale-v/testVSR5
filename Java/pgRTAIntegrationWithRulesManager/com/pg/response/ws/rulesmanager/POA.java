package com.pg.response.ws.rulesmanager;

import java.util.List;

public class POA {
	  private String gcas;
	  // Added by RTA Capgemini Offshore for 18x.6 June_CW Defect 48282  - Starts
	  private String ilCopyListNumber ;
	  // Added by RTA Capgemini Offshore for 18x.6 June_CW Defect 48282  - Ends
	  private String ruleManagerRule ;
	  private String ruleManagerRequestID;
	  private String region;
	  private String businessArea;
	  private String poaNumber;
	  private String poaDescription;
	  private String poaLanguages;
	  private String variantMCVariantCopy;
	  private String poaBrand;
	  private String poaProduct;
	  private String artworkUsage;
	  private List<MasterCopy> masterCopy;
	
	  public String getGcas() {
		return gcas;
	}
	public void setGcas(String gcas) {
		this.gcas = gcas;
	}
	// Added by RTA Capgemini Offshore for 18x.6 June_CW Defect 48282  - Starts
	public String getIlCopyListNumber() {
		return ilCopyListNumber ;
	}
	public void setIlCopyListNumber(String ilCopyListNumber ) {
		this.ilCopyListNumber  = ilCopyListNumber ;
	}
	// Added by RTA Capgemini Offshore for 18x.6 June_CW Defect 48282  - Ends
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
	public String getVariantMCVariantCopy() {
		return variantMCVariantCopy;
	}
	public void setVariantMCVariantCopy(String variantMCVariantCopy) {
		this.variantMCVariantCopy = variantMCVariantCopy;
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
	public List<MasterCopy> getMasterCopy() {
		return masterCopy;
	}
	public void setMasterCopy(List<MasterCopy> masterCopy) {
		this.masterCopy = masterCopy;
	}
	public String getPoaLanguages() {
		return poaLanguages;
	}
	public void setPoaLanguages(String poaLanguages) {
		this.poaLanguages = poaLanguages;
	}
}
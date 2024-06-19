package com.pg.claimsrequest.rulesmanager;

import java.util.List;

//Added by RTA Capgemini Offshore for 18x.6 June_CW Requirement 42530 
public class RTAOriginlMasterCopy {
	
	private String gs1copytype;
	private String mcnumber;
	private String marketingName;
	
	private String copycontent;
	//Added by RTA Capgemini Offshore for 22x.6 AUG_24_CW Req 49854 Starts
	private String instanceSequence;
	private String subCopyType;
	private String mcValidityDate;
	private String mcRevision;
    //Added by RTA Capgemini Offshore for 22x.6 AUG_24_CW Req 49854 End
	private String valueClaim;
	private String executionType;
	private String brand;
	private String subBrand;
	//Added by RTA Capgemini Offshore for 22x.6 AUG_24_CW Req 49854 Starts
	private String productform;
	private String packageComponentType;
	//Added by RTA Capgemini Offshore for 22x.6 AUG_24_CW Req 49854 End
	private String count;
	private String variant;
	private String lotion_Scent;
	
	private String size;
	
	//Added by RTA for 18x.6 AUG 22 CW ALM-42530 - Starts
	private String gcas; 
	private String fpc;
    private String pmp;
    private String dpp;
    private String app;
 //Added by RTA for 18x.6 AUG 22 CW ALM-42530 - End
 //Added by RTA for 22x May 23 CW ALM-46088 - Starts
    private String sortId;
	 public String getSortId() {
		return sortId;
	}
	public void setSortId(String sortId) {
		this.sortId = sortId;
	}
	//Added by RTA for 22x May 23 CW ALM-46088 - End
	//Added by RTA for 22x May 23 CW Defect-52638 - Starts
		private String translate;
		private String inlineTranslation;
		
		public String getTranslate() {
			return translate;
		}
		public void setTranslate(String translate) {
			this.translate = translate;
		}
		
		
		public String getInlineTranslation() {
			return inlineTranslation;
		}
		public void setInlineTranslation(String inlineTranslation) {
			this.inlineTranslation = inlineTranslation;
		}
		
	
	//Added by RTA for 22x May 23 CW Defect-52638 - End
	private List<RTAOriginLocalCopy> rtaoriginlocalcopy;
	//Added by RTA Capgemini Offshore for 22x.6 AUG_24_CW Req 49854 Starts
	public String getInstanceSequence() {
		return instanceSequence;
	}
	public void setInstanceSequence(String instanceSequence) {
		this.instanceSequence = instanceSequence;
	}
	//Added by RTA Capgemini Offshore for 22x.6 AUG_24_CW Req 49854 End
	public String getCopycontent() {
		return copycontent;
	}
	public void setCopycontent(String copycontent) {
		this.copycontent = copycontent;
	}
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
	//Added by RTA Capgemini Offshore for 22x.6 AUG_24_CW Req 49854 Starts
	public String getSubCopyType() {
		return subCopyType;
	}
	public void setSubCopyType(String subCopyType) {
		this.subCopyType = subCopyType;
	}
	public String getMcValidityDate() {
		return mcValidityDate;
	}
	//Added by RTA Capgemini Offshore for 22x.6 AUG_24_CW Req 49854 End
	public void setMcValidityDate(String mcValidityDate) {
		this.mcValidityDate = mcValidityDate;
	}
	public String getMcRevision() {
		return mcRevision;
	}
	public void setMcRevision(String mcRevision) {
		this.mcRevision = mcRevision;
	}
	//Added by RTA Capgemini Offshore for 22x.6 AUG_24_CW Req 49854 End
	public String getValueClaim() {
		return valueClaim;
	}
	public void setValueclaim(String valueClaim) {
		this.valueClaim = valueClaim;
	}
	public String getExecutionType() {
		return executionType;
	}
	public void setExecutiontype(String executionType) {
		this.executionType = executionType;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getSubBrand() {
		return subBrand;
	}
	public void setSubbrand(String subBrand) {
		this.subBrand = subBrand;
	}
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public String getVariant() {
		return variant;
	}
	public void setVariant(String variant) {
		this.variant = variant;
	}
	public String getLotion_Scent() {
		return lotion_Scent;
	}
	public void setlotion_scent(String lotion_Scent) {
		this.lotion_Scent = lotion_Scent;
	}
	public String getProductform() {
		return productform;
	}
	public void setProductform(String productform) {
		this.productform = productform;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	
	public String getPackageComponentType() {
		return packageComponentType;
	}
	public void setPackageComponentType(String packageComponentType) {
		this.packageComponentType = packageComponentType;
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
	public String getApp() {
		return app;
	}
	public void setApp(String app) {
		this.app = app;
	}
	//RTA 18x.6 AUG 22 CW - Added for ALM-42530 - Ends
	public List<RTAOriginLocalCopy> getRtaoriginlocalcopy() {
		return rtaoriginlocalcopy;
	}
	public void setRtaoriginlocalcopy(List<RTAOriginLocalCopy> rtaoriginlocalcopy) {
		this.rtaoriginlocalcopy = rtaoriginlocalcopy;
	}
}

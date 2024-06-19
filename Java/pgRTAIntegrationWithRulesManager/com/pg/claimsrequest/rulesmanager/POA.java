package com.pg.claimsrequest.rulesmanager;
 import java.util.List;




//Added by RTA Capgemini Offshore for 18x.6 June_CW Requirement 42530 
public class POA {
	//Added by RTA 22x Req 45690 Starts
    private String integrationType;
    private String environmentSource;
  //Added by RTA 22x Req 45690 Ends
	private String poaNumber;
    private String poaDescription;
    private String poaCountries;
    private String poaLanguages;
    //Added by RTA Capgemini Offshore for 22x.6 AUG_24_CW Req 49854 Starts
    private String poaLanguageSequences;
    
	private String gcas;
    private String poaRegulatoryClassification;
    private String paNumber;
    private String fcVersion;
    //Added by RTA Capgemini Offshore for 22x.6 AUG_24_CW Req 49854 End
	private String claimsRequestId;
	private String region;
	private String businessArea;
	//Added by RTA Capgemini Offshore for 22x.6 AUG_24_CW Req 49854 Starts
	private String poaCategory; 
	private String poaProductForm;
    private String poaSegment;
    private String poaBrand;
    private String poaSubBrand;
    private String poaProduct;
    private String longMarketingName;
    private String artworkUsage;
    private String ufiOnlineOfflineCode;
    private String productionPlant;
    private String productionPrimaryPlant;
    private String poaPackagingSite;
	private String artWorkPlant;
	private String poaCountryOfOrigin;
	private String customerOrConsumerUnit;
	private String poaConsumerBenefit1;
	private String poaConsumerBenefit2;
	private String poaConsumerBenefit3;
	private String netWeight;
	private String netWeightuom;
	//Added by RTA Capgemini Offshore for 22x.6 AUG_24_CW Req 49853 End
   
	private String fpc;
    private String pmp;
    private String dpp;
    
  //Added by RTA for 18x.6 AUG 22 CW ALM-42530 - Starts
    private String app;
  //Added by RTA for 18x.6 AUG 22 CW ALM-42530 - End 
	//Added by RTA Capgemini Offshore for 22x.6 AUG_24_CW Req 49854 Starts
    private String ilCopyListNumber;
    

	private String ilCopyListCountries;
    private String ilCopyListLanguages;
  
    private List<RTAOriginlMasterCopy> rtaoriginmastercopy;
    //Added by RTA 22x Req 45690 Starts
	public String getIntegrationType() {
		return this.integrationType;
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
	//Added by RTA 22x Req 45690 Ends
  
    public String getPoaNumber() {
		return poaNumber;
	}
	
    public List<RTAOriginlMasterCopy> getRtaoriginmastercopy() {
		return rtaoriginmastercopy;
	}
	public void setRtaoriginmastercopy(List<RTAOriginlMasterCopy> rtaoriginmastercopy) {
		this.rtaoriginmastercopy = rtaoriginmastercopy;
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
	public String getPoaLanguageSequences() {
		return poaLanguageSequences;
	}

	public void setPoaLanguageSequences(String poaLanguageSequences) {
		this.poaLanguageSequences = poaLanguageSequences;
	}

	
	public String getGcas() {
		return gcas;
	}
	public void setGcas(String gcas) {
		this.gcas = gcas;
	}
	public String getPoaRegulatoryClassification() {
		return poaRegulatoryClassification;
	}
	// Added by RTA Capgemini Offshore for 18x.6 June_CW Defect 48547 - Starts
    public void setPoaRegulatoryClassification(String poaRegulatoryClassification) {
		this.poaRegulatoryClassification = poaRegulatoryClassification;
	}
	 //Added by RTA Capgemini Offshore for 18x.6 June_22_CW Req 42532 starts
	public String getPaNumber() {
		return paNumber;
	}
	public void setPaNumber(String paNumber) {
		this.paNumber = paNumber;
	}
	public String getFcVersion() {
		return fcVersion;
	}

	public void setFcVersion(String fcVersion) {
		this.fcVersion = fcVersion;
	}
	public String getclaimsRequestId() {
		return claimsRequestId;
	}
	public void setclaimsRequestId(String claimsRequestId) {
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
	
	public String getPoaCategory() {
		return poaCategory;
	}

	public void setPoaCategory(String poaCategory) {
		this.poaCategory = poaCategory;
	}

	public String getPoaProductForm() {
		return poaProductForm;
	}

	public void setPoaProductForm(String poaProductForm) {
		this.poaProductForm = poaProductForm;
	}

	public String getPoaSegment() {
		return poaSegment;
	}

	public void setPoaSegment(String poaSegment) {
		this.poaSegment = poaSegment;
	}
	public String getPoaBrand() {
		return poaBrand;
	}
	public void setPoaBrand(String poaBrand) {
		this.poaBrand = poaBrand;
	}
	public String getPoaSubBrand() {
		return poaSubBrand;
	}

	public void setPoaSubBrand(String poaSubBrand) {
		this.poaSubBrand = poaSubBrand;
	}

	public String getPoaProduct() {
		return poaProduct;
	}
	public void setPoaProduct(String poaProduct) {
		this.poaProduct = poaProduct;
	}

	public String getLongMarketingName() {
		return longMarketingName;
	}
	public void setLongMarketingName(String longMarketingName) {
		this.longMarketingName = longMarketingName;
	}

	public String getArtworkUsage() {
		return artworkUsage;
	}
	public void setArtworkUsage(String artworkUsage) {
		this.artworkUsage = artworkUsage;
	}
	public String getUfiOnlineOfflineCode() {
		return ufiOnlineOfflineCode;
	}

	public void setUfiOnlineOfflineCode(String ufiOnlineOfflineCode) {
		this.ufiOnlineOfflineCode = ufiOnlineOfflineCode;
	}

	public String getProductionPlant() {
		return productionPlant;
	}

	public void setProductionPlant(String productionPlant) {
		this.productionPlant = productionPlant;
	}

	public String getProductionPrimaryPlant() {
		return productionPrimaryPlant;
	}

	public void setProductionPrimaryPlant(String productionPrimaryPlant) {
		this.productionPrimaryPlant = productionPrimaryPlant;
	}

	public String getPoaPackagingSite() {
		return poaPackagingSite;
	}

	public void setPoaPackagingSite(String poaPackagingSite) {
		this.poaPackagingSite = poaPackagingSite;
	}

	public String getArtWorkPlant() {
		return artWorkPlant;
	}

	public void setArtWorkPlant(String artWorkPlant) {
		this.artWorkPlant = artWorkPlant;
	}

	public String getPoaCountryOfOrigin() {
		return poaCountryOfOrigin;
	}

	public void setPoaCountryOfOrigin(String poaCountryOfOrigin) {
		this.poaCountryOfOrigin = poaCountryOfOrigin;
	}

	public String getCustomerOrConsumerUnit() {
		return customerOrConsumerUnit;
	}

	public void setCustomerOrConsumerUnit(String customerOrConsumerUnit) {
		this.customerOrConsumerUnit = customerOrConsumerUnit;
	}

	public String getPoaConsumerBenefit1() {
		return poaConsumerBenefit1;
	}

	public void setPoaConsumerBenefit1(String poaConsumerBenefit1) {
		this.poaConsumerBenefit1 = poaConsumerBenefit1;
	}

	public String getPoaConsumerBenefit2() {
		return poaConsumerBenefit2;
	}

	public void setPoaConsumerBenefit2(String poaConsumerBenefit2) {
		this.poaConsumerBenefit2 = poaConsumerBenefit2;
	}

	public String getPoaConsumerBenefit3() {
		return poaConsumerBenefit3;
	}

	public void setPoaConsumerBenefit3(String poaConsumerBenefit3) {
		this.poaConsumerBenefit3 = poaConsumerBenefit3;
	}

	public String getNetWeight() {
		return netWeight;
	}

	public void setNetWeight(String netWeight) {
		this.netWeight = netWeight;
	}

	public String getNetWeightuom() {
		return netWeightuom;
	}

	public void setNetWeightuom(String netWeightuom) {
		this.netWeightuom = netWeightuom;
	}
	public String getIlCopyListNumber() {
		return ilCopyListNumber;
	}

	public void setIlCopyListNumber(String ilCopyListNumber) {
		this.ilCopyListNumber = ilCopyListNumber;
	}
	public String getIlCopyListCountries() {
		return ilCopyListCountries;
	}
	public void setIlCopyListCountries(String ilCopyListCountries) {
		this.ilCopyListCountries = ilCopyListCountries;
	}
	public String getIlCopyListLanguages() {
		return ilCopyListLanguages;
	}
	public void setIlCopyListLanguages(String ilCopyListLanguages) {
		this.ilCopyListLanguages = ilCopyListLanguages;
	}
	
 	//Added by RTA Capgemini Offshore for 22x.6 AUG_24_CW Req 49854 End
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
		//Added by RTA for 18x.6 AUG 22 CW ALM-42530 - Starts
		public String getApp() {
			return app;
		}
		
		public void setApp(String app) {
			this.app = app;
		}
		//Added by RTA for 18x.6 AUG 22 CW ALM-42530 - End
}

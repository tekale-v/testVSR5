package com.pg.request.rulesmanager;
 import java.util.List;

public class POA {
	// Added RTA 22x Req 45604,45680 Starts
    private String integrationType;
	private String environmentSource;
	//Added RTA 22x Req 45604,45680 Ends
	private String poaNumber;
    private String poaDescription;
    private String poaCountries;
    private String poaLanguages;
    //Added by RTA Capgemini Offshore for 22x.6 AUG_24_CW Req 49856 Starts
    private String poaLanguageSequences;
	private String gcas;
 // Added by RTA Capgemini Offshore for 18x.6 June_CW Requirement 43230  - Starts
 	private String poaRegulatoryClassification;
 	// Added by RTA Capgemini Offshore for 18x.6 June_CW Requirement 43230  - Ends
    private String paNumber;
    private String fcVersion;
    private String fopStrategy;
    private String poaCLPWarning;
    private String region;
    private String businessArea;
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
	// Added by RTA Capgemini Offshore for 18x.6 June_CW Defect 48282  - Starts
	private String pmp ;
	// Added by RTA Capgemini Offshore for 18x.6 June_CW Defect 48282  - Ends
		
    private List<RMCopyList> ilCopyList;
    //Added by RTA Capgemini Offshore for 18x.6 June_22_CW Req 42532 ends
	
    private List<RTAOriginlMasterCopy> rtaoriginmastercopy;
	// Added RTA 22x Req 45604,45680 Starts
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
	// Added RTA 22x Req 45604,45680 End
    public String getPoaNumber() {
		return poaNumber;
	}

	public void setPoaNumber(String poaNumber) {
		this.poaNumber = poaNumber;
	}
	
    public List<RTAOriginlMasterCopy> getRtaoriginmastercopy() {
		return rtaoriginmastercopy;
	}
	public void setRtaoriginmastercopy(List<RTAOriginlMasterCopy> rtaoriginmastercopy) {
		this.rtaoriginmastercopy = rtaoriginmastercopy;
	}
	
	public String getPoaDescription() {
		return poaDescription;
	}
	public void setPoaDescription(String poaDescription) {
		this.poaDescription = poaDescription;
	}
	//Added by RTA Capgemini Offshore for 18x.6 June_22_CW Req 42532 ends
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
	// Added by RTA Capgemini Offshore for 18x.6 Oct_CW Req 40325  - Ends

	public String getGcas() {
		return gcas;
	}
	public void setGcas(String gcas) {
		this.gcas = gcas;
	}

	// Added by RTA Capgemini Offshore for 18x.6 June_CW Requirement 43230  - Starts
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
	public String getFopStrategy() {
		return fopStrategy;
	}

	public void setFopStrategy(String fopStrategy) {
		this.fopStrategy = fopStrategy;
	}

	public String getPoaCLPWarning() {
		return poaCLPWarning;
	}

	public void setPoaCLPWarning(String poaCLPWarning) {
		this.poaCLPWarning = poaCLPWarning;
	}
	
	 //Added by RTA Capgemini Offshore for 18x.6 June_22_CW Req 42532 ends
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
	// Added by RTA Capgemini Offshore for 18x.6 June_CW Defect 48282  - Starts
			public String getPmp() {
				return pmp;
			}
		    public void setPmp(String pmp) {
				this.pmp = pmp;
			}
	// Added by RTA Capgemini Offshore for 18x.6 June_CW Defect 48282  - Ends
	 //Added by RTA Capgemini Offshore for 18x.6 June_22_CW Req 42532 starts
	public List<RMCopyList> getCopyList() {
		return ilCopyList;
	}
	public void setCopyList(List<RMCopyList> ilCopyList) {
	this.ilCopyList = ilCopyList;
	}
	
  //Added by RTA Capgemini Offshore for 22x.6 AUG_24_CW Req 49856 End
		// Added by RTA Capgemini Offshore for 18x.6 June_CW Defect 48547 - Ends
		// Added by RTA Capgemini Offshore for 18x.6 June_CW Requirement 43230  - Ends
}

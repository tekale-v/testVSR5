package com.pg.addressdatarequest.rulesmanager;
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
    private String poaLanguageSequences;
	private String gcas;
	//Modified by RTA Capgemini Offshore for 22x.6 AUG_24_CW Req 49916 Starts
	private String poaRegulatoryClassification;
	private String poaCLPWarning;
    private String region;
    private String businessArea;
    private String poaCategory;
    private String poaProductForm;
    private String poaSegment;
    private String poaBrand;
    private String poaSubBrand;
    private String poaFlavourScentDetails;
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
    private String poaCrossSell;
	private String noOfDosesPerScoops;
	private String poaConsumerBenefit1;
	private String poaConsumerBenefit2;
	private String poaConsumerBenefit3;
	private String netWeight;
	private String netWeightuom;
	private String ilCopyListNumber;
	private String ilCLPWarning;
	private String ilCopyListCountries;
	private String ilCopyListLanguages;
	private List<RTAOriginlMasterCopy> rtaoriginmastercopy;
	
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
	public void setPoaRegulatoryClassification(String poaRegulatoryClassification) {
		this.poaRegulatoryClassification = poaRegulatoryClassification;
	}
	public String getPoaCLPWarning() {
		return poaCLPWarning;
	}
	public void setPoaCLPWarning(String poaCLPWarning) {
		this.poaCLPWarning = poaCLPWarning;
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
	public String getPoaFlavourScentDetails() {
		return poaFlavourScentDetails;
	}
	public void setPoaFlavourScentDetails(String poaFlavourScentDetails) {
		this.poaFlavourScentDetails = poaFlavourScentDetails;
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
	public String getPoaCrossSell() {
		return poaCrossSell;
	}
	public void setPoaCrossSell(String poaCrossSell) {
		this.poaCrossSell = poaCrossSell;
	}
	public String getNoOfDosesPerScoops() {
		return noOfDosesPerScoops;
	}
	public void setNoOfDosesPerScoops(String noOfDosesPerScoops) {
		this.noOfDosesPerScoops = noOfDosesPerScoops;
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
	public String getIlCLPWarning() {
		return ilCLPWarning;
	}
	public void setIlCLPWarning(String ilCLPWarning) {
		this.ilCLPWarning = ilCLPWarning;
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
	public List<RTAOriginlMasterCopy> getRtaoriginmastercopy() {
		return rtaoriginmastercopy;
	}
	public void setRtaoriginmastercopy(List<RTAOriginlMasterCopy> rtaoriginmastercopy) {
		this.rtaoriginmastercopy = rtaoriginmastercopy;
	}
	//Modified by RTA Capgemini Offshore for 22x.6 AUG_24_CW Req 49916 Ends
	
}

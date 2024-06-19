package com.pg.orchestration;
public class PgPoaRequestPoaData
	{
	    
	    private String uniqueID;

	    private String brand;

		//Modified by RTA Capgemini Offshore 18x.5 CW defect-37335 Starts
    	private String brandRegion;
		//Modified by RTA Capgemini Offshore 18x.5 CW defect-37335 Ends

	    private String phaseOutPOA;

	    private String referencePMP;

	    private String packagingComponentType;

	    private String country;

	    private String language;

	    private String productForm;

	    private String packagingLevel;

	    private String GCAS;

	    private String paNumber;

	    private String suppressedGTIN;

	    private String materialType;

	    private String noOfDosesPerScoops;

	    private String recommendedDosage;

	    private String netWeight;

	    private String pmp;

	    private String securityCategoryClassification;

	    private String finishedproductcode;
	    
	    private String multiplefpc;
	    
	    private String recommendedDosageuom;
	    
	    private String netWeightuom;

		//Added by RTA Capgemini Offshore 18x.5 CW defect-36820 Starts		
		private String pmpDescription;
		//Added by RTA Capgemini Offshore 18x.5 CW defect-36820 Ends
		
		//Added by RTA Capgemini offshore 2018x.6 req-36492 Starts
	    private String gpsFixedCES;
	    
		private String gpsVariableCES;
	    
	    private String mrkClaimCES;
	    //Added by RTA Capgemini offshore 2018x.6 req-36492 Ends
	    
	    //Added by RTA Capgemini offshore 2018x.6 req-36133 Starts
	    private String harmonizePOA;
	    //Added by RTA Capgemini offshore 2018x.6 req-36133 Ends
	    //Added by RTA for 22x May 23 CW Requirement-46090 - Starts
		private String productionPlant; 
		private String productionPlantPrimary; 
		//Added by RTA for 22x May 23 CW Requirement-46090 - End
		 //Added by RTA for 22x Aug 23 CW ALM-47299 -Starts
	    private String artWorkPlant;
		//Added by RTA for 22x Aug 23 CW ALM-47299 -Ends
        //Added by RTA for 22x.5 APR_24_CW ALM-Req-48395 & 48396 & 48814 & 49052 - Starts
		private String packagingSite;
		private String dpp;
		//Added by RTA for 22x.5 APR_24_CW ALM-Req-48395 & 48396 & 48814 & 49052 - Ends
	    //Added by RTA for 22x.5 APR_24_CW ALM-Req-49054 & 49055 - Starts
	    private String category;
		private String crossSell;
		private String syntheticPerfumeFlag;
		private String subBrand;
		private String flavourScentDetails;
		private String consumerBenefitOne;
		private String consumerBenefitTwo;
		private String consumerBenefitThree;
		//Added by RTA for 22x.5 APR_24_CW ALM-Req-49054 & 49055 - Ends
		//Added by RTA for 22x.6 AUG_24_CW ALM-Req-49750 & 49843 - Starts
		private String countryOfOrigin;
		private String segment;
		//Added by RTA for 22x.6 AUG_24_CW ALM-Req-49750 & 49843- Ends
		//Added by RTA for 22x Dec 23 CW Req-47799 Starts
	    private String source;
	    public String getSource() {
			return source;
		}
		public void setSource(String source) {
			this.source = source;
		}
	  	//Added by RTA for 22x Dec 23 CW Req-47799 Ends	
	    	// Added by RTA Capgemini offshore 2018x.6 req-43229 Starts
	private String regulatoryClassification;

	public String getRegulatoryClassification() {
		return regulatoryClassification;
	}
	// Added by RTA Capgemini offshore 2018x.6 req-43229 Ends

	// Added by RTA Capgemini offshore 2018x.6 req-43432 Starts
	private String ECAPS_ClaimReq;

	public String getECAPS_ClaimReq() {
		return ECAPS_ClaimReq;
	}
	// Added by RTA Capgemini offshore 2018x.6 req-43432 Ends

	// Added by RTA Capgemini offshore 2018x.6 req-39244 Starts
	// Added by RTA Capgemini offshore 2018x.6 req-39244 Ends
	//Added by RTA for 22x May 23 CW Requirement-46090 - Starts
	//Added by RTA for 22x May 23 CW Defect-52685 -Starts
	public void setProductionPlant(String productionPlant) {
			this.productionPlant = productionPlant;
		}
		//Added by RTA for 22x May 23 CW Defect-52516 -Starts
		public String getProductionPlant() {
			return productionPlant;
		}
	//Added by RTA for 22x May 23 CW Defect-52685 -End
	//Added by RTA for 22x May 23 CW Defect-52516 -Starts
	 public String getProductionPlantPrimary() {
		return productionPlantPrimary;
	}
	//Added by RTA for 22x May 23 CW Defect-52516 -End
	public void setProductionPlantPrimary(String productionPlantPrimary) {
				this.productionPlantPrimary = productionPlantPrimary;
	
	}
	//Added by RTA for 22x May 23 CW Requirement-46090 - End
	 //Added by RTA for 22x Aug 23 CW ALM-47299 -Starts
	 public String getArtWorkPlant() {
			return artWorkPlant;
		}
		public void setArtWorkPlant(String artWorkPlant) {
			this.artWorkPlant = artWorkPlant;
		}
		//Added by RTA for 22x Aug 23 CW ALM-47299 -Ends
		//Added by RTA for 22x.5 APR_24_CW ALM-Req-48395 & 48396 & 48814 & 49052 - Starts
		public String getPackagingSite() {
				return packagingSite;
			}
			public void setPackagingSite(String packagingSite) {
				this.packagingSite = packagingSite;
			}
		public String getDpp() {
			return dpp;
		}
		public void setDpp(String dpp) {
			this.dpp = dpp;
		}
		//Added by RTA for 22x.5 APR_24_CW ALM-47900 & 42342 -Ends
		//Added by RTA for 22x.5 APR_24_CW ALM-Req-49054 & 49055 - Starts
		public String getCategory() {
			return category;
		}
		public void setCategory(String category) {
			this.category = category;
		}
		public String getCrossSell() {
			return crossSell;
		}
		public void setCrossSell(String crossSell) {
			this.crossSell = crossSell;
		}
		public String getSyntheticPerfumeFlag() {
			return syntheticPerfumeFlag;
		}
		public void setSyntheticPerfumeFlag(String syntheticPerfumeFlag) {
			this.syntheticPerfumeFlag = syntheticPerfumeFlag;
		}
		public String getSubBrand() {
			return subBrand;
		}
		public void setSubBrand(String subBrand) {
			this.subBrand = subBrand;
		}
		public String getFlavourScentDetails() {
			return flavourScentDetails;
		}
		public void setFlavourScentDetails(String flavourScentDetails) {
			this.flavourScentDetails = flavourScentDetails;
		}
		public String getConsumerBenefitOne() {
			return consumerBenefitOne;
		}
		public void setConsumerBenefitOne(String consumerBenefitOne) {
			this.consumerBenefitOne = consumerBenefitOne;
		}
		public String getConsumerBenefitTwo() {
			return consumerBenefitTwo;
		}
		public void setConsumerBenefitTwo(String consumerBenefitTwo) {
			this.consumerBenefitTwo = consumerBenefitTwo;
		}
		public String getConsumerBenefitThree() {
			return consumerBenefitThree;
		}
		public void setConsumerBenefitThree(String consumerBenefitThree) {
			this.consumerBenefitThree = consumerBenefitThree;
		}
		//Added by RTA for 22x.5 APR_24_CW ALM-Req-49054 & 49055 - Ends
		//Added by RTA for 22x.6 AUG_24_CW ALM-Req-49750 & 49843 - Starts
		public String getCountryOfOrigin() {
			return countryOfOrigin;
		}
		public void setCountryOfOrigin(String countryOfOrigin) {
			this.countryOfOrigin = countryOfOrigin;
		}		
		public String getSegment() {
			return segment;
		}
		public void setSegment(String segment) {
			this.segment = segment;
		}
		//Added by RTA for 22x.6 AUG_24_CW ALM-Req-49750 & 49843 - Ends
	    //Added by RTA Capgemini Offshore for 18x.6 May_CW Requirement 42564,42669,42819,42563,36128 -- START
		private String piFPCDescription;
	  //Added by RTA Capgemini Offshore for 18x.6 May_CW Requirement 42564,42669,42819,42563,36128 -- Ends
		// Added by RTA Capgemini Offshore for 18x.6 JUNE_CW Req 42907   - Starts
		
		//Added by RTA Capgemini Offshore for 22x.5 April_24_CW Req 49282,49283 Starts 
		private String awmProjectId;
		private String awmProjectName;
		private String awmSupplier;
		private String awmArtWorkAssignee;
		//Added by RTA Capgemini Offshore for 22x.5 April_24_CW Req 49282,49283 Ends
		
		private String fcVersion;
		
		public void setFcVersion(String fcVersion){
	        this.fcVersion = fcVersion;
	    }
	    public String getFcVersion(){
	        return this.fcVersion;
	    }
		// Added by RTA Capgemini Offshore for 18x.6 JUNE_CW Req 42907   - Ends
		
		
		//Added by RTA Capgemini offshore 2018x.6 req-36492 Starts
	    public String getGpsFixedCES() {
			return gpsFixedCES;
		}
	    //Added by RTA Capgemini offshore 2018x.6 req-36492 Ends
		
		//Added by RTA Capgemini offshore 2018x.6 req-36492 Starts
		public void setGpsFixedCES(String gpsFixedCES) {
			this.gpsFixedCES = gpsFixedCES;
		}
		//Added by RTA Capgemini offshore 2018x.6 req-36492 Ends
		
		//Added by RTA Capgemini offshore 2018x.6 req-36492 Starts
		public String getGpsVariableCES() {
			return gpsVariableCES;
		}
		//Added by RTA Capgemini offshore 2018x.6 req-36492 Ends
		
		//Added by RTA Capgemini offshore 2018x.6 req-36492 Starts
		public void setGpsVariableCES(String gpsVariableCES) {
			this.gpsVariableCES = gpsVariableCES;
		}
		//Added by RTA Capgemini offshore 2018x.6 req-36492 Ends
		
		//Added by RTA Capgemini offshore 2018x.6 req-36492 Starts
		public String getMrkClaimCES() {
			return mrkClaimCES;
		}
		//Added by RTA Capgemini offshore 2018x.6 req-36492 Ends
		
		//Added by RTA Capgemini offshore 2018x.6 req-36492 Starts
		public void setMrkClaimCES(String mrkClaimCES) {
			this.mrkClaimCES = mrkClaimCES;
		}
	    //Added by RTA Capgemini offshore 2018x.6 req-36492 Ends
		
		//Added by RTA Capgemini offshore 2018x.6 req-36133 Starts
		public String getHarmonizePOA() {
			return harmonizePOA;
		}
		//Added by RTA Capgemini offshore 2018x.6 req-36133 Ends
		
		//Added by RTA Capgemini offshore 2018x.6 req-36133 Starts
		public void setHarmonizePOA(String harmonizePOA) {
			this.harmonizePOA = harmonizePOA;
		}
	    //Added by RTA Capgemini offshore 2018x.6 req-36133 Ends
		
	    //netWeightuom
	    public void setnetWeightuom(String netWeightuom){
	        this.netWeightuom = netWeightuom;
	    }
	    public String getnetWeightuom(){
	        return this.netWeightuom;
	    }
		
		//Added by RTA Capgemini Offshore 18x.5 CW defect-36820 Starts
		//pmpDescription
		public void setpmpDescription(String pmpDescription) {
        	this.pmpDescription = pmpDescription;
    	}
	    //Added by RTA Capgemini Offshore 18x.5 CW defect-36820 Ends
		
		//Added by RTA Capgemini Offshore 18x.5 CW defect-36820 Starts
    	public String getpmpDescription() {
        	return this.pmpDescription;
    	}
    	//Added by RTA Capgemini Offshore 18x.5 CW defect-36820 Ends
		
	    //recommendedDosageuom
	    public void setrecommendedDosageuom(String recommendedDosageuom){
	        this.recommendedDosageuom = recommendedDosageuom;
	    }
	    public String getrecommendedDosageuom(){
	        return this.recommendedDosageuom;
	    }
		//Modified by RTA Capgemini Offshore for 22x.4 Dec_23_CW defect 55686 Starts
	    public String getFinishedproductcode() {
		return finishedproductcode;
		}
		public void setFinishedproductcode(String finishedproductcode) {
		this.finishedproductcode = finishedproductcode;
		}
		//Modified by RTA Capgemini Offshore for 22x.4 Dec_23_CW defect 55686 Ends
	    public void setmultiplefpc(String multiplefpc){
	        this.multiplefpc = multiplefpc;
	    }
	    public String getmultiplefpc(){
	        return this.multiplefpc;
	    }
	    

	    public void setUniqueID(String uniqueID){
	        this.uniqueID = uniqueID;
	    }
	    public String getUniqueID(){
	        return this.uniqueID;
	    }
	    public void setBrand(String brand){
	        this.brand = brand;
	    }
	    public String getBrand(){
	        return this.brand;
	    }
		//Start modified for 22x changes - Matching the method name with keys from request body
		//Modified by RTA Capgemini Offshore 18x.5 CW defect-37335 Starts
	    public void setBrandRegion(String brandRegion){
	        this.brandRegion = brandRegion;
	    }
		//Modified by RTA Capgemini Offshore 18x.5 CW defect-37335 Ends
		
		//Modified by RTA Capgemini Offshore 18x.5 CW defect-37335 Starts
	    public String getBrandRegion(){
	        return this.brandRegion;
	    }
		//Modified by RTA Capgemini Offshore 18x.5 CW defect-37335 Ends
		//End modified for 22x changes - Matching the method name with keys from request body
		
	    public void setPhaseOutPOA(String phaseOutPOA){
	        this.phaseOutPOA = phaseOutPOA;
	    }
	    public String getPhaseOutPOA(){
	        return this.phaseOutPOA;
	    }
	    public void setReferencePMP(String referencePMP){
	        this.referencePMP = referencePMP;
	    }
	    public String getReferencePMP(){
	        return this.referencePMP;
	    }
	    public void setPackagingComponentType(String packagingComponentType){
	        this.packagingComponentType = packagingComponentType;
	    }
	    public String getPackagingComponentType(){
	        return this.packagingComponentType;
	    }
	    public void setCountry(String country){
	        this.country = country;
	    }
	    public String getCountry(){
	        return this.country;
	    }
	    public void setLanguage(String language){
	        this.language = language;
	    }
	    public String getLanguage(){
	        return this.language;
	    }
	    public void setProductForm(String productForm){
	        this.productForm = productForm;
	    }
	    public String getProductForm(){
	        return this.productForm;
	    }
	    public void setPackagingLevel(String packagingLevel){
	        this.packagingLevel = packagingLevel;
	    }
	    public String getPackagingLevel(){
	        return this.packagingLevel;
	    }
	    public void setGCAS(String GCAS){
	        this.GCAS = GCAS;
	    }
	    public String getGCAS(){
	        return this.GCAS;
	    }
	    public void setPaNumber(String paNumber){
	        this.paNumber = paNumber;
	    }
	    public String getPaNumber(){
	        return this.paNumber;
	    }
	    public void setSuppressedGTIN(String suppressedGTIN){
	        this.suppressedGTIN = suppressedGTIN;
	    }
	    public String getSuppressedGTIN(){
	        return this.suppressedGTIN;
	    }
	    public void setMaterialType(String materialType){
	        this.materialType = materialType;
	    }
	    public String getMaterialType(){
	        return this.materialType;
	    }
	    public void setNoOfDosesPerScoops(String noOfDosesPerScoops){
	        this.noOfDosesPerScoops = noOfDosesPerScoops;
	    }
	    public String getNoOfDosesPerScoops(){
	        return this.noOfDosesPerScoops;
	    }
	    public void setRecommendedDosage(String recommendedDosage){
	        this.recommendedDosage = recommendedDosage;
	    }
	    public String getRecommendedDosage(){
	        return this.recommendedDosage;
	    }
	    public void setNetWeight(String netWeight){
	        this.netWeight = netWeight;
	    }
	    public String getNetWeight(){
	        return this.netWeight;
	    }
	    public void setPmp(String pmp){
	        this.pmp = pmp;
	    }
	    public String getPmp(){
	        return this.pmp;
	    }
	    public void setSecurityCategoryClassification(String securityCategoryClassification){
	        this.securityCategoryClassification = securityCategoryClassification;
	    }
	    public String getSecurityCategoryClassification(){
	        return this.securityCategoryClassification;
	    }
	    
	    //Added by RTA Capgemini Offshore for 18x.6 May_CW Requirement 42564,42669,42819,42563,36128 -- START
	    public String getPmpDescription() {
			return pmpDescription;
		}

		public void setPmpDescription(String pmpDescription) {
			this.pmpDescription = pmpDescription;
		}

		

		public String getPiFPCDescription() {
			return piFPCDescription;
		}

		public void setPiFPCDescription(String piFPCDescription) {
			this.piFPCDescription = piFPCDescription;
		}

		//Added by RTA Capgemini Offshore for 18x.6 May_CW Requirement 42564,42669,42819,42563,36128 -- Ends
		
		//Added by RTA Capgemini Offshore for 22x.5 April_24_CW Req 49282,49283 Starts
		public String getAwmProjectId() {
			return awmProjectId;
		}
		public void setAwmProjectId(String awmProjectId) {
			this.awmProjectId = awmProjectId;
		}
		public String getAwmProjectName() {
			return awmProjectName;
		}
		public void setAwmProjectName(String awmProjectName) {
			this.awmProjectName = awmProjectName;
		}
		public String getAwmSupplier() {
			return awmSupplier;
		}
		public void setAwmSupplier(String awmSupplier) {
			this.awmSupplier = awmSupplier;
		}
		public String getAwmArtWorkAssignee() {
			return awmArtWorkAssignee;
		}
		public void setAwmArtWorkAssignee(String awmArtWorkAssignee) {
			this.awmArtWorkAssignee = awmArtWorkAssignee;
		}
		//Added by RTA Capgemini Offshore for 22x.5 April_24_CW Req 49282,49283 Ends
	// Added by RTA Capgemini offshore 2018x.6 req-43229 Starts
	public void setRegulatoryClassification(String regulatoryClassification) {
		this.regulatoryClassification = regulatoryClassification;
	}
	// Added by RTA Capgemini offshore 2018x.6 req-43229 Ends

	// Added by RTA Capgemini offshore 2018x.6 req-43432 Starts
	public void setECAPS_ClaimReq(String ECAPS_ClaimReq) {
		this.ECAPS_ClaimReq = ECAPS_ClaimReq;
	}
	// Added by RTA Capgemini offshore 2018x.6 req-43432 Ends
	}

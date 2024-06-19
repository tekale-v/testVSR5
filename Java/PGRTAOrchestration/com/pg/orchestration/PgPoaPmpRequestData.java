package com.pg.orchestration;

public class PgPoaPmpRequestData
	{
	    private String uniqueID;
		
	    private String pmp;
	    
	    private String poaName;
	    
	    private String country;

	    private String language; 
	    
	    private String noOfDosesPerScoops;
	    
	    private String recommendedDosage;
	    
	    private String recommendedDosageuom;
	    
	    private String netWeight;
	    
	    private String netWeightuom;
	    
	    private String iLLeaderId;
	    
	    private String finishedproductcode;
	    
	    private String multiplefpc;
	    
	    private String GCAS;
		
		//Added by RTA Capgemini Offshore 18x.5 CW defect-36820 Starts
    	private String pmpDescription;
		//Added by RTA Capgemini Offshore 18x.5 CW defect-36820 Ends
		
		//Added by RTA Capgemini offshore 2018x.6 req-36492 Starts
	    private String gpsFixedCES;
	    
		private String gpsVariableCES;
	    
	    private String mrkClaimCES;
	    //Added by RTA Capgemini offshore 2018x.6 req-36492 Ends
	    //Added by RTA for 22x May 23 CW ALM-46090 -Starts
	    private String productionPlant;
	    private String productionPlantPrimary;
	    //Added by RTA for 22x May 23 CW ALM-46090 -End
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
		//Added by RTA for 22x.6 AUG_24_CW ALM-Req-49750 & 49843 - Ends
	   //Added by RTA for 22x Dec 23 CW Req-47799 Starts
	    private String source;
	    public String getSource() {
			return source;
		}
		public void setSource(String source) {
			this.source = source;
		}
		//Added by RTA for 22x Dec 23 CW Req-47799 Ends
		//Added by RTA Capgemini Offshore for 22x.5 April_24_CW Req 49132 Starts
	  		private String initiativeID;
	  		public String getInitiativeID() {
				return initiativeID;
			}
			public void setInitiativeID(String initiativeID) {
				this.initiativeID = initiativeID;
			}
			//Added by RTA Capgemini Offshore for 22x.5 April_24_CW Req 49132 Ends
		//Added by RTA Capgemini offshore 2018x.6 req-36133 Starts
	    private String harmonizePOA;
	    //Added by RTA Capgemini offshore 2018x.6 req-36133 Ends
			// Added by RTA Capgemini offshore 2018x.6 req-43229 Starts
			private String regulatoryClassification;
			// Added by RTA Capgemini offshore 2018x.6 req-43229 Ends

			// Added by RTA Capgemini offshore 2018x.6 req-43432 Starts
			private String ECAPS_ClaimReq;
			// Added by RTA Capgemini offshore 2018x.6 req-43432 Ends

			// Added by RTA Capgemini offshore 2018x.6 req-39244 Starts
			private String productForm;
			// Added by RTA Capgemini offshore 2018x.6 req-39244 Ends
		// Added by RTA Capgemini Offshore for 18x.6 May_CW Req 36128, 42564,42669,42819,42563   - Starts
		private String packagingLevel;
	   
		private String piFPCDescription;
		
		//Added by RTA Capgemini Offshore for 22x.5 April_24_CW Req 49282,49283 Starts
		private String awmProjectId;
		private String awmProjectName;
		private String awmSupplier;
		private String awmArtWorkAssignee;
		//Added by RTA Capgemini Offshore for 22x.5 April_24_CW Req 49282,49283 Starts
		
	    private String referencePMP;
		// Added by RTA Capgemini offshore 2018x.6 June 22 CW Defect 48457 Starts
		private String paNumber;
		// Added by RTA Capgemini offshore 2018x.6 June 22 CW Defect 48457 Ends
		public void setPackagingLevel(String packagingLevel){
	        this.packagingLevel = packagingLevel;
	    }
	    public String getPackagingLevel(){
	        return this.packagingLevel;
	    }
		// Added by RTA Capgemini Offshore for 18x.6 MAY_CW Req 36128,42564,42669,42819,42563   - Ends
		// Added by RTA Capgemini Offshore for 18x.6 JUNE_CW Req 42907   - Starts
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
		
	    //Unique ID
	    public void setUniqueID(String uniqueID){
	        this.uniqueID = uniqueID;
	    }
	    public String getUniqueID(){
	        return this.uniqueID;
	    }
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
		// Added by RTA Capgemini offshore 2018x.6 req-43229 Starts
	public String getRegulatoryClassification() {
		return regulatoryClassification;
	}
		// Added by RTA Capgemini offshore 2018x.6 req-43229 Ends

		// Added by RTA Capgemini offshore 2018x.6 req-43432 Starts
		public String getECAPS_ClaimReq() {
			return ECAPS_ClaimReq;
		}
		// Added by RTA Capgemini offshore 2018x.6 req-43432 Ends

		// Added by RTA Capgemini offshore 2018x.6 req-39244 Starts
		public String getProductForm() {
			return productForm;
		}
		// Added by RTA Capgemini offshore 2018x.6 req-39244 Ends
	    //GCAS
	    public void setGCAS(String GCAS){
	        this.GCAS = GCAS;
	    }
	    public String getGCAS(){
	        return this.GCAS;
	    }
	    // multiple fpc
	    public void setmultiplefpc(String multiplefpc){
	        this.multiplefpc = multiplefpc;
	    }
	    public String getmultiplefpc(){
	        return this.multiplefpc;
	    }
	    // finishedproductcode
	    public void setfinishedproductcode(String finishedproductcode){
	        this.finishedproductcode = finishedproductcode;
	    }
	    public String getfinishedproductcode(){
	        return this.finishedproductcode;
	    }
	    // iLLeaderId
	    public void setiLLeaderId(String iLLeaderId){
	        this.iLLeaderId = iLLeaderId;
	    }
	    public String getiLLeaderId(){
	        return this.iLLeaderId;
	    }
	    // netWeight
	    public void setnetWeight(String netWeight){
	        this.netWeight = netWeight;
	    }
	    public String getnetWeight(){
	        return this.netWeight;
	    }
	    
	    // recommendedDosage
	    public void setrecommendedDosage(String recommendedDosage){
	        this.recommendedDosage = recommendedDosage;
	    }
	    public String getrecommendedDosage(){
	        return this.recommendedDosage;
	    }
	   
	    // noOfDosesPerScoops
	    public void setnoOfDosesPerScoops(String noOfDosesPerScoops){
	        this.noOfDosesPerScoops = noOfDosesPerScoops;
	    }
	    public String getnoOfDosesPerScoops(){
	        return this.noOfDosesPerScoops;
	    }
	    //pmp
	    public void setPmp(String pmp){
	        this.pmp = pmp;
	    }
	    public String getPmp(){
	        return this.pmp;
	    }
	    
	    public void setPoaName(String poaName){
	        this.poaName = poaName;
	    }
	    public String getPoaName(){
	        return this.poaName;
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
	  //Added by RTA Capgemini Offshore for 18x.6 May_CW Requirement 42564,42669,42819,42563 -- START
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
		
		public String getReferencePMP() {
			return referencePMP;
		}
		public void setReferencePMP(String referencePMP) {
			this.referencePMP = referencePMP;
		}
		private String phaseOutPOA;
		public void setPhaseOutPOA(String phaseOutPOA){
	        this.phaseOutPOA = phaseOutPOA;
	    }
	    public String getPhaseOutPOA(){
	        return this.phaseOutPOA;
	    }
		//Added by RTA Capgemini Offshore for 18x.6 May_CW Requirement 42564,42669,42819,42563 -- Ends
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
	
	// Added by RTA Capgemini offshore 2018x.6 req-39244 Starts
		public void setProductForm(String productForm) {
			this.productForm = productForm;
		}	
		// Added by RTA Capgemini offshore 2018x.6 req-39244 Ends
		// Added by RTA Capgemini offshore 2018x.6 June 22 CW Defect 48457 Starts
		public void setPaNumber(String paNumber) {
		    this.paNumber = paNumber;
		  }
		public String getPaNumber() {
		    return this.paNumber;
		  }
		// Added by RTA Capgemini offshore 2018x.6 June 22 CW Defect 48457 Ends
		//Added by RTA for 22x May 23 CW ALM-46090 -Starts
		public void setProductionPlant(String productionPlant) {
			this.productionPlant = productionPlant;
		}
		//Added by RTA for 22x May 23 CW Defect-52516 -Starts
		public String getProductionPlant() {
			return productionPlant;
		}
		 public String getProductionPlantPrimary() {
				return productionPlantPrimary;
		}
		//Added by RTA for 22x May 23 CW Defect-52516 -End
			public void setProductionPlantPrimary(String productionPlantPrimary) {
				this.productionPlantPrimary = productionPlantPrimary;
			}
		//Added by RTA for 22x May 23 CW ALM-46090 -End
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
		//Added by RTA for 22x.5 APR_24_CW ALM-Req-48395 & 48396 & 48814 & 49052 - Ends		
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
		}
	
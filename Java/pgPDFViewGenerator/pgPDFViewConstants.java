

import com.matrixone.apps.domain.util.PropertyUtil;
import com.pg.v3.custom.pgV3Constants;
import com.png.apollo.pgApolloConstants;
import com.matrixone.apps.domain.DomainConstants;

public class pgPDFViewConstants {
	public static final String strCSSTraderDistributor = (String) PropertyUtil.getSchemaProperty("attribute_pgCSSTraderDistributor");
	public static final String strCSSManufacturer = (String)PropertyUtil.getSchemaProperty("attribute_pgCSSManufacturer");
	public static final String strPlantLineSpecific =(String)PropertyUtil.getSchemaProperty("attribute_pgPlantLineSpecific");
	//public static final String strQualStatus = (String)PropertyUtil.getSchemaProperty("attribute_pgQualificationStatus");
	public static final String strSupplierTradeName = (String)PropertyUtil.getSchemaProperty("attribute_pgSupplierTradeName");
	public static final String strQualificationStatus =  (String)PropertyUtil.getSchemaProperty("attribute_pgQualificationStatus");
	public static final String strClassification =  (String)PropertyUtil.getSchemaProperty("attribute_pgClassification");
	public static final String strProductProperty =  (String)PropertyUtil.getSchemaProperty("attribute_pgProduct");
	//public static final String strGBU = (String)PropertyUtil.getSchemaProperty("attribute_pgGBU");
	public static final String strPSRAInformation =  (String)PropertyUtil.getSchemaProperty("attribute_pgPSRAInformation");
	public static final String strSequence1 =  (String)PropertyUtil.getSchemaProperty("attribute_pgSequence");
	public static final String strCountry = (String)PropertyUtil.getSchemaProperty("attribute_Country");
	public static final String strCategory = (String)PropertyUtil.getSchemaProperty("attribute_pgCategory");
	public static final String ATTRIBUTE_PGPLMPARAMDISPLAYUNIT = PropertyUtil.getSchemaProperty("attribute_PlmParamDisplayUnit");
	public static final String SELECT_ATTRIBUTE_PGPLMPARAMDISPLAYUNIT = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PGPLMPARAMDISPLAYUNIT + "]";
	public static final String ATTRIBUTE_PGMEASUREMENTPRECISION = PropertyUtil.getSchemaProperty("attribute_MeasurementPrecision");
	public static final String SELECT_ATTRIBUTE_PGMEASUREMENTPRECISION = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PGMEASUREMENTPRECISION + "]";
	public static final String ATTRIBUTE_PGTESTMETHODREFDOCGCAS = PropertyUtil.getSchemaProperty("attribute_pgTestMethodRefDocGCAS");
	public static final String SELECT_ATTRIBUTE_PGTESTMETHODREFDOCGCAS = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PGTESTMETHODREFDOCGCAS + "]";
	public static final String ATTRIBUTE_PGPLBATTERYWEIGHTLIUOM = PropertyUtil.getSchemaProperty("attribute_pgPLBatteryWeightLiUOM");
	public static final String ATTRIBUTE_PGPLBATTERYENRUOM = PropertyUtil.getSchemaProperty("attribute_pgPLBatteryEnRUOM");
	public static final String ATTRIBUTE_PGPLBATTERYVOLUOM = PropertyUtil.getSchemaProperty("attribute_pgPLBatteryVolUOM");
	public static final String ATTRIBUTE_PGPLBATTERYTCUOM = PropertyUtil.getSchemaProperty("attribute_pgPLBatteryTCUOM");
	public static final String CONST_TYPE_INCLUSION_LIST = "emxCPN.ProductDataTemplate.type_ProductDataIPMPDF.TypeInclusionList";
	public static final String CONST_NON_STRUCT_TYPE_INCLUSION_LIST = "emxCPN.ProductDataTemplate.type_ProductDataIPMPDF.NonStructuredTypeInclusionList";
//	public static final String CONST_GENDOC = "GenDoc";
//	public static final String CONST_USER_AGENT = "User Agent";
//	public static final String CONST_FORMAT_FILE_NAME = "format.file.name";
	public static final String CONST_ALLINFO = "allinfo";
	public static final String CONST_HEADER1 = "header1";
	public static final String CONST_HEADER2 = "header2";
	public static final String CONST_HEADER3 = "header3";
	public static final String CONST_STRHTMLWITHDATA = "strHTMLWithData";
	public static final String CONST_STRXMLTICKETCONTENT = "strXMLTicketContent";
	public static final String CONST_SUPPLIER = "supplier";
	public static final String CONST_CONSOLIDATEDPACKAGING = "consolidatedpackaging";
	public static final String CONST_WAREHOUSE = "warehouse";
	public static final String CONST_CONTRACTPACKAGING = "contractpackaging";
	public static final String CONST_STRING = "String";
	public static final String CONST_ASCENDING = "ascending";
	public static final String CONST_NO_ACCESS = "No Access";
	public static final String CONST_FALSE = "FALSE";
	public static final String CONST_TEXTCENTER = "TEXTCENTER";
	public static final String CONST_COMBINEDWITHMASTER = "combinedwithmaster";
	public static final int DEFAULT_PAGE_SIZE = 35;
	public static final int TABLE_BUFFER_SIZE = 3;
	public static final String DYNAMIC_ROW_SEPERATOR = "~#~";	
	public static final String PATTERN_DECIMALFORMAT = "0.000000";
	//Added by DSM(Sogeti)-2018x.5 for PDF Views Requirements 35306, 32960, 32964, 32965, 32966, 35338, 34926, 32866 and 32869 POA ATS Gendoc- Starts
	public static final String CONST_STRADLIB = "ADLIBNTS";
	public static final String CONST_INPUT_TAG_START = "<JOB:DOCINPUT FILENAME=\"";
	public static final String CONST_FOLDER ="\" FOLDER=\"";
	public static final String CONST_INPUT_TAG_END = "\" />\n";
	//Added by DSM(Sogeti)-2018x.5 for PDF Views Requirements 35306, 32960, 32964, 32965, 32966, 35338, 34926, 32866 and 32869 POA ATS Gendoc- Ends
	public static final int FIRST_LEVEL = 1;
	//Added by DSM ( Sogeti) for 2018x.5 PDF Defect - 33102 : Starts
	public static final String PHYSICAL_ID = "physicalid";
	//Added by DSM ( Sogeti) for 2018x.5 PDF Defect - 33102 : Ends
	//Modified DSM-2018x.5 for PDF Views (Req Id #32405) : Starts
	public static final String CONST_CURRENTACCESS = "current.access[fromdisconnect]";
	//Modified DSM-2018x.5 for PDF Views (Req Id #32405) : Starts
	//Added by DSM (Sogeti) for 2018x.5 PDF Defect - 33190 : Starts
	public static final String CATIA_HEADER = ".CATIA.header";
	//Added by DSM (Sogeti) for 2018x.5 PDF Defect - 33190 : Ends
	public static final String CATIA_LPD = "LPD";
	
	//Added by DSM(Sogeti) - 2018x.2 - Starts
	
	 public static final String ATTRIBUTE_PGPACKINGSITE = PropertyUtil.getSchemaProperty("attribute_pgPackingSite");
	 
	 public static final String ATTRIBUTE_PGMANUFACTURINGSITE = PropertyUtil.getSchemaProperty("attribute_pgManufacturingSite");
	 public static final String SELECT_ATTRIBUTE_PQDOESDEVICECONTAINFLAMMABLELIQUID = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PQDOESDEVICECONTAINFLAMMABLELIQUID + "]";
	 
	 public static final String ATTRIBUTE_PQDOESDEVICECONTAINFLAMMABLELIQUID = PropertyUtil.getSchemaProperty("attribute_pgDoesDeviceContainFlammableLiquid");
	
	 //2018x.2 - Ends
	 //Dangerous goods classification - 2018x.2 attributes and relationship - 2018x.2-starts
	 // public static final String ATTRIBUTE_DESCRIPTION = PropertyUtil.getSchemaProperty("attribute_Description");
	 // public static final String SELECT_ATTRIBUTE_DESCRIPTION = "attribute[" + pgPDFViewConstants.ATTRIBUTE_DESCRIPTION + "]";
	 
	 public static final String ATTRIBUTE_PQUNNUMBER = PropertyUtil.getSchemaProperty("attribute_ pgUNNumber");
	 public static final String SELECT_ATTRIBUTE_PQUNNUMBER = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PQUNNUMBER + "]";
	 
	 public static final String ATTRIBUTE_PGPROPERSHIPPINGNAME = PropertyUtil.getSchemaProperty("attribute_pgProperShippingName");
	 public static final String SELECT_ATTRIBUTE_PGPROPERSHIPPINGNAME = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PGPROPERSHIPPINGNAME + "]";
	 
	 public static final String RELATIONSHIP_PQHAZARDCLASS = PropertyUtil.getSchemaProperty("relationship_pgHazardClass");
	 public static final String RELATIONSHIP_PQDANGEROUSGOODS = PropertyUtil.getSchemaProperty("relationship_pgDangerousgoods");// need to check this
 	 //public static final String RELATIONSHIP_PQPLIPACKAGINGGROUP = PropertyUtil.getSchemaProperty("relationship_pgPLIPackingGroup");
	
	 //public static final String RELATIONSHIP_PQPLIHAZARDCLASS= PropertyUtil.getSchemaProperty("relationship_pgPLIHazardClass");
	 public static final String RELATIONSHIP_PQPACKINGGROUP = PropertyUtil.getSchemaProperty("relationship_pgPackingGroup"); // need to check this
	 
	 public static final String ATTRIBUTE_PQSHIPMENTLIMITEDQUANTITY= PropertyUtil.getSchemaProperty("attribute_pgShipmentLimitedQuantity");
	 public static final String SELECT_ATTRIBUTE_PQSHIPMENTLIMITEDQUANTITY = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PQSHIPMENTLIMITEDQUANTITY+ "]";
	 //UN Specification Packaging -- already used in storage and transportation - check
	 public static final String ATTRIBUTE_PQCONSUMERWEIGHTVOLUME= PropertyUtil.getSchemaProperty("attribute_pgConsumerWeightVolume");
	 public static final String SELECT_ATTRIBUTE_PQCONSUMERWEIGHTVOLUME = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PQCONSUMERWEIGHTVOLUME+ "]";
	 
	 public static final String ATTRIBUTE_PQCUSTOMERWEIGHTVOLUME= PropertyUtil.getSchemaProperty("attribute_pgCustomerWeightVolume");
	 public static final String SELECT_ATTRIBUTE_PQCUSTOMERWEIGHTVOLUME = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PQCONSUMERWEIGHTVOLUME+ "]";

	 public static final String ATTRIBUTE_PQDGLABELREQUIREMENTS= PropertyUtil.getSchemaProperty("attribute_pgDGLabelRequirements");
	 public static final String SELECT_ATTRIBUTE_PQDGLABELREQUIREMENTS = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PQDGLABELREQUIREMENTS+ "]";
	 
	 public static final String ATTRIBUTE_PQDGCOPLABEL_REQUIRED= PropertyUtil.getSchemaProperty("attribute_ pgDGCOPLabelRequired");
	 public static final String SELECT_ATTRIBUTE_PQDGCOPLABEL_REQUIRED = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PQDGCOPLABEL_REQUIRED+ "]";
	 
	 public static final String ATTRIBUTE_PQDGCUPLABEL_REQUIRED= PropertyUtil.getSchemaProperty("attribute_ pgDGCUPLabelRequired");
	 public static final String SELECT_ATTRIBUTE_PQDGCUPLABEL_REQUIRED = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PQDGCUPLABEL_REQUIRED+ "]";
	 
	 // for certification claim data
	 public static final String RELATIONSHIP_PQCOUNTRIESCERTIFICATIONCLAIMED = PropertyUtil.getSchemaProperty("relationship_pgCountriesCertificationClaimed");
	 
	 
	//Added by DSM(Sogeti)-2018x.2 for PDF Views Defect #20091 - Starts
	public static final String ATTRIBUTE_POSTALCODE = (String)PropertyUtil.getSchemaProperty("attribute_PostalCode");
	//Added by DSM(Sogeti)-2018x.2 for PDF Views Defect #20091 - Ends
	
	//Added by DSM(Sogeti)-2018x.2 for PDF Views Requirements #12002 & #11478 - Starts
	public static final String ATTRIBUTE_pgREACHExempt = PropertyUtil.getSchemaProperty("attribute_pgREACHExempt");
	public static final String SELECT_ATTRIBUTE_pgREACHExempt = "attribute[" + ATTRIBUTE_pgREACHExempt + "]";
	//Added by DSM(Sogeti)-2018x.2 for PDF Views Requirements #12002 & #11478 - Ends
		
	public static final String ATTRIBUTE_pgExpirationDate = PropertyUtil.getSchemaProperty("attribute_pgExpirationDate");
	public static final String SELECT_ATTRIBUTE_pgExpirationDate = "attribute[" + ATTRIBUTE_pgExpirationDate + "]";
	public static final String ATTRIBUTE_pgREACHStatus = PropertyUtil.getSchemaProperty("attribute_pgREACHStatus");
	public static final String SELECT_ATTRIBUTE_pgREACHStatus = "attribute[" + ATTRIBUTE_pgREACHStatus + "]";
		
	public static final String RELATIONSHIP_pgMEPSEPCertification = PropertyUtil.getSchemaProperty("relationship_pgMEPSEPCertification");
	//Added by DSM-2018x.2.1 for PDF Views Requirement : Starts
	public static final String ATTRIBUTE_PGCONDUCTIVITYOFTHELIQUID = PropertyUtil.getSchemaProperty("attribute_pgConductivityoftheLiquid");
	public static final String SELECT_ATTRIBUTE_PGCONDUCTIVITYOFTHELIQUID = "attribute[" + ATTRIBUTE_PGCONDUCTIVITYOFTHELIQUID + "]";
	public static final String ATTRIBUTE_PGPRODUCTTOINCREASEBURNRATE = PropertyUtil.getSchemaProperty("attribute_pgProductPotentialToIncreaseBurningRate");
	public static final String SELECT_ATTRIBUTE_PGPRODUCTTOINCREASEBURNRATE = "attribute[" + ATTRIBUTE_PGPRODUCTTOINCREASEBURNRATE + "]";
	//Added by DSM-2018x.2.1 for PDF Views Requirement : Ends
	
	//Added by DSM-2018x.3 for PDF Views Requirement : Starts
	public static final String CONS_HALB = "HALB";
	//Added by DSM 2018x.5 Defect req : 33190 Start
	public static final String CONS_CATIA = "CATIA";
	//Added by DSM 2018x.5 Defect req : 33190 End
	public static final String CONT_UNSTRUCTUREDTYPES= "UnStructuredTypeProperty";
	public static final String CONT_TUP_NAME= "TUP_NAME";
	public static final String ATTRIBUTE_PGREGISTRATIONRENEWALLEADTIME = PropertyUtil.getSchemaProperty("attribute_pgRegistrationRenewalLeadTime");
	public static final String ATTRIBUTE_PGREGISTRATIONRENEWALSTATUS = PropertyUtil.getSchemaProperty("attribute_pgRegistrationRenewalStatus");
	public static final String ATTRIBUTE_PGREPLACEDPRODUCTNAME = PropertyUtil.getSchemaProperty("attribute_pgReplacedProductName");
	public static final String SELECT_ATTRIBUTE_PGREPLACEDPRODUCTNAME = "attribute[" + ATTRIBUTE_PGREPLACEDPRODUCTNAME + "]";
	public static final String ATTRIBUTE_PERCENTPOSTINDUSTRIALRECYCLATE = PropertyUtil.getSchemaProperty("attribute_PercentPostIndustrialRecyclate");
	public static final String SELECT_ATTRIBUTE_PERCENTPOSTINDUSTRIALRECYCLATE = "attribute[" + ATTRIBUTE_PERCENTPOSTINDUSTRIALRECYCLATE + "]";
	//Added by DSM-2018x.3 for PDF Views Requirement : Ends
	//Modified by DSM(Sogeti)-2018x.3 for PDF Views Req #32861  - Starts
	public static final String ATTRIBUTE_CHAR_NOTES = PropertyUtil.getSchemaProperty("attribute_CharacteristicNotes");
	public static final String SELECT_ATTRIBUTE_CHAR_NOTES = "attribute[" + ATTRIBUTE_CHAR_NOTES + "]";
	//Modified by DSM(Sogeti)-2018x.3 for PDF Views Req #32861  - Ends
	// Modified by DSM-2018x.3 for PDF Views (Defect Id #32058 ) : Starts
	public static final String ATTRIBUTE_PGLEGACYPRODUCTWEIGHT = PropertyUtil.getSchemaProperty("attribute_pgLegacyProductWeight");
	public static final String SELECT_ATTRIBUTE_PGLEGACYPRODUCTWEIGHT = "attribute[" + ATTRIBUTE_PGLEGACYPRODUCTWEIGHT + "]";
	public static final String ATTRIBUTE_PGLEGACYWEIGHTFACTOR = PropertyUtil.getSchemaProperty("attribute_pgLegacyWeightFactor");
	public static final String SELECT_ATTRIBUTE_PGLEGACYWEIGHTFACTOR = "attribute[" + ATTRIBUTE_PGLEGACYWEIGHTFACTOR + "]";
	public static final String ATTRIBUTE_PGLEGACYWEIGHTFACTORUOM = PropertyUtil.getSchemaProperty("attribute_pgLegacyWeightFactorUoM");
	public static final String SELECT_ATTRIBUTE_PGLEGACYWEIGHTFACTORUOM = "attribute[" + ATTRIBUTE_PGLEGACYWEIGHTFACTORUOM + "]";
	// Modified by DSM-2018x.3 for PDF Views (Defect Id #32058 ) : Ends
	//Added by IRM-2018x.3 for PDF Views Requirement - 32753: Starts
			public static final String ATTRIBUTE_PGIRMPURPOSE= PropertyUtil.getSchemaProperty("attribute_pgIRMPurpose");
			public static final String SELECT_ATTRIBUTE_PGIRMPURPOSE = "attribute[" + ATTRIBUTE_PGIRMPURPOSE + "]";
			public static final String ATTRIBUTE_PGIRMSUCCESSCRITERIA= PropertyUtil.getSchemaProperty("attribute_pgIRMSuccessCriteria");
			public static final String SELECT_ATTRIBUTE_PGIRMSUCCESSCRITERIA = "attribute[" + ATTRIBUTE_PGIRMSUCCESSCRITERIA + "]";
			public static final String ATTRIBUTE_PGIRMACTION= PropertyUtil.getSchemaProperty("attribute_pgIRMAction");
			public static final String SELECT_ATTRIBUTE_PGIRMACTION = "attribute[" + ATTRIBUTE_PGIRMACTION + "]";
			public static final String ATTRIBUTE_PGIRMBACKGROUND= PropertyUtil.getSchemaProperty("attribute_pgIRMBackground");
			public static final String SELECT_ATTRIBUTE_PGIRMBACKGROUND = "attribute[" + ATTRIBUTE_PGIRMBACKGROUND + "]";
			public static final String ATTRIBUTE_PGIPPROJECTSECURITY= PropertyUtil.getSchemaProperty("attribute_pgIPProjectSecurity");
			public static final String SELECT_ATTRIBUTE_PGIPPROJECTSECURITY = "attribute[" + ATTRIBUTE_PGIPPROJECTSECURITY + "]";
			public static final String ATTRIBUTE_PGBUSINESSAREA= PropertyUtil.getSchemaProperty("attribute_pgBusinessArea");
			public static final String SELECT_ATTRIBUTE_PGBUSINESSAREA = "attribute[" + ATTRIBUTE_PGBUSINESSAREA + "]";
			public static final String RELATIONSHIP_pgProjectDocument = PropertyUtil.getSchemaProperty("relationship_pgProjectDocument");
			public static final String ATTRIBUTE_CHECK_IN_REASON = PropertyUtil.getSchemaProperty("attribute_CheckinReason");
			public static final String SELECT_ATTRIBUTE_CHECK_IN_REASON = "attribute[" + ATTRIBUTE_CHECK_IN_REASON + "]";
			public static final String CONST_FORMAT_FILE_NAME = "format.file.name";
			public static final String ATTRIBUTE_GPSSTATUS = PropertyUtil.getSchemaProperty("attribute_pgGPSStatus");
			public static final String SELECT_ATTRIBUTE_GPSSTATUS = "attribute[" + ATTRIBUTE_GPSSTATUS + "]";  
			public static final String ATTRIBUTE_NRQID = PropertyUtil.getSchemaProperty("attribute_pgNRQID");
			public static final String SELECT_ATTRIBUTE_NRQID = "attribute[" + ATTRIBUTE_NRQID + "]";  
			public static final String ATTRIBUTE_ORIGINALTASKNAME = PropertyUtil.getSchemaProperty("attribute_pgOriginalTaskName");
			public static final String SELECT_ATTRIBUTE_ORIGINALTASKNAME = "attribute[" + ATTRIBUTE_ORIGINALTASKNAME + "]";
		
	public static final String Type_Group = "Group";
	public static final String ATTRIBUTE_PGTYPEOFRESEARCH = PropertyUtil.getSchemaProperty("attribute_pgTypeOfResearch");
	public static final String SELECT_ATTRIBUTE_PGTYPEOFRESEARCH = "attribute[" + ATTRIBUTE_PGTYPEOFRESEARCH + "]";
	public static final String ATTRIBUTE_PGIRMSTUDYTYPEFORMAT = PropertyUtil.getSchemaProperty("attribute_pgIRMStudyTypeFormat");
	public static final String SELECT_ATTRIBUTE_PGIRMSTUDYTYPEFORMAT = "attribute[" + ATTRIBUTE_PGIRMSTUDYTYPEFORMAT + "]";
	public static final String ATTRIBUTE_PGIRMCONFIDENCELEVELSIGNIFICANCETESTING = PropertyUtil.getSchemaProperty("attribute_pgIRMConfidenceLevelSignificanceTesting");
	public static final String SELECT_ATTRIBUTE_PGIRMCONFIDENCELEVELSIGNIFICANCETESTING = "attribute[" + ATTRIBUTE_PGIRMCONFIDENCELEVELSIGNIFICANCETESTING + "]";
	public static final String ATTRIBUTE_PGIRMTOTALNOOFPANELISTS = PropertyUtil.getSchemaProperty("attribute_pgIRMTotalNoOfPanelists");
	public static final String SELECT_ATTRIBUTE_PGIRMTOTALNOOFPANELISTS = "attribute[" + ATTRIBUTE_PGIRMTOTALNOOFPANELISTS + "]";
	public static final String ATTRIBUTE_PGSTUDYPANELISTPURCHASEINTENT = PropertyUtil.getSchemaProperty("attribute_pgStudyPanelistPurchaseIntent");
	public static final String SELECT_ATTRIBUTE_PGSTUDYPANELISTPURCHASEINTENT = "attribute[" + ATTRIBUTE_PGSTUDYPANELISTPURCHASEINTENT + "]";
	public static final String ATTRIBUTE_PGNOTIFYPATENTOFFICE = PropertyUtil.getSchemaProperty("attribute_pgNotifyPatentOffice");
	public static final String SELECT_ATTRIBUTE_PGNOTIFYPATENTOFFICE = "attribute[" + ATTRIBUTE_PGNOTIFYPATENTOFFICE + "]";
	public static final String RELATIONSHIP_PGSTUDYPROTOCOLTOSTUDYTYPE = PropertyUtil.getSchemaProperty("relationship_pgStudyProtocolToStudyType");
	public static final String RELATIONSHIP_PGSTUDYPROTOCOLTOADDITIONALSERVICES = PropertyUtil.getSchemaProperty("relationship_pgStudyProtocolToAdditionalServices");
	public static final String RELATIONSHIP_PGSTUDYPROTOCOLTOSTUDYCLASS = PropertyUtil.getSchemaProperty("relationship_pgStudyProtocolToStudyClass");
	public static final String RELATIONSHIP_PGSTUDYPROTOCOLTOREGULATORYCLASSIFICATION = PropertyUtil.getSchemaProperty("relationship_pgStudyProtocolToRegulatoryClassification");
	public static final String RELATIONSHIP_PGSTUDYPROTOCOLTOPANELISTTYPE = PropertyUtil.getSchemaProperty("relationship_pgStudyProtocolToPanelistType");
	 public static final String RELATIONSHIP_PGSTUDYPROTOCOLTORACE = PropertyUtil.getSchemaProperty("relationship_pgStudyProtocolToRace");
		public static final String ATTRIBUTE_PGIRMDATAUSE = PropertyUtil.getSchemaProperty("attribute_pgIRMDataUse");
		public static final String SELECT_ATTRIBUTE_PGIRMDATAUSE = "attribute[" + ATTRIBUTE_PGIRMDATAUSE + "]";
		
		public static final String ATTRIBUTE_PGLOWERAGELIMIT = PropertyUtil.getSchemaProperty("attribute_pgLowerAgeLimit");
		public static final String SELECT_ATTRIBUTE_PGLOWERAGELIMIT = "attribute[" + ATTRIBUTE_PGLOWERAGELIMIT + "]";
		
		public static final String ATTRIBUTE_PGBALANCINGCRITERIA = PropertyUtil.getSchemaProperty("attribute_pgBalancingCriteria");
		public static final String SELECT_ATTRIBUTE_PGBALANCINGCRITERIA= "attribute[" + ATTRIBUTE_PGBALANCINGCRITERIA + "]";
		
		public static final String ATTRIBUTE_PGUPPERAGELIMIT = PropertyUtil.getSchemaProperty("attribute_pgUpperAgeLimit");
		public static final String SELECT_ATTRIBUTE_PGUPPERAGELIMIT = "attribute[" + ATTRIBUTE_PGUPPERAGELIMIT + "]";
		
		public static final String ATTRIBUTE_PGMANAGEMENTSAMPLES = PropertyUtil.getSchemaProperty("attribute_pgManagementSamples");
		public static final String SELECT_ATTRIBUTE_PGMANAGEMENTSAMPLES = "attribute[" + ATTRIBUTE_PGMANAGEMENTSAMPLES + "]";
		
		public static final String ATTRIBUTE_PGPANELISTRANGENOTPROVIDED = PropertyUtil.getSchemaProperty("attribute_pgPanelistRangeNotProvided");
		public static final String SELECT_ATTRIBUTE_PGPANELISTRANGENOTPROVIDED = "attribute[" + ATTRIBUTE_PGPANELISTRANGENOTPROVIDED + "]";
		
		public static final String ATTRIBUTE_PGPRODUCTUSEDFORRND = PropertyUtil.getSchemaProperty("attribute_pgProductUsedForRnD");
		public static final String SELECT_ATTRIBUTE_PGPRODUCTUSEDFORRND = "attribute[" + ATTRIBUTE_PGPRODUCTUSEDFORRND + "]";
		
		public static final String ATTRIBUTE_PGPANELISTINCLUSIONCRITERIA = PropertyUtil.getSchemaProperty("attribute_pgPanelistInclusionCriteria");
		public static final String SELECT_ATTRIBUTE_PGPANELISTINCLUSIONCRITERIA = "attribute[" + ATTRIBUTE_PGPANELISTINCLUSIONCRITERIA + "]";
		public static final String ATTRIBUTE_PGPANELISTEXCLUSIONCRITERIA = PropertyUtil.getSchemaProperty("attribute_pgPanelistExclusionCriteria");
		public static final String SELECT_ATTRIBUTE_PGPANELISTEXCLUSIONCRITERIA = "attribute[" + ATTRIBUTE_PGPANELISTEXCLUSIONCRITERIA + "]";
	
	public static final String ATTRIBUTE_PANELIST_UNIQUECONSUMER= PropertyUtil.getSchemaProperty("attribute_pgPanelistUniqueConsumerPackage");
	public static final String SELECT_ATTRIBUTE_PANELIST_UNIQUECONSUMER = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PANELIST_UNIQUECONSUMER+ "]";	
	public static final String ATTRIBUTE_PANELIST_STABILITYDATAREQ= PropertyUtil.getSchemaProperty("attribute_pgPanelistStabilityDataRequired");
	public static final String SELECT_ATTRIBUTE_PANELIST_STABILITYDATAREQ = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PANELIST_STABILITYDATAREQ+ "]";
	public static final String ATTRIBUTE_PANELIST_ANCILLARYSUPPORTING= PropertyUtil.getSchemaProperty("attribute_pgPanelistAncillarySupportingItems");
	public static final String SELECT_ATTRIBUTE_PANELIST_ANCILLARYSUPPORTING = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PANELIST_ANCILLARYSUPPORTING+ "]";
	public static final String ATTRIBUTE_PG_LOCATIONSOFSTABILITY= PropertyUtil.getSchemaProperty("attribute_pgLocationOfStabilityData");
	public static final String SELECT_ATTRIBUTE_PG_LOCATIONSOFSTABILITY = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PG_LOCATIONSOFSTABILITY+ "]";
	//for budget owner and approval 
	public static final String ATTRIBUTE_PG_BUDGETAPPROVER= PropertyUtil.getSchemaProperty("attribute_pgBudgetApprover");
	public static final String SELECT_ATTRIBUTE_PG_BUDGETAPPROVER = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PG_BUDGETAPPROVER+ "]";
	public static final String ATTRIBUTE_PG_ESTIMATIONCOMPLETIONDOS= PropertyUtil.getSchemaProperty("attribute_pgEstimatedCompletionDateOfStudy");
	public static final String SELECT_ATTRIBUTE_PG_ESTIMATIONCOMPLETIONDOS = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PG_ESTIMATIONCOMPLETIONDOS+ "]";
	public static final String ATTRIBUTE_PG_COSTOFPLACINGEXEC= PropertyUtil.getSchemaProperty("attribute_pgCostOfPlacingExecution");
	public static final String SELECT_ATTRIBUTE_PG_COSTOFPLACINGEXEC = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PG_COSTOFPLACINGEXEC+ "]";
	public static final String ATTRIBUTE_PG_PO= PropertyUtil.getSchemaProperty("attribute_pgPO");
	public static final String SELECT_ATTRIBUTE_PG_PO = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PG_PO+ "]";
	public static final String ATTRIBUTE_PG_BUDGETCCIO= PropertyUtil.getSchemaProperty("attribute_pgBudgetCCIO");
	public static final String SELECT_ATTRIBUTE_PG_BUDGETCCIO = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PG_BUDGETCCIO+ "]";
	public static final String ATTRIBUTE_PG_CURRENCY= PropertyUtil.getSchemaProperty("attribute_pgCurrency");
	public static final String SELECT_ATTRIBUTE_PG_CURRENCY = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PG_CURRENCY+ "]";
	//for Panelist tasks
	public static final String ATTRIBUTE_PG_PANELISTCONSENT= PropertyUtil.getSchemaProperty("attribute_pgPanelistsignInformedConsent");
	public static final String SELECT_ATTRIBUTE_PG_PANELISTCONSENT = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PG_PANELISTCONSENT+ "]";
	public static final String ATTRIBUTE_PG_PANELISTCOMPENSATION= PropertyUtil.getSchemaProperty("attribute_pgExpectedPanelistCompensation");
	public static final String SELECT_ATTRIBUTE_PG_PANELISTCOMPENSATION = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PG_PANELISTCOMPENSATION+ "]";
	public static final String ATTRIBUTE_PG_STUDYCDASIGNEDFORPANELIST= PropertyUtil.getSchemaProperty("attribute_pgStudyCDASignedForPanelist");
	public static final String SELECT_ATTRIBUTE_PG_STUDYCDASIGNEDFORPANELIST = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PG_STUDYCDASIGNEDFORPANELIST+ "]";
	public static final String ATTRIBUTE_PG_STUDYPANELISTEMAIL= PropertyUtil.getSchemaProperty("attribute_pgStudyPanelistEmailAddress");
	public static final String SELECT_ATTRIBUTE_PG_STUDYPANELISTEMAIL = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PG_STUDYPANELISTEMAIL+ "]";
	public static final String ATTRIBUTE_PG_PANELISTUSEOFPRODUCT= PropertyUtil.getSchemaProperty("attribute_pgPanelistUseOfProduct");
	public static final String SELECT_ATTRIBUTE_PG_PANELISTUSEOFPRODUCT = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PG_PANELISTUSEOFPRODUCT+ "]";
	public static final String ATTRIBUTE_PG_PANELISTINTERACTION= PropertyUtil.getSchemaProperty("attribute_pgDescribePanelistInteraction");
	public static final String SELECT_ATTRIBUTE_PG_PANELISTINTERACTION = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PG_PANELISTINTERACTION+ "]";
	public static final String ATTRIBUTE_PG_STUDYPANELISTMATERIALS= PropertyUtil.getSchemaProperty("attribute_pgStudyPanelistMaterials");
	public static final String SELECT_ATTRIBUTE_PG_STUDYPANELISTMATERIALS = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PG_STUDYPANELISTMATERIALS+ "]";
	public static final String ATTRIBUTE_PG_STUDYPANELISTHOMEWORK= PropertyUtil.getSchemaProperty("attribute_pgStudyPanelistsHomework");
	public static final String SELECT_ATTRIBUTE_PG_STUDYPANELISTHOMEWORK = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PG_STUDYPANELISTHOMEWORK+ "]";
	public static final String ATTRIBUTE_PG_PANELISTINSTRUCTIONSATTACHED= PropertyUtil.getSchemaProperty("attribute_pgPanelistInstructionsAttached");
	public static final String SELECT_ATTRIBUTE_PG_PANELISTINSTRUCTIONSATTACHED = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PG_PANELISTINSTRUCTIONSATTACHED+ "]";
	public static final String ATTRIBUTE_PG_STUDYPANELISTQUESTIONAIRE= PropertyUtil.getSchemaProperty("attribute_pgStudyPanelistQuestionaire");
	public static final String SELECT_ATTRIBUTE_PG_STUDYPANELISTQUESTIONAIRE = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PG_STUDYPANELISTQUESTIONAIRE+ "]";
	public static final String ATTRIBUTE_PG_PANELISTFOCUSGROUPINTERVIEW= PropertyUtil.getSchemaProperty("attribute_pgPanelistFocusGroupInterview");
	public static final String SELECT_ATTRIBUTE_PG_PANELISTFOCUSGROUPINTERVIEW = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PG_PANELISTFOCUSGROUPINTERVIEW+ "]";
	//for GPS Assessment Type
	public static final String ATTRIBUTE_PG_GPSAPPROVALREQTORELSTUDY= PropertyUtil.getSchemaProperty("attribute_pgGPSApprovalRequiredToReleaseStudy");
	public static final String SELECT_ATTRIBUTE_PG_GPSAPPROVALREQTORELSTUDY = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PG_GPSAPPROVALREQTORELSTUDY+ "]";	
	public static final String ATTRIBUTE_PG_IRMNRQID= PropertyUtil.getSchemaProperty("attribute_pgIRMNRQID");
	public static final String SELECT_ATTRIBUTE_PG_IRMNRQID = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PG_IRMNRQID+ "]";
	public static final String ATTRIBUTE_PG_PRODUCTUSEBYCMORCP= PropertyUtil.getSchemaProperty("attribute_pgUseProductByContractManufacturerOrCompetitor");
	public static final String SELECT_ATTRIBUTE_PG_PRODUCTUSEBYCMORCP = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PG_PRODUCTUSEBYCMORCP+ "]";
	public static final String ATTRIBUTE_PG_LEGACYRQID= PropertyUtil.getSchemaProperty("attribute_pgLegacyRQID");
	public static final String SELECT_ATTRIBUTE_PG_LEGACYRQID = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PG_LEGACYRQID+ "]";
	public static final String ATTRIBUTE_PG_SINGLECONSUMERSTUDY= PropertyUtil.getSchemaProperty("attribute_pgSingleConsumerStudy");
	public static final String SELECT_ATTRIBUTE_PG_SINGLECONSUMERSTUDY = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PG_SINGLECONSUMERSTUDY+ "]";
	public static final String ATTRIBUTE_PG_REQUESTEDEXPDATE= PropertyUtil.getSchemaProperty("attribute_pgRequestedExpirationDate");
	public static final String SELECT_ATTRIBUTE_PG_REQUESTEDEXPDATE = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PG_REQUESTEDEXPDATE+ "]";
	//for Agency Tasks
	public static final String ATTRIBUTE_PG_AGENCY= PropertyUtil.getSchemaProperty("attribute_pgAgency");
	public static final String SELECT_ATTRIBUTE_PG_AGENCY = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PG_AGENCY+ "]";
	public static final String RELATIONSHIP_PG_SPTODATAMERGE = PropertyUtil.getSchemaProperty("relationship_pgStudyProtocolToDataMerge");
	public static final String ATTRIBUTE_PG_PLACEMENTFIELDINGINSTRUCTIONS= PropertyUtil.getSchemaProperty("attribute_pgPlacementFieldingInstructions");
	public static final String SELECT_ATTRIBUTE_PG_PLACEMENTFIELDINGINSTRUCTIONS = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PG_PLACEMENTFIELDINGINSTRUCTIONS+ "]";
	public static final String RELATIONSHIP_PG_SPTOATTACHMENTFORMAT = PropertyUtil.getSchemaProperty("relationship_pgStudyProtocolToAttachmentFormat");
	public static final String ATTRIBUTE_PG_ESTIMATEDFIELDINGSCHEDULE= PropertyUtil.getSchemaProperty("attribute_pgEstimatedFieldingSchedule");
	public static final String SELECT_ATTRIBUTE_PG_ESTIMATEDFIELDINGSCHEDULE = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PG_ESTIMATEDFIELDINGSCHEDULE+ "]";
	public static final String RELATIONSHIP_PG_SPTOSTUDYLOCATIONS = PropertyUtil.getSchemaProperty("relationship_pgStudyProtocolToStudyLocations");
	public static final String ATTRIBUTE_PG_REQUESTEDSITESCHEDULE= PropertyUtil.getSchemaProperty("attribute_pgRequestedSiteSchedule");
	public static final String SELECT_ATTRIBUTE_PG_REQUESTEDSITESCHEDULE = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PG_REQUESTEDSITESCHEDULE+ "]";
	//study loc
	public static final String STR_NO = "No";
	public static final String STR_COMMA = ",";
	public static final String STR_RETURN_ADDRESS = "Return (Specify Address)";
	public static final String ATTRIBUTE_PGSTYDYSITES = PropertyUtil.getSchemaProperty("attribute_pgStydySites");
	public static final String SELECT_ATTRIBUTE_PGSTYDYSITES = "attribute[" + ATTRIBUTE_PGSTYDYSITES + "]";
	public static final String ATTRIBUTE_PGSTUDYLAUNCHDATE = PropertyUtil.getSchemaProperty("attribute_pgStudyLaunchDate");
	public static final String SELECT_ATTRIBUTE_PGSTUDYLAUNCHDATE = "attribute[" + ATTRIBUTE_PGSTUDYLAUNCHDATE + "]";
	public static final String ATTRIBUTE_PGADDRESSOTHERPGSITE = PropertyUtil.getSchemaProperty("attribute_pgAddressOtherPGSite");
	public static final String SELECT_ATTRIBUTE_PGADDRESSOTHERPGSITE = "attribute[" + ATTRIBUTE_PGADDRESSOTHERPGSITE + "]";
	public static final String ATTRIBUTE_PGDURATIONSTUDY = PropertyUtil.getSchemaProperty("attribute_pgDurationStudy");
	public static final String SELECT_ATTRIBUTE_PGDURATIONSTUDY = "attribute[" + ATTRIBUTE_PGDURATIONSTUDY + "]";
	public static final String ATTRIBUTE_PGSTORAGEUNPLACEDPRODUCT = PropertyUtil.getSchemaProperty("attribute_pgStorageUnplacedProduct");
	public static final String SELECT_ATTRIBUTE_PGSTORAGEUNPLACEDPRODUCT = "attribute[" + ATTRIBUTE_PGSTORAGEUNPLACEDPRODUCT + "]";
	public static final String ATTRIBUTE_PGOTHERLOCATION = PropertyUtil.getSchemaProperty("attribute_pgOtherLocation");
	public static final String SELECT_ATTRIBUTE_PGOTHERLOCATION = "attribute[" + ATTRIBUTE_PGOTHERLOCATION + "]";
	public static final String ATTRIBUTE_PGRETURNADDRESS = PropertyUtil.getSchemaProperty("attribute_pgReturnAddress");
	public static final String SELECT_ATTRIBUTE_PGRETURNADDRESS = "attribute[" + ATTRIBUTE_PGRETURNADDRESS + "]";
	public static final String RELATIONSHIP_PGGPSTASKASSESSMENTCOUNTRY = PropertyUtil.getSchemaProperty("relationship_pgGPSTaskAssessmentCountry");
	public static final String RELATIONSHIP_PGSTUDYPROTOCOLTOASSEMBLYSTATE = PropertyUtil.getSchemaProperty("relationship_pgStudyProtocolToAssemblyState");
	public static final String RELATIONSHIP_PGSTUDYPROTOCOLTOSTUDYSITES = PropertyUtil.getSchemaProperty("relationship_pgStudyProtocolToStudySites");
	public static final String RELATIONSHIP_PGSTUDYPROTOCOLTOSURPLUSDISPOSITION = PropertyUtil.getSchemaProperty("relationship_pgStudyProtocolToSurplusDisposition");
	public static final String TYPE_COUNTRY = PropertyUtil.getSchemaProperty("type_Country");
	public static final String TYPE_PGPLIASSEMBLYSTATE = PropertyUtil.getSchemaProperty("type_pgPLIAssemblyState");
	public static final String TYPE_PGPLISTUDYSITES = PropertyUtil.getSchemaProperty("type_pgPLIStudySites");
	public static final String TYPE_PGPLISURPLUSDISPOSITION = PropertyUtil.getSchemaProperty("type_pgPLISurplusDisposition");

	public static final String ATTRIBUTE_PG_PRODUCTCODEPANELISTS= PropertyUtil.getSchemaProperty("attribute_pgProductCodePanelists");
	public static final String SELECT_ATTRIBUTE_PG_PRODUCTCODEPANELISTS = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PG_PRODUCTCODEPANELISTS+ "]";
	public static final String ATTRIBUTE_PG_SITEIFOTHERSELECTED= PropertyUtil.getSchemaProperty("attribute_pgSiteIfOtherSelected");
	public static final String SELECT_ATTRIBUTE_PG_SITEIFOTHERSELECTED = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PG_SITEIFOTHERSELECTED+ "]";
	public static final String ATTRIBUTE_PG_FREQUENCYSAMEASCURRENTMARKETPRODUCT= PropertyUtil.getSchemaProperty("attribute_pgFrequencySameAsCurrentMarketProduct");
	public static final String SELECT_ATTRIBUTE_PG_FREQUENCYSAMEASCURRENTMARKETPRODUCT = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PG_FREQUENCYSAMEASCURRENTMARKETPRODUCT+ "]";
	public static final String ATTRIBUTE_PG_NUMBEROFUNITSPERBAG= PropertyUtil.getSchemaProperty("attribute_pgNumberofUnitsperBag");
	public static final String SELECT_ATTRIBUTE_PG_NUMBEROFUNITSPERBAG = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PG_NUMBEROFUNITSPERBAG+ "]";
	public static final String ATTRIBUTE_PG_RELPACKINGSITE= PropertyUtil.getSchemaProperty("attribute_pgRelPackingSite");
	public static final String SELECT_ATTRIBUTE_PG_RELPACKINGSITE = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PG_RELPACKINGSITE+ "]";
	public static final String ATTRIBUTE_PG_MICROMCT= PropertyUtil.getSchemaProperty("attribute_pgMicroMCT");
	public static final String SELECT_ATTRIBUTE_PG_MICROMCT = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PG_MICROMCT+ "]";
	public static final String ATTRIBUTE_PG_PANELISTCOMPLETES= PropertyUtil.getSchemaProperty("attribute_pgPanelistsCompletes");
	public static final String SELECT_ATTRIBUTE_PG_PANELISTCOMPLETES = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PG_PANELISTCOMPLETES+ "]";
	public static final String ATTRIBUTE_PG_UNITSTOLABEL= PropertyUtil.getSchemaProperty("attribute_pgUnitsToLabel");
	public static final String SELECT_ATTRIBUTE_PG_UNITSTOLABEL = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PG_UNITSTOLABEL+ "]";
	public static final String ATTRIBUTE_PG_LABELLING= PropertyUtil.getSchemaProperty("attribute_pgLabeling");
	public static final String SELECT_ATTRIBUTE_PG_LABELLING = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PG_LABELLING+ "]";
	public static final String ATTRIBUTE_PG_ISPRODUCYREPACKAGED= PropertyUtil.getSchemaProperty("attribute_pgIsProductRepackaged");
	public static final String SELECT_ATTRIBUTE_PG_ISPRODUCYREPACKAGED = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PG_ISPRODUCYREPACKAGED+ "]";
	public static final String ATTRIBUTE_PG_UNITWEIGHT= PropertyUtil.getSchemaProperty("attribute_pgUnitWeight");
	public static final String SELECT_ATTRIBUTE_PG_UNITWEIGHT = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PG_UNITWEIGHT+ "]";	
	public static final String ATTRIBUTE_PG_EBPSTUDY= PropertyUtil.getSchemaProperty("attribute_pgEBPStudy");
	public static final String SELECT_ATTRIBUTE_PG_EBPSTUDY = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PG_EBPSTUDY+ "]";	
	public static final String RELATIONSHIP_PG_MANUFACTURINGRESPONSIBILITY_LEG = PropertyUtil.getSchemaProperty("relationship_pgManufacturingResponsibilityLeg");
	
	public static final String TYPE_IPCONTROLCLASS                          = PropertyUtil.getSchemaProperty("type_IPControlClass");
	public static final String TYPE_SECURITYCONTROLCLASS                  = PropertyUtil.getSchemaProperty("type_SecurityControlClass");
	public static final String RELATIONSHIP_CLASSIFIEDITEM                  = PropertyUtil.getSchemaProperty("relationship_ClassifiedItem");
	public static final String TYPE_PGGPSASSESSMENTTASK = PropertyUtil.getSchemaProperty("type_pgGPSAssessmentTask"); 
	public static final String RELATIONSHIP_PGGPSASSESSMENTTASKINPUTS = PropertyUtil.getSchemaProperty("relationship_pgGPSAssessmentTaskInputs"); 
	
	public static final String ATTRIBUTE_PGPRODUCTUSELOCATION= PropertyUtil.getSchemaProperty("attribute_pgProductUseLocation");
	public static final String SELECT_ATTRIBUTE_PGPRODUCTUSELOCATION = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PGPRODUCTUSELOCATION+ "]";	
	//Added for IP Security table for Requirement 32753 - Ends
	//Added by DSM(Sogeti)- 2018x.5 for Defect 33366 Start
	public static final String ATTRIBUTE_PGFLAVORCLUSTERRANK= PropertyUtil.getSchemaProperty("attribute_pgFlavorClusterRank");
	public static final String SELECT_ATTRIBUTE_PGFLAVORCLUSTERRANK = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PGFLAVORCLUSTERRANK+ "]";
	//Added by DSM(Sogeti)- 2018x.5 for Defect 33366 End
	// Added by DSM for PDF Views  : Starts
	public static final String ATTRIBUTE_PGPROJECTMILESTONE= PropertyUtil.getSchemaProperty("attribute_pgProjectMilestone");
	public static final String SELECT_ATTRIBUTE_PGPROJECTMILESTONE = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PGPROJECTMILESTONE+ "]";
	public static final String ATTRIBUTE_PGSTRUCTUREDRELEASECRITERIAREQUIRED= PropertyUtil.getSchemaProperty("attribute_pgStructuredReleaseCriteriaRequired");
	public static final String SELECT_ATTRIBUTE_PGSTRUCTUREDRELEASECRITERIAREQUIRED = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PGSTRUCTUREDRELEASECRITERIAREQUIRED+ "]";
	//Added by DSM for PDF Views (Req id #49326) - Start 
	public static final String ATTRIBUTE_PGSTRUCTUREDPERFORMANCECHARACTERISTICSREQUIRED= PropertyUtil.getSchemaProperty("attribute_pgNexusStructuredPerfCharsRequired");
	public static final String SELECT_ATTRIBUTE_PGSTRUCTUREDPERFORMANCECHARACTERISTICSREQUIRED = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PGSTRUCTUREDPERFORMANCECHARACTERISTICSREQUIRED+ "]";
	//Added by DSM for PDF Views (Req id #49326) - End 
	public static final String RELATIONSHIP_PGDOCUMENTTOPLATFORM = PropertyUtil.getSchemaProperty("relationship_pgDocumentToPlatform");
	public static final String ATTRIBUTE_PGPRODUCTCATEGORYPLATFORM= PropertyUtil.getSchemaProperty("attribute_pgProductCategoryPlatform");
	public static final String SELECT_ATTRIBUTE_PGPRODUCTCATEGORYPLATFORM = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PGPRODUCTCATEGORYPLATFORM+ "]";
	public static final String ATTRIBUTE_PGPRODUCTTECHNOLOGYPLATFORM= PropertyUtil.getSchemaProperty("attribute_pgProductTechnologyPlatform");
	public static final String SELECT_ATTRIBUTE_PGPRODUCTTECHNOLOGYPLATFORM = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PGPRODUCTTECHNOLOGYPLATFORM+ "]";
	public static final String ATTRIBUTE_PGPRODUCTTECHNOLOGYCHASSIS= PropertyUtil.getSchemaProperty("attribute_pgProductTechnologyChassis");
	public static final String SELECT_ATTRIBUTE_PGPRODUCTTECHNOLOGYCHASSIS = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PGPRODUCTTECHNOLOGYCHASSIS+ "]";
	public static final String ATTRIBUTE_PGFRANCHISEPLATFORM= PropertyUtil.getSchemaProperty("attribute_pgFranchisePlatform");
	public static final String SELECT_ATTRIBUTE_PGFRANCHISEPLATFORM = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PGFRANCHISEPLATFORM+ "]";
	public static final String ATTRIBUTE_PGREPLACEDBYPRODUCTID = PropertyUtil.getSchemaProperty("attribute_pgReplacedByProductID");
	public static final String SELECT_ATTRIBUTE_PGREPLACEDBYPRODUCTID = "attribute[" + ATTRIBUTE_PGREPLACEDBYPRODUCTID + "]";
	
	public static final String ATTRIBUTE_PGHASART = PropertyUtil.getSchemaProperty("attribute_pgHasArt");
	public static final String SELECT_ATTRIBUTE_PGHASART = "attribute[" + ATTRIBUTE_PGHASART + "]";
	public static final String ATTRIBUTE_PGHASMULTIPLEGTINS = PropertyUtil.getSchemaProperty("attribute_pgHasMultipleGTINS");
	public static final String SELECT_ATTRIBUTE_PGHASMULTIPLEGTINS = "attribute[" + ATTRIBUTE_PGHASMULTIPLEGTINS + "]";
	
	
	public static final String ATTRIBUTE_PGPREFERREDMATERIAL = PropertyUtil.getSchemaProperty("attribute_pgPreferredMaterial");
	public static final String SELECT_ATTRIBUTE_PGPREFERREDMATERIAL = "attribute[" + ATTRIBUTE_PGPREFERREDMATERIAL + "]";
	public static final String ATTRIBUTE_PGOVERHANGACTUALWIDTH = PropertyUtil.getSchemaProperty("attribute_pgOverhangActualWidth");
	public static final String SELECT_ATTRIBUTE_PGOVERHANGACTUALWIDTH = "attribute[" + ATTRIBUTE_PGOVERHANGACTUALWIDTH + "]";
	public static final String ATTRIBUTE_PGOVERHANGACTUALLENGTH = PropertyUtil.getSchemaProperty("attribute_pgOverhangActualLength");
	public static final String SELECT_ATTRIBUTE_PGOVERHANGACTUALLENGTH = "attribute[" + ATTRIBUTE_PGOVERHANGACTUALLENGTH + "]";
	public static final String ATTRIBUTE_PGUNDERHANGACTUALLENGTH = PropertyUtil.getSchemaProperty("attribute_pgUnderhangActualLength");
	public static final String SELECT_ATTRIBUTE_PGUNDERHANGACTUALLENGTH = "attribute[" + ATTRIBUTE_PGUNDERHANGACTUALLENGTH + "]";
	public static final String ATTRIBUTE_PGUNDERHANGACTUALWIDTH = PropertyUtil.getSchemaProperty("attribute_pgUnderhangActualWidth");
	public static final String SELECT_ATTRIBUTE_PGUNDERHANGACTUALWIDTH = "attribute[" + ATTRIBUTE_PGUNDERHANGACTUALWIDTH + "]";
	public static final String CURRENTACCESSFROMDISCONNECT = "current.access[fromdisconnect]";
	
	
	
	public static final String TYPE_PGPLIFRANCHISEPLATFORM = PropertyUtil.getSchemaProperty("type_pgPLIFranchisePlatform");
	public static final String TYPE_PGPLIPRODUCTCATEGORYPLATFORM = PropertyUtil.getSchemaProperty("type_pgPLIProductCategoryPlatform");
	public static final String TYPE_PGPLIPRODUCTTECHNOLOGYPLATFORM = PropertyUtil.getSchemaProperty("type_pgPLIProductTechnologyPlatform");
	public static final String TYPE_PGPLIPRODUCTTECHNOLOGYCHASSIS = PropertyUtil.getSchemaProperty("type_pgPLIProductTechnologyChassis");
	
	
	public static final String FRANCHISE = "from["+pgPDFViewConstants.RELATIONSHIP_PGDOCUMENTTOPLATFORM+"].to["+pgPDFViewConstants.TYPE_PGPLIFRANCHISEPLATFORM+"].name";
	public static final String PRODUCTCATEGORYPLATFORM = "from["+pgPDFViewConstants.RELATIONSHIP_PGDOCUMENTTOPLATFORM+"].to["+pgPDFViewConstants.TYPE_PGPLIPRODUCTCATEGORYPLATFORM+"].name";
	public static final String TECHNOLOGYPLATFORM = "from["+pgPDFViewConstants.RELATIONSHIP_PGDOCUMENTTOPLATFORM+"].to["+pgPDFViewConstants.TYPE_PGPLIPRODUCTTECHNOLOGYPLATFORM+"].name";
	public static final String RELATIONSHIP_PGDOCUMENTTOCHASSIS = PropertyUtil.getSchemaProperty("relationship_pgDocumentToChassis");
	public static final String TECHNOLOGYCHASSIS = "from["+pgPDFViewConstants.RELATIONSHIP_PGDOCUMENTTOCHASSIS+"].to["+pgPDFViewConstants.TYPE_PGPLIPRODUCTTECHNOLOGYCHASSIS+"].name";
	
	public static final String RELATIONSHIP_PGDOCUMENTTOBUSINESSAREA = PropertyUtil.getSchemaProperty("relationship_pgDocumentToBusinessArea");
	public static final String BUSINESSAREA = "from["+pgPDFViewConstants.RELATIONSHIP_PGDOCUMENTTOBUSINESSAREA+"].to.name";
	
	public static final String RELATIONSHIP_PGPRIMARYPACKAGINGTYPE = PropertyUtil.getSchemaProperty("relationship_pgPrimaryPackagingType");
	public static final String PRIMARYPACKAGINGTYPE = "from["+pgPDFViewConstants.RELATIONSHIP_PGPRIMARYPACKAGINGTYPE+"].to.name";
	// Added by DSM for PDF Views  : Ends
	
	 
	
	//Added by DSM 2018x.5 Defect req : 33190 Start
	public static final String ATTRIBUTE_PGAUTHORINGAPPLICATION= PropertyUtil.getSchemaProperty("attribute_pgAuthoringApplication");
	public static final String SELECT_ATTRIBUTE_PGAUTHORINGAPPLICATION = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PGAUTHORINGAPPLICATION+ "]";
	//Added by DSM 2018x.5 Defect req : 33190 End
	
	// Added by DSM(Sogeti) for 2018x.5 PDF Views : Starts
    public static final String ATTRIBUTE_CHARACTERISTICCATEGORY= PropertyUtil.getSchemaProperty("attribute_CharacteristicCategory");
    public static final String SELECT_ATTRIBUTE_CHARACTERISTICCATEGORY = "attribute[" + ATTRIBUTE_CHARACTERISTICCATEGORY+ "]";
    public static final String ATTRIBUTE_SEQUENCEORDER= PropertyUtil.getSchemaProperty("attribute_SequenceOrder");
    public static final String SELECT_ATTRIBUTE_SEQUENCEORDER = "attribute[" + ATTRIBUTE_SEQUENCEORDER+ "]";
    public static final String ATTRIBUTE_EVALUATEDCRITERIA= PropertyUtil.getSchemaProperty("attribute_EvaluatedCriteria");
    public static final String SELECT_ATTRIBUTE_EVALUATEDCRITERIA = "attribute[" + ATTRIBUTE_EVALUATEDCRITERIA+ "]";
    public static final String RELATIONSHIP_DERIVEDCHARACTERISTIC = PropertyUtil.getSchemaProperty("relationship_DerivedCharacteristic");
    public static final String ATTRIBUTE_PGMATERIALRESTRICTION= PropertyUtil.getSchemaProperty("attribute_pgMaterialRestriction");
    public static final String SELECT_ATTRIBUTE_PGMATERIALRESTRICTION = "attribute[" + ATTRIBUTE_PGMATERIALRESTRICTION+ "]";
    public static final String ATTRIBUTE_PGMATERIALRESTRICTIONCOMMENT= PropertyUtil.getSchemaProperty("attribute_pgMaterialRestrictionComment");
    public static final String SELECT_ATTRIBUTE_PGMATERIALRESTRICTIONCOMMENT = "attribute[" + ATTRIBUTE_PGMATERIALRESTRICTIONCOMMENT+ "]";
    public static final String ATTRIBUTE_PGROLLUPNETWEIGHTTOCOP= PropertyUtil.getSchemaProperty("attribute_pgRollUpNetWeightToCOP");
    public static final String SELECT_ATTRIBUTE_PGROLLUPNETWEIGHTTOCOP = "attribute[" + ATTRIBUTE_PGROLLUPNETWEIGHTTOCOP+ "]";
    public static final String ATTRIBUTE_PGDESIGNSPECIFICS= PropertyUtil.getSchemaProperty("attribute_pgDesignSpecifics");
    public static final String SELECT_ATTRIBUTE_PGDESIGNSPECIFICS = "attribute[" + ATTRIBUTE_PGDESIGNSPECIFICS+ "]";
    public static final String ATTRIBUTE_HYDROPHILICLIPOPHILICBALANCE = PropertyUtil.getSchemaProperty("attribute_HydrophilicLipophilicBalance");
    public static final String SELECT_ATTRIBUTE_HYDROPHILICLIPOPHILICBALANCE = "attribute["  + ATTRIBUTE_HYDROPHILICLIPOPHILICBALANCE + "]";
    // Added by DSM(Sogeti) for 2018x.5 PDF Views : Ends
    
    public static final String ATTR_VALID_START_DATE = PropertyUtil.getSchemaProperty("attribute_pgValidStartDate");
    public static final String SELECT_ATTR_VALID_START_DATE = "attribute[" + ATTR_VALID_START_DATE + "]";
    public static final String ATTRIBUTE_TECHNICAL_NAME_1 = PropertyUtil.getSchemaProperty("attribute_pgTechnicalName1");
    public static final String SELECT_ATTRIBUTE_TECHNICAL_NAME_1 = "attribute[" + ATTRIBUTE_TECHNICAL_NAME_1 + "]";
    public static final String ATTRIBUTE_TECHNICAL_NAME_2 = PropertyUtil.getSchemaProperty("attribute_pgTechnicalName2");
    public static final String SELECT_ATTRIBUTE_TECHNICAL_NAME_2 = "attribute[" + ATTRIBUTE_TECHNICAL_NAME_2 + "]";
    public static final String ATTRIBUTE_IS_LIQUID_AQE_SOL = PropertyUtil.getSchemaProperty("attribute_pgIsTheLiquidanAqeousSolution");
    public static final String SELECT_ATTRIBUTE_IS_LIQUID_AQE_SOL = "attribute[" + ATTRIBUTE_IS_LIQUID_AQE_SOL + "]";
    public static final String ATTRIBUTE_RESERVE_ALKALINITY_UOM = PropertyUtil.getSchemaProperty("attribute_pgReserveAlkalinityUoM");
    public static final String SELECT_ATTRIBUTE_RESERVE_ALKALINITY_UOM = "attribute[" + ATTRIBUTE_RESERVE_ALKALINITY_UOM + "]";
    public static final String ATTRIBUTE_RESERVE_ALKALINITY_TIT_END_POINT = PropertyUtil.getSchemaProperty("attribute_pgReserveAlkalinityTitrationEndPoint");
    public static final String SELECT_ATTRIBUTE_RESERVE_ALKALINITY_TIT_END_POINT = "attribute[" + ATTRIBUTE_RESERVE_ALKALINITY_TIT_END_POINT + "]";
    public static final String ATTRIBUTE_PH_MIN = PropertyUtil.getSchemaProperty("attribute_pgpHMin");
    public static final String SELECT_ATTRIBUTE_PH_MIN = "attribute[" + ATTRIBUTE_PH_MIN + "]";
    public static final String ATTRIBUTE_PH_MAX = PropertyUtil.getSchemaProperty("attribute_pgpHMax");
    public static final String SELECT_ATTRIBUTE_PH_MAX = "attribute[" + ATTRIBUTE_PH_MAX + "]";
    public static final String ATTRIBUTE_PH_DATA_AVAILABLE = PropertyUtil.getSchemaProperty("attribute_pgPHDataAvailable");
    public static final String SELECT_ATTRIBUTE_PH_DATA_AVAILABLE = "attribute[" + ATTRIBUTE_PH_DATA_AVAILABLE + "]";
    public static final String ATTRIBUTE_IS_PRODUCT_DILUTED_FOR_USE = PropertyUtil.getSchemaProperty("attribute_pgIsPGPProductdilutedforUse");
    public static final String SELECT_ATTRIBUTE_IS_PRODUCT_DILUTED_FOR_USE = "attribute[" + ATTRIBUTE_IS_PRODUCT_DILUTED_FOR_USE + "]";
    public static final String ATTRIBUTE_PERCENT_CONCENTRATION = PropertyUtil.getSchemaProperty("attribute_PercentConcentration");
    public static final String SELECT_ATTRIBUTE_PERCENT_CONCENTRATION = "attribute[" + ATTRIBUTE_PERCENT_CONCENTRATION + "]";
    public static final String ATTRIBUTE_PERCENT_WATER = PropertyUtil.getSchemaProperty("attribute_pgPercentWater");
    public static final String SELECT_ATTRIBUTE_PERCENT_WATER = "attribute[" + ATTRIBUTE_PERCENT_WATER + "]";
    public static final String ATTR_PRIMARY_ARTWORK = PropertyUtil.getSchemaProperty("attribute_pgArtworkPrimary");
    public static final String SELECT_ATTR_PRIMARY_ARTWORK = "attribute[" + ATTR_PRIMARY_ARTWORK + "]";
    public static final String ATTR_LEGACY_PRODUCT_WEIGHT = PropertyUtil.getSchemaProperty("attribute_pgLegacyProductWeight");
    public static final String SELECT_ATTR_LEGACY_PRODUCT_WEIGHT = "attribute[" + ATTR_LEGACY_PRODUCT_WEIGHT + "]";
    public static final String ATTR_INHERITANCE_TYPE = PropertyUtil.getSchemaProperty("attribute_pgPFInheritanceType");
    public static final String SELECT_ATTR_INHERITANCE_TYPE = "attribute[" + ATTR_INHERITANCE_TYPE + "]";
    public static final String ATTR_PG_OVER_HANG_ACTUAL_WIDTH = PropertyUtil.getSchemaProperty("attribute_pgOverhangActualWidth");
    public static final String SELECT_ATTR_PG_OVER_HANG_ACTUAL_WIDTH = "attribute[" + ATTR_PG_OVER_HANG_ACTUAL_WIDTH + "]";
    public static final String ATTR_PG_OVER_HANG_ACTUAL_LENGTH = PropertyUtil.getSchemaProperty("attribute_pgOverhangActualLength");
    public static final String SELECT_ATTR_PG_OVER_HANG_ACTUAL_LENGTH = "attribute[" + ATTR_PG_OVER_HANG_ACTUAL_LENGTH + "]";
    public static final String ATTR_PG_UNDER_HANG_ACTUAL_LENGTH = PropertyUtil.getSchemaProperty("attribute_pgUnderhangActualLength");
    public static final String SELECT_ATTR_PG_UNDER_HANG_ACTUAL_LENGTH = "attribute[" + ATTR_PG_UNDER_HANG_ACTUAL_LENGTH + "]";
    public static final String ATTR_PG_UNDER_HANG_ACTUAL_WIDTH = PropertyUtil.getSchemaProperty("attribute_pgUnderhangActualWidth");
    public static final String SELECT_ATTR_PG_UNDER_HANG_ACTUAL_WIDTH= "attribute[" + ATTR_PG_UNDER_HANG_ACTUAL_WIDTH + "]";
    
    public static final String RELATIONSHIP_PGPRODUCTPLATFORMFOP = PropertyUtil.getSchemaProperty("relationship_pgProductPlatformFOP");
    public static final String ATTRIBUTE_LN = "attribute_pgLayerName";
    public static final String ATTRIBUTE_LGN = "attribute_pgLayerGroupName";
    public static final String ATTR_PG_LAYER_NAME =  PropertyUtil.getSchemaProperty("attribute_pgLayerName");
    public static final String ATTR_PG_LAYER_GROUP_NAME =  PropertyUtil.getSchemaProperty("attribute_pgLayerGroupName");
    public static final String COLUMN_PGLAYER_NAME = "pgLayerName";
    public static final String COLUMN_PGLAYER_GROUP_NAME = "pgPlyGroupName";
    public static final String ATTRIBUTE_MIN_ACTUAL_PERCENT_WET = "attribute_pgMinActualPercenWet";
    public static final String ATTRIBUTE_MAX_ACTUAL_PERCENT_WET = "attribute_pgMaxActualPercenWet";
    public static final String COLUMN_MIN_SUB = "pgMinActualPercentWet";
    public static final String COLUMN_MAX_SUB = "pgMaxActualPercentWet";
    public static final String METHOD_LAYER_NAME = "getLayerNameForSubstitute";
    public static final String METHOD_LAYER_GROUP_NAME = "getPlyGroupNameForSubstitute";
	//Added by DSM(Sogeti)-2018x.5 for PDF Views Requirements 35306, 32960, 32964, 32965, 32966, 35338, 34926, 32866 and 32869 POA ATS Gendoc- Starts
    public static final String ATTRIBUTE_IMPACTED_TYPE= PropertyUtil.getSchemaProperty("attribute_pgImpactedType");
    public static final String SELECT_ATTRIBUTE_IMPACTED_TYPE = "attribute[" + ATTRIBUTE_IMPACTED_TYPE+ "]";
    public static final String POLICY_PRODUCT_DATA_SPECIFICATION = PropertyUtil.getSchemaProperty("policy_IPMSpecification");
	//Added by DSM(Sogeti)-2018x.5 for PDF Views Requirements 35306, 32960, 32964, 32965, 32966, 35338, 34926, 32866 and 32869 POA ATS Gendoc- Ends

    // Added by DSM(Sogeti)-2018x.5 for PDF Views Defect# 34507 Starts
    public static final String SELECT_LAST_TITLE = "last."+ pgV3Constants.SELECT_ATTRIBUTE_TITLE;
	
	//Added BY IRM Team in 2018x.5 for Defect 33007 Starts
	public static final String STATE_REVIEW = "Review";
	public static final String STR_PG_PRELIMINARY = " P&G PRELIMINARY Printed ";
	public static final String STR_PG_REVIEW = " P&G REVIEW Printed ";
	public static final String STR_PG_RELEASE = " P&G RELEASE Printed ";
	public static final String STR_PG_OBSOLETE = " P&G OBSOLETE Printed ";
	//Added BY IRM Team in 2018x.5 for Defect 33007 Ends
	//Added by DSM(Sogeti)-2018x.5 for PDF Views Defect #35276: Starts
	public static final String ATTRIBUTE_PGPRODUCTSUSTAINCOMBUSTION= PropertyUtil.getSchemaProperty("attribute_pgProductSustainCombustion");
	public static final String SELECT_ATTRIBUTE_PGPRODUCTSUSTAINCOMBUSTION = "attribute[" +ATTRIBUTE_PGPRODUCTSUSTAINCOMBUSTION+ "]";
	//Added by DSM(Sogeti)-2018x.5 for PDF Views Defect #35276: Ends
	public static final String CONS_MAIN = "MAIN";
	public static final String CONS_COP = "COP";
	public static final String CONS_CUP = "CUP";
	public static final String CONS_IP = "IP";
	public static final int DECIMAL_SIX = 6;
	//Added By IRM Team in 2018x.5 for Defect 35543 Starts
	public static final String ATTR_PANELIST_EXCLUSION_DETAILS = PropertyUtil.getSchemaProperty("attribute_pgPanelistExclusionDetails");
    public static final String SELECT_ATTR_PANELIST_EXCLUSION_DETAILS = "attribute[" + ATTR_PANELIST_EXCLUSION_DETAILS + "]";
	public static final String ATTR_PANELIST_INCLUSION_DETAILS = PropertyUtil.getSchemaProperty("attribute_pgPanelistInclusionDetails");
    public static final String SELECT_ATTR_PANELIST_INCLUSION_DETAILS = "attribute[" + ATTR_PANELIST_INCLUSION_DETAILS + "]";
	public static final String RELATIONSHIP_PGSTUDYPROTOCOLTOPLIPANELISTSUPERVISION = PropertyUtil.getSchemaProperty("relationship_pgStudyProtocolToPLIPanelistSupervision");
	//Added By IRM Team in 2018x.5 for Defect 35543 Ends
	public static final String CONS_GENDOC = "gendoc";
	public static final String ATTRIBUTE_PGCERTFICATIONSTATUS = PropertyUtil.getSchemaProperty("attribute_pgCertficationStatus");
	//Added by DSM(Sogeti) for Defect #37937 for 2018x.5_Jan Downtime PDF Views - Starts
	public static final String CONST_ATS_ALLOWEDTYPES = pgV3Constants.TYPE_FINISHEDPRODUCTPART + pgPDFViewConstants.STR_COMMA + pgV3Constants.TYPE_PGCONSUMERUNITPART +pgPDFViewConstants.STR_COMMA + pgV3Constants.TYPE_PGCUSTOMERUNITPART + pgPDFViewConstants.STR_COMMA + pgV3Constants.TYPE_PGINNERPACKUNITPART + pgPDFViewConstants.STR_COMMA + pgV3Constants.TYPE_PGTRANSPORTUNITPART + pgPDFViewConstants.STR_COMMA + pgV3Constants.TYPE_PACKAGINGMATERIALPART + pgPDFViewConstants.STR_COMMA + pgV3Constants.TYPE_PACKAGINGASSEMBLYPART + pgPDFViewConstants.STR_COMMA + pgV3Constants.TYPE_FABRICATEDPART + pgPDFViewConstants.STR_COMMA + pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART + pgPDFViewConstants.STR_COMMA + pgV3Constants.TYPE_DEVICEPRODUCTPART + pgPDFViewConstants.STR_COMMA + pgV3Constants.TYPE_FORMULATIONPART + pgPDFViewConstants.STR_COMMA + pgV3Constants.TYPE_RAWMATERIALPART + pgPDFViewConstants.STR_COMMA + pgV3Constants.TYPE_INTERMEDIATE_PRODUCT_PART + pgPDFViewConstants.STR_COMMA + pgV3Constants.TYPE_PGFORMULATEDPRODUCT + pgPDFViewConstants.STR_COMMA + pgV3Constants.TYPE_PGFINISHEDPRODUCT + pgPDFViewConstants.STR_COMMA + pgV3Constants.TYPE_PGPACKINGMATERIAL + pgPDFViewConstants.STR_COMMA + pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY + pgPDFViewConstants.STR_COMMA + pgV3Constants.TYPE_PGRAWMATERIAL;
	//Added by DSM(Sogeti) for Defect #37937 for 2018x.5_Jan Downtime PDF Views - Ends
	//Added by DSM(Sogeti)-2018x.6 for PDF Views Req#36434,36465: Starts
	public static final String ATTRIBUTE_PGPACKAGINGTYPE = PropertyUtil.getSchemaProperty("attribute_pgPackagingType");
	public static final String SELECT_ATTRIBUTE_PGPACKAGINGTYPE = "attribute[" + ATTRIBUTE_PGPACKAGINGTYPE + "]";
	public static final String ATTRIBUTE_PGPACKAGINGSIZE = PropertyUtil.getSchemaProperty("attribute_pgPackagingSize");
	public static final String SELECT_ATTRIBUTE_PGPACKAGINGSIZE = "attribute[" + ATTRIBUTE_PGPACKAGINGSIZE + "]";
	public static final String ATTRIBUTE_PGPACKAGINGSIZEUOM = PropertyUtil.getSchemaProperty("attribute_pgPackagingSizeUOM");
	public static final String SELECT_ATTRIBUTE_PGPACKAGINGSIZEUOM = "attribute[" + ATTRIBUTE_PGPACKAGINGSIZEUOM + "]";
	public static final String ATTRIBUTE_PGPOAIMPACTCONFIRMATION = PropertyUtil.getSchemaProperty("attribute_pgPOAImpactConfirmation");
	public static final String SELECT_ATTRIBUTE_PGPOAIMPACTCONFIRMATION = "attribute[" + ATTRIBUTE_PGPOAIMPACTCONFIRMATION + "]";
	public static final String TYPE_PGCUSTOMERUNITPART				= "pgCustomerUnitPart";
	public static final String TYPE_PGMASTERPACKAGINGMATERIALPART 	= "pgMasterPackagingMaterialPart";
	public static final String TYPE_PGCONSUMERUNITPART				= "pgConsumerUnitPart";
	public static final String TYPE_DEVICEPRODUCTPART				= "pgDeviceProductPart";
	//Added by DSM(Sogeti)-2018x.6 for PDF Views Req#36434,36465: Ends
	//Added by DSM-2018x.6 for PDF Views Defect #38203: Start
    public static final String ATTRIBUTE_PGSETPRODUCTNAME = PropertyUtil.getSchemaProperty("attribute_pgSetProductName");
    public static final String SELECT_ATTRIBUTE_PGSETPRODUCTNAME = "attribute[" + ATTRIBUTE_PGSETPRODUCTNAME + "]";
    public static final String ATTRIBUTE_PGNUMBEROFBATTERIESREQUIRED = PropertyUtil.getSchemaProperty("attribute_pgNumberOfBatteriesRequired");
    public static final String SELECT_ATTRIBUTE_PGNUMBEROFBATTERIESREQUIRED = "attribute[" + ATTRIBUTE_PGNUMBEROFBATTERIESREQUIRED + "]";
  //Added by DSM-2018x.6 for PDF Views Defect #38203: End
  //Refactor 2018x.6 start
  	public static final String CLASSIFIED = "to[" + pgV3Constants.RELATIONSHIP_CLASSIFIEDITEM + "].from["
  			+ DomainConstants.TYPE_PART_FAMILY + "].name";
  	public static final String PGBATTERYTYPE = "from[" + pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLIBATTERYTYPE
  			+ "].to.name";
  	public static final String PGLIFECYCLE = "from[" + pgV3Constants.RELATIONSHIP_PGPDTEMPLSTESTOPGPLILIFECYCLESTATUS
  			+ "].to.name";
  	public static final String ORIGINATINGTEMPLATE = "to[" + pgV3Constants.RELATIONSHIP_ORIGINATINGTEMPLATE + "].from.name";
  	public static final String AFFECTEDITEM = "to[" + pgV3Constants.RELATIONSHIP_AFFECTEDITEM + "].from.name";
  	public static final String CHANGEAFFECTEDITEM = "to[" + pgV3Constants.RELATIONSHIP_CHANGEAFFECTEDITEM + "].from.name";
  	public static final String CHANGEORDER = "to[" + pgV3Constants.RELATIONSHIP_CHANGEAFFECTEDITEM + "].from.to["
  			+ pgV3Constants.RELATIONSHIP_CHANGEACTION + "].from[" + pgV3Constants.TYPE_CHANGEORDER + "].name";
  	public static final String PGBRAND = "from[" + pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGBRAND + "].to.name";
  	public static final String PGCOUNTRY = "from[" + pgV3Constants.RELATIONSHIP_PGGPSTASKASSESSMENTCOUNTRY + "].to.name";
  	public static final String MATERIALGROUP = "from[" + pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGMATERIALGROUP + "].to.name";
  	public static final String BOMBASEQTY = "from[" + pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLIBOMBASEQUANTITY+ "].to.name";
  	public static final String PGPLIUS = "from[" + pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLUNITSIZE + "].to.name";
  	public static final String PGPDTTOPGPLICLASS = "from[" + pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLICLASS + "].to.name";
  	public static final String PGPDTTOPGPLIREFUN = "from[" + pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLIREPORTEDFUNCTION+ "].to.name";
  	public static final String PARTSPEC = "from[" + pgV3Constants.RELATIONSHIP_PARTSPECIFICATION + "].to.name";
  	public static final String PARTSPECSPS = "from[" + pgV3Constants.RELATIONSHIP_PARTSPECIFICATION + "].to["+ pgV3Constants.TYPE_PGSTACKINGPATTERN + "].name";
  	public static final String COOWNED = "from[" + pgV3Constants.RELATIONSHIP_COOWNED + "].to.name";
  	public static final String REGIONOWNS = "from[" + pgV3Constants.RELATIONSHIP_REGIONOWNS + "].to.name";
  	public static final String SHIPPINGHC = "from[pgToShippingHazardClasssification].to.name";
  	public static final String PRINTINGPROSS = "from[" + pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLIPRINTINGPROCESS+ "].to.name";
  	public static final String MATERIALCERTIFICATION = "from[" + pgV3Constants.RELATIONSHIP_PG_PLI_MATERIAL_CERTIFICATIONS+ "].to.name";
  	public static final String INTENDEDMARKET = "from[" + pgV3Constants.RELATIONSHIP_PGINTENDEDMARKETS + "].to.name";
  	public static final String SEGMENT = "from[" + pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLISEGMENT + "].to.name";
  	public static final String KEYFORMULATION = "to[" + pgV3Constants.RELATIONSHIP_FORMULATIONPROPAGATE + "].from.name";
  	public static final String KEYFORMULATIONTYPE = "to[" + pgV3Constants.RELATIONSHIP_FORMULATIONPROPAGATE + "].from.attribute["
  			+ pgV3Constants.ATTRIBUTE_FORMULATIONTYPE + "]";
  	public static final String PGBUSINESSAREA = "from["+pgV3Constants.RELATIONSHIP_PGDOCUMENTTOBUSINESSAREA+"].to.name";
  	public static final String PGPACKAGINGTECHNOLOGY = PropertyUtil.getSchemaProperty("attribute_pgPackagingTechnology");
  	
  	public static final String COMMONKEYSTRING = "from[Part Specification].to.name,from[Part Specification].to[pgStackingPattern].name,from[pgPDTemplatestopgPLIPrintingProcess].to.name,from[Region Owns].to.name,from[CoOwned].to.name,to[Classified Item].from[Part Family].name,from[pgPDTemplatestopgPLIBatteryType].to.name,from[pgPDTemplatestopgPLILifeCycleStatus].to.name,to[Originating Template].from.name,to[Affected Item].from.name,to[Change Affected Item].from.name,to[Change Affected Item].from.to[Change Action].from[Change Order].name,from[pgPDTemplatestopgBrand].to.name,from[pgGPSTaskAssessmentCountry].to.name,from[pgPDTemplatestopgMaterialGroup].to.name,from[pgPDTemplatestopgPLIBOMBaseQuantity].to.name,from[pgPDTemplatestopgPLIReportedFunction].to.name,from[pgPDTemplatestopgPLIClass].to.name,from[pgPDTemplatestopgPLIUnitSize].to.name,from[pgToShippingHazardClasssification].to.name,from[pgPLIMaterialCertifications].to.name,from[pgIntendedMarkets].to.name,from[pgPDTemplatestopgPLISegment].to.name,from[pgDocumentToPlatform].to[pgPLIProductCategoryPlatform].name,from[pgDocumentToBusinessArea].to.name,from[pgDocumentToPlatform].to[pgPLIFranchisePlatform].name,from[pgDocumentToPlatform].to[pgPLIProductTechnologyPlatform].name,from[pgDocumentToChassis].to[pgPLIProductTechnologyChassis].name,from[pgPrimaryPackagingType].to.name";
  	public static final String FIRSTCONDITIONKEYSTRING = "from[Part Specification].to.name,from[Part Specification].to[pgStackingPattern].name,from[pgPDTemplatestopgPLIPrintingProcess].to.name,from[Region Owns].to.name,from[CoOwned].to.name,to[Classified Item].from[Part Family].name,from[pgPDTemplatestopgPLIBatteryType].to.name,from[pgPDTemplatestopgPLILifeCycleStatus].to.name,to[Originating Template].from.name,to[Affected Item].from.name,to[Change Affected Item].from.name,to[Change Affected Item].from.to[Change Action].from[Change Order].name,from[pgPDTemplatestopgBrand].to.name,from[pgGPSTaskAssessmentCountry].to.name,from[pgPDTemplatestopgMaterialGroup].to.name,from[pgPDTemplatestopgPLIBOMBaseQuantity].to.name,from[pgPDTemplatestopgPLIReportedFunction].to.name,from[pgPDTemplatestopgPLIClass].to.name,from[pgPDTemplatestopgPLIUnitSize].to.name,from[pgToShippingHazardClasssification].to.name,from[pgPLIMaterialCertifications].to.name,from[pgIntendedMarkets].to.name,from[pgPDTemplatestopgPLISegment].to.name,from[pgDocumentToPlatform].to[pgPLIProductCategoryPlatform].name,from[pgDocumentToBusinessArea].to.name,from[pgDocumentToPlatform].to[pgPLIFranchisePlatform].name,from[pgDocumentToPlatform].to[pgPLIProductTechnologyPlatform].name,from[pgDocumentToChassis].to[pgPLIProductTechnologyChassis].name,from[pgPrimaryPackagingType].to.name";
  	//Refactor 2018x.6 Ends
  	
  	public static final String ATTRIBUTE_ORGANIZATIONNAME = "attribute_OrganizationName";
  	public static final String CONS_LAST = ".last";
  	public static final String CONS_KEYAFFECTEDIMPLEMENTED = "keyAffectedImplemented";
  	public static final String CONS_OWNER = "owner";
  	public static final String CONS_ATTRIBUTENAME = "AttributeName";
  	public static final String CONS_TO_LAST_ID = "].to.last.id";
  	public static final String CONS_TO_ID = "].to.id";
  	public static final String CLASS_PGDSOCPNPRODUCTDATA	= "pgDSOCPNProductData";
  	public static final String CLASS_PGIPMPRODUCTDATA	= "pgIPMProductData";
  	public static final String CONS_OBJECTID	= "objectId";
  	public static final String CONS_PARENTRELNAME	= "parentRelName";
  	public static final String CONS_FROMMID = "frommid[";
  	public static final String CONS_REQUESTMAP = "requestMap";
  	public static final String CONS_MASTERSPECIFICATION = "Master Specification";
  	public static final String CLASS_EMXCPNPRODUCTDATA = "emxCPNProductData";
  	public static final String CONS_FILENAMES = "filenames";
  	public static final String CONS_REFERENCE_DOCUMENTS = "Reference_Documents";
  	public static final String CONS_GETDOCUMENTS = "getDocuments";
  	public static final String CONS_PGIPMTABLESJPO = "pgIPMTablesJPO";
  	public static final String CONS_IPSASSEMBLYTYPE = "IPSAssemblyType";
  	public static final String CONS_MASTER = "Master_";
  	public static final String CONS_PROPERTIES_TESTING_REQUIREMENTS = "Properties Testing Requirements";
  	public static final String CONS_MASTER_CPS = "Master_CPS";
  	public static final String CONS_MANUFACTURERCOUNTRY = "ManufacturerCountry";
  	public static final String CONN_PGMASTER = "from["+pgV3Constants.PGMASTER+"].to";
  	public static final String RELATIONSHIP_REFERENCEDOCUMENT = "relationship_ReferenceDocument";
  	public static final String ATTRIBUTE_PGPHBRAND = PropertyUtil.getSchemaProperty("attribute_pgPHBrand");
  	public static final String SELECT_ATTRIBUTE_PGPHBRAND = "attribute[" + ATTRIBUTE_PGPHBRAND + "]";
//  	public static final String ATTRIBUTE_PGPRODUCTFORM = PropertyUtil.getSchemaProperty("attribute_pgProductForm");
  	public static final String SELECT_ATTRIBUTE_PGPRODUCTFORM = "attribute[" + pgV3Constants.ATTRIBUTE_PGPRODUCTFORM + "]";
//  	public static final String ATTRIBUTE_PGPHBRAND = PropertyUtil.getSchemaProperty("attribute_pgPHBrand");
  	public static final String SELECT_ATTRIBUTE_PGSUBBRAND = "attribute[" + pgV3Constants.ATTRIBUTE_PGSUBBRAND + "]";
  	public static final String ATTRIBUTE_PGNODEID = PropertyUtil.getSchemaProperty("attribute_pgNodeId");
  	public static final String SELECT_ATTRIBUTE_PGNODEID = "attribute[" + ATTRIBUTE_PGNODEID + "]";
  	public static final String STRDANGERCATEGORY = "from[" + pgV3Constants.RELATIONSHIP_PGMASTER + "].to.last.attribute["
  			+ pgV3Constants.ATTRIBUTE_PGDANGERCATEGORY + "]";
  	public static final String STRSAFETYSYMBOL = "from[" + pgV3Constants.RELATIONSHIP_PGMASTER + "].to.last.attribute["
  			+ pgV3Constants.ATTRIBUTE_PGSAFETYSYMBOL + "]";
  	public static final String STRSHIPPINGHC = "from["+pgV3Constants.RELATIONSHIP_PGTOSHIPPINGHAZARDCLASSSIFICATION+"].to.name";
  	public static final String CONST_NAME = "Name";
  	public static final String CONST_PREVIOUS_ID = "previous.id";
  	public static final String REL_PARTSPECIFICATION_PGAPPROVEDSUPPLIERLIST = "relationship_PartSpecification,relationship_pgApprovedSupplierList";
  	
  	//Refactor getAllDataMap() Start
      public static final String CONST_PARENT_REL_NAME = pgV3Constants.RELATIONSHIP_PARTSPECIFICATION + pgPDFViewConstants.STR_COMMA + pgV3Constants.RELATIONSHIP_PGAPPROVEDSUPPLIERLIST;
      public static final String CONST_SPECIFICATIONDISPLAYTYPE = "SpecificationDisplayType";
      public static final String CONST_SPECIFICATIONSUBTYPE ="SpecificationSubType";
      public static final String CONST_SEPCIFICATIONNAME= "SpecificationName";
      public static final String CONST_PRODUCTDATA = "Product Data";
      public static final String CONST_SPECIFICATIONSAPDESC= "SpecificationSAPDescription";
      public static final String CONST_SPECIFICATIONSTATE = "SpecificationState";
      public static final String CONST_MASTERSPECIFICATIONDISPLAYTYPE= "MasterSpecificationDisplayType";
      public static final String CONST_MASTERSPECIFICATIONSUBTYPE= "MasterSpecificationSubtype";
      public static final String CONST_MASTERSEPCIFICATIONNAME= "MasterSpecificationName";
      public static final String CONST_MASTERSPECIFICATIONSAPDESC= "MasterSpecificationSAPDescription";
      public static final String CONST_MASTERSPECIFICATIONSTATE= "MasterSpecificationState";
      public static final String CONST_MASTERDOCUMENTNAME= "MasterDocumentName";
      public static final String CONST_MASTERDOCUMENTTYPE= "MasterDocumentType";
      public static final String CONST_MASTERLANG= "MasterLanguage";
      public static final String CONST_PLNAT = "Plant";
      public static final String CONST_OBJECTLIST = "objectList";
      public static final String CONST_RELATIONSHIPATTR="RelationshipAttr";
      public static final String CONST_FIELDNAME="FieldName";
      public static final String CONST_SPECIFICATIONSOURCE="SpecificationSource";
      public static final String CONST_SPECIFICATIONORIGINATOR="SpecificationOriginator";
      public static final String CONST_SPECIFICATIONAP="SpecificationAP";
      public static final String CONST_SPECIFICATIONIHT="SpecificationIHT";
      public static final String CONST_SPECIFICATIONDESCRIPTION="SpecificationDescription";
      public static final String CONST_SPECIFICATIONREVISION="SpecificationRevision";
      public static final String CONST_FRANCHISEPLATFORM="Franchise Platform";
      public static final String CONST_PGFRANCHISEPLATFORM="pgFranchisePlatform";
      public static final String CONST_FRANCHISE="Franchise";
      public static final String CONST_PRODUCT_CATEGORY_PLATFORM="Product Category Platform";
      public static final String CONST_PRODUCT_TECHNOLOGY_PLATFORM="Product Technology Platform";
      public static final String CONST_PGPRODUCTCATEGORYPLATFORM="pgProductCategoryPlatform";
      public static final String CONST_PRODUCTCATEGORYPLATFORM="ProductCategoryPlatform";
      public static final String CONST_PGPRODUCTTECHNOLOGYCHASSIS="pgProductTechnologyChassis";
      public static final String CONST_PRODUCTTECHNOLOGYPLATFORM="ProductTechnologyPlatform";
      public static final String CONST_PRODUCT_TECHNOLOGY_CHASSIS="Product Technology Chassis";
      public static final String CONST_PRODUCTTECHNOLOGYCHASSIS="ProductTechnologyChassis";
      public static final String CONST_REVISION="revision";
      public static final String CONST_TYPE="Type";
      public static final String CONST_MASTERSEPCIFICATIONMASTERPARTNAME= "MasterSpecificationMasterPartName";
      public static final String CONST_MASTERSEPCIFICATIONTYPE=  "MasterSpecificationType";
      public static final String CONST_MASTERSEPCIFICATIONTITLE= "MasterSpecificationTitle";
      public static final String CONST_MASTERSEPCIFICATIONAP= "MasterSpecificationAP";
      public static final String CONST_MASTERSEPCIFICATIONMSPN= "MasterSpecificationMSPN";
      public static final String CONST_MASTERSEPCIFICATION= "Master Specification";
      public static final String ATTRIBUTE_V_NAME = PropertyUtil.getSchemaProperty("attribute_PLMEntity.V_Name");
      public static final String SELECT_ATTRIBUTE_V_NAME = "attribute[" + ATTRIBUTE_V_NAME + "]";
      public static final String RELATIONSHIP_PROP_TESTING_REQ = PropertyUtil.getSchemaProperty("relationship_PropertiesTestingRequirements");
      public static final String CONST_TAMU = "TAMU";
      public static final String CONST_REF_DOC = "RefDocuments";
      public static final String CONST_TEST_METHOD = "TestMethod";
      public static final String SELECT_ATTRIBUTE_PGPLANTTESTINGTEXT = "attribute[" + pgV3Constants.ATTRIBUTE_PGPLANTTESTINGTEXT+ "]";
      public static final String ATTRIBUTE_ORGNIZATION_NAME = PropertyUtil.getSchemaProperty(pgPDFViewConstants.ATTRIBUTE_ORGANIZATIONNAME);
      public static final String CONST_ROH = "ROH";
      public static final String CONST_SECURITYCLASS = "DSOSecurityClass";
  	  //Refactor 2018x.6 Ends
      
    //Added by DSM-2018x.6 for PDF Views Defect #38061: Start
	  public static final String ATTRIBUTE_PNGIASSEMBLED_PNGLPDMODELTYPE = PropertyUtil.getSchemaProperty("attribute_pngiAssembled.pngLPDModelType");
	  public static final String SELECT_ATTRIBUTE_PNGIASSEMBLED_PNGLPDMODELTYPE = "attribute[" + ATTRIBUTE_PNGIASSEMBLED_PNGLPDMODELTYPE + "]";
	  //Modified by DSM-2018x.6 for PDF Views Defect #44050: Start
	  public static final String SELECT_CONN_PNGIASSEMBLED_PNGLPDMODELTYPE = "from["+pgV3Constants.RELATIONSHIP_PARTSPECIFICATION+"].to["+pgApolloConstants.TYPE_VPMREFERENCE+"]."+SELECT_ATTRIBUTE_PNGIASSEMBLED_PNGLPDMODELTYPE;
	  //Modified by DSM-2018x.6 for PDF Views Defect #44050: Ends	  
	  public static final String ATTRIBUTE_PGCOMPONENTQUANTITY = PropertyUtil.getSchemaProperty("attribute_pgComponentQuantity");
	  public static final String SELECT_ATTRIBUTE_PGCOMPONENTQUANTITY = "attribute[" + ATTRIBUTE_PGCOMPONENTQUANTITY + "]";
	  
	  public static final String ATTRIBUTE_PNGIASSEMBLED_PNGDEFINITION = PropertyUtil.getSchemaProperty("attribute_pngiAssembled.pngDefinition");
	  public static final String SELECT_ATTRIBUTE_PNGIASSEMBLED_PNGDEFINITION = "attribute[" + ATTRIBUTE_PNGIASSEMBLED_PNGDEFINITION + "]";
  	  //Modified by DSM-2018x.6 for PDF Views Defect #44050: Start
	  public static final String SELECT_CONN_PNGIASSEMBLED_PNGDEFINITION = "from["+pgV3Constants.RELATIONSHIP_PARTSPECIFICATION+"].to["+pgApolloConstants.TYPE_VPMREFERENCE+"]."+SELECT_ATTRIBUTE_PNGIASSEMBLED_PNGDEFINITION;
	  //Modified by DSM-2018x.6 for PDF Views Defect #44050: Ends
	  public static final String TYPE_PGASSEMBLEDPRODUCTPART				= "pgAssembledProductPart";
	  public static final String ATTRIBUTE_PGPRODUCTDOSEPERUSENUMERIC = PropertyUtil.getSchemaProperty("attribute_pgProductDoseperUseNumeric");
	  public static final String SELECT_ATTRIBUTE_PGPRODUCTDOSEPERUSENUMERIC = "attribute[" + ATTRIBUTE_PGPRODUCTDOSEPERUSENUMERIC + "]";
	//Added by DSM-2018x.6 for PDF Views Defect #38061: End
	//Added by DSM-2018x.6 for PDF Views Defect #39027: Start
	  public static final String ATTRIBUTE_PGCATEGORYSPECIFICS = PropertyUtil.getSchemaProperty("attribute_pgCategorySpecifics");
	  public static final String SELECT_ATTRIBUTE_PGCATEGORYSPECIFICS = "attribute[" + ATTRIBUTE_PGCATEGORYSPECIFICS + "]";
	//Added by DSM-2018x.6 for PDF Views Defect #39027: Start
	//Added by DSM-2018x.6_Oct CW for PDF Views Req #40760: Start
	  public static final String ATTRIBUTE_PGTRANSPORTATIONTEMPCONTROL = PropertyUtil.getSchemaProperty("attribute_pgTransportationTempControl");
	  public static final String SELECT_ATTRIBUTE_PGTRANSPORTATIONTEMPCONTROL = "attribute[" + ATTRIBUTE_PGTRANSPORTATIONTEMPCONTROL + "]";
	//Added by DSM-2018x.6_Oct CW for PDF Views Req #40760: Start
	//Added by DSM-2018x.6_Oct CW for PDF Views Req #41364: Start
	  public static final String ATTRIBUTE_PGPACKAGECOMPONENTOPTICALPROPERTIES = PropertyUtil.getSchemaProperty("attribute_pgPackageComponentOpticalProperties");
	  public static final String SELECT_ATTRIBUTE_PGPACKAGECOMPONENTOPTICALPROPERTIES = "attribute[" + ATTRIBUTE_PGPACKAGECOMPONENTOPTICALPROPERTIES + "]";
	  public static final String ATTRIBUTE_PGMATERIALDENSITY = PropertyUtil.getSchemaProperty("attribute_pgMaterialDensity");
	  public static final String SELECT_ATTRIBUTE_PGMATERIALDENSITY = "attribute[" + ATTRIBUTE_PGMATERIALDENSITY + "]";
	  public static final String ATTRIBUTE_PGMATERIALTHICKNESS = PropertyUtil.getSchemaProperty("attribute_pgMaterialThickness");
	  public static final String SELECT_ATTRIBUTE_PGMATERIALTHICKNESS = "attribute[" + ATTRIBUTE_PGMATERIALTHICKNESS + "]";
	  public static final String ATTRIBUTE_PGPACKAGEDURABILITY = PropertyUtil.getSchemaProperty("attribute_pgPackageDurability");
	  public static final String SELECT_ATTRIBUTE_PGPACKAGEDURABILITY = "attribute[" + ATTRIBUTE_PGPACKAGEDURABILITY + "]";
	  public static final String ATTRIBUTE_PGLABELREMOVABILITY = PropertyUtil.getSchemaProperty("attribute_pgLabelRemovability");
	  public static final String SELECT_ATTRIBUTE_PGLABELREMOVABILITY = "attribute[" + ATTRIBUTE_PGLABELREMOVABILITY + "]";
	  public static final String ATTRIBUTE_PGMINERALOILSADDEDTOPRINTINGINKS = PropertyUtil.getSchemaProperty("attribute_pgMineralOilsAddedToPrintingInks");
	  public static final String SELECT_ATTRIBUTE_PGMINERALOILSADDEDTOPRINTINGINKS = "attribute[" + ATTRIBUTE_PGMINERALOILSADDEDTOPRINTINGINKS + "]";
	  public static final String ATTRIBUTE_PGPAPERDISSOLVABILITY = PropertyUtil.getSchemaProperty("attribute_pgPaperDissolvability");
	  public static final String SELECT_ATTRIBUTE_PGPAPERDISSOLVABILITY = "attribute[" + ATTRIBUTE_PGPAPERDISSOLVABILITY + "]";
	  public static final String ATTRIBUTE_PGPAPERWETSTRENGTH = PropertyUtil.getSchemaProperty("attribute_pgPaperWetStrength");
	  public static final String SELECT_ATTRIBUTE_PGPAPERWETSTRENGTH = "attribute[" + ATTRIBUTE_PGPAPERWETSTRENGTH + "]";  
          public static final String SELECT_ATTRIBUTE_PGPAPERWETSTRENGTH_INPUTVALUE = SELECT_ATTRIBUTE_PGPAPERWETSTRENGTH+".inputvalue";
	//Added by DSM-2018x.6_Oct CW for PDF Views Req #41364: End
	//Added by DSM-2018x.6_Oct CW for PDF Views Req #41363: Start
	  public static final String ATTRIBUTE_PGINTEGRATEDLIDGLASSBOTTLEONLY = PropertyUtil.getSchemaProperty("attribute_pgIntegratedLidGlassBottleOnly");
	  public static final String SELECT_ATTRIBUTE_PGINTEGRATEDLIDGLASSBOTTLEONLY = "attribute[" + ATTRIBUTE_PGINTEGRATEDLIDGLASSBOTTLEONLY + "]";
	  public static final String ATTRIBUTE_PGDESIGNEDFORRESUSE = PropertyUtil.getSchemaProperty("attribute_pgDesignedforReUse");
	  public static final String SELECT_ATTRIBUTE_PGDESIGNEDFORRESUSE = "attribute[" + ATTRIBUTE_PGDESIGNEDFORRESUSE + "]";
	  public static final String STR_SUSTAINABILITYCOP = pgV3Constants.TYPE_PGCONSUMERUNITPART + pgPDFViewConstants.STR_COMMA +pgV3Constants.TYPE_PGMASTERCONSUMERUNITPART;
	  //Added by DSM(Sogeti) CW 2022x-05 for PDF Views (Req-48295 ) - Start
	  public static final String STR_SUSTAINABILITYPACKMATERIAL = pgV3Constants.TYPE_PACKAGINGMATERIALPART;
	  //Added by DSM(Sogeti) CW 2022x-05 for PDF Views (Req-48295 ) - End
	//Added by DSM-2018x.6_Oct CW for PDF Views Req #41363: End
	//Added by DSM(Sogeti)-2018x.6 for PDF Views Requirement #40493 - Starts
	  public static final String ATTRIBUTE_PGISATSSPECIFICTOIMPACTEDPARTORSPECREV = PropertyUtil.getSchemaProperty("attribute_pgIsATSSpecificToImpactedPartOrSpecRev");
	  public static final String SELECT_ATTRIBUTE_PGISATSSPECIFICTOIMPACTEDPARTORSPECREV = "attribute[" + ATTRIBUTE_PGISATSSPECIFICTOIMPACTEDPARTORSPECREV + "]";
	  public static final String CONST_IS_ATS_SPECIFIC_TO_IMPACTED_PART_OR_SPEC_REV = "Is ATS specific to impacted Part/Spec revision #? ";
	  //Added by DSM(Sogeti)-2018x.6 for PDF Views Requirement #40493 - Ends
          //Added by DSM Sogeti-(2018x.6 Apr CW 2022)for PDF Views Req #41366 #41367: Start
	  public static final String ATTRIBUTE_PGNSPCG = PropertyUtil.getSchemaProperty("attribute_pgNSPCG");
	  public static final String SELECT_ATTRIBUTE_PGNSPCG = "attribute[" + ATTRIBUTE_PGNSPCG + "]";
	  //Added by DSM Sogeti-(2018x.6 Apr CW 2022)for PDF Views Req #41366 #41367: Ends
	  // Added by (DSM Sogeti) for (2018x.6 Apr CW 2022) - PDF Views Req #41365,41373 Start
	 public static final String TYPE_PG_PLI_PACKAGING_MATERIAL_CERTIFICATION = PropertyUtil.getSchemaProperty("type_pgPLIPackagingMaterialCertification");	
	 public static final String RELATIONSHIP_PG_PLI_PACKAGING_CERTIFICATIONS = PropertyUtil.getSchemaProperty("relationship_pgPLIPackagingCertifications");
	 public static final String RELATIONSHIP_PG_CERTIFICATION_DOCUMENT = PropertyUtil.getSchemaProperty("relationship_pgCertificationDocument");
	 public static final String OBJECTLIST = "objectList";
	 public static final String ATTR_PG_PACKAGING_MATERIAL_CERTIFICATIONS=PropertyUtil.getSchemaProperty("attribute_pgPackagingMaterialCertifications");
	 public static final String SELECT_ATTR_PG_PACKAGING_MATERIAL_CERTIFICATIONS="attribute[" + ATTR_PG_PACKAGING_MATERIAL_CERTIFICATIONS + "]";
	 public static final String STR_LABEL_SOURCE_PART_NAME = "Source";
	 public static final String STR_LABEL_SOURCE_PART_ID = "SourceId";
	 public static final String STR_LABEL_PART_NAME = "PartName";
	 public static final String STR_LABEL_PART_TYPE = "PartType";
	 public static final String STR_LABEL_PART_ID = "PartID";
	 public static final String STR_IS_DISABLE_SELECTION = "IsDisableSelection";
	 public static final String STR_POSITION = "Position";	
	 public static final String STR_SEL_CERTIFICATION_DOC_NAME = new StringBuilder("frommid[").append(RELATIONSHIP_PG_CERTIFICATION_DOCUMENT).append("].to.").append(DomainConstants.SELECT_NAME).toString();
	 public static final String STR_SEL_CERTIFICATION_DOC_ID = new StringBuilder("frommid[").append(RELATIONSHIP_PG_CERTIFICATION_DOCUMENT).append("].to.").append(DomainConstants.SELECT_ID).toString();
	 public static final String STR_SEL_CERTIFICATION_DOC_IS_LAST = new StringBuilder("frommid[").append(RELATIONSHIP_PG_CERTIFICATION_DOCUMENT).append("].to.").append(DomainConstants.SELECT_IS_LAST).toString();
	 public static final String RANGE_VALUE_TRUE = "TRUE";
	 public static final String ATTR_EXPIRATION_DATE = PropertyUtil.getSchemaProperty("attribute_ExpirationDate");
	 public static final String SELECT_ATTR_EXPIRATION_DATE = "attribute["+ATTR_EXPIRATION_DATE+"]";
	 public static final String ATTR_COMMENTS = PropertyUtil.getSchemaProperty("attribute_Comments");
	 public static final String SELECT_ATTR_COMMENTS = "attribute["+ATTR_COMMENTS+"]";
	  //Added by (DSM Sogeti) for (2018x.6 Apr CW 2022) - PDF Views Req #41365,41373 End
	  //Added by (DSM Sogeti) for (2018x.6 Apr CW 2022) - PDF Views Def #46673 start
	  public static final String EXP_DATE_FORMAT  = "MMM dd, yyyy";
	  //Added by (DSM Sogeti) for (2018x.6 Apr CW 2022) - PDF Views Def #46673 end
	  //Added by (DSM Sogeti) for (2018x.6 May CW 2022) - PDF Views Req #42994 Starts
	  public static final String ATTRIBUTE_PGLPDMODELTYPE = PropertyUtil.getSchemaProperty("attribute_pgLPDModelType");
	  public static final String SELECT_ATTRIBUTE_PGLPDMODELTYPE = "attribute["+ATTRIBUTE_PGLPDMODELTYPE+"]";
	  public static final String ATTRIBUTE_PGLPDDEFINITION = PropertyUtil.getSchemaProperty("attribute_pgLPDDefinition");
	  public static final String SELECT_ATTRIBUTE_PGLPDDEFINITION = "attribute["+ATTRIBUTE_PGLPDDEFINITION+"]";
	  //Added by (DSM Sogeti) for (2018x.6 May CW 2022) - PDF Views Req #42994 Ends
	  //Added by (DSM Sogeti) for (2018x.6 May CW 2022) - PDF Views Req #42962,42954 Starts
	  public static final String ATTRIBUTE_PGLPDREGION = PropertyUtil.getSchemaProperty("attribute_pgLPDRegion");
	  public static final String SELECT_ATTRIBUTE_PGLPDREGION = "attribute["+ATTRIBUTE_PGLPDREGION+"]";
	  public static final String ATTRIBUTE_PGLPDSUBREGION = PropertyUtil.getSchemaProperty("attribute_pgLPDSubRegion");
	  public static final String SELECT_ATTRIBUTE_PGLPDSUBREGION = "attribute["+ATTRIBUTE_PGLPDSUBREGION+"]";
          public static final String ATTRIBUTE_PGREASONFORCHANGEMANFSTATUS = PropertyUtil.getSchemaProperty("attribute_pgReasonForChangeManfStatus");
	  public static final String SELECT_ATTRIBUTE_PGREASONFORCHANGEMANFSTATUS = "attribute["+ATTRIBUTE_PGREASONFORCHANGEMANFSTATUS+"]";
	  //Added by (DSM Sogeti) for (2018x.6 May CW 2022) - PDF Views Req #42962,42954 Ends
	  //Added by DSM(Sogeti)-2018x.6 May CW 2022 for PDF Views (Req-42227) - Ends
	  public static final String STR_LABEL_SOURCETYPE = "SourceType";
	  public static final String STR_LABEL_SOURCETITLE = "SourceTitle";
	  public static final String STR_LABEL_SOURCENAMEWITHOUTLINK = "SourceNameWithoutLink";
	  public static final String STR_LABEL_CERTNAMEWITHOUTLINK = "CertNameWithoutLink";
	  public static final String STR_LABEL_DOCNAMEWITHOUTLINK = "DocNameWithoutLink";
	  public static final String STR_LABEL_CERTCOMMENTS = "CertComments";
	  public static final String ATTRIBUTE_PGROLLUPSOURCERELTYPE = PropertyUtil.getSchemaProperty("attribute_pgRollupSourceRelType");
	  public static final String SELECT_ATTRIBUTE_PGROLLUPSOURCERELTYPE = "attribute["+ATTRIBUTE_PGROLLUPSOURCERELTYPE+"]";
	  public static final String PACKAGING_CERTIFICATION_DATE_FORMAT = "MMMM dd yyyy";
	  //Added by DSM(Sogeti)-2018x.6 May CW 2022 for PDF Views (Req-42227) - Ends
	  //Added by (DSM Sogeti) for (2018x.6 JUNE CW 2022) - PDF Views Req #43479 Starts
	  public static final String ATTRIBUTE_PGLPDREASONFORCHANGEGENERICMODEL = PropertyUtil.getSchemaProperty("attribute_pgLPDReasonForChangeGenericModel");
	  public static final String SELECT_ATTRIBUTE_PGREASONFORCHANGEGENERICMODEL  = "attribute["+ATTRIBUTE_PGLPDREASONFORCHANGEGENERICMODEL+"]";
	   //Added by (DSM Sogeti) for (2018x.6 JUNE CW 2022) - PDF Views Req #43479 Ends

	  //Added by (DSM Sogeti) for (2022x.01 Feb CW 2022) - PDF Views Req #45516,45503 and 45510 Starts
	  public static final String ATTRIBUTE_PGWNDEXCPTCMT = PropertyUtil.getSchemaProperty("attribute_pgWnDExcpCmt");
	  public static final String SELECT_ATTRIBUTE_PGWNDEXCPTCMT  = "attribute["+ATTRIBUTE_PGWNDEXCPTCMT+"]";
	  public static final String ATTRIBUTE_PGWNDEXCPSUPPORTDOC = PropertyUtil.getSchemaProperty("attribute_pgWnDExcpSupportDoc");
	  public static final String SELECT_ATTRIBUTE_PGWNDEXCPSUPPORTDOC  = "attribute["+ATTRIBUTE_PGWNDEXCPSUPPORTDOC+"]";
	  public static final String ATTRIBUTE_PGWNDVALEXCP = PropertyUtil.getSchemaProperty("attribute_pgWnDValExcp");
	  public static final String SELECT_ATTRIBUTE_PGWNDVALEXCP  = "attribute["+ATTRIBUTE_PGWNDVALEXCP+"]";
	  //Added by (DSM Sogeti) for (2022x.01 Feb CW 2022) - PDF Views Req #45516,45503 and 45510 Ends
	   
	   
	   //Added by (DSM Sogeti) for (2022x.02 May CW 2022) - PDF Views Req #46231 Starts
	   public static final String ATTRIBUTE_PGCOMPONENTPACKFAMILYLEVEL1 = PropertyUtil.getSchemaProperty("attribute_pgComponentPackFamilyLevel1");
	   public static final String SELECT_ATTRIBUTE_PGCOMPONENTPACKFAMILYLEVEL1  = "attribute["+ATTRIBUTE_PGCOMPONENTPACKFAMILYLEVEL1+"]";
	   public static final String ATTRIBUTE_PGCOMPONENTPACKFAMILYLEVEL2 = PropertyUtil.getSchemaProperty("attribute_pgComponentPackFamilyLevel2");
	   public static final String SELECT_ATTRIBUTE_PGCOMPONENTPACKFAMILYLEVEL2  = "attribute["+ATTRIBUTE_PGCOMPONENTPACKFAMILYLEVEL2+"]";
	   public static final String ATTRIBUTE_PGCOMPONENTPACKFAMILYLEVEL3 = PropertyUtil.getSchemaProperty("attribute_pgComponentPackFamilyLevel3");
	   public static final String SELECT_ATTRIBUTE_PGCOMPONENTPACKFAMILYLEVEL3  = "attribute["+ATTRIBUTE_PGCOMPONENTPACKFAMILYLEVEL3+"]";
	   //Added by (DSM Sogeti) for (2022x.02 May CW 2022) - PDF Views Req #46231 Ends
	   //Added by (DSM Sogeti) for (2022x.02 May CW 2022) - PDF Views Req #46143 Starts
	   public static final String ATTRIBUTE_PGCONSUMERPACKFAMILY = PropertyUtil.getSchemaProperty("attribute_pgConsumerPackFamily");
	   public static final String SELECT_ATTRIBUTE_PGCONSUMERPACKFAMILY  = "attribute["+ATTRIBUTE_PGCONSUMERPACKFAMILY+"]";
	   public static final String ATTRIBUTE_PGCONSUMERPRIMARYPACKAGINGTYPE = PropertyUtil.getSchemaProperty("attribute_pgConsumerPrimaryPackagingType");
	   public static final String SELECT_ATTRIBUTE_PGCONSUMERPRIMARYPACKAGINGTYPE  = "attribute["+ATTRIBUTE_PGCONSUMERPRIMARYPACKAGINGTYPE+"]";
	   public static final String ATTRIBUTE_PGCONSUMERSECONDARYPACKAGINGTYPE = PropertyUtil.getSchemaProperty("attribute_pgConsumerSecondaryPackagingType");
	   public static final String SELECT_ATTRIBUTE_PGCONSUMERSECONDARYPACKAGINGTYPE  = "attribute["+ATTRIBUTE_PGCONSUMERSECONDARYPACKAGINGTYPE+"]";	   
	   //Added by (DSM Sogeti) for (2022x.02 May CW 2022) - PDF Views Req #46143 Ends	
	   //Added by DSM(Sogeti) - for 2022x.02 May CW PDF Views Req#46172 - Starts
	   public static final String ATTRIBUTE_PGRELATIONSHIPRESTRICTION= PropertyUtil.getSchemaProperty("attribute_pgMaterialRestriction");
	   public static final String SELECT_ATTRIBUTE_PGRELATIONSHIPRESTRICTION = "attribute[" + ATTRIBUTE_PGRELATIONSHIPRESTRICTION+ "]";
	   public static final String ATTRIBUTE_PGRELATIONSHIPRESTRICTIONCOMMENTS= PropertyUtil.getSchemaProperty("attribute_pgMaterialRestrictionComment");
	   public static final String SELECT_ATTRIBUTE_PGRELATIONSHIPRESTRICTIONCOMMENTS = "attribute[" + ATTRIBUTE_PGRELATIONSHIPRESTRICTIONCOMMENTS + "]";
	   //Added by DSM(Sogeti) - for 2022x.02 May CW PDF Views Req#46172 - Ends
	   
	   //Added by DSM Sogeti for 2022x.03 Aug CW for PDF Views (Req -47318) - Start
	   public static final String ATTRIBUTE_REFERENCE_IDENTIFIER = PropertyUtil.getSchemaProperty("attribute_ReferenceIdentifier");
	   public static final String SELECT_ATTRIBUTE_REFERENCE_IDENTIFIER = "attribute[" + ATTRIBUTE_REFERENCE_IDENTIFIER + "]";
	   public static final String CONST_FIS_REFERENCE = "FIS_REFERENCE";
	   //Added by DSM Sogeti for 2022x.03 Aug CW for PDF Views (Req -47318) - End
	   //Added DSM for Requirement 47656,47667 2022x.04 Dec CW 2023 - Start
		public static final String POLICY_PGSTRUCTUREDATS = PropertyUtil.getSchemaProperty(null,"pgStructuredATS");  
		//Added DSM for Requirement 47656,47667 2022x.04 Dec CW 2023 - End	
		
		 //Added DSM for Defect 55289 2022x.04 Dec CW 2023 - Start 
		 //Modified by DSM (Sogeti) for 22x.04 (Dec CW 2023) Defect 55638 - Starts
		   public static final String EBOM_KEY  = "BOM";
		   public static final String EBOM_SUBSTITUTE_KEY  = "Substitute";
		   public static final String Structured_ATS_FORMULA_KEY  = "Formula";
		   public static final String RELATIONSHIP_PGATSCONTEXT = PropertyUtil.getSchemaProperty("relationship_pgATSContext");
		   public static final String ATTRIBUTE_PGSTRUCTUREATSACTION = PropertyUtil.getSchemaProperty(null,"attribute_pgStructuredATSAction");
		 //Modified by DSM (Sogeti) for 22x.04 (Dec CW 2023) Defect 55638 - Ends
		   //Added DSM for Defect 55289 2022x.04 Dec CW 2023 - Ends 
		
		   //Modified by DSM (Sogeti) for 22x.04 (Dec CW 2023) Defect 55877 - Starts
		   public static final String ALTERNATE_KEY  = "Alternate";
		   public static final String CHAR_ADD_OPERATION  = "add";
		   public static final String ATTRIBUTE_PGSATSCONTEXT_ID = PropertyUtil.getSchemaProperty(null,"attribute_pgSATSPCContext");
		  //Modified by DSM (Sogeti) for 22x.04 (Dec CW 2023) Defect 55877 - Ends
		   
		  //Added by DSM (Sogeti) for 22x.05 (APR CW 2024) Req 48209,48203 and 48221 - START  
		  public static final String SUSTAINABILITY_TYPE_FAB_AND_APMP = pgV3Constants.TYPE_PGANCILLARYPACKAGINGMATERIALPART + pgPDFViewConstants.STR_COMMA + pgV3Constants.TYPE_FABRICATEDPART + pgPDFViewConstants.STR_COMMA + pgV3Constants.TYPE_RAWMATERIALPART;
		  public static final String ATTRIBUTE_PG_ASSEMBLYTYPE_RANGE_PACKAGING= "Packaging";
		  //Added by DSM (Sogeti) for 22x.05 (APR CW 2024) Req 48209,48203 and 48221 - END
		  //Added by DSM for PDF Views (Req id #47506) - Start
		  public static final String ATTRIBUTE_PGSTRUCTUREDATSWHYWHYANALYSIS= PropertyUtil.getSchemaProperty("attribute_pgStructuredATSWhyWhyAnalysis");
		  public static final String SELECT_ATTRIBUTE_PGSTRUCTUREDATSWHYWHYANALYSIS = "attribute[" + pgPDFViewConstants.ATTRIBUTE_PGSTRUCTUREDATSWHYWHYANALYSIS+ "]";
		 //Added by DSM for PDF Views (Req id #47506) - End
		  //Added by DSM Sogeti for 2022x.06 CW for PDF Views (Req -49517) - Start
	  	  public static final String TYPE_PGFORMULATIONPART = "Formulation Part";
	      //Added by DSM Sogeti for 2022x.06 CW for PDF Views (Req -49517) - End
}



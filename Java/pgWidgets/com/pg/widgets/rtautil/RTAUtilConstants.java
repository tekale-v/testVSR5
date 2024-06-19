package com.pg.widgets.rtautil;

import com.custom.pg.Artwork.ArtworkConstants;
import com.matrixone.apps.awl.enumeration.AWLRel;
import com.matrixone.apps.domain.util.PropertyUtil;

public class RTAUtilConstants {
	
	static final String POA_IDs = "POAIds";
	static final String CHECK_POA_IDs = "checkPOAIds";
	static final String STR_POANAME = "POAName";
	static final String KEY_REASON = "Reason";
	static final String KEY_REASON_ID = "ResonId";
	static final String KEY_COMMENT = "Comment";
	static final String KEY_DEFECT_TYPE = "DefectType";
	static final String KEY_DEFECT_TYPE_ID = "DefectTypeId";
	static final String KEY_SUBCOPYTYPE = "Sub Copy Type";
	static final String JOB_SUCCEEDED = "Succeeded";
	static final String KEY_JOB_ID = "jobId";
	static final String KEY_JOB_STATUS = "jobStatus";
	static final String KEY_JOB_COMPLETION_STATUS = "jobCompletionStatus";
	static final String KEY_JOB_ERROR_MESSAGE = "jobErrorMessage";
	static final String VALUE_FAILED = "failed";
	static final String KEY_COUNTRIES = "countries";
	static final String KEY_INACTIVE_USERS = "inactiveUsers";
	
	static final String JSON_KEY_DATA = "data";
	static final String KEY_CONST_YES = "yes";

	public static final String ATTR_ORIGINATING_SOURCE = PropertyUtil.getSchemaProperty(null, "attribute_pgOriginatingSource");
	public static final String ATTR_VALIDITY_DATE = PropertyUtil.getSchemaProperty(null, "attribute_pgRTAValidityDate");
	public static final String ATTR_PGSIZE = PropertyUtil.getSchemaProperty(null, "attribute_pgSize");
	public static final String ATTR_PGCLAIMBRAND = PropertyUtil.getSchemaProperty(null, "attribute_pgBrand");
	public static final String ATTR_PGCLAIMSUBBRAND = PropertyUtil.getSchemaProperty(null, "attribute_pgSubBrand");
	public static final String ATTR_PGVALUECLAIM = PropertyUtil.getSchemaProperty(null, "attribute_pgValueClaim");
	public static final String ATTR_PGPRODUCTFORM = PropertyUtil.getSchemaProperty(null, "attribute_pgProductForm");
	public static final String ATTR_PGCOUNT = PropertyUtil.getSchemaProperty(null, "attribute_pgCount");
	public static final String ATTR_PGVARIANT = PropertyUtil.getSchemaProperty(null, "attribute_pgVariant");
	public static final String ATTR_PGLOTION = PropertyUtil.getSchemaProperty(null, "attribute_pgLotion");
	public static final String ATTR_PGCLAIMINTENDEDMARKETS = PropertyUtil.getSchemaProperty(null,
			"attribute_pgIntendedMarkets");
	public static final String ATTR_PGPACKCOMPONENTTYPE = PropertyUtil.getSchemaProperty(null,
			"attribute_pgPackComponentType");
	public static final String ATTR_PGPANELLOCATION = PropertyUtil.getSchemaProperty(null, "attribute_pgPanelLocation");
	public static final String ATTR_PGDISCLAIMERPANELLOCATION = PropertyUtil.getSchemaProperty(null,
			"attribute_pgDisclaimerPanelLocation");
	public static final String ATTR_PGPRODCATEPLATFORM = PropertyUtil.getSchemaProperty(null,
			"attribute_pgProductCategoryPlatform");
	public static final String ATTR_PGBUSINESSAREA = PropertyUtil.getSchemaProperty(null, "attribute_pgBusinessArea");
	public static final String ATTR_PGFRANCHISEPLATFORM = PropertyUtil.getSchemaProperty(null,
			"attribute_pgFranchisePlatform");
	public static final String ATTRIBUTE_PGEXPIRATIONDATE = PropertyUtil.getSchemaProperty(null,
			"attribute_ExpirationDate");
	public static final String ATTRIBUTE_PGRTAMCPRODUCTIONPLANT = PropertyUtil.getSchemaProperty(null,
			"attribute_pgRTAMCProductionPlant");
	public static final String ATTRIBUTE_PGRTAMCPRIMARYPRODPLANT = PropertyUtil.getSchemaProperty(null,
			"attribute_pgRTAMCPrimaryProdPlant");
	public static final String ATTR_PGEXECUTIONTYPE = PropertyUtil.getSchemaProperty(null, "attribute_pgExecutionType");
	public static final String ATTR_INSTRUCTIONS = PropertyUtil.getSchemaProperty(null, "attribute_pgAWLInstructions]");
	public static final String ATTR_REFERENCE_NO = PropertyUtil.getSchemaProperty(null, "attribute_pgAWLReferenceNo");
	public static final String ATTRIBUTE_PGRTAADDITIONALDESCRIPTION = PropertyUtil.getSchemaProperty(null,
			"attribute_pgAAA_AdditionalDescription");
	public static final String ATTRIBUTE_PGRTAGPSFIXEDCES = PropertyUtil.getSchemaProperty(null,
			"attribute_pgRTAGPSFixedCES");
	public static final String ATTRIBUTE_PGRTAGRSFIXEDRES = PropertyUtil.getSchemaProperty(null,
			"attribute_pgRTAGPSFixedRES");
	public static final String ATTRIBUTE_PGRTAADRESSCES = PropertyUtil.getSchemaProperty(null,
			"attribute_pgRTAAdressCES");
	public static final String ATTRIBUTE_PGRTASIZEBASECES = PropertyUtil.getSchemaProperty(null,
			"attribute_pgRTASizeBaseCES");
	public static final String ATTRIBUTE_PGRTAAUTOMRKCLAIMSRES = PropertyUtil.getSchemaProperty(null,
			"attribute_pgRTAAutoMRKClaimsRES");
	public static final String ATTRIBUTE_AUTOMRKCLAIMRESULTS = PropertyUtil.getSchemaProperty(null,
			"attribute_pgAutomaticMRKClaimResults");
	public static final String RELATIONSHIP_PGMASTERCOPYCLAIM = PropertyUtil.getSchemaProperty(null,
			"relationship_pgMasterCopyClaim");
	public static final String ATTR_PGRTAHARMONIZATIONPATH = PropertyUtil.getSchemaProperty(null,
			"attribute_pgRTAHarmonizationPath");
	public static final String ATTR_ISBASECOPY = PropertyUtil.getSchemaProperty(null, "attribute_IsBaseCopy");
	public static final String ATTR_PGAWLREFERENCENO = PropertyUtil.getSchemaProperty(null,
			"attribute_pgAWLReferenceNo");
	public static final String ATTR_TITLE = PropertyUtil.getSchemaProperty(null, "attribute_Title");
	public static final String ATTR_COPYTEXT = PropertyUtil.getSchemaProperty(null, "attribute_CopyText");
	public static final String ATTR_MARKETINGNAME = PropertyUtil.getSchemaProperty(null, "attribute_MarketingName");
	public static final String ATTR_COPYTEXTLANG = PropertyUtil.getSchemaProperty(null, "attribute_CopyTextLanguage");
	public static final String ATTR_ROUTESTATUS = PropertyUtil.getSchemaProperty(null, "attribute_RouteStatus");
	public static final String ATTR_ARTWORKINFO = PropertyUtil.getSchemaProperty(null, "attribute_ArtworkInfo");
	public static final String ATTRIBUTE_COMPLETION_STATUS = PropertyUtil.getSchemaProperty(null,"attribute_CompletionStatus");
	public static final String ATTRIBUTE_ERROR_MESSAGE = PropertyUtil.getSchemaProperty(null,"attribute_ErrorMessage");
	public static final String ATTRIBUTE_REPORT_INPUT_INFORMATION = "ReportInputInformation";
	
	
	public static final String SELECT_ATTRIBUTE_COMPLETION_STATUS = "attribute[" + ATTRIBUTE_COMPLETION_STATUS + "]";
	public static final String SELECT_ATTRIBUTE_ERROR_MESSAGE = "attribute[" + ATTRIBUTE_ERROR_MESSAGE + "]";
	public static final String SELECT_ATTRIBUTE_REPORT_INPUT_INFORMATION = "attribute[" + ATTRIBUTE_REPORT_INPUT_INFORMATION + "]";
	
	public static final String SELECT_PGRTAHARMONIZATIONPATH = "attribute[" + ATTR_PGRTAHARMONIZATIONPATH + "]";
	public static final String SELECT_REL_PGMASTERCOPYCLAIM_FROM_NAME = "to[" + RELATIONSHIP_PGMASTERCOPYCLAIM
			+ "].from.name";
	public static final String SELECT_PGSIZE = "attribute[" + ATTR_PGSIZE + "]";
	public static final String SELECT_PGVALUECLAIM = "attribute[" + ATTR_PGVALUECLAIM + "]";
	public static final String SELECT_PGCOUNT = "attribute[" + ATTR_PGCOUNT + "]";
	public static final String SELECT_PGVARIANT = "attribute[" + ATTR_PGVARIANT + "]";
	public static final String SELECT_PGLOTION = "attribute[" + ATTR_PGLOTION + "]";
	public static final String SELECT_PGCLAIMBRAND = "attribute[" + ATTR_PGCLAIMBRAND + "]";
	public static final String SELECT_PGBUSINESSAREA = "attribute[" + ATTR_PGBUSINESSAREA + "]";
	public static final String SELECT_PGPRODCATEPLATFORM = "attribute[" + ATTR_PGFRANCHISEPLATFORM + "]";
	public static final String SELECT_PGFRANCHISEPLATFORM = "attribute[" + ATTR_PGPRODCATEPLATFORM + "]";
	public static final String SELECT_PGEXPIRATIONDATE = "attribute[" + ATTRIBUTE_PGEXPIRATIONDATE + "]";
	public static final String SELECT_PGRTAMCPRODUCTIONPLANT = "attribute[" + ATTRIBUTE_PGRTAMCPRODUCTIONPLANT + "]";
	public static final String SELECT_PGRTAMCPRIMARYPRODPLANT = "attribute[" + ATTRIBUTE_PGRTAMCPRIMARYPRODPLANT + "]";
	public static final String SELECT_ATTR_ROUTESTATUS = "attribute[" + ATTR_ROUTESTATUS + "]";
	public static final String SELECT_ATTR_ARTWORKINFO = "attribute[" + ATTR_ARTWORKINFO + "]";
	public static final String SELECT_ATTR_AWL_INSTRUCTIONS = "attribute[" + ATTR_INSTRUCTIONS + "]";
	
	public static final String STR_ATTRIBUTE = "attribute";
	
	public static final String RELATIONSHIP_POAARTWORKMASTER = PropertyUtil.getSchemaProperty(null,
			"relationship_POAArtworkMaster");
	public static final String REL_ARTWORKELEMENTCONTENT = PropertyUtil.getSchemaProperty(null,
			"relationship_ArtworkElementContent");
	public static final String RELATIONSHIP_OBJECTROUTE = PropertyUtil.getSchemaProperty(null,
			"relationship_ObjectRoute");
	public static final String RELATIONSHIP_LANGUAGEUSED = PropertyUtil.getSchemaProperty(null,
			"relationship_LanguageUsed");
	public static final String SELECT_RELPROJECTTOPOA = "to[" + ArtworkConstants.RELATIONSHIP_PROJECT_TO_POA
			+ "].from.owner";
	public static final String SELECT_RELPROJECTTOPOANAMEID = "to[" + ArtworkConstants.RELATIONSHIP_PROJECT_TO_POA
			+ "].from.";
	public static final String SELECT_LANGUAGE_USED = "to[" + AWLRel.LANGUAGE_USED.get(null) + "].from.name";
	public static final String ATTRIBUTE_PGCOPYELEMENTTYPE = PropertyUtil.getSchemaProperty(null,
			"attribute_pgCopyElementType");
	public static final String SELECT_CLAIM_TYPE = "attribute[" + ATTRIBUTE_PGCOPYELEMENTTYPE + "].value";

	public static final String SELECT_PGCLAIMSUBBRAND = "attribute[" + ATTR_PGCLAIMSUBBRAND + "]";
	public static final String SELECT_PGPRODUCTFORM = "attribute[" + ATTR_PGPRODUCTFORM + "]";
	public static final String SELECT_PGCLAIMINTENDEDMARKETS = "attribute[" + ATTR_PGCLAIMINTENDEDMARKETS + "]";
	public static final String SELECT_PGPACKCOMPONENTTYPE = "attribute[" + ATTR_PGPACKCOMPONENTTYPE + "]";
	public static final String SELECT_PGPANELLOCATION = "attribute[" + ATTR_PGPANELLOCATION + "]";
	public static final String SELECT_PGDISCLAIMERPANELLOCATION = "attribute[" + ATTR_PGDISCLAIMERPANELLOCATION + "]";
	public static final String SELECT_PGEXECUTIONTYPE = "attribute[" + ATTR_PGEXECUTIONTYPE + "]";
	public static final String SELECT_PGRTAADDITIONALDESCRIPTION = "attribute[" + ATTRIBUTE_PGRTAADDITIONALDESCRIPTION
			+ "]";
	public static final String SELECT_PGRTAGPSFIXEDCES = "attribute[" + ATTRIBUTE_PGRTAGPSFIXEDCES + "]";
	public static final String SELECT_PGRTAGPSFIXEDRES = "attribute[" + ATTRIBUTE_PGRTAGRSFIXEDRES + "]";
	public static final String SELECT_PGRTAADRESSCES = "attribute[" + ATTRIBUTE_PGRTAADRESSCES + "]";
	public static final String SELECT_PGRTASIZEBASECES = "attribute[" + ATTRIBUTE_PGRTASIZEBASECES + "]";
	public static final String SELECT_AUTOMRKCLAIMRESULTS = "attribute[" + ATTRIBUTE_AUTOMRKCLAIMRESULTS + "]";
	public static final String SELECT_VALIDITY_DATE = "attribute[" + ATTR_VALIDITY_DATE + "]";
	public static final String SELECT_ISBASECOPY = "attribute[" + ATTR_ISBASECOPY + "]";
	public static final String SELECT_PGAWLREFNO = "attribute[" + ATTR_PGAWLREFERENCENO + "]";
	public static final String SELECT_TITLE = "attribute[" + ATTR_TITLE + "]";
	public static final String SELECT_COPYTEXT = "attribute[" + ATTR_COPYTEXT + "]";
	public static final String SELECT_MARKETINGNAME = "attribute[" + ATTR_MARKETINGNAME + "]";
	public static final String SELECT_COPYTEXTLANG = "attribute[" + ATTR_COPYTEXTLANG + "]";
	public static final String TYP_SYMBOL = PropertyUtil.getSchemaProperty(null, "type_Symbol");
	public static final String TYP_POA = PropertyUtil.getSchemaProperty(null, "type_POA");
	public static final String TYP_STRUCMASTERARTELETYPE = PropertyUtil.getSchemaProperty(null,
			"type_StructuredMasterArtworkElement");
	public static final String TYPE_ASSIGN_AUTHOR_APPROVER_TASK = PropertyUtil.getSchemaProperty(null, "type_pgAAA_AssignAuthorAndApproverTask");
	public static final String TYP_COPYELEMENT = PropertyUtil.getSchemaProperty(null, "type_CopyElement");
	public static final String TYPE_COUNTRY = PropertyUtil.getSchemaProperty(null, "type_Country");
	public static final String ATTR_PGAUTOMATEDRETRIEVCEsCHANGES = PropertyUtil.getSchemaProperty(null,
			"attribute_pgAutomatedRetrievCEsChanges");
	public static final String REL_PG_COPYLIST_CLAIM_REQUEST = PropertyUtil.getSchemaProperty(null,
			"relationship_pgCopyListClaimRequest");
	public static final String ATTR_PG_MAX_POA_DETAILS_TO_RULES_MANAGER = PropertyUtil.getSchemaProperty(null,
			"attribute_pgMaxPOADetailsToRulesManager");
	public static final String ATTR_PG_CLAIM_REQUEST_ID = PropertyUtil.getSchemaProperty(null,
			"attribute_pgClaimsRequestId");
	public static final String PERSON_USER_AGENT = PropertyUtil.getSchemaProperty(null, "person_UserAgent");
	public static final String ATTR_PG_ROBOTIC_2_COMMENT = PropertyUtil.getSchemaProperty(null,
			"attribute_pgRobotic2Comment");
	public static final String RELATIONSHIP_ARTWORK_PACKAGE_CONTENT= PropertyUtil.getSchemaProperty(null, "relationship_ArtworkPackageContent");
	
	public static final String ATTR_PG_CONFIRM_ASSIGNMENT = PropertyUtil.getSchemaProperty(null, "attribute_pgConfirmAuthorApproverAssignee");
	public static final String SELECT_ATTR_PG_CONFIRM_ASSIGNMENT = "attribute[" + ATTR_PG_CONFIRM_ASSIGNMENT + "]";
	public static final String ATTRIBUTE_PGSUBCOPYTYPE = PropertyUtil.getSchemaProperty(null,
			"attribute_pgSubCopyType");
	public static final String SELECT_ATTRIBUTE_PGSUBCOPYTYPE = "attribute[" + ATTRIBUTE_PGSUBCOPYTYPE + "]";
	
	public static final String KEY_MCE_ID = "mceId";
	public static final String KEY_LCE_IDS = "lceIds";
	public static final String KEY_LCE_ID = "lceId";
	public static final String KEY_LOCAL_BASE_COPY = "localBaseCopy";	
	public static final String KEY_AUTHOR = "Author";
	public static final String KEY_APPROVER = "Approver";
	public static final String KEY_MASTER_COPY_APPROVAL = "MasterCopyApproval";
	public static final String KEY_APPROVER_ROUTE_STATUS = "approverRouteStatus";
	public static final String KEY_MASTER_COPY_AUTHORING = "MasterCopyAuthoring";
	public static final String KEY_LOCAL_COPY_AUTHORING = "LocalCopyAuthoring";
	public static final String KEY_LOCAL_COPY_APPROVAL = "LocalCopyApproval";
	public static final String KEY_AUTHOR_ROUTE_STATUS = "authorRouteStatus";
	public static final String KEY_REV_CLAIMS_LC = "reviseClaimsLC";
	public static final String KEY_ROOT_NODE_ID = "ROOT_NODE_ID";
	
	public static final String KEY_POA_MIXED = "Mixed";
	public static final String SELECT_AUTHOR_PERSON = "from["+AWLRel.ARTWORK_CONTENT_AUTHOR.get(null)+"].to.name";
	public static final String SELECT_APPROVER_PERSON = "from["+AWLRel.ARTWORK_CONTENT_APPROVER.get(null)+"].to.name";
	public static final String SELECT_AUTHOR_FROM_TEMPLATE = "from["+AWLRel.ARTWORK_CONTENT_AUTHOR.get(null)+"].to.from[Route Node].to.name";
	public static final String SELECT_APPROVER_FROM_TEMPLATE = "from["+AWLRel.ARTWORK_CONTENT_APPROVER.get(null)+"].to.from[Route Node].to.name";

	public static final String SELECT_AUTHOR_ROUTE_STATUS = "from["+RELATIONSHIP_OBJECTROUTE+"].to."+SELECT_ATTR_ROUTESTATUS;
	public static final String SELECT_AUTHOR_ARTWORK_INFO = "from["+RELATIONSHIP_OBJECTROUTE+"].to."+SELECT_ATTR_ARTWORKINFO;
	
    public static final String KEY_IS_VALID_ORIGINATOR = "isValidOriginator";
	public static final String ATTRIBUTE_PGPLI_APPROVER_ROLE = PropertyUtil.getSchemaProperty(null, "attribute_pgPLIApproverRole");
	
	//Assign Author And Approver
	public static final String REL_ARTWORKCONTENTAUTHOR =  PropertyUtil.getSchemaProperty(null, "relationship_ArtworkContentAuthor");
	public static final String REL_ARTWORKCONTENTAPPROVER =  PropertyUtil.getSchemaProperty(null,"relationship_ArtworkContentApprover");
	public static final String ATTRIBUTE_MARKETINGNAME =  PropertyUtil.getSchemaProperty(null, "attribute_MarketingName");
	public static final String ATTRIBUTE_CELANGUAGE =  PropertyUtil.getSchemaProperty(null, "attribute_CopyTextLanguage");
	public static final String SELECT_APPROVER_ID =  "from["+ REL_ARTWORKCONTENTAPPROVER + "].to.id";
	public static final String SELECT_AUTHOR_ID =  "from["+ REL_ARTWORKCONTENTAUTHOR + "].to.id";	
	public static final String REL_CLARTWORKMASTER =  PropertyUtil.getSchemaProperty(null,"relationship_CopyListArtworkMaster") ;
	public static final String SELECT_APPROVER_NAME =  "from["+ REL_ARTWORKCONTENTAPPROVER + "].to.name";
	public static final String SELECT_AUTHOR_NAME =  "from["+ REL_ARTWORKCONTENTAUTHOR + "].to.name";
	public static final String ATTRIBUTE_ARTWORKINFO =  PropertyUtil.getSchemaProperty(null, "attribute_ArtworkInfo");
	public static final String ATTR_PG_RTA_JOB_STATUS = PropertyUtil.getSchemaProperty(null, "attribute_pgRTABGJobStatus");
	public static final String SELECT_RTA_BG_JOB_STATUS = "attribute[" + ATTR_PG_RTA_JOB_STATUS + "]";
	public static final String PRODUCT_TYPES_TOBE_SELECTED = "GPS,Address,Size Base,ShelfLife,MRK Claim";
	public static final String TYPE_PRODUCTTYPE = PropertyUtil.getSchemaProperty(null,"type_ProductType");
	
	public static final String STR_AWL_APPROVAL_RELEASE_IMG_PATH= "../awl/images/AWLApprovedForRelease.gif";
	public static final String STR_PG_AWL_REDTICK_IMG_PATH= "../awl/images/pgAWLRedTick.gif";
	public static final String STR_PG_BLUE_STATUS_IMG_PATH = "../common/images/pgCPDTaskStatusBlue.gif";
	public static final String STR_CPD_TASK_STATUS_RED_IMG_PATH= "../common/images/CPDTaskStatusRed.gif";

}

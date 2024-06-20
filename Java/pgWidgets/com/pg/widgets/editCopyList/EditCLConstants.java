package com.pg.widgets.editCopyList;

import com.custom.pg.Artwork.ArtworkConstants;
import com.matrixone.apps.awl.enumeration.AWLRel;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.PropertyUtil;

public class EditCLConstants {

	static final String POA_IDs = "POAIds";
	static final String CHECK_POA_IDs = "checkPOAIds";
	static final String STR_POANAME = "POAName";
	static final String KEY_REASON = "Reason";
	static final String KEY_COMMENT = "Comment";
	static final String KEY_DEFECT_TYPE = "DefectType";

	static final String JSON_KEY_DATA = "data";
	
	public static final String KEY_TITLE = "title";
	public static final String KEY_LAST = "last";
	public static final String KEY_FLAG = "flag";
    public static final String KEY_PARENTTYPE = "parenttype";
    public static final String KEY_CONTENT = "content";
    public static final String STR_TRANSLATE = "Translate";
    public static final String STR_NOTRANSLATE = "NoTranslate";
    public static final String STR_INLINETYPE = "InlineType";
    public static final String STR_GRAPHICTYPE = "GraphicType";
    public static final String STR_NONE = "None";
    public static final String STR_LANG = "language";
    public static final String STR_INSSEQ = "instancesequence";
    public static final String STR_ORDER = "order";
    public static final String STR_NOTES = "notes";
    public static final String STR_LANGUAGE_ID = "languageID";
    public static final String KEY_SEQ = "seq";
    public static final String KEY_COUNTRYINFO = "CountryInfo";
	public static final String KEY_LANGUAGEINFO = "LanguageInfo";
	public static final String KEY_BASELANG = "preferenceLanguage";
	public static final String KEY_BASELANGNAME = "name";
	public static final String KEY_POAHIERARCHYDATA = "POAHierachyData";
	public static final String KEY_BASELANGVALUE = "value";
	public static final String KEY_MARKETINGNAME = "marketingName";
	public static final String KEY_MASTERCOPYTEXT = "masterCopyText";
	public static final String KEY_TRANSLATE = "translate";
	public static final String KEY_INLINETRANSLATE = "inlineTranslation";
	public static final String KEY_VALIDITYDATE = "validityDate";
	public static final String KEY_INSTRUCTIONS = "instructions";
	public static final String KEY_REFERENCENUMBER = "referenceNumber";
	public static final String KEY_PLACEOFORIGIN = "placeOfOrigin";
	public static final String KEY_LISTSEP = "listSeparator";
	public static final String KEY_LISTSEQ = "listItemSequence";
	public static final String KEY_LISTITEM = "listItemId";
	public static final String KEY_LEAF = "leaf";
	public static final String KEY_SELECTED = "selected";
	public static final String KEY_EXPANDED = "expanded";
	public static final String KEY_REGIONS = "regions";
	public static final String KEY_DOCUMENTTYPE = "documentType";
	public static final String KEY_FILENAME = "filename";
	public static final String KEY_MCSURL = "mcsUrl";
	public static final String STR_ALLCOUNTRIES = "AllCountries";
	public static final String STR_MCIDS = "MCIds";
	public static final String STR_COPYLIST_ID = "CopyListID";
	public static final String REQUESTVALUEMAP = "RequestValuesMap";
	public static final String TRIGGERFAILED = "TriggerFailed";
	public static final String STR_MESSAGE = "Message";
	public static final String KEY_LANGUAGES = "Languages";
	public static final String EXCEPTION_MESSAGE = "Exception in EditCLUil";
	public static final String KEY_BRANDLEVEL = "isPOAAtBrandLevel";
	public static final String KEY_BRAND = "Brand";
	public static final String KEY_SHOWBRANDTYPE = "ShowBrandType";
	public static final String KEY_PARENT_ID = "parentId";
	public static final String KEY_NEW_COPY_TEXT = "newCopyText";
	public static final String KEY_FCS_ENABLED = "fcsEnabled";
	public static final String KEY_CL_MC_INST_SEQ = "cl_mc_instSeq";
	public static final String KEY_APPEND = "append";
	public static final String KEY_UNLOCK = "unlock";
	public static final String KEY_type = "type";
	public static final String KEY_policy = "policy";
	public static final String KEY_PARENT_REL_NAME = "parentRelName";
	public static final String KEY_NO_OF_FILES = "noOfFiles";
	public static final String KEY_OBJECT_ACTION = "objectAction";
	public static final String KEY_FORMAT = "format";
	public static final String KEY_MASTERCOPYLANG = "masterCopyLanguage";
	public static final String KEY_ISBASECOPY = "IsBaseCopy";
	public static final String KEY_MC = "MC-";
	public static final String KEY_MOTHERCHILD = "MotherChild";
	public static final String KEY_CHILDMOTHER = "ChildMother";
	public static final String KEY_MOTHER = "mother";
	public static final String KEY_CHILD = "child";
	public static final String KEY_MOTHERCHILDS = "motherChilds";
	public static final String KEY_CONTENTMISMATCH = "ContentMismatch";
	public static final String KEY_MOTHERCONTENT = "motherContent";
	public static final String COPY_MANAGER_ROOT_NODE_NAME = "PL-0000208";
	public static final String STR_ADDEXISTINGELEMENT = "addExistingElement";
	public static final String STR_ADDARTWORKELEMENT ="addArtworkElement";
	public static final String STR_CHANGEREVISION = "changeRevision";
	public static final String STR_REVISIONS_ID ="revisions.id";
	public static final String KEY_REL_ID ="relID";
	public static final String KEY_STRING = "string";
	public static final String KEY_RETURN_STRING = "returnString";
	public static final String KEY_MC_ID = "MCId";
	public static final String KEY_MC_SUBCOPYTYPE = "MCSubCopyType";
	public static final String KEY_ATTR_MAP = "attributeMap";
	public static final String KEY_CE_IDS = "ceIds";
	public static final String KEY_NEW_AUTHOR_ASSIGNEE_ID = "NewAuthorAssigneeOID";
	public static final String KEY_NEW_APPROVER_ASSIGNEE_ID = "NewApproverAssigneeOID";
	public static final String KEY_CE_ID_REASSIGN_AUTH_APPROVER = "CEIdsForReAssignAuthorApprover";
	public static final String KEY_STATUS = "status";
	public static final String KEY_COPYLIST = "copyList";
	public static final String KEY_P_ID = "pId";
	public static final String KEY_OU = "ou";
	public static final String KEY_OUID = "ouId";
	public static final String KEY_TYPEID = "typeId";
	public static final String KEY_MC_POA = "mc_poa";
	public static final String KEY_IS_VALIDITY_DATE_EXPIRED = "isValidityDateExpired";
	

	public static final String ATTR_ORIGINATING_SOURCE = PropertyUtil.getSchemaProperty(null,
			"attribute_pgOriginatingSource");
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

	public static final String STR_ATTRIBUTE = "attribute";

	public static final String RELATIONSHIP_POAARTWORKMASTER = PropertyUtil.getSchemaProperty(null,
			"relationship_POAArtworkMaster");
	public static final String RELATIONSHIP_PG_RTA_COPYLIST_CLONED_FROM = PropertyUtil.getSchemaProperty(null,
			"relationship_pgRTACopyListClonedFrom");
	public static final String REL_ARTWORKELEMENTCONTENT = PropertyUtil.getSchemaProperty(null,
			"relationship_ArtworkElementContent");
	public static final String RELATIONSHIP_OBJECTROUTE = PropertyUtil.getSchemaProperty(null,
			"relationship_ObjectRoute");
	public static final String SELECT_RELPROJECTTOPOA = "to[" + ArtworkConstants.RELATIONSHIP_PROJECT_TO_POA
			+ "].from.owner";
	public static final String SELECT_RELPROJECTTOPOANAMEID = "to[" + ArtworkConstants.RELATIONSHIP_PROJECT_TO_POA
			+ "].from.";
	public static final String ATTRIBUTE_PGCOPYELEMENTTYPE = PropertyUtil.getSchemaProperty(null,
			"attribute_pgCopyElementType");
	public static final String SELECT_CLAIM_TYPE = "attribute[" + ATTRIBUTE_PGCOPYELEMENTTYPE + "].value";
	public static final String RELATIONSHIP_COPYLISTCOUNTRY = PropertyUtil.getSchemaProperty(null,
			"relationship_CopyListCountry");

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
	public static final String TYPE_ASSIGN_AUTHOR_APPROVER_TASK = PropertyUtil.getSchemaProperty(null,
			"type_pgAAA_AssignAuthorAndApproverTask");
	public static final String TYP_COPYELEMENT = PropertyUtil.getSchemaProperty(null, "type_CopyElement");
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

	public static final String ATTR_PG_CONFIRM_ASSIGNMENT = PropertyUtil.getSchemaProperty(null,
			"attribute_pgConfirmAuthorApproverAssignee");
	public static final String SELECT_ATTR_PG_CONFIRM_ASSIGNMENT = "attribute[" + ATTR_PG_CONFIRM_ASSIGNMENT + "]";

	public static final String KEY_MCE_ID = "mceId";
	public static final String KEY_LCE_IDS = "lceIds";
	public static final String KEY_AUTHOR = "Author";
	public static final String KEY_APPROVER = "Approver";
	public static final String KEY_MASTER_COPY_APPROVAL = "MasterCopyApproval";
	public static final String KEY_APPROVER_ROUTE_STATUS = "approverRouteStatus";
	public static final String KEY_MASTER_COPY_AUTHORING = "MasterCopyAuthoring";
	public static final String KEY_AUTHOR_ROUTE_STATUS = "authorRouteStatus";

	public static final String KEY_POA_MIXED = "Mixed";
	public static final String SELECT_AUTHOR_PERSON = "from[" + AWLRel.ARTWORK_CONTENT_AUTHOR.get(null) + "].to.name";
	public static final String SELECT_APPROVER_PERSON = "from[" + AWLRel.ARTWORK_CONTENT_APPROVER.get(null)
			+ "].to.name";
	public static final String SELECT_AUTHOR_FROM_TEMPLATE = "from[" + AWLRel.ARTWORK_CONTENT_AUTHOR.get(null)
			+ "].to.from[Route Node].to.name";
	public static final String SELECT_APPROVER_FROM_TEMPLATE = "from[" + AWLRel.ARTWORK_CONTENT_APPROVER.get(null)
			+ "].to.from[Route Node].to.name";

	public static final String SELECT_AUTHOR_ROUTE_STATUS = "from[" + RELATIONSHIP_OBJECTROUTE + "].to."
			+ SELECT_ATTR_ROUTESTATUS;
	public static final String SELECT_AUTHOR_ARTWORK_INFO = "from[" + RELATIONSHIP_OBJECTROUTE + "].to."
			+ SELECT_ATTR_ARTWORKINFO;

	public static final String KEY_IS_VALID_ORIGINATOR = "isValidOriginator";
	public static final String COPYLIST_ID = "CopyListId";
	static final String KEY_LCES = "LCEs";
	static final String MC_IDS = "MCIds";
	static final String MODE = "mode";
	public static final String ATTRIBUTE_PGRTACECATEGORY = PropertyUtil.getSchemaProperty(null,
			"attribute_pgRTACECategory");
	public static final String ATTRIBUTE_PGRTACEBRAND = PropertyUtil.getSchemaProperty(null, "attribute_pgRTACEBrand");
	public static final String ATTRIBUTE_PGRTACLASSIFICATION = PropertyUtil.getSchemaProperty(null,
			"attribute_pgRTAClassification");
	public static final String ATTRIBUTE_PGRTAPLANTCODE = PropertyUtil.getSchemaProperty(null,
			"attribute_pgRTAPlantCode");
	public static final String ATTRIBUTE_PGRTACOPYELEMENTCATEGORY = PropertyUtil.getSchemaProperty(null,
			"attribute_pgRTACopyElementCategory");
	public static final String ATTRIBUTE_PGRTACOPYELEMENTTYPE = PropertyUtil.getSchemaProperty(null,
			"attribute_pgRTACopyElementType");
	public static final String ATTRIBUTE_PGRTADESCRIPTION = PropertyUtil.getSchemaProperty(null,
			"attribute_pgRTADescription");
	public static final String ATTRIBUTE_PGRTAFIXEDORVARIABLE = PropertyUtil.getSchemaProperty(null,
			"attribute_pgRTAFixedorVariable");
	public static final String ATTRIBUTE_PGSUBCOPYTYPE = PropertyUtil.getSchemaProperty(null,
			"attribute_pgSubCopyType");
	public static final String ATTRIBUTE_PGRTACECOUNTRY = PropertyUtil.getSchemaProperty(null,
			"attribute_pgRTACECountry");
	public static final String ATTRIBUTE_PGRTAEUCLASSIFICATION = PropertyUtil.getSchemaProperty(null,
			"attribute_pgRTAEuClassification");
	public static final String ATTRIBUTE_PGRTADISTRIBTYPE = PropertyUtil.getSchemaProperty(null,
			"attribute_pgRTADistribType");
	public static final String ATTRIBUTE_PGRTACECLWARNING = PropertyUtil.getSchemaProperty(null,
			"attribute_pgRTACECLPWarning");
	public static final String ATTRIBUTE_PGRTAMCSIZE = PropertyUtil.getSchemaProperty(null,
			"attribute_pgRTAMCSize");
	public static final String ATTRIBUTE_PGRTAMCCUCODATA = PropertyUtil.getSchemaProperty(null,
			"attribute_pgRTAMCCuCoData");
	public static final String ATTRIBUTE_PGRTACECROSSSELL = PropertyUtil.getSchemaProperty(null,
			"attribute_pgRTACECrossSell");
	public static final String ATTRIBUTE_PGRTACECONSUMERBENEFITONE = PropertyUtil.getSchemaProperty(null,
			"attribute_pgRTACEConsumerBenefitOne");
	public static final String ATTRIBUTE_PGRTACECONSUMERBENEFITTWO = PropertyUtil.getSchemaProperty(null,
			"attribute_pgRTACEConsumerBenefitTwo");
	public static final String ATTRIBUTE_PGRTACECONSUMERBENEFITTHREE = PropertyUtil.getSchemaProperty(null,
			"attribute_pgRTACEConsumerBenefitThree");
	public static final String ATTRIBUTE_PGRTAMCPACKAGINGCOMPONENTTYPE = PropertyUtil.getSchemaProperty(null,
			"attribute_pgRTAMCPackagingComponentType");
	public static final String ATTRIBUTE_PGRTACEPRODUCTFORM = PropertyUtil.getSchemaProperty(null,
			"attribute_pgRTACEProductForm");
	public static final String ATTRIBUTE_PGConfirmAuthorApproverAssignee = PropertyUtil.getSchemaProperty(null,
			"attribute_pgConfirmAuthorApproverAssignee");
	public static final String KEY_PGRTACECATEGORY = "pgRTACECategory";
	public static final String KEY_PGRTACEBRAND = "pgRTACEBrand";
	public static final String KEY_PGRTACLASSIFICATION = "pgRTAClassification";
	public static final String KEY_PGRTAPLANTCODE = "pgRTAPlantCode";
	public static final String KEY_PGRTACOPYELEMENTCATEGORY = "pgRTACopyElementCategory";
	public static final String KEY_PGRTACOPYELEMENTTYPE = "pgRTACopyElementType";
	public static final String KEY_PGRTADESCRIPTION = "pgRTADescription";
	public static final String KEY_PGRTAFIXEDORVARIABLE = "pgRTAFixedorVariable";
	public static final String KEY_PGConfirmAuthorApproverAssignee = "pgConfirmAuthorApproverAssignee";
	public static final String KEY_PGSUBCOPYTYPE = "pgSubCopyType";
	public static final String KEY_PGRTACECOUNTRY = "pgRTACECountry";
	public static final String KEY_PGRTAEUCLASSIFICATION = "pgRTAEuClassification";
	public static final String KEY_PGRTADISTRIBTYPE = "pgRTADistribType";
	
	public static final String ATTRIBUTE_PGINSTANCE_SEQUENCE = "Instance Sequence";
	

	public static final String TYPE_PGPLICOPYELEMENTTYPE = PropertyUtil.getSchemaProperty(null, "type_pgPLICopyElementType");
	public static final String TYPE_PGPLISUBCOPYTYPE = PropertyUtil.getSchemaProperty(null, "type_pgPLISubCopyType");
	public static final String RELATIONSHIP_PGPLCOPYELEMENTTOSUBCOPY = PropertyUtil
			.getSchemaProperty(null, "relationship_pgPLCopyElementToSubCopy");
	public static final String CURRENT_EQUALS_ACTIVE = "current == Active";
	
	public static final String ATTRIBUTE_PGLANGMOTHERCHILDINFO = PropertyUtil.getSchemaProperty(null, "attribute_pgLangMotherChildInfo");
	public static final String SEL_ATTRIBUTE_PGLANGMOTHERCHILDINFO = DomainObject.getAttributeSelect(PropertyUtil.getSchemaProperty(null, "attribute_pgLangMotherChildInfo"));
	public static final String KEY_LOCAL_BASE_COPY_LANGUAGE = "localBaseCopyLanguage";
	public static final String SEL_ATTR_SCHEDULED_COMPLETION_DATE = DomainObject.getAttributeSelect(DomainConstants.ATTRIBUTE_SCHEDULED_COMPLETION_DATE);
	public static final String SEL_ATTR_ACTUAL_COMPLETION_DATE = DomainObject.getAttributeSelect(DomainConstants.ATTRIBUTE_ACTUAL_COMPLETION_DATE);
	public static final String strBGSubmittedPref = "Submitted : ";
	public static final String PHYSICALID_CONNECTION = "physicalid[connection]";
	
	public static final String ATTRIBUTE_PGRTALCADDITIONALIFCLUSTER = PropertyUtil.getSchemaProperty(null, "attribute_pgRTALCAdditionalIfCluster");
	public static final String SEL_ATTRIBUTE_PGRTALCADDITIONALIFCLUSTER = DomainObject.getAttributeSelect(ATTRIBUTE_PGRTALCADDITIONALIFCLUSTER);
	
	public static final String ATTRIBUTE_PGRTAMCCOMMENT = PropertyUtil.getSchemaProperty(null, "attribute_pgRTAMCComment");
	public static final String SEL_ATTRIBUTE_PGRTAMCCOMMENT = DomainObject.getAttributeSelect(ATTRIBUTE_PGRTAMCCOMMENT);

	public static final String KEY_OUTYPE = "outype";
	
}

package com.pg.artwork.claims;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.PropertyUtil;

public interface PGRTADCMIntegrationConstants extends DomainConstants
{
	public static final String STR_TITLE_RTADCM = "RTADCMIntegration";
	public static final String RELATIONSHIP_PGBGRELATEDCLAIMREQUESTS =PropertyUtil.getSchemaProperty("relationship_pgBGRelatedClaimRequests");
	public static final String RELATIONSHIP_PGDISCLAIMER = PropertyUtil.getSchemaProperty("relationship_pgDisclaimer");
	public static final String STR_INTEGRATION_USER ="dcm2rta.im";
	public static final String STR_USER_AGENT ="User Agent";
	public static final String STR_PG_COMPANY="PG";
	public static final String STR_HYPHEN="-";
	public static final String CDM_ADMIN="PL-0000208";
	public static final String VALUE_RESTRICTED = "Restricted";
	public static final String STR_FILE_PATH = "/var/opt/gplm/scripts/workdir/gplm458_DCMRTAIntegration";
	public static final String STR_FILE_NAME="ClaimStatus";
	public static final String TYPE_SYMBOL = PropertyUtil.getSchemaProperty("type_Symbol");
	public static final String ATTR_PG_ORIGINATINGSOURCE = PropertyUtil.getSchemaProperty("attribute_pgOriginatingSource");
	public static final String ATTR_IPCLASSIFICATION = PropertyUtil.getSchemaProperty("attribute_pgIPClassification");
	public static final String TYPE_PGCLAIMREQUEST = PropertyUtil.getSchemaProperty("type_pgClaimRequest");
	public static final String TYPE_PGCLAIM = PropertyUtil.getSchemaProperty("type_pgClaim");
	public static final String TYPE_PGDISCLAIMER = PropertyUtil.getSchemaProperty("type_pgDisclaimer");
	public static final String REL_PGCLAIMS = PropertyUtil.getSchemaProperty("relationship_pgClaims");
	public static final String RELATIONSHIP_PGMASTERCOPYCLAIM = PropertyUtil.getSchemaProperty("relationship_pgMasterCopyClaim");
	public static final String RELATIONSHIP_PGCOPYLISTCLAIMREQUEST = PropertyUtil.getSchemaProperty("relationship_pgCopyListClaimRequest");
	public static final String ATTRIBUTE_PGSIZE	=	PropertyUtil.getSchemaProperty("attribute_pgSize");
	public static final String ATTRIBUTE_PGCLAIMIMAGE	=	PropertyUtil.getSchemaProperty("attribute_pgClaimImage");
	public static final String ATTRIBUTE_PGSUBBRAND	=	PropertyUtil.getSchemaProperty("attribute_pgClaimSubBrand");
	public static final String ATTRIBUTE_PGTRADEMARKCLEARANCECOMPLETE	=	PropertyUtil.getSchemaProperty("attribute_pgTrademarkClearanceComplete");
	public static final String ATTRIBUTE_PGCLAIMNAME	=	PropertyUtil.getSchemaProperty("attribute_pgClaimName");
	public static final String ATTRIBUTE_PGCLAIMGRAPHICALIMAGE	=	PropertyUtil.getSchemaProperty("attribute_pgClaimGraphicalImage");
	public static final String ATTRIBUTE_PGVALUECLAIM	=	PropertyUtil.getSchemaProperty("attribute_pgValueClaim");
	public static final String ATTRIBUTE_PGPANELLOCATION	=	PropertyUtil.getSchemaProperty("attribute_pgPanelLocation");
	public static final String ATTRIBUTE_PGPACKTYPE	=	PropertyUtil.getSchemaProperty("attribute_pgPackType");
	public static final String ATTRIBUTE_PGCONSUMERTESTING	=	PropertyUtil.getSchemaProperty("attribute_pgConsumerTesting");
	public static final String ATTRIBUTE_PGPACKCOMPONENTTYPE	=	PropertyUtil.getSchemaProperty("attribute_pgPackComponentType");
	public static final String ATTRIBUTE_PGLEGALRISKASSESSMENT	=	PropertyUtil.getSchemaProperty("attribute_pgLegalRiskAssessment");
	public static final String ATTRIBUTE_PGCOPYELEMENTTYPE	=	PropertyUtil.getSchemaProperty("attribute_pgCopyElementType");
	public static final String ATTRIBUTE_PGMARKETINGCOMMENTS	=	PropertyUtil.getSchemaProperty("attribute_pgMarketingComments");
	public static final String ATTRIBUTE_PGGPSCOMMENTS	=	PropertyUtil.getSchemaProperty("attribute_pgGPSComments");
	public static final String ATTRIBUTE_PGPRCOMMENTS	=	PropertyUtil.getSchemaProperty("attribute_pgPRComments");
	public static final String ATTRIBUTE_PGPRODUCTFORM	=	PropertyUtil.getSchemaProperty("attribute_pgProductForm");
	public static final String ATTRIBUTE_PGNEWART	=	PropertyUtil.getSchemaProperty("attribute_pgNewArt");
	public static final String ATTRIBUTE_PGDISCLAIMER	=	PropertyUtil.getSchemaProperty("attribute_pgDisclaimerName");
	public static final String ATTRIBUTE_PGINTENDEDMARKETS	=	PropertyUtil.getSchemaProperty("attribute_pgClaimIntendedMarkets");
	public static final String ATTRIBUTE_PGSAFETYCOMMENT	=	PropertyUtil.getSchemaProperty("attribute_pgSafetyComment");
	public static final String ATTRIBUTE_PGCOUNT	=	PropertyUtil.getSchemaProperty("attribute_pgCount");
	public static final String ATTRIBUTE_PGCOMMSCOMMENT	=	PropertyUtil.getSchemaProperty("attribute_pgCOMMSComment");
	public static final String ATTRIBUTE_PGPACKDEVCOMMENTS	=	PropertyUtil.getSchemaProperty("attribute_pgPackDevComments");
	public static final String ATTRIBUTE_PGCLINICALCOMMENT	=	PropertyUtil.getSchemaProperty("attribute_pgClinicalComment");
	public static final String ATTRIBUTE_PGBRAND	=	PropertyUtil.getSchemaProperty("attribute_pgClaimBrand");
	public static final String ATTRIBUTE_PGLOTION	=	PropertyUtil.getSchemaProperty("attribute_pgLotion");
	public static final String ATTRIBUTE_PGVARIANT	=	PropertyUtil.getSchemaProperty("attribute_pgVariant");
	public static final String ATTRIBUTE_PGEXECUTIONTYPE	=	PropertyUtil.getSchemaProperty("attribute_pgExecutionType");
	//RTA 22x Added for ALM-49635 starts
	public static final String ATTRIBUTE_CLAIMSEQUENCE	=	PropertyUtil.getSchemaProperty("attribute_pgClaimSequence");
	//RTA 22x Added for ALM-49635 ends
	//RTA 22x Added for ALM-46415 starts
	public static final String ATTRIBUTE_PGEXPIRATIONDATE	=	PropertyUtil.getSchemaProperty("attribute_ExpirationDate");
	public static final String ATTRIBUTE_VALIDATE_DATE = PropertyUtil.getSchemaProperty("attribute_pgRTAValidityDate");
	//RTA 22x Added for ALM-46415 ends
	public static final String REL_PGCLAIMRELATEDPART	=	PropertyUtil.getSchemaProperty("relationship_pgClaimRelatedPart");	 
	public static final String ATTRIBUTE_PGTHIRDPARTYENDORSEMENTCLEARANCECOMPLETE	=	PropertyUtil.getSchemaProperty("attribute_pgThirdPartyEndorsementClearanceComplete");
	public static final String POLICY_COPYLIST =  PropertyUtil.getSchemaProperty("policy_CopyList") ;
	public static final String CL_STATE_RELEASE = PropertyUtil.getSchemaProperty("Policy", POLICY_COPYLIST, "state_Release");
	public static final String REL_PGCOPYLIST = PropertyUtil.getSchemaProperty("relationship_pgCopyListClaimRequest") ;
	public static final String SEL_REL_COPYLIST = "to[" + REL_PGCOPYLIST + "].from.name";
	public static final String TYPE_COPYLIST =  PropertyUtil.getSchemaProperty("type_CopyList") ;	 
	public static final String SELECT_REL_PGMASTERCOPYCLAIM_FROM_NAME = "to[" + RELATIONSHIP_PGMASTERCOPYCLAIM + "].from.name";
	public static final String REL_CONTENTLANG = PropertyUtil.getSchemaProperty("relationship_ContentLanguage");
	public static final String SELECT_REL_CONTENTLANG_FROM_NAME = "to[" + REL_CONTENTLANG + "].from.name";
	public static final String POLICY_ARTWORKELEMENT =  PropertyUtil.getSchemaProperty("policy_ArtworkElement") ;
	public static final String REL_CLARTWORKMASTER =  PropertyUtil.getSchemaProperty("relationship_CopyListArtworkMaster") ;
	public static final String REL_ARTWORKELEMENTCONTENT =  PropertyUtil.getSchemaProperty("relationship_ArtworkElementContent") ;
	
	public static final String TYPE_IPCONTROLCLASS          = PropertyUtil.getSchemaProperty("type_IPControlClass");
	public static final String ATTRIBUTE_PGCLAIMACECONTACT  = PropertyUtil.getSchemaProperty("attribute_pgClaimACEContact");
	public static final String ATTRIBUTE_PGCATEGORY         = PropertyUtil.getSchemaProperty("attribute_pgClaimCategory");	 
	//RTA 22x Added for ALM-51831 starts
	public static final String ATTRIBUTE_PGBUSINESSAREA         = PropertyUtil.getSchemaProperty("attribute_pgBusinessArea");	
	//RTA 22x Added for ALM-51831 ends
	//public static final String RELATIONSHIP_CLASSIFIEDITEM  = DomainConstants.RELATIONSHIP_PROTECTED_ITEM;
	public static final String RELATIONSHIP_COMPANYPRODUCT  = PropertyUtil.getSchemaProperty("relationship_CompanyProduct") ;
	public static final String RELATIONSHIP_ASSOCIATEDCOPYLIST    = PropertyUtil.getSchemaProperty("relationship_AssociatedCopyList");
	public static final String RELATIONSHIP_ASSOCIATEDCOPYLISTHISTORY    = PropertyUtil.getSchemaProperty("relationship_AssociatedCopyListHistory");
	public static final String RELATIONSHIP_ARTWORKMASTER     = PropertyUtil.getSchemaProperty("relationship_ArtworkMaster");
	public static final String RELATIONSHIP_ARTWORKASSEMBLY   = PropertyUtil.getSchemaProperty("relationship_ArtworkAssembly");
	
	public static final String RELATIONSHIP_OBJECTROUTE =  PropertyUtil.getSchemaProperty("relationship_ObjectRoute");
	public static final String RELATIONSHIP_ROUTETASK   =  PropertyUtil.getSchemaProperty("relationship_RouteTask");
	
	
	public static final String STRRTAEUROPEREGION = "EUROPE";
	public static final String STRDCMEUROPEREGION = "EUROPE ENTERPRISE";
	public static final String STRDCMEUROPEFOCUSREGION = "EUROPE FOCUS";
	public static final String STRRTANAREGION = "NA";
	public static final String STRDCMNAREGION = "NORTH AMERICA";
	public static final String STRRTALAREGION = "LA";
	public static final String STRDCMLAREGION = "LATIN AMERICA";
	
	public static final String STRDCM             = "DCM";
	public static final String MCEID              = "MCEID";
	public static final String LCID              = "LCID";
	public static final String ESERPROD          = "eService Production";
	public static final String STR_YES            = "Yes";
	public static final String STRCOPYTEXTRTE = "Copy Text_RTE";
	public static final String ATTRIBUTE_ARTWORKINFO   =  PropertyUtil.getSchemaProperty("attribute_ArtworkInfo");
	public static final String ATTRIBUTE_TITLE   =  PropertyUtil.getSchemaProperty("attribute_Title");
	public static final String ATTRIBUTE_ROUTEINSTRUCTIONS   =  PropertyUtil.getSchemaProperty("attribute_RouteInstructions");	 
	public static final String TYPE_PERSON   =  PropertyUtil.getSchemaProperty("type_Person");
	public static final String ATTRIBUTE_PLACEOFORIGIN = PropertyUtil.getSchemaProperty("attribute_PlaceOfOrigin");
	public static final String ATTRIBUTE_COPYTEXTLANGUAGE = PropertyUtil.getSchemaProperty("attribute_CopyTextLanguage");
	public static final String ATTRIBUTE_COPYTEXT = PropertyUtil.getSchemaProperty("attribute_CopyText");	 
	
	public static final String RELATIONSHIP_ARTWORKCONTENTAPPROVER = PropertyUtil.getSchemaProperty("relationship_ArtworkContentApprover");
	public static final String RELATIONSHIP_ARTWORKCONTENTAUTHOR   = PropertyUtil.getSchemaProperty("relationship_ArtworkContentAuthor");
	
	public static final String RELATIONSHIP_POAARTWORKMASTER   = PropertyUtil.getSchemaProperty("relationship_POAArtworkMaster");	 
	public static final String ATTRIBUTE_MARKETINGNAME   = PropertyUtil.getSchemaProperty("attribute_MarketingName");
	public static final String TYPE_IMAGEHOLDER   = PropertyUtil.getSchemaProperty("type_ImageHolder");
	public static final String ATTRIBUTE_PGCONFIRMAUTHORAPPROVERASSIGNEE = PropertyUtil.getSchemaProperty("attribute_pgConfirmAuthorApproverAssignee");
	public static final  String REVIEWED ="REVIEWED";

	public static final String RELATIONSHIP_COPYLISTARTWORKMASTER = PropertyUtil.getSchemaProperty("relationship_CopyListArtworkMaster");
	public static final String ATTRIBUTE_PGCLAIMNAME_RTE          = PropertyUtil.getSchemaProperty("attribute_pgClaimName_RTE");
	public static final String ATTRIBUTE_PGDISCLAIMER_RTE         = PropertyUtil.getSchemaProperty("attribute_pgDisclaimerName_RTE");
	public static final String ATTRIBUTE_REGION         = PropertyUtil.getSchemaProperty("attribute_pgClaimRegion");
	public static final String TYPE_PGPLIPACKCOMPONENTTYPE = PropertyUtil.getSchemaProperty("type_pgPLIPackComponentType");
	public static final String POLICY_PGPICKLISTITEM = PropertyUtil.getSchemaProperty("policy_pgPicklistItem");
	public static final String PGPLIPACKCOMPONENTTYPE_STATE_ACTIVE = PropertyUtil.getSchemaProperty("Policy", POLICY_PGPICKLISTITEM, "state_Active");
	public static final String ATTRIBUTE_COPYTEXTRTE = PropertyUtil.getSchemaProperty("attribute_CopyText_RTE");

	public static final String CONSTANT_STRING_COMMA = ",";
	public static final String STRING_COMMA = "(comma)";
	public static final String CONSTANT_STRING_PIPE = "|";
	public static final String STRING_PIPE = "(v bar)";
	public static final String CONSTANT_STRING_SPACE_PIPE_SPACE = " | ";
	public static final String CONSTANT_STRING_COLON = ":";
	public static final String STRING_COLON = "(colon)";
	public static final String CONSTANT_STRING_HYPHEN  = "-";
	public static final String CONSTANT_STRING_UNDERSCORE  = "_";
	public static final String CONSTANT_STRING_DOT = ".";
	public static final String CONSTANT_STRING_AMPERSAND = "&";
	public static final String STRING_AMPERSAND = "(and)";
	public static final String CONSTANT_STRING_SPACE = " ";
	public static final String CONSTANT_STRING_EQUAL_SIGN = "=";
	public static final String STRING_EQUAL_SIGN = "(equal)";
	public static final String CONSTANT_STRING_FIVE = "5";
	public static final String CONSTANT_STRING_HASH_AT_HASH = "#@#";
	public static final String CONSTANT_STRING_OPEN_ROUND_BRACE = "(";
	public static final String CONSTANT_STRING_CLOSE_ROUND_BRACE = ")";
	public static final String CONSTANT_STRING_DOUBLE_EQUAL = "==";
	public static final String CONSTANT_STRING_NOT_EQUAL = "!=";
	public static final String CONSTANT_STRING_NEGATE = "!";
	public static final String STRING_NEGATE = "(exl)";
	public static final String CONSTANT_STRING_DOUBLE_AMPERSAND = "&&";
	public static final String CONSTANT_STRING_DOUBLE_PIPE = "||";
	public static final String CONSTANT_STRING_SINGLE_QUOTE = "'";
	public static final String STRING_SINGLE_QUOTE = "(aps)";
	public static final String CONSTANT_STRING_NEWLINE = "\n";
	public static final String CONSTANT_STRING_HASH = "#";
	public static final String STRING_HASH ="(hash)";
	public static final String CONSTANT_STRING_THREE_HASH = "###";
	public static final String CONSTANT_STRING_THREE_COLON = ":::";
	
	public static final String STRTILDE= "~";
	public static final String STRING_TILDE="(tilde)";
	public static final String CONSTANT_STRING_STAR = "*";
	public static final String STRING_STAR ="(ast)";
	public static final String CONSTANT_STRING_DOLLAR = "$";
	public static final String STRING_DOLLAR ="(dollar)";
	public static final String CONSTANT_STRING_CARET = "^";
	public static final String STRING_CARET ="(caret)";
	public static final String CONSTANT_STRING_SLASH = "/";
	public static final String STRING_SLASH = "(slash)";
	public static final String CONSTANT_STRING_PLUS = "+";
	public static final String STRING_PLUS = "(plus)";
	public static final String CONSTANT_STRING_LESSTHAN = "<";
	public static final String STRING_LESSTHAN = "(less than)";
	public static final String CONSTANT_STRING_GREATERTHAN = ">";
	public static final String STRING_GREATERTHAN = "(greater than)";
	public static final String CONSTANT_STRING_SEMICOLON = ";";
	public static final String STRING_SEMICOLON = "(semicolon)";
	public static final String CONSTANT_STRING_DOUUBLEQUOTE = "\"";
	public static final String STRING_DOUUBLEQUOTE = "(quote)";
	public static final String CONSTANT_STRING_QUEST = "?";
	public static final String STRING_QUEST = "(quest)";
	public static final String CONSTANT_STRING_PERCENT = "%";
	public static final String STRING_PERCENT = "(percent)";
	public static final String CONSTANT_STRING_BSLASH = "\\";
	public static final String STRING_BSLASH = "(b slash)";
	public static final String CONSTANT_STRING_AT = "@";
	public static final String STRING_AT = "(at)";
	public static final String CONSTANT_STRING_OPENSQUAREBRACKET = "[";
	public static final String CONSTANT_STRING_CLOSESQUAREBRACKET = "]";
	public static final String STRING_SQUAREBRACKET = "(brackets)";
	
	public static final String CONSTANT_STRING_SELECT_FROM = "from[";
	public static final String CONSTANT_STRING_SELECT_FROMMID = "frommid[";
	public static final String CONSTANT_STRING_SELECT_TO = "to[";
	public static final String CONSTANT_STRING_SELECT_CLOSE_BRACKET = "]";
	public static final String CONSTANT_STRING_SELECT_TOID = "].to.id";
	public static final String CONSTANT_STRING_SELECT_TONAME = "].to.name";
	public static final String CONSTANT_STRING_SELECT_FROMID = "].from.id";
	public static final String CONSTANT_STRING_SELECT_FROMNAME = "].from.name";
	public static final String CONSTANT_STRING_SELECT_RELID = "].id";	
	public static final String CONSTANT_STRING_SELECT_FROMREVISION = "].from.revision";
	public static final String CONSTANT_STRING_SELECT_FROMCURRENT = "].from.current";
	public static final String CONSTANT_STRING_SELECT_FROMDOT = "].from.";
	public static final String TYPE_PGBACKGROUDPROCESS = PropertyUtil.getSchemaProperty("type_pgBackgroundProcess");
	public static final String VAULT_ESERVICE_PRODUCTION = PropertyUtil.getSchemaProperty("vault_eServiceProduction");
	public static final String STATE_SHARED	= "RELEASED";
	public static final String STATE_PRIVATE = "PRIVATE";
	public static final String STATE_IN_WORK = "IN_WORK";
	public static final String STATE_WAITAPP = "FROZEN";	
	public static final String STATE_RELEASE = "Release";
	public static final String STATE_COMPLETE = "Complete";
	public static final String STATE_APPROVED = "Approved";
	public static final String STATE_OBSOLETE = "Obsolete";
	public static final String STATE_FROZEN = "Frozen";
	public static final String STATE_RELEASED = "Released";
	public static final String STATE_OBSOLETE_CATIA = "OBSOLETE";
	public static final String STATE_ACTIVE = "Active";
	public static final String STATE_IN_PROGRESS = "In-Progress";
	public static final String STATE_COMPLETED	= "Completed";
	public static final String STATE_ARCHIVED	= "Archived";
	public static final String ATTRIBUTE_METHODNAME = PropertyUtil.getSchemaProperty("attribute_MethodName");
	public static final String ATTRIBUTE_PROGRAMNAME = PropertyUtil.getSchemaProperty("attribute_ProgramName");
	public static final String SELECT_ATTRIBUTE_METHODNAME = "attribute["+ATTRIBUTE_METHODNAME+"]";
	public static final String SELECT_ATTRIBUTE_PROGRAMNAME = "attribute["+ATTRIBUTE_PROGRAMNAME+"]";
	public static final String STR_BACKGROUND_JOB_NAME = "BackgroundJobName";
	public static final String STR_SUCCESS = "Success";
	public static final String STR_FAIL = "Fail";
	public static final String STR_STATUS_JOB_INITIATED = "Background Job Initiated";
	public static final String STR_NO_DATA_FOR_PROCESSING = "No data sent for collaboration";
	public static final String STR_ERROR = "Error";
	public static final String KEY_MESSAGE = "Message";
	public static final String STR_NO_CLAIM_MSG="None of claims have Packaging Artwork. OR ACEContact is empty";
	public static final String STR_NO_ARTWORK_USER_ROLE_MSG="No Artwork User role assigned to dcm2rta.im user";
	public static final String STR_MASTER_GRAPHIC_TYPE="Master Artwork Graphic Element";
	public static final String STR_CLAIM_INFO = "ClaimsData";
	public static final String STR_CLAIM_ROUTE = "Claim Route";
	public static final String STR_ROUTE_DESC   = "Kick-off Translation module for ";
	public static final String STR_ROUTE_INST = "Please assign translations and related A&A for ";
	public static final String STR_MRK_CLAIM_REQ = "MRK Claim Request";
	public static final String ATTRIBUTE_ERROR_MESSAGE = PropertyUtil.getSchemaProperty("attribute_ErrorMessage");
	public static final String SPECIAL_CHAR_MATCH_STRING = "[^a-z A-Z0-9]";
	//Added for Email Notification
	public static final String ATTRIBUTE_PGPARAMETERARG = PropertyUtil.getSchemaProperty("attribute_pgParameterArgument1");
	public static final String STR_SUBJECT_SUCCESS = "SUCCESS: Copy List <CLName> is created for Claim Request <CLRName>";
	public static final String STR_SUBJECT_FAILURE = "FAIL: Copy List for Claim Request <CLRName> is not created";

}

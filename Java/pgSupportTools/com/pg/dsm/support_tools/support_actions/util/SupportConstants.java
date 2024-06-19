package com.pg.dsm.support_tools.support_actions.util;

import com.pg.v3.custom.pgV3Constants;

import matrix.util.StringList;

//Modified by DSM(Sogeti) for Req # 33939,34932

public abstract interface SupportConstants extends pgV3Constants {
	
	public String WAREHOUSE_CLASSIFICATION="WC";
	public String SMART_LABEL="SL";
	public String STABILITY_RESULT="SR";
	public String DGC="DGC";
	public String GHS="GHS";
	public String MATERIAL_CLASSIFICATION="MC";
	public String BATTERY="BATTERY";
	public String INGREDIENT="INGREDIENT";
	public String REGISTRATION="REGISTRATION";

	public String WAREHOUSE_CLASSIFICATION_DISPLAY_NAME = "Warehouse Classification";
	public String SMART_LABEL_DISPLAY_NAME="Smart Label";
	public String STABILITY_RESULT_DISPLAY_NAME="Stability Result";
	public String DGC_DISPLAY_NAME="DGC";
	public String GHS_DISPLAY_NAME="GHS";
	public String MATERIAL_CLASSIFICATION_DISPLAY_NAME="Material Certification";
	public String BATTERY_DISPLAY_NAME="Battery";
	public String INGREDIENT_DISPLAY_NAME="Ingredient Statement";
	public String REGISTRATION_DISPLAY_NAME="Market Registration";
	
	public String WAREHOUSE_CLASSIFICATION_EVENT_NAME = "Event For Warehouse Rollup";
	public String SMART_LABEL_EVENT_NAME="Event For Smart Label Rollup";
	public String STABILITY_RESULT_EVENT_NAME="Event For Stability Result Rollup";
	public String DGC_EVENT_NAME="Event For DGC Rollup";
	public String GHS_EVENT_NAME="Event For GHS Rollup";
	public String MATERIAL_CLASSIFICATION_EVENT_NAME="Event For Certification Rollup";
	public String BATTERY_EVENT_NAME = "Event For Battery Rollup";
	public String INGREDIENT_EVENT_NAME = "Event For Ingredient Statement Rollup";
	public String REGISTRATION_EVENT_NAME = "Event For Market Registration Rollup";

	public String MARK_EBP = "MARK_EBP";
	public String MARK_ROLLUP = "MARK_ROLLUP";
	public String MARK_COS = "MARK_COS";
	public String UNMARK_COS = "UNMARK_COS";
	public String MARK_DYNAMIC_SUBSCRIPTION = "MARK_DYNAMIC_SUBSCRIPTION";
	public String MARK_GENDOC = "MARK_GENDOC";
	public String RESEND_BOM_EDELIVERY = "RESEND_BOM_EDELIVERY";
	public String RESEND_WND = "RESEND_WND";
	public String REGENERATE_GENDOC ="REGENERATE_GENDOC";
	public String REPUBLISH = "REPUBLISH";

	public String MARK_EBP_DISPLAY_NAME = "Mark EBP Security (ResetRelease event)";
	public String MARK_ROLLUP_DISPLAY_NAME = "Mark Roll Up";
	public String MARK_COS_DISPLAY_NAME = "Mark Country of Sale";
	public String UNMARK_COS_DISPLAY_NAME = "Un-Mark Country of Sale";
	public String MARK_DYNAMIC_SUBSCRIPTION_DISPLAY_NAME = "Mark Dynamic Subscription (Internal - Release Event)";
	public String MARK_GENDOC_DISPLAY_NAME = "Mark Gen Doc";
	public String RESEND_BOM_EDELIVERY_DISPLAY_NAME = "Resend BOM eDelivery";
	public String RESEND_WND_DISPLAY_NAME = "Resend WnD";
	public String REGENERATE_GENDOC_DISPLAY_NAME = "Regenerate GenDoc for Released Parts";
	public String REPUBLISH_DISPLAY_NAME = "RePublish";

	public String MARK_EBP_JPO="pgEBPResetToPendingUtil";
	public String MARK_ROLLUP_JPO="pgFPPRollUp_Deferred";
	public String MARK_COS_JPO="pgIPMUtil_Deferred";
	public String UNMARK_COS_JPO="pgCountriesOfSale";
	public String MARK_DYNAMIC_SUBSCRIPTION_JPO="com.dassault_systemes.enovia.dynamic_subscriptions.triggers.enoGLSDynamicSubscription";
	public String MARK_GENDOC_JPO="pgIPMPDFViewUtil";
	public String RESEND_WND_JPO="pgDSOUtil_Deferred";
	public String RESEND_BOM_EDELIVERY_JPO="pgIPMUtil_Deferred";
	public String REGENERATE_GENDOC_JPO="pgIPMPDFViewUtil";	
	public String REPUBLISH_JPO="pgSendRepublishMessage";
	
	public String MARK_EBP_METHOD="markForEBPSummary";
	public String MARK_ROLLUP_METHOD="markRollupForSupportTool";
	public String MARK_COS_METHOD="setCOSCalculationsAttribute";
	public String UNMARK_COS_METHOD="setCOSCalculationsAttributeforUnMark";
	public String MARK_DYNAMIC_SUBSCRIPTION_METHOD="pgDynSubPartPromoteAction";
	public String MARK_GENDOC_METHOD="markCAForGenDocGeneration";
	public String RESEND_WND_METHOD="sendWNDDataToSAPOnFPPRelease";
	public String RESEND_BOM_EDELIVERY_METHOD="doBOMeDelivery";
	public String REGENERATE_GENDOC_METHOD ="performGenDocRegenerationForReleasedParts";
	public String ISGENDOCPRESENT_METHOD ="isGenDocPresent";
	public String REPUBLISH_METHOD="pgRepublishObjectOnDemand";

	public String MARK_EBP_EXCEPTION= "Exception occured in Mark EBP Program";
	public String MARK_ROLLUP_EXCEPTION="Exception occured in Mark Roll-Up Program";
	public String MARK_DYNAMIC_SUBSCRIPTION_EXCEPTION= "Exception occured in Mark Dynamic Program";
	public String MARK_GENDOC_EXCEPTION= "Exception occured in Mark Gendoc Program";
	public String MARK_COS_EXCEPTION= "Exception occured in Mark COS Program";
	public String UNMARK_COS_EXCEPTION= "Exception occured in Un-Mark COS Program";
	public String RESEND_WND_EXCEPTION= "Exception occured in Resend WnD Program";
	public String RESEND_BOM_EDELIVERY_EXCEPTION="Exception occured in Resend BOM eDelivery Program";
	public String REGENERATE_GENDOC_EXCEPTION ="Exception occured in GenDoc Regeneration for Released objects";
	public String REPUBLISH_EXCEPTION ="Exception occured in RePublish Program";
	
	public String MARK_EBP_COMMENT= "EBP Mark done by Support User:";
	public String MARK_ROLLUP_COMMENT="Roll-Up Mark done by Support User:";
	public String MARK_DYNAMIC_SUBSCRIPTION_COMMENT= "Dynamic Subscription Mark  done by Support User:";
	public String MARK_GENDOC_COMMENT= "Gen Doc Mark done by Support User:";
	public String MARK_COS_COMMENT= "Mark COS done by Support User:";
	public String UNMARK_COS_COMMENT= "Un-Mark COS done by Support User:";
	public String RESEND_WND_COMMENT= "Resend WnD done by Support User:";
	public String RESEND_BOM_EDELIVERY_COMMENT="Resend BOM Marking is done by Support User:";
	public String REGENERATE_GENDOC_COMMENT= "GenDoc Regeneration is Initiated by Support User:";
	public String REPUBLISH_COMMENT="Republish is Submitted by Support User:";
	
	public String SYMBOL_PIPE = "|";
	public String SYMBOL_COMMA = ",";
	public String LIMIT = "limit";
	public String COUNT = "count";
	public String NULL = "null";
	public String BLANK_SPACE = " ";
	public String NO_SPACE = " ";
	public String EVENT_DEMOTE = "Demote";
	public String STATE_RELEASE = "Release";
	public String EVENT_PROMOTE = "Promote";
	public String CONST_TRUE = "True";
	public String EMPTY_STRING ="";
	
	public String SUPPORT_ACTION_SUCCESS = "Success";
	public String SUPPORT_ACTION_FAILED = "Failed";
	public String UNSUPPORTED_ACTION = "Unsupported Action";
	public String UNSUPPORTED_STATE = "Unsupported State";
	public String GENDOC_INITIATE = "Background Job Initiated for GenDoc ReGeneration";

	public String SUPPORT_ACTION = "supportAction";
	public String SUPPORT_ACTION_SUB_ROLL_UP = "subRollUpActions";
	public String SUPPORT_ACTION_SUB_OPTIONS = "subActions";
	public String SUPPORT_ACTION_RESULT = "supportActionResult";
	public String SUPPORT_ACTION_DISPLAY_NAME = "supportActionDisplayName";
	public String SUPPORT_ACTION_JSON_RESPONSE = "response";
	public String SUPPORT_ACTION_JSON_RESPONSE_KEY = "responseJson";
	
	public String SUPPORT_ACTION_TYPE = "supportActionType";
	
	public String SUPPORT_SELECTED_ACTION = "Selected Action";
	public String SUPPORT_ACTION_SUMMARY = "Summary";
	
	public String OBJECT_LIST = "objectList";
	public String SYMB_SQUARE_BRACKET_OPEN = "[";
	public String SYMB_SQUARE_BRACKET_CLOSE = "]";
	public String FIELD_CHOICES = "field_choices";
	public String FIELD_DISPLAY_CHOICES = "field_display_choices";
	public String REQUEST_MAP = "requestMap";
	public String FIELD_MAP = "fieldMap";
	public String PARAM_NAME_OID = "NameOID";
	public String PARAM_NAME_DH_OID = "NameforDHOID";
	public String PARAM_SUPPORT_ACTIONS = "SupportActions";
	public String PARAM_ROLLUP_EVENTS = "RollUpTypes";
	public String CPN_STRING_RESOURCE = "emxCPNStringResource";
	public String PARAM_OBJECT_ID = "objectId";
	public String PARAM_FUNCTION = "function";
	public String PARAM_CHECK_LIMIT = "checkLimit";
	public String PARAM_ACTION = "action";
	public String PARAM_SUITE_KEY = "suiteKey";
	public String PARAM_PROGRAM = "program";
	public String PARAM_AMPERSAND = "&";
	public String PARAM_HEADER = "header";
	public String PARAM_EQUAL = "=";
	public String PARAM_COLON = ":";
	public String PARAM_TABLE = "table";
	public String PARAM_CLOSE = "Close";
	public String PARAM_TABLE_JSP = "../common/emxTable.jsp?";
	public String SUPPORT_PROGRAM="com.pg.dsm.support_tools.support_actions.util.SupportUtil";
	public String SUPPORT_METHOD="getSupportActionSummary";
	public String SUPPORT_SUMMARY_TABLE="pgSupportActionSummaryTable";
	
	public String SELECT_PREVIOUS_REVISION_ID="previous.id";
	
	public String ON_DEMAND_SUPPORT_ACTION_CONFIG = "pgOnDemandSupportActionConfig";
	public String SUPPORT_LIMIT = "10";
	public String SUPPORT_LIMIT_EXCEEDS_MESSAGE = "Number of selected objects (<count>) which exceeds the limit (<limit>).";
	
	public String UNMARK_COS_PACKAGING_TYPES="type_PackagingMaterialPart,type_pgOnlinePrintingPart,type_pgConsumerUnitPart,type_pgCustomerUnitPart,type_pgInnerPackUnitPart,type_pgAssembledProductPart,type_pgDeviceProductPart,type_FormulationPart,type_pgPackingMaterial";
	public String REL_EXPAND_SUBSTITUTE_IDS = "relationship[" + RELATIONSHIP_EBOMSUBSTITUTE +"].fromrel.from.id";
	public String REL_EXPAND_SUBSTITUTE_TYPES = "relationship[" + RELATIONSHIP_EBOMSUBSTITUTE +"].fromrel.from.type";
	public String REL_EXPAND_SUBSTITUTE_STATES = "relationship[" + RELATIONSHIP_EBOMSUBSTITUTE +"].fromrel.from.current";
	
	public StringBuffer IPS_POST_TYPES = new StringBuffer(TYPE_PGFINISHEDPRODUCT).append(SYMBOL_COMMA).append(TYPE_PGPACKINGSUBASSEMBLY).append(SYMBOL_COMMA).append(TYPE_FINISHEDPRODUCTPART).append(SYMBOL_COMMA).append(TYPE_PACKAGINGASSEMBLYPART).append(SYMBOL_COMMA).append(TYPE_FABRICATEDPART).append(SYMBOL_COMMA).append(TYPE_PGCUSTOMERUNITPART).append(SYMBOL_COMMA).append(TYPE_PGCONSUMERUNITPART);
	public StringBuffer EBOM_POST_TPYES = new StringBuffer(TYPE_PGFINISHEDPRODUCT).append(SYMBOL_COMMA).append(TYPE_PGPACKINGSUBASSEMBLY).append(SYMBOL_COMMA).append(TYPE_FINISHEDPRODUCTPART).append(SYMBOL_COMMA).append(TYPE_PACKAGINGASSEMBLYPART).append(SYMBOL_COMMA).append(TYPE_FABRICATEDPART).append(SYMBOL_COMMA).append(TYPE_FABRICATEDPART).append(SYMBOL_COMMA).append(TYPE_PGCUSTOMERUNITPART);
	public StringBuffer IPS_REL_EBOM_ALTERNATE_PATTERN = new StringBuffer(RELATIONSHIP_EBOM).append(SYMBOL_COMMA).append(SupportConstants.RELATIONSHIP_ALTERNATE);
	
	
	public String SELECT_ATTRIBUTE_ASSEMBLY_TYPE = "attribute["+ATTRIBUTE_PGASSEMBLYTYPE+"]";
	public String SELECT_ATTRIBUT_COS_CALCULATE = "attribute["+ATTRIBUTE_PGCOSCALCULATE+"]";
	public String SELECT_ATTRIBUTE_MARK_FOR_ROLLUP = "attribute["+ ATTRIBUTE_PGMARKFORROLLUP +"]";
	public String SELECT_ATTRIBUTE_EVENT_FOR_ROLLUP = "attribute["+ ATTRIBUTE_PGEVENTFORROLLUP +"]";
	
	public StringList UNMARK_COS_BUS_SELECTS=StringList.create(SELECT_ID,SELECT_TYPE,SELECT_NAME,SELECT_REVISION,SELECT_CURRENT,SELECT_POLICY,SELECT_ATTRIBUTE_ASSEMBLY_TYPE,SELECT_ATTRIBUT_COS_CALCULATE);
	public StringList UNMARK_COS_REL_SUBSTITUTE_SELECTS=StringList.create(REL_EXPAND_SUBSTITUTE_IDS,REL_EXPAND_SUBSTITUTE_TYPES,REL_EXPAND_SUBSTITUTE_STATES);
	public StringList ROLLUP_EXLCUDE_TYPES=StringList.create(GHS,MATERIAL_CLASSIFICATION,SMART_LABEL);
	public StringList UNMARK_COS_MULTI_FLAG_ALLOWED_TYPES=StringList.create(TYPE_PGFINISHEDPRODUCT,TYPE_PGPACKINGSUBASSEMBLY,TYPE_FINISHEDPRODUCTPART,TYPE_PGCONSUMERUNITPART,TYPE_PACKAGINGASSEMBLYPART,TYPE_FABRICATEDPART);
	public StringList UNMARK_COS_PARENT_FLAG_ALLOWED_TYPES=StringList.create(TYPE_PACKAGINGMATERIALPART,TYPE_PGONLINEPRINTINGPART,TYPE_PGCONSUMERUNITPART,TYPE_PGCUSTOMERUNITPART,TYPE_PGINNERPACKUNITPART,TYPE_ASSEMBLEDPRODUCTPART,TYPE_DEVICEPRODUCTPART,TYPE_FORMULATIONPART,TYPE_PGPACKINGMATERIAL);
	public StringList ROLLUP_BUS_SELECTS=StringList.create(SELECT_ATTRIBUTE_MARK_FOR_ROLLUP, SELECT_ATTRIBUTE_EVENT_FOR_ROLLUP, SELECT_ATTRIBUTE_ASSEMBLY_TYPE);
	
	public String SUPPORT_CONTEXT_USER = "contextUser";
	public String PARAM_CUSTOM = "custom";
	
    public String ACCESS_READ = "read";
	public String ACCESS_SHOW = "show";
	public String CONSTANT_CLEAR = "Clear";
	public String SEARCH_TYPES_EQUALS = "TYPES=";
	public String SEARCH_TYPES_NOT_EQUALS = ":Type!=";
	public String SEARCH_POLICY_EQUALS = ":Policy=";
	public String SEARCH_POLICY_NOT_EQUALS = ":Policy!=";
	public String SEARCH_STATE_EQUALS = ":CURRENT=";
	
	public String SUBSCRIBER_NAME = "SubscriberName";
	public String RESULT = "result";
}

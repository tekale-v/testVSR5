/*
**   PGWidgetConstants
*    This file contains constants which can reused across widgets
**
**   Copyright (c) 1992-2021 Dassault Systemes.
**   All Rights Reserved.
**   This program contains proprietary and trade secret information of MatrixOne,
**   Inc.  Copyright notice is precautionary only
**   and does not evidence any actual or intended publication of such program
**
*/

package com.pg.widgets.util;

import com.matrixone.apps.awl.util.AWLUtil;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainSymbolicConstants;
import com.matrixone.apps.domain.util.PropertyUtil;

import matrix.db.Context;

public class PGWidgetConstants {
	
	public static final String KEY_OBJECT_SELECTS = "ObjectSelects";
	public static final String STR_FROM = "from";
	public static final String STR_TO = "to";
	public static final String STR_OPENBRACKET = "[";
	public static final String STR_FROM_OPEN = AWLUtil.strcat(STR_FROM, STR_OPENBRACKET); 
	public static final String STR_TO_OPEN = AWLUtil.strcat(STR_TO, STR_OPENBRACKET); 
	public static final String STR_ATTRIBUTE = "attribute";
	public static final String STR_ATTRIBUTE_OPEN = AWLUtil.strcat(STR_ATTRIBUTE, STR_OPENBRACKET);
	public static final String SELECT_PHYSICAL_ID = "physicalid";
	public static final String KEY_PIPE_SEPARATOR = "|";
	public static final String KEY_COMMA_SEPARATOR = ",";
	public static final String STR_PIPE_SEPARATED_WITHSPACE = " | ";
	public static final String KEY_SELECTS_SEPARATOR  = "-";
	public static final String KEY_SEMICOLON_SEPARATOR  = ";";
	public static final String KEY_DOUBLE_EQUALS = "==";
	public static final String KEY_NEW_LINE = "\n";
	public static final String KEY_EMPTY_STRING = "";
	public static final String KEY_COLON_SEPERATOR = ":";
	public static final String KEY_DOLLAR_SEPERATOR = "$";
	
	public static final String KEY_TITLE = "Title";
	public static final String KEY_TYPE = "Type";
	public static final String KEY_POLICY = "Policy";
	public static final String KEY_STATES = "States";
	public static final String MULIT_VAL_CHAR = "\u0007";
	public static final String PERSON_USER_AGENT =  PropertyUtil.getSchemaProperty(null,"person_UserAgent");
	public static final String OPERATION_PROMOTE = "promote";
	public static final String OPERATION_DEMOTE = "demote";
	public static final String KEY_VALUE = "value";
	public static final String KEY_IS_CURRENT = "isCurrent";
	public static final String KEY_OPERATION = "operation";
	public static final String MAX_STRING_LENGTH = "org.apache.johnzon.max-string-length";
	public static final String VALUE_KILOBYTES = "262144";
	public static final String TYPE_PATTERN = "TypePattern";
	public static final String KEY_SIMPLE_DATEFORMAT = "yyyy-MM-dd";
	public static final String KEY_STATUS = "Status";
	public static final String KEY_FAILED = "failure";
	public static final String KEY_ERROR = "error";
	public static final String KEY_WARNING = "warning";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_TRACE = "trace";
	public static final String KEY_EXPR = "expr";
	public static final String KEY_HIERARCHY  = "hierarchy";	
	public static final String KEY_NAME = "name";
	public static final String KEY_DESCRIPTION = "Description";
	public static final String REASON_FOR_CHANGE_VALUE ="New";
	public static final String KEY_RELATED_IDS = "RelatedIds";
	public static final String KEY_RELPATTERN = "RelPattern";
	public static final String KEY_OBJECTID = "id";
	public static final String KEY_EXPANDLEVEL = "ExpandLevel";
	public static final String KEY_WHERECONDITION = "WhereCondition";
	public static final String KEY_GETTO = "GetTo";
	public static final String KEY_GETFROM = "GetFrom";
	public static final String KEY_LIMIT = "Limit";
	public static final String KEY_RELWHERECONDITION = "RelWhereCondition";
	public static final String KEY_RELATIONSHIPSELECTS = "RelationshipSelects";
	public static final String KEY_TOOBJECTID = "ToObjectId";
	public static final String KEY_FROMOBJECTID = "fromObjectId";
	public static final String KEY_PERSON ="Person";
	public static final String KEY_OUTPUT = "output";
	public static final String KEY_OBJECT_ID = "objectId";
	public static final String KEY_REL_ID = "relId";
	public static final String KEY_REL_NAME = "relName";
    public static final String KEY_SUCCESS = "success";
    public static final String KEY_ATTRIBUTES = "attributes";
    public static final String KEY_CONNECTIONS = "connections";
    public static final String KEY_REVISIONS = "revisions";
    public static final String KEY_ERROR_MSG = "errorMsg";
    public static final String KEY_FILE_NAME = "fileName";
    public static final String KEY_DATE = "date";
    public static final String KEY_INTEGER = "integer";
    public static final String KEY_PRILIMINARY = "Preliminary";
    public static final String KEY_RELEASE = "Release"; 
    
    public static final String STRING_TRUE = "true";
    public static final String STRING_FALSE = "false";
    public static final String STRING_YES = "Yes";
    
    public static final String STRING_CAPITAL_TRUE = "TRUE";
    public static final String STRING_CAPITAL_FALSE = "FALSE";
	
    public static final String TYPE_DOCUMENTS = PropertyUtil.getSchemaProperty(null, "type_DOCUMENTS");
    public static final String ATTRIBUTE_ROUTE_BASE_PURPOSE = "attribute["+DomainConstants.ATTRIBUTE_ROUTE_BASE_PURPOSE+"]";
    public static final String STATE_PRODUCTION = "Production";
    public static final String STATE_ACTIVE = "Active";
	
    public static final String ERROR_MSG_NOT_DOCUMENTS = "Please drop only Documents";
    public static final String ERROR_MSG_NOT_ROUTE_TEMPLATE = "Please drop only approval Active/Production Route Template";
    public static final String ERROR_MSG_NO_DUPLICATE_CONNECT = "Duplicate connections are not allowed";

	public static final String TYPE_APPLICATION_FORMAT  = "application/json";
	public static final String OBJ_SELECT = "ObjectSelects";
	public static final String FILE_FRAMEWORK_STRING_RESOURCE = "emxFrameworkStringResource";
	public static final String NAME_PATTERN = "NamePattern";
	public static final String REVISION_PATTERN = "RevisionPattern";
	public static final String OBJECT_LIMIT = "ObjectLimit";
	public static final String WHERE_EXP = "WhereExpression";
	public static final String EXPAND_TYPE = "ExpandType";
	public static final String DURATION = "Duration";
	public static final String ALLOWED_STATE = "AllowedStates";
	public static final String SHOW_OWNED = "ShowOwned";
	public static final String ASCENDING = "ascending";
	public static final String DESCENDING = "descending";
	public static final String STRING = "String";
	public static final String KEY_REVISIOM = "Revision";
	public static final String KEY_READ = "read";

	public static final String DENIED = "#DENIED!";
	public static final String KEY_NO_ACCESS = "No Access";
	public static final String LAST_WEEK = "LAST WEEK";
	public static final String LAST_TWO_WEEKS = "LAST TWO WEEKS";
	public static final String LAST_MONTH = "LAST MONTH";
	public static final String LAST_THREE_MONTHS = "LAST THREE MONTHS";
	public static final String LAST_SIX_MONTHS = "LAST SIX MONTHS";
	public static final String TIME_FORMAT_1 = "11:59:59 PM";
	public static final String TIME_FORMAT_2 = "12:00:00 AM";
	public static final String ACCESS_READ = "read";
	public static final String ACCESS_MODIFY = "modify";
	public static final String KEY_MODIFIED = "modified";
	public static final String KEY_DISPLAY_TYPE  = "displayType";
	public static final String KEY_OBJECT_TYPE  = "objectType";
	public static final String KEY_DISPLAY_NAME  = "displayName";
	public static final String SELECT_CONNECTION_ID = "id[connection]";
	public static final String SELECT_CONNECTION_NAME = "name[connection]";
	public static final String KEY_STATE = "state";
	
	public static final String KEY_DATA = "data";
	public static final String KEY_RELATED_DATA = "relateddata";
	public static final String KEY_DATA_ELEMENTS = "dataelements";
	public static final String KEY_HAS_FILES = "hasfiles";
	public static final String KEY_CAN_CHECKIN = "canCheckin";
	public static final String KEY_CAN_DELETE = "canDelete";
	public static final String SELECT_HAS_DELETE_ACCESS = "current.access[delete]";
	public static final String KEY_INPUTS = "inputs";
	public static final String KEY_DELIVERABLES = "deliverables";
	public static final String KEY_ATTACHMENTS = "attachments";
	public static final String KEY_CONTEXT = "context";
	public static final String KEY_IS_FROM = "isFrom";
	public static final String KEY_IS_TO = "isTo";
	public static final String KEY_CAPITAL_CONNECT = "CONNECT";
	public static final String KEY_CAPITAL_DISCONNECT = "DISCONNECT";
	public static final String KEY_UPDATE_ACTION = "updateAction";

	public static final String CONSTANT_STRING_STAR = "*";
	public static final String CONSTANT_STRING_ZERO = "0";
	public static final String KEY_UNDERSCORE = "_";
	public static final String KEY_OR = "|";
	public static final String KEY_TILDE = "~";

	public static final String ATTRIBUTE_PGUPTPHYSICALID = PropertyUtil.getSchemaProperty(null, "attribute_pgUPTPhyID");
	
	public static final String KEY_DEFAULT_USER_PREF_CLASSIFICATION = "preference_IRMPreferredClassification";
	public static final String KEY_DEFAULT_USER_PREF_TITLE = "preference_IRMPreferredTitle";
	public static final String KEY_DEFAULT_USER_PREF_DESCRIPTION = "preference_IRMPreferredDescription";
	public static final String KEY_DEFAULT_USER_PREF_POLICY = "preference_IRMPreferredPolicy";
	public static final String KEY_DEFAULT_USER_PREF_HIGHLY_RESTRICTED_CLASS = "preference_HighlyRestrictedClass";
	public static final String KEY_DEFAULT_USER_PREF_BUSINESS_USE_CLASS = "preference_BusinessUseClass";
	public static final String KEY_IRM_POLICY_VALUES = "IRMPolicyValues";
	public static final String ATTRIBUTE_REFERENCE_URI = PropertyUtil.getSchemaProperty(null, "attribute_ReferenceURI");
	public static final String KEY_DEFAULT_PREFERENCE_IRMBUSINESSAREA = "preference_IRMBusinessArea";
	public static final String KEY_DEFAULT_PREFERENCE_IRMPREFERREDREGION = "preference_IRMPreferredRegion";
	public static final String KEY_DEFAULT_PREFERENCE_IRMPREFERREDSHARINGMEMBERS = "preference_IRMPreferredSharingMembers";
	public static final String KEY_DEFAULT_PREFERENCE_IRMPREFERREDROUTEINSTRUCTION = "preference_IRMPreferredRouteInstruction";
	
	public static final String KEY_DEFAULT_PREFERENCE_GPS_PREF_POST_TASK_NOTIFY = "preference_GPSPreferredPostTaskNotificationUsers";
	public static final String KEY_DEFAULT_PREFERENCE_GPS_PREF_PRE_TASK_NOTIFY = "preference_GPSPreferredPreTaskNotificationUsers";
	public static final String KEY_DEFAULT_PREFERENCE_GPS_PREF_SHARE_WITH_MEMBERS = "preference_GPSPreferredShareWithMembers";
	
	//Assigned actual value as PropertyUtil.getSchemaProperty returns nothing for below attribute an type
	public static final String ATTRIBUTE_APP_DISPLAY_NAME = "App Display Name";
	public static final String TYPE_APP_DEFINITION = "AppDefinition";
	
	public static final String PROG_USER_PREF_REMPLATE_DSM = "pgDSMUPTServices";
	public static final String METHOD_GET_DSM_USER_PREF_TEMPLATES = "getAllUserPreferenceTemplates";
	public static final String PROG_IRMUPT_CLIENT = "IRMUPTClient";
	public static final String METHOD_GET_IRM_USER_PREF_TEMPLATES = "getIRMPreferenceTemplatesOIDs";
	public static final String VALUE_DSM = "DSM";
	public static final String VALUE_IRM = "IRM";
	public static final String KEY_TEMPLATE_TYPE= "templateType";
	public static final String KEY_OWNED = "owned";
	public static final String VALUE_CATEGOTY_TS = "TS";
	
	public static final String VAULT_ESERVICE_PRODUCTION = PropertyUtil.getSchemaProperty(null, DomainSymbolicConstants.SYMBOLIC_vault_eServiceProduction);
	public static final String TYPE_PG_SATS = PropertyUtil.getSchemaProperty(null, "type_pgStructuredATS");
	public static final String ATTRIBUTE_UPT_PART_CATEGOTY = PropertyUtil.getSchemaProperty(null, "attribute_pgUPTPartCategory");
	public static final String ATTRIBUTE_UPT_PART_TYPE = PropertyUtil.getSchemaProperty(null, "attribute_pgUPTPartType");
	
	/**
     * Constructor.
     *
     * @param context the eMatrix <code>Context</code> object
     * @param args holds no arguments
     * @throws Exception if the operation fails
     */
    private PGWidgetConstants (Context context, String[] args)
    {

    }

}

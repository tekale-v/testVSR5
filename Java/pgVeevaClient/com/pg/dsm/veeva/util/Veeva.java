/*
 **   Veeva.java
 **   Description - Introduced as part of Veeva integration.      
 **   Interface
 **
 */
package com.pg.dsm.veeva.util;

import com.matrixone.apps.domain.util.PropertyUtil;

public interface Veeva extends com.pg.v3.custom.pgV3Constants {
	public String AUTHENTICATION = "AUTHENTICATION";
	public String DOCUMENTS_QUERY = "DOCUMENTS_QUERY";
	@SuppressWarnings("deprecation")
	public String ATTRIBUTE_PG_CONFIG_COMMON_START_DATE = PropertyUtil.getSchemaProperty("attribute_pgConfigCommonStartedDate");
	@SuppressWarnings("deprecation")
	public String ATTRIBUTE_PG_CONFIG_COMMON_ENDED_DATE = PropertyUtil.getSchemaProperty("attribute_pgConfigCommonEndedDate");
	@SuppressWarnings("deprecation")
	public String ATTRIBUTE_PG_EPADEX_INTENDED_IPMS_GCAS = PropertyUtil.getSchemaProperty("attribute_pgEPADExIntendedIPMSGCAS");
	public String SELECT_ATTRIBUTE_PG_EPADEX_INTENDED_IPMS_GCAS = "attribute["+ATTRIBUTE_PG_EPADEX_INTENDED_IPMS_GCAS+"]";
	public String MATRIX_VEEVA_CONFIG_OBJECT_NAME = "pgVeevaIntegration";
	public String SELECT_ATTRIBUTE_PG_CONFIG_COMMON_START_DATE = "attribute["+ATTRIBUTE_PG_CONFIG_COMMON_START_DATE+"]";
	public String SELECT_ATTRIBUTE_PG_CONFIG_COMMON_ENDED_DATE = "attribute["+ATTRIBUTE_PG_CONFIG_COMMON_ENDED_DATE+"]";
	public String SELECT_ATTRIBUTE_PGCONFIGISACTIVE = "attribute["+ATTRIBUTE_PGCONFIGISACTIVE+"]";
	public String SELECT_ATTRIBUTE_PGCONFIGCOMMONRETRYCOUNT = "attribute["+ATTRIBUTE_PGCONFIGCOMMONRETRYCOUNT+"]";
	public String SELECT_ATTRIBUTE_PGCONFIGCOMMONADMINMAILID = "attribute["+ATTRIBUTE_PGCONFIGCOMMONADMINMAILID+"]";
	public String CONST_SYMBOL_DOUBLE_HYPHEN = "--";
	public String CONST_SYMBOL_SPACE = " ";
	public String CONST_SYMBOL_EQUAL = "=";
	public String CONST_SYMBOL_AMPERSAND = "&";
	public String CONST_SYMBOL_BACKWARD_SLASH = "/";
	public String CONST_KEYWORD_SESSION_ID = "sessionId";
	public String CONST_KEYWORD_ATTACHMENTS = "attachments";
	public String CONST_KEYWORD_FILE = "file";
	public String CONST_KEYWORD_OUTPUT = "output";
	public String CONST_KEYWORD_STRING ="%s";
	
	public String NULL = "null";
	public String CONST_KEYWORD_SPACE =" ";
	public String CONST_SYMBOL_SINGLE_QUOTE ="'";
	public String CONST_KEYWORD_COMMA =",";
	public String CONST_OPEN_BRACKET ="(";
	public String CONST_CLOSED_BRACKET =")";
	public String CONST_KEYWORD_AUTHENTICATION = "Authorization";
	public String CONST_KEYWORD_SUCCESS = "SUCCESS";
	public String CONST_KEYWORD_200 = "200";
	public String RESPONSE_CODE_200 = "200";
	public String CONST_KEYWORD_VEEVA = "Veeva";
	public String CONST_KEYWORD_VEEVA_FAILURE = "Veeva_Failure";
	public String CONST_FILE_PREFIX_JSON = ".json";
	public String CONST_DATA_SET_FILE_POSTFIX = "_dataset";
	public String CONST_DOCUMENT_PROPERTY_FILE_POSTFIX = "_property";
	public String CONST_USERS_FILE_POSTFIX = "_users_emails";
	public String EMPTY_STRING = "";
	public String CONST_JOB_CONFIG_OBJECT_TYPE = "pgConfigurationAdmin";
	public String CONST_JOB_CONFIG_OBJECT_NAME = "pgVeevaIntegration";
	public String CONST_JOB_CONFIG_OBJECT_REV = "-";
	public String USERNAME = "username";
	public String PASSWORD = "password";
	public String VEEVA_INPUT_FOLDER = "input";
	public String VEEVA_OUTPUT_FOLDER = "output";
	public String VEEVA_PROCESSED_FOLDER = "processed";
	public String VEEVA_SKIP_FOLDER = "skip";
	public String VEEVA_SUCCESS_FOLDER = "success";
	public String VEEVA_FAILED_FOLDER = "failed";
	public String VEEVA_JSON_FOLDER = "json";
	public String VEEVA_INWORK_FOLDER = "inwork";
	public String VEEVA_LOGS_FOLDER = "logs";
	public String VEEVA_INPUT_FILE = "doc.json";
	public String DOCUMENT_QUERY_RESPONSE_FILE = "doc.json";
	public String DOCUMENT_ID_FILE = "docIDs.txt";
	public String PAGE_VEEVA_XML = "VeevaXML";

	@SuppressWarnings("deprecation")
	public String POLICY_IPMSPECIFICATION = PropertyUtil.getSchemaProperty("policy_IPMSpecification");
	public String PROJECT = "Internal_PG";
	public String ORGANIZATION = "PG";
	public String STATECHANGE_QUERY = "mod bus <OBJECTID> current Release";
	public String USER_PGEPADEXUSERID = "pgePADExUserId";
	public String SYMBOL_HYPHEN = "-";
	@SuppressWarnings("deprecation")
	public String ATTRIBUTE_RELEASE_PHASE = PropertyUtil.getSchemaProperty("attribute_ReleasePhase");
	public String ARTWORK_ALLOWED_TYPES = TYPE_PACKAGINGMATERIALPART+","+TYPE_PGONLINEPRINTINGPART+","+TYPE_PGPACKINGMATERIAL;
	@SuppressWarnings("deprecation")
	public String TYPE_TEMPLATE =  PropertyUtil.getSchemaProperty("type_Template");
	public String ARTWORK_TEMPLATE_NAME = "Default Artwork Template";
	public String SELECT_LAST_REVISION = "last.revision";
	
	public String RENDITION_PREFIX = "LR_";
	public String RENDITION_SUFIX = "001";
	public String RENDITION_REV = "Rendition";
	public String STR_BASIC = "basic";
	public String STR_ATTRIBUTE = "attribute";
    public String STR_PROMOTE = "promote";
    public String CONST_SYMBOL_COLON = ":";
    public String STR_RENDITION_PREFIX = "_rendition_";
	@SuppressWarnings("deprecation")
    public String TYPE_CHANGE_TEMPLATE = PropertyUtil.getSchemaProperty("type_ChangeTemplate");
    
    public String VEEVA_PROPERTIES_FILE = "/var/opt/gplm/scripts/Veeva/veeva.properties";
    public String VEEVA_LOG4J_FILE = "/var/opt/gplm/scripts/Veeva/log4j.properties";
    public String VEEVA_LOG4J_FILE_ON_DEMAND = "/var/opt/gplm/scripts/Veeva/ondemand/log4j.properties";
          
    public String CONST_YES="yes";
    public String CONST_NO="no";
	
    public String EXECUTION_TYPE_SCHEDULED = "scheduled";
    public String EXECUTION_TYPE_ONDEMAND = "ondemand";
    
    public String STATE_IMPLEMENTED = "Implemented";
    public String STATE_WITHDRAWN = "Withdrawn";
    public String STATE_EXPIRED = "Expired";
    public String CONST_ERROR = "error";
    
    public String VQL_QUERY_ERROR_AUTHENTICATION = "Error forming Authentication VQL Query";
    public String VQL_QUERY_ERROR_DOCUMENTS = "Error forming Documents VQL Query";
    public String VQL_QUERY_ERROR_DOCUMENT_DATASET = "Error forming Document DataSet VQL Query";
    public String VQL_QUERY_ERROR_DOCUMENT_PROPERTY = "Error forming Document Property VQL Query";
    public String VQL_QUERY_ERROR_USERS_EMAIL = "Error forming Users Email VQL Query";
    public String VQL_QUERY_ERROR_RENDITION = "Error forming Rendition VQL Query";
	
    public String VEEVA_RESYNC_PROCESSED_MESSAGE = "Artwork processed. Please check updated failure view.";
    public String VEEVA_RESYNC_AUTHENTICATION_FAILURE_MESSAGE = "Veeva Authentication Failed.";
    public String VEEVA_RESYNC_GENERAL_EXCEPTION_MESSAGE = "Veeva Resync Exception occurred.";
    
    public String VEEVA_LAST_MODIFIED_DATE_KEY = "version_modified_date__v";
    public String SYMBOL_GREATHEN_THAN = ">";
    public String SYMBOL_LESSER_THAN_AND_EQUAL = "<=";
    public String SYMBOL_VEEVA_VQL_AND = "AND";
    public String REVISION_KEY = "<REVISION>";
    public String NAME_KEY = "<NAME>";

}

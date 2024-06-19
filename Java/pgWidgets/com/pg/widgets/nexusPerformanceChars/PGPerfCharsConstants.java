/*
**   PGPerfCharsConstants
*    This file contains constants which can be used for 'Performance Characteristics' widget web services 
**
**   Copyright (c) 1992-2021 Dassault Systemes.
**   All Rights Reserved.
**   This program contains proprietary and trade secret information of MatrixOne,
**   Inc.  Copyright notice is precautionary only
**   and does not evidence any actual or intended publication of such program
** 
*/

package com.pg.widgets.nexusPerformanceChars;

import com.dassault_systemes.enovia.enterprisechangemgt.common.ChangeConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.PropertyUtil;

/**
 * Constants class for Performance Characteristics web services
 */
public class PGPerfCharsConstants {	
	public static final String TYPE_PG_STABILITY_RESULTS = PropertyUtil.getSchemaProperty(null, "type_pgStabilityResults");
	public static final String TYPE_FINISHEDPRODUCT_PART = PropertyUtil.getSchemaProperty(null, "type_FinishedProductPart");
	public static final String TYPE_PG_CUSTOMERUNIT = PropertyUtil.getSchemaProperty(null, "type_pgCustomerUnitPart");
	public static final String TYPE_PG_MASTERCUSTOMERUNIT = PropertyUtil.getSchemaProperty(null, "type_pgMasterCustomerUnitPart");
	public static final String TYPE_PG_INNERPACK  = PropertyUtil.getSchemaProperty(null, "type_pgInnerPackUnitPart");
	public static final String TYPE_PG_MASTERINNERPACKUNIT = PropertyUtil.getSchemaProperty(null, "type_pgMasterInnerPackUnitPart");
	public static final String TYPE_PG_CONSUMERUNIT = PropertyUtil.getSchemaProperty(null, "type_pgConsumerUnitPart");
	public static final String TYPE_PG_MASTERCONSUMERUNIT = PropertyUtil.getSchemaProperty(null, "type_pgMasterConsumerUnitPart");
	public static final String TYPE_TEST_METHOD_SPECIFICATION = PropertyUtil.getSchemaProperty(null, "type_TestMethodSpecification");
	public static final String TYPE_PG_IRM_DOC_TYPES = PropertyUtil.getSchemaProperty(null, "type_pgPKGStabilityReport")
			+ "," + PropertyUtil.getSchemaProperty(null, "type_ProductStabilityReport");
	public static final String TYPE_PG_TMRD_TYPES = PropertyUtil.getSchemaProperty(null, "type_pgQualitySpecification")
			+ "," + PropertyUtil.getSchemaProperty(null, "type_pgStandardOperatingProcedure") + ","
			+ PropertyUtil.getSchemaProperty(null, "type_pgIllustration");

	public static final String ATTRIBUTE_TITLE = PropertyUtil.getSchemaProperty(null, "attribute_Title");
	public static final String ATTR_REFERENCE_TYPE = PropertyUtil.getSchemaProperty(null, "attribute_ReferenceType");
	public static final String SELECT_ATTR_REFERENCE_TYPE = DomainObject.getAttributeSelect(ATTR_REFERENCE_TYPE);
	public static final String ATTR_PGORIGINATINGSOURCE = PropertyUtil.getSchemaProperty(null, "attribute_pgOriginatingSource");
	public static final String SELECT_ATTR_PGORIGINATINGSOURCE = DomainObject.getAttributeSelect(ATTR_PGORIGINATINGSOURCE);
	public static final String ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE = PropertyUtil.getSchemaProperty(null,"attribute_SharedTableCharacteristicSequence");
	public static final String SELECT_ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE = DomainObject.getAttributeSelect(ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE);
	public static final String ATTR_PG_INHERITANCE_TYPE = PropertyUtil.getSchemaProperty(null, "attribute_pgPFInheritanceType");
	public static final String SELECT_ATTR_PG_INHERITANCE_TYPE = DomainObject.getAttributeSelect(ATTR_PG_INHERITANCE_TYPE);
	public static final String ATTRIBUTE_PG_INHERITED_FROM_PLATFORM = PropertyUtil.getSchemaProperty(null, "attribute_pgInheritedFromPlatform");
	public static final String ATTRIBUTE_PG_APPLICATION = PropertyUtil.getSchemaProperty(null, "attribute_pgApplication");
	public static final String SELECT_ATTRIBUTE_PG_APPLICATION = DomainObject.getAttributeSelect(ATTRIBUTE_PG_APPLICATION);
	public static final String STR_RELEASE_PHASE = PropertyUtil.getSchemaProperty(null,"attribute_ReleasePhase");
	public static final String ATTR_PG_PALLETTYPE = PropertyUtil.getSchemaProperty(null, "attribute_pgPalletType");
	public static final String ATTR_ACTION_REQUIRED = PropertyUtil.getSchemaProperty(null, "attribute_pgActionRequired"); 
	public static final String ATTR_PG_TMLOGIC = PropertyUtil.getSchemaProperty(null, "attribute_pgTMLogic");
	public static final String ATTR_PG_REPORTTYPE = PropertyUtil.getSchemaProperty(null, "attribute_pgReportType");
	public static final String ATTR_PG_CHARACTERISTIC = PropertyUtil.getSchemaProperty(null, "attribute_pgCharacteristic"); 
	public static final String SELECT_ATTR_PG_CHARACTERISTIC = DomainObject.getAttributeSelect(ATTR_PG_CHARACTERISTIC);
	public static final String ATTR_PG_CHARACTERISTIC_SPECIFICS = PropertyUtil.getSchemaProperty(null, "attribute_pgCharacteristicSpecifics"); 
	public static final String SELECT_ATTR_PG_CHARACTERISTIC_SPECIFICS = DomainObject.getAttributeSelect(ATTR_PG_CHARACTERISTIC_SPECIFICS);
	public static final String ATTR_PG_LOWERSPECIFICATIONLIMIT = PropertyUtil.getSchemaProperty(null, "attribute_pgLowerSpecificationLimit");
	public static final String ATTR_PG_UPPERSPECIFICATIONLIMIT = PropertyUtil.getSchemaProperty(null, "attribute_pgUpperSpecificationLimit");
	public static final String ATTR_PG_LOWERROUTINERELEASELIMIT = PropertyUtil.getSchemaProperty(null, "attribute_pgLowerRoutineReleaseLimit");
	public static final String ATTR_PG_UPPERROUTINERELEASELIMIT = PropertyUtil.getSchemaProperty(null, "attribute_pgUpperRoutineReleaseLimit");
	public static final String ATTR_PG_TARGET = PropertyUtil.getSchemaProperty(null, "attribute_pgTarget");
	public static final String ATTR_PG_LOWERTARGET = PropertyUtil.getSchemaProperty(null, "attribute_pgLowerTarget");
	public static final String ATTR_PG_UPPERTARGET = PropertyUtil.getSchemaProperty(null, "attribute_pgUpperTarget");
	public static final String SYM_ATTR_PG_LOWERROUTINE_RELEASELIMIT = "attribute_pgLowerRoutineReleaseLimit";
	public static final String SYM_ATTR_PG_UPPERROUTINE_RELEASELIMIT = "attribute_pgUpperRoutineReleaseLimit";
	public static final String ATTR_PG_LOWERROUTINE_RELEASELIMIT = PropertyUtil.getSchemaProperty(null, SYM_ATTR_PG_LOWERROUTINE_RELEASELIMIT);
	public static final String ATTR_PG_UPPERROUTINE_RELEASELIMIT = PropertyUtil.getSchemaProperty(null,SYM_ATTR_PG_UPPERROUTINE_RELEASELIMIT);
	public static final String ATTRIBUTE_NEXUS_STRUCTURED_PERFCHARSREQUIRED = PropertyUtil.getSchemaProperty(null, "attribute_pgNexusStructuredPerfCharsRequired");
	public static final String SELECT_ATTRIBUTE_NEXUS_STRUCTURED_PERFCHARSREQUIRED = DomainObject.getAttributeSelect(ATTRIBUTE_NEXUS_STRUCTURED_PERFCHARSREQUIRED);
	public static final String ATTRIBUTE_PGNEXUSPARAMETERVALUES = PropertyUtil.getSchemaProperty(null, "attribute_pgNexusParameterValues");
	public static final String SELECT_ATTRIBUTE_PGNEXUSPARAMETERVALUES = DomainObject.getAttributeSelect(ATTRIBUTE_PGNEXUSPARAMETERVALUES);
	public static final String ATTRIBUTE_PG_SAMPLING = PropertyUtil.getSchemaProperty(null, "attribute_pgSampling");
	public static final String SELECT_ATTRIBUTE_PG_SAMPLING =  DomainObject.getAttributeSelect(ATTRIBUTE_PG_SAMPLING);
	public static final String ATTRIBUTE_NEXUS_VALID_CONVERTIBLE_UNITS = PropertyUtil.getSchemaProperty(null, "attribute_pgNexusValidConvertibleUnits");
	public static final String SELECT_ATTRIBUTE_NEXUS_VALID_CONVERTIBLE_UNITS =  DomainObject.getAttributeSelect(ATTRIBUTE_NEXUS_VALID_CONVERTIBLE_UNITS);
	
	public static final String RELATIONSHIP_CHARACTERISTIC = PropertyUtil.getSchemaProperty(null,"relationship_Characteristic");
	public static final String REL_PARTFAMILYREFERENCE = PropertyUtil.getSchemaProperty(null, "relationship_PartFamilyReference");
	public static final String RELATIONSHIP_PGPRODUCTPLATFORMFOP = PropertyUtil.getSchemaProperty(null, "relationship_pgProductPlatformFOP");
	public static final String REL_PG_INHERITED_CAD_SPEC = PropertyUtil.getSchemaProperty(null, "relationship_pgInheritedCADSpecification");
	public static final String RELATIONSHIP_PART_SPECIFICATION = PropertyUtil.getSchemaProperty(null, "relationship_PartSpecification");
	public static final String RELATIONSHIP_SHARED_CHARACTERISTIC = PropertyUtil.getSchemaProperty(null, "relationship_SharedCharacteristic");
	
	public static final String FILTER_ALL = "All";
	public static final String FILTER_LOCAL = "Local";
	public static final String FILTER_REFERENCED = "Referenced";
	
	public static final String STRING_OBJECTID = "objectId";
	public static final String STATE_RELEASE = "Release";
	public static final String STATE_COMPLETE = "Complete";
	public static final String STATE_APPROVED = "Approved";
	public static final String STATE_OBSOLETE = "Obsolete";
	public static final String STR_REFERENCED = "Referenced";
	public static final String STR_LOCAL = "Local";
	public static final String BLANK_SPACE = " ";
	public static final String PREFIX_COL_PROG = "ColProg_";
	
	public static final String SECURITYCLASS_LIST = "type_SecurityControlClass,type_IPControlClass,type_ExportControlClass";
	public static final String ARROW_SYMBOL = "<img style=\"padding-left:2px; padding-right:2px;\" src=\"../common/images/iconTreeToArrow.gif\"></img>";
	
	public static final String TYPE_PG_PERFORMANCE_CHARACTERSTIC = PropertyUtil.getSchemaProperty(null,"type_pgPerformanceCharacteristic");	
	public static final String EXCEPTION_MESSAGE_PERF_CHAR_FETCH_DATA = "Exception in PGPerfCharsFetchData";
	public static final String EXCEPTION_MESSAGE_PERF_CHAR_CREATE_EDIT_UTIL= "Exception in PGPerfCharsCreateEditUtil";
	public static final String EXCEPTION_MESSAGE_PERF_CHAR_RELEASE_CRITERIA_UTIL= "Exception in PGPerfCharsReleaseCriteriaWizardUtil";
	public static final String EXCEPTION_MESSAGE_PERF_CHAR_SPECIAL_ATTR_UTIL= "Exception in PGPerfCharsSpecializedDataUtil";
	public static final String MESSAGE_DEL_FILE_FAILED = "Exception in 'PGPerfCharsCreateEditUtil:importPerfCharsFromExcel'. Unable to delete the file {0}";
	public static final String MESSAGE_DEL_DIR_FAILED = "Exception in 'PGPerfCharsCreateEditUtil:importPerfCharsFromExcel'. Unable to delete the workspace {0}";
	public static final String KEY_OBJ_SELECTS = "objectSelects";
	public static final String KEY_DATA = "data";
	public static final String KEY_CHARS_DERIVED_FILTER = "pgVPDCPNCharacteristicDerivedFilter";
	public static final String PERSON_USER_AGENT = "person_UserAgent";
	public static final String PERSON_AGENT = PropertyUtil.getSchemaProperty(null,PERSON_USER_AGENT);
	public static final String KEY_OBJECT_ID = "objectId";
	public static final String KEY_MODE = "Mode";
	public static final String KEY_ADD_ROW = "AddRow";
	public static final String KEY_SWITCH_MODE = "SwitchMode";
	
	public static final String KEY_SELECTED_TABLE = "selectedTable";
	public static final String KEY_OPERATION = "operation";
	public static final String KEY_CREATE = "create";
	public static final String KEY_EDIT = "edit";
	public static final String KEY_DEL = "delete";
	public static final String KEY_ATTRIBUTE = "attribute";
	public static final String KEY_REL_ATTRIBUTE = "relAttributes";
	public static final String KEY_CONNECTIONS = "connections";
	public static final String KEY_SEQUENCE = "Sequence";
	public static final String PREFIX_ATTR_SELECT = "attribute[";
	public static final String SUFFIX_ATTR_SELECT = "]";
	public static final String KEY_RELID = "relId";
	public static final String ERROR = "ERROR";
	public static final String SETTING_PG_EXPORT_HIDDEN = "pgExportHidden";//Custom Setting 'pgExportHidden=true' to hide a column from Exported Excel sheet
	public static final String KEY_FILENAME = "filename";
	public static final String KEY_FILEN_AME = "fileName";
	public static final String KEY_BASE_64 = "base64,";
	public static final String KEY_CPN_PROPERTIES = "emxCPN.properties";
	public static final String PROPERTIES_KEY_RELEASE_CRITERIA= "emxCPN.StructuredReleaseCriteria";
	public static final String KEY_PROPERTIES = "Properties";
	public static final String KEY_GRADING = "Grading";
	public static final String KEY_NEXUS_PARAM_ID = "NexusParamId";
	public static final String KEY_METHOD_SPECIFICS = "TestMethodSpecifics";
	public static final String KEY_SAMPLING = "SamplingPlan";
	public static final String KEY_APPLICATION = "Application";
	public static final String KEY_RELEASE_CRITERIA = "ReleaseCriteria";
	public static final String KEY_TABLE_COL_DETAILS = "table Column Details";
	public static final String PAGE_NEXUS_PERF_CHAR_TABLE= "pgNexusPerfCharTables";
	
	public static final String RANGE_VALUE_TRUE = "TRUE";
	public static final String RANGE_VALUE_FALSE = "FALSE";
	public static final String RANGE_VALUE_SMALL_TRUE = "true";
	public static final String RANGE_VALUE_SMALL_FALSE = "false";
	
	public static final String STR_PG_VPD_PERFORMANCE_CHARACTERISTIC_TABLE = "pgVPDPerformanceCharacteristicTable";
	public static final String STR_PG_VPD_PERFORMANCE_CHARACTERISTIC_MASTER_PATH_TABLE = "pgVPDPerformanceCharacteristicMasterPathTable";
	public static final String STR_PG_DSO_MASTERPART_DETAILS_TABLE = "pgDSOMasterPartDetails";
	public static final String STR_PG_VPD_APP_DOCUMENTSUMMARY = "pgVPDAPPDocumentSummary";
	public static final String STR_ENC_DOCUMENTSUMMARY = "ENCDocumentSummary";
	public static final String STR_APP_DOCUMENTSUMMARY = "APPDocumentSummary";
	
	public static final String TEST_METHOD_SPECIFICATION = "Test Method Specification";
	public static final String KEY_LIST_OF_PC_IDS = "PCId";
	public static final String EXCEPTION_MESSAGE_PERF_CHAR_DELETE_UTIL = "Exception in PGPerfCharsDeleteUtil";
	public static final String KEY_STATUS = "status";
	public static final String CONSTANT_STRING_PIPE = "|";
	public static final String STRING_ERR_DELETE = "No Performance Characterstic Object is present to delete";
	public static final String ORIGINATING_SOURCE_DSO = "DSO";

	public static final String VALUE_SUCCESS = "success";
	public static final String VALUE_FAIL = "Fail";
	public static final String EXCEPTION_MESSAGE_PERF_CHAR_COPYFROM_PROD_DATA = "Exception in PGPerfCharsCopyFromProductData";
	public static final String TYPE_SHARED_TABLE = PropertyUtil.getSchemaProperty(null,"type_SharedTable");
	public static final String SELECT_LAST_PREVIOUS_ID = "last.previous.id";
	public static final String SELECT_LAST_PREVIOUS_CURRENT = "last.previous.current";
	public static final String SELECT_LAST_CURRENT = "last.current";
	public static final String ATTR_PG_RELEASECRITERIA = PropertyUtil.getSchemaProperty(null, "attribute_pgReleaseCriteria");
	public static final String ATTR_PG_PLANTTESTINGTEXT = PropertyUtil.getSchemaProperty(null, "attribute_pgPlantTestingText");
	public static final String VAULT_ESERVICE_PRODUCTION = "eService Production";
	public static final String TYPE_PG_STANDARD_OPERATING_PROCEDURE = PropertyUtil.getSchemaProperty(null, "type_pgStandardOperatingProcedure");
	public static final String TYPE_PG_QUALITY_SPECIFICATION = PropertyUtil.getSchemaProperty(null,"type_pgQualitySpecification");
	public static final String TYPE_PG_ILLUSTRATION = PropertyUtil.getSchemaProperty(null,"type_pgIllustration");
	public static final String ATTR_PG_ROUTINERELEASECRITERIA = PropertyUtil.getSchemaProperty(null, "attribute_pgRoutineReleaseCriteria");
	public static final String ATTR_PG_PLANTTESTINGRETESTING = PropertyUtil.getSchemaProperty(null, "attribute_pgPlantTestingRetesting");
	
	// DSM (DS) : 2018x.6 : APR_CW : ALM_40397,ALM_40641 : Performance Characteristics Validation : START
	public static final String ATTRIBUTE_PG_CHARACTERISTIC =  PropertyUtil.getSchemaProperty("attribute_pgCharacteristic");
	public static final String SELECT_ATTRIBUTE_PG_CHARACTERISTIC = "attribute["+ATTRIBUTE_PG_CHARACTERISTIC+"]";
	public static final String ATTRIBUTE_PG_CHARACTERISTICSPECIFIC =  PropertyUtil.getSchemaProperty("attribute_pgCharacteristicSpecifics");
	public static final String SELECT_ATTRIBUTE_PG_CHARACTERISTICSPECIFIC = "attribute["+ATTRIBUTE_PG_CHARACTERISTICSPECIFIC+"]";
	public static final String ATTRIBUTE_PG_UNIT_OF_MEASURE =  PropertyUtil.getSchemaProperty("attribute_pgUnitOfMeasure");
	public static final String SELECT_ATTRIBUTE_PG_UNIT_OF_MEASURE = "attribute["+ATTRIBUTE_PG_UNIT_OF_MEASURE+"]";
	public static final String STR_PG_PLI_CHARACTERISTIC = "pgPLICharacteristic";
	public static final String STR_PG_PLI_CHARACTERISTICSPECIFIC = "pgPLICharacteristicSpecifics";
	public static final String STR_PG_PLI_UNITOFMEASUREMASTERLIST = "pgPLIUnitofMeasureMasterList";
	public static final String ATTRIBUTE_PG_INVALIDPERFCHARS =  PropertyUtil.getSchemaProperty("attribute_pgInvalidPerfChars");
	public static final String SELECT_ATTRIBUTE_PG_INVALIDPERFCHARS = "attribute["+ATTRIBUTE_PG_INVALIDPERFCHARS+"]";
	public static final String ATTRIBUTE_PG_SHORT_CODE = PropertyUtil.getSchemaProperty("attribute_pgShortCode");
	public static final String REL_CHARACTERISTICTOCHARACTERISTICSPECIFICS = PropertyUtil.getSchemaProperty("relationship_pgCharateristicToCharateristicSpecifics");
	public static final String CONSTANT_STRING_DOUBLE_EQUAL = "==";
	public static final String STATE_ACTIVE = "Active";
	public static final String STR_DESCENDING = "descending";
	public static final String TYPE_PG_PLICHARACTERISTIC = PropertyUtil.getSchemaProperty("type_pgPLICharacteristic");
	public static final String TYPE_PG_PLICHARSPECIFICS = PropertyUtil.getSchemaProperty("type_pgPLICharacteristicSpecifics");
	public static final String TYPE_CPG_PRODUCT	= PropertyUtil.getSchemaProperty("type_CPGProduct");
	public static final String STATE_PRELIMINARY = "Preliminary";
	public static final String STABILITYRESULT_TABLE = "pgStabilityResultsTable";
	public static final String STATE_CA_INWORK = "In Work";
	public static final String SELECT_ID = "id";
	public static final String STATE_CA_PREPARE = PropertyUtil.getSchemaProperty("policy", ChangeConstants.POLICY_CHANGE_ACTION, "state_Prepare");
	public static final String TYPE_INTERNAL_MATERIAL = PropertyUtil.getSchemaProperty("type_InternalMaterial");
	public static final String TYPE_CRITERIA = PropertyUtil.getSchemaProperty("type_Criteria");
	public static final String POLICY_CRITERIA = PropertyUtil.getSchemaProperty("policy_Criteria");
	public static final String CRITERIA_STATE_IN_WORK = PropertyUtil.getSchemaProperty("policy", POLICY_CRITERIA, "state_In-Work");
	public static final String ROLE_GSO = PropertyUtil.getSchemaProperty("role_pgStandardsOffice");
	public static final String CONST_INTERFACE_PART_FAMILY = "interface[Part Family Reference]";
	public static final String TYPE_CPGPRODUCT = PropertyUtil.getSchemaProperty("type_CPGProduct");
	public static final String ROLE_PGCONTRACTMANUFACTURER = PropertyUtil.getSchemaProperty("role_pgIPMContractManufacturer");
	public static final String ROLE_PGCONTRACTSUPPLIER = PropertyUtil.getSchemaProperty("role_pgIPMContractSupplier");
	public static final String ATTRIBUTE_PG_STRUCTURE_RELEASE_CRIT_REQUIRED = PropertyUtil.getSchemaProperty("attribute_pgStructuredReleaseCriteriaRequired");
	public static final String SELECT_ATTRIBUTE_STRUCTUREDRELEASECRITERIAREQUIRED = DomainObject.getAttributeSelect(ATTRIBUTE_PG_STRUCTURE_RELEASE_CRIT_REQUIRED);
	public static final String CONST_YES ="Yes";
	public static final String CONST_NO ="No";
		// DSM (DS) : 2018x.6 : APR_CW : ALM_40397,ALM_40641 : Performance Characteristics Validation : END
	
	public static final String OBJECT_SELECTS_FOR_PC_DATA  = "id,type,name,revision,physicalid,owner,current," + "attribute[pgChange],"
			+ "attribute[pgCharacteristic]," + "attribute[pgCharacteristicSpecifics]," + "attribute[pgTMLogic],"
			+ "attribute[pgMethodOrigin]," + "attribute[pgMethodNumber]," + "attribute[pgMethodSpecifics],"
			+ "attribute[pgSampling]," + "attribute[pgSubGroup]," + "attribute[pgPlantTesting],"
			+ "attribute[pgPlantTestingRetesting]," + "attribute[pgRetestingUOM],"
			+ "attribute[pgLowerSpecificationLimit]," + "attribute[pgLowerRoutineReleaseLimit],"
			+ "attribute[pgLowerTarget]," + "attribute[pgTarget]," + "attribute[pgUpperTarget],"
			+ "attribute[pgUpperRoutineReleaseLimit]," + "attribute[pgUpperSpecificationLimit],"
			+ "attribute[pgUnitOfMeasure]," + "attribute[pgReportToNearest]," + "attribute[pgReportType],"
			+ "attribute[pgRoutineReleaseCriteria]," + "attribute[pgReleaseCriteria],"
			+ "attribute[pgActionRequired]," + "attribute[pgCriticalityFactor]," + "attribute[pgBasis],"
			+ "attribute[pgTestGroup]," + "attribute[pgApplication]";
	
	public static final String TYPE_PRODUCTDATAPART = PropertyUtil.getSchemaProperty(null, "type_ProductDataPart");
	public static final String TYPE_RAWMATERIAL = PropertyUtil.getSchemaProperty(null, "type_RawMaterial");
	public static final String ATTRIBUTE_PG_CATEGORYSPECIFICS = PropertyUtil.getSchemaProperty(null, "attribute_pgCategorySpecifics");
	public static final String SELECT_ATTRIBUTE_PG_CATEGORYSPECIFICS = "attribute[" + ATTRIBUTE_PG_CATEGORYSPECIFICS + "]";
	public static final String ATTRIBUTE_CHARACTERISTICSPECIFIC = PropertyUtil.getSchemaProperty(null, "attribute_pgCharacteristicSpecifics");
	public static final String SELECT_ATTRIBUTE_CHARACTERISTICSPECIFIC = "attribute[" + ATTRIBUTE_CHARACTERISTICSPECIFIC + "]";
	public static final String RELATIONSHIP_PARAMETER_AGGREGATION = PropertyUtil.getSchemaProperty(null, "relationship_ParameterAggregation");
	public static final String TYPE_PLM_PARAMETER = PropertyUtil.getSchemaProperty(null, "type_PlmParameter");
	public static final String STRING_ASCENDING= "ascending";
	public static final String STR_STRING= "string";
	public static final String SELECT_ATTRIBUTE_TITLE = "attribute[" + DomainConstants.ATTRIBUTE_TITLE + "]";
	public static final String ATTRIBUTE_CHARACTERISTICSLOWERSPECIFICATIONLIMIT = PropertyUtil.getSchemaProperty(null,"attribute_CharacteristicsLowerSpecificationLimit");
	public static final String ATTRIBUTE_PG_CHARACTERISTIC_CATEGORY = PropertyUtil.getSchemaProperty(null, "attribute_CharacteristicCategory");
	public static final String ATTRIBUTE_CHARACTERISTICSUPPERROUTINERELEASELIMIT = PropertyUtil.getSchemaProperty(null, "attribute_CharacteristicsUpperRoutineReleaseLimit");
	public static final String SELECT_ATTRIBUTE_PG_CHARACTERISTIC_CATEGORY = "attribute[" + ATTRIBUTE_PG_CHARACTERISTIC_CATEGORY + "]";
	public static final String ATTRIBUTE_CHARACTERISTICSUPPERSPECIFICATIONLIMIT = PropertyUtil.getSchemaProperty(null,"attribute_CharacteristicsUpperSpecificationLimit");
	public static final String ATTRIBUTE_CHARACTERISTICSLOWERROUTINERELEASELIMIT = PropertyUtil.getSchemaProperty(null,"attribute_CharacteristicsLowerRoutineReleaseLimit");
	public static final String ATTRIBUTE_PG_TEST_GROUP = PropertyUtil.getSchemaProperty(null,"attribute_pgTestGroup");
	public static final String ATTRIBUTE_PG_PLANT_TESTING = PropertyUtil.getSchemaProperty(null,"attribute_pgPlantTesting");
	public static final String SELECT_ATTRIBUTE_CHARACTERISTICSUPPERSPECIFICATIONLIMIT = "attribute[" + ATTRIBUTE_CHARACTERISTICSUPPERSPECIFICATIONLIMIT + "]";
	public static final String SELECT_ATTRIBUTE_CHARACTERISTICSLOWERSPECIFICATIONLIMIT = "attribute[" + ATTRIBUTE_CHARACTERISTICSLOWERSPECIFICATIONLIMIT + "]";
	public static final String SELECT_ATTRIBUTE_CHARACTERISTICSLOWERROUTINERELEASELIMIT = "attribute[" + ATTRIBUTE_CHARACTERISTICSLOWERROUTINERELEASELIMIT + "]";
	public static final String SELECT_ATTRIBUTE_CHARACTERISTICSUPPERROUTINERELEASELIMIT = "attribute[" + ATTRIBUTE_CHARACTERISTICSUPPERROUTINERELEASELIMIT + "]";
	public static final String ATTRIBUTE_PG_TARGET = PropertyUtil.getSchemaProperty(null,"attribute_pgTarget");
	public static final String SELECT_ATTRIBUTE_PG_TARGET = "attribute[" + ATTRIBUTE_PG_TARGET + "]";
	public static final String ATTRIBUTE_PG_UPPER_TARGET = PropertyUtil.getSchemaProperty(null, "attribute_pgUpperTarget");
	public static final String SELECT_ATTRIBUTE_PG_UPPER_TARGET = "attribute[" + ATTRIBUTE_PG_UPPER_TARGET + "]";
	public static final String ATTRIBUTE_PG_LOWER_TARGET = PropertyUtil.getSchemaProperty(null, "attribute_pgLowerTarget");
	public static final String SELECT_ATTRIBUTE_PG_LOWER_TARGET = "attribute[" + ATTRIBUTE_PG_LOWER_TARGET + "]";
	public static final String TRACE_LPD = "LPD";
	public static final String ATTR_PG_ORIGINATINGSOURCE = PropertyUtil.getSchemaProperty(null,"attribute_pgOriginatingSource");
	public static final String TYPE_PG_TEST_METHOD = PropertyUtil.getSchemaProperty(null, "type_pgTestMethod");
	public static final String CONSTANT_STRING_COLON = ":";
	public static final String RANGE_VALUE_TMLOGIC_ANY = "ANY";
	public static final String RANGE_VALUE_TMLOGIC_ALL = "ALL";
	public static final String RANGE_VALUE_VARIABLE = "VARIABLE";
	public static final String RANGE_VALUE_ATTRIBUTE = "ATTRIBUTE";
	public static final String RANGE_VALUE_REPORT = "REPORT";
	public static final String RANGE_VALUE_SUMMARY = "SUMMARY";
	
	// TM Specification fetchNexusTMSpecificsPerfChars - Start
	public static final String OBJECT_SELECTS_FOR_TEST_METHOD_NEXUS_BASICS = "name,revision,current,description,from[pgPrimaryOrganization].to.name," + "attribute[Title]," + "attribute[pgLocalDescription],"  + "attribute[Expiration Date]," + "attribute[pgNexusExternalRef]," + "attribute[Release Phase]," + "attribute[pgAuthoringApplication]";
	// DSM 2022x-05 Req 48479  - START
	public static final String MULTIVALUE_OBJECT_SELECTS_FOR_TEST_METHOD_NEXUS_BASICS ="attribute[pgNexusExternalRef]";
		// DSM 2022x-05 Req 48479  - END
	public static final String OBJECT_SELECTS_FOR_TEST_METHOD_NEXUS_CHAR_TABLE = "id,"+ "attribute[pgCharacteristic]," + "attribute[pgCharacteristicSpecific]," + "attribute[pgUnitOfMeasure]," + "attribute[pgNexusDataType]," + "attribute[pgNexusObjectComponent]," + "attribute[pgNexusTechnique]," + "attribute[pgNexusInstrumentType]," + "attribute[pgNexusParameterValues]," + "attribute[pgNexusValidConvertibleUnits]," + "attribute[pgNexusParameterID]," + "attribute[pgNexusParameterType]," + "attribute[pgNexusParameterListID]," + "attribute[pgNexusParameterListVersionID]," + "attribute[pgNexusParameterReferenceTypeID]," + "attribute[pgNexusTransformationRule]," + "attribute[pgNexusParameterListVariantID]," + "attribute[pgNexusPCParameterId]," + "attribute[pgNexusTestMethodId]," + "attribute[pgNexusTestMethodVerId]," + "attribute[pgNexusGroupTestMethodId],"+"attribute[pgNexusGroupTestMethodVerId]";
	public static final String OBJECT_SELECTS_FOR_TEST_METHOD_NEXUS_ATTRIBUTE_TABLE ="id,"+ "attribute[pgNexusID],"+ "attribute[pgNexusValues],"+ "attribute[pgNexusRefTypeID]," + "attribute[pgNexusDefaultValue]," + "attribute[pgNexusRequired]," + "attribute[pgNexusInstance]," + "attribute[pgNexusSDCID]," + "attribute[pgNexusTestMethodId],"+ "attribute[pgNexusTestMethodVerId]";
	public static final String MULTIVALUE_OBJECT_SELECTS_FOR_TEST_METHOD_NEXUS_CHAR_TABLE ="attribute[pgNexusValidConvertibleUnits]," + "attribute[pgNexusParameterValues]"; 
	public static final String CONST_NEXUS_CHAR_TABLE = "Nexus Characteristics Table";
	public static final String CONST_NEXUS_ATTR_TABLE = "Nexus Attribute Table";
	public static final String CONST_NEXUS_CONDITION_ATTRIBUTE_DETAILS = "NexusCondAttribDetails";
	public static final String CONST_NEXUS_PARAMETER_DETAILS = "NexusParameterDetails";
	public static final String CONST_NEXUS_TEST_METHOD_BASICS = "Test Method Basics";
	public static final String CONST_NEXUS_TM_BASICS = "Basics";
	public static final String CONST_TILDE = "~";
	public static final String CONST_PERFCHAR_ID = "PerfChar id";
	public static final String CONST_TESTMETHOD_ID = "testmethod id";
	public static final String CONST_SELECT = "_Select";
	public static final String ATTR_PG_CHARACTERISTIC_SPECS=PropertyUtil.getSchemaProperty(null,"attribute_pgCharacteristicSpecific");
	public static final String SELECT_ATTRIBUTE_NEXUS_CHARACTERISTICSPECIFICS = "attribute["+ATTR_PG_CHARACTERISTIC_SPECS+"]";
	public static final String ATTR_PG_NEXUS_PC_PARAMETER_ID =PropertyUtil.getSchemaProperty(null,"attribute_pgNexusPCParameterId"); 
	public static final String ATTR_PG_METHOD_SPECIFICS =PropertyUtil.getSchemaProperty(null,"attribute_pgMethodSpecifics"); 
	public static final String SELECT_ATTR_PG_METHOD_SPECIFICS = DomainObject.getAttributeSelect(ATTR_PG_METHOD_SPECIFICS);
	public static final String ATTR_PG_NEXUS_PARAMETER_LISTID=PropertyUtil.getSchemaProperty(null,"attribute_pgNexusParameterListID");
	public static final String ATTR_PG_NEXUS_VALUES=PropertyUtil.getSchemaProperty(null,"attribute_pgNexusValues");
	public static final String ATTR_PG_NEXUS_ID=PropertyUtil.getSchemaProperty(null,"attribute_pgNexusID");
	public static final String SELECT_ATTR_PG_NEXUS_ID =  DomainObject.getAttributeSelect(ATTR_PG_NEXUS_ID);
	public static final String REL_PG_TM_TO_NEXUSPARAMETERS=PropertyUtil.getSchemaProperty(null,"relationship_pgTMToNexusParameters");
	public static final String REL_PG_TM_TO_NEXUSCONDITONATTRIBUTES=PropertyUtil.getSchemaProperty(null,"relationship_pgTMToNexusConditionAttributes");
	public static final String TYPE_PG_NEXUSPARAMETERS=PropertyUtil.getSchemaProperty(null,"type_pgNexusParameters");
	public static final String TYPE_PG_NEXUSCONDITONATTRIBUTES=PropertyUtil.getSchemaProperty(null,"type_pgNexusConditionsAttributes");
	public static final String MESSAGE_PC_LOCAL_DEL = "Please select Local Characteristics for delete";
	public static final String ATTR_PG_NEXUS_PC_PARAMETER_TYPE =PropertyUtil.getSchemaProperty(null,"attribute_pgNexusDataType"); 
	public static final String ATTR_PG_AUTHORING_APP = PropertyUtil.getSchemaProperty("attribute_pgAuthoringApplication");
	public static final String SELECT_ATTRIBUTE_PGAUTHORINGAPPLICATION = DomainObject.getAttributeSelect(ATTR_PG_AUTHORING_APP);
	// TM Specification fetchNexusTMSpecificsPerfChars - End
	
	public static final String SELECT_ATTR_PG_RELEASECRITERIA =  DomainObject.getAttributeSelect(ATTR_PG_RELEASECRITERIA);
	public static final String SELECT_ATTR_PG_NEXUS_PARAMETER_LISTID =  DomainObject.getAttributeSelect(ATTR_PG_NEXUS_PARAMETER_LISTID);
	public static final String PG_NEXUS_PARAM_ID = PropertyUtil.getSchemaProperty(null,"attribute_pgNexusParameterID");
	public static final String SELECT_PG_NEXUS_PARAM_ID =  DomainObject.getAttributeSelect(PG_NEXUS_PARAM_ID);
	public static final String PG_NEXUS_PARAM_TYPE = PropertyUtil.getSchemaProperty(null,"attribute_pgNexusParameterType");
	public static final String SELECT_PG_NEXUS_PARAM_TYPE =  DomainObject.getAttributeSelect(PG_NEXUS_PARAM_TYPE);
	public static final String PG_NEXUS_PARAM_LIST_VAR_ID = PropertyUtil.getSchemaProperty(null,"attribute_pgNexusParameterListVariantID");
	public static final String SELECT_PG_NEXUS_PARAM_LIST_VAR_ID =  DomainObject.getAttributeSelect(PG_NEXUS_PARAM_LIST_VAR_ID);
	public static final String PG_NEXUS_PARAM_LIST_VER_ID = PropertyUtil.getSchemaProperty(null,"attribute_pgNexusParameterListVersionID");
	public static final String SELECT_PG_NEXUS_PARAM_LIST_VER_ID =  DomainObject.getAttributeSelect(PG_NEXUS_PARAM_LIST_VER_ID);
	public static final String PG_NEXUS_PARAM_REF_TYPE_ID = PropertyUtil.getSchemaProperty(null,"attribute_pgNexusParameterReferenceTypeID");
	public static final String SELECT_PG_NEXUS_PARAM_REF_TYPE_ID =  DomainObject.getAttributeSelect(PG_NEXUS_PARAM_REF_TYPE_ID);
	public static final String PG_NEXUS_TRANS_RULE = PropertyUtil.getSchemaProperty(null,"attribute_pgNexusTransformationRule");
	public static final String SELECT_PG_NEXUS_TRANS_RULE =  DomainObject.getAttributeSelect(PG_NEXUS_TRANS_RULE);
	public static final String ATTR_NEXUS_GROUP_TESTMETHOD_ID = PropertyUtil.getSchemaProperty(null,"attribute_pgNexusGroupTestMethodId");
	public static final String SELECT_ATTR_NEXUS_GROUP_TESTMETHOD_ID =  DomainObject.getAttributeSelect(ATTR_NEXUS_GROUP_TESTMETHOD_ID);
	public static final String ATTR_NEXUS_GROUP_TESTMETHOD_VERID = PropertyUtil.getSchemaProperty(null,"attribute_pgNexusGroupTestMethodVerId");
	public static final String SELECT_ATTR_NEXUS_GROUP_TESTMETHOD_VERID =  DomainObject.getAttributeSelect(ATTR_NEXUS_GROUP_TESTMETHOD_VERID);
	public static final String ATTR_NEXUS_TESTMETHOD_VERID = PropertyUtil.getSchemaProperty(null,"attribute_pgNexusTestMethodVerId");
	public static final String SELECT_ATTR_NEXUS_TESTMETHOD_VERID =  DomainObject.getAttributeSelect(ATTR_NEXUS_TESTMETHOD_VERID);
	public static final String ATTR_NEXUS_TESTMETHOD_ID = PropertyUtil.getSchemaProperty(null,"attribute_pgNexusTestMethodId");
	public static final String SELECT_ATTR_NEXUS_TESTMETHOD_ID =  DomainObject.getAttributeSelect(ATTR_NEXUS_TESTMETHOD_ID);
	public static final String ATTR_NEXUS_INSTANCE = PropertyUtil.getSchemaProperty(null,"attribute_pgNexusInstance");
	public static final String SELECT_ATTR_NEXUS_INSTANCE =  DomainObject.getAttributeSelect(ATTR_NEXUS_INSTANCE);
	public static final String ATTR_PG_REPORT_NEAREST = PropertyUtil.getSchemaProperty(null,"attribute_pgReportToNearest");
	public static final String ATTRIBUTE_PG_CSS_TYPE = PropertyUtil.getSchemaProperty(null, "attribute_pgCSSType");
	public static final String SELECT_ATTRIBUTE_PG_CSS_TYPE =  DomainObject.getAttributeSelect(ATTRIBUTE_PG_CSS_TYPE);
	
	// Export -START
	final static String COLUMNTYPE_PROGRAM = "program";
	final static String COLUMNTYPE_PROGRAM_HTML_OUTPUT = "programHTMLOutput";
	final static String COLUMNTYPE_IMAGE = "Image";
	final static String COLUMNTYPE_FILE = "File";
	final static String COLUMNTYPE_ICON = "icon";
	final static String COLUMNTYPE_CHECKBOX = "checkbox";
	final static String COLUMNTYPE_SEPARATOR = "separator";
	final static String SETTING_COLUMNTYPE = "Column Type";
	final static String SETTING_PROGRAM = "program";
	final static String SETTING_FUNCTION = "function";
	final static String SETTING_RANGE_PROGRAM = "Range Program";
	final static String SETTING_RANGE_FUNCTION = "Range Function";
	final static String EXPRESSION_REL = "expression_relationship";
	final static String EXPRESSION_BUS = "expression_businessobject";

	public static final String STR_FIELD_DISPLAY_CHOICES = "field_display_choices";
	public static final String STR_FIELD_CHOICES = "field_choices";

	final static String SETTING_PG_EXP_RANGE_PROGRAM = "pgExportRangeProgram";// Custom Setting to display ranges in
																				// Excel Column
	final static String SETTING_PG_EXP_RANGE_FUNCTION = "pgExportRangeFunction";// Custom Setting to display ranges in
																				// Excel Column
	public static final String CONSTANT_STRING_COMMA = ",";

	public static final String TYPE_PGPKGSTUDYPROTOCOL = PropertyUtil.getSchemaProperty(null,
			"type_pgPKGStudyProtocol");
	public static final String STR_A5_TEAM_TEST = "A5 - Team Test";
	public static final String STR_A6_INVESTIGATIONAL_TEST = "A6 - Investigational Use Consumer Test";
	public static final String STR_A7a_INVESTIGATIONAL_SALES = "A7a - Investigational Use with Sales";
	public static final String STR_A7a1_INVESTIGATIONAL_SALES = "A7a1 - Investigational Use with Sales";
	public static final String STR_A7a2_INVESTIGATIONAL_SALES_SAMPLES = "A7a2 - Investigational Use - NonMarketed Sale Samples";
	public static final String TYPE_CONSUMER_RESEARCH = PropertyUtil.getSchemaProperty(null,
			"type_pgPKGConsumerProposition");
	public static final String STR_A2_GPS_APPROVAL = "A2 - GPS Approval for NRQ Reuse";
	public static final String PICKLIST_SUB_TYPE_PAGE_OBJECT = "pgPicklistSubTypes";
	public static final String TYPE_PG_PL_CONFIGURATION = PropertyUtil.getSchemaProperty(null,
			"type_pgPLConfiguration");
	public static final String STRING_ZERO = "0";
	public static final String SYMB_WILD = "*";
	public static final String VAULT_ESERVICEPRODUCTION = PropertyUtil.getSchemaProperty(null,
			"vault_eServiceProduction");
	public static final String SYMB_ATTRIBUTE_OPEN_SQUARE_BRACKET = "attribute[";
	public static final String ATTRIBUTE_PG_PL_PICKLIST_TYPE_MAPPING_NUMBER = PropertyUtil.getSchemaProperty(null,
			"attribute_pgPLPicklistTypeMappingNumber");
	public static final String SELECT_ATTRIBUTE_PG_PL_PICKLIST_TYPE_MAPPING_NUMBER = SYMB_ATTRIBUTE_OPEN_SQUARE_BRACKET
			+ ATTRIBUTE_PG_PL_PICKLIST_TYPE_MAPPING_NUMBER + "]";
	public static final String STR_BLANK = "BLANK";
	public static final String SYMBOL_UNDER_SCORE = "_";
	public static final String STR_PREFIX_PL = "PL";
	public static final String SYMBOL_TILDE = "~";
	public static final String OBJ_NAME_PG_UNIT_OF_MEASURE_WD = "pgPLIUnitofMeasureWD";
	public static final String INPUT_TYPE_LISTBOX = "listbox";
	public static final String RANGE_BLANK = " <BLANK>";
	public static final String RANGE_VALUE_BLANK = "BLANK";
	public static final String SYM_PIPE_SEPERATOR = "|";
	public static final String TYPE_PG_GLOBALFORM = PropertyUtil.getSchemaProperty(null, "type_pgGlobalForm");
	public static final String ATTRIBUTE_MARKETING_NAME = PropertyUtil.getSchemaProperty(null,
			"attribute_MarketingName");
	public static final String TYPE_PGPICKLISTITEM = PropertyUtil.getSchemaProperty(null, "type_pgPicklistItem");
	public static final String TYPE_PG_PLI_PLATFORM = PropertyUtil.getSchemaProperty(null, "type_pgPLIPlatform");
	public static final String TYPE_PG_PPM_PHRASE = PropertyUtil.getSchemaProperty(null, "type_pgPPMPhrase");
	public static final String TYPE_PG_PLI_CHASSIS = PropertyUtil.getSchemaProperty(null, "type_pgPLIChassis");
	public static final String STR_EXISTS = "Exists";
	public static final String ACTIVE = "Active";
	public static final String INACTIVE = "Inactive";
	public static final String ATTR_PG_NEXUS_PARAMETER_ID = PropertyUtil.getSchemaProperty(null, "attribute_pgNexusPCParameterId"); 
	public static final String SELECT_ATTR_PG_NEXUS_PARAMETER_ID = "attribute["+ATTR_PG_NEXUS_PARAMETER_ID+"]";	
	public static final String ATTR_PG_NEXUS_PARAMETER_LIST_ID = PropertyUtil.getSchemaProperty(null, "attribute_pgNexusParameterListID"); 
	public static final String SELECT_ATTRIBUTE_SHARED_TABLE_CHAR_SEQUENCE = "attribute["+ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE+"]";
	public static final String INTEGER = "integer";
	public static final String SELECT_ATTR_PG_NEXUS_PARAMETER_LIST_ID = "attribute["+ATTR_PG_NEXUS_PARAMETER_LIST_ID+"]";
	public static final String SELECT_ATTR_PG_INHERITED_FROM_PLATFORM = "attribute["+ATTRIBUTE_PG_INHERITED_FROM_PLATFORM+"]";
	// Export -END
	public static final String CONST_CHARACTERISTIC_NAME = "CharacteristicNew";
	public static final String ATTR_PG_NEXUS_TESTMETHOD_ID = PropertyUtil.getSchemaProperty(null, "attribute_pgNexusTestMethodId");
	public static final String ATTR_PG_NEXUS_TESTMETHOD_VERSIONID = PropertyUtil.getSchemaProperty(null, "attribute_pgNexusTestMethodVerId");
	public static final String CONST_NEXUS_TM_ID_LIST = "TestMethodIDList";
	public static final String CONST_NEXUS_UNIQUE_GROUP_ID = "NexusUniqueGroupId"; 
	
	public static final String COL_ACTION_REQUIRED = "Action Required";
	public static final String COL_REPORT_TYPE = "Report Type";
	public static final String COL_LOWER_SPECIFICATION_LIMIT = "Lower Specification Limit";
	public static final String COL_UPPER_SPECIFICATION_LIMIT = "Upper Specification Limit";
	public static final String COL_TARGET = "Target";
	public static final String COL_LOWER_TARGET = "Lower Target";
	public static final String COL_UPPER_TARGET = "Upper Target";
	public static final String COL_LOWER_ROUTINE_RELEASE_LIMIT = "Lower Routine Release Limit";
	public static final String COL_UPPER_ROUTINE_RELEASE_LIMIT = "Upper Routine Release Limit";
	
	public static final String VAL_REPORT = "REPORT";
	public static final String VAL_SUMMARY = "SUMMARY";
	public static final String VAL_ATTRIBUTE = "ATTRIBUTE";
	public static final String VAL_VARIABLE = "VARIABLE";
	public static final String MSG_PREFIX_SEQ_NO = "Seq No ";
	public static final String MSG_COLON = " : ";
	public static final String MSG_ACTION_REQUIRED = "Column 'Action Required' is mandatory\n";
	public static final String MSG_REPORT_TYPE = "Please select Report Type value as ATTRIBUTE or VARIABLE\n";
	public static final String MSG_TARGET_LIMIT = "Please select value for Target or Limit related columns\n";
	public static final String MSG_LSL_USL= "At least one of the columns must have value : 'Lower Specification Limit' or 'Upper Specification Limit'\n";
	public static final String MSG_ERROR_TARGET_COLS = "Target can have value only when Lower and Upper Target are blank\n";
	public static final String MSG_LESS_THAN= " must be less than ";
	
	public static final String NEXUS_PARAM_ATTR_COL_LIST = "Group Test Method ID,Group Test Method Version ID,Test Method ID,Test Method Version ID,Parameter ID,Parameter Type,Parameter List ID,Parameter List Variant ID,Parameter Reference Type ID,Transformation Rule";
	public static final String KEY_PC_TYPE = "Perf_Char_Type";
	
	public static final String SELECTABLE_PERCHARS_TO_TESTMETHOD_AUTH_APPLICATION="to[Reference Document|from.type=='Test Method Specification'].from.attribute[pgAuthoringApplication]";
	public static final String STR_MQL_CREATE_GET_TEST_METHODS = "temp query bus $1 \"$2\" $3  where $4 select $5 $6 dump $7";
	public static final String STR_MQL_COL_DATA_PRINT_OBJ = "print bus $1 select $2 dump $3";
	public static final String STR_MQL_PREFIX_TEMP_QUERY_BUS = "temp query bus ";
	public static final String STR_MQL_PREFIX_TEMP_QUERY_BUS_PICKLIST = "temp query bus pgPicklistItem ";
	public static final String WHERE_CLAUSE_CURRENT_ACTIVE_EXISTS = " * * where \'(current != New && current != Inactive)\' !expand select id dump |";
	public static final String WHERE_CLAUSE_CURRENT_ACTIVE_EXISTS_PICKLIST= " * where \'(current != New && current != Inactive)\' !expand select id dump |";
	public static final String WHERE_CLAUSE_CURRENT_ALL = " * * where \'(current != New)\' !expand select id dump |";
	public static final String WHERE_CLAUSE_CURRENT_ALL_PICKLIST = " * where \'(current != New)\' !expand select id dump |";
	public static final String WHERE_CLAUSE_CURRENT_INACTIVE = " * * where \'(current == Inactive)\' !expand select id dump |";
	public static final String WHERE_CLAUSE_CURRENT_INACTIVE_PICKLIST = " * where \'(current == Inactive)\' !expand select id dump |";
	public static final String WHERE_CLAUSE_ATTR_PROD_TYPE = " * where \"attribute[pgProductType] ~~ \'*Formulated*\' && current == Active\" !expand select id dump |";
	public static final String WHERE_CLAUSE_ATTR_PROD_TYPE_PICKLIST = "' * * where \"attribute[pgProductType].value ~~ \'*Formulated*\' && current == Active\" !expand select id dump |";
	
	public static final String KEY_CLONED_PC_ID = "cloneObjectId";
	public static final String KEY_NO_OF_CLONES = "noOfClonesRequired";
	// DSM DEFECT Id: 57037 Data Types Captured in Nexus Characteristics Table Should Not Be Visible -START
	public static final String SELECT_ATTRIBUTE_PG_NEXUS_DATA_TYPE = DomainObject.getAttributeSelect(ATTR_PG_NEXUS_PC_PARAMETER_TYPE);
	public static final String ATTR_PG_NEXUS_DATA_TYPE_PARAMETER_LIST = PropertyUtil.getSchemaProperty(null,"attribute_pgNexusParamDataTypeList");
	public static final String TYPE_PGCONFIGURATIONADMIN =  PropertyUtil.getSchemaProperty(null,"type_pgConfigurationAdmin");
	public static final String CONST_NEXUSDSMINTGRATION_CONFIG = "pgNexusDSMIntegration";
	public static final String CONSTANT_STRING_HYPHEN = "-";
	// DSM DEFECT Id: 57037 Data Types Captured in Nexus Characteristics Table Should Not Be Visible -END
}
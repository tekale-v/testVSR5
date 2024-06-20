/*
**   PGStructuredATSConstants
*    This file contains constants which can be reused across SATS widgets
** 
**   Copyright (c) 1992-2021 Dassault Systemes.
**   All Rights Reserved.
**   This program contains proprietary and trade secret information of MatrixOne,
**   Inc.  Copyright notice is precautionary only
**   and does not evidence any actual or intended publication of such program
** 
*/

package com.pg.widgets.structuredats;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.PropertyUtil;

public class PGStructuredATSConstants {

	public static final String TYPE_ASSEMBLED_PRODUCT_PART = PropertyUtil.getSchemaProperty(null,
			"type_pgAssembledProductPart");
	public static final String TYPE_FORMULATION_PART = PropertyUtil.getSchemaProperty(null, "type_FormulationPart");
	public static final String TYPE_FORMULATION_PHASE = PropertyUtil.getSchemaProperty(null, "type_FormulationPhase");
	public static final String TYPE_FORMULATION_PROCESS = PropertyUtil.getSchemaProperty(null, "type_FormulationProcess");
	public static final String TYPE_RAW_MATERIAL = PropertyUtil.getSchemaProperty(null, "type_RawMaterial");
	public static final String TYPE_PLANT = PropertyUtil.getSchemaProperty(null, "type_Plant");
	public static final String TYPE_SATS = PropertyUtil.getSchemaProperty(null, "type_pgStructuredATS");
	public static final String TYPE_PARENT_SUB = PropertyUtil.getSchemaProperty(null, "type_ParentSub");
	public static final String TYPE_PG_PERFORMANCE_CHARACTERSTIC = PropertyUtil.getSchemaProperty(null,"type_pgPerformanceCharacteristic");
	public static final String POLICY_PARENT_SUB = PropertyUtil.getSchemaProperty(null, "policy_ParentSub");
	public static final String TYPE_PARENT_SUB_REGISTRY_NAME = "type_ParentSub";
	public static final String POLICY_PARENT_SUB_REGISTRY_NAME = "policy_ParentSub";
	
	public static final String RELATIONSHIP_FBOM = PropertyUtil.getSchemaProperty(null, "relationship_FBOM");
	public static final String RELATIONSHIP_EBOM_SUBSTITUTE  = PropertyUtil.getSchemaProperty(null, "relationship_EBOMSubstitute");
	public static final String RELATIONSHIP_FBOM_SUBSTITUTE  = PropertyUtil.getSchemaProperty(null, "relationship_FBOMSubstitute");
	public static final String RELATIONSHIP_PLANNED_FOR = PropertyUtil.getSchemaProperty(null, "relationship_PlannedFor");
	public static final String RELATIONSHIP_AUTHORIZED_TEMPORARY_SPECIFICATION = PropertyUtil.getSchemaProperty(null, "relationship_AuthorizedTemporarySpecification");
	public static final String REL_PG_ATS_CONTEXT = PropertyUtil.getSchemaProperty(null,"relationship_pgATSContext");
	public static final String REL_PG_ATS_OPERATION = PropertyUtil.getSchemaProperty(null,"relationship_pgATSOperation");
	
	public static final String SELECT_RELATED_FORMULATION_PROCESS = "from["+RELATIONSHIP_PLANNED_FOR+"].to["+TYPE_FORMULATION_PROCESS+"].id";
	public static final String SELECT_RELATED_FORMULATION_PHASE = "from["+RELATIONSHIP_FBOM+"].to["+TYPE_FORMULATION_PHASE+"].id";
	public static final String SELECT_RELATED_FBOM_OBJ_ID = "from["+RELATIONSHIP_FBOM+"].to.id";
	public static final String SELECT_RELATED_FORMULATION_PHASE_FOR_CHILDREN = "to["+RELATIONSHIP_FBOM+"].from["+TYPE_FORMULATION_PHASE+"].id";
	public static final String SELECT_RELATED_FORMULATION_PHASE_FOR_SUBSTITUTES = "to["+RELATIONSHIP_FBOM+"].from["+TYPE_PARENT_SUB+"].to["+RELATIONSHIP_FBOM_SUBSTITUTE+"].fromrel.from.id";
	public static final String SELECT_RELATED_FBOM_SUBSTITUTES_IDS = "from["+RELATIONSHIP_FBOM+"].frommid["+RELATIONSHIP_FBOM_SUBSTITUTE+"].to["+TYPE_PARENT_SUB+"].from["+RELATIONSHIP_FBOM+"].to.id";
	public static final String SELECT_FBOM_REL_ID = "to["+RELATIONSHIP_FBOM+"].from["+TYPE_PARENT_SUB+"].to["+RELATIONSHIP_FBOM_SUBSTITUTE+"].fromrel.id";
	public static final String SELECT_EBOM_REL_ID = "to["+RELATIONSHIP_EBOM_SUBSTITUTE+"].fromrel["+DomainConstants.RELATIONSHIP_EBOM+"].id";
	public static final String SELECT_RELATED_PARENT_SUB_FOR_ATS_CONTEXT = "fromrel.to.id"; 
	public static final String SELECT_ATS_CONTEXT_RELID = "tomid["+REL_PG_ATS_CONTEXT+"].id";
	public static final String SELECT_ATS_OPERATION_RELID = "fromrel["+REL_PG_ATS_OPERATION+"].id";
	public static final String SELECT_RELATED_ATS_OPERATION_REL_IDS = "tomid["+REL_PG_ATS_CONTEXT+"].fromrel["+REL_PG_ATS_OPERATION+"].id";
	public static final String SELECT_RELATED_ATS_OPERATION_REL_IDS_FBOMSUB = "to["+TYPE_PARENT_SUB+"].to["+REL_PG_ATS_CONTEXT+"].fromrel["+REL_PG_ATS_OPERATION+"].id";
	public static final String SELECT_ATS_OPERATION_RELID_FOR_FOP = "fromrel["+REL_PG_ATS_OPERATION+"].to["+TYPE_PARENT_SUB+"].from["+REL_PG_ATS_OPERATION+"].id";
	public static final String SELECT_EBOM_SUB_RELID = "frommid["+RELATIONSHIP_EBOM_SUBSTITUTE+"].id";
	public static final String SELECT_FBOM_SUB_RELID = "frommid["+RELATIONSHIP_FBOM_SUBSTITUTE+"].id";
	public static final String SELECT_FBOM_RELIDS_FROM_PARENT_SUB = "to["+TYPE_PARENT_SUB+"].from["+RELATIONSHIP_FBOM+"].id";
	public static final String SELECT_FBOM_SUB_OBJ_IDS= "to["+TYPE_PARENT_SUB+"].from["+RELATIONSHIP_FBOM+"].to.id";
	public static final String SELECT_ATS_CONTEXT_RELID_FOR_FBOM_SUBSTITUTES = "to["+TYPE_PARENT_SUB+"].to["+REL_PG_ATS_CONTEXT+"].id";
	public static final String SELECT_RELATED_SATS_ID = "fromrel["+REL_PG_ATS_OPERATION+"].from["+TYPE_SATS+"].id";
	public static final String SELECT_RELATED_SATS_NAME = "fromrel["+REL_PG_ATS_OPERATION+"].from["+TYPE_SATS+"].name";
	public static final String SELECT_RELATED_OBJS_FOR_SATS = "fromrel["+REL_PG_ATS_OPERATION+"].to.id";
	public static final String SELECT_RELATED_OBJS_FOR_SATS_PARENT_SUB = "fromrel["+REL_PG_ATS_OPERATION+"].to["+TYPE_PARENT_SUB+"].id";
	public static final String SELECT_RELATED_OBJS_FOR_SATS_PERF_CHAR = "fromrel["+REL_PG_ATS_OPERATION+"].to["+TYPE_PG_PERFORMANCE_CHARACTERSTIC+"].id";
	public static final String SELECT_RELATED_OBJS_FOR_SATS_FOR_FOP = "fromrel["+REL_PG_ATS_OPERATION+"].to["+TYPE_PARENT_SUB+"].from["+REL_PG_ATS_OPERATION+"].to.id";
	public static final String SELECT_RELATED_ATS_OPERATION_IDS = "from["+REL_PG_ATS_OPERATION+"].id";
	public static final String SELECT_RELATED_ATS_CONTEXT_IDS = "frommid["+REL_PG_ATS_CONTEXT+"].id";
	public static final String SELECT_TO_REL_ID = "torel.id";
	public static final String SELECT_RELATED_PHASE_FOR_FOP = "from["+RELATIONSHIP_PLANNED_FOR+"].to["+TYPE_FORMULATION_PROCESS+"].from["+RELATIONSHIP_FBOM+"].to["+TYPE_FORMULATION_PHASE+"].id";
	public static final String SELECT_RELATED_EBOM_OBJ_IDS = "from["+DomainConstants.RELATIONSHIP_EBOM+"].to.id"; 
	public static final String SELECT_RELATED_EBOM_SUB_OBJ_IDS = "from["+DomainConstants.RELATIONSHIP_EBOM+"].frommid["+RELATIONSHIP_EBOM_SUBSTITUTE+"].to.id"; 
	public static final String SELECT_RELATED_EBOM_SUB_OBJ_IDS_FROM_APP = "frommid["+RELATIONSHIP_EBOM_SUBSTITUTE+"].to.id";
	public static final String SELECT_RELATED_FOP_FOR_FBOM = "from["+TYPE_FORMULATION_PHASE+"].to["+RELATIONSHIP_FBOM+"].from["+TYPE_FORMULATION_PROCESS+"].to["+RELATIONSHIP_PLANNED_FOR+"].from["+TYPE_FORMULATION_PART+"].name";
	public static final String SELECT_RELATED_FOP_FOR_FBOM_SUB = "fromrel["+RELATIONSHIP_FBOM+"].from["+TYPE_FORMULATION_PHASE+"].to["+RELATIONSHIP_FBOM+"].from["+TYPE_FORMULATION_PROCESS+"].to["+RELATIONSHIP_PLANNED_FOR+"].from["+TYPE_FORMULATION_PART+"].name";
	public static final String SELECT_RELATED_APP_FOR_EBOM_SUB = "fromrel["+DomainConstants.RELATIONSHIP_EBOM+"].from.name";
	public static final String SELECT_ATS_CONTEXT_RELID_FOR_PHASE = "to["+REL_PG_ATS_CONTEXT+"].id";
	
	public static final String ATTRIBUTE_PGSATSWHEREUSEDSELECTEDITEMS = PropertyUtil.getSchemaProperty(null,
			"attribute_pgSATSWhereUsedSelectedItems");
	public static final String ATTRIBUTE_PGISAUTHORIZEDTOPRODUCE = PropertyUtil.getSchemaProperty(null,
			"attribute_pgIsAuthorizedtoProduce");
	public static final String SELECT_ATTRIBUTE_PGISAUTHORIZEDTOPRODUCE = DomainObject
			.getAttributeSelect(ATTRIBUTE_PGISAUTHORIZEDTOPRODUCE);
	public static final String ATTRIBUTE_PGISAUTHORIZEDTOUSE = PropertyUtil.getSchemaProperty(null,
			"attribute_pgIsAuthorizedtoUse");
	public static final String SELECT_ATTRIBUTE_PGISAUTHORIZEDTOUSE = DomainObject
			.getAttributeSelect(ATTRIBUTE_PGISAUTHORIZEDTOUSE);
	public static final String ATTRIBUTE_PGISAUTHORIZEDTOVIEW = PropertyUtil.getSchemaProperty(null,
			"attribute_pgIsAuthorizedtoView");
	public static final String SELECT_ATTRIBUTE_PGISAUTHORIZEDTOVIEW = DomainObject
			.getAttributeSelect(ATTRIBUTE_PGISAUTHORIZEDTOVIEW);
	public static final String STR_SCHEMA_TYPE = "Type";
	public static final String ATTRIBUTE_RELEASE_PHASE = PropertyUtil.getSchemaProperty(null, "attribute_ReleasePhase");
	public static final String SELECT_ATTRIBUTE_RELEASE_PHASE = DomainObject.getAttributeSelect(ATTRIBUTE_RELEASE_PHASE);
	public static final String ATTRIBUTE_EXPIRATION_DATE = PropertyUtil.getSchemaProperty(null, "attribute_ExpirationDate");
	public static final String SELECT_ATTRIBUTE_EXPIRATION_DATE = DomainObject.getAttributeSelect(ATTRIBUTE_EXPIRATION_DATE);
	public static final String ATTRIBUTE_AUTHORING_APPLICATION = PropertyUtil.getSchemaProperty(null, "attribute_pgAuthoringApplication");
	public static final String SELECT_ATTRIBUTE_AUTHORING_APPLICATION = DomainObject.getAttributeSelect(ATTRIBUTE_AUTHORING_APPLICATION);
	
	public static final String ATTRIBUTE_TARGET_WEIGHT_DRY = PropertyUtil.getSchemaProperty(null, "attribute_TargetWeightDry");
	public static final String SELECT_ATTRIBUTE_TARGET_WEIGHT_DRY = DomainObject.getAttributeSelect(ATTRIBUTE_TARGET_WEIGHT_DRY);
	public static final String ATTRIBUTE_TARGET_WEIGHT_WET = PropertyUtil.getSchemaProperty(null, "attribute_TargetWeightWet");
	public static final String SELECT_ATTRIBUTE_TARGET_WEIGHT_WET = DomainObject.getAttributeSelect(ATTRIBUTE_TARGET_WEIGHT_WET);
	public static final String ATTRIBUTE_PROCESSING_NOTE = PropertyUtil.getSchemaProperty(null, "attribute_ProcessingNote");
	public static final String SELECT_ATTRIBUTE_PROCESSING_NOTE = DomainObject.getAttributeSelect(ATTRIBUTE_PROCESSING_NOTE);
	public static final String ATTRIBUTE_TOTAL = PropertyUtil.getSchemaProperty(null, "attribute_Total");
	public static final String SELECT_ATTRIBUTE_TOTAL = DomainObject.getAttributeSelect(ATTRIBUTE_TOTAL);
	public static final String ATTRIBUTE_LOSS = PropertyUtil.getSchemaProperty(null, "attribute_Loss");
	public static final String SELECT_ATTRIBUTE_LOSS = DomainObject.getAttributeSelect(ATTRIBUTE_LOSS);
	public static final String ATTRIBUTE_MAXIMUM_ACTUAL_WEIGHT_WET = PropertyUtil.getSchemaProperty(null, "attribute_pgMaximumActualWeightWet");
	public static final String SELECT_ATTRIBUTE_MAXIMUM_ACTUAL_WEIGHT_WET = DomainObject.getAttributeSelect(ATTRIBUTE_MAXIMUM_ACTUAL_WEIGHT_WET);
	public static final String ATTRIBUTE_MINIMUM_ACTUAL_WEIGHT_WET = PropertyUtil.getSchemaProperty(null, "attribute_pgMinimumActualWeightWet");
	public static final String SELECT_ATTRIBUTE_MINIMUM_ACTUAL_WEIGHT_WET = DomainObject.getAttributeSelect(ATTRIBUTE_MINIMUM_ACTUAL_WEIGHT_WET);
	public static final String ATTRIBUTE_PGSATSPCCONTEXT = PropertyUtil.getSchemaProperty(null, "attribute_pgSATSPCContext");
	public static final String SELECT_ATTRIBUTE_PGSATSPCCONTEXT = DomainObject.getAttributeSelect(ATTRIBUTE_PGSATSPCCONTEXT);
	public static final String ATTRIBUTE_PGREPLACEDORMODIFIED = PropertyUtil.getSchemaProperty(null, "attribute_pgReplacedOrModified");
	public static final String SELECT_ATTRIBUTE_PGREPLACEDORMODIFIED = DomainObject.getAttributeSelect(ATTRIBUTE_PGREPLACEDORMODIFIED);
	public static final String ATTRIBUTE_PGMINACTUALPERCENWET = PropertyUtil.getSchemaProperty(null, "attribute_pgMinActualPercenWet");
	public static final String SELECT_ATTRIBUTE_PGMINACTUALPERCENWET = DomainObject.getAttributeSelect(ATTRIBUTE_PGMINACTUALPERCENWET);
	public static final String ATTRIBUTE_PGMAXACTUALPERCENWET = PropertyUtil.getSchemaProperty(null, "attribute_pgMaxActualPercenWet");
	public static final String SELECT_ATTRIBUTE_PGMAXACTUALPERCENWET = DomainObject.getAttributeSelect(ATTRIBUTE_PGMAXACTUALPERCENWET);
	public static final String ATTRIBUTE_BM_PHASE = PropertyUtil.getSchemaProperty(null, "attribute_pgBMPhase");
	public static final String SELECT_ATTRIBUTE_BM_PHASE = DomainObject.getAttributeSelect(ATTRIBUTE_BM_PHASE);
	public static final String ATTRIBUTE_BALANCING_MATERIAL = PropertyUtil.getSchemaProperty(null, "attribute_pgBalancingMaterial");
	public static final String SELECT_ATTRIBUTE_BALANCING_MATERIAL = DomainObject.getAttributeSelect(ATTRIBUTE_BALANCING_MATERIAL);
	public static final String ATTRIBUTE_MAX_FOP = PropertyUtil.getSchemaProperty(null, "attribute_Max");
	public static final String SELECT_ATTRIBUTE_MAX_FOP = DomainObject.getAttributeSelect(ATTRIBUTE_MAX_FOP);
	public static final String ATTRIBUTE_MIN_FOP = PropertyUtil.getSchemaProperty(null, "attribute_Min");
	public static final String SELECT_ATTRIBUTE_MIN_FOP = DomainObject.getAttributeSelect(ATTRIBUTE_MIN_FOP);
			
	public static final String PREFIX_ATTRIBUTE_SELECT = "attribute[";
	public static final String SUFFIX_ATTRIBUTE_SELECT = "]";
	public static final String DISP_MIN_PER = "MinPer";
	public static final String DISP_MAX_PER = "MaxPer";
	public static final String DISP_QUANTITY = "Quantity";
	public static final String DISP_PRIMARY_QUANTITY = "PrimaryQuantity";
	public static final String DISP_SUB_QUANTITY = "SubstituteQuantity";
	public static final String DISP_WETWEIGHTMIN = "WetWeightMin";
	public static final String DISP_WETWEIGHTMAX = "WetWeightMax";
	public static final String DISP_QUANTITYWETPER = "QuantityWetPer";
	public static final String DISP_TARGETDRYWEIGHT = "TargetDryWeight";
	public static final String DISP_TARGETWETWEIGHT = "TargetWetWeight";
	public static final String DISP_DRYPER = "DryPer";
	public static final String DISP_PROCESSINGNOTE = "ProcessingNote";
	public static final String DISP_BM_PHASE = "BMPhase";
	public static final String DISP_BALANCINGMATERIAL = "BalancingMaterial";
	public static final String DISP_PROCESS_LOSS_PER = "ProcessingLossPer";
	public static final String OPR_BALANCINGMATERIAL = "addBalacingMaterial";
	
	public static final String PREFIX_CTX_FOP = "CTXFOP-";
	public static final String PREFIX_CTX_PHASE = "CTXPHASE-";
	public static final String PREFIX_CTX_FOP_ID = "ctxfop";
	public static final String PREFIX_ATS = "ATS-";
	public static final String KEY_AFFECTED_PART = "AffectedPart";
	public static final String KEY_RELATIONSHIP_ID = "relationshipId";
	public static final String KEY_RELATIONSHIP_TYPE = "relationshipType";
	public static final String KEY_ATS_CONTEXT_RELID = "ATSContextRelId";
	public static final String KEY_ATS_OPERATION_RELID = "ATSOperationRelId";
	public static final String KEY_FBOM_RELID = "FBOM_RelId";
	
	public static final String RANGE_PRODUCTION = "Production";
	public static final String VALUE_TRUE = "TRUE";
	public static final String VALUE_FALSE = "FALSE";
	public static final String KEY_OUTPUT = "output";
	public static final String KEY_DISPLAY_TYPE = "displayType";
	public static final String KEY_OBJ_SELECTS = "objectSelects";
	public static final String KEY_CONSUMING_FORMULA = "ConsumingFormula";
	public static final String KEY_SEARCH_MATERIAL = "SearchMaterial";
	public static final String KEY_SEARCH_PLANT = "SearchPlant";
	public static final String KEY_AUTHORIZEDTOPRODUCE = "AuthorizedToProduce";
	public static final String KEY_AUTHORIZEDTOUSE = "AuthorizedToUse";
	public static final String KEY_ALTERNATES = "Alternates";
	public static final String KEY_OPERATION = "Operation";
	public static final String KEY_ALLSMALL_OPERATION = "operation";
	
	public static final String KEY_DENIED = "DENIED";
	public static final String KEY_ACCESS_DENIED = "#DENIED!";
	public static final String VALUE_NO_ACCESS = "No Access";
	public static final String KEY_PRIMARY = "Primary";
	public static final String KEY_HIERARCHY = "hierarchy";
	public static final String KEY_GROUP_REL = "groupRel";
	public static final String STRING_NULL = "null";
	public static final String KEY_DATA = "data";
	public static final String KEY_ATTRIBUTES = "attributes";
	public static final String KEY_CONNECTIONS = "connections";
	public static final String KEY_OBJECT_SELECTS = "ObjectSelects";
	public static final String KEY_IS_FROM= "isFrom";
	public static final String KEY_REL_TYPE = "relType";
	public static final String KEY_ACTION = "action";
	public static final String ACTION_CONNECT = "connect";
	public static final String ACTION_DISCONNECT = "disconnect";
	public static final String ACTION_REMOVE = "remove";
	public static final String ACTION_ADD = "add";
	public static final String ACTION_CAP_ADD = "Add";
	public static final String ACTION_MODIFY = "modify";
	public static final String ACTION_DELETE = "delete";
	public static final String KEY_STATUS = "status";
	public static final String VALUE_SUCCESS = "success";
	public static final String KEY_REL_ID = "relId";
	public static final String KEY_TARGET_ID = "targetId";
	public static final String KEY_REL_ATTRIBUTES = "relAttributes";
	public static final String KEY_TARGET_DATA = "targetData";
	public static final String KEY_SOURCE_DATA = "sourceData";
	public static final String KEY_SOURCE_ID = "sourceId";
	public static final String KEY_PARENT_NAME = "parentName";
	public static final String KEY_PARENT_SUB_ID = "parentSubId";
	public static final String SEPARATOR_FOR_SUBSTITUTES = "_";
	public static final String KEY_PARENT_TYPE = "parentType";
	public static final String KEY_ADJUST = "adjust";
	
	public static final String EXCEPTION_MESSAGE_SATS_WHEREUSED = "Exception in PGStructuredATSWhereUsedUtil";
	public static final String EXCEPTION_MESSAGE_SATS_BOM_REPLACE = "Exception in PGStructuredATSReplaceOperationsUtil";
	public static final String EXCEPTION_MESSAGE_SATS_BOM_DATA = "Exception in PGNWStructuredATSBOMDataUtil";
	public static final String EXCEPTION_MESSAGE_SATS_PLANT_REFRESH = "Exception in PGStructuredATSRefreshPlants";
	public static final String EXCEPTION_MESSAGE_SATS_ALTERNATE_UTIL = "Exception in PGStructuredATSAlternateUtil";
	public static final String EXCEPTION_MESSAGE_SATS_PERFORMANCE_UTIL = "Exception in PGStructuredATSPerformanceUtil";
	public static final String EXCEPTION_MESSAGE_SATS_REVISE_UTIL = "Exception in PGStructuredATSReviseUtil";
	public static final String EXCEPTION_MESSAGE_SATS_WHEREUSED_DELETE = "Exception in PGStructuredATSWhereUsedDeleteUtil";
	public static final String ERROR_MSG_SELECTED_FORMULAS = "Selected Materials in the Formula(s) ";
	public static final String ERROR_MSG_FORMULAS_REPLACED = " are already replaced in one of the following ATS(s): ";
	public static final String ERROR_MSG_REVISE_LAST_REVISION = "Unable to revise the selected ATS object, since it is not the last revision";
	
	public static final String REL_PG_ATSCONTEXT  = PropertyUtil.getSchemaProperty(null,"relationship_pgATSContext");
	public static final String ATTRIBUTE_PGSTRUCTUREATSACTION = PropertyUtil.getSchemaProperty(null,"attribute_pgStructuredATSAction");
	
	public static final String VALUE_BOM = "BOM";
	public static final String VALUE_SUBSTITUTE = "Substitute";
	public static final String VALUE_ALTERNATE = "Alternate";
	public static final String STRING_OBJECT = "ObjectId";
	public static final String STATUS_ERROR = "error";
	public static final String EXCEPTION_MESSAGE = "Exception in PGStructuredATSCreateEditUtil";
	public static final String TEMPLATE_NAME = "Fast track";
	public static final String CHANGE_ORDER = "CreateNew";
	public static final String STRING_MESSAGE = "message";
	public static final String STRING_OK = "OK";
	public static final String SATS_REVISION = "001";
	public static final String PARENT_SUB_REVISION = "0";
	public static final String VALUE_LPD_APOLLO = "LPD";

	public static final String REL_AUTHORISEDTEMPORARYSPECIFICATION = PropertyUtil
			.getSchemaProperty(null,"relationship_AuthorizedTemporarySpecification");
	public static final String TYPE_PG_STRUCTURED_ATS = PropertyUtil.getSchemaProperty(null,"type_pgStructuredATS");
	public static final String POLICY_PG_STRUCTURED_ATS = PropertyUtil.getSchemaProperty(null,"policy_pgStructuredATS");
	public static final String TYPE_PG_RAW_MATERIAL = PropertyUtil.getSchemaProperty(null,"type_pgRawMaterial");
	public static final String TYPE_PG_ARMP = PropertyUtil.getSchemaProperty(null,"type_pgAncillaryRawMaterialPart");
	public static final String TYPE_PG_MASTER_RAW_MATERIAL = PropertyUtil.getSchemaProperty(null,"type_pgMasterRawMaterial");
	public static final String RELATIONSHIP_PGPDTEMPLATES_TO_PGPLISEGMENT = PropertyUtil
			.getSchemaProperty(null,"relationship_pgPDTemplatestopgPLISegment");
	public static final String REL_TEMPLATE = PropertyUtil.getSchemaProperty(null,"relationship_Template");
	public static final String REL_PG_PRIMARY_ORGANIZATION = PropertyUtil
			.getSchemaProperty(null,"relationship_pgPrimaryOrganization");
	public static final String REL_PROTECTED_ITEM = PropertyUtil.getSchemaProperty(null,"relationship_ProtectedItem");
	public static final String TYPE_PG_PLI_SEGMENT = PropertyUtil.getSchemaProperty(null,"type_pgPLISegment");
	public static final String TYPE_TEMPLATE = PropertyUtil.getSchemaProperty(null,"type_Template");
	public static final String TYPE_PG_PLI_ORGANIZATION_CHANGE_MANAGEMENT = PropertyUtil
			.getSchemaProperty(null,"type_pgPLIOrganizationChangeManagement");
	public static final String TYPE_IP_CONTROL_CLASS = PropertyUtil.getSchemaProperty(null,"type_IPControlClass");
	public static final String TYPE_PG_AUTHORIZED_CONFIGURATION_STANDARD = PropertyUtil
			.getSchemaProperty(null,"type_pgAuthorizedConfigurationStandard");

	public static final String TYPE_PG_AUTHORIZED_TEMPORARY_SPECIFICATION = "type_pgAuthorizedTemporarySpecification";
	public static final String REVISION_NUMBER_GENERATOR = "A";

	public static final String REL_CHANGE_INSTANCE = PropertyUtil.getSchemaProperty(null,"relationship_ChangeInstance");
	public static final String STRING_Denied="#denied!";
	public static final String STRING_NoAccess="No Access";
	
	public static final String KEY_RELATIONSHIP = "relationship";
	public static final String KEY_LEVEL = "level";
	public static final String STRING_OBJ = "objectid";
	
	public static final String STRING_CHANGETEMPLATE = "changeTemplate";
    public static final String STRING_CHANGETEMPLATEID = "changeTemplateID";
    public static final String STRING_CO = "CO";
    public static final String STRING_COID = "COID";
    public static final String STRING_CHANGEACTIONID = "changeActionId";
    public static final String STRING_CREATENEW = "CreateNew";
    public static final String STRING_PLANT_ID = "plantid";
	public static final String VALUE_FAILED = "failure"; 
    public static final String STRING_CHANGEACTION = "changeAction";
	public static final String  RELATIONSHIP_MANUFACTURINGRESPONSIBILITY=PropertyUtil.getSchemaProperty("relationship_ManufacturingResponsibility");
	public static final String RELATIONSHIP_ALTERNATE  = PropertyUtil.getSchemaProperty(null, "relationship_Alternate");
	public static final String STRING_ALTERNATE  = "Alternate";
	public static final String POLICY_EXTENDED_DATA= PropertyUtil.getSchemaProperty(null,"policy_Characteristic");
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
	public static final String ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE = PropertyUtil.getSchemaProperty("attribute_SharedTableCharacteristicSequence");
	public static final String SELECT_ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE = (new StringBuilder()).append("attribute[").append(ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE).append("]").toString();
	public static final String TYPE_PG_STABILITY_RESULTS = PropertyUtil.getSchemaProperty("type_pgStabilityResults");
	public static final String ATTRIBUTE_PG_SHORT_CODE = PropertyUtil.getSchemaProperty("attribute_pgShortCode");
	public static final String REL_CHARACTERISTICTOCHARACTERISTICSPECIFICS = PropertyUtil.getSchemaProperty("relationship_pgCharateristicToCharateristicSpecifics");
	public static final String TYPE_PG_PLICHARACTERISTIC = PropertyUtil.getSchemaProperty("type_pgPLICharacteristic");
	public static final String TYPE_PG_PLICHARSPECIFICS = PropertyUtil.getSchemaProperty("type_pgPLICharacteristicSpecifics");
	public static final String VAULT_ESERVICE_PRODUCTION = "eService Production";
	public static final String RELATIONSHIP_EXTENDED_DATA= PropertyUtil.getSchemaProperty(null,"relationship_Characteristic");
	public static final String SOURCE_ID = "sourceid";
	public static final String KEY_PARENT_ID = "parentId";
	public static final String POLICY_PG_STRUCTUREDATS	= PropertyUtil.getSchemaProperty(null,"policy_pgStructuredATS");
	public static final String STATE_PG_STRUCTUREDATS_IN_APPROVAL = PropertyUtil.getSchemaProperty(null,"policy", POLICY_PG_STRUCTUREDATS, "state_InApproval");
	public static final String STATE_PG_STRUCTUREDATS_RELEASE = PropertyUtil.getSchemaProperty(null,"policy", POLICY_PG_STRUCTUREDATS, "state_Release");
    public static final String STRING_ErrMessage = "Please revise the Structured ATS to modify/exclude the consuming formula(s)/APP";
    public static final String CONST_FROMREL_ID = "fromrel.id";
    public static final String CONST_FROMREL_TO_ID = "fromrel.to.id";

	public static final String KEY_ATS_ID = "atsid";
	public static final String KEY_ATS_CTX_RELID = "atsctxrelid";
	public static final String KEY_ATS_OPR_RELID = "atsoperationrelid";
	public static final String CONST_TOREL_TYPE = "torel.type";
	public static final String CONST_TO_TYPE = "to.type";
	public static final String KEY_TARGET_ID_ATTRIBUTES = "targetIdAttributes";
	public static final String ATTRIBUTE_PG_ORIGINATING_SOURCE = PropertyUtil.getSchemaProperty(null,
			"attribute_pgOriginatingSource");
	public static final String CONST_DSO = "DSO";
    public static final String ACCESS_READ = "read";
    public static final String CONST_FROMREL_FROM_ID = "fromrel.from.id";
    public static final String STATE_OBSOLETE = "Obsolete";
    public static final String STRING_ERR_ACCESS = "User doesn't have Read Access on ";
    public static final String STRING_ERR_ATS = "The ATS Item ";
    public static final String STRING_ERR_STATE = " is Obsoleted to modify and Save.";
    public static final String STRING_ERR_DELETE = "No ATS related item is present to delete";
	public static final String EXCEPTION_MESSAGE_SATS_HISTORY_UTIL = "Exception in PGStructuredATSHistoryUtil";
	public static final String TYPE_PG_TEST_METHOD = PropertyUtil.getSchemaProperty(null,"type_pgTestMethod");
	public static final String TYPE_PG_TEST_METHOD_SPEC = PropertyUtil.getSchemaProperty(null,"type_TestMethodSpecification");
	public static final String TYPE_PG_TMRD_TYPES = PropertyUtil.getSchemaProperty(null, "type_pgQualitySpecification")
			+ "," + PropertyUtil.getSchemaProperty(null, "type_pgStandardOperatingProcedure") + ","
			+ PropertyUtil.getSchemaProperty(null, "type_pgIllustration");
	public static final String PRODUCTION = "Production";	
	public static final String CONST_TEST_METHOD = "TestMethod";
	public static final String CONST_TEST_METHOD_REF_DOC = "TestMethodRefDoc";
	public static final String CONST_TEST_METHOD_NAME = "TestMethodName";
	public static final String CONST_TEST_METHOD_REF_DOC_NAME = "TestMethodRefDocName";
	
	public static final String SELECT_ATS_CTX_RELID = "from["+RELATIONSHIP_PLANNED_FOR+"].to["+TYPE_FORMULATION_PROCESS+"].from["+RELATIONSHIP_FBOM+"].to["+TYPE_FORMULATION_PHASE+"].from["+RELATIONSHIP_FBOM+"].tomid["+REL_PG_ATS_CONTEXT+"].id";
    public static final String SELECT_ATS_CTX_FSUB_RELID = "from["+RELATIONSHIP_PLANNED_FOR+"].to["+TYPE_FORMULATION_PROCESS+"].from["+RELATIONSHIP_FBOM+"].to["+TYPE_FORMULATION_PHASE+"].from["+RELATIONSHIP_FBOM+"].frommid["+RELATIONSHIP_FBOM_SUBSTITUTE+"].to["+TYPE_PARENT_SUB+"].to["+REL_PG_ATS_CONTEXT+"].id";
    public static final String SELECT_ATS_CTX_ESUB_RELID = "from["+DomainConstants.RELATIONSHIP_EBOM+"].frommid["+RELATIONSHIP_EBOM_SUBSTITUTE+"].tomid["+REL_PG_ATS_CONTEXT+"].id";
    public static final String SELECT_ATS_CTX_EBOM_RELID = "from["+DomainConstants.RELATIONSHIP_EBOM+"].tomid["+REL_PG_ATS_CONTEXT+"].id";
    public static final String SELECT_ATS_CTX_PERF_CHAR_RELID = "from["+RELATIONSHIP_EXTENDED_DATA+"].to["+TYPE_PG_PERFORMANCE_CHARACTERSTIC+"].to["+REL_PG_ATS_CONTEXT+"].id";
    public static final String SELECT_ATS_CTX_ALTERNATE_RELID = "from["+RELATIONSHIP_ALTERNATE+"].tomid["+REL_PG_ATS_CONTEXT+"].id";
    public static final String SELECT_ATS_RELID = "from["+RELATIONSHIP_AUTHORIZED_TEMPORARY_SPECIFICATION+"].id";
    public static final String SELECT_ATS_ID = "to["+RELATIONSHIP_AUTHORIZED_TEMPORARY_SPECIFICATION+"].from["+TYPE_SATS+"].id";
    public static final String KEY_TARGET_IDS = "targetIds";
    public static final String MULIT_VAL_CHAR = "\u0007";
    public static final String CONSTANT_STRING_PIPE = "|";
    public static final String CONSTANT_STRING_SEMICOLON = ";";
    public static final String CONSTANT_STRING_COLON = ":";
	
	public static final String ATTRIBUTE_UPT_PHYID = PropertyUtil.getSchemaProperty(null, "attribute_pgUPTPhyID");
	public static final String SELECT_ATTRIBUTE_UPT_PHYID = DomainObject.getAttributeSelect(ATTRIBUTE_UPT_PHYID);
	public static final String CONST_FROM = "from.id";
	
	public static final String SEP_NEW_LINE = "\n";
	public static final String VALUE_ATS = "ATS";
	public static final String VALUE_CURRENT = "Current";
	public static final String TNR_SEP = "|";
	public static final String COMMA_SEP = ",";
	public static final String VALUE_CURRENT_SUB= "Current(Substitute)";
	public static final String SELECT_ATS_OPERSTION_ID = "tomid["+REL_PG_ATS_CONTEXT+"].fromrel["+REL_PG_ATS_OPERATION+"].id";
	public static final String SELECT_ATS_OPERSTION_ID_FOR_FBOM = "to["+TYPE_PARENT_SUB+"].from["+REL_PG_ATS_OPERATION+"].id";
	public static final String SELECT_PARENT_SUB_FOR_FBOM_SUB = "frommid["+RELATIONSHIP_FBOM_SUBSTITUTE+"].to["+TYPE_PARENT_SUB+"].id";
	public static final String SELECT_ATS_OPERSTION_ID_FBOM_SUB = "to["+REL_PG_ATS_CONTEXT+"].fromrel["+REL_PG_ATS_OPERATION+"].id";
	public static final String TO_SIDE = "to.";
	public static final String KEY_MIN = "Min";
	public static final String KEY_MAX = "Max";
	public static final String KEY_QTY = "Quantity";
	public static final String KEY_TARGET_WET_WEIGHT = "Target Wet Weight";
	public static final String KEY_FOP = "FOP";
	public static final String KEY_BOM_HEADER = "bom_dynamic_col_data";
	public static final String KEY_BOM_DATA = "bom_row_data";
	public static final String KEY_EXTRA_INFO = "extraInfo";
	public static final String CONST_TO = "to.id";
	public static final String KEY_FILENAME = "filename";
	public static final String KEY_FILE_NAME = "fileName";
	public static final String KEY_BASE_64 = "base64,";
	public static final String STR_BALANCING_MATERIAL = "Balancing Material";
	public static final String MESSAGE_DEL_FILE_FAILED = "Exception in 'PGNWStructuredATSImportFromExcelUtil:importDataFromExcel'. Unable to delete the file {0}";
	public static final String MESSAGE_DEL_DIR_FAILED = "Exception in 'PGNWStructuredATSImportFromExcelUtil:importDataFromExcel'. Unable to delete the workspace {0}";
	public static final String EXCEPTION_MESSAGE_SATS_IMPORT_FROM_EXCEL= "Exception in PGNWStructuredATSImportFromExcelUtil";
	public static final String EXCEPTION_MESSAGE_SATS_IMPORT_FROM_PCEXCEL= "Exception in PGNWStructuredATSImportFromExcelPCUtil";
	public static final String MESSAGE_PREFIX_IMPORT = "Import from excel operation is completed with following errors.\n\n";
	
	public static final String MSG_PREFIX_FOR_BOM_DATA = "For BOM data '";
	public static final String MSG_UNABLE_TO_FIND_SUB = "Unable to find the Primary object for Substitute Part '";
	public static final String MSG_FOR_BOM_DATA = "' for BOM data '";
	public static final String MSG_UNABLE_TO_FIND_OBJ = "Unable to find object id for : ";
	public static final String MSG_UNABLE_TO_FIND_CHILD = "Unable to find the child '";
	public static final String MSG_MIN_MAX_QTY_NOT_MATCHNING = "Column values from sheet are not matching the values inside the database for part '";
	public static final String MSG_THE_OBJ = " the object ";
	public static final String MSG_NOT_REPLACED = " is not replaced for current ATS.\n";
	public static final String MSG_UNABLE_TO_FIND_NAME_REV = "Unable to find the 'Name-Rev' info for BOM data '";
	public static final String MSG_UNABLE_TO_FIND_REPLACED_DATA = "' unable to find replaced ATS data '";
	public static final String MSG_FOR_ATS_DATA = " for ATS data ";
	public static final String MSG_NEGATIVE_VALUES_NOT_ALLOWED = "Negative values are not allowed for column/s ";
	public static final String MSG_MIN_LESS_THAN_MAX= "Min value should be less than or equals to Max value for ATS data ";
	public static final String MSG_MIN_LESS_THAN_QTY= "Min value should be less than or equals to Quantity value for ATS data ";
	public static final String MSG_QTY_LESS_THAN_MAX= "Quantity value should be less than or equals to Max value for ATS data ";
	public static final String MSG_NUMERIC_VALUES = "Please provide Numeric values for column/s ";
	public static final String MSG_FOR_PART = "' for part '";
	public static final String MSG_FOR_BM = "' for "+STR_BALANCING_MATERIAL;
	public static final String MSG_BM_OBJ_NOT_FOUND_PREFIX = "Either Phase '";
	public static final String MSG_BM_OBJ_NOT_FOUND = "' or  "+STR_BALANCING_MATERIAL+" '";
	public static final String MSG_BM_OBJ_NOT_FOUND_SUFFIX = "' does not exist";
	public static final String MSG_NO_BM_PREFIX = "Unable to find "+STR_BALANCING_MATERIAL+" '";
	public static final String MSG_NO_BM_SUFFIX = "' for Phase ";
	
	//PC data : start
	public static final String KEY_CURRENT_PC = "Current Target and Limits";
	public static final String KEY_ATS_PC = "ATS Target and Limits";
	public static final String KEY_ADD_ATS_PC = "ATS Target and Limits: Add";
	public static final String STR_KEY = "columnIdentifier";
	public static final String KEY_PC_HEADER_DATA = "headerdata";
	public static final String KEY_PC_ROW_DATA = "rowdata";
	public static final String SEP_DUP = "-";
	public static final String SUFFIX_NAME = "Name";
	public static final String KEY_PARENT_INFO = "parentInfo";
	public static final String KEY_CHAR_GRPS = "charGroups";
	public static final String KEY_CHAR_KEYS = "charGroupKeys";
	public static final String KEY_CHAR_GRPS_MAX = "charGroupMaxSize";
	public static final String COL_HIDDEN_PCID = "Do Not Modify";
	public static final String KEY_ATTR_COL = "AttributesMap";
	public static final String KEY_REL_COL = "ConnectionsMap";
	public static final String TM_WHERE_CLAUSE = "latest == TRUE";
	public static final String PERSON_USER_AGENT = "person_UserAgent";
	public static final String PERSON_AGENT = PropertyUtil.getSchemaProperty(null,PERSON_USER_AGENT);
	public static final String STATE_RELEASE = "Release";
	public static final String STATE_COMPLETE = "Complete";
	
	public static final String VAL_REPORT = "REPORT";
	public static final String VAL_SUMMARY = "SUMMARY";
	public static final String VAL_ATTRIBUTE = "ATTRIBUTE";
	public static final String VAL_VARIABLE = "VARIABLE";
	public static final String MSG_COLON = " - ";
	public static final String MSG_ACTION_REQUIRED = "Column 'Action Required' is mandatory\n";
	public static final String MSG_REPORT_TYPE = "Please select Report Type value as ATTRIBUTE or VARIABLE\n";
	public static final String MSG_TARGET_LIMIT = "Please select value for Target or Limit related columns\n";
	public static final String MSG_LSL_USL= "At least one of the columns must have value : 'Lower Specification Limit' or 'Upper Specification Limit'\n";
	public static final String MSG_ERROR_TARGET_COLS = "Target can have value only when Lower and Upper Target are blank\n";
	public static final String MSG_LESS_THAN= " must be less than ";
	
	public static final String MSG_SEP = ", ";
	public static final String MSG_FORMULA = "Formula:";
	public static final String MSG_PREFIX_FORMULA = "For context formula ";
	public static final String MSG_NO_MOD_ADD_DATA_EXIST = " there is no related PC object found for SATS with ";
	
	public static final String DISP_TM_NAME = "Test Method Name";
	public static final String DISP_TM_LOGIC = "Test Method Logic";
	public static final String DISP_TM_ORIGIN = "Test Method Origin";
	public static final String DISP_TM_NUM = "Other Test Method Number";
	public static final String DISP_TM_SPECIFICS = "Test Method Specifics";
	public static final String DISP_TM_REF_DOC = "Test Method Reference Document Name";
	public static final String DISP_SAMPLING = "Sampling";
	public static final String DISP_SUB_GRP = "Subgroup";
	public static final String DISP_PLANT_TESTING_LEVEL = "Plant Testing Level";
	public static final String DISP_PLANT_RETESTING = "Plant Testing : Retesting";
	public static final String DISP_RETESTING_UOM = "Retesting : Unit of Measure";
	public static final String DISP_LSL = "Lower Specification Limit";
	public static final String DISP_LRRL = "Lower Routine Release Limit";
	public static final String DISP_LT = "Lower Target";
	public static final String DISP_TARGET = "Target";
	public static final String DISP_UT = "Upper Target";
	public static final String DISP_URRL = "Upper Routine Release Limit";
	public static final String DISP_USL = "Upper Specification Limit";
	public static final String DISP_REPORT_NEAREST = "Report to Nearest";
	public static final String DISP_REPORT_TYPE = "Report Type";
	public static final String DISP_RELEASE_CRITERIA = "Release Criteria";
	public static final String DISP_ACTION_RQD = "Action Required";
	public static final String DISP_CRITICALITY_FACT = "Criticality Factor";
	public static final String DISP_BASIS = "Basis";
	public static final String DISP_TEST_GRP = "Test Group";
	public static final String DISP_APPLICATION = "Application";
	public static final String DISP_UOM = "Unit of Measure";
	
	public static final String ATTRIBUTE_TM_LOGIC = PropertyUtil.getSchemaProperty(null, "attribute_pgTMLogic");
	public static final String SELECT_ATTRIBUTE_TM_LOGIC = DomainObject.getAttributeSelect(ATTRIBUTE_TM_LOGIC);
	public static final String ATTRIBUTE_METHOD_ORIGIN = PropertyUtil.getSchemaProperty(null, "attribute_pgMethodOrigin");
	public static final String SELECT_ATTRIBUTE_METHOD_ORIGIN = DomainObject.getAttributeSelect(ATTRIBUTE_METHOD_ORIGIN);
	public static final String ATTRIBUTE_METHOD_NUM = PropertyUtil.getSchemaProperty(null, "attribute_pgMethodNumber");
	public static final String SELECT_ATTRIBUTE_METHOD_NUM = DomainObject.getAttributeSelect(ATTRIBUTE_METHOD_NUM);
	public static final String ATTRIBUTE_TM_SPECIFCS = PropertyUtil.getSchemaProperty(null, "attribute_pgMethodSpecifics");
	public static final String SELECT_ATTRIBUTE_TM_SPECIFCS = DomainObject.getAttributeSelect(ATTRIBUTE_TM_SPECIFCS);
	public static final String ATTRIBUTE_SAMPLING = PropertyUtil.getSchemaProperty(null, "attribute_pgSampling");
	public static final String SELECT_ATTRIBUTE_SAMPLING= DomainObject.getAttributeSelect(ATTRIBUTE_SAMPLING);
	public static final String ATTRIBUTE_SUB_GRP = PropertyUtil.getSchemaProperty(null, "attribute_pgSubGroup");
	public static final String SELECT_ATTRIBUTE_SUB_GRP = DomainObject.getAttributeSelect(ATTRIBUTE_SUB_GRP);
	public static final String ATTRIBUTE_PLANT_TESTING = PropertyUtil.getSchemaProperty(null, "attribute_pgPlantTesting");
	public static final String SELECT_ATTRIBUTE_PLANT_TESTING = DomainObject.getAttributeSelect(ATTRIBUTE_PLANT_TESTING);
	public static final String ATTRIBUTE_PLANT_RETESTING = PropertyUtil.getSchemaProperty(null, "attribute_pgPlantTestingRetesting");
	public static final String SELECT_ATTRIBUTE_PLANT_RETESTING = DomainObject.getAttributeSelect(ATTRIBUTE_PLANT_RETESTING);
	public static final String ATTRIBUTE_RETESTING_UOM = PropertyUtil.getSchemaProperty(null, "attribute_pgRetestingUOM");
	public static final String SELECT_ATTRIBUTE_RETESTING_UOM = DomainObject.getAttributeSelect(ATTRIBUTE_RETESTING_UOM);
	public static final String ATTRIBUTE_LSL = PropertyUtil.getSchemaProperty(null, "attribute_pgLowerSpecificationLimit");
	public static final String SELECT_ATTRIBUTE_LSL = DomainObject.getAttributeSelect(ATTRIBUTE_LSL);
	public static final String ATTRIBUTE_LRRL= PropertyUtil.getSchemaProperty(null, "attribute_pgLowerRoutineReleaseLimit");
	public static final String SELECT_ATTRIBUTE_LRRL = DomainObject.getAttributeSelect(ATTRIBUTE_LRRL);
	public static final String ATTRIBUTE_LT = PropertyUtil.getSchemaProperty(null, "attribute_pgLowerTarget");
	public static final String SELECT_ATTRIBUTE_LT = DomainObject.getAttributeSelect(ATTRIBUTE_LT);
	public static final String ATTRIBUTE_TARGET = PropertyUtil.getSchemaProperty(null, "attribute_pgTarget");
	public static final String SELECT_ATTRIBUTE_TARGET = DomainObject.getAttributeSelect(ATTRIBUTE_TARGET);
	public static final String ATTRIBUTE_UT = PropertyUtil.getSchemaProperty(null, "attribute_pgUpperTarget");
	public static final String SELECT_ATTRIBUTE_UT = DomainObject.getAttributeSelect(ATTRIBUTE_UT);
	public static final String ATTRIBUTE_URRL = PropertyUtil.getSchemaProperty(null, "attribute_pgUpperRoutineReleaseLimit");
	public static final String SELECT_ATTRIBUTE_URRL = DomainObject.getAttributeSelect(ATTRIBUTE_URRL);
	public static final String ATTRIBUTE_USL = PropertyUtil.getSchemaProperty(null, "attribute_pgUpperSpecificationLimit");
	public static final String SELECT_ATTRIBUTE_USL = DomainObject.getAttributeSelect(ATTRIBUTE_USL);
	public static final String ATTRIBUTE_REPORT_NEAREST = PropertyUtil.getSchemaProperty(null, "attribute_pgReportToNearest");
	public static final String SELECT_ATTRIBUTE_REPORT_NEAREST = DomainObject.getAttributeSelect(ATTRIBUTE_REPORT_NEAREST);
	public static final String ATTRIBUTE_REPORT_TYPE = PropertyUtil.getSchemaProperty(null, "attribute_pgReportType");
	public static final String SELECT_ATTRIBUTE_REPORT_TYPE = DomainObject.getAttributeSelect(ATTRIBUTE_REPORT_TYPE);
	public static final String ATTRIBUTE_REL_CRITERIA = PropertyUtil.getSchemaProperty(null, "attribute_pgReleaseCriteria");
	public static final String SELECT_ATTRIBUTE_REL_CRITERIA = DomainObject.getAttributeSelect(ATTRIBUTE_REL_CRITERIA);
	public static final String ATTRIBUTE_ACTION_REQ = PropertyUtil.getSchemaProperty(null, "attribute_pgActionRequired");
	public static final String SELECT_ATTRIBUTE_ACTION_REQ = DomainObject.getAttributeSelect(ATTRIBUTE_ACTION_REQ);
	public static final String ATTRIBUTE_CRITICALITY_FACTOR = PropertyUtil.getSchemaProperty(null, "attribute_pgCriticalityFactor");
	public static final String SELECT_ATTRIBUTE_CRITICALITY_FACTOR = DomainObject.getAttributeSelect(ATTRIBUTE_CRITICALITY_FACTOR);
	public static final String ATTRIBUTE_BASIS = PropertyUtil.getSchemaProperty(null, "attribute_pgBasis");
	public static final String SELECT_ATTRIBUTE_BASIS = DomainObject.getAttributeSelect(ATTRIBUTE_BASIS);
	public static final String ATTRIBUTE_TEST_GRP = PropertyUtil.getSchemaProperty(null, "attribute_pgTestGroup");
	public static final String SELECT_ATTRIBUTE_TEST_GRP = DomainObject.getAttributeSelect(ATTRIBUTE_TEST_GRP);
	public static final String ATTRIBUTE_APPLICATION = PropertyUtil.getSchemaProperty(null, "attribute_pgApplication");
	public static final String SELECT_ATTRIBUTE_APPLICATION = DomainObject.getAttributeSelect(ATTRIBUTE_APPLICATION);
	public static final String ATTRIBUTE_UOM = PropertyUtil.getSchemaProperty(null, "attribute_pgUnitOfMeasure");
	public static final String SELECT_ATTRIBUTE_UOM = DomainObject.getAttributeSelect(ATTRIBUTE_UOM);
	
	public static final String VALUE_ATTR_STRING = "string";
	public static final String VALUE_ATTR_NUM = "number";
	//PC data : end
	//Save PC - Start
		public static final String VALUE_OPERATION = "operation";
		public static final String KEY_ADD_ADJUST = "add/adjust";
		public static final String KEY_CTXFOP_ID = "CTXFOP-id";
		public static final String ACTION_CAP_ADJUST = "Adjust";
	//Save PC - End
}

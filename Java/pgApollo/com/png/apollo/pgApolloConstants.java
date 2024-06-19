/*
 * Added by APOLLO Team
 * For all APOLLO Constants
 */

package com.png.apollo;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.PropertyUtil;

public class pgApolloConstants implements DomainConstants
{
	//Attribute Constants
	public static final String ATTRIBUTE_OVERRIDEALLOWEDONCHILD = PropertyUtil.getSchemaProperty("attribute_OverwriteAllowedOnChild");
	public static final String ATTRIBUTE_CHARACTERISTICSPECIFIC = PropertyUtil.getSchemaProperty("attribute_pgCharacteristicSpecifics");
	public static final String ATTRIBUTE_ACTION_REQUIRED = PropertyUtil.getSchemaProperty("attribute_pgActionRequired"); 
	public static final String ATTRIBUTE_REPORT_TYPE = PropertyUtil.getSchemaProperty("attribute_pgReportType");	
	public static final String ATTRIBUTE_PG_PLRELVALUE = PropertyUtil.getSchemaProperty("attribute_pgPLRelValue");
	public static final String ATTRIBUTE_SEQUENCE_NAME = PropertyUtil.getSchemaProperty("attribute_pgProcessSequence");
	public static final String ATTRIBUTE_PLY_GROUP_NAME = PropertyUtil.getSchemaProperty("attribute_pgLayerGroupName");
	public static final String ATTRIBUTE_PLY_NAME = PropertyUtil.getSchemaProperty("attribute_pgLayerName");
	public static final String ATTRIBUTE_PGBASEUOM = PropertyUtil.getSchemaProperty("attribute_pgBaseUnitOfMeasure");
	public static final String ATTRIBUTE_VCheckedOut = PropertyUtil.getSchemaProperty("attribute_PLMDMTDocument.V_IsCheckedOut");
	public static final String ATTRIBUTE_PG_CLASS = PropertyUtil.getSchemaProperty("attribute_pgClass");
	public static final String ATTRIBUTE_PG_SUB_CLASS = PropertyUtil.getSchemaProperty("attribute_pgSubClass");
	public static final String ATTRIBUTE_PG_CHARACTERISTIC = PropertyUtil.getSchemaProperty("attribute_pgCharacteristic"); 
	public static final String ATTRIBUTE_PG_TARGET = PropertyUtil.getSchemaProperty("attribute_pgTarget");
	public static final String ATTRIBUTE_PG_UPPER_TARGET = PropertyUtil.getSchemaProperty("attribute_pgUpperTarget");
	public static final String ATTRIBUTE_PG_LOWER_TARGET = PropertyUtil.getSchemaProperty("attribute_pgLowerTarget");
	public static final String ATTRIBUTE_PG_UNIT_OF_MEASURE = PropertyUtil.getSchemaProperty("attribute_pgUnitOfMeasure");
	public static final String ATTRIBUTE_PG_APPLICATION = PropertyUtil.getSchemaProperty("attribute_pgApplication");
	public static final String ATTRIBUTE_PG_SEGMENT = PropertyUtil.getSchemaProperty("attribute_pgSegment");
	public static final String ATTRIBUTE_PG_LIFECYCLE_STATUS = PropertyUtil.getSchemaProperty("attribute_pgLifeCycleStatus");
	public static final String ATTRIBUTE_V_DESCRIPTION = PropertyUtil.getSchemaProperty("attribute_PLMEntity.V_description");
	public static final String ATTRIBUTE_V_NAME = PropertyUtil.getSchemaProperty("attribute_PLMEntity.V_Name");
	public static final String ATTRIBUTE_V_NAME_ENTERPRISE_PART = PropertyUtil.getSchemaProperty("attribute_V_Name");
	public static final String ATTRIBUTE_PG_CHARACTERISTIC_CATEGORY = PropertyUtil.getSchemaProperty("attribute_CharacteristicCategory");
	public static final String ATTRIBUTE_CHARACTERISTIC_CATEGORY = PropertyUtil.getSchemaProperty("attribute_CharacteristicCategory");
	public static final String ATTRIBUTE_CHARACTERISTIC_NOTES = PropertyUtil.getSchemaProperty("attribute_CharacteristicNotes");
	public static final String ATTRIBUTE_ISVPLMCONTROLLED = PropertyUtil.getSchemaProperty("attribute_PLMReference.V_isVPLMControlled");
	public static final String ATTRIBUTE_APPLICATION = PropertyUtil.getSchemaProperty("attribute_Application");
	public static final String ATTRIBUTE_PGLPDMODELTYPE = PropertyUtil.getSchemaProperty("attribute_pngiAssembled.pngLPDModelType");
	public static final String ATTRIBUTE_PGSTRUCTUREDRELEASECRITERIAREQUIRED = PropertyUtil.getSchemaProperty("attribute_pgStructuredReleaseCriteriaRequired");
	public static final String ATTRIBUTE_VERSION_COMMENT = PropertyUtil.getSchemaProperty("attribute_PLMReference.V_versionComment");
	public static final String ATTRIBUTE_REASON_FOR_CHANGE = PropertyUtil.getSchemaProperty("attribute_ReasonforChange");
	public static final String ATTRIBUTE_PNG_GROSS_WEIGHT = PropertyUtil.getSchemaProperty("attribute_pngiAssembled.pngGrossWeight");
	public static final String ATTRIBUTE_PG_GROSSWEIGHTREAL = PropertyUtil.getSchemaProperty("attribute_pgGrossWeightReal");
	public static final String ATTRIBUTE_PG_COMPONENTQUANTITY = PropertyUtil.getSchemaProperty("attribute_pgComponentQuantity");
	public static final String ATTRIBUTE_PG_PLATFORM_TYPE = PropertyUtil.getSchemaProperty("attribute_pgPlatformType");
	public static final String ATTRIBUTE_PG_CHASSIS_TYPE = PropertyUtil.getSchemaProperty("attribute_pgChassisType");
	public static final String ATTRIBUTE_PGPRIMARYORGANIZATION = PropertyUtil.getSchemaProperty("attribute_pngiAssembled.pngPrimaryOrganization");
	public static final String ATTRIBUTE_PGBUSINESSAREA = PropertyUtil.getSchemaProperty("attribute_pngiAssembled.pngBusinessArea");
	public static final String ATTRIBUTE_PGPRODUCTCATEGORYPLATFORM = PropertyUtil.getSchemaProperty("attribute_pngiAssembled.pngProductCategoryPlatform");
	public static final String ATTRIBUTE_PGPRODUCTTECHNOLOGYPLATFORM = PropertyUtil.getSchemaProperty("attribute_pngiAssembled.pngProductTechnologyPlatform");
	public static final String ATTRIBUTE_PGPRODUCTTECHNOLOGYCHASSIS = PropertyUtil.getSchemaProperty("attribute_pngiAssembled.pngProductTechnologyChassis");
	public static final String ATTRIBUTE_PGFRANCHISEPLATFORM = PropertyUtil.getSchemaProperty("attribute_pngiAssembled.pngFranchisePlatform");	
	public static final String ATTRIBUTE_PNGLPDORIGINATEDFROM = PropertyUtil.getSchemaProperty("attribute_pngiAssembled.pngLPDOriginatedFrom");
	public static final String ATTRIBUTE_PNGLPDORIGINATEDFROMROOT = PropertyUtil.getSchemaProperty("attribute_pngiAssembled.pngLPDOriginatedFromRoot");
	public static final String ATTRIBUTE_PGDEFINITION = PropertyUtil.getSchemaProperty("attribute_pngiAssembled.pngDefinition");
	public static final String ATTRIBUTE_CATIA_PGNETWEIGHT = PropertyUtil.getSchemaProperty("attribute_pngiAssembled.pngNetWeight");
	public static final String ATTRIBUTE_PG_ORIGINATINGSOURCE = PropertyUtil.getSchemaProperty("attribute_pgOriginatingSource");
	public static final String ATTRIBUTE_PG_MATERIAL_FUNCTION = PropertyUtil.getSchemaProperty("attribute_pgMaterialFunction");
	public static final String ATTRIBUTE_PGSIZE = PropertyUtil.getSchemaProperty("attribute_pngiAssembled.pngSIZE");	
	public static final String ATTRIBUTE_PG_DSMPRODUCTSIZE = PropertyUtil.getSchemaProperty("attribute_pgDSMProductSize");
	public static final String ATTRIBUTE_PGCHARSPECIFICS = PropertyUtil.getSchemaProperty("attribute_pgCharacteristicSpecifics");
	public static final String ATTRIBUTE_PGACTIONREQUIRED = PropertyUtil.getSchemaProperty("attribute_pgActionRequired");
	public static final String ATTRIBUTE_PGREPORTTYPE = PropertyUtil.getSchemaProperty("attribute_pgReportType");
	public static final String ATTRIBUTE_CHARACTERISTICSLOWERSPECIFICATIONLIMIT = PropertyUtil.getSchemaProperty("attribute_CharacteristicsLowerSpecificationLimit");
	public static final String ATTRIBUTE_CHARACTERISTICSUPPERSPECIFICATIONLIMIT = PropertyUtil.getSchemaProperty("attribute_CharacteristicsUpperSpecificationLimit");
	public static final String ATTRIBUTE_CHARACTERISTICSLOWERROUTINERELEASELIMIT = PropertyUtil.getSchemaProperty("attribute_CharacteristicsLowerRoutineReleaseLimit");
	public static final String ATTRIBUTE_CHARACTERISTICSUPPERROUTINERELEASELIMIT = PropertyUtil.getSchemaProperty("attribute_CharacteristicsUpperRoutineReleaseLimit");
	public static final String ATTRIBUTE_PGREGION = PropertyUtil.getSchemaProperty("attribute_pngiAssembled.pngRegion");
	public static final String ATTRIBUTE_PGSUBREGION = PropertyUtil.getSchemaProperty("attribute_pngiAssembled.pngSubRegion");
	public static final String ATTRIBUTE_PGRMPGCAS = PropertyUtil.getSchemaProperty("attribute_pngiMaterialCore.pngRMPGCAS");
	public static final String ATTRIBUTE_EVALUATED_CRITERIA = PropertyUtil.getSchemaProperty("attribute_EvaluatedCriteria");
	public static final String ATTRIBUTE_PGMANUFACTURING_MATURITYSTATUS = PropertyUtil.getSchemaProperty("attribute_pngiDesignPart.pngManufacturingMaturityStatus");
	public static final String ATTRIBUTE_PGENTERPRISETYPE = PropertyUtil.getSchemaProperty("attribute_pngiDesignPart.pngEnterpriseType");
	public static final String ATTRIBUTE_APPLICABLETYPE = PropertyUtil.getSchemaProperty("attribute_ApplicableType");
	public static final String ATTRIBUTE_PG_V_INEBOMAPPLICATIVE = PropertyUtil.getSchemaProperty("attribute_SynchroEBOMExt.V_InEBOMApplicative");
	public static final String ATTRIBUTE_PG_V_INEBOMUSER = PropertyUtil.getSchemaProperty("attribute_SynchroEBOMExt.V_InEBOMUser");
	public static final String ATTRIBUTE_PGRELATEDARTWORK = PropertyUtil.getSchemaProperty("attribute_pngiMaterialCovering.pngRelatedArtwork");
	public static final String ATTRIBUTE_PROGRAM_ARGUMENTS =PropertyUtil.getSchemaProperty("attribute_ProgramArguments");
	public static final String ATTRIBUTE_COMPLETION_STATUS =PropertyUtil.getSchemaProperty("attribute_CompletionStatus");
	public static final String ATTRIBUTE_PLMPARAMDISPLAYUNIT =PropertyUtil.getSchemaProperty("attribute_PlmParamDisplayUnit");
	public static final String ATTRIBUTE_PGPRODUCTEXPOSEDTOCHILDREN =PropertyUtil.getSchemaProperty("attribute_pgProductExposedToChildren");
	public static final String ATTRIBUTE_PGPRODUCTMARKETEDASCHILDRENPRODUCT =PropertyUtil.getSchemaProperty("attribute_pgProductMarketedAsChildrenProduct");
	public static final String ATTRIBUTE_PGDOESTHEPRODUCTREQUIRECHILDSAFEDESIGN =PropertyUtil.getSchemaProperty("attribute_pgDoestheProductRequireChildSafeDesign");
	public static final String ATTRIBUTE_PGLPDORIGINATEDFROMGENERICMODEL = PropertyUtil.getSchemaProperty("attribute_pngiAssembled.pngLPDOriginatedFromGenericModel");
	public static final String ATTRIBUTE_PGLPDPERFCHARACTERISTICSUPDATESTATUS = PropertyUtil.getSchemaProperty("attribute_pgLPDPerfCharacteristicsUpdateStatus");
	public static final String ATTRIBUTE_METHODNAME = PropertyUtil.getSchemaProperty("attribute_MethodName");
	public static final String ATTRIBUTE_PROGRAMNAME = PropertyUtil.getSchemaProperty("attribute_ProgramName");
	public static final String ATTRIBUTE_PGPARAMETERARGUMENT1 = PropertyUtil.getSchemaProperty("attribute_pgParameterArgument1");
	public static final String ATTRIBUTE_ERROR_MESSAGE = PropertyUtil.getSchemaProperty("attribute_ErrorMessage");

	//Custom attribute
	public static final String ATTRIBUTE_PGLOWERSPECIFICATIONLIMIT = PropertyUtil.getSchemaProperty("attribute_pgLowerSpecificationLimit");
	public static final String ATTRIBUTE_PGUPPERSPECIFICATIONLIMIT = PropertyUtil.getSchemaProperty("attribute_pgUpperSpecificationLimit");
	public static final String ATTRIBUTE_PGLOWERROUTINERELEASELIMIT = PropertyUtil.getSchemaProperty("attribute_pgLowerRoutineReleaseLimit");
	public static final String ATTRIBUTE_PGUPPERROUTINERELEASELIMIT = PropertyUtil.getSchemaProperty("attribute_pgUpperRoutineReleaseLimit");
	
	public static final String ATTRIBUTE_PGAPPLICATION = PropertyUtil.getSchemaProperty("attribute_pgApplication");		
	public static final String ATTRIBUTE_PG_STRUCTURED_RELEASE_CRITERIA_REQUIRED = PropertyUtil.getSchemaProperty ("attribute_pgStructuredReleaseCriteriaRequired");
	public static final String ATTRIBUTE_PG_ROUTINERELEASECRITERIA = PropertyUtil.getSchemaProperty("attribute_pgRoutineReleaseCriteria");
	public static final String ATTRIBUTE_PGAUTHORINGAPPLICATION = PropertyUtil.getSchemaProperty("attribute_pgAuthoringApplication");
	public static final String ATTRIBUTE_PG_CHARACTERISTICSSPECIFICS= PropertyUtil.getSchemaProperty("attribute_pgCharacteristicSpecifics");
	public static final String ATTRIBUTE_PG_DESIGNSPECIFICS= PropertyUtil.getSchemaProperty("attribute_pgDesignSpecifics");
	public static final String ATTRIBUTE_CRITERIA_1 = PropertyUtil.getSchemaProperty("attribute_Criteria1");
	public static final String ATTRIBUTE_CRITERIA_2 = PropertyUtil.getSchemaProperty("attribute_Criteria2");
	public static final String ATTRIBUTE_CRITERIA_3 = PropertyUtil.getSchemaProperty("attribute_Criteria3");
	public static final String ATTRIBUTE_CRITERIA_4 = PropertyUtil.getSchemaProperty("attribute_Criteria4");
	public static final String ATTRIBUTE_CRITERIA_5 = PropertyUtil.getSchemaProperty("attribute_Criteria5");
	public static final String ATTRIBUTE_CRITERIA_6 = PropertyUtil.getSchemaProperty("attribute_Criteria6");
	public static final String ATTRIBUTE_CRITERIA_7 = PropertyUtil.getSchemaProperty("attribute_Criteria7");
	public static final String ATTRIBUTE_CRITERIA_8 = PropertyUtil.getSchemaProperty("attribute_Criteria8");
	public static final String ATTRIBUTE_CRITERIA_9 = PropertyUtil.getSchemaProperty("attribute_Criteria9");
	public static final String ATTRIBUTE_CRITERIA_EXPRESSION = PropertyUtil.getSchemaProperty("attribute_CriteriaExpression");
	public static final String ATTRIBUTE_PG_INTENDED_MARKETS = PropertyUtil.getSchemaProperty("attribute_pgIntendedMarkets");
	public static final String ATTRIBUTE_ENTERPRISEPART_PGBUSINESSAREA = PropertyUtil.getSchemaProperty("attribute_pgBusinessArea");
	public static final String ATTRIBUTE_ENTERPRISEPART_PGPRODUCTCATEGORYPLATFORM = PropertyUtil.getSchemaProperty("attribute_pgProductCategoryPlatform");
	public static final String ATTRIBUTE_ENTERPRISEPART_PGPRODUCTTECHNOLOGYPLATFORM = PropertyUtil.getSchemaProperty("attribute_pgProductTechnologyPlatform");
	public static final String ATTRIBUTE_ENTERPRISEPART_PGPRODUCTTECHNOLOGYCHASSIS = PropertyUtil.getSchemaProperty("attribute_pgProductTechnologyChassis");
	public static final String ATTRIBUTE_ENTERPRISEPART_PGFRANCHISEPLATFORM = PropertyUtil.getSchemaProperty("attribute_pgFranchisePlatform");
	public static final String ATTRIBUTE_ENTERPRISEPART_PGLPDMODELTYPE = PropertyUtil.getSchemaProperty("attribute_pgLPDModelType");
	public static final String ATTRIBUTE_ENTERPRISEPART_PGLPDDEFINITION = PropertyUtil.getSchemaProperty("attribute_pgLPDDefinition");
	public static final String ATTRIBUTE_ENTERPRISEPART_PGLPDREGION = PropertyUtil.getSchemaProperty("attribute_pgLPDRegion");
	public static final String ATTRIBUTE_ENTERPRISEPART_PGLPDSUBREGION = PropertyUtil.getSchemaProperty("attribute_pgLPDSubRegion");
	public static final String ATTRIBUTE_PGCHANGE = PropertyUtil.getSchemaProperty("attribute_pgChange");
	public static final String ATTRIBUTE_V_CHECKEDOUTUSER = PropertyUtil.getSchemaProperty("attribute_PLMDMTDocument.V_CheckedOutUser");
		
	public static final String ATTRIBUTE_PLM_EXTERNAL_ID = PropertyUtil.getSchemaProperty("attribute_PLMEntity.PLM_ExternalID");
	public static final String ATTRIBUTE_PLMINSTANCE_PLM_EXTERNAL_ID = PropertyUtil.getSchemaProperty("attribute_PLMInstance.PLM_ExternalID");
	public static final String ATTRIBUTE_V_VERSION_ID = PropertyUtil.getSchemaProperty("attribute_PLMReference.V_VersionID");
	public static final String ATTRIBUTE_V_ORDER = PropertyUtil.getSchemaProperty("attribute_PLMReference.V_order");
	public static final String ATTRIBUTE_V_DISCIPLINE = PropertyUtil.getSchemaProperty("attribute_PLMEntity.V_discipline");
	public static final String ATTRIBUTE_MATERIALDOMAIN_MATDOMAIN = PropertyUtil.getSchemaProperty("attribute_MaterialDomain.V_MatDomain");
	public static final String ATTRIBUTE_PGGCAS_CORE = PropertyUtil.getSchemaProperty("attribute_pngpg_dsc_matref_ref_Core_Ext.pngpgCATGCAS");
	public static final String ATTRIBUTE_MATERIAL_FUNCTION_CORE = PropertyUtil.getSchemaProperty("attribute_pngpg_dsc_matref_ref_Core_Ext.pngpgCATMaterialFunction");
	public static final String ATTRIBUTE_REPORTED_FUNCTION_CORE = PropertyUtil.getSchemaProperty("attribute_pngpg_dsc_matref_ref_Core_Ext.pngpgCATReportedFunction");	
	public static final String ATTRIBUTE_V_SEC_LEVEL = PropertyUtil.getSchemaProperty("attribute_PLMEntity.V_sec_level");
	public static final String ATTRIBUTE_C_UPDATESTAMP = PropertyUtil.getSchemaProperty("attribute_PLMEntity.C_updatestamp");
	public static final String ATTRIBUTE_PGGCAS_COMP = PropertyUtil.getSchemaProperty("attribute_pngpg_dsc_matref_rep_Composite_Ext.pngpgCATGCAS");
	public static final String ATTRIBUTE_MATERIAL_FUNCTION_COMP = PropertyUtil.getSchemaProperty("attribute_pngpg_dsc_matref_rep_Composite_Ext.pngpgCATMaterialFunction");
	public static final String ATTRIBUTE_REPORTED_FUNCTION_COMP = PropertyUtil.getSchemaProperty("attribute_pngpg_dsc_matref_rep_Composite_Ext.pngpgCATReportedFunction");	
	public static final String ATTRIBUTE_NET_WEIGHT = PropertyUtil.getSchemaProperty("attribute_pgNetWeight");
	public static final String ATTRIBUTE_PGQUANTITY = PropertyUtil.getSchemaProperty("attribute_pgQuantity");
	public static final String ATTRIBUTE_QUANTITY = PropertyUtil.getSchemaProperty("attribute_Quantity");
	public static final String ATTRIBUTE_PGPLRELVALUE = PropertyUtil.getSchemaProperty("attribute_pgPLRelValue");
	public static final String ATTRIBUTE_PGRETESTINGUOM = PropertyUtil.getSchemaProperty("attribute_pgRetestingUOM");
	public static final String ATTRIBUTE_PGTESTGROUP = PropertyUtil.getSchemaProperty("attribute_pgTestGroup");
	public static final String ATTRIBUTE_PGMETHODSPECIFICS = PropertyUtil.getSchemaProperty("attribute_pgMethodSpecifics");
	public static final String ATTRIBUTE_PGMETHODNUMBER = PropertyUtil.getSchemaProperty("attribute_pgMethodNumber");
	public static final String ATTRIBUTE_PGTMLOGIC = PropertyUtil.getSchemaProperty("attribute_pgTMLogic");
	public static final String ATTRIBUTE_PGCRITICALITYFACTOR = PropertyUtil.getSchemaProperty("attribute_pgCriticalityFactor");
	public static final String ATTRIBUTE_PGPLANTTESTING = PropertyUtil.getSchemaProperty("attribute_pgPlantTesting");
	public static final String ATTRIBUTE_PGMETHODORIGIN = PropertyUtil.getSchemaProperty("attribute_pgMethodOrigin");
	public static final String ATTRIBUTE_PGPLANTTESTINGRETESTING = PropertyUtil.getSchemaProperty("attribute_pgPlantTestingRetesting");
	public static final String ATTRIBUTE_PGNETWEIGHTUOM = PropertyUtil.getSchemaProperty("attribute_pgNetWeightUnitOfMeasure");
	public static final String ATTRIBUTE_PGGROSSWEIGHT = PropertyUtil.getSchemaProperty("attribute_pgGrossWeight");
	public static final String ATTRIBUTE_PGGROSSWEIGHTUOM = PropertyUtil.getSchemaProperty("attribute_pgGrossWeightUnitOfMeasure");
	public static final String ATTRIBUTE_PGSAMPLING = PropertyUtil.getSchemaProperty("attribute_pgSampling"); 
	public static final String ATTRIBUTE_PGSUBGROUP = PropertyUtil.getSchemaProperty("attribute_pgSubGroup"); 
	public static final String ATTRIBUTE_PGBASIS = PropertyUtil.getSchemaProperty("attribute_pgBasis");
	public static final String ATTRIBUTE_PGRELEASECRITERIA = PropertyUtil.getSchemaProperty("attribute_pgReleaseCriteria");	
	public static final String ATTRIBUTE_PGPLMENTITY_USAGE = PropertyUtil.getSchemaProperty("attribute_PLMEntity.V_usage");
	public static final String ATTRIBUTE_PLMREP_ONCE_INSTANTIABLE = PropertyUtil.getSchemaProperty("attribute_PLMCoreRepReference.V_isOnceInstantiable");
	public static final String ATTRIBUTE_LPABSTRACT_MANDATORY = PropertyUtil.getSchemaProperty("attribute_LPAbstractRepReference.V_isMandatory"); 
	public static final String ATTRIBUTE_V_ISTERMINAL = PropertyUtil.getSchemaProperty("attribute_PLMCoreReference.V_isTerminal"); 
	public static final String ATTRIBUTE_PGLAYEREDPRODUCTAREA = PropertyUtil.getSchemaProperty("attribute_pgLayeredProductArea");
	public static final String ATTRIBUTE_MANDATORY_CHARACTERISTIC = PropertyUtil.getSchemaProperty("attribute_MandatoryCharacteristic");
	public static final String ATTRIBUTE_PGREPORTTONEAREST = PropertyUtil.getSchemaProperty("attribute_pgReportToNearest");
	public static final String ATTRIBUTE_PGROLLUPNETWEIGHTTOCOP = PropertyUtil.getSchemaProperty("attribute_pgRollUpNetWeightToCOP");
	public static final String ATTRIBUTE_EXPIRATION_DATE = PropertyUtil.getSchemaProperty("attribute_ExpirationDate");
	public static final String ATTRIBUTE_PG_MATERIAL_RESTRICTION = PropertyUtil.getSchemaProperty("attribute_pgMaterialRestriction");
	public static final String ATTRIBUTE_PG_MATERIAL_RESTRICTION_COMMENT = PropertyUtil.getSchemaProperty("attribute_pgMaterialRestrictionComment");
	public static final String ATTRIBUTE_V_IS_LAST_VERSION = PropertyUtil.getSchemaProperty("attribute_PLMReference.V_isLastVersion");
	public static final String ATTRIBUTE_PG_ISDESIGNDRIVEN = PropertyUtil.getSchemaProperty("attribute_pgIsDesignDriven");
	public static final String ATTRIBUTE_PG_REPORTSUBTYPE = PropertyUtil.getSchemaProperty("attribute_pgReportSubType");
	public static final String ATTRIBUTE_NET_QUANTITY = PropertyUtil.getSchemaProperty("attribute_Total");
	public static final String ATTRIBUTE_EOREPORT_DISPATCHER_OUTPUT_DIRECTORY =PropertyUtil.getSchemaProperty("attribute_pgEOReportDispatcherOutputDirectory");
	public static final String ATTRIBUTE_PG_EOREPORT_TIMEOUT =PropertyUtil.getSchemaProperty("attribute_pgEOReportTimeout");
	public static final String ATTRIBUTE_PG_EOREPORT_FREQUENCY =PropertyUtil.getSchemaProperty("attribute_pgEOReportFrequency");
	public static final String ATTRIBUTE_PG_EOREPORT_DISPATCHERPROCESS_DIRECTORY =PropertyUtil.getSchemaProperty("attribute_pgEOReportDispatcherProcessDirectory");	
	public static final String ATTRIBUTE_PG_EOREPORT_DISPATCHERCACHEBASELOCATION =PropertyUtil.getSchemaProperty("attribute_pgEOReportDispatcherCacheBaseLocation");

	public static final String ATTRIBUTE_PG_CHARACTERISTICSAMPLINGINFO = PropertyUtil.getSchemaProperty("attribute_pgCharacteristicSamplingInfo");
	public static final String ATTRIBUTE_ADHESIVEFLOWTESTDURATION=PropertyUtil.getSchemaProperty("attribute_pgAdhesiveFlowTestDuration");
	public static final String ATTRIBUTE_PGPERFUMEFLOWTESTDURATION=PropertyUtil.getSchemaProperty("attribute_pgPerfumeFlowTestDuration");
	
	public static final String ATTRIBUTE_PG_COMPONENTFAMILYID=PropertyUtil.getSchemaProperty("attribute_pngiComponentFamily.pngComponentFamilyId");
	public static final String ATTRIBUTE_PG_CADDESIGN_ORIGINATION = PropertyUtil.getSchemaProperty("attribute_pngiDesignPart.pngCADDesignOrigination");
	public static final String ATTRIBUTE_PG_LIFECYCLESTATUS = PropertyUtil.getSchemaProperty("attribute_pgLifeCycleStatus");
	public static final String ATTRIBUTE_ROLESEMANTICS = "RoleSemantics";
	public static final String ATTRIBUTE_PGISAUTHORIZEDTOUSE = PropertyUtil.getSchemaProperty("attribute_pgIsAuthorizedtoUse");
	public static final String ATTRIBUTE_PGISAUTHORIZEDTOPRODUCE = PropertyUtil.getSchemaProperty("attribute_pgIsAuthorizedtoProduce");
	public static final String ATTRIBUTE_PGISACTIVATED = PropertyUtil.getSchemaProperty("attribute_pgIsActivated");
	public static final String ATTRIBUTE_PG_WEBWIDTH = PropertyUtil.getSchemaProperty("attribute_pgWebWidth");

	
	public static final String ATTRIBUTE_PG_LPD_MODEL_UPDATE_STATUS = PropertyUtil.getSchemaProperty("attribute_pgLPDModelUpdateStatus");
	public static final String ATTRIBUTE_PG_LPD_REASON_FOR_CHANGE_GENERIC_MODEL = PropertyUtil.getSchemaProperty("attribute_pgLPDReasonForChangeGenericModel");
	public static final String ATTRIBUTE_PG_LPD_GENERIC_MODEL_CONFIGURATION = PropertyUtil.getSchemaProperty("attribute_pgLPDGenericModelConfiguration");
	
	//Attribute Selects
	public static final String SELECT_ATTRIBUTE_OVERRIDEALLOWEDONCHILD = "attribute["+ATTRIBUTE_OVERRIDEALLOWEDONCHILD+"]";
	public static final String SELECT_ATTRIBUTE_CHARACTERISTICSPECIFIC = "attribute["+ATTRIBUTE_CHARACTERISTICSPECIFIC+"]";
	public static final String SELECT_ATTRIBUTE_PG_PLRELVALUE = "attribute["+ATTRIBUTE_PG_PLRELVALUE+"]";
	public static final String SELECT_ATTRIBUTE_PLYNAME = "attribute["+ATTRIBUTE_PLY_NAME+"]";
	public static final String SELECT_ATTRIBUTE_PGLAYEREDPRODUCTAREA = "attribute["+ATTRIBUTE_PGLAYEREDPRODUCTAREA+"]";
	public static final String SELECT_ATTRIBUTE_PLYGROUPNAME = "attribute["+ATTRIBUTE_PLY_GROUP_NAME+"]";
	public static final String SELECT_ATTRIBUTE_SEQUENCENAME = "attribute["+ATTRIBUTE_SEQUENCE_NAME+"]";
	public static final String SELECT_ATTRIBUTE_PGBASEUOM = "attribute["+ATTRIBUTE_PGBASEUOM+"]";
	public static final String SELECT_ATTRIBUTE_PG_APPLICATION = "attribute["+ATTRIBUTE_PG_APPLICATION+"]";
	public static final String SELECT_ATTRIBUTE_V_DESCRIPTION = "attribute["+ATTRIBUTE_V_DESCRIPTION+"]";
	public static final String SELECT_ATTRIBUTE_V_NAME = "attribute["+ATTRIBUTE_V_NAME+"]";
	public static final String SELECT_ATTRIBUTE_REPORT_TYPE = "attribute["+ATTRIBUTE_REPORT_TYPE+"]";
	public static final String SELECT_ATTRIBUTE_PGREGION = "attribute["+ATTRIBUTE_PGREGION+"]";
	public static final String SELECT_ATTRIBUTE_PGSUBREGION = "attribute["+ATTRIBUTE_PGSUBREGION+"]";
	public static final String SELECT_ATTRIBUTE_PGENTERPRISETYPE = "attribute["+ATTRIBUTE_PGENTERPRISETYPE+"]";
	public static final String SELECT_ATTRIBUTE_REASON_FOR_CHANGE = "attribute["+ATTRIBUTE_REASON_FOR_CHANGE+"]";
	
	public static final String SELECT_ATTRIBUTE_PG_COMPONENTFAMILYID = "attribute["+ATTRIBUTE_PG_COMPONENTFAMILYID+"]";
	public static final String SELECT_ATTRIBUTE_ISVPLMCONTROLLED = "attribute["+ATTRIBUTE_ISVPLMCONTROLLED+"]";
	public static final String SELECT_ATTRIBUTE_VCHECKEDOUT = "attribute["+ATTRIBUTE_VCheckedOut+"]";
	public static final String SELECT_ATTRIBUTE_V_CHECKEDOUTUSER = "attribute["+ATTRIBUTE_V_CHECKEDOUTUSER+"]";

	public static final String SELECT_ATTRIBUTE_PGPRIMARYORGANIZATION = "attribute["+ATTRIBUTE_PGPRIMARYORGANIZATION+"]";
	public static final String SELECT_ATTRIBUTE_PGBUSINESSAREA = "attribute["+ATTRIBUTE_PGBUSINESSAREA+"]";
	public static final String SELECT_ATTRIBUTE_PGPRODUCTCATEGORYPLATFORM = "attribute["+ATTRIBUTE_PGPRODUCTCATEGORYPLATFORM+"]";
	public static final String SELECT_ATTRIBUTE_PGPRODUCTTECHNOLOGYPLATFORM = "attribute["+ATTRIBUTE_PGPRODUCTTECHNOLOGYPLATFORM+"]";
	public static final String SELECT_ATTRIBUTE_PGPRODUCTTECHNOLOGYCHASSIS = "attribute["+ATTRIBUTE_PGPRODUCTTECHNOLOGYCHASSIS+"]";
	public static final String SELECT_ATTRIBUTE_PGFRANCHISEPLATFORM = "attribute["+ATTRIBUTE_PGFRANCHISEPLATFORM+"]";	
	public static final String SELECT_ATTRIBUTE_PGDEFINITION = "attribute["+ATTRIBUTE_PGDEFINITION+"]";	
	public static final String SELECT_ATTRIBUTE_CATIA_PGNETWEIGHT = "attribute["+ATTRIBUTE_CATIA_PGNETWEIGHT+"]";	
		
	public static final String SELECT_ATTRIBUTE_PG_CLASS = "attribute["+ATTRIBUTE_PG_CLASS+"]";
	public static final String SELECT_ATTRIBUTE_PG_SUB_CLASS = "attribute["+ATTRIBUTE_PG_SUB_CLASS+"]";
	public static final String SELECT_ATTRIBUTE_PG_CHARACTERISTIC = "attribute["+ATTRIBUTE_PG_CHARACTERISTIC+"]";
	public static final String SELECT_ATTRIBUTE_PG_TARGET = "attribute["+ATTRIBUTE_PG_TARGET+"]";
	public static final String SELECT_ATTRIBUTE_PG_UPPER_TARGET = "attribute["+ATTRIBUTE_PG_UPPER_TARGET+"]";
	public static final String SELECT_ATTRIBUTE_PG_LOWER_TARGET = "attribute["+ATTRIBUTE_PG_LOWER_TARGET+"]";
	public static final String SELECT_ATTRIBUTE_PG_UNIT_OF_MEASURE = "attribute["+ATTRIBUTE_PG_UNIT_OF_MEASURE+"]";
	
	public static final String SELECT_ATTRIBUTE_PG_CHARACTERISTIC_CATEGORY = "attribute["+ATTRIBUTE_PG_CHARACTERISTIC_CATEGORY+"]";
	public static final String SELECT_ATTRIBUTE_PGLPDMODELTYPE = "attribute["+ATTRIBUTE_PGLPDMODELTYPE+"]";
	public static final String SELECT_ATTRIBUTE_VERSION_COMMENT = "attribute["+ATTRIBUTE_VERSION_COMMENT+"]";
	public static final String SELECT_ATTRIBUTE_PNG_GROSS_WEIGHT = "attribute["+ATTRIBUTE_PNG_GROSS_WEIGHT+"]";
	public static final String SELECT_ATTRIBUTE_PG_GROSSWEIGHTREAL = "attribute["+ATTRIBUTE_PG_GROSSWEIGHTREAL+"]";	
	public static final String SELECT_ATTRIBUTE_PG_COMPONENTQUANTITY = "attribute["+ATTRIBUTE_PG_COMPONENTQUANTITY+"]";
	public static final String SELECT_ATTRIBUTE_PGAUTHORINGAPPLICATION = "attribute["+ATTRIBUTE_PGAUTHORINGAPPLICATION+"]";
	public static final String SELECT_ATTRIBUTE_PG_PLATFORM_TYPE = "attribute["+ATTRIBUTE_PG_PLATFORM_TYPE+"]";
	public static final String SELECT_ATTRIBUTE_PG_CHASSIS_TYPEE = "attribute["+ATTRIBUTE_PG_CHASSIS_TYPE+"]";
	public static final String SELECT_ATTRIBUTE_PGRELEASECRITERIA = "attribute["+ATTRIBUTE_PGRELEASECRITERIA+"]";	
	public static final String SELECT_ATTRIBUTE_PGGROSSWEIGHTUOM = "attribute["+ATTRIBUTE_PGGROSSWEIGHTUOM+"]";	
	public static final String SELECT_ATTRIBUTE_TITLE = "attribute[" + DomainConstants.ATTRIBUTE_TITLE + "]";
	public static final String SELECT_ATTRIBUTE_PGGCAS_CORE = "attribute[" + ATTRIBUTE_PGGCAS_CORE + "]";
	public static final String SELECT_ATTRIBUTE_MATERIAL_FUNCTION_CORE = "attribute[" + ATTRIBUTE_MATERIAL_FUNCTION_CORE + "]";
	public static final String SELECT_ATTRIBUTE_REPORTED_FUNCTION_CORE = "attribute[" + ATTRIBUTE_REPORTED_FUNCTION_CORE + "]";	
	public static final String SELECT_ATTRIBUTE_PGGCAS_COMP = "attribute[" + ATTRIBUTE_PGGCAS_COMP + "]";
	public static final String SELECT_ATTRIBUTE_MATERIAL_FUNCTION_COMP = "attribute[" + ATTRIBUTE_MATERIAL_FUNCTION_COMP + "]";
	public static final String SELECT_ATTRIBUTE_REPORTED_FUNCTION_COMP = "attribute[" + ATTRIBUTE_REPORTED_FUNCTION_COMP + "]";
	public static final String SELECT_ATTRIBUTE_PGPLRELVALUE = "attribute["+ATTRIBUTE_PGPLRELVALUE+"]";
	public static final String SELECT_ATTRIBUTE_PGCHARSPECIFICS = "attribute["+ATTRIBUTE_PGCHARSPECIFICS+"]";
	public static final String SELECT_ATTRIBUTE_CHARACTERISTICSLOWERSPECIFICATIONLIMIT = "attribute["+ATTRIBUTE_CHARACTERISTICSLOWERSPECIFICATIONLIMIT+"]";
	public static final String SELECT_ATTRIBUTE_CHARACTERISTICSLOWERROUTINERELEASELIMIT = "attribute["+ATTRIBUTE_CHARACTERISTICSLOWERROUTINERELEASELIMIT+"]";
	public static final String SELECT_ATTRIBUTE_CHARACTERISTICSUPPERROUTINERELEASELIMIT = "attribute["+ATTRIBUTE_CHARACTERISTICSUPPERROUTINERELEASELIMIT+"]";
	public static final String SELECT_ATTRIBUTE_CHARACTERISTICSUPPERSPECIFICATIONLIMIT = "attribute["+ATTRIBUTE_CHARACTERISTICSUPPERSPECIFICATIONLIMIT+"]";
	public static final String SELECT_ATTRIBUTE_PGACTIONREQUIRED = "attribute["+ATTRIBUTE_PGACTIONREQUIRED+"]";
	public static final String SELECT_ATTRIBUTE_PGREPORTTYPE = "attribute["+ATTRIBUTE_PGREPORTTYPE+"]";
	public static final String SELECT_ATTRIBUTE_PGTESTGROUP = "attribute["+ATTRIBUTE_PGTESTGROUP+"]";
	public static final String SELECT_ATTRIBUTE_PGAPPLICATION = "attribute["+ATTRIBUTE_PGAPPLICATION+"]";
	public static final String SELECT_ATTRIBUTE_PGGROSSWEIGHT = "attribute["+ATTRIBUTE_PGGROSSWEIGHT+"]";
	public static final String SELECT_ATTRIBUTE_PGNETWEIGHTUOM = "attribute["+ATTRIBUTE_PGNETWEIGHTUOM+"]";
	public static final String SELECT_ATTRIBUTE_PGNETWEIGHT = "attribute["+ATTRIBUTE_NET_WEIGHT+"]";
	public static final String SELECT_ATTRIBUTE_PGQUANTITY = "attribute["+ATTRIBUTE_PGQUANTITY+"]";
	public static final String SELECT_ATTRIBUTE_QUANTITY = "attribute["+ATTRIBUTE_QUANTITY+"]";
	public static final String SELECT_ATTRIBUTE_PGSIZE = "attribute["+ATTRIBUTE_PGSIZE+"]";
	public static final String SELECT_ATTRIBUTE_PG_DSMPRODUCTSIZE = "attribute["+ATTRIBUTE_PG_DSMPRODUCTSIZE+"]";
	public static final String SELECT_ATTRIBUTE_PGPLMENTITY_USAGE = "attribute["+ATTRIBUTE_PGPLMENTITY_USAGE+"]";
	public static final String SELECT_ATTRIBUTE_PLMREP_ONCE_INSTANTIABLE = "attribute["+ATTRIBUTE_PLMREP_ONCE_INSTANTIABLE+"]";
	public static final String SELECT_ATTRIBUTE_LPABSTRACT_MANDATORY = "attribute["+ATTRIBUTE_LPABSTRACT_MANDATORY+"]";
	public static final String SELECT_ATTRIBUTE_MANDATORY_CHARACTERISTIC = "attribute["+ATTRIBUTE_MANDATORY_CHARACTERISTIC+"]";
	public static final String SELECT_ATTRIBUTE_REPORT_TO_NEAREST = "attribute["+ATTRIBUTE_PGREPORTTONEAREST+"]";	
	public static final String SELECT_ATTRIBUTE_PGMETHODSPECIFICS = "attribute["+ATTRIBUTE_PGMETHODSPECIFICS+"]";	
	public static final String SELECT_ATTRIBUTE_PNGLPDORIGINATEDFROM = "attribute["+ATTRIBUTE_PNGLPDORIGINATEDFROM+"]";
	public static final String SELECT_ATTRIBUTE_PNGLPDORIGINATEDFROMROOT = "attribute["+ATTRIBUTE_PNGLPDORIGINATEDFROMROOT+"]";
	public static final String SELECT_ATTRIBUTE_PG_DESIGNSPECIFICS = "attribute["+ATTRIBUTE_PG_DESIGNSPECIFICS+"]";
	public static final String SELECT_ATTRIBUTE_PGCHANGE = "attribute["+ATTRIBUTE_PGCHANGE+"]";
	public static final String SELECT_ATTRIBUTE_PGRMPGCAS = "attribute["+ATTRIBUTE_PGRMPGCAS+"]";
	public static final String SELECT_ATTRIBUTE_PG_ORIGINATINGSOURCE = "attribute["+ATTRIBUTE_PG_ORIGINATINGSOURCE+"]";
	public static final String SELECT_ATTRIBUTE_PG_MATERIAL_FUNCTION = "attribute["+ATTRIBUTE_PG_MATERIAL_FUNCTION+"]";
	public static final String SELECT_ATTRIBUTE_EXPIRATION_DATE = "attribute["+ATTRIBUTE_EXPIRATION_DATE+"]";
	public static final String SELECT_ATTRIBUTE_PG_MATERIAL_RESTRICTION = "attribute["+ATTRIBUTE_PG_MATERIAL_RESTRICTION+"]";
	public static final String SELECT_ATTRIBUTE_PG_MATERIAL_RESTRICTION_COMMENT = "attribute["+ATTRIBUTE_PG_MATERIAL_RESTRICTION_COMMENT+"]";
	public static final String SELECT_ATTRIBUTE_V_IS_LAST_VERSION = "attribute["+ATTRIBUTE_V_IS_LAST_VERSION+"]";
	public static final String SELECT_ATTRIBUTE_PG_ISDESIGNDRIVEN = "attribute["+ATTRIBUTE_PG_ISDESIGNDRIVEN+"]";
	public static final String SELECT_ATTRIBUTE_APPLICABLETYPE = "attribute["+ATTRIBUTE_APPLICABLETYPE+"]";
	public static final String SELECT_ATTRIBUTE_CRITERIA1 = "attribute["+ATTRIBUTE_CRITERIA_1+"]";
	public static final String SELECT_ATTRIBUTE_CRITERIA2 = "attribute["+ATTRIBUTE_CRITERIA_2+"]";
	public static final String SELECT_ATTRIBUTE_CRITERIA3 = "attribute["+ATTRIBUTE_CRITERIA_3+"]";
	public static final String SELECT_ATTRIBUTE_CRITERIA4 = "attribute["+ATTRIBUTE_CRITERIA_4+"]";
	public static final String SELECT_ATTRIBUTE_CRITERIA5 = "attribute["+ATTRIBUTE_CRITERIA_5+"]";
	public static final String SELECT_ATTRIBUTE_CRITERIA6 = "attribute["+ATTRIBUTE_CRITERIA_6+"]";
	public static final String SELECT_ATTRIBUTE_CRITERIA7 = "attribute["+ATTRIBUTE_CRITERIA_7+"]";
	public static final String SELECT_ATTRIBUTE_PG_REPORTSUBTYPE = "attribute["+ATTRIBUTE_PG_REPORTSUBTYPE+"]";
	public static final String SELECT_ATTRIBUTE_EOREPORT_DISPATCHER_OUTPUT_DIRECTORY ="attribute["+ATTRIBUTE_EOREPORT_DISPATCHER_OUTPUT_DIRECTORY+"]";
	public static final String SELECT_ATTRIBUTE_PG_EOREPORT_TIMEOUT ="attribute["+ATTRIBUTE_PG_EOREPORT_TIMEOUT+"]";
	public static final String SELECT_ATTRIBUTE_PG_EOREPORT_REPORTFREQUENCY ="attribute["+ATTRIBUTE_PG_EOREPORT_FREQUENCY+"]";
	public static final String SELECT_ATTRIBUTE_PG_EOREPORT_DISPATCHERPROCESS_DIRECTORY ="attribute["+ATTRIBUTE_PG_EOREPORT_DISPATCHERPROCESS_DIRECTORY+"]";
  	public static final String SELECT_ATTRIBUTE_PG_EOREPORT_DISPATCHERCACHEBASELOCATION ="attribute["+ATTRIBUTE_PG_EOREPORT_DISPATCHERCACHEBASELOCATION+"]";
	public static final String SELECT_ATTRIBUTE_PROGRAM_ARGUMENTS ="attribute["+ATTRIBUTE_PROGRAM_ARGUMENTS+"]";
	public static final String SELECT_ATTRIBUTE_COMPLETION_STATUS ="attribute["+ATTRIBUTE_COMPLETION_STATUS+"]";
	public static final String SELECT_ATTRIBUTE_PGLPDORIGINATEDFROMGENERICMODEL ="attribute["+ATTRIBUTE_PGLPDORIGINATEDFROMGENERICMODEL+"]";
	public static final String SELECT_ATTRIBUTE_PGPRODUCTEXPOSEDTOCHILDREN ="attribute["+ATTRIBUTE_PGPRODUCTEXPOSEDTOCHILDREN+"]";
	public static final String SELECT_ATTRIBUTE_PGPRODUCTMARKETEDASCHILDRENPRODUCT ="attribute["+ATTRIBUTE_PGPRODUCTMARKETEDASCHILDRENPRODUCT+"]";
	public static final String SELECT_ATTRIBUTE_PGDOESTHEPRODUCTREQUIRECHILDSAFEDESIGN ="attribute["+ATTRIBUTE_PGDOESTHEPRODUCTREQUIRECHILDSAFEDESIGN+"]";
	
	public static final String SELECT_ATTRIBUTE_PG_CHARACTERISTICSAMPLINGINFO="attribute["+ATTRIBUTE_PG_CHARACTERISTICSAMPLINGINFO+"]";
	public static final String SELECT_ATTRIBUTE_ADHESIVEFLOWTESTDURATION="attribute["+ATTRIBUTE_ADHESIVEFLOWTESTDURATION+"]";
	public static final String SELECT_ATTRIBUTE_PGPERFUMEFLOWTESTDURATION="attribute["+ATTRIBUTE_PGPERFUMEFLOWTESTDURATION+"]";
	public static final String SELECT_ATTRIBUTE_PGSAMPLING="attribute["+ATTRIBUTE_PGSAMPLING+"]";
	public static final String SELECT_ATTRIBUTE_PGROLLUPNETWEIGHTTOCOP = "attribute["+ATTRIBUTE_PGROLLUPNETWEIGHTTOCOP+"]";//Apollo A10-915 Changes
	public static final String SELECT_ATTRIBUTE_PGTMLOGIC="attribute["+ATTRIBUTE_PGTMLOGIC+"]";
	public static final String SELECT_ATTRIBUTE_PGCRITICALITYFACTOR="attribute["+ATTRIBUTE_PGCRITICALITYFACTOR+"]";
	public static final String SELECT_ATTRIBUTE_PGSUBGROUP="attribute["+ATTRIBUTE_PGSUBGROUP+"]";
	public static final String SELECT_ATTRIBUTE_CHARACTERISTIC_CATEGORY="attribute["+ATTRIBUTE_CHARACTERISTIC_CATEGORY+"]";
	public static final String SELECT_ATTRIBUTE_CHARACTERISTIC_NOTES="attribute["+ATTRIBUTE_CHARACTERISTIC_NOTES+"]";
	public static final String SELECT_ATTRIBUTE_PLMPARAMDISPLAYUNIT="attribute["+ATTRIBUTE_PLMPARAMDISPLAYUNIT+"]";
	public static final String SELECT_ATTRIBUTE_ENTERPRISEPART_PGPRODUCTTECHNOLOGYCHASSIS="attribute["+ATTRIBUTE_ENTERPRISEPART_PGPRODUCTTECHNOLOGYCHASSIS+"]";
	public static final String SELECT_ATTRIBUTE_ENTERPRISEPART_PGPRODUCTTECHNOLOGYPLATFORM="attribute["+ATTRIBUTE_ENTERPRISEPART_PGPRODUCTTECHNOLOGYPLATFORM+"]";
	public static final String SELECT_ATTRIBUTE_ENTERPRISEPART_PGFRANCHISEPLATFORM="attribute["+ATTRIBUTE_ENTERPRISEPART_PGFRANCHISEPLATFORM+"]";
	public static final String SELECT_ATTRIBUTE_PGLPDPERFCHARACTERISTICSUPDATESTATUS = "attribute["+ATTRIBUTE_PGLPDPERFCHARACTERISTICSUPDATESTATUS+"]";
	public static final String SELECT_ATTRIBUTE_METHODNAME = "attribute["+ATTRIBUTE_METHODNAME+"]";
	public static final String SELECT_ATTRIBUTE_PROGRAMNAME = "attribute["+ATTRIBUTE_PROGRAMNAME+"]";
	public static final String SELECT_ATTRIBUTE_PGPARAMETERARGUMENT1 = "attribute["+ATTRIBUTE_PGPARAMETERARGUMENT1+"]";
	public static final String SELECT_ATTRIBUTE_PG_INTENDED_MARKETS = "attribute["+ATTRIBUTE_PG_INTENDED_MARKETS+"]";
	public static final String SELECT_ATTRIBUTE_ENTERPRISEPART_PGBUSINESSAREA = "attribute["+ATTRIBUTE_ENTERPRISEPART_PGBUSINESSAREA+"]";
	public static final String SELECT_ATTRIBUTE_ENTERPRISEPART_PGPRODUCTCATEGORYPLATFORM = "attribute["+ATTRIBUTE_ENTERPRISEPART_PGPRODUCTCATEGORYPLATFORM+"]";
	public static final String SELECT_ATTRIBUTE_ENTERPRISEPART_PGLPDMODELTYPE = "attribute["+ATTRIBUTE_ENTERPRISEPART_PGLPDMODELTYPE+"]";
	public static final String SELECT_ATTRIBUTE_ENTERPRISEPART_PGLPDDEFINITION = "attribute["+ATTRIBUTE_ENTERPRISEPART_PGLPDDEFINITION+"]";
	public static final String SELECT_ATTRIBUTE_ENTERPRISEPART_PGLPDREGION = "attribute["+ATTRIBUTE_ENTERPRISEPART_PGLPDREGION+"]";
	public static final String SELECT_ATTRIBUTE_ENTERPRISEPART_PGLPDSUBREGION = "attribute["+ATTRIBUTE_ENTERPRISEPART_PGLPDSUBREGION+"]";
	public static final String SELECT_ATTRIBUTE_ERROR_MESSAGE = "attribute["+ATTRIBUTE_ERROR_MESSAGE+"]";
	public static final String SELECT_ATTRIBUTE_CRITERIA_EXPRESSION = "attribute["+ATTRIBUTE_CRITERIA_EXPRESSION+"]";
	public static final String SELECT_ATTRIBUTE_CRITERIA_1 = "attribute["+ATTRIBUTE_CRITERIA_1+"]";
	public static final String SELECT_ATTRIBUTE_CRITERIA_2 = "attribute["+ATTRIBUTE_CRITERIA_2+"]";
	public static final String SELECT_ATTRIBUTE_CRITERIA_3 = "attribute["+ATTRIBUTE_CRITERIA_3+"]";
	public static final String SELECT_ATTRIBUTE_CRITERIA_4 = "attribute["+ATTRIBUTE_CRITERIA_4+"]";
	public static final String SELECT_ATTRIBUTE_CRITERIA_5 = "attribute["+ATTRIBUTE_CRITERIA_5+"]";
	public static final String SELECT_ATTRIBUTE_CRITERIA_6 = "attribute["+ATTRIBUTE_CRITERIA_6+"]";
	public static final String SELECT_ATTRIBUTE_CRITERIA_7 = "attribute["+ATTRIBUTE_CRITERIA_7+"]";
	public static final String SELECT_ATTRIBUTE_CRITERIA_8 = "attribute["+ATTRIBUTE_CRITERIA_8+"]";
	public static final String SELECT_ATTRIBUTE_CRITERIA_9 = "attribute["+ATTRIBUTE_CRITERIA_9+"]";
	public static final String SELECT_ATTRIBUTE_PG_LIFECYCLESTATUS = "attribute["+ATTRIBUTE_PG_LIFECYCLESTATUS+"]";
	public static final String SELECT_ATTRIBUTE_ROLESEMANTICS = "attribute["+ATTRIBUTE_ROLESEMANTICS+"]";
	public static final String SELECT_ATTRIBUTE_PGLOWERSPECIFICATIONLIMIT = "attribute["+ATTRIBUTE_PGLOWERSPECIFICATIONLIMIT+"]";
	public static final String SELECT_ATTRIBUTE_PGUPPERSPECIFICATIONLIMIT = "attribute["+ATTRIBUTE_PGUPPERSPECIFICATIONLIMIT+"]";
	public static final String SELECT_ATTRIBUTE_EVALUATED_CRITERIA="attribute["+ATTRIBUTE_EVALUATED_CRITERIA+"]";	
	public static final String SELECT_ATTRIBUTE_PG_LPD_MODEL_UPDATE_STATUS = "attribute["+ATTRIBUTE_PG_LPD_MODEL_UPDATE_STATUS+"]";
	public static final String SELECT_ATTRIBUTE_PG_LPD_REASON_FOR_CHANGE_GENERIC_MODEL = "attribute["+ATTRIBUTE_PG_LPD_REASON_FOR_CHANGE_GENERIC_MODEL+"]";
	public static final String SELECT_ATTRIBUTE_PGMANUFACTURING_MATURITYSTATUS = "attribute["+ATTRIBUTE_PGMANUFACTURING_MATURITYSTATUS+"]";
	public static final String SELECT_ATTRIBUTE_PG_LPD_GENERIC_MODEL_CONFIGURATION = "attribute["+ATTRIBUTE_PG_LPD_GENERIC_MODEL_CONFIGURATION+"]";
	public static final String SELECT_ATTRIBUTE_PG_WEBWIDTH = "attribute["+ATTRIBUTE_PG_WEBWIDTH+"]";
	public static final String SELECT_ATTRIBUTE_PG_WEBWIDTH_INPUT = "attribute["+ATTRIBUTE_PG_WEBWIDTH+"].inputvalue";	
	
	public static final String SELECT_MAJOR_REVISION = "majorrevision";
	public static final String SELECT_MINOR_REVISION = "minorrevision";
	public static final String SELECT_PREVIOUS_ID = "previous.id";
	public static final String SELECT_PHYSICAL_ID = "physicalid";
	public static final String SELECT_REL_PHYSICAL_ID = "physicalid[connection]";
	public static final String SELECT_LOGICAL_ID = "logicalid";
	public static final String SELECT_MAJOR_ID = "majorid"; //PROD Issue Changes
	public static final String SELECT_REL_LOGICAL_ID = "logicalid[connection]";
	public static final String SELECT_REL_MAJOR_ID = "majorid[connection]";
	public static final String STR_SELECT_TO_PHYSICALID = "to.physicalid";
	public static final String SELECT_ISLAST = "islast";
	public static final String SELECT_LAST = "last";
	public static final String SELECT_SEMANTIC_PATH_ID = "paths[SemanticRelation].path.id";
	public static final String SELECT_ELEMENT_PHYSICAL_ID = "element.physicalid";
	public static final String SELECT_ELEMENT_TYPE = "element.type";
	public static final String SELECT_ELEMENT_ZERO_PHYSICAL_ID = "element[0].physicalid";
	public static final String SELECT_ELEMENT_ZERO_TYPE = "element[0].type";
	public static final String SELECT_OWNER_ID = "owner.id";
	public static final String SELECT_OWNER_CURRENT = "owner.current";
	public static final String SELECT_OWNER_TYPE = "owner.type";
	public static final String SELECT_NEXT_ID = "next.id";
	public static final String SELECT_ELEMENT_KIND = "element.kind";
	public static final String SELECT_ELEMENT_RELEVANT = "element.relevant";
	public static final String SELECT_RELEASED_ACTUAL = "state[RELEASED].actual";
	public static final String SELECT_RESERVED = "reserved";
	public static final String SELECT_RESERVEDBY = "reservedby";

	public static final String PERSON_USER_AGENT = PropertyUtil.getSchemaProperty("person_UserAgent");
	public static final String PERSON_PLMADMIN = "PLM Admin";

	//Type Constants
	public static final String TYPE_RAW_MATERIAL = PropertyUtil.getSchemaProperty("type_RawMaterial");
	public static final String TYPE_PG_RAW_MATERIAL = PropertyUtil.getSchemaProperty("type_pgRawMaterial");
	public static final String TYPE_PGASSEMBLEDPRODUCTPART = PropertyUtil.getSchemaProperty("type_pgAssembledProductPart");
	public static final String TYPE_CHARACTERISTICSMASTER = PropertyUtil.getSchemaProperty("type_CharacteristicMaster");
	
	public static final String TYPE_VPMREFERENCE = PropertyUtil.getSchemaProperty("type_VPMReference");
	public static final String TYPE_DSC_MATREF_REF_CORE = PropertyUtil.getSchemaProperty("type_dsc_matref_ref_Core");
	public static final String TYPE_PLMPARAMETER = PropertyUtil.getSchemaProperty("type_PlmParameter");
	public static final String TYPE_PG_PLICHARACTERISTIC = PropertyUtil.getSchemaProperty("type_pgPLICharacteristic");
	public static final String TYPE_PG_PLICHARACTERISTICTYPE = PropertyUtil.getSchemaProperty("type_pgPLICharacteristicType");
	public static final String TYPE_PLMDMTDocument = PropertyUtil.getSchemaProperty("type_PLMDMTDocument");
	public static final String TYPE_PG_PERFORMANCECHARACTERISTICS = PropertyUtil.getSchemaProperty("type_pgPerformanceCharacteristic");
	public static final String TYPE_PLISEGMENT = PropertyUtil.getSchemaProperty("type_pgPLISegment");
	public static final String TYPE_PG_PLIORGANIZATION = PropertyUtil.getSchemaProperty("type_pgPLIOrganizationChangeManagement");
	public static final String TYPE_PLI_MATERIALFUNCTION = PropertyUtil.getSchemaProperty("type_pgPLIMaterialFunctionGlobal");
    public static final String TYPE_PG_PLI_BUSINESSAREA=PropertyUtil.getSchemaProperty("type_pgPLIBusinessArea");
	public static final String TYPE_PG_CHASSIS = PropertyUtil.getSchemaProperty("type_pgPLIChassis");
	public static final String TYPE_PG_PLATFORM = PropertyUtil.getSchemaProperty("type_pgPLIPlatform");
	public static final String TYPE_PG_PLI_PRODUCTTECHNOLOGYPLATFORM = PropertyUtil.getSchemaProperty("type_pgPLIProductTechnologyPlatform");
	public static final String TYPE_PG_PLI_PRODUCTCATEGORYPLATFORM = PropertyUtil.getSchemaProperty("type_pgPLIProductCategoryPlatform");
		
	public static final String TYPE_PLM_PARAMETER = PropertyUtil.getSchemaProperty("type_PlmParameter");
	public static final String TYPE_PG_STANDARD_OPERATING_PROCEDURE = PropertyUtil.getSchemaProperty("type_pgStandardOperatingProcedure");
	public static final String TYPE_PG_ILLUSTRATION = PropertyUtil.getSchemaProperty("type_pgIllustration");
	public static final String TYPE_PG_QUALITY_SPECIFICATION = PropertyUtil.getSchemaProperty("type_pgQualitySpecification");
	public static final String TYPE_ASSEMBLED_PRODUCT_PART = PropertyUtil.getSchemaProperty("type_pgAssembledProductPart");
	
	public static final String TYPE_DSC_MATREF_REF_MATERIAL = PropertyUtil.getSchemaProperty("type_dsc_matref_ref_Material");
	public static final String TYPE_DSC_MATREF_REP_COMPOSITE = PropertyUtil.getSchemaProperty("type_dsc_matref_rep_Composite");
	public static final String TYPE_PGPLIMATERIAL_FUNCTION_GLOBAL = PropertyUtil.getSchemaProperty("type_pgPLIMaterialFunctionGlobal");
	public static final String TYPE_PGPLI_REPORTED_FUNCTION = PropertyUtil.getSchemaProperty("type_pgPLIReportedFunction");
	public static final String TYPE_PGPLIUNITOFMEASUREMASTERLIST = PropertyUtil.getSchemaProperty("type_pgPLIUnitofMeasureMasterList");
	public static final String TYPE_PGPLICHARACTERISTICTYPE = PropertyUtil.getSchemaProperty("type_pgPLICharacteristicType");
	public static final String TYPE_PLANT = PropertyUtil.getSchemaProperty("type_Plant");
	public static final String TYPE_3DSHAPE = PropertyUtil.getSchemaProperty("type_3DShape");		
	public static final String TYPE_DRAWING = PropertyUtil.getSchemaProperty("type_Drawing");
	public static final String TYPE_PLMTEMPLATEREPREFERENCE  = PropertyUtil.getSchemaProperty("type_PLMTemplateRepReference");
	public static final String TYPE_JOB = PropertyUtil.getSchemaProperty("type_Job");
	public static final String TYPE_PG_PLICHASSISPRODUCTSIZE = PropertyUtil.getSchemaProperty("type_pgPLIChassisProductSize");
	public static final String TYPE_CRITERIA = PropertyUtil.getSchemaProperty("type_Criteria");
	public static final String TYPE_DSC_MATREF_REF_COVERING = PropertyUtil.getSchemaProperty("type_dsc_matref_ref_Covering");
	public static final String TYPE_CATCOMPONENTSFAMILYGENERICCONNECTION = PropertyUtil.getSchemaProperty("type_CATComponentsFamilyGenericConnection");
	public static final String TYPE_CATCOMPONENTSFAMILYPROXYTOELEMENT = PropertyUtil.getSchemaProperty("type_CATComponentsFamilyProxyToElement");
	public static final String TYPE_CATCOMPONENTSFAMILYEXPLICIT = PropertyUtil.getSchemaProperty("type_CATComponentsFamilyExplicit");
	public static final String TYPE_DSC_MAT_CNX_COVERING_DESIGN = PropertyUtil.getSchemaProperty("type_dsc_mat_cnx_Covering_Design");
	public static final String TYPE_DSC_MAT_CNX_CORE_DESIGN = PropertyUtil.getSchemaProperty("type_dsc_mat_cnx_Core_Design");
	public static final String TYPE_PGCONFIGURATIONADMIN =PropertyUtil.getSchemaProperty("type_pgConfigurationAdmin");
	public static final String TYPE_NONDISCLOSUREAGREEMENT = PropertyUtil.getSchemaProperty("type_NonDisclosureAgreement");
	public static final String TYPE_PGEOREPORTDOCUMENT = PropertyUtil.getSchemaProperty("type_pgEOReportDocument");
	public static final String TYPE_PGBACKGROUDPROCESS = PropertyUtil.getSchemaProperty("type_pgBackgroundProcess");
	public static final String TYPE_PGIMPACTANALYSIS=PropertyUtil.getSchemaProperty("type_pgImpactAnalysis");
	public static final String TYPE_PGLPDGENERICCONFIGURATION=PropertyUtil.getSchemaProperty("type_pgLPDGenericConfiguration");
	public static final String TYPE_PGPLILPDREGION=PropertyUtil.getSchemaProperty("type_pgPLILPDRegion");
	public static final String TYPE_PGPLILPDSUBREGION=PropertyUtil.getSchemaProperty("type_pgPLILPDSubRegion");

	//Relationship Constants
	public static final String RELATIONSHIP_PARAMETERAGGREGATION = PropertyUtil.getSchemaProperty("relationship_ParameterAggregation");
	public static final String RELATIONSHIP_PG_PLRELATEDDATA = PropertyUtil.getSchemaProperty("relationship_pgPLRelatedData");
	public static final String RELATIONSHIP_PGRELATEDMATERIAL = PropertyUtil.getSchemaProperty("relationship_pgRelatedMaterial");
	public static final String RELATIONSHIP_VPMRepInstance = PropertyUtil.getSchemaProperty("relationship_VPMRepInstance");
	public static final String RELATIONSHIP_CLASSIFIED_ITEM = PropertyUtil.getSchemaProperty("relationship_ClassifiedItem");
	public static final String RELATIONSHIP_PARTFAMILYREFERENCE = PropertyUtil.getSchemaProperty("relationship_PartFamilyReference");
	public static final String RELATIONSHIP_PDTEMPLATE_SEGMENT = PropertyUtil.getSchemaProperty("relationship_pgPDTemplatestopgPLISegment");
	public static final String RELATIONSHIP_PRIMARYORGANIZATION = PropertyUtil.getSchemaProperty("relationship_pgPrimaryOrganization");
	public static final String RELATIONSHIP_MATERIAL_FUNCTIONALITY = PropertyUtil.getSchemaProperty("relationship_MaterialFunctionality");
	public static final String RELATIONSHIP_PG_DOCUMENT_TO_CHASSIS= PropertyUtil.getSchemaProperty("relationship_pgDocumentToChassis");
	public static final String RELATIONSHIP_PG_DOCUMENT_TO_PLATFORM = PropertyUtil.getSchemaProperty("relationship_pgDocumentToPlatform");
	public static final String RELATIONSHIP_PLATFORM_TO_BUSINESSAREA = PropertyUtil.getSchemaProperty("relationship_pgPlatformToBusinessArea");
	public static final String RELATIONSHIP_PG_PLATFORM_TO_CHASSIS = PropertyUtil.getSchemaProperty("relationship_pgPlatformToChassis");
	public static final String RELATIONSHIP_PG_PLATFORM_TO_PLATFORM = PropertyUtil.getSchemaProperty("relationship_pgPlatformToPlatform");
	public static final String RELATIONSHIP_PGDOCUMENT_TO_BUSINESSAREA = PropertyUtil.getSchemaProperty("relationship_pgDocumentToBusinessArea");
	public static final String RELATIONSHIP_PG_INTENDED_MARKETS = PropertyUtil.getSchemaProperty("relationship_pgIntendedMarkets");
	public static final String RELATIONSHIP_VPLMREL_PLMCONNECTION_V_OWNER = PropertyUtil.getSchemaProperty("relationship_VPLMrel%PLMConnection%V_Owner");
	public static final String RELATIONSHIP_VPMINSTANCE = PropertyUtil.getSchemaProperty("relationship_VPMInstance");
	public static final String RELATIONSHIP_PARAMETER_AGGREGATION = PropertyUtil.getSchemaProperty("relationship_ParameterAggregation");
	public static final String RELATIONSHIP_PGPDTEMPLATES_TOP_GPLI_REPORTED_FUNCTION = PropertyUtil.getSchemaProperty("relationship_pgPDTemplatestopgPLIReportedFunction");
	public static final String RELATIONSHIP_RELATED_MATERIAL = PropertyUtil.getSchemaProperty("relationship_pgRelatedMaterial");
	public static final String RELATIONSHIP_MATERIAL_DOMAIN_INSTANCE = PropertyUtil.getSchemaProperty("relationship_MaterialDomainInstance");
	public static final String RELATIONSHIP_MANUFACTURINGRESPONSIBILITY     = PropertyUtil.getSchemaProperty("relationship_ManufacturingResponsibility");
	public static final String RELATIONSHIP_PART_SPECIFICATION = PropertyUtil.getSchemaProperty("relationship_PartSpecification");
	public static final String RELATIONSHIP_EXTENDED_DATA = PropertyUtil.getSchemaProperty("relationship_Characteristic");
	public static final String RELATIONSHIP_CHARACTERISTIC_TEST_METHOD = PropertyUtil.getSchemaProperty("relationship_CharacteristicTestMethod");
	public static final String RELATIONSHIP_EXCLICENSEDPEOPLE = PropertyUtil.getSchemaProperty("relationship_EXCLicensedPeople");
	
	public static final String RELATIONSHIP_PGORIGINATEDFROM = PropertyUtil.getSchemaProperty("relationship_pgOriginatedFrom");
	public static final String RELATIONSHIP_DERIVED_CHARACTERISTIC = PropertyUtil.getSchemaProperty("relationship_DerivedCharacteristic");
	
	public static final String RELATIONSHIP_EVALUATED_PART = PropertyUtil.getSchemaProperty("relationship_pgEvaluatedPart");
	public static final String RELATIONSHIP_CRITERIA_OUTPUT = PropertyUtil.getSchemaProperty("relationship_CriteriaOutput");
	public static final String RELATIONSHIP_PENDING_JOB=PropertyUtil.getSchemaProperty("relationship_PendingJob");
	public static final String RELATIONSHIP_PGBACKGROUNDPROCESSRELATEDOBJECTS=PropertyUtil.getSchemaProperty("relationship_pgBackgroundProcessRelatedObjects");
	public static final String RELATIONSHIP_PGIMPACTANALYSIS=PropertyUtil.getSchemaProperty("relationship_pgImpactAnalysis");
  	public static final String RELATIONSHIP_PG_PLI_MATERIAL_CERTIFICATIONS = PropertyUtil.getSchemaProperty("relationship_pgPLIMaterialCertifications");
	public static final String RELATIONSHIP_PGLPDRELATEDGENERICCONFIGURATION=PropertyUtil.getSchemaProperty("relationship_pgLPDRelatedGenericConfiguration");
	public static final String RELATIONSHIP_PGPLLPDREGIONTOLPDSUBREGION=PropertyUtil.getSchemaProperty("relationship_pgPLLPDRegionToLPDSubRegion");
	public static final String RELATIONSHIP_PGPLBUSINESSAREATOLPDREGION=PropertyUtil.getSchemaProperty("relationship_pgPLBusinessAreaToLPDRegion");

	//Policy Definition
	public static final String POLICY_VPLM_SMB_RESOURCE = PropertyUtil.getSchemaProperty("policy_VPLM_SMB_Resource");
	public static final String POLICY_CRITERIA = PropertyUtil.getSchemaProperty("policy_Criteria");
	public static final String POLICY_VPLM_SMB_DEFINITION_DOCUMENT = PropertyUtil.getSchemaProperty("policy_VPLM_SMB_Definition_Document");
	public static final String POLICY_PGBACKGROUNDPROCESSPOLICY = PropertyUtil.getSchemaProperty("policy_pgBackgroundProcessPolicy");
	public static final String POLICY_PGIMPACTANALYSIS=PropertyUtil.getSchemaProperty("policy_pgImpactAnalysis");
	public static final String POLICY_VPLM_SMB_DEFINITION = PropertyUtil.getSchemaProperty("policy_VPLM_SMB_Definition");
	public static final String POLICY_PGLPDGENERICCONFIGURATION=PropertyUtil.getSchemaProperty("policy_pgLPDGenericConfiguration");
	
	//Vault Constants
	public static final String VAULT_VPLM = PropertyUtil.getSchemaProperty("vault_vplm");
	public static final String VAULT_ESERVICE_PRODUCTION = PropertyUtil.getSchemaProperty("vault_eServiceProduction");
	
	public static final String INTERFACE_REALPARAMETER = PropertyUtil.getSchemaProperty("interface_RealParameter");
	public static final String INTERFACE_OBSCURECHARACTERISTIC = PropertyUtil.getSchemaProperty("interface_ObscureCharacteristic");
	public static final String INTERFACE_BOOLEANPARAMETER = PropertyUtil.getSchemaProperty("interface_BooleanParameter");
	public static final String INTERFACE_SUBJECTIVITYPARAMETER = PropertyUtil.getSchemaProperty("interface_SubjectivityParameter");
	public static final String INTERFACE_STRINGPARAMETER = PropertyUtil.getSchemaProperty("interface_StringParameter");
	public static final String INTERFACE_INTEGERPARAMETER = PropertyUtil.getSchemaProperty("interface_IntegerParameter");
	
	public static final String SELECT_INTERFACE_REALPARAMETER = "interface[" + INTERFACE_REALPARAMETER +"]";
	public static final String SELECT_INTERFACE_OBSCURECHARACTERISTIC = "interface[" + INTERFACE_OBSCURECHARACTERISTIC +"]";
	public static final String SELECT_INTERFACE_STRINGPARAMETER = "interface[" + INTERFACE_STRINGPARAMETER +"]";
	public static final String SELECT_INTERFACE_BOOLEANPARAMETER = "interface[" + INTERFACE_BOOLEANPARAMETER +"]";
	public static final String SELECT_INTERFACE_SUBJECTIVITYPARAMETER = "interface[" + INTERFACE_SUBJECTIVITYPARAMETER +"]";
	public static final String SELECT_INTERFACE_INTEGERPARAMETER = "interface[" + INTERFACE_INTEGERPARAMETER +"]";	
	
	//Interface Constants
	public static final String INTERFACE_LAYERREPRESENTATION = PropertyUtil.getSchemaProperty("interface_pgLayerRepresentationInterface");
	public static final String INTERFACE_LAYERPRODUCTINTERFACE = PropertyUtil.getSchemaProperty("interface_pgLayerRepresentationInterface");
	public static final String INTERFACE_DSMLAYERPRODUCTEBOMINTERFACE = PropertyUtil.getSchemaProperty("interface_pgDSMLayerProductEBOMInterface");
	public static final String INTERFACE_DSMLAYERPRODUCTEBOMSUBSTITUTEINTERFACE = PropertyUtil.getSchemaProperty("interface_pgDSMLayerProductEBOMSubtituteInterface");
	public static final String INTERFACE_CHARACTERISTICS = PropertyUtil.getSchemaProperty("interface_Characteristics");
	public static final String INTERFACE_SYNCHROEBOMEXT = PropertyUtil.getSchemaProperty("interface_SynchroEBOMExt");
	public static final String INTERFACE_PG_MATERIALCOVERING = PropertyUtil.getSchemaProperty("interface_pngiMaterialCovering");
	public static final String SELECT_INTERFACE_PG_MATERIALCOVERING = "interface["+INTERFACE_PG_MATERIALCOVERING+"]";
	
	public static final String INTERFACE_CATCOMPONENTSFAMILYEXTENSIONONELEMENT = PropertyUtil.getSchemaProperty("interface_CATComponentsFamilyExtensionOnElement");
	public static final String SELECT_INTERFACE_CATCOMPONENTSFAMILYEXTENSIONONELEMENT = "interface["+INTERFACE_CATCOMPONENTSFAMILYEXTENSIONONELEMENT+"]";
	public static final String INTERFACE_PNGICOMPONENTFAMILY = PropertyUtil.getSchemaProperty("interface_pngiComponentFamily");
	public static final String SELECT_INTERFACE_PNGICOMPONENTFAMILY = "interface["+INTERFACE_PNGICOMPONENTFAMILY+"]";
	public static final String COMMAND_PG_ENGLAUNCH3DPLAYCHANNEL = PropertyUtil.getSchemaProperty("command_pgENGLaunch3DPlayChannel");
	
	public static final String SELECT_ASSOCIATED_PHYSICAL_PRODUCT_ID = new StringBuilder().append(pgApolloConstants.CONSTANT_STRING_SELECT_FROM).append(RELATIONSHIP_PART_SPECIFICATION).append(pgApolloConstants.CONSTANT_STRING_SELECT_CLOSE_BRACKET).append(pgApolloConstants.CONSTANT_STRING_DOT).append(pgApolloConstants.CONSTANT_STRING_SELECT_TO).append(TYPE_VPMREFERENCE).append(pgApolloConstants.CONSTANT_STRING_SELECT_CLOSE_BRACKET).append(pgApolloConstants.CONSTANT_STRING_DOT).append(SELECT_ID).toString();
	public static final String SELECT_ASSOCIATED_PHYSICAL_PRODUCT_NAME = new StringBuilder().append(pgApolloConstants.CONSTANT_STRING_SELECT_FROM).append(RELATIONSHIP_PART_SPECIFICATION).append(pgApolloConstants.CONSTANT_STRING_SELECT_CLOSE_BRACKET).append(pgApolloConstants.CONSTANT_STRING_DOT).append(pgApolloConstants.CONSTANT_STRING_SELECT_TO).append(TYPE_VPMREFERENCE).append(pgApolloConstants.CONSTANT_STRING_SELECT_CLOSE_BRACKET).append(pgApolloConstants.CONSTANT_STRING_DOT).append(SELECT_NAME).toString();
	public static final String SELECT_ASSOCIATED_PHYSICAL_PRODUCT_REVISION = new StringBuilder().append(pgApolloConstants.CONSTANT_STRING_SELECT_FROM).append(RELATIONSHIP_PART_SPECIFICATION).append(pgApolloConstants.CONSTANT_STRING_SELECT_CLOSE_BRACKET).append(pgApolloConstants.CONSTANT_STRING_DOT).append(pgApolloConstants.CONSTANT_STRING_SELECT_TO).append(TYPE_VPMREFERENCE).append(pgApolloConstants.CONSTANT_STRING_SELECT_CLOSE_BRACKET).append(pgApolloConstants.CONSTANT_STRING_DOT).append(SELECT_REVISION).toString();
		
	public static final String SELECT_ASSOCIATEDPHYSICALPRODUCT_ISVPLMCONTROLLED = new StringBuilder().append(pgApolloConstants.CONSTANT_STRING_SELECT_FROM).append(RELATIONSHIP_PART_SPECIFICATION).append(pgApolloConstants.CONSTANT_STRING_SELECT_CLOSE_BRACKET).append(pgApolloConstants.CONSTANT_STRING_DOT).append(pgApolloConstants.CONSTANT_STRING_SELECT_TO).append(TYPE_VPMREFERENCE).append(pgApolloConstants.CONSTANT_STRING_SELECT_CLOSE_BRACKET).append(pgApolloConstants.CONSTANT_STRING_DOT).append(SELECT_ATTRIBUTE_ISVPLMCONTROLLED).toString();
	
	//Role Constants
	public static final String ROLE_PARTANDSPECIFICATIONADMIN = PropertyUtil.getSchemaProperty("role_PartandSpecificationAdmin");
	
	public static final String RANGE_PGAUTHORINGAPPLICATION_LPD = "LPD";
	public static final String RANGE_CHARACTERISTICTYPE= "Characteristic Type";
	public static final String RANGE_VALUE_VARIABLE = "VARIABLE";
	public static final String RANGE_VALUE_ATTRIBUTE = "ATTRIBUTE";
	public static final String RANGE_VALUE_REPORT = "REPORT";	
	public static final String RANGE_VALUE_SUMMARY = "SUMMARY";
	public static final String RANGE_VALUE_CHG_C = "C";	
	public static final String RANGE_VALUE_CHG_CPLUS = "C+";
	public static final String RANGE_PGLPDPERFCHARACTERISTICSUPDATESTATUS_NOTAPPLICABLE = "Not Applicable";
	public static final String RANGE_PGLPDPERFCHARACTERISTICSUPDATESTATUS_PENDING = "Pending";
	public static final String RANGE_PGLPDPERFCHARACTERISTICSUPDATESTATUS_INPROGRESS = "In-Progress";
	public static final String RANGE_PGLPDPERFCHARACTERISTICSUPDATESTATUS_COMPLETED = "Completed";
	public static final String RANGE_PGLPDPERFCHARACTERISTICSUPDATESTATUS_IGNORED = "Ignored";

	
	public static final String CHARACTERISTICS_DEFAULT_DIMENSION = "RealParameter";
	public static final String CHARACTERISTICS_DEFAULT_DISPLAYUNIT = "mm per 10";
	
	public static final String RANGE_VALUE_GENERIC = "Generic";
	public static final String RANGE_VALUE_BASE = "Base";
	public static final String RANGE_VALUE_VARIANT = "Variant";
	public static final String RANGE_VALUE_PG_ISDESIGNDRIVEN_YES="Yes";
	public static final String RANGE_VALUE_PG_ISDESIGNDRIVEN_NO="No";
	public static final String RANGE_CADDESIGN_ORIGINATION_AUTOMATION = "Automation";
	
	public static final String RANGE_DSO = "DSO";
	public static final String STR_TILDA_CHAR = "_TILDA_CHAR_";	
	public static final String STR_SPACE_CHAR = "_SPACE_CHAR_";	
	public static final String CONSTANT_STRING_TILDA = "~";
	public static final String CONSTANT_STRING_CARET = "^";
	public static final String CONSTANT_STRING_DOUBLE_ATSIGN = "@@"; //Apollo 18x.6 ALM-42391 Changes

	public static final String CONSTANT_STRING_COMMA = ",";
	public static final String CONSTANT_STRING_PIPE = "|";
	public static final String CONSTANT_STRING_SPACE_PIPE_SPACE = " | ";
	public static final String CONSTANT_STRING_COLON = ":";
	public static final String CONSTANT_STRING_HYPHEN  = "-";
	public static final String CONSTANT_STRING_UNDERSCORE  = "_";
	public static final String CONSTANT_STRING_DOT = ".";
	public static final String CONSTANT_STRING_AMPERSAND = "&";
	public static final String CONSTANT_STRING_SPACE = " ";
	public static final String CONSTANT_STRING_EQUAL_SIGN = "=";
	public static final String CONSTANT_STRING_FIVE = "5";
	public static final String CONSTANT_STRING_HASH_AT_HASH = "#@#";
	public static final String CONSTANT_STRING_OPEN_ROUND_BRACE = "(";
	public static final String CONSTANT_STRING_CLOSE_ROUND_BRACE = ")";
	public static final String CONSTANT_STRING_DOUBLE_EQUAL = "==";
	public static final String CONSTANT_STRING_NOT_EQUAL = "!=";
	public static final String CONSTANT_STRING_NEGATE = "!";
	public static final String CONSTANT_STRING_DOUBLE_AMPERSAND = "&&";
	public static final String CONSTANT_STRING_DOUBLE_PIPE = "||";
	public static final String CONSTANT_STRING_SINGLE_QUOTE = "'";
	public static final String CONSTANT_STRING_NEWLINE = "\n";
	public static final String CONSTANT_STRING_HASH = "#";
	public static final String CONSTANT_STRING_THREE_HASH = "###";
	public static final String CONSTANT_STRING_THREE_COLON = ":::";
	
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
	public static final String CONSTANT_STRING_SELECT_FROMTITLE = "].from."+DomainConstants.SELECT_ATTRIBUTE_TITLE;
	
	public static final String TYPE_PG_INTENDEDMARKETS = PropertyUtil.getSchemaProperty("type_Country");
	public static final String CONSTANT_STRING_NOT_EQUAL_SIGN="!=";
	
	public static final String CONST_ENV_EVENT = "EVENT";
	public static final String CONST_MACRO_OBJECTID = "OBJECTID";
	public static final String CONST_MACRO_TOOBJECTID = "TOOBJECTID";
	public static final String CONST_MACRO_FROMOBJECTID = "FROMOBJECTID";
	public static final String CONST_MACRO_TYPE = "TYPE";
	public static final String CONST_EVN_EVENT_DISCONNECT = "Disconnect";
	public static final String CONST_EVN_EVENT_DELETE = "Delete";
	public static final String CONST_ENV_RELTYPE = "RELTYPE";
	public static final String CONST_ENV_VPLM_DELETE_VPMREF_FROMENOVIA = "VPLMInteg-DeleteVPMRef-FromEnovia-";
	public static final String CONST_KEY_ENVDELETION = "EnvKeyDeletion";
	public static final String CONST_KEY_SKIP_DELETION = "SkipDeletion";
	public static final String CONST_SEMANTICROLE_CFY_GENERICMODELREFERENCE = "CFY_GenericModelReference";
	public static final String CONST_SEMANTICROLE_CFY_ITEMREFERENCE = "CFY_ItemReference";
	public static final String CONST_SEMANTICROLE_DESIGNTABLESOURCEFILEREFERENCE4 = "DesignTableSourceFileReference4";
	public static final String CONST_NONE_BRANCH = "none branch";
	public static final String CONST_KEY_NEW = "NEW";
	public static final String PREF_DEFAULT_EXPLORATION="preference_DefaultIPClassForExploration";
	public static final String PREF_DESIGN_FOR="preference_DIDesignFor";
	public static final String PREF_HIR="preference_HighlyRestrictedClass";
	
	public static final String FORMAT_DECIMAL_NINE = "0.0########";
	public static final String FORMAT_DECIMAL_SIX = "0.0#####";
	public static final String FORMAT_DECIMAL_FOUR = "0.0###";
	public static final String FORMAT_DECIMAL_THREE = "0.0##";
	public static final String NUMBER_DECIMAL_NINE = "9";
	public static final String NUMBER_DECIMAL_SIX = "6";
	public static final String NUMBER_DECIMAL_FOUR = "4";
	public static final String NUMBER_DECIMAL_THREE = "3";
	
	public static final String STR_COMPARE_REPORT_TYPE_DESIGNPARAMETER = "DESIGNPARAMETER";
	public static final String STR_COMPARE_REPORT_TYPE_SUBSTITUTE = "SUBSTITUTE";
	public static final String STR_COMPARE_REPORT_TYPE_CHARACTERISTIC = "CHARACTERISTIC";
	public static final String STR_COMPARE_REPORT_TYPE_ATTRIBUTE = "ATTRIBUTE";
	public static final String STR_COMPARE_REPORT_TYPE_PLANT = "PLANT";
	public static final String STR_COMPARE_REPORT_TYPE_EBOM = "EBOM";
	
	public static final String STR_BASE = "Base";
	public static final String STR_CATEGORY = "Category";
	public static final String STR_CHARACTERISTICS = "Characteristics";
	public static final String STR_CHARACTERISTICSPECIFICS = "CharacteristicSpecifics";
	public static final String STR_PLANTNAME = "PlantName";
	public static final String STR_ATTRIBUTE = "Attribute";
	public static final String STR_LAYERNAME = "LayerName";
	public static final String STR_SUBLAYERNAME = "SubLayerName";
	public static final String STR_GROUPNAME = "GroupName";
	public static final String STR_PARAMETERLAYERDNAME = "ParameterLayerName";
	public static final String STR_PARAMETERGROUP = "ParameterGroup";
	public static final String STR_PARAMETERNAME = "ParameterName";
	public static final String STR_BLANKSPECIFIC = "BlankSpecific";
	public static final String STR_GPSUOM_MG = "mg";
	public static final String STR_GPSUOM_G = "g";
	public static final String STR_DESIGN_PARAMETER_RMP = "RMP";
	public static final String STR_DESIGN_PARAMETER_MATERIAL_FUNCTION = "MaterialFunction";
	public static final String STR_LPDGETAPPSERVICE_LOGFILE = "pgLPDGetAPPService.log";
	public static final String STR_LPDUPDATE_LOGFILE = "pgLPDUpdateVPMReferenceService.log";
	public static final String STR_LPDGETCHARACTERISTICDETAILS_LOGFILE = "pgLPDGetCharacteristicsDetailsService.log";
	public static final String STR_SYNC_LOGFILE = "pgLPDMassCollaborationService";
	public static final String STR_READXML_LOGFILE = "pgLPDReadXMLService.log";
	public static final String STR_COREMTRL_LOGFILE = "pgLPDCoreMaterialService";
	public static final String STR_VALIDATEGENERICMODEL_LOGFILE = "pgLPDValidateGenericModelService";
	public static final String STR_UPDATEVARIANTAPP_LOGFILE = "pgLPDUpdateVariantAPPService";
	public static final String STR_LPDUPDATEWIPMODEL_LOGFILE = "pgLPDUpdateModelWithGenericModelService";
	public static final String STR_INTENDEDMARKETS_LOGFILE = "pgLPDReadUpdateIntendedMarketsAPPService";
	public static final String STR_GECOMPONENTFAMILY_LOGFILE = "pgLPDGetComponentFamilyDetailsService";

	public static final String STR_LOGFILE_EXTENSION = ".log";
	public static final String STR_XMLFILE_EXTENSION = ".xml";
	public static final String STR_XLSXFILE_EXTENSION = ".xlsx";
	public static final String CONSTANT_STRING_TILD = "~";
	public static final String CONSTANT_STRING_XMLPATH_SEPARATOR = "/";

	public static final String STR_READ_MODE_GETCONFIGURATION= "getConfigurations";
	public static final String STR_READ_MODE_ALLPARAMETERDETAILS="getAllParameterDetails";
	public static final String STR_READ_MODE_GETPARAMETERVALUES="getParameterValues";
	public static final String STR_READ_MODE_GETVARIANTPARTS="getVariantParts";
	public static final String STR_READ_MODE_GETDELTAPARAMSFORVARIANTS="getDeltaParamsForVariants";
	public static final String STR_READ_MODE_GETPERFORMANCECHARVALUES="getPerfCharacValues";
	public static final String STR_READ_MODE_GETALLPERFCHARACDETAILS="getAllPerfCharacDetails";
	public static final String STR_READ_MODE_GETDELTAPERFCHARFORVARIANTS="getDeltaPerfCharForVariants";
	public static final String STR_READ_MODE_GETALLBASES="getAllBases";	
	public static final String STR_READ_MODE_GETINWORKBASEPARTS="getInWorkBaseParts";
	public static final String STR_MODE_GETBOOKMARK = "GETBOOKMARK";	
	
	public static final String STR_MODE_GETRESOLVEDITEMS="getResolvedItems";	
	public static final String STR_MODE_GETGENERICITEMS="getGenericItems";	
	public static final String STR_MODE_GETCOMPONENTFAMILY_GENERICITEM="getComponentFamilyFromGenericItem";	
	public static final String STR_MODE_GETCOMPONENTFAMILY_RESOLVEDITEM="getComponentFamilyFromResolvedItem";	
	public static final String STR_MODE_PREVIEW="Preview";	
	public static final String STR_MODE_SYNC="SYNC";
	public static final String STR_MODE_SHOW_PREVIEW="ShowPreview";	
	public static final String STR_MODE_UPDATE_CHAR="UpdateCharacteristics";	

	public static final String STR_MODE_PREVIOUSCOLLABORATION="PreviousCollaboration";	
	public static final String STR_MODE_MASSREVISE="MassRevise";	

	public static final String STR_HIGHLY_RESTRICTED="Highly Restricted";

	public static final String STR_TITLE_SEND_NOTIFICATION="Send Notification to Product Owner";

	public static final String STR_CHARACTERISTICSUPDATESTATUS="CharacteristicsUpdateStatus";
	public static final String STR_MODE_MASSUPDATE_UPDATE="MassUpdateCharacteristics";
	public static final String STR_MODE_MASSUPDATE_IGNORED="MassUpdateMarkIgnored";
	public static final String STR_MODE_MASSUPDATE_PENDING="MassUpdateMarkPending";
	public static final String STR_MASSUPDATECHARACTERISTICSSEARCHCRITERIA="MassUpdateCharacteristicsSearchCriteria";
	public static final String STR_TITLE_MASSUPDATECHARACTERISTICS="Mass Update Characteristics";
	public static final String STR_TITLE_CRITERIAUPDATE_SENDNOTIFICATION="Criteria Impacted Parts";
	public static final String STR_MODE_BACKGROUNDPROCESS_RESUBMIT="ReSubmitBackgroundProcess";
	public static final String STR_MODE_BACKGROUNDPROCESS_CANCEL="CancelBackgroundProcess";
	public static final String STR_MODE_BACKGROUNDPROCESS_REMOVE="RemoveAssociatedObjects";

	//String Constants	
	public static final String STR_ERROR_MISSING_QTY ="Quantity is missing for Layer <LAYERNAME>.";
	public static final String STR_ERROR_MISSING_NET_WEIGHT ="Net Weight is missing for Layer <LAYERNAME>. EBOM generation cannot be processed.";
	public static final String STR_ERROR_MISSING_GROSS_WEIGHT ="Gross Weight is missing for Layer <LAYERNAME>. EBOM generation cannot be processed.";
	public static final String STR_ERROR_MISSING_LAMINATE_NAME ="LaminateName is missing for Laminate Layer <LAYERNAME>. EBOM generation cannot be processed.";
	public static final String STR_ERROR_MISSING_REPORT_TYPE ="Sync failed because ReportType parameter does not exist or Invalid character for Characteristic <PERFCHAR>";
	public static final String STR_ERROR_MISSING_UNIT ="<PARAMETER> Unit is missing for Layer <LAYERNAME>.";
	public static final String STR_ERROR_MISSING_PARAMETER ="<PARAMETER> is missing for Layer <LAYERNAME>.";
	public static final String STR_ERROR_QUANTITY_CALCULATION="Quantity calculation is failed for Layer <LAYERNAME>.";
	public static final String STR_CONVERSION_FAILED ="Missing <PARAMETER> Unit Conversion Mapping or <PARAMETER> Conversion is failed for Layer <LAYERNAME>.";
	public static final String STR_ERROR_LAMINATE_MISMATCH_UNITS ="Same Unit of Measure is not used in Laminated Layers <LAYERNAME> for <PARAMETER>";
	public static final String STR_ERROR_INVALID_CHARACTERS_PARAMETER ="Sync failed because of invalid characters in parameters";
	public static final String STR_ERROR_DUPLICATE_APPLICATOR_RMPS ="Duplicate Applicator RMPs";
	public static final String STR_ERROR_SAME_MASTER_APPLICATORS ="Duplicate Applicator Parts";
	public static final String STR_ERROR_APPLICATORS_NOTRELEASED ="Applicator Parts are not released";
	public static final String STR_ERROR_MASTERAPPPLICATOR_INVALID ="Invalid or missing Applicator Parts";
	public static final String STR_ERROR_MISSING_INVALID_WEB_WIDTH ="WebWidth has invalid value for Layer <LAYERNAME>. EBOM generation cannot be processed.";

	public static final String STR_ERROR_EBOMNOTPROCESSED = "EBOM generation cannot be processed.";
	public static final String STR_ERROR_VPMREFERENCEREVISIONNOTFOUND = "Revision information not received from Catia for VPMReference <VPMREF_NAME>";
	public static final String STR_ERROR_MATERIALREVISIONNOTFOUND = "Revision information not received from Catia for Material <MATERIAL_NAME>";
	public static final String STR_ERROR_VPMREFERENCESTATE = "VPMReference <VPMREF_NAME> is either in Released or Obsolete state";
	public static final String STR_ERROR_APPNOTEXIST = "VPMReference <VPMREF_NAME> <VPMREF_REV> does not have an associated Assembled Product Part";
	public static final String STR_ERROR_MULTIAPPEXIST = "VPMReference <VPMREF_NAME> <VPMREF_REV> is connected to more than one Assembled Product Part, Please review data again.";
	public static final String STR_ERROR_APPSTATECHECK = "Assembled Product Part <APP_NAME> is beyond Preliminary state";
	public static final String STR_ERROR_PREVIOUSREVAPPSTATECHECK = "Previous revision of Assembled Product Part <APP_NAME> is not in Release state";
	public static final String STR_ERROR_MANDATORYMODELUPDATESTATUS = "Model <VPMREF_NAME> <VPMREF_REV> is not up to date with latest Mandatory Generic Model. Please execute a Model Update";
	public static final String STR_ERROR_DATANOTRECEIVED = "Catia Material and Raw Material details not received in Enovia";
	public static final String STR_ERROR_NORAWMATERIAL = "Catia Material <MATERIAL_NAME> does not have an associated Raw Material";
	public static final String STR_ERROR_DATAPARSEERROR = "Error in processing data for VPMReference <VPMREF_NAME> <VPMREF_REV>";
	public static final String STR_ERROR_CHANGEOBJECTERROR = "Error in Change Object creation and connection to Enterprise Parts";
	public static final String STR_ERROR_CHARACTERISTICSERROR = "Error in creating/updating Characteristics";
	public static final String STR_ERROR_PUBLISH_DESIGN_PARAM = "Error occured while publishing design parameters : ";
	public static final String STR_ERROR_ADDToBOOKMARK = "Error occured while adding Parts to Bookmark : ";

	public static final String STR_ERROR_FREEZE_DESIGN_TRANSFER_CONTROL= "Error while Freeze Design and Transfer Control to ENOVIA, Please check APP History for more details";
	public static final String STR_ERROR_TRANSFER_CONTROL_WITHOUT_FREEZE_DESIGN = "Error while Transfer Control : Freeze Design option needs to selected in order to successfully transfer control to EBOM.";
	public static final String STR_EBOM_SUCCESSMESSAGE = "EBOM and Performance Characteristics have been successfully generated.";
	public static final String STR_BACKGROUND_JOB_NAME = "BackgroundJobName";
	public static final String STR_SUCCESS = "Success";
	public static final String STR_STATUS_JOB_INITIATED = "Background Job Initiated";
	public static final String STR_NO_DATA_FOR_PROCESSING = "No data sent for collaboration";
	public static final String STR_ERROR = "Error";
	public static final String STR_ERROR_VPMREFERENCENOTEXIST = "VPMReference <VPMREF_NAME> <VPMREF_REV> does not exist. Please save data and try again";
	public static final String STR_REVISE_SUCCESSMESSAGE = "Base VPM Reference Product has been successfully revised and connected to 3DShape";
	public static final String STR_REVISE_ERRORMESSAGE = "Base VPM Reference Product or New Product does not exist in Enovia";
	public static final String STR_EXTRACT_SUCCESSMESSAGE = "true";
	public static final String STR_EXTRACT_ERRORMESSAGE = "false";
	public static final String STR_EXTRACT_OBJECTNOTFOUND = " Object does not exist in Enovia";
	public static final String STR_ERROR_NORAWMATERIAFOUND = "Raw Material <MATERIAL_NAME> does not exist in Enovia database.";
	public static final String STR_ERROR_NOACCESSRAWMATERIA = "User does not have show access on Raw Material <MATERIAL_NAME>.";
	public static final String STR_ERROR_NORAWMATERIAFOUND_RELEASE_PRILIMINARY = "Latest Released Revisions NOT found for Raw Material <MATERIAL_NAME>.";
	public static final String STR_ERROR_RAWMATERIAOBSOLETE = "Raw Material <MATERIAL_NAME> is in Obsolete state";
	public static final String STR_ERROR_RAWMATERIAL_NOREADACCESS = "No Read access on <MATERIAL_NAME>.";
	public static final String STR_ERROR_RAWMATERIAL_RESTRICTED = "<MATERIAL_NAME> is no longer Fit For Use. Please update Material Characteristic RMP Name with a new value."; 
	public static final String STR_ERROR_RAWMATERIAL_RESTRICTED_EMPTY_WARNING = "<MATERIAL_NAME> cannot be used because of missing Relationship Restriction Comment. Please contact material owner to update this attribute or update Material Characteristic RMP Name with a new RMP value in the model.";
	public static final String STR_ERROR_RAWMATERIAL_RELEASEPHASENOTVALID = "Raw Material <MATERIAL_NAME> with Release Phase <RELEASE_PHASE> cannot be used in the structure.";
	public static final String STR_ERROR_RAWMATERIAL_EXPIRED = "<MATERIAL_NAME> has expired. Please update Material Characteristic RMP.";
	public static final String STR_ERROR_COREMATERIAL_NOTCONNECTEDTORMP = "Core Material <CORE_MATERIAL> is not connected to Raw Material <MATERIAL_NAME>.";
	public static final String STR_ERROR_COREMATERIAL_NOTASSOCIATEDWITHRMP = "Core Material <CORE_MATERIAL> is not assigned to Raw Material <MATERIAL_NAME>.";
	public static final String STR_ERROR_COREMATERIAL_NOTLATESTREV = "Core Material <CORE_MATERIAL> is not Latest revision.";
	public static final String STR_ERROR_COREMATERIAL_NOTRELEASEDREV = "Core Material <CORE_MATERIAL> is not in released state.";
	public static final String STR_ERROR_COREMATERIAL_NOTVALIDFORLAMINATEFUNCTION = "Core Material <CORE_MATERIAL> is not created for Laminate Function <LAMINATE_FUNCTION>.";
	public static final String STR_ERROR_COREMATERIAL_NOTFOUND = "Core Material <CORE_MATERIAL> does not exist in Enovia database.";
	public static final String STR_ERROR_COREMATERIAL_MULTIPLE_OBJECT = "More than one Core Material Found. Please correct data.";	
	public static final String STR_ERROR_MATERIALFUNCTION_NOTFOUND  = "Material Function <MATERIAL_FUNCTION> is not valid for Raw Material <MATERIAL_NAME>.";
	public static final String STR_ERROR_COREMATERIAL_NOTLATESTREV_FOR_LAYER = "Core Material <CORE_MATERIAL> specified for layer <LAYER_NAME> is not Latest revision.";
	public static final String STR_ERROR_COREMATERIAL_NOTRELEASEDREV_FOR_LAYER = "Core Material <CORE_MATERIAL> specified for layer <LAYER_NAME> is not in released state.";
	public static final String STR_ERROR_COREMATERIAL_NOTFOUND_FOR_LAYER = "Core Material <CORE_MATERIAL> specified for layer <LAYER_NAME> does not exist in Enovia database.";
	public static final String STR_ERROR_COREMATERIAL_NOTVALIDFORLAMINATEFUNCTION_FOR_LAYER = "Core Material <CORE_MATERIAL> specified for layer <LAYER_NAME> is not created for Laminate Function <LAMINATE_FUNCTION>.";
	public static final String STR_ERROR_DUPLICATE_LAYERS = "Duplicate Group and Layer Name <GROUP_LAYER> found for EBOM Update.";
	public static final String STR_ERROR_READING_DESIGNPARAMS = "Error occured while reading Design Parameters: <ERROR>.";
	public static final String STR_ERROR_MISSING_BOOKMARK = "Valid Bookmark object not selected for Add To Bookmark.";
	public static final String STR_ERROR_READING_READREFINEQUERY = "Error occured while read and refine Model Query: <ERROR>";
	public static final String STR_ERROR_REFINE_EBOM = "Error occured while refining EBOM Parameters: <ERROR>";
	public static final String STR_ERROR_REFINE_CHARACTERISTICS = "Error occured while refining Characteristics Parameters: <ERROR>";
	public static final String STR_ERROR_LAMINATE_PROCESSING ="Error Occurred in Lamination Processing : ";
	public static final String STR_ERROR_PERFCHARACTERISTIC_NOTPRESENT="Characteristic does not exist in model";
	public static final String STR_APPLICATORPART_NOTRELEASED="Applicator Part is not in Released State";
	public static final String STR_APPLICATORPART_NOTASSOCIATED= "Applicator Part is not found for the given RMP";
	public static final String STR_APPLICATORPART_NOPHYSICALPRODUCT="Applicator Part does not have Physical Product";
	public static final String STR_APPLICATORPART_NOTVALIDTYPE="Applicator Part is not valid type";

	public static final String STR_ERROR_NOAPP = "No APP found for VPMReferece ";
	public static final String STR_ERROR_MOREAPP ="More than one APP found for VPMReferece ";
	public static final String STR_ERROR_NOOBJ = "No Object Found for VPMReference ";
	public static final String STR_ASSEMBLED_PRODUCT_PART = "Assembled Product Part";
	public static final String STR_MASTER_PRODUCT_PART = "Master Product Part";
	public static final String STR_MASTER_RAW_MATERIAL_PART = "Master Raw Material Part";
	public static final String STR_ASSEMBLED_PRODUCT = "Assembled Product";
	public static final String STR_ERROR_NOGENERICOBJECTFOUND = "No Generic Model Found for Selected Configuration";

	public static final String STR_ERROR_NO3DSHAPEDRAWINGATTACHED = "No 3DShape or Drawing attached to Model to copy to Target";
	public static final String STR_ERROR_VPMREF_IS_INWORK = "Given VPMReference is not in IN_WORK state";
	public static final String STR_UPDATEMODEL_OBJECTNOTPRESENT = "Either of the VPMReference does not exist in Enovia";
	public static final String STR_ERROR_NOINTENDEDMARKETS = "Intended Markets either not Active or not found";
	public static final String STR_ERROR_FILE_CHECKOUT = "Error while checking out Design Parameter File: ";
	public static final String STR_ERROR_FILE_CHECKIN = "Error while checking in modified File: ";
	public static final String STR_SUCCESS_FILE_CHECKIN = "Design Parameters file is successfully modified.";
	
	public static final String STR_OBJECTNOTFOUND = "Corresponding Assembly Product Part Object does not exist in Enovia";
	public static final String STR_NULL_STRING = "null";
	public static final String STR_YES_FLAG = "YES";
	public static final String STR_NO_FLAG = "NO";
	public static final String STR_TRUE_FLAG = "true";
	public static final String STR_FALSE_FLAG = "false";
	public static final String STR_TRUE_FLAG_CAPS = "TRUE";
	public static final String STR_FALSE_FLAG_CAPS = "FALSE";
	public static final String STR_PRODUCT_TECHNOLOGY = "Product Technology Chassis";
	public static final String STR_PRODUCT_TECHNOLOGY_CHASSIS = "Product Technology Chassis";
	public static final String STR_PRODUCT_TECHNOLOGY_PLATFORM = "Product Technology Platform";
	public static final String STR_PRODUCT_CATEGORY_PLATFORM = "Product Category Platform";
	public static final String STR_BUSINESS_AREA = "Business Area";
	public static final String STR_VARIANT_TYPE = "Variant Type";
	public static final String STR_FRANCHISE_PLATFORM = "Franchise Platform";
	public static final String STR_NO_MF_ERROR  = "Material Function <MATERIAL_FUNCTION> not present in Enovia";
	public static final String STR_NO_MF_CONNECTED_ERROR  = "Material Function <MATERIAL_FUNCTION> not valid for Raw Material <RAW MATERIAL>";
	public static final String STR_ERROR_EBOM_ATTRIBUTE_Update  = "Error ocurred while updating EBOM Attributes";
	public static final String STR_ERROR_NO_CHARACTERISTICS = "One or more Characteristic Master not found.";
	public static final String STR_ERROR_NO_CRITERIA = "No Characteristic present under APP. Please check if valid Criteria exists.";
	public static final String STR_ERROR_EXPRESSION_NOT_SATISFIED = "Limits or Targets on following characteristic do not satisfy the expression - LSL <=  LRRL < LT < Target < UT < URRL <= USL - " ;
	public static final String STR_ERROR_INITIATE_BACKGROUND_JOB = "Error occured while initiating background job for Collaborate :";
	public static final String STR_ERROR_BACKGROUND_JOB_ISRUNNING = "Operation failed. Following Background Jobs for previous Collaborate operation are still running: ";
	public static final String STR_ERROR_BACKGROUND_PROCESS_ISRUNNING = "Operation failed. Following Background Process for Part are still running: ";
	public static final String STR_ERROR_CHARACTERISTICSUPDATESTATUS_ISRUNNING = "Characteristics Update is running in Background Process, Please try after some time";
	public static final String STR_ERROR_EVALUATECRITERIABACKGROUND_JOB_ISRUNNING = "Operation failed. Following Background Job for Characteristics Update is still running: ";
	public static final String STR_ERROR_REPORT_GENERATION_NOTIFICATION = "Error ocurred while generating report and sending it Background Process Owners";
	public static final String STR_ERROR_REPORT_TONEAREST = "Report To Nearest is not a Positive Number in Characteristics - " ;
	public static final String STR_ERROR_POSTPROCESS_PREVIOUSREVISION = "Error ocurred while post processing previous revision";
	public static final String STR_ERROR_POSTPROCESS_UPDATEDREVISION = "Error ocurred while post processing updated revision";
	public static final String STR_ERROR_CHANGEACTION_CREATION = "Error ocurred while creating Change Action";
	public static final String STR_ERROR_DRAWING_UPDATE = "Error in updating Drawing";
	public static final String STR_CANCELLED = "Cancelled";
	
	public static final String STR_SUCCESS_EBOM = "EBOM Update successfully completed";
	public static final String STR_SUCCESS_CHARACTERISTIC = "Characteristic Update successfully completed";
	public static final String STR_SUCCESS_PUBLISH_DESIGN_PARAMETERS = "Publish Design Parameters successfully completed";
	public static final String STR_SUCCESS_DRAWING_UPDATE = "Drawing Update successfully completed";
	public static final String STR_SUCCESS_ADD_TO_BOOKMARK = "Enterprise Part is successfully added to selected Bookmark.";
	public static final String STR_SUCCESS_DESIGN_FREEZE = "Freeze Design is successfully completed." ;
	public static final String STR_SUCCESS_DESIGN_FREEZE_TRANSFER_CONTROL = "Design is frozen and Control is transferred to Enovia" ;
	public static final String STR_SUCCESS_BACKGROUND_JOB_INITIATED = "Operation will be processed in background using Background Job <JOB_NAME>. Email notification will be sent once processing is completed." ;
	public static final String STR_WARNING_SUBSTITUTE_CALCULATION = "Auto-calculation of Quantity/Gross Weight/Net Weight failed for one or more EBOM Substitutes. Please verify values again by navigating to 'Substitute Part In' page in Enovia.";
	
	public static final String FILE_NAME_CATIA_QUERYSTRING = "pgCatiaModelInfo.txt";
	public static final String STR_PG_MSPR_CONFIG = "pgMSPRConfig";	
	public static final String STR_MSPR_SEGMENT_CHECKED = "BySegmentOptionChecked";
	public static final String STR_MSPR_BA_CHECKED = "ByBAOptionChecked";
	public static final String STR_MSPR_SEGMENT_DISABLED = "BySegmentOptionDisabled";
	public static final String STR_MSPR_BA_DISABLED = "ByBAOptionDisabled";	
	
	public static final String ERROR_MISSING_CHARACTERISTIC = "ERROR: Characteristic Name is not provided to locate Target value.";
	public static final String ERROR_MISSING_CHARACTERISTIC_SPECIFICS = "ERROR: Characteristic Specifics Name is not found to locate Target value.";
	public static final String ERROR_MISSING_VALID_UOM = "ERROR: Valid Unit of Measure is not present on Characteristic.";
	public static final String ERROR_MULTIPLE_TARGETS_FOUND = "ERROR: Multiple Targets found on RMP";

	public static final String KEY_READINESS_COMMENT = "ReadinessComment";
	public static final String KEY_READINESS_REASON = "ReadinessReason"; //Added for A10-902
	public static final String STR_READY = "Ready";
	public static final String STR_NOT_READY = "Not Ready";
	public static final String KEY_RM_GPS_AP = "RM GPS AP";
	public static final String KEY_RM_GPS_NA = "RM GPS NA";
	public static final String KEY_RM_GPS_EU = "RM GPS EU";
	public static final String KEY_RM_GPS_LA = "RM GPS LA";
	public static final String KEY_DIFFERENCE = "iDifference";
	public static final String KEY_DIFFERENCE_PERCENTAGE = "iDifferencePercentage";
	public static final String KEY_SUP_MTRL_TRADE_NAME = "mepTradeName";
	public static final String KEY_MAX_CLEARED_AMOUNT = "maxClearedAmount";
	public static final String KEY_MAX_CLEARED_AMOUNT_UNIT = "maxClearedAmountUnit";
	public static final String KEY_MEP_PLM_STATE = "mepPlmState";
	public static final String KEY_LINKED_SRI_PART_STATUS = "sriPartStatus";
	public static final String KEY_SRI_RELEASED = "SRI-Released";	
	public static final String KEY_REQUEST_NUMBER = "requestNumber";
	public static final String KEY_COMMENTS = "comments";
	public static final String KEY_FUNCTION_MATCH = "FunctionMatch";
	public static final String KEY_SEGMENT_MATCH = "SegmentMatch";
	public static final String KEY_MATERIAL_FUNCTION = "MaterialFunction";
	public static final String KEY_APP_SEGMENT = "APPSegment";
	public static final String KEY_ASSESSED_PCP = "assessedProductCategoryPlatforms";
	public static final String KEY_ASSESSED_BA = "assessedBusinessArea";
	public static final String KEY_ASSESSED_FUNCTION = "assessedFunctions";
	public static final String KEY_UNIQUEKEY = "uniqueKey";
	public static final String KEY_PREVIOUSUNIQUEKEY = "previousUniqueKey";
	public static final String KEY_ISACTIVE = "IsActive";
	public static final String KEY_PLYMATERIAL = "PlyMaterial";
	public static final String KEY_STACKING = "Stacking";
	public static final String KEY_EBOMPLYGROUPLIST = "EBOMPlyGroupList";
	public static final String KEY_PERFCHAR = "PerfChar";
	public static final String KEY_CHARPLYGROUPLIST = "CharPlyGroupList";
	public static final String KEY_INACTIVEDESIGNSPECIFICS = "InActiveDesignSpecifics";
	public static final String KEY_PRODUCT = "Prod";
	public static final String KEY_UNITOFMEASURE = "UnitOfMeasure";
	public static final String KEY_APPLICATORS = "Applicators";
	public static final String KEY_APPLICATOR = "Applicator";
	public static final String KEY_APPLICATOR_RMP = "ApplicatorRMPs";
	public static final String KEY_UOM = "UOM";
	public static final String KEY_AREA = "Area";
	public static final String KEY_GROSS_AREA = "GrossArea";
	public static final String KEY_MASS = "Mass";
	public static final String KEY_PLYGROUPNAME = "PlyGroupName";
	public static final String KEY_UNIQUEPLYGROUPLIST = "UniquePlyGroupList";
	public static final String KEY_TRANSFERCONTROL = "TransferControl";
	public static final String KEY_REV = "Rev";
	public static final String KEY_FREEZEDESIGN = "FreezeDesign";
	public static final String KEY_RETAINSUBSTITUTE = "RetainSubstitute";
	public static final String KEY_DESIGNPARAM = "DesignParam";
	public static final String KEY_NAME = "Name";
	public static final String KEY_EBOMATTRIBUTELIST = "EBOMAttributeList";
	public static final String KEY_CHARATTRIBUTEMAPPING = "CharAttributeMapping";
	public static final String KEY_COREMATERIAL = "CoreMaterial";
	public static final String KEY_LAMINATENAME = "LaminateName";
	public static final String KEY_LAMINATEFUNCTION = "LaminateFunction";
	public static final String KEY_APPLICATION = "Application";
	public static final String KEY_APPLICATION_PHYSICALID = "Application_PhysicalId";
	public static final String KEY_ISLAMINATE = "IsLaminate";
	public static final String KEY_GROSSWEIGHT = "GrossWeight";
	public static final String KEY_NETWEIGHT = "NetWeight";
	public static final String KEY_GROSS_VOLUME = "GrossVolume";
	public static final String KEY_GROSS_LENGTH = "GrossLength";
	public static final String KEY_WEBWIDTH = "WebWidth";
	public static final String KEY_WIDTHRATIO = "WidthRatio";
	public static final String KEY_VOLUME = "Volume";
	public static final String KEY_HIDDEN = "hidden";
	public static final String KEY_GROUP = "Group";
	public static final String KEY_LAYER = "Layer";
	public static final String KEY_PARAMETERSET = "ParameterSet";
	public static final String KEY_DESIGN_PARAMETER = "Design Parameter";
	public static final String KEY_DESIGN_PARAMETER_VALUES = "Design Parameter Values";
	public static final String KEY_RMPNAME = "RMPName";
	public static final String KEY_CHG = "Chg";
	public static final String KEY_BACKGROUNDEXECUTION = "BackgroundExecution";
	public static final String KEY_ADDTOBOOKMARK = "AddToBookmark";
	public static final String KEY_PUBLISHDESIGNPARAM = "PublishDesignParam";
	public static final String KEY_UPDATEDRAWINGSELECTION = "UpdateDrawingSelection";
	public static final String KEY_UPDATEDRAWINGSTATUS = "UpdateDrawingStatus";
	public static final String KEY_UPDATEDRAWINGSTATUS_BOOLEAN_FLAG = "UpdateDrawingStatusFlag";
	public static final String KEY_BOOKMARKID = "BookMarkId";
	public static final String KEY_NET_AREA = "NetArea";
	public static final String KEY_NET_VOLUME = "NetVolume";
	public static final String KEY_NET_LENGTH = "NetLength";
	//public static final String KEY_PART_KEY = "partKey";
	public static final String KEY_MAT_NAME = "materialName";
	public static final String KEY_BASE_UNITOFMEASURE ="BaseUnitOfMeasure";
	public static final String KEY_REPORT_TYPE = "ReportType";
	public static final String KEY_PREVIOUSOBJECTID = "PreviousObjectId";
	public static final String KEY_EBOMSUBSTITUTE_CHGATTRIBUTELIST = "EBOMSubstituteChgAttributeList";
	public static final String KEY_EBOMSUBSTITUTE_OBJECTLIST = "EBOMSubstituteObjectList";
	public static final String KEY_EBOMSUBSTITUTE_RELIDLIST = "EBOMSubstituteRelIdList";
	public static final String KEY_EBOMCHGATTRIBUTELIST = "EBOMChgAttributeList";
	public static final String KEY_PHYSICALPRODUCT = "PhysicalProduct";
	public static final String KEY_BACKGROUNDPROCESSNAME = "BackgroundProcessName";
	public static final String KEY_BACKGROUNDPROCESSSTATE = "BackgroundProcessState";
	public static final String KEY_ACCESS = "access";
	public static final String KEY_USERNAME = "username";
	public static final String KEY_COOWNER = "Co-Owner";
	public static final String KEY_MATERIALRESTRICTION = "MaterialRestriction";
	public static final String KEY_MATERIALRESTRICTION_COMMENT = "MaterialRestrictionComment";
	public static final String KEY_ACTIVATED_PLANTS = "ActivatedPlants";
	public static final String KEY_NONACTIVATED_PLANTS = "NonActivatedPlants";
	public static final String KEY_ISAPPLICATOR = "IsApplicator";
	public static final String KEY_RMPAPPLICATOR = "RMPApplicator";
	
	public static final String KEY_TMRD = "TMRD";
	public static final String KEY_TEST_METHOD = "TestMethod";
	public static final String KEY_APPLICABLE_CHARS = "ApplicableCharacteristics";
	public static final String KEY_MESSAGE = "Message";
	public static final String KEY_MISSING_PARAMETER_SET = "MissingParameterSet";
	public static final String KEY_MISSING_CHARS = "MissingCharacteristics";
	public static final String KEY_INACTIVE_CHARS = "InactiveCharacteristics";
	public static final String KEY_ACTIVE_CHARS = "ActiveCharacteristics";
	public static final String KEY_CMID = "CMId";
	public static final String KEY_CMTITLE = "CMTitle";
	public static final String KEY_CRITERIA_ID = "CriteriaId";
	public static final String KEY_TEST_METHOD_ID = "TestMethodId";
	public static final String KEY_TMRD_ID = "TMRDId";
	public static final String KEY_LATEST = "Latest";
	public static final String KEY_CRITERIA_NAME = "CriteriaName";
	public static final String KEY_CRITERIA_REVISION = "CriteriaRevision";
	public static final String KEY_CRITERIA_CURRENT = "CriteriaCurrent";
	public static final String KEY_TEST_METHOD_NAME = "TestMethodName";
	public static final String KEY_TMRD_NAME = "TMRDName";
	public static final String KEY_CATEGORY_SPECIFIC = "CategorySpecific";
	public static final String KEY_MODIFYCONTROLCHECK = "ModifyControlCheck";
	public static final String KEY_ACTION = "action";
	public static final String KEY_OPERATION = "operation";
	public static final String KEY_DUPLICATE = "duplicate";
	public static final String KEY_REUSE = "reuse";
	public static final String KEY_RELATIONS = "relations";
	public static final String KEY_ISROOTNODE = "isRootNode";
	public static final String KEY_KEYPARAMETER = "key";
	public static final String KEY_VALUEPARAMETER = "value";
	public static final String KEY_NLSKEY = "nlskey";
	public static final String KEY_DATA = "data";
	public static final String KEY_OPTIONS = "options";
	public static final String KEY_INCLUDE_DRAWINGS = "includeDrawings";
	public static final String KEY_USINGADVANCEDDUPLICATE = "usingAdvancedDuplicate";
	public static final String KEY_WIDTH = "WIDTH";
	public static final String KEY_WEIGHT = "WEIGHT";
	public static final String KEY_LINEARMASSDENSITY = "LINEARMASSDENSITY";
	public static final String KEY_CHAR_MAPPING = "CHAR_MAPPING";
	public static final String KEY_OLD_WEBWIDTH = "OldWebWidth";


	public static final String KEY_APP_BUSINESSAREA = "APPBusinessArea";
	public static final String KEY_APP_PRODUCT_CATEGORY_PLATFORM = "APPPCP";
	
	public static final String KEY_COLUMN_TYPE = "ColumnType";
	public static final String KEY_SUBSTITUTE_FOR = "SubstituteFor";
	public static final String KEY_ALTERNATE_FOR = "AlternateFor";
	public static final String STR_ALTERNATE = "Alternate";
	public static final String STR_SUBSTITUTE = "Substitute";
	public static final String STR_EBOM = "EBOM";
	public static final String KEY_SINGLE_FUNCTION = "singleFunction";
	public static final String STR_NA = "NA";
	public static final String STR_LA = "LA";
	public static final String STR_AP = "AP";
	public static final String STR_EU = "EU";
	public static final String STR_PRODUCT_SIZE = "ProductSize";
	public static final String STR_DSMPRODUCTSIZE = "DSMProductSize";
	public static final String STR_DEFAULT = "Default";
	public static final String STR_CHECKED = "checked";
	public static final String STR_UNCHECKED = "unchecked";
	public static final String STR_DISABLED = "disabled";	
	public static final String STR_ALL = "All";
	public static final String STR_GENERATE_REPORT = "Generate Report";
	public static final String STR_PGEBOMPLANNINGVIEWPLANTFILTER = "pgEBOMPlanningViewPlantFilter";	
	public static final String STR_PREVIEW_RELEASED_CHARARACTERISTICS = "Released Characteristic Master Revision";
	public static final String STR_PREVIEW_LATEST_CHARARACTERISTICS = "Latest Characteristic Master Revision";
	public static final String STR_COLLABORATION_WITHAPP = "Collaboration with APP";
	public static final String STR_CHARARACTERISTICS_PREVIEW = "Characteristics Preview";
	public static final String STR_MANUAL_EVALUATE_WITH_SYNC = "Manual Evaluate With Sync";
	public static final String STR_ISDESIGNPARAMPRESENT = "IsDesignParamPresent";
	public static final String STR_TM = "TM";
	public static final String STR_TAU = "TAU";

	public static final String STR_SPECLIMITS = "SpecLimits";
	public static final String STR_CHARACTERISTIC_DIMENSION_WIDTH = "Dimension ~ Width";
	public static final String STR_CHARACTERISTIC_BASIS_WEIGHT = "Basis Weight";
	public static final String STR_CHARACTERISTIC_LINEARMASSDENSITY = "Linear Mass Density";

	public static final String STR_SELECTABLE_SUBSTITUTES = "Substitutes";
	public static final String STR_SELECTABLE_RELIDS = "relIds";
	
	public static final String STR_BASIS_WEIGHT = "Basis Weight";
	public static final String STR_LINEAR_MASS_DENSITY = "Linear Mass Density";
	public static final String STR_DIMENSION_WIDTH = "Dimension ~ Width";
	public static final String STR_CAPACITY="Capacity";
	
	public static final String STR_UOM_SQUARE_METER = "SQUARE METER";
	public static final String STR_UOM_SQUARE_CENTIMETER = "SQUARE CENTIMETER";
	public static final String STR_UOM_GRAMS_PER_SQUARE_METER = "Grams per square meter";	
	public static final String STR_UOM_GRAMS_PER_10_KILOMETER = "Grams per 10 kilometer";
	public static final String STR_UOM_MILLIMETER_ACTUAL = "Millimeter";	
	public static final String STR_UOM_GRAMS_PER_METER = "Grams per meter";
	public static final String STR_UOM_KILOGRAM = "KILOGRAM";
	public static final String STR_UOM_GRAM = "GRAM";
	public static final String STR_MASS_UOM_GRAM = "Gram";
	public static final String STR_UOM_MILLIMETER = "MILLIMETER";
	public static final String STR_UOM_MM = "MM";
	public static final String STR_UOM_NEWTON = "NEWTON";
	public static final String STR_UOM_SQ_METER = "METER2";
	public static final String STR_UOM_SECOND = "SECOND";
	public static final String STR_UOM_KELVIN = "KELVIN";
	public static final String STR_UOM_FAHRENT = "FAHRENT";
	public static final String STR_UOM_GRAM_SYMBOL = "g";
	public static final String STR_UOM_SQUARE_CENTIMETER_SYMBOL = "cm2";
	public static final String STR_UOM_LITER = "LITER";
	public static final String STR_UOM_CUBIC_METER = "CUBIC METER";
	public static final String STR_UOM_METER = "METER";
	public static final String STR_UOM_EACH = "EACH";
	public static final String STR_UOM_ANGLE_PERCENT = "ANGLE_PERCENT";
	public static final String STR_UOM_DECA_NEWTON_OVER_MILLIMETER = "DECA_NEWTON_OVER_MILLIMETER";
	public static final String STR_UOM_CM_PER_MINUTE = "CM_PER_MINUTE";

	
	public static final String STR_GROSS_WEIGHT = "Gross Weight";
	public static final String STR_NET_WEIGHT = "Net Weight";
	public static final String STR_NET_WEIGHT_ERROR = "Net Weight Error";
	public static final String STR_QUANTITY = "Quantity";
	public static final String STR_NET_QUANTITY = "Net Quantity";
	public static final String STR_WEB_WIDTH = "Web Width";

	
	public static final String STR_AUTOMATION_ERROR_INVALID_PARAMETERS = "Invalid Parameters";
	public static final String STR_AUTOMATION_ERROR_PART_NODE = "Error in creating Part Node";
	public static final String STR_AUTOMATION_ERROR_BASE_PARAMETER = "Base for [CONFIGURATION] does not exist. Unable to save Variant";
	public static final String STR_AUTOMATION_ERROR = "Error";
	public static final String STR_AUTOMATION_ERROR_NO_INITIATIVE_CONFIGURATION = "No Initiative or Configuration Found";
	public static final String STR_AUTOMATION_ERROR_NO_CONFIGURATION = "No Configurations Found";
	public static final String STR_AUTOMATION_ERROR_NO_BASE_FOUND = "No Base Found";
	public static final String STR_AUTOMATION_ERROR_NO_VARIANTS = "No Variant present";
	public static final String STR_AUTOMATION_ERROR_DI_PREFERENCES_NOT_SET = "Valid DI Preferences not set";
	public static final String STR_AUTOMATION_ERROR_USER_NOIPACCESS = "User is not having IP access";
	public static final String STR_AUTOMATION_BLANK = "blank";
	public static final String STR_AUTOMATION_UNSET = "<Unset>";
	
	public static final String STR_AUTOMATION_APPNAME = "APPName";
	public static final String STR_AUTOMATION_APPREVISION = "APPRevision";
	public static final String STR_AUTOMATION_PARTTITLE = "PartTitle";
	public static final String STR_AUTOMATION_PARTNAME = "PartName";
	public static final String STR_AUTOMATION_MAJORREVISION = "MajorRevision";
	public static final String STR_AUTOMATION_MINORREVISION = "MinorRevision";
	public static final String STR_AUTOMATION_SIZE = "Size";
	public static final String STR_AUTOMATION_REGION = "Region";
	public static final String STR_AUTOMATION_SUBREGION = "SubRegion";	
	public static final String STR_AUTOMATION_BUSINESSAREA = "BusinessArea";
	public static final String STR_AUTOMATION_PRODUCTCATEGORYPLATFORM = "ProductCategoryPlatform";
	public static final String STR_AUTOMATION_PRODUCTTECHNOLOGYPLATFORM = "ProductTechnologyPlatform";
	public static final String STR_AUTOMATION_PRODUCTTECHNOLOGYCHASSIS = "ProductTechnologyChassis";
	public static final String STR_AUTOMATION_FRANCHISEPLATFORM = "FranchisePlatform";
	public static final String STR_AUTOMATION_PRIMARYORGANIZATION = "PrimaryOrganization";
	public static final String STR_AUTOMATION_DESIGN_PARAMETER_FILE_NAME = "Design_Parameters";
	public static final String STR_AUTOMATION_DESIGN_PARAMETER_PREVIOUS_FILE_NAME = "Design_Parameters_Previous";
	public static final String STR_AUTOMATION_DEFINITION = "Definition";
	public static final String STR_AUTOMATION_INTENDEDMARKETS = "IntendedMarket";
	public static final String STR_AUTOMATION_PARTMATURITYSTATE = "Maturity";
	public static final String STR_AUTOMATION_PARTRELEASEPHASE = "ReleasePhase";

	
	public static final String STR_ERROR_CODE = "ErrorCode";
	public static final String STR_ERROR_LIST = "ErrorList";
	public static final String STR_ERROR_CODE_E001 = "E001";
	public static final String STR_ERROR_CODE_E002 = "E002";
	public static final String STR_ERROR_CODE_E003 = "E003";
	public static final String STR_ERROR_CODE_E004 = "E004";
	public static final String STR_ERROR_CODE_E005 = "E005";
	public static final String STR_ERROR_CODE_E006 = "E006";
	public static final String STR_ERROR_CODE_E007 = "E007";
	public static final String STR_ERROR_CODE_E008 = "E008";
	public static final String STR_ERROR_CODE_E009 = "E009";
	public static final String STR_ERROR_CODE_E010 = "E010";
	public static final String STR_ERROR_CODE_E011 = "E011";
	public static final String STR_ERROR_CODE_E012 = "E012";
	public static final String STR_ERROR_CODE_E013_01 = "E013-01";
	public static final String STR_ERROR_CODE_E013_02 = "E013-02";
	public static final String STR_ERROR_CODE_E014 = "E014";
	public static final String STR_ERROR_CODE_E015 = "E015";
	public static final String STR_ERROR_CODE_E016 = "E016";
	public static final String STR_ERROR_CODE_E017 = "E017";
	public static final String STR_ERROR_CODE_E018 = "E018";
	public static final String STR_ERROR_CODE_E019 = "E019";
	public static final String STR_ERROR_CODE_E020 = "E020";

	
	public static final String STR_RMP_NOT_EXIST = "RMP does not exist";
	public static final String STR_COREMATERIAL_RMP_NOT_EXIST = "Core Material or RMP does not exist";
	public static final String STR_MATERIAL_CONNECTED = "Core Material already connected";
	public static final String STR_NO_COREMATERIAL_ASSOCIATED = "No Core Material assigned to RMP";
	public static final String STR_RELEASED_VPMREF_OBJECTNOTFOUND = "Released Base configuration not found. Variant Definition cannot be stored";
	public static final String STR_INVALID_LPDMODELTYPE = "Invalid or Empty LPD Model Type";
	public static final String STR_DESIGN_PARAMETER_NOT_PUBLISHED = "Design Parameters Details are not yet published";
	public static final String STR_GENERICMODEL_NOT_RELEASED = "The current Generic Model for <Title> is not in Release state";
	public static final String STR_MULTIPLE_RELEASED_BASES_FOUND = "Multiple Released Base configuration found. Variant Definition cannot be stored";

	public static final String STR_3DSHAPE_NOT_RELEASED = "The current 3DShape for <Title> is not in Release state";
	public static final String STR_MULTIPLE_TEMPLATES = "Multiple PLMTemplateRepReference objects are present";
	public static final String STR_NO_VPMREF_FOUND = "No VPMReference found";
	public static final String STR_NO_VPMREF_3DSHAPE_FOUND = "No VPMReference or 3DShape found";
	public static final String STR_NO_VPMREF_APP_FOUND = "No Assembled Product Part found";
	public static final String STR_NO_3DSHAPE_FOUND = "No 3DShape found";

	public static final String STR_NO_ENGG_TEMPLETE_FOUND = "No Engineering Template found";
	public static final String STR_MATURITYSTATUS_PRODUCTION = "Production";
	public static final String STR_MATURITYSTATUS_PILOT = "Pilot";
	public static final String STR_MATURITYSTATUS_DEVELOPMENT = "Development";
	
	public static final String KEY_SUBSTITUTETYPE = "SubstituteType";
	public static final String KEY_SUBSTITUTENAME = "SubstituteName";
	public static final String KEY_SUBSTITUTEREVISION = "SubstituteRevision";
	
	public static final String STR_ROOT = "Root";
	public static final String STR_PRODUCT_DEFINITION = "ProductDefinition";
	public static final String STR_VARIANTS = "Variants";
	public static final String STR_PRODUCT_DETAILS = "ProductDetails";
	public static final String STR_PERFORMANCE_CHAR = "PerformanceChar";
	public static final String STR_VARIANTS_PERFORMANCE_CHAR = "VariantsPerformanceChar";
	public static final String STR_INDEX = "index";
	public static final String STR_APP = "APP";
	public static final String STR_RUNLIST = "RunList";

	public static final String KEY_REGIONS = "regions";
	public static final String KEY_FUNCTION = "function";
	public static final String KEY_SEGMENT = "segment";
	
	public static final String KEY_CLEARANCE_LEVEL = "clearanceLevel";
	public static final String KEY_MAX_CLEARANCE_WEIGHT_MG = "mxWeightInMG";
	
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
	
	public static final String CRITERIA_STATE_IN_WORK = PropertyUtil.getSchemaProperty("policy", POLICY_CRITERIA, "state_In-Work");
	public static final String CRITERIA_STATE_FROZEN = PropertyUtil.getSchemaProperty("policy", POLICY_CRITERIA, "state_Frozen");
	public static final String CRITERIA_STATE_RELEASED = PropertyUtil.getSchemaProperty("policy", POLICY_CRITERIA, "state_Released");
	public static final String CRITERIA_STATE_OBSOLETE = PropertyUtil.getSchemaProperty("policy", POLICY_CRITERIA, "state_Obsolete");
	
	public static final String LPDGENERICCONFIGURATION_STATE_ACTIVE = PropertyUtil.getSchemaProperty("policy", POLICY_PGLPDGENERICCONFIGURATION, "state_Active");
	public static final String LPDGENERICCONFIGURATION_STATE_INACTIVE = PropertyUtil.getSchemaProperty("policy", POLICY_PGLPDGENERICCONFIGURATION, "state_Inactive");

	public static final String FORMAT_NOT_RENDERABLE = "Not Renderable";
	
	public static final String STR_DIMENTION_MASSFLOWPARAMETER = "MASSFLOWParameter";
	public static final String STR_DIMENTION_RATIOPARAMETER = "RatioParameter";
	public static final String STR_DIMENTION_STRINGPARAMETER = "StringParameter";
	public static final String STR_DIMENTION_REALPARAMTER = "RealParameter";
	public static final String STR_DIMENTION_MASSPARAMETER = "MASSParameter";
	public static final String STR_DIMENTION_FORCEPARAMETER = "FORCEParameter";
	public static final String STR_DIMENTION_AREAPARAMETER = "AREAParameter";
	public static final String STR_DIMENTION_TIMEPARAMETER = "TIMEParameter";
	public static final String STR_DIMENTION_LENGTHPARAMETER = "LENGTHParameter";
	public static final String STR_DIMENTION_TEMPRTREPARAMETER = "TEMPRTREParameter";
	public static final String STR_DIMENTION_STRING = "String";
	public static final String STR_DIMENTION_ANGLEPARAMETER = "ANGLEParameter";
	public static final String STR_DIMENTION_BOOLEANPARAMETER = "BooleanParameter";
	public static final String STR_DIMENTION_INTEGERPARAMETER = "IntegerParameter";
	public static final String STR_DIMENTION_FORCE_OVER_DISPLACEMENTPARAMETER = "FORCE_OVER_DISPLACEMENTParameter";
	public static final String STR_DIMENTION_SPEEDPARAMETER = "SPEEDParameter";
	public static final String STR_DIMENTION_CMYKFORMATTEDSTRINGPARAMETER = "CMYKFormattedStringParameter";
	public static final String STR_DIMENTION_HEXFORMATTEDSTRINGPARAMETER = "HEXFormattedStringParameter";
	public static final String STR_DIMENTION_RGBFORMATTEDSTRINGPARAMETER = "RGBFormattedStringParameter";
	public static final String STR_DIMENTION_SUBJECTIVITYPARAMETER = "SubjectivityParameter";
	
	public static final String STR_APOLLO_CONFIG_PAGE_FILENAME = "pgApolloConfigurations";
	public static final String STR_APOLLO_SUBSTITUTE_CONFIG_PAGE_FILENAME = "pgApolloSubstituteConfigurations";
	public static final String STR_CPN_STRING_RESOURCE_FILENAME = "emxCPNStringResource";	
	
	//Addeded for Comparison Report Enhancements - starts
	public static final String STR_DENIED = "#DENIED!";
	public static final String STR_NO_ACCESS = "No Access";
	public static final String STR_MODEL_TYPE = "Model Type";
	public static final String STR_ALTERNATE_PART = "Alternate Part";
	public static final String STR_ALTERNATE_PART_FOR = "Alternate Part For";
	public static final String STR_TEST_METHODS = "Test Method(s)";
	public static final String STR_TEST_METHOD = "Test Method";
	public static final String STR_EBOM_ID = "EBOM ID";
	public static final String STR_BASE_UOM = "BaseUOM";
	public static final String STR_SUBSTITUTE_PART = "Substitute Part";
	public static final String STR_SUBSTITUTE_FOR = "Substitute For";
	public static final String STR_SUBSTITUTE_FOR_TITLE = "Substitute For Title";
	public static final String STR_BASE_UNIT_OF_MEASURE = "Base Unit Of Measure";
	public static final String ATTRIBUTE_MIN_VALUE = "attribute[MinValue]";
	public static final String ATTRIBUTE_MAX_VALUE = "attribute[MaxValue]";
	public static final String ATTRIBUTE_PARAMETER_VALUE = "attribute[ParameterValue]";
	//Addeded for Comparison Report Enhancements - End
	public static final String STR_FORM_COPYDATA = "pgCopyDataDetails";
	public static final String STR_TABLE_PLANTS = "pgPlantDataSummary";
	public static final String STR_TABLE_SUBSTITUTES = "pgSubstitutePartSummary";
	public static final String STR_TABLE_REFDOCS = "pgReferenceDocumentSummary";
	public static final String STR_TABLE_PARTSPEC = "pgSpecSummary";
	public static final String STR_TABLE_COOWNER = "pgLPDDomainAccess";
	public static final String STR_TABLE_CERTIFICATIONS = "pgLPDCertificationsSummary";

	public static final  String STRING_MINVALUE = "MinValue";
	public static final  String STRING_PARAMETERVALUE = "ParameterValue";
	public static final  String STRING_MAXVALUE = "MaxValue";
	public static final  String STRING_MINRANGEPROPERTY = "MinRangeProperty";
	public static final  String STRING_MAXRANGEPROPERTY = "MaxRangeProperty";
	
	public static final String STR_MINIMUM_VALUE = "Minimum Value";
	public static final String STR_MAXIMUM_VALUE = "Maximum Value";
	public static final String STR_VALUE = "Value";
	public static final String STR_PREVIOUS_VALUE = "PreviousValue";
	public static final String STR_LOWER_TARGET = "Lower Target";
	public static final String STR_UPPER_TARGET = "Upper Target";
	public static final String STR_TARGET = "Target";
	public static final String STR_SOURCE = "Source";
	
	public static final  String STRING_EVALUATE_CRITERIA_JOB_TITLE = "Evaluate Criteria : ";
	public static final  String STRING_COLLABORATE_WITH_APP_JOB_TITLE = "Collaborate with APP : ";
	
	//Added for EBOM Sync History Messages -Start
	public static final String EBOM_SYNC_HISTORY_COLLABRATE_APP="Collaborate with APP : ";
	public static final String EBOM_SYNC_HISTORY_STARTS= "Start";
	public static final String EBOM_SYNC_HISTORY_END= "End";
	public static final String EBOM_SYNC_HISTORY_ERROR_TRANSFER_CONTROL="Error during Freeze Design and Transfer Control : ";
	public static final String EBOM_SYNC_HISTORY_SUCCESS_TRANSFER_CONTROL="Successfully completed. ";
	public static final String EBOM_SYNC_HISTORY_COLLABRATE_APP_ERROR="ERROR :";
	public static final String EBOM_SYNC_HISTORY_COLLABRATE_APP_ERROR_UPDATE_BOM ="Exception while updating EBOM ";
	public static final String EBOM_SYNC_HISTORY_COLLABRATE_APP_ERROR_UPDATE_MF ="Exception while updating Material Function ";
	public static final String EBOM_SYNC_HISTORY_COLLABRATE_APP_ERROR_UPDATE_CHARACTERISTIC="Exception while updating characteristics : ";
	public static final String EBOM_SYNC_HISTORY_COLLABRATE_APP_ERROR_MISSING_CHARACTERISTIC="Characteristic Master for <PERFCHAR> does not exist so characteristic cannot be created.";
	public static final String EBOM_SYNC_ENV_TRANSFER_CONTROL="TransferThroughCollaborateWithAPP~";
	public static final String EBOM_SYNC_HISTORY_COLLABRATE_APP_ERROR_NOT_DESIGNDRIVEN="Is Design Driven attribute on Characteristic Master for <PERFCHAR> is set to No so characteristic cannot be updated.";

	//Added for EBOM Sync History Messages -End
	//PG APOLLO 2018x.3 Defect 32603
	public static final String STR_SETTINGS = "SETTINGS";
	//PG APOLLO 2018x.3 Defect 32603
	
	public static final String STR_FRANCHISE = "Franchise";

	//PG APOLLO 2018x.5 ALM 35389 Allow 'Add as a Substitute' for CATIA APP - Starts
	public static final String STRING_SUB_CALC_WARNING_GLOBAL_ACTION_ENV = "STRING_SUB_CALC_WARNING_GLOBAL_ACTION_ENV";
	
	//PG APOLLO 2018x.5 ALM 35389 Allow 'Add as a Substitute' for CATIA APP - Ends
	
	//PG APOLLO 2018x.5 - Added for ALM 28164 - Starts
	public static final String PG_GLOBAL_REMOVE_TEST_METHOD_FOR_CM = "pgGlobalRemoveTestMethodForCM";
	public static final String PG_GLOBAL_REPLACE_TEST_METHOD_FOR_CM = "pgGlobalReplaceTestMethodForCM";
	//PG APOLLO 2018x.5 - Added for ALM 28164 - Ends

	//APOLLO 2018x.5 ALM 34489,34490,32151 - Starts
	public static final  String STRING_IMPORT_CM_RELEASE_MESSAGE = "CM created successfully and promoted to Release state.";
	public static final  String STRING_IMPORT_CM_INWORK_MESSAGE = "CM created successfully and it is In-Work state.";
	public static final  String STRING_IMPORT_CM_CRITERIA_CONNECTED = "Criteria connected successfully.";
	public static final  String STR_TEST_METHOD_REFERENCE_DOCUMENT = "Test Method Reference Document";
	//APOLLO 2018x.5 ALM 34489,34490,32151 - Ends

	//APOLLO 2018x.5 ALM 34995, 35097, 35133, 35134 - Starts
	public static final String TYPE_TEST_METHOD_SPECIFICATION = PropertyUtil.getSchemaProperty("type_TestMethodSpecification");
	public static final String TYPE_PG_TEST_METHOD = PropertyUtil.getSchemaProperty("type_pgTestMethod");
	public static final String ATTR_PG_ASSEMBLY_TYPE = PropertyUtil.getSchemaProperty("attribute_pgAssemblyType");
	public static final String SELECT_ATTRIBUTE_PG_ASSEMBLY_TYPE = "attribute["+ATTR_PG_ASSEMBLY_TYPE+"]";
	public static final String STR_TARGET_ACCEPTABLE_MARGINAL_UNACCEPTABLE = "Target Acceptable Marginal Unacceptable";
	//APOLLO 2018x.5 ALM 34995, 35097, 35133, 35134 - Ends
	//Apollo 2018x.5 Requirement 34689- Start
	public static final String STR_RELEASE_PHASE = PropertyUtil.getSchemaProperty("attribute_ReleasePhase");
	public static final String SELECT_ATTRIBUTE_RELEASE_PHASE  = (new StringBuilder()).append("attribute[").append(STR_RELEASE_PHASE).append("]").toString();
	public static final String STR_DEVELOPMENT_PHASE = "Development";
	public static final String STR_EXPERIMENTAL_PHASE ="Experimental";
	public static final String STR_PRODUCION_PHASE ="Production";
	public static final String STR_PILOT_PHASE="Pilot";
	//Apollo 2018x.5 Requirement 34689- End

	//PG APOLLO 2018x.5 ALM 34218 - Starts
	public static final String STR_MAP_RESULT_KEY_ID_CONNECTION = "id[connection]";
	public static final String STR_MAP_RESULT_KEY_PLANTS = "Plants";
	public static final String STR_MAP_RESULT_KEY_UNCOMMONPLANTS = "UncommonPlants";
	public static final String STR_MAP_RESULT_KEY_PRIMARYRMP = "PrimaryRMP";
	public static final String STR_MAP_RESULT_KEY_REPORTEDFUNCTION = "ReportedFunction";
	public static final String STR_MAP_RESULT_KEY_TITLE = "Title";
	public static final String STR_MAP_RESULT_KEY_NO = "No^";
	public static final String STR_MAP_RESULT_KEY_YES = "Yes^";
	public static final String STR_COMPARE_REPORT_TYPE_PLANNINGVIEW = "PLANNINGVIEW";
	//PG APOLLO 2018x.5 ALM 34218 - Ends
	public static final String STR_MAP_RESULT_KEY_DESCRIPTION = "Description";
	//PG APOLLO 2018x.5 ALM 28395 - Starts
	public static final String STR_SUBSTITUTES = "Substitute(s)";
	//PG APOLLO 2018x.5 ALM 28395 - Ends
	//PG APOLLO Requirement 28399 A10-510 - Starts
	public static final String STR_TEST_METHOD_TITLE = "Test Method Title";
	//PG APOLLO Requirement 28399 A10-510 - Ends
	
	public static final String KEY_PREVIOUSEBOM = "PreviousRevBOM";
	public static final String KEY_RELPATTERN = "RelPattern";
	public static final String KEY_RMPINFO = "RMPInfo";
	public static final String STR_NOTCONNECTED = "NotConnected";
	public static final String STR_CONNECTED = "Connected";
	public static final String STR_BLOCK_FOR_NEW = "Block for new parent objects";
	public static final String STR_BLOCK_FOR_ALL = "Block for new or revised parent objects";
	public static final String STR_WARNING = "Warning";
	public static final String STR_MATERIAL_RESTRICTION_WARNING = "Relationship Restriction Warning:";
	public static final String STR_BLOCK_FOR_EMPTY_WARNING = "Block for Empty Warning";	
	public static final String STR_MODE_MASSSUBSTITUTES = "massSubstitutes";
	
	//Apollo 2018x.5 Defect 33994 Start
	public static final String CONST_TYPE_RAWMATERIAL = "type_RawMaterial";
	public static final String CONST_TYPE_PG_ANCILLARY_RAW_MATERIAL_PART = "type_pgAncillaryRawMaterialPart";
	public static final String CONST_TYPE_PG_RAWMATERIAL = "type_pgRawMaterial";
	//Apollo 2018x.5 Defect 33994 End
  
	public static final String STR_DESCENDING = "descending";
	public static final String STR_ASCENDING = "ascending";
	public static final String STR_STRING = "String";

	//Apollo 2018x.5 Fix for ALM Defect 35237 - Starts
	public static final String KEY_WARNING_MESSAGE = "WarningMessage";
	//Apollo 2018x.5 Fix for ALM Defect 35237 - Ends
	
	public static final String RELATIONSHIP_PGPDTEMPLATES_TO_PGPLISEGMENT = PropertyUtil.getSchemaProperty("relationship_pgPDTemplatestopgPLISegment");
	public static final String SELECT_CAD_ID = CONSTANT_STRING_SELECT_FROM+DomainConstants.RELATIONSHIP_PART_SPECIFICATION+"].to["+TYPE_VPMREFERENCE+"].id";
	public static final String STR_SYMBOLIC_ASSEMBLED_PRODUCT_PART = "type_pgAssembledProductPart";
	public static final String STR_REVISION_VPMREF_FAILED = "Error Occured while revising Base Object. Please refer webservice logs for more details.";
	public static final String STR_BASE_NOT_RELEASED = "Base Object is not in RELEASED state.";
	
	public static final String KEY_ATTRIBUTESMAP = "AttributesMap";
	public static final String KEY_BU = "BU";
	public static final String KEY_TEMPLATE = "Template";
	public static final String KEY_REGION = "Region";
	public static final String KEY_SEGMENT_REVISE = "Segment";
	public static final String KEY_SPECTYPE = "specType";
	public static final String KEY_PRODUCTDATANAME = "productDataName";
	public static final String KEY_REVISION = "revision";
	public static final String KEY_POLICY = "policy";
	public static final String KEY_VAULTNAME = "vaultName";
	public static final String KEY_ATTRIBUTELIST = "attributeList";
	public static final String KEY_RELATEDOBJLIST = "relatedObjList";
	public static final String KEY_SPECAUTONAME = "specAutoName";
	public static final String KEY_TEMPLATEID = "templateId";
	public static final String KEY_STRSOURCEPRODDATAID = "strSourceProdDataId";
	public static final String KEY_CHANGETEMPLATE = "changeTemplate";
	public static final String KEY_CO = "CO";
	public static final String KEY_RDOID = "rdoId";


	//Apollo Dashborad widget String Constant start
	public static final String STR_Type = "Type";
	public static final String STR_PHASE = "Phase";
	public static final String STR_BA = "BA";
	public static final String STR_PCP = "PCP";
	public static final String STR_PTP = "PTP";
	public static final String STR_PTC = "PTC";
	//Apollo Dashborad widget String Constant end
	public static final String STR_DATE = "Date";
	public static final String STR_PORTAL = "portal";

	//Apollo 18x.6 Component Family Changes JIRA:A10-767 Start
	public static final String STR_EOPROJECT = "EOProject";
	public static final String STR_EOFOLDER = "EOFolder";
	public static final String STR_EOPROJECTTYPE = "EOProjectType";
	
	//Apollo 18x.6 Component Family Changes JIRA:A10-767 Start
	public static final String STR_SORT_ID = "Sort ID";
	public static final String STR_SAMPLE_SIZE = "Sample Size";
	public static final String STR_EOREPORT_SUBTYPE_EO_LAB_SAMPLING_PLAN_REPORT = "EO Lab Sampling Plan Report";
	public static final String STR_EOREPORT_SUBTYPE_EO_LOT_TRACKING_REPORT = "EO LOT Tracking Report";
	public static final String STR_EOREPORT_SUBTYPE_EO_MATERIAL_USAGE_REPORT = "EO Material Usage Report";
	public static final String STR_EOREPORT_SUBTYPE_EO_GRAVIMETRICS_REPORT = "EO Gravimetrics Report";
	public static final String STR_EOREPORT_SUBTYPE_EO_MATRIX_REPORT = "EO Matrix Report";
	public static final String STR_LPD_REPORT_SUBTYPE_QUALIFICATION_PROTOCOL = "Qualification Protocol";
	public static final String STR_NODENAME = "nodename";
	public static final String STR_CHECKEDOUTUSER = "checkedoutuser";
	public static final String EOCONFIG_OBJ_NAME ="pgEOReportConfig";
	
	
	public static final String XML_TAG_DISPATCHER_COMMANDS ="DispatcherCommands";
	public static final String XML_TAG_DISPATCHER_COMMAND="DispatcherCommand";
	public static final String XML_TAG_PARAMETER="Parameter";
	public static final String XML_ATTRIBUTE_ACTION ="Action";
	public static final String XML_ATTRIBUTE_NAME ="Name";
	public static final String XML_ATTRIBUTE_PARAM_NAME ="ParameterName";
	public static final String XML_ATTRIBUTE_PARAM_VALUE ="ParameterValue";

	public static final String STR_ARGSFILE_PREFIX ="ArgsFile_";
	public static final String STR_RESULTFILE_PREFIX ="DispatcherResults_";
	public static final String STR_NODEINFO_PRIFIX="NodeInfo_";
	public static final String DISPATCHER_SUCCESS_CODE ="0";
	public static final String XML_TAG_PXMLNODE = "pxmlnode";
	public static final String XML_TAG_ATTRIBUTE = "attribute";
	public static final String XML_ATTRIBUTE_UUID = "uuid";

	public static final String CONSTANT_LOG_PATH = "../logs/";
	public static final String LOGGING_MODE_WEBSERVICE = "WEBSERVICE";
	public static final String LOGGING_MODE_SYNC = "SYNC";
	public static final String LOGGING_MODE_COMMON = "COMMON";	
	public static final String LOGGING_MODE_EO = "EO";

	public static final String STR_WEBSERVICE_LOGFILE = "pgLPDWebservice";
	public static final String STR_EO_LOGFILE = "pgEOReport";
	public static final String STR_COMMON_LOGFILE = "pgLPDCommon";

	public static final String STR_CHAR_DIMENSION = "Dimension";
	public static final String ATTRIBUTE_OBSCUREUNITOFMEASURE = PropertyUtil.getSchemaProperty("attribute_ObscureUnitofMeasure");
	public static final String SELECT_ATTRIBUTE_OBSCUREUNITOFMEASURE = "attribute["+ATTRIBUTE_OBSCUREUNITOFMEASURE+"]";
	public static final String LANGUAGE_ENGLISH = "en";
	public static final String KEY_TEST_METHOD_TYPE = "TestMethodType";
	public static final String KEY_TEST_METHOD_TITLE = "TestMethodTitle";
	public static final String KEY_TEST_METHOD_DESC = "TestMethodDescription";
	public static final String KEY_TEST_METHOD_REV = "TestMethodRevision";
	public static final String KEY_TMRD_TYPE = "TestMethodReferenceDocumentType";
	public static final String KEY_TMRD_TITLE = "TestMethodReferenceDocumentTitle";
	public static final String KEY_TMRD_DESC = "TestMethodReferenceDocumentDescription";
	public static final String KEY_TMRD_REV = "TestMethodReferenceDocumentRevision";
	public static final String KEY_CM_NAME = "CharacteristicMasterName";
	public static final String KEY_CM_REV = "CharacteristicMasterRevision";
	public static final String KEY_CM_TITLE = "CharacteristicMasterTitle";
	public static final String KEY_NETWEIGHTUNITOFMEASURE = "NetWeightUnitOfMeasure";
	public static final String KEY_GROSSWEIGHTUNITOFMEASURE = "GrossWeightUnitOfMeasure";
	public static final String KEY_RMPDESCRIPTION = "RMPDescription";
	public static final String KEY_TAMU = "TAMU";
	public static final String STR_BABY_CARE = "Baby Care";
	public static final String KEY_CHARACTERISTIC_MASTER = "CharacteristicMaster";

	public static final String CONSTANT_OPEN_SQUARE_BRACKET = "[";
	public static final String CONSTANT_CLOSE_SQUARE_BRACKET = "]";
	public static final String CONSTANT_STRING_GREATERTHAN = ">";
	public static final String CONSTANT_STRING_LESSTHAN = "<";
	public static final String CONSTANT_STRING_DOUBLEQUOTE = "\"";
	public static final String CONSTANT_STRING_SINGLEQUOTE = "'";
	public static final String CONSTANT_STRING_FORWARD_SLASH = "/";

	public static final String STR_OPEN_SQUARE_BRACKET_CHAR = "_OPEN_SQUARE_BRACKET_CHAR_";	
	public static final String STR_CLOSE_SQUARE_BRACKET_CHAR = "_CLOSE_SQUARE_BRACKET_CHAR_";
	public static final String STR_DOUBLE_QUOTE_CHAR = "&quot;";	
	public static final String STR_SINGLE_QUOTE_CHAR = "&apos;";	
	public static final String STR_GREATER_THAN_CHAR = "&gt;";	
	public static final String STR_LESS_THAN_CHAR = "&lt;";	
	public static final String STR_AMPERSAND_CHAR = "&amp;";
	public static final String STR_FORWARD_SLASH_CHAR = "_FORWARD_SLASH_CHAR_";
	public static final String SELECT_ATTRIBUTE_APPLICATION = "attribute["+ATTRIBUTE_APPLICATION+"]";
	public static final String STR_AUTOMATION_EO_REPORTS_FILE_NAME = "PerformanceChar_MaterialUsages_";
	public static final String STR_AUTOMATION_EO_REPORTS_DESINGPARAM_FILE_NAME = "ProductDefinition_";
	
	public static final String STR_LAYERGROUP = "LayerGroup";
	public static final String STR_CHAR = "CHAR";
	public static final String STR_LAYER = "Layer";
	public static final String STR_MATERIAL_USAGES_EO = "MaterialUsages";
	public static final String XML_NODE_ATTRIBUTE_P7APEOMATRIXREVISION = "P7APEOMatrixRevision";
	public static final String XML_NODE_ATTRIBUTE_P7APSELECTEDVARIANTSREL = "P7APSelectedVariantsRel";
	public static final String XML_NODE_ATTRIBUTE_P7APABSTRACTCFG = "P7APAbstractCfg";
	public static final String XML_NODE_ATTRIBUTE_P7APLEARNINGOBJECTIVE = "p7apLearningObjective";
	public static final String XML_NODE_ATTRIBUTE_P7APABSTRACTCFGREVISION = "P7APAbstractCfgRevision";
	public static final String XML_NODE_ATTRIBUTE_P7APCODEID = "p7apCodeId";
	public static final String XML_NODE_ATTRIBUTE_P7APEOMATRIX = "P7APEOMatrix";
	public static final String XML_NODE_PXMLNODES = "pxmlnodes";
	public static final String XML_NODE_ATTRIBUTE_P7APREFERENCEDVARIANT = "p7apReferencedVariant";
	public static final String XML_NODE_ATTRIBUTE_P7APMTLCHGOVERREVISION = "P7APMtlChgOverRevision";
	public static final String XML_NODE_ATTRIBUTE_P7APMTLUSAGEREVISION = "P7APMtlUsageRevision";
	public static final String XML_NODE_ATTRIBUTE_P7APMTLGRAVIMETRICS = "P7APGravimetricsRevision";
	public static final String XML_NODE_ATTRIBUTE_P7APQUALPROTOCOLREVISION ="P7APQualProtocolRevision";
	
	public static final String STR_FAILED = "Failed";
	public static final String STR_SUCCEEDED = "Succeeded";
	public static final String HTML_TAG_BR = "<BR>";
	public static final String STR_ATTR_FLOWTESTDURATION_SUFFIX = " min";
	public static final String NODE_XLM_MAP_KEY_DISPLAYNAME = "displayName";
	public static final String NODE_XLM_MAP_KEY_DISPLAYVALUE = "displayValue";
	public static final String NODE_XML_MAP_VALUE_FLOW_TEST_DURATION = "Flow Test Duration";
	public static final String NODE_XML_MAP_VALUE_FLOWTESTDURATION = "flowTestDuration";
	public static final String NODE_XML_MAP_VALUE_PERFUMEFLOWTESTDURATION = "perfumeFlowTestDuration";
	public static final String NODE_XML_MAP_VALUE_PERFUME_FLOW_TEST_DURATION = "Perfume Flow Test Duration";
	public static final String STR_EOMATRIX_RESULT_FILE_PREFIX = "EOMatrix_";
	public static final String STR_EOMATRIX_RESULT_FILE_EXTENSION = ".xlsm";
	public static final String STR_QUALIFICATION_PROTOCOL_RESULT_FILE_EXTENSION = ".docx";
	public static final String KEY_RMP_CLASS = "RMPClass";
	public static final String STR_RE_GENERATE = "ReGenerate";
	
	public static final String STR_LSP_RESULT_FILE_PREFIX = "LSPReport_";
	public static final String STR_MATERIALUSAGE_RESULT_FILE_PREFIX = "MaterialUsageReport_";
	public static final String STR_GRAVIMETRICS_RESULT_FILE_PREFIX = "GravimetricsReport_";
	public static final String STR_LOTTRACKING_RESULT_FILE_PREFIX = "LotTrackingReport_";
	public static final String STR_QUALIFICATION_PROTOCOL_FILE_PREFIX = "QualificationProtocol_";	
	public static final String STR_MSPR_REPORT_PREFIX = "MSPR-";	
	public static final String STR_MSPR_REPORT_EXTENSION = STR_XLSXFILE_EXTENSION;	
	
	

	//Apollo 18x.6 EO Report Changes ALM-37439 End
	public static final String STR_MESSAGE_PROVIDE_CHARACTERS = "Please provide at least 3 characters";
	public static final String CONSTANT_STRING_DOUBLE_LESS_THAN = ">>";
	public static final String STR_OBJECT_ID = "Object Id";
	//Apollo ALM-39879  Changes Start
	public static final String HTML_TAG_STRIKEOUT_START = "<s>";
	public static final String HTML_TAG_STRIKEOUT_END = "</s>";
	public static final String STR_NOT_AUTHORIZED = "Not Authorized";
	public static final String STR_NOT_ACTIVATED = "Not Activated";
	//Apollo ALM-39879  Changes End
	//Apollo 18x.6 EO Report Changes ALM-37439 End
	public static final String STR_FAIL = "Fail";
	public static final String KEY_EXCELROW = "excelRow";
	
	public static final String STR_MODE_PRODUCTDESIGN = "ProductDesign";
	public static final String STR_MODE = "mode";
	public static final String STR_SELECTEDROWID = "selectedRowId";
	public static final String STR_3DPALYSUBHEADING = "3DPalySubHeading";
	public static final String SELECT_HAS_READ_ACCESS = "current.access[read]";  
	
	public static final String STR_BASE_DESIGN = "Base Design";
	public static final String STR_DESIGN_COPIED_TO = "Design Copied To";
	public static final String STR_DESIGN_COPIED_FROM = "Design Copied From";
	public static final String STR_GENERIC_DESIGN = "Generic Design";//A10-967 Changes

	//Apollo Requirement 38722, 38723, 38724 and 38725 Changes Start
	public static final String STR_COMMANDNAME_ECM3DAFFECTEDITEMSCA = "ECM3DAffectedItemsCA";
	public static final String STR_COMMANDNAME_ECMCAIMPLEMENTEDITEMS = "ECMCAImplementedItems";	
	//Apollo Requirement 38722, 38723, 38724 and 38725 Changes End
	
	//Apollo ALM-41061  Changes Starts
	public static final String STR_CUSTO_STARTTAG_IDS = "IDS#";
	public static final String STR_CUSTO_ENDTAG_IDE = "IDE#";
	//Apollo ALM-41061  Changes End
	//context.printTrace(TRACE_LPD, "Message");
	public static final String TRACE_LPD = "LPD";  
	
	//A10-924 Category Specifics - Changes - Starts
	public static final String ATTRIBUTE_PG_CATEGORYSPECIFICS= PropertyUtil.getSchemaProperty("attribute_pgCategorySpecifics");
	public static final String SELECT_ATTRIBUTE_PG_CATEGORYSPECIFICS = new StringBuilder("attribute[").append(ATTRIBUTE_PG_CATEGORYSPECIFICS).append("]").toString();
	//A10-924 Category Specifics - Changes - Ends

	//Apollo 2018x.6 Change Summary Report - Starts
	public static final String STR_TABLE_TR_HTML_TAG = "</td></tr><tr><td>";
	public static final String STR_REMOVE_HTML_TAGS = "\\<.*?\\>";
	public static final String CONSTANT_STRING_COMMA_SPACE = ", ";
	public static final String CONSTANT_STRING_SPACE_COLON_SPACE = " : ";
	public static final String STR_CHANGE = "Change";
	public static final String STR_SECTION = "Section";
	public static final String STR_ENTERPRISECHANGEMGT_STRING_RESOURCE = "emxEnterpriseChangeMgtStringResource";
	public static final String STR_A_HREF_EMXTABLECOLUMNLINK = "<a href=\"JavaScript:emxTableColumnLinkClick('../common/emxTree.jsp?objectId=";
	public static final String KEY_SETTINGS = "settings";
	public static final String KEY_LABEL = "label";
	public static final String KEY_REGISTERED_SUITE = "Registered Suite";
	public static final String KEY_ATTRIBUTE_NAME = "ATTRIBUTE_NAME";
	public static final String KEY_ATTR1 = "ATTR1";
	public static final String KEY_ATTR2 = "ATTR2";
	public static final String STR_COMMAND_PGCACHANGESUMMARYATTRIBUTES = "pgCAChangeSummaryAttributes";
	//Apollo 2018x.6 Change Summary Report - Starts

	public static final String APOLLO_TRIGGER_CONFIG_OBJ_NAME ="pgApolloTriggerConfig";
		
	public static final String STRING_SECONDARY_ORGANIZATION = "Secondary Organization";
	
	// LPD Global Actions - Starts
	public static final String KEY_FILELIST = "FileList";	
	public static final String KEY_LPDGLOBALFILESUPLOADSELECTEDOBJECTS = "LPDGlobalFilesUploadSelectedObjects";	
	public static final String KEY_PHYSICALPRODUCT_NAME = "PhysicalProductName";
	public static final String KEY_PHYSICALPRODUCT_REVISION = "PhysicalProductRevision";
	public static final String KEY_DESIGN_CONTROLLED = "DesignControlled";
	
	public static final String MAX_STRING_LENGTH = "org.apache.johnzon.max-string-length";
	public static final String VALUE_KILOBYTES = "262144";

	public static final String STR_LPD_GLOBAL_ACTIONS_UPLOAD_FILES = "LPD Global Actions Upload Files";	
	public static final String STR_LPD_GLOBAL_ACTIONS_UPDATE_PLANTS = "LPD Global Actions Assign Plants";	
	public static final String STR_LPD_GLOBAL_ACTIONS_COLLABORATE_WITH_PHYSICAL = "LPD Global Actions Transfer Control to CATIA";	
	public static final String STR_LPD_GLOBAL_ACTIONS_MASS_REVISE = "LPD Global Actions Mass Revise";	
	public static final String STR_ERROR_COLLABORATE_WITH_PHYSICAL= "Error while Transfer Control to CATIA";
	// LPD Global Actions - Ends
	//Apollo 2018x.6 JIRA A10-1136 - STARTS
	public static final String RANGE_PG_LPD_MODEL_UPDATE_STATUS_NOT_APPLICABLE = "Not Applicable";
	public static final String RANGE_PG_LPD_MODEL_UPDATE_STATUS_CURRENT = "Current";
	public static final String RANGE_PG_LPD_MODEL_UPDATE_STATUS_MANDATORY = "Mandatory";
	public static final String RANGE_PG_LPD_MODEL_UPDATE_STATUS_OPTIONAL = "Optional";
	//Apollo 2018x.6 JIRA A10-1136 - ENDS

	public static final String CONSTANT_STRING_3DPART = "3DPart";
	public static final String CONSTANT_STRING_3DSHAPE = "3DShape";
	public static final String CONSTANT_STRING_DRAFTING_REPRESENTATION = "DraftingRepresentation";

	public static final String RANGE_REQUESTEDCHG_REVISE_VALUE = "For Revise";
	public static final String RANGE_REQUESTEDCHG_MAJOR_REVISE_VALUE = "For Major Revise";
	public static final String STR_MODE_ADDBASEAPP = "addBaseAPP";

	public static final String ATTRIBUTE_APP_DISPLAY_NAME = "App Display Name";
	public static final String TYPE_APP_DEFINITION = "AppDefinition";
	
	//APP Request Trigger and 3DSpace Changes - Starts	
	public static final String ATTRIBUTE_PG_APP_REQUESTCHANGETYPE = PropertyUtil.getSchemaProperty("attribute_pgAPPRequestChangeType");
	public static final String ATTRIBUTE_PG_IMPLEMENTEDITEM = PropertyUtil.getSchemaProperty("attribute_pgImplementedItem");

	public static final String SELECT_ATTRIBUTE_PG_APP_REQUESTCHANGETYPE = "attribute["+ATTRIBUTE_PG_APP_REQUESTCHANGETYPE+"]";
	
	public static final String TYPE_PGAPPREQUEST=PropertyUtil.getSchemaProperty("type_pgAPPRequest");

	public static final String RELATIONSHIP_PGAPPREQUESTAFFECTEDITEM=PropertyUtil.getSchemaProperty("relationship_pgAPPRequestAffectedItem");
	public static final String RELATIONSHIP_PGAPPREQUESTIMPLEMENTEDITEM=PropertyUtil.getSchemaProperty("relationship_pgAPPRequestImplementedItem");
	public static final String RELATIONSHIP_PGAPPREQUESTCHANGEDATA=PropertyUtil.getSchemaProperty("relationship_pgAPPRequestChangeData");

	public static final String STR_TITLE_IMPLEMENTAPPREQUEST="Implement APP Requests";
	public static final String RANGE_PGAPPREQUESTCHANGETYPE_ACCELERATED_RELEASE_ADDITION = "Accelerated Release";
	public static final String RANGE_PGAPPREQUESTCHANGETYPE_OBSOLESENCE = "Obsolescence Request";
	public static final String RANGE_PGAPPREQUESTCHANGETYPE_SUBSTITUTE_ADDITION = "Substitutes Addition";
	public static final String RANGE_PGAPPREQUESTCHANGETYPE_PLANTS_ADDITION = "Plants Addition";
	public static final String RANGE_PGAPPREQUESTCHANGETYPE_EXPIRATION_DATE_CHANGE = "Expiration Date Change";
	public static final String RANGE_PGAPPREQUESTCHANGETYPE_MFG_STATUS_CHANGE = "Manufacturing Status Change";
	

	public static final String POLICY_PGAPPREQUEST=PropertyUtil.getSchemaProperty("policy_pgAPPRequest");
	
	public static final String PGAPPREQUEST_STATE_CREATE = PropertyUtil.getSchemaProperty("policy", POLICY_PGAPPREQUEST, "state_Create");
	public static final String PGAPPREQUEST_STATE_REVIEW = PropertyUtil.getSchemaProperty("policy", POLICY_PGAPPREQUEST, "state_Review");
	public static final String PGAPPREQUEST_STATE_READY_TO_IMPLEMENT = PropertyUtil.getSchemaProperty("policy", POLICY_PGAPPREQUEST, "state_ReadyToImplement");
	public static final String PGAPPREQUEST_STATE_IMPLEMENTED = PropertyUtil.getSchemaProperty("policy", POLICY_PGAPPREQUEST, "state_Implemented");
	public static final String PGAPPREQUEST_STATE_RELEASED = PropertyUtil.getSchemaProperty("policy", POLICY_PGAPPREQUEST, "state_Released");
	
	//Table addition
	public static final String ATTRIBUTE_PG_PROJECT = PropertyUtil.getSchemaProperty("attribute_pgProject");
	public static final String SELECT_ATTRIBUTE_PG_PROJECT = "attribute["+ATTRIBUTE_PG_PROJECT+"]";

	public static final String ATTRIBUTE_PG_LPD_PLANT = PropertyUtil.getSchemaProperty("attribute_pgLPDPlants");
	public static final String SELECT_ATTRIBUTE_PG_LPD_PLANT = "attribute["+ATTRIBUTE_PG_LPD_PLANT+"]";

	public static final String RELATIONSHIP_PG_APP_REQUEST_CHANGE_DATA =PropertyUtil.getSchemaProperty("relationship_pgAPPRequestChangeData");
	public static final String GET_ASSOCIATED_APP_REQUEST_CHANGE_DATA_ID = new StringBuilder().append(pgApolloConstants.CONSTANT_STRING_SELECT_TO).append(pgApolloConstants.RELATIONSHIP_PG_APP_REQUEST_CHANGE_DATA).append(pgApolloConstants.CONSTANT_STRING_SELECT_FROMID).toString();
	
	public static final String KEY_VALID_START_DATE = "ValidStartDate";
	public static final String KEY_VALID_UNTIL_DATE = "ValidUntilDate";
	public static final String KEY_GROUP_NAME = "GroupName";
	public static final String KEY_LAYER_NAME = "LayerName";
	public static final String KEY_COMMENTS_CAPITAL = "Comments";
	public static final String KEY_AUTHORIZE_TO_PRODUCE = "AuthorizedToProduce";
	public static final String KEY_AUTHORIZE_TO_USE = "AuthorizedToUse";
	public static final String KEY_ACTIVATED = "Activated";
	
	//APP Request Trigger and 3DSpace Changes - Ends	
}

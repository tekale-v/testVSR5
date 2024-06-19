package com.pg.plm.util;

public class PLMConstants {
	 public static final String ECO = "ECO";
	  //Select Constants
	  public static final String SELECT_NAME = "name";
	  public static final String SELECT_ID = "id";
	  public static final String SELECT_OWNER = "owner";
	  public static final String SELECT_RELATIONSHIP_ID = "id[connection]";
	  
	  //Object Basics Constants
	  public static final String VAULT_PRODUCTION = "eService Production";
	  public static final String REVISION_ONE = "001";

	  public static final boolean CREATE_MISSING_OBJECTS = true;
	  public static final String SPEC_OWNER = "Corporate";
	  public static final String CONSUMER_UNIT = "Consumer_Unit";
	  public static final String CUSTOMER_UNIT = "Customer_Unit";
	  public static final String INNER_PACK = "Inner_Pack"; 
	  
	  //Policy Constants
	  public static final String ORGANIZATION_POLICY = "Organization";
	  public static final String POLICY_TEST_METHOD = "IPM Specification";
	  public static final String PERF_CHAR_POLICY = "Characteristic";
	  public static final String PGPHASE_POLICY = "pgBasicPart";
	  public static final String FORMULATED_PRODUCT_POLICY = "IPM Restricted Part";
	  public static final String MATERIAL_PRODUCT_POLICY = "IPM Part";
	  public static final String POLICY_CHARACTERISTIC = "Characteristic";
	  public static final String DOCUMENT_POLICY = "Document";
	  public static final String POLICY_COMMON_TABLE = "Common Table";
	  
	  //Format constants
	  public static final String FILE_FORMAT = "generic";
	  
	  //Type Constants
	  public static final String TYPE_PERF_CHAR = "pgPerformanceCharacteristic";
	  public static final String TYPE_TEST_METHOD = "pgTestMethod";
	  public static final String TYPE_PLANT = "Plant";
	  public static final String TYPE_APPROVED_SUPPLIER_LIST = "pgApprovedSupplierList";
	  public static final String TYPE_ARTWORK = "pgArtwork";
	  public static final String TYPE_CONSUMER_DESIGN_BASIS = "pgConsumerDesignBasis";
	  public static final String TYPE_ILLUSTRATION = "pgIllustration";
	  public static final String TYPE_LOGISTIC_SPEC = "pgLogisticSpec";
	  public static final String TYPE_MAKING_INSTRUCTIONS = "pgMakingInstructions";
	  public static final String TYPE_PROCESS_STANDARDS = "pgProcessStandard";
	  public static final String TYPE_PACKING_INSTRUCTIONS = "pgPackingInstructions";
	  public static final String TYPE_STACKING_PATTERN = "pgStackingPattern";
	  public static final String TYPE_QUALITY_SPECIFICATION = "pgQualitySpecification";
	  public static final String TYPE_STANDARD_OPERATING_PROCEDURE = "pgStandardOperatingProcedure";
	  public static final String TYPE_SUPPLIER_INFORMATION_SHEET = "pgSupplierInformationSheet";
	  public static final String TYPE_FINISHED_PRODUCT = "pgFinishedProduct";
	  public static final String TYPE_MASTER_FINISHED_PRODUCT = "pgMasterFinishedProduct";
	  public static final String TYPE_FORMULATED_PRODUCT = "pgFormulatedProduct";
	  public static final String TYPE_BASE_FORMULATED_PRODUCT = "pgBaseFormula";
	  public static final String TYPE_BASE_FORMULA = "pgBaseFormula";
	  public static final String TYPE_RAW_MATERIAL = "pgRawMaterial";
	  public static final String TYPE_MASTER_RAW_MATERIAL = "pgMasterRawMaterial";
	  public static final String TYPE_PACKING_MATERIAL = "pgPackingMaterial";
	  public static final String TYPE_MASTER_PACKING_MATERIAL = "pgMasterPackingMaterial";
	  public static final String TYPE_NON_GCAS_PART = "pgNonGCASPart";
	  public static final String TYPE_PHASE = "pgPhase";
	  public static final String TYPE_PROD_QUALITY_CHAR = "pgProductQualityCharacteristic";
	  public static final String TYPE_PACK_COMP_NOTE = "pgNotesCharacteristic";
	  public static final String TYPE_ENVI_CHAR = "pgEnvironmentalCharacteristic";
	  public static final String TYPE_MATERIALCONCHAR_CHAR="pgMaterialConstructionCharacteristic";
	  public static final String TYPE_PACK_QUALITY_CHAR = "pgPackageQualityCharacteristic";
	  public static final String TYPE_MANU_MATERIAL = "pgManufacturerMaterial";
	  public static final String TYPE_PACK_UNIT_CHAR = "pgPackingUnitCharacteristic";
	  public static final String TYPE_TRAN_UNIT_CHAR = "pgTransportUnitCharacteristic";
	  public static final String TYPE_SHARED_TABLE = "Shared Table";
	  public static final String TYPE_DOCUMENT = "Document";
	  public static final String TYPE_BUSINESS_UNIT = "Business Unit";
	  public static final String TYPE_MATERIAL = "pgMaterial";
	  
	  //Relationship Constants
	  public static final String REL_MANUF_RESPON = "Manufacturing Responsibility";
	  public static final String REL_PART_SPEC = "Part Specification";
	  public static final String REL_EBOM_SUBS = "EBOM Substitute";
	  public static final String REL_EBOM = "EBOM";
	  public static final String REL_SUPERSEDE = "pgSupersedes";
	  public static final String REL_ATS = "Authorized Temporary Specification";
	  public static final String REL_CHARACTERISTIC = "Characteristic";
	  public static final String REL_ASL_ROW = "pgApprovedSupplierListRow";
	  public static final String ATT_SUPERSEDE_ON_DATE = "pgSupersedesOnDate";
	  public static final String REL_SHARED_TABLE = "Shared Table";
	  public static final String REL_TESTING_REQUIREMENTS = "Reference Document";
	  public static final String REL_DOCUMENT = "Reference Document";
	  public static final String REL_SHARING_ORG = "pgCSSOrganization";
	  public static final String REL_DEFINES_MATERIAL = "pgDefinesMaterial";
	  public static final String REL_MASTER = "pgMaster";
	  public static final String REL_ASL = "pgApprovedSupplierList";
	  public static final String REL_SHARED_CHARACTERISTIC = "Shared Characteristic";
	  public static final String REL_DESIGN_RESPONSIBILITY = "Design Responsibility";
	  
}
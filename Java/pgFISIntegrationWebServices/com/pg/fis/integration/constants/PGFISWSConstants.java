package com.pg.fis.integration.constants;

import java.io.StringReader;
import java.util.Properties;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.framework.ui.UIUtil;

import matrix.db.Context;

public class PGFISWSConstants {
	//Type
	public static final String TYPE_FORMULATION_PART = PropertyUtil.getSchemaProperty("type_FormulationPart");
	public static final String TYPE_RAW_MATERIAL = PropertyUtil.getSchemaProperty("type_RawMaterial");
	public static final String TYPE_FORMULATION_PROCESS = PropertyUtil.getSchemaProperty("type_FormulationProcess");
	public static final String TYPE_INTERNAL_MATERIAL = PropertyUtil.getSchemaProperty("type_InternalMaterial"); 
	public static final String TYPE_SUBSTANCE = PropertyUtil.getSchemaProperty("type_Substance");
	public static final String TYPE_PRODUCT_FORM = PropertyUtil.getSchemaProperty("type_pgPLIProductForm");
	public static final String TYPE_PERSON = PropertyUtil.getSchemaProperty("type_Person");
	public static final String TYPE_IP_CONTROL_CLASS = PropertyUtil.getSchemaProperty("type_IPControlClass");
	public static final String TYPE_SECURITY_CONTROL_CLASS = PropertyUtil.getSchemaProperty("type_SecurityControlClass");
	public static final String TYPE_PGPLIBUSINESS_AREA = PropertyUtil.getSchemaProperty("type_pgPLIBusinessArea");
	public static final String TYPE_PGPLIPRODUCT_CACTEGORY_PLATFORM = PropertyUtil.getSchemaProperty("type_pgPLIProductCategoryPlatform");
	public static final String TYPE_PGPLIPRODUCT_TECHNOLOGY_PLATFORM = PropertyUtil.getSchemaProperty("type_pgPLIProductTechnologyPlatform");
	public static final String TYPE_PGPLIPRODUCT_TECHNOLOGY_CHASSIS = PropertyUtil.getSchemaProperty("type_pgPLIProductTechnologyChassis");
	public static final String TYPE_PGPLI__MATERIAL_FUNCTIONALITY = PropertyUtil.getSchemaProperty("type_pgPLIMaterialFunctionality");
	public static final String TYPE__MATERIAL_FUNCTIONALITY = PropertyUtil.getSchemaProperty("type_MaterialFunctionality");
	
	//Relationship
	public static final String REL_PGPRIMARYORGANIZATION = PropertyUtil.getSchemaProperty("relationship_pgPrimaryOrganization");
	public static final String REL_OWNINGPRODUCTLINE = PropertyUtil.getSchemaProperty("relationship_OwningProductLine");
	public static final String REL_GPSASSESSMENTTASKINPUT = PropertyUtil.getSchemaProperty("relationship_pgGPSAssessmentTaskInputs");
	public static final String REL_FBOM = PropertyUtil.getSchemaProperty("relationship_FBOM");
	public static final String REL_PLANNED_FOR = PropertyUtil.getSchemaProperty("relationship_PlannedFor");
	public static final String REL_PGPLISEGMENT = PropertyUtil.getSchemaProperty("relationship_pgPDTemplatestopgPLISegment");
	public static final String REL_COMPONENT_MATERIAL = PropertyUtil.getSchemaProperty("relationship_ComponentMaterial");
	public static final String REL_COMPONENT_SUBSTANCE = PropertyUtil.getSchemaProperty("relationship_ComponentSubstance");
	public static final String REL_SIGNUPFORM = PropertyUtil.getSchemaProperty("relationship_pgSignupForm");
	public static final String REL_CLASSIFICATION_LICENSE= PropertyUtil.getSchemaProperty("relationship_EXCClassificationLicense");
	public static final String REL_LICENSED_PEOPLE= PropertyUtil.getSchemaProperty("relationship_EXCLicensedPeople");
	public static final String SELECT_ORGNIZATION_ID = "from[" + REL_PGPRIMARYORGANIZATION + "].to.id";
	public static final String SELECT_SEGMENT_NAME = "from[" + REL_PGPLISEGMENT + "].to.name";
	public static final String SELECT_PLANNED_FOR_TO_TYPE = "from[" + REL_PLANNED_FOR + "].to.type";
	public static final String SELECT_PLANNED_FOR_TO_ID = "from[" + REL_PLANNED_FOR + "].to.physicalid";
	public static final String SELECT_PLANNED_FOR_FROM_ID = "to[" + REL_PLANNED_FOR + "].from.physicalid";
	public static final String SELECT_COMPONENT_MATERIAL_TO_ID = "from[" + REL_COMPONENT_MATERIAL + "].to.id";
	public static final String SELECT_COMPONENT_MATERIAL_TO_STATE = "from[" + REL_COMPONENT_MATERIAL + "].to.current";
	public static final String SELECT_COMPONENT_MATERIAL_TO_TYPE = "from[" + REL_COMPONENT_MATERIAL + "].to.type";
	public static final String SELECT_COMPONENT_SUBSTANCE_REL_ID_FROM_RM = "from[" +REL_COMPONENT_MATERIAL+ "].to.from[" +REL_COMPONENT_SUBSTANCE+ "].id";
	public static final String SELECT_PRODUCTFORM_FROM_PROCESS = "to[" +REL_PLANNED_FOR+ "].from.to[" +REL_OWNINGPRODUCTLINE+ "].from";
	public static final String SELECT_PRODUCTFORM_REL_ID_FROM_PROCESS = "to[" +REL_PLANNED_FOR+ "].from.to[" +REL_OWNINGPRODUCTLINE+ "].id";
	public static final String SELECT_PRODUCTFORM_FROM_FORM_PART = "to[" +REL_OWNINGPRODUCTLINE+ "].from.name";
	public static final String SELECT_PRODUCTFORM_REL_ID_FROM_FORM_PART = "to[" +REL_OWNINGPRODUCTLINE+ "].id";
	public static final String SELECT_GPS_ASSESSMENT_TASK_FROM_PROCESS = "to[" +REL_PLANNED_FOR+ "].from.to[" +REL_GPSASSESSMENTTASKINPUT+ "].from";
	public static final String SELECT_COMPONENT_SUBSTANCE_REL_ID_FROM_INTERNAL_MAT = "from[" +REL_COMPONENT_SUBSTANCE+ "].id";
	public static final String RELATIONSHIP_PGPLATFORMTOBUSINESSAREA = PropertyUtil.getSchemaProperty("relationship_pgPlatformToBusinessArea");
	public static final String RELATIONSHIP_PGPLATFORMTOPLATFORM = PropertyUtil.getSchemaProperty("relationship_pgPlatformToPlatform");
	public static final String RELATIONSHIP_PGPLATFORMTOCHASSIS = PropertyUtil.getSchemaProperty("relationship_pgPlatformToChassis");
	public static final String RELATIONSHIP_SUBCLASS = PropertyUtil.getSchemaProperty("relationship_Subclass");

	
	//Attribute
	public static final String ATTRIBUTE_RENDER_LANGUAGE = PropertyUtil.getSchemaProperty("attribute_RenderLanguage");
	public static final String ATTRIBUTE_SPEC_CATEGORY = PropertyUtil.getSchemaProperty("attribute_SpecificationCategory");
	public static final String ATTRIBUTE_REFERENCE_URI = PropertyUtil.getSchemaProperty("attribute_ReferenceURI");
	public static final String ATTRIBUTE_ISNGF_LOCKED = PropertyUtil.getSchemaProperty("attribute_pgIsFISLocked");  
	public static final String ATTRIBUTE_QUANTITY = PropertyUtil.getSchemaProperty("attribute_Quantity");
	public static final String ATTRIBUTE_FILL = PropertyUtil.getSchemaProperty("attribute_Fill");
	public static final String ATTRIBUTE_IS_TARGET_MATERIAL = PropertyUtil.getSchemaProperty("attribute_IsTargetMaterial");
	public static final String ATTRIBUTE_BASE_UNIT_OF_MEASURE = PropertyUtil.getSchemaProperty("attribute_pgBaseUnitOfMeasure");
	public static final String ATTRIBUTE_STRUCTURE_RELEASE_CRITERIA_REQ = PropertyUtil.getSchemaProperty("attribute_pgStructuredReleaseCriteriaRequired");
	public static final String ATTRIBUTE_REASON_FOR_CHANGE = PropertyUtil.getSchemaProperty("attribute_ReasonforChange");
	public static final String ATTRIBUTE_MANUFACTURE_STATUS = PropertyUtil.getSchemaProperty("attribute_pgLifeCycleStatus");
	public static final String ATTRIBUTE_EMAIL_ADDRESS = PropertyUtil.getSchemaProperty("attribute_EmailAddress");
	public static final String ATTRIBUTE_PG_EMPLOYEE = PropertyUtil.getSchemaProperty("attribute_pgEmployee");
	public static final String ATTRIBUTE_PG_ROLE = PropertyUtil.getSchemaProperty("attribute_pgRole");
	public static final String ATTRIBUTE_REFERENCE_SOURCE = PropertyUtil.getSchemaProperty("attribute_ReferenceSource");
	public static final String ATTRIBUTE_REFERENCE_IDENTIFIER = PropertyUtil.getSchemaProperty("attribute_ReferenceIdentifier");
	public static final String ATTRIBUTE_PG_GROUP_REFERENCE_URI = PropertyUtil.getSchemaProperty("attribute_pgFISReferenceGroupURI");
	public static final String ATTRIBUTE_PG_CONTRACT_GROUP_REFERENCE_URI = PropertyUtil.getSchemaProperty("attribute_pgFISContractorReferenceGroupURI");
	public static final String ATTRIBUTE_HIGHLY_RESTRICTED_SEC_CATEGORY = PropertyUtil.getSchemaProperty("attribute_pgHighlyRestrictedSecurityCategory");
	public static final String ATTRIBUTE_RESTRICTED_SEC_CATEGORY = PropertyUtil.getSchemaProperty("attribute_pgRestrictedSecurityCategory");
	public static final String ATTRIBUTE_PG_SPGS = PropertyUtil.getSchemaProperty("attribute_pgSPSG");
	public static final String ATTRIBUTE_PG_SECURITY_EMP_TYPE = PropertyUtil.getSchemaProperty("attribute_pgSecurityEmployeeType");
	public static final String ATTRIBUTE_PG_LIBRARY_CLASSIFICATION = PropertyUtil.getSchemaProperty("attribute_pgIPLibraryClassification");
	public static final String ATTRIBUTE_REFERENCE_FORMULATED_MATERIAL_URI = PropertyUtil.getSchemaProperty("attribute_ReferenceFormulatedMaterialURI");
	public static final String ATTRIBUTE_PG_ENG_PRODUCT_FORM = PropertyUtil.getSchemaProperty("attribute_pgEngProductForm");
	public static final String ATTRIBUTE_PG_APPLICATION = PropertyUtil.getSchemaProperty("attribute_pgApplication");
	
	public static final String SELECT_ATTRIBUTE_REFERENCE_FORMULATED_MATERIAL_URI = "attribute[" + ATTRIBUTE_REFERENCE_FORMULATED_MATERIAL_URI + "]";
	public static final String SELECT_ATTRIBUTE_RENDER_LANGUAGE = "attribute[" + ATTRIBUTE_RENDER_LANGUAGE + "]";
	public static final String SELECT_ATTRIBUTE_EMAIL_ADDRESS = "attribute[" + ATTRIBUTE_EMAIL_ADDRESS + "]";
	public static final String SELECT_ATTRIBUTE_SPEC_CATEGORY = "attribute[" + ATTRIBUTE_SPEC_CATEGORY + "]";
	public static final String SELECT_ATTRIBUTE_REFERENCE_URI = "attribute[" + ATTRIBUTE_REFERENCE_URI + "]";
	public static final String SELECT_ATTRIBUTE_REFERENCE_URI_FOR_ALL_REVS = "revisions.attribute[" + ATTRIBUTE_REFERENCE_URI + "]";
	public static final String SELECT_ATTRIBUTE_REFERENCE_URI_FOR_PREV_REVS = "previous.attribute[" + ATTRIBUTE_REFERENCE_URI + "]";
	public static final String SELECT_ATTRIBUTE_REFERENCE_IDENTIFIER = "attribute[" + ATTRIBUTE_REFERENCE_IDENTIFIER + "]";
	public static final String SELECT_ATTRIBUTE_BASE_UNIT_OF_MEASURE = "attribute[" + ATTRIBUTE_BASE_UNIT_OF_MEASURE + "]";
	public static final String SELECT_ATTRIBUTE_STRUCTURE_RELEASE_CRITERIA_REQ = "attribute[" + ATTRIBUTE_STRUCTURE_RELEASE_CRITERIA_REQ + "]";
	public static final String SELECT_ATTRIBUTE_REASON_FOR_CHANGE = "attribute[" + ATTRIBUTE_REASON_FOR_CHANGE + "]";
	public static final String SELECT_ATTRIBUTE_MANUFACTURE_STATUS = "attribute[" + ATTRIBUTE_MANUFACTURE_STATUS + "]";
	public static final String SELECT_ATTRIBUTE_ISNGF_LOCKED = "attribute[" + ATTRIBUTE_ISNGF_LOCKED + "]";
	public static final String SELECT_COMPONENT_SUBSTANCE_COMPOSITION_FROM_INTERNAL_MAT = "from[" +REL_COMPONENT_SUBSTANCE+ "].attribute["+ATTRIBUTE_QUANTITY+"]";
	public static final String SELECT_SIGNUPFORM_EMAIL_FROM_PERSON = "to[" +REL_SIGNUPFORM+ "].from.attribute["+ATTRIBUTE_EMAIL_ADDRESS+"]";
	public static final String SELECT_SIGNUPFORM_EMPLOYEE_TYPE_FROM_PERSON = "to["+ REL_SIGNUPFORM +"].from.attribute["+ATTRIBUTE_PG_EMPLOYEE+"]";
	public static final String SELECT_SIGNUPFORM_ROLES_FROM_PERSON = "to["+ REL_SIGNUPFORM +"].from.attribute["+ATTRIBUTE_PG_ROLE+"]";
	public static final String SELECT_PREV_SIGNUPFORM_ROLES_FROM_PERSON = "to["+ REL_SIGNUPFORM +"].from.previous.attribute["+ATTRIBUTE_PG_ROLE+"]";
	public static final String SELECT_PERSON_EMAIL_FROM_SECURITY_CLASSES = "from["+REL_CLASSIFICATION_LICENSE+"].to.from["+REL_LICENSED_PEOPLE+"].to.attribute["+ATTRIBUTE_EMAIL_ADDRESS+"]";
	public static final String SELECT_PERSON_EMP_TYPE_FROM_SECURITY_CLASSES = "from["+REL_CLASSIFICATION_LICENSE+"].to.from["+REL_LICENSED_PEOPLE+"].to.attribute["+ATTRIBUTE_PG_SECURITY_EMP_TYPE+"]";
	public static final String SELECT_GROUP_URI_OF_SECURITY_CLASSES = "to["+REL_LICENSED_PEOPLE+"].from.to["+REL_CLASSIFICATION_LICENSE+"].from.attribute["+ATTRIBUTE_PG_GROUP_REFERENCE_URI+"]";
	public static final String SELECT_CONTRACT_GROUP_URI_OF_SECURITY_CLASSES = "to["+REL_LICENSED_PEOPLE+"].from.to["+REL_CLASSIFICATION_LICENSE+"].from.attribute["+ATTRIBUTE_PG_CONTRACT_GROUP_REFERENCE_URI+"]";
	public static final String SELECT_NAME_OF_SECURITY_CLASSES = "to["+REL_LICENSED_PEOPLE+"].from.to["+REL_CLASSIFICATION_LICENSE+"].from.name";
	public static final String SELECT_SIGNUPFORM_PREV_REV_HIGH_RESTRICTED_SEC_CLASSES = "to["+ REL_SIGNUPFORM +"].from.previous.attribute["+ ATTRIBUTE_HIGHLY_RESTRICTED_SEC_CATEGORY +"]";
	public static final String SELECT_SIGNUPFORM_PREV_REV_RESTRICTED_SEC_CLASSES = "to["+ REL_SIGNUPFORM +"].from.previous.attribute["+ ATTRIBUTE_RESTRICTED_SEC_CATEGORY +"]";
	public static final String SELECT_SIGNUPFORM_PREV_REV_SPGS_CLASSES = "to["+ REL_SIGNUPFORM +"].from.previous.attribute["+ ATTRIBUTE_PG_SPGS +"]";
	public static final String SELECT_ATTRIBUTE_PG_GROUP_REFERENCE_URI = "attribute["+ ATTRIBUTE_PG_GROUP_REFERENCE_URI +"]";
	public static final String SELECT_ATTRIBUTE_PG_CONTRACT_GROUP_REFERENCE_URI = "attribute["+ ATTRIBUTE_PG_CONTRACT_GROUP_REFERENCE_URI +"]";
	public static final String SELECT_ATTRIBUTE_PG_SECURITY_EMP_TYPE = "attribute["+ ATTRIBUTE_PG_SECURITY_EMP_TYPE +"]";
	public static final String SELECT_ATTRIBUTE_PG_LIBRARY_CLASSIFICATION_FROM_IP_CLASSES = "to["+RELATIONSHIP_SUBCLASS+"].from.attribute["+ATTRIBUTE_PG_LIBRARY_CLASSIFICATION+"]";
	public static final String SELECT_ATTRIBUTE_PG_LIBRARY_CLASSIFICATION_FROM_PERSON ="to[" + PGFISWSConstants.REL_LICENSED_PEOPLE + "].from.to["
	+ PGFISWSConstants.REL_CLASSIFICATION_LICENSE
	+ "].from.to["+RELATIONSHIP_SUBCLASS+"].from.attribute["+ATTRIBUTE_PG_LIBRARY_CLASSIFICATION+"]";
	public static final String SELECT_SIGNUPFORM_PREV_REV_APPS = "to["+ REL_SIGNUPFORM +"].from.previous.attribute["+ ATTRIBUTE_PG_APPLICATION +"]";
	public static final String SELECT_SIGNUPFORM_CURRENT_REV_APPS = "to["+ REL_SIGNUPFORM +"].from.attribute["+ ATTRIBUTE_PG_APPLICATION +"]";
	
	public static final String SELECT_PLANNED_FOR_TO_URI = "from[" + REL_PLANNED_FOR + "].to."+SELECT_ATTRIBUTE_REFERENCE_URI;
	public static final String SELECT_LIBRARY_NAME= "to["+RELATIONSHIP_SUBCLASS+"].from.name";
	//Vault
	public static final String VAULT_PRODUCTION = PropertyUtil.getSchemaProperty("vault_eServiceProduction");
	
	//General constants
	public static final String MANDETORY_DETAILS_MISSING = "Mandatory information(Physical_Id) is empty or null";
	public static final String MANDETORY_CLOUD_DETAILS_MISSING = "Mandatory information(Cloud Object Id) is empty or null";
	public static final String MANDETORY_DETAILS_MISSING_GCAS = "Mandatory information(ObjectEvent or ObjectType) is empty or null";
	public static final String MAT_REVISED_RELEASED_MSG = "Material Revised and Released successfully";
	public static final String NO_OBJECT_FOUND_MSG = "No Object found in enovia, With name <NAME> or with Reference URI <Reference URI>";
	public static final String NO_NTERNAL_OBJECT_FOUND_MSG = "Object Revised Successfully !! But Internal Material is not added to it, Hence Substance is not updated";
	public static final String NO_PLACEHOLDER_SUBSTANCE_FOUND_MSG = "Object Revised Successfully !! But placeHolder substance <SUB_NAME> not found in enovia, Hence Substance is not updated";
	public static final String MAT_UNLOCKED_MSG = "Material UnLocked Successfully";
	public static final String BAD_DATA_MSG = "Incorrect data!! Formulation Process is not having Formulation Part";

	public static final String STRING_MESSAGE = "Message";
	public static final String JSON_OUTPUT_KEY_ERROR = "error";
	public static final String JSON_OUTPUT_KEY_TRACE = "trace";
	
	public static final String KEY_COMMA_SEPARATOR = ",";
	public static final String KEY_DISPLAY_TYPE  = "displayType";
	public static final String KEY_OBJECT_TYPE  = "objectType";
	public static final String KEY_DISPLAY_NAME  = "displayName";
	public static final String KEY_VALUE = "value";
	public static final String MAX_STRING_LENGTH = "org.apache.johnzon.max-string-length";
	public static final String VALUE_KILOBYTES = "262144";
	public static final String KEY_OBJECT_ID = "objectId";
	public static final String KEY_OBJECT_SELECTS = "ObjectSelects";
	public static final String VAULT_ESERVICE_PRODUCTION = PropertyUtil.getSchemaProperty("vault_eServiceProduction");
	public static final String POLICY_ECPART = PropertyUtil.getSchemaProperty("policy_ECPart");
	//Update below entries with cloud details
	public static final String STR_BLANK = "";
	public static String FIS_CLOUD_TENANT = STR_BLANK;
	public static String FIS_CLOUD_CLUSTER = STR_BLANK;
	public static String FIS_CLOUD_DOMAIN = STR_BLANK;
	public static String FIS_CLOUD_DATACENTER = STR_BLANK;
	public static String FIS_CLOUD_USERNAME = STR_BLANK;
	public static String FIS_CLOUD_PASSWORD = STR_BLANK;
	public static String FIS_CLOUD_REQUIRECAA = STR_BLANK;
	
	//Entry to use clmagent and clmpassword for cloud authentication
	public static String FIS_CLOUD_CLM_AGENT_ID = STR_BLANK;
	public static String FIS_CLOUD_CLM_AGENT_PASSWORD = STR_BLANK;
	
	

	//Update below entries with On Prem instance details
	public static String ONPREM_SPACE_URL = STR_BLANK;
	public static String ONPREM_ENGINUITY_URL = STR_BLANK;
	public static String ONPREM_PASSPORT_URL = STR_BLANK;
	public static String ONPREM_PASSPORT_USERNAME = STR_BLANK;
	public static String ONPREM_PASSPORT_PASSWORD = STR_BLANK;
	public static String ONPREM_ADMIN_USERNAME = STR_BLANK;
	public static String ONPREM_BASIC_AUTH = STR_BLANK;
	
	public static String URL_RECIPE_PRIVATE = STR_BLANK;
	
	public static String SUBSTANCE_GET_URL = STR_BLANK;
	public static String RAWMATERIAL_GET_URL = STR_BLANK;
	public static String FORMULA_GET_URL = STR_BLANK;
	
	public static final String JSON_TAG_HAS_MIXURE_RATIO = "hasMixtureRatio";
	public static final String JSON_TAG_RATIO_TARGET = "ratioTarget";
	public static final String JSON_TAG_REST_OF_MIXURE = "restOfMixture";
	public static final String TYPE_SOURCED = "Sourced";
	public static final String STATE_ARCHIVED = "Archived";
	public static final String STATE_APPROVED = "Approved"; 
	public static final String STATE_RELEASED = "Release";
	public static final String STATE_EXTERNALLY_MANAGED = "Externally Managed";
	public static final String STATE_PUBLICHED = "Published";
	public static final String JSON_TAG_LIFECYCLE_STATE = "lifecycleState";
	public static final String JSON_TAG_HAS_MAT_TYPE = "hasMaterialType";
	public static final String JSON_TAG_HAS_CONSTITUENCY = "hasConstituency";
	public static final String JSON_TAG_PREF_LABEL = "prefLabel";
	public static final String JSON_TAG_LABEL = "label";
	public static final String JSON_TAG_HAS_ASSIGNED_IDENTIFIER = "hasAssignedIdentifier";
	public static final String JSON_TAG_ENTERPRISE_IDENTIFIER = "Enterprise Identifier";
	public static final String JSON_TAG_IDENTIFIER = "identifier";
	public static final String JSON_TAG_IDENTIFIER_TYPE = "hasIdentifierType";
	
	public static final String CONST_REVISE_FOR_FIS = "Revise for FIS";
	public static final String JSON_TAG_ENTERPRISE_ID = "enterpriseId";
	public static final String JSON_TAG_PHYSICAL_ID = "physicalId";
	public static final String JSON_TAG_PATCH_TO_CLOUD = "patchtocloud";
	public static final String CONST_EMPLOYEE = "Employee";
	public static final String CONST_NON_EMPLOYEE = "Non-Emp";
	public static final String CONST__ROLE_ENGINUITY = "Enginuity";
	public static final String CONST_ROLE_FORMULATOR = "Formulator";
	public static final String CONST_ROLE_MATERIAL_ADMINISTRATOR = "Material Administrator";
	public static final String CONST_STATE_ACTIVE = "Active";
	public static final String JSON_TAG_DATA = "data";
	public static final String JSON_TAG_INVITATION_MSG = "invitationCustomMessage";
	public static final String CONST_FORMULA_INNOVATION_SUIT= "Formula Innovation Suite";
	
	public static final String JSON_TAG_GROUPS = "groups";
	public static final String JSON_TAG_SECURE_COLLECTIONS = "securecollections";
	public static final String JSON_TAG_TITLE = "title";
	public static final String JSON_TAG_DESC = "description";
	public static final String JSON_TAG_MEMBERS = "members";
	public static final String JSON_TAG_SHARING = "sharing";
	public static final String JSON_TAG_VISIBILITY = "visibility";
	public static final String JSON_TAG_NAME = "name";
	public static final String JSON_TAG_RESPONSIBILITIES = "responsibilities";
	public static final String JSON_TAG_OPERATION = "op";
	public static final String JSON_TAG_FIELD = "field";
	public static final String JSON_TAG_VALUE = "value";
	public static final String JSON_TAG_USER_GROUPS = "usergroups";
	public static final String ATTRIBUTE_RELEASEPHASE = PropertyUtil.getSchemaProperty("attribute_ReleasePhase");
	public static final String SELECT_ATTRIBUTE_RELEASEPHASE = "attribute["+ATTRIBUTE_RELEASEPHASE+"]";	
	
	public static final String RELATIONSHIP_FORMULATION_PROCESS = PropertyUtil.getSchemaProperty("relationship_FormulationProcess");
	public static final String  JSON_TAG_PREDECESSOR= "predecessor";
	public static final String JSON_TAG_ID = "id";
	public static final String CONST_TRUE = "TRUE";
	public static final String TYPE_COSMETIC_FORMULATION = PropertyUtil.getSchemaProperty("type_CosmeticFormulation");
    public static final String RELATIONSHIP_FORMULATION_PROPAGATE = PropertyUtil.getSchemaProperty("relationship_FormulationPropagate");
    
    public static final String CONST_FROM_REL_PLANNEDFOR_TO = "from["+REL_PLANNED_FOR+"].to.";
    public static final String STATE_RELEASE = "Release";
	public static final String STATE_OBSOLETE = "Obsolete";
	public static final String STATE_COMPLETE = "Complete";
	public static final String FORMULA_EXT_FAILED_FROM_ONPREM  = "FAILED: Error while extracting Formula from On Prem. Please contact administrator";
	public static final String FORMULA_PROCESS_NOT_HAVING_FORM_PART_MESSAGE = "Formulation Process is not having Formulation Part";
	public static final String OBJECT_REFERENCE_URI_BLANK_MESSAGE = "Object Reference URI(uuid) is blank. Please pass proper value";
	public static final String OBJECT_NOT_FOUND_MESSAGE = "No Object found in On-Prem, for this Reference URI - ";
	public static final String PROPERTY_UPDATED_SUCCESSFULLY = "Phys Chem Properties Updated Successfully";
	public static final String CLOUD_OBJECT_UUID_BLANK = "Cloud Object UUID is blank. Please pass proper value";
	public static final String STRING_KEY_FORMULA_UUID = "Formula UUID";
	public static final String STRING_KEY_PROPERTIES = "Properties";
	public static final String STRING_KEY_PRODUCT_FORM = "Product Form";
	public static final String OBJECT_PHYSICAL_ID = "OBJECT_PHYSICAL_ID";
	public static final String OBJECT_TYPE = "OBJECT_TYPE";
	public static final String OBJECT_LEVEL = "OBJECT_LEVEL";
	public static final String STRING_EMPTY = "Empty";
	public static final String RM_NOT_FOUND_IN_FIS_MSG = "Import of <<Formula_Name>> has failed due to materials not existing in FIS (Obsolete and Legacy CSS materials are not in FIS).\nPlease review formula directly in Enovia and either correct it there, or recreate in FIS using currently valid materials.";
	public static final String EVENT_GENERATION_SUCCESS_MSG = "Event generation is successfully completed for Formula.";
	public static final String FORMULATED_MAT_DETAILS_MISSING_MSG =  "Formulated Material details not found for physicalid : ";
	public static final String FORM_PROCESS_API_SUPPORT_MSG =  "This API is Supported only for Formulation Process Business Type.";
	public static final String ENTERPRISE_ID_KEY = "enterpriseId";
	public static final String IMPORTED_IDENTIFIER_KEY = "importedIdentifier";
	public static final String CONSTANT_REL_FROM_COSMETICFORMULATION_TO_FORMUTATIONPROCESS = "from["+PGFISWSConstants.RELATIONSHIP_FORMULATION_PROCESS+"].to.to["+PGFISWSConstants.RELATIONSHIP_FORMULATION_PROCESS+"].from.";
	public static final String STRING_STATUS = "status";
	public static final String STATUS_SUCCESS = "success";
	public static final String STATUS_ERROR = "error";
	public static final String REQUEST_FOR = "REQUEST_FOR";
	public static final String REVISE_AND_UPDATE_API_VALUE = "REVISE_AND_UPDATE";
	public static final String UPDATE_API_VALUE = "UPDATE";
	public static final String MATERIAL_ID_KEY = "MATERIAL_ID";
	public static final String IS_RAW_MATERIAL_KEY = "IsNGFObjectLocked";
	public static final String SUCCESS_MESSAGE_ON_MAT_OBSOLETION = "Material obsoleted Successfully";
	public static final String STATE_ACTIVE = "Active";
	public static final String STRING_ISACTIVE = "isActive";
	public static final String STRING_EXTERNAL_ID = "externalId";
	public static final String STRING_PREF_LABEL = "prefLabel";
	public static final String STRING_REFERENCE_ID = "id";
	public static final String STRING_PRODUCT_CLASSIFICATION = "Product Classifications";
	public static final String PERSON_NOT_FOUND = "No Person Found in enovia with emailId/Name as ";
	public static final String CONST_RESTRICTED = "Restricted";
	public static final String CONST_HIGHLY_RESTRICTED = "Highly Restricted";
	public static final String CONST_NONE = "None";
	public static final String CONST_NON_EMP = "Non-Emp";
	public static final String CONST_NAME_NON_EMP = "Non Emp";
	public static final String CONST_EBP = "EBP";
	
	public static final String CONST_BIO_EXPERIMENT_SERVICE = "bioexperiment";
	public static final String CONST_RECIPE_CLASSIFICATION_URI = "/resources/v1/spr/dictionary/recipeclassifications";
	public static final String CONST_SINGLE_PIECE_DESC_URI = "/resources/v1/spr/dictionary/singlepiecedescriptions";
	public static final String JSON_FIS_ENTERPRISE_IDENTIIFIER = "dsmatdata:Enterprise_Identifier_IDT";
	public static final String JSON_TAG_BODY= "body";
	public static final String JSON_TAG_OFFLABEL = "offLabel";
	public static final String CONST_FIS = "Formula Innovation Suite";
	public static final String CONST_ALL_REVISIONS = "revisions";
	public static final String CONST_SPECIAL_PROJECTS= "Special Projects";
	public static final String STR_COMMA = ",";
	public static final String STR_INVALID_SECURE_COLLECTION = "INVALID SECURE COLLECTION";
	
	
	/**
	 * Constructor.
	 *
	 * @param context the eMatrix <code>Context</code> object
	 * @param args    holds no arguments
	 * @throws Exception if the operation fails
	 */
	private PGFISWSConstants(Context context, String[] args) {

	}
	
	/**
	 * This is a utility method to get the value from Page file property
	 * @param context
	 * @param strPageName : Page File name
	 * @param strKey : Property Key
	 * @return : returns value of the given property key
	 * @throws Exception
	 */
	public static void loadDataFromProperties(Context context, String strPageName)throws Exception {
		Properties properties = new Properties();
		try{
			String isPageExists	= MqlUtil.mqlCommand(context, "list page $1", strPageName);
			String strProperties= UIUtil.isNotNullAndNotEmpty(isPageExists) ? MqlUtil.mqlCommand(context, "print page $1 select content dump", strPageName) : "";
			if(UIUtil.isNotNullAndNotEmpty(strProperties)){
				properties.load(new StringReader(strProperties));
				FIS_CLOUD_TENANT = properties.getProperty("pgFIS.CASAuthentication.cloud.tenant");
				FIS_CLOUD_CLUSTER = properties.getProperty("pgFIS.CASAuthentication.cloud.cluster");
				FIS_CLOUD_DOMAIN = properties.getProperty("pgFIS.CASAuthentication.cloud.domain");
				FIS_CLOUD_DATACENTER = properties.getProperty("pgFIS.CASAuthentication.cloud.datacenter");
				FIS_CLOUD_USERNAME = properties.getProperty("pgFIS.CASAuthentication.cloud.username");
				FIS_CLOUD_PASSWORD = properties.getProperty("pgFIS.CASAuthentication.cloud.password");
				
				FIS_CLOUD_CLM_AGENT_ID = properties.getProperty("pgFIS.CASAuthentication.cloud.clmAgent");
				FIS_CLOUD_CLM_AGENT_PASSWORD = properties.getProperty("pgFIS.CASAuthentication.cloud.clmPassword");
				
				ONPREM_SPACE_URL = properties.getProperty("pgFIS.CASAuthentication.onprem.spaceurl");
				ONPREM_ENGINUITY_URL= properties.getProperty("pgFIS.CASAuthentication.onprem.enginuityurl");
				ONPREM_PASSPORT_URL= properties.getProperty("pgFIS.CASAuthentication.onprem.passporturl");
				ONPREM_PASSPORT_USERNAME= properties.getProperty("pgFIS.CASAuthentication.onprem.username");
				ONPREM_PASSPORT_PASSWORD= properties.getProperty("pgFIS.CASAuthentication.onprem.password");
				ONPREM_ADMIN_USERNAME= properties.getProperty("pgFIS.CASAuthentication.onprem.adminusername");
				
				ONPREM_BASIC_AUTH= properties.getProperty("pgFIS.CASAuthentication.onprem.basicauth");
				
				URL_RECIPE_PRIVATE = properties.getProperty("pgFIS.cloud.formulaload.webservicesurl");
				
				SUBSTANCE_GET_URL= properties.getProperty("pgFIS.substance.onprem.get_api_url");
				RAWMATERIAL_GET_URL= properties.getProperty("pgFIS.rawmaterial.onprem.get_api_url");
				FORMULA_GET_URL= properties.getProperty("pgFIS.fomrulation.onprem.get_api_url");
				
				FIS_CLOUD_REQUIRECAA = properties.getProperty("pgFIS.CASAuthentication.cloud.requireCAA");
			}
		}catch(Exception ex){
			throw ex;
		}
	}

}

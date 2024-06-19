package com.pg.dsm.preference.enumeration;

import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;

public class PreferenceConstants {
    public enum Basic {
        EXPLORATION_TYPES("explorationTypes"),
        TECH_SPEC_TYPES("techSpecTypes"),
        PACKAGING_TYPES("packagingTypes"),
        PRODUCT_TYPES("productTypes"),
        RAW_MATERIAL_TYPES("rawMaterialTypes"),
		//Added by DSM for 22x CW-06 for Requirement #50186,50187 - START
        MEP_TYPES("MEPTypes"),
        SEP_TYPES("SEPTypes"),
		//Added by DSM for 22x CW-06 for Requirement #50186,50187 - END
        PRODUCT_COMPLIANCE("productCompliance"),
        REPORTED_FUNCTION("reportedFunctions"),
        MATERIAL_FUNCTION("materialFunctions"),
        COMPONENT_TYPES("componentTypes"),
        MATERIAL_TYPES("materialTypes"),
        UNIT_OF_MEASURES("unitOfMeasures"),
        CLASSES("classes"),
        BUSINESS_AREA("businessArea"),
        RELEASE_CRITERIA("releaseCriteria"),
        SEGMENT("segment"),
        CHOICE("choice"),
        DISPLAY_CHOICE("displayChoice"),

        SECURITY_DATA("securityData"),
        STATE_ACTIVE("Active"),
        CHANGE_ACTION_DATA("changeActionData"),
        TEMPLATE_SPECIFIC_DATA("templateSpecificData"),
        SHARING_TEMPLATE_MEMBERS_DATA("sharingTemplateMembersData"),
        SHARING_MEMBERS_DATA("sharingMembersData"),
        PLANTS_DATA("plantData"),
        PACKAGING_DATA("packagingData"),
        PRODUCT_DATA("productData"),
        RAW_MATERIAL_DATA("rawMaterialData"),
        TECHNICAL_SPECIFICATION_DATA("technicalSpecificationData"),
        EXPLORATION_DATA("explorationData"),
		//Added by DSM for 22x CW-06 for Requirement #50186,50187 - START
        MANUFACTURING_EQUIVALENT_DATA("manufacturingEquivalentData"),
        SUPPLIER_EQUIVALENT_DATA("supplierEquivalentData"),
		//Added by DSM for 22x CW-06 for Requirement #50186,50187 - END
        TEMPLATE_NAME("templateName"),
        TEMPLATE_AUTO_NAME("templateAutoName"),
        CONTEXT_USER_NAME("contextUserName"),
        CONTEXT_USER_ID("contextUserID"),
        PART_CATEGORY("partCategory"),
        IS_MIGRATED("isMigrated"),
        PLANTS("plants"),
        SHARING_MEMBERS("sharingMembers"),
        TEMPLATE_SHARING_MEMBERS("templateSharingMembers"),
        ASCENDING("ascending"),
        OUTPUT("output"),
        YES("Yes"),
        NO("No"),
        USER_PLANT_PREFERENCE_TABLE("UserPlantPreference"),

        IS_VALID("isvalid"),
        MESSAGE("message"),

        FIS("FIS"),

        FILTER_ALL("All"),
        FILTER_PACKAGING("Packaging"),
        FILTER_PRODUCT("Product"),
        FILTER_RAW_MATERIAL("Raw Material"),
        FILTER_TECHNICAL_SPECIFICATION("Technical Specification"),

        DSM_SHARING_MEMBER_ACCESSES_FOR_EBP("read,checkout,create,show"),
        DSM_SHARING_MEMBER_ACCESSES_FOR_CA_INFORMEDUSER("read,fromconnect,fromdisconnect,show"),
        IRM_SHARING_MEMBER_ACCESSES_FOR_EBP("read,modify,checkout,checkin,lock,unlock,revise,fromconnect,toconnect,fromdisconnect,todisconnect,show,reserve,unreserve,changesov"),

        FULL("Full"),
        COPY("copy"),
        REVISE("revise"),
        CREATE("create"),
        USE_PREFERENCE("UserPreference"),

        HAS_RELATED_BUSINESS_AREA("from[" + pgV3Constants.RELATIONSHIP_PGDOCUMENTTOBUSINESSAREA + "]"),
        RELATED_BUSINESS_AREA_ID("from[" + pgV3Constants.RELATIONSHIP_PGDOCUMENTTOBUSINESSAREA + "].to.id"),
        RELATED_BUSINESS_AREA_CONNECTION_ID("from[" + pgV3Constants.RELATIONSHIP_PGDOCUMENTTOBUSINESSAREA + "].id"),

        HAS_RELATED_PRODUCT_CATEGORY_PLATFORM("from[" + pgV3Constants.RELATIONSHIP_PGDOCUMENTTOPLATFORM + "]"),
        RELATED_PRODUCT_CATEGORY_PLATFORM_ID("from[" + pgV3Constants.RELATIONSHIP_PGDOCUMENTTOPLATFORM + "].to.id"),
        RELATED_PRODUCT_CATEGORY_PLATFORM_CONNECTION_ID("from[" + pgV3Constants.RELATIONSHIP_PGDOCUMENTTOPLATFORM + "].id"),
        HAS_PRIMARY_ORG("from[" + pgV3Constants.RELATIONSHIP_PGPRIMARYORGANIZATION + "]"),
        RELATED_PRIMARY_ORG("from[" + pgV3Constants.RELATIONSHIP_PGPRIMARYORGANIZATION + "].to.id"),
        RELATED_PRIMARY_ORG_CONNECTION_ID("from[" + pgV3Constants.RELATIONSHIP_PGPRIMARYORGANIZATION + "].id"),
        RELATED_PRODUCT_CATEGORY_PLATFORM("from[" + pgV3Constants.RELATIONSHIP_PGDOCUMENTTOPLATFORM + "].to.physicalid"),
        RELATED_SEGMENT_CONNECTION_ID("from[" + pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLISEGMENT + "].id"),
        RELATED_SEGMENT_ID("from[" + pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLISEGMENT + "].to.id"),
        RELATED_SEGMENT_NAME("from[" + pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLISEGMENT + "].to.name"),
        HAS_RELATED_SEGMENT("from[" + pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLISEGMENT + "]"),
        HAS_FORMULATION_PART("to[" + pgV3Constants.RELATIONSHIP_PLANNEDFOR + "]"),
        SELECT_FORMULATION_PART_RELEASE_PHASE("to[" + pgV3Constants.RELATIONSHIP_PLANNEDFOR + "].from." + pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE),
        SELECT_FORMULATION_ID("from[" + pgV3Constants.RELATIONSHIP_PLANNEDFOR + "].to.id"),
        HAS_FORMULATION("to[" + pgV3Constants.RELATIONSHIP_FORMULATIONPROPAGATE + "]"),
        SELECT_FORMULATION_RELEASE_PHASE("to[" + pgV3Constants.RELATIONSHIP_FORMULATIONPROPAGATE + "].from." + pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE),

        SELECT_FORMULATION_PART_ID("from[" + pgV3Constants.RELATIONSHIP_FORMULATIONPROPAGATE + "].to.id"),
        SELECT_FORMULATION_PART_TYPE("from[" + pgV3Constants.RELATIONSHIP_FORMULATIONPROPAGATE + "].to.type"),
        COSMETIC_FORMULATION_HAS_FORMULATION_PART("from[" + pgV3Constants.RELATIONSHIP_FORMULATIONPROPAGATE + "]"),
        SELECT_FORMULATION_PROCESS_ID("to[" + pgV3Constants.RELATIONSHIP_FORMULATIONPROCESS + "].from.id"),
        SELECT_FORMULATION_PROCESS_TYPE("to[" + pgV3Constants.RELATIONSHIP_FORMULATIONPROCESS + "].from.type"),

        COSMETIC_FORMULATION_HAS_FORMULATION_PROCESS("to[" + pgV3Constants.RELATIONSHIP_FORMULATIONPROCESS + "]"),

        ALL("all"),

        FAST_TRACK_CHANGE("Fast track Change"),
        USER_COPY_PREFERENCE_TABLE("UserCopyPreference"),
        PRODUCT("PRD"),
        PACKAGING("PKG"),
        RAW_MATERIAL("RM"),
        TECHNICAL_SPEC("TS"),
        Exploration("EXP"),
		//Added by DSM for 22x CW-06 for Requirement #50186,50187 - START
        ManufacturingEquivalent("MEP"),
        SupplierEquivalent("SEP"),
		//Added by DSM for 22x CW-06 for Requirement #50186,50187 - END
        USER_PREFERENCE_CONFIG_PAGE("pgUserPreferenceConfig"),

        PHYSICAL_ID("physicalid"),
        PRODUCT_CATEGORY_PLATFORM("Product Category Platform"),

        JPO_PICKLIST("pgPLPicklist"),
        METHOD_PICKLIST_SUBSET_RANGE("getPicklistSubsetRange"),
        HYPOTHETICAL("Hypothetical"),
        KEY_APP("APP"),
        PROMOTE_CONNECTED_OBJECT("Promote Connected Object"),
        APPROVAL("Approval"),
        DI_PREFERRED_PART_TYPE_LABEL_NAME("Part Type"),
        PACKAGING_COMPONENT_TYPE_LABEL_NAME("Packaging Component Type"),
        BASE_UNIT_OF_MEASURE_LABEL_NAME("Base Unit of Measure"),
        DI_PREFERRED_PHASE_LABEL_NAME("Phase"),
        DI_PREFERRED_PART_TYPE_LABEL_KEY("DIPreferredPartTypeLabel"),
        DI_PREFERRED_PHASE_LABEL_KEY("DIPreferredPhaseLabel"),
        DI_PREFERRED_MANUFACTURING_STATUS_LABEL_KEY("DIPreferredManufacturingStatusLabel"),
        DI_PREFERRED_RELEASE_CRITERIA_STATUS_LABEL_KEY("DIPreferredReleaseCriteriaStatusLabel"),
        DI_PREFERRED_PACKAGING_MATERIAL_TYPE_LABEL_KEY("DIPreferredPackagingMaterialTypeLabel"),
        DI_PREFERRED_SEGMENT_LABEL_KEY("DIPreferredSegmentLabel"),
        DI_PREFERRED_CLASS_LABEL_KEY("DIPreferredClassLabel"),
        DI_PREFERRED_REPORTED_FUNCTION_LABEL_KEY("DIPreferredReportedFunctionLabel"),
        DI_PREFERRED_PART_TYPE_FIELD_ID("DIPreferredPartTypeLabel"),
        DI_PREFERRED_PHASE_FIELD_ID("DIPreferredPhaseLabel"),
        DI_PREFERRED_MANUFACTURING_STATUS_FIELD_ID("MaturityStatusSelectedValue"),
        DI_PREFERRED_RELEASE_CRITERIA_STATUS_FIELD_ID("ReleaseCriteriaReqSelectedValue"),
        DI_PREFERRED_PACKAGING_MATERIAL_TYPE_FIELD_ID("PackagingMaterialTypeSelectedValue"),
        DI_PREFERRED_SEGMENT_FIELD_ID("SegmentSelectedValue"),
        DI_PREFERRED_CLASS_FIELD_ID("ClassSelectedValue"),
        DI_PREFERRED_REPORTED_FUNCTION_FIELD_ID("ReportedFunctionSelectedValue"),
        DI_PREFERRED_PART_TYPE("DIPreferredPartType"),
        DI_PREFERRED_PHASE("DIPreferredPhase"),
        DI_PREFERRED_MANUFACTURING_STATUS("DIPreferredManufacturingStatus"),
        DI_PREFERRED_RELEASE_CRITERIA_STATUS("DIPreferredReleaseCriteriaStatus"),
        DI_PREFERRED_PACKAGING_MATERIAL_TYPE("DIPreferredPackagingMaterialType"),
        DI_PREFERRED_SEGMENT("DIPreferredSegment"),
        DI_PREFERRED_CLASS("DIPreferredClass"),
        DI_PREFERRED_REPORTED_FUNCTION("DIPreferredReportedFunction"),
        DSM_USER_PREFERENCE_CONFIG_OBJECT_NAME("pgDSMUserPreferencesConfig"),
        BUSINESS_USE("BusinessUse"),
        HIGHLY_RESTRICTED("HighlyRestricted"),
        CHANGE_TEMPLATE_NAME("ChangeTemplateName"),
        CHANGE_TEMPLATE_OID("ChangeTemplateOID"),
        PRIMARY_ORG_NAME("PrimaryOrgName"),
        PRIMARY_ORG_OID("PrimaryOrgOID"),
        PRIMARY_ORGANIZATION("PrimaryOrganization"),
        ROUTE_TEMPLATE_PRIMARY_ORG_NAME("RouteTemplatePrimaryOrgName"),
        ROUTE_TEMPLATE_PRIMARY_ORG_OID("RouteTemplatePrimaryOrgOID"),
        ROUTE_TEMPLATE_IN_WORK_NAME("RouteTemplateInWorkName"),
        ROUTE_TEMPLATE_IN_WORK_OID("RouteTemplateInWorkOID"),
        ROUTE_TEMPLATE_IN_APPROVAL_NAME("RouteTemplateInApprovalName"),
        ROUTE_TEMPLATE_IN_APPROVAL_OID("RouteTemplateInApprovalOID"),
        BUSINESS_USE_CLASS("BusinessUseClass"),
        HIGHLY_RESTRICTED_CLASS("HighlyRestrictedClass"),
        IRM_PREFERRED_TITLE("IRMPreferredTitle"),
        IRM_PREFERRED_DESCRIPTION("IRMPreferredDescription"),
        IRM_PREFERRED_POLICY("IRMPreferredPolicy"),
        IRM_PREFERRED_REGION_NAME("IRMPreferredRegionName"),
        IRM_PREFERRED_REGION_OID("IRMPreferredRegionOID"),
        IRM_PREFERRED_CLASSIFICATION("IRMPreferredClassification"),
        SECURITY_CATEGORY_CLASSIFICATION_NAME("SecurityCategoryClassificationName"),
        SECURITY_CATEGORY_CLASSIFICATION_OID("SecurityCategoryClassificationOID"),
        IS_BUSINESS_USE_CLASS("isBusinessUseClass"),
        IS_HIGHLY_RESTRICTED_CLASS("isHighlyRestrictedClass"),
        PREFERRED_ROUTE_TEMPLATE_IN_WORK_NAME("PreferredRouteTemplateInWorkName"),
        PREFERRED_ROUTE_TEMPLATE_IN_WORK_OID("PreferredRouteTemplateInWorkOID"),
        PREFERRED_ROUTE_TEMPLATE_IN_APPROVAL_NAME("PreferredRouteTemplateInApprovalName"),
        PREFERRED_ROUTE_TEMPLATE_IN_APPROVAL_OID("PreferredRouteTemplateInApprovalOID"),
        HYPOTHETICAL_PRIVATE("Hypothetical-Private"),
        HYPOTHETICAL_PUBLIC("Hypothetical-Public"),
        DEVELOPMENT("Development"),
        MANUFACTURING_STATUS_EXPERIMENTAL("Experimental"),
        PRODUCTION("Production"),
        SYMBOL_HYPHEN("-"),
        SYMBOL_PIPE("|"),
        SYMBOL_COLON(":"),
        SYMBOL_TILDE("~"),
        SYMBOL_SEMICOLON(";");
        private final String name;

        Basic(String name) {
            this.name = name;
        }

        public String get() {
            return this.name;
        }
    }

    public enum Attribute {

        DEFAULT_POLICY("attribute_DefaultPolicy"),

        STRUCTURE_RELEASE_CRITERIA_REQUIRED("attribute_pgStructuredReleaseCriteriaRequired"),

        IS_PRODUCT_COMPLIANCE_REQUIRED("attribute_pgIsProductCertificationorLocalStandardsComplianceStatementRequired"),
        ATTRIBUTE_PLATFORM_TYPE("attribute_pgPlatformType"),
        AUTHORIZED_TO_USE("attribute_pgIsAuthorizedtoUse"),
        AUTHORIZED_TO_PRODUCE("attribute_pgIsAuthorizedtoProduce"),
        AUTHORIZED_TO_VIEW("attribute_pgIsAuthorizedtoView"),
        IS_ACTIVATED("attribute_pgIsActivated"),
        STRUCTURED_RELEASE_CRITERIA_REQUIRED("attribute_pgStructuredReleaseCriteriaRequired"),
        SKIP_PART_VIA_DESIGNER("attribute_pgUserPrefSettingSkipPartViaDesigner"),
        ALLOWED_TYPES_AND_DEFAULT_VALUE_FOR_AUTHORIZED_TO_USE("attribute_pgUserPrefSettingAllowedTypesAndDefaultValueForAuthorizedToUse"),
        ALLOWED_TYPES_AND_DEFAULT_VALUE_FOR_AUTHORIZED_TO_PRODUCE("attribute_pgUserPrefSettingAllowedTypesAndDefaultValueForAuthorizedToProduce"),
        ALLOWED_TYPES_AND_DEFAULT_VALUE_FOR_AUTHORIZED("attribute_pgUserPrefSettingAllowedTypesAndDefaultValueForAuthorized"),
        ALLOWED_TYPES_AND_DEFAULT_VALUE_FOR_ACTIVATED("attribute_pgUserPrefSettingAllowedTypesAndDefaultValueForActivated"),
        ALLOWED_TYPES_FOR_PLANT_CONNECTION_FOR_EQUIVALENT("attribute_pgUserPrefSettingAllowedTypesForPlantConnectionForEquivalent"),
        ALLOWED_TYPES_FOR_SHARE_WITH_MEMBERS_CONNECTION_FOR_EQUIVALENT("attribute_pgUserPrefSettingAllowedTypesForShareWithMembersForEquivalent"),
        ALLOWED_TYPES_FOR_PLANT_CONNECTION_FOR_TECH_SPEC("attribute_pgUserPrefSettingAllowedTypesForPlantConnectionForTechSpec"),
        ALLOWED_TYPES_FOR_SHARE_WITH_MEMBERS_CONNECTION_FOR_TECH_SPEC("attribute_pgUserPrefSettingAllowedTypesForShareWithMembersForTechSpec"),
        ALLOWED_TYPES_FOR_PLANT_CONNECTION_FOR_PRODUCT_PART("attribute_pgUserPrefSettingAllowedTypesForPlantConnectionForProductPart"),
        ALLOWED_TYPES_FOR_SHARE_WITH_MEMBERS_CONNECTION_FOR_PRODUCT_PART("attribute_pgUserPrefSettingAllowedTypesForShareWithMembersForProductPart"),

        ALLOWED_PART_TYPES_FOR_PACKAGING_PREFERENCE("attribute_pgUserPrefSettingAllowedPartTypesForPackagingPreference"),
        ALLOWED_PART_TYPES_FOR_PRODUCT_PREFERENCE("attribute_pgUserPrefSettingAllowedPartTypesForProductPreference"),
        ALLOWED_PART_TYPES_FOR_RAW_MATERIAL_PREFERENCE("attribute_pgUserPrefSettingAllowedPartTypesForRawMaterialPreference"),
        ATTRIBUTE_PG_SECURITY_EMPLOYEE_TYPE("attribute_pgSecurityEmployeeType"),
        ALLOWED_TYPES_FOR_PART_TYPE("attribute_pgUserPrefSettingAllowedTypesForPartType"),
        ALLOWED_TYPES_FOR_PHASE("attribute_pgUserPrefSettingAllowedTypesForPhase"),
        ALLOWED_TYPES_FOR_MANUFACTURING_STATUS("attribute_pgUserPrefSettingAllowedTypesForManufacturingStatus"),
        ALLOWED_TYPES_FOR_PLANT_CONNECTION("attribute_pgUserPrefSettingAllowedTypesForPlantConnection"),
        ALLOWED_TYPES_FOR_SHARE_WITH_MEMBERS("attribute_pgUserPrefSettingAllowedTypesForShareWithMembers"),
        ALLOWED_TYPES_FOR_RELEASE_CRITERIA("attribute_pgUserPrefSettingAllowedTypesForStructuredReleaseCriteria"),
        ALLOWED_TYPES_FOR_PACKAGING_MATERIAL_TYPE("attribute_pgUserPrefSettingAllowedTypesForPackagingMaterialType"),
        ALLOWED_TYPES_FOR_SEGMENT("attribute_pgUserPrefSettingAllowedTypesForSegment"),
        ALLOWED_TYPES_FOR_CLASS("attribute_pgUserPrefSettingAllowedTypesForClass"),
        ALLOWED_TYPES_FOR_REPORTED_FUNCTION("attribute_pgUserPrefSettingAllowedTypesForReportedFunction"),
        IRM_ROUTE_TASK_DUE_DAY("attribute_pgUserPrefSettingIRMRouteTaskDueDay");

        private final String name;

        Attribute(String name) {
            this.name = name;
        }

        public String getName(Context context) {
            return PropertyUtil.getSchemaProperty(context, this.name);
        }

        public String getSelect(Context context) {
            return DomainObject.getAttributeSelect(this.getName(context));
        }

        public String toString() {
            return this.name;
        }

    }

    public enum Type {
        TYPE_PICKLIST_MATERIAL_FUNCTION_GLOBAL("type_pgPLIMaterialFunctionGlobal"),
        TYPE_PICKLIST_PLATFORM("type_pgPLIPlatform"),
        TYPE_PICKLIST_BUSINESS_AREA("type_pgPLIBusinessArea"),
        TYPE_PICKLIST_REPORTED_FUNCTION("type_pgPLIReportedFunction"),
        TYPE_PICKLIST_LIFE_CYCLE_STATUS("type_pgPLILifeCycleStatus"),
        TYPE_PICKLIST_PACKAGING_MATERIAL_TYPE("type_pgPLIPackMaterialType"),
        //Added for API.
        TYPE_PICKLIST_SEGMENT("type_pgPLISegment"),
        TYPE_CHANGE_ACTION("type_ChangeAction"),
        TYPE_FINISHED_PRODUCT_PART("type_FinishedProductPart"),
        TYPE_ASSEMBLED_PRODUCT_PART("type_pgAssembledProductPart"),
        TYPE_IRM_DOCUMENT("type_pgIRMDocument"),
        TYPE_IP_CONTROL("type_IPControlClass"),
        TYPE_FORMULATION("type_Formulation"),
        TYPE_PLANT("type_Plant"),
        TYPE_CONFIGURATION_ADMIN("type_pgConfigurationAdmin"),
        TYPE_PG_PLI_ORGANIZATION_CHANGE_MANAGEMENT("type_pgPLIOrganizationChangeManagement"),
        TYPE_CHANGE_TEMPLATE("type_ChangeTemplate"),
        TYPE_IP_CONTROL_CLASS("type_IPControlClass"),
        TYPE_REGION("type_Region");
        private final String name;

        Type(String name) {
            this.name = name;
        }

        public String getName(Context context) {
            return PropertyUtil.getSchemaProperty(context, this.name);
        }
    }

    public enum Relationship {
        RELATIONSHIP_PICKLIST_REPORTED_FUNCTION("relationship_pgPDTemplatestopgPLIReportedFunction"),
        RELATIONSHIP_PICKLIST_DOCUMENT_TO_PLATFORM("relationship_pgDocumentToPlatform"),
        RELATIONSHIP_PICKLIST_MATERIAL_FUNCTIONALITY("relationship_MaterialFunctionality"),
        RELATIONSHIP_PICKLIST_TEMPLATES_TO_SEGMENT("relationship_pgPDTemplatestopgPLISegment"),
        RELATIONSHIP_PICKLIST_DOCUMENT_TO_BUSINESSAREA("relationship_pgDocumentToBusinessArea");
        private final String name;

        Relationship(String name) {
            this.name = name;
        }

        public String getName(Context context) {
            return PropertyUtil.getSchemaProperty(context, this.name);
        }
    }

    public enum Preferences {
        GPS_PREFERRED_SHARE_WITH_MEMBERS("preference_GPSPreferredShareWithMembers"),
        GPS_PREFERRED_PRE_TASK_NOTIFICATION_USERS("preference_GPSPreferredPreTaskNotificationUsers"),
        GPS_PREFERRED_POST_TASK_NOTIFICATION_USERS("preference_GPSPreferredPostTaskNotificationUsers"),
        COMMON_BUSINESS_AREA("common_BusinessArea"), // not an actual preference key
        COMMON_SEGMENT("common_Segment"), // not an actual preference key
        COMMON_RELEASE_CRITERIA("common_ReleaseCriteria"), // not an actual preference key
        COMMON_CLASS("common_Class"), // not an actual preference key

        TECHNICAL_SPEC_PART_TYPE("preference_TechSpecType"),
		//Added by DSM for 22x CW-06 for Requirement #50186,50187 - START
        MEP_PART_TYPE("preference_MEPType"),
        SEP_PART_TYPE("preference_SEPType"),
		//Added by DSM for 22x CW-06 for Requirement #50186,50187 - END
        TECHNICAL_SPEC_SEGMENT("preference_TechSpecSegment"),
        PRODUCT_PART_TYPE("preference_ProductPartType"),
        PRODUCT_PHASE("preference_ProductPartPhase"),
        PRODUCT_MANUFACTURING_STATUS("preference_ProductMfgStatus"),
        PRODUCT_RELEASE_CRITERIA("preference_ProductReleaseCriteria"),
        PRODUCT_CLASS("preference_ProductClass"),
        PRODUCT_REPORTED_FUNCTION("preference_ProductReportedFunction"),
        PRODUCT_SEGMENT("preference_ProductSegment"),
        PRODUCT_BUSINESS_AREA("preference_ProductBusinessArea"),
        PRODUCT_CATEGORY_PLATFORM("preference_ProductCategoryPlatform"),

        PRODUCT_COMPLIANCE_REQUIRED("preference_ProductComplianceRequired"),

        RAW_MATERIAL_PART_TYPE("preference_RawMaterialPartType"),
        RAW_MATERIAL_PHASE("preference_RawMaterialPartPhase"),
        RAW_MATERIAL_MANUFACTURING_STATUS("preference_RawMaterialMfgStatus"),
        RAW_MATERIAL_RELEASE_CRITERIA("preference_RawMaterialReleaseCriteria"),
        RAW_MATERIAL_CLASS("preference_RawMaterialClass"),
        RAW_MATERIAL_REPORTED_FUNCTION("preference_RawMaterialReportedFunction"),
        RAW_MATERIAL_SEGMENT("preference_RawMaterialSegment"),
        RAW_MATERIAL_BUSINESS_AREA("preference_RawMaterialBusinessArea"),
        RAW_MATERIAL_PRODUCT_CATEGORY_PLATFORM("preference_RawMaterialProductCategoryPlatform"),
        RAW_MATERIAL_FUNCTION("preference_RawMaterialFunction"),

        PACKAGING_PART_TYPE("preference_PackagingPartType"),
        PACKAGING_PHASE("preference_PackagingPartPhase"),
        PACKAGING_MANUFACTURING_STATUS("preference_PackagingMfgStatus"),
        PACKAGING_RELEASE_CRITERIA("preference_PackagingReleaseCriteria"),
        PACKAGING_CLASS("preference_PackagingClass"),
        PACKAGING_REPORTED_FUNCTION("preference_PackagingReportedFunction"),
        PACKAGING_SEGMENT("preference_PackagingSegment"),
        PACKAGING_COMPONENT_TYPE("preference_PackagingComponentType"),
        PACKAGING_MATERIAL_TYPE("preference_PackagingMaterialType"),
        PACKAGING_BASE_UOM("preference_PackagingBaseUoM"),

        IRM_PREFERRED_ROUTE_INSTRUCTION("preference_IRMPreferredRouteInstruction"),
        IRM_PREFERRED_ROUTE_ACTION("preference_IRMPreferredRouteAction"),
        IRM_PREFERRED_ROUTE_TASK_RECIPIENT_MEMBERS("preference_IRMPreferredRouteTaskRecipientMembers"),
        IRM_PREFERRED_ROUTE_TASK_RECIPIENT_USER_GROUPS("preference_IRMPreferredRouteTaskRecipientUserGroups"),
        IS_IRM_PREFERRED_ROUTE_TASK_RECIPIENTS_USER_GROUPS("preference_IsPreferredRouteTaskRecipientUserGroups"),
        IS_IRM_PREFERRED_ROUTE_TASK_RECIPIENTS_MEMBERS("preference_IsPreferredRouteTaskRecipientMembers"),
        IRM_BUSINESS_AREA("preference_IRMBusinessArea"),

        PREFERRED_DEFAULT_TYPE_ON_CREATE_MEP_SEP("preference_PreferredDefaultTypeOnCreateMEPSEP"),
        PREFERRED_DEFAULT_TYPE_ON_CREATE_PRODUCT("preference_PreferredDefaultTypeOnCreateProduct"),
        PREFERRED_DEFAULT_TYPE_ON_CREATE_SPEC("preference_PreferredDefaultTypeOnCreateSpec"),
        PREFERRED_DEFAULT_PLANTS("preference_PreferredDefaultPlants"),
        PREFERRED_DEFAULT_SHARING_MEMBERS("preference_PreferredDefaultSharingMembers"),
        DI_PREFERRED_PART_TYPE("preference_DIPartType"),
        DI_PREFERRED_PHASE("preference_DIMaturityStatus"),
        DI_PREFERRED_MANUFACTURING_STATUS("preference_DefaultAttrMfgStatus"),
        DI_PREFERRED_RELEASE_CRITERIA_STATUS("preference_DefaultAttrReleaseCriteriaReq"),
        DI_PREFERRED_PACKAGING_MATERIAL_TYPE("preference_DefaultAttrPackagingMaterialType"),
        DI_PREFERRED_SEGMENT("preference_DefaultAttrSegment"),
        DI_PREFERRED_CLASS("preference_DefaultAttrClass"),
        DI_PREFERRED_REPORTED_FUNCTION("preference_DefaultAttrReportedFunction"),
        PREFERRED_CHANGE_TEMPLATE("preference_PreferredChangeTemplate"),
        PREFERRED_ROUTE_TEMPLATE_PRIMARY_ORG("preference_PreferredRouteTemplatePrimaryOrg"),
        PREFERRED_ROUTE_TEMPLATE_IN_WORK("preference_PreferredRouteTemplateInWork"),
        PREFERRED_ROUTE_TEMPLATE_IN_APPROVAL("preference_PreferredRouteTemplateInApproval"),
        IRM_PREFERRED_CLASSIFICATION("preference_IRMPreferredClassification"),
        IRM_PREFERRED_TITLE("preference_IRMPreferredTitle"),
        IRM_PREFERRED_DESCRIPTION("preference_IRMPreferredDescription"),
        IRM_PREFERRED_POLICY("preference_IRMPreferredPolicy"),
        IRM_PREFERRED_REGION("preference_IRMPreferredRegion"),

        IRM_PREFERRED_SHARING_MEMBERS("preference_IRMPreferredSharingMembers"),

        IRM_PROJECT_SPACE_BUSINESS_AREA("preference_IRMProjectSpaceBusinessArea"),
        PRIMARY_ORGANIZATION("preference_DefaultAttrPrimaryOrg"),
        HIGHLY_RESTRICTED_CLASS("preference_HighlyRestrictedClass"),
        BUSINESS_CLASS("preference_BusinessUseClass");
        private final String name;

        Preferences(String name) {
            this.name = name;
        }

        public String get() {
            return this.name;
        }
    }

}

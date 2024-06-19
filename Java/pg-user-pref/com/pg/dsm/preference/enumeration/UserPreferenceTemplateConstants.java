package com.pg.dsm.preference.enumeration;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.PropertyUtil;

import matrix.db.Context;

public class UserPreferenceTemplateConstants {
    public enum Basic {
        OBJECT_ID("objectId"),
        ID_CONNECTION("id[connection]"),
        ROUTE_TEMPLATE("routeTemplate"),
        INFORMED_USER("informedUser"),
        CHANGE_TEMPLATE("changeTemplate"),
        MIGRATED("migrated"),
        PRODUCT_COMPLIANCE_REQUIRED("productComplianceRequired"),
        RELEASE_PHASE("releasePhase"),
        LIFECYCLE_STATUS("lifecycleStatus"),
        REPORTED_FUNCTION("reportedFunction"),
        PACKAGING_COMPONENT_TYPE("packagingComponentType"),
        PACKAGING_MATERIAL_TYPE("packagingMaterialType"),
        UNIT_OF_MEASURE("unitOfMeasure"),
        RELEASE_CRITERIA_REQUIRED("releaseCriteriaRequired"),
        CLASS_TYPE("classType"),

        MATERIAL_FUNCTION_LIST("materialFunctionList"),
        BUSINESS_USE_LIST("businessUseList"),
        HIGHLY_RESTRICTED_LIST("highlyRestrictedList"),
        IN_WORK_ROUTE_TEMPLATE_LIST("inWorkRouteTemplateList"),
        IN_APPROVAL_ROUTE_TEMPLATE_LIST("inApprovalRouteTemplateList"),
        PRODUCT_CATEGORY_PLATFORM_LIST("productCategoryPlatformList"),
        INFORMED_USER_LIST("informedUserList"),
        SHARE_WITH_MEMBER_LIST("shareWithMemberList"),
        BUSINESS_AREA_LIST("businessAreaList"),
        SEGMENT("segment"),

        OWNERSHIP("ownership"),
        USER_TEMPLATE_FILTER_DROP_DOWN("Owned|Shared"),
        USER_TEMPLATE_FILTER_NAME("pgUserPreferenceTemplateDSMFilter"),
        PACKAGING_TYPES_CONFIGURATION("pgAncillaryPackagingMaterialPart:type_pgAncillaryPackagingMaterialPart:Ancillary Packaging Material Part:APMP|pgConsumerUnitPart:type_pgConsumerUnitPart:Consumer Unit Part:COP|pgCustomerUnitPart:type_pgCustomerUnitPart:Customer Unit Part:CUP|pgFabricatedPart:type_pgFabricatedPart:Fabricated Part:FAB|Finished Product Part:type_FinishedProductPart:Finished Product Part:FPP|pgInnerPackUnitPart:type_pgInnerPackUnitPart:Inner Pack:IP|pgMasterConsumerUnitPart:type_pgMasterConsumerUnitPart:Master Consumer Unit Part:MCOP|pgMasterCustomerUnitPart:type_pgMasterCustomerUnitPart:Master Customer Unit Part:MCUP|pgMasterInnerPackUnitPart:type_pgMasterInnerPackUnitPart:Master Inner Pack:MIP|pgMasterPackagingAssemblyPart:type_pgMasterPackagingAssemblyPart:Master Packaging Assembly Part:MPAP|pgMasterPackagingMaterialPart:type_pgMasterPackagingMaterialPart:Master Packaging Material Part:MPMP|pgOnlinePrintingPart:type_pgOnlinePrintingPart:Online Printing Part:OPP|Packaging Assembly Part:type_PackagingAssemblyPart:Packaging Assembly Part:PAP|Packaging Material Part:type_PackagingMaterialPart:Packaging Material Part:PMP|pgPromotionalItemPart:type_pgPromotionalItemPart:Promotional Item Part:PIP|pgTransportUnitPart:type_pgTransportUnitPart:Transport Unit Part:TUP"),
        PRODUCT_TYPES_CONFIGURATION("pgAssembledProductPart:type_pgAssembledProductPart:Assembled Product Part:APP|pgDeviceProductPart:type_pgDeviceProductPart:Device Product Part:DPP|Formulation Part:type_FormulationPart:Formulation Part:FOP|pgIntermediateProductPart:type_pgIntermediateProductPart:Intermediate Product Part:IPP|pgMasterProductPart:type_pgMasterProductPart:Master Product Part:MPP|pgSoftwarePart:type_pgSoftwarePart:Software Part:SWP"),
        RAW_MATERIAL_TYPES_CONFIGURATION("pgAncillaryRawMaterialPart:type_pgAncillaryRawMaterialPart:Ancillary Raw Material Part:ARMP|pgMasterRawMaterialPart:type_pgMasterRawMaterialPart:Master Raw Material Part:MRMP|Raw Material:type_RawMaterial:Raw Material:RM"),
        TECHNICAL_SPECIFICATION_TYPES_CONFIGURATION("pgDSOAffectedFPPList:type_pgDSOAffectedFPPList:Affected Finished Product Part List:AFPP|pgArtwork:type_pgArtwork:Art:ART|pgAuthorizedConfigurationStandard:type_pgAuthorizedConfigurationStandard:Authorized Configuration Standard:ACS|pgAuthorizedTemporarySpecification:type_pgAuthorizedTemporarySpecification:Authorized Temporary Standard:ATS|Formula Technical Specification:type_FormulaTechnicalSpecification:Intermediate Assembled Product Specification:IAPS|pgIllustration:type_pgIllustration:Illustration:ILST|pgLaboratoryIndexSpecification:type_pgLaboratoryIndexSpecification:Laboratory Index Specification: LIS |pgMakingInstructions:type_pgMakingInstructions:Making Instruction:MI|pgPackingInstructions:type_pgPackingInstructions:Packing Instruction:PI|pgProcessStandard:type_pgProcessStandard:Process Standard:PROS|pgQualitySpecification:type_pgQualitySpecification:Quality Specification:QUAL|pgRawMaterialPlantInstruction:type_pgRawMaterialPlantInstruction:Raw Material Plant Instruction:RMPI|pgStackingPattern:type_pgStackingPattern:Stacking Pattern Specification:SPS|pgStandardOperatingProcedure:type_pgStandardOperatingProcedure:Standard Operating Procedure:SOP|Test Method Specification:type_TestMethodSpecification:Test Method Specification:TM|pgStructuredATS:type_pgStructuredATS:Structured ATS:STRU-ATS"),
        EXPLORATION_TYPES_CONFIGURATION("pgPKGVPMPart:type_pgPKGVPMPart:Shape Part:ShapePart"),
        Allowd_IRM_AND_GPS("Project Lead,CATIADesigner,Component Engineer,Component Qualification Engineer,Library User,Manufacturing Engineer,Project User,Restricted Product Data Originator,Restricted Product Data User,Senior Manufacturing Engineer,SLMViewer"),
        //Added by DSM for 22x CW-06 for Requirement #50186,50187 - START
        MEP_SEP_TYPES_CONFIGURATION("pgAncillaryPackagingMaterialPart:type_pgAncillaryPackagingMaterialPart:Ancillary Packaging Material Part:APMP|pgAncillaryRawMaterialPart:type_pgAncillaryRawMaterialPart:Ancillary Raw Material Part:ARMP|pgDeviceProductPart:type_pgDeviceProductPart:Device Product Part:DPP|pgFabricatedPart:type_pgFabricatedPart:Fabricated Part:FAB|pgIntermediateProductPart:type_pgIntermediateProductPart:Intermediate Product Part:IPP|pgOnlinePrintingPart:type_pgOnlinePrintingPart:Online Printing Part:OPP|Packaging Assembly Part:type_PackagingAssemblyPart:Packaging Assembly Part:PAP|Packaging Material Part:type_PackagingMaterialPart:Packaging Material Part:PMP|pgPromotionalItemPart:type_pgPromotionalItemPart:Promotional Item Part:PIP|Raw Material:type_RawMaterial:Raw Material:RM|pgSoftwarePart:type_pgSoftwarePart:Software Part:SWP"),
        //Added by DSM for 22x CW-06 for Requirement #50186,50187 - END
        PART_TYPE("partType"),
        PURPOSE("vPurpose"),
        STATE("vState"),
        PRIMARY_ORGANIZATION("primaryOrganization"),
        CONTEXT_USER_NAME("contextUserName"),
        CONTEXT_USER_ID("contextUserID"),
        PART_CATEGORY("partCategory"),
        IS_MIGRATED("isMigrated"),
        PLANTS("plants"),
        SHARING_MEMBERS("sharingMembers"),
        TEMPLATE_SHARING_MEMBERS("templateSharingMembers"),
        UPT_INTERFACE_DSM("pgUPTInterfaceDSM"),
        CONFIG_OBJ_TYPE_MAPPING("pgUserPreferenceTemplateMapping"),
        AUTONAME_TYPE_USER_PREFERENCE_TEMPLATE("type_pgUserPreferenceTemplate"),
        AUTONAME_POLICY_USER_PREFERENCE_TEMPLATE_DSM("policy_pgUserPreferenceTemplateDSM"),
        TEMPLATE_SHARE_WITH_BASIC_ACCESS("read,show"),
        //Modified by DSM for 22x CW-06 for Requirement #50184 - START
        TEMPLATE_PART_CATEGORY_CONFIGURATION("PKG:Packaging|PRD:Product|RM:Raw Material|TS:Technical Specification|EXP:Exploration|MEP:Manufacturer Equivalent|SEP:Supplier Equivalent"),
        //Modified by DSM for 22x CW-06 for Requirement #50184 - END
        TEMPLATE_PART_CATEGORY(",PKG,PRD,RM,TS"),
        TEMPLATE_PART_CATEGORY_DISPLAY(",Packaging,Product,Raw Material,Technical Specification"),
        SYMBOL_SEMICOLON(";"),
        //Added by DSM for 22x CW-04 for Requirement #47545 - START
        SELECTED_TYPE("_selectedType"),
        UPT_TEMPLATE_NAME("SelectedTemplateName"),
        SELECTED_UPT_OID("SelectedUPTOID"),
        UPT_PHASE("ReleasePhase"),
        SEGMENT_ID("SegmentID"),
        CHANGE_TEMPLATE_ID("ChangeTemplateID"),
        CHANGE_TEMPLATE_NAME("ChangeTemplateName"),
        PRIM_ORG_OID("sPrimOrgOID"),
        PRIM_ORG_NAME("PrimOrgName"),
        INFORMED_USER_ID("InformedUserID"),
        INFORMED_USER_NAME("InformedUserName"),
        OUTPUT("output"),
        SYMB_PIPE("|"),
        SYMB_COLON(":"),
        SYMB_SEMICOLON("),"),
        SYMB_COMMA(","),
        PRODUCT_CATEGORY_PLATFORM("Product Category Platform"),
        UPT_HIGHLY_RESTRICTED_IP_CLASS_NAME("HighlyRestrictedname"),
        UPT_HIGHLY_RESTRICTED_IP_CLASS_ID("HighlyRestrictedid"),
        UPT_BUSINESS_USE_IP_CLASS_NAME("Restrictedname"),
        UPT_BUSINESS_USE_IP_CLASS_ID("Restrictedid"),
        //Added by DSM for 22x CW-04 for Requirement #47545 - END
        // Added by DSM for 22x CW-04 for Requirement 47971 - Start
        FORMULATION_PROCESS("Formulation Process"),
        // Added by DSM for 22x CW-04 for Requirement 47971 - END
    	//Added by DSM for 22x CW-04 for Requirement 47972 - START
        COSMETIC_FORMULATION("Cosmetic Formulation"),
    	//Added by DSM for 22x CW-04 for Requirement 47972 - END
    	//Added by DSM for 22x.06 CW JAS for Requirement 47969 - START
        VENDOR_NAME("Vendor_Name"),
        VENDOR_ID("Vendor_Id");
    	//Added by DSM for 22x.06 CW JAS for Requirement 47969 - END
    	
        private final String name;

        Basic(String name) {
            this.name = name;
        }

        public String get() {
            return this.name;
        }
    }

    public enum Attribute {
        ATTRIBUTE_UPT_PHYSICAL_ID("attribute_pgUPTPhyID"),
        ATTRIBUTE_UPT_IS_MIGRATED("attribute_pgUPTIsMigrated"),
        ATTRIBUTE_UPT_REGION("attribute_pgUPTRegion"),
        ATTRIBUTE_UPT_CLASSIFICATION("attribute_pgUPTClassification"),
        ATTRIBUTE_UPT_ROUTE_INSTRUCTION("attribute_pgUPTRouteInstruction"),
        ATTRIBUTE_UPT_ROUTE_ACTION("attribute_pgUPTRouteAction"),
        ATTRIBUTE_UPT_CLASS("attribute_pgUPTClass"),
        ATTRIBUTE_UPT_RELEASE_PHASE("attribute_pgUPTReleasePhase"),
        ATTRIBUTE_UPT_LIFECYCLE_STATUS("attribute_pgUPTLifeCycleStatus"),
        ATTRIBUTE_UPT_STRUCTURE_RELEASE_CRITERIA_REQUIRED("attribute_pgUPTStructuredReleaseCriteriaRequired"),
        ATTRIBUTE_UPT_REPORTED_FUNCTION("attribute_pgUPTReportedFunction"),
        ATTRIBUTE_UPT_SEGMENT("attribute_pgUPTSegment"),
        ATTRIBUTE_UPT_PACKAGING_COMPONENT_TYPE("attribute_pgUPTPackagingComponentType"),
        ATTRIBUTE_UPT_PACKAGING_MATERIAL_TYPE("attribute_pgUPTPackagingMaterialType"),
        ATTRIBUTE_UPT_BASE_UNIT_OF_MEASURE("attribute_pgUPTBaseUnitOfMeasure"),
        ATTRIBUTE_UPT_IS_PRODUCT_COMPLIANCE_REQUIRED("attribute_pgUPTIsProductComplianceRequired"),
        ATTRIBUTE_UPT_MATERIAL_FUNCTION("attribute_pgUPTMaterialFunction"),
        ATTRIBUTE_UPT_PART_TYPE("attribute_pgUPTPartType"),
        //Added by DSM for 22x CW-06 for Requirement #50186,50187 - START
        ATTRIBUTE_UPT_VENDOR("attribute_pgUPTVendor"),
        //Added by DSM for 22x CW-06 for Requirement #50186,50187 - END
        ATTRIBUTE_UPT_PART_CATEGORY("attribute_pgUPTPartCategory"),
        ATTRIBUTE_UPT_BUSINESS_USE_IP_CLASS("attribute_pgUPTBusinessUseIPClass"),
        ATTRIBUTE_UPT_HIGHLY_RESTRICTED_IP_CLASS("attribute_pgUPTHighlyRestrictedIPClass"),
        ATTRIBUTE_UPT_PRIMARY_ORGANIZATION("attribute_pgUPTPrimaryOrganization"),
        ATTRIBUTE_UPT_INFORMED_USERS("attribute_pgUPTInformedUsers"),


        ATTRIBUTE_UPT_ROUTE_TEMPLATE_IN_WORK("attribute_pgUPTInWork"),
        ATTRIBUTE_UPT_ROUTE_TEMPLATE_IN_APPROVAL("attribute_pgUPTInApproval"),
        ATTRIBUTE_UPT_CHANGE_TEMPLATE("attribute_pgUPTChangeTemplate"),

        ATTRIBUTE_UPT_SHARE_WITH_MEMBERS("attribute_pgUPTShareWithMembers"),
        ATTRIBUTE_UPT_BUSINESS_AREA("attribute_pgUPTBusinessArea"),
        ATTRIBUTE_UPT_PRODUCT_CATEGORY_PLATFORM("attribute_pgUPTProductCategoryPlatform"),

        ATTRIBUTE_UPT_TYPES_FOR_ACTIVATED("attribute_pgUPTTypesForActivated"),
        ATTRIBUTE_UPT_TYPES_FOR_AUTHORIZED_TO_USE_AND_PRODUCE("attribute_pgUPTTypesForAuthorizedToUseAndProduce"),
        ATTRIBUTE_UPT_TYPES_FOR_AUTHORIZED_TO_VIEW("attribute_pgUPTTypesForAuthorizedToView"),
        ATTRIBUTE_UPT_TYPES_FOR_AUTHORIZED_TO_VIEW_DEFAULT_SET("attribute_pgUPTTypesToSetDefaultValueForAuthorizedToView"),
        //Added by DSM for 22x CW-04 for Requirement #47545 - START
        ATTRIBUTE_RELEASE_PHASE("attribute_ReleasePhase"),
        ATTRIBUTE_SEGMENT("attribute_pgSegment"),
        ATTRIBUTE_LIFECYCLE_STATUS("attribute_pgLifeCycleStatus"),
        ATTRIBUTE_STRUCTURED_RELEASE_CRITERIA_REQUIRED("attribute_pgStructuredReleaseCriteriaRequired"),
        ATTRIBUTE_CLASS("attribute_pgClass"),
        ATTRIBUTE_REPORTED_FUNCTION("attribute_pgReportedFunction"),
        ATTRIBUTE_PACKAGING_COMPONENT_TYPE("attribute_pgPackagingComponentType"),
        ATTRIBUTE_PACKAGING_MATERIAL_TYPE("attribute_pgPackagingMaterialType"),
        ATTRIBUTE_BASE_UNIT_OF_MEASURE("attribute_pgBaseUnitOfMeasure"),
        ATTRIBUTE_BUSINESS_AREA("attribute_pgBusinessArea"),
        ATTRIBUTE_PLATFORM_TYPE("attribute_pgPlatformType"),
        ATTRIBUTE_MATERIAL_FUNCTION_GLOBAL("attribute_pgMaterialFunctionGlobal"),
        //Added by DSM for 22x CW-04 for Requirement #47545 - END
    	//Added by DSM for 22x CW-04 for Requirement #47969 - START
        ATTRIBUTE_VENDOR("attribute_pgUPTVendor");
    	//Added by DSM for 22x CW-04 for Requirement #47969 - END
    	

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
        TYPE_UPT_TEMPLATE("type_pgUserPreferenceTemplate"),
        TYPE_AUTHORIZED_CONFIGURATION_STANDARD("type_pgAuthorizedConfigurationStandard"),
        TYPE_AUTHORIZED_TEMPORARY_SPECIFICATION("type_pgAuthorizedTemporarySpecification"),
        TYPE_INTERMEDIATE_ASSEMBLED_PRODUCT_SPECIFICATION("type_FormulaTechnicalSpecification"),
        TYPE_LABORATORY_INDEX_SPECIFICATION("type_pgLaboratoryIndexSpecification"),
        TYPE_MAKING_INSTRUCTION("type_pgMakingInstructions"),
        TYPE_PROCESS_STANDARD("type_pgProcessStandard"),
        TYPE_STANDARD_OPERATING_PROCEDURE("type_pgStandardOperatingProcedure"),
        TYPE_USER_PREFERENCE_TEMPLATE("type_pgUserPreferenceTemplate"),
        TYPE_USER_PREFERENCE_TEMPLATE_IRM("type_pgUserPreferenceTemplateIRM"),
        //Added by DSM for 22x CW-04 for Requirement #47545 - START
        TYPE_SEGMENT("type_pgPLISegment"),
        TYPE_CHANGE_ACTION("type_ChangeAction"),
        //Added by DSM for 22x CW-04 for Requirement #47545 - END

        // Added by DSM for 22x CW-04 for Requirement 47971 - Start
        TYPE_FORMULATION_PART("type_FormulationPart");
        // Added by DSM for 22x CW-04 for Requirement 47971 - END
        private final String name;

        Type(String name) {
            this.name = name;
        }

        public String getName(Context context) {
            return PropertyUtil.getSchemaProperty(context, this.name);
        }
    }

    public enum Policy {

        POLICY_USER_PREFERENCE_TEMPLATE_DSM("policy_pgUserPreferenceTemplateDSM"),
        POLICY_USER_PREFERENCE_TEMPLATE_IRM("policy_pgUserPreferenceTemplateIRM");
        private final String name;

        Policy(String name) {
            this.name = name;
        }

        public String getName(Context context) {
            return PropertyUtil.getSchemaProperty(context, this.name);
        }
    }

    public enum Interface {
        INTERFACE_UPT_TEMPLATE_IRM("interface_pgUPTInterfaceIRM"),
        INTERFACE_UPT_TEMPLATE_DSM("interface_pgUPTInterfaceDSM"),
        //Added by DSM for 22x CW-04 for Requirement #47545 - START
        INTERFACE_UPT_PHYSICAL_ID_EXTN("interface_pgUPTPhysicalIDExtn"),
        INTERFACE_UPT_DSM("interface_pgUPTInterfaceDSM");
        //Added by DSM for 22x CW-04 for Requirement #47545 - END
        private final String name;

        Interface(String name) {
            this.name = name;
        }

        public String getName(Context context) {
            return PropertyUtil.getSchemaProperty(context, this.name);
        }
    }

    public enum Relationship {
        RELATIONSHIP_AFFECTED_ITEM("relationship_Region"),
        RELATIONSHIP_USER_PREFERENCE_PLANT("relationship_pgUserPreferencePlant"),
        RELATIONSHIP_CHANGE_FOLLOWER("relationship_ChangeFollower");
        private final String name;

        Relationship(String name) {
            this.name = name;
        }

        public String getName(Context context) {
            return PropertyUtil.getSchemaProperty(context, this.name);
        }
    }

    public enum PackagingFields {
        PART_CATEGORY("PartCategory", "partCategory", "partCategoryDisplay", DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING),
        PART_TYPE("PartType", "partTypeTag", "partTypeDisplay", DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING),
        PHASE("Phase", "phaseTag", "phaseDisplay", DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING),
        MANUFACTURING_STATUS("ManufacturingStatus", "maturityStatusSelectTag", "manufacturingStatusDisplay", DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING),
        RELEASE_CRITERIA("ReleaseCriteria", "releaseCriteria", "releaseCriteriaDisplay", DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING),

        // below fields holds pick-list objects.
        CLASS_NAME("ClassName", "className", "classNameDisplay", "classNameOID", "classTag"),
        REPORTED_FUNCTION("ReportedFunction", "reportedFunction", "reportedFunctionDisplay", "reportedFunctionOID", "reportedFunctionTag"),
        SEGMENT("Segment", "segment", "segmentDisplay", "segmentOID", "segmentTag"),
        COMPONENT_TYPE("PackagingComponentType", "packagingComponentType", "packagingComponentTypeDisplay", "packagingComponentTypeOID", "packagingComponentTypeTag"),
        MATERIAL_TYPE("PackagingMaterialType", "packagingMaterialType", "packagingMaterialTypeDisplay", "packagingMaterialTypeOID", "packagingMaterialTypeTag"),
        BASE_UOM("BaseUnitOfMeasure", "baseUnitOfMeasure", "baseUnitOfMeasureDisplay", "baseUnitOfMeasureOID", "baseUnitOfMeasureTag");
        private final String fieldIdentifier;
        private final String fieldName;
        private final String fieldDisplayName;
        private final String fieldOID;
        private final String fieldPhysicalID;


        PackagingFields(String fieldIdentifier, String fieldName, String fieldDisplayName, String fieldOID, String fieldPhysicalID) {
            this.fieldIdentifier = fieldIdentifier;
            this.fieldName = fieldName;
            this.fieldDisplayName = fieldDisplayName;
            this.fieldOID = fieldOID;
            this.fieldPhysicalID = fieldPhysicalID;
        }

        public static PackagingFields getByFieldIdentifier(String field) {
            for (PackagingFields type : PackagingFields.values()) {
                if (type.fieldIdentifier.equals(field)) {
                    return type;
                }
            }
            return null;
        }

        public String getFieldIdentifier() {
            return fieldIdentifier;
        }

        public String getFieldName() {
            return fieldName;
        }

        public String getFieldDisplayName() {
            return fieldDisplayName;
        }

        public String getFieldOID() {
            return fieldOID;
        }

        public String getFieldPhysicalID() {
            return fieldPhysicalID;
        }
    }

    public enum ProductFields {
        PART_CATEGORY("PartCategory", "partCategory", "partCategoryDisplay", DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING),
        PART_TYPE("PartType", "partTypeTag", "partTypeDisplay", DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING),
        PHASE("Phase", "phaseTag", "phaseDisplay", DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING),
        MANUFACTURING_STATUS("ManufacturingStatus", "maturityStatusSelectTag", "manufacturingStatusDisplay", DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING),
        RELEASE_CRITERIA("ReleaseCriteria", "releaseCriteria", "releaseCriteriaDisplay", DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING),

        // below fields holds pick-list objects.
        CLASS_NAME("ClassName", "className", "classNameDisplay", "classNameOID", "classTag"),
        REPORTED_FUNCTION("ReportedFunction", "reportedFunction", "reportedFunctionDisplay", "reportedFunctionOID", "reportedFunctionTag"),
        SEGMENT("Segment", "segment", "segmentDisplay", "segmentOID", "segmentTag"),
        BUSINESS_AREA("BusinessArea", "businessArea", "businessAreaDisplay", "businessAreaOID", "businessAreaTag"),
        PRODUCT_CATEGORY_PLATFORM("ProductCategoryPlatform", "productCategoryPlatform", "productCategoryPlatformDisplay", "productCategoryPlatformOID", "productCategoryPlatformTag"),
        PRODUCT_COMPLIANCE_REQUIRED("ProductCompliance", "productCompliance", "productComplianceDisplay", "productComplianceOID", "productComplianceTag");
        private final String fieldIdentifier;
        private final String fieldName;
        private final String fieldDisplayName;
        private final String fieldOID;
        private final String fieldPhysicalID;


        ProductFields(String fieldIdentifier, String fieldName, String fieldDisplayName, String fieldOID, String fieldPhysicalID) {
            this.fieldIdentifier = fieldIdentifier;
            this.fieldName = fieldName;
            this.fieldDisplayName = fieldDisplayName;
            this.fieldOID = fieldOID;
            this.fieldPhysicalID = fieldPhysicalID;
        }

        public static ProductFields getByFieldIdentifier(String field) {
            for (ProductFields type : ProductFields.values()) {
                if (type.fieldIdentifier.equals(field)) {
                    return type;
                }
            }
            return null;
        }

        public String getFieldIdentifier() {
            return fieldIdentifier;
        }

        public String getFieldName() {
            return fieldName;
        }

        public String getFieldDisplayName() {
            return fieldDisplayName;
        }

        public String getFieldOID() {
            return fieldOID;
        }

        public String getFieldPhysicalID() {
            return fieldPhysicalID;
        }
    }

    public enum RawMaterialFields {
        PART_CATEGORY("PartCategory", "partCategory", "partCategoryDisplay", DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING),
        PART_TYPE("PartType", "partTypeTag", "partTypeDisplay", DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING),
        PHASE("Phase", "phaseTag", "phaseDisplay", DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING),
        MANUFACTURING_STATUS("ManufacturingStatus", "maturityStatusSelectTag", "manufacturingStatusDisplay", DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING),
        RELEASE_CRITERIA("ReleaseCriteria", "releaseCriteria", "releaseCriteriaDisplay", DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING),

        // below fields holds pick-list objects.
        CLASS_NAME("ClassName", "className", "classNameDisplay", "classNameOID", "classTag"),
        REPORTED_FUNCTION("ReportedFunction", "reportedFunction", "reportedFunctionDisplay", "reportedFunctionOID", "reportedFunctionTag"),
        SEGMENT("Segment", "segment", "segmentDisplay", "segmentOID", "segmentTag"),
        BUSINESS_AREA("BusinessArea", "businessArea", "businessAreaDisplay", "businessAreaOID", "businessAreaTag"),
        PRODUCT_CATEGORY_PLATFORM("ProductCategoryPlatform", "productCategoryPlatform", "productCategoryPlatformDisplay", "productCategoryPlatformOID", "productCategoryPlatformTag"),
        MATERIAL_FUNCTION("MaterialFunction", "materialFunction", "materialFunctionDisplay", "materialFunctionOID", "materialFunctionTag");
        private final String fieldIdentifier;
        private final String fieldName;
        private final String fieldDisplayName;
        private final String fieldOID;
        private final String fieldPhysicalID;


        RawMaterialFields(String fieldIdentifier, String fieldName, String fieldDisplayName, String fieldOID, String fieldPhysicalID) {
            this.fieldIdentifier = fieldIdentifier;
            this.fieldName = fieldName;
            this.fieldDisplayName = fieldDisplayName;
            this.fieldOID = fieldOID;
            this.fieldPhysicalID = fieldPhysicalID;
        }

        public static RawMaterialFields getByFieldIdentifier(String field) {
            for (RawMaterialFields type : RawMaterialFields.values()) {
                if (type.fieldIdentifier.equals(field)) {
                    return type;
                }
            }
            return null;
        }

        public String getFieldIdentifier() {
            return fieldIdentifier;
        }

        public String getFieldName() {
            return fieldName;
        }

        public String getFieldDisplayName() {
            return fieldDisplayName;
        }

        public String getFieldOID() {
            return fieldOID;
        }

        public String getFieldPhysicalID() {
            return fieldPhysicalID;
        }
    }

    public enum TechnicalSpecificationFields {
        PART_CATEGORY("PartCategory", "partCategory", "partCategoryDisplay", DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING),
        PART_TYPE("PartType", "partTypeTag", "partTypeDisplay", DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING),

        // below fields holds pick-list objects.
        SEGMENT("Segment", "segment", "segmentDisplay", "segmentOID", "segmentTag");

        private final String fieldIdentifier;
        private final String fieldName;
        private final String fieldDisplayName;
        private final String fieldOID;
        private final String fieldPhysicalID;

        TechnicalSpecificationFields(String fieldIdentifier, String fieldName, String fieldDisplayName, String fieldOID, String fieldPhysicalID) {
            this.fieldIdentifier = fieldIdentifier;
            this.fieldName = fieldName;
            this.fieldDisplayName = fieldDisplayName;
            this.fieldOID = fieldOID;
            this.fieldPhysicalID = fieldPhysicalID;
        }

        public static TechnicalSpecificationFields getByFieldIdentifier(String field) {
            for (TechnicalSpecificationFields type : TechnicalSpecificationFields.values()) {
                if (type.fieldIdentifier.equals(field)) {
                    return type;
                }
            }
            return null;
        }

        public String getFieldIdentifier() {
            return fieldIdentifier;
        }

        public String getFieldName() {
            return fieldName;
        }

        public String getFieldDisplayName() {
            return fieldDisplayName;
        }

        public String getFieldOID() {
            return fieldOID;
        }

        public String getFieldPhysicalID() {
            return fieldPhysicalID;
        }
    }

    public enum ExplorationFields {
        PART_CATEGORY("PartCategory", "partCategory", "partCategoryDisplay", DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING),
        PART_TYPE("PartType", "partTypeTag", "partTypeDisplay", DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);

        private final String fieldIdentifier;
        private final String fieldName;
        private final String fieldDisplayName;
        private final String fieldOID;
        private final String fieldPhysicalID;

        ExplorationFields(String fieldIdentifier, String fieldName, String fieldDisplayName, String fieldOID, String fieldPhysicalID) {
            this.fieldIdentifier = fieldIdentifier;
            this.fieldName = fieldName;
            this.fieldDisplayName = fieldDisplayName;
            this.fieldOID = fieldOID;
            this.fieldPhysicalID = fieldPhysicalID;
        }

        public static ExplorationFields getByFieldIdentifier(String field) {
            for (ExplorationFields type : ExplorationFields.values()) {
                if (type.fieldIdentifier.equals(field)) {
                    return type;
                }
            }
            return null;
        }

        public String getFieldIdentifier() {
            return fieldIdentifier;
        }

        public String getFieldName() {
            return fieldName;
        }

        public String getFieldDisplayName() {
            return fieldDisplayName;
        }

        public String getFieldOID() {
            return fieldOID;
        }

        public String getFieldPhysicalID() {
            return fieldPhysicalID;
        }
    }
        //Added by DSM for 22x CW-06 for Requirement #50186,50187 - START
    public enum MEPFields {
        PART_CATEGORY("PartCategory", "partCategory", "partCategoryDisplay", DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING),
        PART_TYPE("PartType", "partTypeTag", "partTypeDisplay", DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING),
        VENDOR("Vendor", "vendor", "vendorDisplay", "vendorOID", "vendorPhysicalID");

        private final String fieldIdentifier;
        private final String fieldName;
        private final String fieldDisplayName;
        private final String fieldOID;
        private final String fieldPhysicalID;

        MEPFields(String fieldIdentifier, String fieldName, String fieldDisplayName, String fieldOID, String fieldPhysicalID) {
            this.fieldIdentifier = fieldIdentifier;
            this.fieldName = fieldName;
            this.fieldDisplayName = fieldDisplayName;
            this.fieldOID = fieldOID;
            this.fieldPhysicalID = fieldPhysicalID;
        }

        public static MEPFields getByFieldIdentifier(String field) {
            for (MEPFields type : MEPFields.values()) {
                if (type.fieldIdentifier.equals(field)) {
                    return type;
                }
            }
            return null;
        }

        public String getFieldIdentifier() {
            return fieldIdentifier;
        }

        public String getFieldName() {
            return fieldName;
        }

        public String getFieldDisplayName() {
            return fieldDisplayName;
        }

        public String getFieldOID() {
            return fieldOID;
        }

        public String getFieldPhysicalID() {
            return fieldPhysicalID;
        }
    }

    public enum SEPFields {
        PART_CATEGORY("PartCategory", "partCategory", "partCategoryDisplay", DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING),
        PART_TYPE("PartType", "partTypeTag", "partTypeDisplay", DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING),
        VENDOR("Vendor", "vendor", "vendorDisplay", "vendorOID", "vendorPhysicalID");

        private final String fieldIdentifier;
        private final String fieldName;
        private final String fieldDisplayName;
        private final String fieldOID;
        private final String fieldPhysicalID;

        SEPFields(String fieldIdentifier, String fieldName, String fieldDisplayName, String fieldOID, String fieldPhysicalID) {
            this.fieldIdentifier = fieldIdentifier;
            this.fieldName = fieldName;
            this.fieldDisplayName = fieldDisplayName;
            this.fieldOID = fieldOID;
            this.fieldPhysicalID = fieldPhysicalID;
        }

        public static SEPFields getByFieldIdentifier(String field) {
            for (SEPFields type : SEPFields.values()) {
                if (type.fieldIdentifier.equals(field)) {
                    return type;
                }
            }
            return null;
        }

        public String getFieldIdentifier() {
            return fieldIdentifier;
        }

        public String getFieldName() {
            return fieldName;
        }

        public String getFieldDisplayName() {
            return fieldDisplayName;
        }

        public String getFieldOID() {
            return fieldOID;
        }

        public String getFieldPhysicalID() {
            return fieldPhysicalID;
        }
    }
        //Added by DSM for 22x CW-06 for Requirement #50186,50187 - END
    
    public enum ShareWithMembersFields {
        SHARE_TEMPLATE_WITH_MEMBERS("ShareWithMembers", "shareWithMembers", "shareWithMembersDisplay", "shareWithMembersOID", "shareWithMembersPhysicalID");
        private final String fieldIdentifier;
        private final String fieldName;
        private final String fieldDisplayName;
        private final String fieldOID;
        private final String fieldPhysicalID;


        ShareWithMembersFields(String fieldIdentifier, String fieldName, String fieldDisplayName, String fieldOID, String fieldPhysicalID) {
            this.fieldIdentifier = fieldIdentifier;
            this.fieldName = fieldName;
            this.fieldDisplayName = fieldDisplayName;
            this.fieldOID = fieldOID;
            this.fieldPhysicalID = fieldPhysicalID;
        }

        public static ShareWithMembersFields getByFieldIdentifier(String field) {
            for (ShareWithMembersFields type : ShareWithMembersFields.values()) {
                if (type.fieldIdentifier.equals(field)) {
                    return type;
                }
            }
            return null;
        }

        public String getFieldIdentifier() {
            return fieldIdentifier;
        }

        public String getFieldName() {
            return fieldName;
        }

        public String getFieldDisplayName() {
            return fieldDisplayName;
        }

        public String getFieldOID() {
            return fieldOID;
        }

        public String getFieldPhysicalID() {
            return fieldPhysicalID;
        }
    }

    public enum ShareTemplateWithMembersFields {
        SHARE_TEMPLATE_WITH_MEMBERS("ShareTemplateWithMembers", "shareTemplateWithMembers", "shareTemplateWithMembersDisplay", "shareTemplateWithMembersOID", "shareTemplateWithMembersPhysicalID");
        private final String fieldIdentifier;
        private final String fieldName;
        private final String fieldDisplayName;
        private final String fieldOID;
        private final String fieldPhysicalID;


        ShareTemplateWithMembersFields(String fieldIdentifier, String fieldName, String fieldDisplayName, String fieldOID, String fieldPhysicalID) {
            this.fieldIdentifier = fieldIdentifier;
            this.fieldName = fieldName;
            this.fieldDisplayName = fieldDisplayName;
            this.fieldOID = fieldOID;
            this.fieldPhysicalID = fieldPhysicalID;
        }

        public static ShareTemplateWithMembersFields getByFieldIdentifier(String field) {
            for (ShareTemplateWithMembersFields type : ShareTemplateWithMembersFields.values()) {
                if (type.fieldIdentifier.equals(field)) {
                    return type;
                }
            }
            return null;
        }

        public String getFieldIdentifier() {
            return fieldIdentifier;
        }

        public String getFieldName() {
            return fieldName;
        }

        public String getFieldDisplayName() {
            return fieldDisplayName;
        }

        public String getFieldOID() {
            return fieldOID;
        }

        public String getFieldPhysicalID() {
            return fieldPhysicalID;
        }
    }

    public enum SecurityFields {
        PRIMARY_ORGANIZATION("PrimaryOrganization", "primaryOrg", "primaryOrgDisplay", "primaryOrgOID", "primaryOrgPhysicalID"),
        BUSINESS_USE("BusinessUse", "businessUse", "businessUseDisplay", "businessUseOID", "businessUsePhysicalID"),
        HIGHLY_RESTRICTED("HighlyRestricted", "highlyRestricted", "highlyRestrictedDisplay", "highlyRestrictedOID", "highlyRestrictedPhysicalID");
        private final String fieldIdentifier;
        private final String fieldName;
        private final String fieldDisplayName;
        private final String fieldOID;
        private final String fieldPhysicalID;


        SecurityFields(String fieldIdentifier, String fieldName, String fieldDisplayName, String fieldOID, String fieldPhysicalID) {
            this.fieldIdentifier = fieldIdentifier;
            this.fieldName = fieldName;
            this.fieldDisplayName = fieldDisplayName;
            this.fieldOID = fieldOID;
            this.fieldPhysicalID = fieldPhysicalID;
        }

        public static SecurityFields getByFieldIdentifier(String field) {
            for (SecurityFields type : SecurityFields.values()) {
                if (type.fieldIdentifier.equals(field)) {
                    return type;
                }
            }
            return null;
        }

        public String getFieldIdentifier() {
            return fieldIdentifier;
        }

        public String getFieldName() {
            return fieldName;
        }

        public String getFieldDisplayName() {
            return fieldDisplayName;
        }

        public String getFieldOID() {
            return fieldOID;
        }

        public String getFieldPhysicalID() {
            return fieldPhysicalID;
        }
    }

    public enum ChangeActionFields {
        IN_WORK_ROUTE_TEMPLATE("InWorkRouteTemplate", "inWorkRouteTemplate", "inWorkRouteTemplateDisplay", "inWorkRouteTemplateOID", "inWorkRouteTemplatePhysicalID"),
        IN_APPROVAL_ROUTE_TEMPLATE("InApprovalRouteTemplate", "inApprovalRouteTemplate", "inApprovalRouteTemplateDisplay", "inApprovalRouteTemplateOID", "inApprovalRouteTemplatePhysicalID"),
        CHANGE_TEMPLATE("ChangeTemplate", "changeTemplate", "changeTemplateDisplay", "changeTemplateOID", "changeTemplatePhysicalID"),
        INFORMED_USERS("InformedUser", "Follower", "informedUserDisplay", "FollowerHidden", "informedUserPhysicalID");
        private final String fieldIdentifier;
        private final String fieldName;
        private final String fieldDisplayName;
        private final String fieldOID;
        private final String fieldPhysicalID;

        ChangeActionFields(String fieldIdentifier, String fieldName, String fieldDisplayName, String fieldOID, String fieldPhysicalID) {
            this.fieldIdentifier = fieldIdentifier;
            this.fieldName = fieldName;
            this.fieldDisplayName = fieldDisplayName;
            this.fieldOID = fieldOID;
            this.fieldPhysicalID = fieldPhysicalID;
        }

        public static ChangeActionFields getByFieldIdentifier(String field) {
            for (ChangeActionFields type : ChangeActionFields.values()) {
                if (type.fieldIdentifier.equals(field)) {
                    return type;
                }
            }
            return null;
        }

        public String getFieldIdentifier() {
            return fieldIdentifier;
        }

        public String getFieldName() {
            return fieldName;
        }

        public String getFieldDisplayName() {
            return fieldDisplayName;
        }

        public String getFieldOID() {
            return fieldOID;
        }

        public String getFieldPhysicalID() {
            return fieldPhysicalID;
        }
    }

    public enum TemplateFields {
        PART_TYPE("partTypeTag"),
        PART_CATEGORY("partCategory"),
        TEMPLATE_NAME("templateName"),
        TEMPLATE_AUTO_NAME("templateAutoName"),
        CONTEXT_USER_NAME("contextUserName"),
        CONTEXT_USER_ID("contextUserId"),
        DESCRIPTION("description");
        private final String name;

        TemplateFields(String name) {
            this.name = name;
        }

        public String get() {
            return this.name;
        }
    }

    public enum PlantFields {

        PLANT("plants"),
        PLANT_DISPLAY("plantsDisplay"),
        PLANT_OID("plantsOID"),
        ACTIVATED("activated"),
        AUTHORIZED_TO_PRODUCE("authorizedToProduce"),
        AUTHORIZED("authorized"),
        AUTHORIZED_TO_USE("authorizedToUse");
        private final String name;

        PlantFields(String name) {
            this.name = name;
        }

        public String get() {
            return this.name;
        }
    }

}

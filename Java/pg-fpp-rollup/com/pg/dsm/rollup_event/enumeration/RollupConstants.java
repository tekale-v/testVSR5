package com.pg.dsm.rollup_event.enumeration;

import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.PropertyUtil;

import matrix.db.Context;

public class RollupConstants {
    public enum Basic {
        PHYSICAL_ID("physicalid"),
        BATTERY_ROLLUP_EVENT_IDENTIFIER("BatteryRollupEvent"),
        GHS_ROLLUP_EVENT_IDENTIFIER("GHSRollupEvent"),
        MARKET_REGISTRATION_ROLLUP_EVENT_IDENTIFIER("MarketRegistrationRollupEvent"),
        PHYS_CHEM_ROLLUP_EVENT_IDENTIFIER("PhysChemRollupEvent"),
        DGC_ROLLUP_EVENT_IDENTIFIER("DGCRollupEvent"),
        SMART_LABEL_READY_ROLLUP_EVENT_IDENTIFIER("SmartLabelReadyRollupEvent"),
        INGREDIENT_STATEMENT_ROLLUP_EVENT_IDENTIFIER("IngredientStatementRollupEvent"),
        STABILITY_RESULTS_ROLLUP_EVENT_IDENTIFIER("StabilityDocumentRollupEvent"),
        CERTIFICATIONS_ROLLUP_EVENT_IDENTIFIER("CertificationsRollupEvent"),
        SMART_LABEL_ROLLUP_EVENT_IDENTIFIER("SmartLabelRollupEvent"),
        WAREHOUSE_ROLLUP_EVENT_IDENTIFIER("WarehouseRollupEvent"),
        MATERIAL_CERTIFICATION_ROLLUP_EVENT_IDENTIFIER("MaterialCertificationsRollupEvent"),
        PRODUCT_PART_ROLLUP_RULE("ProductPartRollupRule"),
        FINISHED_PRODUCT_PART_ROLLUP_RULE("FinishedProductPartRollupRule"),
        CUP_RE_SHIPPER_ROLLUP_RULE("CUPReshipperRollupRule"),
        EBOM_CHILDREN("EBOMChildren"),
        EBOM_PARENT("EBOMParent"),
        ROLLUP_PROPERTIES_PATH("/var/opt/gplm/scripts/RollUp/RollUp.properties"),
        ROLLUP_LOGGER_PROPERTIES_PATH("./resources/config/FPPPerformRollupCron-log4j.properties"),
        MARK_FOR_ROLLUP_LOGGER_PROPERTIES_PATH("./resources/config/FPPMarkingCron-log4j.properties"),
        PRE_RELEASE_ROLLUP_LOGGER_PROPERTIES_PATH("./resources/config/FPPAttributeRollupCron-log4j.properties"),
        ROLLUP_RULE_CONFIG_XML_PAGE("pgRollupRuleConfig"),
        IDENTIFIER_FINISHED_PRODUCTPART_RULE("finished-product-part"),
        IDENTIFIER_PRODUCTPART_RULE("product-part"),
        IDENTIFIER_DS_RULE("dynamic-subscription"),
        IDENTIFIER_CHILDPRODUCTPARTS_RULE("child-of-product-part"),
        IDENTIFIER_PRODUCEDBY("producing-formula"),
        IDENTIFIER_MASTER_PRODUCT_PART("master-product-part"),
        IDENTIFIER_SUBSTITUTE_ALTERNATE("substitute-alternate"),
        IDENTIFIER_ALTERNATES("alternate"),
        IDENTIFIER_COP_IN_COP("cop-in-cop"),
        IDENTIFIER_FINISHED_PRODUCT("finished-product"),
        IDENTIFIER_PAP("pap"),
        IDENTIFIER_FAB("fab"),
        IDENTIFIER_FPP_IN_FPP("fpp-in-fpp"),
        IDENTIFIER_SHIPPABLE_HALB("shippable-halb-fpp"),
        IDENTIFIER_CUP_RESHIPPER("re-shipper"),
        IDENTIFIER_PSUB("psub"),
        IDENTIFIER_COPS_SUBSTITUTE("substitute-cop-child"),
        // Modified by DSM (Sogeti) for 22x.02 (May CW) - REQ 46276 - Start
        IDENTIFIER_IP_SUBSTITUTE("substitute-ip-child"),
        IDENTIFIER_IP_IN_IP("ip-in-ip"),
        // Modified by DSM (Sogeti) for 22x.02 (May CW) - REQ 46276 - End
        IDENTIFIER_PRODUCT_PARTS_SUBSTITUTES("substitute-product-part"),
        IDENTIFIER_COP_BULK("cop-bulk-as-product-part"),
        KEY_ALTERNATE("Alternate"),
        KEY_SUBSTITUTE("Substitute"),
        RUNNING_ENV("RUNNING_ENV"),
        MATRIX_HOST("LOGIN_MATRIX_HOST"),
        CONTEXT_USER("CONTEXT_USER"),
        CONTEXT_PASSWORD("CONTEXT_PASSWORD"),
        FPP_MARKING_CONFIG_OBJECT_NAME("FPPMARKING_CONFIG_OBJECT_NAME"),
        PERFORM_ROLLUP_CONFIG_OBJECT_NAME("PERFORMROLLUP_CONFIG_OBJECT_NAME"),
        PERFORM_ATTRIBUTE_ROLLUP_CONFIG_OBJECT_NAME("PERFORMATTRIBUTEROLLUP_CONFIG_OBJECT_NAME"),
        SENDING_MAIL_FAILED("SENDING_MAIL_FAILED"),
        MAIL_SUBJECT_MARK_FPPS_FOR_ROLLUP("MAIL_SUBJECT_MARKFPPSFORROLLUP"),
        MAIL_SUBJECT_PROCESS_FPP_ROLLUP("MAIL_SUBJECT_PROCESSFPPROLLUP"),
        MAIL_SUBJECT_ATTRIBUTE_ROLLUP("MAIL_SUBJECT_ATTRIBUTEROLLUP"),
        MAIL_MESSAGE_MARK_FPPS_FOR_ROLLUP("MAIL_MESSAGE_MARKFPPSFORROLLUP"),
        MAIL_MESSAGE_PROCESS_FPP_ROLLUP("MAIL_MESSAGE_PROCESSFPPROLLUP"),
        MAIL_MESSAGE_ATTRIBUTE_ROLLUP("MAIL_MESSAGE_ATTRIBUTEROLLUP"),
        PAGE_FILE_NAME("PAGEFILE_NAME"),
        CTRLM1_CRON_JOB_NAME("CTRLM1_CRON_JOB_NAME"),
        CTRLM2_CRON_JOB_NAME("CTRLM2_CRON_JOB_NAME"),
        CTRLM3_CRON_JOB_NAME("CTRLM3_CRON_JOB_NAME"),
        FPP_ATTRIBUTE_ROLLUP_INPUTFILE_PATH("FPPATTRIBUTEROLLUP.INPUTFILE.PATH"),
        FPP_ATTRIBUTE_ROLLUP_INPUTFILE_NAME("FPPATTRIBUTEROLLUP.INPUTFILE.NAME"),
        FPP_ATTRIBUTE_ROLLUP_INPUTFILE_EXTENSION("FPPATTRIBUTEROLLUP.INPUTFILE.EXTENSION"),
        DEFAULT_QUANTITY_VALUE("DEFAULT.QUANTITY.VALUE"),
        NUMBER_OF_BATTERIES_SHIPPED_OUTSIDE_DEVICE_DEFAULT_VALUE("NUMBER.OF.BATTERIES.SHIPPED.OUTSIDE.DEVICE.DEFAULT.VALUE"),
        NUMBER_OF_BATTERIES_SHIPPED_INSIDE_DEVICE_DEFAULT_VALUE("NUMBER.OF.BATTERIES.SHIPPED.INSIDE.DEVICE.DEFAULT.VALUE"),
        NUMBER_OF_BATTERIES_REQUIRED_DEFAULT_VALUE("NUMBER.OF.BATTERIES.REQUIRED.DEFAULT.VALUE"),
        ROLLUP_EVENT_WAREHOUSE_CLASSIFICATION("pgPLIWarehousingClassification"),
        SYMBOL_NOT("!"),
        SYMBOL_NOT_EQUALS("!="),
        SYMBOL_COMMA(","),
        SYMBOL_DOT("."),
        EXECUTION_TYPE_MANUAL("manual"),
        EXECUTION_TYPE_CTRLM("ctrlm"),
        RELATIONSHIP("relationship"),
        ZERO("0"),
        BOM_TYPE("BOM_TYPE"),
        SCHEMA_TYPE_ATTRIBUTE("attribute"),
        ROLLUP_PHYS_CHEM_CONFIG_PAGE("pgRollupPhysChemConfig"),
        FEATURE_NAME_STRUCTURE_COPY("StructureCopy"),
        SYMBOL_COLON(":"),
        NO_SPECIAL_STORAGE_REQUIRED("No Special Storage Required"),
        VALUE_YES("YES"),
        PRODUCED_BY_OID("producedByOid"),
        PRODUCT_OID("productOid"),
        NON_SUBSTITUTE_OID("nonSubstituteOid"),
        EVENT_ATTRIBUTE_MAP("eventAttributeMap"),

        EXCEPTION_OCCURRED("Exception Occurred @@@@@@@@@@@@@@@@@@@@@@@@@@@"),
        SUFFIX_INPUT_VALUE(".inputvalue"),
        ERROR_LOADING_CHILD_EXPANSION_BEAN("Error loading Child Expansion Bean"),
        NOT_POWERED("Not Powered"),
        //Added by DSM(Sogeti) for 2018x.6 Defect #40528 - Starts
        DPP_ROLLUP_QUANTITY("DPPRollupQuantity"),
        POWERSOURCE_ROLLUP("Rollup"),
        //Added by DSM(Sogeti) for 2018x.6 Defect #40528 - Ends
        FORWARD_ARROWS(" >> "),
        CIRCULAR_REFERENCE_MESSAGE("Circular Reference -> "),
        CIRCULAR_HISTORY_UPDATE("Circular information updated on object history"),
        DOUBLE_VALUE_ONE("1.0"),
        CTRLM_CIRCULAR_CHECK("FPPROLLUP_CTRLM_CIRCULAR_CHECK"),
        TABLE_CONFIG("tableconfig"),
        COLUMN_CONFIG("columnconfig"),
        SELECT_TYPE("selecttype"),
        SELECTABLE("selectable"),
        MULTIVALUED("multivalued"),
        BUSINESS_OBJECT("businessobject"),
        PRODUCT_DATA("productData"),
        TYPES("types"),
        FROM("from"),
        TO("to"),
        OBJECT_WHERE("objectwhere"),
        REL_WHERE("relwhere"),
        LIMIT("limit"),
        EVENT_NAME("rollupevent"),
        ROLLUP_TYPE("rolluptype"),
        MANUAL_PROGRAM("com.pg.dsm.rollup_event.RollupManualClient"),
        MANUAL_METHOD("performRollup"),
        //Added by DSM(Sogeti) for 2018x.6 Defect #45404 - Starts
        ROUND_DPP_QTY("RoundDPPQuantity"),
        //Added by DSM(Sogeti) for 2018x.6 Defect #45404 - Ends
        ALLOWED_TYPE_FOR_CIRCULAR_CHECK("ALLOWED_TYPES_FOR_CIRCULAR_CHECK"),
        // Added (Sogeti) for Requirement (39920) 2018x.6 May CW 2022 Release - Start
        PACKAGING_CERTIFICATION_ROLLUP_CALCULATE_JOB_CIRCULAR_CHECK_ALLOWED_TYPES("PACKAGING.CERTIFICATION.ROLLUP.CALCULATE.JOB.CIRCULAR.CHECK.ALLOWED.TYPES"),
        PACKAGING_CERTIFICATION_ROLLUP_MARKING_JOB_CIRCULAR_CHECK_ALLOWED_TYPES("PACKAGING.CERTIFICATION.ROLLUP.MARKING.JOB.CIRCULAR.CHECK.ALLOWED.TYPES"),
        PACKAGING_CERTIFICATION_ROLLUP_MARKING_JOB_PERFORM_CIRCULAR_CHECK("PACKAGING.CERTIFICATION.ROLLUP.MARKING.JOB.PERFORM.CIRCULAR.CHECK"),
        PACKAGING_CERTIFICATION_ROLLUP_CALCULATE_JOB_PERFORM_CIRCULAR_CHECK("PACKAGING.CERTIFICATION.ROLLUP.CALCULATE.JOB.PERFORM.CIRCULAR.CHECK"),
        PACKAGING_CERTIFICATION_ROLLUP_CALCULATE_JOB_STUCK_EMAIL_MESSAGE("PACKAGING.CERTIFICATION.ROLLUP.CALCULATE.JOB.STUCK.EMAIL.MESSAGE"),
        PACKAGING_CERTIFICATION_ROLLUP_CALCULATE_JOB_STUCK_EMAIL_SUBJECT("PACKAGING.CERTIFICATION.ROLLUP.CALCULATE.JOB.STUCK.EMAIL.SUBJECT"),
        PACKAGING_CERTIFICATION_ROLLUP_MARKING_JOB_STUCK_EMAIL_MESSAGE("PACKAGING.CERTIFICATION.ROLLUP.MARKING.JOB.STUCK.EMAIL.MESSAGE"),
        PACKAGING_CERTIFICATION_ROLLUP_MARKING_JOB_STUCK_EMAIL_SUBJECT("PACKAGING.CERTIFICATION.ROLLUP.MARKING.JOB.STUCK.EMAIL.SUBJECT"),
        PACKAGING_CERTIFICATION_ROLLUP_CALCULATE_CONFIG_OBJECT_NAME("PACKAGING.CERTIFICATION.ROLLUP.CALCULATE.CONFIG.OBJECT.NAME"),
        PACKAGING_CERTIFICATION_ROLLUP_MARKING_CONFIG_OBJECT_NAME("PACKAGING.CERTIFICATION.ROLLUP.MARKING.CONFIG.OBJECT.NAME"),
        PACKAGING_CERTIFICATION_ROLLUP_MARKING_ALLOWED_TYPES("PACKAGING.CERTIFICATION.ROLLUP.MARKING.ALLOWED.TYPES"),
        PACKAGING_CERTIFICATION_ROLLUP_MARKING_ALLOWED_TYPES_FILTER("PACKAGING.CERTIFICATION.ROLLUP.MARKING.ALLOWED.TYPES.FILTER"),
        PACKAGING_CERTIFICATION_ROLLUP_MARKING_JOB_SEARCH_MQL_QUERY("PACKAGING.CERTIFICATION.ROLLUP.MARKING.JOB.SEARCH.MQL.QUERY"),
        PACKAGING_CERTIFICATION_ROLLUP_CALCULATE_JOB_SEARCH_MQL_QUERY("PACKAGING.CERTIFICATION.ROLLUP.CALCULATE.JOB.SEARCH.MQL.QUERY");
        // Added (Sogeti) for Requirement (39920) 2018x.6 May CW 2022 Release - End

        private final String name;

        Basic(String name) {
            this.name = name;
        }

        public String getValue() {
            return this.name;
        }
    }

    public enum Type {
        PART("type_Part"),
        COPY_LIST("type_CopyList"),
        WARNING_STATEMENTS_COPY("type_WarningStatementsCopy"),
        WARNING_STATEMENTS_MASTER_COPY("type_WarningStatementsMasterCopy"),
        WAREHOUSING_CLASSIFICATION("type_pgPLIWarehousingClassification"),

        // Added (Sogeti) for Requirement (39920) 2018x.6 May CW 2022 Release - Start
        PLI_PACKAGING_MATERIAL_CERTIFICATION("type_pgPLIPackagingMaterialCertification"),
        PACKAGING_MATERIAL_PART("type_PackagingMaterialPart"),
        INTERNAL_MATERIAL("type_InternalMaterial");
        // Added (Sogeti) for Requirement (39920) 2018x.6 May CW 2022 Release - End

        private final String name;

        Type(String name) {
            this.name = name;
        }

        public String getName(Context context) {
            return PropertyUtil.getSchemaProperty(context, this.name);
        }
    }

    public enum Policy { // Added (Sogeti) for Requirement (39920) 2018x.6 May CW 2022 Release - Start
        MANUFACTURER_EQUIVALENT("policy_ManufacturerEquivalent"),
        SUPPLIER_EQUIVALENT("policy_SupplierEquivalent");
        private final String name;

        Policy(String name) {
            this.name = name;
        }

        public String getName(Context context) {
            return PropertyUtil.getSchemaProperty(context, this.name);
        }
    } // Added (Sogeti) for Requirement (39920) 2018x.6 May CW 2022 Release - End

    public enum Relationship {
        EBOM("relationship_EBOM"),
        OWNING_PRODUCT_LINE("relationship_OwningProductLine"),
        MOS_ROLLEDUP_REGISTRATION("relationship_pgMOSRolledUpRegistration"),
        INGREDIENT_STATEMENT("relationship_pgIngredientStatement"),
        SMART_LABEL("relationship_pgSmartLabel"),
        REFERENCE_DOCUMENT("relationship_ReferenceDocument"),
        GLOBAL_HARMONIZED_STANDARD("relationship_pgGlobalHarmonizedStandard"),
        ROLLED_UP_GHS("relationship_pgRolledUpGHS"),
        ARTWORK_ASSEMBLY("relationship_ArtworkAssembly"),
        COPY_LIST_ARTWORK_MASTER("relationship_CopyListArtworkMaster"),
        WAREHOUSING_CLASSIFICATION("relationship_pgToWarehousingClassification"),
        DANGEROUS_GOODS("relationship_pgDangerousgoods"),

        // Added (Sogeti) for Requirement (39920) 2018x.6 May CW 2022 Release - Start
        PLI_PACKAGING_MATERIAL_CERTIFICATIONS("relationship_pgPLIPackagingCertifications"),
        SUPPLIER_EQUIVALENT("relationship_SupplierEquivalent"),
        MANUFACTURER_EQUIVALENT("relationship_ManufacturerEquivalent"),
        COMPONENT_MATERIAL("relationship_ComponentMaterial");
        // Added (Sogeti) for Requirement (39920) 2018x.6 May CW 2022 Release - End
        private final String name;

        Relationship(String name) {
            this.name = name;
        }

        public String getName(Context context) {
            return PropertyUtil.getSchemaProperty(context, this.name);
        }
    }

    public enum Attribute {
        QUANTITY("attribute_Quantity"),
        UOM("attribute_UnitofMeasure"),
        ARE_BATTERIES_INCLUDED("attribute_pgAreBatteriesIncluded"),
        ARE_BATTERIES_BUILTIN("attribute_pgAreBatteriesBuiltIn"),
        ARE_BATTERIES_REQUIRED("attribute_pgAreBatteriesRequired"),
        SET_PRODUCT_NAME("attribute_pgSetProductName"),
        DPP_QTY_PER_COP("attribute_pgDPPQtyperCOP"),
        MOS_ROLLED_UP_ASSEMBLY_TYPE("attribute_pgMOSRolledupAssembleType"),
        PHYSICAL_CHEMICAL_ROLL_UP_FLAG("attribute_pgPhysicalChemicalRollupFlag"),
        BATTERY_ROLLUP_FLAG("attribute_pgBatteryRollupFlag"),
        MARKET_REGISTRATION_ROLLUP_FLAG("attribute_pgRegistrationRollupFlag"),
        INGREDIENT_STATEMENT_ROLLUP_FLAG("attribute_pgIngredientStatementRollupFlag"),
        NUMBER_OF_BATTERIES_REQUIRED("attribute_pgNumberOfBatteriesRequired"),
        CALCULATE_FOR_ROLLUP("attribute_pgCalculateforRollup"),
        PUBLISH_DGC_TO_SAP("attribute_pgPublishDGCToSAP"),
        //Added by DSM for 22x CW-05 for Requirement 49480-Start
        PUBLISH_INGREDIENTTRANSPARENCY_TO_SAP("attribute_pgPublishIngredientTransparency"),
        //Added by DSM for 22x CW-05 for Requirement 49480-End
        MARK_FOR_ROLLUP("attribute_pgMarkforRollup");

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

}

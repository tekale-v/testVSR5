package com.pg.dsm.sapview.enumeration;

public enum SAPViewConstant {
    PARENT_EBOM_LIST("parentEBOMList"),
    ALTERNATE_LIST("alternateList"),
    SUBSTITUTE_LIST("substituteList"),
    RESHIPPER("Reshipper"),
    IDENTIFIER_DUMMY("Dummy"),
    IDENTIFIER_ASSEMBLY_TYPE("assemblyType"),
    //Added by DSM(Sogeti) for 2018x.6 Dec CW SAP Requirement #40805 - Starts
    CTRLM_PROPERTIES_FILE("sapbom/BOMeDelivery.properties"),
    ATTRIBUTE_PGBOMEDELIVERY("pgBOMeDelivery"),
    ATTRIBUTE_PGBOMEDELIVERYPARENT("pgBOMeDeliveryParent"),
    CONFIG_OBJECT_NAME("pgBOMeDeliveryConfiguration"),
    SAP_BOM_EDELIVERY_ATTR_INTERFACE("pgBOMeDeliveryInterface"),
    //Added by DSM(Sogeti) for 2018x.6 Dec CW SAP Requirement #40805 - Ends
    //Added by (DSM Sogeti) for (2018x.6 Apr CW 2022) SNC-RFC Requirements ID (42702,42703,42704) - Starts.
    BOM_EDELIVERY_CONFIG_PARAMETER_ISSUE("SAP Client Number | SAP System Number is not configured on config object"),
    BOM_EDELIVERY_CONFIG_OBJECT_MAP_ISSUE("Querying config object resulted in null map"),
    BOM_EDELIVERY_CONFIG_OBJECT_NOT_FOUND("SAP BOM eDelivery Config Object not found"),
    BOM_PROCESSED_SUCCESS_MESSAGE("BOM Successfully Processed"), 
    BOM_EDELIVERY_MAIL_MESSAGE_BODY("Hi,\n\nSAP BOM eDelivery cron job is stuck and is not executed properly for last 2 scheduled runs. Please look into the issue.\n\nThanks."),
    BOM_EDELIVERY_MAIL_MESSAGE_SUBJECT("SAP BOM eDelivery scheduled job problem"),
    //Added by (DSM Sogeti) for (2018x.6 Apr CW 2022) SNC-RFC Requirements ID (42702,42703,42704) - Ends
	// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
    KEY_COMPONENT_TYPES_LIST("__ComponentTypesList__"),
    KEY_PARENT("__Parent__"),
    KEY_HAS_PARENT("__hasParent__"),
    KEY_HAS_COMPONENT("__hasComponent__"),
    KEY_COMPONENTS("_Components__"),
    KEY_PARENT_ID("__ParentID__"),
    KEY_PARENT_TYPE("__ParentType__"),
    KEY_IS_PARENT_COMPLEX_BOM("__isParentComplexBOM__"),
    KEY_IS_SELF_COMPLEX_BOM("__isSelfComplexBOM__");
    // Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
    private final String value;

    /**
     * Constructor
     *
     * @param value - String
     * @since DSM 2018x.5
     */
    SAPViewConstant(String value) {
        this.value = value;
    }

    /**
     * Method to get value
     *
     * @since DSM 2018x.5
     */
    public String getValue() {
        return value;
    }
}

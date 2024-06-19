package com.pg.dsm.preference.models;

import java.util.Map;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.pg.dsm.preference.enumeration.PreferenceConstants;

import matrix.db.Context;

public class DSMUserPreferenceConfig {
    private static final Logger logger = Logger.getLogger(DSMUserPreferenceConfig.class.getName());
    String id;
    String name;

    String allowedTypesForPartType;
    String allowedTypesForPhase;
    String allowedTypesForManufacturingStatus;
    String allowedTypesForPlantConnection;
    String allowedTypesForShareWithMembers;
    String allowedTypesForReleaseCriteria;
    String allowedTypesForPackagingMaterial;
    String allowedTypesForSegment;
    String allowedTypesForClass;
    String allowedTypesForReportedFunction;

    String allowedTypesForPlantConnectionForEquivalent;
    String allowedTypesForShareWithMembersForEquivalent;

    String allowedTypesForPlantConnectionForTechSpec;
    String allowedTypesForShareWithMembersForTechSpec;

    String allowedTypesForPlantConnectionForProductPart;
    String allowedTypesForShareWithMembersForProductPart;

    String allowedTypesAndDefaultValueForAuthorizedToUse;
    String allowedTypesAndDefaultValueForAuthorizedToProduce;
    String allowedTypesAndDefaultValueForAuthorized;
    String allowedTypesAndDefaultValueForActivated;

    String skipPartViaDesigner;

    String irmRouteTaskDueDay;

    String allowedPartTypesForPackagingPreference;
    String allowedPartTypesForProductPreference;
    String allowedPartTypesForRawMaterialPreference;


    /**
     * @param context
     * @param objectMap
     */
    public DSMUserPreferenceConfig(Context context, Map<Object, Object> objectMap) {
        this.id = (String) objectMap.get(DomainConstants.SELECT_ID);
        this.name = (String) objectMap.get(DomainConstants.SELECT_NAME);

        this.allowedTypesForPartType = (String) objectMap.get(PreferenceConstants.Attribute.ALLOWED_TYPES_FOR_PART_TYPE.getSelect(context));
        this.allowedTypesForPhase = (String) objectMap.get(PreferenceConstants.Attribute.ALLOWED_TYPES_FOR_PHASE.getSelect(context));
        this.allowedTypesForManufacturingStatus = (String) objectMap.get(PreferenceConstants.Attribute.ALLOWED_TYPES_FOR_MANUFACTURING_STATUS.getSelect(context));
        this.allowedTypesForPlantConnection = (String) objectMap.get(PreferenceConstants.Attribute.ALLOWED_TYPES_FOR_PLANT_CONNECTION.getSelect(context));
        this.allowedTypesForShareWithMembers = (String) objectMap.get(PreferenceConstants.Attribute.ALLOWED_TYPES_FOR_SHARE_WITH_MEMBERS.getSelect(context));
        this.allowedTypesForReleaseCriteria = (String) objectMap.get(PreferenceConstants.Attribute.ALLOWED_TYPES_FOR_RELEASE_CRITERIA.getSelect(context));
        this.allowedTypesForPackagingMaterial = (String) objectMap.get(PreferenceConstants.Attribute.ALLOWED_TYPES_FOR_PACKAGING_MATERIAL_TYPE.getSelect(context));
        this.allowedTypesForSegment = (String) objectMap.get(PreferenceConstants.Attribute.ALLOWED_TYPES_FOR_SEGMENT.getSelect(context));
        this.allowedTypesForClass = (String) objectMap.get(PreferenceConstants.Attribute.ALLOWED_TYPES_FOR_CLASS.getSelect(context));
        this.allowedTypesForReportedFunction = (String) objectMap.get(PreferenceConstants.Attribute.ALLOWED_TYPES_FOR_REPORTED_FUNCTION.getSelect(context));
        this.irmRouteTaskDueDay = (String) objectMap.get(PreferenceConstants.Attribute.IRM_ROUTE_TASK_DUE_DAY.getSelect(context));

        this.allowedTypesForPlantConnectionForEquivalent = (String) objectMap.get(PreferenceConstants.Attribute.ALLOWED_TYPES_FOR_PLANT_CONNECTION_FOR_EQUIVALENT.getSelect(context));
        this.allowedTypesForShareWithMembersForEquivalent = (String) objectMap.get(PreferenceConstants.Attribute.ALLOWED_TYPES_FOR_SHARE_WITH_MEMBERS_CONNECTION_FOR_EQUIVALENT.getSelect(context));

        this.allowedTypesForPlantConnectionForTechSpec = (String) objectMap.get(PreferenceConstants.Attribute.ALLOWED_TYPES_FOR_PLANT_CONNECTION_FOR_TECH_SPEC.getSelect(context));
        this.allowedTypesForShareWithMembersForTechSpec = (String) objectMap.get(PreferenceConstants.Attribute.ALLOWED_TYPES_FOR_SHARE_WITH_MEMBERS_CONNECTION_FOR_TECH_SPEC.getSelect(context));

        this.allowedTypesForPlantConnectionForProductPart = (String) objectMap.get(PreferenceConstants.Attribute.ALLOWED_TYPES_FOR_PLANT_CONNECTION_FOR_PRODUCT_PART.getSelect(context));
        this.allowedTypesForShareWithMembersForProductPart = (String) objectMap.get(PreferenceConstants.Attribute.ALLOWED_TYPES_FOR_SHARE_WITH_MEMBERS_CONNECTION_FOR_PRODUCT_PART.getSelect(context));

        this.allowedTypesAndDefaultValueForAuthorizedToUse = (String) objectMap.get(PreferenceConstants.Attribute.ALLOWED_TYPES_AND_DEFAULT_VALUE_FOR_AUTHORIZED_TO_USE.getSelect(context));
        this.allowedTypesAndDefaultValueForAuthorizedToProduce = (String) objectMap.get(PreferenceConstants.Attribute.ALLOWED_TYPES_AND_DEFAULT_VALUE_FOR_AUTHORIZED_TO_PRODUCE.getSelect(context));
        this.allowedTypesAndDefaultValueForAuthorized = (String) objectMap.get(PreferenceConstants.Attribute.ALLOWED_TYPES_AND_DEFAULT_VALUE_FOR_AUTHORIZED.getSelect(context));
        this.allowedTypesAndDefaultValueForActivated = (String) objectMap.get(PreferenceConstants.Attribute.ALLOWED_TYPES_AND_DEFAULT_VALUE_FOR_ACTIVATED.getSelect(context));

        this.skipPartViaDesigner = (String) objectMap.get(PreferenceConstants.Attribute.SKIP_PART_VIA_DESIGNER.getSelect(context));

        this.allowedPartTypesForPackagingPreference = (String) objectMap.get(PreferenceConstants.Attribute.ALLOWED_PART_TYPES_FOR_PACKAGING_PREFERENCE.getSelect(context));
        this.allowedPartTypesForProductPreference = (String) objectMap.get(PreferenceConstants.Attribute.ALLOWED_PART_TYPES_FOR_PRODUCT_PREFERENCE.getSelect(context));
        this.allowedPartTypesForRawMaterialPreference = (String) objectMap.get(PreferenceConstants.Attribute.ALLOWED_PART_TYPES_FOR_RAW_MATERIAL_PREFERENCE.getSelect(context));
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAllowedTypesForPlantConnection() {
        return allowedTypesForPlantConnection;
    }

    public String getAllowedTypesForShareWithMembers() {
        return allowedTypesForShareWithMembers;
    }

    public String getAllowedTypesForReleaseCriteria() {
        return allowedTypesForReleaseCriteria;
    }

    public String getAllowedTypesForPackagingMaterial() {
        return allowedTypesForPackagingMaterial;
    }

    public String getAllowedTypesForSegment() {
        return allowedTypesForSegment;
    }

    public String getAllowedTypesForClass() {
        return allowedTypesForClass;
    }

    public String getAllowedTypesForReportedFunction() {
        return allowedTypesForReportedFunction;
    }

    public String getAllowedTypesForPartType() {
        return allowedTypesForPartType;
    }

    public String getAllowedTypesForPhase() {
        return allowedTypesForPhase;
    }

    public String getAllowedTypesForManufacturingStatus() {
        return allowedTypesForManufacturingStatus;
    }

    public String getAllowedTypesForPlantConnectionForEquivalent() {
        return allowedTypesForPlantConnectionForEquivalent;
    }

    public String getAllowedTypesForShareWithMembersForEquivalent() {
        return allowedTypesForShareWithMembersForEquivalent;
    }

    public String getAllowedTypesForPlantConnectionForTechSpec() {
        return allowedTypesForPlantConnectionForTechSpec;
    }

    public String getAllowedTypesForShareWithMembersForTechSpec() {
        return allowedTypesForShareWithMembersForTechSpec;
    }

    public String getAllowedTypesForPlantConnectionForProductPart() {
        return allowedTypesForPlantConnectionForProductPart;
    }

    public String getAllowedTypesForShareWithMembersForProductPart() {
        return allowedTypesForShareWithMembersForProductPart;
    }

    public String getAllowedTypesAndDefaultValueForAuthorizedToUse() {
        return allowedTypesAndDefaultValueForAuthorizedToUse;
    }

    public String getAllowedTypesAndDefaultValueForAuthorizedToProduce() {
        return allowedTypesAndDefaultValueForAuthorizedToProduce;
    }

    public String getAllowedTypesAndDefaultValueForAuthorized() {
        return allowedTypesAndDefaultValueForAuthorized;
    }

    public String getAllowedTypesAndDefaultValueForActivated() {
        return allowedTypesAndDefaultValueForActivated;
    }

    public String getSkipPartViaDesigner() {
        return skipPartViaDesigner;
    }

    public String getIrmRouteTaskDueDay() {
        return irmRouteTaskDueDay;
    }

    public String getAllowedPartTypesForPackagingPreference() {
        return allowedPartTypesForPackagingPreference;
    }

    public String getAllowedPartTypesForProductPreference() {
        return allowedPartTypesForProductPreference;
    }

    public String getAllowedPartTypesForRawMaterialPreference() {
        return allowedPartTypesForRawMaterialPreference;
    }

    @Override
    public String toString() {
        return "DSMUserPreferenceConfig{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", allowedTypesForPartType='" + allowedTypesForPartType + '\'' +
                ", allowedTypesForPhase='" + allowedTypesForPhase + '\'' +
                ", allowedTypesForManufacturingStatus='" + allowedTypesForManufacturingStatus + '\'' +
                ", allowedTypesForPlantConnection='" + allowedTypesForPlantConnection + '\'' +
                ", allowedTypesForShareWithMembers='" + allowedTypesForShareWithMembers + '\'' +
                ", allowedTypesForReleaseCriteria='" + allowedTypesForReleaseCriteria + '\'' +
                ", allowedTypesForPackagingMaterial='" + allowedTypesForPackagingMaterial + '\'' +
                ", allowedTypesForSegment='" + allowedTypesForSegment + '\'' +
                ", allowedTypesForClass='" + allowedTypesForClass + '\'' +
                ", allowedTypesForReportedFunction='" + allowedTypesForReportedFunction + '\'' +
                ", allowedTypesForPlantConnectionForEquivalent='" + allowedTypesForPlantConnectionForEquivalent + '\'' +
                ", allowedTypesForShareWithMembersForEquivalent='" + allowedTypesForShareWithMembersForEquivalent + '\'' +
                ", allowedTypesForPlantConnectionForTechSpec='" + allowedTypesForPlantConnectionForTechSpec + '\'' +
                ", allowedTypesForShareWithMembersForTechSpec='" + allowedTypesForShareWithMembersForTechSpec + '\'' +
                ", allowedTypesForPlantConnectionForProductPart='" + allowedTypesForPlantConnectionForProductPart + '\'' +
                ", allowedTypesForShareWithMembersForProductPart='" + allowedTypesForShareWithMembersForProductPart + '\'' +
                ", allowedTypesAndDefaultValueForAuthorizedToUse='" + allowedTypesAndDefaultValueForAuthorizedToUse + '\'' +
                ", allowedTypesAndDefaultValueForAuthorizedToProduce='" + allowedTypesAndDefaultValueForAuthorizedToProduce + '\'' +
                ", allowedTypesAndDefaultValueForAuthorized='" + allowedTypesAndDefaultValueForAuthorized + '\'' +
                ", allowedTypesAndDefaultValueForActivated='" + allowedTypesAndDefaultValueForActivated + '\'' +
                ", skipPartViaDesigner='" + skipPartViaDesigner + '\'' +
                ", irmRouteTaskDueDay='" + irmRouteTaskDueDay + '\'' +
                ", allowedPartTypesForPackagingPreference='" + allowedPartTypesForPackagingPreference + '\'' +
                ", allowedPartTypesForProductPreference='" + allowedPartTypesForProductPreference + '\'' +
                ", allowedPartTypesForRawMaterialPreference='" + allowedPartTypesForRawMaterialPreference + '\'' +
                '}';
    }
}

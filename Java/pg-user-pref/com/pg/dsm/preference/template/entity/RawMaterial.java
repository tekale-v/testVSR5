package com.pg.dsm.preference.template.entity;

import java.util.Map;

import com.pg.dsm.preference.enumeration.UserPreferenceTemplateConstants;
import com.pg.dsm.preference.template.DSMUserPreferenceTemplateUtil;

public class RawMaterial {
    String category;
    String type;
    String phase;
    String mfgStatus;
    String releaseCriteria;
    String classType;
    String reportedFunction;
    String segment;
    String businessArea;
    String categoryPlatform;
    String materialFunction;

    boolean hasRawMaterial;

    public RawMaterial(Map<Object, Object> objectMap) {
        // Part Category
        UserPreferenceTemplateConstants.RawMaterialFields categoryField = UserPreferenceTemplateConstants.RawMaterialFields.getByFieldIdentifier("PartCategory");
        this.category = (String) objectMap.get(categoryField.getFieldName());

        // Part Type
        UserPreferenceTemplateConstants.RawMaterialFields typeField = UserPreferenceTemplateConstants.RawMaterialFields.getByFieldIdentifier("PartType");
        this.type = (String) objectMap.get(typeField.getFieldName());

        // Phase
        UserPreferenceTemplateConstants.RawMaterialFields phaseField = UserPreferenceTemplateConstants.RawMaterialFields.getByFieldIdentifier("Phase");
        this.phase = (String) objectMap.get(phaseField.getFieldName());

        // Mfg Status
        UserPreferenceTemplateConstants.RawMaterialFields mfgStatusField = UserPreferenceTemplateConstants.RawMaterialFields.getByFieldIdentifier("ManufacturingStatus");
        this.mfgStatus = (String) objectMap.get(mfgStatusField.getFieldName());

        // Release Criteria
        UserPreferenceTemplateConstants.RawMaterialFields releaseCriteriaField = UserPreferenceTemplateConstants.RawMaterialFields.getByFieldIdentifier("ReleaseCriteria");
        this.releaseCriteria = (String) objectMap.get(releaseCriteriaField.getFieldName());

        // Class (stored as name on person property) - single selection
        UserPreferenceTemplateConstants.RawMaterialFields classTypeField = UserPreferenceTemplateConstants.RawMaterialFields.getByFieldIdentifier("ClassName");
        this.classType = (String) objectMap.get(classTypeField.getFieldPhysicalID());

        // Reported Function (stored as id on person property) - single selection
        UserPreferenceTemplateConstants.RawMaterialFields reportedFunctionField = UserPreferenceTemplateConstants.RawMaterialFields.getByFieldIdentifier("ReportedFunction");
        this.reportedFunction = (String) objectMap.get(reportedFunctionField.getFieldPhysicalID());

        // Segment (stored as id on person property) - single selection
        UserPreferenceTemplateConstants.RawMaterialFields segmentField = UserPreferenceTemplateConstants.RawMaterialFields.getByFieldIdentifier("Segment");
        this.segment = (String) objectMap.get(segmentField.getFieldPhysicalID());

        // Business Area (stored as physical id on person property) - multiple selection
        UserPreferenceTemplateConstants.RawMaterialFields businessAreaField = UserPreferenceTemplateConstants.RawMaterialFields.getByFieldIdentifier("BusinessArea");
        this.businessArea = (String) objectMap.get(businessAreaField.getFieldPhysicalID());

        // Product Category Platform (stored as physical id on person property) - multiple selection
        UserPreferenceTemplateConstants.RawMaterialFields productCategoryPlatformField = UserPreferenceTemplateConstants.RawMaterialFields.getByFieldIdentifier("ProductCategoryPlatform");
        this.categoryPlatform = (String) objectMap.get(productCategoryPlatformField.getFieldPhysicalID());

        // Material Function (stored as id on person property)
        UserPreferenceTemplateConstants.RawMaterialFields productComplianceField = UserPreferenceTemplateConstants.RawMaterialFields.getByFieldIdentifier("MaterialFunction");
        this.materialFunction = (String) objectMap.get(productComplianceField.getFieldPhysicalID());

        this.hasRawMaterial = DSMUserPreferenceTemplateUtil.atleastOneKeyHasValue(objectMap);
    }

    public String getCategory() {
        return category;
    }

    public String getType() {
        return type;
    }

    public String getPhase() {
        return phase;
    }

    public String getMfgStatus() {
        return mfgStatus;
    }

    public String getReleaseCriteria() {
        return releaseCriteria;
    }

    public String getClassType() {
        return classType;
    }

    public String getReportedFunction() {
        return reportedFunction;
    }

    public String getSegment() {
        return segment;
    }

    public String getBusinessArea() {
        return businessArea;
    }

    public String getCategoryPlatform() {
        return categoryPlatform;
    }

    public String getMaterialFunction() {
        return materialFunction;
    }

    public boolean isHasRawMaterial() {
        return hasRawMaterial;
    }

    @Override
    public String toString() {
        return "RawMaterial{" +
                "category='" + category + '\'' +
                ", type='" + type + '\'' +
                ", phase='" + phase + '\'' +
                ", mfgStatus='" + mfgStatus + '\'' +
                ", releaseCriteria='" + releaseCriteria + '\'' +
                ", classType='" + classType + '\'' +
                ", reportedFunction='" + reportedFunction + '\'' +
                ", segment='" + segment + '\'' +
                ", businessArea='" + businessArea + '\'' +
                ", categoryPlatform='" + categoryPlatform + '\'' +
                ", materialFunction='" + materialFunction + '\'' +
                ", hasRawMaterial=" + hasRawMaterial +
                '}';
    }
}

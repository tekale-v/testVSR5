package com.pg.dsm.preference.template.entity;

import java.util.Map;

import com.pg.dsm.preference.enumeration.UserPreferenceTemplateConstants;
import com.pg.dsm.preference.template.DSMUserPreferenceTemplateUtil;

public class Product {
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
    String complianceRequired;

    boolean hasProduct;

    public Product(Map<Object, Object> objectMap) {
        // Part Category
        UserPreferenceTemplateConstants.ProductFields categoryField = UserPreferenceTemplateConstants.ProductFields.getByFieldIdentifier("PartCategory");
        this.category = (String) objectMap.get(categoryField.getFieldName());

        // Part Type
        UserPreferenceTemplateConstants.ProductFields typeField = UserPreferenceTemplateConstants.ProductFields.getByFieldIdentifier("PartType");
        this.type = (String) objectMap.get(typeField.getFieldName());

        // Phase
        UserPreferenceTemplateConstants.ProductFields phaseField = UserPreferenceTemplateConstants.ProductFields.getByFieldIdentifier("Phase");
        this.phase = (String) objectMap.get(phaseField.getFieldName());

        // Mfg Status
        UserPreferenceTemplateConstants.ProductFields mfgStatusField = UserPreferenceTemplateConstants.ProductFields.getByFieldIdentifier("ManufacturingStatus");
        this.mfgStatus = (String) objectMap.get(mfgStatusField.getFieldName());

        // Release Criteria
        UserPreferenceTemplateConstants.ProductFields releaseCriteriaField = UserPreferenceTemplateConstants.ProductFields.getByFieldIdentifier("ReleaseCriteria");
        this.releaseCriteria = (String) objectMap.get(releaseCriteriaField.getFieldName());

        // Class (stored as name on person property) - single selection
        UserPreferenceTemplateConstants.ProductFields classTypeField = UserPreferenceTemplateConstants.ProductFields.getByFieldIdentifier("ClassName");
        this.classType = (String) objectMap.get(classTypeField.getFieldPhysicalID());

        // Reported Function (stored as id on person property) - single selection
        UserPreferenceTemplateConstants.ProductFields reportedFunctionField = UserPreferenceTemplateConstants.ProductFields.getByFieldIdentifier("ReportedFunction");
        this.reportedFunction = (String) objectMap.get(reportedFunctionField.getFieldPhysicalID());

        // Segment (stored as id on person property) - single selection
        UserPreferenceTemplateConstants.ProductFields segmentField = UserPreferenceTemplateConstants.ProductFields.getByFieldIdentifier("Segment");
        this.segment = (String) objectMap.get(segmentField.getFieldPhysicalID());

        // Business Area (stored as physical id on person property) - multiple selection
        UserPreferenceTemplateConstants.ProductFields businessAreaField = UserPreferenceTemplateConstants.ProductFields.getByFieldIdentifier("BusinessArea");
        this.businessArea = (String) objectMap.get(businessAreaField.getFieldPhysicalID());

        // Product Category Platform (stored as physical id on person property) - multiple selection
        UserPreferenceTemplateConstants.ProductFields productCategoryPlatformField = UserPreferenceTemplateConstants.ProductFields.getByFieldIdentifier("ProductCategoryPlatform");
        this.categoryPlatform = (String) objectMap.get(productCategoryPlatformField.getFieldPhysicalID());

        // Product Compliance Required (stored as value on person property)
        UserPreferenceTemplateConstants.ProductFields productComplianceField = UserPreferenceTemplateConstants.ProductFields.getByFieldIdentifier("ProductCompliance");
        this.complianceRequired = (String) objectMap.get(productComplianceField.getFieldPhysicalID());

        this.hasProduct = DSMUserPreferenceTemplateUtil.atleastOneKeyHasValue(objectMap);
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

    public String getComplianceRequired() {
        return complianceRequired;
    }

    public boolean isHasProduct() {
        return hasProduct;
    }

    @Override
    public String toString() {
        return "Product{" +
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
                ", complianceRequired='" + complianceRequired + '\'' +
                ", hasProduct=" + hasProduct +
                '}';
    }
}

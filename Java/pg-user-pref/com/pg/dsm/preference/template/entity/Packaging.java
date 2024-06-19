package com.pg.dsm.preference.template.entity;

import java.util.Map;

import com.pg.dsm.preference.enumeration.UserPreferenceTemplateConstants;
import com.pg.dsm.preference.template.DSMUserPreferenceTemplateUtil;

public class Packaging {
    String category;
    String type;
    String phase;
    String mfgStatus;
    String releaseCriteria;
    String classType;
    String reportedFunction;
    String segment;
    String componentType;
    String materialType;
    String baseUoM;

    boolean hasPackaging;

    public Packaging(Map<Object, Object> objectMap) {
        // Part Category
        UserPreferenceTemplateConstants.PackagingFields categoryField = UserPreferenceTemplateConstants.PackagingFields.getByFieldIdentifier("PartCategory");
        this.category = (String) objectMap.get(categoryField.getFieldName());

        // Part Type
        UserPreferenceTemplateConstants.PackagingFields typeField = UserPreferenceTemplateConstants.PackagingFields.getByFieldIdentifier("PartType");
        this.type = (String) objectMap.get(typeField.getFieldName());

        // Phase
        UserPreferenceTemplateConstants.PackagingFields phaseField = UserPreferenceTemplateConstants.PackagingFields.getByFieldIdentifier("Phase");
        this.phase = (String) objectMap.get(phaseField.getFieldName());

        // Mfg Status
        UserPreferenceTemplateConstants.PackagingFields mfgStatusField = UserPreferenceTemplateConstants.PackagingFields.getByFieldIdentifier("ManufacturingStatus");
        this.mfgStatus = (String) objectMap.get(mfgStatusField.getFieldName());

        // Release Criteria
        UserPreferenceTemplateConstants.PackagingFields releaseCriteriaField = UserPreferenceTemplateConstants.PackagingFields.getByFieldIdentifier("ReleaseCriteria");
        this.releaseCriteria = (String) objectMap.get(releaseCriteriaField.getFieldName());

        // Class (stored as name on person property) - single selection
        UserPreferenceTemplateConstants.PackagingFields classTypeField = UserPreferenceTemplateConstants.PackagingFields.getByFieldIdentifier("ClassName");
        //this.classType = (String) objectMap.get(classTypeField.getFieldName());
        this.classType = (String) objectMap.get(classTypeField.getFieldPhysicalID());


        // Reported Function (stored as id on person property) - single selection
        UserPreferenceTemplateConstants.PackagingFields reportedFunctionField = UserPreferenceTemplateConstants.PackagingFields.getByFieldIdentifier("ReportedFunction");
        //this.reportedFunction = (String) objectMap.get(reportedFunctionField.getFieldName());
        this.reportedFunction = (String) objectMap.get(reportedFunctionField.getFieldPhysicalID());

        // Segment (stored as id on person property) - single selection
        UserPreferenceTemplateConstants.PackagingFields segmentField = UserPreferenceTemplateConstants.PackagingFields.getByFieldIdentifier("Segment");
        //this.segment = (String) objectMap.get(segmentField.getFieldName());
        this.segment = (String) objectMap.get(segmentField.getFieldPhysicalID());

        // Packaging Component Type (stored as name on person property) - single selection
        UserPreferenceTemplateConstants.PackagingFields packagingComponentTypeField = UserPreferenceTemplateConstants.PackagingFields.getByFieldIdentifier("PackagingComponentType");
        //this.componentType = (String) objectMap.get(packagingComponentTypeField.getFieldName());
        this.componentType = (String) objectMap.get(packagingComponentTypeField.getFieldPhysicalID());

        // Packaging Material Type (stored as name on person property) - single selection
        UserPreferenceTemplateConstants.PackagingFields packagingMaterialTypeField = UserPreferenceTemplateConstants.PackagingFields.getByFieldIdentifier("PackagingMaterialType");
        //this.materialType = (String) objectMap.get(packagingMaterialTypeField.getFieldName());
        this.materialType = (String) objectMap.get(packagingMaterialTypeField.getFieldPhysicalID());

        // Base Unit of Measure (stored as name on person property) - single selection
        UserPreferenceTemplateConstants.PackagingFields baseUnitOfMeasureField = UserPreferenceTemplateConstants.PackagingFields.getByFieldIdentifier("BaseUnitOfMeasure");
        //this.baseUoM = (String) objectMap.get(baseUnitOfMeasureField.getFieldName());
        this.baseUoM = (String) objectMap.get(baseUnitOfMeasureField.getFieldPhysicalID());

        this.hasPackaging = DSMUserPreferenceTemplateUtil.atleastOneKeyHasValue(objectMap);
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

    public String getComponentType() {
        return componentType;
    }

    public String getMaterialType() {
        return materialType;
    }

    public String getBaseUoM() {
        return baseUoM;
    }

    public boolean isHasPackaging() {
        return hasPackaging;
    }

    @Override
    public String toString() {
        return "Packaging{" +
                "category='" + category + '\'' +
                ", type='" + type + '\'' +
                ", phase='" + phase + '\'' +
                ", mfgStatus='" + mfgStatus + '\'' +
                ", releaseCriteria='" + releaseCriteria + '\'' +
                ", classType='" + classType + '\'' +
                ", reportedFunction='" + reportedFunction + '\'' +
                ", segment='" + segment + '\'' +
                ", componentType='" + componentType + '\'' +
                ", materialType='" + materialType + '\'' +
                ", baseUoM='" + baseUoM + '\'' +
                ", hasPackaging=" + hasPackaging +
                '}';
    }
}

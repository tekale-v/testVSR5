package com.pg.dsm.preference.template.entity;

import java.util.Map;

import com.pg.dsm.preference.enumeration.UserPreferenceTemplateConstants;
import com.pg.dsm.preference.template.DSMUserPreferenceTemplateUtil;

public class TechnicalSpecification {
    String category;
    String type;
    String segment;
    boolean hasTechnicalSpecification;

    public TechnicalSpecification(Map<Object, Object> objectMap) {
        // Part Category
        UserPreferenceTemplateConstants.TechnicalSpecificationFields categoryField = UserPreferenceTemplateConstants.TechnicalSpecificationFields.getByFieldIdentifier("PartCategory");
        this.category = (String) objectMap.get(categoryField.getFieldName());

        // Part Type
        UserPreferenceTemplateConstants.TechnicalSpecificationFields typeField = UserPreferenceTemplateConstants.TechnicalSpecificationFields.getByFieldIdentifier("PartType");
        this.type = (String) objectMap.get(typeField.getFieldName());

        // Segment (stored as id on person property) - single selection
        UserPreferenceTemplateConstants.TechnicalSpecificationFields segmentField = UserPreferenceTemplateConstants.TechnicalSpecificationFields.getByFieldIdentifier("Segment");
        this.segment = (String) objectMap.get(segmentField.getFieldPhysicalID());

        this.hasTechnicalSpecification = DSMUserPreferenceTemplateUtil.atleastOneKeyHasValue(objectMap);
    }

    public String getCategory() {
        return category;
    }

    public String getType() {
        return type;
    }

    public String getSegment() {
        return segment;
    }

    public boolean isHasTechnicalSpecification() {
        return hasTechnicalSpecification;
    }

    @Override
    public String toString() {
        return "TechnicalSpecification{" +
                "category='" + category + '\'' +
                ", type='" + type + '\'' +
                ", segment='" + segment + '\'' +
                ", hasTechnicalSpecification=" + hasTechnicalSpecification +
                '}';
    }
}

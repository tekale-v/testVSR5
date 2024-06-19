package com.pg.dsm.preference.template.entity;

import java.util.Map;

import com.pg.dsm.preference.enumeration.UserPreferenceTemplateConstants;
import com.pg.dsm.preference.template.DSMUserPreferenceTemplateUtil;

public class ManufacturingEquivalent {
    String category;
    String type;
    String vendor;
    boolean hasMEP;

    public ManufacturingEquivalent(Map<Object, Object> objectMap) {
        // Part Category
        UserPreferenceTemplateConstants.MEPFields categoryField = UserPreferenceTemplateConstants.MEPFields.getByFieldIdentifier("PartCategory");
        this.category = (String) objectMap.get(categoryField.getFieldName());

        // Part Type
        UserPreferenceTemplateConstants.MEPFields typeField = UserPreferenceTemplateConstants.MEPFields.getByFieldIdentifier("PartType");
        this.type = (String) objectMap.get(typeField.getFieldName());

        // Segment (stored as id on person property) - single selection
        UserPreferenceTemplateConstants.MEPFields segmentField = UserPreferenceTemplateConstants.MEPFields.getByFieldIdentifier("Vendor");
        this.vendor = (String) objectMap.get(segmentField.getFieldPhysicalID());

        this.hasMEP = DSMUserPreferenceTemplateUtil.atleastOneKeyHasValue(objectMap);
    }

    public String getCategory() {
        return category;
    }

    public String getType() {
        return type;
    }

    public String getVendor() {
        return vendor;
    }

    public boolean isHasMEP() {
        return hasMEP;
    }

    @Override
    public String toString() {
        return "ManufacturingEquivalent{" +
                "category='" + category + '\'' +
                ", type='" + type + '\'' +
                ", vendor='" + vendor + '\'' +
                ", hasMEP=" + hasMEP +
                '}';
    }
}

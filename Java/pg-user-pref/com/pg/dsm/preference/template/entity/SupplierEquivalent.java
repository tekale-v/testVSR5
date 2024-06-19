package com.pg.dsm.preference.template.entity;

import java.util.Map;

import com.pg.dsm.preference.enumeration.UserPreferenceTemplateConstants;
import com.pg.dsm.preference.template.DSMUserPreferenceTemplateUtil;

public class SupplierEquivalent {
    String category;
    String type;
    String vendor;
    boolean hasSEP;

    public SupplierEquivalent(Map<Object, Object> objectMap) {
        // Part Category
        UserPreferenceTemplateConstants.SEPFields categoryField = UserPreferenceTemplateConstants.SEPFields.getByFieldIdentifier("PartCategory");
        this.category = (String) objectMap.get(categoryField.getFieldName());

        // Part Type
        UserPreferenceTemplateConstants.SEPFields typeField = UserPreferenceTemplateConstants.SEPFields.getByFieldIdentifier("PartType");
        this.type = (String) objectMap.get(typeField.getFieldName());

        // Segment (stored as id on person property) - single selection
        UserPreferenceTemplateConstants.SEPFields segmentField = UserPreferenceTemplateConstants.SEPFields.getByFieldIdentifier("Vendor");
        this.vendor = (String) objectMap.get(segmentField.getFieldPhysicalID());

        this.hasSEP = DSMUserPreferenceTemplateUtil.atleastOneKeyHasValue(objectMap);
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

    public boolean isHasSEP() {
        return hasSEP;
    }

    @Override
    public String toString() {
        return "SupplierEquivalent{" +
                "category='" + category + '\'' +
                ", type='" + type + '\'' +
                ", vendor='" + vendor + '\'' +
                ", hasSEP=" + hasSEP +
                '}';
    }
}

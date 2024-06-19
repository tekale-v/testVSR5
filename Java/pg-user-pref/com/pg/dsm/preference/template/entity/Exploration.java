package com.pg.dsm.preference.template.entity;

import java.util.Map;

import com.pg.dsm.preference.enumeration.UserPreferenceTemplateConstants;
import com.pg.dsm.preference.template.DSMUserPreferenceTemplateUtil;

public class Exploration {
    String category;
    String type;
    boolean hasExploration;

    public Exploration(Map<Object, Object> objectMap) {
        // Part Category
        UserPreferenceTemplateConstants.ExplorationFields categoryField = UserPreferenceTemplateConstants.ExplorationFields.getByFieldIdentifier("PartCategory");
        this.category = (String) objectMap.get(categoryField.getFieldName());

        // Part Type
        UserPreferenceTemplateConstants.ExplorationFields typeField = UserPreferenceTemplateConstants.ExplorationFields.getByFieldIdentifier("PartType");
        this.type = (String) objectMap.get(typeField.getFieldName());

        this.hasExploration = DSMUserPreferenceTemplateUtil.atleastOneKeyHasValue(objectMap);
    }

    public String getCategory() {
        return category;
    }

    public String getType() {
        return type;
    }


    public boolean isHasExploration() {
        return hasExploration;
    }

    @Override
    public String toString() {
        return "Exploration{" +
                "category='" + category + '\'' +
                ", type='" + type + '\'' +
                ", hasExploration=" + hasExploration +
                '}';
    }
}

package com.pg.dsm.preference.template.apply;

import java.util.Map;

import com.pg.dsm.preference.enumeration.DSMUPTConstants;

import matrix.db.Context;

public class PackagingUserTemplate {
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

    public PackagingUserTemplate(Context context, Map<Object, Object> objectMap) {
        this.category = (String) objectMap.get(DSMUPTConstants.Attributes.UPT_PART_CATEGORY.getSelect(context));
        this.type = (String) objectMap.get(DSMUPTConstants.Attributes.UPT_PART_TYPE.getSelect(context));
        this.phase = (String) objectMap.get(DSMUPTConstants.Attributes.UPT_RELEASE_PHASE.getSelect(context));
        this.mfgStatus = (String) objectMap.get(DSMUPTConstants.Attributes.UPT_MFG_STATUS.getSelect(context));

        this.releaseCriteria = (String) objectMap.get(DSMUPTConstants.Attributes.UPT_RELEASE_CRITERIA_REQUIRED.getSelect(context));
        this.classType = (String) objectMap.get(DSMUPTConstants.Attributes.UPT_CLASS.getSelect(context));
        this.reportedFunction = (String) objectMap.get(DSMUPTConstants.Attributes.UPT_REPORTED_FUNCTION.getSelect(context));
        this.segment = (String) objectMap.get(DSMUPTConstants.Attributes.UPT_SEGMENT.getSelect(context));
        this.componentType = (String) objectMap.get(DSMUPTConstants.Attributes.UPT_PACKAGING_COMPONENT_TYPE.getSelect(context));
        this.materialType = (String) objectMap.get(DSMUPTConstants.Attributes.UPT_PACKAGING_MATERIAL_TYPE.getSelect(context));
        this.baseUoM = (String) objectMap.get(DSMUPTConstants.Attributes.UPT_BASE_UNIT_OF_MEASURE.getSelect(context));
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
}

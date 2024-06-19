package com.pg.dsm.preference.template.apply;

import java.util.Map;

import com.matrixone.apps.domain.util.StringUtil;
import com.pg.dsm.preference.enumeration.DSMUPTConstants;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.db.SelectConstants;
import matrix.util.StringList;

public class ProductUserTemplate {
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

    public ProductUserTemplate(Context context, Map<Object, Object> objectMap) throws Exception {
        this.category = (String) objectMap.get(DSMUPTConstants.Attributes.UPT_PART_CATEGORY.getSelect(context));
        this.type = (String) objectMap.get(DSMUPTConstants.Attributes.UPT_PART_TYPE.getSelect(context));
        this.phase = (String) objectMap.get(DSMUPTConstants.Attributes.UPT_RELEASE_PHASE.getSelect(context));
        this.mfgStatus = (String) objectMap.get(DSMUPTConstants.Attributes.UPT_MFG_STATUS.getSelect(context));

        this.releaseCriteria = (String) objectMap.get(DSMUPTConstants.Attributes.UPT_RELEASE_CRITERIA_REQUIRED.getSelect(context));
        this.classType = (String) objectMap.get(DSMUPTConstants.Attributes.UPT_CLASS.getSelect(context));
        this.reportedFunction = (String) objectMap.get(DSMUPTConstants.Attributes.UPT_REPORTED_FUNCTION.getSelect(context));
        this.segment = (String) objectMap.get(DSMUPTConstants.Attributes.UPT_SEGMENT.getSelect(context));

        this.businessArea = getPipeSeparatedStringFromMap(objectMap, DSMUPTConstants.Attributes.UPT_BUSINESS_AREA.getSelect(context));
        this.categoryPlatform = getPipeSeparatedStringFromMap(objectMap, DSMUPTConstants.Attributes.UPT_PRODUCT_CATEGORY_PLATFORM.getSelect(context));
        this.complianceRequired = (String) objectMap.get(DSMUPTConstants.Attributes.UPT_PRODUCT_COMPLIANCE_REQUIRED.getSelect(context));
    }

    public String getPipeSeparatedStringFromMap(Map<Object, Object> objectMap, String select) throws Exception {
        StringList objectList = new StringList();
        Object result = objectMap.get(select);
        if (null != result) {
            if (result instanceof StringList) {
                objectList = (StringList) result;
            } else if (result.toString().contains(SelectConstants.cSelectDelimiter)) {
                objectList = StringUtil.splitString(result.toString(), SelectConstants.cSelectDelimiter);
            } else if (result.toString().contains(pgV3Constants.SYMBOL_COMMA)) {
                objectList = StringUtil.split(result.toString(), pgV3Constants.SYMBOL_COMMA);
            } else {
                objectList.add(result.toString());
            }
        }
        return String.join("|", objectList);
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
}

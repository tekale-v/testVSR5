package com.pg.dsm.preference.models;

import com.pg.dsm.preference.Preferences;

import matrix.db.Context;
import matrix.util.MatrixException;

public class ProductPreferenceData {
    String partType;
    String phase;
    String manufacturingStatus;
    String releaseCriteria;
    String classValue;
    String reportedFunction;
    String segment;
    String businessArea;
    String productCategoryPlatform;
    String productComplianceRequired;

    public ProductPreferenceData(Context context) throws MatrixException {
        this.partType = Preferences.ProductPreference.PART_TYPE.getName(context);
        this.phase = Preferences.ProductPreference.PHASE.getName(context);
        this.manufacturingStatus = Preferences.ProductPreference.MANUFACTURING_STATUS.getName(context);
        this.releaseCriteria = Preferences.ProductPreference.STRUCTURE_RELEASE_CRITERIA.getName(context);
        this.classValue = Preferences.ProductPreference.CLASS.getPhysicalID(context);
        this.reportedFunction = Preferences.ProductPreference.REPORTED_FUNCTION.getPhysicalID(context); // reported function is stored as object id
        this.segment = Preferences.ProductPreference.SEGMENT.getPhysicalID(context); // segment is stored as object id.
        this.businessArea = Preferences.ProductPreference.BUSINESS_AREA.getPhysicalID(context); // business area is stored as physical id.
        this.productCategoryPlatform = Preferences.ProductPreference.PRODUCT_CATEGORY_PLATFORM.getPhysicalID(context);
        this.productComplianceRequired = Preferences.ProductPreference.PRODUCT_COMPLIANCE_REQUIRED.getName(context);

    }

    public String getPartType() {
        return partType;
    }

    public String getPhase() {
        return phase;
    }

    public String getManufacturingStatus() {
        return manufacturingStatus;
    }

    public String getReleaseCriteria() {
        return releaseCriteria;
    }

    public String getClassValue() {
        return classValue;
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

    public String getProductCategoryPlatform() {
        return productCategoryPlatform;
    }

    public String getProductComplianceRequired() {
        return productComplianceRequired;
    }

    @Override
    public String toString() {
        return "ProductPreferenceData{" +
                "partType='" + partType + '\'' +
                ", phase='" + phase + '\'' +
                ", manufacturingStatus='" + manufacturingStatus + '\'' +
                ", releaseCriteria='" + releaseCriteria + '\'' +
                ", classValue='" + classValue + '\'' +
                ", reportedFunction='" + reportedFunction + '\'' +
                ", segment='" + segment + '\'' +
                ", businessArea='" + businessArea + '\'' +
                ", productCategoryPlatform='" + productCategoryPlatform + '\'' +
                ", productComplianceRequired='" + productComplianceRequired + '\'' +
                '}';
    }
}

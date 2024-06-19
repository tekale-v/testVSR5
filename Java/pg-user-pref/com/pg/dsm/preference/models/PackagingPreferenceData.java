package com.pg.dsm.preference.models;

import com.pg.dsm.preference.Preferences;

import matrix.db.Context;
import matrix.util.MatrixException;

public class PackagingPreferenceData {
    String partType;
    String phase;
    String manufacturingStatus;
    String releaseCriteria;
    String classValue;
    String reportedFunction;
    String segment;
    String packagingComponentType;
    String packagingMaterialType;
    String baseUnitOfMeasure;

    public PackagingPreferenceData(Context context) throws MatrixException {
        this.partType = Preferences.PackagingPreference.PART_TYPE.getName(context);
        this.phase = Preferences.PackagingPreference.PHASE.getName(context);
        this.manufacturingStatus = Preferences.PackagingPreference.MANUFACTURING_STATUS.getName(context);
        this.releaseCriteria = Preferences.PackagingPreference.STRUCTURE_RELEASE_CRITERIA.getName(context);

        this.classValue = Preferences.PackagingPreference.CLASS.getPhysicalID(context);
        this.reportedFunction = Preferences.PackagingPreference.REPORTED_FUNCTION.getPhysicalID(context);
        this.segment = Preferences.PackagingPreference.SEGMENT.getPhysicalID(context);
        this.packagingComponentType = Preferences.PackagingPreference.PACKAGING_COMPONENT_TYPE.getPhysicalID(context);
        this.packagingMaterialType = Preferences.PackagingPreference.PACKAGING_MATERIAL_TYPE.getPhysicalID(context);
        this.baseUnitOfMeasure = Preferences.PackagingPreference.BASE_UNIT_OF_MEASURE.getPhysicalID(context);
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

    public String getPackagingComponentType() {
        return packagingComponentType;
    }

    public String getPackagingMaterialType() {
        return packagingMaterialType;
    }

    public String getBaseUnitOfMeasure() {
        return baseUnitOfMeasure;
    }

    @Override
    public String toString() {
        return "PackagingPreferenceData{" +
                "partType='" + partType + '\'' +
                ", phase='" + phase + '\'' +
                ", manufacturingStatus='" + manufacturingStatus + '\'' +
                ", releaseCriteria='" + releaseCriteria + '\'' +
                ", classValue='" + classValue + '\'' +
                ", reportedFunction='" + reportedFunction + '\'' +
                ", segment='" + segment + '\'' +
                ", packagingComponentType='" + packagingComponentType + '\'' +
                ", packagingMaterialType='" + packagingMaterialType + '\'' +
                ", baseUnitOfMeasure='" + baseUnitOfMeasure + '\'' +
                '}';
    }
}

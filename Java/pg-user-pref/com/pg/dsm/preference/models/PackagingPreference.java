package com.pg.dsm.preference.models;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.designtools.datamanagement.DataConstants;
import com.pg.designtools.util.CacheManagement;
import com.pg.designtools.util.PreferenceManagement;
import com.pg.dsm.preference.enumeration.PreferenceConstants;
import com.pg.dsm.preference.util.PackagingPreferenceUtil;
import com.pg.dsm.preference.util.UserPreferenceUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class PackagingPreference {
    private static final Logger logger = Logger.getLogger(PackagingPreference.class.getName());
    String partTypeFieldLabel;
    String phaseFieldLabel;
    String manufacturingStatusFieldLabel;
    String releaseStatusCriteriaFieldLabel;
    String classFieldLabel;
    String reportedFunctionFieldLabel;
    String segmentFieldLabel;
    String partTypeFieldID;
    String phaseFieldID;
    String manufacturingStatusFieldID;
    String releaseStatusCriteriaFieldID;
    String classFieldID;
    String reportedFunctionFieldID;
    String segmentFieldID;
    String packagingMaterialTypeFieldLabel;
    String packagingComponentTypeFieldLabel;
    String baseUnitOfMeasureFieldLabel;
    String packagingMaterialTypeFieldID;
    String packagingComponentTypeFieldID;
    String baseUnitOfMeasureFieldID;
    Context context;
    String user;
    Locale locale;
    String partType;
    String phase;
    String manufacturingStatus;
    String releaseCriteria;
    String classValue;
    String reportedFunction;
    String reportedFunctionName;
    String segment;
    String segmentName;
    String packagingComponentType;
    String packagingMaterialType;
    String baseUnitOfMeasure;

    StringList partTypeOptions;
    StringList partTypeDisplayOptions;

    StringList phaseOptions;
    StringList phaseDisplayOptions;

    StringList manufacturingStatusOptions;
    StringList manufacturingStatusDisplayOptions;

    StringList releaseCriteriaStatusOptions;
    StringList releaseCriteriaStatusDisplayOptions;

    StringList classOptions;
    StringList classDisplayOptions;

    StringList reportedFunctionOptions;
    StringList reportedFunctionDisplayOptions;

    StringList segmentOptions;
    StringList segmentDisplayOptions;

    StringList packagingComponentTypeOptions;
    StringList packagingComponentTypeDisplayOptions;

    StringList packagingMaterialTypeOptions;
    StringList packagingMaterialTypeDisplayOptions;

    StringList baseUnitOfMeasureOptions;
    StringList baseUnitOfMeasureDisplayOptions;
    String partTypeJson;
    String phaseJson;
    String mfgStatusJson;
    String releaseCriteriaJson;
    String classesJson;
    String reportedFunctionJson;
    String segmentJson;
    String packagingComponentTypeJson;
    String packagingMaterialTypeJson;
    String baseUoMJson;

    public PackagingPreference(Context context) throws Exception {
        Instant startTime = Instant.now();
        this.context = context;
        this.locale = context.getLocale();
        this.user = context.getUser();

        PreferenceManagement preferenceManagement = new PreferenceManagement(this.context);
        PackagingPreferenceUtil packagingPreferenceUtil = new PackagingPreferenceUtil();
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        CacheManagement cacheManagement = new CacheManagement(this.context);

        this.partType = packagingPreferenceUtil.getPartType(this.context, preferenceManagement, userPreferenceUtil);
        this.phase = packagingPreferenceUtil.getPhase(this.context, preferenceManagement, userPreferenceUtil);
        this.manufacturingStatus = packagingPreferenceUtil.getManufacturingStatus(this.context, preferenceManagement, userPreferenceUtil);
        this.releaseCriteria = packagingPreferenceUtil.getStructureReleaseCriteria(this.context, preferenceManagement, userPreferenceUtil);
        this.classValue = packagingPreferenceUtil.getClassValue(this.context, preferenceManagement, userPreferenceUtil);
        this.reportedFunction = packagingPreferenceUtil.getReportedFunction(this.context, preferenceManagement, userPreferenceUtil); // reported function object id is stored on preference.
        this.reportedFunctionName = packagingPreferenceUtil.getReportedFunctionName(this.context, userPreferenceUtil, this.reportedFunction);
        this.segment = packagingPreferenceUtil.getSegment(this.context, preferenceManagement, userPreferenceUtil); // segment object id is stored on preference.
        this.segmentName = packagingPreferenceUtil.getSegmentName(this.context, preferenceManagement, userPreferenceUtil);
        this.packagingComponentType = packagingPreferenceUtil.getPackagingComponentType(this.context, preferenceManagement, userPreferenceUtil);
        this.packagingMaterialType = packagingPreferenceUtil.getPackagingMaterialType(this.context, preferenceManagement, userPreferenceUtil);
        this.baseUnitOfMeasure = packagingPreferenceUtil.getBaseUnitOfMeasure(this.context, preferenceManagement, userPreferenceUtil);

        this.partTypeDisplayOptions = new StringList();
        this.partTypeOptions = new StringList();
        this.getPartType(packagingPreferenceUtil);

        this.phaseDisplayOptions = new StringList();
        this.phaseOptions = new StringList();
        this.getPhase(userPreferenceUtil);

        this.manufacturingStatusDisplayOptions = new StringList();
        this.manufacturingStatusOptions = new StringList();
        this.getManufacturingStatus(userPreferenceUtil);

        this.releaseCriteriaStatusDisplayOptions = new StringList();
        this.releaseCriteriaStatusOptions = new StringList();
        this.getReleaseStatusCriteria(userPreferenceUtil);

        this.segmentDisplayOptions = new StringList();
        this.segmentOptions = new StringList();
        this.getSegment(cacheManagement, userPreferenceUtil);

        this.classDisplayOptions = new StringList();
        this.classOptions = new StringList();
        this.getClasses(cacheManagement, userPreferenceUtil);

        this.reportedFunctionDisplayOptions = new StringList();
        this.reportedFunctionOptions = new StringList();
        this.getReportedFunction(userPreferenceUtil);

        this.packagingMaterialTypeDisplayOptions = new StringList();
        this.packagingMaterialTypeOptions = new StringList();
        this.getPackagingMaterialType(cacheManagement, userPreferenceUtil);

        this.packagingComponentTypeDisplayOptions = new StringList();
        this.packagingComponentTypeOptions = new StringList();
        this.getPackagingComponentType(userPreferenceUtil);

        this.baseUnitOfMeasureDisplayOptions = new StringList();
        this.baseUnitOfMeasureOptions = new StringList();
        this.getBaseUnitOfMeasure(userPreferenceUtil);

        this.partTypeFieldLabel = PreferenceConstants.Basic.DI_PREFERRED_PART_TYPE_LABEL_NAME.get();
        this.phaseFieldLabel = PreferenceConstants.Basic.DI_PREFERRED_PHASE_LABEL_NAME.get();
        this.manufacturingStatusFieldLabel = EnoviaResourceBundle.getFrameworkStringResourceProperty(this.context, "emxFramework.Preferences.DIPreferences.DIMaturityStatus", this.locale);
        this.releaseStatusCriteriaFieldLabel = EnoviaResourceBundle.getProperty(this.context, DataConstants.CONST_CPN_STRING_RESOURCE, this.locale, "emxCPN.Label.EditSection.StructuredReleaseCriteria");
        this.segmentFieldLabel = EnoviaResourceBundle.getProperty(this.context, DataConstants.CONST_CPN_STRING_RESOURCE, this.locale, "emxCPN.Common.Segment");
        this.classFieldLabel = EnoviaResourceBundle.getProperty(this.context, DataConstants.CONST_CPN_STRING_RESOURCE, this.locale, "emxCPN.Label.EditSection.Class");
        this.reportedFunctionFieldLabel = EnoviaResourceBundle.getProperty(this.context, DataConstants.CONST_CPN_STRING_RESOURCE, this.locale, "emxCPN.Label.EditSection.ReportedFunction");
        this.packagingMaterialTypeFieldLabel = EnoviaResourceBundle.getProperty(this.context, DataConstants.CONST_CPN_STRING_RESOURCE, this.locale, "emxCPN.Label.EditSection.PackagingMaterialType");
        this.packagingComponentTypeFieldLabel = PreferenceConstants.Basic.PACKAGING_COMPONENT_TYPE_LABEL_NAME.get();
        this.baseUnitOfMeasureFieldLabel = PreferenceConstants.Basic.BASE_UNIT_OF_MEASURE_LABEL_NAME.get();

        this.partTypeFieldID = "pgCreateProductPart";
        this.phaseFieldID = "pgDIPreferencesPhase";
        this.manufacturingStatusFieldID = EnoviaResourceBundle.getProperty(this.context, DataConstants.CONST_COMPONENTS_STRING_RESOURCE, this.locale, "emxComponents.MaturityStatus.Field");
        this.releaseStatusCriteriaFieldID = EnoviaResourceBundle.getProperty(this.context, DataConstants.CONST_COMPONENTS_STRING_RESOURCE, this.locale, "emxComponents.StructuredReleaseCriteriaReq.Field");
        this.segmentFieldID = EnoviaResourceBundle.getProperty(this.context, DataConstants.CONST_COMPONENTS_STRING_RESOURCE, this.locale, "emxComponents.Segment.Field");
        this.classFieldID = EnoviaResourceBundle.getProperty(this.context, DataConstants.CONST_COMPONENTS_STRING_RESOURCE, this.locale, "emxComponents.Class.Field");
        this.reportedFunctionFieldID = EnoviaResourceBundle.getProperty(this.context, DataConstants.CONST_COMPONENTS_STRING_RESOURCE, this.locale, "emxComponents.ReportedFunction.Field");
        this.packagingMaterialTypeFieldID = EnoviaResourceBundle.getProperty(this.context, DataConstants.CONST_COMPONENTS_STRING_RESOURCE, this.locale, "emxComponents.PackagingMaterialType.Field");
        this.packagingComponentTypeFieldID = "pgPkgComponentTypeID";
        this.baseUnitOfMeasureFieldID = "pgBaseUoMID";

        this.loadPartTypeJson(packagingPreferenceUtil);
        this.loadPhaseJson(packagingPreferenceUtil, userPreferenceUtil);
        this.loadMfgStatusJson(packagingPreferenceUtil, userPreferenceUtil);
        this.loadReleaseCriteriaJson(packagingPreferenceUtil, userPreferenceUtil);
        this.loadClassesJson(packagingPreferenceUtil, cacheManagement, userPreferenceUtil);
        this.loadReportedFunctionJson(packagingPreferenceUtil, userPreferenceUtil);
        this.loadSegmentJson(packagingPreferenceUtil, cacheManagement, userPreferenceUtil);
        this.loadPackagingComponentTypeJson(packagingPreferenceUtil, userPreferenceUtil);
        this.loadPackagingMaterialTypeJson(packagingPreferenceUtil, cacheManagement, userPreferenceUtil);
        this.loadBaseUoMJson(packagingPreferenceUtil, userPreferenceUtil);

        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("PackagingPreference instantiation - took|" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
    }

    void loadPartTypeJson(PackagingPreferenceUtil packagingPreferenceUtil) throws FrameworkException {
        this.partTypeJson = packagingPreferenceUtil.getPartTypeJson(this.context);
    }

    void loadPhaseJson(PackagingPreferenceUtil packagingPreferenceUtil, UserPreferenceUtil userPreferenceUtil) throws Exception {
        this.phaseJson = packagingPreferenceUtil.getPhaseJson(this.context, userPreferenceUtil, this.partType);
    }

    void loadMfgStatusJson(PackagingPreferenceUtil packagingPreferenceUtil, UserPreferenceUtil userPreferenceUtil) throws Exception {
        this.mfgStatusJson = packagingPreferenceUtil.getManufacturingStatusJson(this.context, userPreferenceUtil, this.partType, this.phase);
    }

    void loadReleaseCriteriaJson(PackagingPreferenceUtil packagingPreferenceUtil, UserPreferenceUtil userPreferenceUtil) throws MatrixException {
        this.releaseCriteriaJson = packagingPreferenceUtil.getReleaseCriteriaJson(this.context, userPreferenceUtil);
    }

    void loadClassesJson(PackagingPreferenceUtil packagingPreferenceUtil, CacheManagement cacheManagement, UserPreferenceUtil userPreferenceUtil) throws MatrixException {
        this.classesJson = packagingPreferenceUtil.getClassesJson(this.context, cacheManagement, userPreferenceUtil);
    }

    void loadReportedFunctionJson(PackagingPreferenceUtil packagingPreferenceUtil, UserPreferenceUtil userPreferenceUtil) throws MatrixException {
        this.reportedFunctionJson = packagingPreferenceUtil.getReportedFunctionJson(this.context, userPreferenceUtil, pgV3Constants.TYPE_PACKAGINGMATERIALPART);
    }

    void loadSegmentJson(PackagingPreferenceUtil packagingPreferenceUtil, CacheManagement cacheManagement, UserPreferenceUtil userPreferenceUtil) throws MatrixException {
        this.segmentJson = packagingPreferenceUtil.getSegmentJson(this.context, cacheManagement, userPreferenceUtil);
    }

    void loadPackagingComponentTypeJson(PackagingPreferenceUtil packagingPreferenceUtil, UserPreferenceUtil userPreferenceUtil) throws MatrixException {
        this.packagingComponentTypeJson = packagingPreferenceUtil.getPackagingComponentTypeJson(this.context, userPreferenceUtil);
    }

    void loadPackagingMaterialTypeJson(PackagingPreferenceUtil packagingPreferenceUtil, CacheManagement cacheManagement, UserPreferenceUtil userPreferenceUtil) throws MatrixException {
        this.packagingMaterialTypeJson = packagingPreferenceUtil.getPackagingMaterialTypeJson(this.context, cacheManagement, userPreferenceUtil);
    }

    void loadBaseUoMJson(PackagingPreferenceUtil packagingPreferenceUtil, UserPreferenceUtil userPreferenceUtil) throws MatrixException {
        this.baseUoMJson = packagingPreferenceUtil.getBaseUnitOfMeasureJson(this.context, userPreferenceUtil);
    }

    void getPartType(PackagingPreferenceUtil packagingPreferenceUtil) throws FrameworkException {
        Map<String, StringList> partTypeRanges = packagingPreferenceUtil.getPartTypeRanges(this.context);
        if (null != partTypeRanges && !partTypeRanges.isEmpty()) {
            this.partTypeDisplayOptions.add(pgV3Constants.SYMBOL_SPACE);
            this.partTypeOptions.add(pgV3Constants.SYMBOL_SPACE);
            this.partTypeDisplayOptions.addAll(partTypeRanges.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES));
            this.partTypeOptions.addAll(partTypeRanges.get(DataConstants.CONST_FIELD_CHOICES));
        } else {
            logger.log(Level.WARNING, "Type range map is empty");
        }
    }

    void getPhase(UserPreferenceUtil userPreferenceUtil) throws Exception {
        if (UIUtil.isNotNullAndNotEmpty(this.partType)) {
            Map phaseRanges = userPreferenceUtil.getPhase(this.context, this.partType);
            if (null != phaseRanges && !phaseRanges.isEmpty()) {
                this.phaseDisplayOptions.addAll((StringList) phaseRanges.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES));
                this.phaseOptions.addAll((StringList) phaseRanges.get(DataConstants.CONST_FIELD_CHOICES));
            }
        } else {
            logger.log(Level.WARNING, "Type is empty cannot fetch Phase");
        }
    }

    void getManufacturingStatus(UserPreferenceUtil userPreferenceUtil) throws Exception {
        if (UIUtil.isNotNullAndNotEmpty(this.partType) && UIUtil.isNotNullAndNotEmpty(this.phase)) {
            Map<String, StringList> rangeMap = userPreferenceUtil.getManufacturingStatusRange(this.context, this.partType, this.phase);
            if (null != rangeMap && !rangeMap.isEmpty()) {
                this.manufacturingStatusDisplayOptions.addAll(rangeMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES));
                this.manufacturingStatusOptions.addAll(rangeMap.get(DataConstants.CONST_FIELD_CHOICES));
            }
        } else {
            logger.log(Level.WARNING, "Type or Phase is empty cannot fetch Mfg Status");
        }
    }

    void getReleaseStatusCriteria(UserPreferenceUtil userPreferenceUtil) throws MatrixException {
        Map<String, StringList> rangeMap = userPreferenceUtil.getReleaseCriteriaStatus(this.context);
        if (null != rangeMap && !rangeMap.isEmpty()) {
            this.releaseCriteriaStatusDisplayOptions.addAll(rangeMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES));
            this.releaseCriteriaStatusOptions.addAll(rangeMap.get(DataConstants.CONST_FIELD_CHOICES));
        } else {
            logger.log(Level.WARNING, "Release Criteria range is empty");
        }
    }

    void getSegment(CacheManagement cacheManagement, UserPreferenceUtil userPreferenceUtil) throws MatrixException {
        Map<String, StringList> rangeMap = userPreferenceUtil.getSegment(this.context, cacheManagement);
        if (null != rangeMap && !rangeMap.isEmpty()) {
            this.segmentDisplayOptions.addAll(rangeMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES));
            this.segmentOptions.addAll(rangeMap.get(DataConstants.CONST_FIELD_CHOICES));
        } else {
            logger.log(Level.WARNING, "Segments range is empty");
        }
    }

    void getClasses(CacheManagement cacheManagement, UserPreferenceUtil userPreferenceUtil) throws MatrixException {
        Map<String, StringList> rangeMap = userPreferenceUtil.getClasses(this.context, cacheManagement);
        if (null != rangeMap && !rangeMap.isEmpty()) {
            this.classDisplayOptions.addAll(rangeMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES));
            this.classOptions.addAll(rangeMap.get(DataConstants.CONST_FIELD_CHOICES));
        } else {
            logger.log(Level.WARNING, "Classes range is empty");
        }
    }

    /**
     * @param userPreferenceUtil
     * @throws MatrixException
     */
    void getReportedFunction(UserPreferenceUtil userPreferenceUtil) throws MatrixException {
        // Reported Function is same for all Packaging Parts. so take PMP type as baseline.
        Map<Object, Object> reportedInfoMap = userPreferenceUtil.getReportedFunctionRangeByType(this.context, pgV3Constants.TYPE_PACKAGINGMATERIALPART);
        if (null != reportedInfoMap && !reportedInfoMap.isEmpty()) {
            this.reportedFunctionDisplayOptions.addAll((StringList) reportedInfoMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES));
            this.reportedFunctionOptions.addAll((StringList) reportedInfoMap.get(DataConstants.CONST_FIELD_CHOICES));
        } else {
            logger.log(Level.WARNING, "Reported Function range is empty");
        }
    }

    void getPackagingMaterialType(CacheManagement cacheManagement, UserPreferenceUtil userPreferenceUtil) throws MatrixException {
        Map<String, StringList> rangeMap = userPreferenceUtil.getPackagingMaterialType(this.context, cacheManagement);
        if (null != rangeMap && !rangeMap.isEmpty()) {
            this.packagingMaterialTypeDisplayOptions.addAll(rangeMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES));
            this.packagingMaterialTypeOptions.addAll(rangeMap.get(DataConstants.CONST_FIELD_CHOICES));
        } else {
            logger.log(Level.WARNING, "Packaging Material Type range is empty");
        }
    }

    void getPackagingComponentType(UserPreferenceUtil userPreferenceUtil) throws MatrixException {
        Map<String, StringList> rangeMap = userPreferenceUtil.getPackagingComponentType(this.context);
        if (null != rangeMap && !rangeMap.isEmpty()) {
            this.packagingComponentTypeDisplayOptions.addAll(rangeMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES));
            this.packagingComponentTypeOptions.addAll(rangeMap.get(DataConstants.CONST_FIELD_CHOICES));
        } else {
            logger.log(Level.WARNING, "Packaging Component Type range is empty");
        }
    }

    void getBaseUnitOfMeasure(UserPreferenceUtil userPreferenceUtil) throws MatrixException {
        Map<String, StringList> rangeMap = userPreferenceUtil.getBaseUnitOfMeasure(this.context);
        if (null != rangeMap && !rangeMap.isEmpty()) {
            this.baseUnitOfMeasureDisplayOptions.addAll(rangeMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES));
            this.baseUnitOfMeasureOptions.addAll(rangeMap.get(DataConstants.CONST_FIELD_CHOICES));
        } else {
            logger.log(Level.WARNING, "Base UoM range is empty");
        }
    }

    public Context getContext() {
        return context;
    }

    public String getUser() {
        return user;
    }

    public Locale getLocale() {
        return locale;
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

    public StringList getPartTypeOptions() {
        return partTypeOptions;
    }

    public StringList getPartTypeDisplayOptions() {
        return partTypeDisplayOptions;
    }

    public StringList getPhaseOptions() {
        return phaseOptions;
    }

    public StringList getPhaseDisplayOptions() {
        return phaseDisplayOptions;
    }

    public StringList getManufacturingStatusOptions() {
        return manufacturingStatusOptions;
    }

    public StringList getManufacturingStatusDisplayOptions() {
        return manufacturingStatusDisplayOptions;
    }

    public StringList getReleaseCriteriaStatusOptions() {
        return releaseCriteriaStatusOptions;
    }

    public StringList getReleaseCriteriaStatusDisplayOptions() {
        return releaseCriteriaStatusDisplayOptions;
    }

    public StringList getClassOptions() {
        return classOptions;
    }

    public StringList getClassDisplayOptions() {
        return classDisplayOptions;
    }

    public StringList getReportedFunctionOptions() {
        return reportedFunctionOptions;
    }

    public StringList getReportedFunctionDisplayOptions() {
        return reportedFunctionDisplayOptions;
    }

    public StringList getSegmentOptions() {
        return segmentOptions;
    }

    public StringList getSegmentDisplayOptions() {
        return segmentDisplayOptions;
    }

    public StringList getPackagingComponentTypeOptions() {
        return packagingComponentTypeOptions;
    }

    public StringList getPackagingComponentTypeDisplayOptions() {
        return packagingComponentTypeDisplayOptions;
    }

    public StringList getPackagingMaterialTypeOptions() {
        return packagingMaterialTypeOptions;
    }

    public StringList getPackagingMaterialTypeDisplayOptions() {
        return packagingMaterialTypeDisplayOptions;
    }

    public StringList getBaseUnitOfMeasureOptions() {
        return baseUnitOfMeasureOptions;
    }

    public StringList getBaseUnitOfMeasureDisplayOptions() {
        return baseUnitOfMeasureDisplayOptions;
    }

    public String getPhaseFieldLabel() {
        return phaseFieldLabel;
    }

    public String getManufacturingStatusFieldLabel() {
        return manufacturingStatusFieldLabel;
    }

    public String getPartTypeFieldID() {
        return partTypeFieldID;
    }

    public String getClassFieldID() {
        return classFieldID;
    }

    public String getPhaseFieldID() {
        return phaseFieldID;
    }

    public String getManufacturingStatusFieldID() {
        return manufacturingStatusFieldID;
    }

    public String getReleaseStatusCriteriaFieldLabel() {
        return releaseStatusCriteriaFieldLabel;
    }

    public String getPackagingMaterialTypeFieldLabel() {
        return packagingMaterialTypeFieldLabel;
    }

    public String getPackagingComponentTypeFieldLabel() {
        return packagingComponentTypeFieldLabel;
    }

    public String getBaseUnitOfMeasureFieldLabel() {
        return baseUnitOfMeasureFieldLabel;
    }

    public String getReleaseStatusCriteriaFieldID() {
        return releaseStatusCriteriaFieldID;
    }

    public String getSegmentFieldLabel() {
        return segmentFieldLabel;
    }

    public String getClassFieldLabel() {
        return classFieldLabel;
    }

    public String getPackagingMaterialTypeFieldID() {
        return packagingMaterialTypeFieldID;
    }

    public String getPackagingComponentTypeFieldID() {
        return packagingComponentTypeFieldID;
    }

    public String getBaseUnitOfMeasureFieldID() {
        return baseUnitOfMeasureFieldID;
    }

    public String getReportedFunctionFieldLabel() {
        return reportedFunctionFieldLabel;
    }

    public String getSegmentFieldID() {
        return segmentFieldID;
    }

    public String getReportedFunctionFieldID() {
        return reportedFunctionFieldID;
    }

    public String getPartTypeFieldLabel() {
        return partTypeFieldLabel;
    }

    public String getSegmentName() {
        return segmentName;
    }

    public String getReportedFunctionName() {
        return reportedFunctionName;
    }

    public String getPartTypeJson() {
        return partTypeJson;
    }

    public String getPhaseJson() {
        return phaseJson;
    }

    public String getMfgStatusJson() {
        return mfgStatusJson;
    }

    public String getReleaseCriteriaJson() {
        return releaseCriteriaJson;
    }

    public String getClassesJson() {
        return classesJson;
    }

    public String getReportedFunctionJson() {
        return reportedFunctionJson;
    }

    public String getSegmentJson() {
        return segmentJson;
    }

    public String getPackagingComponentTypeJson() {
        return packagingComponentTypeJson;
    }

    public String getPackagingMaterialTypeJson() {
        return packagingMaterialTypeJson;
    }

    public String getBaseUoMJson() {
        return baseUoMJson;
    }

    @Override
    public String toString() {
        return "PackagingPreference{" +
                ", context=" + context +
                ", user='" + user + '\'' +
                ", locale=" + locale +
                ", partType='" + partType + '\'' +
                ", phase='" + phase + '\'' +
                ", manufacturingStatus='" + manufacturingStatus + '\'' +
                ", releaseCriteria='" + releaseCriteria + '\'' +
                ", classValue='" + classValue + '\'' +
                ", reportedFunction='" + reportedFunction + '\'' +
                ", segment='" + segment + '\'' +
                ", packagingComponentType='" + packagingComponentType + '\'' +
                ", packagingMaterialType='" + packagingMaterialType + '\'' +
                ", baseUnitOfMeasure='" + baseUnitOfMeasure + '\'' +
                ", partTypeJson='" + partTypeJson + '\'' +
                ", phaseJson='" + phaseJson + '\'' +
                ", mfgStatusJson='" + mfgStatusJson + '\'' +
                ", releaseCriteriaJson='" + releaseCriteriaJson + '\'' +
                ", classesJson='" + classesJson + '\'' +
                ", reportedFunctionJson='" + reportedFunctionJson + '\'' +
                ", segmentJson='" + segmentJson + '\'' +
                ", packagingComponentTypeJson='" + packagingComponentTypeJson + '\'' +
                ", packagingMaterialTypeJson='" + packagingMaterialTypeJson + '\'' +
                ", baseUoMJson='" + baseUoMJson + '\'' +
                '}';
    }
}

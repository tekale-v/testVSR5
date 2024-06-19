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
import com.pg.dsm.preference.util.RawMaterialPreferenceUtil;
import com.pg.dsm.preference.util.UserPreferenceUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class RawMaterialPreference {
    private static final Logger logger = Logger.getLogger(RawMaterialPreference.class.getName());
    String partTypeFieldLabel;
    String phaseFieldLabel;
    String manufacturingStatusFieldLabel;
    String releaseStatusCriteriaFieldLabel;
    String classFieldLabel;
    String reportedFunctionFieldLabel;
    String segmentFieldLabel;
    String businessAreaFieldLabel;
    String productCategoryPlatformFieldLabel;
    String materialFunctionFieldLabel;
    String partTypeFieldID;
    String phaseFieldID;
    String manufacturingStatusFieldID;
    String releaseStatusCriteriaFieldID;
    String classFieldID;
    String reportedFunctionFieldID;
    String segmentFieldID;
    String businessAreaFieldID;
    String productCategoryPlatformFieldID;
    String materialFunctionFieldID;

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
    StringList businessAreaOptions;
    StringList businessAreaDisplayOptions;
    StringList productCategoryPlatformOptions;
    StringList productCategoryPlatformDisplayOptions;
    String businessArea;
    String businessAreaName;
    String businessAreaID;
    String productCategoryPlatform;
    String productCategoryPlatformID;
    String productCategoryPlatformName;
    String materialFunction;
    String materialFunctionName;
    String materialFunctionID;
    StringList materialFunctionOptions;
    StringList materialFunctionDisplayOptions;
    String partTypeJson;
    String phaseJson;
    String mfgStatusJson;
    String releaseCriteriaJson;
    String segmentJson;
    String classesJson;
    String reportedFunctionJson;
    String businessAreaJson;
    String productCategoryPlatformJson;
    String materialFunctionJson;

    public RawMaterialPreference(Context context) throws Exception {
        Instant startTime = Instant.now();
        this.context = context;
        this.locale = context.getLocale();
        this.user = context.getUser();

        PreferenceManagement preferenceManagement = new PreferenceManagement(this.context);
        RawMaterialPreferenceUtil rawMaterialPreferenceUtil = new RawMaterialPreferenceUtil();
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        CacheManagement cacheManagement = new CacheManagement(this.context);

        this.partType = rawMaterialPreferenceUtil.getPartType(this.context, preferenceManagement, userPreferenceUtil);
        this.phase = rawMaterialPreferenceUtil.getPhase(this.context, preferenceManagement, userPreferenceUtil);
        this.manufacturingStatus = rawMaterialPreferenceUtil.getManufacturingStatus(this.context, preferenceManagement, userPreferenceUtil);
        this.releaseCriteria = rawMaterialPreferenceUtil.getStructureReleaseCriteria(this.context, preferenceManagement, userPreferenceUtil);
        this.classValue = rawMaterialPreferenceUtil.getClassValue(this.context, preferenceManagement, userPreferenceUtil);
        this.reportedFunction = rawMaterialPreferenceUtil.getReportedFunction(this.context, preferenceManagement, userPreferenceUtil); // stored as id
        this.reportedFunctionName = rawMaterialPreferenceUtil.getReportedFunctionName(this.context, userPreferenceUtil, this.reportedFunction);
        this.segment = rawMaterialPreferenceUtil.getSegment(this.context, preferenceManagement, userPreferenceUtil); // stored as id
        this.segmentName = rawMaterialPreferenceUtil.getSegmentName(this.context, userPreferenceUtil, this.segment);
        this.businessArea = rawMaterialPreferenceUtil.getBusinessArea(this.context, preferenceManagement, userPreferenceUtil); // stores physical id.
        this.businessAreaName = userPreferenceUtil.getBusinessAreaName(this.context, this.businessArea);
        this.businessAreaID = rawMaterialPreferenceUtil.getBusinessAreaID(this.context, userPreferenceUtil, this.businessArea);
        this.productCategoryPlatform = rawMaterialPreferenceUtil.getProductCategoryPlatform(this.context, preferenceManagement, userPreferenceUtil); // stored as physical id
        this.productCategoryPlatformID = rawMaterialPreferenceUtil.getProductCategoryPlatformID(this.context, userPreferenceUtil, this.productCategoryPlatform);
        this.productCategoryPlatformName = rawMaterialPreferenceUtil.getProductCategoryPlatformName(this.context, userPreferenceUtil, this.productCategoryPlatform);

        this.materialFunction = rawMaterialPreferenceUtil.getMaterialFunction(this.context, preferenceManagement, userPreferenceUtil); // stored as id
        this.materialFunctionName = rawMaterialPreferenceUtil.getMaterialFunctionName(this.context, userPreferenceUtil, this.materialFunction);
        this.materialFunctionID = this.materialFunction;

        this.partTypeDisplayOptions = new StringList();
        this.partTypeOptions = new StringList();
        this.getPartType(rawMaterialPreferenceUtil, userPreferenceUtil);

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

        this.businessAreaDisplayOptions = new StringList();
        this.businessAreaOptions = new StringList();
        this.getBusinessArea(userPreferenceUtil);

        this.productCategoryPlatformDisplayOptions = new StringList();
        this.productCategoryPlatformOptions = new StringList();
        this.getProductCategoryPlatformByBusinessArea(rawMaterialPreferenceUtil, userPreferenceUtil);

        this.materialFunctionOptions = new StringList();
        this.materialFunctionDisplayOptions = new StringList();
        this.getMaterialFunctionRanges();

        this.partTypeFieldLabel = PreferenceConstants.Basic.DI_PREFERRED_PART_TYPE_LABEL_NAME.get();
        this.phaseFieldLabel = PreferenceConstants.Basic.DI_PREFERRED_PHASE_LABEL_NAME.get();
        this.manufacturingStatusFieldLabel = EnoviaResourceBundle.getFrameworkStringResourceProperty(this.context, "emxFramework.Preferences.DIPreferences.DIMaturityStatus", this.locale);
        this.releaseStatusCriteriaFieldLabel = EnoviaResourceBundle.getProperty(this.context, DataConstants.CONST_CPN_STRING_RESOURCE, this.locale, "emxCPN.Label.EditSection.StructuredReleaseCriteria");
        this.segmentFieldLabel = EnoviaResourceBundle.getProperty(this.context, DataConstants.CONST_CPN_STRING_RESOURCE, this.locale, "emxCPN.Common.Segment");
        this.classFieldLabel = EnoviaResourceBundle.getProperty(this.context, DataConstants.CONST_CPN_STRING_RESOURCE, this.locale, "emxCPN.Label.EditSection.Class");
        this.reportedFunctionFieldLabel = EnoviaResourceBundle.getProperty(this.context, DataConstants.CONST_CPN_STRING_RESOURCE, this.locale, "emxCPN.Label.EditSection.ReportedFunction");
        this.businessAreaFieldLabel = "Business Area";
        this.productCategoryPlatformFieldLabel = "Product Category Platform";
        this.materialFunctionFieldLabel = "Function";

        this.partTypeFieldID = "pgCreateProductPart";
        this.phaseFieldID = "pgDIPreferencesPhase";
        this.manufacturingStatusFieldID = EnoviaResourceBundle.getProperty(this.context, DataConstants.CONST_COMPONENTS_STRING_RESOURCE, this.locale, "emxComponents.MaturityStatus.Field");
        this.releaseStatusCriteriaFieldID = EnoviaResourceBundle.getProperty(this.context, DataConstants.CONST_COMPONENTS_STRING_RESOURCE, this.locale, "emxComponents.StructuredReleaseCriteriaReq.Field");
        this.segmentFieldID = EnoviaResourceBundle.getProperty(this.context, DataConstants.CONST_COMPONENTS_STRING_RESOURCE, this.locale, "emxComponents.Segment.Field");
        this.classFieldID = EnoviaResourceBundle.getProperty(this.context, DataConstants.CONST_COMPONENTS_STRING_RESOURCE, this.locale, "emxComponents.Class.Field");
        this.reportedFunctionFieldID = EnoviaResourceBundle.getProperty(this.context, DataConstants.CONST_COMPONENTS_STRING_RESOURCE, this.locale, "emxComponents.ReportedFunction.Field");
        this.businessAreaFieldID = "pgBusinessArea";
        this.productCategoryPlatformFieldID = "pgProductCategoryPlatform";
        this.materialFunctionFieldID = "pgMaterialFunction";

        this.loadReportedFunctionJson(rawMaterialPreferenceUtil, userPreferenceUtil);
        this.loadMaterialFunctionJson(rawMaterialPreferenceUtil);


        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);


        logger.info("RawMaterialPreference instantiation - took|" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
    }

    void loadPartTypeJson(RawMaterialPreferenceUtil rawMaterialPreferenceUtil, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        this.partTypeJson = rawMaterialPreferenceUtil.getPartTypeJson(this.context, userPreferenceUtil);
    }

    void loadPhaseJson(RawMaterialPreferenceUtil rawMaterialPreferenceUtil, UserPreferenceUtil userPreferenceUtil) throws Exception {
        this.phaseJson = rawMaterialPreferenceUtil.getPhaseJson(this.context, userPreferenceUtil, this.partType);
    }

    void loadMfgStatusJson(RawMaterialPreferenceUtil rawMaterialPreferenceUtil, UserPreferenceUtil userPreferenceUtil) throws Exception {
        this.mfgStatusJson = rawMaterialPreferenceUtil.getManufacturingStatusJson(this.context, userPreferenceUtil, this.partType, this.phase);
    }

    void loadReleaseCriteriaJson(RawMaterialPreferenceUtil rawMaterialPreferenceUtil, UserPreferenceUtil userPreferenceUtil) throws Exception {
        this.releaseCriteriaJson = rawMaterialPreferenceUtil.getReleaseStatusCriteriaJson(this.context, userPreferenceUtil);
    }

    void loadSegmentJson(RawMaterialPreferenceUtil rawMaterialPreferenceUtil, CacheManagement cacheManagement, UserPreferenceUtil userPreferenceUtil) throws Exception {
        this.segmentJson = rawMaterialPreferenceUtil.getSegmentJson(this.context, cacheManagement, userPreferenceUtil);
    }

    void loadClassesJson(RawMaterialPreferenceUtil rawMaterialPreferenceUtil, CacheManagement cacheManagement, UserPreferenceUtil userPreferenceUtil) throws MatrixException {
        this.classesJson = rawMaterialPreferenceUtil.getClassesJson(this.context, cacheManagement, userPreferenceUtil);
    }

    void loadReportedFunctionJson(RawMaterialPreferenceUtil rawMaterialPreferenceUtil, UserPreferenceUtil userPreferenceUtil) throws MatrixException {
        this.reportedFunctionJson = rawMaterialPreferenceUtil.getReportedFunctionJson(this.context, userPreferenceUtil, this.partType);
    }

    void loadBusinessAreaJson(RawMaterialPreferenceUtil rawMaterialPreferenceUtil, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        this.businessAreaJson = rawMaterialPreferenceUtil.getBusinessAreaJson(this.context, userPreferenceUtil);
    }

    void loadProductCategoryPlatformJson(RawMaterialPreferenceUtil rawMaterialPreferenceUtil, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        this.productCategoryPlatformJson = rawMaterialPreferenceUtil.getProductCategoryPlatformJson(this.context, userPreferenceUtil, this.businessArea);
    }

    void loadMaterialFunctionJson(RawMaterialPreferenceUtil rawMaterialPreferenceUtil) throws MatrixException {
        this.materialFunctionJson = rawMaterialPreferenceUtil.getMaterialFunctionJson(this.context);
    }

    void getPartType(RawMaterialPreferenceUtil rawMaterialPreferenceUtil, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        Map<String, StringList> partTypeRanges = rawMaterialPreferenceUtil.getPartTypeRanges(this.context, userPreferenceUtil);
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
        // Reported Function is same for all Packaging Parts. so take RM type as baseline.
        Map<Object, Object> reportedInfoMap = userPreferenceUtil.getReportedFunctionRangeByType(this.context, pgV3Constants.TYPE_RAWMATERIALPART);
        if (null != reportedInfoMap && !reportedInfoMap.isEmpty()) {
            this.reportedFunctionDisplayOptions.addAll((StringList) reportedInfoMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES));
            this.reportedFunctionOptions.addAll((StringList) reportedInfoMap.get(DataConstants.CONST_FIELD_CHOICES));
        } else {
            logger.log(Level.WARNING, "Reported Function range is empty");
        }
    }

    void getBusinessArea(UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        Map<String, StringList> rangeMap = userPreferenceUtil.getBusinessAreaRanges(this.context);
        if (null != rangeMap && !rangeMap.isEmpty()) {
            this.businessAreaDisplayOptions.add(pgV3Constants.SYMBOL_SPACE);
            this.businessAreaOptions.add(pgV3Constants.SYMBOL_SPACE);
            this.businessAreaDisplayOptions.addAll(rangeMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES));
            this.businessAreaOptions.addAll(rangeMap.get(DataConstants.CONST_FIELD_CHOICES));
        } else {
            logger.log(Level.WARNING, "Reported Function range is empty");
        }
    }

    void getProductCategoryPlatformByBusinessArea(RawMaterialPreferenceUtil rawMaterialPreferenceUtil, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        if (UIUtil.isNotNullAndNotEmpty(this.businessArea)) {
            Map<String, StringList> rangeMap = rawMaterialPreferenceUtil.getProductCategoryPlatformByBusinessArea(this.context, this.businessArea, userPreferenceUtil);
            if (null != rangeMap && !rangeMap.isEmpty()) {
                this.productCategoryPlatformDisplayOptions.addAll(rangeMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES));
                this.productCategoryPlatformOptions.addAll(rangeMap.get(DataConstants.CONST_FIELD_CHOICES));
            } else {
                logger.log(Level.WARNING, "Business Area does not have related Product Category Platform");
            }
        } else {
            logger.log(Level.WARNING, "Business Area empty cannot fetch related Product Category Platform");
        }
    }

    void getMaterialFunctionRanges() throws MatrixException {
        Map resultMap = JPO.invoke(context, "pgUserPreferencesCustom", null, "getMaterialFunction", new String[]{}, Map.class);
        if (null != resultMap && !resultMap.isEmpty()) {
            this.materialFunctionOptions = (StringList) resultMap.get(DataConstants.CONST_FIELD_CHOICES);
            this.materialFunctionDisplayOptions = (StringList) resultMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES);
        } else {
            logger.log(Level.WARNING, "Material Function is empty");
        }
    }

    public String getReportedFunctionJson() {
        return reportedFunctionJson;
    }

    public String getMaterialFunctionJson() {
        return materialFunctionJson;
    }

    public String getPartTypeFieldLabel() {
        return partTypeFieldLabel;
    }

    public String getPhaseFieldLabel() {
        return phaseFieldLabel;
    }

    public String getManufacturingStatusFieldLabel() {
        return manufacturingStatusFieldLabel;
    }

    public String getReleaseStatusCriteriaFieldLabel() {
        return releaseStatusCriteriaFieldLabel;
    }

    public String getClassFieldLabel() {
        return classFieldLabel;
    }

    public String getReportedFunctionFieldLabel() {
        return reportedFunctionFieldLabel;
    }

    public String getSegmentFieldLabel() {
        return segmentFieldLabel;
    }

    public String getPartTypeFieldID() {
        return partTypeFieldID;
    }

    public String getPhaseFieldID() {
        return phaseFieldID;
    }

    public String getManufacturingStatusFieldID() {
        return manufacturingStatusFieldID;
    }

    public String getReleaseStatusCriteriaFieldID() {
        return releaseStatusCriteriaFieldID;
    }

    public String getClassFieldID() {
        return classFieldID;
    }

    public String getReportedFunctionFieldID() {
        return reportedFunctionFieldID;
    }

    public String getSegmentFieldID() {
        return segmentFieldID;
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

    public String getBusinessAreaFieldLabel() {
        return businessAreaFieldLabel;
    }

    public String getProductCategoryPlatformFieldLabel() {
        return productCategoryPlatformFieldLabel;
    }

    public String getBusinessAreaFieldID() {
        return businessAreaFieldID;
    }

    public String getProductCategoryPlatformFieldID() {
        return productCategoryPlatformFieldID;
    }

    public StringList getBusinessAreaOptions() {
        return businessAreaOptions;
    }

    public StringList getBusinessAreaDisplayOptions() {
        return businessAreaDisplayOptions;
    }

    public StringList getProductCategoryPlatformOptions() {
        return productCategoryPlatformOptions;
    }

    public StringList getProductCategoryPlatformDisplayOptions() {
        return productCategoryPlatformDisplayOptions;
    }

    public String getBusinessArea() {
        return businessArea;
    }

    public String getProductCategoryPlatform() {
        return productCategoryPlatform;
    }

    public String getBusinessAreaName() {
        return businessAreaName;
    }

    public String getProductCategoryPlatformID() {
        return productCategoryPlatformID;
    }

    public String getMaterialFunction() {
        return materialFunction;
    }

    public String getMaterialFunctionFieldLabel() {
        return materialFunctionFieldLabel;
    }

    public String getMaterialFunctionFieldID() {
        return materialFunctionFieldID;
    }

    public String getMaterialFunctionID() {
        return materialFunctionID;
    }

    public StringList getMaterialFunctionOptions() {
        return materialFunctionOptions;
    }

    public StringList getMaterialFunctionDisplayOptions() {
        return materialFunctionDisplayOptions;
    }

    public String getSegmentName() {
        return segmentName;
    }

    public String getProductCategoryPlatformName() {
        return productCategoryPlatformName;
    }

    public String getReportedFunctionName() {
        return reportedFunctionName;
    }

    public String getMaterialFunctionName() {
        return materialFunctionName;
    }

    public String getBusinessAreaID() {
        return businessAreaID;
    }

    @Override
    public String toString() {
        return "RawMaterialPreference{" +
                "partTypeFieldLabel='" + partTypeFieldLabel + '\'' +
                ", phaseFieldLabel='" + phaseFieldLabel + '\'' +
                ", manufacturingStatusFieldLabel='" + manufacturingStatusFieldLabel + '\'' +
                ", releaseStatusCriteriaFieldLabel='" + releaseStatusCriteriaFieldLabel + '\'' +
                ", classFieldLabel='" + classFieldLabel + '\'' +
                ", reportedFunctionFieldLabel='" + reportedFunctionFieldLabel + '\'' +
                ", segmentFieldLabel='" + segmentFieldLabel + '\'' +
                ", businessAreaFieldLabel='" + businessAreaFieldLabel + '\'' +
                ", productCategoryPlatformFieldLabel='" + productCategoryPlatformFieldLabel + '\'' +
                ", materialFunctionFieldLabel='" + materialFunctionFieldLabel + '\'' +
                ", partTypeFieldID='" + partTypeFieldID + '\'' +
                ", phaseFieldID='" + phaseFieldID + '\'' +
                ", manufacturingStatusFieldID='" + manufacturingStatusFieldID + '\'' +
                ", releaseStatusCriteriaFieldID='" + releaseStatusCriteriaFieldID + '\'' +
                ", classFieldID='" + classFieldID + '\'' +
                ", reportedFunctionFieldID='" + reportedFunctionFieldID + '\'' +
                ", segmentFieldID='" + segmentFieldID + '\'' +
                ", businessAreaFieldID='" + businessAreaFieldID + '\'' +
                ", productCategoryPlatformFieldID='" + productCategoryPlatformFieldID + '\'' +
                ", materialFunctionFieldID='" + materialFunctionFieldID + '\'' +
                ", context=" + context +
                ", user='" + user + '\'' +
                ", locale=" + locale +
                ", partType='" + partType + '\'' +
                ", phase='" + phase + '\'' +
                ", manufacturingStatus='" + manufacturingStatus + '\'' +
                ", releaseCriteria='" + releaseCriteria + '\'' +
                ", classValue='" + classValue + '\'' +
                ", reportedFunction='" + reportedFunction + '\'' +
                ", reportedFunctionName='" + reportedFunctionName + '\'' +
                ", segment='" + segment + '\'' +
                ", segmentName='" + segmentName + '\'' +
                ", partTypeOptions=" + partTypeOptions +
                ", partTypeDisplayOptions=" + partTypeDisplayOptions +
                ", phaseOptions=" + phaseOptions +
                ", phaseDisplayOptions=" + phaseDisplayOptions +
                ", manufacturingStatusOptions=" + manufacturingStatusOptions +
                ", manufacturingStatusDisplayOptions=" + manufacturingStatusDisplayOptions +
                ", releaseCriteriaStatusOptions=" + releaseCriteriaStatusOptions +
                ", releaseCriteriaStatusDisplayOptions=" + releaseCriteriaStatusDisplayOptions +
                ", classOptions=" + classOptions +
                ", classDisplayOptions=" + classDisplayOptions +
                ", reportedFunctionOptions=" + reportedFunctionOptions +
                ", reportedFunctionDisplayOptions=" + reportedFunctionDisplayOptions +
                ", segmentOptions=" + segmentOptions +
                ", segmentDisplayOptions=" + segmentDisplayOptions +
                ", businessAreaOptions=" + businessAreaOptions +
                ", businessAreaDisplayOptions=" + businessAreaDisplayOptions +
                ", productCategoryPlatformOptions=" + productCategoryPlatformOptions +
                ", productCategoryPlatformDisplayOptions=" + productCategoryPlatformDisplayOptions +
                ", businessArea='" + businessArea + '\'' +
                ", businessAreaName='" + businessAreaName + '\'' +
                ", businessAreaID='" + businessAreaID + '\'' +
                ", productCategoryPlatform='" + productCategoryPlatform + '\'' +
                ", productCategoryPlatformID='" + productCategoryPlatformID + '\'' +
                ", productCategoryPlatformName='" + productCategoryPlatformName + '\'' +
                ", materialFunction='" + materialFunction + '\'' +
                ", materialFunctionName='" + materialFunctionName + '\'' +
                ", materialFunctionID='" + materialFunctionID + '\'' +
                ", materialFunctionOptions=" + materialFunctionOptions +
                ", materialFunctionDisplayOptions=" + materialFunctionDisplayOptions +
                ", partTypeJson='" + partTypeJson + '\'' +
                ", phaseJson='" + phaseJson + '\'' +
                ", mfgStatusJson='" + mfgStatusJson + '\'' +
                ", releaseCriteriaJson='" + releaseCriteriaJson + '\'' +
                ", segmentJson='" + segmentJson + '\'' +
                ", classesJson='" + classesJson + '\'' +
                ", reportedFunctionJson='" + reportedFunctionJson + '\'' +
                ", businessAreaJson='" + businessAreaJson + '\'' +
                ", productCategoryPlatformJson='" + productCategoryPlatformJson + '\'' +
                ", materialFunctionJson='" + materialFunctionJson + '\'' +
                '}';
    }
}



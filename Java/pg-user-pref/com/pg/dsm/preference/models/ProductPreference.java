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
import com.pg.dsm.preference.util.ProductPreferenceUtil;
import com.pg.dsm.preference.util.UserPreferenceUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class ProductPreference {
    private static final Logger logger = Logger.getLogger(ProductPreference.class.getName());
    String partTypeFieldLabel;
    String phaseFieldLabel;
    String manufacturingStatusFieldLabel;
    String releaseStatusCriteriaFieldLabel;
    String classFieldLabel;
    String reportedFunctionFieldLabel;
    String segmentFieldLabel;

    String businessAreaFieldLabel;
    String productCategoryPlatformFieldLabel;

    String partTypeFieldID;
    String phaseFieldID;
    String manufacturingStatusFieldID;
    String releaseStatusCriteriaFieldID;
    String classFieldID;
    String reportedFunctionFieldID;
    String segmentFieldID;

    String businessAreaFieldID;
    String productCategoryPlatformFieldID;

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
    String businessArea;
    String businessAreaID;
    String businessAreaName;
    String productCategoryPlatform;
    String productCategoryPlatformName;
    String productCategoryPlatformID;

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

    String productComplianceRequired;
    String productComplianceRequiredFieldLabel;
    String productComplianceRequiredFieldID;
    StringList productComplianceOptions;
    StringList productComplianceDisplayOptions;

    String partTypeJson;
    String phaseJson;
    String mfgStatusJson;
    String releaseCriteriaJson;
    String segmentJson;
    String classesJson;
    String reportedFunctionJson;
    String businessAreaJson;
    String productCategoryPlatformJson;
    String productComplianceJson;

    public ProductPreference(Context context) throws Exception {
        Instant startTime = Instant.now();
        this.context = context;
        this.locale = context.getLocale();
        this.user = context.getUser();

        PreferenceManagement preferenceManagement = new PreferenceManagement(this.context);
        ProductPreferenceUtil productPreferenceUtil = new ProductPreferenceUtil();
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        CacheManagement cacheManagement = new CacheManagement(this.context);

        this.partType = productPreferenceUtil.getPartType(this.context, preferenceManagement, userPreferenceUtil);
        this.phase = productPreferenceUtil.getPhase(this.context, preferenceManagement, userPreferenceUtil);
        this.manufacturingStatus = productPreferenceUtil.getManufacturingStatus(this.context, preferenceManagement, userPreferenceUtil);
        this.releaseCriteria = productPreferenceUtil.getStructureReleaseCriteria(this.context, preferenceManagement, userPreferenceUtil);
        this.classValue = productPreferenceUtil.getClassValue(this.context, preferenceManagement, userPreferenceUtil);
        this.reportedFunction = productPreferenceUtil.getReportedFunction(this.context, preferenceManagement, userPreferenceUtil); // reported function is stored as object id
        this.reportedFunctionName = productPreferenceUtil.getReportedFunctionName(this.context, userPreferenceUtil, this.reportedFunction);
        this.segment = productPreferenceUtil.getSegment(this.context, preferenceManagement, userPreferenceUtil); // segment is stored as object id.
        this.segmentName = productPreferenceUtil.getSegmentName(this.context, userPreferenceUtil, this.segment);
        this.businessArea = productPreferenceUtil.getBusinessArea(this.context, preferenceManagement, userPreferenceUtil); // business area is stored as physical id.
        this.businessAreaName = userPreferenceUtil.getBusinessAreaName(this.context, this.businessArea);
        this.businessAreaID = productPreferenceUtil.getBusinessAreaID(this.context, userPreferenceUtil, this.businessArea);
        this.productCategoryPlatform = productPreferenceUtil.getProductCategoryPlatform(this.context, preferenceManagement, userPreferenceUtil);
        this.productCategoryPlatformID = productPreferenceUtil.getProductCategoryPlatformID(this.context, userPreferenceUtil, this.productCategoryPlatform);
        this.productCategoryPlatformName = productPreferenceUtil.getProductCategoryPlatformName(this.context, userPreferenceUtil, this.productCategoryPlatform);
        this.productComplianceRequired = productPreferenceUtil.getProductComplianceRequired(this.context, preferenceManagement, userPreferenceUtil);

        this.partTypeDisplayOptions = new StringList();
        this.partTypeOptions = new StringList();
        this.getPartType(productPreferenceUtil, userPreferenceUtil);

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
        this.getProductCategoryPlatformByBusinessArea(productPreferenceUtil, userPreferenceUtil);

        this.productComplianceOptions = new StringList();
        this.productComplianceDisplayOptions = new StringList();
        this.getProductComplianceRequiredRanges(userPreferenceUtil);

        this.partTypeFieldLabel = PreferenceConstants.Basic.DI_PREFERRED_PART_TYPE_LABEL_NAME.get();
        this.phaseFieldLabel = PreferenceConstants.Basic.DI_PREFERRED_PHASE_LABEL_NAME.get();
        this.manufacturingStatusFieldLabel = EnoviaResourceBundle.getFrameworkStringResourceProperty(this.context, "emxFramework.Preferences.DIPreferences.DIMaturityStatus", this.locale);
        this.releaseStatusCriteriaFieldLabel = EnoviaResourceBundle.getProperty(this.context, DataConstants.CONST_CPN_STRING_RESOURCE, this.locale, "emxCPN.Label.EditSection.StructuredReleaseCriteria");
        this.segmentFieldLabel = EnoviaResourceBundle.getProperty(this.context, DataConstants.CONST_CPN_STRING_RESOURCE, this.locale, "emxCPN.Common.Segment");
        this.classFieldLabel = EnoviaResourceBundle.getProperty(this.context, DataConstants.CONST_CPN_STRING_RESOURCE, this.locale, "emxCPN.Label.EditSection.Class");
        this.reportedFunctionFieldLabel = EnoviaResourceBundle.getProperty(this.context, DataConstants.CONST_CPN_STRING_RESOURCE, this.locale, "emxCPN.Label.EditSection.ReportedFunction");
        this.businessAreaFieldLabel = "Business Area";
        this.productCategoryPlatformFieldLabel = "Product Category Platform";
        this.productComplianceRequiredFieldLabel = "Is Product Certification or Local Standards Compliance Statement Required";

        this.partTypeFieldID = "pgCreateProductPart";
        this.phaseFieldID = "pgDIPreferencesPhase";
        this.manufacturingStatusFieldID = EnoviaResourceBundle.getProperty(this.context, DataConstants.CONST_COMPONENTS_STRING_RESOURCE, this.locale, "emxComponents.MaturityStatus.Field");
        this.releaseStatusCriteriaFieldID = EnoviaResourceBundle.getProperty(this.context, DataConstants.CONST_COMPONENTS_STRING_RESOURCE, this.locale, "emxComponents.StructuredReleaseCriteriaReq.Field");
        this.segmentFieldID = EnoviaResourceBundle.getProperty(this.context, DataConstants.CONST_COMPONENTS_STRING_RESOURCE, this.locale, "emxComponents.Segment.Field");
        this.classFieldID = EnoviaResourceBundle.getProperty(this.context, DataConstants.CONST_COMPONENTS_STRING_RESOURCE, this.locale, "emxComponents.Class.Field");
        this.reportedFunctionFieldID = EnoviaResourceBundle.getProperty(this.context, DataConstants.CONST_COMPONENTS_STRING_RESOURCE, this.locale, "emxComponents.ReportedFunction.Field");
        this.businessAreaFieldID = "pgBusinessArea";
        this.productCategoryPlatformFieldID = "pgProductCategoryPlatform";
        this.productComplianceRequiredFieldID = "pgProductComplianceRequired";

        this.loadPartTypeJson(productPreferenceUtil, userPreferenceUtil);
        this.loadPhaseJson(productPreferenceUtil, userPreferenceUtil);
        this.loadMfgStatusJson(productPreferenceUtil, userPreferenceUtil);
        this.loadReleaseCriteriaJson(productPreferenceUtil, userPreferenceUtil);
        this.loadSegmentJson(productPreferenceUtil, cacheManagement, userPreferenceUtil);
        this.loadClassesJson(productPreferenceUtil, cacheManagement, userPreferenceUtil);
        this.loadReportedFunctionJson(productPreferenceUtil, userPreferenceUtil);
        this.loadBusinessAreaJson(productPreferenceUtil, userPreferenceUtil);
        this.loadProductCategoryPlatformJson(productPreferenceUtil, userPreferenceUtil);
        this.loadProductComplianceJson(productPreferenceUtil, userPreferenceUtil);

        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("ProductPreference instantiation - took|" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
    }

    void loadPartTypeJson(ProductPreferenceUtil productPreferenceUtil, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        this.partTypeJson = productPreferenceUtil.getPartTypeJson(this.context, userPreferenceUtil);
    }

    void loadPhaseJson(ProductPreferenceUtil productPreferenceUtil, UserPreferenceUtil userPreferenceUtil) throws Exception {
        this.phaseJson = productPreferenceUtil.getPhaseJson(this.context, userPreferenceUtil, this.partType);
    }

    void loadMfgStatusJson(ProductPreferenceUtil productPreferenceUtil, UserPreferenceUtil userPreferenceUtil) throws Exception {
        this.mfgStatusJson = productPreferenceUtil.getManufacturingStatusJson(this.context, userPreferenceUtil, this.partType, this.phase);
    }

    void loadReleaseCriteriaJson(ProductPreferenceUtil productPreferenceUtil, UserPreferenceUtil userPreferenceUtil) throws Exception {
        this.releaseCriteriaJson = productPreferenceUtil.getReleaseStatusCriteriaJson(this.context, userPreferenceUtil);
    }

    void loadSegmentJson(ProductPreferenceUtil productPreferenceUtil, CacheManagement cacheManagement, UserPreferenceUtil userPreferenceUtil) throws Exception {
        this.segmentJson = productPreferenceUtil.getSegmentJson(this.context, cacheManagement, userPreferenceUtil);
    }

    void loadClassesJson(ProductPreferenceUtil productPreferenceUtil, CacheManagement cacheManagement, UserPreferenceUtil userPreferenceUtil) throws MatrixException {
        this.classesJson = productPreferenceUtil.getClassesJson(this.context, cacheManagement, userPreferenceUtil);
    }

    void loadReportedFunctionJson(ProductPreferenceUtil productPreferenceUtil, UserPreferenceUtil userPreferenceUtil) throws MatrixException {
        this.reportedFunctionJson = productPreferenceUtil.getReportedFunctionJson(this.context, userPreferenceUtil, pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART);
    }

    void loadBusinessAreaJson(ProductPreferenceUtil productPreferenceUtil, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        this.businessAreaJson = productPreferenceUtil.getBusinessAreaJson(this.context, userPreferenceUtil);
    }

    void loadProductCategoryPlatformJson(ProductPreferenceUtil productPreferenceUtil, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        this.productCategoryPlatformJson = productPreferenceUtil.getProductCategoryPlatformJson(this.context, userPreferenceUtil, this.businessArea);
    }

    void loadProductComplianceJson(ProductPreferenceUtil productPreferenceUtil, UserPreferenceUtil userPreferenceUtil) throws MatrixException {
        this.productComplianceJson = productPreferenceUtil.getProductComplianceJson(this.context, userPreferenceUtil);
    }

    void getPartType(ProductPreferenceUtil productPreferenceUtil, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        Map<String, StringList> partTypeRanges = productPreferenceUtil.getPartTypeRanges(this.context, userPreferenceUtil);
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
        // Reported Function is same for all Packaging Parts. so take APP type as baseline.
        Map<Object, Object> reportedInfoMap = userPreferenceUtil.getReportedFunctionRangeByType(this.context, pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART);
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

    void getProductCategoryPlatformByBusinessArea(ProductPreferenceUtil productPreferenceUtil, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        if (UIUtil.isNotNullAndNotEmpty(this.businessArea)) {
            Map<String, StringList> rangeMap = productPreferenceUtil.getProductCategoryPlatformByBusinessArea(this.context, this.businessArea, userPreferenceUtil);
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

    void getProductComplianceRequiredRanges(UserPreferenceUtil userPreferenceUtil) throws MatrixException {
        Map<String, StringList> rangeMap = userPreferenceUtil.getProductComplianceRequiredRanges(this.context);
        if (null != rangeMap && !rangeMap.isEmpty()) {
            this.productComplianceOptions = rangeMap.get(DataConstants.CONST_FIELD_CHOICES);
            this.productComplianceDisplayOptions = rangeMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES);
        } else {
            logger.log(Level.WARNING, "Product Compliance is empty");
        }
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

    public String getProductComplianceRequired() {
        return productComplianceRequired;
    }

    public String getProductComplianceRequiredFieldLabel() {
        return productComplianceRequiredFieldLabel;
    }

    public String getProductComplianceRequiredFieldID() {
        return productComplianceRequiredFieldID;
    }

    public StringList getProductComplianceOptions() {
        return productComplianceOptions;
    }

    public StringList getProductComplianceDisplayOptions() {
        return productComplianceDisplayOptions;
    }

    public String getSegmentName() {
        return segmentName;
    }

    public String getReportedFunctionName() {
        return reportedFunctionName;
    }

    public String getProductCategoryPlatformName() {
        return productCategoryPlatformName;
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

    public String getSegmentJson() {
        return segmentJson;
    }

    public String getClassesJson() {
        return classesJson;
    }

    public String getReportedFunctionJson() {
        return reportedFunctionJson;
    }

    public String getBusinessAreaJson() {
        return businessAreaJson;
    }

    public String getProductCategoryPlatformJson() {
        return productCategoryPlatformJson;
    }

    public String getProductComplianceJson() {
        return productComplianceJson;
    }

    public String getBusinessAreaID() {
        return businessAreaID;
    }

    @Override
    public String toString() {
        return "ProductPreference{" +
                "partTypeFieldLabel='" + partTypeFieldLabel + '\'' +
                ", phaseFieldLabel='" + phaseFieldLabel + '\'' +
                ", manufacturingStatusFieldLabel='" + manufacturingStatusFieldLabel + '\'' +
                ", releaseStatusCriteriaFieldLabel='" + releaseStatusCriteriaFieldLabel + '\'' +
                ", classFieldLabel='" + classFieldLabel + '\'' +
                ", reportedFunctionFieldLabel='" + reportedFunctionFieldLabel + '\'' +
                ", segmentFieldLabel='" + segmentFieldLabel + '\'' +
                ", businessAreaFieldLabel='" + businessAreaFieldLabel + '\'' +
                ", productCategoryPlatformFieldLabel='" + productCategoryPlatformFieldLabel + '\'' +
                ", partTypeFieldID='" + partTypeFieldID + '\'' +
                ", phaseFieldID='" + phaseFieldID + '\'' +
                ", manufacturingStatusFieldID='" + manufacturingStatusFieldID + '\'' +
                ", releaseStatusCriteriaFieldID='" + releaseStatusCriteriaFieldID + '\'' +
                ", classFieldID='" + classFieldID + '\'' +
                ", reportedFunctionFieldID='" + reportedFunctionFieldID + '\'' +
                ", segmentFieldID='" + segmentFieldID + '\'' +
                ", businessAreaFieldID='" + businessAreaFieldID + '\'' +
                ", productCategoryPlatformFieldID='" + productCategoryPlatformFieldID + '\'' +
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
                ", businessArea='" + businessArea + '\'' +
                ", businessAreaID='" + businessAreaID + '\'' +
                ", businessAreaName='" + businessAreaName + '\'' +
                ", productCategoryPlatform='" + productCategoryPlatform + '\'' +
                ", productCategoryPlatformName='" + productCategoryPlatformName + '\'' +
                ", productCategoryPlatformID='" + productCategoryPlatformID + '\'' +
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
                ", productComplianceRequired='" + productComplianceRequired + '\'' +
                ", productComplianceRequiredFieldLabel='" + productComplianceRequiredFieldLabel + '\'' +
                ", productComplianceRequiredFieldID='" + productComplianceRequiredFieldID + '\'' +
                ", productComplianceOptions=" + productComplianceOptions +
                ", productComplianceDisplayOptions=" + productComplianceDisplayOptions +
                ", partTypeJson='" + partTypeJson + '\'' +
                ", phaseJson='" + phaseJson + '\'' +
                ", mfgStatusJson='" + mfgStatusJson + '\'' +
                ", releaseCriteriaJson='" + releaseCriteriaJson + '\'' +
                ", segmentJson='" + segmentJson + '\'' +
                ", classesJson='" + classesJson + '\'' +
                ", reportedFunctionJson='" + reportedFunctionJson + '\'' +
                ", businessAreaJson='" + businessAreaJson + '\'' +
                ", productCategoryPlatformJson='" + productCategoryPlatformJson + '\'' +
                ", productComplianceJson='" + productComplianceJson + '\'' +
                '}';
    }
}

package com.pg.dsm.preference.models;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.designtools.datamanagement.DataConstants;
import com.pg.designtools.util.CacheManagement;
import com.pg.designtools.util.PreferenceManagement;
import com.pg.dsm.preference.enumeration.PreferenceConstants;
import com.pg.dsm.preference.util.UserPreferenceUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class DIPreference {
    private static final Logger logger = Logger.getLogger(DIPreference.class.getName());
    Context context;
    String user;
    Locale locale;
    PreferenceManagement preferenceManagement;
    CacheManagement cacheManagement;
    StringList partTypeOptions;
    StringList partTypeDisplayOptions;
    String partTypePreferredValue;
    String partTypeFieldLabel;
    String partTypeFieldID;
    StringList phaseOptions;
    StringList phaseDisplayOptions;
    String phasePreferredValue;
    String phaseFieldLabel;
    String phaseFieldID;
    StringList manufacturingStatusOptions;
    String manufacturingStatusPreferredValue;
    String manufacturingStatusFieldLabel;
    String manufacturingStatusFieldID;

    StringList releaseStatusCriteriaOptions;
    String releaseStatusCriteriaPreferredValue;
    String releaseStatusCriteriaFieldLabel;
    String releaseStatusCriteriaFieldID;

    StringList packagingMaterialTypeOptions;
    StringList packagingMaterialTypeDisplayOptions;
    String packagingMaterialTypePreferredValue;

    String packagingMaterialTypePreferredID;
    String packagingMaterialTypeFieldLabel;
    String packagingMaterialTypeFieldID;

    StringList segmentOptions;
    StringList segmentDisplayOptions;
    String segmentPreferredValue;
    String segmentFieldLabel;
    String segmentFieldID;

    StringList classOptions;
    StringList classDisplayOptions;
    String classPreferredValue;
    String classFieldLabel;
    String classFieldID;

    StringList reportedFunctionOptions;
    StringList reportedFunctionDisplayOptions;
    String reportedFunctionPreferredValue;
    String reportedFunctionFieldLabel;
    String reportedFunctionFieldID;

    /**
     * @param context
     * @throws Exception
     */
    public DIPreference(Context context) throws Exception {
        Instant startTime = Instant.now();
        this.context = context;
        this.locale = context.getLocale();
        this.user = context.getUser();
        this.preferenceManagement = new PreferenceManagement(context);
        this.cacheManagement = new CacheManagement(context);

        UserPreferenceUtil preferenceUtil = new UserPreferenceUtil();
        this.partTypePreferredValue = preferenceUtil.getPreferredDIPartType(context);
        this.phasePreferredValue = preferenceUtil.getPreferredDIPhase(context);
        this.manufacturingStatusPreferredValue = preferenceUtil.getPreferredDIManufacturingStatus(context);

        this.releaseStatusCriteriaPreferredValue = preferenceUtil.getPreferredDIStructureReleaseCriteria(context);
        this.packagingMaterialTypePreferredValue = preferenceUtil.getPreferredDIPackagingMaterialType(context);
        this.packagingMaterialTypePreferredID = preferenceUtil.getPackagingMaterialTypePreferredID(context);

        this.segmentPreferredValue = preferenceUtil.getPreferredDISegment(context);
        this.classPreferredValue = preferenceUtil.getPreferredDIClass(context);
        this.reportedFunctionPreferredValue = preferenceUtil.getPreferredDIReportedFunction(context);

        this.partTypeDisplayOptions = new StringList();
        this.partTypeOptions = new StringList();
        this.getPartType();

        this.phaseDisplayOptions = new StringList();
        this.phaseOptions = new StringList();
        this.getPhase(preferenceUtil);

        this.manufacturingStatusOptions = new StringList();
        this.getManufacturingStatus(preferenceUtil);

        this.releaseStatusCriteriaOptions = new StringList();
        this.getReleaseStatusCriteria();

        this.packagingMaterialTypeDisplayOptions = new StringList();
        this.packagingMaterialTypeOptions = new StringList();
        this.getPackagingMaterialType();

        this.segmentDisplayOptions = new StringList();
        this.segmentOptions = new StringList();
        this.getSegment();

        this.classDisplayOptions = new StringList();
        this.classOptions = new StringList();
        this.getClasses();

        this.reportedFunctionDisplayOptions = new StringList();
        this.reportedFunctionOptions = new StringList();
        this.getReportedFunctionRange(preferenceUtil);

        this.partTypeFieldLabel = PreferenceConstants.Basic.DI_PREFERRED_PART_TYPE_LABEL_NAME.get();
        this.phaseFieldLabel = PreferenceConstants.Basic.DI_PREFERRED_PHASE_LABEL_NAME.get();
        this.manufacturingStatusFieldLabel = EnoviaResourceBundle.getFrameworkStringResourceProperty(this.context, "emxFramework.Preferences.DIPreferences.DIMaturityStatus", this.locale);
        this.releaseStatusCriteriaFieldLabel = EnoviaResourceBundle.getProperty(this.context, DataConstants.CONST_CPN_STRING_RESOURCE, this.locale, "emxCPN.Label.EditSection.StructuredReleaseCriteria");
        this.packagingMaterialTypeFieldLabel = EnoviaResourceBundle.getProperty(this.context, DataConstants.CONST_CPN_STRING_RESOURCE, this.locale, "emxCPN.Label.EditSection.PackagingMaterialType");
        this.segmentFieldLabel = EnoviaResourceBundle.getProperty(this.context, DataConstants.CONST_CPN_STRING_RESOURCE, this.locale, "emxCPN.Common.Segment");
        this.classFieldLabel = EnoviaResourceBundle.getProperty(this.context, DataConstants.CONST_CPN_STRING_RESOURCE, this.locale, "emxCPN.Label.EditSection.Class");
        this.reportedFunctionFieldLabel = EnoviaResourceBundle.getProperty(this.context, DataConstants.CONST_CPN_STRING_RESOURCE, this.locale, "emxCPN.Label.EditSection.ReportedFunction");

        this.partTypeFieldID = "pgCreateProductPart";
        this.phaseFieldID = "pgDIPreferencesPhase";
        this.manufacturingStatusFieldID = EnoviaResourceBundle.getProperty(this.context, DataConstants.CONST_COMPONENTS_STRING_RESOURCE, this.locale, "emxComponents.MaturityStatus.Field");
        this.releaseStatusCriteriaFieldID = EnoviaResourceBundle.getProperty(this.context, DataConstants.CONST_COMPONENTS_STRING_RESOURCE, this.locale, "emxComponents.StructuredReleaseCriteriaReq.Field");
        this.packagingMaterialTypeFieldID = EnoviaResourceBundle.getProperty(this.context, DataConstants.CONST_COMPONENTS_STRING_RESOURCE, this.locale, "emxComponents.PackagingMaterialType.Field");
        this.segmentFieldID = EnoviaResourceBundle.getProperty(this.context, DataConstants.CONST_COMPONENTS_STRING_RESOURCE, this.locale, "emxComponents.Segment.Field");
        this.classFieldID = EnoviaResourceBundle.getProperty(this.context, DataConstants.CONST_COMPONENTS_STRING_RESOURCE, this.locale, "emxComponents.Class.Field");
        this.reportedFunctionFieldID = EnoviaResourceBundle.getProperty(this.context, DataConstants.CONST_COMPONENTS_STRING_RESOURCE, this.locale, "emxComponents.ReportedFunction.Field");

        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("DIPreference instantiation - took|" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
    }

    void getPartType() throws MatrixException {
        HashMap paramMap = new HashMap();
        HashMap requestedMap = new HashMap();
        paramMap.put("requestMap", requestedMap);
        Map partInfoMap = (Map) JPO.invoke(this.context, "emxCPNProductDataPartStage", null, "getProductDataTypes", JPO.packArgs(paramMap), Map.class);
        if (null != partInfoMap && !partInfoMap.isEmpty()) {
            this.partTypeDisplayOptions.add(pgV3Constants.SYMBOL_SPACE);
            this.partTypeOptions.add(pgV3Constants.SYMBOL_SPACE);
            this.partTypeDisplayOptions.addAll((StringList) partInfoMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES));
            this.partTypeOptions.addAll((StringList) partInfoMap.get(DataConstants.CONST_FIELD_CHOICES));
            String formulationType = PreferenceConstants.Type.TYPE_FORMULATION.getName(this.context);
            this.partTypeDisplayOptions.add(10, formulationType);
            this.partTypeOptions.add(10, formulationType);
        } else {
            logger.log(Level.WARNING, "Part Type (Map) is null or empty");
        }
    }

    void getPhase(UserPreferenceUtil preferenceUtil) throws Exception {
        if (UIUtil.isNotNullAndNotEmpty(this.partTypePreferredValue)) {
            Map phaseInfoMap = preferenceUtil.getPhase(this.context, this.partTypePreferredValue);
            if (null != phaseInfoMap && !phaseInfoMap.isEmpty()) {
                this.phaseDisplayOptions.addAll((StringList) phaseInfoMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES));
                this.phaseOptions.addAll((StringList) phaseInfoMap.get(DataConstants.CONST_FIELD_CHOICES));
            }
        } else {
            logger.log(Level.WARNING, "Preferred (Type) is empty");
        }
    }

    void getReleaseStatusCriteria() throws MatrixException {
        StringList resultList = JPO.invoke(this.context, DataConstants.CONST_DSOUTIL_JPO, null, DataConstants.CONST_BLANK_YESNO_METHOD, null, StringList.class);
        if (null != resultList && !resultList.isEmpty()) {
            this.releaseStatusCriteriaOptions.addAll(resultList);
        } else {
            logger.log(Level.WARNING, "Release Criteria (List) is null or empty");
        }
    }

    void getPackagingMaterialType() throws MatrixException {
        @SuppressWarnings("unchecked")
        Map<Object, Object> packagingInfoMap = (Map<Object, Object>) cacheManagement.getPickListItems(this.context, DataConstants.CONST_PICKLIST_PACKMATERIALTYPE);
        if (null != packagingInfoMap && !packagingInfoMap.isEmpty()) {
            this.packagingMaterialTypeDisplayOptions.addAll((StringList) packagingInfoMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES));
            this.packagingMaterialTypeOptions.addAll((StringList) packagingInfoMap.get(DataConstants.CONST_FIELD_CHOICES));
        } else {
            logger.log(Level.WARNING, "Packaging Material Type (Map) is null or empty");
        }
    }

    void getSegment() throws MatrixException {
        @SuppressWarnings("unchecked")
        Map<Object, Object> segmentInfoMap = (Map<Object, Object>) cacheManagement.getPickListItems(this.context, DataConstants.CONST_PICKLIST_SEGMENT);
        if (null != segmentInfoMap && !segmentInfoMap.isEmpty()) {
            this.segmentDisplayOptions.addAll((StringList) segmentInfoMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES));
            this.segmentOptions.addAll((StringList) segmentInfoMap.get(DataConstants.CONST_FIELD_CHOICES));
        } else {
            logger.log(Level.WARNING, "Segments (Map) is null or empty");
        }
    }

    void getClasses() throws MatrixException {
        @SuppressWarnings("unchecked")
        Map<Object, Object> classInfoMap = (Map<Object, Object>) cacheManagement.getPickListItems(this.context, DataConstants.CONST_PICKLIST_CLASS);
        if (null != classInfoMap && !classInfoMap.isEmpty()) {
            this.classDisplayOptions.addAll((StringList) classInfoMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES));
            this.classOptions.addAll((StringList) classInfoMap.get(DataConstants.CONST_FIELD_CHOICES));
        } else {
            logger.log(Level.WARNING, "Classes (Map) is null or empty");
        }
    }

    void getReportedFunction() throws MatrixException {
        @SuppressWarnings("unchecked")
        Map<Object, Object> reportedInfoMap = (Map<Object, Object>) cacheManagement.getPickListItems(this.context, DataConstants.CONST_PICKLIST_REPORTEDFUNCTION);
        if (null != reportedInfoMap && !reportedInfoMap.isEmpty()) {
            this.reportedFunctionDisplayOptions.addAll((StringList) reportedInfoMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES));
            this.reportedFunctionOptions.addAll((StringList) reportedInfoMap.get(DataConstants.CONST_FIELD_CHOICES));
        } else {
            logger.log(Level.WARNING, "Reported Function (Map) is null or empty");
        }
    }

    /**
     * @param preferenceUtil
     * @throws MatrixException
     */
    void getReportedFunctionRange(UserPreferenceUtil preferenceUtil) throws MatrixException {
        if (UIUtil.isNotNullAndNotEmpty(this.partTypePreferredValue)) {
            Map<Object, Object> reportedInfoMap = preferenceUtil.getReportedFunctionRangeByType(this.context, this.partTypePreferredValue);
            if (null != reportedInfoMap && !reportedInfoMap.isEmpty()) {
                this.reportedFunctionDisplayOptions.addAll((StringList) reportedInfoMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES));
                this.reportedFunctionOptions.addAll((StringList) reportedInfoMap.get(DataConstants.CONST_FIELD_CHOICES));
            } else {
                logger.log(Level.WARNING, "Reported Function (Map) is null or empty");
            }
        } else {
            logger.log(Level.WARNING, "Reported Function depends on Type selection which is null or empty");
        }
    }

    void getManufacturingStatus(UserPreferenceUtil preferenceUtil) throws Exception {
        if (UIUtil.isNotNullAndNotEmpty(this.partTypePreferredValue) && UIUtil.isNotNullAndNotEmpty(this.phasePreferredValue)) {
            this.manufacturingStatusOptions.addAll(preferenceUtil.getManufacturingStatus(context, this.partTypePreferredValue, this.phasePreferredValue));
        } else {
            logger.log(Level.WARNING, "Preferred (Type OR Phase) is empty");
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

    public PreferenceManagement getPreferenceManagement() {
        return preferenceManagement;
    }

    public CacheManagement getCacheManagement() {
        return cacheManagement;
    }

    public StringList getPartTypeOptions() {
        return partTypeOptions;
    }

    public StringList getPartTypeDisplayOptions() {
        return partTypeDisplayOptions;
    }

    public String getPartTypePreferredValue() {
        return partTypePreferredValue;
    }

    public String getPartTypeFieldLabel() {
        return partTypeFieldLabel;
    }

    public String getPartTypeFieldID() {
        return partTypeFieldID;
    }

    public StringList getPhaseOptions() {
        return phaseOptions;
    }

    public StringList getPhaseDisplayOptions() {
        return phaseDisplayOptions;
    }

    public String getPhasePreferredValue() {
        return phasePreferredValue;
    }

    public String getPhaseFieldLabel() {
        return phaseFieldLabel;
    }

    public String getPhaseFieldID() {
        return phaseFieldID;
    }

    public StringList getManufacturingStatusOptions() {
        return manufacturingStatusOptions;
    }

    public String getManufacturingStatusPreferredValue() {
        return manufacturingStatusPreferredValue;
    }

    public String getManufacturingStatusFieldLabel() {
        return manufacturingStatusFieldLabel;
    }

    public String getManufacturingStatusFieldID() {
        return manufacturingStatusFieldID;
    }

    public StringList getReleaseStatusCriteriaOptions() {
        return releaseStatusCriteriaOptions;
    }

    public String getReleaseStatusCriteriaPreferredValue() {
        return releaseStatusCriteriaPreferredValue;
    }

    public String getReleaseStatusCriteriaFieldLabel() {
        return releaseStatusCriteriaFieldLabel;
    }

    public String getReleaseStatusCriteriaFieldID() {
        return releaseStatusCriteriaFieldID;
    }

    public StringList getPackagingMaterialTypeOptions() {
        return packagingMaterialTypeOptions;
    }

    public StringList getPackagingMaterialTypeDisplayOptions() {
        return packagingMaterialTypeDisplayOptions;
    }

    public String getPackagingMaterialTypePreferredValue() {
        return packagingMaterialTypePreferredValue;
    }

    public String getPackagingMaterialTypeFieldLabel() {
        return packagingMaterialTypeFieldLabel;
    }

    public String getPackagingMaterialTypeFieldID() {
        return packagingMaterialTypeFieldID;
    }

    public StringList getSegmentOptions() {
        return segmentOptions;
    }

    public StringList getSegmentDisplayOptions() {
        return segmentDisplayOptions;
    }

    public String getSegmentPreferredValue() {
        return segmentPreferredValue;
    }

    public String getSegmentFieldLabel() {
        return segmentFieldLabel;
    }

    public String getSegmentFieldID() {
        return segmentFieldID;
    }

    public StringList getClassOptions() {
        return classOptions;
    }

    public StringList getClassDisplayOptions() {
        return classDisplayOptions;
    }

    public String getClassPreferredValue() {
        return classPreferredValue;
    }

    public String getClassFieldLabel() {
        return classFieldLabel;
    }

    public String getClassFieldID() {
        return classFieldID;
    }

    public StringList getReportedFunctionOptions() {
        return reportedFunctionOptions;
    }

    public StringList getReportedFunctionDisplayOptions() {
        return reportedFunctionDisplayOptions;
    }

    public String getReportedFunctionPreferredValue() {
        return reportedFunctionPreferredValue;
    }

    public String getReportedFunctionFieldLabel() {
        return reportedFunctionFieldLabel;
    }

    public String getReportedFunctionFieldID() {
        return reportedFunctionFieldID;
    }

    public String getPackagingMaterialTypePreferredID() {
        return packagingMaterialTypePreferredID;
    }

    @Override
    public String toString() {
        return "DIPreference{" +
                "context=" + context +
                ", user='" + user + '\'' +
                ", locale=" + locale +
                ", preferenceManagement=" + preferenceManagement +
                ", cacheManagement=" + cacheManagement +
                ", partTypeOptions=" + partTypeOptions +
                ", partTypeDisplayOptions=" + partTypeDisplayOptions +
                ", partTypePreferredValue='" + partTypePreferredValue + '\'' +
                ", partTypeFieldLabel='" + partTypeFieldLabel + '\'' +
                ", partTypeFieldID='" + partTypeFieldID + '\'' +
                ", phaseOptions=" + phaseOptions +
                ", phaseDisplayOptions=" + phaseDisplayOptions +
                ", phasePreferredValue='" + phasePreferredValue + '\'' +
                ", phaseFieldLabel='" + phaseFieldLabel + '\'' +
                ", phaseFieldID='" + phaseFieldID + '\'' +
                ", manufacturingStatusOptions=" + manufacturingStatusOptions +
                ", manufacturingStatusPreferredValue='" + manufacturingStatusPreferredValue + '\'' +
                ", manufacturingStatusFieldLabel='" + manufacturingStatusFieldLabel + '\'' +
                ", manufacturingStatusFieldID='" + manufacturingStatusFieldID + '\'' +
                ", releaseStatusCriteriaOptions=" + releaseStatusCriteriaOptions +
                ", releaseStatusCriteriaPreferredValue='" + releaseStatusCriteriaPreferredValue + '\'' +
                ", releaseStatusCriteriaFieldLabel='" + releaseStatusCriteriaFieldLabel + '\'' +
                ", releaseStatusCriteriaFieldID='" + releaseStatusCriteriaFieldID + '\'' +
                ", packagingMaterialTypeOptions=" + packagingMaterialTypeOptions +
                ", packagingMaterialTypeDisplayOptions=" + packagingMaterialTypeDisplayOptions +
                ", packagingMaterialTypePreferredValue='" + packagingMaterialTypePreferredValue + '\'' +
                ", packagingMaterialTypePreferredID='" + packagingMaterialTypePreferredID + '\'' +
                ", packagingMaterialTypeFieldLabel='" + packagingMaterialTypeFieldLabel + '\'' +
                ", packagingMaterialTypeFieldID='" + packagingMaterialTypeFieldID + '\'' +
                ", segmentOptions=" + segmentOptions +
                ", segmentDisplayOptions=" + segmentDisplayOptions +
                ", segmentPreferredValue='" + segmentPreferredValue + '\'' +
                ", segmentFieldLabel='" + segmentFieldLabel + '\'' +
                ", segmentFieldID='" + segmentFieldID + '\'' +
                ", classOptions=" + classOptions +
                ", classDisplayOptions=" + classDisplayOptions +
                ", classPreferredValue='" + classPreferredValue + '\'' +
                ", classFieldLabel='" + classFieldLabel + '\'' +
                ", classFieldID='" + classFieldID + '\'' +
                ", reportedFunctionOptions=" + reportedFunctionOptions +
                ", reportedFunctionDisplayOptions=" + reportedFunctionDisplayOptions +
                ", reportedFunctionPreferredValue='" + reportedFunctionPreferredValue + '\'' +
                ", reportedFunctionFieldLabel='" + reportedFunctionFieldLabel + '\'' +
                ", reportedFunctionFieldID='" + reportedFunctionFieldID + '\'' +
                '}';
    }
}

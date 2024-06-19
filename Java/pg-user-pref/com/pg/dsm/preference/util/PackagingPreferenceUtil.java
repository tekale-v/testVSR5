package com.pg.dsm.preference.util;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.designtools.datamanagement.DataConstants;
import com.pg.designtools.util.CacheManagement;
import com.pg.designtools.util.PreferenceManagement;
import com.pg.dsm.preference.enumeration.PreferenceConstants;
import com.pg.dsm.preference.enumeration.UserPreferenceTemplateConstants;
import com.pg.dsm.preference.models.DSMUserPreferenceConfig;
import com.pg.dsm.preference.services.PreferenceConfigLoader;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class PackagingPreferenceUtil {
    private static final Logger logger = Logger.getLogger(PackagingPreferenceUtil.class.getName());

    public PackagingPreferenceUtil() {
    }

    public void setUserPreference(Context context, String user, String key, String value) throws Exception {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        userPreferenceUtil.setUserPreference(context, user, key, value);
    }

    public void setUserPreference(Context context, String key, String value) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        userPreferenceUtil.setUserPreference(context, key, value);
    }

    public String getPartType(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, PreferenceConstants.Preferences.PACKAGING_PART_TYPE.get());
    }

    public String getPartType(Context context, PreferenceManagement preferenceManagement, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, preferenceManagement, PreferenceConstants.Preferences.PACKAGING_PART_TYPE.get());
    }

    public String getPhase(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, PreferenceConstants.Preferences.PACKAGING_PHASE.get());
    }

    public String getPhase(Context context, PreferenceManagement preferenceManagement, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, preferenceManagement, PreferenceConstants.Preferences.PACKAGING_PHASE.get());
    }

    public String getManufacturingStatus(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, PreferenceConstants.Preferences.PACKAGING_MANUFACTURING_STATUS.get());
    }

    public String getManufacturingStatus(Context context, PreferenceManagement preferenceManagement, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, preferenceManagement, PreferenceConstants.Preferences.PACKAGING_MANUFACTURING_STATUS.get());
    }

    public String getStructureReleaseCriteria(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, PreferenceConstants.Preferences.PACKAGING_RELEASE_CRITERIA.get());
    }

    public String getStructureReleaseCriteria(Context context, PreferenceManagement preferenceManagement, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, preferenceManagement, PreferenceConstants.Preferences.PACKAGING_RELEASE_CRITERIA.get());
    }

    public String getClassValue(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, PreferenceConstants.Preferences.PACKAGING_CLASS.get());
    }

    public String getClassValueIDs(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        String ids = DomainConstants.EMPTY_STRING;
        try {
            ids = userPreferenceUtil.getPipeSeparatedIDs(context,   // context
                    DataConstants.CONST_PICKLIST_CLASS,      // type
                    getClassValue(context));    // names - pipe separated.
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error: " + e);
            throw e;
        }
        return ids;

    }

    public String getClassValuePhysicalIDs(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        String ids = DomainConstants.EMPTY_STRING;
        try {
            ids = userPreferenceUtil.getPipeSeparatedPhyscialIDs(context,   // context
                    DataConstants.CONST_PICKLIST_CLASS,      // type
                    getClassValue(context));    // names - pipe separated.
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error: " + e);
            throw e;
        }
        return ids;

    }

    public String getClassValue(Context context, PreferenceManagement preferenceManagement, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, preferenceManagement, PreferenceConstants.Preferences.PACKAGING_CLASS.get());
    }

    public String getReportedFunctionName(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        String reportedFunctionID = userPreferenceUtil.getAdminPropertyWithoutCache(context, PreferenceConstants.Preferences.PACKAGING_REPORTED_FUNCTION.get());
        return userPreferenceUtil.getPipeSeparatedNameFromID(context, reportedFunctionID);
    }

    public String getReportedFunctionID(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, PreferenceConstants.Preferences.PACKAGING_REPORTED_FUNCTION.get());
    }

    public String getReportedFunctionPhysicalID(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getPhysicalIDFromObjectID(context, getReportedFunctionID(context));
    }

    public String getReportedFunction(Context context, PreferenceManagement preferenceManagement, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, preferenceManagement, PreferenceConstants.Preferences.PACKAGING_REPORTED_FUNCTION.get());
    }

    public String getReportedFunctionName(Context context, PreferenceManagement preferenceManagement, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        String reportedFunctionID = userPreferenceUtil.getAdminPropertyWithoutCache(context, preferenceManagement, PreferenceConstants.Preferences.PACKAGING_REPORTED_FUNCTION.get());
        return userPreferenceUtil.getPipeSeparatedNameFromID(context, reportedFunctionID);
    }

    public String getReportedFunctionName(Context context, UserPreferenceUtil userPreferenceUtil, String reportedFunction) throws FrameworkException {
        return userPreferenceUtil.getPipeSeparatedNameFromID(context, reportedFunction);
    }

    public String getSegment(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, PreferenceConstants.Preferences.PACKAGING_SEGMENT.get());
    }

    public String getSegmentName(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        String segmentID = userPreferenceUtil.getAdminPropertyWithoutCache(context, PreferenceConstants.Preferences.PACKAGING_SEGMENT.get());
        return userPreferenceUtil.getPipeSeparatedNameFromID(context, segmentID);
    }

    public String getSegmentID(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, PreferenceConstants.Preferences.PACKAGING_SEGMENT.get());
    }

    public String getSegmentPhysicalID(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getPhysicalIDFromObjectID(context, getSegmentID(context));
    }

    public String getSegment(Context context, PreferenceManagement preferenceManagement, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, preferenceManagement, PreferenceConstants.Preferences.PACKAGING_SEGMENT.get());
    }

    public String getSegmentName(Context context, PreferenceManagement preferenceManagement, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        String segmentID = userPreferenceUtil.getAdminPropertyWithoutCache(context, preferenceManagement, PreferenceConstants.Preferences.PACKAGING_SEGMENT.get());
        return userPreferenceUtil.getPipeSeparatedNameFromID(context, segmentID);
    }

    public String getPackagingMaterialType(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, PreferenceConstants.Preferences.PACKAGING_MATERIAL_TYPE.get());
    }

    public String getPackagingMaterialTypeIDs(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        String ids = DomainConstants.EMPTY_STRING;
        try {
            ids = userPreferenceUtil.getPipeSeparatedIDs(context,   // context
                    DataConstants.CONST_PICKLIST_PACKMATERIALTYPE,      // type
                    getPackagingMaterialType(context));    // names - pipe separated.
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error: " + e);
            throw e;
        }
        return ids;

    }

    public String getPackagingMaterialTypePhysicalIDs(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        String ids = DomainConstants.EMPTY_STRING;
        try {
            ids = userPreferenceUtil.getPipeSeparatedPhyscialIDs(context,   // context
                    DataConstants.CONST_PICKLIST_PACKMATERIALTYPE,      // type
                    getPackagingMaterialType(context));    // names - pipe separated.
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error: " + e);
            throw e;
        }
        return ids;

    }

    public String getPackagingMaterialType(Context context, PreferenceManagement preferenceManagement, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, preferenceManagement, PreferenceConstants.Preferences.PACKAGING_MATERIAL_TYPE.get());
    }

    public String getPackagingComponentTypeIDs(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        String ids = DomainConstants.EMPTY_STRING;
        try {
            ids = userPreferenceUtil.getPipeSeparatedIDs(context,   // context
                    "pgPLIPackComponentType",      // type     // type
                    getPackagingComponentType(context));    // names - pipe separated.
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error: " + e);
            throw e;
        }
        return ids;

    }

    public String getPackagingComponentTypePhysicalIDs(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        String ids = DomainConstants.EMPTY_STRING;
        try {
            ids = userPreferenceUtil.getPipeSeparatedPhyscialIDs(context,   // context
                    "pgPLIPackComponentType",      // type
                    getPackagingComponentType(context));    // names - pipe separated.
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error: " + e);
            throw e;
        }
        return ids;

    }

    public String getPackagingComponentType(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, PreferenceConstants.Preferences.PACKAGING_COMPONENT_TYPE.get());
    }

    public String getPackagingComponentType(Context context, PreferenceManagement preferenceManagement, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, preferenceManagement, PreferenceConstants.Preferences.PACKAGING_COMPONENT_TYPE.get());
    }

    public String getBaseUnitOfMeasure(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, PreferenceConstants.Preferences.PACKAGING_BASE_UOM.get());
    }

    public String getBaseUnitOfMeasureIDs(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        String ids = DomainConstants.EMPTY_STRING;
        try {
            ids = userPreferenceUtil.getPipeSeparatedIDs(context,   // context
                    "pgPLIBUOM",      // type     // type
                    getBaseUnitOfMeasure(context));    // names - pipe separated.
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error: " + e);
            throw e;
        }
        return ids;

    }

    public String getBaseUnitOfMeasurePhysicalIDs(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        String ids = DomainConstants.EMPTY_STRING;
        try {
            ids = userPreferenceUtil.getPipeSeparatedPhyscialIDs(context,   // context
                    "pgPLIBUOM",      // type
                    getBaseUnitOfMeasure(context));    // names - pipe separated.
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error: " + e);
            throw e;
        }
        return ids;

    }

    public String getBaseUnitOfMeasure(Context context, PreferenceManagement preferenceManagement, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, preferenceManagement, PreferenceConstants.Preferences.PACKAGING_BASE_UOM.get());
    }

    public Map<String, StringList> getPartTypeRanges(Context context, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        DSMUserPreferenceConfig userPreferenceConfig = userPreferenceUtil.getDSMUserPreferenceConfig(context);
        String allowedPartTypesForPackagingPreference = userPreferenceConfig.getAllowedPartTypesForPackagingPreference();
        return userPreferenceUtil.getPartTypeRanges(context, allowedPartTypesForPackagingPreference);
    }

    public Map<String, StringList> getPartTypeRanges(Context context) throws FrameworkException {
        String packagingTypes = DomainConstants.EMPTY_STRING;
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        PreferenceConfigLoader preferenceConfigLoader = userPreferenceUtil.getPreferenceConfigLoader(context);
        if (preferenceConfigLoader.isLoaded()) {
            packagingTypes = preferenceConfigLoader.getPreferenceConfig().getPackagingPreferenceConfig().getTypes();
        }
        return userPreferenceUtil.getPartTypeRanges(context, packagingTypes);
    }

    public String getPartTypeJson(Context context) throws FrameworkException {
        JsonObjectBuilder jsonOutput = Json.createObjectBuilder();
        JsonArrayBuilder jsonArr = Json.createArrayBuilder();
        Map<String, StringList> partTypeRanges = getPartTypeRanges(context);
        if (null != partTypeRanges && !partTypeRanges.isEmpty()) {
            StringList displayChoices = partTypeRanges.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES);
            StringList choices = partTypeRanges.get(DataConstants.CONST_FIELD_CHOICES);
            jsonArr.add(getBlankChoiceJson()); // append a blank choice.
            JsonObjectBuilder json;
            for (int i = 0; i < choices.size(); i++) {
                json = Json.createObjectBuilder();
                json.add("choice", choices.get(i));
                json.add("displayChoice", displayChoices.get(i));
                jsonArr.add(json);
            }
        } else {
            logger.log(Level.WARNING, "Type range map is empty");
        }
        jsonOutput.add("output", jsonArr.build());
        return jsonOutput.build().toString();
    }

    public String getPartTypeJson(String configurations) throws FrameworkException {
        JsonObjectBuilder jsonOutput = Json.createObjectBuilder();
        JsonArrayBuilder jsonArr = Json.createArrayBuilder();
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        Map<String, StringList> partTypeRanges = userPreferenceUtil.getPartTypes(configurations);
        if (null != partTypeRanges && !partTypeRanges.isEmpty()) {
            List<String> displayChoices = partTypeRanges.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES);
            List<String> choices = partTypeRanges.get(DataConstants.CONST_FIELD_CHOICES);
            jsonArr.add(getBlankChoiceJson()); // append a blank choice.
            JsonObjectBuilder json;
            for (int i = 0; i < choices.size(); i++) {
                json = Json.createObjectBuilder();
                json.add("choice", choices.get(i));
                json.add("displayChoice", displayChoices.get(i));
                jsonArr.add(json);
            }
        } else {
            logger.log(Level.WARNING, "Type range map is empty");
        }
        jsonOutput.add("output", jsonArr.build());
        return jsonOutput.build().toString();
    }

    public String getPartTypeJson() throws FrameworkException {
        return getPartTypeJson(UserPreferenceTemplateConstants.Basic.PACKAGING_TYPES_CONFIGURATION.get());
    }

    public String getPhaseJson(Context context, UserPreferenceUtil userPreferenceUtil, String type) throws Exception {
        JsonObjectBuilder jsonOutput = Json.createObjectBuilder();
        JsonArrayBuilder jsonArr = Json.createArrayBuilder();
        if (UIUtil.isNotNullAndNotEmpty(type)) {
            Map phaseRanges = userPreferenceUtil.getPhase(context, type);
            if (null != phaseRanges && !phaseRanges.isEmpty()) {
                StringList displayChoices = (StringList) phaseRanges.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES);
                StringList choices = (StringList) phaseRanges.get(DataConstants.CONST_FIELD_CHOICES);
                JsonObjectBuilder json;
                for (int i = 0; i < choices.size(); i++) {
                    json = Json.createObjectBuilder();
                    json.add("choice", choices.get(i));
                    json.add("displayChoice", displayChoices.get(i));
                    jsonArr.add(json);
                }
            }
        } else {
            logger.log(Level.WARNING, "Type is empty cannot fetch Phase");
        }
        jsonOutput.add("output", jsonArr.build());
        return jsonOutput.build().toString();
    }

    public String getManufacturingStatusJson(Context context, UserPreferenceUtil userPreferenceUtil, String type, String phase) throws Exception {
        JsonObjectBuilder jsonOutput = Json.createObjectBuilder();
        JsonArrayBuilder jsonArr = Json.createArrayBuilder();
        if (UIUtil.isNotNullAndNotEmpty(type) && UIUtil.isNotNullAndNotEmpty(phase)) {
            Map<String, StringList> rangeMap = userPreferenceUtil.getManufacturingStatusRange(context, type, phase);
            if (null != rangeMap && !rangeMap.isEmpty()) {
                StringList displayChoices = rangeMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES);
                StringList choices = rangeMap.get(DataConstants.CONST_FIELD_CHOICES);
                JsonObjectBuilder json;
                for (int i = 0; i < choices.size(); i++) {
                    json = Json.createObjectBuilder();
                    json.add("choice", choices.get(i));
                    json.add("displayChoice", displayChoices.get(i));
                    jsonArr.add(json);
                }
            }
        } else {
            logger.log(Level.WARNING, "Type or Phase is empty cannot fetch Mfg Status");
        }
        jsonOutput.add("output", jsonArr.build());
        return jsonOutput.build().toString();
    }

    public String getReleaseCriteriaJson(Context context, UserPreferenceUtil userPreferenceUtil) throws MatrixException {
        JsonObjectBuilder jsonOutput = Json.createObjectBuilder();
        JsonArrayBuilder jsonArr = Json.createArrayBuilder();
        Map<String, StringList> rangeMap = userPreferenceUtil.getReleaseCriteriaStatus(context);
        if (null != rangeMap && !rangeMap.isEmpty()) {
            StringList displayChoices = rangeMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES);
            StringList choices = rangeMap.get(DataConstants.CONST_FIELD_CHOICES);
            JsonObjectBuilder json;
            for (int i = 0; i < choices.size(); i++) {
                json = Json.createObjectBuilder();
                json.add("choice", choices.get(i));
                json.add("displayChoice", displayChoices.get(i));
                jsonArr.add(json);
            }
        } else {
            logger.log(Level.WARNING, "Release Criteria range is empty");
        }
        jsonOutput.add("output", jsonArr.build());
        return jsonOutput.build().toString();
    }

    public String getReleaseCriteriaJson(Context context) throws MatrixException {
        return getReleaseCriteriaJson(context, new UserPreferenceUtil());
    }

    public String getSegmentJson(Context context, CacheManagement cacheManagement, UserPreferenceUtil userPreferenceUtil) throws MatrixException {
        JsonObjectBuilder jsonOutput = Json.createObjectBuilder();
        JsonArrayBuilder jsonArr = Json.createArrayBuilder();
        Map<String, StringList> rangeMap = userPreferenceUtil.getSegment(context, cacheManagement);
        if (null != rangeMap && !rangeMap.isEmpty()) {
            StringList displayChoices = rangeMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES);
            StringList choices = rangeMap.get(DataConstants.CONST_FIELD_CHOICES);
            JsonObjectBuilder json;
            for (int i = 0; i < choices.size(); i++) {
                json = Json.createObjectBuilder();
                json.add(DomainConstants.SELECT_ID, choices.get(i));
                json.add(DomainConstants.SELECT_NAME, displayChoices.get(i));
                json.add(PreferenceConstants.Basic.PHYSICAL_ID.get(), userPreferenceUtil.getPhysicalIDFromObjectID(context, choices.get(i)));
                jsonArr.add(json);
            }
        } else {
            logger.log(Level.WARNING, "Segments range is empty");
        }
        jsonOutput.add("output", jsonArr.build());
        return jsonOutput.build().toString();
    }

    public String getSegmentJson(Context context) throws MatrixException {
        return getSegmentJson(context, new CacheManagement(context), new UserPreferenceUtil());
    }

    public String getClassesJson(Context context, CacheManagement cacheManagement, UserPreferenceUtil userPreferenceUtil) throws MatrixException {
        JsonObjectBuilder jsonOutput = Json.createObjectBuilder();
        JsonArrayBuilder jsonArr = Json.createArrayBuilder();
        Map<String, StringList> rangeMap = userPreferenceUtil.getClasses(context, cacheManagement);
        if (null != rangeMap && !rangeMap.isEmpty()) {
            StringList displayChoices = rangeMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES);
            StringList choices = rangeMap.get(DataConstants.CONST_FIELD_CHOICES);
            JsonObjectBuilder json;
            for (int i = 0; i < choices.size(); i++) {
                json = Json.createObjectBuilder();
                json.add("choice", choices.get(i));
                json.add("displayChoice", displayChoices.get(i));
                json.add(PreferenceConstants.Basic.PHYSICAL_ID.get(), userPreferenceUtil.getPhysicalIDFromTNR(context, DataConstants.CONST_PICKLIST_CLASS, choices.get(i)));
                jsonArr.add(json);
            }
        } else {
            logger.log(Level.WARNING, "Classes range is empty");
        }
        jsonOutput.add("output", jsonArr.build());
        return jsonOutput.build().toString();
    }

    public String getClassesJson(Context context) throws MatrixException {
        return getClassesJson(context, new CacheManagement(context), new UserPreferenceUtil());
    }

    public String getReportedFunctionJson(Context context, UserPreferenceUtil userPreferenceUtil, String type) throws MatrixException {
        JsonObjectBuilder jsonOutput = Json.createObjectBuilder();
        JsonArrayBuilder jsonArr = Json.createArrayBuilder();
        if (UIUtil.isNotNullAndNotEmpty(type)) {
            Map<Object, Object> reportedInfoMap = userPreferenceUtil.getReportedFunctionRangeByType(context, type);
            if (null != reportedInfoMap && !reportedInfoMap.isEmpty()) {
                StringList displayChoices = (StringList) reportedInfoMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES);
                StringList choices = (StringList) reportedInfoMap.get(DataConstants.CONST_FIELD_CHOICES);
                JsonObjectBuilder json;
                for (int i = 0; i < choices.size(); i++) {
                    json = Json.createObjectBuilder();
                    json.add(DomainConstants.SELECT_ID, choices.get(i));
                    json.add(DomainConstants.SELECT_NAME, displayChoices.get(i));
                    json.add(PreferenceConstants.Basic.PHYSICAL_ID.get(), userPreferenceUtil.getPhysicalIDFromObjectID(context, choices.get(i)));
                    jsonArr.add(json);
                }
            } else {
                logger.log(Level.WARNING, "Reported Function range is empty");
            }
        } else {
            logger.log(Level.WARNING, "Type is empty cannot fetch Reported Function");
        }
        jsonOutput.add("output", jsonArr.build());
        return jsonOutput.build().toString();
    }

    public String getReportedFunctionJson(Context context) throws MatrixException {
        return getReportedFunctionJson(context, new UserPreferenceUtil(), pgV3Constants.TYPE_PACKAGINGMATERIALPART);
    }

    public String getPackagingMaterialTypeJson(Context context, CacheManagement cacheManagement, UserPreferenceUtil userPreferenceUtil) throws MatrixException {
        JsonObjectBuilder jsonOutput = Json.createObjectBuilder();
        JsonArrayBuilder jsonArr = Json.createArrayBuilder();
        Map<String, StringList> rangeMap = userPreferenceUtil.getPackagingMaterialType(context, cacheManagement);
        if (null != rangeMap && !rangeMap.isEmpty()) {
            StringList displayChoices = rangeMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES);
            StringList choices = rangeMap.get(DataConstants.CONST_FIELD_CHOICES);
            JsonObjectBuilder json;
            for (int i = 0; i < choices.size(); i++) {
                json = Json.createObjectBuilder();
                json.add("choice", choices.get(i));
                json.add("displayChoice", displayChoices.get(i));
                json.add(PreferenceConstants.Basic.PHYSICAL_ID.get(), userPreferenceUtil.getPhysicalIDFromTNR(context, DataConstants.CONST_PICKLIST_PACKMATERIALTYPE, choices.get(i)));
                jsonArr.add(json);
            }
        } else {
            logger.log(Level.WARNING, "Packaging Material Type range is empty");
        }
        jsonOutput.add("output", jsonArr.build());
        return jsonOutput.build().toString();
    }

    public String getPackagingMaterialTypeJson(Context context) throws MatrixException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        CacheManagement cacheManagement = new CacheManagement(context);
        return getPackagingMaterialTypeJson(context, cacheManagement, userPreferenceUtil);
    }

    public String getPackagingComponentTypeJson(Context context, UserPreferenceUtil userPreferenceUtil) throws MatrixException {
        JsonObjectBuilder jsonOutput = Json.createObjectBuilder();
        JsonArrayBuilder jsonArr = Json.createArrayBuilder();
        Map<String, StringList> rangeMap = userPreferenceUtil.getPackagingComponentType(context);
        if (null != rangeMap && !rangeMap.isEmpty()) {
            StringList displayChoices = rangeMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES);
            StringList choices = rangeMap.get(DataConstants.CONST_FIELD_CHOICES);
            JsonObjectBuilder json;
            for (int i = 0; i < choices.size(); i++) {
                json = Json.createObjectBuilder();
                json.add("choice", choices.get(i));
                json.add("displayChoice", displayChoices.get(i));
                json.add(PreferenceConstants.Basic.PHYSICAL_ID.get(), userPreferenceUtil.getPhysicalIDFromTNR(context, "pgPLIPackComponentType", choices.get(i)));
                jsonArr.add(json);
            }
        } else {
            logger.log(Level.WARNING, "Packaging Component Type range is empty");
        }
        jsonOutput.add("output", jsonArr.build());
        return jsonOutput.build().toString();
    }

    public String getPackagingComponentTypeJson(Context context) throws MatrixException {
        return getPackagingComponentTypeJson(context, new UserPreferenceUtil());
    }

    public String getBaseUnitOfMeasureJson(Context context, UserPreferenceUtil userPreferenceUtil) throws MatrixException {
        JsonObjectBuilder jsonOutput = Json.createObjectBuilder();
        JsonArrayBuilder jsonArr = Json.createArrayBuilder();
        Map<String, StringList> rangeMap = userPreferenceUtil.getBaseUnitOfMeasure(context);
        if (null != rangeMap && !rangeMap.isEmpty()) {
            StringList displayChoices = rangeMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES);
            StringList choices = rangeMap.get(DataConstants.CONST_FIELD_CHOICES);
            JsonObjectBuilder json;
            for (int i = 0; i < choices.size(); i++) {
                json = Json.createObjectBuilder();
                json.add("choice", choices.get(i));
                json.add("displayChoice", displayChoices.get(i));
                json.add(PreferenceConstants.Basic.PHYSICAL_ID.get(), userPreferenceUtil.getPhysicalIDFromTNR(context, "pgPLIBUOM", choices.get(i)));
                jsonArr.add(json);
            }
        } else {
            logger.log(Level.WARNING, "Base UoM range is empty");
        }
        jsonOutput.add("output", jsonArr.build());
        return jsonOutput.build().toString();
    }

    public String getBaseUnitOfMeasureJson(Context context) throws MatrixException {
        return getBaseUnitOfMeasureJson(context, new UserPreferenceUtil());
    }

    private JsonObjectBuilder getBlankChoiceJson() {
        JsonObjectBuilder json = Json.createObjectBuilder();
        json.add("choice", DomainConstants.EMPTY_STRING);
        json.add("displayChoice", DomainConstants.EMPTY_STRING);
        return json;
    }
}

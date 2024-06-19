package com.pg.dsm.preference.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.StringUtil;
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
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class RawMaterialPreferenceUtil {
    private static final Logger logger = Logger.getLogger(RawMaterialPreferenceUtil.class.getName());

    public RawMaterialPreferenceUtil() {
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
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, PreferenceConstants.Preferences.RAW_MATERIAL_PART_TYPE.get());
    }

    public String getPartType(Context context, PreferenceManagement preferenceManagement, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, preferenceManagement, PreferenceConstants.Preferences.RAW_MATERIAL_PART_TYPE.get());
    }

    public String getPhase(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, PreferenceConstants.Preferences.RAW_MATERIAL_PHASE.get());
    }

    public String getPhase(Context context, PreferenceManagement preferenceManagement, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, preferenceManagement, PreferenceConstants.Preferences.RAW_MATERIAL_PHASE.get());
    }

    public String getManufacturingStatus(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, PreferenceConstants.Preferences.RAW_MATERIAL_MANUFACTURING_STATUS.get());

    }

    public String getManufacturingStatus(Context context, PreferenceManagement preferenceManagement, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, preferenceManagement, PreferenceConstants.Preferences.RAW_MATERIAL_MANUFACTURING_STATUS.get());

    }

    public String getStructureReleaseCriteria(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, PreferenceConstants.Preferences.RAW_MATERIAL_RELEASE_CRITERIA.get());

    }

    public String getStructureReleaseCriteria(Context context, PreferenceManagement preferenceManagement, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, preferenceManagement, PreferenceConstants.Preferences.RAW_MATERIAL_RELEASE_CRITERIA.get());

    }

    public String getClassValue(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, PreferenceConstants.Preferences.RAW_MATERIAL_CLASS.get());

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
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, preferenceManagement, PreferenceConstants.Preferences.RAW_MATERIAL_CLASS.get());

    }

    public String getReportedFunction(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, PreferenceConstants.Preferences.RAW_MATERIAL_REPORTED_FUNCTION.get());
    }

    public String getReportedFunctionName(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        String reportedFunctionID = userPreferenceUtil.getAdminPropertyWithoutCache(context, PreferenceConstants.Preferences.RAW_MATERIAL_REPORTED_FUNCTION.get());
        return userPreferenceUtil.getPipeSeparatedNameFromID(context, reportedFunctionID);
    }

    public String getReportedFunctionID(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, PreferenceConstants.Preferences.RAW_MATERIAL_REPORTED_FUNCTION.get());
    }

    public String getReportedFunctionPhysicalID(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getPhysicalIDFromObjectID(context, getReportedFunctionID(context));
    }

    public String getReportedFunction(Context context, PreferenceManagement preferenceManagement, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, preferenceManagement, PreferenceConstants.Preferences.RAW_MATERIAL_REPORTED_FUNCTION.get());
    }

    public String getReportedFunctionName(Context context, UserPreferenceUtil userPreferenceUtil, String reportedFunctionID) throws FrameworkException {
        return userPreferenceUtil.getPipeSeparatedNameFromID(context, reportedFunctionID);
    }

    public String getSegment(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, PreferenceConstants.Preferences.RAW_MATERIAL_SEGMENT.get());
    }

    public String getSegmentName(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        String segmentID = userPreferenceUtil.getAdminPropertyWithoutCache(context, PreferenceConstants.Preferences.RAW_MATERIAL_SEGMENT.get());
        return userPreferenceUtil.getPipeSeparatedNameFromID(context, segmentID);
    }

    public String getSegmentID(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, PreferenceConstants.Preferences.RAW_MATERIAL_SEGMENT.get());
    }

    public String getSegmentPhysicalID(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getPhysicalIDFromObjectID(context, getSegmentID(context));
    }

    public String getSegment(Context context, PreferenceManagement preferenceManagement, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, preferenceManagement, PreferenceConstants.Preferences.RAW_MATERIAL_SEGMENT.get());

    }

    public String getSegmentName(Context context, UserPreferenceUtil userPreferenceUtil, String segmentID) throws FrameworkException {
        return userPreferenceUtil.getPipeSeparatedNameFromID(context, segmentID);
    }

    public String getBusinessArea(Context context, PreferenceManagement preferenceManagement, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, preferenceManagement, PreferenceConstants.Preferences.RAW_MATERIAL_BUSINESS_AREA.get());
    }

    public String getBusinessArea(Context context) throws FrameworkException {
        PreferenceManagement preferenceManagement = new PreferenceManagement(context);
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, preferenceManagement, PreferenceConstants.Preferences.RAW_MATERIAL_BUSINESS_AREA.get());
    }

    public String getBusinessAreaName(Context context) throws FrameworkException {
        PreferenceManagement preferenceManagement = new PreferenceManagement(context);
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        String businessAreaID = userPreferenceUtil.getAdminPropertyWithoutCache(context, preferenceManagement, PreferenceConstants.Preferences.RAW_MATERIAL_BUSINESS_AREA.get());
        return userPreferenceUtil.getPipeSeparatedNameFromID(context, businessAreaID);
    }

    public String getBusinessAreaID(Context context) throws FrameworkException {
        String businessArea = getBusinessArea(context);
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getPipeSeparatedObjectIDFromPhysicalID(context, businessArea);
    }

    public String getBusinessAreaPhysicalID(Context context) throws FrameworkException {
        return getBusinessArea(context);
    }

    public String getProductCategoryPlatform(Context context) throws FrameworkException {
        PreferenceManagement preferenceManagement = new PreferenceManagement(context);
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, preferenceManagement, PreferenceConstants.Preferences.RAW_MATERIAL_PRODUCT_CATEGORY_PLATFORM.get());
    }

    public String getProductCategoryPlatformName(Context context) throws FrameworkException {
        PreferenceManagement preferenceManagement = new PreferenceManagement(context);
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        String productCategoryPlatformID = userPreferenceUtil.getAdminPropertyWithoutCache(context, preferenceManagement, PreferenceConstants.Preferences.RAW_MATERIAL_PRODUCT_CATEGORY_PLATFORM.get());
        return userPreferenceUtil.getPipeSeparatedNameFromID(context, productCategoryPlatformID);
    }

    public String getProductCategoryPlatformID(Context context) throws FrameworkException {
        String productCategoryPlatform = getProductCategoryPlatform(context);
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getPipeSeparatedObjectIDFromPhysicalID(context, productCategoryPlatform);
    }

    public String getProductCategoryPlatformPhysicalID(Context context) throws FrameworkException {
        return getProductCategoryPlatform(context);
    }

    public String getProductCategoryPlatform(Context context, PreferenceManagement preferenceManagement, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, preferenceManagement, PreferenceConstants.Preferences.RAW_MATERIAL_PRODUCT_CATEGORY_PLATFORM.get());
    }

    public String getProductCategoryPlatformID(Context context, UserPreferenceUtil userPreferenceUtil, String productCategoryPlatform) throws FrameworkException {
        return userPreferenceUtil.getPipeSeparatedObjectIDFromPhysicalID(context, productCategoryPlatform);
    }

    public String getBusinessAreaID(Context context, UserPreferenceUtil userPreferenceUtil, String businessArea) throws FrameworkException {
        return userPreferenceUtil.getPipeSeparatedObjectIDFromPhysicalID(context, businessArea);
    }

    public String getProductCategoryPlatformName(Context context, UserPreferenceUtil userPreferenceUtil, String productCategoryPlatform) throws FrameworkException {
        return userPreferenceUtil.getPipeSeparatedNameFromID(context, productCategoryPlatform);
    }

    public String getMaterialFunction(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, PreferenceConstants.Preferences.RAW_MATERIAL_FUNCTION.get());
    }

    public String getMaterialFunctionName(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        String materialFunctionID = userPreferenceUtil.getAdminPropertyWithoutCache(context, PreferenceConstants.Preferences.RAW_MATERIAL_FUNCTION.get());
        return userPreferenceUtil.getPipeSeparatedNameFromID(context, materialFunctionID);
    }

    public String getMaterialFunctionID(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, PreferenceConstants.Preferences.RAW_MATERIAL_FUNCTION.get());

    }

    public String getMaterialFunctionPhysicalID(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getPipeSeparatedPhysicalIDFromObjectID(context, getMaterialFunctionID(context));

    }

    public String getMaterialFunction(Context context, PreferenceManagement preferenceManagement, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, preferenceManagement, PreferenceConstants.Preferences.RAW_MATERIAL_FUNCTION.get());
    }

    public String getMaterialFunctionName(Context context, UserPreferenceUtil userPreferenceUtil, String materialFunctionID) throws FrameworkException {
        return userPreferenceUtil.getPipeSeparatedNameFromID(context, materialFunctionID);
    }

    public String getMaterialFunctionID(Context context, PreferenceManagement preferenceManagement, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, preferenceManagement, PreferenceConstants.Preferences.RAW_MATERIAL_FUNCTION.get());
    }


    public Map<String, StringList> getPartTypeRanges(Context context, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        String rawMaterialTypes = DomainConstants.EMPTY_STRING;
        PreferenceConfigLoader preferenceConfigLoader = userPreferenceUtil.getPreferenceConfigLoader(context);
        if (preferenceConfigLoader.isLoaded()) {
            rawMaterialTypes = preferenceConfigLoader.getPreferenceConfig().getRawMaterialPreferenceConfig().getTypes();
        }
        return userPreferenceUtil.getPartTypeRanges(context, rawMaterialTypes);
    }

    public Map<String, StringList> getPartTypeRanges(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        DSMUserPreferenceConfig userPreferenceConfig = userPreferenceUtil.getDSMUserPreferenceConfig(context);
        String allowedPartTypesForPackagingPreference = userPreferenceConfig.getAllowedPartTypesForPackagingPreference();
        return userPreferenceUtil.getPartTypeRanges(context, allowedPartTypesForPackagingPreference);
    }

    public Map<String, StringList> getProductCategoryPlatformByBusinessArea(Context context, String businessAreaID, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        Map<String, StringList> rangeMap = new HashMap<>();
        StringList businessAreaList = StringUtil.split(businessAreaID, pgV3Constants.SYMBOL_PIPE);
        if (null != businessAreaList && !businessAreaList.isEmpty()) {
            rangeMap = userPreferenceUtil.getProductCategoryPlatformByBusinessAreaRange(context, businessAreaList);
        }
        return rangeMap;
    }

    public String getPartTypeJson(Context context, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        JsonObjectBuilder jsonOutput = Json.createObjectBuilder();
        JsonArrayBuilder jsonArr = Json.createArrayBuilder();
        Map<String, StringList> partTypeRanges = getPartTypeRanges(context, userPreferenceUtil);
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
        return getPartTypeJson(UserPreferenceTemplateConstants.Basic.RAW_MATERIAL_TYPES_CONFIGURATION.get());
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
            } else {
                logger.log(Level.WARNING, "No Mfg Status found for given type: " + type);
            }
        } else {
            logger.log(Level.WARNING, "Type or Phase is empty cannot fetch Mfg Status");
        }
        jsonOutput.add("output", jsonArr.build());
        return jsonOutput.build().toString();
    }

    public String getReleaseStatusCriteriaJson(Context context, UserPreferenceUtil userPreferenceUtil) throws MatrixException {
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

    public String getReleaseStatusCriteriaJson(Context context) throws MatrixException {
        return getReleaseStatusCriteriaJson(context, new UserPreferenceUtil());
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

    public String getBusinessAreaJson(Context context, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        JsonObjectBuilder jsonOutput = Json.createObjectBuilder();
        JsonArrayBuilder jsonArr = Json.createArrayBuilder();
        MapList objectList = userPreferenceUtil.findBusinessArea(context);
        if (null != objectList && !objectList.isEmpty()) {
            objectList.sort("name", "ascending", "String");
            Iterator iterator = objectList.iterator();
            Map<Object, Object> resultMap;
            JsonObjectBuilder json;
            while (iterator.hasNext()) {
                resultMap = (Map<Object, Object>) iterator.next();
                json = Json.createObjectBuilder();
                json.add(DomainConstants.SELECT_ID, (String) resultMap.get(DomainConstants.SELECT_ID));
                json.add(DomainConstants.SELECT_NAME, (String) resultMap.get(DomainConstants.SELECT_NAME));
                json.add(PreferenceConstants.Basic.PHYSICAL_ID.get(), (String) resultMap.get(PreferenceConstants.Basic.PHYSICAL_ID.get()));
                jsonArr.add(json);
            }
        } else {
            logger.log(Level.WARNING, "No Business Area found");
        }
        jsonOutput.add("output", jsonArr.build());
        return jsonOutput.build().toString();
    }

    public String getBusinessAreaJson(Context context) throws FrameworkException {
        return getBusinessAreaJson(context, new UserPreferenceUtil());
    }

    public String getProductCategoryPlatformJson(Context context, UserPreferenceUtil userPreferenceUtil, String businessArea) throws FrameworkException {
        JsonObjectBuilder jsonOutput = Json.createObjectBuilder();
        JsonArrayBuilder jsonArr = Json.createArrayBuilder();
        if (UIUtil.isNotNullAndNotEmpty(businessArea)) {
            StringList businessAreaList = StringUtil.split(businessArea, pgV3Constants.SYMBOL_PIPE);
            if (null != businessAreaList && !businessAreaList.isEmpty()) {
                MapList resultList = new MapList();
                for (String businessAreaID : businessAreaList) {
                    MapList objectList = userPreferenceUtil.getProductCategoryPlatformByBusinessArea(context, businessAreaID);
                    if (null != objectList && !objectList.isEmpty()) {
                        resultList.addAll(objectList);
                    }
                }
                if (null != resultList && !resultList.isEmpty()) {
                    resultList.sort("name", "ascending", "String");
                    Iterator iterator = resultList.iterator();
                    Map<Object, Object> resultMap;
                    JsonObjectBuilder json;
                    while (iterator.hasNext()) {
                        resultMap = (Map<Object, Object>) iterator.next();
                        json = Json.createObjectBuilder();
                        json.add(DomainConstants.SELECT_ID, (String) resultMap.get(DomainConstants.SELECT_ID));
                        json.add(DomainConstants.SELECT_NAME, (String) resultMap.get(DomainConstants.SELECT_NAME));
                        json.add(PreferenceConstants.Basic.PHYSICAL_ID.get(), (String) resultMap.get(PreferenceConstants.Basic.PHYSICAL_ID.get()));
                        jsonArr.add(json);
                    }
                } else {
                    logger.log(Level.WARNING, "Business Area does not have related Product Category Platform");
                }
            } else {
                logger.log(Level.WARNING, "Business Area is empty, hence cannot fetch related Product Category Platform");
            }
        } else {
            logger.log(Level.WARNING, "Business Area empty cannot fetch related Product Category Platform");
        }
        jsonOutput.add("output", jsonArr.build());
        return jsonOutput.build().toString();
    }

    public String getMaterialFunctionJson(Context context) throws MatrixException {
        JsonObjectBuilder jsonOutput = Json.createObjectBuilder();
        JsonArrayBuilder jsonArr = Json.createArrayBuilder();
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        Map resultMap = JPO.invoke(context, "pgUserPreferencesCustom", null, "getMaterialFunction", new String[]{}, Map.class);
        if (null != resultMap && !resultMap.isEmpty()) {
            StringList choices = (StringList) resultMap.get(DataConstants.CONST_FIELD_CHOICES);
            StringList displayChoices = (StringList) resultMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES);
            JsonObjectBuilder json;
            for (int i = 0; i < choices.size(); i++) {
                json = Json.createObjectBuilder();
                json.add(DomainConstants.SELECT_ID, choices.get(i));
                json.add(DomainConstants.SELECT_NAME, displayChoices.get(i));
                json.add(PreferenceConstants.Basic.PHYSICAL_ID.get(), userPreferenceUtil.getPhysicalIDFromObjectID(context, choices.get(i)));
                jsonArr.add(json);
            }
        } else {
            logger.log(Level.WARNING, "Material Function is empty");
        }
        jsonOutput.add("output", jsonArr.build());
        return jsonOutput.build().toString();
    }

    private JsonObjectBuilder getBlankChoiceJson() {
        JsonObjectBuilder json = Json.createObjectBuilder();
        json.add("choice", DomainConstants.EMPTY_STRING);
        json.add("displayChoice", DomainConstants.EMPTY_STRING);
        return json;
    }
}

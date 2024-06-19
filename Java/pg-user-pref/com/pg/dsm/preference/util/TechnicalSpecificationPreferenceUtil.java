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
import com.pg.designtools.datamanagement.DataConstants;
import com.pg.designtools.util.CacheManagement;
import com.pg.dsm.preference.enumeration.UserPreferenceTemplateConstants;
import com.pg.dsm.preference.services.PreferenceConfigLoader;

import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class TechnicalSpecificationPreferenceUtil {
    private static final Logger logger = Logger.getLogger(TechnicalSpecificationPreferenceUtil.class.getName());

    public TechnicalSpecificationPreferenceUtil() {
    }

    public Map<String, StringList> getPartTypeRanges(Context context, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        String technicalSpecTypes = DomainConstants.EMPTY_STRING;
        PreferenceConfigLoader preferenceConfigLoader = userPreferenceUtil.getPreferenceConfigLoader(context);
        if (preferenceConfigLoader.isLoaded()) {
            technicalSpecTypes = preferenceConfigLoader.getPreferenceConfig().getTechnicalSpecPreferenceConfig().getTypes();
        }
        return userPreferenceUtil.getPartTypeRanges(context, technicalSpecTypes);
    }

    private JsonObjectBuilder getBlankChoiceJson() {
        JsonObjectBuilder json = Json.createObjectBuilder();
        json.add("choice", DomainConstants.EMPTY_STRING);
        json.add("displayChoice", DomainConstants.EMPTY_STRING);
        return json;
    }

    /**
     * @param context
     * @param userPreferenceUtil
     * @return
     * @throws FrameworkException
     */
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
            logger.log(Level.INFO, "Technical Spec Part type is not configured in page file pgUserPreferenceConfig");
        }
        jsonOutput.add("output", jsonArr.build());
        return jsonOutput.build().toString();
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

    public String getSegmentJson(Context context) throws MatrixException {
        return getSegmentJson(context, new CacheManagement(context), new UserPreferenceUtil());
    }

    public String getPartTypeJson() throws FrameworkException {
        return getPartTypeJson(UserPreferenceTemplateConstants.Basic.TECHNICAL_SPECIFICATION_TYPES_CONFIGURATION.get());
    }
}

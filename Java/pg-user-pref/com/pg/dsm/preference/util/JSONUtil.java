package com.pg.dsm.preference.util;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.pg.designtools.datamanagement.DataConstants;
import com.pg.dsm.preference.models.Item;

import matrix.util.MatrixException;
import matrix.util.StringList;

public class JSONUtil {
    private static final Logger logger = Logger.getLogger(JSONUtil.class.getName());

    public static String asJSON(Map<String, StringList> rangeMap) {
        JsonObjectBuilder jsonOutput = Json.createObjectBuilder();
        JsonArrayBuilder jsonArr = Json.createArrayBuilder();
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
            logger.log(Level.WARNING, "Packaging Material Type range is empty");
        }
        jsonOutput.add("output", jsonArr.build());
        return jsonOutput.build().toString();
    }

    public static String asJSONWithBlank(Map<String, List<String>> typesRange) {
        JsonObjectBuilder jsonOutput = Json.createObjectBuilder();
        JsonArrayBuilder jsonArr = Json.createArrayBuilder();
        if (null != typesRange && !typesRange.isEmpty()) {
            List<String> displayChoices = typesRange.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES);
            List<String> choices = typesRange.get(DataConstants.CONST_FIELD_CHOICES);
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

    public static JsonArray asJSONArrayWithBlank(Map<String, StringList> typesRange) {
        JsonObjectBuilder jsonOutput = Json.createObjectBuilder();
        JsonArrayBuilder jsonArr = Json.createArrayBuilder();
        if (null != typesRange && !typesRange.isEmpty()) {
            List<String> displayChoices = typesRange.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES);
            List<String> choices = typesRange.get(DataConstants.CONST_FIELD_CHOICES);
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
        return jsonArr.build();
    }

    public static JsonArray asJSONArray(Map<String, StringList> rangeMap) {
        JsonArrayBuilder jsonArr = Json.createArrayBuilder();
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
            logger.log(Level.WARNING, "Packaging Material Type range is empty");
        }
        return jsonArr.build();
    }

    private static JsonObjectBuilder getBlankChoiceJson() {
        JsonObjectBuilder json = Json.createObjectBuilder();
        json.add("choice", DomainConstants.EMPTY_STRING);
        json.add("displayChoice", DomainConstants.EMPTY_STRING);
        return json;
    }

    public static String objectAsJSON(Map<Object, Object> rangeMap) throws MatrixException {
        JsonObjectBuilder jsonOutput = Json.createObjectBuilder();
        JsonArrayBuilder jsonArr = Json.createArrayBuilder();
        if (null != rangeMap && !rangeMap.isEmpty()) {
            StringList displayChoices = (StringList) rangeMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES);
            StringList choices = (StringList) rangeMap.get(DataConstants.CONST_FIELD_CHOICES);
            JsonObjectBuilder json;
            for (int i = 0; i < choices.size(); i++) {
                json = Json.createObjectBuilder();
                json.add("choice", choices.get(i));
                json.add("displayChoice", displayChoices.get(i));
                jsonArr.add(json);
            }
        } else {
            logger.log(Level.WARNING, "Incoming range map is empty");
        }
        jsonOutput.add("output", jsonArr.build());
        return jsonOutput.build().toString();
    }

    public static JsonArray objectAsJSONArray(Map<Object, Object> rangeMap) throws MatrixException {
        JsonArrayBuilder jsonArr = Json.createArrayBuilder();
        if (null != rangeMap && !rangeMap.isEmpty()) {
            StringList displayChoices = (StringList) rangeMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES);
            StringList choices = (StringList) rangeMap.get(DataConstants.CONST_FIELD_CHOICES);
            JsonObjectBuilder json;
            for (int i = 0; i < choices.size(); i++) {
                json = Json.createObjectBuilder();
                json.add("choice", choices.get(i));
                json.add("displayChoice", displayChoices.get(i));
                jsonArr.add(json);
            }
        } else {
            logger.log(Level.WARNING, "Incoming range map is empty");
        }
        return jsonArr.build();
    }

    public static String asJSONString(JsonArray jsonArray, String key) {
        JsonObjectBuilder jsonOutput = Json.createObjectBuilder();
        jsonOutput.add(key, jsonArray);
        return jsonOutput.build().toString();
    }

    public static JsonArray getMapListToJSONArray(MapList objectList) {
        JsonArrayBuilder jsonArr = Json.createArrayBuilder();
        if (null != objectList && !objectList.isEmpty()) {
            Iterator iterator = objectList.iterator();
            Map<Object, Object> resultMap;
            JsonObjectBuilder json;
            while (iterator.hasNext()) {
                resultMap = (Map<Object, Object>) iterator.next();
                json = Json.createObjectBuilder();
                json.add(DomainConstants.SELECT_ID, (String) resultMap.get(DomainConstants.SELECT_ID));
                json.add(DomainConstants.SELECT_NAME, (String) resultMap.get(DomainConstants.SELECT_NAME));
                json.add(DomainConstants.SELECT_PHYSICAL_ID, (String) resultMap.get(DomainConstants.SELECT_PHYSICAL_ID));
                jsonArr.add(json);
            }
        } else {
            logger.log(Level.WARNING, "Incoming MapList is empty");
        }
        return jsonArr.build();
    }

    public JsonArray toJSONArray(MapList mapList) {
        JsonArrayBuilder jsonArr = Json.createArrayBuilder();
        StringList selectList = StringList.create("id", "type", "name", "revision", "current", "originated", "modified", "lattice", "owner", "policy");
        JsonObjectBuilder json;
        Iterator iterator = mapList.iterator();
        while (iterator.hasNext()) {
            Map<Object, Object> map = (Map<Object, Object>) iterator.next();
            json = Json.createObjectBuilder();
            for (String key : selectList) {
                Object o = map.get(key);
                if (o instanceof String) {
                    json.add(key, (String) map.get(key));
                }
            }
            jsonArr.add(json);
        }
        return jsonArr.build();
    }

    public JsonArray toJSONArray(List<Item> itemList) {
        JsonArrayBuilder jsonArr = Json.createArrayBuilder();
        JsonObjectBuilder json;
        for (Item item : itemList) {
            json = Json.createObjectBuilder();
            json.add("id", item.getPhysicalId());
            json.add("type", item.getType());
            json.add("name", item.getName());
            json.add("revision", item.getRevision());
            json.add("category", item.getCategory());
            json.add("originated", item.getOriginated());
            json.add("preference", item.getPreference());
            json.add("person", item.getPerson());
            json.add("title", item.getTitle());
            jsonArr.add(json);
        }
        return jsonArr.build();
    }

    public String toJSONString(MapList objectList) throws FrameworkException {
        JsonObjectBuilder jsonOutput = Json.createObjectBuilder();
        jsonOutput.add("output", getMapListToJSONArray(objectList));
        return jsonOutput.build().toString();
    }

}

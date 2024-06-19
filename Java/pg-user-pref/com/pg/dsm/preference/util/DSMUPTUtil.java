package com.pg.dsm.preference.util;

import java.io.StringReader;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.preference.enumeration.DSMUPTConstants;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.db.SelectConstants;
import matrix.util.StringList;

public class DSMUPTUtil {
    private final Logger logger = Logger.getLogger(DSMUPTUtil.class.getName());

    public String getAttributeInfoAsJsonStringExample(Context context, String objectOid) throws Exception {
        List<String> selectList = List.of(
                DSMUPTConstants.Attributes.UPT_PRIMARY_ORGANIZATION.getSelect(context),
                DSMUPTConstants.Attributes.UPT_SEGMENT.getSelect(context),
                DSMUPTConstants.Attributes.UPT_BUSINESS_USE.getSelect(context),
                DSMUPTConstants.Attributes.UPT_HIGHLY_RESTRICTED.getSelect(context),
                DSMUPTConstants.Attributes.UPT_BUSINESS_AREA.getSelect(context),
                DSMUPTConstants.Attributes.UPT_ROUTE_TEMPLATE_IN_WORK.getSelect(context),
                DSMUPTConstants.Attributes.UPT_ROUTE_TEMPLATE_IN_APPROVAL.getSelect(context),
                DSMUPTConstants.Attributes.UPT_RELEASE_PHASE.getSelect(context),
                DSMUPTConstants.Attributes.UPT_MFG_STATUS.getSelect(context),
                DSMUPTConstants.Attributes.UPT_RELEASE_CRITERIA_REQUIRED.getSelect(context),
                DSMUPTConstants.Attributes.UPT_PACKAGING_COMPONENT_TYPE.getSelect(context),
                DSMUPTConstants.Attributes.UPT_PACKAGING_MATERIAL_TYPE.getSelect(context),
                DomainConstants.SELECT_NAME,
                DomainConstants.SELECT_ID,
                DomainConstants.SELECT_POLICY,
                DomainConstants.SELECT_PHYSICAL_ID
        );
        String asJsonString = getAttributeInfoAsJsonString(context, objectOid, selectList);
        JsonReader jsonReader = Json.createReader(new StringReader(asJsonString));
        JsonObject jsonObject = jsonReader.readObject();
        JsonObject segment = jsonObject.getJsonObject(DSMUPTConstants.Attributes.UPT_SEGMENT.getSelect(context));
        String segmentPhysicalId = segment.getString(DomainConstants.SELECT_PHYSICAL_ID);
        return asJsonString;
    }

    public Map<Object, Object> getAttributeInfoAsMap(Context context, String objectOid) throws Exception {
        StringList objectSelects = new StringList();
        DSMUPTConstants.Attributes[] attributes = DSMUPTConstants.Attributes.values();
        for (DSMUPTConstants.Attributes attribute : attributes) {
            objectSelects.add(attribute.getSelect(context));
        }
        objectSelects.addAll(DSMUPTConstants.AttributeGroups.BASIC.getAttributeList(context));
        return getAttributeInfoAsMap(context, objectOid, objectSelects);
    }

    public String getAttributeInfoAsJson(Context context, String objectOid) throws Exception {
        StringList objectSelects = new StringList();
        DSMUPTConstants.Attributes[] attributes = DSMUPTConstants.Attributes.values();
        for (DSMUPTConstants.Attributes attribute : attributes) {
            objectSelects.add(attribute.getSelect(context));
        }
        objectSelects.addAll(DSMUPTConstants.AttributeGroups.BASIC.getAttributeList(context));
        return getAttributeInfoAsJsonString(context, objectOid, objectSelects);
    }

    public String getAttributeInfoAsJsonString(Context context, String objectOid, List<String> selectList) throws Exception {
        Instant startTime = Instant.now();
        Map<Object, Object> infoMap = getAttributeInfoAsMap(context, objectOid, selectList);
        String jsonString = convertToJSONString(infoMap);
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.log(Level.INFO, "Get DSM User Preference Template Attribute Info as Json - took |" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
        return jsonString;
    }

    public String getAttributeInfoAsJsonString(Map<Object, Object> infoMap) throws Exception {
        Instant startTime = Instant.now();
        String jsonString = convertToJSONString(infoMap);
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.log(Level.INFO, "Get DSM User Preference Template Attribute Info as Json - took |" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
        logger.log(Level.INFO, jsonString); // TO-DO: remove this.
        return jsonString;
    }

    public Map<Object, Object> getAttributeInfoAsMap(Context context, String objectOid, List<String> selectList) throws Exception {
        Map<Object, Object> resultMap = new HashMap<>();
        if (UIUtil.isNotNullAndNotEmpty(objectOid)) {
            if (null != selectList && !selectList.isEmpty()) {
                List<String> singleAttributes = DSMUPTConstants.AttributeGroups.ATTRIBUTE_HOLDING_SINGLE_PHYSICAL_ID.getAttributeList(context);
                List<String> multipleAttributes = DSMUPTConstants.AttributeGroups.ATTRIBUTE_HOLDING_MULTIPLE_PHYSICAL_ID.getAttributeList(context);
                List<String> normalAttributes = DSMUPTConstants.AttributeGroups.ATTRIBUTE_NORMAL.getAttributeList(context);
                List<String> basics = DSMUPTConstants.AttributeGroups.BASIC.getAttributeList(context);

                List<String> incomingSingleAttributes = new ArrayList<>();
                List<String> incomingMultipleAttributes = new ArrayList<>();
                List<String> incomingNormalAttributes = new ArrayList<>();
                List<String> incomingBasics = new ArrayList<>();

                for (String select : selectList) {
                    if (singleAttributes.contains(select)) {
                        incomingSingleAttributes.add(select);
                    }
                    if (multipleAttributes.contains(select)) {
                        incomingMultipleAttributes.add(select);
                    }
                    if (normalAttributes.contains(select)) {
                        incomingNormalAttributes.add(select);
                    }
                    if (basics.contains(select)) {
                        incomingBasics.add(select);
                    }
                }

                StringList objectSelects = new StringList();
                objectSelects.addAll(incomingSingleAttributes);
                objectSelects.addAll(incomingMultipleAttributes);
                objectSelects.addAll(incomingNormalAttributes);
                objectSelects.addAll(incomingBasics);

                MapList infoList = DomainObject.getInfo(context, new String[]{objectOid}, objectSelects);
                if (null != infoList && !infoList.isEmpty()) {
                    Map<Object, Object> objectMap = (Map<Object, Object>) infoList.get(0);
                    resultMap.putAll(getAttributeInfoWithPhysicalID(context, objectMap, incomingSingleAttributes, incomingMultipleAttributes));
                    resultMap.putAll(getAttributeInfoNormal(context, objectMap, incomingNormalAttributes, incomingBasics));
                }
            } else {
                logger.log(Level.WARNING, "Incoming select list is empty");
            }
        } else {
            logger.log(Level.WARNING, "Incoming object ID is empty");
        }
        logger.log(Level.INFO, resultMap.toString()); // TO-DO: remove this.
        return resultMap;
    }

    private String convertToJSONString(Map<Object, Object> resultMap) {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        for (Map.Entry<Object, Object> entry : resultMap.entrySet()) {
            if (entry.getValue() instanceof List) {
                JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
                List<Map<Object, Object>> attributeList = (List<Map<Object, Object>>) entry.getValue();
                for (Map<Object, Object> attributeMap : attributeList) {
                    JsonObjectBuilder tempJsonObjectBuilder = Json.createObjectBuilder();
                    for (Map.Entry<Object, Object> attrEntry : attributeMap.entrySet()) {
                        tempJsonObjectBuilder.add(attrEntry.getKey().toString(), attrEntry.getValue().toString());
                    }
                    jsonArrayBuilder.add(tempJsonObjectBuilder);
                }
                jsonObjectBuilder.add(entry.getKey().toString(), jsonArrayBuilder);
            } else if (entry.getValue() instanceof Map) {
                JsonObjectBuilder tempJsonObjectBuilder = Json.createObjectBuilder();
                Map<Object, Object> attributeMap = (Map<Object, Object>) entry.getValue();
                for (Map.Entry<Object, Object> attrEntry : attributeMap.entrySet()) {
                    tempJsonObjectBuilder.add(attrEntry.getKey().toString(), attrEntry.getValue().toString());
                }
                jsonObjectBuilder.add(entry.getKey().toString(), tempJsonObjectBuilder);
            } else {
                jsonObjectBuilder.add(entry.getKey().toString(), entry.getValue().toString());
            }
        }
        JsonObject jsonObject = jsonObjectBuilder.build();
        return jsonObject.toString();
    }

    private Map<Object, Object> getAttributeInfoWithPhysicalID(Context context, Map<Object, Object> objectMap, List<String> singlePhysicalIDAttributes, List<String> multiplePhysicalIDAttributes) throws Exception {
        Map<Object, Object> resultMap = new HashMap<>();
        Map<String, String> singleValueAttributeMap = new HashMap<>();
        for (String attribute : singlePhysicalIDAttributes) {
            String id = objectMap.containsKey(attribute) ? getStringFromMap(objectMap, attribute) : DomainConstants.EMPTY_STRING;
            singleValueAttributeMap.put(attribute, id);
        }
        Map<String, StringList> multiValueAttributeMap = new HashMap<>();
        for (String attribute : multiplePhysicalIDAttributes) {
            StringList idList = objectMap.containsKey(attribute) ? getStringListFromMap(objectMap, attribute) : new StringList();
            multiValueAttributeMap.put(attribute, idList);
        }
        StringList physicalIDList = new StringList();
        for (StringList idList : multiValueAttributeMap.values()) {
            physicalIDList.addAll(idList);
        }
        for (String key : singleValueAttributeMap.keySet()) {
            physicalIDList.addAll(singleValueAttributeMap.get(key));
        }
        physicalIDList.removeIf(String::isEmpty);
        List<String> uniquePhysicalIDs = physicalIDList.stream().distinct().collect(Collectors.toList());
        StringList selectList = StringList.create(
                DomainConstants.SELECT_TYPE,
                DomainConstants.SELECT_NAME,
                DomainConstants.SELECT_ID,
                DomainConstants.SELECT_PHYSICAL_ID
        );
        MapList infoList = DomainObject.getInfo(context, uniquePhysicalIDs.toArray(new String[0]), selectList);
        if (infoList != null && !infoList.isEmpty()) {
            Map<String, Map<Object, Object>> singleValueInfoMap = new HashMap<>();
            for (String singleValueSelect : singlePhysicalIDAttributes) {
                singleValueInfoMap.put(singleValueSelect, new HashMap<>());
            }
            Map<String, List<Map<Object, Object>>> multiValueInfoMap = new HashMap<>();
            for (String multiValueSelect : multiplePhysicalIDAttributes) {
                multiValueInfoMap.put(multiValueSelect, new ArrayList<>());
            }
            for (Object info : infoList) {
                Map<Object, Object> tempMap = (Map<Object, Object>) info;
                String tempPhysicalId = (String) tempMap.get(DomainConstants.SELECT_PHYSICAL_ID);
                String tempObjectId = (String) tempMap.get(DomainConstants.SELECT_ID);
                for (String key : multiValueAttributeMap.keySet()) {
                    StringList physIDList = multiValueAttributeMap.get(key);
                    if (physIDList.contains(tempPhysicalId) || physIDList.contains(tempObjectId)) {
                        multiValueInfoMap.get(key).add(tempMap);
                    }
                }
                for (String key : singleValueAttributeMap.keySet()) {
                    String physicalId = singleValueAttributeMap.get(key);
                    if (physicalId.equalsIgnoreCase(tempPhysicalId) || physicalId.equalsIgnoreCase(tempObjectId)) {
                        singleValueInfoMap.put(key, tempMap);
                    }
                }
            }
            for (String select : singlePhysicalIDAttributes) {
                resultMap.put(select, singleValueInfoMap.get(select));
            }
            for (String select : multiplePhysicalIDAttributes) {
                resultMap.put(select, multiValueInfoMap.get(select));
            }
        }
        return resultMap;
    }

    private Map<Object, Object> getAttributeInfoNormal(Context context, Map<Object, Object> objectMap, List<String> attributes, List<String> basicInfoAttributes) throws Exception {
        Map<Object, Object> resultMap = new HashMap<>();
        for (String attribute : attributes) {
            resultMap.put(attribute, objectMap.getOrDefault(attribute, DomainConstants.EMPTY_STRING));
        }
        for (String attribute : basicInfoAttributes) {
            resultMap.put(attribute, objectMap.getOrDefault(attribute, DomainConstants.EMPTY_STRING));
        }
        return resultMap;
    }

    private StringList getStringListFromMap(Map<Object, Object> objectMap, String select) throws Exception {
        StringList objectList = new StringList();
        Object result = objectMap.get(select);
        if (null != result) {
            if (result instanceof StringList) {
                objectList = (StringList) result;
            } else if (result.toString().contains(SelectConstants.cSelectDelimiter)) {
                objectList = StringUtil.splitString(result.toString(), SelectConstants.cSelectDelimiter);
            } else if (result.toString().contains(pgV3Constants.SYMBOL_COMMA)) {
                objectList = StringUtil.split(result.toString(), pgV3Constants.SYMBOL_COMMA);
            } else {
                objectList.add(result.toString());
            }
        }
        return objectList;
    }

    private String getStringFromMap(Map<Object, Object> objectMap, String select) {
        String ret = DomainConstants.EMPTY_STRING;
        Object result = objectMap.get(select);
        if (null != result) {
            if (result instanceof String) {
                ret = (String) result;
            }
        }
        return ret;
    }
}

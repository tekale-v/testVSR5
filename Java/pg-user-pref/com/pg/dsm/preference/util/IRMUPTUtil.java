package com.pg.dsm.preference.util;

import java.io.StringReader;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.preference.enumeration.IRMUPTConstants;
import com.pg.dsm.preference.template.irm.IIRMTemplateCreateSteps;
import com.pg.dsm.preference.template.irm.IRMTemplateCreateSteps;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.db.SelectConstants;
import matrix.util.StringList;

public class IRMUPTUtil {
    private final Logger logger = Logger.getLogger(DSMUPTUtil.class.getName());

    public String getAttributeInfoAsJsonStringExampleRestricted(Context context, String objectOid) throws Exception {
        List<String> selectList = List.of(
                IRMUPTConstants.Attributes.UPT_BUSINESS_USE.getSelect(context),
                IRMUPTConstants.Attributes.UPT_HIGHLY_RESTRICTED.getSelect(context),
                IRMUPTConstants.Attributes.UPT_BUSINESS_AREA.getSelect(context),
                IRMUPTConstants.Attributes.UPT_SHARE_WITH_MEMBERS.getSelect(context),
                IRMUPTConstants.Attributes.UPT_REGION.getSelect(context),
                IRMUPTConstants.Attributes.UPT_MIGRATED.getSelect(context),
                IRMUPTConstants.Attributes.UPT_TITLE.getSelect(context),
                IRMUPTConstants.Attributes.UPT_POLICY.getSelect(context),
                IRMUPTConstants.Attributes.UPT_DESCRIPTION.getSelect(context),
                IRMUPTConstants.Attributes.UPT_CLASSIFICATION.getSelect(context),
                IRMUPTConstants.Attributes.UPT_ROUTE_INSTRUCTION.getSelect(context),
                IRMUPTConstants.Attributes.UPT_ROUTE_ACTION.getSelect(context),
                IRMUPTConstants.Attributes.UPT_TASK_RECIPIENT_MEMBER.getSelect(context),
                DomainConstants.SELECT_NAME,
                DomainConstants.SELECT_ID,
                DomainConstants.SELECT_POLICY,
                DomainConstants.SELECT_PHYSICAL_ID,
                DomainConstants.SELECT_TYPE,
                DomainConstants.SELECT_OWNER
        );
        String asJsonString = getAttributeInfoAsJsonString(context, objectOid, selectList);
        JsonReader jsonReader = Json.createReader(new StringReader(asJsonString));
        JsonObject jsonObject = jsonReader.readObject();
        return asJsonString;
    }

    public String getAttributeInfoAsJsonStringExampleHighlyRestricted(Context context, String objectOid) throws Exception {
        List<String> selectList = List.of(
                IRMUPTConstants.Attributes.UPT_BUSINESS_USE.getSelect(context),
                IRMUPTConstants.Attributes.UPT_HIGHLY_RESTRICTED.getSelect(context),
                IRMUPTConstants.Attributes.UPT_BUSINESS_AREA.getSelect(context),
                IRMUPTConstants.Attributes.UPT_SHARE_WITH_MEMBERS.getSelect(context),
                IRMUPTConstants.Attributes.UPT_REGION.getSelect(context),
                IRMUPTConstants.Attributes.UPT_MIGRATED.getSelect(context),
                IRMUPTConstants.Attributes.UPT_TITLE.getSelect(context),
                IRMUPTConstants.Attributes.UPT_POLICY.getSelect(context),
                IRMUPTConstants.Attributes.UPT_DESCRIPTION.getSelect(context),
                IRMUPTConstants.Attributes.UPT_CLASSIFICATION.getSelect(context),
                IRMUPTConstants.Attributes.UPT_ROUTE_INSTRUCTION.getSelect(context),
                IRMUPTConstants.Attributes.UPT_ROUTE_ACTION.getSelect(context),
                IRMUPTConstants.Attributes.UPT_TASK_RECIPIENT_GROUP.getSelect(context),
                DomainConstants.SELECT_NAME,
                DomainConstants.SELECT_ID,
                DomainConstants.SELECT_POLICY,
                DomainConstants.SELECT_PHYSICAL_ID,
                DomainConstants.SELECT_TYPE,
                DomainConstants.SELECT_OWNER
        );
        String asJsonString = getAttributeInfoAsJsonString(context, objectOid, selectList);
        JsonReader jsonReader = Json.createReader(new StringReader(asJsonString));
        JsonObject jsonObject = jsonReader.readObject();
        return asJsonString;
    }

    public String getAttributeInfoAsJsonString(Context context, String objectOid, List<String> selectList) throws Exception {
        Instant startTime = Instant.now();
        Map<Object, Object> infoMap = getAttributeInfoAsMap(context, objectOid, selectList);
        String jsonString = convertToJSONString(infoMap);
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.log(Level.INFO, "Get IRM User Preference Template Attribute Info as Json - took |" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
        return jsonString;
    }

    public Map<Object, Object> getAttributeInfoAsMap(Context context, String objectOid) throws Exception {
        StringList objectSelects = new StringList();
        IRMUPTConstants.Attributes[] attributes = IRMUPTConstants.Attributes.values();
        for (IRMUPTConstants.Attributes attribute : attributes) {
            objectSelects.add(attribute.getSelect(context));
        }
        objectSelects.addAll(IRMUPTConstants.AttributeGroups.BASIC.getAttributeList(context));
        return getAttributeInfoAsMap(context, objectOid, objectSelects);
    }

    public String getAttributeInfoAsJson(Context context, String objectOid) throws Exception {
        StringList objectSelects = new StringList();
        IRMUPTConstants.Attributes[] attributes = IRMUPTConstants.Attributes.values();
        for (IRMUPTConstants.Attributes attribute : attributes) {
            objectSelects.add(attribute.getSelect(context));
        }
        objectSelects.addAll(IRMUPTConstants.AttributeGroups.BASIC.getAttributeList(context));
        return getAttributeInfoAsJsonString(context, objectOid, objectSelects);
    }

    public Map<Object, Object> getAttributeInfoAsMap(Context context, String objectOid, List<String> selectList) throws Exception {
        Map<Object, Object> resultMap = new HashMap<>();
        if (UIUtil.isNotNullAndNotEmpty(objectOid)) {
            if (null != selectList && !selectList.isEmpty()) {
                List<String> normalAttributes = IRMUPTConstants.AttributeGroups.ATTRIBUTE_NORMAL.getAttributeList(context);
                List<String> recipientGroupAttributes = IRMUPTConstants.AttributeGroups.ATTRIBUTE_HOLDING_RECIPIENT_GROUP.getAttributeList(context);
                List<String> recipientMemberAttributes = IRMUPTConstants.AttributeGroups.ATTRIBUTE_HOLDING_RECIPIENT_MEMBER.getAttributeList(context);
                List<String> multipleAttributes = IRMUPTConstants.AttributeGroups.ATTRIBUTE_HOLDING_MULTIPLE_PHYSICAL_ID.getAttributeList(context);
                List<String> basics = IRMUPTConstants.AttributeGroups.BASIC.getAttributeList(context);

                List<String> incomingMultipleAttributes = new ArrayList<>();
                List<String> incomingNormalAttributes = new ArrayList<>();
                List<String> incomingBasics = new ArrayList<>();
                List<String> incomingRecipientGroupAttributes = new ArrayList<>();
                List<String> incomingRecipientMemberAttributes = new ArrayList<>();

                for (String select : selectList) {
                    if (recipientGroupAttributes.contains(select)) {
                        incomingRecipientGroupAttributes.add(select);
                    }
                    if (recipientMemberAttributes.contains(select)) {
                        incomingRecipientMemberAttributes.add(select);
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
                objectSelects.addAll(incomingMultipleAttributes);
                objectSelects.addAll(incomingNormalAttributes);
                objectSelects.addAll(incomingBasics);
                objectSelects.addAll(incomingRecipientGroupAttributes);
                objectSelects.addAll(incomingRecipientMemberAttributes);

                MapList infoList = DomainObject.getInfo(context, new String[]{objectOid}, objectSelects);
                if (null != infoList && !infoList.isEmpty()) {
                    Map<Object, Object> objectMap = (Map<Object, Object>) infoList.get(0);
                    resultMap.putAll(getAttributeInfoWithPhysicalID(context, objectMap, incomingMultipleAttributes));
                    resultMap.putAll(getAttributeInfoNormal(objectMap, incomingNormalAttributes, incomingBasics));
                    if (!incomingRecipientGroupAttributes.isEmpty()) {
                        resultMap.putAll(getAttributeInfoRecipientGroup(context, objectMap, incomingRecipientGroupAttributes.get(0)));
                    }
                    if (!incomingRecipientMemberAttributes.isEmpty()) {
                        resultMap.putAll(getAttributeInfoRecipientMember(context, objectMap, incomingRecipientMemberAttributes.get(0)));
                    }
                }
            } else {
                logger.log(Level.WARNING, "Incoming select list is empty");
            }
        } else {
            logger.log(Level.WARNING, "Incoming object ID is empty");
        }
        return resultMap;
    }

    private Map<Object, Object> getAttributeInfoWithPhysicalID(Context context, Map<Object, Object> objectMap, List<String> multiplePhysicalIDAttributes) throws Exception {
        Map<Object, Object> resultMap = new HashMap<>();

        Map<String, StringList> multiValueAttributeMap = new HashMap<>();
        for (String attribute : multiplePhysicalIDAttributes) {
            StringList idList = objectMap.containsKey(attribute) ? getStringListFromMap(objectMap, attribute) : new StringList();
            multiValueAttributeMap.put(attribute, idList);
        }
        StringList physicalIDList = new StringList();
        for (StringList idList : multiValueAttributeMap.values()) {
            physicalIDList.addAll(idList);
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
            }
            for (String select : multiplePhysicalIDAttributes) {
                resultMap.put(select, multiValueInfoMap.get(select));
            }
        }
        return resultMap;
    }

    private Map<Object, Object> getAttributeInfoNormal(Map<Object, Object> objectMap, List<String> attributes, List<String> basicInfoAttributes) throws Exception {
        Map<Object, Object> resultMap = new HashMap<>();
        for (String attribute : attributes) {
            resultMap.put(attribute, objectMap.getOrDefault(attribute, DomainConstants.EMPTY_STRING));
        }
        for (String attribute : basicInfoAttributes) {
            resultMap.put(attribute, objectMap.getOrDefault(attribute, DomainConstants.EMPTY_STRING));
        }
        return resultMap;
    }

    private Map<Object, Object> getAttributeInfoRecipientGroup(Context context, Map<Object, Object> objectMap, String select) throws Exception {
        Map<Object, Object> resultMap = new HashMap<>();
        if (objectMap.containsKey(select)) {
            StringList asStoredList = objectMap.containsKey(select) ? getStringListFromMap(objectMap, select) : new StringList();
            StringList physicalIDList = new StringList();
            for (String asStored : asStoredList) {
                if (UIUtil.isNotNullAndNotEmpty(asStored)) {
                    StringList eachStoredList = FrameworkUtil.split(asStored, "~");
                    if (null != eachStoredList && eachStoredList.size() > 1) {
                        physicalIDList.add(eachStoredList.get(1));
                    }
                }
            }
            physicalIDList.removeIf(String::isEmpty);
            List<String> uniquePhysicalIDs = physicalIDList.stream().distinct().collect(Collectors.toList());
            MapList infoList = DomainObject.getInfo(context, uniquePhysicalIDs.toArray(new String[0]), StringList.create(
                    DomainConstants.SELECT_TYPE,
                    DomainConstants.SELECT_NAME,
                    DomainConstants.SELECT_ID,
                    DomainConstants.SELECT_PHYSICAL_ID));

            resultMap.put(select, infoList);
        }
        return resultMap;
    }

    private Map<Object, Object> getAttributeInfoRecipientMember(Context context, Map<Object, Object> objectMap, String select) throws Exception {
        Map<Object, Object> resultMap = new HashMap<>();
        if (objectMap.containsKey(select)) {
            StringList asStoredList = objectMap.containsKey(select) ? getStringListFromMap(objectMap, select) : new StringList();
            StringList physicalIDList = new StringList();

            Map<String, String> iDRolePair = new HashMap<>();
            String physicalID;
            String role;
            String name;
            StringList eachStoredList;
            for (String asStored : asStoredList) {
                if (UIUtil.isNotNullAndNotEmpty(asStored)) {
                    eachStoredList = FrameworkUtil.split(asStored, "~");
                    if (null != eachStoredList && eachStoredList.size() > 2) {
                        name = eachStoredList.get(0);
                        role = eachStoredList.get(1);
                        physicalID = eachStoredList.get(2);
                        physicalIDList.add(physicalID);
                        iDRolePair.put(physicalID.concat(name), role); // in case - same person is added with different role. (it's not really possible, but just in case)
                    }
                }
            }
            physicalIDList.removeIf(String::isEmpty);
            List<String> uniquePhysicalIDs = physicalIDList.stream().distinct().collect(Collectors.toList());
            MapList infoList = DomainObject.getInfo(context, uniquePhysicalIDs.toArray(new String[0]), StringList.create(
                    DomainConstants.SELECT_TYPE,
                    DomainConstants.SELECT_NAME,
                    DomainConstants.SELECT_ID,
                    DomainConstants.SELECT_PHYSICAL_ID));


            if (null != infoList && !infoList.isEmpty()) {
                Iterator iterator = infoList.iterator();
                Map<Object, Object> tempMap;
                String key;
                while (iterator.hasNext()) {
                    tempMap = (Map<Object, Object>) iterator.next();
                    physicalID = (String) tempMap.get(DomainConstants.SELECT_PHYSICAL_ID);
                    name = (String) tempMap.get(DomainConstants.SELECT_NAME);
                    key = physicalID.concat(name);
                    if (iDRolePair.containsKey(key)) {
                        role = iDRolePair.get(key);
                        tempMap.put("role", role);
                    }
                }
            }

            resultMap.put(select, infoList);
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

    public void createIRMTemplate(Context context, String[] args) throws Exception {
        try {
            Map programMap = JPO.unpackArgs(args);
            Map requestMap = (Map) programMap.get("requestMap");
            if (null != requestMap && !requestMap.isEmpty()) {
                requestMap.put(IRMUPTConstants.Basic.MODE.get(), IRMUPTConstants.Basic.MODE_CREATE.get());
                IIRMTemplateCreateSteps steps = new IRMTemplateCreateSteps(context);
                DomainObject domainObject = steps.createTemplate(requestMap);
                if (null != domainObject) {
                    boolean isInterfaceAdded = steps.addInterface(domainObject);
                    if (isInterfaceAdded) {
                        boolean isObjectUpdated = steps.updateAttributes(domainObject, requestMap);
                        if (isObjectUpdated) {
                            String shareTemplateWithKey = IRMUPTConstants.Basic.FIELD_SHARE_TEMPLATE_WITH.get();
                            String shareTemplateWithValue = requestMap.containsKey(shareTemplateWithKey) ? (String) requestMap.get(shareTemplateWithKey) : DomainConstants.EMPTY_STRING;
							// Modified by IRM (Sogeti) for 22x.04 Defect 55069 - Start
                            if (UIUtil.isNotNullAndNotEmpty(shareTemplateWithValue)) {
                            boolean isOwnershipApplied = steps.applyShareTemplateWith(domainObject, shareTemplateWithValue);
                            if (isOwnershipApplied) {
                                logger.log(Level.INFO, "Sharing template applied");
                            } else {
                                logger.log(Level.SEVERE, "Failed to apply ownership on UPT Template");
                            }
							}
							// Modified by IRM (Sogeti) for 22x.04 Defect 55069 - End
                        } else {
                            logger.log(Level.SEVERE, "Failed to update attributes on UPT Template");
                        }
                    } else {
                        logger.log(Level.SEVERE, "Failed to add interface on UPT Template");
                    }
                } else {
                    logger.log(Level.SEVERE, "Created UPT Template Object instance is null");
                }
            } else {
                logger.log(Level.SEVERE, "Incoming requestMap is empty for IRM UPT Template creation");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception: " + e);
            throw e;
        }
    }
}

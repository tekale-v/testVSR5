package com.pg.dsm.preference.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.preference.enumeration.IRMUPTConstants;
import com.pg.dsm.preference.enumeration.UserPreferenceTemplateConstants;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.BusinessInterface;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.db.SelectConstants;
import matrix.db.Vault;
import matrix.util.MatrixException;
import matrix.util.StringList;


public class IRMUPT {
    private static final Logger logger = Logger.getLogger(DSMUPTUtil.class.getName());
    Context context;
    String objectOid;
    DomainObject template;

    public IRMUPT(Context context, String objectOid) {
        this.context = context;
        this.objectOid = objectOid;
    }

    public IRMUPT(Context context) throws FrameworkException {
    	this.context = context;
        template = DomainObject.newInstance(context);
    }

    public static MapList findAllTemplates(Context context, String[] args) {
        MapList objectList = new MapList();
        MapList sharedObjectList = new MapList();
        Iterator mapIterator;
        Map sharedObjectMap;
        String strSharedObjectOwnership;
        StringList slSharedObjectOwnership;
        try {
            Map programMap = JPO.unpackArgs(args);
            String strUser = context.getUser();
            String strUserPreferenceTemplateIRMFilter = (String) programMap.get(IRMUPTConstants.Basic.FILTER.get());

            StringList slbusSelects = new StringList();
            slbusSelects.add(DomainConstants.SELECT_ID);
            slbusSelects.add(DomainConstants.SELECT_TYPE);
            slbusSelects.add(DomainConstants.SELECT_NAME);
            slbusSelects.add(DomainConstants.SELECT_REVISION);
            slbusSelects.add(DomainConstants.SELECT_ORIGINATED);

            StringBuilder sbWhere = new StringBuilder();
            sbWhere.append("(");
    		sbWhere.append(DomainConstants.SELECT_POLICY);
    		sbWhere.append(" == '");
    		sbWhere.append(UserPreferenceTemplateConstants.Policy.POLICY_USER_PREFERENCE_TEMPLATE_IRM.getName(context));
    		sbWhere.append("')");
            if (strUserPreferenceTemplateIRMFilter.equals(IRMUPTConstants.Basic.FILTER_OWNED.get())) {
               
                objectList = findTemplates(context, UserPreferenceTemplateConstants.Type.TYPE_USER_PREFERENCE_TEMPLATE.getName(context), strUser, sbWhere.toString(), slbusSelects);
            } else {
            	sbWhere.append(" && (");	
    			sbWhere.append(DomainConstants.SELECT_OWNER);
    			sbWhere.append(" != '");
    			sbWhere.append(strUser);
    			sbWhere.append("')");
    		   
                slbusSelects.add(IRMUPTConstants.Basic.OWNERSHIP.get());
                sharedObjectList = findTemplates(context, UserPreferenceTemplateConstants.Type.TYPE_USER_PREFERENCE_TEMPLATE.getName(context), DomainConstants.QUERY_WILDCARD, sbWhere.toString(), slbusSelects);
                mapIterator = sharedObjectList.iterator();
                while (mapIterator.hasNext()) {
                    sharedObjectMap = (Map) mapIterator.next();
                    slSharedObjectOwnership = (StringList) sharedObjectMap.get(IRMUPTConstants.Basic.OWNERSHIP.get());
                    strSharedObjectOwnership = slSharedObjectOwnership.toString();
                    if (strSharedObjectOwnership.contains(strUser)) {
                        objectList.add(sharedObjectMap);
                    }
                }
            }

        } catch (Exception e) {
            logger.log(Level.WARNING, "Exception in findAllTemplates: "+ e.getMessage());
        }
        return objectList;
    }

    public static MapList findTemplates(Context context, String type, String owner, String where, StringList selects) throws FrameworkException {
        MapList templatesList = new MapList();

        templatesList = DomainObject.findObjects(
                context,                    //context
                type,                        //Type Pattern
                DomainConstants.QUERY_WILDCARD,                //Name Pattern
                DomainConstants.QUERY_WILDCARD,                //Revision Pattern
                owner,                        //owner Pattern
                pgV3Constants.VAULT_ESERVICEPRODUCTION,  //vault Pattern
                where,                        //where expression
                false,                        //expand type
                selects                        //bus Selects
        );

        return templatesList;
    }

    public static HashMap getTemplateFilters() {
        HashMap hmTypeFilter = new HashMap();
        StringList filterList = new StringList();
        filterList.add(IRMUPTConstants.Basic.FILTER_OWNED.get());
        filterList.add(IRMUPTConstants.Basic.FILTER_SHARED.get());

        hmTypeFilter.put(IRMUPTConstants.Basic.FIELD_CHOICES.get(), filterList);
        hmTypeFilter.put(IRMUPTConstants.Basic.FIELD_DISPLAY_CHOICES.get(), filterList);
        return hmTypeFilter;
    }

    public void createTemplate(Map attributeMap) throws Exception {
        createTemplate(context, attributeMap, template);
    }

    public Map<Object, Object> getAttributeMap() throws Exception {
        IRMUPTUtil util = new IRMUPTUtil();
        return util.getAttributeInfoAsMap(this.context, this.objectOid);
    }

    public String getAttributeJson() throws Exception {
        IRMUPTUtil util = new IRMUPTUtil();
        return util.getAttributeInfoAsJson(this.context, this.objectOid);
    }

    
    public String getTemplateName() {
    	String name = DomainConstants.SELECT_NAME;
    	try {
    		name = getAttributeValueAsString(name);
    	}catch(Exception e) {
    		logger.log(Level.WARNING, "Exception in getTemplateName: " + e.getMessage());
    	}
    	
    	return name;
    }
    
    public String getTemplateDescription() {
    	String desc = DomainConstants.SELECT_DESCRIPTION;
    	try {
    		desc = getAttributeValueAsString(desc);
    	}catch(Exception e) {
    		logger.log(Level.WARNING, "Exception in getTemplateDescription: " + e.getMessage());
    	}
    	
    	return desc;
    }
    
    
    public Map<Object, Object> getBusinessUseClass() throws Exception {
        return getInfoMap(IRMUPTConstants.Attributes.UPT_BUSINESS_USE.getSelect(this.context));
    }

    public Map<Object, Object> getHighlyRestrictedClass() throws Exception {
        return getInfoMap(IRMUPTConstants.Attributes.UPT_HIGHLY_RESTRICTED.getSelect(this.context));
    }

    public Map<Object, Object> getBusinessArea() throws Exception {
        return getInfoMap(IRMUPTConstants.Attributes.UPT_BUSINESS_AREA.getSelect(this.context));
    }

    public Map<Object, Object> getRegion() throws Exception {
        return getInfoMap(IRMUPTConstants.Attributes.UPT_REGION.getSelect(this.context));
    }

    public Map<Object, Object> getShareWithMembers() throws Exception {
        return getInfoMap(IRMUPTConstants.Attributes.UPT_SHARE_WITH_MEMBERS.getSelect(this.context));
    }

    public Map<Object, Object> getTaskMemberRecipients() throws Exception {
        Map<Object, Object> resultMap = new HashMap<>();
        String select = IRMUPTConstants.Attributes.UPT_TASK_RECIPIENT_MEMBER.getSelect(this.context);
        MapList infoList = DomainObject.getInfo(this.context, new String[]{this.objectOid}, StringList.create(select));
        if (null != infoList && !infoList.isEmpty()) {
            Map<Object, Object> infoMap = (Map<Object, Object>) infoList.get(0);
            if (null != infoMap && !infoMap.isEmpty()) {
                StringList asStoredList = infoMap.containsKey(select) ? getStringListFromMap(infoMap, select) : new StringList();
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
                MapList objectList = DomainObject.getInfo(context, uniquePhysicalIDs.toArray(new String[0]), getBasicSelects());

                if (null != objectList && !objectList.isEmpty()) {
                    Iterator iterator = objectList.iterator();
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
                    resultMap.put(select, objectList);
                }
            }
        }
        return resultMap;
    }
    
    public Map<Object, Object> getTaskGroupRecipients() throws Exception {
    	String select = IRMUPTConstants.Attributes.UPT_TASK_RECIPIENT_GROUP.getSelect(this.context);
        Map<Object, Object> resultMap = new HashMap<>();
        MapList infoList = DomainObject.getInfo(this.context, new String[]{this.objectOid}, StringList.create(select));
        if (null != infoList && !infoList.isEmpty()) {
            Map<Object, Object> infoMap = (Map<Object, Object>) infoList.get(0);
            if (null != infoMap && !infoMap.isEmpty()) {
                StringList asStoredList = infoMap.containsKey(select) ? getStringListFromMap(infoMap, select) : new StringList();
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
                MapList objectList = DomainObject.getInfo(this.context, uniquePhysicalIDs.toArray(new String[0]), getBasicSelects());

                if (null != objectList && !objectList.isEmpty()) {
                    resultMap.put(select, objectList);
                }
            }
        }
        return resultMap;
    }

    public Map<Object, Object> getInfoMap(String select) throws Exception {
        Map<Object, Object> resultMap = new HashMap<>();
        MapList infoList = DomainObject.getInfo(this.context, new String[]{this.objectOid}, StringList.create(select));
        
        if (null != infoList && !infoList.isEmpty()) {
            Map<Object, Object> infoMap = (Map<Object, Object>) infoList.get(0);
            if (null != infoMap && !infoMap.isEmpty()) {
                StringList physicalIDList = getStringListFromMap(infoMap, select);               
                String firstElement = physicalIDList.get(0);
                           	
                if (physicalIDList.size()>0 && !firstElement.isEmpty()) { //Sometimes, the list is not really empty.                		
            		infoList = DomainObject.getInfo(this.context, physicalIDList.toStringArray(), getBasicSelects());
            		resultMap.put(select, infoList);              	
                }
            }
        }
        return resultMap;
    }

    StringList getBasicSelects() {
        return StringList.create(
                DomainConstants.SELECT_TYPE,
                DomainConstants.SELECT_NAME,
                DomainConstants.SELECT_ID,
                DomainConstants.SELECT_PHYSICAL_ID);
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

    public String getAttributeValueAsString(String attributeSelect) throws FrameworkException {
        return DomainObject.newInstance(context, objectOid)
                .getInfo(context, attributeSelect);
    }

    public void createTemplate(Context context, Map attributeList, DomainObject template) throws Exception {
        template = createAndSetName(context, attributeList, template);
        template = connectInterface(context, template);
        setIRMAttributes(attributeList, template);
        shareTemplateOwnership(context, attributeList, template);

    }

    private DomainObject createAndSetName(Context context, Map attributeList, DomainObject template) {

        try {
            String isAutoName = (String) attributeList.get(IRMUPTConstants.Basic.AUTO_NAME.get());

            if (isAutoName != null && isAutoName.equalsIgnoreCase(IRMUPTConstants.Basic.STRING_ON.get())) {
                autonameIRMTemplate(context, template, attributeList);
            } else {
                String name = (String) attributeList.get(IRMUPTConstants.Basic.FIELD_NAME.get());
                template.createObject(context,
                        UserPreferenceTemplateConstants.Type.TYPE_USER_PREFERENCE_TEMPLATE.getName(context),
                        name, "-",
                        UserPreferenceTemplateConstants.Policy.POLICY_USER_PREFERENCE_TEMPLATE_IRM.getName(context),
                        pgV3Constants.VAULT_ESERVICEPRODUCTION);
                logger.log(Level.INFO, "***** Generated IRM UPT Template: " + template.getInfo(context, DomainConstants.SELECT_NAME) + " *****");
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Exception in createAndSetName: "+e);
        }
        return template;
    }

    private void autonameIRMTemplate(Context context, DomainObject template, Map attributeList) {
        try {
            String prefix = "";
            if (IRMUPTConstants.Basic.STRING_BUSINESS_USE.get().equals(attributeList.get(IRMUPTConstants.Basic.FIELD_CLASSIFICATION.get())))
                prefix = IRMUPTConstants.Basic.SYMBOL_RESTRICTED.get();
            else
                prefix = IRMUPTConstants.Basic.SYMBOL_HIG_RESTRICTED.get();

            String autoName = FrameworkUtil.autoName(context,
                    UserPreferenceTemplateConstants.Basic.AUTONAME_TYPE_USER_PREFERENCE_TEMPLATE.get(),
                    null,
                    UserPreferenceTemplateConstants.Basic.AUTONAME_POLICY_USER_PREFERENCE_TEMPLATE_DSM.get(),
                    null,
                    null,
                    true,
                    false);

            String name = prefix.concat(autoName);
            template.createObject(context,
                    UserPreferenceTemplateConstants.Type.TYPE_USER_PREFERENCE_TEMPLATE.getName(context),
                    name,
                    "-",
                    UserPreferenceTemplateConstants.Policy.POLICY_USER_PREFERENCE_TEMPLATE_IRM.getName(context),
                    pgV3Constants.VAULT_ESERVICEPRODUCTION);
            logger.log(Level.INFO, "Generated IRM UPT Template: " + template.getInfo(context, DomainConstants.SELECT_NAME));
        } catch (Exception e) {
            logger.log(Level.WARNING, "Exception in autonameIRMTemplate: "+e);
        }
    }

    private DomainObject connectInterface(Context context, DomainObject template) throws MatrixException {
        Vault vault = context.getVault();
        BusinessInterface irmInterface = new BusinessInterface(UserPreferenceTemplateConstants.Interface.INTERFACE_UPT_TEMPLATE_IRM.getName(context), vault);
        template.addBusinessInterface(context, irmInterface);
        logger.log(Level.INFO, ">>>> Template interface is connected");
        return template;
    }

    public void setIRMAttributes(Map attributeList, DomainObject template) throws Exception {
        Map resetTaskrecipientAttribute = new HashMap<>();
        String taskMemberAttribute = IRMUPTConstants.Attributes.UPT_TASK_RECIPIENT_MEMBER.getName(context);
        String taskMemberAttributeSelect = IRMUPTConstants.Attributes.UPT_TASK_RECIPIENT_MEMBER.getSelect(context);
        String[] memberFinalList = (String[]) attributeList.get(IRMUPTConstants.Basic.FIELD_ROUTE_MEMBERS.get());


        String[] groupFinalList = (String[]) attributeList.get(IRMUPTConstants.Basic.FIELD_ROUTE_GROUPS.get());
        String taskGroupAttribute = IRMUPTConstants.Attributes.UPT_TASK_RECIPIENT_GROUP.getName(context);
        String taskGroupAttributeSelect = IRMUPTConstants.Attributes.UPT_TASK_RECIPIENT_GROUP.getSelect(context);
        String mode = (String) attributeList.get(IRMUPTConstants.Basic.MODE.get());

        Map attributeMap = new HashMap();
        attributeMap.put(IRMUPTConstants.Attributes.UPT_TITLE.getName(context), attributeList.get(IRMUPTConstants.Basic.FIELD_TITLE.get()));
        attributeMap.put(IRMUPTConstants.Attributes.UPT_DESCRIPTION.getName(context), attributeList.get(IRMUPTConstants.Basic.FIELD_TEMPLATE_DESC.get()));
        attributeMap.put(IRMUPTConstants.Attributes.UPT_POLICY, attributeList.get(IRMUPTConstants.Basic.FIELD_POLICY.get()));
        attributeMap.put(IRMUPTConstants.Attributes.UPT_CLASSIFICATION, attributeList.get(IRMUPTConstants.Basic.FIELD_CLASSIFICATION.get()));
        attributeMap.put(IRMUPTConstants.Attributes.UPT_ROUTE_ACTION.getName(context), attributeList.get(IRMUPTConstants.Basic.FIELD_ROUTE_ACTION.get()));
        attributeMap.put(IRMUPTConstants.Attributes.UPT_ROUTE_INSTRUCTION.getName(context), attributeList.get(IRMUPTConstants.Basic.FIELD_ROUTE_INSTRUCTION.get()));
        attributeMap.put(IRMUPTConstants.Attributes.UPT_MIGRATED.getName(context), attributeList.get(IRMUPTConstants.Basic.FALSE_LITERAL.get()));

        //Multivalue attributes
        attributeMap.put(IRMUPTConstants.Attributes.UPT_SHARE_WITH_MEMBERS.getName(context), convertToPID(context, (String) attributeList.get(IRMUPTConstants.Basic.FIELD_SHARE_WITH_MEMBERS.get()), new StringList(new String[]{DomainConstants.SELECT_NAME, DomainConstants.SELECT_PHYSICAL_ID})));
        attributeMap.put(IRMUPTConstants.Attributes.UPT_BUSINESS_USE.getName(context), convertToPID(context, (String) attributeList.get(IRMUPTConstants.Basic.FIELD_BUSINESS_USE_OID.get()), new StringList(DomainConstants.SELECT_PHYSICAL_ID)));
        attributeMap.put(IRMUPTConstants.Attributes.UPT_HIGHLY_RESTRICTED.getName(context), convertToPID(context, (String) attributeList.get(IRMUPTConstants.Basic.FIELD_HIGHLY_RESTRICTED_OID.get()), new StringList(DomainConstants.SELECT_PHYSICAL_ID)));
        attributeMap.put(IRMUPTConstants.Attributes.UPT_REGION.getName(context), convertToPID(context, (String) attributeList.get(IRMUPTConstants.Basic.FIELD_REGION_OID.get()), new StringList(DomainConstants.SELECT_PHYSICAL_ID)));
        attributeMap.put(IRMUPTConstants.Attributes.UPT_BUSINESS_AREA.getName(context), convertToStringList(context, (String) attributeList.get(IRMUPTConstants.Basic.FIELD_BUSINESS_AREA_PID.get())));

        //Route Task Receipients
        if (groupFinalList != null) {
            if (IRMUPTConstants.Basic.MODE_EDIT.get().equals(IRMUPTConstants.Basic.MODE.get())) {
                Map infoMap = template.getInfo(context, StringList.create(taskMemberAttributeSelect));
                if (infoMap.size() > 0) {
                    resetTaskrecipientAttribute.put(taskMemberAttribute, new StringList());
                    template.setAttributeValues(context, resetTaskrecipientAttribute);
                }
            }
            attributeMap.put(taskGroupAttribute, new StringList(groupFinalList));
        }

        if (memberFinalList != null) {
            if (IRMUPTConstants.Basic.MODE_EDIT.get().equals(IRMUPTConstants.Basic.MODE.get())) {
                Map infoMap = template.getInfo(context, StringList.create(taskGroupAttribute));
                if (infoMap.size() > 0) {
                    resetTaskrecipientAttribute.put(taskGroupAttribute, new StringList());
                    template.setAttributeValues(context, resetTaskrecipientAttribute);
                }
            }
            attributeMap.put(taskMemberAttribute, new StringList(memberFinalList));
        }


        template.setAttributeValues(context, attributeMap);
        template.setDescription(context, (String) attributeList.get(IRMUPTConstants.Basic.FIELD_DESCRIPTION.get()));
    }

    private void shareTemplateOwnership(Context context, Map attributeList, DomainObject template) {
        try {
            String[] templateID = {template.getInfo(context, DomainConstants.SELECT_ID)};
            UserPreferenceUtilIRM preferenceUtil = new UserPreferenceUtilIRM();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Exception in shareTemplateOwnership: " + e.getMessage());
        }
    }

    private StringList convertToStringList(Context context, String attrValues) throws FrameworkException {
        String[] values = attrValues.split("\\|");
        return new StringList(values);
    }

    private StringList convertToPID(Context context, String oid, StringList selectList) throws FrameworkException {
        StringList infoList = new StringList();
        if (UIUtil.isNotNullAndNotEmpty(oid)) {
            String[] values = oid.split("\\|");
            infoList = getPhysicalIDs(context, values, selectList);
        }
        return infoList;
    }

    private StringList getPhysicalIDs(Context context, String[] objectIds, StringList selectList) throws FrameworkException {
        StringList infoList = new StringList();
        MapList mlphysicalID = getSelectedInfoForIDs(objectIds, selectList);

        for (int i = 0; i < mlphysicalID.size(); i++) {
            Map infoMap = (Map) mlphysicalID.get(i);
            String physicalId = (String) infoMap.get(DomainConstants.SELECT_PHYSICAL_ID);
            String name = (String) infoMap.get(DomainConstants.SELECT_NAME);
            if (UIUtil.isNotNullAndNotEmpty(name)) {
                StringBuilder sb = new StringBuilder();
                sb.append(name);
                sb.append(IRMUPTConstants.Basic.SYMBOL_TILDE.get());
                sb.append(physicalId);
                infoList.add(sb.toString());
            } else {
                infoList.add(physicalId);
            }
        }
        return infoList;
    }

    public MapList getSelectedInfoForIDs(String[] objectOids, StringList selectList) throws FrameworkException {
        return DomainObject.getInfo(context, objectOids, selectList);
    }

    public String isSharedTemplate() throws FrameworkException {
        StringList slbusSelects = new StringList();
        slbusSelects.add(IRMUPTConstants.Basic.OWNERSHIP.get());
        template = DomainObject.newInstance(context, objectOid);

        Map ownersListMap = template.getInfo(context, slbusSelects);

        for (int i = 0; i < ownersListMap.size(); i++) {
            StringList ownerShip = (StringList) ownersListMap.get(IRMUPTConstants.Basic.OWNERSHIP.get());

            if (ownerShip.size() > 0 && UIUtil.isNotNullAndNotEmpty(ownerShip.get(0))) {
                return IRMUPTConstants.Basic.STRING_YES.get();
            }
        }
        return IRMUPTConstants.Basic.STRING_NO.get();
    }

    public String getUPTClassificationAsString() throws Exception {
        return getAttributeValueAsString(IRMUPTConstants.Attributes.UPT_CLASSIFICATION.getSelect(context));
    }

    public String getUPTTitleAsString() throws Exception {
        return getAttributeValueAsString(IRMUPTConstants.Attributes.UPT_TITLE.getSelect(context));
    }

    public String getUPTDescriptionAsString() throws Exception {
        return getAttributeValueAsString(IRMUPTConstants.Attributes.UPT_DESCRIPTION.getSelect(context));
    }

    public String getUPTRouteInstructionsAsString() throws Exception {
        return getAttributeValueAsString(IRMUPTConstants.Attributes.UPT_ROUTE_INSTRUCTION.getSelect(context));
    }

    public String getUPTRouteActionAsString() throws Exception {
        return getAttributeValueAsString(IRMUPTConstants.Attributes.UPT_ROUTE_ACTION.getSelect(context));
    }

    public String getUPTHighlyRestrictedAsString(String select) throws Exception {
        Map<Object, Object> infoMap = getHighlyRestrictedClass();
        MapList mapList = (MapList) infoMap.get(IRMUPTConstants.Attributes.UPT_HIGHLY_RESTRICTED.getSelect(context));
        return getValuesAsPipeSeparatedString(mapList, select);
    }

    public String getUPTBusinessUseAsString(String select) throws Exception {
        Map<Object, Object> infoMap = getBusinessUseClass();
        MapList mapList = (MapList) infoMap.get(IRMUPTConstants.Attributes.UPT_BUSINESS_USE.getSelect(context));
        return getValuesAsPipeSeparatedString(mapList, select);
    }

    public String getUPTBusinessAreaAsString(String select) throws Exception {
        Map<Object, Object> infoMap = getBusinessArea();
        MapList mapList = (MapList) infoMap.get(IRMUPTConstants.Attributes.UPT_BUSINESS_AREA.getSelect(context));
        return getValuesAsPipeSeparatedString(mapList, select);
    }

    public String getUPTShareWithMembersAsString(String select) throws Exception {
        Map<Object, Object> infoMap = getShareWithMembers();
        MapList mapList = (MapList) infoMap.get(IRMUPTConstants.Attributes.UPT_SHARE_WITH_MEMBERS.getSelect(context));
        return getValuesAsPipeSeparatedString(mapList, select);
    }

    public String getUPTTaskMembersAsString(String select) throws Exception {
        Map<Object, Object> infoMap = getTaskMemberRecipients();
        MapList mapList = (MapList) infoMap.get(IRMUPTConstants.Attributes.UPT_TASK_RECIPIENT_MEMBER.getSelect(context));
        return getValuesAsPipeSeparatedString(mapList, select);
    }

    public String getUPTTaskGroupsAsString(String select) throws Exception {
        Map<Object, Object> infoMap = getTaskGroupRecipients();
        MapList mapList = (MapList) infoMap.get(IRMUPTConstants.Attributes.UPT_TASK_RECIPIENT_GROUP.getSelect(context));
        return getValuesAsPipeSeparatedString(mapList, select);
    }

    public String getUPTRegionAsString(String select) throws Exception {
        Map<Object, Object> infoMap = getRegion();
        MapList mapList = (MapList) infoMap.get(IRMUPTConstants.Attributes.UPT_REGION.getSelect(context));
        return getValuesAsPipeSeparatedString(mapList, select);
    }

    public String getUPTPolicyAsString() throws Exception {
        return getAttributeValueAsString(IRMUPTConstants.Attributes.UPT_POLICY.getSelect(context));
    }

    String getValuesAsPipeSeparatedString(MapList mapList, String select) {
        String ret = DomainConstants.EMPTY_STRING;

        if (null != mapList && mapList.size() > 0 && !mapList.isEmpty()) {           
            ret = (String) mapList
                    .stream()
                    .map(o -> ((Map<Object, Object>) o).get(select))
                    .collect(Collectors.joining(IRMUPTConstants.Basic.SYMBOL_PIPE.get()));
        }

        return ret;
    }

    public String getValuesAsPipeSeparatedString(StringList values) {
        return values.stream()
                .collect(Collectors.joining(IRMUPTConstants.Basic.SYMBOL_PIPE.get()));
    }
    
    /*
     * Added by IRM(Sogeti) for 2022x.04 Dec CW Defect - 55113
     * Called from pgUserPreferenceTemplateIRMCreateDialog.jsp 
     */
    public static String getGlobalRegionOID(Context context) {
	    String globalRegion = DomainConstants.EMPTY_STRING;
    
        try {
            String strMQLQuery = "temp query bus Region 'GLOBAL' * select id dump |";
            globalRegion = MqlUtil.mqlCommand(context, strMQLQuery, false, false);
            globalRegion = FrameworkUtil.split(globalRegion, IRMUPTConstants.Basic.SYMBOL_PIPE.get()).getElement(3);
        } catch (Exception e){
            logger.log(Level.SEVERE, "Exception in getGlobalRegionOID method: " + e);
        }
	    
		return globalRegion ;
	 }
   
}

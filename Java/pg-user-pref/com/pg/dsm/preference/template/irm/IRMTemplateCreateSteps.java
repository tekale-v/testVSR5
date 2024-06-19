package com.pg.dsm.preference.template.irm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.preference.enumeration.IRMUPTConstants;
import com.pg.dsm.preference.enumeration.PreferenceConstants;
import com.pg.dsm.preference.enumeration.UserPreferenceTemplateConstants;
import com.pg.dsm.preference.util.UserPreferenceUtilIRM;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.BusinessInterface;
import matrix.db.Context;
import matrix.db.Vault;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class IRMTemplateCreateSteps implements IIRMTemplateCreateSteps {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    Context context;

    public IRMTemplateCreateSteps(Context context) {
        this.context = context;
    }

    /**
     * @param attributeMap
     * @return
     * @throws FrameworkException
     */
    @Override
    public DomainObject createTemplate(Map attributeMap) throws FrameworkException {
        DomainObject domainObject = null;
        try {
            String autoNameKey = IRMUPTConstants.Basic.AUTO_NAME.get();
            String autoNameChecked = attributeMap.containsKey(autoNameKey) ? (String) attributeMap.get(autoNameKey) : DomainConstants.EMPTY_STRING;
            if (UIUtil.isNotNullAndNotEmpty(autoNameChecked) && autoNameChecked.equalsIgnoreCase(IRMUPTConstants.Basic.STRING_ON.get())) {
                domainObject = getTemplateObjectWithAutoName(attributeMap);
                logger.log(Level.INFO, "UPT Template Create | Success with auto-name");
            } else {
                String nameKey = IRMUPTConstants.Basic.FIELD_NAME.get();
                String manualName = (attributeMap.containsKey(nameKey)) ? (String) attributeMap.get(nameKey) : DomainConstants.EMPTY_STRING;
                if (UIUtil.isNotNullAndNotEmpty(manualName)) {
                    if (UIUtil.isNotNullAndNotEmpty(manualName)) {
                    	domainObject = DomainObject.newInstance(this.context);
                        domainObject.createObject(this.context,
                                UserPreferenceTemplateConstants.Type.TYPE_USER_PREFERENCE_TEMPLATE.getName(this.context),
                                manualName, "-",
                                UserPreferenceTemplateConstants.Policy.POLICY_USER_PREFERENCE_TEMPLATE_IRM.getName(this.context),
                                pgV3Constants.VAULT_ESERVICEPRODUCTION);
                        logger.log(Level.INFO, "UPT Template Create | Success with manual-name");
                    } else {
                        logger.log(Level.SEVERE, "UPT Template Create | Failed - Manual name is empty");
                    }
                } else {
                    logger.log(Level.SEVERE, "UPT Template Create | Failed - Incoming Manual name is empty");
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception occurred while creating IRM UPT Object: " + e);
            throw e;
        }
        return domainObject;
    }

    /**
     * @param domainObject
     * @return
     * @throws MatrixException
     */
    @Override
    public boolean addInterface(DomainObject domainObject) throws MatrixException {
        boolean isOkay = Boolean.FALSE;
        try {
            Vault vault = this.context.getVault();
            String interfaceName = UserPreferenceTemplateConstants.Interface.INTERFACE_UPT_TEMPLATE_IRM.getName(this.context);
            if (UIUtil.isNotNullAndNotEmpty(interfaceName)) {
                BusinessInterface irmInterface = new BusinessInterface(interfaceName, vault);
                domainObject.addBusinessInterface(this.context, irmInterface);
                isOkay = Boolean.TRUE;
                logger.log(Level.INFO, "UPT Template Create | Interface Addition Successful");
            } else {
                logger.log(Level.SEVERE, "Interface schema property name is empty");
            }
        } catch (MatrixException e) {
            logger.log(Level.SEVERE, "Exception occurred while adding interface to IRM UPT Object: " + e);
            throw e;
        }
        return isOkay;
    }

    /**
     * @param domainObject
     * @param requestMap
     * @return
     * @throws FrameworkException
     */
    @Override
    public boolean updateAttributes(DomainObject domainObject, Map requestMap) throws FrameworkException {
        boolean isOkay = Boolean.FALSE;
        try {
            String taskMemberAttribute = IRMUPTConstants.Attributes.UPT_TASK_RECIPIENT_MEMBER.getName(this.context);
            String taskMemberAttributeSelect = IRMUPTConstants.Attributes.UPT_TASK_RECIPIENT_MEMBER.getSelect(this.context);
            String[] memberFinalList = (String[]) requestMap.get(IRMUPTConstants.Basic.FIELD_ROUTE_MEMBERS.get());

            String[] groupFinalList = (String[]) requestMap.get(IRMUPTConstants.Basic.FIELD_ROUTE_GROUPS.get());
            String taskGroupAttribute = IRMUPTConstants.Attributes.UPT_TASK_RECIPIENT_GROUP.getName(this.context);

            logger.log(Level.INFO, "UPT Template Create | Single value attributes - START");
            Map attributeMap = new HashMap();
            attributeMap.put(IRMUPTConstants.Attributes.UPT_MIGRATED.getName(this.context), String.valueOf(Boolean.FALSE));

            updateAttributeMap(requestMap, IRMUPTConstants.Basic.FIELD_TITLE.get(), attributeMap);
            updateAttributeMap(requestMap, IRMUPTConstants.Basic.FIELD_TEMPLATE_DESC.get(), attributeMap);
            updateAttributeMap(requestMap, IRMUPTConstants.Basic.FIELD_POLICY.get(), attributeMap);
            updateAttributeMap(requestMap, IRMUPTConstants.Basic.FIELD_CLASSIFICATION.get(), attributeMap);
            updateAttributeMap(requestMap, IRMUPTConstants.Basic.FIELD_ROUTE_ACTION.get(), attributeMap);
            updateAttributeMap(requestMap, IRMUPTConstants.Basic.FIELD_ROUTE_INSTRUCTION.get(), attributeMap);

            logger.log(Level.INFO, "UPT Template Create | Single value attributes - END");

            String shareWithMemberFieldKey = IRMUPTConstants.Basic.FIELD_SHARE_WITH_MEMBERS.get();
            String businessUseFieldKey = IRMUPTConstants.Basic.FIELD_BUSINESS_USE_OID.get();
            String highlyRestrictedFieldKey = IRMUPTConstants.Basic.FIELD_HIGHLY_RESTRICTED_OID.get();
            String regionFieldKey = IRMUPTConstants.Basic.FIELD_REGION_OID.get();
            String businessAreaFieldKey = IRMUPTConstants.Basic.FIELD_BUSINESS_AREA_PID.get();
            String pipe = PreferenceConstants.Basic.SYMBOL_PIPE.get();

            String shareWithMemberAttributeKey = IRMUPTConstants.Attributes.UPT_SHARE_WITH_MEMBERS.getName(this.context);
            String businessUseAttributeKey = IRMUPTConstants.Attributes.UPT_BUSINESS_USE.getName(this.context);
            String highlyRestrictedAttributeKey = IRMUPTConstants.Attributes.UPT_HIGHLY_RESTRICTED.getName(this.context);
            String regionAttributeKey = IRMUPTConstants.Attributes.UPT_REGION.getName(this.context);
            String businessAreaAttributeKey = IRMUPTConstants.Attributes.UPT_BUSINESS_AREA.getName(this.context);

            Set<String> shareWithMemberSet = new HashSet<>();
            Set<String> businessUseSet = new HashSet<>();
            Set<String> highlyRestrictedSet = new HashSet<>();
            Set<String> regionSet = new HashSet<>();

            getMultiValueOID(shareWithMemberSet, requestMap, shareWithMemberFieldKey, pipe);
            getMultiValueOID(businessUseSet, requestMap, businessUseFieldKey, pipe);
            getMultiValueOID(highlyRestrictedSet, requestMap, highlyRestrictedFieldKey, pipe);
            getMultiValueOID(regionSet, requestMap, regionFieldKey, pipe);

            StringList allOIDs = new StringList();
            allOIDs.addAll(shareWithMemberSet);
            allOIDs.addAll(businessUseSet);
            allOIDs.addAll(highlyRestrictedSet);
            allOIDs.addAll(regionSet);

            if (!allOIDs.isEmpty()) {
                MapList infoList = DomainObject.getInfo(this.context, allOIDs.toStringArray(), StringList.create(DomainConstants.SELECT_ID, DomainConstants.SELECT_PHYSICAL_ID));
                Map<String, String> flatMap = getFlatMap(infoList);
                if (null != flatMap && !flatMap.isEmpty()) {
                    // handle multi-value attributes
                    logger.log(Level.INFO, "UPT Template Create | Multi-value attributes - START");
                    updateMultiValueList(flatMap, requestMap, shareWithMemberFieldKey, attributeMap, shareWithMemberAttributeKey, pipe);
                    updateMultiValueList(flatMap, requestMap, businessUseFieldKey, attributeMap, businessUseAttributeKey, pipe);
                    updateMultiValueList(flatMap, requestMap, highlyRestrictedFieldKey, attributeMap, highlyRestrictedAttributeKey, pipe);
                    updateMultiValueList(flatMap, requestMap, regionFieldKey, attributeMap, regionAttributeKey, pipe);
                    updateMultiValueList(flatMap, requestMap, businessAreaFieldKey, attributeMap, businessAreaAttributeKey, pipe);
                    logger.log(Level.INFO, "UPT Template Create | Multi-value attributes - END");
                }
            }

            logger.log(Level.INFO, "UPT Template Create | Route Recipient Group - START");
            //Route Task Receipients
            if (groupFinalList != null) {
                attributeMap.put(taskGroupAttribute, new StringList(groupFinalList));
            }
            logger.log(Level.INFO, "UPT Template Create | Route Recipient Group - END");

            logger.log(Level.INFO, "UPT Template Create | Route Recipient Member - START");
            if (memberFinalList != null) {
                attributeMap.put(taskMemberAttribute, new StringList(memberFinalList));

            }
            logger.log(Level.INFO, "UPT Template Create | Route Recipient Member - END");

            logger.log(Level.INFO, String.format("Update Attribute Map Key Pair (%s)", attributeMap));
            domainObject.setAttributeValues(this.context, attributeMap);
            domainObject.setDescription(this.context, (String) requestMap.get(IRMUPTConstants.Basic.FIELD_DESCRIPTION.get()));
            isOkay = Boolean.TRUE;
            logger.log(Level.INFO, "UPT Template Create | All Attributes Updated successfully");
        } catch (FrameworkException e) {
            logger.log(Level.SEVERE, "Exception occurred while updating attributes on IRM UPT Object:" + e);
            throw e;
        }
        return isOkay;
    }

    public void updateAttributeMap(Map requestMap, String requestKey, Map attributeMap) {
        switch (requestKey) {
            case "title":
                updateSingleValue(requestMap,
                        requestKey,
                        attributeMap,
                        IRMUPTConstants.Attributes.UPT_TITLE.getName(this.context));
                break;
            case "templateDesc":
                updateSingleValue(requestMap,
                        requestKey,
                        attributeMap,
                        IRMUPTConstants.Attributes.UPT_DESCRIPTION.getName(this.context));
                break;
            case "policy":
                updateSingleValue(requestMap,
                        requestKey,
                        attributeMap,
                        IRMUPTConstants.Attributes.UPT_POLICY.getName(this.context));
                break;
            case "classification":
                updateSingleValue(requestMap,
                        requestKey,
                        attributeMap,
                        IRMUPTConstants.Attributes.UPT_CLASSIFICATION.getName(this.context));
                break;
            case "routeAction":
                updateSingleValue(requestMap,
                        requestKey,
                        attributeMap,
                        IRMUPTConstants.Attributes.UPT_ROUTE_ACTION.getName(this.context));
                break;
            case "routeInstructions":
                updateSingleValue(requestMap,
                        requestKey,
                        attributeMap,
                        IRMUPTConstants.Attributes.UPT_ROUTE_INSTRUCTION.getName(this.context));
                break;
                default:
                	break;
        }

    }

    void updateSingleValue(Map requestMap, String requestKey, Map attributeMap, String attributeKey) {
        String value = getValueFromMap(requestMap, requestKey);
        if (UIUtil.isNotNullAndNotEmpty(value)) {
            attributeMap.put(attributeKey, value);
        }
    }

    /*
     * Access modifier is set to public as it is called directly called from pgUserPreferenceTemplateEditProcess.jsp
     */
    public Map<String, String> getFlatMap(MapList infoList) {
        Map<String, String> flatMap = new HashMap<>();
        Iterator iterator = infoList.iterator();
        Map<Object, Object> tempMap;
        while (iterator.hasNext()) {
            tempMap = (Map<Object, Object>) iterator.next();
            flatMap.put((String) tempMap.get(DomainConstants.SELECT_ID), (String) tempMap.get(DomainConstants.SELECT_PHYSICAL_ID));
        }
        return flatMap;
    }

	/*
	 * Access modifier is set to public as it is called directly called from pgUserPreferenceTemplateEditProcess.jsp
	 */
    public void getMultiValueOID(Set<String> uniqueSet, Map requestMap, String requestKey, String separator) {
        if (requestMap.containsKey(requestKey)) {
            String fieldValue = (String) requestMap.get(requestKey);
            if (UIUtil.isNotNullAndNotEmpty(fieldValue)) {
                StringList spiltList = StringUtil.split(fieldValue, separator);
                if (null != spiltList && !spiltList.isEmpty()) {
                    uniqueSet.addAll(spiltList);
                }
            }
        }
    }

    String getValueFromMap(Map incomingMap, String key) {
        return (incomingMap.containsKey(key)) ? (String) incomingMap.get(key) : DomainConstants.EMPTY_STRING;
    }

    /**
     * @param domainObject
     * @return
     * @throws Exception
     */
    @Override
    public boolean applyShareTemplateWith(DomainObject domainObject, String shareTemplateWithValue) throws Exception {
        boolean applied = Boolean.FALSE;
        try {
            if (UIUtil.isNotNullAndNotEmpty(shareTemplateWithValue)) {
                UserPreferenceUtilIRM preferenceUtil = new UserPreferenceUtilIRM();
                preferenceUtil.applyShareTemplateWith(this.context, domainObject, shareTemplateWithValue);
                logger.log(Level.INFO, "UPT Template Create | (Share Template With) - Successful");
                applied = Boolean.TRUE;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception occurred while apply (Share Template With) on IRM UPT Object: " + e.getMessage());
            throw e;
        }
        return applied;
    }

    /**
     * @param attributeMap
     * @return
     * @throws FrameworkException
     */
    private DomainObject getTemplateObjectWithAutoName(Map attributeMap) throws FrameworkException {
        DomainObject domainObject = null;
        try {
            String prefix = "";
            String classificationKey = IRMUPTConstants.Basic.FIELD_CLASSIFICATION.get();
            String classification = (attributeMap.containsKey(classificationKey)) ? (String) attributeMap.get(classificationKey) : DomainConstants.EMPTY_STRING;
            if (IRMUPTConstants.Basic.STRING_BUSINESS_USE.get().equals(classification))
                prefix = IRMUPTConstants.Basic.SYMBOL_RESTRICTED.get();
            else {
                prefix = IRMUPTConstants.Basic.SYMBOL_HIG_RESTRICTED.get();
            }

            String autoName = FrameworkUtil.autoName(this.context,
                    UserPreferenceTemplateConstants.Basic.AUTONAME_TYPE_USER_PREFERENCE_TEMPLATE.get(),
                    null,
                    UserPreferenceTemplateConstants.Basic.AUTONAME_POLICY_USER_PREFERENCE_TEMPLATE_DSM.get(),
                    null,
                    null,
                    true,
                    false);

            String templateName = prefix.concat(autoName);
            String templateType = UserPreferenceTemplateConstants.Type.TYPE_USER_PREFERENCE_TEMPLATE.getName(this.context);
            String templatePolicy = UserPreferenceTemplateConstants.Policy.POLICY_USER_PREFERENCE_TEMPLATE_IRM.getName(this.context);
            domainObject = DomainObject.newInstance(this.context);
            domainObject.createObject(this.context,
                    templateType,
                    templateName,
                    "1",
                    templatePolicy,
                    pgV3Constants.VAULT_ESERVICEPRODUCTION);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception occurred while creating IRM UPT object with auto-name: " + e);
            throw e;
        }
        return domainObject;
    }

    /*
     * Access modifier is set to public as it is called directly called from pgUserPreferenceTemplateEditProcess.jsp
     */
    public void updateMultiValueList(Map<String, String> flatMap, Map requestMap, String requestKey, Map attributeMap, String attributeKey, String separator) {
        StringList physicalIDs = new StringList();
        if (requestMap.containsKey(requestKey)) {
            String value = (String) requestMap.get(requestKey);
            StringList iDs = StringUtil.split(value, separator);
            if (null != iDs) {
                if (IRMUPTConstants.Basic.FIELD_BUSINESS_AREA_PID.get().equals(requestKey)) {
                    physicalIDs = iDs; // in case of business area - as incoming value shall be physical id.
                } else {
                    for (String id : iDs) {
                        if (flatMap.containsKey(id)) {
                            physicalIDs.add(flatMap.get(id));
                        }
                    }
                }
            }
        }
        if (null != physicalIDs) {
            attributeMap.put(attributeKey, physicalIDs);
        }
    }

    private StringList convertToPID(String oid, StringList selectList) throws FrameworkException {
        StringList infoList = new StringList();
        if (UIUtil.isNotNullAndNotEmpty(oid)) {
            String[] values = oid.split("\\|");
            infoList = getPhysicalIDs(values, selectList);
        }
        return infoList;
    }

    private StringList getPhysicalIDs(String[] objectIds, StringList selectList) throws FrameworkException {
        StringList infoList = new StringList();
        if (null != objectIds && objectIds.length > 0) {
            MapList physicalIDList = getSelectedInfoForIDs(objectIds, selectList);
            for (int i = 0; i < physicalIDList.size(); i++) {
                Map infoMap = (Map) physicalIDList.get(i);
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
        }
        return infoList;
    }

    public MapList getSelectedInfoForIDs(String[] objectOids, StringList selectList) throws FrameworkException {
        return DomainObject.getInfo(this.context, objectOids, selectList);
    }

    private StringList convertToStringList(String attrValues) throws FrameworkException {
        String[] values = attrValues.split("\\|");
        return new StringList(values);
    }
    
    /*
     * Used this API to update UPT attributes in the pgUserPreferenceTemplateIRMEditProcess.jsp
     */
    public boolean updateAttributesWhileEdit(Context context, Map attributeMap, String objectOID) throws FrameworkException {
		boolean flag = false;
		if(UIUtil.isNotNullAndNotEmpty(objectOID)) {
			if(!attributeMap.isEmpty()) {
				DomainObject object = DomainObject.newInstance(context, objectOID);
				//Update description
				if(attributeMap.containsKey(IRMUPTConstants.Basic.FIELD_TEMPLATE_DESC.get())){
		          object.setDescription(context,(String) attributeMap.get(IRMUPTConstants.Basic.FIELD_TEMPLATE_DESC.get()));
		          attributeMap.remove(IRMUPTConstants.Basic.FIELD_TEMPLATE_DESC.get());
			    }
				object.setAttributeValues(context, attributeMap);	
				flag = true;
			} else {
				logger.log(Level.WARNING, "Passed empty attributeMap Map");
			}		
		} else {
			logger.log(Level.SEVERE, "Passed empty object ID");
		}
		return flag;
	}
}

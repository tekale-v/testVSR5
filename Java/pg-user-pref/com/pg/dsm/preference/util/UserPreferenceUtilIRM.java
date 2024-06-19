package com.pg.dsm.preference.util;
import com.matrixone.apps.domain.util.*;
import com.pg.dsm.preference.enumeration.IRMUPTConstants;
import com.pg.dsm.preference.enumeration.PreferenceConstants;
import com.pg.dsm.preference.enumeration.UserPreferenceTemplateConstants;
import com.pg.dsm.preference.models.IRMTemplate;
import com.pg.dsm.preference.models.IRMUserGroup;
import com.pg.dsm.preference.services.IRMSimpleRoute;

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
import com.matrixone.apps.domain.DomainAccess;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;

import com.pg.dsm.preference.models.IRMMember;
import com.pg.dsm.preference.models.IRMRouteDetails;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.db.SelectConstants;
import matrix.util.MatrixException;
import matrix.util.StringList;


public class UserPreferenceUtilIRM {
    private static final Logger logger = Logger.getLogger(UserPreferenceUtil.class.getName());
    
    
    public Map<Object, Object> getIRMAttributePreferencesMap(Context context,IRMTemplate irmTemplateObj) throws Exception {
        Map<Object, Object> returnMap = new HashMap<>();
        returnMap.put(pgV3Constants.ATTRIBUTE_TITLE,irmTemplateObj.getTemplateTitle());
        String strCLassification;
        if(IRMUPTConstants.Basic.STRING_BUSINESS_USE.get().equalsIgnoreCase(irmTemplateObj.getUptClassification())) 
        	strCLassification = IRMUPTConstants.Basic.CLASSIFICATION_BUSINESS_USE.get();
        else 
        	strCLassification = IRMUPTConstants.Basic.CLASSIFICATION_RESTRICTED.get();
        returnMap.put(pgV3Constants.ATTRIBUTE_PGIPCLASSIFICATION, strCLassification);
        
        returnMap.put(IRMUPTConstants.Attributes.DOCUMENT_BUSINESS_AREA.getName(context), GetAttributeasStringList(context,irmTemplateObj.getUptBusinessArea()));  
        return returnMap;
    }
    public StringList GetAttributeasStringList(Context context, StringList slID) throws Exception {
    	String str = GetAttributeasString(context,  slID); 
        StringList slName = StringUtil.split(str, "|");
        return slName;
    }
    
    public String GetAttributeasString(Context context, StringList slID) throws Exception {
    	MapList mlData = null;
    	String firstElement = slID.get(0);
         if (slID.size()>0 && !firstElement.isEmpty()) { //Sometimes, the list is not really empty.                		
        	 mlData = DomainObject.getInfo(context, slID.toStringArray(), new StringList(DomainConstants.SELECT_NAME));
        }
        String str =  getValuesAsPipeSeparatedString(mlData, DomainConstants.SELECT_NAME);
        return str;
    }
   public String getValuesAsPipeSeparatedString(MapList mapList, String select) {
        String ret = DomainConstants.EMPTY_STRING;

        if (null != mapList && mapList.size() > 0 && !mapList.isEmpty()) {           
            ret = (String) mapList
                    .stream()
                    .map(o -> ((Map<Object, Object>) o).get(select))
                    .collect(Collectors.joining(IRMUPTConstants.Basic.SYMBOL_PIPE.get()));
        }

        return ret;
    }
   
   
    private String getZerothIndexValue(Map map, String var1){
		StringList var2 = (StringList) map.get(var1);
		return (var2.size()==0)? "" : var2.get(0);
	}
	private String pipeConcatenatedString(Map map, String attrName){
		StringList list = (StringList) map.get(attrName);
		return list.stream()
					.collect(Collectors.joining("|"));
}
	public Map<Object, Object> fetchValuesFromIRMPreference(Context context,String strDocId,StringList selectList) throws FrameworkException {
	
		Map mapUPTInfo = null;
		if(UIUtil.isNotNullAndNotEmpty(strDocId)) {
			DomainObject domDocId = DomainObject.newInstance(context,strDocId);
		   	String strUPTTemplateId = domDocId.getInfo(context, IRMUPTConstants.Attributes.UPT_PID.getSelect(context));
		   	if(UIUtil.isNotNullAndNotEmpty(strUPTTemplateId)) {	 		   		
		   		MapList infoList = DomainObject.getInfo(context, new String[]{strUPTTemplateId}, selectList);
		   		if (null != infoList && !infoList.isEmpty()) {
		   			mapUPTInfo = (Map<Object, Object>) infoList.get(0);
		   		}
		   	}
		}
		   	
	   	return mapUPTInfo;
   		    	 
	}
	
	/**
     * @param context
     * @returnIRMTemplate
     * @throws FrameworkException
     */
   
    public IRMTemplate setIRMUserPreferenceAttributes(Context context,String strDocId, String strUPTID) throws Exception {
    	StringList selectList = new StringList(8);
        selectList.add(IRMUPTConstants.Attributes.UPT_TITLE.getSelect(context));
        selectList.add(IRMUPTConstants.Attributes.UPT_DESCRIPTION.getSelect(context));
        selectList.add(IRMUPTConstants.Attributes.UPT_POLICY.getSelect(context));
        selectList.add(IRMUPTConstants.Attributes.UPT_CLASSIFICATION.getSelect(context));
        selectList.add(IRMUPTConstants.Attributes.UPT_BUSINESS_USE.getSelect(context));
        selectList.add(IRMUPTConstants.Attributes.UPT_HIGHLY_RESTRICTED.getSelect(context));
        selectList.add(IRMUPTConstants.Attributes.UPT_BUSINESS_AREA.getSelect(context));
        selectList.add(IRMUPTConstants.Attributes.UPT_REGION.getSelect(context));
        DomainObject domDocId = DomainObject.newInstance(context,strDocId);
        if(UIUtil.isNotNullAndNotEmpty(strUPTID))
        	domDocId.setAttributeValue(context,PropertyUtil.getSchemaProperty(context, "attribute_pgUPTPhyID"), strUPTID);;
        Map mapUPTInfo = fetchValuesFromIRMPreference(context, strDocId,selectList);
    	IRMTemplate irmTemplateObj = new IRMTemplate();
		if(null != mapUPTInfo) {
			 irmTemplateObj.setTemplateTitle((getStringListFromMap(mapUPTInfo,IRMUPTConstants.Attributes.UPT_TITLE.getSelect(context))).get(0));
			 irmTemplateObj.setTemplateDesc((getStringListFromMap(mapUPTInfo,IRMUPTConstants.Attributes.UPT_DESCRIPTION.getSelect(context))).get(0));		    
			String strPolicy = (getStringListFromMap(mapUPTInfo,IRMUPTConstants.Attributes.UPT_POLICY.getSelect(context))).get(0);
	        if(IRMUPTConstants.Basic.UPT_POLICY_SIGNATURE.get().equalsIgnoreCase(strPolicy))
	        	strPolicy = IRMUPTConstants.Basic.POLICY_SIGNATURE.get();
	        else if(IRMUPTConstants.Basic.UPT_POLICY_SELF.get().equalsIgnoreCase(strPolicy))
	        	strPolicy = IRMUPTConstants.Basic.POLICY_SELF.get();
	        irmTemplateObj.setUptPolicy(strPolicy);
	        irmTemplateObj.setUptClassification((getStringListFromMap(mapUPTInfo,IRMUPTConstants.Attributes.UPT_CLASSIFICATION.getSelect(context))).get(0));
	        irmTemplateObj.setUptBusinessUseIPClass((getStringListFromMap(mapUPTInfo,IRMUPTConstants.Attributes.UPT_BUSINESS_USE.getSelect(context))));
	       irmTemplateObj.setUptHiRestrictedIPClass((getStringListFromMap(mapUPTInfo,IRMUPTConstants.Attributes.UPT_HIGHLY_RESTRICTED.getSelect(context))));
	       irmTemplateObj.setUptBusinessArea((getStringListFromMap(mapUPTInfo,IRMUPTConstants.Attributes.UPT_BUSINESS_AREA.getSelect(context))));
			 irmTemplateObj.setUptRegion((getStringListFromMap(mapUPTInfo,IRMUPTConstants.Attributes.UPT_REGION.getSelect(context))));
			
    	}
    	 return irmTemplateObj;
    }
    /**
	 * @param context
	 * @return
	 * @throws FrameworkException
	 */
	public String getPreferredRouteInstruction(Context context,IRMTemplate irmTemplateObj) throws FrameworkException {
		return irmTemplateObj.getUptRouteInstruction();
	}
	/**
	 * @param context
	 * @return
	 * @throws FrameworkException
	 */
	public String getPreferredRouteAction(Context context,IRMTemplate irmTemplateObj) throws FrameworkException {
		return irmTemplateObj.getUptRouteAction();
		
	}
	
	  /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getPreferredRouteTaskRecipientMembers(Context context,IRMTemplate irmTemplateObj) throws FrameworkException {
        return String.join("|",irmTemplateObj.getUptRouteTaskRecipientsMember());
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public List<IRMMember> getPreferredRouteTaskRecipientMemberList(Context context,IRMTemplate irmTemplateObj) throws FrameworkException {
        List<IRMMember> resultList = new ArrayList<>();
        String memberString = getPreferredRouteTaskRecipientMembers(context, irmTemplateObj);
        if (UIUtil.isNotNullAndNotEmpty(memberString)) {
            StringList memberList = StringUtil.split(memberString, "|");
            Map<Object, Object> personMap;
            StringList memberRoleList;
            String fullName;
            for (String memberRole : memberList) {
                memberRoleList = StringUtil.split(memberRole, "~");
                if (memberRoleList.size() > 1) {
                    personMap = getPersonObjectDetails(context, memberRoleList.get(2).trim());
                    if (null != personMap && !personMap.isEmpty()) {
                       
                         fullName = ((String) personMap.get(pgV3Constants.SELECT_ATTRIBUTE_LASTNAME)).concat("; ").concat((String) personMap.get(pgV3Constants.SELECT_ATTRIBUTE_FIRSTNAME));
                         resultList.add(new IRMMember(memberRoleList.get(2).trim(), (String) personMap.get(DomainConstants.SELECT_NAME), fullName, memberRoleList.get(1).trim()));
                       
                    }
                }
            }
        }
        return resultList;
    }
    public Map<Object, Object> getPersonObjectDetails(Context context,String strId) throws FrameworkException {
    	DomainObject domPerson = DomainObject.newInstance(context,strId);
	    Map mapUPTInfo = domPerson.getInfo(context, StringList.create(DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME, pgV3Constants.SELECT_ATTRIBUTE_FIRSTNAME, pgV3Constants.SELECT_ATTRIBUTE_LASTNAME));// objectSelects
	    return mapUPTInfo;
	}

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public List<IRMUserGroup> getPreferredRouteTaskRecipientUserGroupList(Context context,IRMTemplate irmTemplateObj) throws FrameworkException {
        List<IRMUserGroup> resultList = new ArrayList<>();
        String userGroupsString = getPreferredRouteTaskRecipientUserGroups(context,irmTemplateObj);
        if (UIUtil.isNotNullAndNotEmpty(userGroupsString)) {
            StringList userGroupsList = StringUtil.split(userGroupsString, "|");
            String objectOid;
            StringList groupList;
            for (String group : userGroupsList) {
            	groupList = StringUtil.split(group, "~");
                if (groupList.size() > 1) {
               
                  resultList.add(new IRMUserGroup(groupList.get(1).trim(), groupList.get(0).trim()));
                }
            }
        }
        return resultList;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getPreferredRouteTaskRecipientUserGroups(Context context,IRMTemplate irmTemplateObj) throws FrameworkException {
    	return String.join("|",irmTemplateObj.getUptRouteTaskRecipientsGroup());
    }

  

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public boolean isPreferredRouteTaskRecipientUserGroup(Context context,IRMTemplate irmTemplateObj) throws FrameworkException {
        String value = String.join("|",irmTemplateObj.getUptRouteTaskRecipientsGroup());
        return (UIUtil.isNotNullAndNotEmpty(value)) ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getIsPreferredRouteTaskRecipientMembers(Context context,IRMTemplate irmTemplateObj) throws FrameworkException {
        String value = PropertyUtil.getAdminProperty(context, // context
                DomainConstants.TYPE_PERSON, // type
                context.getUser(), // user
                PreferenceConstants.Preferences.IS_IRM_PREFERRED_ROUTE_TASK_RECIPIENTS_MEMBERS.get()); // preference property name
        return UIUtil.isNotNullAndNotEmpty(value) ? value : DomainConstants.EMPTY_STRING;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public boolean isPreferredRouteTaskRecipientMembers(Context context,IRMTemplate irmTemplateObj) throws FrameworkException {
        String value = String.join("|",irmTemplateObj.getUptRouteTaskRecipientsMember());
        return (UIUtil.isNotNullAndNotEmpty(value)) ? Boolean.TRUE : Boolean.FALSE;
    }

    public void createRouteBasedOnIRMApprovalPreference(Context context, String[] args ) throws Exception {
		try {
			 String objectOid = args[0];
			
			if (UIUtil.isNotNullAndNotEmpty(objectOid)) {
				DomainObject domainObject = DomainObject.newInstance(context, objectOid);
				if (domainObject.isKindOf(context, PreferenceConstants.Type.TYPE_IRM_DOCUMENT.getName(context))) {
					IRMTemplate objIRM = setIRMRouteAttributes(context,objectOid);
					IRMRouteDetails approvalPreference = new IRMRouteDetails(context,objIRM);
					boolean isUserGroupOn = approvalPreference.isPreferredRouteTaskRecipientUserGroups();
					boolean isMemberOn = approvalPreference.isPreferredRouteTaskRecipientMembers();
					String policy = domainObject.getInfo(context, DomainConstants.SELECT_POLICY);
					String policySignatureReference = PropertyUtil.getSchemaProperty(context, "policy_pgPKGSignatureReferenceDoc");
					if ((isMemberOn || isUserGroupOn) && policySignatureReference.equalsIgnoreCase(policy)) {
						Instant startTime = Instant.now();
						IRMSimpleRoute simpleRoute = new IRMSimpleRoute.Creator(context, approvalPreference, objectOid).create();
						Instant endTime = Instant.now();
						Duration duration = Duration.between(startTime, endTime);
						logger.info("User Preferences - IRM Route Creation - took|" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
						if (!simpleRoute.isCreated()) {
							String errorMessage = simpleRoute.getErrorMessage();
							logger.log(Level.WARNING, "Route Creation Failed for Object ID: " + objectOid.concat(" with error:").concat(errorMessage));
						}
					}
				}
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Error: " + e);
		}
	}
    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public IRMTemplate setIRMRouteAttributes(Context context,String strDocId) throws Exception {
    	StringList selectList = new StringList(5);
		selectList.add(IRMUPTConstants.Attributes.UPT_ROUTE_INSTRUCTION.getSelect(context));
	    selectList.add(IRMUPTConstants.Attributes.UPT_ROUTE_ACTION.getSelect(context));
	    selectList.add(IRMUPTConstants.Attributes.UPT_TASK_RECIPIENT_MEMBER.getSelect(context));
	    selectList.add(IRMUPTConstants.Attributes.UPT_TASK_RECIPIENT_GROUP.getSelect(context));
	   Map mapUPTInfo = fetchValuesFromIRMPreference(context, strDocId,selectList);
		IRMTemplate irmTemplateObj = new IRMTemplate();
		if(null != mapUPTInfo) {
			 irmTemplateObj.setUptRouteInstruction((getStringListFromMap(mapUPTInfo,IRMUPTConstants.Attributes.UPT_ROUTE_INSTRUCTION.getSelect(context))).get(0));
	       irmTemplateObj.setUptRouteAction((getStringListFromMap(mapUPTInfo,IRMUPTConstants.Attributes.UPT_ROUTE_ACTION.getSelect(context))).get(0));
		   
	       irmTemplateObj.setUptRouteTaskRecipientsMember(getStringListFromMap(mapUPTInfo, IRMUPTConstants.Attributes.UPT_TASK_RECIPIENT_MEMBER.getSelect(context))) ; 
	       irmTemplateObj.setUptRouteTaskRecipientsGroup(getStringListFromMap(mapUPTInfo, IRMUPTConstants.Attributes.UPT_TASK_RECIPIENT_GROUP.getSelect(context))) ; 
		   
	   }
    	 return irmTemplateObj;
       
    }
    
   
       /**
        * @param context
        * @param objectOid
        * @return
        * @throws FrameworkException
        */
       public boolean isPersonEBPUser(Context context, String objectOid) throws FrameworkException {
           boolean isEBP = false;
           if (UIUtil.isNotNullAndNotEmpty(objectOid)) {
               String attributeSecurityEmployeeTypeSelect = PreferenceConstants.Attribute.ATTRIBUTE_PG_SECURITY_EMPLOYEE_TYPE.getSelect(context);
               DomainObject domainObject = DomainObject.newInstance(context, objectOid);
               String employeeTypeValue = domainObject.getInfo(context, attributeSecurityEmployeeTypeSelect);
               if (UIUtil.isNotNullAndNotEmpty(employeeTypeValue) && pgV3Constants.KEY_EBP.equalsIgnoreCase(employeeTypeValue)) {
                   isEBP = true;
               }
           } else {
               logger.log(Level.WARNING, "EBP User check Person object ID is null");
           }
           return isEBP;
       }
       /**
        * @param context
        * @param domainObject
        * @param IRMTemplate
        * @throws Exception
        */
       public void connectSecurityClassification(Context context,DomainObject domDocId, IRMTemplate objIRM ) throws Exception {
    	  
    	   String strClassification = objIRM.getUptClassification();
    	   String strBusinessUse = null;
    	   if (UIUtil.isNotNullAndNotEmpty(strClassification)) {
	    	   if ((IRMUPTConstants.Basic.STRING_BUSINESS_USE.get()).equalsIgnoreCase(strClassification)) {
	    		   strBusinessUse = String.join("|",objIRM.getUptBusinessUseIPClass());
	    	   } else {
	    		   strBusinessUse = String.join("|",objIRM.getUptHiRestrictedIPClass());
	    	   }
	    	   applyIPSecurityClass(context, domDocId, strBusinessUse);
    	   }
       }
       /**
        * @param context
        * @param domainObject
        * @param preferenceIPSecurity
        * @throws FrameworkException
        */
       public void applyIPSecurityClass(Context context, DomainObject domainObject, String preferenceIPSecurity) throws FrameworkException {
           if (UIUtil.isNotNullAndNotEmpty(preferenceIPSecurity)) {
               logger.info("_______________ - Attach IP Security Start - _______________");
               refreshRelationshipWithCommaSeparated(context,
                       domainObject,
                       preferenceIPSecurity,
                       getRelatedSecurityControl(context, domainObject),
                       PropertyUtil.getSchemaProperty(context, "relationship_ProtectedItem"),
                       false,
                       false);
               logger.info("_______________ - Attach IP Security End - _______________");
           }
       }
       /**
        * @param context
        * @param domainObject
        * @return
        * @throws FrameworkException
        */
       public MapList getRelatedSecurityControl(Context context, DomainObject domainObject) throws FrameworkException {
           StringList objectSelects = new StringList();
           objectSelects.add(DomainConstants.SELECT_ID);
           objectSelects.add(PreferenceConstants.Basic.PHYSICAL_ID.get());
           StringList relSelects = new StringList();
           relSelects.add(DomainRelationship.SELECT_RELATIONSHIP_ID);
           MapList relatedObjects = domainObject.getRelatedObjects(context,
                   PropertyUtil.getSchemaProperty(context, "relationship_ProtectedItem"),
                   PreferenceConstants.Type.TYPE_IP_CONTROL.getName(context),
                   objectSelects,
                   relSelects,
                   true,
                   false,
                   (short) 1,
                   DomainConstants.EMPTY_STRING,
                   DomainConstants.EMPTY_STRING,
                   0);

           return relatedObjects;
       }

       /**
        * @param context
        * @param domainObject
        * @param materialFunctions
        * @throws FrameworkException
        */
       public void applyRegion(Context context, String strRegion, DomainObject domainObject) throws FrameworkException {
           try {
               if (UIUtil.isNotNullAndNotEmpty(strRegion)) {
        		   getRegionToConnect(context, domainObject, strRegion);
                  
               }
           } catch (FrameworkException e) {
               logger.log(Level.WARNING, "Error Connecting Material Function: " + e);
               throw e;
           }
       }
       /**
        * @param context
        * @param domainObject
        * @param materialFunctions
        * @return
        * @throws FrameworkException
        */
       public void getRegionToConnect(Context context, DomainObject domainObject, String strRegion) throws FrameworkException {
            if (UIUtil.isNotNullAndNotEmpty(strRegion)) {
        	   refreshRelationshipWithCommaSeparated(context,
                       domainObject,
                       strRegion,
                       getRelatedRegion(context, domainObject),
                       pgV3Constants.RELATIONSHIP_REGIONOWNS,
                       false,
                       true);
           }
          
       }
    
       /**
        * @param context
        * @param domainObject
        * @return
        * @throws FrameworkException
        */
       public MapList getRelatedRegion(Context context, DomainObject domainObject) throws FrameworkException {
           StringList objectSelects = new StringList();
           objectSelects.add(DomainConstants.SELECT_ID);
           objectSelects.add(PreferenceConstants.Basic.PHYSICAL_ID.get());
           StringList relSelects = new StringList();
           relSelects.add(DomainRelationship.SELECT_RELATIONSHIP_ID);
           MapList relatedObjects = domainObject.getRelatedObjects(context,
        		   pgV3Constants.RELATIONSHIP_REGIONOWNS,
                   "*",
                   objectSelects,
                   relSelects,
                   true,
                   false,
                   (short) 1,
                   DomainConstants.EMPTY_STRING,
                   DomainConstants.EMPTY_STRING,
                   0);

           return relatedObjects;
       }
       

       /**
        * @param context
        * @param domainObject
        * @param preferenceValue
        * @param relatedList
        * @param relationshipName
        * @param side
        * @param updateObjectNameOnAttribute
        * @throws FrameworkException
        */
       public void refreshRelationshipWithCommaSeparated(Context context, DomainObject domainObject, String preferenceValue, MapList relatedList, String relationshipName, boolean side, boolean updateObjectNameOnAttribute) throws FrameworkException {
           if (UIUtil.isNotNullAndNotEmpty(preferenceValue)) {
               StringList preferenceValueList = StringUtil.split(preferenceValue, "|");
               logger.log(Level.INFO, "Preferences List: {0}", preferenceValueList);
               List<String> toDisconnectList = new ArrayList<>();
               List<String> alreadyConnectedList = new ArrayList<>();
               if (null != relatedList && !relatedList.isEmpty()) {
                   Map<Object, Object> objectMap;
                   String id;
                   String relId;
                   Iterator iterator = relatedList.iterator();
                   while (iterator.hasNext()) {
                       objectMap = (Map<Object, Object>) iterator.next();
                       id = (String) objectMap.get(DomainConstants.SELECT_ID);
                       relId = (String) objectMap.get(DomainRelationship.SELECT_RELATIONSHIP_ID);
                       if (!preferenceValueList.contains(id)) {
                           toDisconnectList.add(relId);
                       } else {
                           alreadyConnectedList.add(id);
                       }
                   }
               }
               logger.log(Level.INFO, "Connected List: {0}", alreadyConnectedList);
               List<String> toConnectList = new ArrayList<>();
               for (String preferenceBusinessUseID : preferenceValueList) {
                   if (!alreadyConnectedList.contains(preferenceBusinessUseID)) {
                       toConnectList.add(preferenceBusinessUseID);
                   }
               }
               logger.log(Level.INFO, "ToDisconnect List: {0}", toDisconnectList);
               if (null != toDisconnectList && !toDisconnectList.isEmpty()) {
                   String[] toDisconnectArr = toDisconnectList.toArray(String[]::new);
                   DomainRelationship.disconnect(context, toDisconnectArr);
               }
               logger.log(Level.INFO, "ToConnect List: {0}", toConnectList);
               String[] toConnectArr = toConnectList.toArray(String[]::new);
               if (toConnectArr.length > 0) {
                   DomainRelationship.connect(context, domainObject, relationshipName, side, toConnectArr);
                   if (updateObjectNameOnAttribute) {// in case of material function. update object name on attribute
                       updateAttribute(context, domainObject, toConnectArr);
                   }
               }

           }
          
           
       }
           /**
            * @param context
            * @param domainObject
            * @param toConnectArr
            * @throws FrameworkException
            */
           public void updateAttribute(Context context, DomainObject domainObject, String[] toConnectArr) throws FrameworkException {
               MapList objectList = DomainObject.getInfo(context, toConnectArr, StringList.create(DomainConstants.SELECT_NAME));
               List<String> nameList = new ArrayList<>();
               if (null != objectList && !objectList.isEmpty()) {
                   for (Object object : objectList) {
                       nameList.add((String) ((Map<Object, Object>) object).get(DomainConstants.SELECT_NAME));
                   }
               }
               if (!nameList.isEmpty()) { // update pgMaterialFunctionGlobal attribute (multi-value)
                   domainObject.setAttributeValue(context, PropertyUtil.getSchemaProperty(context, "attribute_pgRegion"), String.join("", nameList));
               }
           }
           
        
           /**
            * @param context
            * @param docOID
            * @return
            * @throws Exception
            */
           public Map<String, String> getShareWithMemberFlatMap(Context context, String docOID) throws Exception {
               Map<String, String> personMap = new HashMap<>(); // physical, name
               if (UIUtil.isNotNullAndNotEmpty(docOID)) {
                   String selectUPTPhysicalID = IRMUPTConstants.Attributes.UPT_PID.getSelect(context); // physical id of upt which is store as an attribute on irm doc.
                   String selectShareWithMember = IRMUPTConstants.Attributes.UPT_SHARE_WITH_MEMBERS.getSelect(context);
                   DomainObject domainObject = DomainObject.newInstance(context, docOID);
                   String uptPhysicalOid = domainObject.getInfo(context, selectUPTPhysicalID);
                   if (UIUtil.isNotNullAndNotEmpty(uptPhysicalOid)) {
                       MapList infoList = DomainObject.getInfo(context, new String[]{uptPhysicalOid}, StringList.create(selectShareWithMember));
                       if (null != infoList && !infoList.isEmpty()) {
                           Map<Object, Object> objectMap = (Map<Object, Object>) infoList.get(0);
                           if (null != objectMap && !objectMap.isEmpty()) {
                               StringList personPhysicalIDs = getStringListFromMap(objectMap, selectShareWithMember);
                               if (null != personPhysicalIDs && !personPhysicalIDs.isEmpty() && UIUtil.isNotNullAndNotEmpty(personPhysicalIDs.get(0))) {
                                   MapList personInfoList = DomainObject.getInfo(context, personPhysicalIDs.toStringArray(), StringList.create(DomainConstants.SELECT_NAME, DomainConstants.SELECT_PHYSICAL_ID, DomainConstants.SELECT_ID));
                                   if (null != personInfoList && !personInfoList.isEmpty()) {
                                       Iterator iterator = personInfoList.iterator();
                                       Map<Object, Object> tempMap;
                                       while (iterator.hasNext()) {
                                           tempMap = (Map<Object, Object>) iterator.next();
                                           personMap.put((String) tempMap.get(DomainConstants.SELECT_PHYSICAL_ID), (String) tempMap.get(DomainConstants.SELECT_NAME));
                                       }
                                   } else {
                                       logger.log(Level.WARNING, "UPT share with member info list is blank");
                                   }
                               } else {
                                   logger.log(Level.WARNING, "UPT get share with member physical id (multi-val StringList) is blank");
                               }
                           } else {
                               logger.log(Level.WARNING, "UPT get share with member physical id is blank");
                           }

                       } else {
                           logger.log(Level.WARNING, "UPT get share with member info map is empty");
                       }
                   } else {
                       logger.log(Level.WARNING, "UPT Physical ID is blank on IRM Document object");
                   }
               }
               return personMap;
           }

           private StringList getStringListFromMap(Map<Object, Object> objectMap, String select) throws Exception {
               StringList objectList = new StringList();
               Object result = objectMap.get(select);
                 if (null != result ) {
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

           /**
            * @param context
            * @param domainObject
            * @param shareTemplateWithValue
            * @throws Exception
            */
           public void applyShareTemplateWith(Context context, DomainObject domainObject, String shareTemplateWithValue) throws Exception {
               try {
                   if (UIUtil.isNotNullAndNotEmpty(shareTemplateWithValue)) {
                       String templateID = domainObject.getInfo(context, DomainConstants.SELECT_ID);
                       String[] memberList = shareTemplateWithValue.split("\\|");
                       MapList infoList = DomainObject.getInfo(context, memberList, new StringList(DomainConstants.SELECT_NAME));
                       Map infoMap;
                       for (int i = 0; i < infoList.size(); i++) {
                           infoMap = (Map) infoList.get(i);
                           StringBuilder person = new StringBuilder();
                           person.append((String) infoMap.get(DomainConstants.SELECT_NAME));
                           person.append(IRMUPTConstants.Basic.SYMBOL_PRJ_SUFFIX.get());
                           DomainAccess.createObjectOwnership(context,
                                   templateID,
                                   IRMUPTConstants.Basic.SYMBOL_HYPHEN.get(),
                                   person.toString(),
                                   UserPreferenceTemplateConstants.Basic.TEMPLATE_SHARE_WITH_BASIC_ACCESS.get(),
                                   DomainAccess.COMMENT_MULTIPLE_OWNERSHIP);
                       }
                   } else {
                       logger.log(Level.WARNING, "Incoming (Share Template With) value is empty");
                   }
               } catch (Exception e) {
                   logger.log(Level.SEVERE, "Exception occurred while applying (share template with) on incoming UPT object:" + e);
                   throw e;
               }
           }

	/**
	 * @Modified by DSM (Sogeti) for 2022x-04 DeC CW - Defect 55525,55490,55586,55587,55598
	 * @param context
	 * @param type
	 * @param uptPolicy
	 * @return
	 * @throws MatrixException
	 */
	public boolean allowdPolicyForType(Context context, String type, String uptPolicy) throws MatrixException {
		boolean allowPolicy=false;
		MapList policies = mxType.getPolicies(context, type, false);
		Map policy = null;
		String policyName = DomainConstants.EMPTY_STRING;
		if (!policies.isEmpty() && UIUtil.isNotNullAndNotEmpty(uptPolicy)) {
			Iterator iterator = policies.iterator();
			while (iterator.hasNext()) {
				policy = (Map) iterator.next();
				policyName = (String) policy.get(DomainConstants.SELECT_NAME);
				if(policyName.equals(uptPolicy)){
					allowPolicy=true;
				}
			}
		}
		logger.log(Level.INFO, "allowPolicy@@: {0}", allowPolicy);
		return allowPolicy;
	}

	/**
	 * 
	 * @param context
	 * @param args
	 * @throws Exception
	 */
           public void applySharingWithMemberOnIRMDoc(Context context, String[] args) throws Exception {
               boolean isCtxPushed = Boolean.FALSE;
               try {
                   String docOID = args[0];
			StringList seletable = new StringList(2);
			seletable.add(DomainConstants.SELECT_TYPE);
			seletable.add(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_PHYSICAL_ID.getSelect(context));
                   if (UIUtil.isNotNullAndNotEmpty(docOID)) {
                       DomainObject domainObject = DomainObject.newInstance(context, docOID);
				//
				Map info = domainObject.getInfo(context, seletable);
				String uptId= (String) info.get(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_PHYSICAL_ID.getSelect(context));
				String type= (String) info.get(DomainConstants.SELECT_TYPE);
				String uptPolicy = DomainObject.newInstance(context, uptId).getInfo(context, IRMUPTConstants.Attributes.UPT_POLICY.getSelect(context));
				logger.log(Level.INFO, "uptPolicy: " + uptPolicy+" @:Type: "+type);
				if(IRMUPTConstants.Basic.UPT_POLICY_SIGNATURE.get().equalsIgnoreCase(uptPolicy)) {
					uptPolicy=IRMUPTConstants.Basic.POLICY_SIGNATURE.get();
				} else if(IRMUPTConstants.Basic.UPT_POLICY_SELF.get().equalsIgnoreCase(uptPolicy)) {
					uptPolicy=IRMUPTConstants.Basic.POLICY_SELF.get();
				}
				boolean allowedPolicy = allowdPolicyForType(context, type, uptPolicy);

				//
				if(allowedPolicy) {
                       if (domainObject.isKindOf(context, PreferenceConstants.Type.TYPE_IRM_DOCUMENT.getName(context))) {
                    	   logger.log(Level.INFO, "Context user before Getting Sharing Members from Preference: " + context.getUser());
                           StringList selectList = new StringList(1);
                           selectList.add(IRMUPTConstants.Attributes.UPT_SHARE_WITH_MEMBERS.getName(context));
                           Map<String, String> memberFlatMap = getShareWithMemberFlatMap(context, docOID);
                           if (null != memberFlatMap) {
                               ContextUtil.pushContext(context, PropertyUtil.getSchemaProperty(context, "person_UserAgent"), DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
                               isCtxPushed = Boolean.TRUE;
                               boolean isEBP;
                               String access;
                               String personName;
                               List<String> preferenceMemberList = new ArrayList<>();
                               List<String> existingMemberList = getAccessMemberNameList(context, docOID);
                               logger.log(Level.INFO, "Existing Sharing Members List: {0}", existingMemberList);
                               List<String> deleteOwnershipList = new ArrayList<>();
                               for (String existingMember : existingMemberList) {
                                   if (!preferenceMemberList.contains(existingMember)) {
                                       deleteOwnershipList.add(existingMember);
                                   } else {
                                       preferenceMemberList.add(existingMember);
                                   }
                               }
                               logger.log(Level.INFO, "Delete Sharing Members List: {0}", deleteOwnershipList);

                               // delete member ownership which is not present in preferences.
                               for (String memberName : deleteOwnershipList) {
                                   DomainAccess.deleteObjectOwnership(context, docOID, DomainConstants.EMPTY_STRING, memberName + "_PRJ", DomainAccess.COMMENT_MULTIPLE_OWNERSHIP);
                               }
                               for (Map.Entry<String, String> entry : memberFlatMap.entrySet()) {
                                   isEBP = isPersonEBPUser(context, entry.getKey());
                                   access = (isEBP) ? PreferenceConstants.Basic.IRM_SHARING_MEMBER_ACCESSES_FOR_EBP.get() : PreferenceConstants.Basic.FULL.get();
                                   personName = entry.getValue();
                                   personName += "_PRJ";
                                   DomainAccess.createObjectOwnership(context, docOID, "-", personName, access, DomainAccess.COMMENT_MULTIPLE_OWNERSHIP);
                               }
                               ContextUtil.popContext(context);
                               isCtxPushed = Boolean.FALSE;
                           }
                       }
                   }
			}
               } catch (Exception e) {
                   logger.log(Level.INFO, "Error: " + e);
               } finally {
                   if (isCtxPushed) {
                       ContextUtil.popContext(context);
                   }
               }
           }

           public List<String> getAccessMemberNameList(Context context, String objectOid) throws Exception {
               MapList summaryList = DomainAccess.getAccessSummaryList(context, objectOid);
               logger.log(Level.INFO, "Access Member MapList: " + summaryList);
               List<String> accessMemberList = new ArrayList<>();
               DomainObject domainObject = DomainObject.newInstance(context, objectOid);
               String owner = domainObject.getInfo(context, DomainConstants.SELECT_OWNER);
               if (null != summaryList && !summaryList.isEmpty()) {
                   Iterator iterator = summaryList.iterator();
                   Map<Object, Object> accessMap;
                   String userName;
                   while (iterator.hasNext()) {
                       accessMap = (Map<Object, Object>) iterator.next();
                       userName = (String) accessMap.get("username");
                       if (UIUtil.isNotNullAndNotEmpty(userName) && !userName.equalsIgnoreCase(owner)) {
                           accessMemberList.add(userName);
                       }
                   }
               }
               logger.log(Level.INFO, "Access Member User Names: " + accessMemberList);
               return accessMemberList;
           }

       }


    	   
    	   

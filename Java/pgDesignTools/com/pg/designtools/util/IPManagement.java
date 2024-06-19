package com.pg.designtools.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.dassault_systemes.enovia.gls.common.model.PRSPContext;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.exportcontrol.ExportControlConstants;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.apps.library.LibraryCentralConstants;
import com.matrixone.vplmintegration.util.VPLMIntegTraceUtil;
import com.pg.designtools.datamanagement.DataConstants;

import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.Pattern;
import matrix.util.SelectList;
import matrix.util.StringList;

public class IPManagement {
	
    
    public static final String STR_PROGNAME_SECURITY_UTIL = "pgIPSecurityCommonUtil";
    public static final String STR_METHODNAME_IPCONTROLCLASS_USER = "includeSecurityCategoryClassification";
    public static final String STR_ALWAYS_HIR = "AlwaysHighlyRestricted";
    public static final String STR_ALWAYS_R = "AlwaysRestricted";
    public static final String STR_DEFAULT_HIR = "DefaultHighlyRestricted";
    public static final String STR_DEFAULT_R = "DefaultRestricted";
    public static final String STR_ANY_CLASSIFICATION="AnyIPClassification";
    public static final String STR_PROGNAME_CATIA_INTEGRATION="pgDSOCATIAIntegration";
    public static final String STR_METHODNAME_GET_ECPART_INFO="getECPartInfoFromCAD";
    public static final String STR_METHODNAME_GET_PROXY_INFO="getProxyObject";
    public static final String PREF_BU="preference_BusinessUseClass";
    public static final String PREF_HIR="preference_HighlyRestrictedClass";
    public static final String PREF_DEFAULT_EXPLORATION="preference_DefaultIPClassForExploration";
    public static final String PREF_DESIGN_FOR="preference_DIDesignFor";
    public static final String PREF_DI_MATURITY_STATUS="preference_PackagingPartPhase";
    public static final String STR_PREFERENCE="preference";
    public static final String STR_CONFIG_PREFERENCE="configPreference";
    public static final String STR_BUSINESS_USE="Business Use";
    public static final String STR_HIGHLY_RESTRICTED="Highly Restricted";
    public static final String STR_RESTRICTED="Restricted";
    public static final String STR_PROPERTY="property[";
    public static final String STR_VALUE="].value";
  //Added for Get Security Classification API call - JIRA (CAD) DSM15X4-94 ENDS    

    public IPManagement(Context context) {
		PRSPContext.set(context);
	}
    
	/**
	 * This is a utility method to get Ip Class connected to target OID
	 * Shifted from pgDSOCATIAIntegration JPO
	 * @param context
	 * @param target DO
	 * @return MapList
	 * @throws FrameworkException 
	 */
	public MapList getConnectedIPControlClasses(Context context, DomainObject targetDO) throws FrameworkException  {
		CommonDocumentHandler handler=new CommonDocumentHandler();
		MapList parentClasses = null;
		try {
		//DT18X3-41: Pushed the context to get the Security Proxy object details for non-owner user
		handler.pushContext();
		
		SelectList selectStmts      = new SelectList(2);
		selectStmts.addElement(DomainConstants.SELECT_ID);
		selectStmts.addElement(DomainConstants.SELECT_NAME);
		selectStmts.addElement(DomainConstants.SELECT_TYPE);
		
		SelectList relSelects      = new SelectList(1);
		relSelects.addElement(DomainRelationship.SELECT_ID);
		
		parentClasses = targetDO.getRelatedObjects(context,
				DomainConstants.RELATIONSHIP_PROTECTED_ITEM,  // relationship pattern
				DomainConstants.QUERY_WILDCARD,         // type pattern
				selectStmts,        // Object selects
				relSelects,               // relationship selects
				true,               // from
				false,              // to
				(short)1,           // expand level
				null,               // object where
				null,               // relationship where
				0);                 // limit
		
		//DT18X3-41: Popped the context
		}catch(FrameworkException fe) {
			VPLMIntegTraceUtil.trace(context, fe.getMessage());
		}finally {
			handler.popContext();
		}
		return parentClasses;
	}
	
	/**
	 * This is a utility method to get the list of types defined for IP Classifications in the emxExportControl_ECL.properties page file.
	 * @param context
	 * @param boolean
	 * @return Map
	 */
	public Map<String,StringList> getTypeListFromPageConfig(Context context, boolean includeDefaultEntry) {
		Map<String,StringList> returnTypeMap = new HashMap<>();
		try {
			String sAlwaysHighlyRestricted = EnoviaResourceBundle.getProperty(context, "pgExportControl.IPClassification.AlwaysHighlyRestricted");
			String sAlwaysRestricted = EnoviaResourceBundle.getProperty(context, "pgExportControl.IPClassification.AlwaysRestricted");
			StringList slAlwaysHighlyRestrictedList = StringUtil.split(sAlwaysHighlyRestricted, "|");
			StringList slAlwaysRestrictedList = StringUtil.split(sAlwaysRestricted, "|");
			returnTypeMap.put(STR_ALWAYS_HIR, slAlwaysHighlyRestrictedList);
			returnTypeMap.put(STR_ALWAYS_R, slAlwaysRestrictedList);
			
			String sAnyIPClassification = EnoviaResourceBundle.getProperty(context, "pgExportControl.IPClassification.AnyClassification");
			StringList sAnyClassificationList = StringUtil.split(sAnyIPClassification, "|");
			returnTypeMap.put(STR_ANY_CLASSIFICATION, sAnyClassificationList);
			
			if (includeDefaultEntry) {
				String sDefaultHighlyRestricted = EnoviaResourceBundle.getProperty(context, "pgExportControl.IPClassification.DefaultHighlyRestricted");
				String sDefaultRestricted = EnoviaResourceBundle.getProperty(context, "pgExportControl.IPClassification.DefaultRestricted");
				StringList slDefaultHighlyRestrictedList = StringUtil.split(sDefaultHighlyRestricted, "|");
				StringList slDefaultRestrictedList = StringUtil.split(sDefaultRestricted, "|");
				returnTypeMap.put(STR_DEFAULT_HIR, slDefaultHighlyRestrictedList);
				returnTypeMap.put(STR_DEFAULT_R, slDefaultRestrictedList);
			}
		} catch (Exception e) {
			VPLMIntegTraceUtil.trace(context, e.getMessage());
		}
		return returnTypeMap;
	}
    
	/**
	 * This method would be invoked from webservice. It would return the list of IP Classes connected to the object.
	 * @param context
	 * @param String type of object
	 * @param String name of object
	 * @param String rev of object
	 * @return String of connected IP Classes
	 * @throws MatrixException 
	 */
    public String getConnectedIPClasses(Context context, String sType,String sName,String sRev) throws MatrixException {
    	
    	String sIPClassNameList="";
    	
    	if(UIUtil.isNotNullAndNotEmpty(sType) && UIUtil.isNotNullAndNotEmpty(sName) && UIUtil.isNotNullAndNotEmpty(sRev)) {
    		
    		StringList slSelects=new StringList(2);
    		slSelects.addElement(DomainConstants.SELECT_ID);
    		slSelects.addElement(DataConstants.SELECT_PHYSICALID);
    		
    		String sObjectId="";
    		String sPhysicalId="";
    		
    		MapList mlObject=findObject(context,sType,sName,sRev,slSelects);

			if(!mlObject.isEmpty())
			{
				Map<String,String> mpObject=(Map<String,String>) mlObject.get(0);
				sObjectId = mpObject.get(DomainConstants.SELECT_ID);
				sPhysicalId=mpObject.get(DataConstants.SELECT_PHYSICALID);
			}
   
        	if(UIUtil.isNotNullAndNotEmpty(sObjectId)) {
	    		String sIPClassConnectedObjId=getIPClassConnectedObjectId(context,sType,sObjectId,sPhysicalId);
	    			    		
	    		//when we get the object id of the object who has IP Class connected, then invoke the method to get the IP Class from there
	    		if(UIUtil.isNotNullAndNotEmpty(sIPClassConnectedObjId)) {
		    		DomainObject doTargetObj=DomainObject.newInstance(context,sIPClassConnectedObjId);
		    		MapList mlIPClassList=getConnectedIPControlClasses(context, doTargetObj);

		    		sIPClassNameList=getIPClassNameList(mlIPClassList);
	    		}
        	}
    	}
    	return sIPClassNameList;
    }

    /**
	 * This method would search for the object who will have the IP Classes connected, depending upon the type of the current object.
	 * @param context
	 * @param String type of object
	 * @param String objectId of object
	 * @param String physicalid of object
	 * @return String objectId of the IP Class connected object
	 * @throws MatrixException 
	 */
	private String getIPClassConnectedObjectId(Context context, String sType,String sObjectId, String sPhysicalId) throws MatrixException {
		String sIPClassConnectedObjId="";
		
		if(DataConstants.TYPE_VPMREFERENCE.equals(sType)) {
			sIPClassConnectedObjId=getIPClassConnectedObjFromVPMRef(context,sObjectId,sPhysicalId);
		}
		else if(DataConstants.TYPE_FEM.equals(sType) || DataConstants.TYPE_SIM_SHAPE.equals(sType)) {
			
			//1. get the connected VPMReference object
			Pattern relPattern=new Pattern(DataConstants.REL_VPM_REPINSTANCE);
			StringList slObjSelects=new StringList(DomainConstants.SELECT_ID);
			slObjSelects.addElement(DataConstants.SELECT_PHYSICALID);
			
			Map<String,String>mpVPMReference=getRelatedVPMReferenceObjectId(context, sObjectId, relPattern, slObjSelects, null, false);//VPMReference is at from side
			
			String sVPMReferenceObjId=mpVPMReference.get(DomainConstants.SELECT_ID);
			String sVPMReferencePhyId=mpVPMReference.get(DataConstants.SELECT_PHYSICALID);
			if(UIUtil.isNotNullAndNotEmpty(sVPMReferenceObjId))
				sIPClassConnectedObjId=getIPClassConnectedObjFromVPMRef(context, sVPMReferenceObjId, sVPMReferencePhyId);
		}
		else if(DataConstants.TYPE_DSC_MATREF_REF_CORE.equals(sType) || DataConstants.TYPE_MACRO_LIBRARY_VBA.equals(sType)
				|| DataConstants.TYPE_SIMULATION_DOC_VERSIONED.equals(sType) || DataConstants.TYPE_SIMULATION_DOC_NONVERSIONED.equals(sType)
				|| DataConstants.TYPE_REQUIREMENT.equals(sType) || DataConstants.TYPE_REQUIREMENT_SPECIFICATION.equals(sType) 
				|| DataConstants.TYPE_DESIGN_SIGHT.equals(sType)) {
			sIPClassConnectedObjId=sObjectId;
		}
		return sIPClassConnectedObjId;
	}

	/**
	 * This method would search for either EC Part/proxy object, who will have the IP Classes connected, depending upon te VPMReference object passed
	 * @param context
	 * @param String objectId of object
	 * @param String physicalid of object
	 * @return String objectId of the IP Class connected object
	 * @throws MatrixException 
	 */
	private String getIPClassConnectedObjFromVPMRef(Context context, String sObjectId, String sPhysicalId) throws MatrixException {
		HashMap<String, String>hmParam=new HashMap<>();
		String sIPClassConnectedObjId="";
		
		//1. get the connected EC Part object Id
		hmParam.put("objectId", sObjectId);
		hmParam.put("objSelects", DomainConstants.SELECT_ID);
		Map mpECPartInfo=JPO.invoke(context, STR_PROGNAME_CATIA_INTEGRATION, null, STR_METHODNAME_GET_ECPART_INFO, JPO.packArgs(hmParam), Map.class);
		sIPClassConnectedObjId=(String)mpECPartInfo.get(DomainConstants.SELECT_ID);
		
		//2. If EC Part is not connected, then search for the proxy object
		if(UIUtil.isNullOrEmpty(sIPClassConnectedObjId)) {
			hmParam.put("physicalid", sPhysicalId);
			sIPClassConnectedObjId=JPO.invoke(context, STR_PROGNAME_CATIA_INTEGRATION, null, STR_METHODNAME_GET_PROXY_INFO, JPO.packArgs(hmParam), String.class);
		}
		return sIPClassConnectedObjId;
	}

	/**
	 * This method would find the object depending upon TNR passed
	 * @param context
	 * @param String type of object
	 * @param String name of object
	 * @param String rev of object
	 * @param StringList selectables for object
	 * @return MapList
	 * @throws FrameworkException 
	 */
	public MapList findObject(Context context, String sType,String sName,String sRev,StringList slSelects) throws FrameworkException {
		MapList mlObject;
		mlObject=DomainObject.findObjects(context,
				sType,									//typePattern
				sName,									//namePattern
				sRev,									//revPattern
				DomainConstants.QUERY_WILDCARD,			//ownerPattern
				DomainConstants.QUERY_WILDCARD,         //vaultPattern
				null,                                   //where expression
				true,                                   //expandType
				slSelects);                             //object selectables
		return mlObject;
	}

	/**
	 * This method return the names of IP Class from maplist. The names would be separated using semi colon.
	 * @param MapList of IP Classes
	 * @return String
	 */
	private String getIPClassNameList(MapList mlIPClassList) {
		StringBuilder sbList=new StringBuilder();
		if(!mlIPClassList.isEmpty()) {
			for(int i=0;i<mlIPClassList.size();i++) {
				Map<String,String> mpIPClass=(Map<String, String>) mlIPClassList.get(i);
				if(UIUtil.isNullOrEmpty(sbList.toString()))
					sbList.append(mpIPClass.get(DomainConstants.SELECT_NAME));
				else
					sbList.append(";").append(mpIPClass.get(DomainConstants.SELECT_NAME));
			}
		}
		return sbList.toString();
	}
	
	/**
	 * This method would be invoked from webservice. 
	 * It would compare the two list of IP Classes and decide which should be the final list to be attached to the resulting object.
	 * @param context
	 * @param String type of object
	 * @param String IP Class list 1
	 * @param String IP Class list 2
	 * @return String
	 * @throws FrameworkException 
	 */
	public String compareIPClassList(Context context,String strType,String sOrgDataIPClassList,String sRepDataIPClassList) throws FrameworkException {
		String sFinalIPClassList="";
		if(UIUtil.isNullOrEmpty(sOrgDataIPClassList) && UIUtil.isNotNullAndNotEmpty(sRepDataIPClassList))
			sFinalIPClassList=sRepDataIPClassList;
		else if(UIUtil.isNotNullAndNotEmpty(sOrgDataIPClassList) && UIUtil.isNullOrEmpty(sRepDataIPClassList))
			sFinalIPClassList=sOrgDataIPClassList;
		else if(UIUtil.isNotNullAndNotEmpty(sOrgDataIPClassList) && UIUtil.isNotNullAndNotEmpty(sRepDataIPClassList)) {
			boolean bOrgHiR=false;
			boolean bOrgBU=false;
			boolean bRepHiR=false;
			boolean bRepBU=false;
			StringList slOrgIPClassList=StringUtil.split(sOrgDataIPClassList, ";");
			StringList slRepIPClassList=StringUtil.split(sRepDataIPClassList, ";");
			
			if(slOrgIPClassList.toString().contains("HiR"))
				bOrgHiR=true;
			else
				bOrgBU=true;
			
			if(slRepIPClassList.toString().contains("HiR"))
				bRepHiR=true;
			else
				bRepBU=true;
			
			if(bOrgBU && (bRepBU || bRepHiR))
				sFinalIPClassList=sRepDataIPClassList;
			else if(bOrgHiR && bRepBU)
				sFinalIPClassList=sOrgDataIPClassList;
			else if(bOrgHiR && bRepHiR)
				sFinalIPClassList=sRepDataIPClassList;
		}
		else if(UIUtil.isNullOrEmpty(sOrgDataIPClassList) && UIUtil.isNullOrEmpty(sRepDataIPClassList)) {
			//get the IP Class from user preference (based on type it would be decided BU or HiR)
			Map<String,String> mpPreference=getPreferenceFromType(context, strType);
			sFinalIPClassList=getUserPreferenceValue(context, mpPreference);
		}
		if(UIUtil.isNullOrEmpty(sFinalIPClassList))
			sFinalIPClassList=DataConstants.DEFAULT_USER_PREFERENCE_NOT_SET;
		return sFinalIPClassList;
	}
	
	/**
	 * This method is moved from pgSyncEBOMDefer_mxJPO. Original method name was setDefaultIPClassesOnCreation()
	 * The logic is broken down into 2 methods: first to get the user preference and second to connect the IP Control classes to object
	 * @param context
	 * @param String type of object
	 * @param String objectId
	 * @return Map
	 * @throws Exception 
	 */
	public Map<String,String> getUserPreferenceFromObject(Context context, String strType, String objectId) throws Exception{
		
		Map<String,String> mpFinalMap=new HashMap<>();
		
		String strClassSelect = "to["+DomainConstants.RELATIONSHIP_PROTECTED_ITEM+"].from["+ExportControlConstants.TYPE_IP_CONTROL_CLASS+"].name";
		
		StringList slSelects=new StringList(2);
		slSelects.addElement(strClassSelect);
		slSelects.addElement(DomainConstants.SELECT_OWNER);
		
		String strPreference = "";
		String strConfigPreference = "";
		String strUsePrefvalue = "";
		String strDefaultExplorationValue="";
		String strMQLCommand = "print person $1 select $2 dump '$3'";

		if(UIUtil.isNotNullAndNotEmpty(objectId) && UIUtil.isNotNullAndNotEmpty(strType)) {
				
			//In case of Drawing directly created for VPMReference, the proxy object is created and deleted once connected. 
			//Hence,added check to verify whether Proxy object exists or not.
			BusinessObject boCreatedObj=new BusinessObject(objectId);
			
			if(boCreatedObj.exists(context)) {
				DomainObject dobCreatedObj = DomainObject.newInstance(context, objectId);
				Map mpInfo=dobCreatedObj.getInfo(context,slSelects);
				String strConnectedClass = (String)mpInfo.get(strClassSelect);
				String strOwner=(String) mpInfo.get(DomainConstants.SELECT_OWNER);
				
				if(UIUtil.isNullOrEmpty(strConnectedClass)) {
					
					Map<String,String>mpPreference=getPreferenceFromType(context, strType);
					strPreference=mpPreference.get(STR_PREFERENCE);
					strConfigPreference =mpPreference.get(STR_CONFIG_PREFERENCE);
					
					//START: Added for DT18X6-135
					strType = FrameworkUtil.getAliasForAdmin(context,DomainConstants.SELECT_TYPE, strType, true);
					
					CacheManagement cacheMgmt=new CacheManagement(context);
					
					String strDefaultExplorationTypes=cacheMgmt.getValueForProperty(context, DataConstants.STR_EXPORT_CONTROL_PAGE_FILENAME, 
							"pgExportControl.DefaultExplorationValue.Types");
					String strDefaultExplorationAlertTypes=cacheMgmt.getValueForProperty(context, DataConstants.STR_EXPORT_CONTROL_PAGE_FILENAME, 
							"pgExportControl.DefaultExplorationValue.Alert.Types");
					
					//END: Added for DT18X6-135
					
					//DT18X3-316 : Handle Shape Parts as HiR by MSH7 -- Start
					//DT18X15-312: Added the condition to check whether type exists in sAnyIPClassificationList
					//DT18X6-135: Modified condition to validate type with page file string
					if((UIUtil.isNotNullAndNotEmpty(strType) && strDefaultExplorationTypes.contains(strType)) || strPreference.equals(PREF_DEFAULT_EXPLORATION)){
			
						strUsePrefvalue = MqlUtil.mqlCommand(context, strMQLCommand ,strOwner, STR_PROPERTY+PREF_DEFAULT_EXPLORATION+STR_VALUE, "~");
						strDefaultExplorationValue=strUsePrefvalue;
						
						//Checking DefaultIPClassForExploration pref value, if it is null or empty then setting it as per IP Classification configured on type
						if(UIUtil.isNullOrEmpty(strUsePrefvalue) || DataConstants.CONSTANT_EMPTY.equals(strUsePrefvalue)) {
							strPreference = strConfigPreference;
							//START: Added for DT18X15-312 Condition to throw error for Drawing type in case the  value is blank
							//DT18X6-135: Modified condition to validate type with page file string
							if(UIUtil.isNullOrEmpty(strPreference) || strDefaultExplorationAlertTypes.contains(strType)) {
								String strMessage=EnoviaResourceBundle.getProperty(context, "emxFrameworkStringResource", context.getLocale(), 
										"emxFramework.Alert.NoIPClassificationForExplorationPreference");
								throw new Exception(strMessage);
							}
							//END: Added for DT18X15-312
						} else {
							if(strUsePrefvalue.equalsIgnoreCase(STR_BUSINESS_USE))
								strPreference = PREF_BU;
							else if(strUsePrefvalue.equalsIgnoreCase(STR_HIGHLY_RESTRICTED))
								strPreference = PREF_HIR;
						}
					}
				
					//DT18X3-316 : Handle Shape Parts as HiR by MSH7 -- End
					if(UIUtil.isNotNullAndNotEmpty(strPreference)) {										
						//START: Added for DT18X2-171 (Changed logic for context as context.getUser returned User Agent)
						strUsePrefvalue = MqlUtil.mqlCommand(context, strMQLCommand ,strOwner, STR_PROPERTY + strPreference + STR_VALUE, "~");
						//END: Added for DT18X2-171
						//DT18X3-316 : Shape Part defaulted to HiR but no value, change to BU
						//DT18X6-135: Modified condition to validate type with page file string
						if (strDefaultExplorationTypes.contains(strType) && (UIUtil.isNullOrEmpty(strUsePrefvalue) 
										&& strDefaultExplorationValue.equalsIgnoreCase(STR_HIGHLY_RESTRICTED))) {
							strPreference = PREF_BU;
							strUsePrefvalue = MqlUtil.mqlCommand(context, strMQLCommand ,strOwner, STR_PROPERTY + strPreference + STR_VALUE, "~");
						}
						//DT18X3-316 : Handle Shape Parts as HiR by MSH7 -- End
					
						mpFinalMap.put("preferenceName", strPreference);
						mpFinalMap.put("preferenceValue", strUsePrefvalue);
					}
				}
			}
		}
		return mpFinalMap;
	}
	
	/**
	 * This method would fetch the preference for particular type from the page file
	 * @param context
	 * @param String type of object
	 * @return Map
	 * @throws FrameworkException 
	 */
	public Map<String,String> getPreferenceFromType(Context context,String strType) throws FrameworkException {
		
		HashMap<String, String> mpPreference=new HashMap<>();
		String strPreference = "";
		String strConfigPreference="";
		Map<String, StringList> typesListMap = getTypeListFromPageConfig(context,true);
		StringList sAlwaysHighlyRestrictedList = typesListMap.get(STR_ALWAYS_HIR);
		StringList sAlwaysRestrictedList = typesListMap.get(STR_ALWAYS_R);
		StringList sDefaultHighlyRestrictedList = typesListMap.get(STR_DEFAULT_HIR);
		StringList sDefaultRestrictedList = typesListMap.get(STR_DEFAULT_R);
		StringList sAnyIPClassificationList = typesListMap.get(STR_ANY_CLASSIFICATION);
		
		strType = FrameworkUtil.getAliasForAdmin(context,DomainConstants.SELECT_TYPE, strType, true);
			
		if (sAlwaysHighlyRestrictedList.contains(strType) || sDefaultHighlyRestrictedList.contains(strType)) {
			strPreference = PREF_HIR;
			strConfigPreference=strPreference;
		} else if (sAlwaysRestrictedList.contains(strType) || sDefaultRestrictedList.contains(strType)) {
			strPreference = PREF_BU;
			strConfigPreference=strPreference;
		}else if(sAnyIPClassificationList.contains(strType)) {
			strPreference=PREF_DEFAULT_EXPLORATION;
		}
		mpPreference.put(STR_PREFERENCE, strPreference);
		mpPreference.put(STR_CONFIG_PREFERENCE, strConfigPreference);
	
		return mpPreference;
	}
	
	/**
	 * This method would fetch the preference value from the property preference of the user
	 * @param context
	 * @param Map
	 * @return String
	 * @throws FrameworkException 
	 */
	public String getUserPreferenceValue(Context context,Map<String,String> mpPreference) throws FrameworkException {
		
		String strPreference=mpPreference.get(STR_PREFERENCE);
		String strConfigPreference =mpPreference.get(STR_CONFIG_PREFERENCE);
		String strMQLCommand = "print person $1 select $2 dump '$3'";
		String strUser=context.getUser();
		String strUsePrefvalue="";
		
		if(strPreference.equals(PREF_DEFAULT_EXPLORATION)) {
			strUsePrefvalue = MqlUtil.mqlCommand(context, strMQLCommand ,strUser, STR_PROPERTY+PREF_DEFAULT_EXPLORATION+STR_VALUE, "~");
			if(UIUtil.isNullOrEmpty(strUsePrefvalue) || DataConstants.CONSTANT_EMPTY.equals(strUsePrefvalue)) {
				strPreference = strConfigPreference;
			} else {
				if(strUsePrefvalue.equalsIgnoreCase(STR_BUSINESS_USE))
					strPreference = PREF_BU;
				else if(strUsePrefvalue.equalsIgnoreCase(STR_HIGHLY_RESTRICTED))
					strPreference = PREF_HIR;
			}
		}
		
		if(UIUtil.isNotNullAndNotEmpty(strPreference)) {										
			strUsePrefvalue = MqlUtil.mqlCommand(context, strMQLCommand ,strUser, STR_PROPERTY + strPreference + STR_VALUE, "~");
		}
		
		return strUsePrefvalue;
	}
	
	/**
	 * This method would connect the list of IP Control classes to the object.
	 * If bReplace parameter is passed as true, it would disconnect the existing IP Control classes
	 * @param context
	 * @param String objectId
	 * @param String list of IP Classes
	 * @param String preference (BU or HiR)
	 * @param boolean replace existing IP Control classes or not
	 * @throws MatrixException 
	 * @throws IOException 
	 */
	public void connectIPControlClass(Context context, String strObjectId, String strIPClassList, String strPreference,boolean bReplace) throws MatrixException, IOException{
		
		if(UIUtil.isNotNullAndNotEmpty(strIPClassList) && UIUtil.isNotNullAndNotEmpty(strObjectId)) {
			
			DomainObject doObject=DomainObject.newInstance(context,strObjectId);
			
			String strType=doObject.getInfo(context, DomainConstants.SELECT_TYPE);
			String strSymbolicTypeName=FrameworkUtil.getAliasForAdmin(context,DomainConstants.SELECT_TYPE, strType, true);
			
			if(UIUtil.isNullOrEmpty(strPreference)) {
				if(strIPClassList.contains("HiR"))
					strPreference=PREF_HIR;
				else
					strPreference=PREF_BU;
			}
			
			InterfaceManagement interfaceMgmt=new InterfaceManagement(context);
			boolean bInterfaceExists=interfaceMgmt.checkInterfaceOnObject(context, strObjectId, DataConstants.INTERFACE_IPSEC_CLASS);
			
			CacheManagement cacheMgmt=new CacheManagement(context);
			String strAllowedTypes=cacheMgmt.getValueForProperty(context, DataConstants.STR_EXPORT_CONTROL_PAGE_FILENAME, "pgExportControl.DTIPSecClassExtension.Interface.AllowedTypes");
			if(!bInterfaceExists && strAllowedTypes.contains(strSymbolicTypeName))
				interfaceMgmt.addInterface(context, strObjectId, DataConstants.INTERFACE_IPSEC_CLASS);
			
			if(strPreference.equalsIgnoreCase(PREF_BU))
				doObject.setAttributeValue(context, DataConstants.ATTR_PG_IP_CLASSIFICATION, DataConstants.RANGE_RESTRICTED);
			else if(strPreference.equalsIgnoreCase(PREF_HIR))
				doObject.setAttributeValue(context, DataConstants.ATTR_PG_IP_CLASSIFICATION, DataConstants.RANGE_HIGHLYRESTRICTED);
			
			strIPClassList = strIPClassList.replace(";", "|");
		
			HashMap<String, String> paramMapNew1 = new HashMap<>();
			paramMapNew1.put("strClassNames",strIPClassList);
			String[] methodargs = JPO.packArgs(paramMapNew1);
				
			Map<String, String> mapPrefclass = JPO.invoke(context,STR_PROGNAME_SECURITY_UTIL, null, "getPreferredClasseMap", methodargs, Map.class);
			String strPreferredClassIds = mapPrefclass.get("classIds");
			StringList slIPCLassList;
			
			if(bReplace)
				slIPCLassList=disconnectExistingIPClasses(context, strPreferredClassIds, doObject);
			else
				slIPCLassList = StringUtil.split(strPreferredClassIds, "|");

			String[] strClassOIDArray = slIPCLassList.toArray(new String[slIPCLassList.size()]);
		
			if(strClassOIDArray.length > 0){
				DomainRelationship.connect(context,doObject,DomainConstants.RELATIONSHIP_PROTECTED_ITEM,false,strClassOIDArray);
			}
		}
	}
	
	/**
	 * This method would disconnect the list of IP Control classes already connected to the object (if those are not present in the strIPClassIds)
	 * @param context
	 * @param String list of IP Classes to be connected
	 * @param DO object
	 * @return StringList of IP Classes to be connected
	 * @throws FrameworkException 
	 */
	public StringList disconnectExistingIPClasses(Context context,String strIPClassIds,DomainObject doObject) throws FrameworkException  {
		
		StringList slIPCLassList = StringUtil.split(strIPClassIds, "|");
		
		MapList mConnectedIPClassMapList = getConnectedIPControlClasses(context, doObject);

		String sOID = "";
		String sRelID = "";
		Map<String,String> mIPClassMap = null;
		StringList slAlreadyConnectedIPClass = new StringList();
		StringList slToBeConnectedIPClassList = new StringList();
		int size = mConnectedIPClassMapList.size();
		
		if(size > 0){
			for (int i = 0; i < size ; i++) {
				mIPClassMap =(Map<String,String>)mConnectedIPClassMapList.get(i);
				sOID = mIPClassMap.get(DomainConstants.SELECT_ID);
				slAlreadyConnectedIPClass.add(sOID);
				sRelID =mIPClassMap.get(DomainRelationship.SELECT_ID);
				if(!slIPCLassList.contains(sOID)){
					slToBeConnectedIPClassList.add(sRelID);
				}
			}
			
			String[] strClassOIDArray = slToBeConnectedIPClassList.toArray(new String[slToBeConnectedIPClassList.size()]);
			
			//disconnecting IP Classes which are not present in slIPCLassList
			if(strClassOIDArray.length > 0){
				DomainRelationship.disconnect(context,strClassOIDArray,true);
				slToBeConnectedIPClassList.clear();
			}	
		}
		
		size = slIPCLassList.size();
	
		//adding IP Classes ids to stringlist  which are not connected to the target DO
		for (int i = 0; i < size ; i++) {
			sOID = slIPCLassList.get(i);
			if(!slAlreadyConnectedIPClass.contains(sOID)){
				slToBeConnectedIPClassList.add(sOID);
			}
		}
		return slToBeConnectedIPClassList;
	}
	
	/**
	 * This method would be invoked from web service.
	 * This would connect the list of IP Control classes to respective object whose TNR is passed
	 * @param context
	 * @param String type of object
	 * @param String name of object
	 * @param String rev of object
	 * @param String list of IP Class
	 * @throws MatrixException 
	 * @throws IOException 
	 */
	public void connectIPClassesToObject(Context context,String sType,String sName, String sRev,String sIPClassList) throws MatrixException, IOException{
		StringList slSelects=new StringList(2);
		slSelects.addElement(DomainConstants.SELECT_ID);
		slSelects.addElement(DataConstants.SELECT_PHYSICALID);
		if(UIUtil.isNotNullAndNotEmpty(sType) && UIUtil.isNotNullAndNotEmpty(sName) && UIUtil.isNotNullAndNotEmpty(sRev)) {
			MapList mlObject=findObject(context, sType, sName, sRev, slSelects);
		
			if(!mlObject.isEmpty()) {
				Map<String,String>mpObject=(Map<String, String>) mlObject.get(0);
				String sObjectId=mpObject.get(DomainConstants.SELECT_ID);
				String sPhysicalId=mpObject.get(DataConstants.SELECT_PHYSICALID);
				
				String sIPClassConnectedObjId=getIPClassConnectedObjectId(context, sType, sObjectId, sPhysicalId);
				if(UIUtil.isNotNullAndNotEmpty(sIPClassConnectedObjId))
					connectIPControlClass(context, sIPClassConnectedObjId, sIPClassList, "", true);
			}
		}
	}
	
	/**
	 * This method would get the connected VPMReference object information
	 * @param context
	 * @param String objectId of the object
	 * @param Pattern relPattern
	 * @param StringList object selectables
	 * @param StringList rel selectables
	 * @param boolean is object at from side
	 * @return Map
	 * @throws FrameworkException 
	 */
	public Map<String,String> getRelatedVPMReferenceObjectId(Context context, String sObjectId,Pattern relPattern,StringList slObjSelects,StringList slRelSelects,boolean isFrom) throws FrameworkException{
	
		Map<String,String> mpVPMReference = null;
		boolean isTo=false;
		DomainObject doObject=DomainObject.newInstance(context,sObjectId);
		if(!isFrom)
			isTo=true;
		
		MapList mlVPMReference= doObject.getRelatedObjects(context,
				relPattern.getPattern(),  // relationship pattern
				DataConstants.TYPE_VPMREFERENCE,         // type pattern
				slObjSelects,        // Object selects
				slRelSelects,               // relationship selects
				isTo,               // to
				isFrom,              //from
				(short)1,           // expand level
				"",               // object where
				null,               // relationship where
				0);                 // limit
		
		if(!mlVPMReference.isEmpty()) {
			mpVPMReference=(Map<String, String>)mlVPMReference.get(0);
		}
		return mpVPMReference;
	}
	
	/**
	 * This method would be invoked from web service
	 * It is used to get the value of particular preference from user properties
	 * @param context
	 * @param String preference name
	 * @return String preference value
	 * @throws FrameworkException
	 */
	public String getPreferenceValue(Context context,String sPreferenceName) throws FrameworkException {
		String sPreferenceValue=PropertyUtil.getAdminProperty(context, DataConstants.CONSTANT_PERSON, context.getUser(), sPreferenceName);
		if(UIUtil.isNullOrEmpty(sPreferenceValue))
			sPreferenceValue="";
		return sPreferenceValue;
	}
	
	/**
	 * This method would be invoked from web service
	 * It is used to set the value of particular property on user
	 * @param context
	 * @param String preference name
	 * @param String preference value
	 * @throws FrameworkException
	 */
	public void setPreferenceValue(Context context,String sPreferenceName, String sPreferenceValue) throws FrameworkException {
		PropertyUtil.setAdminProperty(context, DataConstants.CONSTANT_PERSON, context.getUser(), sPreferenceName, sPreferenceValue);
	}
	
	/**
	 * This method would be invoked from web service
	 * It is used to remove particular preference from user properties
	 * @param context
	 * @param String preference name
	 * @throws FrameworkException
	 */
	public void clearPreferenceValue(Context context,String sPreferenceName) throws FrameworkException {
		PropertyUtil.removeAdminProperty(context, DataConstants.CONSTANT_PERSON, context.getUser(), sPreferenceName);
	}

	/**
	 * This method would be invoked from web service
	 * It is used to fetch the IP Control classes list for Business Use
	 * @param context
	 * @return StringList
	 * @throws MatrixException
	 */
	public StringList getSecurityCategoryListForBU(Context context) throws MatrixException {
		return getSecurityCategoryList(context,STR_RESTRICTED);
	}
	
	/**
	 * This method would be invoked from web service
	 * It is used to fetch the IP Control classes list for HiR
	 * @param context
	 * @return StringList
	 * @throws MatrixException
	 */
	public StringList getSecurityCategoryListForHiR(Context context) throws MatrixException {
		return getSecurityCategoryList(context,STR_HIGHLY_RESTRICTED);
	}

	/**
	 * This method would fetch the IP Control classes list for the user.(BU or HiR)
	 * @param context
	 * @param String IP Class Type (BU/HiR)
	 * @return StringList
	 * @throws MatrixException
	 */
	private StringList getSecurityCategoryList(Context context, String sIPClassType) throws MatrixException {
		HashMap<String,String> hmParam=new HashMap<>();
		hmParam.put("vSelectedValue", sIPClassType);
		return JPO.invoke(context, STR_PROGNAME_SECURITY_UTIL, null, STR_METHODNAME_IPCONTROLCLASS_USER, JPO.packArgs(hmParam), StringList.class);
	}
	
	/**
	 * This method would be invoked from web service
	 * It is used to fetch the list for Design For field
	 * @param context
	 * @param Locale
	 * @return StringList
	 * @throws FrameworkException 
	 */
	public StringList getDesignForList(Context context) throws FrameworkException{
		String sDesignForOptions=EnoviaResourceBundle.getFrameworkStringResourceProperty(context, "emxFramework.Preferences.DIPreferences.DIDesignFor.Options", context.getLocale());
		String sDesignForPrefValue=getPreferenceValue(context, PREF_DESIGN_FOR);
		StringList slOptions=StringUtil.split(sDesignForOptions, ",");
		StringList slFinalOptions=new StringList();
		
		if(UIUtil.isNotNullAndNotEmpty(sDesignForPrefValue)) {
			for(int i=0;i<slOptions.size();i++) {
				if(sDesignForPrefValue.equals(slOptions.get(i)))
					slFinalOptions.add(0, slOptions.get(i));
				else
					slFinalOptions.add(slOptions.get(i));
			}
		}else {
			slFinalOptions=slOptions;
		}
		return slFinalOptions;
	}
	
	/**
	 * This method would be invoked from web service
	 * It is used to fetch the list for Manufacturing Maturity Status field
	 * @param context
	 * @param Locale
	 * @return StringList
	 * @throws FrameworkException 
	 */
	public StringList getMfgMaturityStatusList(Context context) throws FrameworkException{
		String sMfgMaturityOptions=EnoviaResourceBundle.getFrameworkStringResourceProperty(context, "emxFramework.Preferences.DIPreferences.DIMaturityStatus.Options", context.getLocale());

		String sMfgMaturityPrefValue=getPreferenceValue(context, PREF_DI_MATURITY_STATUS);
		StringList slOptions=StringUtil.split(sMfgMaturityOptions, ",");
		StringList slFinalOptions=new StringList();
		
		if(UIUtil.isNotNullAndNotEmpty(sMfgMaturityPrefValue)) {
			for(int i=0;i<slOptions.size();i++) {
				if(sMfgMaturityPrefValue.equals(slOptions.get(i)))
					slFinalOptions.add(0, slOptions.get(i));
				else
					slFinalOptions.add(slOptions.get(i));
			}
		}else {
			slFinalOptions=slOptions;
		}
		return slFinalOptions;
	}
	
	/**
	 * This method would be invoked from web service
	 * It is used to fetch the list for Default IP Classification for Exploration/Drawing field
	 * @param context
	 * @param Locale
	 * @return StringList
	 * @throws FrameworkException 
	 */
	public StringList getDefaultIPClassificationList(Context context) throws FrameworkException{
		String strDefaultIpClassOptions=EnoviaResourceBundle.getFrameworkStringResourceProperty(context, "emxFramework.Preferences.DIPreferences.DefaultIpClassificationForShapePart.Options",
				context.getLocale());
		StringList slIPClassificationOptions=StringUtil.split(strDefaultIpClassOptions, ",");
		
		
		String strBusinessUsePreference=getPreferenceValue(context, PREF_BU);
		String strHighlyRestrictedClasstrPreference=getPreferenceValue(context, PREF_HIR);
		
		
		StringList slTempPrefList = new StringList();
		slTempPrefList.add("");
		slTempPrefList.addAll(slIPClassificationOptions);
		
		if(UIUtil.isNullOrEmpty(strBusinessUsePreference))
			slTempPrefList.remove(slIPClassificationOptions.get(1));

		if(UIUtil.isNullOrEmpty(strHighlyRestrictedClasstrPreference))
			slTempPrefList.remove(slIPClassificationOptions.get(0));
			
		return slTempPrefList;
	}
}

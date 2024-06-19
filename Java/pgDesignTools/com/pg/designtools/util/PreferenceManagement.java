package com.pg.designtools.util;

import java.util.Map;

import com.dassault_systemes.enovia.gls.common.model.PRSPContext;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.vplmintegration.util.VPLMIntegTraceUtil;
import com.pg.designtools.datamanagement.CommonUtility;
import com.pg.designtools.datamanagement.DataConstants;

import matrix.db.Context;
import matrix.util.StringList;

public class PreferenceManagement {
	
    public static final String PREF_BU="preference_BusinessUseClass";
    public static final String PREF_HIR="preference_HighlyRestrictedClass";
    public static final String PREF_DESIGN_FOR="preference_DIDesignFor";
    public static final String PREF_PACKAGING_MATURITY_STATUS="preference_PackagingPartPhase";
    public static final String PREF_PRODUCT_MATURITY_STATUS="preference_ProductPartPhase";
    public static final String PREF_PACKAGING_STRUCTURE_RELEASE_CRITERIA="preference_PackagingReleaseCriteria";
    public static final String PREF_PRODUCT_STRUCTURE_RELEASE_CRITERIA="preference_ProductReleaseCriteria";
    public static final String DEFAULT_PREF_DESIGN_FOR="Packaging";
    public static final String DEFAULT_PREF_MATURITY_STATUS="Development";
    public static final String DEFAULT_PREF_STRUCTURE_RELEASE_CRITERIA="No";

    public PreferenceManagement(Context context) {
		PRSPContext.set(context);
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
		//getAdminProperty gets the preference value from cache. This caused problem while getting actual preference value for multiple jvms.
		//Hence, used direct mql command to get the actual value
		String sPreferenceValue=getAdminPropertyWithoutCache(context, context.getUser(), sPreferenceName);
		if(UIUtil.isNullOrEmpty(sPreferenceValue) || DataConstants.CONSTANT_EMPTY.equals(sPreferenceValue))
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
		//setAdminProperty sets the preference value in cache. This caused problem while getting actual preference value for multiple jvms.
		//Hence, used direct mql command to set the actual value
		setAdminPropertyWithoutCache(context, context.getUser(), sPreferenceName, sPreferenceValue);
	}
	
	/**
	 * This method would be invoked from web service
	 * It is used to remove particular preference from user properties
	 * @param context
	 * @param String preference name
	 * @throws FrameworkException
	 */
	public void clearPreferenceValue(Context context,String sPreferenceName) throws FrameworkException {
		//removeAdminProperty removes the preference value in cache. This caused problem while getting actual preference value for multiple jvms.
		//Hence, used direct mql command to remove preference
		removeAdminPropertyWithoutCache(context, context.getUser(), sPreferenceName);
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
		StringList slOptions=StringUtil.split(sDesignForOptions, DataConstants.SEPARATOR_COMMA);
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
		String sMfgMaturityPrefValue="";
		
		String sDesignForPrefValue=getPreferenceValue(context, PREF_DESIGN_FOR);
		if(DataConstants.CONSTANT_DESIGN_FOR_PACKAGING.equals(sDesignForPrefValue) || DataConstants.CONSTANT_DESIGN_FOR_EXPLORATION.equals(sDesignForPrefValue) || DataConstants.CONSTANT_DESIGN_FOR_AUTOMATION.equals(sDesignForPrefValue))
			sMfgMaturityPrefValue=getPreferenceValue(context, DataConstants.PREFERENCE_PACKAGING_PHASE);
		else
			sMfgMaturityPrefValue=getPreferenceValue(context, DataConstants.PREFERENCE_PRODUCT_PHASE);
		
		StringList slOptions=StringUtil.split(sMfgMaturityOptions, DataConstants.SEPARATOR_COMMA);
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
		StringList slIPClassificationOptions=StringUtil.split(strDefaultIpClassOptions, DataConstants.SEPARATOR_COMMA);
		
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
	
	/**
	 * It is used to fetch the default values of the preferences
	 * @param String preferenceName
	 * @return String
	 */
	private String getDefaultValueForPreference(String strPreferenceName) {
		String strPreferenceValue="";
		if(PREF_DESIGN_FOR.equals(strPreferenceName))
			strPreferenceValue=DEFAULT_PREF_DESIGN_FOR;
		else if(PREF_PACKAGING_MATURITY_STATUS.equals(strPreferenceName) || PREF_PRODUCT_MATURITY_STATUS.equals(strPreferenceName))
			strPreferenceValue=DEFAULT_PREF_MATURITY_STATUS;
		else if(PREF_PACKAGING_STRUCTURE_RELEASE_CRITERIA.equals(strPreferenceName) || PREF_PRODUCT_STRUCTURE_RELEASE_CRITERIA.equals(strPreferenceName))
			strPreferenceValue=DEFAULT_PREF_STRUCTURE_RELEASE_CRITERIA;
		return strPreferenceValue;
	}
	
	/**
	 * Used to get the preference values for the list of preferences passed
	 * @param Context
	 * @param StringList list of preferenceName
	 * @return String
	 * @throws FrameworkException
	 */
	public String getUserPreferenceValues(Context context,StringList slPreferenceNames) throws FrameworkException {
		VPLMIntegTraceUtil.trace(context, ">>>>> START getUserPreferenceValues method");
		String strPrefName;
		String strPrefValue;
		StringBuilder sbFinalList=new StringBuilder();
		VPLMIntegTraceUtil.trace(context, ">>>>> slPreferenceNames::"+slPreferenceNames);
		
		for(int i=0;i<slPreferenceNames.size();i++) {
			strPrefName=slPreferenceNames.get(i);
			strPrefValue=getPreferenceValue(context, strPrefName);
			
			 if(DataConstants.CONSTANT_EMPTY.equals(strPrefValue))
				 strPrefValue="";
			 
			 VPLMIntegTraceUtil.trace(context, ">>>>> strPrefName::"+strPrefName+" strPrefValue::"+strPrefValue);
			 
			//format the string 
			formatPreferenceNameValuePair(sbFinalList, strPrefName, strPrefValue);
		}
		VPLMIntegTraceUtil.trace(context, ">>>>> Result::"+sbFinalList.toString());
		return sbFinalList.toString();
	}

	/**
	 * Used to create comma separated string of preferenceName~preferenceValue pairs
	 * @param StringList list of preferenceName
	 * @param String preferenceValues
	 * @return String
	 */
	private void formatPreferenceNameValuePair(StringBuilder sbFinalList,String strPrefName, String strPrefValue) {

		if(sbFinalList.length()>0)
			sbFinalList.append(DataConstants.SEPARATOR_AT_THE_RATE);
		
		sbFinalList.append(strPrefName);
		sbFinalList.append(DataConstants.SEPARATOR_COLON);
		sbFinalList.append(strPrefValue);
	}
	
	/**
	 * Method to fetch the Property values from user and connect the corresponding Organizations to the object
	 * @param context
	 * @param doObject
	 * @throws FrameworkException
	 */
	public void connectOrganizations(Context context, DomainObject doObject) throws FrameworkException {
		String strPrimaryOrgPreference=EnoviaResourceBundle.getProperty(context, "emxComponentsStringResource", context.getLocale(),"emxComponents.Preference.PrimaryOrg"); 
		
		StringList slPreferenceNames=new StringList(2);
		slPreferenceNames.add(strPrimaryOrgPreference);

		String strPrefNameValues=getUserPreferenceValues(context, slPreferenceNames);

		//preference_DefaultAttrPrimaryOrg:Digestive Wellness~EUR/ME/AFRICA
		if(UIUtil.isNotNullAndNotEmpty(strPrefNameValues)) {
			StringList slPrefList=StringUtil.split(strPrefNameValues, DataConstants.SEPARATOR_AT_THE_RATE);
			StringList slPrefNameValue;
			String strPreferenceName="";
			String strPreferenceValue="";
			
			for(int i=0;i<slPrefList.size();i++) {
				
				slPrefNameValue=StringUtil.split(slPrefList.get(i), DataConstants.SEPARATOR_COLON);
				
				strPreferenceName=slPrefNameValue.get(0);
				strPreferenceValue=slPrefNameValue.get(1);

				if(UIUtil.isNotNullAndNotEmpty(strPreferenceValue)) {
					if(strPrimaryOrgPreference.equals(strPreferenceName))
						connectOrganization(context,doObject,strPreferenceValue,DataConstants.REL_PRIMARY_ORGANIZATION);
					else
						connectSecondaryOrganization(context,doObject,strPreferenceValue);
				}
			}
		}
	}

	/**
	 * Method for connecting Secondary Organizations to object
	 * @param context
	 * @param doObject
	 * @param strOrganizationNames
	 * @throws FrameworkException
	 */
	private void connectSecondaryOrganization(Context context, DomainObject doObject, String strOrganizationNames) throws FrameworkException {
		//There can be multiple Secondary organizations. Hence need to split the strOrganizationNames
		
		StringList slOrganizations=StringUtil.split(strOrganizationNames, DataConstants.SEPARATOR_PIPE);
		for(int i=0;i<slOrganizations.size();i++) {
			connectOrganization(context, doObject, slOrganizations.get(i), DataConstants.REL_SECONDARY_ORGANIZATION);
		}
	}

	/**
	 * Generic method to connect Organization to the corresponding object
	 * @param context
	 * @param doObject
	 * @param strOrgName
	 * @param strRelName
	 * @throws FrameworkException
	 */
	private void connectOrganization(Context context, DomainObject doObject, String strOrgName,String strRelName) throws FrameworkException {
		IPManagement ipMgmt=new IPManagement(context);
		MapList mlObjectInfo=ipMgmt.findObject(context, DataConstants.TYPE_ORG_CHANGE_MGMT, strOrgName, "-", new StringList(DomainConstants.SELECT_ID));

		if(!mlObjectInfo.isEmpty()) {
			Map<String,String> mpObjectInfo=(Map<String, String>) mlObjectInfo.get(0);
			String strOrgId=mpObjectInfo.get(DomainConstants.SELECT_ID);
			
			if(UIUtil.isNotNullAndNotEmpty(strOrgId)) {
				DomainObject doOrg = DomainObject.newInstance(context, strOrgId);
				DomainRelationship.connect(context, doObject, strRelName, doOrg);
			}
		}
	}
	
	/**
	 * Added for DTWPI-32
	 * This method would get the display value for the preferences and send the preferenceName:Value#preferenceName:Value string as result
	 * Mainly used to get the name of the preference value, whose object ids are stored as preference value
	 * @param context
	 * @param strUserPreferenceNameValues
	 * @return String
	 * @throws FrameworkException
	 */
	public String getDisplayValuesForUserPreferences(Context context, String strUserPreferenceNameValues) throws FrameworkException {
		VPLMIntegTraceUtil.trace(context, "START of PreferenceManagement: getDisplayValuesForUserPreferences method");
		StringBuilder sbFinalList=new StringBuilder();
		
		if(UIUtil.isNotNullAndNotEmpty(strUserPreferenceNameValues)) {
			//split the string with comma so that we get individual preference name: value pair
			
			StringList slUserPrefNameValues=StringUtil.split(strUserPreferenceNameValues, DataConstants.SEPARATOR_AT_THE_RATE);
			VPLMIntegTraceUtil.trace(context, "slUserPrefNameValues:::"+slUserPrefNameValues);
			
			StringList slSinglePrefDetails;
			DomainObject doObject;
			String strPrefName;
			String strPrefValue;
			
			for(int i=0;i<slUserPrefNameValues.size();i++) {
				
				slSinglePrefDetails=StringUtil.split(slUserPrefNameValues.get(i), DataConstants.SEPARATOR_COLON);
				
				strPrefName=slSinglePrefDetails.get(0);
				strPrefValue=slSinglePrefDetails.get(1);
				
				if(UIUtil.isNotNullAndNotEmpty(strPrefValue) && Character.isDigit(strPrefValue.charAt(0))){
					doObject=DomainObject.newInstance(context,strPrefValue);
					strPrefValue=doObject.getInfo(context, DomainConstants.SELECT_NAME);
				}
				
				VPLMIntegTraceUtil.trace(context, "strPrefName:::"+strPrefName+" strPrefValue::"+strPrefValue);
				
				//format the string 
				formatPreferenceNameValuePair(sbFinalList, strPrefName, strPrefValue);
			}
		}
		VPLMIntegTraceUtil.trace(context, "Final Output:::"+sbFinalList.toString());
		return sbFinalList.toString();
	}

	/**
	 * Method to get the preference value using mql 
	 * @param context
	 * @param strUserName
	 * @param strPreferenceName
	 * @return String Preference value
	 * @throws FrameworkException
	 */
	public String getAdminPropertyWithoutCache(Context context,String strUserName, String strPreferenceName) throws FrameworkException{
		
		VPLMIntegTraceUtil.trace(context, ">>>> Inside getAdminPropertyWithoutCache method");
		VPLMIntegTraceUtil.trace(context, ">>>> strUserName::"+strUserName+" strPreferenceName::"+strPreferenceName);
		
		String strResult="";
		
		if(UIUtil.isNotNullAndNotEmpty(strUserName) && UIUtil.isNotNullAndNotEmpty(strPreferenceName)) {
			StringBuilder sbMQLCommand=new StringBuilder();
			sbMQLCommand.append("print person ");
			sbMQLCommand.append("'"+strUserName+"'");
			sbMQLCommand.append(" select");
			sbMQLCommand.append(" property[").append(strPreferenceName).append("].value dump '';");
			
			VPLMIntegTraceUtil.trace(context, ">>>>sbMQLCommand::"+sbMQLCommand.toString());
			
			CommonUtility commonUtility=new CommonUtility(context);
			strResult=commonUtility.executeMQLCommands(context, sbMQLCommand.toString());
		}
		VPLMIntegTraceUtil.trace(context, ">>>>strResult::"+strResult);
		return strResult;
	}
	
	/**
	 * Method to set the preference value using mql
	 * @param context
	 * @param strUserName
	 * @param strPreferenceName
	 * @param strPreferenceValue
	 * @throws FrameworkException
	 */
	public void setAdminPropertyWithoutCache(Context context,String strUserName, String strPreferenceName,String strPreferenceValue) throws FrameworkException{
		
		VPLMIntegTraceUtil.trace(context, ">>>> Inside setAdminPropertyWithoutCache method");
		VPLMIntegTraceUtil.trace(context, ">>>> strUserName::"+strUserName+" strPreferenceName::"+strPreferenceName+" strPreferenceValue::"+strPreferenceValue);
		
		if(UIUtil.isNotNullAndNotEmpty(strUserName) && UIUtil.isNotNullAndNotEmpty(strPreferenceName)) {
			StringBuilder sbMQLCommand=new StringBuilder();
			sbMQLCommand.append("mod person ");
			sbMQLCommand.append("'"+strUserName+"'");
			sbMQLCommand.append(" property ").append(strPreferenceName).append(" value \"");
			sbMQLCommand.append(strPreferenceValue).append("\";");
						
			VPLMIntegTraceUtil.trace(context, ">>>>sbMQLCommand::"+sbMQLCommand.toString());
			
			CommonUtility commonUtility=new CommonUtility(context);
			commonUtility.executeMQLCommands(context, sbMQLCommand.toString());
		}
	}
	
	/**
	 * Method to remove the preference using mql
	 * @param context
	 * @param strUserName
	 * @param strPreferenceName
	 * @throws FrameworkException
	 */
	public void removeAdminPropertyWithoutCache(Context context,String strUserName, String strPreferenceName) throws FrameworkException{
		
		VPLMIntegTraceUtil.trace(context, ">>>>Inside removeAdminPropertyWithoutCache method");
		VPLMIntegTraceUtil.trace(context, ">>>> strUserName::"+strUserName+" strPreferenceName::"+strPreferenceName);
		
		if(UIUtil.isNotNullAndNotEmpty(strUserName) && UIUtil.isNotNullAndNotEmpty(strPreferenceName)) {
			
			String strResult=getAdminPropertyWithoutCache(context, strUserName, strPreferenceName);
			if(UIUtil.isNotNullAndNotEmpty(strResult)) {
				StringBuilder sbMQLCommand=new StringBuilder();
				sbMQLCommand.append("mod person ");
				sbMQLCommand.append("'"+strUserName+"'");
				sbMQLCommand.append(" remove property ").append(strPreferenceName).append(";");
							
				VPLMIntegTraceUtil.trace(context, ">>>>sbMQLCommand::"+sbMQLCommand.toString());
				
				CommonUtility commonUtility=new CommonUtility(context);
				commonUtility.executeMQLCommands(context, sbMQLCommand.toString());
			}
		}
	}
}

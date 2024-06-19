package com.pg.designtools.services;

import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import com.dassault_systemes.platform.restServices.RestService;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.vplmintegration.util.VPLMIntegTraceUtil;
import com.pg.designtools.datamanagement.CommonUtility;
import com.pg.designtools.datamanagement.DataConstants;
import com.pg.designtools.util.CacheManagement;
import com.pg.designtools.util.PreferenceManagement;

import matrix.util.StringList;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;

@Path("/PreferenceServices")
public class PreferenceServices extends RestService {

	protected matrix.db.Context context; 
	protected PreferenceManagement prefManagement=new PreferenceManagement(context);
	
	@GET
	@Path("/syncDIPreferenceFromServer")
	public Response syncDIPreferenceFromServer(@Context HttpServletRequest req) {
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> START of syncDIPreferenceFromServer method");
		String strValue="";
		try {
			context=getAuthenticatedContext(req, false);
			
			CacheManagement cacheMgmt=new CacheManagement(context);
			String strDesignFor = prefManagement.getPreferenceValue(context, DataConstants.PREFERENCE_DESIGN_FOR);
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> strDesignFor:::"+strDesignFor);
			String strDesignForValue="";
			String strPreferences="";

			if(UIUtil.isNotNullAndNotEmpty(strDesignFor)) {
				if (DataConstants.CONSTANT_DESIGN_FOR_PACKAGING.equals(strDesignFor) || DataConstants.CONSTANT_DESIGN_FOR_EXPLORATION.equals(strDesignFor) || DataConstants.CONSTANT_DESIGN_FOR_AUTOMATION.equals(strDesignFor)){
					strDesignForValue = "pgDIPreferences.Properties.Packaging";
				}
				else if (DataConstants.CONSTANT_DESIGN_FOR_PRODUCT.equals(strDesignFor)){
	
					strDesignForValue = "pgDIPreferences.Properties.Product";
				}
				strPreferences=cacheMgmt.getValueForProperty(context, DataConstants.STR_DESIGN_CONFIG_PAGE_FILENAME, strDesignForValue);
				VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>strPreferences::"+strPreferences);
			}
			else {
				strValue=DataConstants.PREFERENCES_NOT_SET;
			}
		
			
			if(UIUtil.isNotNullAndNotEmpty(strPreferences)) {
				
				StringList slPrefList=StringUtil.split(strPreferences, DataConstants.SEPARATOR_PIPE);
				strValue=prefManagement.getUserPreferenceValues(context, slPrefList);
				strValue=prefManagement.getDisplayValuesForUserPreferences(context,strValue);
			}
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> Final output::"+strValue);
		} catch (Exception ex) {
			return Response.status(500).entity(ex.getMessage()).build();
		}
		return Response.status(200).entity(strValue).build();
	}
	
	@GET
	@Path("/syncDIPreferenceToServer")
	public Response syncDIPreferenceToServer(@Context HttpServletRequest req) {
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> START of syncDIPreferenceToServer method");
		try {
			context=getAuthenticatedContext(req, false);
			
			String queryString = req.getQueryString();
			queryString = java.net.URLDecoder.decode(queryString,"UTF-8");
			StringList slPrefData;
			
			if (UIUtil.isNotNullAndNotEmpty(queryString)) {
				slPrefData=StringUtil.split(queryString, DataConstants.SEPARATOR_AT_THE_RATE);
				VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> Data from query::"+slPrefData);
				StringList slPrefNameValue;
				
				if (!slPrefData.isEmpty()) {
					for(int i=0;i<slPrefData.size();i++) {
						slPrefNameValue=StringUtil.split(slPrefData.get(i), DataConstants.SEPARATOR_COLON);
						VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> Preference name ::"+ slPrefNameValue.get(0));
						VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> Preference  value::"+slPrefNameValue.get(1));
						prefManagement.setPreferenceValue(context, slPrefNameValue.get(0), slPrefNameValue.get(1));
					}
					
						//set the value of PartType preference as per the Design For value
						String strDesignDomain=prefManagement.getPreferenceValue(context, DataConstants.PREFERENCE_DESIGN_FOR);
						VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> strDesignDomain value::"+strDesignDomain);
						
						if(UIUtil.isNullOrEmpty(strDesignDomain))
							strDesignDomain=DataConstants.CONSTANT_DESIGN_FOR_PACKAGING;
						
						if(UIUtil.isNotNullAndNotEmpty(strDesignDomain)) {
							if(DataConstants.CONSTANT_DESIGN_FOR_PACKAGING.equals(strDesignDomain)) {
								prefManagement.setPreferenceValue(context,  "preference_PackagingPartType ", DataConstants.TYPE_PG_MASTER_PACKAGING_MATERIAL_PART);
							}
							else if(DataConstants.CONSTANT_DESIGN_FOR_PRODUCT.equals(strDesignDomain)) {
								prefManagement.setPreferenceValue(context,  "preference_ProductPartType ", DataConstants.TYPE_PG_MASTER_PRODUCT_PART);
							}
						}
					}
				}
		} catch (Exception ex) {
			return Response.status(500).entity(ex.getMessage()).build();
		}
		return Response.status(200).entity("Preference values set successfully").build();
	}
	
	@GET
	@Path("/getPreferenceValue")
	public Response getPrefererenceValueForUser(@Context HttpServletRequest req,
			@DefaultValue("") @QueryParam("PreferenceName") String sPreferenceName) {
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> START of getPrefererenceValueForUser method");
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>sPreferenceName::"+sPreferenceName);
		String sPreferenceValue="";
		try {
			context=getAuthenticatedContext(req, false);
			
			if(UIUtil.isNotNullAndNotEmpty(sPreferenceName)) {
				
				sPreferenceValue=prefManagement.getPreferenceValue(context, sPreferenceName);
				if(UIUtil.isNullOrEmpty(sPreferenceValue))
					sPreferenceValue=DataConstants.PREFERENCE_NOT_SET_MESSAGE;
			}
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> sPreferenceValue::"+sPreferenceValue);
		} catch (Exception ex) {
			return Response.status(500).entity(ex.getMessage()).build();
		}
		return Response.status(200).entity(sPreferenceValue).build();
	}
	
	@GET
	@Path("/setPreferenceValue")
	public Response setPrefererenceValueOnUser(@Context HttpServletRequest req,
			@DefaultValue("") @QueryParam("PreferenceName") String sPreferenceName,
			@DefaultValue("") @QueryParam("PreferenceValue") String sPreferenceValue) {
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> START of setPrefererenceValueOnUser method");
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>sPreferenceName::"+sPreferenceName+" sPreferenceValue::"+sPreferenceValue);
		try {
			context=getAuthenticatedContext(req, false);
			
			if(UIUtil.isNotNullAndNotEmpty(sPreferenceName)) {
				prefManagement.setPreferenceValue(context, sPreferenceName,sPreferenceValue);
			}
		} catch (Exception ex) {
			return Response.status(500).entity(ex.getMessage()).build();
		}
		return Response.status(200).entity(DataConstants.PREFERENCE_SET_SUCCESS_MESSAGE).build();
	}
	
	@GET
	@Path("/clearPreferenceValue")
	public Response clearPrefererenceValueOnUser(@Context HttpServletRequest req,
			@DefaultValue("") @QueryParam("PreferenceName") String sPreferenceName) {
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> START of clearPrefererenceValueOnUser method");
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> sPreferenceName::"+sPreferenceName);
		try {
			context=getAuthenticatedContext(req, false);
			
			if(UIUtil.isNotNullAndNotEmpty(sPreferenceName)) {
				prefManagement.clearPreferenceValue(context, sPreferenceName);
			}
		} catch (Exception ex) {
			return Response.status(500).entity(ex.getMessage()).build();
		}
		return Response.status(200).entity(DataConstants.PREFERENCE_REMOVED_SUCCESS_MESSAGE).build();
	}

	@GET
	@Path("/getDesignForList")
	public Response getDesignForList(@Context HttpServletRequest req) {
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> START of getDesignForList method");
		StringList slDesignForList;
		try {
			context=getAuthenticatedContext(req, false);
			
			slDesignForList=prefManagement.getDesignForList(context);
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> slDesignForList::"+slDesignForList);
		} catch (Exception ex) {
			return Response.status(500).entity(ex.getMessage()).build();
		}
		return Response.status(200).entity(slDesignForList.toString()).build();
	}
	
	@GET
	@Path("/getMfgMaturityStatusList")
	public Response getMfgMaturityStatusList(@Context HttpServletRequest req) {
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> START of getMfgMaturityStatusList method");
		StringList slMfgMaturityList;
		try {
			context=getAuthenticatedContext(req, false);

			slMfgMaturityList=prefManagement.getMfgMaturityStatusList(context);
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>slMfgMaturityList:::"+slMfgMaturityList);
		} catch (Exception ex) {
			return Response.status(500).entity(ex.getMessage()).build();
		}
		return Response.status(200).entity(slMfgMaturityList.toString()).build();
	}
	
	@GET
	@Path("/getDefaultIPClassificationList")
	public Response getDefaultIPClassificationList(@Context HttpServletRequest req) {
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> START of getDefaultIPClassificationList method");
		StringList slDefaultIPClassificationList;
		try {
			context=getAuthenticatedContext(req, false);
			
			slDefaultIPClassificationList=prefManagement.getDefaultIPClassificationList(context);
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> slDefaultIPClassificationList::"+slDefaultIPClassificationList);
		} catch (Exception ex) {
			return Response.status(500).entity(ex.getMessage()).build();
		}
		return Response.status(200).entity(slDefaultIPClassificationList.toString()).build();
	}
	
	@GET
	@Path("/checkPreferencesSet")
	public Response checkPreferencesSet(@Context HttpServletRequest req,@DefaultValue("") @QueryParam("PreferenceNames") String sPreferenceNames) {
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> START of checkPreferencesSet method");
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>sPreferenceNames::"+sPreferenceNames);
		String sPreferenceValue="";
		try {
			context=getAuthenticatedContext(req, false);
			
			if(UIUtil.isNotNullAndNotEmpty(sPreferenceNames)) {
				
				StringList slPrefList=StringUtil.split(sPreferenceNames, DataConstants.SEPARATOR_TILDE);
				sPreferenceValue=prefManagement.getUserPreferenceValues(context, slPrefList);
			}
		} catch (Exception ex) {
			return Response.status(500).entity(ex.getMessage()).build();
		}
		return Response.status(200).entity(sPreferenceValue).build();
				
	}		
				
				
	@GET
	@Path("/checkDefaultRequiredPreferences")
	public Response checkDefaultRequiredPreferences(@Context HttpServletRequest req,@DefaultValue("false") @QueryParam("isLPDUser") boolean isApolloUser) {
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> START of checkDefaultRequiredPreferences method");
		StringBuilder sbResult=new StringBuilder();
		int iStatusCode=0;
		try {
				context=getAuthenticatedContext(req, false);
				
				StringList slPrefList=new StringList(4);
				slPrefList.add(DataConstants.PREFERENCE_DESIGN_FOR);
				slPrefList.add(DataConstants.PREF_BU);
				slPrefList.add(DataConstants.PREF_HIR);
				slPrefList.add(DataConstants.PREF_DEFAULT_IP_CLASSIFICATION);
				
				String sPreferenceValue=prefManagement.getUserPreferenceValues(context, slPrefList);
				
				VPLMIntegTraceUtil.trace(context, ">>>>>>isApolloUser::"+isApolloUser);
				
				if(UIUtil.isNotNullAndNotEmpty(sPreferenceValue)) {
					StringList slPrefValueList=StringUtil.split(sPreferenceValue, DataConstants.SEPARATOR_AT_THE_RATE);
					VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>slPrefValueList::"+slPrefValueList);
					
					String strPrefDetail;
					StringList slPrefDetailList;
					String strPrefName;
					String strPrefValue;
					StringBuilder sbErrorMessage=new StringBuilder();
					boolean bLPDUserIPClassMessage=false;
					
					for(int i=0;i<slPrefValueList.size();i++) {
						strPrefDetail=slPrefValueList.get(i);
						slPrefDetailList=StringUtil.split(strPrefDetail, DataConstants.SEPARATOR_COLON);
						
						strPrefName=slPrefDetailList.get(0);
						strPrefValue=slPrefDetailList.get(1);
						VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>strPrefName::"+strPrefName+" strPrefValue::"+strPrefValue);
						
						//if Apollo user, then HiR preference is mandatory
						if(isApolloUser && (DataConstants.PREF_HIR.equalsIgnoreCase(strPrefName) || DataConstants.PREF_BU.equalsIgnoreCase(strPrefName)) 
								&& UIUtil.isNullOrEmpty(strPrefValue) && !bLPDUserIPClassMessage) {
							if(UIUtil.isNotNullAndNotEmpty(sbErrorMessage.toString()))
								sbErrorMessage.append(DataConstants.CONSTANT_NEW_LINE);
							sbErrorMessage.append(DataConstants.customCATIAHomePage.ERROR_400_LPD_IP_CLASS_NOT_SET.getMessage());
							bLPDUserIPClassMessage=true;
						}
						else if(!isApolloUser && DataConstants.PREF_BU.equalsIgnoreCase(strPrefName) && UIUtil.isNullOrEmpty(strPrefValue)) {
							if(UIUtil.isNotNullAndNotEmpty(sbErrorMessage.toString()))
								sbErrorMessage.append(DataConstants.CONSTANT_NEW_LINE);
							sbErrorMessage.append(DataConstants.customCATIAHomePage.ERROR_400_PKG_IP_CLASS_NOT_SET.getMessage());
						}
					
						if(DataConstants.PREF_DEFAULT_IP_CLASSIFICATION.equalsIgnoreCase(strPrefName) && UIUtil.isNullOrEmpty(strPrefValue)) {
							if(UIUtil.isNotNullAndNotEmpty(sbErrorMessage.toString()))
								sbErrorMessage.append(DataConstants.CONSTANT_NEW_LINE);
							sbErrorMessage.append(DataConstants.customCATIAHomePage.ERROR_400_DEFAULT_CLASSIFICATION_NOT_SET.getMessage());
						}
						if(DataConstants.PREFERENCE_DESIGN_FOR.equalsIgnoreCase(strPrefName) && UIUtil.isNullOrEmpty(strPrefValue)) {
							if(UIUtil.isNotNullAndNotEmpty(sbErrorMessage.toString()))
								sbErrorMessage.append(DataConstants.CONSTANT_NEW_LINE);
							sbErrorMessage.append(DataConstants.customCATIAHomePage.ERROR_400_DESIGN_FOR_NOT_SET.getMessage());
						}
					}
					
					if(UIUtil.isNotNullAndNotEmpty(sbErrorMessage.toString())) {
						sbResult.append(DataConstants.customCATIAHomePage.ERROR_400_PREFERENCES_NOT_SET.getMessage());
						sbResult.append(DataConstants.CONSTANT_NEW_LINE);
						sbResult.append(sbErrorMessage.toString());
						iStatusCode=DataConstants.customCATIAHomePage.ERROR_400_PREFERENCES_NOT_SET.getCode();
					}else {
						sbResult.append(DataConstants.customCATIAHomePage.MESSAGE_200_PREFERENCES_SET.getMessage());
						iStatusCode=DataConstants.customCATIAHomePage.MESSAGE_200_PREFERENCES_SET.getCode();
					}
				}
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> return string from WS::"+sbResult.toString()+" status code::"+iStatusCode);
		} catch (Exception ex) {
			return Response.status(500).entity(ex.getMessage()).build();
		}
		return Response.status(iStatusCode).entity(sbResult.toString()).build();
	}

}
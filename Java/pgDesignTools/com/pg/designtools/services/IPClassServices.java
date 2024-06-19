package com.pg.designtools.services;

import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import com.dassault_systemes.platform.restServices.RestService;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.designtools.util.IPManagement;
import matrix.util.StringList;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;

@Path("/IPClassServices")
public class IPClassServices extends RestService {

	protected matrix.db.Context context; 
	
	@GET
	@Path("/getIPControlClassList")
	public Response getIPControlClassList(@Context HttpServletRequest req) {

		String sFinalIPClassList="";
		try {
			context = getAuthenticatedContext(req,false);

	        String sOrgDataName="";
	        String sOrgDataRev="";
	        String sOrgDataType="";
	        String sRepDataType="";
	        String sRepDataName="";
	        String sRepDataRev="";
	        
			String queryString = req.getQueryString();
			queryString =  java.net.URLDecoder.decode(queryString,"UTF-8");
			String[] urlParam;
			
			if (UIUtil.isNotNullAndNotEmpty(queryString)) {
				urlParam = queryString.split("&");
				
				if (urlParam.length > 0) {
					sOrgDataType=urlParam[0];
					sOrgDataName=urlParam[1];
					sOrgDataRev=urlParam[2];
					sRepDataType=urlParam[3];
					sRepDataName=urlParam[4];
					sRepDataRev=urlParam[5];
					
					IPManagement ipManagement=new IPManagement(context);
					String sOrgDataIPClassList=ipManagement.getConnectedIPClasses(context,sOrgDataType,sOrgDataName,sOrgDataRev);
					
					String sRepDataIPClassList=ipManagement.getConnectedIPClasses(context,sRepDataType,sRepDataName,sRepDataRev);
					
					//Invoke method to decide which IP class list should be attached to the Resulting data
					sFinalIPClassList=ipManagement.compareIPClassList(context,sRepDataType,sOrgDataIPClassList, sRepDataIPClassList);
				}
			}
		} catch (Exception ex) {
			return Response.status(500).entity("Error").build();
		}
		return Response.status(200).entity(sFinalIPClassList).build();
	}
	
	@GET
	@Path("/connectIPClasses")
	public Response connectIPClasses(@Context HttpServletRequest req){

		try {
			context = getAuthenticatedContext(req,false);
			String queryString = req.getQueryString();
			queryString =  java.net.URLDecoder.decode(queryString,"UTF-8");
			String[] urlParam;
			String sType="";
			String sName="";
			String sRev="";
			String sIPClassList="";
			
			if (UIUtil.isNotNullAndNotEmpty(queryString)) {
				urlParam = queryString.split("&");
				
				if (urlParam.length > 0) {
					sType=urlParam[0];
					sName=urlParam[1];
					sRev=urlParam[2];
					sIPClassList=urlParam[3];
					
					if(UIUtil.isNotNullAndNotEmpty(sIPClassList)) {
						IPManagement ipManagement=new IPManagement(context);
						ipManagement.connectIPClassesToObject(context, sType, sName, sRev, sIPClassList);
					}
				}
			}
		
		} catch (Exception ex) {
			return Response.status(500).entity("Error").build();
		}
		return Response.status(200).entity("Success").build();
	}
	
	@GET
	@Path("/getPreferenceValue")
	public Response getPrefererenceValueForUser(@Context HttpServletRequest req,
			@DefaultValue("") @QueryParam("PreferenceName") String sPreferenceName) {
		String sPreferenceValue="";
		try {
			context=getAuthenticatedContext(req, false);
			
			if(UIUtil.isNotNullAndNotEmpty(sPreferenceName)) {
				IPManagement ipManagement=new IPManagement(context);
				sPreferenceValue=ipManagement.getPreferenceValue(context, sPreferenceName);
			}
		} catch (Exception ex) {
			return Response.status(500).entity("Error").build();
		}
		return Response.status(200).entity(sPreferenceValue).build();
	}
	
	@GET
	@Path("/setPreferenceValue")
	public Response setPrefererenceValueOnUser(@Context HttpServletRequest req,
			@DefaultValue("") @QueryParam("PreferenceName") String sPreferenceName,
			@DefaultValue("") @QueryParam("PreferenceValue") String sPreferenceValue) {
		try {
			context=getAuthenticatedContext(req, false);
			
			if(UIUtil.isNotNullAndNotEmpty(sPreferenceName)) {
				IPManagement ipManagement=new IPManagement(context);
				ipManagement.setPreferenceValue(context, sPreferenceName,sPreferenceValue);
			}
		} catch (Exception ex) {
			return Response.status(500).entity("Error").build();
		}
		return Response.status(200).entity("Preference set successfully").build();
	}
	
	@GET
	@Path("/clearPreferenceValue")
	public Response clearPrefererenceValueOnUser(@Context HttpServletRequest req,
			@DefaultValue("") @QueryParam("PreferenceName") String sPreferenceName) {
		try {
			context=getAuthenticatedContext(req, false);
			
			if(UIUtil.isNotNullAndNotEmpty(sPreferenceName)) {
				IPManagement ipManagement=new IPManagement(context);
				ipManagement.clearPreferenceValue(context, sPreferenceName);
			}
		} catch (Exception ex) {
			return Response.status(500).entity("Error").build();
		}
		return Response.status(200).entity("Preference removed successfully").build();
	}
	
	@GET
	@Path("/getBUIPControlClasses")
	public Response getBUIPControlClasses(@Context HttpServletRequest req) {
		StringList slSecurityCategoryList;
		try {
			context=getAuthenticatedContext(req, false);
			
			IPManagement ipManagement=new IPManagement(context);
			slSecurityCategoryList=ipManagement.getSecurityCategoryListForBU(context);
		} catch (Exception ex) {
			return Response.status(500).entity("Error").build();
		}
		return Response.status(200).entity(slSecurityCategoryList).build();
	}
	
	@GET
	@Path("/getHiRIPControlClasses")
	public Response getHiRIPControlClasses(@Context HttpServletRequest req) {
		StringList slSecurityCategoryList;
		try {
			context=getAuthenticatedContext(req, false);
			
			IPManagement ipManagement=new IPManagement(context);
			slSecurityCategoryList=ipManagement.getSecurityCategoryListForHiR(context);
		} catch (Exception ex) {
			return Response.status(500).entity("Error").build();
		}
		return Response.status(200).entity(slSecurityCategoryList).build();
	}
	
	@GET
	@Path("/getDesignForList")
	public Response getDesignForList(@Context HttpServletRequest req) {
		StringList slDesignForList;
		try {
			context=getAuthenticatedContext(req, false);
			
			IPManagement ipManagement=new IPManagement(context);
			slDesignForList=ipManagement.getDesignForList(context);
		} catch (Exception ex) {
			return Response.status(500).entity("Error").build();
		}
		return Response.status(200).entity(slDesignForList.toString()).build();
	}
	
	@GET
	@Path("/getMfgMaturityStatusList")
	public Response getMfgMaturityStatusList(@Context HttpServletRequest req) {
		StringList slMfgMaturityList;
		try {
			context=getAuthenticatedContext(req, false);
			
			IPManagement ipManagement=new IPManagement(context);
			slMfgMaturityList=ipManagement.getMfgMaturityStatusList(context);
		} catch (Exception ex) {
			return Response.status(500).entity("Error").build();
		}
		return Response.status(200).entity(slMfgMaturityList.toString()).build();
	}
	
	@GET
	@Path("/getDefaultIPClassificationList")
	public Response getDefaultIPClassificationList(@Context HttpServletRequest req) {
		StringList slDefaultIPClassificationList;
		try {
			context=getAuthenticatedContext(req, false);
			
			IPManagement ipManagement=new IPManagement(context);
			slDefaultIPClassificationList=ipManagement.getDefaultIPClassificationList(context);
		} catch (Exception ex) {
			return Response.status(500).entity("Error").build();
		}
		return Response.status(200).entity(slDefaultIPClassificationList.toString()).build();
	}
}
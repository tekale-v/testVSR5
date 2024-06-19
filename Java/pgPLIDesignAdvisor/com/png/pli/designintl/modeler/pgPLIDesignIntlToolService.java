package com.png.pli.designintl.modeler;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

//ADDED FOR DEFECT 55836 2022X CW4 START
import com.matrixone.apps.awl.util.BusinessUtil;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.AccessUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkUtil;
//ADDED FOR DEFECT 55434 2022X CW4 END
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.json.JSONObject;
import com.matrixone.servlet.Framework;

import matrix.db.AccessList;
import matrix.db.BusinessObject;
import matrix.db.BusinessObjectList;
import matrix.db.Context;
import matrix.db.JPO;
//ADDED FOR DEFECT 55836 2022X CW4 END
//ADDED FOR DEFECT 55434 2022X CW4 START
import matrix.util.Pattern;
import matrix.util.StringList;

@Path("/service")
//@POST
@Produces({"application/json", "application/ds-json"})
public class pgPLIDesignIntlToolService {


	@javax.ws.rs.core.Context HttpServletRequest request;	
	private static final Logger logger = Logger.getLogger("com.png.pli.designintl.modeler.pgPLIDesignIntlToolService");
	//Added for 57394-Accept Suggestion does not work for Expiration Date field on Raw Material -START
	protected static final String DELIMITER = "^";
	//Added for 57394-Accept Suggestion does not work for Expiration Date field on Raw Material -END
	@GET
	@Path("/generateuidata")
	@Produces({"application/json", "application/ds-json"})	
	public Response generateUIData(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest, @QueryParam("objectId") String strObjId, @QueryParam("cmd") String strCmd) throws Exception 
	{

		matrix.db.Context context = null;
		String strJSONInputContent = "";
		Map programMap = new HashMap();

		if ((paramHttpServletRequest == null) || (!paramHttpServletRequest.getMethod().equals("GET"))) {
			throw new Exception("Invalid service call!");
		}		
		JSONObject localJSONObject =null;
		matrix.db.Context localContext = null;

		try
		{

			if (Framework.isLoggedIn((HttpServletRequest) paramHttpServletRequest)) 
			{
				context = Framework.getContext((HttpSession) paramHttpServletRequest.getSession(false));
			}
			programMap.put("strObjId",strObjId);
			programMap.put("strCmd", strCmd);
			String[] methodargs = JPO.packArgs(programMap);
			strJSONInputContent = (String) JPO.invoke(context, "pgDesignIntelegenceGUI", null, "generateUIData",methodargs, String.class);
			DomainObject doAPPbj = DomainObject.newInstance(context, strObjId);
			localJSONObject = new JSONObject();

		}catch(Exception e)
		{
			throw e;
		}

		return Response.ok(strJSONInputContent).build();


	}


	@GET
	@Path("/validatedidisplaydata")
	@Produces({"application/json", "application/ds-json"})	
	public Response validateDIDisplayData(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest, @QueryParam("objectId") String strObjId, @QueryParam("cmd") String strCmd) throws Exception 
	{

		matrix.db.Context context = null;
		String strJSONInputContent = "";
		Map programMap = new HashMap();

		if ((paramHttpServletRequest == null) || (!paramHttpServletRequest.getMethod().equals("GET"))) {
			throw new Exception("Invalid service call!");
		}		
		JSONObject localJSONObject =null;
		matrix.db.Context localContext = null;

		try
		{

			if (Framework.isLoggedIn((HttpServletRequest) paramHttpServletRequest)) 
			{
				context = Framework.getContext((HttpSession) paramHttpServletRequest.getSession(false));
			}
			programMap.put("strObjId",strObjId);
			programMap.put("strCmd", strCmd);
			String[] methodargs = JPO.packArgs(programMap);
			strJSONInputContent = (String) JPO.invoke(context, "pgDesignIntelegenceGUI", null, "validateDIDisplayData",methodargs, String.class);

		}catch(Exception e)
		{
			throw e;
		}

		return Response.ok(strJSONInputContent).build();


	}


	@GET
	@Path("/setpanelpreference")
	@Produces({"application/json", "application/ds-json"})	
	public void setPLISession(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest, @QueryParam("status") String strStatus,@QueryParam("objectId") String strObjId) throws Exception 
	{
		try
		{
			HttpSession session = request.getSession(true) ;
			String strCMDName = request.getParameter("portalCmdName");
			String CONSTANTSPLIPANELSTATUS = "PLIPanelStatus".concat("_").concat(strCMDName);
			String strThisSessionStatus = (String)session.getAttribute(CONSTANTSPLIPANELSTATUS);
			if(null==strThisSessionStatus || "".equals(strThisSessionStatus))
			{	
				session.setAttribute(CONSTANTSPLIPANELSTATUS,strStatus);

			}else{
				session.removeAttribute(CONSTANTSPLIPANELSTATUS);
				session.setAttribute(CONSTANTSPLIPANELSTATUS,strStatus);										

			}


		}catch(Exception e)
		{
			throw e;
		}


	}	

	@GET
	@Path("/getpanelpreference")
	@Produces({"application/json", "application/ds-json"})	
	public Response getPLISession(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,@QueryParam("ObjectType") String strType) throws Exception 
	{
		String strPanelStatus = "";
		try
		{
			HttpSession session = request.getSession(true) ;
			String strCMDName = request.getParameter("portalCmdName");
			String strStatus = (String)session.getAttribute("PLIPanelStatus".concat("_").concat(strCMDName));
			if(null==strStatus || "".equals(strStatus))
			{
				strStatus = (String)session.getAttribute("PLIDefaultGlobalPanelStatus");
			}
			if(null==strStatus || "".equals(strStatus))
			{
				matrix.db.Context context = null;
				if (Framework.isLoggedIn((HttpServletRequest) paramHttpServletRequest)) 
				{
					context = Framework.getContext((HttpSession) paramHttpServletRequest.getSession(false));
				}
				strStatus = (String) JPO.invoke(context, "pgDesignIntelegenceGUI", null, "getPLIPanelPreference",null, String.class);
				session.setAttribute("PLIDefaultGlobalPanelStatus",strStatus);
			}
			strPanelStatus = "{\"status\":\""+strStatus+"\"}";
		}catch(Exception e)
		{
			throw e;
		}
		return Response.ok(strPanelStatus).build();
	}	




	@GET
	@Path("/getdisclaimerpreference")
	@Produces({"application/json", "application/ds-json"})	
	public Response getDisclaimerVisibility(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest) throws Exception 
	{
		String strDisclaimerVisibility = "";
		try
		{
			HttpSession session = request.getSession(true) ;
			String strStatus = (String)session.getAttribute("DisclaimerVisibility");
			if(null==strStatus || "".equals(strStatus))strStatus = "header expanded";
			strDisclaimerVisibility = "{\"status\":\""+strStatus+"\"}";
		}catch(Exception e)
		{
			throw e;
		}
		return Response.ok(strDisclaimerVisibility).build();
	}



	@GET
	@Path("/setdisclaimerpreference")
	@Produces({"application/json", "application/ds-json"})	
	public void setDisclaimerVisibility(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest, @QueryParam("status") String strStatus) throws Exception 
	{
		try
		{
			HttpSession session = request.getSession(true) ;
			String strThisSessionStatus = (String)session.getAttribute("DisclaimerVisibility");
			if(null==strThisSessionStatus || "".equals(strThisSessionStatus))
			{	
				session.setAttribute("DisclaimerVisibility",strStatus);

			}else{
				session.removeAttribute("DisclaimerVisibility");
				session.setAttribute("DisclaimerVisibility",strStatus);

			}


		}catch(Exception e)
		{
			throw e;
		}


	}

	@POST
	@Path("/logInteraction")
	public Response logInteraction(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest, String strInteractionJSON) throws Exception 
	{
		Context context = null;
		Map programMap = new HashMap();
		int iStatus = 1;
		try
		{
			if ((paramHttpServletRequest == null) || (!"POST".equals(paramHttpServletRequest.getMethod()))) {
				throw new Exception("Invalid service call!");
			}

			if (UIUtil.isNotNullAndNotEmpty(strInteractionJSON) && Framework.isLoggedIn((HttpServletRequest) paramHttpServletRequest)) 
			{
				context = Framework.getContext((HttpSession) paramHttpServletRequest.getSession(false));
				strInteractionJSON = strInteractionJSON.replace("\\\"","\"");
				strInteractionJSON = strInteractionJSON.replace("\"{","{");
				strInteractionJSON = strInteractionJSON.replace("}\"","}");
				programMap.put("strJSONInteraction", strInteractionJSON);
				String[] methodargs = JPO.packArgs(programMap);
				iStatus = JPO.invoke(context, "pgDesignIntelegenceGUI", null, "sendInteractionJSONToWS",methodargs);
			}
			System.out.println("pgPLIDesignIntlToolService : InteractionJSON - "+strInteractionJSON+" , InvocationStatus - "+iStatus);		
		}catch(Exception e)
		{
			System.out.println("Exception:::pgPLIDesignIntlToolService : InteractionJSON - "+strInteractionJSON+" , InvocationStatus - "+iStatus);
		}
		return Response.ok(strInteractionJSON).build();	
	}

	@POST
	@Path("/setcriticaldata")
	@Produces({"application/json", "application/ds-json"})
	public Response setCriticalData(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest, @QueryParam("objectId") String strObjId, @QueryParam("isCritical") String isCritical)
	{
		matrix.db.Context context = null;
		if (paramHttpServletRequest != null && "POST".equals(paramHttpServletRequest.getMethod()))
		{
			try
			{
				if (Framework.isLoggedIn((HttpServletRequest) paramHttpServletRequest)) 
				{ 
					context = Framework.getContext((HttpSession) paramHttpServletRequest.getSession(false));
				}
				DomainObject domObj =  DomainObject.newInstance(context,strObjId);

				//ADDED FOR DEFECT 55836 2022X CW4 START
				String strGrantee = null;
				BusinessObjectList boList = null;
				matrix.db.Access access = null;
				AccessList accessList = null;
				BusinessObject busObj = null;				
				//ADDED FOR DEFECT 55836 2022X CW4 END

				if(null != context && ((UIUtil.isNotNullAndNotEmpty(isCritical) && "true".equalsIgnoreCase(isCritical)) || (UIUtil.isNullOrEmpty(isCritical) || (UIUtil.isNotNullAndNotEmpty(isCritical) && "false".equalsIgnoreCase(isCritical)))))
				{
					//ADDED FOR DEFECT 55836 2022X CW4 START
					strGrantee = context.getUser();
					busObj = (BusinessObject)domObj;				
					if(!busObj.getAccessMask(context).hasModifyAccess()){						
						access = new matrix.db.Access();
						boList = new BusinessObjectList();
						boList.add(busObj);
						access.setModifyAccess(true);
						access.setUser(strGrantee);
						accessList = new AccessList(access);
						AccessUtil.grantAccess(context, boList, accessList,PropertyUtil.getSchemaProperty("person_UserAgent"));					
					}
					//ADDED FOR DEFECT 55836 2022X CW4 END

					domObj.setAttributeValue(context, "pgPLISpecAdvHardStopBlock", UIUtil.isNullOrEmpty(isCritical)?"FALSE":isCritical.toUpperCase());

					//ADDED FOR DEFECT 55836 2022X CW4 START
					if(BusinessUtil.isNotNullOrEmpty(boList)){
						AccessUtil.revokeAccess(context, boList, new StringList(strGrantee));
						if(null != boList)
							boList.clear();
					}
					//ADDED FOR DEFECT 55836 2022X CW4 END
				}

			}catch(Exception e)
			{ 
				logger.info("Exception occurred : Class pgPLIDesignIntlToolService Method setCriticalData");
			}
		}
		return Response.ok().build();
	}

	@POST
	@Path("/setsuggestion")
	@Produces({"application/json", "application/ds-json"})	
	public Response setSuggestion(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest, @QueryParam("objectId") String strObjId, @QueryParam("val") String strAcceptOrReject, String strSuggestionData) throws Exception 
	{
		matrix.db.Context context = null;
		StringBuilder sbReturnValue = new StringBuilder();
		if (paramHttpServletRequest != null && "POST".equals(paramHttpServletRequest.getMethod()))
		{
			try
			{
				String sAttribute,sOldVlaue,sNewValue,sAcceptSuggestion,sLabel;
				if(Framework.isLoggedIn((HttpServletRequest) paramHttpServletRequest))
				{
					context = Framework.getContext((HttpSession) paramHttpServletRequest.getSession(false));
				}
				String[] strSuggestionArray = strSuggestionData.split("\\|");
				int iArrayLen = strSuggestionArray.length;
				String[] strAttrArray = new String[iArrayLen];
				if(UIUtil.isNotNullAndNotEmpty(strAcceptOrReject))
				{
					String[] sAcceptArray;
					HashMap hmAttrValMap;
					boolean bInvalidValue = false;
					Map<String,String> mAttrKeyValMap;
					Map<String,String> mAttrKeyLabelMap = new HashMap<String,String>();
					for(int i = 0; i < iArrayLen; i++)
					{
						sAcceptSuggestion = strSuggestionArray[i];
						//Modified for 57394-Accept Suggestion does not work for Expiration Date field on Raw Material -START
						sAcceptArray = sAcceptSuggestion.split("\\^");
						//Modified for 57394-Accept Suggestion does not work for Expiration Date field on Raw Material -END
						sAttribute = sAcceptArray[0];
						strAttrArray[i] = sAttribute;
					}
					hmAttrValMap = (HashMap) JPO.invoke(context, "pgDesignIntelegenceGUI", null, "getCorrespondingPageAttributeName", strAttrArray, HashMap.class);  //customer_units_per_layer,pgCustomerUnitsPerLayerInteger

					mAttrKeyValMap = new HashMap<String,String>();
					for( int i = 0; i < iArrayLen; i++)
					{
						sAcceptSuggestion = strSuggestionArray[i];
						//Modified for 57394-Accept Suggestion does not work for Expiration Date field on Raw Material -START
						sAcceptArray = sAcceptSuggestion.split("\\^");
						//Modified for 57394-Accept Suggestion does not work for Expiration Date field on Raw Material -END
						sAttribute = sAcceptArray[0];
						if(sAcceptArray.length > 1)
							sOldVlaue = sAcceptArray[1];
						else sOldVlaue = DomainConstants.EMPTY_STRING;
						if(sAcceptArray.length > 2)
							sNewValue = sAcceptArray[2];
						else sNewValue = DomainConstants.EMPTY_STRING;
						if(sAcceptArray.length > 3 && !"undefined".equalsIgnoreCase(sAcceptArray[3]))
							sLabel = sAcceptArray[3];
						else sLabel = DomainConstants.EMPTY_STRING;		
						String[] strAttrArr = new String[5];
						strAttrArr[0] = sAttribute;
						strAttrArr[1] = sOldVlaue;
						strAttrArr[2] = sNewValue;
						strAttrArr[3] = sLabel;
						strAttrArr[4] = strObjId;
						bInvalidValue =  (boolean) JPO.invoke(context, "pgDesignIntelegenceGUI", null, "getCorrespondingPagePickList", strAttrArr, boolean.class); 
						if(bInvalidValue)
							return Response.status(Response.Status.BAD_REQUEST).entity("Invalid input").build();
						mAttrKeyLabelMap.put((String)hmAttrValMap.get(sAttribute),sLabel); 
						if("Accept".equalsIgnoreCase(strAcceptOrReject))
						{
							mAttrKeyValMap.put((String)hmAttrValMap.get(sAttribute),sNewValue); 
						}
						else
						{
							mAttrKeyValMap.put((String)hmAttrValMap.get(sAttribute),sOldVlaue);
						}
					}
					sbReturnValue = new StringBuilder();

					processAcceptSuggestion(context, mAttrKeyValMap, strObjId, sbReturnValue, mAttrKeyLabelMap);
				}
			}
			catch(Exception e)
			{
				throw e;
			}
		}
		return Response.ok(sbReturnValue.toString()).build();
	}

	/*
	 * Method to process Accept Suggestion JSON input
	 * @param Context context
	 * @param DomainObject domObj
	 * @param Map<String,String> mAttrKeyValMap
	 * @param String strObjId
	 * @param StringBuilder sbReturnValue
	 * @returns void
	 * @throws Exception
	 */
	public void processAcceptSuggestion(Context context, Map<String,String> mAttrKeyValMap, String strObjId, StringBuilder sbReturnValue, Map<String,String> mAttrKeyLabelMap) throws Exception
	{
		Map<String,String> mRelKeyValMap = new HashMap<String,String>();
		StringList slRelSuggestions = new StringList();
		String strLabel;
		for (String strReceivedAttrName : mAttrKeyValMap.keySet()) {
			if(UIUtil.isNotNullAndNotEmpty(strReceivedAttrName))
			{
				if(strReceivedAttrName.startsWith("from[") || strReceivedAttrName.startsWith("to["))
				{
					mRelKeyValMap.put(strReceivedAttrName,mAttrKeyValMap.get(strReceivedAttrName));
					slRelSuggestions.add(strReceivedAttrName);
				}
				else
				{
					strLabel = UIUtil.isNotNullAndNotEmpty(mAttrKeyLabelMap.get(strReceivedAttrName))?mAttrKeyLabelMap.get(strReceivedAttrName):EnoviaResourceBundle.getAttributeI18NString(context, strReceivedAttrName, context.getSession().getLanguage());
					//Modified for 57394-Accept Suggestion does not work for Expiration Date field on Raw Material -START
					if(sbReturnValue.length()>0)
						sbReturnValue.append("~".concat(strLabel.concat(DELIMITER.concat(mAttrKeyValMap.get(strReceivedAttrName)))));
					else
						sbReturnValue.append(strLabel.concat(DELIMITER.concat(mAttrKeyValMap.get(strReceivedAttrName))));   
					//Modified for 57394-Accept Suggestion does not work for Expiration Date field on Raw Material -END
				}
			}
		}
		if(slRelSuggestions.size()>0)
		{
			for(int i =0;i<slRelSuggestions.size();i++)
			{
				mAttrKeyValMap.remove(slRelSuggestions.get(i));
			}
		}
		DomainObject domObj = DomainObject.newInstance(context,strObjId);
		if(mAttrKeyValMap.size()>0)
		{
			domObj.setAttributeValues(context, mAttrKeyValMap);
		}
		if(mRelKeyValMap.size()>0)
		{
			processRelInfo(context, domObj, mRelKeyValMap, sbReturnValue, mAttrKeyLabelMap);
		}
	}
	//ADDED FOR DEFECT 55434 2022X CW4 START
	/*
	 * Method to process rel and picklist
	 * @param Context context
	 * @param DomainObject domObj
	 * @param Map<String,String> mRelKeyValMap
	 * @param StringBuilder sbReturnValue
	 * @returns void
	 * @throws Exception
	 */
	public void processRelInfo(Context context, DomainObject domObj, Map<String,String> mRelKeyValMap, StringBuilder sbReturnValue, Map<String,String> mAttrKeyLabelMap) throws Exception
	{
		String strRel;
		StringList slBusSelects = new StringList();
		StringList slRelSelects = new StringList();
		Pattern relPattern = null;
		Map<String,String> mToBeConnected = new HashMap<String,String>();
		Map<String,String> mRelLabel = new HashMap<String,String>();
		StringList slConnNames;
		int iSize;
		String strObjType, strObjName, strRelName, strRelId, strName;
		for(String strEachRelInfo : mRelKeyValMap.keySet())
		{
			strRel = strEachRelInfo.substring(strEachRelInfo.indexOf("[")+1,strEachRelInfo.indexOf("]"));
			if(null == relPattern)
			{
				relPattern = new Pattern(strRel);
			}
			else relPattern.addPattern(strRel);
			mToBeConnected.put(strRel,(String)mRelKeyValMap.get(strEachRelInfo));
			mRelLabel.put(strRel,mAttrKeyLabelMap.get(strEachRelInfo));
		}

		//Disconnect all
		slBusSelects.add(DomainConstants.SELECT_TYPE);
		slRelSelects.add(DomainConstants.SELECT_RELATIONSHIP_ID);
		slRelSelects.add(DomainConstants.SELECT_RELATIONSHIP_NAME);
		MapList mlRelInfo = new MapList();
		Map<String,String> mRelInfoMap = new HashMap<String,String>();
		if(null != relPattern)
		{
			mlRelInfo = domObj.getRelatedObjects(context,
					relPattern.getPattern(),
					DomainConstants.QUERY_WILDCARD,
					slBusSelects,
					slRelSelects,
					true,
					true,
					(short) 1,
					null, null, (short) 0);
			if(mlRelInfo.size()>0)
			{
				Iterator itRelMapList = mlRelInfo.iterator();
				StringList slConnectionToBeDisconnected = new StringList();
				Map mRelMap;
				while(itRelMapList.hasNext())
				{
					mRelMap = (Map)itRelMapList.next();
					strObjType = (String)mRelMap.get(DomainConstants.SELECT_TYPE);
					strRelName = (String)mRelMap.get(DomainConstants.SELECT_RELATIONSHIP_NAME);
					strRelId = (String)mRelMap.get(DomainConstants.SELECT_RELATIONSHIP_ID);
					slConnectionToBeDisconnected.add(strRelId);
					mRelInfoMap.put(strRelName,strObjType);
				}
				DomainRelationship.disconnect(context, BusinessUtil.toStringArray(slConnectionToBeDisconnected));
			}
			else if(mToBeConnected.size() > 0)
			{
				for (String strPLRelName : mToBeConnected.keySet())
				{
					mRelInfoMap.put(strPLRelName,strPLRelName.substring(strPLRelName.indexOf("pgPDTemplatesto")+15,strPLRelName.length()));
				}
			}
			StringBuilder sbName = new StringBuilder();
			String strLabel;
			for (Map.Entry<String,String> entry : mRelInfoMap.entrySet())
			{
				strRelName = entry.getKey();
				strObjType = entry.getValue();
				strObjName = (String)mToBeConnected.get(strRelName);
				slConnNames = FrameworkUtil.split(strObjName,",");
				iSize = slConnNames.size();
				for(int z = 0; z < iSize ;z++)
				{
					sbName.append((slConnNames.get(z)).trim());
					if(z<iSize-1)
						sbName.append(",");
				}
				strName = sbName.toString();
				findAndConnectPickList(context, domObj, strRelName, strObjType, strName);

				strLabel = UIUtil.isNotNullAndNotEmpty(mRelLabel.get(strRelName))?mRelLabel.get(strRelName):EnoviaResourceBundle.getTypeI18NString(context, strObjType, context.getSession().getLanguage());
				//Modified for 57394-Accept Suggestion does not work for Expiration Date field on Raw Material -START
				if(sbReturnValue.length()>0)
					sbReturnValue.append("~".concat(strLabel.concat(DELIMITER.concat(strName))));
				else
					sbReturnValue.append(strLabel.concat(DELIMITER.concat(strName)));
				//Modified for 57394-Accept Suggestion does not work for Expiration Date field on Raw Material -END
			}
		}
	}

	/*
	 * Method to find and connect picklist
	 * @param Context context
	 * @param DomainObject domObj
	 * @param String strRelName
	 * @param String strObjType
	 * @param String strName
	 * @returns void
	 * @throws Exception
	 */
	public void findAndConnectPickList(Context context, DomainObject domObj, String strRelName, String strObjType, String strName) throws Exception
	{
		StringBuffer sbWhereClause = new StringBuffer();
		String STATE_ACTIVE = PropertyUtil.getSchemaProperty("policy", PropertyUtil.getSchemaProperty("policy_pgPicklistItem"), "state_Active");
		String strWhereClause = (sbWhereClause.append(DomainConstants.SELECT_CURRENT).append("=='").append(STATE_ACTIVE).append("'")).toString();
		String strObjRev = "-";
		String strPLId;
		StringList slObjectSelects = new StringList();
		slObjectSelects.add(DomainConstants.SELECT_ID);
		Map mEachPL;

		MapList mlPickList = DomainObject.findObjects (context, // context
				strObjType, // Type Pattern
				strName, // Name pattern
				strObjRev, // Rev pattern
				DomainConstants.QUERY_WILDCARD, // owner pattern
				PropertyUtil.getSchemaProperty("vault_eServiceProduction"), // vault pattern
				strWhereClause, // bus where expression
				DomainConstants.EMPTY_STRING, // rel where expression
				false, // expand type
				slObjectSelects, // bus selects
				(short)0, // limit
				new StringList()); //Multi Select values

		for (int i = 0 ; i < mlPickList.size() ; i++)
		{
			mEachPL = (Map) mlPickList.get(i);
			strPLId = (String)mEachPL.get(DomainConstants.SELECT_ID);
			connectPickList(context, domObj, strRelName, strPLId);				
		}
	}

	/*
	 * Method to find and connect picklist
	 * @param Context context
	 * @param DomainObject domObj
	 * @param String strRelName
	 * @param String strPLId
	 * @returns void
	 * @throws Exception
	 */
	public void connectPickList(Context context, DomainObject domObj, String strRelName, String strPLId) throws Exception
	{
		DomainRelationship.connect(context, domObj, strRelName, DomainObject.newInstance(context, strPLId));
	}
	//ADDED FOR DEFECT 55434 2022X CW4 END
}

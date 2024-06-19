/*
 * Added by APOLLO Team
 * For CATIA Automation Web Services
 */

package com.png.apollo.designtool.getData;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.matrixone.apps.cpn.CPNCommonConstants;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.Job;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.domain.util.eMatrixDateFormat;
import com.matrixone.apps.engineering.EngineeringConstants;
import com.matrixone.apps.engineering.Part;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.servlet.Framework;
import com.pg.v3.custom.pgV3Constants;
import com.png.apollo.pgApolloCommonUtil;
import com.png.apollo.pgApolloConstants;

import matrix.db.BusinessInterface;
import matrix.db.BusinessObject;
import matrix.db.BusinessObjectWithSelect;
import matrix.db.BusinessObjectWithSelectList;
import matrix.db.JPO;
import matrix.db.PathQuery;
import matrix.db.PathQueryIterator;
import matrix.db.PathWithSelect;
import matrix.db.PathWithSelectList;
import matrix.db.RelationshipType;
import matrix.util.MatrixException;
import matrix.util.StringList;

@Path("/fetchData")
public class ExtractAPPFORVPMReference {

	 private static final Logger loggerWS = LoggerFactory.getLogger("APOLLOWS");
	 private static final org.apache.log4j.Logger loggerApolloTrace = org.apache.log4j.Logger.getLogger(ExtractAPPFORVPMReference.class);
	@GET
	@Path("/getAPPName")
	public Response getAPPName(@Context HttpServletRequest req) throws Exception {		
	
		matrix.db.Context context = null;
		String[] URLParam = new String[20];
		String strType = pgApolloConstants.TYPE_VPMREFERENCE;
		String strObjectName = DomainConstants.EMPTY_STRING;
		String strObjectRevision = DomainConstants.EMPTY_STRING;
		String strAPP = DomainConstants.EMPTY_STRING;

		StringBuffer sbReturnMessages = new StringBuffer();
		BusinessObject VPMREFERENCEBO = null;
		boolean bVPPMReferenceOpen = false;
		boolean bContextPushed =false;

		StringList objectSelects = new StringList();
		objectSelects.add(DomainConstants.SELECT_NAME);

		MapList mlRelatedAPP = new MapList();

		try {

			loggerWS.debug("******************************************************************************");
			loggerWS.debug("Path : /fetchData/getAPPName");
			loggerWS.debug("Method: ExtractAPPFORVPMReference : getAPPName");
			loggerWS.debug("\n\n New Extract started : {}" , new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) );

			// Get the user context
			if (Framework.isLoggedIn((HttpServletRequest) req)) {
				context = Framework.getContext((HttpSession) req.getSession(false));				
			}

			loggerWS.debug("context  : {}" , context);

			//Pushing the context to User Agent
			ContextUtil.pushContext(context,pgApolloConstants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
			bContextPushed = true;
			String queryString = req.getQueryString();
			queryString =  java.net.URLDecoder.decode(queryString,"UTF-8");
			
			loggerWS.debug("\n\nQueryString  : {}" , queryString);

			if (UIUtil.isNotNullAndNotEmpty(queryString)) {
				URLParam = queryString.split("&");				

				if (URLParam.length > 0) {

					strObjectName = URLParam[0];
					strObjectName = strObjectName.trim();
					if(strObjectName.contains("%20"))
						strObjectName = strObjectName.replace("%20", " ");
					loggerWS.debug("\n\nstrObjectName_New : {}" , strObjectName);

					strObjectRevision = URLParam[1];
					strObjectRevision = strObjectRevision.trim();
					loggerWS.debug("\n\nstrObjectRevision : {}" , strObjectRevision);

					VPMREFERENCEBO = new BusinessObject(strType, strObjectName,strObjectRevision, pgApolloConstants.VAULT_VPLM);	

					loggerWS.debug("\n\n VPMREFERENCEBO-----------------{}" , VPMREFERENCEBO);

					boolean bPLMVPMREFERENCEBOExists = VPMREFERENCEBO.exists(context);

					loggerWS.debug("\n\bPLMVPMREFERENCEBOExists  : {}" , bPLMVPMREFERENCEBOExists);

					if(bPLMVPMREFERENCEBOExists) {
						VPMREFERENCEBO.open(context);
						bVPPMReferenceOpen = true;
						DomainObject domVPMReference = DomainObject.newInstance(context,VPMREFERENCEBO);
						loggerWS.debug("\n\ndomVPMReference  : {}" , domVPMReference);

						mlRelatedAPP = domVPMReference.getRelatedObjects(context,
								DomainConstants.RELATIONSHIP_PART_SPECIFICATION,
								pgApolloConstants.TYPE_PGASSEMBLEDPRODUCTPART,
								objectSelects,//Object Select
								null,//rel Select
								true,//get To
								false,//get From
								(short)1,//recurse level
								null,//where Clause
								null,
								0);	

						loggerWS.debug("\n\n mlRelatedAPP-----------------{}" , mlRelatedAPP);

						if(mlRelatedAPP.size() > 0) {

							Map mpRelatedAPP = (Map)mlRelatedAPP.get(0);
							loggerWS.debug("\n\n mpRelatedAPP-----------------{}" , mpRelatedAPP);
							strAPP = (String)mpRelatedAPP.get(DomainConstants.SELECT_NAME);
							loggerWS.debug("\n\n strAPP-----------------{}" , strAPP);
							sbReturnMessages.append(strAPP);
						}
					} else {					
						sbReturnMessages.append(pgApolloConstants.STR_OBJECTNOTFOUND);
					}	
				}
			}
		} catch (Exception e) {
			loggerApolloTrace.error(e.getMessage(), e);
			return Response.serverError().entity("Error:" + e.toString()).build();
		}
		finally {
			if(bVPPMReferenceOpen){
				VPMREFERENCEBO.close(context);
			}

			if(bContextPushed){
				ContextUtil.popContext(context);
			}

			loggerWS.debug("\n getAPPNamet Ended-----------------");
			loggerWS.debug("\n******************************************************************************");
		}
		return Response.status(200).entity(sbReturnMessages.toString()).build();
	}
	
	/**
	 * Webservice Method to get APP Name and Access on APP
	 * APOLLO 2018x.5 ALM Requirement to fetch APP Name and Access
	 * Format : /fetchData/getAPPGCASWithAccess?VPMReferenceName1~VPMReferenceRevision1|VPMReferenceName2~VPMReferenceName2
				or
	 * Format : /fetchData/getAPPGCASWithAccess?VPMReferenceName1~VPMReferenceRevision1|VPMReferenceName2~VPMReferenceName2|GETBOOKMARK
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getAPPGCASWithAccess")
	public Response getAPPGCASWithAccess(@Context HttpServletRequest req) throws MatrixException {
		      		
		matrix.db.Context context = null;		
		String strType = pgApolloConstants.TYPE_VPMREFERENCE;
		String strObjectName = DomainConstants.EMPTY_STRING;
		String strObjectRevision = DomainConstants.EMPTY_STRING;
		String strGetBookMark = DomainConstants.EMPTY_STRING;
		boolean isGetBookMark = false;
		String strObjectDeails = DomainConstants.EMPTY_STRING;
		StringList slURLParam = new StringList();
		StringList slObjectDetails = new StringList();
		int iURLParamSize = 0;
		String strValidURLParam = DomainConstants.EMPTY_STRING;
		String strVPMRefAPPDetails;

		StringBuilder sbReturnMessages = new StringBuilder();
		BusinessObject boVPMREFERENCE = null;
		

		try {

			loggerWS.debug("******************************************************************************");
			loggerWS.debug("Path : /fetchData/getAPPGCASWithAccess");
			loggerWS.debug("Method: ExtractAPPFORVPMReference : getAPPGCASWithAccess");
			String strStartTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
			loggerWS.debug("GET APP Details started : {}" , strStartTime );

			// Get the user context
			if (Framework.isLoggedIn(req)) {
				context = Framework.getContext(req.getSession(false));				
			}
			
			if(null != context)
			{
				loggerWS.debug("context user : {}" , context.getUser());
			}
			String queryString = req.getQueryString();
			queryString =  java.net.URLDecoder.decode(queryString,"UTF-8");
			
			loggerWS.debug("QueryString  : {}" , queryString);

			if(UIUtil.isNotNullAndNotEmpty(queryString)) 
			{
				slURLParam = StringUtil.split(queryString, pgApolloConstants.CONSTANT_STRING_AMPERSAND);
				strValidURLParam = slURLParam.get(0);
				slURLParam = StringUtil.split(strValidURLParam, pgApolloConstants.CONSTANT_STRING_PIPE);
				loggerWS.debug("slURLParam  : {}" , slURLParam);
				iURLParamSize = slURLParam.size();
				if(iURLParamSize > 0) 
				{
					if(iURLParamSize > 1)
					{
						strGetBookMark =  slURLParam.get(iURLParamSize - 1);
						strGetBookMark = strGetBookMark.trim();
						if(UIUtil.isNotNullAndNotEmpty(strGetBookMark) && pgApolloConstants.STR_MODE_GETBOOKMARK.equalsIgnoreCase(strGetBookMark))
						{
							isGetBookMark = true;
							slURLParam.remove(iURLParamSize - 1);
							iURLParamSize = slURLParam.size();
							loggerWS.debug("isGetBookMark : {}" , isGetBookMark);
						}
					}
					for(int i = 0; i<iURLParamSize; i++)
					{
						strObjectDeails = slURLParam.get(i);
						if(strObjectDeails.contains(pgApolloConstants.CONSTANT_STRING_TILDA))
						{
							slObjectDetails = StringUtil.split(strObjectDeails, pgApolloConstants.CONSTANT_STRING_TILDA);					

							strObjectName = slObjectDetails.get(0);
							strObjectName = strObjectName.trim();
							if(strObjectName.contains("%20"))
								strObjectName = strObjectName.replace("%20", " ");
							loggerWS.debug("strObjectName_New : {}" , strObjectName);

							strObjectRevision = slObjectDetails.get(1);
							strObjectRevision = strObjectRevision.trim();
							loggerWS.debug("strObjectRevision : {}" , strObjectRevision);		

							boVPMREFERENCE = new BusinessObject(strType, strObjectName,strObjectRevision, pgApolloConstants.VAULT_VPLM);						

							boolean bPLMVPMREFERENCEBOExists = boVPMREFERENCE.exists(context);

							if(i > 0)
							{
								sbReturnMessages.append(pgApolloConstants.CONSTANT_STRING_AMPERSAND);
							}

							if(bPLMVPMREFERENCEBOExists)
							{
								strVPMRefAPPDetails = getAPPDetailsForVPMReference(context, isGetBookMark, boVPMREFERENCE);
								sbReturnMessages.append(strVPMRefAPPDetails);
							}
							else
							{					
								sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_NO_VPMREF_APP_FOUND);
							}	

						}

					}				
					
				}
			}
		} 
		catch (Exception e)
		{
			sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage());
		}
		loggerWS.debug("GET APP Details Ended-----Final Response ------------ : {}", sbReturnMessages);
		loggerWS.debug("******************************************************************************");		
		return Response.status(200).entity(sbReturnMessages.toString()).build();
	}

	/**
	 * Method to get VPMREf APP details for each VPMRef
	 * @param context
	 * @param isGetBookMark
	 * @param boVPMREFERENCE
	 * @return
	 * @throws MatrixException
	 */
	public String getAPPDetailsForVPMReference(matrix.db.Context context, boolean isGetBookMark, BusinessObject boVPMREFERENCE) throws MatrixException {
		StringBuilder sbReturnMessages = new StringBuilder();
		boolean bVPMReferenceOpen = false;		
		try
		{
			boVPMREFERENCE.open(context);
			bVPMReferenceOpen = true;			
			DomainObject domVPMReference = DomainObject.newInstance(context,boVPMREFERENCE);
			String strAPPDetails = getAPPDetails(context, isGetBookMark, domVPMReference);	
			sbReturnMessages.append(strAPPDetails);			
		}
		catch(Exception e1)
		{
			sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e1.getLocalizedMessage());
		}
		finally
		{
			if(bVPMReferenceOpen)
			{
				boVPMREFERENCE.close(context);
				bVPMReferenceOpen = false;
			}
		}		
		return sbReturnMessages.toString();
	}

	/**
	 * Method to get APP Details from VPMRef DomainObject
	 * @param context
	 * @param isGetBookMark
	 * @param domVPMReference
	 * @return
	 * @throws FrameworkException
	 */
	public String getAPPDetails(matrix.db.Context context, boolean isGetBookMark, DomainObject domVPMReference) throws FrameworkException
	{
		StringBuilder sbReturnMessages = new StringBuilder();		
		String strAPPName;
		String strAPPObjectId;
		String strAPPRevision;
		String strAPPReleasePhase;	
		String strLPDmodelUpdateStatus;
		String strBookMarkDetails;
		MapList mlRelatedAPP;
		
		StringList objectSelects = new StringList();
		objectSelects.add(DomainConstants.SELECT_NAME);
		objectSelects.add(DomainConstants.SELECT_ID);
		objectSelects.add(DomainConstants.SELECT_REVISION);
		objectSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_RELEASE_PHASE);
		objectSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PG_LPD_MODEL_UPDATE_STATUS);
		if(isGetBookMark)
		{
			objectSelects.add("to["+DomainConstants.RELATIONSHIP_VAULTED_OBJECTS_REV2+"]");
		}
		
		mlRelatedAPP = domVPMReference.getRelatedObjects(context,
				DomainConstants.RELATIONSHIP_PART_SPECIFICATION,
				pgApolloConstants.TYPE_PGASSEMBLEDPRODUCTPART,
				objectSelects,//Object Select
				null,//rel Select
				true,//get To
				false,//get From
				(short)1,//recurse level
				null,//where Clause
				null,
				0);	

		loggerWS.debug("\n mlRelatedAPP-----------------{}" , mlRelatedAPP);

		if(!mlRelatedAPP.isEmpty()) 
		{
			Map mpRelatedAPP = (Map)mlRelatedAPP.get(0);
			strAPPName = (String)mpRelatedAPP.get(DomainConstants.SELECT_NAME);
			strAPPObjectId = (String)mpRelatedAPP.get(DomainConstants.SELECT_ID);
			strAPPRevision = (String)mpRelatedAPP.get(DomainConstants.SELECT_REVISION);
			strAPPReleasePhase = (String)mpRelatedAPP.get(pgApolloConstants.SELECT_ATTRIBUTE_RELEASE_PHASE);
			strLPDmodelUpdateStatus = (String)mpRelatedAPP.get(pgApolloConstants.SELECT_ATTRIBUTE_PG_LPD_MODEL_UPDATE_STATUS);
			DomainObject doAPPObject = DomainObject.newInstance(context, strAPPObjectId);
			boolean bModifyAccess = FrameworkUtil.hasAccess(context, doAPPObject , "modify");
			sbReturnMessages.append(strAPPName)
			.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(strAPPRevision)
			.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(strAPPReleasePhase)
			.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(bModifyAccess)
			//Apollo 2018x.6 JIRA A10-1132 - STARTS
			.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(strLPDmodelUpdateStatus);
			//Apollo 2018x.6 JIRA A10-1132 - ENDS

			if(isGetBookMark)
			{
				strBookMarkDetails = getAPPBookMarkFlag(mpRelatedAPP);									
				sbReturnMessages.append(strBookMarkDetails);
			}

		}
		else
		{					
			sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_NO_VPMREF_APP_FOUND);
		}		
	return sbReturnMessages.toString();
	}
	
	/**
	 * Get APP Bookmark Flag details
	 * @param mpRelatedAPP
	 * @return
	 */
	private String getAPPBookMarkFlag(Map mpRelatedAPP)
	{
		String strBookMarkFlagDetails;
		String strIsBookMarkConnected;
		boolean isBookMarkConnected = false;
		StringBuilder sbReturnMessages = new StringBuilder();
		if(mpRelatedAPP.containsKey("to["+DomainConstants.RELATIONSHIP_VAULTED_OBJECTS_REV2+"]"))
		{
			strIsBookMarkConnected = (String)mpRelatedAPP.get("to["+DomainConstants.RELATIONSHIP_VAULTED_OBJECTS_REV2+"]");
			if(pgApolloConstants.STR_TRUE_FLAG.equalsIgnoreCase(strIsBookMarkConnected))
			{
				isBookMarkConnected = true;
			}
		}
		if(isBookMarkConnected)
		{
			sbReturnMessages.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(pgApolloConstants.STR_YES_FLAG);
		}
		else
		{
			sbReturnMessages.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(pgApolloConstants.STR_NO_FLAG);
		}
		strBookMarkFlagDetails = sbReturnMessages.toString();
		return strBookMarkFlagDetails;
	}
	
	
	/**
	 * Webservice Method to get Project Space and Book mark Details
	 * APOLLO 2018x.6 ALM Requirement to fetch Project Space and Book mark Details
	 * Format : /fetchData/getProjectSpaceBookmarkDetails?VPMReferenceName1~VPMReferenceRevision1|VPMReferenceName2~VPMReferenceName2
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getProjectSpaceBookmarkDetails")
	public Response getProjectSpaceBookmarkDetails(@Context HttpServletRequest req) throws MatrixException {
		      		
		matrix.db.Context context = null;		
		String strType = pgApolloConstants.TYPE_VPMREFERENCE;
		String strObjectName = DomainConstants.EMPTY_STRING;
		String strObjectRevision = DomainConstants.EMPTY_STRING;
		String strAPPObjectId = DomainConstants.EMPTY_STRING;
		String strObjectDeails = DomainConstants.EMPTY_STRING;
		StringList slURLParam = new StringList();
		StringList slObjectDetails = new StringList();
		int iURLParamSize = 0;
		String strVPMReferenceId = DomainConstants.EMPTY_STRING;
		StringList slVPMRefIdList = new StringList();
		Map mapAPPProjectDetails;
		String strValidURLParam = DomainConstants.EMPTY_STRING;
		
		StringBuilder sbReturnMessages = new StringBuilder();
		BusinessObject boVPMREFERENCE = null;
		StringBuilder sbHTMLOutput = new StringBuilder();

		StringList objectSelects = new StringList();
		objectSelects.add(DomainConstants.SELECT_NAME);
		objectSelects.add(DomainConstants.SELECT_REVISION);
		objectSelects.add("to["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION+"].from."+DomainConstants.SELECT_ID);
		objectSelects.add("to["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION+"].from."+DomainConstants.SELECT_NAME);
		objectSelects.add("to["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION+"].from."+DomainConstants.SELECT_REVISION);

		try {

			loggerWS.debug("******************************************************************************");
			loggerWS.debug("Path : /fetchData/getProjectSpaceBookmarkDetails");
			loggerWS.debug("Method: ExtractAPPFORVPMReference : getProjectSpaceBookmarkDetails");
			String strStartTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
			loggerWS.debug("Get APP Project Bookmark Report Details started : {}" ,strStartTime );

			// Get the user context
			if (Framework.isLoggedIn(req)) {
				context = Framework.getContext(req.getSession(false));				
			}			
			if(null != context)
			{
				loggerWS.debug("context user : {}" , context.getUser());
			}
			String queryString = req.getQueryString();
			queryString =  java.net.URLDecoder.decode(queryString,"UTF-8");
			
			loggerWS.debug("QueryString  : {}" , queryString);

			if (UIUtil.isNotNullAndNotEmpty(queryString)) 
			{
				slURLParam = StringUtil.split(queryString, pgApolloConstants.CONSTANT_STRING_AMPERSAND);
				strValidURLParam = slURLParam.get(0);
				slURLParam = StringUtil.split(strValidURLParam, pgApolloConstants.CONSTANT_STRING_PIPE);
				loggerWS.debug("slURLParam  : {}" , slURLParam);
				iURLParamSize = slURLParam.size();
				if(iURLParamSize > 0) 
				{					
					for(int i = 0; i<iURLParamSize; i++)
					{
						strObjectDeails = slURLParam.get(i);
						if(strObjectDeails.contains(pgApolloConstants.CONSTANT_STRING_TILDA))
						{
							slObjectDetails = StringUtil.split(strObjectDeails, pgApolloConstants.CONSTANT_STRING_TILDA);					
							
							strObjectName = slObjectDetails.get(0);
							strObjectName = strObjectName.trim();
							if(strObjectName.contains("%20"))
								strObjectName = strObjectName.replace("%20", " ");
							loggerWS.debug("strObjectName_New : {}" , strObjectName);

							strObjectRevision = slObjectDetails.get(1);
							strObjectRevision = strObjectRevision.trim();
							loggerWS.debug("strObjectRevision : {}" , strObjectRevision);		
							
							boVPMREFERENCE = new BusinessObject(strType, strObjectName,strObjectRevision, pgApolloConstants.VAULT_VPLM);						

							boolean bPLMVPMREFERENCEBOExists = boVPMREFERENCE.exists(context);						

							if(bPLMVPMREFERENCEBOExists)
							{
								strVPMReferenceId = boVPMREFERENCE.getObjectId(context);
								slVPMRefIdList.add(strVPMReferenceId);							
							}						
						}
						
					}	
					loggerWS.debug("slVPMRefIdList : {}" , slVPMRefIdList);

					String strAPPHTMLRow;
					sbHTMLOutput.append("<!DOCTYPE html>");
					sbHTMLOutput.append("<html>");
					sbHTMLOutput.append("<head>");
					sbHTMLOutput.append("</head>");
					
					sbHTMLOutput.append("<style>");
					sbHTMLOutput.append("table#appProjectDetailsTable {width:100%; border: 1px solid black; border-collapse: collapse;font-family: 'Courier New';}");
					sbHTMLOutput.append("table#appProjectDetailsTable tr { background-color: #eee;}");
					sbHTMLOutput.append("table#appProjectDetailsTable th {background-color: #006699; color: white; border: 1px solid black; border-collapse: collapse; padding: 5px; text-align: left;}");
					sbHTMLOutput.append("table#appProjectDetailsTable td {border: 1px solid #999999; border-collapse: collapse; padding: 5px; text-align: left;}");
					sbHTMLOutput.append("</style>");
					
					sbHTMLOutput.append("</head>");
					
					sbHTMLOutput.append("<body>");					
					sbHTMLOutput.append("<table id='appProjectDetailsTable'>");	

					sbHTMLOutput.append("<tr>");
					
					sbHTMLOutput.append("<th>");	
					sbHTMLOutput.append(EnoviaResourceBundle.getProperty(context, "emxCPNStringResource", context.getLocale(), "emxCPN.CollaborateWithAPP.APPProjectSpaceBookMarkDetails.VPMRefNameRevision"));
					sbHTMLOutput.append("</th>");
					
					sbHTMLOutput.append("<th>");
					sbHTMLOutput.append(EnoviaResourceBundle.getProperty(context, "emxCPNStringResource", context.getLocale(), "emxCPN.CollaborateWithAPP.APPProjectSpaceBookMarkDetails.APPNameRevision"));
					sbHTMLOutput.append("</th>");
					
					sbHTMLOutput.append("<th>");
					sbHTMLOutput.append(EnoviaResourceBundle.getProperty(context, "emxCPNStringResource", context.getLocale(), "emxCPN.CollaborateWithAPP.APPProjectSpaceBookMarkDetails.Project"));
					sbHTMLOutput.append("</th>");
					
					sbHTMLOutput.append("<th>");
					sbHTMLOutput.append(EnoviaResourceBundle.getProperty(context, "emxCPNStringResource", context.getLocale(), "emxCPN.CollaborateWithAPP.APPProjectSpaceBookMarkDetails.Bookmark"));
					sbHTMLOutput.append("</th>");
					
					sbHTMLOutput.append("</tr>");	

					
					if(!slVPMRefIdList.isEmpty())
					{
						MapList mlVPMReference;
						Map mapVPMRef;
						Object objectAPP;	
						int iVPMRefMapListSize = 0;
						mlVPMReference = DomainObject.getInfo(context, slVPMRefIdList.toArray(new String[slVPMRefIdList.size()]), objectSelects);

						if(null!=mlVPMReference && !mlVPMReference.isEmpty())
						{
							iVPMRefMapListSize = mlVPMReference.size();
							for(int i = 0; i<iVPMRefMapListSize; i++)
							{
								mapVPMRef = (Map)mlVPMReference.get(i);

								if(mapVPMRef.containsKey("to["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION+"].from."+DomainConstants.SELECT_ID))
								{
									objectAPP = mapVPMRef.get("to["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION+"].from."+DomainConstants.SELECT_ID);
									if(objectAPP instanceof String)
									{
										strAPPObjectId = (String)objectAPP;
										if(UIUtil.isNotNullAndNotEmpty(strAPPObjectId))
										{
											DomainObject doAPPObject = DomainObject.newInstance(context, strAPPObjectId);
											
											mapAPPProjectDetails = getAPPBookMarkDetails(context, doAPPObject);	

											strAPPHTMLRow = generateAPPHTMLRow(mapVPMRef, mapAPPProjectDetails);										

											sbHTMLOutput.append(strAPPHTMLRow);
										}
									}
								}								
							}
						}
						
					}
					
					sbHTMLOutput.append("</table>");	
					sbHTMLOutput.append("</body>");			
					sbHTMLOutput.append("</html>");					

				}
			}
		} 
		catch (Exception e)
		{
			sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage());
		}
		loggerWS.debug("Get APP Project Bookmark Details Ended-----Final Response ------------ : {}", sbReturnMessages);
		loggerWS.debug("******************************************************************************");		
		return Response.status(200).entity(sbHTMLOutput.toString()).build();
	}
	
	
	/**
	 * Method to generate HTML row for APP Project Space, Bookmark details
	 * @param mapVPMRef
	 * @param mapAPPProjectDetails
	 * @return
	 */
	private String generateAPPHTMLRow(Map mapVPMRef, Map mapAPPProjectDetails)
	{
		StringBuilder sbHTMLOutput = new StringBuilder();
		int iProjectRowSpan = 1;
		int iBookMarkRowSpan = 1;
		
		String strVPMRefName = (String)mapVPMRef.get(DomainConstants.SELECT_NAME);
		String strVPMRefRevision = (String)mapVPMRef.get(DomainConstants.SELECT_REVISION);
		String strAPPName = (String)mapVPMRef.get("to["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION+"].from."+DomainConstants.SELECT_NAME);
		String strAPPRevision = (String)mapVPMRef.get("to["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION+"].from."+DomainConstants.SELECT_REVISION);
		
		if(!mapAPPProjectDetails.isEmpty())
		{
			
			String strProjectNameKey;
			StringList slBookmarkList;
			Set<String> setAPPProjectBookmarks = mapAPPProjectDetails.keySet();
			Iterator<String> itrAPPProjectBookmark = setAPPProjectBookmarks.iterator();
			StringList slAllBookmarkList = new StringList();
			while (itrAPPProjectBookmark.hasNext()) 
			{
				strProjectNameKey = itrAPPProjectBookmark.next();
				slBookmarkList = (StringList)mapAPPProjectDetails.get(strProjectNameKey);
				slAllBookmarkList.addAll(slBookmarkList);
			}
			if(!slAllBookmarkList.isEmpty())
			{
				iBookMarkRowSpan = slAllBookmarkList.size();
			}

			int iAPPRow = 0;
			int iProjectRow = 0;
			StringList slProjectList = new StringList();

			itrAPPProjectBookmark = setAPPProjectBookmarks.iterator();

			while (itrAPPProjectBookmark.hasNext()) 
			{
				sbHTMLOutput.append("<tr>");		

				if(iAPPRow == 0)
				{
					sbHTMLOutput.append("<td rowspan='"+iBookMarkRowSpan+"'>");

					sbHTMLOutput.append(strVPMRefName);
					sbHTMLOutput.append(pgApolloConstants.CONSTANT_STRING_COLON);
					sbHTMLOutput.append(strVPMRefRevision);

					sbHTMLOutput.append("</td>");

					sbHTMLOutput.append("<td rowspan='"+iBookMarkRowSpan+"'>");

					sbHTMLOutput.append(strAPPName);
					sbHTMLOutput.append(pgApolloConstants.CONSTANT_STRING_COLON);
					sbHTMLOutput.append(strAPPRevision);

					sbHTMLOutput.append("</td>");	
					iAPPRow = iAPPRow + 1;
				}			

				strProjectNameKey = itrAPPProjectBookmark.next();
				slProjectList.add(strProjectNameKey);				
				slBookmarkList = (StringList)mapAPPProjectDetails.get(strProjectNameKey);
				iProjectRowSpan = slBookmarkList.size();	
				iProjectRow = 0;
				for(String strBookMark : slBookmarkList)
				{
					if(iProjectRow > 0)
					{
						sbHTMLOutput.append("<tr>");
					}
					else
					{
						sbHTMLOutput.append("<td rowspan='"+iProjectRowSpan+"'>");
						sbHTMLOutput.append(strProjectNameKey);
						sbHTMLOutput.append("</td>");
					}
					sbHTMLOutput.append("<td>");
					sbHTMLOutput.append(strBookMark);
					sbHTMLOutput.append("</td>");

					sbHTMLOutput.append("</tr>");

					iProjectRow = iProjectRow + 1;
				}			

			}	
		}
		else
		{
			sbHTMLOutput.append("<tr>");
			sbHTMLOutput.append("<td>");

			sbHTMLOutput.append(strVPMRefName);
			sbHTMLOutput.append(pgApolloConstants.CONSTANT_STRING_COLON);
			sbHTMLOutput.append(strVPMRefRevision);

			sbHTMLOutput.append("</td>");

			sbHTMLOutput.append("<td>");

			sbHTMLOutput.append(strAPPName);
			sbHTMLOutput.append(pgApolloConstants.CONSTANT_STRING_COLON);
			sbHTMLOutput.append(strAPPRevision);

			sbHTMLOutput.append("</td>");	
			
			sbHTMLOutput.append("<td>");
			sbHTMLOutput.append(DomainConstants.EMPTY_STRING);
			sbHTMLOutput.append("</td>");
			
			sbHTMLOutput.append("<td>");
			sbHTMLOutput.append(DomainConstants.EMPTY_STRING);
			sbHTMLOutput.append("</td>");
			
			sbHTMLOutput.append("</tr>");

		}
		
		return sbHTMLOutput.toString();
	}

	/**
	 * Method to get Book Mark Details associated with APP for Get APP webservice
	 * @param context
	 * @param doAPPObject
	 * @return
	 * @throws FrameworkException 
	 */
	public static Map getAPPBookMarkDetails(matrix.db.Context context, DomainObject doAPPObject) throws FrameworkException 
	{
		Map mapAPPProjectDetails = new HashMap();
		StringList slUniqueFolderList;
		StringBuilder sbRelatioshipPattern = new StringBuilder();
		sbRelatioshipPattern.append(DomainConstants.RELATIONSHIP_VAULTED_OBJECTS_REV2);
		sbRelatioshipPattern.append(pgApolloConstants.CONSTANT_STRING_COMMA);
		sbRelatioshipPattern.append(DomainConstants.RELATIONSHIP_SUB_VAULTS);
		sbRelatioshipPattern.append(pgApolloConstants.CONSTANT_STRING_COMMA);
		sbRelatioshipPattern.append(DomainConstants.RELATIONSHIP_PROJECT_VAULTS);


		StringBuilder sbPostRelPattern = new StringBuilder();
		sbPostRelPattern.append(DomainConstants.RELATIONSHIP_PROJECT_VAULTS);
		sbPostRelPattern.append(pgApolloConstants.CONSTANT_STRING_COMMA);
		sbPostRelPattern.append(DomainConstants.RELATIONSHIP_VAULTED_OBJECTS_REV2);
		
		StringList objectSelects = new StringList();
		objectSelects.add(DomainConstants.SELECT_NAME);
		objectSelects.add(DomainConstants.SELECT_ID);
		objectSelects.add(DomainConstants.SELECT_TYPE);
		objectSelects.add(DomainConstants.SELECT_KINDOF_PROJECT_SPACE);
		objectSelects.add(DomainConstants.SELECT_ATTRIBUTE_TITLE);

		MapList mlRelatedFolderProject = doAPPObject.getRelatedObjects(context,
				sbRelatioshipPattern.toString(), // relationshippattern
				DomainConstants.TYPE_WORKSPACE_VAULT +","+DomainConstants.TYPE_PROJECT_SPACE, // type pattern
				true, // to side
				false, // from side
				(short)0,//recurse level
				objectSelects,//Object Select
				null,//rel Select
				null,//where Clause
				null, // relWhereClause
				0, //limit
				sbPostRelPattern.toString(), // postRelPattern,
				null,// PostPattern
				null);// Map post pattern	

		if(null!= mlRelatedFolderProject && !mlRelatedFolderProject.isEmpty())
		{
			Map mapRelatedFolderProject;
			StringList slFolderList = new StringList();
			String strFolderList;
			String strObjectName;
			String sObjectTitle;
			String isProjectSpace;

			for(Object objectRelatedFolderProject : mlRelatedFolderProject)		
			{
				mapRelatedFolderProject = (Map)objectRelatedFolderProject;
				isProjectSpace = (String)mapRelatedFolderProject.get(DomainConstants.SELECT_KINDOF_PROJECT_SPACE);
				strObjectName = (String)mapRelatedFolderProject.get(DomainConstants.SELECT_NAME);	
				sObjectTitle = (String)mapRelatedFolderProject.get(DomainConstants.SELECT_ATTRIBUTE_TITLE);			
				if(pgApolloConstants.STR_TRUE_FLAG.equalsIgnoreCase(isProjectSpace))
				{
					strFolderList = slFolderList.get(0);	
					if(mapAPPProjectDetails.containsKey(strObjectName))
					{
						slUniqueFolderList = (StringList)mapAPPProjectDetails.get(strObjectName);
						slUniqueFolderList.add(strFolderList);
						mapAPPProjectDetails.put(strObjectName, slUniqueFolderList);
					}
					else
					{
						slUniqueFolderList = new StringList();
						slUniqueFolderList.add(strFolderList);
						mapAPPProjectDetails.put(strObjectName, slUniqueFolderList);
					}
					slFolderList = new StringList();
				}
				else
				{
					if(UIUtil.isNullOrEmpty(sObjectTitle))
					{
						sObjectTitle = strObjectName;
					}
					slFolderList.add(sObjectTitle);
				}
			}
		}
		return mapAPPProjectDetails;
	}

	/**
	 * Webservice Method to check valid Generic Model for Instantiation
	 * APOLLO 2018x.3 ALM Requirement 32494 The BASE Layered Product Design may only be instantiated from a Generic Model in the Release Maturity State.
	 * Format : /fetchData/checkGenericModelState?BA|PCP|PTP|PTC
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/checkGenericModelState")
	public Response checkGenericModelState(@Context HttpServletRequest req) throws Exception {
		
		matrix.db.Context context = null;	

		StringBuffer sbReturnMessages = new StringBuffer();
		boolean bContextPushed =false;
		
		String sGenericModel = DomainConstants.EMPTY_STRING;

		StringList objectSelects = new StringList();
		objectSelects.add(DomainConstants.SELECT_NAME);	
		
		try {

			loggerWS.debug("******************************************************************************");
			loggerWS.debug("Path : /fetchData/checkGenericModelState");
			loggerWS.debug("Method: ExtractAPPFORVPMReference : checkGenericModelState");
			loggerWS.debug("\n\n Check Generic Model started : {}" , new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) );

			// Get the user context
			if (Framework.isLoggedIn((HttpServletRequest) req)) {
				context = Framework.getContext((HttpSession) req.getSession(false));				
			}

			loggerWS.debug("context  : {}" , context);

			//Pushing the context to User Agent
			ContextUtil.pushContext(context,pgApolloConstants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);		
			bContextPushed = true;
			
			String queryString = req.getQueryString();
			queryString =  java.net.URLDecoder.decode(queryString,"UTF-8");

			loggerWS.debug("\n\nQueryString  : {}" , queryString);

			if (UIUtil.isNotNullAndNotEmpty(queryString)) {
				
				StringList slParameterList = StringUtil.split(queryString, pgApolloConstants.CONSTANT_STRING_PIPE);
				
				if(slParameterList.size() > 3)
				{
					String sBA = slParameterList.get(0);
					String sPCP = slParameterList.get(1);
					String sPTP = slParameterList.get(2);
					String sPTC = slParameterList.get(3);
					
					
					StringList slBusinessArea = new StringList(sBA);
					
					removeEmptyStringFromStringList(slBusinessArea);


					StringList slPCP = new StringList(sPCP);	
					
					removeEmptyStringFromStringList(slPCP);


					StringList slPTP = new StringList(sPTP);
					
					removeEmptyStringFromStringList(slPTP);


					StringList slPTC = new StringList(sPTC);	
					
					removeEmptyStringFromStringList(slPTC);
					
					
					StringBuilder sbWhereClause = new StringBuilder();	

					StringList slCurrent = new StringList();		
					slCurrent.add(pgApolloConstants.LPDGENERICCONFIGURATION_STATE_ACTIVE);

					sbWhereClause = pgApolloCommonUtil.appendWhereClause(sbWhereClause, DomainConstants.SELECT_CURRENT, slCurrent);
					
					sbWhereClause = pgApolloCommonUtil.appendWhereClause(sbWhereClause, pgApolloConstants.SELECT_ATTRIBUTE_ENTERPRISEPART_PGBUSINESSAREA, slBusinessArea);

					sbWhereClause = pgApolloCommonUtil.appendWhereClause(sbWhereClause, pgApolloConstants.SELECT_ATTRIBUTE_ENTERPRISEPART_PGPRODUCTCATEGORYPLATFORM, slPCP);
					
					sbWhereClause = pgApolloCommonUtil.appendWhereClause(sbWhereClause, pgApolloConstants.SELECT_ATTRIBUTE_ENTERPRISEPART_PGPRODUCTTECHNOLOGYPLATFORM, slPTP);

					sbWhereClause = pgApolloCommonUtil.appendWhereClause(sbWhereClause, pgApolloConstants.SELECT_ATTRIBUTE_ENTERPRISEPART_PGPRODUCTTECHNOLOGYCHASSIS, slPTC);


					StringList slObjectSelects = new StringList();
					slObjectSelects.add(DomainConstants.SELECT_ID);
					slObjectSelects.add(DomainConstants.SELECT_CURRENT);
					slObjectSelects.add(DomainConstants.SELECT_NAME);

					MapList mlGenericConfigurationList = DomainObject.findObjects(context,			 // Context
																pgApolloConstants.TYPE_PGLPDGENERICCONFIGURATION, // Type
																DomainConstants.QUERY_WILDCARD, //Name
																DomainConstants.QUERY_WILDCARD, // Revision
																null,							//Owner
																pgApolloConstants.VAULT_ESERVICE_PRODUCTION, // Vault
																sbWhereClause.toString(), // Where Clause
																true,				// Include Sub-types Flag
																slObjectSelects); // Object Selects
					
					if(null != mlGenericConfigurationList && !mlGenericConfigurationList.isEmpty())
					{
						Map mapGenericConfigMap = (Map)mlGenericConfigurationList.get(0);
						
						String sGenericConfigId = (String)mapGenericConfigMap.get(DomainConstants.SELECT_ID);
						
						if(UIUtil.isNotNullAndNotEmpty(sGenericConfigId))
						{
							DomainObject domObject = DomainObject.newInstance(context, sGenericConfigId);
							
							StringList slGenericConfigSelects = new StringList();
							slGenericConfigSelects.add("from["+pgApolloConstants.RELATIONSHIP_PGLPDRELATEDGENERICCONFIGURATION+"].to.id");

							Map mapGenericModel = domObject.getInfo(context, slGenericConfigSelects);
							
							StringList slExistingAPPId = pgApolloCommonUtil.getStringListMultiValue(mapGenericModel.get("from["+pgApolloConstants.RELATIONSHIP_PGLPDRELATEDGENERICCONFIGURATION+"].to.id"));

							if(null != slExistingAPPId && !slExistingAPPId.isEmpty() )
								{
									String sExistingAPPId = slExistingAPPId.get(0);
									
									if(UIUtil.isNotNullAndNotEmpty(sExistingAPPId))
									{
										DomainObject domAPPObject = DomainObject.newInstance(context,sExistingAPPId);

										String sVPMReferenceIdSelectable = new StringBuilder(pgApolloConstants.CONSTANT_STRING_SELECT_FROM).append(pgApolloConstants.RELATIONSHIP_PART_SPECIFICATION).append("].to[").append(pgApolloConstants.TYPE_VPMREFERENCE).append("].id").toString();
										
										String sVPMRefObjectId = domAPPObject.getInfo(context, sVPMReferenceIdSelectable);
										
										StringList slObjectSelectable = new StringList();
										slObjectSelectable.add(DomainConstants.SELECT_NAME);
										slObjectSelectable.add(DomainConstants.SELECT_REVISION);
										slObjectSelectable.add(pgApolloConstants.SELECT_RELEASED_ACTUAL);
								

										if(UIUtil.isNotNullAndNotEmpty(sVPMRefObjectId))
										{
											DomainObject domVPMRefObject = DomainObject.newInstance(context,sVPMRefObjectId);

											Map mapVPMRef = domVPMRefObject.getInfo(context, slObjectSelectable);
											
											String sVPMRefName = (String)mapVPMRef.get(DomainConstants.SELECT_NAME);
											
											String sVPMRefRevision = (String)mapVPMRef.get(DomainConstants.SELECT_REVISION);
											
											String sVPMRefFormattedReleaseDate = pgApolloConstants.STR_AUTOMATION_BLANK;
											
											String sVPMRefReleaseDate = (String)mapVPMRef.get(pgApolloConstants.SELECT_RELEASED_ACTUAL);
											
											if(UIUtil.isNotNullAndNotEmpty(sVPMRefReleaseDate))
											{
												SimpleDateFormat newFormatter = new SimpleDateFormat("MM/dd/yyyy");
												SimpleDateFormat ematrixFormat =new SimpleDateFormat(eMatrixDateFormat.getEMatrixDateFormat());
												Date releaseDate  = ematrixFormat.parse(sVPMRefReleaseDate);
												sVPMRefFormattedReleaseDate = newFormatter.format(releaseDate);
											}
											sGenericModel = new StringBuilder(sVPMRefName).append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sVPMRefRevision).append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sVPMRefFormattedReleaseDate).toString();
										}
									}
								}
						}
					}
				}
				
				
				if(UIUtil.isNotNullAndNotEmpty(sGenericModel))
				{
					sbReturnMessages.append(pgApolloConstants.STR_SUCCESS).append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sGenericModel);
				}
				else
				{
					sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_ERROR_NOGENERICOBJECTFOUND);
				}				
				
				if(sbReturnMessages.toString().isEmpty())
				{
					sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_ERROR);
				}

			}
		} catch (Exception e) {
			loggerApolloTrace.error(e.getMessage(), e);
			return Response.serverError().entity(pgApolloConstants.STR_ERROR+pgApolloConstants.CONSTANT_STRING_COLON + e.toString()).build();
		}
		finally {
				if(bContextPushed){
				ContextUtil.popContext(context);
			}

			loggerWS.debug("\nFinal Response : {}" ,sbReturnMessages.toString());
			loggerWS.debug("\n\n Check Generic Model ended-----------------");
			loggerWS.debug("\n******************************************************************************");
		}
		return Response.status(200).entity(sbReturnMessages.toString()).build();
	}
	
	/**
	 * Method to remove empty String from string list
	 * @param criteria1ValueList
	 * @return
	 */
	public static StringList removeEmptyStringFromStringList(StringList slValueList) 
	{
		if(null != slValueList && slValueList.contains(DomainConstants.EMPTY_STRING))
		{
			slValueList.remove(DomainConstants.EMPTY_STRING);
		}		
		return slValueList;
	}
	
	/*
	 * This webservice added to improve performance during Sync operation - ALM 31535
	 * Webservice is called by automation tool after generic model instantiation.
	 * This webservice disconnects all characteristics that are copied from generic model during instantiation..
	 * 
	 * We can disable this call, once we get Performance issue fix from R&D.
	 */	
	@GET
	@Path("/deleteCharacteristics")
	public Response deleteCharacteristics(@Context HttpServletRequest req) throws Exception {
		
		loggerWS.debug("Path : /fetchData/deleteCharacteristics");
		
		matrix.db.Context context = null;
		String[] arrayURLParam = null;
		String strType = pgApolloConstants.TYPE_VPMREFERENCE;
		String strObjectName = DomainConstants.EMPTY_STRING;
		String strObjectRevision = DomainConstants.EMPTY_STRING;
		String strAPPId = DomainConstants.EMPTY_STRING;

		StringList objectSelects = new StringList();
		objectSelects.add(DomainConstants.SELECT_NAME);
		objectSelects.add(DomainConstants.SELECT_ID);

		MapList mlRelatedAPP = new MapList();
		//APOLLO 2018x.5 Dec CW Defect 37443 - Start
		boolean bContextPushed = false;
		//APOLLO 2018x.5 Dec CW Defect 37443 - End

		try
		{
			loggerWS.debug("******************************************************************************");
			loggerWS.debug("Path : /fetchData/deleteCharacteristics");
			loggerWS.debug("Method: ExtractAPPFORVPMReference : deleteCharacteristics");			
			
			// Get the user context
			if (Framework.isLoggedIn(req)) {
				context = Framework.getContext(req.getSession(false));				
			}
			
			String queryString = req.getQueryString();
			queryString =  java.net.URLDecoder.decode(queryString,"UTF-8");
			
			if (UIUtil.isNotNullAndNotEmpty(queryString)) {
				arrayURLParam = queryString.split("&");
				if (arrayURLParam.length > 0) {

					strObjectName = arrayURLParam[0];
					strObjectName = strObjectName.trim();
					if(strObjectName.contains("%20"))
					{
						strObjectName = strObjectName.replace("%20", " ");
					}
					strObjectRevision = arrayURLParam[1];
					strObjectRevision = strObjectRevision.trim();

					String strVPMReferenceId = pgApolloCommonUtil.getObjectId(context, strType, strObjectName, strObjectRevision);
					
					if(UIUtil.isNotNullAndNotEmpty(strVPMReferenceId))
					{
						DomainObject domVPMReference = DomainObject.newInstance(context,strVPMReferenceId);
						mlRelatedAPP = domVPMReference.getRelatedObjects(context,
								DomainConstants.RELATIONSHIP_PART_SPECIFICATION,
								pgApolloConstants.TYPE_PGASSEMBLEDPRODUCTPART,
								objectSelects,//Object Select
								null,//rel Select
								true,//get To
								false,//get From
								(short)1,//recurse level
								null,//where Clause
								null,
								0);
						
						if(null != mlRelatedAPP && !mlRelatedAPP.isEmpty()) 
						{
							Map mpRelatedAPP = (Map)mlRelatedAPP.get(0);
							strAPPId = (String)mpRelatedAPP.get(DomainConstants.SELECT_ID);
							DomainObject doAPP = DomainObject.newInstance(context, strAPPId);
							//APOLLO 2018x.5 Dec CW Defect 37443 - Start
							//Context user might not get access to disconnect/delete Characteristics depending on associated Test Method objects. So we have to use Push context here. 
							ContextUtil.pushContext(context,pgApolloConstants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
							bContextPushed = true;
							deleteCharacteristicAndCertifications(context, doAPP);
							ContextUtil.popContext(context);
							bContextPushed = false;
							//APOLLO 2018x.5 Dec CW Defect 37443 - End																			
						}						
					}
				}
			}
		} 
		catch (Exception e) {
			loggerApolloTrace.error(e.getMessage(), e);
		}
		finally
		{
			if(bContextPushed)
			{
				ContextUtil.popContext(context);
			}
		}
		loggerWS.debug("\n\n Delete Characteristics webservice ended-----------------");
		loggerWS.debug("\n******************************************************************************");
		return Response.status(200).entity("Success").build();
	}
	
	
	
	
	
	/*
	 * This webservice added to improve performance during Sync operation - ALM 31535
	 * Webservice is called by automation tool after generic model instantiation.
	 * This webservice creates Background Job to evaluate criteria on the background.
	 * 
	 * We can disable this call, once we get Performance issue fix from R&D.
	 */	
	@GET
	@Path("/evaluateCriteria")
	public Response evaluateCriteria(@Context HttpServletRequest req) throws Exception {
		
		loggerWS.debug("Path : /fetchData/evaluateCriteria");
		
		matrix.db.Context context = null;
		String[] URLParam = null;
		String strType = pgApolloConstants.TYPE_VPMREFERENCE;
		String strObjectName = DomainConstants.EMPTY_STRING;
		String strObjectRevision = DomainConstants.EMPTY_STRING;
		String strAPPId = DomainConstants.EMPTY_STRING;

		StringList objectSelects = new StringList();
		objectSelects.add(DomainConstants.SELECT_NAME);
		objectSelects.add(DomainConstants.SELECT_ID);

		MapList mlRelatedAPP = new MapList();
		//APOLLO 2018x.5 Dec CW Defect 37443 - Start
		boolean bContextPushed = false;
		//APOLLO 2018x.5 Dec CW Defect 37443 - End

		try
		{
			loggerWS.debug("******************************************************************************");
			loggerWS.debug("Path : /fetchData/evaluateCriteria");
			loggerWS.debug("Method: ExtractAPPFORVPMReference : evaluateCriteria");
			loggerWS.debug("\n\n Evaluate Criteria webservice started : {}" , new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) );
			
			// Get the user context
			if (Framework.isLoggedIn((HttpServletRequest) req)) {
				context = Framework.getContext((HttpSession) req.getSession(false));				
			}

			String queryString = req.getQueryString();
			queryString =  java.net.URLDecoder.decode(queryString,"UTF-8");
			
			if (UIUtil.isNotNullAndNotEmpty(queryString)) {
				URLParam = queryString.split("&");
				if (URLParam.length > 0) {

					strObjectName = URLParam[0];
					strObjectName = strObjectName.trim();
					if(strObjectName.contains("%20"))
					{
						strObjectName = strObjectName.replace("%20", " ");
					}
					strObjectRevision = URLParam[1];
					strObjectRevision = strObjectRevision.trim();

					String strVPMReferenceId = pgApolloCommonUtil.getObjectId(context, strType, strObjectName, strObjectRevision);
					
					if(UIUtil.isNotNullAndNotEmpty(strVPMReferenceId))
					{
						DomainObject domVPMReference = DomainObject.newInstance(context,strVPMReferenceId);
						mlRelatedAPP = domVPMReference.getRelatedObjects(context,
								DomainConstants.RELATIONSHIP_PART_SPECIFICATION,
								pgApolloConstants.TYPE_PGASSEMBLEDPRODUCTPART,
								objectSelects,//Object Select
								null,//rel Select
								true,//get To
								false,//get From
								(short)1,//recurse level
								null,//where Clause
								null,
								0);
						
						if(null != mlRelatedAPP && !mlRelatedAPP.isEmpty()) 
						{
							Map mpRelatedAPP = (Map)mlRelatedAPP.get(0);
							strAPPId = (String)mpRelatedAPP.get(DomainConstants.SELECT_ID);
							DomainObject doAPP = DomainObject.newInstance(context, strAPPId);
							//APOLLO 2018x.5 Dec CW Defect 37443 - Start
							ContextUtil.pushContext(context,pgApolloConstants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
							bContextPushed = true;
							deleteCharacteristicAndCertifications(context, doAPP);
							ContextUtil.popContext(context);
							bContextPushed = false;
							//APOLLO 2018x.5 Dec CW Defect 37443 - End
							//To check whether APP is connected with any background job or not
							StringBuffer sbWhere = new StringBuffer(pgApolloConstants.SELECT_ATTRIBUTE_TITLE);
							sbWhere.append(" == '").append(pgApolloConstants.STRING_EVALUATE_CRITERIA_JOB_TITLE).append(strAPPId).append("'");
							MapList mlPendingJOB = doAPP.getRelatedObjects(context,
									pgApolloConstants.RELATIONSHIP_PENDING_JOB,
									pgApolloConstants.TYPE_JOB,
									objectSelects,//Object Select
									null,//rel Select
									false,//get To
									true,//get From
									(short)1,//recurse level
									sbWhere.toString(),//where Clause
									null,
									0);
															
							if(null != mlPendingJOB && mlPendingJOB.isEmpty()) 
							{									
								String[] argCriteria = new String[] {strAPPId};
								
								Job bgJob = new Job("pgDSMLayeredProductSyncUtil", "evaluateCriteria", argCriteria, false);	
								
								bgJob.setTitle(pgApolloConstants.STRING_EVALUATE_CRITERIA_JOB_TITLE + strAPPId );
								bgJob.setActionOnCompletion("Delete");
								bgJob.setNotifyOwner("No");
								bgJob.createAndSubmit(context);
								bgJob.setStartDate(context);									
								String strJobid = bgJob.getId(context);
								if(UIUtil.isNotNullAndNotEmpty(strJobid)) 
								{
									doAPP.open(context);
									doAPP.addToObject(context, new RelationshipType(pgApolloConstants.RELATIONSHIP_PENDING_JOB), strJobid); 
									doAPP.close(context);
								}
							}													
						}						
					}
				}
			}
		} 
		catch (Exception e) {
			loggerApolloTrace.error(e.getMessage(), e);
		}
		finally
		{
			if(bContextPushed)
			{
				ContextUtil.popContext(context);
			}
		}
		loggerWS.debug("\n\n Evaluate Criteria ended-----------------");
		loggerWS.debug("\n******************************************************************************");
		return Response.status(200).entity("Success").build();
	}

	/**
	 * Webservice Method to update Variant APP
	 * APOLLO 2018x.3 ALM 29954
	 * Format : /fetchData/updateVariantAPP?<SourceVPMreferenceName>&<SourceVPMReferenceRevision>&<TargetVPMReferenceName>&<TargetVPMReferenceRevision>
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/updateVariantAPP")
	public Response updateVariantAPP(@Context HttpServletRequest req) throws Exception {
		
		matrix.db.Context context = null;

		String[] URLParam = new String[20];
		String strSourceObjectName = DomainConstants.EMPTY_STRING;
		String strSourceObjectRevision = DomainConstants.EMPTY_STRING;
		String strTargetObjectName = DomainConstants.EMPTY_STRING;
		String strTargetObjectRevision = DomainConstants.EMPTY_STRING;	
		String strVPMRefSourceObjectId = DomainConstants.EMPTY_STRING;	
		String strVPMRefTargetObjectId = DomainConstants.EMPTY_STRING;	
		String strAPPSourceObjectId = DomainConstants.EMPTY_STRING;	
		String strAPPTargetObjectId = DomainConstants.EMPTY_STRING;
		String []strArgs =null;

		StringBuffer sbReturnMessages = new StringBuffer();
		boolean bContextPushed =false;		
		
		try {

			loggerWS.debug("******************************************************************************");
			loggerWS.debug("Path : /fetchData/updateVariantAPP");
			loggerWS.debug("Method: ExtractAPPFORVPMReference : updateVariantAPP");
			loggerWS.debug("\n\n Update Variant APP started : {}" , new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) );

			// Get the user context
			if (Framework.isLoggedIn((HttpServletRequest) req)) {
				context = Framework.getContext((HttpSession) req.getSession(false));				
			}

			loggerWS.debug("context  : {}" , context);
			//Pushing the context to User Agent
			ContextUtil.pushContext(context,pgApolloConstants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);		
			bContextPushed = true;
			
			String queryString = req.getQueryString();
			queryString =  java.net.URLDecoder.decode(queryString,"UTF-8");

			loggerWS.debug("\n\nQueryString  : {}" , queryString);

			if (UIUtil.isNotNullAndNotEmpty(queryString)) {
				URLParam = queryString.split("&");
				if (URLParam.length > 3) {

					strSourceObjectName = URLParam[0];
					strSourceObjectRevision = URLParam[1];
					strSourceObjectName = strSourceObjectName.trim();
					strSourceObjectRevision = strSourceObjectRevision.trim();
					
					strTargetObjectName = URLParam[2];
					strTargetObjectRevision = URLParam[3];
					strTargetObjectName = strTargetObjectName.trim();
					strTargetObjectRevision = strTargetObjectRevision.trim();
					
					strVPMRefSourceObjectId = pgApolloCommonUtil.getObjectId(context, pgApolloConstants.TYPE_VPMREFERENCE, strSourceObjectName, strSourceObjectRevision);
					strVPMRefTargetObjectId = pgApolloCommonUtil.getObjectId(context, pgApolloConstants.TYPE_VPMREFERENCE, strTargetObjectName, strTargetObjectRevision);
					if(UIUtil.isNotNullAndNotEmpty(strVPMRefSourceObjectId) && UIUtil.isNotNullAndNotEmpty(strVPMRefTargetObjectId))
					{
						strAPPSourceObjectId = pgApolloCommonUtil.fetchAPPObjectId(context, strVPMRefSourceObjectId);
						strAPPTargetObjectId = pgApolloCommonUtil.fetchAPPObjectId(context, strVPMRefTargetObjectId);
						
						loggerWS.debug("\n\nstrAPPSourceObjectId  : {}" , strAPPSourceObjectId);
						loggerWS.debug("\n\nstrAPPTargetObjectId  : {}" , strAPPTargetObjectId);
						
						if(UIUtil.isNotNullAndNotEmpty(strAPPSourceObjectId) && UIUtil.isNotNullAndNotEmpty(strAPPTargetObjectId))
						{
							try 
							{
								strArgs = new String[]{strAPPSourceObjectId, strAPPTargetObjectId, pgApolloConstants.TYPE_ASSEMBLED_PRODUCT_PART , pgApolloConstants.TYPE_ASSEMBLED_PRODUCT_PART};
								JPO.invoke(context, "pgDSMLayeredProductSyncUtil", strArgs, "updateLayeredProductPartOnDerivation", strArgs);
								sbReturnMessages.append(pgApolloConstants.STR_SUCCESS);
							}catch (Exception e) 
							{
								loggerApolloTrace.error(e.getMessage(), e);
								sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage());
							}
						}
					}					
					if(sbReturnMessages.toString().isEmpty())
					{
						sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_NO_VPMREF_APP_FOUND);
					}
				}
				else
				{
					sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_AUTOMATION_ERROR_INVALID_PARAMETERS);
				}
			}
		} catch (Exception e) {
			loggerApolloTrace.error(e.getMessage(), e);
			return Response.serverError().entity(pgApolloConstants.STR_ERROR+pgApolloConstants.CONSTANT_STRING_COLON + e.toString()).build();
		}
		finally 
		{
			if(bContextPushed){
				ContextUtil.popContext(context);
			}

			loggerWS.debug("\nFinal Response : {}" ,sbReturnMessages.toString());
			loggerWS.debug("\n\n Update Variant APP ended-----------------");
			loggerWS.debug("\n******************************************************************************");
		}
		return Response.status(200).entity(sbReturnMessages.toString()).build();
	}
	
	//APOLLO 2018x.5 ALM Requirement 34599 - A10-535 - Intended Market Webservice Changes - Start
	
	/**
	 * Webservice Method to apply Intended Markets on APP
	 * APOLLO 2018x.5 ALM Requirement 34599 - A10-535
	 * Format : /fetchData/applyIntendedMarket?<VPMreferenceName>&<VPMReferenceRevision>&USA|Canada|Mexico
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/applyIntendedMarket")
	public Response updateIntendedMarketOnAPP(@Context HttpServletRequest req) throws Exception {
		
		matrix.db.Context context = null;

		String[] URLParam = new String[3];
		String strObjectName = DomainConstants.EMPTY_STRING;
		String strObjectRevision = DomainConstants.EMPTY_STRING;
		String strVPMRefObjectId = DomainConstants.EMPTY_STRING;	
		String strAPPObjectId = DomainConstants.EMPTY_STRING;
		String strIntendedMarkets = DomainConstants.EMPTY_STRING;
	
		StringList slMissingIntendedMarket = new StringList();
		StringList slConnectedIntendedMarketObjectId = new StringList();
		StringList slConnectedIntendedMarketRelId = new StringList();				
		StringList slToBeConnectedIntendedMarketName = new StringList();
		StringList slToBeConnectedIntendedMarketId = new StringList();
		StringList slToBeConnectedNewIntendedMarketId = new StringList();
		StringList slToBeDisconnectedIntendedMarketRelId = new StringList();

		String strObjectId = DomainConstants.EMPTY_STRING;	
		String strRelId = DomainConstants.EMPTY_STRING;
		String strIntendedMarketName = DomainConstants.EMPTY_STRING;
		MapList mlIntendedMarkets = new MapList();
		Map objectMap = new HashMap();
		StringBuffer sbWhereClause = new StringBuffer();
		MapList mlCountry = new MapList();
		StringList slObjSelect = new StringList();
		slObjSelect.addElement(DomainConstants.SELECT_ID);
		boolean bDisconnectAll = false;

		StringBuffer sbReturnMessages = new StringBuffer();
		boolean bContextPushed =false;		
		
		try {

			loggerWS.debug("******************************************************************************");
			loggerWS.debug("Path : /fetchData/applyIntendedMarket");
			loggerWS.debug("Method: ExtractAPPFORVPMReference : updateIntendedMarketOnAPP");
			loggerWS.debug("\n\n Apply Intended Markets on APP started : {}" , new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) );

			// Get the user context
			if (Framework.isLoggedIn((HttpServletRequest) req)) {
				context = Framework.getContext((HttpSession) req.getSession(false));				
			}

			loggerWS.debug("context  : {}" , context);
			//Pushing the context to User Agent
			ContextUtil.pushContext(context,pgApolloConstants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);		
			bContextPushed = true;

			String queryString = req.getQueryString();
			queryString =  java.net.URLDecoder.decode(queryString,"UTF-8");

			loggerWS.debug("\n\nQueryString  : {}" , queryString);

			if (UIUtil.isNotNullAndNotEmpty(queryString)) {
				URLParam = queryString.split(pgApolloConstants.CONSTANT_STRING_DOUBLE_ATSIGN);//Apollo 18x.6 ALM-42391 Changes

				if (URLParam.length > 2 && UIUtil.isNotNullAndNotEmpty(URLParam[2])) {					
					strObjectName = URLParam[0];
					strObjectRevision = URLParam[1];
					strIntendedMarkets = URLParam[2];
					strObjectName = strObjectName.trim();
					strObjectRevision = strObjectRevision.trim();
					strIntendedMarkets = strIntendedMarkets.trim();					
					if(pgApolloConstants.STR_AUTOMATION_BLANK.equalsIgnoreCase(strIntendedMarkets))
					{
						bDisconnectAll = true;
					}
					else
					{
						slToBeConnectedIntendedMarketName = StringUtil.split(strIntendedMarkets, pgApolloConstants.CONSTANT_STRING_PIPE);
					}
					if(!bDisconnectAll && null!=slToBeConnectedIntendedMarketName && !slToBeConnectedIntendedMarketName.isEmpty())
					{

						for(int i=0; i<slToBeConnectedIntendedMarketName.size();i++)
						{
							strIntendedMarketName = slToBeConnectedIntendedMarketName.get(i);
							sbWhereClause = new StringBuffer();
							sbWhereClause.append(DomainConstants.SELECT_CURRENT).append(pgApolloConstants.CONSTANT_STRING_EQUAL_SIGN).append(pgApolloConstants.CONSTANT_STRING_EQUAL_SIGN).append(pgApolloConstants.STATE_ACTIVE);
							sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE).append(pgApolloConstants.CONSTANT_STRING_AMPERSAND).append(pgApolloConstants.CONSTANT_STRING_AMPERSAND).append(pgApolloConstants.CONSTANT_STRING_SPACE);
							sbWhereClause.append(DomainConstants.SELECT_NAME).append(pgApolloConstants.CONSTANT_STRING_SPACE).append(pgApolloConstants.CONSTANT_STRING_TILDA).append(pgApolloConstants.CONSTANT_STRING_TILDA).append("'").append(strIntendedMarketName).append("'");
							mlCountry =  DomainObject.findObjects(context, CPNCommonConstants.TYPE_COUNTRY,DomainConstants.QUERY_WILDCARD,DomainConstants.QUERY_WILDCARD,DomainConstants.QUERY_WILDCARD,DomainConstants.QUERY_WILDCARD, sbWhereClause.toString(),false, slObjSelect);
							loggerWS.debug("\nmlCountry for strIntendedMarketName : {}" , mlCountry);
							objectMap = new HashMap();
							if(null != mlCountry && !mlCountry.isEmpty())
							{
								objectMap =	(Map) mlCountry.get(0) ;	
							}
							if(null!=objectMap && !objectMap.isEmpty())
							{
								strObjectId = (String)objectMap.get(DomainConstants.SELECT_ID);
								slToBeConnectedIntendedMarketId.add(strObjectId);																
							}
							else
							{
								slMissingIntendedMarket.add(strIntendedMarketName);
							}
						}

					}
					loggerWS.debug("\nslToBeConnectedIntendedMarketId : {}" , slToBeConnectedIntendedMarketId);
					
						strVPMRefObjectId = pgApolloCommonUtil.getObjectId(context, pgApolloConstants.TYPE_VPMREFERENCE, strObjectName, strObjectRevision);
						if(UIUtil.isNotNullAndNotEmpty(strVPMRefObjectId))
						{
							strAPPObjectId = pgApolloCommonUtil.fetchAPPObjectId(context, strVPMRefObjectId);	
							loggerWS.debug("\n\nstrAPPObjectId  : {}" , strAPPObjectId);
							StringList slObjectSelect = new StringList();
							slObjectSelect.addElement(DomainConstants.SELECT_ID);

							StringList slRelSelect = new StringList();
							slRelSelect.addElement(DomainRelationship.SELECT_ID);

							if(UIUtil.isNotNullAndNotEmpty(strAPPObjectId))
							{
								DomainObject domAPPObject = DomainObject.newInstance(context, strAPPObjectId);								
								mlIntendedMarkets = domAPPObject.getRelatedObjects(context,
										pgApolloConstants.RELATIONSHIP_PG_INTENDED_MARKETS,
										CPNCommonConstants.TYPE_COUNTRY,
										slObjectSelect,//Object Select
										slRelSelect,//rel Select
										false,//get To
										true,//get From
										(short)1,//recurse level
										null,//object where Clause
										null,//rel where clause
										0); //object limit
								if(null != mlIntendedMarkets && !mlIntendedMarkets.isEmpty())
								{
									Map mpIntendedMarket = new HashMap();
									for(int i=0 ; i< mlIntendedMarkets.size() ; i++ )
									{
										mpIntendedMarket = new HashMap();
										mpIntendedMarket = (Map) mlIntendedMarkets.get(i);
										strRelId = (String) mpIntendedMarket.get(DomainRelationship.SELECT_ID);
										strObjectId = (String) mpIntendedMarket.get(DomainConstants.SELECT_ID);
										slConnectedIntendedMarketRelId.add(strRelId);
										slConnectedIntendedMarketObjectId.add(strObjectId);
									}
								}

								if(null!=slToBeConnectedIntendedMarketId && !slToBeConnectedIntendedMarketId.isEmpty())
								{					
									for(int i=0; i<slToBeConnectedIntendedMarketId.size();i++)
									{
										strObjectId = slToBeConnectedIntendedMarketId.get(i);
										if(UIUtil.isNotNullAndNotEmpty(strObjectId) && !slConnectedIntendedMarketObjectId.contains(strObjectId))
										{
											slToBeConnectedNewIntendedMarketId.add(strObjectId);						
										}						
									}
								}				
								if(null!=slConnectedIntendedMarketObjectId && !slConnectedIntendedMarketObjectId.isEmpty())
								{	
									if(bDisconnectAll)
									{
										slToBeDisconnectedIntendedMarketRelId.addAll(slConnectedIntendedMarketRelId);
									}
									else
									{
										for(int i=0; i<slConnectedIntendedMarketObjectId.size();i++)
										{
											strObjectId = slConnectedIntendedMarketObjectId.get(i);
											strRelId = slConnectedIntendedMarketRelId.get(i);
											if(UIUtil.isNotNullAndNotEmpty(strObjectId) && !slToBeConnectedIntendedMarketId.contains(strObjectId))
											{
												slToBeDisconnectedIntendedMarketRelId.add(strRelId);						
											}						
										}
									}									
								}
								if(null!=slToBeDisconnectedIntendedMarketRelId && !slToBeDisconnectedIntendedMarketRelId.isEmpty())
								{
									DomainRelationship.disconnect(context, slToBeDisconnectedIntendedMarketRelId.toArray(new String[slToBeDisconnectedIntendedMarketRelId.size()]));
								}
								if(null!=slToBeConnectedNewIntendedMarketId && !slToBeConnectedNewIntendedMarketId.isEmpty())
								{
									DomainRelationship.connect(context, domAPPObject, pgApolloConstants.RELATIONSHIP_PG_INTENDED_MARKETS, true,(String[])slToBeConnectedNewIntendedMarketId.toArray(new String[slToBeConnectedNewIntendedMarketId.size()]));
								}
								if(null!=slMissingIntendedMarket && !slMissingIntendedMarket.isEmpty())
								{
									String strMissingIntendedMarkets = StringUtil.join(slMissingIntendedMarket, pgApolloConstants.CONSTANT_STRING_PIPE);
									sbReturnMessages.append(pgApolloConstants.STR_WARNING);
									sbReturnMessages.append(pgApolloConstants.CONSTANT_STRING_COLON);
									sbReturnMessages.append(pgApolloConstants.STR_ERROR_NOINTENDEDMARKETS);
									sbReturnMessages.append(pgApolloConstants.CONSTANT_STRING_SPACE);
									sbReturnMessages.append(pgApolloConstants.CONSTANT_STRING_COLON);
									sbReturnMessages.append(pgApolloConstants.CONSTANT_STRING_SPACE);
									sbReturnMessages.append(strMissingIntendedMarkets);
								}
								else
								{
									sbReturnMessages.append(pgApolloConstants.STR_SUCCESS);
								}
							}
						}
					
					if(sbReturnMessages.toString().isEmpty())
					{
						sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_NO_VPMREF_APP_FOUND);
					}
				}
				else
				{
					sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_AUTOMATION_ERROR_INVALID_PARAMETERS);
				}
			}
		} catch (Exception e) {
			loggerApolloTrace.error(e.getMessage(), e);
			sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON ).append(e.getLocalizedMessage());
		}
		finally 
		{
			if(bContextPushed){
				ContextUtil.popContext(context);
			}

			loggerWS.debug("\nFinal Response : {}" ,sbReturnMessages.toString());
			loggerWS.debug("\n\n Apply Intended Markets on APP started ended-----------------");
			loggerWS.debug("\n******************************************************************************");
		}
		return Response.status(200).entity(sbReturnMessages.toString()).build();
	}
	
	
	
	/**
	 * Webservice Method to get Intended Markets on APP
	 * APOLLO 2018x.5 ALM Requirement 34599 - A10-535
	 * Format : /fetchData/getIntendedMarket?<VPMreferenceName>&<VPMReferenceRevision>
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getIntendedMarket")
	public Response getIntendedMarket(@Context HttpServletRequest req) throws Exception {
		
		matrix.db.Context context = null;

		String[] URLParam = new String[20];
		String strObjectName = DomainConstants.EMPTY_STRING;
		String strObjectRevision = DomainConstants.EMPTY_STRING;
		String strVPMRefObjectId = DomainConstants.EMPTY_STRING;	
		String strAPPObjectId = DomainConstants.EMPTY_STRING;
		String strIntendedMarkets = DomainConstants.EMPTY_STRING;
		
		StringList slConnectedIntendedMarketObjectName = new StringList();
		MapList mlIntendedMarkets = new MapList();

		StringBuffer sbReturnMessages = new StringBuffer();
		boolean bContextPushed =false;		
		
		try {

			loggerWS.debug("******************************************************************************");
			loggerWS.debug("Path : /fetchData/getIntendedMarket");
			loggerWS.debug("Method: ExtractAPPFORVPMReference : getIntendedMarket");
			loggerWS.debug("\n\n Get Intended Markets on APP started : {}" , new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) );

			// Get the user context
			if (Framework.isLoggedIn((HttpServletRequest) req)) {
				context = Framework.getContext((HttpSession) req.getSession(false));				
			}

			loggerWS.debug("context  : {}" , context);
			//Pushing the context to User Agent
			ContextUtil.pushContext(context,pgApolloConstants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);		
			bContextPushed = true;
			
			String queryString = req.getQueryString();
			queryString =  java.net.URLDecoder.decode(queryString,"UTF-8");

			loggerWS.debug("\n\nQueryString  : {}" , queryString);

			if (UIUtil.isNotNullAndNotEmpty(queryString)) {
				URLParam = queryString.split(pgApolloConstants.CONSTANT_STRING_AMPERSAND);				
				if (null!= URLParam && URLParam.length > 1) {					
					strObjectName = URLParam[0];
					strObjectRevision = URLParam[1];
					strObjectName = strObjectName.trim();
					strObjectRevision = strObjectRevision.trim();
					strVPMRefObjectId = pgApolloCommonUtil.getObjectId(context, pgApolloConstants.TYPE_VPMREFERENCE, strObjectName, strObjectRevision);
						if(UIUtil.isNotNullAndNotEmpty(strVPMRefObjectId))
						{
							strAPPObjectId = pgApolloCommonUtil.fetchAPPObjectId(context, strVPMRefObjectId);	
							loggerWS.debug("\n\nstrAPPObjectId  : {}" , strAPPObjectId);
							StringList slObjectSelect = new StringList();
							slObjectSelect.addElement(DomainConstants.SELECT_NAME);
							if(UIUtil.isNotNullAndNotEmpty(strAPPObjectId))
							{
								DomainObject domAPPObject = DomainObject.newInstance(context, strAPPObjectId);								
								mlIntendedMarkets = domAPPObject.getRelatedObjects(context,
										pgApolloConstants.RELATIONSHIP_PG_INTENDED_MARKETS,
										CPNCommonConstants.TYPE_COUNTRY,
										slObjectSelect,//Object Select
										null,//rel Select
										false,//get To
										true,//get From
										(short)1,//recurse level
										null,//object where Clause
										null,//rel where clause
										0); //object limit
								if(null != mlIntendedMarkets && !mlIntendedMarkets.isEmpty())
								{
									Map mpIntendedMarket = new HashMap();
									for(int i=0 ; i< mlIntendedMarkets.size() ; i++ )
									{
										mpIntendedMarket = new HashMap();
										mpIntendedMarket = (Map) mlIntendedMarkets.get(i);
										strObjectName = (String) mpIntendedMarket.get(DomainConstants.SELECT_NAME);
										slConnectedIntendedMarketObjectName.add(strObjectName);
									}
								}
								if(null!=slConnectedIntendedMarketObjectName && !slConnectedIntendedMarketObjectName.isEmpty())
								{
									strIntendedMarkets = FrameworkUtil.join(slConnectedIntendedMarketObjectName, pgApolloConstants.CONSTANT_STRING_PIPE);
									sbReturnMessages.append(strIntendedMarkets);
								}
								else
								{
									sbReturnMessages.append(pgApolloConstants.STR_AUTOMATION_BLANK);
								}
							}
						}
					
					if(sbReturnMessages.toString().isEmpty())
					{
						sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_NO_VPMREF_APP_FOUND);
					}
				}
				else
				{
					sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_AUTOMATION_ERROR_INVALID_PARAMETERS);
				}
			}
		} catch (Exception e) {
			loggerApolloTrace.error(e.getMessage(), e);
			sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON ).append(e.getLocalizedMessage());
		}
		finally 
		{
			if(bContextPushed){
				ContextUtil.popContext(context);
			}
			loggerWS.debug("\nFinal Response : {}" ,sbReturnMessages.toString());
			loggerWS.debug("\n\n Get Intended Markets on APP started ended-----------------");
			loggerWS.debug("\n******************************************************************************");
		}
		return Response.status(200).entity(sbReturnMessages.toString()).build();
	}
	//APOLLO 2018x.5 ALM Requirement 34599 - A10-535 - Intended Market Webservice Changes - End
	
	

	//APOLLO 2018x.5 ALM 35878,35879 - A10-603 - Copy EBOM and EBOM Substitute in case of Merged Variant - Start
	/**
	 * Webservice Method to copy Specific EBOM and EBOM Substitutes from Source Variant to Target Variant
	 * APOLLO 2018x.5 Requirement 35878,35879
	 * Format : /fetchData/updateVariantAPPForSpecificBOM?<SourceVPMRefName>&<SourceVPMRefRevision>&<TargetVPMRefName>&<TargetVPMRefRevision>&<GroupName>&<LayerName>&<RMPGCAS>
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/updateVariantAPPForSpecificBOM")
	public Response updateVariantAPPForSpecificBOM(@Context HttpServletRequest req) throws Exception {
		
		matrix.db.Context context = null;

		String[] urlParam = new String[20];
		String strSourceObjectName = DomainConstants.EMPTY_STRING;
		String strSourceObjectRevision = DomainConstants.EMPTY_STRING;
		String strTargetObjectName = DomainConstants.EMPTY_STRING;
		String strTargetObjectRevision = DomainConstants.EMPTY_STRING;
		String strVPMRefSourceObjectId = DomainConstants.EMPTY_STRING;	
		String strVPMRefTargetObjectId = DomainConstants.EMPTY_STRING;
		String strAPPSourceObjectId = DomainConstants.EMPTY_STRING;	
		String strAPPTargetObjectId = DomainConstants.EMPTY_STRING;
		
		String strGroupName = DomainConstants.EMPTY_STRING;
		String strLayerName = DomainConstants.EMPTY_STRING;
		String strRMPGCASName = DomainConstants.EMPTY_STRING;
		
		StringBuilder sbReturnMessages = new StringBuilder();
		boolean bContextPushed =false;		
		
		try {
			
			loggerWS.debug("******************************************************************************");
			loggerWS.debug("Path : /fetchData/updateVariantAPPForSpecificBOM");
			loggerWS.debug("Method: ExtractAPPFORVPMReference : updateVariantAPPForSpecificBOM");
			loggerWS.debug("\n\n Update Specific EBOM and EBOM Substitute to Variant APP started : {}" , new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) );

			// Get the user context
			if (Framework.isLoggedIn(req)) {
				context = Framework.getContext(req.getSession(false));				
			}

			loggerWS.debug("context  : {}" , context);
			//Pushing the context to User Agent
			ContextUtil.pushContext(context,pgApolloConstants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);		
			bContextPushed = true;
			
			String queryString = req.getQueryString();
			queryString =  java.net.URLDecoder.decode(queryString,"UTF-8");

			loggerWS.debug("\n\nQueryString  : {}" , queryString);

			if (UIUtil.isNotNullAndNotEmpty(queryString)) {
				urlParam = queryString.split(pgApolloConstants.CONSTANT_STRING_AMPERSAND);
				if (urlParam.length > 6) {

					strSourceObjectName = urlParam[0];
					strSourceObjectRevision = urlParam[1];
					strSourceObjectName = strSourceObjectName.trim();
					strSourceObjectRevision = strSourceObjectRevision.trim();
					
					strTargetObjectName = urlParam[2];
					strTargetObjectRevision = urlParam[3];
					strTargetObjectName = strTargetObjectName.trim();
					strTargetObjectRevision = strTargetObjectRevision.trim();
					
					strGroupName = urlParam[4];
					strLayerName = urlParam[5];
					strRMPGCASName = urlParam[6];
					strGroupName = strGroupName.trim();
					strLayerName = strLayerName.trim();
					strRMPGCASName = strRMPGCASName.trim();					
					
					strVPMRefSourceObjectId = pgApolloCommonUtil.getObjectId(context, pgApolloConstants.TYPE_VPMREFERENCE, strSourceObjectName, strSourceObjectRevision);
					strVPMRefTargetObjectId = pgApolloCommonUtil.getObjectId(context, pgApolloConstants.TYPE_VPMREFERENCE, strTargetObjectName, strTargetObjectRevision);
					
					if(UIUtil.isNotNullAndNotEmpty(strVPMRefSourceObjectId) && UIUtil.isNotNullAndNotEmpty(strVPMRefTargetObjectId))
					{
						strAPPSourceObjectId = pgApolloCommonUtil.fetchAPPObjectId(context, strVPMRefSourceObjectId);
						strAPPTargetObjectId = pgApolloCommonUtil.fetchAPPObjectId(context, strVPMRefTargetObjectId);
						
						loggerWS.debug("\n\nstrAPPSourceObjectId  : {}" , strAPPSourceObjectId);
						loggerWS.debug("\n\nstrAPPTargetObjectId  : {}" , strAPPTargetObjectId);
						
						if(UIUtil.isNotNullAndNotEmpty(strAPPSourceObjectId) && UIUtil.isNotNullAndNotEmpty(strAPPTargetObjectId))
						{
							copySpecificEBOMandEBOMSubstitutes(context, strAPPSourceObjectId, strAPPTargetObjectId, strGroupName, strLayerName, strRMPGCASName);
							sbReturnMessages.append(pgApolloConstants.STR_SUCCESS);
						}
					}					
					if(sbReturnMessages.toString().isEmpty())
					{
						sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_NO_VPMREF_APP_FOUND);
					}
				}
				else
				{
					sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_AUTOMATION_ERROR_INVALID_PARAMETERS);
				}
			}
			loggerWS.debug("\nFinal Response : {}" ,sbReturnMessages.toString());
			loggerWS.debug("\n\n Update Variant APP ended-----------------\n");
			
		} catch (Exception e) {
			loggerApolloTrace.error(e.getMessage(), e);
			loggerWS.debug("\n\n Update Variant APP ended with errors -----------------\n");			
			return Response.serverError().entity(pgApolloConstants.STR_ERROR+pgApolloConstants.CONSTANT_STRING_COLON + e.toString()).build();
		}
		finally 
		{
			if(bContextPushed){
				ContextUtil.popContext(context);
			}
		}
		return Response.status(200).entity(sbReturnMessages.toString()).build();
	}
	

	/**
	 *
	 * This method is called from webservice to copy specific EBOM and EBOM Substitute from Source APP to Variant APP
	 * @param context
	 * @param strSourceAPPId
	 * @param strTargetAPPId
	 * @param strGroupName
	 * @param strLayerName
	 * @param strRMPGCAS
	 * @throws Exception
	 */
	public void copySpecificEBOMandEBOMSubstitutes(matrix.db.Context context, String strSourceAPPId, String strTargetAPPId, String strGroupName, String strLayerName, String strRMPGCAS) throws Exception
	{
			StringList slObjectSelect = new StringList();
			slObjectSelect.addElement(DomainConstants.SELECT_ID);
			slObjectSelect.addElement(DomainConstants.SELECT_NAME);
			
			StringList slRelSelect = new StringList();
			slRelSelect.addElement(DomainRelationship.SELECT_ID);
			slRelSelect.addElement(DomainConstants.SELECT_FIND_NUMBER);
			slRelSelect.addElement(pgApolloConstants.SELECT_ATTRIBUTE_PLYGROUPNAME);
			slRelSelect.addElement(pgApolloConstants.SELECT_ATTRIBUTE_PLYNAME);
			
			StringBuilder sbObjWhr = new StringBuilder();
			sbObjWhr.append(DomainConstants.SELECT_NAME).append(pgApolloConstants.CONSTANT_STRING_DOUBLE_EQUAL).append(strRMPGCAS);
			
			StringBuilder sbRelWhr = new StringBuilder();
			sbRelWhr.append(pgApolloConstants.SELECT_ATTRIBUTE_PLYGROUPNAME).append(pgApolloConstants.CONSTANT_STRING_DOUBLE_EQUAL).append(pgApolloConstants.CONSTANT_STRING_SINGLE_QUOTE).append(strGroupName).append(pgApolloConstants.CONSTANT_STRING_SINGLE_QUOTE);
			sbRelWhr.append(pgApolloConstants.CONSTANT_STRING_DOUBLE_AMPERSAND);
			sbRelWhr.append(pgApolloConstants.SELECT_ATTRIBUTE_PLYNAME).append(pgApolloConstants.CONSTANT_STRING_DOUBLE_EQUAL).append(pgApolloConstants.CONSTANT_STRING_SINGLE_QUOTE).append(strLayerName).append(pgApolloConstants.CONSTANT_STRING_SINGLE_QUOTE);
			
			DomainObject doSourceAPPObject = DomainObject.newInstance(context, strSourceAPPId);
			DomainObject doTargetAPPObject = DomainObject.newInstance(context, strTargetAPPId);
			
			MapList mlTargetEBOMList = doTargetAPPObject.getRelatedObjects(context,
					DomainConstants.RELATIONSHIP_EBOM,
					DomainConstants.TYPE_PART,
					slObjectSelect,//Object Select
					slRelSelect,//rel Select
					false,//get To
					true,//get From
					(short)1,//recurse level
					null,//object where Clause
					sbRelWhr.toString(),//rel where clause
					0); //object limit
			
			 Map mpTemp = null;
			 StringList deleteListRelId = new StringList();
			 String strRelId = null;
			 String strFindNumber = DomainConstants.EMPTY_STRING;
			 String strRelIdTarget= null;
			 StringList slSubstitueObjId = null;
			 StringList slSubstitueRelId = null;	
			 StringList slSubstitueTargetObjId = null;
			 StringList slSubstitueTargetRelId = null;
			 Part part = null;
			 
			 String strSubstituteId = null;
			 String strSubstituteRelId = null;

			 String strSubstituteTargetId = null;
			 String strSubstituteTargetRelId = null;			 
			 
			 DomainObject domRMPObj = null;
			 DomainRelationship domEBOMRelObj = null;
			 DomainRelationship domTargetEBOMRelObj = null;
			 DomainRelationship domEBOMSubstituteRelObj = null;
			 DomainRelationship domTargetEBOMSubstituteRelObj = null;
			 BusinessInterface biLayer = new BusinessInterface(pgApolloConstants.INTERFACE_LAYERPRODUCTINTERFACE, context.getVault());
			 BusinessInterface biEBOMLayer = new BusinessInterface(pgApolloConstants.INTERFACE_DSMLAYERPRODUCTEBOMINTERFACE, context.getVault());
			 Map relAttributeMap = null;
			 Map relEBOMSubstituteAttributeMap = null;

			 StringList relTargetSelects = StringList.create(DomainRelationship.SELECT_ID);
			 java.util.Hashtable relData = null;
			 StringList relObjectList = null;
			 
			
			 if(null != mlTargetEBOMList && !mlTargetEBOMList.isEmpty())
			 {
				 for(int i=0 ; i < mlTargetEBOMList.size() ; i++ )
				 {					 
					 mpTemp = (Map) mlTargetEBOMList.get(i);	
					 strRelId = (String) mpTemp.get(DomainRelationship.SELECT_ID);
					 strFindNumber = (String) mpTemp.get(DomainConstants.SELECT_FIND_NUMBER);
					 deleteListRelId.add(strRelId);
				 }			
				 if(!deleteListRelId.isEmpty())
				 {
					 DomainRelationship.disconnect(context,deleteListRelId.toArray(new String[deleteListRelId.size()]));
				 }
			 }
			 
			 String strEBOMSubIdSelect = new StringBuilder().append(pgApolloConstants.CONSTANT_STRING_SELECT_FROMMID).append(EngineeringConstants.RELATIONSHIP_EBOM_SUBSTITUTE).append(pgApolloConstants.CONSTANT_STRING_SELECT_TOID).toString();
			 String strEBOMSubRelIdSelect = new StringBuilder().append(pgApolloConstants.CONSTANT_STRING_SELECT_FROMMID).append(EngineeringConstants.RELATIONSHIP_EBOM_SUBSTITUTE).append(pgApolloConstants.CONSTANT_STRING_SELECT_RELID).toString();

			slRelSelect.addElement(strEBOMSubIdSelect);
			slRelSelect.addElement(strEBOMSubRelIdSelect);
			 
			MapList mlSourceEBOMList = doSourceAPPObject.getRelatedObjects(context,
					DomainConstants.RELATIONSHIP_EBOM,
					DomainConstants.TYPE_PART,
					slObjectSelect,//Object Select
					slRelSelect,//rel Select
					false,//get To
					true,//get From
					(short)1,//recurse level
					sbObjWhr.toString(),//object where Clause
					sbRelWhr.toString(),//rel where clause
					0); //object limit
			 
			 if(null != mlSourceEBOMList && !mlSourceEBOMList.isEmpty())
			 {

					 mpTemp = (Map) mlSourceEBOMList.get(0);	

					 String strObjectId = (String) mpTemp.get(DomainConstants.SELECT_ID);
					 strRelId = (String) mpTemp.get(DomainRelationship.SELECT_ID);
					 domEBOMRelObj = DomainRelationship.newInstance(context, strRelId);

					 relAttributeMap = domEBOMRelObj.getAttributeMap(context);
					 if(UIUtil.isNotNullAndNotEmpty(strFindNumber))
					 {
						 relAttributeMap.put(DomainConstants.ATTRIBUTE_FIND_NUMBER, strFindNumber);
					 }
					 domRMPObj = DomainObject.newInstance(context,strObjectId);
					 //Create new EBOM connection
					 domTargetEBOMRelObj = DomainRelationship.connect(context, doTargetAPPObject, DomainConstants.RELATIONSHIP_EBOM, domRMPObj);
					 //Add interface on EBOM connection
					 domTargetEBOMRelObj.addBusinessInterface(context, biLayer);
					 domTargetEBOMRelObj.addBusinessInterface(context, biEBOMLayer);
					 //Set attributes on EBOM
					 domTargetEBOMRelObj.setAttributeValues(context, relAttributeMap);
					 
					 relData = domTargetEBOMRelObj.getRelationshipData(context, relTargetSelects);
					 relObjectList = (StringList) relData.get(DomainRelationship.SELECT_ID);
					 strRelIdTarget = relObjectList.get(0);
					 slSubstitueObjId = pgApolloCommonUtil.getStringListFromObject(mpTemp.get(strEBOMSubIdSelect));
					 slSubstitueRelId = pgApolloCommonUtil.getStringListFromObject(mpTemp.get(strEBOMSubRelIdSelect));
					 
					 //Code to create Substitute Relationship
					 if(null != slSubstitueObjId && !slSubstitueObjId.isEmpty())
					 {
						 for(int x=0 ; x < slSubstitueObjId.size() ; x++)
						 {
							 strSubstituteId = slSubstitueObjId.get(x);	
							 part = (Part)DomainObject.newInstance(context,DomainConstants.TYPE_PART,DomainConstants.ENGINEERING);
							 part.setId(strObjectId);
							 part.createSubstitutePart(context, strObjectId, strRelIdTarget, strSubstituteId);
						 }
					 }
					 
					 relData = domTargetEBOMRelObj.getRelationshipData(context, slRelSelect);
					 relObjectList = (StringList) relData.get(DomainRelationship.SELECT_ID);
					 strRelIdTarget = relObjectList.get(0);
					 slSubstitueTargetObjId = pgApolloCommonUtil.getStringListFromObject(relData.get(strEBOMSubIdSelect));
					 slSubstitueTargetRelId = pgApolloCommonUtil.getStringListFromObject(relData.get(strEBOMSubRelIdSelect));
					 if(null != slSubstitueObjId && !slSubstitueObjId.isEmpty())
					 {	
						 for(int x=0 ; x < slSubstitueObjId.size() ; x++)
						 {
							 strSubstituteId = slSubstitueObjId.get(x);
							 strSubstituteRelId = slSubstitueRelId.get(x);
							 domEBOMSubstituteRelObj = DomainRelationship.newInstance(context, strSubstituteRelId);
							 relEBOMSubstituteAttributeMap = domEBOMSubstituteRelObj.getAttributeMap(context);
							 if(null != slSubstitueTargetObjId && !slSubstitueTargetObjId.isEmpty())
							 {
								 for(int y=0 ; y < slSubstitueTargetObjId.size() ; y++)
								 {
									 strSubstituteTargetId = slSubstitueTargetObjId.get(y);
									 strSubstituteTargetRelId = slSubstitueTargetRelId.get(y);
									 if(strSubstituteTargetId.equals(strSubstituteId))
									 {
										 domTargetEBOMSubstituteRelObj = DomainRelationship.newInstance(context, strSubstituteTargetRelId);
										 domTargetEBOMSubstituteRelObj.setAttributeValues(context, relEBOMSubstituteAttributeMap);
										 break;
									 }										
								 }
							 }
						 }
					 }
			 }			
		
	}	
	//APOLLO 2018x.5 ALM 35878,35879 - A10-603 - Copy EBOM and EBOM Substitute in case of Merged Variant - End
	

	/**
	 * APOLLO 2018x.5 Dec Change Window Defect 37443 - Upgrade 2022x.00 Defect-48596
	 * This method disconnects All Characteristics and Certifications objects from APP when Generic Model is instantiated
	 * This method is called from Webservice before calling background job for evaluate criteria to make sure existing characteristics are completely deleted before evaluating criteria again
	 * @param context
	 * @param args
	 * @throws Exception
	 */
	public void deleteCharacteristicAndCertifications(matrix.db.Context context,DomainObject donAPPObject) throws Exception 
	{
		StringList relSelects = new StringList(1);
		relSelects.add(DomainRelationship.SELECT_ID);
		StringList busSelects = new StringList(1);
		busSelects.add(DomainConstants.SELECT_ID);

		MapList mlConnectedCharList = donAPPObject.getRelatedObjects(context,
				com.png.apollo.pgApolloConstants.RELATIONSHIP_PARAMETER_AGGREGATION,
				com.png.apollo.pgApolloConstants.TYPE_PLM_PARAMETER,
				busSelects,				//Object Select
				relSelects,			//rel Select
				false,			//get To
				true,			//get From
				(short)1,	   //recurse level
				null,			//where Clause
				null,
				0);
		
		if(null != mlConnectedCharList && !mlConnectedCharList.isEmpty())
		{
				Map mpConnectedChar = null;
				String[] strObjectIdArray = new String[mlConnectedCharList.size()];
				for(int i=0;i<mlConnectedCharList.size();i++) 
				{
					mpConnectedChar = (Map)mlConnectedCharList.get(i);
					strObjectIdArray[i]=(String)mpConnectedChar.get(DomainConstants.SELECT_ID);
				}
				//Disconnect all characteristics objects in a single transaction
				DomainObject.deleteObjects(context, strObjectIdArray);
		}
		
		StringList slExistingCertificateRelIds = donAPPObject.getInfoList(context, "from["+ pgApolloConstants.RELATIONSHIP_PG_PLI_MATERIAL_CERTIFICATIONS + "].id");
		
		if(null != slExistingCertificateRelIds && !slExistingCertificateRelIds.isEmpty())
		{
			DomainRelationship.disconnect(context, slExistingCertificateRelIds.toArray(new String []{}));
		}
	}
	

	/**
	 * Web Service Method to get VPMReference List associated with 3DShape
	 * APOLLO 2018x.6 ALM Requirement to fetch VPMReference List associated with 3DShape
	 * Format : /fetchData/getVPMReference?3DShapeName&3DShapeRevision
	 * @param req
	 * @return
	 * @throws IOException 
	 * @throws MatrixException 
	 * @throws Exception
	 */
	@GET
	@Path("/getVPMReference")
	public Response getVPMRefNameRev(@Context HttpServletRequest req) throws IOException, MatrixException  {

		matrix.db.Context context = null;

		String[] arrayURLParam = null;
		String strType = pgApolloConstants.TYPE_3DSHAPE;
		String strObjectName = DomainConstants.EMPTY_STRING;
		String strObjectRevision = DomainConstants.EMPTY_STRING;
		String strVPMRefName = DomainConstants.EMPTY_STRING;
		String strVPMRefRevision = DomainConstants.EMPTY_STRING;

		StringBuilder sbReturnMessages = new StringBuilder();
		StringBuilder sbVPMRefInfo = new StringBuilder();
		BusinessObject bo3DShape = null;
		boolean b3DShapeOpen = false;
		StringList slVPMRef = new StringList();
		String strVPMRefInfo = DomainConstants.EMPTY_STRING;
		String strVPMRefInfoList = DomainConstants.EMPTY_STRING;

		StringList objectSelects = new StringList();
		objectSelects.add(DomainConstants.SELECT_ID);
		objectSelects.add(DomainConstants.SELECT_NAME);
		objectSelects.add(DomainConstants.SELECT_REVISION);

		MapList mlRelatedVPMRef = new MapList();
		Map mpRelatedVPMRef;

		try
		{
			loggerWS.debug("******************************************************************************");
			loggerWS.debug("Path : /fetchData/getVPMReference");
			loggerWS.debug("Method: ExtractAPPFORVPMReference : getVPMRefNameRev");
			String strStartTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
			loggerWS.debug("GET VPMReference started : {}" , strStartTime );

			// Get the user context
			if (Framework.isLoggedIn(req)) {
				context = Framework.getContext(req.getSession(false));				
			}

			if(null != context)
			{
				loggerWS.debug("context user : {}" , context.getUser());
			}
			String queryString = req.getQueryString();
			queryString =  java.net.URLDecoder.decode(queryString,"UTF-8");

			loggerWS.debug("QueryString : {}" , queryString );
			
			if (UIUtil.isNotNullAndNotEmpty(queryString)) 
			{
				arrayURLParam = queryString.split(pgApolloConstants.CONSTANT_STRING_AMPERSAND);
				if (arrayURLParam.length > 0) 
				{
					strObjectName = arrayURLParam[0];
					strObjectName = strObjectName.trim();					
					
					loggerWS.debug("strObjectName : {}" , strObjectName );
					
					strObjectRevision = arrayURLParam[1];
					strObjectRevision = strObjectRevision.trim();					
					
					loggerWS.debug("strObjectRevision : {}" , strObjectRevision );
					
					bo3DShape = new BusinessObject(strType, strObjectName,strObjectRevision, pgApolloConstants.VAULT_VPLM);						

					boolean bPLMbo3DShapeExists = bo3DShape.exists(context);

					if(bPLMbo3DShapeExists) 
					{
						bo3DShape.open(context);
						b3DShapeOpen = true;
						DomainObject dom3DShape = DomainObject.newInstance(context,bo3DShape);
						mlRelatedVPMRef = dom3DShape.getRelatedObjects(context,
								pgApolloConstants.RELATIONSHIP_VPMRepInstance,
								pgApolloConstants.TYPE_VPMREFERENCE,
								objectSelects,//Object Select
								null,//rel Select
								true,//get To
								false,//get From
								(short)1,//recurse level
								null,// object where Clause
								null,//rel where clause
								0);	// limit
						
						loggerWS.debug("mlRelatedVPMRef : {}" , mlRelatedVPMRef );
						
						if(null!=mlRelatedVPMRef && !mlRelatedVPMRef.isEmpty()) 
						{		
							
							for(Object objectVPMRef : mlRelatedVPMRef)
							{
								mpRelatedVPMRef = (Map)objectVPMRef;								
								strVPMRefName = (String)mpRelatedVPMRef.get(DomainConstants.SELECT_NAME);
								strVPMRefRevision = (String)mpRelatedVPMRef.get(DomainConstants.SELECT_REVISION);								
								sbVPMRefInfo = new StringBuilder();
								sbVPMRefInfo.append(strVPMRefName).append(pgApolloConstants.CONSTANT_STRING_PIPE).append(strVPMRefRevision);
								strVPMRefInfo = sbVPMRefInfo.toString();
								slVPMRef.add(strVPMRefInfo);								
							}							
							if(!slVPMRef.isEmpty())
							{
								strVPMRefInfoList = StringUtil.join(slVPMRef, pgApolloConstants.CONSTANT_STRING_AMPERSAND);
								sbReturnMessages.append(strVPMRefInfoList);
							}
							else
							{
								sbReturnMessages.append(pgApolloConstants.STR_AUTOMATION_BLANK);
							}
						}
						else
						{
							sbReturnMessages.append(pgApolloConstants.STR_AUTOMATION_BLANK);
						}
						
					}					
					else 
					{					
						sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_NO_3DSHAPE_FOUND);
					}	
				}
				else
				{
					sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_AUTOMATION_ERROR_INVALID_PARAMETERS);
				}
			}
		} 
		catch (Exception e) 
		{
			sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage());
		}
		finally 
		{
			if(b3DShapeOpen)
			{
				bo3DShape.close(context);
			}			
		}
			loggerWS.debug("GET VPMReference ended - Final Response  : {}" , sbReturnMessages );
			loggerWS.debug("******************************************************************************");			
		return Response.status(200).entity(sbReturnMessages.toString()).build();
	}
	
	
	
	/**
	 * Web Service Method to get Component Family related details
	 * APOLLO 2018x.6 ALM Requirement to fetch Component Family Context related details
	 * Format : /fetchData/getComponentFamilyContextDetails?mode&objectName&objectRevision
	 * @param req
	 * @return
	 * @throws IOException 
	 * @throws MatrixException 
	 * @throws Exception
	 */
	@GET
	@Path("/getComponentFamilyContextDetails")
	public Response getComponentFamilyContextDetails(@Context HttpServletRequest req) throws IOException, MatrixException  {

		matrix.db.Context context = null;

		String[] arrayURLParam = null;
		String strMode = DomainConstants.EMPTY_STRING;
		String strObjectName = DomainConstants.EMPTY_STRING;
		String strObjectRevision = DomainConstants.EMPTY_STRING;

		StringBuilder sbReturnMessages = new StringBuilder();
		String strSemanticRole;
		String strContextObjectType;
		String strPathOwnerType;

		StringList objectSelects = new StringList();
		objectSelects.add(DomainConstants.SELECT_ID);
		objectSelects.add(DomainConstants.SELECT_NAME);
		objectSelects.add(DomainConstants.SELECT_REVISION);

		try
		{

			loggerWS.debug("******************************************************************************");
			loggerWS.debug("Path : /fetchData/getComponentFamilyContextDetails");
			loggerWS.debug("Method: ExtractAPPFORVPMReference : getComponentFamilyContextDetails");
			String strStartDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
			loggerWS.debug(" GET Component Family Context Details started : {}" , strStartDate );

			// Get the user context
			if (Framework.isLoggedIn(req)) {
				context = Framework.getContext(req.getSession(false));				
			}

			if(null != context)
			{
				String strUser = context.getUser();
				loggerWS.debug("context user : {}" , strUser);
			}
			String queryString = req.getQueryString();
			queryString =  java.net.URLDecoder.decode(queryString,"UTF-8");

			loggerWS.debug("QueryString  : {}" , queryString);
			
			if (UIUtil.isNotNullAndNotEmpty(queryString)) 
			{
				arrayURLParam = queryString.split(pgApolloConstants.CONSTANT_STRING_AMPERSAND);
				if (arrayURLParam.length > 2) 
				{
					strMode  = arrayURLParam[0];
					loggerWS.debug(" Mode  : {}" , strMode);
					strObjectName = arrayURLParam[1];
					strObjectName = strObjectName.trim();
					loggerWS.debug("strObjectName : {}" , strObjectName);

					strObjectRevision = arrayURLParam[2];
					strObjectRevision = strObjectRevision.trim();
					loggerWS.debug("strObjectRevision : {}" , strObjectRevision);
					String strAllItemDetails = DomainConstants.EMPTY_STRING;
					if(pgApolloConstants.STR_MODE_GETRESOLVEDITEMS.equalsIgnoreCase(strMode))
					{
						strContextObjectType = pgApolloConstants.TYPE_CATCOMPONENTSFAMILYEXPLICIT;
						strPathOwnerType = pgApolloConstants.TYPE_CATCOMPONENTSFAMILYPROXYTOELEMENT;						
						strAllItemDetails = getComponentFamilyItemDetails(context, strObjectName, strObjectRevision, strContextObjectType, strPathOwnerType);						
					}	
					else if(pgApolloConstants.STR_MODE_GETGENERICITEMS.equalsIgnoreCase(strMode))
					{
						strContextObjectType = pgApolloConstants.TYPE_CATCOMPONENTSFAMILYEXPLICIT;
						strPathOwnerType = pgApolloConstants.TYPE_CATCOMPONENTSFAMILYGENERICCONNECTION;						
						strAllItemDetails = getComponentFamilyItemDetails(context, strObjectName, strObjectRevision, strContextObjectType, strPathOwnerType);	
					}
					else if(pgApolloConstants.STR_MODE_GETCOMPONENTFAMILY_GENERICITEM.equalsIgnoreCase(strMode))
					{
						strContextObjectType = pgApolloConstants.TYPE_VPMREFERENCE;
						strPathOwnerType = pgApolloConstants.TYPE_CATCOMPONENTSFAMILYGENERICCONNECTION;
						strSemanticRole = pgApolloConstants.CONST_SEMANTICROLE_CFY_GENERICMODELREFERENCE;
						strAllItemDetails = getComponentFamilyDetails(context, strObjectName, strObjectRevision, strContextObjectType, strPathOwnerType, strSemanticRole);	
					}
					else if(pgApolloConstants.STR_MODE_GETCOMPONENTFAMILY_RESOLVEDITEM.equalsIgnoreCase(strMode))
					{
						strContextObjectType = pgApolloConstants.TYPE_VPMREFERENCE;
						strPathOwnerType = pgApolloConstants.TYPE_CATCOMPONENTSFAMILYPROXYTOELEMENT;	
						strSemanticRole = pgApolloConstants.CONST_SEMANTICROLE_CFY_ITEMREFERENCE;
						strAllItemDetails = getComponentFamilyDetails(context, strObjectName, strObjectRevision, strContextObjectType, strPathOwnerType, strSemanticRole);	
					}
					if(!strAllItemDetails.isEmpty())
					{
						sbReturnMessages.append(strAllItemDetails);
					}
					else
					{
						sbReturnMessages.append(pgApolloConstants.STR_AUTOMATION_BLANK);
					}
				}
				else
				{
					sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_AUTOMATION_ERROR_INVALID_PARAMETERS);
				}
			}				
		} 
		catch (Exception e) 
		{
			sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage());
		}
		loggerWS.debug("  GET Component Family Context Details-----Final Response ------------{}", sbReturnMessages);
		loggerWS.debug("******************************************************************************");		
		return Response.status(200).entity(sbReturnMessages.toString()).build();
	}

	/**
	 * Method to get Component Family details from Resolved/Generic Item
	 * @param context
	 * @param strObjectName
	 * @param strObjectRevision
	 * @param strContextObjectType
	 * @param strPathOwnerType
	 * @param strSemanticRole
	 * @return
	 * @throws MatrixException
	 */
	public String getComponentFamilyDetails(matrix.db.Context context, String strObjectName, String strObjectRevision,String strContextObjectType, String strPathOwnerType, String strSemanticRole) throws MatrixException
	{
		String strObjectDetails = DomainConstants.EMPTY_STRING;
		BusinessObject boInputObject = null;
		String strContextObjectPhysicalId;
		boolean bInputObject = false;

		try {
			boInputObject = new BusinessObject(strContextObjectType, strObjectName,strObjectRevision, pgApolloConstants.VAULT_VPLM);
			boolean bPLMboExists = boInputObject.exists(context);
			if(bPLMboExists) 
			{
				boInputObject.open(context);
				bInputObject = true;
				DomainObject domVPMReference = DomainObject.newInstance(context,boInputObject);
				strContextObjectPhysicalId = domVPMReference.getInfo(context, pgApolloConstants.SELECT_PHYSICAL_ID);
				if(UIUtil.isNotNullAndNotEmpty(strContextObjectPhysicalId))
				{
					StringList slObjectSelect = new StringList();
					slObjectSelect.add("owner.to["+pgApolloConstants.RELATIONSHIP_VPLMREL_PLMCONNECTION_V_OWNER+"].from.id");
					List <String> lList = new ArrayList<>(1);
					lList.add(strContextObjectPhysicalId); // Physical Id of VPMReferece

					PathQuery pathQuery = new PathQuery();
					pathQuery.setPathType("SemanticRelation") ;
					pathQuery.setCriterion(PathQuery.ENDS_WITH_ANY, lList);
					pathQuery.setWhereExpression("attribute[RoleSemantics]=='"+strSemanticRole+"' && owner.type=='"+strPathOwnerType+"'");
					PathQueryIterator  pathQueryIter  =  pathQuery.getIterator(context, slObjectSelect, (short)0, null, false);
					StringList slComponentFamilyIdList = new StringList();
					StringList slComponentFamilyIds;
					Iterator iterquerySR = pathQueryIter.iterator();
					PathWithSelect pathsSR = null;
					while(iterquerySR.hasNext())
					{
						pathsSR = (PathWithSelect) iterquerySR.next();
						slComponentFamilyIds = pathsSR.getSelectDataList("owner.to["+pgApolloConstants.RELATIONSHIP_VPLMREL_PLMCONNECTION_V_OWNER+"].from.id");
						if(null!=slComponentFamilyIds)
						{
							slComponentFamilyIdList.addAll(slComponentFamilyIds);
						}
					}
					pathQueryIter.close();					
					strObjectDetails = getObjectDetails(context, slComponentFamilyIdList, false);				
				}			
			}
		}		
		finally
		{
			if(bInputObject)
			{
				boInputObject.close(context);
			}
		}
		return strObjectDetails;
	}
	

	/**
	 * Method to get Object Details for given Object Id List
	 * @param context
	 * @param slObjectIdList
	 * @param bFetchAPPDetails 
	 * @return
	 * @throws FrameworkException
	 */
	public String getObjectDetails(matrix.db.Context context, StringList slObjectIdList, boolean bFetchAPPDetails) throws FrameworkException {
		String strObjectDetails = DomainConstants.EMPTY_STRING;
		StringList slSelectItem = new StringList(4);
		slSelectItem.add(DomainConstants.SELECT_ID);
		slSelectItem.add(DomainConstants.SELECT_TYPE);
		slSelectItem.add(DomainConstants.SELECT_NAME);
		slSelectItem.add(DomainConstants.SELECT_REVISION);
		
		if(bFetchAPPDetails)
		{
			slSelectItem.add(pgApolloConstants.SELECT_ATTRIBUTE_V_NAME);
			slSelectItem.add(pgApolloConstants.SELECT_ATTRIBUTE_PGLPDMODELTYPE);
		}

		if(null!=slObjectIdList && !slObjectIdList.isEmpty())
		{
			MapList mlObjectDetails = DomainObject.getInfo(context, slObjectIdList.toArray(new String[slObjectIdList.size()]), slSelectItem);		
			Map mapItem;
			String strLocalObjectName;
			String strLocalObjectRevision;
			String strLocalObjectId;
			String strLocalObjectTitle;
			String strLocalObjectModelType;
			String strLocalObjectEnterprisePartDetails;
			DomainObject domVPMReference = null;

			String strLocalObjectType;
			StringBuilder sbLocalObject;
			StringList slItemDetails = new StringList();
			String strItemDetails;

			if(null!=mlObjectDetails && !mlObjectDetails.isEmpty())
			{
				for(Object object: mlObjectDetails)
				{
					mapItem = (Map)object;
					strLocalObjectName = (String)mapItem.get(DomainConstants.SELECT_NAME);
					strLocalObjectRevision = (String)mapItem.get(DomainConstants.SELECT_REVISION);
					strLocalObjectType = (String)mapItem.get(DomainConstants.SELECT_TYPE);
					if(pgApolloConstants.TYPE_VPMREFERENCE.equalsIgnoreCase(strLocalObjectType) || pgApolloConstants.TYPE_CATCOMPONENTSFAMILYEXPLICIT.equalsIgnoreCase(strLocalObjectType))
					{
						sbLocalObject = new StringBuilder();						
						if(!bFetchAPPDetails)
						{
							sbLocalObject.append(strLocalObjectType).append(pgApolloConstants.CONSTANT_STRING_PIPE);
						}					
						sbLocalObject.append(strLocalObjectName).append(pgApolloConstants.CONSTANT_STRING_PIPE);
						sbLocalObject.append(strLocalObjectRevision);
						
						if(bFetchAPPDetails)
						{
							strLocalObjectTitle  = (String)mapItem.get(pgApolloConstants.SELECT_ATTRIBUTE_V_NAME);
							sbLocalObject.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(strLocalObjectTitle);
							
							strLocalObjectModelType = (String)mapItem.get(pgApolloConstants.SELECT_ATTRIBUTE_PGLPDMODELTYPE);
							sbLocalObject.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(strLocalObjectModelType);

							strLocalObjectId = (String)mapItem.get(DomainConstants.SELECT_ID);
							if(UIUtil.isNotNullAndNotEmpty(strLocalObjectId))
							{
								domVPMReference = DomainObject.newInstance(context,strLocalObjectId);
								strLocalObjectEnterprisePartDetails = getAPPDetails(context, true, domVPMReference);	
								if(UIUtil.isNotNullAndNotEmpty(strLocalObjectEnterprisePartDetails))
								{
									sbLocalObject.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(strLocalObjectEnterprisePartDetails);
								}
								else
								{
									loggerWS.error(" Error in fetching APP Details for getComponentFamilyContextDetails > {}",strLocalObjectEnterprisePartDetails);
								}

							}
						}						
						strItemDetails = sbLocalObject.toString();
						slItemDetails.add(strItemDetails);											
					}
				}	
				
				if(!slItemDetails.isEmpty())
				{
					strObjectDetails = StringUtil.join(slItemDetails, pgApolloConstants.CONSTANT_STRING_AMPERSAND);
				}
			}
		}		
		return strObjectDetails;
	}

	/**
	 * Method to get Component Family Item details
	 * @param context
	 * @param strObjectName
	 * @param strObjectRevision
	 * @param strContextObjectType
	 * @param strPathOwnerType
	 * @return
	 * @throws MatrixException
	 */
	public String getComponentFamilyItemDetails(matrix.db.Context context, String strObjectName, String strObjectRevision, String strContextObjectType, String strPathOwnerType)	throws MatrixException {
		String strAllItemDetails = DomainConstants.EMPTY_STRING;
		BusinessObject boInputObject;
		String strContextObjectPhysicalId;
		boInputObject = new BusinessObject(strContextObjectType, strObjectName,strObjectRevision, pgApolloConstants.VAULT_VPLM);
		boolean bPLMboExists = boInputObject.exists(context);
		if(bPLMboExists) 
		{
			strContextObjectPhysicalId = boInputObject.getObjectId(context);
			if(UIUtil.isNotNullAndNotEmpty(strContextObjectPhysicalId))
			{
				List<String> listPathIds;
				List<String> listPathElementPhysicalId;
				String[] oidList = new String[1]; 
				oidList[0] = strContextObjectPhysicalId; 

				StringList selectOnCnx = new StringList();
				selectOnCnx.add("from["+pgApolloConstants.RELATIONSHIP_VPLMREL_PLMCONNECTION_V_OWNER+"].to["+strPathOwnerType+"].paths[SemanticRelation].path.id");
				
				List<String> listPaths = new ArrayList<>();
				BusinessObjectWithSelectList queryCnx = BusinessObject.getSelectBusinessObjectData (context, oidList, selectOnCnx);
				Iterator iterqueryCnx = queryCnx.iterator();
				BusinessObjectWithSelect cnx = null;
				while(iterqueryCnx.hasNext())
				{
					cnx = (BusinessObjectWithSelect) iterqueryCnx.next();
					listPathIds =  cnx.getSelectDataList("from["+pgApolloConstants.RELATIONSHIP_VPLMREL_PLMCONNECTION_V_OWNER+"].to["+strPathOwnerType+"].paths[SemanticRelation].path.id");
					if (null != listPathIds)
				    {
				    	listPaths.addAll(listPathIds);
				    }
				} 
				
				StringList slAllItems = new StringList();								  
				StringList selectOnPath = new StringList();
				selectOnPath.add("element.physicalid");
			
				if(!listPaths.isEmpty())
				{
					PathWithSelectList pathWithSelect= matrix.db.Path.getSelectPathData(context, listPaths.toArray(new String[]{}),selectOnPath);
					for (PathWithSelect  path : pathWithSelect ) 
					{
						listPathElementPhysicalId =  path.getSelectDataList("element[0].physicalid");
						slAllItems.addAll(listPathElementPhysicalId);
					}				
					strAllItemDetails = getObjectDetails(context, slAllItems, true);		
				}
							
			}
		}
		
		return strAllItemDetails;
	}
	
	
	
	/**
	 * Webservice Method to check IP Access
	 * APOLLO 2018x.6 Dec CW ALM Requirement to check IP Access
	 * Format : /fetchData/checkIPAccess?VPMReferenceName|VPMReferenceRevision
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/checkIPAccess")
	public Response checkIPAccess(@Context HttpServletRequest req) throws MatrixException {
		      		
		matrix.db.Context context = null;		
		String strObjectName = DomainConstants.EMPTY_STRING;
		String strObjectRevision = DomainConstants.EMPTY_STRING;
		StringList slURLParam = new StringList();
		int iURLParamSize = 0;
		String strValidURLParam = DomainConstants.EMPTY_STRING;
		String sVPMRefObjectId;
		StringBuilder sbReturnMessages = new StringBuilder();
		boolean bContextPushed = false;
		try 
		{

			loggerWS.debug("******************************************************************************");
			loggerWS.debug("Path : /fetchData/checkIPAccess");
			loggerWS.debug("Method: ExtractAPPFORVPMReference : checkIPAccess");
			String strStartTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
			loggerWS.debug("Check IP Access Details started : {}" , strStartTime );

			// Get the user context
			if (Framework.isLoggedIn(req)) {
				context = Framework.getContext(req.getSession(false));				
			}
			
			String sUserName = DomainConstants.EMPTY_STRING;
			
			if(null != context)
			{
				sUserName = context.getUser();
			}
			String queryString = req.getQueryString();
			queryString =  java.net.URLDecoder.decode(queryString,"UTF-8");

			loggerWS.debug("QueryString  : {}" , queryString);
			if(UIUtil.isNotNullAndNotEmpty(queryString)) 
			{
				slURLParam = StringUtil.split(queryString, pgApolloConstants.CONSTANT_STRING_AMPERSAND);
				strValidURLParam = slURLParam.get(0);
				slURLParam = StringUtil.split(strValidURLParam, pgApolloConstants.CONSTANT_STRING_PIPE);
				loggerWS.debug("slURLParam  : {}" , slURLParam);
				iURLParamSize = slURLParam.size();
				if(iURLParamSize > 0) 
				{	
					loggerWS.debug("context user : {}" , sUserName);
					String sDesignForPreference = pgApolloCommonUtil.getPreferenceValue(context, sUserName, pgApolloConstants.PREF_DESIGN_FOR);
					String sDefaultIPClassification = pgApolloCommonUtil.getPreferenceValue(context, sUserName, pgApolloConstants.PREF_DEFAULT_EXPLORATION);
					String sIPControlClassPreference = pgApolloCommonUtil.getPreferenceValue(context, sUserName, pgApolloConstants.PREF_HIR);
					
					loggerWS.debug("sDesignForPreference : {}, sDefaultIPClassification : {}, sIPControlClassPreference : {}" , sDesignForPreference, sDefaultIPClassification, sIPControlClassPreference);
					
					if(UIUtil.isNotNullAndNotEmpty(sDesignForPreference) && pgApolloConstants.STR_ASSEMBLED_PRODUCT.equals(sDesignForPreference)
						&& UIUtil.isNotNullAndNotEmpty(sDefaultIPClassification) && pgApolloConstants.STR_HIGHLY_RESTRICTED.equals(sDefaultIPClassification) 
						&& UIUtil.isNotNullAndNotEmpty(sIPControlClassPreference))
					{
						
						strObjectName = slURLParam.get(0);
						strObjectName = strObjectName.trim();
						loggerWS.debug("strObjectName : {}" , strObjectName);

						strObjectRevision = slURLParam.get(1);
						strObjectRevision = strObjectRevision.trim();
						loggerWS.debug("strObjectRevision : {}" , strObjectRevision);		
						
						//Pushing the context to User Agent - User will not have always access to all parts or IP classes
						ContextUtil.pushContext(context,pgApolloConstants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);		
						bContextPushed = true;
						
						sVPMRefObjectId = pgApolloCommonUtil.getObjectId(context, pgApolloConstants.TYPE_VPMREFERENCE, strObjectName, strObjectRevision);
						
						ContextUtil.popContext(context);
						bContextPushed = false;
						
						if(null != context)
						{
							String sOriginalUser = context.getUser();
							loggerWS.debug("Original User  : {}" , sOriginalUser);
						}

						if(UIUtil.isNotNullAndNotEmpty(sVPMRefObjectId))
						{	
							DomainObject doVPMRefObject = DomainObject.newInstance(context, sVPMRefObjectId);
							boolean bAccessPresent = FrameworkUtil.hasAccess(context, doVPMRefObject , pgV3Constants.READSHOW_ACCESS);
							loggerWS.debug("bAccessPresent :: {}" , bAccessPresent);
							
							if(bAccessPresent)
							{
								sbReturnMessages.append(pgApolloConstants.STR_SUCCESS);
							}
							else
							{
								sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_AUTOMATION_ERROR_USER_NOIPACCESS);
							}		

						}							
						else
						{					
							sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_NO_VPMREF_APP_FOUND);
						}	
						
						
					}
					else
					{
						sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_AUTOMATION_ERROR_DI_PREFERENCES_NOT_SET);
					}				
				}
				else
				{
					sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_AUTOMATION_ERROR_INVALID_PARAMETERS);
				}
			}
		} 
		catch (Exception e)
		{
			sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage());
		}
		finally 
		{
			if(bContextPushed)
			{
				ContextUtil.popContext(context);
			}
		}
		loggerWS.debug("Check APP IP Access Details Ended-----Final Response ------------ : {}", sbReturnMessages);
		loggerWS.debug("******************************************************************************");		
		return Response.status(200).entity(sbReturnMessages.toString()).build();
	}
	
	
	
	/**
	 * Web service Method to get Clearance Markets on APP
	 * APOLLO 2018x.6 June 22 CW ALM Requirement
	 * Format : /fetchData/getClearanceMarkets?<VPMreferenceName>&<VPMReferenceRevision>
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getClearanceMarkets")
	public Response getClearanceMarkets(@Context HttpServletRequest req) throws Exception {
		
		matrix.db.Context context = null;

		String[] arrayURLParam = new String[20];
		String strObjectName = DomainConstants.EMPTY_STRING;
		String strObjectRevision = DomainConstants.EMPTY_STRING;
		String strVPMRefObjectId = DomainConstants.EMPTY_STRING;	
		String strAPPObjectId = DomainConstants.EMPTY_STRING;
		String strCleranceMarkets = DomainConstants.EMPTY_STRING;
		
		StringList slConnectedCleranceMarketObjectName = new StringList();
		MapList mlCleranceMarkets = new MapList();

		StringBuffer sbReturnMessages = new StringBuffer();
		boolean bContextPushed =false;		
		
		try {

			loggerWS.debug("******************************************************************************");
			loggerWS.debug("Path : /fetchData/getClearedMarkets");
			loggerWS.debug("Method: ExtractAPPFORVPMReference : getClearedMarkets");
			String sStartTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
			loggerWS.debug("Get Cleared Markets on APP started : {}" , sStartTime );

			// Get the user context
			if (Framework.isLoggedIn(req)) {
				context = Framework.getContext(req.getSession(false));				
			}

			loggerWS.debug("context user : {}" , context.getUser());
			//Pushing the context to User Agent
			ContextUtil.pushContext(context,pgApolloConstants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);		
			bContextPushed = true;
			
			String queryString = req.getQueryString();
			queryString =  java.net.URLDecoder.decode(queryString,"UTF-8");

			loggerWS.debug("QueryString  : {}" , queryString);

			if (UIUtil.isNotNullAndNotEmpty(queryString)) {
				arrayURLParam = queryString.split(pgApolloConstants.CONSTANT_STRING_AMPERSAND);				
				if (null!= arrayURLParam && arrayURLParam.length > 1) {					
					strObjectName = arrayURLParam[0];
					strObjectRevision = arrayURLParam[1];
					strObjectName = strObjectName.trim();
					strObjectRevision = strObjectRevision.trim();
					strVPMRefObjectId = pgApolloCommonUtil.getObjectId(context, pgApolloConstants.TYPE_VPMREFERENCE, strObjectName, strObjectRevision);
						if(UIUtil.isNotNullAndNotEmpty(strVPMRefObjectId))
						{
							strAPPObjectId = pgApolloCommonUtil.fetchAPPObjectId(context, strVPMRefObjectId);	
							loggerWS.debug("sAPPObjectId  : {}" , strAPPObjectId);
							StringList slObjectSelect = new StringList();
							slObjectSelect.addElement(DomainConstants.SELECT_NAME);
							if(UIUtil.isNotNullAndNotEmpty(strAPPObjectId))
							{
								DomainObject domAPPObject = DomainObject.newInstance(context, strAPPObjectId);								
								mlCleranceMarkets = domAPPObject.getRelatedObjects(context,//context
										pgV3Constants.RELATIONSHIP_PGPRODUCTCOUNTRYCLEARANCE, //Relationship Pattern
										CPNCommonConstants.TYPE_COUNTRY, // Type Pattern
										slObjectSelect,//Object Select
										null,//rel Select
										false,//get To
										true,//get From
										(short)1,//recurse level
										null,//object where Clause
										null,//rel where clause
										0); //object limit
								if(null != mlCleranceMarkets && !mlCleranceMarkets.isEmpty())
								{
									Map mpCleranceMarket = new HashMap();
									for(int i=0 ; i< mlCleranceMarkets.size() ; i++ )
									{
										mpCleranceMarket = new HashMap();
										mpCleranceMarket = (Map) mlCleranceMarkets.get(i);
										strObjectName = (String) mpCleranceMarket.get(DomainConstants.SELECT_NAME);
										slConnectedCleranceMarketObjectName.add(strObjectName);
									}
								}
								if(null!=slConnectedCleranceMarketObjectName && !slConnectedCleranceMarketObjectName.isEmpty())
								{
									strCleranceMarkets = FrameworkUtil.join(slConnectedCleranceMarketObjectName, pgApolloConstants.CONSTANT_STRING_PIPE);
									sbReturnMessages.append(strCleranceMarkets);
								}
								else
								{
									sbReturnMessages.append(pgApolloConstants.STR_AUTOMATION_BLANK);
								}
							}
						}
					
					if(sbReturnMessages.toString().isEmpty())
					{
						sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_NO_VPMREF_APP_FOUND);
					}
				}
				else
				{
					sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_AUTOMATION_ERROR_INVALID_PARAMETERS);
				}
			}
		} catch (Exception e) {
			loggerApolloTrace.error(e.getMessage(), e);
			sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON ).append(e.getLocalizedMessage());
		}
		finally 
		{
			if(bContextPushed){
				ContextUtil.popContext(context);
			}
			loggerWS.debug("\nFinal Response : {}" ,sbReturnMessages);
			loggerWS.debug("\n\n Get Cleared Markets on APP started ended-----------------");
			loggerWS.debug("\n******************************************************************************");
		}
		return Response.status(200).entity(sbReturnMessages.toString()).build();
	}
	
	
	
	/**
	 * Web service Method to get getModelUpdateStatus
	 * APOLLO 2018x.6 A10-1133
	 * Format : /fetchData/getModelUpdateStatus?<VPMreferenceName>&<VPMReferenceRevision>
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getModelUpdateStatus")
	public Response getModelUpdateStatus(@Context HttpServletRequest req) throws Exception {
		
		matrix.db.Context context = null;

		String[] arrayURLParam = new String[20];
		String strObjectName = DomainConstants.EMPTY_STRING;
		String strObjectRevision = DomainConstants.EMPTY_STRING;
		String strVPMRefObjectId = DomainConstants.EMPTY_STRING;	
		String strAPPObjectId = DomainConstants.EMPTY_STRING;
		String strpgLPDModelUpdateStatus = DomainConstants.EMPTY_STRING;
		StringBuilder sbReturnMessages = new StringBuilder();		
		boolean bContextPushed =false;		
		
		try {

			loggerWS.debug("******************************************************************************");
			loggerWS.debug("Path : /fetchData/getModelUpdateStatus");
			loggerWS.debug("Method: ExtractAPPFORVPMReference : getModelUpdateStatus");
			String sStartTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
			loggerWS.debug("Get getModelUpdateStatus on APP started : {}" , sStartTime );

			// Get the user context
			if (Framework.isLoggedIn(req)) {
				context = Framework.getContext(req.getSession(false));				
			}
			if(null != context) {
				loggerWS.debug("context user : {}" , context.getUser());
			}
			//Push context will be required in CATIA automation web services, as user will not have access to the objects.
			ContextUtil.pushContext(context,pgApolloConstants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);		
			bContextPushed = true;
			
			String queryString = req.getQueryString();
			queryString =  java.net.URLDecoder.decode(queryString,"UTF-8");

			loggerWS.debug("QueryString  : {}" , queryString);

			if (UIUtil.isNotNullAndNotEmpty(queryString)) 
			{
				arrayURLParam = queryString.split(pgApolloConstants.CONSTANT_STRING_AMPERSAND);				
				if (null!= arrayURLParam && arrayURLParam.length > 1) 
				{					
					strObjectName = arrayURLParam[0];
					strObjectRevision = arrayURLParam[1];
					strObjectName = strObjectName.trim();
					strObjectRevision = strObjectRevision.trim();
					strVPMRefObjectId = pgApolloCommonUtil.getObjectId(context, pgApolloConstants.TYPE_VPMREFERENCE, strObjectName, strObjectRevision);
					if(UIUtil.isNotNullAndNotEmpty(strVPMRefObjectId))
					{
							strAPPObjectId = pgApolloCommonUtil.fetchAPPObjectId(context, strVPMRefObjectId);	
							loggerWS.debug("sAPPObjectId  : {}" , strAPPObjectId);
							if(UIUtil.isNotNullAndNotEmpty(strAPPObjectId))
							{
								DomainObject domAPPObject = DomainObject.newInstance(context, strAPPObjectId);								
								strpgLPDModelUpdateStatus = domAPPObject.getInfo(context, pgApolloConstants.SELECT_ATTRIBUTE_PG_LPD_MODEL_UPDATE_STATUS);
								
								if(UIUtil.isNotNullAndNotEmpty(strpgLPDModelUpdateStatus))
								{
									sbReturnMessages.append(strpgLPDModelUpdateStatus);									
									sbReturnMessages.append(getVPMReferenceDetailsForModelUpdate(context, strpgLPDModelUpdateStatus, strVPMRefObjectId));									
								}
								else
								{
									sbReturnMessages.append(pgApolloConstants.STR_AUTOMATION_BLANK);
								}
							}
							else 
							{
								sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_NO_VPMREF_APP_FOUND);
							}
					} 
					else 
					{
						sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_NO_VPMREF_APP_FOUND);
					}
				}
				else
				{
					sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_AUTOMATION_ERROR_INVALID_PARAMETERS);
				}
			}
		} catch (Exception e) {
			loggerApolloTrace.error(e.getMessage(), e);
			sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON ).append(e.getLocalizedMessage());
		}
		finally 
		{
			if(bContextPushed){
				ContextUtil.popContext(context);
			}
			loggerWS.debug("\nFinal Response : {}" ,sbReturnMessages);
			loggerWS.debug("\n\n Get ModelUpdateStatus on APP started ended-----------------");
			loggerWS.debug("\n******************************************************************************");
		}
		return Response.status(200).entity(sbReturnMessages.toString()).build();
	}
	
	/**
	 * Method to get VPM Reference Model Update Status
	 * @param context
	 * @param strpgLPDModelUpdateStatus
	 * @param strVPMRefObjectId
	 * @return
	 * @throws Exception
	 */
	private String getVPMReferenceDetailsForModelUpdate (matrix.db.Context context, String strpgLPDModelUpdateStatus, String strVPMRefObjectId) throws Exception
	{
		StringBuilder sbReturnMessages = new StringBuilder();
		
		if(pgApolloConstants.RANGE_PG_LPD_MODEL_UPDATE_STATUS_MANDATORY.equalsIgnoreCase(strpgLPDModelUpdateStatus) || pgApolloConstants.RANGE_PG_LPD_MODEL_UPDATE_STATUS_OPTIONAL.equalsIgnoreCase(strpgLPDModelUpdateStatus))
		{										
				DomainObject domVPMRefObject =  DomainObject.newInstance(context, strVPMRefObjectId);						
				String strGenericModelStamped = domVPMRefObject.getInfo(context, pgApolloConstants.SELECT_ATTRIBUTE_PGLPDORIGINATEDFROMGENERICMODEL);
				
				if(UIUtil.isNotNullAndNotEmpty(strGenericModelStamped))
				{
					StringList slGenericModelStamped = StringUtil.split(strGenericModelStamped, pgApolloConstants.CONSTANT_STRING_PIPE);
					
					if(null != slGenericModelStamped && slGenericModelStamped.size() > 1)
					{
						String strGenericModelName = slGenericModelStamped.get(0);
						String strGenericModelRev = slGenericModelStamped.get(1);
						
						loggerWS.debug("\n strGenericModelName : {}" ,strGenericModelName);
						loggerWS.debug("\n strGenericModelRev : {}" ,strGenericModelRev);
						
						if(UIUtil.isNotNullAndNotEmpty(strGenericModelName) && UIUtil.isNotNullAndNotEmpty(strGenericModelRev))
						{
							StringList singleValueSelects = new StringList(DomainConstants.SELECT_ID);
							singleValueSelects.add(DomainConstants.SELECT_NAME);
							singleValueSelects.add(DomainConstants.SELECT_REVISION);
							singleValueSelects.add(DomainConstants.SELECT_CURRENT);
							singleValueSelects.add(DomainConstants.SELECT_ORIGINATED);											
							
							MapList mlVPMRefdetails = 	DomainObject.findObjects(context,  // Context
																				pgApolloConstants.TYPE_VPMREFERENCE, // Object Type Pattern
																				strGenericModelName, // Object Name Pattern
																				DomainConstants.QUERY_WILDCARD, // Revision
																				DomainConstants.QUERY_WILDCARD, //Owner
																				pgApolloConstants.VAULT_VPLM, // Vault
																				DomainConstants.EMPTY_STRING, // Where Clause
																				false, // Include Sub-types Flag
																				singleValueSelects); // Object Selects
							
							

							if(null != mlVPMRefdetails && !mlVPMRefdetails.isEmpty()) 
							{
								mlVPMRefdetails.addSortKey(DomainConstants.SELECT_ORIGINATED, "ascending", "date");
								mlVPMRefdetails.sort();
								
								loggerWS.debug("\n After Sorting mlVPMRefdetails : {}" ,mlVPMRefdetails);

								boolean isRevMatch = false;
								String modelRev;
								String modelState;
								Map temp;
								for(int i=0;i<mlVPMRefdetails.size();i++) 
								{
									temp=(Map)mlVPMRefdetails.get(i);
									modelRev = (String)temp.get(DomainConstants.SELECT_REVISION);
									
									if(modelRev.equalsIgnoreCase(strGenericModelRev))
									{
										isRevMatch = true;
										continue;
									}
									if(isRevMatch) 
									{														
										modelState = (String)temp.get(DomainConstants.SELECT_CURRENT);

										if(pgApolloConstants.STATE_OBSOLETE_CATIA.equalsIgnoreCase(modelState) || pgApolloConstants.STATE_SHARED.equalsIgnoreCase(modelState)) {
											sbReturnMessages.append(pgApolloConstants.CONSTANT_STRING_TILDA).append(pgApolloConstants.CONSTANT_STRING_TILDA).append(strGenericModelName).append(pgApolloConstants.CONSTANT_STRING_PIPE).append(modelRev);
										}
									}													
								}
							}
							
						}
						
						
					}
					
				
				}
				
									
		}		
		
		return sbReturnMessages.toString();
	}

	/**
	 * Web service Method to get pgLPDReasonForChangeGenericModel
	 * APOLLO 2018x.6 A10-1134
	 * Format : /fetchData/getReasonForChangeInGenericModel?<VPMreferenceName>&<VPMReferenceRevision>
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getReasonForChangeInGenericModel")
	public Response getReasonForChangeInGenericModel(@Context HttpServletRequest req) throws Exception {
		
		matrix.db.Context context = null;

		String[] arrayURLParam = new String[20];
		String strObjectName = DomainConstants.EMPTY_STRING;
		String strObjectRevision = DomainConstants.EMPTY_STRING;
		String strVPMRefObjectId = DomainConstants.EMPTY_STRING;	
		String strAPPObjectId = DomainConstants.EMPTY_STRING;
		String strReasonForChangeInGenericModel = DomainConstants.EMPTY_STRING;

		StringBuilder sbReturnMessages = new StringBuilder();
		boolean bContextPushed =false;		
		
		try {

			loggerWS.debug("******************************************************************************");
			loggerWS.debug("Path : /fetchData/getReasonForChangeInGenericModel");
			loggerWS.debug("Method: ExtractAPPFORVPMReference : getReasonForChangeInGenericModel");
			String sStartTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
			loggerWS.debug("Get ReasonForChangeInGenericModel of Generic Model started : {}" , sStartTime );

			// Get the user context
			if (Framework.isLoggedIn(req)) {
				context = Framework.getContext(req.getSession(false));				
			}
			if(null != context) {
				loggerWS.debug("context user : {}" , context.getUser());
			}
			//Push context will be required in CATIA automation web services, as user will not have access to the objects.
			ContextUtil.pushContext(context,pgApolloConstants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);		
			bContextPushed = true;
			
			String queryString = req.getQueryString();
			queryString =  java.net.URLDecoder.decode(queryString,"UTF-8");

			loggerWS.debug("QueryString  : {}" , queryString);

			if (UIUtil.isNotNullAndNotEmpty(queryString)) {
				arrayURLParam = queryString.split(pgApolloConstants.CONSTANT_STRING_AMPERSAND);				
				if (null!= arrayURLParam && arrayURLParam.length > 1) {					
					strObjectName = arrayURLParam[0];
					strObjectRevision = arrayURLParam[1];
					strObjectName = strObjectName.trim();
					strObjectRevision = strObjectRevision.trim();
					strVPMRefObjectId = pgApolloCommonUtil.getObjectId(context, pgApolloConstants.TYPE_VPMREFERENCE, strObjectName, strObjectRevision);
					if(UIUtil.isNotNullAndNotEmpty(strVPMRefObjectId))
					{
						strAPPObjectId = pgApolloCommonUtil.fetchAPPObjectId(context, strVPMRefObjectId);	
						loggerWS.debug("sAPPObjectId  : {}" , strAPPObjectId);
						if(UIUtil.isNotNullAndNotEmpty(strAPPObjectId))
						{
							DomainObject domAPPObject = DomainObject.newInstance(context, strAPPObjectId);								
							strReasonForChangeInGenericModel = domAPPObject.getInfo(context, pgApolloConstants.SELECT_ATTRIBUTE_PG_LPD_REASON_FOR_CHANGE_GENERIC_MODEL);

							if(UIUtil.isNotNullAndNotEmpty(strReasonForChangeInGenericModel))
							{
								sbReturnMessages.append(strReasonForChangeInGenericModel);
							}
							else
							{
								sbReturnMessages.append(pgApolloConstants.STR_AUTOMATION_BLANK);
							}
						} else {
							sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_NO_VPMREF_APP_FOUND);
						}						
					} else {
						sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_NO_VPMREF_APP_FOUND);
					}
				}
				else
				{
					sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_AUTOMATION_ERROR_INVALID_PARAMETERS);
				}
			}
		} catch (Exception e) {
			loggerApolloTrace.error(e.getMessage(), e);
			sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON ).append(e.getLocalizedMessage());
		}
		finally 
		{
			if(bContextPushed){
				ContextUtil.popContext(context);
			}
			loggerWS.debug("\nFinal Response : {}" ,sbReturnMessages);
			loggerWS.debug("\n\n Get ReasonForChangeInGenericModel on APP started ended-----------------");
			loggerWS.debug("\n******************************************************************************");
		}
		return Response.status(200).entity(sbReturnMessages.toString()).build();
	}
	
	
	/**
	 * Web service Method to reset attribute pgLPDModelUpdateStatus
	 * APOLLO 2018x.6 A10-1135
	 * Format : /fetchData/resetModelUpdateStatus?<VPMreferenceName>&<VPMReferenceRevision>
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/resetModelUpdateStatus")
	public Response resetModelUpdateStatus(@Context HttpServletRequest req) throws Exception {
		
		matrix.db.Context context = null;

		String[] arrayURLParam = new String[20];
		String strObjectName = DomainConstants.EMPTY_STRING;
		String strObjectRevision = DomainConstants.EMPTY_STRING;
		String strVPMRefObjectId = DomainConstants.EMPTY_STRING;	
		String strAPPObjectId = DomainConstants.EMPTY_STRING;

		StringBuilder sbReturnMessages = new StringBuilder();
		boolean bContextPushed =false;		
		
		try {

			loggerWS.debug("******************************************************************************");
			loggerWS.debug("Path : /fetchData/resetModelUpdateStatus");
			loggerWS.debug("Method: ExtractAPPFORVPMReference : resetModelUpdateStatus");
			String sStartTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
			loggerWS.debug("ResetModelUpdateStatus of APP started : {}" , sStartTime );

			// Get the user context
			if (Framework.isLoggedIn(req)) {
				context = Framework.getContext(req.getSession(false));				
			}
			if(null != context) {
				loggerWS.debug("context user : {}" , context.getUser());
			}
			//Push context will be required in CATIA automation web services, as user will not have access to the objects.
			ContextUtil.pushContext(context,pgApolloConstants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);		
			bContextPushed = true;
			
			String queryString = req.getQueryString();
			queryString =  java.net.URLDecoder.decode(queryString,"UTF-8");

			loggerWS.debug("QueryString  : {}" , queryString);

			if (UIUtil.isNotNullAndNotEmpty(queryString)) 
			{
				arrayURLParam = queryString.split(pgApolloConstants.CONSTANT_STRING_AMPERSAND);				
				if (null!= arrayURLParam && arrayURLParam.length > 1) {					
					strObjectName = arrayURLParam[0];
					strObjectRevision = arrayURLParam[1];
					strObjectName = strObjectName.trim();
					strObjectRevision = strObjectRevision.trim();
					strVPMRefObjectId = pgApolloCommonUtil.getObjectId(context, pgApolloConstants.TYPE_VPMREFERENCE, strObjectName, strObjectRevision);
					if(UIUtil.isNotNullAndNotEmpty(strVPMRefObjectId))
					{
						strAPPObjectId = pgApolloCommonUtil.fetchAPPObjectId(context, strVPMRefObjectId);	
						loggerWS.debug("sAPPObjectId  : {}" , strAPPObjectId);
						if(UIUtil.isNotNullAndNotEmpty(strAPPObjectId))
						{
							DomainObject domAPPObject = DomainObject.newInstance(context, strAPPObjectId);	
							domAPPObject.setAttributeValue(context, pgApolloConstants.ATTRIBUTE_PG_LPD_MODEL_UPDATE_STATUS, pgApolloConstants.RANGE_PG_LPD_MODEL_UPDATE_STATUS_CURRENT);
							sbReturnMessages.append(pgApolloConstants.STR_SUCCESS);
						}
					}					
					if(sbReturnMessages.toString().isEmpty())
					{
						sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_NO_VPMREF_APP_FOUND);
					}
				}
				else
				{
					sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_AUTOMATION_ERROR_INVALID_PARAMETERS);
				}
			}
		} catch (Exception e) {
			loggerApolloTrace.error(e.getMessage(), e);
			sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON ).append(e.getLocalizedMessage());
		}
		finally 
		{
			if(bContextPushed){
				ContextUtil.popContext(context);
			}
			loggerWS.debug("\nFinal Response : {}" ,sbReturnMessages);
			loggerWS.debug("\n\n ResetModelUpdateStatus on APP ended-----------------");
			loggerWS.debug("\n******************************************************************************");
		}
		return Response.status(200).entity(sbReturnMessages.toString()).build();
	}	
		
}


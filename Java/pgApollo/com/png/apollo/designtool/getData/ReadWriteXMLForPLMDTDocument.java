/*
 * Added by APOLLO Team For CATIA Automation Web Services
 * 2018x.2 Change to read and write XML for Base and Variant information
 */

package com.png.apollo.designtool.getData;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.servlet.Framework;
import com.png.apollo.pgApolloCommonUtil;
import com.png.apollo.pgApolloConstants;

import matrix.db.FileList;
import matrix.util.MatrixException;
import matrix.util.StringList;

@Path("/xmlReadWriteService")
public class ReadWriteXMLForPLMDTDocument {

	 private static final Logger loggerSync = LoggerFactory.getLogger("APOLLOSYNC");    
	 private static final Logger loggerWS = LoggerFactory.getLogger("APOLLOWS");
	 private static final org.apache.log4j.Logger loggerApolloTrace = org.apache.log4j.Logger.getLogger(ReadWriteXMLForPLMDTDocument.class);
	
	/**
	 * Method to fetch Configurations from VPMReference/APP 2.1 Solution 
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/readXML")
	public Response getXMLConfigurationsWithNewFormat(@Context HttpServletRequest req) throws Exception {

		matrix.db.Context context = null;		

		String strNoConfigurations = pgApolloConstants.STR_AUTOMATION_ERROR_NO_CONFIGURATION;

		StringBuilder sbReturnMessages = new StringBuilder();
		boolean bContextPushed =false;

		try
		{

			loggerWS.debug("******************************************************************************");
			loggerWS.debug("Path : /xmlReadWriteService/readXML");
			loggerWS.debug("Method: ReadWriteXMLForPLMDTDocument : getXMLConfigurationsWithNewFormat");
			String strStartDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
			loggerWS.debug("Checkout and Read XML Started : {}"  , strStartDate  );

			// Get the user context
			if (Framework.isLoggedIn(req)) {
				context = Framework.getContext(req.getSession(false));				
			}
			
			if(null!=context)
			{
				String strContextUser = context.getUser();
				loggerWS.debug("context  : {}" , strContextUser);			
			}
			//Pushing the context to User Agent
			//This is webservice used by Automation in CATIA. Context user will not always have access on all Base objects in the database. 
			//So push context is necessary to find out all the Base objects in the database.
			ContextUtil.pushContext(context, pgApolloConstants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
			bContextPushed = true;
			
			String queryString = req.getQueryString();			
			
			
			sbReturnMessages = readXML(context, strNoConfigurations, queryString);
		} 
		catch (Exception e)
		{
			loggerApolloTrace.error(e.getMessage(), e);
			sbReturnMessages.append(pgApolloConstants.STR_ERROR+ pgApolloConstants.CONSTANT_STRING_COLON + e.getLocalizedMessage());
		}
		finally 
		{			
			if(bContextPushed)
			{
				ContextUtil.popContext(context);
			}			
		}
		return Response.status(200).entity(sbReturnMessages.toString()).build();
	}	
	

	/**
	 * Method to read XML web service
	 * @param context
	 * @param strNoConfigurations
	 * @param queryString
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws MatrixException
	 * @throws Exception
	 */
	private StringBuilder readXML(matrix.db.Context context, String strNoConfigurations, String queryString) throws Exception {
		
		StringBuilder sbReturnMessages = new StringBuilder();

		queryString =  java.net.URLDecoder.decode(queryString,"UTF-8");

		loggerWS.debug("queryString  : {}" , queryString );
		
		StringList slURLParam = new StringList();
		//APOLLO 2018x.5 A10-531 - Added Granularity for Read XML Webservices Starts
		StringBuilder sbWhereClause = new StringBuilder();
		//APOLLO 2018x.5 A10-531 - Added Granularity for Read XML Webservices Ends

		String strMode = DomainConstants.EMPTY_STRING;
		String strParameter = DomainConstants.EMPTY_STRING;
		
		String strVPMRefSize = DomainConstants.EMPTY_STRING;
		String strVPMRefRegion = DomainConstants.EMPTY_STRING;
		String strVPMRefSubRegion = DomainConstants.EMPTY_STRING;
		
		//APOLLO 2018x.5 A10-531 - Added Granularity for Read XML Webservices Starts
		String strVPMRefBusinessArea = DomainConstants.EMPTY_STRING;
		String strVPMRefProductCategoryPlatform = DomainConstants.EMPTY_STRING;
		String strVPMRefProductTechnologyPlatform = DomainConstants.EMPTY_STRING;
		//APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5 Starts
		String strVPMRefProductTechnologyChassis = DomainConstants.EMPTY_STRING;
		String strVPMRefDefinition = DomainConstants.EMPTY_STRING;
		String strMaturityState = DomainConstants.EMPTY_STRING; //Apollo A10-886 Changes 
		String strVPMRefDetails = DomainConstants.EMPTY_STRING;
		String strVPMRefName = DomainConstants.EMPTY_STRING;
		String strVPMRefRevision = DomainConstants.EMPTY_STRING;
		StringList slVPMRefDetails = new StringList();
		int iURLParamSize = 0;
		boolean isLatestFlag = false;
		//APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5 Ends
		//APOLLO 2018x.5 A10-531 - Added Granularity for Read XML Webservices Ends

		String strFinalOutput = DomainConstants.EMPTY_STRING;
		String objectName = DomainConstants.EMPTY_STRING;
		String objectRevision = DomainConstants.EMPTY_STRING;
		String objectDetails = DomainConstants.EMPTY_STRING;
		//APOLLO 2018x.5 A10-479 - Read Webservice change for Variant Parts Starts
		StringBuilder sbObjectDetails = new StringBuilder();
		String strAPPName = DomainConstants.EMPTY_STRING;
		String strAPPRevision = DomainConstants.EMPTY_STRING;
		//APOLLO 2018x.5 A10-479 - Read Webservice change for Variant Parts Ends
		
		String objectState = DomainConstants.EMPTY_STRING;
		String objectReleasePhase = DomainConstants.EMPTY_STRING;

		
		String strWhere = DomainConstants.EMPTY_STRING;
		MapList mlVPMRefMapList = new MapList();
		StringList slFinalOutput = new StringList();
		int iVPMRefMapListSize = 0;
		
		Map map = new HashMap();
		
		if(UIUtil.isNotNullAndNotEmpty(queryString))
		{
			slURLParam = StringUtil.split(queryString, pgApolloConstants.CONSTANT_STRING_AMPERSAND);
			strMode = slURLParam.get(0);
			strMode = strMode.trim();
			//APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5 Starts
			iURLParamSize = slURLParam.size();
			//APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5 Ends
		}			
		loggerWS.debug("strMode = {}" , strMode );	
		//APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5 Starts
		if(pgApolloConstants.STR_READ_MODE_GETCONFIGURATION.equalsIgnoreCase(strMode) || pgApolloConstants.STR_READ_MODE_GETALLBASES.equalsIgnoreCase(strMode))
		//APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5 Ends
		{
			//APOLLO 2018x.5 A10-531 - Added Granularity for Read XML Webservices Starts
			if(iURLParamSize>=5)
			{
				strVPMRefBusinessArea = slURLParam.get(1);
				strVPMRefProductCategoryPlatform = slURLParam.get(2);
				strVPMRefProductTechnologyPlatform = slURLParam.get(3);
				strVPMRefProductTechnologyChassis = slURLParam.get(4);
			}										
			sbWhereClause = new StringBuilder();
			sbWhereClause.append(pgApolloConstants.SELECT_ATTRIBUTE_PGLPDMODELTYPE).append(pgApolloConstants.CONSTANT_STRING_EQUAL_SIGN).append(pgApolloConstants.CONSTANT_STRING_EQUAL_SIGN).append("'" ).append(pgApolloConstants.RANGE_VALUE_BASE).append("'");
			sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE).append(pgApolloConstants.CONSTANT_STRING_AMPERSAND).append(pgApolloConstants.CONSTANT_STRING_AMPERSAND).append(pgApolloConstants.CONSTANT_STRING_SPACE).append(DomainConstants.SELECT_CURRENT).append(pgApolloConstants.CONSTANT_STRING_EQUAL_SIGN).append(pgApolloConstants.CONSTANT_STRING_EQUAL_SIGN).append(pgApolloConstants.STATE_SHARED);
			sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE).append(pgApolloConstants.CONSTANT_STRING_AMPERSAND).append(pgApolloConstants.CONSTANT_STRING_AMPERSAND).append(pgApolloConstants.CONSTANT_STRING_SPACE).append(pgApolloConstants.SELECT_ATTRIBUTE_PGBUSINESSAREA ).append(pgApolloConstants.CONSTANT_STRING_EQUAL_SIGN).append(pgApolloConstants.CONSTANT_STRING_EQUAL_SIGN).append("'").append( strVPMRefBusinessArea ).append("'"); 

			Map mapCriteria = new HashMap();
			mapCriteria.put(pgApolloConstants.SELECT_ATTRIBUTE_PGPRODUCTCATEGORYPLATFORM, strVPMRefProductCategoryPlatform);
			mapCriteria.put(pgApolloConstants.SELECT_ATTRIBUTE_PGPRODUCTTECHNOLOGYPLATFORM, strVPMRefProductTechnologyPlatform);
			mapCriteria.put(pgApolloConstants.SELECT_ATTRIBUTE_PGPRODUCTTECHNOLOGYCHASSIS, strVPMRefProductTechnologyChassis);
			if(iURLParamSize>=8)
			{
				strVPMRefRegion = slURLParam.get(5);
				strVPMRefSubRegion = slURLParam.get(6);
				strVPMRefSize = slURLParam.get(7);
				
				mapCriteria.put(pgApolloConstants.SELECT_ATTRIBUTE_PGSIZE, strVPMRefSize);
				mapCriteria.put(pgApolloConstants.SELECT_ATTRIBUTE_PGREGION, strVPMRefRegion);
				mapCriteria.put(pgApolloConstants.SELECT_ATTRIBUTE_PGSUBREGION, strVPMRefSubRegion);
			}
			if(iURLParamSize == 9)
			{
				strVPMRefDefinition = slURLParam.get(8);
			
				mapCriteria.put(pgApolloConstants.SELECT_ATTRIBUTE_PGDEFINITION, strVPMRefDefinition);
			}
			
			strWhere = sbWhereClause.toString();
			loggerWS.debug(" strWhere = {}" , strWhere );			
			//APOLLO 2018x.5 A10-531 - Added Granularity for Read XML Webservices Ends
			
			//APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5 Starts
			mlVPMRefMapList = fetchVPMRefList(context, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING, strWhere, strMode, false, mapCriteria);
			//APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5 Ends
			loggerWS.debug("VPMRefMapList = {}" , mlVPMRefMapList );			
			
			//APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5 Starts
			strFinalOutput = fetchConfigurations(context, mlVPMRefMapList, strMode);
			//APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5 Ends
			loggerWS.debug("strFinalOutput = {}" , strFinalOutput );	
			
			if(UIUtil.isNotNullAndNotEmpty(strFinalOutput))
			{
				sbReturnMessages.append(strFinalOutput);
			}
			else
			{
				//APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5 Starts
				if(pgApolloConstants.STR_READ_MODE_GETALLBASES.equalsIgnoreCase(strMode))
				{
					sbReturnMessages.append(pgApolloConstants.STR_AUTOMATION_ERROR_NO_BASE_FOUND);
				}
				else
				{
					sbReturnMessages.append(strNoConfigurations);
				}
				//APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5 Ends
			}
		}
		else
		{
		//APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5 Starts
			//APOLLO 2018x.5 A10-531 - Added Granularity for Read XML Webservices Starts
			Map mapCriteria = new HashMap();

			if(iURLParamSize>= 9)
			{
				strVPMRefBusinessArea = slURLParam.get(1);
				strVPMRefProductCategoryPlatform = slURLParam.get(2);
				strVPMRefProductTechnologyPlatform = slURLParam.get(3);
				strVPMRefProductTechnologyChassis = slURLParam.get(4);
				strVPMRefRegion = slURLParam.get(5);
				strVPMRefSubRegion = slURLParam.get(6);
				strVPMRefSize = slURLParam.get(7);
				strVPMRefDefinition = slURLParam.get(8);	
				//Apollo A10-886 Changes Starts
				if(iURLParamSize >= 10) {
					strMaturityState = slURLParam.get(9);
				}
				//Apollo A10-886 Ends
				
				loggerWS.debug("strVPMRefBusinessArea : {}" , strVPMRefBusinessArea);
				loggerWS.debug("strVPMRefProductCategoryPlatform : {}" , strVPMRefProductCategoryPlatform);
				loggerWS.debug("strVPMRefProductTechnologyPlatform : {}" , strVPMRefProductTechnologyPlatform);
				loggerWS.debug("strVPMRefProductTechnologyChassis : {}" , strVPMRefProductTechnologyChassis);
				loggerWS.debug("strVPMRefRegion : {}" , strVPMRefRegion);
				loggerWS.debug("strVPMRefSubRegion : {}" , strVPMRefSubRegion);
				loggerWS.debug("strVPMRefSize : {}" , strVPMRefSize);
				loggerWS.debug("strVPMRefDefinition : {}" , strVPMRefDefinition);
				loggerWS.debug("strMaturityState : {}" , strMaturityState); //Apollo A10-886 Changes 
				sbWhereClause = new StringBuilder();
				sbWhereClause.append(pgApolloConstants.SELECT_ATTRIBUTE_PGBUSINESSAREA ).append(pgApolloConstants.CONSTANT_STRING_EQUAL_SIGN).append(pgApolloConstants.CONSTANT_STRING_EQUAL_SIGN).append("'").append( strVPMRefBusinessArea ).append("'"); 
				
				mapCriteria.put(pgApolloConstants.SELECT_ATTRIBUTE_PGPRODUCTCATEGORYPLATFORM, strVPMRefProductCategoryPlatform);
				mapCriteria.put(pgApolloConstants.SELECT_ATTRIBUTE_PGPRODUCTTECHNOLOGYPLATFORM, strVPMRefProductTechnologyPlatform);
				mapCriteria.put(pgApolloConstants.SELECT_ATTRIBUTE_PGPRODUCTTECHNOLOGYCHASSIS, strVPMRefProductTechnologyChassis);
				mapCriteria.put(pgApolloConstants.SELECT_ATTRIBUTE_PGSIZE, strVPMRefSize);
				mapCriteria.put(pgApolloConstants.SELECT_ATTRIBUTE_PGREGION, strVPMRefRegion);
				mapCriteria.put(pgApolloConstants.SELECT_ATTRIBUTE_PGSUBREGION, strVPMRefSubRegion);
				mapCriteria.put(pgApolloConstants.SELECT_ATTRIBUTE_PGDEFINITION, strVPMRefDefinition);

			}
			if(iURLParamSize< 9  && iURLParamSize> 1)
			{
				strVPMRefDetails = slURLParam.get(1);
				slVPMRefDetails = StringUtil.split(strVPMRefDetails, pgApolloConstants.CONSTANT_STRING_PIPE);
				if(slVPMRefDetails.size()>1)
				{
					strVPMRefName = slVPMRefDetails.get(0);
					strVPMRefRevision = slVPMRefDetails.get(1);
				}					
			}
			//APOLLO 2018x.5 A10-531 - Added Granularity for Read XML Webservices Ends
			if(!sbWhereClause.toString().isEmpty() || (UIUtil.isNotNullAndNotEmpty(strVPMRefName) && UIUtil.isNotNullAndNotEmpty(strVPMRefRevision)))
			{				
				if(pgApolloConstants.STR_READ_MODE_GETVARIANTPARTS.equalsIgnoreCase(strMode) || pgApolloConstants.STR_READ_MODE_GETINWORKBASEPARTS.equalsIgnoreCase(strMode))
				{
					if(pgApolloConstants.STR_READ_MODE_GETINWORKBASEPARTS.equalsIgnoreCase(strMode))
					{
						sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE).append(pgApolloConstants.CONSTANT_STRING_AMPERSAND).append(pgApolloConstants.CONSTANT_STRING_AMPERSAND).append(pgApolloConstants.CONSTANT_STRING_SPACE).append(pgApolloConstants.SELECT_ATTRIBUTE_PGLPDMODELTYPE ).append(pgApolloConstants.CONSTANT_STRING_EQUAL_SIGN).append(pgApolloConstants.CONSTANT_STRING_EQUAL_SIGN).append("'").append(pgApolloConstants.RANGE_VALUE_BASE).append("'");
					}
					else
					{
						sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE).append(pgApolloConstants.CONSTANT_STRING_DOUBLE_AMPERSAND).append(pgApolloConstants.CONSTANT_STRING_SPACE).append(pgApolloConstants.SELECT_ATTRIBUTE_PGLPDMODELTYPE ).append(pgApolloConstants.CONSTANT_STRING_DOUBLE_EQUAL).append(pgApolloConstants.CONSTANT_STRING_SINGLE_QUOTE).append(pgApolloConstants.RANGE_VALUE_VARIANT).append(pgApolloConstants.CONSTANT_STRING_SINGLE_QUOTE);
						
						//Apollo A10-886 Changes Starts
						if(UIUtil.isNotNullAndNotEmpty(strMaturityState) && !strMaturityState.equalsIgnoreCase(pgApolloConstants.STR_ALL)) {
							sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE).append(pgApolloConstants.CONSTANT_STRING_DOUBLE_AMPERSAND).append(pgApolloConstants.CONSTANT_STRING_SPACE).append(DomainConstants.SELECT_CURRENT).append(pgApolloConstants.CONSTANT_STRING_DOUBLE_EQUAL).append(pgApolloConstants.CONSTANT_STRING_SINGLE_QUOTE).append(strMaturityState).append(pgApolloConstants.CONSTANT_STRING_SINGLE_QUOTE);
						}else if(UIUtil.isNullOrEmpty(strMaturityState) || strMaturityState.equalsIgnoreCase(pgApolloConstants.STR_ALL)) { //A10-912 Changes Start
							sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE).append(pgApolloConstants.CONSTANT_STRING_DOUBLE_AMPERSAND).append(pgApolloConstants.CONSTANT_STRING_SPACE).append(DomainConstants.SELECT_CURRENT).append(pgApolloConstants.CONSTANT_STRING_NOT_EQUAL_SIGN).append(pgApolloConstants.CONSTANT_STRING_SINGLE_QUOTE).append(pgApolloConstants.STATE_OBSOLETE_CATIA).append(pgApolloConstants.CONSTANT_STRING_SINGLE_QUOTE);
						}// A10-912 Changes Ends
						//Apollo A10-886 Changes Ends
						
					}
					
					strWhere = sbWhereClause.toString();
					//APOLLO 2018x.5 A10-531 - Added Granularity for Read XML Webservices Ends
					loggerWS.debug("Where Clause : {}" , strWhere);

					mlVPMRefMapList = fetchVPMRefList(context, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING, strWhere, strMode, false, mapCriteria);
					
					loggerWS.debug("VPMRefMapList : {}" , mlVPMRefMapList);
					
					if(mlVPMRefMapList!=null)
					{
						iVPMRefMapListSize = mlVPMRefMapList.size();
						if(iVPMRefMapListSize>0)
						{
							for(int i=0;i<mlVPMRefMapList.size();i++)
							{
								map = (Map)mlVPMRefMapList.get(i);
								//APOLLO 2018x.5 A10-479 - Read Webservice change for Variant Parts Starts
								sbObjectDetails = new StringBuilder();
								objectDetails = DomainConstants.EMPTY_STRING;
								objectName = (String)map.get(DomainConstants.SELECT_NAME);
								objectState = (String)map.get(DomainConstants.SELECT_CURRENT);
								objectState = EnoviaResourceBundle.getStateI18NString(context,pgApolloConstants.POLICY_VPLM_SMB_DEFINITION ,objectState, Locale.ENGLISH.getLanguage());				
								objectReleasePhase = (String)map.get(pgApolloConstants.SELECT_ATTRIBUTE_PGMANUFACTURING_MATURITYSTATUS);
								strAPPName = (String)map.getOrDefault("to["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION +pgApolloConstants.CONSTANT_STRING_SELECT_FROMNAME, DomainConstants.EMPTY_STRING);
								objectRevision = (String)map.get(DomainConstants.SELECT_REVISION);
								strAPPRevision = (String)map.getOrDefault("to["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION +pgApolloConstants.CONSTANT_STRING_SELECT_FROMREVISION, DomainConstants.EMPTY_STRING);
								sbObjectDetails.append(objectName).append(pgApolloConstants.CONSTANT_STRING_COMMA).append(objectRevision);
								sbObjectDetails.append(pgApolloConstants.CONSTANT_STRING_COMMA).append(strAPPName).append(pgApolloConstants.CONSTANT_STRING_COMMA).append(strAPPRevision);
								sbObjectDetails.append(pgApolloConstants.CONSTANT_STRING_COMMA).append(objectReleasePhase).append(pgApolloConstants.CONSTANT_STRING_COMMA).append(objectState);
								objectDetails = sbObjectDetails.toString(); 
								//APOLLO 2018x.5 A10-479 - Read Webservice change for Variant Parts Ends
								if(!slFinalOutput.contains(objectDetails))
								{
									slFinalOutput.add(objectDetails);
								}
							}
						}							
					}
					if(null!=slFinalOutput && !slFinalOutput.isEmpty())
					{
						strFinalOutput = StringUtil.join(slFinalOutput, pgApolloConstants.CONSTANT_STRING_PIPE);
					}
					if(UIUtil.isNotNullAndNotEmpty(strFinalOutput))
					{
						sbReturnMessages.append(strFinalOutput);
					}
					else
					{
						sbReturnMessages.append(strNoConfigurations);
					}
				}
				else if(pgApolloConstants.STR_READ_MODE_ALLPARAMETERDETAILS.equalsIgnoreCase(strMode) || pgApolloConstants.STR_READ_MODE_GETDELTAPARAMSFORVARIANTS.equalsIgnoreCase(strMode) || pgApolloConstants.STR_READ_MODE_GETDELTAPERFCHARFORVARIANTS.equalsIgnoreCase(strMode) || pgApolloConstants.STR_READ_MODE_GETALLPERFCHARACDETAILS.equalsIgnoreCase(strMode))
				{
						//APOLLO 2018x.5 A10-531 - Added Granularity for Read XML Webservices Starts
						if(!sbWhereClause.toString().isEmpty())
						{
							sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE).append(pgApolloConstants.CONSTANT_STRING_AMPERSAND).append(pgApolloConstants.CONSTANT_STRING_AMPERSAND).append(pgApolloConstants.CONSTANT_STRING_SPACE).append(pgApolloConstants.SELECT_ATTRIBUTE_PGLPDMODELTYPE).append(pgApolloConstants.CONSTANT_STRING_EQUAL_SIGN).append(pgApolloConstants.CONSTANT_STRING_EQUAL_SIGN).append("'" ).append(pgApolloConstants.RANGE_VALUE_BASE).append("'");
							sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE).append(pgApolloConstants.CONSTANT_STRING_AMPERSAND).append(pgApolloConstants.CONSTANT_STRING_AMPERSAND).append(pgApolloConstants.CONSTANT_STRING_SPACE).append(DomainConstants.SELECT_CURRENT).append(pgApolloConstants.CONSTANT_STRING_EQUAL_SIGN).append(pgApolloConstants.CONSTANT_STRING_EQUAL_SIGN).append(pgApolloConstants.STATE_SHARED);
							isLatestFlag = true;
						}
						//APOLLO 2018x.5 A10-531 - Added Granularity for Read XML Webservices Ends							
						//APOLLO 2018x.5 A10-531 - Added Granularity for Read XML Webservices Starts
						strWhere = sbWhereClause.toString();
						//APOLLO 2018x.5 A10-531 - Added Granularity for Read XML Webservices Ends
						
						loggerWS.debug("Where Clause : {}" , strWhere);

						mlVPMRefMapList = fetchVPMRefList(context, strVPMRefName, strVPMRefRevision, strWhere, strMode, isLatestFlag, mapCriteria);
						
						loggerWS.debug("VPMRefMapList : {}" , mlVPMRefMapList);				
					
					if(mlVPMRefMapList!=null && !mlVPMRefMapList.isEmpty())
					{
						strFinalOutput = readConfigurationsFromXML(context, mlVPMRefMapList, null, strMode);							
					}
					else
					{
						sbReturnMessages.append(pgApolloConstants.STR_EXTRACT_OBJECTNOTFOUND);
					}
					
					loggerWS.debug("strFinalOutput : {}" , strFinalOutput);
					
					if(UIUtil.isNotNullAndNotEmpty(strFinalOutput))
					{
						sbReturnMessages.append(strFinalOutput);
					}					
				}
				//APOLLO 2018x.5 A10-508 - Read Webservice - Get Performance Characteristic Value Starts
				else if(pgApolloConstants.STR_READ_MODE_GETPARAMETERVALUES.equalsIgnoreCase(strMode) || pgApolloConstants.STR_READ_MODE_GETPERFORMANCECHARVALUES.equalsIgnoreCase(strMode))
				//APOLLO 2018x.5 A10-508 - Read Webservice - Get Performance Characteristic Value Ends
				{
					if(iURLParamSize> 9)
					{
						strParameter = slURLParam.get(9);
					}
					else if(iURLParamSize> 2 && (UIUtil.isNotNullAndNotEmpty(strVPMRefName) && UIUtil.isNotNullAndNotEmpty(strVPMRefRevision)))
					{
						strParameter = slURLParam.get(2);
					}
					strParameter = strParameter.trim();
					if(!sbWhereClause.toString().isEmpty())
					{
						sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE).append(pgApolloConstants.CONSTANT_STRING_AMPERSAND).append(pgApolloConstants.CONSTANT_STRING_AMPERSAND).append(pgApolloConstants.CONSTANT_STRING_SPACE).append(pgApolloConstants.SELECT_ATTRIBUTE_PGLPDMODELTYPE).append(pgApolloConstants.CONSTANT_STRING_EQUAL_SIGN).append(pgApolloConstants.CONSTANT_STRING_EQUAL_SIGN).append("'" ).append(pgApolloConstants.RANGE_VALUE_BASE).append("'");
						sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE).append(pgApolloConstants.CONSTANT_STRING_AMPERSAND).append(pgApolloConstants.CONSTANT_STRING_AMPERSAND).append(pgApolloConstants.CONSTANT_STRING_SPACE).append(DomainConstants.SELECT_CURRENT).append(pgApolloConstants.CONSTANT_STRING_EQUAL_SIGN).append(pgApolloConstants.CONSTANT_STRING_EQUAL_SIGN).append(pgApolloConstants.STATE_SHARED);
						isLatestFlag = true;
					}					
						//APOLLO 2018x.5 A10-531 - Added Granularity for Read XML Webservices Starts
						strWhere = sbWhereClause.toString();
						//APOLLO 2018x.5 A10-531 - Added Granularity for Read XML Webservices Ends
						
						loggerWS.debug("Where Clause : {}" , strWhere);

						mlVPMRefMapList = fetchVPMRefList(context, strVPMRefName, strVPMRefRevision, strWhere, strMode, isLatestFlag, mapCriteria);
						
						loggerWS.debug("VPMRefMapList : {}" , mlVPMRefMapList);					
					if(mlVPMRefMapList!=null && !mlVPMRefMapList.isEmpty())
					{
						//APOLLO 2018x.5 A10-508 - Read Webservice - Get Performance Characteristic Value Starts
						strFinalOutput = readConfigurationsFromXML(context, mlVPMRefMapList, strParameter, strMode);
						//APOLLO 2018x.5 A10-508 - Read Webservice - Get Performance Characteristic Value Ends
					}
					else
					{
						sbReturnMessages.append(pgApolloConstants.STR_EXTRACT_OBJECTNOTFOUND);
					}
					
					loggerWS.debug("strFinalOutput : {}" , strFinalOutput);
					
					if(UIUtil.isNotNullAndNotEmpty(strFinalOutput))
					{
						sbReturnMessages.append(strFinalOutput);
					}
				}
			}
			else
			{
				sbReturnMessages.append(strNoConfigurations);
			}
			//APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5 Ends
		}			
		loggerWS.debug("sbReturnMessages = {}" , sbReturnMessages );
		loggerWS.debug("Checkout and Read XML Ended-----------------");
		loggerWS.debug("******************************************************************************");
		
		return sbReturnMessages;
	}
	
	/**
	 * Method to fetch Configurations from VPMReference POST Request
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@POST
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	@Path("/readXMLPost")
	public Response getXMLConfigurationPostCall(@Context HttpServletRequest req) throws Exception {

		matrix.db.Context context = null;		

		String strNoConfigurations = pgApolloConstants.STR_AUTOMATION_ERROR_NO_CONFIGURATION;

		StringBuilder sbReturnMessages = new StringBuilder();
		boolean bContextPushed =false;
		StringBuilder sbCatiaDetails = new StringBuilder();

		try (BufferedReader in = new BufferedReader(new InputStreamReader(req.getInputStream())))
		{

			loggerWS.debug("******************************************************************************");
			loggerWS.debug("Path : /xmlReadWriteService/readXMLPost");
			loggerWS.debug("Method: ReadWriteXMLForPLMDTDocument : getXMLConfigurationsWithNewFormat");
			String strStartDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
			loggerWS.debug("Checkout and Read XML Started : {}"  , strStartDate  );

			// Get the user context
			if (Framework.isLoggedIn(req)) {
				context = Framework.getContext(req.getSession(false));				
			}
			
			if(null!=context)
			{
				String strContextUser = context.getUser();
				loggerWS.debug("context  : {}" , strContextUser);			
			}
			//Pushing the context to User Agent
			//This is webservice used by Automation in CATIA. Context user will not always have access on all Base objects in the database. 
			//So push context is necessary to find out all the Base objects in the database.
			ContextUtil.pushContext(context, pgApolloConstants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
			bContextPushed = true;
			
			String line = DomainConstants.EMPTY_STRING;

			while((line = in.readLine())!=null) {
				sbCatiaDetails.append(line);
			}
			String queryString = sbCatiaDetails.toString();
			
			sbReturnMessages = readXML(context, strNoConfigurations, queryString);
		} 
		catch (Exception e)
		{
			loggerApolloTrace.error(e.getMessage(), e);
			sbReturnMessages.append(pgApolloConstants.STR_ERROR+ pgApolloConstants.CONSTANT_STRING_COLON + e.getLocalizedMessage());
		}
		finally 
		{			
			if(bContextPushed)
			{
				ContextUtil.popContext(context);
			}			
		}
		return Response.status(200).entity(sbReturnMessages.toString()).build();
	}

	
	/**
	 * Method to read configurations from XML 2.1 Solution
	 * @param context
	 * @param XMLReadLog
	 * @param VPMRefMapList
	 * @param strParameter
	 * @return
	 * @throws Exception
	 */
	 //APOLLO 2018x.5 A10-508 - Read Webservice - Get Performance Characteristic Value Starts
	public String readConfigurationsFromXML(matrix.db.Context context, MapList mlVPMRefMap, String strParameter, String mode) throws Exception {
	//APOLLO 2018x.5 A10-508 - Read Webservice - Get Performance Characteristic Value Ends
		String strFinalOutput = DomainConstants.EMPTY_STRING;
		StringList slParam = new StringList();
		String strName = DomainConstants.EMPTY_STRING;
		String strValue = DomainConstants.EMPTY_STRING;

		String strVPMRefObjectId = DomainConstants.EMPTY_STRING;
		int iVPMRefMapListSize = 0;
		Map map = new HashMap();
		Map mapXMLOutput = new HashMap();
		Map attributeMap = new HashMap();
		Map outputMap = new HashMap();
		StringList slFinal = new StringList();
		Set<String> key = outputMap.keySet();
		Iterator<String> itr = key.iterator();
		//APOLLO 2018x.5 A10-508 - Read Webservice - Get Performance Characteristic Value Starts
		StringBuilder sbParameter = new StringBuilder();
		//APOLLO 2018x.5 A10-508 - Read Webservice - Get Performance Characteristic Value Ends
		
		try {
			if(mlVPMRefMap!=null)
			{
				iVPMRefMapListSize = mlVPMRefMap.size();
				if(iVPMRefMapListSize>0)
				{
					map = (Map)mlVPMRefMap.get(0);
					strVPMRefObjectId = (String)map.get(DomainConstants.SELECT_ID);
					//APOLLO 2018x.5 A10-569 - Publish Design/Read Webservice changes for handling isActive true/false parameter - Starts
					mapXMLOutput = getConfigXMLParameters (context, strVPMRefObjectId, false, false);
					//APOLLO 2018x.5 A10-569 - Publish Design/Read Webservice changes for handling isActive true/false parameter - Ends
					if(!mapXMLOutput.isEmpty())
					{
						//APOLLO 2018x.5 A10-508 - Read Webservice - Get Performance Characteristic Value Starts
						if(pgApolloConstants.STR_READ_MODE_GETDELTAPARAMSFORVARIANTS.equalsIgnoreCase(mode))						
						{
						//APOLLO 2018x.5 A10-508 - Read Webservice - Get Performance Characteristic Value Ends
							outputMap = (Map)mapXMLOutput.get(pgApolloConstants.STR_VARIANTS);	
						//APOLLO 2018x.5 A10-508 - Read Webservice - Get Performance Characteristic Value Starts	
						}
						//APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5 Starts
						else if(pgApolloConstants.STR_READ_MODE_GETDELTAPERFCHARFORVARIANTS.equalsIgnoreCase(mode))						
						{
							outputMap = (Map)mapXMLOutput.get(pgApolloConstants.STR_VARIANTS_PERFORMANCE_CHAR);	
						}
						//APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5 Ends
						else if(pgApolloConstants.STR_READ_MODE_GETPERFORMANCECHARVALUES.equalsIgnoreCase(mode) || pgApolloConstants.STR_READ_MODE_GETALLPERFCHARACDETAILS.equalsIgnoreCase(mode))						
						{
							outputMap = (Map)mapXMLOutput.get(pgApolloConstants.STR_PERFORMANCE_CHAR);	
						//APOLLO 2018x.5 A10-508 - Read Webservice - Get Performance Characteristic Value Ends
						}
						else
						{
							attributeMap = getVPMRefParameterMap(context, strVPMRefObjectId);
							outputMap = (Map)mapXMLOutput.get(pgApolloConstants.STR_PRODUCT_DEFINITION);	
							outputMap.putAll(attributeMap);
						}
												
							if(UIUtil.isNotNullAndNotEmpty(strParameter))								
							{
								slParam = StringUtil.split(strParameter, pgApolloConstants.CONSTANT_STRING_PIPE);
								boolean isFoundParam = false;
								for (String strParam : slParam)
								{
									isFoundParam = false;
									strValue = DomainConstants.EMPTY_STRING;
									if(null != outputMap && !outputMap.isEmpty())
									{
										key = outputMap.keySet();
										itr = key.iterator();
										while (itr.hasNext()) 
										{
											strName = itr.next();
											strValue = (String) outputMap.get(strName);										
											if(strName.equalsIgnoreCase(strParam))
											{	
												 isFoundParam = true;
												break;
											}
										}					
									}												
									if(!isFoundParam || UIUtil.isNullOrEmpty(strValue))
									{
										strValue = pgApolloConstants.STR_AUTOMATION_BLANK;
									}									
									slFinal.addElement(strValue);									
								}
							}
							else
							{
								if(!outputMap.isEmpty())
								{
									key = outputMap.keySet();
									itr = key.iterator();
									while (itr.hasNext()) 
									{
										strName = itr.next();
										strValue = (String)outputMap.get(strName);									
										sbParameter = new StringBuilder();
										sbParameter.append(strName).append(pgApolloConstants.CONSTANT_STRING_EQUAL_SIGN).append(strValue);
										strName = sbParameter.toString();
										slFinal.addElement(strName);
									}
								}
							}
						
						if(slFinal.isEmpty())
						{
							strFinalOutput = pgApolloConstants.STR_AUTOMATION_BLANK;
						}
						else
						{
							strFinalOutput = StringUtil.join(slFinal, pgApolloConstants.CONSTANT_STRING_PIPE);
						}						
					}
					else
					{
						strFinalOutput = pgApolloConstants.STR_DESIGN_PARAMETER_NOT_PUBLISHED;
					}
				}
				else
				{
					strFinalOutput = pgApolloConstants.STR_EXTRACT_OBJECTNOTFOUND;
				}
			}
			else
			{
				strFinalOutput = pgApolloConstants.STR_EXTRACT_OBJECTNOTFOUND;
			}
		} catch (Exception e) {
			loggerApolloTrace.error(e.getMessage(), e);
			throw e;
		}				
		return strFinalOutput;
	}
	
	/**
	 * Method to fetch VPMReference Basic Parameters 2.1 Solution
	 * @param context
	 * @param strVPMRefObjectId
	 * @return
	 * @throws Exception
	 */
	private Map getVPMRefParameterMap(matrix.db.Context context, String strVPMRefObjectId) throws MatrixException {
		
		Map parameterMap = new HashMap();
		String strPartName = DomainConstants.EMPTY_STRING;
		String strPartTitle = DomainConstants.EMPTY_STRING;
		String strMajorRevision = DomainConstants.EMPTY_STRING;
		String strMinorRevision = DomainConstants.EMPTY_STRING;
		String strSize = DomainConstants.EMPTY_STRING;
		String strRegion = DomainConstants.EMPTY_STRING;
		String strSubRegion = DomainConstants.EMPTY_STRING;
		String strProductTechnologyChassis = DomainConstants.EMPTY_STRING;
		String strFranchisePlatform = DomainConstants.EMPTY_STRING;
		String strBusinessArea = DomainConstants.EMPTY_STRING;
		String strProductCategoryPlatform = DomainConstants.EMPTY_STRING;
		String strPrimaryOrganization = DomainConstants.EMPTY_STRING;
		String strProductTechnologyPlatform = DomainConstants.EMPTY_STRING;
		String strAPPName = DomainConstants.EMPTY_STRING;
		String strAPPRevision = DomainConstants.EMPTY_STRING;
		//APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5 Starts
		String strDefintion = DomainConstants.EMPTY_STRING;
		//APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5 Ends
		Object object = null;
		String sReleasePhase;
		String sCurrent;

		try {
			DomainObject domObject = DomainObject.newInstance( context, strVPMRefObjectId);
			StringList VPMRefObjectSelects = new StringList();
			VPMRefObjectSelects.add(DomainConstants.SELECT_NAME);
			VPMRefObjectSelects.add(DomainConstants.SELECT_ID);
			VPMRefObjectSelects.add(pgApolloConstants.SELECT_MAJOR_REVISION);
			VPMRefObjectSelects.add(pgApolloConstants.SELECT_MINOR_REVISION);
			VPMRefObjectSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_V_NAME);
			VPMRefObjectSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PGSIZE);
			VPMRefObjectSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PGREGION);
			VPMRefObjectSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PGSUBREGION);
			VPMRefObjectSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PGPRODUCTTECHNOLOGYCHASSIS);
			VPMRefObjectSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PGFRANCHISEPLATFORM);
			VPMRefObjectSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PGBUSINESSAREA);
			VPMRefObjectSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PGPRODUCTCATEGORYPLATFORM);
			VPMRefObjectSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PGPRIMARYORGANIZATION);
			VPMRefObjectSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PGPRODUCTTECHNOLOGYPLATFORM);
			VPMRefObjectSelects.add(DomainConstants.SELECT_CURRENT);
			VPMRefObjectSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PGMANUFACTURING_MATURITYSTATUS);

			//APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5 Starts
			VPMRefObjectSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PGDEFINITION);
			//APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5 Ends
			VPMRefObjectSelects.add("to["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION +"].from."+DomainConstants.SELECT_NAME);
			VPMRefObjectSelects.add("to["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION +"].from."+DomainConstants.SELECT_REVISION);
			
			Map VPMRefAttributeMap = new HashMap();
			VPMRefAttributeMap = (Map)domObject.getInfo(context, VPMRefObjectSelects);				
			strPartName = (String)VPMRefAttributeMap.get(DomainConstants.SELECT_NAME);
			strMajorRevision = (String)VPMRefAttributeMap.get(pgApolloConstants.SELECT_MAJOR_REVISION);
			strMinorRevision = (String)VPMRefAttributeMap.get(pgApolloConstants.SELECT_MINOR_REVISION);
			sCurrent = (String)VPMRefAttributeMap.get(DomainConstants.SELECT_CURRENT);
			sCurrent = EnoviaResourceBundle.getStateI18NString(context,pgApolloConstants.POLICY_VPLM_SMB_DEFINITION ,sCurrent, Locale.ENGLISH.getLanguage());				
			sReleasePhase = (String)VPMRefAttributeMap.get(pgApolloConstants.SELECT_ATTRIBUTE_PGMANUFACTURING_MATURITYSTATUS);

			if(VPMRefAttributeMap.containsKey("to["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION +"].from."+DomainConstants.SELECT_NAME))
			{
				object = (Object)VPMRefAttributeMap.get("to["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION +"].from."+DomainConstants.SELECT_NAME);
				if(null != object)
				{
					if (object instanceof String)
					{ 
						strAPPName = (String)object;
					}								    		
				}
			}
			if(VPMRefAttributeMap.containsKey("to["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION +"].from."+DomainConstants.SELECT_REVISION))
			{
				object = (Object)VPMRefAttributeMap.get("to["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION +"].from."+DomainConstants.SELECT_REVISION);
				if(null != object)
				{
					if (object instanceof String)
					{ 
						strAPPRevision = (String)object;
					}								    		
				}
			}
			if(VPMRefAttributeMap.containsKey(pgApolloConstants.SELECT_ATTRIBUTE_V_NAME))
			{
				strPartTitle = (String)VPMRefAttributeMap.get(pgApolloConstants.SELECT_ATTRIBUTE_V_NAME);
			}
			if(VPMRefAttributeMap.containsKey(pgApolloConstants.SELECT_ATTRIBUTE_PGSIZE))
			{
				strSize = (String)VPMRefAttributeMap.get(pgApolloConstants.SELECT_ATTRIBUTE_PGSIZE);
			}
			if(VPMRefAttributeMap.containsKey(pgApolloConstants.SELECT_ATTRIBUTE_PGREGION))
			{
				strRegion = (String)VPMRefAttributeMap.get(pgApolloConstants.SELECT_ATTRIBUTE_PGREGION);
			}
			if(VPMRefAttributeMap.containsKey(pgApolloConstants.SELECT_ATTRIBUTE_PGSUBREGION))
			{
				strSubRegion = (String)VPMRefAttributeMap.get(pgApolloConstants.SELECT_ATTRIBUTE_PGSUBREGION);
			}
			if(VPMRefAttributeMap.containsKey(pgApolloConstants.SELECT_ATTRIBUTE_PGPRODUCTTECHNOLOGYCHASSIS))
			{			
				strProductTechnologyChassis = (String)VPMRefAttributeMap.get(pgApolloConstants.SELECT_ATTRIBUTE_PGPRODUCTTECHNOLOGYCHASSIS);
			}
			if(VPMRefAttributeMap.containsKey(pgApolloConstants.SELECT_ATTRIBUTE_PGFRANCHISEPLATFORM))
			{						
				strFranchisePlatform = (String)VPMRefAttributeMap.get(pgApolloConstants.SELECT_ATTRIBUTE_PGFRANCHISEPLATFORM);
			}
			if(VPMRefAttributeMap.containsKey(pgApolloConstants.SELECT_ATTRIBUTE_PGBUSINESSAREA))
			{
				strBusinessArea = (String)VPMRefAttributeMap.get(pgApolloConstants.SELECT_ATTRIBUTE_PGBUSINESSAREA);
			}
			if(VPMRefAttributeMap.containsKey(pgApolloConstants.SELECT_ATTRIBUTE_PGPRODUCTCATEGORYPLATFORM))
			{
				strProductCategoryPlatform = (String)VPMRefAttributeMap.get(pgApolloConstants.SELECT_ATTRIBUTE_PGPRODUCTCATEGORYPLATFORM);
			}
			if(VPMRefAttributeMap.containsKey(pgApolloConstants.SELECT_ATTRIBUTE_PGPRIMARYORGANIZATION))
			{
				strPrimaryOrganization = (String)VPMRefAttributeMap.get(pgApolloConstants.SELECT_ATTRIBUTE_PGPRIMARYORGANIZATION);
			}
			if(VPMRefAttributeMap.containsKey(pgApolloConstants.SELECT_ATTRIBUTE_PGPRODUCTTECHNOLOGYPLATFORM))
			{
				strProductTechnologyPlatform = (String)VPMRefAttributeMap.get(pgApolloConstants.SELECT_ATTRIBUTE_PGPRODUCTTECHNOLOGYPLATFORM);
			}			
			//APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5 Starts
			if(VPMRefAttributeMap.containsKey(pgApolloConstants.SELECT_ATTRIBUTE_PGDEFINITION))
			{
				strDefintion = (String)VPMRefAttributeMap.get(pgApolloConstants.SELECT_ATTRIBUTE_PGDEFINITION);
			}
			//APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5 Ends
			parameterMap.put(pgApolloConstants.STR_AUTOMATION_APPNAME, strAPPName);
			parameterMap.put(pgApolloConstants.STR_AUTOMATION_APPREVISION, strAPPRevision);
			parameterMap.put(pgApolloConstants.STR_AUTOMATION_PARTMATURITYSTATE, sCurrent);
			parameterMap.put(pgApolloConstants.STR_AUTOMATION_PARTRELEASEPHASE, sReleasePhase);
			parameterMap.put(pgApolloConstants.STR_AUTOMATION_PARTNAME, strPartName);
			parameterMap.put(pgApolloConstants.STR_AUTOMATION_PARTTITLE, strPartTitle);
			parameterMap.put(pgApolloConstants.STR_AUTOMATION_MAJORREVISION, strMajorRevision);
			parameterMap.put(pgApolloConstants.STR_AUTOMATION_MINORREVISION, strMinorRevision);
			parameterMap.put(pgApolloConstants.STR_AUTOMATION_SIZE, strSize);
			parameterMap.put(pgApolloConstants.STR_AUTOMATION_REGION, strRegion);
			parameterMap.put(pgApolloConstants.STR_AUTOMATION_SUBREGION, strSubRegion);
			parameterMap.put(pgApolloConstants.STR_AUTOMATION_PRODUCTTECHNOLOGYCHASSIS, strProductTechnologyChassis);
			parameterMap.put(pgApolloConstants.STR_AUTOMATION_FRANCHISEPLATFORM, strFranchisePlatform);
			parameterMap.put(pgApolloConstants.STR_AUTOMATION_BUSINESSAREA, strBusinessArea);
			parameterMap.put(pgApolloConstants.STR_AUTOMATION_PRODUCTCATEGORYPLATFORM, strProductCategoryPlatform);
			parameterMap.put(pgApolloConstants.STR_AUTOMATION_PRIMARYORGANIZATION, strPrimaryOrganization);
			parameterMap.put(pgApolloConstants.STR_AUTOMATION_PRODUCTTECHNOLOGYPLATFORM, strProductTechnologyPlatform);
			//APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5 Starts
			parameterMap.put(pgApolloConstants.STR_AUTOMATION_DEFINITION, strDefintion);
			//APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5 Ends

		} catch (Exception e) {
			loggerApolloTrace.error(e.getMessage(), e);
			throw e;
		}
		return parameterMap;
	}

	/**
	 * Method to read Configurations APP - APOLLO 2018x.2.1 Changes
	 * @param context
	 * @param VPMRefMapList
	 * @param XMLReadLog
	 * @return
	 * @throws Exception
	 */
	//APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5 Starts
	public String fetchConfigurations(matrix.db.Context context, MapList mlVPMRefMap, String strMode)  {
	//APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5 Ends
		String strFinalOutput = DomainConstants.EMPTY_STRING;
		StringList slFinalOutput = new StringList();
		try {
			String objectName;
			String objectRegion;
			String objectSubRegion;
			String objectSize;
			//APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5 Starts
			String objectRevision;
			String objectDefinition;
			//APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5 Ends
			String output;
			StringBuilder sbOutput;

			Map map;	
			
			if(null!=mlVPMRefMap && !mlVPMRefMap.isEmpty())
			{
				for(int i=0;i<mlVPMRefMap.size();i++)
				{					
					map = (Map)mlVPMRefMap.get(i);
					objectName = (String)map.get(DomainConstants.SELECT_NAME);
					objectRegion = (String)map.get(pgApolloConstants.SELECT_ATTRIBUTE_PGREGION);
					objectSubRegion = (String)map.get(pgApolloConstants.SELECT_ATTRIBUTE_PGSUBREGION);
					objectSize = (String)map.get(pgApolloConstants.SELECT_ATTRIBUTE_PGSIZE);
					//APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5 Starts
					objectDefinition = (String)map.get(pgApolloConstants.SELECT_ATTRIBUTE_PGDEFINITION);
					//APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5 Ends
					sbOutput = new StringBuilder();
					//APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5 Starts
					if(pgApolloConstants.STR_READ_MODE_GETALLBASES.equalsIgnoreCase(strMode))
					{
						objectRevision = (String)map.get(DomainConstants.SELECT_REVISION);
						sbOutput.append(objectName).append(pgApolloConstants.CONSTANT_STRING_PIPE).append(objectRevision).append(pgApolloConstants.CONSTANT_STRING_PIPE).append(objectDefinition);
					}
					else
					{
						sbOutput.append(objectRegion).append(pgApolloConstants.CONSTANT_STRING_UNDERSCORE).append(objectSubRegion).append(pgApolloConstants.CONSTANT_STRING_UNDERSCORE).append(objectSize).append(pgApolloConstants.CONSTANT_STRING_UNDERSCORE).append(objectDefinition);
					}
					//APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5 Ends
					output = sbOutput.toString();
					if(!slFinalOutput.contains(output))
					{
						slFinalOutput.add(output);
					}					
				}				
			}
			if(!slFinalOutput.isEmpty())
			{
				//APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5 Starts
				if(pgApolloConstants.STR_READ_MODE_GETALLBASES.equalsIgnoreCase(strMode))
				{
					strFinalOutput = StringUtil.join(slFinalOutput, pgApolloConstants.CONSTANT_STRING_COMMA);
				}
				else
				{
					//APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5 Ends
					strFinalOutput = StringUtil.join(slFinalOutput, pgApolloConstants.CONSTANT_STRING_PIPE);
					//APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5 Starts
				}
				//APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5 Ends
			}
			loggerWS.debug("Reading Configurations : {}" , strFinalOutput);	

		} catch (Exception e) {
			loggerApolloTrace.error(e.getMessage(), e);
			throw e;
		}
		
		return strFinalOutput;
	}

	/**
	 * Method to delete checkedout file
	 * @param strFileName
	 * @throws Exception
	 */
	private static void deleteFile(String strFileName) throws Exception{
		try {
			File file = new File(strFileName);	
			boolean bFileDeleted = file.delete();
			if(!bFileDeleted)
			{
				loggerApolloTrace.debug("File Deletion failed");
			}
		} catch (Exception e) {
			loggerApolloTrace.error(e.getMessage(), e);
		}
	}

	
	
	
	/**
	 * Method for write update XML 2.1 Solution
	 * @param context
	 * @param paramList
	 * @param XMLUpdateLog
	 * @return
	 * @throws Exception
	 */
	 //APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Starts
	//public static String updateConfigFile(matrix.db.Context context, StringList paramList, BufferedWriter XMLUpdateLog) throws Exception
	public static String updateConfigFile(matrix.db.Context context, Map paramMap) throws Exception
	//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Ends
	{
		StringBuffer sbReturnMessages = new StringBuffer();
		String strTemp = DomainConstants.EMPTY_STRING;
		StringList slTemp = new StringList();
		String strOutput = DomainConstants.EMPTY_STRING;
		String strType = pgApolloConstants.TYPE_VPMREFERENCE;
		StringList VPMRefObjectSelects = new StringList();
		VPMRefObjectSelects.add(DomainConstants.SELECT_NAME);
		VPMRefObjectSelects.add(DomainConstants.SELECT_ID);
		VPMRefObjectSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PGLPDMODELTYPE);
		//APOLLO 2018x.5 ALM Requirement 32493 A10-558 - Publish Design Information - Logic For Variant to fetch its base VPMRef Starts
		VPMRefObjectSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PNGLPDORIGINATEDFROMROOT);
		//APOLLO 2018x.5 ALM Requirement 32493 A10-558 - Publish Design Information - Logic For Variant to fetch its base VPMRef Ends		
		
		String strAPPObjectId = DomainConstants.EMPTY_STRING;
		String strBaseAPPObjectId = DomainConstants.EMPTY_STRING;
		String strBaseVPMRefObjectId = DomainConstants.EMPTY_STRING;
		String strVPMRefObjectId = DomainConstants.EMPTY_STRING;
		String strVPMRefCurrent = DomainConstants.EMPTY_STRING;

		String strVPMRefLPDModelType = DomainConstants.EMPTY_STRING;
		//APOLLO 2018x.5 ALM Requirement 32493 A10-558 - Publish Design Information - Logic For Variant to fetch its base VPMRef Starts
		String strVPMRefLPDOriginatedFromRoot = DomainConstants.EMPTY_STRING;
		String strVPMRefLPDOriginatedFromRootObjectDetails = DomainConstants.EMPTY_STRING;
		StringList slVPMRefLPDOriginatedFromRoot = new StringList();
		StringList slRootVPMRef = new StringList();
		//APOLLO 2018x.5 ALM Requirement 32493 A10-558 - Publish Design Information - Logic For Variant to fetch its base VPMRef Ends		
		//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Starts
		StringList paramList = new StringList();		
		StringList performanceCharList = new StringList();
		//XMLUpdateLog.write("\n\n\n  paramMap : " + paramMap);
		if(paramMap.containsKey(pgApolloConstants.STR_PRODUCT_DEFINITION))
		{
			paramList = (StringList)paramMap.get(pgApolloConstants.STR_PRODUCT_DEFINITION);
		}		
		if(paramMap.containsKey(pgApolloConstants.STR_PERFORMANCE_CHAR))
		{
			performanceCharList = (StringList)paramMap.get(pgApolloConstants.STR_PERFORMANCE_CHAR);
		}
		//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Ends		
		try
		{
			//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Starts
			if(paramMap.containsKey(pgApolloConstants.STR_PRODUCT_DETAILS))
			{
				strTemp = (String)paramMap.get(pgApolloConstants.STR_PRODUCT_DETAILS);
			}
			//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Ends
			slTemp = FrameworkUtil.split(strTemp, pgApolloConstants.CONSTANT_STRING_COLON);
			String strVPMRefName = slTemp.get(0).toString();
			String strVPMRefRevision = slTemp.get(1).toString();
			//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Starts
			//paramList.remove(0);
			//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Ends
			strVPMRefObjectId = pgApolloCommonUtil.getObjectId(context, strType, strVPMRefName, strVPMRefRevision);
			if(UIUtil.isNotNullAndNotEmpty(strVPMRefObjectId))
			{
				DomainObject domVPMReference = DomainObject.newInstance(context,strVPMRefObjectId);				
				//Get attribute values on VPMReference
				Map VPMRefAttributeMap = new HashMap();
				VPMRefAttributeMap = (Map)domVPMReference.getInfo(context, VPMRefObjectSelects);				
				strVPMRefLPDModelType = (String)VPMRefAttributeMap.get(pgApolloConstants.SELECT_ATTRIBUTE_PGLPDMODELTYPE);
				loggerSync.debug( " VPMRef LPD Model Type : {}",strVPMRefLPDModelType);					
				
				if(UIUtil.isNullOrEmpty(strVPMRefLPDModelType) || !pgApolloConstants.RANGE_VALUE_VARIANT.equalsIgnoreCase(strVPMRefLPDModelType))
				{
					strAPPObjectId = fetchAPPObject(context, domVPMReference);
					loggerSync.debug( " File To be Checked In For Base strAPPObjectId : {}", strAPPObjectId);
					//APOLLO 2018x.5 A10-563 Starts
					if(UIUtil.isNotNullAndNotEmpty(strAPPObjectId))
					{
					//APOLLO 2018x.5 A10-563 Ends	
						//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Starts
						strOutput = createAndUpdateXML(context, paramList, strAPPObjectId, null, strVPMRefLPDModelType, performanceCharList);
						//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Ends
						if(pgApolloConstants.STR_SUCCESS.equalsIgnoreCase(strOutput))
						{
							sbReturnMessages.append(pgApolloConstants.STR_SUCCESS);
						}
						//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Starts
						else if(strOutput.contains(pgApolloConstants.STR_ERROR))
						{
							sbReturnMessages.append(strOutput);
						}
						//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Ends
						else
						{
							//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Starts
							sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_ERROR);
							//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Ends
						}
					//APOLLO 2018x.5 A10-563 Starts
					}
					else
					{
						sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_NO_VPMREF_APP_FOUND);
					}
					//APOLLO 2018x.5 A10-563 Ends

				}
				else if(pgApolloConstants.RANGE_VALUE_VARIANT.equalsIgnoreCase(strVPMRefLPDModelType))
				{
					strAPPObjectId = fetchAPPObject(context, domVPMReference);					
					loggerSync.debug( " File To be Checked In For Variant strAPPObjectId-----------------{}" , strAPPObjectId);
					//APOLLO 2018x.5 A10-563 Starts
					if(UIUtil.isNotNullAndNotEmpty(strAPPObjectId))
					{
					//APOLLO 2018x.5 A10-563 Ends
						//APOLLO 2018x.5 ALM Requirement 32493 A10-558 - Publish Design Information - Logic For Variant to fetch its base VPMRef Starts
						if(VPMRefAttributeMap.containsKey(pgApolloConstants.SELECT_ATTRIBUTE_PNGLPDORIGINATEDFROMROOT))
						{
							strVPMRefLPDOriginatedFromRoot = (String) VPMRefAttributeMap.get(pgApolloConstants.SELECT_ATTRIBUTE_PNGLPDORIGINATEDFROMROOT);
						}
						loggerSync.debug( " Originated From Root Details ----------------{}" , strVPMRefLPDOriginatedFromRoot);
						if(UIUtil.isNotNullAndNotEmpty(strVPMRefLPDOriginatedFromRoot))
						{
							slVPMRefLPDOriginatedFromRoot = FrameworkUtil.split(strVPMRefLPDOriginatedFromRoot, pgApolloConstants.CONSTANT_STRING_COMMA);
							if(null != slVPMRefLPDOriginatedFromRoot && !slVPMRefLPDOriginatedFromRoot.isEmpty() )
							{
								if(slVPMRefLPDOriginatedFromRoot.size() == 1)
								{
									strVPMRefLPDOriginatedFromRootObjectDetails = (String)slVPMRefLPDOriginatedFromRoot.get(0);
									slRootVPMRef = FrameworkUtil.split(strVPMRefLPDOriginatedFromRootObjectDetails, pgApolloConstants.CONSTANT_STRING_PIPE);
									if(null!= slRootVPMRef && !slRootVPMRef.isEmpty() && slRootVPMRef.size()>1 )
									{
										strVPMRefName = slRootVPMRef.get(0).toString().trim();
										strVPMRefRevision = slRootVPMRef.get(1).toString().trim();
										strBaseVPMRefObjectId = pgApolloCommonUtil.getObjectId(context, strType, strVPMRefName, strVPMRefRevision);
										//APOLLO 2018x.5 ALM Requirement 32493 A10-558 - Publish Design Information - Logic For Variant to fetch its base VPMRef Ends
										if(UIUtil.isNotNullAndNotEmpty(strBaseVPMRefObjectId))
										{
											domVPMReference = DomainObject.newInstance(context,strBaseVPMRefObjectId);										
											strVPMRefCurrent = domVPMReference.getInfo(context,DomainConstants.SELECT_CURRENT);
											strBaseAPPObjectId = fetchAPPObject(context, domVPMReference);
											loggerSync.debug( " Base APP for comparison strBaseAPPObjectId-----------------{}" , strBaseAPPObjectId);
											//APOLLO 2018x.5 A10-563 Starts
											if(UIUtil.isNotNullAndNotEmpty(strBaseAPPObjectId) && (pgApolloConstants.STATE_SHARED.equalsIgnoreCase(strVPMRefCurrent) || pgApolloConstants.STATE_OBSOLETE_CATIA.equalsIgnoreCase(strVPMRefCurrent)))
											{
											//APOLLO 2018x.5 A10-563 Ends
												//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Starts
												strOutput = createAndUpdateXML(context, paramList, strAPPObjectId, strBaseAPPObjectId, strVPMRefLPDModelType, performanceCharList);
												//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Ends
												if(pgApolloConstants.STR_SUCCESS.equalsIgnoreCase(strOutput))
												{
													sbReturnMessages.append(pgApolloConstants.STR_SUCCESS);
												}
												//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Starts
												else if(strOutput.contains(pgApolloConstants.STR_ERROR))
												{
													sbReturnMessages.append(strOutput);
												}
												//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Ends
												else
												{
													//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Starts
													sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_ERROR);
													//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Ends
												}
											//APOLLO 2018x.5 A10-563 Starts
											}
											else
											{
												sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_RELEASED_VPMREF_OBJECTNOTFOUND);
											}
											//APOLLO 2018x.5 A10-563 Ends
										}							 
										else
										{
											//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Starts
											sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_RELEASED_VPMREF_OBJECTNOTFOUND);
											//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Ends
										}
						//APOLLO 2018x.5 ALM Requirement 32493 A10-558 - Publish Design Information - Logic For Variant to fetch its base VPMRef Starts
									}								
								}
								else
								{
									sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_MULTIPLE_RELEASED_BASES_FOUND);
								}
							}
							else
							{
								sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_RELEASED_VPMREF_OBJECTNOTFOUND);
							}
						}
						else
						{
							sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_RELEASED_VPMREF_OBJECTNOTFOUND);
						}
						//APOLLO 2018x.5 ALM Requirement 32493 A10-558 - Publish Design Information - Logic For Variant to fetch its base VPMRef Ends
					//APOLLO 2018x.5 A10-563 Starts
					}
					else
					{
						sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_NO_VPMREF_APP_FOUND);
					}
					//APOLLO 2018x.5 A10-563 Ends
				}
				else
				{
					//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Starts
					sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_INVALID_LPDMODELTYPE);
					//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Ends
				}

			}
			else
			{
				sbReturnMessages.append(pgApolloConstants.STR_EXTRACT_OBJECTNOTFOUND);
			}			
		}
		catch(Exception ex)
		{
			loggerApolloTrace.error(ex.getMessage() ,ex);
			loggerSync.error( "ERROR while writing config file : {}" , ex.getLocalizedMessage());
			//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Starts
			sbReturnMessages = new StringBuffer();
			sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(ex.getLocalizedMessage());			
			//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Starts
		}			
		return sbReturnMessages.toString();
	}
	
	/**
	 * Method to fetch VPMReference MapList for given whereclause 2.1 Solution
	 * @param context
	 * @param XMLUpdateLog
	 * @param whereClause
	 * @param mapCriteria 
	 * @return
	 * @throws Exception
	 */
	//APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5 Starts
	public static MapList fetchVPMRefList(matrix.db.Context context, String strVPMRefName, String strVPMRefRevision, String whereClause, String strMode, boolean latestFlag, Map mapCriteria) throws MatrixException {
	//APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5 Ends
		MapList mlVPMRefList = new MapList();
		MapList mlFinalVPMRefList = new MapList();

		try 
		{
			//APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5 Starts
			String strObjectName;
			String strObjectRevision;
			//APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5 Ends			
			String strType = pgApolloConstants.TYPE_VPMREFERENCE;
			StringList slVPMRefObjectSelects = new StringList();
			slVPMRefObjectSelects.add(DomainConstants.SELECT_NAME);
			slVPMRefObjectSelects.add(DomainConstants.SELECT_CURRENT);
			slVPMRefObjectSelects.add(DomainConstants.SELECT_ID);
			slVPMRefObjectSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PGLPDMODELTYPE);
			slVPMRefObjectSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PGPRODUCTTECHNOLOGYCHASSIS);
			slVPMRefObjectSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PGSIZE);
			slVPMRefObjectSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PGREGION);
			slVPMRefObjectSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PGSUBREGION);
			//APOLLO 2018x.5 A10-531 - Added Granularity for Read XML Webservices Starts
			slVPMRefObjectSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PGBUSINESSAREA);
			slVPMRefObjectSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PGPRODUCTCATEGORYPLATFORM);
			slVPMRefObjectSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PGPRODUCTTECHNOLOGYPLATFORM);
			//APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5 Starts
			slVPMRefObjectSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PGDEFINITION);
			//APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5 Ends
			slVPMRefObjectSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PGMANUFACTURING_MATURITYSTATUS);
			//APOLLO 2018x.5 A10-531 - Added Granularity for Read XML Webservices Ends
			if(pgApolloConstants.STR_READ_MODE_GETVARIANTPARTS.equalsIgnoreCase(strMode) || pgApolloConstants.STR_READ_MODE_GETINWORKBASEPARTS.equalsIgnoreCase(strMode))
			{
				//APOLLO 2018x.5 A10-479 - Read Webservice change for Variant Parts Starts
				slVPMRefObjectSelects.add(DomainConstants.SELECT_REVISION);
				slVPMRefObjectSelects.add("to["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION +"].from."+DomainConstants.SELECT_NAME);
				slVPMRefObjectSelects.add("to["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION +"].from."+DomainConstants.SELECT_REVISION);
				//APOLLO 2018x.5 A10-479 - Read Webservice change for Variant Parts Ends
			}
			//APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5 Starts
			else if(pgApolloConstants.STR_READ_MODE_GETALLBASES.equalsIgnoreCase(strMode))
			{
				slVPMRefObjectSelects.add(DomainConstants.SELECT_REVISION);
			}
			//APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5 Ends
			if(latestFlag)
			{
				slVPMRefObjectSelects.add("current.actual");
			}
			//APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5 Starts
			if(UIUtil.isNotNullAndNotEmpty(strVPMRefName) && UIUtil.isNotNullAndNotEmpty(strVPMRefRevision))
			{
				strObjectName = strVPMRefName;
				strObjectRevision = strVPMRefRevision;
			}
			else
			{
				strObjectName = DomainConstants.QUERY_WILDCARD;
				strObjectRevision = DomainConstants.QUERY_WILDCARD;
			}
			//APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5 Ends
	
			mlVPMRefList = DomainObject.findObjects(context,
						strType,
						strObjectName, //APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5
						strObjectRevision, //APOLLO 2018x.5 A10-585 - Read and Write Webservice changes for 2018x.5
						null,
						pgApolloConstants.VAULT_VPLM,
						whereClause,
						true,
						slVPMRefObjectSelects);
			
			if(null == mlVPMRefList)
			{
				mlVPMRefList = new MapList();
			}
			
			if(!mlVPMRefList.isEmpty() && !mapCriteria.isEmpty())
			{
				Set<String> setAttributeKeys = mapCriteria.keySet();

				String sCriteriaValue;
				String sObjectValue;
				Map mapVPMRef;
				boolean bMatch = true;

				for(Object objMap : mlVPMRefList)
				{
					mapVPMRef = (Map)objMap;
					bMatch = true;
					
					for(String sAttributeKey : setAttributeKeys)
					{
						sCriteriaValue = (String)mapCriteria.get(sAttributeKey);
						sObjectValue = (String)mapVPMRef.get(sAttributeKey);

						if(UIUtil.isNotNullAndNotEmpty(sCriteriaValue) && !sCriteriaValue.equals(sObjectValue))
						{
							bMatch = false;
							break;
						}
					}
					
					if(bMatch)
					{
						mlFinalVPMRefList.add(mapVPMRef);
					}
				}

			}
			else
			{
				mlFinalVPMRefList = mlVPMRefList;
			}

			if(latestFlag)
			{
				mlFinalVPMRefList.sort("current.actual", "descending", "date");
			}	
		}
		catch(Exception e)
		{
			loggerApolloTrace.error(e.getMessage(), e);
		}			
		loggerWS.debug("mlFinalVPMRefList : {}" , mlFinalVPMRefList);

		return mlFinalVPMRefList;
	}

	/**
	 * Method to update XML in APP 2.1 Solution
	 * @param context
	 * @param XMLUpdateLog
	 * @param paramList
	 * @param strAPPObjectId
	 * @param strBaseAPPObjectId
	 * @return
	 * @throws Exception
	 */
	 //APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Starts
	//public static String createAndUpdateXML(matrix.db.Context context, BufferedWriter XMLUpdateLog, StringList paramList, String strAPPObjectId, String strBaseAPPObjectId, String mode) throws Exception {
	public static String createAndUpdateXML(matrix.db.Context context, StringList paramList, String strAPPObjectId, String strBaseAPPObjectId, String mode, StringList performanceCharList) throws Exception {
	//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Ends	
		Map parameterMap = new HashMap();
		Map BaseAPPParameterMap = new HashMap();
		Map VariantParameterMap = new HashMap();
		//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Starts
		Map performanceCharMap = new HashMap();
		Map BaseAPPPerformanceCharMap = new HashMap();
		Map VariantPerformanceCharMap = new HashMap();
		StringBuffer sbReturnMessages = new StringBuffer();
		//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Ends
		String strTemp = DomainConstants.EMPTY_STRING;
		String strParameterName = DomainConstants.EMPTY_STRING;
		String strParameterValue = DomainConstants.EMPTY_STRING;
		//APOLLO 2018x.5 A10-569 - Publish Design/Read Webservice changes for handling isActive true/false parameter - Starts
		String strParameterIsActive = DomainConstants.EMPTY_STRING;
		StringBuffer sbParameter = new StringBuffer();
		//APOLLO 2018x.5 A10-569 - Publish Design/Read Webservice changes for handling isActive true/false parameter - Ends
		String strParameterIsHidden = DomainConstants.EMPTY_STRING;
		int intListSize = 0;

		StringList slTemp = new StringList();		
		try {
			DomainObject dPartObj = DomainObject.newInstance(context,strAPPObjectId) ;
			if(paramList!=null)
			{
				int paramListSize = paramList.size();
				if(paramListSize>0)
				{
					for (int n = 0; n < paramListSize; n++) 
					{
						intListSize = 0;
						strTemp = paramList.get(n).toString();
						slTemp = StringUtil.splitString(strTemp, pgApolloConstants.CONSTANT_STRING_THREE_COLON);
						intListSize = slTemp.size();
						strParameterName = slTemp.get(0).toString();
						strParameterValue = slTemp.get(1).toString();
						//APOLLO 2018x.5 A10-648 - Need to handle space in parameter name for Publish Design - Starts
						strParameterName = strParameterName.replaceAll(pgApolloConstants.CONSTANT_STRING_SPACE, pgApolloConstants.STR_SPACE_CHAR);
						//APOLLO 2018x.5 A10-648 - Need to handle space in parameter name for Publish Design - Ends
						parameterMap.put(strParameterName, strParameterValue);
						//APOLLO 2018x.5 A10-569 - Publish Design/Read Webservice changes for handling isActive true/false parameter - Starts
						if(intListSize>2)
						{
							strParameterIsActive = slTemp.get(2).toString();
							sbParameter = new StringBuffer();
							sbParameter.append(strParameterName).append(pgApolloConstants.CONSTANT_STRING_UNDERSCORE).append(pgApolloConstants.KEY_ISACTIVE);
							parameterMap.put(sbParameter.toString(), strParameterIsActive);
						}						
						//APOLLO 2018x.5 A10-569 - Publish Design/Read Webservice changes for handling isActive true/false parameter - Ends
						if(intListSize>3)
						{
							strParameterIsHidden = slTemp.get(3).toString();
							sbParameter = new StringBuffer();
							sbParameter.append(strParameterName).append(pgApolloConstants.CONSTANT_STRING_UNDERSCORE).append(pgApolloConstants.KEY_HIDDEN);
							parameterMap.put(sbParameter.toString(), strParameterIsHidden);
						}
					}
				}
			}	
			//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Starts
			if(performanceCharList!=null)
			{
				int performanceCharListSize = performanceCharList.size();
				if(performanceCharListSize>0)
				{
					for (int n = 0; n < performanceCharListSize; n++) 
					{
						intListSize = 0;
						strTemp = performanceCharList.get(n).toString();
						slTemp = StringUtil.splitString(strTemp, pgApolloConstants.CONSTANT_STRING_THREE_COLON);
						intListSize = slTemp.size();
						strParameterName = slTemp.get(0).toString();
						strParameterValue = slTemp.get(1).toString();
						//APOLLO 2018x.5 A10-648 - Need to handle space in parameter name for Publish Design - Starts
						strParameterName = strParameterName.replaceAll(pgApolloConstants.CONSTANT_STRING_SPACE, pgApolloConstants.STR_SPACE_CHAR);
						//APOLLO 2018x.5 A10-648 - Need to handle space in parameter name for Publish Design - Ends
						performanceCharMap.put(strParameterName, strParameterValue);
						//APOLLO 2018x.5 A10-569 - Publish Design/Read Webservice changes for handling isActive true/false parameter - Starts
						if(intListSize>2)
						{
							strParameterIsActive = slTemp.get(2).toString();;
							sbParameter = new StringBuffer();
							sbParameter.append(strParameterName).append(pgApolloConstants.CONSTANT_STRING_UNDERSCORE).append(pgApolloConstants.KEY_ISACTIVE);
							performanceCharMap.put(sbParameter.toString(), strParameterIsActive);
						}						
						//APOLLO 2018x.5 A10-569 - Publish Design/Read Webservice changes for handling isActive true/false parameter - Ends
						if(intListSize>3)
						{
							strParameterIsHidden = slTemp.get(3).toString();
							sbParameter = new StringBuffer();
							sbParameter.append(strParameterName).append(pgApolloConstants.CONSTANT_STRING_UNDERSCORE).append(pgApolloConstants.KEY_HIDDEN);
							parameterMap.put(sbParameter.toString(), strParameterIsHidden);
						}
					}
				}
			}
			//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Ends	
			if(UIUtil.isNotNullAndNotEmpty(strBaseAPPObjectId) && pgApolloConstants.RANGE_VALUE_VARIANT.equals(mode))
			{
				//APOLLO 2018x.5 A10-569 - Publish Design/Read Webservice changes for handling isActive true/false parameter - Starts
				Map BaseXMLParameterMap = getConfigXMLParameters (context, strBaseAPPObjectId, false, false);
				//APOLLO 2018x.5 A10-569 - Publish Design/Read Webservice changes for handling isActive true/false parameter - Ends
				if(BaseXMLParameterMap!=null && !BaseXMLParameterMap.isEmpty())
				{
					BaseAPPParameterMap = (Map)BaseXMLParameterMap.get(pgApolloConstants.STR_PRODUCT_DEFINITION);	
					BaseAPPParameterMap = getEncodedMap(BaseAPPParameterMap);
					VariantParameterMap = getDifferenceInMap(BaseAPPParameterMap, parameterMap);
					if(null!= VariantParameterMap && !VariantParameterMap.isEmpty())
					{
						VariantParameterMap = excludeParametersInMap(context,VariantParameterMap);
					}
					//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Starts
					BaseAPPPerformanceCharMap = (Map)BaseXMLParameterMap.get(pgApolloConstants.STR_PERFORMANCE_CHAR);	
					BaseAPPPerformanceCharMap = getEncodedMap(BaseAPPPerformanceCharMap);
					VariantPerformanceCharMap = getDifferenceInMap(BaseAPPPerformanceCharMap, performanceCharMap);
					//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Ends
				}

			}
			//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Starts		
			createAndUploadXML(context, strAPPObjectId, parameterMap, VariantParameterMap, performanceCharMap, VariantPerformanceCharMap, mode);
			//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Ends
		} catch (Exception e) {
			loggerApolloTrace.error(e.getMessage() ,e);
			//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Starts
			//return "Error:"+pgApolloConstants.STR_ERROR;
			sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage());
			return sbReturnMessages.toString();
			//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Starts
		}		
		return pgApolloConstants.STR_SUCCESS;
	}
	
	/**
	 * Method to exclude parameter 
	 * @param context
	 * @param parameterMap
	 * @return
	 * @throws Exception
	 */
	private static Map excludeParametersInMap(matrix.db.Context context, Map parameterMap) throws Exception {		
		Map localMap = new HashMap();
		try {
			Iterator<String> itr = parameterMap.keySet().iterator();
			String key = DomainConstants.EMPTY_STRING;
			String value = DomainConstants.EMPTY_STRING;	
			while (itr.hasNext())
			{
				key = itr.next();
				if(!key.contains(".Usages."))
				{
					value = (String)parameterMap.get(key);
					localMap.put(key, value);
				}
			}
		} catch (Exception e) {
			loggerApolloTrace.error(e.getMessage(), e);
			throw e;
		}	
		return localMap;
	}
	/**
	 * Method to upload XML in APP 2.1 Solution
	 * @param context
	 * @param XMLUpdateLog
	 * @param strAPPObjectId
	 * @param parameterMap
	 * @param variantParameterMap
	 * @param mode
	 * @throws Exception
	 */
	//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Starts
	//public static void createAndUploadXML(matrix.db.Context context, BufferedWriter XMLUpdateLog, String strAPPObjectId, Map parameterMap, Map variantParameterMap, String mode) throws Exception {
	public static void createAndUploadXML(matrix.db.Context context, String strAPPObjectId, Map parameterMap, Map variantParameterMap, Map performanceCharMap, Map variantPerformanceCharMap, String mode) throws Exception {
	//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Ends	
		try 
		{
			DomainObject domObject = DomainObject.newInstance(context, strAPPObjectId);	
			String strAPPName = (String)domObject.getInfo(context, DomainConstants.SELECT_NAME);
			
			StringBuilder sbDesignParamFileName = new StringBuilder();
			sbDesignParamFileName.append(pgApolloConstants.STR_AUTOMATION_DESIGN_PARAMETER_FILE_NAME).append(pgApolloConstants.CONSTANT_STRING_UNDERSCORE).append(strAPPName).append(pgApolloConstants.STR_XMLFILE_EXTENSION);
			
			String sWorkspacePath = context.createWorkspace();
			
			StringBuilder sbUpdateFileName = new StringBuilder();
			sbUpdateFileName.append(sWorkspacePath).append(File.separator).append(sbDesignParamFileName.toString());			
			
			File fFileOriginal = new File(sbUpdateFileName.toString());			
			
			uploadPreviousCollaoboratedFile(context, domObject, strAPPName, sWorkspacePath, fFileOriginal);				
			
			
			String updateFileName = sbUpdateFileName.toString();
			Document doc = getIntializedDocument();			
			// Root Element
			Element rootElement = doc.createElement(pgApolloConstants.STR_ROOT);
			doc.appendChild(rootElement);
			Element productDefElement = doc.createElement(pgApolloConstants.STR_PRODUCT_DEFINITION);
			rootElement.appendChild(productDefElement);
			String key = DomainConstants.EMPTY_STRING;
			String value = DomainConstants.EMPTY_STRING;	
			//APOLLO 2018x.5 A10-569 - Publish Design/Read Webservice changes for handling isActive true/false parameter - Starts
			String strIsActiveParameterKey = DomainConstants.EMPTY_STRING;
			StringBuilder sbParameter = new StringBuilder();
			//APOLLO 2018x.5 A10-569 - Publish Design/Read Webservice changes for handling isActive true/false parameter - Ends
			String strIsHiddenParameterKey = DomainConstants.EMPTY_STRING;
			Iterator<String> itr = parameterMap.keySet().iterator();
			//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Starts
			Element localElement = null;
			//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Ends
			while (itr.hasNext())
			{
				key = itr.next();
				//APOLLO 2018x.5 A10-569 - Publish Design/Read Webservice changes for handling isActive true/false parameter - Starts
				sbParameter = new StringBuilder();
				sbParameter.append(pgApolloConstants.CONSTANT_STRING_UNDERSCORE).append(pgApolloConstants.KEY_ISACTIVE);
				strIsActiveParameterKey = sbParameter.toString();
				sbParameter = new StringBuilder();
				sbParameter.append(pgApolloConstants.CONSTANT_STRING_UNDERSCORE).append(pgApolloConstants.KEY_HIDDEN);
				strIsHiddenParameterKey = sbParameter.toString();
				if(null!=key && (key.endsWith(strIsActiveParameterKey) || key.endsWith(strIsHiddenParameterKey)))
				{
					continue;
				}
				//APOLLO 2018x.5 A10-569 - Publish Design/Read Webservice changes for handling isActive true/false parameter - Ends
				value = (String)parameterMap.get(key);
				//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Starts
				localElement = doc.createElement(key);
				//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Ends
				localElement.setAttribute("Value", value);
				//APOLLO 2018x.5 A10-569 - Publish Design/Read Webservice changes for handling isActive true/false parameter - Starts
				localElement = processLocalElementForIsActive(parameterMap, key, localElement);				
				//APOLLO 2018x.5 A10-569 - Publish Design/Read Webservice changes for handling isActive true/false parameter - Ends
				localElement = processLocalElementForIsHidden(parameterMap, key, localElement);			
				productDefElement.appendChild(localElement);
			}			
			//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Starts
			Element performanceCharElement = doc.createElement(pgApolloConstants.STR_PERFORMANCE_CHAR);
			rootElement.appendChild(performanceCharElement);
			itr = performanceCharMap.keySet().iterator();
			while (itr.hasNext())
			{
				key = itr.next();
				//APOLLO 2018x.5 A10-569 - Publish Design/Read Webservice changes for handling isActive true/false parameter - Starts
				sbParameter = new StringBuilder();
				sbParameter.append(pgApolloConstants.CONSTANT_STRING_UNDERSCORE).append(pgApolloConstants.KEY_ISACTIVE);
				strIsActiveParameterKey = sbParameter.toString();
				sbParameter = new StringBuilder();
				sbParameter.append(pgApolloConstants.CONSTANT_STRING_UNDERSCORE).append(pgApolloConstants.KEY_HIDDEN);
				strIsHiddenParameterKey = sbParameter.toString();
				if(null!=key && (key.endsWith(strIsActiveParameterKey) || key.endsWith(strIsHiddenParameterKey)))
				{
					continue;
				}
				//APOLLO 2018x.5 A10-569 - Publish Design/Read Webservice changes for handling isActive true/false parameter - Ends
				value = (String)performanceCharMap.get(key);
				localElement = doc.createElement(key);
				localElement.setAttribute("Value", value);
				localElement = processLocalElementForIsActive(performanceCharMap, key, localElement);
				localElement = processLocalElementForIsHidden(parameterMap, key, localElement);		
				//APOLLO 2018x.5 A10-569 - Publish Design/Read Webservice changes for handling isActive true/false parameter - Ends
				performanceCharElement.appendChild(localElement);
			}
			//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Ends	
			if(pgApolloConstants.RANGE_VALUE_VARIANT.equalsIgnoreCase(mode))
			{
				//Element variantElement = doc.createElement("Variants");
				Element variantElement = doc.createElement(pgApolloConstants.STR_VARIANTS);
				rootElement.appendChild(variantElement);
				//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Starts
				if(null!=variantParameterMap && !variantParameterMap.isEmpty())
				{
				
					itr = variantParameterMap.keySet().iterator();
					while (itr.hasNext())
					{
						key = itr.next();
						//APOLLO 2018x.5 A10-569 - Publish Design/Read Webservice changes for handling isActive true/false parameter - Starts
						sbParameter = new StringBuilder();
						sbParameter.append(pgApolloConstants.CONSTANT_STRING_UNDERSCORE).append(pgApolloConstants.KEY_ISACTIVE);
						strIsActiveParameterKey = sbParameter.toString();
						sbParameter = new StringBuilder();
						sbParameter.append(pgApolloConstants.CONSTANT_STRING_UNDERSCORE).append(pgApolloConstants.KEY_HIDDEN);
						strIsHiddenParameterKey = sbParameter.toString();
						if(null!=key && (key.endsWith(strIsActiveParameterKey) || key.endsWith(strIsHiddenParameterKey)))
						{
							continue;
						}
						//APOLLO 2018x.5 A10-569 - Publish Design/Read Webservice changes for handling isActive true/false parameter - Ends
						value = (String)variantParameterMap.get(key);						
						localElement = doc.createElement(key);						
						localElement.setAttribute("Value", value);
						variantElement.appendChild(localElement);
					}
				}				
				Element variantPerformanceCharElement = doc.createElement(pgApolloConstants.STR_VARIANTS_PERFORMANCE_CHAR);
				rootElement.appendChild(variantPerformanceCharElement);	
				if(null!=variantPerformanceCharMap && !variantPerformanceCharMap.isEmpty())
				{
					itr = variantPerformanceCharMap.keySet().iterator();
					while (itr.hasNext())
					{
						key = itr.next();
						//APOLLO 2018x.5 A10-569 - Publish Design/Read Webservice changes for handling isActive true/false parameter - Starts
						sbParameter = new StringBuilder();
						sbParameter.append(pgApolloConstants.CONSTANT_STRING_UNDERSCORE).append(pgApolloConstants.KEY_ISACTIVE);
						strIsActiveParameterKey = sbParameter.toString();
						sbParameter = new StringBuilder();
						sbParameter.append(pgApolloConstants.CONSTANT_STRING_UNDERSCORE).append(pgApolloConstants.KEY_HIDDEN);
						strIsHiddenParameterKey = sbParameter.toString();
						if(null!=key && (key.endsWith(strIsActiveParameterKey) || key.endsWith(strIsHiddenParameterKey)))
						{
							continue;
						}
						//APOLLO 2018x.5 A10-569 - Publish Design/Read Webservice changes for handling isActive true/false parameter - Ends
						value = (String)variantPerformanceCharMap.get(key);
						localElement = doc.createElement(key);
						localElement.setAttribute("Value", value);
						variantPerformanceCharElement.appendChild(localElement);
					}
				}
				//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Ends
			}			
			writeXmlFile(updateFileName, doc);     
			domObject.checkinFile(context, false, true, "" , pgApolloConstants.FORMAT_NOT_RENDERABLE, sbDesignParamFileName.toString(), sWorkspacePath );

			deleteFile(updateFileName);
			loggerSync.debug( " Updated File checked in : {}  From Folder : {}", strAPPName, sWorkspacePath);		
	}
	catch (Exception ex) {
		loggerApolloTrace.error(ex.getMessage() ,ex);
		throw ex;
	}
}


	/**
	 * Method to upload previous collaborated file
	 * @param context
	 * @param domObject
	 * @param strAPPName
	 * @param sWorkspacePath
	 * @param fileOriginal
	 * @throws MatrixException
	 * @throws IOException
	 * @throws Exception
	 */
	public static void uploadPreviousCollaoboratedFile(matrix.db.Context context, DomainObject domObject,	String strAPPName, String sWorkspacePath, File fileOriginal)throws Exception 
	{
		FileList files = domObject.getFiles(context, pgApolloConstants.FORMAT_NOT_RENDERABLE);
		
		FileList slDesignParamFileList = new FileList();
		
		String sFileName;
		
		if(null != files && !files.isEmpty())
		{
			
			StringBuilder sbDesignParamFileName = new StringBuilder();
			sbDesignParamFileName.append(pgApolloConstants.STR_AUTOMATION_DESIGN_PARAMETER_FILE_NAME).append(pgApolloConstants.CONSTANT_STRING_UNDERSCORE).append(strAPPName).append(pgApolloConstants.STR_XMLFILE_EXTENSION);
		
			
			String sDesignParamFileName = sbDesignParamFileName.toString();
			
			StringBuilder sbDesignParamBackupFileName = new StringBuilder();
			sbDesignParamBackupFileName.append(pgApolloConstants.STR_AUTOMATION_DESIGN_PARAMETER_PREVIOUS_FILE_NAME).append(pgApolloConstants.CONSTANT_STRING_UNDERSCORE).append(strAPPName).append(pgApolloConstants.STR_XMLFILE_EXTENSION);
			
			StringBuilder sbUpdateBackupFileName = new StringBuilder();
			sbUpdateBackupFileName.append(sWorkspacePath).append(File.separator).append(sbDesignParamBackupFileName.toString());
		
			String sBackupFileWithPath = sbUpdateBackupFileName.toString();
			File fileBackup = new File(sBackupFileWithPath);
			
			for(matrix.db.File f : files)
			{
				sFileName = f.getName();
				if(UIUtil.isNotNullAndNotEmpty(sFileName) && sFileName.equals(sDesignParamFileName))
				{
					slDesignParamFileList.add(f);
				}
			}
			
			if(!slDesignParamFileList.isEmpty())
			{
				domObject.checkoutFiles(context, false, pgApolloConstants.FORMAT_NOT_RENDERABLE, slDesignParamFileList, sWorkspacePath);
				
				FileUtils.copyFile(fileOriginal, fileBackup);			
				
				domObject.checkinFile(context, false, true, "" , pgApolloConstants.FORMAT_NOT_RENDERABLE, sbDesignParamBackupFileName.toString(), sWorkspacePath);	
				
				deleteFile(sBackupFileWithPath);
			}		
			
		}
	}

	/**
	 * Method to process Local element for Hidden
	 * @param parameterMap
	 * @param key
	 * @param localElement
	 */
	private static Element processLocalElementForIsHidden(Map parameterMap, String key, Element localElement) {
		StringBuilder sbParameter;
		String strIsHiddenParameterKey;
		String strIsHiddenValue;
		String strIsHidden;
		strIsHiddenValue = DomainConstants.EMPTY_STRING;
		sbParameter = new StringBuilder();
		sbParameter.append(key).append(pgApolloConstants.CONSTANT_STRING_UNDERSCORE).append(pgApolloConstants.KEY_HIDDEN);
		strIsHiddenParameterKey = sbParameter.toString();
		if(parameterMap.containsKey(strIsHiddenParameterKey))
		{
			strIsHidden = (String)parameterMap.get(strIsHiddenParameterKey);
			if(pgApolloConstants.KEY_HIDDEN.equalsIgnoreCase(strIsHidden))
			{
				strIsHiddenValue = pgApolloConstants.STR_TRUE_FLAG;
				localElement.setAttribute(pgApolloConstants.KEY_HIDDEN, strIsHiddenValue);
			}					
		}
		return localElement;
	}


	/**
	 * Method to process Local Element for IsActive
	 * @param parameterMap
	 * @param key
	 * @param localElement
	 */
	private static Element processLocalElementForIsActive(Map parameterMap, String key, Element localElement) {
		String strIsActive;
		String strIsActiveValue;
		String strIsActiveParameterKey;
		StringBuilder sbParameter;
		strIsActiveValue = DomainConstants.EMPTY_STRING;
		sbParameter = new StringBuilder();
		sbParameter.append(key).append(pgApolloConstants.CONSTANT_STRING_UNDERSCORE).append(pgApolloConstants.KEY_ISACTIVE);
		strIsActiveParameterKey = sbParameter.toString();
		if(parameterMap.containsKey(strIsActiveParameterKey))
		{
			strIsActive = (String)parameterMap.get(strIsActiveParameterKey);
			if(pgApolloConstants.STR_FALSE_FLAG.equalsIgnoreCase(strIsActive))
			{
				strIsActiveValue = pgApolloConstants.STR_FALSE_FLAG;
				localElement.setAttribute(pgApolloConstants.KEY_ISACTIVE, strIsActiveValue);
			}					
		}
		return localElement;
	}

	/**
	 * Method to Initialize Document Object
	 * @return
	 * @throws Exception
	 */
	private static Document getIntializedDocument() throws Exception {
		DocumentBuilderFactory dbFactory;
		DocumentBuilder dBuilder;
		Document doc;
		try {
			dbFactory = DocumentBuilderFactory.newInstance("com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl", null);
			dbFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, ""); // Compliant
			dbFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, ""); // compliant
            dbFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true); // Secure processing
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.newDocument();
		} catch (Exception e) {
			loggerApolloTrace.error(e.getMessage(), e);
			throw e;
		}
		return doc;
	}
		
	/**
	 * Method to find difference in Maps 2.1 Solution
	 * @param baseAPPParameterMap
	 * @param parameterMap
	 * @return
	 */
	private static Map getDifferenceInMap(Map baseAPPParameterMap, Map parameterMap) {
		Map localMap = new HashMap();
		try {
			//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Starts
			if(null!=baseAPPParameterMap && !baseAPPParameterMap.isEmpty())
			{
			//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Ends
				Iterator<String> itr = baseAPPParameterMap.keySet().iterator();
				String key = DomainConstants.EMPTY_STRING;
				String value1 = DomainConstants.EMPTY_STRING;	
				String value2 = DomainConstants.EMPTY_STRING;		

				while (itr.hasNext())
				{
					key = itr.next();
					if(parameterMap.containsKey(key))
					{
						value1 = (String)baseAPPParameterMap.get(key);
						value2 = (String)parameterMap.get(key);
						if(!value1.equals(value2))
						{
							localMap.put(key, value2);
						}

					}
				}			
				itr = parameterMap.keySet().iterator();
				while (itr.hasNext())
				{
					key = itr.next();
					if(!baseAPPParameterMap.containsKey(key))
					{
						value2 = (String)parameterMap.get(key);
						localMap.put(key, value2);
					}
				}
			//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Starts
			}
			//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Ends
		} catch (Exception e) {
			loggerApolloTrace.error(e.getMessage(), e);
			throw e;
		}
		return localMap;
	}

	/**
	 * Method read Configuration parameters APP 2.1 Solution
	 * This method is called from Design Parameters table and Comparison report functionaity to get all Design Parameters in the XML from APP / VPMReference.
	 * @param context
	 * @param updateFileName
	 * @return
	 * @throws Exception
	 */
	//APOLLO 2018x.5 A10-569 - Publish Design/Read Webservice changes for handling isActive true/false parameter - Starts
	//public static Map getConfigXMLParameters(matrix.db.Context context, String strObjectId) throws Exception {
	public static Map getConfigXMLParameters(matrix.db.Context context, String strObjectId, boolean bSkipInactive, boolean bSkipHidden) throws Exception {
	//APOLLO 2018x.5 A10-569 - Publish Design/Read Webservice changes for handling isActive true/false parameter - Starts	
		Map XMLParameterMap = new HashMap();
		//String updateFileName = DomainConstants.EMPTY_STRING;
		String strFileName = DomainConstants.EMPTY_STRING;		
		String strAPPObjectId = DomainConstants.EMPTY_STRING;
		String objectType = DomainConstants.EMPTY_STRING;
		StringBuffer updateFileName = new StringBuffer();
		try 
		{
			DomainObject domObject = DomainObject.newInstance( context, strObjectId);
			objectType = (String)domObject.getInfo(context, DomainConstants.SELECT_TYPE);
			if(pgApolloConstants.TYPE_VPMREFERENCE.equalsIgnoreCase(objectType))
			{
				strAPPObjectId = fetchAPPObject(context, domObject);
			}
			else
			{
				strAPPObjectId = strObjectId;
			}
			//APOLLO 2018x.5 A10-563 Starts
			if(UIUtil.isNotNullAndNotEmpty(strAPPObjectId))
			{
				DomainObject domAPPObject = DomainObject.newInstance( context, strAPPObjectId);
				String sAPPName = (String)domAPPObject.getInfo(context, DomainConstants.SELECT_NAME);
				//APOLLO 2018x.5 A10-563 Ends
				StringBuffer sbFileNameDP = new StringBuffer();
				sbFileNameDP.append(pgApolloConstants.STR_AUTOMATION_DESIGN_PARAMETER_FILE_NAME).append(pgApolloConstants.CONSTANT_STRING_UNDERSCORE).append(sAPPName).append(pgApolloConstants.STR_XMLFILE_EXTENSION);
				//String strFileNameDP = pgApolloConstants.STR_AUTOMATION_DESIGN_PARAMETER_FILE_NAME+pgApolloConstants.CONSTANT_STRING_UNDERSCORE + (String)domObject.getInfo(context, DomainConstants.SELECT_NAME) + ".xml";	
				String strFileNameDP = sbFileNameDP.toString();
				XMLParameterMap = checkoutAndGetConfigParameterParameters(context, strAPPObjectId, strFileNameDP, bSkipInactive, bSkipHidden);
			//APOLLO 2018x.5 A10-563 Starts
			}
			//APOLLO 2018x.5 A10-563 Ends
		} catch (Exception e) {
			loggerApolloTrace.error(e.getMessage(), e);
			throw e;
		}		
		return XMLParameterMap;
	}

	/**
	 * Method to checkout and get Config parameters based on file name checked in
	 * @param context
	 * @param sObjectId
	 * @param sFileNameDP
	 * @param bSkipInactive
	 * @param bSkipHidden
	 * @return
	 * @throws MatrixException
	 * @throws Exception
	 */
	public static Map checkoutAndGetConfigParameterParameters(matrix.db.Context context, String sObjectId, String sFileNameDP, boolean bSkipInactive,boolean bSkipHidden) throws MatrixException, Exception
	{		
		Map mapXMLParameter = new HashMap();
		StringBuffer updateFileName = new StringBuffer();
		String strFileName;		
		DomainObject domObject = DomainObject.newInstance( context, sObjectId);
		String sWorkspacePath = context.createWorkspace();
		FileList files = domObject.getFiles(context, pgApolloConstants.FORMAT_NOT_RENDERABLE);
		if(files!=null && files.size()>0)
		{
			for(matrix.db.File f : files)
			{
				strFileName = (String)f.getName();
				if(UIUtil.isNotNullAndNotEmpty(strFileName) && strFileName.equals(sFileNameDP))
				{
					domObject.checkoutFile(context, false, pgApolloConstants.FORMAT_NOT_RENDERABLE, strFileName, sWorkspacePath);
					updateFileName.append(sWorkspacePath).append(File.separator).append(strFileName);
					mapXMLParameter = getConfigXMLParametersMap(context, updateFileName.toString(), bSkipInactive, bSkipHidden);
					deleteFile(updateFileName.toString());
					break;
				}
			}			
		}
		return mapXMLParameter;
	}

	/**
	 * Method read Configuration parameters APP based on File Name 2.1 Solution
	 * @param context
	 * @param updateFileName
	 * @return
	 * @throws Exception
	 */
	//APOLLO 2018x.5 A10-569 - Publish Design/Read Webservice changes for handling isActive true/false parameter - Starts
	//public static Map getConfigXMLParametersMap(matrix.db.Context context, String updateFileName) throws Exception
	public static Map getConfigXMLParametersMap(matrix.db.Context context, String updateFileName, boolean bSkipInActive, boolean bSkipHidden) throws Exception
	//APOLLO 2018x.5 A10-569 - Publish Design/Read Webservice changes for handling isActive true/false parameter - Ends
	{
		Map XMLParameterMap = new HashMap();
		File file = new File(updateFileName);
		XPath xPath;
		DocumentBuilderFactory dbFactory = null;
		DocumentBuilder dBuilder;
		Document doc;

		try 
		{
			dbFactory = DocumentBuilderFactory.newInstance("com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl", null);
			dbFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, ""); // Compliant
			dbFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, ""); // compliant
            dbFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true); // Secure processing
			dBuilder = dbFactory.newDocumentBuilder();

			doc = dBuilder.parse(file);
			doc.getDocumentElement().normalize();

			xPath = XPathFactory.newInstance().newXPath();
		} catch (Exception e) {
			loggerApolloTrace.error(e.getMessage(), e);
			throw e;
		}
		
		//String strXPath = "Root/ProductDefinition";
		//String strXPath = pgApolloConstants.STR_ROOT + "/" + pgApolloConstants.STR_PRODUCT_DEFINITION;
		StringBuffer sbXPath = new StringBuffer();
		sbXPath.append(pgApolloConstants.STR_ROOT).append(pgApolloConstants.CONSTANT_STRING_XMLPATH_SEPARATOR).append(pgApolloConstants.STR_PRODUCT_DEFINITION);
		String strXPath = sbXPath.toString();
		
		try {
			Map parameterMap = new HashMap();
			NodeList nodeList = (NodeList) xPath.compile(strXPath).evaluate(doc, XPathConstants.NODESET);
			int nodeLength = nodeList.getLength();
			if(nodeLength>0)
			{
				Node nNode = nodeList.item(0);
				parameterMap = new HashMap();
				//APOLLO 2018x.5 A10-569 - Publish Design/Read Webservice changes for handling isActive true/false parameter - Starts
				parameterMap = getParameterMap(xPath, doc, nNode, bSkipInActive, bSkipHidden);
				//APOLLO 2018x.5 A10-569 - Publish Design/Read Webservice changes for handling isActive true/false parameter - Ends
				XMLParameterMap.put(pgApolloConstants.STR_PRODUCT_DEFINITION, parameterMap);
				//strXPath = "Root/Variants";
				//strXPath = pgApolloConstants.STR_ROOT + "/" + pgApolloConstants.STR_VARIANTS;
				
				sbXPath = new StringBuffer();
				sbXPath.append(pgApolloConstants.STR_ROOT).append(pgApolloConstants.CONSTANT_STRING_XMLPATH_SEPARATOR).append(pgApolloConstants.STR_VARIANTS);
				strXPath = sbXPath.toString();
				
				nodeList = (NodeList) xPath.compile(strXPath).evaluate(doc, XPathConstants.NODESET);
				nodeLength = nodeList.getLength();
				if(nodeLength>0)
				{
					nNode = nodeList.item(0);
					parameterMap = new HashMap();
					//APOLLO 2018x.5 A10-569 - Publish Design/Read Webservice changes for handling isActive true/false parameter - Starts
					parameterMap = getParameterMap(xPath, doc, nNode, bSkipInActive, bSkipHidden);
					//APOLLO 2018x.5 A10-569 - Publish Design/Read Webservice changes for handling isActive true/false parameter - Ends
					XMLParameterMap.put(pgApolloConstants.STR_VARIANTS, parameterMap);
					//XMLParameterMap.put("ModelType", "Variant");
				}
				else
				{
					//XMLParameterMap.put("ModelType", "Base");
				}
				//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Starts
				sbXPath = new StringBuffer();
				sbXPath.append(pgApolloConstants.STR_ROOT).append(pgApolloConstants.CONSTANT_STRING_XMLPATH_SEPARATOR).append(pgApolloConstants.STR_PERFORMANCE_CHAR);
				strXPath = sbXPath.toString();
				
				nodeList = (NodeList) xPath.compile(strXPath).evaluate(doc, XPathConstants.NODESET);
				nodeLength = nodeList.getLength();
				if(nodeLength>0)
				{
					nNode = nodeList.item(0);
					parameterMap = new HashMap();
					//APOLLO 2018x.5 A10-569 - Publish Design/Read Webservice changes for handling isActive true/false parameter - Starts
					parameterMap = getParameterMap(xPath, doc, nNode, bSkipInActive, bSkipHidden);
					//APOLLO 2018x.5 A10-569 - Publish Design/Read Webservice changes for handling isActive true/false parameter - Ends
					XMLParameterMap.put(pgApolloConstants.STR_PERFORMANCE_CHAR, parameterMap);
				}
				
				sbXPath = new StringBuffer();
				sbXPath.append(pgApolloConstants.STR_ROOT).append(pgApolloConstants.CONSTANT_STRING_XMLPATH_SEPARATOR).append(pgApolloConstants.STR_VARIANTS_PERFORMANCE_CHAR);
				strXPath = sbXPath.toString();
				
				nodeList = (NodeList) xPath.compile(strXPath).evaluate(doc, XPathConstants.NODESET);
				nodeLength = nodeList.getLength();
				if(nodeLength>0)
				{
					nNode = nodeList.item(0);
					parameterMap = new HashMap();
					//APOLLO 2018x.5 A10-569 - Publish Design/Read Webservice changes for handling isActive true/false parameter - Starts
					parameterMap = getParameterMap(xPath, doc, nNode, bSkipInActive, bSkipHidden);
					//APOLLO 2018x.5 A10-569 - Publish Design/Read Webservice changes for handling isActive true/false parameter - Ends
					XMLParameterMap.put(pgApolloConstants.STR_VARIANTS_PERFORMANCE_CHAR, parameterMap);
				}
				//APOLLO 2018x.5 A10-507 - Publish Design Information - Storing Performance Characteristics Ends
				
			}
		} catch (Exception e) {
			loggerApolloTrace.error(e.getMessage(), e);
			throw e;
		}		
		return XMLParameterMap;
	}

	/**
	 * Method to fetch APP Object Id connected to VPMReference 2.1 Solution
	 * @param context
	 * @param domVPMReference
	 * @return
	 * @throws Exception
	 */
	public static String fetchAPPObject(matrix.db.Context context, DomainObject domVPMReference) throws Exception 
	{		
		StringList objectSelects = new StringList();
		objectSelects.add(DomainConstants.SELECT_ID);		
		String strAPPObjectId = DomainConstants.EMPTY_STRING;		
		try 
		{
			//Get APP associated with VPMReference
			MapList mlRelatedAPP = domVPMReference.getRelatedObjects(context,
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
				strAPPObjectId = (String)mpRelatedAPP.get(DomainConstants.SELECT_ID);
			}
		} catch (Exception e) {
			loggerApolloTrace.error(e.getMessage(), e);
			throw e;
		}
		return strAPPObjectId;
	}
	
	/**
	 * Method to return Parameter Map for specified node used in 2.1 Solution
	 * @param xPath
	 * @param doc
	 * @param configurationNode
	 * @return
	 */
	//APOLLO 2018x.5 A10-569 - Publish Design/Read Webservice changes for handling isActive true/false parameter - Starts
	//private static Map getParameterMap(XPath xPath, Document doc, Node ParentNode) {
	private static Map getParameterMap(XPath xPath, Document doc, Node ParentNode, boolean bSkipInActive, boolean bSkipHidden) {
	//APOLLO 2018x.5 A10-569 - Publish Design/Read Webservice changes for handling isActive true/false parameter - Ends
		Map parameterMap = new HashMap();
		try {
			NodeList parameterNodeList;
			String strValue = DomainConstants.EMPTY_STRING;
			//APOLLO 2018x.5 A10-569 - Publish Design/Read Webservice changes for handling isActive true/false parameter - Starts
			String strIsActiveValue = DomainConstants.EMPTY_STRING;
			StringBuffer sbParameter = new StringBuffer();
			String strIsActiveParameterKey = DomainConstants.EMPTY_STRING;
			sbParameter.append(pgApolloConstants.CONSTANT_STRING_DOT).append(pgApolloConstants.KEY_ISACTIVE);
			strIsActiveParameterKey = sbParameter.toString();
			//APOLLO 2018x.5 A10-569 - Publish Design/Read Webservice changes for handling isActive true/false parameter - Ends
			String strIsHiddenValue = DomainConstants.EMPTY_STRING;
			sbParameter = new StringBuffer();
			String strIsHiddenParameterKey = DomainConstants.EMPTY_STRING;
			sbParameter.append(pgApolloConstants.CONSTANT_STRING_DOT).append(pgApolloConstants.KEY_HIDDEN);
			strIsHiddenParameterKey = sbParameter.toString();
			Node nNode;
			String parameterName = DomainConstants.EMPTY_STRING;
			parameterNodeList = ParentNode.getChildNodes();
			int length = parameterNodeList.getLength();
			if(length>0)
			{
				for (int n = 0; n < length; n++) {	
					nNode = parameterNodeList.item(n);
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {	
						parameterName = nNode.getNodeName();
						Element eElement = (Element) nNode;
						//APOLLO 2018x.5 A10-569 - Publish Design/Read Webservice changes for handling isActive true/false parameter - Starts
						if(bSkipInActive)
						{
							if(eElement.hasAttribute(pgApolloConstants.KEY_ISACTIVE))
							{
								strIsActiveValue = eElement.getAttribute(pgApolloConstants.KEY_ISACTIVE);
								if(pgApolloConstants.STR_FALSE_FLAG.equalsIgnoreCase(strIsActiveValue))
								{
									continue;
								}
							}						 
							if(null!=parameterName && parameterName.endsWith(strIsActiveParameterKey))
							{
								continue;
							}
						}
						//APOLLO 2018x.5 A10-569 - Publish Design/Read Webservice changes for handling isActive true/false parameter - Ends
						if(bSkipHidden)
						{
							if(eElement.hasAttribute(pgApolloConstants.KEY_HIDDEN))
							{
								strIsHiddenValue = eElement.getAttribute(pgApolloConstants.KEY_HIDDEN);
								if(pgApolloConstants.STR_TRUE_FLAG.equalsIgnoreCase(strIsHiddenValue))
								{
									continue;
								}
							}						 
							if(null!=parameterName && parameterName.endsWith(strIsHiddenParameterKey))
							{
								continue;
							}
						}
						if(null!=parameterName)
						{
							parameterName = unescapeSpecialCharacterForDesignParameter(parameterName, true);
						}
						strValue = eElement.getAttribute("Value");
						strValue = unescapeSpecialCharacterForDesignParameter(strValue, false);
						parameterMap.put(parameterName, strValue);
					}
				}
			}
		} catch (Exception e) {
			loggerApolloTrace.error(e.getMessage(), e);
			throw e;
		}		
		return parameterMap;
	}

	/**
	 * Writer Doc to XML file used in 2.1 Solution
	 * @param filename
	 * @param doc
	 * @throws Exception
	 */
	public static void writeXmlFile(String filename, Document doc) throws Exception {
		try 
		{

			// Prepare the DOM document for writing
			Source source = new DOMSource(doc);

			// Prepare the output file
			File file = new File(filename);
			if (!file.exists())
			{
				if(!file.getParentFile().exists())
				{
					file.getParentFile().mkdirs();		
				}
				boolean bFileCreation = file.createNewFile();
				if(!bFileCreation)
				{
					loggerApolloTrace.debug("File Creation is failed");
				}
			}
			// Result result = new StreamResult(file);
			Result result = new StreamResult(file.getAbsolutePath());
			// Write the DOM document to the file
			TransformerFactory factory = TransformerFactory.newInstance("com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl", null);
			 factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
             factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
             factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true); // Secure processing
            
			Transformer xformer = factory.newTransformer();

			// Following code is important to retain the doctype dtd info in o/p
			// xml
			DocumentType doctype = doc.getDoctype();
			if (doctype != null) {
				xformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doctype.getSystemId());
			}
			xformer.setOutputProperty(OutputKeys.INDENT, "yes");
			xformer.setOutputProperty(OutputKeys.METHOD, "xml");
			xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			xformer.transform(source, result);
		} catch (Exception e) {
			loggerApolloTrace.error(e.getMessage(), e);
			throw e;
		}
	}	
	
	
	/**
	 * Method to unescape special character for Design Param
	 * @param strParamValue
	 * @return
	 */
	public static String unescapeSpecialCharacterForDesignParameter(String strParameter, boolean bIncludeParameterDecoding) 
	{
		if(UIUtil.isNotNullAndNotEmpty(strParameter))
		{
			if(bIncludeParameterDecoding)
			{
				strParameter = strParameter.replace(pgApolloConstants.STR_SPACE_CHAR, pgApolloConstants.CONSTANT_STRING_SPACE);	
			}
			strParameter = strParameter.replace(pgApolloConstants.STR_TILDA_CHAR, pgApolloConstants.CONSTANT_STRING_TILDA);	
			strParameter = StringEscapeUtils.unescapeXml(strParameter);
		}		
		return strParameter;
	}
	
	
	/**
	 * Method to escape special character for Design Param
	 * @param strParamValue
	 * @return
	 */
	public static String escapeSpecialCharacterForDesignParameter(String strParameter, boolean bIncludeParameterEncoding) 
	{
		if(UIUtil.isNotNullAndNotEmpty(strParameter))
		{
			if(bIncludeParameterEncoding)
			{
				strParameter = strParameter.replace(pgApolloConstants.CONSTANT_STRING_SPACE, pgApolloConstants.STR_SPACE_CHAR);			
			}
			strParameter = strParameter.replace(pgApolloConstants.CONSTANT_STRING_TILDA, pgApolloConstants.STR_TILDA_CHAR);	
		}		
		return strParameter;
	}
	
	
	
	/**
	 * Method to escape Base map to make compatible with variant map
	 * @param baseAPPParameterMap
	 * @param parameterMap
	 * @return
	 */
	public static Map getEncodedMap(Map baseAPPParameterMap) throws Exception
	{
		Map mapLocal = new HashMap();
		if(null!=baseAPPParameterMap && !baseAPPParameterMap.isEmpty())
		{
			Iterator<String> itr = baseAPPParameterMap.keySet().iterator();
			String strKey;
			String strValue;	
			while (itr.hasNext())
			{
				strKey = itr.next();
				strValue = (String)baseAPPParameterMap.get(strKey);
				strKey = escapeSpecialCharacterForDesignParameter(strKey, true);
				strValue = escapeSpecialCharacterForDesignParameter(strValue, false);
				mapLocal.put(strKey, strValue);					
			}				
		}
		return mapLocal;
	}

}


/*
 * Added by APOLLO Team
 * Webservice to populate range values in CATIA
 */

package com.png.integ.designtool.getData;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dassault_systemes.enovia.enterprisechangemgt.util.ChangeUtil;
import com.matrixone.apps.cpn.CPNCommonConstants;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.servlet.Framework;
import com.png.apollo.pgApolloCommonUtil;
import com.png.apollo.pgApolloConstants;

import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.Pattern;
import matrix.util.StringList;

@Path("/fetchData")
public class UpdateRangeForCATIAAttributes {

	 private static final Logger loggerWS = LoggerFactory.getLogger("APOLLOWS");
	 private static final org.apache.log4j.Logger loggerApolloTrace = org.apache.log4j.Logger.getLogger(UpdateRangeForCATIAAttributes.class);
	
	
	@GET
	@Path("/getRangeValues")
	public Response getRangeValues(@Context HttpServletRequest req) throws Exception {
		loggerWS.debug("Entered method in GET UpdateRangeForCATIAAttributes : getRangeValues --------");
		loggerWS.debug("Path : /getIntegDesignToolData/fetchData/getRangeValues"); 
		matrix.db.Context context = null;
		StringBuffer sbReturnMessages = new StringBuffer();
		String sAttributeName = "";
		String sInputValue = "";
		String strType = "";
		StringList slOutput;
		StringList slInput;

		try {
			String[] URLParam = null;
			String[] URLParamValue = null;
			int iURLParamLength;
			int iURLParamValueLength;
			// Get the user context
			if (Framework.isLoggedIn(req)) {
				context = Framework.getContext(req.getSession(false));
			}			
			
			loggerWS.debug("******************************************************************************");
			String strStartDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
			loggerWS.debug("Range For CATIA Attribute starts : {}"  , strStartDate );
			loggerWS.debug("Path : /getIntegDesignToolData/fetchData/getRangeValues");
			if(null!= context)
			{
				String strUser = context.getUser();
				loggerWS.debug("Context User: {}" , strUser);
			}
			
			String queryString = req.getQueryString();
			queryString =  java.net.URLDecoder.decode(queryString,"UTF-8");
			loggerWS.debug("QueryString  :{}" , queryString);
			
			if (UIUtil.isNotNullAndNotEmpty(queryString)) {
				URLParam = queryString.split("&");
				iURLParamLength = URLParam.length;
				if (URLParam != null && iURLParamLength > 0) {	
					URLParamValue = URLParam[0].split("=");//Split attribute parameter
					iURLParamValueLength = URLParamValue.length;
					if(URLParamValue != null && iURLParamValueLength > 1) {							
						sAttributeName = URLParamValue[1].trim();
					}
					loggerWS.debug("Attribute  :{}" , sAttributeName);
					if (iURLParamLength > 1) {						
						URLParamValue = URLParam[1].split("=");//Split input parameter
						iURLParamValueLength = URLParamValue.length;
						if(URLParamValue != null && iURLParamValueLength > 1) {							
							sInputValue = URLParamValue[1].trim();
						}
						loggerWS.debug("Input  :{}" , sInputValue);
					}
					
					if(UIUtil.isNotNullAndNotEmpty(sAttributeName)) {				
						StringList slSelectedPlatformList = new StringList();
						Map programMap = new HashMap();
						if ("ProductCategoryPlatform".equalsIgnoreCase(sAttributeName)) {			
							slSelectedPlatformList = getSelectedPlatformList(context, "pgPLIBusinessArea", sInputValue, "");
							programMap.put("selectedPlatformList",slSelectedPlatformList);
							programMap.put("strgetAttrObject", pgApolloConstants.STR_PRODUCT_CATEGORY_PLATFORM);
							String[] methodargs = JPO.packArgs(programMap);
							strType = "PlatformToBusinessArea";
							sbReturnMessages.append(getProductFormPlatform(context, slSelectedPlatformList, methodargs, strType));
						}
						else if ("ProductTechnologyPlatform".equalsIgnoreCase(sAttributeName)) {
							slSelectedPlatformList = getSelectedPlatformList(context, pgApolloConstants.TYPE_PG_PLI_PRODUCTCATEGORYPLATFORM, sInputValue, "");
							programMap.put("selectedPlatformList",slSelectedPlatformList);
							programMap.put("strgetAttrObject", pgApolloConstants.STR_PRODUCT_TECHNOLOGY_PLATFORM);
							String[] methodargs = JPO.packArgs(programMap);
							strType = "Platform";
							sbReturnMessages.append(getProductFormPlatform(context, slSelectedPlatformList, methodargs, strType));
						}
						else if ("ProductTechnologyChassis".equalsIgnoreCase(sAttributeName)) {
							
							StringList slInputValues = StringUtil.split(sInputValue, pgApolloConstants.CONSTANT_STRING_PIPE);
							slOutput = pgApolloCommonUtil.getRelatedPTCListBasedOnParentNames(context, new StringList(slInputValues.get(0).toString()), new StringList(slInputValues.get(1).toString()));
							sbReturnMessages.append(slOutput.join(pgApolloConstants.CONSTANT_STRING_PIPE));
							
						}
						else if ("BusinessArea".equalsIgnoreCase(sAttributeName)) {
							slSelectedPlatformList = getSelectableFromMapList(context, "pgPLIBusinessArea", "*", "", DomainConstants.SELECT_NAME);
							int iSize = slSelectedPlatformList.size();
							for (int iCount = 0; iCount < iSize; iCount++) {					
								sbReturnMessages.append(slSelectedPlatformList.get(iCount));
								if(iCount != iSize-1)
									sbReturnMessages.append("|");
							}
						}			 
						else if ("PrimaryOrganization".equalsIgnoreCase(sAttributeName)) {							
							
							String strWhere = DomainConstants.EMPTY_STRING;
							if(UIUtil.isNotNullAndNotEmpty(sInputValue))
							{
								sInputValue = DomainConstants.QUERY_WILDCARD + sInputValue + DomainConstants.QUERY_WILDCARD;
								strWhere = "name ~~" + sInputValue;
							}
							
							slSelectedPlatformList = getSelectableFromMapList(context, "pgPLIOrganizationChangeManagement", DomainConstants.QUERY_WILDCARD, strWhere, DomainConstants.SELECT_NAME);
							
							int iSize = slSelectedPlatformList.size();
							for (int iCount = 0; iCount < iSize; iCount++) {					
								sbReturnMessages.append(slSelectedPlatformList.get(iCount));
								if(iCount != iSize-1)
									sbReturnMessages.append("|");
							}
						}
						else if ("FranchisePlatform".equalsIgnoreCase(sAttributeName)) {			
							slSelectedPlatformList = getSelectedPlatformList(context, "pgPLIBusinessArea", sInputValue, "");
							programMap.put("selectedPlatformList",slSelectedPlatformList);
							programMap.put("strgetAttrObject", "Franchise Platform");
							String[] methodargs = JPO.packArgs(programMap);
							strType = "PlatformToBusinessArea";
							sbReturnMessages.append(getProductFormPlatform(context, slSelectedPlatformList, methodargs, strType));
						}
						else if ("Region".equalsIgnoreCase(sAttributeName) || "SubRegion".equalsIgnoreCase(sAttributeName))
						{
							sbReturnMessages = getRegionSubRegionPickListValues(context, sAttributeName, sInputValue);						
							
						}
						//2018x.2 ALM 27912 - Requirement 29039 - Product Size on APP - start
						else if (pgApolloConstants.STR_PRODUCT_SIZE.equalsIgnoreCase(sAttributeName)) {
							slSelectedPlatformList = getSelectedPlatformList(context, "pgPLIChassis", sInputValue, "");
							programMap.put("selectedPlatformList",slSelectedPlatformList);
							programMap.put("strgetAttrObject", "Product Size");
							String[] methodargs = JPO.packArgs(programMap);
							strType = pgApolloConstants.STR_PRODUCT_SIZE;
							sbReturnMessages.append(getProductFormPlatform(context, slSelectedPlatformList, methodargs, strType));
						}
						//2018x.2 ALM 27912 - Requirement 29039 - Product Size on APP - end
						//APOLLO 2018x.5 ALM Requirement 34599 - A10-535 - get Intended Market List - Start
						else if (pgApolloConstants.STR_AUTOMATION_INTENDEDMARKETS.equalsIgnoreCase(sAttributeName)) {						
							String strWhere;
							StringBuffer sbWhereClause = new StringBuffer();
							sbWhereClause.append(DomainConstants.SELECT_CURRENT).append(pgApolloConstants.CONSTANT_STRING_EQUAL_SIGN).append(pgApolloConstants.CONSTANT_STRING_EQUAL_SIGN).append(pgApolloConstants.STATE_ACTIVE);
							if(UIUtil.isNotNullAndNotEmpty(sInputValue))
							{
								sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE).append(pgApolloConstants.CONSTANT_STRING_AMPERSAND).append(pgApolloConstants.CONSTANT_STRING_AMPERSAND).append(pgApolloConstants.CONSTANT_STRING_SPACE);
								sbWhereClause.append(DomainConstants.SELECT_NAME).append(pgApolloConstants.CONSTANT_STRING_SPACE).append(pgApolloConstants.CONSTANT_STRING_TILDA).append(pgApolloConstants.CONSTANT_STRING_TILDA).append(pgApolloConstants.CONSTANT_STRING_SPACE).append("'");
								sbWhereClause.append(DomainConstants.QUERY_WILDCARD).append(sInputValue).append(DomainConstants.QUERY_WILDCARD);
								sbWhereClause.append("'");
							}
							strWhere = sbWhereClause.toString();
							slSelectedPlatformList = getSelectableFromMapList(context, CPNCommonConstants.TYPE_COUNTRY, DomainConstants.QUERY_WILDCARD, strWhere, DomainConstants.SELECT_NAME);
							if(null!=slSelectedPlatformList && !slSelectedPlatformList.isEmpty())
							{
								Collections.sort(slSelectedPlatformList);
								int iSize = slSelectedPlatformList.size();
								for (int iCount = 0; iCount < iSize; iCount++) {					
									sbReturnMessages.append(slSelectedPlatformList.get(iCount));
									if(iCount != iSize-1)
										sbReturnMessages.append(pgApolloConstants.CONSTANT_STRING_PIPE);
								}
							}
							else
							{
								sbReturnMessages.append(pgApolloConstants.STR_AUTOMATION_BLANK);
							}
						}
						//APOLLO 2018x.5 ALM Requirement 34599 - A10-535 - get Intended Market List - End
						//APOLLO 2018x.6 ALM Requirement - A10-781 Component Family Changes Start
						else if(pgApolloConstants.STR_EOPROJECTTYPE.equalsIgnoreCase(sAttributeName)) {
							StringBuilder sbRange = new StringBuilder();
							String attrProjectType  = PropertyUtil.getSchemaProperty(context,"attribute_ProjectType");
							StringList projectTypeRanges = FrameworkUtil.getRanges(context, attrProjectType);
							if(!projectTypeRanges.isEmpty()) {
								projectTypeRanges.sort();
								for(int i  = 0 ; i < projectTypeRanges.size(); i++) {
									sbRange.append(projectTypeRanges.get(i));
									if(i != projectTypeRanges.size()-1) {
										sbRange.append(pgApolloConstants.CONSTANT_STRING_PIPE);
									}
								}
							}
							sbReturnMessages.append(sbRange.toString());
						}
						else if(pgApolloConstants.STR_EOPROJECT.equalsIgnoreCase(sAttributeName)) {
							String strProjectType = sInputValue;
							String strProjectInitialName;
							StringBuilder sbWhereClause = new StringBuilder();
							if(UIUtil.isNotNullAndNotEmpty(strProjectType)) {
								String[] values = strProjectType.split("\\"+pgApolloConstants.CONSTANT_STRING_PIPE);
								strProjectType = values[0];
								strProjectInitialName = values[1];
								if(strProjectInitialName.length() < 3) {
									sbReturnMessages.append(pgApolloConstants.STR_MESSAGE_PROVIDE_CHARACTERS);
								}else {
									sbWhereClause.append("attribute[").append(PropertyUtil.getSchemaProperty(context,"attribute_ProjectType")).append("]").append(pgApolloConstants.CONSTANT_STRING_SPACE).append(pgApolloConstants.CONSTANT_STRING_DOUBLE_EQUAL);
									sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE);
									sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_SINGLE_QUOTE);
									sbWhereClause.append(strProjectType);
									sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_SINGLE_QUOTE);
									sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE);
									sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_DOUBLE_AMPERSAND);
									sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE);
									sbWhereClause.append(DomainConstants.SELECT_NAME).append(pgApolloConstants.CONSTANT_STRING_SPACE).append(pgApolloConstants.CONSTANT_STRING_TILDA).append(pgApolloConstants.CONSTANT_STRING_TILDA).append(pgApolloConstants.CONSTANT_STRING_SPACE).append(pgApolloConstants.CONSTANT_STRING_SINGLE_QUOTE);
									sbWhereClause.append(DomainConstants.QUERY_WILDCARD).append(strProjectInitialName).append(DomainConstants.QUERY_WILDCARD);
									sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_SINGLE_QUOTE);
									sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE);
									sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_DOUBLE_AMPERSAND);
									sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE);
									sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_OPEN_ROUND_BRACE);
									sbWhereClause.append(DomainConstants.SELECT_CURRENT);
									sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE);
									sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_DOUBLE_EQUAL);
									sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE);
									sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_SINGLE_QUOTE);
									sbWhereClause.append(DomainConstants.STATE_PROJECT_SPACE_CREATE);
									sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_SINGLE_QUOTE);
									sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE);
									sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_DOUBLE_PIPE);
									sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE);
									sbWhereClause.append(DomainConstants.SELECT_CURRENT);
									sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE);
									sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_DOUBLE_EQUAL);
									sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE);
									sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_SINGLE_QUOTE);
									sbWhereClause.append(DomainConstants.STATE_PROJECT_SPACE_ASSIGN);
									sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_SINGLE_QUOTE);
									sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE);
									sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_DOUBLE_PIPE);
									sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE);
									sbWhereClause.append(DomainConstants.SELECT_CURRENT);
									sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE);
									sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_DOUBLE_EQUAL);
									sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE);
									sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_SINGLE_QUOTE);
									sbWhereClause.append(DomainConstants.STATE_PROJECT_SPACE_ACTIVE);
									sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_SINGLE_QUOTE);

									sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_CLOSE_ROUND_BRACE);
									
									StringList slSelectable = new StringList(DomainConstants.SELECT_NAME);
									slSelectable.add(DomainConstants.SELECT_ID);
									slSelectedPlatformList = getSelectableFromMapList(context,DomainConstants.TYPE_PROJECT_SPACE, DomainConstants.QUERY_WILDCARD, sbWhereClause.toString(), slSelectable,pgApolloConstants.CONSTANT_STRING_PIPE);
									if(null!=slSelectedPlatformList && !slSelectedPlatformList.isEmpty())
									{
										Collections.sort(slSelectedPlatformList);
										int iSize = slSelectedPlatformList.size();
										for (int iCount = 0; iCount < iSize; iCount++) {					
											sbReturnMessages.append(slSelectedPlatformList.get(iCount));
											if(iCount != iSize-1)
												sbReturnMessages.append(pgApolloConstants.CONSTANT_STRING_AMPERSAND);
										}
									}
									else
									{
										sbReturnMessages.append(pgApolloConstants.STR_AUTOMATION_BLANK);
									}
									
								}
							}
						}
						else if(pgApolloConstants.STR_EOFOLDER.equalsIgnoreCase(sAttributeName)) {
							String strProjectId = sInputValue;//Project Name
							StringBuilder sbReturnString = new StringBuilder();
							MapList mlFolderList;
							DomainObject doObj = DomainObject.newInstance(context, strProjectId);
							Map mp;
							int iLevel = 0;
							String strLevel;

							Pattern relPatten = new Pattern(DomainConstants.RELATIONSHIP_PROJECT_VAULTS);
							relPatten.addPattern(DomainConstants.RELATIONSHIP_SUB_VAULTS);
							Pattern typePattern = new Pattern(DomainConstants.TYPE_PROJECT_VAULT);
							
							StringList slSelectable = new StringList(2);
							slSelectable.addElement(DomainConstants.SELECT_ID);
							//Upgrade 22x Fix Starts
							slSelectable.addElement(DomainConstants.SELECT_ATTRIBUTE_TITLE);
							//Upgrade 22x Fix Ends
							
							StringList slRelSelectable = new StringList(DomainRelationship.SELECT_ID);
							
							mlFolderList = doObj.getRelatedObjects(context, 
									relPatten.getPattern(),  // relationshipPattern
									typePattern.getPattern(),// typePattern
									slSelectable,						 // objectSelects
									slRelSelectable,				 // relationshipSelects
									false,								 // getTo
									true,								 // getFrom
									(short)0,							 // recurseToLevel
									DomainConstants.EMPTY_STRING,			 // objectWhere
									DomainConstants.EMPTY_STRING,			 // relationshipWhere
									0);									 // limit
							if(!mlFolderList.isEmpty()) {
								for(int i = 0;i < mlFolderList.size() ; i++) {
									mp = (Map)mlFolderList.get(i);
									strLevel = (String)mp.get(DomainConstants.SELECT_LEVEL);
									iLevel = Integer.parseInt(strLevel);
									sbReturnString.append(generatePrefixForLevel(iLevel));
									//Upgrade 22x Fix Starts
									sbReturnString.append((String)mp.get(DomainConstants.SELECT_ATTRIBUTE_TITLE));
									//Upgrade 22x Fix Ends
									sbReturnString.append(pgApolloConstants.CONSTANT_STRING_PIPE);
									sbReturnString.append((String)mp.get(DomainConstants.SELECT_ID));
									if(i != mlFolderList.size()-1) {
										sbReturnString.append(pgApolloConstants.CONSTANT_STRING_AMPERSAND);
									}
								}
							}
							sbReturnMessages.append(sbReturnString.toString());
						}
						//APOLLO 2018x.6 ALM Requirement - A10-781 Component Family Changes End
					}
				}
			}
		} 
		catch (Exception e)
		{
			loggerApolloTrace.error(e.getMessage(), e);
			return Response.serverError().entity("Error:" + e.toString()).build();
		} 
		finally 
		{
			loggerWS.debug("\n\nRange For CATIA Attribute Ends-----------------");
			loggerWS.debug("\n******************************************************************************");
		}
		return Response.status(200).entity(sbReturnMessages.toString()).build();
	}

	
	/**
	 * Method to fetch Region and Sub region pick list
	 * @param context
	 * @param sAttributeName
	 * @param sInputValue
	 * @return
	 * @throws Exception
	 */
	private StringBuffer getRegionSubRegionPickListValues(matrix.db.Context context, String sAttributeName, String sInputValue) throws Exception
	{
		StringBuffer sbReturnMessages = new StringBuffer();
		
		String strRangeValues;
		if(UIUtil.isNotNullAndNotEmpty(sInputValue))
		{
			if(pgApolloConstants.STR_AUTOMATION_REGION.equalsIgnoreCase(sAttributeName))
			{
				StringList slBusinessAreaList = getSelectableFromMapList(context, pgApolloConstants.TYPE_PG_PLI_BUSINESSAREA, sInputValue, DomainConstants.EMPTY_STRING, DomainConstants.SELECT_ID);

				if(null != slBusinessAreaList && !slBusinessAreaList.isEmpty())
				{
					String sBusinessArea = slBusinessAreaList.get(0);								
					DomainObject domBusinessArea = DomainObject.newInstance(context, sBusinessArea);

					StringList slObjectSelect = new StringList();
					slObjectSelect.add(DomainConstants.SELECT_ID);
					slObjectSelect.add(DomainConstants.SELECT_NAME);


					MapList mlConnectedObjects = domBusinessArea.getRelatedObjects(context,//Context
							pgApolloConstants.RELATIONSHIP_PGPLBUSINESSAREATOLPDREGION, // Relationship Pattern
							pgApolloConstants.TYPE_PGPLILPDREGION, // Type Pattern
							slObjectSelect,//Object Select
							new StringList(),//rel Select
							false,//get To
							true,//get From
							(short)1,//recurse level
							null,//object where Clause
							null,// rel Where clause
							0);// object limit

					if(null != mlConnectedObjects && !mlConnectedObjects.isEmpty())
					{
						StringList slLPDRegionList = new ChangeUtil().getStringListFromMapList(mlConnectedObjects, DomainConstants.SELECT_NAME);
						Collections.sort(slLPDRegionList);
						strRangeValues=StringUtil.join(slLPDRegionList, pgApolloConstants.CONSTANT_STRING_PIPE);
						sbReturnMessages.append(strRangeValues);							
					}

				}
			}
			else if(pgApolloConstants.STR_AUTOMATION_SUBREGION.equalsIgnoreCase(sAttributeName))
			{

				StringList slInputValues = StringUtil.split(sInputValue, pgApolloConstants.CONSTANT_STRING_PIPE);

				sInputValue = StringUtil.join(slInputValues, pgApolloConstants.CONSTANT_STRING_TILDA);

				sInputValue = new StringBuilder(sInputValue).append(DomainConstants.QUERY_WILDCARD).toString();

				StringList slLPDSubRegionList = getSelectableFromMapList(context, pgApolloConstants.TYPE_PGPLILPDSUBREGION, sInputValue, "", DomainConstants.SELECT_ATTRIBUTE_TITLE);

				Set uniqueSubRegionList = new HashSet();
				uniqueSubRegionList.addAll(slLPDSubRegionList);
				slLPDSubRegionList.clear();
				slLPDSubRegionList.addAll(uniqueSubRegionList);
				Collections.sort(slLPDSubRegionList);
				strRangeValues=StringUtil.join(slLPDSubRegionList, pgApolloConstants.CONSTANT_STRING_PIPE);
				sbReturnMessages.append(strRangeValues);
			}

		}
		
		return sbReturnMessages;
	}
	
	/** This method added hyphens as prefix to Folder name according to level value
	 * @param iLevel
	 * @return
	 */
	public String generatePrefixForLevel(int iLevel) {

		StringBuilder sbPrifix = new StringBuilder();
		for(int i =0;i < iLevel-1; i++) {
			sbPrifix.append(pgApolloConstants.CONSTANT_STRING_HYPHEN);
		}
		return sbPrifix.toString();
	}
	
	/** This method is overloaded version of older method to select multiple selectable for find object and concatenate them with seperator
	 * @param context
	 * @param sType
	 * @param sName
	 * @param sWhereCondition
	 * @param slSelectableList
	 * @param strConcatinateWith
	 * @return
	 * @throws Exception
	 */
	public StringList getSelectableFromMapList(matrix.db.Context context, String sType, String sName, String sWhereCondition, StringList slSelectableList,String strConcatinateWith) throws MatrixException{
		StringList slReturnList = null;
		StringList slSelectable = null;
		StringBuilder sbValue  = new StringBuilder();
		MapList mapList = null;
		try 
		{
			slReturnList = new StringList();
			slSelectable = new StringList();
			slSelectable.addAll(slSelectableList);
			mapList = DomainObject.findObjects(context,//Context
					sType,//Object Type
					sName,//Object Name
					DomainConstants.QUERY_WILDCARD,//Object Revision
					DomainConstants.QUERY_WILDCARD,//Owner
					DomainConstants.QUERY_WILDCARD,//Vault
					sWhereCondition,//Where Clause
					true,//Expand type
					slSelectable);//Object selects
			mapList.sort(DomainConstants.SELECT_NAME, "ascending", "string");
			Map mapObject = null;
			int iSize = mapList.size();
			for (int iCount = 0; iCount < iSize; iCount++) {
				sbValue = new StringBuilder();
				mapObject = (Map)mapList.get(iCount);
				for(int j = 0; j < slSelectableList.size(); j++) {
					sbValue.append((String)mapObject.get(slSelectableList.get(j)));
					if(j != slSelectableList.size() - 1) {
						sbValue.append(strConcatinateWith);
					}
				}
				slReturnList.add(sbValue.toString());
			}
		} 
		catch (Exception ex) 
		{
			loggerApolloTrace.error(ex.getMessage(), ex);
		}
		return slReturnList;
	}
	
	
	/**
	 * This method is used to get selected StringList from MapList.
	 * @param context
	 * @param sType
	 * @param sName
	 * @param sWhereCondition
	 * @param sSelectable
	 * @return
	 * @throws Exception
	 */
	public StringList getSelectableFromMapList(matrix.db.Context context, String sType, String sName, String sWhereCondition, String sSelectable) throws Exception{
		StringList slReturnList = null;
		StringList slSelectable = null;
		MapList mapList = null;
		try {
			slReturnList = new StringList();
			slSelectable = new StringList(1);
			slSelectable.add(sSelectable);
			mapList = DomainObject.findObjects(context,sType,sName,DomainConstants.QUERY_WILDCARD,DomainConstants.QUERY_WILDCARD,DomainConstants.QUERY_WILDCARD,sWhereCondition,true,slSelectable);
			//APOLLO 2018x.5 A10-495 Values of BA, PCP, PTP, PTC, Product Size etc. should be sorted Starts
			mapList.sort(DomainConstants.SELECT_NAME, "ascending", "string");
			//APOLLO 2018x.5 A10-495 Values of BA, PCP, PTP, PTC, Product Size etc. should be sorted Ends
			Map mapObject = null;
			int iSize = mapList.size();
			for (int iCount = 0; iCount < iSize; iCount++) {
				mapObject = (Map)mapList.get(iCount);
				slReturnList.add((String)mapObject.get(sSelectable));
			}
		} catch (Exception ex) {
			throw ex;
		}
		return slReturnList;
	}
	
	/**
	 * This method is used to get corresponding ObjectId of sInputValue.
	 * @param context
	 * @param sType
	 * @param sInputValue
	 * @param sWhereCondition
	 * @return
	 * @throws Exception
	 */
	public StringList getSelectedPlatformList(matrix.db.Context context, String sType, String sInputValue, String sWhereCondition) throws Exception{
		StringList slSelectedPlatformList = new StringList();
		StringList slObjectIds = null;
		StringList slInputValue = null;
		try {
			if(UIUtil.isNotNullAndNotEmpty(sInputValue)) {
				slInputValue = FrameworkUtil.split(sInputValue, ",");
				int iSize = slInputValue.size();
				for (int iCount = 0; iCount < iSize; iCount++) {
					slObjectIds = getSelectableFromMapList(context, sType, slInputValue.get(iCount), sWhereCondition, DomainConstants.SELECT_ID);
					if (!slObjectIds.isEmpty() && UIUtil.isNotNullAndNotEmpty(slObjectIds.get(0))) {
						slSelectedPlatformList.add(slObjectIds.get(0));
					}
				}
			}
		} catch (Exception ex) {
			throw ex;
		}
		return slSelectedPlatformList;
	}
	
	/**
	 * This method is used to get range values of Platform and Chassis.
	 * @param context
	 * @param strfieldValueList
	 * @param methodargs
	 * @param strType
	 * @return
	 * @throws Exception
	 */
	public String getProductFormPlatform(matrix.db.Context context, StringList strfieldValueList, String[] methodargs, String strType) throws Exception{
		StringBuffer sbResponse = new StringBuffer();
		try {
			StringList fieldDisplay = null;
			Map hConnectedValues = new HashMap();
			if ("Platform".equalsIgnoreCase(strType)) {
				hConnectedValues = (HashMap) JPO.invoke(context, "emxCommonDocument", null, "getConnectedPlatform",methodargs,Map.class);
			} else if ("Chassis".equalsIgnoreCase(strType)) {
				hConnectedValues = (HashMap) JPO.invoke(context, "emxCommonDocument", null, "getConnectedChassisToPlatform",methodargs,Map.class);
     		//2018x.2 ALM 27912 - Requirement 29039 - Product Size on APP - start
			} else if (pgApolloConstants.STR_PRODUCT_SIZE.equalsIgnoreCase(strType)) {
				hConnectedValues = (HashMap) JPO.invoke(context, "emxCommonDocument", null, "getConnectedProductSizeToChassis",methodargs,Map.class);				
			//2018x.2 ALM 27912 - Requirement 29039 - Product Size on APP - end
			} else if (pgApolloConstants.STR_AUTOMATION_REGION.equalsIgnoreCase(strType)) {
				hConnectedValues = (HashMap) JPO.invoke(context, "emxCommonDocument", null, "getConnectedBusinessAreaToRegion",methodargs,Map.class);				
			} else if (pgApolloConstants.STR_AUTOMATION_SUBREGION.equalsIgnoreCase(strType)) {
				hConnectedValues = (HashMap) JPO.invoke(context, "emxCommonDocument", null, "getApplicableSubRegionsBasedOnBusinessAreaAndRegion",methodargs,Map.class);				
			} else {
				hConnectedValues = (HashMap) JPO.invoke(context, "emxCommonDocument", null, "getConnectedPlatformToBusinessArea",methodargs, Map.class);
			}
			fieldDisplay = (StringList) hConnectedValues.get("field_display_choices");
			if(fieldDisplay != null) {				
				int iSize = fieldDisplay.size();
				for (int iCount = 0; iCount < iSize; iCount++) {
					sbResponse.append(fieldDisplay.get(iCount));
					if(iCount != iSize-1)
						sbResponse.append("|");
				}
			}
		} catch (Exception ex) {
			throw ex;
		}
		return sbResponse.toString();
	}
}
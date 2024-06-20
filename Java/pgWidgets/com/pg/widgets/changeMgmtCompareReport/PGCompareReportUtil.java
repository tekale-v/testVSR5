/*
 * PGCompareReportUtil.java
 * 
 * Added by DSM Team
 * For Change Management Compare Report Widget related Web Service
 * 
 */

package com.pg.widgets.changeMgmtCompareReport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import com.matrixone.apps.awl.util.BusinessUtil;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MailUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIComponent;
import com.matrixone.apps.framework.ui.UIMenu;
import com.matrixone.apps.framework.ui.UITable;
import com.matrixone.apps.framework.ui.UITableCommon;
import com.matrixone.apps.framework.ui.UITableCustom;
import com.matrixone.apps.framework.ui.UIUtil;

import matrix.db.BusinessObjectWithSelectList;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.db.RelationshipWithSelect;
import matrix.db.RelationshipWithSelectList;
import matrix.util.MatrixException;
import matrix.util.StringList;
/**
 * Class PGCompareReportUtil has all the methods defined for Change Management compare report widget activities.
 * 

 *
 */
public class PGCompareReportUtil 
{		
	static final Logger logger = Logger.getLogger(PGCompareReportUtil.class.getName());

	//STRING CONSTANTS
	static final String EXCEPTION_MESSAGE  = "Exception in PGCompareReportUtil";
	private static final String STATUS_SUCCESS = "success";
	private static final String STATUS_ERROR = "error";
	static final String STRING_MESSAGE = "message";
	static final String STATUS_INFO = "info";
	static final String STRING_STATUS = "status";
	static final String STRING_KO = "KO";
	static final String STRING_DATA = "data";
	static final String STRING_OK = "OK";	
	public static final String STRING_ID = "id";
	public static final String STRING_OBJECTID = "objectId";
	public static final String STRING_REQUESTMAP = "requestMap";

	/**
	 * This method returns the Dashboard URL for the widget having the input name
	 * @param context
	 * @param strAppDisplayName - Display name of the Widget
	 * @return
	 * @throws Exception
	 */
	public String getDashboardAppURL(matrix.db.Context context, String strAppDisplayName) throws Exception
	{
		String strBaseURL = MailUtil.getBaseURL(context);
		if(UIUtil.isNullOrEmpty(strBaseURL))
		{
			strBaseURL = EnoviaResourceBundle.getProperty(context,"emxCPN.BaseURL");
		}
		StringBuilder sbURL = new StringBuilder(strBaseURL);
		if (BusinessUtil.isNotNullOrEmpty(strBaseURL)) {
			String[] arrURL = strBaseURL.split(".pg.com");
			String strWhere = new StringBuilder(DomainObject.getAttributeSelect("App Display Name")).append("=='").append(strAppDisplayName).append("'").toString();
			StringList slSelect = new StringList(DomainConstants.SELECT_NAME);

			MapList mlObjs = DomainObject.findObjects(context,	// eMatrix context
					"AppDefinition",							// type pattern
					DomainConstants.QUERY_WILDCARD,             // name pattern    
					DomainConstants.QUERY_WILDCARD,             // revision pattern
					DomainConstants.QUERY_WILDCARD,             // owner pattern   
					"eService Production",                  // Vault Pattern	
					strWhere,               						// where expression
					true,                                       // expand type     
					slSelect);									// object selects

			String strAppName = "";
			if(BusinessUtil.isNotNullOrEmpty(mlObjs))
			{
				strAppName = (String) ((Map) mlObjs.get(0)).get(DomainConstants.SELECT_NAME);
			}

			sbURL = new StringBuilder(arrURL[0]).append("-3dd.pg.com/3ddashboard").append("/#app:").append(strAppName);
		}
		return sbURL.toString();
	}
	/**
	 * This method returns Table data for each of the Compare Type values
	 * @param context
	 * @param mpRequestMap
	 * @return
	 */
	public Response getTableSchemaData(matrix.db.Context context, Map<String,Object> mpRequestMap){
		String strTableName = (String) mpRequestMap.get("tables");
		String[] tableNames = strTableName.split(",");
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		JsonObjectBuilder jsonTableDetails = Json.createObjectBuilder();
		JsonArrayBuilder jsonArray = null;
		String strEnoviaTable=null;
		MapList mlColumnDetails = null;
		Vector<String> assignments = new Vector<String>(1);
		assignments.add("all");
		try{
			for(String strTable: tableNames){
				strTable =strTable.replace(' ','_');
				jsonArray = Json.createArrayBuilder();
				strEnoviaTable = EnoviaResourceBundle.getProperty(context,"emxCPN.pgChangeActionWidget.CompareReport.Table."+strTable);
				mlColumnDetails = UITableCustom.getColumns(context, strEnoviaTable, assignments);
				PGCompareReportModeler.mapList2JsonArray(jsonArray, mlColumnDetails);
				jsonTableDetails.add(strTable,jsonArray );
			}
			jsonReturnObj.add("data", jsonTableDetails);
			jsonReturnObj.add(STRING_MESSAGE, STRING_OK);
		}catch (Exception e) {
			jsonReturnObj.add(STRING_STATUS, STATUS_ERROR);
			jsonReturnObj.add(STRING_MESSAGE, e.getMessage());
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		}
		return Response.status(HttpServletResponse.SC_OK).entity(jsonReturnObj.build().toString()).build(); 		
	}
	/**
	 * This method returns objects info for the selectables passed
	 * @param context
	 * @param mpRequestMap
	 * @return
	 * @throws Exception
	 */
	public Response getObjectDetails(matrix.db.Context context, Map<String,Object> mpRequestMap) throws Exception 
	{
		String strObjectId = (String) mpRequestMap.get("objectIds");
		String strSelect = (String) mpRequestMap.get("ObjSelectables");
		StringList slObjSelects = new StringList();
		JsonArrayBuilder jsonArray = Json.createArrayBuilder();
		if(UIUtil.isNotNullAndNotEmpty(strSelect))
		{
			slObjSelects = StringUtil.split(strSelect, ",");
		}
		JsonObjectBuilder jsonClaimInfoReturn = Json.createObjectBuilder();
		StringList slObjId = StringUtil.split(strObjectId, ",");
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		try {
			if(BusinessUtil.isNotNullOrEmpty(slObjId))
			{
				MapList mlDetails = DomainObject.getInfo(context, slObjId.toArray(new String []{}), slObjSelects);

				if(BusinessUtil.isNotNullOrEmpty(mlDetails))
				{
					//Checking if objects have previous revision available for comparison
					MapList mlDetailsWithCompareResult = getCompareReportCriteria(context, mlDetails);
					PGCompareReportModeler.mapList2JsonArray(jsonArray, mlDetailsWithCompareResult);
					jsonClaimInfoReturn.add(strObjectId, jsonArray);
				}
			}
			jsonReturnObj.add("data", jsonArray);
			jsonReturnObj.add(STRING_MESSAGE, STRING_OK);
		} catch (FrameworkException e) {
			jsonReturnObj.add(STRING_STATUS, STATUS_ERROR);
			jsonReturnObj.add(STRING_MESSAGE, e.getMessage());
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		}
		return Response.status(HttpServletResponse.SC_OK).entity(jsonReturnObj.build().toString()).build(); 
	}
	/** 
	 * Method will check if object can be compared with previous revision and based on result, add flag and message to return MapList
	 * @param context
	 * @param mlDetails
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	private MapList getCompareReportCriteria(Context context, MapList mlDetails) throws Exception {
		MapList mlReturn = new MapList();
		try {
			Map<String, Object> mapParam = new HashMap<>();
			mapParam.put("objectList", mlDetails);
			mapParam.put("ReportType", "details");
			StringList slAttributeCompare = JPO.invoke(context, "pgComparePartRevisionUtils", null, "getCompareReportIcon", JPO.packArgs(mapParam), StringList.class);
			int iSize = mlDetails.size();
			HashMap hmMap = null;
			String strListElement;
			boolean bFlag = true;
			String strMessage;
			Map<String, Object> programMap;
			Map<String, String> tempProgramMap;
			HashMap<String, Object> returnMap ;
			String strObjectId;
			String strPrevObjId;
			String strCompareWith;
			String strObjectType;
			for(int i=0; i<iSize; i++)
			{
				hmMap = (HashMap) mlDetails.get(i);
				strListElement = slAttributeCompare.get(i);
				if(strListElement.contains("javascript:alert(")) {
					bFlag = false;
					strMessage = StringUtils.substringBetween(strListElement, "<a onclick=\"javascript:alert('", "')\">");
				}
				else
				{
					bFlag = true;
					strMessage = DomainConstants.EMPTY_STRING;
				}
				hmMap.put("bFlag", bFlag);
				hmMap.put("CompareMessage", strMessage);
				if(bFlag)
				{
					programMap = new HashMap<String, Object>();
					tempProgramMap = new HashMap<String, String>();
					strObjectId = (String) hmMap.get(DomainConstants.SELECT_ID);
					tempProgramMap.put(STRING_OBJECTID, strObjectId);
					programMap.put(STRING_REQUESTMAP, tempProgramMap);
					returnMap    = (HashMap) JPO.invoke(context, "pgComparePartRevisionUtils", null, "getTypeRanges", JPO.packArgs(programMap), HashMap.class);
					StringList slFieldChoices = (StringList) returnMap.get("field_display_choices");
					strObjectType = DomainObject.newInstance(context, strObjectId).getInfo(context, DomainConstants.SELECT_TYPE);
					
					slFieldChoices.addAll(addNewTabsToExistingTabs(context, strObjectId));
					if("Formulation Process".equals(strObjectType))
					{
						 int indexBOM = slFieldChoices.indexOf("BOM");
						 if(indexBOM >= 0)
						 {
							 slFieldChoices.set(indexBOM, "FBOM");
						 }
						 int indexSubstitutes = slFieldChoices.indexOf("Substitutes");
						 if(indexSubstitutes >= 0)
						 {
							 slFieldChoices.set(indexSubstitutes, "FBOM Substitutes");
						 }
					}
					hmMap.put("TabList", slFieldChoices);
					
					strPrevObjId = (String) hmMap.get("previous.id");
					if(UIUtil.isNullOrEmpty(strPrevObjId))
					{
						strPrevObjId = JPO.invoke(context, "pgComparePartRevisionUtils", null, "getPrevOrDerivedObjectId", JPO.packArgs(tempProgramMap), String.class);
						strCompareWith = "Base Part";
					}
					else
					{
						strCompareWith = "Previous revision";
					}
					hmMap.put("previous.id", strPrevObjId);
					hmMap.put("comparedWith", strCompareWith);
				}
				mlReturn.add(hmMap);
			}
		} 
		catch (FrameworkException e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		}	
		return mlReturn;
	}
	/** 
	 * Method will check if object can be compared with previous revision and based on result, add flag and message to return MapList
	 * @param context
	 * @param mlDetails
	 * @return
	 * @throws Exception 
	 */
	private StringList addNewTabsToExistingTabs(Context context, String strObjectId) throws Exception {
		StringList slReturn = new StringList();
		try {
			boolean bAccessExpressionVal = true;
			boolean bAccessProgramVal = true;
			String sTokenTemp;
			String strCommand;
			String sTokenName;
			String sTypeOptions  = EnoviaResourceBundle.getProperty(context, "emxCPN", context.getLocale(),"emxCPN.pgChangeActionWidget.CompareReport.TabCommandMapping");
			String[] arrTypeOption = sTypeOptions.split(",");
			Map<String, Object> programMap;
			for(int i=0 ; i < arrTypeOption.length ; i++)   
			{
				programMap = new HashMap<String, Object>();
				sTokenTemp = arrTypeOption[i];
				String[] arrTypeCommand = sTokenTemp.split(":");
				sTokenName = arrTypeCommand[0];
				strCommand = arrTypeCommand[1];
				String[] arrCommands = strCommand.split("\\|");
				for(int j=0 ; j < arrCommands.length ; j++)   
				{
					Map<String, Object> mCommandMap = UIMenu.getCommand(context, arrCommands[j]);
					Map<String, String> mCommandSettingMap = (Map)mCommandMap.get("settings");
					String strCommandAccessExpr = mCommandSettingMap.get("Access Expression");
					String strCommandAccessProgram = mCommandSettingMap.get("Access Program");
					String strCommandAccessFunction = mCommandSettingMap.get("Access Function");
					if(UIUtil.isNotNullAndNotEmpty(strCommandAccessExpr))
					{
						programMap.put(STRING_OBJECTID, strObjectId);
						programMap.put("Access Expression", strCommandAccessExpr);
						bAccessExpressionVal    = JPO.invoke(context, "pgComparePartRevisionUtils", null, "evaluateAccessExpressionCall", JPO.packArgs(programMap), Boolean.class);
						if(!bAccessExpressionVal)
						{
							break;
						}
					}
					if(UIUtil.isNotNullAndNotEmpty(strCommandAccessProgram) && UIUtil.isNotNullAndNotEmpty(strCommandAccessFunction))
					{
						Map programArgs = new HashMap();
						programArgs.put(STRING_OBJECTID, strObjectId);
						boolean bHasAccess = JPO.invoke(context,strCommandAccessProgram, null,strCommandAccessFunction,JPO.packArgs(programArgs), Boolean.class);
						if(!bHasAccess){
							bAccessProgramVal = false;
							break;
						}
					}
				}
				
			if(bAccessExpressionVal && bAccessProgramVal){
					slReturn.add(sTokenName);
				}
			}
		} 
		catch (FrameworkException e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		}	
		return slReturn;
	}
	/*
	 * This method returns Attribute comparison between 2 objects
	 * @param context
	 * @param mpRequestMap - Should have objectId, objectId2
	 * @return
	 * @throws Exception 
	 */
	public Response getAttributeCompareReport(matrix.db.Context context, Map<String,Object> mpRequestMap ) throws Exception 
	{
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		//Pass following parameters in the map: objectId, objectId2, compareType
		String objIds = (String) mpRequestMap.get("objectId");

		JsonArrayBuilder jsonArray = Json.createArrayBuilder();
		JsonObjectBuilder jsonCompareDetailsReturn = Json.createObjectBuilder();

		try {
			String[] strArrObjIds = objIds.split(",");

			for (String strObjectId: strArrObjIds)
			{ 
				mpRequestMap.put("objectId", strObjectId);
				MapList mlAttributeCompare = JPO.invoke(context, "pgComparePartRevisionUtils", null, "getFormFieldValuesToCompare", JPO.packArgs(mpRequestMap), MapList.class);
				if(BusinessUtil.isNotNullOrEmpty(mlAttributeCompare))
				{
					PGCompareReportModeler.mapList2JsonArray(jsonArray, mlAttributeCompare);
					jsonCompareDetailsReturn.add(strObjectId, jsonArray);
				}
			}
			jsonReturnObj.add("data", jsonCompareDetailsReturn);
			jsonReturnObj.add(STRING_MESSAGE, STRING_OK);
		} catch (FrameworkException e) {
			jsonReturnObj.add(STRING_STATUS, STATUS_ERROR);
			jsonReturnObj.add(STRING_MESSAGE, e.getMessage());
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		}
		return Response.status(HttpServletResponse.SC_OK).entity(jsonReturnObj.build().toString()).build(); 
	}
	/**
	 * This method returns the RelName and Type for the input Compare Type
	 * @param strReportType
	 * @return
	 */
	private Map<String,String> getRelName(String strReportType) {
		Map<String,String> mpData = new HashMap<String, String>();
		if("BOM".equalsIgnoreCase(strReportType))
		{
			mpData.put("RelName", "EBOM");
		}
		else if("Plants".equalsIgnoreCase(strReportType))
		{
			mpData.put("RelName", "Manufacturing Responsibility");
		}
		else if("Performance Characteristics".equalsIgnoreCase(strReportType))
		{
			mpData.put("RelName", "Extended Data");
		}
		else if("Specifications".equalsIgnoreCase(strReportType))
		{
			mpData.put("RelName", "Part Specification");
		}
		else if("Reference Documents".equalsIgnoreCase(strReportType))
		{
			mpData.put("RelName", "Reference Document");
		}
		else if("Substitutues".equalsIgnoreCase(strReportType))
		{
			mpData.put("RelName", "EBOM Substitute");
		}
		else if("Alternates".equalsIgnoreCase(strReportType))
		{
			mpData.put("RelName", "Alternate");
		}
		else if("Components Equivalent".equalsIgnoreCase(strReportType))
		{
			mpData.put("RelName", "Manufacturer Equivalent");
		}
		else if("Component Materials".equalsIgnoreCase(strReportType))
		{
			mpData.put("RelName", "Component Material");
		}
		else if("IP Security".equalsIgnoreCase(strReportType))
		{
			mpData.put("RelName", "Protected Item");
		}
		else if("Files".equalsIgnoreCase(strReportType))
		{
			mpData.put("RelName", "Files");
		}
		else if("Market Clearance".equalsIgnoreCase(strReportType))
		{
			mpData.put("RelName", "pgProductCountryClearance");
		}
		else if("GPS Task".equalsIgnoreCase(strReportType))
		{
			mpData.put("RelName", "pgGPSAssessmentTaskInputs");
		}
		return mpData;
	}
	/**This method return expanded data using relationship with object select
	 * @param context
	 * @param mpRequestMap
	 * @return
	 * @throws MatrixException 
	 */
	public Response getCompareReportData(Context context, Map<String, Object> mpRequestMap) throws Exception 
	{
		// Start time
		long startTime = System.currentTimeMillis();
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		JsonArrayBuilder jsonArrayColumn = Json.createArrayBuilder();
		JsonArrayBuilder jsonArrayCurrent = Json.createArrayBuilder();
		JsonArrayBuilder jsonArrayPrev = Json.createArrayBuilder();
		JsonObjectBuilder jsonObjCompareDetails = Json.createObjectBuilder();
		JsonObjectBuilder jsonCompareDetailsReturn = Json.createObjectBuilder(); 
		//Pass below parameters in the map
		String objIds = (String) mpRequestMap.get("objectId");
		String compareType = (String) mpRequestMap.get("compareType");
		String strPrevRevId = (String) mpRequestMap.get("objectId2");
		ArrayList compareTable = (ArrayList) mpRequestMap.get("table");

		Map<String,String> mpRelDetails = getRelName(compareType);

		try 
		{
			compareType = compareType.replace(' ','_');
			String strEnoviaTable = EnoviaResourceBundle.getProperty(context,"emxCPN.pgChangeActionWidget.CompareReport.Table."+compareType);
			String[] strArrObjIds = objIds.split(",");

			for (String strObjectId: strArrObjIds)
			{ 
				mpRequestMap.put("objectId", strObjectId);
				//Get Table name for Compare Type
				MapList mlColumnDetails = null;
				if(UIUtil.isNullOrEmpty(strPrevRevId))
				{
					strPrevRevId = JPO.invoke(context, "pgComparePartRevisionUtils", null, "getPrevOrDerivedObjectId", JPO.packArgs(mpRequestMap), String.class);
				}

				if(null==compareTable)
				{
					//Get columns from table
					Vector<String> assignments = new Vector<String>(1);
					assignments.add("all");
					mlColumnDetails = UITable.getColumns(context, strEnoviaTable, assignments);		
				}
				else
				{
					mlColumnDetails = convertArrayListToMapList(compareTable) ;
				}

				//Get columns to be displayed based on the objectId
				HashMap<String, String> requestMap = new HashMap<String, String>();
				requestMap.put("languageStr", "en");
				requestMap.put("objectId", strObjectId);
				requestMap.put("parentOID", strObjectId);
				UITable table   = new UITable();
				MapList mlColumnProcess = table.processColumns(context, new HashMap(), mlColumnDetails, requestMap);

				MapList mlCurrent = new MapList();
				MapList mlPrevious = new MapList();
			
				//Get expand program for the given Compare Type
				String strExpandProgram = EnoviaResourceBundle.getProperty(context,"emxCPN.pgChangeActionWidget.CompareReport.ExpandProgram."+compareType);
				StringList expandProgram = StringUtil.split(strExpandProgram, ":");
				String strProgram = expandProgram.get(0);
				String strMethod = expandProgram.get(1);
				//Invoke Expand program with the required arguments
				mpRequestMap.put("objectId", strObjectId);
				mpRequestMap.put("strExpandLevel", "0");
				mpRequestMap.put("IsStructureCompare", "TRUE");
				mpRequestMap.put("parentOID", strObjectId);
				mpRequestMap.put("emxExpandFilter",  "0");
				mpRequestMap.put("relName", mpRelDetails.get("RelName"));
				mpRequestMap.put("relation", mpRelDetails.get("RelName"));
				mpRequestMap.put("expandLevel", "0");
				//Invoke expand program for current object
				mlCurrent = JPO.invoke(context, strProgram, null, strMethod, JPO.packArgs(mpRequestMap), MapList.class);
				//Invoke expand program for previous revision object
				mpRequestMap.put("objectId", strPrevRevId);
				mpRequestMap.put("parentOID", strPrevRevId);
				mlPrevious = JPO.invoke(context, strProgram, null, strMethod, JPO.packArgs(mpRequestMap), MapList.class);
				if("BOM".equals(compareType) || "Component_Materials".equals(compareType)) 
				{
					Map<String, String> mpObj1Data = new HashMap();
					mpObj1Data.put(DomainConstants.SELECT_ID, strObjectId);
					mpObj1Data.put(DomainConstants.SELECT_LEVEL, "0");

					Map<String, String> mpObj2Data = new HashMap();
					mpObj2Data.put(DomainConstants.SELECT_ID, strPrevRevId);
					mpObj2Data.put(DomainConstants.SELECT_LEVEL, "0");

					mlCurrent.add(0, mpObj1Data);
					mlPrevious.add(0, mpObj2Data);
				}
				
				MapList mlColDef = getColumnDef(context,mlColumnProcess);
				HashMap<String, String> hmRequestMap = new HashMap<String, String>();
				hmRequestMap.put("languageStr", "en");
				//DSM (DS) 2022x-03 - Defect 53720 - Same data in Perf Chars but shows difference - START
				hmRequestMap.put("IsStructureCompare", "TRUE");
				//DSM (DS) 2022x-03 - Defect 53720 - Same data in Perf Chars but shows difference - END
				hmRequestMap.put("table", strEnoviaTable);
				hmRequestMap.put("objectId",strObjectId);
				hmRequestMap.put("parentOID", strObjectId);
				HashMap<String, HashMap<String, String>> hmTableData = new HashMap<String, HashMap<String, String>>();
				hmTableData.put("RequestMap",hmRequestMap);
				//Adding parent Id in the MapList
				addParentIdToMap(compareType, mlCurrent);
				addParentIdToMap(compareType, mlPrevious);
				//Get column values for current revision
				mlCurrent = getColumnValues(context, mlColumnProcess, mlCurrent, hmTableData);
				hmRequestMap.put("objectId",strPrevRevId);
				hmRequestMap.put("parentOID", strPrevRevId);
				hmTableData.put("RequestMap",hmRequestMap);
				//Get column values for previous revision
				mlPrevious = getColumnValues(context, mlColumnProcess, mlPrevious, hmTableData);
				PGCompareReportModeler.mapList2JsonArray(jsonArrayColumn, mlColDef);
				jsonObjCompareDetails.add("column", jsonArrayColumn);
				PGCompareReportModeler.mapList2JsonArray(jsonArrayCurrent, mlCurrent);
				jsonObjCompareDetails.add("current", jsonArrayCurrent);
				PGCompareReportModeler.mapList2JsonArray(jsonArrayPrev, mlPrevious);
				jsonObjCompareDetails.add("previous", jsonArrayPrev);
				jsonCompareDetailsReturn.add(strObjectId, jsonObjCompareDetails);
			}
			jsonReturnObj.add("data", jsonCompareDetailsReturn);
			jsonReturnObj.add(STRING_STATUS, STATUS_SUCCESS);
		} catch (FrameworkException e) {
			jsonReturnObj.add(STRING_STATUS, STATUS_ERROR);
			jsonReturnObj.add(STRING_MESSAGE, e.getMessage());
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		}
		// End time
		long endTime = System.currentTimeMillis();
		// Calculate execution time
		long executionTime = endTime - startTime;
		// Print execution time
		String strInfoTimeMsg = new StringBuilder("Execution time for getCompareReportData for compareType : ").append(compareType).append(" in milliseconds is :").append(executionTime).toString();
		logger.log(Level.INFO, strInfoTimeMsg);
		return Response.status(HttpServletResponse.SC_OK).entity(jsonReturnObj.build().toString()).build(); 
	}
	/**
	 * This method returns the names of the final columns to be displayed
	 * @param context
	 * @param mlColumnProcess
	 * @return
	 */
	private MapList getColumnDef(Context context, MapList mlColumnProcess)
	{
		MapList mlFinalColDef  = new MapList();
		try {

			MapList mlColumnDef = new MapList();
			HashMap hmColumnMap = null;
			String strColumnLabel = null;
			String strColumnName = null;
			HashMap mpColMap = null;
			
			String strColumnsToSkip = EnoviaResourceBundle.getProperty(context,"emxCPN.pgChangeActionWidget.CompareReport.TableColumnsToSkip");
			StringList slColumnsToSkip = StringUtil.split(strColumnsToSkip,",");
			MapList mlTemp = new MapList();
			String strRegSuite = null;
			String strGroupLabel = null;	
			MapList mlChild = null;
			Map mpChild = null;
			Map mpSettingMap = null;
			boolean hasGroupColumn = false;
			
			for (int k = 0; k < mlColumnProcess.size(); k++)
			{
				hmColumnMap    = (HashMap)mlColumnProcess.get(k);
				strColumnLabel = UITable.getLabel(hmColumnMap);
				strColumnName = UITable.getName(hmColumnMap);
				
				mpSettingMap = UIComponent.getSettings(hmColumnMap);
				strGroupLabel = (String) mpSettingMap.get("Group Header");

				if(!(slColumnsToSkip.contains(strColumnLabel) || strColumnLabel.startsWith("<img")))
				{
					mpColMap = new HashMap();							
					if(UIUtil.isNotNullAndNotEmpty(strGroupLabel) )
					{
						strRegSuite = (String) mpSettingMap.get("Registered Suite");
						if(UIUtil.isNotNullAndNotEmpty(strRegSuite))
						{
							strGroupLabel = EnoviaResourceBundle.getProperty(context, "emx"+strRegSuite+"StringResource", context.getLocale(), strGroupLabel);		
						}					
						mpColMap.put("headerName", strGroupLabel);					
						mlChild = new MapList();
						mpChild = new HashMap();
						mpChild.put("headerName", strColumnLabel);
						mpChild.put("field", strGroupLabel+"_"+strColumnName);
						mpChild.put("editable", false);
						mlChild.add(mpChild);
						mpColMap.put("children", mlChild);
						hasGroupColumn= true;
					}
					else
					{
						mpColMap.put("headerName", strColumnLabel);	
						mpColMap.put("field", strColumnName);
						mpColMap.put("editable", false);
					}
					mlColumnDef.add(mpColMap);
					mlTemp.add(hmColumnMap);
				}
			}
			mlColumnProcess = mlTemp;

			//Arrange coldef according to group
			Map mpTemp = null;
			MapList mlChildTemp = null;
			String groupHeader = null;
			StringList slAddedGroupData = new StringList();
			if(hasGroupColumn)
			{
				for(int i=0;i<mlColumnDef.size();i++)
				{
					mpColMap = (HashMap) mlColumnDef.get(i);
					groupHeader = (String) mpColMap.get("headerName");
					if(mpColMap.containsKey("children") && !slAddedGroupData.contains(groupHeader))
					{					
						mlChildTemp = (MapList) mpColMap.get("children");
						for(int j=i+1;j<mlColumnDef.size();j++)
						{
							mpTemp = (HashMap) mlColumnDef.get(j);
							if(groupHeader.equals(mpTemp.get("headerName")) && mpTemp.containsKey("children"))
							{
								mlChildTemp.addAll((MapList)mpTemp.get("children"));
							}
						}
						slAddedGroupData.add(groupHeader);
						mlFinalColDef.add(mpColMap);
					}
					else if(!mpColMap.containsKey("children"))
					{
						mlFinalColDef.add(mpColMap);
					}
				}
			}
			else
			{
				mlFinalColDef=mlColumnDef;
			}
		} 
		catch (Exception e) 
		{
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		}
		return mlFinalColDef;
	}
	/**
	 * This method adds Parent Id details in the expand program MapList
	 * Added to fix Sequence number issue in FBOM view
	 * @param strCompareType
	 * @param mlObjectList
	 * @throws Exception
	 */
	private void addParentIdToMap(String strCompareType, MapList mlObjectList) throws Exception
	{
		if("FBOM".equals(strCompareType))
		{
			Map mpObj = null;
			for(Object objDetails :mlObjectList)
			{
				mpObj = (Map) objDetails;
				mpObj.put("id[parent]", mpObj.get("from.id"));
			}
		}
	}
	/**
	 * This method gets the values for each processed columns based on the IdList got from the expand Program
	 * @param context
	 * @param processColumns
	 * @param objectIdList
	 * @param hmTableData
	 * @return
	 * @throws FrameworkException
	 */
	private MapList getColumnValues(Context context,MapList processColumns, MapList objectIdList, HashMap<String, HashMap<String, String>> hmTableData) throws FrameworkException
	{
		// Start time
		long startTime = System.currentTimeMillis();
		try{
			UITableCommon uiTable = new UITableCommon();
			HashMap hmColumnValuesMap           = uiTable.getColumnValuesMap(context, processColumns, objectIdList, hmTableData, false);

			BusinessObjectWithSelectList bwsl   = (BusinessObjectWithSelectList)hmColumnValuesMap.get("Businessobject");
			RelationshipWithSelectList rwsl     = (RelationshipWithSelectList)hmColumnValuesMap.get("Relationship");
			Vector[] programResult              = (Vector[])hmColumnValuesMap.get("Program");

			int iColumnSize                     = processColumns.size();
			int iObjectListSize                 = objectIdList.size();
			String strPropkey;
			String strNLSType;
			MapList mlFinalList     = new MapList();
			HashMap<String, Object> hmFinalMap  = null;
			//DSM (DS) 2022x-03 - Defect 53642 - Compare page pop up error message when click specifications part - START
			MapList mlObjListAccess = new MapList();
			Map mpObj = null;
			String strAccess = null;
			int y = 0;
			for(int x = 0; x < iObjectListSize; x++)
			{
				mpObj = (Map) objectIdList.get(x);
				strAccess = (String) mpObj.get("objReadAccess");
				if("TRUE".equalsIgnoreCase(strAccess))
				{
					mpObj.put("accessCounter", y++);
				}
				mlObjListAccess.add(x, mpObj);
			}
			//DSM (DS) 2022x-03 - Defect 53642 - Compare page pop up error message when click specifications part - END
			for (int i = 0; i < iObjectListSize; i++)
			{
				hmFinalMap  = new HashMap<String, Object>();
				//DSM (DS) 2022x-03 - Defect 53642 - Compare page pop up error message when click specifications part - START
				getRowColumnValues(context, processColumns, mlObjListAccess, bwsl, rwsl, programResult, iColumnSize, hmFinalMap, i);
				//DSM (DS) 2022x-03 - Defect 53642 - Compare page pop up error message when click specifications part - END

				Map objectMap  = (Map)objectIdList.get(i);
				Iterator iterator   = objectMap.keySet().iterator();
				while (iterator.hasNext())
				{
					String strKey     = (String) iterator.next();
					hmFinalMap.put(strKey,  objectMap.get(strKey));
				}
				mlFinalList.add(hmFinalMap);
			}
			// End time
			long endTime = System.currentTimeMillis();
			// Calculate execution time
			long executionTime = endTime - startTime;
			// Print execution time
			String strInfoTimeMsg = new StringBuilder("Execution time for getColumnValues for table : ").append(hmTableData.get("RequestMap").get("table")).append(" in milliseconds is :").append(executionTime).toString();
			logger.log(Level.INFO, strInfoTimeMsg);
			return mlFinalList;
		}
		catch(Exception ex)
		{
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, ex);
			throw new FrameworkException(ex);
		}
	}
	//DSM (DS) 2022x-03 - Defect 53642 - Compare page pop up error message when click specifications part - START
	/**
	 * This method fetches the column values for an individual row of UI Table
	 * @param context
	 * @param processColumns
	 * @param bwsl
	 * @param rwsl
	 * @param programResult
	 * @param iColumnSize
	 * @param hmFinalMap
	 * @param rowIndex
	 * @throws FrameworkException
	 */
	private void getRowColumnValues(Context context, MapList processColumns, MapList mlObjListAccess,
			BusinessObjectWithSelectList bwsl, RelationshipWithSelectList rwsl,
			Vector[] programResult, int iColumnSize, HashMap<String, Object> hmFinalMap,
			int rowIndex) throws FrameworkException 
	{
		String strPropkey;
		String strNLSType;
		HashMap hmColumnMap;
		String strColumnType;
		String strGroupHeader;
		String strColumnLabel;
		String strRegSuite;
		String strColumnValue;
		for (int k = 0; k < iColumnSize; k++) 
		{
			hmColumnMap = (HashMap<String, Object>) processColumns.get(k);
			strColumnType = UITable.getSetting(hmColumnMap, "Column Type");
			strGroupHeader = UITable.getSetting(hmColumnMap, "Group Header");
			strColumnLabel = UITable.getName(hmColumnMap);

			if (UIUtil.isNotNullAndNotEmpty(strGroupHeader)) 
			{
				strRegSuite = UITable.getSetting(hmColumnMap, "Registered Suite");
				if (UIUtil.isNotNullAndNotEmpty(strRegSuite)) 
				{
					strGroupHeader = EnoviaResourceBundle.getProperty(context, "emx" + strRegSuite + "StringResource",
							context.getLocale(), strGroupHeader);
				}
				strColumnLabel = new StringBuilder(strGroupHeader).append("_").append(strColumnLabel).toString();
			}

			strColumnValue = getColumnValue(context, strColumnType, programResult, rowIndex, k, hmColumnMap,
					mlObjListAccess, bwsl, rwsl);

			if (null != strColumnValue) 
			{
				hmFinalMap.put(strColumnLabel, strColumnValue);
			}
		}
	}
	/**
	 * This program gets the value for columns based on Column Type
	 * @param context
	 * @param strColumnType
	 * @param programResult
	 * @param rowIndex
	 * @param columnIdx
	 * @param hmColumnMap
	 * @param mlObjListAccess
	 * @param bwsl
	 * @param rwsl
	 * @return
	 * @throws FrameworkException
	 */
	private String getColumnValue(Context context, String strColumnType, Vector[] programResult, int rowIndex, int columnIdx,
			HashMap<String, Object> hmColumnMap, MapList mlObjListAccess,
			BusinessObjectWithSelectList bwsl, RelationshipWithSelectList rwsl) throws FrameworkException 
	{
		if ("program".equals(strColumnType) || "programHTMLOutput".equals(strColumnType))
		{
			return getProgramColumnValue(context, strColumnType, programResult, rowIndex, columnIdx, hmColumnMap,
					mlObjListAccess);
		} 
		else if ("businessobject".equals(strColumnType)) 
		{
			return getBusinessObjectColumnValue(context, hmColumnMap, bwsl, rowIndex);
		} 
		else if ("relationship".equals(strColumnType)) 
		{
			return getRelationshipColumnValue(context, hmColumnMap, rwsl, rowIndex);
		}
		return null;
	}
	/**
	 * This program gets the value for Columns of type program/programHTMLOutput
	 * @param context
	 * @param strColumnType
	 * @param programResult
	 * @param rowIndex
	 * @param columnIdx
	 * @param hmColumnMap
	 * @param mlObjListAccess
	 * @return
	 * @throws FrameworkException
	 */
	private String getProgramColumnValue(Context context, String strColumnType, Vector[] programResult, int rowIndex, int columnIdx,
			HashMap<String, Object> hmColumnMap, MapList mlObjListAccess) throws FrameworkException 
	{
		int iObjListSize = mlObjListAccess.size();
		int iColumnProgResultSize = null!=programResult[columnIdx] ? programResult[columnIdx].size() : -1;
		if (null != programResult[columnIdx] && iObjListSize == iColumnProgResultSize) 
		{
			if ("program".equals(strColumnType) && iColumnProgResultSize > rowIndex) 
			{
				HashMap<?, ?> hmProgram = (HashMap<?, ?>) programResult[columnIdx].get(rowIndex);
				return (String) hmProgram.get("DisplayValue");
			} 
			else if ("programHTMLOutput".equals(strColumnType) && iColumnProgResultSize > rowIndex) 
			{
				Object obj = programResult[columnIdx].get(rowIndex);
				return null != obj ? obj.toString() : null;
			}
		} 
		else if (null !=programResult[columnIdx] && iObjListSize != iColumnProgResultSize) 
		{
			Map<?, ?> mpObjAccess = (Map<?, ?>) mlObjListAccess.get(rowIndex);
			if (mpObjAccess.containsKey("accessCounter")) 
			{
				int iCounter = (int) mpObjAccess.get("accessCounter");
				if ("program".equals(strColumnType) && iColumnProgResultSize > iCounter) 
				{
					HashMap<?, ?> hmProgram = (HashMap<?, ?>) programResult[columnIdx].get(iCounter);
					return (String) hmProgram.get("DisplayValue");
				} 
				else if ("programHTMLOutput".equals(strColumnType) && iColumnProgResultSize > iCounter) 
				{
					Object obj = programResult[columnIdx].get(iCounter);
					return null != obj ? obj.toString() : null;
				}
			}
		}
		return null;
	}
	/**
	 * This program gets the value for columns fetching value from bus expression
	 * @param context
	 * @param hmColumnMap
	 * @param bwsl
	 * @param rowIndex
	 * @return
	 * @throws FrameworkException
	 */
	private String getBusinessObjectColumnValue(Context context, HashMap<String, Object> hmColumnMap,
			BusinessObjectWithSelectList bwsl, int rowIndex) throws FrameworkException 
	{
		String strColumnSelect = UITableCommon.getBusinessObjectSelect(hmColumnMap);
		if (null != strColumnSelect) 
		{
			StringList slColValueList = (StringList) (bwsl.getElement(rowIndex).getSelectDataList(strColumnSelect));
			if (slColValueList != null && !slColValueList.isEmpty()) 
			{
				String strColumnValue = slColValueList.firstElement();
				if (strColumnSelect.equalsIgnoreCase(DomainConstants.SELECT_TYPE)) 
				{
					String strPropkey = "emxFramework.Type." + strColumnValue;
					String strNLSType = EnoviaResourceBundle.getFrameworkStringResourceProperty(context, strPropkey,
							context.getLocale());
					if (!strPropkey.equals(strNLSType)) 
					{
						return strNLSType;
					}
				}
				return strColumnValue;
			}
		}
		return null;
	}
	/**
	 * This program gets the value for columns fetching value from rel expression
	 * @param context
	 * @param hmColumnMap
	 * @param rwsl
	 * @param rowIndex
	 * @return
	 * @throws FrameworkException
	 */
	private String getRelationshipColumnValue(Context context, HashMap<String, Object> hmColumnMap,
			RelationshipWithSelectList rwsl, int rowIndex) throws FrameworkException 
	{
		String strColumnSelect = UITableCommon.getRelationshipSelect(hmColumnMap);
		if (null != strColumnSelect) 
		{
			try {
				StringList slColValueList = ((RelationshipWithSelect) rwsl.elementAt(rowIndex))
						.getSelectDataList(strColumnSelect);
				if (slColValueList != null && !slColValueList.isEmpty()) 
				{
					return slColValueList.firstElement();
				}
			} catch (Exception ex) {
				return null;
			}
		}
		return null;
	}
	//DSM (DS) 2022x-03 - Defect 53642 - Compare page pop up error message when click specifications part - END
	/**
	 * Utility method to convert ArrayList coming from Response to MapList
	 * @param mapOfArray
	 * @return
	 */
	public MapList convertArrayListToMapList(ArrayList jsonArray)
	{
		MapList mlData = new MapList();
		HashMap map;
		for (int i = 0; i < jsonArray.size(); i++) {
			map= (HashMap) jsonArray.get(i);
			mlData.add(map);
		}
		return mlData;
	}
}

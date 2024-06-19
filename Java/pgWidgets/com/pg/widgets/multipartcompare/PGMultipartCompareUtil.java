package com.pg.widgets.multipartcompare;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.domain.util.i18nNow;
import com.matrixone.apps.framework.ui.UIMenu;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.widgets.changeMgmtCompareReport.PGCompareReportUtil;
import com.pg.widgets.util.PGWidgetConstants;
import com.pg.widgets.util.PGWidgetUtil;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class PGMultipartCompareUtil {
	static final Logger logger = Logger.getLogger(PGMultipartCompareUtil.class.getName());

	// STRING CONSTANTS
	static final String EXCEPTION_MESSAGE = "Exception in PGCompareReportUtil";
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
	public static final String STRING_ALLOWED_TYPES = "AllowedTypes";
	private static final String EMXENGINEERINGCENTRAL_STRINGRESOURCE = "emxEngineeringCentralStringResource";
	private static final String SELECT_MASTER_PART_EXPR = "to[Classified Item].frommid[Part Family Reference].torel.to.";
	private static SecureRandom rand = new SecureRandom(); 

	/**
	 * This method returns objects info for the selectables passed
	 * 
	 * @param context
	 * @param mpRequestMap
	 * @return
	 * @throws Exception
	 */
	public Response getObjectDetails(matrix.db.Context context, String paramString) throws Exception {

		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
			String strObjectId = jsonInputData.getString("objectIds");
			StringList slObjId = StringUtil.split(strObjectId, ",");

			String strMode = jsonInputData.getString("mode");

			logger.log(Level.INFO, "slObjId :: " + slObjId);
			String strSelect = jsonInputData.getString("objSelectables");
			PGCompareReportUtil pgCompareReportUtil = new PGCompareReportUtil();

			String strErrorMsgNoAccess = EnoviaResourceBundle.getProperty(context, EMXENGINEERINGCENTRAL_STRINGRESOURCE,
					context.getLocale(), "pgEngineeringCentral.Platform.CompareReport.HasNoShowAccess.Error");
			String strErrorMsgNotApplicableType = EnoviaResourceBundle.getProperty(context,
					EMXENGINEERINGCENTRAL_STRINGRESOURCE, context.getLocale(),
					"pgEngineeringCentral.CompareReport.ReportNotApplicableForType");
			String strErrorMsgNonDSM = EnoviaResourceBundle.getProperty(context, EMXENGINEERINGCENTRAL_STRINGRESOURCE,
					context.getLocale(), "pgEngineeringCentral.CompareReport.NoSupported.NonDSM");
			strErrorMsgNonDSM = strErrorMsgNonDSM.replaceFirst(" : ", DomainConstants.EMPTY_STRING);
			
			Map<String, Object> mapParams = new HashMap<>();
			mapParams.put("objectIds", String.join(",", slObjId));
			mapParams.put("ObjSelectables", strSelect);

			Response respCompareReport = pgCompareReportUtil.getObjectDetails(context, mapParams);

			String strRespReprt = respCompareReport.readEntity(String.class);
			JsonObject jsonReportOut = PGWidgetUtil.getJsonFromJsonString(strRespReprt);
			JsonArray jsonArrayData = jsonReportOut.getJsonArray("data");

			JsonArrayBuilder updatedArrayBuilder = Json.createArrayBuilder();

			for (JsonObject jsonObject : jsonArrayData.getValuesAs(JsonObject.class)) {
				JsonObjectBuilder builder = Json.createObjectBuilder();
				for (String key : jsonObject.keySet()) {
					JsonValue value = jsonObject.get(key);
					if (key.equals("type")) {
						String strValue = value.toString().substring(1, value.toString().length() - 1);
						strValue = strValue.replaceAll(" ", "_");
						String strNLSType = EnoviaResourceBundle.getFrameworkStringResourceProperty(context,
								"emxFramework.Type." + strValue, context.getLocale());
						builder.add(key, strNLSType);
					}
					else if (key.equals("previous.id")) {
						builder.add("previousId", value);
					} else if (key.equals("previous.revision")) {
						builder.add("previousRevision", value);
					} else {
						builder.add(key, value);
					}
					builder.add("lastId", jsonObject.getString(DomainConstants.SELECT_ID));
					builder.add("lastRevision", jsonObject.getString(DomainConstants.SELECT_REVISION));
					String strCompareMsg = jsonObject.getString("CompareMessage");
					
					if (UIUtil.isNotNullAndNotEmpty(strMode) && "comparePart".equalsIgnoreCase(strMode)) {
						boolean fetchTabs = true;
						JsonObject jsonTemp = builder.build();
						if (jsonTemp.containsKey("TabList") && jsonTemp.getJsonArray("TabList") != null
								&& !jsonTemp.getJsonArray("TabList").isEmpty()) {
							fetchTabs = false;
						}
						logger.log(Level.INFO, "fetchTabs :: " + fetchTabs);
						if (fetchTabs && (!(strCompareMsg.startsWith(strErrorMsgNoAccess)
								|| strCompareMsg.startsWith(strErrorMsgNotApplicableType)
								|| strCompareMsg.startsWith(strErrorMsgNonDSM)))) {

							getTabList(context, jsonObject.getString(DomainConstants.SELECT_ID), jsonObject, builder);
						}
					}
					else if (jsonObject.containsKey("comparedWith")
							&& "Base Part".equals(jsonObject.getString("comparedWith"))) {
						StringList slSelects = new StringList(DomainConstants.SELECT_NAME);
						slSelects.add(DomainConstants.SELECT_REVISION);
						Map mapInfoBase = DomainObject.newInstance(context, jsonObject.getString("previous.id"))
								.getInfo(context, slSelects);
						builder.add("derivedPartName", (String) mapInfoBase.get(DomainConstants.SELECT_NAME));
						builder.add("derivedPartRevision", (String) mapInfoBase.get(DomainConstants.SELECT_REVISION));
					}
				}
				updatedArrayBuilder.add(builder.build());
			}
			jsonReturnObj.add("data", updatedArrayBuilder.build());
		} catch (Exception e) {
			jsonReturnObj.add(STRING_STATUS, STATUS_ERROR);
			jsonReturnObj.add(STRING_MESSAGE, e.getMessage());
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		}
		return Response.status(HttpServletResponse.SC_OK).entity(jsonReturnObj.build().toString()).build();

	}
	
	private static StringList getTabList(Context context, String strObjectId, JsonObject jsonObject,
			JsonObjectBuilder builder) throws Exception {
		Map programMap = new HashMap<String, Object>();
		Map tempProgramMap = new HashMap<String, String>();
		tempProgramMap.put(STRING_OBJECTID, jsonObject.getString(DomainConstants.SELECT_ID));
		programMap.put(STRING_REQUESTMAP, tempProgramMap);

		Map returnMap = (HashMap) JPO.invoke(context, "pgComparePartRevisionUtils", null, "getTypeRanges",
				JPO.packArgs(programMap), HashMap.class);
		StringList slFieldChoices = (StringList) returnMap.get("field_display_choices");

		StringList slTemp = addNewTabsToExistingTabs(context, strObjectId);
		slFieldChoices.addAll(slTemp);
		String strObjectType = DomainObject.newInstance(context, strObjectId).getInfo(context, DomainConstants.SELECT_TYPE);
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
		builder.add("TabList", PGWidgetUtil.convertArrayToJsonArray(slFieldChoices));
		return slFieldChoices;
	}

	private static StringList addNewTabsToExistingTabs(Context context, String strObjectId) throws Exception {
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
						try {
							bAccessExpressionVal = JPO.invoke(context, "pgComparePartRevisionUtils", null, "evaluateAccessExpressionCall", JPO.packArgs(programMap), Boolean.class);
						} catch (Exception e) {
							bAccessExpressionVal = false;
						}
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
		catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			return slReturn;
		}	
		return slReturn;
	}
	
	
	public String getMultiAttributeCompareReport(matrix.db.Context context, String paramString) throws Exception {
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		MapList finalMapList = new MapList();
		Map<String, String> mpRequestMap = new HashMap<>();

		JsonArray jsonArray = Json.createArrayBuilder().build();

		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
		JsonArray strObjectIds = jsonInputData.getJsonArray("objectIds");
		String strCompareType = jsonInputData.getString("compareType");
		String strMode = jsonInputData.getString("mode");
		
		StringBuilder sbMessage = null;
		StringList slExcludedParts = new StringList();
		try {

			for (int i = 0; i < strObjectIds.size(); i++) {
				JsonObject jsonObj = strObjectIds.getJsonObject(i);
				String lastId = jsonObj.getString("lastId");
				if ("compareRevision".equalsIgnoreCase(strMode) && !jsonObj.containsKey("previousId")) {
					slExcludedParts.add(
							DomainObject.newInstance(context, lastId).getInfo(context, DomainConstants.SELECT_NAME));
					continue;
				}
				String previousId = jsonObj.containsKey("previousId") ? jsonObj.getString("previousId") : DomainConstants.EMPTY_STRING;

				mpRequestMap.put("objectId", lastId);
				mpRequestMap.put("objectId2", previousId);
				mpRequestMap.put("compareType", strCompareType);

				MapList mlAttributeCompare = JPO.invoke(context, "pgComparePartRevisionUtils", null,
						"getFormFieldValuesToCompare", JPO.packArgs(mpRequestMap), MapList.class);

				if("Attributes".equals(strCompareType)) {
					mlAttributeCompare.add(buildMasterMap(context, lastId, previousId));	
				}
				finalMapList = mergeMapList(finalMapList, mlAttributeCompare, lastId, previousId);
			}
			logger.log(Level.INFO ,"getMultiAttributeCompareReport finalMapList :: " + finalMapList);
			jsonArray = PGWidgetUtil.converMaplistToJsonArray(context, finalMapList);
			jsonReturnObj.add("data", jsonArray);

			jsonReturnObj.add(STRING_MESSAGE, STRING_OK);
			if (slExcludedParts.size() > 0) {
				sbMessage = new StringBuilder();
				sbMessage.append("No Previous/Base revision exist for ").append(slExcludedParts.join(", "));
				jsonReturnObj.add(STRING_MESSAGE, sbMessage.toString());
			}
		} catch (FrameworkException e) {
			jsonReturnObj.add(STRING_STATUS, STATUS_ERROR);
			jsonReturnObj.add(STRING_MESSAGE, e.getMessage());
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		}
		return jsonReturnObj.build().toString();
	}

	public MapList mergeMapList(MapList finalMapList, MapList mlAttributeCompare, String lastId, String previousId) {
		if (finalMapList.isEmpty()) {
			for (int i = 0; i < mlAttributeCompare.size(); i++) {
				Map<String, String> map = (Map<String, String>) mlAttributeCompare.get(i);
				if (map.containsKey("ATTR1")) {
					String value = map.get("ATTR1");
					map.remove("ATTR1");
					map.put(lastId, value);
				}
				if (map.containsKey("ATTR2")) {
					String value = map.get("ATTR2");
					map.remove("ATTR2");
					map.put(previousId, value);
				}
				if(map.containsKey(DomainConstants.EMPTY_STRING)) {
					map.remove(DomainConstants.EMPTY_STRING);
				}
			}
			finalMapList = mlAttributeCompare;
		} else {
			for (int j = 0; j < finalMapList.size(); j++) {
				Map<String, String> finalMap = (Map<String, String>) finalMapList.get(j);
				for (int i = 0; i < mlAttributeCompare.size(); i++) {
					Map<String, String> currentObjMap = (Map<String, String>) mlAttributeCompare.get(i);
					if (currentObjMap.containsKey("ATTRIBUTE_NAME")) {
						String ATTRIBUTE_NAME1 = currentObjMap.get("ATTRIBUTE_NAME");
						String ATTRIBUTE_NAME2 = finalMap.get("ATTRIBUTE_NAME");
						if(finalMap.containsKey(DomainConstants.EMPTY_STRING)) {
							finalMap.remove(DomainConstants.EMPTY_STRING);
						}
						if (ATTRIBUTE_NAME1.equals(ATTRIBUTE_NAME2)) {
							finalMap.put(lastId, currentObjMap.get("ATTR1"));
							finalMap.put(previousId, currentObjMap.get("ATTR2"));
							break;
						}
					}
				}

			}
		}
		return finalMapList;
	}

	private Map buildMasterMap(Context context, String lastId, String previousId) {
		Map mapRet = new HashMap();
		mapRet.put(previousId, getMasterPartInfo(context, previousId));
		mapRet.put(lastId, getMasterPartInfo(context, lastId));
		mapRet.put("ATTRIBUTE_NAME", "Master Part Name");
		
		return mapRet;
	}
	
	private String getMasterPartInfo(Context context, String strObjId) {
		try {
			Map mapInfo = DomainObject.newInstance(context, strObjId).getInfo(context,
					StringList.create(SELECT_MASTER_PART_EXPR + DomainConstants.SELECT_NAME,
							SELECT_MASTER_PART_EXPR + DomainConstants.SELECT_REVISION));
			String strMasterName = (String) mapInfo.get(SELECT_MASTER_PART_EXPR + DomainConstants.SELECT_NAME);
			String strMasterRevison = (String) mapInfo.get(SELECT_MASTER_PART_EXPR + DomainConstants.SELECT_REVISION);
			if (UIUtil.isNotNullAndNotEmpty(strMasterName)) {
				return strMasterName + " " + strMasterRevison;
			}
			return DomainConstants.EMPTY_STRING;
		} catch (FrameworkException e) {
			logger.log(Level.SEVERE, "Exception in getMasterPartInfo ", e.getMessage());
			return DomainConstants.EMPTY_STRING;
		}
	}
	
	private static StringList generateRandomNumber(Context context, int listSize) {
		StringList slRandomList = new StringList();
		for(int i=0 ; i<listSize ; i++) {
			slRandomList.add(Integer.toString(rand.nextInt(9000000) + 1000000));
		}
		return slRandomList;
	}
	
	public static String getBOMData(Context context, String strInput) throws Exception {

		JsonArrayBuilder jsonArrBuilder = Json.createArrayBuilder();
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInput);
		String strCompareType = jsonInputData.getString("compareType");
		String strMode = jsonInputData.getString("mode");
		JsonArray jsonArrObjectIds = jsonInputData.getJsonArray("objectIds");
		boolean isModeComparePart = "comparePart".equalsIgnoreCase(strMode) ? true : false;
		PGCompareReportUtil pgCompareReportUtil = new PGCompareReportUtil();
		Map<String, Object> mapParams = new HashMap<>();
		mapParams.put("compareType", strCompareType);
		StringList slSelects = new StringList();
		slSelects.add(DomainConstants.SELECT_NAME);
		slSelects.add(DomainConstants.SELECT_TYPE);
		slSelects.add(DomainConstants.SELECT_REVISION);
		StringBuilder sbMessage = new StringBuilder();
		StringList slNoBOMParts = new StringList();
		String strLocale = context.getLocale().getLanguage();
		for (int i = 0; i < jsonArrObjectIds.size(); i++) {
			JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
			JsonObject jsonObj = jsonArrObjectIds.getJsonObject(i);
			if (isModeComparePart && ((!jsonObj.containsKey("previous") || !jsonObj.containsKey("last"))
					|| (jsonObj.getString("last").equals(jsonObj.getString("previous"))))) {
				continue;
			}
			/*
			 * In case of mode = comparePart:
			 * last = non-base part
			 * previous = base part
			 * no use of key 'id'
			 */
			String strObjIdToReturn = jsonObj.getString("id"); // Dropped Object
			String strLastRevId = jsonObj.getString("last"); // last / non-base
			String strPrevRevId = jsonObj.getString("previous") ; //Previous / base
			mapParams.put("objectId", strLastRevId);
			mapParams.put("objectId2", strPrevRevId);
			Response respCompareReport = pgCompareReportUtil.getCompareReportData(context, mapParams);

			String strRespReprt = respCompareReport.readEntity(String.class);

			JsonObject jsonReportOut = PGWidgetUtil.getJsonFromJsonString(strRespReprt);

			logger.log(Level.INFO ,"getBOMData jsonReportOut :: " + jsonReportOut);
			
			JsonObject jsonObjData = jsonReportOut.getJsonObject("data");
			JsonObject jsonObjDataObj = jsonObjData.getJsonObject(strLastRevId);
			JsonArray jsonArrCurrent = jsonObjDataObj.getJsonArray("current");
			
			Map mapInfo = DomainObject.newInstance(context, strObjIdToReturn).getInfo(context, slSelects);
			
			if(jsonArrCurrent == null || jsonArrCurrent.size() == 1) {//Only Part itself is returned
				slNoBOMParts.add((String)mapInfo.get(DomainConstants.SELECT_NAME));
//				continue;
			}
			JsonArray jsonArrPrevious = jsonObjDataObj.getJsonArray("previous");

			int listSize = 0;
			if(jsonArrPrevious != null) {
				listSize = jsonArrPrevious.size();
			}if(jsonArrCurrent != null && jsonArrPrevious != null && jsonArrCurrent.size() <= jsonArrPrevious.size()) {
				listSize = jsonArrCurrent.size();
			}
//			int listSize = jsonArrCurrent.size() <= jsonArrPrevious.size() ? jsonArrCurrent.size() : jsonArrPrevious.size();
			StringList slRandomList = generateRandomNumber(context, listSize);
			
			MapList mlCurrent = convertJsonArrayToMaplistBOM(context, jsonArrCurrent, "last", slRandomList);
			MapList mlPrevious = convertJsonArrayToMaplistBOM(context, jsonArrPrevious, "previous", slRandomList);

			Map mapInfoRight = DomainObject.newInstance(context, strPrevRevId).getInfo(context, slSelects);
			Map mapInfoLeft = DomainObject.newInstance(context, strLastRevId).getInfo(context, slSelects);
			
			if ("FBOM".equals(strCompareType)) {
				Map<String, String> mapTemp = new HashMap();
				mapTemp.put(DomainConstants.SELECT_LEVEL, "0");
				mapTemp.put(DomainConstants.SELECT_ID, strLastRevId);
				mapTemp.put(PGWidgetConstants.KEY_REL_ID, strLastRevId);
				mapTemp.put("revType", "last");
				mapTemp.put("Type Icon", (String) mapInfoLeft.get(DomainConstants.SELECT_NAME));
				mlCurrent.add(0, mapTemp);
				
				mapTemp = new HashMap<String, String>();
				mapTemp.put(DomainConstants.SELECT_LEVEL, "0");
				mapTemp.put(DomainConstants.SELECT_ID, strPrevRevId);
				mapTemp.put(PGWidgetConstants.KEY_REL_ID, strPrevRevId);
				mapTemp.put("revType", "previous");
				mapTemp.put("Type Icon", (String) mapInfoRight.get(DomainConstants.SELECT_NAME));
				mlPrevious.add(0, mapTemp);
			}
			MapList mlTreeCurrent = getTreeStructureMaplist(context, mlCurrent, 0);
			MapList mlTreePrevious = getTreeStructureMaplist(context, mlPrevious, 0);

			JsonArray jsonArrLastFormatted = formatTreeTableInJson(context, mlTreeCurrent, 0);
			JsonArray jsonArrPreviousFormatted = formatTreeTableInJson(context, mlTreePrevious, 0);

			jsonObjBuilder.add(DomainConstants.SELECT_ID, strObjIdToReturn);
			jsonObjBuilder.add("previousRevision", mapInfoRight.get(DomainConstants.SELECT_REVISION).toString());
			jsonObjBuilder.add("lastRevision", mapInfoLeft.get(DomainConstants.SELECT_REVISION).toString());
			jsonObjBuilder.add("previousName", mapInfoRight.get(DomainConstants.SELECT_NAME).toString());
			jsonObjBuilder.add(DomainConstants.SELECT_ID, strObjIdToReturn);
			jsonObjBuilder.add(DomainConstants.SELECT_NAME, (String)mapInfo.get(DomainConstants.SELECT_NAME));
			String strDisplayType = i18nNow.getTypeI18NString((String)mapInfo.get(DomainConstants.SELECT_TYPE), strLocale);
			jsonObjBuilder.add(DomainConstants.SELECT_TYPE, UIUtil.isNotNullAndNotEmpty(strDisplayType) ?strDisplayType : (String)mapInfo.get(DomainConstants.SELECT_TYPE));
			jsonObjBuilder.add(DomainConstants.SELECT_REVISION, (String)mapInfo.get(DomainConstants.SELECT_REVISION));

			if(isModeComparePart) {
				jsonObjBuilder.add("id_basePart", strLastRevId);
				jsonObjBuilder.add("id_nonBasePart", strPrevRevId);
				jsonObjBuilder.add("type_basePart", mapInfoLeft.get(DomainConstants.SELECT_TYPE).toString());
				jsonObjBuilder.add("type_nonBasePart", mapInfoRight.get(DomainConstants.SELECT_TYPE).toString());
				jsonObjBuilder.add("name_basePart", mapInfoLeft.get(DomainConstants.SELECT_NAME).toString());
				jsonObjBuilder.add("name_nonBasePart", mapInfoRight.get(DomainConstants.SELECT_NAME).toString());
				jsonObjBuilder.add("rev_basePart", mapInfoLeft.get(DomainConstants.SELECT_REVISION).toString());
				jsonObjBuilder.add("rev_nonBasePart", mapInfoRight.get(DomainConstants.SELECT_REVISION).toString());
			}
			
			jsonObjBuilder.add("previous", jsonArrPreviousFormatted);
			jsonObjBuilder.add("last", jsonArrLastFormatted);
			jsonObjBuilder.add("column", jsonObjDataObj.getJsonArray("column"));

			jsonArrBuilder.add(jsonObjBuilder.build());
		}
		jsonReturnObj.add("data", jsonArrBuilder.build());
		if(slNoBOMParts.size() > 0) {
			sbMessage.append("No BOM data found for ").append(slNoBOMParts.join(", "));
			jsonReturnObj.add("warning", sbMessage.toString());
		}
		return jsonReturnObj.build().toString();
	}
	
	
	public static MapList convertJsonArrayToMaplistBOM(Context context, JsonArray jsonArrInput, String strRev, StringList slRandomList)
			throws Exception {
		MapList mlReturn = new MapList();
		try {
			Object objValue = null;
			if (jsonArrInput != null) {
				for (int i = 0; i < jsonArrInput.size(); i++) {
					Map mapToAdd = new HashMap();
					JsonObject jsonObjectTemp = jsonArrInput.getJsonObject(i);
					Set setKeys = jsonObjectTemp.keySet();
					Iterator itr = setKeys.iterator();
					while (itr.hasNext()) {
						String strKey = (String) itr.next();
						objValue = jsonObjectTemp.get(strKey) != null ? jsonObjectTemp.get(strKey)
								: DomainConstants.EMPTY_STRING;
						if (objValue instanceof StringList) {
							mapToAdd.put(strKey, PGWidgetUtil.getStringFromSL((StringList) objValue,
									PGWidgetConstants.KEY_COMMA_SEPARATOR));
						} else if (objValue instanceof JsonValue || objValue instanceof String) {
							mapToAdd.put(strKey, jsonObjectTemp.getString(strKey));
						}
					}
					if(!mapToAdd.containsKey(PGWidgetConstants.SELECT_CONNECTION_ID)) {
						//added to be aligned with other rows on UI for row comparison
						mapToAdd.put(PGWidgetConstants.KEY_REL_ID, mapToAdd.get(DomainConstants.SELECT_ID));
					}else {
						mapToAdd.put(PGWidgetConstants.KEY_REL_ID, mapToAdd.get(PGWidgetConstants.SELECT_CONNECTION_ID));
					}
					if (i < slRandomList.size()) {
						mapToAdd.put("refId", slRandomList.get(i));
					}
					mapToAdd.put("revType", strRev);
					mlReturn.add(mapToAdd);
				}
			}
		} catch (Exception ex) {
			throw ex;
		}
		return mlReturn;
	}

	public static MapList convertJsonArrayToMaplist(Context context, JsonArray jsonArrInput, String strRev, StringList slRandomList)
			throws Exception {
		MapList mlReturn = new MapList();
		try {
			Object objValue = null;
			for (int i = 0; i < jsonArrInput.size(); i++) {
				Map mapToAdd = new HashMap();
				JsonObject jsonObjectTemp = jsonArrInput.getJsonObject(i);
				Set setKeys = jsonObjectTemp.keySet();
				Iterator itr = setKeys.iterator();
				while (itr.hasNext()) {
					String strKey = (String) itr.next();
					objValue = jsonObjectTemp.get(strKey) != null ? jsonObjectTemp.get(strKey)
							: DomainConstants.EMPTY_STRING;
					if (objValue instanceof StringList) {
						mapToAdd.put(strKey, PGWidgetUtil.getStringFromSL((StringList) objValue,
								PGWidgetConstants.KEY_COMMA_SEPARATOR));
					} else if (objValue instanceof JsonArray) {
						mapToAdd.put(strKey, jsonObjectTemp.getJsonArray(strKey));
					} else if (objValue instanceof String || objValue instanceof JsonValue) {
						mapToAdd.put(strKey, jsonObjectTemp.getString(strKey));
					}
				}
				if (i < slRandomList.size()) {
					mapToAdd.put("refId", slRandomList.get(i));
				}
				mapToAdd.put("revType", strRev);
				mlReturn.add(mapToAdd);
			}
		} catch (Exception ex) {
			throw ex;
		}
		return mlReturn;
	}

	public static MapList getTreeStructureMaplist(Context context, MapList mlInput, int iLevel)
			throws NumberFormatException {
		Stack<Map> stackJsonObj = new Stack<>();
		MapList mlReturn = new MapList();
		int nLevelCurrent = 1;
		int nLevelPrev = 1;
		int nLevelAppend = 0;
		for (Iterator<?> iterator = mlInput.iterator(); iterator.hasNext();) {
			Map<?, ?> tempMap = (Map<?, ?>) iterator.next();
			String strLevel = (String) tempMap.get(DomainConstants.SELECT_LEVEL);
			nLevelCurrent = Integer.parseInt(strLevel);
			nLevelAppend = nLevelCurrent + iLevel;
			if (nLevelCurrent <= nLevelPrev) {
				if (!stackJsonObj.empty()) {
					mlReturn.add(stackJsonObj.pop());
				}
				for (int i = nLevelCurrent; i < nLevelPrev; i++) {
					try {
						stackJsonObj.pop();
					} catch (Exception e) {
					}
				}
			}
			stackJsonObj.push(convertTreeMapToFlatMap(stackJsonObj.empty() ? (Map) new HashMap() : stackJsonObj.peek(),
					tempMap, Integer.toString(nLevelAppend)));
			nLevelPrev = nLevelCurrent;
		}
		if (!stackJsonObj.empty()) {
			mlReturn.add(stackJsonObj.pop());
		}
		return mlReturn;
	}

	public static Map convertTreeMapToFlatMap(Map inputMapObj, Map<?, ?> inputMapTemp, String strLevel) {
		Map mapReturn = new HashMap();
		Map mapInner = new HashMap();
		inputMapObj.entrySet().forEach(e -> mapInner.put(((Entry<?, ?>) e).getKey(), ((Entry<?, ?>) e).getValue()));
		inputMapTemp.entrySet().forEach(e -> mapInner.put((String) e.getKey(),
				((e.getValue() instanceof StringList)
						? PGWidgetUtil.getStringFromSL((StringList) e.getValue(), PGWidgetConstants.KEY_PIPE_SEPARATOR)
						: (String) e.getValue())));
		mapReturn.put(strLevel, mapInner);
		return mapReturn;
	}

	public static JsonArray sortJsonArray(JsonArray jsonArr) {
		List<JsonObject> jsonValues = new ArrayList<JsonObject>();
	    for (int j = 0; j < jsonArr.size(); j++) {
	        jsonValues.add(jsonArr.getJsonObject(j));
	    }
		Collections.sort(jsonValues, new Comparator<JsonObject>() {
	        @Override
	        public int compare(JsonObject a, JsonObject b) {
	            Integer valA = 0;
	            Integer valB = 0;

	            try {
	                valA = Integer.parseInt(a.getString("hierarchy-level"));
	                valB = Integer.parseInt(b.getString("hierarchy-level"));
	            } 
	            catch (Exception e) {
	            	String valAName = a.containsKey("Name") ? a.getString("Name")
							: a.containsKey(DomainConstants.SELECT_NAME) ? a.getString(DomainConstants.SELECT_NAME) : DomainConstants.EMPTY_STRING;
					String valBName = b.containsKey("Name") ? b.getString("Name")
							: b.containsKey(DomainConstants.SELECT_NAME) ? b.getString(DomainConstants.SELECT_NAME) : DomainConstants.EMPTY_STRING;
					
					String valAType = a.containsKey(DomainConstants.SELECT_TYPE) ? a.getString(DomainConstants.SELECT_TYPE)
							: DomainConstants.EMPTY_STRING;
					String valBType = b.containsKey(DomainConstants.SELECT_TYPE) ? b.getString(DomainConstants.SELECT_TYPE)
							: DomainConstants.EMPTY_STRING;
					
					String valARev = a.containsKey(DomainConstants.SELECT_REVISION) ? a.getString(DomainConstants.SELECT_REVISION)
							: DomainConstants.EMPTY_STRING;
					String valBRev = b.containsKey(DomainConstants.SELECT_REVISION) ? b.getString(DomainConstants.SELECT_REVISION)
							: DomainConstants.EMPTY_STRING;
					
					if(!valBType.equals(valAType)) {
						return valAType.compareTo(valBType);
					}
					if(!valAName.equals(valBName)) {
						return valAName.compareTo(valBName);
					}
					return valARev.compareTo(valBRev);
	            }
	            return valA.compareTo(valB);
	        }
	    });
		JsonArrayBuilder jsonArrBldr = Json.createArrayBuilder();
		for (int j = 0; j < jsonValues.size(); j++) {
			jsonArrBldr.add(jsonValues.get(j));
	    }
		return jsonArrBldr.build();
	}
	
	public static JsonArray formatTreeTableInJson(Context context, MapList mlInput, int iStartLevel)
			throws MatrixException {
		JsonArrayBuilder jsonArrOut = Json.createArrayBuilder();
		Set<String> setIdsInserted = new HashSet<>();
		Set<String> setRelIdsInserted = new HashSet<>();
		for (int i = 0; i < mlInput.size(); i++) {
			Map mapTree = new HashMap();
			mapTree.putAll((Map) mlInput.get(i));
			Map mapTreeCopy = new HashMap();
			mapTreeCopy.putAll((Map) mlInput.get(i));
			JsonArrayBuilder jsonArrOutTemp = Json.createArrayBuilder();
			for (Iterator<?> itr = mapTree.keySet().iterator(); itr.hasNext();) {
				String strKey = (String) itr.next();
				int iCurrentLevel = Integer.parseInt(strKey);
				int iHierLength = (iCurrentLevel - iStartLevel) + 1;
				if (iHierLength >= iStartLevel) {
					while (iCurrentLevel >= iStartLevel) {
						JsonObjectBuilder inputBuilder = Json.createObjectBuilder();
						List<String> listHier = new ArrayList();
						List<String> listHierUnique = new ArrayList();
						strKey = Integer.toString(iCurrentLevel);
						for (int level = iCurrentLevel; level >= iStartLevel; level--) {
							String strLevel = Integer.toString(level);
							Map mapObject = new HashMap();
							
							mapObject.putAll((Map) mapTree.get(strLevel));
							if (mapObject != null) {
								if (level >= iStartLevel) {
									listHier.add(mapObject.get(DomainConstants.SELECT_ID).toString());
									if(mapObject.containsKey(PGWidgetConstants.SELECT_CONNECTION_ID))
										listHierUnique.add(mapObject.get(PGWidgetConstants.SELECT_CONNECTION_ID).toString());
									else
										listHierUnique.add(mapObject.get(DomainConstants.SELECT_ID).toString());
								}
								mapTree = new HashMap();
								mapTree.putAll(mapObject);
								if (strLevel.equals(strKey)) {
									if (mapObject.containsKey(Integer.toString(level - 1))) {
										mapObject.remove(Integer.toString(level - 1));
									}
									PGWidgetUtil.putJsonInJsonBuilder(inputBuilder, PGWidgetUtil
											.convertMapToJsonObj(Json.createObjectBuilder().build(), mapObject));
								}
							}
						}
						if (!listHier.isEmpty()) {
							Collections.reverse(listHier);
							inputBuilder.add("hierarchy", PGWidgetUtil.convertArrayToJsonArray(listHier));
							inputBuilder.add("hierarchy-level", strKey);
						}
						if (!listHierUnique.isEmpty()) {
							Collections.reverse(listHierUnique);
							inputBuilder.add("hierarchyUnique", PGWidgetUtil.convertArrayToJsonArray(listHierUnique));
						}
						if (inputBuilder.build().containsKey(DomainConstants.SELECT_ID)) {
							if (!setIdsInserted.contains(inputBuilder.build().getString(DomainConstants.SELECT_ID)) || ((inputBuilder.build().containsKey(PGWidgetConstants.SELECT_CONNECTION_ID)
									&& !setRelIdsInserted.contains(
											inputBuilder.build().getString(PGWidgetConstants.SELECT_CONNECTION_ID))))) {
								jsonArrOutTemp.add(inputBuilder.build());
							}
							setIdsInserted.add(inputBuilder.build().getString(DomainConstants.SELECT_ID));
							if (inputBuilder.build().containsKey(PGWidgetConstants.SELECT_CONNECTION_ID)) {
								setRelIdsInserted
										.add(inputBuilder.build().getString(PGWidgetConstants.SELECT_CONNECTION_ID));
							}
						}
						mapTree = new HashMap();
						if (mapTreeCopy != null) {
							mapTreeCopy = (Map) mapTreeCopy.get(strKey);
							if (mapTreeCopy != null) {
								mapTree.putAll(mapTreeCopy);
							}
						}
						iCurrentLevel--;
					}
				}
			}
			JsonArray jsonArrSorted = sortJsonArray(jsonArrOutTemp.build());
			for (int j = 0; j < jsonArrSorted.size(); j++) {
				jsonArrOut.add(jsonArrSorted.getJsonObject(j));
		    }
		}
		return jsonArrOut.build();
	}
	
	public static String getCompareReport(Context context, String strInput) throws Exception {

		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		try {
			JsonArrayBuilder jsonArrBuilder = Json.createArrayBuilder();
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInput);
			String strCompareType = jsonInputData.getString("compareType");

			if ("FBOM".equals(strCompareType)) {// Convert into tree for FBOM
				return getBOMData(context, strInput);
			}
			JsonArray jsonArrObjectIds = jsonInputData.getJsonArray("objectIds");
			String strMode = jsonInputData.getString("mode");
			boolean isModeComparePart = "comparePart".equalsIgnoreCase(strMode) ? true : false;
			PGCompareReportUtil pgCompareReportUtil = new PGCompareReportUtil();
			Map<String, Object> mapParamsFromWS = new HashMap<>();
			mapParamsFromWS.put("compareType", strCompareType);

			logger.log(Level.INFO, "jsonInputData :: " + jsonInputData);

			StringList slSelects = new StringList();
			slSelects.add(DomainConstants.SELECT_NAME);
			slSelects.add(DomainConstants.SELECT_TYPE);
			slSelects.add(DomainConstants.SELECT_REVISION);
			String strLocale = context.getLocale().getLanguage();
			for (int i = 0; i < jsonArrObjectIds.size(); i++) {
				JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
				JsonObject jsonObj = jsonArrObjectIds.getJsonObject(i);
				if (isModeComparePart && (!jsonObj.containsKey("previous") || !jsonObj.containsKey("last"))) {
					continue;
				}
				/*
				 * In case of mode = comparePart: last = non-base part, previous = base part, no
				 * use of key 'id'
				 */
				String strLastRevId = jsonObj.getString("last"); // Current
				String strPrevRevId = jsonObj.getString("previous"); // Previous
				String strObjIdToReturn = jsonObj.getString("id"); // Dropped Object
				Map mapInfo = DomainObject.newInstance(context, strObjIdToReturn).getInfo(context, slSelects);

				mapParamsFromWS.put("objectId", strLastRevId);
				mapParamsFromWS.put("objectId2", strPrevRevId);
				Map map = new HashMap();
				map.putAll(mapParamsFromWS);

				Response respCompareReport = pgCompareReportUtil.getCompareReportData(context, map);

				String strRespReprt = respCompareReport.readEntity(String.class);

				JsonObject jsonReportOut = PGWidgetUtil.getJsonFromJsonString(strRespReprt);

				logger.log(Level.INFO, "getCompareReport jsonReportOut :: " + jsonReportOut);

				JsonObject jsonObjData = jsonReportOut.getJsonObject("data");
				JsonObject jsonObjDataObj = jsonObjData.getJsonObject(strLastRevId);
				JsonArray jsonArrCurrent = jsonObjDataObj.getJsonArray("current");
				JsonArray jsonArrPrevious = jsonObjDataObj.getJsonArray("previous");

				int listSize = jsonArrCurrent.size() <= jsonArrPrevious.size() ? jsonArrCurrent.size()
						: jsonArrPrevious.size();
				StringList slRandomList = generateRandomNumber(context, listSize);

				MapList mlPrev = convertJsonArrayToMaplist(context, jsonArrPrevious, "previous", slRandomList);
				MapList mlCurr = convertJsonArrayToMaplist(context, jsonArrCurrent, "last", slRandomList);

				Map mapInfoLeft = DomainObject.newInstance(context, strLastRevId).getInfo(context, slSelects);
				Map mapInfoRight = DomainObject.newInstance(context, strPrevRevId).getInfo(context, slSelects);
				
				jsonObjBuilder.add("previousRevision", mapInfoRight.get(DomainConstants.SELECT_REVISION).toString());
				jsonObjBuilder.add("lastRevision", mapInfoLeft.get(DomainConstants.SELECT_REVISION).toString());
				jsonObjBuilder.add("previousName", mapInfoRight.get(DomainConstants.SELECT_NAME).toString());
				
				jsonObjBuilder.add(DomainConstants.SELECT_ID, strObjIdToReturn);
				jsonObjBuilder.add(DomainConstants.SELECT_NAME, (String) mapInfo.get(DomainConstants.SELECT_NAME));
				String strDisplayType = i18nNow.getTypeI18NString((String) mapInfo.get(DomainConstants.SELECT_TYPE),
						strLocale);
				jsonObjBuilder.add(DomainConstants.SELECT_TYPE,
						UIUtil.isNotNullAndNotEmpty(strDisplayType) ? strDisplayType
								: (String) mapInfo.get(DomainConstants.SELECT_TYPE));
				jsonObjBuilder.add(DomainConstants.SELECT_REVISION,
						(String) mapInfo.get(DomainConstants.SELECT_REVISION));

				if (isModeComparePart) {
					jsonObjBuilder.add("id_basePart", strLastRevId);
					jsonObjBuilder.add("id_nonBasePart", strPrevRevId);
					jsonObjBuilder.add("type_basePart", mapInfoLeft.get(DomainConstants.SELECT_TYPE).toString());
					jsonObjBuilder.add("type_nonBasePart", mapInfoRight.get(DomainConstants.SELECT_TYPE).toString());
					jsonObjBuilder.add("name_basePart", mapInfoLeft.get(DomainConstants.SELECT_NAME).toString());
					jsonObjBuilder.add("name_nonBasePart", mapInfoRight.get(DomainConstants.SELECT_NAME).toString());
					jsonObjBuilder.add("rev_basePart", mapInfoLeft.get(DomainConstants.SELECT_REVISION).toString());
					jsonObjBuilder.add("rev_nonBasePart",
							mapInfoRight.get(DomainConstants.SELECT_REVISION).toString());
				}
				JsonArray jsonArrLastFormatted = sortJsonArray(PGWidgetUtil.converMaplistToJsonArray(context, mlCurr));
				JsonArray jsonArrPreviousFormatted = sortJsonArray(
						PGWidgetUtil.converMaplistToJsonArray(context, mlPrev));

				jsonObjBuilder.add("previous", jsonArrPreviousFormatted);
				jsonObjBuilder.add("last", jsonArrLastFormatted);
				jsonObjBuilder.add("column", jsonObjDataObj.getJsonArray("column"));

				jsonArrBuilder.add(jsonObjBuilder.build());
			}
			jsonReturnObj.add("data", jsonArrBuilder.build());
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		}
		return jsonReturnObj.build().toString();
	}

	/**
	 * Method is to check allowed type for Multipart compare
	 * @param context
	 * @param paramString
	 * @return
	 * @throws Exception
	 */
	public String isValidType(matrix.db.Context context, String paramString) throws Exception {
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
		String strObjectId = jsonInputData.getString(DomainConstants.SELECT_ID);
		String strAllowedTypes = jsonInputData.getString(STRING_ALLOWED_TYPES);
		StringList slAllowedTypes = StringUtil.split(strAllowedTypes, ",");

		DomainObject domObj = DomainObject.newInstance(context, strObjectId);

		boolean flag = false;

		for (int i = 0; i < slAllowedTypes.size(); i++) {
			if (domObj.isKindOf(context, slAllowedTypes.get(i))) {
				flag = true;
				break;
			}
		}
		jsonReturnObj.add(PGWidgetConstants.KEY_STATUS, flag);
		return jsonReturnObj.build().toString();
	}
	
}
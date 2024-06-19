package com.png.apollo.dashboard.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import com.dassault_systemes.enovia.gls.common.util.PRSPUtil;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.png.apollo.pgApolloCommonUtil;
import com.png.apollo.pgApolloConstants;
import com.png.apollo.designtool.getData.ReadWriteXMLForPLMDTDocument;

import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class ApolloWidgetServiceUtil extends pgApolloConstants{
	static final String MULIT_VAL_CHAR = "\u0007";

	/**
	 * This method converts MapList into JSONArray
	 * 
	 * @param mlList
	 * @return Converted JSONArray
	 */
	public static JsonArray convertMapListToJsonArray(MapList mlList) {
		JsonArrayBuilder jsonArr = Json.createArrayBuilder();
		JsonObjectBuilder jsonObject = null;
		Map<?, ?> objMap = null;
		String strKey ;
		String strValue;
		for (int i = 0; i < mlList.size(); i++) 
		{
			jsonObject = Json.createObjectBuilder(); 
			objMap = (Map<?,?>)mlList.get(i);
			
		
			for (Entry<?, ?> entry : objMap.entrySet()) {
				 strKey = (String) entry.getKey();						
				 strValue = checkNullValueforString((String)entry.getValue());	
				 jsonObject.add(strKey, strValue);						
			}	
			jsonArr.add(jsonObject);
		}
		return jsonArr.build();
	}
	
	
	private static String checkNullValueforString(String strString )
	{
		if(strString!=null && strString.contains(MULIT_VAL_CHAR)) {
			strString=strString.replace(MULIT_VAL_CHAR, " , ");
		}
		return null != strString ? strString : DomainConstants.EMPTY_STRING;
	}
	
	private static JsonObjectBuilder addMapToJson(Map<?, ?> argMap) {
		JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
		String strKey ;
		Object strValue;
		for (Entry<?, ?> entry : argMap.entrySet()) {
			 strKey = (String) entry.getKey();						
			 strValue = entry.getValue();	
			 jsonObjectBuilder.add(strKey, strValue.toString());						
		}	
		return jsonObjectBuilder;
	}
	public static JsonArray convertMapListToJasonArray(Context  context ,MapList mlCMList, Map dragableColumnMap) throws MatrixException{
		JsonObjectBuilder jsonObjectBuilder = null;
		JsonArrayBuilder jsonArray = Json.createArrayBuilder();
		JsonObject jsonObj;
		StringList physicalIdKeyList = new StringList();
		StringList dragableColumnList =new StringList();
		Set<String> dragableColumnMapKeySet = dragableColumnMap.keySet();
		Iterator<String> dragableColumnMapKeySetItr =dragableColumnMapKeySet.iterator();
		String strDragableKey ;
		Map dragableMap;
		while(dragableColumnMapKeySetItr.hasNext()) {
			strDragableKey =dragableColumnMapKeySetItr.next();
			dragableMap =(Map)dragableColumnMap.get(strDragableKey);
			physicalIdKeyList.add((String)dragableMap.get(pgApolloConstants.SELECT_PHYSICAL_ID));
			dragableColumnList.add(strDragableKey);
		}
		
		Map mapResult = null;
		for (int i = 0; i < mlCMList.size(); i++) {
			mapResult = (Map) mlCMList.get(i);	
			jsonObjectBuilder = addMapToJson(mapResult);
			for(int j=0;j<physicalIdKeyList.size();j++) {
				strDragableKey =physicalIdKeyList.elementAt(j);
				if(mapResult.containsKey(strDragableKey)){
					dragableMap=(Map)dragableColumnMap.get(dragableColumnList.get(j));
					jsonObjectBuilder.add(dragableColumnList.get(j), convertDragableJsonArray(context ,mapResult ,(String)dragableMap.get(DomainConstants.SELECT_TYPE),(String)dragableMap.get(DomainConstants.SELECT_NAME),(String)dragableMap.get(pgApolloConstants.SELECT_PHYSICAL_ID)));
					jsonObj=jsonObjectBuilder.build();
					//Changed for 22x Upgrade - ALM 47841 - Starts
					if(null!=dragableMap.get(DomainConstants.SELECT_TYPE) && jsonObj.containsKey(dragableMap.get(DomainConstants.SELECT_TYPE))) {
						jsonObj = removeProperty(jsonObj, (String)dragableMap.get(DomainConstants.SELECT_TYPE));
					}
					if(null!=dragableMap.get(DomainConstants.SELECT_NAME) && jsonObj.containsKey(dragableMap.get(DomainConstants.SELECT_NAME))) {
						jsonObj = removeProperty(jsonObj, (String)dragableMap.get(DomainConstants.SELECT_NAME));
					}
					if(null!=dragableMap.get(pgApolloConstants.SELECT_PHYSICAL_ID) && jsonObj.containsKey(dragableMap.get(pgApolloConstants.SELECT_PHYSICAL_ID))) {
						jsonObj = removeProperty(jsonObj, (String)dragableMap.get(pgApolloConstants.SELECT_PHYSICAL_ID));
					//Changed for 22x Upgrade - ALM 47841 - Ends
					}
					
				}
			}
			
			jsonArray.add(jsonObjectBuilder.build());
		}
		return jsonArray.build();
	}
	
	
	/**
	 * Method to remove property from JSON Object
	 * @param jsonOriginalObject
	 * @param key
	 * @return
	 */
	public static JsonObject removeProperty(JsonObject jsonOriginalObject, String key) {
	    JsonObjectBuilder builder = Json.createObjectBuilder();

	    for (Map.Entry<String,JsonValue> entry : jsonOriginalObject.entrySet())
	    {
	        if (!entry.getKey().equals(key))
	        {
	           builder.add(entry.getKey(), entry.getValue());
	        }
	    }       
	    return builder.build();
	}
	
	
	public static JsonArray convertDragableJsonArray (Context context , Map inputMap , String strTypeKey , String strNameKey , String strPhysicalIDkey) throws MatrixException {
		JsonArrayBuilder jsonArray = Json.createArrayBuilder();
		String strLanguage = context.getSession().getLanguage();
		JsonObjectBuilder jsonObject = null;
		StringList strDisplayTypeList = new StringList();
		StringList strDisplayTypeTempList;
		
		StringList strTypeList = new StringList();
		
		StringList strNameList = new StringList();
		
		StringList strPhysicalIdList = new StringList();
		if(null!=strTypeKey && inputMap.containsKey(strTypeKey)) {
			strDisplayTypeTempList =pgApolloCommonUtil.getStringListFromObject(inputMap.get(strTypeKey));
			for(int i=0;i<strDisplayTypeTempList.size();i++) {
				strDisplayTypeList.add(EnoviaResourceBundle.getTypeI18NString(context, strDisplayTypeTempList.get(i), strLanguage));
			}				
		}
		
		if(null!=strTypeKey && inputMap.containsKey(strTypeKey)) {
			strTypeList =pgApolloCommonUtil.getStringListFromObject(inputMap.get(strTypeKey));
		}
		
		if(null!=strNameKey &&  inputMap.containsKey(strNameKey)) {
			strNameList =pgApolloCommonUtil.getStringListFromObject(inputMap.get(strNameKey));
		}
		
		if(null!=strPhysicalIDkey && inputMap.containsKey(strPhysicalIDkey)) {
			strPhysicalIdList =pgApolloCommonUtil.getStringListFromObject(inputMap.get(strPhysicalIDkey));
		}
		int nSize =strPhysicalIdList.size();
		
		if(nSize==strNameList.size() && nSize==strTypeList.size() && nSize==strDisplayTypeList.size()) {
			for(int i=0;i<nSize;i++) {
				jsonObject = Json.createObjectBuilder(); 
				jsonObject.add(DomainConstants.SELECT_TYPE, strTypeList.get(i));
				jsonObject.add("displayType", strDisplayTypeList.get(i));
				jsonObject.add(DomainConstants.SELECT_NAME, strNameList.get(i));
				jsonObject.add(pgApolloConstants.SELECT_PHYSICAL_ID, strPhysicalIdList.get(i));
				jsonArray.add(jsonObject.build());
			}
		}else if(nSize>0) {
			String strPhysicalId ;
			DomainObject domObj;
			Map tempMap;
			StringList stBusSelect = new StringList(2);
			stBusSelect.add(DomainConstants.SELECT_NAME);
			stBusSelect.add(DomainConstants.SELECT_TYPE);
			stBusSelect.add(DomainConstants.SELECT_ATTRIBUTE_TITLE);
			stBusSelect.add(DomainConstants.SELECT_REVISION);
			stBusSelect.add(DomainConstants.SELECT_CURRENT);
			for(int i=0;i<nSize;i++) {
				strPhysicalId =strPhysicalIdList.get(i);				
				if(UIUtil.isNotNullAndNotEmpty(strPhysicalId)) {
					domObj = DomainObject.newInstance(context ,PRSPUtil.convertToObjectId(context, strPhysicalId));
					tempMap =domObj.getInfo(context, stBusSelect);
					jsonObject = Json.createObjectBuilder();
					jsonObject.add(DomainConstants.SELECT_TYPE, (String)tempMap.get(DomainConstants.SELECT_TYPE));
					jsonObject.add("displayType", EnoviaResourceBundle.getTypeI18NString(context, (String)tempMap.get(DomainConstants.SELECT_TYPE), strLanguage));
					jsonObject.add(DomainConstants.SELECT_NAME, (String)tempMap.get(DomainConstants.SELECT_NAME));
					jsonObject.add(pgApolloConstants.SELECT_PHYSICAL_ID, strPhysicalIdList.get(i));
					jsonObject.add(DomainConstants.SELECT_REVISION, (String)tempMap.get(DomainConstants.SELECT_REVISION));
					jsonObject.add(DomainConstants.SELECT_CURRENT, (String)tempMap.get(DomainConstants.SELECT_CURRENT));
					jsonObject.add("title", (String)tempMap.get(DomainConstants.SELECT_ATTRIBUTE_TITLE));
					jsonArray.add(jsonObject.build());
				}
			}
			
		}
		
		return jsonArray.build();
	}
	
	/**
	 * Method to get Map List for Design Parameter View
	 * @param mapXMLParameter
	 * @param sMode 
	 * @return
	 */
	public static MapList getMapListForDesignParameterView(Map mapXMLParameter, String sMode) {
		MapList mlFinalList = new MapList();
		
		if(pgApolloConstants.KEY_DESIGNPARAM.equalsIgnoreCase(sMode))
		{
			mlFinalList = getDesignParameterList(mapXMLParameter);
		}
		else if(pgApolloConstants.KEY_PERFCHAR.equalsIgnoreCase(sMode))
		{
			mlFinalList = getPerfCharList(mapXMLParameter);
		}
		
		return mlFinalList;
	}
	
	/**
	 * Method to get Map List for Design Parameter View
	 * @param mapXMLParameter
	 * @return
	 */
	public static MapList getDesignParameterList(Map mapXMLParameter) {
		MapList mlFinalList = new MapList();		
		if(null != mapXMLParameter && mapXMLParameter.containsKey(STR_PRODUCT_DEFINITION))
		{
			Map mapBaseAPPParameter = (Map)mapXMLParameter.get(STR_PRODUCT_DEFINITION);		  
			Iterator entries1 = mapBaseAPPParameter.entrySet().iterator();

			Map mpRawMaterial = new HashMap();
			Map mpMaterialFunc = new HashMap();
			Map mpParam;
			String strRMPName;
			String strMaterialFunction;
			String key;
			String strDesignParam;
			String strGrpLayer;
			StringBuilder sbGrpLayer;				
			StringList slParamDetails;

			Map.Entry entry = null;
			while (entries1.hasNext()) 
			{
				entry = (Map.Entry) entries1.next();
				key = entry.getKey().toString();

				slParamDetails = StringUtil.split(key, CONSTANT_STRING_DOT);

				if(slParamDetails.size() == 1 ) 
				{
					continue;
				}

				strDesignParam = slParamDetails.get(3);
				sbGrpLayer = new StringBuilder();
				strGrpLayer =sbGrpLayer.append(slParamDetails.get(0)).append(CONSTANT_STRING_DOT).append(slParamDetails.get(1)).toString();

				if(strDesignParam.equals(STR_DESIGN_PARAMETER_RMP) && !mpRawMaterial.containsKey(strGrpLayer))
				{
					mpRawMaterial.put(strGrpLayer,entry.getValue().toString());
				}
				if(strDesignParam.equals(STR_DESIGN_PARAMETER_MATERIAL_FUNCTION) && !mpMaterialFunc.containsKey(strGrpLayer))
				{
					mpMaterialFunc.put(strGrpLayer,entry.getValue().toString());
				}
			}

			Iterator entries = mapBaseAPPParameter.entrySet().iterator();		  

			while (entries.hasNext()) 
			{
				entry = (Map.Entry) entries.next();
				key = entry.getKey().toString();
				slParamDetails = StringUtil.split(key, CONSTANT_STRING_DOT);
				mpParam = new HashMap();

				if(slParamDetails.size() == 1)
				{

					mpParam.put(KEY_GROUP,CONSTANT_STRING_HYPHEN);
					mpParam.put(KEY_LAYER,CONSTANT_STRING_HYPHEN);				
					mpParam.put(KEY_PARAMETERSET,CONSTANT_STRING_HYPHEN);
					mpParam.put(KEY_DESIGN_PARAMETER,slParamDetails.get(0));
					mpParam.put(KEY_DESIGN_PARAMETER_VALUES,entry.getValue().toString());
					mpParam.put(KEY_RMPNAME, CONSTANT_STRING_HYPHEN);				
					mpParam.put(KEY_MATERIAL_FUNCTION,CONSTANT_STRING_HYPHEN);

				}

				else
				{

					sbGrpLayer = new StringBuilder();
					strGrpLayer = sbGrpLayer.append(slParamDetails.get(0)).append(CONSTANT_STRING_DOT).append(slParamDetails.get(1)).toString();			

					mpParam.put(KEY_GROUP,slParamDetails.get(0));
					mpParam.put(KEY_LAYER,slParamDetails.get(1));				
					mpParam.put(KEY_PARAMETERSET,slParamDetails.get(2));
					mpParam.put(KEY_DESIGN_PARAMETER,slParamDetails.get(3));
					mpParam.put(KEY_DESIGN_PARAMETER_VALUES,entry.getValue().toString());

					strRMPName = EMPTY_STRING;
					strMaterialFunction = EMPTY_STRING;

					if(mpRawMaterial.containsKey(strGrpLayer))
					{
						strRMPName = (String)mpRawMaterial.get(strGrpLayer);
					}
					if(mpMaterialFunc.containsKey(strGrpLayer))
					{
						strMaterialFunction = (String)mpMaterialFunc.get(strGrpLayer);
					}
					mpParam.put(KEY_RMPNAME, strRMPName);				
					mpParam.put(KEY_MATERIAL_FUNCTION,strMaterialFunction);			
				}
				mpParam.put(KEY_CHG, DomainConstants.EMPTY_STRING);			
				mpParam = updateDesignParameterMap(mpParam);						
				mlFinalList.add(mpParam);			
			}
		}
		return mlFinalList;
	}	
	
	/**
	 * Method to update Design Parameter Map
	 * @param mpParam
	 * @return
	 */
	public static Map updateDesignParameterMap(Map mpParam) {
		StringBuilder sbUniqueKey;			
		if(null!=mpParam && !mpParam.isEmpty())
		{
			sbUniqueKey = new StringBuilder();
			sbUniqueKey.append((String)mpParam.get(KEY_GROUP));
			sbUniqueKey.append(pgApolloConstants.CONSTANT_STRING_PIPE);
			sbUniqueKey.append((String)mpParam.get(KEY_LAYER));
			sbUniqueKey.append(pgApolloConstants.CONSTANT_STRING_PIPE);
			sbUniqueKey.append((String)mpParam.get(KEY_PARAMETERSET));
			sbUniqueKey.append(pgApolloConstants.CONSTANT_STRING_PIPE);
			sbUniqueKey.append((String)mpParam.get(KEY_DESIGN_PARAMETER));			
			mpParam.put(KEY_UNIQUEKEY, sbUniqueKey.toString());
		}		
		return mpParam;
	}
	
	/**
	 * Method to update Design Parameter for Chg value
	 * @param context
	 * @param strPreviousObjectId
	 * @param mlFinalList
	 * @param sMode 
	 * @return
	 * @throws Exception
	 */
	public static MapList updateDesignParameterListForChange(Context context, String strPreviousObjectId, MapList mlFinalList, String sMode) throws Exception {
		
		MapList mlPreviousPartFinalList = new MapList();
		Map mapPreviousXMLParameter;
		if(UIUtil.isNotNullAndNotEmpty(strPreviousObjectId))
		{
			mapPreviousXMLParameter = ReadWriteXMLForPLMDTDocument.getConfigXMLParameters(context, strPreviousObjectId, true, true);
			mlPreviousPartFinalList = getMapListForDesignParameterView(mapPreviousXMLParameter, sMode);
		}
		if(null!=mlPreviousPartFinalList && !mlPreviousPartFinalList.isEmpty())
		{
			Map mapDesignParameter;
			Map mapPreviousDesignParameter;
			Iterator itr = mlFinalList.iterator();
			String strUniqueKey;
			String strPreviousUniqueKey;
			String strParameterValue;
			String strPreviousParameterValue;
			boolean isParameterFound = false;
			boolean isValueMatch = false;

			while(itr.hasNext())
			{
				mapDesignParameter = (Map)itr.next();
				if(mapDesignParameter.containsKey(KEY_UNIQUEKEY))
				{
					isParameterFound = false;
					isValueMatch = false;
					strUniqueKey = (String)mapDesignParameter.get(KEY_UNIQUEKEY);
					strParameterValue = (String)mapDesignParameter.get(KEY_DESIGN_PARAMETER_VALUES);					
					for(int i=0;i<mlPreviousPartFinalList.size();i++) 
					{	
						mapPreviousDesignParameter = (Map)mlPreviousPartFinalList.get(i);
						if(mapPreviousDesignParameter.containsKey(KEY_UNIQUEKEY))
						{
							strPreviousUniqueKey = (String)mapPreviousDesignParameter.get(KEY_UNIQUEKEY);
							if(strUniqueKey.equalsIgnoreCase(strPreviousUniqueKey))
							{
								isParameterFound = true;
								strPreviousParameterValue = (String)mapPreviousDesignParameter.get(KEY_DESIGN_PARAMETER_VALUES);
								if(strParameterValue.equalsIgnoreCase(strPreviousParameterValue))
								{
									isValueMatch = true;
								}								
							}
						}						
					}
					if(!isValueMatch && isParameterFound)
					{
						mapDesignParameter.put(KEY_CHG, RANGE_VALUE_CHG_C);
					}
					else if(!isParameterFound)
					{
						mapDesignParameter.put(KEY_CHG, RANGE_VALUE_CHG_CPLUS);
					}
				}
				
			}
		}
		return mlFinalList;
	}
	public static JsonObject convertAttributeMapToJson ( Map attributeDetailsMap, StringList strGroupSequenceList) {		
		JsonObjectBuilder returnJson = Json.createObjectBuilder();
		Iterator strGroupSequenceListItr =strGroupSequenceList.iterator();
		MapList attributeMapList;
		JsonArray attributeArray;
		String strGroupName;
		while(strGroupSequenceListItr.hasNext()) {
			strGroupName =(String)strGroupSequenceListItr.next();
			attributeMapList=(MapList)attributeDetailsMap.get(strGroupName);
			attributeMapList.sort("attributeSortId", "ascending", "integer");
			attributeArray =ApolloWidgetServiceUtil.convertMapListToJsonArray(attributeMapList);
			returnJson.add(strGroupName, attributeArray);
			
		}
		return returnJson.build();
		
	}
	
	
	/**
	 * Method to get Perf Char Parameter List
	 * @param mapXMLParameter
	 * @return 
	 */
	public static MapList getPerfCharList(Map mapXMLParameter)
	{
		MapList mlFinalList = new MapList();
		
		Map<String, String> mapPerformanceCharParameter;
		StringList slParamDetails;

		String sPlyGroupName;
		String sCategorySpecifics;
		String sCharacteristic;
		String sCharacteristicSpecifics;
		String sParameterName;
		String sParameterValue;
				
		StringBuilder sbUniqueParameterKey;
		String sUniqueParameterKey;
		
		Map mapParam;

		if(null != mapXMLParameter && mapXMLParameter.containsKey(pgApolloConstants.STR_PERFORMANCE_CHAR))
		{			
			mapPerformanceCharParameter = (Map)mapXMLParameter.get(pgApolloConstants.STR_PERFORMANCE_CHAR);	

			if(null !=mapPerformanceCharParameter && !mapPerformanceCharParameter.isEmpty())
			{
				Set<String> setParameterKeys = mapPerformanceCharParameter.keySet();

				for(String sParameterKey : setParameterKeys)
				{
					slParamDetails = StringUtil.split(sParameterKey, pgApolloConstants.CONSTANT_STRING_DOT);

					if(!slParamDetails.isEmpty())
					{
						int iParamListSize = slParamDetails.size();
						
						if(iParamListSize < 6 && iParamListSize >= 4)
						{
							sPlyGroupName = slParamDetails.get(0);
							sCategorySpecifics = slParamDetails.get(1);
							sCharacteristic = slParamDetails.get(2);
							if(iParamListSize >= 5)
							{
								sCharacteristicSpecifics = slParamDetails.get(3);
								sParameterName =  slParamDetails.get(4);
							}
							else
							{
								sCharacteristicSpecifics = pgApolloConstants.CONSTANT_STRING_HYPHEN;
								sParameterName =  slParamDetails.get(3);
							}					
							sbUniqueParameterKey = new StringBuilder();
							sbUniqueParameterKey.append(sPlyGroupName);
							sbUniqueParameterKey.append(pgApolloConstants.CONSTANT_STRING_DOT).append(sCategorySpecifics);
							sbUniqueParameterKey.append(pgApolloConstants.CONSTANT_STRING_DOT).append(sCharacteristic);
							if(UIUtil.isNotNullAndNotEmpty(sCharacteristicSpecifics))
							{
								sbUniqueParameterKey.append(pgApolloConstants.CONSTANT_STRING_DOT).append(sCharacteristicSpecifics);
							}
							sbUniqueParameterKey.append(pgApolloConstants.CONSTANT_STRING_DOT).append(sParameterName);

							sParameterValue = mapPerformanceCharParameter.get(sParameterKey);

							sUniqueParameterKey = sbUniqueParameterKey.toString();			
							
							
							mapParam = new HashMap();
							mapParam.put(pgApolloConstants.STR_CATEGORY, sPlyGroupName);
							mapParam.put(pgApolloConstants.KEY_CATEGORY_SPECIFIC, sCategorySpecifics);				
							mapParam.put(pgApolloConstants.STR_CHARACTERISTICS, sCharacteristic);
							mapParam.put(pgApolloConstants.STR_CHARACTERISTICSPECIFICS,sCharacteristicSpecifics);
							mapParam.put(pgApolloConstants.KEY_DESIGN_PARAMETER,sParameterName);
							mapParam.put(pgApolloConstants.KEY_DESIGN_PARAMETER_VALUES,sParameterValue);
							mapParam.put(pgApolloConstants.KEY_UNIQUEKEY, sUniqueParameterKey);
							mlFinalList.add(mapParam);
							
						}
						
					}

				}
			}
		}
		
		return mlFinalList;
	}
	
	
	/**
	 * Method to merge Current and Previous list with to update values
	 * @param mlFinalList
	 * @param mlPreviousList
	 * @return
	 * @throws Exception 
	 */
	public static MapList updateDesignParamListWithPreviousCollaboration(MapList mlCurrentList, MapList mlPreviousList) throws Exception
	{
		MapList mlReturnList = new MapList();
		
		Map mapCurrent;
		Map mapPrevious;

		String sCurrentUniqueKey;
		String sPreviousUniqueKey;

		StringList slCurrentUniqueKeyList = new StringList();
		StringList slPreviousUniqueKeyList = new StringList();
		
		Map mapCurrentParameters = new HashMap();		
		Map mapPreviousParameters = new HashMap();

		for(Object objMap : mlCurrentList)
		{
			mapCurrent = (Map)objMap;
			sCurrentUniqueKey = (String)mapCurrent.get(pgApolloConstants.KEY_UNIQUEKEY);	
			slCurrentUniqueKeyList.add(sCurrentUniqueKey);
			mapCurrentParameters.put(sCurrentUniqueKey, mapCurrent);
		}
		
		for(Object objMap : mlPreviousList)
		{
			mapPrevious = (Map)objMap;
			sPreviousUniqueKey = (String)mapPrevious.get(pgApolloConstants.KEY_UNIQUEKEY);		
			slPreviousUniqueKeyList.add(sPreviousUniqueKey);
			mapPreviousParameters.put(sPreviousUniqueKey, mapPrevious);
		}

		Set<String> setUniqueList = new HashSet();
		setUniqueList.addAll(slCurrentUniqueKeyList);
		setUniqueList.addAll(slPreviousUniqueKeyList);
		
		StringList slUniqueList = new StringList();
		slUniqueList.addAll(setUniqueList);
				
		List<String> newParametersAdded = slCurrentUniqueKeyList.stream().filter(element -> !slPreviousUniqueKeyList.contains(element)).collect(Collectors.toList());
		List<String> parametersRemoved = slPreviousUniqueKeyList.stream().filter(element -> !slCurrentUniqueKeyList.contains(element)).collect(Collectors.toList());	
		
		String sPreviousValue;
			

		for(String sLocalUniqueKey : slUniqueList)
		{
			mapCurrent = (Map)mapCurrentParameters.get(sLocalUniqueKey);

			if(null != mapCurrent)
			{
				if(newParametersAdded.contains(sLocalUniqueKey))
				{
					mapCurrent.put(pgApolloConstants.STR_PREVIOUS_VALUE, pgApolloConstants.CONSTANT_STRING_HYPHEN);
				}
				else
				{
					mapPrevious = (Map)mapPreviousParameters.get(sLocalUniqueKey);
					sPreviousValue = (String)mapPrevious.get(pgApolloConstants.KEY_DESIGN_PARAMETER_VALUES);
					mapCurrent.put(pgApolloConstants.STR_PREVIOUS_VALUE, sPreviousValue);
				}
				
			}			
			else if(parametersRemoved.contains(sLocalUniqueKey)) 
			{
				mapPrevious = (Map)mapPreviousParameters.get(sLocalUniqueKey);
				if(null != mapPrevious)
				{
					sPreviousValue = (String)mapPrevious.get(pgApolloConstants.KEY_DESIGN_PARAMETER_VALUES);
					mapPrevious.put(pgApolloConstants.KEY_DESIGN_PARAMETER_VALUES, pgApolloConstants.CONSTANT_STRING_HYPHEN);
					mapPrevious.put(pgApolloConstants.STR_PREVIOUS_VALUE, sPreviousValue);
					
					mapCurrent = new HashMap();
					mapCurrent.putAll(mapPrevious);
				}				
			}

			mlReturnList.add(mapCurrent);
		}
		return mlReturnList;
	}
}

package com.pg.widgets.lpdAPP;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.dassault_systemes.enovia.characteristic.impl.CharacteristicServices;
import com.dassault_systemes.enovia.characteristic.interfaces.ENOCharacteristicFactory;
import com.dassault_systemes.enovia.characteristic.interfaces.ENOICharacteristic;
import com.dassault_systemes.enovia.characteristic.interfaces.ENOICharacteristicsUtil;
import com.dassault_systemes.enovia.characteristic.util.CharacteristicMasterConstants;
import com.dassault_systemes.parameter_interfaces.ParameterInterfacesServices;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.png.apollo.pgApolloCommonUtil;
import com.png.apollo.pgApolloConstants;
import com.png.apollo.dashboard.util.ApolloWidgetServiceUtil;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class PGLPDAPPCharacteristic extends pgApolloConstants{
	
	public static final String SELECT_ATTRIBUTE_EVALUATED_CRITERIA ="attribute["+ATTRIBUTE_EVALUATED_CRITERIA+"]";
	//Characteristic Widget
	public static final String CONSTANT_STRING_SELECT_FROMPHYSICALID = "].from.physicalid";
	public static final String CONSTANT_STRING_SELECT_TOMPHYSICALID = "].to.physicalid";
	public static final String CONSTANT_STRING_SELECT_FROMTYPE = "].from.type";
	public static final String CONSTANT_STRING_SELECT_TOPHYSICALID = "].to.physicalid";
	public static final String CONSTANT_STRING_SELECT_TOTYPE = "].to.type";
	public static final String SELECT_TMRD = new StringBuilder(pgApolloConstants.CONSTANT_STRING_SELECT_FROM).append(pgApolloConstants.RELATIONSHIP_CHARACTERISTIC_TEST_METHOD).append(pgApolloConstants.CONSTANT_STRING_SELECT_TOID).toString();
	public static final String SELECT_TM_NAME = new StringBuilder(pgApolloConstants.CONSTANT_STRING_SELECT_FROM).append(pgApolloConstants.RELATIONSHIP_CHARACTERISTIC_TEST_METHOD).append(pgApolloConstants.CONSTANT_STRING_SELECT_TONAME).toString();
	public static final String SELECT_TM_PHYSICALID = new StringBuilder(pgApolloConstants.CONSTANT_STRING_SELECT_FROM).append(pgApolloConstants.RELATIONSHIP_CHARACTERISTIC_TEST_METHOD).append(CONSTANT_STRING_SELECT_TOPHYSICALID).toString();
	public static final String SELECT_TM_TYPE = new StringBuilder(pgApolloConstants.CONSTANT_STRING_SELECT_FROM).append(pgApolloConstants.RELATIONSHIP_CHARACTERISTIC_TEST_METHOD).append(CONSTANT_STRING_SELECT_TOTYPE).toString();
	
	public static final String SELECT_TMRD_ID = new StringBuilder(pgApolloConstants.CONSTANT_STRING_SELECT_FROM).append(DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT).append(pgApolloConstants.CONSTANT_STRING_SELECT_TOID).toString();
	public static final String SELECT_TMRD_NAME = new StringBuilder(pgApolloConstants.CONSTANT_STRING_SELECT_FROM).append(DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT).append(pgApolloConstants.CONSTANT_STRING_SELECT_TONAME).toString();
	public static final String SELECT_TMRD_PHYSICALID = new StringBuilder(pgApolloConstants.CONSTANT_STRING_SELECT_FROM).append(DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT).append(CONSTANT_STRING_SELECT_TOPHYSICALID).toString();
	public static final String SELECT_TMRD_TYPE = new StringBuilder(pgApolloConstants.CONSTANT_STRING_SELECT_FROM).append(DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT).append(CONSTANT_STRING_SELECT_TOTYPE).toString();
	public static final String SELECT_CHAR_MASTER=new StringBuilder(pgApolloConstants.CONSTANT_STRING_SELECT_TO).append(pgApolloConstants.RELATIONSHIP_DERIVED_CHARACTERISTIC).append(CONSTANT_STRING_SELECT_FROMPHYSICALID).toString();
	
	public static final String SELECT_CRITERIA=new StringBuilder(pgApolloConstants.CONSTANT_STRING_SELECT_TO).append(pgApolloConstants.RELATIONSHIP_DERIVED_CHARACTERISTIC).append(CONSTANT_STRING_SELECT_CLOSE_BRACKET).append(CONSTANT_STRING_DOT).append(SELECT_ATTRIBUTE_EVALUATED_CRITERIA).toString();
	public static final String ATTRIBUTE_PLMPARAMDISPLAYUNIT =PropertyUtil.getSchemaProperty("attribute_PlmParamDisplayUnit");
	public static final String SELECT_ATTRIBUTE_PLMPARAMDISPLAYUNIT="attribute["+ATTRIBUTE_PLMPARAMDISPLAYUNIT+"]";
	
	public static JsonObject getAPPCharacteristic(matrix.db.Context context , String strAPPId)throws Exception{

		boolean bContextPushed=false;
		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonArray jsonArray = null;
		
		//Check is LPD APP
		Map programMap = new HashMap();
		programMap.put("objectId", strAPPId);
		programMap.put("objectType", TYPE_ASSEMBLED_PRODUCT_PART);
		String []args = JPO.packArgs(programMap);
		boolean isLPDAPP = (boolean)JPO.invoke(context, "pgDSMLayeredProductUtil", null, "isLayeredProductPart", args, Object.class);
		
		if(!isLPDAPP) {
			throw new MatrixException("Only LPD APP is supported");
		}
		
		//Fetch Dimension Display Values Map
		ENOICharacteristicsUtil charUtil = ENOCharacteristicFactory.getCharacteristicUtil(context);
	  	HashMap<String, StringList> mpDimensionDisplayVals =  charUtil.getDimensions(context);
		try {
			if(!bContextPushed)
			{					
				//Push Context is done to get the characteristic and  improve the performance of the Characteristic tab in the widget. In Enovia too if the user has access to the APP they already get access to the Characteristics
				ContextUtil.pushContext(context, PropertyUtil.getSchemaProperty(context, PERSON_USER_AGENT), null, context.getVault().getName());
				bContextPushed = true;
			}
			
			//Get EBOM 
			DomainObject appObj = DomainObject.newInstance(context , strAPPId);
			MapList childApp =appObj.getRelatedObjects(context,
					DomainConstants.RELATIONSHIP_EBOM,
					TYPE_ASSEMBLED_PRODUCT_PART,
					new StringList(DomainConstants.SELECT_ID),//Object Select
					null,//rel Select
					false,//get To
					true,//get From
					(short)1,//recurse level
					null,//where Clause
					null,
					0);	
			StringList appListIds =new StringList(childApp.size()+1);
			appListIds.add(strAPPId);
			Iterator<Map> childAppItr = childApp.iterator();
			Map appTempMap;
			while(childAppItr.hasNext()) {
				appTempMap =childAppItr.next();
				appListIds.add((String)appTempMap.get(DomainConstants.SELECT_ID));
				
			}

			String strCharAttributeList = pgApolloCommonUtil.getPageProperty(context, STR_APOLLO_CONFIG_PAGE_FILENAME, "pgApolloDashBoard.CHARACTERISTIC.AttributeList");
			StringList strSelectable = new StringList();
			if(null != strCharAttributeList && !strCharAttributeList.isEmpty())
			{
				strSelectable = StringUtil.split(strCharAttributeList, CONSTANT_STRING_PIPE);   
			}

			strSelectable.add(SELECT_TM_NAME);
			strSelectable.add(SELECT_TM_PHYSICALID);
			strSelectable.add(SELECT_TM_TYPE);
			strSelectable.add(SELECT_TMRD_NAME);
			strSelectable.add(SELECT_TMRD_PHYSICALID);
			strSelectable.add(SELECT_TMRD_TYPE);
			strSelectable.add(SELECT_CHAR_MASTER);
			strSelectable.add(SELECT_CRITERIA);
			

			DomainConstants.MULTI_VALUE_LIST.add(SELECT_TMRD);
			DomainConstants.MULTI_VALUE_LIST.add(SELECT_TM_NAME);
			DomainConstants.MULTI_VALUE_LIST.add(SELECT_TM_PHYSICALID);
			DomainConstants.MULTI_VALUE_LIST.add(SELECT_TMRD_ID);
			DomainConstants.MULTI_VALUE_LIST.add(SELECT_TMRD_NAME);
			DomainConstants.MULTI_VALUE_LIST.add(SELECT_TMRD_PHYSICALID);
			DomainConstants.MULTI_VALUE_LIST.add(SELECT_TM_TYPE);
			DomainConstants.MULTI_VALUE_LIST.add(SELECT_TMRD_TYPE);
			

			MapList appCharList = new MapList();
			for(String strTempAppID : appListIds) {
				appCharList.addAll(CharacteristicServices.getAssociatedCharactertisticsOnItem(context, strTempAppID, strSelectable, false));
			}
			Map mpCharData;
			Map mapCriteriaDetails = new HashMap();
			StringList slInterface;
			String sDimension;
			String sDimensionName;
			MapList appFinalCharList = new MapList();
			for(int j = 0 ; j < appCharList.size(); j++) {
				mpCharData = (Map) appCharList.get(j);
				filterCriteriaPhysicalIds(context, mpCharData, mapCriteriaDetails);				
				ENOICharacteristic characteristic = ENOCharacteristicFactory.getCharacteristicById(context, (String)mpCharData.get("physicalid"));
				mpCharData.put("LSL",characteristic.getLowerSpecificationLimit(context));
				mpCharData.put("LRRL",characteristic.getLowerRoutineReleaseLimit(context));
				mpCharData.put("LT",characteristic.getMinimalValue(context));
				mpCharData.put("Target",characteristic.getNominalValue(context));
				mpCharData.put("UT",characteristic.getMaximalValue(context));
				mpCharData.put("URRL",characteristic.getUpperRoutineReleaseLimit(context));
				mpCharData.put("USL",characteristic.getUpperSpecificationLimit(context));
				mpCharData.put(CharacteristicMasterConstants.PARAM_DISPLAY_UNIT,characteristic.getDisplayUnit());
				
				sDimension = (UIUtil.isNotNullAndNotEmpty(characteristic.getDimension()))?characteristic.getDimension():DomainConstants.EMPTY_STRING ;
				sDimensionName = ParameterInterfacesServices.getDimensionNLS(context, sDimension);				
				
				mpCharData.put(pgApolloConstants.STR_CHAR_DIMENSION, sDimensionName);
				appFinalCharList.add(mpCharData);
			}
			
			
			appFinalCharList.sort(SELECT_ATTRIBUTE_REPORT_TYPE, "descending", "string");
			appFinalCharList.sort(SELECT_ATTRIBUTE_PG_CHARACTERISTIC_CATEGORY, "ascending", "string");
			jsonArray =ApolloWidgetServiceUtil.convertMapListToJasonArray(context ,appFinalCharList,getDragableColumnMapForCharacteristic());
		}

		finally {
			DomainConstants.MULTI_VALUE_LIST.remove(SELECT_TMRD);
			DomainConstants.MULTI_VALUE_LIST.remove(SELECT_TM_NAME);
			DomainConstants.MULTI_VALUE_LIST.remove(SELECT_TM_PHYSICALID);
			DomainConstants.MULTI_VALUE_LIST.remove(SELECT_TMRD_ID);
			DomainConstants.MULTI_VALUE_LIST.remove(SELECT_TMRD_NAME);
			DomainConstants.MULTI_VALUE_LIST.remove(SELECT_TMRD_PHYSICALID);
			DomainConstants.MULTI_VALUE_LIST.remove(SELECT_TM_TYPE);
			DomainConstants.MULTI_VALUE_LIST.remove(SELECT_TMRD_TYPE);
			DomainConstants.MULTI_VALUE_LIST.remove("interface");
			if(bContextPushed)
			{
				ContextUtil.popContext(context);
				bContextPushed = false;
			}
		}


		output.add("APPCharacteristic", jsonArray);
		return output.build();

	}

	
	/**
	 * Method to filter Criteria Physical Ids
	 * @param context 
	 * @param mpCharData
	 * @param mapCriteriaDetails
	 * @throws FrameworkException 
	 */
	public static void filterCriteriaPhysicalIds(Context context, Map mpCharData, Map mapCriteriaDetails) throws FrameworkException 
	{
		StringList slCriteriaPhysicalIdList = new StringList();
		Object objectCriteriaPhysicalIdList;

		Map mapCriteriaInfo;
		Map mapFinalCriteriaInfo = new HashMap();
		String sObjectLogicalId;
		MapList mlCriteriaInfo;

		Map mapCriteria;
		String sObjectPhysicalId;
		String sCriteriaLocalPhysicalId;

		if(mpCharData.containsKey(SELECT_CRITERIA))
		{
			objectCriteriaPhysicalIdList = mpCharData.get(SELECT_CRITERIA);
			if ((objectCriteriaPhysicalIdList instanceof StringList)) {
				slCriteriaPhysicalIdList = (StringList)objectCriteriaPhysicalIdList;
			}				
		}

		StringList slCriteriaPhysicalIdToBeFetched = new StringList();

		if(!slCriteriaPhysicalIdList.isEmpty())
		{
			for(String sCriteriaPhysicalId : slCriteriaPhysicalIdList)
			{
				if(!mapCriteriaDetails.containsKey(sCriteriaPhysicalId))
				{
					slCriteriaPhysicalIdToBeFetched.add(sCriteriaPhysicalId);
				}
			}

			if(!slCriteriaPhysicalIdToBeFetched.isEmpty())
			{
				StringList slObjectSelects = new StringList();
				slObjectSelects.add(DomainConstants.SELECT_REVISION);
				slObjectSelects.add(SELECT_LOGICAL_ID);
				slObjectSelects.add(SELECT_PHYSICAL_ID);

				MapList mlCriteriaFetched = DomainObject.getInfo(context, slCriteriaPhysicalIdToBeFetched.toArray(new String[slCriteriaPhysicalIdToBeFetched.size()]), slObjectSelects);
				for(int n = 0; n < mlCriteriaFetched.size(); n++)
				{
					mapCriteria = (Map) mlCriteriaFetched.get(n);
					sObjectPhysicalId = (String) mapCriteria.get(SELECT_PHYSICAL_ID);
					mapCriteriaDetails.put(sObjectPhysicalId, mapCriteria);
				}
			}		

			for(String sCriteriaPhysicalId : slCriteriaPhysicalIdList)
			{
				if(mapCriteriaDetails.containsKey(sCriteriaPhysicalId))
				{
					mapCriteriaInfo = (Map)mapCriteriaDetails.get(sCriteriaPhysicalId);
					sObjectLogicalId = (String)mapCriteriaInfo.get(SELECT_LOGICAL_ID);

					mlCriteriaInfo = new MapList();
					if(mapFinalCriteriaInfo.containsKey(sObjectLogicalId))
					{
						mlCriteriaInfo = (MapList)mapFinalCriteriaInfo.get(sObjectLogicalId);
					}
					mlCriteriaInfo.add(mapCriteriaInfo);
					mapFinalCriteriaInfo.put(sObjectLogicalId, mlCriteriaInfo);
				}
			}

			Map mapLocalCriteriaMap;
			StringList slLocalCriteriaPhysicalIdList = new StringList();

			for(String sCriteriaPhysicalId : slCriteriaPhysicalIdList)
			{
				mapCriteriaInfo = (Map) mapCriteriaDetails.get(sCriteriaPhysicalId);
				sObjectLogicalId = (String)mapCriteriaInfo.get(SELECT_LOGICAL_ID);
				if(mapFinalCriteriaInfo.containsKey(sObjectLogicalId))
				{
					mlCriteriaInfo = (MapList)mapFinalCriteriaInfo.get(sObjectLogicalId);					
					if(!mlCriteriaInfo.isEmpty())
					{
						mlCriteriaInfo.sort(DomainConstants.SELECT_REVISION, pgApolloConstants.STR_DESCENDING, "integer");
						mapLocalCriteriaMap = (Map)mlCriteriaInfo.get(0);
						sCriteriaLocalPhysicalId = (String)mapLocalCriteriaMap.get(SELECT_PHYSICAL_ID);	
						if(!slLocalCriteriaPhysicalIdList.contains(sCriteriaLocalPhysicalId))
						{
							slLocalCriteriaPhysicalIdList.add(sCriteriaLocalPhysicalId);						
						}
					}

				}
			}			
			mpCharData.put(SELECT_CRITERIA, slLocalCriteriaPhysicalIdList);
		}
	}
	
	private static String identifyDimensionUsingInterface(StringList slInterfaceList, Map<String, StringList> mpDimensionDisplayVals) {
		String sReturnDimension = DomainConstants.EMPTY_STRING;
		
		
	  	
	  	Map mpDimensionNames;
	  	if(mpDimensionDisplayVals.size() > 0 ) {
	  		mpDimensionNames = prepareMapForName(mpDimensionDisplayVals);
	  		
	  		if(!mpDimensionNames.isEmpty()) {
		  		String sValue;
			  	for(int i = 0 ; i < slInterfaceList.size(); i++) {
			  		sValue = slInterfaceList.get(i);
			  		if(UIUtil.isNotNullAndNotEmpty(sValue) && mpDimensionNames.containsKey(sValue)) {
			  			sReturnDimension = (String) mpDimensionNames.get(sValue);
			  			break;
			  		}
			  	}
		  	}
	  	}
		return sReturnDimension;
	}
	
	public static Map prepareMapForName(Map<String, StringList> mpReturnMap) {
		HashMap<String,String> mpDisplayName = new HashMap<>();
		StringList slActual = mpReturnMap.get("field_choices");
		StringList slDisplay = mpReturnMap.get("field_display_choices"); 
		for(int i = 0 ; i < slActual.size(); i++ ) {
			mpDisplayName.put(slActual.get(i),slDisplay.get(i) );
		}
		return mpDisplayName;
	}
	
	private static Map getDragableColumnMapForCharacteristic() {
		Map dragableColumnMap = new HashMap<String, Map>();
		Map dragableColumnKeyMap = new HashMap<String, String>();
		dragableColumnKeyMap.put(DomainConstants.SELECT_NAME,SELECT_TM_NAME);
		dragableColumnKeyMap.put(pgApolloConstants.SELECT_PHYSICAL_ID,SELECT_TM_PHYSICALID);
		dragableColumnKeyMap.put(DomainConstants.SELECT_TYPE,SELECT_TM_TYPE);
		dragableColumnMap.put("TestMethod", dragableColumnKeyMap);
		
		
		dragableColumnKeyMap = new HashMap<String, String>();
		dragableColumnKeyMap.put(DomainConstants.SELECT_NAME,SELECT_TMRD_NAME);
		dragableColumnKeyMap.put(pgApolloConstants.SELECT_PHYSICAL_ID,SELECT_TMRD_PHYSICALID);
		dragableColumnKeyMap.put(DomainConstants.SELECT_TYPE,SELECT_TMRD_TYPE);
		dragableColumnMap.put("TMRD", dragableColumnKeyMap);
		
		dragableColumnKeyMap = new HashMap<String, String>();
		dragableColumnKeyMap.put(pgApolloConstants.SELECT_PHYSICAL_ID,SELECT_CHAR_MASTER);
		dragableColumnMap.put("CM", dragableColumnKeyMap);
		
		dragableColumnKeyMap = new HashMap<String, String>();
		dragableColumnKeyMap.put(pgApolloConstants.SELECT_PHYSICAL_ID,SELECT_CRITERIA);
		dragableColumnMap.put("Criteria", dragableColumnKeyMap);
		
		return dragableColumnMap;
	}

}

package com.png.apollo.dashboard.productspecification.dashboardutil;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dassault_systemes.enovia.characteristic.interfaces.ENOCharacteristicFactory;
import com.dassault_systemes.enovia.characteristic.interfaces.ENOICharacteristic;
import com.dassault_systemes.enovia.characteristic.util.CharacteristicMasterConstants;
import com.dassault_systemes.enovia.criteria.interfaces.ENOCriteriaEnum;
import com.dassault_systemes.enovia.criteria.interfaces.ENOCriteriaFactory;
import com.dassault_systemes.enovia.criteria.interfaces.ENOICriteria;
import com.dassault_systemes.knowledge_itfs.IKweDictionary;
import com.dassault_systemes.knowledge_itfs.IKweUnit;
import com.dassault_systemes.knowledge_itfs.KweInterfacesServices;
import com.dassault_systemes.parameter_interfaces.ParameterInterfacesServices;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.CacheUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.png.apollo.pgApolloCommonUtil;
import com.png.apollo.pgApolloConstants;
import com.png.integ.designtool.getData.UpdateRangeForCATIAAttributes;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.db.Page;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class CharacteristicsDashboardUtill {

	static  String strCOMPARE_RESULT_COLS = "";
	static  String strCOMPARE_RESULT_KEYS = "";
	static final String STR_OTHER_COMPARE_ATTRIBUTES = "Sampling,Subgroup,Dimension,UOM,ReportType,TMRD,LSL,LRRL,URRL,USL,MinVal,MaxVal,Target"
			+ ",CMTitle,TestMethod,TestMethodSpecifics,ReporttoNearest,ActionRequired,CriticalityFactor,Application,CharaDesc,Notes,Criteria"
			+ ",BusinessArea,ProductCategoryPlatform,ReasonForChange";
	static final String STR_PROCESSED = "Processed";
	static final String STR_CC = "Category";
	static final String STR_DS = "CategorySpecific";//APOLLO 2018x.6 A10-926 - New attribute on Characteristic for Category Specifics - starts
	static final String STR_CS="CharSpec";
	static final String STR_CHAR="Characteristics";
	static final String STR_CM_SELECTABLES = "name|attribute[Title]|from[ParameterAggregation].to.attribute[Characteristic Category]|from[ParameterAggregation].to.attribute[pgDesignSpecifics]|Title|from[ParameterAggregation].to.attribute[pgCharacteristicSpecifics]|attribute["+pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGBUSINESSAREA+"]|attribute["+pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGPRODUCTCATEGORYPLATFORM+"]|testMethods|from[ParameterAggregation].to.attribute[pgMethodSpecifics]|cm_tmrd|from[ParameterAggregation].to.attribute[pgSampling]|from[ParameterAggregation].to.attribute[pgSubGroup]|Display Unit|from[ParameterAggregation].to.attribute[pgReportToNearest]|from[ParameterAggregation].to.attribute[pgReportType]|from[ParameterAggregation].to.attribute[pgActionRequired]|from[ParameterAggregation].to.attribute[pgCriticalityFactor]|from[ParameterAggregation].to.attribute[pgApplication]|from[ParameterAggregation].to.description|from[ParameterAggregation].to.attribute[Characteristic Notes]|cm_criteria|tmids|cm_tmrdids|criteriaids|"+pgApolloConstants.SELECT_ATTRIBUTE_REASON_FOR_CHANGE;
	static final String STR_CM_SELECTABLES_KEYS = "Name|CMTitle|Category|DesignSpecific|Characteristics|CharSpec|BusinessArea|ProductCategoryPlatform|TestMethod|TestMethodSpecifics|TMRD|Sampling|Subgroup|UOM|ReporttoNearest|ReportType|ActionRequired|CriticalityFactor|Application|CharaDesc|Notes|Criteria|tmids|tmrdids|CriteriaIDs|ReasonForChange";
	static final String STR_STATUS1= "status1";
	static final String STR_STATUS2= "status2";
	static final String STR_APOLLO_WS_TRACE= "apollows";	
	static final String STR_GET_ATTR_OBJECT = "strgetAttrObject";
	static final String STR_SELECTED_PLATFORM_LIST = "selectedPlatformList";
	static final String STR_SELECTED_PLATFORM_PCP_LIST = "selectedPlatformListPCP";
	static final String STR_INTERFACE = "interface";
	static final String STR_IDENTICAL = "Identical";
	static final String CONSTANT_STRING_SELECT_TOATTRIBUTE = "].to.attribute[";
	static final String CONSTANT_STRING_SELECT_TORELATIONSHIP = "].to.relationship[";
	
	static final String MAX_STRING_LENGTH = "org.apache.johnzon.max-string-length";
	static final String VALUE_KILOBYTES = "262144";

	static String strCMW_COL_KEYS = "";
	
	static final String STR_PLMPARAM_SELECTS = DomainConstants.SELECT_ID+pgApolloConstants.CONSTANT_STRING_PIPE+pgApolloConstants.SELECT_ATTRIBUTE_REPORT_TO_NEAREST+pgApolloConstants.CONSTANT_STRING_PIPE+pgApolloConstants.SELECT_ATTRIBUTE_PGMETHODSPECIFICS+pgApolloConstants.CONSTANT_STRING_PIPE+pgApolloConstants.SELECT_ATTRIBUTE_PGTMLOGIC+pgApolloConstants.CONSTANT_STRING_PIPE+pgApolloConstants.SELECT_ATTRIBUTE_PGREPORTTYPE+pgApolloConstants.CONSTANT_STRING_PIPE+pgApolloConstants.SELECT_ATTRIBUTE_PG_CATEGORYSPECIFICS+pgApolloConstants.CONSTANT_STRING_PIPE+pgApolloConstants.SELECT_ATTRIBUTE_PG_DESIGNSPECIFICS+pgApolloConstants.CONSTANT_STRING_PIPE+pgApolloConstants.SELECT_ATTRIBUTE_PGCHARSPECIFICS+pgApolloConstants.CONSTANT_STRING_PIPE+pgApolloConstants.SELECT_ATTRIBUTE_PGACTIONREQUIRED+pgApolloConstants.CONSTANT_STRING_PIPE+pgApolloConstants.SELECT_ATTRIBUTE_PGSAMPLING+pgApolloConstants.CONSTANT_STRING_PIPE+pgApolloConstants.SELECT_ATTRIBUTE_PGCRITICALITYFACTOR+pgApolloConstants.CONSTANT_STRING_PIPE+pgApolloConstants.SELECT_ATTRIBUTE_PGTESTGROUP+pgApolloConstants.CONSTANT_STRING_PIPE+pgApolloConstants.SELECT_ATTRIBUTE_PGSUBGROUP+pgApolloConstants.CONSTANT_STRING_PIPE+pgApolloConstants.SELECT_ATTRIBUTE_PG_APPLICATION+pgApolloConstants.CONSTANT_STRING_PIPE+pgApolloConstants.SELECT_ATTRIBUTE_CHARACTERISTIC_CATEGORY+pgApolloConstants.CONSTANT_STRING_PIPE+pgApolloConstants.SELECT_ATTRIBUTE_CHARACTERISTIC_NOTES+pgApolloConstants.CONSTANT_STRING_PIPE+pgApolloConstants.SELECT_ATTRIBUTE_PLMPARAMDISPLAYUNIT+pgApolloConstants.CONSTANT_STRING_PIPE+DomainConstants.SELECT_ATTRIBUTE_TITLE+pgApolloConstants.CONSTANT_STRING_PIPE+DomainConstants.SELECT_DESCRIPTION;
	
	private CharacteristicsDashboardUtill() {
		
	}
	/**
	 * @param context
	 * @return Unique CM MapList 
	 * @throws Exception
	 */	
	public static MapList getReleasedCriterias(Context context) throws Exception {
		context.printTrace(STR_APOLLO_WS_TRACE, "Method ::: getReleasedCriterias starts");
		MapList mlCMList = new MapList();
		HashMap paramMap = new HashMap();
		paramMap.put("MyDeskCriteriaCustomFilter", DomainConstants.EMPTY_STRING);
		String[] methodargs = JPO.packArgs(paramMap);
		MapList mlCriteriasList = JPO.invoke(context, "ENOCriteriaUIBase", null, "getMyDeskCriterias", methodargs,
				MapList.class);

		if (mlCriteriasList != null && !mlCriteriasList.isEmpty()) {
			mlCMList = getCharacteristicsMastersForCriteria(context, mlCriteriasList,true);
		}
		context.printTrace(STR_APOLLO_WS_TRACE, "Method ::: getReleasedCriterias ends");
		return mlCMList;
	}	
	 
	/**
	 * @param context
	 * @param mlCriteriasList
	 * @return Unique CM MapList for Criteria
	 * @throws Exception
	 * @NOTE: This method logic is cloned from JPO:ENOCharacteristicMasterUIBase Method:getMyDeskCharacteristicMasters
	 */
	public static MapList getCharacteristicsMastersForCriteria(Context context, MapList mlCriteriasList,boolean bFlag)
			throws Exception{
		context.printTrace(STR_APOLLO_WS_TRACE, "Method ::: getCharacteristicsMastersForCriteria starts");		
		
		MapList mlCharacteristicsMasters = new MapList();
		Map mapInfo = null;
		HashMap paramMap = null;
		String sCriteriaId = null;
		String sCMId = null;
		String sObjId = null;
		String sCriteriaList = null;
		String sParamId = null;
		String sDimension = null;
		StringList slCMIDList = new StringList();
		StringList slCriteriaList = null;
		StringList slCriteriaReleased = null;
		StringList slCriteriaName = null;
		StringList slCriteriaState = null;
		StringList slTM = null;
		StringList slCriteriaId = null;
		StringList slCharacteristicIds = null;		
		
		Map<String, Object> consolidatedMap = null;
		Map cmMap = null;
		Object obj = null;
		Object objId = null;
		Object objTM = null;
		Object objState = null;
		String sCriteriaState ;
		String sCMCurrent;

		MapList mlCharacteristicMasters = null;
		StringList objSelects = new StringList(DomainConstants.SELECT_ID);
		objSelects.add(DomainConstants.SELECT_ATTRIBUTE_TITLE);
		objSelects.add(DomainConstants.SELECT_NAME);
		objSelects.add(DomainConstants.SELECT_REVISION);
		objSelects.add(DomainConstants.SELECT_DESCRIPTION);
		objSelects.add(DomainConstants.SELECT_CURRENT);
		objSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_REASON_FOR_CHANGE);
		objSelects.add("attribute["+pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGBUSINESSAREA+"]");
		objSelects.add("attribute["+pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGPRODUCTCATEGORYPLATFORM+"]");
	  	objSelects.add(pgApolloConstants.CONSTANT_STRING_SELECT_FROM+pgApolloConstants.RELATIONSHIP_PARAMETER_AGGREGATION+"].to.attribute[Characteristic Category]");
	  	objSelects.add(pgApolloConstants.CONSTANT_STRING_SELECT_FROM+pgApolloConstants.RELATIONSHIP_PARAMETER_AGGREGATION+"].to.attribute[Characteristic Notes]");
	  	objSelects.add(pgApolloConstants.CONSTANT_STRING_SELECT_FROM+pgApolloConstants.RELATIONSHIP_PARAMETER_AGGREGATION+CONSTANT_STRING_SELECT_TOATTRIBUTE+pgApolloConstants.ATTRIBUTE_PG_DESIGNSPECIFICS+"]");
	  	objSelects.add(pgApolloConstants.CONSTANT_STRING_SELECT_FROM+pgApolloConstants.RELATIONSHIP_PARAMETER_AGGREGATION+CONSTANT_STRING_SELECT_TOATTRIBUTE+pgApolloConstants.ATTRIBUTE_PG_CATEGORYSPECIFICS+"]"); //APOLLO 2018x.6 A10-926 - New attribute on Characteristic for Category Specifics
	  	objSelects.add(pgApolloConstants.CONSTANT_STRING_SELECT_FROM+pgApolloConstants.RELATIONSHIP_PARAMETER_AGGREGATION+CONSTANT_STRING_SELECT_TOATTRIBUTE+pgApolloConstants.ATTRIBUTE_PG_APPLICATION+"]");
	  	objSelects.add(pgApolloConstants.CONSTANT_STRING_SELECT_FROM+pgApolloConstants.RELATIONSHIP_PARAMETER_AGGREGATION+CONSTANT_STRING_SELECT_TOATTRIBUTE+pgApolloConstants.ATTRIBUTE_PGSAMPLING+"]");
	  	objSelects.add(pgApolloConstants.CONSTANT_STRING_SELECT_FROM+pgApolloConstants.RELATIONSHIP_PARAMETER_AGGREGATION+CONSTANT_STRING_SELECT_TOATTRIBUTE+pgApolloConstants.ATTRIBUTE_PGSUBGROUP+"]");
	  	objSelects.add(pgApolloConstants.CONSTANT_STRING_SELECT_FROM+pgApolloConstants.RELATIONSHIP_PARAMETER_AGGREGATION+CONSTANT_STRING_SELECT_TOATTRIBUTE+pgApolloConstants.ATTRIBUTE_PGREPORTTONEAREST+"]");
	  	objSelects.add(pgApolloConstants.CONSTANT_STRING_SELECT_FROM+pgApolloConstants.RELATIONSHIP_PARAMETER_AGGREGATION+CONSTANT_STRING_SELECT_TOATTRIBUTE+pgApolloConstants.ATTRIBUTE_REPORT_TYPE+"]");
	  	objSelects.add(pgApolloConstants.CONSTANT_STRING_SELECT_FROM+pgApolloConstants.RELATIONSHIP_PARAMETER_AGGREGATION+CONSTANT_STRING_SELECT_TOATTRIBUTE+pgApolloConstants.ATTRIBUTE_PGACTIONREQUIRED+"]");
	  	objSelects.add(pgApolloConstants.CONSTANT_STRING_SELECT_FROM+pgApolloConstants.RELATIONSHIP_PARAMETER_AGGREGATION+CONSTANT_STRING_SELECT_TOATTRIBUTE+pgApolloConstants.ATTRIBUTE_PGCRITICALITYFACTOR+"]");
	  	objSelects.add(pgApolloConstants.CONSTANT_STRING_SELECT_FROM+pgApolloConstants.RELATIONSHIP_PARAMETER_AGGREGATION+CONSTANT_STRING_SELECT_TOATTRIBUTE+pgApolloConstants.ATTRIBUTE_CHARACTERISTICSPECIFIC+"]");
	  	objSelects.add(pgApolloConstants.CONSTANT_STRING_SELECT_FROM+pgApolloConstants.RELATIONSHIP_PARAMETER_AGGREGATION+CONSTANT_STRING_SELECT_TOATTRIBUTE+pgApolloConstants.ATTRIBUTE_PGMETHODSPECIFICS+"]");
	  	objSelects.add(pgApolloConstants.CONSTANT_STRING_SELECT_FROM+pgApolloConstants.RELATIONSHIP_PARAMETER_AGGREGATION+CONSTANT_STRING_SELECT_TORELATIONSHIP+pgApolloConstants.RELATIONSHIP_CHARACTERISTIC_TEST_METHOD+pgApolloConstants.CONSTANT_STRING_SELECT_TONAME);
	  	objSelects.add(pgApolloConstants.CONSTANT_STRING_SELECT_FROM+pgApolloConstants.RELATIONSHIP_PARAMETER_AGGREGATION+"].to.attribute[Obscure Unit of Measure]");
	  	objSelects.add(pgApolloConstants.CONSTANT_STRING_SELECT_FROM+pgApolloConstants.RELATIONSHIP_PARAMETER_AGGREGATION+"].to.attribute[PlmParamDisplayUnit]");
	  	objSelects.add(pgApolloConstants.CONSTANT_STRING_SELECT_FROM+pgApolloConstants.RELATIONSHIP_PARAMETER_AGGREGATION+"].to."+DomainConstants.SELECT_DESCRIPTION);
	  	objSelects.add(pgApolloConstants.CONSTANT_STRING_SELECT_FROM+pgApolloConstants.RELATIONSHIP_PARAMETER_AGGREGATION+"].to."+DomainConstants.SELECT_ID);
	  	objSelects.add(pgApolloConstants.CONSTANT_STRING_SELECT_TO.concat(ENOCriteriaEnum.Relationship.CRITERIA_OUTPUT.get(context).concat(pgApolloConstants.CONSTANT_STRING_SELECT_FROMDOT).concat(DomainConstants.SELECT_ID)));
	  	objSelects.add(pgApolloConstants.CONSTANT_STRING_SELECT_TO.concat(ENOCriteriaEnum.Relationship.CRITERIA_OUTPUT.get(context).concat(pgApolloConstants.CONSTANT_STRING_SELECT_FROMDOT).concat(DomainConstants.SELECT_NAME)));
	  	objSelects.add(pgApolloConstants.CONSTANT_STRING_SELECT_TO.concat(ENOCriteriaEnum.Relationship.CRITERIA_OUTPUT.get(context).concat(pgApolloConstants.CONSTANT_STRING_SELECT_FROMDOT).concat(DomainConstants.SELECT_CURRENT)));
	  	objSelects.add(CharacteristicMasterConstants.MASTER_TO_CHARACTERISTIC_SELECTABLE+DomainConstants.SELECT_ID);

	  	
	  	String sDimensionDisplayName;
	  	String sCharUOM;


		for (int i = 0; i < mlCriteriasList.size(); i++) {
			mapInfo = (Map) mlCriteriasList.get(i);
			paramMap = new HashMap();
			sCriteriaId = (String) mapInfo.get("id");
			paramMap.put("objectId", sCriteriaId);

			// Get CM Table Data For Dashboard Start
			ENOICriteria iCriteria = ENOCriteriaFactory.getCriteriaById(context, sCriteriaId);
			mlCharacteristicMasters = iCriteria.getCriteriaOutput(context, objSelects, DomainConstants.EMPTY_STRING);

			slCharacteristicIds = new StringList();
			for(int c = 0; c < mlCharacteristicMasters.size(); c++) {
				cmMap = (Map) mlCharacteristicMasters.get(c);
				sCMCurrent = (String)cmMap.get(DomainConstants.SELECT_CURRENT);
				if(UIUtil.isNotNullAndNotEmpty(sCMCurrent) && sCMCurrent.equals(pgApolloConstants.STATE_RELEASED)) {
					slCharacteristicIds.add((String)cmMap.get(CharacteristicMasterConstants.MASTER_TO_CHARACTERISTIC_SELECTABLE+CharacteristicMasterConstants.SELECT_ID));
				}
			}
			StringList slENOICharSelects = new StringList();
			slENOICharSelects.add(DomainConstants.SELECT_ID);
			slENOICharSelects.add(DomainConstants.SELECT_TYPE);
			slENOICharSelects.add(DomainConstants.SELECT_NAME);
			slENOICharSelects.add(DomainConstants.SELECT_REVISION);
			slENOICharSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_OBSCUREUNITOFMEASURE);
			List<ENOICharacteristic> listChar = com.dassault_systemes.enovia.characteristic.impl.CharacteristicServices.getCharacteristicDetails(context, slCharacteristicIds, slENOICharSelects);
				for(ENOICharacteristic enoChar: listChar)	{		
					consolidatedMap = new HashMap<>();
				sObjId =  enoChar.getId(context);
				Iterator mlIter = mlCharacteristicMasters.iterator();
				while (mlIter.hasNext()) {
					slCriteriaList = new StringList();
					slCriteriaId = new StringList();
					slCriteriaState = new StringList();
					slTM = new StringList();
					slCriteriaReleased = new StringList();
					slCriteriaName = new StringList();
					Map<String, Object> mpCharacteristicMaster = (Map<String, Object>) mlIter.next();
					sParamId = (String)mpCharacteristicMaster.get(pgApolloConstants.CONSTANT_STRING_SELECT_FROM+pgApolloConstants.RELATIONSHIP_PARAMETER_AGGREGATION+pgApolloConstants.CONSTANT_STRING_SELECT_TOID);
					
					objId = mpCharacteristicMaster.get(pgApolloConstants.CONSTANT_STRING_SELECT_TO.concat(ENOCriteriaEnum.Relationship.CRITERIA_OUTPUT.get(context).concat(pgApolloConstants.CONSTANT_STRING_SELECT_FROMDOT).concat(DomainConstants.SELECT_ID)));
					if (objId instanceof String) {
						slCriteriaId.addElement((String)objId);
					}else if (objId instanceof StringList) {
						slCriteriaId.addAll((StringList) objId);
					}
					objState = mpCharacteristicMaster.get(pgApolloConstants.CONSTANT_STRING_SELECT_TO.concat(ENOCriteriaEnum.Relationship.CRITERIA_OUTPUT.get(context).concat(pgApolloConstants.CONSTANT_STRING_SELECT_FROMDOT).concat(DomainConstants.SELECT_CURRENT)));
					if (objState instanceof String) {
						slCriteriaState.addElement((String)objState);
					}else if (objState instanceof StringList) {
						slCriteriaState.addAll((StringList) objState);
					}
					
					obj = mpCharacteristicMaster.get(pgApolloConstants.CONSTANT_STRING_SELECT_TO.concat(ENOCriteriaEnum.Relationship.CRITERIA_OUTPUT.get(context).concat(pgApolloConstants.CONSTANT_STRING_SELECT_FROMDOT).concat(DomainConstants.SELECT_NAME)));
					if (obj instanceof String) {
						slCriteriaList.addElement((String)obj);
					}else if (obj instanceof StringList) {
						slCriteriaList.addAll((StringList) obj);
					}
					for(int j=0; j<slCriteriaState.size();j++) {
						sCriteriaState = slCriteriaState.get(j);
						if(sCriteriaState.equalsIgnoreCase(pgApolloConstants.STATE_RELEASED)) {
							slCriteriaReleased.add(slCriteriaList.get(j) + "#" +slCriteriaId.get(j));
							slCriteriaName.add(slCriteriaList.get(j));
						}
					}
					objTM = mpCharacteristicMaster.get(pgApolloConstants.CONSTANT_STRING_SELECT_FROM+pgApolloConstants.RELATIONSHIP_PARAMETER_AGGREGATION+CONSTANT_STRING_SELECT_TORELATIONSHIP+pgApolloConstants.RELATIONSHIP_CHARACTERISTIC_TEST_METHOD+pgApolloConstants.CONSTANT_STRING_SELECT_TONAME);
					if (objTM instanceof String) {
						slTM.addElement((String)objTM);
					}else if (objTM instanceof StringList) {
						slTM.addAll((StringList) objTM);
					}
					sCriteriaList  = StringUtil.join(slCriteriaReleased,pgApolloConstants.CONSTANT_STRING_PIPE);
					consolidatedMap.put("cm_criteria", sCriteriaList);
					consolidatedMap.put("cm_crinames", StringUtil.join(slCriteriaName," | "));
					
					mpCharacteristicMaster = updateMultiValueAttributeAsString(mpCharacteristicMaster, pgApolloConstants.SELECT_ATTRIBUTE_ENTERPRISEPART_PGBUSINESSAREA);
					mpCharacteristicMaster = updateMultiValueAttributeAsString(mpCharacteristicMaster, pgApolloConstants.SELECT_ATTRIBUTE_ENTERPRISEPART_PGPRODUCTCATEGORYPLATFORM);

					if (mpCharacteristicMaster.get(CharacteristicMasterConstants.MASTER_TO_CHARACTERISTIC_SELECTABLE
							+ DomainConstants.SELECT_ID).equals(sObjId)) {
						consolidatedMap.putAll(mpCharacteristicMaster);
						break;
					}
				}
				consolidatedMap.put("cm_tmrd",getConnectedTestMethodReferenceDocumentAPP(context,sParamId));
				consolidatedMap.put(CharacteristicMasterConstants.TEST_METHODS,StringUtil.join(slTM," | ") );
				sDimension = (UIUtil.isNotNullAndNotEmpty(enoChar.getDimension()))?enoChar.getDimension():"";
				sDimensionDisplayName = ParameterInterfacesServices.getDimensionNLS(context, sDimension);
				consolidatedMap.put(CharacteristicMasterConstants.DIMENSION, sDimensionDisplayName);
				consolidatedMap.put("Title",enoChar.getTitle(context));
				//Get Display Unit Value For This CM
				sCharUOM = getValidUOM(enoChar, consolidatedMap);
				consolidatedMap.put(CharacteristicMasterConstants.PARAM_DISPLAY_UNIT, sCharUOM);
				//Check for duplicates slCMIDList
				sCMId = sObjId;
				if(!slCMIDList.contains(sCMId)) {
					if(bFlag){
						redefineMapKeys(consolidatedMap);
						//Remove unwanted keys from map
						removeUnwantedKeys(consolidatedMap);
					}
					context.printTrace(STR_APOLLO_WS_TRACE, "Method ::: getCharacteristicsMastersForCriteria consolidatedMap >> "+consolidatedMap.toString());
					mlCharacteristicsMasters.add(consolidatedMap);
					slCMIDList.add(sCMId);
				}
				
			}
		}
		context.printTrace(STR_APOLLO_WS_TRACE, "Method ::: getCharacteristicsMastersForCriteria ends");
		return mlCharacteristicsMasters;
	}
	
	/**
	 * Method to get Valid UOM
	 * @param context
	 * @param sDimension
	 * @param enoChar
	 * @return
	 */
	private static String getValidUOM(ENOICharacteristic enoChar, Map<String, Object> consolidatedMap)
	{
		String sCharUOM = enoChar.getDisplayUnit();
		
		if(UIUtil.isNullOrEmpty(sCharUOM))
		{
			sCharUOM = (String)consolidatedMap.get(pgApolloConstants.CONSTANT_STRING_SELECT_FROM+pgApolloConstants.RELATIONSHIP_PARAMETER_AGGREGATION+"].to."+pgApolloConstants.SELECT_ATTRIBUTE_OBSCUREUNITOFMEASURE);
		}	
		
		if(UIUtil.isNullOrEmpty(sCharUOM))
		{
			sCharUOM = DomainConstants.EMPTY_STRING;
		}
		
		return sCharUOM;
	}
	/**
	 * Method to update map with multi value attribute as String
	 * @param mpCharacteristicMaster
	 * @param sSelectAttribute
	 */
	public static Map<String, Object> updateMultiValueAttributeAsString(Map<String, Object> mpCharacteristicMaster, String sSelectAttribute)
	{
		String sAttributeValues = DomainConstants.EMPTY_STRING;
		StringList slAttributeValues;
		slAttributeValues = pgApolloCommonUtil.getStringListMultiValue(mpCharacteristicMaster.get(sSelectAttribute));
		if(null != slAttributeValues && !slAttributeValues.isEmpty())
		{
			slAttributeValues.sort();
			sAttributeValues = StringUtil.join(slAttributeValues, pgApolloConstants.CONSTANT_STRING_PIPE);				
		}
		mpCharacteristicMaster.put(sSelectAttribute, sAttributeValues);		
		return mpCharacteristicMaster;
	}
	
	public static Map prepareMapForName(Map<String, StringList> mpreturnMap) {
		HashMap<String,String> mpDisplayName = new HashMap<>();
		StringList slActual = mpreturnMap.get("field_choices");
		StringList slDisplay = mpreturnMap.get("field_display_choices"); 
		for(int i = 0 ; i < slActual.size(); i++ ) {
			mpDisplayName.put(slActual.get(i),slDisplay.get(i) );
		}
		return mpDisplayName;
	}
	
	
	public static void redefineMapKeys(Map consolidatedMap) {
		StringList slOldKeys = StringUtil.split(STR_CM_SELECTABLES, pgApolloConstants.CONSTANT_STRING_PIPE);
		StringList slNewKeys = StringUtil.split(STR_CM_SELECTABLES_KEYS, pgApolloConstants.CONSTANT_STRING_PIPE);
		
		String sOldKey = null;
		for(int i = 0 ; i < slOldKeys.size(); i++) {
			sOldKey = slOldKeys.get(i);
			if(consolidatedMap.containsKey(sOldKey)) {
				consolidatedMap.put(slNewKeys.get(i),consolidatedMap.get(sOldKey));
				consolidatedMap.remove(sOldKey);
			}
			
		}
	}
	
	public static void removeUnwantedKeys(Map refinedMap) {
		StringList slColKeys = StringUtil.split(strCMW_COL_KEYS, pgApolloConstants.CONSTANT_STRING_PIPE);
		String sKey;
		for(Iterator<String> iterator = refinedMap.keySet().iterator(); iterator.hasNext(); ) {
			  sKey = iterator.next();
			  if(!slColKeys.contains(sKey)) {
			    iterator.remove();
			  }
			}
	}

	/**
	 * @param mlTMList
	 * @return String having Pipe separated Test Methods List for CM
	 */
	public static String getTestMethodNameList(MapList mlTMList) {
		String sTestMethodList = DomainConstants.EMPTY_STRING;
		Map map = null;
		StringList slTestMethodList = new StringList();
		for(int i =0;i<mlTMList.size();i++) {
			map = (Map) mlTMList.get(i);
			slTestMethodList.add((String)map.get(DomainConstants.SELECT_NAME));
		}
		if(!slTestMethodList.isEmpty()) {
			sTestMethodList = StringUtil.join(slTestMethodList," | ");
		}
		return sTestMethodList;
	}
	/**
	 * @param context
	 * @param sParameterId
	 * @return String having pipe separated TMRD List for CM
	 * @throws Exception
	 */
	public static String getConnectedTestMethodReferenceDocumentAPP(Context context,String sParameterId) throws FrameworkException {
		String sTMRDList = DomainConstants.EMPTY_STRING;
	      
	      StringList objectSelects = new StringList(1);
	      objectSelects.add(DomainConstants.SELECT_NAME);
	      MapList docList = null;
	      Map docMap = null;
	      StringList slTMRDList = new StringList();
	      DomainObject domObj = DomainObject.newInstance(context,sParameterId);					  
		  docList = domObj.getRelatedObjects(context, //context
				  DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT, // relationship pattern
				  DomainConstants.QUERY_WILDCARD,// type pattern
				  objectSelects, // object selects
				  null, // relationship selects
				  false, // to direction
				  true, // from direction
				  (short)1,// recursion level
				  "", // object where clause
				  "", // relationship where clause
				  0); // objects Limit	
		  if(null != docList && !docList.isEmpty()) {
		      for (Object docObject : docList)
		      {
		        docMap = (Map)docObject;				        
		        slTMRDList.add((String)docMap.get(DomainConstants.SELECT_NAME));
		      }
		  }
		if(!slTMRDList.isEmpty()) {
			sTMRDList = StringUtil.join(slTMRDList, " | ");
		}
		return sTMRDList;
	}
	/**
	 * @param mlCMList
	 * @return Converted JSONArray
	 */
	public static JSONArray convertMapListToJasonArray(MapList mlCMList) {
		JSONObject jsonObject = null;
		JSONArray jsonArray = new JSONArray();
		Map mapResult = null;
		for (int i = 0; i < mlCMList.size(); i++) {
			mapResult = (Map) mlCMList.get(i);
			jsonObject = new JSONObject(mapResult);
			jsonArray.put(jsonObject);
		}
		return jsonArray;
	}


	/**
	 * @param context
	 * @param sInputPL
	 * @return MapList for Business Areas
	 * @throws Exception
	 */
	public static JSONArray getBusinessAreas(Context context) throws Exception {
		JSONObject jsonObjTemp = null;
		JSONArray jsonArr = new JSONArray();
			
		UpdateRangeForCATIAAttributes updateBAObj = new UpdateRangeForCATIAAttributes();
		StringList slSelectedPlatformList = updateBAObj.getSelectableFromMapList(context, pgApolloConstants.TYPE_PG_PLI_BUSINESSAREA, "*", DomainConstants.EMPTY_STRING,DomainConstants.SELECT_NAME);
		int iSize = slSelectedPlatformList.size();
		for (int iCount = 0; iCount < iSize; iCount++) {
			jsonObjTemp = new JSONObject();
			jsonObjTemp.put("name", slSelectedPlatformList.get(iCount));
			jsonArr.put(jsonObjTemp);
		}
		return jsonArr;
	}

	public static JSONArray getPhaseList(Context context) throws Exception {
		JSONObject jsonObjTemp = null;
		JSONArray jsonArr = new JSONArray();
        StringList slPhaseRanges = FrameworkUtil.getRanges(context, pgApolloConstants.STR_RELEASE_PHASE);
        for(int i =0;i < slPhaseRanges.size();i++) {
        	jsonObjTemp = new JSONObject();
        	if(!slPhaseRanges.get(i).equals(DomainConstants.EMPTY_STRING)) {
        		jsonObjTemp.put("name", slPhaseRanges.get(i));
    			jsonArr.put(jsonObjTemp);
        	}
			
        }
		
		return jsonArr;
	}
	/**
	 * @param context
	 * @param sInputPL
	 * @return MapList of Platform Data
	 * @throws Exception
	 */
	public static MapList getPlatformData(Context context) throws MatrixException{
		MapList mlPlatformList = null;
		StringList slBusSelect = new StringList(2);
		slBusSelect.add(DomainConstants.SELECT_ID);
		slBusSelect.add(DomainConstants.SELECT_NAME);
		mlPlatformList = DomainObject.findObjects(context, pgApolloConstants.TYPE_PG_PLATFORM, DomainConstants.QUERY_WILDCARD, pgApolloConstants.CONSTANT_STRING_HYPHEN,
				DomainConstants.QUERY_WILDCARD, pgApolloConstants.VAULT_ESERVICE_PRODUCTION, null, false, slBusSelect);

		context.printTrace(STR_APOLLO_WS_TRACE, "mlPlatformList >>> "+mlPlatformList.toString());
		return mlPlatformList;
	}

	/**
	 * @param context
	 * @param sInputPL
	 * @return MapList of Product Chassis Data
	 * @throws Exception
	 */
	public static MapList getChassisData(Context context) throws MatrixException{
		MapList mlChassisList = null;
		StringList slBusSelect = new StringList(2);
		slBusSelect.add(DomainConstants.SELECT_ID);
		slBusSelect.add(DomainConstants.SELECT_NAME);
		mlChassisList = DomainObject.findObjects(context, pgApolloConstants.TYPE_PG_CHASSIS, DomainConstants.QUERY_WILDCARD, "-",
				DomainConstants.QUERY_WILDCARD, pgApolloConstants.VAULT_ESERVICE_PRODUCTION, null, false, slBusSelect);
		context.printTrace(STR_APOLLO_WS_TRACE, "mlChassisList >>> "+mlChassisList.toString());

		return mlChassisList;
	}

	/**
	 * @param sAttrName
	 * @param slValues
	 * @return String having where clause for Criteria find objects
	 */
	public static String createWhereExpression(String sAttrName, StringList slValues) {
		String sAttribute = DomainConstants.EMPTY_STRING;
		StringBuilder sbReturn = new StringBuilder();
		sbReturn.append("(");

		for (int i = 0; i < slValues.size(); i++) {
			if (sAttrName != null && "BA".equalsIgnoreCase(sAttrName)) {
				sAttribute = pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA2;
			} else if (sAttrName != null && "PCP".equalsIgnoreCase(sAttrName)) {
				sAttribute = pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA3;
			} else if (sAttrName != null && "PTP".equalsIgnoreCase(sAttrName)) {
				sAttribute = pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA4;
			} else if (sAttrName != null && "PTC".equalsIgnoreCase(sAttrName)) {
				sAttribute = pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA5;
			} else if (sAttrName != null && "ProductSize".equalsIgnoreCase(sAttrName)) {
				sAttribute = pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA6;
			} else if (sAttrName != null && "IntendedMarket".equalsIgnoreCase(sAttrName)) {
				sAttribute = pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA7;
			}else if (sAttrName != null && "Phase".equalsIgnoreCase(sAttrName)) {
				sAttribute = pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA1;
			}

			sbReturn.append(sAttribute);
			sbReturn.append(pgApolloConstants.CONSTANT_STRING_SPACE);
			sbReturn.append(pgApolloConstants.CONSTANT_STRING_DOUBLE_EQUAL);
			sbReturn.append(pgApolloConstants.CONSTANT_STRING_SPACE);
			sbReturn.append(pgApolloConstants.CONSTANT_STRING_SINGLE_QUOTE);
			sbReturn.append(slValues.get(i));
			sbReturn.append(pgApolloConstants.CONSTANT_STRING_SINGLE_QUOTE);
			if (i != slValues.size() - 1) {
				sbReturn.append(pgApolloConstants.CONSTANT_STRING_SPACE);
				sbReturn.append(pgApolloConstants.CONSTANT_STRING_DOUBLE_PIPE);
			}

		}
		sbReturn.append(")");
		return sbReturn.toString();

	}

	/**
	 * @param context
	 * @param mapQuery
	 * @return Released Criteria(s) based on search attributes
	 * @throws Exception
	 */
	public static MapList getReleasedCriterias(Context context, Map mapQuery,boolean bFlag) throws Exception {

		context.printTrace(STR_APOLLO_WS_TRACE, "Method ::: getReleasedCriterias starts");
		
		//Set Class level data members
		if(UIUtil.isNullOrEmpty(strCMW_COL_KEYS)) {
			strCMW_COL_KEYS = getPageProperty(context,pgApolloConstants.STR_APOLLO_CONFIG_PAGE_FILENAME,"pgApolloWidget.CharacteristicMasterWidget.ColumnsKeys");
		}
		if(UIUtil.isNullOrEmpty(strCOMPARE_RESULT_COLS)) {
			strCOMPARE_RESULT_COLS = getPageProperty(context,pgApolloConstants.STR_APOLLO_CONFIG_PAGE_FILENAME,"pgApolloWidget.CharacteristicMasterWidget.CompareResultCols");
		}
		if(UIUtil.isNullOrEmpty(strCOMPARE_RESULT_KEYS)) {
			strCOMPARE_RESULT_KEYS = getPageProperty(context,pgApolloConstants.STR_APOLLO_CONFIG_PAGE_FILENAME,"pgApolloWidget.CharacteristicMasterWidget.CompareResultKeys");
		}

		
		MapList mlCriteriaList = new MapList();
		MapList mlEvaluatedCriterias = new MapList();
		MapList mlCMList = new MapList();
		MapList mlNewList= new MapList();
		
		StringList slMultiSelect = new StringList();
		slMultiSelect.add(pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA1);
		slMultiSelect.add(pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA2);
		slMultiSelect.add(pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA3);
		slMultiSelect.add(pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA4);
		slMultiSelect.add(pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA5);
		slMultiSelect.add(pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA6);
		slMultiSelect.add(pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA7);				
		slMultiSelect.add(pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA_8);				
		slMultiSelect.add(pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA_9);				

		StringList slBusSelect = new StringList(3);
		slBusSelect.add(DomainConstants.SELECT_ID);
		slBusSelect.add(DomainConstants.SELECT_NAME);
		slBusSelect.add(pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA1);
		slBusSelect.add(pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA2);
		slBusSelect.add(pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA3);
		slBusSelect.add(pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA4);
		slBusSelect.add(pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA5);
		slBusSelect.add(pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA6);
		slBusSelect.add(pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA7);
		slBusSelect.add(pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA_8);				
		slBusSelect.add(pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA_9);	
		
		
		StringBuilder sbReleasedWhereExp = new StringBuilder();
		sbReleasedWhereExp.append(pgApolloConstants.CONSTANT_STRING_OPEN_ROUND_BRACE);
		sbReleasedWhereExp.append(DomainConstants.SELECT_CURRENT);
		sbReleasedWhereExp.append(pgApolloConstants.CONSTANT_STRING_SPACE);
		sbReleasedWhereExp.append(pgApolloConstants.CONSTANT_STRING_DOUBLE_EQUAL);
		sbReleasedWhereExp.append(pgApolloConstants.CONSTANT_STRING_SPACE);
		sbReleasedWhereExp.append(pgApolloConstants.CONSTANT_STRING_SINGLE_QUOTE);
		sbReleasedWhereExp.append(pgApolloConstants.STATE_RELEASED);
		sbReleasedWhereExp.append(pgApolloConstants.CONSTANT_STRING_SINGLE_QUOTE);
		sbReleasedWhereExp.append(pgApolloConstants.CONSTANT_STRING_CLOSE_ROUND_BRACE);
		
		StringBuilder sbInworkWhereExp = new StringBuilder();
		sbInworkWhereExp.append(pgApolloConstants.CONSTANT_STRING_OPEN_ROUND_BRACE);
		sbInworkWhereExp.append(pgApolloConstants.SELECT_ISLAST);
		sbInworkWhereExp.append(pgApolloConstants.CONSTANT_STRING_SPACE);
		sbInworkWhereExp.append(pgApolloConstants.CONSTANT_STRING_DOUBLE_EQUAL);
		sbInworkWhereExp.append(pgApolloConstants.CONSTANT_STRING_SPACE);
		sbInworkWhereExp.append(pgApolloConstants.CONSTANT_STRING_SINGLE_QUOTE);
		sbInworkWhereExp.append(pgApolloConstants.STR_TRUE_FLAG_CAPS);
		sbInworkWhereExp.append(pgApolloConstants.CONSTANT_STRING_SINGLE_QUOTE);
		sbInworkWhereExp.append(pgApolloConstants.CONSTANT_STRING_SPACE);
		sbInworkWhereExp.append(pgApolloConstants.CONSTANT_STRING_DOUBLE_AMPERSAND);
		sbInworkWhereExp.append(pgApolloConstants.CONSTANT_STRING_SPACE);
		sbInworkWhereExp.append(DomainConstants.SELECT_CURRENT);
		sbInworkWhereExp.append(pgApolloConstants.CONSTANT_STRING_SPACE);
		sbInworkWhereExp.append(pgApolloConstants.CONSTANT_STRING_NOT_EQUAL_SIGN);
		sbInworkWhereExp.append(pgApolloConstants.CONSTANT_STRING_SPACE);
		sbInworkWhereExp.append(pgApolloConstants.CONSTANT_STRING_SINGLE_QUOTE);
		sbInworkWhereExp.append(pgApolloConstants.STATE_OBSOLETE);
		sbInworkWhereExp.append(pgApolloConstants.CONSTANT_STRING_SINGLE_QUOTE);
		sbInworkWhereExp.append(pgApolloConstants.CONSTANT_STRING_CLOSE_ROUND_BRACE);
		
		String sGetInWorkCriteria = (String) mapQuery.getOrDefault("GetInWorkCriteria",pgApolloConstants.STR_FALSE_FLAG);
		boolean bIncludeInWorkCriteria  = sGetInWorkCriteria.equalsIgnoreCase(pgApolloConstants.STR_TRUE_FLAG);
		String sIncludeInWork = (String) mapQuery.getOrDefault("GetInWorkCM",pgApolloConstants.STR_FALSE_FLAG);
		boolean bIncludeInWork  = sIncludeInWork.equalsIgnoreCase(pgApolloConstants.STR_TRUE_FLAG);
		
		String sGetTargetLimitVals = (String) mapQuery.getOrDefault("FetchLimitValues",pgApolloConstants.STR_FALSE_FLAG);
		boolean bFetchTargetLimitVals  = sGetTargetLimitVals.equalsIgnoreCase(pgApolloConstants.STR_TRUE_FLAG);
		StringBuilder sbWhereExp = new StringBuilder();
		
		sbWhereExp.append(pgApolloConstants.CONSTANT_STRING_OPEN_ROUND_BRACE);
		sbWhereExp.append(pgApolloConstants.SELECT_ATTRIBUTE_APPLICABLETYPE);
		sbWhereExp.append(pgApolloConstants.CONSTANT_STRING_CLOSE_ROUND_BRACE);
		sbWhereExp.append(pgApolloConstants.CONSTANT_STRING_SPACE);
		sbWhereExp.append(pgApolloConstants.CONSTANT_STRING_DOUBLE_EQUAL);
		sbWhereExp.append(pgApolloConstants.CONSTANT_STRING_SPACE);
		sbWhereExp.append(pgApolloConstants.CONSTANT_STRING_SINGLE_QUOTE);
		sbWhereExp.append(pgApolloConstants.STR_SYMBOLIC_ASSEMBLED_PRODUCT_PART);
		sbWhereExp.append(pgApolloConstants.CONSTANT_STRING_SINGLE_QUOTE);
		sbWhereExp.append(pgApolloConstants.CONSTANT_STRING_SPACE);
		sbWhereExp.append(pgApolloConstants.CONSTANT_STRING_DOUBLE_AMPERSAND);
		sbWhereExp.append(pgApolloConstants.CONSTANT_STRING_SPACE);
		
		if(bIncludeInWorkCriteria) {
			sbWhereExp.append(sbInworkWhereExp.toString());
		}else {
			sbWhereExp.append(sbReleasedWhereExp.toString());
		}
		
		sbWhereExp.append(pgApolloConstants.CONSTANT_STRING_SPACE);
		sbWhereExp.append(pgApolloConstants.CONSTANT_STRING_DOUBLE_AMPERSAND);
		sbWhereExp.append(pgApolloConstants.CONSTANT_STRING_SPACE);
		sbWhereExp.append(createWhereExpression("BA", (StringList) mapQuery.get("BA")));
		
		try {
			context.printTrace(STR_APOLLO_WS_TRACE, "sbWhereExp >>> " + sbWhereExp.toString());
			// Fix for 22x Upgrade MultiValueList Changes - Start
			mlCriteriaList = DomainObject.findObjects(context, // context
					pgApolloConstants.TYPE_CRITERIA, // typePattern
					DomainConstants.QUERY_WILDCARD, // namePattern
					DomainConstants.QUERY_WILDCARD, // revPattern
					DomainConstants.QUERY_WILDCARD, // ownerPattern
					pgApolloConstants.VAULT_ESERVICE_PRODUCTION, // vaultPattern
					sbWhereExp.toString(), // whereExpression
					false, // expandType
					slBusSelect, // objectSelects
					slMultiSelect); // Multi Value Selects
			// Fix for 22x Upgrade MultiValueList Changes - End
			
			if (mlCriteriaList != null && !mlCriteriaList.isEmpty()) {
				mlEvaluatedCriterias = filterCriterias(context,mlCriteriaList,mapQuery);
				//mlCMList = getCharacteristicsMastersForCriteria(context, mlEvaluatedCriterias,bFlag); //A10-880 Changes
				//Call new method getCharactristicsMasters //A10-880 Changes Starts
				String sCriteriaId;
				Map mpCriteriaData;
				for(int i = 0; i < mlEvaluatedCriterias.size();i++) {
					mpCriteriaData = (Map)mlEvaluatedCriterias.get(i);
					sCriteriaId = (String)mpCriteriaData.get(DomainConstants.SELECT_ID);
					if(bFetchTargetLimitVals) {
						mlNewList.addAll(getCharactristicMastersForCriteriaWithLimit(context,sCriteriaId,bIncludeInWork,bIncludeInWorkCriteria));
					}else {
						mlNewList.addAll(getCharactristicMastersForCriteria(context,sCriteriaId,bIncludeInWork,bIncludeInWorkCriteria));
					}
					
				}
				 //A10-880 Changes Ends 
			}
		}
		catch(Exception ex) {
			context.printTrace(STR_APOLLO_WS_TRACE, "Exception ::: "+ex.toString());
			throw ex;
			
		}finally {
		
		}
		context.printTrace(STR_APOLLO_WS_TRACE, "Method ::: getReleasedCriterias ends");
		//A10-880 Changes Starts
		MapList mlFinalCMList;
		if(bIncludeInWork) {
			mlFinalCMList = filterLatestCMFromList(mlNewList);
			mlFinalCMList = removeDuplicatesFromList(mlFinalCMList,DomainConstants.SELECT_ID);
		}else {
			mlFinalCMList = removeDuplicatesFromList(mlNewList,DomainConstants.SELECT_ID);
		}
	

		//A10-880 Changes Ends
		return mlFinalCMList;
	
		
	}


	/** This method will return response with CM and its Target & Limit Values
	 * @param context
	 * @param sCriteriaId
	 * @param bIncludeInWork
	 * @param bIncludeInWorkCriteria 
	 * @return
	 * @throws Exception 
	 */
	private static MapList getCharactristicMastersForCriteriaWithLimit(Context context, String sCriteriaId,
			boolean bIncludeInWork, boolean bIncludeInWorkCriteria) throws Exception {
		MapList mlCMTargetLimitData = new MapList();
		MapList mlBasicData = getCharactristicMastersForCriteria(context,sCriteriaId,bIncludeInWork,bIncludeInWorkCriteria);
		Map mpData;
		ENOICharacteristic charac;
		for(int i = 0 ; i <mlBasicData.size();i++ ) {
			mpData = (Map)mlBasicData.get(i);
			charac =  ENOCharacteristicFactory.getCharacteristicById(context, (String)mpData.get("ParamId"));
			mpData.put("LSL", charac.getLowerSpecificationLimit(context));
			mpData.put("LRRL", charac.getLowerRoutineReleaseLimit(context));
			mpData.put("URRL", charac.getUpperRoutineReleaseLimit(context));
			mpData.put("USL", charac.getUpperSpecificationLimit(context));
			mpData.put("MinVal", charac.getMinimalValue(context));
			mpData.put("MaxVal", charac.getMaximalValue(context));
			mpData.put("Target", charac.getNominalValue(context));
			mlCMTargetLimitData.add(mpData);
		}
		return mlCMTargetLimitData;
	}
	/** This Method will filter non latest CMs
	 * @param mlNewList
	 * @return
	 */
	private static MapList filterLatestCMFromList(MapList mlNewList) {
		Map mpData;
		String sIsLast;
		MapList mlReturnList = new MapList();
		for(int i = 0 ; i < mlNewList.size(); i++) {
			mpData = (Map)mlNewList.get(i);
			sIsLast = (String)mpData.getOrDefault("islast",pgApolloConstants.STR_FALSE_FLAG);
			if(UIUtil.isNotNullAndNotEmpty(sIsLast) && sIsLast.equalsIgnoreCase(pgApolloConstants.STR_TRUE_FLAG)) {
				mlReturnList.add(mpData);
			}
		}
		return mlReturnList;
	}
	/** This Method Removes Duplicate Entry from List
	 * @param mlNewList
	 * @param selectId
	 */
	private static MapList removeDuplicatesFromList(MapList mlNewList, String selectId) {
		MapList mlReturnList = new MapList();
		StringList slUniqueKeyList = new StringList();
		Map mpListData;
		String sKey;
		for(int i = 0 ; i < mlNewList.size(); i++) {
			mpListData = (Map)mlNewList.get(i);
			sKey = (String)mpListData.get(selectId);
			if(UIUtil.isNotNullAndNotEmpty(sKey) && !slUniqueKeyList.contains(sKey)) {
				slUniqueKeyList.add(sKey);
				mlReturnList.add(mpListData);
			}
		}
		return mlReturnList;
	}
	public static MapList filterCriterias (Context context,MapList mlCriteriaList,Map mapQuery) throws Exception {
		

		MapList mlCandidateCriterias = new MapList();
		Map mapCriteria = null;
		StringList slDBCriteriaAttributeValues = null;
		
		context.printTrace(STR_APOLLO_WS_TRACE, "Input Map >>> " + mapQuery);

		context.printTrace(STR_APOLLO_WS_TRACE, "mlCriteriaList before  >>> " + mlCriteriaList.toString());
		Object obj = null;

		
		boolean bMatchResult = false;
		boolean bFinalMatchResult = false;


		StringBuilder sbQryAttribute = new StringBuilder(pgApolloConstants.STR_PHASE);
		sbQryAttribute.append(pgApolloConstants.CONSTANT_STRING_PIPE);
		sbQryAttribute.append(pgApolloConstants.STR_BA);
		sbQryAttribute.append(pgApolloConstants.CONSTANT_STRING_PIPE);
		sbQryAttribute.append(pgApolloConstants.STR_PCP);
		sbQryAttribute.append(pgApolloConstants.CONSTANT_STRING_PIPE);
		sbQryAttribute.append(pgApolloConstants.STR_PTP);
		sbQryAttribute.append(pgApolloConstants.CONSTANT_STRING_PIPE);
		sbQryAttribute.append(pgApolloConstants.STR_PTC);
		sbQryAttribute.append(pgApolloConstants.CONSTANT_STRING_PIPE);
		sbQryAttribute.append(pgApolloConstants.STR_PRODUCT_SIZE);		
		sbQryAttribute.append(pgApolloConstants.CONSTANT_STRING_PIPE);
		sbQryAttribute.append(pgApolloConstants.STR_AUTOMATION_INTENDEDMARKETS);
		sbQryAttribute.append(pgApolloConstants.CONSTANT_STRING_PIPE);
		sbQryAttribute.append(pgApolloConstants.STR_AUTOMATION_REGION);
		sbQryAttribute.append(pgApolloConstants.CONSTANT_STRING_PIPE);
		sbQryAttribute.append(pgApolloConstants.STR_AUTOMATION_SUBREGION);
		
		String sQryAttributes = sbQryAttribute.toString();
		StringList slQryAttributes = StringUtil.split(sQryAttributes, pgApolloConstants.CONSTANT_STRING_PIPE);

		
		StringBuilder sbCriteriaAttrList = new StringBuilder(pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA1);
		sbCriteriaAttrList.append(pgApolloConstants.CONSTANT_STRING_PIPE);
		sbCriteriaAttrList.append(pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA2);
		sbCriteriaAttrList.append(pgApolloConstants.CONSTANT_STRING_PIPE);
		sbCriteriaAttrList.append(pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA3);
		sbCriteriaAttrList.append(pgApolloConstants.CONSTANT_STRING_PIPE);
		sbCriteriaAttrList.append(pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA4);
		sbCriteriaAttrList.append(pgApolloConstants.CONSTANT_STRING_PIPE);
		sbCriteriaAttrList.append(pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA5);
		sbCriteriaAttrList.append(pgApolloConstants.CONSTANT_STRING_PIPE);
		sbCriteriaAttrList.append(pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA6);
		sbCriteriaAttrList.append(pgApolloConstants.CONSTANT_STRING_PIPE);
		sbCriteriaAttrList.append(pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA7);
		sbCriteriaAttrList.append(pgApolloConstants.CONSTANT_STRING_PIPE);
		sbCriteriaAttrList.append(pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA_8);
		sbCriteriaAttrList.append(pgApolloConstants.CONSTANT_STRING_PIPE);
		sbCriteriaAttrList.append(pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA_9);
		String sDBCriteriaAttrList = sbCriteriaAttrList.toString();
		StringList slDBCriteriaAttrList = StringUtil.split(sDBCriteriaAttrList, pgApolloConstants.CONSTANT_STRING_PIPE);
		String sQryParam = null;
		
		StringList slInputValueList;

		for(int i =0;i<mlCriteriaList.size();i++) {
			
			bFinalMatchResult = true;

			mapCriteria = (Map) mlCriteriaList.get(i);

			for(int j = 0; j< slDBCriteriaAttrList.size();j++) {
				bFinalMatchResult = true;
				sQryParam = slQryAttributes.get(j);
				bMatchResult = false;

				slDBCriteriaAttributeValues = new StringList();

				obj = mapCriteria.get(slDBCriteriaAttrList.get(j));
								
				
				if (obj instanceof String) {
					slDBCriteriaAttributeValues.addElement((String)obj);
				}else if (obj instanceof StringList) {
					slDBCriteriaAttributeValues.addAll((StringList) obj);
				}
				
				if(null != slDBCriteriaAttributeValues && slDBCriteriaAttributeValues.contains(DomainConstants.EMPTY_STRING))
				{
					slDBCriteriaAttributeValues.remove(DomainConstants.EMPTY_STRING);
				}	
				
				slInputValueList = (StringList) mapQuery.get(sQryParam);
				if(!slDBCriteriaAttributeValues.isEmpty() && UIUtil.isNotNullAndNotEmpty(slDBCriteriaAttributeValues.get(0)) && !slInputValueList.isEmpty()) {
					bMatchResult = isAttributeMatching(slDBCriteriaAttributeValues, slInputValueList);
					if(!bMatchResult) {
						bFinalMatchResult = false;
						break;
					}
				}
				


			}
			context.printTrace(STR_APOLLO_WS_TRACE, "bFinal >>> " + bFinalMatchResult);
			if(bFinalMatchResult) {
				mlCandidateCriterias.add(mapCriteria);
			}

		}
		context.printTrace(STR_APOLLO_WS_TRACE, "mlCandidateCriterias >>> " + mlCandidateCriterias.toString());
		return mlCandidateCriterias;
	
		
	}
	
	public static boolean isAttributeMatching(StringList slCriteriaDB,StringList slQryAttribute) {
		boolean bFound = false;
		String sQryValue = null;
			for(int i =0 ; i < slQryAttribute.size(); i++) {
				sQryValue = slQryAttribute.get(i);
				if(slCriteriaDB.contains(sQryValue) ) {
					bFound = true;
					break;
				}
		}
		return bFound;
	}
	
	/**
	 * @param context
	 * @param qryMap
	 * @return Platform & Chassis Filter Value for Dashboard
	 * @throws Exception
	 */
	public static JSONObject getRangeValuesForDashboard(Context context, Map qryMap) throws Exception {
		context.printTrace(STR_APOLLO_WS_TRACE, "Method ::: getRangeValuesForDashboard starts");
		JSONObject jsonObj = new JSONObject();
		JSONObject jsonObjTemp = null;
		JSONArray jsonArr = new JSONArray();
		String sAttributeName = (String) qryMap.getOrDefault("Attribute", DomainConstants.EMPTY_STRING);
		String sInputValue = (String) qryMap.getOrDefault("Input", DomainConstants.EMPTY_STRING);
		String sInputValue2 = (String) qryMap.getOrDefault("Input2", DomainConstants.EMPTY_STRING);
		UpdateRangeForCATIAAttributes jpo = new UpdateRangeForCATIAAttributes();
		StringBuilder sbReturnMessages = new StringBuilder();
		String strType = null;

		if (UIUtil.isNotNullAndNotEmpty(sAttributeName)) {
			StringList slSelectedPlatformList = null;
			StringList slSelectedPlatformList2 = null;

			Map programMap = new HashMap();
			if (pgApolloConstants.STR_AUTOMATION_PRODUCTCATEGORYPLATFORM.equalsIgnoreCase(sAttributeName)) {
				slSelectedPlatformList = jpo.getSelectedPlatformList(context, pgApolloConstants.TYPE_PG_PLI_BUSINESSAREA, sInputValue, DomainConstants.EMPTY_STRING);
				programMap.put(STR_SELECTED_PLATFORM_LIST, slSelectedPlatformList);
				programMap.put(STR_GET_ATTR_OBJECT, pgApolloConstants.STR_PRODUCT_CATEGORY_PLATFORM);
				String[] methodargs = JPO.packArgs(programMap);
				strType = "PlatformToBusinessArea";
				sbReturnMessages.append(jpo.getProductFormPlatform(context, slSelectedPlatformList, methodargs, strType));
			} else if (pgApolloConstants.STR_AUTOMATION_PRODUCTTECHNOLOGYPLATFORM.equalsIgnoreCase(sAttributeName)) {
				slSelectedPlatformList = jpo.getSelectedPlatformList(context, pgApolloConstants.TYPE_PG_PLATFORM, sInputValue, DomainConstants.EMPTY_STRING);
				programMap.put(STR_SELECTED_PLATFORM_LIST, slSelectedPlatformList);
				programMap.put(STR_GET_ATTR_OBJECT, pgApolloConstants.STR_PRODUCT_TECHNOLOGY_PLATFORM);
				String[] methodargs = JPO.packArgs(programMap);
				strType = "Platform";
				sbReturnMessages.append(jpo.getProductFormPlatform(context, slSelectedPlatformList, methodargs, strType));
			} else if (pgApolloConstants.STR_AUTOMATION_PRODUCTTECHNOLOGYCHASSIS.equalsIgnoreCase(sAttributeName)) {
				slSelectedPlatformList = jpo.getSelectedPlatformList(context, pgApolloConstants.TYPE_PG_PLATFORM, sInputValue, DomainConstants.EMPTY_STRING);
				slSelectedPlatformList2 = jpo.getSelectedPlatformList(context, pgApolloConstants.TYPE_PG_PLATFORM, sInputValue2, DomainConstants.EMPTY_STRING);
				programMap.put(STR_SELECTED_PLATFORM_PCP_LIST, slSelectedPlatformList);
				programMap.put(STR_SELECTED_PLATFORM_LIST, slSelectedPlatformList2);
				programMap.put(STR_GET_ATTR_OBJECT, pgApolloConstants.STR_PRODUCT_TECHNOLOGY);
				String[] methodargs = JPO.packArgs(programMap);
				strType = "Chassis";
				sbReturnMessages.append(jpo.getProductFormPlatform(context, slSelectedPlatformList, methodargs, strType));
			} else if (pgApolloConstants.STR_AUTOMATION_BUSINESSAREA.equalsIgnoreCase(sAttributeName)) {
				slSelectedPlatformList = jpo.getSelectableFromMapList(context, pgApolloConstants.TYPE_PG_PLI_BUSINESSAREA, DomainConstants.QUERY_WILDCARD, DomainConstants.EMPTY_STRING,
						DomainConstants.SELECT_NAME);
				int iSize = slSelectedPlatformList.size();
				for (int iCount = 0; iCount < iSize; iCount++) {
					sbReturnMessages.append(slSelectedPlatformList.get(iCount));
					if (iCount != iSize - 1) {
						sbReturnMessages.append(pgApolloConstants.CONSTANT_STRING_PIPE);
					}
				}
			} else if (pgApolloConstants.STR_PRODUCT_SIZE.equalsIgnoreCase(sAttributeName)) {
				slSelectedPlatformList = jpo.getSelectedPlatformList(context, pgApolloConstants.TYPE_PG_CHASSIS, sInputValue, DomainConstants.EMPTY_STRING);
				programMap.put(STR_SELECTED_PLATFORM_LIST, slSelectedPlatformList);
				programMap.put(STR_GET_ATTR_OBJECT, "Product Size");
				String[] methodargs = JPO.packArgs(programMap);
				strType = pgApolloConstants.STR_PRODUCT_SIZE;
				sbReturnMessages.append(jpo.getProductFormPlatform(context, slSelectedPlatformList, methodargs, strType));
			} else if (pgApolloConstants.STR_AUTOMATION_REGION.equalsIgnoreCase(sAttributeName)) {
				slSelectedPlatformList = jpo.getSelectedPlatformList(context, pgApolloConstants.TYPE_PG_PLI_BUSINESSAREA, sInputValue, DomainConstants.EMPTY_STRING);
				programMap.put(STR_SELECTED_PLATFORM_LIST, slSelectedPlatformList);
				programMap.put(STR_GET_ATTR_OBJECT, pgApolloConstants.STR_AUTOMATION_REGION);
				String[] methodargs = JPO.packArgs(programMap);
				strType = pgApolloConstants.STR_AUTOMATION_REGION;
				sbReturnMessages.append(jpo.getProductFormPlatform(context, slSelectedPlatformList, methodargs, strType));
			} else if (pgApolloConstants.STR_AUTOMATION_SUBREGION.equalsIgnoreCase(sAttributeName)) {
				slSelectedPlatformList = StringUtil.split(sInputValue, pgApolloConstants.CONSTANT_STRING_COMMA);
				programMap.put(STR_SELECTED_PLATFORM_LIST, slSelectedPlatformList);
				slSelectedPlatformList = StringUtil.split(sInputValue2, pgApolloConstants.CONSTANT_STRING_COMMA);
				programMap.put("selectedPlatformList2", slSelectedPlatformList);
				programMap.put(STR_GET_ATTR_OBJECT, pgApolloConstants.STR_AUTOMATION_SUBREGION);
				String[] methodargs = JPO.packArgs(programMap);
				strType = pgApolloConstants.STR_AUTOMATION_SUBREGION;
				sbReturnMessages.append(jpo.getProductFormPlatform(context, slSelectedPlatformList, methodargs, strType));
			}
			
		}
		if (sbReturnMessages.length() > 0) {
			// Convert StringList to JSON
			String sReturnList = sbReturnMessages.toString();
			StringList slReturnList = StringUtil.split(sReturnList, pgApolloConstants.CONSTANT_STRING_PIPE);
			for (int i = 0; i < slReturnList.size(); i++) {
				jsonObjTemp = new JSONObject();
				jsonObjTemp.put("name", slReturnList.get(i));
				jsonArr.put(jsonObjTemp);
			}
		} else {
			jsonArr = new JSONArray();
		}
		jsonObj.put("PlatformData", jsonArr);
		context.printTrace(STR_APOLLO_WS_TRACE, "Method ::: getRangeValuesForDashboard ends");
		return jsonObj;
	}

	/**
	 * @param context
	 * @return JSONArray for Applicable type deprecated 
	 * @throws Exception
	 */
	public static JSONArray getApplicableTypes(Context context) throws Exception {
		JSONObject jsonObj = new JSONObject();
		JSONObject jsonObjTemp = null;
		JSONArray jsonArr = new JSONArray();
		Map requestMap = new HashMap();
		requestMap.put("requestMap", new HashMap());
		String[] methodargs = JPO.packArgs(requestMap);
		Map mapFieldValues = JPO.invoke(context, "emxCPNProductDataPartStage", null, "getProductDataTypes", methodargs,
				Map.class);
		StringList slTypeLabel = (StringList) mapFieldValues.get("field_display_choices");
		StringList slTypeName = (StringList) mapFieldValues.get("field_choices");
		if (!slTypeLabel.isEmpty()) {
			for (int i = 0; i < slTypeLabel.size(); i++) {
				jsonObjTemp = new JSONObject();
				jsonObjTemp.put("name",  slTypeLabel.get(i));
				jsonObjTemp.put("value",  slTypeName.get(i));
				jsonArr.put(jsonObjTemp);
			}
		}
		jsonObj.put("TypeData", jsonArr);
		context.printTrace(STR_APOLLO_WS_TRACE, "jsonObj >>> " + jsonObj.toString());
		return jsonArr;
	}

	/**
	 * @param context
	 * @return JSONArray for Intended Markets
	 * @throws org.json.JSONException 
	 * @throws Exception
	 */
	public static JSONArray getIntendedMarketsList(Context context) throws MatrixException, org.json.JSONException{
		context.printTrace(STR_APOLLO_WS_TRACE, "Method ::: getIntendedMarketsList starts");
		JSONArray jsonArr = new JSONArray();
		JSONObject jsonObjTemp = null;	
		
		StringBuilder sbWhereExp = new StringBuilder().append(DomainConstants.SELECT_CURRENT);
		sbWhereExp.append(pgApolloConstants.CONSTANT_STRING_SPACE);
		sbWhereExp.append(pgApolloConstants.CONSTANT_STRING_NOT_EQUAL_SIGN);
		sbWhereExp.append(pgApolloConstants.CONSTANT_STRING_SPACE);
		sbWhereExp.append("New");
		sbWhereExp.append(pgApolloConstants.CONSTANT_STRING_SPACE);
		sbWhereExp.append(pgApolloConstants.CONSTANT_STRING_DOUBLE_AMPERSAND);
		sbWhereExp.append(pgApolloConstants.CONSTANT_STRING_SPACE);
		sbWhereExp.append(DomainConstants.SELECT_CURRENT);
		sbWhereExp.append(pgApolloConstants.CONSTANT_STRING_SPACE);
		sbWhereExp.append(pgApolloConstants.CONSTANT_STRING_NOT_EQUAL_SIGN);
		sbWhereExp.append(pgApolloConstants.CONSTANT_STRING_SPACE);
		sbWhereExp.append("Inactive");
		
		StringList slObjSelects = new StringList(DomainConstants.SELECT_NAME);
		MapList mlCountryList = DomainObject.findObjects(context,
															pgApolloConstants.TYPE_PG_INTENDEDMARKETS, //typepattern
															DomainConstants.QUERY_WILDCARD,  // namepattern
															DomainConstants.QUERY_WILDCARD,  // revpattern
															DomainConstants.QUERY_WILDCARD,  // owner pattern
															DomainConstants.QUERY_WILDCARD,  // vault pattern
															sbWhereExp.toString(), // where exp
											                true,
											                slObjSelects);
		if(null != mlCountryList && !mlCountryList.isEmpty()) {
			mlCountryList.sort(DomainConstants.SELECT_NAME,"ascending","string");
			String strCountryName = null;
			Map<String,Object> mapCountry = null;
			for(int i = 0; i < mlCountryList.size(); i++) {
				mapCountry = (Map) mlCountryList.get(i);
				strCountryName = (String)mapCountry.get(DomainConstants.SELECT_NAME);
				jsonObjTemp = new JSONObject();
				jsonObjTemp.put("name", strCountryName);
				jsonObjTemp.put("value", strCountryName);
				jsonArr.put(jsonObjTemp);
			}
		}
		context.printTrace(STR_APOLLO_WS_TRACE, "Method ::: getIntendedMarketsList ends");
		return jsonArr;
	}
	
	/**
	 * This method will be used to Compare Criteria Objects. This is called from the widget web service. 
	 * @param context
	 * @param mapQuery
	 * @return
	 * @throws Exception
	 */
	public static MapList  getComparedResultForCriteriasByID(Context context, Map mapQuery)throws Exception {
		
		
		//Set Class level data members
		if(UIUtil.isNullOrEmpty(strCMW_COL_KEYS)) {
			strCMW_COL_KEYS = getPageProperty(context,pgApolloConstants.STR_APOLLO_CONFIG_PAGE_FILENAME,"pgApolloWidget.CharacteristicMasterWidget.ColumnsKeys");
		}
		if(UIUtil.isNullOrEmpty(strCOMPARE_RESULT_COLS)) {
			strCOMPARE_RESULT_COLS = getPageProperty(context,pgApolloConstants.STR_APOLLO_CONFIG_PAGE_FILENAME,"pgApolloWidget.CharacteristicMasterWidget.CompareResultCols");
		}
		if(UIUtil.isNullOrEmpty(strCOMPARE_RESULT_KEYS)) {
			strCOMPARE_RESULT_KEYS = getPageProperty(context,pgApolloConstants.STR_APOLLO_CONFIG_PAGE_FILENAME,"pgApolloWidget.CharacteristicMasterWidget.CompareResultKeys");
		}
		
		
		String sGetTargetLimitVals = (String) mapQuery.getOrDefault("FetchLimitValues",pgApolloConstants.STR_FALSE_FLAG);
		String sGetInWorkCriteria = (String) mapQuery.getOrDefault("GetInWorkCriteria",pgApolloConstants.STR_FALSE_FLAG);
		boolean bIncludeInWorkCriteria  = sGetInWorkCriteria.equalsIgnoreCase(pgApolloConstants.STR_TRUE_FLAG);
		String sIncludeInWork = (String) mapQuery.getOrDefault("GetInWorkCM",pgApolloConstants.STR_FALSE_FLAG);
		boolean bIncludeInWork  = sIncludeInWork.equalsIgnoreCase(pgApolloConstants.STR_TRUE_FLAG);
		boolean bFetchTargetLimitVals  = sGetTargetLimitVals.equalsIgnoreCase(pgApolloConstants.STR_TRUE_FLAG);

		StringList criteriaIDList1 =(StringList)mapQuery.get("id1");
		StringList criteriaIDList2 =(StringList)mapQuery.get("id2");
		
		context.printTrace(STR_APOLLO_WS_TRACE, "Method getComparedResultForCriteriasByID ::: criteriaIDList1 >>>>>>>>>>>>>>>>>>>>>>> "+criteriaIDList1.toString());
		context.printTrace(STR_APOLLO_WS_TRACE, "Method getComparedResultForCriteriasByID ::: criteriaIDList2 >>>>>>>>>>>>>>>>>>>>>>> "+criteriaIDList2.toString());
		
		
		Iterator criteriaIDList1Itr = criteriaIDList1.iterator();
		Iterator criteriaIDList2Itr = criteriaIDList2.iterator();
		MapList mlCM1List = new MapList();
		MapList mlCM2List = new MapList();
		String strCriteriaID;
		while(criteriaIDList1Itr.hasNext()) {
			strCriteriaID=(String)criteriaIDList1Itr.next();
			if(bFetchTargetLimitVals) {
				mlCM1List.addAll(getCharactristicMastersForCriteriaWithLimit(context,strCriteriaID,bIncludeInWork,bIncludeInWorkCriteria));
			}else {
				mlCM1List.addAll(getCharactristicMastersForCriteria(context,strCriteriaID,bIncludeInWork,bIncludeInWorkCriteria));
			}
			
		}
		while(criteriaIDList2Itr.hasNext()) {
			strCriteriaID=(String)criteriaIDList2Itr.next();
			if(bFetchTargetLimitVals) {
				mlCM1List.addAll(getCharactristicMastersForCriteriaWithLimit(context,strCriteriaID,bIncludeInWork,bIncludeInWorkCriteria));
			}else {
				mlCM2List.addAll(getCharactristicMastersForCriteria(context,strCriteriaID,bIncludeInWork,bIncludeInWorkCriteria));
			}
		}
		
		context.printTrace(STR_APOLLO_WS_TRACE, "Method getComparedResultForCriteriasByID ::: mlCM1List >>>>>>>>>>>>>>>>>>>>>>> "+mlCM1List.toString());
		context.printTrace(STR_APOLLO_WS_TRACE, "Method getComparedResultForCriteriasByID ::: mlCM2List >>>>>>>>>>>>>>>>>>>>>>> "+mlCM2List.toString());
		
		
		MapList mlCriteria1;
		MapList mlCriteria2;
		if(bIncludeInWork) {
			mlCriteria1 = filterLatestCMFromList(mlCM1List);
			mlCriteria1 = removeDuplicatesFromList(mlCriteria1,DomainConstants.SELECT_ID);
			mlCriteria2 = filterLatestCMFromList(mlCM2List);
			mlCriteria2 = removeDuplicatesFromList(mlCriteria2,DomainConstants.SELECT_ID);
			
			
		}else {
			mlCriteria1 = removeDuplicatesFromList(mlCM1List,DomainConstants.SELECT_ID);
			mlCriteria2 = removeDuplicatesFromList(mlCM2List,DomainConstants.SELECT_ID);
		}
		return compareCriteriaMapList(context , mlCriteria1,mlCriteria2);
		
	}
	
	private static MapList compareCriteriaMapList(Context context , MapList mlCriteria1 ,MapList mlCriteria2) throws MatrixException{

		MapList mlReturnList = new MapList();
		context.printTrace(STR_APOLLO_WS_TRACE, "Method compareCriteriaMapList ::: mlCriteria1 >>>>>>>>>>>>>>>>>>>>>>> "+mlCriteria1.toString());
		context.printTrace(STR_APOLLO_WS_TRACE, "Method compareCriteriaMapList ::: mlCriteria2 >>>>>>>>>>>>>>>>>>>>>>> "+mlCriteria2.toString());
		//Compare IDs
		mlReturnList.addAll(getMatchingObjects(mlCriteria1,mlCriteria2));
		
		//Remove Processed Objects
		removeProcessed(mlCriteria1,STR_PROCESSED);
		removeProcessed(mlCriteria2,STR_PROCESSED);
		
		
		//Similar Objects in Same Condition
		StringList slTemp = new StringList();
		StringList slDuplicate = new StringList();
		StringList sl =  ((List<Map>)mlCriteria1).stream().map(elementInfo -> {return (String) ((String)elementInfo.get(STR_CC) +pgApolloConstants.CONSTANT_STRING_PIPE+ (String) elementInfo.get(STR_DS) +pgApolloConstants.CONSTANT_STRING_PIPE+ (String) elementInfo.get(pgApolloConstants.STR_CHAR) +pgApolloConstants.CONSTANT_STRING_PIPE+ (String) elementInfo.get(STR_CS));}).collect(Collectors.toCollection(StringList::new));
		context.printTrace(STR_APOLLO_WS_TRACE, "Method compareCriteriaMapList ::: sl >>>>>>>>>>>>>>>>>>>>>>> "+sl.size());
		context.printTrace(STR_APOLLO_WS_TRACE, "Method compareCriteriaMapList ::: sl >>>>>>>>>>>>>>>>>>>>>>> "+sl.toString());
		HashSet hs = new HashSet<String>(sl);
		context.printTrace(STR_APOLLO_WS_TRACE, "Method compareCriteriaMapList ::: hs >>>>>>>>>>>>>>>>>>>>>>> "+hs.size());
		context.printTrace(STR_APOLLO_WS_TRACE, "Method compareCriteriaMapList ::: hs >>>>>>>>>>>>>>>>>>>>>>> "+hs.toString());
		
		String sKey = null;
		if(sl.size() != hs.size()) {
			for(int s =0;s<sl.size();s++) {
				sKey = sl.get(s);
				if(slTemp.contains(sKey)) {
					slDuplicate.add(sKey);
				}else {
					slTemp.add(sKey);
				}
			}
		}
		
		//Check Target for Matching 
		//Get c1 list with each slDuplicate
		//Iterate it
		//Get c2 list with each slDuplicate
		//Find Best Match From Target
		//Create return list
		
		context.printTrace(STR_APOLLO_WS_TRACE, "Method compareCriteriaMapList ::: slDuplicate >>>>>>>>>>>>>>>>>>>>>>> "+slDuplicate.toString());
		
		Map mapCM1 = null;
		Map mapCM2 = null;
		Map mapTemp = null;
		String  sTemp;
		String sIsProcessed = null;
		String sConcatinationCMProps1 = null;
		String sConcatinationCMProps2 = null;
		MapList mlTargetBestMatchList = null;
		MapList mlPlaceHolderCriteria1List = new MapList();
		mlPlaceHolderCriteria1List.addAll(mlCriteria1);
		
		//Compare Attributes
		for(int i = 0 ; i <mlPlaceHolderCriteria1List.size();i++) {
			mlTargetBestMatchList = new MapList();
			mapCM1 = (Map)mlPlaceHolderCriteria1List.get(i);
			sConcatinationCMProps1 = getConcatinatedValue(mapCM1,false);			
			for(int j=0 ; j < mlCriteria2.size();j++) {
				mapCM2 = (Map) mlCriteria2.get(j);				
				sConcatinationCMProps2 = getConcatinatedValue(mapCM2,false);				
				if(sConcatinationCMProps1.equalsIgnoreCase(sConcatinationCMProps2)) {
					mapCM2.put(STR_PROCESSED, "yes");
					mlTargetBestMatchList.add(mapCM2);
				}
			}
			
			if(mlTargetBestMatchList.isEmpty()) {
				mapCM1.put(STR_PROCESSED, "yes");
				mlReturnList.add(concatinateMaps(mapCM1,new HashMap<>(),"YES","10","10"));
			}else if (!mlTargetBestMatchList.isEmpty()) {
				for(int a = 0 ; a <mlTargetBestMatchList.size();a++ ) {
					mapCM2 = (Map)mlTargetBestMatchList.get(a);
					
					if(isIdentical(mapCM1,mapCM2)) {
						mapCM1.put(STR_PROCESSED, "yes");
						mapCM2.put(STR_IDENTICAL, "yes");
						mlReturnList.add(concatinateMaps(mapCM1,mapCM2,"YES","20","20"));
						a = mlTargetBestMatchList.size();
					}				
				}
				removeProcessed(mlTargetBestMatchList,STR_IDENTICAL);
				if(!mlTargetBestMatchList.isEmpty()) {
					for(int b=0;b<mlTargetBestMatchList.size();b++) {
						if(b==0) {
							mapCM1.put(STR_PROCESSED, "yes");
							mlReturnList.add(concatinateMaps(mapCM1,mapCM2,"YES","15","15"));
						}else {
							mapCM2 = (Map)mlTargetBestMatchList.get(b);
							mlReturnList.add(concatinateMaps(new HashMap<>(),mapCM2,"YES","10","10"));
						}
					}
				}
				sTemp = (String)mapCM1.getOrDefault(STR_PROCESSED, DomainConstants.EMPTY_STRING);
				if(!"yes".equals(sTemp)) {
					mapCM1.put(STR_PROCESSED, "yes");
					mlReturnList.add(concatinateMaps(mapCM1,new HashMap<>(),"YES","10","10"));
				}
			}
			removeProcessed(mlCriteria2,STR_PROCESSED);
			removeProcessed(mlCriteria1,STR_PROCESSED);
		}		
		
		for(int k = 0 ; k < mlCriteria2.size(); k++) {
			mapTemp = (Map)mlCriteria2.get(k);
			sIsProcessed = (String)mapTemp.getOrDefault(STR_PROCESSED, "not");
			if("not".equalsIgnoreCase(sIsProcessed)) {
				mlReturnList.add(concatinateMaps(new HashMap<>(), mapTemp,"YES","10","10"));
			}
		}
		for(int l = 0 ; l < mlCriteria1.size(); l++) {
			mapTemp = (Map)mlCriteria1.get(l);
			sIsProcessed = (String)mapTemp.getOrDefault(STR_PROCESSED, "not");
			if("not".equalsIgnoreCase(sIsProcessed)) {
				mlReturnList.add(concatinateMaps(mapTemp,new HashMap<>(),"YES","10","10"));
			}
		}
		
		context.printTrace(STR_APOLLO_WS_TRACE, "Method compareCriteriaMapList ::: getComparResultForCriterias mlReturnList >>> "+mlReturnList.toString());
		return mlReturnList;	
	}
	
	/**
	 * @param context
	 * @param mapQuery
	 * @param mapQuery2
	 * @return Unique MapList of CM Comparison for search conditions
	 * @throws Exception
	 */
	public static MapList getComparedResultForCriterias(Context context, Map mapQuery,Map mapQuery2)throws Exception {
		context.printTrace(STR_APOLLO_WS_TRACE, "Method ::: getComparResultForCriterias starts");
		MapList mlReturnList = new MapList();
		
		//Generate Maplists for Both Criteria
		MapList mlCriteria1 = getReleasedCriterias(context,mapQuery,false); 
		MapList mlCriteria2 = getReleasedCriterias(context,mapQuery2,false);	

		//Compare IDs
		mlReturnList.addAll(getMatchingObjects(mlCriteria1,mlCriteria2));
		
		//Remove Processed Objects
		removeProcessed(mlCriteria1,STR_PROCESSED);
		removeProcessed(mlCriteria2,STR_PROCESSED);		
		
		
		//Similar Objects in Same Condition
		StringList slTemp = new StringList();
		StringList slDuplicate = new StringList();
		StringList sl =  ((List<Map>)mlCriteria1).stream().map(elementInfo -> {return (String) ((String)elementInfo.get(STR_CC) +pgApolloConstants.CONSTANT_STRING_PIPE+ (String) elementInfo.get(STR_DS) +pgApolloConstants.CONSTANT_STRING_PIPE+ (String) elementInfo.get(STR_CHAR) +pgApolloConstants.CONSTANT_STRING_PIPE+ (String) elementInfo.get(STR_CS));}).collect(Collectors.toCollection(StringList::new));
		context.printTrace(STR_APOLLO_WS_TRACE, "Method ::: sl >>>>>>>>>>>>>>>>>>>>>>> "+sl.size());
		context.printTrace(STR_APOLLO_WS_TRACE, "Method ::: sl >>>>>>>>>>>>>>>>>>>>>>> "+sl.toString());
		HashSet hs = new HashSet<String>(sl);
		context.printTrace(STR_APOLLO_WS_TRACE, "Method ::: hs >>>>>>>>>>>>>>>>>>>>>>> "+hs.size());
		context.printTrace(STR_APOLLO_WS_TRACE, "Method ::: hs >>>>>>>>>>>>>>>>>>>>>>> "+hs.toString());
		
		String sKey = null;
		if(sl.size() != hs.size()) {
			for(int s =0;s<sl.size();s++) {
				sKey = sl.get(s);
				if(slTemp.contains(sKey)) {
					slDuplicate.add(sKey);
				}else {
					slTemp.add(sKey);
				}
			}
		}
		
		//Check Target for Matching 
		//Get c1 list with each slDuplicate
		//Iterate it
		//Get c2 list with each slDuplicate
		//Find Best Match From Target
		//Create return list
		context.printTrace(STR_APOLLO_WS_TRACE, "Method ::: slDuplicate >>>>>>>>>>>>>>>>>>>>>>> "+slDuplicate.toString());
		
		Map mapCM1 = null;
		Map mapCM2 = null;
		Map mapTemp = null;
		String  sTemp;
		String sIsProcessed = null;
		String sConcatinationCMProps1 = null;
		String sConcatinationCMProps2 = null;
		MapList mlTargetBestMatchList = null;
		
		MapList mlPlaceHolderCriteria1 = new MapList();
		mlPlaceHolderCriteria1.addAll(mlCriteria1);
		
		//Compare Attributes
		for(int i = 0 ; i < mlPlaceHolderCriteria1.size();i++) {
			mlTargetBestMatchList = new MapList();
			mapCM1 = (Map)mlPlaceHolderCriteria1.get(i);
			sConcatinationCMProps1 = getConcatinatedValue(mapCM1,false);
			
			for(int j=0 ; j < mlCriteria2.size();j++) {
				mapCM2 = (Map) mlCriteria2.get(j);
				sConcatinationCMProps2 = getConcatinatedValue(mapCM2,false);
				
				if(sConcatinationCMProps1.equalsIgnoreCase(sConcatinationCMProps2)) {
					mapCM2.put(STR_PROCESSED, "yes");
					mlTargetBestMatchList.add(mapCM2);
				}
			}
			
			if(mlTargetBestMatchList.isEmpty()) {
				mapCM1.put(STR_PROCESSED, "yes");
				mlReturnList.add(concatinateMaps(mapCM1,new HashMap<>(),"YES","10","10"));
			}else if (!mlTargetBestMatchList.isEmpty()) {
				for(int a = 0 ; a <mlTargetBestMatchList.size();a++ ) {
					mapCM2 = (Map)mlTargetBestMatchList.get(a);
					
					if(isIdentical(mapCM1,mapCM2)) {
						mapCM1.put(STR_PROCESSED, "yes");
						mapCM2.put(STR_IDENTICAL, "yes");
						mlReturnList.add(concatinateMaps(mapCM1,mapCM2,"YES","20","20"));
						a = mlTargetBestMatchList.size();
					}				
				}
				removeProcessed(mlTargetBestMatchList,STR_IDENTICAL);
				if(!mlTargetBestMatchList.isEmpty()) {
					for(int b=0;b<mlTargetBestMatchList.size();b++) {
						if(b==0) {
							mapCM1.put(STR_PROCESSED, "yes");
							mlReturnList.add(concatinateMaps(mapCM1,mapCM2,"YES","15","15"));
						}else {
							mapCM2 = (Map)mlTargetBestMatchList.get(b);
							mlReturnList.add(concatinateMaps(new HashMap<>(),mapCM2,"YES","10","10"));
						}

					}
				}
				 sTemp = (String)mapCM1.getOrDefault(STR_PROCESSED, DomainConstants.EMPTY_STRING);
				if(!"yes".equals(sTemp)) {
					mapCM1.put(STR_PROCESSED, "yes");
					mlReturnList.add(concatinateMaps(mapCM1,new HashMap<>(),"YES","10","10"));
				}
			}
		
			removeProcessed(mlCriteria2,STR_PROCESSED);
			removeProcessed(mlCriteria1,STR_PROCESSED);		

		}
		
		for(int k = 0 ; k < mlCriteria2.size(); k++) {
			mapTemp = (Map)mlCriteria2.get(k);
			sIsProcessed = (String)mapTemp.getOrDefault(STR_PROCESSED, "not");
			if("not".equalsIgnoreCase(sIsProcessed)) {
				mlReturnList.add(concatinateMaps(new HashMap<>(), mapTemp,"YES","10","10"));
			}
		}
		for(int l = 0 ; l < mlCriteria1.size(); l++) {
			mapTemp = (Map)mlCriteria1.get(l);
			sIsProcessed = (String)mapTemp.getOrDefault(STR_PROCESSED, "not");
			if("not".equalsIgnoreCase(sIsProcessed)) {
				mlReturnList.add(concatinateMaps(mapTemp,new HashMap<>(),"YES","10","10"));
			}
		}
		context.printTrace(STR_APOLLO_WS_TRACE, "Method ::: getComparResultForCriterias mlReturnList >>> "+mlReturnList.toString());
		return mlReturnList;
	}
	
	public static void removeProcessed(MapList sourceMapList,String sKey)
	{
		Iterator<Map<String,Object>> itr = sourceMapList.iterator();
		Map<String,Object> m = null;
		String sProccesed ;
		while (itr.hasNext())
		{
			m =  itr.next();
			sProccesed = (String)m.getOrDefault(sKey,DomainConstants.EMPTY_STRING);
			if("yes".equalsIgnoreCase(sProccesed)) {
				itr.remove();
			}
		}
	}
	
	public static MapList getMatchingObjects(MapList mlCriteria1,MapList mlCriteria2) {
		
		MapList mlForFinalList = new MapList();
		Map<String, Object> mapCM1 = null;
		Map<String,Object> mapCM2 = null;
		String sCM1Id = null;
		String sCM2Id = null;

		for(int i = 0; i < mlCriteria1.size();i++) {
			mapCM1 = (Map) mlCriteria1.get(i);
			sCM1Id = (String)mapCM1.get(DomainConstants.SELECT_ID);
			for(int j=0;j< mlCriteria2.size();j++) {
				mapCM2 = (Map) mlCriteria2.get(j);
				sCM2Id = (String)mapCM2.get(DomainConstants.SELECT_ID);
				if(sCM1Id.equals(sCM2Id)) {
					//Remove mapCM1 From mlCriteria1 and mapCM2 from mlCriteria2
					mapCM1.put(STR_PROCESSED, "yes");
					mapCM2.put(STR_PROCESSED, "yes");
					//Return Status Green
					mlForFinalList.add(concatinateMaps(mapCM1,mapCM2,"YES","20","20"));
				}
			}
		}
		return mlForFinalList;
	} 

	
	/**
	 * @param compMap
	 * @param mlMatchedList
	 * @return MapList having merged data for Source CM and Taget CM
	 * @throws MatrixException
	 */
	public static MapList compareMaps(Map<String,Object> compMap, MapList mlMatchedList){
		
		Map<String,Object> cmMap2;
		MapList mlFinal = new MapList();
		for(int i =0; i < mlMatchedList.size();i++) {
			cmMap2 = (Map)mlMatchedList.get(i);

				if(isIdentical(compMap,cmMap2)) {
					mlFinal.add(concatinateMaps(compMap,cmMap2,"YES","20","20"));
				}else {
					mlFinal.add(concatinateMaps(new HashMap<>(),cmMap2,"YES","15","10"));
				}		
		}
		return mlFinal;
	}
	
	/**
	 * @param sourceMap
	 * @param destMap
	 * @return True if two CM Maps are matching based on attributes else False 
	 */
	public static boolean isIdentical(Map<String,Object> sourceMap,Map<String,Object> destMap) {
		boolean bResult = true;
		
		StringList slAttributeToCompare = StringUtil.split(STR_OTHER_COMPARE_ATTRIBUTES, pgApolloConstants.CONSTANT_STRING_COMMA);
		String s1 = null;
		String s2 = null;
		for(int i =0; i < slAttributeToCompare.size(); i ++) {
			s1 = (String)sourceMap.getOrDefault(slAttributeToCompare.get(i), DomainConstants.EMPTY_STRING);
			s2 = (String)destMap.getOrDefault(slAttributeToCompare.get(i), DomainConstants.EMPTY_STRING);
			if(!(s1.equals(s2))) {
				bResult = false;
				break;
			}
		}
		return bResult;
	}
	
	/**
	 * @param sourceMap
	 * @param bIncludeId
	 * @return Concatenated attribute values as String 
	 */
	public static String getConcatinatedValue(Map<String,Object> sourceMap,boolean bIncludeId) {
		StringBuilder sb = new StringBuilder("");
		if(null != sourceMap) {
			if(bIncludeId) {
				sb.append((String)sourceMap.get(DomainConstants.SELECT_ID));
			}
			sb.append((String)sourceMap.get(STR_CC));
			sb.append((String)sourceMap.get(STR_DS));//APOLLO 2018x.6 A10-926 - New attribute on Characteristic for Category Specifics
			sb.append((String)sourceMap.get(STR_CHAR));
			sb.append((String)sourceMap.get(STR_CS));
		}
		return sb.toString();
	}
	
	/**
	 * @param sourceMap
	 * @param destMap
	 * @param sComparable
	 * @param sStatus
	 * @return A concatenated Map from two CM Maps 
	 */
	public static Map<String,Object> concatinateMaps(Map<String,Object> sourceMap, Map<String,Object> destMap,String sComparable,String sStatus,String sStatus2) {
		Map<String,Object> mapReturn = new HashMap();
		StringList slCols = StringUtil.split(strCOMPARE_RESULT_COLS, pgApolloConstants.CONSTANT_STRING_COMMA);
		StringList slKeys = StringUtil.split(strCOMPARE_RESULT_KEYS, pgApolloConstants.CONSTANT_STRING_COMMA);
		//Loop for first Map
		for(int i = 0; i < slCols.size(); i++) {
			mapReturn.put("C1_"+slKeys.get(i),sourceMap.getOrDefault(slCols.get(i),DomainConstants.EMPTY_STRING));
		}
		//Loop for Second Map
		for(int j = 0; j < slCols.size(); j++) {
			mapReturn.put("C2_"+slKeys.get(j),destMap.getOrDefault(slCols.get(j),DomainConstants.EMPTY_STRING));
		}
		mapReturn.put("comparable", sComparable);
		mapReturn.put(STR_STATUS1, sStatus);
		mapReturn.put(STR_STATUS2, sStatus2);
		return mapReturn;
		
	}


	public static StringList getIDListFromMapList(MapList mlReleasedCriteriasinDB, String sSelectable) {
		StringList slIDList = new StringList();
		Map mpData;
		Object objValue;
		for(int i = 0 ; i < mlReleasedCriteriasinDB.size(); i++ ) {
			mpData = (Map)mlReleasedCriteriasinDB.get(i);
			if(mpData.containsKey(sSelectable)) {
				objValue = mpData.get(sSelectable);
				if(objValue instanceof String) {
					slIDList.add((String)objValue);
				}else if(objValue instanceof StringList) {
					slIDList.addAll((StringList)objValue);
				}
			}
		}
		return slIDList;
	}
	
	
	/** This method will fetch CM + PLM Parameter Data for Characteristics Master Widget A10-880 
	 * @param context
	 * @param sCriteriaId
	 * @param bIncludeInWorkCriteria 
	 * @return
	 * @throws Exception
	 */
	public static MapList getCharactristicMastersForCriteria(Context context,String sCriteriaId,boolean bNotReleased, boolean bIncludeInWorkCriteria) throws Exception {
		MapList mlCharDataInfoList = new MapList();
		DomainObject doCriteriaObj = DomainObject.newInstance(context, sCriteriaId);
		
		StringList slObjectSelects = new StringList();
		slObjectSelects.add(DomainConstants.SELECT_ID);
		slObjectSelects.add(DomainConstants.SELECT_NAME);
		slObjectSelects.add(DomainConstants.SELECT_REVISION);
		slObjectSelects.add(DomainConstants.SELECT_ATTRIBUTE_TITLE);
		slObjectSelects.add("islast");
		slObjectSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_REASON_FOR_CHANGE);
		slObjectSelects.add(CharacteristicMasterConstants.MASTER_TO_CHARACTERISTIC_SELECTABLE+DomainConstants.SELECT_ID);
		slObjectSelects.addElement("attribute["+pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGBUSINESSAREA+"]");			
		slObjectSelects.addElement("attribute["+pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGPRODUCTCATEGORYPLATFORM+"]");
		slObjectSelects.add(pgApolloConstants.CONSTANT_STRING_SELECT_TO.concat(ENOCriteriaEnum.Relationship.CRITERIA_OUTPUT.get(context).concat(pgApolloConstants.CONSTANT_STRING_SELECT_FROMDOT).concat(DomainConstants.SELECT_ID)));
		slObjectSelects.add(pgApolloConstants.CONSTANT_STRING_SELECT_TO.concat(ENOCriteriaEnum.Relationship.CRITERIA_OUTPUT.get(context).concat(pgApolloConstants.CONSTANT_STRING_SELECT_FROMDOT).concat(DomainConstants.SELECT_NAME)));
		slObjectSelects.add(pgApolloConstants.CONSTANT_STRING_SELECT_TO.concat(ENOCriteriaEnum.Relationship.CRITERIA_OUTPUT.get(context).concat(pgApolloConstants.CONSTANT_STRING_SELECT_FROMDOT).concat(DomainConstants.SELECT_CURRENT)));
		slObjectSelects.add(pgApolloConstants.CONSTANT_STRING_SELECT_TO.concat(ENOCriteriaEnum.Relationship.CRITERIA_OUTPUT.get(context).concat(pgApolloConstants.CONSTANT_STRING_SELECT_FROMDOT).concat(pgApolloConstants.SELECT_ISLAST)));
		slObjectSelects.add(pgApolloConstants.CONSTANT_STRING_SELECT_FROM+pgApolloConstants.RELATIONSHIP_PARAMETER_AGGREGATION+CONSTANT_STRING_SELECT_TORELATIONSHIP+pgApolloConstants.RELATIONSHIP_CHARACTERISTIC_TEST_METHOD+pgApolloConstants.CONSTANT_STRING_SELECT_TONAME);
		slObjectSelects.add(pgApolloConstants.CONSTANT_STRING_SELECT_FROM+pgApolloConstants.RELATIONSHIP_PARAMETER_AGGREGATION+CONSTANT_STRING_SELECT_TORELATIONSHIP+pgApolloConstants.RELATIONSHIP_CHARACTERISTIC_TEST_METHOD+pgApolloConstants.CONSTANT_STRING_SELECT_TOID);
		slObjectSelects.add(pgApolloConstants.CONSTANT_STRING_SELECT_FROM+pgApolloConstants.RELATIONSHIP_PARAMETER_AGGREGATION+CONSTANT_STRING_SELECT_TORELATIONSHIP+DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT+pgApolloConstants.CONSTANT_STRING_SELECT_TONAME);
		slObjectSelects.add(pgApolloConstants.CONSTANT_STRING_SELECT_FROM+pgApolloConstants.RELATIONSHIP_PARAMETER_AGGREGATION+CONSTANT_STRING_SELECT_TORELATIONSHIP+DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT+pgApolloConstants.CONSTANT_STRING_SELECT_TOID);
		
		StringList slRelSelects = new StringList();
		slRelSelects.add(DomainRelationship.SELECT_ID);
		
		StringBuilder sbObjWhere = new StringBuilder();
		sbObjWhere.append(DomainConstants.SELECT_CURRENT);
		sbObjWhere.append(pgApolloConstants.CONSTANT_STRING_DOUBLE_EQUAL);
		sbObjWhere.append(pgApolloConstants.CONSTANT_STRING_SINGLE_QUOTE);
		sbObjWhere.append(pgApolloConstants.STATE_RELEASED);
		sbObjWhere.append(pgApolloConstants.CONSTANT_STRING_SINGLE_QUOTE);
		
		
		StringBuilder sbObjWhereNotObsolete = new StringBuilder();
		sbObjWhereNotObsolete.append(pgApolloConstants.SELECT_IS_LAST);
		sbObjWhereNotObsolete.append(pgApolloConstants.CONSTANT_STRING_SPACE);
		sbObjWhereNotObsolete.append(pgApolloConstants.CONSTANT_STRING_DOUBLE_EQUAL);
		sbObjWhereNotObsolete.append(pgApolloConstants.CONSTANT_STRING_SPACE);
		sbObjWhereNotObsolete.append(pgApolloConstants.CONSTANT_STRING_SINGLE_QUOTE);
		sbObjWhereNotObsolete.append(pgApolloConstants.STR_TRUE_FLAG_CAPS);
		sbObjWhereNotObsolete.append(pgApolloConstants.CONSTANT_STRING_SINGLE_QUOTE);
		sbObjWhereNotObsolete.append(pgApolloConstants.CONSTANT_STRING_SPACE);
		sbObjWhereNotObsolete.append(pgApolloConstants.CONSTANT_STRING_DOUBLE_AMPERSAND);
		sbObjWhereNotObsolete.append(pgApolloConstants.CONSTANT_STRING_SPACE);
		sbObjWhereNotObsolete.append(DomainConstants.SELECT_CURRENT);
		sbObjWhereNotObsolete.append(pgApolloConstants.CONSTANT_STRING_NOT_EQUAL_SIGN);
		sbObjWhereNotObsolete.append(pgApolloConstants.CONSTANT_STRING_SINGLE_QUOTE);
		sbObjWhereNotObsolete.append(pgApolloConstants.STATE_OBSOLETE);
		sbObjWhereNotObsolete.append(pgApolloConstants.CONSTANT_STRING_SINGLE_QUOTE);
		
		String strObjWhere;
		if(bNotReleased) {
			strObjWhere = sbObjWhereNotObsolete.toString();
		}else {
			strObjWhere = sbObjWhere.toString();
		}
		
		MapList mlCMList = doCriteriaObj.getRelatedObjects(context, 
							pgApolloConstants.RELATIONSHIP_CRITERIA_OUTPUT,  // relationshipPattern
							pgApolloConstants.TYPE_CHARACTERISTICSMASTER,                  // typePattern
							slObjectSelects,						// objectSelects
							slRelSelects,				// relationshipSelects
							false,								// getTo
							true,								// getFrom
							(short)1,							// recurseToLevel
							strObjWhere,			// objectWhere
							DomainConstants.EMPTY_STRING,			// relationshipWhere
							0);									// limit
		

		if(!mlCMList.isEmpty()) {
			
			mlCMList = updateCMListForMultiValueAttributes(mlCMList);
			
			//Get Parameter Values
			StringList slCMIDList = getIDListFromMapList(mlCMList,CharacteristicMasterConstants.MASTER_TO_CHARACTERISTIC_SELECTABLE+DomainConstants.SELECT_ID);		
			StringList slENOICharSelects = new StringList();

			String strPicklistAttributesList = getPageProperty(context,pgApolloConstants.STR_APOLLO_CONFIG_PAGE_FILENAME,"pgApolloWidget.CharacteristicMasterWidget.PlmParamSelects");
			HashSet<String> hsPlmParamSelects = new HashSet<>();
			hsPlmParamSelects.add(STR_INTERFACE);					
			hsPlmParamSelects.addAll(StringUtil.split(strPicklistAttributesList, pgApolloConstants.CONSTANT_STRING_PIPE));

			slENOICharSelects.addAll(hsPlmParamSelects);
			slENOICharSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_OBSCUREUNITOFMEASURE);
					  	
		  	//Fetch Dimension Display Values Map
			Map mpDimensionNames = pgApolloCommonUtil.getDimensions(context);
		  			  	
		  	//Start: Changes for 22x Upgrade: handle Multi value API correctly
			StringList slMultiValueSelect=new StringList();
			slMultiValueSelect.add("interface");
			mlCharDataInfoList = DomainObject.getInfo(context, slCMIDList.toArray(new String[slCMIDList.size()]), slENOICharSelects,slMultiValueSelect);
			//End: Changes for 22x Upgrade: handle Multi value API correctly
		
			
			
			
			//Create MapList with final values
			Map mpCharData;
			StringList slInterface;
			String sDimension;
			String sDisplayUnit;
			IKweDictionary kweDico;
			IKweUnit kweCurrUnit;
			for(int j = 0 ; j < mlCharDataInfoList.size(); j++) {
				mpCharData = (Map) mlCharDataInfoList.get(j);
				//Update Dimension Value
				slInterface = (StringList) mpCharData.get(STR_INTERFACE);
				sDimension = identifyDimensionUsingInterface(slInterface,mpDimensionNames);
				mpCharData.put("ParamId",mpCharData.get(DomainConstants.SELECT_ID));
				mpCharData.put(pgApolloConstants.STR_CHAR_DIMENSION,sDimension);
				//Update Characteristic Title
				mpCharData.put(STR_CHAR,mpCharData.getOrDefault(DomainConstants.SELECT_ATTRIBUTE_TITLE,DomainConstants.EMPTY_STRING));
				//Update Display Unit Value
				mpCharData.remove(DomainConstants.SELECT_ATTRIBUTE_TITLE);
				sDisplayUnit = (String)mpCharData.getOrDefault(pgApolloConstants.SELECT_ATTRIBUTE_PLMPARAMDISPLAYUNIT,DomainConstants.EMPTY_STRING);
				if(UIUtil.isNotNullAndNotEmpty(sDisplayUnit)) {
					kweDico = KweInterfacesServices.getKweDictionary();
					kweCurrUnit = kweDico.findUnitBySymbol(context, sDisplayUnit);
					sDisplayUnit = kweCurrUnit.getNLSName(context);
					mpCharData.put(CharacteristicMasterConstants.PARAM_DISPLAY_UNIT, sDisplayUnit);
				}else {
					sDisplayUnit = (String)mpCharData.getOrDefault(pgApolloConstants.SELECT_ATTRIBUTE_OBSCUREUNITOFMEASURE, DomainConstants.EMPTY_STRING);
					mpCharData.put(CharacteristicMasterConstants.PARAM_DISPLAY_UNIT, sDisplayUnit);
				}
			}
			context.printTrace(STR_APOLLO_WS_TRACE, "Method getCharactristicMastersForCriteria ::: mpCharData >>>>>>>>>>>>>>>>>>>>>>> "+mlCMList.toString());
			context.printTrace(STR_APOLLO_WS_TRACE, "Method getCharactristicMastersForCriteria ::: mlCharDataInfoList >>>>>>>>>>>>>>>>>>>>>>> "+mlCharDataInfoList.toString());
			
			//Add BA & PCP Values
			updateCMDataToList(mlCMList,mlCharDataInfoList,slObjectSelects);
			//Add Criteria
			updateCriteriaDataToList(context,mlCharDataInfoList,bIncludeInWorkCriteria);
		}
		context.printTrace(STR_APOLLO_WS_TRACE, "Method getCharactristicMastersForCriteria ::: mpCharData >>>>>>>>>>>>>>>>>>>>>>> "+mlCMList.toString());
		context.printTrace(STR_APOLLO_WS_TRACE, "Method getCharactristicMastersForCriteria ::: mlCharDataInfoList >>>>>>>>>>>>>>>>>>>>>>> "+mlCharDataInfoList.toString());
		return mlCharDataInfoList;
	}
	

	/**
	 * Method to get updated map list after updating multi value attributes
	 * @param mlCMList
	 * @return
	 */
	public static MapList updateCMListForMultiValueAttributes(MapList mlCMList) 
	{
		if(null != mlCMList && !mlCMList.isEmpty())
		{
			Map mapCM;
			Iterator itrMapCM = mlCMList.iterator();
			while(itrMapCM.hasNext())
			{		
				mapCM = (Map) itrMapCM.next();
				updateMultiValueAttributeAsString(mapCM, pgApolloConstants.SELECT_ATTRIBUTE_ENTERPRISEPART_PGBUSINESSAREA);
				updateMultiValueAttributeAsString(mapCM, pgApolloConstants.SELECT_ATTRIBUTE_ENTERPRISEPART_PGPRODUCTCATEGORYPLATFORM);
			}
		}
		return mlCMList;
	}
	
	/** This method will add criteria details into return map for keys cm_criteria,cm_crinames
	 * @param mlCharDataInfoList
	 * @param bIncludeInWorkCriteria 
	 * @throws IOException 
	 * @throws MatrixException 
	 * @throws Exception 
	 */
	private static void updateCriteriaDataToList(Context context,MapList mlCharDataInfoList, boolean bIncludeInWorkCriteria) throws MatrixException, IOException{
		
		Map mpParamData;
		Object objId;
		Object objState;
		Object obj;
		Object objTM;
		Object objTMIds;
		Object objTMRD;
		Object objTMRDIds;
		String sCriteriaList;
		StringList slTM;
		StringList slTMIDs;
		StringList slTMRD;
		StringList slTMRDIDs;
		StringList slCriteriaId;
		StringList slCriteriaState;
		StringList slCriteriaIsLast;
		StringList slCriteriaList;
		StringList slCriteriaReleased;
		StringList slCriteriaReleasedIds;
		StringList slCriteriaName;
		String sCriteriaState;
		String sCriteriaIsLast;
		
	
		String slSelectKeyActual = getPageProperty(context,pgApolloConstants.STR_APOLLO_CONFIG_PAGE_FILENAME,"pgApolloWidget.CharacteristicMasterWidget.SelectKeysActual");
		String slSelectKeyUI = getPageProperty(context,pgApolloConstants.STR_APOLLO_CONFIG_PAGE_FILENAME,"pgApolloWidget.CharacteristicMasterWidget.SelectKeysUI");
		
		StringList slOldKeys = StringUtil.split(slSelectKeyActual, pgApolloConstants.CONSTANT_STRING_PIPE);
		StringList slNewKeys = StringUtil.split(slSelectKeyUI, pgApolloConstants.CONSTANT_STRING_PIPE);
		
		for(int i = 0; i < mlCharDataInfoList.size();i++) {
			mpParamData = (Map) mlCharDataInfoList.get(i);
			slCriteriaName= new StringList();
			slCriteriaReleased= new StringList();
			slCriteriaReleasedIds= new StringList();
			
			objId = mpParamData.get(pgApolloConstants.CONSTANT_STRING_SELECT_TO.concat(pgApolloConstants.RELATIONSHIP_CRITERIA_OUTPUT).concat(pgApolloConstants.CONSTANT_STRING_SELECT_FROMDOT).concat(DomainConstants.SELECT_ID));
			slCriteriaId = getStringListFromObject(objId);
			objState = mpParamData.get(pgApolloConstants.CONSTANT_STRING_SELECT_TO.concat(pgApolloConstants.RELATIONSHIP_CRITERIA_OUTPUT).concat(pgApolloConstants.CONSTANT_STRING_SELECT_FROMDOT).concat(DomainConstants.SELECT_CURRENT));
			slCriteriaState = getStringListFromObject(objState);
			obj = mpParamData.get(pgApolloConstants.CONSTANT_STRING_SELECT_TO.concat(pgApolloConstants.RELATIONSHIP_CRITERIA_OUTPUT).concat(pgApolloConstants.CONSTANT_STRING_SELECT_FROMDOT).concat(DomainConstants.SELECT_NAME));
			slCriteriaList = getStringListFromObject(obj);
			obj = mpParamData.get(pgApolloConstants.CONSTANT_STRING_SELECT_TO.concat(pgApolloConstants.RELATIONSHIP_CRITERIA_OUTPUT).concat(pgApolloConstants.CONSTANT_STRING_SELECT_FROMDOT).concat(pgApolloConstants.SELECT_IS_LAST));
			slCriteriaIsLast = getStringListFromObject(obj);
			
			for(int j=0; j<slCriteriaState.size();j++) {
				sCriteriaState = slCriteriaState.get(j);
				sCriteriaIsLast = slCriteriaIsLast.get(j);
				
				if((!bIncludeInWorkCriteria && pgApolloConstants.STATE_RELEASED.equalsIgnoreCase(sCriteriaState)) || (bIncludeInWorkCriteria && pgApolloConstants.STR_TRUE_FLAG.equalsIgnoreCase(sCriteriaIsLast) && !sCriteriaState.equalsIgnoreCase(pgApolloConstants.STATE_OBSOLETE)))
				{
					slCriteriaReleased.add(slCriteriaList.get(j) + "#" +slCriteriaId.get(j));
					slCriteriaReleasedIds.add(slCriteriaId.get(j));
					slCriteriaName.add(slCriteriaList.get(j));
				}
			}
			
			sCriteriaList  = StringUtil.join(slCriteriaReleased,pgApolloConstants.CONSTANT_STRING_PIPE);
			mpParamData.put("cm_criteria", sCriteriaList);
			mpParamData.put("cm_crinames", StringUtil.join(slCriteriaName,pgApolloConstants.CONSTANT_STRING_SPACE_PIPE_SPACE));
			mpParamData.put("criteriaids", StringUtil.join(slCriteriaReleasedIds,pgApolloConstants.CONSTANT_STRING_SPACE_PIPE_SPACE));
			
			
			objTM = mpParamData.get(pgApolloConstants.CONSTANT_STRING_SELECT_FROM+pgApolloConstants.RELATIONSHIP_PARAMETER_AGGREGATION+CONSTANT_STRING_SELECT_TORELATIONSHIP+pgApolloConstants.RELATIONSHIP_CHARACTERISTIC_TEST_METHOD+pgApolloConstants.CONSTANT_STRING_SELECT_TONAME);
			slTM = getStringListFromObject(objTM);
			mpParamData.put(CharacteristicMasterConstants.TEST_METHODS,StringUtil.join(slTM,pgApolloConstants.CONSTANT_STRING_SPACE_PIPE_SPACE) );
			
			objTMIds = mpParamData.get(pgApolloConstants.CONSTANT_STRING_SELECT_FROM+pgApolloConstants.RELATIONSHIP_PARAMETER_AGGREGATION+CONSTANT_STRING_SELECT_TORELATIONSHIP+pgApolloConstants.RELATIONSHIP_CHARACTERISTIC_TEST_METHOD+pgApolloConstants.CONSTANT_STRING_SELECT_TOID);
			slTMIDs = getStringListFromObject(objTMIds);
			mpParamData.put("tmids",StringUtil.join(slTMIDs,pgApolloConstants.CONSTANT_STRING_PIPE) );
			
			objTMRD = mpParamData.get(pgApolloConstants.CONSTANT_STRING_SELECT_FROM+pgApolloConstants.RELATIONSHIP_PARAMETER_AGGREGATION+CONSTANT_STRING_SELECT_TORELATIONSHIP+DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT+pgApolloConstants.CONSTANT_STRING_SELECT_TONAME);
			slTMRD = getStringListFromObject(objTMRD);
			mpParamData.put("cm_tmrd",StringUtil.join(slTMRD,pgApolloConstants.CONSTANT_STRING_SPACE_PIPE_SPACE) );
			
			
			objTMRDIds = mpParamData.get(pgApolloConstants.CONSTANT_STRING_SELECT_FROM+pgApolloConstants.RELATIONSHIP_PARAMETER_AGGREGATION+CONSTANT_STRING_SELECT_TORELATIONSHIP+DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT+pgApolloConstants.CONSTANT_STRING_SELECT_TOID);
			slTMRDIDs = getStringListFromObject(objTMRDIds);
			mpParamData.put("cm_tmrdids",StringUtil.join(slTMRDIDs,pgApolloConstants.CONSTANT_STRING_PIPE) );
			
			redefineMapWithNewKeys(mpParamData,slOldKeys,slNewKeys);
			removeUnwantedKeys(mpParamData);
			
		}
	}
	
	public static void redefineMapWithNewKeys(Map consolidatedMap,StringList slOldKeys,StringList slNewKeys) {
		
		
		String sOldKey = null;
		for(int i = 0 ; i < slOldKeys.size(); i++) {
			sOldKey = slOldKeys.get(i);
			if(consolidatedMap.containsKey(sOldKey)) {
				consolidatedMap.put(slNewKeys.get(i),consolidatedMap.get(sOldKey));
			}
			
		}
	}
	
	
	/**This Method will update Characteristics Map with CM Data like BA, PCP values
	 * @param mlCMList
	 * @param mlCharDataInfoList
	 * @param slCMDataToAdd
	 */
	private static void updateCMDataToList(MapList mlCMList, MapList mlCharDataInfoList, StringList slCMDataToAdd) {
		
		Map mpCMData;
		Map mpPlmParamData;
		String sCMParamId;
		String sParamId;
		for(int i = 0 ; i < mlCMList.size(); i++) {
			mpCMData = (Map)mlCMList.get(i);
			sCMParamId = (String)mpCMData.get(CharacteristicMasterConstants.MASTER_TO_CHARACTERISTIC_SELECTABLE+DomainConstants.SELECT_ID);
			if(UIUtil.isNotNullAndNotEmpty(sCMParamId)) {
				for(int j = 0; j < mlCharDataInfoList.size(); j++) {
					mpPlmParamData = (Map)mlCharDataInfoList.get(j);
					if(mpPlmParamData.containsKey(DomainConstants.SELECT_ID)) {
						sParamId =  (String)mpPlmParamData.get(DomainConstants.SELECT_ID);
						if(UIUtil.isNotNullAndNotEmpty(sCMParamId) && sParamId.equals(sCMParamId)) {
							for(int k = 0; k < slCMDataToAdd.size(); k++) {
								mpPlmParamData.put(slCMDataToAdd.get(k),mpCMData.get(slCMDataToAdd.get(k)));
							}
							break;
						}
					}
				}
			}
		}
		
	}
	public static String identifyDimensionUsingInterface(StringList slInterfaceList, Map<String, String> mpDimensionDisplayVals) {
		String sReturnDimension = DomainConstants.EMPTY_STRING;
			if(!mpDimensionDisplayVals.isEmpty()) {
		  		String sValue;
			  	for(int i = 0 ; i < slInterfaceList.size(); i++) {
			  		sValue = slInterfaceList.get(i);
			  		if(UIUtil.isNotNullAndNotEmpty(sValue) && mpDimensionDisplayVals.containsKey(sValue)) {
			  			sReturnDimension = mpDimensionDisplayVals.get(sValue);
			  			break;
			  		}
			  	}
		  	}
		return sReturnDimension;
	}

	
	 public static StringList getStringListFromObject(Object value) 
	    {
	        StringList stringList=new StringList();  
	        if(null != value)
	        {
		        if(value instanceof String)
		        {
		        	stringList.add((String)value);
		        }
		        else
		        {
		        	stringList=(StringList)value;
		        }
	        }
	        return stringList;
	    }
	

		
		/**
		 * This is a utility method to get the value from Page file property
		 * @param context
		 * @param strPageName : Page File name
		 * @param strKey : Property Key
		 * @return : returns value of the given property key
		 * @throws MatrixException 
		 * @throws IOException 
		 * @throws Exception
		 */
		public static String getPageProperty(Context context, String strPageName, String strKey) throws MatrixException, IOException {
			String strValue="";
			String strPageContent=(String) CacheUtil.getCacheObject(context, strPageName);		
			if(UIUtil.isNullOrEmpty(strPageContent)) {
				Page pageObject = new Page(strPageName);
				boolean isPageExists	= pageObject.exists(context);
				if(isPageExists)
				{
					pageObject.open(context);
					strPageContent = pageObject.getContents(context);
					pageObject.close(context);
					CacheUtil.setCacheObject(context, strPageName, strPageContent);
				}
				else
				{
					strPageContent = DomainConstants.EMPTY_STRING;
				}
				
			}			
			if(UIUtil.isNotNullAndNotEmpty(strPageContent) && UIUtil.isNotNullAndNotEmpty(strKey)) {
				Properties properties = new Properties();
				properties.load(new StringReader(strPageContent));
				strValue = getPropertyIgnoreCase(properties, strKey,DomainConstants.EMPTY_STRING);
			}
			return strValue;
		}
		
		 /**
		  * get value from {Properties}, if no key exist then return default value.
		  * 
		  * @param props
		  * @param key
		  * @param defaultV
		  * @return
		  */
		 public static String getPropertyIgnoreCase(Properties properties, String strKey, String strDefaultValue) {
			 String strValue = properties.getProperty(strKey);
			 if (null != strValue)
			 {
				 return strValue;
			 }
			 return strDefaultValue;
		 }
		
		 /**
		  * Method to get CM Associated for given Criteria Ids
		  * @param context
		  * @param slCriteriaList
		  * @param getInworkCMs
		  * @param fetchTargetLimit
		  * @return
		 * @throws Exception 
		  */
		public static MapList getCharacteristicMasterAssociatedWithCriterias(Context context, StringList slCriteriaList, boolean bGetInworkCMs, boolean bFetchTargetLimitVals) throws Exception 
		{
			
			//Set Class level data members
			if(UIUtil.isNullOrEmpty(strCMW_COL_KEYS)) {
				strCMW_COL_KEYS = getPageProperty(context,pgApolloConstants.STR_APOLLO_CONFIG_PAGE_FILENAME,"pgApolloWidget.CharacteristicMasterWidget.ColumnsKeys");
			}
			if(UIUtil.isNullOrEmpty(strCOMPARE_RESULT_COLS)) {
				strCOMPARE_RESULT_COLS = getPageProperty(context,pgApolloConstants.STR_APOLLO_CONFIG_PAGE_FILENAME,"pgApolloWidget.CharacteristicMasterWidget.CompareResultCols");
			}
			if(UIUtil.isNullOrEmpty(strCOMPARE_RESULT_KEYS)) {
				strCOMPARE_RESULT_KEYS = getPageProperty(context,pgApolloConstants.STR_APOLLO_CONFIG_PAGE_FILENAME,"pgApolloWidget.CharacteristicMasterWidget.CompareResultKeys");
			}
			
			
			MapList mlNewList = new MapList();
			MapList mlFinalCMList = new MapList();
			
			if(null != slCriteriaList && !slCriteriaList.isEmpty())
			{
				for(String sCriteriaId : slCriteriaList)
				{
					if(bFetchTargetLimitVals) 
					{
						mlNewList.addAll(getCharactristicMastersForCriteriaWithLimit(context,sCriteriaId,bGetInworkCMs,true));
					}else {
						mlNewList.addAll(getCharactristicMastersForCriteria(context,sCriteriaId,bGetInworkCMs,true));
					}		
				}
				
				if(bGetInworkCMs) {
					mlFinalCMList = filterLatestCMFromList(mlNewList);
					mlFinalCMList = removeDuplicatesFromList(mlFinalCMList,DomainConstants.SELECT_ID);
				}else {
					mlFinalCMList = removeDuplicatesFromList(mlNewList,DomainConstants.SELECT_ID);
				}
			}		
			
			return mlFinalCMList;			
		}
}

 


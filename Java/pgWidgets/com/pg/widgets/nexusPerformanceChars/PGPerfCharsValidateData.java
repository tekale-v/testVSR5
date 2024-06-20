package com.pg.widgets.nexusPerformanceChars;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

import com.matrixone.apps.domain.util.FrameworkUtil;
import com.pg.widgets.structuredats.PGStructuredATSConstants;
import com.pg.widgets.util.PGWidgetConstants;
import com.pg.widgets.util.PGWidgetUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.cpn.CPNCommonConstants;
import com.matrixone.apps.cpn.util.BusinessUtil;

import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.StringList;


public class PGPerfCharsValidateData {
	private static final Logger logger = Logger.getLogger(PGPerfCharsCopyFromProductData.class.getName());
	static final String STRING_STATUS = "status";
	
	public String validatePerformanceCharacteristicsData(Context context, String strJsonInput) throws Exception {
		System.out.println("---entered validatePerformanceCharacteristicsData strJsonInput---"+strJsonInput);
		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strJsonInput);
		MapList mlReturnList = new MapList();
		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonArrayBuilder jsonArrObjInfo = Json.createArrayBuilder();
		String sObjectId = jsonInputData.getString("objectId");
		System.out.println("---inside validatePerformanceCharacteristicsData sObjectId---"+sObjectId);
		try {
			StringList objectSelects = new StringList();
			objectSelects.add(DomainConstants.SELECT_ID);
			objectSelects.add(DomainConstants.SELECT_CURRENT);
			objectSelects.add(PGPerfCharsConstants.SELECT_ATTRIBUTE_PG_CHARACTERISTIC);
			objectSelects.add(PGPerfCharsConstants.SELECT_ATTRIBUTE_PG_CHARACTERISTICSPECIFIC);
			objectSelects.add(PGPerfCharsConstants.SELECT_ATTRIBUTE_PG_UNIT_OF_MEASURE);

			String relPattern = CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC;
			String type = PropertyUtil.getSchemaProperty(context, "type_pgPerformanceCharacteristic");
			String typePattern = type;
			String relWhere = null;

			StringList relSelects = new StringList();
			relSelects.add(DomainConstants.SELECT_RELATIONSHIP_ID);
			relSelects.add(PGPerfCharsConstants.SELECT_ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE);
			
			Map mPickListDataForCharacteristic = getPicklistValuesMap(context,
					PGPerfCharsConstants.STR_PG_PLI_CHARACTERISTIC, "Active");
			Map mPickListDataForCharacteristicSpecific = getPicklistValuesMap(context,
					PGPerfCharsConstants.STR_PG_PLI_CHARACTERISTICSPECIFIC, "Active");
			Map mPickListDataForUoM = getPicklistValuesMap(context,
					PGPerfCharsConstants.STR_PG_PLI_UNITOFMEASUREMASTERLIST, "Active");

			if (UIUtil.isNotNullAndNotEmpty(sObjectId)) {
				DomainObject doObj = DomainObject.newInstance(context, sObjectId);
				StringBuffer typeWhere = new StringBuffer();
				typeWhere.append("type!=").append(PGPerfCharsConstants.TYPE_PG_STABILITY_RESULTS);
				MapList mlOperatedList = doObj.getRelatedObjects(context, 
						relPattern,  // Relationship 
						typePattern, // Type
						objectSelects, // Bus Selectable
						relSelects, // Rel Selectable
						false, // Get To
						true, // Get From
						(short) 1, // Recursion Level
						typeWhere.toString(), // Where Clause for Type
						relWhere, // Where Clause for Relationship
						0);
				for (int i = 0; i < mlOperatedList.size(); i++) {
					Map mOperatedData = (Map) mlOperatedList.get(i);
					String sChara = (String) mOperatedData.get(PGPerfCharsConstants.SELECT_ATTRIBUTE_PG_CHARACTERISTIC);
					String sCharaSpec = (String) mOperatedData
							.get(PGPerfCharsConstants.SELECT_ATTRIBUTE_PG_CHARACTERISTICSPECIFIC);
					String sUoM = (String) mOperatedData.get(PGPerfCharsConstants.SELECT_ATTRIBUTE_PG_UNIT_OF_MEASURE);
					// DSM (DS) : 2018x.6 : APR_CW : ALM_46782 : Retrieve Characteristics Specific List based on Characteristics Value : START
					boolean bValid = validateWithPickList(context, sChara, mPickListDataForCharacteristic, sCharaSpec,
							mPickListDataForCharacteristicSpecific, sUoM, mPickListDataForUoM, mOperatedData);
					// DSM (DS) : 2018x.6 : APR_CW : ALM_46782 : Retrieve Characteristics Specific List based on Characteristics Value : END
					if (!bValid) {
						mlReturnList.add(mOperatedData);
						output = getJsonObjectFromMap(mOperatedData);
						jsonArrObjInfo.add(output);
					}
				}
			}
				
		/*	String strArgs[] = JPO.packArgs(argsMap);
			//bResult = copyCharacteristicsFromProductData(context, strArgs);
			if (bResult) {
				output.add(PGPerfCharsConstants.KEY_STATUS, PGPerfCharsConstants.VALUE_SUCCESS);
				strReturnVal = output.build().toString();
			} else {
				output.add(PGPerfCharsConstants.KEY_STATUS, PGPerfCharsConstants.VALUE_FAIL);
				strReturnVal = output.build().toString();
			}  */
		} catch (Exception excep) {
			logger.log(Level.SEVERE, PGPerfCharsConstants.EXCEPTION_MESSAGE_PERF_CHAR_COPYFROM_PROD_DATA, excep);
			output.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
			//output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
			//return output.build().toString();
		}   
		return jsonArrObjInfo.build().toString();
	}
	
	
	/**
	 * Validate input value from Import with corresponding Picklist value
	 * @param context
	 * @param sChara
	 * @param mPickListDataForCharacteristic
	 * @param sCharaSpec
	 * @param mPickListDataForCharacteristicSpecific
	 * @param sUoM
	 * @param mPickListDataForUoM
	 * @param mOperatedData 
	 * @return boolean
	 */
	private boolean validateWithPickList(Context context, String sChara, Map mPickListDataForCharacteristic,
			String sCharaSpec, Map mPickListDataForCharacteristicSpecific, String sUoM, Map mPickListDataForUoM, Map mOperatedData) {

		try {
			if (null != mPickListDataForCharacteristic && !mPickListDataForCharacteristic.isEmpty()
					&& null != mPickListDataForCharacteristicSpecific
					&& !mPickListDataForCharacteristicSpecific.isEmpty() && null != mPickListDataForUoM
					&& !mPickListDataForUoM.isEmpty()) {

				StringList slPickListDataForCharacteristic = (StringList) mPickListDataForCharacteristic
						.get("field_display_choices");
				StringList slPickListDataForCharacteristicSpecific = (StringList) mPickListDataForCharacteristicSpecific
						.get("field_display_choices");
				StringList slPickListDataForUoM = (StringList) mPickListDataForUoM.get("field_display_choices");

				boolean bCharacterstics = UIUtil.isNotNullAndNotEmpty(sChara);
				boolean bCharactersticsSpecific = UIUtil.isNotNullAndNotEmpty(sCharaSpec);
				boolean bUoM = UIUtil.isNotNullAndNotEmpty(sUoM);
				boolean bCheckCharacterstics = false;
				boolean bCheckCharactersticsSpecific = false;
				boolean bCheckUoM = false;

				// DSM (DS) : 2018x.6 : APR_CW : ALM_46782 : Retrieve Characteristics Specific List based on Characteristics Value : START
				boolean bShortCodeMatchingCS =  false;
				boolean bDefaultCharSpecValidation = false;
				String sCharShortCode = "";
				boolean isObjectID = FrameworkUtil.isObjectId(context,sChara);
				String sCharectersticID = "";
				StringList slbusSelects = new StringList();
				slbusSelects.add(DomainObject.getAttributeSelect(PGPerfCharsConstants.ATTRIBUTE_PG_SHORT_CODE));
				slbusSelects.add("from["+PGPerfCharsConstants.REL_CHARACTERISTICTOCHARACTERISTICSPECIFICS+"]");
				String bHasCharSpecifics = "";
				
				//Defect - 54594 - Aug CW - Invalid Entries error when adding correct performance characteristics - Starts
				Map mapObject;
				String sCharSpecId = DomainConstants.EMPTY_STRING;
				//Defect - 54594 - Aug CW - Invalid Entries error when adding correct performance characteristics - Ends
		
				if(!isObjectID)
			       {
						//Defect - 54594 - 2022x.03 - Aug CW - Invalid Entries error when adding correct performance characteristics - Starts
						// Get Latest Revision for Characteristics 
						mapObject = getLatestActiveObjectInfo(context, PGPerfCharsConstants.TYPE_PG_PLICHARACTERISTIC, sChara, new StringList());				
						if(null != mapObject && !mapObject.isEmpty())
						{
							sCharectersticID = (String)mapObject.get(DomainConstants.SELECT_ID);
						}
						//Defect - 54594 - 2022x.03 - Aug CW - Invalid Entries error when adding correct performance characteristics - Ends
					
			       }
				if(UIUtil.isNotNullAndNotEmpty(sCharectersticID)) {
					
					 DomainObject domCategoryCharacteristics = DomainObject.newInstance(context,sCharectersticID);				 
					 
					 Map objMap = domCategoryCharacteristics.getInfo(context,slbusSelects);
					 bHasCharSpecifics = (String) objMap.get("from["+PGPerfCharsConstants.REL_CHARACTERISTICTOCHARACTERISTICSPECIFICS+"]");
					 
					if ("TRUE".equalsIgnoreCase(bHasCharSpecifics)) {
						 sCharShortCode = (String) objMap.get(DomainObject.getAttributeSelect(PGPerfCharsConstants.ATTRIBUTE_PG_SHORT_CODE));
					
						// Characteristics Specific Validation
						//Defect - 54594 - 2022x.03 - Aug CW - Invalid Entries error when adding correct performance characteristics - Starts
						mapObject = getLatestActiveObjectInfo(context, PGPerfCharsConstants.TYPE_PG_PLICHARSPECIFICS, sCharaSpec, new StringList());				
						if(null != mapObject && !mapObject.isEmpty())
						{
							sCharSpecId = (String)mapObject.get(DomainConstants.SELECT_ID);
						}
						StringList slCharSpecShortCode = new StringList();
						if (UIUtil.isNotNullAndNotEmpty(sCharSpecId))
						{
							domCategoryCharacteristics.setId(sCharSpecId);							
							slCharSpecShortCode = domCategoryCharacteristics.getInfoList(context,
									"to[pgCharateristicToCharateristicSpecifics].from.attribute[pgShortCode]");
						}
						//Defect - 54594 - 2022x.03 - Aug CW - Invalid Entries error when adding correct performance characteristics - Ends
						
						//LOG4J.info("SHORT CODE of Characteristics Specific Object : " + slCharSpecShortCode);
						if (BusinessUtil.isNotNullOrEmpty(slCharSpecShortCode)
								&& slCharSpecShortCode.contains(sCharShortCode))
							bShortCodeMatchingCS = true;
						//LOG4J.info("Characteristics Specific Validation Result with Short Code : " + bShortCodeMatchingCS);
					} else {
						bDefaultCharSpecValidation = slPickListDataForCharacteristicSpecific.contains(sCharaSpec);
					}
				}
				// Build Color Code for Characteristics				
				if (!bCharacterstics || slPickListDataForCharacteristic.contains(sChara)) {
					bCheckCharacterstics = true;
					mOperatedData.put(PGPerfCharsConstants.ATTRIBUTE_PG_CHARACTERISTIC, "Pass");
				} else {
					mOperatedData.put(PGPerfCharsConstants.ATTRIBUTE_PG_CHARACTERISTIC, "Fail");
				}
				
				// Build Color Code for Characteristics Specific
				if(!bCharactersticsSpecific || bShortCodeMatchingCS || bDefaultCharSpecValidation) {
					bCheckCharactersticsSpecific = true;
					mOperatedData.put(PGPerfCharsConstants.ATTRIBUTE_PG_CHARACTERISTICSPECIFIC, "Pass");
				} else {
					mOperatedData.put(PGPerfCharsConstants.ATTRIBUTE_PG_CHARACTERISTICSPECIFIC, "Fail");
				}
				
				// Build Color Code for Unit of Measure
				if (!bUoM || slPickListDataForUoM.contains(sUoM)) {
					bCheckUoM = true;
					mOperatedData.put(PGPerfCharsConstants.ATTRIBUTE_PG_UNIT_OF_MEASURE, "Pass");
				}else {
					mOperatedData.put(PGPerfCharsConstants.ATTRIBUTE_PG_UNIT_OF_MEASURE, "Fail");
				}

				if (bCheckCharacterstics && bCheckCharactersticsSpecific && bCheckUoM)
					return true;
			}
		} catch (Exception e) {
			
		}
		return false;
	}
	
	
	/**
	 * Method to get Latest Active Object Info
	 * @param context
	 * @param strType
	 * @param strName
	 * @param slBusSelects
	 * @return
	 * @throws Exception
	 */
	public static Map getLatestActiveObjectInfo(Context context, String strType, String strName, StringList slBusSelects) throws Exception
	{
		Map returnMap = new HashMap();
		
		//Object Select
		StringList slObjSelect = new StringList();
		slObjSelect.add(DomainConstants.SELECT_ID);	
		slObjSelect.addAll(slBusSelects);
		slObjSelect.add(DomainConstants.SELECT_ORIGINATED);	

		//Where Condition : current == Active 
		StringBuilder sbWhereCondition = new StringBuilder(DomainConstants.SELECT_CURRENT).append(PGPerfCharsConstants.CONSTANT_STRING_DOUBLE_EQUAL).append(PGPerfCharsConstants.STATE_ACTIVE);
		
		//Fetch object info
		MapList mlObjectDetails =  DomainObject.findObjects(context, // context
				strType, // type pattern
				strName, // name pattern
				DomainConstants.QUERY_WILDCARD, // revision pattern	
				DomainConstants.QUERY_WILDCARD, // owner pattern	
				PGPerfCharsConstants.VAULT_ESERVICE_PRODUCTION, // vault pattern	
				sbWhereCondition.toString(), // where expression
				false, // expand type
				slObjSelect); // Object Selectables
		
		if(null != mlObjectDetails && !mlObjectDetails.isEmpty())
		{
			mlObjectDetails.addSortKey(DomainConstants.SELECT_ORIGINATED, PGPerfCharsConstants.STR_DESCENDING, "date");
			mlObjectDetails.sort();
			returnMap =	(Map) mlObjectDetails.get(0) ;	
		}
		
		return returnMap;
	}
	//DSM (DS) 2018x.0 -post upgrade- to fix- The value of Characteristic Specifics column for PC table cannot be selected- end
	
	public Map getPicklistValuesMap(Context context, String picklistname, String pickliststate)throws Exception
	{
		StringTokenizer strTok = null;
		StringTokenizer strTokItem = null;
		String sPicklistItemName = "";
		StringList picklistItems = new StringList();
		String sNext="";
		String mqlString = "";
		String strResult  = "";
		Map mpReturnMap = new HashMap();
		StringList slPicklistID = new StringList();
		if ("".equals(pickliststate) || "Active".equalsIgnoreCase(pickliststate) || "Exists".equalsIgnoreCase(pickliststate))
		{
			mqlString = "temp query bus " + picklistname + " * * where \'(current != New && current != Inactive)\' !expand select id dump |";
		}
		try
		{
			strResult = MqlUtil.mqlCommand(context,mqlString.toString()).trim();
		}
		catch (Exception e)
		{
			
		}
		strTok = new StringTokenizer(strResult,"\n");
		//DSO 2013X.4 -  - Added code for displaying default picklist field value in form as Blank -Start
		if(UIUtil.isNotNullAndNotEmpty(strResult) && !"pgPLIUnitofMeasureWD".equals(picklistname)){
			picklistItems.add(DomainConstants.EMPTY_STRING);
		}
		while (strTok.hasMoreTokens())
		{
			sPicklistItemName="";
			sNext=strTok.nextToken();
			strTokItem = new StringTokenizer(sNext,"|");
			strTokItem.nextToken();
			//DSO 2013X.4 -  : Modified logic to skip the entries where revision is included as the token : START
			sPicklistItemName = strTokItem.nextToken();
			//DSO 2013X.4 -  : Modified logic to skip the entries where revision is included as the token : END
			//DSO 2013X.4 -  : Modified logic to return Range map with key and value as Name and ID : START
			picklistItems.add(sPicklistItemName);
		}
		//Added by DSM (DS) 2015X.1 - To sort the Class picklist   -START
		picklistItems.sort();
		//Added by DSM (DS) 2015X.1 - To sort the Class picklist   -END
		mpReturnMap.put("field_display_choices", picklistItems);
		mpReturnMap.put("field_choices", picklistItems);
		return mpReturnMap;
		//DSO 2013X.4 -  : Modified logic to return Range map with key and value as Name and ID : END
	}
	
	/**
	 * Method to convert Map to JSON object
	 * @param mObjectAttributeValues
	 * @return
	 */
	private JsonObjectBuilder getJsonObjectFromMap(Map<?,?> mObjectAttributeValues) {
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		for (Map.Entry<?, ?> entry : mObjectAttributeValues.entrySet()) {
			jsonReturnObj.add((String) entry.getKey(), (String) entry.getValue());
		}
		return jsonReturnObj;
	}
}

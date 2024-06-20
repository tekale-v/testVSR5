package com.pg.widgets.nexusPerformanceChars;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.dassault_systemes.evp.messaging.utils.UIUtil;
import com.matrixone.apps.awl.util.BusinessUtil;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.pg.widgets.util.PGWidgetUtil;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.Pattern;
import matrix.util.StringList;

import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;


public class PGFetchNexusTMSpecificsPerfChars {


	public static PGPerfCharsUtil pgPerfCharsUtil = new PGPerfCharsUtil();

	public String fetchNexusTMSpecificsPerfChars(Context context, String strJsonInput) throws Exception {
		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strJsonInput);
		JsonObjectBuilder jsonReturnBasicDetails = Json.createObjectBuilder();
		JsonObjectBuilder jsonFinalReturnObj = Json.createObjectBuilder();
		JsonObjectBuilder jsonReturnNexusParameterDetailsWrapObj = Json.createObjectBuilder();
		StringList slTestMethodIdList = null;
		Map mEachTMDetails  =new HashMap<>();
		MapList mlNexusCondAttribDetails  =new MapList();
		Map tempNCAMap = null;
		String strTMId = DomainConstants.EMPTY_STRING;
		String strTMVersionId ;
		MapList mltempNCAMapList = null;
		MapList mlNexusParameterDetails  =new MapList();
		Map mBasics  =new HashMap<>();
		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonArrayBuilder jsonArrDataInfo = Json.createArrayBuilder();
		JsonArrayBuilder jsonReturnNexusCharacterisitcsTableDetails = Json.createArrayBuilder();
		JsonArrayBuilder jsonReturnNexusParameterDetails = Json.createArrayBuilder();
		String sObjectId = jsonInputData.getString(PGPerfCharsConstants.CONST_TESTMETHOD_ID);
		String strPerfCharId = jsonInputData.getString(PGPerfCharsConstants.CONST_PERFCHAR_ID);
		String strCharacteristicName = jsonInputData.getString(PGPerfCharsConstants.CONST_CHARACTERISTIC_NAME);
		StringList slTMIdList = FrameworkUtil.split(sObjectId, "|");
		StringList slPCIdList = FrameworkUtil.split(strPerfCharId, ",");
		Map mpNexusTMDetails  = getNexusTMDetails(context, slTMIdList,slPCIdList,strCharacteristicName);
		if(!mpNexusTMDetails.isEmpty())
		{
			for(int iTMs = 0 ; iTMs<slTMIdList.size() ; iTMs++)
			{
				mEachTMDetails =(Map)mpNexusTMDetails.get(slTMIdList.get(iTMs));
				mlNexusCondAttribDetails = (MapList)mEachTMDetails.get(PGPerfCharsConstants.CONST_NEXUS_CONDITION_ATTRIBUTE_DETAILS);
				mlNexusParameterDetails =(MapList)mEachTMDetails.get(PGPerfCharsConstants.CONST_NEXUS_PARAMETER_DETAILS); 
				slTestMethodIdList =(StringList)mEachTMDetails.get(PGPerfCharsConstants.CONST_NEXUS_TM_ID_LIST);
				mBasics =(Map)mEachTMDetails.get(PGPerfCharsConstants.CONST_NEXUS_TM_BASICS); 
				
				jsonReturnBasicDetails = pgPerfCharsUtil.getJsonObjectFromMap(mBasics);
				jsonReturnNexusCharacterisitcsTableDetails = pgPerfCharsUtil.getJsonDataFromMapList(mlNexusParameterDetails);
				
				jsonFinalReturnObj.add(PGPerfCharsConstants.SELECT_ID, (String)slTMIdList.get(iTMs));
				jsonFinalReturnObj.add(PGPerfCharsConstants.CONST_NEXUS_TEST_METHOD_BASICS, jsonReturnBasicDetails);
				jsonFinalReturnObj.add(PGPerfCharsConstants.CONST_NEXUS_CHAR_TABLE, jsonReturnNexusCharacterisitcsTableDetails);
				
				
				for(int iTMCount = 0 ; iTMCount < slTestMethodIdList.size() ; iTMCount++)
				{
					mltempNCAMapList = new MapList();
					for(int iNCACount=0; iNCACount < mlNexusCondAttribDetails.size(); iNCACount++)
					{
						tempNCAMap = (Map)mlNexusCondAttribDetails.get(iNCACount);
						strTMId = (String)tempNCAMap.get(DomainObject.getAttributeSelect(PGPerfCharsConstants.ATTR_PG_NEXUS_TESTMETHOD_ID));
						strTMVersionId = (String)tempNCAMap.get(DomainObject.getAttributeSelect(PGPerfCharsConstants.ATTR_PG_NEXUS_TESTMETHOD_VERSIONID));
						strTMId = new StringBuilder(strTMId).append(strTMVersionId).toString();
						if(UIUtil.isNotNullAndNotEmpty(strTMId) && UIUtil.isNotNullAndNotEmpty(slTestMethodIdList.get(iTMCount)) && slTestMethodIdList.get(iTMCount).equalsIgnoreCase(strTMId))
						{
							mltempNCAMapList.add(tempNCAMap);	
						}
					}
					jsonReturnNexusParameterDetails = pgPerfCharsUtil.getJsonDataFromMapList(mltempNCAMapList);
					jsonReturnNexusParameterDetailsWrapObj.add(slTestMethodIdList.get(iTMCount), jsonReturnNexusParameterDetails);
				}
				
				jsonFinalReturnObj.add(PGPerfCharsConstants.CONST_NEXUS_ATTR_TABLE, jsonReturnNexusParameterDetailsWrapObj);
				jsonArrDataInfo.add(jsonFinalReturnObj);
			}
		}
		output.add(PGPerfCharsConstants.KEY_DATA,jsonArrDataInfo);
		return output.build().toString();
	}


	/**
	 * Sends all the details of a Nexus Test Method like Nexus Parameters , Nexus Condition attribute values etc
	 * @param context
	 * @param strTMObjectId,Performance Characteristic Id
	 * @return mpTMDetails
	 */

	public Map getNexusTMDetails(Context context, StringList slTMObjectIdList,StringList slPCIdList,String strCharacteristicName) throws Exception 
	{
		Map mpTMDetails = new HashMap();
		Map mpTotalMap = new HashMap();
		String strTMObjectId;
		String pgNexusPCParameterId;
		StringBuffer sbPGNexusValidConvertibleUnits = new StringBuffer();
		DomainObject domTMObj = null;
		StringList slAttribTableSelects = new StringList();
		StringList slCharTableSelects  = new StringList();
		StringList slMultiValUOMAttValList =  new StringList();
		MapList mlNexusParamListFinal = new MapList();
		MapList mlNexusConditionAttribList = new MapList();
		StringList slPerfCharAttriSelects = new StringList(DomainObject.getAttributeSelect(PGPerfCharsConstants.ATTR_PG_NEXUS_PC_PARAMETER_ID));
		StringList slMutilValueAttributes = StringUtil.split(PGPerfCharsConstants.MULTIVALUE_OBJECT_SELECTS_FOR_TEST_METHOD_NEXUS_CHAR_TABLE, ",");
		StringList slMutilValueAttributesBasics = StringUtil.split(PGPerfCharsConstants.MULTIVALUE_OBJECT_SELECTS_FOR_TEST_METHOD_NEXUS_BASICS, ",");
		slPerfCharAttriSelects.add(DomainObject.getAttributeSelect(PGPerfCharsConstants.ATTR_PG_METHOD_SPECIFICS));
		Pattern relPatternNexusCondAttribute = new Pattern(PGPerfCharsConstants.REL_PG_TM_TO_NEXUSCONDITONATTRIBUTES);
		Pattern typePatternNexusCondAttribute = new Pattern(PGPerfCharsConstants.TYPE_PG_NEXUSCONDITONATTRIBUTES);
		Pattern relPatternNexusParameter = new Pattern(PGPerfCharsConstants.REL_PG_TM_TO_NEXUSPARAMETERS);			
		Pattern typePatternNexusParameter = new Pattern(PGPerfCharsConstants.TYPE_PG_NEXUSPARAMETERS);
		StringList slTestMethodIdList = new StringList();
		String strTestMethodIdList = DomainConstants.EMPTY_STRING;
		String strTestMethodVersionId ;
		// DSM DEFECT Id: 57037 Data Types Captured in Nexus Characteristics Table Should Not Be Visible -START
		MapList mlFinalList = new MapList();
		Map mpConfigDetails = null;
		StringList slTempLableAttrList = new StringList();
		// DSM DEFECT Id: 57037 Data Types Captured in Nexus Characteristics Table Should Not Be Visible -END
		for(int TMCount = 0 ; TMCount < slTMObjectIdList.size(); TMCount++)
		{
			mpTMDetails = new HashMap();
			strTMObjectId = slTMObjectIdList.get(TMCount);
			if(UIUtil.isNotNullAndNotEmpty(strTMObjectId))
			{
				domTMObj = DomainObject.newInstance(context, strTMObjectId);
				// DSM 2022x - Req 48479 pgNexusExternalRef multivalue - START 
				Map mTMBasicInfo = domTMObj.getInfo(context, StringUtil.split(PGPerfCharsConstants.OBJECT_SELECTS_FOR_TEST_METHOD_NEXUS_BASICS, ","),slMutilValueAttributesBasics);
				mTMBasicInfo = getTMBasicMap(context,mTMBasicInfo,slMutilValueAttributesBasics);
				// DSM 2022x - Req 48479 pgNexusExternalRef multivalue - END 
				
				//List of Nexus Parameter Attributes - start
				String strObjSelects = PGPerfCharsConstants.OBJECT_SELECTS_FOR_TEST_METHOD_NEXUS_CHAR_TABLE;
				slCharTableSelects = StringUtil.split(strObjSelects, ",");
				// DSM DEFECT Id: 57037 Data Types Captured in Nexus Characteristics Table Should Not Be Visible -START
				StringList slConfigSelect = StringList.create(
						DomainObject.getAttributeSelect(PGPerfCharsConstants.ATTR_PG_NEXUS_DATA_TYPE_PARAMETER_LIST));
				MapList mlConfigObjectDetails = pgPerfCharsUtil.getNexusConfigObjectDetails(context, slConfigSelect);
				if (BusinessUtil.isNotNullOrEmpty(mlConfigObjectDetails)) {
					mpConfigDetails = (Map) mlConfigObjectDetails.get(0);
					if (mpConfigDetails != null && (!mpConfigDetails.isEmpty()) && mpConfigDetails.containsKey(
							DomainObject.getAttributeSelect(PGPerfCharsConstants.ATTR_PG_NEXUS_DATA_TYPE_PARAMETER_LIST))) {

						slTempLableAttrList = StringUtil.split(
								(String) mpConfigDetails
										.get(DomainObject.getAttributeSelect(PGPerfCharsConstants.ATTR_PG_NEXUS_DATA_TYPE_PARAMETER_LIST)),
										PGPerfCharsConstants.CONSTANT_STRING_PIPE);
					}
				}
				// DSM DEFECT Id: 57037 Data Types Captured in Nexus Characteristics Table Should Not Be Visible -END
				MapList mlNexusParamList =  getNexusDetails(context, domTMObj, relPatternNexusParameter, typePatternNexusParameter,  true, false, slCharTableSelects,strCharacteristicName) ;
				
				// DSM DEFECT Id: 57037 Data Types Captured in Nexus Characteristics Table Should Not Be Visible -START
				Iterator<Map<String, Object>> iterator = mlNexusParamList.iterator();
				while (iterator.hasNext()) {
					Map mpAddSelectedMap = iterator.next();
					String strNexusDataType = (String) mpAddSelectedMap.get(PGPerfCharsConstants.SELECT_ATTRIBUTE_PG_NEXUS_DATA_TYPE);
					if (slTempLableAttrList.contains(strNexusDataType)) {
						mlFinalList.add(mpAddSelectedMap);
					}
				}
				mlNexusParamList = mlFinalList;
				// DSM DEFECT Id: 57037 Data Types Captured in Nexus Characteristics Table Should Not Be Visible -END
				//Get the Nexus Condition attribute list 
				slAttribTableSelects.addAll(StringUtil.split(PGPerfCharsConstants.OBJECT_SELECTS_FOR_TEST_METHOD_NEXUS_ATTRIBUTE_TABLE, ","));
				mlNexusConditionAttribList = getNexusDetails(context, domTMObj, relPatternNexusCondAttribute,  typePatternNexusCondAttribute,  true, false, slAttribTableSelects,DomainConstants.EMPTY_STRING) ;

				
				for (int iPGNexusParameterListID=0; iPGNexusParameterListID<mlNexusParamList.size(); iPGNexusParameterListID++ ) 
				{
					Map mNexusParameterListMap = (Map)mlNexusParamList.get(iPGNexusParameterListID);
						pgNexusPCParameterId = (String)mNexusParameterListMap.get(DomainConstants.SELECT_ID);
						
						strTestMethodIdList = (String)mNexusParameterListMap.get(DomainObject.getAttributeSelect(PGPerfCharsConstants.ATTR_PG_NEXUS_TESTMETHOD_ID));
						strTestMethodVersionId = (String)mNexusParameterListMap.get(DomainObject.getAttributeSelect(PGPerfCharsConstants.ATTR_PG_NEXUS_TESTMETHOD_VERSIONID));
						strTestMethodIdList = new StringBuilder(strTestMethodIdList).append(strTestMethodVersionId).toString();
						mNexusParameterListMap.put(PGPerfCharsConstants.CONST_NEXUS_UNIQUE_GROUP_ID, strTestMethodIdList);
						if(UIUtil.isNotNullAndNotEmpty(strTestMethodIdList) && !slTestMethodIdList.contains(strTestMethodIdList))
						{
						slTestMethodIdList.add(strTestMethodIdList);
						}
						
						
						mNexusParameterListMap.put(DomainObject.getAttributeSelect(PGPerfCharsConstants.ATTR_PG_NEXUS_PC_PARAMETER_ID), pgNexusPCParameterId);
						for(int iMutilValueAttributes=0;iMutilValueAttributes<slMutilValueAttributes.size();iMutilValueAttributes++)
						{
							sbPGNexusValidConvertibleUnits.setLength(0);
							if (mNexusParameterListMap.containsKey(slMutilValueAttributes.get(iMutilValueAttributes))) {
								Object oMutilAttributesValList = mNexusParameterListMap.get(slMutilValueAttributes.get(iMutilValueAttributes));
								if(oMutilAttributesValList instanceof String)
								{
									sbPGNexusValidConvertibleUnits.append((String)oMutilAttributesValList);
								}else {
									slMultiValUOMAttValList = (StringList) mNexusParameterListMap.get(slMutilValueAttributes.get(iMutilValueAttributes));
									for (int i = 0; i < slMultiValUOMAttValList.size(); i++) {
										String strAttrVal = slMultiValUOMAttValList.get(i);
										sbPGNexusValidConvertibleUnits.append(strAttrVal); 
										if(i!=slMultiValUOMAttValList.size()-1)
											sbPGNexusValidConvertibleUnits.append("|"); 
									}
								}
								if(UIUtil.isNotNullAndNotEmpty(sbPGNexusValidConvertibleUnits.toString()))
								{
								mNexusParameterListMap.remove(slMutilValueAttributes.get(iMutilValueAttributes));
								mNexusParameterListMap.put(slMutilValueAttributes.get(iMutilValueAttributes), sbPGNexusValidConvertibleUnits.toString());
								}
							}
							
						}
						
						if(slPCIdList.size()>0 &&  UIUtil.isNotNullAndNotEmpty(pgNexusPCParameterId))
						{	
							mNexusParameterListMap.putAll(getSelectAttributeTableData(context, slPCIdList, pgNexusPCParameterId,
									mlNexusConditionAttribList, slPerfCharAttriSelects, mNexusParameterListMap));
						}

						mlNexusParamListFinal.add(mNexusParameterListMap);
					
				}
				mpTMDetails.put(PGPerfCharsConstants.CONST_NEXUS_PARAMETER_DETAILS, mlNexusParamListFinal);
				
				mpTMDetails.put("strTMObjectId", strTMObjectId);
				mpTMDetails.put(PGPerfCharsConstants.CONST_NEXUS_TM_BASICS, mTMBasicInfo);
				mpTMDetails.put(PGPerfCharsConstants.CONST_NEXUS_TM_ID_LIST, slTestMethodIdList);
				
				
				
				mpTMDetails.put(PGPerfCharsConstants.CONST_NEXUS_CONDITION_ATTRIBUTE_DETAILS, mlNexusConditionAttribList);
				mpTotalMap.put(strTMObjectId, mpTMDetails);
			}
		}
		return mpTotalMap ;
	}

	/**
	 * Gets all the details of a Selected Nexus Condition attribute values for Performance Characteristic
	 * @param context
	 * @param Performance Characteristic Id,NexusConditionAttribList
	 * @return NexusParameterListMap
	 */

	private Map getSelectAttributeTableData(Context context, StringList slPCIdList, String pgNexusPCParameterId,
			MapList mlNexusConditionAttribList, StringList slPerfCharAttriSelects, Map mNexusParameterListMap)
					throws FrameworkException, Exception {
		String strPGNexusNewPCId;
		String strPGSelectedAttribute;
		String strPGNexusPCParameterId;
		String strPGMethodSpecifics;
		StringList slPGMethodSpecifics;
		DomainObject domPerfCharObj;
		Map mPerfCharAttriSelects;
		for(int iPCs=0; iPCs<slPCIdList.size() ;iPCs++ )
		{
			strPGNexusNewPCId = slPCIdList.get(iPCs);
			if(FrameworkUtil.isObjectId(context, strPGNexusNewPCId))
			{
			domPerfCharObj = DomainObject.newInstance(context, strPGNexusNewPCId);
			mPerfCharAttriSelects = (Map)domPerfCharObj.getInfo(context, slPerfCharAttriSelects);
			strPGNexusPCParameterId = (String)mPerfCharAttriSelects.get(DomainObject.getAttributeSelect(PGPerfCharsConstants.ATTR_PG_NEXUS_PC_PARAMETER_ID));
			strPGMethodSpecifics =(String)mPerfCharAttriSelects.get(DomainObject.getAttributeSelect(PGPerfCharsConstants.ATTR_PG_METHOD_SPECIFICS)); 
			if(pgNexusPCParameterId.equals(strPGNexusPCParameterId) && UIUtil.isNotNullAndNotEmpty(strPGMethodSpecifics))
			{
				slPGMethodSpecifics =  FrameworkUtil.split(strPGMethodSpecifics, "~");	
				if(slPGMethodSpecifics.size()>2)
				{
					for(int iPGMethodSpecifics=3; iPGMethodSpecifics<slPGMethodSpecifics.size() ;iPGMethodSpecifics++ )
					{
						strPGSelectedAttribute = slPGMethodSpecifics.get(iPGMethodSpecifics);	
						if(UIUtil.isNotNullAndNotEmpty(strPGSelectedAttribute) && UIUtil.isNotNullAndNotEmpty(strPGNexusPCParameterId))
						{
							mNexusParameterListMap.putAll(getSelectedpgNexusValue(context,strPGSelectedAttribute,mlNexusConditionAttribList));	
						}
					}
				}
			}
			}
		}
		return mNexusParameterListMap;
	}

	/**
	 * Matches the details of a Selected Nexus Condition attribute values for Performance Characteristic
	 * @param context
	 * @param strPGSelectedAttribute,NexusConditionAttribList
	 * @return NexusParameterListMap
	 */
	private Map getSelectedpgNexusValue(Context context,String strPGSelectedAttribute,MapList mlNexusConditionAttribList)throws Exception {
		Map mFinalRet = new HashMap();
		String strPGNexusValues;
		for(int p =0; p<mlNexusConditionAttribList.size(); p++){
			Map mFinal =(Map) mlNexusConditionAttribList.get(p);
			strPGNexusValues =(String)mFinal.get(DomainObject.getAttributeSelect(PGPerfCharsConstants.ATTR_PG_NEXUS_VALUES)); 
			if(UIUtil.isNotNullAndNotEmpty(strPGNexusValues) && UIUtil.isNotNullAndNotEmpty(strPGSelectedAttribute) && strPGNexusValues.contains(strPGSelectedAttribute))
			{	
				mFinal.put(((String)mFinal.get(DomainObject.getAttributeSelect(PGPerfCharsConstants.ATTR_PG_NEXUS_ID)))+PGPerfCharsConstants.CONST_SELECT,strPGSelectedAttribute.trim());
				mFinalRet.putAll(mFinal);
			}
		}
		return mFinalRet;
	} 


	private MapList getNexusDetails(Context context,DomainObject domTMObj,Pattern relPattern, Pattern typePattern, boolean bFrom, boolean bTo, StringList slSelectStmt,String strCharacteristicName)throws Exception {
		 
		StringBuilder sbPCWhereClause = new StringBuilder();
		MapList mlNexusDetails = new MapList();
		boolean isCtxtPushed = false;
		try {
			if (UIUtil.isNotNullAndNotEmpty(strCharacteristicName)) {
				sbPCWhereClause = new StringBuilder(
						DomainObject.getAttributeSelect(PGPerfCharsConstants.ATTR_PG_CHARACTERISTIC)).append("== '")
						.append(strCharacteristicName).append("' && current== ")
						.append(PGPerfCharsConstants.STATE_ACTIVE);
			} else {
				sbPCWhereClause = new StringBuilder("current== ").append(PGPerfCharsConstants.STATE_ACTIVE);
			}
			//context pushed to get the Nexus Test Method Details
			ContextUtil.pushContext(context, PropertyUtil.getSchemaProperty(context, PGPerfCharsConstants.PERSON_USER_AGENT),
					DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
					isCtxtPushed = true;
			mlNexusDetails = domTMObj.getRelatedObjects(context, // Context
					relPattern.getPattern(), // Relationship
					typePattern.getPattern(), // Type
					slSelectStmt, // Object Select
					null, // Rel Select
					bTo, // get To
					bFrom, // get From
					(short) 1, // recurse level
					sbPCWhereClause.toString(), // object where clause
					null, // relationship where clause
					0); // limit
		}  finally {
			if (isCtxtPushed)
			ContextUtil.popContext(context);
		}
		return mlNexusDetails;

	}
	
	/**
	 * Below mathod gets the latest Test Method Ids by using TM name . 
	 * We can input the TM names in a stringlist for which we want to get the latest version of TMs . 
	 * to the part.In case of Transport Unit Part only one one SPS spec can be added.
	 * @param context
	 * @param args
	 * @return Latest Test Method Ids
	 * @throws Exception
	 */
	public StringList  getLatestTestMethodIds(Context context, StringList slTMNameList) throws Exception
	{
		
		String strObjWhere = "latest == TRUE";
		MapList objectsList = new MapList();
		String strTMID = DomainConstants.EMPTY_STRING;
		String strTMCurrentState =  DomainConstants.EMPTY_STRING;
		String strReleasedTM = DomainConstants.EMPTY_STRING;
		String strNonReleasedTM = DomainConstants.EMPTY_STRING;
		StringList slTMIds = new StringList();
		Map tMobject = null;
		
		
		StringList slObjSelect = new StringList();
		slObjSelect.add(DomainConstants.SELECT_LAST_ID);
		slObjSelect.add(DomainConstants.SELECT_ID);
		slObjSelect.add(DomainConstants.SELECT_CURRENT);
		
		for(int i=0 ; i < slTMNameList.size();i++)
		{
			strReleasedTM = DomainConstants.EMPTY_STRING;
			strNonReleasedTM = DomainConstants.EMPTY_STRING;
			objectsList = DomainObject.findObjects(context, PGPerfCharsConstants.TYPE_TEST_METHOD_SPECIFICATION, slTMNameList.get(i), "*", "*", null, strObjWhere, true, slObjSelect);
			//DSM(DS) 2018x.2 - Fix for INC1371758 Cannot add TM numbers in custom view to RMP tables - END
			if(objectsList != null && !objectsList.isEmpty())
            {
				for (Iterator iterator = objectsList.iterator(); iterator.hasNext();){
					tMobject = (Map) iterator.next();
					strTMID = (String)tMobject.get(DomainConstants.SELECT_ID);
					strTMCurrentState = (String)tMobject.get(DomainConstants.SELECT_CURRENT);
					
					if(UIUtil.isNotNullAndNotEmpty(strTMID))
					{
						
						if(!PGPerfCharsConstants.STATE_OBSOLETE.equals(strTMCurrentState) && (PGPerfCharsConstants.STATE_RELEASE.equals(strTMCurrentState) || PGPerfCharsConstants.STATE_COMPLETE.equals(strTMCurrentState)))
						{
							strReleasedTM = strTMID;
							
						}
						else
						{
							strNonReleasedTM = strTMID;
							
						}
					}
					
					
						
				}
			}
			
			
			if((UIUtil.isNotNullAndNotEmpty(strReleasedTM) && UIUtil.isNotNullAndNotEmpty(strNonReleasedTM)) || (UIUtil.isNotNullAndNotEmpty(strReleasedTM) && UIUtil.isNullOrEmpty(strNonReleasedTM)))
			{
				
					slTMIds.add(strReleasedTM);
				
			}
			else if(UIUtil.isNotNullAndNotEmpty(strNonReleasedTM))
			{
				
					slTMIds.add(strNonReleasedTM);
			
			} 
		}
		
		return slTMIds;
	}
	
	/*	public String getReportTypeValidation(Context context, String strDataType, String strPCReportType) throws Exception
	{
		String mFinalRet= DomainConstants.EMPTY_STRING;
		StringBuffer sbKey = new StringBuffer("emxCPN.ValidateNexusTestMethod");
		sbKey.append(strDataType.replaceAll(" ",""));
		sbKey.append(".");
		sbKey.append(strPCReportType.replaceAll(" ",""));
		// attribute[pgReportToNearest].value
		String strTypeSym =  EnoviaResourceBundle.getProperty(context, "emxCPNStringResource", context.getLocale(),sbKey.toString());
		//strTypeSym 
		
	
		return mFinalRet;
	}  */
	
	/*

     * Method to Check if the Test Method selected in the PC row is a Nexus or Not . It will return a boolean true only if the context Parts STRUCTUREDRELEASECRITERIAREQUIRED is true and then if TM authoringapplication is true .
     * @param context the matrix context
     * @param String args
     * @throws Exception if operation fails
     * @return boolean
	*/
    
    public String checkIsNexusTMForValidate(Context context, String[] args) throws Exception
	{
    	String isNexus = DomainConstants.EMPTY_STRING;
		PGFetchNexusTMSpecificsPerfChars NexusPerfCharsInstance = new PGFetchNexusTMSpecificsPerfChars();
		Map<?,?> mProgramMap = (Map)JPO.unpackArgs(args);	
		String strProductObjID = (String) mProgramMap.get("objectId");
		String strTestMethods = (String) mProgramMap.get("TMName");
		DomainObject domProductDataObj = null;
		String strPGNexusStructuredPerfCharsRequired;
		StringList slTestMethods = new StringList();
		StringList slTMNameList = new StringList();
		StringList slTMSelectables = new StringList(PGPerfCharsConstants.SELECT_ATTRIBUTE_PGAUTHORINGAPPLICATION);
		DomainObject domTMObj = null;
		String strAuthoredApplication = DomainConstants.EMPTY_STRING;
		if(UIUtil.isNotNullAndNotEmpty(strProductObjID) && UIUtil.isNotNullAndNotEmpty(strTestMethods))
		{
			domProductDataObj = DomainObject.newInstance(context, strProductObjID);
			strPGNexusStructuredPerfCharsRequired = (String)domProductDataObj.getInfo(context,PGPerfCharsConstants.SELECT_ATTRIBUTE_NEXUS_STRUCTURED_PERFCHARSREQUIRED);
			slTMNameList = StringUtil.split(strTestMethods, ",");
			if(slTMNameList.size()>0 && !FrameworkUtil.isObjectId(context, slTMNameList.get(0)))
			{
				slTestMethods = getLatestTestMethodIds(context,slTMNameList);
			}else if(slTMNameList.size()>0 && FrameworkUtil.isObjectId(context, slTMNameList.get(0))) 
			{
				slTestMethods.addAll(slTMNameList);
			}
			if(PGPerfCharsConstants.CONST_YES.equalsIgnoreCase(strPGNexusStructuredPerfCharsRequired) && slTMNameList.size() ==1)
			{
				domTMObj= DomainObject.newInstance(context, slTestMethods.get(0));			
				strAuthoredApplication = domTMObj.getAttributeValue(context, "pgAuthoringApplication");	
				if("Nexus".equalsIgnoreCase(strAuthoredApplication))
				{
					isNexus =  "yes";
				}
			}
			else if (PGPerfCharsConstants.CONST_YES.equalsIgnoreCase(strPGNexusStructuredPerfCharsRequired) && slTMNameList.size() > 1)
			{
				MapList TMDetails = BusinessUtil.getInfo(context, slTestMethods, slTMSelectables);
				for(int iTMs  =0; iTMs<TMDetails.size(); iTMs++)
				{
					Map oMap = (Map)TMDetails.get(iTMs);
					strAuthoredApplication =(String) oMap.get(PGPerfCharsConstants.SELECT_ATTRIBUTE_PGAUTHORINGAPPLICATION);
					if(UIUtil.isNotNullAndNotEmpty(strAuthoredApplication) && "Nexus".equalsIgnoreCase(strAuthoredApplication))
					{
						isNexus =  "no";
						break;
					}else{
						isNexus = DomainConstants.EMPTY_STRING;
					}
				}
			}
		}
		return isNexus;
	}
    
    
    // To get pgNexusPCParameterId by passing Performance Char Id
    public String checkIsNexusTMFromPCId(Context context, String strPerfCharId) throws Exception
	{
    	String isNexus =  DomainConstants.EMPTY_STRING;
    	DomainObject domPCObj = null;
    	String strPGNexusPCParameterId;
    	//ATTR_PG_NEXUS_PC_PARAMETER_ID
    	if(UIUtil.isNotNullAndNotEmpty(strPerfCharId))
    	{
    		domPCObj= DomainObject.newInstance(context, strPerfCharId);		
    		strPGNexusPCParameterId = domPCObj.getAttributeValue(context, PGPerfCharsConstants.ATTR_PG_NEXUS_PC_PARAMETER_ID);
    		if(UIUtil.isNotNullAndNotEmpty(strPGNexusPCParameterId))
    		{
    			isNexus = strPGNexusPCParameterId;
    		}
    	}
    	return isNexus;
 	}

    /*

    * Method to Check if the Test Method selected in the PC row is a Nexus or Not . It will return a boolean true only if the context Parts STRUCTUREDRELEASECRITERIAREQUIRED is true and then if TM authoringapplication is true .
    * @param context the matrix context
    * @param String args
    * @throws Exception if operation fails
    * @return boolean
	*/
   
   public String validateNexusTestMethod(Context context, String[] args) throws Exception
	{
	   String sResult = "";
	   
	   
		return null;
    	
	}
	
	public String getValidateResultForTestMethods(Context context,Map mProgramMap,String strFromEnv) throws Exception
	{
		String sReturn = DomainObject.EMPTY_STRING;
		String sAttributeVal = DomainObject.EMPTY_STRING;
		String sTempAttributeName = DomainObject.EMPTY_STRING;
		StringBuffer sbFinalRet= new StringBuffer();
		StringBuffer sbResult= new StringBuffer();
		//StringBuffer sbAttribute = new StringBuffer();
		String strDataType = DomainObject.EMPTY_STRING;
		DomainObject doNexusParameters = null;
		//emxCPN.Attribute.ValidateNexusTestMethodMandatorAttribute
		String strPCReportType = (String)mProgramMap.get(PGPerfCharsConstants.ATTR_PG_REPORTTYPE);
		String strTMNexusParameterId = (String)mProgramMap.get(PGPerfCharsConstants.ATTR_PG_NEXUS_PC_PARAMETER_ID);  
		StringList slGetAttributes =FrameworkUtil.split((EnoviaResourceBundle.getProperty(context, "emxCPNStringResource", context.getLocale(),"emxCPN.Attribute.ValidateNexusTestMethodMandatorAttribute")) , ",");
		if("trigger".equalsIgnoreCase(strFromEnv))
		{
			slGetAttributes = FrameworkUtil.split((EnoviaResourceBundle.getProperty(context, "emxCPNStringResource", context.getLocale(),"emxCPN.Trigger.ValidateNexusTestMethodMandatorAttribute")) , ",");
			strPCReportType = (String)mProgramMap.get(DomainObject.getAttributeSelect(PGPerfCharsConstants.ATTR_PG_REPORTTYPE));
			strTMNexusParameterId = (String)mProgramMap.get(DomainObject.getAttributeSelect(PGPerfCharsConstants.ATTR_PG_NEXUS_PC_PARAMETER_ID));  
		}
		if("widget".equalsIgnoreCase(strFromEnv) || "trigger".equalsIgnoreCase(strFromEnv) )
		{
			for(int iAttributes = 0 ;iAttributes < slGetAttributes.size() ; iAttributes++) 
			{
				sbResult.setLength(0);
				sAttributeVal = (String)mProgramMap.get(slGetAttributes.get(iAttributes));
				if(UIUtil.isNullOrEmpty(sAttributeVal)) {
					if(UIUtil.isNullOrEmpty(sTempAttributeName))
					{
						sTempAttributeName = slGetAttributes.get(iAttributes);
					}
					else
					{
						sTempAttributeName = sTempAttributeName + "," + slGetAttributes.get(iAttributes);
					}

				}
				if(UIUtil.isNotNullAndNotEmpty(sTempAttributeName))
				{
					sbResult.append(sTempAttributeName);		
					sbResult.append(" is mandatory");		
				}
			}
			sbFinalRet.append(sbResult.toString());
		}
		if(UIUtil.isNotNullAndNotEmpty(strTMNexusParameterId) && UIUtil.isNotNullAndNotEmpty(strPCReportType))
		{
			doNexusParameters = DomainObject.newInstance(context, strTMNexusParameterId);
			strDataType = doNexusParameters.getInfo(context, DomainObject.getAttributeSelect(PGPerfCharsConstants.ATTR_PG_NEXUS_PC_PARAMETER_TYPE));
			if(UIUtil.isNotNullAndNotEmpty(strDataType))
			{
				if("trigger".equalsIgnoreCase(strFromEnv))
				{
					strFromEnv = "widget";
				}
				sReturn = getReportTypeValidation(context, strDataType,  strPCReportType,strFromEnv);
				if(UIUtil.isNotNullAndNotEmpty(sReturn)) 
				{
					sbFinalRet.append("\n");
					sbFinalRet.append(sReturn);
				}
			}
		}
	return sbFinalRet.toString();
	}
	
	public String getReportTypeValidation(Context context, String strDataType, String strPCReportType,String strFromEnv) throws Exception
	{
		String strTypeSym =  DomainObject.EMPTY_STRING;
		StringBuffer sbFinalRet= new StringBuffer();
		StringBuffer sbKey = new StringBuffer("emxCPN.ValidateNexusTestMethod.");
		sbKey.append(strFromEnv);
		sbKey.append(".");
		sbKey.append(strDataType.replaceAll(" ",""));
		sbKey.append(".");
		sbKey.append(strPCReportType.replaceAll(" ",""));
		strTypeSym =  EnoviaResourceBundle.getProperty(context, "emxCPNStringResource", context.getLocale(),sbKey.toString());
		if(UIUtil.isNullOrEmpty(strTypeSym) || "emxCPN".contains(strTypeSym) ||  (sbKey.toString().equalsIgnoreCase(strTypeSym)))
		{
			strTypeSym = DomainObject.EMPTY_STRING;
		}
		return strTypeSym;
	}

	private Map getTMBasicMap(Context context, Map mTMBasicInfo, StringList slMutilValueAttributes) throws  Exception {
		StringBuffer sbAttributeVal = new StringBuffer();
		StringList slMultiValAttValList =  new StringList();
		// DSM DEFECT Id: 57181 [RTTQ]: Nexus TM attributes is not applied after clicking submit in TM specifics wizard -START
		String strFinalString = DomainConstants.EMPTY_STRING;
		// DSM DEFECT Id: 57181 [RTTQ]: Nexus TM attributes is not applied after clicking submit in TM specifics wizard -END
		for(int iMutilValueAttributes=0;iMutilValueAttributes<slMutilValueAttributes.size();iMutilValueAttributes++)
		{
			sbAttributeVal.setLength(0);
			if (mTMBasicInfo.containsKey(slMutilValueAttributes.get(iMutilValueAttributes))) {
				Object oMutilAttributesValList = mTMBasicInfo.get(slMutilValueAttributes.get(iMutilValueAttributes));
				if(oMutilAttributesValList instanceof String)
				{
					sbAttributeVal.append((String)oMutilAttributesValList);
				}else {
					slMultiValAttValList = (StringList) mTMBasicInfo.get(slMutilValueAttributes.get(iMutilValueAttributes));
					for (int i = 0; i < slMultiValAttValList.size(); i++) {
						String strAttrVal = slMultiValAttValList.get(i);
						sbAttributeVal.append(strAttrVal); 
						if(i!=slMultiValAttValList.size()-1)
							sbAttributeVal.append(","); 
					}
				}
				if(UIUtil.isNotNullAndNotEmpty(sbAttributeVal.toString()))
				{
					// DSM DEFECT Id: 57181 [RTTQ]: Nexus TM attributes is not applied after clicking submit in TM specifics wizard -START	
					strFinalString = sbAttributeVal.toString();
					mTMBasicInfo.remove(slMutilValueAttributes.get(iMutilValueAttributes));
					mTMBasicInfo.put(slMutilValueAttributes.get(iMutilValueAttributes), strFinalString);
				} else {
					strFinalString = DomainConstants.EMPTY_STRING;
					mTMBasicInfo.remove(slMutilValueAttributes.get(iMutilValueAttributes));
					mTMBasicInfo.put(slMutilValueAttributes.get(iMutilValueAttributes), strFinalString);
				}
				// DSM DEFECT Id: 57181 [RTTQ]: Nexus TM attributes is not applied after clicking submit in TM specifics wizard -END
			}

		}
		return mTMBasicInfo;
	}
}

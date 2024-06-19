package com.png.apollo;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class pgApolloWeightConversionUtility extends pgApolloConstants {
	
	
	/**
	 * This method is used to get basis weight and Linear Density of RMP
	 * @param context
	 * @param sRMId
	 * @param sRMCharacteristic
	 * @param sCharSpecific
	 * @return
	 * @throws Exception
	 */
	public static String getTargetValue (Context context, String sRMId, String sRMCharacteristic, String sCharSpecific) throws Exception
	{		
		context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getTargetValue with Char Spe");
		return getTargetValue(context, sRMId, sRMCharacteristic, sCharSpecific, false);				
	}
	
	/**
	 * This method is used to get basis weight and Linear Density of RMP
	 * @param context
	 * @param sRMId
	 * @param sRMCharacteristic
	 * @return
	 * @throws Exception
	 */
	public static String getTargetValue (Context context, String sRMId,  String sRMCharacteristic) throws Exception
	{		
		context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getTargetValue without Char Spe");
		return getTargetValue(context, sRMId, sRMCharacteristic, EMPTY_STRING, false);				
	}		
	
	/**
	 * This method is used to get basis weight and Linear Density of RMP
	 * @param context
	 * @param sRMId
	 * @param sRMCharacteristic
	 * @param sCharSpecific
	 * @return
	 * @throws Exception
	 */

	public static String getTargetValue (Context context, String sRMId, String sRMCharacteristic, String sCharSpecific, boolean bReturnError) throws Exception	
	{
		String sReturnValue = DomainConstants.EMPTY_STRING;

		context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getTargetValue >>>");
		context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getTargetValue - sRMId ="+sRMId+"\n sRMCharacteristic ="+sRMCharacteristic+"\n sCharSpecific ="+sCharSpecific+"\n bReturnError ="+bReturnError);
		if (UIUtil.isNullOrEmpty(sRMCharacteristic) || UIUtil.isNullOrEmpty(sRMId))
		{	
			return sReturnValue;
		}

		Map paramMap = new HashMap();
		paramMap.put("objectId", sRMId);
		paramMap.put("selectedTable", "pgVPDPerformanceCharacteristicTable");
		paramMap.put("pgVPDCPNCharacteristicDerivedFilter", "All");
		paramMap.put("CPNCharacteristicDisplayCustomFilter", "pgVPDPerformanceCharacteristicTable");

		String[] args = JPO.packArgs(paramMap);
		MapList mlCharacteristics = JPO.invoke(context, "emxCPNCharacteristicList", null, "getPerformanceChar", args, MapList.class);
		context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getTargetValue - mlCharacteristics ="+mlCharacteristics);

		if(null != mlCharacteristics && !mlCharacteristics.isEmpty())
		{	
			MapList mlCharDetails;
			StringList objSelects = new StringList();
			objSelects.add(DomainConstants.SELECT_ID);
			objSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PG_CHARACTERISTIC);
			objSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PG_TARGET);
			objSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PG_UPPER_TARGET);
			objSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PG_LOWER_TARGET);
			objSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PG_UNIT_OF_MEASURE);
			objSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_CHARACTERISTICSPECIFIC);

			Set setObjectAdditionalSelects = new HashSet();

			String sReportFunctionSelectable = new StringBuilder(pgApolloConstants.CONSTANT_STRING_SELECT_FROM).append(pgApolloConstants.RELATIONSHIP_PGPDTEMPLATES_TOP_GPLI_REPORTED_FUNCTION).append(pgApolloConstants.CONSTANT_STRING_SELECT_TONAME).toString();

			StringList slRMPSelects = new StringList();
			slRMPSelects.add(DomainConstants.SELECT_ID);
			slRMPSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PG_CLASS);
			slRMPSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PG_SUB_CLASS);
			slRMPSelects.add(sReportFunctionSelectable);

			StringList slMultiValueRMPSelects = new StringList();
			slMultiValueRMPSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PG_CLASS);
			slMultiValueRMPSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PG_SUB_CLASS);
			slMultiValueRMPSelects.add(sReportFunctionSelectable);

			mlCharacteristics.sortStructure("attribute[pgPFInheritanceType]","ascending","string");

			Map<String, Object> mapCharConfig;

			StringList slCharSpec = new StringList();
			Map mapAdditionalAttributeMapping;

			Set setAdditionalAttributes;

			boolean bSpecLimits = false;

			//Get the Parsed JSON in Map format for given page file
			Map mapOutput = pgApolloCommonUtil.getPagePropertyMapBasedOnJson(context, pgApolloConstants.STR_APOLLO_SUBSTITUTE_CONFIG_PAGE_FILENAME);
			context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getTargetValue mapOutput ="+mapOutput);

			if(UIUtil.isNullOrEmpty(sCharSpecific))
			{
				DomainObject domObject = DomainObject.newInstance(context,sRMId);
				Map mapRMP = domObject.getInfo(context, slRMPSelects, slMultiValueRMPSelects);	
				StringList slRMPClass = pgApolloCommonUtil.getStringListMultiValue(mapRMP.get(pgApolloConstants.SELECT_ATTRIBUTE_PG_CLASS));	
				String sRMPClass = slRMPClass.get(0);

				if(UIUtil.isNotNullAndNotEmpty(sRMPClass))
				{
					//For Given RMP Class - Fetch Valid configuration map list
					MapList mlApplicableConfigList = getValidConfigListForClass(context, mapOutput, sRMPClass, sRMCharacteristic, mapRMP);
					
					context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getTargetValue mlApplicableConfigList ="+mlApplicableConfigList);

					if(!mlApplicableConfigList.isEmpty())
					{
						for(Object objMap : mlApplicableConfigList)
						{
							mapCharConfig = (Map)objMap;

								bSpecLimits = (boolean) mapCharConfig.get(pgApolloConstants.STR_SPECLIMITS);

								if(bSpecLimits)
								{
									objSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PGLOWERSPECIFICATIONLIMIT);
									objSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PGUPPERSPECIFICATIONLIMIT);
								}
								if(mapCharConfig.containsKey(pgApolloConstants.KEY_ATTRIBUTESMAP))
								{
									mapAdditionalAttributeMapping = (Map)mapCharConfig.get(pgApolloConstants.KEY_ATTRIBUTESMAP);
									if(null != mapAdditionalAttributeMapping && !mapAdditionalAttributeMapping.isEmpty())
									{
										setAdditionalAttributes = mapAdditionalAttributeMapping.keySet();
										setObjectAdditionalSelects.addAll(setAdditionalAttributes);
									}
									context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getTargetValue - mapAdditionalAttributeMapping ="+mapAdditionalAttributeMapping);
								}
						}
						
						objSelects.addAll(setObjectAdditionalSelects);
						mlCharDetails = getCharDetails(context, mlCharacteristics, objSelects);	
						
						
						if(null != mlCharDetails && !mlCharDetails.isEmpty())
						{							
							sReturnValue =  getTargetValueBasedOnValidConfigurations(context, mlApplicableConfigList, mlCharDetails, bReturnError);
						}	
						
						
					}
				}
			}
			else
			{
				slCharSpec.add(sCharSpecific);
				StringList slUOM = new StringList();
				
				Map mpTemp = new HashMap();
				mpTemp.put(pgApolloConstants.STR_CHARACTERISTICS, sRMCharacteristic);
				mpTemp.put(pgApolloConstants.STR_CHARACTERISTICSPECIFICS, slCharSpec);
				MapList mlCharStandalone = new MapList();
				mlCharStandalone.add(mpTemp);
		
				//bSpecLimits - set value based on JSON - ClassesToFetchSpecLimits				
				StringList slClassesToFetchSpecLimits = (StringList)mapOutput.get("ClassesToFetchSpecLimits");	
				if(pgApolloCommonUtil.containsInListCaseInsensitive(sRMCharacteristic, slClassesToFetchSpecLimits))
				{
					bSpecLimits = true;
				}

				//Get mlCharDetails	
				mlCharDetails = getCharDetails(context, mlCharacteristics, objSelects);
				
				Map returnTargetMap = getTargetValueForListOfCharSpecs(context, mlCharDetails, mlCharStandalone, slUOM, bSpecLimits, new HashMap());				
				
				context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getTargetValue - returnTargetMap ="+returnTargetMap);
				if(returnTargetMap.containsKey(STR_ERROR))
				{
					if(bReturnError)
					{
						sReturnValue = (String)returnTargetMap.get(STR_ERROR);
					}
					else
					{
						sReturnValue = EMPTY_STRING;
					}
					return sReturnValue;
				}
				else
				{
					sReturnValue = (String)returnTargetMap.get(STR_TARGET);
				}
				context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getTargetValue - sReturnValue ="+sReturnValue);
				
				context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getTargetValue - Add in list sCharSpecific ="+sCharSpecific);
			}
		}

		return sReturnValue;
	}

	
	/**
	 * Method to get Char Details
	 * @param context
	 * @param mlCharacteristics
	 * @param objSelects
	 * @return
	 * @throws FrameworkException
	 */
	private static MapList getCharDetails(Context context, MapList mlCharacteristics, StringList objSelects) throws FrameworkException
	{
		MapList mlCharDetails = new MapList();
		StringList slCharacteristicList = new StringList();
		Map mpTemp;
		String strCharacteristicId;				

		for(Object objTemp : mlCharacteristics)
		{
			mpTemp = (Map) objTemp;
			strCharacteristicId = (String) mpTemp.get(DomainConstants.SELECT_ID);
			slCharacteristicList.add(strCharacteristicId);
		}
		
		if(!slCharacteristicList.isEmpty())
		{
			String[] saChar = slCharacteristicList.toArray(new String []{});
			mlCharDetails = DomainObject.getInfo(context, saChar, objSelects);					
		}
		return mlCharDetails;
	}
	
	
	
	/**
	 * This method is used to get basis weight and Linear Density of RMP
	 * @param context
	 * @param mlCharDetails
	 * @param sRMCharacteristic
	 * 	@param slCharSpec
	 * 	@param slUOM
	 * @param mapAdditionalAttributeMapping 
	 * @param fetchSpecLimits 
	 * @return mTargetMap
	 * @throws MatrixException
	 */
	
	private static Map getTargetValueForListOfCharSpecs (Context context, MapList mlCharDetails, MapList mlCharMapping, StringList slUoM, boolean fetchSpecLimits, Map mapAdditionalAttributeMapping) throws MatrixException
	{		

		context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getTargetValueForListOfCharSpecs >>>");
		Map mTargetMap =new HashMap();						
		BigDecimal bdTarget = null;
		BigDecimal bdLowerTarget = null;
		BigDecimal bdUpperTarget = null;
		String strCharacteristic;
		String strTarget= EMPTY_STRING;
		String strTargetChar;
		String strLowerTarget;
		String strUpperTarget;
		String strLowerSpecLimit;
		String strUpperSpecLimit;
		String strCharUOM= EMPTY_STRING;
		String strCharacteristicSpecific;
		String sRMCharacteristic;
		StringList slCharSpec = new StringList();
		Map mpTemp;
		Map mpCharMap;
		String strFinalTarget = EMPTY_STRING;
		String strFinalUoM = EMPTY_STRING;
		boolean bMatchAdditionalAttributes = false;		
		boolean bErrorMultipleTargets = false;
		context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getTargetValueForListOfCharSpecs - mlCharDetails ="+mlCharDetails+"\n mlCharMapping ="+mlCharMapping + "\n slUoM ="+slUoM);
		
		for(Object objCharMap : mlCharMapping)
		{
			slCharSpec = new StringList();
			mpCharMap = new HashMap();
			mpCharMap = (Map) objCharMap;
			sRMCharacteristic = (String)mpCharMap.get(pgApolloConstants.STR_CHARACTERISTICS);			
			slCharSpec = pgApolloCommonUtil.getStringListMultiValue(mpCharMap.get(pgApolloConstants.STR_CHARACTERISTICSPECIFICS));
		
			context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getTargetValueForListOfCharSpecs - mpCharMap ="+mpCharMap);

			for(Object objTemp : mlCharDetails)
			{
				mpTemp = new HashMap();
				mpTemp = (Map) objTemp;
				context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getTargetValueForListOfCharSpecs - mpTemp ="+mpTemp);
				strCharacteristic = EMPTY_STRING;
				strTarget = EMPTY_STRING;
				strTargetChar = EMPTY_STRING;
				strLowerTarget = EMPTY_STRING;
				strUpperTarget = EMPTY_STRING;
				strCharUOM = EMPTY_STRING;
				strCharacteristicSpecific = EMPTY_STRING;
				if(mpTemp.containsKey(SELECT_ATTRIBUTE_PG_CHARACTERISTIC))
				{
					strCharacteristic = (String) mpTemp.get(SELECT_ATTRIBUTE_PG_CHARACTERISTIC);
				}
				if(mpTemp.containsKey(SELECT_ATTRIBUTE_CHARACTERISTICSPECIFIC))
				{
					strCharacteristicSpecific = (String) mpTemp.get(SELECT_ATTRIBUTE_CHARACTERISTICSPECIFIC);
				}
				if(UIUtil.isNullOrEmpty(strCharacteristicSpecific))
				{
					strCharacteristicSpecific = EMPTY_STRING;
				}			
				context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getTargetValueForListOfCharSpecs - strCharacteristic ="+strCharacteristic+" \n strCharacteristicSpecific ="+strCharacteristicSpecific);
				strTargetChar = (String) mpTemp.get(SELECT_ATTRIBUTE_PG_TARGET);
				strLowerTarget = (String) mpTemp.get(SELECT_ATTRIBUTE_PG_LOWER_TARGET);
				strUpperTarget = (String) mpTemp.get(SELECT_ATTRIBUTE_PG_UPPER_TARGET);	
				
				if(fetchSpecLimits)
				{
					strLowerSpecLimit = (String) mpTemp.get(SELECT_ATTRIBUTE_PGLOWERSPECIFICATIONLIMIT);
					strUpperSpecLimit = (String) mpTemp.get(SELECT_ATTRIBUTE_PGUPPERSPECIFICATIONLIMIT);
					strLowerTarget = strLowerSpecLimit;
					strUpperTarget = strUpperSpecLimit;
				}
				
				strCharUOM = (String) mpTemp.get(SELECT_ATTRIBUTE_PG_UNIT_OF_MEASURE);
				context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getTargetValueForListOfCharSpecs - strTargetChar ="+strTargetChar+" \n strLowerTarget ="+strLowerTarget+"\n strUpperTarget ="+strUpperTarget+" \n strCharUOM ="+strCharUOM);
				
				bMatchAdditionalAttributes = compareAdditionalAttributes(context, mpTemp, mapAdditionalAttributeMapping);
				
				context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getTargetValueForListOfCharSpecs - strCharacteristic ="+strCharacteristic+" sRMCharacteristic ="+sRMCharacteristic);
				context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getTargetValueForListOfCharSpecs - strCharacteristicSpecific = "+strCharacteristicSpecific+" slCharSpec = "+slCharSpec);

				if(bMatchAdditionalAttributes && strCharacteristic.equalsIgnoreCase(sRMCharacteristic) && pgApolloCommonUtil.containsInListCaseInsensitive(strCharacteristicSpecific, slCharSpec))
				{			
					context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getTargetValueForListOfCharSpecs - Matching Char Found ");
					if(UIUtil.isNotNullAndNotEmpty(strTargetChar))
					{						
						strTargetChar = strTargetChar.trim();
						bdTarget = new BigDecimal(strTargetChar);
						context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getTargetValueForListOfCharSpecs - bdTarget ="+bdTarget);
						strTarget = bdTarget.toPlainString();
						context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getTargetValueForListOfCharSpecs - Actual strTarget ="+strTarget);
					}
					else if(UIUtil.isNotNullAndNotEmpty(strLowerTarget) && UIUtil.isNotNullAndNotEmpty(strUpperTarget))
					{
						strLowerTarget = strLowerTarget.trim();
						strUpperTarget = strUpperTarget.trim();
						bdLowerTarget = new BigDecimal(strLowerTarget);
						bdUpperTarget = new BigDecimal(strUpperTarget);
						strTarget = ((bdLowerTarget.add(bdUpperTarget)).divide(new BigDecimal("2.0"))).toPlainString();
						context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getTargetValueForListOfCharSpecs - Average strTarget ="+strTarget);
					}
					if(UIUtil.isNotNullAndNotEmpty(strTarget))
					{				
						context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getTargetValueForListOfCharSpecs - strFinalTarget ="+strFinalTarget);
						if(UIUtil.isNullOrEmpty(strFinalTarget))
						{
							strFinalTarget = strTarget;
							strFinalUoM = strCharUOM;
						}
						else
						{
							bErrorMultipleTargets = true;
							break;
						}
					}
				}
			}
		}
		context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getTargetValueForListOfCharSpecs - Final Values");
		context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getTargetValueForListOfCharSpecs - bErrorMultipleTargets ="+bErrorMultipleTargets+"\n strFinalTarget ="+strFinalTarget+"\n strUoM ="+slUoM+"\n strFinalUoM ="+strFinalUoM);
		
		if(bErrorMultipleTargets)
		{
			mTargetMap.put(STR_ERROR, ERROR_MULTIPLE_TARGETS_FOUND);
		}
		else if (!slUoM.isEmpty() && UIUtil.isNotNullAndNotEmpty(strFinalTarget) && !pgApolloCommonUtil.containsInListCaseInsensitive(strFinalUoM, slUoM))
		{
			mTargetMap.put(STR_ERROR, ERROR_MISSING_VALID_UOM);
		}
		else
		{
			if(UIUtil.isNotNullAndNotEmpty(strFinalTarget))
			{
				strFinalTarget = new StringBuilder(strFinalTarget).append(CONSTANT_STRING_PIPE).append(strFinalUoM).toString();
			}
			mTargetMap.put(STR_TARGET,strFinalTarget);
		}		
		context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getTargetValueForListOfCharSpecs - mTargetMap ="+mTargetMap);
		context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getTargetValueForListOfCharSpecs <<<");
		return mTargetMap;		
	}
	
	
	/**
	 * Method to get Target Value based on valid configurations
	 * @param context
	 * @param mlValidConfigList
	 * @param mlCharDetails
	 * @param bReturnError
	 * @return
	 * @throws MatrixException
	 */
	private static String getTargetValueBasedOnValidConfigurations(Context context,  MapList mlValidConfigList, MapList mlCharDetails, boolean bReturnError) throws MatrixException 
	{
		Map<String, Object> mapCharConfig;
		Map mapAdditionalAttributeMapping;
		boolean fetchSpecLimits;
		Map charSpecificMap;
		String sConfiguredCharacteristic;
		StringList slUOM;
		StringList slCharSpec;
		String sReturnValue = DomainConstants.EMPTY_STRING;
		MapList mlCharMapping = new MapList();
		
		StringList slErrorList = new StringList();

		context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getTargetValueBasedOnValidConfigurations mlCharDetails ="+mlCharDetails+" mlValidConfigList ="+mlValidConfigList+" bReturnError ="+bReturnError);

		for(Object objMap : mlValidConfigList)
		{
			mapCharConfig = (Map)objMap;
			mlCharMapping = (MapList)mapCharConfig.get(pgApolloConstants.KEY_CHAR_MAPPING);
			slUOM = pgApolloCommonUtil.getStringListMultiValue(mapCharConfig.get(pgApolloConstants.KEY_UOM));
			fetchSpecLimits = (boolean)mapCharConfig.get(pgApolloConstants.STR_SPECLIMITS);
			mapAdditionalAttributeMapping =  (Map)mapCharConfig.get(pgApolloConstants.KEY_ATTRIBUTESMAP);
			
			context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getTargetValueBasedOnValidConfigurations mlCharMapping ="+mlCharMapping +" slUOM ="+slUOM+" fetchSpecLimits ="+fetchSpecLimits+" mapAdditionalAttributeMapping ="+mapAdditionalAttributeMapping);

			charSpecificMap = getTargetValueForListOfCharSpecs(context, mlCharDetails, mlCharMapping, slUOM, fetchSpecLimits, mapAdditionalAttributeMapping);

			if(charSpecificMap.containsKey(pgApolloConstants.STR_ERROR))
			{
				if(bReturnError)
				{
					sReturnValue = (String)charSpecificMap.get(pgApolloConstants.STR_ERROR);
					slErrorList.add(sReturnValue);
				}
				else
				{
					sReturnValue = DomainConstants.EMPTY_STRING;
				}
				context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getTargetValueBasedOnValidConfigurations - sReturnValue ="+sReturnValue);
			}
			else
			{
				sReturnValue = (String)charSpecificMap.get(pgApolloConstants.STR_TARGET);
				context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getTargetValueBasedOnValidConfigurations - sReturnValue Target value ="+sReturnValue);
				if(UIUtil.isNotNullAndNotEmpty(sReturnValue))
				{
					return sReturnValue;
				}
			}								
			
		}
		
		if(!slErrorList.isEmpty())
		{
			sReturnValue = slErrorList.join(pgApolloConstants.CONSTANT_STRING_PIPE);
		}
		
		return sReturnValue;
	}
	
	
	
	/**
	 * Method to get Applicable Config List for Class
	 * @param context 
	 * @param mapOutput
	 * @param sRMPClass
	 * @param sRMCharacteristicMode 
	 * @return
	 * @throws MatrixException 
	 */
	private static MapList getValidConfigListForClass(Context context, Map mapOutput, String sRMPClass, String sConfigKey, Map mapRMP) throws MatrixException
	{
		MapList mlSpecificConfigList = new MapList();

		MapList mlAllConfigList = new MapList();
		Map mapConfig;
		StringList slLocalClassList;
		
		if (STR_DIMENSION_WIDTH.equalsIgnoreCase(sConfigKey)) {
			sConfigKey = KEY_WIDTH;
		} else if (STR_BASIS_WEIGHT.equalsIgnoreCase(sConfigKey)) {
			sConfigKey = KEY_WEIGHT;
		} else if (STR_LINEAR_MASS_DENSITY.equalsIgnoreCase(sConfigKey)) {
			sConfigKey = KEY_LINEARMASSDENSITY;
		}
		
		context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getValidConfigListForClass - sRMPClass ="+sRMPClass+" sConfigKey ="+sConfigKey+" mapRMP ="+mapRMP);

		Object objMapList;
		if(mapOutput.containsKey(sConfigKey))
		{
			objMapList = mapOutput.get(sConfigKey);
			if(objMapList instanceof MapList)
			{
				mlAllConfigList = (MapList)mapOutput.get(sConfigKey);		
			}
		}	
		
		context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getValidConfigListForClass - mlAllConfigList ="+mlAllConfigList);

		
		if(!mlAllConfigList.isEmpty())
		{
			for(Object objectMap : mlAllConfigList)
			{
				mapConfig = (Map)objectMap;
				slLocalClassList = (StringList)mapConfig.get("CLASS");
				
				context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getValidConfigListForClass - sRMPClass ="+sRMPClass+" slLocalClassList ="+slLocalClassList);

				if(pgApolloCommonUtil.containsInListCaseInsensitive(sRMPClass, slLocalClassList))
				{
					mlSpecificConfigList =  (MapList)mapConfig.get("CONFIG");
					
					if(!mlSpecificConfigList.isEmpty())
					{
						mlSpecificConfigList.sort(pgApolloConstants.STR_INDEX, "ascending", "integer");		
					}
					
					context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getValidConfigListForClass - mapRMP ="+mapRMP+" mlSpecificConfigList ="+mlSpecificConfigList);

					mlSpecificConfigList = validateToGetSpecificConfigList(mapRMP, mlSpecificConfigList);
					
					if(!mlSpecificConfigList.isEmpty())
					{
						break;
					}
					
				}
			}
			
		}
		
		context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getValidConfigListForClass - mlSpecificConfigList ="+mlSpecificConfigList);

		return mlSpecificConfigList;
	}

	/**
	 * Method to validate and get Specific Config. List
	 * @param mapRMP
	 * @param mlSpecificConfigList
	 */
	private static MapList validateToGetSpecificConfigList(Map mapRMP, MapList mlSpecificConfigList) 
	{
		MapList mlValidConfigList = new MapList();
		
		if(!mlSpecificConfigList.isEmpty()) 
		{
			StringList slRMPConfiguredAttributes;
			StringList slRMPConfiguredAttributeValueList;
			StringList slRMPActualAttributeValueList;

			Map mapRMPMapping;
			Set setRMPAttributes;
			boolean matchAllRMPMapping = false;
			Map mapObject;
			config: for(Object objectMap : mlSpecificConfigList)
			{		
				mapObject = (Map)objectMap;
				slRMPConfiguredAttributes = new StringList();
				if(mapObject.containsKey("RMP_MAPPING"))
				{
					mapRMPMapping = (Map)mapObject.get("RMP_MAPPING");
					if(!mapRMPMapping.isEmpty())
					{
						setRMPAttributes = mapRMPMapping.keySet();
						slRMPConfiguredAttributes.addAll(setRMPAttributes);
						
						rmpAttribute: for(String sRMPConfiguredSelectable : slRMPConfiguredAttributes)
						{							
							matchAllRMPMapping = false;
							
							slRMPConfiguredAttributeValueList = (StringList)mapRMPMapping.get(sRMPConfiguredSelectable);
							slRMPActualAttributeValueList = pgApolloCommonUtil.getStringListMultiValue(mapRMP.get(sRMPConfiguredSelectable));

							for (String val : slRMPConfiguredAttributeValueList)
							{
								if (pgApolloCommonUtil.containsInListCaseInsensitive(val, slRMPActualAttributeValueList))
								{
									matchAllRMPMapping = true;	
								}									
							}
							if(!matchAllRMPMapping)
							{
								break rmpAttribute;
							}
						}
						if(matchAllRMPMapping)
						{
							mlValidConfigList.add(objectMap);
							break config;
						}
					}
					else
					{
						mlValidConfigList.add(objectMap);
					}					
				}
			}
		}
		
		return mlValidConfigList;
	}

	
	
	/**
	 * Method to compare additional attributes
	 * @param mpTemp
	 * @param mapAdditionalAttributeMapping
	 * @return
	 * @throws MatrixException 
	 */
	private static boolean compareAdditionalAttributes(Context context, Map mpTemp, Map mapAdditionalAttributeMapping) throws MatrixException 
	{
		
		context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : compareAdditionalAttributes >>> mpTemp = "+mpTemp+" mapAdditionalAttributeMapping = "+mapAdditionalAttributeMapping);
		boolean bMatchAttributes = false;
		
		if(null != mapAdditionalAttributeMapping && !mapAdditionalAttributeMapping.isEmpty() && null != mpTemp && !mpTemp.isEmpty())
		{
			Set<String> setAttributeSelectablesToCompare = mapAdditionalAttributeMapping.keySet();
			
			String sCharAttributeValue;
			String sMappedAttributeValue;
			
			if(!setAttributeSelectablesToCompare.isEmpty())
			{
				for(String sAttributeSelectable : setAttributeSelectablesToCompare) 
				{
					sCharAttributeValue = (String)mpTemp.get(sAttributeSelectable);
					
					sMappedAttributeValue = (String)mapAdditionalAttributeMapping.get(sAttributeSelectable);
					
					context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : compareAdditionalAttributes  sCharAttributeValue = "+sCharAttributeValue);
					context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : compareAdditionalAttributes  sMappedAttributeValue = "+sMappedAttributeValue);
					
					if((UIUtil.isNullOrEmpty(sMappedAttributeValue) && UIUtil.isNullOrEmpty(sCharAttributeValue)) || (UIUtil.isNotNullAndNotEmpty(sMappedAttributeValue) && sMappedAttributeValue.equalsIgnoreCase(sCharAttributeValue)))
					{
						bMatchAttributes = true;
					}
					else
					{
						bMatchAttributes = false;
						break;
					}
				}
			}
			else
			{
				bMatchAttributes = true;
			}			
		}
		else
		{
			bMatchAttributes = true;
		}		
		context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : compareAdditionalAttributes <<< bMatchAttributes = "+bMatchAttributes);

		return bMatchAttributes;
	}

	/**
	 * Method to get Class Characteristic Mapping	
	 * @param strCharacteristicMapping
	 * @param strRMPClass
	 * @return
	 * @throws Exception 
	 */
	private static Map<String, Object> getClassCharacteristicMapping(Context context, String strCharacteristicMapping, String strRMPClass) throws Exception {
		
		context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getClassCharacteristicMapping >>>");
		context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getClassCharacteristicMapping - strCharacteristicMapping ="+strCharacteristicMapping+"\n strRMPClass ="+strRMPClass);
		StringList slCharacteristicMapping;
		String strLocalCharacteristicMapping;
		StringList slLocalCharacteristicMapping;
		String strClassMapping;
		String strCharacteristicCharSpecMapping;
		String strCharacteristicUOMMapping;
		StringList slCharSpecList;
		String strAlternateCharCharSpecs;
		StringList slAlternateCharCharSpecs = new StringList();
		Map<String, Object> mapCharConfigMapping = new HashMap();
		StringList slRMPClassMapping;
		String sAdditionalAttributeMapping;
		String sAlternateTargetMapping;
		Map mapAdditionalAttributeMapping;

		if(UIUtil.isNotNullAndNotEmpty(strCharacteristicMapping))
		{
			slCharacteristicMapping = StringUtil.split(strCharacteristicMapping, CONSTANT_STRING_CARET);
			context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getClassCharacteristicMapping - slCharacteristicMapping ="+slCharacteristicMapping);
			if(null!=slCharacteristicMapping && !slCharacteristicMapping.isEmpty())				
			{
				int iCharListSize = slCharacteristicMapping.size();			
				for(int i=0;i<iCharListSize;i++)
				{
					strLocalCharacteristicMapping = slCharacteristicMapping.get(i);
					context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getClassCharacteristicMapping - strLocalCharacteristicMapping ="+strLocalCharacteristicMapping);
					slLocalCharacteristicMapping = StringUtil.split(strLocalCharacteristicMapping, CONSTANT_STRING_PIPE);
					context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getClassCharacteristicMapping - slLocalCharacteristicMapping ="+slLocalCharacteristicMapping);
					if(null!=slLocalCharacteristicMapping && !slLocalCharacteristicMapping.isEmpty() && slLocalCharacteristicMapping.size()>2)
					{
						strClassMapping = slLocalCharacteristicMapping.get(0);
						context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getClassCharacteristicMapping - strClassMapping ="+strClassMapping);
						slRMPClassMapping = StringUtil.split(strClassMapping, CONSTANT_STRING_COMMA);
						context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getClassCharacteristicMapping - slRMPClassMapping ="+slRMPClassMapping);
						if(null!=slRMPClassMapping && !slRMPClassMapping.isEmpty() && pgApolloCommonUtil.containsInListCaseInsensitive(strRMPClass, slRMPClassMapping) )
						{
								strCharacteristicCharSpecMapping = slLocalCharacteristicMapping.get(1);
								context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getClassCharacteristicMapping - strCharacteristicCharSpecMapping ="+strCharacteristicCharSpecMapping);
								mapCharConfigMapping = new HashMap();						
								slCharSpecList = StringUtil.split(strCharacteristicCharSpecMapping, CONSTANT_STRING_COMMA);
								context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getClassCharacteristicMapping - slCharSpecList ="+slCharSpecList);
								if(null!=slCharSpecList && !slCharSpecList.isEmpty())
								{
									mapCharConfigMapping.put(STR_CHARACTERISTICSPECIFICS, slCharSpecList);
								}	
								context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getClassCharacteristicMapping - mapCharConfigMapping ="+mapCharConfigMapping);
								strCharacteristicUOMMapping = slLocalCharacteristicMapping.get(2);		
								context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getClassCharacteristicMapping - strCharacteristicUOMMapping ="+strCharacteristicUOMMapping);
								mapCharConfigMapping.put(KEY_UOM, strCharacteristicUOMMapping);
								context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getClassCharacteristicMapping - mapCharConfigMapping ="+mapCharConfigMapping);
								
								sAdditionalAttributeMapping = slLocalCharacteristicMapping.get(3);	
								
								context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getClassCharacteristicMapping - sAdditionalAttributeMapping ="+sAdditionalAttributeMapping);
								
								mapAdditionalAttributeMapping = getAdditionalAttributeMapping(context, sAdditionalAttributeMapping);
								
								context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getClassCharacteristicMapping - mapAdditionalAttributeMapping ="+mapAdditionalAttributeMapping);

								mapCharConfigMapping.put(KEY_ATTRIBUTESMAP, mapAdditionalAttributeMapping);
								
								sAlternateTargetMapping = slLocalCharacteristicMapping.get(4);	
								
								mapCharConfigMapping.put(STR_TARGET, sAlternateTargetMapping);
								
								context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getClassCharacteristicMapping - sAlternateTargetMapping ="+sAlternateTargetMapping);

								if(slLocalCharacteristicMapping.size()>5)
								{
									context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getClassCharacteristicMapping - slLocalCharacteristicMapping ="+slLocalCharacteristicMapping);
									for(int x=5; x<slLocalCharacteristicMapping.size() ; x++)
									{
										strAlternateCharCharSpecs = slLocalCharacteristicMapping.get(x).trim();
										if(UIUtil.isNotNullAndNotEmpty(strAlternateCharCharSpecs))
										{
											slAlternateCharCharSpecs.addElement(slLocalCharacteristicMapping.get(x));
										}
										context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getClassCharacteristicMapping - slAlternateCharCharSpecs ="+slAlternateCharCharSpecs);
									}
									context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getClassCharacteristicMapping - Final slAlternateCharCharSpecs ="+slAlternateCharCharSpecs);
									if(null != slAlternateCharCharSpecs && !slAlternateCharCharSpecs.isEmpty())
									{
										mapCharConfigMapping.put(STR_ALTERNATE, slAlternateCharCharSpecs);
									}
									context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getClassCharacteristicMapping - Local mapCharConfigMapping ="+mapCharConfigMapping);
								}
						}
					}					
				}

			}				
		}		
		context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getClassCharacteristicMapping - Final mapCharConfigMapping ="+mapCharConfigMapping);
		context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getClassCharacteristicMapping <<<");
		return mapCharConfigMapping;
	}

	
	/**
	 * Method to get Additional attribute mapping
	 * @param context
	 * @param sAdditionalAttributeMapping
	 * @return
	 * @throws Exception 
	 */
	private static Map getAdditionalAttributeMapping(Context context, String sAdditionalAttributeMapping) throws Exception
	{
		context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getAdditionalAttributeMapping >>>");
		Map mapAdditionalAttributeMapping = new HashMap();
		
		if(UIUtil.isNotNullAndNotEmpty(sAdditionalAttributeMapping))
		{
			StringList slAdditionalAttributeMapping = StringUtil.split(sAdditionalAttributeMapping, CONSTANT_STRING_COMMA);
			
			if(null != slAdditionalAttributeMapping && !slAdditionalAttributeMapping.isEmpty())
			{		
				StringList slLocalAdditionalAttribute;
				String sAttributeSelectable;
				String sAttributeValue;
				
				for(String sAdditionalAttribute : slAdditionalAttributeMapping)
				{
					slLocalAdditionalAttribute = StringUtil.splitString(sAdditionalAttribute, CONSTANT_STRING_TILDA + CONSTANT_STRING_TILDA);
					
					if(null != slLocalAdditionalAttribute && !slLocalAdditionalAttribute.isEmpty() && slLocalAdditionalAttribute.size()>1)
					{
						sAttributeSelectable = slLocalAdditionalAttribute.get(0);
						
						sAttributeValue = slLocalAdditionalAttribute.get(1);
						
						if(null == sAttributeValue)
						{
							sAttributeValue = DomainConstants.EMPTY_STRING;
						}
						
						if(UIUtil.isNotNullAndNotEmpty(sAttributeSelectable))
						{
							mapAdditionalAttributeMapping.put(sAttributeSelectable, sAttributeValue);
						}

					}
					
				}
				
			}
			
		}
		context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloWeightConversionUtility : getAdditionalAttributeMapping <<< "+mapAdditionalAttributeMapping);

		return mapAdditionalAttributeMapping;
	}

	/**
	 * This method is used to compute Gross, Net weight and quantity of substitute RMP
	 * @param context
	 * @param strPrimaryMaterialId
	 * @param strSubstituteMaterialId
	 * @param mpEBOMDetails
	 * @return
	 */
	public static Map getConvertedWeightsAndQuantity(Context context, String strPrimaryMaterialId, String strSubstituteMaterialId, Map mpEBOMDetails) throws Exception
	{		
		Map mpEBOMresult = new HashMap<>();		
		boolean isContextPushed = false;
		
		context.printTrace(pgApolloConstants.TRACE_LPD, "strPrimaryMaterialId ="+strPrimaryMaterialId+", strSubstituteMaterialId="+strSubstituteMaterialId+", mpEBOMDetails="+mpEBOMDetails);
		try 
		{			
			String strGrossWeight=(String) mpEBOMDetails.get(KEY_GROSSWEIGHT);
			String strNetWeight=(String) mpEBOMDetails.get(KEY_NETWEIGHT);
			String strQuantity=(String) mpEBOMDetails.get(DomainConstants.ATTRIBUTE_QUANTITY);
			String strArea=(String) mpEBOMDetails.get(KEY_AREA);
			String sWidthRatio =(String) mpEBOMDetails.get(KEY_WIDTHRATIO);
			String strSubGrossWeight = EMPTY_STRING;
			String strSubNetWeight = EMPTY_STRING;
			String strSubQuantity = EMPTY_STRING;
			
			BigDecimal bdZero = new BigDecimal("0.0");
			
			ContextUtil.pushContext(context,pgApolloConstants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
			isContextPushed = true;
			
			StringList slRMSelectable = new StringList();
			slRMSelectable.addElement(SELECT_ATTRIBUTE_PGBASEUOM);
			slRMSelectable.addElement(SELECT_ATTRIBUTE_PG_CLASS);
						
			//Substitute 
			DomainObject doSubstitute = DomainObject.newInstance(context, strSubstituteMaterialId);
			Map mpRMInfo = doSubstitute.getInfo(context, slRMSelectable);
			String strSubstituteBaseUOM = (String) mpRMInfo.get(SELECT_ATTRIBUTE_PGBASEUOM);
			String strSubstituteClass = (String) mpRMInfo.get(SELECT_ATTRIBUTE_PG_CLASS);
			
			//Primary
			DomainObject domprimaryID = DomainObject.newInstance(context,strPrimaryMaterialId);
			mpRMInfo = domprimaryID.getInfo(context, new StringList(SELECT_ATTRIBUTE_PGBASEUOM));
			
			String strPrimaryBaseUOM = (String) mpRMInfo.get(SELECT_ATTRIBUTE_PGBASEUOM);
						
			//Gross Weight and Net Weight Calculations
			String strPrimaryWeightFactor = EMPTY_STRING;
			String strPrimaryWeightFactorUOM = EMPTY_STRING;
			String strSubstituteWeightFactor = EMPTY_STRING;
			String strSubstituteWeightFactorUOM = EMPTY_STRING;
			
			String sPrimaryWidthFactor = EMPTY_STRING;
			String sSubstituteWidthFactor = EMPTY_STRING;
			
			StringList slTemp = new StringList();
			//Changes for Primary Class Starts
			StringList slRMPPrimaryClasses = new StringList();
			StringList slRMPLinearMassDensityPrimaryClasses = new StringList();
			Map mapPageFileOutput = pgApolloCommonUtil.getPagePropertyMapBasedOnJson(context, pgApolloConstants.STR_APOLLO_SUBSTITUTE_CONFIG_PAGE_FILENAME);
			if(null != mapPageFileOutput && !mapPageFileOutput.isEmpty())
			{
				if(mapPageFileOutput.containsKey("NoCalculationsRequiredClasses"))
				{
					slRMPPrimaryClasses = (StringList)mapPageFileOutput.get("NoCalculationsRequiredClasses");
				}
				if(mapPageFileOutput.containsKey("ElasticClasses"))
				{
					slRMPLinearMassDensityPrimaryClasses = (StringList)mapPageFileOutput.get("ElasticClasses");
				}
			}			
			
			context.printTrace(pgApolloConstants.TRACE_LPD, "strSubstituteClass ="+strSubstituteClass+", slRMPPrimaryClasses="+slRMPPrimaryClasses);

			if(UIUtil.isNotNullAndNotEmpty(strSubstituteClass) && null!=slRMPPrimaryClasses && !slRMPPrimaryClasses.isEmpty() && pgApolloCommonUtil.containsInListCaseInsensitive(strSubstituteClass, slRMPPrimaryClasses))
			{
				//Changes for Primary Class Ends
				mpEBOMresult.put(KEY_GROSSWEIGHT, strGrossWeight);
				mpEBOMresult.put(KEY_NETWEIGHT, strNetWeight);
				context.printTrace(pgApolloConstants.TRACE_LPD, "strGrossWeight ="+strGrossWeight+", strNetWeight="+strNetWeight);
				mpEBOMresult = getQuantityForBasisWeightRMPClasses(context, mpEBOMresult, strQuantity, strSubstituteBaseUOM, strPrimaryBaseUOM);
			}
			else
			{
				boolean isElastic=false;
				String strStandardWeightFactorUOM = EMPTY_STRING;
				String sStandardWidthFactorUOM = EMPTY_STRING;
				BigDecimal bdWeightRatio = new BigDecimal("0.0");
				BigDecimal bdWidthRatio = new BigDecimal("0.0");
				BigDecimal bdGrossWt = new BigDecimal("0.0");
				BigDecimal bdNetWt = new BigDecimal("0.0");
				
				context.printTrace(pgApolloConstants.TRACE_LPD, "strSubstituteClass ="+strSubstituteClass+", slRMPLinearMassDensityPrimaryClasses="+slRMPLinearMassDensityPrimaryClasses);

				if(UIUtil.isNotNullAndNotEmpty(strSubstituteClass) && null!=slRMPLinearMassDensityPrimaryClasses && !slRMPLinearMassDensityPrimaryClasses.isEmpty() && pgApolloCommonUtil.containsInListCaseInsensitive(strSubstituteClass, slRMPLinearMassDensityPrimaryClasses))
				{	
					strPrimaryWeightFactor = getTargetValue(context, strPrimaryMaterialId, STR_LINEAR_MASS_DENSITY );
					strSubstituteWeightFactor = getTargetValue(context, strSubstituteMaterialId, STR_LINEAR_MASS_DENSITY );	
					isElastic=true;			
					strStandardWeightFactorUOM = STR_UOM_GRAMS_PER_10_KILOMETER;
					
					context.printTrace(pgApolloConstants.TRACE_LPD, "Elastic True >>>> strPrimaryWeightFactor ="+strPrimaryWeightFactor+", strSubstituteWeightFactor="+strSubstituteWeightFactor);
				}
				else
				{				
					strPrimaryWeightFactor = getTargetValue(context, strPrimaryMaterialId, STR_BASIS_WEIGHT );
					strSubstituteWeightFactor = getTargetValue(context, strSubstituteMaterialId, STR_BASIS_WEIGHT );
					
					context.printTrace(pgApolloConstants.TRACE_LPD, "Elastic False >>>> strPrimaryWeightFactor ="+strPrimaryWeightFactor+", strSubstituteWeightFactor="+strSubstituteWeightFactor);


					strStandardWeightFactorUOM = STR_UOM_GRAMS_PER_SQUARE_METER;
					
					sPrimaryWidthFactor = getTargetValue(context, strPrimaryMaterialId, STR_DIMENSION_WIDTH );
					sSubstituteWidthFactor = getTargetValue(context, strSubstituteMaterialId, STR_DIMENSION_WIDTH );
					
					context.printTrace(pgApolloConstants.TRACE_LPD, "Elastic False >>>> sPrimaryWidthFactor ="+sPrimaryWidthFactor+", sSubstituteWidthFactor="+sSubstituteWidthFactor);

					sStandardWidthFactorUOM = STR_UOM_MILLIMETER_ACTUAL;
				}
				
				String sWeightRatio = getFactorRatio(strPrimaryWeightFactor, strSubstituteWeightFactor, strStandardWeightFactorUOM);
				
				context.printTrace(pgApolloConstants.TRACE_LPD, "sWeightRatio ="+sWeightRatio+", sWidthRatio="+sWidthRatio);

				if(UIUtil.isNullOrEmpty(sWeightRatio) || (!isElastic && UIUtil.isNullOrEmpty(sWidthRatio)))
				{
					//Gross Wt and Net Wt will be returned as Empty
				}
				else
				{	
					bdWeightRatio = new BigDecimal(sWeightRatio);
					bdWidthRatio = new BigDecimal("0.0");
					if(UIUtil.isNotNullAndNotEmpty(sWidthRatio))
					{
						bdWidthRatio = new BigDecimal(sWidthRatio);
					}
					
					if(!bdWeightRatio.equals(bdZero))
					{
						if(UIUtil.isNotNullAndNotEmpty(strGrossWeight))
						{
							if(!isElastic && !bdWidthRatio.equals(bdZero))
							{
								bdGrossWt = new BigDecimal(strGrossWeight);
								bdGrossWt = bdWeightRatio.multiply(bdGrossWt, MathContext.DECIMAL64);
								bdGrossWt = bdWidthRatio.multiply(bdGrossWt, MathContext.DECIMAL64);
								strSubGrossWeight = bdGrossWt.toString();
								context.printTrace(pgApolloConstants.TRACE_LPD, "1  strSubGrossWeight="+strSubGrossWeight);
							}
							else if(isElastic)
							{
								bdGrossWt = new BigDecimal(strGrossWeight);
								bdGrossWt = bdWeightRatio.multiply(bdGrossWt, MathContext.DECIMAL64);
								strSubGrossWeight = bdGrossWt.toString();
								context.printTrace(pgApolloConstants.TRACE_LPD, "2  strSubGrossWeight="+strSubGrossWeight);
							}		
							
						}
						if(UIUtil.isNotNullAndNotEmpty(strNetWeight))
						{
							if(!isElastic && !bdWidthRatio.equals(bdZero))
							{
								bdNetWt = new BigDecimal(strNetWeight);
								bdNetWt = bdWeightRatio.multiply(bdNetWt, MathContext.DECIMAL64);
								bdNetWt = bdWidthRatio.multiply(bdNetWt, MathContext.DECIMAL64);
								strSubNetWeight = bdNetWt.toString();
								context.printTrace(pgApolloConstants.TRACE_LPD, "1  strSubNetWeight="+strSubNetWeight);
							}
							else if(isElastic)
							{
								bdNetWt = new BigDecimal(strNetWeight);
								bdNetWt = bdWeightRatio.multiply(bdNetWt, MathContext.DECIMAL64);
								strSubNetWeight = bdNetWt.toString();
								context.printTrace(pgApolloConstants.TRACE_LPD, "2  strSubNetWeight="+strSubNetWeight);
							}
							
						}				
					}
				}				
				mpEBOMresult.put(KEY_GROSSWEIGHT, strSubGrossWeight);
				mpEBOMresult.put(KEY_NETWEIGHT, strSubNetWeight);
				
				//Calculate Quantity for Substitutes
				//If BaseUoM is empty on either Primary or Substitute, Quantity cannot be calculated
				if(UIUtil.isNotNullAndNotEmpty(strPrimaryBaseUOM) && UIUtil.isNotNullAndNotEmpty(strSubstituteBaseUOM))
				{
					strSubQuantity = getSubstituteQuantityBasedonUOM(context, strQuantity, strSubstituteBaseUOM, strPrimaryBaseUOM, strSubstituteWeightFactor, isElastic, bdWeightRatio, bdWidthRatio, bdGrossWt);
				}
				mpEBOMresult.put(DomainConstants.ATTRIBUTE_QUANTITY, strSubQuantity);				
			}
		}catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			if(isContextPushed)
			{
				ContextUtil.popContext(context);	
			}
		}
		return mpEBOMresult;
	}
	
	
	/**
	 * Method to calculate and set LPD APP substitute attributes
	 * @param context
	 * @param domNewEBOMSubRel
	 * @param sSubstituteId
	 * @param subRelAttributeMap
	 * @param mapNewEBOM
	 * @param sNewEBOMSubstituteWebWidth
	 * @throws Exception
	 */
	public static void calculateAndSetLPDAPPSubstituteAttributes(Context context, DomainRelationship domNewEBOMSubRel, String sSubstituteId, Map subRelAttributeMap, Map mapNewEBOM, String sNewEBOMSubstituteWebWidth) throws Exception  {

		
		context.printTrace(pgApolloConstants.TRACE_LPD,  "calculateAndSetLPDAPPSubstituteAttributes >>> sSubstituteId = "+sSubstituteId+" subRelAttributeMap = "+subRelAttributeMap+" mapNewEBOM = "+mapNewEBOM+" sNewEBOMSubstituteWebWidth = "+sNewEBOMSubstituteWebWidth);

		String sNewEBOMWebWidth = (String)mapNewEBOM.get(pgApolloConstants.ATTRIBUTE_PG_WEBWIDTH);

		String sWidthFactor = pgApolloCommonUtil.divideValues(sNewEBOMSubstituteWebWidth, sNewEBOMWebWidth);

		context.printTrace(pgApolloConstants.TRACE_LPD,  "sWidthFactor = "+sWidthFactor);

		String sGrossWeight = (String)mapNewEBOM.get(pgApolloConstants.ATTRIBUTE_PG_GROSSWEIGHTREAL);
		String sNetWeight = (String)mapNewEBOM.get(pgApolloConstants.ATTRIBUTE_NET_WEIGHT);
		String sQuantity = (String)mapNewEBOM.get(pgApolloConstants.ATTRIBUTE_QUANTITY);
		String sEBOMObjectId = (String)mapNewEBOM.get(DomainConstants.SELECT_ID);


		Map mpEBOMDetails = new HashMap<>();
		mpEBOMDetails.put(pgApolloConstants.KEY_GROSSWEIGHT, sGrossWeight);
		mpEBOMDetails.put(pgApolloConstants.KEY_NETWEIGHT, sNetWeight);
		mpEBOMDetails.put(DomainConstants.ATTRIBUTE_QUANTITY, sQuantity);
		mpEBOMDetails.put(pgApolloConstants.KEY_WIDTHRATIO, sWidthFactor);
		
		String sExistingEBOMSubstittueGrossWeight = (String)subRelAttributeMap.get(pgApolloConstants.ATTRIBUTE_PG_GROSSWEIGHTREAL);
		String sExistingEBOMSubstituteNetWeight = (String)subRelAttributeMap.get(pgApolloConstants.ATTRIBUTE_NET_WEIGHT);
		String sExistingEBOMSubstituteQuantity = (String)subRelAttributeMap.get(pgApolloConstants.ATTRIBUTE_QUANTITY);


		Map mpEBOMResultDetail= getConvertedWeightsAndQuantity (context, sEBOMObjectId,  sSubstituteId, mpEBOMDetails);

		context.printTrace(pgApolloConstants.TRACE_LPD,  "mpEBOMResultDetail = "+mpEBOMResultDetail);

		String sCalculatedGrossWeight = DomainConstants.EMPTY_STRING;

		String sCalculatedNetWeight = DomainConstants.EMPTY_STRING;

		String sCalculateQuantity = DomainConstants.EMPTY_STRING;


		if(mpEBOMResultDetail.containsKey(pgApolloConstants.KEY_GROSSWEIGHT))
		{
			sCalculatedGrossWeight=(String) mpEBOMResultDetail.get(pgApolloConstants.KEY_GROSSWEIGHT);
		}
		if(mpEBOMResultDetail.containsKey(pgApolloConstants.KEY_NETWEIGHT))
		{
			sCalculatedNetWeight=(String) mpEBOMResultDetail.get(pgApolloConstants.KEY_NETWEIGHT);
		}
		if(mpEBOMResultDetail.containsKey(DomainConstants.ATTRIBUTE_QUANTITY))
		{
			sCalculateQuantity=(String) mpEBOMResultDetail.get(DomainConstants.ATTRIBUTE_QUANTITY);
		}

		StringList slEmptyValue = new StringList();
		if(UIUtil.isNullOrEmpty(sCalculatedGrossWeight)){
			slEmptyValue.add(pgApolloConstants.STR_GROSS_WEIGHT);
		}				
		if(UIUtil.isNullOrEmpty(sCalculatedNetWeight)){
			slEmptyValue.add(pgApolloConstants.STR_NET_WEIGHT);
		}				
		if(UIUtil.isNullOrEmpty(sCalculateQuantity)){
			slEmptyValue.add(pgApolloConstants.STR_QUANTITY);
			sCalculateQuantity = "0.0";
		}

		subRelAttributeMap.put(pgApolloConstants.ATTRIBUTE_PG_WEBWIDTH, sNewEBOMSubstituteWebWidth);

		context.printTrace(pgApolloConstants.TRACE_LPD,  "sExistingEBOMSubstituteQuantity = " + sExistingEBOMSubstituteQuantity+"  sCalculateQuantity = "+ sCalculateQuantity );
		subRelAttributeMap = pgApolloCommonUtil.updateAttributeMap(context, subRelAttributeMap, sExistingEBOMSubstituteQuantity, sCalculateQuantity, DomainConstants.ATTRIBUTE_QUANTITY);
		context.printTrace(pgApolloConstants.TRACE_LPD,  "sExistingEBOMSubstittueGrossWeight = " + sExistingEBOMSubstittueGrossWeight+"  sCalculatedGrossWeight = "+ sCalculatedGrossWeight );
		subRelAttributeMap = pgApolloCommonUtil.updateAttributeMap(context, subRelAttributeMap, sExistingEBOMSubstittueGrossWeight, sCalculatedGrossWeight, pgApolloConstants.ATTRIBUTE_PG_GROSSWEIGHTREAL);
		context.printTrace(pgApolloConstants.TRACE_LPD,  "sExistingEBOMSubstituteNetWeight = " + sExistingEBOMSubstituteNetWeight+"  sCalculatedNetWeight = "+ sCalculatedNetWeight );
		subRelAttributeMap = pgApolloCommonUtil.updateAttributeMap(context, subRelAttributeMap, sExistingEBOMSubstituteNetWeight, sCalculatedNetWeight, pgApolloConstants.ATTRIBUTE_NET_WEIGHT);


		context.printTrace(pgApolloConstants.TRACE_LPD,  "New Attribute Values setting on Substitute >>> subRelAttributeMap = " + subRelAttributeMap );

		domNewEBOMSubRel.setAttributeValues(context, subRelAttributeMap);


		if(!slEmptyValue.isEmpty())
		{
			String sSubstituteName = DomainConstants.EMPTY_STRING;
			if(UIUtil.isNotNullAndNotEmpty(sSubstituteId))
			{
				DomainObject domSubstituteObject = DomainObject.newInstance(context, sSubstituteId);
				sSubstituteName = domSubstituteObject.getInfo(context, DomainConstants.SELECT_NAME);
			}
			StringBuffer sbNotice = new StringBuffer();
			sbNotice.append(FrameworkUtil.join(slEmptyValue, ", "));
			sbNotice.append(" ");
			sbNotice.append(EnoviaResourceBundle.getProperty(context, "emxEngineeringCentralStringResource", context.getLocale(),"emxEngineeringCentral.AddEBOMSubstitute.CalculationLimitationError1"));
			sbNotice.append(sSubstituteName);
			sbNotice.append(EnoviaResourceBundle.getProperty(context, "emxEngineeringCentralStringResource", context.getLocale(),"emxEngineeringCentral.AddEBOMSubstitute.CalculationLimitationError2"));
			MqlUtil.mqlCommand(context, "notice $1", sbNotice.toString());

		}
	}

	
	/**
	 * Method to get Width Ratio
	 * @param sPrimaryFactor
	 * @param sSubstituteFactor
	 * @param sStandardFactorUOM
	 * @return
	 */
	private static String getFactorRatio(String sPrimaryFactor, String sSubstituteFactor,	String sStandardFactorUOM)
	{
		
		String sRatio = DomainConstants.EMPTY_STRING;
		BigDecimal bdRatio;
		BigDecimal bdZero = new BigDecimal("0.0");
		
		StringList slTemp;
		String sPrimaryFactorUOM = DomainConstants.EMPTY_STRING;
		String sSubstituteFactorUOM = DomainConstants.EMPTY_STRING;

		if(UIUtil.isNotNullAndNotEmpty(sPrimaryFactor))
		{
			slTemp = StringUtil.split(sPrimaryFactor, CONSTANT_STRING_PIPE);
			sPrimaryFactor = slTemp.get(0);
			if(slTemp.size() > 1)
			{
				sPrimaryFactorUOM = slTemp.get(1);
			}					
			if(!sPrimaryFactorUOM.equalsIgnoreCase(sStandardFactorUOM))
			{
				sPrimaryFactor=EMPTY_STRING;
			}					
		}			
		if(UIUtil.isNotNullAndNotEmpty(sSubstituteFactor))
		{
			slTemp = StringUtil.split(sSubstituteFactor,CONSTANT_STRING_PIPE);
			sSubstituteFactor = slTemp.get(0);
			if(slTemp.size() > 1)
			{
				sSubstituteFactorUOM = slTemp.get(1);
			}
			if(!sSubstituteFactorUOM.equalsIgnoreCase(sStandardFactorUOM))
			{
				sSubstituteFactor=EMPTY_STRING;
			}					
		}
		if(UIUtil.isNotNullAndNotEmpty(sPrimaryFactor) && UIUtil.isNotNullAndNotEmpty(sSubstituteFactor))
		{
			BigDecimal bdPrimary = new BigDecimal(sPrimaryFactor);
			BigDecimal bdSubstitute = new BigDecimal(sSubstituteFactor);
			
			if(!bdPrimary.equals(bdZero) && !bdSubstitute.equals(bdZero))
			{
				bdRatio = bdSubstitute.divide(bdPrimary, MathContext.DECIMAL64);
				if(!bdRatio.equals(bdZero))
				{
					sRatio = bdRatio.toString();
				}
			}			
		}		
		return sRatio;
	}

	/**
	 * Method to process Quantity for Basis Weight RMP Classes
	 * @param context
	 * @param mpEBOMresult
	 * @param strQuantity
	 * @param strSubstituteBaseUOM
	 * @param strPrimaryBaseUOM
	 * @return
	 * @throws Exception
	 */
	private static Map getQuantityForBasisWeightRMPClasses(Context context, Map mpEBOMresult, String strQuantity,	String strSubstituteBaseUOM, String strPrimaryBaseUOM) throws Exception {
		context.printTrace(pgApolloConstants.TRACE_LPD, "getQuantityForBasisWeightRMPClasses >>> mpEBOMresult ="+mpEBOMresult+", strQuantity="+strQuantity+", strSubstituteBaseUOM="+strSubstituteBaseUOM+", strPrimaryBaseUOM="+strPrimaryBaseUOM);
		if(UIUtil.isNotNullAndNotEmpty(strPrimaryBaseUOM) && UIUtil.isNotNullAndNotEmpty(strSubstituteBaseUOM))
		{
			if(strPrimaryBaseUOM.equalsIgnoreCase(strSubstituteBaseUOM))
			{
				context.printTrace(pgApolloConstants.TRACE_LPD, "getQuantityForBasisWeightRMPClasses UOM Matching");
				mpEBOMresult.put(DomainConstants.ATTRIBUTE_QUANTITY, strQuantity);
			}
			else
			{
				strQuantity = convertValues(context , strQuantity ,strPrimaryBaseUOM.toUpperCase() , strSubstituteBaseUOM.toUpperCase());
				context.printTrace(pgApolloConstants.TRACE_LPD, "getQuantityForBasisWeightRMPClasses strQuantity converted ="+strQuantity);
				mpEBOMresult.put(DomainConstants.ATTRIBUTE_QUANTITY, strQuantity);
			}
		}
		return mpEBOMresult;
	}	
	
	/**
	 * Method to get Quantity based on UOM
	 * @param context 
	 * @param strQuantity
	 * @param strSubstituteBaseUOM
	 * @param strPrimaryBaseUOM
	 * @param strSubstituteWeightFactor
	 * @param isElastic
	 * @param bdWeightRatio
	 * @param bdGrossWt
	 * @return
	 * @throws MatrixException 
	 */
	private static String getSubstituteQuantityBasedonUOM(Context context, String strQuantity, String strSubstituteBaseUOM, String strPrimaryBaseUOM, String strSubstituteWeightFactor, boolean isElastic, BigDecimal bdWeightRatio, BigDecimal bdWidthRatio, BigDecimal bdGrossWt) throws MatrixException {
		
		context.printTrace(pgApolloConstants.TRACE_LPD, "getSubstituteQuantityBasedonUOM >> strQuantity ="+strQuantity+", strSubstituteBaseUOM="+strSubstituteBaseUOM+", strPrimaryBaseUOM="+strPrimaryBaseUOM);
		context.printTrace(pgApolloConstants.TRACE_LPD, "getSubstituteQuantityBasedonUOM >> strSubstituteWeightFactor ="+strSubstituteWeightFactor+", isElastic="+isElastic+", bdWeightRatio="+bdWeightRatio+" bdWidthRatio="+bdWidthRatio+", bdGrossWt="+bdGrossWt);

		
		BigDecimal bdPrimaryQuantity = new BigDecimal("0.0");
		String strSubQuantity = EMPTY_STRING;		
		BigDecimal bdZero = new BigDecimal("0.0");
		if(UIUtil.isNotNullAndNotEmpty(strQuantity))
		{
			bdPrimaryQuantity = new BigDecimal(strQuantity);
		}
		//For Substitute UOM Each
		if(STR_UOM_EACH.equalsIgnoreCase(strPrimaryBaseUOM) && STR_UOM_EACH.equalsIgnoreCase(strSubstituteBaseUOM))
		{
			strSubQuantity = strQuantity;
		}								
		//If BaseUoM is same, then Quantity will be same				
		else if((STR_UOM_SQUARE_METER.equalsIgnoreCase(strPrimaryBaseUOM) && STR_UOM_SQUARE_METER.equalsIgnoreCase(strSubstituteBaseUOM)) || (STR_UOM_SQUARE_CENTIMETER.equalsIgnoreCase(strPrimaryBaseUOM) && STR_UOM_SQUARE_CENTIMETER.equalsIgnoreCase(strSubstituteBaseUOM)))
		{			
			if(!bdWidthRatio.equals(bdZero))
			{
				BigDecimal bdQuantity = bdWidthRatio.multiply(bdPrimaryQuantity, MathContext.DECIMAL64);
				if(!bdQuantity.equals(bdZero))
				{
					strSubQuantity = bdQuantity.toString();
				}
			}
		}					
		else if (STR_UOM_SQUARE_METER.equalsIgnoreCase(strPrimaryBaseUOM) && STR_UOM_KILOGRAM.equalsIgnoreCase(strSubstituteBaseUOM))
		{
			strSubQuantity = getQuantityByDividingThousand(bdGrossWt);
		}
		else if (STR_UOM_SQUARE_METER.equalsIgnoreCase(strPrimaryBaseUOM) && STR_UOM_GRAM.equalsIgnoreCase(strSubstituteBaseUOM))
		{
			strSubQuantity = getSubstituteQuantityWithGrossWeight(bdGrossWt);
		}	
		else if (isElastic && STR_UOM_KILOGRAM.equalsIgnoreCase(strPrimaryBaseUOM) && STR_UOM_KILOGRAM.equalsIgnoreCase(strSubstituteBaseUOM))
		{
			strSubQuantity = getQuantityByDividingThousand(bdGrossWt);
		}
		else if (!isElastic && STR_UOM_KILOGRAM.equalsIgnoreCase(strPrimaryBaseUOM) && STR_UOM_KILOGRAM.equalsIgnoreCase(strSubstituteBaseUOM) )
		{
			strSubQuantity = getSubstituteQuantityFromPrimaryQty(bdWeightRatio, bdPrimaryQuantity);
		}
		else if (isElastic && STR_UOM_KILOGRAM.equalsIgnoreCase(strPrimaryBaseUOM) && STR_UOM_GRAM.equalsIgnoreCase(strSubstituteBaseUOM))
		{
			strSubQuantity = getSubstituteQuantityWithGrossWeight(bdGrossWt);
		}
		else if (!isElastic && STR_UOM_KILOGRAM.equalsIgnoreCase(strPrimaryBaseUOM) && STR_UOM_GRAM.equalsIgnoreCase(strSubstituteBaseUOM))
		{
			if(!bdWeightRatio.equals(bdZero))
			{
				BigDecimal bdQuantity = bdWeightRatio.multiply(bdPrimaryQuantity, MathContext.DECIMAL64).multiply(new BigDecimal("1000.0"));
				if(!bdQuantity.equals(bdZero))
				{
					strSubQuantity = bdQuantity.toString();
				}
			}
		}
		else if (STR_UOM_KILOGRAM.equalsIgnoreCase(strPrimaryBaseUOM) && STR_UOM_SQUARE_METER.equalsIgnoreCase(strSubstituteBaseUOM) )
		{
			strSubQuantity = getSubstituteQuantityGrossWeightSubstituteWeightFactor(strSubstituteWeightFactor, bdGrossWt, bdWidthRatio);
		}
		else if (isElastic && STR_UOM_GRAM.equalsIgnoreCase(strPrimaryBaseUOM) && STR_UOM_GRAM.equalsIgnoreCase(strSubstituteBaseUOM))
		{
			strSubQuantity = getSubstituteQuantityWithGrossWeight(bdGrossWt);
		}	
		else if (!isElastic && STR_UOM_GRAM.equalsIgnoreCase(strPrimaryBaseUOM) && STR_UOM_GRAM.equalsIgnoreCase(strSubstituteBaseUOM))
		{
			strSubQuantity = getSubstituteQuantityFromPrimaryQty(bdWeightRatio, bdPrimaryQuantity);
		}	
		else if (isElastic && STR_UOM_GRAM.equalsIgnoreCase(strPrimaryBaseUOM) && STR_UOM_KILOGRAM.equalsIgnoreCase(strSubstituteBaseUOM))
		{
			strSubQuantity = getQuantityByDividingThousand(bdGrossWt);
		}	
		else if (!isElastic && STR_UOM_GRAM.equalsIgnoreCase(strPrimaryBaseUOM) && STR_UOM_KILOGRAM.equalsIgnoreCase(strSubstituteBaseUOM))
		{
			strSubQuantity = getSubstituteQuantityFromPrimaryQty(bdWeightRatio, bdPrimaryQuantity);
		}	
		else if (STR_UOM_GRAM.equalsIgnoreCase(strPrimaryBaseUOM) && STR_UOM_SQUARE_METER.equalsIgnoreCase(strSubstituteBaseUOM))
		{
			strSubQuantity = getSubstituteQuantityGrossWeightSubstituteWeightFactor(strSubstituteWeightFactor, bdGrossWt, bdWidthRatio);
		}	
		else if (STR_UOM_LITER.equalsIgnoreCase(strPrimaryBaseUOM) && STR_UOM_LITER.equalsIgnoreCase(strSubstituteBaseUOM))
		{
			strSubQuantity = strQuantity;
		}
		else if (STR_UOM_LITER.equalsIgnoreCase(strPrimaryBaseUOM) && STR_UOM_CUBIC_METER.equalsIgnoreCase(strSubstituteBaseUOM))
		{
			strSubQuantity = getQuantityByDividingThousand(bdPrimaryQuantity);
		}
		else if (STR_UOM_CUBIC_METER.equalsIgnoreCase(strPrimaryBaseUOM) && STR_UOM_CUBIC_METER.equalsIgnoreCase(strSubstituteBaseUOM))
		{
			strSubQuantity = strQuantity;
		}
		else if (STR_UOM_CUBIC_METER.equalsIgnoreCase(strPrimaryBaseUOM) && STR_UOM_LITER.equalsIgnoreCase(strSubstituteBaseUOM))
		{
			BigDecimal bdQuantity =bdPrimaryQuantity.multiply(new BigDecimal("1000.0"));
			if(!bdQuantity.equals(bdZero))
			{
				strSubQuantity = bdQuantity.toString();
			}
		}
		else if(isElastic && STR_UOM_METER.equalsIgnoreCase(strPrimaryBaseUOM) && STR_UOM_KILOGRAM.equalsIgnoreCase(strSubstituteBaseUOM))
		{
			strSubQuantity = getQuantityByDividingThousand(bdGrossWt);
		}
		return strSubQuantity;
	}
	
	/**
	 * Method to get Substitute Quantity based on Gross Weight and Substitute Weight factor
	 * @param strSubstituteWeightFactor
	 * @param bdGrossWt
	 * @return
	 */
	private static String getSubstituteQuantityGrossWeightSubstituteWeightFactor(String strSubstituteWeightFactor, BigDecimal bdGrossWt, BigDecimal bdWidthRatio) {
		String strSubQuantity = DomainConstants.EMPTY_STRING;
		BigDecimal bdZero = new BigDecimal("0.0");	
		String sSubstituteFactor = DomainConstants.EMPTY_STRING;
		if(UIUtil.isNotNullAndNotEmpty(strSubstituteWeightFactor))
		{
			StringList slTemp = StringUtil.split(strSubstituteWeightFactor,CONSTANT_STRING_PIPE);
			sSubstituteFactor = slTemp.get(0);		
		}
		if(UIUtil.isNotNullAndNotEmpty(sSubstituteFactor) && !bdWidthRatio.equals(bdZero))
		{
			BigDecimal bdSubstituteWeightFactor = new BigDecimal(sSubstituteFactor);
			if(!bdSubstituteWeightFactor.equals(bdZero))
			{
				BigDecimal bdQuantity = bdGrossWt.divide(bdSubstituteWeightFactor, MathContext.DECIMAL64);
				bdQuantity = bdWidthRatio.multiply(bdQuantity, MathContext.DECIMAL64);
				if(!bdQuantity.equals(bdZero))
				{
					strSubQuantity = bdQuantity.toString();
				}
			}
		}		
			
		return strSubQuantity;
	}
	
	/**
	 * Method to get Substitute Quantity based on Primary Quantity
	 * @param bdRatio
	 * @param bdPrimaryQuantity
	 * @return
	 */
	private static String getSubstituteQuantityFromPrimaryQty(BigDecimal bdRatio, BigDecimal bdPrimaryQuantity) {
		String strSubQuantity = DomainConstants.EMPTY_STRING;
		BigDecimal bdZero = new BigDecimal("0.0");
		if(!bdRatio.equals(bdZero))
		{
			BigDecimal bdQuantity = bdRatio.multiply(bdPrimaryQuantity, MathContext.DECIMAL64);
			if(!bdQuantity.equals(bdZero))
			{
				strSubQuantity = bdQuantity.toString();
			}
		}
		return strSubQuantity;
	}
	
	/**
	 * Method to get Substitute Quantity based on Gross Weight
	 * @param bdGrossWt
	 * @return
	 */
	private static String getSubstituteQuantityWithGrossWeight(BigDecimal bdGrossWt) {		
		String strSubQuantity = DomainConstants.EMPTY_STRING;
		BigDecimal bdZero = new BigDecimal("0.0");
		BigDecimal bdQuantity = bdGrossWt;
		if(!bdQuantity.equals(bdZero))
		{
			strSubQuantity = bdQuantity.toString();
		}
		return strSubQuantity;
	}
	
	/**
	 * Method to get Substitute Quantity by dividing input parameter by 1000
	 * @param bdParameter
	 * @return
	 */
	private static String getQuantityByDividingThousand(BigDecimal bdParameter) {
		String strSubQuantity = DomainConstants.EMPTY_STRING;
		BigDecimal bdZero = new BigDecimal("0.0");
		if(!bdParameter.equals(bdZero))
		{
			BigDecimal bdQuantity = bdParameter.divide(new BigDecimal("1000.0"));
			if(!bdQuantity.equals(bdZero))
			{
				strSubQuantity = bdQuantity.toString();
			}
		}
		return strSubQuantity;
	}	
	
	/**
	 * Method to convert Values based on Source and Target UOMs
	 * @param context
	 * @param strValue
	 * @param strSourceUOM
	 * @param strtargetUOM
	 * @return
	 * @throws Exception
	 */
	public static String convertValues (Context context, String strValue, String strSourceUOM, String strtargetUOM) throws Exception
	   {
			String strReturnstring = EMPTY_STRING;
		  	try
		   {			   
			   if(UIUtil.isNotNullAndNotEmpty(strSourceUOM) && UIUtil.isNotNullAndNotEmpty(strtargetUOM) && UIUtil.isNotNullAndNotEmpty(strValue))
			   {  
				   Map uomMap = loadUOMs(context,strSourceUOM, strtargetUOM);
				   String strSourceFactor =(String)uomMap.get("sourceFactor");
				   String strTargetFactor =(String)uomMap.get("targetFactor");
				   if(UIUtil.isNotNullAndNotEmpty(strSourceFactor)&& UIUtil.isNotNullAndNotEmpty(strTargetFactor))
				   {					   
					   BigDecimal bstrValue = new BigDecimal(strValue);
					   BigDecimal bSourceFactor = new BigDecimal(strSourceFactor);
					   BigDecimal bTargetFactor = new BigDecimal(strTargetFactor);				   
					   BigDecimal bdConvertedValue = new BigDecimal("0.0");				   
					   bdConvertedValue=bstrValue.multiply(bSourceFactor, MathContext.DECIMAL128);	
					   if(bTargetFactor.compareTo(new BigDecimal("0.0"))!=0)
					   {
						   bdConvertedValue=bdConvertedValue.divide(bTargetFactor, MathContext.DECIMAL128);
					   }
					   else
					   {
						   bdConvertedValue=new BigDecimal("0.0");
					   }				   
					   strReturnstring =bdConvertedValue.toString();
				   }			   
			   }
		   }
		   catch(Exception e) {
			   e.printStackTrace();
			   throw e;
		   }		   
		   return strReturnstring;
		   
	   }
	
	
	 private static Map loadUOMs(Context context , String strSourceUOM , String strTargetUOM) throws Exception
	   {		  
		   Map returnMap =new HashMap();
		   try 
		   {
			   strSourceUOM = strSourceUOM.toUpperCase();
			   strTargetUOM = strTargetUOM.toUpperCase();
			   
			   Map propertyMap = pgApolloCommonUtil.geValueMapfromProperty(context , STR_APOLLO_CONFIG_PAGE_FILENAME , "pgConvertUtitily.UOM.");
			   Set keySet =propertyMap.keySet();
			   Iterator keySetItr = keySet.iterator();
			   String strPropertyKey = null;
			   String strPrpertyValue = null;
			   StringList UOMList =null;
			   StringBuffer strFactorKeyBuffer = new StringBuffer();
			   String strFactorKey= null;
			   while(keySetItr.hasNext()) {
				   strPropertyKey =(String)keySetItr.next();
				   strPrpertyValue = (String)propertyMap.get(strPropertyKey);
				   UOMList =FrameworkUtil.split(strPrpertyValue, pgApolloConstants.CONSTANT_STRING_PIPE);
				   if(UOMList.contains(strSourceUOM) && UOMList.contains(strTargetUOM))
				   {
					   strFactorKeyBuffer.append(strPropertyKey);
					   strFactorKeyBuffer.append(".Factor");
					   strFactorKey=strFactorKeyBuffer.toString();
					   StringList factorlist =FrameworkUtil.split((String)propertyMap.get(strFactorKey), pgApolloConstants.CONSTANT_STRING_PIPE);
					   returnMap.put("sourceFactor", factorlist.get(UOMList.indexOf(strSourceUOM)));
					   returnMap.put("targetFactor", factorlist.get(UOMList.indexOf(strTargetUOM)));
					   break;
				   }
			   }
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		   return returnMap;
	   }


}




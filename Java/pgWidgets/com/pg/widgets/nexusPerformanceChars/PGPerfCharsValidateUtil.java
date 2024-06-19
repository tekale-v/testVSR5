package com.pg.widgets.nexusPerformanceChars;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONObject;
import javax.json.JsonObject;

import com.dassault_systemes.enovia.characteristic.interfaces.ENOCharacteristicEnum;
import com.dassault_systemes.enovia.characteristic.interfaces.ENOICharacteristic;
import com.dassault_systemes.enovia.enterprisechangemgt.util.ChangeUtil;
import com.matrixone.apps.configuration.ConfigurationConstants;
import com.matrixone.apps.configuration.ConfigurationFeature;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.i18nNow;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.widgets.structuredats.PGStructuredATSWhereUsedUtil;
import com.pg.widgets.util.PGWidgetConstants;
import com.pg.widgets.util.PGWidgetUtil;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.StringList;

public class PGPerfCharsValidateUtil {
	private static final org.apache.log4j.Logger LOG4J = org.apache.log4j.Logger.getLogger("emxCPNCharacteristicList");
	private static final Logger logger = Logger.getLogger(PGPerfCharsValidateUtil.class.getName());
	static final String STRING_STATUS = "status";
	static Map<String,String> mpNexusValidationMsgMap = new HashMap<>();
	PGStructuredATSWhereUsedUtil objSATSWhereUsedUtil = new PGStructuredATSWhereUsedUtil();
	// validateCharacteristics
	public String validateCharacteristics(Context context, String strJsonInput) throws Exception {
		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strJsonInput);
		String sObjectId = jsonInputData.getString("objectId");
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		JsonArrayBuilder jsonArrObjInfo = Json.createArrayBuilder();
		HashMap argsMap = new HashMap();
		argsMap.put("objectId", sObjectId);
		String strArgs[] = JPO.packArgs(argsMap);
		Map mCharInfo;
		String strResult = "";
		String strComments = "";
		String strDesc = "";
		try {
			if (UIUtil.isNotNullAndNotEmpty(sObjectId)) {
				updateNexusValidationMsgMap();
				MapList mlList = getValidationResults(context, strArgs);
				Iterator itr = mlList.iterator();
				StringBuffer sb = new StringBuffer();
				// com.matrixone.json.JSONArray jsonArr= new com.matrixone.json.JSONArray();
				while (itr.hasNext()) {
					JSONObject json = new JSONObject();
					mCharInfo = (Map) itr.next();
					strResult = (String) mCharInfo.get("result");
					strComments = (String) mCharInfo.get("Comments");
					strDesc = (String) mCharInfo.get("description");

					Map<String, String> mpParentInfoMap = new HashMap();
					mpParentInfoMap.put("status", strDesc);
					mpParentInfoMap.put("Result", strResult);
					mpParentInfoMap.put("Comments", strComments);

					jsonReturnObj = getJsonObjectFromMap(mpParentInfoMap);
					jsonArrObjInfo.add(jsonReturnObj);
				}

				jsonReturnObj.add(PGPerfCharsConstants.KEY_STATUS, jsonArrObjInfo.toString());
			}
		} catch (Exception excep) {
			logger.log(Level.SEVERE, PGPerfCharsConstants.EXCEPTION_MESSAGE_PERF_CHAR_COPYFROM_PROD_DATA, excep);
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
			return jsonReturnObj.build().toString();
		}
		
		return jsonArrObjInfo.build().toString();
	}

	/**
	 * @param context
	 * @param args
	 * @return MapList
	 * @throws Exception
	 */

	@com.matrixone.apps.framework.ui.ProgramCallable
	public MapList getValidationResults(Context context, String[] args) throws Exception {
		MapList mapReturn = new MapList();
		HashMap paraMap = (HashMap) JPO.unpackArgs(args);
		String strObjectId = paraMap.get("objectId").toString();
		Map programMap = new HashMap();
		programMap.put("objectId", strObjectId);
		String strResults = validateCharacteristic(context, JPO.packArgs(programMap));
		strResults = strResults.replace("\n", "::");
		StringList slResults = FrameworkUtil.split(strResults, "|");
		String strValue3 = DomainConstants.EMPTY_STRING;
		String strValue2 = DomainConstants.EMPTY_STRING;
		for (int i = 0; i < slResults.size(); i++) {
			StringList slValues = FrameworkUtil.split((String) slResults.get(i), "~");
			Map map = new HashMap();
			map.put("id", slValues.get(0));
			strValue2 = slValues.get(2).toString();
			strValue3 = slValues.get(3).toString();
			strValue3 = strValue3.replace("Seq : ", "");
			map.put("result", slValues.get(1));
			StringBuilder sbComments = new StringBuilder();
			if (strValue2.indexOf("::") != -1) {
				strValue2 = strValue2.replace("::", ", ");
				sbComments.append(strValue2);
				sbComments.deleteCharAt(sbComments.lastIndexOf(","));
				strValue2 = sbComments.toString();
			}
			strValue2 = strValue2.replace(", ", "\n");
			map.put("Comments", strValue2);
			if (slValues.get(3).toString().indexOf("::") != -1) {
				strValue3 = strValue3.replace("::", "");
			}
			map.put("description", strValue3);
			mapReturn.add(map);
		}
		mapReturn.addSortKey("description", "ascending", "string");
		mapReturn.sort();

		return mapReturn;
	}

	/**
	 * DSO15X.1 : This method is called on the Validate command on Performance
	 * Characteristic view
	 * 
	 * @param context
	 * @param args
	 * @throws Exception
	 */

	public String validateCharacteristic(Context context, String[] args) throws Exception {
		StringBuilder sbValidationFailureMsg = new StringBuilder();
		String strSessionLanguage = context.getLocale().getLanguage();
		Map mpTargetLimitMap = new HashMap();
		mpTargetLimitMap.put(0,
				i18nNow.getI18nString("emxFramework.Attribute." + PGPerfCharsConstants.ATTR_PG_LOWERSPECIFICATIONLIMIT,
						"emxFrameworkStringResource", strSessionLanguage));
		mpTargetLimitMap.put(1,
				i18nNow.getI18nString("emxFramework.Attribute." + PGPerfCharsConstants.ATTR_PG_LOWERROUTINERELEASELIMIT,
						"emxFrameworkStringResource", strSessionLanguage));
		mpTargetLimitMap.put(2,
				i18nNow.getI18nString("emxFramework.Attribute." + PGPerfCharsConstants.ATTR_PG_LOWERTARGET,
						"emxFrameworkStringResource", strSessionLanguage));
		mpTargetLimitMap.put(3, i18nNow.getI18nString("emxFramework.Attribute." + PGPerfCharsConstants.ATTR_PG_TARGET,
				"emxFrameworkStringResource", strSessionLanguage));
		mpTargetLimitMap.put(4,
				i18nNow.getI18nString("emxFramework.Attribute." + PGPerfCharsConstants.ATTR_PG_UPPERTARGET,
						"emxFrameworkStringResource", strSessionLanguage));
		mpTargetLimitMap.put(5,
				i18nNow.getI18nString("emxFramework.Attribute." + PGPerfCharsConstants.ATTR_PG_UPPERROUTINERELEASELIMIT,
						"emxFrameworkStringResource", strSessionLanguage));
		mpTargetLimitMap.put(6,
				i18nNow.getI18nString("emxFramework.Attribute." + PGPerfCharsConstants.ATTR_PG_UPPERSPECIFICATIONLIMIT,
						"emxFrameworkStringResource", strSessionLanguage));
		String[] strLimitTargetArray = new String[7];
		int iArrayLength = strLimitTargetArray.length;
		int iReducedArrayLength = iArrayLength - 1;

		String[] strResult = new String[2];
		StringList slSequenceNumber = new StringList();
		try {
			Map mpCharMap = new HashMap();
			String strObjectId = getParam(args, "objectId");
			MapList mlCharInfo = getConnectedCharacteristics(context, strObjectId, false);
			MapList mlProductCharInfo = getProductConnectedCharacteristics(context, strObjectId);
			if (!mlProductCharInfo.isEmpty()) {
				mlCharInfo.addAll(mlProductCharInfo);
			}
			if (mlCharInfo != null && !mlCharInfo.isEmpty()) {
				for (Object objCharInfo : mlCharInfo) {
					mpCharMap = (Map) objCharInfo;
					slSequenceNumber.addElement(mpCharMap
							.get(PGPerfCharsConstants.SELECT_ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE).toString());
					strResult = performCharValidations(context, mpTargetLimitMap, (Map) objCharInfo,
							strLimitTargetArray, iArrayLength, iReducedArrayLength, "emxCPNStringResource",
							strSessionLanguage, false);
					if (strResult != null && UIUtil.isNotNullAndNotEmpty(strResult[0])) {
						if (sbValidationFailureMsg.indexOf(strResult[0]) == -1
								&& UIUtil.isNotNullAndNotEmpty(strResult[1])) {
							sbValidationFailureMsg.append(strObjectId).append("~").append("Fail").append("~")
									.append(strResult[1]).append("~");
							if (UIUtil.isNotNullAndNotEmpty(strResult[0])) {
								sbValidationFailureMsg.append(strResult[0]).append("|");
							}
						}
					}
				}

				for (Object objSeqNo : slSequenceNumber) {
					if (sbValidationFailureMsg.indexOf("Seq : " + objSeqNo.toString()) == -1) {
						sbValidationFailureMsg.append(strObjectId).append("~").append("Pass").append("~")
								.append(i18nNow.getI18nString("emxCPN.Alert.PerformanceCharValidationSuccess",
										"emxCPNStringResource", context.getLocale().getLanguage()))
								.append("~Seq : ").append(objSeqNo).append("|");
					}
				}
				if (sbValidationFailureMsg.indexOf("|") != -1) {
					sbValidationFailureMsg.deleteCharAt(sbValidationFailureMsg.lastIndexOf("|"));
				}
			}

		} catch (Exception fme) {
			
			throw fme;
		}
		return sbValidationFailureMsg.toString();
	}

	/**
	 * 
	 * This method returns parameter value from the packed arguments
	 * 
	 * @param args
	 * @param paramName
	 * @return String
	 * @throws Exception
	 */

	public String getParam(String[] args, String paramName) throws Exception {
		Map paramMap = (Map) JPO.unpackArgs(args);
		String paramValue = "";
		if (paramMap.get(paramName) != null) {
			Object value = paramMap.get(paramName);
			if (value instanceof String[]) {
				paramValue = ((String[]) paramMap.get(paramName))[0];
			} else if (value instanceof String) {
				paramValue = (String) paramMap.get(paramName);
			}
		} else {
			Map requestMap = (Map) paramMap.get("requestMap");
			if (requestMap != null)
				paramValue = (String) requestMap.get(paramName);
		}
		return paramValue;
	}

	/**
	 * Method to retrieve connect Characteristic objects
	 * 
	 * @param context
	 * @param strObjectId
	 * @return
	 * @throws Exception
	 */
	public MapList getConnectedCharacteristics(Context context, String strObjectId, boolean isLPD) throws Exception {
		MapList mlCharInfo = new MapList();
		try {
			if (isOfDSOOrigin(context, strObjectId)) {
				DomainObject doProductDataPart = DomainObject.newInstance(context, strObjectId);
				if ((doProductDataPart.isKindOf(context, PGPerfCharsConstants.TYPE_PRODUCTDATAPART))
						|| (doProductDataPart.isKindOf(context, PGPerfCharsConstants.TYPE_RAWMATERIAL))) {
					StringList slSelectables = new StringList(8);
					slSelectables.addElement("attribute[" + PGPerfCharsConstants.ATTR_PG_TMLOGIC + "]");
					slSelectables.addElement("attribute[" + PGPerfCharsConstants.ATTR_ACTION_REQUIRED + "]");
					slSelectables.addElement("attribute[" + PGPerfCharsConstants.ATTR_PG_REPORTTYPE + "]");
					slSelectables.addElement("attribute[" + PGPerfCharsConstants.ATTR_PG_CHARACTERISTIC + "]");
					if (isLPD) {
						slSelectables.addElement(DomainConstants.SELECT_ID);
						slSelectables.addElement(PGPerfCharsConstants.SELECT_ATTRIBUTE_PG_CATEGORYSPECIFICS);
						slSelectables.addElement(PGPerfCharsConstants.SELECT_ATTRIBUTE_CHARACTERISTICSPECIFIC);
						
					} else {
						slSelectables
								.addElement("attribute[" + PGPerfCharsConstants.ATTR_PG_LOWERSPECIFICATIONLIMIT + "]");
						slSelectables
								.addElement("attribute[" + PGPerfCharsConstants.ATTR_PG_UPPERSPECIFICATIONLIMIT + "]");
						slSelectables
								.addElement("attribute[" + PGPerfCharsConstants.ATTR_PG_LOWERROUTINERELEASELIMIT + "]");
						slSelectables
								.addElement("attribute[" + PGPerfCharsConstants.ATTR_PG_UPPERROUTINERELEASELIMIT + "]");
						
						// DSM Req Id: 49078 Performance Characteristic Validation On Validate Characteristic -START
						slSelectables.add(DomainConstants.SELECT_ID);
						slSelectables.add("attribute[" + PGPerfCharsConstants.ATTRIBUTE_PG_TEST_GROUP + "]");
						slSelectables.add("attribute[" + PGPerfCharsConstants.ATTRIBUTE_PG_SAMPLING + "]");
						slSelectables.add("attribute[" + PGPerfCharsConstants.ATTR_PG_REPORTTYPE + "]");
						slSelectables.add("attribute[" + PGPerfCharsConstants.ATTR_PG_REPORT_NEAREST + "]");
						slSelectables.add("attribute[" + PGPerfCharsConstants.ATTR_PG_RELEASECRITERIA + "]");
						slSelectables.add("attribute[" + PGPerfCharsConstants.ATTRIBUTE_PG_APPLICATION + "]");

						slSelectables.add("attribute[" + PGPerfCharsConstants.ATTR_PG_LOWERROUTINERELEASELIMIT + "]");
						slSelectables.add("attribute[" + PGPerfCharsConstants.ATTR_PG_LOWERSPECIFICATIONLIMIT + "]");
						slSelectables.add("attribute[" + PGPerfCharsConstants.ATTR_PG_LOWERTARGET + "]");
						slSelectables.add("attribute[" + PGPerfCharsConstants.ATTR_PG_TARGET + "]");
						slSelectables.add("attribute[" + PGPerfCharsConstants.ATTR_PG_UPPERTARGET + "]");
						slSelectables.add("attribute[" + PGPerfCharsConstants.ATTR_PG_UPPERROUTINERELEASELIMIT + "]");
						slSelectables.add("attribute[" + PGPerfCharsConstants.ATTR_PG_UPPERSPECIFICATIONLIMIT + "]");

						slSelectables.add("attribute[" + PGPerfCharsConstants.ATTR_PG_TMLOGIC + "]");
						slSelectables.add("attribute[" + PGPerfCharsConstants.ATTRIBUTE_PG_PLANT_TESTING + "]");
						slSelectables.add(PGPerfCharsConstants.SELECTABLE_PERCHARS_TO_TESTMETHOD_AUTH_APPLICATION);

						slSelectables.add("attribute[" + PGPerfCharsConstants.ATTR_PG_NEXUS_PC_PARAMETER_ID + "]");
						slSelectables.add("attribute[" + PGPerfCharsConstants.ATTR_PG_CHARACTERISTIC + "]");
						slSelectables.add("attribute[" + PGPerfCharsConstants.ATTR_PG_CHARACTERISTIC_SPECIFICS + "]");

						slSelectables.add("attribute[" + PGPerfCharsConstants.ATTR_PG_RELEASECRITERIA + "]");
						slSelectables.add("attribute[" + PGPerfCharsConstants.ATTR_PG_NEXUS_PC_PARAMETER_ID + "]");
						// DSM Req Id: 49078 Performance Characteristic Validation On Validate Characteristic -END
						
					} // End of If Else Loop
					slSelectables.addElement("attribute[" + PGPerfCharsConstants.ATTR_PG_TARGET + "]");
					slSelectables.addElement("attribute[" + PGPerfCharsConstants.ATTR_PG_LOWERTARGET + "]");
					slSelectables.addElement("attribute[" + PGPerfCharsConstants.ATTR_PG_UPPERTARGET + "]");

					if (isLPD) {
						slSelectables.addElement(
								"from[" + ENOCharacteristicEnum.CharacteristicRelationships.CHARACTERISTIC_TEST_METHOD
										.getRelationship(context) + "].to.type");
					} else {
						slSelectables
								.addElement("to[" + DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT + "].from.type");
					}

					StringList slRelSelect = new StringList(1);
					slRelSelect.addElement(PGPerfCharsConstants.SELECT_ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE);

					if (isLPD) {
						StringList relSelects = new StringList(1);
						relSelects.add(DomainRelationship.SELECT_ID);
						mlCharInfo = doProductDataPart.getRelatedObjects(context,
								PGPerfCharsConstants.RELATIONSHIP_PARAMETER_AGGREGATION,
								PGPerfCharsConstants.TYPE_PLM_PARAMETER, slSelectables, // Object Select
								relSelects, // rel Select
								false, // get To
								true, // get From
								(short) 1, // recurse level
								DomainConstants.EMPTY_STRING, // where Clause
								DomainConstants.EMPTY_STRING, 0);
						if (null != mlCharInfo && !mlCharInfo.isEmpty()) {
							mlCharInfo.sortStructure(DomainConstants.SELECT_ID, PGPerfCharsConstants.STRING_ASCENDING,
									PGPerfCharsConstants.STR_STRING);
							StringList slCharIdList = new ChangeUtil().getStringListFromMapList(mlCharInfo,
									DomainConstants.SELECT_ID);// To get char id list from maplist
							StringList slENOICharSelects = new StringList(10);
							slENOICharSelects.addElement(DomainConstants.SELECT_ID);
							slENOICharSelects.addElement(DomainConstants.SELECT_TYPE);
							slENOICharSelects.addElement(DomainConstants.SELECT_NAME);
							slENOICharSelects.addElement(DomainConstants.SELECT_REVISION);
							slENOICharSelects.addElement(PGPerfCharsConstants.SELECT_ATTRIBUTE_TITLE);
							slENOICharSelects
									.addElement(PGPerfCharsConstants.SELECT_ATTRIBUTE_PG_CHARACTERISTIC_CATEGORY);
							slENOICharSelects.addElement(
									PGPerfCharsConstants.SELECT_ATTRIBUTE_CHARACTERISTICSLOWERSPECIFICATIONLIMIT);
							slENOICharSelects.addElement(
									PGPerfCharsConstants.SELECT_ATTRIBUTE_CHARACTERISTICSUPPERSPECIFICATIONLIMIT);
							slENOICharSelects.addElement(
									PGPerfCharsConstants.SELECT_ATTRIBUTE_CHARACTERISTICSLOWERROUTINERELEASELIMIT);
							slENOICharSelects.addElement(
									PGPerfCharsConstants.SELECT_ATTRIBUTE_CHARACTERISTICSUPPERROUTINERELEASELIMIT);
							List<ENOICharacteristic> listChar = com.dassault_systemes.enovia.characteristic.impl.CharacteristicServices
									.getCharacteristicDetails(context, slCharIdList, slENOICharSelects);
							Map<String, String> mapCharInfo = null;
							ENOICharacteristic enoChar = null;
							for (int iCount = 0, iSize = mlCharInfo.size(); iCount < iSize; iCount++) {
								mapCharInfo = (Map) mlCharInfo.get(iCount);
								enoChar = listChar.get(iCount);
								mapCharInfo.put(PGPerfCharsConstants.SELECT_ATTRIBUTE_PG_TARGET,
										enoChar.getNominalValue(context));
								mapCharInfo.put(PGPerfCharsConstants.SELECT_ATTRIBUTE_PG_UPPER_TARGET,
										enoChar.getMaximalValue(context));
								mapCharInfo.put(PGPerfCharsConstants.SELECT_ATTRIBUTE_PG_LOWER_TARGET,
										enoChar.getMinimalValue(context));
								mapCharInfo.put(
										PGPerfCharsConstants.SELECT_ATTRIBUTE_CHARACTERISTICSLOWERSPECIFICATIONLIMIT,
										enoChar.getLowerSpecificationLimit(context));
								mapCharInfo.put(
										PGPerfCharsConstants.SELECT_ATTRIBUTE_CHARACTERISTICSUPPERSPECIFICATIONLIMIT,
										enoChar.getUpperSpecificationLimit(context));
								mapCharInfo.put(
										PGPerfCharsConstants.SELECT_ATTRIBUTE_CHARACTERISTICSLOWERROUTINERELEASELIMIT,
										enoChar.getLowerRoutineReleaseLimit(context));
								mapCharInfo.put(
										PGPerfCharsConstants.SELECT_ATTRIBUTE_CHARACTERISTICSUPPERROUTINERELEASELIMIT,
										enoChar.getUpperRoutineReleaseLimit(context));
								mapCharInfo.put(PGPerfCharsConstants.SELECT_ATTRIBUTE_TITLE, enoChar.getTitle(context));
								mapCharInfo.put(PGPerfCharsConstants.SELECT_ATTRIBUTE_PG_CHARACTERISTIC_CATEGORY,
										enoChar.getCharacteristicCategory());
							}
						}
						context.printTrace(PGPerfCharsConstants.TRACE_LPD,
								"pgDSOCPNProductData : getConnectedCharacteristics - mlCharInfo = " + mlCharInfo);
					} else {
						mlCharInfo = doProductDataPart.getRelatedObjects(context,
								PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC,
								PGPerfCharsConstants.TYPE_PG_PERFORMANCE_CHARACTERSTIC, slSelectables, slRelSelect,
								false, true, (short) 1, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING, 0);
					}
				}
				
				if(mlCharInfo !=null && !mlCharInfo.isEmpty()) {
					mlCharInfo = updateWizardRelatedAttributes(context, mlCharInfo, doProductDataPart);
				}
			}
			
		} catch (Exception ex) {
			throw ex;
		}
		return mlCharInfo;
	}

	/**
	 * Method to add 'pgNexusStructuredPerfCharsRequired' and 'pgStructuredReleaseCriteriaRequired' attributes to Char List
	 * @param context
	 * @param mlCharInfo
	 * @param doProductDataPart
	 * @return
	 * @throws FrameworkException 
	 */
	private MapList updateWizardRelatedAttributes(Context context, MapList mlCharInfo, DomainObject doProductDataPart) throws FrameworkException {
		MapList mlPCCharInfoList = new MapList();
		StringList slObjSelects = new StringList();
		slObjSelects.add(PGPerfCharsConstants.SELECT_ATTRIBUTE_NEXUS_STRUCTURED_PERFCHARSREQUIRED);
		slObjSelects.add(PGPerfCharsConstants.SELECT_ATTRIBUTE_STRUCTUREDRELEASECRITERIAREQUIRED);
		
		Map<?,?> mpAttrInfoMap = doProductDataPart.getInfo(context, slObjSelects);
		String strAttrNexusPCRequired = (String) mpAttrInfoMap.get(PGPerfCharsConstants.SELECT_ATTRIBUTE_NEXUS_STRUCTURED_PERFCHARSREQUIRED);
		String strAttrReleaseCriteriaRequired = (String) mpAttrInfoMap.get(PGPerfCharsConstants.SELECT_ATTRIBUTE_STRUCTUREDRELEASECRITERIAREQUIRED);
		
		int iListSize = mlCharInfo.size();
		for(int i=0;i<iListSize;i++) {
			Map<Object,Object> mpObjInfoMap = (Map<Object, Object>) mlCharInfo.get(i);
			mpObjInfoMap.put(PGPerfCharsConstants.SELECT_ATTRIBUTE_NEXUS_STRUCTURED_PERFCHARSREQUIRED, strAttrNexusPCRequired);
			mpObjInfoMap.put(PGPerfCharsConstants.SELECT_ATTRIBUTE_STRUCTUREDRELEASECRITERIAREQUIRED, strAttrReleaseCriteriaRequired);
			mlPCCharInfoList.add(mpObjInfoMap);
		}
		
		return mlPCCharInfoList;
	}

	/**
	 * This method checks for the pgOriginatingSource attribute for the object
	 * 
	 * @param context
	 * @param strObjectId
	 * @return boolean
	 * @throws FrameworkException
	 */
	public boolean isOfDSOOrigin(Context context, String strObjectId) throws FrameworkException {
		boolean isDSO = false;
		try {
			if (UIUtil.isNotNullAndNotEmpty(strObjectId)) {
				DomainObject doObject = DomainObject.newInstance(context, strObjectId);
				String strAttrValue = doObject.getInfo(context,
						"attribute[" + PGPerfCharsConstants.ATTR_PG_ORIGINATINGSOURCE + "]");
				if (PGPerfCharsConstants.ORIGINATING_SOURCE_DSO.equalsIgnoreCase(strAttrValue)) {
					isDSO = true;
				}
			}
		} catch (FrameworkException fme) {
			throw fme;
		}
		return isDSO;
	}

	/**
	 * This method return the list of connected Performance Characteristics to a
	 * Product and its Configuration Option
	 * 
	 * @param context     ENOVIA context object
	 * @param strObjectId product object ID
	 * @return MapList containing characteristic information
	 * @throws Exception
	 */
	public MapList getProductConnectedCharacteristics(Context context, String strObjectId) throws Exception {
		MapList mlCharInfo = new MapList();
		try {
			if (UIUtil.isNotNullAndNotEmpty(strObjectId)) {
				DomainObject domProdObj = DomainObject.newInstance(context, strObjectId);
				String strOriginatingSource = domProdObj.getInfo(context,
						"attribute[" + PGPerfCharsConstants.ATTR_PG_ORIGINATINGSOURCE + "]");
				if (PGPerfCharsConstants.ORIGINATING_SOURCE_DSO.equalsIgnoreCase(strOriginatingSource)) {
					if (domProdObj.isKindOf(context, PGPerfCharsConstants.TYPE_CPG_PRODUCT)) {
						StringList slSelectables = new StringList(13);
						slSelectables.addElement(DomainConstants.SELECT_ID);
						slSelectables.addElement("attribute[" + PGPerfCharsConstants.ATTR_PG_TMLOGIC + "]");
						slSelectables.addElement("attribute[" + PGPerfCharsConstants.ATTR_ACTION_REQUIRED + "]");
						slSelectables.addElement("attribute[" + PGPerfCharsConstants.ATTR_PG_REPORTTYPE + "]");
						slSelectables.addElement("attribute[" + PGPerfCharsConstants.ATTR_PG_CHARACTERISTIC + "]");
						slSelectables
								.addElement("attribute[" + PGPerfCharsConstants.ATTR_PG_LOWERSPECIFICATIONLIMIT + "]");
						slSelectables
								.addElement("attribute[" + PGPerfCharsConstants.ATTR_PG_UPPERSPECIFICATIONLIMIT + "]");
						slSelectables
								.addElement("attribute[" + PGPerfCharsConstants.ATTR_PG_LOWERROUTINERELEASELIMIT + "]");
						slSelectables
								.addElement("attribute[" + PGPerfCharsConstants.ATTR_PG_UPPERROUTINERELEASELIMIT + "]");
						slSelectables.addElement("attribute[" + PGPerfCharsConstants.ATTR_PG_TARGET + "]");
						slSelectables.addElement("attribute[" + PGPerfCharsConstants.ATTR_PG_LOWERTARGET + "]");
						slSelectables.addElement("attribute[" + PGPerfCharsConstants.ATTR_PG_UPPERTARGET + "]");
						slSelectables
								.addElement("to[" + DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT + "].from.type");

						StringList slRelSelect = new StringList(1);
						slRelSelect.addElement(PGPerfCharsConstants.SELECT_ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE);

						// Fetch the Characteristics connected to the context Product
						mlCharInfo = getProductCharacteristics(context, strObjectId, slSelectables, slRelSelect);

						Map hArgs = new HashMap();
						hArgs.put("objectId", strObjectId);

						// Fetch all the Configuration Options for the context Product
						MapList mlConfOptionsList = getConfOptionsListForRootProduct(context, JPO.packArgs(hArgs));
						// Fetch the Characteristics connected to the Configuration Feature-Option
						// relationship
						MapList mlConfOptionCharList = getConsolidatedCharMapList(context, mlConfOptionsList);

						if (!mlConfOptionCharList.isEmpty()) {
							mlCharInfo.addAll(mlConfOptionCharList);
						}
					}
				}
			}
		} catch (Exception ex) {
			
			throw ex;
		}
		return mlCharInfo;
	}

	/**
	 * This method returns Performance Characteristics connected to Product.
	 * 
	 * @param context   - Enovia Context
	 * @param productId - Product Id
	 * @return
	 * @throws Exception
	 */
	public MapList getProductCharacteristics(Context context, String productId, StringList objectSelects,
			StringList relSelects) throws Exception {
		MapList mlProductCharList = new MapList();
		try {
			DomainObject domProduct = DomainObject.newInstance(context, productId);
			mlProductCharList = domProduct.getRelatedObjects(context, PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC,
					PGPerfCharsConstants.TYPE_PG_PERFORMANCE_CHARACTERSTIC, objectSelects, relSelects, false, true,
					(short) 1, null, null, 0);

		} catch (Exception ex) {
			
		}
		return mlProductCharList;
	}

	/**
	 * This method returns the list of all Configuration options under the reference
	 * Product
	 * 
	 * @param context Enovia COntext object
	 * @param args    arguments
	 * @return MapList containing Configuration options
	 * @throws Exception
	 */
	public MapList getConfOptionsListForRootProduct(Context context, String args[]) throws Exception {
		MapList mlReturn = new MapList();
		Map confFeatureMap = null;
		String strCFObjId = null;
		String strCurrentState = null;
		ConfigurationFeature cfOptBean = null;
		MapList mapConfigurationOptions = null;
		try {
			HashMap programMap = (HashMap) JPO.unpackArgs(args);
			String objectId = (String) programMap.get("parentOID");
			if (UIUtil.isNullOrEmpty(objectId))
				objectId = (String) programMap.get("objectId");

			if (UIUtil.isNotNullAndNotEmpty(objectId)) {
				// Get Configuration Features connected to Product
				StringList objectSelects = new StringList(2);
				objectSelects.addElement(DomainConstants.SELECT_ID);
				objectSelects.addElement(DomainConstants.SELECT_CURRENT);
				StringList relSelects = new StringList(DomainRelationship.SELECT_ID);
				// Get Configuration options
				ConfigurationFeature cfBean = new ConfigurationFeature(objectId);
				MapList mapConfigurationFeatureList = (MapList) cfBean.getConfigurationFeatureStructure(context,
						ConfigurationConstants.TYPE_CONFIGURATION_FEATURES,
						ConfigurationConstants.RELATIONSHIP_CONFIGURATION_STRUCTURES, objectSelects, relSelects, false,
						true, (short) 1, 0, null, DomainConstants.EMPTY_STRING, DomainObject.FILTER_STR_AND_ITEM, "");

				for (Object objMap : mapConfigurationFeatureList) {
					confFeatureMap = (Map) objMap;
					strCurrentState = (String) confFeatureMap.get(DomainConstants.SELECT_CURRENT);
					if (!"#DENIED!".equalsIgnoreCase(strCurrentState)) {
						strCFObjId = (String) confFeatureMap.get(DomainConstants.SELECT_ID);
						cfOptBean = new ConfigurationFeature(strCFObjId);
						// Get Configuration Option connected to each Configuration Feature
						mapConfigurationOptions = (MapList) cfOptBean.getConfigurationFeatureStructure(context,
								ConfigurationConstants.TYPE_CONFIGURATION_OPTION,
								ConfigurationConstants.RELATIONSHIP_CONFIGURATION_OPTIONS, objectSelects, relSelects,
								false, true, (short) 1, 0, null, DomainObject.EMPTY_STRING,
								DomainObject.FILTER_STR_AND_ITEM, "");

						if (!mapConfigurationOptions.isEmpty())
							mlReturn.addAll(mapConfigurationOptions);
					}
				}
			}
			return mlReturn;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	/**
	 * This method returns a consolidated Maplist to display Configuration Option
	 * related characteristic
	 * 
	 * @param context ENOVIA context object
	 * @param tmpList MapList of Configuraion option IDs
	 * @return a Maplist
	 * @throws Exception
	 */
	public MapList getConsolidatedCharMapList(Context context, MapList optionMapList) throws Exception {
		StringList strConnectionIdList = new StringList();
		MapList mlRelInfoList = new MapList();
		MapList finalCharMapList = new MapList();
		StringList strUniqIdList = new StringList();
		String strObjId = null;
		StringList strObjIdList = null;
		StringList strObjTypeList = null;
		StringList strObjNameList = null;
		StringList strObjPolicyList = null;
		StringList strObjStateList = null;
		StringList strRelIdList = null;
		StringList strRelAttrList = null;
		StringList strRelAttrInhTypeList = null;
		StringList strObjAttrTMLOGIC = null;
		StringList strObjAttrActionRequired = null;
		StringList strObjAttrRepType = null;
		StringList strObjAttrChar = null;
		StringList strObjAttrLowSpecLimit = null;
		StringList strObjAttrUpperSpecLimit = null;
		StringList strObjAttrLowRouteinLimit = null;
		StringList strObjAttrUpperRouteinLimit = null;
		StringList strObjAttrTarget = null;
		StringList strObjAttrLowerTarget = null;
		StringList strObjAttrUpperTarget = null;

		Map tempOptionMap = null;
		String strConnId = null;
		Map charInfoMap = null;
		Object charObjMap = null;
		Map tempCharMap = null;
		Map finalCharMap = null;

		try {
			if (optionMapList != null && !optionMapList.isEmpty()) {
				for (Object optionMap : optionMapList) {
					tempOptionMap = (Map) optionMap;
					strConnId = (String) tempOptionMap.get(DomainRelationship.SELECT_ID);
					strConnectionIdList.add(strConnId);
				}
			}

			String[] connectionOidsArray = new String[strConnectionIdList.size()];
			connectionOidsArray = (String[]) strConnectionIdList.toArray(connectionOidsArray);
			StringList slrelationshipSelects = new StringList(19);
			slrelationshipSelects.addElement("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC + "].to."
					+ DomainConstants.SELECT_ID);
			slrelationshipSelects.addElement(
					"frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC + "]." + DomainConstants.SELECT_ID);
			slrelationshipSelects.addElement("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC
					+ "].attribute[" + PGPerfCharsConstants.ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE + "].value");
			slrelationshipSelects.addElement("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC + "].to."
					+ DomainConstants.SELECT_CURRENT);
			slrelationshipSelects.addElement("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC + "].to."
					+ DomainConstants.SELECT_NAME);
			slrelationshipSelects.addElement("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC + "].to."
					+ DomainConstants.SELECT_TYPE);
			slrelationshipSelects.addElement("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC + "].to."
					+ DomainConstants.SELECT_POLICY);
			slrelationshipSelects.addElement("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC
					+ "].attribute[" + PGPerfCharsConstants.ATTR_PG_INHERITANCE_TYPE + "].value");

			slrelationshipSelects.addElement("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC
					+ "].to.attribute[" + PGPerfCharsConstants.ATTR_PG_TMLOGIC + "]");
			slrelationshipSelects.addElement("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC
					+ "].to.attribute[" + PGPerfCharsConstants.ATTR_ACTION_REQUIRED + "]");
			slrelationshipSelects.addElement("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC
					+ "].to.attribute[" + PGPerfCharsConstants.ATTR_PG_REPORTTYPE + "]");
			slrelationshipSelects.addElement("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC
					+ "].to.attribute[" + PGPerfCharsConstants.ATTR_PG_CHARACTERISTIC + "]");
			slrelationshipSelects.addElement("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC
					+ "].to.attribute[" + PGPerfCharsConstants.ATTR_PG_LOWERSPECIFICATIONLIMIT + "]");
			slrelationshipSelects.addElement("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC
					+ "].to.attribute[" + PGPerfCharsConstants.ATTR_PG_UPPERSPECIFICATIONLIMIT + "]");
			slrelationshipSelects.addElement("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC
					+ "].to.attribute[" + PGPerfCharsConstants.ATTR_PG_LOWERROUTINERELEASELIMIT + "]");
			slrelationshipSelects.addElement("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC
					+ "].to.attribute[" + PGPerfCharsConstants.ATTR_PG_UPPERROUTINERELEASELIMIT + "]");
			slrelationshipSelects.addElement("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC
					+ "].to.attribute[" + PGPerfCharsConstants.ATTR_PG_TARGET + "]");
			slrelationshipSelects.addElement("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC
					+ "].to.attribute[" + PGPerfCharsConstants.ATTR_PG_LOWERTARGET + "]");
			slrelationshipSelects.addElement("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC
					+ "].to.attribute[" + PGPerfCharsConstants.ATTR_PG_UPPERTARGET + "]");

			mlRelInfoList = DomainRelationship.getInfo(context, connectionOidsArray, slrelationshipSelects);
			int iRelInfoListSize = mlRelInfoList.size();
			for (int i = 0; i < iRelInfoListSize; i++) {
				charInfoMap = (Map) mlRelInfoList.get(i);
				charObjMap = (Object) charInfoMap
						.get("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC + "].to.id");
				if (charObjMap != null) {
					if (charObjMap instanceof String) {
						tempCharMap = new HashMap();
						strObjId = ((String) charInfoMap
								.get("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC + "].to.id"));
						tempCharMap.put(DomainConstants.SELECT_ID, strObjId);
						tempCharMap.put(DomainConstants.SELECT_LEVEL, "1");
						tempCharMap.put(DomainRelationship.SELECT_ID,
								((String) charInfoMap.get("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC
										+ "]." + DomainConstants.SELECT_ID)));
						tempCharMap.put(
								"attribute[" + PGPerfCharsConstants.ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE + "]",
								((String) charInfoMap.get(
										"frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC + "].attribute["
												+ PGPerfCharsConstants.ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE
												+ "].value")));
						tempCharMap.put(DomainConstants.SELECT_TYPE,
								((String) charInfoMap.get("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC
										+ "].to." + DomainConstants.SELECT_TYPE)));
						tempCharMap.put(DomainConstants.SELECT_NAME,
								((String) charInfoMap.get("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC
										+ "].to." + DomainConstants.SELECT_NAME)));
						tempCharMap.put(DomainConstants.SELECT_POLICY,
								((String) charInfoMap.get("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC
										+ "].to." + DomainConstants.SELECT_POLICY)));
						tempCharMap.put(DomainConstants.SELECT_CURRENT,
								((String) charInfoMap.get("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC
										+ "].to." + DomainConstants.SELECT_CURRENT)));
						tempCharMap.put("attribute[" + PGPerfCharsConstants.ATTR_PG_INHERITANCE_TYPE + "]",
								((String) charInfoMap.get("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC
										+ "].attribute[" + PGPerfCharsConstants.ATTR_PG_INHERITANCE_TYPE + "].value")));

						tempCharMap.put("attribute[" + PGPerfCharsConstants.ATTR_PG_TMLOGIC + "]",
								((String) charInfoMap.get("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC
										+ "].to.attribute[" + PGPerfCharsConstants.ATTR_PG_TMLOGIC + "]")));
						tempCharMap.put("attribute[" + PGPerfCharsConstants.ATTR_ACTION_REQUIRED + "]",
								((String) charInfoMap.get("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC
										+ "].to.attribute[" + PGPerfCharsConstants.ATTR_ACTION_REQUIRED + "]")));
						tempCharMap.put("attribute[" + PGPerfCharsConstants.ATTR_PG_REPORTTYPE + "]",
								((String) charInfoMap.get("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC
										+ "].to.attribute[" + PGPerfCharsConstants.ATTR_PG_REPORTTYPE + "]")));
						tempCharMap.put("attribute[" + PGPerfCharsConstants.ATTR_PG_CHARACTERISTIC + "]",
								((String) charInfoMap.get("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC
										+ "].to.attribute[" + PGPerfCharsConstants.ATTR_PG_CHARACTERISTIC + "]")));
						tempCharMap.put("attribute[" + PGPerfCharsConstants.ATTR_PG_LOWERSPECIFICATIONLIMIT + "]",
								((String) charInfoMap.get("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC
										+ "].to.attribute[" + PGPerfCharsConstants.ATTR_PG_LOWERSPECIFICATIONLIMIT
										+ "]")));
						tempCharMap.put("attribute[" + PGPerfCharsConstants.ATTR_PG_UPPERSPECIFICATIONLIMIT + "]",
								((String) charInfoMap.get("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC
										+ "].to.attribute[" + PGPerfCharsConstants.ATTR_PG_UPPERSPECIFICATIONLIMIT
										+ "]")));
						tempCharMap.put("attribute[" + PGPerfCharsConstants.ATTR_PG_LOWERROUTINERELEASELIMIT + "]",
								((String) charInfoMap.get("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC
										+ "].to.attribute[" + PGPerfCharsConstants.ATTR_PG_LOWERROUTINERELEASELIMIT
										+ "]")));
						tempCharMap.put("attribute[" + PGPerfCharsConstants.ATTR_PG_UPPERROUTINERELEASELIMIT + "]",
								((String) charInfoMap.get("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC
										+ "].to.attribute[" + PGPerfCharsConstants.ATTR_PG_UPPERROUTINERELEASELIMIT
										+ "]")));
						tempCharMap.put("attribute[" + PGPerfCharsConstants.ATTR_PG_TARGET + "]",
								((String) charInfoMap.get("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC
										+ "].to.attribute[" + PGPerfCharsConstants.ATTR_PG_TARGET + "]")));
						tempCharMap.put("attribute[" + PGPerfCharsConstants.ATTR_PG_LOWERTARGET + "]",
								((String) charInfoMap.get("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC
										+ "].to.attribute[" + PGPerfCharsConstants.ATTR_PG_LOWERTARGET + "]")));
						tempCharMap.put("attribute[" + PGPerfCharsConstants.ATTR_PG_UPPERTARGET + "]",
								((String) charInfoMap.get("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC
										+ "].to.attribute[" + PGPerfCharsConstants.ATTR_PG_UPPERTARGET + "]")));

						if (!strUniqIdList.contains(strObjId)) {
							strUniqIdList.add(strObjId);
							finalCharMapList.add(tempCharMap);
						}
					} else {
						strObjIdList = (StringList) charInfoMap
								.get("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC + "].to."
										+ DomainConstants.SELECT_ID);
						strObjTypeList = (StringList) charInfoMap
								.get("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC + "].to."
										+ DomainConstants.SELECT_TYPE);
						strObjNameList = (StringList) charInfoMap
								.get("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC + "].to."
										+ DomainConstants.SELECT_NAME);
						strObjPolicyList = (StringList) charInfoMap
								.get("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC + "].to."
										+ DomainConstants.SELECT_POLICY);
						strObjStateList = (StringList) charInfoMap
								.get("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC + "].to."
										+ DomainConstants.SELECT_CURRENT);

						strRelIdList = (StringList) charInfoMap.get("frommid["
								+ PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC + "]." + DomainConstants.SELECT_ID);
						strRelAttrList = (StringList) charInfoMap
								.get("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC + "].attribute["
										+ PGPerfCharsConstants.ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE + "].value");
						strRelAttrInhTypeList = (StringList) charInfoMap
								.get("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC + "].attribute["
										+ PGPerfCharsConstants.ATTR_PG_INHERITANCE_TYPE + "].value");
						strObjAttrTMLOGIC = (StringList) charInfoMap
								.get("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC + "].to.attribute["
										+ PGPerfCharsConstants.ATTR_PG_TMLOGIC + "]");
						strObjAttrActionRequired = (StringList) charInfoMap
								.get("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC + "].to.attribute["
										+ PGPerfCharsConstants.ATTR_ACTION_REQUIRED + "]");
						strObjAttrRepType = (StringList) charInfoMap
								.get("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC + "].to.attribute["
										+ PGPerfCharsConstants.ATTR_PG_REPORTTYPE + "]");
						strObjAttrChar = (StringList) charInfoMap
								.get("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC + "].to.attribute["
										+ PGPerfCharsConstants.ATTR_PG_CHARACTERISTIC + "]");
						strObjAttrLowSpecLimit = (StringList) charInfoMap
								.get("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC + "].to.attribute["
										+ PGPerfCharsConstants.ATTR_PG_LOWERSPECIFICATIONLIMIT + "]");
						strObjAttrUpperSpecLimit = (StringList) charInfoMap
								.get("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC + "].to.attribute["
										+ PGPerfCharsConstants.ATTR_PG_UPPERSPECIFICATIONLIMIT + "]");
						strObjAttrLowRouteinLimit = (StringList) charInfoMap
								.get("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC + "].to.attribute["
										+ PGPerfCharsConstants.ATTR_PG_LOWERROUTINERELEASELIMIT + "]");
						strObjAttrUpperRouteinLimit = (StringList) charInfoMap
								.get("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC + "].to.attribute["
										+ PGPerfCharsConstants.ATTR_PG_UPPERROUTINERELEASELIMIT + "]");
						strObjAttrTarget = (StringList) charInfoMap
								.get("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC + "].to.attribute["
										+ PGPerfCharsConstants.ATTR_PG_TARGET + "]");
						strObjAttrLowerTarget = (StringList) charInfoMap
								.get("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC + "].to.attribute["
										+ PGPerfCharsConstants.ATTR_PG_LOWERTARGET + "]");
						strObjAttrUpperTarget = (StringList) charInfoMap
								.get("frommid[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC + "].to.attribute["
										+ PGPerfCharsConstants.ATTR_PG_UPPERTARGET + "]");

						// iterate over both lists simultaneously
						int iObjIdIdListSize = strObjIdList.size();
						for (int num = 0; num < iObjIdIdListSize; num++) {
							finalCharMap = new HashMap();
							strObjId = (String) strObjIdList.get(num);
							finalCharMap.put(DomainConstants.SELECT_ID, strObjId);
							finalCharMap.put(DomainConstants.SELECT_LEVEL, "1");
							finalCharMap.put(DomainRelationship.SELECT_ID, strRelIdList.get(num));
							finalCharMap.put("attribute["
									+ PGPerfCharsConstants.ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE + "]",
									strRelAttrList.get(num));

							finalCharMap.put(DomainConstants.SELECT_TYPE, strObjTypeList.get(num));
							finalCharMap.put(DomainConstants.SELECT_NAME, strObjNameList.get(num));
							finalCharMap.put(DomainConstants.SELECT_POLICY, strObjPolicyList.get(num));
							finalCharMap.put(DomainConstants.SELECT_CURRENT, strObjStateList.get(num));

							finalCharMap.put("attribute[" + PGPerfCharsConstants.ATTR_PG_INHERITANCE_TYPE + "]",
									strRelAttrInhTypeList.get(num));
							finalCharMap.put("attribute[" + PGPerfCharsConstants.ATTR_PG_TMLOGIC + "]",
									strObjAttrTMLOGIC.get(num));
							finalCharMap.put("attribute[" + PGPerfCharsConstants.ATTR_ACTION_REQUIRED + "]",
									strObjAttrActionRequired.get(num));
							finalCharMap.put("attribute[" + PGPerfCharsConstants.ATTR_PG_REPORTTYPE + "]",
									strObjAttrRepType.get(num));
							finalCharMap.put("attribute[" + PGPerfCharsConstants.ATTR_PG_CHARACTERISTIC + "]",
									strObjAttrChar.get(num));
							finalCharMap.put("attribute[" + PGPerfCharsConstants.ATTR_PG_LOWERSPECIFICATIONLIMIT + "]",
									strObjAttrLowSpecLimit.get(num));
							finalCharMap.put("attribute[" + PGPerfCharsConstants.ATTR_PG_UPPERSPECIFICATIONLIMIT + "]",
									strObjAttrUpperSpecLimit.get(num));
							finalCharMap.put("attribute[" + PGPerfCharsConstants.ATTR_PG_LOWERROUTINERELEASELIMIT + "]",
									strObjAttrLowRouteinLimit.get(num));
							finalCharMap.put("attribute[" + PGPerfCharsConstants.ATTR_PG_UPPERROUTINERELEASELIMIT + "]",
									strObjAttrUpperRouteinLimit.get(num));
							finalCharMap.put("attribute[" + PGPerfCharsConstants.ATTR_PG_TARGET + "]",
									strObjAttrTarget.get(num));
							finalCharMap.put("attribute[" + PGPerfCharsConstants.ATTR_PG_LOWERTARGET + "]",
									strObjAttrLowerTarget.get(num));
							finalCharMap.put("attribute[" + PGPerfCharsConstants.ATTR_PG_UPPERTARGET + "]",
									strObjAttrUpperTarget.get(num));

							if (!strUniqIdList.contains(strObjId)) {
								strUniqIdList.add(strObjId);
								finalCharMapList.add(finalCharMap);
							}
						}
					}
				}
			}
		} catch (Exception exc) {
			
		}
		return finalCharMapList;
	}

	/**
	 * Method to perform Characteristic validations
	 * 
	 * @param context
	 * @param mpTargetLimitMap
	 * @param mpCharInfo
	 * @param strLimitTargetArray
	 * @param iArrayLength
	 * @param iReducedArrayLength
	 * @param strPropertyFile
	 * @param strSessionLanguage
	 * @return
	 * @throws Exception
	 */
	public static String[] performCharValidations(Context context, Map mpTargetLimitMap, Map<String, String> mpCharInfo,
			String[] strLimitTargetArray, int iArrayLength, int iReducedArrayLength, String strPropertyFile,
			String strSessionLanguage, boolean isLPD) throws Exception {

		String[] strResult = new String[3];
		String strAttrActionRequired = DomainConstants.EMPTY_STRING;
		String strAttrReportType = DomainConstants.EMPTY_STRING;
		String strAttrCharacteristic = DomainConstants.EMPTY_STRING;
		String strAttrLowerSpecLimit = DomainConstants.EMPTY_STRING;
		String strAttrUpperSpecLimit = DomainConstants.EMPTY_STRING;
		String strAttrLowerRoutineReleaseLimit = DomainConstants.EMPTY_STRING;
		String strAttrUpperRoutineReleaseLimit = DomainConstants.EMPTY_STRING;
		String strAttrTarget = DomainConstants.EMPTY_STRING;
		String strAttrUpperTarget = DomainConstants.EMPTY_STRING;
		String strAttrLowerTarget = DomainConstants.EMPTY_STRING;
		String strAttrSeqNumber = DomainConstants.EMPTY_STRING;
		String strAttrTMLogic = DomainConstants.EMPTY_STRING;
		int countTMs = 0;
		String strTMValue = DomainConstants.EMPTY_STRING;
		try {
			String strUpdateChar = i18nNow.getI18nString("emxCPN.Alert.updateCharacteristic", strPropertyFile,
					strSessionLanguage);
			String strUpdateActionReq = i18nNow.getI18nString("emxCPN.Alert.updateActionRequired", strPropertyFile,
					strSessionLanguage);
			String strValidateReportType = i18nNow.getI18nString("emxCPN.Alert.ValidateReportTypeValue",
					strPropertyFile, strSessionLanguage);
			String strPleaseSelectLimitOrTargetMsg = i18nNow.getI18nString(
					"emxCPN.Alert.ValidateReportTypeForTargetLimit", strPropertyFile, strSessionLanguage);
			String strValidateTargetValue = i18nNow.getI18nString("emxCPN.Alert.ValidateTargetValue", strPropertyFile,
					strSessionLanguage);
			String strValidateSpecLimit = i18nNow.getI18nString("emxCPN.Alert.ValidateReportTypeForSpec",
					strPropertyFile, strSessionLanguage);
			String strValidateColumnValue = i18nNow.getI18nString("emxCPN.Alert.ValidateColumnValue", strPropertyFile,
					strSessionLanguage);
			String strValidateTMLogicValue = i18nNow.getI18nString("emxCPN.Alert.ValidateTMLogic", strPropertyFile,
					strSessionLanguage);
			StringBuilder sbValidationFailureMsg = new StringBuilder();
			StringBuilder sbIntermediateMsg = new StringBuilder();
			Object strTMValueObj = null;
			if (isLPD) {
				strTMValueObj = mpCharInfo
						.get("from[" + ENOCharacteristicEnum.CharacteristicRelationships.CHARACTERISTIC_TEST_METHOD
								.getRelationship(context) + "].to.type");
			} else {
				strTMValueObj = mpCharInfo.get("to[" + DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT + "].from.type");
			}
			if (null != strTMValueObj) {
				if (strTMValueObj instanceof StringList) {
					StringList strTMValueList = (StringList) strTMValueObj;
					Iterator itr = strTMValueList.iterator();
					while (itr.hasNext()) {
						strTMValue = (String) itr.next();
						if (strTMValue.equals(PGPerfCharsConstants.TYPE_TEST_METHOD_SPECIFICATION)
								|| strTMValue.equals(PGPerfCharsConstants.TYPE_PG_TEST_METHOD)) {
							countTMs++;
						}
					}
				}
			}
			strAttrTMLogic = mpCharInfo.get("attribute[" + PGPerfCharsConstants.ATTR_PG_TMLOGIC + "]");
			strAttrActionRequired = mpCharInfo.get("attribute[" + PGPerfCharsConstants.ATTR_ACTION_REQUIRED + "]");
			strAttrCharacteristic = mpCharInfo.get("attribute[" + PGPerfCharsConstants.ATTR_PG_CHARACTERISTIC + "]");
			strAttrReportType = mpCharInfo.get("attribute[" + PGPerfCharsConstants.ATTR_PG_REPORTTYPE + "]");
			strAttrSeqNumber = mpCharInfo.get(PGPerfCharsConstants.SELECT_ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE);
			if (isLPD) {
				strLimitTargetArray[0] = strAttrLowerSpecLimit = mpCharInfo.get(
						"attribute[" + PGPerfCharsConstants.ATTRIBUTE_CHARACTERISTICSLOWERSPECIFICATIONLIMIT + "]");
				strLimitTargetArray[1] = strAttrLowerRoutineReleaseLimit = mpCharInfo.get(
						"attribute[" + PGPerfCharsConstants.ATTRIBUTE_CHARACTERISTICSLOWERROUTINERELEASELIMIT + "]");
				strLimitTargetArray[5] = strAttrUpperRoutineReleaseLimit = mpCharInfo.get(
						"attribute[" + PGPerfCharsConstants.ATTRIBUTE_CHARACTERISTICSUPPERROUTINERELEASELIMIT + "]");
				strLimitTargetArray[6] = strAttrUpperSpecLimit = mpCharInfo.get(
						"attribute[" + PGPerfCharsConstants.ATTRIBUTE_CHARACTERISTICSUPPERSPECIFICATIONLIMIT + "]");
				String strCategory = mpCharInfo.get(PGPerfCharsConstants.SELECT_ATTRIBUTE_PG_CHARACTERISTIC_CATEGORY);
				String strCategorySpecific = mpCharInfo.get(PGPerfCharsConstants.SELECT_ATTRIBUTE_PG_CATEGORYSPECIFICS);
				String strCharcteristics = mpCharInfo.get(PGPerfCharsConstants.SELECT_ATTRIBUTE_TITLE);
				String strCharacteristicSpecifics = mpCharInfo
						.get(PGPerfCharsConstants.SELECT_ATTRIBUTE_CHARACTERISTICSPECIFIC);
				strPleaseSelectLimitOrTargetMsg = new StringBuilder(strPleaseSelectLimitOrTargetMsg)
						.append(PGPerfCharsConstants.BLANK_SPACE).append(PGPerfCharsConstants.CONSTANT_STRING_COLON)
						.append(PGPerfCharsConstants.BLANK_SPACE).append(strCategory)
						.append(PGPerfCharsConstants.CONSTANT_STRING_PIPE).append(strCategorySpecific)
						.append(PGPerfCharsConstants.CONSTANT_STRING_PIPE).append(strCharcteristics)
						.append(PGPerfCharsConstants.CONSTANT_STRING_PIPE).append(strCharacteristicSpecifics)
						.toString();
				strValidateSpecLimit = new StringBuilder(strValidateSpecLimit).append(PGPerfCharsConstants.BLANK_SPACE)
						.append(PGPerfCharsConstants.CONSTANT_STRING_COLON).append(PGPerfCharsConstants.BLANK_SPACE)
						.append(strCategory).append(PGPerfCharsConstants.CONSTANT_STRING_PIPE)
						.append(strCategorySpecific).append(PGPerfCharsConstants.CONSTANT_STRING_PIPE)
						.append(strCharcteristics).append(PGPerfCharsConstants.CONSTANT_STRING_PIPE)
						.append(strCharacteristicSpecifics).toString();
			} else {
				strLimitTargetArray[0] = strAttrLowerSpecLimit = mpCharInfo
						.get("attribute[" + PGPerfCharsConstants.ATTR_PG_LOWERSPECIFICATIONLIMIT + "]");
				strLimitTargetArray[1] = strAttrLowerRoutineReleaseLimit = mpCharInfo
						.get("attribute[" + PGPerfCharsConstants.ATTR_PG_LOWERROUTINERELEASELIMIT + "]");
				strLimitTargetArray[5] = strAttrUpperRoutineReleaseLimit = mpCharInfo
						.get("attribute[" + PGPerfCharsConstants.ATTR_PG_UPPERROUTINERELEASELIMIT + "]");
				strLimitTargetArray[6] = strAttrUpperSpecLimit = mpCharInfo
						.get("attribute[" + PGPerfCharsConstants.ATTR_PG_UPPERSPECIFICATIONLIMIT + "]");
			}
			strLimitTargetArray[2] = strAttrUpperTarget = mpCharInfo
					.get("attribute[" + PGPerfCharsConstants.ATTR_PG_LOWERTARGET + "]");
			strLimitTargetArray[3] = strAttrTarget = mpCharInfo
					.get("attribute[" + PGPerfCharsConstants.ATTR_PG_TARGET + "]");
			strLimitTargetArray[4] = strAttrLowerTarget = mpCharInfo
					.get("attribute[" + PGPerfCharsConstants.ATTR_PG_UPPERTARGET + "]");

			if (!isLPD && UIUtil.isNullOrEmpty(strAttrCharacteristic)) {
				sbIntermediateMsg.append(strUpdateChar).append("\n");
			}
			if (UIUtil.isNullOrEmpty(strAttrActionRequired)) {
				sbIntermediateMsg.append(strUpdateActionReq).append("\n");
			}
			if ((countTMs > 1) && UIUtil.isNullOrEmpty(strAttrTMLogic)) {
				sbIntermediateMsg.append(strValidateTMLogicValue).append("\n");
			}
			if (UIUtil.isNotNullAndNotEmpty(strAttrTMLogic)
					&& !(PGPerfCharsConstants.RANGE_VALUE_TMLOGIC_ALL.equalsIgnoreCase(strAttrTMLogic)
							|| PGPerfCharsConstants.RANGE_VALUE_TMLOGIC_ANY.equalsIgnoreCase(strAttrTMLogic))) {
				sbIntermediateMsg.append(strValidateTMLogicValue).append("\n");
			}
			if ((PGPerfCharsConstants.RANGE_VALUE_REPORT.equalsIgnoreCase(strAttrActionRequired)
					|| PGPerfCharsConstants.RANGE_VALUE_SUMMARY.equalsIgnoreCase(strAttrActionRequired))
					&& !(PGPerfCharsConstants.RANGE_VALUE_VARIABLE.equalsIgnoreCase(strAttrReportType)
							|| PGPerfCharsConstants.RANGE_VALUE_ATTRIBUTE.equalsIgnoreCase(strAttrReportType))) {
				sbIntermediateMsg.append(strValidateReportType).append("\n");
			}
			if (PGPerfCharsConstants.RANGE_VALUE_VARIABLE.equalsIgnoreCase(strAttrReportType)) {

				if (UIUtil.isNullOrEmpty(strAttrLowerSpecLimit) && UIUtil.isNullOrEmpty(strAttrUpperSpecLimit)
						&& UIUtil.isNullOrEmpty(strAttrTarget) && UIUtil.isNullOrEmpty(strAttrUpperTarget)
						&& UIUtil.isNullOrEmpty(strAttrLowerTarget)) {
					sbIntermediateMsg.append(strPleaseSelectLimitOrTargetMsg).append("\n");
				}

			} else if (PGPerfCharsConstants.RANGE_VALUE_ATTRIBUTE.equalsIgnoreCase(strAttrReportType)) {

				if (UIUtil.isNotNullAndNotEmpty(strAttrLowerRoutineReleaseLimit)
						|| UIUtil.isNotNullAndNotEmpty(strAttrUpperRoutineReleaseLimit)
						|| UIUtil.isNotNullAndNotEmpty(strAttrTarget) || UIUtil.isNotNullAndNotEmpty(strAttrUpperTarget)
						|| UIUtil.isNotNullAndNotEmpty(strAttrLowerTarget)) {
					if (UIUtil.isNullOrEmpty(strAttrLowerSpecLimit) && UIUtil.isNullOrEmpty(strAttrUpperSpecLimit)) {

						sbIntermediateMsg.append(strValidateSpecLimit).append("\n");
					}
				}

			}
			if (!isLPD) {
				if (UIUtil.isNotNullAndNotEmpty(strAttrTarget) && (UIUtil.isNotNullAndNotEmpty(strAttrLowerTarget)
						|| UIUtil.isNotNullAndNotEmpty(strAttrUpperTarget))) {

					sbIntermediateMsg.append(strValidateTargetValue).append("\n");
				}
			}
			for (int index = 0; index < iArrayLength; index++) {

				for (int iSecIndex = index; iSecIndex < iReducedArrayLength; iSecIndex++) {

					if (NumberUtils.isNumber(strLimitTargetArray[iSecIndex + 1])
							&& NumberUtils.isNumber(strLimitTargetArray[index])) {
						if ((index == 0) && (iSecIndex + 1 == 1) || (index == 5) && (iSecIndex + 1 == 6)) {
							if (Float.parseFloat(strLimitTargetArray[iSecIndex + 1]) < Float
									.parseFloat(strLimitTargetArray[index])) {
								sbIntermediateMsg.append(mpTargetLimitMap.get(index)).append(" ")
										.append(strValidateColumnValue).append(" ")
										.append(mpTargetLimitMap.get(iSecIndex + 1)).append("\n");
							}
						} else {
							if (Float.parseFloat(strLimitTargetArray[iSecIndex + 1]) <= Float
									.parseFloat(strLimitTargetArray[index])) {
								sbIntermediateMsg.append(mpTargetLimitMap.get(index)).append(" ")
										.append(strValidateColumnValue).append(" ")
										.append(mpTargetLimitMap.get(iSecIndex + 1)).append("\n");
							}
						}
					}
				}
			}
			
			//DSM: 2022x.5 : Nexus PC Validations : Start
			
			HashMap hmArgs = new HashMap();	
			hmArgs.put("CharInfoMap",mpCharInfo);
			StringList slNexusPCValidations = JPO.invoke(context,"pgDSMNexusPCUtil",null,"executeNexusPCValidations",JPO.packArgs(hmArgs),StringList.class);
			String strAttributes = JPO.invoke(context,"pgDSMNexusPCUtil",null,"validationRule9ReportTypeCheckForWebService",JPO.packArgs(hmArgs),String.class);
			updateErrorMsgForNexusData(context,sbIntermediateMsg,slNexusPCValidations,strAttributes);

			if (slNexusPCValidations.size() > 0) {
				strResult[2] = "Fail";
			} else {
				strResult[2] = "Pass";
			}
			//DSM: 2022x.5 : Nexus PC Validations : End
			
			if (UIUtil.isNotNullAndNotEmpty(sbIntermediateMsg.toString())) {
				if (sbIntermediateMsg.indexOf(",") > -1) {
					sbIntermediateMsg.deleteCharAt(sbIntermediateMsg.lastIndexOf(","));
				}
				sbValidationFailureMsg.append(
						i18nNow.getI18nString("emxCPN.Common.SequenceNumber", strPropertyFile, strSessionLanguage))
						.append(" : ").append(strAttrSeqNumber).append("\n");
			}
			strResult[0] = sbValidationFailureMsg.toString();
			strResult[1] = sbIntermediateMsg.toString();
		} catch (Exception ex) {
			
			throw ex;
		}
		return strResult;
	}

	/**
	 * Method to update the Nexus Data related validations
	 * @param context
	 * @param sbIntermediateMsg
	 * @param slNexusPCValidations
	 * @param strAttributes
	 */
	private static void updateErrorMsgForNexusData(Context context, StringBuilder sbIntermediateMsg,
			StringList slNexusPCValidations, String strAttributes) {
		if(strAttributes != null) {
			int iListSize = slNexusPCValidations.size();
			for(int i=0;i<iListSize;i++) {
				String strErrorKey =  slNexusPCValidations.get(i);
				if(UIUtil.isNotNullAndNotEmpty(strErrorKey) && mpNexusValidationMsgMap.containsKey(strErrorKey)) {
					String strErrorPropertyKey = mpNexusValidationMsgMap.get(strErrorKey);
					String strErrorMsg = getPropertyLabel(context, strErrorPropertyKey);
					sbIntermediateMsg.append(strErrorMsg).append("\n");
				}
			}
		}
		
		if(UIUtil.isNotNullAndNotEmpty(strAttributes)) {
			String strErrorMsg = getPropertyLabel(context, "emxCPN.Common.NexusPerfCahrs.Attributes.Check");
			sbIntermediateMsg.append(strErrorMsg).append("\n").append(strAttributes).append("\n");
		}
		
	}
	
	/**
	 * Method to get the Map with keys and validation messages for Nexus PC data
	 * @return
	 */
	private static void updateNexusValidationMsgMap() {
		mpNexusValidationMsgMap = new HashMap<>();
		mpNexusValidationMsgMap.put("pgAuthoringApplication", "emxCPN.Common.NexusPerfCahrs.SingleTestMethod");
		mpNexusValidationMsgMap.put("pgReportToNearest", "emxCPN.Common.NexusPerfCahrs.RTN.Mandatory");
		mpNexusValidationMsgMap.put("pgSampling", "emxCPN.Common.NexusPerfCahrs.Sampling.FreqAndSamples"); 
		mpNexusValidationMsgMap.put("pgTestGroup", "emxCPN.Common.NexusPerfCahrs.TestGroup.Active"); 
		mpNexusValidationMsgMap.put("pgSampling2", "emxCPN.Common.NexusPerfCahrs.Sampling.PickListValidations"); 
		mpNexusValidationMsgMap.put("pgCharaAndCharSpec", "emxCPN.Common.NexusPerfCahrs.CharAndCharSpec.PLActive"); 
		mpNexusValidationMsgMap.put("pgTMLogic", "emxCPN.Common.NexusPerfCahrs.TestMethodAndPlantTestingLevel"); 
		mpNexusValidationMsgMap.put("pgReleaseCriteria", "emxCPN.Common.NexusPerfCahrs.ReleaseCriteria.Blank"); 
		mpNexusValidationMsgMap.put("pgDataTypeNum", "emxCPN.Common.NexusPerfCahrs.DataType.Numeric"); 
		mpNexusValidationMsgMap.put("pgDataTypeRef", "emxCPN.Common.NexusPerfCahrs.DataType.Reference");

	}
	
	/**
	 * Method to get the Properties label for keys
	 * 
	 * @param context
	 * @param strPropertyKey
	 * @return
	 */
	private static String getPropertyLabel(Context context, String strPropertyKey) {
		String strDisplayLabel = EnoviaResourceBundle.getProperty(context, "emxCPNStringResource", context.getLocale(),
				strPropertyKey);

		if (UIUtil.isNotNullAndNotEmpty(strDisplayLabel)) {
			return strDisplayLabel;
		} else {
			return strPropertyKey;
		}
	}

	/**
	 * Method to convert Map to JSON object
	 * 
	 * @param mObjectAttributeValues
	 * @return
	 */
	private JsonObjectBuilder getJsonObjectFromMap(Map<?, ?> mObjectAttributeValues) {
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		for (Map.Entry<?, ?> entry : mObjectAttributeValues.entrySet()) {
			jsonReturnObj.add((String) entry.getKey(), (String) entry.getValue());
		}
		return jsonReturnObj;
	}

}
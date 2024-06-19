package com.pg.fis.integration.formulas.services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.fis.integration.constants.PGFISWSConstants;
import com.pg.fis.integration.formula.loader.PGFISRecipeCreator;
import com.pg.fis.integration.util.PGFISIntegrationUtil;
import com.pg.fis.integration.util.PGFISCommonUtil;
import com.pg.fis.integration.util.PGFISCASAuthenticator;

import java.util.logging.Level;
import java.util.logging.Logger;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.Pattern;
import matrix.util.StringList;

public class PGFISFormulationDetails {
	private static final Logger logger = Logger.getLogger(PGFISFormulationDetails.class.getName());
	static final String ERROR_Logger = "Exception in PGFISFormulationDetails";
	/**
	 * method to get GCAS Number For FIS Integration
	 * @param enterpriseId 
	 * 
	 * @param Context Context used to call API
	 * @return response in JSON String format
	 * @throws Exception when operation fails
	 */
	public static Response getGCASCodeFromOnPrem(Context context, String physicalId, String enterpriseId) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		String objectName = "";
		try {
			String strwhere = DomainConstants.EMPTY_STRING;
			if(UIUtil.isNotNullAndNotEmpty(physicalId)) {
				if(UIUtil.isNotNullAndNotEmpty(enterpriseId)) {
					strwhere = "revision == Last && " + PGFISWSConstants.SELECT_ATTRIBUTE_REFERENCE_URI + "== '"+ physicalId + "'" + " || name ~="+enterpriseId+"*";
				} else {
					strwhere = "revision == Last && " + PGFISWSConstants.SELECT_ATTRIBUTE_REFERENCE_URI + "== '"+ physicalId + "'";
				}

				StringList slSelectable = new StringList();
				slSelectable.add(DomainConstants.SELECT_NAME);
				MapList objDetailList = PGFISIntegrationUtil.findObjectAndRetunDetails(context,
						PGFISWSConstants.TYPE_FORMULATION_PROCESS, DomainConstants.QUERY_WILDCARD, slSelectable,
						strwhere);
				if (objDetailList != null && objDetailList.size() != 0 && !objDetailList.isEmpty()) {
					Map<String, String> objectMap = (Map<String, String>) objDetailList.get(0);
					objectName = (String) objectMap.get(DomainConstants.SELECT_NAME);
					if(UIUtil.isNotNullAndNotEmpty(objectName)) {
						objectName = objectName.split("-")[0];
						output.add(PGFISWSConstants.JSON_TAG_ENTERPRISE_ID, objectName);
					}
				}
			}
			if(UIUtil.isNullOrEmpty(objectName)) {
			HashMap<String, String> strGCASDetails = JPO.invoke(context, "pgDSOGCASValidation", null,
					"getGCASNumbersForNGFUtility", new String[] {}, HashMap.class);
			String errorMessage = strGCASDetails.get("ERROR");
			if (UIUtil.isNotNullAndNotEmpty(errorMessage)) {
				output.add(PGFISWSConstants.JSON_OUTPUT_KEY_ERROR, errorMessage);
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(output.build().toString()).build();
			} else {
				Iterator<Entry<String, String>> it = strGCASDetails.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry pairs = it.next();
					output.add((String) pairs.getKey(), (String) pairs.getValue());
				}
			}
			}
		} catch (Exception ex) {
			logger.log(Level.SEVERE, ERROR_Logger, ex);
			output.add(PGFISWSConstants.JSON_OUTPUT_KEY_ERROR, ex.getMessage());
			output.add(PGFISWSConstants.JSON_OUTPUT_KEY_TRACE, PGFISIntegrationUtil.getExceptionTrace(ex));
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(output.build().toString()).build();
		}
		return Response.status(Status.OK).entity(output.build().toString()).build();
	}

	/**
	 * method to get cloud status for FBOM for given Formulation Process
	 * 
	 * @param Context    Context used to call API
	 * @param pyisicalId Business Object physicalId
	 * @return response in JSON String format
	 * @throws Exception when operation fails
	 */
	@SuppressWarnings("unchecked")
	public static Response getFormulationCloudFlagResponse(Context context, String physicalId) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonArrayBuilder jsonArr = Json.createArrayBuilder();
		try {
			boolean isRawMaterialPresentInFBOM = false;
			DomainObject domObj = DomainObject.newInstance(context, physicalId);
			StringList strListToCheckDuplicates = new StringList();
			StringList objectSelect = new StringList();
			objectSelect.add(DomainConstants.SELECT_TYPE);
			objectSelect.add(DomainConstants.SELECT_NAME);
			objectSelect.add(DomainConstants.SELECT_REVISION);
			objectSelect.add(PGFISWSConstants.SELECT_ATTRIBUTE_REFERENCE_URI);
			objectSelect.add(PGFISWSConstants.SELECT_ATTRIBUTE_REFERENCE_URI_FOR_ALL_REVS);
			objectSelect.add(PGFISWSConstants.SELECT_ATTRIBUTE_REFERENCE_IDENTIFIER);
			objectSelect.add(PGFISWSConstants.CONST_ALL_REVISIONS);
			StringList multiList = new StringList();
			multiList.add(PGFISWSConstants.CONST_ALL_REVISIONS);
			
			Map<String, String> parentDetailsMap = domObj.getInfo(context, objectSelect, multiList);
			String attributeParentRefURI = (String) parentDetailsMap.get(PGFISWSConstants.SELECT_ATTRIBUTE_REFERENCE_URI);
			String attributeParentRefIdentifier = (String) parentDetailsMap.get(PGFISWSConstants.SELECT_ATTRIBUTE_REFERENCE_IDENTIFIER);
			
			if (UIUtil.isNullOrEmpty(attributeParentRefIdentifier) || validateFormulaForImport(parentDetailsMap, attributeParentRefURI)) {
				String parentType = (String) parentDetailsMap.get(DomainConstants.SELECT_TYPE);
				StringList busSelects = new StringList();
				StringList slRelSelect = new StringList();
				busSelects.add(PGFISWSConstants.SELECT_ATTRIBUTE_REFERENCE_URI);
				busSelects.add(DomainConstants.SELECT_TYPE);
				busSelects.add(DomainConstants.SELECT_PHYSICAL_ID);
				busSelects.add(PGFISWSConstants.SELECT_ATTRIBUTE_REFERENCE_URI_FOR_ALL_REVS);
				busSelects.add(PGFISWSConstants.SELECT_PLANNED_FOR_FROM_ID);
				busSelects.add(PGFISWSConstants.SELECT_ATTRIBUTE_REFERENCE_URI_FOR_PREV_REVS);
				busSelects.add(PGFISWSConstants.SELECT_ATTRIBUTE_REFERENCE_IDENTIFIER);
				busSelects.add(DomainConstants.SELECT_REVISION);
				busSelects.add(PGFISWSConstants.CONST_ALL_REVISIONS);
	
				Pattern relPattern = new Pattern(PGFISWSConstants.REL_FBOM);
				relPattern.addPattern(PGFISWSConstants.REL_PLANNED_FOR);
				MapList mlFBOMData = domObj.getRelatedObjects(context, relPattern.getPattern(), // Relationship Pattern
						DomainConstants.QUERY_WILDCARD, // Type Pattern
						busSelects, // Business object select
						slRelSelect, // Relationship Select
						false, // get To side objects
						true, // get From side objects
						(short) 0, // Recurse to level
						DomainConstants.EMPTY_STRING, // Object Where clause
						DomainConstants.EMPTY_STRING, // Relationship Where clause
						0); // The max number of Objects to get in the expand
				if (mlFBOMData != null) {
					if (!mlFBOMData.isEmpty()) {
						java.util.Collections.sort(mlFBOMData, new Comparator<Map<String, String>>() {
							public int compare(final Map<String, String> o1, final Map<String, String> o2) {
								int value1 = 0;
								int value2 = 0;
								try {
									value1 = Integer.parseInt(o1.get("level"));
								} catch (Exception ex) {
									value1 = 0;
								}
								try {
									value2 = Integer.parseInt(o2.get("level"));
								} catch (Exception ex) {
									value2 = 0;
								}
								
								return value2 - value1;
							}
						});
					}
	
					if (!mlFBOMData.isEmpty()) {
						Iterator iterator = mlFBOMData.iterator();
						while (iterator.hasNext()) {
							JsonObjectBuilder jsonObject = Json.createObjectBuilder();
							Map dataMap = (Map) iterator.next();
							String strType = (String) dataMap.get(DomainConstants.SELECT_TYPE);
							String strPysicalID = (String) dataMap.get(DomainConstants.SELECT_PHYSICAL_ID);
							DomainObject domObject = DomainObject.newInstance(context, strPysicalID);
							String refrenceIdentifier = (String) dataMap.get(PGFISWSConstants.SELECT_ATTRIBUTE_REFERENCE_IDENTIFIER);
							String level = (String) dataMap.get("level");
							if (!(strListToCheckDuplicates.contains(strPysicalID)) && (PGFISWSConstants.TYPE_FORMULATION_PROCESS.equals(strType) || domObject.isKindOf(context, PGFISWSConstants.TYPE_RAW_MATERIAL))) {
								if (PGFISWSConstants.TYPE_FORMULATION_PROCESS.equals(strType)) {
									Object processPartId =  dataMap.get(PGFISWSConstants.SELECT_PLANNED_FOR_FROM_ID);
									String processURI = (String) dataMap.get(PGFISWSConstants.SELECT_ATTRIBUTE_REFERENCE_URI);
									if (processPartId != null && !processPartId.toString().equals("")) {
										if(UIUtil.isNullOrEmpty(refrenceIdentifier) || validateFormulaForImport(dataMap, processURI)) {
											jsonObject.add(PGFISWSConstants.OBJECT_PHYSICAL_ID, strPysicalID);
											jsonObject.add(PGFISWSConstants.OBJECT_TYPE, strType);
											jsonObject.add(PGFISWSConstants.OBJECT_LEVEL, level);
											jsonArr.add(jsonObject);
											strListToCheckDuplicates.add(strPysicalID);
										}
									} else {
										jsonObject.add(PGFISWSConstants.OBJECT_PHYSICAL_ID, PGFISWSConstants.STRING_EMPTY);
										jsonObject.add(PGFISWSConstants.OBJECT_TYPE, strType);
										jsonObject.add(PGFISWSConstants.STRING_MESSAGE, PGFISWSConstants.BAD_DATA_MSG);
										jsonObject.add(PGFISWSConstants.OBJECT_LEVEL, level);
										jsonArr.add(jsonObject);
										strListToCheckDuplicates.add(strPysicalID);
									}
	
								} else if(UIUtil.isNullOrEmpty(refrenceIdentifier)){
									jsonObject.add(PGFISWSConstants.OBJECT_PHYSICAL_ID, strPysicalID);
									jsonObject.add(PGFISWSConstants.OBJECT_TYPE, strType);
									jsonObject.add(PGFISWSConstants.OBJECT_LEVEL, level);
									jsonArr.add(jsonObject);
									strListToCheckDuplicates.add(strPysicalID);
									isRawMaterialPresentInFBOM = true;
								}
	
							}
						}
					}
				}
				JsonObjectBuilder jsonParentObject = Json.createObjectBuilder();
				jsonParentObject.add(PGFISWSConstants.OBJECT_PHYSICAL_ID, physicalId);
				jsonParentObject.add(PGFISWSConstants.OBJECT_TYPE, parentType);
				jsonParentObject.add(PGFISWSConstants.OBJECT_LEVEL, "0");
				jsonArr.add(jsonParentObject);
			}
			output.add("DATA", jsonArr.build());
			String loadWithBoomi = PGFISIntegrationUtil.getPageProperty(context, "pgFIS.properties","pgFIS.LoadFormulaToCloud.With.Boomi");
			if (isRawMaterialPresentInFBOM) {
				String formulaName = (String) parentDetailsMap.get(DomainConstants.SELECT_NAME);
				String message = PGFISWSConstants.RM_NOT_FOUND_IN_FIS_MSG;
				message = message.replace("<<Formula_Name>>", formulaName);
				output.add("Message",message);
				return Response.status(Status.BAD_REQUEST).entity(output.build().toString()).build();

			} else if (UIUtil.isNotNullAndNotEmpty(loadWithBoomi) && loadWithBoomi.equals("TRUE")) {
				generateFormulaEventMessageForFIS(context, output.build().toString());
				output.add("Message", PGFISWSConstants.EVENT_GENERATION_SUCCESS_MSG);
			} else {
				String message = processFormulasToFIS(context, output.build().toString());
				output.add("Message", message);
				if(UIUtil.isNotNullAndNotEmpty(message)) {
					if(message.indexOf("FAILED") > -1) {
						return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(output.build().toString()).build();
					}
					String []sArrFormUUID = message.split("\\|");
					if(null != sArrFormUUID && sArrFormUUID.length > 1) {
						output.add("fisformulauri", sArrFormUUID[1]);
					}
				}
			}
		} catch (Exception ex) {
			JsonObjectBuilder errorOutput = Json.createObjectBuilder();
			logger.log(Level.SEVERE, ERROR_Logger, ex);
			errorOutput.add(PGFISWSConstants.JSON_OUTPUT_KEY_ERROR, ex.getMessage());
			errorOutput.add(PGFISWSConstants.JSON_OUTPUT_KEY_TRACE, PGFISIntegrationUtil.getExceptionTrace(ex));
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorOutput.build().toString())
					.build();
		}
		return Response.status(Status.OK).entity(output.build().toString()).build();
	}

	private static boolean validateFormulaForImport(Map<String, String> objectDetailsMap, String strRefURI) {
		if(objectDetailsMap != null && UIUtil.isNotNullAndNotEmpty(strRefURI)) {
			Object revisionsObject = objectDetailsMap.get(PGFISWSConstants.CONST_ALL_REVISIONS);
			if(revisionsObject instanceof StringList) {
				StringList revisionsList = (StringList)revisionsObject;
				String currentRevsion = (String) objectDetailsMap.get(DomainConstants.SELECT_REVISION);
				int index = revisionsList.indexOf(currentRevsion);
				for(int i=index; i< revisionsList.size(); i++ ){
					String key = PGFISWSConstants.CONST_ALL_REVISIONS+"["+revisionsList.get(i)+"]."+PGFISWSConstants.SELECT_ATTRIBUTE_REFERENCE_URI;
					objectDetailsMap.remove(key);
				}
			} else {
				//As it is object of type String, It is a object with Single revision and as it is having reference URI updated means this is already imported
				return false;
			}
			List<?> values = new ArrayList<>(objectDetailsMap.values());
            int count = Collections.frequency(values, strRefURI);
            if(count > 1) {
                return true;
            }
		}
		return false;
	}

	public static String processFormulasToFIS(Context context, String formulaJSON) throws Exception {
		String returnMessage = "";
		PGFISWSConstants.loadDataFromProperties(context, "pgFIS.properties");
		if(UIUtil.isNotNullAndNotEmpty(formulaJSON)) {
			JSONObject formulaJSONObj = new JSONObject(formulaJSON);
			try {
				String formulaPhysicalID = "";
				String onPremResponse = "";
				if(null != formulaJSONObj && formulaJSONObj.has("DATA")) {
					JSONArray formulas = formulaJSONObj.getJSONArray("DATA");
					if(null != formulas) {
						PGFISCommonUtil commonUtil = new PGFISCommonUtil();
						PGFISCASAuthenticator casAuthenticator = commonUtil.getCasAuthenticatorForOnPrem();
						
						for(int i=0; i<formulas.length(); i++) {
							JSONObject formula = formulas.getJSONObject(i);
							if(null != formula && formula.has(PGFISWSConstants.OBJECT_PHYSICAL_ID)) {
								formulaPhysicalID = formula.getString(PGFISWSConstants.OBJECT_PHYSICAL_ID);
								onPremResponse = commonUtil.callOnPremAPIToGetJSON(casAuthenticator,formulaPhysicalID, PGFISWSConstants.TYPE_FORMULATION_PROCESS);
								onPremResponse = checkAndUpdatePredecessor(context, formulaPhysicalID, onPremResponse);
								logger.info("onPremResponse -- " + onPremResponse);
								if(null != onPremResponse) {
									PGFISRecipeCreator recipeCreator = new PGFISRecipeCreator();
									returnMessage = recipeCreator.loadOnPremFormulas(onPremResponse, formulaPhysicalID, casAuthenticator);
								} else {
									returnMessage = PGFISWSConstants.FORMULA_EXT_FAILED_FROM_ONPREM;
								}
								if(UIUtil.isNotNullAndNotEmpty(returnMessage) && returnMessage.indexOf("FAILED") > -1) {
									break;
								}
							}
						}
					}
				}
			} catch(Exception ex) {
				returnMessage = "FAILED: Formula Load to FIS Failed - " + ex.getMessage();
				throw new Exception("Exception Message: " + ex.getMessage());
			}
		}
		return returnMessage;
		
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String checkAndUpdatePredecessor(Context context, String strContextFormulationProcessPhysicalid, String formulaJSON)  throws Exception {
		String transformedJSON = formulaJSON;
		MapList mlReturnList = new MapList();
		Map returnMap = new HashMap();
		StringList slBusSelect = new StringList();
		slBusSelect.add(PGFISWSConstants.CONSTANT_REL_FROM_COSMETICFORMULATION_TO_FORMUTATIONPROCESS.concat(PGFISWSConstants.SELECT_ATTRIBUTE_REFERENCE_URI));
		slBusSelect.add(PGFISWSConstants.CONSTANT_REL_FROM_COSMETICFORMULATION_TO_FORMUTATIONPROCESS.concat(DomainConstants.SELECT_PHYSICAL_ID));
		slBusSelect.add(PGFISWSConstants.CONSTANT_REL_FROM_COSMETICFORMULATION_TO_FORMUTATIONPROCESS.concat(DomainConstants.SELECT_NAME));
		slBusSelect.add(PGFISWSConstants.CONSTANT_REL_FROM_COSMETICFORMULATION_TO_FORMUTATIONPROCESS+DomainConstants.SELECT_CURRENT);
		slBusSelect.add(PGFISWSConstants.CONSTANT_REL_FROM_COSMETICFORMULATION_TO_FORMUTATIONPROCESS+PGFISWSConstants.SELECT_ATTRIBUTE_RELEASEPHASE);

		StringList slFormBusSelect = new StringList();
		slFormBusSelect.add(DomainConstants.SELECT_PHYSICAL_ID);
		slFormBusSelect.add(DomainConstants.SELECT_NAME);
		
		
		StringList slFormProcessBusSelect = new StringList();
		slFormProcessBusSelect.add(DomainConstants.SELECT_PHYSICAL_ID);
		slFormProcessBusSelect.add(PGFISWSConstants.SELECT_ATTRIBUTE_REFERENCE_URI);
		slFormProcessBusSelect.add(DomainConstants.SELECT_NAME);
		slFormProcessBusSelect.add(DomainConstants.SELECT_REVISION);
		slFormProcessBusSelect.add(DomainConstants.SELECT_MODIFIED);
		slFormProcessBusSelect.add(DomainConstants.SELECT_CURRENT);
		slFormProcessBusSelect.add(PGFISWSConstants.SELECT_ATTRIBUTE_RELEASEPHASE);
		slFormProcessBusSelect.add(DomainConstants.SELECT_IS_LAST);
		StringBuffer sbWhereClause = new StringBuffer();
		sbWhereClause.append(" ( ");
		sbWhereClause.append(DomainConstants.SELECT_CURRENT).append("=='").append(PGFISWSConstants.STATE_COMPLETE).append("'");
		sbWhereClause.append(" || ");
		sbWhereClause.append(DomainConstants.SELECT_CURRENT).append("=='").append(PGFISWSConstants.STATE_RELEASE).append("'");
		sbWhereClause.append(" ) ");
		//sbWhereClause.append(" && ").append("revision==last.revision");
		
		
		StringBuffer sbWhereClauseFormulationPart = new StringBuffer();
		sbWhereClauseFormulationPart.append(" ( ");
		sbWhereClauseFormulationPart.append(DomainConstants.SELECT_CURRENT).append("=='").append(PGFISWSConstants.STATE_COMPLETE).append("'");
		sbWhereClauseFormulationPart.append(" || ");
		sbWhereClauseFormulationPart.append(DomainConstants.SELECT_CURRENT).append("=='").append(PGFISWSConstants.STATE_RELEASE).append("'");
		sbWhereClauseFormulationPart.append(" ) ");
		sbWhereClauseFormulationPart.append(" && ").append("revision==last.revision");
		
		StringList slSelectFormulationProcessDetails = new StringList();
		slSelectFormulationProcessDetails.add(PGFISWSConstants.CONST_FROM_REL_PLANNEDFOR_TO.concat(DomainConstants.SELECT_PHYSICAL_ID));
		slSelectFormulationProcessDetails.add(PGFISWSConstants.CONST_FROM_REL_PLANNEDFOR_TO.concat(DomainConstants.SELECT_CURRENT));
		slSelectFormulationProcessDetails.add(PGFISWSConstants.CONST_FROM_REL_PLANNEDFOR_TO.concat(DomainConstants.SELECT_IS_LAST));
		slSelectFormulationProcessDetails.add(PGFISWSConstants.CONST_FROM_REL_PLANNEDFOR_TO.concat(DomainConstants.SELECT_NAME));
		slSelectFormulationProcessDetails.add(PGFISWSConstants.CONST_FROM_REL_PLANNEDFOR_TO.concat(DomainConstants.SELECT_MODIFIED));
		slSelectFormulationProcessDetails.add(PGFISWSConstants.CONST_FROM_REL_PLANNEDFOR_TO.concat(PGFISWSConstants.SELECT_ATTRIBUTE_REFERENCE_URI));
		slSelectFormulationProcessDetails.add(PGFISWSConstants.CONST_FROM_REL_PLANNEDFOR_TO.concat(PGFISWSConstants.SELECT_ATTRIBUTE_RELEASEPHASE));
		
		StringBuffer sbWhereClauseForFormulationProcess = new StringBuffer();
		sbWhereClauseForFormulationProcess.append(" ( ");
		sbWhereClauseForFormulationProcess.append(DomainConstants.SELECT_CURRENT).append("=='").append(PGFISWSConstants.STATE_COMPLETE).append("'");
		sbWhereClauseForFormulationProcess.append(" || ");
		sbWhereClauseForFormulationProcess.append(DomainConstants.SELECT_CURRENT).append("=='").append(PGFISWSConstants.STATE_RELEASE).append("'");
		sbWhereClauseForFormulationProcess.append(" || ");
		sbWhereClauseForFormulationProcess.append(DomainConstants.SELECT_CURRENT).append("=='").append(PGFISWSConstants.STATE_OBSOLETE).append("'");
		sbWhereClauseForFormulationProcess.append(" ) ");
		
		
		try {
			if(UIUtil.isNotNullAndNotEmpty(strContextFormulationProcessPhysicalid)) {
				DomainObject domFormulationProcessObj = DomainObject.newInstance(context, strContextFormulationProcessPhysicalid);
				//Step 1: Get the Cosmetic Formulation from Context Formulation Process
				MapList mlFormulationData = domFormulationProcessObj.getRelatedObjects(context, 
						PGFISWSConstants.RELATIONSHIP_FORMULATION_PROCESS, // Relationship
					DomainConstants.QUERY_WILDCARD, // Type
					slFormBusSelect, // Business object select
					null, // Relationship Select
					false, // get To side objects
					true, // get From side objects
					(short) 0, // Recurse to level
					DomainConstants.EMPTY_STRING,//sbWhereClause.toString(), // Object Where clause
					DomainConstants.EMPTY_STRING, // Relationship Where clause
					0); // The max number of Objects to get in the expand
				
				
				Map mFormulationData = null;
				String strFormulationPhyId = DomainConstants.EMPTY_STRING;
				String strFormulationName = DomainConstants.EMPTY_STRING;
				MapList mlCosmeticFormualtionChain = new MapList();
				String strCosmeticFormulationPhyId = DomainConstants.EMPTY_STRING;
				Map mCosmeticFormulationData = null;
				DomainObject doCosmeticFormulationObj = null;
				DomainObject doFormulationProcessObj = null;
				MapList mlConnectedFormulaProcessList = new MapList();
				Map mConnectedFormulaProcess = null;
				String sFormulaProcessPhyID = DomainConstants.EMPTY_STRING;
				String sFormulaProcessIsLast = DomainConstants.EMPTY_STRING;
				String sFormulaProcessRefURI = DomainConstants.EMPTY_STRING;
				MapList mlConnectedFormulationPartList = new MapList();
				Map mConnectedFormulationParts = null;
				if(null != mlFormulationData && !mlFormulationData.isEmpty()) {
					Iterator iteratorFormulation = mlFormulationData.iterator();
					while (iteratorFormulation.hasNext()) {
						mFormulationData = (Map) iteratorFormulation.next();
						if(null != mFormulationData) {
							strFormulationPhyId = (String) mFormulationData.get(DomainConstants.SELECT_PHYSICAL_ID);
							strFormulationName = (String) mFormulationData.get(DomainConstants.SELECT_NAME);
							if(UIUtil.isNotNullAndNotEmpty(strFormulationName)) {
								//Step 2: Get the Latest Release or Complete Cosmetic Formulation
								mlCosmeticFormualtionChain = DomainObject.findObjects(context, PGFISWSConstants.TYPE_COSMETIC_FORMULATION, strFormulationName, DomainConstants.QUERY_WILDCARD, DomainConstants.QUERY_WILDCARD, DomainConstants.QUERY_WILDCARD, sbWhereClauseFormulationPart.toString(), false, slFormBusSelect);
								if(null != mlCosmeticFormualtionChain && !mlCosmeticFormualtionChain.isEmpty()) {
									Iterator iteratorCosmeticFormulation = mlCosmeticFormualtionChain.iterator();
									while (iteratorCosmeticFormulation.hasNext()) {
										mCosmeticFormulationData = (Map) iteratorCosmeticFormulation.next();
										if(null != mCosmeticFormulationData) {
											strCosmeticFormulationPhyId = (String) mCosmeticFormulationData.get(DomainConstants.SELECT_PHYSICAL_ID);
											if(UIUtil.isNotNullAndNotEmpty(strCosmeticFormulationPhyId)) {
												doCosmeticFormulationObj = DomainObject.newInstance(context, strCosmeticFormulationPhyId);
												if(null != doCosmeticFormulationObj) {
													//Step 2: Get the Latest Release or Complete Connected Formulation Parts
													mlConnectedFormulationPartList = doCosmeticFormulationObj.getRelatedObjects(context, 
															PGFISWSConstants.RELATIONSHIP_FORMULATION_PROPAGATE, // Relationship
															DomainConstants.QUERY_WILDCARD, // Type
															slFormBusSelect, // Business object select
															null, // Relationship Select
															false, // get To side objects
															true, // get From side objects
															(short) 0, // Recurse to level
															sbWhereClauseFormulationPart.toString(), // Object Where clause
															DomainConstants.EMPTY_STRING, // Relationship Where clause
															0); // The max number of Objects to get in the expand
													if(null != mlConnectedFormulationPartList && !mlConnectedFormulationPartList.isEmpty()) {
														Iterator iteratorFormulaParts = mlConnectedFormulationPartList.iterator();
														while(iteratorFormulaParts.hasNext()) {
															mConnectedFormulationParts = (Map) iteratorFormulaParts.next();
															if(null != mConnectedFormulationParts) {
																sFormulaProcessPhyID = (String) mConnectedFormulationParts.get(DomainConstants.SELECT_PHYSICAL_ID);
																if(UIUtil.isNotNullAndNotEmpty(sFormulaProcessPhyID)) {
																	doFormulationProcessObj = DomainObject.newInstance(context, sFormulaProcessPhyID);
																	
																	mlConnectedFormulaProcessList = doFormulationProcessObj.getRelatedObjects(context, 
																			PGFISWSConstants.REL_PLANNED_FOR, // Relationship
																			DomainConstants.QUERY_WILDCARD, // Type
																			slFormProcessBusSelect, // Business object select
																			null, // Relationship Select
																			false, // get To side objects
																			true, // get From side objects
																			(short) 0, // Recurse to level
																			sbWhereClauseForFormulationProcess.toString(), // Object Where clause
																			DomainConstants.EMPTY_STRING, // Relationship Where clause
																			0); // The max number of Objects to get in the expand
																	
																	if(null != mlConnectedFormulaProcessList && !mlConnectedFormulaProcessList.isEmpty()) {
																		Iterator iteratorFormulaProcess = mlConnectedFormulaProcessList.iterator();
																		while(iteratorFormulaProcess.hasNext()) {
																			mConnectedFormulaProcess = (Map) iteratorFormulaProcess.next();
																			if(null != mConnectedFormulaProcess) {
																				sFormulaProcessIsLast = (String) mConnectedFormulaProcess.get(DomainConstants.SELECT_IS_LAST);
																				sFormulaProcessRefURI = (String) mConnectedFormulaProcess.get(PGFISWSConstants.SELECT_ATTRIBUTE_REFERENCE_URI);
																				if(UIUtil.isNotNullAndNotEmpty(sFormulaProcessIsLast) && PGFISWSConstants.CONST_TRUE.equalsIgnoreCase(sFormulaProcessIsLast) 
																						&&  UIUtil.isNotNullAndNotEmpty(sFormulaProcessRefURI) )  {
																					mlReturnList.add(mConnectedFormulaProcess);
																				}
																			}
																		}
																	}
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
				returnMap.put("FORMULATION_ID", strFormulationPhyId);
				returnMap.put("FORMULATION_NAME", strFormulationName);
				returnMap.put("FORMULATION_PROCESS_LIST", mlReturnList);
			}
		} catch (Exception ex) {
			logger.info("ERROR While getting Formulation Process Chain" + ex.getMessage());
		}
		
		if(null != returnMap && !returnMap.isEmpty()) {
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
			MapList mlFinalFormulationList = returnMap.containsKey("FORMULATION_PROCESS_LIST") ? (MapList) returnMap.get("FORMULATION_PROCESS_LIST") : new MapList();
			if(null != mlFinalFormulationList && !mlFinalFormulationList.isEmpty()) {
				Collections.sort(mlFinalFormulationList, new Comparator<Map<String, String>>() {
					public int compare(final Map<String, String> map1, final Map<String, String> map2) {
						int i=0;
						try {
							i = (sdf.parse((String)map1.get(DomainConstants.SELECT_MODIFIED))).compareTo(sdf.parse((String)map2.get(DomainConstants.SELECT_MODIFIED)));
						}
						catch(Exception e) {
							logger.info("Error - " + e.getMessage());
						}
						return i;
					}
				});
				int size = mlFinalFormulationList.size();
				Map mPredecessorDetails = (Map) mlFinalFormulationList.get(size-1);
				if(null != mPredecessorDetails) {
					String strRefURI = (String) mPredecessorDetails.get(PGFISWSConstants.SELECT_ATTRIBUTE_REFERENCE_URI);
					if(UIUtil.isNotNullAndNotEmpty(formulaJSON) && UIUtil.isNotNullAndNotEmpty(strRefURI) ) {
						JsonObject formulaJSONObj = JsonParser.parseString(formulaJSON).getAsJsonObject();
						if(null != formulaJSONObj && formulaJSONObj.has("recipe")) {
							JsonObject jsonReceipe = formulaJSONObj.get("recipe").getAsJsonObject();
							if (jsonReceipe.has(PGFISWSConstants.JSON_TAG_PREDECESSOR)) {
								jsonReceipe.remove(PGFISWSConstants.JSON_TAG_PREDECESSOR);
							}
							JsonObject jsonPredecessorUUID = new JsonObject();
							jsonPredecessorUUID.addProperty(PGFISWSConstants.JSON_TAG_ID, strRefURI);
							jsonReceipe.add(PGFISWSConstants.JSON_TAG_PREDECESSOR,jsonPredecessorUUID);
							transformedJSON =  formulaJSONObj.toString();
						}
						
					}
				}
			}
		}
		
		
		return transformedJSON;
		
	}
	
	@SuppressWarnings("unchecked")
	public static String checkAndUpdatePredecessor2(Context context, String sFormulaProcessID, String formulaJSON) throws Exception {
		String transformedJSON = formulaJSON;
		try {
			
			StringList slFormProcessBusSelect = new StringList();
			slFormProcessBusSelect.add(DomainConstants.SELECT_PHYSICAL_ID);
			slFormProcessBusSelect.add(PGFISWSConstants.SELECT_ATTRIBUTE_REFERENCE_URI);
			slFormProcessBusSelect.add(DomainConstants.SELECT_NAME);
			slFormProcessBusSelect.add(DomainConstants.SELECT_MODIFIED);
			slFormProcessBusSelect.add(DomainConstants.SELECT_CURRENT);
			slFormProcessBusSelect.add(PGFISWSConstants.SELECT_ATTRIBUTE_RELEASEPHASE);
			
			StringList slFormBusSelect = new StringList();
			slFormBusSelect.add(DomainConstants.SELECT_PHYSICAL_ID);
			
			StringBuffer sbWhereClauseForFormulationProcess = new StringBuffer();
			sbWhereClauseForFormulationProcess.append(PGFISWSConstants.SELECT_ATTRIBUTE_REFERENCE_URI).append("!=").append("''");
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
			String strRefURI = DomainConstants.EMPTY_STRING;
			if(UIUtil.isNotNullAndNotEmpty(sFormulaProcessID)) {
				DomainObject doFormProcessObj = DomainObject.newInstance(context, sFormulaProcessID);
				
				
				if(null != doFormProcessObj && doFormProcessObj.exists(context)) {
					MapList mlFormulationData = doFormProcessObj.getRelatedObjects(context, 
							PGFISWSConstants.RELATIONSHIP_FORMULATION_PROCESS, // Relationship
							DomainConstants.QUERY_WILDCARD, // Type
							slFormBusSelect, // Business object select
							null, // Relationship Select
							false, // get To side objects
							true, // get From side objects
							(short) 0, // Recurse to level
							DomainConstants.EMPTY_STRING, // Object Where clause
							DomainConstants.EMPTY_STRING, // Relationship Where clause
							0); // The max number of Objects to get in the expand
					if(null != mlFormulationData && !mlFormulationData.isEmpty()) {
						logger.info("mlFormulationData -- " + mlFormulationData);
						Iterator iteratorFormulation = mlFormulationData.iterator();
						while (iteratorFormulation.hasNext()) {
							strRefURI = DomainConstants.EMPTY_STRING;
							Map mFormulationData = (Map) iteratorFormulation.next();
							String strFormulationPhyId = (String) mFormulationData.get(DomainConstants.SELECT_PHYSICAL_ID);
							DomainObject domFormulationObj = DomainObject.newInstance(context, strFormulationPhyId);
							MapList mlFormulationProcessData = domFormulationObj.getRelatedObjects(context, 
									PGFISWSConstants.RELATIONSHIP_FORMULATION_PROCESS, // Relationship
									DomainConstants.QUERY_WILDCARD, // Type
									slFormProcessBusSelect, // Business object select
									null, // Relationship Select
									true, // get To side objects
									false, // get From side objects
									(short) 0, // Recurse to level
									sbWhereClauseForFormulationProcess.toString(), // Object Where clause
									DomainConstants.EMPTY_STRING, // Relationship Where clause
									0); // The max number of Objects to get in the expand
							if (null!=mlFormulationProcessData && !mlFormulationProcessData.isEmpty()) {
								
								Collections.sort(mlFormulationProcessData, new Comparator<Map<String, String>>() {
									public int compare(final Map<String, String> map1, final Map<String, String> map2) {
										int i=0;
										try {
											i = (sdf.parse((String)map1.get(DomainConstants.SELECT_MODIFIED))).compareTo(sdf.parse((String)map2.get(DomainConstants.SELECT_MODIFIED)));
										}
										catch(Exception e) {
											logger.info("Error - " + e.getMessage());
										}
										return i;
									}
								});
								logger.info("mlFormulationProcessData -- " + mlFormulationProcessData);
								Iterator iteratorFormulationProcess = mlFormulationProcessData.iterator();
								while (iteratorFormulationProcess.hasNext()) {
									Map mFormulationProcess = (Map) iteratorFormulationProcess.next();
									strRefURI = (String) mFormulationProcess.get(PGFISWSConstants.SELECT_ATTRIBUTE_REFERENCE_URI);
								}
							}
						}
					}
					logger.info("strRefURI -- " + strRefURI);
					if(UIUtil.isNotNullAndNotEmpty(formulaJSON) && UIUtil.isNotNullAndNotEmpty(strRefURI) ) {
						JsonObject formulaJSONObj = JsonParser.parseString(formulaJSON).getAsJsonObject();
						if(null != formulaJSONObj && formulaJSONObj.has("recipe")) {
							JsonObject jsonReceipe = formulaJSONObj.get("recipe").getAsJsonObject();
							if (jsonReceipe.has(PGFISWSConstants.JSON_TAG_PREDECESSOR)) {
								jsonReceipe.remove(PGFISWSConstants.JSON_TAG_PREDECESSOR);
							}
							JsonObject jsonPredecessorUUID = new JsonObject();
							jsonPredecessorUUID.addProperty(PGFISWSConstants.JSON_TAG_ID, strRefURI);
							jsonReceipe.add(PGFISWSConstants.JSON_TAG_PREDECESSOR,jsonPredecessorUUID);
							transformedJSON =  formulaJSONObj.toString();
						}
							
					}
				}
			}
		} catch(Exception ex) {
			throw new Exception("Exception Message: " + ex.getMessage());
		}
		
		return transformedJSON;
		
	}
	
	private static void generateFormulaEventMessageForFIS(Context context, String JsonData) throws Exception {
		JSONObject jsonObject = new JSONObject(JsonData);
		try {
			if (jsonObject.has("DATA")) {
				JSONArray dataArray = jsonObject.getJSONArray("DATA");
				for (int i = 0; i < dataArray.length(); i++) {
					JSONObject dataJsonObject = dataArray.getJSONObject(i);
					if (dataJsonObject.has(PGFISWSConstants.OBJECT_PHYSICAL_ID) && dataJsonObject.has(PGFISWSConstants.OBJECT_TYPE)) {
						String physicalId = dataJsonObject.getString(PGFISWSConstants.OBJECT_PHYSICAL_ID);
						DomainObject domObj = DomainObject.newInstance(context, physicalId);
						String owner = (String) domObj.getInfo(context, DomainConstants.SELECT_OWNER);
						String objectType = dataJsonObject.getString(PGFISWSConstants.OBJECT_TYPE);
						if (PGFISWSConstants.TYPE_FORMULATION_PROCESS.equals(objectType)) {
							String[] inputParam = new String[5];
							inputParam[0] = "formulaSyncQueue";
							inputParam[1] = physicalId;
							inputParam[2] = objectType;
							inputParam[3] = owner;
							JPO.invoke(context, "bioFormEventGenerationDeferred", null, "generateFormulaEventMessage",
									inputParam, void.class);
						}
						try {
							TimeUnit.SECONDS.sleep(3);
						} catch (InterruptedException ie) {
							Thread.currentThread().interrupt();
						}
					}
				}
			}
		} catch (Exception ex) {
			Thread.currentThread().interrupt();
		}
	}
	
	
	/**
	 * method to get crossreference details For FIS Integration
	 * 
	 * @param Context Context used to call API
	 * @param physicalId - physicalId of context object
	 * @return response in JSON String format
	 * @throws Exception when operation fails
	 */
	public static Response getCrossReferenceResponse(Context context, String physicalId) throws Exception {
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		boolean isContextPushed = false;
		try {
			
			if(UIUtil.isNotNullAndNotEmpty(physicalId) && "invalidsecurecollections".equalsIgnoreCase(physicalId)) {
				jsonReturnObj.add(PGFISWSConstants.IMPORTED_IDENTIFIER_KEY, PGFISWSConstants.STR_INVALID_SECURE_COLLECTION);
				jsonReturnObj.add(PGFISWSConstants.ENTERPRISE_ID_KEY, PGFISWSConstants.STR_INVALID_SECURE_COLLECTION);
				
			} else {
				//Pushing content to fetch the required details from context object, as context user sometime not having read and show access.
				ContextUtil.pushContext(context);
				isContextPushed = true;
				DomainObject domObj = DomainObject.newInstance(context, physicalId);
				if(domObj != null && domObj.isKindOf(context, PGFISWSConstants.TYPE_FORMULATION_PROCESS)) {
					StringList slSelectable = new StringList();
					slSelectable.add(DomainConstants.SELECT_NAME);
					slSelectable.add(DomainConstants.SELECT_REVISION);
					Map objectMap = domObj.getInfo(context, slSelectable);
					if (objectMap != null && !objectMap.isEmpty()) {
						String processName = (String) objectMap.get(DomainConstants.SELECT_NAME);
						String processRevision = (String) objectMap.get(DomainConstants.SELECT_REVISION);
						
						if(UIUtil.isNotNullAndNotEmpty(processName) && UIUtil.isNotNullAndNotEmpty(processRevision)) {
							String importedidentifier = processName +" ("+processRevision+")";
							jsonReturnObj.add(PGFISWSConstants.IMPORTED_IDENTIFIER_KEY, importedidentifier);
						}
						
						/*
						 * if(UIUtil.isNotNullAndNotEmpty(processName)) { processName =
						 * processName.split("-")[0];
						 * jsonReturnObj.add(PGFISWSConstants.ENTERPRISE_ID_KEY, processName); }
						 */
					}
				} else {
					String errorMessage = PGFISWSConstants.FORM_PROCESS_API_SUPPORT_MSG;
					jsonReturnObj.add(PGFISWSConstants.JSON_OUTPUT_KEY_ERROR, errorMessage);
					return Response.status(Response.Status.BAD_REQUEST).entity(jsonReturnObj.build().toString())
							.build();
				}
			}
		} catch (Exception ex) {
			JsonObjectBuilder errorOutput = Json.createObjectBuilder();
			logger.log(Level.SEVERE, ERROR_Logger, ex);
			errorOutput.add(PGFISWSConstants.JSON_OUTPUT_KEY_ERROR, ex.getMessage());
			errorOutput.add(PGFISWSConstants.JSON_OUTPUT_KEY_TRACE, PGFISIntegrationUtil.getExceptionTrace(ex));
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorOutput.build().toString())
					.build();
		} finally {
			if(isContextPushed) {
				ContextUtil.popContext(context);
			}
		}
		return Response.status(Status.OK).entity(jsonReturnObj.build().toString()).build();
	}
	
	
	/**
	 * method to get Formulated Material details For FIS Integration
	 * 
	 * @param Context Context used to call API
	 * @param physicalId - physicalId of context object
	 * @return response in JSON String format
	 * @throws Exception when operation fails
	 */
	public static Response getFormulatedMaterialDetailsResponse(Context context, String physicalId) throws Exception {
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		boolean isContextPushed = false;
		try {
			//Pushing content to fetch the required details from context object, as context user sometime not having read and show access.
			ContextUtil.pushContext(context);
			isContextPushed = true;
			DomainObject domObj = DomainObject.newInstance(context, physicalId);
			if (domObj != null && domObj.isKindOf(context, PGFISWSConstants.TYPE_FORMULATION_PROCESS)) {
				String formulatedMatUri = domObj.getInfo(context, PGFISWSConstants.SELECT_ATTRIBUTE_REFERENCE_FORMULATED_MATERIAL_URI);
				if(UIUtil.isNotNullAndNotEmpty(formulatedMatUri)) {
					jsonReturnObj.add(PGFISWSConstants.JSON_TAG_ID, formulatedMatUri);
				} else {
					String errorMessage = PGFISWSConstants.FORMULATED_MAT_DETAILS_MISSING_MSG+ physicalId;
					jsonReturnObj.add(PGFISWSConstants.JSON_OUTPUT_KEY_ERROR, errorMessage);
					return Response.status(Response.Status.BAD_REQUEST).entity(jsonReturnObj.build().toString())
							.build();
				}	
			} else {
				String errorMessage = PGFISWSConstants.FORM_PROCESS_API_SUPPORT_MSG;
				jsonReturnObj.add(PGFISWSConstants.JSON_OUTPUT_KEY_ERROR, errorMessage);
				return Response.status(Response.Status.BAD_REQUEST).entity(jsonReturnObj.build().toString())
						.build();
			}
		} catch (Exception ex) {
			JsonObjectBuilder errorOutput = Json.createObjectBuilder();
			logger.log(Level.SEVERE, ERROR_Logger, ex);
			errorOutput.add(PGFISWSConstants.JSON_OUTPUT_KEY_ERROR, ex.getMessage());
			errorOutput.add(PGFISWSConstants.JSON_OUTPUT_KEY_TRACE, PGFISIntegrationUtil.getExceptionTrace(ex));
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorOutput.build().toString())
					.build();
		} finally {
			if(isContextPushed) {
				ContextUtil.popContext(context);
			}
		}
		return Response.status(Status.OK).entity(jsonReturnObj.build().toString()).build();
	}
	
	
	/**
	 * method to get Formulated Material details For FIS Integration
	 * 
	 * @param Context Context used to call API
	 * @param physicalId - physicalId of context object
	 * @return response in JSON String format
	 * @throws Exception when operation fails
	 */
	public static Response getFormulatedMaterialReferenceResponse(Context context, String physicalId) throws Exception {
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		boolean isContextPushed = false;
		try {
			//Pushing content to fetch the required details from context object, as context user sometime not having read and show access.
			ContextUtil.pushContext(context);
			isContextPushed = true;
			DomainObject domObj = DomainObject.newInstance(context, physicalId);
			if (domObj != null && domObj.isKindOf(context, PGFISWSConstants.TYPE_FORMULATION_PROCESS)) {
				StringList selectList = new StringList();
				selectList.add(DomainConstants.SELECT_NAME);
				selectList.add(PGFISWSConstants.SELECT_ATTRIBUTE_REFERENCE_FORMULATED_MATERIAL_URI);
				Map objectInfo =  domObj.getInfo(context, selectList);
				String formulatedMatUri = (String)objectInfo.get(PGFISWSConstants.SELECT_ATTRIBUTE_REFERENCE_FORMULATED_MATERIAL_URI);
				String processName = (String)objectInfo.get(DomainConstants.SELECT_NAME);
				if(UIUtil.isNotNullAndNotEmpty(formulatedMatUri)) {
					if(UIUtil.isNotNullAndNotEmpty(processName)) {
						processName = processName.split("-")[0];
					}
					jsonReturnObj.add(PGFISWSConstants.JSON_TAG_ID, formulatedMatUri);
					JsonArrayBuilder hasIdentifierTypeArray = Json.createArrayBuilder();
					JsonObjectBuilder identifier = Json.createObjectBuilder();
					JsonObjectBuilder hasIdentifierTypeJson = Json.createObjectBuilder();
					JsonObjectBuilder subIdentifier = Json.createObjectBuilder();
					subIdentifier.add(PGFISWSConstants.JSON_TAG_ID, PGFISWSConstants.JSON_FIS_ENTERPRISE_IDENTIIFIER);
					identifier.add(PGFISWSConstants.JSON_TAG_IDENTIFIER_TYPE, subIdentifier);
					identifier.add(PGFISWSConstants.JSON_TAG_IDENTIFIER, processName);
					hasIdentifierTypeArray.add(identifier);
					hasIdentifierTypeJson.add(PGFISWSConstants.JSON_TAG_HAS_ASSIGNED_IDENTIFIER, hasIdentifierTypeArray.build());
					jsonReturnObj.add(PGFISWSConstants.JSON_TAG_BODY, hasIdentifierTypeJson);
				} else {
					String errorMessage = PGFISWSConstants.FORMULATED_MAT_DETAILS_MISSING_MSG+ physicalId;
					jsonReturnObj.add(PGFISWSConstants.JSON_OUTPUT_KEY_ERROR, errorMessage);
					return Response.status(Response.Status.BAD_REQUEST).entity(jsonReturnObj.build().toString())
							.build();
				}	
			} else {
				String errorMessage = PGFISWSConstants.FORM_PROCESS_API_SUPPORT_MSG;
				jsonReturnObj.add(PGFISWSConstants.JSON_OUTPUT_KEY_ERROR, errorMessage);
				return Response.status(Response.Status.BAD_REQUEST).entity(jsonReturnObj.build().toString())
						.build();
			}
		} catch (Exception ex) {
			JsonObjectBuilder errorOutput = Json.createObjectBuilder();
			logger.log(Level.SEVERE, ERROR_Logger, ex);
			errorOutput.add(PGFISWSConstants.JSON_OUTPUT_KEY_ERROR, ex.getMessage());
			errorOutput.add(PGFISWSConstants.JSON_OUTPUT_KEY_TRACE, PGFISIntegrationUtil.getExceptionTrace(ex));
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorOutput.build().toString())
					.build();
		} finally {
			if(isContextPushed) {
				ContextUtil.popContext(context);
			}
		}
		return Response.status(Status.OK).entity(jsonReturnObj.build().toString()).build();
	}

}

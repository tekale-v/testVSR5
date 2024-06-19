package com.pg.fis.integration.picklist.services;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.fis.integration.constants.PGFISWSConstants;
import com.pg.fis.integration.util.PGFISIntegrationUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import matrix.db.Context;
import matrix.util.StringList;

public class PGFISPickListDetails {

	private static final Logger logger = Logger.getLogger(PGFISPickListDetails.class.getName());
	static final String ERROR_Logger = "Exception in PGFISPickListDetails";

	/**
	 * REST method to get PickList Details
	 * 
	 * @param Context    - Context used to call API
	 * @param pyisicalId - Business Object physicalId
	 * @return response in JSON String format
	 * @throws Exception when operation fails
	 */
	public static Response getPickListDetailsResponse(Context context, String physicalId) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		try {
			DomainObject domObj = DomainObject.newInstance(context, physicalId);

			StringList slObjectSelect = new StringList();
			slObjectSelect.add(DomainConstants.SELECT_NAME);
			slObjectSelect.add(DomainConstants.SELECT_PHYSICAL_ID);
			slObjectSelect.add(DomainConstants.SELECT_CURRENT);
			slObjectSelect.add(DomainConstants.SELECT_TYPE);
			slObjectSelect.add(PGFISWSConstants.SELECT_ATTRIBUTE_REFERENCE_URI);

			Map dataMap = domObj.getInfo(context, slObjectSelect);
			if (dataMap != null && !dataMap.isEmpty()) {
				String sName = (String) dataMap.get(DomainConstants.SELECT_NAME);
				String sType = (String) dataMap.get(DomainConstants.SELECT_TYPE);
				String sPhysicalId = (String) dataMap.get(DomainConstants.SELECT_PHYSICAL_ID);
				String sCurrentState = (String) dataMap.get(DomainConstants.SELECT_CURRENT);
				String sCloudReferenceURI = (String) dataMap.get(PGFISWSConstants.SELECT_ATTRIBUTE_REFERENCE_URI);
				JsonArrayBuilder jsonArr = Json.createArrayBuilder();
				jsonArr.add(sName);
				if(PGFISWSConstants.TYPE_PGPLI__MATERIAL_FUNCTIONALITY.equals(sType)) {
					StringList selectables= new StringList();
					selectables.add(DomainConstants.SELECT_PHYSICAL_ID);
					MapList objDetailList = PGFISIntegrationUtil.findObjectAndRetunDetails(context,
							PGFISWSConstants.TYPE__MATERIAL_FUNCTIONALITY, sName, selectables, "");
					if (objDetailList != null && objDetailList.size() != 0 && !objDetailList.isEmpty()) {
						@SuppressWarnings("unchecked")
						Map<String, String> objectMap = (Map<String, String>) objDetailList.get(0);
						sPhysicalId = (String) objectMap.get(DomainConstants.SELECT_PHYSICAL_ID);
					}
				}
				boolean isActive = false;
				if (PGFISWSConstants.STATE_ACTIVE.equals(sCurrentState)) {
					isActive = true;
				}
				output.add(PGFISWSConstants.STRING_ISACTIVE, isActive);
				output.add(PGFISWSConstants.STRING_EXTERNAL_ID, sPhysicalId);
				output.add(PGFISWSConstants.STRING_PREF_LABEL, jsonArr.build());
				if (UIUtil.isNotNullAndNotEmpty(sCloudReferenceURI)) {
					output.add(PGFISWSConstants.STRING_REFERENCE_ID, sCloudReferenceURI);
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
	 * REST method to get PickList product classification combinations
	 * 
	 * @param Context          - Context used to call API
	 * @param childPhysicalId  - child picklist Object physicalId
	 * @param parentPhysicalId - parent picklist Object physicalId
	 * @return response in JSON String format
	 * @throws Exception when operation fails
	 */
	public static Response getPickListCombinationsResponse(Context context, String childPhysicalId,
			String parentPhysicalId) {
		JsonObjectBuilder outputJSON = Json.createObjectBuilder();
		JsonArrayBuilder productClassificationArray = Json.createArrayBuilder();
		try {
			String sValidBusinessArea = EnoviaResourceBundle.getProperty(context, "pgFIS.ProductClassificationData.BA");
			StringList slValidBAList = FrameworkUtil.split(sValidBusinessArea, PGFISWSConstants.STR_COMMA);
			ArrayList<String> productClassificationDataList = getProductClassification(context, childPhysicalId,
					parentPhysicalId);
			for (String productClassification : productClassificationDataList) {
				if(checkValidBA(productClassification, slValidBAList)) {
					JsonObjectBuilder productClassificationJSON = Json.createObjectBuilder();
					JsonArrayBuilder prefLabelArray = Json.createArrayBuilder();
					prefLabelArray.add(productClassification);
					productClassificationJSON.add(PGFISWSConstants.JSON_TAG_PREF_LABEL, prefLabelArray.build());
					productClassificationJSON.add(PGFISWSConstants.STRING_ISACTIVE, "true");
					productClassificationArray.add(productClassificationJSON);
				}
			}
			outputJSON.add(PGFISWSConstants.STRING_PRODUCT_CLASSIFICATION, productClassificationArray.build());
		} catch (Exception ex) {
			logger.log(Level.SEVERE, ERROR_Logger, ex);
			outputJSON.add(PGFISWSConstants.JSON_OUTPUT_KEY_ERROR, ex.getMessage());
			outputJSON.add(PGFISWSConstants.JSON_OUTPUT_KEY_TRACE, PGFISIntegrationUtil.getExceptionTrace(ex));
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(outputJSON.build().toString()).build();
		}
		return Response.status(Status.OK).entity(outputJSON.build().toString()).build();
	}

	public static boolean checkValidBA(String productClassification, StringList slValidBAList) throws Exception {
		if(null == productClassification || slValidBAList == null || slValidBAList.isEmpty()) {
			return false;
		}
		
		String sBAName = productClassification.split(">")[0].trim();
		if(slValidBAList.contains(sBAName)) {
			return true;
		}
		
		return false;
	}
	/**
	 * method to get PickList product classification combinations
	 * 
	 * @param Context          - Context used to call API
	 * @param childPhysicalId  - child picklist Object physicalId
	 * @param parentPhysicalId - parent picklist Object physicalId
	 * @return response - Product classification in ArrayList
	 * @throws Exception when operation fails
	 */
	private static ArrayList<String> getProductClassification(Context context, String childPhysicalId,
			String parentPhysicalId) throws Exception {
		ArrayList<String> productClassificationDataList = new ArrayList<String>();
		try {
			StringList slObjectSelects = new StringList();
			slObjectSelects.add(DomainConstants.SELECT_ID);
			slObjectSelects.add(DomainConstants.SELECT_NAME);
			String whereClauseForPCP = "";
			String whereClauseForPTP = "";
			String whereClauseForPTC = "";
			String updatedType = "";
			DomainObject childObject = DomainObject.newInstance(context, childPhysicalId);
			if (childObject.isKindOf(context, PGFISWSConstants.TYPE_PGPLIBUSINESS_AREA)) {
				Map<String, String> tempBAMap = new HashMap<>();
				String baNAME = (String) childObject.getInfo(context, DomainConstants.SELECT_NAME);
				tempBAMap.put(childPhysicalId, baNAME);
				whereClauseForPCP = "current == Active";
				whereClauseForPTP = "current == Active";
				whereClauseForPTC = "current == Active";
				updatedType = PGFISWSConstants.TYPE_PGPLIBUSINESS_AREA;
				getObjectDetailsForProductClassification(context, updatedType, whereClauseForPCP, whereClauseForPTP,
						whereClauseForPTC, tempBAMap, productClassificationDataList);

			} else if (childObject.isKindOf(context, PGFISWSConstants.TYPE_PGPLIPRODUCT_CACTEGORY_PLATFORM)) {
				String pcpNAME = "";
				Map<String, String> tempBAMap = new HashMap<>();
				if (UIUtil.isNotNullAndNotEmpty(parentPhysicalId)) {
					DomainObject parentObject = DomainObject.newInstance(context, parentPhysicalId);
					String parentObjectName = parentObject.getInfo(context, DomainConstants.SELECT_NAME);
					tempBAMap.put(parentPhysicalId, parentObjectName);
					pcpNAME = childObject.getInfo(context, DomainConstants.SELECT_NAME);
				} else {
					StringList slObjectSelects1 = new StringList();
					slObjectSelects1.add("from[" + PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOBUSINESSAREA
							+ "|to.current == Active].to.id");
					slObjectSelects1.add("from[" + PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOBUSINESSAREA
							+ "|to.current == Active].to.name");
					slObjectSelects1.add(DomainConstants.SELECT_NAME);
					StringList slObjectmultiSelects = new StringList();
					slObjectmultiSelects
							.add("from[" + PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOBUSINESSAREA + "].to.id");
					slObjectmultiSelects
							.add("from[" + PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOBUSINESSAREA + "].to.name");
					Map detailMap = childObject.getInfo(context, slObjectSelects1, slObjectmultiSelects);
					if (detailMap != null) {
						StringList businessAreaIdList = (StringList) detailMap
								.get("from[" + PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOBUSINESSAREA + "].to.id");
						StringList businessAreaNameList = (StringList) detailMap
								.get("from[" + PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOBUSINESSAREA + "].to.name");
						if (businessAreaIdList != null && businessAreaNameList != null) {
							for (int i = 0; i < businessAreaIdList.size(); i++) {
								tempBAMap.put(businessAreaIdList.get(i), businessAreaNameList.get(i));
							}
						}
						pcpNAME = (String) detailMap.get(DomainConstants.SELECT_NAME);
					}
				}
				whereClauseForPCP = new StringBuffer("name == '").append(pcpNAME).append("'").toString();
				whereClauseForPTP = "current == Active";
				whereClauseForPTC = "current == Active";
				updatedType = PGFISWSConstants.TYPE_PGPLIPRODUCT_CACTEGORY_PLATFORM;
				getObjectDetailsForProductClassification(context, updatedType, whereClauseForPCP, whereClauseForPTP,
						whereClauseForPTC, tempBAMap, productClassificationDataList);

			} else if (childObject.isKindOf(context, PGFISWSConstants.TYPE_PGPLIPRODUCT_TECHNOLOGY_PLATFORM)) {

				Map<String, String> tempBAMap = new HashMap<>();
				String ptpNAME = "";
				if (UIUtil.isNotNullAndNotEmpty(parentPhysicalId)) {
					DomainObject parentObject = DomainObject.newInstance(context, parentPhysicalId);
					StringList slObjectSelects1 = new StringList();
					slObjectSelects1.add("from[" + PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOBUSINESSAREA
							+ "|to.current == Active].to.id");
					slObjectSelects1.add("from[" + PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOBUSINESSAREA
							+ "|to.current == Active].to.name");
					StringList slObjectmultiSelects = new StringList();
					slObjectmultiSelects
							.add("from[" + PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOBUSINESSAREA + "].to.id");
					slObjectmultiSelects
							.add("from[" + PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOBUSINESSAREA + "].to.name");
					Map detailMap = parentObject.getInfo(context, slObjectSelects1, slObjectmultiSelects);
					if (detailMap != null) {
						StringList businessAreaIdList = (StringList) detailMap
								.get("from[" + PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOBUSINESSAREA + "].to.id");
						StringList businessAreaNameList = (StringList) detailMap
								.get("from[" + PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOBUSINESSAREA + "].to.name");
						if (businessAreaIdList != null && businessAreaNameList != null) {
							for (int i = 0; i < businessAreaIdList.size(); i++) {
								tempBAMap.put(businessAreaIdList.get(i), businessAreaNameList.get(i));
							}
						}
						ptpNAME = childObject.getInfo(context, DomainConstants.SELECT_NAME);
					}
				} else {
					StringList slObjectSelects1 = new StringList();
					slObjectSelects1.add("from[" + PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOPLATFORM + "].to.from["
							+ PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOBUSINESSAREA + "|to.current == Active].to.id");
					slObjectSelects1.add("from[" + PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOPLATFORM + "].to.from["
							+ PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOBUSINESSAREA
							+ "|to.current == Active].to.name");
					slObjectSelects1.add(DomainConstants.SELECT_NAME);
					StringList slObjectmultiSelects = new StringList();
					slObjectmultiSelects.add("from[" + PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOPLATFORM + "].to.from["
							+ PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOBUSINESSAREA + "].to.id");
					slObjectmultiSelects.add("from[" + PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOPLATFORM + "].to.from["
							+ PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOBUSINESSAREA + "].to.name");
					Map detailMap = childObject.getInfo(context, slObjectSelects1, slObjectmultiSelects);
					if (detailMap != null) {
						StringList businessAreaIdList = (StringList) detailMap
								.get("from[" + PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOPLATFORM + "].to.from["
										+ PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOBUSINESSAREA + "].to.id");
						StringList businessAreaNameList = (StringList) detailMap
								.get("from[" + PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOPLATFORM + "].to.from["
										+ PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOBUSINESSAREA + "].to.name");
						if (businessAreaIdList != null && businessAreaNameList != null) {
							for (int i = 0; i < businessAreaIdList.size(); i++) {
								tempBAMap.put(businessAreaIdList.get(i), businessAreaNameList.get(i));
							}
						}
						ptpNAME = (String) detailMap.get(DomainConstants.SELECT_NAME);
					}
				}
				whereClauseForPCP = new StringBuffer("current == Active && to[").append(PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOPLATFORM).append("].from.name == '").append(ptpNAME).append("'").toString();
				whereClauseForPTP = new StringBuffer("name == '").append(ptpNAME).append("'").toString();
				whereClauseForPTC = "current == Active";
				updatedType = PGFISWSConstants.TYPE_PGPLIPRODUCT_TECHNOLOGY_PLATFORM;
				getObjectDetailsForProductClassification(context, updatedType, whereClauseForPCP, whereClauseForPTP,
						whereClauseForPTC, tempBAMap, productClassificationDataList);

			} else if (childObject.isKindOf(context, PGFISWSConstants.TYPE_PGPLIPRODUCT_TECHNOLOGY_CHASSIS)) {

				Map<String, String> tempBAMap = new HashMap<>();
				String ptcNAME = "";
				if (UIUtil.isNotNullAndNotEmpty(parentPhysicalId)) {
					DomainObject parentObject = DomainObject.newInstance(context, parentPhysicalId);
					StringList slObjectSelects1 = new StringList();
					slObjectSelects1.add("from[" + PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOPLATFORM + "].to.from["
							+ PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOBUSINESSAREA + "|to.current == Active].to.id");
					slObjectSelects1.add("from[" + PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOPLATFORM + "].to.from["
							+ PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOBUSINESSAREA
							+ "|to.current == Active].to.name");
					slObjectSelects1.add(DomainConstants.SELECT_NAME);
					StringList slObjectmultiSelects = new StringList();
					slObjectmultiSelects.add("from[" + PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOPLATFORM + "].to.from["
							+ PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOBUSINESSAREA + "].to.id");
					slObjectmultiSelects.add("from[" + PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOPLATFORM + "].to.from["
							+ PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOBUSINESSAREA + "].to.name");
					Map detailMap = parentObject.getInfo(context, slObjectSelects1, slObjectmultiSelects);
					if (detailMap != null) {
						StringList businessAreaIdList = (StringList) detailMap
								.get("from[" + PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOPLATFORM + "].to.from["
										+ PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOBUSINESSAREA + "].to.id");
						StringList businessAreaNameList = (StringList) detailMap
								.get("from[" + PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOPLATFORM + "].to.from["
										+ PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOBUSINESSAREA + "].to.name");
						if (businessAreaIdList != null && businessAreaNameList != null) {
							for (int i = 0; i < businessAreaIdList.size(); i++) {
								tempBAMap.put(businessAreaIdList.get(i), businessAreaNameList.get(i));
							}
						}
						ptcNAME = childObject.getInfo(context, DomainConstants.SELECT_NAME);
					}
				} else {
					StringList slObjectSelects1 = new StringList();
					slObjectSelects1.add("to[" + PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOCHASSIS + "].from.from["
							+ PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOPLATFORM + "].to.from["
							+ PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOBUSINESSAREA + "|to.current == Active].to.id");
					slObjectSelects1.add("to[" + PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOCHASSIS + "].from.from["
							+ PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOPLATFORM + "].to.from["
							+ PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOBUSINESSAREA
							+ "|to.current == Active].to.name");
					slObjectSelects1.add(DomainConstants.SELECT_NAME);
					StringList slObjectmultiSelects = new StringList();
					slObjectmultiSelects.add("to[" + PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOCHASSIS + "].from.from["
							+ PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOPLATFORM + "].to.from["
							+ PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOBUSINESSAREA + "].to.id");
					slObjectmultiSelects.add("to[" + PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOCHASSIS + "].from.from["
							+ PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOPLATFORM + "].to.from["
							+ PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOBUSINESSAREA + "].to.name");
					Map detailMap = childObject.getInfo(context, slObjectSelects1, slObjectmultiSelects);
					if (detailMap != null) {
						StringList businessAreaIdList = (StringList) detailMap
								.get("to[" + PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOCHASSIS + "].from.from["
										+ PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOPLATFORM + "].to.from["
										+ PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOBUSINESSAREA + "].to.id");
						StringList businessAreaNameList = (StringList) detailMap
								.get("to[" + PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOCHASSIS + "].from.from["
										+ PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOPLATFORM + "].to.from["
										+ PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOBUSINESSAREA + "].to.name");
						if (businessAreaIdList != null && businessAreaNameList != null) {
							for (int i = 0; i < businessAreaIdList.size(); i++) {
								tempBAMap.put(businessAreaIdList.get(i), businessAreaNameList.get(i));
							}
						}
						ptcNAME = (String) detailMap.get(DomainConstants.SELECT_NAME);
					}
				}
				whereClauseForPCP = new StringBuffer("current == Active && to[").append(PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOPLATFORM).append("].from.from[").append(PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOCHASSIS).append("].to.name == '").append(ptcNAME).append("'").toString();
				
				whereClauseForPTP = new StringBuffer("current == Active && from[").append(PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOCHASSIS).append("].to.name == '").append(ptcNAME).append("'").toString();
				whereClauseForPTC = new StringBuffer("name == '").append(ptcNAME).append("'").toString();
				updatedType = PGFISWSConstants.TYPE_PGPLIPRODUCT_TECHNOLOGY_CHASSIS;
				getObjectDetailsForProductClassification(context, updatedType, whereClauseForPCP, whereClauseForPTP,
						whereClauseForPTC, tempBAMap, productClassificationDataList);
			}
		} catch (Exception ex) {
			throw new Exception("Exception Message: " + ex.getMessage());
		}
		return productClassificationDataList;
	}

	private static void getObjectDetailsForProductClassification(Context context, String updatedType,
			String whereClauseForPCP, String whereClauseForPTP, String whereClauseForPTC, Map<String, String> tempBAMap,
			ArrayList<String> productClassificationDataList) throws Exception {
		StringList slObjectSelects = new StringList();
		slObjectSelects.add(DomainConstants.SELECT_ID);
		slObjectSelects.add(DomainConstants.SELECT_NAME);
		try {
			// 1:: Iterate tempBAmap
			if (null != tempBAMap && !tempBAMap.isEmpty()) {
				for (Map.Entry<String, String> entryBA : tempBAMap.entrySet()) {
					String strBAID = entryBA.getKey();
					String strBAName = entryBA.getValue();
					String relBA = PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOBUSINESSAREA;
					String typePCP = PGFISWSConstants.TYPE_PGPLIPRODUCT_CACTEGORY_PLATFORM;
					Map<String, String> tempPCPMap = new HashMap<>();
					tempPCPMap = getRelatedObjectDetails(context, relBA, typePCP, slObjectSelects, true, false,
							whereClauseForPCP, strBAID, strBAName, tempPCPMap, productClassificationDataList,
							updatedType);

					// 2:: Iterate tempPCPMap map
					if (null != tempPCPMap && !tempPCPMap.isEmpty()) {
						for (Map.Entry<String, String> entryPCP : tempPCPMap.entrySet()) {
							String strPCPID = entryPCP.getKey();
							String strPCPName = entryPCP.getValue();
							String relPCP = PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOPLATFORM;
							String typePTP = PGFISWSConstants.TYPE_PGPLIPRODUCT_TECHNOLOGY_PLATFORM;
							Map<String, String> tempPTPMap = new HashMap<>();
							tempPTPMap = getRelatedObjectDetails(context, relPCP, typePTP, slObjectSelects, true, false,
									whereClauseForPTP, strPCPID, strPCPName, tempPTPMap, productClassificationDataList,
									updatedType);

							// 3:: Iterate tempPTPMap map
							if (null != tempPTPMap && !tempPTPMap.isEmpty()) {
								for (Map.Entry<String, String> entryPTP : tempPTPMap.entrySet()) {
									String strID = entryPTP.getKey();
									String strName = entryPTP.getValue();
									String relPTC = PGFISWSConstants.RELATIONSHIP_PGPLATFORMTOCHASSIS;
									String typePTC = PGFISWSConstants.TYPE_PGPLIPRODUCT_TECHNOLOGY_CHASSIS;
									Map<String, String> tempPTCMap = new HashMap<>();
									tempPTCMap = getRelatedObjectDetails(context, relPTC, typePTC, slObjectSelects,
											false, true, whereClauseForPTC, strID, strName, tempPTCMap,
											productClassificationDataList, updatedType);
								}
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			throw new Exception("Exception Message: " + ex.getMessage());
		}
	}

	/**
	 * Method is for getting related objects
	 * 
	 * @param context
	 * @param strRelName
	 * @param strType
	 * @param slObjectSelects
	 * @param getTo
	 * @param getFrom
	 * @param sbWhereClause
	 * @param strID
	 * @param strName
	 * @param tempMap
	 * @param productClassificationDataList
	 * @param updatedType
	 * @return MapList of object details
	 * @throws Exception
	 */
	private static Map<String, String> getRelatedObjectDetails(Context context, String strRelName, String strType,
			StringList slObjectSelects, boolean getTo, boolean getFrom, String sbWhereClause, String strID,
			String strName, Map<String, String> tempMap, ArrayList<String> productClassificationDataList,
			String updatedType) throws Exception {
		// Domain Object initialized
		DomainObject obj;
		try {
			obj = DomainObject.newInstance(context, strID);
			MapList mlConnectedObjects = obj.getRelatedObjects(context, 
					strRelName,           	//relationshipPattern
					strType, 				//typePattern
					slObjectSelects, 		//objectSelects
					null,					//relationshipSelects
					getTo, 					//getTo
					getFrom, 				//getFrom
					(short) 1, 				//recurseToLevel
					sbWhereClause, 			//objectWhere
					null, 					//relationshipWhere
					0);						// The max number of Objects to get in the expand
			if (null != mlConnectedObjects) {
				Iterator mlIter = mlConnectedObjects.iterator();
				while (mlIter != null && mlIter.hasNext()) {
					Map map = (Map) mlIter.next();
					String sID = (String) map.get(DomainConstants.SELECT_ID);
					String sName = (String) map.get(DomainConstants.SELECT_NAME);

					if (UIUtil.isNotNullAndNotEmpty(strName)) {
						String strTemp = strName + " > " + sName;
						tempMap.put(sID, strTemp);
						if (isTypeValidForCombination(updatedType, strType)) {
							productClassificationDataList.add(strTemp);
						}
					}
				}
			}
		} catch (Exception ex) {
			throw new Exception("Exception Message: " + ex.getMessage());
		}
		return tempMap;
	}

	private static boolean isTypeValidForCombination(String updatedType, String contextType) throws Exception {
		boolean isTypeValidForCombination = false;
		if (PGFISWSConstants.TYPE_PGPLIBUSINESS_AREA.equals(updatedType)) {
			isTypeValidForCombination = true;
		} else if (PGFISWSConstants.TYPE_PGPLIPRODUCT_CACTEGORY_PLATFORM.equals(updatedType)) {
			isTypeValidForCombination = true;
		} else if (PGFISWSConstants.TYPE_PGPLIPRODUCT_TECHNOLOGY_PLATFORM.equals(updatedType)) {
			if (PGFISWSConstants.TYPE_PGPLIPRODUCT_TECHNOLOGY_PLATFORM.equals(contextType)
					|| PGFISWSConstants.TYPE_PGPLIPRODUCT_TECHNOLOGY_CHASSIS.equals(contextType)) {
				isTypeValidForCombination = true;
			}
		} else if (PGFISWSConstants.TYPE_PGPLIPRODUCT_TECHNOLOGY_CHASSIS.equals(updatedType)) {
			if (PGFISWSConstants.TYPE_PGPLIPRODUCT_TECHNOLOGY_CHASSIS.equals(contextType)) {
				isTypeValidForCombination = true;
			}
		}
		return isTypeValidForCombination;
	}

}

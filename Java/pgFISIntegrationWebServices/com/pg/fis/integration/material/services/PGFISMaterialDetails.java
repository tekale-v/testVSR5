package com.pg.fis.integration.material.services;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.fis.integration.constants.PGFISWSConstants;
import com.pg.fis.integration.util.PGFISIntegrationUtil;
import com.dassault_systemes.egs.server.common.ENOFormulationECMUtil;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.json.JSONObject;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.StringList;
import com.dassault_systemes.enovia.enterprisechangemgt.common.ChangeAction;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class PGFISMaterialDetails {

	private static final Logger logger = Logger.getLogger(PGFISMaterialDetails.class.getName());
	static final String ERROR_Logger = "Exception in PGFISMaterialDetails";
	private  boolean blnFlagForCLoudPatchRequest =  false;
	// This substance is used as place holder when quality is not 100% in Material created in cloud tenant
	private static final String placeHolderSubstance = "869697";

	/**
	 * REST method to Update On-Premise Material
	 * 
	 * @param paramHttpServletRequest
	 * 
	 * @param Context  Context used to call API
	 * @param mpRequestMap
	 * @return response in JSON String format
	 * @throws Exception when operation fails
	 */
	@SuppressWarnings("unchecked")
	public  Response reviseAndupdateOnPremMaterial(Context context, HttpServletRequest paramHttpServletRequest,
		Map<String, Object> mpRequestMap) throws Exception {
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		boolean isContextPushed = false;
		blnFlagForCLoudPatchRequest = false;
		try {
			String strUUID = (String) mpRequestMap.get("id");
			String strEnterpriseId = DomainConstants.EMPTY_STRING;
			if (mpRequestMap.containsKey(PGFISWSConstants.JSON_TAG_HAS_ASSIGNED_IDENTIFIER)) {
				ArrayList<Map> hasAssignedIdentifiers = (ArrayList<Map>) mpRequestMap.get(PGFISWSConstants.JSON_TAG_HAS_ASSIGNED_IDENTIFIER);
				if (hasAssignedIdentifiers != null && !hasAssignedIdentifiers.isEmpty()) {
					for (int i = 0; i < hasAssignedIdentifiers.size(); i++) {
						Map<String, ?> identifierMap = (Map<String, ?>) hasAssignedIdentifiers.get(i);
						if(identifierMap.containsKey(PGFISWSConstants.JSON_TAG_IDENTIFIER_TYPE)) {
							Map<String, String> identifireTypeMap = (Map<String, String>) identifierMap
									.get(PGFISWSConstants.JSON_TAG_IDENTIFIER_TYPE);
							String identifireType = (String) identifireTypeMap.get(PGFISWSConstants.JSON_TAG_PREF_LABEL);
							if (identifireType.equals(PGFISWSConstants.JSON_TAG_ENTERPRISE_IDENTIFIER)) {
								strEnterpriseId = (String) identifierMap.get(PGFISWSConstants.JSON_TAG_IDENTIFIER);
								break;
							}
						}
					}
				}
			}
			String objectType = DomainConstants.EMPTY_STRING;
			String objectCurrentState = DomainConstants.EMPTY_STRING;
			String strwhere = DomainConstants.EMPTY_STRING;
			Map hasConstituencyMap = new HashMap();
			if(mpRequestMap.containsKey(PGFISWSConstants.JSON_TAG_HAS_CONSTITUENCY)) {
				hasConstituencyMap = (Map) mpRequestMap.get(PGFISWSConstants.JSON_TAG_HAS_CONSTITUENCY);
			}
			if (mpRequestMap.containsKey(PGFISWSConstants.JSON_TAG_HAS_MAT_TYPE)) {
				Map<String, String> matTypeMap = (Map<String, String>) mpRequestMap.get(PGFISWSConstants.JSON_TAG_HAS_MAT_TYPE);
				objectType = (String) matTypeMap.get(PGFISWSConstants.JSON_TAG_PREF_LABEL);
			}
			if (mpRequestMap.containsKey(PGFISWSConstants.JSON_TAG_LIFECYCLE_STATE)) {
				Map<String, String> currentStateMap = (Map<String, String>) mpRequestMap.get(PGFISWSConstants.JSON_TAG_LIFECYCLE_STATE);
				if (currentStateMap.containsKey(PGFISWSConstants.JSON_TAG_LABEL)) {
					objectCurrentState = (String) currentStateMap.get(PGFISWSConstants.JSON_TAG_LABEL);
				}
			}
			if (UIUtil.isNotNullAndNotEmpty(strEnterpriseId)) {
				strwhere = "revision == Last && " + DomainConstants.SELECT_NAME + "==" + strEnterpriseId;
			} else {
				strwhere = "revision == Last && " + PGFISWSConstants.SELECT_ATTRIBUTE_REFERENCE_URI + "== '" + strUUID
						+ "'";
			}
			if (PGFISWSConstants.TYPE_SOURCED.equals(objectType) && PGFISWSConstants.STATE_PUBLICHED.equals(objectCurrentState)) {
				StringList slSelectable = new StringList();
				slSelectable.add(DomainConstants.SELECT_PHYSICAL_ID);
				slSelectable.add(PGFISWSConstants.SELECT_ATTRIBUTE_ISNGF_LOCKED);
				MapList objDetailList = PGFISIntegrationUtil.findObjectAndRetunDetails(context,
						PGFISWSConstants.TYPE_RAW_MATERIAL, DomainConstants.QUERY_WILDCARD, slSelectable, strwhere);
				if (objDetailList != null && objDetailList.size() != 0 && !objDetailList.isEmpty()) {
					Map<String, String> objectMap = (Map<String, String>) objDetailList.get(0);
					String productId = (String) objectMap.get(DomainConstants.SELECT_PHYSICAL_ID);
					String isPartLockedForNGF = (String) objectMap.get(PGFISWSConstants.SELECT_ATTRIBUTE_ISNGF_LOCKED);
					ContextUtil.startTransaction(context, true);
					JSONObject payload = new JSONObject(mpRequestMap);
					Map argumentMap = prepareJPOArgs(paramHttpServletRequest, payload.toString());
					argumentMap.put(PGFISWSConstants.REQUEST_FOR, PGFISWSConstants.REVISE_AND_UPDATE_API_VALUE);
					argumentMap.put(PGFISWSConstants.MATERIAL_ID_KEY, productId);
					argumentMap.put(PGFISWSConstants.IS_RAW_MATERIAL_KEY, isPartLockedForNGF);
					String[] args = JPO.packArgs(argumentMap);
					Map<String, Object> returnMap = JPO.invoke(context, "bioFormRawMaterialCloudIntegration", null,
							"pgUpdateRawMaterial", args, Map.class);
					StringBuilder responseString = new StringBuilder();
					String revobjectId = "";
					Map objectDetailsMap = new HashMap();
					Response.Status status = (Response.Status) returnMap.get("Status Code");
					if (!status.equals(Status.OK)) {
						String returnMessage = returnMap.get("Result").toString();
						if (returnMessage != null)
							responseString.append(returnMessage);
						return Response.status(status).entity(responseString.toString()).build();
					} else {
						ObjectNode resultNode = (ObjectNode) returnMap.get("Result");
						revobjectId = resultNode.get("physicalid").asText();
						objectDetailsMap = (Map) returnMap.get("objectDetailsMap");
					}
					DomainObject newRevisionObject = DomainObject.newInstance(context, revobjectId);
					String substanceUpdateMessage = "";
					String releaseRMPartMessage = "";
					try {
						//Pushing content to allow Modification, connection to  context object, as context user sometime not having proper access.
						ContextUtil.pushContext(context);
						isContextPushed = true;
						substanceUpdateMessage = calculateSubstacePercentageAndCreateSubstance(context,
							objectDetailsMap, hasConstituencyMap);
						releaseRMPartMessage = releaseRMPart(context, newRevisionObject, objectDetailsMap);
					} finally {
						if(isContextPushed){
							ContextUtil.popContext(context);
							isContextPushed = false;
						}	
					}
					ContextUtil.commitTransaction(context);
					if (substanceUpdateMessage.equals(PGFISWSConstants.STATUS_SUCCESS) && releaseRMPartMessage.equals(PGFISWSConstants.STATUS_SUCCESS)) {
						jsonReturnObj.add(PGFISWSConstants.STRING_STATUS, PGFISWSConstants.STATUS_SUCCESS);
						jsonReturnObj.add(PGFISWSConstants.JSON_TAG_PHYSICAL_ID, revobjectId);
						jsonReturnObj.add(PGFISWSConstants.JSON_TAG_PATCH_TO_CLOUD, blnFlagForCLoudPatchRequest);
						jsonReturnObj.add(PGFISWSConstants.STRING_MESSAGE, PGFISWSConstants.MAT_REVISED_RELEASED_MSG);
					} else if (!substanceUpdateMessage.equals(PGFISWSConstants.STATUS_SUCCESS)) {
						jsonReturnObj.add(PGFISWSConstants.STRING_STATUS, PGFISWSConstants.STATUS_ERROR);
						jsonReturnObj.add(PGFISWSConstants.JSON_TAG_PATCH_TO_CLOUD, blnFlagForCLoudPatchRequest);
						jsonReturnObj.add(PGFISWSConstants.STRING_MESSAGE, substanceUpdateMessage);
					} else if (!releaseRMPartMessage.equals(PGFISWSConstants.STATUS_SUCCESS)) {
						jsonReturnObj.add(PGFISWSConstants.STRING_STATUS, PGFISWSConstants.STATUS_ERROR);
						jsonReturnObj.add(PGFISWSConstants.JSON_TAG_PATCH_TO_CLOUD, blnFlagForCLoudPatchRequest);
						jsonReturnObj.add(PGFISWSConstants.STRING_MESSAGE, releaseRMPartMessage);
					}

				} else {
					String message = FrameworkUtil.findAndReplace(PGFISWSConstants.NO_OBJECT_FOUND_MSG, "<NAME>",
							strEnterpriseId);
					message = FrameworkUtil.findAndReplace(message, "<Reference URI>", strUUID);
					jsonReturnObj.add(PGFISWSConstants.STRING_STATUS, PGFISWSConstants.JSON_OUTPUT_KEY_ERROR);
					jsonReturnObj.add(PGFISWSConstants.STRING_MESSAGE, message);
					return Response.status(Response.Status.BAD_REQUEST).entity(jsonReturnObj.build().toString())
							.build();
				}

			} else if (PGFISWSConstants.TYPE_SOURCED.equals(objectType) && PGFISWSConstants.STATE_EXTERNALLY_MANAGED.equals(objectCurrentState)) {
				StringList slSelectable = new StringList(2);
				slSelectable.add(DomainConstants.SELECT_ID);
				MapList objDetailList = PGFISIntegrationUtil.findObjectAndRetunDetails(context,
						PGFISWSConstants.TYPE_RAW_MATERIAL, DomainConstants.QUERY_WILDCARD, slSelectable, strwhere);
				if (objDetailList != null && objDetailList.size() != 0 && !objDetailList.isEmpty()) {
					Map<String, String> objectMap = (Map<String, String>) objDetailList.get(0);
					String productId = (String) objectMap.get(DomainConstants.SELECT_ID);
					DomainObject dmObj = DomainObject.newInstance(context, productId);
					try {
						//Pushing content to allow Modification to  context object, as context user sometime not having proper access.
						ContextUtil.pushContext(context);
						isContextPushed = true;
						dmObj.setAttributeValue(context, PGFISWSConstants.ATTRIBUTE_ISNGF_LOCKED, "FALSE");
					} finally {
						if(isContextPushed){
							ContextUtil.popContext(context);
							isContextPushed = false;
						}	
					}
					jsonReturnObj.add(PGFISWSConstants.STRING_STATUS, PGFISWSConstants.STATUS_SUCCESS);
					jsonReturnObj.add(PGFISWSConstants.JSON_TAG_PATCH_TO_CLOUD, blnFlagForCLoudPatchRequest);
					jsonReturnObj.add(PGFISWSConstants.STRING_MESSAGE, PGFISWSConstants.MAT_UNLOCKED_MSG);
				} else {
					String message = FrameworkUtil.findAndReplace(PGFISWSConstants.NO_OBJECT_FOUND_MSG, "<NAME>",
							strEnterpriseId);
					message = FrameworkUtil.findAndReplace(message, "<Reference URI>", strUUID);
					jsonReturnObj.add(PGFISWSConstants.STRING_STATUS, PGFISWSConstants.JSON_OUTPUT_KEY_ERROR);
					jsonReturnObj.add(PGFISWSConstants.STRING_MESSAGE, message);
					return Response.status(Response.Status.BAD_REQUEST).entity(jsonReturnObj.build().toString())
							.build();
				}
			} else if(PGFISWSConstants.TYPE_SOURCED.equals(objectType) && PGFISWSConstants.STATE_ARCHIVED.equals(objectCurrentState)) {
				StringList slSelectable = new StringList(2);
				slSelectable.add(DomainConstants.SELECT_ID);
				MapList objDetailList = PGFISIntegrationUtil.findObjectAndRetunDetails(context,
						PGFISWSConstants.TYPE_RAW_MATERIAL, DomainConstants.QUERY_WILDCARD, slSelectable, strwhere);
				if (objDetailList != null && objDetailList.size() != 0 && !objDetailList.isEmpty()) {
					Map<String, String> objectMap = (Map<String, String>) objDetailList.get(0);
					String productId = (String) objectMap.get(DomainConstants.SELECT_ID);
					DomainObject dmObj = DomainObject.newInstance(context, productId);
					try {
						//Pushing content to allow Modification to  context object, as context user sometime not having proper access.
						ContextUtil.pushContext(context);
						isContextPushed = true;
						dmObj.setState(context, PGFISWSConstants.STATE_OBSOLETE);
					} finally {
						if(isContextPushed){
							ContextUtil.popContext(context);
							isContextPushed = false;
						}	
					}
					jsonReturnObj.add(PGFISWSConstants.STRING_STATUS, PGFISWSConstants.STATUS_SUCCESS);
					jsonReturnObj.add(PGFISWSConstants.JSON_TAG_PATCH_TO_CLOUD, blnFlagForCLoudPatchRequest);
					jsonReturnObj.add(PGFISWSConstants.STRING_MESSAGE, PGFISWSConstants.SUCCESS_MESSAGE_ON_MAT_OBSOLETION);
				} else {
					String message = FrameworkUtil.findAndReplace(PGFISWSConstants.NO_OBJECT_FOUND_MSG, "<NAME>",
							strEnterpriseId);
					message = FrameworkUtil.findAndReplace(message, "<Reference URI>", strUUID);
					jsonReturnObj.add(PGFISWSConstants.STRING_STATUS, PGFISWSConstants.JSON_OUTPUT_KEY_ERROR);
					jsonReturnObj.add(PGFISWSConstants.STRING_MESSAGE, message);
					return Response.status(Response.Status.BAD_REQUEST).entity(jsonReturnObj.build().toString())
							.build();
				}	
			}
		} catch (Exception e) {
			if (context.isTransactionActive())
				ContextUtil.abortTransaction(context);
			logger.log(Level.SEVERE, ERROR_Logger, e);
			jsonReturnObj.add(PGFISWSConstants.STRING_STATUS, PGFISWSConstants.JSON_OUTPUT_KEY_ERROR);
			jsonReturnObj.add(PGFISWSConstants.STRING_MESSAGE, e.getMessage());
			jsonReturnObj.add(PGFISWSConstants.JSON_OUTPUT_KEY_TRACE, PGFISIntegrationUtil.getExceptionTrace(e));
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(jsonReturnObj.build().toString())
					.build();
		}
		return Response.status(Status.OK).entity(jsonReturnObj.build().toString()).build();
	}

	public static String releaseRMPart(Context context, DomainObject newRevisionObject,
			Map<String, String> objectDetailsMap) throws Exception {
		try {
			String internalMatState = (String) objectDetailsMap
					.get(PGFISWSConstants.SELECT_COMPONENT_MATERIAL_TO_STATE);
			String internalMatId = (String) objectDetailsMap.get(PGFISWSConstants.SELECT_COMPONENT_MATERIAL_TO_ID);
			String rawMatstate = (String) objectDetailsMap.get(DomainConstants.SELECT_CURRENT);
			if (UIUtil.isNotNullAndNotEmpty(internalMatId) && !internalMatState.equals(PGFISWSConstants.STATE_APPROVED)) {
				DomainObject internalMatObject = DomainObject.newInstance(context, internalMatId);
				try {
					internalMatObject.setState(context, PGFISWSConstants.STATE_APPROVED);
				} catch (Exception Ex) {
					throw new Exception("Exception Message: " + Ex.getMessage());
				}
			}
			if (UIUtil.isNotNullAndNotEmpty(rawMatstate) && !rawMatstate.equals(PGFISWSConstants.STATE_RELEASED)) {
				if (UIUtil.isNullOrEmpty(
						(String) objectDetailsMap.get(PGFISWSConstants.SELECT_ATTRIBUTE_REASON_FOR_CHANGE)))
					newRevisionObject.setAttributeValue(context, PGFISWSConstants.ATTRIBUTE_REASON_FOR_CHANGE, "New");
				if (UIUtil.isNullOrEmpty(
						(String) objectDetailsMap.get(PGFISWSConstants.SELECT_ATTRIBUTE_BASE_UNIT_OF_MEASURE)))
					newRevisionObject.setAttributeValue(context, PGFISWSConstants.ATTRIBUTE_BASE_UNIT_OF_MEASURE,
							"KILOGRAM");
				if (UIUtil.isNullOrEmpty((String) objectDetailsMap
						.get(PGFISWSConstants.SELECT_ATTRIBUTE_STRUCTURE_RELEASE_CRITERIA_REQ)))
					newRevisionObject.setAttributeValue(context,
							PGFISWSConstants.ATTRIBUTE_STRUCTURE_RELEASE_CRITERIA_REQ, "No");
				if (UIUtil.isNullOrEmpty(
						(String) objectDetailsMap.get(PGFISWSConstants.SELECT_ATTRIBUTE_MANUFACTURE_STATUS)))
					newRevisionObject.setAttributeValue(context, PGFISWSConstants.ATTRIBUTE_MANUFACTURE_STATUS,
							"DEVELOPMENT");
				if (UIUtil.isNullOrEmpty((String) objectDetailsMap.get(DomainConstants.SELECT_DESCRIPTION)))
					newRevisionObject.setDescription(context, "FIS Material");

				String strphysicalIdofNewObj = newRevisionObject.getPhysicalId(context);
				ENOFormulationECMUtil eNOFormulationECMUtil = ENOFormulationECMUtil.getInstance();
				ChangeAction changeAction = eNOFormulationECMUtil.getChangeActionForAffectedItem(context,
						strphysicalIdofNewObj);
				if (changeAction != null) {
					changeAction.delete(context);
				}
				newRevisionObject.setState(context, PGFISWSConstants.STATE_RELEASED);
			}
		} catch (Exception ex) {
			throw new Exception("Exception Message: " + ex.getMessage());
		}
		return PGFISWSConstants.STATUS_SUCCESS;
	}

	@SuppressWarnings("unchecked")
	public String calculateSubstacePercentageAndCreateSubstance(Context context,
			Map<String, String> objectDetailsMap, Map hasConstituencyMap) throws Exception {
		try {
			double totalMixure = 0.0;
			if (hasConstituencyMap != null) {
				if (!hasConstituencyMap.isEmpty()) {
					if (hasConstituencyMap.containsKey(PGFISWSConstants.JSON_TAG_HAS_MIXURE_RATIO)) {
						ArrayList<Map> mixtureRatioList = (ArrayList<Map>) hasConstituencyMap.get(PGFISWSConstants.JSON_TAG_HAS_MIXURE_RATIO);
						for (int i = 0; i < mixtureRatioList.size(); i++) {
							Map<String, ?> MixtureMap = (Map<String, ?>) mixtureRatioList.get(i);
							if (MixtureMap.containsKey(PGFISWSConstants.JSON_TAG_RATIO_TARGET)) {
								if (MixtureMap.containsKey(PGFISWSConstants.JSON_TAG_OFFLABEL)) {
									boolean offLabel =  (boolean) MixtureMap.get(PGFISWSConstants.JSON_TAG_OFFLABEL);
									if(!offLabel) {
										Number substanceRaito = (Number) MixtureMap.get(PGFISWSConstants.JSON_TAG_RATIO_TARGET);
										totalMixure = totalMixure + substanceRaito.doubleValue();
									}
								} else {
									Number substanceRaito = (Number) MixtureMap.get(PGFISWSConstants.JSON_TAG_RATIO_TARGET);
									totalMixure = totalMixure + substanceRaito.doubleValue();
								}
							}
							if (MixtureMap.containsKey(PGFISWSConstants.JSON_TAG_REST_OF_MIXURE)) {
								boolean restOfMixture = (boolean) MixtureMap.get(PGFISWSConstants.JSON_TAG_REST_OF_MIXURE);
								if (restOfMixture) {
									return PGFISWSConstants.STATUS_SUCCESS;
								}
							}
						}
						if (totalMixure >= 0.0 && totalMixure < 100) {
							String internalMatId = objectDetailsMap.get(PGFISWSConstants.SELECT_COMPONENT_MATERIAL_TO_ID);
							if (UIUtil.isNotNullAndNotEmpty(internalMatId)) {
								DomainObject intMatObj = DomainObject.newInstance(context, internalMatId);
								StringList slSelectList = new StringList();
								slSelectList.add("from[" + PGFISWSConstants.REL_COMPONENT_SUBSTANCE + "|to.name == "+ placeHolderSubstance + "].id");
								slSelectList.add("from[" + PGFISWSConstants.REL_COMPONENT_SUBSTANCE + "|to.name == "+ placeHolderSubstance + "].attribute["+PGFISWSConstants.ATTRIBUTE_QUANTITY+"]");
								Map placeholderSubRelIdMap = intMatObj.getInfo(context,slSelectList);
								String placeholderSubRelId = (String) placeholderSubRelIdMap.get(PGFISWSConstants.SELECT_COMPONENT_SUBSTANCE_REL_ID_FROM_INTERNAL_MAT);
								if (UIUtil.isNotNullAndNotEmpty(placeholderSubRelId)) {
									DomainRelationship domRelObject = DomainRelationship.newInstance(context,
											placeholderSubRelId);
									String placeholderSubComposition = (String) placeholderSubRelIdMap.get(PGFISWSConstants.SELECT_COMPONENT_SUBSTANCE_COMPOSITION_FROM_INTERNAL_MAT);
									double dplaceholderMixture = Double.parseDouble(placeholderSubComposition);
									String quantity = String.valueOf(100 - totalMixure + dplaceholderMixture);
									HashMap attributesMap = new HashMap();
									attributesMap.put(PGFISWSConstants.ATTRIBUTE_QUANTITY, quantity);
									attributesMap.put(PGFISWSConstants.ATTRIBUTE_FILL, "TRUE");
									attributesMap.put(PGFISWSConstants.ATTRIBUTE_IS_TARGET_MATERIAL, "FALSE");
									domRelObject.setAttributeValues(context, attributesMap);
									blnFlagForCLoudPatchRequest = true;
								} else {
										String quantity = String.valueOf(100 - totalMixure);
									return findAndUpdateSubstanceComposition(context, internalMatId, quantity);
								}
							} else {
								return PGFISWSConstants.NO_NTERNAL_OBJECT_FOUND_MSG;
							}
						}
					}
				}
			} else {
				String internalMatId = objectDetailsMap.get(PGFISWSConstants.SELECT_COMPONENT_MATERIAL_TO_ID);
				return findAndUpdateSubstanceComposition(context, internalMatId, "100.0");
			}
		} catch (Exception ex) {
			throw new Exception("Exception Message: " + ex.getMessage());
		}
		return PGFISWSConstants.STATUS_SUCCESS;
	}

	private String findAndUpdateSubstanceComposition(Context context, String internalMatId, String quantity)
			throws Exception {
		try {
			if(UIUtil.isNotNullAndNotEmpty(internalMatId)) {
			DomainObject internalMatObject = DomainObject.newInstance(context, internalMatId);
			StringList slSelectable = new StringList(2);
			slSelectable.add(DomainConstants.SELECT_ID);
			MapList objDetailList = PGFISIntegrationUtil.findObjectAndRetunDetails(context,
					PGFISWSConstants.TYPE_SUBSTANCE, placeHolderSubstance, slSelectable, "");
			if (objDetailList != null && objDetailList.size() > 0 && !objDetailList.isEmpty()) {
				Map objDetailsMap = (Map) objDetailList.get(0);
				String objectId = (String) objDetailsMap.get(DomainConstants.SELECT_ID);
					if(UIUtil.isNotNullAndNotEmpty(objectId)) {
				DomainRelationship domRel = DomainRelationship.connect(context, internalMatObject,
						PGFISWSConstants.REL_COMPONENT_SUBSTANCE, DomainObject.newInstance(context, objectId));
				HashMap attributesMap = new HashMap();
				if("100.0".equals(quantity)) {
					attributesMap.put(PGFISWSConstants.ATTRIBUTE_QUANTITY, quantity);
					attributesMap.put(PGFISWSConstants.ATTRIBUTE_FILL, "FALSE");
					attributesMap.put(PGFISWSConstants.ATTRIBUTE_IS_TARGET_MATERIAL, "TRUE");
				} else {
					attributesMap.put(PGFISWSConstants.ATTRIBUTE_QUANTITY, quantity);
					attributesMap.put(PGFISWSConstants.ATTRIBUTE_FILL, "TRUE");
					attributesMap.put(PGFISWSConstants.ATTRIBUTE_IS_TARGET_MATERIAL, "FALSE");
				}
				

				domRel.setAttributeValues(context, attributesMap);
				blnFlagForCLoudPatchRequest = true;
					}
			} else {
				String message = FrameworkUtil.findAndReplace(PGFISWSConstants.NO_PLACEHOLDER_SUBSTANCE_FOUND_MSG,
						"<SUB_NAME>", placeHolderSubstance);
				return message;
				}
			} else {
				return PGFISWSConstants.NO_NTERNAL_OBJECT_FOUND_MSG;
			}
		} catch (Exception ex) {
			throw new Exception("Exception Message: " + ex.getMessage());
		}
		return PGFISWSConstants.STATUS_SUCCESS;

	}

	/**
	 * REST method to Update On-Premise Material
	 * 
	 * @param paramHttpServletRequest
	 * 
	 * @param Context                 Context used to call API
	 * @param payLoad
	 * @return response in JSON String format
	 * @throws Exception when operation fails
	 */
	public static Response updateOnPremMaterial(Context context, HttpServletRequest Request, String payLoad)
			throws Exception {
		Response.Status status;
		StringBuilder responseString = new StringBuilder();
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		try {
			Map argumentMap = prepareJPOArgs(Request, payLoad);
			argumentMap.put(PGFISWSConstants.REQUEST_FOR, PGFISWSConstants.UPDATE_API_VALUE);
			String[] args = JPO.packArgs(argumentMap);
			Map<String, Object> returnMap = JPO.invoke(context, "bioFormRawMaterialCloudIntegration", null,
					"pgUpdateRawMaterial", args, Map.class);
			status = (Response.Status) returnMap.get("Status Code");
			String returnMessage = returnMap.get("Result").toString();
			if (returnMessage != null)
				responseString.append(returnMessage);
		} catch (Exception ex) {
			logger.log(Level.SEVERE, ERROR_Logger, ex);
			jsonReturnObj.add(PGFISWSConstants.STRING_STATUS, PGFISWSConstants.JSON_OUTPUT_KEY_ERROR);
			jsonReturnObj.add(PGFISWSConstants.STRING_MESSAGE, ex.getMessage());
			jsonReturnObj.add(PGFISWSConstants.JSON_OUTPUT_KEY_TRACE, PGFISIntegrationUtil.getExceptionTrace(ex));
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(jsonReturnObj.build().toString())
					.build();
		}
		return Response.status(status).entity(responseString.toString()).build();
	}

	private static Map prepareJPOArgs(HttpServletRequest request, String payload) throws Exception {
		Map<Object, Object> headers = new HashMap<>();
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String name = headerNames.nextElement();
			headers.put(name, request.getHeader(name));
		}
		Map<Object, Object> argumentMap = new HashMap<>();
		argumentMap.put("payload", payload);
		argumentMap.put("headers", headers);
		return argumentMap;
	}

}

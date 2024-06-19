package com.pg.fis.integration.physchemproperties.services;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.domain.util.i18nNow;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.fis.integration.constants.PGFISWSConstants;
import com.pg.fis.integration.util.PGFISIntegrationUtil;

import java.io.StringReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.Pattern;
import matrix.util.StringList;

public class PGFISPhysChemPropertyDetails {

	private static final Logger logger = Logger.getLogger(PGFISPhysChemPropertyDetails.class.getName());
	static final String ERROR_Logger = "Exception in PGFISPhysChemPropertyDetails";
	static final String TYPE_APPLICATION_FORMAT = "application/json";

	/**
	 * REST method to Update PhysChem Attributes
	 * 
	 * @param Context      used to call API
	 * @param mpRequestMap
	 * @return response in JSON String format
	 * @throws Exception when operation fails
	 */
	@SuppressWarnings("unchecked")
	public static Response updatePhysChemPropertiesToEnovia(Context context, Map<String, Object> mpRequestMap)
			throws Exception {
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		boolean isContextPushed = false;
		try {
			String strUUID = (String) mpRequestMap.get(PGFISWSConstants.STRING_KEY_FORMULA_UUID);
			Map attributeMap = (Map) mpRequestMap.get(PGFISWSConstants.STRING_KEY_PROPERTIES);
			String cloudProductForm = (String) mpRequestMap.get(PGFISWSConstants.STRING_KEY_PRODUCT_FORM);

			if (UIUtil.isNotNullAndNotEmpty(strUUID)) {
				String strwhere = "(current == Release || current == Complete || current == Obsolete) && " + PGFISWSConstants.SELECT_ATTRIBUTE_REFERENCE_URI + "== '"
						+ strUUID + "' && "+ PGFISWSConstants.SELECT_ATTRIBUTE_REFERENCE_IDENTIFIER + "!= ''" ;
				StringList slSelectable = new StringList();
				slSelectable.add(DomainConstants.SELECT_ID);
				slSelectable.add(DomainConstants.SELECT_REVISION);
				MapList objDetailList = PGFISIntegrationUtil.findObjectAndRetunDetails(context,
						PGFISWSConstants.TYPE_FORMULATION_PROCESS, DomainConstants.QUERY_WILDCARD, slSelectable,strwhere);
				Map objectMap = new HashMap();
				if (objDetailList != null && objDetailList.size() != 0 && !objDetailList.isEmpty()) {
					String formulaPartId = DomainConstants.EMPTY_STRING;
					String productForm = DomainConstants.EMPTY_STRING;
					java.util.Collections.sort(objDetailList, new Comparator<Map<String, String>>() {
						public int compare(final Map<String, String> o1, final Map<String, String> o2) {
							return o1.get(DomainConstants.SELECT_REVISION).compareTo(o2.get(DomainConstants.SELECT_REVISION));
						}
					});
					Map objDetailsMap = (Map<String, String>) objDetailList.get(0);
					String formulaProcessId = (String) objDetailsMap.get(DomainConstants.SELECT_ID);
					String formulaProcessRev= (String) objDetailsMap.get(DomainConstants.SELECT_REVISION);
					DomainObject formulaProcessObj = DomainObject.newInstance(context, formulaProcessId);
					
					StringList busSelects = new StringList();
					StringList slRelSelect = new StringList();
					busSelects.add(DomainConstants.SELECT_PHYSICAL_ID);
					busSelects.add(DomainConstants.SELECT_CURRENT);
					busSelects.add(PGFISWSConstants.SELECT_PRODUCTFORM_FROM_FORM_PART);
					busSelects.add(PGFISWSConstants.SELECT_PRODUCTFORM_REL_ID_FROM_FORM_PART);
					
					StringBuilder strObjectWhere = new StringBuilder("current ==");
		            strObjectWhere.append(PGFISWSConstants.STATE_RELEASE);
		            strObjectWhere.append(" || current == ");
		            strObjectWhere.append(PGFISWSConstants.STATE_COMPLETE);
		            strObjectWhere.append(" || current == ");
		            strObjectWhere.append(PGFISWSConstants.STATE_OBSOLETE);

					Pattern relPattern = new Pattern(PGFISWSConstants.REL_PLANNED_FOR);
					Pattern typePattern = new Pattern(PGFISWSConstants.TYPE_FORMULATION_PART);
					MapList objectDetailsList = formulaProcessObj.getRelatedObjects(context, relPattern.getPattern(), // Relationship Pattern
							typePattern.getPattern(), // Type Pattern
							busSelects, // Business object select
							slRelSelect, // Relationship Select
							true, // get To side objects
							false, // get From side objects
							(short) 0, // Recurse to level
							strObjectWhere.toString(), // Object Where clause
							DomainConstants.EMPTY_STRING, // Relationship Where clause
							0); // The max number of Objects to get in the expand
					if (objectDetailsList != null && objectDetailsList.size() != 0 && !objectDetailsList.isEmpty()) {
						Iterator objItr = objectDetailsList.iterator();
						while (objItr.hasNext()) 
				        {
							 Map map = (Map)objItr.next();
							 String state = (String) map.get(DomainConstants.SELECT_CURRENT);
							 if(PGFISWSConstants.STATE_RELEASE.equals(state) || PGFISWSConstants.STATE_COMPLETE.equals(state)) {
								 objectMap = map;
								 formulaPartId = (String) objectMap.get(DomainConstants.SELECT_PHYSICAL_ID);
								 productForm = (String) objectMap.get(PGFISWSConstants.SELECT_PRODUCTFORM_FROM_FORM_PART);
								 break;
							 } else if(PGFISWSConstants.STATE_OBSOLETE.equals(state)){
								 objectMap = map;
								 formulaPartId = (String) objectMap.get(DomainConstants.SELECT_PHYSICAL_ID);
								 productForm = (String) objectMap.get(PGFISWSConstants.SELECT_PRODUCTFORM_FROM_FORM_PART);
							 }
				        }
					}
					if(UIUtil.isNotNullAndNotEmpty(formulaPartId)) {
						DomainObject partObj = DomainObject.newInstance(context, formulaPartId);
						try {
							//Pushing content to allow Modification to  context object, as context user sometime not having proper access.
							ContextUtil.pushContext(context);
							isContextPushed = true;
							partObj.setAttributeValues(context, attributeMap);
							HashMap argumentMap = new HashMap();
							HashMap requestMap = new HashMap();
							requestMap.put("objectId", formulaPartId);
					    	argumentMap.put("requestMap", requestMap);
				            JPO.invoke(context, "pgDSOCPNProductData", null, "postProcessPhysicalChemicalPropertiesOnEdit", JPO.packArgs(argumentMap), Object.class);
						} finally {
							if (isContextPushed) {
								ContextUtil.popContext(context);
								isContextPushed = false;
							}
						}
						if (!cloudProductForm.equals(productForm)) {
							StringList pfSelectable = new StringList(2);
							pfSelectable.add(DomainConstants.SELECT_PHYSICAL_ID);
							String relId = (String) objectMap.get(PGFISWSConstants.SELECT_PRODUCTFORM_REL_ID_FROM_FORM_PART);
							MapList productFormList = PGFISIntegrationUtil.findObjectAndRetunDetails(context,
									PGFISWSConstants.TYPE_PRODUCT_FORM, cloudProductForm, pfSelectable, "");
							if (productFormList != null && productFormList.size() != 0 && !productFormList.isEmpty()) {
								Map<String, String> ProductFormMap = (Map<String, String>) productFormList.get(0);
								String productFormId = (String) ProductFormMap.get(DomainConstants.SELECT_PHYSICAL_ID);
								try {
									//Pushing content to allow connection/disconnection to  context object, as context user sometime not having proper access.
									ContextUtil.pushContext(context);
									isContextPushed = true;
									if (UIUtil.isNotNullAndNotEmpty(relId)) {
										DomainRelationship.disconnect(context, relId);
									}
									if (UIUtil.isNotNullAndNotEmpty(productFormId)) {
										DomainRelationship.connect(context, DomainObject.newInstance(context, productFormId),
											PGFISWSConstants.REL_OWNINGPRODUCTLINE, partObj);
										partObj.setAttributeValue(context, PGFISWSConstants.ATTRIBUTE_PG_ENG_PRODUCT_FORM,productFormId);
									}
								} finally {
									if (isContextPushed) {
										ContextUtil.popContext(context);
										isContextPushed = false;
									}
								}
							}
						}
						jsonReturnObj.add(PGFISWSConstants.STRING_STATUS, "Success");
						jsonReturnObj.add(PGFISWSConstants.STRING_MESSAGE, PGFISWSConstants.PROPERTY_UPDATED_SUCCESSFULLY);
					} else {
						String message = PGFISWSConstants.FORMULA_PROCESS_NOT_HAVING_FORM_PART_MESSAGE;
						jsonReturnObj.add(PGFISWSConstants.STRING_STATUS, PGFISWSConstants.JSON_OUTPUT_KEY_ERROR);
						jsonReturnObj.add(PGFISWSConstants.STRING_MESSAGE, message);
						return Response.status(Response.Status.BAD_REQUEST).entity(jsonReturnObj.build().toString())
								.build();
					}
					
				} else {
					String message = PGFISWSConstants.OBJECT_NOT_FOUND_MESSAGE + strUUID;
					jsonReturnObj.add(PGFISWSConstants.STRING_STATUS, PGFISWSConstants.JSON_OUTPUT_KEY_ERROR);
					jsonReturnObj.add(PGFISWSConstants.STRING_MESSAGE, message);
					return Response.status(Response.Status.BAD_REQUEST).entity(jsonReturnObj.build().toString())
							.build();
				}
			} else {
				String message = PGFISWSConstants.CLOUD_OBJECT_UUID_BLANK;
				jsonReturnObj.add(PGFISWSConstants.STRING_STATUS, PGFISWSConstants.JSON_OUTPUT_KEY_ERROR);
				jsonReturnObj.add(PGFISWSConstants.STRING_MESSAGE, message);
				return Response.status(Response.Status.BAD_REQUEST).entity(jsonReturnObj.build().toString()).build();
			}

		} catch (Exception e) {
			logger.log(Level.SEVERE, ERROR_Logger, e);
			jsonReturnObj.add(PGFISWSConstants.STRING_STATUS, PGFISWSConstants.JSON_OUTPUT_KEY_ERROR);
			jsonReturnObj.add(PGFISWSConstants.STRING_MESSAGE, e.getMessage());
			jsonReturnObj.add(PGFISWSConstants.JSON_OUTPUT_KEY_TRACE, PGFISIntegrationUtil.getExceptionTrace(e));
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(jsonReturnObj.build().toString())
					.build();

		}
		return Response.status(Status.OK).entity(jsonReturnObj.build().toString()).build();
	}

	/**
	 * Get PhysChem Attributes based on passed Selectables.
	 * 
	 * @param context
	 * @param strJsonString - Json String with UUID and selectables
	 * @return String format
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static Response getObjectInfoData(Context context, String strInputData) throws Exception {
		JsonArray outputArray = Json.createArrayBuilder().build();
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		try {
			if (UIUtil.isNotNullAndNotEmpty(strInputData)) {
				JsonObject jsonInputData = getJsonFromJsonString(strInputData);
				String strObjectUUID = jsonInputData.getString(PGFISWSConstants.KEY_OBJECT_ID);
				if (UIUtil.isNotNullAndNotEmpty(strObjectUUID)) {
					String strColumnSelectable = jsonInputData.getString(PGFISWSConstants.KEY_OBJECT_SELECTS);
					StringList slObjectSelect = new StringList();
					slObjectSelect.add(DomainConstants.SELECT_ID);
					slObjectSelect.add(DomainConstants.SELECT_REVISION);
					String strwhere = "(current == Release || current == Complete || current == Obsolete) && " + PGFISWSConstants.SELECT_ATTRIBUTE_REFERENCE_URI + "== '"
							+ strObjectUUID + "' && "+ PGFISWSConstants.SELECT_ATTRIBUTE_REFERENCE_IDENTIFIER + "!= ''" ;
					MapList objDetailList = PGFISIntegrationUtil.findObjectAndRetunDetails(context,PGFISWSConstants.TYPE_FORMULATION_PROCESS, DomainConstants.QUERY_WILDCARD, slObjectSelect,strwhere);
					if (objDetailList.size() > 0 && !objDetailList.isEmpty()) {
						String formulaPartId = DomainConstants.EMPTY_STRING;
						java.util.Collections.sort(objDetailList, new Comparator<Map<String, String>>() {
							public int compare(final Map<String, String> o1, final Map<String, String> o2) {
								return o1.get(DomainConstants.SELECT_REVISION).compareTo(o2.get(DomainConstants.SELECT_REVISION));
							}
						});
						Map objDetailsMap = (Map<String, String>) objDetailList.get(0);
						String formulaProcessId = (String) objDetailsMap.get(DomainConstants.SELECT_ID);
						String formulaProcessRev= (String) objDetailsMap.get(DomainConstants.SELECT_REVISION);
						DomainObject formulaProcessObj = DomainObject.newInstance(context, formulaProcessId);
						
						StringList busSelects = new StringList();
						StringList slRelSelect = new StringList();
						busSelects.add(DomainConstants.SELECT_PHYSICAL_ID);
						busSelects.add(DomainConstants.SELECT_CURRENT);
			            StringBuilder strObjectWhere = new StringBuilder("current ==");
			            strObjectWhere.append(PGFISWSConstants.STATE_RELEASE);
			            strObjectWhere.append(" || current == ");
			            strObjectWhere.append(PGFISWSConstants.STATE_COMPLETE);
			            strObjectWhere.append(" || current == ");
			            strObjectWhere.append(PGFISWSConstants.STATE_OBSOLETE);

						Pattern relPattern = new Pattern(PGFISWSConstants.REL_PLANNED_FOR);
						Pattern typePattern = new Pattern(PGFISWSConstants.TYPE_FORMULATION_PART);
						MapList objectDetailsList = formulaProcessObj.getRelatedObjects(context, relPattern.getPattern(), // Relationship Pattern
								typePattern.getPattern(), // Type Pattern
								busSelects, // Business object select
								slRelSelect, // Relationship Select
								true, // get To side objects
								false, // get From side objects
								(short) 0, // Recurse to level
								strObjectWhere.toString(), // Object Where clause
								DomainConstants.EMPTY_STRING, // Relationship Where clause
								0); // The max number of Objects to get in the expand
						if (objectDetailsList != null && objectDetailsList.size() != 0 && !objectDetailsList.isEmpty()) {
							Iterator objItr = objectDetailsList.iterator();
							while (objItr.hasNext()) 
					        {
								 Map objectMap = (Map)objItr.next();
								 String state = (String) objectMap.get(DomainConstants.SELECT_CURRENT);
								 if(PGFISWSConstants.STATE_RELEASE.equals(state) || PGFISWSConstants.STATE_COMPLETE.equals(state)) {
									 formulaPartId = (String) objectMap.get(DomainConstants.SELECT_PHYSICAL_ID);
									 break;
								 } else if(PGFISWSConstants.STATE_OBSOLETE.equals(state)) {
									 formulaPartId = (String) objectMap.get(DomainConstants.SELECT_PHYSICAL_ID);
								 }
					        }
						}
						if (UIUtil.isNotNullAndNotEmpty(formulaPartId)) {
							StringList columnSelectable = StringUtil.split(strColumnSelectable, ",");
							DomainObject partObj = DomainObject.newInstance(context, formulaPartId);
							Map objInfoMap = partObj.getInfo(context, columnSelectable);
							outputArray = converMaplistToJsonArray(context, objInfoMap);
						} else {
							String message = PGFISWSConstants.FORMULA_PROCESS_NOT_HAVING_FORM_PART_MESSAGE;
							jsonReturnObj.add(PGFISWSConstants.STRING_STATUS, PGFISWSConstants.JSON_OUTPUT_KEY_ERROR);
							jsonReturnObj.add(PGFISWSConstants.STRING_MESSAGE, message);
							return Response.status(Response.Status.BAD_REQUEST).entity(jsonReturnObj.build().toString())
									.build();
						}
					} else {
						String message = PGFISWSConstants.OBJECT_NOT_FOUND_MESSAGE + strObjectUUID;
						jsonReturnObj.add(PGFISWSConstants.STRING_STATUS, PGFISWSConstants.JSON_OUTPUT_KEY_ERROR);
						jsonReturnObj.add(PGFISWSConstants.STRING_MESSAGE, message);
						return Response.status(Response.Status.BAD_REQUEST).entity(jsonReturnObj.build().toString())
								.build();
					}
				} else {
					String message = PGFISWSConstants.OBJECT_REFERENCE_URI_BLANK_MESSAGE;
					jsonReturnObj.add(PGFISWSConstants.STRING_STATUS, PGFISWSConstants.JSON_OUTPUT_KEY_ERROR);
					jsonReturnObj.add(PGFISWSConstants.STRING_MESSAGE, message);
					return Response.status(Response.Status.BAD_REQUEST).entity(jsonReturnObj.build().toString())
							.build();
				}

			}
		} catch (Exception ex) {
			logger.log(Level.SEVERE, ERROR_Logger, ex);
			jsonReturnObj.add(PGFISWSConstants.STRING_STATUS, PGFISWSConstants.JSON_OUTPUT_KEY_ERROR);
			jsonReturnObj.add(PGFISWSConstants.STRING_MESSAGE, ex.getMessage());
			jsonReturnObj.add(PGFISWSConstants.JSON_OUTPUT_KEY_TRACE, PGFISIntegrationUtil.getExceptionTrace(ex));
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(jsonReturnObj.build().toString())
					.build();
		}
		return Response.ok(outputArray).type(TYPE_APPLICATION_FORMAT).build();
	}

	/**
	 * Converts Json String data to json object
	 * 
	 * @param context
	 * @param strJsonString - Json String with UUID and selectables
	 * @return json format
	 * @throws Exception
	 */
	public static JsonObject getJsonFromJsonString(String strJsonString) throws Exception {
		StringReader srJsonString = new StringReader(strJsonString);
		Map<String, String> configMap = new HashMap<>();
		configMap.put(PGFISWSConstants.MAX_STRING_LENGTH, PGFISWSConstants.VALUE_KILOBYTES);
		JsonReaderFactory factory = Json.createReaderFactory(configMap);
		try (JsonReader jsonReader = factory.createReader(srJsonString)) {
			return jsonReader.readObject();
		} finally {
			srJsonString.close();
		}
	}

	/**
	 * Converts map data to json array
	 * 
	 * @param context
	 * @param mlList  list of maps containing object details
	 * @return json array format
	 * @throws Exception
	 */
	public static JsonArray converMaplistToJsonArray(Context context, Map<?, ?> objMap) throws Exception {
		JsonArrayBuilder jsonArr = Json.createArrayBuilder();
		try {
			JsonObjectBuilder jsonObject = null;
			String strValue = DomainConstants.EMPTY_STRING;
			Object objValue = null;
			StringList slTemp = null;
			StringBuilder sbValues = null;
			String strTypeDisplayName = null;
			String strLocale = context.getLocale().getLanguage();
			jsonObject = Json.createObjectBuilder();
			for (Entry<?, ?> entry : objMap.entrySet()) {
				objValue = entry.getValue();
				if (objValue == null || "".equals(objValue)) {
					objValue = DomainConstants.EMPTY_STRING;
				}
				if (objValue instanceof String) {
					strValue = (String) objValue;
				} else if (objValue instanceof StringList) {
					slTemp = (StringList) objValue;
					sbValues = new StringBuilder();
					for (int j = 0; j < slTemp.size(); j++) {
						if (UIUtil.isNotNullAndNotEmpty(slTemp.get(j))) {
							sbValues.append(slTemp.get(j));
						}
						if (j != slTemp.size() - 1) {
							sbValues.append(PGFISWSConstants.KEY_COMMA_SEPARATOR);
						}
					}
					strValue = sbValues.toString();
				} else if (DomainConstants.SELECT_TYPE.equals(entry.getKey())) {
					strTypeDisplayName = i18nNow.getTypeI18NString((String) objValue, strLocale);
					jsonObject.add(PGFISWSConstants.KEY_DISPLAY_TYPE, strTypeDisplayName);
					jsonObject.add(PGFISWSConstants.KEY_OBJECT_TYPE, (String) objValue);
				} else if (DomainConstants.SELECT_ATTRIBUTE_TITLE.equals(entry.getKey())) {
					jsonObject.add(PGFISWSConstants.KEY_DISPLAY_NAME, UIUtil.isNotNullAndNotEmpty(strValue) ? strValue
							: objMap.get(DomainConstants.SELECT_NAME).toString());
				} else if (DomainConstants.SELECT_ID.equals(entry.getKey())) {
					jsonObject.add(PGFISWSConstants.KEY_VALUE, (String) objValue);
				}
				jsonObject.add((String) entry.getKey(), strValue);
			}
			jsonArr.add(jsonObject);
		} catch (Exception ex) {
			logger.log(Level.SEVERE, ERROR_Logger, ex);
			throw new Exception(ERROR_Logger + " : " + ex.getMessage());
		}
		return jsonArr.build();
	}

}

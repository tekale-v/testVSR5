/**
 * 
 * Copyright (c) Dassault Systemes.
 * All Rights Reserved.
 *
 */
package com.pg.widgets.platformsecurity;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.PersonUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.exportcontrol.ExportControlConstants;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.apps.library.LibraryCentralConstants;
import com.matrixone.jdom.Document;
import com.matrixone.jdom.Element;
import com.matrixone.jdom.JDOMException;
import com.matrixone.jdom.input.SAXBuilder;
import com.matrixone.jdom.xpath.XPath;
import com.pg.widgets.util.PGWidgetConstants;
import com.pg.widgets.util.PGWidgetUtil;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.db.Relationship;
import matrix.util.MatrixException;
import matrix.util.Pattern;
import matrix.util.StringList;

public class PGIPSecurityUtil  {
	private static Logger logger = Logger.getLogger(PGIPSecurityUtil.class.getName());
	
	static final String RELATIONSHIP_CLASSIFIED_ITEM = PropertyUtil.getSchemaProperty(null, "relationship_ClassifiedItem");
	static final String PROGRAM_IP_SECURITY_COMMON_UTIL = "pgIPSecurityCommonUtil";
	static final String METHOD_GET_ALL_IP_CLASSES = "includeSecurityCategoryClassification";
	static final String METHOD_GET_ALL_SECURITY_CLASSES = "loadProjectSecurityGroup";
	static final String METHOD_CHANGE_CLASSIFICATION = "changeClassificationAttribute";
	static final String RANGE_RESTRICTED = "Restricted";
	static final String RANGE_HIGHLY_RESTRICTED = "Highly Restricted";

	static final String MAX_STRING_LENGTH = "org.apache.johnzon.max-string-length";
	static final String VALUE_KILOBYTES = "262144";
	static final String SELECT_HAS_CLASS_ACCESS = "current.access[fromdisconnect]";
	static final String SELECT_ACCESS_MODIFY = "current.access[modify]";
	static final String SELECT_ACCESS_CHANGEOWNER = "current.access[changeowner]";


	static final String KEY_PGIPCLASSIFICATION = "pgIPClassification";
	static final String KEY_CLASSIFICATION_VALUE = "vSelectedValue";
	static final String KEY_CLASS_LIST = "ClassList";
	static final String SYMBOLIC_TYPE_IP_CONTROL_CLASS = "type_IPControlClass";
	
	static final String KEY_CLASS_TYPE = "classType";
	static final String KEY_REQUEST_MAP = "requestMap";
	static final String KEY_RESTRICTED_ID_LIST = "pgIPRestrictedSecurityGroupsOID";
	static final String KEY_HIR_ID_LIST = "pgIPHighlyRestrictedSecurityGroupsOID";
	
	static final String KEY_RELATIONSHIP_ID = "RelationshipId";
	static final String KEY_NEW_ID = "NewId";
	static final String KEY_SELECTED_CLASS_IDS= "SelectedClassIds";

	static final String KEY_ALLOWED_CLASSIFICATION_TYPE = "AllowedClassificationType";
	static final String KEY_IP_DEFAULT = "default";
	
	static final String KEY_HAS_ADD_IP_ACCESS = "hasAddIPAccess";
	static final String KEY_HAS_ADD_SC_ACCESS = "hasAddSCAccess";
	static final String KEY_HAS_CHANGE_ACCESS = "hasChangeAccess";
	static final String KEY_HAS_REMOVE_ACCESS = "hasRemoveAccess";
	static final String KEY_HAS_RECLASSIFY_IP_ACCESS = "hasReclassifyIPAccess";
	static final String KEY_HAS_RECLASSIFY_SC_ACCESS = "hasReclassifySCAccess";
	static final String KEY_INTERNAL_USE = "Internal Use";
	static final String KEY_RELEASE = "Release";
	static final String KEY_TRUE = "TRUE";
	String strExceptionMessage = "Exception occurred during the operation, please check the logs !!!";
	static final String ERROR_PGIPSECURITY = "Exception in PGIPSecurityUtil";
	static final String PREFERENCE_BUSINESSUSECLASS = "preference_BusinessUseClass";
	static final String PREFERENCE_HIGHLYRESTRICTED=  "preference_HighlyRestrictedClass";
	static final String PREFERENCE_SECURITYCONTROL= "preference_SecurityControlClass";
	static final String RANGE_SECURITYCONTROL ="SecurityControl";
	static final String KEY_SECURITY_ID_LIST ="pgSecurityControlClasses";
	/**
	 * Method to get all IP and Security control classes related to an object
	 * @param context : eMatrix context object
	 * @param strInputData : String input data
	 * @return : String json with connected class info
	 * @throws FrameworkException 
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	String getAllClassesForObject(Context context, String strInputData) throws FrameworkException {

		JsonObjectBuilder jsonObjOutput = Json.createObjectBuilder();
		JsonArrayBuilder jsonIPClassArray = Json.createArrayBuilder();
		JsonArrayBuilder jsonSecurityClassArray = Json.createArrayBuilder();
		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInputData);
		
		String strObjectId = jsonInputData.getString(DomainConstants.SELECT_ID);
		String strObjSelectsString = jsonInputData.getString(PGWidgetConstants.KEY_OBJECT_SELECTS);
		String strObjType = null;
		String strObjPolicy = null;
		if (jsonInputData.containsKey(DomainConstants.SELECT_TYPE) && jsonInputData.containsKey(DomainConstants.SELECT_POLICY)) {
			strObjType = jsonInputData.getString(DomainConstants.SELECT_TYPE);
			strObjPolicy = jsonInputData.getString(DomainConstants.SELECT_POLICY);
		}
		if(UIUtil.isNotNullAndNotEmpty(strObjectId)) {
			JsonObject jsonAccess =PGWidgetUtil.convertMapToJsonObj(Json.createObjectBuilder().build(), checkClassifyAccess(context, strObjectId)) ;
			jsonObjOutput.add("Access", jsonAccess);
			if(UIUtil.isNotNullAndNotEmpty(strObjType) && UIUtil.isNotNullAndNotEmpty(strObjPolicy)) {
				try {
					strObjType 	= FrameworkUtil.getAliasForAdmin(context, PGWidgetConstants.KEY_TYPE, strObjType, true);
					strObjPolicy = FrameworkUtil.getAliasForAdmin(context, PGWidgetConstants.KEY_POLICY, strObjPolicy, true);
					
					Map mapClassification = getTypeAllowedClassificationForTypePolicy(context, strObjType, strObjPolicy);
					JsonObject jsonObjInfo = PGWidgetUtil.convertMapToJsonObj(Json.createObjectBuilder().build(), mapClassification);
					if(jsonObjInfo != null && jsonObjInfo.containsKey(KEY_ALLOWED_CLASSIFICATION_TYPE)) {
						jsonObjOutput.add(KEY_ALLOWED_CLASSIFICATION_TYPE, jsonObjInfo.get(KEY_ALLOWED_CLASSIFICATION_TYPE));
						if(jsonObjInfo.containsKey(KEY_IP_DEFAULT)) {
							jsonObjOutput.add(KEY_IP_DEFAULT, jsonObjInfo.get(KEY_IP_DEFAULT));
						}
					}
				} catch (FrameworkException | JDOMException | IOException e) {
				
					jsonObjOutput.add(PGWidgetConstants.KEY_ERROR, strExceptionMessage);
					return jsonObjOutput.build().toString();
				}
			}
			DomainObject dobCurrentObj = DomainObject.newInstance(context, strObjectId);
			StringList slObjectselects = StringUtil.split(strObjSelectsString, PGWidgetConstants.KEY_COMMA_SEPARATOR);
			slObjectselects.add(DomainConstants.SELECT_TYPE);
			slObjectselects.add(SELECT_HAS_CLASS_ACCESS);
			slObjectselects.add(PGWidgetConstants.SELECT_PHYSICAL_ID);
			
			StringList slRelSelects = new StringList(DomainRelationship.SELECT_ID);
			Pattern typePattern = new Pattern(ExportControlConstants.TYPE_IP_CONTROL_CLASS);
			typePattern.addPattern(ExportControlConstants.TYPE_SECURITY_CONTROL_CLASS);
			
			MapList mlAllConnectedClasses = dobCurrentObj.getRelatedObjects(context, DomainConstants.RELATIONSHIP_PROTECTED_ITEM, typePattern.getPattern(), slObjectselects, slRelSelects, true, false, (short) 1,
					null, null, 0);
			
			addClassInfoToJson(mlAllConnectedClasses, jsonIPClassArray, jsonSecurityClassArray);
		}
		
		jsonObjOutput.add(ExportControlConstants.TYPE_IP_CONTROL_CLASS, jsonIPClassArray);
		jsonObjOutput.add(ExportControlConstants.TYPE_SECURITY_CONTROL_CLASS, jsonSecurityClassArray);
		
		System.out.println("getAllClassesForObject jsonObjOutput 2 :: "+jsonObjOutput.build());
		return jsonObjOutput.build().toString();
	}
	
	public Map checkClassifyAccess(Context context, String strObjId) throws FrameworkException{
		Map<String,Boolean> mapRet = new HashMap<>();
		
		DomainObject dom = DomainObject.newInstance(context, strObjId);
		StringList slSelects = new StringList();
		slSelects.add(SELECT_ACCESS_MODIFY);
		slSelects.add(SELECT_ACCESS_CHANGEOWNER);
		slSelects.add(DomainConstants.SELECT_CURRENT);
		slSelects.add("attribute["+KEY_PGIPCLASSIFICATION+"]");
		
		Map<?, ?> map = dom.getInfo(context, slSelects);
		
		String strPgClassification = (String) map.get("attribute["+KEY_PGIPCLASSIFICATION+"]");
		
		boolean hasExportControl = PersonUtil.hasAssignment(context, ExportControlConstants.ROLE_EXPORT_CONTROL);
		boolean hasLicMngt = PersonUtil.hasAssignment(context, PropertyUtil.getSchemaProperty(context, "role_PG_LicenseManagement"));
		boolean hasBaipr = PersonUtil.hasAssignment(context, PropertyUtil.getSchemaProperty(context, "role_PG_BAIPR"));
		
		boolean showChange = (KEY_TRUE.equalsIgnoreCase(map.get(SELECT_ACCESS_MODIFY).toString()) && KEY_TRUE.equalsIgnoreCase(map.get(SELECT_ACCESS_CHANGEOWNER).toString()) && map.get(DomainConstants.SELECT_CURRENT) != KEY_RELEASE) ||
				hasExportControl || hasLicMngt || hasBaipr ;
		
		boolean showIPAdd = (KEY_TRUE.equalsIgnoreCase(map.get(SELECT_ACCESS_MODIFY).toString()) && KEY_TRUE.equalsIgnoreCase(map.get(SELECT_ACCESS_CHANGEOWNER).toString()) && map.get(DomainConstants.SELECT_CURRENT) != KEY_RELEASE &&
				UIUtil.isNotNullAndNotEmpty(strPgClassification) && !KEY_INTERNAL_USE.equals(strPgClassification)) ||
				hasExportControl || hasLicMngt || hasBaipr; 
		
		boolean showSCAdd = (KEY_TRUE.equalsIgnoreCase(map.get(SELECT_ACCESS_MODIFY).toString()) && 
				KEY_TRUE.equalsIgnoreCase(map.get(SELECT_ACCESS_CHANGEOWNER).toString()) && 
				map.get(DomainConstants.SELECT_CURRENT) != KEY_RELEASE) || hasExportControl || hasLicMngt || hasBaipr; 
		
		//Access required are same for (add & reclassify) , (change & remove)
		mapRet.put(KEY_HAS_ADD_IP_ACCESS, showIPAdd);
		mapRet.put(KEY_HAS_CHANGE_ACCESS, showChange);
		mapRet.put(KEY_HAS_REMOVE_ACCESS, showChange);
		mapRet.put(KEY_HAS_RECLASSIFY_IP_ACCESS, showIPAdd);
		mapRet.put(KEY_HAS_ADD_SC_ACCESS, showSCAdd);
		mapRet.put(KEY_HAS_RECLASSIFY_SC_ACCESS, showSCAdd);
		
		return mapRet;
	}
	
	public Map<String,Object> getTypeAllowedClassificationForTypePolicy(Context context, String strobjType, String strPolicy) throws FrameworkException, JDOMException, IOException {
		String strPageIPClassContent = MqlUtil.mqlCommand(context, "print page $1 select content dump", "pgIPClassTypePolicyMapping");
		SAXBuilder localSAXBuilder = new SAXBuilder();
		Document document = null;
		HashMap<String, Object> mapIPClass = new HashMap<>();
		if (strPageIPClassContent != null && !"".equals(strPageIPClassContent.trim())) {
			document = localSAXBuilder.build(new StringReader(strPageIPClassContent));
			XPath xpath = XPath.newInstance("/SECUIRTY_CLASS_MAPPING/TYPE[@name='" + strobjType + "']/POLICY[@name='" + strPolicy + "']");
			Element elemType = (Element) xpath.selectSingleNode(document);
			System.out.println("elemType :: "+elemType);
			String strIPClass = elemType.getAttribute("ipclass").getValue();
			System.out.println("strIPClass :: "+strIPClass);
			String strDefault = elemType.getAttribute("default").getValue();
			System.out.println("strDefault :: "+strDefault);
			mapIPClass.put(KEY_ALLOWED_CLASSIFICATION_TYPE, FrameworkUtil.split(strIPClass, ","));
		}
		return mapIPClass;
	}

	
	/**
	 * Method to add class information to Json object
	 * @param mlAllConnectedClasses
	 * @param jsonIPClassArray
	 * @param jsonSecurityClassArray
	 * @param slObjectselects 
	 */
	void addClassInfoToJson(MapList mlAllConnectedClasses, JsonArrayBuilder jsonIPClassArray,
			JsonArrayBuilder jsonSecurityClassArray) {
		for(int i=0; i<mlAllConnectedClasses.size(); i++) {
			Map<?, ?> objectMap = (Map<?, ?>) mlAllConnectedClasses.get(i);
			String strType = (String) objectMap.get(DomainConstants.SELECT_TYPE);
			JsonObject jsonObjInfo = PGWidgetUtil.convertMapToJsonObj(Json.createObjectBuilder().build(), objectMap);
			if(ExportControlConstants.TYPE_IP_CONTROL_CLASS.equals(strType)) {
				jsonIPClassArray.add(jsonObjInfo);
			} else {
				jsonSecurityClassArray.add(jsonObjInfo);
			}
		}
	}
	
	/**
	 * Method to get all IP Control classes for current user
	 * @param request : HttpServletRequest request param
	 * @param strInputData : String classification value Restricted or 'Highly Restricted' along with object selects
	 * @return : String json with list of IP Classes
	 * @throws FrameworkException,MatrixException 
	 * @throws Exception 
	 */
	String getIPClassesForUser(Context context, String strInputData) throws MatrixException  {

		JsonObjectBuilder jsonObjOutput = Json.createObjectBuilder();
		JsonObjectBuilder jsonObjResult = Json.createObjectBuilder();
		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInputData);
		
		String strClassificationValue = jsonInputData.getString(KEY_PGIPCLASSIFICATION);
		String strObjSelectsString = jsonInputData.getString(PGWidgetConstants.KEY_OBJECT_SELECTS);
		StringList slIPValues = new StringList();
	
		if(UIUtil.isNotNullAndNotEmpty(strClassificationValue)) {
			slIPValues.addAll(strClassificationValue.split(PGWidgetConstants.KEY_COMMA_SEPARATOR));
			for(int i = 0;i < slIPValues.size();i++){
				StringList tempList = new StringList();
				HashMap<String, String> hmArgsMap = new HashMap<>();
				
				hmArgsMap.put(KEY_CLASSIFICATION_VALUE, slIPValues.get(i));
				String[] strJPOArgs = JPO.packArgs(hmArgsMap);
	            StringList slIPClasslist = JPO.invoke(context, PROGRAM_IP_SECURITY_COMMON_UTIL, strJPOArgs, METHOD_GET_ALL_IP_CLASSES, strJPOArgs, StringList.class);
	            tempList.addAll(slIPClasslist);
	            getObjSelectsDetailsForClass(context, tempList, strObjSelectsString, jsonObjOutput);
	    		jsonObjResult.add( slIPValues.get(i), jsonObjOutput);		
			}
	
		}
		
		return jsonObjResult.build().toString();
	}
	
	/**
	 * Method to add get selectables details for classes
	 * @param context
	 * @param slClasslist
	 * @param strObjSelectsString
	 * @param jsonObjOutput
	 * @throws FrameworkException 
	 */
	void getObjSelectsDetailsForClass(Context context, StringList slClasslist, String strObjSelectsString,
			JsonObjectBuilder jsonObjOutput) throws FrameworkException {
		
		JsonArrayBuilder jsonArrayObj = Json.createArrayBuilder();
		String[] strObjectIds = getStringArrayFromStringList(slClasslist);
		
		StringList slObjectselects = StringUtil.split(strObjSelectsString, PGWidgetConstants.KEY_COMMA_SEPARATOR);
		slObjectselects.add(DomainConstants.SELECT_CURRENT);
		slObjectselects.add(SELECT_HAS_CLASS_ACCESS);
		slObjectselects.add(PGWidgetConstants.SELECT_PHYSICAL_ID);
		slObjectselects.add(DomainConstants.SELECT_TYPE);
		MapList mlClassInfoList = DomainObject.getInfo(context, strObjectIds, slObjectselects);
		String strIPClassPolicy = (String) ExportControlConstants.POLICY_EXPORT_CONTROL_CLASSIFICATION;
		String strStateActive = PropertyUtil.getSchemaProperty(null, DomainConstants.SELECT_POLICY, strIPClassPolicy, "state_Active");
		
		for(int i=0; i<mlClassInfoList.size(); i++) {
			Map<?, ?> objectMap = (Map<?, ?>) mlClassInfoList.get(i);
			String strState = (String) objectMap.get(DomainConstants.SELECT_CURRENT);
			if(strStateActive.equals(strState)) {
				JsonObject jsonObjInfo = PGWidgetUtil.convertMapToJsonObj(Json.createObjectBuilder().build(), objectMap);
				jsonArrayObj.add(jsonObjInfo);
			}
		}
		
		jsonObjOutput.add(KEY_CLASS_LIST, jsonArrayObj);
	}

	/**
	 * Method to convert StrinList to Array of String
	 * @param slIPClasslist
	 * @return
	 */
	String[] getStringArrayFromStringList(StringList slIPClasslist) {
		int iSize = slIPClasslist.size();
		String[] strOIDArray = new String[iSize];
		for(int i=0; i<iSize; i++) {
			strOIDArray[i] = slIPClasslist.get(i);
		}
		return strOIDArray;
	}	
	
	/**
	 * Method to get all Security Control classes for current user
	 * 
	 * @param request      : HttpServletRequest request param
	 * @param strInputData : String object selects
	 * @return : String json with list of Security Classes
	 * @throws MatrixException,FrameworkException 
	 * @throws Exception
	 */
	String getSecurityClassesForUser(Context context, String strInputData) throws MatrixException  {

		JsonObjectBuilder jsonObjOutput = Json.createObjectBuilder();
		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInputData);

		String strObjSelectsString = jsonInputData.getString(PGWidgetConstants.KEY_OBJECT_SELECTS);

		StringList slSecurityClasslist = JPO.invoke(context, PROGRAM_IP_SECURITY_COMMON_UTIL, null,
				METHOD_GET_ALL_SECURITY_CLASSES, null, StringList.class);

		getObjSelectsDetailsForClass(context, slSecurityClasslist, strObjSelectsString, jsonObjOutput);

		return jsonObjOutput.build().toString();
	}
	
	/**
	 *  Method to change the classification of the object from Restricted to 'Highly Restricted' and vice versa
	 * 
	 * @param context
	 * @param strInputData
	 * @return
	 * @throws FrameworkException 
	 * @throws Exception
	 */
	String changeClassification(Context context, String strInputData) throws FrameworkException {
		JsonObjectBuilder jsonObjOutput = Json.createObjectBuilder();
		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInputData);

		String strObjectId = jsonInputData.getString(DomainConstants.SELECT_ID);
		String strClassificationValue = jsonInputData.getString(KEY_PGIPCLASSIFICATION);
		String strClassIds = jsonInputData.getString(KEY_CLASS_LIST);
		String strObjSelectsString = jsonInputData.getString(PGWidgetConstants.KEY_OBJECT_SELECTS);
		try {
		
			 Map mpAccess = checkClassifyAccess(context, strObjectId);
			 boolean hasChangeAccess = (boolean) mpAccess.get(KEY_HAS_CHANGE_ACCESS);
			 if(hasChangeAccess){
				 
				Map<String, String> hmArgsMap = new HashMap<>();
				hmArgsMap.put(PGWidgetConstants.KEY_OBJECT_ID, strObjectId);
				hmArgsMap.put(KEY_CLASS_TYPE, SYMBOLIC_TYPE_IP_CONTROL_CLASS);
				hmArgsMap.put(KEY_PGIPCLASSIFICATION, strClassificationValue);

				if (RANGE_RESTRICTED.equals(strClassificationValue)) {
					hmArgsMap.put(KEY_RESTRICTED_ID_LIST, strClassIds);
				} else {
					hmArgsMap.put(KEY_HIR_ID_LIST, strClassIds);
				}

				Map<String, Map<String, String>> hmRequestMap = new HashMap<>();
				hmRequestMap.put(KEY_REQUEST_MAP, hmArgsMap);

				String[] strJPOArgs = JPO.packArgs(hmRequestMap);

				JPO.invoke(context, PROGRAM_IP_SECURITY_COMMON_UTIL, null, METHOD_CHANGE_CLASSIFICATION, strJPOArgs);		
				jsonObjOutput.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS);
			 }
			 else{		 
				 jsonObjOutput.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED);
				 jsonObjOutput.add(PGWidgetConstants.KEY_ERROR,"No access to change classification");
			 }
			
		} catch (Exception e) {
			jsonObjOutput.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED);
			if (UIUtil.isNotNullAndNotEmpty(e.getMessage())) {
				strExceptionMessage = e.getMessage();
			}
			jsonObjOutput.add(PGWidgetConstants.KEY_ERROR, strExceptionMessage);
			logger.log(Level.SEVERE, ERROR_PGIPSECURITY, e);
		}
		JsonObjectBuilder jsonObjInput = Json.createObjectBuilder();
		jsonObjInput.add(PGWidgetConstants.KEY_OBJECT_SELECTS, strObjSelectsString);
		jsonObjInput.add(DomainConstants.SELECT_ID, strObjectId);
		jsonObjOutput.add(PGWidgetConstants.KEY_OUTPUT, getAllClassesForObject(context, jsonObjInput.build().toString()));
		return jsonObjOutput.build().toString();
	}
	
	/**
	 * 
	 * Method adds the selected IP or Security Control Classes to the object.
	 * @param context
	 * @param args
	 * @return
	 * @throws FrameworkException 
	 * @throws Exception
	 */
	String addClassToObject(Context context, String strInputData) throws FrameworkException {

		JsonObjectBuilder jsonOutput = Json.createObjectBuilder();
		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInputData);

		String strObjId = jsonInputData.getString(DomainConstants.SELECT_ID);
		String strClassIds = jsonInputData.getString(KEY_CLASS_LIST);
		StringList slClassIdList = StringUtil.split(strClassIds, PGWidgetConstants.KEY_COMMA_SEPARATOR);
		String strObjSelectsString = jsonInputData.getString(PGWidgetConstants.KEY_OBJECT_SELECTS);
		String strInputClassName = jsonInputData.getString("InputClassName");
		boolean bHasAddAccess = false;
		Map mpAccess = checkClassifyAccess(context, strObjId);
		if(strInputClassName.equals("IPClass")){	
			bHasAddAccess = (boolean) mpAccess.get(KEY_HAS_ADD_IP_ACCESS);
		}
		else if(strInputClassName.equals("SecurityClass")){	
			bHasAddAccess = (boolean) mpAccess.get(KEY_HAS_ADD_SC_ACCESS);
		}
		StringBuilder sbAlert = new StringBuilder();

		if(bHasAddAccess){
			for (int i = 0; i < slClassIdList.size(); i++) {
				String strClassId = slClassIdList.get(i);
				try {
					DomainRelationship.connect(context, strClassId, DomainConstants.RELATIONSHIP_PROTECTED_ITEM, strObjId, true);

				} catch (Exception e) {
					bHasAddAccess = false;
					String errorMsg = e.getMessage();
					sbAlert.append(errorMsg).append("\n\n");
				}
			}
		}
		

		if (bHasAddAccess) {
			jsonOutput.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS);
			
		} else {
			if(sbAlert.length() < 1){
				sbAlert.append("No access to add classification");
			}
			jsonOutput.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED);
			jsonOutput.add(PGWidgetConstants.KEY_ERROR, sbAlert.toString());
		}
		JsonObjectBuilder jsonObjInput = Json.createObjectBuilder();
		jsonObjInput.add(PGWidgetConstants.KEY_OBJECT_SELECTS, strObjSelectsString);
		jsonObjInput.add(DomainConstants.SELECT_ID, strObjId);
		jsonOutput.add(PGWidgetConstants.KEY_OUTPUT, getAllClassesForObject(context, jsonObjInput.build().toString()));
		return jsonOutput.build().toString();

	}
	
	/**
	 * Method to reclassify object for a new Class
	 * @param context
	 * @param strInputData
	 * @return
	 * @throws FrameworkException
	 */
	String reclassifyObject(Context context, String strInputData) throws FrameworkException {
		boolean isActiveTransaction = false;
		JsonObjectBuilder jsonOutput = Json.createObjectBuilder();
		try {
			isActiveTransaction = ContextUtil.isTransactionActive(context);
			if (!isActiveTransaction) {
				isActiveTransaction = ContextUtil.startTransaction(context, true);
			}

			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInputData);

			String strObjId = jsonInputData.getString(DomainConstants.SELECT_ID);
			String strRelIdToDelete = jsonInputData.getString(KEY_RELATIONSHIP_ID);
			String strNewClassId = jsonInputData.getString(KEY_NEW_ID);
			String strObjSelectsString = jsonInputData.getString(PGWidgetConstants.KEY_OBJECT_SELECTS);
			DomainRelationship.disconnect(context, strRelIdToDelete);

			DomainRelationship.connect(context, strNewClassId, DomainConstants.RELATIONSHIP_PROTECTED_ITEM, strObjId, true);

		
			if (isActiveTransaction) {
				ContextUtil.commitTransaction(context);
			}
			jsonOutput.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS);
			JsonObjectBuilder jsonObjInput = Json.createObjectBuilder();
			jsonObjInput.add(PGWidgetConstants.KEY_OBJECT_SELECTS, strObjSelectsString);
			jsonObjInput.add(DomainConstants.SELECT_ID, strObjId);
			jsonOutput.add(PGWidgetConstants.KEY_OUTPUT, getAllClassesForObject(context, jsonObjInput.build().toString()));
		} catch (Exception e) {
			if (isActiveTransaction) {
				ContextUtil.abortTransaction(context);
			}
			String errorMsg = e.getMessage();
			jsonOutput.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED);
			jsonOutput.add(PGWidgetConstants.KEY_ERROR, errorMsg);
			throw e;
		}

		return jsonOutput.build().toString();

	}

	/**
	 * Method to remove the selected class. This method is based on
	 * EXCSecurityRuleRemove.jsp which will be invoked on remove operation as part
	 * of OOTB Remove command.
	 * 
	 * @param context
	 * @param strInputData
	 * @return
	 * @throws FrameworkException 
	 * @throws Exception
	 */
	String removeClass(Context context, String strInputData) throws FrameworkException {
		JsonObjectBuilder jsonOutput = Json.createObjectBuilder();
		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInputData);

		String strObjectId = jsonInputData.getString(DomainConstants.SELECT_ID);
		String strRelIdToDelete = jsonInputData.getString(KEY_RELATIONSHIP_ID);
		String strSelectedClassIds = jsonInputData.getString(KEY_SELECTED_CLASS_IDS);
		String strClassType = jsonInputData.getString(DomainConstants.SELECT_TYPE);
		String strObjSelectsString = jsonInputData.getString(PGWidgetConstants.KEY_OBJECT_SELECTS);
		StringList slRelIdList = StringUtil.split(strRelIdToDelete, PGWidgetConstants.KEY_COMMA_SEPARATOR);
		int numberOfSelectedClasses = slRelIdList.size();

		String strErroMessage = validateDeleteOperation(context, strObjectId, strSelectedClassIds, strClassType,
				numberOfSelectedClasses);
		if (UIUtil.isNotNullAndNotEmpty(strErroMessage)) {
			jsonOutput.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED);
			jsonOutput.add(PGWidgetConstants.KEY_ERROR, strErroMessage);
			return jsonOutput.build().toString();
		}

		boolean isContextPushed = false;
		try {
			if (UIUtil.isNotNullAndNotEmpty(strObjectId)) {
				DomainObject domObjClass = DomainObject.newInstance(context);
				domObjClass.setId(strObjectId);
				domObjClass.open(context);

				// If strArrRowId is not null, then disconnecting the selected Rules.
				if (numberOfSelectedClasses > 0) {
					// P&G: Code segment added for ALM Defect 3134 (Logged in user is unable to
					// Remove unauthorized classes) Start
					ContextUtil.pushContext(context);
					isContextPushed = true;
					// DSM (DS) 2015x.5 ALM 16289 : INC0760610 User Agent removed IP class off a
					// released formulation exposing the formula to everyone. - End
					for (int i = 0; i < numberOfSelectedClasses; i++) {
						String strRelId = slRelIdList.get(i);
						domObjClass.disconnect(context, new Relationship(strRelId));
					
					} // end for
				} // end if
				domObjClass.close(context);
			} // end if
			jsonOutput.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS);
			JsonObjectBuilder jsonObjInput = Json.createObjectBuilder();
			jsonObjInput.add(PGWidgetConstants.KEY_OBJECT_SELECTS, strObjSelectsString);
			jsonObjInput.add(DomainConstants.SELECT_ID, strObjectId);
			jsonOutput.add(PGWidgetConstants.KEY_OUTPUT, getAllClassesForObject(context, jsonObjInput.build().toString()));
		} catch (Exception excMessage) {
			String errorMsg = excMessage.getMessage();
			jsonOutput.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED);
			jsonOutput.add(PGWidgetConstants.KEY_ERROR, errorMsg);
			logger.log(Level.SEVERE, ERROR_PGIPSECURITY, excMessage);
		
			
		}
		// P&G: Code segment added for ALM Defect 3134 (Logged in user is unable to
		// Remove unauthorized classes) Start
		finally {
			if (isContextPushed) {
				ContextUtil.popContext(context);
			}
		}

		return jsonOutput.build().toString();

	}

	/**
	 * Method to validate the delete operation
	 * @param context
	 * @param strObjectId
	 * @param strSelectedClassIds
	 * @param strClassType
	 * @param numberOfSelectedClasses
	 * @return
	 * @throws FrameworkException 
	 */
	String validateDeleteOperation(Context context, String strObjectId, String strSelectedClassIds,
			String strClassType, int numberOfSelectedClasses) throws FrameworkException {
		String strReturnMsg = "";
		if(ExportControlConstants.TYPE_IP_CONTROL_CLASS.equals(strClassType) && UIUtil.isNotNullAndNotEmpty(strObjectId)){
			DomainObject domToObj = DomainObject.newInstance(context, strObjectId);

			StringList objectSelects = new StringList(4);
			objectSelects.addElement(DomainConstants.SELECT_ID);
			objectSelects.addElement(PGWidgetConstants.SELECT_PHYSICAL_ID);
			objectSelects.addElement(DomainConstants.SELECT_NAME);
			objectSelects.addElement(SELECT_HAS_CLASS_ACCESS);
			
			MapList mapConnectedClassifiedItem = domToObj.getRelatedObjects(context, DomainConstants.RELATIONSHIP_PROTECTED_ITEM, strClassType, objectSelects, null, true, false, (short) 1, null, null,0);
		
			boolean hasFromDisconntAccess = false;
			if(mapConnectedClassifiedItem.size() == numberOfSelectedClasses)
			{
				strReturnMsg = EnoviaResourceBundle.getProperty(context, "emxExportControlStringResource", context.getLocale(),"pgExportControl.Alert.RemoveIPControlClass");
				return strReturnMsg;
			} 
			//P&G: Code segment added for ALM Defect 3134 (Logged in user is unable to Remove unauthorized classes) Start
			else {
				//P&G: To bypass the below check for special admin roles(Export Control, BAIPR and LicenseManagement roles): Start		
				boolean isSpecialRoleExists = false;
				final String ROLE_EXPORT_CONTROL = PropertyUtil.getSchemaProperty(context, "role_ExportControl");
				final String ROLE_LICENSE_MANAGEMENT = PropertyUtil.getSchemaProperty(context, "role_PG_LicenseManagement");
				final String ROLE_BAIPR = PropertyUtil.getSchemaProperty(context, "role_PG_BAIPR");
				StringList selects = new StringList(DomainConstants.SELECT_NAME);
				MapList securiyContextChoices = PersonUtil.getSecurityContexts(context, context.getUser(), selects);
				int iSecurityContextChoicesSize = securiyContextChoices.size();

				String strSCRole = "";
				for (int count = 0; count < iSecurityContextChoicesSize; count++)
				{
					  Map<?,?>scMap = (Map<?, ?>)securiyContextChoices.get(count);
					  StringList strSecurityContextList = StringUtil.split((String)scMap.get(DomainConstants.SELECT_NAME), ".");
					  strSCRole = strSecurityContextList.get(0);
					  if(ROLE_EXPORT_CONTROL.equals(strSCRole) || ROLE_LICENSE_MANAGEMENT.equals(strSCRole) || ROLE_BAIPR.equals(strSCRole)) {
							isSpecialRoleExists = true;
							break;
					  }
				}
				//P&G: To bypass the below check for special admin roles(Export Control, BAIPR and LicenseManagement roles): End
				if(!isSpecialRoleExists) {
					StringList strUnauthorizedClassList = new StringList();
					Iterator<?> mapItr = mapConnectedClassifiedItem.iterator();
					String strClassID = "";
					String hasFromDisconnectAccess = "";
					StringBuilder sb= new StringBuilder();
					while(mapItr.hasNext())
					{
						Map<?,?> objMap = (Map<?, ?>) mapItr.next();
						strClassID = (String)objMap.get(PGWidgetConstants.SELECT_PHYSICAL_ID);
						hasFromDisconnectAccess = (String)objMap.get(SELECT_HAS_CLASS_ACCESS);
						if("FALSE".equalsIgnoreCase(hasFromDisconnectAccess)) {
							strUnauthorizedClassList.addElement((String)objMap.get(DomainConstants.SELECT_NAME));
						}
						if ((!strSelectedClassIds.contains(strClassID)) && "TRUE".equalsIgnoreCase(hasFromDisconnectAccess)) {
							hasFromDisconntAccess = true;
							break;
						}
					}
					if(!hasFromDisconntAccess) {
						int listSize = strUnauthorizedClassList.size();
						for (int j = 0; j < listSize; j++)
						{
							sb.append(strUnauthorizedClassList.get(j));
							if(j < listSize-1) {
								sb.append(", ");
							}
						}
						strReturnMsg = EnoviaResourceBundle.getProperty(context, "emxExportControlStringResource", context.getLocale(),"pgExportControl.Alert.RemoveIPControlClass2")+sb.toString();						
						return strReturnMsg;
					}
				}
			} 
			//P&G: Code segment added for ALM Defect 3134 (Logged in user is unable to Remove unauthorized classes) End
		}
		
		return strReturnMsg;
	}
	
	String getUserPreferenceIPClass(Context context) throws FrameworkException{
		JsonObjectBuilder jsonObjOutput = Json.createObjectBuilder();
		String strBusinessUsePrefvalue = PropertyUtil.getAdminProperty(context, PGWidgetConstants.KEY_PERSON, context.getUser(), PREFERENCE_BUSINESSUSECLASS);
		if(strBusinessUsePrefvalue == null) {
			strBusinessUsePrefvalue = "";
		} else {
			strBusinessUsePrefvalue = strBusinessUsePrefvalue.replace(";", "|");
		}

		String strHiRPrefvalue = PropertyUtil.getAdminProperty(context,PGWidgetConstants.KEY_PERSON, context.getUser(), PREFERENCE_HIGHLYRESTRICTED);
		if(strHiRPrefvalue == null) {
			strHiRPrefvalue = "";
		} else {
			strHiRPrefvalue = strHiRPrefvalue.replace(";", "|");
		}
		
		String strSCPrefvalue = PropertyUtil.getAdminProperty(context, PGWidgetConstants.KEY_PERSON, context.getUser(), PREFERENCE_SECURITYCONTROL);
		if(strSCPrefvalue == null) {
			strSCPrefvalue = "";
		} else {
			strSCPrefvalue = strSCPrefvalue.replace(";", "|");
		}
		jsonObjOutput.add(RANGE_RESTRICTED, strBusinessUsePrefvalue);
		jsonObjOutput.add(RANGE_HIGHLY_RESTRICTED, strHiRPrefvalue);
		jsonObjOutput.add(RANGE_SECURITYCONTROL, strSCPrefvalue);
		return jsonObjOutput.build().toString();
	}
	
	
	
	/**
	 * Set Preference for IP Class
	 * @param context
	 * @param strInputData
	 * @return
	 * @throws Exception
	 */
	String setIPPreferencesBothClasses(Context context,String strInputData)  {
		JsonObjectBuilder jsonObjOutput = Json.createObjectBuilder();
		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInputData);
		String strHiRClasses = jsonInputData.getString(KEY_HIR_ID_LIST);
		String strBusinessUseClasses = jsonInputData.getString(KEY_RESTRICTED_ID_LIST);
		
		try {
			setPreferenceOnClass(context, strBusinessUseClasses, PREFERENCE_BUSINESSUSECLASS);
			setPreferenceOnClass(context, strHiRClasses, PREFERENCE_HIGHLYRESTRICTED);
			jsonObjOutput.add(PGWidgetConstants.KEY_OUTPUT, getUserPreferenceIPClass(context));
			jsonObjOutput.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS);
		} catch (Exception e) {
			jsonObjOutput.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED);
			if (UIUtil.isNotNullAndNotEmpty(e.getMessage())) {
				strExceptionMessage = e.getMessage();
			}
			jsonObjOutput.add(PGWidgetConstants.KEY_ERROR, strExceptionMessage);
			logger.log(Level.SEVERE, ERROR_PGIPSECURITY, e);
		}
		
	
		return jsonObjOutput.build().toString();
	}
	
	/**
	 * Set Preference for Security Class
	 * @param context
	 * @param strInputData
	 * @return
	 * @throws Exception
	 */
	String setIPPreferencesSCClasses(Context context,String strInputData) {
		JsonObjectBuilder jsonObjOutput = Json.createObjectBuilder();
		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInputData);
		String strSCClasses = jsonInputData.getString(KEY_SECURITY_ID_LIST);
		
		try {
			setPreferenceOnClass(context, strSCClasses, PREFERENCE_SECURITYCONTROL);
			
			jsonObjOutput.add(PGWidgetConstants.KEY_OUTPUT, getUserPreferenceIPClass(context));
			jsonObjOutput.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS);
		} catch (Exception e) {
			jsonObjOutput.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED);
			if (UIUtil.isNotNullAndNotEmpty(e.getMessage())) {
				strExceptionMessage = e.getMessage();
			}
			jsonObjOutput.add(PGWidgetConstants.KEY_ERROR, strExceptionMessage);
			logger.log(Level.SEVERE, ERROR_PGIPSECURITY, e);
		}
		
	
		return jsonObjOutput.build().toString();
	}
	
	public void setPreferenceOnClass(Context context, String strClassNames, String strPropertyName) throws FrameworkException
	{
		if (strClassNames != null) {	
			strClassNames = strClassNames.replace("|", ";");
			if(!"".equals(strClassNames)) {
				PropertyUtil.setAdminProperty(context, "Person", context.getUser(), strPropertyName, strClassNames);
			} else {
				PropertyUtil.removeAdminProperty(context, "Person", context.getUser(), strPropertyName);
			}
	    }
	}
	
}

package com.pg.widgets.myfavorites;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.StringList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.apps.domain.DomainObject;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.SetUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;

public class PGFavoritesUtil {

	static final String MAX_STRING_LENGTH = "org.apache.johnzon.max-string-length";
	static final String VALUE_KILOBYTES = "262144";
	static final String KEY_PIPE_SEPARATOR = "|";
	static final String KEY_COMMA_SEPARATOR = ",";
	static final String KEY_RELID = "relId";
	static final String MEMBER_ID = "memberId";
	static final String COLLECTION_ID = "collectionId";
	static final String PHYSICAL_ID = "physicalid";
	static final String OBJECT_TYPE = "objectType";
	static final String DISPLAY_TYPE = "displayType";
	static final String DISPLAY_NAME = "displayName";
	/**
	 * Add the items to selected collection
	 * @param context
	 * @param strData
	 * @return
	 * @throws Exception
	 */
	public static String addToCollection(Context context, String strData) throws Exception {
		JsonObject jsonInput = getJsonFromJsonString(strData);
		String strMemberIds = jsonInput.getString(MEMBER_ID);
		String strCollectionId = jsonInput.getString(COLLECTION_ID);
		String selectedCollectionName = SetUtil.getCollectionName(context, strCollectionId);
		StringList memberIds = FrameworkUtil.split(strMemberIds, KEY_COMMA_SEPARATOR);
		SetUtil.append(context, selectedCollectionName, memberIds);
		return getCollectionItems(context, strCollectionId);		
	}
	
	/**
	 * Remove the items from selected collection
	 * @param context
	 * @param strData
	 * @return
	 * @throws Exception
	 */
	public static String removeFromCollection(Context context, String strData) throws Exception {
		JsonObject jsonInput = getJsonFromJsonString(strData);
		String strMemberIds = jsonInput.getString(MEMBER_ID);
		String strCollectionId = jsonInput.getString(COLLECTION_ID);
		String strCollectionName = SetUtil.getCollectionName(context, strCollectionId);
		StringList memberIds = FrameworkUtil.split(strMemberIds, KEY_COMMA_SEPARATOR);
		SetUtil.removeMembers(context, strCollectionName, memberIds, false);
		return getCollectionItems(context, strCollectionId);		
	}
	
	
	/**
	 * Return the items added to selected collection	
	 * @param context
	 * @param collectionId
	 * @return
	 * @throws Exception
	 */
	static String getCollectionItems(Context context, String collectionId) throws Exception {
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(KEY_RELID, collectionId);
		MapList mlCollectionItems = JPO.invoke(context, "emxAEFCollection", null, "getObjects", JPO.packArgs(paramMap),
				MapList.class);

		Map dataMap = null;
		int iSize = mlCollectionItems.size();
		JsonArrayBuilder jsonArrayObj = Json.createArrayBuilder();
		if(iSize > 0)
		{
			String[] objectIds = new String[iSize];
			for (int i = 0; i < iSize; i++) {
				dataMap = (Map) mlCollectionItems.get(i);			
				objectIds[i] = (String) dataMap.get(DomainConstants.SELECT_ID);			
			}
			
			StringList slObjectselects = new StringList(6);
			slObjectselects.add(DomainConstants.SELECT_TYPE);
			slObjectselects.add(DomainConstants.SELECT_NAME);
			slObjectselects.add(DomainConstants.SELECT_REVISION);
			slObjectselects.add(DomainConstants.SELECT_CURRENT);
			slObjectselects.add(DomainObject.SELECT_ATTRIBUTE_TITLE);
			slObjectselects.add(PHYSICAL_ID);

			MapList mlItems = DomainObject.getInfo(context, objectIds, slObjectselects);
			String strLanguage = context.getSession().getLanguage();
			String strTypeDisplayName = null;
			JsonObject jsonObjInfo = null;
			Map<String, String> objectMap = null;
			for (int i = 0; i < mlItems.size(); i++) {
				objectMap = (Map<String, String>) mlItems.get(i);				
				strTypeDisplayName = EnoviaResourceBundle.getTypeI18NString(context, (String) objectMap.get(DomainConstants.SELECT_TYPE), strLanguage);
				objectMap.put(OBJECT_TYPE, objectMap.get(DomainConstants.SELECT_TYPE));
				objectMap.put(DISPLAY_NAME, objectMap.get(DomainConstants.SELECT_NAME));
				objectMap.put(DISPLAY_TYPE, strTypeDisplayName);				
				jsonObjInfo = convertMapToJsonObj(Json.createObjectBuilder().build(), objectMap);
				jsonArrayObj.add(jsonObjInfo);
			}
		}
		JsonObjectBuilder output = Json.createObjectBuilder();	
		output.add("data", jsonArrayObj.build());
		return output.build().toString();
	}

	/**
	 * This method is used to convert StringList to pipe separated String
	 * 
	 * @param slSelectValuelist : StringList to be converted
	 * @return : Pipe line separated String
	 */
	static String getStringFromSL(StringList slSelectValuelist) {

		StringBuilder sbSelectValue = new StringBuilder();
		String strSelectValue = "";
		String strReturnValue = "";
		for (int i = 0; i < slSelectValuelist.size(); i++) {
			strSelectValue = slSelectValuelist.get(i);
			sbSelectValue.append(strSelectValue).append(KEY_PIPE_SEPARATOR);
		}

		strReturnValue = sbSelectValue.toString();

		if (UIUtil.isNotNullAndNotEmpty(strReturnValue)) {
			strReturnValue = strReturnValue.substring(0, strReturnValue.length() - 1);
		}

		return strReturnValue;
	}

	/**
	 * Method used convert Map to JsonObject
	 * 
	 * @param inputJsonObj : JsonObject in which converted JsonObject from Map will
	 *                     be merged
	 * @param inputMap     : Map to converted to JsonObject
	 * @return : JsonObject merged with converted Map and parent inputJsonObj
	 */
	static JsonObject convertMapToJsonObj(JsonObject inputJsonObj, Map<?, ?> inputMap) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		inputJsonObj.entrySet().forEach(e -> builder.add(e.getKey(), e.getValue()));
		inputMap.entrySet()
				.forEach(e -> builder.add((String) e.getKey(),
						((e.getValue() instanceof StringList) ? getStringFromSL((StringList) e.getValue())
								: (String.valueOf(e.getValue())))));
		return builder.build();
	}	

	/**
	 * Method to convert String json which is usually the value of attributes
	 * ReportInputInformation, pgReportFormData and pgReportOutputData etc to
	 * JsonObject
	 * 
	 * @param strJsonString : String json (usually the value of attributes
	 *                      ReportInputInformation, pgReportFormData and
	 *                      pgReportOutputData etc)
	 * @return : JsonObject created from String json
	 */
	static JsonObject getJsonFromJsonString(String strJsonString) {
		StringReader srJsonString = new StringReader(strJsonString);
		Map<String, String> configMap = new HashMap<>();
		configMap.put(MAX_STRING_LENGTH, VALUE_KILOBYTES);
		JsonReaderFactory factory = Json.createReaderFactory(configMap);
		try (JsonReader jsonReader = factory.createReader(srJsonString)) {
			return jsonReader.readObject();
		} finally {
			srJsonString.close();
		}
	}

}

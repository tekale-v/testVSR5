package com.pg.dsm.support_tools.support_actions.util;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import com.matrixone.apps.awl.util.Access;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MessageUtil;

import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.db.Page;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class SupportUtil implements SupportConstants {

	public static String getValue(Map<String, String> map, String key) throws Exception {
		String val = (String)map.get(key);
		return val == null ? "" : val.trim();
	}
	/**DSM 2018x.3 - On Demand Support Tools 
	 * @about -  Method to get the type Symbolic name
	 * @return String 
	 * @throws Exception
	 */
	public static String getTypeSymbolicName(Context context, String type) throws Exception {
		if(isNullEmpty(type)) 
			throw new IllegalArgumentException();
		return FrameworkUtil.getAliasForAdmin(context, SELECT_TYPE, type, true);
	}
	/**DSM 2018x.3 - On Demand Support Tools 
	 * @about -  Method to remove spaces
	 * @return String 
	 * @throws Exception
	 */
	public static String removeAllSpaces(String paramString) throws Exception {
		if(isNullEmpty(paramString)) 
			throw new IllegalArgumentException();
		return paramString.replaceAll("\\s+","");
	}
	/**DSM 2018x.3 - On Demand Support Tools 
	 * @about -  Method to get the List of types seperated by comma
	 * @return StringList
	 * @throws Exception
	 */
	public static StringList getList(String paramString) throws Exception {
		if(isNullEmpty(paramString)) 
			throw new IllegalArgumentException();
		return FrameworkUtil.split(removeAllSpaces(paramString), SYMBOL_COMMA);
	}
	public static boolean isNullEmpty(String str) { 
		return (str == null || str.trim().length() == 0 || NULL.equalsIgnoreCase(str)); 
	}
	public static boolean isNotNullEmpty(String str) { 
		return !isNullEmpty(str); 
	}
	/**DSM 2018x.3 - On Demand Support Tools 
	 * @about -  Method to convert the JSON response to Map
	 * @return JSONObject 
	 * @throws Exception
	 */
	public static JSONObject convertMapToJSON(Map<String, String> info) throws Exception {
		if(info == null) 
			throw new IllegalArgumentException();

		JSONObject jsonObj = new JSONObject();
		Iterator itr = info.keySet().iterator();
		String key = EMPTY_STRING;
		String val = EMPTY_STRING;
		while(itr.hasNext()) {
			key = (String)itr.next();
			val = (String)info.get(key);
			jsonObj.put(key, val);
		}
		return jsonObj;
	}
	/**DSM 2018x.3 - On Demand Support Tools 
	 * @about - This is helper method to configure the page file 
	 * @param context 
	 * @return void 
	 * @throws Exception
	 * @since DSM 2018x.3
	 */
	public static Properties loadSupportActionConfigPage(Context context) throws Exception {
		Properties properties = new Properties();
		try {
			Page page = new Page(ON_DEMAND_SUPPORT_ACTION_CONFIG);
			page.open(context);
			String content = page.getContents(context);
			page.close(context);
			properties.load(new StringReader(content));
		} catch(Exception e) {
			e.printStackTrace();
		}
		return properties;
	}
	/**
	 * @about - Helper method to check read & show access. 
	 * @param context
	 * @param args - array of arguments
	 * @since DSM 2018x.3 - On Demand Support Tools
	 */
	public static boolean hasAccess(Context context, String objectId, String read, String show) throws Exception {
		return Access.hasAccess(context, objectId, read, show);
	}
	/**
	 * @about - Helper method to check read & show access. 
	 * @param context
	 * @param args - array of arguments
	 * @since DSM 2018x.3 - On Demand Support Tools
	 */
	public static StringList getTypes(String types) throws Exception {
		return FrameworkUtil.split(types.trim(), SYMBOL_COMMA);
	}
	
	/**
	 * @about - Helper method to check object limit
	 * @param context
	 * @param args - array of arguments
	 * @since DSM 2018x.3 - On Demand Support Tools
	 */
	public static String checkSupportActionLimit(Context context, String objectIds) throws Exception {
		Properties properties = SupportUtil.loadSupportActionConfigPage(context);
		StringList objectList = FrameworkUtil.split(objectIds, SYMBOL_PIPE);
		int count = objectList.size();
		int limit = Integer.valueOf(properties.getProperty("SupportAction.Object.Limit").trim());
		if(count>limit) {
			return MessageUtil.getMessage(context, SUPPORT_LIMIT_EXCEEDS_MESSAGE, new String[] {COUNT, LIMIT}, new String[] {String.valueOf(count), String.valueOf(limit)}, EMPTY_STRING ); 
		}
		return EMPTY_STRING;
	}
	/**
	 * @about - Helper method to get choices for support actions. 
	 * @param context
	 * @param args -  array of arguments
	 * @return Map 
	 * @since DSM 2018x.3 - On Demand Support Tools
	 */
	public static Map getSupportChoices(Context context, String[] args) throws Exception {
		Map returnMap = new HashMap();
		StringList fieldChoicesList = new StringList();
		StringList fieldDisplayChoicesList = new StringList();
		for (SupportType supportType : SupportType.values()) {
			fieldChoicesList.addElement(supportType.getAction());
			fieldDisplayChoicesList.addElement(supportType.getDisplayName());	
		}
		returnMap.put(FIELD_CHOICES, fieldChoicesList);
		returnMap.put(FIELD_DISPLAY_CHOICES,fieldDisplayChoicesList);
		return returnMap;
	}
	/**
	 * @about - This is helper method to build choices for sub-roll-up. 
	 * @param context
	 * @param args - array of arguments.
	 * @return String - drop-down containing actions.
	 * @throws Exception
	 * @since DSM 2018x.3 - On Demand Support Tools
	 */
	public static String getRollupChoices(Context context, String[] args)throws Exception {
		Map programMap = (HashMap) JPO.unpackArgs(args);
		Map requestMap=(HashMap)programMap.get(REQUEST_MAP);
		Map fieldmap=(HashMap)programMap.get(FIELD_MAP);
		String rollupField = (String)fieldmap.get(SELECT_NAME);

		StringBuilder choices = new StringBuilder();
		if(SupportUtil.isNotNullEmpty(rollupField)) {
			choices.append("<select name=\"");
			choices.append(rollupField);
			choices.append("\" id=\""+rollupField);
			choices.append("\" size=\"9\" multiple>");

			for (RollupType rollupType : RollupType.values()) {
				choices.append("<option value='" + rollupType.getAction() + "'>"+ rollupType.getDisplayName() +"</option>");	
			}
			choices.append("</select>");
		}
		return choices.toString();    
	}
	/**DSM 2018x.3 - On Demand Support Tools 
	 * @about - This is helper method to get the JSON response 
	 * @param context
	 * @param args - array of arguments.
	 * @return Maplist - proccessed MapList
	 * @throws Exception
	 * @since DSM 2018x.3
	 */
	public static MapList getSupportActionSummary(Context context, String[] args) throws Exception {
		MapList objectList = new MapList();
		try {
			Map programMap = (HashMap) JPO.unpackArgs(args);
			String jsonRespose = (String)programMap.get(SUPPORT_ACTION_JSON_RESPONSE_KEY);
			if(SupportUtil.isNotNullEmpty(jsonRespose)) {
				JSONObject jsonObj = new JSONObject(jsonRespose);
				JSONArray jsonArray = jsonObj.getJSONArray(SUPPORT_ACTION_JSON_RESPONSE);
				Map<String, String> temp = null;
				JSONObject eachJson = null;
				Iterator itr = null;
				String key = EMPTY_STRING;
				int jsonArraySize = jsonArray.length();
				for(int i=0; i<jsonArraySize; i++) {
					eachJson = jsonArray.getJSONObject(i);
					temp = new HashMap<String, String>();
					itr = eachJson.keys();
					while(itr.hasNext()) {
						key = (String)itr.next();
						temp.put(key, (String)eachJson.get(key));
					}
					objectList.add(temp);
				}
			}
		} catch(Exception e) {
			throw e;
		}
		return objectList;
	}
	/**DSM 2018x.3 - On Demand Support Tools 
	 * @about - This is helper method to get the Summary Result for table.
	 * @param context
	 * @param args - array of arguments.
	 * @return Vector 
	 * @throws Exception
	 * @since DSM 2018x.3
	 */
	public static Vector<String> getResultSummary(Context context, String[] args) throws Exception {
		Vector<String> summaryVector= new Vector<String>();
		try {
			Map paramMap=(Map)JPO.unpackArgs(args);
			MapList objectList = (MapList) paramMap.get(OBJECT_LIST);
			if(objectList != null && !objectList.isEmpty()) {
				Iterator itr = objectList.iterator();
				while(itr.hasNext()) {
					summaryVector.addElement(SupportUtil.getValue((Map)itr.next(), SUPPORT_ACTION_RESULT));
				}
			}
		} catch(Exception e) {
			throw e;
		}
		return summaryVector;
	}
	/**DSM 2018x.3 - On Demand Support Tools 
	 * @about - This is helper method to get the Summary Display name for table.
	 * @param context
	 * @param args - array of arguments.
	 * @return Vector 
	 * @throws Exception
	 * @since DSM 2018x.3
	 */
	public static Vector<String> getActionSummary(Context context, String[] args) throws Exception {
		Vector<String> actionVector= new Vector<String>();
		try {
			Map paramMap=(Map)JPO.unpackArgs(args);
			MapList objectList = (MapList) paramMap.get(OBJECT_LIST);
			if(objectList != null && !objectList.isEmpty()) {
				Iterator itr = objectList.iterator();
				while(itr.hasNext()) {
					actionVector.addElement(SupportUtil.getValue((Map)itr.next(), SUPPORT_ACTION_DISPLAY_NAME));
				}
			}
		} catch(Exception e) {
			throw e;
		}
		return actionVector;
	}
	/**
	 * @about - Helper method to get objects info. 
	 * @param context
	 * @param objectId - array of arguments.
	 * @return MapList - object info
	 * @throws Exception
	 * @since DSM 2018x.3 - On Demand Support Tools
	 */
	public static MapList getActionInfoList(Context context, String objectId) throws Exception {
		StringList objectList = FrameworkUtil.split(objectId, SYMBOL_PIPE);
		return DomainObject.getInfo(context, objectList.toArray(new String[objectList.size()]), StringList.create(SELECT_ID, SELECT_TYPE, SELECT_NAME, SELECT_REVISION, SELECT_CURRENT, SELECT_POLICY, SELECT_VAULT, SELECT_ATTRIBUTE_RELEASE_PHASE, SELECT_ATTRIBUTE_PGORIGINATINGSOURCE));
	}
	/**DSM 2018x.3 - On Demand Support Tools 
	 * @about -  Method to check if state is 'In Approval'
	 * @return boolean 
	 * @throws Exception
	 */
	public static boolean isStateInApproval(String current) throws Exception {
		if(isNullEmpty(current)) 
			throw new IllegalArgumentException();
		return STATE_IN_APPROVAL.equals(current);
	}
	/**DSM 2018x.3 - On Demand Support Tools 
	 * @about -  Method to check if state is 'In Approval'
	 * @return boolean 
	 * @throws Exception
	 */
	public static boolean isStateRelease(String current) throws Exception {
		if(isNullEmpty(current)) 
			throw new IllegalArgumentException();
		return STATE_RELEASE.equals(current);
	}
	/**DSM 2018x.5 - On Demand Support Tools 
	 * @about -  Method to check if state is 'Complete'
	 * @return boolean 
	 * @throws Exception
	 */
	public static boolean isStateInComplete(String current) throws Exception {
		if(isNullEmpty(current)) 
			throw new IllegalArgumentException();
		return STATE_COMPLETE.equals(current);
	}	
	/**DSM 2018x.5 - On Demand Support Tools 
	 * @about -  Method to check if current state is allowed for GenDoc Marking or not
	 * @return boolean 
	 * @throws Exception
	 */
	public static boolean isStateAllowedForMarking(String current)  throws Exception {	
		if(isNullEmpty(current)) 
			throw new IllegalArgumentException();
		return (isStateInApproval(current)||isStateInComplete(current));		
	}
	
	/**DSM 2018x.5 - On Demand Support Tools 
	 * @about -  Method to configure range href for DSM name Field not
	 * @return boolean 
	 * @throws Exception
	 */
	public static String getRangeHref(Context context,String[] args)  throws Exception {	
		Properties properties = SupportUtil.loadSupportActionConfigPage(context);
		Map programMap = (HashMap) JPO.unpackArgs(args);
		Map fieldMap = (HashMap)programMap.get("fieldMap");
		Map settingsMap = (HashMap)fieldMap.get("settings");
		String type = (String) settingsMap.get("type");
		Map fieldValuesMap = (HashMap)programMap.get("fieldValues");
		String strSupportAction = (String)fieldValuesMap.get("SupportActions");
		if(SupportUtil.isNotNullEmpty(type)){
			strSupportAction=type;			
		}
		
		String strSearchTypes =properties.getProperty("SupportAction.Search."+strSupportAction);
		
		return (strSearchTypes);		
	}
	
	/**DSM 2018x.5 - On Demand Support Tools 
	 * @about -  Method to get details from SupportConfigObject
         * @param - context - Context
	 * @return boolean 
	 * @throws MatrixException 
	 */
	public static String getSupportTeamMailId(Context context) throws  MatrixException {
		String strSupportTeamMailId = EMPTY_STRING;
		try{
			BusinessObject boConfig = new BusinessObject(TYPE_PGCONFIGURATIONADMIN, PGDSMSUPPORTTEAM_CONFIGOBJECT, SYMBOL_HYPHEN,VAULT_ESERVICEPRODUCTION);
			if (boConfig.exists(context)){
				String strObjId = boConfig.getObjectId(context);
				if(SupportUtil.isNotNullEmpty(strObjId)){
					DomainObject dobjConfigurationObject = DomainObject.newInstance(context,strObjId);				
					strSupportTeamMailId = dobjConfigurationObject.getInfo(context, SELECT_ATTRIBUTE_PGCONFIGCOMMONADMINMAILID);			
				}
			}
		}catch (MatrixException e) {
		    e.printStackTrace();
		} 
		return strSupportTeamMailId;
	}
	
}

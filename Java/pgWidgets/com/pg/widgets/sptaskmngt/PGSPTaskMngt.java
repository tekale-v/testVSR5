package com.pg.widgets.sptaskmngt;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;

import com.matrixone.apps.awl.util.BusinessUtil;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PersonUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.widgets.util.PGWidgetConstants;
import com.pg.widgets.util.PGWidgetUtil;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.Pattern;
import matrix.util.StringList;

public class PGSPTaskMngt{

	private static final String JSON_OUTPUT_KEY_ID = "Id";
	private static final String JSON_OUTPUT_KEY_NAME= "name";
	private static final String JSON_OUTPUT_KEY_ERROR = "error";
	private static final String JSON_OUTPUT_KEY_TRACE = "trace";
	private static final String JSON_OUTPUT_KEY_CONTENT = "Content";

	private static final String JSON_OUTPUT_KEY_HEADER = "header";
	private static final String JSON_OUTPUT_KEY_TITLE = "title";
	private static final String JSON_OUTPUT_KEY_SUB_TITLE = "subTitle";
	private static final String JSON_OUTPUT_KEY_DESCRIPTION = "description";
	private static final String KEY_PHYSICALID = "physicalid";
	private static final String JSON_OUTPUT_KEY_TYPE = "Type";
	private static final String JSON_OUTPUT_KEY_INSTRUCTIONS = "instructions";
	private static final String SELECT_ATTRIBUTE_DUE_DATE = "attribute[" + DomainConstants.ATTRIBUTE_SCHEDULED_COMPLETION_DATE + "]";

	static final String MAX_STRING_LENGTH = "org.apache.johnzon.max-string-length";
	static final String VALUE_KILOBYTES = "262144";

	private static final Logger logger = Logger.getLogger(PGSPTaskMngt.class.getName());

	static final String ERROR_Logger = "Exception in PGSPTaskMngt";

	static final String MULIT_VAL_CHAR = "\u0007";

	static final String ROUTECONTENTTYPE = "from["+DomainConstants.RELATIONSHIP_ROUTE_TASK+"].to.to["+DomainConstants.RELATIONSHIP_OBJECT_ROUTE+"].from."+DomainConstants.SELECT_TYPE;
	static final String ROUTECONTENTID = "from["+DomainConstants.RELATIONSHIP_ROUTE_TASK+"].to.to["+DomainConstants.RELATIONSHIP_OBJECT_ROUTE+"].from.id";
	//from[Route Task].to.to[Object Route].from.name
	static final String ROUTECONTENTNAME = "from["+DomainConstants.RELATIONSHIP_ROUTE_TASK+"].to.to["+DomainConstants.RELATIONSHIP_OBJECT_ROUTE+"].from.name";	
	static final String ROUTECONTENTRELID = "from["+DomainConstants.RELATIONSHIP_ROUTE_TASK+"].to.to["+DomainConstants.RELATIONSHIP_OBJECT_ROUTE+"].id";

	/**
	 * The method creates the JSON object to get the list of Study Protocol associated with the user. the input parameters are:
	 * 
	 * @param context
	 *            The enovia Context object
	 * @param args
	 *            the type, name, revision and where clause,allowed state can be configured and sent as input
	 *            to get the desired list of the objects to be displayed
	 * @return JSON object consisting of the information to be displayed
	 * @throws Exception
	 *             When operation fails
	 */
	public static String getSPConnectedRouteTasks(Context context , String inputData) throws Exception {	
		JsonObjectBuilder output = Json.createObjectBuilder();

		try 
		{
			JsonObject jsonInputInfo = getJsonFromJsonString(inputData);
			jsonInputInfo.getString("contentType");

			HashMap<String, Object> mpParamMAP = new HashMap<>();
			mpParamMAP.put("functionality", "AppMyTasks");

			MapList mpTaskList = JPO.invoke(context, "emxInboxTask", null, "getActiveTasks", JPO.packArgs(mpParamMAP), MapList.class);
			JsonArrayBuilder jsonArr = Json.createArrayBuilder();	
			if(mpTaskList !=null && !mpTaskList.isEmpty())
			{
				JsonObjectBuilder jsonObject = null;
				Map<?, ?> objMap = null;
				String strTaskId;
				String strTaskName;
				String strTaskTitle;
				String strTaskDueDate;
				String strTaskState;
				String strContentRelIds = "";
				String strContentIds = "";
				String strContentNames = "";
				String strInstructions = "";
				String strType = "";
				StringList strContentIdNameList = null;
				StringList strContentIdList = null;

				StringList selectContentRelInfo = new StringList();
				selectContentRelInfo.add(DomainConstants.SELECT_NAME);
				selectContentRelInfo.add(DomainConstants.SELECT_ID);
				selectContentRelInfo.add(KEY_PHYSICALID);

				for (int i = 0; i < mpTaskList.size(); i++) 
				{

					objMap = (Map<?,?>)mpTaskList.get(i);
					strTaskId = checkNullValueforString((String)objMap.get(DomainConstants.SELECT_ID));
					strTaskName = checkNullValueforString((String)objMap.get(DomainConstants.SELECT_NAME));
					strTaskTitle = checkNullValueforString((String)objMap.get(DomainConstants.SELECT_ATTRIBUTE_TITLE));
					strTaskDueDate = checkNullValueforString((String)objMap.get(SELECT_ATTRIBUTE_DUE_DATE));
					strTaskState = checkNullValueforString((String)objMap.get(DomainConstants.SELECT_CURRENT));
					strType = checkNullValueforString((String)objMap.get(DomainConstants.SELECT_TYPE));
					strInstructions = checkNullValueforString((String) objMap.get("attribute[Route Instructions]"));

					strContentIdList = BusinessUtil.getStringList(objMap, ROUTECONTENTID);			
					strContentIdNameList = BusinessUtil.getStringList(objMap, ROUTECONTENTNAME);		

					if(null != strContentIdList && !strContentIdList.isEmpty()) {
						jsonObject=Json.createObjectBuilder();
						String[] strContentIdsArray = new String[strContentIdList.size()];

						for(int j=0 ; j<strContentIdList.size() ; j++) {
							strContentIdsArray[j] = strContentIdList.get(j);
						}
						MapList mlInfo = DomainObject.getInfo(context, strContentIdsArray, selectContentRelInfo);
						JsonArrayBuilder jsonArr1 = Json.createArrayBuilder();
						for(int k=0 ; k<mlInfo.size() ; k++) {
							JsonObjectBuilder jsonObject1 = Json.createObjectBuilder();
							jsonObject1.add(JSON_OUTPUT_KEY_NAME, ((Map)mlInfo.get(k)).get(DomainConstants.SELECT_NAME).toString());
							jsonObject1.add(JSON_OUTPUT_KEY_ID, ((Map)mlInfo.get(k)).get(DomainConstants.SELECT_ID).toString());
							jsonObject1.add(KEY_PHYSICALID, ((Map)mlInfo.get(k)).get(KEY_PHYSICALID).toString());
							jsonArr1.add(jsonObject1.build());
						}
						strContentIds = strContentIdList.get(0);
						strContentNames = strContentIdNameList.get(0);
						jsonObject.add(JSON_OUTPUT_KEY_ID, strTaskId);
						jsonObject.add(JSON_OUTPUT_KEY_NAME,checkNullValueforString(strTaskName));
						jsonObject.add(JSON_OUTPUT_KEY_CONTENT, jsonArr1.build());
						jsonObject.add(JSON_OUTPUT_KEY_HEADER,checkNullValueforString(strTaskName));
						jsonObject.add(JSON_OUTPUT_KEY_TITLE,checkNullValueforString(strTaskTitle));
						jsonObject.add(JSON_OUTPUT_KEY_SUB_TITLE,  parseDate(strTaskDueDate));
						jsonObject.add(JSON_OUTPUT_KEY_DESCRIPTION, strContentNames);
						jsonObject.add(JSON_OUTPUT_KEY_INSTRUCTIONS, strInstructions);
						jsonArr.add(jsonObject);
					} 
				}
			}		
			output.add("data",jsonArr.build());
		}catch (MatrixException ex) {
			logger.log(Level.SEVERE, ERROR_Logger, ex);
			output.add(JSON_OUTPUT_KEY_ERROR, ex.getMessage());
			output.add(JSON_OUTPUT_KEY_TRACE, getExceptionTrace(ex));
		}
		return output.build().toString();
	}	 
	
	/**
	 * The method creates the JSON array to get the list of Inbox Tasks assigned to context user
	 * 
	 * @param context
	 *            The enovia Context object
	 * @param args
	 *            ObjectsSelects, Duration, Limit, whereExpression, contentType
	 * @return JSON array consisting of the information to be displayed
	 * @throws Exception
	 *             When operation fails
	 */
	public static String getRouteTasks(Context context , String inputData) throws Exception {	
		JsonArrayBuilder jsonOutput = Json.createArrayBuilder();
		try {
			JsonObject jsonInput = PGWidgetUtil.getJsonFromJsonString(inputData);
			String strObjSels = jsonInput.getString(PGWidgetConstants.KEY_OBJECT_SELECTS);
			StringList slObjSelects = FrameworkUtil.split(strObjSels, PGWidgetConstants.KEY_COMMA_SEPARATOR);

			String strContentIdSel = "from["+DomainConstants.RELATIONSHIP_ROUTE_TASK+"].to.to["+DomainConstants.RELATIONSHIP_OBJECT_ROUTE+"].from."+DomainConstants.SELECT_ID;
			String strContentNameSel = "from["+DomainConstants.RELATIONSHIP_ROUTE_TASK+"].to.to["+DomainConstants.RELATIONSHIP_OBJECT_ROUTE+"].from."+DomainConstants.SELECT_NAME;
			String strContentTypeSel = "from["+DomainConstants.RELATIONSHIP_ROUTE_TASK+"].to.to["+DomainConstants.RELATIONSHIP_OBJECT_ROUTE+"].from."+DomainConstants.SELECT_TYPE;

			slObjSelects.add(strContentIdSel);
			slObjSelects.add(strContentNameSel);
			slObjSelects.add(strContentTypeSel);

			DomainConstants.MULTI_VALUE_LIST.add(strContentIdSel);
			DomainConstants.MULTI_VALUE_LIST.add(strContentNameSel);
			DomainConstants.MULTI_VALUE_LIST.add(strContentTypeSel);

			String strWhere = jsonInput.getString(PGWidgetConstants.WHERE_EXP);


			String strDuration = jsonInput.getString(PGWidgetConstants.DURATION);
			String strLimit = jsonInput.getString(PGWidgetConstants.KEY_LIMIT);

			String strType = jsonInput.getString("contentType");

			if(UIUtil.isNotNullAndNotEmpty(strDuration)) {
				strWhere = PGWidgetUtil.getWhereClause(context, strDuration, strWhere, null);
			}
			Pattern relPattern = new Pattern(DomainConstants.RELATIONSHIP_PROJECT_TASK);

			Pattern typePattern = new Pattern(DomainConstants.TYPE_INBOX_TASK);

			DomainObject boPerson = PersonUtil.getPersonObject(context);
			MapList taskMapList =  boPerson.getRelatedObjects(context,
					relPattern.getPattern(),
					typePattern.getPattern(),
					slObjSelects,
					null,
					true,
					true,
					(short)0,
					strWhere,
					null,
					Integer.parseInt(strLimit));

			JsonObjectBuilder jsonObjTask = null;
			JsonObjectBuilder jsonObjContent = null;

			if(taskMapList != null) {
				for(int i=0 ; i < taskMapList.size() ; i++) {
					Map mapTemp =  (Map) taskMapList.get(i);
					//					StringList slContentTypes = (StringList) mapTemp.get(strContentTypeSel);
					//					StringList slContentIds = (StringList) mapTemp.get(strContentIdSel);
					//					StringList slContentNames = (StringList) mapTemp.get(strContentNameSel);
					//					boolean isAllowedContent = false;
					//					for(int j=0 ; j<slContentTypes.size() ; j++) {
					//						if(UIUtil.isNullOrEmpty(strType) || strType.indexOf(slContentTypes.get(j)) > -1) {
					//							jsonObjContent = Json.createObjectBuilder();
					//							jsonObjContent.add(DomainConstants.SELECT_TYPE, slContentTypes.get(j));
					//							jsonObjContent.add(DomainConstants.SELECT_ID, slContentIds.get(j));
					//							jsonObjContent.add(DomainConstants.SELECT_TYPE, slContentNames.get(j));
					//							jsonArrContent.add(jsonObjContent.build());
					//							isAllowedContent = true;
					//						}
					//					}
					String slContentTypes = (String) mapTemp.get(strContentTypeSel);
					String slContentIds = (String) mapTemp.get(strContentIdSel);
					String slContentNames = (String) mapTemp.get(strContentNameSel);
					JsonArrayBuilder jsonArrContent = Json.createArrayBuilder();
					boolean isAllowedContent = false;
					//for(int j=0 ; j<slContentTypes.size() ; j++) {
					if(UIUtil.isNullOrEmpty(strType) || strType.indexOf(slContentTypes) > -1) {
						jsonObjContent = Json.createObjectBuilder();
						jsonObjContent.add(DomainConstants.SELECT_TYPE, checkNullValueforString(slContentTypes));
						jsonObjContent.add(DomainConstants.SELECT_ID, checkNullValueforString(slContentIds));
						jsonObjContent.add(DomainConstants.SELECT_NAME, checkNullValueforString(slContentNames));
						jsonArrContent.add(jsonObjContent.build());
						isAllowedContent = true;
					}
					//}
					if(isAllowedContent) {
						jsonObjTask = Json.createObjectBuilder();
						jsonObjTask.add(JSON_OUTPUT_KEY_ID, mapTemp.get(DomainConstants.SELECT_ID).toString());
						jsonObjTask.add(JSON_OUTPUT_KEY_NAME, mapTemp.get(DomainConstants.SELECT_NAME).toString());
						jsonObjTask.add(JSON_OUTPUT_KEY_CONTENT, jsonArrContent.build());
						jsonObjTask.add(JSON_OUTPUT_KEY_TITLE, checkNullValueforString((String) mapTemp.get(DomainConstants.SELECT_ATTRIBUTE_TITLE)));
						jsonObjTask.add("dueDate",  checkNullValueforString((String)mapTemp.get(SELECT_ATTRIBUTE_DUE_DATE)));
						jsonObjTask.add(JSON_OUTPUT_KEY_INSTRUCTIONS, checkNullValueforString((String) mapTemp.get("attribute[Route Instructions]")));
						jsonObjTask.add("modifiedDate", checkNullValueforString((String) mapTemp.get(DomainConstants.SELECT_MODIFIED)));
						jsonOutput.add(jsonObjTask);
					}
				}
			}
			DomainConstants.MULTI_VALUE_LIST.remove(strContentIdSel);
			DomainConstants.MULTI_VALUE_LIST.remove(strContentNameSel);
			DomainConstants.MULTI_VALUE_LIST.remove(strContentTypeSel);
		}catch (Exception e) {
			logger.log(Level.SEVERE, ERROR_Logger, e);
			jsonOutput.add(Json.createObjectBuilder().add(PGWidgetConstants.KEY_ERROR, e.getMessage()));
		}
		return jsonOutput.build().toString();
	}	 

	/**
	 * The method creates the JSON Object to get the passed Inbox Task Info
	 * 
	 * @param context
	 *            The enovia Context object
	 * @param args
	 *            ObjectsSelects, TaskId
	 * @return JSON Object consisting of the information to be displayed
	 * @throws Exception
	 *             When operation fails
	 */
	public static String getTaskInfo(Context context, String inputData){

		JsonObjectBuilder jsonOutput = Json.createObjectBuilder();
		try {
			JsonObject jsonInput = PGWidgetUtil.getJsonFromJsonString(inputData);
			String strObjSels = jsonInput.getString(PGWidgetConstants.KEY_OBJECT_SELECTS);
			StringList slObjSelects = FrameworkUtil.split(strObjSels, PGWidgetConstants.KEY_COMMA_SEPARATOR);

			String strTaskId = jsonInput.getString(DomainConstants.SELECT_ID);
			DomainObject domTask = DomainObject.newInstance(context, strTaskId);

			Map mapTask = domTask.getInfo(context, slObjSelects);
			return PGWidgetUtil.getJSONFromMap(context, mapTask).toString();

		} catch (Exception e) {
			logger.log(Level.SEVERE, ERROR_Logger, e);
			jsonOutput.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			return jsonOutput.build().toString();
		}
	}

	private static String parseDate(String sDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		Date date;
		String sDateView = "";
		try {
			date = sdf.parse(sDate);
			sDateView = sdf.format(date);
		} catch (ParseException e) {
			logger.log(Level.SEVERE, ERROR_Logger, e);
		}

		return sDateView;
	}

	/**
	 * Stack trace to string 
	 * @param ex
	 * @return
	 */
	private static String getExceptionTrace(Exception ex) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		return sw.toString(); // stack trace as a string
	}

	/**
	 * Check value is null or Empty and if MultiValue character(\u0007) present then replace with comma
	 * @param strString
	 * @return
	 */
	private static String checkNullValueforString(  String strString )
	{
		if(strString!=null && strString.contains(MULIT_VAL_CHAR)) {
			strString=strString.replaceAll(MULIT_VAL_CHAR, " , ");
		}
		return null != strString ? strString : DomainConstants.EMPTY_STRING;
	}

	/**
	 * Method to convert String json which is usually the value of attributes to JsonObject
	 * @param strJsonString : String json
	 * @return : JsonObject created from String json
	 */
	public static JsonObject getJsonFromJsonString(String strJsonString) {
		StringReader srJsonString = new StringReader(strJsonString);
		Map<String, String> configMap = new HashMap<>();
		configMap.put(MAX_STRING_LENGTH, VALUE_KILOBYTES);
		JsonReaderFactory factory = Json.createReaderFactory(configMap);					
		try(JsonReader jsonReader = factory.createReader(srJsonString)) {
			return jsonReader.readObject();
		} finally {
			srJsonString.close();
		}
	}	

}

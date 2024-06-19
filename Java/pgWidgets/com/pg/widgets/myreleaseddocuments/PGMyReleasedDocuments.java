package com.pg.widgets.myreleaseddocuments;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.domain.util.eMatrixDateFormat;
import com.matrixone.apps.framework.ui.UIUtil;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class PGMyReleasedDocuments {

	private static final String JSON_OUTPUT_KEY_ERROR = "error";
	private static final String JSON_OUTPUT_KEY_TRACE = "trace";
	private static final String JSON_OUTPUT_KEY_OBJECT_ID = "Id";
	private static final String JSON_OUTPUT_KEY_NAME = "Name";
	private static final String JSON_OUTPUT_KEY_TYPE = "Type";
	
	
	private static final String POLICY_DOCUMENT_RELEASE = PropertyUtil.getSchemaProperty(null,
			"policy_Document");
	private static final String POLICY_PGPKG_SIGNATURE_REFERENCEDOC = PropertyUtil.getSchemaProperty(null,
			"policy_pgPKGSignatureReferenceDoc");
	
	private static final String STATE_RELEASED = PropertyUtil.getSchemaProperty(null,
			"policy",POLICY_DOCUMENT_RELEASE,"state_RELEASED");
	
	
	private static final String STATE_RELEASE = PropertyUtil.getSchemaProperty(null,
			"policy",POLICY_PGPKG_SIGNATURE_REFERENCEDOC,"state_Release");
	
	
	private static final String RELEASED_ON_DATE = "state["+STATE_RELEASED+"].actual";
	
	private static final String RELEASE_ON_DATE = "state["+STATE_RELEASE+"].actual";
	
	
	private static final Logger logger = Logger.getLogger(PGMyReleasedDocuments.class.getName());

	static final String RELATIONSHIP_PGPROJECTDOCUMENT = PropertyUtil.getSchemaProperty(null,
			"relationship_pgProjectDocument");
	static final String ERROR_OBJECT_NOT_PROJECTTYPE = "Only object of Type 'Project Space' is allowed!";
	static final String ERROR_IN_GETPROJECTDOCUMENT_IN_MYRELEASEDDOCUMENT = "Exception in PGMyReleasedDocuments : getProjectDocuments::";
	static final String DENIED = "#DENIED!";
	static final String LAST_WEEK = "LAST WEEK";
	static final String LAST_TWO_WEEKS = "LAST TWO WEEKS";
	static final String LAST_MONTH = "LAST MONTH";
	
	static final String TIME_FORMAT_1 = "11:59:59 PM";
	static final String TIME_FORMAT_2= "12:00:00 AM";
	static final String JSON_OUTPUT_KEY_BASE_OBJ_DETAIL = "baseObjectDetail";
	static final String JSON_OUTPUT_KEY_RELATED_OBJECT = "relatedObject";
	static final String ACCESS_READ = "read";
	
	static final String TYPE_PATTERN = "TypePattern";
	static final String PROJECT_ATTRIBUTES = "ProjectAttributes";
	static final String RELEASED_IN_DURATION = "ReleasedInDuration";
	static final String OBJECT_LIMIT = "ObjectLimit";
	static final String PROJECT_ID = "ProjectId";
	
	static final String OBJ_SELECT = "ObjectSelects";

	/**
	 * This method is used to get the all documents added to current project
	 * @param context
	 * @param args 
	 * ProjectId :  object id of project
	 * ReleasedInDuration : filter the date based on 'Released On' date
	 * ObjectLimit : Number of object it should return
	 * ObjectSelects : Object selectables for Document object
	 * ProjectAttributes : Object selectables for Project
	 * @return String in JSON format having project details and connected document details
	 * @throws Exception
	 */
	public static String getProjectDocuments(Context context , Map<?,?> mpParamMAP)  throws Exception {

		JsonObjectBuilder output = Json.createObjectBuilder();
		try {

			HashMap<?,?> programMap = (HashMap <?,?>)mpParamMAP;

			String strProjectId = (String) programMap.get(PROJECT_ID);			
			String strReleasedInDuration = (String) programMap.get(RELEASED_IN_DURATION);
			String strObjectLimit = (String) programMap.get(OBJECT_LIMIT);
			String strObjectSelects = (String) programMap.get(OBJ_SELECT);	
			String strProjectAttributes = (String) programMap.get(PROJECT_ATTRIBUTES);				
			String strTypePattern = (String) programMap.get(TYPE_PATTERN);	

			DomainObject domObject = DomainObject.newInstance(context,strProjectId);
			if(!domObject.isKindOf(context,DomainConstants.TYPE_PROJECT_SPACE))
			{
				output.add(JSON_OUTPUT_KEY_ERROR, ERROR_OBJECT_NOT_PROJECTTYPE);
				return output.build().toString();
			}

			StringList slObjectSelect=new StringList(6);
			slObjectSelect.add(DomainConstants.SELECT_ID);
			slObjectSelect.add(DomainConstants.SELECT_TYPE);
			slObjectSelect.add(DomainConstants.SELECT_NAME);
			if (UIUtil.isNotNullAndNotEmpty(strProjectAttributes)) {
				slObjectSelect.addAll(strProjectAttributes.split(","));
			}

			Map<?,?> objectInfo = domObject.getInfo(context, slObjectSelect);			
			JsonObject baseObjJson = addObjectDetail(context,objectInfo);			

			StringBuilder sbTypes = getTypePattern(context, strTypePattern);
			
			StringList slChildSelects = new StringList();
			slChildSelects.add(DomainConstants.SELECT_ID);
			slChildSelects.add(DomainConstants.SELECT_NAME);
			slChildSelects.add(DomainConstants.SELECT_TYPE);
			if (UIUtil.isNotNullAndNotEmpty(strObjectSelects)) {
				slChildSelects.addAll(strObjectSelects.split(","));
			}

			String strWhere = getWhereClause(context, strReleasedInDuration);
			MapList mlConnectedDocuments = domObject.getRelatedObjects(context, RELATIONSHIP_PGPROJECTDOCUMENT, // relationshipPattern
					sbTypes.toString(), // typePattern
					slChildSelects, // objectSelects
					null, // relationshipSelects
					false , // getTo
					true, // getFrom
					(short) 1, // recurseToLevel
					strWhere, // objectWhere
					DomainConstants.EMPTY_STRING, // relationshipWhere
					Short.parseShort(strObjectLimit));// limit				
		
			JsonArrayBuilder arrRelObjectJson=Json.createArrayBuilder();
			if(mlConnectedDocuments != null && !mlConnectedDocuments.isEmpty())
			{
				int mlSize = mlConnectedDocuments.size();
				for(int i = 0 ; i < mlSize ; i++) {
					arrRelObjectJson.add(addObjectDetail(context,(Map<?,?>) mlConnectedDocuments.get(i)));		  
				}
			}

			output.add(JSON_OUTPUT_KEY_BASE_OBJ_DETAIL,baseObjJson);
			output.add(JSON_OUTPUT_KEY_RELATED_OBJECT, arrRelObjectJson.build());		   
		}
		catch (Exception ex)
		{
			logger.log(Level.SEVERE, ERROR_IN_GETPROJECTDOCUMENT_IN_MYRELEASEDDOCUMENT, ex);
			output.add(JSON_OUTPUT_KEY_ERROR, ex.getMessage());
			output.add(JSON_OUTPUT_KEY_TRACE, getExceptionTrace(ex));
		}
		return output.build().toString();
	}
	/**
	 * Get Type Pattern
	 * @param context
	 * @param strTypePattern
	 * @return
	 */

	private static StringBuilder getTypePattern(Context context,String strTypePattern) 
	{
		StringBuilder sbTypes = new StringBuilder();		
		StringList slTypeList = StringUtil.split(strTypePattern,",");
		for(int i= 0 ;i < slTypeList.size();i++)
		{
			String strSymSchema = PropertyUtil.getSchemaProperty(context,slTypeList.get(i));	
			sbTypes.append(strSymSchema).append(",");
		}
		return sbTypes;
	}

	
	/**
	 * Build where condition to filter the data
	 * @param context
	 * @param strReleasedInDuration
	 * @return
	 * @throws Exception
	 */
	private static String getWhereClause(Context context, String strReleasedInDuration) throws Exception
	{	
		StringBuilder strWhereClause = new StringBuilder();	

		if(UIUtil.isNotNullAndNotEmpty(strReleasedInDuration))
		{
			strWhereClause.append(getDateQuery(context,strReleasedInDuration));
		}
		else
		{
			strWhereClause.append("(");		
			strWhereClause.append(DomainConstants.SELECT_CURRENT);	
			strWhereClause.append("=='");
			strWhereClause.append(STATE_RELEASE);
			strWhereClause.append("' || ");	
			strWhereClause.append(DomainConstants.SELECT_CURRENT);			
			strWhereClause.append("=='");
			strWhereClause.append(STATE_RELEASED);
			strWhereClause.append("')");			
		}
		return strWhereClause.toString();
	}


	/**
	 * Generate date expression on Released on value
	 * @param context
	 * @param strReleasedInDuration
	 * @return
	 * @throws Exception
	 */
	private static String getDateQuery(Context context,String strReleasedInDuration) throws Exception
	{	
		StringBuilder sbBuildQuery = new StringBuilder();

		DateFormat dtFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);		
		java.util.TimeZone tz = java.util.TimeZone.getTimeZone(context.getSession().getTimezone());									
		double dbMilisecondsOffset = (double)(-1)*tz.getRawOffset();
		double clientTZOffset = dbMilisecondsOffset/(1000*60*60);

		String strCurrentDate = dtFormat.format(new Date());		
		strCurrentDate = eMatrixDateFormat.getFormattedInputDateTime(context, strCurrentDate, TIME_FORMAT_1,clientTZOffset,Locale.US);		
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);				

		if (LAST_WEEK.equalsIgnoreCase(strReleasedInDuration))
		{
			cal.add(Calendar.DATE, -7);
		} 
		else if (LAST_TWO_WEEKS.equalsIgnoreCase(strReleasedInDuration))
		{
			cal.add(Calendar.DATE, -14);
		} 
		else if (LAST_MONTH.equalsIgnoreCase(strReleasedInDuration))
		{
			cal.add(Calendar.DATE, -30);
		}	

		Date start = cal.getTime();
		String strFromDate = dtFormat.format(start);	 
		strFromDate = eMatrixDateFormat.getFormattedInputDateTime(context, strFromDate, TIME_FORMAT_2,clientTZOffset,Locale.US);		
			   

		sbBuildQuery.append("((");		
		sbBuildQuery.append(DomainConstants.SELECT_CURRENT);	
		sbBuildQuery.append("=='Release'");		
		sbBuildQuery.append(" && (");	
		sbBuildQuery.append(RELEASE_ON_DATE).append(" >= ");
		sbBuildQuery.append("'" + strFromDate + "'");
		sbBuildQuery.append(")");
		sbBuildQuery.append(" && ");
		sbBuildQuery.append("(");
		sbBuildQuery.append(RELEASE_ON_DATE).append(" <= ");
		sbBuildQuery.append("'" + strCurrentDate + "'");
		sbBuildQuery.append("))");		

		sbBuildQuery.append(" || ");		

		sbBuildQuery.append("(");
		sbBuildQuery.append(DomainConstants.SELECT_CURRENT);	
		sbBuildQuery.append("=='RELEASED'");		
		sbBuildQuery.append(" && (");	
		sbBuildQuery.append(RELEASED_ON_DATE).append(" >= ");
		sbBuildQuery.append("'" + strFromDate + "'");
		sbBuildQuery.append(")");
		sbBuildQuery.append(" && ");
		sbBuildQuery.append("(");
		sbBuildQuery.append(RELEASED_ON_DATE).append(" <= ");
		sbBuildQuery.append("'" + strCurrentDate + "'");
		sbBuildQuery.append(")))");		


		return sbBuildQuery.toString();
	}

	/**
	 * This method add the data from Map into JSON object
	 * @param mpObjectInfo
	 * @return
	 * @throws MatrixException 
	 */
	private static  JsonObject addObjectDetail(Context context,Map<?,?> mpObjectInfo) throws MatrixException
	{	
		String strLanguage = context.getSession().getLanguage();
		JsonObjectBuilder jsonObject = Json.createObjectBuilder();
		
		String strId = checkNullValueforString((String)mpObjectInfo.get(DomainConstants.SELECT_ID));
		jsonObject.add(JSON_OUTPUT_KEY_OBJECT_ID,checkNullValueforString(mpObjectInfo.get(DomainConstants.SELECT_ID).toString()));
		
		String strTypeDisplayName = EnoviaResourceBundle.getTypeI18NString(context, (String)mpObjectInfo.get(DomainConstants.SELECT_TYPE), strLanguage);			
		jsonObject.add(JSON_OUTPUT_KEY_TYPE,strTypeDisplayName);
		jsonObject.add(JSON_OUTPUT_KEY_NAME,checkNullValueforString(mpObjectInfo.get(DomainConstants.SELECT_NAME).toString()));
		boolean hasAccess = FrameworkUtil.hasAccess(context, DomainObject.newInstance(context, strId) , ACCESS_READ);
		String	strAttrName;	
		String	strAttrValue;		
		for (Entry<?, ?> entry : mpObjectInfo.entrySet()) {		
			strAttrName = (String) entry.getKey();	
			strAttrValue = checkNullValueforString(extractMultiValueSelect(mpObjectInfo, strAttrName));
			
			if(!hasAccess)
			{
			      if(strAttrValue.equals(DENIED))
	    		  {
			    	  jsonObject.add(strAttrName,EnoviaResourceBundle.getProperty(context,"emxFrameworkStringResource",context.getLocale(), "emxFramework.Access.NoAccess"));  
	    		  }
			      else
				  {
					   jsonObject.add(strAttrName,strAttrValue);
				  }
		     }
			else
			{
				jsonObject.add(strAttrName,strAttrValue);
			}
		}
		return jsonObject.build();		
	}
	
	/**
	 * Check value is null or Empty and if multi value character and replace it with comma
	 * @param strString
	 * @return
	 */
	private static String checkNullValueforString(  String strString )
	{
		return null != strString ? strString : DomainConstants.EMPTY_STRING;
	}

	/**
	 * Remove Duplicates from List
	 * @param list
	 * @return
	 */
	private static List<String> removeDublicates(List<String> list) {
		List<String> tempList=new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			if(!tempList.contains(list.get(i))) {
				tempList.add(list.get(i));
			}
		}

		return tempList;
	}
	/**
	 * Handle MultiValue list
	 * @param mpData
	 * @param strSelect
	 * @return
	 */

	private static String extractMultiValueSelect(Map<?, ?> mpData, String strSelect)
	{
		String strValue = DomainConstants.EMPTY_STRING;
		try {
			strValue = (String) mpData.get(strSelect);
		} catch (Exception e) {
			StringBuilder sbValues = new StringBuilder();
			List<String> listValues = (List<String>) mpData.get(strSelect);
			listValues=removeDublicates(listValues);	
			Collections.sort(listValues);

			for (int i = 0; i < listValues.size(); i++) {
				sbValues.append(listValues.get(i));
				sbValues.append(",");
			}
			if (sbValues.length() > 0) {
				sbValues.setLength(sbValues.length() - 1);
			}
			strValue = sbValues.toString();
		}
		return strValue;
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

}

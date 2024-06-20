package com.pg.widgets.structuredats;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonObject;

import matrix.util.StringList;
import matrix.db.Context;

import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.widgets.util.PGWidgetUtil;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.framework.ui.UINavigatorUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;

public class PGStructuredATSHistoryUtil 
{
	private static final Logger logger= Logger.getLogger(PGStructuredATSHistoryUtil.class.getName()); 
	static final String STRING_HISTORY = "History";
	private static final String CONST_STATE_WITH_SPACE = "state: ";
	/**This Method reads history of the SATS object and returns as response
	 * @param context
	 * @param strJsonInput
	 * @return
	 * @throws Exception  
	 */
	public String getSATSHistory(Context context, String strJsonInput) throws Exception 
	{
		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonArrayBuilder jsonArrRelated = Json.createArrayBuilder();
		String strObjectId = null;
		HashMap hmaplist = new HashMap();
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strJsonInput);
			strObjectId = jsonInputData.getString(DomainConstants.SELECT_ID);
			if (UIUtil.isNotNullAndNotEmpty(strObjectId))
			{
				hmaplist=UINavigatorUtil.getHistoryData(context,strObjectId);
				if (null!=hmaplist && hmaplist.size()>0)
				{
					jsonArrRelated =  constructSATSHistory(context, hmaplist);
				}
				output.add(STRING_HISTORY,jsonArrRelated.build());
			}
			
		}catch(Exception excep)
		{
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_HISTORY_UTIL, excep);
		}
		return output.build().toString();
	}
	/**This Method constructs the History for WS
	 * @param context
	 * @param slHistory
	 * @return
	 * @throws Exception  
	 */
	public JsonArrayBuilder constructSATSHistory(Context context, HashMap hmaplist) throws Exception 
	{
		String strUser= null;
		String strTime =null;
		String strDescription = null;
		String strAction = null;
		String strCurrent = null;
		String strDesc = null;
		StringBuffer sbHistory = new StringBuffer();
		JsonArrayBuilder jsonArr = Json.createArrayBuilder();
		JsonObjectBuilder jsonObject = Json.createObjectBuilder();
		try{
			StringList timeArray = (StringList)hmaplist.get("time");
			StringList userArray = (StringList)hmaplist.get("user");
			StringList actionArray = (StringList)hmaplist.get("action");
			StringList stateArray = (StringList)hmaplist.get("state");
			StringList descriptionArray = (StringList)hmaplist.get("description");
			String languageStr = context.getLocale().getDisplayName();
			for( int j=0; j < timeArray.size(); j++) 
			{  
				
				strUser    = (String)userArray.get(j);
				strTime    = (String)timeArray.get(j);
				strAction    = (String)actionArray.get(j);
				strDescription    = (String)descriptionArray.get(j);
				strCurrent   = (String)stateArray.get(j);		
				if(strCurrent != null && !"null".equals(strCurrent) && strCurrent.length() > 0) {
					strCurrent = strCurrent.substring(strCurrent.indexOf("state: ")+ 7,strCurrent.length());
					strCurrent=strCurrent.trim();
				}
				jsonObject.add("current", strCurrent);
				if(strTime != null && !"null".equals(strTime) && strTime.length() > 0) {
					strTime = strTime.substring(strTime.indexOf("time: ")+ 6,strTime.length());
					strTime=strTime.trim();
				}
				jsonObject.add("Date", strTime);
				if(strUser != null && !"null".equals(strUser) && strUser.length() > 0){
					strUser = strUser.substring(strUser.indexOf("user: ")+ 6,strUser.length());
					strUser=strUser.trim();
				}
				jsonObject.add("user", strUser);
				if(strAction!=null && !strAction.equalsIgnoreCase("null") && !strAction.equals("")) 
				{
					if(strAction.indexOf("(")==0)
					{
						if(strAction.contains("Promote")||strAction.contains("promote")||strAction.contains("Demote")||strAction.contains("demote") || strAction.contains("Reject")||strAction.contains("reject"))
						strAction = strAction.substring(strAction.indexOf("(")+1,strAction.indexOf(")")).toLowerCase();			
					}
                }
				jsonObject.add("action", strAction);
				try {
					strDesc = UINavigatorUtil.getHistoryDescriptionDisplayString(context, strDescription, languageStr, "");
				} catch(Exception e) {
					strDesc = strDescription;
				}
				jsonObject.add("description", strDescription);
				jsonArr.add(jsonObject);
			}
		}catch (Exception excep)
		{
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_HISTORY_UTIL, excep);
		}
		return jsonArr;
	}
}

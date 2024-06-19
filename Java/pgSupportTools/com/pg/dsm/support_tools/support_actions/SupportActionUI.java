package com.pg.dsm.support_tools.support_actions;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;

import com.matrixone.apps.domain.util.MapList;
import com.pg.dsm.support_tools.support_actions.util.SupportAction;
import com.pg.dsm.support_tools.support_actions.util.SupportActionFactory;
import com.pg.dsm.support_tools.support_actions.util.SupportConstants;
import com.pg.dsm.support_tools.support_actions.util.SupportType;
import com.pg.dsm.support_tools.support_actions.util.SupportUtil;
import com.pg.dsm.support_tools.support_actions.actions.RegenerateGenDoc;

import matrix.db.Context;
import matrix.db.JPO;

public class SupportActionUI  implements Serializable,SupportConstants {
	private static final long serialVersionUID = -3746004817667444020L;
	public SupportActionUI() {}

	public SupportActionUI(Context context, String[] args) throws Exception {
	}

	/**DSM 2018x.3 - On Demand Support Tools 
	 * @about - This is an intermediate method to process the support actions. 
	 * @param context
	 * @param args - array of arguments
	 * @return String - processed string
	 * @throws Exception
	 * @since DSM 2018x.3 - On Demand Support Tools
	 */
	public String executeAction(Context context, String[] args) throws Exception {
		JSONObject jsonObj = new JSONObject();
		try {
			Properties config = SupportUtil.loadSupportActionConfigPage(context);
			JSONArray jsonArray = new JSONArray();
			Map programMap = (HashMap)JPO.unpackArgs(args);
			String subRollupActions = EMPTY_STRING;
			String supportContextUser = (String)programMap.get(SUPPORT_CONTEXT_USER);
			String supportAction = ((String)programMap.get(SUPPORT_ACTION)).trim();
			String objectId = (String)programMap.get(SELECT_ID);
			if(programMap.containsKey(SUPPORT_ACTION_SUB_OPTIONS)) {
				subRollupActions = (String)programMap.get(SUPPORT_ACTION_SUB_OPTIONS);
			}
			if(SupportUtil.isNotNullEmpty(objectId)) {
				MapList infoList = SupportUtil.getActionInfoList(context, objectId);
				if(infoList != null && !infoList.isEmpty()) {
					Iterator itr = infoList.iterator();
					Map<String, String> infoMap = new HashMap<String, String>();
					SupportAction supObj = null;
					while(itr.hasNext()) {
						String result = UNSUPPORTED_ACTION;
						infoMap = (Map)itr.next();
						infoMap.put(SUPPORT_ACTION, supportAction);
						infoMap.put(SUPPORT_CONTEXT_USER, supportContextUser);
						infoMap.put(SUPPORT_ACTION_SUB_OPTIONS, subRollupActions);
						infoMap.put(SUPPORT_ACTION_TYPE, config.getProperty("SupportAction.Types."+supportAction));
						supObj = SupportActionFactory.getSupportAction(context, supportAction, infoMap);
						System.out.println("Print Support Action Request :"+supObj.toString());
						if(supObj != null) {
							if(supObj.hasAccess()) {
								if(supObj.isQualified()) {
									if(supObj.checkState()) {
										result = supObj.execute();
										if(!UNSUPPORTED_ACTION.equals(result))
											supObj.recordHistory();
									} else 
										result = UNSUPPORTED_STATE;
								}
								else 
									result = UNSUPPORTED_ACTION;
							}
							infoMap.put(SUPPORT_ACTION_RESULT, result);
							infoMap.put(SUPPORT_ACTION_DISPLAY_NAME, SupportType.valueOf(supportAction).getDisplayName());
							
							infoMap.remove(SUPPORT_ACTION_TYPE);
							jsonArray.put(SupportUtil.convertMapToJSON(infoMap));
						}
					}
					//Added by DSM in 2018x.5 for Req#33939 Starts
					if(supportAction.equalsIgnoreCase(REGENERATE_GENDOC)){
						RegenerateGenDoc regenerate = new RegenerateGenDoc();
						regenerate.backgroundProcess(context, infoList, supportAction);
					}
					//Added by DSM in 2018x.5 for Req#33939 Ends
				}
				jsonObj.put(SUPPORT_ACTION_JSON_RESPONSE, jsonArray);
			}
		} catch(Exception e) {
			throw e;
		}
		System.out.println("Print Support Action Response :"+jsonObj.toString());
		return jsonObj.toString();
	}

}


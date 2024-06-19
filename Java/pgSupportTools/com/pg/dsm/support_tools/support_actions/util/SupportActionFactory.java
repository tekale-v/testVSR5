package com.pg.dsm.support_tools.support_actions.util;

import java.util.Map;

import matrix.db.Context;
/**DSM 2018x.3 - On Demand Support Tools 
 * @about - Helper Class which calls the Support Action method and performs marking according to the Action Selected. 
 */
public class SupportActionFactory {
	
	public static SupportAction getSupportAction(Context context, String action, Map<String, String> infoMap) {
		return SupportType.valueOf(action).getSupportAction(context, action, infoMap);
	}

}

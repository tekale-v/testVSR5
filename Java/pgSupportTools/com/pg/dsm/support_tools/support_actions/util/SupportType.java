package com.pg.dsm.support_tools.support_actions.util;

import java.util.Map;

import com.pg.dsm.support_tools.support_actions.actions.MarkCOS;
import com.pg.dsm.support_tools.support_actions.actions.MarkDynamicSubscription;
import com.pg.dsm.support_tools.support_actions.actions.MarkEBP;
import com.pg.dsm.support_tools.support_actions.actions.MarkGendoc;
import com.pg.dsm.support_tools.support_actions.actions.MarkRollup;
import com.pg.dsm.support_tools.support_actions.actions.RePublish;
import com.pg.dsm.support_tools.support_actions.actions.RegenerateGenDoc;
import com.pg.dsm.support_tools.support_actions.actions.ResendBOMEDelivery;
import com.pg.dsm.support_tools.support_actions.actions.ResendWND;
import com.pg.dsm.support_tools.support_actions.actions.UnMarkCOS;

import matrix.db.Context;
import matrix.util.StringList;

//Modified by DSM(Sogeti) for Req # 33939,34932

public enum SupportType {
	
	MARK_EBP(SupportConstants.MARK_EBP),
	MARK_ROLLUP(SupportConstants.MARK_ROLLUP),
	MARK_COS(SupportConstants.MARK_COS),
	UNMARK_COS(SupportConstants.UNMARK_COS),
	MARK_DYNAMIC_SUBSCRIPTION(SupportConstants.MARK_DYNAMIC_SUBSCRIPTION),
	MARK_GENDOC(SupportConstants.MARK_GENDOC),
	RESEND_BOM_EDELIVERY(SupportConstants.RESEND_BOM_EDELIVERY),
	RESEND_WND(SupportConstants.RESEND_WND),
	REGENERATE_GENDOC(SupportConstants.REGENERATE_GENDOC),
	REPUBLISH(SupportConstants.REPUBLISH);
	
	public final String _action;
	SupportType(String paramString) {
		this._action = paramString;
	}
	public String getAction() {
        return _action;
    }
	/**DSM 2018x.3 - On Demand Support Tools 
	 * @about -  Method to get the Display Name for the particular Support Action
	 * @return String - containing Display Name
	 * @throws Exception
	 */
	public String getDisplayName() {
		switch(_action) {
		case SupportConstants.MARK_EBP:
			return SupportConstants.MARK_EBP_DISPLAY_NAME; 
		case SupportConstants.MARK_ROLLUP:
			return SupportConstants.MARK_ROLLUP_DISPLAY_NAME;
		case SupportConstants.MARK_COS:
			return SupportConstants.MARK_COS_DISPLAY_NAME;
		case SupportConstants.UNMARK_COS:
			return SupportConstants.UNMARK_COS_DISPLAY_NAME;
		case SupportConstants.MARK_DYNAMIC_SUBSCRIPTION:
			return SupportConstants.MARK_DYNAMIC_SUBSCRIPTION_DISPLAY_NAME; 
		case SupportConstants.MARK_GENDOC:
			return SupportConstants.MARK_GENDOC_DISPLAY_NAME; 
		case SupportConstants.RESEND_BOM_EDELIVERY:
			return SupportConstants.RESEND_BOM_EDELIVERY_DISPLAY_NAME; 
		case SupportConstants.RESEND_WND:
			return SupportConstants.RESEND_WND_DISPLAY_NAME; 
		case SupportConstants.REGENERATE_GENDOC:
			return SupportConstants.REGENERATE_GENDOC_DISPLAY_NAME; 
		case SupportConstants.REPUBLISH:
			return SupportConstants.REPUBLISH_DISPLAY_NAME;
		}
		return SupportConstants.EMPTY_STRING;
	}
	/**DSM 2018x.3 - On Demand Support Tools 
	 * @about -  Method to get the JPO Name for the particular Support Action
	 * @return String - containing Allowed JPO 
	 * @throws Exception
	 */
	
	public String getJPO() {
		switch(_action) {
		case SupportConstants.MARK_EBP:
			return SupportConstants.MARK_EBP_JPO; 
		case SupportConstants.MARK_ROLLUP:
			return SupportConstants.MARK_ROLLUP_JPO;
		case SupportConstants.MARK_COS:
			return SupportConstants.MARK_COS_JPO;
		case SupportConstants.UNMARK_COS:
			return SupportConstants.UNMARK_COS_JPO;
		case SupportConstants.MARK_DYNAMIC_SUBSCRIPTION:
			return SupportConstants.MARK_DYNAMIC_SUBSCRIPTION_JPO; 
		case SupportConstants.MARK_GENDOC:
			return SupportConstants.MARK_GENDOC_JPO; 
		case SupportConstants.RESEND_BOM_EDELIVERY:
			return SupportConstants.RESEND_BOM_EDELIVERY_JPO; 
		case SupportConstants.RESEND_WND:
			return SupportConstants.RESEND_WND_JPO; 
		case SupportConstants.REGENERATE_GENDOC:
			return SupportConstants.REGENERATE_GENDOC_JPO;
		case SupportConstants.REPUBLISH:
			return SupportConstants.REPUBLISH_JPO; 
		}
		return SupportConstants.EMPTY_STRING;
	}
	/**DSM 2018x.3 - On Demand Support Tools 
	 * @about -  Method to get the method for the particular Support Action
	 * @return String - containing Allowed Method
	 * @throws Exception
	 */
	public String getMethod() {
		switch(_action) {
		case SupportConstants.MARK_EBP:
			return SupportConstants.MARK_EBP_METHOD; 
		case SupportConstants.MARK_ROLLUP:
			return SupportConstants.MARK_ROLLUP_METHOD;
		case SupportConstants.MARK_COS:
			return SupportConstants.MARK_COS_METHOD;
		case SupportConstants.UNMARK_COS:
			return SupportConstants.UNMARK_COS_METHOD;
		case SupportConstants.MARK_DYNAMIC_SUBSCRIPTION:
			return SupportConstants.MARK_DYNAMIC_SUBSCRIPTION_METHOD; 
		case SupportConstants.MARK_GENDOC:
			return SupportConstants.MARK_GENDOC_METHOD; 
		case SupportConstants.RESEND_BOM_EDELIVERY:
			return SupportConstants.RESEND_BOM_EDELIVERY_METHOD; 
		case SupportConstants.RESEND_WND:
			return SupportConstants.RESEND_WND_METHOD; 
		case SupportConstants.REGENERATE_GENDOC:
			return SupportConstants.REGENERATE_GENDOC_METHOD;
		case SupportConstants.REPUBLISH:
			return SupportConstants.REPUBLISH_METHOD;
		}
		return SupportConstants.EMPTY_STRING;
	}
	/**DSM 2018x.3 - On Demand Support Tools 
	 * @about -  Method to get the Exception
	 * @return String 
	 * @throws Exception
	 */
	public String getException() throws Exception {
		switch(_action) {
		case SupportConstants.MARK_EBP:
			return SupportConstants.MARK_EBP_EXCEPTION;
		case SupportConstants.MARK_ROLLUP:
			return SupportConstants.MARK_ROLLUP_EXCEPTION;
		case SupportConstants.MARK_COS:
			return SupportConstants.MARK_COS_EXCEPTION;
		case SupportConstants.UNMARK_COS:
			return SupportConstants.UNMARK_COS_EXCEPTION;
		case SupportConstants.MARK_DYNAMIC_SUBSCRIPTION:
			return SupportConstants.MARK_DYNAMIC_SUBSCRIPTION_EXCEPTION;
		case SupportConstants.MARK_GENDOC:
			return SupportConstants.MARK_GENDOC_EXCEPTION;
		case SupportConstants.RESEND_BOM_EDELIVERY:
			return SupportConstants.RESEND_BOM_EDELIVERY_EXCEPTION;
		case SupportConstants.RESEND_WND:
			return SupportConstants.RESEND_WND_EXCEPTION;
		case SupportConstants.REGENERATE_GENDOC:
			return SupportConstants.REGENERATE_GENDOC_EXCEPTION;
		case SupportConstants.REPUBLISH:
			return SupportConstants.REPUBLISH_EXCEPTION;
		}
		return SupportConstants.EMPTY_STRING;
	}
	/**DSM 2018x.3 - On Demand Support Tools 
	 * @about - Helper method to to call the another class
	 * @param context
	 * @param action
	 * @param infoMap
	 * @return null
	 * @throws Exception
	 */
	
	public SupportAction getSupportAction(Context context, String action, Map<String, String> infoMap) {
		switch(action) {
		case SupportConstants.MARK_EBP:
			return new MarkEBP(context, action, infoMap);
		case SupportConstants.MARK_ROLLUP:
			return new MarkRollup(context, action, infoMap);
		case SupportConstants.MARK_COS:
			return new MarkCOS(context, action, infoMap);
		case SupportConstants.UNMARK_COS:
			return new UnMarkCOS(context, action, infoMap);
		case SupportConstants.MARK_DYNAMIC_SUBSCRIPTION:
			return new MarkDynamicSubscription(context, action, infoMap);
		case SupportConstants.MARK_GENDOC:
			return new MarkGendoc(context, action, infoMap);
		case SupportConstants.RESEND_BOM_EDELIVERY:
			return new ResendBOMEDelivery(context, action, infoMap);
		case SupportConstants.RESEND_WND:
			return new ResendWND(context, action, infoMap);
		case SupportConstants.REGENERATE_GENDOC:
			return new RegenerateGenDoc(context, action, infoMap);			
		case SupportConstants.REPUBLISH:
			return new RePublish(context, action, infoMap);
			
		}
		return null;
	}
	/**DSM 2018x.3 - On Demand Support Tools 
	 * @about -  Method to get the Comments for History
	 * @return String 
	 * @throws Exception
	 */
	
	public String getComment() throws Exception {
		switch(_action) {
		case SupportConstants.MARK_EBP:
			return SupportConstants.MARK_EBP_COMMENT;
		case SupportConstants.MARK_ROLLUP:
			return SupportConstants.MARK_ROLLUP_COMMENT;
		case SupportConstants.MARK_COS:
			return SupportConstants.MARK_COS_COMMENT;
		case SupportConstants.UNMARK_COS:
			return SupportConstants.UNMARK_COS_COMMENT;
		case SupportConstants.MARK_DYNAMIC_SUBSCRIPTION:
			return SupportConstants.MARK_DYNAMIC_SUBSCRIPTION_COMMENT;
		case SupportConstants.MARK_GENDOC:
			return SupportConstants.MARK_GENDOC_COMMENT;
		case SupportConstants.RESEND_BOM_EDELIVERY:
			return SupportConstants.RESEND_BOM_EDELIVERY_COMMENT;
		case SupportConstants.RESEND_WND:
			return SupportConstants.RESEND_WND_COMMENT;
		case SupportConstants.REGENERATE_GENDOC:
			return SupportConstants.REGENERATE_GENDOC_COMMENT;
		case SupportConstants.REPUBLISH:
			return SupportConstants.REPUBLISH_COMMENT;
		
		}
		return SupportConstants.EMPTY_STRING;
	}
}

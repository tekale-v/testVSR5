package com.pg.dsm.support_tools.support_actions.util;

import com.matrixone.apps.domain.DomainConstants;
/**DSM 2018x.3 - On Demand Support Tools 
 * @about - Enumeration Class created for the Roll Up sub events to perform the action.
 */
public enum RollupType {
	WC(SupportConstants.WAREHOUSE_CLASSIFICATION),
	SL(SupportConstants.SMART_LABEL),
	SR(SupportConstants.STABILITY_RESULT),
	DGC(SupportConstants.DGC),
	GHS(SupportConstants.GHS),
	MC(SupportConstants.MATERIAL_CLASSIFICATION),
	BATTERY(SupportConstants.BATTERY),
	INGREDIENT(SupportConstants.INGREDIENT),
	REGISTRATION(SupportConstants.REGISTRATION);
	
	public final String _action;
	RollupType(String paramString) {
		this._action = paramString;
	}
	public String getAction() {
        return _action;
    }
	/**DSM 2018x.3 - On Demand Support Tools 
	 * @about - Helper method which has the Conditional statement and and perform the action according to the condition satisfied.
	 * @return String 
	 * @throws Exception
	 */
	public String getDisplayName() {
		switch(_action) {
		case SupportConstants.WAREHOUSE_CLASSIFICATION:
			return SupportConstants.WAREHOUSE_CLASSIFICATION_DISPLAY_NAME; 
		case SupportConstants.SMART_LABEL:
			return SupportConstants.SMART_LABEL_DISPLAY_NAME;
		case SupportConstants.STABILITY_RESULT:
			return SupportConstants.STABILITY_RESULT_DISPLAY_NAME;
		case SupportConstants.DGC:
			return SupportConstants.DGC_DISPLAY_NAME;
		case SupportConstants.GHS:
			return SupportConstants.GHS_DISPLAY_NAME; 
		case SupportConstants.MATERIAL_CLASSIFICATION:
			return SupportConstants.MATERIAL_CLASSIFICATION_DISPLAY_NAME; 
		case SupportConstants.BATTERY:
			return SupportConstants.BATTERY_DISPLAY_NAME; 
		case SupportConstants.INGREDIENT:
			return SupportConstants.INGREDIENT_DISPLAY_NAME; 
		case SupportConstants.REGISTRATION:
			return SupportConstants.REGISTRATION_DISPLAY_NAME; 
		}
		return DomainConstants.EMPTY_STRING;
	}
	/**DSM 2018x.3 - On Demand Support Tools 
	 * @about - Helper method which has the Conditional statement and and perform the action according to the condition satisfied.
	 * @return String 
	 * @throws Exception
	 */
	public String getEventName() {
		switch(_action) {
		case SupportConstants.WAREHOUSE_CLASSIFICATION:
			return SupportConstants.WAREHOUSE_CLASSIFICATION_EVENT_NAME; 
		case SupportConstants.SMART_LABEL:
			return SupportConstants.SMART_LABEL_EVENT_NAME;
		case SupportConstants.STABILITY_RESULT:
			return SupportConstants.STABILITY_RESULT_EVENT_NAME;
		case SupportConstants.DGC:
			return SupportConstants.DGC_EVENT_NAME;
		case SupportConstants.GHS:
			return SupportConstants.GHS_EVENT_NAME; 
		case SupportConstants.MATERIAL_CLASSIFICATION:
			return SupportConstants.MATERIAL_CLASSIFICATION_EVENT_NAME;  
		case SupportConstants.BATTERY:
			return SupportConstants.BATTERY_EVENT_NAME; 
		case SupportConstants.INGREDIENT:
			return SupportConstants.INGREDIENT_EVENT_NAME; 
		case SupportConstants.REGISTRATION:
			return SupportConstants.REGISTRATION_EVENT_NAME; 
		}
		return DomainConstants.EMPTY_STRING;
	}
}

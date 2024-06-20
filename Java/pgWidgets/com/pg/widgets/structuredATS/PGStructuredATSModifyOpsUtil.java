package com.pg.widgets.structuredats;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import java.util.HashMap;
import com.pg.widgets.util.PGWidgetConstants;
import com.pg.widgets.util.PGWidgetUtil;
import com.matrixone.apps.awl.util.BusinessUtil;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.DomainRelationship;
import matrix.db.Context;

public class PGStructuredATSModifyOpsUtil {
	static final Logger logger = Logger.getLogger(PGStructuredATSModifyOpsUtil.class.getName());

	/**
	 * This method is used to modify connected BOM & ALTERNATE Operations to SATS
	 * 
	 * @param context
	 * @param mpRequestMap
	 * @return String
	 * @throws Exception
	 */
	public String modifyBOMSATS(Context context, String strJsonInput) throws Exception {
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		String strATSOpsId = null;
		String strTargetId = null;
		Map<String, String> mAttribute = null;
		String strToObjectState = null;
		String strToObjectName = null;
		boolean bHasReadAccessOnCurrent = false;
		DomainRelationship doRel = null;
		try {
			ContextUtil.startTransaction(context, true);
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strJsonInput);
			JsonArray jsonDataArray = jsonInputData.getJsonArray(PGStructuredATSConstants.KEY_DATA);
			int iBOMArraySize = jsonDataArray.size();
			for (int k = 0; k < iBOMArraySize; k++) {
					JsonObject jsonInputObj = jsonDataArray.getJsonObject(k);
					strATSOpsId = jsonInputObj.getString(PGStructuredATSConstants.KEY_ATS_OPR_RELID);
					strTargetId = jsonInputObj.getString(PGStructuredATSConstants.KEY_TARGET_ID);
					JsonObject jsonRelAttributes = jsonInputObj.getJsonObject(PGStructuredATSConstants.KEY_TARGET_ID_ATTRIBUTES);
					mAttribute = getAttributeMapFromJson(jsonRelAttributes);
					if (BusinessUtil.isNotNullOrEmpty(strTargetId)) {
						logger.info("To Object id : " + strTargetId);
						DomainObject doRMObj = DomainObject.newInstance(context, strTargetId);
						strToObjectState = doRMObj.getInfo(context, DomainObject.SELECT_CURRENT);
						strToObjectName = doRMObj.getInfo(context, DomainObject.SELECT_NAME);
						bHasReadAccessOnCurrent = FrameworkUtil.hasAccess(context, doRMObj,
								PGStructuredATSConstants.ACCESS_READ);
						logger.info("To Object id state : " + strToObjectState);
					}
					if (PGStructuredATSConstants.STATE_OBSOLETE.equals(strToObjectState) || !bHasReadAccessOnCurrent) {
						if (PGStructuredATSConstants.STATE_OBSOLETE.equals(strToObjectState)) {
							jsonReturnObj.add(PGStructuredATSConstants.KEY_STATUS,
									PGStructuredATSConstants.STRING_ERR_ATS + strToObjectName
											+ PGStructuredATSConstants.STRING_ERR_STATE);
						} else if (!bHasReadAccessOnCurrent) {
							jsonReturnObj.add(PGStructuredATSConstants.KEY_STATUS,
									PGStructuredATSConstants.STRING_ERR_ACCESS + strToObjectName);
						}
					} else {
						doRel = DomainRelationship.newInstance(context, strATSOpsId);
						if (null != mAttribute && mAttribute.size() > 0) {
							doRel.setAttributeValues(context, mAttribute);
						}
						jsonReturnObj.add(PGStructuredATSConstants.KEY_STATUS, PGStructuredATSConstants.VALUE_SUCCESS);
					}
			}
			ContextUtil.commitTransaction(context);
		} catch (Exception excep) {
			ContextUtil.abortTransaction(context);
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
			throw excep;
		}
		return jsonReturnObj.build().toString();
	}
	
	/**
	 * Method to get attributes Map from Json object
	 * 
	 * @param jsonRelAttributes
	 * @return
	 */
	private Map<String, String> getAttributeMapFromJson(JsonObject jsonRelAttributes) {
		Map<String, String> mpAttributeInfoMap = new HashMap<>();

		jsonRelAttributes.forEach((strKey, strValue) -> {
			mpAttributeInfoMap.put(strKey, strValue.toString());
		});

		return mpAttributeInfoMap;
	}
	
}

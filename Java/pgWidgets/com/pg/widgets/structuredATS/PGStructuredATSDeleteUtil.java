package com.pg.widgets.structuredats;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonArray;
import javax.json.JsonObjectBuilder;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import com.pg.widgets.util.PGWidgetConstants;
import com.pg.widgets.util.PGWidgetUtil;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.apps.domain.util.FrameworkUtil;
import matrix.db.Context;
import matrix.util.StringList;

public class PGStructuredATSDeleteUtil {
	static final Logger logger = Logger.getLogger(PGStructuredATSDeleteUtil.class.getName());

	/**
	 * Method to delete replaced items for SATS
	 * @param context
	 * @param strJsonInput
	 * @return
	 * @throws Exception
	 */
	public String deleteSATSDataForBOM(Context context, String strJsonInput) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		String strReturn = "";
		String strError = "";
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strJsonInput);
			JsonArray jsonInputArray = jsonInputData.getJsonArray(PGStructuredATSConstants.KEY_DATA);
			int iArraySize = jsonInputArray.size();
			for(int i=0;i<iArraySize;i++) {
				JsonObject jsonDelInfoObj = jsonInputArray.getJsonObject(i);
				String strATSOperationRelId = jsonDelInfoObj.getString(PGStructuredATSConstants.KEY_ATS_OPERATION_RELID);
				String strParentSubObjId = jsonDelInfoObj.getString(PGStructuredATSConstants.KEY_PARENT_SUB_ID);
				PGStructuredATSUtil objStructuredATSUtil = new PGStructuredATSUtil();
				PGStructuredATSWhereUsedUtil objStructuredATSWhereUsedUtil = new PGStructuredATSWhereUsedUtil();

				if (UIUtil.isNotNullAndNotEmpty(strParentSubObjId)) {
					String[] strOIDArray = new String[1];
					strOIDArray[0] = strParentSubObjId;
					String strObjSelect = PGStructuredATSConstants.SELECT_RELATED_ATS_OPERATION_IDS;
					MapList mlRelatedChilInfoList = DomainObject.getInfo(context, strOIDArray,
							new StringList(strObjSelect));
					if (mlRelatedChilInfoList != null && !mlRelatedChilInfoList.isEmpty()) {
						Map<?, ?> mpRelatedObjInfoMap = (Map<?, ?>) mlRelatedChilInfoList.get(0);
						if (mpRelatedObjInfoMap.containsKey(strObjSelect)) {
							Object objRelatedObjIds = mpRelatedObjInfoMap.get(strObjSelect);
							StringList slObjIdList = objStructuredATSWhereUsedUtil
									.getStringListFromObject(objRelatedObjIds);
							if (slObjIdList.size() == 1) {
								String strATSRelId = slObjIdList.get(0);
								if (strATSRelId.equals(strATSOperationRelId)) {
									strReturn = objStructuredATSUtil.deleteObject(context, strParentSubObjId);
								}
							} else {
								strReturn = objStructuredATSUtil.deleteRelationship(context, strATSOperationRelId);
							}
						}
					}

				} else {
					strReturn = objStructuredATSUtil.deleteRelationship(context, strATSOperationRelId);
				}
				
				if(strReturn.contains(PGWidgetConstants.KEY_ERROR)) {
					strError = strReturn;
				}
			}

		} catch (Exception excep) {
			output.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
			return output.build().toString();
		}
		
		if(UIUtil.isNotNullAndNotEmpty(strError)) {
			return strError;
		} else {
			return strReturn;
		}

	}
	
	/**
	 * This method is used to delete connected Performance Characteristics Operations to SATS
	 * 
	 * @param context
	 * @param mpRequestMap
	 * @return String
	 * @throws Exception
	 */
	public String deletePCSATS(Context context, Map mpRequestMap) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		String strTargetId = (String) mpRequestMap.get(PGStructuredATSConstants.KEY_TARGET_ID);
		String strReturnVal = null;
		String getSelectedKey = null;
		try {
			StringList slList = FrameworkUtil.split(strTargetId, PGStructuredATSConstants.CONSTANT_STRING_PIPE);
			for (String strPerfChar : slList) {
				getSelectedKey = strPerfChar;
			if (UIUtil.isNotNullAndNotEmpty(getSelectedKey)) {
			PGStructuredATSUtil pgStructuredATSUtil = new PGStructuredATSUtil();
			strReturnVal = pgStructuredATSUtil.deleteObject(context, getSelectedKey);
		}else {
			output.add(PGStructuredATSConstants.KEY_STATUS,PGStructuredATSConstants.STRING_ERR_DELETE);
			strReturnVal = output.build().toString();
		}
		}
		}
		catch (Exception excep) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_PERFORMANCE_UTIL, excep);
			output.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
			return output.build().toString();
		}
		return strReturnVal;
	}
}

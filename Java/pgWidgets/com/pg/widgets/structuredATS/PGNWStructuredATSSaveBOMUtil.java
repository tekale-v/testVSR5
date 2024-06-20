package com.pg.widgets.structuredats;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Hashtable;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import matrix.db.Context;
import matrix.db.BusinessInterface;
import matrix.db.Vault;
import matrix.util.Pattern;
import matrix.util.StringList;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainConstants;
import javax.ws.rs.core.Response;
import javax.servlet.http.HttpServletResponse;

import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.widgets.util.PGWidgetConstants;
import com.pg.widgets.util.PGWidgetUtil;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.dassault_systemes.catrgn.pervasive.json.tokens.JsonValue;
import com.dassault_systemes.enovia.enterprisechangemgt.common.ChangeAction;
import com.dassault_systemes.enovia.enterprisechangemgt.util.ChangeUtil;
import com.matrixone.apps.domain.util.MqlUtil;


public class PGNWStructuredATSSaveBOMUtil {

	private static final Logger logger = Logger.getLogger(PGNWStructuredATSSaveBOMUtil.class.getName());

	static StringList slObjectSelects = new StringList();
	/**
	 * This method is used to delete connected WhereUsed Operations to SATS
	 * @param context
	 * @param strJsonInput
	 * @return
	 */
	public String saveStructureATSBOM(Context context, String strJsonInput) {
		StringBuilder sbMsg = new StringBuilder();
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		boolean bCont = false;
		try {

			ContextUtil.startTransaction(context, true);
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strJsonInput);
			String strSATSId = jsonInputData.getString("id");
			JsonArray jsonBOMArray = jsonInputData.getJsonArray(PGStructuredATSConstants.KEY_DATA);
			PGStructuredATSReplaceOperationsUtil pgSATSWhereUsedBOMOperationsUtil = new PGStructuredATSReplaceOperationsUtil();
			PGStructuredATSModifyOpsUtil pgSATSModify = new PGStructuredATSModifyOpsUtil();
			String strOpr = "";
			int iBOMArraySize = jsonBOMArray.size();
			JsonArrayBuilder jsonAddAdjust = Json.createArrayBuilder();
			String strStatus = "";
			for (int i = 0; i < iBOMArraySize; i++) {
				JsonObjectBuilder jsRetObj = Json.createObjectBuilder();
				jsRetObj.add(DomainConstants.SELECT_ID, strSATSId);
				JsonArrayBuilder jsonTempArrBuilder = Json.createArrayBuilder();
				JsonObject jsonTemp = jsonBOMArray.getJsonObject(i);
				
				strOpr = jsonTemp.getString(PGStructuredATSConstants.KEY_ALLSMALL_OPERATION);
				
				JsonObjectBuilder jsTempBuild = Json.createObjectBuilder();
				if(jsonTemp.containsKey(PGStructuredATSConstants.PREFIX_CTX_FOP))
				{
					jsTempBuild.add(PGStructuredATSConstants.KEY_PARENT_ID, jsonTemp.getJsonString(PGStructuredATSConstants.PREFIX_CTX_FOP+"id"));
				}
				if(PGStructuredATSConstants.KEY_ADJUST.equalsIgnoreCase(strOpr))
				{
					jsTempBuild.add(PGStructuredATSConstants.KEY_TARGET_ID, jsonTemp.getJsonString(PGStructuredATSConstants.KEY_SOURCE_ID));
				}
				if(PGStructuredATSConstants.ACTION_MODIFY.equalsIgnoreCase(strOpr))
				{
					jsTempBuild.add(PGStructuredATSConstants.KEY_ATS_OPR_RELID, jsonTemp.getJsonString(PGStructuredATSConstants.KEY_REL_ID));
					jsTempBuild.add(PGStructuredATSConstants.KEY_TARGET_ID_ATTRIBUTES,jsonTemp.getJsonObject(PGStructuredATSConstants.KEY_REL_ATTRIBUTES));
					jsTempBuild.add(DomainConstants.SELECT_ID, strSATSId);
				}
				JsonObject jsonInputField = updateJsonObject(context,jsonTemp,jsTempBuild);
				if(PGStructuredATSConstants.ACTION_CAP_ADD.equalsIgnoreCase(strOpr) || PGStructuredATSConstants.KEY_ADJUST.equalsIgnoreCase(strOpr))
				{
					jsonAddAdjust.add(jsonInputField);
				
				}
				else if(PGStructuredATSConstants.ACTION_MODIFY.equalsIgnoreCase(strOpr))
				{	
					jsonTempArrBuilder.add(jsonInputField);
					jsRetObj.add(PGStructuredATSConstants.KEY_DATA, jsonTempArrBuilder);
					strStatus = pgSATSModify.modifyBOMSATS(context, jsRetObj.build().toString());
					if(strStatus.contains(PGStructuredATSConstants.STATUS_ERROR))
					{					
						sbMsg.append(strStatus);
					}
				}
				else if(PGStructuredATSConstants.OPR_BALANCINGMATERIAL.equalsIgnoreCase(strOpr))
				{
					jsonTempArrBuilder.add(jsonInputField);
					jsRetObj.add(PGStructuredATSConstants.KEY_DATA, jsonTempArrBuilder);
					strStatus = pgSATSWhereUsedBOMOperationsUtil.connectBalancingMaterial(context, jsRetObj.build().toString());
					if(strStatus.contains(PGStructuredATSConstants.STATUS_ERROR))
					{					
						sbMsg.append(strStatus);
					}
				}
			}
			//For Add and Adjust
			if(UIUtil.isNotNullAndNotEmpty(jsonAddAdjust.build().toString())) {
				JsonObjectBuilder jsonRetAddAdjustObj = Json.createObjectBuilder();
				jsonRetAddAdjustObj.add(PGStructuredATSConstants.KEY_DATA, jsonAddAdjust);
				jsonRetAddAdjustObj.add(DomainConstants.SELECT_ID, strSATSId);
				strStatus = pgSATSWhereUsedBOMOperationsUtil.replaceOperationsSATS(context, jsonRetAddAdjustObj.build().toString());
				if(strStatus.contains(PGStructuredATSConstants.STATUS_ERROR))
				{
					sbMsg.append(strStatus);
				}
			
			}
			if(UIUtil.isNullOrEmpty(sbMsg.toString()))
			{
				sbMsg.append(PGStructuredATSConstants.VALUE_SUCCESS);
			}
			 ContextUtil.commitTransaction(context);

		} catch (Exception e) {
			ContextUtil.abortTransaction(context);
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_WHEREUSED_DELETE, e);
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
		}



		return sbMsg.toString();
	}
	/**
	 * @param context
	 * @param jsontemp
	 * @param jsTempBuild
	 * @return
	 */
	public JsonObject updateJsonObject(Context context, JsonObject jsontemp, JsonObjectBuilder jsTempBuild)
	{
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder = jsTempBuild;
		jsontemp.forEach(builder::add);
		return builder.build();
		
	}
	}

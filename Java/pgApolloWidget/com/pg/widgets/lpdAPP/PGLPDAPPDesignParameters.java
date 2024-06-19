package com.pg.widgets.lpdAPP;

import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.png.apollo.pgApolloConstants;
import com.png.apollo.dashboard.util.ApolloWidgetServiceUtil;
import com.png.apollo.designtool.getData.ReadWriteXMLForPLMDTDocument;

import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;

public class PGLPDAPPDesignParameters extends pgApolloConstants{

	public static JsonObject getDesignParameters(matrix.db.Context context , String strAPPId, String sMode, boolean showPreviousCollab)throws Exception{
		
		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonArray jsonArray = null;
		
		//Check is LPD APP
		Map programMap = new HashMap();
		programMap.put("objectId", strAPPId);
		programMap.put("objectType", TYPE_ASSEMBLED_PRODUCT_PART);
		String []args = JPO.packArgs(programMap);
		boolean isLPDAPP = (boolean)JPO.invoke(context, "pgDSMLayeredProductUtil", null, "isLayeredProductPart", args, Object.class);
		
		if(!isLPDAPP) {
			throw new MatrixException("Only LPD APP is supported");
		}
		
		MapList designParameterList =  getValuesFromXML(context,strAPPId, sMode, showPreviousCollab);
		
		jsonArray =ApolloWidgetServiceUtil.convertMapListToJsonArray(designParameterList);
		output.add("APPDesignParameters", jsonArray);
		return output.build();
	}
	
	/**
	 * Method to get Values from XML based on mode passed
	 * @param context
	 * @param objectId
	 * @param sMode
	 * @param showPreviousCollab 
	 * @return
	 * @throws Exception
	 */
	public static MapList getValuesFromXML(Context context,String objectId, String sMode, boolean showPreviousCollab) throws Exception 
	{				
		MapList mlFinalList = new MapList();		
		String strPreviousObjectId = DomainConstants.EMPTY_STRING;
		if(UIUtil.isNotNullAndNotEmpty(objectId))
		{
			DomainObject domPartObject = DomainObject.newInstance(context,objectId);							
			boolean bPreviousAPPRevExists = false;
			BusinessObject boPreviousRevision= domPartObject.getPreviousRevision(context);
			if(null!=boPreviousRevision && boPreviousRevision.exists(context))
			{
				bPreviousAPPRevExists = true;
				strPreviousObjectId = boPreviousRevision.getObjectId(context);
			}
			
			String sAPPName = (String)domPartObject.getInfo(context, DomainConstants.SELECT_NAME);
			
			StringBuilder sbFileNameDP = new StringBuilder();
			sbFileNameDP.append(pgApolloConstants.STR_AUTOMATION_DESIGN_PARAMETER_FILE_NAME).append(pgApolloConstants.CONSTANT_STRING_UNDERSCORE).append(sAPPName).append(pgApolloConstants.STR_XMLFILE_EXTENSION);

			String sCurrentDPName = sbFileNameDP.toString();
			
			sbFileNameDP = new StringBuilder();
			sbFileNameDP.append(pgApolloConstants.STR_AUTOMATION_DESIGN_PARAMETER_PREVIOUS_FILE_NAME).append(pgApolloConstants.CONSTANT_STRING_UNDERSCORE).append(sAPPName).append(pgApolloConstants.STR_XMLFILE_EXTENSION);
				
			String sPreviousDPName = sbFileNameDP.toString();
			
			Map mapXMLParameter = ReadWriteXMLForPLMDTDocument.checkoutAndGetConfigParameterParameters(context, objectId, sCurrentDPName, true, true);
			
			mlFinalList = ApolloWidgetServiceUtil.getMapListForDesignParameterView(mapXMLParameter, sMode);
			
		
			if(bPreviousAPPRevExists)
			{
				mlFinalList = ApolloWidgetServiceUtil.updateDesignParameterListForChange(context, strPreviousObjectId, mlFinalList, sMode);
			}	
			
			if(showPreviousCollab)
			{
				Map mapPreviousXMLParameter = ReadWriteXMLForPLMDTDocument.checkoutAndGetConfigParameterParameters(context, objectId, sPreviousDPName, true, true);
				
				
				MapList mlPreviousList = ApolloWidgetServiceUtil.getMapListForDesignParameterView(mapPreviousXMLParameter, sMode);
								
				mlFinalList = ApolloWidgetServiceUtil.updateDesignParamListWithPreviousCollaboration(mlFinalList, mlPreviousList);
				

			}	
			
		}
		return mlFinalList;
	}

}

package com.pg.designtools.migration.impl;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dassault_systemes.enovia.versioning.services.VersioningServices;
import com.dassault_systemes.enovia.versioning.util.ENOVersioningException;
import com.dassault_systemes.enovia.versioning.util.ENOVersioningOptions;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.vplmintegration.util.VPLMIntegTraceUtil;
import com.pg.designtools.datamanagement.DataConstants;
import matrix.db.Context;
import matrix.util.MatrixException;

public class CreateVPMReferenceRevision {
	private static final Logger logger = LoggerFactory.getLogger("DT_MIGRATION"); 
	
	/**
	 * Main method to create revision of the VPMReference object
	 * @param context
	 * @param doObject
	 * @return Physical id of data to be revised
	 * @throws  MatrixException, ENOVersioningException
	 */
	public String execute(Context context, DomainObject doObject) throws MatrixException, ENOVersioningException {
		VPLMIntegTraceUtil.trace(context, ">>> Start of CreateVPMReferenceRevision: execute method");
		logger.debug(">>> Start of CreateVPMReferenceRevision: execute method");
		String strVPMReferenceId="";
				
		String strPhysicalId=doObject.getInfo(context, DomainConstants.SELECT_PHYSICAL_ID);
		VPLMIntegTraceUtil.trace(context, ">>> strPhysicalId::"+strPhysicalId);
		logger.debug(">>> strPhysicalId::"+strPhysicalId);
		
		String paramString1="0";
		String paramString2="0";
		String paramString3="0";
		String paramString4="0";
		String paramString5="0";
		String paramString6="0";
		String paramString7="0";
		String paramString8="0";
		String paramString9="0";
		String paramString10="0";
		String paramString11="1";
		String paramString12="ALL";
		String paramString13="DUPL_";
		String paramString14="1";
		
		ENOVersioningOptions eNOVersioningOptions = new ENOVersioningOptions(paramString1, paramString2, paramString3, paramString4, paramString5, paramString6, paramString7, 
				paramString8, paramString9, paramString10, paramString11, paramString12, paramString13, paramString14);

		VersioningServices versioningServices = new VersioningServices();
		
		JSONObject objOptions = new JSONObject();
	    objOptions.put("reserve", "inherit");
	    objOptions.put("fileCopy", true);
	    objOptions.put("configuration", "reset");
	    objOptions.put("maturity", "reset");
	    objOptions.put("updatestamp", "reset");
	    objOptions.put("ein", "inherit");
	    objOptions.put("withPost", false);
	    
	    JSONObject objAttributes = new JSONObject();
	    objAttributes.put(DomainConstants.SELECT_NAME, "PLMReference.V_versionComment");
	    objAttributes.put("value", DataConstants.CONSTANT_CAD2SPEC_CONVERSION_PROCESS);
	    
	    JSONArray arrayAttributes  = new JSONArray();
	    arrayAttributes.put(objAttributes);
	    
	    JSONObject objAncestors = new JSONObject();
	    objAncestors.put(DomainConstants.SELECT_ID, strPhysicalId);
	    objAncestors.put("semantic", "LAST");
	    
	    JSONArray arrayAncestors  = new JSONArray();
	    arrayAncestors.put(objAncestors);
	    
	    JSONObject objAddRequests = new JSONObject();
	    objAddRequests.put("copyId", strPhysicalId);
	    objAddRequests.put("t", "B");
	    objAddRequests.put("prefix", "EVOL_");
	    objAddRequests.put("ancestors", arrayAncestors);
	    objAddRequests.put("attributes", arrayAttributes);
	    objAddRequests.put("options", objOptions);
	    
	    JSONArray arrayAddRequests  = new JSONArray();
	    arrayAddRequests.put(objAddRequests);
	    
	    JSONObject objMainAddRequests = new JSONObject();
	    objMainAddRequests.put("addRequests", arrayAddRequests);
		
	    VPLMIntegTraceUtil.trace(context, ">>> JSON input:::"+objMainAddRequests.toString());
	    logger.debug( ">>> JSON input:::"+objMainAddRequests.toString());
	    
	   String strResult= versioningServices.addVersions(context, eNOVersioningOptions, objMainAddRequests.toString());
	   
	   VPLMIntegTraceUtil.trace(context, ">>> strResult:::"+strResult);
	   logger.debug( ">>> strResult:::"+strResult);
	   
		if(UIUtil.isNotNullAndNotEmpty(strResult)) {
			
			JSONObject jsonObject=new JSONObject(strResult);
			 VPLMIntegTraceUtil.trace(context, ">>> strResult jsonObject:::"+jsonObject);
			 logger.debug(">>> strResult jsonObject:::"+jsonObject);
	
			JSONArray addReq=(JSONArray) jsonObject.get("addRequests");
			ArrayList arrIds=(ArrayList) IntStream.range(0, addReq.length()).mapToObj(index -> ((JSONObject)addReq.get(index)).optString("id")).collect(Collectors.toList());
			strVPMReferenceId= (String) arrIds.get(0);
			
		}
			
		VPLMIntegTraceUtil.trace(context, ">>> physical id of revised VPMReference:::"+strVPMReferenceId);
		 VPLMIntegTraceUtil.trace(context, "<<< End of CreateVPMReferenceRevision: execute method");
		 
		 logger.debug(">>> physical id of revised VPMReference:::"+strVPMReferenceId);
		 logger.debug("<<< End of CreateVPMReferenceRevision: execute method");
	
		return strVPMReferenceId;
	}
}

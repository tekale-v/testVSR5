package com.pg.designtools.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.json.JSONArray;
import org.json.JSONObject;
import com.dassault_systemes.enovia.versioning.services.VersioningServices;
import com.dassault_systemes.enovia.versioning.util.ENOVersioningException;
import com.dassault_systemes.enovia.versioning.util.ENOVersioningOptions;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.vplmintegration.util.VPLMIntegTraceUtil;
import matrix.db.Context;
import matrix.util.MatrixException;

public class LifecycleManagement {
	/**
	 * Main method to create evolution/duplication  of the object
	 * @param context
	 * @param doObject
	 * @param strOperation (semantic to be used NEW for evolution DUPLICATE for duplication)
	 * @param prefix
	 * @param Maplist of attributes to be set on new object
	 * @return Physical id of new data
	 * @throws MatrixException, ENOVersioningException
	 */
	public Map manageLifecycleOperation(Context context, String strPhysicalId,String strOperation,String strPrefix, MapList mlAttr) throws ENOVersioningException {
		VPLMIntegTraceUtil.trace(context, ">>> Start of manageLifecycleOperation method");
		Map mpVPMRefObject=new HashMap();
			String strNewObjectId="";
					
			VPLMIntegTraceUtil.trace(context,">>> context::"+context.getUser());
			VPLMIntegTraceUtil.trace(context,">>> role::"+context.getRole());
			VPLMIntegTraceUtil.trace(context, ">>> strPhysicalId::"+strPhysicalId);
			VPLMIntegTraceUtil.trace(context, ">>> strOperation::"+strOperation);
			VPLMIntegTraceUtil.trace(context, ">>> strPrefix::"+strPrefix);
			VPLMIntegTraceUtil.trace(context, ">>> mlAttr::"+mlAttr);
			
			String paramString1="0";		//iAnswerFormat Decide information to be returned by this web-services either graph mode ("graph") or Version creation request mode ("attribute")
			String paramString2="0";		//iWithCopy Add in output JSON message "Copy From" information
			String paramString3="0";		//iWithThumbnail Request thumbnails and icons associated to object revised or duplicated
			String paramString4="0";      //iWithIsLastVersion  Request information if object could be considered as last of the branches according selected intent in validIntents parameter
			String paramString5="0";     //iWithAttributes Request some basic object attributes including system ones
			String paramString6="0";     //iWithSystemAttributes Request system object attributes.
			String paramString7="0";    //iWithInputAttributes Request that attributes in update clause are added in select clause
			String paramString8="0";    //iWithAdjacentVersion Compute adjacent object in context of "graph" output mode. If not set or set to "1", files will be carried over.
			String paramString9="0";   //iWithModifiedObjects Include modified or created objects during version creation transaction. This option is only available in context of "attributes" answer mode. 0 to not include this information
			String paramString10="0";  //iWithNlsInformation Provide support of the end-user language
			String paramString11="1";  //iCopyFiles Define if at revise/duplicate the files are carried over on new created objects
			String paramString12="ALL";  //iValidIntents Define Version Semantic Intent to be considered.. If not set or set to "ALL", all Version Semantic intents are considered.
			String paramString13="DUPL_";  //iWithClonePrefix Provide a prefix."DUPL_" is used as default value if this parameter is not present.
			String paramString14="1";   //iInitializationBL Activate/deactivate Initialization Business Logic execution for duplication
	
			
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
		    
		    JSONArray arrayAttributes  = new JSONArray();
	    
		    if(null!=mlAttr && !mlAttr.isEmpty()) {
		    	JSONObject objAttributes;
		    	Map mpAttr;
	    	
		    	for(int i=0;i<mlAttr.size();i++) {
		    		mpAttr=(Map)mlAttr.get(i);
				    objAttributes = new JSONObject();
				    objAttributes.put(DomainConstants.SELECT_NAME, mpAttr.get(DomainConstants.SELECT_NAME));
				    objAttributes.put("value", mpAttr.get("value"));
				    arrayAttributes.put(objAttributes);
		    	}
		    }
	    
		    JSONObject objAncestors = new JSONObject();
		    objAncestors.put(DomainConstants.SELECT_ID, strPhysicalId);
		    objAncestors.put("semantic", strOperation);
	    
		    JSONArray arrayAncestors  = new JSONArray();
		    arrayAncestors.put(objAncestors);
	    
		    JSONObject objAddRequests = new JSONObject();
		    objAddRequests.put("copyId", strPhysicalId);
		    objAddRequests.put("t", "B");
		    objAddRequests.put("prefix", strPrefix);
		    objAddRequests.put("ancestors", arrayAncestors);
		    objAddRequests.put("options", objOptions);
		    if(null!=mlAttr && !mlAttr.isEmpty()) {
			    objAddRequests.put("attributes", arrayAttributes);
		    }
	    
		    JSONArray arrayAddRequests  = new JSONArray();
		    arrayAddRequests.put(objAddRequests);
		    
		    JSONObject objMainAddRequests = new JSONObject();
		    objMainAddRequests.put("addRequests", arrayAddRequests);
			
		    VPLMIntegTraceUtil.trace(context, ">>> JSON input:::"+objMainAddRequests.toString());
	    
		    String strResult= versioningServices.addVersions(context, eNOVersioningOptions, objMainAddRequests.toString());
	   
		    VPLMIntegTraceUtil.trace(context, ">>> strResult:::"+strResult);
	   
		    if(UIUtil.isNotNullAndNotEmpty(strResult)) {
			
				JSONObject jsonObject=new JSONObject(strResult);
				 VPLMIntegTraceUtil.trace(context, ">>> strResult jsonObject:::"+jsonObject);
		
				JSONArray addReq=(JSONArray) jsonObject.get("addRequests");
				ArrayList arrIds=(ArrayList) IntStream.range(0, addReq.length()).mapToObj(index -> ((JSONObject)addReq.get(index)).optString("id")).collect(Collectors.toList());
				strNewObjectId= (String) arrIds.get(0);
			
				mpVPMRefObject.put("validRev",strNewObjectId);
				VPLMIntegTraceUtil.trace(context, ">>> physical id of new object:::"+strNewObjectId);
				
				if(UIUtil.isNullOrEmpty(strNewObjectId)) {
					String errorMessage= (String) jsonObject.get("errorMessage");
					VPLMIntegTraceUtil.trace(context, ">>> errorMessage :::"+errorMessage);
					throw new ENOVersioningException(errorMessage);
				}
		    }
		 VPLMIntegTraceUtil.trace(context, "<<< End of manageLifecycleOperation method");
		 
		return mpVPMRefObject;
	}
}
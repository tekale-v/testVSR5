package com.pg.designtools.services;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.dassault_systemes.platform.restServices.RestService;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.servlet.Framework;
import com.matrixone.vplmintegration.util.VPLMIntegTraceUtil;
import com.pg.designtools.datamanagement.CommonUtility;
import com.pg.designtools.datamanagement.DataConstants;
import com.pg.designtools.util.LifecycleManagement;

import matrix.util.StringList;

@Path("/DataMgmtServices")
public class LifecycleMgmtServices extends RestService {
	protected matrix.db.Context context; 
	
	@GET
	@Path("/copyData")
	public Response manageLifecycle(@Context HttpServletRequest req) {

		VPLMIntegTraceUtil.trace(context, ">>>> START of manageLifecycle method");
		String strNewObjectPhysicalId="";
		try {
			context = Framework.getContext(req.getSession(false)); 
			String strType="";
			String strName="";
			String strRevision="";
			String strPhysicalId="";
			String strOperation="";
			String strPrefix="";
			MapList mlAttr=new MapList();
			String queryString = req.getQueryString();
			
			queryString =  java.net.URLDecoder.decode(queryString,"UTF-8");
			String[] urlParam;
			
			if (UIUtil.isNotNullAndNotEmpty(queryString)) {
				urlParam = queryString.split("&");
				
				if (urlParam.length > 0) {
					strType=urlParam[0];
					strName=urlParam[1];
					strRevision=urlParam[2];
					strOperation=urlParam[3];
				}
				if(urlParam.length > 4)
					strPrefix=urlParam[4];
			}
			
			VPLMIntegTraceUtil.trace(context, ">>>> strType::"+strType+" name::"+strName+" revision::"+strRevision);
			VPLMIntegTraceUtil.trace(context, ">>>> strOperation::"+strOperation);
			VPLMIntegTraceUtil.trace(context, ">>>> strPrefix::"+strPrefix);

			if (UIUtil.isNotNullAndNotEmpty(strType) && UIUtil.isNotNullAndNotEmpty(strName) && UIUtil.isNotNullAndNotEmpty(strRevision) && UIUtil.isNotNullAndNotEmpty(strOperation)) {
				
				CommonUtility commonUtility=new CommonUtility(context);
				MapList mlObjectInfo=commonUtility.findObjectWithWhereClause(context, strType, strName, strRevision, "", new StringList(DataConstants.SELECT_PHYSICALID));
				
				VPLMIntegTraceUtil.trace(context, ">>>> mlObjectInfo::"+mlObjectInfo);
				
				if(null!=mlObjectInfo && !mlObjectInfo.isEmpty()) {
					Map mpObjectInfo=(Map)mlObjectInfo.get(0);
					strPhysicalId=(String)mpObjectInfo.get(DataConstants.SELECT_PHYSICALID);
				}
				VPLMIntegTraceUtil.trace(context, ">>>> strPhysicalId::"+strPhysicalId);
				
				if(DataConstants.TYPE_VPMREFERENCE.equals(strType) && "NEW".equals(strOperation)) {
					Map<String,String> mpAttr=new HashMap<>();
					mpAttr.put(DomainConstants.SELECT_NAME, DataConstants.ATTRIBUTE_V_VERSION_COMMENT);
					mpAttr.put("value", DataConstants.CONSTANT_CAD2SPEC_CONVERSION_PROCESS);
					
					mlAttr.add(mpAttr);
				}
				VPLMIntegTraceUtil.trace(context, ">>>> mlAttr::"+mlAttr);
				
				LifecycleManagement lifecycleMgmt=new LifecycleManagement();
				Map mpNewObject=lifecycleMgmt.manageLifecycleOperation(context, strPhysicalId, strOperation, strPrefix, mlAttr);
				VPLMIntegTraceUtil.trace(context, ">>>> mpNewObject::"+mpNewObject);
				
				if(!mpNewObject.isEmpty())
					strNewObjectPhysicalId=(String) mpNewObject.get("validRev");
			}else if(UIUtil.isNullOrEmpty(strType) || UIUtil.isNullOrEmpty(strName) || UIUtil.isNullOrEmpty(strRevision)) {
				throw new Exception(DataConstants.STR_ERROR_TNR_NOT_PASSED);
			}else if(UIUtil.isNullOrEmpty(strOperation)) {
				throw new Exception(DataConstants.STR_ERROR_OPERATION_NOT_PASSED);
			}
		} catch (Exception ex) {
			return Response.status(500).entity(ex.getMessage()).build();
		}
		return Response.status(200).entity(strNewObjectPhysicalId).build();
	}

}

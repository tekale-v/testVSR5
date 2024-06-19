package com.pg.designtools.migration.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.vplmintegration.util.VPLMIntegTraceUtil;
import com.pg.designtools.datamanagement.DataConstants;
import com.pg.designtools.datamanagement.DataConstants.customWorkProcessD2SExceptions;
import matrix.db.BusinessObjectList;
import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class CheckEvolution implements IVersioningStrategy {

	private static final Logger logger = LoggerFactory.getLogger("DT_MIGRATION"); 
	/**
	 * Main method to validate the revision of the object
	 * @param context
	 * @param doObject
	 * @return String
	 * @throws MatrixException
	 */
	@Override
	public Map execute(Context context, DomainObject doObject) throws MatrixException {
		VPLMIntegTraceUtil.trace(context, ">>> Start of CheckEvolution: execute method");
		VPLMIntegTraceUtil.trace(context, ">>> doObject::"+doObject);
		
		logger.debug(">>> Start of CheckEvolution: execute method");
		logger.debug( ">>> doObject::"+doObject);
		Map mpCheckRevForVPMRef=new HashMap();
		
		if(null!=doObject) {
			
			StringList slSelects=new StringList(1);
			slSelects.add(DomainConstants.SELECT_ID);
			
			Map mpObjInfo=doObject.getInfo(context, slSelects);
			
			mpCheckRevForVPMRef=checkRevisionForVPMReference(context, doObject, mpObjInfo);
			
		}
		VPLMIntegTraceUtil.trace(context, ">>> result of CheckEvolution:execute::"+mpCheckRevForVPMRef);
		VPLMIntegTraceUtil.trace(context, "<<< End of CheckEvolution: execute method");
		
		logger.debug( ">>> result of CheckEvolution:execute::"+mpCheckRevForVPMRef);
		logger.debug( "<<< End of CheckEvolution: execute method");
		return mpCheckRevForVPMRef;
	}
	
	/**
	 * Method to verify the revision of VPMReference object
	 * @param context
	 * @param doObject
	 * @param mpObjInfo
	 * @return boolean
	 * @throws MatrixException
	 */
	private Map checkRevisionForVPMReference(Context context,DomainObject doObject,Map mpObjInfo) throws MatrixException {
		VPLMIntegTraceUtil.trace(context, ">>> Start of CheckRevision: checkRevisionForVPMReference method");
		logger.debug(">>> Start of CheckRevision: checkRevisionForVPMReference method");
		
		Map mpCheckVPMRefRev=new HashMap();
		String strError="";
		boolean bValidRev=true;
		BusinessObjectList boList=doObject.getMajorRevisions(context);
		VPLMIntegTraceUtil.trace(context, ">>> Major revisions::"+boList);
		logger.debug(">>> Major revisions::"+boList);
		
		String strLatestRevId=boList.get(boList.size()-1).getObjectId(context);
		VPLMIntegTraceUtil.trace(context, ">>> strLatestRevId::"+strLatestRevId);
		logger.debug(">>> strLatestRevId::"+strLatestRevId);
		
		if(UIUtil.isNotNullAndNotEmpty(strLatestRevId) && !strLatestRevId.equals(mpObjInfo.get(DomainConstants.SELECT_ID))) {
			bValidRev=false;
			DataConstants.customWorkProcessD2SExceptions errorNoLatestRev=customWorkProcessD2SExceptions.ERROR_400_NOT_LATEST_REVISION_VPMREF;
			
			strError=errorNoLatestRev.getExceptionCode()+DataConstants.SEPARATOR_COLON+errorNoLatestRev.getExceptionMessage();

			VPLMIntegTraceUtil.trace(context, errorNoLatestRev.getExceptionMessage());
		}
		VPLMIntegTraceUtil.trace(context, ">>> bValidRev::"+bValidRev);
		VPLMIntegTraceUtil.trace(context,">>> strError::"+strError);
	
		logger.debug(">>> bValidRev::"+bValidRev);
		logger.debug(">>> strError::"+strError);
		
		if(bValidRev)
			mpCheckVPMRefRev.put("validRev", DataConstants.CONSTANT_TRUE);
		else
			mpCheckVPMRefRev.put("validRev", DataConstants.CONSTANT_FALSE);
		
		mpCheckVPMRefRev.put(DataConstants.STR_ERROR, strError);
		
		logger.debug(">>> mpCheckVPMRefRev::"+mpCheckVPMRefRev);
		logger.debug("<<< End of CheckRevision: checkRevisionForVPMReference method");
		
		VPLMIntegTraceUtil.trace(context, ">>> mpCheckVPMRefRev::"+mpCheckVPMRefRev);
		VPLMIntegTraceUtil.trace(context, "<<< End of CheckRevision: checkRevisionForVPMReference method");
		return mpCheckVPMRefRev;
	}
	
}

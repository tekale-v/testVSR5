package com.pg.designtools.migration.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.vplmintegration.util.VPLMIntegTraceUtil;
import com.pg.designtools.datamanagement.DataConstants;
import com.pg.designtools.datamanagement.DataConstants.customWorkProcessD2SExceptions;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class CheckRevision implements IVersioningStrategy {

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
		VPLMIntegTraceUtil.trace(context, ">>> Start of CheckRevision: execute method");
		VPLMIntegTraceUtil.trace(context, ">>> doObject::"+doObject);
		
		logger.debug(">>> Start of CheckRevision: execute method");
		logger.debug(">>> doObject::"+doObject);
		Map mpCheckRevForECPart=new HashMap();
		
		if(null!=doObject) {
			
			StringList slSelects=new StringList(2);
			slSelects.add(DomainConstants.SELECT_CURRENT);
			slSelects.add(DomainConstants.SELECT_REVISION);
			
			Map mpObjInfo=doObject.getInfo(context, slSelects);
			
			mpCheckRevForECPart=checkRevisionForECPart(context, doObject, mpObjInfo);
			
		}
		VPLMIntegTraceUtil.trace(context, ">>> result of CheckRevision:execute::"+mpCheckRevForECPart);
		VPLMIntegTraceUtil.trace(context, "<<< End of CheckRevision: execute method");
		
		logger.debug(">>> result of CheckRevision:execute::"+mpCheckRevForECPart);
		logger.debug("<<< End of CheckRevision: execute method");
		return mpCheckRevForECPart;
	}
	
	/**
	 * Method to verify the revision of ECPart object
	 * @param context
	 * @param doObject
	 * @param mpObjInfo
	 * @return boolean
	 * @throws FrameworkException
	 */
	private Map checkRevisionForECPart(Context context,DomainObject doObject,Map mpObjInfo) throws FrameworkException {
		VPLMIntegTraceUtil.trace(context, ">>> Start of CheckRevision: checkRevisionForECPart method");
		logger.debug(">>> Start of CheckRevision: checkRevisionForECPart method");
		
		Map mpCheckECPartRev=new HashMap();
		String strError="";
		
		boolean bValidECPartRev=true;
		BusinessObject boLastRev=doObject.getLastRevision(context);
		VPLMIntegTraceUtil.trace(context, ">>> boLastRev::"+boLastRev);
		logger.debug(">>> boLastRev::"+boLastRev);
		
		if(!boLastRev.getRevision().equals(mpObjInfo.get(DomainConstants.SELECT_REVISION))) {
			bValidECPartRev=false;
			DataConstants.customWorkProcessD2SExceptions errorNoLatestRev=customWorkProcessD2SExceptions.ERROR_400_NOT_LATEST_REVISION_ECPART;

			strError=errorNoLatestRev.getExceptionCode()+DataConstants.SEPARATOR_COLON+errorNoLatestRev.getExceptionMessage();
		
			VPLMIntegTraceUtil.trace(context, errorNoLatestRev.getExceptionMessage());
			logger.debug(errorNoLatestRev.getExceptionMessage());
		}
		else if(!(DataConstants.STATE_PRELIMINARY.equals(mpObjInfo.get(DomainConstants.SELECT_CURRENT)) ||
				DataConstants.STATE_RELEASE.equals(mpObjInfo.get(DomainConstants.SELECT_CURRENT)))) {
			bValidECPartRev=false;
			DataConstants.customWorkProcessD2SExceptions errorNotReleased=customWorkProcessD2SExceptions.ERROR_400_NOT_RELEASED_ECPART;
			
			if(UIUtil.isNotNullAndNotEmpty(strError))
				strError+=DataConstants.SEPARATOR_PIPE+errorNotReleased.getExceptionCode()+DataConstants.SEPARATOR_COLON+errorNotReleased.getExceptionMessage();
			else
				strError=errorNotReleased.getExceptionCode()+DataConstants.SEPARATOR_COLON+errorNotReleased.getExceptionMessage();
			
			VPLMIntegTraceUtil.trace(context, errorNotReleased.getExceptionMessage());
			logger.debug(errorNotReleased.getExceptionMessage());
		}
		else {
			StringList slPartSpecObjects=doObject.getInfoList(context, "from["+DataConstants.REL_PART_SPECIFICATION+"].to.type");
			VPLMIntegTraceUtil.trace(context, ">>> slPartSpecObjects::"+slPartSpecObjects);
			logger.debug(">>> slPartSpecObjects::"+slPartSpecObjects);
			
			if(!slPartSpecObjects.isEmpty() && slPartSpecObjects.contains(DataConstants.TYPE_VPMREFERENCE)) {
				bValidECPartRev=false;
				DataConstants.customWorkProcessD2SExceptions errorNotStandalone=customWorkProcessD2SExceptions.ERROR_400_NOT_STANDALONE_ECPART;
				
				if(UIUtil.isNotNullAndNotEmpty(strError))
					 strError+=DataConstants.SEPARATOR_PIPE+errorNotStandalone.getExceptionCode()+DataConstants.SEPARATOR_COLON+errorNotStandalone.getExceptionMessage();
				else
					strError=errorNotStandalone.getExceptionCode()+DataConstants.SEPARATOR_COLON+errorNotStandalone.getExceptionMessage();
				
				VPLMIntegTraceUtil.trace(context, errorNotStandalone.getExceptionMessage());
				logger.debug(errorNotStandalone.getExceptionMessage());
			}
		}
		if(bValidECPartRev)
			mpCheckECPartRev.put("validRev", DataConstants.CONSTANT_TRUE);
		else
			mpCheckECPartRev.put("validRev", DataConstants.CONSTANT_FALSE);
		
		mpCheckECPartRev.put(DataConstants.STR_ERROR, strError);
		
		VPLMIntegTraceUtil.trace(context, ">>> bValidECPartRev::"+bValidECPartRev);
		VPLMIntegTraceUtil.trace(context, ">>> mpCheckECPartRev::"+mpCheckECPartRev);
		VPLMIntegTraceUtil.trace(context, "<<< End of CheckRevision: checkRevisionForECPart method");
		
		logger.debug(">>> bValidECPartRev::"+bValidECPartRev);
		logger.debug(">>> mpCheckECPartRev::"+mpCheckECPartRev);
		logger.debug("<<< End of CheckRevision: checkRevisionForECPart method");
		return mpCheckECPartRev;
	}

}

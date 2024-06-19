package com.pg.designtools.migration.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.vplmintegration.util.VPLMIntegTraceUtil;
import com.pg.designtools.datamanagement.CommonProductData;
import com.pg.designtools.datamanagement.DataConstants;
import com.pg.designtools.datamanagement.DataConstants.customWorkProcessD2SExceptions;
import com.pg.designtools.integrations.ebom.CollaborateEBOMJobFacility;

import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class CreateRevision implements IVersioningStrategy {
	private static final Logger logger = LoggerFactory.getLogger("DT_MIGRATION"); 
	/**
	 * Method to create revision of EC part object
	 * @param context
	 * @param doObject
	 * @return ObjectId of new revision
	 * @throws MatrixException
	 */
	@Override
	public Map execute(Context context,DomainObject doObject) throws MatrixException {
		VPLMIntegTraceUtil.trace(context, ">>> Start of CreateRevision: execute method");
		VPLMIntegTraceUtil.trace(context, ">>> doObject:::"+doObject);
		
		logger.debug(">>> Start of CreateRevision: execute method");
		logger.debug(">>> doObject:::"+doObject);
		
		String strNewObjectId="";
		Map mpECPartObject=new HashMap();
		
		if(null!=doObject) {
			
			CollaborateEBOMJobFacility ebomJobFacility=new CollaborateEBOMJobFacility();
			StringList slECPartSelects=ebomJobFacility.getECPartSelectables();
			
			Map mpECPartInfo=doObject.getInfo(context, slECPartSelects);
			mpECPartInfo.put(DomainConstants.SELECT_OWNER, context.getUser());
			VPLMIntegTraceUtil.trace(context, ">>> mpECPartInfo:::"+mpECPartInfo);
			logger.debug(">>> mpECPartInfo:::"+mpECPartInfo);
			
			//check the current state of the object and whether it has previous revisions
			String strCurrentState=(String) mpECPartInfo.get(DomainConstants.SELECT_CURRENT);
			
			//if the current object is in Preliminary state, then do not create a new revision.
			if(DataConstants.STATE_PRELIMINARY.equals(strCurrentState))
				strNewObjectId=(String)mpECPartInfo.get(DomainConstants.SELECT_ID);
			else
				strNewObjectId=ebomJobFacility.reviseProductData(context, doObject, mpECPartInfo);
			
				mpECPartObject.put("validRev", strNewObjectId);
		}
		
		if(UIUtil.isNullOrEmpty(strNewObjectId)) {
			DataConstants.customWorkProcessD2SExceptions errorTransactionAborted=customWorkProcessD2SExceptions.ERROR_400_TRANSACTION_ABORTED;
			throw new MatrixException(errorTransactionAborted.getExceptionMessage());
		}
		
		VPLMIntegTraceUtil.trace(context, ">>> New revision objectid:::"+strNewObjectId);
		VPLMIntegTraceUtil.trace(context, "<<< End of CreateRevision: execute method");
		
		logger.debug(">>> New revision objectid:::"+strNewObjectId);
		logger.debug("<<< End of CreateRevision: execute method");
	
		return mpECPartObject;
	}

}

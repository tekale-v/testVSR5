package com.pg.designtools.util;

import java.util.Iterator;
import java.util.Map;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.vplmintegration.util.VPLMIntegTraceUtil;
import com.pg.designtools.datamanagement.DataConstants;
import com.pg.designtools.integrations.ebom.CollaborateEBOMJobFacility;

import matrix.db.Context;
import matrix.util.StringList;
import com.dassault_systemes.enovia.enterprisechangemgt.common.ChangeConstants;
import com.dassault_systemes.enovia.enterprisechangemgt.common.ChangeOrder;
import com.dassault_systemes.enovia.enterprisechangemgt.util.ChangeUtil;
import com.dassault_systemes.enovia.enterprisechangemgt.common.ChangeAction;

/**
 * Class if a wrapper on the CO\CA functionality with specific DT extensions to functionality
 * All CM activities should flow through this class and not be coded in specific classes within
 * DataManagement package
 * @author GQS
 *
 */

public class ChangeManagement {

	/**
	 * * Connect an SPS to the same CA that a TUP is associated too
	 * @param context
	 * @param strObjectId
	 * @return
	 * @throws FrameworkException
	 */
	public MapList getCACOInfoList(Context context, String strObjectId) throws FrameworkException {
		StringList slObjectSelectsList = new StringList(DomainConstants.SELECT_ID);
		slObjectSelectsList.addElement(DomainConstants.SELECT_CURRENT);

		Map<?, ?> mpChangeInfo = com.dassault_systemes.enovia.enterprisechangemgt.util.ChangeUtil
				.getChangeObjectsInProposed(context, slObjectSelectsList, new String[] { strObjectId }, 1);

		return (MapList) mpChangeInfo.get(strObjectId);
	}

	/**
	 * @param context
	 * @param strSPSId
	 * @param strSPSCAId
	 * @param strTUPChangeActionID
	 * @throws Exception
	 */
	public void connectSPSToTUPCA(Context context, String strSPSId, String strSPSCAId,
			String strTUPChangeActionID) throws Exception{
		VPLMIntegTraceUtil.trace(context, ">>connectSPSToTUPCA strSPSCAId "+strSPSCAId);
		VPLMIntegTraceUtil.trace(context, ">>connectSPSToTUPCA strTUPChangeActionID "+strTUPChangeActionID);
		if (UIUtil.isNullOrEmpty(strSPSCAId) && UIUtil.isNotNullAndNotEmpty(strTUPChangeActionID)) {
			String strCAOwner = DomainObject.newInstance(context, strTUPChangeActionID).getInfo(context,
					DomainConstants.SELECT_OWNER);
			/*push the context to the owner of the CA, since all users with content on a CO\CA are not granted access
			 * directly to the CO\CA; the super user is not used here 
			 */
			boolean isContextPushed = false;
			if (!context.getUser().equalsIgnoreCase(strCAOwner)) {
				ContextUtil.pushContext(context, strCAOwner, DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
				isContextPushed = true;
			}
			try {	
				ChangeAction caInstance = new ChangeAction(strTUPChangeActionID);
				caInstance.connectAffectedItems(context, new StringList(strSPSId), true,
						ChangeConstants.FOR_RELEASE);
			}finally {
				if(isContextPushed) {
				ContextUtil.popContext(context);
			}
		}
	}
	}
	
	/**
	 * This creates Change Order and Change Action for List of Parts
	 * @param context
	 * @param slpartId
	 * @return
	 * @throws FrameworkException 
	 */
	public String createChangeObjects(Context context, StringList partIdList) throws FrameworkException
	{
		VPLMIntegTraceUtil.trace(context, ">>>> START of createChangeObjects method");
		String strReturnMessage = DataConstants.STR_SUCCESS;
		boolean isTransactionStarted = false;
		try
		{
			if(null!=partIdList && !partIdList.isEmpty())
			{
				StringList strNewPartIdList = new StringList();
				int iPartListSize = partIdList.size();
				String[] partIdArr = new String[iPartListSize];
				for(int k=0 ; k < iPartListSize; k++){
					partIdArr[k] = partIdList.get(k).trim();
				}

				Map mpCAInfo = ChangeUtil.getChangeObjectsInProposed(context, new StringList(DomainConstants.SELECT_ID), partIdArr, 1);
				VPLMIntegTraceUtil.trace(context, ">>>>mpCAInfo:::"+mpCAInfo);
			boolean bChangeExist = false;
				String strPartId = null;
				Map caMap = null;
				MapList proposedchangeActionList = null;
				Iterator proposedChangeItr = null;
				for(int iPart=0; iPart<iPartListSize; iPart++){
					bChangeExist = false;
					strPartId = partIdList.get(iPart).trim();
					proposedchangeActionList = (MapList)mpCAInfo.get(strPartId);
					if(!proposedchangeActionList.isEmpty())
					{
						proposedChangeItr = proposedchangeActionList.iterator();
						while(proposedChangeItr.hasNext()){
							caMap = (Map)proposedChangeItr.next();	
							if(ChangeConstants.TYPE_CHANGE_ACTION.equals(caMap.get(DomainConstants.SELECT_TYPE))){	
								bChangeExist = true;
								break;
							}
						}
						VPLMIntegTraceUtil.trace(context, ">>>>bChangeExist:::"+bChangeExist);
						if(!bChangeExist){
							strNewPartIdList.add(strPartId);
						}
					} else {
						strNewPartIdList.add(strPartId);
					}
				}

				VPLMIntegTraceUtil.trace(context, ">>>>strNewPartIdList:::"+strNewPartIdList);
				//Create new CO and CA if it does not exist
				if(!strNewPartIdList.isEmpty()){
					StringList objectSelects = new StringList(1);
					objectSelects.add(DomainConstants.SELECT_ID);

					String strDefaultChangeTemplateName = EnoviaResourceBundle.getProperty(context, "emxCPN.ProductDataStructureCopy.DefaultChangeTemplateName");
					String strDefaultChangeTemplateRev = EnoviaResourceBundle.getProperty(context, "emxCPN.ProductDataStructureCopy.DefaultChangeTemplateRev");
					MapList mlChangeTemplate = DomainObject.findObjects(context, 
							ChangeConstants.TYPE_CHANGETEMPLATE, 						//typePattern
							strDefaultChangeTemplateName, 											//namePattern
							strDefaultChangeTemplateRev, 												//revPattern
							null, 																						//ownerPattern
							DataConstants.VAULT_ESERVICE_PRODUCTION,                 //vaultPattern
							null, 																					   //where expression
							true, 																					   //expandType
							objectSelects);																		   //object selectables
					Map mpChangeTemplate = (Map)mlChangeTemplate.get(0);
					String strChangeTemplateId=(String)mpChangeTemplate.get(DomainConstants.SELECT_ID);

					VPLMIntegTraceUtil.trace(context, ">>>>strChangeTemplateId:::"+strChangeTemplateId);
					ContextUtil.startTransaction(context,true);
					isTransactionStarted = true;
					//Create new CO
					String strCOId  = (new ChangeOrder()).create(context, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING, context.getUser(), strChangeTemplateId, null); 
					
					CollaborateEBOMJobFacility collabEBOM=new CollaborateEBOMJobFacility();
					collabEBOM.addAutomationInterface(context, DataConstants.CONSTANT_CATIA_APPLICATION, DataConstants.CONSTANT_COCA_PROCESS, strCOId);
					
					VPLMIntegTraceUtil.trace(context, ">>>>strCOId:::"+strCOId);
					ChangeOrder changeOrderObj = new ChangeOrder(strCOId);	
					 
					Map mpCOInfo=changeOrderObj.connectAffectedItems(context,  strNewPartIdList); 
					VPLMIntegTraceUtil.trace(context, ">> mpCOInfo:::"+mpCOInfo);
					// mpCOInfo:::{strErrorMSG=, objIDCAMap={20336.41905.7957.56484=00000000D9260000636B7BC500067CC3}}
					Map objIDCAMap=(Map) mpCOInfo.get("objIDCAMap");
					VPLMIntegTraceUtil.trace(context, ">> objIDCAMap:::"+objIDCAMap);
					Iterator<String> itr =objIDCAMap.values().iterator();
					while(itr.hasNext()) {
						collabEBOM.addAutomationInterface(context, DataConstants.CONSTANT_CATIA_APPLICATION, DataConstants.CONSTANT_COCA_PROCESS, itr.next());
					}
				}
			}
		}
		catch(Exception exp)
		{
			if(isTransactionStarted)
			{
				ContextUtil.abortTransaction(context);
				isTransactionStarted = false;
			}
			VPLMIntegTraceUtil.trace(context, "ChangeManagement: Exception in createChangeObjects >> " + exp.getLocalizedMessage());
			strReturnMessage = DataConstants.STR_ERROR;
		}finally {
			if(isTransactionStarted) {
				ContextUtil.commitTransaction(context);
			}
		}
		return strReturnMessage;
	}
	
}
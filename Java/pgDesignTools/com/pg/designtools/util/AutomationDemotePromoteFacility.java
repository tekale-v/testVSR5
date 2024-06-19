package com.pg.designtools.util;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.dassault_systemes.enovia.gls.common.model.PRSPContext;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.designtools.datamanagement.DataConstants;
import com.pg.designtools.datamanagement.EngineeringItem;
import com.pg.designtools.datamanagement.ITransientJob;
import com.pg.designtools.integrations.exception.DesignToolsIntegrationException;
import com.matrixone.apps.domain.util.StringUtil;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.StringList;



/**
	 * @author GQS
	 * This class is an implementation of a Transient Job Handler 
	 * A Transient Job can be designed to handler one or more events for which specific updates can be 
	 * made to the Job Configuration attribute that maintains information that may span different client sessions.
	 * The only rule is that only one Transient Job can be managed at a given time.
	 *
	 */
	public class AutomationDemotePromoteFacility extends InterfaceManagement implements ITransientJob {
		
		DataConstants.customCATIAExceptions errorNoInterface = DataConstants.customCATIAExceptions.ERROR_400_JOB_INTERFACE_NOT_PRESENT;
		DataConstants.customCATIAExceptions errorNoValidEvent = DataConstants.customCATIAExceptions.ERROR_400_INVALID_EVENT;
		
		public AutomationDemotePromoteFacility() {
			super();
		}
		
		public AutomationDemotePromoteFacility(Context context) {
			PRSPContext.set(context);
		}
		
		// init arrayList to allowable events: DEMOTE PROMOTE
		ArrayList<String> validEvents = new ArrayList<>();
		
		String strTransferFlag="";
		private String strJobConfiguration="";
		
		private String readJobConfiguration(Context context,DomainObject doObject) throws FrameworkException {
			
			strJobConfiguration=doObject.getAttributeValue(context, DataConstants.ATTRIBUTE_PG_JOB_CONFIG);
			
			StringList slConfigList=StringUtil.split(strJobConfiguration,"|");
			String strFlag=slConfigList.get(2);
			strFlag=strFlag.substring(strFlag.indexOf(':')+1);
			return strFlag;
		}

		private String getTransferFlagInfo() {
			return strTransferFlag;
			
		}
		
		private void setTransferFlagInfo(String strJobEvent,String strVPLMControlled) {
			if(DataConstants.CONSTANT_DEMOTE.equalsIgnoreCase(strJobEvent)) {
				if(DataConstants.CONSTANT_FALSE.equalsIgnoreCase(strVPLMControlled))
					strTransferFlag="Design";
			}
			else if(DataConstants.CONSTANT_PROMOTE.equalsIgnoreCase(strJobEvent))
				strTransferFlag=strVPLMControlled;
			
		}
		
		void setJobConfiguration(String strJobEvent) {
			
			//2 parts if the configuration string
			// core
			//JobName:AutomationDemotePromote|JobID:<jobid is context session+"-"+currenttime>
			// automation job specific
			// Transfer:[Physical|Design] //here Design means Enovia and Physical means CATIA. Value would be the location where the control is currently
			strJobConfiguration="JobEvent:"+strJobEvent+"|JobID:"+System.currentTimeMillis()+"|Transfer:"+getTransferFlagInfo();
		}
		
		void updateJobConfiguration(Context context,DomainObject doObject) throws FrameworkException{
	
			Map<String,String> mpAttr=new HashMap<>();
			mpAttr.put(DataConstants.ATTRIBUTE_PG_JOB_CONFIG, strJobConfiguration);
			mpAttr.put(DomainConstants.ATTRIBUTE_ORIGINATOR, context.getUser());
			doObject.setAttributeValues(context, mpAttr);
		}

		@Override
		public String init(Context context,String strJobEvent,String strObjType,String strObjName,String strObjRev) throws Exception {
			String strResult="";
			addEvent();
			// verify that this handler takes strJobEvent
			boolean bIsValidEvent=isValidEvent(strJobEvent);

			if(bIsValidEvent) {
				String strObjectId=getObject(context, strObjType, strObjName, strObjRev);
				
				strResult=doEvent(context,strJobEvent,strObjectId);
			}
			return strResult;
		}


		@Override
		public boolean isValidEvent(String strJobEvent) throws FrameworkException{
			boolean bIsValidEvent=false;
			// check if uppercase is in validEvents
			if(validEvents.contains(strJobEvent.toUpperCase()))
				bIsValidEvent=true;
			else {
				throw new DesignToolsIntegrationException(errorNoValidEvent.getExceptionCode(),
						errorNoValidEvent.getExceptionMessage());
				
			}
			return bIsValidEvent;
		}


		@Override
		public String doEvent(Context context,String strJobEvent,String strObjectId) throws Exception {
			String strResult="";
			if(UIUtil.isNotNullAndNotEmpty(strObjectId)) {
				StringList slSelects=new StringList();
				slSelects.addElement(DomainConstants.SELECT_CURRENT);
				slSelects.addElement(DataConstants.SELECT_ATTRIBUTE_ISVPLMCONTROLLED);
				
				DomainObject doObject=DomainObject.newInstance(context,strObjectId);
				
				Map<String,String> mpObjDetails=doObject.getInfo(context, slSelects);
				String strCurrent=mpObjDetails.get(DomainConstants.SELECT_CURRENT);
				String strVPLMControlled=mpObjDetails.get(DataConstants.SELECT_ATTRIBUTE_ISVPLMCONTROLLED);			
						
				EngineeringItem enggItem=new EngineeringItem(context);
			
				if(strJobEvent.equalsIgnoreCase(DataConstants.CONSTANT_DEMOTE)) {
					if(strCurrent.equals(DataConstants.STATE_FROZEN)) {
						if(!checkInterfaceOnObject(context, strObjectId, DataConstants.INTERFACE_DT_TRANSIENT_JOB_EXTENSION)) {
							addInterface(context,strObjectId);
						}
						setTransferFlagInfo(strJobEvent, strVPLMControlled);
						setJobConfiguration(strJobEvent);
						
						enggItem.demoteVPMReference(context, strObjectId);
						
						if(UIUtil.isNotNullAndNotEmpty(getTransferFlagInfo())) {
							HashMap<String, String>hmParam=new HashMap<>();
							hmParam.put("objectId", strObjectId);
							hmParam.put("objSelects", DomainConstants.SELECT_ID);
							Map mpECPartInfo=JPO.invoke(context, "pgDSOCATIAIntegration", null, "getECPartInfoFromCAD", JPO.packArgs(hmParam), Map.class);
							String strECPartObjId=(String)mpECPartInfo.get(DomainConstants.SELECT_ID);
							
							if(UIUtil.isNotNullAndNotEmpty(strECPartObjId))
								enggItem.syncToDesign(context, strECPartObjId,"give");
						}
						
						updateJobConfiguration(context, doObject);
						strResult=DataConstants.DEMOTE_SUCCESS_MESSAGE;
					}
				}else if(strJobEvent.equalsIgnoreCase(DataConstants.CONSTANT_PROMOTE) && strCurrent.equals(DataConstants.STATE_IN_WORK)){
						if(checkInterfaceOnObject(context, strObjectId, DataConstants.INTERFACE_DT_TRANSIENT_JOB_EXTENSION)) {
				
							String strFlag=readJobConfiguration(context, doObject);
							
							enggItem.freezeVPMReference(context, strObjectId);
							
							if(UIUtil.isNotNullAndNotEmpty(strFlag)) {
								setTransferFlagInfo(strJobEvent, strFlag);
								enggItem.syncToEnterprise(context, strObjectId,"give");
							}
							
							removeInterface(context, strObjectId);
							strResult=DataConstants.PROMOTE_SUCCESS_MESSAGE;
						}else {
							throw new DesignToolsIntegrationException(errorNoInterface.getExceptionCode(),
									errorNoInterface.getExceptionMessage());
					}
				}
			}
			return strResult;
		}

		@Override
		public int addInterface(Context context,String strObjectId) throws MatrixException {
			return addInterface(context, strObjectId, DataConstants.INTERFACE_DT_TRANSIENT_JOB_EXTENSION);
		}


		@Override
		public int removeInterface(Context context,String strObjectId) throws MatrixException{
			return removeInterface(context, strObjectId, DataConstants.INTERFACE_DT_TRANSIENT_JOB_EXTENSION);
		}

		@Override
		public void addEvent() {
			validEvents.add(DataConstants.CONSTANT_DEMOTE);
			validEvents.add(DataConstants.CONSTANT_PROMOTE);
		}
		
		@Override
		public String getObject(Context context,String strObjType,String strObjName,String strObjRev) throws FrameworkException {
			String strObjectId="";
			StringList slSelects=new StringList();
			slSelects.add(DomainConstants.SELECT_ID);
			IPManagement ipMgmt=new IPManagement(context);
			MapList mlObject=ipMgmt.findObject(context, strObjType, strObjName, strObjRev, slSelects);
			
			if(!mlObject.isEmpty()) {
				Map<String,String> mpObject=(Map)mlObject.get(0);
				strObjectId=mpObject.get(DomainConstants.SELECT_ID);
			}
			return strObjectId;
			
		}
}
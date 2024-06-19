package com.pg.designtools.migration.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dassault_systemes.enovia.versioning.util.ENOVersioningException;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.vplmintegration.util.VPLMIntegTraceUtil;
import com.pg.designtools.datamanagement.CommonProductData;
import com.pg.designtools.datamanagement.CommonUtility;
import com.pg.designtools.datamanagement.DataConstants;
import com.pg.designtools.datamanagement.EngineeringItem;
import com.pg.designtools.datamanagement.DataConstants.customWorkProcessD2SExceptions;
import com.pg.designtools.integrations.datahandlers.DataRectificationHandler;
import com.pg.designtools.util.InterfaceManagement;

import matrix.db.BusinessInterfaceList;
import matrix.db.ConnectParameters;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.db.Relationship;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class ProductDTMigration extends ProductDTAnalyzer {
	
	private static final Logger logger = LoggerFactory.getLogger("DT_MIGRATION"); 
	private boolean bOldRevExists=false;
	private String strNewEvolvedRevisedVPMReferenceId="";
	
	public ProductDTMigration(String strModeOfTitle) {
		super(strModeOfTitle);
		strTitleMode=strModeOfTitle;
	}
		/**
		 * Method to invoke the actual migration
		 * @param context
		 * @param doObject
		 * @param strType
		 * @return new objectId
		 * @throws MatrixException, ENOVersioningException
		 */
		public Map process(Context context, DomainObject doObject,String strType) throws MatrixException, ENOVersioningException {
			VPLMIntegTraceUtil.trace(context, ">>> Start of ProductDTMigration : process method");
			VPLMIntegTraceUtil.trace(context, ">>> ProductDTMigration : process method _vpmRefVersioning.getClass()::"+_vpmRefVersioning.getClass());
			VPLMIntegTraceUtil.trace(context, ">>> ProductDTMigration : process method _ecVersioning.getClass()::"+_ecVersioning.getClass());
			
			logger.debug(">>> Start of ProductDTMigration : process method");
			logger.debug(" >>> ProductDTMigration : process method _vpmRefVersioning.getClass()::"+_vpmRefVersioning.getClass());
				logger.debug(">>> ProductDTMigration : process method _ecVersioning.getClass()::"+_ecVersioning.getClass());
			Map mpResult;
			
			//First, validation of objects should be done. Hence, invoking the process method of ProductDTAnalyzer
			if(DataConstants.TYPE_VPMREFERENCE.equalsIgnoreCase(strType)) {
				_vpmRefVersioning = new CheckEvolution();
			}else {
				_ecVersioning = new CheckRevision();
			}
			mpResult=super.process(context, doObject,strType);
			VPLMIntegTraceUtil.trace(context, ">>> ProductDTMigration : process method strValidVPMReferenceObject::"+strValidVPMReferenceObject+" strValidECPartObject::"+strValidECPartObject);
			logger.debug(">>> ProductDTMigration : process method strValidVPMReferenceObject::"+strValidVPMReferenceObject+" strValidECPartObject::"+strValidECPartObject);
			
			if(Boolean.parseBoolean(strValidVPMReferenceObject) && Boolean.parseBoolean(strValidECPartObject)) {
				if(DataConstants.TYPE_VPMREFERENCE.equals(strType)) {
					_vpmRefVersioning = new CreateEvolution();
				}else {
					_ecVersioning = new CreateRevision(); 
				}
				mpResult=super.process(context, doObject,strType);
			}

			VPLMIntegTraceUtil.trace(context, "<<< End of ProductDTMigration : process method");
			logger.debug("<<< End of ProductDTMigration : process method");
			return mpResult;
		}
		
		/**
		 * Method to get the selectables for EC Part
		 * @return StringList
		 */
		private StringList getECPartSelectables()
		{
			StringList slECPartSelectables=new StringList();
			slECPartSelectables.add(DomainConstants.SELECT_ID);
			slECPartSelectables.add(DomainConstants.SELECT_TYPE);
			slECPartSelectables.add(DomainConstants.SELECT_DESCRIPTION);
			slECPartSelectables.add(DataConstants.SELECT_ATTRIBUTE_SEGMENT);
			slECPartSelectables.add(DataConstants.SELECT_ATTRIBUTE_CLASS);	
			slECPartSelectables.add(DataConstants.SELECT_ATTRIBUTE_MATERIAL_TYPE);	
			slECPartSelectables.add("attribute["+DataConstants.ATTR_RELEASE_PHASE+"]");	
			slECPartSelectables.add(DataConstants.SELECT_ATTRIBUTE_LIFECYCLE_STATUS);	
			slECPartSelectables.add(DataConstants.SELECT_ATTRIBUTE_RELEASE_CRITERIA);	
			slECPartSelectables.add("attribute["+DataConstants.ATTR_TITLE+"]");	
			slECPartSelectables.add("from["+DataConstants.RELATIONSHIP_PGPDTEMPLATES_TO_PGPLIREPORTEDFUNCTION+"].to.name");
			slECPartSelectables.add("from["+DataConstants.REL_PG_PRIMARY_ORGANIZATION+"].to.name");
			slECPartSelectables.add("attribute["+DataConstants.ATTRIBUTE_REASONFORCHANGE+"]");
			return slECPartSelectables;
		}

		/**
		 * Method for post processing of the revised and evolved objects
		 * @param context
		 * @param physicalid of evolved VPMReference object
		 * @param objectid of revised EC Part object
		 * @param DomainObject of original EC Part object
		 * @throws Exception 
		 */
		@Override
		public void updateObjects(Context context, String strNewVPMReferenceObjectId, String strNewECPartObjectId) throws Exception {
			
			VPLMIntegTraceUtil.trace(context, ">>> Start of ProductDTMigration: updateObjects method");
			VPLMIntegTraceUtil.trace(context, ">>> strNewVPMReferenceObjectId:::"+strNewVPMReferenceObjectId+" strNewECPartObjectId::"+strNewECPartObjectId);
			
			logger.debug(">>> Start of ProductDTMigration: updateObjects method");
			logger.debug(">>> strNewVPMReferenceObjectId:::"+strNewVPMReferenceObjectId+" strNewECPartObjectId::"+strNewECPartObjectId);
			
			if(UIUtil.isNotNullAndNotEmpty(strNewECPartObjectId) && UIUtil.isNotNullAndNotEmpty(strNewVPMReferenceObjectId)) {
				
				CommonProductData cpd=new CommonProductData(context);
				EngineeringItem enggItem=new EngineeringItem(context);
				
				 DomainObject doRevisedECPartObject=DomainObject.newInstance(context,strNewECPartObjectId);
				 DomainObject doEvolvedVPMReferenceObject=DomainObject.newInstance(context,strNewVPMReferenceObjectId);
				 
				MapList ml3DShapeDrawing=enggItem.getRelatedRepInstance(context, doEvolvedVPMReferenceObject);
					
				//logic to modify name and PLMEternalID attribute of VPMReference if it is prefixed by OOTB prefix (prd)
				updateVPMReferenceName(context,doEvolvedVPMReferenceObject,ml3DShapeDrawing);
					
				//check whether the EC Part has previous revisions
				ArrayList<String> arrOldRevisions=(ArrayList<String>) cpd.getPreviousRevisions(context, doRevisedECPartObject);
				VPLMIntegTraceUtil.trace(context, ">>> arrOldRevisions:::"+arrOldRevisions.size());
				
				if(!arrOldRevisions.isEmpty()) {
					
						for(int i=0;i<arrOldRevisions.size();i++) {
							VPLMIntegTraceUtil.trace(context, ">>> OldRevision id:::"+arrOldRevisions.get(i));
						}
						bOldRevExists=true;
				}
				VPLMIntegTraceUtil.trace(context, ">>> bOldRevExists:::"+bOldRevExists);
				
				//1. Get all the attributes of evolved VPMReference object
				StringList slSelects=new StringList(3);
				slSelects.add(DataConstants.SELECT_ATTRIBUTE_PGENTERPRISETYPE);
				slSelects.add(DataConstants.SELECT_ATTRIBUTE_PLMENTITY_V_NAME);
				slSelects.add(DataConstants.SELECT_ATTRIBUTE_V_DESCRIPTION);
				
				Map mpEvolvedVPMRefData=doEvolvedVPMReferenceObject.getInfo(context, slSelects);
			   VPLMIntegTraceUtil.trace(context, ">>> mpEvolvedVPMRefData:::"+mpEvolvedVPMRefData);
			   
				String strVPMReferenceEnterpriseType=(String) mpEvolvedVPMRefData.get(DataConstants.SELECT_ATTRIBUTE_PGENTERPRISETYPE);
				String strVPMReferenceTitle=(String) mpEvolvedVPMRefData.get(DataConstants.SELECT_ATTRIBUTE_PLMENTITY_V_NAME);
				String strVPMReferenceDesc=(String) mpEvolvedVPMRefData.get(DataConstants.SELECT_ATTRIBUTE_V_DESCRIPTION);
				
				VPLMIntegTraceUtil.trace(context, ">>> strVPMReferenceEnterpriseType:::"+strVPMReferenceEnterpriseType+"  strVPMReferenceTitle::"+strVPMReferenceTitle
						+" strVPMReferenceDesc::"+strVPMReferenceDesc);
				
			   logger.debug(">>> strVPMReferenceEnterpriseType:::"+strVPMReferenceEnterpriseType+" strVPMReferenceTitle::"+strVPMReferenceTitle+" strVPMReferenceDesc::"+strVPMReferenceDesc);
				
				//2. Get attribute information from revised EC Part
			   Map mpRevisedECPartObjInfo=doRevisedECPartObject.getInfo(context, getECPartSelectables());
			   VPLMIntegTraceUtil.trace(context, ">>> mpRevisedECPartObjInfo:::"+mpRevisedECPartObjInfo);
			   logger.debug(">>> mpRevisedECPartObjInfo:::"+mpRevisedECPartObjInfo);
			   
			   //3. Get Reason for Change from original EC Part
			   String strOriginalECPartId=doECPartObject.getInfo(context, DomainConstants.SELECT_ID);
			   
			   //if new revision is not created for the EC Part and old revision exists, then get the reason for change from the previous revision.
			   String strReasonForChange;
			   if(strNewECPartObjectId.equals(strOriginalECPartId) && bOldRevExists) {
				   DomainObject doPrevRev=DomainObject.newInstance(context,arrOldRevisions.get(0));
				   strReasonForChange=doPrevRev.getInfo(context,"attribute["+DataConstants.ATTRIBUTE_REASONFORCHANGE+"]");
			   }
			   else {
				   strReasonForChange=doECPartObject.getInfo(context,"attribute["+DataConstants.ATTRIBUTE_REASONFORCHANGE+"]");
			   }
			   VPLMIntegTraceUtil.trace(context, ">>> strReasonForChange of original EC Part:::"+strReasonForChange);
			   if(UIUtil.isNullOrEmpty(strReasonForChange))
				   strReasonForChange="New";
			   
			   VPLMIntegTraceUtil.trace(context, ">>> strReasonForChange of original EC Part111:::"+strReasonForChange);
			   logger.debug(">>> strReasonForChange of original EC Part:::"+strReasonForChange);
			   
			   //4. check if the EnterprisePartType matches the Spec Type. If not, need to change it and pngDesignDomain . Also remove relevant interface and add pngiPackaging interface
			   DataRectificationHandler dataHandler=new DataRectificationHandler(context);
			  String strEnterpriseType= dataHandler.getEnterpriseTypeForECPartType((String)mpRevisedECPartObjInfo.get(DomainConstants.SELECT_TYPE));
			  String strDesignDomain= dataHandler.getDesignDomainForECPartType((String)mpRevisedECPartObjInfo.get(DomainConstants.SELECT_TYPE));
			 
			  VPLMIntegTraceUtil.trace(context, ">>> EC Part strEnterpriseType:::"+strEnterpriseType+" strDesignDomain::"+strDesignDomain);
			  logger.debug(">>> EC Part strEnterpriseType:::"+strEnterpriseType+" strDesignDomain::"+strDesignDomain);
			  
			  String strECPartTypeForVPMReference=dataHandler.getECPartTypeForEnterpriseType(strVPMReferenceEnterpriseType);
			  String strDesignDomainForVPMReference=dataHandler.getDesignDomainForECPartType(strECPartTypeForVPMReference);
			 
			  VPLMIntegTraceUtil.trace(context, ">>>VPMReference strECPartTypeForVPMReference:::"+strECPartTypeForVPMReference+" strDesignDomainForVPMReference::"+strDesignDomainForVPMReference);
			  logger.debug(">>>VPMReference strECPartTypeForVPMReference:::"+strECPartTypeForVPMReference+" strDesignDomainForVPMReference::"+strDesignDomainForVPMReference);
			  
			  if(!strVPMReferenceEnterpriseType.equals(strEnterpriseType)) {
				  updateEnterpriseAttrAndInterface(context,strEnterpriseType,strDesignDomain,strDesignDomainForVPMReference,strNewVPMReferenceObjectId,doEvolvedVPMReferenceObject);
			  }
		  
			  //START: added for DTCLD-617: Make the revised EC Part Title same as source CAD object
			  VPLMIntegTraceUtil.trace(context, ">>> strTitleMode:::"+strTitleMode);
			  
			  if(! (UIUtil.isNotNullAndNotEmpty(strTitleMode) && strTitleMode.equalsIgnoreCase("Spec"))) {
				  updateTitleDescriptionOfECParts(context,arrOldRevisions,doRevisedECPartObject,strVPMReferenceTitle,strVPMReferenceDesc);
			  }
			  //END: added for DTCLD-617: Make the revised EC Part Title same as source CAD object
			  
			  if(bOldRevExists) {
				  updateCADDesignOrigination(context,new StringList(strNewVPMReferenceObjectId),DataConstants.RANGE_VALUE_AUTOMATION);
				   updateOperationOnPreviousRevisions(context,arrOldRevisions,strNewVPMReferenceObjectId,doRevisedECPartObject);
			  }else {
				  //7. update evolved VPMReference attributes with EC Part values
					updateAttributesOfVPMReference(context,doEvolvedVPMReferenceObject,mpRevisedECPartObjInfo,strReasonForChange);
				  // connect the revised EC Part to the  evolved VPMReference object
				  connectNewObjects(context,doEvolvedVPMReferenceObject,doRevisedECPartObject);
			  }
			  
				//add the automation usage tracking interface on the Evolved object and revision of EC Part
				if(!bOldRevExists) {
							
					StringBuilder sbObjectIds=new StringBuilder();
					sbObjectIds.append(strNewECPartObjectId).append(DataConstants.SEPARATOR_PIPE).append(strNewVPMReferenceObjectId);
					
					addAutomationInterface(context,sbObjectIds);
				}
				
			 //6. update the isVPMVisible attribute of revised EC Part as TRUE
			 doRevisedECPartObject.setAttributeValue(context, DataConstants.ATTRIBUTE_ISVPMVISIBLE, DataConstants.CONSTANT_TRUE.toUpperCase()); 
			 VPLMIntegTraceUtil.trace(context, ">>> Set isVPMVisible on revised EC Part as TRUE");
			 logger.debug(">>> Set isVPMVisible on revised EC Part as TRUE");
			 
			 //delete the Derived relationship from all the revisions of EC Part
			 deleteDerivedRelationshipFromECPart(context,arrOldRevisions,strNewECPartObjectId);
			 
			  //8.Collaborate with EBOM
			 if(bOldRevExists) {
				 enggItem.syncToEnterprise(context, strNewEvolvedRevisedVPMReferenceId, "give", DataConstants.CONSTANT_ZERO);
			 }else {
				enggItem.syncToEnterprise(context, strNewVPMReferenceObjectId, "give", DataConstants.CONSTANT_ZERO);
			 }
		}
			VPLMIntegTraceUtil.trace(context, "<<< End of ProductDTMigration: updateObjects method");
			logger.debug( "<<< End of ProductDTMigration: updateObjects method");
		}

		/**
		 * Method to update the CADDesignOrigination value
		 * @param context
		 * @param slObjectIds
		 * @param strCADDesignOriginationValue
		 * @throws FrameworkException
		 */
		private void updateCADDesignOrigination(Context context, StringList slObjectIds, String strCADDesignOriginationValue) throws FrameworkException {
			   VPLMIntegTraceUtil.trace(context, ">>> START of updateCADDesignOrigination method");
			   CommonUtility commonUtility=new CommonUtility(context); 
			   commonUtility.executeMQLCommands(context, "trigger off");
			   
			   DomainObject doVPMReferenceObject;
			   boolean bContextPushed=false;
			   //user would not have modify access on the objects , as they can be in released/obsolete states. Hence, pushing context
			   try {
				   ContextUtil.pushContext(context);
				   bContextPushed=true;
				   
				   for(int i=0;i<slObjectIds.size();i++) {
					   doVPMReferenceObject=DomainObject.newInstance(context,slObjectIds.get(i));
					   doVPMReferenceObject.setAttributeValue(context, DataConstants.ATTRIBUTE_CAD_DESIGN_ORIGINATION, strCADDesignOriginationValue);
					   VPLMIntegTraceUtil.trace(context, ">>> Set the CAD Design Origination as "+strCADDesignOriginationValue+" for "+doVPMReferenceObject);
				   }
			   }finally {
				   if(bContextPushed)
					   ContextUtil.popContext(context);
			   }
			  commonUtility.executeMQLCommands(context, "trigger on");
			  VPLMIntegTraceUtil.trace(context, "<<< END of updateCADDesignOrigination method");
		}

		/**
		 * Method to add the pgAutomationUsageExtension extension on the newly created/modified objects
		 * @param context
		 * @param sbObjectIds
		 * @throws MatrixException
		 */
		private void addAutomationInterface(Context context, StringBuilder sbObjectIds) throws MatrixException {
			VPLMIntegTraceUtil.trace(context, ">>> START of addAutomationInterface method");
			boolean bContextPushed=false;
			//The data can be in released/obsolete states Hence, pushed the context
			try {
				ContextUtil.pushContext(context);
				bContextPushed=true;
				String[] newArgs = {DataConstants.CONSTANT_CATIA_APPLICATION, DataConstants.CONSTANT_CAD2SPEC_CONVERSION_PROCESS,sbObjectIds.toString()};
				
				VPLMIntegTraceUtil.trace(context, ">>> newArgs[0]:::"+newArgs[0]+" newArgs[1]::"+newArgs[1]+" newArgs[2]::"+newArgs[2]);
				JPO.invoke(context, DataConstants.CONSTANT_JPO_PGDTAUTOMATION_METRIC_TRACKING, null, DataConstants.CONSTANT_METHOD_ADD_USAGE_TRACKING_TODATA, newArgs);
			}finally {
				if(bContextPushed)
					ContextUtil.popContext(context);
			}
			VPLMIntegTraceUtil.trace(context, "<<< END of addAutomationInterface method");
		}

		/**
		 * Method to delete Derived relationship from EC Part objects
		 * @param context
		 * @param arrOldRevisions
		 * @param strNewECPartObjectId
		 * @throws MatrixException
		 */
		private void deleteDerivedRelationshipFromECPart(Context context, ArrayList<String> arrOldRevisions,String strNewECPartObjectId) throws MatrixException {
			VPLMIntegTraceUtil.trace(context, ">>> START of deleteDerivedRelationship method");
			
			StringList slSelects=new StringList(3);
			slSelects.add(DomainConstants.SELECT_ID);
			slSelects.add("to["+DataConstants.RELATIONSHIP_DERIVED+"].id");
			slSelects.add("from["+DataConstants.RELATIONSHIP_DERIVED+"].id");
			
			StringList slObjectIds=new StringList();
			if(null!=arrOldRevisions && !arrOldRevisions.isEmpty()) {
				for(int i=0;i<arrOldRevisions.size();i++)
					slObjectIds.add(arrOldRevisions.get(i));
			}
			slObjectIds.add(strNewECPartObjectId);
			
			VPLMIntegTraceUtil.trace(context, ">>> slObjectIds::"+slObjectIds);
			
			MapList mlDerivedData=DomainObject.getInfo(context,  slObjectIds.toArray(new String[slObjectIds.size()]), slSelects);
			VPLMIntegTraceUtil.trace(context, ">>> mlDerivedData::"+mlDerivedData);
			DomainObject doObject;
			Map mpData;
			String strDerivedRelId;
			String strObjectId;
			
			if(null!=mlDerivedData && !mlDerivedData.isEmpty()) {
				for(int i=0;i<mlDerivedData.size();i++) {
					 mpData=(Map)mlDerivedData.get(i);
					 strObjectId=(String)mpData.get(DomainConstants.SELECT_ID);
					 doObject=DomainObject.newInstance(context,strObjectId);
					 
					 strDerivedRelId=(String)mpData.get("to["+DataConstants.RELATIONSHIP_DERIVED+"].id");
					 if(UIUtil.isNotNullAndNotEmpty(strDerivedRelId))
						 doObject.disconnect(context, new Relationship(strDerivedRelId));
					 
					 strDerivedRelId=(String)mpData.get("from["+DataConstants.RELATIONSHIP_DERIVED+"].id");
					 if(UIUtil.isNotNullAndNotEmpty(strDerivedRelId))
						 doObject.disconnect(context, new Relationship(strDerivedRelId));
				}
			}
			VPLMIntegTraceUtil.trace(context, "<<< END of deleteDerivedRelationship method");
		}

		/**
		 * Method to update the previous revisions of VPMReference and EC Part
		 * @param context
		 * @param arrOldRevisions
		 * @param strVPMReferenceObjectId
		 * @param doRevisedECPartObject
		 * @throws Exception 
		 */
		private void updateOperationOnPreviousRevisions(Context context, ArrayList<String> arrOldRevisions, 
				String strVPMReferenceObjectId, DomainObject doRevisedECPartObject) throws Exception {
			
			VPLMIntegTraceUtil.trace(context, ">>> START of updateOperationOnPreviousRevisions method");
			StringBuilder sbObjectIds=new StringBuilder();
			
			DomainObject doTempECPartObject;
			//as the old revisions can be either Released/Obsolete, pushing the context
			EngineeringItem enggItem=new EngineeringItem(context);
			CommonUtility commonUtility=new CommonUtility(context);
			CreateVPMReferenceRevision createVPMRefRev = new CreateVPMReferenceRevision();
				
			StringList slSelects=new StringList(2);
			slSelects.add(DataConstants.SELECT_ATTRIBUTE_PNG_CLONE_DERIVED_FROM);
			slSelects.add(DataConstants.SELECT_PHYSICALID);
			
			DomainObject doEvolvedVPMReferenceObject=DomainObject.newInstance(context,strVPMReferenceObjectId);
			
			Map mpVPMRefData=doEvolvedVPMReferenceObject.getInfo(context, slSelects);
			  VPLMIntegTraceUtil.trace(context, ">>>mpVPMRefData::"+mpVPMRefData);
			  
			  HashMap hmAttr=new HashMap();
			  hmAttr.put(DataConstants.ATTRIBUTE_V_DERIVED_FROM, mpVPMRefData.get(DataConstants.SELECT_ATTRIBUTE_PNG_CLONE_DERIVED_FROM));
			  hmAttr.put(DataConstants.ATTRIBUTE_PNG_CLONE_DERIVED_FROM, "");
			  
			  VPLMIntegTraceUtil.trace(context, ">>>hmAttr:::"+hmAttr);
			  doEvolvedVPMReferenceObject.setAttributeValues(context, hmAttr);
			  VPLMIntegTraceUtil.trace(context, ">>>Updated clone derived from attributes on evolved VPMReference");
			  
			  //delete the proxy object
			  MapList mlProxy=commonUtility.findObjectWithWhereClause(context, DataConstants.TYPE_PROXY_OBJECT, (String)mpVPMRefData.get(DataConstants.SELECT_PHYSICALID), 
					  DataConstants.SEPARATOR_STAR, "",new StringList(DomainConstants.SELECT_ID));
			  VPLMIntegTraceUtil.trace(context, ">>>mlProxy::"+mlProxy);
			  
			  if(null!=mlProxy && !mlProxy.isEmpty()) {
				  Map mpProxy=(Map)mlProxy.get(0);
				  commonUtility.deleteObject(context, (String)mpProxy.get(DomainConstants.SELECT_ID));
				  VPLMIntegTraceUtil.trace(context, ">>>Deleted the proxy object of evolved VPMReference");
			  }
				  
			updateIsVPMVisibleAttr(context,arrOldRevisions);
			
			 DomainObject doTempOldVPMReference;
			 StringList slVPMReferenceRevIds=new StringList();
			 slVPMReferenceRevIds.add(strVPMReferenceObjectId);
			//no of revisions required to be created for VPMReference. Since, we already have evolution created, no of revisions required would be equal to previous revisions of EC Part
			 for(int i=0;i<arrOldRevisions.size();i++) {
				 
				 doTempOldVPMReference=DomainObject.newInstance(context,strVPMReferenceObjectId);
				 strVPMReferenceObjectId=createVPMRefRev.execute(context, doTempOldVPMReference);
				 VPLMIntegTraceUtil.trace(context, ">>>> Next revision of VPMReference:::"+strVPMReferenceObjectId);
				 slVPMReferenceRevIds.add(strVPMReferenceObjectId);
			 }
			 
			 //remove the last element (latest revision) from stringList
			 VPLMIntegTraceUtil.trace(context, ">>>> slVPMReferenceRevIds before removal of latest rev:::"+slVPMReferenceRevIds);
			 slVPMReferenceRevIds.remove(slVPMReferenceRevIds.size()-1);
			 VPLMIntegTraceUtil.trace(context, ">>>> slVPMReferenceRevIds after removal of latest rev:::"+slVPMReferenceRevIds);
			strNewEvolvedRevisedVPMReferenceId=strVPMReferenceObjectId;	  
			  
			  updateCADDesignOrigination(context,slVPMReferenceRevIds,DataConstants.RANGE_VALUE_SYSTEM);
			   updateCADDesignOrigination(context,new StringList(strNewEvolvedRevisedVPMReferenceId),DataConstants.RANGE_VALUE_MANUAL);
			   
			  String strReasonForChange="";
				  
			  //the revisions are given in sequence from latest till oldest (rev A being last one). hence reversed the order
			  Collections.reverse(arrOldRevisions);
			  Map mpECPartObjInfo;
			  
			  for(int i=0;i<arrOldRevisions.size();i++) {
					doTempECPartObject=DomainObject.newInstance(context,arrOldRevisions.get(i));
					doTempOldVPMReference=DomainObject.newInstance(context,slVPMReferenceRevIds.get(i));

					VPLMIntegTraceUtil.trace(context, ">>> EC Part object ID:::"+arrOldRevisions.get(i)+" VPMReference Object ID::"+slVPMReferenceRevIds.get(i));
					if(sbObjectIds.length()>0)
						sbObjectIds.append(DataConstants.SEPARATOR_PIPE);
					
					sbObjectIds.append(slVPMReferenceRevIds.get(i));
					
					mpECPartObjInfo=doTempECPartObject.getInfo(context, getECPartSelectables());
					
					strReasonForChange=(String) mpECPartObjInfo.get("attribute["+DataConstants.ATTRIBUTE_REASONFORCHANGE+"]");
					updateAttributesOfVPMReference(context, doTempOldVPMReference, mpECPartObjInfo, strReasonForChange);
					
					doTempOldVPMReference.setAttributeValue(context, DataConstants.ATTRIBUTE_IS_LAST_VERSION,DataConstants.CONSTANT_FALSE.toUpperCase());
					
					//connect Evolution with revision of EC Part revision
					connectNewObjects(context,doTempOldVPMReference,doTempECPartObject);
					
					// release VPMReference object
					enggItem.releaseVPMReference(context,doTempOldVPMReference);
				}
			  
			  	mpECPartObjInfo=doRevisedECPartObject.getInfo(context, getECPartSelectables());
				sbObjectIds.append(DataConstants.SEPARATOR_PIPE).append(strNewEvolvedRevisedVPMReferenceId);
				sbObjectIds.append(DataConstants.SEPARATOR_PIPE).append(mpECPartObjInfo.get(DomainConstants.SELECT_ID));
				addAutomationInterface(context,sbObjectIds);
				
				DomainObject doRevisedObjofEvolvedVPMReference=DomainObject.newInstance(context,strNewEvolvedRevisedVPMReferenceId);
				VPLMIntegTraceUtil.trace(context, ">>> strNewEvolvedRevisedVPMReferenceId:::"+strNewEvolvedRevisedVPMReferenceId);
				
				//update the PLMReference.V_isLastVersion to TRUE for latest revision
				updateAttributesOfVPMReference(context, doRevisedObjofEvolvedVPMReference, mpECPartObjInfo, strReasonForChange);
				doRevisedObjofEvolvedVPMReference.setAttributeValue(context,DataConstants.ATTRIBUTE_IS_LAST_VERSION, DataConstants.CONSTANT_TRUE.toUpperCase());
					
				 //connect the revised EC Part to the revision of evolved VPMReference
				connectNewObjects(context,doRevisedObjofEvolvedVPMReference,doRevisedECPartObject);
				
				obsoletePreviousRevisions(context,doRevisedObjofEvolvedVPMReference);
				VPLMIntegTraceUtil.trace(context, "<<< END of updateOperationOnPreviousRevisions method");
		}

		/**
		 * Method to update the isVPMVisible attribute on all rev of EC Part
		 * @param context
		 * @param arrOldRevisions
		 * @throws FrameworkException
		 */
		private void updateIsVPMVisibleAttr(Context context, ArrayList<String> arrOldRevisions) throws FrameworkException {
		VPLMIntegTraceUtil.trace(context, ">>> START of updateIsVPMVisibleAttr method");
			boolean bSuperUserContext=false;
			try {
				  ContextUtil.pushContext(context);
				  VPLMIntegTraceUtil.trace(context, ">>> user pushed before setting isVPMVisible attribute::"+context.getUser());
				  bSuperUserContext=true;
				  DomainObject doTempECPartObject;
				  
				  for(int i=0;i<arrOldRevisions.size();i++) {
					  doTempECPartObject=DomainObject.newInstance(context,arrOldRevisions.get(i));
						doTempECPartObject.setAttributeValue(context, DataConstants.ATTRIBUTE_ISVPMVISIBLE, DataConstants.CONSTANT_TRUE.toUpperCase()); 
				  }
			  }finally {
				  if(bSuperUserContext)
					  ContextUtil.popContext(context);
				  VPLMIntegTraceUtil.trace(context, ">>> user popped after setting isVPMVisible attribute::"+context.getUser());
			 }
			VPLMIntegTraceUtil.trace(context, "<<< END of updateIsVPMVisibleAttr method");
		}

		/**
		 * Method to promote the previous revisions of VPMReference to Obsolete state
		 * @param context
		 * @param doRevisedObjofEvolvedVPMReference
		 * @throws MatrixException
		 */
		private void obsoletePreviousRevisions(Context context, DomainObject doRevisedObjofEvolvedVPMReference) throws MatrixException {
			VPLMIntegTraceUtil.trace(context, ">>> START of obsoletePreviousRevisions method");  
			//promote the old revisions of VPMReference to Obsolete state
			  CommonProductData cpd=new CommonProductData(context);
			  EngineeringItem enggItem=new EngineeringItem(context);
			  
			  ArrayList<String> arrOldVPMRefRevisions=(ArrayList<String>)cpd.getPreviousRevisions(context, doRevisedObjofEvolvedVPMReference);
			  
			  DomainObject doTempObject;
			  MapList ml3dShapeDrawingObjects;
			  Map mpData;
			  //list has elements in descending order. We need to obsolete the revisions previous
			  //As current revision would be in Frozen, so its previous should be in Released state. Hence started the loop with i=1
			  for(int i=1;i<arrOldVPMRefRevisions.size();i++) {
				  doTempObject=DomainObject.newInstance(context,arrOldVPMRefRevisions.get(i));
				  enggItem.processPromoteDemotion(context, doTempObject,DataConstants.STATE_RELEASED,DataConstants.STATE_OBSOLETE.toUpperCase(),
						  DataConstants.STATE_FROZEN);
				   VPLMIntegTraceUtil.trace(context, ">>> Promoted VPMReference "+arrOldVPMRefRevisions.get(i)+" to Obsolete state");
				   
				   ml3dShapeDrawingObjects=enggItem.getRelatedRepInstance(context, doTempObject);
				   
				   for(int j=0;j<ml3dShapeDrawingObjects.size();j++) {
					   mpData=(Map)ml3dShapeDrawingObjects.get(j);
					   doTempObject=DomainObject.newInstance(context,(String)mpData.get(DomainConstants.SELECT_ID));
						  enggItem.processPromoteDemotion(context, doTempObject,DataConstants.STATE_RELEASED,DataConstants.STATE_OBSOLETE.toUpperCase(),
								  DataConstants.STATE_FROZEN);
						   VPLMIntegTraceUtil.trace(context, ">>> Promoted RepInstance "+arrOldVPMRefRevisions.get(i)+" to Obsolete state");
				   }
			  }
			VPLMIntegTraceUtil.trace(context, "<<< END of obsoletePreviousRevisions method");
		}

		/**
		 * Method to update the attributes on evolved VPMReference
		 * @param context
		 * @param doVPMReferenceObject
		 * @param mpRevisedECPartObjInfo
		 * @param strReasonForChange
		 * @throws MatrixException
		 */
		private void updateAttributesOfVPMReference(Context context, DomainObject doVPMReferenceObject, Map mpRevisedECPartObjInfo,String strReasonForChange) throws MatrixException {
			VPLMIntegTraceUtil.trace(context, ">>> Start of ProductDTMigration: updateAttributesOfVPMReference method");
			logger.debug(">>> Start of ProductDTMigration: updateAttributesOfVPMReference method");
			
			Map mpAttr=new HashMap();
			boolean bPackaging=false;
			boolean bProduct=false;
			BusinessInterfaceList busInterfaces = doVPMReferenceObject.getBusinessInterfaces(context);
			
			if(busInterfaces.toString().contains(DataConstants.INTERFACE_PNG_PACKAGING)) {
				bPackaging=true;
			}else if(busInterfaces.toString().contains(DataConstants.INTERFACE_PNG_PRODUCT)) {
				bProduct=true;
			}
			VPLMIntegTraceUtil.trace(context, ">>> bPackaging::"+bPackaging+" bProduct::"+bProduct);
			logger.debug(">>> bPackaging::"+bPackaging+" bProduct::"+bProduct);
			
			//since sync code is invoked on modify action of pngiDesignPart.pngManufacturingMaturityStatus, doing triggers off
			CommonUtility commonUtility=new CommonUtility(context);
			  commonUtility.executeMQLCommands(context,"trigger off");
			
			mpAttr.put(DataConstants.ATTRIBUTE_MFG_MATURITY_STATUS , (String)mpRevisedECPartObjInfo.get("attribute["+DataConstants.ATTR_RELEASE_PHASE+"]"));
			mpAttr.put(DataConstants.ATTRIBUTE_V_VERSION_COMMENT, strReasonForChange);
			
			//START: added for DTCLD-617: Make the revised EC Part Title same as source CAD object
			 if(UIUtil.isNotNullAndNotEmpty(strTitleMode) && strTitleMode.equalsIgnoreCase("Spec")) {
				 mpAttr.put(DataConstants.ATTRIBUTE_PLMENTITY_V_NAME, (String)mpRevisedECPartObjInfo.get("attribute["+DataConstants.ATTR_TITLE+"]"));
				 mpAttr.put(DataConstants.ATTRIBUTE_V_DESCRIPTION, (String)mpRevisedECPartObjInfo.get(DomainConstants.SELECT_DESCRIPTION));
			 }
			//END: added for DTCLD-617: Make the revised EC Part Title same as source CAD object
			
			if(bPackaging) {
				mpAttr.put(DataConstants.ATTRIBUTE_PACKAGING_SEGMENT, (String)mpRevisedECPartObjInfo.get(DataConstants.SELECT_ATTRIBUTE_SEGMENT));
				mpAttr.put(DataConstants.ATTRIBUTE_PACKAGING_CLASS, (String)mpRevisedECPartObjInfo.get(DataConstants.SELECT_ATTRIBUTE_CLASS));
				mpAttr.put(DataConstants.ATTRIBUTE_PACKAGING_MATERIAL_TYPE, (String)mpRevisedECPartObjInfo.get(DataConstants.SELECT_ATTRIBUTE_MATERIAL_TYPE));
				mpAttr.put(DataConstants.ATTRIBUTE_PACKAGING_MFG_STATUS, (String)mpRevisedECPartObjInfo.get(DataConstants.SELECT_ATTRIBUTE_LIFECYCLE_STATUS));
				mpAttr.put(DataConstants.ATTRIBUTE_PACKAGING_RELEASE_CRITERIA, (String)mpRevisedECPartObjInfo.get(DataConstants.SELECT_ATTRIBUTE_RELEASE_CRITERIA));
				mpAttr.put(DataConstants.ATTRIBUTE_PACKAGING_REPORTED_FUNCTION, 
						(String)mpRevisedECPartObjInfo.get("from["+DataConstants.RELATIONSHIP_PGPDTEMPLATES_TO_PGPLIREPORTEDFUNCTION+"].to.name"));
				mpAttr.put(DataConstants.ATTRIBUTE_PACKAGING_PRIMARY_ORGANIZATION, 
						(String)mpRevisedECPartObjInfo.get("from["+DataConstants.REL_PG_PRIMARY_ORGANIZATION+"].to.name"));
		
			}else if(bProduct) {
				mpAttr.put(DataConstants.ATTRIBUTE_PRODUCT_SEGMENT, (String)mpRevisedECPartObjInfo.get(DataConstants.SELECT_ATTRIBUTE_SEGMENT));
				mpAttr.put(DataConstants.ATTRIBUTE_PRODUCT_MFG_STATUS, (String)mpRevisedECPartObjInfo.get(DataConstants.SELECT_ATTRIBUTE_LIFECYCLE_STATUS));
				mpAttr.put(DataConstants.ATTRIBUTE_PRODUCT_RELEASE_CRITERIA, (String)mpRevisedECPartObjInfo.get(DataConstants.SELECT_ATTRIBUTE_RELEASE_CRITERIA));
				mpAttr.put(DataConstants.ATTRIBUTE_PRODUCT_PRIMARY_ORGANIZATION, 
						(String)mpRevisedECPartObjInfo.get("from["+DataConstants.REL_PG_PRIMARY_ORGANIZATION+"].to.name"));
			}
			
			VPLMIntegTraceUtil.trace(context, ">>> mpAttr::"+mpAttr);
			logger.debug(">>> mpAttr::"+mpAttr);
			
			doVPMReferenceObject.setAttributeValues(context, mpAttr);
			VPLMIntegTraceUtil.trace(context, ">>> Attributes set on evolved VPMReference");
			VPLMIntegTraceUtil.trace(context, "<<< End of ProductDTMigration: updateAttributesOfVPMReference method");
			commonUtility.executeMQLCommands(context,"trigger on");
			logger.debug(">>> Attributes set on evolved VPMReference");
			logger.debug("<<< End of ProductDTMigration: updateAttributesOfVPMReference method");
		}

		/**
		 * Method to connect the evolved VPMReference with revised EC Part object
		 * @param context
		 * @param doVPMReferenceObject
		 * @param doRevisedECPartObject
		 * @throws MatrixException
		 */
		private void connectNewObjects(Context context, DomainObject doVPMReferenceObject,DomainObject doRevisedECPartObject) throws MatrixException {
			VPLMIntegTraceUtil.trace(context, ">>> Start of ProductDTMigration: connectNewObjects method");
			logger.debug(">>> Start of ProductDTMigration: connectNewObjects method");
			
			ConnectParameters connectParams = new ConnectParameters();
			connectParams.setRelType(DataConstants.REL_PART_SPECIFICATION);
			connectParams.setFrom(true); 
			connectParams.setTarget(doVPMReferenceObject);
			doRevisedECPartObject.connect(context,connectParams);
			
			VPLMIntegTraceUtil.trace(context, ">>> Connected  EC Part "+doRevisedECPartObject+" with VPMReference "+doVPMReferenceObject+" using Part Specification rel");
			VPLMIntegTraceUtil.trace(context, "<<< End of ProductDTMigration: connectNewObjects method");
			
			logger.debug(">>> Connected  EC Part "+doRevisedECPartObject+" with VPMReference "+doVPMReferenceObject+" using Part Specification rel");
			logger.debug("<<< End of ProductDTMigration: connectNewObjects method");
		}

		/**
		 * Method to validate the Enterprise Type attribute and update attributes and interfaces on evolved VPMReference object
		 * @param context
		 * @param strEnterpriseType
		 * @param strDesignDomain
		 * @param strDesignDomainForVPMReference
		 * @param strNewVPMReferenceObjectId
		 * @param doVPMReferenceObject
		 * @throws MatrixException
		 */
		private void updateEnterpriseAttrAndInterface(Context context, String strEnterpriseType, String strDesignDomain, String strDesignDomainForVPMReference,
				String strNewVPMReferenceObjectId, DomainObject doVPMReferenceObject) throws MatrixException {
			
			  VPLMIntegTraceUtil.trace(context, ">>> Start of ProductDTMigration: updateEnterpriseAttrAndInterface method");
			  logger.debug(">>> Start of ProductDTMigration: updateEnterpriseAttrAndInterface method");
			  
			  Map mpAttr=new HashMap();
			  mpAttr.put(DataConstants.ATTRIBUTE_PGENTERPRISETYPE, strEnterpriseType);
			  mpAttr.put(DataConstants.ATTRIBUTE_DESIGN_DOMAIN, strDesignDomain);
			  VPLMIntegTraceUtil.trace(context, ">>> mpAttr:::"+mpAttr);
			  logger.debug(">>> mpAttr:::"+mpAttr);
			  			  
			  CommonUtility commonUtility=new CommonUtility(context);
			  commonUtility.executeMQLCommands(context,"trigger off");
			  doVPMReferenceObject.setAttributeValues(context, mpAttr);
			  VPLMIntegTraceUtil.trace(context, ">>> EnterpriseType and Design Domain updated on evolved VPMReference");
			  logger.debug(">>> EnterpriseType and Design Domain updated on evolved VPMReference");
			  commonUtility.executeMQLCommands(context,"trigger on");
			  
			  InterfaceManagement interfaceMgmt=new InterfaceManagement(context);
			  String  strInterfaceName;
			  boolean bInterfaceAdded;
			  
			  //remove the already added interface as per the Enterprise Type attribute of VPMReference
			  if(UIUtil.isNotNullAndNotEmpty(strDesignDomainForVPMReference)) {
				  strInterfaceName="pngi"+strDesignDomainForVPMReference;
				  VPLMIntegTraceUtil.trace(context, ">>>strDesignDomainForVPMReference  strInterfaceName::"+strInterfaceName);
				  logger.debug(">>> strDesignDomainForVPMReference strInterfaceName::"+strInterfaceName);
				  
				  bInterfaceAdded= interfaceMgmt.checkInterfaceOnObject(context, strNewVPMReferenceObjectId, strInterfaceName);
				  VPLMIntegTraceUtil.trace(context, ">>> bInterfaceAdded::"+bInterfaceAdded);
				  logger.debug(">>> bInterfaceAdded::"+bInterfaceAdded);
				  
				  if(bInterfaceAdded) {
					  interfaceMgmt.removeInterface(context, strNewVPMReferenceObjectId, strInterfaceName);
					  VPLMIntegTraceUtil.trace(context, ">>> Interface "+strInterfaceName+" removed from evolved VPMReference");
					  logger.debug(">>> Interface "+strInterfaceName+" removed from evolved VPMReference");
				  }
			  }
			  
			  //add interface as per the EC Part type
			  strInterfaceName="pngi"+strDesignDomain;
			  VPLMIntegTraceUtil.trace(context, ">>> strInterfaceName::"+strInterfaceName);
			  logger.debug(">>> strInterfaceName::"+strInterfaceName);
			  
			  bInterfaceAdded= interfaceMgmt.checkInterfaceOnObject(context, strNewVPMReferenceObjectId, strInterfaceName);
			  VPLMIntegTraceUtil.trace(context, ">>> bInterfaceAdded::"+bInterfaceAdded);
			  logger.debug(">>> bInterfaceAdded::"+bInterfaceAdded);
			  
			  if(!bInterfaceAdded) {
				  interfaceMgmt.addInterface(context, strNewVPMReferenceObjectId, strInterfaceName);
				  VPLMIntegTraceUtil.trace(context, ">>> Interface "+strInterfaceName+" added on evolved VPMReference");
				  logger.debug(">>> Interface "+strInterfaceName+" added on evolved VPMReference");
			  }
			  VPLMIntegTraceUtil.trace(context, "<<< End of ProductDTMigration: updateEnterpriseAttrAndInterface method");
			  logger.debug("<<< End of ProductDTMigration: updateEnterpriseAttrAndInterface method");
		}
		
		/**
		 * Method to generate the message response in case migration is successful.
		 * @param context
		 * @param strEvolvedVPMReferenceObjectId
		 * @param strRevisedECPartObjectId
		 * @throws FrameworkException
		 */
		private void generateMigratedMessageString(Context context, String strEvolvedVPMReferenceObjectId,String strRevisedECPartObjectId) throws FrameworkException {
			VPLMIntegTraceUtil.trace(context, ">>> START of ProductDTMigration: generateMigratedMessageString method");
			logger.debug( ">>> START of ProductDTMigration: generateMigratedMessageString method");
			
			StringList slObjSelects=new StringList(3);
			slObjSelects.add(DomainConstants.SELECT_TYPE);
			slObjSelects.add(DomainConstants.SELECT_NAME);
			slObjSelects.add(DomainConstants.SELECT_REVISION);
			
			DomainObject doObject=DomainObject.newInstance(context,strEvolvedVPMReferenceObjectId);
			if(UIUtil.isNotNullAndNotEmpty(strNewEvolvedRevisedVPMReferenceId))
				doObject=DomainObject.newInstance(context,strNewEvolvedRevisedVPMReferenceId);
			
			Map mpVPMRefData=doObject.getInfo(context, slObjSelects);
			
			doObject=DomainObject.newInstance(context,strRevisedECPartObjectId);
			Map mpECPartData=doObject.getInfo(context, slObjSelects);
			
			DataConstants.customWorkProcessD2SExceptions newEvolutionCreated=customWorkProcessD2SExceptions.MESSAGE_200_NEW_VPMREF_EVOLUTION;
			DataConstants.customWorkProcessD2SExceptions newEvolutionAndRevCreated=customWorkProcessD2SExceptions.MESSAGE_200_NEW_VPMREF_EVOLUTION_AND_REVISION;
			DataConstants.customWorkProcessD2SExceptions newRevisionCreated=customWorkProcessD2SExceptions.MESSAGE_200_NEW_ECPART_REVISION;;
			
			sbMessageResponseData.append("Migrated").append(DataConstants.SEPARATOR_COMMA);
			
			if(bOldRevExists) {
				sbMessageResponseData.append(newEvolutionAndRevCreated.getExceptionCode()).append(DataConstants.SEPARATOR_COLON);
				sbMessageResponseData.append(newEvolutionAndRevCreated.getExceptionMessage());
			}else {
				sbMessageResponseData.append(newEvolutionCreated.getExceptionCode()).append(DataConstants.SEPARATOR_COLON);
				sbMessageResponseData.append(newEvolutionCreated.getExceptionMessage());
			}
			sbMessageResponseData.append(DataConstants.SEPARATOR_PIPE);
			
			String strOriginalECPartObjId=doECPartObject.getInfo(context, DomainConstants.SELECT_ID);
			if(!strRevisedECPartObjectId.equals(strOriginalECPartObjId)) {
				sbMessageResponseData.append(newRevisionCreated.getExceptionCode()).append(DataConstants.SEPARATOR_COLON);
				sbMessageResponseData.append(newRevisionCreated.getExceptionMessage()).append(DataConstants.SEPARATOR_COLON);
			}
			sbMessageResponseData.append(mpECPartData.get(DomainConstants.SELECT_TYPE)).append(DataConstants.CONSTANT_DOT);
			sbMessageResponseData.append(mpECPartData.get(DomainConstants.SELECT_NAME)).append(DataConstants.CONSTANT_DOT);
			sbMessageResponseData.append(mpECPartData.get(DomainConstants.SELECT_REVISION));
			
			sbMessageResponseData.append(DataConstants.SEPARATOR_COMMA);
			sbMessageResponseData.append(mpVPMRefData.get(DomainConstants.SELECT_TYPE)).append(DataConstants.CONSTANT_DOT);
			sbMessageResponseData.append(mpVPMRefData.get(DomainConstants.SELECT_NAME)).append(DataConstants.CONSTANT_DOT);
			sbMessageResponseData.append(mpVPMRefData.get(DomainConstants.SELECT_REVISION));
			
			VPLMIntegTraceUtil.trace(context, "Migration successful message::"+sbMessageResponseData.toString());
			VPLMIntegTraceUtil.trace(context, "<<< End of ProductDTMigration: generateMigratedMessageString method");
			
			logger.debug("Migration successful message::"+sbMessageResponseData.toString());
			logger.debug("<<< End of ProductDTMigration: generateMigratedMessageString method");
		}
		
		/**
		 * Method to process the objects mentioned by user in the input file
		 * @param context
		 * @param mpObjDetails
		 * @param int i
		 * @param strMode
		 * @throws Exception 
		 */
		@Override
		public void processObjects(Context context,Map mpObjectDetails,String strMode) throws Exception {
			VPLMIntegTraceUtil.trace(context, ">>> START of ProductDTMigration:processObjects method");
			logger.debug(">>> START of ProductDTMigration:processObjects method");
			
			CommonUtility commonUtility=new CommonUtility(context);
			DataConstants.customWorkProcessD2SExceptions errorTransactionAborted=customWorkProcessD2SExceptions.ERROR_400_TRANSACTION_ABORTED;
			String strEvolvedVPMReferenceObjectId = "";
			String strRevisedECPartObjectId="";
			String strOriginalECPartObjID="";
			Map mpVPMRefObject=new HashMap();
			Map mpECPartObject=new HashMap();
			try {
				super.processObjects(context, mpObjectDetails, strMode);
			
				VPLMIntegTraceUtil.trace(context, ">>> ProductDTMigration:processObjects strValidVPMReferenceObject:::"+strValidVPMReferenceObject+" strValidECPartObject::"+strValidECPartObject);
				logger.debug( ">>> ProductDTMigration:processObjects strValidVPMReferenceObject:::"+strValidVPMReferenceObject+" strValidECPartObject::"+strValidECPartObject);
				
				if(Boolean.parseBoolean(strValidVPMReferenceObject) && Boolean.parseBoolean(strValidECPartObject)) {
					
					VPLMIntegTraceUtil.trace(context, ">>> ProductDTMigration:processObjects before invoking process method");
					logger.debug(">>> ProductDTMigration:processObjects before invoking process method");
					
					mpVPMRefObject=process(context, doVPMReferenceObject,strVPMRefType);
					strEvolvedVPMReferenceObjectId=(String) mpVPMRefObject.get("validRev");
					
					strOriginalECPartObjID=doECPartObject.getInfo(context, DomainConstants.SELECT_ID);
					mpECPartObject=process(context, doECPartObject,strECPartType);
					strRevisedECPartObjectId=(String) mpECPartObject.get("validRev");
					
					VPLMIntegTraceUtil.trace(context,">>> evolved VPM Ref physical id:::"+strEvolvedVPMReferenceObjectId);
					VPLMIntegTraceUtil.trace(context,">>> revised VPM Ref object id:::"+strRevisedECPartObjectId);
					
					logger.debug(">>> evolved VPM Ref physical id:::"+strEvolvedVPMReferenceObjectId);
					logger.debug(">>> revised VPM Ref object id:::"+strRevisedECPartObjectId);
					
					updateObjects(context, strEvolvedVPMReferenceObjectId, strRevisedECPartObjectId);
					
					if(UIUtil.isNotNullAndNotEmpty(strEvolvedVPMReferenceObjectId) && UIUtil.isNotNullAndNotEmpty(strRevisedECPartObjectId)) {
						generateMigratedMessageString(context,strEvolvedVPMReferenceObjectId,strRevisedECPartObjectId);
					}
				}
			}catch(Exception e){
				VPLMIntegTraceUtil.trace(context,"Inside catch...");
				VPLMIntegTraceUtil.trace(context, "exception message::"+e.getMessage());
				VPLMIntegTraceUtil.trace(context, "Response message inside catch:::"+sbMessageResponseData.toString());
				
				logger.debug("Inside catch...");
				logger.debug("exception message::"+e.getMessage());
				logger.debug("Response message inside catch:::"+sbMessageResponseData.toString());
				
				int iLastIndexOfNewLine=sbMessageResponseData.toString().lastIndexOf("\n");
				VPLMIntegTraceUtil.trace(context, ">>> iLastIndexOfNewLine::::"+iLastIndexOfNewLine);
				logger.debug(">>> iLastIndexOfNewLine::::"+iLastIndexOfNewLine);				
				
				String strLatestMessage="";
				if(iLastIndexOfNewLine>=0) {
					strLatestMessage=sbMessageResponseData.toString().substring(iLastIndexOfNewLine, sbMessageResponseData.toString().length());
					VPLMIntegTraceUtil.trace(context, ">>> strLatestMessage::::"+strLatestMessage);
					logger.debug(">>> strLatestMessage::::"+strLatestMessage);
				}
				
				// to replace the word Migrated with Error in case some error has occurred
					if(UIUtil.isNotNullAndNotEmpty(strLatestMessage)) {
						if(strLatestMessage.indexOf("Migrated")>=0) {
							strLatestMessage=strLatestMessage.replace("Migrated","Error");
							
							//add the message back to stringbuilder
							String strMessageWithoutLastLine=sbMessageResponseData.toString().substring(0,iLastIndexOfNewLine);
							VPLMIntegTraceUtil.trace(context, ">>> strMessageWithoutLastLine::::"+strMessageWithoutLastLine);
							logger.debug(">>> strMessageWithoutLastLine::::"+strMessageWithoutLastLine);
							
							sbMessageResponseData=new StringBuilder();
							sbMessageResponseData.append(strMessageWithoutLastLine).append(strLatestMessage);
							VPLMIntegTraceUtil.trace(context, ">>> Modified sbMessageResponseData::::"+sbMessageResponseData.toString());
							logger.debug(">>> Modified sbMessageResponseData::::"+sbMessageResponseData.toString());
						}
					}
						
					VPLMIntegTraceUtil.trace(context, "before adding the transaction aborted message ");
					logger.debug("before adding the transaction aborted message ");
					sbMessageResponseData.append(DataConstants.SEPARATOR_PIPE).append(" Error ");
					sbMessageResponseData.append(errorTransactionAborted.getExceptionCode()).append(DataConstants.SEPARATOR_COLON);
					sbMessageResponseData.append(errorTransactionAborted.getExceptionMessage());
					
					if(!e.getMessage().equals(errorTransactionAborted.getExceptionMessage()))
						sbMessageResponseData.append(e.getMessage());
					
					VPLMIntegTraceUtil.trace(context, "after adding the transaction aborted message:::"+sbMessageResponseData.toString());
					VPLMIntegTraceUtil.trace(context,errorTransactionAborted.getExceptionMessage());
					
					logger.debug( "after adding the transaction aborted message:::"+sbMessageResponseData.toString());
					logger.debug(errorTransactionAborted.getExceptionMessage());
					
					//since evolution happens in another transaction, explicitly deleting it, if there is any error while revising the EC Part
					ContextUtil.pushContext(context);
					
					commonUtility.deleteObject(context,strEvolvedVPMReferenceObjectId);
					VPLMIntegTraceUtil.trace(context,">>>Deleted the evolved VPMReference object");
					
					//delete the new revision of evolved VPMReference
					if(bOldRevExists && UIUtil.isNotNullAndNotEmpty(strNewEvolvedRevisedVPMReferenceId)) {
						commonUtility.deleteObject(context, strNewEvolvedRevisedVPMReferenceId);
						VPLMIntegTraceUtil.trace(context,">>>Deleted the revision of evolved VPMReference object");
					}
					
					VPLMIntegTraceUtil.trace(context,">>> strOriginalECPartObjID::"+strOriginalECPartObjID+" strRevisedECPartObjectId::"+strRevisedECPartObjectId);

					//delete the revised EC Part
					if(!strOriginalECPartObjID.equals(strRevisedECPartObjectId)) {
						commonUtility.deleteObject(context,strRevisedECPartObjectId);
						VPLMIntegTraceUtil.trace(context,">>>Deleted the revised EC Part object");
					}
			}finally {
				VPLMIntegTraceUtil.trace(context,"bIsContextPushed:::"+bIsContextPushed);
				logger.debug("bIsContextPushed:::"+bIsContextPushed);
				
				if(bIsContextPushed) {
					ContextUtil.popContext(context);
					VPLMIntegTraceUtil.trace(context, ">>> context is popped");
					logger.debug( ">>> context is popped");
				}
			}
			VPLMIntegTraceUtil.trace(context, "<<< END of ProductDTMigration:processObjects method");
			logger.debug("<<< END of ProductDTMigration:processObjects method");
		}
		
		/**
		 * Method to get VPMReference and its connected data and check for correct nomenclature of name
		 * @param context
		 * @param doEvolvedVPMReferenceObject
		 * @throws FrameworkException
		 */
		private void updateVPMReferenceName(Context context,DomainObject doEvolvedVPMReferenceObject,MapList ml3DShapeDrawing) throws FrameworkException {
		
			VPLMIntegTraceUtil.trace(context, ">>> START of ProductDTMigration:updateVPMReferenceName method");
			String strName=doEvolvedVPMReferenceObject.getInfo(context, DomainConstants.SELECT_NAME);
			VPLMIntegTraceUtil.trace(context, ">>> VPMReference original strName::"+strName);
			
			if(!StringUtils.isNumeric(strName)) {
				VPLMIntegTraceUtil.trace(context, ">>> VPMReference name is not numeric");
				//replace the letters with 0
				updateNameAndPLMExternalID(context,strName,doEvolvedVPMReferenceObject);
				
				//get the connected 3DShape and Drawing objects
				if(null!=ml3DShapeDrawing && !ml3DShapeDrawing.isEmpty()) {
					Map mpData;
					String strObjID;
					DomainObject doObj;
					
					for(int i=0;i<ml3DShapeDrawing.size();i++) {
						mpData=(Map)ml3DShapeDrawing.get(i);
						strName=(String) mpData.get(DomainConstants.SELECT_NAME);
						
						VPLMIntegTraceUtil.trace(context, ">>>3DShape/Drawing Original name::"+strName);
						
						strObjID=(String)mpData.get(DomainConstants.SELECT_ID);
						doObj=DomainObject.newInstance(context,strObjID);
						updateNameAndPLMExternalID(context,strName,doObj);
					}
				}
			}
			VPLMIntegTraceUtil.trace(context, "<<< END of ProductDTMigration:updateVPMReferenceName method");
		}
		
		/**
		 * Method to get the modified name and update it on object
		 * @param context
		 * @param strName
		 * @param doObject
		 * @throws FrameworkException
		 */
		private void updateNameAndPLMExternalID(Context context,String strName,DomainObject doObject) throws FrameworkException {
			VPLMIntegTraceUtil.trace(context, ">>> START of ProductDTMigration:updateNameAndPLMExternalID method");
			char cChar;
			char[] cArray=strName.toCharArray();
			char[] cFinalArray=new char[strName.length()];
					
			for(int i=0;i<cArray.length;i++) {
				cChar=cArray[i];
				if(i<=2)
					cChar='0';
				cFinalArray[i]=cChar;
			}
			strName=String.valueOf(cFinalArray);
			
			VPLMIntegTraceUtil.trace(context, ">>> Modified strName::"+strName);
			//update the name of the VPMReference and PLM_ExternalId attribute
			doObject.setName(context, strName);
			doObject.setAttributeValue(context,DataConstants.ATTRIBUTE_PLMENTITY_PLM_EXTERNALID, strName);
			
			VPLMIntegTraceUtil.trace(context, ">>> Modified name and PLMEntity.PLM_ExternalID of object "+doObject);
			VPLMIntegTraceUtil.trace(context, "<<< END of ProductDTMigration:updateNameAndPLMExternalID method");
		}
		
		/**
		 * Added for DTCLD-617:  Method to update the Title and Description of EC Parts same as CAD Title and description
		 * @param context
		 * @param arrOldRevisions 
		 * @param doRevisedECPartObject
		 * @param strVPMReferenceTitle 
		 * @param strVPMReferenceDesc 
		 * @throws FrameworkException 
		 */
		private void updateTitleDescriptionOfECParts(Context context, ArrayList<String> arrOldRevisions, DomainObject doRevisedECPartObject,
				String strVPMReferenceTitle, String strVPMReferenceDesc) throws FrameworkException {
			 VPLMIntegTraceUtil.trace(context, ">>>START of updateTitleDescriptionOfECParts method");
			 
			 boolean bContextPushed=false;
			 
			 if(!arrOldRevisions.isEmpty()) {
				 DomainObject doObject;
				 //user would not have modify access on the objects , as they can be in released/obsolete states. Hence, pushing context
				   try {
					   ContextUtil.pushContext(context);
					   bContextPushed=true;
					   
					 for(int i=0;i<arrOldRevisions.size();i++) {
						doObject=DomainObject.newInstance(context,arrOldRevisions.get(i));
						doObject.setAttributeValue(context, DataConstants.ATTR_TITLE, strVPMReferenceTitle);
						doObject.setDescription(context,strVPMReferenceDesc);
					 }
				   }finally {
					   if(bContextPushed)
						   ContextUtil.popContext(context);
				   }
			 }
			 doRevisedECPartObject.setAttributeValue(context, DataConstants.ATTR_TITLE, strVPMReferenceTitle);
			 doRevisedECPartObject.setDescription(context,strVPMReferenceDesc);
			 
			 VPLMIntegTraceUtil.trace(context, ">>> Updated the Title and Description of EC Part objects");
			  VPLMIntegTraceUtil.trace(context, "<<< END of updateTitleDescriptionOfECParts method");
		}
}


package com.pg.designtools.integrations.ebom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.vplmintegration.util.VPLMIntegTraceUtil;
import com.pg.designtools.datamanagement.DataConstants;
import com.pg.designtools.datamanagement.EngineeringItem;

import matrix.db.BusinessObject;
import matrix.db.BusinessObjectWithSelect;
import matrix.db.BusinessObjectWithSelectList;
import matrix.db.Context;
import matrix.db.Path;
import matrix.db.PathWithSelect;
import matrix.db.PathWithSelectList;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class ImportIntoReference  {
	
		
		public ImportIntoReference() {
			super();
		}
		
		/**
		 * Main method invoked form web service
		 * @param context
		 * @param strBaseProdName
		 * @param strBaseProdRev
		 * @param strImportedProdName
		 * @param strImportedProdRev
		 * @param bRevise
		 * @throws MatrixException 
		 */
		public StringBuilder importIntoVPMReference(Context context,String strBaseProdName,String strBaseProdRev,String strImportedProdName,
				String strImportedProdRev, boolean bRevise) throws MatrixException {
			
			VPLMIntegTraceUtil.trace(context, ">>>> START of importIntoVPMReference method");
		
			boolean bIsTransactionStarted=false;
			boolean bContextPushed=false;
			BusinessObject baseProductBO=null;
			BusinessObject importedProductBO=null;
			
			StringBuilder sbReturnMessages = new StringBuilder();
			
			try {
					
				baseProductBO = new BusinessObject(DataConstants.TYPE_VPMREFERENCE, strBaseProdName,strBaseProdRev, DataConstants.VAULT_VPLM);
				importedProductBO = new BusinessObject(DataConstants.TYPE_VPMREFERENCE, strImportedProdName,strImportedProdRev, DataConstants.VAULT_VPLM);
				boolean bBaseProdExists = baseProductBO.exists(context);
				boolean bImportedProdExists = importedProductBO.exists(context);
				
				VPLMIntegTraceUtil.trace(context, ">>>>baseProductBO::"+baseProductBO);
				VPLMIntegTraceUtil.trace(context, ">>>>importedProductBO::"+importedProductBO);
				VPLMIntegTraceUtil.trace(context, ">>>>bBaseProdExists::"+bBaseProdExists+" bImportedProdExists::"+bImportedProdExists);
				
				if (bBaseProdExists && bImportedProdExists) {

					EngineeringItem enggItem=new EngineeringItem(context);
				
					StringList slSelects=new StringList(2);
					slSelects.add(DomainConstants.SELECT_ID);
					slSelects.add(DomainConstants.SELECT_CURRENT);
					slSelects.add(DataConstants.SELECT_ATTRIBUTE_MFG_MATURITY_STATUS);
						
					DomainObject domBaseProdObj = DomainObject.newInstance(context, baseProductBO);
					Map mpBaseVPMRefInfo= domBaseProdObj.getInfo(context, slSelects);
					String strBaseProductId=(String)mpBaseVPMRefInfo.get(DomainConstants.SELECT_ID);
					VPLMIntegTraceUtil.trace(context, ">>>>strBaseProductId::"+strBaseProductId);
									
					String strRevisedBaseProdId=DomainConstants.EMPTY_STRING;
						
					ContextUtil.startTransaction(context, true);
					bIsTransactionStarted = true;
					
					if(bRevise)
					{
						//objectList::[{newPhase=Development, currentPhase=Development, name=00000619263, revision=001.001}]
						Map mpDataForRevision=new HashMap();
						String strMfgMaturityStatus=(String)mpBaseVPMRefInfo.get(DataConstants.SELECT_ATTRIBUTE_MFG_MATURITY_STATUS);
						mpDataForRevision.put("newPhase", strMfgMaturityStatus);
						mpDataForRevision.put("currentPhase", strMfgMaturityStatus);
						mpDataForRevision.put(DomainConstants.SELECT_NAME, strBaseProdName);
						mpDataForRevision.put(DomainConstants.SELECT_REVISION, strBaseProdRev);
						
						VPLMIntegTraceUtil.trace(context, ">>>>mpDataForRevision::"+mpDataForRevision);
						
						CollaborateEBOMJobFacility coll=new CollaborateEBOMJobFacility();
						coll.objectList.add(mpDataForRevision);
						Map mpRevisedData=coll.processObjectsFromXMLForReviseUpstage(context);
						VPLMIntegTraceUtil.trace(context, ">>>>mpRevisedData::"+mpRevisedData);
						
						strRevisedBaseProdId=(String)mpRevisedData.get("RevisedVPMReferenceId");
						sbReturnMessages.append(mpRevisedData.get("message"));
						sbReturnMessages.append(DataConstants.CONSTANT_NEW_LINE);
					}else {
						strRevisedBaseProdId=strBaseProductId;
					}
						
					VPLMIntegTraceUtil.trace(context, ">>>>strRevisedBaseProdId::"+strRevisedBaseProdId);
								
					if(UIUtil.isNotNullAndNotEmpty(strRevisedBaseProdId))
					{
							DomainObject domRevisedBaseProd = DomainObject.newInstance(context,strRevisedBaseProdId);
							if(domRevisedBaseProd.exists(context))
							{
								//current user might not have access to the imported object. Hence pushContext is used
								ContextUtil.pushContext(context);
								bContextPushed = true;
								DomainObject doImportedProd=DomainObject.newInstance(context,importedProductBO);
								
								//disconnect the 3dShape and Drawings connected to the revised product
								VPLMIntegTraceUtil.trace(context, ">>>>Invoking disconnect3DShapeDrawings for revised object");
								disconnect3DShapeDrawings(context, domRevisedBaseProd,enggItem);	
								
								//disconnect the core materials connected to the revised product
								VPLMIntegTraceUtil.trace(context, ">>>>Invoking disconnectCoreMaterials for revised object");
								disconnectCoreMaterials(context, domRevisedBaseProd,enggItem); 
								
								//get the info of 3DShape and Drawings of the imported data 
								VPLMIntegTraceUtil.trace(context, ">>>>Invoking getRepInstanceRelConnectedObjInfo for imported object");
								Map mpRepInstanceImportedDataInfo= getRepInstanceRelConnectedObjInfo(context,doImportedProd,enggItem);
								
								//connect the 3DShape and Drawings of the imported data to the revised product
								VPLMIntegTraceUtil.trace(context, ">>>>Invoking connect3DShapeDrawings for revised object");
								Map newRepInstances=connect3DShapeDrawings(context, domRevisedBaseProd,mpRepInstanceImportedDataInfo,enggItem);
								Map mpImportedDataRepInstances=(Map) mpRepInstanceImportedDataInfo.get(DataConstants.STR_REP_INSTANCES_MAP);
								
								//Update the repinstance attributes as per the imported data on the revised product
								updateRepInstanceAttributes(context,mpImportedDataRepInstances,newRepInstances);
								
								//update the semantic relations on the 3DShape and Drawing as per the Imported data on the revised product
								updateSemanticRelationsOn3DShapeDrawing(context,domRevisedBaseProd, doImportedProd,enggItem);
							
								VPLMIntegTraceUtil.trace(context, ">>>>Invoking disconnectCoreMaterials for imported object");
								//disconnect the core materials connected to the imported product
								disconnectCoreMaterials(context,doImportedProd,enggItem);
														
								//disconnect 3DShape and Drawing from imported product
								VPLMIntegTraceUtil.trace(context, ">>>>Invoking disconnectRelationships for imported object");
								StringList slImportedDataRelationshipIds=(StringList)mpRepInstanceImportedDataInfo.get(DataConstants.STR_REP_INSTANCES_RELID);
								enggItem.disconnectRelationships(context, slImportedDataRelationshipIds);
								
								StringList selectables = new StringList(2);
								selectables.add(DomainConstants.SELECT_NAME);
								selectables.add(DomainConstants.SELECT_REVISION);
								
								Map mpRevisedRevProdDetails = domRevisedBaseProd.getInfo(context, selectables);
								String strRevisedProductName = (String)mpRevisedRevProdDetails.get(DomainConstants.SELECT_NAME);
								String strRevisedRevProductRev = (String)mpRevisedRevProdDetails.get(DomainConstants.SELECT_REVISION);
								VPLMIntegTraceUtil.trace(context, ">>>>strRevisedProductName::"+strRevisedProductName+" strRevisedRevProductRev::"+strRevisedRevProductRev);
					
								sbReturnMessages.append(DataConstants.STR_SUCCESS);
								sbReturnMessages.append(DataConstants.SEPARATOR_COLON);
								sbReturnMessages.append("Object Updated::");
								sbReturnMessages.append(strRevisedProductName);
								sbReturnMessages.append(DataConstants.CONSTANT_STRING_SPACE);
								sbReturnMessages.append(strRevisedRevProductRev);
							}
							else
							{
									sbReturnMessages.append(DataConstants.STR_ERROR);
									sbReturnMessages.append(DataConstants.SEPARATOR_COLON);
									sbReturnMessages.append(DataConstants.OBJECT_TO_BE_UPDATED_DOES_NOT_EXIST);
							}
						}							
						else
						{
								sbReturnMessages.append(DataConstants.STR_ERROR);
								sbReturnMessages.append(DataConstants.SEPARATOR_COLON);
								sbReturnMessages.append(DataConstants.OBJECT_TO_BE_UPDATED_DOES_NOT_EXIST);
						}
				}else {
					sbReturnMessages.append(DataConstants.STR_ERROR);
					sbReturnMessages.append(DataConstants.SEPARATOR_COLON);
					sbReturnMessages.append(DataConstants.IMPORT_WS_OBJECT_NOT_EXIST);
				}
		}catch (Exception e) {
			VPLMIntegTraceUtil.trace(context, ">>> Inside catch of importIntoVPMReference method");
			//VPLMIntegTraceUtil.printStackTrace(context, e);
			if(bIsTransactionStarted)
			{
				ContextUtil.abortTransaction(context);
				bIsTransactionStarted = false;
			}
		}finally {
			
		
			if(bContextPushed)
			{
				ContextUtil.popContext(context);
			}
			if(bIsTransactionStarted)
			{
				ContextUtil.commitTransaction(context);	
			}	
		}
		return sbReturnMessages;
	}
    
	/**
	 * Method to disconnect the 3DShape and Drawings from the VPMReference
	 * @param context
	 * @param doVPMReferenceObject
	 * @param enggItem
	 * @throws FrameworkException
	 */
	private void disconnect3DShapeDrawings(Context context, DomainObject doVPMReferenceObject,EngineeringItem enggItem) throws FrameworkException {		
		VPLMIntegTraceUtil.trace(context, ">>>> START of disconnect3DShapeDrawings method");
		Map mpFinalOuput=getRepInstanceRelConnectedObjInfo(context, doVPMReferenceObject,enggItem);
		
		if(!mpFinalOuput.isEmpty()) {
			StringList slRelationshipIds=(StringList)mpFinalOuput.get(DataConstants.STR_REP_INSTANCES_RELID);
			VPLMIntegTraceUtil.trace(context, ">>>> slRelationshipIds::"+slRelationshipIds);

			enggItem.disconnectRelationships(context, slRelationshipIds);
			VPLMIntegTraceUtil.trace(context, ">>>> Disconnected relationships");
		}
		VPLMIntegTraceUtil.trace(context, "<<<< END of disconnect3DShapeDrawings method");
	}	
	
	/**
	 * Method to disconnect the core materials
	 * @param context
	 * @param doVPMReferenceObject
	 * @param enggItem
	 * @throws FrameworkException
	 */
	private void disconnectCoreMaterials(Context context, DomainObject doVPMReferenceObject,EngineeringItem enggItem) throws FrameworkException {
		VPLMIntegTraceUtil.trace(context,">>>> START of disconnectCoreMaterials method");
		MapList mlCoreMaterials = enggItem.getConnectedCoreMaterials(context, doVPMReferenceObject);
		if(!mlCoreMaterials.isEmpty()) {
			Map mpCoreMaterial;
			StringList slCoreMaterialConnIDList = new StringList();
			
			for(int i = 0 ; i < mlCoreMaterials.size(); i++) {
				mpCoreMaterial = (Map)mlCoreMaterials.get(i);
				slCoreMaterialConnIDList.add((String)mpCoreMaterial.get(DomainRelationship.SELECT_ID));
			}
			VPLMIntegTraceUtil.trace(context,">>>> slCoreMaterialConnIDList::"+slCoreMaterialConnIDList);
			
			enggItem.disconnectRelationships(context, slCoreMaterialConnIDList);
			VPLMIntegTraceUtil.trace(context,">>>> Disconnected relationships of core materials");
		}
		VPLMIntegTraceUtil.trace(context,"<<<< END of disconnectCoreMaterials method");
	}
	
	/**
	 * Method to get the information of objects connected to VPMReference using RepInstance relationship
	 * @param context
	 * @param doVPMReferenceObject
	 * @param enggItem
	 * @return Map
	 */
	private Map getRepInstanceRelConnectedObjInfo(Context context,DomainObject doVPMReferenceObject,EngineeringItem enggItem) {
		VPLMIntegTraceUtil.trace(context, ">>>> START of getRepInstanceRelConnectedObjInfo method");
		MapList ml3DShapeDrawing = enggItem.getRelatedRepInstance(context, doVPMReferenceObject);
		Map mpFinalMap=new HashMap();
		
		if(null!=ml3DShapeDrawing && !ml3DShapeDrawing.isEmpty())
		{
			Map mp3DShapeDrawing;
			String strObjectId;
			String strRelId;
			String strName;
			Map mpRepInstances=new HashMap();
			StringList slObjectIds=new StringList();
			StringList slRelationshipIds=new StringList();
			
			for(int i=0;i<ml3DShapeDrawing.size();i++)
			{
				mp3DShapeDrawing = (Map)ml3DShapeDrawing.get(i);
				
				strObjectId = (String)mp3DShapeDrawing.get(DomainConstants.SELECT_ID);
				strRelId = (String)mp3DShapeDrawing.get(DomainRelationship.SELECT_ID);
				strName = (String)mp3DShapeDrawing.get(DomainConstants.SELECT_NAME);
				
				mpRepInstances.put(strObjectId, strName);
				slObjectIds.add(strObjectId);
				slRelationshipIds.add(strRelId);
			}
			
			mpFinalMap.put(DataConstants.STR_REP_INSTANCES_MAP, mpRepInstances);
			mpFinalMap.put(DataConstants.STR_REP_INSTANCES_OID, slObjectIds);
			mpFinalMap.put(DataConstants.STR_REP_INSTANCES_RELID,slRelationshipIds);
		}
		VPLMIntegTraceUtil.trace(context, ">>>> mpFinalMap::"+mpFinalMap);
		VPLMIntegTraceUtil.trace(context, "<<<< END of getRepInstanceRelConnectedObjInfo method");

		return mpFinalMap;
	}
	
	/**
	 * Method to connect the 3DShape and Drawings
	 * @param context
	 * @param doVPMReferenceObject
	 * @param mpRepInstanceImportedDataInfo
	 * @param enggItem
	 * @return Map
	 */
	private Map connect3DShapeDrawings(Context context, DomainObject doVPMReferenceObject,Map mpRepInstanceImportedDataInfo,EngineeringItem enggItem)
			throws FrameworkException {
		VPLMIntegTraceUtil.trace(context, ">>>> START of connect3DShapeDrawings method");
		
		StringList slObjectIds=(StringList)mpRepInstanceImportedDataInfo.get(DataConstants.STR_REP_INSTANCES_OID);
		VPLMIntegTraceUtil.trace(context, ">>>> slObjectIds::"+slObjectIds);
		
		Map newRepInstances=enggItem.connectObjects(context, slObjectIds,doVPMReferenceObject,DataConstants.REL_VPM_REPINSTANCE);
		VPLMIntegTraceUtil.trace(context, ">>>> newRepInstances::"+newRepInstances);
		VPLMIntegTraceUtil.trace(context, "<<<< END of connect3DShapeDrawings method");

		return newRepInstances;
	}
	
	/**
	 * Method to update the attributes on RepInstance relationship
	 * @param context
	 * @param mpImportedDataRepInstances
	 * @param newRepInstances
	 * @throws FrameworkException
	 */
	private void updateRepInstanceAttributes(Context context, Map mpImportedDataRepInstances, Map newRepInstances) throws FrameworkException {
		VPLMIntegTraceUtil.trace(context, ">>>>START of updateRepInstanceAttributes method");
		String strObjectId;
		String strRelId;	
		String str3DShapeDrawingName;
		DomainRelationship domRelRepInstance;
		VPLMIntegTraceUtil.trace(context, ">>>>mpImportedDataRepInstances::"+mpImportedDataRepInstances);
		VPLMIntegTraceUtil.trace(context, ">>>>newRepInstances::"+newRepInstances);
		
		if(null != newRepInstances && !newRepInstances.isEmpty() && null != mpImportedDataRepInstances && !mpImportedDataRepInstances.isEmpty())
		{
			for (Iterator iterator = newRepInstances.keySet().iterator(); iterator.hasNext();) 
			{
				strObjectId = (String) iterator.next();
				strRelId = (String) newRepInstances.get(strObjectId);
				VPLMIntegTraceUtil.trace(context, ">>>>strObjectId::"+strObjectId+" strRelId::"+strRelId);
				
				if(mpImportedDataRepInstances.containsKey(strObjectId))
				{
					str3DShapeDrawingName = (String)mpImportedDataRepInstances.get(strObjectId);
					VPLMIntegTraceUtil.trace(context, ">>>>str3DShapeDrawingName::"+str3DShapeDrawingName);
					
					domRelRepInstance = new DomainRelationship(strRelId);
					domRelRepInstance.setAttributeValue(context, DataConstants.ATTRIBUTE_PLMINSTANCE_PLM_EXTERNAL_ID, new StringBuilder(str3DShapeDrawingName).append(".1").toString());
					VPLMIntegTraceUtil.trace(context, ">>>>Updated PLMInstance.PLM_ExternalID attribute on rel ::"+strRelId+"with value:: "+str3DShapeDrawingName+".1");
				}
			}
		}
		VPLMIntegTraceUtil.trace(context, "<<<< END of updateRepInstanceAttributes method");
	}
	
	/**
	 * Method to update the semantic relationships of 3DShape and Drawings
	 * @param context
	 * @param domRevisedBaseProd
	 * @param doImportedProd
	 * @param enggItem
	 * @throws MatrixException
	 */
	private void updateSemanticRelationsOn3DShapeDrawing(Context context, DomainObject domRevisedBaseProd, DomainObject doImportedProd,EngineeringItem enggItem) throws MatrixException {
		VPLMIntegTraceUtil.trace(context, ">>>> START of updateSemanticRelationsOn3DShapeDrawing method");

		List<String> listPaths;
		List<String> listPathElementPhysicalId;
		List<String> listPathElementRelevantId;
		List<String> listPathElementKind;
		
		boolean isRelavant = false;							  
		StringList selectOnPath = new StringList(3);
		selectOnPath.add(DataConstants.SELECT_ELEMENT_PHYSICAL_ID);
		selectOnPath.add(DataConstants.SELECT_ELEMENT_KIND);
		selectOnPath.add(DataConstants.SELECT_ELEMENT_RELEVANT);
		
		Map mpNew3DShapeConnectionInfo = null;
		
		StringList slProductSelects = new StringList(3);
		slProductSelects.add(DataConstants.SELECT_PHYSICALID);
		slProductSelects.add(DataConstants.SELECT_LOGICAL_ID);
		slProductSelects.add(DataConstants.SELECT_MAJOR_ID);
		
		Map mpRevisedBaseProductRevisionInfo = domRevisedBaseProd.getInfo(context, slProductSelects);
		VPLMIntegTraceUtil.trace(context, ">>>> mpRevisedBaseProductRevisionInfo::"+mpRevisedBaseProductRevisionInfo);
		
		MapList ml3DShapeInfoList  = get3DShapeConnectionDetail(context,doImportedProd,domRevisedBaseProd,enggItem);
		VPLMIntegTraceUtil.trace(context, ">>>> ml3DShapeInfoList::"+ml3DShapeInfoList);
		
		listPaths=getListOfPaths(context,domRevisedBaseProd,enggItem);
		VPLMIntegTraceUtil.trace(context, ">>>> listPaths::"+listPaths);
		
		if(!listPaths.isEmpty())
		{
			PathWithSelectList pathWithSelect= Path.getSelectPathData(context, listPaths.toArray(new String[]{}),selectOnPath);
			VPLMIntegTraceUtil.trace(context, ">>>> pathWithSelect::"+pathWithSelect);

			for (PathWithSelect  path : pathWithSelect ) 
			{
				isRelavant = false;
				listPathElementKind =  path.getSelectDataList("element[0].kind");
				listPathElementRelevantId =  path.getSelectDataList("element[0].relevant");
				
				VPLMIntegTraceUtil.trace(context, ">>>> listPathElementKind::"+listPathElementKind);
				VPLMIntegTraceUtil.trace(context, ">>>>listPathElementRelevantId::"+listPathElementRelevantId);
				
				if(!listPathElementRelevantId.isEmpty() && listPathElementRelevantId.get(0).equalsIgnoreCase(DataConstants.CONSTANT_TRUE)) {
						isRelavant  = true;
				}
				VPLMIntegTraceUtil.trace(context, ">>>> isRelavant::"+isRelavant);
				
				if(null !=listPathElementKind && listPathElementKind.contains("businessobject")) {
					VPLMIntegTraceUtil.trace(context, ">>>> Inside listPathElementKind contains BO condition");
					//Update new VPMReference Details
					modifyPath(context,path,DataConstants.TYPE_VPMREFERENCE,mpRevisedBaseProductRevisionInfo,isRelavant);
				}
				else if(null !=listPathElementKind && listPathElementKind.contains("connection")) {
					VPLMIntegTraceUtil.trace(context, ">>>> Inside listPathElementKind contains connection condition");
					
					listPathElementPhysicalId =  path.getSelectDataList("element[1].physicalid");
					VPLMIntegTraceUtil.trace(context, ">>>> listPathElementPhysicalId::"+listPathElementPhysicalId);
					
					if(null == listPathElementPhysicalId || (null != listPathElementPhysicalId && listPathElementPhysicalId.isEmpty())) {
						VPLMIntegTraceUtil.trace(context, ">>>> Inside listPathElementPhysicalId nullorempty condition");
						
						listPathElementPhysicalId = path.getSelectDataList("element[0].physicalid");
						//Element 0 Case
						if(!listPathElementPhysicalId.isEmpty() && listPathElementPhysicalId.size() == 1) {
							mpNew3DShapeConnectionInfo = getMapFromBaseProduct(ml3DShapeInfoList,listPathElementPhysicalId.get(0),listPathElementPhysicalId.get(0));
							VPLMIntegTraceUtil.trace(context, ">>> mpNew3DShapeConnectionInfo::"+mpNew3DShapeConnectionInfo);
							
							if(!mpNew3DShapeConnectionInfo.isEmpty()) {
								//Update for 3DShape connection IDs
								modifyPath(context,path,DataConstants.REL_VPM_REPINSTANCE,mpNew3DShapeConnectionInfo,isRelavant);
							}
						}
					}
					else if(null != listPathElementPhysicalId && !listPathElementPhysicalId.isEmpty() && listPathElementPhysicalId.size() == 1) {
						VPLMIntegTraceUtil.trace(context, ">>>>  Inside listPathElementPhysicalId NOT nullorempty condition");
						
						//Element 1 Case
						mpNew3DShapeConnectionInfo = getMapFromBaseProduct(ml3DShapeInfoList,listPathElementPhysicalId.get(0),DataConstants.STR_SELECT_TO_PHYSICALID);
						VPLMIntegTraceUtil.trace(context, ">>>> mpNew3DShapeConnectionInfo::"+mpNew3DShapeConnectionInfo);
								
						if(!mpNew3DShapeConnectionInfo.isEmpty()) {
							//Update for 3DShape connection IDs
							modifyPath(context,path,DataConstants.REL_VPM_REPINSTANCE,mpNew3DShapeConnectionInfo,isRelavant);
						}
					}
				}
			}	
		}
		VPLMIntegTraceUtil.trace(context, "<<<< END of updateSemanticRelationsOn3DShapeDrawing method");
	}
	
	/**
	 * Method to get the list of paths
	 * @param context
	 * @param doVPMReferenceObject
	 * @param enggItem
	 * @return List
	 * @throws MatrixException
	 */
	private List getListOfPaths(Context context,DomainObject doVPMReferenceObject, EngineeringItem enggItem) throws MatrixException {
		VPLMIntegTraceUtil.trace(context, ">>>> START of getListOfPaths method");

		List<String> listPaths = new ArrayList<>();
		List<String> listPathIds;
		StringList selectOnCnx = new StringList(1);
		selectOnCnx.add(DataConstants.SELECT_SEMANTIC_PATH_ID);
		
		StringList slDrawingObjectIds = getDrawingObjectIds(context,doVPMReferenceObject,enggItem);
		BusinessObjectWithSelectList queryCnx = BusinessObject.getSelectBusinessObjectData (context, slDrawingObjectIds.toArray(new String[slDrawingObjectIds.size()]), selectOnCnx);
		Iterator iterqueryCnx = queryCnx.iterator();
		BusinessObjectWithSelect cnx = null;
		while(iterqueryCnx.hasNext()) {
			cnx = (BusinessObjectWithSelect) iterqueryCnx.next();
			listPathIds =  cnx.getSelectDataList(DataConstants.SELECT_SEMANTIC_PATH_ID);
		
			if (null != listPathIds)
		    {
		    	listPaths.addAll(listPathIds);
		    }
		}
		VPLMIntegTraceUtil.trace(context, ">>>>listPaths::"+listPaths);
		VPLMIntegTraceUtil.trace(context, "<<<< END of getListOfPaths method");

		return listPaths;
	}
	
	/**
	 * Method to get the 3DShape connection details
	 * @param context
	 * @param doImportedProd
	 * @param domRevisedProdObj
	 * @param enggItem
	 * @return MapList
	 */
	private MapList get3DShapeConnectionDetail(Context context, DomainObject doImportedProd,DomainObject domRevisedProdObj,EngineeringItem enggItem) {
		VPLMIntegTraceUtil.trace(context, ">>>> START of get3DShapeConnectionDetail method");

		MapList mlReturnList = new MapList();
	
		Map mpBase;
		Map mpRevised;
		String strBase3DShapePhysicalID;
		String strRevised3DShapePhysicalID;
		String strBaseConnection3DShapePhysicalID;
		
		MapList mlImportedData3DShapeList=get3DShapeObjects(context,doImportedProd,enggItem);
		VPLMIntegTraceUtil.trace(context, ">>>> mlImportedData3DShapeList::"+mlImportedData3DShapeList);
		
		MapList mlRevisedData3DShapeList =get3DShapeObjects(context,domRevisedProdObj,enggItem);
		VPLMIntegTraceUtil.trace(context, ">>>> mlRevisedData3DShapeList::"+mlRevisedData3DShapeList);
		
		if(!mlImportedData3DShapeList.isEmpty()) {
			for(int i = 0 ; i < mlImportedData3DShapeList.size(); i++) {
				mpBase = (Map)mlImportedData3DShapeList.get(i);
				
				strBase3DShapePhysicalID = (String)mpBase.get(DataConstants.STR_SELECT_TO_PHYSICALID);
				strBaseConnection3DShapePhysicalID = (String)mpBase.get(DataConstants.SELECT_PHYSICALID);
				
				for(int j = 0; j < mlRevisedData3DShapeList.size();j++) {
					mpRevised = (Map)mlRevisedData3DShapeList.get(j);
					
					strRevised3DShapePhysicalID = (String)mpRevised.get(DataConstants.STR_SELECT_TO_PHYSICALID);
					
					if(strBase3DShapePhysicalID.equals(strRevised3DShapePhysicalID)) {
						mpRevised.put(strBaseConnection3DShapePhysicalID, strBaseConnection3DShapePhysicalID);
						mlReturnList.add(mpRevised);
					}
				}
			}
		}
		VPLMIntegTraceUtil.trace(context, ">>>> mlReturnList::"+mlReturnList);
		return mlReturnList;
	}
	
	/**
	 * Method to modify the Path
	 * @param context
	 * @param path
	 * @param strElementName
	 * @param mpProductInfo
	 * @param isRelavant
	 * @throws MatrixException
	 */
	private void modifyPath(Context context,PathWithSelect path, String strElementName,Map mpProductInfo,boolean isRelavant) throws MatrixException {
		VPLMIntegTraceUtil.trace(context, ">>> START of modifyPath method");
		String strPhysicalId=(String)mpProductInfo.get(DataConstants.SELECT_PHYSICALID);
		Path.Element[] paramArry;
		int[] intIndex;
		paramArry = new  matrix.db.Path.Element[1];
		paramArry[0] = new matrix.db.Path.Element(0, strElementName, 
				strPhysicalId, 
				(String)mpProductInfo.get(DataConstants.SELECT_MAJOR_ID), 
				(String)mpProductInfo.get(DataConstants.SELECT_LOGICAL_ID), DataConstants.STR_ZEROS, isRelavant);
		intIndex = new int[1];
		path.modifyPath(context, intIndex, paramArry);
		VPLMIntegTraceUtil.trace(context, ">>> Modify Path element::"+strPhysicalId);
		VPLMIntegTraceUtil.trace(context, "<<<< END of modifyPath method");
	}
	
	/** Method to find equivalent Map from multiple 3dShape connection details
	 * @param ml3dShapeInfoList
	 * @param strCurrentPhysicalID
	 * @param strSelectable
	 * @return Map
	 */
	private Map<String,String> getMapFromBaseProduct(MapList ml3dShapeInfoList, String strCurrentPhysicalID,String strSelectable) {
		Map<String,String> mpReturnMap = new HashMap<>();
		Map mpConnection;
		String strPhysicalId;
		if(!ml3dShapeInfoList.isEmpty()) {
			for(int i =0; i < ml3dShapeInfoList.size(); i++) {
				mpConnection = (Map)ml3dShapeInfoList.get(i);
				strPhysicalId = (String) mpConnection.get(strSelectable);
				if(UIUtil.isNotNullAndNotEmpty(strPhysicalId) && strPhysicalId.equals(strCurrentPhysicalID))  {
					mpReturnMap = mpConnection;
				}
			}
		}
		return mpReturnMap;
	}
	
	/**
	 * Method to get the objectIds of the Drawing
	 * @param context
	 * @param doVPMReferenceObject
	 * @param enggItem
	 * @return StringList
	 */
	private StringList getDrawingObjectIds(Context context, DomainObject doVPMReferenceObject,EngineeringItem enggItem) {
		VPLMIntegTraceUtil.trace(context, ">>>> START of getDrawingObjectIds method");
		
		StringList slDrawingIDs = new StringList();
	
		MapList mlRepInstanceData=enggItem.getRelatedRepInstance(context, doVPMReferenceObject);
		if(null!=mlRepInstanceData && !mlRepInstanceData.isEmpty()) {
			Map mpRepInstanceData;
			
			for(int i=0;i<mlRepInstanceData.size();i++) {
				mpRepInstanceData=(Map)mlRepInstanceData.get(i);
				
				if(DataConstants.TYPE_DRAWING.equals(mpRepInstanceData.get(DomainConstants.SELECT_TYPE))) {
					slDrawingIDs.add((String)mpRepInstanceData.get(DomainConstants.SELECT_ID));
				}
			}
		}
		VPLMIntegTraceUtil.trace(context, ">>>> slDrawingIDs::"+slDrawingIDs);
		VPLMIntegTraceUtil.trace(context, "<<<< END of getDrawingObjectIds method");

		return slDrawingIDs;
	}
	
	/**
	 * Method to get the 3D Shape objects
	 * @param context
	 * @param doVPMReferenceObject
	 * @param enggItem
	 * @return MapList
	 */
	private MapList get3DShapeObjects(Context context, DomainObject doVPMReferenceObject,EngineeringItem enggItem) {
		VPLMIntegTraceUtil.trace(context, ">>>> START of get3DShapeObjects method");
		
		MapList ml3DShapeDetails=new MapList();
		MapList mlRepInstanceData=enggItem.getRelatedRepInstance(context, doVPMReferenceObject);
		VPLMIntegTraceUtil.trace(context, ">>>>mlRepInstanceData::"+mlRepInstanceData);
		
		if(null!=mlRepInstanceData && !mlRepInstanceData.isEmpty()) {
			Map mpRepInstanceData;
			
			for(int i=0;i<mlRepInstanceData.size();i++) {
					mpRepInstanceData=(Map)mlRepInstanceData.get(i);
				
				if(DataConstants.TYPE_3DSHAPE.equals(mpRepInstanceData.get(DomainConstants.SELECT_TYPE))) {
					ml3DShapeDetails.add(mpRepInstanceData);
				}
			}
		}
		VPLMIntegTraceUtil.trace(context, ">>>>ml3DShapeDetails::"+ml3DShapeDetails);
		VPLMIntegTraceUtil.trace(context, "<<<< END of get3DShapeObjects method");
		return ml3DShapeDetails;
	}
}
package com.pg.designtools.datamanagement;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import com.dassault_systemes.enovia.gls.common.model.PRSPContext;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.vplmintegration.util.VPLMIntegTraceUtil;
import com.pg.designtools.util.InterfaceManagement;
import matrix.db.BusinessInterfaceList;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.db.Signature;
import matrix.db.SignatureList;
import matrix.util.MatrixException;
import matrix.util.Pattern;
import matrix.util.StringList;

public class EngineeringItem {

	public EngineeringItem(Context context) {
			PRSPContext.set(context);
	}
	public EngineeringItem() {
		super();
	}
	   
	/**
	 * Method to sync to Enterprise
	 * @param context
	 * @param strObjId
	 * @param strSyncTransfer
	 * @return result
	 * @throws MatrixException
	 */
	public String syncToEnterprise (Context context,String strObjId,String strSyncTransfer) throws MatrixException {

		//GQS - lifted from pgSyncEBOMDefer_mxJPO; sync engine is VPLM_API - VPLMIntegSynchronizeEngineVPLM_API
		VPLMIntegTraceUtil.trace(context, "START of syncToEnterprise method");
		//vplm::SynchronizeWithMatrix or vplm::SynchronizeAndTransferToMatrix
		//Secured commands that control access to feature in UI
		//for PG work process if one can create then allow sync
	
		return syncToEnterprise(context, strObjId, strSyncTransfer, "1");
	}
	
	/**
	 * Method to sync to Enterprise
	 * @param context
	 * @param strObjId
	 * @param strSyncTransfer
	 * @return result
	 * @throws MatrixException
	 */
	public String syncToEnterprise(Context context,String strObjId,String strSyncTransfer,String strSyncDepth) throws MatrixException {

		//GQS - lifted from pgSyncEBOMDefer_mxJPO; sync engine is VPLM_API - VPLMIntegSynchronizeEngineVPLM_API
		VPLMIntegTraceUtil.trace(context, "START of syncToEnterprise with sync depth method");
		//vplm::SynchronizeWithMatrix or vplm::SynchronizeAndTransferToMatrix
		//Secured commands that control access to feature in UI
		//for PG work process if one can create then allow sync
		Hashtable<String, String> argTable = new Hashtable<>();
		argTable.put("ROOTID", strObjId);
		argTable.put("SYNC_AND_TRANSFER", strSyncTransfer);
		argTable.put("objectId", strObjId);
		argTable.put("SYNC_DEPTH", strSyncDepth);

		VPLMIntegTraceUtil.trace(context, "argTable:::"+argTable);
		String[] arguments = JPO.packArgs(argTable);
		String strResult = "";
		try {
		Hashtable results=JPO.invoke(context, "VPLMIntegBOMVPLMSynchronizeBase", null, "synchronizeFromVPMToMatrix", arguments, Hashtable.class);

		VPLMIntegTraceUtil.trace(context, "syncToEnterprise results:::"+results);
		
		if (results.containsKey(strObjId)) {
			Map rpts = (Map) results.get(strObjId);
			if (rpts.containsKey("ERROR_MESSAGES")){
				//Changed ArrayList to Vector for PU2-103
				Vector err = (Vector) rpts.get("ERROR_MESSAGES");
				if (!err.isEmpty()) {
					strResult = err.toString();
					throw new MatrixException(strResult);
				}
			}
			if (rpts.containsKey("DETAILED_REPORT")){
				//Changed ArrayList to Vector for PU2-103
				Vector arrDetailedReport = (Vector) rpts.get("DETAILED_REPORT");
				if (!arrDetailedReport.isEmpty()) {
					strResult = arrDetailedReport.toString();
				}else if(rpts.containsKey("SHORT_REPORT")){
					Vector arrShortReport = (Vector) rpts.get("SHORT_REPORT");
					if (!arrShortReport.isEmpty()) {
						strResult = arrShortReport.toString();
					}
				}
			}
		}
		}catch(Exception e) {
			VPLMIntegTraceUtil.trace(context, "syncToEnterprise Inside Catch:::"+e.getMessage());
			throw e;
		}
		return strResult;
	}
	
	public String syncToDesign (Context context,String strObjId,String strSyncTransfer) throws Exception {

		VPLMIntegTraceUtil.trace(context, "START of syncToDesign method");
		//GQS - lifted from pgSyncEBOMDefer_mxJPO; sync engine is MATRIX_API - VPLMIntegSynchronizeEngineMatrix_API
		
		//vplm::SynchronizeWithMatrix or vplm::SynchronizeAndTransferToMatrix
		//Secured commands that control access to feature in UI
		//for PG work process if one can create then allow sync
		return syncToDesign(context, strObjId, strSyncTransfer, "1");
	}
	
	public String syncToDesign (Context context,String strObjId,String strSyncTransfer,String strSyncDepth) throws Exception {

		VPLMIntegTraceUtil.trace(context, "START of syncToDesign with sync depth method");
	
		//DTCLD-265 ALM 49534 Changed HashMap to Hashtable
		Hashtable<String, String> argTable = new Hashtable();
		argTable.put("ROOTID", strObjId);
		argTable.put("SYNC_AND_TRANSFER", strSyncTransfer);
		argTable.put("objectId", strObjId);
		argTable.put("SYNC_DEPTH", strSyncDepth);
		
		VPLMIntegTraceUtil.trace(context, "argTable:::"+argTable);
		String[] arguments = JPO.packArgs(argTable);
		String strResult = "";
		try {
			//DTCLD-265 ALM 49534 Mapped return type to Hashtable
			Map results=new Hashtable();
			results=JPO.invoke(context, "VPLMIntegBOMVPLMSynchronizeBase", null, "synchronizeFromMatrixToVPM", arguments, Hashtable.class);
			VPLMIntegTraceUtil.trace(context, "syncToDesign results:::"+results);
	
			if (results.containsKey(strObjId)) {
				Map rpts = (Map) results.get(strObjId);
				if (rpts.containsKey("ERROR_MESSAGES")){
					//Changed ArrayList to Vector for PU2-103
					Vector err = (Vector) rpts.get("ERROR_MESSAGES");
					if (!err.isEmpty()) {
						strResult = err.toString();
						throw new MatrixException(strResult);
					}
				}
				if (rpts.containsKey("DETAILED_REPORT")){
					//Changed ArrayList to Vector for PU2-103
					Vector arrDetailedReport = (Vector) rpts.get("DETAILED_REPORT");
					if (!arrDetailedReport.isEmpty()) {
						strResult = arrDetailedReport.toString();
					}
				}
			}
		}catch(Exception e) {
			VPLMIntegTraceUtil.trace(context, "Inside Catch of syncToDesign:::"+e.getMessage());
			throw e;
		}
		return strResult;
	}
	
	/**
	 * Added for DTCLD-265 ALM 49534 BOM Disconnecting during Revise
	 * @param context
	 * @param strObjId objectId of the EC Part
	 * @param strSyncTransfer whether control should be transferred to CATIA
	 * @param strSyncDepth
	 * @return DomainObject of VPMReference
	 * @throws Exception
	 */
	public DomainObject syncToDesignAsDomain (Context context,String strObjId,String strSyncTransfer,String strSyncDepth) throws Exception {
		VPLMIntegTraceUtil.trace(context, ">>> START of syncToDesignAsDomain method");
		VPLMIntegTraceUtil.trace(context, "syncToDesignAsDomain strObjId:::"+strObjId+" strSyncTransfer::"+strSyncTransfer+" strSyncDepth::"+strSyncDepth);
		
		DomainObject doObject=DomainObject.newInstance(context,strObjId);
		StringList slOldPartSpecIds=doObject.getInfoList(context, "from["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION+"].to["+DataConstants.TYPE_VPMREFERENCE+"].id");  
		VPLMIntegTraceUtil.trace(context, "syncToDesignAsDomain slPartSpecIds before sync ::"+slOldPartSpecIds);
	
		String strResult=syncToDesign(context, strObjId, strSyncTransfer, strSyncDepth);
		
		VPLMIntegTraceUtil.trace(context, "syncToDesignAsDomain strResult:::"+strResult);
		
		DomainObject doResultObject=null;
		
		StringList slPartSpecIds=doObject.getInfoList(context, "from["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION+"].to["+DataConstants.TYPE_VPMREFERENCE+"].id");  
		VPLMIntegTraceUtil.trace(context, "syncToDesignAsDomain slPartSpecIds after sync ::"+slPartSpecIds);
		
		String strNewCADId = doObject.getInfo(context, "from["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION+"].to["+DataConstants.TYPE_VPMREFERENCE+"].id");  
		VPLMIntegTraceUtil.trace(context, "syncToDesignAsDomain strNewCADId::"+strNewCADId);
		
		if(UIUtil.isNotNullAndNotEmpty(strNewCADId)) {
			doResultObject=DomainObject.newInstance(context,strNewCADId);
		}
		VPLMIntegTraceUtil.trace(context, "syncToDesignAsDomain doResultObject:::"+doResultObject);
		VPLMIntegTraceUtil.trace(context, "<<< END of syncToDesignAsDomain method");
		return doResultObject;
	}

	/**
	 * Method to Promote VPMRef to Frozen state
	 * @param context
	 * @param strVPMReferenceObjId
	 * @throws MatrixException 
	 */
	public String freezeVPMReference (Context context, String strVPMReferenceObjId) throws MatrixException
	{
		String strResult="";
		if(UIUtil.isNotNullAndNotEmpty(strVPMReferenceObjId))
		{
			DomainObject domVPMRefObj  = DomainObject.newInstance(context, strVPMReferenceObjId);
			String strVPMRefCurrent = domVPMRefObj.getInfo(context, DomainConstants.SELECT_CURRENT);

			if(DataConstants.STATE_IN_WORK.equalsIgnoreCase(strVPMRefCurrent)) 
			{
				SignatureList slSignature = domVPMRefObj.getSignatures(context,strVPMRefCurrent,DataConstants.STATE_FROZEN);                                                                                                                       
				for(Signature objSignature : slSignature)
				{
					domVPMRefObj.approveSignature(context, objSignature, DomainConstants.EMPTY_STRING);  
				}
				SignatureList slSignatureLocal = domVPMRefObj.getSignatures(context,DataConstants.STATE_IN_WORK,DataConstants.STATE_PRIVATE);                                                                                                                             
				for(Signature objSignature : slSignatureLocal)
				{
					if(objSignature.isSigned())
					{
						domVPMRefObj.rejectSignature(context, objSignature, DomainConstants.EMPTY_STRING);
					}
				}
				domVPMRefObj.setState(context, DataConstants.STATE_FROZEN);
				strResult=DataConstants.STR_SUCCESS;
			}
		}
		return strResult;
	}

	/**
	 * Method to Promote VPMRef from Private to In Work to Frozen state
	 * @param context
	 * @param strVPMReferenceObjId
	 * @throws MatrixException 
	 */
	public String promoteVPMReferenceFromPrivateToFrozen (Context context, String strVPMReferenceObjId) throws MatrixException
	{
		boolean isContextPushed = false;
		String strResult="";
		try{
			
			if(UIUtil.isNotNullAndNotEmpty(strVPMReferenceObjId))
			{
				DomainObject domVPMRefObj  = DomainObject.newInstance(context, strVPMReferenceObjId);
				String strVPMRefCurrent = domVPMRefObj.getInfo(context, DomainConstants.SELECT_CURRENT);
				
				if(DataConstants.STATE_PRIVATE.equalsIgnoreCase(strVPMRefCurrent)) 
				{
					SignatureList slInWorkSignature = domVPMRefObj.getSignatures(context,strVPMRefCurrent,DataConstants.STATE_IN_WORK);        
					                                                                                                            
					for(Signature objSignature : slInWorkSignature)
					{
						domVPMRefObj.approveSignature(context, objSignature, DomainConstants.EMPTY_STRING);  
					}
					domVPMRefObj.setState(context, DataConstants.STATE_IN_WORK);
									
					SignatureList slFrozenSignature = domVPMRefObj.getSignatures(context,DataConstants.STATE_IN_WORK,DataConstants.STATE_FROZEN); 
					
					for(Signature objSignature : slFrozenSignature)
					{
						domVPMRefObj.approveSignature(context, objSignature, DomainConstants.EMPTY_STRING);  
					}
					SignatureList slPrivateSignature = domVPMRefObj.getSignatures(context,DataConstants.STATE_IN_WORK,DataConstants.STATE_PRIVATE);             
					                                                                                                               
					for(Signature objSignature : slPrivateSignature)
					{
						if(objSignature.isSigned())
						{
							// Here Context user wont have access to reject the signature hence push context is needed
							ContextUtil.pushContext(context, DataConstants.PERSON_USER_AGENT, null, context.getVault().getName());
							isContextPushed = true;
							domVPMRefObj.rejectSignature(context, objSignature, DomainConstants.EMPTY_STRING);
						}
					}
					domVPMRefObj.setState(context, DataConstants.STATE_FROZEN);	
					strResult=DataConstants.STR_SUCCESS;
				}
			}
		}
		finally
		{
			if(isContextPushed)
			{
				ContextUtil.popContext(context);
				isContextPushed = false;
			}
		}	
		return strResult;
	}

	/**
	 * Method to demote VPMRef to In Work state
	 * @param context
	 * @param strVPMReferenceObjId
	 * @throws FrameworkException 
	 */
	public void demoteVPMReference (Context context, String strVPMReferenceObjId) throws FrameworkException 
	{
		try
		{
			if(UIUtil.isNotNullAndNotEmpty(strVPMReferenceObjId))
			{
				DomainObject domVPMRefObj  = DomainObject.newInstance(context, strVPMReferenceObjId);
				String strVPMRefCurrent = domVPMRefObj.getInfo(context, DomainConstants.SELECT_CURRENT);

				if(DataConstants.STATE_FROZEN.equalsIgnoreCase(strVPMRefCurrent)) 
				{
					SignatureList slSignature = domVPMRefObj.getSignatures(context,strVPMRefCurrent,DataConstants.STATE_IN_WORK);                                                                                                                         
					for(Signature objSignature : slSignature)
					{
						domVPMRefObj.approveSignature(context, objSignature, DomainConstants.EMPTY_STRING); 
					}
					SignatureList slSignatureLocal = domVPMRefObj.getSignatures(context,DataConstants.STATE_FROZEN,DataConstants.STATE_RELEASED);                                                                                                                             
					for(Signature objSignature : slSignatureLocal)
					{
						if(objSignature.isSigned())
						{
							domVPMRefObj.rejectSignature(context, objSignature, DomainConstants.EMPTY_STRING);
						}
					}
					domVPMRefObj.setState(context, DataConstants.STATE_IN_WORK);
					String[] arguments=new String[4];
					arguments[0]=strVPMReferenceObjId;
					arguments[1]=DataConstants.STATE_FROZEN;
					arguments[2]=DataConstants.STATE_IN_WORK;
					arguments[3]=DataConstants.CONSTANT_DEMOTE;
					JPO.invoke(context, "pgDSOCATIAIntegration", null, "autoPromoteDemote3DShapeConnected", arguments, Integer.class);
					JPO.invoke(context, "pgDSOCATIAIntegration", null, "autoPromoteDemoteDrawingConnected", arguments, Integer.class);
				}
			}
		}
		catch(Exception ex)
		{
			VPLMIntegTraceUtil.trace(context, ex.getMessage());
		}
	}
	
	/**
	 * Method to get the objects connected to VPMReference with VPLMRepInstance relationship.
	 * @param context
	 * @param doVPMReferenceObject
	 * @return MapList
	 */
	public MapList getRelatedRepInstance(Context context,DomainObject doVPMReferenceObject) {
		VPLMIntegTraceUtil.trace(context, ">>>> START of getRelatedRepInstance method");
		
		MapList ml3DShapeDrawing = new MapList();
		try
		{	
			StringList busSelects = new StringList(6);
			busSelects.add(DomainConstants.SELECT_ID);
			busSelects.add(DomainConstants.SELECT_TYPE);
			busSelects.add(DomainConstants.SELECT_NAME);
			busSelects.add(DomainConstants.SELECT_REVISION);
			busSelects.add(DomainConstants.SELECT_CURRENT);
			busSelects.add(DataConstants.SELECT_ATTRIBUTE_ISONCE_INSTANTIABLE);
			
			StringList relSelects = new StringList(5);
			relSelects.add(DomainRelationship.SELECT_ID);
			relSelects.add(DataConstants.SELECT_PHYSICALID);
			relSelects.add(DataConstants.SELECT_LOGICAL_ID);
			relSelects.add(DataConstants.SELECT_MAJOR_ID);
			relSelects.add(DataConstants.STR_SELECT_TO_PHYSICALID);
			
			ml3DShapeDrawing = doVPMReferenceObject.getRelatedObjects(context, 
					DataConstants.REL_VPM_REPINSTANCE, 				// relationshipPattern
					DomainConstants.QUERY_WILDCARD, 					// typePattern
					busSelects, 																// objectSelects
					relSelects, 																// relSelects
					false, 																		// getTo
					true, 																		// getFrom
					(short)1, 																	// recurseToLevel
					null, 																		// objectWhere
					null, 																		// relationshipWhere
					0);																			// limit
			
			ml3DShapeDrawing.sort(DomainConstants.SELECT_TYPE, "ascending", "string");
		}
		catch(Exception ex)
		{
			VPLMIntegTraceUtil.trace(context,">>>> Inside catch block of getRelatedRepInstance method");
			//VPLMIntegTraceUtil.printStackTrace(context, ex);
		}
		VPLMIntegTraceUtil.trace(context, ">>>>ml3DShapeDrawing::"+ml3DShapeDrawing);
		
		return ml3DShapeDrawing;
	}
	
	/**
	 * Method to get the core materials connected to VPMReference
	 * @param context
	 * @param doVPMReferenceObject
	 * @return MapList
	 * @throws FrameworkException
	 */
	public MapList getConnectedCoreMaterials(Context context, DomainObject doVPMReferenceObject) throws FrameworkException {
		VPLMIntegTraceUtil.trace(context, ">>>> START of getConnectedCoreMaterials method");
		
		MapList mlCoreCoveringMaterials = new MapList();
		try
		{	
			Pattern typePattern = new Pattern(DataConstants.TYPE_DSC_MAT_CNX_CORE_DESIGN);
			typePattern.addPattern(DataConstants.TYPE_DSC_MAT_CNX_COVERING_DESIGN);
			StringList busSelects = new StringList(1);
			busSelects.add(DomainConstants.SELECT_ID);
			StringList relSelects = new StringList(1);
			relSelects.add(DomainRelationship.SELECT_ID);
			mlCoreCoveringMaterials = doVPMReferenceObject.getRelatedObjects(context, 
																	DataConstants.RELATIONSHIP_VPLMREL_PLMCONNECTION_V_OWNER,// relationshipPattern
																	typePattern.getPattern(),// typePattern
																	busSelects,// objectSelects
																	relSelects,// relationshipSelects
																	false,// getTo
																	true,// getFrom
																	(short)1,// recurseToLevel
																	null,// objectWhere
																	null, // relationshipWhere
																	0);// limit
		}
		catch(Exception ex)
		{
			VPLMIntegTraceUtil.trace(context,">>>> Inside catch block of getConnectedCoreMaterials method");
			//VPLMIntegTraceUtil.printStackTrace(context, ex);
		}
		VPLMIntegTraceUtil.trace(context, "<<<< END of getConnectedCoreMaterials method");
		
		return mlCoreCoveringMaterials;
	}
	
	/**
	 * Method to disconnect the relationships
	 * @param context
	 * @param slRelationshipIds
	 * @throws FrameworkException
	 */
	public void disconnectRelationships(Context context,StringList slRelationshipIds) throws FrameworkException {
		VPLMIntegTraceUtil.trace(context, ">>> START of disconnectRelationships method");
		VPLMIntegTraceUtil.trace(context, ">>> slRelationshipIds::"+slRelationshipIds);
		
		if(null!=slRelationshipIds && !slRelationshipIds.isEmpty())
		{
			DomainRelationship.disconnect(context, slRelationshipIds.toArray(new String[slRelationshipIds.size()]));
			VPLMIntegTraceUtil.trace(context, ">>> Disconnected relationships");
		}
	}
	
	/**
	 * Method to connect the objects
	 * @param context
	 * @param slRelationshipIds
	 * @param doVPMReferenceObject
	 * @return
	 * @throws FrameworkException
	 */
	public Map connectObjects(Context context,StringList slObjectIds,DomainObject doVPMReferenceObject,String strRelationshipName) throws FrameworkException {
		VPLMIntegTraceUtil.trace(context, ">>>> START of connectObjects method");
		VPLMIntegTraceUtil.trace(context, ">>>> slObjectIds::"+slObjectIds);
		
		Map newRepInstances=new HashMap();
		if(null!=slObjectIds && !slObjectIds.isEmpty())
		{
			newRepInstances = DomainRelationship.connect(context, doVPMReferenceObject, strRelationshipName, true, 
					slObjectIds.toArray(new String[slObjectIds.size()]));
			VPLMIntegTraceUtil.trace(context, ">>> Connected objects to VPMReference");
		}
		VPLMIntegTraceUtil.trace(context, ">>>> newRepInstances::"+newRepInstances);
	
		return newRepInstances;
	}
	
	/**
	 * Method to update the pngRemovalLock attribute
	 * @param context
	 * @param mpVPMReferenceInfo
	 * @throws MatrixException
	 */
	public void updateRemovalLockAttribute(Context context,Map mpVPMReferenceInfo) throws MatrixException {
		VPLMIntegTraceUtil.trace(context, ">>>>START of updateRemovalLockAttribute method");
		String strVPMReferenceId=(String)mpVPMReferenceInfo.get(DomainConstants.SELECT_ID);
		String strDesignDomain=(String)mpVPMReferenceInfo.get(DataConstants.SELECT_ATTRIBUTE_DESIGN_DOMAIN);
		
		VPLMIntegTraceUtil.trace(context, ">>>>strVPMReferenceId::"+strVPMReferenceId+" strDesignDomain::"+strDesignDomain);
		
		if(UIUtil.isNotNullAndNotEmpty(strVPMReferenceId)) {
			DomainObject doVPMRef=DomainObject.newInstance(context,strVPMReferenceId);
			
			// get all the interfaces of the object
			BusinessInterfaceList busInterfaces = doVPMRef.getBusinessInterfaces(context);
			VPLMIntegTraceUtil.trace(context, ">>>>busInterfaces::"+busInterfaces.toString());
			
			InterfaceManagement interfaceMgmt=new InterfaceManagement(context);
			String strInterfaceName="";
			String strAttributeName="";
			
			if(DataConstants.CONSTANT_DESIGN_FOR_PACKAGING.equalsIgnoreCase(strDesignDomain)){
				strInterfaceName= DataConstants.INTERFACE_PNG_PACKAGING;
				strAttributeName=DataConstants.ATTRIBUTE_PACKAGING_REMOVAL_LOCK;
			}
			else if(DataConstants.CONSTANT_DESIGN_FOR_PRODUCT.equalsIgnoreCase(strDesignDomain)) {
				strInterfaceName= DataConstants.INTERFACE_PNG_PRODUCT;
				strAttributeName=DataConstants.ATTRIBUTE_PRODUCT_REMOVAL_LOCK;
			}
			else if(DataConstants.CONSTANT_DESIGN_FOR_EXPLORATION.equalsIgnoreCase(strDesignDomain)) {
				strInterfaceName=DataConstants.INTERFACE_PNG_EXPLORATION;
				strAttributeName=DataConstants.ATTRIBUTE_EXPLORATION_REMOVAL_LOCK;
			}	
			else if(DataConstants.CONSTANT_DESIGN_FOR_ASSEMBLED.equalsIgnoreCase(strDesignDomain)) {
				strInterfaceName=DataConstants.INTERFACE_PNG_ASSEMBLED;	
				strAttributeName=DataConstants.ATTRIBUTE_ASSEMBLED_REMOVAL_LOCK;
			}
			VPLMIntegTraceUtil.trace(context, ">>>> Interface Name::"+strInterfaceName+" strAttributeName::"+strAttributeName);
			
			if(UIUtil.isNotNullAndNotEmpty(strInterfaceName) && !busInterfaces.toString().contains(strInterfaceName)) {
				interfaceMgmt.addInterface(context, strVPMReferenceId,strInterfaceName);
				VPLMIntegTraceUtil.trace(context, ">>>>added interface "+strInterfaceName);
			}
			
			//update the attribute pngRemovalLock attribute value to True
			if(UIUtil.isNotNullAndNotEmpty(strAttributeName)) {
				doVPMRef.setAttributeValue(context, strAttributeName, DataConstants.CONSTANT_TRUE.toUpperCase());
				VPLMIntegTraceUtil.trace(context, ">>>>Set the pngRemovalLock attribute as True");
			}
		}
	}
	
	/**
	 * Method to revise VPLM object
	 * @param context
	 * @param strObjectId
	 * @return String object id of revised object
	 * @throws MatrixException
	 */
	public String reviseProduct(Context context, String strObjectId) throws MatrixException {
		VPLMIntegTraceUtil.trace(context, ">>>> Inside reviseProduct method");
		DomainObject doObject=DomainObject.newInstance(context,strObjectId);
		
		String nextSequence = doObject.getNextMajorSequence(context);
		VPLMIntegTraceUtil.trace(context, ">>>> next sequence:::"+nextSequence);
		
		String vault = doObject.getInfo(context, DomainConstants.SELECT_VAULT);
		VPLMIntegTraceUtil.trace(context, ">>>> vault:::"+vault);
	
		String physicalId = com.matrixone.jsystem.util.UUID.getNewUUIDHEXString();
		VPLMIntegTraceUtil.trace(context, ">>>> physicalId:::"+physicalId);
		// Major Revise
		BusinessObject nextRev = doObject.revise(context, null, nextSequence, vault, physicalId, true, false);
		return nextRev.getObjectId(context);
	}
	
	/**
	 * Method to get the level of the object
	 * @param strToSideValue
	 * @param strFromSideValue
	 * @return String
	 */
	public String getLevelForVPMReferenceObject(String strToSideValue, String strFromSideValue) {
		String strLevel="";
		if(DataConstants.CONSTANT_FALSE.equalsIgnoreCase(strToSideValue) && DataConstants.CONSTANT_FALSE.equalsIgnoreCase(strFromSideValue))
			strLevel=DataConstants.CONST_STANDALONE;
		else if(DataConstants.CONSTANT_TRUE.equalsIgnoreCase(strToSideValue) && DataConstants.CONSTANT_FALSE.equalsIgnoreCase(strFromSideValue))
			strLevel=DataConstants.CONST_LEAF;
		else if(DataConstants.CONSTANT_TRUE.equalsIgnoreCase(strToSideValue) && DataConstants.CONSTANT_TRUE.equalsIgnoreCase(strFromSideValue))
			strLevel=DataConstants.CONST_INTERMEDIATE;
		else if(DataConstants.CONSTANT_FALSE.equalsIgnoreCase(strToSideValue) && DataConstants.CONSTANT_TRUE.equalsIgnoreCase(strFromSideValue))
			strLevel=DataConstants.CONST_TOP;
		return strLevel;
	}
	
	
	
	/**
	 * Method to generate the where clause for finding objects
	 * @param strLevel
	 * @return where clause
	 */
	public String getWhereClauseForLevel(String strLevel) {
		String strWhere=DataConstants.SELECT_ATTRIBUTE_PNG_CLONE_DERIVED_FROM+"!='' && "+DataConstants.SELECT_ATTRIBUTE_V_DERIVED_FROM+"!='' && ";
		strWhere+=getWhereClauseForVPMReferenceLevel(strLevel);
		return strWhere;
	}
	
	/**
	 * Method to generate the where clause for finding VPMReference objects
	 * @param strLevel
	 * @return where clause
	 */
	public String getWhereClauseForVPMReferenceLevel(String strLevel) {
		String strWhere="";
		if(DataConstants.CONST_STANDALONE.equalsIgnoreCase(strLevel)){
			strWhere="to["+DataConstants.REL_VPM_INSTANCE+"] == "+DataConstants.CONSTANT_FALSE+" && from["+DataConstants.REL_VPM_INSTANCE+"]== "+DataConstants.CONSTANT_FALSE;
		}else if(DataConstants.CONST_LEAF.equalsIgnoreCase(strLevel)) {
			strWhere="to["+DataConstants.REL_VPM_INSTANCE+"] == "+DataConstants.CONSTANT_TRUE+" && from["+DataConstants.REL_VPM_INSTANCE+"]== "+DataConstants.CONSTANT_FALSE;
		}else if(DataConstants.CONST_INTERMEDIATE.equalsIgnoreCase(strLevel)) {
			strWhere="to["+DataConstants.REL_VPM_INSTANCE+"] == "+DataConstants.CONSTANT_TRUE+" && from["+DataConstants.REL_VPM_INSTANCE+"]== "+DataConstants.CONSTANT_TRUE;
		}else if(DataConstants.CONST_TOP.equalsIgnoreCase(strLevel)) {
			strWhere="to["+DataConstants.REL_VPM_INSTANCE+"] == "+DataConstants.CONSTANT_FALSE+" && from["+DataConstants.REL_VPM_INSTANCE+"]== "+DataConstants.CONSTANT_TRUE;
		}
		return strWhere;
	}

	/**
	 * Method to get all the revisions and their information, for given VPMReference object
	 * @param context
	 * @param strName
	 * @return MapList of all revisions
	 * @throws FrameworkException
	 */
	public MapList getAllRevisionsOfVPMReference(Context context, String strName) throws FrameworkException {
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START getAllRevisionsOfVPMReference method");
		
		StringList slObjSelects=new StringList();
		slObjSelects.addElement(DomainConstants.SELECT_ID);
		slObjSelects.add(DomainConstants.SELECT_TYPE);
		slObjSelects.add(DomainConstants.SELECT_NAME);
		slObjSelects.add(DomainConstants.SELECT_REVISION);
		slObjSelects.addElement(DomainConstants.SELECT_OWNER);
		slObjSelects.addElement(DataConstants.SELECT_ATTRIBUTE_V_DESCRIPTION);
		slObjSelects.addElement(DataConstants.SELECT_ATTRIBUTE_DESIGN_DOMAIN);
		slObjSelects.addElement(DataConstants.SELECT_ATTRIBUTE_MFG_MATURITY_STATUS);
		slObjSelects.addElement(DataConstants.SELECT_ATTRIBUTE_PGENTERPRISETYPE);
		slObjSelects.addElement(DataConstants.SELECT_ATTRIBUTE_PNG_CLONE_DERIVED_FROM);
		slObjSelects.addElement(DataConstants.SELECT_ATTRIBUTE_V_USAGE);
		slObjSelects.addElement("to["+DataConstants.REL_VPM_INSTANCE+"]");
		slObjSelects.addElement("from["+DataConstants.REL_VPM_INSTANCE+"]");
		slObjSelects.addElement("to["+DataConstants.REL_PART_SPECIFICATION+"].from.type");
		
		MapList mlRevisionObjects = DomainObject.findObjects(context,
				DataConstants.TYPE_VPMREFERENCE, //typepattern
				strName,  												// namepattern
                DomainConstants.QUERY_WILDCARD,  // revpattern
                DomainConstants.QUERY_WILDCARD,  // owner pattern
                DataConstants.VAULT_VPLM,  // vault pattern
                "", // where exp
                false,   //expandType 
                slObjSelects); //objectselects
		
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>mlRevisionObjects::"+mlRevisionObjects);
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>END getAllRevisionsOfVPMReference method");
		return mlRevisionObjects;
	}
	
	/**
	 * Method to get Related Child or Parent VPMReference according to from and to side arguments
	 * @param mpRevObj
	 * @return boolean
	 * @author PTE2
	 */
	public MapList getRelatedVPMReference(Context context,DomainObject domObj,boolean fromSide ,boolean toSide) throws FrameworkException{
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START getRelatedVPMReference method");
		StringList selectList = new StringList();
		selectList.add(DomainConstants.SELECT_TYPE);
		selectList.add(DomainConstants.SELECT_ID);
		selectList.add(DomainConstants.SELECT_NAME);
		selectList.add("to["+DataConstants.REL_PART_SPECIFICATION+"].from.name");
		selectList.add("to["+DataConstants.REL_PART_SPECIFICATION+"].from.type");
			
		Pattern typePattern = new Pattern(DataConstants.TYPE_VPMREFERENCE);
		Pattern relPattern = new Pattern(DataConstants.REL_VPM_INSTANCE);	
			
		return domObj.getRelatedObjects(context, // context.
							relPattern.getPattern(),// relationship pattern
							typePattern.getPattern(), // type filter.
							selectList, // business selectables.
							null, // relationship selectables.
							toSide, // expand to direction.
							fromSide, // expand from direction.
							(short) 1, // level
							DomainConstants.EMPTY_STRING, // object where clause
							DomainConstants.EMPTY_STRING,// relationship where clause
							0);
	}
	
	/**
	 * Method to release VPMRefererence object
	 * @param context
	 * @param doEvolvedVPMReferenceObject
	 * @return String
	 * @throws FrameworkException 
	 */
	public void releaseVPMReference(Context context, DomainObject doVPMReferenceObject) throws FrameworkException{
		VPLMIntegTraceUtil.trace(context, ">>> START of releaseVPMReference method");
		String strCurrent=doVPMReferenceObject.getInfo(context, DomainConstants.SELECT_CURRENT);
		VPLMIntegTraceUtil.trace(context, ">>> strCurrent:::"+strCurrent);
		
		if(DataConstants.STATE_PRIVATE.equalsIgnoreCase(strCurrent)) {
			processPromoteDemotion(context, doVPMReferenceObject,DataConstants.STATE_PRIVATE, DataConstants.STATE_IN_WORK,"");
			processPromoteDemotion(context, doVPMReferenceObject,DataConstants.STATE_IN_WORK,DataConstants.STATE_FROZEN,DataConstants.STATE_PRIVATE);
			processPromoteDemotion(context, doVPMReferenceObject,DataConstants.STATE_FROZEN,DataConstants.STATE_RELEASED,DataConstants.STATE_IN_WORK);
		}
		else	if(DataConstants.STATE_IN_WORK.equalsIgnoreCase(strCurrent)) {
			processPromoteDemotion(context, doVPMReferenceObject,DataConstants.STATE_IN_WORK,DataConstants.STATE_FROZEN,DataConstants.STATE_PRIVATE);
			processPromoteDemotion(context, doVPMReferenceObject,DataConstants.STATE_FROZEN,DataConstants.STATE_RELEASED,DataConstants.STATE_IN_WORK);
		}
		else	if(DataConstants.STATE_FROZEN.equalsIgnoreCase(strCurrent)) {
			processPromoteDemotion(context, doVPMReferenceObject,DataConstants.STATE_FROZEN,DataConstants.STATE_RELEASED,DataConstants.STATE_IN_WORK);
		}
		VPLMIntegTraceUtil.trace(context, "<<< END of releaseVPMReference method");
	}
	
	/**
	 * Method to process promote demote of object
	 * @param context
	 * @param doObj
	 * @param strCurrentState
	 * @param strNextState
	 * @param strPreviousState
	 * @throws FrameworkException 
	 */
	public void processPromoteDemotion(Context context, DomainObject doObj,	String strCurrentState, String strNextState, String strPreviousState) throws FrameworkException {
		VPLMIntegTraceUtil.trace(context, ">>> START of processPromoteDemotion method");
		boolean bSuperUserContext=false;
		
		try {
			approveSignatures(context, doObj, strCurrentState, strNextState);
			if(UIUtil.isNotNullAndNotEmpty(strPreviousState))
			{
				rejectPreviousSignatures(context, doObj, strCurrentState, strPreviousState);
			}
			if(DataConstants.STATE_FROZEN.equals(strNextState)) {
					ContextUtil.pushContext(context);
					bSuperUserContext=true;
					VPLMIntegTraceUtil.trace(context, ">>> Context pushed before promotion to Frozen or above state::"+context.getUser());
			}
			doObj.setState(context, strNextState);
		} catch (Exception e) {
			VPLMIntegTraceUtil.trace(context, ">>> Exception inside processPromoteDemotion method::"+e.getMessage());
		}finally {
			if(bSuperUserContext)
				ContextUtil.popContext(context);
			VPLMIntegTraceUtil.trace(context, ">>> Context popped after promotion to Frozen or above state::"+context.getUser());
		}
		VPLMIntegTraceUtil.trace(context, "<<< END of processPromoteDemotion method");
	}	
	
	/**
	 * Method to reject the signature 
	 * @param context
	 * @param domObjRef
	 * @param strCurrentState
	 * @param strPreviousState
	 */
	private void rejectPreviousSignatures(Context context, DomainObject doObj,String strCurrentState, String strPreviousState) {
		VPLMIntegTraceUtil.trace(context, ">>> START of rejectPreviousSignatures method");
		SignatureList slSignature = null;
		Signature objSignature = null;
		
			try {
				slSignature = doObj.getSignatures(context,strCurrentState,strPreviousState);													
				for(Iterator<Signature> itr =  slSignature.iterator();itr.hasNext();)
				{
					objSignature = itr.next();
					if(objSignature.isApproved())
					{
						doObj.rejectSignature(context, objSignature, DomainConstants.EMPTY_STRING);
						VPLMIntegTraceUtil.trace(context, ">>> Rejected "+objSignature.getName()+" signature");
					}
				}
			} catch (Exception e) {
				VPLMIntegTraceUtil.trace(context, ">>> Exception inside rejectPreviousSignatures method::"+e.getMessage());
			}	
			VPLMIntegTraceUtil.trace(context, "<<< END of rejectPreviousSignatures method");
	}	
	
	/**
	 * Method to approve signature
	 * @param context
	 * @param doObj
	 * @param strCurrentState
	 * @param strNextState
	 */
	private void approveSignatures(Context context, DomainObject doObj, String strCurrentState, String strNextState) {
		VPLMIntegTraceUtil.trace(context, ">>> START of approveSignatures method");
		Signature objSignature = null;
		SignatureList slSignature = null;
		try {
			slSignature = doObj.getSignatures(context, strCurrentState, strNextState);
			for(Iterator<Signature> itr =  slSignature.iterator(); itr.hasNext();)
			{
				objSignature = itr.next();
				doObj.approveSignature(context, objSignature, DomainConstants.EMPTY_STRING);
				VPLMIntegTraceUtil.trace(context, ">>> Approved "+objSignature.getName()+" signature");
			}
		} catch (Exception e) {
			VPLMIntegTraceUtil.trace(context, ">>> Exception inside approveSignatures method::"+e.getMessage());
		}	
		VPLMIntegTraceUtil.trace(context, "<<< END of approveSignatures method");
	}	
	
	/**
	 * Method to update the filenames on the revised object
	 * @param context
	 * @param strRevisedObjId
	 * @param strSrcPhysicalId
	 * @throws FrameworkException
	 */
	public void updateFilenameOnRevisedObject(Context context, String strRevisedObjId,String strSrcPhysicalId) throws FrameworkException {
		
		VPLMIntegTraceUtil.trace(context, ">>> START of EngineeringItem  updateFilenameOnRevisedObject method");
		
		StringList slSelects=new StringList(3);
		slSelects.add(DomainConstants.SELECT_PHYSICAL_ID);
		slSelects.add(DataConstants.FORMAT_1_FILE_NAME);
		slSelects.add(DataConstants.FORMAT_2_FILE_NAME);
		
		DomainObject doRevisedObj = DomainObject.newInstance(context, strRevisedObjId);
		Map mpRevisedObjInfo=doRevisedObj.getInfo(context,slSelects);
		VPLMIntegTraceUtil.trace(context, ">> mpRevisedObjInfo "+mpRevisedObjInfo);
		boolean hasFormat2FileName=false;
		if(slSelects.toString().contains(DataConstants.FORMAT_2_FILE_NAME))
			hasFormat2FileName=true;
		
		VPLMIntegTraceUtil.trace(context, ">> hasFormat2FileName::"+hasFormat2FileName);
		String strNewPhysicalID=(String)mpRevisedObjInfo.get(DomainConstants.SELECT_PHYSICAL_ID); //code changed for 22x upgrade
		// replace the old xcad physical id with the new 3dshape physicalid on the filename.
		//format[1].file.name = A6956133D8144776966E162C144E2246_''.1=30AprilTestPart3
		//format[2].file.name = A6956133D8144776966E162C144E2246_''.2=Visu_0_8f18-52d0-6331-91b0
		
		String strFormatOldFileName =(String)mpRevisedObjInfo.get(DataConstants.FORMAT_1_FILE_NAME);
		
		VPLMIntegTraceUtil.trace(context, ">>strFormatOldFileName::"+strFormatOldFileName);
		VPLMIntegTraceUtil.trace(context, ">>strSrcPhysicalId::"+strSrcPhysicalId);
		VPLMIntegTraceUtil.trace(context, ">>strNewPhysicalID::"+strNewPhysicalID);
		
		String strFormatNewFileName = strFormatOldFileName.replace(strSrcPhysicalId, strNewPhysicalID);

		// mod bus 3DShape 00000627986 002.001 rename format 1
		// propagaterename file "FC4C9E624A424B02A8AD561AD36FD9FF_''.1=Part41_Rupesh"
		// "1DF5C1A36D2E43B19FBF8C5E4A28B4B5_''.1=Part41_Rupesh";
		VPLMIntegTraceUtil.trace(context, ">>strFormatNewFileName for format 1 "+strFormatNewFileName);
		String strMQLString = "mod bus $1 rename format $2 propagaterename file $3 $4";
		MqlUtil.mqlCommand(context, strMQLString, strRevisedObjId, DataConstants.CONSTANT_ONE,strFormatOldFileName,strFormatNewFileName);
		
		if(hasFormat2FileName) {
			strFormatOldFileName =(String)mpRevisedObjInfo.get(DataConstants.FORMAT_2_FILE_NAME);
			strFormatNewFileName = strFormatOldFileName.replace(strSrcPhysicalId, strNewPhysicalID);
			VPLMIntegTraceUtil.trace(context, ">>strFormatNewFileName for format 2  "+strFormatNewFileName);
			MqlUtil.mqlCommand(context, strMQLString, strRevisedObjId, DataConstants.CONSTANT_TWO,strFormatOldFileName,strFormatNewFileName);
		}
		VPLMIntegTraceUtil.trace(context, "<<< END of updateFilenameOnRevisedObject method");
	}
	
	/**
	 * This method checks whether the VPMReference object is connected to XCAD object or not.
	 * @param Context
	 * @param object id of the VPMReference object
	 * @return boolean
	 * @throws MatrixException 
	 */
	public boolean checkVPMReferenceObjectForXCADType(Context context, String strCADObjId) throws MatrixException {
		VPLMIntegTraceUtil.trace(context, ">> START of EngineeringItem: checkVPMReferenceObjectForXCADType method");
		boolean bIsXCADType = false;
		DomainObject doCADObj = DomainObject.newInstance(context, strCADObjId);

		// get all the interfaces of the VPMReference object
		BusinessInterfaceList busInterfaces = doCADObj.getBusinessInterfaces(context);
		VPLMIntegTraceUtil.trace(context, ">> checkVPMReferenceObjectForXCADType busInterfaces::"+busInterfaces.toString());
		
		if(busInterfaces.toString().contains(DataConstants.INTERFACE_XCADITEMEXTENSION))
			bIsXCADType = true;

		VPLMIntegTraceUtil.trace(context, "<< END of EngineeringItem: checkVPMReferenceObjectForXCADType method bIsXCADType::"+bIsXCADType);
		return bIsXCADType;
	}
}

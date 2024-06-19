/*
 * Added by APOLLO Team
 * For CATIA Automation Web Services
 * 
 * CUSTOM REVISION:
 * https://localhost/3dspace/resources/getDesignToolData/fetchData/revisiongenerate?BaseProductName=<BASE_VPMRef_NAME>&BaseProductRev=<BASE_VPMRef_REV>&NewProductName=<TARGET_VPMRef_NAME>&NewProductRev=<TARGET_VPMRef_REV>
 * 
 * UPDATE MODEL:
 *  https://localhost/3dspace/resources/getDesignToolData/fetchData/updateModel?VPMRefModName|VPMRefModRev&VPMRefCopyName|VPMRefCopyRev
 *  
 *  QUERY APP / VPMReference:
 *  https://localhost/3dspace/resources/getDesignToolData/fetchData/queryPart?state=<MaturityState>&part=<Free text that can include PartName or APP number>
 * 
 */

package com.png.apollo.designtool.getData;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dassault_systemes.lifecycle.implementations.duplicate.OperationResult;
import com.dassault_systemes.lifecycle.implementations.duplicate.interfaces.IResultItem;
import com.dassault_systemes.lifecycle.services.DefaultNLSService;
import com.dassault_systemes.rest.service.lifecycle.DuplicateResource;
import com.matrixone.apps.cpn.CPNCommonConstants;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PersonUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.servlet.Framework;
import com.png.apollo.pgApolloCommonUtil;
import com.png.apollo.pgApolloConstants;

import matrix.db.BusinessObject;
import matrix.db.BusinessObjectWithSelect;
import matrix.db.BusinessObjectWithSelectList;
import matrix.db.JPO;
import matrix.db.PathWithSelect;
import matrix.db.PathWithSelectList;
import matrix.util.MatrixException;
import matrix.util.Pattern;
import matrix.util.StringList;

@Path("/fetchData")
public class ExtractDataForDesignTool {
	 private static final Logger loggerWS = LoggerFactory.getLogger("APOLLOWS");
	 private static final org.apache.log4j.Logger loggerApolloTrace = org.apache.log4j.Logger.getLogger(ExtractDataForDesignTool.class);	
	@GET
	@Path("/revisiongenerate")
	public Response reviseBaseVPMReference(@Context HttpServletRequest req) throws Exception {
	
		matrix.db.Context context = null;
		StringBuffer sbReturnMessages = new StringBuffer();
		BusinessObject baseProductBO = null;
		BusinessObject newProductBO = null;
		
		boolean bBaseProductOpen = false;
		boolean bNewProductOpen = false;
		boolean isContextPushed = false;
		boolean bIsTransactionStarted = false;

		
		try {			
			loggerWS.debug("******************************************************************************");
			loggerWS.debug("Path : /fetchData/revisiongenerate");
			loggerWS.debug("Method: ExtractDataForDesignTool : reviseBaseVPMReference");			
			loggerWS.debug("\n\n New Revision creation started : {}" ,new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
			String strBaseProdName = DomainConstants.EMPTY_STRING;
			String strBaseProdRev = DomainConstants.EMPTY_STRING;
			String strNewProdName = DomainConstants.EMPTY_STRING;
			String strNewProdRev = DomainConstants.EMPTY_STRING;
			String strBaseProdName_BaseProd = DomainConstants.EMPTY_STRING;
			String strBaseProdRev_BaseProd = DomainConstants.EMPTY_STRING;
			String strNewProdName_NewProd =  DomainConstants.EMPTY_STRING;
			String strNewProdRev_NewProd = DomainConstants.EMPTY_STRING;
			String strNewProdRev_ModelType = DomainConstants.EMPTY_STRING;
			//Apollo 2018x.3 Defect 32796 : Revise Automation Webservice is not copying Drawing object properly - Start
			StringList sl3DShapeDrawing_TargetProd = new StringList();
			StringList sl3DShapeDrawing_RelIDTargetProd = new StringList();
			Map map3DShapeDrawing = new HashMap();
			String str3DShapeDrawingObjectId = DomainConstants.EMPTY_STRING;
			String str3DShapeDrawingRelId = DomainConstants.EMPTY_STRING;
			//Apollo 2018x.3 Defect 32796 : Revise Automation Webservice is not copying Drawing object properly - End
			
			String[] URLParam = new String[20];
			
			String strNewRevProductName = DomainConstants.EMPTY_STRING;
			String strNewRevProductRev = DomainConstants.EMPTY_STRING;
						
			// Get the user context
			if (Framework.isLoggedIn((HttpServletRequest) req)) {
				context = Framework.getContext((HttpSession) req.getSession(false));
			}
			loggerWS.debug("context  : {}" ,context.getUser());
			String queryString = req.getQueryString();
			queryString =  java.net.URLDecoder.decode(queryString,"UTF-8");
			loggerWS.debug("\n\nQueryString  : {}" ,queryString);			
			
			if(queryString.contains("%26")){
				queryString = queryString.replace("%26", "&");
			}
			
			
			if (UIUtil.isNotNullAndNotEmpty(queryString)) {
				URLParam = queryString.split("&");
				// Put the two prods in a map.Get the Query Type and Root Product Name from URL/Query String
				if (URLParam.length > 0) {

					strBaseProdName_BaseProd = URLParam[0];
					strBaseProdRev_BaseProd= URLParam[1];
					strNewProdName_NewProd = URLParam[2];
					strNewProdRev_NewProd = URLParam[3];
					
					String[] strQueryTypeInfoName_BaseProd = strBaseProdName_BaseProd.split("=");
					strBaseProdName = strQueryTypeInfoName_BaseProd[1];
					loggerWS.debug("\n\nstrBaseProdName----------{}" ,strBaseProdName);
					
					String[] strQueryTypeInfoRevision_BaseProd = strBaseProdRev_BaseProd.split("=");
					strBaseProdRev = strQueryTypeInfoRevision_BaseProd[1];
					loggerWS.debug("\n\nstrBaseProdRev----------{}" ,strBaseProdRev);
															
					String[] strQueryTypeInfoName_NewProd = strNewProdName_NewProd.split("=");
					strNewProdName = strQueryTypeInfoName_NewProd[1];
					loggerWS.debug("\n\nstrNewProdName---------{}" ,strNewProdName);
					
					String[] strQueryTypeInfoRevision_NewProd = strNewProdRev_NewProd.split("=");
					strNewProdRev = strQueryTypeInfoRevision_NewProd[1];
					loggerWS.debug("\n\nstrNewProdRev---------{}" ,strNewProdRev);
															
					StringList slObjSelects = new StringList(2);
					slObjSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PGLPDMODELTYPE);
					
					StringList selectables = new StringList(2);
					selectables.add(DomainConstants.SELECT_NAME);
					selectables.add(DomainConstants.SELECT_REVISION);
					
					baseProductBO = new BusinessObject(pgApolloConstants.TYPE_VPMREFERENCE, strBaseProdName,strBaseProdRev, pgApolloConstants.VAULT_VPLM);
					newProductBO = new BusinessObject(pgApolloConstants.TYPE_VPMREFERENCE, strNewProdName,strNewProdRev, pgApolloConstants.VAULT_VPLM);
					
					boolean bBaseProdExists = baseProductBO.exists(context);
					boolean bNewProdExists = newProductBO.exists(context);
					loggerWS.debug("\n\nbBaseProdExists : {}" ,bBaseProdExists);
					loggerWS.debug("\n\nbNewProdExists: {}" ,bNewProdExists);
					System.out.println("before If---------");
					if (bBaseProdExists && bNewProdExists) {
						baseProductBO.open(context);
						bBaseProductOpen = true;
						newProductBO.open(context);
						bNewProductOpen = true;
						
						// Get New VPMReference Info
						DomainObject domTargetProd = DomainObject.newInstance(context, newProductBO);
						//Apollo 2018x.3 Defect 32796 : Revise Automation Webservice is not copying Drawing object properly - Start
						MapList ml3DShapeDrawing = getConnected3DShapeDrawingObject(context, domTargetProd);
						Map mapRepInstances = new HashMap();
						String str3DShapeDrawingName;
						if(null!=ml3DShapeDrawing && !ml3DShapeDrawing.isEmpty())
						{
							for(int i=0;i<ml3DShapeDrawing.size();i++)
							{
								map3DShapeDrawing = new HashMap();
								map3DShapeDrawing = (Map)ml3DShapeDrawing.get(i);
								str3DShapeDrawingObjectId = (String)map3DShapeDrawing.get(DomainConstants.SELECT_ID);
								str3DShapeDrawingRelId = (String)map3DShapeDrawing.get(DomainRelationship.SELECT_ID);
								str3DShapeDrawingName = (String)map3DShapeDrawing.get(DomainConstants.SELECT_NAME);
								mapRepInstances.put(str3DShapeDrawingObjectId, str3DShapeDrawingName);
								sl3DShapeDrawing_TargetProd.add(str3DShapeDrawingObjectId);
								sl3DShapeDrawing_RelIDTargetProd.add(str3DShapeDrawingRelId);
							}
							loggerWS.debug("\nsl3DShapeDrawing_TargetProd-----------{}" ,sl3DShapeDrawing_TargetProd);
							loggerWS.debug("\nsl3DShapeDrawing_RelIDTargetProd-----------{}" ,sl3DShapeDrawing_RelIDTargetProd);
						}
						//Apollo 2018x.3 Defect 32796 : Revise Automation Webservice is not copying Drawing object properly - End					
						Map mpRevTargetProdDetails = domTargetProd.getInfo(context, slObjSelects);
						strNewProdRev_ModelType = (String) mpRevTargetProdDetails.get(pgApolloConstants.SELECT_ATTRIBUTE_PGLPDMODELTYPE);
						
						loggerWS.debug("\n\nmpRevTargetProdDetails-----------{}" ,mpRevTargetProdDetails);
						DomainObject domBaseProdObj = DomainObject.newInstance(context, baseProductBO);
						String strBaseProductCurrent = domBaseProdObj.getInfo(context, DomainConstants.SELECT_CURRENT);
						if(pgApolloConstants.STATE_SHARED.equals(strBaseProductCurrent))
						{
							ContextUtil.startTransaction(context, true);
							bIsTransactionStarted = true;
							loggerWS.debug("\n\nBefore Revise :---");
							Map mapRevisedProduct = reviseBaseProduct(context, domBaseProdObj);
							loggerWS.debug("\n\nmapRevisedProduct-----------{}" ,mapRevisedProduct);	
							String strRevisedBaseProd = (String)mapRevisedProduct.get(pgApolloConstants.KEY_PHYSICALPRODUCT);
							loggerWS.debug("\n\nstrRevisedBaseProd-----------{}" ,strRevisedBaseProd);	
							if(mapRevisedProduct.containsKey(pgApolloConstants.STR_ERROR))
							{
								String sErrorMessage =  (String)mapRevisedProduct.get(pgApolloConstants.STR_ERROR);
								loggerWS.debug("\n\n sErrorMessage-----------{}" ,sErrorMessage);	
								// Put it as Error: and error msg
								sbReturnMessages.append(pgApolloConstants.STR_ERROR);
								sbReturnMessages.append(pgApolloConstants.CONSTANT_STRING_COLON);
								sbReturnMessages.append(pgApolloConstants.STR_REVISION_VPMREF_FAILED);
								sbReturnMessages.append(pgApolloConstants.CONSTANT_STRING_COLON);
								sbReturnMessages.append(sErrorMessage);

							}
							else if(UIUtil.isNotNullAndNotEmpty(strRevisedBaseProd))
							{
								DomainObject domRevisedBaseProd = DomainObject.newInstance(context, strRevisedBaseProd);
								if(domRevisedBaseProd.exists(context))
								{
									ContextUtil.pushContext(context, pgApolloConstants.PERSON_USER_AGENT, null, context.getVault().getName());
									isContextPushed = true;
									
									/* 
									//Original
									disconnect3DShapeDrawings(context, domRevisedBaseProd);									
									disconnectCoreMaterials(context, domRevisedBaseProd); //PROD Issue Changes			
									connectDisconnect3DShapeDrawings(context, strNewProdRev_ModelType,sl3DShapeDrawing_TargetProd, sl3DShapeDrawing_RelIDTargetProd,domRevisedBaseProd, mapRepInstances);
									updateSemanticRelationsOnDrawing(context,domRevisedBaseProd, domTargetProd); //PROD Issue Changes
									disconnectCoreMaterials(context,domTargetProd); //PROD Issue Changes
									*/
									
									
									//Test Scenario 1
									disconnectCoreMaterials(context, domRevisedBaseProd); //PROD Issue Changes	
									disconnect3DShapeDrawings(context, domRevisedBaseProd);
									connectDisconnect3DShapeDrawings(context, strNewProdRev_ModelType,sl3DShapeDrawing_TargetProd, sl3DShapeDrawing_RelIDTargetProd,domRevisedBaseProd, mapRepInstances);
									updateSemanticRelationsOnDrawing(context,domRevisedBaseProd, domTargetProd); //PROD Issue Changes
									disconnectCoreMaterials(context,domTargetProd); //PROD Issue Changes									
									/*
									//Scenario 2
									disconnect3DShapeDrawings(context, domRevisedBaseProd);
									connectDisconnect3DShapeDrawings(context, strNewProdRev_ModelType,sl3DShapeDrawing_TargetProd, sl3DShapeDrawing_RelIDTargetProd,domRevisedBaseProd, mapRepInstances);
									disconnectCoreMaterials(context, domRevisedBaseProd); //PROD Issue Changes	
									updateSemanticRelationsOnDrawing(context,domRevisedBaseProd, domTargetProd); //PROD Issue Changes
									disconnectCoreMaterials(context,domTargetProd); //PROD Issue Changes
									*/
									ContextUtil.popContext(context);
									isContextPushed = false;
									
									//Disconnect 3DShape and Drawing from Target/Dummy Product
									if(null!=sl3DShapeDrawing_RelIDTargetProd && !sl3DShapeDrawing_RelIDTargetProd.isEmpty())
									{
										DomainRelationship.disconnect(context, sl3DShapeDrawing_RelIDTargetProd.toArray(new String[sl3DShapeDrawing_RelIDTargetProd.size()]));
									}
									
									//Dummy product should be deleted
									domTargetProd.deleteObject(context);	
									loggerWS.debug("\n Dummy VPMRef Delete Sucessful");

									Map mpNewRevProdDetails = domRevisedBaseProd.getInfo(context, selectables);
									strNewRevProductName = (String)mpNewRevProdDetails.get(DomainConstants.SELECT_NAME);
									loggerWS.debug("\n\nstrNewRevProductName-----------{}" ,strNewRevProductName);
									strNewRevProductRev = (String)mpNewRevProdDetails.get(DomainConstants.SELECT_REVISION);
									loggerWS.debug("\n\nstrNewRevProductRev----------{}" ,strNewRevProductRev);

									sbReturnMessages.append(pgApolloConstants.STR_SUCCESS);
									sbReturnMessages.append(pgApolloConstants.CONSTANT_STRING_COLON);
									sbReturnMessages.append(strNewRevProductName);
									sbReturnMessages.append(pgApolloConstants.CONSTANT_STRING_COLON);
									sbReturnMessages.append(strNewRevProductRev);
								}
								else
								{
									// Put it as Error: and error msg
									sbReturnMessages.append(pgApolloConstants.STR_ERROR);
									sbReturnMessages.append(pgApolloConstants.CONSTANT_STRING_COLON);
									sbReturnMessages.append(pgApolloConstants.STR_REVISION_VPMREF_FAILED);
								}
							}							
							else
							{
								// Put it as Error: and error msg
								sbReturnMessages.append(pgApolloConstants.STR_ERROR);
								sbReturnMessages.append(pgApolloConstants.CONSTANT_STRING_COLON);
								sbReturnMessages.append(pgApolloConstants.STR_REVISION_VPMREF_FAILED);
							}
							System.out.println("End of method----------");
						}
						else
						{
							sbReturnMessages.append(pgApolloConstants.STR_ERROR);
							sbReturnMessages.append(pgApolloConstants.CONSTANT_STRING_COLON);
							sbReturnMessages.append(pgApolloConstants.STR_BASE_NOT_RELEASED);
						}
					} else {
						// Put it as Error: and error msg
						sbReturnMessages.append(pgApolloConstants.STR_ERROR);
						sbReturnMessages.append(pgApolloConstants.CONSTANT_STRING_COLON);
						sbReturnMessages.append(pgApolloConstants.STR_REVISE_ERRORMESSAGE);
					}
				}
			}
		} catch (Exception e) {
			loggerApolloTrace.error(e.getMessage(), e);
			if(bIsTransactionStarted)
			{
				ContextUtil.abortTransaction(context);
				bIsTransactionStarted = false;
			}
			return Response.serverError().entity("Error:" + e.toString()).build();
		} finally {
			
			if(isContextPushed)
			{
				ContextUtil.popContext(context);
			}
			if(bBaseProductOpen){
				baseProductBO.close(context);
			}
			if(bNewProductOpen){
				newProductBO.close(context);
			}
			if(bIsTransactionStarted)
			{
				ContextUtil.commitTransaction(context);	
			}	
			loggerWS.debug("\n\nRevision Creation Ended-----------------");
			loggerWS.debug("\n******************************************************************************");
		}
		return Response.status(200).entity(sbReturnMessages.toString()).build();
	}

	/** Method to update Semantic Relation data on Drawing Object PROD Issue Changes
	 * @param context
	 * @param domRevisedBaseProd	  
	 * @param domTargetProd 
	 * @throws MatrixException 
	 */
	public void updateSemanticRelationsOnDrawing(matrix.db.Context context, DomainObject domRevisedBaseProd, DomainObject domTargetProd) throws MatrixException {
		//Find Drawing Object
		StringList slDrawings = getDrawingObjects(context,domRevisedBaseProd);
		
		//Get Path Values
		StringList selectOnCnx = new StringList();
		selectOnCnx.add(pgApolloConstants.SELECT_SEMANTIC_PATH_ID);
		List<String> listPathIds;
		List<String> listPaths = new ArrayList<>();
		List<String> listPathElementPhysicalId;
		List<String> listPathElementRelevantId;
		List<String> listPathElementKind;
		
		boolean isRelavant = false;							  
		StringList selectOnPath = new StringList();
		selectOnPath.add(pgApolloConstants.SELECT_ELEMENT_PHYSICAL_ID);
		selectOnPath.add(pgApolloConstants.SELECT_ELEMENT_KIND);
		selectOnPath.add(pgApolloConstants.SELECT_ELEMENT_RELEVANT);
		
		Map mpNew3DShapeConnectionInfo = null;
		
		StringList slProductSelects = new StringList(pgApolloConstants.SELECT_PHYSICAL_ID);
		slProductSelects.add(pgApolloConstants.SELECT_LOGICAL_ID);
		slProductSelects.add(pgApolloConstants.SELECT_MAJOR_ID);
		Map mpNewBaseProductRevisionInfo = domRevisedBaseProd.getInfo(context, slProductSelects);
		
		BusinessObjectWithSelectList queryCnx = BusinessObject.getSelectBusinessObjectData (context, slDrawings.toArray(new String[slDrawings.size()]), selectOnCnx);
		MapList ml3DShapeInfoList  = get3DshapeConnectionDetail(context,domTargetProd,domRevisedBaseProd);
		loggerWS.debug("\n ml3DShapeInfoList : {}" ,ml3DShapeInfoList);
		Iterator iterqueryCnx = queryCnx.iterator();
		BusinessObjectWithSelect cnx = null;
		while(iterqueryCnx.hasNext()) {
			cnx = (BusinessObjectWithSelect) iterqueryCnx.next();
			listPathIds =  cnx.getSelectDataList(pgApolloConstants.SELECT_SEMANTIC_PATH_ID);
			if (null != listPathIds)
		    {
		    	listPaths.addAll(listPathIds);
		    }
		}
		if(!listPaths.isEmpty())
		{
			PathWithSelectList pathWithSelect= matrix.db.Path.getSelectPathData(context, listPaths.toArray(new String[]{}),selectOnPath);
			matrix.db.Path.Element[] paramArry;
			int[] intIndex;
			loggerWS.debug("\n pathWithSelect  : {}" ,pathWithSelect);
			for (PathWithSelect  path : pathWithSelect ) 
			{
				isRelavant = false;
				listPathElementKind =  path.getSelectDataList("element[0].kind");
				listPathElementRelevantId =  path.getSelectDataList("element[0].relevant");
				if(!listPathElementRelevantId.isEmpty() && listPathElementRelevantId.get(0).equalsIgnoreCase(pgApolloConstants.STR_TRUE_FLAG)) {
						isRelavant  = true;
				}
				if(null !=listPathElementKind && listPathElementKind.contains("businessobject")) {
					//Update new VPMReference Details
					paramArry = new  matrix.db.Path.Element[1];
					paramArry[0] = new matrix.db.Path.Element(0, pgApolloConstants.TYPE_VPMREFERENCE, (String)mpNewBaseProductRevisionInfo.get(pgApolloConstants.SELECT_PHYSICAL_ID), (String)mpNewBaseProductRevisionInfo.get(pgApolloConstants.SELECT_MAJOR_ID), (String)mpNewBaseProductRevisionInfo.get(pgApolloConstants.SELECT_LOGICAL_ID), "00000000000000000000000000000000", isRelavant);
					intIndex = new int[1];
					path.modifyPath(context, intIndex, paramArry);
					loggerWS.debug("\n Modify Path BusinessObject {}" ,(String)mpNewBaseProductRevisionInfo.get(pgApolloConstants.SELECT_PHYSICAL_ID));
				}else if(null !=listPathElementKind && listPathElementKind.contains("connection")) {
					listPathElementPhysicalId =  path.getSelectDataList("element[1].physicalid");

					if(null == listPathElementPhysicalId || (null != listPathElementPhysicalId && listPathElementPhysicalId.isEmpty())) {
						listPathElementPhysicalId = path.getSelectDataList("element[0].physicalid");
						//Element 0 Case
						if(!listPathElementPhysicalId.isEmpty() && listPathElementPhysicalId.size() == 1) {
							mpNew3DShapeConnectionInfo = getMapFromBaseProduct(ml3DShapeInfoList,listPathElementPhysicalId.get(0),listPathElementPhysicalId.get(0));
							if(!mpNew3DShapeConnectionInfo.isEmpty()) {
								//Update for 3DShape connection IDs
								paramArry = new  matrix.db.Path.Element[1];
								paramArry[0] = new matrix.db.Path.Element(0, pgApolloConstants.RELATIONSHIP_VPMRepInstance, (String)mpNew3DShapeConnectionInfo.get(pgApolloConstants.SELECT_PHYSICAL_ID), (String)mpNew3DShapeConnectionInfo.get(pgApolloConstants.SELECT_MAJOR_ID), (String)mpNew3DShapeConnectionInfo.get(pgApolloConstants.SELECT_LOGICAL_ID), "00000000000000000000000000000000", isRelavant);
								intIndex = new int[1];
								path.modifyPath(context, intIndex, paramArry);
								loggerWS.debug("\n Modify Path Element 0  Connection : {}" ,(String)mpNew3DShapeConnectionInfo.get(pgApolloConstants.SELECT_PHYSICAL_ID));
							}
							
						}
					}else if(null != listPathElementPhysicalId && !listPathElementPhysicalId.isEmpty() && listPathElementPhysicalId.size() == 1) {
						//Element 1 Case
							mpNew3DShapeConnectionInfo = getMapFromBaseProduct(ml3DShapeInfoList,listPathElementPhysicalId.get(0),pgApolloConstants.STR_SELECT_TO_PHYSICALID);
							if(!mpNew3DShapeConnectionInfo.isEmpty()) {
								//Update for 3DShape connection IDs
								paramArry = new  matrix.db.Path.Element[1];
								paramArry[0] = new matrix.db.Path.Element(0, pgApolloConstants.RELATIONSHIP_VPMRepInstance, (String)mpNew3DShapeConnectionInfo.get(pgApolloConstants.SELECT_PHYSICAL_ID), (String)mpNew3DShapeConnectionInfo.get(pgApolloConstants.SELECT_LOGICAL_ID), (String)mpNew3DShapeConnectionInfo.get(pgApolloConstants.SELECT_MAJOR_ID), "00000000000000000000000000000000", isRelavant);
								intIndex = new int[1];
								path.modifyPath(context, intIndex, paramArry);
								loggerWS.debug("\n Modify Path Element 1 Connection : {}" ,(String)mpNew3DShapeConnectionInfo.get(pgApolloConstants.SELECT_PHYSICAL_ID));
							}
					}
				}
			}	
		}
	}


	/** Method to found equivalent Map from multiple 3dshape connection details Maplist PROD Issue Changes
	 * @param ml3dShapeInfoList
	 * @param strCurrentPhysicalID
	 * @param strSelectable
	 * @return
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


	/**Metod to get Old and New 3DShape Connection Attribute Detail PROD Issue Changes
	 * @param context
	 * @param domBaseProdObj
	 * @param domRevisedProdObj
	 * @return
	 * @throws FrameworkException
	 */
	private MapList get3DshapeConnectionDetail(matrix.db.Context context, DomainObject domTargetProd,DomainObject domRevisedProdObj) throws FrameworkException {
		MapList mlReturnList = new MapList();
		StringList busSelects = new StringList(DomainConstants.SELECT_ID);
		StringList relSelects  = new StringList(DomainRelationship.SELECT_ID);
		relSelects.add(pgApolloConstants.SELECT_PHYSICAL_ID);
		relSelects.add(pgApolloConstants.SELECT_LOGICAL_ID);
		relSelects.add(pgApolloConstants.SELECT_MAJOR_ID);
		relSelects.add(pgApolloConstants.STR_SELECT_TO_PHYSICALID);
		
		Map mpBase;
		Map mpRevised;
		String strBase3DShapePhysicalID;
		String strRevised3DShapePhysicalID;
		String strBaseConnection3DShapePhysicalID;
		MapList mlTargetInfoList = domTargetProd.getRelatedObjects(context, 
				pgApolloConstants.RELATIONSHIP_VPMRepInstance,// relationshipPattern
				pgApolloConstants.TYPE_3DSHAPE,// typePattern
				busSelects,// objectSelects
				relSelects,// relationshipSelects
				false,// getTo
				true,// getFrom
				(short)1,// recurseToLevel
				null,// objectWhere
				null, // relationshipWhere
				0);// limit
		MapList mlRevisedObjectData = domRevisedProdObj.getRelatedObjects(context, 
				pgApolloConstants.RELATIONSHIP_VPMRepInstance,// relationshipPattern
				pgApolloConstants.TYPE_3DSHAPE,// typePattern
				busSelects,// objectSelects
				relSelects,// relationshipSelects
				false,// getTo
				true,// getFrom
				(short)1,// recurseToLevel
				null,// objectWhere
				null, // relationshipWhere
				0);// limit
		if(!mlTargetInfoList.isEmpty()) {
			for(int i = 0 ; i < mlTargetInfoList.size(); i++) {
				mpBase = (Map)mlTargetInfoList.get(i);
				strBase3DShapePhysicalID = (String)mpBase.get(pgApolloConstants.STR_SELECT_TO_PHYSICALID);
				strBaseConnection3DShapePhysicalID = (String)mpBase.get(pgApolloConstants.SELECT_PHYSICAL_ID);
				for(int j = 0; j < mlRevisedObjectData.size();j++) {
					mpRevised = (Map)mlRevisedObjectData.get(j);
					strRevised3DShapePhysicalID = (String)mpRevised.get(pgApolloConstants.STR_SELECT_TO_PHYSICALID);
					if(strBase3DShapePhysicalID.equals(strRevised3DShapePhysicalID)) {
						mpRevised.put(strBaseConnection3DShapePhysicalID, strBaseConnection3DShapePhysicalID);
						mlReturnList.add(mpRevised);
					}
				}
			}
			loggerWS.debug("\n get3DshapeConnectionDetail-----------{}" ,mlReturnList);
		}
		return mlReturnList;
	}


	/** Method to get Drawing Object from VPMReference  PROD Issue Changes
	 * @param context
	 * @param domRevisedBaseProd
	 * @return
	 * @throws FrameworkException
	 */
	public StringList getDrawingObjects(matrix.db.Context context, DomainObject domRevisedBaseProd) throws FrameworkException {
		StringList slDrawingIDs = new StringList();
		MapList mlVPMRepInstanceData;
		Pattern typePattern = new Pattern(pgApolloConstants.TYPE_DRAWING);
		StringList busSelects = new StringList();
		busSelects.add(DomainConstants.SELECT_ID);
		StringList relSelects = new StringList();
		relSelects.add(DomainRelationship.SELECT_ID);
		Map mpDrawing = null;
		
		mlVPMRepInstanceData = domRevisedBaseProd.getRelatedObjects(context, 
				pgApolloConstants.RELATIONSHIP_VPMRepInstance,// relationshipPattern
				typePattern.getPattern(),// typePattern
				busSelects,// objectSelects
				relSelects,// relationshipSelects
				false,// getTo
				true,// getFrom
				(short)1,// recurseToLevel
				null,// objectWhere
				null, // relationshipWhere
				0);// limit
		if(!mlVPMRepInstanceData.isEmpty()) {
			for(int i = 0 ; i < mlVPMRepInstanceData.size(); i++) {
				mpDrawing = (Map)mlVPMRepInstanceData.get(i);
				slDrawingIDs.add((String)mpDrawing.get(DomainConstants.SELECT_ID));
			}
			loggerWS.debug("\n getDrawingObjects-----------{}" ,slDrawingIDs);
		}
		return slDrawingIDs;
	}


	/**
	 * Method to disconnect 3DShape and Drawing from Base VPMRef
	 * @param context
	 * @param EBOMUpdateLog
	 * @param domBaseProdObj
	 * @throws Exception
	 */
	private void disconnect3DShapeDrawings(matrix.db.Context context, DomainObject domBaseProdObj)throws Exception {		
		StringList sl3DShapeDrawing_TargetProd = new StringList();
		StringList sl3DShapeDrawing_RelIDTargetProd = new StringList();
		Map map3DShapeDrawing = new HashMap();
		String str3DShapeDrawingObjectId = DomainConstants.EMPTY_STRING;
		String str3DShapeDrawingRelId = DomainConstants.EMPTY_STRING;
		MapList ml3DShapeDrawing = getConnected3DShapeDrawingObject(context, domBaseProdObj);
		if(null!=ml3DShapeDrawing && !ml3DShapeDrawing.isEmpty())
		{
			for(int i=0;i<ml3DShapeDrawing.size();i++)
			{
				map3DShapeDrawing = new HashMap();
				map3DShapeDrawing = (Map)ml3DShapeDrawing.get(i);
				str3DShapeDrawingObjectId = (String)map3DShapeDrawing.get(DomainConstants.SELECT_ID);
				str3DShapeDrawingRelId = (String)map3DShapeDrawing.get(DomainRelationship.SELECT_ID);
				sl3DShapeDrawing_TargetProd.add(str3DShapeDrawingObjectId);
				sl3DShapeDrawing_RelIDTargetProd.add(str3DShapeDrawingRelId);
			}
			if(null!=sl3DShapeDrawing_RelIDTargetProd && !sl3DShapeDrawing_RelIDTargetProd.isEmpty())
			{
				DomainRelationship.disconnect(context, sl3DShapeDrawing_RelIDTargetProd.toArray(new String[sl3DShapeDrawing_RelIDTargetProd.size()]));
				//Following Line will delete 3DShape and Drawings
				//Check if 3DShape or Drawings are not connected to any other VPMReference
				pgApolloCommonUtil.deleteRepresentations(context, sl3DShapeDrawing_TargetProd);
				loggerWS.debug("\n\n3Dshape and Drawing objects disconnected from Revised Base Product and deleted successfully -----------");
			}
			loggerWS.debug("\nsl3DShapeDrawing_TargetProd-----------{}" ,sl3DShapeDrawing_TargetProd);
			loggerWS.debug("\nsl3DShapeDrawing_RelIDTargetProd-----------{}" ,sl3DShapeDrawing_RelIDTargetProd);
		}
	}

	/** Method to disconnect Core Material connected to Newly Revised VPMReference PROD Issue Changes
	 * @param context
	 * @param domProdObject
	 * @throws FrameworkException 
	 */
	private void disconnectCoreMaterials(matrix.db.Context context, DomainObject domProdObject) throws FrameworkException {
		Map mpCoreMaterial = null;
		StringList slCoreMaterialConnIDList = new StringList();
		StringList slCoreMaterialIDList = new StringList();
		MapList mlCoreMaterials = getConnectedCoreMaterials(context, domProdObject);
		if(!mlCoreMaterials.isEmpty()) {
			for(int i = 0 ; i < mlCoreMaterials.size(); i++) {
				mpCoreMaterial = (Map)mlCoreMaterials.get(i);
				slCoreMaterialConnIDList.add((String)mpCoreMaterial.get(DomainRelationship.SELECT_ID));
				slCoreMaterialIDList.add((String)mpCoreMaterial.get(DomainConstants.SELECT_ID));
			}
			if(!slCoreMaterialConnIDList.isEmpty()) {
				//Disconnect Existing Connected Core Material connections.
				DomainRelationship.disconnect(context, slCoreMaterialConnIDList.toArray(new String[slCoreMaterialConnIDList.size()]));
				loggerWS.debug("\n\nCore Materials are disconnected from Product -----------");
			}
			loggerWS.debug("\nslCoreMaterialIDList-----------{}" ,slCoreMaterialIDList);
			loggerWS.debug("\nslCoreMaterialConnIDList-----------{}" ,slCoreMaterialConnIDList);
		}
	}
	
	/** Method to get Connected Core Material detail PROD Issue Changes
	 * @param context
	 * @param domRevisedBaseProd
	 * @return MapList having connection id and to object id
	 * @throws FrameworkException
	 */
	private MapList getConnectedCoreMaterials(matrix.db.Context context, DomainObject domRevisedBaseProd) throws FrameworkException {
		MapList mlCoreCoveringMaterials = new MapList();
		try
		{	
			Pattern typePattern = new Pattern(pgApolloConstants.TYPE_DSC_MAT_CNX_CORE_DESIGN);
			typePattern.addPattern(pgApolloConstants.TYPE_DSC_MAT_CNX_COVERING_DESIGN);
			StringList busSelects = new StringList();
			busSelects.add(DomainConstants.SELECT_ID);
			StringList relSelects = new StringList();
			relSelects.add(DomainRelationship.SELECT_ID);
			mlCoreCoveringMaterials = domRevisedBaseProd.getRelatedObjects(context, 
																	pgApolloConstants.RELATIONSHIP_VPLMREL_PLMCONNECTION_V_OWNER,// relationshipPattern
																	typePattern.getPattern(),// typePattern
																	busSelects,// objectSelects
																	relSelects,// relationshipSelects
																	false,// getTo
																	true,// getFrom
																	(short)1,// recurseToLevel
																	null,// objectWhere
																	null, // relationshipWhere
																	0);// limit
			loggerWS.debug("\n mlCoreCoveringMaterials-----------{}" ,mlCoreCoveringMaterials);
		}
		catch(Exception ex)
		{
			loggerApolloTrace.error(ex.getMessage(), ex);
			throw ex;
		}
		return mlCoreCoveringMaterials;
	}

	/**
	 * Method to connect/disconnect 3Dshape and drawings
	 * @param context
	 * @param EBOMUpdateLog
	 * @param strNewProdRev_ModelType
	 * @param sl3DShapeDrawing_TargetProd
	 * @param sl3DShapeDrawing_RelIDTargetProd
	 * @param domRevisedBaseProd
	 * @param mapRepInstances 
	 * @throws FrameworkException
	 * @throws IOException
	 */
	private void connectDisconnect3DShapeDrawings(matrix.db.Context context,
			String strNewProdRev_ModelType, StringList sl3DShapeDrawing_TargetProd,
			StringList sl3DShapeDrawing_RelIDTargetProd, DomainObject domRevisedBaseProd, Map mapRepInstances)
					throws FrameworkException, IOException {
		domRevisedBaseProd.setAttributeValue(context, pgApolloConstants.ATTRIBUTE_ISVPLMCONTROLLED, "TRUE");
		if(null!=sl3DShapeDrawing_TargetProd && !sl3DShapeDrawing_TargetProd.isEmpty())
		{
			Map newRepInstances = DomainRelationship.connect(context, domRevisedBaseProd, pgApolloConstants.RELATIONSHIP_VPMRepInstance, true, (String[])sl3DShapeDrawing_TargetProd.toArray(new String[sl3DShapeDrawing_TargetProd.size()]));
			updateRepInstanceAttributes(context, mapRepInstances, newRepInstances);
			loggerWS.debug("\n\n3dshape and Drawing objects connected to revised Product-----------");
		}

	}

	/**
	 * Method to update Rep Instance attributes
	 * @param context
	 * @param mapRepInstances
	 * @param newRepInstances
	 * @throws FrameworkException
	 */
	public void updateRepInstanceAttributes(matrix.db.Context context, Map mapRepInstances, Map newRepInstances) throws FrameworkException {
		String strObjectId;
		String strRelId;	
		String str3DShapeDrawingName;
		DomainRelationship domRelRepInstance;
		if(null != newRepInstances && !newRepInstances.isEmpty() && null != mapRepInstances && !mapRepInstances.isEmpty())
		{
			for (Iterator iterator = newRepInstances.keySet().iterator(); iterator.hasNext();) 
			{
				strObjectId = (String) iterator.next();
				strRelId = (String) newRepInstances.get(strObjectId);
				loggerWS.debug("strRelId {}", strRelId);
				if(mapRepInstances.containsKey(strObjectId))
				{
					str3DShapeDrawingName = (String)mapRepInstances.get(strObjectId);
					domRelRepInstance = new DomainRelationship(strRelId);
					domRelRepInstance.setAttributeValue(context, pgApolloConstants.ATTRIBUTE_PLMINSTANCE_PLM_EXTERNAL_ID, new StringBuilder(str3DShapeDrawingName).append(".1").toString());
					loggerWS.debug("RepInstance updated for : {}", strObjectId);

				}

			}

		}
	}

	/**
	 * Method to revise APP/Base Product to return revised VPMRef
	 * @param context
	 * @param domBaseProduct
	 * @param EBOMUpdateLog
	 * @return
	 * @throws Exception
	 */
	public Map reviseBaseProduct(matrix.db.Context context, DomainObject domBaseProduct) throws Exception {
		
		BusinessObject nextRev = new BusinessObject();
		String strNewCADId = DomainConstants.EMPTY_STRING;
		String sErrorMessage = DomainConstants.EMPTY_STRING;
		Map mapRevisedObjectMap = new HashMap();
		
		try
		{
			String strAPPObjectId = domBaseProduct.getInfo(context,"to[" + DomainConstants.RELATIONSHIP_PART_SPECIFICATION + "].from.id");
			if(UIUtil.isNotNullAndNotEmpty(strAPPObjectId))
			{
				mapRevisedObjectMap = getRevisedVPMRefId(context, strAPPObjectId);
				sErrorMessage = (String)mapRevisedObjectMap.getOrDefault(pgApolloConstants.STR_ERROR, DomainConstants.EMPTY_STRING);
				strNewCADId = (String)mapRevisedObjectMap.get(pgApolloConstants.KEY_PHYSICALPRODUCT);
			}
			else
			{
				String nextSequence = domBaseProduct.getNextMajorSequence(context);
				loggerWS.debug("\n\nNextSequence--------------{}" ,nextSequence);
				String vault = domBaseProduct.getInfo(context, DomainObject.SELECT_VAULT);
				loggerWS.debug("\n\nvault-------------{}" ,vault);
				String physicalId = com.matrixone.jsystem.util.UUID.getNewUUIDHEXString();
				loggerWS.debug("\n\nphysicalId-------------{}" ,physicalId);
				// Major Revise
				nextRev = domBaseProduct.revise(context, null, nextSequence, vault, physicalId, false, false);
				strNewCADId = nextRev.getObjectId(context);
			}			
			loggerWS.debug("\n\nstrNewCADId-------------{}" ,strNewCADId);			
		}
		catch(Exception ex)
		{
			sErrorMessage = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(ex.getLocalizedMessage()).toString();
			loggerApolloTrace.error(ex.getMessage(), ex);
		}
		
		mapRevisedObjectMap.put(pgApolloConstants.KEY_PHYSICALPRODUCT, strNewCADId);
		if(UIUtil.isNotNullAndNotEmpty(sErrorMessage))
		{
			mapRevisedObjectMap.put(pgApolloConstants.STR_ERROR, sErrorMessage);
		}

		return mapRevisedObjectMap;
	}


	/**
	 * revise APP to get revised VPMRef Id
	 * @param context
	 * @param EBOMUpdateLog
	 * @param strAPPObjectId
	 * @return
	 * @throws IOException 
	 * @throws Exception
	 */
	public static Map getRevisedVPMRefId(matrix.db.Context context, String strAPPObjectId) throws MatrixException {
		String strNewCADId = DomainConstants.EMPTY_STRING;
		Map mapRevisedObjectInfo = new HashMap();
		String sError = DomainConstants.EMPTY_STRING;
		try 
		{	
			String personCTX = PersonUtil.getDefaultSecurityContext(context, context.getUser());
			DomainObject domAPP = DomainObject.newInstance(context, strAPPObjectId);			
			StringList slobjectSelect = new StringList();
			slobjectSelect.add(DomainConstants.SELECT_NAME);
			slobjectSelect.add(DomainConstants.SELECT_OWNER);
			slobjectSelect.add(DomainConstants.SELECT_POLICY);
			slobjectSelect.add(DomainConstants.SELECT_ATTRIBUTE_TITLE);
			slobjectSelect.add(DomainConstants.SELECT_DESCRIPTION);
			slobjectSelect.add(CPNCommonConstants.SELECT_PRODUCT_DATA_RENDER_LANGUAGE);
			slobjectSelect.add(CPNCommonConstants.SELECT_SPECIFICATION_CATEGORY);
			slobjectSelect.add("from[" + pgApolloConstants.RELATIONSHIP_PRIMARYORGANIZATION + "].to.id");
			slobjectSelect.add("to[" + CPNCommonConstants.RELATIONSHIP_REGION_OWNS + "].from.id");
			slobjectSelect.add("from["+pgApolloConstants.RELATIONSHIP_PGPDTEMPLATES_TO_PGPLISEGMENT+"].to.id");

			StringList slMultiValue = new StringList();

			String strObjectId = DomainConstants.EMPTY_STRING;
			Map mapAPP = domAPP.getInfo(context, slobjectSelect, slMultiValue);
			String strProductDataName = (String)mapAPP.get(DomainConstants.SELECT_NAME);
			String strPolicy = (String)mapAPP.get(DomainConstants.SELECT_POLICY);
			String strVaultName = pgApolloConstants.VAULT_ESERVICE_PRODUCTION;
			String strOwner = context.getUser();
			String strDescription = (String)mapAPP.get(DomainConstants.SELECT_DESCRIPTION);
			String strRenderLanguage = (String)mapAPP.get(CPNCommonConstants.SELECT_PRODUCT_DATA_RENDER_LANGUAGE);
			String strTitle = (String)mapAPP.get(DomainConstants.SELECT_ATTRIBUTE_TITLE);
			String strSpecificationCategory = (String)mapAPP.get(CPNCommonConstants.SELECT_SPECIFICATION_CATEGORY);

			HashMap attributeList = new HashMap();
			attributeList.put(DomainConstants.SELECT_DESCRIPTION,strDescription);
			attributeList.put(DomainConstants.SELECT_OWNER,strOwner);

			HashMap attributesMap = new HashMap();
			attributesMap.put(CPNCommonConstants.ATTRIBUTE_PRODUCT_DATA_RENDER_LANGUAGE, strRenderLanguage);
			attributesMap.put(CPNCommonConstants.ATTRIBUTE_TITLE, strTitle);
			attributesMap.put(CPNCommonConstants.ATTRIBUTE_SPECIFICATION_CATEGORY,strSpecificationCategory);

			attributeList.put(pgApolloConstants.KEY_ATTRIBUTESMAP,attributesMap);

			String strBusinessUnitId = (String)mapAPP.get("from[" + pgApolloConstants.RELATIONSHIP_PRIMARYORGANIZATION + "].to.id");
			String strOwningRegionId = (String)mapAPP.get("to[" + CPNCommonConstants.RELATIONSHIP_REGION_OWNS + "].from.id");
			String strOwningSegmentForDSO = (String)mapAPP.get("from["+pgApolloConstants.RELATIONSHIP_PGPDTEMPLATES_TO_PGPLISEGMENT+"].to.id");

			HashMap relatedObjList = new HashMap();			
			// List of related objects.
			relatedObjList.put(pgApolloConstants.KEY_BU, strBusinessUnitId);
			relatedObjList.put(pgApolloConstants.KEY_TEMPLATE, null);
			relatedObjList.put(pgApolloConstants.KEY_REGION, strOwningRegionId);
			relatedObjList.put(pgApolloConstants.KEY_SEGMENT_REVISE, strOwningSegmentForDSO);					

			Map mapProgramMap = new HashMap();
			mapProgramMap.put(pgApolloConstants.KEY_SPECTYPE, pgApolloConstants.STR_SYMBOLIC_ASSEMBLED_PRODUCT_PART);
			mapProgramMap.put(pgApolloConstants.KEY_UOM, null);
			mapProgramMap.put(pgApolloConstants.KEY_PRODUCTDATANAME, strProductDataName);
			mapProgramMap.put(pgApolloConstants.KEY_REVISION, (String)domAPP.getNextSequence(context));
			mapProgramMap.put(pgApolloConstants.KEY_POLICY, strPolicy);
			mapProgramMap.put(pgApolloConstants.KEY_VAULTNAME, strVaultName);
			mapProgramMap.put(pgApolloConstants.KEY_ATTRIBUTELIST, attributeList);
			mapProgramMap.put(pgApolloConstants.KEY_RELATEDOBJLIST, relatedObjList);
			mapProgramMap.put(pgApolloConstants.KEY_SPECAUTONAME, new Boolean(false));
			mapProgramMap.put(pgApolloConstants.KEY_TEMPLATEID, null);
			mapProgramMap.put(pgApolloConstants.KEY_STRSOURCEPRODDATAID, strAPPObjectId);
			mapProgramMap.put(pgApolloConstants.KEY_CHANGETEMPLATE, DomainConstants.EMPTY_STRING);
			mapProgramMap.put(pgApolloConstants.KEY_CO,DomainConstants.EMPTY_STRING);
			mapProgramMap.put(pgApolloConstants.KEY_RDOID, strBusinessUnitId);

			loggerWS.debug("\n\nmapProgramMap--------------{}" ,mapProgramMap);					
			PropertyUtil.setGlobalRPEValue(context,"REVISE_IPM_EBOM", "true");
			PropertyUtil.setGlobalRPEValue(context,"REVISE_IPM_DSM", "true");
			strObjectId = (String) JPO.invoke(context, "enoGLSFormulationProductData", null, "reviseProductData", JPO.packArgs(mapProgramMap), String.class); // to be used for 2018x.5
			//For 2018x.3, below line to be used
			//strObjectId = (String) JPO.invoke(context, "emxCPNProductData", null, "reviseProductData", JPO.packArgs(mapProgramMap), String.class);  
			loggerWS.debug("\n\n strObjectId--------------{}" ,strObjectId);					
			if(UIUtil.isNotNullAndNotEmpty(strObjectId))
			{
				DomainObject domRevisedAPP = DomainObject.newInstance(context,strObjectId);
				strNewCADId = domRevisedAPP.getInfo(context, pgApolloConstants.SELECT_CAD_ID);						
				if(UIUtil.isNotNullAndNotEmpty(strNewCADId))
				{
					DomainObject domRevisedBaseProd = DomainObject.newInstance(context, strNewCADId);
					domRevisedAPP.setAttributeValue(context, DomainConstants.ATTRIBUTE_REASON_FOR_CHANGE, DomainConstants.EMPTY_STRING);
					domRevisedBaseProd.setAttributeValue(context, pgApolloConstants.ATTRIBUTE_VERSION_COMMENT, DomainConstants.EMPTY_STRING);
					loggerWS.debug("\n Updated Version Comment and Reason For Change");
				}
				mapRevisedObjectInfo.put(pgApolloConstants.KEY_PHYSICALPRODUCT, strNewCADId);
				mapRevisedObjectInfo.put(DomainConstants.SELECT_ID, strObjectId);
			}
		}
		catch (Exception e) 
		{
			loggerApolloTrace.error(e.getMessage(), e);	
			sError = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getMessage()).toString();
			mapRevisedObjectInfo.put(pgApolloConstants.STR_ERROR, sError);
		}		
		
		return mapRevisedObjectInfo;
	}
	
	
	//Apollo 2018x.3 Defect 32796 : Revise Automation Webservice is not copying Drawing object properly - Start
	/**
	 * This method will return all VPMRepInstance Connected object for a give APP.
	 * @param context
	 * @param domTargetProd
	 * @return
	 * @throws Exception
	 */
	public MapList getConnected3DShapeDrawingObject(matrix.db.Context context, DomainObject domTargetProd) throws Exception {
		MapList ml3DShapeDrawing = new MapList();
		try
		{	
			StringList busSelects = new StringList();
			busSelects.add(DomainConstants.SELECT_ID);
			busSelects.add(DomainConstants.SELECT_TYPE);
			busSelects.add(DomainConstants.SELECT_NAME);
			StringList relSelects = new StringList();
			relSelects.add(DomainRelationship.SELECT_ID);
			ml3DShapeDrawing = domTargetProd.getRelatedObjects(context, pgApolloConstants.RELATIONSHIP_VPMRepInstance, DomainConstants.QUERY_WILDCARD, busSelects, relSelects, false, true, (short)1, null, null, 0);
			ml3DShapeDrawing.sort(DomainConstants.SELECT_TYPE, "ascending", "string");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw ex;
		}
		return ml3DShapeDrawing;
	}
		//Apollo 2018x.3 Defect 32796 : Revise Automation Webservice is not copying Drawing object properly - End
	
	
	
	//APOLLO 2018x.5 - A10-501 Webservice - Copy/Paste 3DShape and Drawing Starts
	/**
	 * Apollo 2018x.5 A10-501
	 * Method to update WIP model with revised generic model
	 * Format - fetchData/updateModel?VPMRefModName|VPMRefModRev&VPMRefCopyName|VPMRefCopyRev
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/updateModel")
	public Response updateWIPModelWithGenericModel(@Context HttpServletRequest req) throws Exception {
		System.out.println("Entered method in GET ExtractDataForDesignTool : updateWIPModelWithGenericModel --------");
		System.out.println("Path : /fetchData/updateModel");
		
		matrix.db.Context context = null;

		StringBuffer sbReturnMessages = new StringBuffer();
		String strVPMRefModId = DomainConstants.EMPTY_STRING;
		String strVPMRefCopyId = DomainConstants.EMPTY_STRING;

		boolean isContextPushed = false;
		
		try {			
			loggerWS.debug("******************************************************************************");
			loggerWS.debug("Path : /fetchData/updateModel");
			loggerWS.debug("Method: ExtractDataForDesignTool : updateWIPModelWithGenericModel");	
			loggerWS.debug("\n\n Update Model started : {}" ,new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
			String strVPMRefModName = DomainConstants.EMPTY_STRING;
			String strVPMRefModRev = DomainConstants.EMPTY_STRING;
			String strVPMRefCopyName = DomainConstants.EMPTY_STRING;
			String strVPMRefCopyRev = DomainConstants.EMPTY_STRING;
			String strVPMRefModDetails = DomainConstants.EMPTY_STRING;
			String strVPMRefCopyDetails =  DomainConstants.EMPTY_STRING;
			StringList sl3DShapeDrawing_VPMRefCopy = new StringList();
			StringList sl3DShapeDrawing_RelIDVPMRefCopy = new StringList();
			StringList sl3DShapeDrawing_VPMRefMod= new StringList();
			StringList sl3DShapeDrawing_RelIDVPMRefMod = new StringList();
			Map map3DShapeDrawing = new HashMap();
			String str3DShapeDrawingObjectId = DomainConstants.EMPTY_STRING;
			String str3DShapeDrawingRelId = DomainConstants.EMPTY_STRING;

			String[] strArrayURLParam = null;			

			// Get the user context
			if (Framework.isLoggedIn((HttpServletRequest) req)) {
				context = Framework.getContext((HttpSession) req.getSession(false));
			}
			loggerWS.debug("context  : {}" ,context);			
			
			String queryString = req.getQueryString();
			queryString =  java.net.URLDecoder.decode(queryString,"UTF-8");
			loggerWS.debug("\n\nQueryString  : {}" ,queryString);
						
			if(queryString.contains("%26")){
				queryString = queryString.replace("%26", pgApolloConstants.CONSTANT_STRING_AMPERSAND);
			}
			
			//Setting context as User agent
			ContextUtil.pushContext(context, PropertyUtil.getSchemaProperty(context, pgApolloConstants.PERSON_USER_AGENT), DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
			isContextPushed = true;
			
			if (UIUtil.isNotNullAndNotEmpty(queryString)) {
				strArrayURLParam = queryString.split(pgApolloConstants.CONSTANT_STRING_AMPERSAND);
				if (strArrayURLParam.length > 1) 
				{

					strVPMRefModDetails = strArrayURLParam[0];
					strVPMRefCopyDetails = strArrayURLParam[1];

					StringList slVPMRefModDetails = FrameworkUtil.split(strVPMRefModDetails, pgApolloConstants.CONSTANT_STRING_PIPE);
					if(null!=slVPMRefModDetails && !slVPMRefModDetails.isEmpty() && slVPMRefModDetails.size()>1)
					{
						strVPMRefModName = (String)slVPMRefModDetails.get(0);
						loggerWS.debug("\n\nstrVPMRefModName----------{}" ,strVPMRefModName);
						strVPMRefModRev = (String)slVPMRefModDetails.get(1);
						loggerWS.debug("\n\nstrVPMRefModRev----------{}" ,strVPMRefModRev);
					}
					StringList slVPMRefCopyDetails = FrameworkUtil.split(strVPMRefCopyDetails, pgApolloConstants.CONSTANT_STRING_PIPE);
					if(null!=slVPMRefCopyDetails && !slVPMRefCopyDetails.isEmpty() && slVPMRefCopyDetails.size()>1)
					{
						strVPMRefCopyName = (String)slVPMRefCopyDetails.get(0);
						loggerWS.debug("\n\nstrVPMRefCopyName---------{}" ,strVPMRefCopyName);
						strVPMRefCopyRev = (String)slVPMRefCopyDetails.get(1);
						loggerWS.debug("\n\nstrVPMRefCopyRev---------{}" ,strVPMRefCopyRev);
					}
					StringList slObjSelects = new StringList(2);
					slObjSelects.add(DomainConstants.SELECT_CURRENT);

					StringList selectables = new StringList(2);
					selectables.add(DomainConstants.SELECT_NAME);
					selectables.add(DomainConstants.SELECT_REVISION);

					strVPMRefModId = pgApolloCommonUtil.getObjectId(context, pgApolloConstants.TYPE_VPMREFERENCE, strVPMRefModName, strVPMRefModRev);
					strVPMRefCopyId = pgApolloCommonUtil.getObjectId(context, pgApolloConstants.TYPE_VPMREFERENCE, strVPMRefCopyName, strVPMRefCopyRev);

					loggerWS.debug("\n\nstrVPMRefModId : {}" ,strVPMRefModId);
					loggerWS.debug("\n\nstrVPMRefCopyId  : {}" ,strVPMRefCopyId);
					Map mapRepInstances = new HashMap();
					String str3DShapeDrawingName;
					if (UIUtil.isNotNullAndNotEmpty(strVPMRefModId) && UIUtil.isNotNullAndNotEmpty(strVPMRefCopyId))
					{
						//Dummy Model i.e. VPMRefCopy
						DomainObject domVPMRefCopyProd = DomainObject.newInstance(context, strVPMRefCopyId);
						MapList ml3DShapeDrawing = getConnectedOnly3DShapeDrawingObject(context, domVPMRefCopyProd);
						if(null!=ml3DShapeDrawing && !ml3DShapeDrawing.isEmpty())
						{
							for(int i=0;i<ml3DShapeDrawing.size();i++)
							{
								map3DShapeDrawing = new HashMap();
								map3DShapeDrawing = (Map)ml3DShapeDrawing.get(i);
								str3DShapeDrawingObjectId = (String)map3DShapeDrawing.get(DomainConstants.SELECT_ID);
								str3DShapeDrawingRelId = (String)map3DShapeDrawing.get(DomainRelationship.SELECT_ID);
								str3DShapeDrawingName = (String)map3DShapeDrawing.get(DomainConstants.SELECT_NAME);
								mapRepInstances.put(str3DShapeDrawingObjectId, str3DShapeDrawingName);
								sl3DShapeDrawing_VPMRefCopy.add(str3DShapeDrawingObjectId);
								sl3DShapeDrawing_RelIDVPMRefCopy.add(str3DShapeDrawingRelId);
							}
							loggerWS.debug("\n sl3DShapeDrawing_VPMRefCopy-----------{}" ,sl3DShapeDrawing_VPMRefCopy);
							loggerWS.debug("\n sl3DShapeDrawing_RelIDVPMRefCoy-----------{}" ,sl3DShapeDrawing_RelIDVPMRefCopy);

						}						
						Map mpRevVPMRefCopyProdDetails = domVPMRefCopyProd.getInfo(context, slObjSelects);
						String strVPMRefCopyCurrent = (String)mpRevVPMRefCopyProdDetails.get(DomainConstants.SELECT_CURRENT);

						DomainObject domVPMRefModObj = DomainObject.newInstance(context, strVPMRefModId);
						ml3DShapeDrawing = getConnectedOnly3DShapeDrawingObject(context, domVPMRefModObj);
						disconnectCoreMaterials(context, domVPMRefModObj); // PROD Issue Changes
						disconnectCoreMaterials(context, domVPMRefCopyProd); // PROD Issue Changes
						if(null!=ml3DShapeDrawing && !ml3DShapeDrawing.isEmpty())
						{
							for(int i=0;i<ml3DShapeDrawing.size();i++)
							{
								map3DShapeDrawing = new HashMap();
								map3DShapeDrawing = (Map)ml3DShapeDrawing.get(i);
								str3DShapeDrawingObjectId = (String)map3DShapeDrawing.get(DomainConstants.SELECT_ID);
								str3DShapeDrawingRelId = (String)map3DShapeDrawing.get(DomainRelationship.SELECT_ID);
								sl3DShapeDrawing_VPMRefMod.add(str3DShapeDrawingObjectId);
								sl3DShapeDrawing_RelIDVPMRefMod.add(str3DShapeDrawingRelId);
							}
							loggerWS.debug("\n sl3DShapeDrawing_VPMRefMod-----------{}" ,sl3DShapeDrawing_VPMRefMod);
							loggerWS.debug("\n sl3DShapeDrawing_RelIDVPMRefMod-----------{}" ,sl3DShapeDrawing_RelIDVPMRefMod);

						}						
						Map mpRevVPMRefModProdDetails = domVPMRefModObj.getInfo(context, slObjSelects);
						String strVPMRefModCurrent =(String)mpRevVPMRefModProdDetails.get(DomainConstants.SELECT_CURRENT);						

						if(pgApolloConstants.STATE_IN_WORK.equals(strVPMRefModCurrent) && pgApolloConstants.STATE_IN_WORK.equals(strVPMRefCopyCurrent))
						{					

							if(null!=sl3DShapeDrawing_VPMRefCopy && !sl3DShapeDrawing_VPMRefCopy.isEmpty())
							{
								if(null!=sl3DShapeDrawing_VPMRefMod && !sl3DShapeDrawing_VPMRefMod.isEmpty())
								{
									DomainRelationship.disconnect(context, (String[])sl3DShapeDrawing_RelIDVPMRefMod.toArray(new String[sl3DShapeDrawing_RelIDVPMRefMod.size()]));
									pgApolloCommonUtil.deleteRepresentations(context, sl3DShapeDrawing_VPMRefMod);
									loggerWS.debug("\n Deleted 3DShape and Drawing from to be modified Model with OOTB API : {}" ,sl3DShapeDrawing_VPMRefMod);
								}
								Map newRepInstances = DomainRelationship.connect(context, domVPMRefModObj, pgApolloConstants.RELATIONSHIP_VPMRepInstance, true, (String[])sl3DShapeDrawing_VPMRefCopy.toArray(new String[sl3DShapeDrawing_VPMRefCopy.size()]));
								loggerWS.debug("\n Connected 3DShape and Drawing to To Be modified Model : {}" ,sl3DShapeDrawing_VPMRefCopy);
								updateRepInstanceAttributes(context, mapRepInstances, newRepInstances);
								updateSemanticRelationsOnDrawing(context, domVPMRefModObj, domVPMRefCopyProd); //PROD Issue Changes
								
								//Disconnect 3DShape and Drawing from Dummy/Copy Product
								if(null!=sl3DShapeDrawing_RelIDVPMRefCopy && !sl3DShapeDrawing_RelIDVPMRefCopy.isEmpty())
								{
									DomainRelationship.disconnect(context, sl3DShapeDrawing_RelIDVPMRefCopy.toArray(new String[sl3DShapeDrawing_RelIDVPMRefCopy.size()]));
								}
								
								//Dummy product should be deleted
								domVPMRefCopyProd.deleteObject(context);
								loggerWS.debug("\n Dummy VPMRef Delete Sucessful");

								sbReturnMessages.append(pgApolloConstants.STR_SUCCESS);
							}
							else
							{
								sbReturnMessages.append(pgApolloConstants.STR_ERROR);
								sbReturnMessages.append(pgApolloConstants.CONSTANT_STRING_COLON);
								sbReturnMessages.append(pgApolloConstants.STR_ERROR_NO3DSHAPEDRAWINGATTACHED);
							}
						}
						else
						{
							sbReturnMessages.append(pgApolloConstants.STR_ERROR);
							sbReturnMessages.append(pgApolloConstants.CONSTANT_STRING_COLON);
							sbReturnMessages.append(pgApolloConstants.STR_ERROR_VPMREF_IS_INWORK);
						}
					} 
					else
					{
						sbReturnMessages.append(pgApolloConstants.STR_ERROR); 
						sbReturnMessages.append(pgApolloConstants.CONSTANT_STRING_COLON);
						sbReturnMessages.append(pgApolloConstants.STR_UPDATEMODEL_OBJECTNOTPRESENT);
					}
				}
				else
				{
					sbReturnMessages.append(pgApolloConstants.STR_ERROR);
					sbReturnMessages.append(pgApolloConstants.CONSTANT_STRING_COLON);
					sbReturnMessages.append(pgApolloConstants.STR_AUTOMATION_ERROR_INVALID_PARAMETERS);
				}
			}
		} catch (Exception e) {
			loggerApolloTrace.error(e.getMessage(), e);
			sbReturnMessages.append(pgApolloConstants.STR_ERROR+ pgApolloConstants.CONSTANT_STRING_COLON + e.getLocalizedMessage());
		} finally {
			
			if(isContextPushed)
			{
				ContextUtil.popContext(context);
			}			
			loggerWS.debug("\n\nUpdate Model Ended-----------------");
			loggerWS.debug("\n******************************************************************************");
		}
		return Response.status(200).entity(sbReturnMessages.toString()).build();
	}
	

	/**
	 * Apollo 2018x.5 A10-501
	 * Method to fetch connected Only 3DShape and Drawing objects
	 * @param context
	 * @param domTargetProd
	 * @return
	 */
	public MapList getConnectedOnly3DShapeDrawingObject(matrix.db.Context context, DomainObject domObject) throws Exception {
		MapList ml3DShapeDrawing = new MapList();
		try
		{	
			StringList busSelects = new StringList();
			busSelects.add(DomainConstants.SELECT_ID);
			busSelects.add(DomainConstants.SELECT_TYPE);
			busSelects.add(DomainConstants.SELECT_NAME);

			StringList relSelects = new StringList();
			relSelects.add(DomainRelationship.SELECT_ID);
			
			ml3DShapeDrawing = domObject.getRelatedObjects(context, pgApolloConstants.RELATIONSHIP_VPMRepInstance, pgApolloConstants.TYPE_3DSHAPE+","+pgApolloConstants.TYPE_DRAWING, busSelects, relSelects, false, true, (short)1, null, null, 0);
			ml3DShapeDrawing.sort(DomainConstants.SELECT_TYPE, "ascending", "string");

		}
		catch(Exception ex)
		{
			loggerApolloTrace.error(ex.getMessage(), ex);
			throw ex;
		}
		return ml3DShapeDrawing;
	}
	
	//APOLLO 2018x.5 - A10-501 Webservice - Copy/Paste 3DShape and Drawing Ends	
	
	

	/**
	 * Webservice Method to Query APPs based on PartName/APP number and Maturity State
	 * APOLLO 2018x.6 Sep CW ALM Requirement A10-949
	 * Format : /fetchData/QueryAPP?state=<MaturityState>&part=<Free text that can include PartName or APP number>
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/queryPart")
	public Response queryForAPPAndVPMReference (@Context HttpServletRequest req) throws Exception {
		
		matrix.db.Context context = null;

		String[] arrayURLParam = new String[20];
		String strMaturityStateDetails = DomainConstants.EMPTY_STRING;
		String strMaturityState = DomainConstants.EMPTY_STRING;
		StringList slMaturityStateDetails;
		StringList slObjectDetails;
		String strObjectDetails = DomainConstants.EMPTY_STRING;
		String strObjectName = DomainConstants.EMPTY_STRING;

		StringBuilder sbOutput = new StringBuilder();
		StringBuilder sbAPPWhereClause = new StringBuilder();
		StringBuilder sbPhysicalProductWhereClause = new StringBuilder();
		String sPhysicalProductWhereClause;
		String sAPPWhereClause;

		String sOutput;
		
		boolean bContextPushed =false;	
		boolean bValidState = false;
		
		try {

			loggerWS.debug("******************************************************************************");
			loggerWS.debug("Path : /fetchData/queryPart");
			loggerWS.debug("Method: ExtractDataForDesignTool : queryForAPPAndVPMReference");
			String strStartTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) ;
			loggerWS.debug("Query APP or Physical Product Details started : {}" ,strStartTime );

			// Get the user context
			if (Framework.isLoggedIn(req)) {
				context = Framework.getContext(req.getSession(false));				
			}

			if(null != context)
			{
				loggerWS.debug("context user  : {}" , context.getUser());
			}
			//Pushing the context to User Agent - User will not have always access to all parts 
			ContextUtil.pushContext(context,pgApolloConstants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);		
			bContextPushed = true;
			
			String queryString = req.getQueryString();
			queryString =  java.net.URLDecoder.decode(queryString,"UTF-8");

			loggerWS.debug("QueryString  : {}" , queryString);

			if (UIUtil.isNotNullAndNotEmpty(queryString))
			{
				// /fetchData/queryPart?state=<MaturityState>&part=<Free text that can include PartName or APP number>
				arrayURLParam = queryString.split(pgApolloConstants.CONSTANT_STRING_AMPERSAND);				
				if (null!= arrayURLParam && arrayURLParam.length > 1)
				{					
					strMaturityStateDetails = arrayURLParam[0];
					slMaturityStateDetails = StringUtil.split(strMaturityStateDetails, pgApolloConstants.CONSTANT_STRING_EQUAL_SIGN);					
					if(slMaturityStateDetails.size() > 1)
					{
						strMaturityState = slMaturityStateDetails.get(1);
					}					
					strObjectDetails  = arrayURLParam[1];
					slObjectDetails = StringUtil.split(strObjectDetails, pgApolloConstants.CONSTANT_STRING_EQUAL_SIGN);					
					if(slObjectDetails.size() > 1)
					{
						strObjectName = slObjectDetails.get(1);
					}					
					if(UIUtil.isNotNullAndNotEmpty(strObjectName))
					{
						strObjectName = new StringBuilder(DomainConstants.QUERY_WILDCARD).append(strObjectName).append(DomainConstants.QUERY_WILDCARD).toString();
									
						//LPD Physical Product condition
						sbPhysicalProductWhereClause.append(pgApolloConstants.SELECT_ATTRIBUTE_PGENTERPRISETYPE);
						sbPhysicalProductWhereClause.append(pgApolloConstants.CONSTANT_STRING_DOUBLE_EQUAL);
						sbPhysicalProductWhereClause.append("'");
						sbPhysicalProductWhereClause.append(pgApolloConstants.STR_ASSEMBLED_PRODUCT_PART);
						sbPhysicalProductWhereClause.append("'");			
						
						//APP LPD Condition
						sbAPPWhereClause.append(pgApolloConstants.SELECT_ATTRIBUTE_PGAUTHORINGAPPLICATION);
						sbAPPWhereClause.append(pgApolloConstants.CONSTANT_STRING_DOUBLE_EQUAL);
						sbAPPWhereClause.append("'");
						sbAPPWhereClause.append(pgApolloConstants.RANGE_PGAUTHORINGAPPLICATION_LPD);
						sbAPPWhereClause.append("'");	
						
						
						//Maturity Condition
						if(UIUtil.isNotNullAndNotEmpty(strMaturityState))
						{
							sbPhysicalProductWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE);
							sbPhysicalProductWhereClause.append(pgApolloConstants.CONSTANT_STRING_DOUBLE_AMPERSAND);
							sbPhysicalProductWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE);	
							
							sbAPPWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE);
							sbAPPWhereClause.append(pgApolloConstants.CONSTANT_STRING_DOUBLE_AMPERSAND);
							sbAPPWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE);	

							
							if(pgApolloConstants.STR_ALL.equalsIgnoreCase(strMaturityState))
							{
								sbPhysicalProductWhereClause.append(DomainConstants.SELECT_CURRENT);
								sbPhysicalProductWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE);
								sbPhysicalProductWhereClause.append(pgApolloConstants.CONSTANT_STRING_NOT_EQUAL_SIGN);
								sbPhysicalProductWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE);	
								sbPhysicalProductWhereClause.append("'");
								sbPhysicalProductWhereClause.append(pgApolloConstants.STATE_OBSOLETE_CATIA);
								sbPhysicalProductWhereClause.append("'");
								
								sbAPPWhereClause.append(DomainConstants.SELECT_CURRENT);
								sbAPPWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE);
								sbAPPWhereClause.append(pgApolloConstants.CONSTANT_STRING_NOT_EQUAL_SIGN);
								sbAPPWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE);
								sbAPPWhereClause.append("'");
								sbAPPWhereClause.append(DomainConstants.STATE_PART_OBSOLETE);
								sbAPPWhereClause.append("'");
								
								bValidState = true;	
								
							}	
							else
							{							
								sbPhysicalProductWhereClause.append(DomainConstants.SELECT_CURRENT);
								sbPhysicalProductWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE);
								sbPhysicalProductWhereClause.append(pgApolloConstants.CONSTANT_STRING_DOUBLE_EQUAL);
								sbPhysicalProductWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE);	
																					
								sbAPPWhereClause.append(DomainConstants.SELECT_CURRENT);
								sbAPPWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE);
								sbAPPWhereClause.append(pgApolloConstants.CONSTANT_STRING_DOUBLE_EQUAL);
								sbAPPWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE);
								
								if(pgApolloConstants.STATE_SHARED.equalsIgnoreCase(strMaturityState) || pgApolloConstants.STATE_RELEASE.equalsIgnoreCase(strMaturityState) || pgApolloConstants.STATE_RELEASED.equalsIgnoreCase(strMaturityState))
								{
									
									sbPhysicalProductWhereClause.append("'");
									sbPhysicalProductWhereClause.append(pgApolloConstants.STATE_SHARED);
									sbPhysicalProductWhereClause.append("'");
									
									sbAPPWhereClause.append("'");
									sbAPPWhereClause.append(pgApolloConstants.STATE_RELEASE);
									sbAPPWhereClause.append("'");	
	
									bValidState = true;								
								}
								else if(pgApolloConstants.STATE_OBSOLETE_CATIA.equalsIgnoreCase(strMaturityState))
								{
									sbPhysicalProductWhereClause.append("'");
									sbPhysicalProductWhereClause.append(pgApolloConstants.STATE_OBSOLETE_CATIA);
									sbPhysicalProductWhereClause.append("'");
									
									sbAPPWhereClause.append("'");
									sbAPPWhereClause.append(DomainConstants.STATE_PART_OBSOLETE);
									sbAPPWhereClause.append("'");
									
									bValidState = true;	
								}
								else if(pgApolloConstants.STATE_IN_WORK.equalsIgnoreCase(strMaturityState))
								{
									sbPhysicalProductWhereClause.append("'");
									sbPhysicalProductWhereClause.append(pgApolloConstants.STATE_IN_WORK);
									sbPhysicalProductWhereClause.append("'");
									
									sbAPPWhereClause.append("'");
									sbAPPWhereClause.append(DomainConstants.STATE_PART_PRELIMINARY);
									sbAPPWhereClause.append("'");
									
									bValidState = true;	
								}							
								else if(pgApolloConstants.STATE_WAITAPP.equalsIgnoreCase(strMaturityState) || pgApolloConstants.STATE_APPROVED.equalsIgnoreCase(strMaturityState))
								{
									sbPhysicalProductWhereClause.append("'");
									sbPhysicalProductWhereClause.append(pgApolloConstants.STATE_WAITAPP);
									sbPhysicalProductWhereClause.append("'");	
									
									sbAPPWhereClause.append("'");
									sbAPPWhereClause.append(DomainConstants.STATE_PART_APPROVED);
									sbAPPWhereClause.append("'");
									
									bValidState = true;	
								}	
							}
						}		
						
						if(bValidState)
						{			

							sbPhysicalProductWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE);
							sbPhysicalProductWhereClause.append(pgApolloConstants.CONSTANT_STRING_DOUBLE_AMPERSAND);
							sbPhysicalProductWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE);					

							//Physical Product Name Condition					
							sbPhysicalProductWhereClause.append(DomainConstants.SELECT_NAME);
							sbPhysicalProductWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE);
							sbPhysicalProductWhereClause.append("~~");
							sbPhysicalProductWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE);							

							sbPhysicalProductWhereClause.append("'");
							sbPhysicalProductWhereClause.append(strObjectName);
							sbPhysicalProductWhereClause.append("'");

							sbAPPWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE);
							sbAPPWhereClause.append(pgApolloConstants.CONSTANT_STRING_DOUBLE_AMPERSAND);
							sbAPPWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE);

							//APP Name Condition
							sbAPPWhereClause.append(DomainConstants.SELECT_NAME);
							sbAPPWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE);
							sbAPPWhereClause.append("~~");
							sbAPPWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE);	

							sbAPPWhereClause.append("'");
							sbAPPWhereClause.append(strObjectName);
							sbAPPWhereClause.append("'");	


							loggerWS.debug("sbPhysicalProductWhereClause : {}", sbPhysicalProductWhereClause);
							loggerWS.debug("sbAPPWhereClause : {}", sbAPPWhereClause);


							String sAPPNameSelectable = "to["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION+"].from.name";
							String sAPPRevisionSelectable = "to["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION+"].from.revision";

							StringList slPhysicalProductSelectables = new StringList();
							slPhysicalProductSelectables.add(DomainConstants.SELECT_NAME);
							slPhysicalProductSelectables.add(DomainConstants.SELECT_REVISION);
							slPhysicalProductSelectables.add(pgApolloConstants.SELECT_ATTRIBUTE_PGLPDMODELTYPE);
							slPhysicalProductSelectables.add(sAPPNameSelectable);
							slPhysicalProductSelectables.add(sAPPRevisionSelectable);
							slPhysicalProductSelectables.add(DomainConstants.SELECT_CURRENT);
							slPhysicalProductSelectables.add(pgApolloConstants.SELECT_ATTRIBUTE_PGMANUFACTURING_MATURITYSTATUS);


							sPhysicalProductWhereClause = sbPhysicalProductWhereClause.toString();						

							MapList mlPhysicalProductList  = DomainObject.findObjects(context,	//Context
									pgApolloConstants.TYPE_VPMREFERENCE,//	Object Type 
									DomainConstants.QUERY_WILDCARD, 	//	Object Name
									DomainConstants.QUERY_WILDCARD,		//	Object Revision
									DomainConstants.QUERY_WILDCARD,		//	Owner 
									pgApolloConstants.VAULT_VPLM,		//	Vault
									sPhysicalProductWhereClause,		//	Where Clause
									false,								//	Expand type
									slPhysicalProductSelectables);		//  Object Selectables

							loggerWS.debug("mlPhysicalProductList : {}", mlPhysicalProductList.size());


							String sPhysicalProductNameSelectable = new StringBuilder("from["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION+"].").append("to["+pgApolloConstants.TYPE_VPMREFERENCE+"].name").toString();
							String sPhysicalProductRevisionSelectable = new StringBuilder("from["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION+"].").append("to["+pgApolloConstants.TYPE_VPMREFERENCE+"].revision").toString();
							String sPhysicalProductModelTypeSelectable = new StringBuilder("from["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION+"].").append("to["+pgApolloConstants.TYPE_VPMREFERENCE+"].").append(pgApolloConstants.SELECT_ATTRIBUTE_PGLPDMODELTYPE).toString();
							String sPhysicalProductStateSelectable = new StringBuilder("from["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION+"].").append("to["+pgApolloConstants.TYPE_VPMREFERENCE+"].").append(DomainConstants.SELECT_CURRENT).toString();
							String sPhysicalProductReleasePhaseSelectable = new StringBuilder("from["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION+"].").append("to["+pgApolloConstants.TYPE_VPMREFERENCE+"].").append(pgApolloConstants.SELECT_ATTRIBUTE_PGMANUFACTURING_MATURITYSTATUS).toString();

							StringList slAPPSelectables = new StringList();
							slAPPSelectables.add(DomainConstants.SELECT_NAME);
							slAPPSelectables.add(DomainConstants.SELECT_REVISION);						
							slAPPSelectables.add(sPhysicalProductNameSelectable);						
							slAPPSelectables.add(sPhysicalProductRevisionSelectable);						
							slAPPSelectables.add(sPhysicalProductModelTypeSelectable);	
							slAPPSelectables.add(sPhysicalProductStateSelectable);	
							slAPPSelectables.add(sPhysicalProductReleasePhaseSelectable);	

							sAPPWhereClause = sbAPPWhereClause.toString();

							MapList mlAPPList              = DomainObject.findObjects(context,	//Context
									pgApolloConstants.TYPE_ASSEMBLED_PRODUCT_PART,//Object Type 
									DomainConstants.QUERY_WILDCARD, 			//	Object Name
									DomainConstants.QUERY_WILDCARD,				//	Object Revision
									DomainConstants.QUERY_WILDCARD,				//	Owner 
									pgApolloConstants.VAULT_ESERVICE_PRODUCTION,//	Vault
									sAPPWhereClause,							//	Where Clause
									false,										//	Expand type
									slAPPSelectables);		         			//  Object Selectables


							loggerWS.debug("mlAPPList : {}", mlAPPList.size());

							Map mapPhysicalProduct;
							Map mapAPP;
							String sVPMRefName;
							String sVPMRefRevision;
							String sVPMRefCurrent;
							String sVPMRefModelType;
							String sAPPName;
							String sAPPRevision;
							String sVPMRefReleasePhase;

							if(null != mlPhysicalProductList && !mlPhysicalProductList.isEmpty())
							{
								for(int i=0; i<mlPhysicalProductList.size(); i++)
								{
									mapPhysicalProduct = (Map)mlPhysicalProductList.get(i);

									sVPMRefName = (String)mapPhysicalProduct.get(DomainConstants.SELECT_NAME);
									sVPMRefRevision = (String)mapPhysicalProduct.get(DomainConstants.SELECT_REVISION);
									sVPMRefCurrent = (String)mapPhysicalProduct.get(DomainConstants.SELECT_CURRENT);
									sVPMRefCurrent = EnoviaResourceBundle.getStateI18NString(context,pgApolloConstants.POLICY_VPLM_SMB_DEFINITION ,sVPMRefCurrent, Locale.ENGLISH.getLanguage());				
									sVPMRefReleasePhase = (String)mapPhysicalProduct.get(pgApolloConstants.SELECT_ATTRIBUTE_PGMANUFACTURING_MATURITYSTATUS);
									sAPPName = (String)mapPhysicalProduct.get(sAPPNameSelectable);								
									sAPPRevision = (String)mapPhysicalProduct.get(sAPPRevisionSelectable);
									sVPMRefModelType = (String)mapPhysicalProduct.get(pgApolloConstants.SELECT_ATTRIBUTE_PGLPDMODELTYPE);

									if(UIUtil.isNotNullAndNotEmpty(sAPPName))
									{
										if(i > 0)
										{
											sbOutput.append(pgApolloConstants.CONSTANT_STRING_TILDA);
										}
										
										sbOutput.append(sVPMRefName);
										sbOutput.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sVPMRefRevision);
										sbOutput.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sAPPName);
										sbOutput.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sAPPRevision);
										sbOutput.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sVPMRefModelType);
										sbOutput.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sVPMRefReleasePhase);
										sbOutput.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sVPMRefCurrent);
									}				

								}
							}


							if(null != mlAPPList && !mlAPPList.isEmpty())
							{
								if(!sbOutput.toString().isEmpty())
								{
									sbOutput.append(pgApolloConstants.CONSTANT_STRING_TILDA);
								}

								for(int i=0; i<mlAPPList.size(); i++)
								{
									mapAPP = (Map)mlAPPList.get(i);

									sAPPName = (String)mapAPP.get(DomainConstants.SELECT_NAME);
									sAPPRevision = (String)mapAPP.get(DomainConstants.SELECT_REVISION);
									sVPMRefName = (String)mapAPP.get(sPhysicalProductNameSelectable);								
									sVPMRefRevision = (String)mapAPP.get(sPhysicalProductRevisionSelectable);
									sVPMRefModelType = (String)mapAPP.get(sPhysicalProductModelTypeSelectable);
									sVPMRefCurrent = (String)mapAPP.get(sPhysicalProductStateSelectable);
									sVPMRefCurrent = EnoviaResourceBundle.getStateI18NString(context,pgApolloConstants.POLICY_VPLM_SMB_DEFINITION ,sVPMRefCurrent, Locale.ENGLISH.getLanguage());				
									sVPMRefReleasePhase = (String)mapAPP.get(sPhysicalProductReleasePhaseSelectable);

									if(UIUtil.isNotNullAndNotEmpty(sVPMRefName))
									{
										if(i > 0)
										{
											sbOutput.append(pgApolloConstants.CONSTANT_STRING_TILDA);
										}
										sbOutput.append(sVPMRefName);
										sbOutput.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sVPMRefRevision);
										sbOutput.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sAPPName);
										sbOutput.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sAPPRevision);
										sbOutput.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sVPMRefModelType);
										sbOutput.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sVPMRefReleasePhase);										
										sbOutput.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sVPMRefCurrent);
									}								
								}
							}
						}						
					}									
				}
				else
				{
					sbOutput.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_AUTOMATION_ERROR_INVALID_PARAMETERS);
				}
			}			
			if(sbOutput.toString().isEmpty())
			{
				sbOutput.append(pgApolloConstants.STR_AUTOMATION_BLANK);
			}	
		}
		catch (Exception e)
		{
			loggerApolloTrace.error(e.getMessage(), e);
			sbOutput.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON ).append(e.getLocalizedMessage());
		}
		finally 
		{
			if(bContextPushed)
			{
				ContextUtil.popContext(context);
			}
			sOutput = sbOutput.toString();
			loggerWS.debug("Final Response : {}" , sOutput);
			String strEndTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) ;
			loggerWS.debug("Query APP or Physical Product Details ended : {} ", strEndTime);
			loggerWS.debug("******************************************************************************");
		}
		return Response.status(200).entity(sOutput).build();
	}
	
	
	/**
	 * Added in May 2022 CW
	 * Web service Method to copy existing model 
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/copyModel")
	public Response copyPhysicalProduct (@Context HttpServletRequest req) throws Exception {
		
		loggerWS.debug("Path : /fetchData/copyModel");
		
		matrix.db.Context context = null;
		StringList slURLParam;
		String sValidURLParam;
		int iURLParamSize = 0;

		StringList objectSelects = new StringList();
		objectSelects.add(DomainConstants.SELECT_NAME);
		objectSelects.add(DomainConstants.SELECT_ID);

		StringBuilder sbReturnMessages = new StringBuilder();
		String sNewObjectDetails;

		boolean isTransactionStarted = false;

		try 
		{				
			loggerWS.debug("******************************************************************************");
			loggerWS.debug("Path : /fetchData/copyModel");
			loggerWS.debug("Method: ExtractDataForDesignTool : copyModel");			
			
			// Get the user context
			if (Framework.isLoggedIn(req))
			{
				context = Framework.getContext(req.getSession(false));				
			}			
			
			String sUserName = DomainConstants.EMPTY_STRING;
			
			if(null != context)
			{
				sUserName = context.getUser();
			}
			
			String queryString = req.getQueryString();
			queryString =  java.net.URLDecoder.decode(queryString,"UTF-8");
			
			if(UIUtil.isNotNullAndNotEmpty(queryString)) 
			{
				loggerWS.debug("queryString  : {}" , queryString);
				slURLParam = StringUtil.split(queryString, pgApolloConstants.CONSTANT_STRING_AMPERSAND);
				sValidURLParam = slURLParam.get(0);
				slURLParam = StringUtil.split(sValidURLParam, pgApolloConstants.CONSTANT_STRING_PIPE);
				loggerWS.debug("slURLParam  : {}" , slURLParam);
				iURLParamSize = slURLParam.size();
				
				if(iURLParamSize > 0) 
				{	
					loggerWS.debug("context user : {}" , sUserName);
					
					String sObjectName = slURLParam.get(0);
					String sObjectRevision = slURLParam.get(1);					
					
					String sVPMRefObjectId = pgApolloCommonUtil.getObjectId(context, pgApolloConstants.TYPE_VPMREFERENCE, sObjectName, sObjectRevision);
					
					StringList slObjSelect = new StringList();
					slObjSelect.addElement(pgApolloConstants.SELECT_PHYSICAL_ID);
					slObjSelect.addElement(pgApolloConstants.SELECT_ATTRIBUTE_PGLPDORIGINATEDFROMGENERICMODEL);
					
					String sPhysicalId = DomainConstants.EMPTY_STRING;
					String sGenericModelAttVal = DomainConstants.EMPTY_STRING;
					

					if(UIUtil.isNotNullAndNotEmpty(sVPMRefObjectId))
					{	
						DomainObject doVPMRefObject = DomainObject.newInstance(context, sVPMRefObjectId);

						Map mpVPMRefDetails = doVPMRefObject.getInfo(context, slObjSelect);
						sPhysicalId = (String) mpVPMRefDetails.get(pgApolloConstants.SELECT_PHYSICAL_ID);
						sGenericModelAttVal = (String) mpVPMRefDetails.get(pgApolloConstants.SELECT_ATTRIBUTE_PGLPDORIGINATEDFROMGENERICMODEL);
						
						loggerWS.debug("Source Object Physical Id >> {}" , sPhysicalId);
						
						ContextUtil.startTransaction(context, true);
						isTransactionStarted = true;
						
						String sPayload = createPayloadToDuplicate(context, sPhysicalId);
						
						loggerWS.debug("sPayload >> {}" , sPayload);

						com.matrixone.json.JSONObject response = new DuplicateResource().executeDuplicate(context, sPayload);

						OperationResult operationResult = new OperationResult(new DefaultNLSService(context));
						operationResult.setJsonResult(response);
						Iterator resultIterator = operationResult.getResult().iterator();
						String sourceId;
						String targetId;
						String sNewClonedPhysicalProductId = DomainConstants.EMPTY_STRING;

						while (resultIterator.hasNext()) 
						{
							IResultItem resultItem = (IResultItem) resultIterator.next();
							sourceId = resultItem.getSourceID();
							if(sourceId.equals(sPhysicalId))
							{
								targetId = resultItem.getTargetID();
								sNewClonedPhysicalProductId = targetId;
								break;
							}

						}
						loggerWS.debug("New Cloned Physical Product Id >>>>>>>>>>>>>>> {} ", sNewClonedPhysicalProductId);
						if(UIUtil.isNotNullAndNotEmpty(sNewClonedPhysicalProductId))
						{
							DomainObject doClonedProduct = DomainObject.newInstance(context, sNewClonedPhysicalProductId);
							
							//Map attrMap = new HashMap();			
							//attrMap.put(pgApolloConstants.ATTRIBUTE_PGLPDORIGINATEDFROMGENERICMODEL, sGenericModelAttVal);
							//Add attribute PLMReference.V_DerivedFrom
							//Add attribute Model TYpe
							//doClonedProduct.setAttributeValues(context, attrMap);
							
							StringList slObjectSelect = new StringList();
							slObjectSelect.add(DomainConstants.SELECT_ID);
							slObjectSelect.add(DomainConstants.SELECT_TYPE);
							slObjectSelect.add(DomainConstants.SELECT_NAME);
							slObjectSelect.add(DomainConstants.SELECT_REVISION);
							
							Map clonedProductMap = doClonedProduct.getInfo(context, slObjectSelect);
							
							String sType = (String)clonedProductMap.get(DomainConstants.SELECT_TYPE);
							String sName = (String)clonedProductMap.get(DomainConstants.SELECT_NAME);
							String sRevision = (String)clonedProductMap.get(DomainConstants.SELECT_REVISION);						
							
							sNewObjectDetails = new StringBuilder(sType).append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sName).append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sRevision).toString();
							
							loggerWS.debug("New Object Details >>>>>>>>>>>>>>> {} ", sNewObjectDetails);
							
							sbReturnMessages.append(sNewObjectDetails);
						}						
					}					
				}		
			}
		} 
		catch (Exception e) 
		{
			if(isTransactionStarted)
			{						
				ContextUtil.abortTransaction(context);
				isTransactionStarted = false;
			}
			sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage());
			loggerApolloTrace.error(e.getMessage(), e);
		}
		finally
		{
			//Transaction ends
			if(isTransactionStarted)
			{
				ContextUtil.commitTransaction(context);
				isTransactionStarted = false;
			}
		}
		
		loggerWS.debug("Copy Model webservice ended-----------------");
		loggerWS.debug("******************************************************************************");
		
		return Response.status(200).entity(sbReturnMessages.toString()).build();
	}
	
	
	
	
	/**
	 * Method to prepare payload for Source Object to be duplicated
	 * @param ctx
	 * @param sPhysicalId
	 * @return
	 * @throws FrameworkException
	 */
	public static String createPayloadToDuplicate(matrix.db.Context ctx, String sPhysicalId) throws FrameworkException
	{
		JsonObjectBuilder payload = Json.createObjectBuilder();
		
		if(UIUtil.isNotNullAndNotEmpty(sPhysicalId))
		{
			StringList busSelects = new StringList();
			busSelects.add(DomainConstants.SELECT_ID);
			busSelects.add(pgApolloConstants.SELECT_PHYSICAL_ID);

			StringList relSelects = new StringList();
			relSelects.add(DomainConstants.SELECT_ID);
			relSelects.add(DomainConstants.SELECT_RELATIONSHIP_NAME);
			relSelects.add(pgApolloConstants.SELECT_REL_PHYSICAL_ID);

			DomainObject doCADObj = DomainObject.newInstance(ctx, sPhysicalId);

			MapList mlConnectedChildObjects = doCADObj.getRelatedObjects(ctx,	//context
					pgApolloConstants.RELATIONSHIP_VPMRepInstance+","+pgApolloConstants.RELATIONSHIP_VPMINSTANCE,// relationship pattern
					DomainConstants.QUERY_WILDCARD,	// type pattern
					busSelects,	// object selects
					relSelects,	// relationship selects
					false,// to direction
					true,// from direction
					(short) 1,// recursion level
					null,// object where clause
					null,	// relationship where clause
					0);// objects Limit		

			JsonArrayBuilder payloadSourceRelationshipArray = Json.createArrayBuilder();
			
			JsonArrayBuilder payloadDataArray = Json.createArrayBuilder();
			JsonObjectBuilder payloadSourcePhysicalProduct = Json.createObjectBuilder().add(pgApolloConstants.SELECT_PHYSICAL_ID, sPhysicalId).add(pgApolloConstants.KEY_ACTION, pgApolloConstants.KEY_DUPLICATE).add(pgApolloConstants.KEY_ISROOTNODE, true).add(pgApolloConstants.KEY_OPERATION, pgApolloConstants.KEY_DUPLICATE);
			
			if(null != mlConnectedChildObjects && !mlConnectedChildObjects.isEmpty())
			{
				Map mpT;
				String sRelName;
				String sRelPhysicalId;
				String sObjPhysicalId;

				for(int i=0 ; i< mlConnectedChildObjects.size() ; i++ )
				{
					mpT = (Map) mlConnectedChildObjects.get(i);

					sRelName = (String) mpT.get(DomainConstants.SELECT_RELATIONSHIP_NAME);
					sRelPhysicalId = (String) mpT.get(pgApolloConstants.SELECT_REL_PHYSICAL_ID);
					sObjPhysicalId = (String) mpT.get(pgApolloConstants.SELECT_PHYSICAL_ID);

					if(pgApolloConstants.RELATIONSHIP_VPMRepInstance.equalsIgnoreCase(sRelName))
					{
						payloadSourceRelationshipArray.add(Json.createObjectBuilder().add(pgApolloConstants.SELECT_PHYSICAL_ID, sRelPhysicalId).add(DomainConstants.SELECT_TYPE, sRelName).add(pgApolloConstants.KEY_OPERATION, pgApolloConstants.KEY_DUPLICATE));
						payloadDataArray.add(Json.createObjectBuilder().add(pgApolloConstants.SELECT_PHYSICAL_ID, sObjPhysicalId).add(pgApolloConstants.KEY_ISROOTNODE, false).add(pgApolloConstants.KEY_OPERATION, pgApolloConstants.KEY_DUPLICATE).add(pgApolloConstants.KEY_RELATIONS, Json.createArrayBuilder()));

					}			
					else
					{
						payloadSourceRelationshipArray.add(Json.createObjectBuilder().add(pgApolloConstants.SELECT_PHYSICAL_ID, sRelPhysicalId).add(DomainConstants.SELECT_TYPE, sRelName).add(pgApolloConstants.KEY_OPERATION, pgApolloConstants.KEY_REUSE));
						payloadDataArray.add(Json.createObjectBuilder().add(pgApolloConstants.SELECT_PHYSICAL_ID, sObjPhysicalId).add(pgApolloConstants.KEY_ISROOTNODE, false).add(pgApolloConstants.KEY_OPERATION, pgApolloConstants.KEY_REUSE).add(pgApolloConstants.KEY_RELATIONS, Json.createArrayBuilder()));
					}
					
				}				
				payloadSourcePhysicalProduct.add(pgApolloConstants.KEY_RELATIONS, payloadSourceRelationshipArray);				
			}
			
			payloadDataArray.add(payloadSourcePhysicalProduct);

			JsonArrayBuilder optionArray = Json.createArrayBuilder();
			optionArray.add(Json.createObjectBuilder().add(pgApolloConstants.KEY_KEYPARAMETER, "prefix").add(pgApolloConstants.KEY_NLSKEY, "Prefix:").add(DomainConstants.SELECT_TYPE, "text").add(pgApolloConstants.KEY_VALUEPARAMETER, "")); // If required we can pass prefix, currently its value is blank
			optionArray.add(Json.createObjectBuilder().add(pgApolloConstants.KEY_KEYPARAMETER, "wholeStructure").add(pgApolloConstants.KEY_NLSKEY, "Whole Structure").add(DomainConstants.SELECT_TYPE, "checkbox").add(pgApolloConstants.KEY_VALUEPARAMETER, false));
			optionArray.add(Json.createObjectBuilder().add(pgApolloConstants.KEY_KEYPARAMETER, "advanced").add(pgApolloConstants.KEY_NLSKEY, "Advanced").add(DomainConstants.SELECT_TYPE, "accordion").add(pgApolloConstants.KEY_VALUEPARAMETER, true).add(pgApolloConstants.KEY_USINGADVANCEDDUPLICATE, true));

			payload.add(pgApolloConstants.KEY_DATA, payloadDataArray);
			payload.add(pgApolloConstants.KEY_OPTIONS, optionArray);
			payload.add(pgApolloConstants.KEY_INCLUDE_DRAWINGS, false);
			
		}
		return payload.build().toString();
	}

}

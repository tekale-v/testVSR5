package com.png.apollo.designtool.getData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dassault_systemes.enovia.enterprisechangemgt.util.ChangeUtil;
import com.dassault_systemes.platform.restServices.RestService;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.servlet.Framework;
import com.pg.v3.custom.pgV3Constants;
import com.png.apollo.pgApolloCommonUtil;
import com.png.apollo.pgApolloConstants;
import com.png.apollo.pgApolloWeightConversionUtility;
import com.png.apollo.sync.ebom.GenerateEBOMService;

import matrix.db.Signature;
import matrix.db.SignatureList;
import matrix.util.Pattern;
import matrix.util.StringList;

@Path("/CoreMaterial")
public class LayeredProductCoreMaterialUtility extends RestService {
	
	 private static final Logger loggerWS = LoggerFactory.getLogger("APOLLOWS");
	 private static final org.apache.log4j.Logger loggerApolloTrace = org.apache.log4j.Logger.getLogger(LayeredProductCoreMaterialUtility.class);
	/*
	 * This webservice is used to fetch Target Value based on RMP Name, Characteristic Name and Characteristic Specifics
	 */
	
	@GET
	@Path("/fetchRMPCharTargetValue")
	public Response fetchRMPTargetValue(@Context HttpServletRequest request)throws Exception{
		
		StringBuilder sbReturnMessage = new StringBuilder();
		matrix.db.Context context = null;
		String[] arrayURLParam = new String[20];

		boolean bContextPushed = false;	
		try
		{			
			// Get the user context
			if (Framework.isLoggedIn( request)) {
				context = Framework.getContext( request.getSession(false));
			}
			
			//Used in Automation webservice - Push context is needed to get Target values. Context user will not always get access to characteristics associated with RMP. 
			ContextUtil.pushContext(context,pgApolloConstants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
			bContextPushed = true;			
			loggerWS.debug("******************************************************************************");
			loggerWS.debug("Path : /CoreMaterial/fetchRMPCharTargetValue");
			loggerWS.debug("Method: LayeredProductCoreMaterialUtility : fetchRMPTargetValue");
			loggerWS.debug("\n\n New Data fetching started :{} ", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) );
			
			String queryString = request.getQueryString();
			queryString =  java.net.URLDecoder.decode(queryString,"UTF-8");
			
			loggerWS.debug("\n\n Query String : {}",queryString);
			
			if (UIUtil.isNotNullAndNotEmpty(queryString)) 
			{
				queryString=queryString.trim();
				queryString =queryString.replaceAll("%20", " ");				
				arrayURLParam = queryString.split("\\|");				
				if (arrayURLParam.length >= 2) 
				{
					String strRMName = arrayURLParam[0];
					String strRMCharacteristic = arrayURLParam[1];
					String sCharSpecific = DomainConstants.EMPTY_STRING;
					if(arrayURLParam.length >= 3) {
						sCharSpecific = arrayURLParam[2];
					}
					//Apollo 2018x.5 Requirement 34689- Start
					Map mpRMP = new HashMap();
					mpRMP = pgApolloCommonUtil.getRMPIdForSync(context,strRMName);
					String strRmId = (String)mpRMP.get(DomainConstants.SELECT_ID);
					//Apollo 2018x.5 Requirement 34689- End
					loggerWS.debug("\n\n RMP ID :{} " ,strRmId);
					if(UIUtil.isNotNullAndNotEmpty(strRmId)) 
					{
						String strTargetValue = pgApolloWeightConversionUtility.getTargetValue(context, strRmId, strRMCharacteristic ,sCharSpecific, true);
						if(UIUtil.isNullOrEmpty(strTargetValue))
						{
							strTargetValue = pgApolloConstants.STR_AUTOMATION_BLANK;
						}
						//Apollo 2018x.3 32781  Webservice to get RMP Target Values - Modification- Start
						strTargetValue =getCatiaFormatTarget(context ,strTargetValue);
						//Apollo 2018x.3 32781 Webservice to get RMP Target Values - Modification- End
						sbReturnMessage.append(strTargetValue);
						loggerWS.debug("\n\n target weight : {}" ,strTargetValue);
					}
					else
					{
						sbReturnMessage.append(pgApolloConstants.STR_RMP_NOT_EXIST);
					}					
				}
				else 
				{
					sbReturnMessage.append(pgApolloConstants.STR_AUTOMATION_ERROR_INVALID_PARAMETERS);
				}
			}
			else 
			{
				sbReturnMessage.append(pgApolloConstants.STR_AUTOMATION_ERROR_INVALID_PARAMETERS);				
			}			
			loggerWS.debug("\n\nFinal Response---->{}" ,sbReturnMessage);
			loggerWS.debug("\n******************************************************\n");
		}
		catch (Exception e) 
		{
			sbReturnMessage.append(pgApolloConstants.STR_ERROR + pgApolloConstants.CONSTANT_STRING_COLON+ e.getLocalizedMessage());
			loggerApolloTrace.error(e.getMessage(), e);
		}
		finally 
		{
			if(bContextPushed)
			{
				ContextUtil.popContext(context);
			}					
		}		
		return Response.status(200).entity(sbReturnMessage.toString()).build();		
	}
		
	//Apollo 2018x.3 32781 Webservice to get RMP Target Values - Modification- Start
	/**
	 * This method will convert target value n Catia format.
	 * @param context
	 * @param strTarget
	 * @return
	 * @throws Exception
	 */
	private String getCatiaFormatTarget(matrix.db.Context context , String strTarget)throws Exception{
		String strReturnString = DomainConstants.EMPTY_STRING;
		try {
			GenerateEBOMService ebomservice = new GenerateEBOMService();
			Map catiashortNameMap =ebomservice.getShortNameCATIAUoMMap(context);
			StringList strTargetValueList =FrameworkUtil.split(strTarget,pgApolloConstants.CONSTANT_STRING_PIPE );
			if(strTargetValueList.size()==2) {
				String strUOM =strTargetValueList.get(1);
				String strValue =strTargetValueList.get(0);
				Set ketSet =catiashortNameMap.keySet();
				Iterator ketSetItr = ketSet.iterator();
				String strCatiaUOM =strUOM;
				String strTempCatiaUOM = null;
				String strTempUOMValue = null;
				while(ketSetItr.hasNext()) {
					strTempCatiaUOM =(String)ketSetItr.next();
					strTempUOMValue =(String)catiashortNameMap.get(strTempCatiaUOM);
					if(strTempUOMValue.equalsIgnoreCase(strUOM)) {
						strCatiaUOM =strTempCatiaUOM;
						break;
					}
					
				}
				strReturnString =strValue+strCatiaUOM;				
			}
			else {
				strReturnString =strTarget;
			}
		}
		catch (Exception ex) {
			loggerApolloTrace.error(ex.getMessage(), ex);
			throw ex;
		}
		return strReturnString;
	}
	
	//Modified for Apollo 2018x.5 ALM 34491,35062,34606,34672 A10-505 
	/**
	 * Method to Fetch RMP information - associated core material and material functions
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/fetchMaterialInfo")
	public Response fetchCoreMaterial(@Context HttpServletRequest request) throws Exception
	{	
		matrix.db.Context context = null;		
		StringBuilder strReturnMessageBuf = new StringBuilder();
		String strDate = new SimpleDateFormat("yyyyMMdd").format(new Date());

		boolean bContextPushed =false;
		String[] URLParam = new String[20];
		String strRMPName = DomainConstants.EMPTY_STRING;
		String strCoreMaterialName = DomainConstants.EMPTY_STRING;
		String strCoreMaterialRevision = DomainConstants.EMPTY_STRING;
		String strLatestRMPObjectId = DomainConstants.EMPTY_STRING;
		String strMaterialFunction = DomainConstants.EMPTY_STRING;
		String strCoreMaterial = DomainConstants.EMPTY_STRING;
		String strMaterialRestrictionComment = DomainConstants.EMPTY_STRING;
		try 
		{
			loggerWS.debug("******************************************************************************");
			loggerWS.debug("Path : /CoreMaterial/fetchMaterialInfo");
			loggerWS.debug("Method: LayeredProductCoreMaterialUtility : fetchCoreMaterial");
			loggerWS.debug("\n\n New Data fetching started : {}" ,new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) );

			// Get the user context
			if (Framework.isLoggedIn((HttpServletRequest) request)) {
				context = Framework.getContext((HttpSession) request.getSession(false));				
			}

			loggerWS.debug("\ncontext  : {}" , context.getUser());

			String queryString = request.getQueryString();
			queryString =  java.net.URLDecoder.decode(queryString,"UTF-8");
			loggerWS.debug("\n\nQueryString  : {}" , queryString);
			
			String strError = DomainConstants.EMPTY_STRING;
			String strVPMRefId = DomainConstants.EMPTY_STRING;
			String strVPMRefName = DomainConstants.EMPTY_STRING;
			String strVPMRefRevision = DomainConstants.EMPTY_STRING;
			String strAPPObjectId = DomainConstants.EMPTY_STRING;
			
			boolean sendPreferredMaterial = true; 
			if (UIUtil.isNotNullAndNotEmpty(queryString)) 
			{
				URLParam = queryString.split("&");
				if (URLParam.length > 0) 
				{
					/*
					 * QueryString : <VPMReferenceName>|<VPMReferenceRevision>&<RMPName>
					 * or
					 * QueryString : <VPMReferenceName>|<VPMReferenceRevision>&<RMPName>&<CoreMaterialName>|<CoreMaterialRevision>
					 * 	or
					 * QueryString : <VPMReferenceName>|<VPMReferenceRevision>|OLD&<RMPName>&<CoreMaterialName>|<CoreMaterialRevision>
					 */
					strVPMRefName = URLParam[0];
					if(strVPMRefName!=null && !strVPMRefName.isEmpty())
					{
						String[] strVPMRefDetails = strVPMRefName.split("\\|");//<VPMReferenceName>|<VPMReferenceRevision>
						strVPMRefName = strVPMRefDetails[0];//<VPMReferenceName>
						if (strVPMRefDetails.length > 1) 
						{
							strVPMRefRevision = strVPMRefDetails[1];//<VPMReferenceRevision>
						}
						if(UIUtil.isNotNullAndNotEmpty(strVPMRefName) && UIUtil.isNotNullAndNotEmpty(strVPMRefRevision))
						{
							strVPMRefId = pgApolloCommonUtil.getObjectId(context, pgApolloConstants.TYPE_VPMREFERENCE, strVPMRefName, strVPMRefRevision);	
						}
						if (strVPMRefDetails.length > 2) 
						{
							sendPreferredMaterial = false;
						}
						
					}
					
					loggerWS.debug("\nstrVPMRefName  : {} ,strVPMRefRevision : {}" ,strVPMRefName,strVPMRefRevision);
					
					if(UIUtil.isNotNullAndNotEmpty(strVPMRefId))
					{
						strAPPObjectId = pgApolloCommonUtil.fetchAPPObjectId(context, strVPMRefId);
						loggerWS.debug("\nstrAPPObjectId  : {}" ,strAPPObjectId);
						if(UIUtil.isNotNullAndNotEmpty(strAPPObjectId))
						{		
							if(URLParam.length > 1)
							{						
								strRMPName = URLParam[1];//<RMPName>						
								if(strRMPName!=null && !strRMPName.isEmpty())
								{
									String[] strRMPDetails = strRMPName.split("\\|");
									strRMPName = strRMPDetails[0];//<RMPName>			
									
									loggerWS.debug("\nstrRMPName  : {}" ,strRMPName);
									
									//To check validation on RMP 
									MapList mlStacking = new MapList(1);
									Map mpMaterial = new HashMap();
									mpMaterial.put(pgApolloConstants.KEY_PLYMATERIAL, strRMPName);									
									if(URLParam.length > 2)
									{
										mpMaterial.put(pgApolloConstants.KEY_COREMATERIAL, URLParam[2]);								
									}								
									mlStacking.add(mpMaterial);							
									mlStacking = pgApolloCommonUtil.validateRMPDataForSync(context, strAPPObjectId, mlStacking, false, true, false, true);
									
									//Pushing the context to User Agent
									ContextUtil.pushContext(context,pgApolloConstants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
									bContextPushed = true;
									
									loggerWS.debug("\ncontext  : {}" ,context.getUser());
									
									if(null != mlStacking && !mlStacking.isEmpty())
									{
										mpMaterial = (Map)mlStacking.get(0);
										if(mpMaterial.containsKey(pgApolloConstants.STR_ERROR))
										{									
											strError = (String) mpMaterial.get(pgApolloConstants.STR_ERROR);
											loggerWS.debug("\nstrError : {}" ,strError);
										}
										
										//Get latest revision of RMP
										if(mpMaterial.containsKey(pgApolloConstants.SELECT_ID))
										{									
											strLatestRMPObjectId = (String) mpMaterial.get(pgApolloConstants.SELECT_ID);
											
											//get Material Restriction Comment value if Material Restriction attribute value of RMP is 'Warning'
											if(mpMaterial.containsKey(pgApolloConstants.STR_WARNING) && UIUtil.isNotNullAndNotEmpty((String)mpMaterial.get(pgApolloConstants.STR_WARNING)))
											{												
												strMaterialRestrictionComment = (String)mpMaterial.get(pgApolloConstants.STR_WARNING);
											}
											loggerWS.debug("\nstrMaterialRestrictionComment  : {}" ,strMaterialRestrictionComment);
											
											//if QueryString = <VPMReferenceName>|<VPMReferenceRevision>&<RMPName>
											if(URLParam.length == 2)
											{
												//get latest revision of Core Material details by using RMP
												if(sendPreferredMaterial)
												{
													strCoreMaterial = getConnectedPreferredCoreMaterial(context, strLatestRMPObjectId, DomainConstants.EMPTY_STRING, strRMPName);
												}
												else
												{
													strCoreMaterial = getConnectedCoreMaterial(context, strLatestRMPObjectId, DomainConstants.EMPTY_STRING, strRMPName);
												}
												loggerWS.debug("\nstrCoreMaterial  : {}" , strCoreMaterial);
												
												//checking error in Core Material
												if(strCoreMaterial.contains(pgApolloConstants.STR_ERROR))
												{
													//Show only one error message either RMP Validation Error or Core Material Error
													if(UIUtil.isNotNullAndNotEmpty(strError))
													{
														strReturnMessageBuf.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(strError);													
													}
													else
													{
														strReturnMessageBuf.append(strCoreMaterial);
													}
												}
												else
												{									
													//Core Material details : T|N|R|<NotConnected or Connected>
													strReturnMessageBuf.append(strCoreMaterial);
												}												
												
												//Get Material Functions associated with RMP Object
												strMaterialFunction = getAssociatedMaterialFunction(context, strLatestRMPObjectId);
												loggerWS.debug("\nstrMaterialFunction  : {}" ,strMaterialFunction);													
												if(UIUtil.isNotNullAndNotEmpty(strMaterialFunction))
												{
													strReturnMessageBuf.append("~"); 
													strReturnMessageBuf.append(strMaterialFunction);
												}
												else
												{
													strReturnMessageBuf.append("~"); 
													strReturnMessageBuf.append(pgApolloConstants.STR_AUTOMATION_BLANK);
												}
												if(UIUtil.isNotNullAndNotEmpty(strCoreMaterial)&& strCoreMaterial.contains(pgApolloConstants.STR_NO_COREMATERIAL_ASSOCIATED	)) 
												{
													DomainObject objRMP =DomainObject.newInstance(context, strLatestRMPObjectId);
													String strRMPtitle =objRMP.getInfo(context ,DomainConstants.SELECT_ATTRIBUTE_TITLE);
													loggerWS.debug("\nTitle   : {}" ,strRMPtitle);
													if(UIUtil.isNotNullAndNotEmpty(strRMPtitle))
													{
														strRMPtitle=FrameworkUtil.findAndReplace(strRMPtitle, pgApolloConstants.CONSTANT_STRING_TILDA, pgApolloConstants.STR_TILDA_CHAR);
														strReturnMessageBuf.append("~"); 
														strReturnMessageBuf.append(strRMPtitle);
													}
													else
													{
														strReturnMessageBuf.append("~"); 
														strReturnMessageBuf.append(pgApolloConstants.STR_AUTOMATION_BLANK);
													}										
			
												}
												
												if(!strCoreMaterial.contains(pgApolloConstants.STR_ERROR))
												{
													if(UIUtil.isNotNullAndNotEmpty(strError))
													{
														//Adding Material Restriction Comment if Material Restriction attribute value of RMP is 'Warning'	
														strReturnMessageBuf.append("~");
														strReturnMessageBuf.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(strError);														
													}
													else if(UIUtil.isNotNullAndNotEmpty(strMaterialRestrictionComment))
													{
														//Adding Material Restriction Comment if Material Restriction attribute value of RMP is 'Warning'	
														strReturnMessageBuf.append("~");
														strReturnMessageBuf.append(pgApolloConstants.STR_MATERIAL_RESTRICTION_WARNING);
														strReturnMessageBuf.append(strMaterialRestrictionComment);
													}
												}												
											}
											else if(URLParam.length > 2)
											{
												if(UIUtil.isNotNullAndNotEmpty(strError))
												{
													//Return error message
													strReturnMessageBuf.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(strError);														
												}
												else
												{
													//If QueryString : <VPMReferenceName>|<VPMReferenceRevision>&<RMPName>&<CoreMaterialName>|<CoreMaterialRevision>
													strCoreMaterialName = URLParam[2];
													if(UIUtil.isNotNullAndNotEmpty(strCoreMaterialName))
													{
														String[] strCoreMaterialDetail = strCoreMaterialName.split("\\|");
														strCoreMaterialName = strCoreMaterialDetail[0];//<CoreMaterialName>								
														if (strCoreMaterialDetail.length > 1) 
														{
															strCoreMaterialRevision = strCoreMaterialDetail[1];//<CoreMaterialRevision>
														}
																												
														loggerWS.debug("\nstrCoreMaterialName  : {} ,strCoreMaterialRevision : {}" ,strCoreMaterialName, strCoreMaterialRevision);												
														
														//Connect Core Material with RMP and return Core Material and Material Function details								
														Map mapConnectInfo = new HashMap();
														mapConnectInfo.put(DomainConstants.SELECT_ID, strLatestRMPObjectId);
														mapConnectInfo.put("CoreMaterialName", strCoreMaterialName);
														mapConnectInfo.put("CoreMaterialRevision", strCoreMaterialRevision);
														
														strCoreMaterial = connectCoreMaterialToRMP(context, mapConnectInfo);
														loggerWS.debug("\nstrCoreMaterial  : {}" ,strCoreMaterial);
														
														//checking error in Core Material
														if(strCoreMaterial.contains(pgApolloConstants.STR_ERROR))
														{
															strReturnMessageBuf.append(strCoreMaterial);
														}								
														else
														{
															strReturnMessageBuf.append(strCoreMaterial);
															
															//Get Material Functions associated with RMP Object
															strMaterialFunction = getAssociatedMaterialFunction(context, strLatestRMPObjectId);
															loggerWS.debug("\nstrMaterialFunction  : {}" ,strMaterialFunction);
															
															if(UIUtil.isNotNullAndNotEmpty(strMaterialFunction))
															{
																strReturnMessageBuf.append("~");
																strReturnMessageBuf.append(strMaterialFunction);
															}
															else
															{
																strReturnMessageBuf.append("~"); 
																strReturnMessageBuf.append(pgApolloConstants.STR_AUTOMATION_BLANK);
															}
															if(UIUtil.isNotNullAndNotEmpty(strMaterialRestrictionComment))
															{		
																//Adding Material Restriction Comment if Material Restriction attribute value of RMP is 'Warning'
																strReturnMessageBuf.append("~");
																strReturnMessageBuf.append(pgApolloConstants.STR_MATERIAL_RESTRICTION_WARNING);
																strReturnMessageBuf.append(strMaterialRestrictionComment);
															}
														}
													}
											    }
											}
										}
										else if(UIUtil.isNotNullAndNotEmpty(strError))
										{												
												//Return error message
												strReturnMessageBuf.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(strError);																								
										}
										loggerWS.debug("\nstrReturnMessageBuf  : {}" ,strReturnMessageBuf);
									}
								}															
							}
						}
						else
						{
							strReturnMessageBuf.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_ERROR_NOAPP);
						}
					}
					else
					{
						strReturnMessageBuf.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_ERROR_NOOBJ);
					}
				}
			}
		}				
		catch(Exception ex)
		{
			loggerApolloTrace.error(ex.getMessage(), ex);
			strReturnMessageBuf = new StringBuilder();
			strReturnMessageBuf.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(ex.getLocalizedMessage());		
		}
		finally 
		{
			loggerWS.debug("\n\n Final Response -->{}" ,strReturnMessageBuf);
			loggerWS.debug("\n****Data Sent****{}" ,new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date())  );
			if(bContextPushed){
				ContextUtil.popContext(context);
			}
		}
		return Response.status(200).entity(strReturnMessageBuf.toString()).build();
	}

	//Modified for Apollo 2018x.5 ALM 34491,35062,34606,34672 A10-505 
	/**
	 * Method to get latest revision of Core Material by using pngiMaterialCore.pngRMPGCAS attribute and PLMEntity.V_description on Core Material
	 * pngiMaterialCore.pngRMPGCAS attribute stores RMP GCAS number and PLMEntity.V_description stores LaminateFunction
	 * @param context
	 * @param strLatestRMPObjectId
	 * @param strLaminateFunction
	 * @param fetchUpdateRMPDataLog
	 * @param strRMPName
	 * @return String
	 * @throws Exception
	 */
	private String getConnectedCoreMaterial(matrix.db.Context context, String strLatestRMPObjectId, String strLaminateFunction, String strRMPName) throws Exception 
	{
		StringBuffer sbFinalOutput = new StringBuffer();
		try 
		{			
			int iSizeCoreMaterials = 0;
			MapList mlCoreMaterialList = new MapList();
			StringBuffer sbCoreMaterialInfo = new StringBuffer();
			Map mRelatedMaterial = null;	
			String strMaterialType = DomainConstants.EMPTY_STRING;		
			String strMaterialName = DomainConstants.EMPTY_STRING;		
			String strMaterialRevision = DomainConstants.EMPTY_STRING;
			String strMaterialCurrent = DomainConstants.EMPTY_STRING;
			
			if(UIUtil.isNotNullAndNotEmpty(strRMPName) && UIUtil.isNotNullAndNotEmpty(strLatestRMPObjectId))
			{
				
				String strSelToPgRelatedMaterialForID = new StringBuilder().append(pgApolloConstants.CONSTANT_STRING_SELECT_TO).append(pgApolloConstants.RELATIONSHIP_PGRELATEDMATERIAL).append("|from.id==").append(strLatestRMPObjectId).append(pgApolloConstants.CONSTANT_STRING_SELECT_CLOSE_BRACKET).toString();
				String strSelToPgRelatedMaterial = new StringBuilder().append(pgApolloConstants.CONSTANT_STRING_SELECT_TO).append(pgApolloConstants.RELATIONSHIP_PGRELATEDMATERIAL).append(pgApolloConstants.CONSTANT_STRING_SELECT_CLOSE_BRACKET).toString();
				
				StringList objectSelects = new StringList();
				objectSelects.add(DomainConstants.SELECT_ID);
				objectSelects.add(DomainConstants.SELECT_TYPE);
				objectSelects.add(DomainConstants.SELECT_NAME);
				objectSelects.add(DomainConstants.SELECT_REVISION);
				objectSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PGRMPGCAS);
				objectSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_V_DESCRIPTION);
				objectSelects.add(strSelToPgRelatedMaterialForID);
				objectSelects.add(DomainConstants.SELECT_CURRENT);
				
				if(UIUtil.isNullOrEmpty(strLaminateFunction))
				{
					strLaminateFunction = DomainConstants.EMPTY_STRING;
				}
	
				StringBuffer sbWhereClause = new StringBuffer();
				sbWhereClause.append(pgApolloConstants.SELECT_ATTRIBUTE_PGRMPGCAS).append("=='").append(strRMPName).append("'");				
				sbWhereClause.append(" && ");
				sbWhereClause.append(pgApolloConstants.SELECT_ATTRIBUTE_V_DESCRIPTION).append("~~'").append(strLaminateFunction).append("'");// Defect 37441 Apollo 18x.5_dec Changes
				
				loggerWS.debug("\nWhereClause  : {}" ,sbWhereClause.toString());

				//Find out latest revision of Core Material by using pngiMaterialCore.pngRMPGCAS attribute and description on Core Material.
				mlCoreMaterialList = DomainObject.findObjects(context,						//context
												pgApolloConstants.TYPE_DSC_MATREF_REF_CORE,	//type pattern
												DomainConstants.QUERY_WILDCARD,				//vault pattern
												sbWhereClause.toString(),					//where Clause
												objectSelects);								//object Select
				
				loggerWS.debug("\nmlCoreMaterialList  : {}" ,mlCoreMaterialList);
				
				HashSet hsCoreMaterialNames = new HashSet();
				if(null!=mlCoreMaterialList && !mlCoreMaterialList.isEmpty())
				{
					StringList slCoreMaterialNames = new ChangeUtil().getStringListFromMapList(mlCoreMaterialList, DomainConstants.SELECT_NAME);
					hsCoreMaterialNames.addAll(slCoreMaterialNames);
					iSizeCoreMaterials = hsCoreMaterialNames.size();
					if(iSizeCoreMaterials > 1)
					{
						//More than one Core Material Found 
						sbFinalOutput.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_ERROR_COREMATERIAL_MULTIPLE_OBJECT);
						return sbFinalOutput.toString();
					}
					else
					{	
						//Core Material found
						mlCoreMaterialList.sort(DomainConstants.SELECT_REVISION, pgApolloConstants.STR_DESCENDING, pgApolloConstants.STR_STRING); //sorting revision in descending order
						mRelatedMaterial = (Map) mlCoreMaterialList.get(0);
						strMaterialType = (String)mRelatedMaterial.get(DomainConstants.SELECT_TYPE);
						strMaterialName = (String)mRelatedMaterial.get(DomainConstants.SELECT_NAME);
						strMaterialRevision = (String)mRelatedMaterial.get(DomainConstants.SELECT_REVISION);
						strMaterialCurrent = (String)mRelatedMaterial.get(DomainConstants.SELECT_CURRENT);
						sbCoreMaterialInfo.append(strMaterialType).append(pgApolloConstants.CONSTANT_STRING_PIPE).append(strMaterialName).append(pgApolloConstants.CONSTANT_STRING_PIPE).append(strMaterialRevision);
						if(mRelatedMaterial.containsKey(strSelToPgRelatedMaterial) && pgApolloConstants.STR_TRUE_FLAG.equalsIgnoreCase((String)mRelatedMaterial.get(strSelToPgRelatedMaterial)))
						{
							//Core Material found and it is connected to RMP object
							sbCoreMaterialInfo.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(pgApolloConstants.STR_CONNECTED);
						}
						else
						{
							//Core Material found, but it is not connected to RMP object
							sbCoreMaterialInfo.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(pgApolloConstants.STR_NOTCONNECTED);
						}
						sbCoreMaterialInfo.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(strMaterialCurrent);//Adding current state of Core Material
						sbFinalOutput.append(sbCoreMaterialInfo.toString());
					}
				} 
				else
				{ 	
					//No Core Material associated with RMP
					sbFinalOutput.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_NO_COREMATERIAL_ASSOCIATED);
				}
			}
		}
		catch(Exception e)
		{
			loggerApolloTrace.error(e.getMessage(), e);
			sbFinalOutput.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage());
		}
		return sbFinalOutput.toString();
	}
	
	
	
	/**
	 * Method to get latest revision of Core Material by using pngiMaterialCore.pngRMPGCAS attribute and PLMEntity.V_description on Core Material
	 * pngiMaterialCore.pngRMPGCAS attribute stores RMP GCAS number and PLMEntity.V_description stores LaminateFunction
	 * @param context
	 * @param strLatestRMPObjectId
	 * @param strLaminateFunction
	 * @param fetchUpdateRMPDataLog
	 * @param strRMPName
	 * @return String
	 * @throws Exception
	 */
	private String getConnectedPreferredCoreMaterial(matrix.db.Context context, String strLatestRMPObjectId, String strLaminateFunction, String strRMPName) throws Exception 
	{
		StringBuffer sbFinalOutput = new StringBuffer();
		try 
		{			
			MapList mlCoreMaterialList;
			StringBuilder sbCoreMaterialInfo = new StringBuilder();
			Map mRelatedMaterial = null;	
			String strMaterialType;		
			String strMaterialName;		
			String strMaterialRevision;
			String strMaterialCurrent;
			
			if(UIUtil.isNotNullAndNotEmpty(strRMPName) && UIUtil.isNotNullAndNotEmpty(strLatestRMPObjectId))
			{				
				String strSelToPgRelatedMaterialForID = new StringBuilder().append(pgApolloConstants.CONSTANT_STRING_SELECT_TO).append(pgApolloConstants.RELATIONSHIP_PGRELATEDMATERIAL).append("|from.id==").append(strLatestRMPObjectId).append(pgApolloConstants.CONSTANT_STRING_SELECT_CLOSE_BRACKET).toString();
				String strSelToPgRelatedMaterial = new StringBuilder().append(pgApolloConstants.CONSTANT_STRING_SELECT_TO).append(pgApolloConstants.RELATIONSHIP_PGRELATEDMATERIAL).append(pgApolloConstants.CONSTANT_STRING_SELECT_CLOSE_BRACKET).toString();
				
				StringList objectSelects = new StringList();
				objectSelects.add(DomainConstants.SELECT_ID);
				objectSelects.add(DomainConstants.SELECT_TYPE);
				objectSelects.add(DomainConstants.SELECT_NAME);
				objectSelects.add(DomainConstants.SELECT_REVISION);
				objectSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PGRMPGCAS);
				objectSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_V_DESCRIPTION);
				objectSelects.add(strSelToPgRelatedMaterialForID);
				objectSelects.add(DomainConstants.SELECT_CURRENT);
				
				StringList relSelects = new StringList();
				relSelects.add(DomainRelationship.SELECT_ID);
				
				if(UIUtil.isNullOrEmpty(strLaminateFunction))
				{
					strLaminateFunction = DomainConstants.EMPTY_STRING;
				}
				
				StringBuffer sbWhereClause = new StringBuffer();
				sbWhereClause.append(pgApolloConstants.SELECT_ATTRIBUTE_V_DESCRIPTION).append("~~'").append(strLaminateFunction).append("'");
				
				sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE);

				sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_DOUBLE_AMPERSAND);
				
				sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE);
			
				sbWhereClause.append(pgApolloConstants.SELECT_ATTRIBUTE_PGRMPGCAS).append(pgApolloConstants.CONSTANT_STRING_DOUBLE_EQUAL).append("'").append(strRMPName).append("'");				
	
				DomainObject doRMP = DomainObject.newInstance(context, strLatestRMPObjectId);
				
				MapList mlNewRevCoreMaterials = doRMP.getRelatedObjects(context,							//context
						pgApolloConstants.RELATIONSHIP_PGRELATEDMATERIAL,	//relationship pattern
						pgApolloConstants.TYPE_DSC_MATREF_REF_CORE,			//type pattern
						objectSelects,									//object Select
						relSelects,											//rel Select
						false,												//get To
						true,												//get From
						(short)1,											//recurse level
						sbWhereClause.toString(),												//object where Clause
						null,												//rel where Clause
						0);													// object limit	
				loggerWS.debug("mlNewRevCoreMaterials  : {}" ,mlNewRevCoreMaterials);

				if(null != mlNewRevCoreMaterials && !mlNewRevCoreMaterials.isEmpty())
				{
					mlNewRevCoreMaterials.addSortKey(DomainConstants.SELECT_CURRENT, pgApolloConstants.STR_DESCENDING, pgApolloConstants.STR_STRING);
					mlNewRevCoreMaterials.addSortKey(DomainConstants.SELECT_NAME, pgApolloConstants.STR_ASCENDING, pgApolloConstants.STR_STRING);
					mlNewRevCoreMaterials.sort();
					
					Map mpTemp = (Map) mlNewRevCoreMaterials.get(0);
					String strCoreMaterialName = (String) mpTemp.get(DomainConstants.SELECT_NAME);
					
					MapList mlCMRevisionChain = DomainObject.findObjects(context,									//Context
							pgApolloConstants.TYPE_DSC_MATREF_REF_CORE, 				//	Object Type 
							strCoreMaterialName, 	//	Object Name
							DomainConstants.QUERY_WILDCARD,		//	Object Revision
							null,		//	Owner 
							DomainConstants.QUERY_WILDCARD,				//	Vault
							null,																	//	Where Clause
							false,																//	Expand type
							objectSelects);													//	Object selects
					loggerWS.debug("mlCMRevisionChain  : {}" ,mlCMRevisionChain);

					if(null != mlCMRevisionChain && !mlCMRevisionChain.isEmpty())
					{
						mlCMRevisionChain.sort(DomainConstants.SELECT_REVISION, pgApolloConstants.STR_DESCENDING, pgApolloConstants.STR_STRING); //sorting revision in descending order
						mRelatedMaterial = (Map) mlCMRevisionChain.get(0);
					}
				}
				else
				{
					sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE).append(pgApolloConstants.CONSTANT_STRING_DOUBLE_AMPERSAND).append(pgApolloConstants.CONSTANT_STRING_SPACE);
					sbWhereClause.append(pgApolloConstants.SELECT_ATTRIBUTE_PGRMPGCAS).append("=='").append(strRMPName).append("'");			
				
					loggerWS.debug("WhereClause  : {}" , sbWhereClause);
	
					//Find out latest revision of Core Material by using pngiMaterialCore.pngRMPGCAS attribute and description on Core Material.
					mlCoreMaterialList = DomainObject.findObjects(context,						//context
													pgApolloConstants.TYPE_DSC_MATREF_REF_CORE,	//type pattern
													DomainConstants.QUERY_WILDCARD,				//vault pattern
													sbWhereClause.toString(),					//where Clause
													objectSelects);								//object Select
					
					loggerWS.debug("mlCoreMaterialList  : {}" ,mlCoreMaterialList);
					
					if(null != mlCoreMaterialList && !mlCoreMaterialList.isEmpty())
					{
						mlCoreMaterialList.addSortKey(DomainConstants.SELECT_CURRENT, pgApolloConstants.STR_DESCENDING, pgApolloConstants.STR_STRING);
						mlCoreMaterialList.addSortKey(DomainConstants.SELECT_NAME, pgApolloConstants.STR_ASCENDING, pgApolloConstants.STR_STRING);
						mlCoreMaterialList.addSortKey(DomainConstants.SELECT_REVISION, pgApolloConstants.STR_DESCENDING, pgApolloConstants.STR_STRING);
						mlCoreMaterialList.sort();						
						mRelatedMaterial = (Map) mlCoreMaterialList.get(0);
					}
				
				}				
				if(null!=mRelatedMaterial && !mRelatedMaterial.isEmpty())
				{
						//Core Material found
						strMaterialType = (String)mRelatedMaterial.get(DomainConstants.SELECT_TYPE);
						strMaterialName = (String)mRelatedMaterial.get(DomainConstants.SELECT_NAME);
						strMaterialRevision = (String)mRelatedMaterial.get(DomainConstants.SELECT_REVISION);
						strMaterialCurrent = (String)mRelatedMaterial.get(DomainConstants.SELECT_CURRENT);
						sbCoreMaterialInfo.append(strMaterialType).append(pgApolloConstants.CONSTANT_STRING_PIPE).append(strMaterialName).append(pgApolloConstants.CONSTANT_STRING_PIPE).append(strMaterialRevision);
						if(mRelatedMaterial.containsKey(strSelToPgRelatedMaterial) && pgApolloConstants.STR_TRUE_FLAG.equalsIgnoreCase((String)mRelatedMaterial.get(strSelToPgRelatedMaterial)))
						{
							//Core Material found and it is connected to RMP object
							sbCoreMaterialInfo.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(pgApolloConstants.STR_CONNECTED);
						}
						else
						{
							//Core Material found, but it is not connected to RMP object
							sbCoreMaterialInfo.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(pgApolloConstants.STR_NOTCONNECTED);
						}
						sbCoreMaterialInfo.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(strMaterialCurrent);//Adding current state of Core Material
						sbFinalOutput.append(sbCoreMaterialInfo.toString());				
				} 
				else
				{ 	
					//No Core Material associated with RMP
					sbFinalOutput.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_NO_COREMATERIAL_ASSOCIATED);
				}
			}
			
			loggerWS.debug("mRelatedMaterial  : {}" ,mRelatedMaterial);

		}
		catch(Exception e)
		{
			loggerApolloTrace.error(e.getMessage(), e);
			sbFinalOutput.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage());
		}
		
		loggerWS.debug("sbFinalOutput  : {}" ,sbFinalOutput);

		return sbFinalOutput.toString();
	}
			
	/**
	 * Method to get associated material function
	 * @param context
	 * @param strLatestRMPObjectId
	 * @param fetchUpdateRMPDataLog
	 * @return
	 * @throws Exception
	 */
	private String getAssociatedMaterialFunction(matrix.db.Context context, String strLatestRMPObjectId) throws Exception {
		String strMaterialFunction = DomainConstants.EMPTY_STRING;
		try
		{
			if(UIUtil.isNotNullAndNotEmpty(strLatestRMPObjectId))
			{
				DomainObject domObject = DomainObject.newInstance(context,strLatestRMPObjectId);
				StringList materialFunctionList = domObject.getInfoList(context, "from["+pgApolloConstants.RELATIONSHIP_MATERIAL_FUNCTIONALITY+"].to.name");
				loggerWS.debug("\nmaterialFunctionList  : {}" ,materialFunctionList);
				if(null!=materialFunctionList && materialFunctionList.size()>0)
				{
					strMaterialFunction = FrameworkUtil.join(materialFunctionList, "|");
				}				
			}
		}
		catch(Exception e)
		{
			loggerApolloTrace.error(e.getMessage(), e);
		}		
		return strMaterialFunction;
	}

	/**
	 * Method to get the latest revision of Raw material
	 * @param context
	 * @param strRMPName
	 * @param fetchUpdateRMPDataLog
	 * @return
	 * @throws Exception
	 */
	private String getLatestRevisionForRawMaterial(matrix.db.Context context, String strRMPName) throws Exception {
		String strRMCurrent = DomainConstants.EMPTY_STRING;
		String strMaterialObjId = DomainConstants.EMPTY_STRING;
		MapList mlRawMaterials = new MapList();			
		Map mpRawMaterialInfo = new HashMap();		
		try {

			Pattern typePattern = new Pattern(pgApolloConstants.TYPE_RAW_MATERIAL);
			typePattern.addPattern(pgApolloConstants.TYPE_PG_RAW_MATERIAL);

			StringList slObjectSelects = new StringList(4);
			slObjectSelects.add(DomainConstants.SELECT_ID);
			slObjectSelects.add(DomainConstants.SELECT_NAME);
			slObjectSelects.add(DomainConstants.SELECT_REVISION);
			slObjectSelects.add(DomainConstants.SELECT_CURRENT);			

			mlRawMaterials = DomainObject.findObjects(context,typePattern.getPattern(), strRMPName, DomainConstants.QUERY_WILDCARD, null, DomainConstants.QUERY_WILDCARD,DomainConstants.EMPTY_STRING, true, slObjectSelects );
			if(null != mlRawMaterials && !mlRawMaterials.isEmpty()) 
			{							
				strRMCurrent = DomainConstants.EMPTY_STRING;
				strMaterialObjId = DomainConstants.EMPTY_STRING;

				mlRawMaterials.sort(DomainConstants.SELECT_REVISION, "descending", "String"); //sorting revision in descending order

				if(mlRawMaterials.size()== 1) 
				{							
					mpRawMaterialInfo = (Map)mlRawMaterials.get(0);
					strRMCurrent = (String)mpRawMaterialInfo.get(DomainConstants.SELECT_CURRENT);
					if(!pgApolloConstants.STATE_OBSOLETE.equals(strRMCurrent)) 
					{
						strMaterialObjId = (String)mpRawMaterialInfo.get(DomainConstants.SELECT_ID);
					}

				}
				else 
				{
					for(int i=0;i<mlRawMaterials.size();i++) 
					{
						mpRawMaterialInfo = (Map)mlRawMaterials.get(i);
						strRMCurrent = (String)mpRawMaterialInfo.get(DomainConstants.SELECT_CURRENT);
						if("Release".equals(strRMCurrent)) {
							strMaterialObjId = (String)mpRawMaterialInfo.get(DomainConstants.SELECT_ID);
							break;
						}
					}								

				}
			}
			loggerWS.debug("\nRMP latest Object Id  : {}" , strMaterialObjId);

		}
		catch(Exception e)
		{
			loggerApolloTrace.error(e.getMessage(), e);
		}		
		return 	strMaterialObjId;		
	}
	//Apollo 2018x.5 ALM 34491,35062,34606,34672 A10-505 - Starts
	/**
	 * Method to get the latest revision of Core material by passing Core Material name and revision.
	 * @param context
	 * @param strCoreMaterialName
	 * @param strCoreMaterialRevision
	 * @param fetchUpdateRMPDataLog
	 * @return
	 * @throws Exception
	 */
	private String connectCoreMaterialToRMP(matrix.db.Context context, Map mapConnectInfo) throws Exception 
	{	
		StringBuffer sbCoreMaterialInfo = new StringBuffer();
		try 
		{
			String strLatestRMPObjectId = (String)mapConnectInfo.get(DomainConstants.SELECT_ID);
			String strCoreMaterialName = (String)mapConnectInfo.get("CoreMaterialName");
			String strCoreMaterialRevision = (String)mapConnectInfo.get("CoreMaterialRevision");

			String strCoreMaterialId;

			Map mapTemp = null;
			
			StringList slObjectSelects = new StringList(1);			
			slObjectSelects.addElement(DomainConstants.SELECT_ID);	

			MapList mlCoreMaterials = new MapList();
			if(UIUtil.isNotNullAndNotEmpty(strCoreMaterialName) && UIUtil.isNotNullAndNotEmpty(strCoreMaterialRevision))
			{
				mlCoreMaterials = DomainObject.findObjects(context,							//context
														pgApolloConstants.TYPE_DSC_MATREF_REF_CORE, //type pattern
														strCoreMaterialName, 						//name pattern
														strCoreMaterialRevision, 					//revision pattern
														null, 										//owner
														DomainConstants.QUERY_WILDCARD,				//vault pattern
														DomainConstants.EMPTY_STRING, 				//where condition
														true, 										//expand type
														slObjectSelects );							//object selects
			}
			loggerWS.debug("\n\nmlCoreMaterials :{}",mlCoreMaterials);
			
			if(null != mlCoreMaterials && !mlCoreMaterials.isEmpty()) 
			{							
				mapTemp = (Map)mlCoreMaterials.get(0);
				strCoreMaterialId = (String)mapTemp.get(DomainConstants.SELECT_ID);
				
				if(UIUtil.isNotNullAndNotEmpty(strLatestRMPObjectId) && UIUtil.isNotNullAndNotEmpty(strCoreMaterialId))
				{
					//Check if Latest Revision of Core material is connected to RMP	
					DomainObject domCoreMaterial = DomainObject.newInstance(context,strCoreMaterialId);
					DomainObject domRMP = DomainObject.newInstance(context,strLatestRMPObjectId);
					
					StringList relSelects = new StringList();
					relSelects.addElement(DomainRelationship.SELECT_ID);
					
					StringBuffer sbObjectWhereCondition = new StringBuffer();
					sbObjectWhereCondition.append(DomainConstants.SELECT_NAME).append("=='").append(strCoreMaterialName).append("'");
					
					//Get connected core materials from RMP
					MapList mlConnectedCoreMaterial = domRMP.getRelatedObjects(context,							//context
															pgApolloConstants.RELATIONSHIP_PGRELATEDMATERIAL,	//relationship pattern
															pgApolloConstants.TYPE_DSC_MATREF_REF_CORE,			//type pattern
															slObjectSelects,									//object Select
															relSelects,											//rel Select
															false,												//get To
															true,												//get From
															(short)1,											//recurse level
															sbObjectWhereCondition.toString(),					//object where Clause
															null,												//rel where Clause
															0);													//limit	
					
					loggerWS.debug("\n\nmlConnectedCoreMaterial :{}",mlConnectedCoreMaterial);
					
					StringList slCoreMaterialRelIds = new StringList();
					String strConnectedCoreMaterialId;
					boolean isCoreMaterialAlreadyConnected = false;
					
					if(null != mlConnectedCoreMaterial && !mlConnectedCoreMaterial.isEmpty())
					{
						Map mapCM = null;
						for(int iCount=0, iSize=mlConnectedCoreMaterial.size(); iCount<iSize; iCount++)
						{
							mapCM = (Map)mlConnectedCoreMaterial.get(iCount);
							strConnectedCoreMaterialId = (String)mapCM.get(DomainConstants.SELECT_ID);
							if(strConnectedCoreMaterialId.equalsIgnoreCase(strCoreMaterialId))
							{
								isCoreMaterialAlreadyConnected = true;
							}
							else
							{							
								slCoreMaterialRelIds.add((String)mapCM.get(DomainRelationship.SELECT_ID));
							}
						}
					}

					//Checking Latest Revision of Core material is connected to RMP or not
				
					//Latest Revision of Core material is not connected to RMP
					if(null != slCoreMaterialRelIds && !slCoreMaterialRelIds.isEmpty())
					{		
						//If not then disconnect old revision of core material (if any)
						DomainRelationship.disconnect(context, slCoreMaterialRelIds.toArray(new String[slCoreMaterialRelIds.size()]));
						loggerWS.debug("\nSuccessfully disconnected old revision(s) of core material from RMP.");
					}
						
					if(!isCoreMaterialAlreadyConnected)
					{
						//connect latest revision of core material to RMP.
						DomainRelationship.connect(context, domRMP, pgApolloConstants.RELATIONSHIP_PGRELATEDMATERIAL, domCoreMaterial);									
						loggerWS.debug("\nSuccessfully connected latest revision of core material to RMP.");
					}
					else
					{
						loggerWS.debug("\n\nCore Material already connected");						
						
						releaseConnectedInWorkCoreMaterial(context, strCoreMaterialId, strLatestRMPObjectId);			
						
					}					
					
					//Latest revision of connected Core material details
					sbCoreMaterialInfo.append(pgApolloConstants.TYPE_DSC_MATREF_REF_CORE).append(pgApolloConstants.CONSTANT_STRING_PIPE);
					sbCoreMaterialInfo.append(strCoreMaterialName).append(pgApolloConstants.CONSTANT_STRING_PIPE);
					sbCoreMaterialInfo.append(strCoreMaterialRevision).append(pgApolloConstants.CONSTANT_STRING_PIPE);
					sbCoreMaterialInfo.append(pgApolloConstants.STR_CONNECTED);						
					String strCoreMaterialCurrent = domCoreMaterial.getInfo(context, DomainConstants.SELECT_CURRENT);
					if(UIUtil.isNotNullAndNotEmpty(strCoreMaterialCurrent))
					{
						sbCoreMaterialInfo.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(strCoreMaterialCurrent);	
					}
				}
			}
			else
			{	
				sbCoreMaterialInfo.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_COREMATERIAL_RMP_NOT_EXIST);
				loggerWS.debug("\nsbCoreMaterialInfo :{}" ,sbCoreMaterialInfo);
			}
		}
		catch(Exception e)
		{
			loggerApolloTrace.error(e.getMessage(), e);
			sbCoreMaterialInfo.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage());
		}		
		return sbCoreMaterialInfo.toString();		
	}
	//Apollo 2018x.5 ALM 34491,35062,34606,34672 A10-505 - Ends

	/**
	 * Method to release In Work Connected Core Material if RMP is released
	 * @param context
	 * @param domCoreMaterial
	 * @param domRMP
	 * @throws Exception
	 */
	private void releaseConnectedInWorkCoreMaterial(matrix.db.Context context, String sCoreMaterialId, String sLatestRMPObjectId) throws Exception
	{
		if(UIUtil.isNotNullAndNotEmpty(sCoreMaterialId) && UIUtil.isNotNullAndNotEmpty(sLatestRMPObjectId))
		{
			DomainObject domCoreMaterial = DomainObject.newInstance(context,sCoreMaterialId);
			DomainObject domRMP = DomainObject.newInstance(context,sLatestRMPObjectId);
			
			String strCoreMaterialCurrent;
			strCoreMaterialCurrent = domCoreMaterial.getInfo(context, DomainConstants.SELECT_CURRENT);	
			
			//Release In Work Core Material
			StringList slRMPSelects = new StringList();
			slRMPSelects.add(DomainConstants.SELECT_CURRENT);				

			Map mapRMPDetails = domRMP.getInfo(context, slRMPSelects);
			
			String sRMPCurrent = (String)mapRMPDetails.get(DomainConstants.SELECT_CURRENT);			
			
			pgApolloCommonUtil.releaseCoreMaterial(context, domCoreMaterial, strCoreMaterialCurrent, sRMPCurrent);

			loggerWS.debug("\n Core Material is released as RMP is in In Work state");					

		}
		
	}
	
	/**
	 * APOLLO 2018x.5 ALM Requirement 28713 - A10-584 - Validate Core Material and RMP
	 * Format : 3dspace/resources/getDesignToolData/CoreMaterial/validateMaterialsForGlobalUpdate?<VPMRefName>|<VPMRefRevision>&<RMP_GCAS>&<CoreMaterialName>|<CoreMaterialRevision>
	 * or
	 * 3dspace/resources/getDesignToolData/CoreMaterial/validateMaterialsForGlobalUpdate?<VPMRefName>|<VPMRefRevision>&<RMP_GCAS>&<CoreMaterialName>|<CoreMaterialRevision>
	 * Method to Validate Core Material and RMP
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/validateMaterialsForGlobalUpdate")
	public Response validateMaterialsForGlobalUpdate(@Context HttpServletRequest request) throws Exception
	{	
		matrix.db.Context context = null;		
		StringBuffer sbReturnMessages = new StringBuffer();
		String strDate = new SimpleDateFormat("yyyyMMdd").format(new Date());

		String[] URLParam = new String[3];
		String strRMPName = DomainConstants.EMPTY_STRING;
		String strError = DomainConstants.EMPTY_STRING;
		String strVPMRefId = DomainConstants.EMPTY_STRING;
		String strVPMRefName = DomainConstants.EMPTY_STRING;
		String strVPMRefDetails = DomainConstants.EMPTY_STRING;
		String strVPMRefRevision = DomainConstants.EMPTY_STRING;
		String strAPPObjectId = DomainConstants.EMPTY_STRING;
		StringList slVPMRefDetails = new StringList();
		StringList slRMPDetails = new StringList();
		String strRMPDetails = DomainConstants.EMPTY_STRING;
		String strCoreMaterialDetails = DomainConstants.EMPTY_STRING;
		String strMaterialRestrictionComment = DomainConstants.EMPTY_STRING;
		StringList slErrorCodes = new StringList();
		StringList slAllErrors = new StringList();
		try {
			loggerWS.debug("******************************************************************************");
			loggerWS.debug("Path : /CoreMaterial/validateMaterialsForGlobalUpdate");
			loggerWS.debug("Method: LayeredProductCoreMaterialUtility : validateMaterialsForGlobalUpdate");
			loggerWS.debug("\n\n New RMP Validation for Global Material Update Started : {}" ,new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) );

			// Get the user context
			if (Framework.isLoggedIn((HttpServletRequest) request)) {
				context = Framework.getContext((HttpSession) request.getSession(false));				
			}
			String queryString = request.getQueryString();
			
			
			/*
			 * QueryString : <VPMReferenceName>|<VPMReferenceRevision>&<RMPName>&<CoreMaterialName>|<CoreMaterialRevision>
			 * or
			 * QueryString : <VPMReferenceName>|<VPMReferenceRevision>&<RMPName>&<CoreMaterialName>|<CoreMaterialRevision>
			 */

			if (UIUtil.isNotNullAndNotEmpty(queryString)) 
			{
				
				queryString =  java.net.URLDecoder.decode(queryString,"UTF-8");
				loggerWS.debug("\n\nQueryString  : {}" ,queryString);	
				URLParam = queryString.split(pgApolloConstants.CONSTANT_STRING_AMPERSAND);	
				if (null!= URLParam && URLParam.length > 1) {
					strVPMRefDetails = URLParam[0];
					if(strVPMRefDetails!=null && !strVPMRefDetails.isEmpty())
					{
						slVPMRefDetails = FrameworkUtil.split(strVPMRefDetails, pgApolloConstants.CONSTANT_STRING_PIPE);//<VPMReferenceName>|<VPMReferenceRevision>
						if(null!=slVPMRefDetails && !slVPMRefDetails.isEmpty() && slVPMRefDetails.size()>1)
						{
							strVPMRefName = (String)slVPMRefDetails.get(0);//<VPMReferenceName>
							strVPMRefRevision = (String)slVPMRefDetails.get(1);//<VPMReferenceRevision>
							strVPMRefName = strVPMRefName.trim();
							strVPMRefRevision = strVPMRefRevision.trim();
							strVPMRefId = pgApolloCommonUtil.getObjectId(context, pgApolloConstants.TYPE_VPMREFERENCE, strVPMRefName, strVPMRefRevision);
							if(UIUtil.isNotNullAndNotEmpty(strVPMRefId))
							{
								strAPPObjectId = pgApolloCommonUtil.fetchAPPObjectId(context, strVPMRefId);
							}
						}							
					}
					loggerWS.debug("\nstrVPMRefName  : {}, strVPMRefRevision : {}" ,strVPMRefName,strVPMRefRevision);
					loggerWS.debug("\nstrAPPObjectId  : {}" ,strAPPObjectId);					
					if(UIUtil.isNotNullAndNotEmpty(strAPPObjectId))
					{						
						strRMPDetails = URLParam[1];//<RMPName>						
						if(strRMPDetails!=null && !strRMPDetails.isEmpty())
						{
							slRMPDetails = FrameworkUtil.split(strRMPDetails, pgApolloConstants.CONSTANT_STRING_PIPE);
							if(null!=slRMPDetails && !slRMPDetails.isEmpty())
							{
								strRMPName = (String)slRMPDetails.get(0);
								strRMPName = strRMPName.trim();
								
							}												
							loggerWS.debug("\nstrRMPName  : {}" ,strRMPName);							
							if(UIUtil.isNotNullAndNotEmpty(strRMPName))
							{
								//To check validation on RMP
								MapList mlStacking = new MapList(1);
								Map mpMaterial = new HashMap();
								mpMaterial.put(pgApolloConstants.KEY_PLYMATERIAL, strRMPName);
								if(URLParam.length > 2)
								{
									strCoreMaterialDetails = URLParam[2];//<CoreMaterialName>|<CoreMaterialRevision>
									mpMaterial.put(pgApolloConstants.KEY_COREMATERIAL, strCoreMaterialDetails);	
									loggerWS.debug("\nstrCoreMaterialDetails  : {}" ,strCoreMaterialDetails);
								}
								mlStacking.add(mpMaterial);
								loggerWS.debug("\ncontext user : {}" , context.getUser());
								loggerWS.debug("\n Before Validation mlStacking  : {}" ,mlStacking);
								mlStacking = pgApolloCommonUtil.validateRMPDataForSync(context, strAPPObjectId, mlStacking, false, false, true, true);			
								loggerWS.debug("\n After Validation mlStacking  : {}" ,mlStacking);								
								if(null != mlStacking && !mlStacking.isEmpty())
								{
									mpMaterial = (Map)mlStacking.get(0);
									if(mpMaterial.containsKey(pgApolloConstants.STR_ERROR_CODE) && mpMaterial.containsKey(pgApolloConstants.STR_ERROR_LIST))
									{
										slErrorCodes = (StringList)mpMaterial.get(pgApolloConstants.STR_ERROR_CODE);
										slAllErrors = (StringList)mpMaterial.get(pgApolloConstants.STR_ERROR_LIST);
									}
									if(null!=slErrorCodes && !slErrorCodes.isEmpty())
									{
										strError = StringUtil.join(slErrorCodes, pgApolloConstants.CONSTANT_STRING_PIPE);
										sbReturnMessages.append(strError);
									}
									else
									{
										sbReturnMessages.append(pgApolloConstants.STR_SUCCESS);
									}									
									loggerWS.debug("\nAll Errors  : {}" ,slAllErrors);
									loggerWS.debug("\nsbReturnMessages  : {}" ,sbReturnMessages);
								}								
							}
							else
							{
								sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_AUTOMATION_ERROR_INVALID_PARAMETERS);
							}
																				
						}
						else
						{
							sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_AUTOMATION_ERROR_INVALID_PARAMETERS);
						}
					}
					else
					{
						sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_NO_VPMREF_APP_FOUND);
					}					
				}
				else
				{
					sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_AUTOMATION_ERROR_INVALID_PARAMETERS);
				}	
			}
		}						
		catch(Exception ex)
		{
			sbReturnMessages = new StringBuffer();
			sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(ex.getLocalizedMessage());
			loggerApolloTrace.error(ex.getMessage(), ex);
		}
		finally 
		{
			loggerWS.debug("\n\n Final Response -->{}" ,sbReturnMessages.toString());
			loggerWS.debug("\n****Data Sent****{}" ,new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date())  );			
		}
		return Response.status(200).entity(sbReturnMessages.toString()).build();
	}
	
	//Apollo 2018x.5 A10-684 - Starts
	/**
	 * Method to promote core material to IN_WORK state if it is in PRIVATE state.
	 * Format : 3dspace/resources/getDesignToolData/CoreMaterial/promoteCoreMaterial?<CoreMaterialName>&<CoreMaterialRevision>
	 * @param request
	 * @return Success or Error: <Error Message>
	 * @throws Exception
	 */
	@GET
	@Path("/promoteCoreMaterial")
	public Response promoteCoreMaterialToInWorkState(@Context HttpServletRequest request) throws Exception
	{	
		boolean bContextPushed = false;
		matrix.db.Context context = null;			
		StringBuilder sbReturnMessages = new StringBuilder();
		try 
		{
			String[] sURLParam = new String[20];
			String strCoreMaterialName = DomainConstants.EMPTY_STRING;
			String strCoreMaterialRevision = DomainConstants.EMPTY_STRING;
			loggerWS.debug("******************************************************************************");
			loggerWS.debug("Path : /CoreMaterial/promoteCoreMaterial");
			loggerWS.debug("Method: LayeredProductCoreMaterialUtility : promoteCoreMaterialToInWorkState");
			loggerWS.debug("\n\n Promote Core Material to IN_WORK state Started : {}" ,new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) );

			// Get the user context
			if (Framework.isLoggedIn(request)) {
				context = Framework.getContext(request.getSession(false));				
			}

			String queryString = request.getQueryString();//QueryString : <CoreMaterialName>&<CoreMaterialRevision>
			
			//Pushing the context to User Agent
			ContextUtil.pushContext(context,pgApolloConstants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
			bContextPushed = true;
			if (UIUtil.isNotNullAndNotEmpty(queryString)) 
			{
				queryString =  java.net.URLDecoder.decode(queryString,"UTF-8");
				loggerWS.debug("\n\nQueryString  : {}" ,queryString);	
				sURLParam = queryString.split(pgApolloConstants.CONSTANT_STRING_AMPERSAND);	
				if (null != sURLParam && sURLParam.length == 2) 
				{
					strCoreMaterialName = sURLParam[0];
					strCoreMaterialRevision = sURLParam[1];
					loggerWS.debug("\nstrCoreMaterialName  : {}, strCoreMaterialRevision : {}",strCoreMaterialName,strCoreMaterialRevision);																
					if(UIUtil.isNotNullAndNotEmpty(strCoreMaterialName) && UIUtil.isNotNullAndNotEmpty(strCoreMaterialRevision))
					{
						StringList objectSelectList = new StringList(1);
						objectSelectList.addElement(DomainConstants.SELECT_CURRENT);
						Map mapCMInfo = pgApolloCommonUtil.getObjectIdWithSelects(context, pgApolloConstants.TYPE_DSC_MATREF_REF_CORE, strCoreMaterialName, strCoreMaterialRevision, objectSelectList);
						if(null != mapCMInfo && mapCMInfo.containsKey(DomainConstants.SELECT_ID) && UIUtil.isNotNullAndNotEmpty((String)mapCMInfo.get(DomainConstants.SELECT_ID)))
						{
							if(mapCMInfo.containsKey(DomainConstants.SELECT_CURRENT) && pgApolloConstants.STATE_PRIVATE.equalsIgnoreCase((String)mapCMInfo.get(DomainConstants.SELECT_CURRENT)))
							{								
								String strCoreMaterialId = (String)mapCMInfo.get(DomainConstants.SELECT_ID);
								DomainObject domCoreMaterial = DomainObject.newInstance(context, strCoreMaterialId);
								SignatureList slSignature = domCoreMaterial.getSignatures(context, pgApolloConstants.STATE_PRIVATE, pgApolloConstants.STATE_IN_WORK);
								for(Iterator itr =  slSignature.iterator(); itr.hasNext();)
								{
									Signature objSignature = (Signature)itr.next();
									domCoreMaterial.approveSignature(context, objSignature, DomainConstants.EMPTY_STRING);    	    								
								}
								domCoreMaterial.promote(context);
								sbReturnMessages.append(pgApolloConstants.STR_SUCCESS);
							}
						}
						else
						{
							sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_ERROR_COREMATERIAL_NOTFOUND);
						}
					}
					else
					{
						sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_AUTOMATION_ERROR_INVALID_PARAMETERS);
					}					
				}
				else
				{
					sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_AUTOMATION_ERROR_INVALID_PARAMETERS);
				}	
			}
		}						
		catch(Exception ex)
		{
			sbReturnMessages = new StringBuilder();
			sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(ex.getLocalizedMessage());
			loggerApolloTrace.error(ex.getMessage(), ex);
		}
		finally 
		{
			if(bContextPushed)
			{
				ContextUtil.popContext(context);
			}
			loggerWS.debug("\n\n Final Response -->{}",sbReturnMessages.toString());
			loggerWS.debug("\n****Data Sent****{}" ,new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date())  );			
		}
		return Response.status(200).entity(sbReturnMessages.toString()).build();
	}
	//Apollo 2018x.5 A10-684 - Ends
	
	
	/**
	 * APOLLO 2018x.6 Oct CW - A10-1004 - New Web service for Check Tool will give a list of the necessary usages for a specific RMP.
	 * Method to get mandatory parameters for RMP
	 * Format : 3dspace/resources/getDesignToolData//CoreMaterial/validateMandatoryParametersForRMP?<RMPGCAS> 
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/validateMandatoryParametersForRMP")
	public Response validateMandatoryParametersForRMP(@Context HttpServletRequest req)throws Exception{

		StringBuilder sbReturnMessage = new StringBuilder();
		matrix.db.Context context = null;
		StringList slURLParam;
		String sValidURLParam;
		String sBaseUOM;

		boolean bContextPushed = false;	
		try
		{			
			loggerWS.debug("******************************************************************************");
			loggerWS.debug("Path : /CoreMaterial/validateMandatoryParametersForRMP");
			loggerWS.debug("Method: LayeredProductCoreMaterialUtility  : validateMandatoryParametersForRMP");
			String strStartTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
			loggerWS.debug("Validate RMP Parameters Started : {}" , strStartTime );

			// Get the user context
			if (Framework.isLoggedIn(req)) {
				context = Framework.getContext(req.getSession(false));				
			}

			if(null != context)
			{
				loggerWS.debug("context user : {}" , context.getUser());
			}
			String queryString = req.getQueryString();
			queryString =  java.net.URLDecoder.decode(queryString,"UTF-8");

			loggerWS.debug("QueryString  : {}" , queryString);

			//Pushing the context to User Agent - User will not have always have access to RMP - web service used in Automation
			ContextUtil.pushContext(context,pgApolloConstants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
			bContextPushed = true;

			if(UIUtil.isNotNullAndNotEmpty(queryString)) 
			{
				slURLParam = StringUtil.split(queryString, pgApolloConstants.CONSTANT_STRING_AMPERSAND);
				sValidURLParam = slURLParam.get(0).trim();			
				if (UIUtil.isNotNullAndNotEmpty(sValidURLParam)) 
				{
					String sRMPName = sValidURLParam;
					Map mapRMP = pgApolloCommonUtil.getRMPIdForSync(context,sRMPName);
					String sRMPId = (String)mapRMP.get(DomainConstants.SELECT_ID);
					loggerWS.debug("RMP Id :{}" ,sRMPId);
					if(UIUtil.isNotNullAndNotEmpty(sRMPId)) 
					{
						StringList slRMPSelects = new StringList();
						slRMPSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PGBASEUOM);

						DomainObject domRMP = DomainObject.newInstance(context, sRMPId);

						Map mapAttribute = domRMP.getInfo(context, slRMPSelects);						

						if(mapAttribute.containsKey(pgApolloConstants.SELECT_ATTRIBUTE_PGBASEUOM))
						{
							sBaseUOM = (String)mapAttribute.get(pgApolloConstants.SELECT_ATTRIBUTE_PGBASEUOM);
							loggerWS.debug("sBaseUOM : {}" ,sBaseUOM);

							if(pgApolloConstants.STR_UOM_SQUARE_METER.equalsIgnoreCase(sBaseUOM))
							{
								sbReturnMessage.append(pgApolloConstants.KEY_GROSS_AREA);
								sbReturnMessage.append(pgApolloConstants.CONSTANT_STRING_PIPE);
								sbReturnMessage.append(pgApolloConstants.KEY_NET_AREA);								
							}
							else if (pgApolloConstants.STR_UOM_LITER.equalsIgnoreCase(sBaseUOM) || pgApolloConstants.STR_UOM_CUBIC_METER.equalsIgnoreCase(sBaseUOM))
							{
								sbReturnMessage.append(pgApolloConstants.KEY_GROSS_VOLUME);
								sbReturnMessage.append(pgApolloConstants.CONSTANT_STRING_PIPE);
								sbReturnMessage.append(pgApolloConstants.KEY_NET_VOLUME);												
							}
							else if (pgApolloConstants.STR_UOM_METER.equalsIgnoreCase(sBaseUOM))
							{								
								sbReturnMessage.append(pgApolloConstants.KEY_GROSS_LENGTH);
								sbReturnMessage.append(pgApolloConstants.CONSTANT_STRING_PIPE);
								sbReturnMessage.append(pgApolloConstants.KEY_NET_LENGTH);			
							}
						}	

						if(sbReturnMessage.toString().isEmpty())
						{
							sbReturnMessage.append(pgApolloConstants.STR_AUTOMATION_BLANK);
						}
					}
					else
					{
						sbReturnMessage.append(pgApolloConstants.STR_RMP_NOT_EXIST);
					}					
				}
				else 
				{
					sbReturnMessage.append(pgApolloConstants.STR_AUTOMATION_ERROR_INVALID_PARAMETERS);
				}
			}
			else 
			{
				sbReturnMessage.append(pgApolloConstants.STR_AUTOMATION_ERROR_INVALID_PARAMETERS);				
			}			
		}
		catch(Exception ex)
		{
			sbReturnMessage = new StringBuilder();
			sbReturnMessage.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(ex.getLocalizedMessage());
			loggerApolloTrace.error(ex.getMessage(), ex);
		}
		finally 
		{
			if(bContextPushed)
			{
				ContextUtil.popContext(context);
			}
			loggerWS.debug("Final Response -->{}",sbReturnMessage);
			String strEndTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
			loggerWS.debug("Validate RMP Parameters Ended {}" , strEndTime );			
		}
		return Response.status(200).entity(sbReturnMessage.toString()).build();		
	}
	
	
	/**
	 * Requirement 46922 - APOLLO 2022x.03 Aug CW - Tampon Requirements - Synchronizing the Tampon assembly structure in CATIA, with applicator parts attached to the structure 
	 * 3dspace/resources/getDesignToolData/CoreMaterial/fetchApplicatorMasterPart?<VPMRefName>|<VPMRefRevision>&<RMP_GCAS>
	 * Method to fetch Applicator Master Part
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/fetchApplicatorMasterPart")
	public Response fetchApplicatorMasterPart(@Context HttpServletRequest request)throws Exception{

		StringBuilder sbReturnMessage = new StringBuilder();
		matrix.db.Context context = null;

		boolean bContextPushed = false;	
		try
		{			
			// Get the user context
			if (Framework.isLoggedIn( request)) {
				context = Framework.getContext( request.getSession(false));
			}

			//Used in Automation webservice - Push context is needed to get Master Part Details. Context user will not always have read access Master Part. 
			ContextUtil.pushContext(context,pgApolloConstants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
			bContextPushed = true;			
			loggerWS.debug("******************************************************************************");
			loggerWS.debug("Path : /CoreMaterial/fetchApplicatorMasterPart");
			loggerWS.debug("Method: LayeredProductCoreMaterialUtility : fetchApplicatorMasterPart");
			String sStartDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
			loggerWS.debug("\nNew Data fetching started :{} ", sStartDate);

			String queryString = request.getQueryString();
			queryString =  java.net.URLDecoder.decode(queryString,"UTF-8");

			loggerWS.debug("\nQuery String : {}",queryString);
			
			String sError;
			String sPartId = DomainConstants.EMPTY_STRING;
			String sOutput;
			boolean bError = false;

			if(UIUtil.isNotNullAndNotEmpty(queryString)) 
			{
				queryString=queryString.trim();
				StringList slURLParameterList = StringUtil.split(queryString, pgApolloConstants.CONSTANT_STRING_AMPERSAND);
				if(!slURLParameterList.isEmpty() && slURLParameterList.size() > 1)
				{
					String sVPMRefDetails = slURLParameterList.get(0);
					String sRMPName = slURLParameterList.get(1);
					
					if(UIUtil.isNotNullAndNotEmpty(sRMPName))
					{
						sOutput = validateRMPData(context, sVPMRefDetails, sRMPName);	
						
						if(UIUtil.isNotNullAndNotEmpty(sOutput) && sOutput.contains(pgApolloConstants.STR_ERROR))
						{
							loggerWS.debug("\n After Validation sError :{} " ,sOutput);
							sbReturnMessage.append(sOutput);
							bError = true;
						}	
						else
						{
							sPartId = sOutput;
						}
						
						loggerWS.debug("\n RMP ID :{} " ,sOutput);
						
						if(UIUtil.isNotNullAndNotEmpty(sPartId) && !bError) 
						{
							String sReturnOutput = validateAndFetchApplicatorInfo(context, sPartId, false);
							sbReturnMessage.append(sReturnOutput);
						}
						else if(sbReturnMessage.toString().isEmpty())
						{
							sbReturnMessage.append(pgApolloConstants.STR_ERROR);
							sbReturnMessage.append(pgApolloConstants.CONSTANT_STRING_COLON);
							sbReturnMessage.append(pgApolloConstants.STR_RMP_NOT_EXIST);
						}	
					}		

				}
				else
				{
					sbReturnMessage.append(pgApolloConstants.STR_ERROR);
					sbReturnMessage.append(pgApolloConstants.CONSTANT_STRING_COLON);
					sbReturnMessage.append(pgApolloConstants.STR_AUTOMATION_ERROR_INVALID_PARAMETERS);
				}	

			}
			loggerWS.debug("\nFinal Response---->{}" ,sbReturnMessage);
			loggerWS.debug("\n******************************************************\n");
		}
		catch (Exception e) 
		{
			sbReturnMessage.append(pgApolloConstants.STR_ERROR);
			sbReturnMessage.append(pgApolloConstants.CONSTANT_STRING_COLON);
			sbReturnMessage.append(e.getLocalizedMessage());
			loggerApolloTrace.error(e.getMessage(), e);
		}
		finally 
		{
			if(bContextPushed)
			{
				ContextUtil.popContext(context);
			}					
		}		
		return Response.status(200).entity(sbReturnMessage.toString()).build();		
	}

	
	/**
	 * E017 : Applicator Part is not in Released State
	 * E018 : Applicator Part does not have Physical Product
	 * E019 : Applicator Part is not valid type
	 * E020 : Applicator Part is not found for the given RMP
	 * Method to validate and Fetch Applicator Info
	 * @param context
	 * @param sPartId
	 * @return
	 * @throws Exception
	 */
	public static String validateAndFetchApplicatorInfo(matrix.db.Context context, String sPartId, boolean bValidationMode) throws Exception 
	{
		StringBuilder sbReturnMessage = new StringBuilder();
		if(UIUtil.isNotNullAndNotEmpty(sPartId))
		{
		String sInfoSelMaster= new StringBuilder("to[").append(DomainConstants.RELATIONSHIP_CLASSIFIED_ITEM).append("].frommid[").append(pgV3Constants.RELATIONSHIP_PARTFAMILYREFERENCE).append("].torel.to.id").toString();

		StringList slPartSelects = new StringList();
		slPartSelects.add(sInfoSelMaster);

		DomainObject domPart = DomainObject.newInstance(context, sPartId);

		Map mapSource = domPart.getInfo(context, slPartSelects);

		StringList slMasterId = pgApolloCommonUtil.getStringListMultiValue(mapSource.get(sInfoSelMaster));

		String sMasterId = DomainConstants.EMPTY_STRING;

		if(!slMasterId.isEmpty())
		{
			sMasterId = slMasterId.get(0);
		}

		loggerWS.debug("\nMaster Id :{} " , sMasterId);

		if(UIUtil.isNotNullAndNotEmpty(sMasterId))
		{
			String sPhysicalProductIdSelectable = new StringBuilder().append(pgApolloConstants.CONSTANT_STRING_SELECT_FROM).append(pgApolloConstants.RELATIONSHIP_PART_SPECIFICATION).append(pgApolloConstants.CONSTANT_STRING_SELECT_CLOSE_BRACKET).append(pgApolloConstants.CONSTANT_STRING_DOT).append(pgApolloConstants.CONSTANT_STRING_SELECT_TO).append(pgApolloConstants.TYPE_VPMREFERENCE).append(pgApolloConstants.CONSTANT_STRING_SELECT_CLOSE_BRACKET).append(pgApolloConstants.CONSTANT_STRING_DOT).append(DomainConstants.SELECT_ID).toString();

			StringList slMasterSelects = new StringList();
			slMasterSelects.add(DomainConstants.SELECT_ID);
			slMasterSelects.add(DomainConstants.SELECT_TYPE);
			slMasterSelects.add(sPhysicalProductIdSelectable);

			DomainObject domMaster = DomainObject.newInstance(context, sMasterId);

			Map mapMaster = domMaster.getInfo(context, slMasterSelects, new StringList(sPhysicalProductIdSelectable));

			String sMasterType = (String)mapMaster.get(DomainConstants.SELECT_TYPE);

			StringList slApplicatorTypes = new StringList();
			String sApplicatorTypes = pgApolloCommonUtil.getPageProperty(context, pgApolloConstants.STR_APOLLO_CONFIG_PAGE_FILENAME, "pgApolloConfiguration.ApplicatorPart.ApplicableTypes");
			if(UIUtil.isNotNullAndNotEmpty(sApplicatorTypes))
			{
				slApplicatorTypes = StringUtil.split(sApplicatorTypes, pgApolloConstants.CONSTANT_STRING_COMMA);
			}

			loggerWS.debug("\nslApplicatorTypes :{} " , slApplicatorTypes);

			if(slApplicatorTypes.contains(sMasterType))
			{
				StringList slPhysicalProductIdList = pgApolloCommonUtil.getStringListMultiValue(mapMaster.get(sPhysicalProductIdSelectable));

				loggerWS.debug("\nslPhysicalProductIdList :{} " , slPhysicalProductIdList);

				if(null != slPhysicalProductIdList && !slPhysicalProductIdList.isEmpty())
				{
					String sPhysicalProductId = slPhysicalProductIdList.get(0);

					DomainObject domVPMReference = DomainObject.newInstance(context, sPhysicalProductId);

					StringList slPhysicalProductSelectList = new StringList();
					slPhysicalProductSelectList.add(DomainConstants.SELECT_ID);
					slPhysicalProductSelectList.add(DomainConstants.SELECT_NAME);
					slPhysicalProductSelectList.add(DomainConstants.SELECT_REVISION);
					slPhysicalProductSelectList.add(DomainConstants.SELECT_CURRENT);

					Map mapPhysicalProduct = domVPMReference.getInfo(context, slPhysicalProductSelectList);	

					loggerWS.debug("\nmapPhysicalProduct :{} " , mapPhysicalProduct);

					String sPhysicalProductCurrent = (String)mapPhysicalProduct.get(DomainConstants.SELECT_CURRENT);

					if(pgApolloConstants.STATE_SHARED.equalsIgnoreCase(sPhysicalProductCurrent))
					{
						String sPhysicalProductName = (String)mapPhysicalProduct.get(DomainConstants.SELECT_NAME);

						String sPhysicalProductRevision = (String)mapPhysicalProduct.get(DomainConstants.SELECT_REVISION);
						
						if(!bValidationMode)
						{
							sbReturnMessage.append(sPhysicalProductName);
							sbReturnMessage.append(pgApolloConstants.CONSTANT_STRING_PIPE);
							sbReturnMessage.append(sPhysicalProductRevision);
						}

					}
					else
					{
						sbReturnMessage.append(pgApolloConstants.STR_ERROR);
						sbReturnMessage.append(pgApolloConstants.CONSTANT_STRING_COLON);
						sbReturnMessage.append(pgApolloConstants.STR_APPLICATORPART_NOTRELEASED);		
						
					}				

				}
				else
				{
					sbReturnMessage.append(pgApolloConstants.STR_ERROR);
					sbReturnMessage.append(pgApolloConstants.CONSTANT_STRING_COLON);
					sbReturnMessage.append(pgApolloConstants.STR_APPLICATORPART_NOPHYSICALPRODUCT);

				}
			}
			else
			{
				sbReturnMessage.append(pgApolloConstants.STR_ERROR);
				sbReturnMessage.append(pgApolloConstants.CONSTANT_STRING_COLON);
				sbReturnMessage.append(pgApolloConstants.STR_APPLICATORPART_NOTVALIDTYPE);

			}


		}
		else	
		{
			sbReturnMessage.append(pgApolloConstants.STR_ERROR);
			sbReturnMessage.append(pgApolloConstants.CONSTANT_STRING_COLON);
			sbReturnMessage.append(pgApolloConstants.STR_APPLICATORPART_NOTASSOCIATED);
		}
		
	}
		return sbReturnMessage.toString();
	}

	
	/**
	 * Method to validate RMP Data
	 * @param context
	 * @param sVPMRefDetails
	 * @param sRMPName
	 * @return
	 * @throws Exception
	 */
	public static String validateRMPData(matrix.db.Context context, String sVPMRefDetails, String sRMPName) throws Exception 
	{
		String sReturn = DomainConstants.EMPTY_STRING;
		String sVPMRefName;
		String sVPMRefRevision;
		String sVPMRefId;
		StringList slVPMRefDetails;
		StringList slAllErrors = new StringList();
		String sAPPObjectId = DomainConstants.EMPTY_STRING;
		
		if(UIUtil.isNotNullAndNotEmpty(sVPMRefDetails))
		{
			slVPMRefDetails = StringUtil.split(sVPMRefDetails, pgApolloConstants.CONSTANT_STRING_PIPE);//<VPMReferenceName>|<VPMReferenceRevision>
			if(null!=slVPMRefDetails && !slVPMRefDetails.isEmpty() && slVPMRefDetails.size() > 1)
			{
				sVPMRefName = (String)slVPMRefDetails.get(0);//<VPMReferenceName>
				sVPMRefRevision = (String)slVPMRefDetails.get(1);//<VPMReferenceRevision>
				sVPMRefName = sVPMRefName.trim();
				sVPMRefRevision = sVPMRefRevision.trim();
				sVPMRefId = pgApolloCommonUtil.getObjectId(context, pgApolloConstants.TYPE_VPMREFERENCE, sVPMRefName, sVPMRefRevision);
				if(UIUtil.isNotNullAndNotEmpty(sVPMRefId))
				{
					sAPPObjectId = pgApolloCommonUtil.fetchAPPObjectId(context, sVPMRefId);
				}
			}							
		}
		
		if(UIUtil.isNotNullAndNotEmpty(sAPPObjectId))
		{
			//To check validation on RMP
			MapList mlStacking = new MapList(1);
			Map mpMaterial = new HashMap();
			mpMaterial.put(pgApolloConstants.KEY_PLYMATERIAL, sRMPName);
			mlStacking.add(mpMaterial);
			loggerWS.debug("\n validateRMPData --> Before Validation mlStacking  : {}" ,mlStacking);
			mlStacking = pgApolloCommonUtil.validateRMPDataForSync(context, sAPPObjectId, mlStacking, false, false, true, true);			
			loggerWS.debug("\n validateRMPData --> After Validation mlStacking  : {}" ,mlStacking);								
			if(null != mlStacking && !mlStacking.isEmpty())
			{
				mpMaterial = (Map)mlStacking.get(0);
				if(mpMaterial.containsKey(pgApolloConstants.STR_ERROR_LIST))
				{
					slAllErrors = (StringList)mpMaterial.get(pgApolloConstants.STR_ERROR_LIST);
				}
				if(null!=slAllErrors && !slAllErrors.isEmpty())
				{
					sReturn = StringUtil.join(slAllErrors, pgApolloConstants.CONSTANT_STRING_COMMA);
					sReturn = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(sReturn).toString();
				}
				else
				{
					//Get latest revision of RMP
					if(mpMaterial.containsKey(pgApolloConstants.SELECT_ID))
					{									
						sReturn = (String) mpMaterial.get(pgApolloConstants.SELECT_ID);
					}
				}
			}
		}	
		
		loggerWS.debug("\n validateRMPData --> sReturn : {}" , sReturn);								

		return sReturn;
	}
	
	
	
	/**
	 * Defect 53782 - APOLLO 2022x.03 August CW - New web service for Applicator Validation (call from Check Tool)
	 * Format : 3dspace/resources/getDesignToolData/CoreMaterial/validateApplicatorsForGlobalUpdate?<RMPGCAS1>|< RMPGCAS2>|<RMPGCAS3>&<Applicator1Name>|<Applicator1Revision>&<Applicator2Name>|<Applicator2Revision>&<Applicator3Name>|<Applicator3Revision>
	 * Method to Validate Applicators and Applicator RMPs
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/validateApplicatorsForGlobalUpdate")
	public Response validateApplicatorsForGlobalUpdate(@Context HttpServletRequest request) throws Exception	
	{
		matrix.db.Context context = null;		
		StringBuilder sbReturnMessages = new StringBuilder();
		String strDate = new SimpleDateFormat("yyyyMMdd").format(new Date());

		StringList slURLParamList = new StringList();
		
		String sRMPGCASDetails;
		String sVPMRefDetails;
		StringList slRMPList = new StringList();
		StringList slVPMRefList = new StringList();
		int iURLParamLength = 0;

		try {
			loggerWS.debug("******************************************************************************");
			loggerWS.debug("Path : /CoreMaterial/validateApplicatorsForGlobalUpdate");
			loggerWS.debug("Method: LayeredProductCoreMaterialUtility : validateApplicatorsForGlobalUpdate");
			loggerWS.debug("\n Applicators Validation at Global Check Tool Started : {}" , strDate);

			// Get the user context
			if (Framework.isLoggedIn((HttpServletRequest) request)) {
				context = Framework.getContext((HttpSession) request.getSession(false));				
			}
			String queryString = request.getQueryString();			

			if (UIUtil.isNotNullAndNotEmpty(queryString)) 
			{

				queryString =  java.net.URLDecoder.decode(queryString,"UTF-8");

				loggerWS.debug("\n QueryString  : {}" ,queryString);	

				slURLParamList = StringUtil.split(queryString, pgApolloConstants.CONSTANT_STRING_AMPERSAND);				
				if (null != slURLParamList) {

					iURLParamLength = slURLParamList.size();

					if(iURLParamLength > 1)
					{
						sRMPGCASDetails = slURLParamList.get(0);

						if(UIUtil.isNotNullAndNotEmpty(sRMPGCASDetails))
						{
							slRMPList = StringUtil.split(sRMPGCASDetails, pgApolloConstants.CONSTANT_STRING_PIPE);
						}

						for(int i= 1; i<iURLParamLength ; i++)
						{
							sVPMRefDetails = slURLParamList.get(i);
							if(UIUtil.isNotNullAndNotEmpty(sVPMRefDetails))
							{
								slVPMRefList.add(sVPMRefDetails);
							}
						}

					}

					if(!slRMPList.isEmpty())
					{
						String sReturn = pgApolloCommonUtil.validateRMPApplicatorList(context, slRMPList, slVPMRefList, true);
						
						loggerWS.debug("\n validateApplicatorsForGlobalUpdate sReturn  : {}" ,sReturn);	

						sbReturnMessages.append(sReturn);
					}

				}

			}						
			
			if(sbReturnMessages.toString().isEmpty())
			{
				sbReturnMessages.append(pgApolloConstants.STR_SUCCESS);
			}
			
		}
		catch(Exception ex)
		{
			sbReturnMessages = new StringBuilder();
			sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(ex.getLocalizedMessage());
			loggerApolloTrace.error(ex.getMessage(), ex);
		}
		finally 
		{
			loggerWS.debug("\n Applicators Validation at Global Check Tool  Final Response -->{}" ,sbReturnMessages.toString());
			loggerWS.debug("\n****Data Sent****{}" ,new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date())  );			
		}
		return Response.status(200).entity(sbReturnMessages.toString()).build();
	
	}
		
}

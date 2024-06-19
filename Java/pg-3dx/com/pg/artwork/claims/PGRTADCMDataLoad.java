package com.pg.artwork.claims; 
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.lang.model.util.ElementScanner6;

import com.matrixone.apps.awl.dao.ArtworkContent;
import com.matrixone.apps.awl.dao.ArtworkMaster;
import com.matrixone.apps.awl.dao.CopyElement;
import com.matrixone.apps.awl.dao.CopyList;
import com.matrixone.apps.awl.dao.GraphicDocument;
import com.matrixone.apps.awl.dao.GraphicsElement;
import com.matrixone.apps.awl.enumeration.AWLAttribute;
import com.matrixone.apps.awl.enumeration.AWLPolicy;
import com.matrixone.apps.awl.enumeration.AWLRel;
import com.matrixone.apps.awl.enumeration.AWLState;
import com.matrixone.apps.awl.enumeration.AWLType;
import com.matrixone.apps.awl.util.AWLConstants;
import com.matrixone.apps.awl.util.AWLPropertyUtil;
import com.matrixone.apps.awl.util.BusinessUtil;
import com.matrixone.apps.common.CommonDocument;
import com.matrixone.apps.common.Person;
import com.matrixone.apps.common.Route;
import com.matrixone.apps.cpd.dao.Country;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.framework.ui.UIRTEUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.v3.custom.pgV3Constants;
import com.matrixone.apps.awl.util.AWLUtil;

import matrix.db.BusinessObject;
import matrix.db.BusinessObjectWithSelect;
import matrix.db.BusinessObjectWithSelectList;
import matrix.db.Context;
import matrix.db.Environment;
import matrix.db.JPO;
import matrix.db.Policy;
import matrix.db.RelationshipType;
import matrix.util.StringList;
import com.dassault_systemes.enovia.partmanagement.modeler.kernel.PartMgtKernel;
public class PGRTADCMDataLoad implements PGRTADCMIntegrationConstants {
	

	public static final Logger LOGGER = Logger.getLogger("pgRTADCM");
	//DCM:US-4383 RTA DCM Integration Code changes as per New data Model Changes  - Start	
	public static final String RELATIONSHIP_PG_CLAIM_PRODUCT_CONFIGURATION = PropertyUtil.getSchemaProperty("relationship_pgClaimProductConfiguration");
	//DCM:US-4383 RTA DCM Integration Code changes as per New data Model Changes  - Start
	public String processBGObject(Context context, String sBackgroundProcessObjectId) throws Exception {
		boolean bError = false;
		try 
		{
			if(UIUtil.isNotNullAndNotEmpty(sBackgroundProcessObjectId)){				
				DomainObject doBG = DomainObject.newInstance(context,sBackgroundProcessObjectId);
				String strAttCRIdState=doBG.getAttributeValue(context, "pgParameterArgument1");
				StringList slCRIdnState=FrameworkUtil.split(strAttCRIdState, "~");
				LOGGER.log(Level.INFO, "##RTADCM## processBGObject Start Processing {0}", slCRIdnState);
				
				if(slCRIdnState.size()==2) {
					String clrID=slCRIdnState.get(0);
					String strCRState=slCRIdnState.get(1);
					if(STATE_APPROVED.equals(strCRState)) {
						 createCLMCEData(context,clrID);	
					}else if(STATE_OBSOLETE.equals(strCRState)) {
						 ObsoleteRTAData(context,clrID);
					}
				}
			}
		} 
		catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.toString());
			LOGGER.log(Level.SEVERE, e.getMessage());
			String strReturnFail = STR_FAIL+"~Error:"+e.getMessage();
			bError = true;
			LOGGER.log(Level.SEVERE, "strReturnFail ==== {0}", strReturnFail);
			String userName		= context.getUser();
			LOGGER.log(Level.SEVERE, "user in catch 1==== {0}", userName);

			// Pop context 1 time if user is user agent
			if(userName.equals(STR_USER_AGENT)) {
				ContextUtil.popContext(context);
				LOGGER.log(Level.SEVERE, "strReturnFail 2==== {0}", strReturnFail);
			}

			userName		= context.getUser();
			LOGGER.log(Level.SEVERE, "user in catch 3==== {0}", userName);

			// Pop context one more time if user is DCM2RTA so context user is sys ctrlm 
			if(userName.equals(STR_INTEGRATION_USER)) {
				ContextUtil.popContext(context);
				LOGGER.log(Level.SEVERE, "strReturnFail 4==== {0}", strReturnFail);
			}
			LOGGER.log(Level.SEVERE, "user 5 {0}", context.getUser());	
			return strReturnFail;
		}finally {
			notifyToUserOnJOBStatus(context,bError,sBackgroundProcessObjectId);
		}

		LOGGER.log(Level.SEVERE, "user exit=== {0}", context.getUser());
		LOGGER.log(Level.INFO, "##RTADCM## processBGObject Exit Return {0}", STR_SUCCESS);
		return STR_SUCCESS;
	}

	public String createCLMCEData(Context context, String strCRId) throws Exception {
		
		boolean isContextPushed = false;
		boolean isContextPushedUserAgent=false;
		String strReturn = "";
		
		LOGGER.log(Level.INFO, "##RTADCM## 1. CR id is ---###1##> {0}", strCRId);			
		DomainObject doCR    = DomainObject.newInstance(context,strCRId);		
		MapList mlClaimsInfo = getClaimData(context, doCR);
		StringList crSelects = getCLRSelects();
		//RTA 22x Added for ALM-51831 starts
		StringList crMultiSelects = StringList.create("attribute["+ATTRIBUTE_REGION+"]", "attribute["+ATTRIBUTE_PGINTENDEDMARKETS+"]", "attribute["+ATTRIBUTE_PGBRAND+"]", "attribute["+ATTRIBUTE_PGCATEGORY+"]","attribute["+ATTRIBUTE_PGBUSINESSAREA+"]");	
		//RTA 22x Added for ALM-51831 ends
		Map crInfoMap = doCR.getInfo(context, crSelects);
		Map mCRMultiInfo = BusinessUtil.getInfoList(context, strCRId, crMultiSelects);
		crInfoMap.putAll(mCRMultiInfo);
		//String crACE = (String) crInfoMap.get("attribute["+ATTRIBUTE_PGCLAIMACECONTACT+"]");
		String crACE=null;
		if(crInfoMap!=null) {
			crACE = checkForStringList(crInfoMap,ATTRIBUTE_PGCLAIMACECONTACT);
		}
		String hasCRConnectedToCL = (String) crInfoMap.get("from["+REL_PGCOPYLIST+"]");
		String strCLRPrevRev = (String) crInfoMap.get("previous.id");

		LOGGER.log(Level.INFO, "##RTADCM## 1. CR Data strCLRPrevRev -2-> {0}", strCLRPrevRev);
		LOGGER.log(Level.INFO, "##RTADCM## 2. CR Data Map -2-> {0}", crInfoMap);
		LOGGER.log(Level.INFO, "##RTADCM## 3. Claim Data Map --> {0}", mlClaimsInfo);	
		
		File fileTemp = new File(STR_FILE_PATH + File.separator + STR_FILE_NAME);
		FileWriter outputFile = new FileWriter(fileTemp,true);
		try(PrintWriter writer = new PrintWriter(outputFile)) {
			LOGGER.log(Level.INFO, "##RTADCM## job Initial Context--> {0} ", context.getUser());
			writer.print("\n@@@RTA Data Creation Started");
			// Error Handling		
			if(crACE==null || "".equals(crACE) || !getClaimsUsingPackagingArtwork(context,mlClaimsInfo)) {	
				errorLogNoPackging(crInfoMap, writer);	
				return STR_FAIL+"~"+STR_NO_CLAIM_MSG;			
			}

			// Data is created with RTA Integration user context
			ContextUtil.pushContext(context, STR_INTEGRATION_USER, "",context.getVault().getName());
			isContextPushed = true;
			LOGGER.log(Level.INFO, "##RTADCM## Context Pushed to Integration User-> {0} ", context.getUser());		

			if(!context.isAssigned("Artwork User")) {
				errorLogNoRole(crInfoMap, writer);	
				return STR_FAIL+"~"+STR_NO_ARTWORK_USER_ROLE_MSG;			
			}	
			writer.print("\n@@@has CR Connected CL ------------>" + hasCRConnectedToCL);
			// Create CL Object
			String copyListId = createCL(context, crInfoMap, mlClaimsInfo);					
			LOGGER.log(Level.INFO, "##RTADCM## 4. CL Created and ID is..{0}", copyListId);
			writer.print("\n@@@Copy List Created ------------>" + copyListId);
			// Create PL Structure
			List plCategories = prepareProductLineToPlaceCL(context,crInfoMap,strCRId);
			LOGGER.log(Level.INFO, "##RTADCM## 5. ProductLine is prepared {0}", plCategories);
			writer.print("\n@@@Product Line Prepared ------------> " + plCategories);
							
			// Iterate claims and create MCE and connect MCE to pgClaim_.
			for (int i=0; i<mlClaimsInfo.size(); i++) {
				Map eachClaimMap     = (Map) mlClaimsInfo.get(i);
				String strClaimId=(String) eachClaimMap.get(DomainConstants.SELECT_ID);
				String strClaimRev   = (String) eachClaimMap.get(DomainConstants.SELECT_REVISION);
				String strFirstRev   = (String) eachClaimMap.get("first");	
				String strClaimCET_   = (String) eachClaimMap.get("attribute["+ATTRIBUTE_PGCOPYELEMENTTYPE+"]");
				//String strClaimExT   = (String) eachClaimMap.get("attribute["+ATTRIBUTE_PGEXECUTIONTYPE+"]");
				String strClaimExT =checkForStringList(eachClaimMap,ATTRIBUTE_PGEXECUTIONTYPE);
				//RTA 22x Added for ALM-49635 starts
				String strSequenceNumber=(String) eachClaimMap.get("attribute["+ATTRIBUTE_CLAIMSEQUENCE+"]");
				//RTA 22x Added for ALM-49635 ends
				//RTA 22x Added for ALM-46415 starts
				String strExpirationDate=(String) eachClaimMap.get("attribute["+ATTRIBUTE_PGEXPIRATIONDATE+"]");
				//RTA 22x Added for ALM-46415 ends
				eachClaimMap.put("ACECONTACT", crACE);
				
				if(strClaimRev!=null && !strClaimRev.equals(strFirstRev)){
					
					// This is for only revise
					if(strClaimCET_.contains("Graphic")) { 

						if(strClaimExT!=null && strClaimExT.contains("Packaging Artwork"))
						{						
							// Get Graphic from previous Claim Rev
							StringList claaimSelects=new StringList();						
							String strMCId=(String) eachClaimMap.get("previous.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id");
							String strClaimGraphicalImage=(String)eachClaimMap.get("attribute["+ATTRIBUTE_PGCLAIMGRAPHICALIMAGE+"]");
							String strPrevClaimGraphicalImage=(String)eachClaimMap.get("previous.attribute["+ATTRIBUTE_PGCLAIMGRAPHICALIMAGE+"]");
							//String strPrevClaimId=null;
							
							Map prevClaimInfoMap = getMapDataWithPushContextGraphic(context,strClaimId,claaimSelects);
					
							boolean isImageDataChange=compareImageData(context,strClaimGraphicalImage,strPrevClaimGraphicalImage);
							
							LOGGER.log(Level.INFO, "##RTADCM##  isImageDataChange {0}", isImageDataChange);
							
							if(isImageDataChange) {
								reviseGraphicElement(context,strMCId,eachClaimMap,plCategories,copyListId,strCRId,crInfoMap);
							}else if(!isImageDataChange ) {
								processGraphicObject(context,prevClaimInfoMap,eachClaimMap,plCategories,copyListId,strCRId,crInfoMap,strClaimGraphicalImage);
				       	 	}
							else {
								// 625 : TBD WRITE TO CONNECT MGE TO CLAIM NEW REV AND CONNECT TO NEW CL , DISCONNECT FROM OLD CLAIM REV
								connectUnRevisedMCEtoRevisedClaim(context,eachClaimMap,strClaimId,copyListId);
								//RTA 22x Added for ALM-49635 starts
								setSequenceNumberForUnRevisedMCE(context,copyListId,eachClaimMap,false,strSequenceNumber);
								//RTA 22x Added for ALM-49635 ends
								//RTA 22x Added for ALM-46415 starts
								updateValidityDateForUnRevisedMCE(context,eachClaimMap,strExpirationDate);
								//RTA 22x Added for ALM-46415 ends
							}
						}
						else
						{
							LOGGER.log(Level.INFO, "##RTADCM##  Graphic Element not processed as not packaging type {0}", strClaimId);
						}
					}
					else {
						// MCE Revision
						
						// TBD: Handle Claim & Disclaimer Revision
						//DCM:US-4383 RTA DCM Integration Code changes as per New data Model Changes  - Start	
						
						if(eachClaimMap.containsKey("IsDisclaimer")) {
							createMCE(context,eachClaimMap,eachClaimMap,true,plCategories,copyListId,strCRId,writer);
						}
						else
						{
							createMCE(context,eachClaimMap,null,false,plCategories,copyListId,strCRId,writer);
						}
						//DCM:US-4383 RTA DCM Integration Code changes as per New data Model Changes  - End	
					}
				} else {
					//This is for new data creation
					if(strClaimCET_.contains("Graphic")) { // only for Graphic Elements
						createGraphicElement(context,eachClaimMap,plCategories,copyListId,strCRId,crInfoMap);
					} else {
						
						// TBD: CHange to get Disclaimer DATA by connected REL instead of Object
						//DCM:US-4383 RTA DCM Integration Code changes as per New data Model Changes  - Start	
						if(eachClaimMap.containsKey("IsDisclaimer")) {
							createMCE(context,eachClaimMap,eachClaimMap,true,plCategories,copyListId,strCRId,writer);
						}
						else
						{
							createMCE(context,eachClaimMap,null,false,plCategories,copyListId,strCRId,writer); 
						}
						//DCM:US-4383 RTA DCM Integration Code changes as per New data Model Changes  - End	
					}
				}						
			}
					
					CopyList clObject = new CopyList(copyListId);
					//set owner for copy list
					setPrimaryOwnership(context, copyListId);
					clObject.setOwner(context, crACE);
					updateInstanceSequenceOnCopyListMasters(context,clObject);
					MapList mlCLElements = clObject.getArtworkElements(context);
					LOGGER.log(Level.INFO, "##RTADCM## 6.1. MCE Elements {0}", mlCLElements);
					writer.print("\n\n@@@Master Copies connected to Copylist ------------>"+mlCLElements);

					// Below condition is for 1st revision of CLR as CL connect to MCE. For revise CLR>CL is not connected to MCE
					if(!mlCLElements.isEmpty()) {
						// Incase if region/category is changed on CRL revision then get those CPG to obsolete CL and disconnect
						List plCategoriesUpdated = plCategories;
						
						if(crInfoMap.containsKey("previous.from["+REL_PGCOPYLIST+"].to.id"))
						{
							String strOldCLId = (String) crInfoMap.get("previous.from["+REL_PGCOPYLIST+"].to.id");
							plCategoriesUpdated = getUpdatedCategories(context, plCategories, strOldCLId);
						}

						LOGGER.log(Level.INFO, "##RTADCM## plCategoriesUpdated-> {0} ", plCategoriesUpdated);
						LOGGER.log(Level.INFO, "##RTADCM## plCategories-> {0}", plCategories);
						LOGGER.log(Level.INFO, "##RTADCM## copyListId passed to obsoleteOldCL ::: ", copyListId);
						obsoleteOldCL(context,plCategoriesUpdated,copyListId);
						LOGGER.log(Level.INFO, "completed obsoleteOldCL method exec ");
						// TBD - verify old copy gets disconnected. WHY BELOW CODE NEEDED?
						disconnectPrevCLFromCPG(context, plCategoriesUpdated);
						LOGGER.log(Level.INFO, "completed disconnectPrevCLFromCPG method exec ");
						for(int ct=0;ct<plCategories.size();ct++){
							String catID = (String) plCategories.get(ct);
							if(catID!=null && !"".equals(catID))
							    // CopyList is added in BrandHierarchy-
								connectCPGToCopyList(context,copyListId,catID);
								writer.print("\n\n@@@CopyList is added in BrandHierarchy");
								syncMCfromCLwithCPG(context,copyListId,catID,crACE);
						}

						// if region or category changed on CRL revision then disconnect MCE from CPG not having active CL
						for(int ct=0;ct<plCategoriesUpdated.size();ct++){
							String catID = (String) plCategoriesUpdated.get(ct);

							if(catID!=null && !"".equals(catID) && !plCategories.contains(catID))
								disconnectMCEfromCPG(context,catID);
						}
				
						LOGGER.log(Level.INFO, "##RTADCM## @@@@@@@@@@@@@@@@@@@@2-> {0} ", context.getUser());
						
						if(hasCRConnectedToCL!=null && !"TRUE".equals(hasCRConnectedToCL)) {
							LOGGER.log(Level.INFO, "##RTADCM## 11111111111111111-> {0} ", context.getUser());
				
							ContextUtil.pushContext(context);
							isContextPushedUserAgent = true;
							LOGGER.log(Level.INFO, "##RTADCM## 33333333333333333333-> {0} ", context.getUser());
							callConnect(context,strCRId,REL_PGCOPYLIST,copyListId,null);							
							writer.print("\n\n@@@CopyList is connected to Claim Request");
							DomainObject doCLID = null;
							for(int k=0;k<2;k++) {
								doCLID = DomainObject.newInstance(context,copyListId);
								doCLID.promote(context);
							}
							
							//Create a Route
							createRoute(context,crACE,copyListId,crInfoMap);								
							writer.print("\n\n@@@Route is Created");
							
							if(isContextPushedUserAgent){
								ContextUtil.popContext(context);
								isContextPushedUserAgent = false;

								// Data is created with RTA Integration user context
								LOGGER.log(Level.INFO, "##RTADCM## 4444444444444444444444444-> {0} ", context.getUser());
							}															
						}
						LOGGER.log(Level.INFO, " ##### RTA Data is created... #####");
						writer.print("\n\n@@@@@@RTA Data is created ");
					}					
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.toString());
			LOGGER.log(Level.SEVERE, e.getMessage());
			throw e;			
		} finally {
			LOGGER.log(Level.INFO, "##RTADCM## Finally User 1-> {0} ", context.getUser());
			LOGGER.log(Level.INFO, "##RTADCM## Finally isContextPushedUserAgent 1-> {0} ", isContextPushedUserAgent);
			LOGGER.log(Level.INFO, "##RTADCM## Finally isContextPushed 1-> {0} ", isContextPushed);
			
			if(isContextPushedUserAgent)
			{
				ContextUtil.popContext(context);
			}

			LOGGER.log(Level.INFO, "##RTADCM## RTADCM## Finally User 2-> {0} ", context.getUser());
			if(isContextPushed)
			{
				ContextUtil.popContext(context);
				LOGGER.log(Level.INFO, "##RTADCM## FINALY USER-> createCLMCEData {0} ", context.getUser());
			}
		}	
		return STR_SUCCESS;
	}
	public void connectCPGToCopyList(Context context,String copyListId,String catID) throws Exception{
		LOGGER.log(Level.INFO, "connectCPGToCopyList - START");
		try {
			
			StringList strCPGIdList = BusinessUtil.getInfoList(context, copyListId, "to["+RELATIONSHIP_ASSOCIATEDCOPYLIST+"].from.id");
			
			if(!strCPGIdList.contains(catID)) {
				callConnect(context,catID,RELATIONSHIP_ASSOCIATEDCOPYLIST,copyListId,null);
			}
			LOGGER.log(Level.INFO, "connectCPGToCopyList - END");
		} catch (Exception e) {
			throw e;
		} 
	}

	public StringList getClaimSelect(){
		StringList slClaimSelects = new StringList();
		slClaimSelects.add(DomainConstants.SELECT_ID);
		slClaimSelects.add(DomainConstants.SELECT_NAME);
		slClaimSelects.add(DomainConstants.SELECT_REVISION);
		slClaimSelects.add("first");
		slClaimSelects.add("attribute["+ATTRIBUTE_PGCOPYELEMENTTYPE+"]");
		slClaimSelects.add("attribute["+ATTRIBUTE_PGCLAIMNAME+"]");
		slClaimSelects.add("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.name");
		slClaimSelects.add("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id");
		slClaimSelects.add("attribute["+ATTRIBUTE_PGCLAIMNAME_RTE+"]");
		slClaimSelects.add(DomainConstants.SELECT_NAME);
		slClaimSelects.add("attribute["+ATTRIBUTE_PGCLAIMGRAPHICALIMAGE+"]");
		slClaimSelects.add("previous.attribute["+ATTRIBUTE_PGCLAIMGRAPHICALIMAGE+"]");
		slClaimSelects.add("previous.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id");
		//RTA 22x Added for ALM-46415 starts
		slClaimSelects.add("attribute["+ATTRIBUTE_PGEXPIRATIONDATE+"]");
		//RTA 22x Added for ALM-46415 ends
		return slClaimSelects;
	}
	//RTA 22x Added for ALM-49635 starts
	public StringList getClaimRelSelect() {
		StringList slClaimRelSelects = new StringList();
		//DCM:US-4383 RTA DCM Integration Code changes as per New data Model Changes  - Start
		StringBuffer sbPackCompType = new StringBuffer("frommid[").append(RELATIONSHIP_PG_CLAIM_PRODUCT_CONFIGURATION).append("].to.attribute[").append(ATTRIBUTE_PGPACKCOMPONENTTYPE).append("]");
		//DCM:US-4383 RTA DCM Integration Code changes as per New data Model Changes  - End
		slClaimRelSelects.add("attribute["+ATTRIBUTE_CLAIMSEQUENCE+"]");
		//DCM:US-4383 RTA DCM Integration Code changes as per New data Model Changes  - Start
		slClaimRelSelects.add(new StringBuffer("attribute[").append(ATTRIBUTE_PGEXECUTIONTYPE).append("]").toString()); 
		slClaimRelSelects.add(sbPackCompType.toString());
		//DCM:US-4383 RTA DCM Integration Code changes as per New data Model Changes  - end
		return slClaimRelSelects;
	}
	
	public StringList getDisClaimerSelect(){
		StringList slDisclaimerSelects = new StringList();
		slDisclaimerSelects.add(DomainConstants.SELECT_ID);
		slDisclaimerSelects.add(DomainConstants.SELECT_NAME);
		slDisclaimerSelects.add(DomainConstants.SELECT_REVISION);
		slDisclaimerSelects.add("first");
		slDisclaimerSelects.add("attribute["+ATTRIBUTE_PGCOPYELEMENTTYPE+"]");
		slDisclaimerSelects.add("attribute["+ATTRIBUTE_PGDISCLAIMER+"]");
		slDisclaimerSelects.add("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.name");
		slDisclaimerSelects.add("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id");
		slDisclaimerSelects.add("attribute["+ATTRIBUTE_PGDISCLAIMER_RTE+"]");
		slDisclaimerSelects.add(DomainConstants.SELECT_NAME);
		slDisclaimerSelects.add("attribute["+ATTRIBUTE_PGCLAIMGRAPHICALIMAGE+"]");
		slDisclaimerSelects.add("previous.attribute["+ATTRIBUTE_PGCLAIMGRAPHICALIMAGE+"]");
		slDisclaimerSelects.add("previous.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id");
		//RTA 22x Added for ALM-46415 starts
		slDisclaimerSelects.add("attribute["+ATTRIBUTE_PGEXPIRATIONDATE+"]");
		
		slDisclaimerSelects.add("previous.id");
		slDisclaimerSelects.add("previous.attribute["+ATTRIBUTE_PGDISCLAIMER+"]");
		slDisclaimerSelects.add("previous.attribute["+ATTRIBUTE_PGDISCLAIMER_RTE+"]");
		slDisclaimerSelects.add("previous.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id");
		slDisclaimerSelects.add("previous.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].id");
		slDisclaimerSelects.add("previous.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.type");
		return slDisclaimerSelects;
	}
	//RTA 22x Added for ALM-49635 starts
	public StringList getDisClaimerRelSelect() {
		StringList slDisclaimerRelSelects = new StringList();
		StringBuffer sbPackCompType = new StringBuffer("frommid[").append(RELATIONSHIP_PG_CLAIM_PRODUCT_CONFIGURATION).append("].to.attribute[").append(ATTRIBUTE_PGPACKCOMPONENTTYPE).append("]");
		slDisclaimerRelSelects.add(new StringBuffer("attribute[").append(ATTRIBUTE_PGEXECUTIONTYPE).append("]").toString()); 
		slDisclaimerRelSelects.add("attribute["+ATTRIBUTE_CLAIMSEQUENCE+"]");
		slDisclaimerRelSelects.add(sbPackCompType.toString());

		return slDisclaimerRelSelects;
	}
	
	
	public MapList getClaimData(Context context, DomainObject doCR) throws Exception{
		

		//DCM:US-4383 RTA DCM Integration Code changes as per New data Model Changes  - Start
		Map mpEachClaimInfo = null;
		Map mpEachDisClaimerInfo = null;
		//Map mapDisclaimer = null;
		String strDisclaimerId= DomainConstants.EMPTY_STRING;
		String strDisclaimerName=DomainConstants.EMPTY_STRING;
		String strDisclaimer=DomainConstants.EMPTY_STRING;
		String strDisclaimerRTE=DomainConstants.EMPTY_STRING;
		String strDisclaimerRev =DomainConstants.EMPTY_STRING;
		String strExpirationDate = DomainConstants.EMPTY_STRING;
		String strDisClaimerEXT = DomainConstants.EMPTY_STRING;
		String strFirstRev = DomainConstants.EMPTY_STRING;
		String strPrevDisclaimerId=DomainConstants.EMPTY_STRING;
		String strPrevDisclaimer=DomainConstants.EMPTY_STRING;
		String strPrevDisclaimerRTE=DomainConstants.EMPTY_STRING;
		StringList slDisclaimerList = new StringList();
		StringBuffer sbPackCompType = new StringBuffer("frommid[").append(RELATIONSHIP_PG_CLAIM_PRODUCT_CONFIGURATION).append("].to.attribute[").append(ATTRIBUTE_PGPACKCOMPONENTTYPE).append("]");
		Object obj = null;
		
		//DCM:US-4383 RTA DCM Integration Code changes as per New data Model Changes  - End
		MapList mlClaimsInfo = doCR.getRelatedObjects(context,    // context object reference
		REL_PGCLAIMS, // relationship name
		TYPE_PGCLAIM, // connected type name 
		getClaimSelect(), // object selects
		getClaimRelSelect(), // rel selects
		false, //get To
		true,  // fet From
		(short)1, //recurse level
		null, // where clause
		null,// rel where clause
		0); //limit
		
		//DCM:US-4383 RTA DCM Integration Code changes as per New data Model Changes  - Start
		MapList mlDisclaimerClaimsInfo = doCR.getRelatedObjects(context,    // context object reference
				RELATIONSHIP_PGDISCLAIMER, // relationship name
				TYPE_PGDISCLAIMER, // connected type name 
				getDisClaimerSelect(), // object selects
				getDisClaimerRelSelect(), // rel selects
				false, //get To
				true,  // fet From
				(short)1, //recurse level
				null, // where clause
				null,// rel where clause
				0); //limit
		
		
		for(int i=0 ; i< mlDisclaimerClaimsInfo.size(); i++)
		{
			mpEachDisClaimerInfo = (Map)mlDisclaimerClaimsInfo.get(i);
			obj = (Object)mpEachDisClaimerInfo.get(sbPackCompType.toString());
			mpEachDisClaimerInfo.put("attribute["+ATTRIBUTE_PGPACKCOMPONENTTYPE+"]", obj);
			
			strDisclaimerId=(String)mpEachDisClaimerInfo.get(DomainConstants.SELECT_ID);
			strDisclaimerName=(String)mpEachDisClaimerInfo.get(DomainConstants.SELECT_NAME);
			strDisclaimer=(String)mpEachDisClaimerInfo.get("attribute["+ATTRIBUTE_PGDISCLAIMER+"]");
			strDisclaimerRTE=(String)mpEachDisClaimerInfo.get("attribute["+ATTRIBUTE_PGDISCLAIMER_RTE+"]");
			strDisclaimerRev =(String)mpEachDisClaimerInfo.get(DomainConstants.SELECT_REVISION);
			strExpirationDate = (String)mpEachDisClaimerInfo.get("attribute["+ATTRIBUTE_PGEXPIRATIONDATE+"]");
			strDisClaimerEXT = checkForStringList(mpEachDisClaimerInfo,ATTRIBUTE_PGEXECUTIONTYPE);
			strFirstRev = (String)mpEachDisClaimerInfo.get("first");
			
			//mapDisclaimer=new HashMap();
			mpEachDisClaimerInfo.put("strDisclaimerId", strDisclaimerId);
			mpEachDisClaimerInfo.put("strDisclaimerName", strDisclaimerName);
			mpEachDisClaimerInfo.put("strDisclaimer", strDisclaimer);
			mpEachDisClaimerInfo.put("strDisclaimerRTE", strDisclaimerRTE);
			mpEachDisClaimerInfo.put("strDisclaimerREV", strDisclaimerRev);
			mpEachDisClaimerInfo.put("strExpirationDate", strExpirationDate);
			mpEachDisClaimerInfo.put("IsDisclaimer", "true");
			

			// Disclaimer can be 1st revision and connected to Claim 2nd revision
			if(null != mpEachDisClaimerInfo.get("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id"))
			mpEachDisClaimerInfo.put("strDisclaimerMCId",mpEachDisClaimerInfo.get("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id"));
			
			if(null != mpEachDisClaimerInfo.get("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.type"))
			mpEachDisClaimerInfo.put("strDisclaimerMCType",mpEachDisClaimerInfo.get("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.type"));
			
			if(BusinessUtil.isNotNullOrEmpty(strFirstRev) && !strDisclaimerRev.equals(strFirstRev)){
				strPrevDisclaimerId=(String)mpEachDisClaimerInfo.get("previous.id");
				strPrevDisclaimer=(String)mpEachDisClaimerInfo.get("previous.attribute["+ATTRIBUTE_PGDISCLAIMER+"]");
				strPrevDisclaimerRTE=(String)mpEachDisClaimerInfo.get("previous.attribute["+ATTRIBUTE_PGDISCLAIMER_RTE+"]");
				mpEachDisClaimerInfo.put("strPrevDisclaimerId", strPrevDisclaimerId);
				mpEachDisClaimerInfo.put("strPrevDisclaimer", strPrevDisclaimer);
				mpEachDisClaimerInfo.put("strPrevDisclaimerRTE", strPrevDisclaimerRTE);
				
				if(null != mpEachDisClaimerInfo.get("previous.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id"))
				mpEachDisClaimerInfo.put("strPrevDisclaimerMCId", mpEachDisClaimerInfo.get("previous.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id"));
				
				
				if(null != mpEachDisClaimerInfo.get("previous.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.type"))
				mpEachDisClaimerInfo.put("strPrevDisclaimerMCType",mpEachDisClaimerInfo.get("previous.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.type"));

			}
			
		}
		
		for(int i=0 ; i< mlClaimsInfo.size(); i++)
		{
			mpEachClaimInfo = (Map)mlClaimsInfo.get(i);
			obj = (Object)mpEachClaimInfo.get(sbPackCompType.toString());
			mpEachClaimInfo.put("attribute["+ATTRIBUTE_PGPACKCOMPONENTTYPE+"]", obj);
		}
		
		mlClaimsInfo.addAll(mlDisclaimerClaimsInfo)	;
		//DCM:US-4383 RTA DCM Integration Code changes as per New data Model Changes  - End
		return mlClaimsInfo;
	}
	//RTA 22x Added for ALM-49635 ends
	public StringList getCLRSelects() {
	StringList crSelects = new StringList();
	crSelects.add("attribute["+ATTRIBUTE_PGCLAIMACECONTACT+"]");
	crSelects.add(DomainConstants.SELECT_NAME);
	crSelects.add(DomainConstants.SELECT_REVISION);
	crSelects.add("from["+REL_PGCOPYLIST+"]");
	crSelects.add("previous.from["+REL_PGCOPYLIST+"].to.id");
	crSelects.add("first");
	crSelects.add("previous.id");
	crSelects.add("name");
	return crSelects;
	}
	
	public String createCL(Context context, Map crInfoMap, MapList mlClaimsInfo) throws Exception
	{
		LOGGER.log(Level.INFO, "createCL - START");
		try {
		List<String> pgPackComponentTypeList = new ArrayList<String>();					
	
		String strpgPackComponentType=null;	
		StringList slPackComponentType=null;
		//Iterate claims and consolidate Claims Pack component type values and add them into CL's Artwork usage.
		LOGGER.log(Level.INFO, "##RTADCM##createCL {0}", mlClaimsInfo);
		for (int i=0; i<mlClaimsInfo.size(); i++) {
			Map eachClaimMap= (Map) mlClaimsInfo.get(i);
			StringBuilder sbPackComponentType=new StringBuilder();
			//LOGGER.log(Level.INFO, "##RTADCM##createCL eachClaimMap== {0}", eachClaimMap);

			//strpgPackComponentType   = (String)eachClaimMap.get("attribute["+ATTRIBUTE_PGPACKCOMPONENTTYPE+"]");
			if(eachClaimMap!=null) {
				strpgPackComponentType   =checkForStringList(eachClaimMap,ATTRIBUTE_PGPACKCOMPONENTTYPE);
			}
			if(BusinessUtil.isNotNullOrEmpty(strpgPackComponentType)) {
				sbPackComponentType.append(strpgPackComponentType);
			}
			LOGGER.log(Level.INFO, "##RTADCM##1111111111createCL strpgPackComponentType== {0}", strpgPackComponentType);
			LOGGER.log(Level.INFO, "##RTADCM##2222222222createCL strpgPackComponentType== {0}", sbPackComponentType);
			if(eachClaimMap!=null && eachClaimMap.containsKey("from["+RELATIONSHIP_PGDISCLAIMER+"].to.id")) {
				if(eachClaimMap.get("from["+RELATIONSHIP_PGDISCLAIMER+"].to.attribute["+ATTRIBUTE_PGPACKCOMPONENTTYPE+"]") instanceof StringList ){
					slPackComponentType = (StringList)eachClaimMap.get("from["+RELATIONSHIP_PGDISCLAIMER+"].to.attribute["+ATTRIBUTE_PGPACKCOMPONENTTYPE+"]");
					slPackComponentType=processPackComponentType(context,slPackComponentType);
					strpgPackComponentType=FrameworkUtil.join(slPackComponentType, ",");
				}else {
					strpgPackComponentType = (String)eachClaimMap.get("from["+RELATIONSHIP_PGDISCLAIMER+"].to.attribute["+ATTRIBUTE_PGPACKCOMPONENTTYPE+"]");
				}

				LOGGER.log(Level.INFO, "##RTADCM##createCL strpgPackComponentType== {0}", strpgPackComponentType);
				if(BusinessUtil.isNotNullOrEmpty(strpgPackComponentType) && sbPackComponentType.length() > 0) {
					sbPackComponentType.append(",");
					sbPackComponentType.append(strpgPackComponentType);
				}
				else
				{
					sbPackComponentType.append(strpgPackComponentType);
				}
			}
			
			strpgPackComponentType=sbPackComponentType.toString();
			if(strpgPackComponentType!=null && !"".equals(strpgPackComponentType)){
				String[] arrr_ = strpgPackComponentType.split(",");
				for(int z=0;z<arrr_.length;z++) {
					if(!pgPackComponentTypeList.contains(arrr_[z])){
						pgPackComponentTypeList.add(arrr_[z]);
					}
				}
			}											
		}

		LOGGER.log(Level.INFO, "##RTADCM##createCL pgPackComponentTypeList FINAL == {0}", pgPackComponentTypeList);

		//Below array uses 'Artwork Usage' attribute on CopyList					
		String[] array1 = pgPackComponentTypeList.toArray(new String[pgPackComponentTypeList.size()]);

		//CLaimRequest name and rev = CopyList title
		String crName = (String) crInfoMap.get(DomainConstants.SELECT_NAME);
		String crRev  = (String) crInfoMap.get(DomainConstants.SELECT_REVISION);
		String strCRId  = (String) crInfoMap.get(DomainConstants.SELECT_ID);
		String name = null;
		
		// Build CL Title & Description
		StringBuilder title = new StringBuilder();					
		title.append(STRDCM);
		title.append(STRTILDE);
		title.append(crName);
		title.append(STRTILDE);
		title.append(crRev);
		
		//CopyList desc = ClaimRequest name~ClaimRequest Rev
		StringBuilder clDesc = new StringBuilder();					
		clDesc.append(crName);
		clDesc.append(STRTILDE);
		clDesc.append(crRev);					
							
		// ** Create CopyList					
		String copyListId   = "";
		String strCountries = getCountries(context,strCRId,copyListId,crInfoMap);
		String strLangs     = getLanguages(context,strCRId,copyListId,crInfoMap);

		if(!pgPackComponentTypeList.isEmpty()) {
			copyListId = CopyList.create(context, name, title.toString(), clDesc.toString(), array1, null, strCountries, strLangs);
		} else {
			// if ClaimRequest doesnt have pgPackComponentType, then include all ArtworkUsageRanges.
			Map<String, StringList> artworkUsageList = getArtworkUsageRanges(context,null);
			StringList artworkUsageFields        = (StringList) artworkUsageList.get(AWLConstants.RANGE_FIELD_CHOICES);
			String[] array = new String[artworkUsageFields.size()];			 
			for (int i = 0; i < artworkUsageFields.size(); i++) {
				array[i] = artworkUsageFields.get(i);
			}
			copyListId = CopyList.create(context, name, title.toString(), clDesc.toString(), array, null, strCountries, strLangs);
		}
		//RTA DS 2022x.04 Added for ALM Requirement 47891 - Start
		if(BusinessUtil.isNotNullOrEmpty(copyListId)){
			DomainObject.newInstance(context, copyListId).setAttributeValue(context, PropertyUtil.getSchemaProperty(context, "attribute_pgCopyListType"), "DCM");
		}
		//RTA DS 2022x.04 Added for ALM Requirement 47891 - End
		LOGGER.log(Level.INFO, "createCL - END");
		return copyListId;
		}catch(Exception e){
			throw e;
		}
	}

	public static StringList processPackComponentType(Context context,StringList slPackComponentType)throws Exception{
		StringList slFinalPackCompType=new StringList();
		LOGGER.log(Level.INFO, "processPackComponentType slPackComponentType - {0}", slPackComponentType);
		for(String strPackComp:slPackComponentType) {
			if(BusinessUtil.isNotNullOrEmpty(strPackComp)) {
				slFinalPackCompType.add(strPackComp);
			}
		}
		LOGGER.log(Level.INFO, "processPackComponentType slFinalPackCompType - {0}", slFinalPackCompType);
		return slFinalPackCompType;
	}

	public static boolean getClaimsUsingPackagingArtwork(Context context,MapList mlClaimsInfo)throws Exception{
		LOGGER.log(Level.INFO, "getClaimsUsingPackagingArtwork - START");
		boolean isClaimsUsingPackagingArtwork = false;
		Map eachClaimMap=null;
		String strClaimExT=null;
		for (int i=0; i<mlClaimsInfo.size(); i++) {
			eachClaimMap     = (Map) mlClaimsInfo.get(i);
			//strClaimExT   = (String) eachClaimMap.get("attribute["+ATTRIBUTE_PGEXECUTIONTYPE+"]");
			strClaimExT   = checkForStringList(eachClaimMap,ATTRIBUTE_PGEXECUTIONTYPE);
			if(strClaimExT!=null && strClaimExT.contains("Packaging Artwork")) {
				isClaimsUsingPackagingArtwork = true;
			}
		}
		LOGGER.log(Level.INFO, "getClaimsUsingPackagingArtwork - END");
		return isClaimsUsingPackagingArtwork;
	}

	public static String getCountries(Context context,String strCRId,String copyListId, Map crInfoMap)throws Exception{
		LOGGER.log(Level.INFO, "getCountries - START");
		String type_Country = PropertyUtil.getSchemaProperty(context,"type_Country");
		String strWhere = "("+DomainConstants.SELECT_CURRENT+ " == \""+STATE_ACTIVE+"\")";
		StringBuffer sbCountries = new StringBuffer();
		
		try {
			if(crInfoMap!=null && crInfoMap.containsKey("attribute["+ATTRIBUTE_PGINTENDEDMARKETS+"]")) {
				//String crCountries    = (String) crInfoMap.get("attribute["+ATTRIBUTE_PGINTENDEDMARKETS+"]");
				String crCountries    = checkForStringList(crInfoMap,ATTRIBUTE_PGINTENDEDMARKETS);
				String[] arrCountries = crCountries.split(",");
				List claimsCountries  = Arrays.asList(arrCountries);
				
				StringList objectSelects = new StringList();
				objectSelects.add(DomainConstants.SELECT_NAME);
				objectSelects.add(DomainConstants.SELECT_ID);
				MapList countriesList  = DomainObject.findObjects(context,  // context object reference
																type_Country, // type pattern
																ESERPROD, // vault pattern
																strWhere, // where condition
																objectSelects); // object selectables
				
				int k = 0;

				for(int i=0;i<countriesList.size();i++) {
					Map map            = (Map) countriesList.get(i);
					String strCtryName = (String) map.get(DomainConstants.SELECT_NAME);
					String strCtryID   = (String) map.get(DomainConstants.SELECT_ID);
					
					if(strCtryName!=null && claimsCountries.contains(strCtryName)) {
						if(sbCountries.length() > 0 ) {
							sbCountries.append(",");
						} 
						sbCountries.append("{\"seq\":\"");
						sbCountries.append(k); // TBD LOW Change Counter so it is 1, 2, 3 , Print JSON to understand issue
						sbCountries.append("\",\"id\":\"");
						sbCountries.append(strCtryID);
						sbCountries.append("\",\"name\":\"");
						sbCountries.append(strCtryName);
						sbCountries.append("\"}");
						k = k + 1;
					} 
				}
				LOGGER.log(Level.INFO, "CL is connected to Countries...{0}", claimsCountries);
				LOGGER.log(Level.INFO, "##RTADCM## sbCountries.."+sbCountries.toString());
			}
			LOGGER.log(Level.INFO, "getCountries - END");		
		}catch (Exception e){
			LOGGER.log(Level.SEVERE, "Issue with connecting countries/Languate to CL.."+copyListId);
			LOGGER.log(Level.SEVERE, e.getMessage());
			throw e;
		}
		return sbCountries.toString();	
	}
	
	public static String getLanguages(Context context,String strCRId,String copyListId, Map crInfoMap)throws Exception{
		LOGGER.log(Level.INFO, "getLanguages - START");
		String type_LocalLanguage = PropertyUtil.getSchemaProperty(context,"type_LocalLanguage");
		String strEngLang = "English_US";
		StringBuffer sbLangs = new StringBuffer();
		try {
			if(crInfoMap!=null && crInfoMap.containsKey("attribute["+ATTRIBUTE_PGINTENDEDMARKETS+"]")) {
				StringList objectSelects1 = new StringList();
				objectSelects1.add(DomainConstants.SELECT_NAME);
				objectSelects1.add(DomainConstants.SELECT_ID);
				MapList langsList  = DomainObject.findObjects(context, // context object reference
													type_LocalLanguage, // type patterns
													strEngLang,  // name pattern
													DomainConstants.QUERY_WILDCARD, // revision pattern
													DomainConstants.QUERY_WILDCARD, // owner pattern
													ESERPROD, // vault patterns
													null, // where condition
													false, // expand type
													objectSelects1); // object selects

				
				for(int i=0;i<langsList.size();i++) {
					Map map            = (Map) langsList.get(i);
					String strLangName = (String) map.get(DomainConstants.SELECT_NAME);
					String strLangId   = (String) map.get(DomainConstants.SELECT_ID);
					if(i > 0 ) {
						sbLangs.append(",");
					} 
					sbLangs.append("{\"seq\":\"");
					sbLangs.append(i);
					sbLangs.append("\",\"id\":\"");
					sbLangs.append(strLangId);
					sbLangs.append("\",\"name\":\"");
					sbLangs.append(strLangName);
					sbLangs.append("\"}");					 
				}
				//LOGGER.log(Level.SEVERE, "##RTADCM## sbLangs.."+sbLangs.toString());
			}
		LOGGER.log(Level.INFO, "getLanguages - END");
		}catch (Exception e){
			LOGGER.log(Level.SEVERE, "Issue with connecting countries/Languate to CL.."+copyListId);
			LOGGER.log(Level.SEVERE, e.getMessage());
			throw e;
		}
		
		return sbLangs.toString();
	}
	
	public static List 	prepareProductLineToPlaceCL(Context context,Map crInfoMap, String claimReqId) throws Exception{
		LOGGER.log(Level.INFO, "prepareProductLineToPlaceCL - START");
		List CLR_PL_List    = new ArrayList();

		try {
			String strProductType                 = PropertyUtil.getSchemaProperty(context,"type_ProductType");
			String strSubProdLineRel              = PropertyUtil.getSchemaProperty(context,"relationship_SubProductLines");
			String strMarketingNameAtt            = PropertyUtil.getSchemaProperty(context,"attribute_MarketingName");			
			String relationship_DesignResponsibility = PropertyUtil.getSchemaProperty(context,"relationship_DesignResponsibility");
			String attribute_Region    = PropertyUtil.getSchemaProperty(context,"attribute_pgClaimRegion");
			String strSel1             = "from["+strSubProdLineRel+"].to.attribute["+strMarketingNameAtt+"]";
			String strSel2             = "from["+strSubProdLineRel+"].to.id";
		
			StringList objectSelects   = new StringList();
			objectSelects.add(DomainConstants.SELECT_NAME);
			objectSelects.add(DomainConstants.SELECT_ID);
		
			MapList productsList      = DomainObject.findObjects(context, // context object reference
															strProductType, // type pattern
															CDM_ADMIN, // name pattern
															DomainConstants.QUERY_WILDCARD, // revision pattern
															DomainConstants.QUERY_WILDCARD, // owner pattern
															ESERPROD, // vault pattern
															null, // where condition
															false, // expand type
															objectSelects // object selects
															);
			
			MapList companyList       = DomainObject.findObjects(context,			// context object reference
																	TYPE_COMPANY, 	// type pattern
																	STR_PG_COMPANY, 	// name pattern
																	STR_HYPHEN,		// rev pattern
																	DomainConstants.QUERY_WILDCARD, // owner pattern
																	ESERPROD, 		// vault pattern
																	null,			// where condition
																	false, 			// expand type
																	objectSelects	// object selects
																	);

			LOGGER.log(Level.INFO, "##RTADCM## productsList..", productsList);
			LOGGER.log(Level.INFO, "##RTADCM## companyList..", companyList);

			String pgCompanyObjectID  = "";
			for(int ik=0;ik<companyList.size();ik++) {
				Map cMap = (HashMap) companyList.get(ik);
				pgCompanyObjectID    = (String) cMap.get(DomainConstants.SELECT_ID);
			}

			StringList regionSelects   = new StringList();
			regionSelects.add(strSel1);
			regionSelects.add(strSel2);
			regionSelects.add(DomainConstants.SELECT_ID);
			regionSelects.add(DomainConstants.SELECT_NAME);
			
			String crName = (String) crInfoMap.get(DomainConstants.SELECT_NAME);
			String crRev  = (String) crInfoMap.get(DomainConstants.SELECT_REVISION);
			
			//getting regions from ClaimRequest to create ProductType in CDM Hierarchy
			//String crRegions = (String) crInfoMap.get("attribute["+attribute_Region+"]");
			String crRegions = checkForStringList(crInfoMap,ATTRIBUTE_REGION);
			crRegions=setEmptyRegionCategory(context,crRegions);
			String[] arrCRRegions = crRegions.split(",");

			//getting categories from ClaimRequest to create ProductType in CDM Hierarchy

			//String crCategories = (String) crInfoMap.get("attribute["+ATTRIBUTE_PGCATEGORY+"]");
			//RTA 22x Added for ALM-51831 starts
			//String crCategories = checkForStringList(crInfoMap,ATTRIBUTE_PGCATEGORY);
			String crCategories = checkForStringList(crInfoMap,ATTRIBUTE_PGBUSINESSAREA);
			//RTA 22x Added for ALM-51831 ends
			crCategories=setEmptyRegionCategory(context,crCategories);
			String[] arrCRCategories = crCategories.split(",");
			

			List ipList = new ArrayList();
			StringList cat_Selects = new StringList();
			cat_Selects.add("to["+DomainConstants.RELATIONSHIP_PROTECTED_ITEM+"].from.name");
		
			// Need to push context since awluser doesnt have access of Claim objects. Getting classification information from ClaimRequest
			Map ipClassInfoFromCR =  getMapDataWithPushContext(context,claimReqId,cat_Selects);
			if(ipClassInfoFromCR!=null) {
				StringList slIPClass = (StringList) ipClassInfoFromCR.get("to["+DomainConstants.RELATIONSHIP_PROTECTED_ITEM+"].from.name");
				
				StringList cat_Selects_ = new StringList();
				cat_Selects_.add(DomainConstants.SELECT_ID);
				for(int z=0;z<slIPClass.size();z++) {
					String eaIPCls = (String) slIPClass.get(z);
					MapList IPCLASSList=getIPClassList(context,eaIPCls,cat_Selects_);
					String IPCLASSid  = "";
					for(int ik=0;ik<IPCLASSList.size();ik++) {
						Map cMap = (Map) IPCLASSList.get(ik);
						IPCLASSid    = (String) cMap.get(DomainConstants.SELECT_ID);
						ipList.add(IPCLASSid);
					}
				}
			}

			//productsList contains only one object info i.e CDM Admin
			MapList mlProductList = BusinessUtil.getInfoList(context,BusinessUtil.toStringList(productsList, DomainConstants.SELECT_ID),regionSelects);
			for(int i=0;i<mlProductList.size();i++) {
		
				Map regionInfoMap=(HashMap)mlProductList.get(i);
			
				String strCDMID    = (((StringList) regionInfoMap.get(DomainConstants.SELECT_ID)).get(0));
				if(regionInfoMap.containsKey(strSel1)) {
					StringList cdmRegions   = (StringList) regionInfoMap.get(strSel1);
					StringList cdmRegionIds = (StringList) regionInfoMap.get(strSel2);
					String autoName = "";
					
					for(int j=0;j<arrCRRegions.length;j++) {
						String eachCRRegion = arrCRRegions[j];
						//new4
						// if DCM region is EUROPE/IMEA then map it to  "EIMEA" in RTA
						eachCRRegion=getDCMRTARegionMapping(context,eachCRRegion);
						
						if(!cdmRegions.contains(eachCRRegion)) {
							//create Region object and connect it to CDM Admin ##
							DomainObject newRegObject = new DomainObject();
							autoName = DomainObject.getAutoGeneratedName(context,"type_ProductLine", null);
							
							newRegObject.createObject(context, strProductType, autoName, null, null, context.getVault().getName());
							newRegObject.setAttributeValue(context,strMarketingNameAtt,eachCRRegion);
							newRegObject.setDescription(context, eachCRRegion);
							setPrimaryOwnership(context, newRegObject.getId());
							callConnect(context,strCDMID,strSubProdLineRel,newRegObject.getId(),null);
							callConnect(context,pgCompanyObjectID,relationship_DesignResponsibility,newRegObject.getId(),null);
							
							for(int c=0;c<arrCRCategories.length;c++) {
								String strCatToBeCreated = arrCRCategories[c];
								DomainObject newCatObject = new DomainObject();
								
								autoName = DomainObject.getAutoGeneratedName(context,"type_ProductLine", null);
								newCatObject.createObject(context, strProductType, autoName, null, null, context.getVault().getName());
								newCatObject.setAttributeValue(context,strMarketingNameAtt,strCatToBeCreated);
								newCatObject.setDescription(context, strCatToBeCreated);
								setPrimaryOwnership(context, newCatObject.getId());
								callConnect(context,newRegObject.getId(),strSubProdLineRel,newCatObject.getId(),null);
								callConnect(context,pgCompanyObjectID,relationship_DesignResponsibility,newCatObject.getId(),null);

								String cpgProdID1 = addSubStructureUnderCategory(context,newCatObject.getId(),pgCompanyObjectID,crName,crRev,ipList);
								CLR_PL_List.add(cpgProdID1);
							}							
						} else {
							//Region already exists & verify if Category is also exists. If exists, connect CL & MCE's to Category
							int idxRegion         = cdmRegions.indexOf(eachCRRegion);
							String tmpRegionId    = (String) cdmRegionIds.get(idxRegion);
							DomainObject doRegId  = DomainObject.newInstance(context,tmpRegionId);
							//Map ctgInfoMap        = doRegId.getInfo(context,regionSelects,regionSelects);
							Map ctgInfoMap=getRegionCategoryInfo(context,regionSelects,doRegId);
							StringList cdmCategories  = (StringList) ctgInfoMap.get(strSel1);
							StringList cdmCategoryIds = (StringList) ctgInfoMap.get(strSel2);
							
							for(int ct=0;ct<arrCRCategories.length;ct++) {
								String eachCRCategory = arrCRCategories[ct];
								if(cdmCategories==null || !cdmCategories.contains(eachCRCategory)) {
									//Create Category and connect to Region
									DomainObject newCatObject = new DomainObject();
									autoName = DomainObject.getAutoGeneratedName(context,"type_ProductLine", null);
									newCatObject.createObject(context, strProductType, autoName, null, null, context.getVault().getName());
									newCatObject.setAttributeValue(context,strMarketingNameAtt,eachCRCategory);
									newCatObject.setDescription(context, eachCRCategory);
									setPrimaryOwnership(context, newCatObject.getId());
									callConnect(context,doRegId.getId(),strSubProdLineRel,newCatObject.getId(),null);
									callConnect(context,pgCompanyObjectID,relationship_DesignResponsibility,newCatObject.getId(),null);
									
									String cpgProdID = addSubStructureUnderCategory(context,newCatObject.getId(),pgCompanyObjectID,crName,crRev,ipList);
									CLR_PL_List.add(cpgProdID);
									
								} else {
									//Connect Category to Region.

									int idxCategory    = cdmCategories.indexOf(eachCRCategory);
									String tmpCatId    = (String) cdmCategoryIds.get(idxCategory);

									DomainObject catDoObject = DomainObject.newInstance(context,tmpCatId);
									StringList ssSelects = new StringList();
									ssSelects.add("from["+strSubProdLineRel+"]");
									ssSelects.add("from["+strSubProdLineRel+"].to.id");
									
									//Map mapData1 = catDoObject.getInfo(context,ssSelects);
									Map mapData1=getRegionCategoryInfo(context,ssSelects,catDoObject);
									if(mapData1.containsKey("from["+strSubProdLineRel+"].to.id") ) {
									
										String mrkID = (String) (((StringList) mapData1.get("from["+strSubProdLineRel+"].to.id")).get(0));
										String cpgProdID2 = addModelnCPGUnderMRK(context,mrkID,pgCompanyObjectID,crName,crRev,ipList);
										CLR_PL_List.add(cpgProdID2);
									} else {
										String cpgProdID3 = addSubStructureUnderCategory(context,tmpCatId,pgCompanyObjectID,crName,crRev,ipList);
										CLR_PL_List.add(cpgProdID3);
									}						
									
								}
							}
							
						}
					}
				}				
				
			}
		LOGGER.log(Level.INFO, "prepareProductLineToPlaceCL - END");	
		}catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			throw e;
		}
		return CLR_PL_List;
	}
	public static String setEmptyRegionCategory(Context context,String crRegions) {
		if(crRegions==null)
			crRegions = "";
		return crRegions;
	}
	public static String getDCMRTARegionMapping(Context context,String eachCRRegion) throws Exception {
		LOGGER.log(Level.INFO, "getDCMRTARegionMapping - START");
		try {
			if(eachCRRegion!=null && (eachCRRegion.equals(STRDCMEUROPEREGION) || eachCRRegion.equals(STRDCMEUROPEFOCUSREGION))) {
				eachCRRegion = STRRTAEUROPEREGION;	
			}else if(eachCRRegion!=null && eachCRRegion.equals(STRDCMNAREGION)) {
				eachCRRegion = STRRTANAREGION;	
			}else if(eachCRRegion!=null && eachCRRegion.equals(STRDCMLAREGION)) {
				eachCRRegion = STRRTALAREGION;	
			}else if(eachCRRegion!=null) {
				return eachCRRegion;
			}
			LOGGER.log(Level.INFO, "getDCMRTARegionMapping - END");
			return eachCRRegion;
		}catch(Exception e){
			throw e;
		}
	}
	public static MapList getIPClassList(Context context,String eaIPCls,StringList cat_Selects_) throws Exception {
		LOGGER.log(Level.INFO, "getIPClassList - START");
		try {
		String strHyphen = STR_HYPHEN;
		MapList IPCLASSList = DomainObject.findObjects(context,  // context object reference
				TYPE_IPCONTROLCLASS, // type pattern
				//Replacing HighlyRestricted to Restricted as RTA data is restricted.
				eaIPCls.replace("-HiR","-R"), // name pattern
				strHyphen, // revision pattern
				DomainConstants.QUERY_WILDCARD, // owner pattern
				ESERPROD, //vault pattern
				null, //where condition
				false, // expand type
				cat_Selects_ // object selects
				);
		LOGGER.log(Level.INFO, "getIPClassList - END");
		return IPCLASSList;
		}catch(Exception e){
			throw e;
		}
	}
	public static Map getRegionCategoryInfo(Context context,StringList busSelects,DomainObject domObject)throws Exception {
		try {
			Map mapInfo = domObject.getInfo(context,busSelects,busSelects);
			return mapInfo;
		}catch(Exception e){
			throw e;
		}
	}
	public static String addSubStructureUnderCategory(Context context,String catID,  String pgCompanyObjectID, String crName,String crRev, List ipClassList) throws Exception {
		LOGGER.log(Level.INFO, "addSubStructureUnderCategory - START");
		try {
			String strProductType                 = PropertyUtil.getSchemaProperty(context,"type_ProductType");
			String strSubProdLineRel              = PropertyUtil.getSchemaProperty(context,"relationship_SubProductLines");
			String strMarketingNameAtt            = PropertyUtil.getSchemaProperty(context,"attribute_MarketingName");			
			String relationship_ProductLineModels = PropertyUtil.getSchemaProperty(context,"relationship_ProductLineModels");
			String relationship_MainProduct       = PropertyUtil.getSchemaProperty(context,"relationship_MainProduct");
			String type_CPGProduct                = PropertyUtil.getSchemaProperty(context,"type_CPGProduct");
			String relationship_DesignResponsibility = PropertyUtil.getSchemaProperty(context,"relationship_DesignResponsibility");
			String strMrkClaimReq = STR_MRK_CLAIM_REQ;
			String autoName = "";
			//Create a new PL (MRK Claim Request) and connect under Category.
			autoName = DomainObject.getAutoGeneratedName(context,"type_ProductLine", null);
			DomainObject newMRKObject = new DomainObject();
		
			newMRKObject.createObject(context, strProductType, autoName, null, null, context.getVault().getName());
			newMRKObject.setAttributeValue(context,strMarketingNameAtt,strMrkClaimReq);
			newMRKObject.setDescription(context, strMrkClaimReq);
			setPrimaryOwnership(context, newMRKObject.getId());
			callConnect(context,catID,strSubProdLineRel,newMRKObject.getId(),null);
			callConnect(context,pgCompanyObjectID,relationship_DesignResponsibility,newMRKObject.getId(),null);
		
			//Create a new CPG Product (for CLR) and connect under above MRK
			autoName = DomainObject.getAutoGeneratedName(context,"type_HardwareProduct", null);
			DomainObject newCLRCPGObject = new DomainObject();
		
			newCLRCPGObject.createObject(context, type_CPGProduct, autoName, null, null, context.getVault().getName());
			Map mAttribMap = new HashMap();
			mAttribMap.put(strMarketingNameAtt,crName);
			mAttribMap.put(ATTR_PG_ORIGINATINGSOURCE,"RTA");
			mAttribMap.put(ATTR_IPCLASSIFICATION,VALUE_RESTRICTED);
			newCLRCPGObject.setAttributeValues(context,mAttribMap);
			newCLRCPGObject.setDescription(context, crName);
			setPrimaryOwnership(context, newCLRCPGObject.getId());
			String ModelId = newCLRCPGObject.getInfo(context,"to["+relationship_MainProduct+"].from.id");
			DomainObject modelDOObject = DomainObject.newInstance(context,ModelId);
			// TBD - May have to add Project and Org as part of ownership.			
			modelDOObject.setAttributeValues(context,mAttribMap);
			modelDOObject.setDescription(context, crName);
			setPrimaryOwnership(context, ModelId);
			callConnect(context,newMRKObject.getId(),relationship_ProductLineModels,modelDOObject.getId(),null);
			callConnect(context,pgCompanyObjectID,relationship_DesignResponsibility,newCLRCPGObject.getId(),null);
			//Connect IP Control Class(Category) to CPG Product - new1
			callConnect(context,pgCompanyObjectID,RELATIONSHIP_COMPANYPRODUCT,newCLRCPGObject.getId(),null);
			for(int i=0;i<ipClassList.size();i++) {
				String classId = (String) ipClassList.get(i);
				callConnect(context,classId,DomainConstants.RELATIONSHIP_PROTECTED_ITEM,newCLRCPGObject.getId(),null);
			}
			LOGGER.log(Level.INFO, "addSubStructureUnderCategory - END");
			return newCLRCPGObject.getId();
		}catch(Exception e){
			throw e;
		}
	}
	
	public static String addModelnCPGUnderMRK(Context context,String mrkID, String pgCompanyObjectID, String crName,String crRev, List ipClassList)throws Exception {
		LOGGER.log(Level.INFO, "addModelnCPGUnderMRK - START");
	try {
		String strMarketingNameAtt            = PropertyUtil.getSchemaProperty(context,"attribute_MarketingName");			
		String relationship_ProductLineModels = PropertyUtil.getSchemaProperty(context,"relationship_ProductLineModels");
		String relationship_MainProduct       = PropertyUtil.getSchemaProperty(context,"relationship_MainProduct");
		String type_CPGProduct                = PropertyUtil.getSchemaProperty(context,"type_CPGProduct");
		String relationship_DesignResponsibility = PropertyUtil.getSchemaProperty(context,"relationship_DesignResponsibility");
		String autoName = "";
		//Create a new Model (for CLR) and connect under above MRK
			String cpgID = "";
			String existingCPGId = (String) getConnectedModelCPGProductId(context,mrkID,crName,relationship_ProductLineModels,strMarketingNameAtt,relationship_MainProduct);
			if(existingCPGId.length()<3) {			
				//Create a new CPG Product (for CLR) and connect under above MRK
				autoName = DomainObject.getAutoGeneratedName(context,"type_HardwareProduct", null);
				DomainObject newCLRCPGObject = new DomainObject();
				// TBD - May have to add Project and Org as part of ownership.
				newCLRCPGObject.createObject(context, type_CPGProduct, autoName, null, null, context.getVault().getName());
				Map mAttribMap = new HashMap();
				mAttribMap.put(strMarketingNameAtt,crName);
				mAttribMap.put(ATTR_PG_ORIGINATINGSOURCE,"RTA");
				mAttribMap.put(ATTR_IPCLASSIFICATION,VALUE_RESTRICTED);
				newCLRCPGObject.setAttributeValues(context,mAttribMap);
				newCLRCPGObject.setDescription(context, crName);
				setPrimaryOwnership(context, newCLRCPGObject.getId());
				String ModelId = newCLRCPGObject.getInfo(context,"to["+relationship_MainProduct+"].from.id");
				DomainObject modelDOObject = DomainObject.newInstance(context,ModelId);
				// TBD - May have to add Project and Org as part of ownership.
				//modelDOObject.setAttributeValue(context,strMarketingNameAtt,crName);
				modelDOObject.setAttributeValues(context,mAttribMap);
				modelDOObject.setDescription(context, crName);				
				setPrimaryOwnership(context, ModelId);
				callConnect(context,mrkID,relationship_ProductLineModels,modelDOObject.getId(),null);
				callConnect(context,pgCompanyObjectID,relationship_DesignResponsibility,newCLRCPGObject.getId(),null);
				//Connect IP Control Class(Category) to CPG Product - new1
				callConnect(context,pgCompanyObjectID,RELATIONSHIP_COMPANYPRODUCT,newCLRCPGObject.getId(),null);
				for(int i=0;i<ipClassList.size();i++) {
					String classId = (String) ipClassList.get(i);
					callConnect(context,classId,DomainConstants.RELATIONSHIP_PROTECTED_ITEM,newCLRCPGObject.getId(),null);
				}
				cpgID = newCLRCPGObject.getId();
			} else {
				cpgID = existingCPGId;
			}
			LOGGER.log(Level.INFO, "addModelnCPGUnderMRK - END");
			return cpgID;
		}catch(Exception e){
			throw e;
		}
	}
	
	public void createMCE(Context context,Map eachClaimMap,Map mapDisclaimer,boolean IsDisclaimer,List plCategories,String copyListId, String strCRId,PrintWriter writer)throws Exception{
		LOGGER.log(Level.INFO, "createMCE - START");
		String type_DisclaimerMasterCopy      = PropertyUtil.getSchemaProperty(context,"type_DisclaimerMasterCopy");	
		String strYes_ = "Yes";
		String strNo_  = "No";
		String strListSeperator = "listSeparator";
		String strListItemSeq   = "listItemSequence";
		String strListItemId    = "listItemId";
		String strEmpty = "";
		String strMasterCopy = "Master Copy";
		String strDisClaimer="";
		String strDisClaimer_RTE="";
		String strDisClaimerRev="";
		String strDisClaimerId="";
		String strDisClaimerMCEID=null;
		//String strDisclaimerExT = "";
		LOGGER.log(Level.INFO, "\n\n##RTADCM## createMCE eachClaimMap {0} ", eachClaimMap);
		
		try {
			String strClaimOID   = (String) eachClaimMap.get(DomainConstants.SELECT_ID);
			String strClaimNAME   = (String) eachClaimMap.get(DomainConstants.SELECT_NAME);
			String strClaimRev   = (String) eachClaimMap.get(DomainConstants.SELECT_REVISION);
			String strFirstRev   = (String) eachClaimMap.get("first");
			//String strClaimExT   = (String) eachClaimMap.get("attribute["+ATTRIBUTE_PGEXECUTIONTYPE+"]");
			String strClaimExT   =checkForStringList(eachClaimMap,ATTRIBUTE_PGEXECUTIONTYPE);
			String strClaimCET   = (String) eachClaimMap.get("attribute["+ATTRIBUTE_PGCOPYELEMENTTYPE+"]");
			String strClaimNam   = (String) eachClaimMap.get("attribute["+ATTRIBUTE_PGCLAIMNAME+"]");
			String strClaimNam_RTE   = (String) eachClaimMap.get("attribute["+ATTRIBUTE_PGCLAIMNAME_RTE+"]");
			//RTA 22x Added for ALM-49635 starts
			String strSequenceNumber=(String) eachClaimMap.get("attribute["+ATTRIBUTE_CLAIMSEQUENCE+"]");
			//RTA 22x Added for ALM-49635 ends
			//RTA 22x Added for ALM-46415 starts
			String strExpirationDate=(String) eachClaimMap.get("attribute["+ATTRIBUTE_PGEXPIRATIONDATE+"]");
			//RTA 22x Added for ALM-46415 ends
			LOGGER.log(Level.INFO, "##RTADCM## createMCE strClaimOID {0} ", strClaimOID);
			
			LOGGER.log(Level.INFO, "##RTADCM## createMCE mapDisclaimer {0} ", mapDisclaimer);
			if(mapDisclaimer!=null) {
				strClaimOID= (String) mapDisclaimer.get("strDisclaimerId");
				strDisClaimerId=(String) mapDisclaimer.get("strDisclaimerId");
				strDisClaimerRev = (String) mapDisclaimer.get("strDisclaimerREV");
				strClaimNAME=(String) mapDisclaimer.get("strDisclaimerName");
				strDisClaimer = (String) mapDisclaimer.get("strDisclaimer");
				strDisClaimer_RTE = (String) mapDisclaimer.get("strDisclaimerRTE");
				strDisClaimerMCEID = (String) mapDisclaimer.get("strDisclaimerMCId");
				//strDisclaimerExT   = (String) mapDisclaimer.get("strDisClaimerEXT");
				//RTA 22x Added for ALM-46415 starts
				strExpirationDate=(String) mapDisclaimer.get("strExpirationDate");
				//RTA 22x Added for ALM-46415 ends
			}
			boolean hasClaimConnectedToMC =false;
			if(eachClaimMap.containsKey("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.name"))
				hasClaimConnectedToMC = true;
			
			LOGGER.log(Level.INFO, "##RTADCM## createMCE strClaimRev {0} ", strClaimRev);
			LOGGER.log(Level.INFO, "##RTADCM## createMCE IsDisclaimer {0} ", IsDisclaimer);
			LOGGER.log(Level.INFO, "##RTADCM## createMCE hasClaimConnectedToMC {0} ", hasClaimConnectedToMC);

			// Check if Claim is Packaging Artwork
			// Case 1 Claim New Rev, No MCE connected - process new Claim and disclaimer = Create New MCE and add to CL
			// Case 2 Claim is revised, No MCE connected - process only claim and NOT disclaimer = Revise or Reuse MCE and add to CL
			// Case 3 Claim New or Revised but MCE connected - NO disclaimer processing = Reuse MCE and add to CL
			// Case 4 Claim is revised & Disclaimer New or Revised - Process only disclaimers 
			// 			4.1 New or Revised Disclaimer with MCE = Reuse MCE and add to CL
			// 			4.2 Revised Disclaimer without MCE = Revise or Reuse MCE and add to CL
			
			if(strClaimExT!=null && strClaimExT.contains("Packaging Artwork"))
			{
				// New Claim Processing
				if(strClaimRev!=null && strClaimRev.equals(strFirstRev) && !hasClaimConnectedToMC) {
					if(BusinessUtil.isNotNullOrEmpty(strClaimCET)) {
							LOGGER.log(Level.INFO, "##RTADCM## Case 1 Claim New Rev, No MCE connected - process new Claim and disclaimer = Create New MCE and add to CL {0} ", strClaimOID);

							ArtworkMaster MCEObject = null;
							LOGGER.log(Level.INFO,"##RTADCM## 5. Before creating MCE..1."+context.getUser());
							writer.print("\n@@@Before creating MCE ------------->"+context.getUser());
							//Map mapMCELCIds = null;
							
							if(IsDisclaimer){
									LOGGER.log(Level.INFO, "##RTADCM## createMCE Before converting HTML {0} ", strDisClaimer);
									String strNewDisClaimNam = UIRTEUtil.getNonRTEString(context, strDisClaimer);
									LOGGER.log(Level.INFO, "##RTADCM## createMCE After converting RTA supported HTML  {0}", strDisClaimer);
									strNewDisClaimNam=replaceClaimNewLine(strNewDisClaimNam);
									Map mapChar=getMapOfSpecialChars(context);
									strNewDisClaimNam=replaceChar(context,strNewDisClaimNam,mapChar);
									String type_DisclaimerMasterCopy_ = type_DisclaimerMasterCopy.replace(strMasterCopy,strEmpty).trim();
									Map<String, String> copyElementData = new HashMap<String, String>();
									copyElementData.put(DomainConstants.SELECT_TYPE, type_DisclaimerMasterCopy);							
									StringBuilder disPlatTxt_disclaimer = new StringBuilder();
									disPlatTxt_disclaimer.append(STRDCM);
									disPlatTxt_disclaimer.append(STRTILDE);
									disPlatTxt_disclaimer.append(type_DisclaimerMasterCopy_);
									disPlatTxt_disclaimer.append(STRTILDE);
									disPlatTxt_disclaimer.append(strNewDisClaimNam);
									disPlatTxt_disclaimer.append(STRTILDE);
									disPlatTxt_disclaimer.append(strClaimNAME);
									copyElementData.put(AWLAttribute.MARKETING_NAME.get(context), disPlatTxt_disclaimer.toString());
									copyElementData.put(AWLAttribute.TRANSLATE.get(context), strYes_);
									copyElementData.put(AWLAttribute.INLINE_TRANSLATION.get(context), strNo_);
									copyElementData.put(AWLAttribute.BUILD_LIST.get(context),strEmpty);
									copyElementData.put(strListSeperator, strEmpty);
									copyElementData.put(strListItemSeq,strEmpty);
									copyElementData.put(strListItemId,strEmpty);
									copyElementData.put(AWLAttribute.DISPLAY_TEXT.get(context), strDisClaimer_RTE);
									MCEObject = ArtworkMaster.createMasterCopyElement(context, type_DisclaimerMasterCopy, copyElementData, null, new ArrayList<Country>());
									//RTA(DS) - Added for 22x CW4 - Req 47882 - Start
									if(MCEObject!=null){
										String strMCE_ID = MCEObject.getId(context);
										updateSubCopyTypeOnMC(context, strMCE_ID);
									}
									//RTA(DS) - Added for 22x CW4 - Req 47882 - End
							} 
							else
							{
								MCEObject=processMCEObjectCreation(context,strClaimNam,strClaimNAME,strClaimCET,strClaimNam_RTE);
							}
							LOGGER.log(Level.INFO,"##RTADCM## createMCE  call {0}", context.getUser());
							LOGGER.log(Level.INFO,"##RTADCM## createMCE  strClaimOID {0}", strClaimOID);
							writer.print("\n@@@processMCEObject ----------------->"+context.getUser());
							processMCEObject(context,STR_INTEGRATION_USER,strClaimOID,copyListId,plCategories,MCEObject,writer);
							//RTA 22x Added for ALM-49635 starts
							setSequenceNumberOnCopyList(context,copyListId,MCEObject.getObjectId(context),IsDisclaimer,strSequenceNumber);
							//RTA 22x Added for ALM-49635 ends
							//RTA 22x Added for ALM-46415 starts
							MCEObject.setAttributeValue(context, ATTRIBUTE_VALIDATE_DATE, strExpirationDate);
							//RTA 22x Added for ALM-46415 ends
					}
				} else if(strClaimRev!=null && !strClaimRev.equals(strFirstRev) && !hasClaimConnectedToMC && !IsDisclaimer) { //rev1+
					//Get previous revision claim & compare pgClaimName & pgDisclaimer with current revision
					LOGGER.log(Level.INFO, "##RTADCM## Case 2 Claim is revised, No MCE connected - process only claim and NOT disclaimer = Revise or Reuse MCE and add to CL {0} ", strClaimOID);

					StringList claaimSelects = new StringList();
					claaimSelects.add("previous.attribute["+ATTRIBUTE_PGCLAIMNAME+"]");
					claaimSelects.add("previous.attribute["+ATTRIBUTE_PGCLAIMNAME_RTE+"]");
					claaimSelects.add("previous.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id");
					claaimSelects.add("previous.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].id");
					claaimSelects.add("previous.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.type");
					claaimSelects.add("previous.attribute["+ATTRIBUTE_PGEXECUTIONTYPE+"]");
					LOGGER.log(Level.INFO, "##RTADCM## Case 2 user = {0}", context.getUser());
					LOGGER.log(Level.INFO, "##RTADCM## Case 2 strClaimOID = {0} ", strClaimOID);
					Map prevClaimInfoMap = getMapDataWithPushContext(context,strClaimOID,claaimSelects);
					if(mapDisclaimer!=null && prevClaimInfoMap!=null) {
						prevClaimInfoMap.put("strPrevDisclaimerId", (String) mapDisclaimer.get("strPrevDisclaimerId"));
						prevClaimInfoMap.put("strPrevDisclaimer", (String) mapDisclaimer.get("strPrevDisclaimer"));
						prevClaimInfoMap.put("strPrevDisclaimerRTE", (String) mapDisclaimer.get("strPrevDisclaimerRTE"));
						prevClaimInfoMap.put("strPrevDisclaimerMCId", (String) mapDisclaimer.get("strPrevDisclaimerMCId"));
						prevClaimInfoMap.put("strPrevDisclaimerMCType", (String) mapDisclaimer.get("strPrevDisclaimerMCType"));
					}
					Map mapParameters=new HashMap();
					mapParameters.put("strCRId", strCRId);
					mapParameters.put("strClaimOID", strClaimOID);
					mapParameters.put("copyListId", copyListId);
					mapParameters.put("crACE1", STR_INTEGRATION_USER);
					mapParameters.put("claimRTEValue", strClaimNam_RTE);

					LOGGER.log(Level.INFO,"##RTADCM## createMCE  prevClaimInfoMap {0}", prevClaimInfoMap);
					StringList strPrevExecutionTypeList = new StringList();

					if(prevClaimInfoMap.containsKey("previous.attribute["+ATTRIBUTE_PGEXECUTIONTYPE+"]")) {
						strPrevExecutionTypeList =(StringList) prevClaimInfoMap.get("previous.attribute["+ATTRIBUTE_PGEXECUTIONTYPE+"]");	
					}
					
					String strPrevExecutionType = "";

					if(strPrevExecutionTypeList.size() > 0) {
						strPrevExecutionType = strPrevExecutionTypeList.get(0);
					}

					if(strPrevExecutionType!=null && !strPrevExecutionType.equals(strClaimExT) && strClaimExT.contains("Packaging Artwork"))
                    {
						LOGGER.log(Level.INFO,"##RTADCM## createMCE  PKG Type changed");

						if(prevClaimInfoMap.containsKey("MCsFromOldClaim"))
						{
							Map mapRevInfo=(Map)prevClaimInfoMap.get("MCsFromOldClaim");

							if(mapRevInfo.containsKey("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id")) {

								StringList slPrevClaimId=(StringList) mapRevInfo.get("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id");
								StringList slPrevClaimType=(StringList) mapRevInfo.get("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.type");
								prevClaimInfoMap.put("previous.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id",slPrevClaimId);
								prevClaimInfoMap.put("previous.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.type",slPrevClaimType);
							}

							StringList slPrevClaimNamRTE = (StringList) mapRevInfo.get("attribute["+ATTRIBUTE_PGCLAIMNAME_RTE+"]");							
							prevClaimInfoMap.put("previous.attribute["+ATTRIBUTE_PGCLAIMNAME_RTE+"]", slPrevClaimNamRTE);
							LOGGER.log(Level.INFO,"##RTADCM## createMCE  strClaimNam_RTE {0}", strClaimNam_RTE);
							LOGGER.log(Level.INFO,"##RTADCM## createMCE  strPrevClaimNamRTE {0}", slPrevClaimNamRTE);
							LOGGER.log(Level.INFO,"##RTADCM## createMCE  mapRevInfo {0}", mapRevInfo);
							
							if(!strClaimNam_RTE.equals(slPrevClaimNamRTE.get(0)) && mapRevInfo.containsKey("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id"))
                            {
								LOGGER.log(Level.INFO,"##RTADCM## Use case 2 processMCERevision");
                        	   processMCERevision(context,mapParameters,prevClaimInfoMap,plCategories,eachClaimMap,strClaimNam,mapDisclaimer);
                            }
							else if(!mapRevInfo.containsKey("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id"))
							{
								LOGGER.log(Level.INFO,"##RTADCM## Use case 2 Create New");
								ArtworkMaster MCEObject=processMCEObjectCreation(context,strClaimNam,strClaimNAME,strClaimCET,strClaimNam_RTE);
								LOGGER.log(Level.INFO,"##RTADCM## createMCE  call {0}", context.getUser());
								LOGGER.log(Level.INFO,"##RTADCM## createMCE  strClaimOID {0}", strClaimOID);
								writer.print("\n@@@processMCEObject ----------------->"+context.getUser());
								processMCEObject(context,STR_INTEGRATION_USER,strClaimOID,copyListId,plCategories,MCEObject,writer);
								//RTA 22x Added for ALM-49635 starts
								setSequenceNumberOnCopyList(context,copyListId,MCEObject.getObjectId(context),IsDisclaimer,strSequenceNumber);
								//RTA 22x Added for ALM-49635 ends
								//RTA 22x Added for ALM-46415 starts
								MCEObject.setAttributeValue(context, ATTRIBUTE_VALIDATE_DATE, strExpirationDate);
								//RTA 22x Added for ALM-46415 ends
							}
							else if(mapRevInfo.containsKey("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id"))
							{
								LOGGER.log(Level.INFO,"##RTADCM## Use case 2 connectUnRevisedMCEtoRevisedClaimForArtworkPackaging");	
								connectUnRevisedMCEtoRevisedClaimForArtworkPackaging(context,mapRevInfo,strClaimOID,copyListId);
								//RTA 22x Added for ALM-49635 starts
								setSequenceNumberForUnRevisedMCE(context,copyListId,mapRevInfo,IsDisclaimer,strSequenceNumber);
								//RTA 22x Added for ALM-49635 ends
								//RTA 22x Added for ALM-46415 starts
								updateValidityDateForUnRevisedMCE(context,mapRevInfo,strExpirationDate);
								//RTA 22x Added for ALM-46415 ends
                            }
							else 
							{
								LOGGER.log(Level.INFO,"##RTADCM## UNKNOWN Use case 2");							
							}
						}
						else 
                        {
							LOGGER.log(Level.INFO,"##RTADCM## createMCE  PKG Type changed, No Prev MCE exist. Create New");
                        	ArtworkMaster MCEObject=processMCEObjectCreation(context,strClaimNam,strClaimNAME,strClaimCET,strClaimNam_RTE);
                    		LOGGER.log(Level.INFO,"##RTADCM## createMCE  call {0}", context.getUser());
							LOGGER.log(Level.INFO,"##RTADCM## createMCE  strClaimOID {0}", strClaimOID);
							writer.print("\n@@@processMCEObject ----------------->"+context.getUser());
							processMCEObject(context,STR_INTEGRATION_USER,strClaimOID,copyListId,plCategories,MCEObject,writer);
							//RTA 22x Added for ALM-49635 starts
							setSequenceNumberOnCopyList(context,copyListId,MCEObject.getObjectId(context),IsDisclaimer,strSequenceNumber);
							//RTA 22x Added for ALM-49635 ends
							//RTA 22x Added for ALM-46415 starts
							MCEObject.setAttributeValue(context, ATTRIBUTE_VALIDATE_DATE, strExpirationDate);
							//RTA 22x Added for ALM-46415 ends
                        }
                    }
                    else {
                    	processMCERevision(context,mapParameters,prevClaimInfoMap,plCategories,eachClaimMap,strClaimNam,mapDisclaimer);
                    }					
				}
				else if(!IsDisclaimer && hasClaimConnectedToMC) {
					// Claims with MCE already connected. Connect MCE to New CL
					LOGGER.log(Level.INFO, "##RTADCM## Case 3 Claim New or Revised but MCE connected - NO disclaimer processing = Reuse MCE and add to CL {0} ", strClaimOID);
					String strMCEOID = (String) eachClaimMap.get("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id");
					String strPrevClaimNam=null;
					LOGGER.log(Level.INFO, "##RTADCM## Case 3 strMCEOID {0}", strMCEOID);
				
					if(BusinessUtil.isNotNullOrEmpty(strMCEOID) && BusinessUtil.isKindOf(context,strMCEOID, AWLType.MASTER_ARTWORK_ELEMENT.get(context))){
						ArtworkMaster master = new ArtworkMaster(strMCEOID);
						ArtworkContent element = master.getBaseArtworkElement(context);
						LOGGER.log(Level.INFO, "##RTADCM## Case 3 element {0}", element.getObjectId());
						LOGGER.log(Level.INFO, "##RTADCM## Case 3 copy text {0}", "attribute[Copy Text_RTE]");
						strPrevClaimNam = BusinessUtil.getInfo(context, element.getObjectId(), "attribute[Copy Text_RTE]");	
						LOGGER.log(Level.INFO, "##RTADCM## Case 3 strPrevClaimNam{0}", strPrevClaimNam);
					}
					//Added for revising existing CEs
					StringList claaimSelects = new StringList();
					//claaimSelects.add("attribute["+ATTRIBUTE_PGCLAIMNAME+"]");
					claaimSelects.add("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id");
					claaimSelects.add("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].id");
					claaimSelects.add("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.type");
					//claaimSelects.add("attribute["+ATTRIBUTE_PGEXECUTIONTYPE+"]");
					LOGGER.log(Level.INFO, "##RTADCM## Case 2 user = {0}", context.getUser());
					LOGGER.log(Level.INFO, "##RTADCM## Case 2 strClaimOID = {0} ", strClaimOID);
					Map prevClaimInfoMap = getMapDataWithPushContext(context,strClaimOID,claaimSelects);
					StringList slPrevClaimName = new StringList();
					slPrevClaimName.add(strPrevClaimNam);


					prevClaimInfoMap.put("previous.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id", prevClaimInfoMap.get("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id"));
					prevClaimInfoMap.put("previous.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].id", prevClaimInfoMap.get("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].id"));
					prevClaimInfoMap.put("previous.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.type", prevClaimInfoMap.get("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.type"));
					prevClaimInfoMap.put("previous.attribute["+ATTRIBUTE_PGCLAIMNAME+"]", slPrevClaimName);
					
					
					Map mapParameters=new HashMap();
					mapParameters.put("strCRId", strCRId);
					mapParameters.put("strClaimOID", strClaimOID);
					mapParameters.put("copyListId", copyListId);
					mapParameters.put("crACE1", STR_INTEGRATION_USER);
					mapParameters.put("claimRTEValue", strClaimNam_RTE);
					
					if(strPrevClaimNam!=null && !strClaimNam_RTE.equals(strPrevClaimNam)) {
						processMCERevision(context,mapParameters,prevClaimInfoMap,plCategories,eachClaimMap,strClaimNam,mapDisclaimer);
					}else {
						// Added for revising existing CEs
						ArtworkMaster masterRevised = new ArtworkMaster(strMCEOID);
						CopyList copyList = new CopyList(copyListId);//ID of COPY LIST
						copyList.addArtworkMaster(context, masterRevised);
						//RTA 22x Added for ALM-49635 starts
						setSequenceNumberOnCopyList(context,copyListId,strMCEOID,IsDisclaimer,strSequenceNumber);
						//RTA 22x Added for ALM-49635 ends
						//RTA 22x Added for ALM-46415 starts
						masterRevised.setAttributeValue(context, ATTRIBUTE_VALIDATE_DATE, strExpirationDate);
						//RTA 22x Added for ALM-46415 ends
					}
				}
				else if(IsDisclaimer) {
					LOGGER.log(Level.INFO, " ##RTADCM## strDisClaimerRev {0}", strDisClaimerRev);
					LOGGER.log(Level.INFO, " ##RTADCM## strDisClaimerMCEID {0}", strDisClaimerMCEID);
					LOGGER.log(Level.INFO, " ##RTADCM## strFirstRev {0}", strFirstRev);
					
						if(strDisClaimerMCEID!=null && strDisClaimerMCEID.length() > 0)
						{
							LOGGER.log(Level.INFO, "##RTADCM## Case 4.1 New or Revised Disclaimer with MCE = Reuse MCE and add to CL {0} ", strClaimOID);

							LOGGER.log(Level.INFO, " ##RTADCM## DISCLAIMER 1st Rev & MCE conencted");
							LOGGER.log(Level.INFO, " ##RTADCM## DISCLAIMER copyListId {0}", copyListId);
							LOGGER.log(Level.INFO, " ##RTADCM## DISCLAIMER 1st masterRevised {0} ", strDisClaimerMCEID);
							// Connect MCE to new CL
							ArtworkMaster masterRevised = new ArtworkMaster(strDisClaimerMCEID);
							CopyList copyList = new CopyList(copyListId);//ID of COPY LIST
							copyList.addArtworkMaster(context, masterRevised);
							//RTA 22x Added for ALM-46415 starts
							masterRevised.setAttributeValue(context, ATTRIBUTE_VALIDATE_DATE, strExpirationDate);
							//RTA 22x Added for ALM-46415 ends
						}
						// This is Disclaimer Revision scenario where MCE will not be connected
						else if(!strDisClaimerRev.equals(strFirstRev) && strDisClaimerMCEID==null) {
							LOGGER.log(Level.INFO, "##RTADCM## Case 4.2 Revised Disclaimer without MCE = Revise or Reuse MCE and add to CL {0} ", strClaimOID);
							// Is content changed for disclaimer then revise MCE else connect disconnect MCE from disclaimer new / old rev. add to CL		
							StringList claaimSelects = new StringList();
							claaimSelects.add("previous.attribute["+ATTRIBUTE_PGCLAIMNAME+"]");
							//DCM:US-4383 RTA DCM Integration Code changes as per New data Model Changes  - Start
							claaimSelects.add("previous.attribute["+ATTRIBUTE_PGDISCLAIMER+"]");
							//DCM:US-4383 RTA DCM Integration Code changes as per New data Model Changes  - End
							claaimSelects.add("previous.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id");
							claaimSelects.add("previous.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].id");
							claaimSelects.add("previous.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.type");
							Map prevClaimInfoMap = getMapDataWithPushContext(context,strClaimOID,claaimSelects);
							if(mapDisclaimer!=null && prevClaimInfoMap!=null) {
								prevClaimInfoMap.put("strPrevDisclaimerId", (String) mapDisclaimer.get("strPrevDisclaimerId"));
								prevClaimInfoMap.put("strPrevDisclaimer", (String) mapDisclaimer.get("strPrevDisclaimer"));
								prevClaimInfoMap.put("strPrevDisclaimerRTE", (String) mapDisclaimer.get("strPrevDisclaimerRTE"));
								prevClaimInfoMap.put("strPrevDisclaimerMCId", (String) mapDisclaimer.get("strPrevDisclaimerMCId"));
								prevClaimInfoMap.put("strPrevDisclaimerMCType", (String) mapDisclaimer.get("strPrevDisclaimerMCType"));
								//DCM:US-4383 RTA DCM Integration Code changes as per New data Model Changes  - Start
								strClaimNam = strDisClaimer;
								//DCM:US-4383 RTA DCM Integration Code changes as per New data Model Changes  - End
							}
							Map mapParameters=new HashMap();
							mapParameters.put("strCRId", strCRId);
							mapParameters.put("strClaimOID", strClaimOID);
							mapParameters.put("copyListId", copyListId);
							mapParameters.put("crACE1", STR_INTEGRATION_USER);
							mapParameters.put("claimRTEValue", strDisClaimer_RTE);

							LOGGER.log(Level.INFO,"##RTADCM## createMCE  prevClaimInfoMap {0}", prevClaimInfoMap);
							processMCERevision(context,mapParameters,prevClaimInfoMap,plCategories,eachClaimMap,strClaimNam,mapDisclaimer);
						}else if(strDisClaimerRev.equals(strFirstRev) && strDisClaimerMCEID==null) {
							LOGGER.log(Level.INFO, "##RTADCM## createMCE Before converting HTML {0} ", strDisClaimer);
							String strNewDisClaimNam = UIRTEUtil.getNonRTEString(context, strDisClaimer);
							LOGGER.log(Level.INFO, "##RTADCM## createMCE After converting RTA supported HTML  {0}", strDisClaimer);
							strNewDisClaimNam=replaceClaimNewLine(strNewDisClaimNam);
							Map mapChar=getMapOfSpecialChars(context);
							strNewDisClaimNam=replaceChar(context,strNewDisClaimNam,mapChar);
							String type_DisclaimerMasterCopy_ = type_DisclaimerMasterCopy.replace(strMasterCopy,strEmpty).trim();
							Map<String, String> copyElementData = new HashMap<String, String>();
							copyElementData.put(DomainConstants.SELECT_TYPE, type_DisclaimerMasterCopy);							
							StringBuilder disPlatTxt_disclaimer = new StringBuilder();
							disPlatTxt_disclaimer.append(STRDCM);
							disPlatTxt_disclaimer.append(STRTILDE);
							disPlatTxt_disclaimer.append(type_DisclaimerMasterCopy_);
							disPlatTxt_disclaimer.append(STRTILDE);
							disPlatTxt_disclaimer.append(strNewDisClaimNam);
							disPlatTxt_disclaimer.append(STRTILDE);
							disPlatTxt_disclaimer.append(strClaimNAME);
							copyElementData.put(AWLAttribute.MARKETING_NAME.get(context), disPlatTxt_disclaimer.toString());
							copyElementData.put(AWLAttribute.TRANSLATE.get(context), strYes_);
							copyElementData.put(AWLAttribute.INLINE_TRANSLATION.get(context), strNo_);
							copyElementData.put(AWLAttribute.BUILD_LIST.get(context),strEmpty);
							copyElementData.put(strListSeperator, strEmpty);
							copyElementData.put(strListItemSeq,strEmpty);
							copyElementData.put(strListItemId,strEmpty);
							copyElementData.put(AWLAttribute.DISPLAY_TEXT.get(context), strDisClaimer_RTE);
							ArtworkMaster MCEObject = ArtworkMaster.createMasterCopyElement(context, type_DisclaimerMasterCopy, copyElementData, null, new ArrayList<Country>());
							LOGGER.log(Level.INFO,"##RTADCM## Disclaimer  call {0}", context.getUser());
							LOGGER.log(Level.INFO,"##RTADCM## Disclaimer  strClaimOID {0}", strClaimOID);
							writer.print("\n@@@processMCEObject ----------------->"+context.getUser());
							processMCEObject(context,STR_INTEGRATION_USER,strClaimOID,copyListId,plCategories,MCEObject,writer);
							//RTA 22x Added for ALM-46415 starts
							if(MCEObject!=null) {
								MCEObject.setAttributeValue(context, ATTRIBUTE_VALIDATE_DATE, strExpirationDate);
								//RTA(DS) - Added for 22x CW4 - Req 47882 - Start
								String strMCE_ID = MCEObject.getId(context);
								updateSubCopyTypeOnMC(context, strMCE_ID);
								//RTA(DS) - Added for 22x CW4 - Req 47882 - End									
							}
							//RTA 22x Added for ALM-46415 ends						
						}
						else{
							LOGGER.log(Level.INFO, " ##RTADCM## DISCLAIMER UNKNON ELSE ");
						}
				} 
				else {
					LOGGER.log(Level.INFO, " ##RTADCM## Bad Data.. check either revision or +1 revision not connected to any MC "+strClaimOID+"--->"+strClaimRev);
					writer.print("\n\n@@@Bad Data.. check either revision or +1 revision not connected to any MC "+strClaimOID+"--->"+strClaimRev);
				}
			}	
			else{
				LOGGER.log(Level.INFO, " ##RTADCM## CreateMCE Claim not processed as not packing {0}", strClaimOID);
			}	
			LOGGER.log(Level.INFO, "createMCE - END");
		} catch(Exception e) {			
			LOGGER.log(Level.SEVERE, e.toString());
			LOGGER.log(Level.SEVERE, e.getMessage());
			throw e;
		}
	}

	public void createMCEForMultipleDisclaimer(Context context,Map eachClaimMap,List plCategories,String copyListId,String strCRId,PrintWriter writer)throws Exception {
		boolean isContextPushed = false;
		LOGGER.log(Level.INFO, " \n\n ##RTADCM## createMCEForMultipleDisclaimer entry---> {0}", context.getUser());
		
		try {
			// RTA user doesnt have access on claim(DCM) data.
			ContextUtil.pushContext(context);
			isContextPushed = true;
			String strClaimId=(String)eachClaimMap.get(DomainConstants.SELECT_ID);
			String strClaimRev   = (String) eachClaimMap.get(DomainConstants.SELECT_REVISION);
			String strFirstRev   = (String) eachClaimMap.get("first");	
			StringList slClaimSelects = new StringList();
			slClaimSelects.add(DomainConstants.SELECT_ID);
			slClaimSelects.add(DomainConstants.SELECT_NAME);
			slClaimSelects.add("attribute["+ATTRIBUTE_PGDISCLAIMER+"]");
			slClaimSelects.add("attribute["+ATTRIBUTE_PGDISCLAIMER_RTE+"]");
			slClaimSelects.add(DomainConstants.SELECT_REVISION);
			slClaimSelects.add("first");
			slClaimSelects.add("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id");
			slClaimSelects.add("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].id");
			slClaimSelects.add("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.type");
			//RTA 22x Added for ALM-46415 starts
			slClaimSelects.add("attribute["+ATTRIBUTE_PGEXPIRATIONDATE+"]");	
			//RTA 22x Added for ALM-46415 ends

			if(BusinessUtil.isNotNullOrEmpty(strFirstRev)&& !strClaimRev.equals(strFirstRev)){
				slClaimSelects.add("previous.id");
				slClaimSelects.add("previous.attribute["+ATTRIBUTE_PGDISCLAIMER+"]");
				slClaimSelects.add("previous.attribute["+ATTRIBUTE_PGDISCLAIMER_RTE+"]");
				slClaimSelects.add("previous.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id");
				slClaimSelects.add("previous.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].id");
				slClaimSelects.add("previous.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.type");
			}

			DomainObject doClaim=DomainObject.newInstance(context, strClaimId);
			MapList mlClaimsInfo = doClaim.getRelatedObjects(context,    // context object reference
				RELATIONSHIP_PGDISCLAIMER, // relationship name
				TYPE_PGDISCLAIMER, // connected type name 
				slClaimSelects, // object selects
				null, // rel selects
				false, //get To
				true,  // fet From
				(short)1, //recurse level
				null, // where clause
				null,// rel where clause
				0); //limit
			LOGGER.log(Level.INFO, " ##RTADCM## createMCEForMultipleDisclaimer mlClaimsInfo---> {0}", mlClaimsInfo);
			if(isContextPushed) {
				ContextUtil.popContext(context);
				isContextPushed = false;
			}
			LOGGER.log(Level.INFO, " ##RTADCM## createMCEForMultipleDisclaimer user---> {0}", context.getUser());
			Map mapDisclaimer=null;
			Map mapClaim=null;
			String strDisclaimerId=null;
			String strDisclaimerName=null;
			String strDisclaimer=null;
			String strDisclaimerRTE=null;
			String strPrevDisclaimerId=null;
			String strPrevDisclaimer=null;
			String strPrevDisclaimerRTE=null;
			String strDisclaimerRev = null;
			String strDisClaimerEXT = null;
			//RTA 22x Added for ALM-46415 starts
			String strExpirationDate = null;
			//RTA 22x Added for ALM-46415 ends

			for(int i=0;i<mlClaimsInfo.size();i++) {
				mapClaim=(Map)mlClaimsInfo.get(i);
				strDisclaimerId=(String)mapClaim.get(DomainConstants.SELECT_ID);
				strDisclaimerName=(String)mapClaim.get(DomainConstants.SELECT_NAME);
				strDisclaimer=(String)mapClaim.get("attribute["+ATTRIBUTE_PGDISCLAIMER+"]");
				strDisclaimerRTE=(String)mapClaim.get("attribute["+ATTRIBUTE_PGDISCLAIMER_RTE+"]");
				strDisclaimerRev =(String)mapClaim.get(DomainConstants.SELECT_REVISION);
				//RTA 22x Added for ALM-46415 starts
				strExpirationDate = (String)mapClaim.get("attribute["+ATTRIBUTE_PGEXPIRATIONDATE+"]");
				//RTA 22x Added for ALM-46415 ends
				strDisClaimerEXT = checkForStringList(mapClaim,ATTRIBUTE_PGEXECUTIONTYPE);

				mapDisclaimer=new HashMap();
				mapDisclaimer.put("strDisclaimerId", strDisclaimerId);
				mapDisclaimer.put("strDisclaimerName", strDisclaimerName);
				mapDisclaimer.put("strDisclaimer", strDisclaimer);
				mapDisclaimer.put("strDisclaimerRTE", strDisclaimerRTE);
				mapDisclaimer.put("strDisclaimerREV", strDisclaimerRev);
				//RTA 22x Added for ALM-46415 starts
				mapDisclaimer.put("strExpirationDate", strExpirationDate);
				//RTA 22x Added for ALM-46415 ends

				// Disclaimer can be 1st revision and connected to Claim 2nd revision
				mapDisclaimer.put("strDisclaimerMCId", mapClaim.get("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id"));
				mapDisclaimer.put("strDisclaimerMCType",mapClaim.get("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.type"));
				
				if(BusinessUtil.isNotNullOrEmpty(strFirstRev) && !strDisclaimerRev.equals(strFirstRev)){
					strPrevDisclaimerId=(String)mapClaim.get("previous.id");
					strPrevDisclaimer=(String)mapClaim.get("previous.attribute["+ATTRIBUTE_PGDISCLAIMER+"]");
					strPrevDisclaimerRTE=(String)mapClaim.get("previous.attribute["+ATTRIBUTE_PGDISCLAIMER_RTE+"]");
					mapDisclaimer.put("strPrevDisclaimerId", strPrevDisclaimerId);
					mapDisclaimer.put("strPrevDisclaimer", strPrevDisclaimer);
					mapDisclaimer.put("strPrevDisclaimerRTE", strPrevDisclaimerRTE);
					mapDisclaimer.put("strPrevDisclaimerMCId", mapClaim.get("previous.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id"));
					mapDisclaimer.put("strPrevDisclaimerMCType",mapClaim.get("previous.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.type"));

				}
				
				LOGGER.log(Level.INFO, " ##RTADCM## createMCEForMultipleDisclaimer mapDisclaimer---> {0}", mapDisclaimer);
				createMCE(context,eachClaimMap,mapDisclaimer,true,plCategories,copyListId,strCRId,writer);
			}
		}catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.toString());
			LOGGER.log(Level.SEVERE, e.getMessage());
			throw e;
		} finally {
			if(isContextPushed)
				ContextUtil.popContext(context);
		}
		LOGGER.log(Level.INFO, " ##RTADCM## createMCEForMultipleDisclaimer Exit---> {0}", context.getUser());
	}
	
	public static String replaceClaimNewLine(String strNewClaimNam)throws Exception {
		if(strNewClaimNam!=null) {
			strNewClaimNam = strNewClaimNam.replace("\n","");
		}
		return strNewClaimNam;
	}
	public static ArtworkMaster processMCEObjectCreation(Context context,String strClaimNam,String strClaimNAME,String strClaimCET,String strClaimNam_RTE)throws Exception {
		LOGGER.log(Level.INFO, "processMCEObjectCreation - START");
		String strYes_ = "Yes";
		String strNo_  = "No";
		String strListSeperator = "listSeparator";
		String strListItemSeq   = "listItemSequence";
		String strListItemId    = "listItemId";
		String strEmpty = "";
		LOGGER.log(Level.INFO, "##RTADCM## createMCE Before converting HTML {0} ", strClaimNam);
		String strNewClaimNam    = UIRTEUtil.getNonRTEString(context, strClaimNam);
		LOGGER.log(Level.INFO, "##RTADCM## createMCE After converting RTA supported HTML  {0}", strClaimNam);
		
		// TBD - to be verified - why new lines are getting replaced.
		strNewClaimNam=replaceClaimNewLine(strNewClaimNam);
		LOGGER.log(Level.INFO, "##RTADCM## 1 strNewClaimNam  {0}", strNewClaimNam);
		Map mapChar=getMapOfSpecialChars(context);
		LOGGER.log(Level.INFO, "##RTADCM## mapChar  {0}", mapChar);
		strNewClaimNam=replaceChar(context,strNewClaimNam,mapChar);
		LOGGER.log(Level.INFO, "##RTADCM## 2 strNewClaimNam  {0}", strNewClaimNam);
		String strClaimCET_ = strClaimCET.replace("Master Copy","").trim();
		LOGGER.log(Level.INFO, "##RTADCM## strClaimCET_  {0}", strClaimCET_);
		Map<String, String> copyElementData = new HashMap<String, String>();
		StringBuilder disPlatTxt = new StringBuilder();
		disPlatTxt.append(STRDCM);
		disPlatTxt.append(STRTILDE);
		disPlatTxt.append(strClaimCET_);
		disPlatTxt.append(STRTILDE);
		disPlatTxt.append(strNewClaimNam);				
		disPlatTxt.append(STRTILDE);
		disPlatTxt.append(strClaimNAME);				
		LOGGER.log(Level.INFO, "##RTADCM## disPlatTxt  {0}", disPlatTxt);
		copyElementData.put(AWLAttribute.MARKETING_NAME.get(context), disPlatTxt.toString());
		copyElementData.put(AWLAttribute.TRANSLATE.get(context), strYes_);
		copyElementData.put(AWLAttribute.INLINE_TRANSLATION.get(context), strNo_);
		copyElementData.put(AWLAttribute.BUILD_LIST.get(context),strEmpty);
		copyElementData.put(strListSeperator, strEmpty);
		copyElementData.put(strListItemSeq,strEmpty);
		copyElementData.put(strListItemId,strEmpty);
		copyElementData.put(DomainConstants.SELECT_TYPE, strClaimCET);
		copyElementData.put(AWLAttribute.DISPLAY_TEXT.get(context), strClaimNam_RTE);
		LOGGER.log(Level.INFO, "##RTADCM## copyElementData  {0}", copyElementData);	
		ArtworkMaster MCEObject = ArtworkMaster.createMasterCopyElement(context, strClaimCET, copyElementData, null, new ArrayList<Country>());
		//RTA(DS) - Added for 22x CW4 - Req 47882 - Start
		if(MCEObject!=null){
			String strMCE_ID = MCEObject.getId(context);
		LOGGER.log(Level.INFO, "##RTADCM## strMCE_ID  {0}", strMCE_ID);	
			updateSubCopyTypeOnMC(context, strMCE_ID);
			LOGGER.log(Level.INFO, "##RTADCM## after updateSubCopyTypeOnMC method  {0}", strMCE_ID);
		}
		//RTA(DS) - Added for 22x CW4 - Req 47882 - End
		LOGGER.log(Level.INFO, "processMCEObjectCreation - END");
		return MCEObject;		
	}
	public static void processMCEObject(Context context,String crACE1,String strClaimOID,String copyListId,List plCategories,ArtworkMaster MCEObject,PrintWriter writer)throws Exception {
		LOGGER.log(Level.INFO, "processMCEObject - START");
		boolean bPushContext = false;
		try {
		if(MCEObject!=null) {
			String strMCE_ID = MCEObject.getId(context);
			LOGGER.log(Level.INFO,"##RTADCM## processMCEObject# MCE has been created-> {0}", strMCE_ID);
			writer.print("\n\n@@@MCE has been created -------->"+strMCE_ID);
			setPrimaryOwnership(context, strMCE_ID);
			LOGGER.log(Level.INFO,"##RTADCM##  processMCEObject# MCE has been updated with Primary Ownership-> {0}", strMCE_ID);
			writer.print("\n\n@@@MCE has been updated with Primary Ownership -------->"+strMCE_ID);
			String sBaseLocalCopyID = getBaseLocalCopyInfoForMaster(context,strMCE_ID);
			//TBD - Change assign and author - DCM2rtm.im user
			String strACEContactID =  getACEContactObjectId(context,crACE1);
			ArtworkContent element = ArtworkContent.getNewInstance(context, sBaseLocalCopyID);
			element.updateAssignee(context, strACEContactID, strACEContactID);	
			LOGGER.log(Level.INFO,"##RTADCM##   processMCEObject# LC has been updated with Assinees-> {0}", strACEContactID);
			writer.print("\n\n@@@LC has been updated with Assinees -------->"+strACEContactID);
			//promote to release
			promoteLCtoReviewReleaseState(context, sBaseLocalCopyID, "Preliminary");
			promoteLCtoReviewReleaseState(context, sBaseLocalCopyID, "Review");
			LOGGER.log(Level.INFO,"##RTADCM##   processMCEObject# MCE has been released");
			writer.print("\n\n@@@MCE has been released");
			//Connect MCE to CopyList
			CopyList copyList = new CopyList(copyListId);//ID of COPY LIST
			copyList.addArtworkMaster(context, MCEObject);
			LOGGER.log(Level.INFO,"##RTADCM##   processMCEObject# MCE has been connected CopyList-> {0}", copyListId);
			writer.print("\n\n@@@MCE has been connected CopyList -------->"+copyListId);
			//Add MCE in Brand Hierarchy (CDM Admin structure)
			Map tmpAtt = new HashMap();
			tmpAtt.put(ATTRIBUTE_PLACEOFORIGIN,STR_YES);
			connectMCTOCategories(context,plCategories,MCEObject,tmpAtt);
			LOGGER.log(Level.INFO,"##RTADCM##   processMCEObject# MCE has been added in BrandHierarchy->");
			writer.print("\n\n@@@MCE has been added in BrandHierarchy");
			//connect MCE to Claim
			ContextUtil.popContext(context);
			// RTA user doesnt have access on DCM data.
			LOGGER.log(Level.INFO,"##RTADCM##  processMCEObject#  before PushContext user > {0}", context.getUser());
			ContextUtil.pushContext(context);
			bPushContext = true;
			LOGGER.log(Level.INFO,"##RTADCM##  processMCEObject#  after PushContext user > {0}", context.getUser());
			LOGGER.log(Level.INFO,"##RTADCM##  processMCEObject# ClaimId-> {0}", strClaimOID);
			LOGGER.log(Level.INFO,"##RTADCM##  processMCEObject# strMCE_ID-> {0}", strMCE_ID);
			callConnect(context,strClaimOID,RELATIONSHIP_PGMASTERCOPYCLAIM,strMCE_ID,null);
			if(bPushContext) {
				ContextUtil.popContext(context);
				bPushContext = false;
			}
			
			LOGGER.log(Level.INFO,"##RTADCM##  processMCEObject#  context user 2 {0}>", context.getUser());
			// Data is created with RTA Integration user context
			ContextUtil.pushContext(context, STR_INTEGRATION_USER, "",context.getVault().getName());
			LOGGER.log(Level.INFO,"##RTADCM##   processMCEObject# MCE has been connected to Claim->{0}", strClaimOID);
			writer.print("\n\n@@@MCE has been connected to Claim -------->"+strClaimOID);
		}
		}catch(Exception e){
			throw e;
		}
		finally {
			if(bPushContext) {
				ContextUtil.popContext(context);
				bPushContext = false;
			}
		}
		LOGGER.log(Level.INFO, "processMCEObject - END {0}", context.getUser());
	}
	
	public static void processMCERevision(Context context,Map mapParameters,Map prevClaimInfoMap,List plCategories,Map eachClaimMap,String strClaimNam,Map mapDisclaimer)throws Exception {
		LOGGER.log(Level.INFO, "processMCERevision - START");
		try {
		LOGGER.log(Level.INFO,"##RTADCM##   processMCERevision# >{0}", prevClaimInfoMap);
		String prevstrClaimNamRTE = "";
		String prevstrDisClaimer   = "";
		if(prevClaimInfoMap!=null) {
			
			if(prevClaimInfoMap.containsKey("previous.attribute["+ATTRIBUTE_PGCLAIMNAME_RTE+"]")) {
				StringList tmp1List = (StringList) prevClaimInfoMap.get("previous.attribute["+ATTRIBUTE_PGCLAIMNAME_RTE+"]");
				if(BusinessUtil.isNotNullOrEmpty(tmp1List))
					prevstrClaimNamRTE = tmp1List.get(0);
			}
			if(prevClaimInfoMap.containsKey("strPrevDisclaimerRTE")) {
					prevstrDisClaimer = (String)prevClaimInfoMap.get("strPrevDisclaimerRTE");
					mapParameters.put("prevstrDisClaimer", prevstrDisClaimer);
			}

			LOGGER.log(Level.INFO,"##RTADCM##   processMCERevision strClaimNam # >{0}", strClaimNam);

			if(strClaimNam!=null) {
				//If content gets changed - then revise
				mapParameters.put("prevstrClaimNam", prevstrClaimNamRTE);
				LOGGER.log(Level.INFO,"##RTADCM##   processMCERevision mapParameters# >{0}", mapParameters);
				reviseMCOrConnectUnRevisedMCToClaim(context,mapParameters,prevClaimInfoMap,plCategories,eachClaimMap,strClaimNam,mapDisclaimer);
			}
		}
		}catch(Exception e){
			throw e;
		}
		LOGGER.log(Level.INFO, "processMCERevision - END");
	}
	
	public static void reviseMCOrConnectUnRevisedMCToClaim(Context context,Map mapParameters,Map prevClaimInfoMap,List plCategories,Map eachClaimMap,String strClaimNam,Map mapDisclaimer)throws Exception {
		LOGGER.log(Level.INFO, "reviseMCOrConnectUnRevisedMCToClaim - START");
		try {
		String strClaimRTEValue = (String)mapParameters.get("claimRTEValue");
		String prevstrClaimNam=(String)mapParameters.get("prevstrClaimNam");
		String prevstrDisClaimer=(String)mapParameters.get("prevstrDisClaimer");
		String strClaimOID=(String)mapParameters.get("strClaimOID");
		String strCopyListId=(String)mapParameters.get("copyListId");
		//RTA 22x Added for ALM-49635 starts
		String strSequenceNumber=(String) eachClaimMap.get("attribute["+ATTRIBUTE_CLAIMSEQUENCE+"]");
		boolean IsDisclaimer=false;
		//RTA 22x Added for ALM-49635 ends
		//RTA 22x Added for ALM-46415 starts
		String strExpirationDate=(String) eachClaimMap.get("attribute["+ATTRIBUTE_PGEXPIRATIONDATE+"]");
		//RTA 22x Added for ALM-46415 ends
		LOGGER.log(Level.INFO,"##RTADCM## reviseMCOrConnectUnRevisedMCToClaim strClaimNam->{0}", strClaimRTEValue);
		LOGGER.log(Level.INFO,"##RTADCM## reviseMCOrConnectUnRevisedMCToClaim prevstrClaimNam->{0}", prevstrClaimNam);
		
		if(mapDisclaimer==null && !strClaimRTEValue.equals(prevstrClaimNam)) {
			//Revise Claim MCE(s)
			if(prevClaimInfoMap.containsKey("previous.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id")) {
				StringList tmp3ListMCEs = (StringList) prevClaimInfoMap.get("previous.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id");
				StringList tmp3ListMCETypes = (StringList) prevClaimInfoMap.get("previous.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.type");
				LOGGER.log(Level.INFO,"##RTADCM## reviseMCOrConnectUnRevisedMCToClaim tmp3ListMCEs->{0}", tmp3ListMCEs);
				LOGGER.log(Level.INFO,"##RTADCM## reviseMCOrConnectUnRevisedMCToClaim tmp3ListMCETypes->{0}", tmp3ListMCETypes);
				// TBD - Revising MC and Disclaimer - need to verify this use case.
				for(int mceCtr = 0; mceCtr < tmp3ListMCEs.size(); mceCtr ++) {
					LOGGER.log(Level.INFO,"##RTADCM## reviseMCOrConnectUnRevisedMCToClaim call reviseMCE->{0}", eachClaimMap);
					reviseMCE(context, tmp3ListMCEs.get(mceCtr), mapParameters,plCategories,tmp3ListMCETypes.get(mceCtr),eachClaimMap,mapDisclaimer);
				}
			}
		}else if(mapDisclaimer!=null && !((String) mapDisclaimer.get("strDisclaimer")).equals(prevstrDisClaimer)) {
			//Revise Disclaimer MCE(s)
			if(prevClaimInfoMap.containsKey("strPrevDisclaimerMCId")) {
				//RTA 22x Added for ALM-49635 starts
				IsDisclaimer=true;
				//RTA 22x Added for ALM-49635 ends
				String strPrevDisclaimerMCId = (String) prevClaimInfoMap.get("strPrevDisclaimerMCId");
				String strPrevDisclaimerMCType = (String) prevClaimInfoMap.get("strPrevDisclaimerMCType");
				LOGGER.log(Level.INFO,"##RTADCM## reviseMCOrConnectUnRevisedMCToClaim strPrevDisclaimerMCId->{0}", strPrevDisclaimerMCId);
				LOGGER.log(Level.INFO,"##RTADCM## reviseMCOrConnectUnRevisedMCToClaim strPrevDisclaimerMCType->{0}", strPrevDisclaimerMCType);
				LOGGER.log(Level.INFO,"##RTADCM## reviseMCOrConnectUnRevisedMCToClaim call reviseMCE->{0}", eachClaimMap);
				reviseMCE(context, strPrevDisclaimerMCId, mapParameters,plCategories,strPrevDisclaimerMCType,eachClaimMap,mapDisclaimer);
			}
		}else {
			LOGGER.log(Level.INFO, " ##RTADCM## Claim content not modified. So not revising ");
			//connect MCE's to new claim Revision.
			connectUnRevisedMCEtoRevisedClaim(context,prevClaimInfoMap,strClaimOID,strCopyListId);	
			//RTA 22x Added for ALM-49635 starts
			setSequenceNumberForUnRevisedMCE(context,strCopyListId,prevClaimInfoMap,IsDisclaimer,strSequenceNumber);
			//RTA 22x Added for ALM-49635 ends
			//RTA 22x Added for ALM-46415 starts
			updateValidityDateForUnRevisedMCE(context,prevClaimInfoMap,strExpirationDate);
			//RTA 22x Added for ALM-46415 ends
		}
		}catch(Exception e){
			throw e;
		}
		LOGGER.log(Level.INFO, "reviseMCOrConnectUnRevisedMCToClaim - END");
	}
	
	public static void reviseMCE(Context context, String MCID, Map mapParameters, List plCategories, String strClaimCET, Map eachClaimMap,Map mapDisclaimer) throws Exception {
		LOGGER.log(Level.INFO, "reviseMCE - START");
		String strCRId=(String)mapParameters.get("strCRId");
		String strClaimOID=(String)mapParameters.get("strClaimOID");
		String copyListId=(String)mapParameters.get("copyListId");
		String strClaimNAME = (String) eachClaimMap.get(DomainConstants.SELECT_NAME);
		String strACEContact = (String) eachClaimMap.get("ACECONTACT");
		//RTA 22x Added for ALM-49635 starts
		String strSequenceNumber=(String) eachClaimMap.get("attribute["+ATTRIBUTE_CLAIMSEQUENCE+"]");
		//RTA 22x Added for ALM-49635 ends
		//RTA 22x Added for ALM-46415 starts
		String strExpirationDate=(String) eachClaimMap.get("attribute["+ATTRIBUTE_PGEXPIRATIONDATE+"]");
		//RTA 22x Added for ALM-46415 ends
		LOGGER.log(Level.INFO, " ##RTADCM## reviseMCE strACEContact--->{0}", strACEContact);
		if(mapDisclaimer!=null) {
			strClaimOID=(String) mapDisclaimer.get("strDisclaimerId");
			strClaimNAME=(String) mapDisclaimer.get("strDisclaimerName");
			//RTA 22x Added for ALM-46415 starts
			strExpirationDate=(String) mapDisclaimer.get("strExpirationDate");
			//RTA 22x Added for ALM-46415 ends
		}
		//Revise MCE
		boolean isContextPushed = false;
		//RTA 22x Added for ALM-49635 starts
		boolean IsDisclaimer=false;
		//RTA 22x Added for ALM-49635 ends
		String autoName = "";
		String relationship_ArtworkElementContent  = PropertyUtil.getSchemaProperty(context,"relationship_ArtworkElementContent");
		String relationship_ContentLanguage        = PropertyUtil.getSchemaProperty(context,"relationship_ContentLanguage");
		String attribute_IsBaseCopy    = PropertyUtil.getSchemaProperty(context,"attribute_IsBaseCopy");
		try {
		LOGGER.log(Level.INFO, " ##RTADCM## Before Revise...newer code--->"+MCID);
		String relationship_ArtworkMaster     = PropertyUtil.getSchemaProperty(context,"relationship_ArtworkMaster");
		Map tempMapRIS = new HashMap();
		String sMCRISContent = "";
		StringList crSelects_ = new StringList();
		crSelects_.add(DomainConstants.SELECT_NAME);
		crSelects_.add(DomainConstants.SELECT_REVISION);
		crSelects_.add("last");		
		
		// Need to check if it is latest revision or not		
		DomainObject dobMC   = DomainObject.newInstance(context,MCID);
		Map InfoMap          = dobMC.getInfo(context,crSelects_);
		String originalRev   = (String) InfoMap.get(DomainConstants.SELECT_REVISION);
		String lastRev       = (String) InfoMap.get("last");
		
		StringList crSelects2_ = new StringList();
		crSelects2_.add("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].id");
		LOGGER.log(Level.INFO, " ##RTADCM## user--->{0}", context.getUser());
		ContextUtil.pushContext(context);
		isContextPushed = true;
		LOGGER.log(Level.INFO, " ##RTADCM## user after push --->{0}", context.getUser());
		LOGGER.log(Level.INFO, " ##RTADCM##  strClaimOID --->{0}", strClaimOID);
		DomainObject doRevisedClaim = DomainObject.newInstance(context,strClaimOID);
		LOGGER.log(Level.INFO, " ##RTADCM##  strClaimOID --->{0}", doRevisedClaim);		
		Map revisedCLWithOldMCData = doRevisedClaim.getInfo(context,crSelects2_,crSelects2_);
		
		if(isContextPushed) {
			ContextUtil.popContext(context);
			isContextPushed = false;
		}
		LOGGER.log(Level.INFO, " ##RTADCM## user after pop --->{0}", context.getUser());
		LOGGER.log(Level.INFO, " ##RTADCM## originalReve--->{0}", originalRev);
		LOGGER.log(Level.INFO, " ##RTADCM## lastRev--->{0}", lastRev);
	
		if(originalRev!=null && lastRev!=null && originalRev.equals(lastRev)) {
			
			ArtworkMaster master = new ArtworkMaster(MCID);
			//RTA 22x Added for ALM-44252 starts
			updateInactiveAuthorApprovers(context,MCID,strACEContact);
			//RTA 22x Added for ALM-44252 ends
			master.setAttributeValue(context, ATTRIBUTE_PGCONFIRMAUTHORAPPROVERASSIGNEE, REVIEWED);
			String sBaseLocalCopyID = getBaseLocalCopyInfoForMaster(context,MCID);
			ArtworkContent element = ArtworkContent.getNewInstance(context, sBaseLocalCopyID);
			ContextUtil.pushContext(context);
			isContextPushed = true;
			element.setAttributeValue(context, ATTRIBUTE_PGCONFIRMAUTHORAPPROVERASSIGNEE, REVIEWED);
			LOGGER.log(Level.INFO, " ##RTADCM## after confirm assignment--->{0}", sBaseLocalCopyID);						

			if(isContextPushed) {
				ContextUtil.popContext(context);
				isContextPushed = false;
			}
			
			String strClaimNam   = (String) eachClaimMap.get("attribute["+ATTRIBUTE_PGCLAIMNAME+"]");
			
			String strClaimNam_RTE   = (String) eachClaimMap.get("attribute["+ATTRIBUTE_PGCLAIMNAME_RTE+"]");
			if(mapDisclaimer!=null) {
				strClaimNam = (String) mapDisclaimer.get("strDisclaimer");
				strClaimNam_RTE = (String) mapDisclaimer.get("strDisclaimerRTE");	
			}
			
			CopyElement copy = new CopyElement(sBaseLocalCopyID);
			LOGGER.log(Level.INFO, " ##RTADCM## call setCopyTextOnClaimOrDisclaimer--->{0}", strClaimNam_RTE);
			LOGGER.log(Level.INFO, " ##RTADCM## call setCopyTextOnClaimOrDisclaimer--->{0}", strClaimCET);
			setCopyTextOnClaimOrDisclaimer(context,strClaimNam_RTE,strClaimNam_RTE,strClaimCET,copy);
			LOGGER.log(Level.INFO, " ##RTADCM## after call setCopyTextOnClaimOrDisclaimer--->");
			String latestRevisionId = copy.getLatestRevisionObjectId(context);
			
			//get Revised MCE
			DomainObject doMCE = DomainObject.newInstance(context,latestRevisionId);
			String revisedMCE_ID = doMCE.getInfo(context,"to["+REL_ARTWORKELEMENTCONTENT+"].from.id");
			
			System.out.println("Before revise MCE...."+MCID);
			System.out.println("After revise MCE...."+revisedMCE_ID);
			
			ArtworkMaster masterRevised = new ArtworkMaster(revisedMCE_ID);
			StringList artworkElementIds = masterRevised.getLocalCopies(context);
			for(String _lcid_ : (List<String>) artworkElementIds ){
				setPrimaryOwnership(context, _lcid_);
				LOGGER.log(Level.INFO, " ##RTADCM## _lcid_--->{0}", _lcid_);
				setObjectOwner(context, _lcid_, strACEContact);
				//ArtworkContent element2 = ArtworkContent.getNewInstance(context, _lcid_);
				//element2.setAttributeValue(context, ATTRIBUTE_PGCONFIRMAUTHORAPPROVERASSIGNEE, REVIEWED);
			}	
			
			//Obsolete prev CL
			//obsoleteCopyList(context,MCID);
			
			//connect revised MCE to CL
			
			CopyList copyList = new CopyList(copyListId);//ID of COPY LIST
			copyList.addArtworkMaster(context, masterRevised);

			//disConnectPrevMCEfromPrevCL_CPGProduct(context, MCID);
				
			Map tmpAtt1 = new HashMap();
			tmpAtt1.put(ATTRIBUTE_PLACEOFORIGIN,STR_YES);
				
			//Add MCE under all cdmCategories
			
			//connect revised MCE to Claim
			ContextUtil.pushContext(context);
			isContextPushed = true;

			// If MCE is revised on existing Claim then disconnect Old MCE revision from Claim
			DomainObject doObjClaim = DomainObject.newInstance(context, strClaimOID);
			String slClaimMCERelID = doObjClaim.getInfo(context, "from[" + RELATIONSHIP_PGMASTERCOPYCLAIM + "].id");
			LOGGER.log(Level.INFO, " ##RTADCM## reviseMCE disconnect slClaimMCERelID--->{0}", slClaimMCERelID);

			if(BusinessUtil.isNotNullOrEmpty(slClaimMCERelID))
			{
				callDisconnect(context, slClaimMCERelID);
			}
			
			LOGGER.log(Level.INFO, " ##RTADCM## reviseMCE connect revisedMCE_ID--->{0}", revisedMCE_ID);
			LOGGER.log(Level.INFO, " ##RTADCM## reviseMCE connect strClaimOID--->{0}", strClaimOID);
			callConnect(context,strClaimOID,RELATIONSHIP_PGMASTERCOPYCLAIM,revisedMCE_ID,null);
			
			if(isContextPushed) {
				ContextUtil.popContext(context);
				isContextPushed = false;
			}			

					
			String strNewClaimNam    = UIRTEUtil.getNonRTEString(context, strClaimNam);

			String type_DisclaimerMasterCopy      = PropertyUtil.getSchemaProperty(context,"type_DisclaimerMasterCopy");
			strNewClaimNam=replaceClaimNewLine(strNewClaimNam);
			Map mapChar=getMapOfSpecialChars(context);
			strNewClaimNam=replaceChar(context,strNewClaimNam,mapChar);
			String strClaimCET_ = strClaimCET.replace("Master Copy","").trim();
			
			if(strClaimCET.contains("Disclaimer")) {
				//RTA 22x Added for ALM-49635 starts
				IsDisclaimer=true;
				///RTA 22x Added for ALM-49635 ends
				String type_DisclaimerMasterCopy_ = type_DisclaimerMasterCopy.replace("Master Copy","").trim();
				StringBuilder disPlatTxt1 = new StringBuilder();
				disPlatTxt1.append(STRDCM);
				disPlatTxt1.append(STRTILDE);
				disPlatTxt1.append(type_DisclaimerMasterCopy_);
				disPlatTxt1.append(STRTILDE);
				disPlatTxt1.append(strNewClaimNam);	
				disPlatTxt1.append(STRTILDE);
				disPlatTxt1.append(strClaimNAME);
				masterRevised.setAttributeValue(context,AWLAttribute.MARKETING_NAME.get(context), disPlatTxt1.toString());
				updateMarketingNameOnLocalCopies(context,masterRevised,disPlatTxt1.toString());
			} else {
				StringBuilder disPlatTxt = new StringBuilder();
				disPlatTxt.append(STRDCM);
				disPlatTxt.append(STRTILDE);
				disPlatTxt.append(strClaimCET_);
				disPlatTxt.append(STRTILDE);
				disPlatTxt.append(strNewClaimNam);
				disPlatTxt.append(STRTILDE);
				disPlatTxt.append(strClaimNAME);
				masterRevised.setAttributeValue(context,AWLAttribute.MARKETING_NAME.get(context), disPlatTxt.toString());
				updateMarketingNameOnLocalCopies(context,masterRevised,disPlatTxt.toString());
			}
			//RTA 22x Added for ALM-49635 starts
			setSequenceNumberOnCopyList(context,copyListId,revisedMCE_ID,IsDisclaimer,strSequenceNumber);
			//RTA 22x Added for ALM-49635 ends
			//RTA 22x Added for ALM-46415 starts
			masterRevised.setAttributeValue(context, ATTRIBUTE_VALIDATE_DATE, strExpirationDate);
			//RTA 22x Added for ALM-46415 ends
			LOGGER.log(Level.INFO, " ##RTADCM## latestRevisionId --->{0}", latestRevisionId);
			promoteLCtoReviewReleaseState(context, latestRevisionId ,"Review"); // REVIEW so Task get completed
			promoteLCtoReviewReleaseState(context, latestRevisionId ,"Review");			
			LOGGER.log(Level.INFO, "reviseMCE - END");
		}	
	} catch(Exception e) {
			//e.printStackTrace();
			LOGGER.log(Level.SEVERE, e.toString());
			LOGGER.log(Level.SEVERE, e.getMessage());
			throw e;
	} finally {
			if(isContextPushed) {
				ContextUtil.popContext(context);
			}
	}
	}
	public static void updateMarketingNameOnLocalCopies(Context context,ArtworkMaster masterRevised,String strDisplayName)throws Exception {
		LOGGER.log(Level.INFO, "updateMarketingNameOnLocalCopies - START");
		try {
			String obsoleteState = AWLState.OBSOLETE.get(context, AWLPolicy.ARTWORK_ELEMENT);
			String whereClauseForPOASelects = new StringBuilder("current!=\"").append(obsoleteState).append("\" && revision == \"last\"").toString();
			Map mapLocalCopy=null;
			String strLCId=null;
			String strCopyLang=null;
			DomainObject domLC=null;
			String SEL_ATTR_COPYTEXT_LANGUAGE = AWLAttribute.COPY_TEXT_LANGUAGE.getSel(context);
			MapList revisedMasterConntectedLocalCopies = masterRevised.getArtworkElements(context, StringList.create(SEL_ATTR_COPYTEXT_LANGUAGE), null, whereClauseForPOASelects);
			for(int i=0;i<revisedMasterConntectedLocalCopies.size();i++) {
				mapLocalCopy=(Map)revisedMasterConntectedLocalCopies.get(i);
				strLCId=(String)mapLocalCopy.get(DomainConstants.SELECT_ID);
				strCopyLang=(String)mapLocalCopy.get(SEL_ATTR_COPYTEXT_LANGUAGE);
				domLC=DomainObject.newInstance(context, strLCId);
				domLC.setAttributeValue(context, AWLAttribute.MARKETING_NAME.get(context), strDisplayName+"_"+strCopyLang);
			}
		}catch(Exception e) {
			throw e;
		}
		LOGGER.log(Level.INFO, "updateMarketingNameOnLocalCopies - END");
	}
	public static void setCopyTextOnClaimOrDisclaimer(Context context,String strDisClaimer_RTE,String strClaimNam_RTE,String strClaimCET,CopyElement copy)throws Exception {
		LOGGER.log(Level.INFO, "setCopyTextOnClaimOrDisclaimer - START");
		try {
			LOGGER.log(Level.INFO, " ##RTADCM## setCopyTextOnClaimOrDisclaimer strClaimCET --->{0}", strClaimCET);			
			LOGGER.log(Level.INFO, " ##RTADCM## setCopyTextOnClaimOrDisclaimer User --->{0}", context.getUser());
			if(strClaimCET.contains("Disclaimer")) {
				copy.setCopyText(context, strDisClaimer_RTE);
				LOGGER.log(Level.INFO, " ##RTADCM## setCopyTextOnClaimOrDisclaimer IF strDisClaimer_RTE--->{0}", strDisClaimer_RTE);
			} else {
				LOGGER.log(Level.INFO, " ##RTADCM## setCopyTextOnClaimOrDisclaimer strClaimNam_RTE --->{0}", strClaimNam_RTE);
				copy.setCopyText(context, strClaimNam_RTE);
				LOGGER.log(Level.INFO, " ##RTADCM## setCopyTextOnClaimOrDisclaimer ELSE strClaimNam_RTE--->{0}", strClaimNam_RTE);
			}
		}catch(Exception e){
			throw e;
		}
		LOGGER.log(Level.INFO, "setCopyTextOnClaimOrDisclaimer - END");
	}
	public static String getBaseLocalCopyInfoForMaster(Context context,String sMasterElemID) throws Exception {
		LOGGER.log(Level.INFO, "getBaseLocalCopyInfoForMaster - START");
		try {
			String sBaseLocalCopyObjId = DomainConstants.EMPTY_STRING;
			if(BusinessUtil.isNotNullOrEmpty(sMasterElemID)) {
				ArtworkMaster artworkMaster = new ArtworkMaster(sMasterElemID);
				ArtworkContent artworkElementId	 = artworkMaster.getBaseArtworkElement(context);
				if(artworkElementId != null) 
					sBaseLocalCopyObjId = artworkElementId.getObjectId(context);
				}
			LOGGER.log(Level.INFO, "getBaseLocalCopyInfoForMaster - END");
			return sBaseLocalCopyObjId;
		}catch(Exception e){
			throw e;
		}
	}
	public static void promoteLCtoReviewReleaseState(Context context, String strLocalId,String strCurrent) throws Exception {	
		LOGGER.log(Level.INFO, "promoteLCtoReviewReleaseState - START");
		StringBuilder sbTaskId = new StringBuilder();
		sbTaskId.append("from[");
		sbTaskId.append(DomainConstants.RELATIONSHIP_OBJECT_ROUTE);
		sbTaskId.append("].to[");
		sbTaskId.append(DomainConstants.TYPE_ROUTE);
		sbTaskId.append("].to[");
		sbTaskId.append(DomainConstants.RELATIONSHIP_ROUTE_TASK);
		sbTaskId.append("].from.id");

		StringBuilder sbTaskIdOwner = new StringBuilder();
		sbTaskIdOwner.append("from[");
		sbTaskIdOwner.append(DomainConstants.RELATIONSHIP_OBJECT_ROUTE);
		sbTaskIdOwner.append("].to[");
		sbTaskIdOwner.append(DomainConstants.TYPE_ROUTE);
		sbTaskIdOwner.append("].to[");
		sbTaskIdOwner.append(DomainConstants.RELATIONSHIP_ROUTE_TASK);
		sbTaskIdOwner.append("].from.owner");

		StringList slLCSelects=new StringList(4);
		slLCSelects.add(DomainConstants.SELECT_CURRENT);
		slLCSelects.add(sbTaskId.toString());
		slLCSelects.add(sbTaskIdOwner.toString());
		slLCSelects.add(DomainConstants.SELECT_OWNER);		
		boolean isContextPushed = false;
		
		try{
			if(BusinessUtil.isNotNullOrEmpty(strLocalId)){
				DomainObject domLocalObj = DomainObject.newInstance(context,strLocalId);
				LOGGER.log(Level.INFO, " ##RTADCM## promoteLCtoReviewReleaseState domLocalObj --->{0}", domLocalObj);
				LOGGER.log(Level.INFO, " ##RTADCM## promoteLCtoReviewReleaseState strCurrent --->{0}", strCurrent);
				LOGGER.log(Level.INFO, " ##RTADCM## promoteLCtoReviewReleaseState strCurrent --->{0}", domLocalObj.getAttributeValue(context, "Copy Text_RTE"));
				LOGGER.log(Level.INFO, " ##RTADCM## promoteLCtoReviewReleaseState strCurrent --->{0}", domLocalObj.getInfo(context, "from[Object Route].to.id"));
				if("Preliminary".equalsIgnoreCase(strCurrent)) {
					LOGGER.log(Level.INFO, " ##RTADCM## promoteLCtoReviewReleaseState IF --->{0}", strCurrent);
						domLocalObj.promote(context);
				}else {
					LOGGER.log(Level.INFO, " ##RTADCM## promoteLCtoReviewReleaseState ELSE --->{0}", strCurrent);
					String []arrObjId = {strLocalId};
					BusinessObjectWithSelectList boITTaskInfo =  BusinessObject.getSelectBusinessObjectData(context,arrObjId,slLCSelects);
					if(!boITTaskInfo.isEmpty()) {
						BusinessObjectWithSelect boITTask  = boITTaskInfo.getElement(0);
						StringList sListTaskId = boITTask.getSelectDataList(sbTaskId.toString());
						StringList slTaskOwner =boITTask.getSelectDataList(sbTaskIdOwner.toString());
						LOGGER.log(Level.INFO, " ##RTADCM## promoteLCtoReviewReleaseState sListTaskId --->{0}", sListTaskId);
						//DCM:US-4383 RTA DCM Integration Code changes as per New data Model Changes  - Start
						if(sListTaskId!=null && !sListTaskId.isEmpty()) {
						//DCM:US-4383 RTA DCM Integration Code changes as per New data Model Changes  - Start
							completeTask(context,isContextPushed,sListTaskId,slTaskOwner);
						}
					}
				}
			}
		}catch(Exception ex) {
			throw ex;
		}
		LOGGER.log(Level.INFO, "promoteLCtoReviewReleaseState - END");
	}
	
	public static void completeTask(Context context,boolean isContextPushed,StringList sListTaskId,StringList slTaskOwner)throws Exception {		
		LOGGER.log(Level.INFO, "completeTask - START");
		try {
			int iSize = sListTaskId.size();
			String sTaskOwner ="";
			for(int i=0;i<iSize;i++) {
				sTaskOwner = slTaskOwner.get(i);
				if(!sTaskOwner.equalsIgnoreCase(context.getUser())) {	
					//in order to complete the task, need to push context to task owner and then complete
					ContextUtil.pushContext(context, sTaskOwner, DomainConstants.EMPTY_STRING,context.getVault().getName());
					isContextPushed = true;
				}
				DomainObject domInbox = DomainObject.newInstance(context, sListTaskId.get(i));
				domInbox.setState(context, pgV3Constants.STATE_COMPLETE);
				if(isContextPushed) {	
					ContextUtil.popContext(context);
					isContextPushed = false;
				}
			}	
		}catch(Exception ex) {
			throw ex;
		}
		finally {
			if(isContextPushed) {	
				ContextUtil.popContext(context);
				isContextPushed = false;
			}
		}
		LOGGER.log(Level.INFO, "completeTask - END");
	}

	public static void setPrimaryOwnership(Context context,String sObjectId) throws Exception {	
		LOGGER.log(Level.INFO, "setPrimaryOwnership - START");
		try {
			if (BusinessUtil.isNotNullOrEmpty(sObjectId)) {
				DomainObject domObj = DomainObject.newInstance(context,sObjectId);
				domObj.setPrimaryOwnership(context, "Internal_PG", "PG");
			}	
		}catch(Exception e){
			throw e;
		}
		LOGGER.log(Level.INFO, "setPrimaryOwnership - END");
	}
	public static String getACEContactObjectId(Context context, String strACEName) throws Exception {
		LOGGER.log(Level.INFO, "getACEContactObjectId - START");
		try {
		String strAuthorId = "";
		StringList selectsACE = new StringList();
		selectsACE.add(DomainConstants.SELECT_ID);
		selectsACE.add(DomainConstants.SELECT_NAME);
		MapList personsList  = DomainObject.findObjects(context,			// context object reference
																TYPE_PERSON,		// type pattern
																strACEName, 			// name pattern
																DomainConstants.QUERY_WILDCARD, // revision pattern
																DomainConstants.QUERY_WILDCARD, // owner pattern
																ESERPROD,			// vault pattern
																null,				// where condition
																false,				// expand type
																selectsACE			// object selects
																);
		for(int k=0;k<personsList.size();k++) {
			Map tmpMapACE    = (Map) personsList.get(k);
			strAuthorId = (String) tmpMapACE.get(DomainConstants.SELECT_ID);
			
		}
		LOGGER.log(Level.INFO, "getACEContactObjectId - END");
		return strAuthorId;
		}catch(Exception e){
			throw e;
		}
	}
	public static void callDisconnect(Context context, String relId) throws FrameworkException {
		DomainRelationship.disconnect(context,relId);
	}
	
	public static void callConnect(Context context, String strFromID,String relName, String toId, Map attMap) throws FrameworkException {
		DomainRelationship dor = DomainRelationship.connect(context,DomainObject.newInstance(context,strFromID),relName,DomainObject.newInstance(context,toId));
		if(attMap!=null)
			dor.setAttributeValues(context,attMap);
	}
	
	public static Map getMapDataWithPushContext(Context context, String objId, StringList claaimSelects) throws Exception {
		boolean isContextPushed = false;
		Map returnMap = null;
		LOGGER.log(Level.INFO, " ##RTADCM## getMapDataWithPushContext Entry---> {0}", context.getUser());
		try {
			// RTA user doesnt have access on claim(DCM) data.
			ContextUtil.pushContext(context);
			isContextPushed = true;
			DomainObject claimOID = DomainObject.newInstance(context,objId);
			returnMap  = claimOID.getInfo(context, claaimSelects,claaimSelects);
			//Added to get MCs from old claim
			MapList mlClaimRevisions = claimOID.getRevisionsInfo(context, StringList.create(DomainConstants.SELECT_ID,DomainConstants.SELECT_ORIGINATED), new StringList());
			mlClaimRevisions.sort(DomainConstants.SELECT_ORIGINATED,"descending", "date");
			StringList slClaimsIds = BusinessUtil.toStringList(mlClaimRevisions, DomainConstants.SELECT_ID);
			MapList mlClaimsRevisionInfo=BusinessUtil.getInfoList(context, slClaimsIds, BusinessUtil.toStringList("attribute["+ATTRIBUTE_PGCLAIMNAME+"]",
						"attribute["+ATTRIBUTE_PGCLAIMNAME_RTE+"]","from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id",
						 "from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.type","from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].id"));
			
			for(int i=0;i<mlClaimsRevisionInfo.size();i++) {
				Map mapInfo=(Map)mlClaimsRevisionInfo.get(i);
				if(mapInfo!=null) {
					returnMap.put("MCsFromOldClaim", mapInfo);
					break;
				}
			}

			for(int i=0;i<mlClaimsRevisionInfo.size();i++) {
				Map mapInfo=(Map)mlClaimsRevisionInfo.get(i);
				if(mapInfo!=null && mapInfo.containsKey("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id")) {
					returnMap.put("MCsFromOldClaim", mapInfo);
					break;
				}
			}
		}catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.toString());
			LOGGER.log(Level.SEVERE, e.getMessage());
			throw e;
		} finally {
			if(isContextPushed)
				ContextUtil.popContext(context);
		}
		LOGGER.log(Level.INFO, " ##RTADCM## getMapDataWithPushContext returnMap---> {0}", returnMap);
		LOGGER.log(Level.INFO, " ##RTADCM## getMapDataWithPushContext Exit---> {0}", context.getUser());
		return returnMap;
	}

	public static Map getMapDataWithPushContextGraphic(Context context, String objId, StringList claaimSelects) throws Exception {
		boolean isContextPushed = false;
		Map returnMap = null;
		LOGGER.log(Level.INFO, " ##RTADCM## getMapDataWithPushContext Entry---> {0}", context.getUser());
		try {
			// RTA user doesnt have access on claim(DCM) data.
			ContextUtil.pushContext(context);
			isContextPushed = true;
			DomainObject claimOID = DomainObject.newInstance(context,objId);
			returnMap  = claimOID.getInfo(context, claaimSelects,claaimSelects);
			//Added to get MCs from old claim
			MapList mlClaimRevisions = claimOID.getRevisionsInfo(context, StringList.create(DomainConstants.SELECT_ID,DomainConstants.SELECT_ORIGINATED), new StringList());
			mlClaimRevisions.sort(DomainConstants.SELECT_ORIGINATED,"descending", "date");
			StringList slClaimsIds = BusinessUtil.toStringList(mlClaimRevisions, DomainConstants.SELECT_ID);
			MapList mlClaimsRevisionInfo=BusinessUtil.getInfo(context, slClaimsIds, BusinessUtil.toStringList("attribute["+ATTRIBUTE_PGCLAIMGRAPHICALIMAGE+"]",
			"from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id", "from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.type","from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].id"));
			//StringList slConnectedMCs=BusinessUtil.getInfo(context, slClaimsIds, "from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id");
			for(int i=0;i<mlClaimsRevisionInfo.size();i++) {
				Map mapInfo=(Map)mlClaimsRevisionInfo.get(i);
				if(mapInfo!=null) {
					returnMap.put("MCsFromOldClaim", mapInfo);
					break;
				}
			}

			for(int i=0;i<mlClaimsRevisionInfo.size();i++) {
				Map mapInfo=(Map)mlClaimsRevisionInfo.get(i);
				if(mapInfo!=null && mapInfo.containsKey("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id")) {
					returnMap.put("MCsFromOldClaim", mapInfo);
					break;
				}
			}
		}catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.toString());
			LOGGER.log(Level.SEVERE, e.getMessage());
			throw e;
		} finally {
			if(isContextPushed)
				ContextUtil.popContext(context);
		}
		LOGGER.log(Level.INFO, " ##RTADCM## getMapDataWithPushContext returnMap---> {0}", returnMap);
		LOGGER.log(Level.INFO, " ##RTADCM## getMapDataWithPushContext Exit---> {0}", context.getUser());
		return returnMap;
	}
	
	public static String getConnectedModelCPGProductId(Context context, String mrkID,String crName,String relProductLineModels,String strMarketingNameAtt, String relationship_MainProduct ) throws Exception {
		LOGGER.log(Level.INFO, "getConnectedModelCPGProductId - START");
		try {
		String existingCPGId = "";
		if(mrkID!=null) {
			DomainObject mrkDO = DomainObject.newInstance(context,mrkID);
			StringList mrkSelects_ = new StringList();
			mrkSelects_.add("from["+relProductLineModels+"].to.attribute["+strMarketingNameAtt+"]");
			mrkSelects_.add("from["+relProductLineModels+"].to.id");
			Map MrkConnectedModelData = mrkDO.getInfo(context,mrkSelects_,mrkSelects_);
					
			if(MrkConnectedModelData!=null && MrkConnectedModelData.containsKey("from["+relProductLineModels+"].to.attribute["+strMarketingNameAtt+"]")) {
				StringList crNamesOnModel = (StringList) MrkConnectedModelData.get("from["+relProductLineModels+"].to.attribute["+strMarketingNameAtt+"]");
				StringList ModelIds = (StringList) MrkConnectedModelData.get("from["+relProductLineModels+"].to.id");
				if(crNamesOnModel.contains(crName)) {
					int idx_ = crNamesOnModel.indexOf(crName);
					String modelId = (String) ModelIds.get(idx_);
					DomainObject modelDO = DomainObject.newInstance(context,modelId);
					existingCPGId =  modelDO.getInfo(context,"from["+relationship_MainProduct+"].to.id");
				}
			}
		} else {
			LOGGER.log(Level.INFO, mrkID);
		}
		LOGGER.log(Level.INFO, "getConnectedModelCPGProductId - END");
		return existingCPGId;
		}catch(Exception e){
			throw e;
		}
	}
	
	public static void createRoute (Context context,String crACE1,String copyListId, Map crInfoMap) throws FrameworkException {
		LOGGER.log(Level.INFO, "createRoute - START");
		//Create a Route
		String strClaimsData = STR_CLAIM_INFO;
		String strRelease    = STATE_RELEASE;
		String strClaimRoute = STR_CLAIM_ROUTE;
		//String strTitleValue = STR_ROUTE_DESC;
		//String strRouteInsts = STR_ROUTE_INST;
		
		try {
		Person objPerson 		= Person.getPerson(context, crACE1); // pending
		Route route 			= (Route) DomainObject.newInstance(context, DomainConstants.TYPE_ROUTE);
		String strRouteName 	= FrameworkUtil.autoName(context, AWLType.ROUTE.toString(), new Policy(DomainConstants.POLICY_ROUTE).getFirstInSequence(context), "policy_Route", null, null, true, true);
		route.createObject(context, DomainConstants.TYPE_ROUTE, strRouteName, null, DomainConstants.POLICY_ROUTE, null);
		
		String clrname = "";
		if(crInfoMap!=null)
			clrname = (String) crInfoMap.get("name");
		Map routeAttributesMap = new HashMap();
		routeAttributesMap.put(ATTRIBUTE_ARTWORKINFO, strClaimsData);
		route.setOwner(context, crACE1); //pending
	    route.setDescription(context, strClaimRoute);
	    route.connect(context, new RelationshipType(DomainConstants.RELATIONSHIP_PROJECT_ROUTE), true, objPerson);
	    route.setAttributeValues(context, routeAttributesMap);
	    connectObjectToRoute(context, copyListId, route, strRelease,crACE1,clrname); //pending - setting on release. 
		route.promote(context);
		
		} catch(Exception e) {
			throw new FrameworkException(e);
		}
		LOGGER.log(Level.INFO, "createRoute - END");
	}
	public static void connectObjectToRoute(Context context, String objectId, Route route, String stateToAddRoute, String crACE1,String clrname) throws FrameworkException {
		LOGGER.log(Level.INFO, "connectObjectToRoute - START");
		try {
			String strTitleValue = STR_ROUTE_DESC;
			String strRouteInsts = STR_ROUTE_INST;
			// Add Copy object as Content to this route
			DomainObject domainObject = DomainObject.newInstance(context, objectId);
			
			DomainRelationship dmoRelationship = DomainRelationship.connect(context, domainObject, DomainConstants.RELATIONSHIP_OBJECT_ROUTE, route);
			HashMap mapRelAttributesNew = new HashMap();
			mapRelAttributesNew.put(DomainConstants.ATTRIBUTE_ROUTE_BASE_PURPOSE, 
					BusinessUtil.getAttribute(context, route.getId(context), DomainConstants.ATTRIBUTE_ROUTE_BASE_PURPOSE));
			
			if(BusinessUtil.isNotNullOrEmpty(stateToAddRoute)) {
				String strPolicyName = domainObject.getInfo(context, DomainConstants.SELECT_POLICY);
				String stateNameSymbolic = FrameworkUtil.reverseLookupStateName(context, strPolicyName, stateToAddRoute);
				String strPolicyNameSymbolic = FrameworkUtil.getAliasForAdmin(context,"Policy", strPolicyName, true);
				mapRelAttributesNew.put(DomainConstants.ATTRIBUTE_ROUTE_BASE_POLICY, strPolicyNameSymbolic);
				mapRelAttributesNew.put(DomainConstants.ATTRIBUTE_ROUTE_BASE_STATE, stateNameSymbolic);
			}
			dmoRelationship.setAttributeValues(context, mapRelAttributesNew);
			
			//Connect CL to Assignee
			//getAceContactId();
			StringList selectsACE = new StringList();
			selectsACE.add(DomainConstants.SELECT_ID);
			selectsACE.add(DomainConstants.SELECT_NAME);
			String strACEContactId = "";
			if(crACE1!=null && !("").equals(crACE1)) {
				MapList personsList  = DomainObject.findObjects(context,			// CONTEXT OBJECT REFERENCE
																TYPE_PERSON,        // type Pattern
																crACE1, 			// name pattern
																DomainConstants.QUERY_WILDCARD,  //revision pattern
																DomainConstants.QUERY_WILDCARD,  // owner pattern
																ESERPROD,			// vault pattern
																null,				// where condition
																false,				// expand type
																selectsACE			// object selects
																);
				for(int k=0;k<personsList.size();k++) {
					Map tmpMapACE    = (Map) personsList.get(k);
					strACEContactId = (String) tmpMapACE.get(DomainConstants.SELECT_ID);
				}
			}
			Map routeNodeDetails = new HashMap();
			DomainObject toObject 	= DomainObject.newInstance(context, strACEContactId); //pending.. update acecontact id
			DomainRelationship drRouteNode = DomainRelationship.connect(context, route, DomainConstants.RELATIONSHIP_ROUTE_NODE, toObject);
			routeNodeDetails.put(DomainConstants.ATTRIBUTE_ROUTE_NODE_ID, drRouteNode.getName());
			routeNodeDetails.put("Route Sequence","1");
			routeNodeDetails.put(DomainConstants.ATTRIBUTE_ALLOW_DELEGATION, AWLConstants.RANGE_TRUE);
			SimpleDateFormat dateFormat = new SimpleDateFormat( "MM/dd/yyyy hh:mm:ss a" );   
			Calendar cal = Calendar.getInstance();    
			cal.add( Calendar.DATE, 10 );    
			String convertedDate=dateFormat.format(cal.getTime());    
			routeNodeDetails.put("Scheduled Completion Date",convertedDate);
			StringBuilder sbTitle = new StringBuilder();
			sbTitle.append(strTitleValue);
			sbTitle.append(clrname);
			StringBuilder sbRouteInst = new StringBuilder();
			sbRouteInst.append(strRouteInsts);
			sbRouteInst.append(clrname);
			routeNodeDetails.put(ATTRIBUTE_TITLE, sbTitle.toString());
			routeNodeDetails.put(ATTRIBUTE_ROUTEINSTRUCTIONS, sbRouteInst.toString());
	        drRouteNode.setAttributeValues(context, routeNodeDetails);			
		
		}catch (Exception e) {
			throw new FrameworkException(e);
		}
		LOGGER.log(Level.INFO, "connectObjectToRoute - END");
	}

	public static void obsoleteOldCL(Context context,List plCategories,String newcopyListId) throws Exception {
		LOGGER.log(Level.INFO, "obsoleteOldCL - START");
		try {
		StringList sl1 = new StringList();
		//sl1.add("from["+REL_CLARTWORKMASTER+"].to.name");
		//sl1.add("from["+REL_CLARTWORKMASTER+"].to.id");
		StringList newCLConnectedMCnames = null;	
		StringList sl = new StringList();
		sl.add("from["+RELATIONSHIP_ASSOCIATEDCOPYLIST +"].to.id");
		LOGGER.log(Level.INFO, " ##RTADCM## obsoleteOldCL plCategories---> {0}", plCategories);
		for(int i=0;i<plCategories.size();i++) {
			String catID = (String) plCategories.get(i);
			LOGGER.log(Level.INFO, " ##RTADCM## obsoleteOldCL catID---> {0}", catID);
			if(BusinessUtil.isNotNullOrEmpty(catID)) {
				// Get prev copy list form CPG product (CLR#)
				Map prevCLData = getconnectedData(context, catID, sl);
				LOGGER.log(Level.INFO, " ##RTADCM## obsoleteOldCL prevCLData---> {0}", prevCLData);
				if(prevCLData!=null && prevCLData.containsKey("from["+RELATIONSHIP_ASSOCIATEDCOPYLIST + "].to.id")) {
					StringList CLIDs = (StringList) prevCLData.get("from["+RELATIONSHIP_ASSOCIATEDCOPYLIST + "].to.id");
					for(int j=0;j<CLIDs.size();j++) {
						String prevCLID = (String) CLIDs.get(j);
						Map prevData = getconnectedData(context,prevCLID,sl1);
						//Move MCs from old CL to new CL
						//moveMCsFromOldCLToNewCL(context,newcopyListId,prevData,sl1,newCLConnectedMCnames);
						//Obsolete CL		
						LOGGER.log(Level.INFO, "Before Obsoleting CL -> START");
						LOGGER.log(Level.INFO, prevCLID);
						obsoleteCopyList(context,prevCLID, false);
						LOGGER.log(Level.INFO, "After Obsoleting CL -> START");
						
					}
				} else {
					LOGGER.log(Level.INFO, "Not connected to any CL");
				}
			}
		}
		}catch(Exception e) {
			LOGGER.log(Level.SEVERE, e.toString());
			LOGGER.log(Level.SEVERE, e.getMessage());
			throw e;
		}
		LOGGER.log(Level.INFO, "obsoleteOldCL - END");
	}
	public static void moveMCsFromOldCLToNewCL(Context context,String newcopyListId,Map prevData,StringList sl1,StringList newCLConnectedMCnames) throws Exception {
		LOGGER.log(Level.INFO, "moveMCsFromOldCLToNewCL - START");
		try {
		if(prevData!=null && prevData.containsKey("from["+REL_CLARTWORKMASTER+"].to.name")) {
			StringList prevCLConnectedMCnames = (StringList) prevData.get("from["+REL_CLARTWORKMASTER+"].to.name");
			StringList prevCLConnectedMCIDs = (StringList) prevData.get("from["+REL_CLARTWORKMASTER+"].to.id");

				Map newData = getconnectedData(context,newcopyListId,sl1);
				if(newData!=null && newData.containsKey("from["+REL_CLARTWORKMASTER+"].to.name"))
					newCLConnectedMCnames = (StringList) newData.get("from["+REL_CLARTWORKMASTER+"].to.name");
				
				for(int k=0;k<prevCLConnectedMCnames.size();k++) {
					String prevCLConnectedMCName = (String) prevCLConnectedMCnames.get(k);
					if(newCLConnectedMCnames!=null && !newCLConnectedMCnames.contains(prevCLConnectedMCName)) {
							//Move MCE and LC's from OLD CL to new CL.
							String MCEID = prevCLConnectedMCIDs.get(k);						
							moveMCEandLCFromPrevCL2NewCL(context,newcopyListId,MCEID);
					}
				}
			}
		}catch(Exception e){
			throw e;
		}
		LOGGER.log(Level.INFO, "moveMCsFromOldCLToNewCL - END");
	}
	public static void moveMCEandLCFromPrevCL2NewCL(Context context,String newcopyListId,String MCEID) throws Exception {
		try {
		LOGGER.log(Level.INFO, "moveMCEandLCFromPrevCL2NewCL - START");
		LOGGER.log(Level.INFO, newcopyListId);
		LOGGER.log(Level.INFO, MCEID);
		DomainRelationship.connect(context,DomainObject.newInstance(context,newcopyListId),REL_CLARTWORKMASTER,DomainObject.newInstance(context,MCEID));
		StringList sl = new StringList();
		sl.add("from["+REL_ARTWORKELEMENTCONTENT+"].to.id");
		Map LCData = getconnectedData(context,MCEID,sl);
		if(LCData!=null && LCData.containsKey("from["+REL_ARTWORKELEMENTCONTENT+"].to.id")) {
			StringList lcs = (StringList) LCData.get("from["+REL_ARTWORKELEMENTCONTENT+"].to.id");
			for(int i =0 ; i<lcs.size(); i++) {
				callConnect(context,newcopyListId,RELATIONSHIP_ARTWORKASSEMBLY,lcs.get(i),null);
			}
		}
		LOGGER.log(Level.INFO, "moveMCEandLCFromPrevCL2NewCL - DONE");
		}catch(Exception e){
			throw e;
		}
		
	}
	
	public static Map getconnectedData(Context context,String CLID, StringList selects) throws Exception {
		Map Data = null;
		try {
		DomainObject doCL = DomainObject.newInstance(context, CLID);
		Data = doCL.getInfo(context,selects,selects);
		}catch(Exception e) {
			LOGGER.log(Level.SEVERE, e.toString());
			LOGGER.log(Level.SEVERE, e.getMessage());
			throw e;
		}
		return Data;
	}
	public static void disconnectPrevCLFromCPG(Context context, List plCategories) throws Exception {
		try {
		StringList sl = new StringList();
		sl.add("from["+RELATIONSHIP_ASSOCIATEDCOPYLISTHISTORY+"].id");
		LOGGER.log(Level.INFO, "disconnectPrevCLFromCPG - START");
		for(int i=0;i<plCategories.size();i++) {
			String catID = (String) plCategories.get(i);
			LOGGER.log(Level.INFO, catID);
			if(BusinessUtil.isNotNullOrEmpty(catID)) {
				Map relIDDataFromCPG2CL = getconnectedData(context, catID, sl);
				if(relIDDataFromCPG2CL!=null && relIDDataFromCPG2CL.containsKey("from["+RELATIONSHIP_ASSOCIATEDCOPYLISTHISTORY+"].id")) {
					StringList relIds = (StringList) relIDDataFromCPG2CL.get("from["+RELATIONSHIP_ASSOCIATEDCOPYLISTHISTORY+"].id");
					//LOGGER.log(Level.INFO, "disconnectPrevCLFromCPG");
					for(int j=0;j<relIds.size();j++) {
						LOGGER.log(Level.INFO,relIds.get(j));
						//DomainRelationship.disconnect(context,relIds.get(j));
						callDisconnect(context,relIds.get(j));
					}
				}
			}	
		}
		LOGGER.log(Level.INFO, "disconnectPrevCLFromCPG - END");
		}catch(Exception e){
			throw e;
		}
	}
	public static void obsoleteCopyList(Context context, String prevCLID, boolean bContextPushed) throws Exception {
		LOGGER.log(Level.INFO, "obsoleteCopyList - START");
		boolean bPushContextCustom = false;
		
		try {
		String Release =AWLState.RELEASE.get(context, AWLPolicy.COPY_LIST) ;
		String Complete = AWLState.COMPLETE.get(context, AWLPolicy.INBOX_TASK);;
		StringList sl = new StringList();
		sl.add("current");
		//RTA(DS) - 22x.04 - Added the code for ALM-55027 - Start
		sl.add( "from["+RELATIONSHIP_OBJECTROUTE+"].to.to["+RELATIONSHIP_ROUTETASK+"].from.id");
		sl.add( "from["+RELATIONSHIP_OBJECTROUTE+"].to.to["+RELATIONSHIP_ROUTETASK+"].from.current");
		DomainObject doCLID = DomainObject.newInstance(context,prevCLID);
		Map clData = doCLID.getInfo(context,sl);
		LOGGER.log(Level.INFO, " ##RTADCM## clData ---> {0}", clData);
		if(clData!=null) {
			String clState = (String) clData.get("current");
			LOGGER.log(Level.INFO, " ##RTADCM## clState ---> {0}", clState);
			if(Release.equals(clState)) {
				LOGGER.log(Level.INFO, " ##RTADCM## Inside IF for obsoleting CL ---> {0}", prevCLID);
				if(clData.containsKey("from["+RELATIONSHIP_OBJECTROUTE+"].to.to["+RELATIONSHIP_ROUTETASK+"].from.id")) {
					String ITTaskId = (String) clData.get("from["+RELATIONSHIP_OBJECTROUTE+"].to.to["+RELATIONSHIP_ROUTETASK+"].from.id");
					LOGGER.log(Level.INFO, " ##RTADCM## ITTaskId ---> {0}", ITTaskId);
					String strITTaskCurrent=(String) clData.get("from["+RELATIONSHIP_OBJECTROUTE+"].to.to["+RELATIONSHIP_ROUTETASK+"].from.current");
					LOGGER.log(Level.INFO, " ##RTADCM## strITTaskCurrent ---> {0}", strITTaskCurrent);
					DomainObject doITtask = DomainObject.newInstance(context,ITTaskId);
					
					// Task can be completed by Owner or Super User only
					if(bContextPushed)
					{
						if(!strITTaskCurrent.equalsIgnoreCase(Complete)) {
							doITtask.setState(context, Complete);
						}
					}
					else{
						//DCM User do not have access on RTA data
						ContextUtil.pushContext(context);
						bPushContextCustom = true;
						if(!strITTaskCurrent.equalsIgnoreCase(Complete)) {
							doITtask.setState(context, Complete);
						}
						//RTA(DS) - 22x.04 - Added the code for ALM-55027 - End
						String itState = doITtask.getInfo(context,"current");
						LOGGER.log(Level.INFO, itState);
						LOGGER.log(Level.INFO, "ITTaskState");
						if(bPushContextCustom) {
							ContextUtil.popContext(context);
							bPushContextCustom = false;
						}
					}
					LOGGER.log(Level.INFO, " ##RTADCM## Before Obsoleting Prev CL ID ---> {0}", prevCLID);
					doCLID.promote(context);					
					
					LOGGER.log(Level.INFO, "obsoleteCopyList Obsoleting id");
					LOGGER.log(Level.INFO, prevCLID);
					LOGGER.log(Level.INFO, "obsoleteCopyList Obsoleting done");
				}else{
					LOGGER.log(Level.INFO, " ##RTADCM## Else block Before Obsoleting Prev CL ID ---> {0}", prevCLID);
					doCLID.promote(context);					
					
					LOGGER.log(Level.INFO, "Else block obsoleteCopyList Obsoleting id");
					LOGGER.log(Level.INFO, prevCLID);
					LOGGER.log(Level.INFO, "Else block obsoleteCopyList Obsoleting done");					
				}
			} else {
				LOGGER.log(Level.INFO, "Following Copy List is not on Release state");
				LOGGER.log(Level.INFO, prevCLID);
			}
		}
		}catch(Exception e){
			throw e;
		}
		finally{
			if(bPushContextCustom) {
				ContextUtil.popContext(context);
				bPushContextCustom = false;
			}
		}
		LOGGER.log(Level.INFO, "obsoleteCopyList - END");
	}
	
	public static void connectUnRevisedMCEtoRevisedClaim(Context context,Map prevClaimInfoMap,String strClaimOID,String strCopyListId) throws Exception {
		boolean bPushContext = false;
		try {
		ContextUtil.pushContext(context);
		bPushContext = true;
		LOGGER.log(Level.INFO, "connectUnRevisedMCEtoRevisedClaim - START {0}", prevClaimInfoMap);
		if(prevClaimInfoMap.containsKey("previous.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id")) {
			StringList tmp3ListMCEs = (StringList) prevClaimInfoMap.get("previous.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id");
			for(int mceCtr = 0; mceCtr < tmp3ListMCEs.size(); mceCtr ++) {
				String mceID = tmp3ListMCEs.get(mceCtr);
				callConnect(context,strClaimOID,RELATIONSHIP_PGMASTERCOPYCLAIM,mceID,null);
				LOGGER.log(Level.INFO,mceID);
			}
		}
	
		//Disconnect prev ones
		if(prevClaimInfoMap.containsKey("previous.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].id")) {
			LOGGER.log(Level.INFO, "Disconnecting Revisions from prev Claim - START");
			StringList tmp3ListMCERels = (StringList) prevClaimInfoMap.get("previous.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].id");
			for(int mceCtr = 0; mceCtr < tmp3ListMCERels.size(); mceCtr ++) {
				String mcerelID = tmp3ListMCERels.get(mceCtr);
				callDisconnect(context,mcerelID);
				LOGGER.log(Level.INFO,mcerelID);
			}
			LOGGER.log(Level.INFO, "Disconnecting Revisions from prev Claim - END");
			
		}

		if(bPushContext) {
			ContextUtil.popContext(context);
			bPushContext = false;
		}
		
		//connect MC to CL 
		LOGGER.log(Level.INFO, "Connecting Master Copies to new CopyList - START");
		CopyList copyList = new CopyList(strCopyListId);//ID of COPY LIST
		if(prevClaimInfoMap.containsKey("previous.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id")) {
			StringList slPrevClaimMCEs = (StringList) prevClaimInfoMap.get("previous.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id");
			for(int mceCtr = 0; mceCtr < slPrevClaimMCEs.size(); mceCtr ++) {
				String mceID = slPrevClaimMCEs.get(mceCtr);
				copyList.addArtworkMaster(context, new ArtworkMaster(mceID));
				//callConnect(context,strCopyListId,REL_CLARTWORKMASTER,mceID,null);
				LOGGER.log(Level.INFO,mceID);
			}
		}
		LOGGER.log(Level.INFO, "Connecting Master Copies to new CopyList - END");
		LOGGER.log(Level.INFO, "connectUnRevisedMCEtoRevisedClaim - END");
		}catch(Exception e) {
			LOGGER.log(Level.SEVERE, e.toString());
			LOGGER.log(Level.SEVERE, e.getMessage());
			throw e;
		}
		finally{
			if(bPushContext) {
				ContextUtil.popContext(context);
				bPushContext = false;
			}
		}
	}
	
	public Map doWithTriggerOff(Context context, String objId, String eventType, StringList selects, Map params) throws Exception {
		boolean isContextPushed = false;
		Map returnMap = new HashMap();
		try {
			//This method gets triggered by DCM User but DCM user doesnt have access on RTA data.
			ContextUtil.pushContext(context);
			isContextPushed = true;
			//Since DCM and RTA users are not same and we need to connect RTA objects to DCM objects need to do with trigger off.
			String strResult11 = MqlUtil.mqlCommand(context, "trigger off").trim();
			
			if("promote".equals(eventType) && objId!=null) {
				DomainObject dObj = DomainObject.newInstance(context,objId);
				dObj.promote(context);
			} else if("connect".equals(eventType)) {
				String fromID    = (String) params.get("fromid");
				String toID      = (String) params.get("toid");
				String relname   = (String) params.get("relname");
				DomainRelationship.connect(context,DomainObject.newInstance(context,fromID),relname,DomainObject.newInstance(context,toID));
			} else if("disconnect".equals(eventType)) {
				
			}
		} catch(Exception e) {
			LOGGER.log(Level.INFO, " Exception in doWithTriggerOff #####"+objId);
			LOGGER.log(Level.SEVERE, e.toString());
			LOGGER.log(Level.SEVERE, e.getMessage());
			throw e;
		} finally {
			if(isContextPushed) {
				String strResult1 = MqlUtil.mqlCommand(context, "trigger on").trim(); 
				ContextUtil.popContext(context);				
			}
		}
		return returnMap;
	}

	public void createGraphicElement(Context context,Map eachClaimMap,List plCategories,String copyListId, String strCRId, Map crInfoMap) throws Exception{
		LOGGER.log(Level.INFO, "createGraphicElement - START");		
		try {
			String strClaimOID   = (String) eachClaimMap.get(DomainConstants.SELECT_ID);
			String strClaimRev   = (String) eachClaimMap.get(DomainConstants.SELECT_REVISION);
			String strFirstRev   = (String) eachClaimMap.get("first");
			//String strClaimExT   = (String) eachClaimMap.get("attribute["+ATTRIBUTE_PGEXECUTIONTYPE+"]");
			String strClaimExT   =checkForStringList(eachClaimMap,ATTRIBUTE_PGEXECUTIONTYPE);
			String strClaimCET   = (String) eachClaimMap.get("attribute["+ATTRIBUTE_PGCOPYELEMENTTYPE+"]");
			String pgClaimGraphicalImage = (String) eachClaimMap.get("attribute["+ATTRIBUTE_PGCLAIMGRAPHICALIMAGE+"]");
			String strClaimNamee = (String) eachClaimMap.get(DomainConstants.SELECT_NAME);
			//RTA 22x Added for ALM-49635 starts
			String strSequenceNumber=(String) eachClaimMap.get("attribute["+ATTRIBUTE_CLAIMSEQUENCE+"]");
			//RTA 22x Added for ALM-49635 ends
			//RTA 22x Added for ALM-46415 starts
			String strExpirationDate=(String) eachClaimMap.get("attribute["+ATTRIBUTE_PGEXPIRATIONDATE+"]");
			//RTA 22x Added for ALM-46415 ends
			String strGraphicRev = (String) eachClaimMap.get("FROMGRAPHIC");
			
			boolean hasClaimConnectedToMC =false;
			if(eachClaimMap.containsKey("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.name"))
				hasClaimConnectedToMC = true;
			
			String strGraphicType = PartMgtKernel.printType(context, STR_MASTER_GRAPHIC_TYPE, "derivative");
			StringList graphicTypes = FrameworkUtil.split(strGraphicType,"|");
			if(strClaimExT!=null && strClaimExT.contains("Packaging Artwork")) {
				if((strClaimRev!=null && strClaimRev.equals(strFirstRev) && !hasClaimConnectedToMC) || (strGraphicRev!=null && strGraphicRev.equals("FROMGRAPHIC"))) {
					// Read base64 data from Claim Object and generate a file(image) and save it in enovia workspace folder.
					Map imageDataMap = getGraphicalImageData(context,pgClaimGraphicalImage,strClaimNamee,strClaimRev);
					
					if(!graphicTypes.contains(strClaimCET)) 
						strClaimCET = "Icon With Variable Text Master Graphic";
					
					String strClaimCET_ = strClaimCET.replace("Master Graphic","").trim();
					
					StringBuilder disPlatTxt = new StringBuilder();
					disPlatTxt.append(STRDCM);
					disPlatTxt.append(STRTILDE);
					disPlatTxt.append(strClaimCET_);				
					disPlatTxt.append(STRTILDE);
					disPlatTxt.append(strClaimNamee);	
					//api
					Map<String, String> imageElementData1 = new HashMap<String, String>();
					imageElementData1.put(DomainConstants.SELECT_TYPE,strClaimCET);
					imageElementData1.put(DomainConstants.SELECT_DESCRIPTION,strClaimCET);
					imageElementData1.put(AWLAttribute.MARKETING_NAME.get(context), disPlatTxt.toString());
					imageElementData1.put("format.file.name",imageDataMap.get("fileName").toString());            
					imageElementData1.put("mcsUrl", DomainConstants.EMPTY_STRING);
					imageElementData1.put(DomainConstants.DOCUMENT,"Symbol");
					
					List<Country> countries = new ArrayList<Country>();
					ArtworkMaster dobImagekMaster = ArtworkMaster.createMasterImageElement(context,strClaimCET, imageElementData1,null,countries);
					
					GraphicsElement graphicsElement = (GraphicsElement)dobImagekMaster.getBaseArtworkElement(context);
					String strGraphicDocumentId=graphicsElement.getGraphicDocument(context).getObjectId(context);
			        
					//Connect MGE to CL// Connect CopyList to MCE.	
					//ArtworkMaster MCEObject_ = new ArtworkMaster(dobImagekMaster.getId());												
					setPrimaryOwnership(context, dobImagekMaster.getId());
					setPrimaryOwnership(context, dobImagekMaster.getImageHolderId(context));
					setPrimaryOwnership(context, graphicsElement.getImageHolderId(context));
					setPrimaryOwnership(context, new GraphicDocument(strGraphicDocumentId).getImageHolderId(context));
					
					CopyList copyList = new CopyList(copyListId);//ID of COPY LIST
					copyList.addArtworkMaster(context, dobImagekMaster);
					//RTA 22x Added for ALM-49635 starts
					setSequenceNumberOnCopyList(context,copyListId,dobImagekMaster.getId(),false,strSequenceNumber);
					//RTA 22x Added for ALM-49635 ends
					//RTA 22x Added for ALM-46415 starts
					dobImagekMaster.setAttributeValue(context, ATTRIBUTE_VALIDATE_DATE, strExpirationDate);	
					//RTA 22x Added for ALM-46415 ends
					//Connect MGE to Claim
					// RTA user does not have access on Claim Data
					ContextUtil.pushContext(context);
					callConnect(context,strClaimOID,RELATIONSHIP_PGMASTERCOPYCLAIM,dobImagekMaster.getId(),null);
					LOGGER.log(Level.INFO, " ##RTADCM## createGraphicElement - Connected to claim");
					ContextUtil.popContext(context);
						
					Map tmpAtt = new HashMap();
					tmpAtt.put(ATTRIBUTE_PLACEOFORIGIN, STR_YES);
								
					//Connect MGE to Categories
					connectMCTOCategories(context,plCategories,dobImagekMaster,tmpAtt);
				
					LOGGER.log(Level.INFO, " ##RTADCM## createGraphicElement - Master Graphic is connected in CDM Admin Structure "+plCategories);
					//TBD - Promote MGE to release - 
					graphicsElement.setState(context, AWLState.RELEASE.get(context,AWLPolicy.ARTWORK_ELEMENT_CONTENT));
					LOGGER.log(Level.INFO, " ##RTADCM## createGraphicElement - Graphic is Released");
				}
				else if(strClaimRev!=null && strClaimRev.equals(strFirstRev) && hasClaimConnectedToMC) {
					String strGraphicMCEId = (String) eachClaimMap.get("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id");
					ArtworkMaster artworkMaster = new ArtworkMaster(strGraphicMCEId);
					CopyList copyList = new CopyList(copyListId);//ID of COPY LIST
					copyList.addArtworkMaster(context, artworkMaster);
					//RTA 22x Added for ALM-49635 starts
					setSequenceNumberOnCopyList(context,copyListId,strGraphicMCEId,false,strSequenceNumber);
					//RTA 22x Added for ALM-49635 ends
					//RTA 22x Added for ALM-46415 starts
					artworkMaster.setAttributeValue(context, ATTRIBUTE_VALIDATE_DATE, strExpirationDate);	
					//RTA 22x Added for ALM-46415 ends
					LOGGER.log(Level.INFO, " ##RTADCM##Graphic Creation Existing MCE Use Case #2. {0}", strGraphicMCEId);
				}
				else {
					LOGGER.log(Level.INFO, " ##RTADCM##Graphic Creation Else Unknows Loop. Use case #3 ");
				}
				
			}	
		   else {
				LOGGER.log(Level.INFO, " ##RTADCM## createGraphicElement - No Packaging Artwork Element.. ");
			}
		}catch(Exception e){
			LOGGER.log(Level.SEVERE, e.toString());
			LOGGER.log(Level.SEVERE, e.getMessage());
			throw e;
		}
		LOGGER.log(Level.INFO, "createGraphicElement - END");
	}
	
	public static void connectMCTOCategories(Context context,List plCategories,ArtworkMaster MCEObject_,Map tmpAtt)throws Exception {
		try {
			StringList slProcessed = new StringList();

			for(int ct=0;ct<plCategories.size();ct++){
				String catID = (String) plCategories.get(ct);
				if(BusinessUtil.isNotNullOrEmpty(catID) && !slProcessed.contains(catID)) {
					callConnect(context,catID,AWLRel.ARTWORK_MASTER.get(context),MCEObject_.getId(),tmpAtt);
					slProcessed.add(catID);
				}							
			}
		}catch(Exception e){
			throw e;
		}
	}

	public Map getGraphicalImageData(Context context,String pgClaimGraphicalImage, String strClaimNamee, String strClaimRev) throws Exception {
		LOGGER.log(Level.INFO, "getGraphicalImageData - START");
		Map ImageDataMap = new HashMap();
		if (pgClaimGraphicalImage!=null && pgClaimGraphicalImage.contains(",")) {
			String base64StringFirstPart  = pgClaimGraphicalImage.split(",")[0];
			String base64StringSecondPart = pgClaimGraphicalImage.split(",")[1];
			String imageBase64 = "";
			if(base64StringSecondPart.contains("'")) {
				imageBase64 = base64StringSecondPart.split("'")[0];
			} else {
				imageBase64 = base64StringSecondPart.split("\"")[0];
			}
			byte[] data = new byte[0];							
			data = Base64.getDecoder().decode(imageBase64);
			String base64StringFirstPart_ = base64StringFirstPart.split(":")[1];
			
			int extentionStartIndex = base64StringFirstPart_.indexOf('/');
			int extensionEndIndex   = base64StringFirstPart_.indexOf(';');
			int filetypeStartIndex  = base64StringFirstPart_.indexOf(':');
			String fileType         = base64StringFirstPart_.substring(filetypeStartIndex + 1, extentionStartIndex);
			String fileExtension    = base64StringFirstPart_.substring(extentionStartIndex + 1, extensionEndIndex);
			
			LOGGER.log(Level.INFO,"##RTADCM## Graphic Element - fileExtension {0}", fileExtension);
		
			String claimName = strClaimNamee+"_"+strClaimRev+"_"+System.currentTimeMillis();
			
			String serverFolder = Environment.getValue(context, "MATRIXINSTALL");
			String strPath = serverFolder+File.separator+context.getWorkspacePath();
		
			String path = strPath+"/"+claimName+"."+fileExtension;
			ImageDataMap.put("filePath",path);
			ImageDataMap.put("fileName",claimName+"."+fileExtension);
			
			LOGGER.log(Level.INFO,"##RTADCM## Graphic Element - File location-> {0} ", ImageDataMap);
			
			File file = new File(path);
			try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
				outputStream.write(data);
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, e.toString());
				LOGGER.log(Level.SEVERE, e.getMessage());
				throw e;
			}
			LOGGER.log(Level.INFO, "getGraphicalImageData - END");
		}
		return ImageDataMap;
	}
	
	public static Map<String, StringList> getArtworkUsageRanges(Context context, String[] args) throws FrameworkException {
		LOGGER.log(Level.INFO, "getArtworkUsageRanges - START");
		HashMap<String, StringList> resultMap = new HashMap();

		try {

			String strWhere = DomainConstants.SELECT_CURRENT + "== \""+ PGPLIPACKCOMPONENTTYPE_STATE_ACTIVE +"\"";

			MapList mlOnlinePrintingPartList  = DomainObject.findObjects(
					context, //context the eMatrix <code>Context</code> object
					TYPE_PGPLIPACKCOMPONENTTYPE,  //typePattern The query type pattern.
					DomainConstants.QUERY_WILDCARD, //namePattern The query name pattern.
					DomainConstants.QUERY_WILDCARD, //revPattern The query revision pattern.
					DomainConstants.QUERY_WILDCARD, //ownerPattern The query owner pattern.
					ESERPROD, //vaultPattern The query vault pattern.
					strWhere, //whereExpression The query where expression.
					false, //expandType true, if the query should find subtypes of the given types.
					new StringList(DomainConstants.SELECT_NAME)); //objectSelects the eMatrix <code>StringList</code> object that holds the list of query select clause.

			if(!mlOnlinePrintingPartList.isEmpty())
			{
				StringList valueranges = BusinessUtil.toStringList(mlOnlinePrintingPartList, DomainConstants.SELECT_NAME);
				valueranges.sort();
				resultMap.put(AWLConstants.RANGE_FIELD_CHOICES, valueranges);
				resultMap.put(AWLConstants.RANGE_FIELD_DISPLAY_CHOICES, valueranges);
			}
			LOGGER.log(Level.INFO, "getArtworkUsageRanges - END");
			return resultMap;

		} catch (Exception e){ throw new FrameworkException(e);	}

	}

	public void reviseGraphicElement(Context context, String strMCId,Map eachClaimMap,List plCategories,String copyListId, String strCRId, Map crInfoMap) throws Exception {
		LOGGER.log(Level.INFO, "reviseGraphicElement - START");
		boolean bPushContext = false;
		try {
			
		if(BusinessUtil.isNotNullOrEmpty(strMCId) && BusinessUtil.isKindOf(context,strMCId, AWLType.MASTER_ARTWORK_GRAPHIC_ELEMENT.get(context))){
			

			DomainObject domoldMaster=DomainObject.newInstance(context,strMCId);
			String strNewMasterId = domoldMaster.reviseObject(context, true).getObjectId(context);
			ArtworkMaster artworkMaster = new ArtworkMaster(strNewMasterId);
			GraphicsElement graphicsElement = (GraphicsElement)artworkMaster.getBaseArtworkElement(context);
	        String strGraphicDocumentId=graphicsElement.getGraphicDocument(context).getObjectId(context);
	        String strGraphicElementId=graphicsElement.getObjectId();
	        String strClaimOID=(String)eachClaimMap.get(DomainConstants.SELECT_ID);
	        String strClaimNamee=(String)eachClaimMap.get(DomainConstants.SELECT_NAME);
	        String strClaimRev=(String)eachClaimMap.get(DomainConstants.SELECT_REVISION);
	        String strClaimGraphicalImage=(String)eachClaimMap.get("attribute["+ATTRIBUTE_PGCLAIMGRAPHICALIMAGE+"]");
	        //RTA 22x Added for ALM-49635 starts
	        String strSequenceNumber=(String) eachClaimMap.get("attribute["+ATTRIBUTE_CLAIMSEQUENCE+"]");
	        //RTA 22x Added for ALM-49635 ends
	        //RTA 22x Added for ALM-46415 starts
	        String strExpirationDate=(String) eachClaimMap.get("attribute["+ATTRIBUTE_PGEXPIRATIONDATE+"]");
	        //RTA 22x Added for ALM-46415 ends
	        Map imageDataMap = getGraphicalImageData(context,strClaimGraphicalImage,strClaimNamee,strClaimRev);
	        String strFileName=imageDataMap.get("fileName").toString();   
			String str1 = AWLPropertyUtil.getConfigPropertyString(context, "emxAWL.ImageDocument.Policies");

			setPrimaryOwnership(context, strNewMasterId);
			
			if (!BusinessUtil.isNullOrEmpty(strFileName)) {
		        HashMap<Object, Object> hashMap = new HashMap<>();
		        hashMap.put("fcsEnabled", "false");
		        hashMap.put("objectId", strGraphicDocumentId);
		        hashMap.put("parentId", strGraphicElementId);
		        hashMap.put("append", "false");
		        hashMap.put("unlock", "true");
		        hashMap.put("type", TYPE_SYMBOL);
		        hashMap.put("policy", str1);
		        hashMap.put("parentRelName", AWLRel.GRAPHIC_DOCUMENT.toString());
		        hashMap.put("fileName0", strFileName);
		        hashMap.put(CommonDocument.SELECT_TITLE, "testing");
		        hashMap.put("noOfFiles", "1");
		        hashMap.put("objectAction", "checkin");
		        hashMap.put("mcsUrl", "");
		        hashMap.put("attributeMap", new HashMap<>());
		        String[] arrayOfString = JPO.packArgs(hashMap);
		        Map<String, String> map1 = (Map)JPO.invoke(context, "emxCommonDocument", null, "commonDocumentCheckin", arrayOfString, Map.class);
		        //Connect MGE to CL// Connect CopyList to MCE.													
				CopyList copyList = new CopyList(copyListId);//ID of COPY LIST
				copyList.addArtworkMaster(context, artworkMaster);
				//RTA 22x Added for ALM-49635 starts
				setSequenceNumberOnCopyList(context,copyListId,strNewMasterId,false,strSequenceNumber);	
				//RTA 22x Added for ALM-49635 ends
				//RTA 22x Added for ALM-46415 starts
				artworkMaster.setAttributeValue(context, ATTRIBUTE_VALIDATE_DATE, strExpirationDate);
				//RTA 22x Added for ALM-46415 ends
				//Connect MGE to Claim
				// RTA user does not have access on Claim Data
				ContextUtil.pushContext(context);
				bPushContext = true;
				callConnect(context,strClaimOID,RELATIONSHIP_PGMASTERCOPYCLAIM,strNewMasterId,null);
				LOGGER.log(Level.INFO, " ##RTADCM## reviseGraphicElement - Connected to claim");
				if(bPushContext) {
					ContextUtil.popContext(context);
					bPushContext = false;
				}
				
				Map tmpAtt = new HashMap();
				tmpAtt.put(ATTRIBUTE_PLACEOFORIGIN, STR_YES);
							
				//Connect MGE to Categories, No need as on revision OOTB Takes care of disconnect old rev and connect new rev
				//connectMCTOCategories(context,plCategories,artworkMaster,tmpAtt);
			
				LOGGER.log(Level.INFO, " ##RTADCM## reviseGraphicElement - Master Graphic is connected in CDM Admin Structure "+plCategories);
		        graphicsElement.setState(context, AWLState.RELEASE.get(context,AWLPolicy.ARTWORK_ELEMENT_CONTENT));
		      }
			}
		}catch(Exception e){
			throw e;
		}
		finally {
			if(bPushContext) {
				ContextUtil.popContext(context);
				bPushContext = false;
			}
		}
		LOGGER.log(Level.INFO, "reviseGraphicElement - END");
	}

	public void errorLogNoPackging(Map selectMap, PrintWriter writer)throws Exception {				
		LOGGER.log(Level.INFO, "##RTADCM## ACE contact is empty..  so not processing further...");
		StringBuilder sb = new StringBuilder();
		sb.append(selectMap.get(DomainConstants.SELECT_NAME));
		sb.append(',');
		sb.append(selectMap.get(DomainConstants.SELECT_ID));
		sb.append(',');
		sb.append(selectMap.get(DomainConstants.SELECT_REVISION));
		sb.append(',');
		sb.append("FAIL");
		sb.append(',');
		sb.append(STR_NO_CLAIM_MSG);
		sb.append('\n');	
		writer.write(sb.toString());
	}

	public void errorLogNoRole(Map selectMap, PrintWriter writer)throws Exception {	
		StringBuilder sb = new StringBuilder();
		sb.append(selectMap.get(DomainConstants.SELECT_NAME));
		sb.append(',');
		sb.append(selectMap.get(DomainConstants.SELECT_ID));
		sb.append(',');
		sb.append(selectMap.get(DomainConstants.SELECT_REVISION));
		sb.append(',');
		sb.append("FAIL");
		sb.append(',');
		sb.append(STR_NO_ARTWORK_USER_ROLE_MSG);
		sb.append('\n');		
		writer.write(sb.toString());
	}
	
	public String  ObsoleteRTAData(Context context, String strCRId) throws Exception {
		LOGGER.log(Level.INFO, "ObsoleteRTAData - START");
		String crACE         = "";
		String crACE1        = "";
		StringList CLIDS     = null;
		StringList MCEIds    = new StringList();
		StringList DISMCEIds=null;
		boolean isContextPushed = false;
		
		StringList crSelects = new StringList();
		crSelects.add("attribute["+ATTRIBUTE_PGCLAIMACECONTACT+"]");
		crSelects.add(DomainConstants.SELECT_NAME);
		crSelects.add(DomainConstants.SELECT_REVISION);
		crSelects.add("last");
		crSelects.add("next");
		StringList crSelectsMulti = new StringList();
		crSelectsMulti.add("from["+REL_PGCOPYLIST+"].to.id");
		crSelectsMulti.add("from["+REL_PGCLAIMS+"].to.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id");
		//DCM:US-4383 RTA DCM Integration Code changes as per New data Model Changes  - Start
		crSelectsMulti.add("from["+RELATIONSHIP_PGDISCLAIMER+"].to.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id");
		//DCM:US-4383 RTA DCM Integration Code changes as per New data Model Changes  - End
		
	
		try {
			DomainObject doCR    = DomainObject.newInstance(context,strCRId);

			// RTA USER Does not have access on Claims data
			ContextUtil.pushContext(context);
			isContextPushed = true;

			LOGGER.log(Level.INFO, "##RTADCM## ObsoleteRTAData - user->>>> {0}", context.getUser());
			Map crInfoMap        = doCR.getInfo(context,crSelects);		
			LOGGER.log(Level.INFO, "##RTADCM## ObsoleteRTAData - CR ID-Map data->>>> {0}", crInfoMap);
			

			if(crInfoMap!=null) {
				//crACE      = (String) crInfoMap.get("attribute["+ATTRIBUTE_PGCLAIMACECONTACT+"]");
				crACE  = checkForStringList(crInfoMap,ATTRIBUTE_PGCLAIMACECONTACT);
				
				String crNextRev = "";
				String crlastRev = "";
				
				String crRev = (String) crInfoMap.get(DomainConstants.SELECT_REVISION);
				if(crInfoMap.containsKey("next"))
					crNextRev = (String) crInfoMap.get("next");
				if(crInfoMap.containsKey("last"))
					crlastRev = (String) crInfoMap.get("last");
				LOGGER.log(Level.INFO, "##RTADCM## ObsoleteRTAData - CR crlastRev ->>>> {0}", crlastRev);
				LOGGER.log(Level.INFO, "##RTADCM## ObsoleteRTAData - CR crRev ->>>> {0}", crRev);
				LOGGER.log(Level.INFO, "##RTADCM## ObsoleteRTAData - CR crACE ->>>> {0}", crACE);
				
				if(crRev.equals(crlastRev)){
					if(crACE!=null && !crACE.equals("")) {
						 crACE1      = crACE.split(",")[0];			
						LOGGER.log(Level.INFO, "##RTADCM## ObsoleteRTAData - CR crACE1 ->>>> {0}", crACE1);
						 // RTA USER Does not have access on Claims data				
						Map crCLMap  = doCR.getInfo(context,crSelectsMulti,crSelectsMulti);					

						LOGGER.log(Level.INFO, "##RTADCM## ObsoleteRTAData - crCLMap "+crCLMap);		

						if(crCLMap.containsKey("from["+REL_PGCOPYLIST+"].to.id")) {
							CLIDS  = (StringList) crCLMap.get("from["+REL_PGCOPYLIST+"].to.id");
							LOGGER.log(Level.INFO, "##RTADCM## ObsoleteRTAData - CR CLIDS ->>>> {0}", CLIDS);		
							for(int i=0;i<CLIDS.size();i++) {
								LOGGER.log(Level.INFO, "##RTADCM## ObsoleteRTAData - CR CLIDS.get(i) ->>>> {0}", CLIDS.get(i));		
								// Obsolete CL
								obsoleteCopyList(context, CLIDS.get(i), true);		
								LOGGER.log(Level.INFO, "##RTADCM## OBS CL {0}", CLIDS.get(i));						
							}					
						}

						if(crCLMap.containsKey("from["+REL_PGCLAIMS+"].to.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id")) {
							MCEIds = (StringList) crCLMap.get("from["+REL_PGCLAIMS+"].to.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id");	
							//StringList slPOAConnected = BusinessUtil.getInfo(context, MCEIds, "to[POA Artwork Master]");
						}
						//DCM:US-4383 RTA DCM Integration Code changes as per New data Model Changes  - Start
						if(crCLMap.containsKey("from["+RELATIONSHIP_PGDISCLAIMER+"].to.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id")) {
							DISMCEIds = (StringList) crCLMap.get("from["+RELATIONSHIP_PGDISCLAIMER+"].to.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id");	
							MCEIds.addAll(DISMCEIds);
						}
						//DCM:US-4383 RTA DCM Integration Code changes as per New data Model Changes  - End
						for(int j=0;j<MCEIds.size();j++) {
							LOGGER.log(Level.INFO, "##RTADCM## OBS MCE MCEIds {0}", MCEIds.get(j));
							String strMessage = JPO.invoke(context, "pgRTA_Util", null, "promoteCEToObsolete", new String[]{MCEIds.get(j)}, String.class);
							LOGGER.log(Level.INFO, "##RTADCM## OBS strMessage {0}", strMessage);
						}
						
					} else {
						
						LOGGER.log(Level.INFO, "##RTADCM## ACE CONTACT VALUE IS EMPTY - Nothing to process "+crInfoMap);
					}
				} else {
					LOGGER.log(Level.INFO, "##RTADCM## ObsoleteRTAData - This is not the last revision.. so nothing to process.. "+crInfoMap);
				}			
			} else {
				LOGGER.log(Level.INFO, "##RTADCM## ObsoleteRTAData - Nothing to process "+crInfoMap);
			}
		} catch(Exception e) {
			LOGGER.log(Level.SEVERE, e.toString());
			LOGGER.log(Level.SEVERE, e.getMessage());
			throw e;
		} finally {
			if(isContextPushed)
				ContextUtil.popContext(context);
		}
		LOGGER.log(Level.INFO, "ObsoleteRTAData - END");
		return STR_SUCCESS;
	}
	
	public boolean compareImageData(Context context,String strClaimGraphicalImage,String strPrevClaimGraphicalImage)throws Exception {	
		LOGGER.log(Level.INFO, "compareImageData - START");
		try {
		String strLatestFileData = "";
		String strPrevFileData="";
		if (strClaimGraphicalImage!=null && strClaimGraphicalImage.contains(",")) {
			//String strClaimGraphicalImageFirstPart  = strClaimGraphicalImage.split(",")[0];
			String strClaimGraphicalImageSecondPart = strClaimGraphicalImage.split(",")[1];
			if(strClaimGraphicalImageSecondPart.contains("'")) {
				strLatestFileData = strClaimGraphicalImageSecondPart.split("'")[0];
			} else {
				strLatestFileData = strClaimGraphicalImageSecondPart.split("\"")[0];
			}
		}
		if (strPrevClaimGraphicalImage!=null && strPrevClaimGraphicalImage.contains(",")) {
			//String strPrevClaimGraphicalImageFirstPart  = strPrevClaimGraphicalImage.split(",")[0];
			String strPrevClaimGraphicalImageSecondPart = strPrevClaimGraphicalImage.split(",")[1];
			if(strPrevClaimGraphicalImageSecondPart.contains("'")) {
				strPrevFileData = strPrevClaimGraphicalImageSecondPart.split("'")[0];
			} else {
				strPrevFileData = strPrevClaimGraphicalImageSecondPart.split("\"")[0];
			}
		}
		LOGGER.log(Level.INFO, "compareImageData - END");
		if(!strLatestFileData.equals(strPrevFileData)) {
			return true;
		}else {
			return false;
		}
		}catch(Exception e){
			throw e;
		}
	}
	public void syncMCfromCLwithCPG(Context context,String copyListId,String catID,String strACEUser)throws Exception{
		LOGGER.log(Level.INFO, "syncMCfromCLwithCPG - START");
		boolean bPushContext = false;
		try {
		if(BusinessUtil.isNotNullOrEmpty(catID)&&BusinessUtil.isNotNullOrEmpty(copyListId)) {
			Map tmpAtt = new HashMap();
			tmpAtt.put(ATTRIBUTE_PLACEOFORIGIN,STR_YES);
			CopyList copyList=new CopyList(copyListId);
			DomainObject domPrd=DomainObject.newInstance(context, catID);
			MapList masterCopyList = copyList.getArtworkMasters(context, new StringList(DomainObject.SELECT_ID),null, AWLConstants.EMPTY_STRING);
			MapList masterCopyListPrd = domPrd.getRelatedObjects(context, AWLRel.ARTWORK_MASTER.get(context), AWLType.MASTER_ARTWORK_ELEMENT.get(context), new StringList(DomainConstants.SELECT_ID), new StringList(DomainConstants.SELECT_RELATIONSHIP_ID), false, true, (short)1,null, DomainConstants.EMPTY_STRING, 0);
			StringList slMCFromCL = BusinessUtil.toStringList(masterCopyList, DomainConstants.SELECT_ID);
			StringList slMCFromPROD = BusinessUtil.toStringList(masterCopyListPrd, DomainConstants.SELECT_ID);
			StringList slMCRelIdFromPROD = BusinessUtil.toStringList(masterCopyListPrd, DomainConstants.SELECT_RELATIONSHIP_ID);
			String strMCId=null;
			String strRelId=null;
			//set owner on master copies connected to copylist
			// push context as Existing MCE belongs to other user. 
			ContextUtil.pushContext(context);
			bPushContext = true;
			for(String strCLMCId:slMCFromCL) {
				setObjectOwner(context,strCLMCId,strACEUser);
			}

			if(bPushContext) {
				ContextUtil.popContext(context);
				bPushContext = false;
			}
		
			//disconnect Master copies from prod which are not related to copylist
			for(int i=0;i<slMCFromPROD.size();i++) {
				strMCId=slMCFromPROD.get(i);
				strRelId=slMCRelIdFromPROD.get(i);
				if(!slMCFromCL.contains(strMCId)) {
					callDisconnect(context, strRelId);
				}
			}
			for(int i=0;i<slMCFromCL.size();i++) {
				strMCId=slMCFromCL.get(i);
				if(!slMCFromPROD.contains(strMCId)) {
					callConnect(context,catID,AWLRel.ARTWORK_MASTER.get(context),strMCId,tmpAtt);
				}
			}
		}
		}catch(Exception e){
			throw e;
		}
		finally{
			if(bPushContext) {
				ContextUtil.popContext(context);
				bPushContext = false;
			}
		}
		LOGGER.log(Level.INFO, "syncMCfromCLwithCPG - END");
	}
	public static void setObjectOwner(Context context,String strObjectId, String strUser)throws Exception{
		try {
			DomainObject domObject = DomainObject.newInstance(context,strObjectId);
			domObject.setOwner(context,strUser);
			domObject.setAttributeValue(context, "Originator", STR_INTEGRATION_USER);
		}catch(Exception e){
			throw e;
		}
	}
	public static void connectUnRevisedMCEtoRevisedClaimForArtworkPackaging(Context context,Map prevClaimInfoMap,String strClaimOID,String strCopyListId) throws Exception {
		boolean bPushContext = false;
		try {
		ContextUtil.pushContext(context);
		bPushContext = true;
		LOGGER.log(Level.INFO, "connectUnRevisedMCEtoRevisedClaimForArtworkPackaging - START");
		LOGGER.log(Level.INFO, "connectUnRevisedMCEtoRevisedClaimForArtworkPackaging - {0}", prevClaimInfoMap);
		if(prevClaimInfoMap.containsKey("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id")) {
			StringList tmp3ListMCEs = (StringList) prevClaimInfoMap.get("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id");
			for(int mceCtr = 0; mceCtr < tmp3ListMCEs.size(); mceCtr ++) {
				String mceID = tmp3ListMCEs.get(mceCtr);
				callConnect(context,strClaimOID,RELATIONSHIP_PGMASTERCOPYCLAIM,mceID,null);
				LOGGER.log(Level.INFO,mceID);
			}
		}
	
		//Disconnect prev ones
		if(prevClaimInfoMap.containsKey("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].id")) {
			LOGGER.log(Level.INFO, "Disconnecting Revisions from prev Claim - START");
			StringList tmp3ListMCERels = (StringList) prevClaimInfoMap.get("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].id");
			for(int mceCtr = 0; mceCtr < tmp3ListMCERels.size(); mceCtr ++) {
				String mcerelID = tmp3ListMCERels.get(mceCtr);
				callDisconnect(context,mcerelID);
				LOGGER.log(Level.INFO,mcerelID);
			}
			LOGGER.log(Level.INFO, "Disconnecting Revisions from prev Claim - END");
			
		}
		if(bPushContext) {
			ContextUtil.popContext(context);
			bPushContext = false;
		}
		
		//connect MC to CL 
		LOGGER.log(Level.INFO, "Connecting Master Copies to new CopyList - START");
		CopyList copyList = new CopyList(strCopyListId);//ID of COPY LIST
		if(prevClaimInfoMap.containsKey("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id")) {
			StringList slPrevClaimMCEs = (StringList) prevClaimInfoMap.get("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id");
			for(int mceCtr = 0; mceCtr < slPrevClaimMCEs.size(); mceCtr ++) {
				String mceID = slPrevClaimMCEs.get(mceCtr);
				copyList.addArtworkMaster(context, new ArtworkMaster(mceID));
				//callConnect(context,strCopyListId,REL_CLARTWORKMASTER,mceID,null);
				LOGGER.log(Level.INFO,mceID);
			}
		}
		LOGGER.log(Level.INFO, "Connecting Master Copies to new CopyList - END");
		LOGGER.log(Level.INFO, "connectUnRevisedMCEtoRevisedClaimForArtworkPackaging - END");
		}catch(Exception e) {
			LOGGER.log(Level.SEVERE, e.toString());
			LOGGER.log(Level.SEVERE, e.getMessage());
			throw e;
		}
		finally{
			if(bPushContext) {
				ContextUtil.popContext(context);
				bPushContext = false;
			}
		}
	}

	public static void connectUnRevisedMCEtoRevisedClaimForArtworkPackagingGraphic(Context context,Map prevClaimInfoMap,String strClaimOID,String strCopyListId) throws Exception {
		boolean bPushContext = false;
		try {
		ContextUtil.pushContext(context);
		bPushContext = true;
		LOGGER.log(Level.INFO, "connectUnRevisedMCEtoRevisedClaimForArtworkPackagingGraphic - START");
		if(prevClaimInfoMap.containsKey("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id")) {
			String mceID = (String) prevClaimInfoMap.get("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id");		
			callConnect(context,strClaimOID,RELATIONSHIP_PGMASTERCOPYCLAIM,mceID,null);
			LOGGER.log(Level.INFO,mceID);			
		}
	
		//Disconnect prev ones
		if(prevClaimInfoMap.containsKey("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].id")) {
			LOGGER.log(Level.INFO, "Disconnecting Revisions from prev Claim - START");
			String mcerelID = (String) prevClaimInfoMap.get("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].id");			
			
			callDisconnect(context,mcerelID);
			LOGGER.log(Level.INFO,mcerelID);			
			LOGGER.log(Level.INFO, "Disconnecting Revisions from prev Claim - END");			
		}

		if(bPushContext) {
			ContextUtil.popContext(context);
			bPushContext = false;
		}
		
		//connect MC to CL 
		LOGGER.log(Level.INFO, "Connecting Master Copies to new CopyList - START");
		CopyList copyList = new CopyList(strCopyListId);//ID of COPY LIST
		if(prevClaimInfoMap.containsKey("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id")) {
			String mceID = (String) prevClaimInfoMap.get("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id");
		
				copyList.addArtworkMaster(context, new ArtworkMaster(mceID));
				//callConnect(context,strCopyListId,REL_CLARTWORKMASTER,mceID,null);
				LOGGER.log(Level.INFO,mceID);
			
		}
		LOGGER.log(Level.INFO, "Connecting Master Copies to new CopyList - END");
		LOGGER.log(Level.INFO, "connectUnRevisedMCEtoRevisedClaimForArtworkPackaging - END");
		}catch(Exception e) {
			LOGGER.log(Level.SEVERE, e.toString());
			LOGGER.log(Level.SEVERE, e.getMessage());
			throw e;
		}
		finally{
			if(bPushContext) {
				ContextUtil.popContext(context);
				bPushContext = false;
			}
		}
	}
	public void processGraphicObject(Context context,Map prevClaimInfoMap,Map eachClaimMap,List plCategories,String copyListId,String strCRId,Map crInfoMap,String strClaimGraphicalImage)throws Exception {
		LOGGER.log(Level.INFO, "##RTADCM## prevClaimInfoMap {0}", prevClaimInfoMap);							
		String strPrevClaimId=null;
		String strClaimId=(String) eachClaimMap.get(DomainConstants.SELECT_ID);
		//RTA 22x Added for ALM-49635 starts
		String strSequenceNumber=(String) eachClaimMap.get("attribute["+ATTRIBUTE_CLAIMSEQUENCE+"]");
		//RTA 22x Added for ALM-49635 ends
		//RTA 22x Added for ALM-46415 starts
		String strExpirationDate=(String) eachClaimMap.get("attribute["+ATTRIBUTE_PGEXPIRATIONDATE+"]");
		//RTA 22x Added for ALM-46415 ends
		if(prevClaimInfoMap.containsKey("MCsFromOldClaim"))
		{
			Map mapRevInfo=(Map)prevClaimInfoMap.get("MCsFromOldClaim");
			LOGGER.log(Level.INFO, "##RTADCM## mapRevInfo {0}", mapRevInfo);

			if(mapRevInfo.containsKey("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id")) {
				strPrevClaimId=(String) mapRevInfo.get("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id");
				String strPrevClaimType=(String) mapRevInfo.get("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.type");
				prevClaimInfoMap.put("previous.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id",strPrevClaimId);
				prevClaimInfoMap.put("previous.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.type",strPrevClaimType);
			}

			String strPrevClaimGraphicalImageTemp = (String)mapRevInfo.get("attribute["+ATTRIBUTE_PGCLAIMGRAPHICALIMAGE+"]");							
			prevClaimInfoMap.put("previous.attribute["+ATTRIBUTE_PGCLAIMGRAPHICALIMAGE+"]", strPrevClaimGraphicalImageTemp);
			LOGGER.log(Level.INFO, "##RTADCM## strClaimGraphicalImage {0}", strClaimGraphicalImage);
			LOGGER.log(Level.INFO, "##RTADCM## strPrevClaimGraphicalImageTemp {0}", strPrevClaimGraphicalImageTemp);
			boolean isImageDataChangeTemp=compareImageData(context,strClaimGraphicalImage,strPrevClaimGraphicalImageTemp);
			LOGGER.log(Level.INFO, "##RTADCM## isImageDataChangeTemp {0}", isImageDataChangeTemp);

			if(isImageDataChangeTemp)
			{
				reviseGraphicElement(context,strPrevClaimId,eachClaimMap,plCategories,copyListId,strCRId,crInfoMap);
			}
			else if(!mapRevInfo.containsKey("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id"))
			{
				eachClaimMap.put("FROMGRAPHIC", "FROMGRAPHIC");
				createGraphicElement(context,eachClaimMap,plCategories,copyListId,strCRId,crInfoMap);
			}
			else 
			{
				connectUnRevisedMCEtoRevisedClaimForArtworkPackagingGraphic(context,mapRevInfo,strClaimId,copyListId);
				//RTA 22x Added for ALM-49635 starts
				setSequenceNumberForUnRevisedMCEGraphic(context,copyListId,mapRevInfo,false,strSequenceNumber);
				//RTA 22x Added for ALM-49635 ends
				//RTA 22x Added for ALM-46415 starts
				updateValidityDateForUnRevisedMCEGraphic(context,mapRevInfo,strExpirationDate);
				//RTA 22x Added for ALM-46415 ends
			}
		}

	}

	public List getUpdatedCategories(Context context, List plCategories, String strOldCLId) throws Exception
	{
		StringList sl = new StringList();
		List plCategoriesUpdated = new ArrayList();
		plCategoriesUpdated.addAll(plCategories);
		sl.add("to[" + RELATIONSHIP_ASSOCIATEDCOPYLIST + "].from.id");
		LOGGER.log(Level.INFO, "getUpdatedCategories - START {0}", plCategories);
		Map relIDDataFromCPG2CL = getconnectedData(context, strOldCLId, sl);
		
		StringList cpgOID = (StringList) relIDDataFromCPG2CL.get("to[" + RELATIONSHIP_ASSOCIATEDCOPYLIST + "].from.id");
		LOGGER.log(Level.INFO, "cpgOID - {0}", cpgOID);
					
		for(int j=0;j<cpgOID.size();j++)
		{
			if(!plCategoriesUpdated.contains((String) cpgOID.get(j)))
			{
				plCategoriesUpdated.add((String) cpgOID.get(j));
			}
		}

		LOGGER.log(Level.INFO, "plCategories - END {0}", plCategories);
		LOGGER.log(Level.INFO, "getUpdatedCategories plCategoriesUpdated - END {0}", plCategoriesUpdated);

		return plCategoriesUpdated;
	}

	public void disconnectMCEfromCPG(Context context, String catID)throws Exception{
		LOGGER.log(Level.INFO, "disconnectMCEfromCPG - START");
		try 
		{
			if(BusinessUtil.isNotNullOrEmpty(catID)) 
			{			
				DomainObject domPrd = DomainObject.newInstance(context, catID);		
				MapList masterCopyListPrd = domPrd.getRelatedObjects(context, AWLRel.ARTWORK_MASTER.get(context), AWLType.MASTER_ARTWORK_ELEMENT.get(context), new StringList(DomainConstants.SELECT_ID), new StringList(DomainConstants.SELECT_RELATIONSHIP_ID), false, true, (short)1,null, DomainConstants.EMPTY_STRING, 0);
				StringList slMCRelIdFromPROD = BusinessUtil.toStringList(masterCopyListPrd, DomainConstants.SELECT_RELATIONSHIP_ID);
				String strMCId=null;
				String strRelId=null;
			
				// disconnect Master copies from CPG
				for(int i=0;i<slMCRelIdFromPROD.size();i++) {
					strRelId=slMCRelIdFromPROD.get(i);	
					LOGGER.log(Level.INFO, "disconnectMCEfromCPG - strRelId disconnected {0}", strRelId);			
					callDisconnect(context, strRelId);				
				}
			}
		}
		catch(Exception e)
		{
			throw e;
		}
		LOGGER.log(Level.INFO, "disconnectMCEfromCPG - END");
	}
	public static Map getMapOfSpecialChars(Context context)throws Exception{
		Map mapChar=new HashMap();
		mapChar.put(STRTILDE, STRING_TILDE);
		mapChar.put(CONSTANT_STRING_STAR, STRING_STAR);
		mapChar.put(CONSTANT_STRING_HASH, STRING_HASH);
		mapChar.put(CONSTANT_STRING_DOLLAR, STRING_DOLLAR);
		mapChar.put(CONSTANT_STRING_CARET, STRING_CARET);
		mapChar.put(CONSTANT_STRING_AMPERSAND, STRING_AMPERSAND);
		mapChar.put(CONSTANT_STRING_EQUAL_SIGN, STRING_EQUAL_SIGN);
		mapChar.put(CONSTANT_STRING_PIPE, STRING_PIPE);
		mapChar.put(CONSTANT_STRING_SLASH, STRING_SLASH);
		mapChar.put(CONSTANT_STRING_PLUS, STRING_PLUS);
		mapChar.put(CONSTANT_STRING_LESSTHAN, STRING_LESSTHAN);
		mapChar.put(CONSTANT_STRING_GREATERTHAN, STRING_GREATERTHAN);
		mapChar.put(CONSTANT_STRING_NEGATE, STRING_NEGATE);
		mapChar.put(CONSTANT_STRING_COLON, STRING_COLON);
		mapChar.put(CONSTANT_STRING_SEMICOLON, STRING_SEMICOLON);
		mapChar.put(CONSTANT_STRING_DOUUBLEQUOTE, STRING_DOUUBLEQUOTE);
		mapChar.put(CONSTANT_STRING_SINGLE_QUOTE, STRING_SINGLE_QUOTE);
		mapChar.put(CONSTANT_STRING_COMMA, STRING_COMMA);
		mapChar.put(CONSTANT_STRING_QUEST, STRING_QUEST);
		mapChar.put(CONSTANT_STRING_PERCENT, STRING_PERCENT);
		mapChar.put(CONSTANT_STRING_BSLASH, STRING_BSLASH);
		mapChar.put(CONSTANT_STRING_AT, STRING_AT);
		mapChar.put(CONSTANT_STRING_OPENSQUAREBRACKET, STRING_SQUAREBRACKET);
		mapChar.put(CONSTANT_STRING_CLOSESQUAREBRACKET, STRING_SQUAREBRACKET);
		return mapChar;
	}
	public static String replaceChar(Context context, String strNewDisClaimNam, Map mapChar)throws Exception{
		Character cValue = null;
		String replaceValue="";
		for(int iIndex=0;iIndex<strNewDisClaimNam.length();iIndex++)
		{
			cValue = strNewDisClaimNam.charAt(iIndex);
			if(isSpecialCharacter(cValue) && mapChar.containsKey(cValue.toString())){
				replaceValue = (String)mapChar.get(cValue.toString());
				strNewDisClaimNam=strNewDisClaimNam.replace(cValue.toString(), replaceValue);
			}else if(isSpecialCharacter(cValue) && !mapChar.containsKey(cValue.toString()) && !(cValue.toString().equals(CONSTANT_STRING_OPEN_ROUND_BRACE)|| cValue.toString().equals(CONSTANT_STRING_CLOSE_ROUND_BRACE))) {
				strNewDisClaimNam=strNewDisClaimNam.replace(cValue.toString(), CONSTANT_STRING_SPACE);
			}
		}
		return strNewDisClaimNam;
	}
	public static boolean isSpecialCharacter(Character c) throws Exception {
		return c.toString().matches(SPECIAL_CHAR_MATCH_STRING);	
	}
	
	public void notifyToUserOnJOBStatus(Context context,boolean bError,String sBackgroundProcessObjectId) throws Exception 
	{
		LOGGER.log(Level.INFO, "notifyToUserOnJOBStatus - START");
		boolean isContextPushed = false;
		String strSuccessSubject = STR_SUBJECT_SUCCESS;
		String strFailureSubject = STR_SUBJECT_FAILURE;
		StringBuilder sbHTMLMessage = new StringBuilder();
		String strSubject = "";
			
		try {		
			if(BusinessUtil.isNotNullOrEmpty(sBackgroundProcessObjectId))
			{				
				DomainObject doBG = DomainObject.newInstance(context,sBackgroundProcessObjectId);
				StringList bgSelects = new StringList();
				bgSelects.add("attribute["+ATTRIBUTE_PGPARAMETERARG+"]");
				bgSelects.add(DomainConstants.SELECT_NAME);
				Map mapBGData = doBG.getInfo(context,bgSelects);
				String strAttCRIdState=(String) mapBGData.get("attribute["+ATTRIBUTE_PGPARAMETERARG+"]");
				String strBGJobName=(String) mapBGData.get(DomainConstants.SELECT_NAME);
				StringList slCRIdnState=FrameworkUtil.split(strAttCRIdState, "~");
				Map mapCLRInfo=null;
				String strACE=null;
				String strCopyListName=null;
				String strCLRName=null;
				StringList crSelects = new StringList();
				crSelects.add("attribute["+ATTRIBUTE_PGCLAIMACECONTACT+"]");
				crSelects.add(DomainConstants.SELECT_NAME);
				crSelects.add(DomainConstants.SELECT_REVISION);
				crSelects.add("from["+REL_PGCOPYLIST+"].to.id");
				crSelects.add("from["+REL_PGCOPYLIST+"].to.name");
				if(slCRIdnState.size()==2) {
					String strCRId=slCRIdnState.get(0);
					String strCRState=slCRIdnState.get(1);
					// RTA USER Does not have access on Claims data
					ContextUtil.pushContext(context);
					isContextPushed = true;
					mapCLRInfo = BusinessUtil.getInfo(context, strCRId, crSelects);
					//strACE=(String)mapCLRInfo.get("attribute["+ATTRIBUTE_PGCLAIMACECONTACT+"]");
					if(mapCLRInfo!=null) {
						strACE= checkForStringList(mapCLRInfo,ATTRIBUTE_PGCLAIMACECONTACT);
						strCopyListName=(String)mapCLRInfo.get("from["+REL_PGCOPYLIST+"].to.name");
						strCLRName=(String)mapCLRInfo.get(DomainConstants.SELECT_NAME);
					}
					if(BusinessUtil.isNullOrEmpty(strCopyListName)) {
						strCopyListName = "";
					} 
					
					if(bError) {
						strFailureSubject = strFailureSubject.replace("<CLRName>",strCLRName);
						strSubject = strFailureSubject;
					} else {
						strSuccessSubject = strSuccessSubject.replace("<CLName>",strCopyListName);
						strSuccessSubject = strSuccessSubject.replace("<CLRName>",strCLRName);
						strSubject = strSuccessSubject;
					}
					
					sbHTMLMessage.append("<table style = 'border:1px solid black;border-collapse:collapse;' cellpadding='5'><thead><tr>");
					sbHTMLMessage.append("<th style='border:1px solid;'> <center>Claim Request</center></th>");
					sbHTMLMessage.append("<th style='border:1px solid;'> <center>Claim Request State</center></th>");
					sbHTMLMessage.append("<th style='border:1px solid;'> <center>Copy List Name</center></th>");
					sbHTMLMessage.append("<th style='border:1px solid;'> <center>ACE Contact</center></th>");
					sbHTMLMessage.append("<th style='border:1px solid;'> <center>Background Job</center></th>");
					sbHTMLMessage.append("</tr></thead><tbody><tr><td style='border:1px solid;'>");
					sbHTMLMessage.append(strCLRName);
					sbHTMLMessage.append("</td><td style='border:1px solid;'>");
					sbHTMLMessage.append(strCRState);
					sbHTMLMessage.append("</td><td style='border:1px solid;'>");
					sbHTMLMessage.append(strCopyListName);
					sbHTMLMessage.append("</td><td style='border:1px solid;'>");
					sbHTMLMessage.append(strACE);
					sbHTMLMessage.append("</td><td style='border:1px solid;'>");
					sbHTMLMessage.append(strBGJobName);
					sbHTMLMessage.append("</td></tr></tbody></table>");
					Map mapArgs = new HashMap();
					mapArgs.put("subject", strSubject);
					mapArgs.put("message", sbHTMLMessage.toString());
					mapArgs.put("toList", strACE);
					JPO.invoke(context, "pgRTADCMUtil", null, "sendEmailNotification", JPO.packArgs(mapArgs));					
					LOGGER.log(Level.INFO, "notifyToUserOnJOBStatus - END");
				}
			}				
		}catch(Exception ex){
			throw new FrameworkException(ex); 
		}finally {
			if(isContextPushed) {
				ContextUtil.popContext(context);
				isContextPushed = false;
			}
		}
	}
	public void updateInstanceSequenceOnCopyListMasters(Context context,CopyList copyList)throws Exception  {
		LOGGER.log(Level.INFO, "updateInstanceSequenceOnCopyListMasters - START");
		String instanceSeqSelect = AWLAttribute.INSTANCE_SEQUENCE.getSel(context);
		StringList selectables = new StringList(instanceSeqSelect);
		StringList objsel=new StringList(DomainConstants.SELECT_ID);
		objsel.add(DomainConstants.SELECT_TYPE);
		MapList masterCopyList = copyList.getArtworkMasters(context, objsel,selectables, AWLConstants.EMPTY_STRING);
		masterCopyList.sort(DomainConstants.SELECT_TYPE, "ascending", "string");
		StringList slMasterTypes = BusinessUtil.toStringList(masterCopyList, DomainConstants.SELECT_TYPE);
		Map mapMaster=null;
		String strMasterId=null;
		String strMasterType=null;
		String strMasterTypeTemp=slMasterTypes.get(0);
		int j=0;
		ArtworkMaster am=null;
		for(int i=0; i<masterCopyList.size();i++){
			mapMaster=(Map)masterCopyList.get(i);
			strMasterId = (String) mapMaster.get(DomainConstants.SELECT_ID);
			strMasterType=(String) mapMaster.get(DomainConstants.SELECT_TYPE);
			am = new ArtworkMaster(strMasterId);
			if(!strMasterTypeTemp.equals(strMasterType)) {
				strMasterTypeTemp=strMasterType;
				j=1;
				copyList.setInstanceSequence(context, am, j);
			}else {
				copyList.setInstanceSequence(context, am, j+1);
				j++;
			}
		}
		LOGGER.log(Level.INFO, "updateInstanceSequenceOnCopyListMasters - END");
	}
	//RTA 22x Added for ALM-44252 starts
	public static void updateInactiveAuthorApprovers(Context context,String strMCId, String strACEContact) throws Exception {
		LOGGER.log(Level.INFO, "updateInactiveAuthorApprovers - START");
		try {
			String strACEContactID =  getACEContactObjectId(context,strACEContact);
			String SEL_AUTHOR_ID = AWLUtil.strcat("from[", AWLRel.ARTWORK_CONTENT_AUTHOR.get(context), "].to.id");
			String SEL_APPROVER_ID = AWLUtil.strcat("from[", AWLRel.ARTWORK_CONTENT_APPROVER.get(context), "].to.id");
			String SEL_AUTHOR_CURRENT = AWLUtil.strcat("from[", AWLRel.ARTWORK_CONTENT_AUTHOR.get(context), "].to.current");
			String SEL_APPROVER_CURRENT = AWLUtil.strcat("from[", AWLRel.ARTWORK_CONTENT_APPROVER.get(context), "].to.current");
			if(BusinessUtil.isNotNullOrEmpty(strMCId)) {
				ArtworkMaster artworkMaster = new ArtworkMaster(strMCId);
				MapList oldArts = artworkMaster.getArtworkElements(context, StringList.create(DomainConstants.SELECT_ID, SEL_AUTHOR_ID, SEL_APPROVER_ID,SEL_AUTHOR_CURRENT,SEL_APPROVER_CURRENT), null, null, true, true);
				String oldArtworkId=null;
				String authorTemplate=null;
				String approverTemplate=null;
				String authorTemplateState=null;
				String approverTemplateState=null;
				boolean isPersonInActive=false;
				for(Map eachOldArt: (List<Map>)oldArts) {
					oldArtworkId = (String) eachOldArt.get(DomainConstants.SELECT_ID);
					authorTemplate = (String) eachOldArt.get(SEL_AUTHOR_ID);
					approverTemplate = (String) eachOldArt.get(SEL_APPROVER_ID);
					authorTemplateState=(String) eachOldArt.get(SEL_AUTHOR_CURRENT);
					approverTemplateState=(String) eachOldArt.get(SEL_APPROVER_CURRENT);
					if(BusinessUtil.isNotNullOrEmpty(authorTemplate)&& BusinessUtil.isNotNullOrEmpty(approverTemplate)){
					if(BusinessUtil.isKindOf(context, authorTemplate, DomainConstants.TYPE_ROUTE_TEMPLATE) && !authorTemplateState.equalsIgnoreCase(DomainConstants.STATE_PERSON_ACTIVE)) {
						isPersonInActive=true;
					}else if(BusinessUtil.isKindOf(context, approverTemplate, DomainConstants.TYPE_ROUTE_TEMPLATE) && !approverTemplateState.equalsIgnoreCase(DomainConstants.STATE_PERSON_ACTIVE)){
						isPersonInActive=true;
					}else if(BusinessUtil.isKindOf(context, authorTemplate, DomainConstants.TYPE_ROUTE_TEMPLATE) && authorTemplateState.equalsIgnoreCase(DomainConstants.STATE_PERSON_ACTIVE)){
						isPersonInActive=checkIfRouteTemplateUserInactive(context,authorTemplate);
					}else if(BusinessUtil.isKindOf(context, approverTemplate, DomainConstants.TYPE_ROUTE_TEMPLATE) && approverTemplateState.equalsIgnoreCase(DomainConstants.STATE_PERSON_ACTIVE)){
						isPersonInActive=checkIfRouteTemplateUserInactive(context,approverTemplate);
					}else {
						if(!authorTemplateState.equalsIgnoreCase(DomainConstants.STATE_PERSON_ACTIVE)||!approverTemplateState.equalsIgnoreCase(DomainConstants.STATE_PERSON_ACTIVE)) {
							isPersonInActive=true;
						}
					}
					if(isPersonInActive){
						(new CopyElement(oldArtworkId)).updateAssignee(context, strACEContactID, strACEContactID);
					}
				}
				}
			}
			LOGGER.log(Level.INFO, "updateInactiveAuthorApprovers - END");	
		}catch(Exception e){
			throw e;
		}
	}
	public static boolean checkIfRouteTemplateUserInactive(Context context ,String strRouteTemplateId)throws Exception {
	LOGGER.log(Level.INFO, "checkIfRouteTemplateUserInactive - START");
	try {
		StringList slBusSelectable = new StringList();
		slBusSelectable.add(DomainConstants.SELECT_ID);
		slBusSelectable.add(DomainConstants.SELECT_TYPE);
		slBusSelectable.add(DomainConstants.SELECT_CURRENT);
		StringList slRelSelectable = new StringList();
		Map mapRouteTemDetails=null;
		String strPersonId=null;
		String strPersonCurrentState=null;
		DomainObject doRTObject = DomainObject.newInstance(context, strRouteTemplateId);
		MapList mlRTDetails = doRTObject.getRelatedObjects(context, DomainConstants.RELATIONSHIP_ROUTE_NODE, DomainConstants.TYPE_PERSON + "," + PropertyUtil.getSchemaProperty(context, "type_RouteTaskUser"), slBusSelectable, slRelSelectable, false, true, (short) 1, null, null, 0);
		for(int i=0;i<mlRTDetails.size();i++) {
			mapRouteTemDetails=(Map)mlRTDetails.get(i);
			strPersonId=(String)mapRouteTemDetails.get(DomainConstants.SELECT_ID);
			strPersonCurrentState=(String)mapRouteTemDetails.get(DomainConstants.SELECT_CURRENT);
			if(!strPersonCurrentState.equalsIgnoreCase(DomainConstants.STATE_PERSON_ACTIVE)) {
				return true;
			}
		}
		LOGGER.log(Level.INFO, "checkIfRouteTemplateUserInactive - END");
		}catch(Exception e){
			throw e;
		}
		return false;
	}
	//RTA 22x Added for ALM-44252 ends
	//RTA 22x Added for ALM-49635 starts
	public static void setSequenceNumberOnCopyList(Context context ,String copyListId,String strMasterId,boolean IsDisclaimer,String strSequenceNumber)throws Exception {
		try {
			LOGGER.log(Level.INFO, "setSequenceNumberOnCopyList - START");
			CopyList cl=new CopyList(copyListId);
			if(BusinessUtil.isNotNullOrEmpty(strMasterId)&& !IsDisclaimer) {
				cl.setSequenceNumber(context,strMasterId,Integer.valueOf(strSequenceNumber));
			}
			LOGGER.log(Level.INFO, "setSequenceNumberOnCopyList - END");
		}catch(Exception e){
			throw e;
		}
	}
	public static void setSequenceNumberForUnRevisedMCE(Context context,String copyListId,Map mapRevInfo,boolean IsDisclaimer,String strSequenceNumber)throws Exception {
		try {
			LOGGER.log(Level.INFO, "setSequenceNumberForUnRevisedMCE - START");
			String strMasterId =null;
			if(mapRevInfo.containsKey("previous.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id")) {
				StringList tmp3ListMCEs = (StringList) mapRevInfo.get("previous.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id");
				for(int mceCtr = 0; mceCtr < tmp3ListMCEs.size(); mceCtr ++) {
					strMasterId = tmp3ListMCEs.get(mceCtr);
				}
			}
			if(mapRevInfo.containsKey("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id")) {
				StringList tmp3ListMCEs = (StringList) mapRevInfo.get("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id");
				for(int mceCtr = 0; mceCtr < tmp3ListMCEs.size(); mceCtr ++) {
					strMasterId = tmp3ListMCEs.get(mceCtr);
				}
			}
			CopyList cl=new CopyList(copyListId);
			if(BusinessUtil.isNotNullOrEmpty(strMasterId)&& !IsDisclaimer) {
				cl.setSequenceNumber(context,strMasterId,Integer.valueOf(strSequenceNumber));
			}
			LOGGER.log(Level.INFO, "setSequenceNumberForUnRevisedMCE - END");
		}catch(Exception e){
			throw e;
		}
	}
	public static void setSequenceNumberForUnRevisedMCEGraphic(Context context,String copyListId,Map mapRevInfo,boolean IsDisclaimer,String strSequenceNumber)throws Exception {
		try {
			LOGGER.log(Level.INFO, "setSequenceNumberForUnRevisedMCEGraphic - START");
			String strMasterId =null;
			if(mapRevInfo.containsKey("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id")) {
				strMasterId = (String) mapRevInfo.get("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id");		
			}
			CopyList cl=new CopyList(copyListId);
			if(BusinessUtil.isNotNullOrEmpty(strMasterId)&& !IsDisclaimer) {
				cl.setSequenceNumber(context,strMasterId,Integer.valueOf(strSequenceNumber));
			}
			LOGGER.log(Level.INFO, "setSequenceNumberForUnRevisedMCEGraphic - END");
		}catch(Exception e){
			throw e;
		}
	}
	public static String checkForStringList(Map crInfoMap, String strAttributeName)throws Exception {
		LOGGER.log(Level.INFO, "checkForStringList - START");
		String strFinalValue="";
		StringList slValues=new StringList();
		try {
		if(crInfoMap!=null && strAttributeName.equals(ATTRIBUTE_PGINTENDEDMARKETS) && crInfoMap.containsKey("attribute["+ATTRIBUTE_PGINTENDEDMARKETS+"]")) {
			if(crInfoMap.get("attribute["+ATTRIBUTE_PGINTENDEDMARKETS+"]") instanceof StringList ){
				slValues = (StringList)crInfoMap.get("attribute["+ATTRIBUTE_PGINTENDEDMARKETS+"]");
				strFinalValue=FrameworkUtil.join(slValues, ",");
			}else {
				strFinalValue = (String)crInfoMap.get("attribute["+ATTRIBUTE_PGINTENDEDMARKETS+"]");
			}
		}else if(crInfoMap!=null && strAttributeName.equals(ATTRIBUTE_PGCATEGORY) && crInfoMap.containsKey("attribute["+ATTRIBUTE_PGCATEGORY+"]")) {
			if(crInfoMap.get("attribute["+ATTRIBUTE_PGCATEGORY+"]") instanceof StringList ){
				slValues = (StringList)crInfoMap.get("attribute["+ATTRIBUTE_PGCATEGORY+"]");
				strFinalValue=FrameworkUtil.join(slValues, ",");
			}else {
				strFinalValue = (String)crInfoMap.get("attribute["+ATTRIBUTE_PGCATEGORY+"]");
			}
		//RTA 22x Added for ALM-51831 starts
		}else if(crInfoMap!=null && strAttributeName.equals(ATTRIBUTE_PGBUSINESSAREA) && crInfoMap.containsKey("attribute["+ATTRIBUTE_PGBUSINESSAREA+"]")) {
			if(crInfoMap.get("attribute["+ATTRIBUTE_PGBUSINESSAREA+"]") instanceof StringList ){
				slValues = (StringList)crInfoMap.get("attribute["+ATTRIBUTE_PGBUSINESSAREA+"]");
				strFinalValue=FrameworkUtil.join(slValues, ",");
			}else {
				strFinalValue = (String)crInfoMap.get("attribute["+ATTRIBUTE_PGBUSINESSAREA+"]");
			}
		//RTA 22x Added for ALM-51831 ends
		}else if(crInfoMap!=null && strAttributeName.equals(ATTRIBUTE_REGION) && crInfoMap.containsKey("attribute["+ATTRIBUTE_REGION+"]")) {
			if(crInfoMap.get("attribute["+ATTRIBUTE_REGION+"]") instanceof StringList ){
				slValues = (StringList)crInfoMap.get("attribute["+ATTRIBUTE_REGION+"]");
				strFinalValue=FrameworkUtil.join(slValues, ",");
			}else {
				strFinalValue = (String)crInfoMap.get("attribute["+ATTRIBUTE_REGION+"]");
			}
		}else if(crInfoMap!=null && strAttributeName.equals(ATTRIBUTE_PGCLAIMACECONTACT) && crInfoMap.containsKey("attribute["+ATTRIBUTE_PGCLAIMACECONTACT+"]")) {
			if(crInfoMap.get("attribute["+ATTRIBUTE_PGCLAIMACECONTACT+"]") instanceof StringList ){
				slValues = (StringList)crInfoMap.get("attribute["+ATTRIBUTE_PGCLAIMACECONTACT+"]");
				strFinalValue=FrameworkUtil.join(slValues, ",");
			}else {
				strFinalValue = (String)crInfoMap.get("attribute["+ATTRIBUTE_PGCLAIMACECONTACT+"]");
			}
		}else if(crInfoMap!=null && strAttributeName.equals(ATTRIBUTE_PGEXECUTIONTYPE) && crInfoMap.containsKey("attribute["+ATTRIBUTE_PGEXECUTIONTYPE+"]")) {
			if(crInfoMap.get("attribute["+ATTRIBUTE_PGEXECUTIONTYPE+"]") instanceof StringList ){
				slValues = (StringList)crInfoMap.get("attribute["+ATTRIBUTE_PGEXECUTIONTYPE+"]");
				strFinalValue=FrameworkUtil.join(slValues, ",");
			}else {
				strFinalValue = (String)crInfoMap.get("attribute["+ATTRIBUTE_PGEXECUTIONTYPE+"]");
			}
		}else if(crInfoMap!=null && strAttributeName.equals(ATTRIBUTE_PGPACKCOMPONENTTYPE) && crInfoMap.containsKey("attribute["+ATTRIBUTE_PGPACKCOMPONENTTYPE+"]")) {
			if(crInfoMap.get("attribute["+ATTRIBUTE_PGPACKCOMPONENTTYPE+"]") instanceof StringList ){
				slValues = (StringList)crInfoMap.get("attribute["+ATTRIBUTE_PGPACKCOMPONENTTYPE+"]");
				strFinalValue=FrameworkUtil.join(slValues, ",");
			}else {
				strFinalValue = (String)crInfoMap.get("attribute["+ATTRIBUTE_PGPACKCOMPONENTTYPE+"]");
			}
		}
		LOGGER.log(Level.INFO, "checkForStringList - END");
		}catch(Exception e){
			throw e;
		}
		return strFinalValue;
	}
	//RTA 22x Added for ALM-49635 ends
	//RTA 22x Added for ALM-46415 starts
	public static void updateValidityDateForUnRevisedMCE(Context context,Map mapRevInfo,String strExpirationDate)throws Exception {
		try {
			LOGGER.log(Level.INFO, "updateValidityDateForUnRevisedMCE - START");
			String strMasterId =null;
			if(mapRevInfo.containsKey("previous.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id")) {
				StringList tmp3ListMCEs = (StringList) mapRevInfo.get("previous.from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id");
				for(int mceCtr = 0; mceCtr < tmp3ListMCEs.size(); mceCtr ++) {
					strMasterId = tmp3ListMCEs.get(mceCtr);
				}
			}
			if(mapRevInfo.containsKey("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id")) {
				StringList tmp3ListMCEs = (StringList) mapRevInfo.get("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id");
				for(int mceCtr = 0; mceCtr < tmp3ListMCEs.size(); mceCtr ++) {
					strMasterId = tmp3ListMCEs.get(mceCtr);
				}
			}
			if(BusinessUtil.isNotNullOrEmpty(strMasterId)) {
				ArtworkMaster am=new ArtworkMaster(strMasterId);
				am.setAttributeValue(context, ATTRIBUTE_VALIDATE_DATE, strExpirationDate);
			}
			LOGGER.log(Level.INFO, "updateValidityDateForUnRevisedMCE - END");
		}catch(Exception e){
			throw e;
		}
	}
	public static void updateValidityDateForUnRevisedMCEGraphic(Context context,Map mapRevInfo,String strExpirationDate)throws Exception {
		try {
			LOGGER.log(Level.INFO, "updateValidityDateForUnRevisedMCEGraphic - START");
			String strMasterId =null;
			if(mapRevInfo.containsKey("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id")) {
				strMasterId = (String) mapRevInfo.get("from["+RELATIONSHIP_PGMASTERCOPYCLAIM+"].to.id");		
			}
			if(BusinessUtil.isNotNullOrEmpty(strMasterId)) {
				ArtworkMaster am=new ArtworkMaster(strMasterId);
				am.setAttributeValue(context, ATTRIBUTE_VALIDATE_DATE, strExpirationDate);
			}
			LOGGER.log(Level.INFO, "updateValidityDateForUnRevisedMCEGraphic - END");
		}catch(Exception e){
			throw e;
		}
	}
	//RTA 22x Added for ALM-46415 ends
	
	//RTA(DS) - Added for 22x CW4 - Req 47882 - Start
	/**
	*
	*Method to update the Sub Copy Type on MC
	*@param strMCId MC Id 
	**/
	public static void updateSubCopyTypeOnMC(Context context,String strMCId) throws Exception{
		try{
			if(BusinessUtil.isNullOrEmpty(strMCId)){
				return;
			}
			
			JPO.invoke(context, "pgRTACopyElementUtil", null, "updateSubCopyTypeOnMC", new String[]{strMCId});	
			
		}catch(Exception e){
			LOGGER.log(Level.SEVERE, e.toString());
			LOGGER.log(Level.SEVERE, e.getMessage());			
			throw e;
		}		
	}
	//RTA(DS) - Added for 22x CW4 - Req 47882 - End
}

/*
Project Name: REQ 35895 - Orchestration Phase-1: Orchestration is an integration between PEGA and RTA(Enovia) system
Service Name: pgRTAOrchestrationPlanService
Purpose: This class contains logic to create POA, PMP data
 */
package com.pg.orchestration;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.PersonUtil;
//Added by RTA Capgemini Offshore 18x.5 CW defect-36820 Starts
import com.matrixone.apps.domain.util.XSSUtil;
//Added by RTA Capgemini Offshore 18x.5 CW defect-36820 Ends
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.dassault_systemes.platform.restServices.RestService;
import com.matrixone.apps.cpn.util.BusinessUtil;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.Job;
import matrix.db.BusinessInterface;
import matrix.db.Context;
import matrix.util.MatrixException;

import com.matrixone.servlet.Framework;
import com.matrixone.servlet.FrameworkServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import com.matrixone.apps.domain.util.ContextUtil;
import javax.json.JsonObjectBuilder;
import javax.json.Json;


@javax.ws.rs.Path(value = "/orchestration")
public class PgRtaOrchestrationService extends RestService {
	
	private static final Logger logger = Logger.getLogger("com.pg.orchestration.pgRTAOrchestrationService");
	private static final String RESPONSE_TWO_ZERO_ZERO = "200 OK";
	private static final String RESPONSE_FIVE_ZERO_ZERO = "500 INTERNAL SERVER ERROR";
	private static final String PGRTAORCHESTRATIONINTERFACE = "pgRTAOrchestrationJob";
	private static final String USERAGENT = "User Agent";
	private static final String PGRTAORCHESTRATIONREQUESTTYPE = "pgRTAOrchestrationRequestType";
	private static final String STATE_ACTIVE = "Active";
	private static final String RESPONSE_TWO_ZERO_ONE = "201";
	private static final String RESPONSE_ESIX_THREE_ONE = "E631";
	// Added by RTA Capgemini Offshore for 22x 45602 Req 42907 - Starts
	private static final String MESSAGE_OWNER_NOT_FOUND = "Please request RTA account extension in the junction.pg.com. Once your account is active you can proceed with POA creation in PEGA.";
	// Added by RTA Capgemini Offshore for 22x 45602 Req 42907 - End
	private static final String MESSAGE_POACREATION_IN_PROGRESS = "The POA Creation request will be processed in sometime..";
	private static final String MESSAGE_MANDATORY_INFO_MISSING = "Mandatory information(ownerId, Initiative Name, Initiative ID, POA information) is empty or null"; 
	private static final String MESSAGE_POAUPDATE_IN_PROGRESS = "POA Update request request will be processed in sometime...";
	
	@POST
	@javax.ws.rs.Path("/createOrchestrationPOA")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createOrchestrationPOA(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,PgPoaRequestData poaRequestData) throws Exception {
		logger.info("\n :::::::::::::::: Inside createOrchestrationPOA method ::::::::::::::: starts");
		//Added by RTA for 22x May 23 CW Defect-52685 -Starts
		System.out.println("===poaRequestDataGetPOA=="+poaRequestData.getPOA());
		System.out.println("===poaRequestDataGetHashcode=="+poaRequestData.hashCode());
		//Added by RTA for 22x May 23 CW Defect-52685 -End
		PgPoaResponseData responseData = null;
		Context context = null;
		String errorMsg = DomainConstants.EMPTY_STRING; 
		try {
			if (Framework.isLoggedIn((HttpServletRequest) paramHttpServletRequest)) {
				context = Framework.getContext((HttpSession) paramHttpServletRequest.getSession(false));
				if(context != null) {
					String initName = poaRequestData.getInitiativeName();
					String initID = poaRequestData.getInitiativeID();
					String ownerName = poaRequestData.getOwnerId();
					//String ownerName = poaRequestData.getOwnerid();
					List<PgPoaRequestPoaData> poaDatafromPEGA = poaRequestData.getPOA();
					String orchestrationcreate = EnoviaResourceBundle.getProperty(context, "emxAWLStringResource",context.getLocale(), "emxAWL.Label.OrchestrationCreate");
					if(!poaDatafromPEGA.isEmpty() && BusinessUtil.isNotNullOrEmpty(initName) && BusinessUtil.isNotNullOrEmpty(initID) && BusinessUtil.isNotNullOrEmpty(ownerName)) {
					//Call method to validate Owner
					boolean bflag = validateOwner(context,ownerName);
					if(bflag) {
					String strObjId = createJobObject(context,orchestrationcreate);
					DomainObject dJob = DomainObject.newInstance(context,strObjId);
					StringBuilder strDescription = setJobDescription(poaRequestData);
					dJob.setDescription(context,strDescription.toString());
					responseData = new PgPoaResponseData(RESPONSE_TWO_ZERO_ZERO,MESSAGE_POACREATION_IN_PROGRESS);
				}else {
					responseData = new PgPoaResponseData(RESPONSE_TWO_ZERO_ONE,MESSAGE_OWNER_NOT_FOUND);
				}
					}else {
					responseData = new PgPoaResponseData(RESPONSE_ESIX_THREE_ONE,MESSAGE_MANDATORY_INFO_MISSING);
				}	
				}
				
			}
		} catch (Exception e) {
			errorMsg = e.getMessage().trim();
			if(errorMsg.contains("No person business object found")) {
				responseData = new PgPoaResponseData(RESPONSE_TWO_ZERO_ONE,MESSAGE_OWNER_NOT_FOUND);
			}else {
			errorMsg = "Enovia System Error, please contact Administrator.";
			responseData = new PgPoaResponseData(RESPONSE_FIVE_ZERO_ZERO, errorMsg);
		}}
		finally {
			try {
				if (context!=null) {
					context.close();
					
				}
			} catch (Exception e) {
				logger.log(Level.INFO, "Exception in method connectOrchestrationPOAPMP", e);
			}
		}
		
		// Added in 22x.
		JsonObjectBuilder job = Json.createObjectBuilder(); 
		if(responseData!=null) {
			job.add("code",responseData.code);
			job.add("message",responseData.message);
		}
		
		return Response.ok(job.build()).build();
	
	}
	@POST
	@javax.ws.rs.Path("/connectOrchestrationPOAPMP")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response connectOrchestrationPOAPMP(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,PgPmpRequestData poaRequestData) throws Exception {
		
		logger.info("\n ::::: Inside connectOrchestrationPOAPMP() method :::::");
		String errorMsg = DomainConstants.EMPTY_STRING; 
		
		PgPoaPmpResponse responseData = null;
		
		Context context = null;
		
		try {
			if (Framework.isLoggedIn((HttpServletRequest) paramHttpServletRequest)) {
				context = Framework.getContext((HttpSession) paramHttpServletRequest.getSession(false));
				if(context != null) {
				String orchestrationupdate = EnoviaResourceBundle.getProperty(context, "emxAWLStringResource",context.getLocale(), "emxAWL.Label.OrchestrationUpdate");
				
				String strObjId = createJobObject(context,orchestrationupdate);
				
				DomainObject dJob = DomainObject.newInstance(context,strObjId);
				
				StringBuilder strDescription = setJobDescriptionUpdate(poaRequestData);
				
				dJob.setDescription(context,strDescription.toString());
				
				responseData = new PgPoaPmpResponse(RESPONSE_TWO_ZERO_ZERO,MESSAGE_POAUPDATE_IN_PROGRESS);
				
				}
				}
			} catch (Exception e) {
						errorMsg = "Enovia System Error, please contact Administrator.";
						responseData = new PgPoaPmpResponse(RESPONSE_FIVE_ZERO_ZERO, errorMsg);
				}
				
				finally {
					try {
						if (context!=null) {
							context.close();
							
						}
					} catch (Exception e) {
						logger.log(Level.INFO, "Exception in method connectOrchestrationPOAPMP", e);
					}
				}
				
		JsonObjectBuilder job = Json.createObjectBuilder(); 
		if(responseData!=null) {
			job.add("code",responseData.code);
			job.add("message",responseData.message);
		}
		
		return Response.ok(job.build()).build();
		
		//return Response.ok(responseData).build();
	}
	/**
     * This method will be used validate Job is in create state
     * @param sAttrVal - Update or Create request
	 * @return String
	 * @throws FrameworkException 
     */
	public String  createJobObject(Context context,String sAttrVal) throws MatrixException {
		logger.log(Level.INFO, "In method createJobObject");
		boolean isContextPushed = false;
		Job job = new Job();
		String sjobId = "";
		
		try {
		job.create(context);
		job.setOwner(context, context.getUser());
		sjobId = job.getId(context);
		// Add the interface
		DomainObject dJob = DomainObject.newInstance(context,sjobId);
		
		BusinessInterface pgOrchestrationInterface = new BusinessInterface(PGRTAORCHESTRATIONINTERFACE,context.getVault());
		ContextUtil.pushContext(context, USERAGENT, DomainConstants.EMPTY_STRING,context.getVault().getName());
		isContextPushed = true;
		dJob.addBusinessInterface(context, pgOrchestrationInterface);
		dJob.setAttributeValue(context, PGRTAORCHESTRATIONREQUESTTYPE, sAttrVal);
		
		
		}catch(MatrixException ex) {
			logger.log(Level.INFO, "Exception in method createJobObject", ex);
			throw new MatrixException(ex);
		}
		finally {
			if(isContextPushed) {
				ContextUtil.popContext(context);
			}
		}
		return sjobId;
	}
	/**
     * This method will be used to return description for Jobs create by Update request
     * @param poaRequestData - JSON Request 
	 * @return StringBuilder
     */
	public StringBuilder setJobDescriptionUpdate(PgPmpRequestData poaRequestData)throws Exception {
		logger.log(Level.INFO, "In method setJobDescriptionUpdate");
		StringBuilder sJSONRequest = new StringBuilder();
		try {
			List<PgPoaPmpRequestData> lPOA = poaRequestData.getPOA();
			for(int i = 0; i < lPOA.size(); i++){
				
				PgPoaPmpRequestData poaVal = lPOA.get(i);
				//POA Name
				String sUniqueId = poaVal.getUniqueID();
				sJSONRequest.append("[");
				sJSONRequest.append("sUniqueId:");
				sJSONRequest.append(sUniqueId);
				sJSONRequest.append(",");
				String sPoaName = poaVal.getPoaName();
				sJSONRequest.append("sPoaName:");
				sJSONRequest.append(sPoaName);
				sJSONRequest.append(",");
				//Added by RTA Capgemini Offshore for 22x.5 April_24_CW Req 49132 Starts
				//InitiativeId
				String sInitId = poaVal.getInitiativeID();
				sJSONRequest.append("initiativeId:");
				sJSONRequest.append(sInitId);
				sJSONRequest.append(",");
				//Added by RTA Capgemini Offshore for 22x.5 April_24_CW Req 49132 Ends
				//Country & Language
				String country = poaVal.getCountry();
				sJSONRequest.append("country:");
				sJSONRequest.append(country);
				sJSONRequest.append(",");
				String language = poaVal.getLanguage();
				sJSONRequest.append("language:");
				sJSONRequest.append(language);
				sJSONRequest.append(",");
				//PMP
				String sPMP = poaVal.getPmp();
				sJSONRequest.append("sPMP:");
				sJSONRequest.append(sPMP);
				sJSONRequest.append(",");
				// CE Values
				String iLLeaderId = poaVal.getiLLeaderId();
				sJSONRequest.append("iLLeaderId:");
				sJSONRequest.append(iLLeaderId);
				sJSONRequest.append(",");
				String netWeight = poaVal.getnetWeight();
				sJSONRequest.append("netWeight:");
				sJSONRequest.append(XSSUtil.encodeForURL(netWeight));//Modified by RTA Capgemini offshore for 2018x.6 Def-38692 Starts/ends
				sJSONRequest.append(",");
				String sNetWeightUOM = poaVal.getnetWeightuom();
				sJSONRequest.append("netWeightUOM:");
				sJSONRequest.append(sNetWeightUOM);
				sJSONRequest.append(",");
				String sRecommDosageUOM = poaVal.getrecommendedDosageuom();
				sJSONRequest.append("recommendedDosageUOM:");
				sJSONRequest.append(sRecommDosageUOM);
				sJSONRequest.append(",");
				String srecommendedDosage = poaVal.getrecommendedDosage();
				sJSONRequest.append("recommendedDosage:");
				sJSONRequest.append(XSSUtil.encodeForURL(srecommendedDosage));//Modified by RTA Capgemini offshore for 2018x.6 Def-38692 Starts/ends
				sJSONRequest.append(",");
				String snoOfDosesPerScoops = poaVal.getnoOfDosesPerScoops();
				sJSONRequest.append("noOfDosesPerScoops:");
				sJSONRequest.append(XSSUtil.encodeForURL(snoOfDosesPerScoops));//Modified by RTA Capgemini offshore for 2018x.6 Def-38692 Starts/ends
				sJSONRequest.append(",");
				String smultiplefpc = poaVal.getmultiplefpc(); 
				sJSONRequest.append("smultiplefpc:");
				sJSONRequest.append(smultiplefpc);
				sJSONRequest.append(",");
				String sfinishedproductcode = poaVal.getfinishedproductcode();
				sJSONRequest.append("sfinishedproductcode:");
				sJSONRequest.append(sfinishedproductcode);
				sJSONRequest.append(",");
				//GCAS
				String sGCAS = poaVal.getGCAS();
				sJSONRequest.append("GCAS:");
				sJSONRequest.append(sGCAS);
				//Added by RTA Capgemini Offshore 18x.5 CW defect-36820 Starts
				//pmpDescription
				sJSONRequest.append(",");
                String spmpDescription = poaVal.getpmpDescription();
                sJSONRequest.append("pmpDescription:");
                sJSONRequest.append(XSSUtil.encodeForURL(spmpDescription));
				//Added by RTA Capgemini Offshore 18x.5 CW defect-36820 Ends
				//Added by RTA Capgemini offshore 2018x.6 req-36492 Starts
                sJSONRequest.append(",");
                String sGpsFixedCES = poaVal.getGpsFixedCES();
                sJSONRequest.append("gpsFixedCES:");
                sJSONRequest.append(sGpsFixedCES);
                sJSONRequest.append(",");
                String sGpsVariableCES = poaVal.getGpsVariableCES();
                sJSONRequest.append("gpsVariableCES:");
                sJSONRequest.append(sGpsVariableCES);
                sJSONRequest.append(",");
                String sMrkClaimCES = poaVal.getMrkClaimCES();
                sJSONRequest.append("mrkClaimCES:");
                sJSONRequest.append(sMrkClaimCES);
                //Added by RTA Capgemini offshore 2018x.6 req-36492 Ends
                
                //Added by RTA Capgemini offshore 2018x.6 req-36133 Starts
                sJSONRequest.append(",");
                String sHarmonizePOA = poaVal.getHarmonizePOA();
                sJSONRequest.append("harmonizePOA:");
                sJSONRequest.append(sHarmonizePOA);
                //Added by RTA Capgemini offshore 2018x.6 req-36133 Ends
								// Added by RTA Capgemini offshore 2018x.6 req-43229 Starts
				sJSONRequest.append(",");
				String sRegulatoryClassification = poaVal.getRegulatoryClassification();
				sJSONRequest.append("regulatoryClassification:");
				sJSONRequest.append(sRegulatoryClassification);
				// Added by RTA Capgemini offshore 2018x.6 req-43229 Ends
				// Added by RTA Capgemini offshore 2018x.6 June 22 CW Defect 48457 Starts
				sJSONRequest.append(",");
				String sPAnumber = poaVal.getPaNumber();
				sJSONRequest.append("sPAnumber:");
				sJSONRequest.append(sPAnumber);
				// Added by RTA Capgemini offshore 2018x.6 June 22 CW Defect 48457 Ends
				// Added by RTA Capgemini offshore 2018x.6 req-43432 Starts
				sJSONRequest.append(",");
				String sECAPS_ClaimReq = poaVal.getECAPS_ClaimReq();
				sJSONRequest.append("ECAPS_ClaimReq:");
				sJSONRequest.append(sECAPS_ClaimReq);
				// Added by RTA Capgemini offshore 2018x.6 req-43432 Ends
				
				// Added by RTA Capgemini offshore 2018x.6 req-39244 Starts
				sJSONRequest.append(",");
				String sProductForm = poaVal.getProductForm();
				sJSONRequest.append("sProdForm:");
				sJSONRequest.append(sProductForm);
				// Added by RTA Capgemini offshore 2018x.6 req-39244 Ends
				// Added by RTA Capgemini Offshore for 18x.6 MAY_CW Req 36128,42564,42669,42819,42563   - Starts
				sJSONRequest.append(",");
				String sPackagingLevel = poaVal.getPackagingLevel();
				sJSONRequest.append("sPackagingLevel:");
				sJSONRequest.append(sPackagingLevel);
				
                //updating json for piFPCDescription
                sJSONRequest.append(",");
                String sPIFPCDes = poaVal.getPiFPCDescription();
                sJSONRequest.append("piFPCDescription:");
                sJSONRequest.append(sPIFPCDes);
                
                //Added by RTA Capgemini Offshore for 22x.5 April_24_CW Req 49282,49283 Starts
                //updating json for awmProjectId
                sJSONRequest.append(",");
                String awmProjectId = poaVal.getAwmProjectId();
                sJSONRequest.append("awmProjectId:");
                sJSONRequest.append(awmProjectId);
                
                //updating json for awmProjectName
                sJSONRequest.append(",");
                String awmProjectName = poaVal.getAwmProjectName();
                sJSONRequest.append("awmProjectName:");
                sJSONRequest.append(awmProjectName);
                
                //updating json for awmSupplier
                sJSONRequest.append(",");
                String awmSupplier = poaVal.getAwmSupplier();
                sJSONRequest.append("awmSupplier:");
                sJSONRequest.append(awmSupplier);
                
                //updating json for awmArtWorkAssignee
                sJSONRequest.append(",");
                String awmArtWorkAssignee = poaVal.getAwmArtWorkAssignee();
                sJSONRequest.append("awmArtWorkAssignee:");
                sJSONRequest.append(awmArtWorkAssignee);
                //Added by RTA Capgemini Offshore for 22x.5 April_24_CW Req 49282,49283 Ends
				
				// Added by RTA Capgemini Offshore for 18x.6 JUNE_CW Req 42907   - Starts
				//updating json for fcVersion
                sJSONRequest.append(",");
                String sFcVersion = poaVal.getFcVersion();
                sJSONRequest.append("fcVersion:");
                sJSONRequest.append(sFcVersion);
				// Added by RTA Capgemini Offshore for 18x.6 JUNE_CW Req 42907   - Ends
                
                sJSONRequest.append(",");
                String sReferencePMP = poaVal.getReferencePMP();
				sJSONRequest.append("sReferencePMP:");
				sJSONRequest.append(sReferencePMP);
				sJSONRequest.append(",");
				
				String sPhaseOutPOA = poaVal.getPhaseOutPOA();
				sJSONRequest.append("sPhaseOutPOA:");
				sJSONRequest.append(sPhaseOutPOA);
                
                
              //Added by RTA Capgemini Offshore for 18x.6 May_CW Requirement 42564,42669,42819,42563,36128 -- Ends
				//Added by RTA for 22x May 23 CW ALM-46090 -Starts
				sJSONRequest.append(",");
				String sProductionPlant = poaVal.getProductionPlant();
				//Added by RTA for 22x May 23 CW Defect-52516 -Starts
				sJSONRequest.append("productionPlant:");
				//Added by RTA for 22x May 23 CW Defect-52516 -End
				sJSONRequest.append(sProductionPlant);
				sJSONRequest.append(",");
				String sProductionPlantPrimary = poaVal.getProductionPlantPrimary();
				//Added by RTA for 22x May 23 CW Defect-52516 -Starts
				sJSONRequest.append("productionPlantPrimary:");
				//Added by RTA for 22x May 23 CW Defect-52516 -End
				sJSONRequest.append(sProductionPlantPrimary);
				//Added by RTA for 22x May 23 CW ALM-46090 -End
				//Added by RTA for 22x Aug 23 CW ALM-47299 -Starts
				sJSONRequest.append(",");
				String sArtworkPlant = poaVal.getArtWorkPlant();
				sJSONRequest.append("artWorkPlant:");
				sJSONRequest.append(sArtworkPlant);
				//Added by RTA for 22x Aug 23 CW ALM-47299 -Ends
                //Added by RTA for 22x.5 APR_24_CW ALM-Req-48395 & 48396 & 48814 & 49052 - Starts
				sJSONRequest.append(",");
				String sPackagingSite = poaVal.getPackagingSite();
				sJSONRequest.append("packagingSite:");
				sJSONRequest.append(sPackagingSite);
				sJSONRequest.append(",");
				String sDpp = poaVal.getDpp();
				sJSONRequest.append("dpp:");
				sJSONRequest.append(sDpp);
				//Added by RTA for 22x.5 APR_24_CW ALM-Req-48395 & 48396 & 48814 & 49052 - Ends
				//Added by RTA for 22x.5 APR_24_CW ALM-Req-49054 & 49055 - Starts
				sJSONRequest.append(",");
				String sCrossSell = poaVal.getCrossSell();
				sJSONRequest.append("crossSell:");
				sJSONRequest.append(sCrossSell);
				sJSONRequest.append(",");
				String sSyntheticPerfumeFlag = poaVal.getSyntheticPerfumeFlag();
				sJSONRequest.append("syntheticPerfumeFlag:");
				sJSONRequest.append(sSyntheticPerfumeFlag);
				sJSONRequest.append(",");
				String sFlavourScentDetails = poaVal.getFlavourScentDetails();
				sJSONRequest.append("flavourScentDetails:");
				sJSONRequest.append(sFlavourScentDetails);
				sJSONRequest.append(",");
				String sSubBrand = poaVal.getSubBrand();
				sJSONRequest.append("subBrand:");
				sJSONRequest.append(sSubBrand);
				sJSONRequest.append(",");
				String sCategory = poaVal.getCategory();
				sJSONRequest.append("category:");
				sJSONRequest.append(sCategory);
				sJSONRequest.append(",");
				String sConsumerBenefitOne = poaVal.getConsumerBenefitOne();
				sJSONRequest.append("consumerBenefitOne:");
				sJSONRequest.append(sConsumerBenefitOne);
				sJSONRequest.append(",");
				String sConsumerBenefitTwo = poaVal.getConsumerBenefitTwo();
				sJSONRequest.append("consumerBenefitTwo:");
				sJSONRequest.append(sConsumerBenefitTwo);
				sJSONRequest.append(",");
				String sConsumerBenefitThree = poaVal.getConsumerBenefitThree();
				sJSONRequest.append("consumerBenefitThree:");
				sJSONRequest.append(sConsumerBenefitThree);
				//Added by RTA for 22x.5 APR_24_CW ALM-Req-49054 & 49055 - Ends
				//Added by RTA for 22x.6 AUG_24_CW ALM-Req-49750 & 49843 - Starts
				sJSONRequest.append(",");
				String sCountryOfOrigin = poaVal.getCountryOfOrigin();
				sJSONRequest.append("countryOfOrigin:");
				sJSONRequest.append(sCountryOfOrigin);
				sJSONRequest.append(",");
				String ssegment = poaVal.getSegment();
				sJSONRequest.append("segment:");
				sJSONRequest.append(ssegment);
				//Added by RTA for 22x.6 AUG_24_CW ALM-Req-49750 & 49843 - Ends
				//Added by RTA for 22x.4 Dec 23 CW Req-47799 -Starts
				sJSONRequest.append(",");
				String sSource = poaVal.getSource();
				sJSONRequest.append("source:");
				sJSONRequest.append(sSource);
				//Added by RTA for 22x.4 Dec 23 CW Req-47799 -Ends
				}
		}catch(Exception ex) {
			logger.log(Level.INFO, "Exception in method setJobDescriptionUpdate", ex);
			throw new Exception(ex);
		}
		return sJSONRequest;
		
	}
	/**
     * This method will be used to return description for Jobs create by Create request
     * @param poaRequestData - JSON Request 
	 * @return StringBuilder
     */
	public StringBuilder setJobDescription(PgPoaRequestData poaRequestData)throws Exception {
		logger.log(Level.INFO, "In method setJobDescription");
		StringBuilder sJSONRequest = new StringBuilder();
		try {
			String sOwnerId = poaRequestData.getOwnerId();
			//String sOwnerId = poaRequestData.getOwnerid();
			String sInitiativeName = poaRequestData.getInitiativeName();
			String sInitiativeID = poaRequestData.getInitiativeID();
			//Start modified for 22x changes - Matching the method name with keys from request body
			String sIlLeader = poaRequestData.getiLLeaderId();
			//End modified for 22x changes - Matching the method name with keys from request body
			// Append the values
			sJSONRequest.append("[");
			sJSONRequest.append("initName:");
			sJSONRequest.append(sInitiativeName);
			sJSONRequest.append("|");
			sJSONRequest.append("initID:");
			sJSONRequest.append(sInitiativeID);
			sJSONRequest.append("|");
			sJSONRequest.append("ownerId:");
			sJSONRequest.append(sOwnerId);
			sJSONRequest.append("|");
			sJSONRequest.append("iLLeaderId:");
			sJSONRequest.append(sIlLeader);
			
			List<PgPoaRequestPoaData> lPOA = poaRequestData.getPOA();
			System.out.println("lPOADesCreate========>"+lPOA);
			PgPoaRequestPoaData poaVal;
			for(int i = 0; i < lPOA.size(); i++){
				
				poaVal = lPOA.get(i);
				System.out.println("==poaValCreate=="+poaVal);
				sJSONRequest.append("[");
				String sPackagingComponentType = poaVal.getPackagingComponentType();
				sJSONRequest.append("sPackagingComponentType:");
				sJSONRequest.append(sPackagingComponentType);
				sJSONRequest.append(",");
				String sCountry = poaVal.getCountry();
				sJSONRequest.append("sCountry:");
				sJSONRequest.append(sCountry);
				sJSONRequest.append(",");
				String sLang = poaVal.getLanguage();
				sJSONRequest.append("sLang:");
				sJSONRequest.append(sLang);
				sJSONRequest.append(",");
				String sSecClass = poaVal.getSecurityCategoryClassification();
				sJSONRequest.append("sSecClass:");
				sJSONRequest.append(sSecClass);
				sJSONRequest.append(",");
				String sPhaseOutPOA = poaVal.getPhaseOutPOA();
				sJSONRequest.append("sPhaseOutPOA:");
				sJSONRequest.append(sPhaseOutPOA);
				sJSONRequest.append(",");
				String sReferencePMP = poaVal.getReferencePMP();
				sJSONRequest.append("sReferencePMP:");
				sJSONRequest.append(sReferencePMP);
				sJSONRequest.append(",");
				String sBrand = poaVal.getBrand();
				sJSONRequest.append("sBrand:");
				sJSONRequest.append(sBrand);
				sJSONRequest.append(",");
				//Start modified for 22x changes - Matching the method name with keys from request body
				String sRegion = poaVal.getBrandRegion();
				//End modified for 22x changes - Matching the method name with keys from request body
				sJSONRequest.append("sRegion:");
				sJSONRequest.append(sRegion);
				sJSONRequest.append(",");
				String sUniqueId = poaVal.getUniqueID();
				sJSONRequest.append("sUniqueId:");
				sJSONRequest.append(sUniqueId);
				sJSONRequest.append(",");
				//Modified by RTA Capgemini Offshore for 22x.4 Dec_23_CW defect 55686 Start
				String sFPC = poaVal.getFinishedproductcode();
				//Modified by RTA Capgemini Offshore for 22x.4 Dec_23_CW defect 55686 Ends
				sJSONRequest.append("FPC:");
				sJSONRequest.append(sFPC);
				sJSONRequest.append(",");
				String sProdForm = poaVal.getProductForm();
				sJSONRequest.append("sProdForm:");
				sJSONRequest.append(sProdForm);
				sJSONRequest.append(",");
				String sPackagingLevel = poaVal.getPackagingLevel();
				sJSONRequest.append("sPackagingLevel:");
				sJSONRequest.append(sPackagingLevel);
				sJSONRequest.append(",");
				String sGCASNumber = poaVal.getGCAS();
				sJSONRequest.append("sGCASNumber:");
				sJSONRequest.append(sGCASNumber);
				sJSONRequest.append(",");
				String sPAnumber = poaVal.getPaNumber();
				sJSONRequest.append("sPAnumber:");
				sJSONRequest.append(sPAnumber);
				sJSONRequest.append(",");
				String sSuppGTIN = poaVal.getSuppressedGTIN();
				sJSONRequest.append("sSuppGTIN:");
				sJSONRequest.append(sSuppGTIN);
				sJSONRequest.append(",");
				String sMatType = poaVal.getMaterialType();
				sJSONRequest.append("sMatType:");
				sJSONRequest.append(sMatType);
				sJSONRequest.append(",");
				String sPMP = poaVal.getPmp();
				sJSONRequest.append("sPMP:");
				sJSONRequest.append(sPMP);
				sJSONRequest.append(",");
				String sNoOfDosesPerScoops = poaVal.getNoOfDosesPerScoops();
				sJSONRequest.append("sgetNoOfDosesPerScoops:");
				sJSONRequest.append(XSSUtil.encodeForURL(sNoOfDosesPerScoops));//Modified by RTA Capgemini offshore for 2018x.6 Def-38692 Starts/ends
				sJSONRequest.append(",");
				String sRecommDosage = poaVal.getRecommendedDosage();
				sJSONRequest.append("sRecommDosage:");
				sJSONRequest.append(XSSUtil.encodeForURL(sRecommDosage));//Modified by RTA Capgemini offshore for 2018x.6 Def-38692 Starts/ends
				sJSONRequest.append(",");
				String sNetWeightUOM = poaVal.getnetWeightuom();
				sJSONRequest.append("sNetWeightUOM:");
				sJSONRequest.append(sNetWeightUOM);
				sJSONRequest.append(",");
				String sRecommDosageUOM = poaVal.getrecommendedDosageuom();
				sJSONRequest.append("sRecommDosageUOM:");
				sJSONRequest.append(sRecommDosageUOM);
				sJSONRequest.append(",");
				String sNetWeight = poaVal.getNetWeight();
				sJSONRequest.append("sNetWeight:");
				sJSONRequest.append(XSSUtil.encodeForURL(sNetWeight));//Modified by RTA Capgemini offshore for 2018x.6 Def-38692 Starts/ends
				sJSONRequest.append(",");
				String multiplefcp = poaVal.getmultiplefpc();
				sJSONRequest.append("multiplefpc:");
				sJSONRequest.append(multiplefcp);
				//Added by RTA Capgemini Offshore 18x.5 CW defect-36820 Starts
				sJSONRequest.append(",");
                String spmpDescription = poaVal.getpmpDescription();
                sJSONRequest.append("pmpDescription:");
                sJSONRequest.append(XSSUtil.encodeForURL(spmpDescription));
				//Added by RTA Capgemini Offshore 18x.5 CW defect-36820 Ends
				//Added by RTA Capgemini offshore 2018x.6 req-36492 Starts
                sJSONRequest.append(",");
                String sGpsFixedCES = poaVal.getGpsFixedCES();
                sJSONRequest.append("gpsFixedCES:");
                sJSONRequest.append(sGpsFixedCES);
                sJSONRequest.append(",");
                String sGpsVariableCES = poaVal.getGpsVariableCES();
                sJSONRequest.append("gpsVariableCES:");
                sJSONRequest.append(sGpsVariableCES);
                sJSONRequest.append(",");
                String sMrkClaimCES = poaVal.getMrkClaimCES();
                sJSONRequest.append("mrkClaimCES:");
                sJSONRequest.append(sMrkClaimCES);
                //Added by RTA Capgemini offshore 2018x.6 req-36492 Ends
                
                //Added by RTA Capgemini offshore 2018x.6 req-36133 Starts
                sJSONRequest.append(",");
                String sHarmonizePOA = poaVal.getHarmonizePOA();
                sJSONRequest.append("harmonizePOA:");
                sJSONRequest.append(sHarmonizePOA);
                //Added by RTA Capgemini offshore 2018x.6 req-36133 Ends
                				// Added by RTA Capgemini offshore 2018x.6 req-43229 Starts
				sJSONRequest.append(",");
				String sRegulatoryClassification = poaVal.getRegulatoryClassification();
				sJSONRequest.append("regulatoryClassification:");
				sJSONRequest.append(sRegulatoryClassification);
				// Added by RTA Capgemini offshore 2018x.6 req-43229 Ends
				
				// Added by RTA Capgemini offshore 2018x.6 req-43432 Starts
				sJSONRequest.append(",");
				String sECAPS_ClaimReq = poaVal.getECAPS_ClaimReq();
				sJSONRequest.append("ECAPS_ClaimReq:");
				sJSONRequest.append(sECAPS_ClaimReq);
				// Added by RTA Capgemini offshore 2018x.6 req-43432 Ends
				
				// Added by RTA Capgemini offshore 2018x.6 req-39244 Starts
				// Added by RTA Capgemini offshore 2018x.6 req-39244 Ends
                
              //Added by RTA Capgemini Offshore for 18x.6 May_CW Requirement 42564,42669,42819,42563 -- START
                //updating json for piFPCDescription
                sJSONRequest.append(",");
                String sPIFPCDes = poaVal.getPiFPCDescription();
                sJSONRequest.append("piFPCDescription:");
                sJSONRequest.append(sPIFPCDes);
                
                //Added by RTA Capgemini Offshore for 22x.5 April_24_CW Req 49282,49283 Starts
                //updating json for awmProjectId
                sJSONRequest.append(",");
                String awmProjectId = poaVal.getAwmProjectId();
                sJSONRequest.append("awmProjectId:");
                sJSONRequest.append(awmProjectId);
                
                //updating json for awmProjectName
                sJSONRequest.append(",");
                String awmProjectName = poaVal.getAwmProjectName();
                sJSONRequest.append("awmProjectName:");
                sJSONRequest.append(awmProjectName);
                
                //updating json for awmSupplier
                sJSONRequest.append(",");
                String awmSupplier = poaVal.getAwmSupplier();
                sJSONRequest.append("awmSupplier:");
                sJSONRequest.append(awmSupplier);
                
                //updating json for awmArtWorkAssignee
                sJSONRequest.append(",");
                String awmArtWorkAssignee = poaVal.getAwmArtWorkAssignee();
                sJSONRequest.append("awmArtWorkAssignee:");
                sJSONRequest.append(awmArtWorkAssignee);
                //Added by RTA Capgemini Offshore for 22x.5 April_24_CW Req 49282,49283 Ends
                  
              //Added by RTA Capgemini Offshore for 18x.6 May_CW Requirement 42564,42669,42819,42563 -- Ends
			  
			  // Added by RTA Capgemini Offshore for 18x.6 JUNE_CW Req 42907   - Starts
				//updating json for fcVersion
                sJSONRequest.append(",");
                String sFcVersion = poaVal.getFcVersion();
                sJSONRequest.append("fcVersion:");
                sJSONRequest.append(sFcVersion);
				// Added by RTA Capgemini Offshore for 18x.6 JUNE_CW Req 42907   - Ends
              //Added by RTA for 22x May 23 CW ALM-46090 -Starts
				sJSONRequest.append(",");
				String sProductionPlant = poaVal.getProductionPlant();
				System.out.println("===sProductionPlantCreateRequest=="+sProductionPlant);
				//Added by RTA for 22x May 23 CW Defect-52516 -Starts
				sJSONRequest.append("productionPlant:");
				//Added by RTA for 22x May 23 CW Defect-52516 -End
				sJSONRequest.append(sProductionPlant);
				sJSONRequest.append(",");
				String sProductionPlantPrimary = poaVal.getProductionPlantPrimary();
				System.out.println("===sProductionPlantPrimaryCreateRequest=="+sProductionPlantPrimary);
				//Added by RTA for 22x May 23 CW Defect-52516 -Starts
				sJSONRequest.append("productionPlantPrimary:");
				//Added by RTA for 22x May 23 CW Defect-52516 -End
				sJSONRequest.append(sProductionPlantPrimary);
				//Added by RTA for 22x May 23 CW ALM-46090 -End
				//Added by RTA for 22x Aug 23 CW ALM-47299 -Starts
				sJSONRequest.append(",");
				String sArtworkPlant = poaVal.getArtWorkPlant();
				sJSONRequest.append("artWorkPlant:");
				sJSONRequest.append(sArtworkPlant);
				//Added by RTA for 22x Aug 23 CW ALM-47299 -Ends
                //Added by RTA for 22x.5 APR_24_CW ALM-Req-48395 & 48396 & 48814 & 49052 - Starts
				sJSONRequest.append(",");
				String sPackagingSite = poaVal.getPackagingSite();
				sJSONRequest.append("packagingSite:");
				sJSONRequest.append(sPackagingSite);
				sJSONRequest.append(",");
				String sDpp = poaVal.getDpp();
				sJSONRequest.append("dpp:");
				sJSONRequest.append(sDpp);
				//Added by RTA for 22x.5 APR_24_CW ALM-Req-48395 & 48396 & 48814 & 49052 - Ends
				//Added by RTA for 22x.5 APR_24_CW ALM-Req-49054 & 49055 - Starts
				sJSONRequest.append(",");
				String sCrossSell = poaVal.getCrossSell();
				sJSONRequest.append("crossSell:");
				sJSONRequest.append(sCrossSell);
				sJSONRequest.append(",");
				String sSyntheticPerfumeFlag = poaVal.getSyntheticPerfumeFlag();
				sJSONRequest.append("syntheticPerfumeFlag:");
				sJSONRequest.append(sSyntheticPerfumeFlag);
				sJSONRequest.append(",");
				String sFlavourScentDetails = poaVal.getFlavourScentDetails();
				sJSONRequest.append("flavourScentDetails:");
				sJSONRequest.append(sFlavourScentDetails);
				sJSONRequest.append(",");
				String sSubBrand = poaVal.getSubBrand();
				sJSONRequest.append("subBrand:");
				sJSONRequest.append(sSubBrand);
				sJSONRequest.append(",");
				String sCategory = poaVal.getCategory();
				sJSONRequest.append("category:");
				sJSONRequest.append(sCategory);
				sJSONRequest.append(",");
				String sConsumerBenefitOne = poaVal.getConsumerBenefitOne();
				sJSONRequest.append("consumerBenefitOne:");
				sJSONRequest.append(sConsumerBenefitOne);
				sJSONRequest.append(",");
				String sConsumerBenefitTwo = poaVal.getConsumerBenefitTwo();
				sJSONRequest.append("consumerBenefitTwo:");
				sJSONRequest.append(sConsumerBenefitTwo);
				sJSONRequest.append(",");
				String sConsumerBenefitThree = poaVal.getConsumerBenefitThree();
				sJSONRequest.append("consumerBenefitThree:");
				sJSONRequest.append(sConsumerBenefitThree);
				//Added by RTA for 22x.5 APR_24_CW ALM-Req-49054 & 49055 - Ends
				//Added by RTA for 22x.6 AUG_24_CW ALM-Req-49750 & 49843 - Starts
				sJSONRequest.append(",");
				String sCountryOfOrigin = poaVal.getCountryOfOrigin();
				sJSONRequest.append("countryOfOrigin:");
				sJSONRequest.append(sCountryOfOrigin);
				sJSONRequest.append(",");
				String ssegment = poaVal.getSegment();
				sJSONRequest.append("segment:");
				sJSONRequest.append(ssegment);
				//Added by RTA for 22x.6 AUG_24_CW ALM-Req-49750 & 49843 - Ends
                //Added by RTA for 22x.4 Dec 23 CW Req-47799 -Starts
				sJSONRequest.append(",");
				String sSource = poaVal.getSource();
				sJSONRequest.append("source:");
				sJSONRequest.append(sSource);
				//Added by RTA for 22x.4 Dec 23 CW Req-47799 -Ends
				}
			
		}catch(Exception ex) {
			logger.log(Level.INFO, "Exception in method setJobDescription", ex);
			throw new Exception(ex);
		}
		return sJSONRequest;
	}
	
	/**
     * This method will Validate the owner
     * @param context, ownerName
	 * @return boolean
     */
	public boolean validateOwner(Context context,String ownerName)throws Exception {
		String personId =DomainConstants.EMPTY_STRING;
		boolean bperson = false;
		try {
			if(BusinessUtil.isNotNullOrEmpty(ownerName)) {
				personId = PersonUtil.getPersonObjectID(context,ownerName);
				if(BusinessUtil.isNotNullOrEmpty(personId)) {
					DomainObject dperson=DomainObject.newInstance(context, personId);
					if(STATE_ACTIVE.equalsIgnoreCase(dperson.getInfo(context,DomainConstants.SELECT_CURRENT))){
					bperson = true;
					}
					}
			}
		}
		catch(Exception ex) {
			logger.log(Level.INFO, "Exception in method validateOwner", ex);
			throw new Exception(ex);
		}
		return bperson;
		
	}
	/**
     * This method will be used to logout the session
     * @param request - is the request session context
	 * @return Response
     */
	@javax.ws.rs.Path("/logout")
    @Produces({"application/ds-json", "application/xml"})
    @GET
    public Response logout(@javax.ws.rs.core.Context HttpServletRequest request) {
        try {
            FrameworkServlet.doLogout(request.getSession());
            return Response.status(200).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(e.getMessage()).build();
        }
    }
}


package com.png.apollo.dashboard.productspecification;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.json.JsonObject;
import javax.json.JsonArray;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dassault_systemes.platform.restServices.RestService;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.servlet.Framework;
import com.png.apollo.pgApolloConstants;
import com.png.apollo.dashboard.productspecification.dashboardutil.CharacteristicsDashboardUtill;
import com.pg.widgets.util.PGWidgetUtil;

import matrix.util.StringList;

@Path("/dashboard")
public class ProductSpecificationService extends RestService{

	
	 private static final org.apache.log4j.Logger loggerApolloTrace = org.apache.log4j.Logger.getLogger(ProductSpecificationService.class);

	public static final String RELEASEPHASE = "ReleasePhase";
	public static final String APPLICABLETYPE = "ApplicableType";
	public static final String BUSINESSAREA = "BusinessArea";
	public static final String PRODUCTCATEGORYPLATFORM = "ProductCategoryPlatform";
	public static final String PRODUCTTECHNOLOGYPLATFORM = "ProductTechnologyPlatform";
	public static final String PRODUCTTECHNOLOGYCHASSIS = "ProductTechnologyChassis";
	public static final String PRODUCTSIZE = "ProductSize";
	public static final String INTENDEDMARKET = "IntendedMarket";
	public static final String GETINWORKCRITERIA = "GetInWorkCriteria";
	public static final String FETCHCHARMASTER = "fetchCharMaster";
	public static final String GETTARGETLIMITVALUES = "FetchLimitValues";
	public static final String GETINWORKCM = "GetInWorkCM";
	public static final String RELEASEPHASE2 = "ReleasePhase2";
	public static final String BUSINESSAREA2 = "BusinessArea2";
	public static final String PRODUCTCATEGORYPLATFORM2 = "ProductCategoryPlatform2";
	public static final String PRODUCTTECHNOLOGYPLATFORM2 = "ProductTechnologyPlatform2";
	public static final String PRODUCTTECHNOLOGYCHASSIS2 = "ProductTechnologyChassis2";
	public static final String PRODUCTSIZE2 = "ProductSize2";
	public static final String INTENDEDMARKET2 = "IntendedMarket2";
	public static final String ATTRIBUTE = "Attribute";
	public static final String INPUT= "Input";	
	public static final String APPLICATION_JASON ="application/json";
	public static final String ID= "ID";	
	
	public static final String ID1 ="id1";
	public static final String ID2 ="id2";
	

	@GET
	@Path("/pgGetInitialData")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getInitialValue(@Context HttpServletRequest request) throws Exception {
		matrix.db.Context context = getAuthenticatedContext(request, false);
		if(context == null && Framework.isLoggedIn( request)) {			 
			context = Framework.getContext(request.getSession(false));			
		}
		JSONArray jsonArr = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		Response res = null;
		try {
			jsonArr  = CharacteristicsDashboardUtill.getBusinessAreas(context);
			jsonObject.put("BAData", jsonArr);
			jsonArr = CharacteristicsDashboardUtill.getIntendedMarketsList(context);
			jsonObject.put("MarketData",jsonArr);
			jsonArr = CharacteristicsDashboardUtill.getPhaseList(context);
			jsonObject.put("PhaseData",jsonArr);
			res = Response.status(200).entity(jsonObject.toString()).build();
		} catch (Exception e) {
			res=Response.ok(e.getMessage()).type(APPLICATION_JASON).build();
			loggerApolloTrace.error(e.getMessage() ,e);
		}
		return  res;
	}

	@POST
	@Path("/pgGetDataForCMD")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getCMDataForCriteria(@javax.ws.rs.core.Context HttpServletRequest request, String strInputData) throws Exception		
	{
		matrix.db.Context context = getAuthenticatedContext(request, false);
		Response res = null;
		MapList ml = null;
		if(context == null && Framework.isLoggedIn(request)) {
			context = Framework.getContext(request.getSession(false));
		}
		
		//Removed input parameters and sent it using JSON value
		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInputData);
		
		String sMode = DomainConstants.EMPTY_STRING;
		
		if(jsonInputData.containsKey(pgApolloConstants.STR_MODE))
		{
			 sMode = jsonInputData.getString(pgApolloConstants.STR_MODE);
		}
				
		if(UIUtil.isNotNullAndNotEmpty(sMode) && FETCHCHARMASTER.equalsIgnoreCase(sMode))
		{
			boolean bGetInworkCMs = jsonInputData.getBoolean(GETINWORKCM);
			
			boolean bFetchTargetLimitVals = jsonInputData.getBoolean(GETTARGETLIMITVALUES);			
			
			String sSelectedCriteriaIds = jsonInputData.getString(ID);
			StringList slCriteriaList = StringUtil.split(sSelectedCriteriaIds, pgApolloConstants.CONSTANT_STRING_PIPE);	
			
			JSONArray jsonArr = new JSONArray();
			JSONObject jsonObject = new JSONObject();		
			
			try 
			{
				ml = CharacteristicsDashboardUtill.getCharacteristicMasterAssociatedWithCriterias(context, slCriteriaList,bGetInworkCMs, bFetchTargetLimitVals);
				
				jsonArr  = CharacteristicsDashboardUtill.convertMapListToJasonArray(ml);
				jsonObject.put("CMData", jsonArr);
				res = Response.status(200).entity(jsonArr.toString()).build();
			} catch (Exception e) {
				res=Response.ok(e.getMessage()).type(APPLICATION_JASON).build();
			}			
			
		}
		else
		{
			boolean getInworkCriteria = jsonInputData.getBoolean(GETINWORKCRITERIA);
			String getInWorkCriteria = Boolean.toString(getInworkCriteria);
			boolean getInworkCMs = jsonInputData.getBoolean(GETINWORKCM);
			String getInWorkCM = Boolean.toString(getInworkCMs);
			boolean fetchTargetLimit = jsonInputData.getBoolean(GETTARGETLIMITVALUES);
			String getTargetLimitValues = Boolean.toString(fetchTargetLimit);
			String phase = jsonInputData.getString(RELEASEPHASE);
			String businessArea = jsonInputData.getString(BUSINESSAREA);
			String pcp = jsonInputData.getString(PRODUCTCATEGORYPLATFORM);
			String ptp = jsonInputData.getString(PRODUCTTECHNOLOGYPLATFORM);
			String ptc = jsonInputData.getString(PRODUCTTECHNOLOGYCHASSIS);
			String productSize = jsonInputData.getString(PRODUCTSIZE);
			String sRegion = jsonInputData.getString(pgApolloConstants.STR_AUTOMATION_REGION);
			String sSubRegion = jsonInputData.getString(pgApolloConstants.STR_AUTOMATION_SUBREGION);
			String intendedMarket = jsonInputData.getString(INTENDEDMARKET);
			String applicableType = jsonInputData.getString(APPLICABLETYPE);
			//We can pass all these attributes from UI and remove string constants
			//Convert String Query Params to StringList For Multiple ValueSelection
			StringList slReleasePhase = StringUtil.split(phase, ",");
			StringList slBusinessArea = StringUtil.split(businessArea, ",");
			StringList slPCP = StringUtil.split(pcp, ",");
			StringList slPTP = StringUtil.split(ptp, ",");
			StringList slPTC = StringUtil.split(ptc, ",");
			StringList slProductSize = StringUtil.split(productSize, ",");
			StringList slRegion =  StringUtil.split(sRegion, ",");
			StringList slSubRegion =  StringUtil.split(sSubRegion, ",");
			StringList slIntendedMarket = StringUtil.split(intendedMarket, ",");
			String sGetInWorkCriteria = getInWorkCriteria;
			String sGetInWorkCM = getInWorkCM;
			String sGetTargetLimitValue = getTargetLimitValues;

			Map<String,Object> mapQuery = new HashMap();
			mapQuery.put("Type",applicableType);
			mapQuery.put("Phase",slReleasePhase);
			mapQuery.put("BA",slBusinessArea);
			mapQuery.put("PCP",slPCP);
			mapQuery.put("PTP",slPTP);
			mapQuery.put("PTC",slPTC);
			mapQuery.put(PRODUCTSIZE,slProductSize);
			mapQuery.put(pgApolloConstants.STR_AUTOMATION_REGION,slRegion);
			mapQuery.put(pgApolloConstants.STR_AUTOMATION_SUBREGION,slSubRegion);
			mapQuery.put(INTENDEDMARKET,slIntendedMarket);
			mapQuery.put(GETINWORKCRITERIA,sGetInWorkCriteria);
			mapQuery.put(GETINWORKCM,sGetInWorkCM);
			mapQuery.put(GETTARGETLIMITVALUES,sGetTargetLimitValue);
			JSONArray jsonArr = new JSONArray();
			JSONObject jsonObject = new JSONObject();

			try {
				ml = CharacteristicsDashboardUtill.getReleasedCriterias(context, mapQuery,true);
				//Use convert maplist to json array from widget util
				jsonArr  = CharacteristicsDashboardUtill.convertMapListToJasonArray(ml);
				//Use generic response name
				jsonObject.put("CMData", jsonArr);
				res = Response.status(200).entity(jsonArr.toString()).build();
			} catch (Exception e) {
				res=Response.ok(e.getMessage()).type(APPLICATION_JASON).build();
				loggerApolloTrace.error(e.getMessage() ,e);
			}
			
		}

		return res;
	}
	
	@GET
	@Path("/pgGetComparedDatabyCriteriaID")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getComparedDataForCriteriaByID(@javax.ws.rs.core.Context HttpServletRequest request, 
			@QueryParam(ID1) String strCriteriaIDs1,
			@QueryParam(ID2) String strCriteriaIDs2,	
			@QueryParam(GETINWORKCM) String getInworkCMs,	
			@QueryParam(GETTARGETLIMITVALUES) String fetchTargetLimit
			) throws Exception	{
		
		matrix.db.Context context = getAuthenticatedContext(request, false);
		Response res = null;
		MapList ml = null;
		if(context == null && Framework.isLoggedIn(request)) {
			context = Framework.getContext(request.getSession(false));
		}
		StringList strCriteriaList1 =StringUtil.split(strCriteriaIDs1, pgApolloConstants.CONSTANT_STRING_PIPE);
		StringList strCriteriaList2 =StringUtil.split(strCriteriaIDs2, pgApolloConstants.CONSTANT_STRING_PIPE);
		Map mapQuery =new HashMap();
		mapQuery.put(ID1, strCriteriaList1);
		mapQuery.put(ID2, strCriteriaList2);
		mapQuery.put(GETINWORKCM,getInworkCMs);
		mapQuery.put(GETINWORKCRITERIA,"True");
		mapQuery.put(GETTARGETLIMITVALUES,fetchTargetLimit);
		
		JSONArray jsonArr = new JSONArray();
		JSONObject jsonObject = new JSONObject();

		try {
			ml = CharacteristicsDashboardUtill.getComparedResultForCriteriasByID(context, mapQuery);
			jsonArr  = CharacteristicsDashboardUtill.convertMapListToJasonArray(ml);
			jsonObject.put("CMData", jsonArr);
			res = Response.status(200).entity(jsonArr.toString()).build(); 
		} catch (Exception e) {
			res=Response.ok(e.getMessage()).type(APPLICATION_JASON).build();
			loggerApolloTrace.error(e.getMessage() ,e);
		}
		
		return res;
	}

	@POST
	@Path("/pgGetComparedDataForCriteria")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getComparedDataForCriteria(@javax.ws.rs.core.Context HttpServletRequest request, String strInputData) throws Exception	
	{
		matrix.db.Context context = getAuthenticatedContext(request, false);
		Response res = null;
		MapList ml = null;
		if(context == null && Framework.isLoggedIn(request)) {
			context = Framework.getContext(request.getSession(false));
		}
		
		//Removed input parameters and sent it using JSON value
		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInputData);	
		boolean bGetInworkCriteria = jsonInputData.getBoolean(GETINWORKCRITERIA);
		String getInworkCriteria = Boolean.toString(bGetInworkCriteria);
		boolean getInworkCMsJson = jsonInputData.getBoolean(GETINWORKCM);
		String getInworkCMs = Boolean.toString(getInworkCMsJson);
		boolean bFetchTargetLimit = jsonInputData.getBoolean(GETTARGETLIMITVALUES);
		String fetchTargetLimit = Boolean.toString(bFetchTargetLimit);
		JsonArray jsonInputArray = jsonInputData.getJsonArray("Criteria Attribute");
		
		JsonObject criteriaAttr1 = jsonInputArray.getJsonObject(0);
		JsonObject criteriaAttr2 = jsonInputArray.getJsonObject(1);

		String applicableType = criteriaAttr1.getString(APPLICABLETYPE);
		String phase = criteriaAttr1.getString(RELEASEPHASE);
		String businessArea = criteriaAttr1.getString(BUSINESSAREA);
		String pcp = criteriaAttr1.getString(PRODUCTCATEGORYPLATFORM);
		String ptp = criteriaAttr1.getString(PRODUCTTECHNOLOGYPLATFORM);
		String ptc = criteriaAttr1.getString(PRODUCTTECHNOLOGYCHASSIS);
		String productSize = criteriaAttr1.getString(PRODUCTSIZE);
		String sRegion = criteriaAttr1.getString(pgApolloConstants.STR_AUTOMATION_REGION);
		String sSubRegion = criteriaAttr1.getString(pgApolloConstants.STR_AUTOMATION_SUBREGION);
		String intendedMarket = criteriaAttr1.getString(INTENDEDMARKET);
		
		String phase2 = criteriaAttr2.getString(RELEASEPHASE);
		String businessArea2 = criteriaAttr2.getString(BUSINESSAREA);
		String pcp2 = criteriaAttr2.getString(PRODUCTCATEGORYPLATFORM);
		String ptp2 = criteriaAttr2.getString(PRODUCTTECHNOLOGYPLATFORM);
		String ptc2 = criteriaAttr2.getString(PRODUCTTECHNOLOGYCHASSIS);
		String productSize2 = criteriaAttr2.getString(PRODUCTSIZE);
		String sRegion2 = criteriaAttr2.getString(pgApolloConstants.STR_AUTOMATION_REGION);
		String sSubRegion2 = criteriaAttr2.getString(pgApolloConstants.STR_AUTOMATION_SUBREGION);
		String intendedMarket2 = criteriaAttr2.getString(INTENDEDMARKET);

		//Convert String Query Params to StringList For Multiple Value Selection 
		StringList slReleasePhase = StringUtil.split(phase, ",");
		StringList slBusinessArea = StringUtil.split(businessArea, ",");
		StringList slPCP = StringUtil.split(pcp, ",");
		StringList slPTP = StringUtil.split(ptp, ",");
		StringList slPTC = StringUtil.split(ptc, ",");
		StringList slProductSize = StringUtil.split(productSize, ",");
		StringList slRegion =  StringUtil.split(sRegion, ",");
		StringList slSubRegion =  StringUtil.split(sSubRegion, ",");
		StringList slIntendedMarket = StringUtil.split(intendedMarket, ",");

		StringList slReleasePhase2 = StringUtil.split(phase2, ",");
		StringList slBusinessArea2 = StringUtil.split(businessArea2, ",");
		StringList slPCP2 = StringUtil.split(pcp2, ",");
		StringList slPTP2 = StringUtil.split(ptp2, ",");
		StringList slPTC2 = StringUtil.split(ptc2, ",");
		StringList slProductSize2 = StringUtil.split(productSize2, ",");
		StringList slRegion2 =  StringUtil.split(sRegion2, ",");
		StringList slSubRegion2 =  StringUtil.split(sSubRegion2, ",");
		StringList slIntendedMarket2 = StringUtil.split(intendedMarket2, ",");

		Map<String,Object> mapQuery = new HashMap();
		mapQuery.put("Type",applicableType);
		mapQuery.put("Phase",slReleasePhase);
		mapQuery.put("BA",slBusinessArea);
		mapQuery.put("PCP",slPCP);
		mapQuery.put("PTP",slPTP);
		mapQuery.put("PTC",slPTC);
		mapQuery.put(PRODUCTSIZE,slProductSize);
		mapQuery.put(pgApolloConstants.STR_AUTOMATION_REGION,slRegion);
		mapQuery.put(pgApolloConstants.STR_AUTOMATION_SUBREGION,slSubRegion);
		mapQuery.put(INTENDEDMARKET,slIntendedMarket);
		mapQuery.put(GETINWORKCRITERIA,getInworkCriteria);
		mapQuery.put(GETINWORKCM,getInworkCMs);
		mapQuery.put(GETTARGETLIMITVALUES,fetchTargetLimit);
		Map<String,Object> mapQuery2 = new HashMap();
		mapQuery2.put("Type",applicableType);
		mapQuery2.put("Phase",slReleasePhase2);
		mapQuery2.put("BA",slBusinessArea2);
		mapQuery2.put("PCP",slPCP2);
		mapQuery2.put("PTP",slPTP2);
		mapQuery2.put("PTC",slPTC2);
		mapQuery2.put(PRODUCTSIZE,slProductSize2);
		mapQuery2.put(pgApolloConstants.STR_AUTOMATION_REGION,slRegion2);
		mapQuery2.put(pgApolloConstants.STR_AUTOMATION_SUBREGION,slSubRegion2);
		mapQuery2.put(INTENDEDMARKET,slIntendedMarket2);
		mapQuery2.put(GETINWORKCRITERIA,getInworkCriteria);
		mapQuery2.put(GETINWORKCM,getInworkCMs);
		mapQuery2.put(GETTARGETLIMITVALUES,fetchTargetLimit);		
		JSONArray jsonArr = new JSONArray();
		JSONObject jsonObject = new JSONObject();

		try {
			ml = CharacteristicsDashboardUtill.getComparedResultForCriterias(context, mapQuery,mapQuery2);
			jsonArr  = CharacteristicsDashboardUtill.convertMapListToJasonArray(ml);
			jsonObject.put("CMData", jsonArr);
			res = Response.status(200).entity(jsonArr.toString()).build(); 
		} catch (Exception e) {
			res=Response.ok(e.getMessage()).type(APPLICATION_JASON).build();
			loggerApolloTrace.error(e.getMessage() ,e);
		}
		return res;
	}
	
	@GET
	@Path("/pgUpdateRangesForFields")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getCMDataForCriteria(@javax.ws.rs.core.Context HttpServletRequest request,
			@QueryParam(ATTRIBUTE) String sAttribute,
			@QueryParam(INPUT) String sInput) throws Exception		
	{
		matrix.db.Context context = getAuthenticatedContext(request, false);
		Response res = null;
		if(context == null && Framework.isLoggedIn(request)) {
			context = Framework.getContext(request.getSession(false));
		}
		Map<String,Object> mapQuery = new HashMap();
		mapQuery.put(ATTRIBUTE,sAttribute);
		mapQuery.put(INPUT,sInput);

		JSONObject jsonObject = new JSONObject();

		try {
			jsonObject = CharacteristicsDashboardUtill.getRangeValuesForDashboard(context, mapQuery);
			res = Response.status(200).entity(jsonObject.toString()).build();
		} catch (Exception e) {
			res=Response.ok(e.getMessage()).type(APPLICATION_JASON).build();
			loggerApolloTrace.error(e.getMessage() ,e);
		}
		return res; 
	}
}

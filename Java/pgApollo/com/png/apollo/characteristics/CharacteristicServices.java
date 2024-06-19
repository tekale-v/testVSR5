/*
 * Added by APOLLO Team
 * For Characteristic Related Web services
 */

package com.png.apollo.characteristics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.dassault_systemes.enovia.characteristic.interfaces.ENOCharacteristicEnum.CharacteristicAttributes;
import com.dassault_systemes.enovia.characteristic.interfaces.ENOCharacteristicServices;
import com.dassault_systemes.enovia.characteristic.restapp.util.ENOCharacteristicsRestAppUtil;
import com.dassault_systemes.enovia.characteristic.util.CharacteristicMasterUtil;
import com.dassault_systemes.enovia.characteristic.util.JsonUtil;
import com.dassault_systemes.enovia.criteria.util.CriteriaUtil;
import com.dassault_systemes.platform.restServices.RestService;
import com.matrixone.apps.common.CommonDocument;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.png.apollo.pgApolloConstants;

import matrix.util.Pattern;
import matrix.util.StringList;

@Path("CharacteristicServices")
public class CharacteristicServices extends RestService {

	 private static final org.apache.log4j.Logger loggerApolloTrace = org.apache.log4j.Logger.getLogger(CharacteristicServices.class);

	protected CacheControl cacheControl;

	public CharacteristicServices() {
		this.cacheControl = new CacheControl();
		this.cacheControl.setNoCache(true);
		this.cacheControl.setNoStore(true);
	}


	/**
	 * Method to get All Characteristics of Part
	 * @param request
	 * @param sPartId
	 * @param isPID
	 * @return
	 * @throws Exception
	 */
	@Path("/getall")
	@GET
	@Produces({"application/json", "application/ds-json"})
	public Response getAllCharacteritics(@Context HttpServletRequest request, @QueryParam("objectId") String sPartId,
			@QueryParam("isPID") boolean isPID) throws Exception {
		matrix.db.Context context = null;

		Response response;
		try {
			context = this.getAuthenticatedContext(request,false);
			ENOCharacteristicsRestAppUtil.checkCSRFToken(request);
			StringList slPartSelects = new StringList();
			HashMap mapResponse = new HashMap();
			if (isPID) {
				sPartId = MqlUtil.mqlCommand(context, "print bus $1 select $2 dump", new String[]{sPartId, "id"});
				mapResponse.put("PID", sPartId);
			}

			slPartSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_V_NAME);
			slPartSelects.add(DomainConstants.SELECT_NAME);
			slPartSelects.add(DomainConstants.SELECT_CURRENT);
			mapResponse.put("M1ObjectId", sPartId);
			DomainObject domPartObject = DomainObject.newInstance(context, sPartId);
			Map mapPart = domPartObject.getInfo(context, slPartSelects);
			if (isPID) {
				mapResponse.put(DomainConstants.ATTRIBUTE_TITLE, mapPart.get(pgApolloConstants.SELECT_ATTRIBUTE_V_NAME));
			} else {
				mapResponse.put(DomainConstants.ATTRIBUTE_TITLE, mapPart.get(DomainConstants.SELECT_NAME));
			}

			mapResponse.put(DomainConstants.SELECT_CURRENT, mapPart.get(DomainConstants.SELECT_CURRENT));
			MapList mapCharList = ENOCharacteristicServices.getAllCharacteristicDetailsOnItem(context, sPartId);

			mapCharList = addCustomColumnsOfCharacteristics(context, mapCharList, sPartId);

			mapResponse.put("Characteristics", mapCharList);
			MapList mlResponse = new MapList();
			mlResponse.add(mapResponse);
			ResponseBuilder responseBuilder = Response.ok(CharacteristicMasterUtil.transformToJSON(mlResponse), "application/json")
					.cacheControl(this.cacheControl);
			this.setResponseHeader(responseBuilder);
			response = responseBuilder.build();
			
		} catch (Exception ex) {
			response = this.setErrorResponse(ex);
		}

		return response;
	}
	
	@Path("/evaluatecriteria")
	@POST
	@Produces({"application/json", "application/ds-json"})
	public Response evaluateCriteria(@Context HttpServletRequest request, @FormParam("objectId") String sObjectId)
			throws Exception {
		Response response = null;
		matrix.db.Context context = null;

		try {
			context = this.getAuthenticatedContext(request, false);
			ENOCharacteristicsRestAppUtil.checkCSRFToken(request);
			Map mapChar = ENOCharacteristicServices.createCharacteristicOnItemBasedOnCriteria(context, sObjectId);
			Set setNewChar = (Set) mapChar.get("New_Characteristics");
			Set setCharModified = (Set) mapChar.get("Characteristic_Modified");
			Set setParameterAggregatedModified = (Set) mapChar.get("ParaAggrRel_Modified");
			new MapList();
			HashMap mapOutput = new HashMap();
			MapList mlChar;
			if (setCharModified.isEmpty() && setParameterAggregatedModified.isEmpty() && !setNewChar.isEmpty()) {
				ArrayList listNewChar = new ArrayList();
				listNewChar.addAll(setNewChar);
				mlChar = ENOCharacteristicServices.getCharacteristicDetailsForUI(context, sObjectId, listNewChar);
				mlChar = addCustomColumnsOfCharacteristics(context, mlChar, sObjectId);
				mapOutput.put("NewCharacteristics", mlChar);
				mapOutput.put("NewCharacteristicsCount", setNewChar.size());
			} else if (!setCharModified.isEmpty() || !setParameterAggregatedModified.isEmpty()) {
				mlChar = ENOCharacteristicServices.getAllCharacteristicDetailsOnItem(context, sObjectId);
				mlChar = addCustomColumnsOfCharacteristics(context, mlChar, sObjectId);
				mapOutput.put("Characteristics", mlChar);
				mapOutput.put("NewCharacteristicsCount", setNewChar.size());
				mapOutput.put("CharacteristicModifiedCount", setCharModified.size());
			}

			if (!mapOutput.isEmpty()) {
				ResponseBuilder responseBuilder = Response.ok(ENOCharacteristicsRestAppUtil.transformToJSON(mapOutput))
						.cacheControl(this.cacheControl);
				this.setResponseHeader(responseBuilder);
				response = responseBuilder.build();
			}
		} catch (Exception exception) {
			response = this.setErrorResponse(exception);
		}

		return response;
	}
	
	
	@Path("/createcharacteristiconitembasedonmaster")
	@POST
	@Consumes({"application/json", "application/ds-json"})
	@Produces({"application/json", "application/ds-json"})
	public Response createCharacteristicOnItemBasedOnMaster(@Context HttpServletRequest request, String sInput)
			throws Exception {
		matrix.db.Context context = this.getAuthenticatedContext(request, false);
		ENOCharacteristicsRestAppUtil.checkCSRFToken(request);
		Response response = null;
		String sObjectId = null;
		JsonArray cmJsonArray = null;
		new StringList();
		ContextUtil.startTransaction(context, true);

		try {
			JsonObject jsonObject = JsonUtil.toJsonObject(sInput);
			sObjectId = jsonObject.getString("objectId");
			cmJsonArray = jsonObject.getJsonArray("charMasterIds");
			String[] cmArray = new String[cmJsonArray.size()];

			for (int index = 0; index < cmJsonArray.size(); ++index) {
				JsonObject jsonObject2 = cmJsonArray.getJsonObject(index);
				String mqlOutput = MqlUtil.mqlCommand(context, "print bus $1 select $2 dump",
						new String[]{jsonObject2.getString("masterId"), "id"});
				cmArray[index] = mqlOutput;
			}

			StringList slCMCharList = com.dassault_systemes.enovia.characteristic.impl.CharacteristicServices.createCharacteristicsFromMasters(context, sObjectId, cmArray, false, true);
			ContextUtil.commitTransaction(context);
			MapList mlOutput = ENOCharacteristicServices.getCharacteristicDetailsForUI(context, sObjectId, slCMCharList);
			mlOutput = addCustomColumnsOfCharacteristics(context, mlOutput, sObjectId);
			response = Response.status(200).entity(ENOCharacteristicsRestAppUtil.transformToJSON(mlOutput)).build();
		} catch (Exception exception) {
			response = this.setErrorResponse(exception);
			if (context != null) {
				ContextUtil.abortTransaction(context);
			}
		}

		return response;
	}

	
	/**
	 * Method to set Response Header
	 * @param responseBuilder
	 */
	private void setResponseHeader(ResponseBuilder responseBuilder) {
		responseBuilder.header("X-Content-Type-Options", "nosniff");
		responseBuilder.header("Pragma", "no-cache");
	}

	/**
	 * Method to add custom columns for Characteristics
	 * @param context
	 * @param mapCharList
	 * @param sPartId
	 * @return
	 * @throws Exception
	 */
	public static MapList addCustomColumnsOfCharacteristics(matrix.db.Context context, MapList mapCharList, String sPartId) throws Exception 
	{	
		MapList mapCharOutput = new MapList();

		if(null != mapCharList && !mapCharList.isEmpty())
		{
			DomainObject domPartObject = DomainObject.newInstance(context, sPartId);

			Map mapCharIdToCMCriteria = UpdateCharacteristicAttributes.getFilteredCMCriteriaMap(context, domPartObject);					

			if(null == mapCharIdToCMCriteria)
			{
				mapCharIdToCMCriteria = new HashMap();
			}

			Map mapCMCriteriaForEachChar;

			Map mapObject;
			String sCharId;		

			StringList slTitle;
			StringList slCriteriaHyperLink = null;
			String strCharMasterName;
			String strCharMasterNameHyperlink;
			String strCharMasterRev;

			Map localMapObject;
			MapList mlLocalMap;

			for(Object object : mapCharList)
			{
				mapObject = (Map)object;				
				sCharId = (String)mapObject.get(DomainConstants.SELECT_ID);	

				mapObject.put("TestMethodReferenceDocumentName", getConnectedTestMethodReferenceDocuments(context, sCharId));

				slTitle = new StringList();
				slCriteriaHyperLink = new StringList();
				strCharMasterName = DomainConstants.EMPTY_STRING;
				strCharMasterNameHyperlink  = DomainConstants.EMPTY_STRING;
				strCharMasterRev  = DomainConstants.EMPTY_STRING;

				if(!mapCharIdToCMCriteria.isEmpty() && mapCharIdToCMCriteria.containsKey(sCharId))
				{						
					mapCMCriteriaForEachChar = (Map)mapCharIdToCMCriteria.get(sCharId);
					slTitle = (StringList)mapCMCriteriaForEachChar.get("CriteriaTitleList");
					slCriteriaHyperLink = (StringList)mapCMCriteriaForEachChar.get("CriteriaHyperLinkList");
					strCharMasterNameHyperlink = (String)mapCMCriteriaForEachChar.get("CMHyperLink");
					strCharMasterName = (String)mapCMCriteriaForEachChar.get("CMName");
					strCharMasterRev = (String)mapCMCriteriaForEachChar.get("CMRevision");					
				}

				localMapObject = new HashMap();
				localMapObject.put(DomainConstants.SELECT_NAME, strCharMasterName);
				localMapObject.put(pgApolloConstants.STR_VALUE, strCharMasterNameHyperlink);

				mlLocalMap = new MapList();
				mlLocalMap.add(localMapObject);

				mapObject.put("pgCharacteristicMaster", mlLocalMap);

				localMapObject = new HashMap();
				localMapObject.put(DomainConstants.SELECT_NAME, strCharMasterRev);
				localMapObject.put(pgApolloConstants.STR_VALUE, strCharMasterRev);

				mlLocalMap = new MapList();
				mlLocalMap.add(localMapObject);

				mapObject.put("pgCharacteristicMasterRevision", mlLocalMap);				

				localMapObject = new HashMap();
				localMapObject.put(DomainConstants.SELECT_NAME, StringUtil.join(slTitle, pgApolloConstants.CONSTANT_STRING_PIPE));
				localMapObject.put(pgApolloConstants.STR_VALUE, StringUtil.join(slCriteriaHyperLink, pgApolloConstants.CONSTANT_STRING_PIPE));

				mlLocalMap = new MapList();
				mlLocalMap.add(localMapObject);

				mapObject.put("pgCriteria", mlLocalMap);

				mapCharOutput.add(mapObject);
			}
		}		
		return mapCharOutput;
	}


	@Path("/getassociatedtestmethodReferenceDocument")
	@GET
	@Produces({"application/json", "application/ds-json"})
	public Response getConnectedTMRDs(@javax.ws.rs.core.Context HttpServletRequest request, @QueryParam("objectId") String sCharId)
			throws Exception {
		Response response = null;
		StringBuilder sb = new StringBuilder();

		try {
			matrix.db.Context context = getAuthenticatedContext(request, false);
			MapList mlOutput = new MapList();
			MapList mlTMRD = getConnectedTestMethodReferenceDocuments(context, sCharId);
			Iterator iterator = mlTMRD.iterator();

			Object object;
			Map mapTMRD;

			while (iterator.hasNext()) {
				object = iterator.next();
				mapTMRD = (Map) object;
				String sType = (String) mapTMRD.get(DomainConstants.SELECT_TYPE);
				String sTypeDisplayName = EnoviaResourceBundle.getAdminI18NString(context, "Type", sType,
						context.getLocale().getLanguage());
				mapTMRD.put(DomainConstants.SELECT_TYPE, sTypeDisplayName);
				String sPolicy = (String) mapTMRD.get(DomainConstants.SELECT_POLICY);
				String sPolicyDisplayName = EnoviaResourceBundle.getAdminI18NString(context, "Policy", sPolicy,
						context.getLocale().getLanguage());
				mapTMRD.put(DomainConstants.SELECT_POLICY, sPolicyDisplayName);
				if (mapTMRD.containsKey("from[Active Version].to.attribute[Title]")) {
					mapTMRD.put("documentTitle", mapTMRD.remove("from[Active Version].to.attribute[Title]"));
					mapTMRD.put("documentId", mapTMRD.remove("from[Active Version].to.id"));
					mlOutput.add(mapTMRD);
				} else {
					mlOutput.add(mapTMRD);
				}
			}

			sb.append(CharacteristicMasterUtil.transformToJSON(mlOutput));
			response = Response.status(200).entity(sb.toString()).cacheControl(this.cacheControl).build();
		} catch (Exception ex) {
			response = this.setErrorResponse(ex);
		}
		return response;
	}


	/**
	 * Method to get connected Test Method Reference Documents
	 * @param context
	 * @param objectId
	 * @return
	 * @throws FrameworkException 
	 * @throws Exception
	 */
	public static MapList getConnectedTestMethodReferenceDocuments(matrix.db.Context context, String objectId) throws FrameworkException {

		MapList returnMapList;
		
		 Pattern typePattern = new Pattern(pgApolloConstants.TYPE_PG_STANDARD_OPERATING_PROCEDURE);
	     typePattern.addPattern(pgApolloConstants.TYPE_PG_ILLUSTRATION);
	     typePattern.addPattern(pgApolloConstants.TYPE_PG_QUALITY_SPECIFICATION);
	     typePattern.addPattern(pgApolloConstants.TYPE_TEST_METHOD_SPECIFICATION);
	     typePattern.addPattern(pgApolloConstants.TYPE_PG_TEST_METHOD);

		DomainObject domObj = DomainObject.newInstance(context, objectId);

		StringList objSelectables = StringList.create(DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME, DomainConstants.ATTRIBUTE_TITLE, DomainConstants.SELECT_REVISION,
				DomainConstants.SELECT_OWNER, DomainConstants.SELECT_DESCRIPTION,  DomainConstants.SELECT_CURRENT, DomainConstants.SELECT_TYPE, DomainConstants.SELECT_POLICY, DomainConstants.SELECT_ORIGINATED, "latest", pgApolloConstants.SELECT_PHYSICAL_ID,
				CriteriaUtil.stringConcat(CharacteristicAttributes.TITLE.getAttributeSelect(context)),
				CriteriaUtil.stringConcat("from[", CommonDocument.RELATIONSHIP_ACTIVE_VERSION, "].to.",
						CharacteristicAttributes.TITLE.getAttributeSelect(context)),
				CriteriaUtil.stringConcat("from[", CommonDocument.RELATIONSHIP_ACTIVE_VERSION, "].to.", "id"));
		StringList relSelects = StringList.create(DomainRelationship.SELECT_ID);

		returnMapList = domObj.getRelatedObjects(context, // Context
				DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT, // Relationship pattern
				typePattern.getPattern(), // Type Pattern
				objSelectables, // Object Select
				relSelects, // Relationship Select
				false,	// From side
				true, // to side
				(short) 1, // recursion level
				"", // Object Where Clause
				"", // Relationship Where Clause
				0); // Object Limit
		return returnMapList;
	}

	/**
	 * Method to set Error Response
	 * @param paramException
	 * @return
	 */
	private Response setErrorResponse(Exception paramException)
	{
		JsonObjectBuilder localJsonObjectBuilder = Json.createObjectBuilder();
		loggerApolloTrace.error(paramException.getMessage() ,paramException);
		try
		{
			localJsonObjectBuilder.add("message", paramException.getMessage());
		}
		catch (Exception localException)
		{
			loggerApolloTrace.error(paramException.getMessage() ,paramException);
		}
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(localJsonObjectBuilder.build().toString()).cacheControl(this.cacheControl).build();
	}


}

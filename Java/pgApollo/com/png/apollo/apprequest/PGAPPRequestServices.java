package com.png.apollo.apprequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import com.dassault_systemes.evp.messaging.utils.UIUtil;
import com.dassault_systemes.platform.ven.jackson.databind.ObjectMapper;
import com.dassault_systemes.platform.ven.jackson.databind.node.ArrayNode;
import com.dassault_systemes.platform.ven.jackson.databind.node.ObjectNode;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.MapList;
import com.png.apollo.pgApolloCommonUtil;
import com.png.apollo.pgApolloConstants;

import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class PGAPPRequestServices {

	 private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(PGAPPRequestServices.class);

	 private static final int APP_REQUEST_CUSTOM_ERROR_CODE = 540;

	 private static final ObjectMapper objectMapper = new ObjectMapper();
	 
	/**
	 * Method to create APP Request
	 * @param context
	 * @param sInput
	 * @return
	 * @throws Exception 
	 */
	public static Response createAPPRequest(Context context, String sInput) throws Exception 
	{
		return createAPPRequest(context, sInput, false);
	}
	
	/**
	 * Method to create APP Request with Integration API Support
	 * @param context
	 * @param sInput
	 * @return
	 * @throws Exception 
	 */
	public static Response createAPPRequest(Context context, String sInput, boolean bIntegrationAPI) throws Exception 
	{
        ObjectNode jsonOutput = objectMapper.createObjectNode();
		int iStatusCode = HttpServletResponse.SC_OK;

		Map mapInput = new HashMap();
		MapList mlOutput = new MapList();		
		Map mapOutput = new HashMap();
		String sError;
		boolean bError = false;

		try
		{
			//Convert JSON to Map
			mapInput = pgApolloCommonUtil.getStringMapBasedOnJsonArray(sInput);
			
			if(bIntegrationAPI)
			{
				mapOutput = PGAPPRequestUtil.validateCreatePayload(context,mapInput);
				
				if(mapOutput.containsKey(pgApolloConstants.STR_ERROR))
				{
					bError = true;
					jsonOutput.put("data", objectMapper.createArrayNode());				
				}
				
			}
			
			if(!bError)
			{
				mapOutput = PGAPPRequestUtil.createAPPRequests(context, mapInput);		
				
				mlOutput = (MapList)mapOutput.get("data");
				
				if(null != mlOutput)
				{
					if(bIntegrationAPI)
					{
						mlOutput = PGAPPRequestUtil.getFilteredDataForAPIIntegration(context, mlOutput);
					}
					
					ArrayNode jsonOutputArray = pgApolloCommonUtil.convertMapListToJsonArray(mlOutput);
					jsonOutput.put("data", jsonOutputArray);				
				}
			}	
			
			if(mapOutput.containsKey(PGAPPRequestConstants.KEY_RESPONSEKEY_STATUS))
			{
				String sStatus = (String)mapOutput.get(PGAPPRequestConstants.KEY_RESPONSEKEY_STATUS);
				jsonOutput.put(PGAPPRequestConstants.KEY_RESPONSEKEY_STATUS, sStatus);
			}
			
			if(mapOutput.containsKey(pgApolloConstants.STR_ERROR))
			{
				sError = (String)mapOutput.get(pgApolloConstants.STR_ERROR);
				jsonOutput.put(pgApolloConstants.KEY_MESSAGE, sError);
				iStatusCode = APP_REQUEST_CUSTOM_ERROR_CODE;
			}
			else
			{
				jsonOutput.put(pgApolloConstants.KEY_MESSAGE, pgApolloConstants.STR_SUCCESS);
			}		
			
		} catch (MatrixException e) {
			logger.error(e.getMessage(), e);
			iStatusCode = APP_REQUEST_CUSTOM_ERROR_CODE;
			jsonOutput.put(PGAPPRequestConstants.KEY_RESPONSEKEY_STATUS, pgApolloConstants.STR_FAILED);
			sError = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage()).toString();
			jsonOutput.put(pgApolloConstants.KEY_MESSAGE, sError);

		}

		String sResponse = objectMapper.writeValueAsString(jsonOutput);

		return Response.status(iStatusCode).entity(sResponse).build();
	}
	
	
	/**
	 * Method to update APP Request
	 * @param context
	 * @param sInput
	 * @return
	 * @throws Exception 
	 */
	public static Response updateAPPRequest(Context context, String sInput) throws Exception 
	{
		return updateAPPRequest(context, sInput, false);
	}
	
	/**
	 * Method to update APP Request with Integration API flag
	 * @param context
	 * @param sInput
	 * @return
	 * @throws Exception 
	 */
	public static Response updateAPPRequest(Context context, String sInput, boolean bIntegrationAPI) throws Exception 
	{
		ObjectNode jsonOutput = objectMapper.createObjectNode();;
		
		Map mapInput = new HashMap();
		MapList mlOutput = new MapList();
		Map mapOutput = new HashMap();
		String sError;
		int iStatusCode = HttpServletResponse.SC_OK;
		boolean bError = false;
		
		try {			
		
			mapInput = pgApolloCommonUtil.getStringMapBasedOnJsonArray(sInput);
			
			if(bIntegrationAPI)
			{
				mapOutput = PGAPPRequestUtil.validateUpdatePayload(context,mapInput);
				
				if(mapOutput.containsKey(pgApolloConstants.STR_ERROR))
				{
					bError = true;
					jsonOutput.put("data", objectMapper.createArrayNode());				
				}
				
			}
				
			
			if(!bError)
			{
				mapOutput = PGAPPRequestUtil.updateAPPRequests(context, mapInput);		
				
				mlOutput = (MapList)mapOutput.get("data");
				
				if(null != mlOutput)
				{
					if(bIntegrationAPI)
					{
						mlOutput = PGAPPRequestUtil.getFilteredDataForAPIIntegration(context, mlOutput);
					}
					
					ArrayNode jsonOutputArray = pgApolloCommonUtil.convertMapListToJsonArray(mlOutput);
					jsonOutput.put("data", jsonOutputArray);				
				}
			}
			
			if(mapOutput.containsKey(PGAPPRequestConstants.KEY_RESPONSEKEY_STATUS))
			{
				String sStatus = (String)mapOutput.get(PGAPPRequestConstants.KEY_RESPONSEKEY_STATUS);
				jsonOutput.put(PGAPPRequestConstants.KEY_RESPONSEKEY_STATUS, sStatus);
			}
			
			if(mapOutput.containsKey(pgApolloConstants.STR_ERROR))
			{
				sError = (String)mapOutput.get(pgApolloConstants.STR_ERROR);
				jsonOutput.put(pgApolloConstants.KEY_MESSAGE, sError);
				iStatusCode = APP_REQUEST_CUSTOM_ERROR_CODE;
			}
			else
			{
				jsonOutput.put(pgApolloConstants.KEY_MESSAGE, pgApolloConstants.STR_SUCCESS);
			}		

			
		} catch (MatrixException e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
			iStatusCode = APP_REQUEST_CUSTOM_ERROR_CODE;
			sError = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage()).toString();
			jsonOutput.put(pgApolloConstants.KEY_MESSAGE, sError);
			jsonOutput.put(PGAPPRequestConstants.KEY_RESPONSEKEY_STATUS, pgApolloConstants.STR_FAILED);

		}
		
		String sResponse = objectMapper.writeValueAsString(jsonOutput);

		return Response.status(iStatusCode).entity(sResponse).build();
	}
	
	
	/**
	 * Method to get APP Requests
	 * @param context
	 * @param sInput
	 * @return
	 * @throws Exception 
	 */
	public static Response getAPPRequests(Context context, String sInput) throws Exception 
	{
		return getAPPRequests(context, sInput, false);
	}
	
	/**
	 * Method to get APP Requests with Integration API support
	 * @param context
	 * @param sInput
	 * @return
	 * @throws Exception 
	 */
	public static Response getAPPRequests(Context context, String sInput, boolean bIntegrationAPI) throws Exception 
	{
		ObjectNode jsonOutput = objectMapper.createObjectNode();;
		MapList mlOutput = new MapList();
		int iStatusCode = HttpServletResponse.SC_OK;

		try {
			
			Map mapFilterCriteria = new HashMap();
			
			if(UIUtil.isNotNullAndNotEmpty(sInput))
			{
				mapFilterCriteria = pgApolloCommonUtil.getStringMapBasedOnJson(sInput);		
			}			
			
			if(!mapFilterCriteria.isEmpty())
			{
				boolean bFilterValidCriteria = PGAPPRequestUtil.validateFilterCriteria(context, mapFilterCriteria);
				
				if(bFilterValidCriteria)
				{
					mlOutput = PGAPPRequestUtil.getAPPRequests(context, mapFilterCriteria);	
					
					if(bIntegrationAPI)
					{
						mlOutput = PGAPPRequestUtil.getFilteredDataForAPIIntegration(context, mlOutput);
					}
				}
				else
				{
					return Response.status(Response.Status.BAD_REQUEST).entity(PGAPPRequestConstants.STR_ERROR_INVALIDKEYS_GETAPPREQUEST).build();
				}
				
			}
		
		
			
			ArrayNode jsonOutputArray = pgApolloCommonUtil.convertMapListToJsonArray(mlOutput);
			
			jsonOutput.put("data", jsonOutputArray);
			jsonOutput.put(pgApolloConstants.KEY_MESSAGE, pgApolloConstants.STR_SUCCESS);
		
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			String sError = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage()).toString();
			jsonOutput.put(pgApolloConstants.KEY_MESSAGE, sError);
		}
		
		String sResponse = objectMapper.writeValueAsString(jsonOutput);

		return Response.status(iStatusCode).entity(sResponse).build();
	}
		

	/**
	 * Method to get APP Requests
	 * @param context
	 * @param sInput 
	 * @param sInput
	 * @return
	 * @throws Exception 
	 */
	public static Response getAllAPPRequests(Context context, String sInput) throws Exception 
	{
		
		context.printTrace(pgApolloConstants.TRACE_LPD, "--------------------------------------------------------------------");

        ObjectNode jsonOutput = objectMapper.createObjectNode();

        long lTime2 = 0;
		MapList mlOutput = new MapList();
		int iStatusCode = HttpServletResponse.SC_OK;

		try {
		
			mlOutput = PGAPPRequestUtil.getAllAPPRequests(context);		
			
			long lTime1 = System.currentTimeMillis();
			
			ArrayNode jsonOutputArray = pgApolloCommonUtil.convertMapListToJsonArray(mlOutput);
			
			lTime2 = System.currentTimeMillis();

			context.printTrace(pgApolloConstants.TRACE_LPD, "For Conversion of MapList to JSON :"+(lTime2 - lTime1));

			jsonOutput.put("data", jsonOutputArray);
			jsonOutput.put(pgApolloConstants.KEY_MESSAGE, pgApolloConstants.STR_SUCCESS);

		
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			String sError = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage()).toString();
			jsonOutput.put(pgApolloConstants.KEY_MESSAGE, sError);
		}
		
		String sResponse = objectMapper.writeValueAsString(jsonOutput);
		
		long lTime3 = System.currentTimeMillis();

		context.printTrace(pgApolloConstants.TRACE_LPD, "For Conversion of ObjectNode to String :"+(lTime3 - lTime2));
		
		context.printTrace(pgApolloConstants.TRACE_LPD, "--------------------------------------------------------------------");

		return Response.status(iStatusCode).entity(sResponse).build();
	}
	
	
	/**
	 * Method to get APP Requests
	 * @param context
	 * @param sInput
	 * @return
	 * @throws Exception 
	 */
	public static Response getAPPRequest(Context context, String sRequestId) throws Exception 
	{
		return getAPPRequest(context, sRequestId, false); 
	}
	
	/**
	 * Method to get APP Requests with Integration API support
	 * @param context
	 * @param sInput
	 * @return
	 * @throws Exception 
	 */
	public static Response getAPPRequest(Context context, String sRequestId, boolean bIntegrationAPI) throws Exception 
	{
		ObjectNode jsonOutput = objectMapper.createObjectNode();;

		MapList mlOutput = new MapList();
		int iStatusCode = HttpServletResponse.SC_OK;

		try {
			
			if(UIUtil.isNotNullAndNotEmpty(sRequestId))
			{
				Map mapObject = pgApolloCommonUtil.getLatestRevisionMap(context, PGAPPRequestConstants.TYPE_PGAPPREQUEST, sRequestId, pgApolloConstants.CONSTANT_STRING_HYPHEN);		
				String sAPPRequestPhysicalId = (String)mapObject.get(DomainConstants.SELECT_PHYSICAL_ID);

				if(UIUtil.isNotNullAndNotEmpty(sAPPRequestPhysicalId))
				{
					mlOutput = PGAPPRequestUtil.getRequestDetails(context, new StringList(sAPPRequestPhysicalId));
					
					if(bIntegrationAPI)
					{
						mlOutput = PGAPPRequestUtil.getFilteredDataForAPIIntegration(context, mlOutput);
					}

					ArrayNode jsonOutputArray = pgApolloCommonUtil.convertMapListToJsonArray(mlOutput);

					jsonOutput.put("data", jsonOutputArray);
					jsonOutput.put(pgApolloConstants.KEY_MESSAGE, pgApolloConstants.STR_SUCCESS);			

				}
				else
				{
					jsonOutput.put(pgApolloConstants.KEY_MESSAGE, PGAPPRequestConstants.STR_ERROR_OBJECT_NOT_FOUND);
					iStatusCode = APP_REQUEST_CUSTOM_ERROR_CODE;

				}		
				
			}
			else
			{
				jsonOutput.put(pgApolloConstants.KEY_MESSAGE, pgApolloConstants.STR_AUTOMATION_ERROR_INVALID_PARAMETERS);
				iStatusCode = APP_REQUEST_CUSTOM_ERROR_CODE;

			}


		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			String sError = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage()).toString();
			jsonOutput.put(pgApolloConstants.KEY_MESSAGE, sError);
		}
			
		String sResponse = objectMapper.writeValueAsString(jsonOutput);

		return Response.status(iStatusCode).entity(sResponse).build();
	}
	
	
	/**
	 * Method to get APP Requests
	 * @param context
	 * @param sInput
	 * @return
	 * @throws Exception 
	 */
	public static Response getAPPRequestData(Context context, String sRequestSetId) throws Exception 
	{
		return getAPPRequestData(context, sRequestSetId, false);
	}
	
	/**
	 * Method to get APP Requests with Integration API support
	 * @param context
	 * @param sInput
	 * @return
	 * @throws Exception 
	 */
	public static Response getAPPRequestData(Context context, String sRequestSetId, boolean bIntegrationAPI) throws Exception 
	{
		ObjectNode jsonOutput = objectMapper.createObjectNode();;
		MapList mlOutput = new MapList();
		int iStatusCode = HttpServletResponse.SC_OK;

		try {
			
			if(UIUtil.isNotNullAndNotEmpty(sRequestSetId))
			{
				Map mapObject = pgApolloCommonUtil.getLatestRevisionMap(context, PGAPPRequestConstants.TYPE_PGAPPREQUESTDATA, sRequestSetId, pgApolloConstants.CONSTANT_STRING_HYPHEN);	
				
				String sAPPRequestDataPhysicalId = (String)mapObject.get(DomainConstants.SELECT_PHYSICAL_ID);
				
				if(UIUtil.isNotNullAndNotEmpty(sAPPRequestDataPhysicalId))
				{
					mlOutput = PGAPPRequestUtil.getRequestDataDetails(context, new StringList(sAPPRequestDataPhysicalId));	
					
					if(bIntegrationAPI)
					{
						mlOutput = PGAPPRequestUtil.getFilteredDataForAPIIntegration(context, mlOutput);
					}

					ArrayNode jsonOutputArray = pgApolloCommonUtil.convertMapListToJsonArray(mlOutput);

					jsonOutput.put("data", jsonOutputArray);
					jsonOutput.put(pgApolloConstants.KEY_MESSAGE, pgApolloConstants.STR_SUCCESS);

				}
				else
				{
					jsonOutput.put(pgApolloConstants.KEY_MESSAGE, PGAPPRequestConstants.STR_ERROR_OBJECT_NOT_FOUND);
					iStatusCode = APP_REQUEST_CUSTOM_ERROR_CODE;

				}

			}
			else
			{
				jsonOutput.put(pgApolloConstants.KEY_MESSAGE, pgApolloConstants.STR_AUTOMATION_ERROR_INVALID_PARAMETERS);
				iStatusCode = APP_REQUEST_CUSTOM_ERROR_CODE;

			}
						
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			String sError = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage()).toString();
			jsonOutput.put(pgApolloConstants.KEY_MESSAGE, sError);
		}
		
		
		String sResponse = objectMapper.writeValueAsString(jsonOutput);

		return Response.status(iStatusCode).entity(sResponse).build();
	}
	
	
	/**
	 * Method to get EBOM Details
	 * @param context
	 * @param sInput
	 * @return
	 */
	public static Response getEBOMDetails(Context context, String sInput) throws Exception
	{
		ObjectNode jsonOutput = objectMapper.createObjectNode();;
		Map mapOutput = new HashMap();
		int iStatusCode = HttpServletResponse.SC_OK;

		try {
			
			Map mapInput = pgApolloCommonUtil.getStringMapBasedOnJsonArray(sInput);
			MapList mlOutput = PGAPPRequestUtil.getEBOMDetails(context, mapInput);		

			if(null != mlOutput && !mlOutput.isEmpty())
			{
				ArrayNode jsonOutputArray = pgApolloCommonUtil.convertMapListToJsonArray(mlOutput);
				jsonOutput.put("data", jsonOutputArray);	
				jsonOutput.put(pgApolloConstants.KEY_MESSAGE, pgApolloConstants.STR_SUCCESS);

			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			String sError = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage()).toString();
			jsonOutput.put(pgApolloConstants.KEY_MESSAGE, sError);
		}
		
		String sResponse = objectMapper.writeValueAsString(jsonOutput);

		return Response.status(iStatusCode).entity(sResponse).build();
			
	}


	
	/**
	 * Method to implement APP Request
	 * @param context
	 * @param sInput
	 * @return
	 * @throws Exception 
	 */
	public static Response implementAPPRequest(Context context, String sInput) throws Exception 
	{
		ObjectNode jsonOutput = objectMapper.createObjectNode();;
		
		Map mapInput = new HashMap();
		MapList mlOutput = new MapList();
		Map mapOutput = new HashMap();
		String sError;
		int iStatusCode = HttpServletResponse.SC_OK;

		try {
			mapInput = pgApolloCommonUtil.getStringMapBasedOnJsonArray(sInput);
						
			mapOutput = PGAPPRequestImplementation.markRequestForImplementation(context, mapInput);		
			
			mlOutput = (MapList)mapOutput.get("data");
			
			if(null != mlOutput)
			{
				ArrayNode jsonOutputArray = pgApolloCommonUtil.convertMapListToJsonArray(mlOutput);
				jsonOutput.put("data", jsonOutputArray);				
			}
			
			if(mapOutput.containsKey(pgApolloConstants.STR_ERROR))
			{
				sError = (String)mapOutput.get(pgApolloConstants.STR_ERROR);
				jsonOutput.put(pgApolloConstants.KEY_MESSAGE, sError);
				iStatusCode = APP_REQUEST_CUSTOM_ERROR_CODE;
			}
			else
			{
				jsonOutput.put(pgApolloConstants.KEY_MESSAGE, pgApolloConstants.STR_SUCCESS);
			}		
			
		} catch (MatrixException e) {
			logger.error(e.getMessage(), e);
			iStatusCode = APP_REQUEST_CUSTOM_ERROR_CODE;
			sError = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage()).toString();
			jsonOutput.put(pgApolloConstants.KEY_MESSAGE, sError);
		}
		
		String sResponse = objectMapper.writeValueAsString(jsonOutput);

		return Response.status(iStatusCode).entity(sResponse).build();
	}

	
	/**
	 * Method to get Latest Affected Items for given requests
	 * @param context
	 * @param sInput
	 * @return
	 */
	public static Response getLatestAffectedItems(Context context, String sInput)  throws Exception
	{
		ObjectNode jsonOutput = objectMapper.createObjectNode();;
		Map mapOutput = new HashMap();
		int iStatusCode = HttpServletResponse.SC_OK;

		try {
			
			Map mapInput = pgApolloCommonUtil.getStringMapBasedOnJsonArray(sInput);
			MapList mlOutput = PGAPPRequestUtil.getLatestAffectedItems(context, mapInput);		

			if(null != mlOutput && !mlOutput.isEmpty())
			{
				ArrayNode jsonOutputArray = pgApolloCommonUtil.convertMapListToJsonArray(mlOutput);
				jsonOutput.put("data", jsonOutputArray);	
				jsonOutput.put(pgApolloConstants.KEY_MESSAGE, pgApolloConstants.STR_SUCCESS);
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			String sError = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage()).toString();
			jsonOutput.put(pgApolloConstants.KEY_MESSAGE, sError);
		}
		
		String sResponse = objectMapper.writeValueAsString(jsonOutput);

		return Response.status(iStatusCode).entity(sResponse).build();
	}

	
	/**
	 * Method to get Request Process Owner List
	 * @param context
	 * @param sInput
	 * @return
	 */
	public static Response getRequestApproverList(Context context, String sInput) throws Exception
	{
		ObjectNode jsonOutput = objectMapper.createObjectNode();;
		Map mapOutput = new HashMap();
		int iStatusCode = HttpServletResponse.SC_OK;

		try {
			
			MapList mlOutput = PGAPPRequestUtil.getRequestApproverList(context);		

			if(null != mlOutput && !mlOutput.isEmpty())
			{
				ArrayNode jsonOutputArray = pgApolloCommonUtil.convertMapListToJsonArray(mlOutput);
				jsonOutput.put("data", jsonOutputArray);	
				jsonOutput.put(pgApolloConstants.KEY_MESSAGE, pgApolloConstants.STR_SUCCESS);

			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			String sError = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage()).toString();
			jsonOutput.put(pgApolloConstants.KEY_MESSAGE, sError);
		}
		
		String sResponse = objectMapper.writeValueAsString(jsonOutput);

		return Response.status(iStatusCode).entity(sResponse).build();
	}

	
	
	/**
	 * Method to claim APP Request
	 * @param context
	 * @param sInput
	 * @return
	 * @throws Exception 
	 */
	public static Response claimAPPRequest(Context context, String sInput) throws Exception 
	{
		ObjectNode jsonOutput = objectMapper.createObjectNode();;
		
		Map mapInput = new HashMap();
		MapList mlOutput = new MapList();
		Map mapOutput = new HashMap();
		String sError;
		int iStatusCode = HttpServletResponse.SC_OK;


		try {
			mapInput = pgApolloCommonUtil.getStringMapBasedOnJsonArray(sInput);
						
			mapOutput = PGAPPRequestUtil.claimAPPRequests(context, mapInput);		
			
			mlOutput = (MapList)mapOutput.get("data");
			
			if(null != mlOutput)
			{
				ArrayNode jsonOutputArray = pgApolloCommonUtil.convertMapListToJsonArray(mlOutput);
				jsonOutput.put("data", jsonOutputArray);				
			}
			
			if(mapOutput.containsKey(pgApolloConstants.STR_ERROR))
			{
				sError = (String)mapOutput.get(pgApolloConstants.STR_ERROR);
				jsonOutput.put(pgApolloConstants.KEY_MESSAGE, sError);
				iStatusCode = APP_REQUEST_CUSTOM_ERROR_CODE;

			}
			else
			{
				jsonOutput.put(pgApolloConstants.KEY_MESSAGE, pgApolloConstants.STR_SUCCESS);
			}		
			
		} catch (MatrixException e) {
			logger.error(e.getMessage(), e);
			iStatusCode = APP_REQUEST_CUSTOM_ERROR_CODE;
			sError = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage()).toString();
			jsonOutput.put(pgApolloConstants.KEY_MESSAGE, sError);
		}
		
		String sResponse = objectMapper.writeValueAsString(jsonOutput);

		return Response.status(iStatusCode).entity(sResponse).build();
	}

	
	/**
	 * Method to promote Requests 
	 * @param context
	 * @param sRequestSetId
	 * @param b
	 * @return
	 */
	public static Response promoteRequests(Context context, String sRequestId, boolean isChildRequest) throws Exception 
	{
		ObjectNode jsonOutput = objectMapper.createObjectNode();;
		MapList mlOutput = new MapList();
		int iStatusCode = HttpServletResponse.SC_OK;

		try {
			
			if(UIUtil.isNotNullAndNotEmpty(sRequestId))
			{
				Map mapObject = pgApolloCommonUtil.getLatestRevisionMap(context, PGAPPRequestConstants.TYPE_PGAPPREQUESTDATA, sRequestId, pgApolloConstants.CONSTANT_STRING_HYPHEN);	
				
				String sAPPRequestDataPhysicalId = (String)mapObject.get(DomainConstants.SELECT_PHYSICAL_ID);
				
				if(UIUtil.isNotNullAndNotEmpty(sAPPRequestDataPhysicalId))
				{
					mlOutput = PGAPPRequestUtil.promoteDemoteRequests(context, new StringList(sAPPRequestDataPhysicalId), false, isChildRequest);	

					ArrayNode jsonOutputArray = pgApolloCommonUtil.convertMapListToJsonArray(mlOutput);

					jsonOutput.put("data", jsonOutputArray);
					jsonOutput.put(pgApolloConstants.KEY_MESSAGE, pgApolloConstants.STR_SUCCESS);

				}
				else
				{
					jsonOutput.put(pgApolloConstants.KEY_MESSAGE, PGAPPRequestConstants.STR_ERROR_OBJECT_NOT_FOUND);
					iStatusCode = APP_REQUEST_CUSTOM_ERROR_CODE;
				}

			}
			else
			{
				jsonOutput.put(pgApolloConstants.KEY_MESSAGE, pgApolloConstants.STR_AUTOMATION_ERROR_INVALID_PARAMETERS);
				iStatusCode = APP_REQUEST_CUSTOM_ERROR_CODE;
			}
						
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			iStatusCode = APP_REQUEST_CUSTOM_ERROR_CODE;
			String sError = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage()).toString();
			jsonOutput.put(pgApolloConstants.KEY_MESSAGE, sError);
		}
				
		String sResponse = objectMapper.writeValueAsString(jsonOutput);

		return Response.status(iStatusCode).entity(sResponse).build();
	}

	/**
	 * Method to demote Requests 
	 * @param context
	 * @param sRequestSetId
	 * @param b
	 * @return
	 * @throws Exception 
	 */
	public static Response demoteRequests(Context context, String sRequestId, boolean isChildRequest) throws Exception 
	{
		ObjectNode jsonOutput = objectMapper.createObjectNode();;
		MapList mlOutput = new MapList();
		int iStatusCode = HttpServletResponse.SC_OK;

		try {
			
			if(UIUtil.isNotNullAndNotEmpty(sRequestId))
			{
				Map mapObject = pgApolloCommonUtil.getLatestRevisionMap(context, PGAPPRequestConstants.TYPE_PGAPPREQUESTDATA, sRequestId, pgApolloConstants.CONSTANT_STRING_HYPHEN);	
				
				String sAPPRequestDataPhysicalId = (String)mapObject.get(DomainConstants.SELECT_PHYSICAL_ID);
				
				if(UIUtil.isNotNullAndNotEmpty(sAPPRequestDataPhysicalId))
				{
					mlOutput = PGAPPRequestUtil.promoteDemoteRequests(context, new StringList(sAPPRequestDataPhysicalId), true, isChildRequest);	

					ArrayNode jsonOutputArray = pgApolloCommonUtil.convertMapListToJsonArray(mlOutput);

					jsonOutput.put("data", jsonOutputArray);
					jsonOutput.put(pgApolloConstants.KEY_MESSAGE, pgApolloConstants.STR_SUCCESS);

				}
				else
				{
					jsonOutput.put(pgApolloConstants.KEY_MESSAGE, PGAPPRequestConstants.STR_ERROR_OBJECT_NOT_FOUND);
					iStatusCode = APP_REQUEST_CUSTOM_ERROR_CODE;
				}

			}
			else
			{
				jsonOutput.put(pgApolloConstants.KEY_MESSAGE, pgApolloConstants.STR_AUTOMATION_ERROR_INVALID_PARAMETERS);
				iStatusCode = APP_REQUEST_CUSTOM_ERROR_CODE;
			}
						
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			iStatusCode = APP_REQUEST_CUSTOM_ERROR_CODE;
			String sError = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage()).toString();
			jsonOutput.put(pgApolloConstants.KEY_MESSAGE, sError);
		}
		
		
		String sResponse = objectMapper.writeValueAsString(jsonOutput);

		return Response.status(iStatusCode).entity(sResponse).build();
	}

	
	/**
	 * Method to update Implemented Item
	 * @param context
	 * @param sInput
	 * @return
	 * @throws Exception 
	 */
	public static Response updateImplementedItem(Context context, String sInput) throws Exception 
	{
		ObjectNode jsonOutput = objectMapper.createObjectNode();;
		
		Map mapInput = new HashMap();
		MapList mlOutput = new MapList();
		Map mapOutput = new HashMap();
		String sError;
		int iStatusCode = HttpServletResponse.SC_OK;


		try {
			mapInput = pgApolloCommonUtil.getStringMapBasedOnJsonArray(sInput);
						
			mapOutput = PGAPPRequestUtil.updateImplementedItem(context, mapInput);		
			
			mlOutput = (MapList)mapOutput.get("data");
			
			if(null != mlOutput)
			{
				ArrayNode jsonOutputArray = pgApolloCommonUtil.convertMapListToJsonArray(mlOutput);
				jsonOutput.put("data", jsonOutputArray);				
			}
			
			if(mapOutput.containsKey(pgApolloConstants.STR_ERROR))
			{
				sError = (String)mapOutput.get(pgApolloConstants.STR_ERROR);
				jsonOutput.put(pgApolloConstants.KEY_MESSAGE, sError);
				iStatusCode = APP_REQUEST_CUSTOM_ERROR_CODE;
			}
			else
			{
				jsonOutput.put(pgApolloConstants.KEY_MESSAGE, pgApolloConstants.STR_SUCCESS);
			}		
		
			
		} catch (MatrixException e) {
			logger.error(e.getMessage(), e);
			iStatusCode = APP_REQUEST_CUSTOM_ERROR_CODE;
			sError = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage()).toString();
			jsonOutput.put(pgApolloConstants.KEY_MESSAGE, sError);
		}
		
		String sResponse = objectMapper.writeValueAsString(jsonOutput);

		return Response.status(iStatusCode).entity(sResponse).build();
	}

	/**
	 * Method to reset Implemented Item
	 * @param context
	 * @param sInput
	 * @return
	 * @throws Exception 
	 */
	public static Response resetImplementedItem(Context context, String sInput) throws Exception
	{
		ObjectNode jsonOutput = objectMapper.createObjectNode();;

		Map mapInput = new HashMap();
		MapList mlOutput = new MapList();
		Map mapOutput = new HashMap();
		String sError;
		int iStatusCode = HttpServletResponse.SC_OK;

		try {
			mapInput = pgApolloCommonUtil.getStringMapBasedOnJsonArray(sInput);

			mapOutput = PGAPPRequestUtil.resetImplementedItem(context, mapInput);		

			mlOutput = (MapList)mapOutput.get("data");

			if(null != mlOutput)
			{
				ArrayNode jsonOutputArray = pgApolloCommonUtil.convertMapListToJsonArray(mlOutput);
				jsonOutput.put("data", jsonOutputArray);				
			}

			if(mapOutput.containsKey(pgApolloConstants.STR_ERROR))
			{
				sError = (String)mapOutput.get(pgApolloConstants.STR_ERROR);
				jsonOutput.put(pgApolloConstants.KEY_MESSAGE, sError);
				iStatusCode = APP_REQUEST_CUSTOM_ERROR_CODE;
			}
			else
			{
				jsonOutput.put(pgApolloConstants.KEY_MESSAGE, pgApolloConstants.STR_SUCCESS);
			}		

		} catch (MatrixException e) {
			logger.error(e.getMessage(), e);
			iStatusCode = APP_REQUEST_CUSTOM_ERROR_CODE;
			sError = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage()).toString();
			jsonOutput.put(pgApolloConstants.KEY_MESSAGE, sError);
		}
		
		String sResponse = objectMapper.writeValueAsString(jsonOutput);

		return Response.status(iStatusCode).entity(sResponse).build();
	}

	
	/**
	 * Method to delete Request Data
	 * @param context
	 * @param sInput
	 * @return
	 * @throws IOException 
	 */
	public static Response deleteRequestData(Context context, String sInput) throws Exception 
	{
		ObjectNode jsonOutput = objectMapper.createObjectNode();;
		
		Map mapInput = new HashMap();
		MapList mlOutput = new MapList();
		Map mapOutput = new HashMap();
		String sError;
		int iStatusCode = HttpServletResponse.SC_OK;

		try {
			
			mapInput = pgApolloCommonUtil.getStringMapBasedOnJsonArray(sInput);
						
			mapOutput = PGAPPRequestUtil.deleteRequestData(context, mapInput);		
			
			mlOutput = (MapList)mapOutput.get("data");
			
			if(null != mlOutput)
			{
				ArrayNode jsonOutputArray = pgApolloCommonUtil.convertMapListToJsonArray(mlOutput);
				jsonOutput.put("data", jsonOutputArray);				
			}
			
			if(mapOutput.containsKey(pgApolloConstants.STR_ERROR))
			{
				sError = (String)mapOutput.get(pgApolloConstants.STR_ERROR);
				jsonOutput.put(pgApolloConstants.KEY_MESSAGE, sError);
				iStatusCode = APP_REQUEST_CUSTOM_ERROR_CODE;
			}
			else
			{
				jsonOutput.put(pgApolloConstants.KEY_MESSAGE, pgApolloConstants.STR_SUCCESS);
			}
			
			
		} catch (MatrixException e) {
			logger.error(e.getMessage(), e);
			iStatusCode = APP_REQUEST_CUSTOM_ERROR_CODE;
			sError = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage()).toString();
			jsonOutput.put(pgApolloConstants.KEY_MESSAGE, sError);
		}
		
		String sResponse = objectMapper.writeValueAsString(jsonOutput);

		return Response.status(iStatusCode).entity(sResponse).build();
	}

	/**
	 * Method to validate Request Approval Membership for Context User
	 * @param context
	 * @return
	 * @throws Exception 
	 */
	public static Response validateAPPRequestApprovalMembership(Context context) throws Exception 
	{
		ObjectNode jsonOutput = objectMapper.createObjectNode();;

		String sError;
		int iStatusCode = HttpServletResponse.SC_OK;

		try
		{
			boolean isAPPRequestApprover = PGAPPRequestUtil.validateAPPRequestApprovalMembership(context);

			jsonOutput.put("data", Boolean.toString(isAPPRequestApprover));				
			jsonOutput.put(pgApolloConstants.KEY_MESSAGE, pgApolloConstants.STR_SUCCESS);

		} catch (MatrixException e) {
			logger.error(e.getMessage(), e);
			sError = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage()).toString();
			jsonOutput.put(pgApolloConstants.KEY_MESSAGE, sError);
		}
		
		String sResponse = objectMapper.writeValueAsString(jsonOutput);

		return Response.status(iStatusCode).entity(sResponse).build();
	}

	
	/**
	 * Method to reject APP Request
	 * @param context
	 * @param sInput
	 * @return
	 * @throws Exception 
	 */
	public static Response rejectAPPRequest(Context context, String sInput) throws Exception 
	{
		ObjectNode jsonOutput = objectMapper.createObjectNode();;
		
		Map mapInput = new HashMap();
		MapList mlOutput = new MapList();
		Map mapOutput = new HashMap();
		String sError;
		int iStatusCode = HttpServletResponse.SC_OK;

		try {
			mapInput = pgApolloCommonUtil.getStringMapBasedOnJsonArray(sInput);
						
			mapOutput = PGAPPRequestUtil.rejectAPPRequests(context, mapInput);		
			
			mlOutput = (MapList)mapOutput.get("data");
			
			if(null != mlOutput)
			{
				ArrayNode jsonOutputArray = pgApolloCommonUtil.convertMapListToJsonArray(mlOutput);
				jsonOutput.put("data", jsonOutputArray);				
			}
			
			if(mapOutput.containsKey(pgApolloConstants.STR_ERROR))
			{
				sError = (String)mapOutput.get(pgApolloConstants.STR_ERROR);
				jsonOutput.put(pgApolloConstants.KEY_MESSAGE, sError);
				iStatusCode = APP_REQUEST_CUSTOM_ERROR_CODE;
			}
			else
			{
				jsonOutput.put(pgApolloConstants.KEY_MESSAGE, pgApolloConstants.STR_SUCCESS);
			}	

			
		} catch (MatrixException e) {
			logger.error(e.getMessage(), e);
			iStatusCode = APP_REQUEST_CUSTOM_ERROR_CODE;
			sError = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage()).toString();
			jsonOutput.put(pgApolloConstants.KEY_MESSAGE, sError);
		}
		
		String sResponse = objectMapper.writeValueAsString(jsonOutput);

		return Response.status(iStatusCode).entity(sResponse).build();
	}

	
	/**
	 * Method to cancel APP Request
	 * @param context
	 * @param sInput
	 * @return
	 * @throws Exception 
	 */
	public static Response cancelAPPRequest(Context context, String sInput) throws Exception 
	{

		ObjectNode jsonOutput = objectMapper.createObjectNode();;
		
		Map mapInput = new HashMap();
		MapList mlOutput = new MapList();
		Map mapOutput = new HashMap();
		String sError;
		int iStatusCode = HttpServletResponse.SC_OK;

		try {
			mapInput = pgApolloCommonUtil.getStringMapBasedOnJsonArray(sInput);
						
			mapOutput = PGAPPRequestUtil.cancelAPPRequests(context, mapInput);		
			
			mlOutput = (MapList)mapOutput.get("data");
			
			if(null != mlOutput)
			{
				ArrayNode jsonOutputArray = pgApolloCommonUtil.convertMapListToJsonArray(mlOutput);
				jsonOutput.put("data", jsonOutputArray);				
			}
			
			if(mapOutput.containsKey(pgApolloConstants.STR_ERROR))
			{
				sError = (String)mapOutput.get(pgApolloConstants.STR_ERROR);
				jsonOutput.put(pgApolloConstants.KEY_MESSAGE, sError);
				iStatusCode = APP_REQUEST_CUSTOM_ERROR_CODE;
			}
			else
			{
				jsonOutput.put(pgApolloConstants.KEY_MESSAGE, pgApolloConstants.STR_SUCCESS);
			}	

			
		} catch (MatrixException e) {
			logger.error(e.getMessage(), e);
			iStatusCode = APP_REQUEST_CUSTOM_ERROR_CODE;
			sError = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage()).toString();
			jsonOutput.put(pgApolloConstants.KEY_MESSAGE, sError);
		}
		
		String sResponse = objectMapper.writeValueAsString(jsonOutput);

		return Response.status(iStatusCode).entity(sResponse).build();
	}

	
	/**
	 * Method to get Configured type of changes
	 * @param context
	 * @return
	 * @throws Exception 
	 */
	public static Response getTypeOfChanges(Context context) throws Exception 
	{
		ObjectNode jsonOutput = objectMapper.createObjectNode();;

		String sError;
		MapList mlOutput = new MapList();
		Map mapOutput = new HashMap();
		int iStatusCode = HttpServletResponse.SC_OK;

		try
		{
			mapOutput = PGAPPRequestUtil.getConfiguredTypeOfChanges(context);

			mlOutput = (MapList)mapOutput.get("data");
			
			if(null != mlOutput)
			{
				ArrayNode jsonOutputArray = pgApolloCommonUtil.convertMapListToJsonArray(mlOutput);
				jsonOutput.put("data", jsonOutputArray);				
			}			
			
			jsonOutput.put(pgApolloConstants.KEY_MESSAGE, pgApolloConstants.STR_SUCCESS);

		} catch (MatrixException e) {
			logger.error(e.getMessage(), e);
			sError = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage()).toString();
			jsonOutput.put(pgApolloConstants.KEY_MESSAGE, sError);
		}
		
		String sResponse = objectMapper.writeValueAsString(jsonOutput);

		return Response.status(iStatusCode).entity(sResponse).build();
	}

	
	/**
	 * Method to assume affected items are implemented and request to be promoted to Implemented
	 * @param context
	 * @param sInput
	 * @return
	 * @throws Exception 
	 */
	public static Response markAsImplemented(Context context, String sInput) throws Exception 
	{

		ObjectNode jsonOutput = objectMapper.createObjectNode();;
		
		Map mapInput = new HashMap();
		MapList mlOutput = new MapList();
		Map mapOutput = new HashMap();
		String sError;
		int iStatusCode = HttpServletResponse.SC_OK;

		try {
			mapInput = pgApolloCommonUtil.getStringMapBasedOnJsonArray(sInput);
						
			mapOutput = PGAPPRequestUtil.markAsImplemented(context, mapInput);		
			
			mlOutput = (MapList)mapOutput.get("data");
			
			if(null != mlOutput)
			{
				ArrayNode jsonOutputArray = pgApolloCommonUtil.convertMapListToJsonArray(mlOutput);
				jsonOutput.put("data", jsonOutputArray);				
			}
			
			if(mapOutput.containsKey(pgApolloConstants.STR_ERROR))
			{
				sError = (String)mapOutput.get(pgApolloConstants.STR_ERROR);
				jsonOutput.put(pgApolloConstants.KEY_MESSAGE, sError);
				iStatusCode = APP_REQUEST_CUSTOM_ERROR_CODE;

			}
			else
			{
				jsonOutput.put(pgApolloConstants.KEY_MESSAGE, pgApolloConstants.STR_SUCCESS);
			}	

			
		} catch (MatrixException e) {
			logger.error(e.getMessage(), e);
			iStatusCode = APP_REQUEST_CUSTOM_ERROR_CODE;
			sError = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage()).toString();
			jsonOutput.put(pgApolloConstants.KEY_MESSAGE, sError);
		}
		
		String sResponse = objectMapper.writeValueAsString(jsonOutput);

		return Response.status(iStatusCode).entity(sResponse).build();
	}

	/**
	 * Method to validate affected items for Implementation
	 * @param context
	 * @param sInput
	 * @return
	 * @throws Exception 
	 */
	public static Response validateAffectedItemsForImplementation(Context context, String sInput) throws Exception 
	{

		ObjectNode jsonOutput = objectMapper.createObjectNode();;
		
		Map mapInput = new HashMap();
		MapList mlOutput = new MapList();
		String sError;
		int iStatusCode = HttpServletResponse.SC_OK;

		try {
			mapInput = pgApolloCommonUtil.getStringMapBasedOnJsonArray(sInput);
						
			Map mapOutput = PGAPPRequestUtil.validateAffectedItemsForImplementation(context, mapInput);			
		
			ObjectNode jsonObject = pgApolloCommonUtil.convertMapToJson(mapOutput);

			jsonOutput.put("data", jsonObject);				
			
			if(mapOutput.containsKey(pgApolloConstants.STR_ERROR))
			{
				sError = (String)mapOutput.get(pgApolloConstants.STR_ERROR);
				jsonOutput.put(pgApolloConstants.KEY_MESSAGE, sError);
				iStatusCode = APP_REQUEST_CUSTOM_ERROR_CODE;
			}
			else
			{
				jsonOutput.put(pgApolloConstants.KEY_MESSAGE, pgApolloConstants.STR_SUCCESS);
			}		

			
		} catch (MatrixException e) {
			logger.error(e.getMessage(), e);
			sError = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage()).toString();
			jsonOutput.put(pgApolloConstants.KEY_MESSAGE, sError);
		}		
		
		String sResponse = objectMapper.writeValueAsString(jsonOutput);

		return Response.status(iStatusCode).entity(sResponse).build();
	}

	/**
	 * Method to get Object History 
	 * @param context
	 * @param sInput
	 * @return
	 * @throws Exception 
	 */
	public static Response getObjectHistory(Context context, String sObjectPhysicalId) throws Exception 
	{
		ObjectNode jsonOutput = objectMapper.createObjectNode();;

		MapList mlOutput = new MapList();
		int iStatusCode = HttpServletResponse.SC_OK;

		try {
			
			if(UIUtil.isNotNullAndNotEmpty(sObjectPhysicalId))
			{
				mlOutput = PGAPPRequestUtil.getHistoryData(context, sObjectPhysicalId);

				if(null!= mlOutput && !mlOutput.isEmpty())
				{
					ArrayNode jsonOutputArray = pgApolloCommonUtil.convertMapListToJsonArray(mlOutput);

					jsonOutput.put("data", jsonOutputArray);
					jsonOutput.put(pgApolloConstants.KEY_MESSAGE, pgApolloConstants.STR_SUCCESS);			

				}
				else
				{
					jsonOutput.put(pgApolloConstants.KEY_MESSAGE, PGAPPRequestConstants.STR_ERROR_OBJECT_NOT_FOUND);
					iStatusCode = APP_REQUEST_CUSTOM_ERROR_CODE;

				}		
				
			}
			else
			{
				jsonOutput.put(pgApolloConstants.KEY_MESSAGE, pgApolloConstants.STR_AUTOMATION_ERROR_INVALID_PARAMETERS);
				iStatusCode = APP_REQUEST_CUSTOM_ERROR_CODE;

			}


		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			String sError = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage()).toString();
			jsonOutput.put(pgApolloConstants.KEY_MESSAGE, sError);
		}
			
		String sResponse = objectMapper.writeValueAsString(jsonOutput);

		return Response.status(iStatusCode).entity(sResponse).build();
	}


}

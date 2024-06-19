package com.png.apollo.apprequest;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.PersonUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.domain.util.eMatrixDateFormat;
import com.matrixone.apps.engineering.EngineeringConstants;
import com.matrixone.apps.framework.ui.UICache;
import com.matrixone.apps.framework.ui.UIUtil;
import com.png.apollo.pgApolloCommonUtil;
import com.png.apollo.pgApolloConstants;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.db.RelationshipType;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class PGAPPRequestImplementation {

	private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(PGAPPRequestImplementation.class);


	/**
	 * Method to mark Request for Implementation
	 * @param context
	 * @param mapAllInput
	 * @return
	 * @throws Exception 
	 */
	public static Map markRequestForImplementation(Context context, Map mapAllInput) throws Exception
	{
		Map mapReturn = new HashMap();

		String sErrorMessage = DomainConstants.EMPTY_STRING;

		boolean isTransactionStarted = false;

		MapList mlInputList = new MapList();	
		MapList mlOutputList = new MapList();	
		StringList slRequestPhysicalIdList = new StringList();
		StringList slAllRequestPhysicalIdList = new StringList();

		Map mapInput;

		try {

			mlInputList = (MapList)mapAllInput.get(PGAPPRequestConstants.KEY_DATA);

			if(null != mlInputList && !mlInputList.isEmpty())
			{
				ContextUtil.startTransaction(context,true);
				isTransactionStarted = true;

				for(Object objMap : mlInputList)
				{	

					mapInput  = (Map)objMap;

					slRequestPhysicalIdList = (StringList)mapInput.get(PGAPPRequestConstants.KEY_REQUEST_PHYSICAL_ID);					

					if(null != slRequestPhysicalIdList && !slRequestPhysicalIdList.isEmpty())
					{							
						markRequestForImplementation(context, slRequestPhysicalIdList);
						slAllRequestPhysicalIdList.addAll(slRequestPhysicalIdList);
					}

				}

			}

			ContextUtil.commitTransaction(context);
			isTransactionStarted = false;
		}  
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
			if(isTransactionStarted)
			{
				ContextUtil.abortTransaction(context);
				isTransactionStarted = false;
			}
			sErrorMessage = e.getLocalizedMessage();
		}

		if(UIUtil.isNotNullAndNotEmpty(sErrorMessage))
		{
			mapReturn.put(pgApolloConstants.STR_ERROR, sErrorMessage);
			mapReturn.put(PGAPPRequestConstants.KEY_DATA, new MapList());
		}
		else
		{
			if(!slAllRequestPhysicalIdList.isEmpty())
			{
				mlOutputList = PGAPPRequestUtil.getRequestDetails(context, slAllRequestPhysicalIdList);
			}
			mapReturn.put(PGAPPRequestConstants.KEY_DATA, mlOutputList);
		}

		return mapReturn;
	}


	/**
	 * Method to mark Requests for Implementation
	 * @param context
	 * @param slRequestPhysicalIdList
	 * @throws Exception 
	 */
	public static StringList markRequestForImplementation(Context context, StringList slAllRequestPhysicalIdList) throws Exception
	{	
		boolean isContextPushed = false;
		MapList mlOutput = new MapList();
		StringList slRequestPhysicalIdList = new StringList();

		try
		{

			if(null != slAllRequestPhysicalIdList && !slAllRequestPhysicalIdList.isEmpty())
			{

				String sRequestBackgroundProcessPhysicalIdSelectable = new StringBuilder("to[").append(pgApolloConstants.RELATIONSHIP_PGBACKGROUNDPROCESSRELATEDOBJECTS).append("].from.physicalid").toString();

				StringList slRequestSelects = new StringList();
				slRequestSelects.add(DomainConstants.SELECT_PHYSICAL_ID);
				slRequestSelects.add(DomainConstants.SELECT_CURRENT);
				slRequestSelects.add(sRequestBackgroundProcessPhysicalIdSelectable);


				StringList slMultiValueRequestSelects = new StringList();
				slMultiValueRequestSelects.add(sRequestBackgroundProcessPhysicalIdSelectable);

				MapList mlAllRequestIdInfo = DomainObject.getInfo(context, slAllRequestPhysicalIdList.toArray(new String[slAllRequestPhysicalIdList.size()]), slRequestSelects, slMultiValueRequestSelects); 

				Map localMap;
				String sRequestPhysicalId;
				String sRequestCurrent;
				StringList slRequestBackgroundProcessPhysicalIdList;

				for(Object objectMap : mlAllRequestIdInfo)
				{
					localMap = (Map)objectMap;	

					sRequestPhysicalId = (String)localMap.get(DomainConstants.SELECT_PHYSICAL_ID);
					sRequestCurrent = (String)localMap.get(DomainConstants.SELECT_CURRENT);
					slRequestBackgroundProcessPhysicalIdList = (StringList)localMap.get(sRequestBackgroundProcessPhysicalIdSelectable);

					if(PGAPPRequestConstants.PGAPPREQUEST_STATE_READY_TO_IMPLEMENT.equalsIgnoreCase(sRequestCurrent) && (null == slRequestBackgroundProcessPhysicalIdList || slRequestBackgroundProcessPhysicalIdList.isEmpty()))
					{
						slRequestPhysicalIdList.add(sRequestPhysicalId);
					}
				}

			}		

			//TO DO - Return Message if there is any existing Background process connected


			if(null != slRequestPhysicalIdList && !slRequestPhysicalIdList.isEmpty())
			{

				String sContextUser = context.getUser();

				String sType = pgApolloConstants.TYPE_PGBACKGROUDPROCESS;
				String sObjGeneratorName = UICache.getObjectGenerator(context, "type_pgBackgroundProcess", DomainConstants.EMPTY_STRING);
				String sAutoGeneratedName = DomainObject.getAutoGeneratedName(context, sObjGeneratorName, DomainConstants.EMPTY_STRING);
				DomainObject domObject = DomainObject.newInstance(context);

				Map mapAttribute = new HashMap();
				mapAttribute.put(DomainConstants.ATTRIBUTE_TITLE, PGAPPRequestConstants.STR_TITLE_IMPLEMENTAPPREQUEST);
				mapAttribute.put(pgApolloConstants.ATTRIBUTE_PROGRAMNAME, "com.png.apollo.apprequest.PGAPPRequestImplementation");
				mapAttribute.put(pgApolloConstants.ATTRIBUTE_METHODNAME, "initiateProcessingOfAssociatedRequests");

				domObject.createObject(context, sType, sAutoGeneratedName, DomainConstants.EMPTY_STRING, pgApolloConstants.POLICY_PGBACKGROUNDPROCESSPOLICY, context.getVault().getName());
				domObject.setAttributeValues(context, mapAttribute);
				domObject.setOwner(context, sContextUser);

				//Push context is needed to update APPs which are released, for which Users will not have access  to it
				ContextUtil.pushContext(context, pgApolloConstants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
				isContextPushed = true;	

				DomainRelationship.connect(context, domObject, pgApolloConstants.RELATIONSHIP_PGBACKGROUNDPROCESSRELATEDOBJECTS, true, slRequestPhysicalIdList.toArray(new String[slRequestPhysicalIdList.size()]));
			
				
			}		


		} 
		catch (Exception ex)
		{
			logger.error(ex.getMessage() ,ex);	
		}
		finally
		{
			if(isContextPushed)
			{
				ContextUtil.popContext(context);
			}
		}		

		return slAllRequestPhysicalIdList;
	}


	/**
	 * This Method will be called from cron job, will have super user context
	 * Method to initiate processing of associated requests
	 * @param context
	 * @param domBackgroundProcess
	 * @param mapBackgroundProcess
	 * @param sChangeTemplateId
	 * @return
	 * @throws Exception
	 */
	public Map initiateProcessingOfAssociatedRequests(Context context, String[] args) throws Exception 
	{
		String sBackgroundProcessObjectId = args[0];

		Map mapResponse = new HashMap();
		String sResponse = pgApolloConstants.STR_SUCCESS;		
		boolean isContextPushed = false;

		context.printTrace(pgApolloConstants.TRACE_LPD, "PGAPPRequestImplementation : initiateProcessingOfAssociatedRequests >> sBackgroundProcessObjectId = "+sBackgroundProcessObjectId);

		try 
		{
			if(UIUtil.isNotNullAndNotEmpty(sBackgroundProcessObjectId))
			{
				String sSelectBackgroundProcessRelatedObjects = "from["+pgApolloConstants.RELATIONSHIP_PGBACKGROUNDPROCESSRELATEDOBJECTS+"].to.id";

				StringBuilder sbContext = new StringBuilder();
				String sContextRole;

				StringList slObjectSelects = new StringList();
				slObjectSelects.add(DomainConstants.SELECT_NAME);
				slObjectSelects.add(DomainConstants.SELECT_ID);
				slObjectSelects.add(DomainConstants.SELECT_CURRENT);
				slObjectSelects.add(DomainConstants.SELECT_OWNER);
				slObjectSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_TITLE);
				slObjectSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PGPARAMETERARGUMENT1);
				slObjectSelects.add(sSelectBackgroundProcessRelatedObjects);

				StringList slMultiValueObjectSelects = new StringList();
				slMultiValueObjectSelects.add(sSelectBackgroundProcessRelatedObjects);

				DomainObject domBackgroundProcess = DomainObject.newInstance(context, sBackgroundProcessObjectId);		

				Map mapBackgroundProcess = domBackgroundProcess.getInfo(context, slObjectSelects, slMultiValueObjectSelects);

				context.printTrace(pgApolloConstants.TRACE_LPD, "PGAPPRequestImplementation : initiateProcessingOfAssociatedRequests  mapBackgroundProcess = "+mapBackgroundProcess);


				String sBackgroundProcessOwner = (String)mapBackgroundProcess.get(DomainConstants.SELECT_OWNER);

				StringList slAssociatedAPPRequestIds = pgApolloCommonUtil.getStringListMultiValue(mapBackgroundProcess.get(sSelectBackgroundProcessRelatedObjects));

				if(null != slAssociatedAPPRequestIds && !slAssociatedAPPRequestIds.isEmpty())
				{
					//User specific context is required to process the objects. - as method execution will be by Cron Job - Super User
					ContextUtil.pushContext(context,sBackgroundProcessOwner,DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
					isContextPushed = true;

					String sPersonCTX = PersonUtil.getDefaultSecurityContext(context, context.getUser());
					sContextRole = sbContext.append("ctx::").append(sPersonCTX).toString();
					context.resetRole(sContextRole);


					String sRequestImplementedItemSelectable = new StringBuilder("from[").append(PGAPPRequestConstants.RELATIONSHIP_PGAPPREQUESTIMPLEMENTEDITEM).append("].to.physicalid").toString();
					String sRequestDataSelectable = new StringBuilder("to[").append(PGAPPRequestConstants.RELATIONSHIP_PGAPPREQUESTCHANGEDATA).append("].from.physicalid").toString();

					StringList slSelectables = new StringList();
					slSelectables.add(DomainConstants.SELECT_ID);
					slSelectables.add(DomainConstants.SELECT_NAME);
					slSelectables.add(DomainConstants.SELECT_TYPE);
					slSelectables.add(DomainConstants.SELECT_PHYSICAL_ID);
					slSelectables.add(DomainConstants.SELECT_CURRENT);
					slSelectables.add(sRequestImplementedItemSelectable);
					slSelectables.add(sRequestDataSelectable);

					StringList slMultiValueSelectables = new StringList();
					slMultiValueSelectables.add(sRequestImplementedItemSelectable);
					slMultiValueSelectables.add(sRequestDataSelectable);

					MapList mlAPPRequestsInfo = DomainObject.getInfo(context, slAssociatedAPPRequestIds.toArray(new String[slAssociatedAPPRequestIds.size()]), slSelectables, slMultiValueSelectables);	

					Map mapRequest;
					StringList slRequestDataList;
					String sRequestDataId;
					String sRequestId;
					StringList slAllRequestDataList = new StringList();
					Map mapRequestSetInfo = new HashMap();

					if(null != mlAPPRequestsInfo && !mlAPPRequestsInfo.isEmpty())
					{

						for(Object objMap : mlAPPRequestsInfo)
						{
							mapRequest = (Map)objMap;
							sRequestId = (String)mapRequest.get(DomainConstants.SELECT_PHYSICAL_ID);
							slRequestDataList = pgApolloCommonUtil.getStringListMultiValue(mapRequest.get(sRequestDataSelectable));
							sRequestDataId = DomainConstants.EMPTY_STRING;
							if(null != slRequestDataList && !slRequestDataList.isEmpty())
							{
								sRequestDataId = slRequestDataList.get(0);
								if(UIUtil.isNotNullAndNotEmpty(sRequestDataId))
								{
									mapRequestSetInfo.put(sRequestId, sRequestDataId);
								}								
								slAllRequestDataList.addAll(slRequestDataList);
							}
						}
					}

					Map mapRequestSetRequestedChangeInfo = new HashMap();
					mapRequestSetRequestedChangeInfo = getRequestedChangeDetailsInfo(context, slAllRequestDataList);

					context.printTrace(pgApolloConstants.TRACE_LPD, "PGAPPRequestImplementation : initiateProcessingOfAssociatedRequests  mlAPPRequestsInfo = "+mlAPPRequestsInfo);

					Map mapParamArgs = new HashMap();
					mapParamArgs.put(PGAPPRequestConstants.KEY_APP_REQUEST_INFOLIST, mlAPPRequestsInfo);
					mapParamArgs.put("mode", PGAPPRequestConstants.STR_MODE_IMPLEMENTAPPREQUEST);
					mapParamArgs.put("mapRequestSetInfo", mapRequestSetInfo);
					mapParamArgs.put(PGAPPRequestConstants.KEY_PARENTREQUESTREQUESTEDCHANGEINFO, mapRequestSetRequestedChangeInfo);

					sResponse = massImplementAPPRequests(context, mapParamArgs, mapBackgroundProcess);

					context.printTrace(pgApolloConstants.TRACE_LPD, "PGAPPRequestImplementation : initiateProcessingOfAssociatedRequests  sResponse = "+sResponse);

				}

				mapResponse.put(pgApolloConstants.KEY_MESSAGE, pgApolloConstants.STR_SUCCESS);
			}
		} 
		catch (Exception e) 
		{
			logger.error(e.getMessage(), e);
			sResponse = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getMessage()).toString();	
			mapResponse.put(pgApolloConstants.KEY_MESSAGE, pgApolloConstants.STR_ERROR);
			mapResponse.put(pgApolloConstants.STR_ERROR, sResponse);
		}
		finally
		{
			if(isContextPushed)
			{
				ContextUtil.popContext(context);
			}
		}	

		if(mapResponse.isEmpty())
		{
			mapResponse.put(pgApolloConstants.KEY_MESSAGE, pgApolloConstants.STR_ERROR);
			mapResponse.put(pgApolloConstants.STR_ERROR, pgApolloConstants.STR_ERROR);
		}	
		context.printTrace(pgApolloConstants.TRACE_LPD, "PGAPPRequestImplementation : initiateProcessingOfAssociatedRequests << mapResponse = "+mapResponse);

		return mapResponse;
	}



	/**
	 * Method to mass implement APP Requests
	 * @param context
	 * @param mapParamArgs
	 * @param mapBackgroundProcess
	 * @return
	 * @throws MatrixException
	 */
	public String massImplementAPPRequests(Context context, Map mapParamArgs, Map mapBackgroundProcess) throws MatrixException 
	{
		String sResponse = pgApolloConstants.STR_SUCCESS;
		context.printTrace(pgApolloConstants.TRACE_LPD, "PGAPPRequestImplementation : massImplementAPPRequests >> args = "+mapParamArgs+" \n mapBackgroundProcess = >> "+mapBackgroundProcess);

		String sBackgroundObjectId = (String)mapBackgroundProcess.get(DomainConstants.SELECT_ID);		
		DomainObject domBackgroundProcess = DomainObject.newInstance(context, sBackgroundObjectId);
		Map mapBackgroundProcessAttribute = new HashMap();
		StringList slBackgroundProcessErrorList = new StringList();
		String sError;

		boolean bBackgroundJobProcessError = false;
		String sMessage;

		try 
		{			

			MapList mlRequestInfo = (MapList)mapParamArgs.get(PGAPPRequestConstants.KEY_APP_REQUEST_INFOLIST);
			Map mapRequestSetRequestedChangeInfo = (Map)mapParamArgs.get(PGAPPRequestConstants.KEY_PARENTREQUESTREQUESTEDCHANGEINFO);

			Map mapRequest;
			Map mapRequestOutput;
			MapList mlRequestOutput = new MapList();
			StringBuilder sbObjectDetails = new StringBuilder();
			StringBuilder sbErrorDetails = new StringBuilder();

			String sRequestName;

			String sRequestError = DomainConstants.EMPTY_STRING;
			StringList slUpdateErrorList = new StringList();
			StringList slMessageList = new StringList();
			String sImplementedItemPhysicalId;
			String sRevisedImplementedItemPhysicalId;
			String sImplementedItemCurrent;
			boolean bError = false;
			String sRequestPhysicalId;

			if(!mlRequestInfo.isEmpty())
			{
				for(Object object : mlRequestInfo)
				{
					mapRequestOutput = new HashMap();

					mapRequest = (Map)object;	

					sRequestName = (String)mapRequest.get(DomainConstants.SELECT_NAME);	

					sbObjectDetails = new StringBuilder();
					sbObjectDetails.append(sRequestName);

					mapRequestOutput = validateRequestPriorProcessing(context, mapRequest, mapRequestSetRequestedChangeInfo);

					context.printTrace(pgApolloConstants.TRACE_LPD, "PGAPPRequestImplementation : massImplementAPPRequests After Validation : mapRequestOutput :"+mapRequestOutput);

					sImplementedItemPhysicalId = (String)mapRequestOutput.get(PGAPPRequestConstants.KEY_IMPLEMENTED_ITEM_PHYSICAL_ID);

					sImplementedItemCurrent = (String)mapRequestOutput.get(PGAPPRequestConstants.KEY_IMPLEMENTED_ITEM_STATE);

					bError = (boolean)mapRequestOutput.get(pgApolloConstants.STR_ERROR);

					slUpdateErrorList = (StringList)mapRequestOutput.get(pgApolloConstants.STR_ERROR_LIST);						

					slMessageList = (StringList)mapRequestOutput.get(pgApolloConstants.KEY_MESSAGE);						

					try
					{	

						if(!bError && UIUtil.isNotNullAndNotEmpty(sImplementedItemPhysicalId))
						{
							if(pgApolloConstants.STATE_RELEASE.equalsIgnoreCase(sImplementedItemCurrent) || pgApolloConstants.STATE_RELEASED.equalsIgnoreCase(sImplementedItemCurrent))
							{
								//Revise Part
								sRevisedImplementedItemPhysicalId = pgApolloCommonUtil.revisePart(context, sImplementedItemPhysicalId);

								context.printTrace(pgApolloConstants.TRACE_LPD, "PGAPPRequestImplementation : massImplementAPPRequests sRevisedImplementedItemPhysicalId :"+sRevisedImplementedItemPhysicalId);

								if(UIUtil.isNullOrEmpty(sRevisedImplementedItemPhysicalId) || sRevisedImplementedItemPhysicalId.contains(pgApolloConstants.STR_ERROR))
								{
									sError = new StringBuilder(sRequestName).append(pgApolloConstants.CONSTANT_STRING_COLON).append(PGAPPRequestConstants.STR_ERROR_AUTOFULLFILLMENT_REVISE).append(pgApolloConstants.CONSTANT_STRING_COLON).append(sRevisedImplementedItemPhysicalId).toString();
									addCustomHistoryOnAPPRequestImplement(context, sImplementedItemPhysicalId, sError);
									slUpdateErrorList.add(sError);
									bError = true;
									mapRequestOutput.put(pgApolloConstants.STR_ERROR_LIST, slUpdateErrorList);
									mapRequestOutput.put(pgApolloConstants.STR_ERROR, bError);

								}
								else if(UIUtil.isNotNullAndNotEmpty(sRevisedImplementedItemPhysicalId))
								{
									sMessage = new StringBuilder(sRequestName).append(pgApolloConstants.CONSTANT_STRING_COLON).append(PGAPPRequestConstants.STR_SUCCESS_AUTOFULLFILLMENT_REVISE).toString();
									addCustomHistoryOnAPPRequestImplement(context, sRevisedImplementedItemPhysicalId, sMessage);
									bError = false;
									sImplementedItemPhysicalId = sRevisedImplementedItemPhysicalId;
									slMessageList.add(PGAPPRequestConstants.STR_SUCCESS_AUTOFULLFILLMENT_REVISE);
									mapRequestOutput.put(pgApolloConstants.KEY_MESSAGE, slMessageList);
								}

							}					

							context.printTrace(pgApolloConstants.TRACE_LPD, "PGAPPRequestImplementation : massImplementAPPRequests sImplementedItemCurrent :"+sImplementedItemCurrent+" sImplementedItemPhysicalId "+sImplementedItemPhysicalId);

							sMessage = new StringBuilder(sRequestName).append(pgApolloConstants.CONSTANT_STRING_COLON).append(PGAPPRequestConstants.HISTORY_APP_REQUEST_IMPLEMENTATION).append(PGAPPRequestConstants.APP_REQUEST_IMPLEMENTATION_HISTORY_STARTS).toString();
							addCustomHistoryOnAPPRequestImplement(context, sImplementedItemPhysicalId, sMessage);

							mapRequestOutput = processImplementation(context, mapRequest, mapRequestOutput, sImplementedItemPhysicalId);
							
						}
						else
						{
							context.printTrace(pgApolloConstants.TRACE_LPD, "PGAPPRequestImplementation : massImplementAPPRequests Processing skipped for Object = "+sRequestName+"  mapRequestOutput = "+mapRequestOutput);
						}


					}
					catch(Exception exp)
					{
						logger.error(exp.getMessage(), exp);
						sbErrorDetails = new StringBuilder();
						sbErrorDetails.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(sbObjectDetails).append(pgApolloConstants.CONSTANT_STRING_COLON).append(exp.getLocalizedMessage());
						sRequestError = sbErrorDetails.toString();
						slBackgroundProcessErrorList.add(sRequestError);
						bBackgroundJobProcessError = true;						

						slUpdateErrorList = (StringList)mapRequestOutput.get(pgApolloConstants.STR_ERROR_LIST);						

						if(UIUtil.isNotNullAndNotEmpty(sRequestError))
						{
							if(null ==slUpdateErrorList)
							{
								slUpdateErrorList = new StringList();
							}
							slUpdateErrorList.add(sRequestError);
							mapRequestOutput.put(pgApolloConstants.STR_ERROR_LIST, slUpdateErrorList);
							mapRequestOutput.put(pgApolloConstants.STR_ERROR, true);
						}
					}
					finally
					{
						if(!mapRequestOutput.isEmpty())
						{
							slUpdateErrorList = (StringList)mapRequestOutput.getOrDefault(pgApolloConstants.STR_ERROR_LIST, new StringList());		

							sImplementedItemPhysicalId = (String)mapRequestOutput.get(PGAPPRequestConstants.KEY_IMPLEMENTED_ITEM_PHYSICAL_ID);

							sRequestPhysicalId = (String)mapRequestOutput.get(PGAPPRequestConstants.KEY_REQUEST_PHYSICAL_ID);

							if(UIUtil.isNotNullAndNotEmpty(sImplementedItemPhysicalId) && UIUtil.isNotNullAndNotEmpty(sRequestPhysicalId))
							{
								sError = postUpdateProcessing(context, domBackgroundProcess, mapRequestOutput);

								if(UIUtil.isNotNullAndNotEmpty(sError))
								{
									sbErrorDetails = new StringBuilder();
									sbErrorDetails.append(sbObjectDetails).append(pgApolloConstants.CONSTANT_STRING_COLON).append(sError);
									slBackgroundProcessErrorList.add(sbErrorDetails.toString());
									bBackgroundJobProcessError = true;
								}

								if(!slUpdateErrorList.isEmpty())
								{
									sMessage = new StringBuilder(sRequestName).append(pgApolloConstants.CONSTANT_STRING_COLON).append(PGAPPRequestConstants.HISTORY_APP_REQUEST_IMPLEMENTATION).append(StringUtil.join(slUpdateErrorList, pgApolloConstants.CONSTANT_STRING_COMMA)).toString();
									sError = addCustomHistoryOnAPPRequestImplement(context, sImplementedItemPhysicalId, sMessage);
								}

								sMessage = new StringBuilder(sRequestName).append(pgApolloConstants.CONSTANT_STRING_COLON).append(PGAPPRequestConstants.HISTORY_APP_REQUEST_IMPLEMENTATION).append(PGAPPRequestConstants.APP_REQUEST_IMPLEMENTATION_HISTORY_END).toString();

								sError = addCustomHistoryOnAPPRequestImplement(context, sImplementedItemPhysicalId, sMessage);

								if(UIUtil.isNotNullAndNotEmpty(sError))
								{
									slBackgroundProcessErrorList.add(sError);
								}

							}
						}		

					}		
					
					mlRequestOutput.add(mapRequestOutput);
					
				}
				
				sError = generateOutputSummaryAndSendNotification(context, mlRequestOutput, mapBackgroundProcess, bBackgroundJobProcessError);	

				if(UIUtil.isNotNullAndNotEmpty(sRequestError))
				{
					slBackgroundProcessErrorList.add(sRequestError);
				}

			}
		}
		catch (Exception e) 
		{
			logger.error(e.getMessage(), e);
			sResponse = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getMessage()).toString();	
			sError = new StringBuilder(PGAPPRequestConstants.STR_TITLE_IMPLEMENTAPPREQUEST).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getMessage()).toString();								
			slBackgroundProcessErrorList.add(sError);
		}
		finally
		{
			try
			{
				if(!slBackgroundProcessErrorList.isEmpty())
				{
					mapBackgroundProcessAttribute.put(pgApolloConstants.ATTRIBUTE_ERROR_MESSAGE, slBackgroundProcessErrorList.join(pgApolloConstants.CONSTANT_STRING_NEWLINE));
					domBackgroundProcess.setAttributeValues(context, mapBackgroundProcessAttribute);
				}
				else
				{
					mapBackgroundProcessAttribute.put(pgApolloConstants.ATTRIBUTE_ERROR_MESSAGE, pgApolloConstants.STR_SUCCESS);
					domBackgroundProcess.setAttributeValues(context, mapBackgroundProcessAttribute);
				}
			} 
			catch (Exception e) 
			{
				logger.error(e.getMessage(), e);
				sResponse = new StringBuilder("Updating Background Job Error Attributes").append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getMessage()).toString();								
			}			
		}
		context.printTrace(pgApolloConstants.TRACE_LPD, "PGAPPRequestImplementation : massImplementAPPRequests << slBackgroundProcessErrorList = "+slBackgroundProcessErrorList);		
		context.printTrace(pgApolloConstants.TRACE_LPD, "PGAPPRequestImplementation : massImplementAPPRequests << Response = "+sResponse);		
		return sResponse;

	}




	/**
	 * Method to update updated revision and disconnect background process object
	 * @param context
	 * @param domBackgroundProcess
	 * @param mapRequestOutput
	 * @return
	 * @throws MatrixException
	 */
	public String postUpdateProcessing(matrix.db.Context context, DomainObject domBackgroundProcess, Map mapRequestOutput) throws MatrixException 
	{

		context.printTrace(pgApolloConstants.TRACE_LPD, "PGAPPRequestImplementation : postUpdateProcessing >> mapRequestPartOutput = "+mapRequestOutput);

		String sError = DomainConstants.EMPTY_STRING;		
		boolean bError = false;
		StringList slUpdateErrorList;
		RelationshipType relTypeBGP = new RelationshipType(pgApolloConstants.RELATIONSHIP_PGBACKGROUNDPROCESSRELATEDOBJECTS);
		Map mapAttribute = new HashMap();
		DomainObject domainRequestObject;
		boolean isUserContextPushed = false;
		boolean bSuccess = false;

		try 
		{
			slUpdateErrorList = (StringList)mapRequestOutput.get(pgApolloConstants.STR_ERROR_LIST);
			bError = (boolean)mapRequestOutput.get(pgApolloConstants.STR_ERROR);
			String sTypeOfChange = (String)mapRequestOutput.get(PGAPPRequestConstants.ATTRIBUTE_PG_APP_REQUESTCHANGETYPE);	
			String sImplementedItemLastCurrent = (String)mapRequestOutput.get(PGAPPRequestConstants.KEY_IMPLEMENTED_ITEM_STATE);
			boolean bChangesAlreadyImplemented = false;			
			if(mapRequestOutput.containsKey(PGAPPRequestConstants.KEY_CHANGES_IMPLEMENTED))
			{
				bChangesAlreadyImplemented = (boolean)mapRequestOutput.get(PGAPPRequestConstants.KEY_CHANGES_IMPLEMENTED);
			}
			
			StringList slTypeOfChangesAllowRequestPromotionIfChangesAlreadyPresent = new StringList();
			slTypeOfChangesAllowRequestPromotionIfChangesAlreadyPresent.add(PGAPPRequestConstants.RANGE_PGAPPREQUESTCHANGETYPE_EXPIRATION_DATE_CHANGE);
			slTypeOfChangesAllowRequestPromotionIfChangesAlreadyPresent.add(PGAPPRequestConstants.RANGE_PGAPPREQUESTCHANGETYPE_MFG_STATUS_CHANGE);

			if(null == slUpdateErrorList)
			{
				slUpdateErrorList = new StringList();
			}

			if(!slUpdateErrorList.isEmpty() && bError)
			{
				String sErrorMessage = new StringBuilder(PGAPPRequestConstants.STR_ERROR_AUTOFULLFILLMENT_FAILED).append(StringUtil.join(slUpdateErrorList, pgApolloConstants.CONSTANT_STRING_COMMA)).toString();
				mapAttribute.put(PGAPPRequestConstants.ATTRIBUTE_PGFAILEDREASON, sErrorMessage);
			}
			else
			{
				bSuccess = true;
				mapAttribute.put(PGAPPRequestConstants.ATTRIBUTE_PGFAILEDREASON, DomainConstants.EMPTY_STRING);
			}	

			String sRequestPhysicalId = (String)mapRequestOutput.get(PGAPPRequestConstants.KEY_REQUEST_PHYSICAL_ID);

			//Context user won't always have access to set attributes on Released Part. So push context is needed here.
			ContextUtil.pushContext(context, pgApolloConstants.PERSON_USER_AGENT, null, context.getVault().getName());
			isUserContextPushed = true;

			String sAssociatedBackgroundJobIdSelect = new StringBuilder("to[").append(pgApolloConstants.RELATIONSHIP_PGBACKGROUNDPROCESSRELATEDOBJECTS).append("].from.").append(DomainConstants.SELECT_ID).toString();

			StringList slRequestSelects = new StringList();
			slRequestSelects.add(DomainConstants.SELECT_NAME);
			slRequestSelects.add(DomainConstants.SELECT_CURRENT);
			slRequestSelects.add(sAssociatedBackgroundJobIdSelect);

			domainRequestObject = DomainObject.newInstance(context, sRequestPhysicalId);			
			Map mapRequestInfo = domainRequestObject.getInfo(context, slRequestSelects, new StringList(sAssociatedBackgroundJobIdSelect));	
			StringList slRequestBackgroundJobId = (StringList)mapRequestInfo.get(sAssociatedBackgroundJobIdSelect);
			String sRequestCurrent = (String)mapRequestInfo.get(DomainConstants.SELECT_CURRENT);
			String sRequestName = (String)mapRequestInfo.get(DomainConstants.SELECT_NAME);

			String sBackgroundProcessObjectId = domBackgroundProcess.getObjectId(context);

			context.printTrace(pgApolloConstants.TRACE_LPD, "PGAPPRequestImplementation : postUpdateProcessing  slRequestBackgroundJobId = "+slRequestBackgroundJobId+"  sBackgroundProcessObjectId = "+sBackgroundProcessObjectId);

			if(null != slRequestBackgroundJobId && !slRequestBackgroundJobId.isEmpty() && slRequestBackgroundJobId.contains(sBackgroundProcessObjectId))
			{
				domBackgroundProcess.disconnect(context, relTypeBGP, true, domainRequestObject);				
				context.printTrace(pgApolloConstants.TRACE_LPD, "PGAPPRequestImplementation : postUpdateProcessing  Disconnection of = "+sRequestPhysicalId+" : "+sRequestName+"  with sBackgroundProcessObjectId = "+sBackgroundProcessObjectId);
			}		

			domainRequestObject.setAttributeValues(context, mapAttribute);

			if(bSuccess && PGAPPRequestConstants.PGAPPREQUEST_STATE_READY_TO_IMPLEMENT.equalsIgnoreCase(sRequestCurrent))
			{
				if(
				  (PGAPPRequestConstants.RANGE_PGAPPREQUESTCHANGETYPE_ACCELERATED_RELEASE_ADDITION.equalsIgnoreCase(sTypeOfChange) || PGAPPRequestConstants.RANGE_PGAPPREQUESTCHANGETYPE_OBSOLESENCE.equalsIgnoreCase(sTypeOfChange)) || 
				  (bChangesAlreadyImplemented && slTypeOfChangesAllowRequestPromotionIfChangesAlreadyPresent.contains(sTypeOfChange) && (DomainConstants.STATE_PART_RELEASE.equalsIgnoreCase(sImplementedItemLastCurrent) || pgApolloConstants.CRITERIA_STATE_RELEASED.equalsIgnoreCase(sImplementedItemLastCurrent))))
				{
					domainRequestObject.setState(context, PGAPPRequestConstants.PGAPPREQUEST_STATE_RELEASED);
					mapRequestOutput.put(PGAPPRequestConstants.KEY_REQUEST_STATE, PGAPPRequestConstants.PGAPPREQUEST_STATE_RELEASED);
					
					if(slTypeOfChangesAllowRequestPromotionIfChangesAlreadyPresent.contains(sTypeOfChange))
					{
						addCustomHistoryOnAPPRequestImplement(context, sRequestPhysicalId, PGAPPRequestConstants.STR_MESSAGE_REQUETPROMOTIONRELEASED_CHANGES_IMPLEMENTED);
					}
				}				
				else
				{				
					domainRequestObject.setState(context, PGAPPRequestConstants.PGAPPREQUEST_STATE_IMPLEMENTED);
					mapRequestOutput.put(PGAPPRequestConstants.KEY_REQUEST_STATE, PGAPPRequestConstants.PGAPPREQUEST_STATE_IMPLEMENTED);
				}
			}
			else
			{
				mapRequestOutput.put(PGAPPRequestConstants.KEY_REQUEST_STATE, sRequestCurrent);
			}


		} 
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
			sError = new StringBuilder(PGAPPRequestConstants.STR_ERROR_AUTOFULLFILLMENT_POSTPROCESS).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getMessage()).toString();								
		}	
		finally
		{
			if(isUserContextPushed)
			{
				ContextUtil.popContext(context);
				isUserContextPushed = false;
			}			
		}	
		return sError;		
	}



	/**
	 * Method to process Implementation
	 * @param context
	 * @param mapRequest
	 * @param mapRequestOutput
	 * @param sImplementedItemPhysicalId
	 * @return
	 * @throws MatrixException
	 */
	public Map processImplementation(Context context, Map mapRequest, Map mapRequestOutput, String sImplementedItemPhysicalId) throws MatrixException 
	{		

		context.printTrace(pgApolloConstants.TRACE_LPD, "PGAPPRequestImplementation : processImplementation >> mapRequest :"+mapRequest+" mapRequestOutput :"+mapRequestOutput+" sImplementedItemPhysicalId : "+sImplementedItemPhysicalId);

		String sRequestImplementedItemSelectable = new StringBuilder("from[").append(PGAPPRequestConstants.RELATIONSHIP_PGAPPREQUESTIMPLEMENTEDITEM).append("].to.physicalid").toString();

		String sRequestPhysicalId = (String)mapRequest.get(DomainConstants.SELECT_PHYSICAL_ID);

		String sRequestName = (String)mapRequest.get(DomainConstants.SELECT_NAME);

		String sExistingImplementedItemPhysicalId = DomainConstants.EMPTY_STRING;
		
		boolean bError = (boolean)mapRequestOutput.get(pgApolloConstants.STR_ERROR);

		StringList slUpdateErrorList = (StringList)mapRequestOutput.getOrDefault(pgApolloConstants.STR_ERROR_LIST, new StringList());	
		
		String sObjectPhysicalId = DomainConstants.EMPTY_STRING;
		
		String sImplementedItemLastCurrent = DomainConstants.EMPTY_STRING;
		
		boolean bChangesAlreadyImplemented = false;
		
		if(mapRequestOutput.containsKey(PGAPPRequestConstants.KEY_CHANGES_IMPLEMENTED))
		{
			bChangesAlreadyImplemented = (boolean)mapRequestOutput.get(PGAPPRequestConstants.KEY_CHANGES_IMPLEMENTED);
		}

		String sTypeOfChange = (String)mapRequestOutput.get(PGAPPRequestConstants.ATTRIBUTE_PG_APP_REQUESTCHANGETYPE);	

		if(UIUtil.isNotNullAndNotEmpty(sImplementedItemPhysicalId))
		{			

			StringList slObjectSelect = new StringList();
			slObjectSelect.add(DomainConstants.SELECT_CURRENT);
			slObjectSelect.add(DomainConstants.SELECT_PHYSICAL_ID);
			slObjectSelect.add(DomainConstants.SELECT_TYPE);
			slObjectSelect.add(DomainConstants.SELECT_NAME);
			slObjectSelect.add(DomainConstants.SELECT_REVISION);
			slObjectSelect.add(DomainConstants.SELECT_POLICY);

			DomainObject domRequest = DomainObject.newInstance(context, sRequestPhysicalId);
			
			StringList slImplementedItemList = domRequest.getInfoList(context, sRequestImplementedItemSelectable);
			
			if(null != slImplementedItemList && !slImplementedItemList.isEmpty())
			{
				sExistingImplementedItemPhysicalId = slImplementedItemList.get(0);
			}

			DomainObject domImplementedItem = DomainObject.newInstance(context, sImplementedItemPhysicalId);

			Map mapImplementedItem = domImplementedItem.getInfo(context, slObjectSelect);

			String sObjectCurrent = (String)mapImplementedItem.get(DomainConstants.SELECT_CURRENT);
			
			sImplementedItemLastCurrent = sObjectCurrent;

			sObjectPhysicalId = (String)mapImplementedItem.get(DomainConstants.SELECT_PHYSICAL_ID);

			String sObjectType = (String)mapImplementedItem.get(DomainConstants.SELECT_TYPE);			

			String sObjectName = (String)mapImplementedItem.get(DomainConstants.SELECT_NAME);

			String sObjectRevision = (String)mapImplementedItem.get(DomainConstants.SELECT_REVISION);

			String sObjectPolicy = (String)mapImplementedItem.get(DomainConstants.SELECT_POLICY);

			context.printTrace(pgApolloConstants.TRACE_LPD, "processImplementation mapImplementedItem :"+mapImplementedItem);

			if(DomainConstants.STATE_PART_PRELIMINARY.equals(sObjectCurrent))
			{			

				context.printTrace(pgApolloConstants.TRACE_LPD, "processImplementation Connect Disconnect >> New Implemented Item Physical Id -> sObjectPhysicalId :"+sObjectPhysicalId+" Existing Implemented Item Physical Id -> sExistingImplementedItemPhysicalId :"+sExistingImplementedItemPhysicalId);

				if(!sObjectPhysicalId.equals(sExistingImplementedItemPhysicalId))
				{
					if(UIUtil.isNotNullAndNotEmpty(sExistingImplementedItemPhysicalId))
					{
						DomainObject domExistingImplementedItem = DomainObject.newInstance(context, sExistingImplementedItemPhysicalId);

						domExistingImplementedItem.disconnect(context, new RelationshipType(PGAPPRequestConstants.RELATIONSHIP_PGAPPREQUESTIMPLEMENTEDITEM), false, domRequest);				
					}
					
					DomainRelationship.connect(context, sRequestPhysicalId, PGAPPRequestConstants.RELATIONSHIP_PGAPPREQUESTIMPLEMENTEDITEM, sObjectPhysicalId, true);					

					String sObjectTNR = new StringBuilder(sObjectType).append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sObjectName).append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sObjectRevision).toString();

					domImplementedItem.setAttributeValue(context, PGAPPRequestConstants.ATTRIBUTE_PG_IMPLEMENTEDITEM, sObjectTNR);

					context.printTrace(pgApolloConstants.TRACE_LPD, "processImplementation Connect Disconnect Successful");

				}			

			}

			mapRequestOutput.put(PGAPPRequestConstants.KEY_IMPLEMENTED_ITEM_PHYSICAL_ID, sImplementedItemPhysicalId);
			mapRequestOutput.put(PGAPPRequestConstants.KEY_IMPLEMENTED_ITEM, sObjectName);
			mapRequestOutput.put(PGAPPRequestConstants.KEY_IMPLEMENTED_ITEM_TYPE, sObjectType);
			mapRequestOutput.put(PGAPPRequestConstants.KEY_IMPLEMENTED_ITEM_REVISION, sObjectRevision);
			mapRequestOutput.put(PGAPPRequestConstants.KEY_IMPLEMENTED_ITEM_STATE, sObjectCurrent);
			mapRequestOutput.put(PGAPPRequestConstants.KEY_IMPLEMENTED_ITEM_POLICY, sObjectPolicy);
			mapRequestOutput.put(PGAPPRequestConstants.KEY_REQUEST_ID, sRequestName);
			mapRequestOutput.put(PGAPPRequestConstants.KEY_REQUEST_PHYSICAL_ID, sRequestPhysicalId);			
			

			context.printTrace(pgApolloConstants.TRACE_LPD, "processImplementation mapRequestOutput : "+mapRequestOutput);

		}		
		
		if(!checkForImplementedChangesForSpecificTypeOfChanges(bChangesAlreadyImplemented, sTypeOfChange, sImplementedItemLastCurrent) && UIUtil.isNotNullAndNotEmpty(sObjectPhysicalId) && !bError && slUpdateErrorList.isEmpty())
		{
			mapRequestOutput = implementChanges(context, sObjectPhysicalId, mapRequestOutput);
		}
		
		return mapRequestOutput;
	}

	/**
	 * Method to implement changes on Implemented Item
	 * @param context
	 * @param domImplementedItem
	 * @param mapRequestOutput
	 * @throws MatrixException 
	 */
	private Map implementChanges(Context context, String sImplementedItemPhysicalId, Map mapRequestOutput) throws MatrixException 
	{

		context.printTrace(pgApolloConstants.TRACE_LPD, "PGAPPRequestImplementation implementChanges mapRequestOutput : "+mapRequestOutput+" sImplementedItemPhysicalId : "+sImplementedItemPhysicalId);

		DomainObject domImplementedItem = DomainObject.newInstance(context, sImplementedItemPhysicalId);

		String sTypeOfChange = (String)mapRequestOutput.get(PGAPPRequestConstants.ATTRIBUTE_PG_APP_REQUESTCHANGETYPE);	

		StringList slMessageList = (StringList)mapRequestOutput.getOrDefault(pgApolloConstants.KEY_MESSAGE, new StringList());

		StringList slErrorList = (StringList)mapRequestOutput.getOrDefault(pgApolloConstants.STR_ERROR_LIST, new StringList());

		String sMessage;

		if(UIUtil.isNotNullAndNotEmpty(sTypeOfChange))
		{
			if(PGAPPRequestConstants.RANGE_PGAPPREQUESTCHANGETYPE_SUBSTITUTE_ADDITION.equalsIgnoreCase(sTypeOfChange))
			{
				MapList mlSubstituteDetails = (MapList)mapRequestOutput.get(sTypeOfChange);

				StringList slObjectSelects = new StringList();
				slObjectSelects.add(DomainConstants.SELECT_ID);

				StringList slEBOMRelSelectable = new StringList();
				slEBOMRelSelectable.add(DomainRelationship.SELECT_ID);
				slEBOMRelSelectable.add(pgApolloConstants.SELECT_ATTRIBUTE_PLYNAME);
				slEBOMRelSelectable.add(pgApolloConstants.SELECT_ATTRIBUTE_PLYGROUPNAME);

				context.printTrace(pgApolloConstants.TRACE_LPD, "implementChanges mlSubstituteDetails : "+mlSubstituteDetails);

				MapList mlAPPEBOMList = domImplementedItem.getRelatedObjects(context,	 // Context
						DomainConstants.RELATIONSHIP_EBOM,       // relationship pattern
						pgApolloConstants.TYPE_RAW_MATERIAL,          // type pattern
						slObjectSelects,               // object selects
						slEBOMRelSelectable,                // relationship selects
						false,                                   // to direction
						true,                                    // from direction
						(short)1,                                // recursion level
						DomainConstants.EMPTY_STRING,            // object where clause
						null,									 // Relationship Where Clause
						0);										 // Limit

				context.printTrace(pgApolloConstants.TRACE_LPD, "implementChanges mlAPPEBOMList : "+mlAPPEBOMList);

				Map mapEBOMInfo = new HashMap();

				if(!mlAPPEBOMList.isEmpty())
				{
					StringBuilder sbKeyBuilder;
					String sPlyGroupName;
					String sPlyName;
					String sUniqueEBOMKey;
					String sUniqueEBOMValue;
					Map mapEBOM;
					String sEBOMObjectId;
					String sEBOMObjectRelId;

					for(int i=0; i<mlAPPEBOMList.size(); i++)
					{
						mapEBOM = (Map)mlAPPEBOMList.get(i);		
						sPlyGroupName = (String)mapEBOM.get(pgApolloConstants.SELECT_ATTRIBUTE_PLYGROUPNAME);
						sPlyName = (String)mapEBOM.get(pgApolloConstants.SELECT_ATTRIBUTE_PLYNAME);	

						sEBOMObjectId = (String)mapEBOM.get(DomainConstants.SELECT_ID);		
						sEBOMObjectRelId = (String)mapEBOM.get(DomainRelationship.SELECT_ID);

						sbKeyBuilder = new StringBuilder();
						sbKeyBuilder.append(sPlyGroupName);
						sbKeyBuilder.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sPlyName);						
						sUniqueEBOMKey = sbKeyBuilder.toString();	

						sbKeyBuilder = new StringBuilder();
						sbKeyBuilder.append(sEBOMObjectRelId);
						sbKeyBuilder.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sEBOMObjectId);						
						sUniqueEBOMValue = sbKeyBuilder.toString();	

						mapEBOMInfo.put(sUniqueEBOMKey, sUniqueEBOMValue);

					}

					context.printTrace(pgApolloConstants.TRACE_LPD, "implementChanges mapEBOMInfo : "+mapEBOMInfo);

					Map mapSubstitute;					

					for(Object objMap : mlSubstituteDetails)
					{
						mapSubstitute = (Map)objMap;			

						sPlyGroupName = (String)mapSubstitute.get(pgApolloConstants.STR_GROUPNAME);
						sPlyName = (String)mapSubstitute.get(pgApolloConstants.STR_LAYERNAME);					

						sbKeyBuilder = new StringBuilder();
						sbKeyBuilder.append(sPlyGroupName);
						sbKeyBuilder.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sPlyName);
						sUniqueEBOMKey = sbKeyBuilder.toString();

						sUniqueEBOMValue = (String)mapEBOMInfo.get(sUniqueEBOMKey);

						context.printTrace(pgApolloConstants.TRACE_LPD, "implementChanges sUniqueEBOMValue : "+sUniqueEBOMValue+" mapSubstitute "+mapSubstitute);

						if(UIUtil.isNotNullAndNotEmpty(sUniqueEBOMValue))
						{							
							String sReturnMessage = DomainConstants.EMPTY_STRING;
							try {
								createAndUpdateSubstitutes(context, sUniqueEBOMValue, mapSubstitute);							
							} catch (Exception e) {
								sReturnMessage = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage()).toString();
							}				
							
							if(sReturnMessage.contains(pgApolloConstants.STR_ERROR))
							{
								sMessage = new StringBuilder(PGAPPRequestConstants.STR_ERROR_IMPLEMENT_SUBSTIUTE_UPDATE).append(sReturnMessage).toString();
								slErrorList.add(sMessage);
							}
						}
						else
						{
							slMessageList.add(PGAPPRequestConstants.STR_ERROR_GROUP_LAYER_MISSING_ONEBOM.replaceFirst("<LAYERNAME>", sUniqueEBOMKey));
						}

					}				

				}		

			}
			else if(PGAPPRequestConstants.RANGE_PGAPPREQUESTCHANGETYPE_PLANTS_ADDITION.equalsIgnoreCase(sTypeOfChange))
			{	
				MapList mlNewPlantDetails = (MapList)mapRequestOutput.get(sTypeOfChange);

				context.printTrace(pgApolloConstants.TRACE_LPD, "implementChanges mlNewPlantDetails : "+mlNewPlantDetails);

				Map mapPlant;
				String sPlantId;
				String sAuthorizedToUse;
				String sAuthorizedToProduce;
				String sActivated;

				Map mapPlantActualAttributeDetails = new HashMap();
				Map mapAttribute;

				StringList slNewPlantList = new StringList();

				for(Object objMap : mlNewPlantDetails)
				{
					mapPlant = (Map)objMap;

					sPlantId = (String)mapPlant.get(DomainConstants.SELECT_ID);
					sAuthorizedToUse = (String)mapPlant.get(PGAPPRequestConstants.KEY_AUTHORIZEDTOUSE);
					sAuthorizedToProduce = (String)mapPlant.get(PGAPPRequestConstants.KEY_AUTHORIZEDTOPRODUCE);
					sActivated = (String)mapPlant.get(PGAPPRequestConstants.KEY_ACTIVATED);					

					if(UIUtil.isNotNullAndNotEmpty(sPlantId))
					{
						mapAttribute = new HashMap();
						mapAttribute.put(pgApolloConstants.ATTRIBUTE_PGISAUTHORIZEDTOUSE, sAuthorizedToUse);
						mapAttribute.put(pgApolloConstants.ATTRIBUTE_PGISAUTHORIZEDTOPRODUCE, sAuthorizedToProduce);
						mapAttribute.put(pgApolloConstants.ATTRIBUTE_PGISACTIVATED, sActivated);

						mapAttribute = getActualPlantAttributeDetails(mapAttribute);

						mapPlantActualAttributeDetails.put(sPlantId, mapAttribute);
						slNewPlantList.add(sPlantId);
					}
				}

				context.printTrace(pgApolloConstants.TRACE_LPD, "implementChanges mapPlantActualAttributeDetails : "+mapPlantActualAttributeDetails);
				context.printTrace(pgApolloConstants.TRACE_LPD, "implementChanges slNewPlantList : "+slNewPlantList);


				Map paramMap = new HashMap();
				paramMap.put("SelectedPlantIdList",slNewPlantList);
				paramMap.put("PlantAttributes",mapPlantActualAttributeDetails);
				paramMap.put("objectId",sImplementedItemPhysicalId);
				paramMap.put("AttributeUpdate",false);

				String[] methodargs = JPO.packArgs(paramMap);

				String sReturnMessage = DomainConstants.EMPTY_STRING;
				try {
					sReturnMessage = JPO.invoke(context,"pgDSMLayeredProductGlobalActions",null,"updatePlantsWithAttributes",methodargs,null);
					
				} catch (Exception e) {
					sReturnMessage = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage()).toString();
				}				
				
				if(sReturnMessage.contains(pgApolloConstants.STR_ERROR))
				{
					sMessage = new StringBuilder(PGAPPRequestConstants.STR_ERROR_IMPLEMENT_PLANT_ADDITION).append(sReturnMessage).toString();
					slErrorList.add(sMessage);
				}

				context.printTrace(pgApolloConstants.TRACE_LPD, "implementChanges sReturnMessage : "+sReturnMessage);

			}
			else if(PGAPPRequestConstants.RANGE_PGAPPREQUESTCHANGETYPE_MFG_STATUS_CHANGE.equalsIgnoreCase(sTypeOfChange) || PGAPPRequestConstants.RANGE_PGAPPREQUESTCHANGETYPE_EXPIRATION_DATE_CHANGE.equalsIgnoreCase(sTypeOfChange))
			{
				Map mapAttributes = (Map)mapRequestOutput.get(sTypeOfChange);

				context.printTrace(pgApolloConstants.TRACE_LPD, "implementChanges mapAttributes : "+mapAttributes);

				if(!mapAttributes.isEmpty())
				{
					String sReturnMessage = DomainConstants.EMPTY_STRING;
					try {
						domImplementedItem.setAttributeValues(context, mapAttributes);
					} catch (Exception e) {
						sReturnMessage = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage()).toString();
					}				
					
					if(sReturnMessage.contains(pgApolloConstants.STR_ERROR))
					{
						sMessage = new StringBuilder(PGAPPRequestConstants.STR_ERROR_IMPLEMENT_ATTRIBUTE_UPDATE).append(sReturnMessage).toString();
						slErrorList.add(sMessage);
					}
				}
			}
			


		}

		if(!slMessageList.isEmpty())
		{
			mapRequestOutput.put(pgApolloConstants.KEY_MESSAGE, slMessageList);
		}

		if(!slErrorList.isEmpty())
		{
			mapRequestOutput.put(pgApolloConstants.STR_ERROR_LIST, slErrorList);
		}

		context.printTrace(pgApolloConstants.TRACE_LPD, "PGAPPRequestImplementation implementChanges << mapRequestOutput : "+mapRequestOutput);

		return mapRequestOutput;
	}


	/**
	 * Method to create and Update Substitutes
	 * @param context
	 * @param sUniqueEBOMValue
	 * @param mapSubstitute
	 * @throws MatrixException
	 * @throws FrameworkException
	 */
	private void createAndUpdateSubstitutes(Context context, String sUniqueEBOMValue, Map mapSubstitute)
			throws MatrixException, FrameworkException {
		String sEBOMObjectId;
		String sEBOMObjectRelId;
		String sValidStartDate;
		String sValidEndDate;
		String sComments;
		StringList slEBOMValueList;
		String sEBOMSubstituteObjectId;
		String sEBOMSubstituteRelId;
		StringList slEBOMSubstituteRelId;
		StringList slSubstituteSelectable;
		DomainRelationship domEBOMSubstituteRelObj;
		slEBOMValueList = StringUtil.split(sUniqueEBOMValue, pgApolloConstants.CONSTANT_STRING_PIPE);

		sEBOMObjectRelId = slEBOMValueList.get(0);	

		sEBOMObjectId = slEBOMValueList.get(1);		

		sValidStartDate = (String)mapSubstitute.get(PGAPPRequestConstants.KEY_VALID_START_DATE);
		sValidStartDate = convertAgGridToStandardDateFormat(context, sValidStartDate);
		sValidEndDate = (String)mapSubstitute.get(PGAPPRequestConstants.KEY_VALID_UNTIL_DATE);
		sValidEndDate = convertAgGridToStandardDateFormat(context, sValidEndDate);
		sComments = (String)mapSubstitute.get(PGAPPRequestConstants.KEY_COMMENTS);

		sEBOMSubstituteObjectId = (String)mapSubstitute.get(DomainConstants.SELECT_ID);							

		String[] tmpArgs = new String[4];
		tmpArgs[0] = sEBOMObjectId;
		tmpArgs[1] = sEBOMObjectRelId;
		tmpArgs[2] = sEBOMSubstituteObjectId;

		JPO.invoke(context,"pgDSOCPNProductData",null,"createSubstitutePart",tmpArgs,null);

		DomainObject doSubstituteObj = DomainObject.newInstance(context, sEBOMSubstituteObjectId);

		slSubstituteSelectable = new StringList();
		slSubstituteSelectable.add("to["+EngineeringConstants.RELATIONSHIP_EBOM_SUBSTITUTE+"|fromrel.id=="+sEBOMObjectRelId+"].id");

		Map mpSubstituteMap = doSubstituteObj.getInfo(context, slSubstituteSelectable, slSubstituteSelectable);


		slEBOMSubstituteRelId = pgApolloCommonUtil.getStringListMultiValue(mpSubstituteMap.get("to["+EngineeringConstants.RELATIONSHIP_EBOM_SUBSTITUTE+"].id"));

		if(null!= slEBOMSubstituteRelId && !slEBOMSubstituteRelId.isEmpty())
		{
			sEBOMSubstituteRelId = slEBOMSubstituteRelId.get(0);

			domEBOMSubstituteRelObj = DomainRelationship.newInstance(context, sEBOMSubstituteRelId);

			Map mapRelAttributes = new HashMap();

			if(UIUtil.isNotNullAndNotEmpty(sValidStartDate))
			{
				mapRelAttributes.put(DomainConstants.ATTRIBUTE_START_EFFECTIVITY, sValidStartDate);
			}

			if(UIUtil.isNotNullAndNotEmpty(sValidEndDate))
			{
				mapRelAttributes.put(PGAPPRequestConstants.ATTRIBUTE_PG_VALIDUNTILDATE, sValidEndDate);
			}

			if(UIUtil.isNotNullAndNotEmpty(sComments))
			{
				mapRelAttributes.put(PGAPPRequestConstants.ATTRIBUTE_COMMENT, sComments);
			}

			domEBOMSubstituteRelObj.setAttributeValues(context, mapRelAttributes);

		}
	}


	/**
	 * This method updates custom History on an Object
	 * @param context
	 * @param strPartObjID
	 * @param strHistory
	 * @throws Exception
	 */
	public static String addCustomHistoryOnAPPRequestImplement(matrix.db.Context context,String sObjectId, String sHistory) throws Exception{

		boolean isUserContextPushed = false;
		String sError = DomainConstants.EMPTY_STRING;

		try
		{
			//Push context is needed to update APPs which are released, for which Users will not have access  to it
			ContextUtil.pushContext(context, pgApolloConstants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
			isUserContextPushed = true;	

			if(UIUtil.isNotNullAndNotEmpty(sObjectId))
			{
				//There is no specific History addition API - so need to use MQLUtil API
				String sMQLstmtUpdateHistory = "modify bus $1 add history Modify comment '$2'";
				MqlUtil.mqlCommand(context, sMQLstmtUpdateHistory, sObjectId, sHistory);	
			}	
		}
		catch(Exception e)
		{
			logger.error(e.getMessage(), e);
			sError = new StringBuilder(PGAPPRequestConstants.STR_ERROR_AUTOFULLFILLMENT_HISTORY_STAMPING).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getMessage()).toString();								
			throw new Exception(sError);
		}	
		finally
		{
			if(isUserContextPushed)
			{
				ContextUtil.popContext(context);
				isUserContextPushed = false;
			}			
		}	

		return sError;
	}

	/**
	 * Method to validate Request prior processing
	 * @param context
	 * @param sRequestPhysicalId
	 * @param sImplmentedItemPhysicalId
	 * @param mapRequest
	 * @param mapRequestSetRequestedChangeInfo 
	 * @return
	 * @throws Exception 
	 */
	public static Map validateRequestPriorProcessing(Context context, Map mapRequest, Map mapRequestSetRequestedChangeInfo) throws Exception
	{

		context.printTrace(pgApolloConstants.TRACE_LPD, "PGAPPRequestImplementation : validateRequestPriorProcessing >> : mapRequest :"+mapRequest+" mapRequestSetRequestedChangeInfo :"+mapRequestSetRequestedChangeInfo);

		Map mapReturn = new HashMap();

		StringList slErrorList = new StringList();

		StringList slMessageList = new StringList();

		boolean bProceedToImplement = false;
		
		boolean bChangesAlreadyImplemented = false;

		boolean bError = false;

		String sRequestImplementedItemSelectable = new StringBuilder("from[").append(PGAPPRequestConstants.RELATIONSHIP_PGAPPREQUESTIMPLEMENTEDITEM).append("].to.physicalid").toString();
		String sRequestDataSelectable = new StringBuilder("to[").append(PGAPPRequestConstants.RELATIONSHIP_PGAPPREQUESTCHANGEDATA).append("].from.physicalid").toString();

		String sImplementedItemPhysicalId = DomainConstants.EMPTY_STRING;

		String sRequestCurrent = (String)mapRequest.get(DomainConstants.SELECT_CURRENT);

		String sRequestPhysicalId = (String)mapRequest.get(DomainConstants.SELECT_PHYSICAL_ID);

		String sRequestName = (String)mapRequest.get(DomainConstants.SELECT_NAME);


		Map mapValidationOutput = new HashMap();		
		Map mapDataToImplement = new HashMap();

		String sTypeOfChange = DomainConstants.EMPTY_STRING;

		if(PGAPPRequestConstants.PGAPPREQUEST_STATE_READY_TO_IMPLEMENT.equalsIgnoreCase(sRequestCurrent))
		{
			StringList slImplementedItemList = (StringList)mapRequest.get(sRequestImplementedItemSelectable);

			if(null != slImplementedItemList && !slImplementedItemList.isEmpty())
			{
				sImplementedItemPhysicalId = slImplementedItemList.get(0);
			}

			StringList slRequestDataList = (StringList)mapRequest.get(sRequestDataSelectable);
			String sRequestDataId = DomainConstants.EMPTY_STRING;
			if(null != slRequestDataList && !slRequestDataList.isEmpty())
			{
				sRequestDataId = slRequestDataList.get(0);			
			}

			context.printTrace(pgApolloConstants.TRACE_LPD, "validateRequestPriorProcessing : sImplementedItemPhysicalId :"+sImplementedItemPhysicalId+" sRequestDataId :"+sRequestDataId);

			if(UIUtil.isNotNullAndNotEmpty(sImplementedItemPhysicalId) && UIUtil.isNotNullAndNotEmpty(sRequestDataId))
			{
				DomainObject domImplementedItem = DomainObject.newInstance(context, sImplementedItemPhysicalId);

				StringList slImplementedItemSelectable = new StringList();
				slImplementedItemSelectable.add(DomainConstants.SELECT_CURRENT);
				slImplementedItemSelectable.add(DomainConstants.SELECT_TYPE);
				slImplementedItemSelectable.add(DomainConstants.SELECT_POLICY);
				slImplementedItemSelectable.add(PGAPPRequestConstants.SELECT_LAST_CURRENT);
				slImplementedItemSelectable.add(PGAPPRequestConstants.SELECT_LAST_PHYSICALID);
				slImplementedItemSelectable.add(PGAPPRequestConstants.SELECT_LAST_NAME);
				slImplementedItemSelectable.add(PGAPPRequestConstants.SELECT_LAST_REVISION);

				Map mapImplementedItem = domImplementedItem.getInfo(context, slImplementedItemSelectable);

				context.printTrace(pgApolloConstants.TRACE_LPD, "validateRequestPriorProcessing : mapImplementedItem :"+mapImplementedItem);

				String sImplementedItemLastRevisionPhysicalId = (String)mapImplementedItem.get(PGAPPRequestConstants.SELECT_LAST_PHYSICALID);

				String sImplementedItemLastCurrent = (String)mapImplementedItem.get(PGAPPRequestConstants.SELECT_LAST_CURRENT);	

				String sImplementedItemType = (String)mapImplementedItem.get(DomainConstants.SELECT_TYPE);				

				String sImplementedItemPolicy = (String)mapImplementedItem.get(DomainConstants.SELECT_POLICY);	

				String sImplementedItemName = (String)mapImplementedItem.get(PGAPPRequestConstants.SELECT_LAST_NAME);				

				String sImplementedItemRevision = (String)mapImplementedItem.get(PGAPPRequestConstants.SELECT_LAST_REVISION);	

				mapDataToImplement = new HashMap();
				mapDataToImplement.put(PGAPPRequestConstants.KEY_IMPLEMENTED_ITEM_PHYSICAL_ID, sImplementedItemLastRevisionPhysicalId);
				mapDataToImplement.put(PGAPPRequestConstants.KEY_IMPLEMENTED_ITEM_STATE, sImplementedItemLastCurrent);
				mapDataToImplement.put(PGAPPRequestConstants.KEY_REQUEST_PHYSICAL_ID, sRequestPhysicalId);
				mapDataToImplement.put(PGAPPRequestConstants.KEY_REQUEST_ID, sRequestName);
				mapDataToImplement.put(PGAPPRequestConstants.KEY_IMPLEMENTED_ITEM_TYPE, sImplementedItemType);
				mapDataToImplement.put(PGAPPRequestConstants.KEY_IMPLEMENTED_ITEM_POLICY, sImplementedItemPolicy);
				mapDataToImplement.put(PGAPPRequestConstants.KEY_IMPLEMENTED_ITEM, sImplementedItemName);
				mapDataToImplement.put(PGAPPRequestConstants.KEY_IMPLEMENTED_ITEM_REVISION, sImplementedItemRevision);

				Map mapRequestSetedChangeDetailsInfo = (Map)mapRequestSetRequestedChangeInfo.get(sRequestDataId);

				sTypeOfChange = (String)mapRequestSetedChangeDetailsInfo.get(PGAPPRequestConstants.ATTRIBUTE_PG_APP_REQUESTCHANGETYPE);

				context.printTrace(pgApolloConstants.TRACE_LPD, "validateRequestPriorProcessing : sTypeOfChange :"+sTypeOfChange);

				DomainObject domLatestImplementedItem = null;
				
				boolean hasReadShowAccess = false;

				boolean hasModifyAccess = false;

				boolean hasReviseAccess = false;

				if(UIUtil.isNotNullAndNotEmpty(sImplementedItemLastRevisionPhysicalId))
				{
					domLatestImplementedItem = DomainObject.newInstance(context, sImplementedItemLastRevisionPhysicalId);
					hasReadShowAccess = FrameworkUtil.hasAccess(context,domLatestImplementedItem,"read,show");
				}

				if(UIUtil.isNotNullAndNotEmpty(sImplementedItemLastRevisionPhysicalId) && hasReadShowAccess)
				{

					if(!pgApolloConstants.STATE_OBSOLETE.equalsIgnoreCase(sImplementedItemLastCurrent) && UIUtil.isNotNullAndNotEmpty(sTypeOfChange))
					{
						Map mapNewRequestedChangeDetails = (Map)mapRequestSetedChangeDetailsInfo.get(sTypeOfChange);

						context.printTrace(pgApolloConstants.TRACE_LPD, "validateRequestPriorProcessing : mapNewRequestedChangeDetails :"+mapNewRequestedChangeDetails);

						Map mapImplementedItemExistingDetails = getAffectedItemExistingInfo(context, sImplementedItemLastRevisionPhysicalId, sTypeOfChange);

						context.printTrace(pgApolloConstants.TRACE_LPD, "validateRequestPriorProcessing : mapImplementedItemExistingDetails :"+mapImplementedItemExistingDetails);

						mapValidationOutput = validateAndGetDataForImplementation(context, sTypeOfChange, mapImplementedItemExistingDetails, mapNewRequestedChangeDetails, mapDataToImplement);

						context.printTrace(pgApolloConstants.TRACE_LPD, "validateRequestPriorProcessing : mapValidationOutput :"+mapValidationOutput);

						if(mapValidationOutput.containsKey(pgApolloConstants.KEY_MESSAGE))
						{
							slMessageList = (StringList)mapValidationOutput.get(pgApolloConstants.KEY_MESSAGE);
						}

						if(mapValidationOutput.containsKey(PGAPPRequestConstants.KEY_CHANGES_IMPLEMENTED))
						{
							bChangesAlreadyImplemented = (boolean)mapValidationOutput.get(PGAPPRequestConstants.KEY_CHANGES_IMPLEMENTED);
						}

						if(mapValidationOutput.containsKey(pgApolloConstants.STR_ERROR_LIST))
						{
							slErrorList = (StringList)mapValidationOutput.get(pgApolloConstants.STR_ERROR_LIST);
						}

						bProceedToImplement = false;

						if(mapValidationOutput.containsKey(PGAPPRequestConstants.KEY_PROCEED_TO_IMPLEMENT))
						{
							bProceedToImplement = (boolean)mapValidationOutput.get(PGAPPRequestConstants.KEY_PROCEED_TO_IMPLEMENT);
						}
						
						if(checkForImplementedChangesForSpecificTypeOfChanges(bChangesAlreadyImplemented, sTypeOfChange, sImplementedItemLastCurrent))
						{
							bProceedToImplement = true;
							mapDataToImplement.put(sTypeOfChange, mapValidationOutput.get(sTypeOfChange));
						}
						else if(!pgApolloConstants.STATE_FROZEN.equalsIgnoreCase(sImplementedItemLastCurrent) && !DomainConstants.STATE_PART_REVIEW.equalsIgnoreCase(sImplementedItemLastCurrent) && !DomainConstants.STATE_PART_APPROVED.equalsIgnoreCase(sImplementedItemLastCurrent))
						{					
							if(DomainConstants.STATE_PART_RELEASE.equalsIgnoreCase(sImplementedItemLastCurrent) || pgApolloConstants.CRITERIA_STATE_RELEASED.equalsIgnoreCase(sImplementedItemLastCurrent))
							{
								hasReviseAccess = FrameworkUtil.hasAccess(context,domLatestImplementedItem,"revise");
								if(hasReviseAccess)
								{
									mapDataToImplement.put(sTypeOfChange, mapValidationOutput.get(sTypeOfChange));
								}
								else
								{
									bProceedToImplement = false;
									slErrorList.add(PGAPPRequestConstants.STR_ERROR_AUTOFULLFILLMENT_NOREVISEACCESS);
								}
							}
							else if(DomainConstants.STATE_PART_PRELIMINARY.equalsIgnoreCase(sImplementedItemLastCurrent) || pgApolloConstants.CRITERIA_STATE_IN_WORK.equalsIgnoreCase(sImplementedItemLastCurrent))
							{
								hasModifyAccess = FrameworkUtil.hasAccess(context,domLatestImplementedItem,"modify");
								if(hasModifyAccess)
								{
									mapDataToImplement.put(sTypeOfChange, mapValidationOutput.get(sTypeOfChange));
								}
								else
								{
									bProceedToImplement = false;
									slErrorList.add(PGAPPRequestConstants.STR_ERROR_AUTOFULLFILLMENT_NOMODIFYACCESS);
								}
							}
							else
							{
								mapDataToImplement.put(sTypeOfChange, mapValidationOutput.get(sTypeOfChange));
							}

						}
						else
						{
							slErrorList.add(PGAPPRequestConstants.STR_ERROR_AUTOFULLFILLMENT_STATE_FROZENAPPROVED);
						}


					}
					else
					{
						slErrorList.add(PGAPPRequestConstants.STR_ERROR_AUTOFULLFILLMENT_STATE_OBSOLETE);
					}	

				}
				else
				{
					slErrorList.add(PGAPPRequestConstants.STR_ERROR_AUTOFULLFILLMENT_NOREADACCESS);
				}
			}	


		}
		else
		{
			slErrorList.add(PGAPPRequestConstants.STR_ERROR_AUTOFULLFILLMENT_REQUEST_NOT_READY_TO_IMPLEMENT);
		}

		if(bProceedToImplement && slErrorList.isEmpty())
		{
			bError = false;
		}
		else
		{
			bError = true;
		}

		context.printTrace(pgApolloConstants.TRACE_LPD, "validateRequestPriorProcessing : bProceedToImplement :"+bProceedToImplement+" bError :"+bError);

		mapReturn.put(pgApolloConstants.STR_ERROR, bError);
		
		mapReturn.put(PGAPPRequestConstants.KEY_CHANGES_IMPLEMENTED, bChangesAlreadyImplemented);

		mapReturn.put(pgApolloConstants.STR_ERROR_LIST, slErrorList);		

		mapReturn.put(PGAPPRequestConstants.ATTRIBUTE_PG_APP_REQUESTCHANGETYPE, sTypeOfChange);

		mapReturn.put(pgApolloConstants.KEY_MESSAGE, slMessageList);

		if(!mapDataToImplement.isEmpty() && UIUtil.isNotNullAndNotEmpty(sTypeOfChange))
		{
			mapReturn.putAll(mapDataToImplement);
		}

		context.printTrace(pgApolloConstants.TRACE_LPD, "validateRequestPriorProcessing : slErrorList :"+slErrorList);

		context.printTrace(pgApolloConstants.TRACE_LPD, "PGAPPRequestImplementation : validatePartPriorProcessing << mapReturn = "+mapReturn);		

		return mapReturn;

	}


	/**
	 * Method to check Implemented Changes for Specific type of Change
	 * @param bChangesAlreadyImplemented
	 * @param sTypeOfChange
	 * @param sImplementedItemLastCurrent
	 * @return
	 */
	private static boolean checkForImplementedChangesForSpecificTypeOfChanges(boolean bChangesAlreadyImplemented, String sTypeOfChange, String sImplementedItemLastCurrent) 
	{
		StringList slTypeOfChangesAllowRequestPromotionIfChangesAlreadyPresent = new StringList();
		slTypeOfChangesAllowRequestPromotionIfChangesAlreadyPresent.add(PGAPPRequestConstants.RANGE_PGAPPREQUESTCHANGETYPE_EXPIRATION_DATE_CHANGE);
		slTypeOfChangesAllowRequestPromotionIfChangesAlreadyPresent.add(PGAPPRequestConstants.RANGE_PGAPPREQUESTCHANGETYPE_MFG_STATUS_CHANGE);

		return (bChangesAlreadyImplemented  &&  slTypeOfChangesAllowRequestPromotionIfChangesAlreadyPresent.contains(sTypeOfChange) &&
		( pgApolloConstants.STATE_FROZEN.equalsIgnoreCase(sImplementedItemLastCurrent) || DomainConstants.STATE_PART_REVIEW.equalsIgnoreCase(sImplementedItemLastCurrent) || DomainConstants.STATE_PART_APPROVED.equalsIgnoreCase(sImplementedItemLastCurrent)));
	}


	/**
	 * Method to validate and get data for implementation
	 * @param context
	 * @param sTypeOfChange
	 * @param mapAffectedItemExistingDetails
	 * @param mapNewRequestedChangeDetails
	 * @param mapDataToImplement 
	 * @return
	 * @throws Exception 
	 */
	public static Map validateAndGetDataForImplementation(Context context, String sTypeOfChange,Map mapAffectedItemExistingDetails, Map mapNewRequestedChangeDetails, Map mapDataToImplement) throws Exception 
	{

		context.printTrace(pgApolloConstants.TRACE_LPD, "PGAPPRequestImplementation : validateAndGetDataForImplementation >> : sTypeOfChange :"+sTypeOfChange+" mapAffectedItemExistingDetails :"+mapAffectedItemExistingDetails+" mapNewRequestedChangeDetails :"+mapNewRequestedChangeDetails);

		boolean bError = false;
		
		boolean bProceedToImplement = false;
		boolean bChangesAlreadyImplemented = false;

		StringList slMessageList = new StringList();

		StringList slErrorList = new StringList();

		String sErrorMessage;

		String sImplementedItemCurrent = (String)mapDataToImplement.get(PGAPPRequestConstants.KEY_IMPLEMENTED_ITEM_STATE);

		if(PGAPPRequestConstants.RANGE_PGAPPREQUESTCHANGETYPE_SUBSTITUTE_ADDITION.equalsIgnoreCase(sTypeOfChange))
		{
			MapList mlNewSubstituteDetails = new MapList();

			Map mapAffectedItemExistingInfo = (Map)mapAffectedItemExistingDetails.get(sTypeOfChange);

			if(null == mapAffectedItemExistingInfo)
			{
				mapAffectedItemExistingInfo = new HashMap();
			}		
			
			Map mapAffectedItemEBOMInfo = (Map)mapAffectedItemExistingInfo.get(DomainConstants.RELATIONSHIP_EBOM);
			
			Map mapAffectedItemEBOMSubstiuteInfo = (Map)mapAffectedItemExistingInfo.get(EngineeringConstants.RELATIONSHIP_EBOM_SUBSTITUTE);


			MapList mlSubstituteDetails = (MapList)mapNewRequestedChangeDetails.get(sTypeOfChange);

			context.printTrace(pgApolloConstants.TRACE_LPD, " validateAndGetDataForImplementation mapAffectedItemExistingInfo :"+mapAffectedItemExistingInfo+" mlSubstituteDetails : "+mlSubstituteDetails);

			String sGroupName;
			String sLayerName;
			String sObjectName;
			Map mapSubstitute;

			StringBuilder sbUniqueKey = new StringBuilder();

			String sUniqueKey;

			String sSubstituteObjectId;
			
			String sSubstitutePhase;

			Map mapExistingSubstitute;	

			Map mapRMP;
			
			Map mapEBOM;
			
			String sEBOMPhase;
			
			String sError;
			StringList slSubstitutesAlreadyPresent = new StringList();

			for(Object objMap : mlSubstituteDetails)
			{
				mapSubstitute = (Map)objMap;			

				sGroupName = (String)mapSubstitute.get(pgApolloConstants.STR_GROUPNAME);
				sLayerName = (String)mapSubstitute.get(pgApolloConstants.STR_LAYERNAME);
				sObjectName = (String)mapSubstitute.get(DomainConstants.SELECT_NAME);	

				if(UIUtil.isNotNullAndNotEmpty(sGroupName) && UIUtil.isNotNullAndNotEmpty(sLayerName))
				{
					sbUniqueKey = new StringBuilder();
					sbUniqueKey.append(sGroupName).append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sLayerName).append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sObjectName);

					sUniqueKey = sbUniqueKey.toString();

					sSubstituteObjectId = (String)mapAffectedItemEBOMSubstiuteInfo.get(sUniqueKey);
					
					sbUniqueKey = new StringBuilder();
					sbUniqueKey.append(sGroupName).append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sLayerName);

					sUniqueKey = sbUniqueKey.toString();
					
					mapEBOM = (Map)mapAffectedItemEBOMInfo.get(sUniqueKey);
					
					if(null != mapEBOM && !mapEBOM.isEmpty())
					{
						if(UIUtil.isNullOrEmpty(sSubstituteObjectId))
						{
							mapRMP = pgApolloCommonUtil.getRMPIdForSync(context, sObjectName);
							
							sSubstituteObjectId = (String)mapRMP.get(DomainConstants.SELECT_ID);	
							
							sError = validateEBOMSubstitute(context, mapRMP, mapEBOM, sObjectName);
							
							if(UIUtil.isNotNullAndNotEmpty(sError))
							{
								slErrorList.add(sError);
							}
							else
							{
								mapSubstitute.put(DomainConstants.SELECT_ID, sSubstituteObjectId);
								mlNewSubstituteDetails.add(mapSubstitute);
							}					
							
						}
						else
						{
							slSubstitutesAlreadyPresent.add(sSubstituteObjectId);
							sbUniqueKey = new StringBuilder();
							sbUniqueKey.append(sGroupName).append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sLayerName);				
							slErrorList.add(PGAPPRequestConstants.STR_ERROR_SUBSTIUTE_ALREADY_PRESENT.replaceFirst("<OBJECTNAME>", sObjectName).replaceFirst("<LAYERNAME>", sbUniqueKey.toString()));
						}
					}
					else
					{
						sbUniqueKey = new StringBuilder();
						sbUniqueKey.append(sGroupName).append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sLayerName);	
						slErrorList.add(PGAPPRequestConstants.STR_ERROR_LAYER_NOT_PRESENT.replaceFirst("<LAYERNAME>", sbUniqueKey.toString()));
					}					
					

				}

			}		
			
			int iRequestedSubstituteChanges = mlSubstituteDetails.size();
			
			int iSubstitutesAlreadyExists = slSubstitutesAlreadyPresent.size();
			
			if(iSubstitutesAlreadyExists > 0 && iSubstitutesAlreadyExists == iRequestedSubstituteChanges)
			{
				bChangesAlreadyImplemented = true;
			}

			mapDataToImplement.put(sTypeOfChange, mlNewSubstituteDetails);

			if(mlNewSubstituteDetails.isEmpty())
			{
				slErrorList.add(PGAPPRequestConstants.STR_ERROR_AUTOFULLFILLMENT_NO_SUBSTIUTES_TO_IMPLEMENT);
			}
			else if(slErrorList.isEmpty())
			{
				bProceedToImplement = true;
			}

			if(!slMessageList.isEmpty())
			{
				mapDataToImplement.put(pgApolloConstants.KEY_MESSAGE, slMessageList);
			}

			context.printTrace(pgApolloConstants.TRACE_LPD, "validateAndGetDataForImplementation  : slMessageList :"+slMessageList+" mlNewSubstituteDetails : "+mlNewSubstituteDetails);

		}
		else if(PGAPPRequestConstants.RANGE_PGAPPREQUESTCHANGETYPE_PLANTS_ADDITION.equalsIgnoreCase(sTypeOfChange))
		{	
			MapList mlNewPlantDetails = new MapList();

			if(null == mapAffectedItemExistingDetails)
			{
				mapAffectedItemExistingDetails = new HashMap();
			}

			StringList slExistingPlants = (StringList)mapAffectedItemExistingDetails.getOrDefault(sTypeOfChange, new StringList());

			MapList mlPlantDetails = (MapList)mapNewRequestedChangeDetails.get(sTypeOfChange);

			context.printTrace(pgApolloConstants.TRACE_LPD, "validateAndGetDataForImplementation  : slExistingPlants :"+slExistingPlants+" mlPlantDetails : "+mlPlantDetails);

			Map mapPlant;
			String sPlantName;
			String sPlantObjectId;

			StringList slNewPlantList = new StringList();			

			for(Object objMap : mlPlantDetails)
			{
				mapPlant = (Map)objMap;				
				sPlantName = (String)mapPlant.get(DomainConstants.SELECT_NAME);
				slNewPlantList.add(sPlantName);
			}

			StringBuilder sbUniqueKey;

			Map mapNewPlantObjects = pgApolloCommonUtil.getPickListObjectIds(context, slNewPlantList, PGAPPRequestConstants.TYPE_PLANT, DomainConstants.EMPTY_STRING);

			context.printTrace(pgApolloConstants.TRACE_LPD, "validateAndGetDataForImplementation  : mapNewPlantObjects :"+mapNewPlantObjects+" slNewPlantList : "+slNewPlantList);

			StringList slPlantsAlreadyPresent = new StringList();

			for(Object objMap : mlPlantDetails)
			{
				mapPlant = (Map)objMap;				
				sPlantName = (String)mapPlant.get(DomainConstants.SELECT_NAME);

				sPlantObjectId = (String)mapNewPlantObjects.get(sPlantName);

				if(UIUtil.isNotNullAndNotEmpty(sPlantObjectId) && !slExistingPlants.contains(sPlantObjectId))
				{
					mapPlant.put(DomainConstants.SELECT_ID, sPlantObjectId);
					mlNewPlantDetails.add(mapPlant);
				}
				else
				{
					slPlantsAlreadyPresent.add(sPlantName);
				}
			}
			
			int iRequestedPlantChanges = mlPlantDetails.size();
			
			int iPlantsAlreadyPresent = slPlantsAlreadyPresent.size();

			if(!slPlantsAlreadyPresent.isEmpty())
			{
				sErrorMessage = new StringBuilder(PGAPPRequestConstants.STR_ERROR_PLANT_ALREADY_PRESENT).append(pgApolloConstants.CONSTANT_STRING_PIPE).append(StringUtil.join(slPlantsAlreadyPresent, pgApolloConstants.CONSTANT_STRING_COMMA)).toString();
				slErrorList.add(sErrorMessage);
				
				if(iPlantsAlreadyPresent == iRequestedPlantChanges)
				{
					bChangesAlreadyImplemented = true;
				}
			}

			if(mlNewPlantDetails.isEmpty())
			{
				slErrorList.add(PGAPPRequestConstants.STR_ERROR_AUTOFULLFILLMENT_NO_PLANTS_TO_IMPLEMENT);
			}
			else if(slErrorList.isEmpty())
			{
				bProceedToImplement = true;
			}

			mapDataToImplement.put(sTypeOfChange, mlNewPlantDetails);
			if(!slMessageList.isEmpty())
			{
				mapDataToImplement.put(pgApolloConstants.KEY_MESSAGE, slMessageList);
			}

			context.printTrace(pgApolloConstants.TRACE_LPD, "validateAndGetDataForImplementation  : slMessageList :"+slMessageList+" mlNewPlantDetails : "+mlNewPlantDetails);

		}
		else if(PGAPPRequestConstants.RANGE_PGAPPREQUESTCHANGETYPE_MFG_STATUS_CHANGE.equalsIgnoreCase(sTypeOfChange))
		{
			Map mapAttributes = new HashMap();
			Map mapExistingAttributes;

			if(null == mapAffectedItemExistingDetails)
			{
				mapAffectedItemExistingDetails = new HashMap();
				mapExistingAttributes = new HashMap();
			}
			else
			{
				mapExistingAttributes = (Map)mapAffectedItemExistingDetails.get(sTypeOfChange);
			}

			Map mapNewDetailsRequested = (Map)mapNewRequestedChangeDetails.get(sTypeOfChange);

			context.printTrace(pgApolloConstants.TRACE_LPD, "validateAndGetDataForImplementation  : mapExistingAttributes :"+mapExistingAttributes+" mapNewDetailsRequested : "+mapNewDetailsRequested);

			String sNewMfgStatus = (String)mapNewDetailsRequested.get(PGAPPRequestConstants.KEY_MANUFACTURINGSTATUS);

			String sExistingMfgStatus = (String)mapExistingAttributes.get(pgApolloConstants.ATTRIBUTE_PG_LIFECYCLE_STATUS);

			StringList slValidMfgList = (StringList)mapExistingAttributes.get(PGAPPRequestConstants.KEY_MANUFACTURINGSTATUS);

			if(null != sExistingMfgStatus && sExistingMfgStatus.equalsIgnoreCase(sNewMfgStatus))
			{
				bChangesAlreadyImplemented = true;
				slMessageList.add(PGAPPRequestConstants.STR_ERROR_MFG_STATUS_ALREADY_SET);
			}
			else if(null != slValidMfgList && slValidMfgList.contains(sNewMfgStatus))
			{
				mapAttributes.put(pgApolloConstants.ATTRIBUTE_PG_LIFECYCLE_STATUS, sNewMfgStatus);
				bProceedToImplement = true;
			}
			else
			{
				slErrorList.add(PGAPPRequestConstants.STR_ERROR_MFG_STATUS_INVALID);
			}

			mapDataToImplement.put(sTypeOfChange, mapAttributes);
			if(!slMessageList.isEmpty())
			{
				mapDataToImplement.put(pgApolloConstants.KEY_MESSAGE, slMessageList);
			}			

			context.printTrace(pgApolloConstants.TRACE_LPD, "validateAndGetDataForImplementation  : mapDataToImplement :"+mapDataToImplement);

		}
		else if(PGAPPRequestConstants.RANGE_PGAPPREQUESTCHANGETYPE_EXPIRATION_DATE_CHANGE.equalsIgnoreCase(sTypeOfChange))
		{
			Map mapAttributes = new HashMap();
			Map mapExistingAttributes;

			if(null == mapAffectedItemExistingDetails)
			{
				mapAffectedItemExistingDetails = new HashMap();
				mapExistingAttributes = new HashMap();
			}
			else
			{
				mapExistingAttributes = (Map)mapAffectedItemExistingDetails.get(sTypeOfChange);
			}

			Map mapNewDetailsRequested = (Map)mapNewRequestedChangeDetails.get(sTypeOfChange);

			context.printTrace(pgApolloConstants.TRACE_LPD, "validateAndGetDataForImplementation  : mapExistingAttributes :"+mapExistingAttributes+" mapNewDetailsRequested :"+mapNewDetailsRequested);

			String sNewExpirationDate = (String)mapNewDetailsRequested.get(PGAPPRequestConstants.KEY_EXPIRATIONDATE);

			String sExistingExpirationDate = (String)mapExistingAttributes.get(pgApolloConstants.ATTRIBUTE_EXPIRATION_DATE);		
			
			String sExistingFormattedExpirationDate = DomainConstants.EMPTY_STRING;
			
			if(null != sExistingExpirationDate)
			{	
				sExistingFormattedExpirationDate = PGAPPRequestUtil.parseDateForDashboard(sExistingExpirationDate);
			}			
			
			if(sExistingFormattedExpirationDate.equalsIgnoreCase(sNewExpirationDate))
			{
				bChangesAlreadyImplemented = true;
				slMessageList.add(PGAPPRequestConstants.STR_ERROR_EXPIRATION_DATE_ALREADY_SET);
			}	
			else 
			{
				if(UIUtil.isNotNullAndNotEmpty(sNewExpirationDate))
				{
					sNewExpirationDate = convertAgGridToStandardDateFormat(context, sNewExpirationDate);
					mapAttributes.put(pgApolloConstants.ATTRIBUTE_EXPIRATION_DATE, sNewExpirationDate);
					bProceedToImplement = true;
				}	
				else
				{
					slErrorList.add(PGAPPRequestConstants.STR_ERROR_AUTOFULLFILLMENT_NO_EXPIRATIONDATE_TO_IMPLEMENT);
				}
			}			

			mapDataToImplement.put(sTypeOfChange, mapAttributes);
			if(!slMessageList.isEmpty())
			{
				mapDataToImplement.put(pgApolloConstants.KEY_MESSAGE, slMessageList);
			}

			context.printTrace(pgApolloConstants.TRACE_LPD, "validateAndGetDataForImplementation  : mapDataToImplement :"+mapDataToImplement);

		}	
		else if(PGAPPRequestConstants.RANGE_PGAPPREQUESTCHANGETYPE_ACCELERATED_RELEASE_ADDITION.equalsIgnoreCase(sTypeOfChange))
		{
			if(!DomainConstants.STATE_PART_RELEASE.equals(sImplementedItemCurrent) && !pgApolloConstants.CRITERIA_STATE_RELEASED.equalsIgnoreCase(sImplementedItemCurrent))
			{
				String sMessage = new StringBuilder(PGAPPRequestConstants.STR_ERROR_AUTOFULLFILLMENT_ACCELERATED_RELEASE).append("'").append(sTypeOfChange).append("'").toString();
				context.printTrace(pgApolloConstants.TRACE_LPD, "validateAndGetDataForImplementation sMessage : "+sMessage);
				slErrorList.add(sMessage);
			}

		}
		else if(PGAPPRequestConstants.RANGE_PGAPPREQUESTCHANGETYPE_OBSOLESENCE.equalsIgnoreCase(sTypeOfChange))
		{
			if(!DomainConstants.STATE_PART_OBSOLETE.equals(sImplementedItemCurrent))
			{
				String sMessage = new StringBuilder(PGAPPRequestConstants.STR_ERROR_AUTOFULLFILLMENT_OBSOLESCENCE).append("'").append(sTypeOfChange).append("'").toString();
				context.printTrace(pgApolloConstants.TRACE_LPD, "validateAndGetDataForImplementation sMessage : "+sMessage);
				slErrorList.add(sMessage);
			}

		}
			
		
		mapDataToImplement.put(PGAPPRequestConstants.KEY_PROCEED_TO_IMPLEMENT, bProceedToImplement);
		mapDataToImplement.put(PGAPPRequestConstants.KEY_CHANGES_IMPLEMENTED, bChangesAlreadyImplemented);

		context.printTrace(pgApolloConstants.TRACE_LPD, "PGAPPRequestImplementation : validateAndGetDataForImplementation << mapDataToImplement :"+mapDataToImplement);

		if(!slErrorList.isEmpty())
		{
			mapDataToImplement.put(pgApolloConstants.STR_ERROR_LIST, slErrorList);
		}

		return mapDataToImplement;
	}


	/**
	 * Method to validate EBOM Substitute
	 * @param context 
	 * @param mapRMP
	 * @param mapEBOM
	 * @param sSubstituteName 
	 * @return
	 */
	private static String validateEBOMSubstitute(Context context, Map mapRMP, Map mapEBOM, String sSubstituteName) 
	{
		String sReturn = DomainConstants.EMPTY_STRING;
		
		if(null != mapRMP && null != mapEBOM)
		{
			String sEBOMReleasePhase = (String)mapEBOM.get(pgApolloConstants.SELECT_ATTRIBUTE_RELEASE_PHASE);
			
			String sEBOMType = (String)mapEBOM.get(DomainConstants.SELECT_TYPE);
			
			String sSubstituteReleasePhase = (String)mapRMP.get(pgApolloConstants.SELECT_ATTRIBUTE_RELEASE_PHASE);
			
			if(UIUtil.isNotNullAndNotEmpty(sEBOMReleasePhase) && UIUtil.isNotNullAndNotEmpty(sSubstituteReleasePhase))
			{
				String sAllowedStages = EnoviaResourceBundle.getProperty(context, "emxCPN", context.getLocale(),"emxCPN.CreatePDBOM."+sEBOMReleasePhase+".FormulationPart.AllwedStages");
				
				if(!sAllowedStages.contains(sSubstituteReleasePhase)){					

					sReturn = EnoviaResourceBundle.getProperty(context, "emxCPNStringResource",context.getLocale(), "emxCPN.CreatePDBOM.InvalidStage.Error.Alert");
					
					sReturn = new StringBuilder(sReturn).append(" for ").append(sSubstituteName).append(pgApolloConstants.CONSTANT_STRING_COLON).append(sAllowedStages).toString();
				}
				
			}
			else
			{
				sReturn = pgApolloConstants.STR_ERROR;
			}

		}
		
		return sReturn;
	}


	/**
	 * Method to get Affected Item Existing Info
	 * @param context
	 * @param domImplementedItem
	 * @param sTypeOfChange
	 * @return
	 * @throws MatrixException 
	 */
	public static Map getAffectedItemExistingInfo(Context context, String sImplementedItemPhysicalId,	String sTypeOfChange) throws MatrixException
	{
		Map mapAffectedItemExistingInfo = new HashMap();

		context.printTrace(pgApolloConstants.TRACE_LPD, "PGAPPRequestImplementation : getAffectedItemExistingInfo >> : sImplementedItemPhysicalId :"+sImplementedItemPhysicalId+" sTypeOfChange :"+sTypeOfChange);

		DomainObject domImplementedItem = DomainObject.newInstance(context, sImplementedItemPhysicalId);

		if(PGAPPRequestConstants.RANGE_PGAPPREQUESTCHANGETYPE_SUBSTITUTE_ADDITION.equalsIgnoreCase(sTypeOfChange))
		{

			StringList slObjectSelects = new StringList();
			slObjectSelects.add(DomainConstants.SELECT_TYPE);			
			slObjectSelects.add(DomainConstants.SELECT_ID);			
			slObjectSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_RELEASE_PHASE);

			StringList slEBOMRelSelectable = new StringList();
			slEBOMRelSelectable.add(DomainRelationship.SELECT_ID);
			slEBOMRelSelectable.add(pgApolloConstants.SELECT_ATTRIBUTE_PLYNAME);
			slEBOMRelSelectable.add(pgApolloConstants.SELECT_ATTRIBUTE_PLYGROUPNAME);
			slEBOMRelSelectable.add("frommid["+EngineeringConstants.RELATIONSHIP_EBOM_SUBSTITUTE+"].id");


			MapList mlAPPEBOMList = domImplementedItem.getRelatedObjects(context,						         // Context
					DomainConstants.RELATIONSHIP_EBOM,       // relationship pattern
					pgApolloConstants.TYPE_RAW_MATERIAL,          // type pattern
					slObjectSelects,               // object selects
					slEBOMRelSelectable,                // relationship selects
					false,                                   // to direction
					true,                                    // from direction
					(short)1,                                // recursion level
					DomainConstants.EMPTY_STRING,            // object where clause
					null,									 // Relationship Where Clause
					0);										 // Limit

			context.printTrace(pgApolloConstants.TRACE_LPD, "getAffectedItemExistingInfo  : mlAPPEBOMList :"+mlAPPEBOMList);

			Map mapAffectedItemSubstituteInfo = new HashMap();
			Map mapAffectedItemEBOMInfo = new HashMap();

			if(!mlAPPEBOMList.isEmpty())
			{
				Map mapLocalEBOM;
				StringList slEBOMSubstituteRelIds;
				StringList slAllEBOMSubstituteRelIds = new StringList();
				StringBuilder sbKeyBuilder;
				String sSubstitutePlyGroupName;
				String sSubstitutePlyName;
				String sUniqueKey;
				String sToObjectName;
				String sToObjectId;			
				Map mapEBOM;
				String sEBOMReleasePhase;
				String sEBOMPlyName;
				String sEBOMPlyGroupName;
				String sEBOMType;


				for(int i=0; i<mlAPPEBOMList.size(); i++)
				{
					mapEBOM = (Map)mlAPPEBOMList.get(i);	
					sEBOMPlyGroupName = (String)mapEBOM.get(pgApolloConstants.SELECT_ATTRIBUTE_PLYGROUPNAME);
					sEBOMPlyName = (String)mapEBOM.get(pgApolloConstants.SELECT_ATTRIBUTE_PLYNAME);	
					sEBOMReleasePhase = (String)mapEBOM.get(pgApolloConstants.SELECT_ATTRIBUTE_RELEASE_PHASE);
					sEBOMType = (String)mapEBOM.get(DomainConstants.SELECT_TYPE);

					sbKeyBuilder = new StringBuilder();
					sbKeyBuilder.append(sEBOMPlyGroupName);
					sbKeyBuilder.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sEBOMPlyName);						
					sUniqueKey = sbKeyBuilder.toString();
					
					mapLocalEBOM = new HashMap();
					mapLocalEBOM.put(pgApolloConstants.SELECT_ATTRIBUTE_RELEASE_PHASE, sEBOMReleasePhase);
					mapLocalEBOM.put(DomainConstants.SELECT_TYPE, sEBOMType);
					
					mapAffectedItemEBOMInfo.put(sUniqueKey, mapLocalEBOM);
					
					slEBOMSubstituteRelIds =  pgApolloCommonUtil.getStringListFromObject(mapEBOM.get("frommid["+EngineeringConstants.RELATIONSHIP_EBOM_SUBSTITUTE+"].id"));
					if(!slEBOMSubstituteRelIds.isEmpty())
					{
						slAllEBOMSubstituteRelIds.addAll(slEBOMSubstituteRelIds);
					}
				}

				context.printTrace(pgApolloConstants.TRACE_LPD, "getAffectedItemExistingInfo  : slAllEBOMSubstituteRelIds :"+slAllEBOMSubstituteRelIds);

				if(null != slAllEBOMSubstituteRelIds && !slAllEBOMSubstituteRelIds.isEmpty())
				{
					StringList slEBOMSubstituteSelectable = new StringList();
					slEBOMSubstituteSelectable.add(DomainRelationship.SELECT_ID);
					slEBOMSubstituteSelectable.add(pgApolloConstants.SELECT_ATTRIBUTE_PLYNAME);
					slEBOMSubstituteSelectable.add(pgApolloConstants.SELECT_ATTRIBUTE_PLYGROUPNAME);
					slEBOMSubstituteSelectable.add(DomainConstants.SELECT_TO_NAME);
					slEBOMSubstituteSelectable.add(DomainConstants.SELECT_TO_ID);

					String[] substituteRelArray = slAllEBOMSubstituteRelIds.toArray(new String[slAllEBOMSubstituteRelIds.size()]);

					MapList mlSubstituteInfoList = DomainRelationship.getInfo(context, substituteRelArray, slEBOMSubstituteSelectable);	

					context.printTrace(pgApolloConstants.TRACE_LPD, "getAffectedItemExistingInfo  : mlSubstituteInfoList :"+mlSubstituteInfoList);

					Map mapEBOMSubstitute;
					Map mapLocalEBOMSubstitute;

					if(!mlSubstituteInfoList.isEmpty())
					{
						for(int i=0; i<mlSubstituteInfoList.size(); i++)
						{
							mapEBOMSubstitute = (Map)mlSubstituteInfoList.get(i);

							sSubstitutePlyGroupName = (String)mapEBOMSubstitute.get(pgApolloConstants.SELECT_ATTRIBUTE_PLYGROUPNAME);
							sSubstitutePlyName = (String)mapEBOMSubstitute.get(pgApolloConstants.SELECT_ATTRIBUTE_PLYNAME);				
							sToObjectName = (String)mapEBOMSubstitute.get(DomainConstants.SELECT_TO_NAME);		
							sToObjectId = (String)mapEBOMSubstitute.get(DomainConstants.SELECT_TO_ID);				

							sbKeyBuilder = new StringBuilder();
							sbKeyBuilder.append(sSubstitutePlyGroupName);
							sbKeyBuilder.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sSubstitutePlyName);	
							sbKeyBuilder.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sToObjectName);	

							sUniqueKey = sbKeyBuilder.toString();

							mapAffectedItemSubstituteInfo.put(sUniqueKey, sToObjectId);
						}
					}	

				}

			}	
			
			Map mapEBOMAndSubstituteInfo = new HashMap();
			mapEBOMAndSubstituteInfo.put(DomainConstants.RELATIONSHIP_EBOM, mapAffectedItemEBOMInfo);
			mapEBOMAndSubstituteInfo.put(EngineeringConstants.RELATIONSHIP_EBOM_SUBSTITUTE, mapAffectedItemSubstituteInfo);

			mapAffectedItemExistingInfo.put(PGAPPRequestConstants.RANGE_PGAPPREQUESTCHANGETYPE_SUBSTITUTE_ADDITION, mapEBOMAndSubstituteInfo);

			context.printTrace(pgApolloConstants.TRACE_LPD, "getAffectedItemExistingInfo  : mapEBOMAndSubstituteInfo :"+mapEBOMAndSubstituteInfo);

		}
		else if(PGAPPRequestConstants.RANGE_PGAPPREQUESTCHANGETYPE_PLANTS_ADDITION.equalsIgnoreCase(sTypeOfChange))
		{	

			StringList slExistingConnectedPlants = new StringList();

			MapList mlConnectedPlants= domImplementedItem.getRelatedObjects(context,  // Context
					pgApolloConstants.RELATIONSHIP_MANUFACTURINGRESPONSIBILITY, //Relationship Pattern
					pgApolloConstants.TYPE_PLANT, // Type Pattern
					new StringList(DomainConstants.SELECT_ID), // Object Select List
					new StringList(DomainConstants.SELECT_RELATIONSHIP_ID), //relationship select list
					true, //get To
					false, //get From
					(short)1, // recurse level
					null, // Object where clause
					null, // Relationship where clause
					0); // Object Limit

			context.printTrace(pgApolloConstants.TRACE_LPD, "getAffectedItemExistingInfo  : mlConnectedPlants :"+mlConnectedPlants);

			if(null != mlConnectedPlants && !mlConnectedPlants.isEmpty())
			{
				Map mapPlant;
				String sPlantId;
				String sPlantRelId;

				for(Object objectMap : mlConnectedPlants)
				{
					mapPlant = (Map)objectMap;
					sPlantId = (String)mapPlant.get(DomainConstants.SELECT_ID);	
					slExistingConnectedPlants.add(sPlantId);
				}
			}		

			context.printTrace(pgApolloConstants.TRACE_LPD, "getAffectedItemExistingInfo  : slExistingConnectedPlants :"+slExistingConnectedPlants);

			mapAffectedItemExistingInfo.put(PGAPPRequestConstants.RANGE_PGAPPREQUESTCHANGETYPE_PLANTS_ADDITION, slExistingConnectedPlants);
		}
		else if(PGAPPRequestConstants.RANGE_PGAPPREQUESTCHANGETYPE_MFG_STATUS_CHANGE.equalsIgnoreCase(sTypeOfChange))
		{
			StringList slObjectSelects = new StringList();
			slObjectSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_RELEASE_PHASE);
			slObjectSelects.add(PGAPPRequestConstants.SELECT_ATTRIBUTE_PG_LIFECYCLE_STATUS);

			Map mapImplementedItemInfo = domImplementedItem.getInfo(context, slObjectSelects);

			context.printTrace(pgApolloConstants.TRACE_LPD, "getAffectedItemExistingInfo  : mapImplementedItemInfo Mfg Status Change :"+mapImplementedItemInfo);

			String sReleasePhase = (String)mapImplementedItemInfo.get(pgApolloConstants.SELECT_ATTRIBUTE_RELEASE_PHASE);

			String sExistingMfgStatus = (String)mapImplementedItemInfo.get(PGAPPRequestConstants.SELECT_ATTRIBUTE_PG_LIFECYCLE_STATUS);

			StringList slMfgStatusValues=new StringList();

			if(pgApolloConstants.STR_DEVELOPMENT_PHASE.equals(sReleasePhase)){
				slMfgStatusValues=(StringList)PGAPPRequestUtil.getStringListPickListItems(context, PGAPPRequestConstants.CONST_PICKLIST_LIFECYCLESTATUS_EXPERIMENTAL);
			}else if(pgApolloConstants.STR_PILOT_PHASE.equals(sReleasePhase)){
				slMfgStatusValues=(StringList)PGAPPRequestUtil.getStringListPickListItems(context, PGAPPRequestConstants.CONST_PICKLIST_LIFECYCLESTATUS_PILOT);
			}else if(pgApolloConstants.STR_PRODUCION_PHASE.equals(sReleasePhase)){
				slMfgStatusValues=(StringList)PGAPPRequestUtil.getStringListPickListItems(context, PGAPPRequestConstants.CONST_PICKLIST_LIFECYCLESTATUS_PRODUCTION);
			}

			context.printTrace(pgApolloConstants.TRACE_LPD, "getAffectedItemExistingInfo  : slMfgStatusValues :"+slMfgStatusValues);

			Map mapMfgStatus = new HashMap();
			mapMfgStatus.put(PGAPPRequestConstants.ATTRIBUTE_PG_LIFECYCLE_STATUS, sExistingMfgStatus);
			mapMfgStatus.put(PGAPPRequestConstants.KEY_MANUFACTURINGSTATUS, slMfgStatusValues);

			context.printTrace(pgApolloConstants.TRACE_LPD, "getAffectedItemExistingInfo  : mapMfgStatus :"+mapMfgStatus);

			mapAffectedItemExistingInfo.put(PGAPPRequestConstants.RANGE_PGAPPREQUESTCHANGETYPE_MFG_STATUS_CHANGE, mapMfgStatus);
		}
		else if(PGAPPRequestConstants.RANGE_PGAPPREQUESTCHANGETYPE_EXPIRATION_DATE_CHANGE.equalsIgnoreCase(sTypeOfChange))
		{			
			StringList slObjectSelects = new StringList();
			slObjectSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_EXPIRATION_DATE);

			Map mapImplementedItemInfo = domImplementedItem.getInfo(context, slObjectSelects);

			context.printTrace(pgApolloConstants.TRACE_LPD, "getAffectedItemExistingInfo  : mapImplementedItemInfo Expiration Date :"+mapImplementedItemInfo);

			String sExistingExpirationDate = (String)mapImplementedItemInfo.get(pgApolloConstants.SELECT_ATTRIBUTE_EXPIRATION_DATE);

			

			Map mapExpirationDate = new HashMap();
			mapExpirationDate.put(pgApolloConstants.ATTRIBUTE_EXPIRATION_DATE, sExistingExpirationDate);

			context.printTrace(pgApolloConstants.TRACE_LPD, "getAffectedItemExistingInfo  : mapExpirationDate :"+mapExpirationDate);

			mapAffectedItemExistingInfo.put(PGAPPRequestConstants.RANGE_PGAPPREQUESTCHANGETYPE_EXPIRATION_DATE_CHANGE, mapExpirationDate);
		}

		context.printTrace(pgApolloConstants.TRACE_LPD, "PGAPPRequestImplementation : getAffectedItemExistingInfo << : mapAffectedItemExistingInfo :"+mapAffectedItemExistingInfo);

		return mapAffectedItemExistingInfo;
	}


	/**
	 * 
	 * Method to get Requested Change Info for given Request Set
	 * @param context
	 * @param slAllRequestDataList
	 * @return
	 * @throws Exception 
	 */
	public static Map getRequestedChangeDetailsInfo(Context context, StringList slAllRequestDataList) throws Exception 
	{
		Map mapParentRequesRequestedChangeInfo = new HashMap();

		StringList slRequestDataSelects = new StringList();
		slRequestDataSelects.add(DomainConstants.SELECT_NAME);
		slRequestDataSelects.add(DomainConstants.SELECT_PHYSICAL_ID);
		slRequestDataSelects.add(DomainConstants.SELECT_CURRENT);
		slRequestDataSelects.add(PGAPPRequestConstants.SELECT_ATTRIBUTE_PG_APP_REQUESTCHANGETYPE);
		slRequestDataSelects.add(PGAPPRequestConstants.SELECT_ATTRIBUTE_PG_APP_REQUEST_SUBSTITUTES);
		slRequestDataSelects.add(PGAPPRequestConstants.SELECT_ATTRIBUTE_PG_APPREQUESTPLANTS);
		slRequestDataSelects.add(PGAPPRequestConstants.SELECT_ATTRIBUTE_PG_LPD_DUE_DATE);
		slRequestDataSelects.add(PGAPPRequestConstants.SELECT_ATTRIBUTE_PG_EXPIRATIONDATE);
		slRequestDataSelects.add(PGAPPRequestConstants.SELECT_ATTRIBUTE_PGAPPREQUESTCOMMENTS);
		slRequestDataSelects.add(PGAPPRequestConstants.SELECT_ATTRIBUTE_PG_LIFECYCLE_STATUS);

		StringList slRequestDataMultiValueSelects = new StringList();

		String sMultiValueAttributes = pgApolloCommonUtil.getPageProperty(context, pgApolloConstants.STR_APOLLO_CONFIG_PAGE_FILENAME, "pgApolloConfiguration.APPRequestManagement.APPRequestData.MultiValueSelectableAttributeMapping");

		StringList slMultiValueAttributeList = StringUtil.split(sMultiValueAttributes, pgApolloConstants.CONSTANT_STRING_PIPE);
		for(String sSelectable : slRequestDataSelects)
		{
			if(slMultiValueAttributeList.contains(sSelectable))
			{
				slRequestDataMultiValueSelects.add(sSelectable);
			}
		}

		MapList mlRequestDataList = DomainObject.getInfo(context, slAllRequestDataList.toArray(new String[slAllRequestDataList.size()]), slRequestDataSelects, slRequestDataMultiValueSelects);

		Map mapRequestChangeDetails;
		Map mapRequestChangesRequested;

		if(null != mlRequestDataList && !mlRequestDataList.isEmpty())
		{   			
			Map localMap;
			Map mapRequestDataInfo;
			String sTypeOfChange;
			String sRequestDataPhysicalId;

			for(Object objectMap : mlRequestDataList)
			{
				localMap = (Map)objectMap;
				sRequestDataPhysicalId = (String)localMap.get(DomainConstants.SELECT_PHYSICAL_ID);
				sTypeOfChange = (String)localMap.get(PGAPPRequestConstants.SELECT_ATTRIBUTE_PG_APP_REQUESTCHANGETYPE);		

				mapRequestDataInfo = new HashMap();
				mapRequestDataInfo = PGAPPRequestUtil.getRequestDataChangeDetails(sTypeOfChange, localMap, mapRequestDataInfo);	
				mapRequestChangeDetails = (Map)mapRequestDataInfo.get(PGAPPRequestConstants.KEY_REQUEST_CHANGE_DETAILS);
				if(null == mapRequestChangeDetails)
				{
					mapRequestChangeDetails = new HashMap();
				}
				mapRequestChangesRequested = new HashMap();
				mapRequestChangesRequested.put(PGAPPRequestConstants.ATTRIBUTE_PG_APP_REQUESTCHANGETYPE, sTypeOfChange);				
				mapRequestChangesRequested.put(sTypeOfChange, mapRequestChangeDetails);

				mapParentRequesRequestedChangeInfo.put(sRequestDataPhysicalId, mapRequestChangesRequested);
			}
		}


		return mapParentRequesRequestedChangeInfo;
	}



	/**
	 * Method to get actual plant attribute values
	 * @param mapPlantAttributeDetails
	 * @return
	 */
	public Map getActualPlantAttributeDetails(Map mapAttribute)
	{
		if(null != mapAttribute && !mapAttribute.isEmpty())
		{
			Set<String> setAttributeKeys = mapAttribute.keySet();

			for(String sAttributeKey : setAttributeKeys)
			{
				mapAttribute.replace(sAttributeKey, "No", "FALSE");
				mapAttribute.replace(sAttributeKey, "Yes", "TRUE");
			}
		}

		return mapAttribute;
	}

	/**
	 * Method to generate Output Summary and Send Notification
	 * @param context
	 * @param mlRequestOutput
	 * @param mapBackgroundProcess
	 * @param bBackgroundJobProcessError
	 * @return
	 * @throws Exception
	 */
	public String generateOutputSummaryAndSendNotification(Context context, MapList mlRequestOutput, Map mapBackgroundProcess, boolean bBackgroundJobProcessError) throws Exception 
	{		
		context.printTrace(pgApolloConstants.TRACE_LPD, "PGAPPRequestImplementation : generateOutputSummaryAndSendNotification mlRequestOutput = "+mlRequestOutput+" mapBackgroundProcess = "+mapBackgroundProcess+" bBackgroundJobProcessError "+bBackgroundJobProcessError);

		String sError = DomainConstants.EMPTY_STRING;
		try
		{
			Map mapRequestOutput;

			if(!mlRequestOutput.isEmpty())
			{
				MapList mlXLSOutput = new MapList();

				int i = 0;
				String sLocalError;
				String sLocalMessage;
				StringList slErrorList;
				StringList slMessageList;
				String sObjectCurrent;
				String sObjectType;
				String sObjectPolicy;
				String sTypeOfChange;
				String sTypeOfChangeLabel;
				
				Map mapAPPRequestTypeOfChangeMapping = pgApolloCommonUtil.prepareAttributeMappingBasedOnConfiguration(context, pgApolloConstants.STR_APOLLO_CONFIG_PAGE_FILENAME, "pgApolloConfiguration.APPRequestManagement.TypeOfChangeMapping", true);

				String sLanguage = Locale.ENGLISH.getLanguage();

				for(Object objMap : mlRequestOutput)
				{
					mapRequestOutput = (Map)objMap;
					i+=1;

					sObjectType = (String)mapRequestOutput.get(PGAPPRequestConstants.KEY_IMPLEMENTED_ITEM_TYPE);

					sObjectPolicy = (String)mapRequestOutput.get(PGAPPRequestConstants.KEY_IMPLEMENTED_ITEM_POLICY);

					sObjectCurrent = (String)mapRequestOutput.get(PGAPPRequestConstants.KEY_IMPLEMENTED_ITEM_STATE);
					
					sTypeOfChange = (String)mapRequestOutput.get(PGAPPRequestConstants.ATTRIBUTE_PG_APP_REQUESTCHANGETYPE);
					
					sTypeOfChangeLabel = (String)mapAPPRequestTypeOfChangeMapping.getOrDefault(sTypeOfChange, sTypeOfChange);
					mapRequestOutput.put(PGAPPRequestConstants.KEY_TYPE_OF_CHANGE, sTypeOfChangeLabel);

					if(!pgApolloConstants.STR_DENIED.equalsIgnoreCase(sObjectCurrent))
					{
						sObjectCurrent = EnoviaResourceBundle.getStateI18NString(context, sObjectPolicy ,sObjectCurrent, sLanguage);			
					}

					mapRequestOutput.put(PGAPPRequestConstants.KEY_IMPLEMENTED_ITEM_STATE, sObjectCurrent);

					if(UIUtil.isNotNullAndNotEmpty(sObjectType))
					{
						sObjectType = EnoviaResourceBundle.getAdminI18NString(context, "Type", sObjectType, Locale.US.getLanguage());
					}
					else
					{
						sObjectType = DomainConstants.EMPTY_STRING;
					}

					mapRequestOutput.put(PGAPPRequestConstants.KEY_IMPLEMENTED_ITEM_TYPE, sObjectType);

					mapRequestOutput.put(DomainConstants.ATTRIBUTE_COUNT, Integer.toString(i));

					slErrorList = (StringList)mapRequestOutput.getOrDefault(pgApolloConstants.STR_ERROR_LIST, new StringList());

					if(null != slErrorList && !slErrorList.isEmpty())
					{
						mapRequestOutput.put(DomainConstants.SELECT_STATUS, pgApolloConstants.STR_ERROR);
						sLocalError = StringUtil.join(slErrorList, pgApolloConstants.CONSTANT_STRING_NEWLINE);
						mapRequestOutput.put(pgApolloConstants.STR_ERROR, sLocalError);
					}
					else
					{
						mapRequestOutput.put(DomainConstants.SELECT_STATUS, pgApolloConstants.STR_SUCCESS);
						mapRequestOutput.put(pgApolloConstants.STR_ERROR, DomainConstants.EMPTY_STRING);
					}

					slMessageList = (StringList)mapRequestOutput.getOrDefault(pgApolloConstants.KEY_MESSAGE, new StringList());
					if(null != slMessageList && !slMessageList.isEmpty())
					{
						sLocalMessage = StringUtil.join(slMessageList, pgApolloConstants.CONSTANT_STRING_NEWLINE);
						mapRequestOutput.put(pgApolloConstants.KEY_MESSAGE, sLocalMessage);
					}
					else
					{
						mapRequestOutput.put(pgApolloConstants.KEY_MESSAGE, DomainConstants.EMPTY_STRING);
					}

					mlXLSOutput.add(mapRequestOutput);
				}

				String sBackgroundJobName = (String)mapBackgroundProcess.get(DomainConstants.SELECT_NAME);
				String sBackgroundJobTitle = (String)mapBackgroundProcess.get(pgApolloConstants.SELECT_ATTRIBUTE_TITLE);

				String sHeaderList = pgApolloCommonUtil.getPageProperty(context, pgApolloConstants.STR_APOLLO_CONFIG_PAGE_FILENAME,"pgApolloConfiguration.APPRequestManagement.Implementation.OutputSummaryHeaderMapping");
				String sSelectList = pgApolloCommonUtil.getPageProperty(context, pgApolloConstants.STR_APOLLO_CONFIG_PAGE_FILENAME,"pgApolloConfiguration.APPRequestManagement.Implementation.OutputSummarySelectMapping");

				StringList slHeaderList = StringUtil.split(sHeaderList, pgApolloConstants.CONSTANT_STRING_PIPE);
				StringList slSelectList = StringUtil.split(sSelectList, pgApolloConstants.CONSTANT_STRING_PIPE);

				StringBuilder sbFileName = new StringBuilder();
				sbFileName.append(sBackgroundJobName).append(pgApolloConstants.CONSTANT_STRING_UNDERSCORE).append(sBackgroundJobTitle).append(pgApolloConstants.STR_XLSXFILE_EXTENSION).toString();
				String sWorkspacePath = context.createWorkspace();

				StringBuilder sbFilePathWithName = new StringBuilder();
				sbFilePathWithName.append(sWorkspacePath).append(File.separator).append(sbFileName.toString());

				String sFileName = sbFileName.toString();
				String sReportFile = sbFilePathWithName.toString();

				context.printTrace(pgApolloConstants.TRACE_LPD, "PGAPPRequestImplementation : generateOutputSummaryAndSendNotification mlXLSOutput = "+mlXLSOutput);
				context.printTrace(pgApolloConstants.TRACE_LPD, "PGAPPRequestImplementation : generateOutputSummaryAndSendNotification slSelectList = "+slSelectList);

				pgApolloCommonUtil.writeXLSXFile(context, mlXLSOutput, slHeaderList, slSelectList, sFileName, sReportFile);

				pgApolloCommonUtil.sendMail(context, sBackgroundJobName, sReportFile, mapBackgroundProcess, bBackgroundJobProcessError, "emxCPN.APPRequestManagement.Implementation.Mail.Message", "emxCPN.APPRequestManagement.Implementation.Mail.Subject");

				Files.deleteIfExists(Paths.get(sReportFile));

			}
		} 
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
			sError = new StringBuilder(pgApolloConstants.STR_ERROR_REPORT_GENERATION_NOTIFICATION).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getMessage()).toString();								
		}
		return sError;
	}

	/**
	 * Method to convert date format from Ag grid to standard format
	 * @param inputDate
	 * @return
	 */
	public static String convertAgGridToStandardDateFormat(Context context, String sInputDate) {
		try {
			if(UIUtil.isNotNullAndNotEmpty(sInputDate))
			{
				// Input date format: "yyyy-MM-dd"
				SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date date = inputDateFormat.parse(sInputDate);

				// Output date format: "MMM/dd/yyyy"
				SimpleDateFormat outputDateFormat = new SimpleDateFormat(eMatrixDateFormat.getEMatrixDateFormat(), context.getLocale());
				return outputDateFormat.format(date);
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return sInputDate;
	}
	

}

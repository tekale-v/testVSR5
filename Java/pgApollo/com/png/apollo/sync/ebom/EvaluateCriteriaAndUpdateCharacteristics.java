package com.png.apollo.sync.ebom;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.dassault_systemes.enovia.characteristic.interfaces.ENOICharacteristic;
import com.dassault_systemes.enovia.characteristic.util.CharacteristicMasterConstants;
import com.dassault_systemes.enovia.criteria.interfaces.ENOCriteriaFactory;
import com.dassault_systemes.enovia.criteria.util.CriteriaUtil;
import com.dassault_systemes.enovia.enterprisechangemgt.common.ChangeConstants;
import com.dassault_systemes.enovia.enterprisechangemgt.util.ChangeUtil;
import com.dassault_systemes.parameter_interfaces.ParameterInterfacesServices;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MessageUtil;
import com.matrixone.apps.domain.util.PersonUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UICache;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.v3.custom.pgV3Constants;
import com.png.apollo.pgApolloCommonUtil;
import com.png.apollo.pgApolloConstants;

import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.db.RelationshipType;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class EvaluateCriteriaAndUpdateCharacteristics
{
	private static final org.apache.log4j.Logger loggerTrace = org.apache.log4j.Logger.getLogger(EvaluateCriteriaAndUpdateCharacteristics.class);

	public String processSelectedProductPartsForMassUpdate(Context context, String[] args) throws Exception
	{
		HashMap programMap = (HashMap) JPO.unpackArgs(args);		
		String sFunctionality = (String)programMap.get("Functionality");		
		String sReturn = DomainConstants.EMPTY_STRING;		
		if(pgApolloConstants.STR_MODE_MASSUPDATE_IGNORED.equalsIgnoreCase(sFunctionality))
		{
			sReturn = markSelectedProductsAsIgnored(context, programMap);
		}
		else if(pgApolloConstants.STR_MODE_MASSUPDATE_PENDING.equalsIgnoreCase(sFunctionality))
		{
			sReturn = markSelectedProductsAsPending(context, programMap);
		}
		else if(pgApolloConstants.STR_MODE_MASSUPDATE_UPDATE.equalsIgnoreCase(sFunctionality))
		{
			sReturn = UpdateCharacteristicsForSelectedProducts(context, programMap);
		}		
		return sReturn;
	}


	/**
	 * Method to mark selected products as Ignored
	 * @param context
	 * @param programMap
	 * @return
	 * @throws FrameworkException 
	 */
	private String markSelectedProductsAsIgnored(Context context, HashMap programMap) throws FrameworkException 
	{
		boolean isContextPushed = false;
		boolean bError = false;

		String sMessageMarkIgnoredFailed = EnoviaResourceBundle.getProperty(context, CharacteristicMasterConstants.CHARACTERISTIC_MASTER_STRING_RESOURCE, context.getLocale(),"CharacteristicMaster.MassUpdateCharacteristics.Message.MarkSelectedProductsAsIgnoredFailed");
		String sMessageMarkIgnoredSuccess = EnoviaResourceBundle.getProperty(context, CharacteristicMasterConstants.CHARACTERISTIC_MASTER_STRING_RESOURCE, context.getLocale(),"CharacteristicMaster.MassUpdateCharacteristics.Message.MarkSelectedProductsAsIgnoredSuccess");
		String sReturn = sMessageMarkIgnoredSuccess;

		try 
		{
			String sSelectedRowIds = (String)programMap.get("emxTableRowId");		
			StringList slSelectedRowIds = new StringList();

			if(UIUtil.isNotNullAndNotEmpty(sSelectedRowIds))
			{
				slSelectedRowIds = StringUtil.split(sSelectedRowIds, pgApolloConstants.CONSTANT_STRING_COMMA);
			}

			context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : markSelectedProductsAsIgnored slSelectedRowIds = "+slSelectedRowIds);

			StringList slValidSelectedRowIds = new StringList();

			if(!slSelectedRowIds.isEmpty())
			{
				StringList slSelectable = new StringList();
				slSelectable.add(DomainConstants.SELECT_ID);
				slSelectable.add(DomainConstants.SELECT_CURRENT);
				slSelectable.add(pgApolloConstants.SELECT_ATTRIBUTE_PGLPDPERFCHARACTERISTICSUPDATESTATUS);

				MapList mlSelectedObjectsInfo = DomainObject.getInfo(context,slSelectedRowIds.toArray(new String[slSelectedRowIds.size()]),slSelectable);

				context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : markSelectedProductsAsIgnored mlSelectedObjectsInfo = "+mlSelectedObjectsInfo);

				Map mapObject = null;
				String sObjectCurrent;
				String sObjectId;
				String sObjectUpdateStatus;

				for(Object objectMap : mlSelectedObjectsInfo)
				{
					mapObject = (Map)objectMap;

					sObjectId = (String)mapObject.get(DomainConstants.SELECT_ID);
					sObjectCurrent = (String)mapObject.get(DomainConstants.SELECT_CURRENT);
					sObjectUpdateStatus = (String)mapObject.get(pgApolloConstants.SELECT_ATTRIBUTE_PGLPDPERFCHARACTERISTICSUPDATESTATUS);

					if(pgApolloConstants.RANGE_PGLPDPERFCHARACTERISTICSUPDATESTATUS_PENDING.equals(sObjectUpdateStatus)  && (pgApolloConstants.STATE_RELEASE.equalsIgnoreCase(sObjectCurrent) || pgApolloConstants.STATE_RELEASED.equalsIgnoreCase(sObjectCurrent)))
					{
						slValidSelectedRowIds.add(sObjectId);
					}
					else
					{
						bError = true;
					}

				}			

			}

			//Push context is needed to update APPs which are released, for which Users will not have access  to it
			ContextUtil.pushContext(context, pgApolloConstants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
			isContextPushed = true;	

			if(!slValidSelectedRowIds.isEmpty())
			{
				Map mapAPPAttribute = new HashMap();
				mapAPPAttribute.put(pgApolloConstants.ATTRIBUTE_PGLPDPERFCHARACTERISTICSUPDATESTATUS, pgApolloConstants.RANGE_PGLPDPERFCHARACTERISTICSUPDATESTATUS_IGNORED);

				for(String sSelectedRowId : slValidSelectedRowIds)
				{
					if(UIUtil.isNotNullAndNotEmpty(sSelectedRowId))
					{
						DomainObject domSelectedPartObject = DomainObject.newInstance(context,sSelectedRowId);
						domSelectedPartObject.setAttributeValues(context, mapAPPAttribute);		

					}
				}		
			}	

		}
		catch (Exception ex)
		{
			loggerTrace.error(ex.getMessage() ,ex);	
			sReturn = sMessageMarkIgnoredFailed;
		}
		finally
		{
			if(isContextPushed)
			{
				ContextUtil.popContext(context);
			}
		}
		
		if(bError)
		{
			sReturn = sMessageMarkIgnoredFailed;
		}

		return sReturn;
	}


	/**
	 * Method to mark selected products as Pending
	 * @param context
	 * @param programMap
	 * @return
	 * @throws FrameworkException 
	 */
	private String markSelectedProductsAsPending(Context context, HashMap programMap) throws FrameworkException 
	{
		boolean isContextPushed = false;
		boolean bError = false;

		String sMessageMarkPendingFailed = EnoviaResourceBundle.getProperty(context, CharacteristicMasterConstants.CHARACTERISTIC_MASTER_STRING_RESOURCE, context.getLocale(),"CharacteristicMaster.MassUpdateCharacteristics.Message.MarkSelectedProductsAsPendingFailed");
		String sMessageMarkPendingSuccess = EnoviaResourceBundle.getProperty(context, CharacteristicMasterConstants.CHARACTERISTIC_MASTER_STRING_RESOURCE, context.getLocale(),"CharacteristicMaster.MassUpdateCharacteristics.Message.MarkSelectedProductsAsPendingSuccess");
		String sReturn = sMessageMarkPendingSuccess;

		try 
		{
			String sSelectedRowIds = (String)programMap.get("emxTableRowId");		
			StringList slSelectedRowIds = new StringList();

			if(UIUtil.isNotNullAndNotEmpty(sSelectedRowIds))
			{
				slSelectedRowIds = StringUtil.split(sSelectedRowIds, pgApolloConstants.CONSTANT_STRING_COMMA);
			}

			StringList slValidSelectedRowIds = new StringList();
			StringList slRelsToBeDisconnected = new StringList();

			context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : markSelectedProductsAsPending slSelectedRowIds = "+slSelectedRowIds);

			if(!slSelectedRowIds.isEmpty())
			{
				StringList slSelectable = new StringList();
				slSelectable.add(DomainConstants.SELECT_ID);
				slSelectable.add(pgApolloConstants.SELECT_ATTRIBUTE_PGLPDPERFCHARACTERISTICSUPDATESTATUS);
				slSelectable.add("to["+pgApolloConstants.RELATIONSHIP_PGBACKGROUNDPROCESSRELATEDOBJECTS+"]."+DomainConstants.SELECT_ID);
				slSelectable.add("to["+pgApolloConstants.RELATIONSHIP_PGBACKGROUNDPROCESSRELATEDOBJECTS+"].from."+DomainConstants.SELECT_CURRENT);
				slSelectable.add(pgApolloConstants.SELECT_NEXT_ID);

				MapList mlSelectedObjectsInfo = DomainObject.getInfo(context,slSelectedRowIds.toArray(new String[slSelectedRowIds.size()]),slSelectable);

				context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : markSelectedProductsAsPending mlSelectedObjectsInfo = "+mlSelectedObjectsInfo);

				Map mapObject = null;
				String sObjectUpdateStatus;
				String sObjectId;
				String sObjectNextId;
				StringList slBackGroundObjectCurrent;
				StringList slBackGroundObjectRelId;
				String sBGCurrent;
				String sBGRelId;

				for(Object objectMap : mlSelectedObjectsInfo)
				{
					mapObject = (Map)objectMap;

					sObjectId = (String)mapObject.get(DomainConstants.SELECT_ID);
					sObjectUpdateStatus = (String)mapObject.get(pgApolloConstants.SELECT_ATTRIBUTE_PGLPDPERFCHARACTERISTICSUPDATESTATUS);
					if(pgApolloConstants.RANGE_PGLPDPERFCHARACTERISTICSUPDATESTATUS_INPROGRESS.equals(sObjectUpdateStatus))
					{
						slBackGroundObjectCurrent = pgApolloCommonUtil.getStringListMultiValue(mapObject.get("to["+pgApolloConstants.RELATIONSHIP_PGBACKGROUNDPROCESSRELATEDOBJECTS+"].from."+DomainConstants.SELECT_CURRENT));
						slBackGroundObjectRelId = pgApolloCommonUtil.getStringListMultiValue(mapObject.get("to["+pgApolloConstants.RELATIONSHIP_PGBACKGROUNDPROCESSRELATEDOBJECTS+"]."+DomainConstants.SELECT_ID));

						if(!slBackGroundObjectCurrent.isEmpty() && !slBackGroundObjectCurrent.contains(pgApolloConstants.STATE_IN_PROGRESS))
						{
							for(int i=0; i<slBackGroundObjectCurrent.size();i++)
							{
								sBGCurrent = slBackGroundObjectCurrent.get(i);
								if(pgApolloConstants.STATE_ACTIVE.equalsIgnoreCase(sBGCurrent))
								{
									sBGRelId = slBackGroundObjectRelId.get(i);
									slRelsToBeDisconnected.add(sBGRelId);
								}
							}	

							slValidSelectedRowIds.add(sObjectId);
						}
						else
						{
							bError = true;							
						}
					}

					if(pgApolloConstants.RANGE_PGLPDPERFCHARACTERISTICSUPDATESTATUS_IGNORED.equals(sObjectUpdateStatus))
					{
						sObjectNextId = (String)mapObject.get(pgApolloConstants.SELECT_NEXT_ID);
						if(UIUtil.isNullOrEmpty(sObjectNextId))
						{
							slValidSelectedRowIds.add(sObjectId);
						}
						else
						{
							bError = true;
						}
					}				

				}			
				
				if(slValidSelectedRowIds.isEmpty())
				{
					bError = true;
				}
			}

			context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : markSelectedProductsAsPending slValidSelectedRowIds = "+slValidSelectedRowIds);

			//Push context is needed to update APPs which are released, for which Users will not have access  to it
			ContextUtil.pushContext(context, pgApolloConstants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
			isContextPushed = true;	

			if(!slValidSelectedRowIds.isEmpty())
			{
				if(!slRelsToBeDisconnected.isEmpty())
				{
					DomainRelationship.disconnect(context, slRelsToBeDisconnected.toArray(new String[slRelsToBeDisconnected.size()]));
				}
				
				Map mapAPPAttribute = new HashMap();
				mapAPPAttribute.put(pgApolloConstants.ATTRIBUTE_PGLPDPERFCHARACTERISTICSUPDATESTATUS, pgApolloConstants.RANGE_PGLPDPERFCHARACTERISTICSUPDATESTATUS_PENDING);

				for(String sSelectedRowId : slValidSelectedRowIds)
				{
					if(UIUtil.isNotNullAndNotEmpty(sSelectedRowId))
					{
						DomainObject domSelectedPartObject = DomainObject.newInstance(context,sSelectedRowId);
						domSelectedPartObject.setAttributeValues(context, mapAPPAttribute);	
					}
				}				
			}	

		}
		catch (Exception ex)
		{
			loggerTrace.error(ex.getMessage() ,ex);			
			sReturn = sMessageMarkPendingFailed;
		}	
		finally
		{
			if(isContextPushed)
			{
				ContextUtil.popContext(context);
			}
		}
		
		if(bError)
		{
			sReturn = sMessageMarkPendingFailed;
		}
		
		return sReturn;
	}


	/**
	 * Method to Update Characteristics for selected products
	 * @param context
	 * @param programMap
	 * @return
	 * @throws MatrixException 
	 */
	private String UpdateCharacteristicsForSelectedProducts(Context context, HashMap programMap) throws MatrixException 
	{
		String sReturn = DomainConstants.EMPTY_STRING;
		boolean isContextPushed = false;
		boolean bError = false;

		String sMessageProductPartsSubmittedForMassUpdate = EnoviaResourceBundle.getProperty(context, CharacteristicMasterConstants.CHARACTERISTIC_MASTER_STRING_RESOURCE, context.getLocale(),"CharacteristicMaster.MassUpdateCharacteristics.Message.SelectedProductsSubmittedForMassUpdate");
		String sMessageMarkForMassUpdateFailed = EnoviaResourceBundle.getProperty(context, CharacteristicMasterConstants.CHARACTERISTIC_MASTER_STRING_RESOURCE, context.getLocale(),"CharacteristicMaster.MassUpdateCharacteristics.Message.MarkSelectedProductsForUpdateFailed");

		StringList slValidSelectedRowIds  =  new StringList();

		try 
		{
			String sSelectedRowIds = (String)programMap.get("emxTableRowId");		
			StringList slSelectedRowIds = new StringList();
			String sContextUser = context.getUser();

			if(UIUtil.isNotNullAndNotEmpty(sSelectedRowIds))
			{
				slSelectedRowIds = StringUtil.split(sSelectedRowIds, pgApolloConstants.CONSTANT_STRING_COMMA);
			}
			
			context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : UpdateCharacteristicsForSelectedProducts slSelectedRowIds = "+slSelectedRowIds);

			String sCOId;
			if(!slSelectedRowIds.isEmpty())
			{

				StringList slSelectable = new StringList();
				slSelectable.add(DomainConstants.SELECT_ID);
				slSelectable.add(DomainConstants.SELECT_HAS_MODIFY_ACCESS);
				slSelectable.add(DomainConstants.SELECT_CURRENT);
				slSelectable.add(pgApolloConstants.SELECT_NEXT_ID);
				slSelectable.add(pgApolloConstants.SELECT_ATTRIBUTE_PGLPDPERFCHARACTERISTICSUPDATESTATUS);
				slSelectable.add(pgApolloConstants.SELECT_ASSOCIATEDPHYSICALPRODUCT_ISVPLMCONTROLLED);

				MapList mlSelectedObjectsInfo = DomainObject.getInfo(context,slSelectedRowIds.toArray(new String[slSelectedRowIds.size()]),slSelectable);

				context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : UpdateCharacteristicsForSelectedProducts mlSelectedObjectsInfo = "+mlSelectedObjectsInfo);

				Map mapObject = null;
				String sObjectCurrent;
				String sObjectId;
				String sObjectUpdateStatus;
				String sObjectNextId;
				String sObjectModifyAccess;
				String sIsVPMControlled;

				for(Object objectMap : mlSelectedObjectsInfo)
				{
					mapObject = (Map)objectMap;

					sObjectId = (String)mapObject.get(DomainConstants.SELECT_ID);					
					sObjectCurrent = (String)mapObject.get(DomainConstants.SELECT_CURRENT);
					sObjectModifyAccess = (String)mapObject.get(DomainConstants.SELECT_HAS_MODIFY_ACCESS);
					sObjectUpdateStatus = (String)mapObject.get(pgApolloConstants.SELECT_ATTRIBUTE_PGLPDPERFCHARACTERISTICSUPDATESTATUS);
					sObjectNextId = (String)mapObject.get(pgApolloConstants.SELECT_NEXT_ID);
					sIsVPMControlled = (String)mapObject.get(pgApolloConstants.SELECT_ASSOCIATEDPHYSICALPRODUCT_ISVPLMCONTROLLED);									
					
					//Error message if any of the following condition is true
					//If next rev is not empty
					//If it is not preliminary state and not released
					//if it is preliminary state and (control is in catia || no modify access) 
					//if status is not pending
					if (UIUtil.isNotNullAndNotEmpty(sObjectNextId) || 
							!pgApolloConstants.RANGE_PGLPDPERFCHARACTERISTICSUPDATESTATUS_PENDING.equals(sObjectUpdateStatus) || 
							(!DomainConstants.STATE_PART_PRELIMINARY.equalsIgnoreCase(sObjectCurrent) && !(pgApolloConstants.STATE_RELEASE.equalsIgnoreCase(sObjectCurrent) || pgApolloConstants.STATE_RELEASED.equalsIgnoreCase(sObjectCurrent))) ||
							(DomainConstants.STATE_PART_PRELIMINARY.equalsIgnoreCase(sObjectCurrent) && (!pgApolloConstants.STR_TRUE_FLAG.equalsIgnoreCase(sObjectModifyAccess) || pgApolloConstants.STR_TRUE_FLAG.equalsIgnoreCase(sIsVPMControlled))))
					{
						bError = true;	
					}
					else
					{
						slValidSelectedRowIds.add(sObjectId);
					}

				}		

				context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : UpdateCharacteristicsForSelectedProducts slValidSelectedRowIds = "+slValidSelectedRowIds);


				if(!slValidSelectedRowIds.isEmpty())				
				{

					String sReasonForChange = (String)programMap.get("ReasonForChange");		
					String sCreateNewChangeFlag = (String)programMap.get("chkCreateNew");

					if(pgApolloConstants.STR_TRUE_FLAG.equalsIgnoreCase(sCreateNewChangeFlag))
					{
						sCOId = pgApolloConstants.CONST_KEY_NEW;
					}
					else
					{
						sCOId = (String)programMap.get("AddCOOID");	
					}

					if(UIUtil.isNullOrEmpty(sCOId))
					{
						sCOId = DomainConstants.EMPTY_STRING;
					}

					String sType = pgApolloConstants.TYPE_PGBACKGROUDPROCESS;
					String sObjGeneratorName = UICache.getObjectGenerator(context, "type_pgBackgroundProcess", DomainConstants.EMPTY_STRING);
					String sAutoGeneratedName = DomainObject.getAutoGeneratedName(context, sObjGeneratorName, DomainConstants.EMPTY_STRING);
					DomainObject domObject = DomainObject.newInstance(context);

					Map mapAttribute = new HashMap();
					mapAttribute.put(DomainConstants.ATTRIBUTE_TITLE, pgApolloConstants.STR_TITLE_MASSUPDATECHARACTERISTICS);
					mapAttribute.put(pgApolloConstants.ATTRIBUTE_PGPARAMETERARGUMENT1, sCOId);
					mapAttribute.put(pgApolloConstants.ATTRIBUTE_REASON_FOR_CHANGE, sReasonForChange);
					mapAttribute.put(pgApolloConstants.ATTRIBUTE_PROGRAMNAME, "com.png.apollo.sync.ebom.EvaluateCriteriaAndUpdateCharacteristics");
					mapAttribute.put(pgApolloConstants.ATTRIBUTE_METHODNAME, "initiateProcessingOfAssociatedParts");

					domObject.createObject(context, sType, sAutoGeneratedName, DomainConstants.EMPTY_STRING, pgApolloConstants.POLICY_PGBACKGROUNDPROCESSPOLICY, context.getVault().getName());
					domObject.setAttributeValues(context, mapAttribute);
					domObject.setOwner(context, sContextUser);
					domObject.setDescription(context, sReasonForChange);

					//Push context is needed to update APPs which are released, for which Users will not have access  to it
					ContextUtil.pushContext(context, pgApolloConstants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
					isContextPushed = true;	

					DomainRelationship.connect(context, domObject, pgApolloConstants.RELATIONSHIP_PGBACKGROUNDPROCESSRELATEDOBJECTS, true, slValidSelectedRowIds.toArray(new String[slValidSelectedRowIds.size()]));

					Map mapAPPAttribute = new HashMap();
					mapAPPAttribute.put(pgApolloConstants.ATTRIBUTE_PGLPDPERFCHARACTERISTICSUPDATESTATUS, pgApolloConstants.RANGE_PGLPDPERFCHARACTERISTICSUPDATESTATUS_INPROGRESS);

					for(String sSelectedRowId : slValidSelectedRowIds)
					{
						if(UIUtil.isNotNullAndNotEmpty(sSelectedRowId))
						{
							DomainObject domSelectedPartObject = DomainObject.newInstance(context,sSelectedRowId);
							domSelectedPartObject.setAttributeValues(context, mapAPPAttribute);

						}
					}				
					sReturn = sMessageProductPartsSubmittedForMassUpdate;

				}
				else
				{
					bError = true;
				}
			}
		} 
		catch (Exception ex)
		{
			loggerTrace.error(ex.getMessage() ,ex);	
			sReturn = sMessageMarkForMassUpdateFailed;

		}
		finally
		{
			if(isContextPushed)
			{
				ContextUtil.popContext(context);
			}
		}
		
		if(bError)
		{
			sReturn = sMessageMarkForMassUpdateFailed;
		}
		
		return sReturn;
	}
	
	
	/**
	 * Method to Validate for selected products for Mass Update
	 * @param context
	 * @param programMap
	 * @return
	 * @throws MatrixException 
	 */
	public String validateSelectedProductPartsForMassUpdate(Context context, String[] args) throws MatrixException 
	{
		String sReturn = DomainConstants.EMPTY_STRING;
		boolean bError = false;
		String sMessageMarkForMassUpdateFailed = EnoviaResourceBundle.getProperty(context, CharacteristicMasterConstants.CHARACTERISTIC_MASTER_STRING_RESOURCE, context.getLocale(),"CharacteristicMaster.MassUpdateCharacteristics.Message.MarkSelectedProductsForUpdateFailed");
		try 
		{
			HashMap programMap = (HashMap) JPO.unpackArgs(args);
			String sSelectedRowIds = (String)programMap.get("emxTableRowId");		
			StringList slSelectedRowIds = new StringList();
			if(UIUtil.isNotNullAndNotEmpty(sSelectedRowIds))
			{
				slSelectedRowIds = StringUtil.split(sSelectedRowIds, pgApolloConstants.CONSTANT_STRING_COMMA);
			}
			context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : validateSelectedProductPartsForMassUpdate slSelectedRowIds = "+slSelectedRowIds);
			if(!slSelectedRowIds.isEmpty())
			{
				StringList slSelectable = new StringList();
				slSelectable.add(DomainConstants.SELECT_ID);
				slSelectable.add(DomainConstants.SELECT_HAS_MODIFY_ACCESS);
				slSelectable.add(DomainConstants.SELECT_CURRENT);
				slSelectable.add(pgApolloConstants.SELECT_NEXT_ID);
				slSelectable.add(pgApolloConstants.SELECT_ATTRIBUTE_PGLPDPERFCHARACTERISTICSUPDATESTATUS);
				slSelectable.add(pgApolloConstants.SELECT_ASSOCIATEDPHYSICALPRODUCT_ISVPLMCONTROLLED);

				MapList mlSelectedObjectsInfo = DomainObject.getInfo(context,slSelectedRowIds.toArray(new String[slSelectedRowIds.size()]),slSelectable);

				context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : validateSelectedProductPartsForMassUpdate mlSelectedObjectsInfo = "+mlSelectedObjectsInfo);

				Map mapObject = null;
				String sObjectCurrent;
				String sObjectUpdateStatus;
				String sObjectNextId;
				String sObjectModifyAccess;
				String sIsVPMControlled;

				for(Object objectMap : mlSelectedObjectsInfo)
				{
					mapObject = (Map)objectMap;
					sObjectCurrent = (String)mapObject.get(DomainConstants.SELECT_CURRENT);
					sObjectModifyAccess = (String)mapObject.get(DomainConstants.SELECT_HAS_MODIFY_ACCESS);
					sObjectUpdateStatus = (String)mapObject.get(pgApolloConstants.SELECT_ATTRIBUTE_PGLPDPERFCHARACTERISTICSUPDATESTATUS);
					sObjectNextId = (String)mapObject.get(pgApolloConstants.SELECT_NEXT_ID);
					sIsVPMControlled = (String)mapObject.get(pgApolloConstants.SELECT_ASSOCIATEDPHYSICALPRODUCT_ISVPLMCONTROLLED);
					
					//Error message if any of the following condition is true
						//If next rev is not empty
						//If it is not preliminary state and not released
						//if it is preliminary state and (control is in catia || no modify access) 
						//if status is not pending
						if (UIUtil.isNotNullAndNotEmpty(sObjectNextId) || 
								!pgApolloConstants.RANGE_PGLPDPERFCHARACTERISTICSUPDATESTATUS_PENDING.equals(sObjectUpdateStatus) || 
								(!DomainConstants.STATE_PART_PRELIMINARY.equalsIgnoreCase(sObjectCurrent) && !(pgApolloConstants.STATE_RELEASE.equalsIgnoreCase(sObjectCurrent) || pgApolloConstants.STATE_RELEASED.equalsIgnoreCase(sObjectCurrent))) ||
								(DomainConstants.STATE_PART_PRELIMINARY.equalsIgnoreCase(sObjectCurrent) && (!pgApolloConstants.STR_TRUE_FLAG.equalsIgnoreCase(sObjectModifyAccess) || pgApolloConstants.STR_TRUE_FLAG.equalsIgnoreCase(sIsVPMControlled))))
						{
							bError = true;	
						}
				}
				context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : validateSelectedProductPartsForMassUpdate bError = "+bError);
			}
		}
		catch (Exception ex)
		{
			loggerTrace.error(ex.getMessage() ,ex);	
			sReturn = sMessageMarkForMassUpdateFailed;
		}
		if(bError)
		{
			sReturn = sMessageMarkForMassUpdateFailed;
		}
		return sReturn;
	}
	
	/**
	 * This Method will be called from cron job, will have super user context
	 * Method to initiate processing of associated parts
	 * @param context
	 * @param domBackgroundProcess
	 * @param mapBackgroundProcess
	 * @param sChangeTemplateId
	 * @return
	 * @throws Exception
	 */
	public Map initiateProcessingOfAssociatedParts(Context context, String[] args) throws Exception 
	{
		String sBackgroundProcessObjectId = args[0];

		Map mapResponse = new HashMap();
		String sResponse = pgApolloConstants.STR_SUCCESS;		
		boolean isContextPushed = false;
		
		context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics. : initiateProcessingOfAssociatedParts >> sBackgroundProcessObjectId = "+sBackgroundProcessObjectId);

		try 
		{
			if(UIUtil.isNotNullAndNotEmpty(sBackgroundProcessObjectId))
			{
				StringList slCTobjectSelects = new StringList(1);
				slCTobjectSelects.add(DomainConstants.SELECT_ID);					 

				String strDefaultChangeTemplateName = EnoviaResourceBundle.getProperty(context, "emxCPN.ProductDataStructureCopy.DefaultChangeTemplateName");
				String strDefaultChangeTemplateRev = EnoviaResourceBundle.getProperty(context, "emxCPN.ProductDataStructureCopy.DefaultChangeTemplateRev");
				
				MapList mlChangeTemplate = DomainObject.findObjects(context,
														ChangeConstants.TYPE_CHANGETEMPLATE, 
														strDefaultChangeTemplateName, 
														strDefaultChangeTemplateRev, 
														null,
														pgApolloConstants.VAULT_ESERVICE_PRODUCTION,
														null,
														true, 
														slCTobjectSelects);
				
				Map mpChangeTemplate = (Map)mlChangeTemplate.get(0);
				String sChangeTemplateId=(String)mpChangeTemplate.get(DomainConstants.SELECT_ID);			
				
				context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics. : initiateProcessingOfAssociatedParts  sChangeTemplateId = "+sChangeTemplateId);

				
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
				slObjectSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_REASON_FOR_CHANGE);
				slObjectSelects.add(sSelectBackgroundProcessRelatedObjects);
				
				StringList slMultiValueObjectSelects = new StringList();
				slMultiValueObjectSelects.add(sSelectBackgroundProcessRelatedObjects);
				
				DomainObject domBackgroundProcess = DomainObject.newInstance(context, sBackgroundProcessObjectId);		
				
				Map mapBackgroundProcess = domBackgroundProcess.getInfo(context, slObjectSelects, slMultiValueObjectSelects);
				
				context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics. : initiateProcessingOfAssociatedParts  mapBackgroundProcess = "+mapBackgroundProcess);

				
				String sBackgroundProcessOwner = (String)mapBackgroundProcess.get(DomainConstants.SELECT_OWNER);
					
				StringList slAssociatedAPPIds = pgApolloCommonUtil.getStringListMultiValue(mapBackgroundProcess.get(sSelectBackgroundProcessRelatedObjects));

				if(null != slAssociatedAPPIds && !slAssociatedAPPIds.isEmpty())
				{
					//User specific context is required to process the objects. - as method execution will be by Cron Job - Super User
					ContextUtil.pushContext(context,sBackgroundProcessOwner,DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
					isContextPushed = true;

					String sPersonCTX = PersonUtil.getDefaultSecurityContext(context, context.getUser());
					sContextRole = sbContext.append("ctx::").append(sPersonCTX).toString();
					context.resetRole(sContextRole);
					
					Map mapParamArgs = new HashMap();
					mapParamArgs.put("TargetParts", slAssociatedAPPIds);

					StringList slSelectables = new StringList();
					slSelectables.add(DomainConstants.SELECT_ID);
					slSelectables.add(DomainConstants.SELECT_NAME);
					slSelectables.add(DomainConstants.SELECT_REVISION);
					slSelectables.add(pgApolloConstants.SELECT_NEXT_ID);
					slSelectables.add(DomainConstants.SELECT_CURRENT);
					slSelectables.add(DomainConstants.SELECT_TYPE);
					slSelectables.add(pgApolloConstants.SELECT_ATTRIBUTE_RELEASE_PHASE);
					slSelectables.add(pgApolloConstants.SELECT_ASSOCIATEDPHYSICALPRODUCT_ISVPLMCONTROLLED);

					MapList mlPartInfo = DomainObject.getInfo(context, slAssociatedAPPIds.toArray(new String[slAssociatedAPPIds.size()]), slSelectables);	

					context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics. : initiateProcessingOfAssociatedParts  mlPartInfo = "+mlPartInfo);

					mapParamArgs.put("PartInfoList", mlPartInfo);
					mapParamArgs.put("mode", pgApolloConstants.STR_MODE_MASSUPDATE_UPDATE);

					sResponse = massUpdateAPPsForCharacteristics(context, mapParamArgs, mapBackgroundProcess, sChangeTemplateId);
					
					context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics. : initiateProcessingOfAssociatedParts  sResponse = "+sResponse);

				}
				
				mapResponse.put(pgApolloConstants.KEY_MESSAGE, pgApolloConstants.STR_SUCCESS);
			}
		} 
		catch (Exception e) 
		{
			loggerTrace.error(e.getMessage(), e);
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
		context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics. : initiateProcessingOfAssociatedParts << mapResponse = "+mapResponse);

		return mapResponse;
	}


	/**
	 * Method to Mass Update Characteristics
	 * @param context
	 * @param mapParamArgs
	 * @param mapBackgroundProcess
	 * @param sChangeTemplateId
	 * @return
	 * @throws Exception
	 */
	public String massUpdateAPPsForCharacteristics (matrix.db.Context context, Map mapParamArgs, Map mapBackgroundProcess, String sChangeTemplateId) throws Exception 
	{
		String sResponse = pgApolloConstants.STR_SUCCESS;
		context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : massUpdateAPPsForCriteria >> args = "+mapParamArgs);
		context.printTrace(pgApolloConstants.TRACE_LPD, "mapBackgroundProcess = "+mapBackgroundProcess);
		context.printTrace(pgApolloConstants.TRACE_LPD, "sChangeTemplateId = "+sChangeTemplateId);

		String sBackgroundObjectId = (String)mapBackgroundProcess.get(DomainConstants.SELECT_ID);		
		DomainObject domBackgroundProcess = DomainObject.newInstance(context, sBackgroundObjectId);
		Map mapBackgroundProcessAttribute = new HashMap();
		StringList slBackgroundProcessErrorList = new StringList();
		String sError;
		
		boolean bBackgroundJobProcessError = false;

		try 
		{
			String sCOId = (String)mapBackgroundProcess.get(pgApolloConstants.SELECT_ATTRIBUTE_PGPARAMETERARGUMENT1);
			boolean bCreateChange = false;
			if(UIUtil.isNotNullAndNotEmpty(sCOId))
			{
				bCreateChange = true;
				if(pgApolloConstants.CONST_KEY_NEW.equals(sCOId))
				{
					sCOId = DomainConstants.EMPTY_STRING;
				}
			}						
			MapList mlPartInfo = (MapList)mapParamArgs.get("PartInfoList");
			String sMode = (String)mapParamArgs.get("mode");


			Map mapPart;
			String sObjectId;
			String sObjectName;
			String sObjectRevision;
			StringBuilder sbObjectDetails = new StringBuilder();
			StringBuilder sbErrorDetails = new StringBuilder();

			String sCurrent;	
			String sReleasePhase;

			GenerateEBOMService ebomservice = new GenerateEBOMService();		
			StringList slCAPartList = new StringList();

			Map mapPartOutput;
			MapList mlPartOutput = new MapList();
			String sUpdatedObjectId;
			String sPartError;
			StringList slUpdateErrorList;

			if(!mlPartInfo.isEmpty())
			{
				for(Object object : mlPartInfo)
				{
					mapPartOutput = new HashMap();
					sPartError = DomainConstants.EMPTY_STRING;
					sUpdatedObjectId = DomainConstants.EMPTY_STRING;
					sObjectId = DomainConstants.EMPTY_STRING;

					mapPart = (Map)object;				
					sObjectId = (String)mapPart.get(DomainConstants.SELECT_ID);	

					mapPartOutput = validatePartPriorProcessing(context, sObjectId, domBackgroundProcess);

					try
					{
						if(mapPartOutput.isEmpty())
						{
							sObjectName = (String)mapPart.get(DomainConstants.SELECT_NAME);
							sObjectRevision = (String)mapPart.get(DomainConstants.SELECT_REVISION);
							sCurrent = (String)mapPart.get(DomainConstants.SELECT_CURRENT);					

							sbObjectDetails = new StringBuilder();
							sbObjectDetails.append(sObjectName).append(pgApolloConstants.CONSTANT_STRING_DOT).append(sObjectRevision);

							context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : massUpdateAPPsForCharacteristics - sObjectId = "+sObjectId);

							if(UIUtil.isNotNullAndNotEmpty(sObjectId))
							{
								if(DomainConstants.STATE_PART_RELEASE.equals(sCurrent))
								{
									sReleasePhase = (String)mapPart.get(pgApolloConstants.SELECT_ATTRIBUTE_RELEASE_PHASE);	
									mapPartOutput = updateReleasedPart(context, mapPart, mapBackgroundProcess, sMode, sObjectId, ebomservice);
									sUpdatedObjectId = (String)mapPartOutput.get(DomainConstants.SELECT_ID);
									context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : massUpdateAPPsForCharacteristics Revise - mapPartOutput = "+mapPartOutput+" sReleasePhase = "+sReleasePhase);

									if(UIUtil.isNotNullAndNotEmpty(sUpdatedObjectId) && !sUpdatedObjectId.equals(sObjectId) && (pgApolloConstants.STR_PRODUCION_PHASE.equals(sReleasePhase) || pgApolloConstants.STR_PILOT_PHASE.equals(sReleasePhase)))
									{
										slCAPartList.add(sUpdatedObjectId);
									}							
								}
								else if(DomainConstants.STATE_PART_PRELIMINARY.equals(sCurrent))
								{
									mapPartOutput = updateInWorkPart(context, mapPart, sMode, sObjectId, ebomservice);

									context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : massUpdateAPPsForCharacteristics In Work - mapPartOutput = "+mapPartOutput);

								}

							}	
						}
						else
						{
							context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : massUpdateAPPsForCharacteristics Processing skipped for Object = "+sObjectId+"  mapPartOutput = "+mapPartOutput);
						}

					}
					catch(Exception exp)
					{
						loggerTrace.error(exp.getMessage(), exp);
						sbErrorDetails = new StringBuilder();
						sbErrorDetails.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(sbObjectDetails).append(pgApolloConstants.CONSTANT_STRING_COLON).append(exp.getLocalizedMessage());
						sPartError = sbErrorDetails.toString();
						slBackgroundProcessErrorList.add(sPartError);
						bBackgroundJobProcessError = true;
						
						
						slUpdateErrorList = (StringList)mapPartOutput.get(pgApolloConstants.STR_ERROR_LIST);
						if(UIUtil.isNotNullAndNotEmpty(sPartError))
						{
							if(null ==slUpdateErrorList)
							{
								slUpdateErrorList = new StringList();
							}
							slUpdateErrorList.add(sPartError);
							mapPartOutput.put(pgApolloConstants.STR_ERROR_LIST, slUpdateErrorList);
							mapPartOutput.put(pgApolloConstants.STR_ERROR, true);
						}
					}
					finally
					{
						if(!mapPartOutput.isEmpty())
						{
							sUpdatedObjectId = (String)mapPartOutput.get(DomainConstants.SELECT_ID);
							
							if(UIUtil.isNotNullAndNotEmpty(sUpdatedObjectId))
							{
								sError = postUpdateProcessing(context, domBackgroundProcess, mapPartOutput, sObjectId, sUpdatedObjectId);

								if(UIUtil.isNotNullAndNotEmpty(sError))
								{
									sbErrorDetails = new StringBuilder();
									sbErrorDetails.append(sbObjectDetails).append(pgApolloConstants.CONSTANT_STRING_COLON).append(sError);
									slBackgroundProcessErrorList.add(sbErrorDetails.toString());
									bBackgroundJobProcessError = true;
								}
							}
						}		
						
					}					
					mlPartOutput.add(mapPartOutput);
				}		

				String sCAError = DomainConstants.EMPTY_STRING;
				
				if(bCreateChange)
				{
					String sCOObjectId = DomainConstants.EMPTY_STRING;
					sCOObjectId = pgApolloCommonUtil.createChangeObjects(context, slCAPartList, sCOId, sChangeTemplateId);
					if(UIUtil.isNotNullAndNotEmpty(sCOObjectId) && sCOObjectId.contains(pgApolloConstants.STR_ERROR))
					{
						sCAError = sCOObjectId;
						slBackgroundProcessErrorList.add(sCAError);
						bBackgroundJobProcessError = true;
					}

					context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : massUpdateAPPsForCharacteristics In Work - slCAPartList = "+slCAPartList);
					context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : massUpdateAPPsForCharacteristics In Work - sCAError = "+sCAError+" sCOObjectId = "+sCOObjectId+" sChangeTemplateId = "+sChangeTemplateId);

				}
				
				sError = generateOutputSummaryAndSendNotification(context, mlPartOutput, mapBackgroundProcess, slCAPartList, sCAError, bBackgroundJobProcessError);	

				if(UIUtil.isNotNullAndNotEmpty(sError))
				{
					slBackgroundProcessErrorList.add(sError);
				}

			}
		}
		catch (Exception e) 
		{
			loggerTrace.error(e.getMessage(), e);
			sResponse = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getMessage()).toString();	
			sError = new StringBuilder(pgApolloConstants.STR_TITLE_MASSUPDATECHARACTERISTICS).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getMessage()).toString();								
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
				loggerTrace.error(e.getMessage(), e);
				sResponse = new StringBuilder("Updating Background Job Error Attributes").append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getMessage()).toString();								
			}			
		}
		context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : massUpdateAPPsForCriteria << slBackgroundProcessErrorList = "+slBackgroundProcessErrorList);		
		context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : massUpdateAPPsForCriteria << Response = "+sResponse);		
		return sResponse;
	}


	/**
	 * Method to validate Part prior to processing
	 * @param context
	 * @param sObjectId
	 * @param domBackgroundProcess 
	 * @return
	 * @throws MatrixException 
	 */
	public Map validatePartPriorProcessing(Context context, String sObjectId, DomainObject domBackgroundProcess) throws MatrixException
	{
		context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : validatePartPriorProcessing >> sObjectId = "+sObjectId);		

		Map mapPartOutput = new HashMap();
		
		String sAssociatedBackgroundJobIdSelect = new StringBuilder("to[").append(pgApolloConstants.RELATIONSHIP_PGBACKGROUNDPROCESSRELATEDOBJECTS).append("].from.").append(DomainConstants.SELECT_ID).toString();
		
		StringList slSelectable = new StringList();
		slSelectable.add(DomainConstants.SELECT_ID);
		slSelectable.add(DomainConstants.SELECT_CURRENT);
		slSelectable.add(DomainConstants.SELECT_HAS_MODIFY_ACCESS);
		slSelectable.add(pgApolloConstants.SELECT_NEXT_ID);
		slSelectable.add(pgApolloConstants.SELECT_ASSOCIATEDPHYSICALPRODUCT_ISVPLMCONTROLLED);
		slSelectable.add(sAssociatedBackgroundJobIdSelect);
		
		StringList slError = new StringList();
		
		if(UIUtil.isNotNullAndNotEmpty(sObjectId))
		{
			DomainObject domainObject = DomainObject.newInstance(context, sObjectId);			
			Map mapObject = domainObject.getInfo(context, slSelectable);	
			
			context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : validatePartPriorProcessing  mapObject = "+mapObject);
			
			String sPartId = (String)mapObject.get(DomainConstants.SELECT_ID);					
			String sObjectCurrent = (String)mapObject.get(DomainConstants.SELECT_CURRENT);
			String sObjectModifyAccess = (String)mapObject.get(DomainConstants.SELECT_HAS_MODIFY_ACCESS);
			String sObjectNextId = (String)mapObject.get(pgApolloConstants.SELECT_NEXT_ID);
			String sIsVPMControlled = (String)mapObject.get(pgApolloConstants.SELECT_ASSOCIATEDPHYSICALPRODUCT_ISVPLMCONTROLLED);			
			StringList slPartBackgroundJobId = pgApolloCommonUtil.getStringListMultiValue(mapObject.get(sAssociatedBackgroundJobIdSelect));						

			//Error message if any of the following condition is true
			//If there is not BGP object associated
			//If next rev is not empty
			//If it is not preliminary state and not released
			//if it is preliminary state and (control is in catia || no modify access) 
			if (slPartBackgroundJobId.isEmpty() || UIUtil.isNotNullAndNotEmpty(sObjectNextId)  || 
					(!DomainConstants.STATE_PART_PRELIMINARY.equalsIgnoreCase(sObjectCurrent) && !(pgApolloConstants.STATE_RELEASE.equalsIgnoreCase(sObjectCurrent) || pgApolloConstants.STATE_RELEASED.equalsIgnoreCase(sObjectCurrent))) ||
					(DomainConstants.STATE_PART_PRELIMINARY.equalsIgnoreCase(sObjectCurrent) && (!pgApolloConstants.STR_TRUE_FLAG.equalsIgnoreCase(sObjectModifyAccess) || pgApolloConstants.STR_TRUE_FLAG.equalsIgnoreCase(sIsVPMControlled))))
			{
				String sMessageValidationFailed = EnoviaResourceBundle.getProperty(context, CharacteristicMasterConstants.CHARACTERISTIC_MASTER_STRING_RESOURCE, context.getLocale(),"CharacteristicMaster.MassUpdateCharacteristics.Message.ValidationError");
				slError.add(sMessageValidationFailed);			

				mapPartOutput.put(pgApolloConstants.STR_ERROR_LIST, slError);	
				mapPartOutput.put(pgApolloConstants.STR_ERROR, true);
				mapPartOutput.put(DomainConstants.SELECT_ID, sPartId);
			}		

		}	
		
		context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : validatePartPriorProcessing << mapPartOutput = "+mapPartOutput);		

		return mapPartOutput;
	}


	/**
	 * Method to update updated revision and disconnect background process object
	 * @param context
	 * @param domBackgroundProcess
	 * @param mapPartOutput
	 * @param sObjectId
	 * @param sUpdatedObjectId
	 * @return
	 * @throws MatrixException
	 */
	public String postUpdateProcessing(matrix.db.Context context, DomainObject domBackgroundProcess, Map mapPartOutput,  String sObjectId,  String sUpdatedObjectId) throws MatrixException 
	{
		
		context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : postUpdateProcessing >> mapPartOutput = "+mapPartOutput+" sObjectId = "+sObjectId+" sUpdatedObjectId = "+sUpdatedObjectId);

		String sError = DomainConstants.EMPTY_STRING;		
		boolean bError = false;
		StringList slUpdateErrorList;
		RelationshipType relTypeBGP = new RelationshipType(pgApolloConstants.RELATIONSHIP_PGBACKGROUNDPROCESSRELATEDOBJECTS);
		Map mapAttribute;
		DomainObject domainObject;
		BusinessObject boToBeDisconnected;	
		Map mapPreviousRevisionAttribute = new HashMap();
		boolean isUserContextPushed = false;
 
		try 
		{
			mapAttribute = (Map)mapPartOutput.get(pgApolloConstants.KEY_ATTRIBUTESMAP);								
			slUpdateErrorList = (StringList)mapPartOutput.get(pgApolloConstants.STR_ERROR_LIST);
			bError = (boolean)mapPartOutput.get(pgApolloConstants.STR_ERROR);
			
			if(null == mapAttribute)
			{
				mapAttribute = new HashMap();
			}
			
			if(null == slUpdateErrorList)
			{
				slUpdateErrorList = new StringList();
			}

			if(!slUpdateErrorList.isEmpty() && bError)
			{
				mapAttribute.put(pgApolloConstants.ATTRIBUTE_PGLPDPERFCHARACTERISTICSUPDATESTATUS, pgApolloConstants.RANGE_PGLPDPERFCHARACTERISTICSUPDATESTATUS_PENDING);
				mapPreviousRevisionAttribute.put(pgApolloConstants.ATTRIBUTE_PGLPDPERFCHARACTERISTICSUPDATESTATUS, pgApolloConstants.RANGE_PGLPDPERFCHARACTERISTICSUPDATESTATUS_PENDING);

			}
			else
			{
				mapAttribute.put(pgApolloConstants.ATTRIBUTE_PGLPDPERFCHARACTERISTICSUPDATESTATUS, pgApolloConstants.RANGE_PGLPDPERFCHARACTERISTICSUPDATESTATUS_COMPLETED);
				mapPreviousRevisionAttribute.put(pgApolloConstants.ATTRIBUTE_PGLPDPERFCHARACTERISTICSUPDATESTATUS, pgApolloConstants.RANGE_PGLPDPERFCHARACTERISTICSUPDATESTATUS_COMPLETED);

			}	
			
			//Context user won't always have access to set attributes on Released Part. So push context is needed here.
			ContextUtil.pushContext(context, pgApolloConstants.PERSON_USER_AGENT, null, context.getVault().getName());
			isUserContextPushed = true;
			
			context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : postUpdateProcessing >> mapAttribute = "+mapAttribute);
			
			domainObject = DomainObject.newInstance(context, sUpdatedObjectId);	
			String sCharUpdateStatusCurrentObject = domainObject.getInfo(context, pgApolloConstants.SELECT_ATTRIBUTE_PGLPDPERFCHARACTERISTICSUPDATESTATUS);	
			mapAttribute = validateAndSetPartCharacteristicsStatusCompleted(context, mapAttribute, sCharUpdateStatusCurrentObject);
			if(!mapAttribute.isEmpty())
			{
				domainObject.setAttributeValues(context, mapAttribute);
			}
			
			if(!sUpdatedObjectId.equalsIgnoreCase(sObjectId))
			{			
				context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : postUpdateProcessing >> mapPreviousRevisionAttribute = "+mapPreviousRevisionAttribute);
				domainObject = DomainObject.newInstance(context, sObjectId);	
				String sCharUpdateStatusPreviousObject = domainObject.getInfo(context, pgApolloConstants.SELECT_ATTRIBUTE_PGLPDPERFCHARACTERISTICSUPDATESTATUS);
				mapPreviousRevisionAttribute = validateAndSetPartCharacteristicsStatusCompleted(context, mapPreviousRevisionAttribute, sCharUpdateStatusPreviousObject);
				if(!mapPreviousRevisionAttribute.isEmpty())
				{
					domainObject.setAttributeValues(context, mapPreviousRevisionAttribute);					
				}
			}

			String sAssociatedBackgroundJobIdSelect = new StringBuilder("to[").append(pgApolloConstants.RELATIONSHIP_PGBACKGROUNDPROCESSRELATEDOBJECTS).append("].from.").append(DomainConstants.SELECT_ID).toString();
			domainObject = DomainObject.newInstance(context, sObjectId);			
			StringList slPartBackgroundJobId = domainObject.getInfoList(context, sAssociatedBackgroundJobIdSelect);		
			String sBackgroundProcessObjectId = domBackgroundProcess.getObjectId(context);
			
			context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : postUpdateProcessing  slPartBackgroundJobId = "+slPartBackgroundJobId+"  sBackgroundProcessObjectId = "+sBackgroundProcessObjectId);
			
			if(null != slPartBackgroundJobId && !slPartBackgroundJobId.isEmpty() && slPartBackgroundJobId.contains(sBackgroundProcessObjectId))
			{
				boToBeDisconnected = new BusinessObject(sObjectId);
				domBackgroundProcess.disconnect(context, relTypeBGP, true, boToBeDisconnected);				
				context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : postUpdateProcessing  Disconnection of = "+sObjectId+"  with sBackgroundProcessObjectId = "+sBackgroundProcessObjectId);
			}		
			
		} 
		catch (Exception e)
		{
			loggerTrace.error(e.getMessage(), e);
			sError = new StringBuilder(pgApolloConstants.STR_ERROR_POSTPROCESS_UPDATEDREVISION).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getMessage()).toString();								
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
	 * Method to validate and return attribute map for char status attribute
	 * @param context
	 * @param mapAttribute
	 * @param sCharUpdateStatus
	 * @return
	 * @throws MatrixException
	 */
	public Map validateAndSetPartCharacteristicsStatusCompleted(matrix.db.Context context, Map mapAttribute, String sCharUpdateStatus)throws MatrixException 
	{
		context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : validateAndSetPartCharacteristicsStatusCompleted - mapAttribute >> "+mapAttribute);
		if(!pgApolloConstants.RANGE_PGLPDPERFCHARACTERISTICSUPDATESTATUS_INPROGRESS.equalsIgnoreCase(sCharUpdateStatus) && !pgApolloConstants.RANGE_PGLPDPERFCHARACTERISTICSUPDATESTATUS_COMPLETED.equalsIgnoreCase(sCharUpdateStatus))
		{
			mapAttribute.put(pgApolloConstants.ATTRIBUTE_PGLPDPERFCHARACTERISTICSUPDATESTATUS, pgApolloConstants.RANGE_PGLPDPERFCHARACTERISTICSUPDATESTATUS_PENDING);
		}
		context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : validateAndSetPartCharacteristicsStatusCompleted - mapAttribute << "+mapAttribute);
		return mapAttribute;
	}

	/**
	 * Method to update In Work Part
	 * @param context
	 * @param mapPart
	 * @param sMode
	 * @param sObjectId
	 * @param ebomservice
	 * @return
	 * @throws Exception
	 */
	public Map updateInWorkPart(matrix.db.Context context, Map mapPart, String sMode, String sObjectId,GenerateEBOMService ebomservice)throws Exception 
	{

		Map mapPartOutput = new HashMap();
		String sDesignParamNotPublished = EnoviaResourceBundle.getProperty(context, CharacteristicMasterConstants.CHARACTERISTIC_MASTER_STRING_RESOURCE, context.getLocale(),"CharacteristicMaster.MassUpdateCharacteristics.Error.DesignParamNotPublished");
		String sControlIsNotInENOVIA = EnoviaResourceBundle.getProperty(context, CharacteristicMasterConstants.CHARACTERISTIC_MASTER_STRING_RESOURCE, context.getLocale(),"CharacteristicMaster.MassUpdateCharacteristics.Error.ControlIsNotInENOVIA");

		StringList slUpdateErrorList = new StringList();	

		boolean bError = false;
		StringList slLocalErrorList = new StringList();
		Map mapAttributeAPP = new HashMap();
		String sError;

		try 
		{
			Map mapProduct;
			boolean bIsDesignParamPresent;
			Map mapUpdateCharOutput;

			String sObjectName = (String)mapPart.get(DomainConstants.SELECT_NAME);
			String sObjectRevision = (String)mapPart.get(DomainConstants.SELECT_REVISION);				

			String sIsVPLMControlled = (String)mapPart.get(pgApolloConstants.SELECT_ASSOCIATEDPHYSICALPRODUCT_ISVPLMCONTROLLED);		
			mapProduct = ebomservice.readPerformanceCharsFromDesignParameterFile(context, sObjectId);
			bIsDesignParamPresent = (boolean)mapProduct.get(pgApolloConstants.STR_ISDESIGNPARAMPRESENT);
			mapPartOutput.put(DomainConstants.SELECT_ID, sObjectId);

			context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : massUpdateAPPsForCriteria - bIsDesignParamPresent = "+bIsDesignParamPresent);

			if(!bIsDesignParamPresent)
			{
				slUpdateErrorList.add(sDesignParamNotPublished);
				bError = true;
			}

			if(pgApolloConstants.STR_TRUE_FLAG_CAPS.equalsIgnoreCase(sIsVPLMControlled))
			{
				bError = true;	
				//Control is in CATIA
				slUpdateErrorList.add(sControlIsNotInENOVIA);

			}
			if(!bError)
			{
				//Check if previous Background Job operation is still in progress
				String sManualUpdateJobTitle = new StringBuilder(pgApolloConstants.STR_MODE_UPDATE_CHAR).append(pgApolloConstants.CONSTANT_STRING_SPACE).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.CONSTANT_STRING_SPACE).append(sObjectName).append(pgApolloConstants.CONSTANT_STRING_SPACE).append(sObjectRevision).toString();
				String sIsBackgroundJobRunning = ebomservice.checkRunningBackGroundJobConnected(context, sObjectId, sManualUpdateJobTitle, sMode);
				context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : updateInWorkPart - "+sMode+": Previous Background Job running = " + sIsBackgroundJobRunning);

				if(pgApolloConstants.STR_FALSE_FLAG.equalsIgnoreCase(sIsBackgroundJobRunning))
				{
					//Update Characteristics based on existing design parameter file
					mapUpdateCharOutput = ebomservice.updateCharacteristics(context, sObjectId, mapProduct, sMode);

					slLocalErrorList = (StringList)mapUpdateCharOutput.get(pgApolloConstants.STR_ERROR);

					if(slLocalErrorList == null || slLocalErrorList.isEmpty())
					{
						slLocalErrorList = new StringList();
					}
					else
					{
						bError = true;
					}
				}
				else
				{
					bError = true;	
					slUpdateErrorList.add(sIsBackgroundJobRunning);
				}			

			}

			slUpdateErrorList.addAll(slLocalErrorList);
			mapPartOutput.put(pgApolloConstants.KEY_ATTRIBUTESMAP, mapAttributeAPP);			
		} 
		catch (Exception e)
		{
			loggerTrace.error(e.getMessage(), e);
			bError = true;
			sError = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getMessage()).toString();	
			slUpdateErrorList.add(sError);
		}	
		mapPartOutput.put(pgApolloConstants.STR_ERROR_LIST, slUpdateErrorList);	
		mapPartOutput.put(pgApolloConstants.STR_ERROR, bError);
		mapPartOutput.put(DomainConstants.SELECT_ID, sObjectId);
		return mapPartOutput;
	}

	/**
	 * Method to revise released part
	 * @param context
	 * @param mapPart
	 * @param mapBackgroundProcess
	 * @param sMode
	 * @param sObjectId
	 * @param ebomservice
	 * @return
	 * @throws Exception
	 */
	public Map updateReleasedPart(matrix.db.Context context, Map mapPart, Map mapBackgroundProcess, String sMode, String sObjectId, GenerateEBOMService ebomservice) throws Exception 
	{
		Map mapPartOutput = new HashMap();
		Map mapAttributeAPP = new HashMap();
		StringList slUpdateErrorList = new StringList();
		StringList slLocalErrorList = new StringList();
		boolean bError = false;
		String sError;

		String sDesignParamNotPublished = EnoviaResourceBundle.getProperty(context, CharacteristicMasterConstants.CHARACTERISTIC_MASTER_STRING_RESOURCE, context.getLocale(),"CharacteristicMaster.MassUpdateCharacteristics.Error.DesignParamNotPublished");
		String sNextRevisionAlreadyPresent = EnoviaResourceBundle.getProperty(context, CharacteristicMasterConstants.CHARACTERISTIC_MASTER_STRING_RESOURCE, context.getLocale(),"CharacteristicMaster.MassUpdateCharacteristics.Error.NextRevisionAlreadyPresent");
		String sReviseOperationFailed = EnoviaResourceBundle.getProperty(context, CharacteristicMasterConstants.CHARACTERISTIC_MASTER_STRING_RESOURCE, context.getLocale(),"CharacteristicMaster.MassUpdateCharacteristics.Error.ReviseOperationFailed");

		try
		{
			String sReasonForChange = (String)mapBackgroundProcess.get(pgApolloConstants.SELECT_ATTRIBUTE_REASON_FOR_CHANGE);
			String sNextId = (String)mapPart.get(pgApolloConstants.SELECT_NEXT_ID);	

			Map mapProduct;
			boolean bIsDesignParamPresent;
			String sRevisedObjectId;
			Map mapUpdateCharOutput;
			if(UIUtil.isNullOrEmpty(sNextId))
			{
				//Revise Part
				sRevisedObjectId = pgApolloCommonUtil.revisePart(context, sObjectId);
				if(sRevisedObjectId.contains(pgApolloConstants.STR_ERROR))
				{
					slUpdateErrorList.add(sRevisedObjectId);
					mapPartOutput.put(DomainConstants.SELECT_ID, sObjectId);
					bError = true;
				}
				else
				{
					if(UIUtil.isNotNullAndNotEmpty(sRevisedObjectId))
					{
						mapPartOutput.put(DomainConstants.SELECT_ID, sRevisedObjectId);
						mapProduct = ebomservice.readPerformanceCharsFromDesignParameterFile(context, sRevisedObjectId);
						bIsDesignParamPresent = (boolean)mapProduct.get(pgApolloConstants.STR_ISDESIGNPARAMPRESENT);


						if(bIsDesignParamPresent)
						{
							//Update Characteristics based on existing design parameter file
							mapUpdateCharOutput = ebomservice.updateCharacteristics(context, sRevisedObjectId, mapProduct, sMode);						
							slLocalErrorList = (StringList)mapUpdateCharOutput.get(pgApolloConstants.STR_ERROR);						
							if(slLocalErrorList == null || slLocalErrorList.isEmpty())
							{
								slLocalErrorList = new StringList();
							}
							else
							{
								bError = true;
							}


						}
						else
						{
							//Design Param not present
							slUpdateErrorList.add(sDesignParamNotPublished);
							bError = true;
							mapAttributeAPP.put(pgApolloConstants.ATTRIBUTE_PGLPDPERFCHARACTERISTICSUPDATESTATUS, pgApolloConstants.RANGE_PGLPDPERFCHARACTERISTICSUPDATESTATUS_PENDING);
						}								

						sObjectId = sRevisedObjectId;
						//Set Reason For Change on Part
						mapAttributeAPP.put(pgApolloConstants.ATTRIBUTE_REASON_FOR_CHANGE, sReasonForChange);	

					}
					else
					{
						//Revise is failed for APP
						slUpdateErrorList.add(sReviseOperationFailed);
						mapPartOutput.put(DomainConstants.SELECT_ID, sObjectId);
						bError = true;
					}

				}
			}
			else
			{
				bError = true;
				//Next Revision is already present
				slUpdateErrorList.add(sNextRevisionAlreadyPresent);
			}	
			slUpdateErrorList.addAll(slLocalErrorList);
			mapPartOutput.put(pgApolloConstants.KEY_ATTRIBUTESMAP, mapAttributeAPP);
		}
		catch (Exception e)
		{
			loggerTrace.error(e.getMessage(), e);
			bError = true;
			sError = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getMessage()).toString();	
			slUpdateErrorList.add(sError);
		}
		mapPartOutput.put(pgApolloConstants.STR_ERROR_LIST, slUpdateErrorList);	
		mapPartOutput.put(pgApolloConstants.STR_ERROR, bError);	

		mapPartOutput.put(DomainConstants.SELECT_ID, sObjectId);
		return mapPartOutput;
	}



	/**
	 * Method to generate output summary and send notification
	 * @param context
	 * @param mlPartOutput
	 * @param mapBackgroundProcess
	 * @param sCOObjectId
	 * @param slCAPartList
	 * @param sCAError 
	 * @param bBackgroundJobProcessError 
	 * @throws Exception
	 */
	public String generateOutputSummaryAndSendNotification(Context context, MapList mlPartOutput, Map mapBackgroundProcess, StringList slCAPartList, String sCAError, boolean bBackgroundJobProcessError) throws Exception 
	{
		String sError = DomainConstants.EMPTY_STRING;
		try
		{
			StringList slPartIdList = new StringList();
			String sPartId;
			Map mapPart;
			StringList slError;
			Map mapObjectError = new HashMap();
			Map mapObjectCA = new HashMap();
			String sCAName;	
			boolean bChangeExist = false;
			String sLocalPartId = null;
			Map caMap = null;
			MapList proposedOrRealizedchangeActionList = null;
			Iterator proposedChangeItr = null;

			String sLanguage = Locale.ENGLISH.getLanguage();

			if(!slCAPartList.isEmpty())
			{
				Map mpCAInfo = ChangeUtil.getChangeObjectsInProposed(context, new StringList(DomainConstants.SELECT_NAME), slCAPartList.toArray(new String[slCAPartList.size()]), 1);
				Map mpRealizedCAInfo = ChangeUtil.getChangeObjectsInRealized(context, new StringList(DomainConstants.SELECT_NAME), slCAPartList.toArray(new String[slCAPartList.size()]), 1);

				context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : generateOutputSummaryAndSendNotification mpCAInfo = "+mpCAInfo);
				context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : generateOutputSummaryAndSendNotification mpRealizedCAInfo = "+mpRealizedCAInfo);

				int iPartListSize = slCAPartList.size();
				for(int iPart=0; iPart<iPartListSize; iPart++)
				{
					bChangeExist = false;
					sLocalPartId = slCAPartList.get(iPart);
					proposedOrRealizedchangeActionList = (MapList)mpCAInfo.get(sLocalPartId);
					if(proposedOrRealizedchangeActionList.isEmpty() && mpRealizedCAInfo.containsKey(sLocalPartId))
					{
						proposedOrRealizedchangeActionList = (MapList)mpRealizedCAInfo.get(sLocalPartId);	
					}
					if(!proposedOrRealizedchangeActionList.isEmpty())
					{
						proposedChangeItr = proposedOrRealizedchangeActionList.iterator();
						while(proposedChangeItr.hasNext()){
							caMap = (Map)proposedChangeItr.next();	
							if(ChangeConstants.TYPE_CHANGE_ACTION.equals(caMap.get(DomainConstants.SELECT_TYPE))){	
								bChangeExist = true;
								sCAName = (String)caMap.get(DomainConstants.SELECT_NAME);
								mapObjectCA.put(sLocalPartId, sCAName);
								break;
							}
						}
						if(!bChangeExist)
						{
							mapObjectCA.put(sLocalPartId, DomainConstants.EMPTY_STRING);
						}
					} 
					else 
					{
						mapObjectCA.put(sLocalPartId, DomainConstants.EMPTY_STRING);
					}
				}
			}

			context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : generateOutputSummaryAndSendNotification mapObjectCA = "+mapObjectCA);

			for(Object objMap : mlPartOutput)
			{
				mapPart = (Map)objMap;
				sPartId = (String)mapPart.get(DomainConstants.SELECT_ID);
				if(UIUtil.isNotNullAndNotEmpty(sPartId))
				{
					slPartIdList.add(sPartId);
					slError = (StringList)mapPart.get(pgApolloConstants.STR_ERROR_LIST);
					if(null == slError || slError.isEmpty())
					{
						slError = new StringList();
					}
					if(slCAPartList.contains(sPartId) && UIUtil.isNotNullAndNotEmpty(sCAError))
					{
						slError.add(sCAError);
					}
					mapObjectError.put(sPartId, slError);
				}
			}
			
			context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : generateOutputSummaryAndSendNotification mapObjectError = "+mapObjectError);


			if(!slPartIdList.isEmpty())
			{
				MapList mlXLSOutput = new MapList();
				
				MapList mlPartInfo = getPartInfo(context, slPartIdList);	

				int i = 0;
				String sLocalError;
				StringList slErrorList;
				String sPartCurrent;
				String sPartOwner;
				String sOwnerFullName;

				for(Object objMap : mlPartInfo)
				{
					mapPart = (Map)objMap;
					sPartId = (String)mapPart.get(DomainConstants.SELECT_ID);					
					i+=1;
					mapPart.put(DomainConstants.ATTRIBUTE_COUNT, Integer.toString(i));

					sPartCurrent = (String)mapPart.get(DomainConstants.SELECT_CURRENT);
					sPartCurrent = EnoviaResourceBundle.getStateI18NString(context,DomainConstants.POLICY_EC_PART ,sPartCurrent, sLanguage);				

					mapPart.put(DomainConstants.SELECT_CURRENT, sPartCurrent);

					sPartOwner = (String)mapPart.get(DomainConstants.SELECT_OWNER);
					sOwnerFullName = pgApolloCommonUtil.getOwnerFullNameList(context, new StringList(sPartOwner), true);
					mapPart.put(DomainConstants.SELECT_OWNER, sOwnerFullName);

					if(slCAPartList.contains(sPartId))
					{
						mapPart.put(ChangeConstants.TYPE_CHANGE_ACTION, mapObjectCA.getOrDefault(sPartId,DomainConstants.EMPTY_STRING));
					}
					else
					{
						mapPart.put(ChangeConstants.TYPE_CHANGE_ACTION, DomainConstants.EMPTY_STRING);
					}
					slErrorList = (StringList)mapObjectError.getOrDefault(sPartId, new StringList());
					if(null != slErrorList && !slErrorList.isEmpty())
					{
						mapPart.put(DomainConstants.SELECT_STATUS, pgApolloConstants.STR_ERROR);
						sLocalError = StringUtil.join(slErrorList, pgApolloConstants.CONSTANT_STRING_NEWLINE);
						mapPart.put(pgApolloConstants.STR_ERROR, sLocalError);
					}
					else
					{
						mapPart.put(DomainConstants.SELECT_STATUS, pgApolloConstants.STR_SUCCESS);
						mapPart.put(pgApolloConstants.STR_ERROR, DomainConstants.EMPTY_STRING);
					}
					mlXLSOutput.add(mapPart);
				}

				String sBackgroundJobName = (String)mapBackgroundProcess.get(DomainConstants.SELECT_NAME);
				String sBackgroundJobTitle = (String)mapBackgroundProcess.get(pgApolloConstants.SELECT_ATTRIBUTE_TITLE);
				
				String sHeaderList = pgApolloCommonUtil.getPageProperty(context, pgApolloConstants.STR_APOLLO_CONFIG_PAGE_FILENAME,"pgApolloConfiguration.MassUpdateCharacteristics.OutputSummaryHeaderMapping");
				String sSelectList = pgApolloCommonUtil.getPageProperty(context, pgApolloConstants.STR_APOLLO_CONFIG_PAGE_FILENAME,"pgApolloConfiguration.MassUpdateCharacteristics.OutputSummarySelectMapping");

				StringList slHeaderList = StringUtil.split(sHeaderList, pgApolloConstants.CONSTANT_STRING_PIPE);
				StringList slSelectList = StringUtil.split(sSelectList, pgApolloConstants.CONSTANT_STRING_PIPE);

				StringBuilder sbFileName = new StringBuilder();
				sbFileName.append(sBackgroundJobName).append(pgApolloConstants.CONSTANT_STRING_UNDERSCORE).append(sBackgroundJobTitle).append(pgApolloConstants.STR_XLSXFILE_EXTENSION).toString();
				String sWorkspacePath = context.createWorkspace();

				StringBuilder sbFilePathWithName = new StringBuilder();
				sbFilePathWithName.append(sWorkspacePath).append(File.separator).append(sbFileName.toString());
				
				String sFileName = sbFileName.toString();
				String sReportFile = sbFilePathWithName.toString();
				
				context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : generateOutputSummaryAndSendNotification mlXLSOutput = "+mlXLSOutput);
				context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : generateOutputSummaryAndSendNotification slSelectList = "+slSelectList);

				pgApolloCommonUtil.writeXLSXFile(context, mlXLSOutput, slHeaderList, slSelectList, sFileName, sReportFile);

				sendMail(context, sBackgroundJobName, sReportFile, mapBackgroundProcess, bBackgroundJobProcessError);
				
				Files.deleteIfExists(Paths.get(sReportFile));

			}
		} 
		catch (Exception e)
		{
			loggerTrace.error(e.getMessage(), e);
			sError = new StringBuilder(pgApolloConstants.STR_ERROR_REPORT_GENERATION_NOTIFICATION).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getMessage()).toString();								
		}
		return sError;
	}



	/**
	 * Method to send Email to Owner
	 * @param context
	 * @param sBackgroundJobName
	 * @param sReportFile
	 * @param mapBackgroundProcess
	 * @param bBackgroundJobProcessError
	 * @return
	 * @throws Exception
	 */
	private String sendMail(Context context, String sBackgroundJobName, String sReportFile, Map mapBackgroundProcess, boolean bBackgroundJobProcessError) throws Exception 
	{	
		String sResponse = pgApolloConstants.STR_SUCCESS;
		try 
		{
			String sBackgroundProcessStatus = pgApolloConstants.STR_SUCCESS;
			if(bBackgroundJobProcessError)
			{
				sBackgroundProcessStatus = pgApolloConstants.STR_FAILED;
			}			
			String sOwner = (String)mapBackgroundProcess.get(DomainConstants.SELECT_OWNER);
			
			StringBuilder sbMailMessage = new StringBuilder(MessageUtil.getMessage(context, null, "CharacteristicMaster.MassUpdateCharacteristics.Mail.Message",new String[] {sBackgroundJobName}, null, MessageUtil.getLocale(context),CharacteristicMasterConstants.CHARACTERISTIC_MASTER_STRING_RESOURCE));
			StringBuilder sbMailSubject = new StringBuilder(MessageUtil.getMessage(context, null, "CharacteristicMaster.MassUpdateCharacteristics.Mail.Subject",new String[] {sBackgroundProcessStatus, sBackgroundJobName}, null, MessageUtil.getLocale(context),CharacteristicMasterConstants.CHARACTERISTIC_MASTER_STRING_RESOURCE));

			String[] arguments = new String[7];
			arguments[0] = PropertyUtil.getEnvironmentProperty(context,"MX_SMTP_HOST");//host
			arguments[1] = PersonUtil.getEmail(context,pgApolloConstants.PERSON_USER_AGENT);//from user
			arguments[2] = PersonUtil.getEmail(context, sOwner);//to user
			arguments[3] = sReportFile;//attached files path
			arguments[4] = sbMailSubject.toString();//mail subject
			arguments[5] = pgApolloConstants.CONSTANT_STRING_NEWLINE + sbMailMessage.toString();//mail body
			arguments[6] = pgApolloConstants.PERSON_PLMADMIN;//from user
			sResponse = pgApolloCommonUtil.sendEmailToUser(arguments);
		} 
		catch (Exception e) 
		{
			sResponse = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage()).toString();
			loggerTrace.error(e.getMessage(), e);
		}

		return sResponse;
	}


	

	/**
	 * Method to get Unique Key for Characteristics Map 
	 * @param mapBasicAttributes
	 * @param isDesignDrivenIncluded
	 */
	public static String getUniqueKeyForCharacteristics(Map mapBasicAttributes, boolean isDesignDrivenIncluded)
	{
		String sCharTitle = (String)mapBasicAttributes.get(DomainConstants.SELECT_ATTRIBUTE_TITLE);
		String sCharCategory = (String)mapBasicAttributes.get(pgApolloConstants.SELECT_ATTRIBUTE_PG_CHARACTERISTIC_CATEGORY);
		String sCharCategorySpecifics = (String)mapBasicAttributes.get(pgApolloConstants.SELECT_ATTRIBUTE_PG_CATEGORYSPECIFICS);
		String sCharSpecifics = (String)mapBasicAttributes.get(pgApolloConstants.SELECT_ATTRIBUTE_CHARACTERISTICSPECIFIC);
		String sCharReportType = (String)mapBasicAttributes.get(pgApolloConstants.SELECT_ATTRIBUTE_REPORT_TYPE);
		String sCharIsDesignDriven = (String)mapBasicAttributes.get(pgApolloConstants.SELECT_ATTRIBUTE_PG_ISDESIGNDRIVEN);

		if(UIUtil.isNullOrEmpty(sCharSpecifics)) {
			sCharSpecifics = DomainConstants.EMPTY_STRING;
		}
		if(UIUtil.isNullOrEmpty(sCharReportType)) {
			sCharReportType = DomainConstants.EMPTY_STRING;
		}

		StringBuilder sbKeyBuilder = new StringBuilder();
		sbKeyBuilder.append(sCharCategory);
		sbKeyBuilder.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sCharCategorySpecifics);
		sbKeyBuilder.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sCharTitle);
		sbKeyBuilder.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sCharSpecifics);
		sbKeyBuilder.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sCharReportType);
		if(isDesignDrivenIncluded)
		{
			sbKeyBuilder.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sCharIsDesignDriven);
		}
		return sbKeyBuilder.toString();
	}



	/**
	 * Method to get CM associated part list
	 * @param context
	 * @param slCMIdList
	 * @param bIncludeAll
	 * @param sCriteriaPhysicalIdForValidation 
	 * @return
	 * @throws MatrixException
	 */
	public static StringList getCMAssociatedParts(Context context, StringList slCMIdList, boolean bIncludeAll, String sCriteriaPhysicalIdForValidation) throws MatrixException 
	{
		StringList slValidPartIdList;

		StringBuilder sbAssociatedPartIdSelect = new StringBuilder();				
		sbAssociatedPartIdSelect.append("from[").append(pgApolloConstants.RELATIONSHIP_DERIVED_CHARACTERISTIC).append("].to.relationship[").append(pgApolloConstants.RELATIONSHIP_PARAMETER_AGGREGATION).append("].from.id");

		StringBuilder sbAssociatedPartRelAttributeSelect = new StringBuilder();				
		sbAssociatedPartRelAttributeSelect.append("from[").append(pgApolloConstants.RELATIONSHIP_DERIVED_CHARACTERISTIC).append("].").append(pgApolloConstants.SELECT_ATTRIBUTE_EVALUATED_CRITERIA);

		String sAssociatedPartIdSelect = sbAssociatedPartIdSelect.toString();
		String sAssociatedPartRelAttributeSelect = sbAssociatedPartRelAttributeSelect.toString();

		StringList slCMSelects = new StringList();
		slCMSelects.add(sAssociatedPartIdSelect);
		slCMSelects.add(sAssociatedPartRelAttributeSelect);

		Set setAssociatedParts = new LinkedHashSet();		

		context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : getCMAssociatedParts slCMIdList = "+slCMIdList);
		
		context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : getCMAssociatedParts sCriteriaPhysicalIdForValidation = "+sCriteriaPhysicalIdForValidation);


		if(!slCMIdList.isEmpty())
		{			
			MapList mlCMObjectsInfo = DomainObject.getInfo(context,slCMIdList.toArray(new String[slCMIdList.size()]),slCMSelects);
			context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : getCMAssociatedParts mlCMObjectsInfo = "+mlCMObjectsInfo);

			Map mapCMPartInfo;

			for(Object objectMap : mlCMObjectsInfo)
			{
				mapCMPartInfo = (Map)objectMap;		

				StringList slPartIdList = pgApolloCommonUtil.getStringListMultiValue(mapCMPartInfo.get(sAssociatedPartIdSelect));
				StringList slPartEvaluatedAttributeList = pgApolloCommonUtil.getStringListMultiValue(mapCMPartInfo.get(sAssociatedPartRelAttributeSelect));		

				if(null != slPartIdList && !slPartIdList.isEmpty())
				{						
					String sPartId;
					String sPartEvaluateAttribute;

					for(int n=0; n<slPartIdList.size(); n++)
					{
						sPartEvaluateAttribute = slPartEvaluatedAttributeList.get(n);
						
						context.printTrace(pgApolloConstants.TRACE_LPD, "sPartEvaluateAttribute : = "+sPartEvaluateAttribute);

						if(UIUtil.isNotNullAndNotEmpty(sPartEvaluateAttribute) && (UIUtil.isNullOrEmpty(sCriteriaPhysicalIdForValidation) || (UIUtil.isNotNullAndNotEmpty(sCriteriaPhysicalIdForValidation) && sPartEvaluateAttribute.contains(sCriteriaPhysicalIdForValidation))))
						{
							sPartId = slPartIdList.get(n);
							if(UIUtil.isNotNullAndNotEmpty(sPartId))
							{
								setAssociatedParts.add(sPartId);
							}
						}

					}			
				}				
			}

		}

		StringList slAllAssociatedParts = new StringList();		
		slAllAssociatedParts.addAll(setAssociatedParts);	
		context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : getCMAssociatedParts slAllAssociatedParts = "+slAllAssociatedParts);

		slValidPartIdList = filterBasedOnPartConditions(context, slAllAssociatedParts, bIncludeAll);	
		
		context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : getCMAssociatedParts slValidPartIdList = "+slValidPartIdList);

		return slValidPartIdList;
	}



	/**
	 * Method to get Criteria Associated Part List
	 * @param context
	 * @param slValidCriteriaList
	 * @param bIncludeAll 
	 * @return
	 * @throws FrameworkException
	 */
	public static StringList getCriteriaAssociatedPartList(Context context, StringList slValidCriteriaList, boolean bIncludeAll) throws FrameworkException
	{
		StringList slValidAssociatedParts;
		StringList slCriteriaSelectable = new StringList();
		slCriteriaSelectable.add(DomainConstants.SELECT_ID);		

		StringBuilder sbAPPIdSelect = new StringBuilder();
		sbAPPIdSelect.append(pgApolloConstants.CONSTANT_STRING_SELECT_FROM).append(pgApolloConstants.RELATIONSHIP_EVALUATED_PART).append(pgApolloConstants.CONSTANT_STRING_SELECT_TOID);

		String sAPPIdSelect = sbAPPIdSelect.toString();

		slCriteriaSelectable.add(sAPPIdSelect);		
		StringList slAssociatedPart;		
		Set setAssociatedParts = new LinkedHashSet();

		if(!slValidCriteriaList.isEmpty())
		{			
			MapList mlCriteriaObjectsInfo = DomainObject.getInfo(context,slValidCriteriaList.toArray(new String[slValidCriteriaList.size()]),slCriteriaSelectable);

			Map mapCriteria;

			for(Object objectMap : mlCriteriaObjectsInfo)
			{
				mapCriteria = (Map)objectMap;				
				slAssociatedPart = pgApolloCommonUtil.getStringListMultiValue(mapCriteria.get(sAPPIdSelect));
				setAssociatedParts.addAll(slAssociatedPart);
			}

		}	

		StringList slAllAssociatedParts = new StringList();		
		slAllAssociatedParts.addAll(setAssociatedParts);		

		slValidAssociatedParts = filterBasedOnPartConditions(context, slAllAssociatedParts, bIncludeAll);	

		return slValidAssociatedParts;
	}


	/**
	 * Method to filter Part list based on conditions
	 * @param context
	 * @param slAllAssociatedParts
	 * @param bIncludeAll
	 * @return
	 * @throws FrameworkException
	 */
	public static StringList filterBasedOnPartConditions(Context context, StringList slAllAssociatedParts, boolean bIncludeAll) throws FrameworkException 
	{
		StringList slValidAssociatedParts = new StringList();
		
		slAllAssociatedParts = removeEmptyIdsIfPresent(slAllAssociatedParts);
		

		if(!slAllAssociatedParts.isEmpty())
		{	
			if(bIncludeAll)
			{			
				slValidAssociatedParts.addAll(slAllAssociatedParts);
			}
			else
			{
				StringList slPartSelectable = new StringList();
				slPartSelectable.add(DomainConstants.SELECT_ID);
				slPartSelectable.add(DomainConstants.SELECT_CURRENT);
				slPartSelectable.add(pgApolloConstants.SELECT_ISLAST);

				MapList mlPartsObjectsInfo = DomainObject.getInfo(context,slAllAssociatedParts.toArray(new String[slAllAssociatedParts.size()]),slPartSelectable);

				Map mapPart;
				String sIsLast;
				String sPartObjectId;
				String sCurrent;
				String sPhysicalProductControl;

				for(Object objectMap : mlPartsObjectsInfo)
				{
					mapPart = (Map)objectMap;	
					sIsLast = (String)mapPart.get(pgApolloConstants.SELECT_ISLAST);		
					sCurrent = (String)mapPart.get(DomainConstants.SELECT_CURRENT);

					if(pgApolloConstants.STR_TRUE_FLAG.equalsIgnoreCase(sIsLast) &&
							(DomainConstants.STATE_PART_RELEASE.equalsIgnoreCase(sCurrent) || pgApolloConstants.STATE_RELEASED.equalsIgnoreCase(sCurrent) || DomainConstants.STATE_PART_PRELIMINARY.equalsIgnoreCase(sCurrent)))
					{
						sPartObjectId = (String)mapPart.get(DomainConstants.SELECT_ID);	
						if(UIUtil.isNotNullAndNotEmpty(sPartObjectId))
						{
							slValidAssociatedParts.add(sPartObjectId);
						}
					}

				}
			}		

		}

		return slValidAssociatedParts;
	}


	/**
	 * Method to remove Empty Ids if present
	 * @param slValueList
	 * @return
	 */
	private static StringList removeEmptyIdsIfPresent(StringList slValueList) 
	{
		StringList slNewValueList = new StringList();
		if(null!= slValueList && !slValueList.isEmpty())
		{
			for(String sValue: slValueList)
			{
				if(UIUtil.isNotNullAndNotEmpty(sValue))
				{
					slNewValueList.add(sValue);
				}
			}			
		}		
		return slNewValueList;
	}


	/**
	 * Method to get Criteria attribute values map
	 * @param context
	 * @param sCriteriaObjectId
	 * @return
	 * @throws Exception
	 */
	public static Map getCriteriaAttributeValueMap(Context context, String sCriteriaObjectId) throws Exception
	{    	
		Map criteriaAttributeValueMap = new HashMap();
		try 
		{
			String sApplicableType = ENOCriteriaFactory.getCriteriaById(context, sCriteriaObjectId).getApplicableType(context);
			StringList slAdditionalAttributes = getAdditionalConfiguredAttributes(context, sApplicableType);
			StringList critAttributes = CriteriaUtil.getCriteriaAttributesConfigured(context, sApplicableType);

			StringList slBusSelects = new StringList();
			slBusSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA_1);
			slBusSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA_2);
			slBusSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA_3);
			slBusSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA_4);
			slBusSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA_5);
			slBusSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA_6);
			slBusSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA_7);
			slBusSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA_8);		
			slBusSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA_9);		
			
			StringList slMultiValueSelects = new StringList();
			slMultiValueSelects.addAll(slBusSelects);

			StringList selectAttributeList = new StringList();
			String sAttributeName;
			if(null!=slAdditionalAttributes && !slAdditionalAttributes.isEmpty())
			{
				for(int i=0;i<slAdditionalAttributes.size();i++)
				{
					sAttributeName = PropertyUtil.getSchemaProperty(context, slAdditionalAttributes.get(i));
					selectAttributeList.add(sAttributeName);
				}
			}				
			if(UIUtil.isNotNullAndNotEmpty(sCriteriaObjectId)) 				
			{
				DomainObject domObj = DomainObject.newInstance(context,sCriteriaObjectId);	
				Map busMap = domObj.getInfo(context, slBusSelects, slMultiValueSelects);
				
				context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : getCriteriaAttributeValueMap sCriteriaObjectId = "+sCriteriaObjectId+" busMap = "+busMap);

				StringList criteria1ValueList = pgApolloCommonUtil.getStringListMultiValue(busMap.get(pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA_1));
				criteria1ValueList.sort();
				StringList criteria2ValueList = pgApolloCommonUtil.getStringListMultiValue(busMap.get(pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA_2));
				criteria2ValueList.sort();
				StringList criteria3ValueList = pgApolloCommonUtil.getStringListMultiValue(busMap.get(pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA_3));
				criteria3ValueList.sort();
				StringList criteria4ValueList = pgApolloCommonUtil.getStringListMultiValue(busMap.get(pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA_4));
				criteria4ValueList.sort();
				StringList criteria5ValueList = pgApolloCommonUtil.getStringListMultiValue(busMap.get(pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA_5));
				criteria5ValueList.sort();
				StringList criteria6ValueList = pgApolloCommonUtil.getStringListMultiValue(busMap.get(pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA_6));
				criteria6ValueList.sort();
				StringList criteria7ValueList = pgApolloCommonUtil.getStringListMultiValue(busMap.get(pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA_7));
				criteria7ValueList.sort();
				StringList criteria8ValueList = pgApolloCommonUtil.getStringListMultiValue(busMap.get(pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA_8));
				criteria8ValueList.sort();
				StringList criteria9ValueList = pgApolloCommonUtil.getStringListMultiValue(busMap.get(pgApolloConstants.SELECT_ATTRIBUTE_CRITERIA_9));
				criteria9ValueList.sort();
				
				criteria1ValueList = removeEmptyStringFromStringList(criteria1ValueList);
				criteria2ValueList = removeEmptyStringFromStringList(criteria2ValueList);
				criteria3ValueList = removeEmptyStringFromStringList(criteria3ValueList);
				criteria4ValueList = removeEmptyStringFromStringList(criteria4ValueList);
				criteria5ValueList = removeEmptyStringFromStringList(criteria5ValueList);
				criteria6ValueList = removeEmptyStringFromStringList(criteria6ValueList);
				criteria7ValueList = removeEmptyStringFromStringList(criteria7ValueList);
				criteria8ValueList = removeEmptyStringFromStringList(criteria8ValueList);
				criteria9ValueList = removeEmptyStringFromStringList(criteria9ValueList);


				if(!critAttributes.isEmpty())
				{
					criteriaAttributeValueMap.put(PropertyUtil.getSchemaProperty(context, critAttributes.get(0)), criteria1ValueList);
				}			
				if(critAttributes.size()>1)
				{
					criteriaAttributeValueMap.put(PropertyUtil.getSchemaProperty(context, critAttributes.get(1)), criteria2ValueList);	
				}
				if(critAttributes.size()>2)
				{
					criteriaAttributeValueMap.put(PropertyUtil.getSchemaProperty(context, critAttributes.get(2)), criteria3ValueList);	
				}
				if(critAttributes.size()>3)
				{
					criteriaAttributeValueMap.put(PropertyUtil.getSchemaProperty(context, critAttributes.get(3)), criteria4ValueList);
				}
				if(critAttributes.size()>4)
				{
					criteriaAttributeValueMap.put(PropertyUtil.getSchemaProperty(context, critAttributes.get(4)), criteria5ValueList);
				} 	
				if(!slAdditionalAttributes.isEmpty())
				{
					criteriaAttributeValueMap.put(PropertyUtil.getSchemaProperty(context, slAdditionalAttributes.get(0)), criteria6ValueList);
				}			
				if(slAdditionalAttributes.size()>1)
				{
					criteriaAttributeValueMap.put(PropertyUtil.getSchemaProperty(context, slAdditionalAttributes.get(1)), criteria7ValueList);	
				}
				if(slAdditionalAttributes.size()>2)
				{
					criteriaAttributeValueMap.put(PropertyUtil.getSchemaProperty(context, slAdditionalAttributes.get(2)), criteria8ValueList);	
				}
				if(slAdditionalAttributes.size()>3)
				{
					criteriaAttributeValueMap.put(PropertyUtil.getSchemaProperty(context, slAdditionalAttributes.get(3)), criteria9ValueList);	
				}				

			}
			else
			{
				if(null!=selectAttributeList && !selectAttributeList.isEmpty())
				{
					for(int i=0;i<selectAttributeList.size();i++)
					{
						criteriaAttributeValueMap.put(selectAttributeList.get(i), new StringList());
					}
				}	
			}

		} 
		catch (Exception e) 
		{
			loggerTrace.error(e.getMessage(), e);
			throw e;
		}
		return criteriaAttributeValueMap;
	}

	/**
	 * Method to remove empty String from string list
	 * @param criteria1ValueList
	 * @return
	 */
	public static StringList removeEmptyStringFromStringList(StringList slValueList) 
	{
		if(null != slValueList && slValueList.contains(DomainConstants.EMPTY_STRING))
		{
			slValueList.remove(DomainConstants.EMPTY_STRING);
		}		
		return slValueList;
	}


	/**
	 * Method Added to read Criteria Additional Configured Attributes
	 * @param context
	 * @param applicableType
	 * @return
	 * @throws Exception
	 */
	public static StringList getAdditionalConfiguredAttributes(Context context, String applicableType) throws Exception 
	{
		StringList slCriteriaAttributes = new StringList();
		String strCriteriaAttributes = pgApolloCommonUtil.getPageProperty(context, pgApolloConstants.STR_APOLLO_CONFIG_PAGE_FILENAME,"pgApolloConfiguration.Criteria.AdditionalAttributes."+applicableType);
		if(UIUtil.isNotNullAndNotEmpty(strCriteriaAttributes))
		{
			slCriteriaAttributes	= StringUtil.split(strCriteriaAttributes, pgApolloConstants.CONSTANT_STRING_COMMA);
		}
		return slCriteriaAttributes;
	}




	/**
	 * Method to find applicable products based on Criteria
	 * @param context
	 * @param sObjectId
	 * @param bIncludeAll
	 * @return
	 * @throws Exception
	 */
	public static StringList findApplicableParts(Context context, String sObjectId, boolean bIncludeAll) throws Exception 
	{
		StringList slValidAPPIdList = new StringList();
		
		if(UIUtil.isNotNullAndNotEmpty(sObjectId))
		{
			Map mapCriteriaAttributeMap = EvaluateCriteriaAndUpdateCharacteristics.getCriteriaAttributeValueMap(context, sObjectId);
			
			slValidAPPIdList = findApplicableParts(context, mapCriteriaAttributeMap, bIncludeAll);
		}	
		
		return slValidAPPIdList;
	}


	/**
	 * Method to find applicable products based on Criteria
	 * @param context
	 * @param mapCriteriaAttributeMap
	 * @param bIncludeAll
	 * @return
	 * @throws MatrixException
	 */
	public static StringList findApplicableParts(Context context, Map mapCriteriaAttributeMap, boolean bIncludeAll) throws MatrixException
	{		
		StringList slValidAPPIdList = new StringList();
		
		context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : findApplicableParts mapCriteriaAttributeMap = "+mapCriteriaAttributeMap);
		
		if(null != mapCriteriaAttributeMap && !mapCriteriaAttributeMap.isEmpty())
		{

		StringBuilder sbWhereClause = new StringBuilder();	

		String sType = pgApolloConstants.TYPE_ASSEMBLED_PRODUCT_PART;		

		sbWhereClause = pgApolloCommonUtil.appendWhereClause(sbWhereClause, pgApolloConstants.SELECT_ATTRIBUTE_PGAUTHORINGAPPLICATION, new StringList(pgApolloConstants.RANGE_PGAUTHORINGAPPLICATION_LPD));

		if(!bIncludeAll)
		{
			sbWhereClause = pgApolloCommonUtil.appendWhereClause(sbWhereClause, pgApolloConstants.SELECT_ISLAST, new StringList(pgApolloConstants.STR_TRUE_FLAG));
			
			StringList slCurrent = new StringList();		
			slCurrent.add(DomainConstants.STATE_PART_PRELIMINARY);
			slCurrent.add(DomainConstants.STATE_PART_RELEASE);

			sbWhereClause = pgApolloCommonUtil.appendWhereClause(sbWhereClause, DomainConstants.SELECT_CURRENT, slCurrent);
			
			StringList slCharactersticsUpdateStatus = new StringList();	
			slCharactersticsUpdateStatus.add(pgApolloConstants.RANGE_PGLPDPERFCHARACTERISTICSUPDATESTATUS_COMPLETED);

			sbWhereClause = pgApolloCommonUtil.appendWhereClause(sbWhereClause, pgApolloConstants.SELECT_ATTRIBUTE_PGLPDPERFCHARACTERISTICSUPDATESTATUS, slCharactersticsUpdateStatus);
		}	

		StringList slBusinessArea = (StringList)mapCriteriaAttributeMap.get(pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGBUSINESSAREA);

		sbWhereClause = pgApolloCommonUtil.appendWhereClause(sbWhereClause, pgApolloConstants.SELECT_ATTRIBUTE_ENTERPRISEPART_PGBUSINESSAREA, slBusinessArea);

		StringList slPCP = (StringList)mapCriteriaAttributeMap.get(pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGPRODUCTCATEGORYPLATFORM);		

		StringList slPTP = (StringList)mapCriteriaAttributeMap.get(pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGPRODUCTTECHNOLOGYPLATFORM);		

		StringList slPTC = (StringList)mapCriteriaAttributeMap.get(pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGPRODUCTTECHNOLOGYCHASSIS);		

		StringList slInputIntendedMarkets = (StringList)mapCriteriaAttributeMap.get(pgApolloConstants.ATTRIBUTE_PG_INTENDED_MARKETS);
		
		StringList slRegionList = (StringList)mapCriteriaAttributeMap.get(pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGLPDREGION);
		
		StringList slSubRegionList = (StringList)mapCriteriaAttributeMap.get(pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGLPDSUBREGION);

		
		if(null!= slInputIntendedMarkets && slInputIntendedMarkets.contains(DomainConstants.EMPTY_STRING))
		{
			slInputIntendedMarkets.remove(DomainConstants.EMPTY_STRING);
		}

		StringList slSize = (StringList)mapCriteriaAttributeMap.get(pgApolloConstants.ATTRIBUTE_PG_DSMPRODUCTSIZE);

		sbWhereClause = pgApolloCommonUtil.appendWhereClause(sbWhereClause, pgApolloConstants.SELECT_ATTRIBUTE_PG_DSMPRODUCTSIZE, slSize);

		StringList slReleasePhase = (StringList)mapCriteriaAttributeMap.get(pgApolloConstants.STR_RELEASE_PHASE);

				

		StringList slPartSelects = new StringList();
		slPartSelects.add(DomainConstants.SELECT_ID);
		slPartSelects.add(DomainConstants.SELECT_CURRENT);
		slPartSelects.add(pgApolloConstants.SELECT_ISLAST);
		slPartSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PGAUTHORINGAPPLICATION);
		slPartSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PGLPDPERFCHARACTERISTICSUPDATESTATUS);
		slPartSelects.add("from["+pgApolloConstants.RELATIONSHIP_PG_INTENDED_MARKETS+"].to.name");
		slPartSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_ENTERPRISEPART_PGBUSINESSAREA);
		slPartSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_ENTERPRISEPART_PGPRODUCTCATEGORYPLATFORM);
		slPartSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_ENTERPRISEPART_PGPRODUCTTECHNOLOGYPLATFORM);
		slPartSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_ENTERPRISEPART_PGPRODUCTTECHNOLOGYCHASSIS);
		slPartSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PG_DSMPRODUCTSIZE);		
		slPartSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_RELEASE_PHASE);
		slPartSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_ENTERPRISEPART_PGLPDREGION);
		slPartSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_ENTERPRISEPART_PGLPDSUBREGION);
	

		context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : findApplicableParts sbWhereClause = "+sbWhereClause.toString());

		MapList mlPartList = DomainObject.findObjects(context,			 // Context
				sType, 						// Type
				DomainConstants.QUERY_WILDCARD, //Name
				DomainConstants.QUERY_WILDCARD, // Revision
				null,							//Owner
				pgApolloConstants.VAULT_ESERVICE_PRODUCTION, // Vault
				sbWhereClause.toString(), // Where Clause
				true,				// Include Sub-types Flag
				slPartSelects); // Object Selects

		context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : findApplicableParts mlPartList = "+mlPartList);
		context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : findApplicableParts slInputIntendedMarkets = "+slInputIntendedMarkets);

		if(null != mlPartList && !mlPartList.isEmpty())
		{   			
			Map localMap;			
			String sPartId;

			boolean bApplicableProduct = false;
			for(Object objectMap : mlPartList)
			{
				localMap = (Map)objectMap;
				
				boolean bValidPCP = filterBasedOnPickListValues(context, localMap, slPCP, pgApolloConstants.SELECT_ATTRIBUTE_ENTERPRISEPART_PGPRODUCTCATEGORYPLATFORM);				
							
				
				boolean bValidPTP = filterBasedOnPickListValues(context, localMap, slPTP, pgApolloConstants.SELECT_ATTRIBUTE_ENTERPRISEPART_PGPRODUCTTECHNOLOGYPLATFORM);
				
								
				boolean bValidPTC = filterBasedOnPickListValues(context, localMap, slPTC, pgApolloConstants.SELECT_ATTRIBUTE_ENTERPRISEPART_PGPRODUCTTECHNOLOGYCHASSIS);
				
				
				boolean bValidReleasePhase = filterBasedOnPickListValues(context, localMap, slReleasePhase, pgApolloConstants.SELECT_ATTRIBUTE_RELEASE_PHASE);
				

				boolean bValidRegion = filterBasedOnPickListValues(context, localMap, slRegionList, pgApolloConstants.SELECT_ATTRIBUTE_ENTERPRISEPART_PGLPDREGION);

				
				boolean bValidSubRegion = filterBasedOnPickListValues(context, localMap, slSubRegionList, pgApolloConstants.SELECT_ATTRIBUTE_ENTERPRISEPART_PGLPDSUBREGION);
				

				context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : findApplicableParts bValidPCP = "+bValidPCP+" bValidPTP = "+bValidPTP+" bValidPTC = "+bValidPTC+" bValidReleasePhase = "+bValidReleasePhase+" ");

				
				if(bValidPCP  && bValidPTP && bValidPTC && bValidReleasePhase && bValidRegion && bValidSubRegion)
				{
					bApplicableProduct = filterBasedOnAdditionalCriteriaForApplicableParts(localMap, slInputIntendedMarkets, bIncludeAll);			
					
					context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : findApplicableParts objectMap = "+objectMap+" bApplicableProduct = "+bApplicableProduct);

					if(bApplicableProduct)
					{
						sPartId = (String)localMap.get(DomainConstants.SELECT_ID);	
						if(UIUtil.isNotNullAndNotEmpty(sPartId))
						{
							slValidAPPIdList.add(sPartId);
						}

					}		
				}			

			}
		}
		else
		{
			slValidAPPIdList = new StringList();
		}
		
		}
		
		return slValidAPPIdList;
	}

	/**
	 * Method to filter based on pick list values
	 * @param mapObject
	 * @param slPickListValues
	 * @param sPickListSelectable
	 * @return
	 * @throws MatrixException 
	 */
	public static boolean filterBasedOnPickListValues(Context context, Map mapObject, StringList slPickListValues, String sPickListSelectable) throws MatrixException
	{
		boolean bValid = false;		
		
		StringList slExistingPickListAttributeValueList = pgApolloCommonUtil.getStringListMultiValue(mapObject.get(sPickListSelectable));	
		
		context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : filterBasedOnPickListValues >> slPickListValues = "+slPickListValues+" slExistingPickListAttributeValueList = "+slExistingPickListAttributeValueList);

		
		if(null != slPickListValues && slPickListValues.contains(DomainConstants.EMPTY_STRING))
		{
			slPickListValues.remove(DomainConstants.EMPTY_STRING);
		}
		
		if(null == slPickListValues || slPickListValues.isEmpty())
		{
			bValid = true;
		}
		else if(!slPickListValues.isEmpty() && !slExistingPickListAttributeValueList.isEmpty())
		{
			for(String sExistingPickListValue : slExistingPickListAttributeValueList)
			{
				if(pgApolloCommonUtil.containsInListCaseInsensitive(sExistingPickListValue, slPickListValues))
				{
					bValid = true;
					break;
				}
			}

		}
		else
		{
			bValid = true;
		}
		
		context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : filterBasedOnPickListValues << bInclude = "+bValid);

		return bValid;
	}


	/**
	 * Method to filter additional criteria after all list of parts found
	 * @param objectMap
	 * @param slInputIntendedMarkets
	 * @param bIncludeAll 
	 * @return
	 */
	public static boolean filterBasedOnAdditionalCriteriaForApplicableParts(Map objectMap, StringList slInputIntendedMarkets, boolean bIncludeAll)
	{
		boolean bApplicableProduct = false;
		StringList slPartIntendedMarkets;

		if(bIncludeAll)
		{
			return true;			
		}

		slPartIntendedMarkets = pgApolloCommonUtil.getStringListMultiValue(objectMap.get("from["+pgApolloConstants.RELATIONSHIP_PG_INTENDED_MARKETS+"].to.name"));	

		if(null ==slInputIntendedMarkets || slInputIntendedMarkets.isEmpty())
		{
			bApplicableProduct = true;
		}
		else if(null !=slPartIntendedMarkets && !slPartIntendedMarkets.isEmpty())
		{
			for(String sPartMarket : slPartIntendedMarkets)
			{
				if(pgApolloCommonUtil.containsInListCaseInsensitive(sPartMarket, slInputIntendedMarkets))
				{
					bApplicableProduct = true;
					break;
				}
			}

		}
		else
		{
			bApplicableProduct = true;
		}
				
		return bApplicableProduct;
	}

	/**
	 * Method to get Criteria associated Char information
	 * @param context
	 * @param sObjectId
	 * @param slCharacteristicAdditionalSelectable
	 * @param includePartAssociated
	 * @return
	 * @throws MatrixException
	 */
	public static Map getCriteriaAssociatedCharacteristics(Context context, String sObjectId, StringList slCharacteristicAdditionalSelectable, boolean includePartAssociated) throws MatrixException 
	{

		String sSelectCriteriaOutput = new StringBuilder(pgApolloConstants.CONSTANT_STRING_SELECT_FROM).append(pgApolloConstants.RELATIONSHIP_CRITERIA_OUTPUT).append(pgApolloConstants.CONSTANT_STRING_SELECT_TOID).toString();
		String sSelectCriteriaOutputIsMandatory = new StringBuilder(pgApolloConstants.CONSTANT_STRING_SELECT_FROM).append(pgApolloConstants.RELATIONSHIP_CRITERIA_OUTPUT).append("].").append(pgApolloConstants.SELECT_ATTRIBUTE_MANDATORY_CHARACTERISTIC).toString();

		StringList slCriteriaSelect = new StringList();
		slCriteriaSelect.add(sSelectCriteriaOutput);
		slCriteriaSelect.add(sSelectCriteriaOutputIsMandatory);
		
		StringList slCriteriaMultiValueSelect = new StringList();
		slCriteriaMultiValueSelect.addAll(slCriteriaSelect);

		DomainObject domCriteriaObj = DomainObject.newInstance(context, sObjectId);		
		Map mapCriteriaCM = domCriteriaObj.getInfo(context, slCriteriaSelect, slCriteriaMultiValueSelect);
		StringList slCharacteristicMasterIds = pgApolloCommonUtil.getStringListMultiValue(mapCriteriaCM.get(sSelectCriteriaOutput));
		StringList slCharacteristicMasterIsMandatory = pgApolloCommonUtil.getStringListMultiValue(mapCriteriaCM.get(sSelectCriteriaOutputIsMandatory));

		context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : getCriteriaAssociatedCharacteristics slCharacteristicMasterIds = "+slCharacteristicMasterIds);

		Map mapCriteriaOutput = new HashMap();
		Map mapDesignDrivenCMInfo = new HashMap();
		Map mapNonDesignDrivenCMInfo = new HashMap();

		if(null != slCharacteristicMasterIds && !slCharacteristicMasterIds.isEmpty())
		{		
			Map mapCMMandatory = new HashMap();
			String sCMId;
			String sCMIsMandatory;

			for(int n=0; n<slCharacteristicMasterIds.size(); n++)
			{				
				sCMId = slCharacteristicMasterIds.get(n);
				sCMIsMandatory = slCharacteristicMasterIsMandatory.get(n);
				mapCMMandatory.put(sCMId, sCMIsMandatory);
			}

			StringList slCMSelects = new StringList();
			slCMSelects.add(DomainConstants.SELECT_ID);
			slCMSelects.add(DomainConstants.SELECT_NAME);
			slCMSelects.add(DomainConstants.SELECT_CURRENT);
			slCMSelects.add("from["+pgApolloConstants.RELATIONSHIP_PARAMETER_AGGREGATION+"].to["+pgApolloConstants.TYPE_PLM_PARAMETER+"].id"); 

			StringBuilder sbAssociatedPartIdSelect = new StringBuilder();				
			sbAssociatedPartIdSelect.append("from[").append(pgApolloConstants.RELATIONSHIP_DERIVED_CHARACTERISTIC).append("].to.relationship[").append(pgApolloConstants.RELATIONSHIP_PARAMETER_AGGREGATION).append("].from.id");

			StringBuilder sbAssociatedPartRelAttributeSelect = new StringBuilder();				
			sbAssociatedPartRelAttributeSelect.append("from[").append(pgApolloConstants.RELATIONSHIP_DERIVED_CHARACTERISTIC).append("].").append(pgApolloConstants.SELECT_ATTRIBUTE_EVALUATED_CRITERIA);

			String sAssociatedPartIdSelect = sbAssociatedPartIdSelect.toString();
			String sAssociatedPartRelAttributeSelect = sbAssociatedPartRelAttributeSelect.toString();


			if(includePartAssociated)			
			{				
				slCMSelects.add(sAssociatedPartIdSelect);
				slCMSelects.add(sAssociatedPartRelAttributeSelect);
			}
			
			MapList mlCMCharacteristicsInfo = DomainObject.getInfo(context, slCharacteristicMasterIds.toArray(new String[slCharacteristicMasterIds.size()]), slCMSelects); 

			context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : getCriteriaAssociatedCharacteristics mlCMCharacteristicsInfo = "+mlCMCharacteristicsInfo);

			Map mapCMCharInfo;
			Map mapCharCMIdInfo = new HashMap();
			Map mapCharCMCurrentInfo = new HashMap();
			Map mapCharCMNameInfo = new HashMap();
			StringList slCharIds;
			String sCMCurrent;
			String sCMName;
			StringList slAllCharIds = new StringList();
			Map mapCMPartInfo = new HashMap();

			StringList slValidPartIdList;
			StringList slPartIdList;
			StringList slPartEvaluatedAttributeList;
			String sPartId;
			String sPartEvaluateAttribute;

			for(Object objectCMCharInfo : mlCMCharacteristicsInfo)
			{
				slValidPartIdList = new StringList();
				mapCMCharInfo = (Map)objectCMCharInfo;
				sCMId = (String)mapCMCharInfo.get(DomainConstants.SELECT_ID);
				sCMCurrent = (String)mapCMCharInfo.get(DomainConstants.SELECT_CURRENT);
				sCMName = (String)mapCMCharInfo.get(DomainConstants.SELECT_NAME);

				slCharIds = pgApolloCommonUtil.getStringListMultiValue(mapCMCharInfo.get("from["+pgApolloConstants.RELATIONSHIP_PARAMETER_AGGREGATION+"].to["+pgApolloConstants.TYPE_PLM_PARAMETER+"].id"));	//				
				for(String sLocalCharId : slCharIds)
				{
					mapCharCMIdInfo.put(sLocalCharId, sCMId);
					mapCharCMCurrentInfo.put(sLocalCharId, sCMCurrent);
					mapCharCMNameInfo.put(sLocalCharId, sCMName);
				}				
				if(includePartAssociated)
				{
					slPartIdList = pgApolloCommonUtil.getStringListMultiValue(mapCMPartInfo.get(sAssociatedPartIdSelect));
					slPartEvaluatedAttributeList = pgApolloCommonUtil.getStringListMultiValue(mapCMPartInfo.get(sAssociatedPartRelAttributeSelect));
					if(null != slPartIdList && !slPartIdList.isEmpty())
					{	
						for(int n=0; n<slPartIdList.size(); n++)
						{
							sPartEvaluateAttribute = slPartEvaluatedAttributeList.get(n);
							if(UIUtil.isNotNullAndNotEmpty(sPartEvaluateAttribute))
							{
								sPartId = slPartIdList.get(n);
								if(UIUtil.isNotNullAndNotEmpty(sPartId))
								{
									slValidPartIdList.add(sPartId);
								}
							}

						}			
					}					
					mapCMPartInfo.put(sCMId, slValidPartIdList);
				}
				slAllCharIds.addAll(slCharIds);
			}		

			String sCharId;
			Map mapChar;
			String sUniqueKey;
			String sIsDesignDriven;			

			StringList slCharSelectable = new StringList();
			slCharSelectable.add(DomainConstants.SELECT_ID);
			slCharSelectable.add(DomainConstants.SELECT_ATTRIBUTE_TITLE);
			slCharSelectable.add(pgApolloConstants.SELECT_ATTRIBUTE_CHARACTERISTIC_CATEGORY);
			slCharSelectable.add(pgApolloConstants.SELECT_ATTRIBUTE_PG_CATEGORYSPECIFICS);
			slCharSelectable.add(pgV3Constants.SELECT_ATTRIBUTE_PGCHARACTERISTICSPECIFICS);			
			slCharSelectable.add(pgApolloConstants.SELECT_ATTRIBUTE_REPORT_TYPE);
			slCharSelectable.add(pgApolloConstants.SELECT_ATTRIBUTE_PG_ISDESIGNDRIVEN);

			if(!slCharacteristicAdditionalSelectable.isEmpty())
			{
				slCharSelectable.addAll(slCharacteristicAdditionalSelectable);
			}

			context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : getCriteriaAssociatedCharacteristics slAllCharIds = "+slAllCharIds);

			MapList mlCharacteristicsInfo = DomainObject.getInfo(context, slAllCharIds.toArray(new String[slAllCharIds.size()]), slCharSelectable);			


			if(!mlCharacteristicsInfo.isEmpty())
			{
				for(int i=0; i<mlCharacteristicsInfo.size(); i++)
				{
					mapChar = (Map)mlCharacteristicsInfo.get(i);
					sCharId = (String)mapChar.get(DomainConstants.SELECT_ID);					
					sIsDesignDriven = (String)mapChar.get(pgApolloConstants.SELECT_ATTRIBUTE_PG_ISDESIGNDRIVEN);
					sUniqueKey = (String)mapCharCMNameInfo.get(sCharId);
					sCMId = (String)mapCharCMIdInfo.get(sCharId);	
					sCMCurrent = (String)mapCharCMCurrentInfo.get(sCharId);
					sCMIsMandatory = (String)mapCMMandatory.get(sCMId);					
					mapChar.put(pgApolloConstants.TYPE_CHARACTERISTICSMASTER, sCMId);
					mapChar.put(DomainConstants.SELECT_CURRENT, sCMCurrent);
					mapChar.put(pgApolloConstants.ATTRIBUTE_MANDATORY_CHARACTERISTIC, sCMIsMandatory);

					if(includePartAssociated)
					{
						slValidPartIdList = (StringList)mapCMPartInfo.get(sCMId);
						mapChar.put(DomainConstants.TYPE_PART, slValidPartIdList);
					}

					if(pgApolloConstants.RANGE_VALUE_PG_ISDESIGNDRIVEN_NO.equalsIgnoreCase(sIsDesignDriven))
					{
						mapNonDesignDrivenCMInfo.put(sUniqueKey, mapChar);						
					}
					else
					{
						mapDesignDrivenCMInfo.put(sUniqueKey, mapChar);
					}

				}				
			}


		}

		mapCriteriaOutput.put(pgApolloConstants.RANGE_VALUE_PG_ISDESIGNDRIVEN_YES, mapDesignDrivenCMInfo);
		mapCriteriaOutput.put(pgApolloConstants.RANGE_VALUE_PG_ISDESIGNDRIVEN_NO, mapNonDesignDrivenCMInfo);
		

		return mapCriteriaOutput;
	}



	/**
	 * Method to find Characteristics Info
	 * @param context
	 * @param domCharObj
	 * @param enoChar
	 * @param slChgCharSelectList
	 * @return
	 * @throws Exception
	 */
	public static Map getCharacteristicsInfo(Context context, DomainObject domCharObj, ENOICharacteristic enoChar, StringList slChgCharSelectList) throws  Exception 
	{
		Map mapLocalCharMap = new HashMap();
		String sCharId;
		String strRelatedTestMethodId = "from["+ pgApolloConstants.RELATIONSHIP_CHARACTERISTIC_TEST_METHOD +"].to.id";
		String strRelatedTMRDId = "from["+ DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT +"].to.id";

		StringList slCharSelectList = new StringList();
		slCharSelectList.addAll(slChgCharSelectList);
		slCharSelectList.add(DomainConstants.SELECT_ID);
		slCharSelectList.add(strRelatedTestMethodId);
		slCharSelectList.add(strRelatedTMRDId);

		if(slCharSelectList.contains(pgApolloConstants.SELECT_ATTRIBUTE_PG_ISDESIGNDRIVEN))
		{
			slCharSelectList.remove(pgApolloConstants.SELECT_ATTRIBUTE_PG_ISDESIGNDRIVEN);
		}
		
		StringList slCharMultiValueList = new StringList();
		slCharMultiValueList.add(strRelatedTestMethodId);
		slCharMultiValueList.add(strRelatedTMRDId);

		Map mapChar = domCharObj.getInfo(context, slCharSelectList, slCharMultiValueList);

		sCharId = (String)mapChar.get(DomainConstants.SELECT_ID);	
		
		String sLowerSL  = enoChar.getLowerSpecificationLimit(context);
		String sLowerRoutineLimit = enoChar.getLowerRoutineReleaseLimit(context);
		String sLowerTarget = enoChar.getMinimalValue(context);
		String sTarget = enoChar.getNominalValue(context);
		String sUpperTarget = enoChar.getMaximalValue(context);
		String sUpperRoutineLimit = enoChar.getUpperRoutineReleaseLimit(context);
		String sUpperSL = enoChar.getUpperSpecificationLimit(context);	


		mapLocalCharMap.put(pgApolloConstants.ATTRIBUTE_CHARACTERISTICSLOWERSPECIFICATIONLIMIT, sLowerSL);
		mapLocalCharMap.put(pgApolloConstants.ATTRIBUTE_CHARACTERISTICSLOWERROUTINERELEASELIMIT, sLowerRoutineLimit);
		mapLocalCharMap.put(pgApolloConstants.STRING_MINVALUE, sLowerTarget);
		mapLocalCharMap.put(pgApolloConstants.STRING_PARAMETERVALUE, sTarget);
		mapLocalCharMap.put(pgApolloConstants.STRING_MAXVALUE, sUpperTarget);
		mapLocalCharMap.put(pgApolloConstants.ATTRIBUTE_CHARACTERISTICSUPPERROUTINERELEASELIMIT, sUpperRoutineLimit);
		mapLocalCharMap.put(pgApolloConstants.ATTRIBUTE_CHARACTERISTICSUPPERSPECIFICATIONLIMIT, sUpperSL);

		StringList slTestMethodId = new StringList();
		StringList slTMRDId = new StringList();
		String sAttributeNameSelect;
		String sAttributeValue;
		if(mapChar.containsKey(strRelatedTestMethodId))
		{					
			slTestMethodId = pgApolloCommonUtil.getStringListFromObject(mapChar.get(strRelatedTestMethodId));
			slTestMethodId.sort();
			mapChar.remove(strRelatedTestMethodId);
		}			
		mapLocalCharMap.put(pgApolloConstants.KEY_TEST_METHOD_ID, slTestMethodId.join(pgApolloConstants.CONSTANT_STRING_PIPE));
		if(mapChar.containsKey(strRelatedTMRDId))
		{
			slTMRDId = pgApolloCommonUtil.getStringListFromObject(mapChar.get(strRelatedTMRDId));
			slTMRDId.sort();
			mapChar.remove(strRelatedTMRDId);
		}			
		mapLocalCharMap.put(pgApolloConstants.KEY_TMRD_ID, slTMRDId.join(pgApolloConstants.CONSTANT_STRING_PIPE));			
		for(int j=0; j<slChgCharSelectList.size(); j++)
		{
			sAttributeNameSelect = slChgCharSelectList.get(j);
			if(mapChar.containsKey(sAttributeNameSelect))
			{
				sAttributeValue = (String)mapChar.get(sAttributeNameSelect);
				mapLocalCharMap.put(sAttributeNameSelect, sAttributeValue);
			}				
		}			
		String sDimension = (UIUtil.isNotNullAndNotEmpty(enoChar.getDimension()))?enoChar.getDimension():DomainConstants.EMPTY_STRING ;
		String sDisplayUnit = enoChar.getDisplayUnit();			
		String sDimensionName = DomainConstants.EMPTY_STRING;			
		if(UIUtil.isNotNullAndNotEmpty(sDimension))
		{
			sDimensionName = ParameterInterfacesServices.getDimensionNLS(context, sDimension);					
			if(UIUtil.isNullOrEmpty(sDimensionName))
			{
				sDimensionName = DomainConstants.EMPTY_STRING;
			}
		}			
		mapLocalCharMap.put(pgApolloConstants.STR_CHAR_DIMENSION, sDimensionName);			
		mapLocalCharMap.put(CharacteristicMasterConstants.PARAM_DISPLAY_UNIT, sDisplayUnit);						
		mapLocalCharMap.put(DomainConstants.SELECT_ID, sCharId);

		return mapLocalCharMap;
	}
	
	
	
	/**
	 * Method to get Part Info List
	 * @param context
	 * @param slPartIdList
	 * @return
	 * @throws FrameworkException
	 */
	public static MapList getPartInfo(Context context, StringList slPartIdList) throws FrameworkException {
		
		MapList mlReturnList = new MapList();
		
		StringList slPartSelects = new StringList();
		slPartSelects.add(DomainConstants.SELECT_ID);
		slPartSelects.add(DomainConstants.SELECT_NAME);
		slPartSelects.add(DomainConstants.SELECT_CURRENT);
		slPartSelects.add(DomainConstants.SELECT_OWNER);
		slPartSelects.add(DomainConstants.SELECT_REVISION);
		slPartSelects.add(DomainConstants.SELECT_ATTRIBUTE_TITLE);
		slPartSelects.add(DomainConstants.SELECT_DESCRIPTION);
		slPartSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_RELEASE_PHASE);
		slPartSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PG_LIFECYCLESTATUS);
		slPartSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PG_DSMPRODUCTSIZE);
		slPartSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_ENTERPRISEPART_PGBUSINESSAREA);
		slPartSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_ENTERPRISEPART_PGPRODUCTCATEGORYPLATFORM);
		slPartSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_ENTERPRISEPART_PGPRODUCTTECHNOLOGYPLATFORM);
		slPartSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_ENTERPRISEPART_PGPRODUCTTECHNOLOGYCHASSIS);
		slPartSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PG_DSMPRODUCTSIZE);
		slPartSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_ENTERPRISEPART_PGLPDREGION);
		slPartSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_ENTERPRISEPART_PGLPDSUBREGION);

		MapList mlPartInfo = DomainObject.getInfo(context, slPartIdList.toArray(new String[slPartIdList.size()]), slPartSelects);
		
		if(null != mlPartInfo && !mlPartInfo.isEmpty())
		{
			Map mapLocal;
			Map mapPart;
			
			StringList slPartBusinessArea;
			StringList slPartPrductCategoryPlatform;
			StringList slPartPrductTechnologyPlatform;
			StringList slPartPrductTechnologyChassis;
			StringList slPartSize;
			StringList slPartRegion;
			StringList slPartSubRegion;
			
			String sPartBusinessArea;
			String sPartPrductCategoryPlatform;
			String sPartPrductTechnologyPlatform;
			String sPartPrductTechnologyChassis;
			String sPartSize;
			String sPartRegion;
			String sPartSubRegion;
			String sPartTitle;
			String sPartDescription;
			String sPartId;
			String sPartName;
			String sPartNameRevision;
			String sPartOwner;
			String sPartCurrent;

			
			String sPartReleasePhase;
			String sPartManufacturingStatus;

			
			for(Object objectMap : mlPartInfo)
			{
				mapLocal = (Map)objectMap;	
				
				mapPart = new HashMap();				
				
				sPartId = (String)mapLocal.get(DomainConstants.SELECT_ID);
				sPartName = (String)mapLocal.get(DomainConstants.SELECT_NAME);
				sPartNameRevision = (String)mapLocal.get(DomainConstants.SELECT_REVISION);
				sPartTitle = (String)mapLocal.get(DomainConstants.SELECT_ATTRIBUTE_TITLE);
				sPartDescription = (String)mapLocal.get(DomainConstants.SELECT_DESCRIPTION);
				sPartOwner = (String)mapLocal.get(DomainConstants.SELECT_OWNER);
				sPartCurrent = (String)mapLocal.get(DomainConstants.SELECT_CURRENT);

				mapPart.put(DomainConstants.SELECT_ID, sPartId);
				mapPart.put(DomainConstants.SELECT_NAME, sPartName);
				mapPart.put(DomainConstants.SELECT_REVISION, sPartNameRevision);
				mapPart.put(DomainConstants.ATTRIBUTE_TITLE, sPartTitle);
				mapPart.put(DomainConstants.SELECT_DESCRIPTION, sPartDescription);
				mapPart.put(DomainConstants.SELECT_OWNER, sPartOwner);
				mapPart.put(DomainConstants.SELECT_CURRENT, sPartCurrent);

				slPartBusinessArea = pgApolloCommonUtil.getStringListMultiValue(mapLocal.get(pgApolloConstants.SELECT_ATTRIBUTE_ENTERPRISEPART_PGBUSINESSAREA));
				slPartPrductCategoryPlatform = pgApolloCommonUtil.getStringListMultiValue(mapLocal.get(pgApolloConstants.SELECT_ATTRIBUTE_ENTERPRISEPART_PGPRODUCTCATEGORYPLATFORM));
				slPartPrductTechnologyPlatform = pgApolloCommonUtil.getStringListMultiValue(mapLocal.get(pgApolloConstants.SELECT_ATTRIBUTE_ENTERPRISEPART_PGPRODUCTTECHNOLOGYPLATFORM));
				slPartPrductTechnologyChassis = pgApolloCommonUtil.getStringListMultiValue(mapLocal.get(pgApolloConstants.SELECT_ATTRIBUTE_ENTERPRISEPART_PGPRODUCTTECHNOLOGYCHASSIS));
				slPartSize = pgApolloCommonUtil.getStringListMultiValue(mapLocal.get(pgApolloConstants.SELECT_ATTRIBUTE_PG_DSMPRODUCTSIZE));
				slPartRegion = pgApolloCommonUtil.getStringListMultiValue(mapLocal.get(pgApolloConstants.SELECT_ATTRIBUTE_ENTERPRISEPART_PGLPDREGION));
				slPartSubRegion = pgApolloCommonUtil.getStringListMultiValue(mapLocal.get(pgApolloConstants.SELECT_ATTRIBUTE_ENTERPRISEPART_PGLPDSUBREGION));

				sPartReleasePhase = (String)(mapLocal.get(pgApolloConstants.SELECT_ATTRIBUTE_RELEASE_PHASE));
				sPartManufacturingStatus = (String)(mapLocal.get(pgApolloConstants.SELECT_ATTRIBUTE_PG_LIFECYCLESTATUS));
				
				sPartBusinessArea = StringUtil.join(slPartBusinessArea, pgApolloConstants.CONSTANT_STRING_COMMA);
				sPartPrductCategoryPlatform = StringUtil.join(slPartPrductCategoryPlatform, pgApolloConstants.CONSTANT_STRING_COMMA);
				sPartPrductTechnologyPlatform = StringUtil.join(slPartPrductTechnologyPlatform, pgApolloConstants.CONSTANT_STRING_COMMA);
				sPartPrductTechnologyChassis = StringUtil.join(slPartPrductTechnologyChassis, pgApolloConstants.CONSTANT_STRING_COMMA);
				sPartSize = StringUtil.join(slPartSize, pgApolloConstants.CONSTANT_STRING_COMMA);
				sPartRegion = StringUtil.join(slPartRegion, pgApolloConstants.CONSTANT_STRING_COMMA);
				sPartSubRegion = StringUtil.join(slPartSubRegion, pgApolloConstants.CONSTANT_STRING_COMMA);

				mapPart.put(pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGBUSINESSAREA, sPartBusinessArea);
				mapPart.put(pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGPRODUCTCATEGORYPLATFORM, sPartPrductCategoryPlatform);
				mapPart.put(pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGPRODUCTTECHNOLOGYPLATFORM, sPartPrductTechnologyPlatform);
				mapPart.put(pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGPRODUCTTECHNOLOGYCHASSIS, sPartPrductTechnologyChassis);
				mapPart.put(pgApolloConstants.ATTRIBUTE_PG_DSMPRODUCTSIZE, sPartSize);
				mapPart.put(pgApolloConstants.STR_RELEASE_PHASE, sPartReleasePhase);
				mapPart.put(pgApolloConstants.ATTRIBUTE_PG_LIFECYCLESTATUS, sPartManufacturingStatus);		
				mapPart.put(pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGLPDREGION, sPartRegion);		
				mapPart.put(pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGLPDSUBREGION, sPartSubRegion);		

				mlReturnList.add(mapPart);
				
			}
			
			
		}
		
		
		return mlReturnList;
	}

	
	/**
	 * Method to Process Background Process or Associated Parts manually based on mode
	 * @param context
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public String processBackgroundProcessManually(Context context, String[] args) throws Exception
	{
		HashMap programMap = (HashMap) JPO.unpackArgs(args);		
		String sMode = (String)programMap.get(pgApolloConstants.STR_MODE);		
		String sReturn = DomainConstants.EMPTY_STRING;		
		if(pgApolloConstants.STR_MODE_BACKGROUNDPROCESS_RESUBMIT.equalsIgnoreCase(sMode))
		{
			sReturn = resubmitBackgroundProcess(context, programMap);
		}
		else if(pgApolloConstants.STR_MODE_BACKGROUNDPROCESS_CANCEL.equalsIgnoreCase(sMode))
		{
			sReturn = cancelBackgroundProcess(context, programMap);
		}	
		else if(pgApolloConstants.STR_MODE_BACKGROUNDPROCESS_REMOVE.equalsIgnoreCase(sMode))
		{
			sReturn = removeAssociatedObjectsFromBackgroundProcess(context, programMap);
		}	
		return sReturn;
	}
	
	
	/**
	 * Method to resubmit background Process 
	 * @param context
	 * @param programMap
	 * @return
	 * @throws MatrixException 
	 */
	private String resubmitBackgroundProcess(Context context, HashMap programMap) throws MatrixException 
	{
		context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : resubmitBackgroundProcess >>  programMap = "+programMap);
		String sReturn = DomainConstants.EMPTY_STRING;		
		
		try
		{
			String sBackgroundProcessObjectId = (String)programMap.get("objectId");
			if(UIUtil.isNotNullAndNotEmpty(sBackgroundProcessObjectId))
			{
				DomainObject domBackgroundProcess = DomainObject.newInstance(context, sBackgroundProcessObjectId);
				domBackgroundProcess.setAttributeValue(context, pgApolloConstants.ATTRIBUTE_ERROR_MESSAGE, DomainConstants.EMPTY_STRING);
				setBackgroundProcessState(context, sBackgroundProcessObjectId, pgApolloConstants.STATE_ACTIVE);			
			}	
		}
		catch (Exception ex)
		{
			loggerTrace.error(ex.getMessage() ,ex);	
			sReturn = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(ex.getLocalizedMessage()).toString();
		}
		
		context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : resubmitBackgroundProcess <<  sReturn = "+sReturn);
		return sReturn;
	}
	
	
	/**
	 * Method to cancel background Process
	 * @param context
	 * @param programMap
	 * @return
	 * @throws MatrixException 
	 */
	private String cancelBackgroundProcess(Context context, HashMap programMap) throws MatrixException
	{
		context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : cancelBackgroundProcess >>  programMap = "+programMap);
		String sReturn = DomainConstants.EMPTY_STRING;
		
		try
		{
			String sBackgroundProcessObjectId = (String)programMap.get("objectId");
			if(UIUtil.isNotNullAndNotEmpty(sBackgroundProcessObjectId))
			{
				DomainObject domBackgroundProcess = DomainObject.newInstance(context, sBackgroundProcessObjectId);
				domBackgroundProcess.setAttributeValue(context, pgApolloConstants.ATTRIBUTE_ERROR_MESSAGE, pgApolloConstants.STR_CANCELLED);
				removeAssociatedObjects(context, sBackgroundProcessObjectId, programMap);			
				setBackgroundProcessState(context, sBackgroundProcessObjectId, pgApolloConstants.STATE_ARCHIVED);			
			}
		}
		catch (Exception ex)
		{
			loggerTrace.error(ex.getMessage() ,ex);	
			sReturn = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(ex.getLocalizedMessage()).toString();
		}
		
		context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : cancelBackgroundProcess <<  sReturn = "+sReturn);
		return sReturn;
	}




	/**
	 * Method to remove associated objects
	 * @param context
	 * @param programMap
	 * @return
	 * @throws MatrixException 
	 */
	private String removeAssociatedObjectsFromBackgroundProcess(Context context, HashMap programMap) throws MatrixException
	{
		context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : removeAssociatedObjectsFromBackgroundProcess >>  programMap = "+programMap);
		String sReturn = DomainConstants.EMPTY_STRING;
		
		try
		{
			String sBackgroundProcessObjectId = (String)programMap.get("objectId");
			if(UIUtil.isNotNullAndNotEmpty(sBackgroundProcessObjectId))
			{
				removeAssociatedObjects(context, sBackgroundProcessObjectId, programMap);			
			}	
		}
		catch (Exception ex)
		{
			loggerTrace.error(ex.getMessage() ,ex);	
			sReturn = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(ex.getLocalizedMessage()).toString();
		}
		
		context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : removeAssociatedObjectsFromBackgroundProcess <<  sReturn = "+sReturn);
		return sReturn;
	}
	
	
	/**
	 * Method to set Background Process State
	 * @param context
	 * @param sBackgroundProcessObjectId
	 * @param sTargetState
	 * @throws MatrixException 
	 */
	public void setBackgroundProcessState(Context context, String sObjectId, String sTargetState) throws MatrixException 
	{
		boolean isContextPushed = false;
		context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : setBackgroundProcessState >>  sObjectId = "+sObjectId+" sTargetState = "+sTargetState);
		try
		{

			//Push context is needed as User will not have access to promote or demote background process state
			ContextUtil.pushContext(context, pgApolloConstants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
			isContextPushed = true;

			if(UIUtil.isNotNullAndNotEmpty(sObjectId))
			{
				DomainObject domObject = DomainObject.newInstance(context, sObjectId);
				domObject.setState(context, sTargetState);
			}
		}
		catch (Exception ex)
		{
			loggerTrace.error(ex.getMessage() ,ex);
			throw ex;
		}
		finally
		{
			if(isContextPushed)
			{
				ContextUtil.popContext(context);
			}
		}
		context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : setBackgroundProcessState <<");
	}
	
	
	/**
	 * Method to remove associated objects
	 * @param context
	 * @param sBackgroundProcessObjectId
	 * @param programMap 
	 * @throws MatrixException
	 */
	public void removeAssociatedObjects(Context context, String sBackgroundProcessObjectId, HashMap programMap)	throws MatrixException 
	{
		String sMode = (String)programMap.get("mode");
		
		boolean isContextPushed = false;		
		try
		{
		//Context user won't always have access to set attributes on Released Part. So push context is needed here.
		ContextUtil.pushContext(context, pgApolloConstants.PERSON_USER_AGENT, null, context.getVault().getName());
		isContextPushed = true;

		context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics. : removeAssociatedObjects  >> sMode = "+sMode);

		if(pgApolloConstants.STR_MODE_BACKGROUNDPROCESS_REMOVE.equalsIgnoreCase(sMode))
		{
			String sSelectedRowIds = (String)programMap.get("emxTableRowId");		
			StringList slSelectedRowIds = new StringList();

			if(UIUtil.isNotNullAndNotEmpty(sSelectedRowIds))
			{
				slSelectedRowIds = StringUtil.split(sSelectedRowIds, pgApolloConstants.CONSTANT_STRING_COMMA);
			}

			slSelectedRowIds.sort();

			context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics : removeAssociatedObjects slSelectedRowIds = "+slSelectedRowIds);		

			DomainObject domBackgroundProcess = DomainObject.newInstance(context, sBackgroundProcessObjectId);

			String sSelectBackgroundProcessRelatedObjectIds = "from["+pgApolloConstants.RELATIONSHIP_PGBACKGROUNDPROCESSRELATEDOBJECTS+"].to.id";

			StringList slObjectSelects = new StringList();			
			slObjectSelects.add(sSelectBackgroundProcessRelatedObjectIds);
			slObjectSelects.add(DomainConstants.SELECT_ATTRIBUTE_TITLE);

			StringList slMultiValueObjectSelects = new StringList();
			slMultiValueObjectSelects.add(sSelectBackgroundProcessRelatedObjectIds);

			Map mapBackgroundProcess = domBackgroundProcess.getInfo(context, slObjectSelects, slMultiValueObjectSelects);

			context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics. : removeAssociatedObjects  mapBackgroundProcess = "+mapBackgroundProcess);

			StringList slAssociatedObjectIds = pgApolloCommonUtil.getStringListMultiValue(mapBackgroundProcess.get(sSelectBackgroundProcessRelatedObjectIds));
			
			String sBackgroundJobTitle = (String)mapBackgroundProcess.get((DomainConstants.SELECT_ATTRIBUTE_TITLE));

			slAssociatedObjectIds.sort();

			BusinessObject boToBeDisconnected = null;
			RelationshipType relTypeBGP = new RelationshipType(pgApolloConstants.RELATIONSHIP_PGBACKGROUNDPROCESSRELATEDOBJECTS);

			DomainObject domObject;

			if(!slSelectedRowIds.isEmpty())
			{
					
				for(String sSelectedRowId : slSelectedRowIds)
				{
					boToBeDisconnected = new BusinessObject(sSelectedRowId);
					domBackgroundProcess.disconnect(context, relTypeBGP, true, boToBeDisconnected);		

					if(pgApolloConstants.STR_TITLE_MASSUPDATECHARACTERISTICS.equalsIgnoreCase(sBackgroundJobTitle))
					{
						domObject = DomainObject.newInstance(context, boToBeDisconnected);
						domObject.setAttributeValue(context, pgApolloConstants.ATTRIBUTE_PGLPDPERFCHARACTERISTICSUPDATESTATUS, pgApolloConstants.RANGE_PGLPDPERFCHARACTERISTICSUPDATESTATUS_PENDING);

					}
					
					context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics :  Disconnection of = "+sSelectedRowId+"  with sBackgroundProcessObjectId = "+sBackgroundProcessObjectId);
				}

				if(!slAssociatedObjectIds.isEmpty() && slAssociatedObjectIds.equals(slSelectedRowIds))
				{
					domBackgroundProcess.setAttributeValue(context, pgApolloConstants.ATTRIBUTE_ERROR_MESSAGE, pgApolloConstants.STR_CANCELLED);
					setBackgroundProcessState(context, sBackgroundProcessObjectId, pgApolloConstants.STATE_ARCHIVED);			
				}

			}
		}
		else if(pgApolloConstants.STR_MODE_BACKGROUNDPROCESS_CANCEL.equalsIgnoreCase(sMode))
		{

			DomainObject domBackgroundProcess = DomainObject.newInstance(context, sBackgroundProcessObjectId);
			
			StringList slObjectSelects = new StringList();			
			slObjectSelects.add(DomainConstants.SELECT_ATTRIBUTE_TITLE);

			Map mapBackgroundProcess = domBackgroundProcess.getInfo(context, slObjectSelects);

			context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics. : removeAssociatedObjects  mapBackgroundProcess = "+mapBackgroundProcess);

			String sBackgroundJobTitle = (String)mapBackgroundProcess.get((DomainConstants.SELECT_ATTRIBUTE_TITLE));


			StringList slBusSelects = new StringList();
			slBusSelects.add(DomainConstants.SELECT_ID);
			slBusSelects.add(pgApolloConstants.SELECT_NEXT_ID);

			StringList slRelSelects = new StringList();
			slRelSelects.add(DomainRelationship.SELECT_ID);


			MapList mlObjectList = domBackgroundProcess.getRelatedObjects(context,//context
																					pgApolloConstants.RELATIONSHIP_PGBACKGROUNDPROCESSRELATEDOBJECTS, // relationship pattern
																					DomainConstants.QUERY_WILDCARD, // type pattern
																					slBusSelects, // object selects
																					slRelSelects, // relationship selects
																					false, // to direction
																					true, // from direction
																					(short)1,// recursion level
																					DomainConstants.EMPTY_STRING, // object where clause
																					null, // relationship where clause
																					0);// objects Limit


			StringList slAssociatedObjectRelIds = new StringList();
			StringList slAssociatedObjectIds = new StringList();

			context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics. : removeAssociatedObjects  mlObjectList = "+mlObjectList);


			if(null!=mlObjectList && !mlObjectList.isEmpty())
			{
				Map mapObject;
				String sRelId;
				String sObjectId;
				String sNextId;

				for(Object object : mlObjectList)
				{
					mapObject = (Map)object;

					sObjectId = (String)mapObject.get(DomainConstants.SELECT_ID);
					sRelId = (String)mapObject.get(DomainRelationship.SELECT_ID);

					sNextId = (String)mapObject.get(pgApolloConstants.SELECT_NEXT_ID);

					slAssociatedObjectRelIds.add(sRelId);
					slAssociatedObjectIds.add(sObjectId);

					if(UIUtil.isNotNullAndNotEmpty(sNextId))
					{
						slAssociatedObjectIds.add(sNextId);
					}

				}
			}	
			
			context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics. : removeAssociatedObjects   slAssociatedObjectIds = "+slAssociatedObjectIds);


			if(!slAssociatedObjectRelIds.isEmpty())
			{
				DomainRelationship.disconnect(context, slAssociatedObjectRelIds.toArray(new String []{}));
			}

			DomainObject domObject;

			if(!slAssociatedObjectIds.isEmpty() && pgApolloConstants.STR_TITLE_MASSUPDATECHARACTERISTICS.equalsIgnoreCase(sBackgroundJobTitle))
			{
				for(String sAssociatedObjectId : slAssociatedObjectIds)
				{
					domObject = DomainObject.newInstance(context, sAssociatedObjectId);
					domObject.setAttributeValue(context, pgApolloConstants.ATTRIBUTE_PGLPDPERFCHARACTERISTICSUPDATESTATUS, pgApolloConstants.RANGE_PGLPDPERFCHARACTERISTICSUPDATESTATUS_PENDING);
				}
			}
		}		
		context.printTrace(pgApolloConstants.TRACE_LPD, "EvaluateCriteriaAndUpdateCharacteristics. : removeAssociatedObjects  << sMode = "+sMode);

		}		
		catch (Exception ex)
		{
			loggerTrace.error(ex.getMessage() ,ex);
			throw ex;
		}
		finally
		{
			if(isContextPushed)
			{
				ContextUtil.popContext(context);
			}
		}
	}
}
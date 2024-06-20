package com.pg.widgets.structuredats;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonObject;
import javax.ws.rs.core.Response;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ArrayList;
import com.matrixone.apps.engineering.Part;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.widgets.util.PGWidgetConstants;
import com.pg.widgets.util.PGWidgetUtil;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.cpn.util.BusinessUtil;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.MultiValueSelects;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.FrameworkUtil;

import matrix.util.MatrixException;
import matrix.util.Pattern;
import matrix.db.Context;
import matrix.util.StringList;

public class PGStructuredATSPerformanceUtil 
{
	
	String strLanguage = "en";
	
static final Logger logger = Logger.getLogger(PGStructuredATSPerformanceUtil.class.getName());
/**
	 * method called on Add Performance Characterstics WS 
	 * @param context
	 * @param mpRequestMap
	 * @return 
	 * @throws Exception
	 */	
	public String addPerfCharCopyToSATS(Context context, Map mpRequestMap) throws Exception 
	{
		JsonObjectBuilder output = Json.createObjectBuilder();
		String strReturn= null;
		String strSourcePCId = null;
		String strSATSId = null;
		Map mAttribute = null;
		String strValidate=null;
		ArrayList<Map<?, ?>> alConnection  = null;
		try {
			strSATSId = (String)mpRequestMap.get(DomainConstants.SELECT_ID);
			ArrayList<Map<?, ?>> alData = (ArrayList<Map<?, ?>>) mpRequestMap.get(PGStructuredATSConstants.KEY_DATA);
			 if (null != alData && !alData.isEmpty())
			{
				for (Map<?, ?> mData : alData) 
				{	
					strSourcePCId = (String)mData.get("sourceid");
					mAttribute = (Map) mData.get("attributes");
					alConnection = (ArrayList<Map<?, ?>>) mData.get("connections");
					updateATSRelations(context,strSourcePCId, strSATSId, mAttribute, alConnection, PGStructuredATSConstants.ACTION_ADD) ;
					PGStructuredATSValidateUtil pgStructuredATSValidateUtil = new PGStructuredATSValidateUtil();
					strValidate=pgStructuredATSValidateUtil.validatePerformanceChar(context,strSATSId);
					strReturn =PGStructuredATSConstants.VALUE_SUCCESS;
				}
			}	
		}
		catch (Exception excep) 
		{
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_PERFORMANCE_UTIL, excep);
			throw excep;
							
		}	
	return strReturn;
	}
/**
	 * method called on Remove Performance Characterstics WS
	 * @param context
	 * @param mpRequestMap
	 * @return 
	 * @throws Exception
	 */	
	public String removePerfCharCopyToSATS(Context context, String strJsonInput) throws Exception 
	{
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		String strReturn= null;
		String strSourcePCId = null;
		String strSATSId = null;
		String strValidate=null;
		try 
		{
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strJsonInput);
			strSATSId = jsonInputData.getString(DomainConstants.SELECT_ID);
			strSourcePCId = jsonInputData.getString(PGStructuredATSConstants.SOURCE_ID);
			if (strSourcePCId.contains(","))
			{
				StringList slSourceIdList = FrameworkUtil.split(strSourcePCId, ",");
				for (int iSize=0;iSize<slSourceIdList.size();iSize++)
				{
					updateATSRelations(context,slSourceIdList.get(iSize),strSATSId, null,null, PGStructuredATSConstants.ACTION_REMOVE);
				}
			} else {
				updateATSRelations(context,strSourcePCId,strSATSId, null,null, PGStructuredATSConstants.ACTION_REMOVE);
			}
			PGStructuredATSValidateUtil pgStructuredATSValidateUtil = new PGStructuredATSValidateUtil();
			strValidate=pgStructuredATSValidateUtil.validatePerformanceChar(context,strSATSId);
			jsonReturnObj.add(PGStructuredATSConstants.KEY_STATUS, PGStructuredATSConstants.VALUE_SUCCESS);
		}
		catch (Exception excep) 
		{
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_PERFORMANCE_UTIL, excep);
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
			return jsonReturnObj.build().toString();				
		}
	return jsonReturnObj.build().toString();
	}
/**
	 * method called on Modify Performance Characterstics WS
	 * @param context
	 * @param mpRequestMap
	 * @return 
	 * @throws Exception
	 */
	public String modifyPerfCharCopyToSATS(Context context, Map mpRequestMap) throws Exception {
		
		String strSATSId = DomainConstants.EMPTY_STRING;
		String strTargetId = DomainConstants.EMPTY_STRING;
		String strSourceId = DomainConstants.EMPTY_STRING;
		String strReturn = DomainConstants.EMPTY_STRING;
		String strCtxFOPId = DomainConstants.EMPTY_STRING;
		Map mAttribute = null;
		String strValidate = null;
		ArrayList<Map<?, ?>> alConnection = null;
		try {
			strSATSId = (String) mpRequestMap.get(DomainConstants.SELECT_ID);
			ArrayList<Map<?, ?>> alData = (ArrayList<Map<?, ?>>) mpRequestMap.get(PGStructuredATSConstants.KEY_DATA);
			if (null != alData && !alData.isEmpty()) {
				for (Map<?, ?> mData : alData) {
					strCtxFOPId = (String) mData.get(PGStructuredATSConstants.KEY_CTXFOP_ID); // FOP/APP
					strSourceId = (String) mData.get(PGStructuredATSConstants.SOURCE_ID); // sourceid FOP
					strTargetId = (String) mData.get(PGStructuredATSConstants.KEY_TARGET_ID);
					mAttribute = (Map) mData.get(PGStructuredATSConstants.KEY_ATTRIBUTES);
					String strOperation = (String) mData.get(PGStructuredATSConstants.VALUE_OPERATION);
					alConnection = (ArrayList<Map<?, ?>>) mData.get(PGStructuredATSConstants.KEY_CONNECTIONS);
					if (PGStructuredATSConstants.ACTION_MODIFY.equalsIgnoreCase(strOperation)&& UIUtil.isNotNullAndNotEmpty(strTargetId)) {
						updateOrCreateTargetPC(context, strSATSId, strSourceId, strTargetId, mAttribute, alConnection);
						PGStructuredATSValidateUtil pgStructuredATSValidateUtil = new PGStructuredATSValidateUtil();
						strValidate = pgStructuredATSValidateUtil.validatePerformanceChar(context, strSATSId);
						strReturn = PGStructuredATSConstants.VALUE_SUCCESS;
					} else if (PGStructuredATSConstants.ACTION_CAP_ADJUST.equalsIgnoreCase(strOperation) && UIUtil.isNotNullAndNotEmpty(strSourceId)) {
						updateOrCreateTargetPC(context, strSATSId, strSourceId, strTargetId, mAttribute, alConnection);
						PGStructuredATSValidateUtil pgStructuredATSValidateUtil = new PGStructuredATSValidateUtil();
						strValidate = pgStructuredATSValidateUtil.validatePerformanceChar(context, strSATSId);
						strReturn = PGStructuredATSConstants.VALUE_SUCCESS;
					} else if (PGStructuredATSConstants.ACTION_CAP_ADD.equalsIgnoreCase(strOperation)) {
						updateATSRelations(context, strCtxFOPId, strSATSId, mAttribute, alConnection,
								PGStructuredATSConstants.ACTION_ADD);
						PGStructuredATSValidateUtil pgStructuredATSValidateUtil = new PGStructuredATSValidateUtil();
						strValidate = pgStructuredATSValidateUtil.validatePerformanceChar(context, strSATSId);
						strReturn = PGStructuredATSConstants.VALUE_SUCCESS;
					}
				}
			}
		} catch (Exception excep) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_PERFORMANCE_UTIL, excep);
			throw excep;
		} 
		return strReturn;
	}
/**
	 * method to Update Attributes, Connections and depending on the WS called
	 * @param context
	 * @param strSourcePCId - Source Performance Characterstics Id
	 * @param strSATSId - Structured ATS Id
	 * @param mPCAttributeMap - Attributes to be added on Cloned PC
	 * @param alConnections - Connections to be made of Cloned PC
	 * @param sAction - Structured ATS Id to connected with Cloned PC
	 * @return 
	 * @throws Exception
	 */
	public String updateATSRelations(Context context, String strSourcePCId, String strSATSId, Map<?, ?> mPCAttributeMap, ArrayList<Map<?, ?>> alConnections, String sAction) throws Exception 
	{
		JsonObjectBuilder output = Json.createObjectBuilder();
		String strATSOperationRelId = null;
		DomainRelationship dompgATSContextId = null;
		String strATSCntxid = null;
		Map mpAddSelectedKey =null;
		try {
				if (UIUtil.isNotNullAndNotEmpty(strSourcePCId) && sAction.equalsIgnoreCase(PGStructuredATSConstants.ACTION_ADD)) 
				{	
					strATSOperationRelId  = createPerformanceCharacterstic(context,strSATSId,mPCAttributeMap,alConnections);
					updateAttributeOnATSOperation(context,strSourcePCId,strATSOperationRelId);
				}			
				else if (UIUtil.isNotNullAndNotEmpty(strSourcePCId) &&  sAction.equalsIgnoreCase(PGStructuredATSConstants.ACTION_REMOVE)) 
				{
					strATSOperationRelId = cloneSourcePCUpdateAttrAndConnect(context, strSourcePCId, strSATSId,null,null);
					strATSCntxid = getATSContextRelId(context,strSourcePCId,strATSOperationRelId);
					dompgATSContextId = new DomainRelationship(strATSCntxid);
					dompgATSContextId.setAttributeValue(context, PGStructuredATSConstants.ATTRIBUTE_PGSTRUCTUREATSACTION, sAction);
				}
			}
		catch (Exception excep)
		{
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_PERFORMANCE_UTIL, excep);
			throw excep;
		}
		return strATSCntxid;
	}
/**
 * Clones Performance Characteristics using Source PC id & connects with SATS. Updates Attributes and Connection sent from WS
 * @param context
 * @param strSourcePCId
 * @param strSATSId
 * @param mpRequestMap
 * @param alConnections
 * @return ATS Operation Rel Id
 * @throws Exception
 */	
	public String cloneSourcePCUpdateAttrAndConnect(Context context,String strSourcePCId,String strSATSId, Map<?, ?> mPCAttributeMap,ArrayList<Map<?, ?>> alConnections) throws Exception
	{
		JsonObjectBuilder output = Json.createObjectBuilder();
		DomainObject dObjPC = null;
		String strATSOperationRelId = null;
		String strNewPCCloneId = DomainConstants.EMPTY_STRING;
		String strAttributeWidgetName = null;
		String strAttributeName = null;
		String strAttributeValue = null;
		String strRelName = null;
		String strAction = null;
		boolean bIsFrom=false;
		String strObjId = null;
		DomainObject dObjClonedPC = null;
		Map<String, String> attrMap = new HashMap<>();
		PGStructuredATSUtil pgStructuredATSObj = new PGStructuredATSUtil();
		try
		{
		    dObjPC = DomainObject.newInstance(context);
			dObjPC.setId(strSourcePCId);
			matrix.db.BusinessObject boPCClone = dObjPC.cloneObject(context,null,null,null,false,true);
			strNewPCCloneId = boPCClone.getObjectId(context);
			if (null != mPCAttributeMap && mPCAttributeMap.size() > 0) 
			{
				for (Map.Entry<?, ?> entry : mPCAttributeMap.entrySet())
				{
					strAttributeWidgetName = (String) entry.getKey();
					strAttributeValue =  entry.getValue().toString();
					if ((UIUtil.isNotNullAndNotEmpty(strAttributeWidgetName) && UIUtil.isNotNullAndNotEmpty(strAttributeValue))) 
					{
						attrMap.put(strAttributeWidgetName, strAttributeValue);
					}
				}
			dObjClonedPC = DomainObject.newInstance(context, strNewPCCloneId);
			dObjClonedPC.setAttributeValues(context, attrMap);
			}
			if (null != alConnections && !alConnections.isEmpty()) 
			{
				for (Map<?, ?> mConnectionInfo : alConnections)
				{
					strObjId = (String) mConnectionInfo.get(DomainConstants.SELECT_ID);
					bIsFrom = (Boolean) mConnectionInfo.get(PGStructuredATSConstants.KEY_IS_FROM);
					strRelName = (String) mConnectionInfo.get(PGStructuredATSConstants.KEY_REL_TYPE);
					strAction = (String) mConnectionInfo.get(PGStructuredATSConstants.KEY_ACTION);
					if ((UIUtil.isNotNullAndNotEmpty(strAction) && strAction.equalsIgnoreCase(PGStructuredATSConstants.ACTION_CONNECT))) 
					{
						if(bIsFrom)
							connectObjects(context, strObjId, strNewPCCloneId, strRelName);
						else
							connectObjects(context, strNewPCCloneId, strObjId, strRelName);
					}
				}
			}
		strATSOperationRelId = pgStructuredATSObj.connectATSOpsRelations( context, strSATSId, strNewPCCloneId); // connecting SATS with Cloned PC
		} 
		catch (FrameworkException excep) 
		{
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_PERFORMANCE_UTIL, excep);
			throw excep;
		}
		return strATSOperationRelId;
	}
/**
 * Created Performance Characteristics when Source PC id is not sent. Connects with SATS & Updates Attributes and Connection sent from WS
 * @param context
 * @param strSATSId
 * @param mpRequestMap
 * @param alConnections
 * @return ATS Operation Rel Id
 * @throws Exception
 */
	public String createPerformanceCharacterstic(Context context, String strSATSId, Map<?, ?> mPCAttributeMap, ArrayList<Map<?, ?>> alConnections) throws Exception 
	{
		String strPCObjectId = null;
		String strObjId = null;
		String strAttributeWidgetName = null;
		String strAttributeName = null;
		String strAttributeValue = null;
		String strATSOperationRelId = null;
		String strRelName = null;
		String strAction = null;
		boolean bIsFrom=false;
		Map<String, String> attrMap = new HashMap<>();
		PGStructuredATSUtil pgStructuredATSObj = new PGStructuredATSUtil();
		try 
		{
			DomainObject dObj = DomainObject.newInstance(context);
			dObj.createObject(context, PGStructuredATSConstants.TYPE_PG_PERFORMANCE_CHARACTERSTIC, null, null, PGStructuredATSConstants.POLICY_EXTENDED_DATA, null);
			strPCObjectId = dObj.getObjectId(context);
			if (null != mPCAttributeMap && mPCAttributeMap.size() > 0) 
			{
				for (Map.Entry<?, ?> entry : mPCAttributeMap.entrySet())
				{
					strAttributeWidgetName = (String) entry.getKey();
					strAttributeValue =  entry.getValue().toString();
					if ((UIUtil.isNotNullAndNotEmpty(strAttributeWidgetName) && UIUtil.isNotNullAndNotEmpty(strAttributeValue))) 
					{
						attrMap.put(strAttributeWidgetName, strAttributeValue);
					}
				}
				dObj.setAttributeValues(context, attrMap);
			}
			if (null != alConnections && !alConnections.isEmpty()) 
			{
				for (Map<?, ?> mConnectionInfo : alConnections)
				{
					strObjId = (String) mConnectionInfo.get(DomainConstants.SELECT_ID);
					bIsFrom = (Boolean) mConnectionInfo.get(PGStructuredATSConstants.KEY_IS_FROM);
					strRelName = (String) mConnectionInfo.get(PGStructuredATSConstants.KEY_REL_TYPE);
					strAction = (String) mConnectionInfo.get(PGStructuredATSConstants.KEY_ACTION);
					if ((UIUtil.isNotNullAndNotEmpty(strAction) && strAction.equalsIgnoreCase(PGStructuredATSConstants.ACTION_CONNECT))) 
					{
						if(bIsFrom)
							connectObjects(context, strObjId, strPCObjectId, strRelName);
						else
							connectObjects(context, strPCObjectId, strObjId, strRelName);
					}
				}
			}
			strATSOperationRelId = pgStructuredATSObj.connectATSOpsRelations( context, strSATSId, strPCObjectId); // connecting SATS with new Created PC
		} catch (Exception excep) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_PERFORMANCE_UTIL, excep);
			throw excep;
		}
		return strATSOperationRelId;
	}
/**
 * called on Modify Performance Characterstics WS. Update Atrributes & Connections on Target PC
 * @param context
 * @param strSATSId
 * @param mpRequestMap
 * @param alConnections
 * @return ATS Operation Rel Id
 * @throws Exception
 */
	public String updateOrCreateTargetPC(Context context,String strSATSId, String strSourcePCId, String strTargetId, Map<?, ?> mPCAttributeMap, ArrayList<Map<?, ?>> alConnections) throws Exception
	{
		String strObjId = null;
		String strRelName = null;
		String strRelAction = null;
		String strAttributeName = null;
		String strAttributeValue = null;
		String strATSOperationRelId = null;
		String strATSCntxId = null;
		boolean bIsFrom=false;
		DomainObject dObjPC = null;
		DomainRelationship dompgATSContextId = null;
		Map<String, String> attrMap = new HashMap<>();
		try{
			if (UIUtil.isNotNullAndNotEmpty(strTargetId))
			{
				dObjPC = DomainObject.newInstance(context, strTargetId);
				if (null != mPCAttributeMap && mPCAttributeMap.size() > 0) 
				{
					for (Map.Entry<?, ?> entry : mPCAttributeMap.entrySet()) 
					{
						strAttributeName = (String) entry.getKey();
						if ((UIUtil.isNotNullAndNotEmpty(strAttributeName))) {
							strAttributeValue = entry.getValue().toString();
							attrMap.put(strAttributeName, strAttributeValue);
						}
					}
					dObjPC.setAttributeValues(context, attrMap);
				}
				if (null != alConnections && !alConnections.isEmpty()) 
				{
					Map<String,String> mpObjIdRelIdMap = getConnectedRefDocObjects(context, dObjPC);
					
					for (Map<?, ?> mConnectionInfo : alConnections)
					{
						strObjId = (String) mConnectionInfo.get(DomainConstants.SELECT_ID);
						bIsFrom = (Boolean) mConnectionInfo.get(PGStructuredATSConstants.KEY_IS_FROM);
						strRelName = (String) mConnectionInfo.get(PGStructuredATSConstants.KEY_REL_TYPE);
						strRelAction = (String) mConnectionInfo.get(PGStructuredATSConstants.KEY_ACTION);
						if ((UIUtil.isNotNullAndNotEmpty(strRelAction) && strRelAction.equalsIgnoreCase(PGStructuredATSConstants.ACTION_CONNECT)) && !mpObjIdRelIdMap.containsKey(strObjId)) 
						{
							if(bIsFrom)
								connectObjects(context, strObjId, strTargetId, strRelName);
							else
								connectObjects(context, strTargetId, strObjId, strRelName);
						} 
						else if ((UIUtil.isNotNullAndNotEmpty(strRelAction) && strRelAction.equalsIgnoreCase(PGStructuredATSConstants.ACTION_DISCONNECT)) && mpObjIdRelIdMap.containsKey(strObjId)) 
						{
							String strRelId = mpObjIdRelIdMap.get(strObjId);
							DomainRelationship.disconnect(context, strRelId);
						}
					}
				}
				updateATSContextAttribute(context,strTargetId);
				//strATSOperationRelId = pgStructuredATSObj.connectATSOpsRelations( context, strSATSId, strTargetId); // connecting SATS with TargetPC
			}
			else if (UIUtil.isNullOrEmpty(strTargetId))
			{
				strATSOperationRelId = cloneSourcePCUpdateAttrAndConnect(context, strSourcePCId, strSATSId,mPCAttributeMap,alConnections);
				strATSCntxId = getATSContextRelId(context,strSourcePCId,strATSOperationRelId);
				dompgATSContextId = new DomainRelationship(strATSCntxId);
				dompgATSContextId.setAttributeValue(context, PGStructuredATSConstants.ATTRIBUTE_PGSTRUCTUREATSACTION, PGStructuredATSConstants.ACTION_MODIFY);
			}
		}catch(Exception excep)
		{
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_PERFORMANCE_UTIL, excep);
			throw excep;
		}
		return strTargetId;
	}
	
	/**
	 * Method to get already connected 'Reference Documents' for disconnect operation
	 * @param context
	 * @param dObjPC
	 * @param alConnections
	 * @return
	 * @throws FrameworkException
	 */
	private Map<String, String> getConnectedRefDocObjects(Context context, DomainObject dObjPC) throws FrameworkException {
		Map<String,String> mpObjIdRelIdMap = new HashMap<>();

			MapList mlRelatedRefDocObjList = dObjPC.getRelatedObjects(context, // the eMatrix Context object
					MultiValueSelects.RELATIONSHIP_REFERENCE_DOCUMENT, // Relationship pattern
					"*", // Type pattern
					StringList.create(DomainConstants.SELECT_ID), // Object selects
					StringList.create(DomainConstants.SELECT_RELATIONSHIP_ID), // Relationship selects
					true, // get From relationships
					false, // get To relationships
					(short) 1, // the number of levels to expand, 0 equals expand all.
					null, // Object where clause
					null, // Relationship where clause
					0); // Limit : The max number of Objects to get in the exapnd.0 to return all the
						// data available
			
			if(mlRelatedRefDocObjList != null) {
				int iListSize = mlRelatedRefDocObjList.size();
				for(int i=0; i<iListSize; i++) {
					Map<?,?> mpRefDocObjMap = (Map<?, ?>) mlRelatedRefDocObjList.get(i);
					String strRefDocObjId = (String) mpRefDocObjMap.get(DomainConstants.SELECT_ID);
					String strRelId = (String) mpRefDocObjMap.get(DomainConstants.SELECT_RELATIONSHIP_ID);
					mpObjIdRelIdMap.put(strRefDocObjId, strRelId);
				}
			}
		
		return mpObjIdRelIdMap;
	}
	
/**
 * updates ATS Context Rel Attributes
 * @param context
 * @param strTargetId
 * @param strATSOperationRelId
 * @return void
 * @throws Exception
 */
	public void updateATSContextAttribute(Context context,String strTargetId) throws Exception
	{
		DomainObject dObjTargetPC = null;
		Map mStructuredATS = null;
		String strATSOperationRelId=null;
		String sATSContextRelId=null;
		StringList selectStmts = new StringList(2);
		selectStmts.addElement(DomainConstants.SELECT_ID);
		selectStmts.addElement(DomainConstants.SELECT_NAME);
		DomainRelationship dompgATSContextId = null;
		StringList relSelectsList = new StringList(DomainConstants.SELECT_RELATIONSHIP_ID);
		try 
		{
			dObjTargetPC = DomainObject.newInstance(context,strTargetId);
			MapList mlStructuredATS = dObjTargetPC.getRelatedObjects(context,PGStructuredATSConstants.REL_PG_ATS_OPERATION,PGStructuredATSConstants.TYPE_PG_STRUCTURED_ATS,selectStmts,relSelectsList,true,false,(short)1,null,"");
			if (null != mlStructuredATS)
			{
				Iterator itrStructuredATS = mlStructuredATS.iterator();
				while (itrStructuredATS.hasNext())
				{
					mStructuredATS = (Map)itrStructuredATS.next();
					if (null != mStructuredATS && mStructuredATS.size()>0)
					{
						strATSOperationRelId = (String)mStructuredATS.get(DomainConstants.SELECT_RELATIONSHIP_ID);
						sATSContextRelId = MqlUtil.mqlCommand(context, "print connection " + strATSOperationRelId + " select frommid.id dump |", true);
						if(UIUtil.isNotNullAndNotEmpty(sATSContextRelId)) {
							dompgATSContextId = new DomainRelationship(sATSContextRelId);
							dompgATSContextId.setAttributeValue(context, PGStructuredATSConstants.ATTRIBUTE_PGSTRUCTUREATSACTION, PGStructuredATSConstants.ACTION_MODIFY);
						}
					}
				}
			}
		} catch (Exception excep)
		{
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_PERFORMANCE_UTIL, excep);
			throw excep;
		}
	}
	public DomainRelationship connectObjects(Context context, String strFromObjID, String strToObjID, String strRelationshipName) throws Exception 
	{
		DomainObject dObjFromObject = null;
		DomainObject dObjToObject = null;
		DomainRelationship domRelationship = null;
		try {
			if (UIUtil.isNotNullAndNotEmpty(strFromObjID) && UIUtil.isNotNullAndNotEmpty(strToObjID)
					&& UIUtil.isNotNullAndNotEmpty(strRelationshipName)) 
			{
				dObjFromObject = DomainObject.newInstance(context, strFromObjID);
				dObjToObject = DomainObject.newInstance(context, strToObjID);
				domRelationship = DomainRelationship.connect(context, dObjFromObject, strRelationshipName,
						dObjToObject);
			}
		} catch (Exception excep) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_PERFORMANCE_UTIL, excep);
		}
		return domRelationship;
	}
/**
 * gets ATSContext Rel Id
 * @param context
 * @param strSourcePCId
 * @param strATSOperationRelId
 * @return void
 * @throws Exception
 */
	public String getATSContextRelId(Context context,String strSourcePCId,String strATSOperationRelId) throws Exception
	{
		String strATSCntxid = null;
		try
		{
			strATSCntxid = MqlUtil.mqlCommand(context, "add connection " + PGStructuredATSConstants.REL_PG_ATS_CONTEXT + " to " + strSourcePCId + " fromrel " + strATSOperationRelId + " select id dump |", true);
		} catch (Exception excep)
		{
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_PERFORMANCE_UTIL, excep);
		}
		return strATSCntxid;
	}
	/**
 * updates APP/FOP Physcial id on pgSATSPCContext attribute
 * @param context
 * @param strSourcePCId
 * @param strATSOperationRelId
 * @return void
 * @throws Exception
 */
	public void updateAttributeOnATSOperation(Context context,String strSourcePCId,String strATSOperationRelId) throws Exception
	{
		DomainRelationship dompgATSOperation = null;
		try
		{
			if (UIUtil.isNotNullAndNotEmpty(strATSOperationRelId) && UIUtil.isNotNullAndNotEmpty(strSourcePCId))
			{
				dompgATSOperation = new DomainRelationship(strATSOperationRelId);
				dompgATSOperation.setAttributeValue(context, PGStructuredATSConstants.ATTRIBUTE_PGSATSPCCONTEXT, strSourcePCId);	
			}
		} catch (Exception excep)
		{
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_PERFORMANCE_UTIL, excep);
			throw excep;
		}
	}	
/**
	 * Get the Payload & attribute Values
	 * @param context
	 * @param strSATSId
	 * @return PDP:strPerfObjID|strATSctxRelID|strATSOperRelID|strATSPerfObjID
	 * @throws Exception
	 */	
	public String fetchPerformanceCharacterstics(Context context, String strJsonInput) throws Exception  {
		return fetchPerformanceCharactersticsJson(context, strJsonInput).toString();
	}
	
	/**
	 * Method to get PC data as Json object
	 * @param context
	 * @param strJsonInput
	 * @return
	 * @throws Exception
	 */
	public JsonObject fetchPerformanceCharactersticsJson(Context context, String strJsonInput) throws Exception 
	{	
		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strJsonInput);
		
		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonArrayBuilder jsonArrObjInfo = Json.createArrayBuilder();
		JsonObjectBuilder jsonData = Json.createObjectBuilder();
		String strSATSId = null;
		String strAPPorFOPPDPId = "";
		String strAPPorFOPPCId = "";
		String strAPPorFOPPCName = "";
		boolean bFlag=false;
		DomainObject dobSATSObj = null;
		Map mSATSConnectedList = null;
		Map mpAPPorFOPPCinfo  = null;
		MapList mlSATSConnectedList = new MapList();
		MapList mlAPPorFOPConnectedPCList = null;
		MapList mlPCTestMethod = new MapList();
		MapList mlPCListCombined = new MapList();

		StringList slObjSelectList = new StringList(DomainConstants.SELECT_ID);
		slObjSelectList.add(DomainConstants.SELECT_TYPE);
		slObjSelectList.add(DomainConstants.SELECT_NAME);
		slObjSelectList.add(DomainConstants.SELECT_REVISION);
		slObjSelectList.add(DomainConstants.SELECT_ATTRIBUTE_TITLE);
		StringList slRelSelectList = new StringList(DomainConstants.SELECT_RELATIONSHIP_ID);
		StringList slATSOperIds = new StringList();
		Object objTM = "";
		MapList mlATSConnectectPC = new MapList();
		StringList  slATSConnectectPC = new StringList();
		try 
		{
			 strSATSId = jsonInputData.getString(DomainConstants.SELECT_ID);
			 if (UIUtil.isNotNullAndNotEmpty(strSATSId))
			 {
				 dobSATSObj = DomainObject.newInstance(context, strSATSId);

				Pattern relPattern = new Pattern(PGStructuredATSConstants.RELATIONSHIP_AUTHORIZED_TEMPORARY_SPECIFICATION);
				//relPattern.addPattern(PGStructuredATSConstants.REL_PG_ATS_OPERATION);

				Pattern typePattern = new Pattern(PGStructuredATSConstants.TYPE_ASSEMBLED_PRODUCT_PART);
				typePattern.addPattern(PGStructuredATSConstants.TYPE_FORMULATION_PART);

				 mlSATSConnectedList = dobSATSObj.getRelatedObjects(context, // the eMatrix Context object
										relPattern.getPattern(), // Relationship pattern
									    typePattern.getPattern(), // Type pattern
										slObjSelectList, // Object selects
										slRelSelectList, // Relationship selects
										false, // get From relationships
										true, // get To relationships
										(short) 1, // the number of levels to expand, 0 equals expand all.
										null, // Object where clause
										null, // Relationship where clause
										0); // Limit : The max number of Objects to get in the exapnd.0 to return all the data available
				//Obtain PC ids connected to ATS
				if(UIUtil.isNotNullAndNotEmpty(strSATSId))
				{
					mlATSConnectectPC = getConnectedPCDetails(context, strSATSId,new Pattern(PGStructuredATSConstants.REL_PG_ATS_OPERATION));
					if(BusinessUtil.isNotNullOrEmpty(mlATSConnectectPC))
					{
						slATSConnectectPC = BusinessUtil.toStringList(mlATSConnectectPC, DomainConstants.SELECT_ID);
					}
				}
				if(null!=mlSATSConnectedList && mlSATSConnectedList.size()>0)
				{
					Iterator itrTmpSATSConnectedList = mlSATSConnectedList.iterator(); 
					while(itrTmpSATSConnectedList.hasNext())
					{
						mSATSConnectedList = (Map)itrTmpSATSConnectedList.next();
						if(null!=mSATSConnectedList && !mSATSConnectedList.isEmpty())
						{
							strAPPorFOPPDPId = (String)mSATSConnectedList.get(DomainConstants.SELECT_ID);
							String strAPPorFOPPDPName = (String)mSATSConnectedList.get(DomainConstants.SELECT_NAME);
							if(UIUtil.isNotNullAndNotEmpty(strAPPorFOPPDPId))
							{
								mSATSConnectedList = prefixATSAttributes(mSATSConnectedList);
								mSATSConnectedList.put(PGStructuredATSConstants.KEY_HIERARCHY, strAPPorFOPPDPId);
								mlPCListCombined.add(mSATSConnectedList);
								mlAPPorFOPConnectedPCList = getConnectedPCDetails(context, strAPPorFOPPDPId,new Pattern(PGStructuredATSConstants.RELATIONSHIP_EXTENDED_DATA));
								if(mlAPPorFOPConnectedPCList != null && !mlAPPorFOPConnectedPCList.isEmpty())
								{
									for (int i=0;i<mlAPPorFOPConnectedPCList.size();i++)
									{
										mpAPPorFOPPCinfo = (Map)mlAPPorFOPConnectedPCList.get(i);
										if(null!=mpAPPorFOPPCinfo && !mpAPPorFOPPCinfo.isEmpty())
										{
											strAPPorFOPPCId = (String)mpAPPorFOPPCinfo.get(DomainConstants.SELECT_ID);
											strAPPorFOPPCName = (String)mpAPPorFOPPCinfo.get(DomainConstants.SELECT_NAME);
											objTM = mpAPPorFOPPCinfo.get("to["+DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT+"].from.id");
											mpAPPorFOPPCinfo.put(PGStructuredATSConstants.KEY_HIERARCHY, strAPPorFOPPDPId+","+strAPPorFOPPCId);
											mpAPPorFOPPCinfo.put(PGStructuredATSConstants.KEY_PARENT_ID, strAPPorFOPPDPId);
											mpAPPorFOPPCinfo.put(PGStructuredATSConstants.CONST_TEST_METHOD, getPCConnectedTM(context, objTM,PGStructuredATSConstants.TYPE_PG_TEST_METHOD_SPEC));
											mpAPPorFOPPCinfo.put(PGStructuredATSConstants.CONST_TEST_METHOD_REF_DOC, getPCConnectedTM(context, objTM,DomainConstants.EMPTY_STRING));
											if(UIUtil.isNotNullAndNotEmpty(strAPPorFOPPCId))
											{
												mlPCListCombined.add(getPCATSContextConnectedToPC(context,mpAPPorFOPPCinfo,slATSOperIds,slATSConnectectPC));
											}
										}
									}		
								}

							}

						}


					}					
				}
				getFinalreturnObjectDetails(context,mlPCListCombined,strSATSId,jsonArrObjInfo,slATSOperIds);
			}

			strLanguage = context.getSession().getLanguage();
			Map<String,String> mpObjIdCharKeyMap = new HashMap<>();
			JsonArray jsonHeaderArrObjInfo = getPCHeaderInfoArray(jsonArrObjInfo.build(), mpObjIdCharKeyMap); 
			jsonData.add(PGStructuredATSConstants.KEY_PC_HEADER_DATA, jsonHeaderArrObjInfo);
			JsonArray jsonRowArrObjInfo = getPCRowDataArray(context, jsonArrObjInfo.build(), mpObjIdCharKeyMap); 
			jsonData.add(PGStructuredATSConstants.KEY_PC_ROW_DATA, jsonRowArrObjInfo);
			
		} catch (Exception excep)
			{
				logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_PERFORMANCE_UTIL, excep);
				output.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
				output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
				return output.build();
			 }	
	return jsonData.build();
	}
	
	public Map getPCATSContextConnectedToPC(Context context, Map mpAPPorFOPPCinfo,StringList slATSOperIds,StringList slATSConnectectPC)throws Exception
	{
		try {
			String strPCId = (String)mpAPPorFOPPCinfo.get(DomainConstants.SELECT_ID);
			Pattern relPattern = new Pattern(PGStructuredATSConstants.REL_PG_ATS_CONTEXT);
			StringList sLSelectList = new StringList();
			String strATSCtxATSOprSelectable = "to["+PGStructuredATSConstants.REL_PG_ATS_CONTEXT+"].fromrel["+PGStructuredATSConstants.REL_PG_ATS_OPERATION;
			sLSelectList.add("to["+PGStructuredATSConstants.REL_PG_ATS_CONTEXT+"].id");
			sLSelectList.add("to["+PGStructuredATSConstants.REL_PG_ATS_CONTEXT+"]."+DomainObject.getAttributeSelect(PGStructuredATSConstants.ATTRIBUTE_PGSTRUCTUREATSACTION));
			sLSelectList.add(strATSCtxATSOprSelectable+"].id");
			sLSelectList.add(strATSCtxATSOprSelectable+"].to.id");
			String strTargetPCCharVal = "";
			String strAPPofFOPPCCharVal = "";
			String strATSCtxID = "";
			String strATSCtxToATSOprID = "";
			String strATSCtxStructuredATSAction = "";
			String strATSCtxTargetPCID = "";
			String strTargetPCTestMethodID = "";
			Map mpUpdatedMap = new HashMap();
			if(UIUtil.isNotNullAndNotEmpty(strPCId))
			{
				DomainObject domPCObj = DomainObject.newInstance(context,strPCId);
				Map mObjAttrValues = domPCObj.getAttributeMap(context,true);
				if(mObjAttrValues!=null && !mObjAttrValues.isEmpty())
				{
					mpAPPorFOPPCinfo.putAll(mObjAttrValues);
					strAPPofFOPPCCharVal = (String) mObjAttrValues.get("pgCharacteristic");
					if(UIUtil.isNotNullAndNotEmpty(strAPPofFOPPCCharVal)) {
					mpAPPorFOPPCinfo.put(DomainConstants.SELECT_NAME,(String) mObjAttrValues.get("pgCharacteristic"));
					}
				}
				mpAPPorFOPPCinfo = prefixATSAttributes(mpAPPorFOPPCinfo);

				Map mpPCATSCtx = (Map)DomainObject.getInfo(context, new String[] {strPCId},sLSelectList).get(0);
				if(mpPCATSCtx != null && !mpPCATSCtx.isEmpty())
				{
					StringList slATSCtxID = convertObjectToStringList(context,mpPCATSCtx.get("to["+PGStructuredATSConstants.REL_PG_ATS_CONTEXT+"].id"));
					StringList slATSCtxToATSOprID = convertObjectToStringList(context,mpPCATSCtx.get(strATSCtxATSOprSelectable+"].id"));
					StringList slATSCtxStructuredATSAction = convertObjectToStringList(context,mpPCATSCtx.get("to["+PGStructuredATSConstants.REL_PG_ATS_CONTEXT+"]."+DomainObject.getAttributeSelect(PGStructuredATSConstants.ATTRIBUTE_PGSTRUCTUREATSACTION)));
					StringList slATSCtxTargetPCID = convertObjectToStringList(context,mpPCATSCtx.get(strATSCtxATSOprSelectable+"].to.id"));
					for(int i =0 ; i < slATSCtxTargetPCID.size() ; i++)
					{
						strATSCtxTargetPCID =(String) slATSCtxTargetPCID.get(i);
						if(BusinessUtil.isNotNullOrEmpty(slATSConnectectPC) && slATSConnectectPC.contains(strATSCtxTargetPCID))
						{
							strATSCtxID = slATSCtxID.get(i);
							strATSCtxToATSOprID = slATSCtxToATSOprID.get(i);
							strATSCtxStructuredATSAction = slATSCtxStructuredATSAction.get(i);
							if(UIUtil.isNotNullAndNotEmpty(strATSCtxID) && UIUtil.isNotNullAndNotEmpty(strATSCtxToATSOprID))
							{	
								//Adding operation to Map
								if(UIUtil.isNotNullAndNotEmpty(strATSCtxStructuredATSAction))
								{
									mpAPPorFOPPCinfo.put(PGStructuredATSConstants.KEY_OPERATION,strATSCtxStructuredATSAction);
								}
								if(!slATSOperIds.contains(strATSCtxToATSOprID))
								{
									slATSOperIds.add(strATSCtxToATSOprID);
								}
								//Connected ATS - ATS operation 

								if(UIUtil.isNotNullAndNotEmpty(strATSCtxTargetPCID))
								{
									DomainObject domTargetPC = DomainObject.newInstance(context,strATSCtxTargetPCID);
									Map mpTargetPCAttributes = (Map)domTargetPC.getAttributeMap(context,true);

									//Adding Test Method info
									if(mpTargetPCAttributes != null && !mpTargetPCAttributes.isEmpty())
									{
										strTargetPCCharVal = (String)mpTargetPCAttributes.get("pgCharacteristic");
										Object obj = domTargetPC.getInfoList(context, "to["+DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT+"].from.id");
										mpTargetPCAttributes.put(PGStructuredATSConstants.CONST_TEST_METHOD_NAME , getPCConnectedTM(context, obj, PGStructuredATSConstants.TYPE_PG_TEST_METHOD_SPEC));
										mpTargetPCAttributes.put(PGStructuredATSConstants.CONST_TEST_METHOD_REF_DOC_NAME , getPCConnectedTM(context, obj, DomainConstants.EMPTY_STRING));
										mpTargetPCAttributes.put(DomainConstants.SELECT_ID, strATSCtxTargetPCID);
										mpTargetPCAttributes.put("ContextRelID", strATSCtxID);
										if(UIUtil.isNotNullAndNotEmpty(strTargetPCCharVal)) {
											mpTargetPCAttributes.put(DomainConstants.SELECT_NAME,strTargetPCCharVal);
										}
										//mpUpdatedMap = prefixATSAttributes(mpTargetPCAttributes);
										mpAPPorFOPPCinfo.putAll(mpTargetPCAttributes);
									}
								}
							}
							//Create final
						}
					}

				}
			}
			
		}catch(Exception ex)
		{
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_PERFORMANCE_UTIL, ex);
		}
		return mpAPPorFOPPCinfo;
	}
	public StringList convertObjectToStringList(Context context, Object obj)
	{
		StringList slRetList = new StringList();
		if(obj != null && obj != "")
		{
			if(obj.toString().contains(""))
			{
				slRetList = (StringList)FrameworkUtil.split((String)obj, "") ;
			}
			else if(obj instanceof String)
			{
				slRetList.add((String)obj);
			}
			else if(obj instanceof StringList)
			{
				slRetList = (StringList)obj;
			}
		}
		return slRetList;
	}
	/**
	 * @param mpUpdatedMap
	 * @param mpTargetPCAttributes
	 * @return 
	 */
	public Map prefixATSAttributes(Map mpTargetPCAttributes) throws Exception {
		Iterator targetPCItr = mpTargetPCAttributes.keySet().iterator();
		Map mpUpdatedMap = new HashMap();
		while(targetPCItr.hasNext())
		{
			String strMapKey = (String) targetPCItr.next();
			if(PGStructuredATSConstants.KEY_HIERARCHY.equals(strMapKey))
			{
				mpUpdatedMap.put(strMapKey,mpTargetPCAttributes.get(strMapKey));
			}
			else
			{
				mpUpdatedMap.put(PGStructuredATSConstants.PREFIX_CTX_FOP+strMapKey,mpTargetPCAttributes.get(strMapKey));
			}
		}
		return mpUpdatedMap;
	}
	public void getFinalreturnObjectDetails(Context context,MapList mlPCListCombined,String strSATSId,JsonArrayBuilder jsonArrObjInfo,StringList slATSOperIds)throws Exception
	{
		JsonObjectBuilder jsonData = Json.createObjectBuilder();
		Map mpATSPCInfo = new HashMap();
		Map mpAPPorFOPPCInfo = null;
		Map mpAPPorFOPPCFinalInfo = null;
		String strAPPorFOPPCATSCtxId = "";
		String strAPPorFOPPCParentId = "";
		String strType = "";
		String strAPPorFOPPCOldParentId = "";
		String strAPPorFOPPCParentName = "";
		String strATSPCId = "";
		String strATSPCATSOprRelId = "";
		String strATSPCATSOprAttrValue = "";
		String strATSPCName = "";
		DomainObject domATSPC = null;
		MapList mlFinalList = new MapList();
		MapList mlTempList = new MapList();
		StringList slATSPCIds = new StringList();
		try {
			if(UIUtil.isNotNullAndNotEmpty(strSATSId))
			{
				StringList slObjSelectList = new StringList();
				slObjSelectList.add(DomainConstants.SELECT_TYPE);
				slObjSelectList.add(DomainConstants.SELECT_NAME);
				slObjSelectList.add(DomainConstants.SELECT_ID);
				slObjSelectList.add("to["+DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT+"].from.id");
				StringList slRelSelectList =new StringList();
				slRelSelectList.add(DomainRelationship.SELECT_ID);
				slRelSelectList.add(PGStructuredATSConstants.SELECT_ATTRIBUTE_PGSATSPCCONTEXT);
				Pattern typePattern = new Pattern(PGStructuredATSConstants.TYPE_PG_PERFORMANCE_CHARACTERSTIC);
				Pattern relPattern = new Pattern(PGStructuredATSConstants.REL_PG_ATS_OPERATION);
				DomainObject domATS = DomainObject.newInstance(context, strSATSId);
				MapList mlATSPCList = domATS.getRelatedObjects(context, // the eMatrix Context object
						relPattern.getPattern(), // Relationship pattern
						typePattern.getPattern(), // Type pattern
						slObjSelectList, // Object selects
						slRelSelectList, // Relationship selects
						false, // get From relationships
						true, // get To relationships
						(short) 1, // the number of levels to expand, 0 equals expand all.
						null, // Object where clause
						null, // Relationship where clause
						0); // Limit : The max number of Objects to get in the exapnd.0 to return all the data available
				Iterator itrAPPorFOPPC = mlPCListCombined.iterator();
				while(itrAPPorFOPPC.hasNext())
				{
					mpAPPorFOPPCInfo = (Map) itrAPPorFOPPC.next();
					strType = (String) mpAPPorFOPPCInfo.get(PGStructuredATSConstants.PREFIX_CTX_FOP+DomainConstants.SELECT_TYPE);
					if(PGStructuredATSConstants.TYPE_ASSEMBLED_PRODUCT_PART.equals(strType) || PGStructuredATSConstants.TYPE_FORMULATION_PART.equals(strType))
					{
						strAPPorFOPPCParentId = (String) mpAPPorFOPPCInfo.get(PGStructuredATSConstants.PREFIX_CTX_FOP+DomainConstants.SELECT_ID);
					}
					else 
					{
						strAPPorFOPPCParentId = (String) mpAPPorFOPPCInfo.get(PGStructuredATSConstants.PREFIX_CTX_FOP+PGStructuredATSConstants.KEY_PARENT_ID);
					}
					if(UIUtil.isNullOrEmpty(strAPPorFOPPCOldParentId))
					{
						strAPPorFOPPCOldParentId = strAPPorFOPPCParentId;
					}
					else if(UIUtil.isNotNullAndNotEmpty(strAPPorFOPPCOldParentId) && !strAPPorFOPPCOldParentId.equals(strAPPorFOPPCParentId))
					{
						strAPPorFOPPCOldParentId = strAPPorFOPPCParentId;
						mlFinalList.addAll(mlTempList);
						mlTempList = new MapList();
					}
					mlFinalList.add(mpAPPorFOPPCInfo);
					if(null !=mlATSPCList && !mlATSPCList.isEmpty() && UIUtil.isNotNullAndNotEmpty(strAPPorFOPPCParentId) )
					{
						for(int i = 0; i<mlATSPCList.size();i++)
						{
							mpATSPCInfo = new HashMap();
							mpATSPCInfo = (Map)mlATSPCList.get(i);
							if(null!=mpATSPCInfo && !mpATSPCInfo.isEmpty())
							{
								strATSPCId = (String)mpATSPCInfo.get(DomainConstants.SELECT_ID);
								strATSPCATSOprRelId = (String)mpATSPCInfo.get(DomainRelationship.SELECT_ID);
								strATSPCATSOprAttrValue = (String)mpATSPCInfo.get(PGStructuredATSConstants.SELECT_ATTRIBUTE_PGSATSPCCONTEXT);
								strATSPCName = (String)mpATSPCInfo.get(DomainConstants.SELECT_NAME);
								//Check for already added ATS PC using rel ATS operation ID
								if(UIUtil.isNotNullAndNotEmpty(strATSPCId) && UIUtil.isNotNullAndNotEmpty(strATSPCATSOprAttrValue) && strATSPCATSOprAttrValue.equals(strAPPorFOPPCParentId) &&!slATSOperIds.contains(strATSPCATSOprRelId) && !slATSPCIds.contains(strATSPCId) )
								{
									slATSPCIds.add(strATSPCId);
									domATSPC = DomainObject.newInstance(context,strATSPCId);
									Object obj = mpATSPCInfo.get("to["+DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT+"].from.id");
									mpATSPCInfo.putAll(domATSPC.getAttributeMap(context,true));
									//mpATSPCInfo  = prefixATSAttributes(mpATSPCInfo);
									mpATSPCInfo.put(PGStructuredATSConstants.KEY_HIERARCHY, strAPPorFOPPCParentId+","+strATSPCId);
									mpATSPCInfo.put(PGStructuredATSConstants.KEY_OPERATION, "Add");
									mpATSPCInfo.put(PGStructuredATSConstants.CONST_TEST_METHOD_NAME, getPCConnectedTM(context, obj, PGStructuredATSConstants.TYPE_PG_TEST_METHOD_SPEC));
									mpATSPCInfo.put(PGStructuredATSConstants.CONST_TEST_METHOD_REF_DOC_NAME , getPCConnectedTM(context, obj, DomainConstants.EMPTY_STRING));
									mlTempList.add(mpATSPCInfo);
								}
							}
						}
					}
				}
				if(BusinessUtil.isNotNullOrEmpty(mlTempList))
				{
					mlFinalList.addAll(mlTempList);
				}
				//To build JsonArray
				for(int iCount = 0 ; iCount < mlFinalList.size() ; iCount++)
				{
					mpAPPorFOPPCFinalInfo = (Map)mlFinalList.get(iCount);
					jsonData =getJsonObjectProcessed((Map<?,?>)mpAPPorFOPPCFinalInfo);
					jsonArrObjInfo.add(jsonData);
				}
			}
		}catch(Exception ex)
		{
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_PERFORMANCE_UTIL, ex);
		}
	}
	/**Obtain Test Method details
	 * @param context
	 * @param domTargetPC
	 * @throws FrameworkException
	 */
	public MapList getPCConnectedTM(Context context, Object  obj, String strType) throws FrameworkException {
		String strTargetPCTestMethodID = "";
		Map mpTMInfo = new HashMap();
		MapList mlRetList = new MapList();
		StringBuilder sbTM = new StringBuilder();
		StringList slTestMethod  = new StringList();
				
		if(obj !=null && obj != "") {
			if(obj instanceof String)
			{
				slTestMethod.add((String)obj);
			}
			else if(obj instanceof StringList)
			{
				slTestMethod.addAll((StringList) obj);
			}
			if(slTestMethod != null && !slTestMethod.isEmpty())
			{
				for(int i = 0 ; i< slTestMethod.size(); i++)
				{
					mpTMInfo = new HashMap();
					String strID = slTestMethod.get(i);
					DomainObject doObj = DomainObject.newInstance(context, strID);
					
					if(UIUtil.isNotNullAndNotEmpty(strType) && (doObj.isKindOf(context, PGStructuredATSConstants.TYPE_PG_TEST_METHOD) || doObj.isKindOf(context, PGStructuredATSConstants.TYPE_PG_TEST_METHOD_SPEC)))
					{
						mpTMInfo.put(DomainConstants.SELECT_ID, strID);
						mpTMInfo.put(DomainConstants.SELECT_NAME, doObj.getInfo(context, DomainConstants.SELECT_NAME));
						//sbTM.append(strID).append("_");
						mlRetList.add(mpTMInfo);
					}
					else if(UIUtil.isNullOrEmpty(strType) && !(doObj.isKindOf(context, PGStructuredATSConstants.TYPE_PG_TEST_METHOD) || doObj.isKindOf(context, PGStructuredATSConstants.TYPE_PG_TEST_METHOD_SPEC)))
					{
						mpTMInfo.put(DomainConstants.SELECT_ID, strID);
						mpTMInfo.put(DomainConstants.SELECT_NAME, doObj.getInfo(context, DomainConstants.SELECT_NAME));
						//sbTM.append(strID).append("_");
						mlRetList.add(mpTMInfo);
					}
				}

			}
		}
		return mlRetList;
	}
/**
 * @param context
 * @param strAPPorFOPPDPId
 * @param dobSATSObj
 * @param slObjSelectList
 * @param slRelSelectList
 * @return
 * @throws FrameworkException
 */
public MapList getConnectedPCDetails(Context context, String strAPPorFOPPDPId, Pattern relPattern) throws FrameworkException {
	Pattern typePattern = new Pattern(PGStructuredATSConstants.TYPE_PG_PERFORMANCE_CHARACTERSTIC);
	MapList mlAPPorFOPConnectedPCList = null;
	JsonObjectBuilder output = Json.createObjectBuilder();
	try
	{
		StringList slObjSelectList = new StringList();
		slObjSelectList.add(DomainConstants.SELECT_TYPE);
		slObjSelectList.add(DomainConstants.SELECT_NAME);
		slObjSelectList.add(DomainConstants.SELECT_ID);
		slObjSelectList.add("to["+DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT+"].from.id");

		StringList slRelSelectList =new StringList();
		slRelSelectList.add(DomainConstants.SELECT_RELATIONSHIP_ID);
		
		if(UIUtil.isNotNullAndNotEmpty(strAPPorFOPPDPId))
		{
			DomainObject dObjAPPorFOP = DomainObject.newInstance(context, strAPPorFOPPDPId);
			mlAPPorFOPConnectedPCList = dObjAPPorFOP.getRelatedObjects(context, // the eMatrix Context object
					relPattern.getPattern(), // Relationship pattern
					typePattern.getPattern(), // Type pattern
					slObjSelectList, // Object selects
					slRelSelectList, // Relationship selects
					false, // get From relationships
					true, // get To relationships
					(short) 1, // the number of levels to expand, 0 equals expand all.
					null, // Object where clause
					null, // Relationship where clause
					0); // Limit : The max number of Objects to get in the exapnd.0 to return all the data available
		}
	}catch(Exception excep)
	{
		logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_PERFORMANCE_UTIL, excep);
	}
	
	return mlAPPorFOPConnectedPCList;
}
	/**
	 * Get pgATSContextRelId
	 * @param context
	 * @param strAPPId
	 * @return PC Name,PC Attributes and pgATSContextRelId
	 * @throws Exception
	 */	
	public String getAPPConnectedATSCtxRelId(Context context, String strAPPId) throws Exception
	{
		String strPCId =null;
		String strPCName =null;
		MapList mlAPPConnectedList = null;
		Map mAPPConnectedList = null;
		String strATSCtxRelIdFromAPP = null;
		DomainObject dObjPerfCharacterstics = null;
		Map mObjAttrValues =new HashMap();
		MapList mlPCTestMethod = null;
		Pattern relPattern = new Pattern(PGStructuredATSConstants.RELATIONSHIP_EXTENDED_DATA);
		relPattern.addPattern(PGStructuredATSConstants.REL_PG_ATS_CONTEXT);
					 
		Pattern typePattern = new Pattern(PGStructuredATSConstants.TYPE_PG_PERFORMANCE_CHARACTERSTIC);
		
		StringList slObjSelectList = new StringList();
		slObjSelectList.add(DomainConstants.SELECT_ID);
		slObjSelectList.add(DomainConstants.SELECT_NAME);
		slObjSelectList.add("to["+PGStructuredATSConstants.REL_PG_ATSCONTEXT+"].id");
		StringList slRelSelectList = new StringList(DomainConstants.SELECT_RELATIONSHIP_ID);
		try {
			DomainObject dObjAPP = DomainObject.newInstance(context,strAPPId);
			mlAPPConnectedList = dObjAPP.getRelatedObjects(context,
								relPattern.getPattern(), // Relationship pattern
								typePattern.getPattern(), // Type pattern
								slObjSelectList, // Object selects
								slRelSelectList, // Relationship selects
								false, // get From relationships
								true, // get To relationships
								(short) 0, // the number of levels to expand, 0 equals expand all.
								null, // Object where clause
								null, // Relationship where clause
								0); // Limit : The max number of Objects to get in the exapnd.0 to return all the data available);	
								
				if(null!=mlAPPConnectedList && mlAPPConnectedList.size()>0)
				{
					Iterator itrAPPConnectedList =mlAPPConnectedList.iterator(); 
					while(itrAPPConnectedList.hasNext())
					{
						mAPPConnectedList = (Map)itrAPPConnectedList.next();
						if (null !=mAPPConnectedList && mAPPConnectedList.size()>0)
						{
							strPCId = (String) mAPPConnectedList.get(DomainConstants.SELECT_ID);
							dObjPerfCharacterstics = DomainObject.newInstance(context,strPCId);
							mObjAttrValues = dObjPerfCharacterstics.getAttributeMap(context,true);
							mlPCTestMethod = getPCConnectedTestMethod(context,strPCId);
							strPCName = (String) mAPPConnectedList.get(DomainConstants.SELECT_NAME);
							strATSCtxRelIdFromAPP =(String) mAPPConnectedList.get("to["+PGStructuredATSConstants.REL_PG_ATSCONTEXT+"].id");
						}						
					}
				}								
		}catch (Exception excep)
		{
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_PERFORMANCE_UTIL, excep);
			throw excep;
		}
		return strPCName+"-"+mObjAttrValues+"|"+mlPCTestMethod +"|"+strATSCtxRelIdFromAPP;
	}
	public MapList getPCConnectedTestMethod (Context context, String strPCId) throws Exception
	{
		MapList mlPCTestItem = new MapList();
		Map mPCTestMethod = new HashMap();
		MapList mlTempPCTestMethod = new MapList();
		Map mTempPCTestMethod = new HashMap();
		DomainObject dObjPerfCharacterstics = null;
		StringList slPCSelectList = new StringList();
		slPCSelectList.add(DomainConstants.SELECT_ID);
		slPCSelectList.add(DomainConstants.SELECT_NAME);
		try {
			dObjPerfCharacterstics = DomainObject.newInstance(context,strPCId);
			mlPCTestItem = dObjPerfCharacterstics.getRelatedObjects(context,DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT,"*",slPCSelectList,null,false,true,(short)1,null,"");
			if(null!=mlPCTestItem && mlPCTestItem.size()>0)
				{
					Iterator itrPCTestItem =mlPCTestItem.iterator(); 
					while(itrPCTestItem.hasNext())
					{
						mTempPCTestMethod = new HashMap();
						mPCTestMethod = (Map)itrPCTestItem.next();
						mTempPCTestMethod.put(DomainConstants.SELECT_NAME,mPCTestMethod.get(DomainConstants.SELECT_NAME));
						mTempPCTestMethod.put(DomainConstants.SELECT_ID,mPCTestMethod.get(DomainConstants.SELECT_ID));
						mlTempPCTestMethod.add(mTempPCTestMethod);
					}
				}
			
		} catch (Exception excep)
		{
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_PERFORMANCE_UTIL, excep);
			throw excep;
		}
		return mlTempPCTestMethod;
	}	
	
	/**
	 * Method to process the Map and return the json object
	 * 
	 * @param objInfoMap
	 * @return
	 */
	public JsonObjectBuilder getJsonObjectProcessed(Map<?, ?> objInfoMap) {
		JsonObjectBuilder jsonObjInfo = Json.createObjectBuilder();
		MapList mpTMlist = new MapList();
		
		for (Map.Entry<?, ?> entry : objInfoMap.entrySet()) {
			String strKey = (String) entry.getKey();
			Object strValue = entry.getValue();
			String strObjValue = "";
			if (strValue == null) {
				strValue = PGStructuredATSConstants.STRING_NULL;
			}
			if(strValue instanceof StringList)
			{
				strObjValue = strValue.toString();
				strObjValue = strObjValue.replace("[","").replace("]","");
			}else if(strValue instanceof MapList)
			{
				mpTMlist = (MapList)strValue;
			}
			else 
			{
				strObjValue = strValue.toString();
			}
			if (PGStructuredATSConstants.KEY_HIERARCHY.equals(strKey)) {
				StringList slHierarchyList = StringUtil.split(strObjValue, ",");
				JsonArrayBuilder jsonArrHierarchy = Json.createArrayBuilder();
				int iHierarchyListSize = slHierarchyList.size();
				for (int i = 0; i < iHierarchyListSize; i++) {
					jsonArrHierarchy.add(slHierarchyList.get(i));
				}
				jsonObjInfo.add(strKey, jsonArrHierarchy);
			}else if(strKey.contains(PGStructuredATSConstants.CONST_TEST_METHOD) || strKey.contains(PGStructuredATSConstants.CONST_TEST_METHOD_REF_DOC)) {
				int iMapCount = mpTMlist.size();
				JsonArrayBuilder jsonArrTMRD = Json.createArrayBuilder();
				JsonObjectBuilder jsonTMInfo = Json.createObjectBuilder();
				for(int j = 0; j < iMapCount ; j++)
				{
					Map<String, String> mpTMInfo = (Map)mpTMlist.get(j);
					Iterator  itr = mpTMInfo.keySet().iterator();
					
					for(Map.Entry<String, String> mpTemp : mpTMInfo.entrySet())
					{
						jsonTMInfo.add(mpTemp.getKey(), mpTemp.getValue());
					}
					
					jsonArrTMRD.add(jsonTMInfo);
				}
				jsonObjInfo.add(strKey, jsonArrTMRD);
			} else {
				jsonObjInfo.add(strKey, strObjValue);
			}
		}
		return jsonObjInfo;
	}
	
	//Methods related to new SATS widget : Start ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
		
	//Methods related to PC Header Info : Start
	/**
	 * Method to get the header information for new SATS widget on fetch PC data
	 * 
	 * @param jsonArrObjInfo
	 * @return
	 */
	private JsonArray getPCHeaderInfoArray(JsonArray jsonArrObjInfo, Map<String,String> mpObjIdCharKeyMap) {
		JsonArrayBuilder jsonFinalHeaderArray = Json.createArrayBuilder();
		JsonArrayBuilder jsonNonReplacedInfoArray = Json.createArrayBuilder();
		JsonObjectBuilder jsonReplacedObjInfo = Json.createObjectBuilder();
		JsonArrayBuilder jsonATSAddedObjInfoArray = Json.createArrayBuilder();
		
		StringList slNonReplacedObjInfoList = new StringList();
		StringList slATSAddedObjIdList = new StringList();

		String strFOPPrefix = PGStructuredATSConstants.PREFIX_CTX_FOP;
		String strRelNameKey = strFOPPrefix+PGStructuredATSConstants.KEY_RELATIONSHIP;
		
		StringList slAttrKeyList = getKeyAttributeList(strFOPPrefix);
		StringList slATSAttrKeyList = getKeyAttributeList(DomainConstants.EMPTY_STRING);
		
		int iSize = jsonArrObjInfo.size();
		for (int i = 0; i < iSize; i++) {
			JsonObject jsonObjInfo = jsonArrObjInfo.getJsonObject(i);
			if (jsonObjInfo.containsKey(PGStructuredATSConstants.KEY_OPERATION)) {
				String strOperation = jsonObjInfo.getString(PGStructuredATSConstants.KEY_OPERATION);
				if(PGStructuredATSConstants.ACTION_CAP_ADD.equalsIgnoreCase(strOperation)) {
					JsonObjectBuilder jsonATSAddedObjInfo = getPCHeaderAttrJson(jsonObjInfo, slATSAttrKeyList);					
					jsonATSAddedObjInfo.add(PGStructuredATSConstants.KEY_LEVEL, PGStructuredATSConstants.KEY_ATS_PC);
					jsonATSAddedObjInfo.add(PGStructuredATSConstants.KEY_OPERATION, PGStructuredATSConstants.ACTION_CAP_ADD);

					String strPCKey = jsonATSAddedObjInfo.build().getString(PGStructuredATSConstants.STR_KEY);
					strPCKey = PGStructuredATSConstants.ACTION_CAP_ADD+PGStructuredATSConstants.SEP_DUP+strPCKey;
					jsonATSAddedObjInfo.add(PGStructuredATSConstants.STR_KEY, strPCKey);
					
					String strPCId = jsonObjInfo.getString(DomainConstants.SELECT_ID);
					mpObjIdCharKeyMap.put(strPCId, strPCKey);
					
					if(!slATSAddedObjIdList.contains(strPCKey)) {
						jsonATSAddedObjInfoArray.add(jsonATSAddedObjInfo);
						slATSAddedObjIdList.add(strPCKey);
					}
					
				} else if(PGStructuredATSConstants.ACTION_MODIFY.equalsIgnoreCase(strOperation)) {
					JsonObjectBuilder jsonCtxFopObjInfo = getPCHeaderAttrJson(jsonObjInfo, slAttrKeyList);
					jsonCtxFopObjInfo.add(PGStructuredATSConstants.KEY_LEVEL, PGStructuredATSConstants.KEY_CURRENT_PC);
					String strPCKey = jsonCtxFopObjInfo.build().getString(PGStructuredATSConstants.STR_KEY);
					String strPCId = jsonObjInfo.getString(strFOPPrefix+DomainConstants.SELECT_ID);
					mpObjIdCharKeyMap.put(strPCId, strPCKey);
					
					JsonObjectBuilder jsonATSAddedObjInfo = getPCHeaderAttrJson(jsonObjInfo, slATSAttrKeyList);					
					jsonATSAddedObjInfo.add(PGStructuredATSConstants.KEY_LEVEL, PGStructuredATSConstants.KEY_ATS_PC);
					String strATSPCKey = jsonATSAddedObjInfo.build().getString(PGStructuredATSConstants.STR_KEY);
					strATSPCKey = PGStructuredATSConstants.VALUE_ATS+PGStructuredATSConstants.SEP_DUP+strATSPCKey;
					jsonATSAddedObjInfo.add(PGStructuredATSConstants.STR_KEY, strATSPCKey);
					String strATSPCId = jsonObjInfo.getString(DomainConstants.SELECT_ID);
					mpObjIdCharKeyMap.put(strATSPCId, strATSPCKey);
					
					JsonObject jsonExistingObjInfo = jsonReplacedObjInfo.build();
					if(!jsonExistingObjInfo.containsKey(strPCKey)) {
						JsonArrayBuilder jsonUniqueReplacedObjArray = Json.createArrayBuilder();
						jsonUniqueReplacedObjArray.add(jsonCtxFopObjInfo);
						jsonUniqueReplacedObjArray.add(jsonATSAddedObjInfo);
						
						jsonReplacedObjInfo.add(strPCKey, jsonUniqueReplacedObjArray);
						
						jsonFinalHeaderArray.addAll(jsonUniqueReplacedObjArray);
						
					}
									
				}
			} else if(jsonObjInfo.containsKey(strRelNameKey)) {
				String strRelName = jsonObjInfo.getString(strRelNameKey);
				if(PGStructuredATSConstants.RELATIONSHIP_EXTENDED_DATA.equals(strRelName)) {
					JsonObjectBuilder jsonHeaderInfoObj = getPCHeaderAttrJson(jsonObjInfo, slAttrKeyList);
					jsonHeaderInfoObj.add(PGStructuredATSConstants.KEY_LEVEL, PGStructuredATSConstants.KEY_CURRENT_PC);
					jsonHeaderInfoObj.add(PGStructuredATSConstants.KEY_RELATIONSHIP, PGStructuredATSConstants.RELATIONSHIP_EXTENDED_DATA);

					String strPCKey = jsonHeaderInfoObj.build().getString(PGStructuredATSConstants.STR_KEY);
					String strPCId = jsonObjInfo.getString(strFOPPrefix+DomainConstants.SELECT_ID);
					mpObjIdCharKeyMap.put(strPCId, strPCKey);
					if(!slNonReplacedObjInfoList.contains(strPCKey)) {
						jsonNonReplacedInfoArray.add(jsonHeaderInfoObj);
						slNonReplacedObjInfoList.add(strPCKey);
						
						jsonFinalHeaderArray.add(jsonHeaderInfoObj);
					}
				}
			}
		}

		jsonFinalHeaderArray.addAll(jsonATSAddedObjInfoArray);
		
		JsonArrayBuilder jsonFinalHeaderInfoArray = updateFinalHeaderData(jsonFinalHeaderArray.build(), jsonReplacedObjInfo.build());

		return jsonFinalHeaderInfoArray.build();
	}
	
	/**
	 * Method to get the attributes to form unique Key
	 * @param strFOPPrefix
	 * @return
	 */
	private StringList getKeyAttributeList(String strFOPPrefix) {
		StringList slAttrKeyList = new StringList();
		String strKeyChar = PGStructuredATSConstants.ATTRIBUTE_PG_CHARACTERISTIC;
		String strKeyCharSpecifics = PGStructuredATSConstants.ATTRIBUTE_PG_CHARACTERISTICSPECIFIC;
		String strKeyTMS = PGStructuredATSConstants.ATTRIBUTE_TM_SPECIFCS;
		
		slAttrKeyList.add(strFOPPrefix+strKeyChar);
		slAttrKeyList.add(strFOPPrefix+strKeyCharSpecifics);
		slAttrKeyList.add(strFOPPrefix+strKeyTMS);
		
		return slAttrKeyList;
	}
	
	/**
	 * Method to get final header info by avoiding duplicates for non-replaced items
	 * @param build
	 * @param build2
	 * @return
	 */
	private JsonArrayBuilder updateFinalHeaderData(JsonArray jsonFinalHeaderArray, JsonObject jsonReplacedObjInfo) {
		JsonArrayBuilder jsonFinalHeaderInfoArray = Json.createArrayBuilder();
		
		int iArrSize = jsonFinalHeaderArray.size();
		for(int i=0;i<iArrSize;i++) {
			JsonObject jsonHeaderDataObj = jsonFinalHeaderArray.getJsonObject(i);
			if(jsonHeaderDataObj.containsKey(PGStructuredATSConstants.KEY_RELATIONSHIP)) {
				String strPCKey = jsonHeaderDataObj.getString(PGStructuredATSConstants.STR_KEY);
				if(!jsonReplacedObjInfo.containsKey(strPCKey)) {
					jsonFinalHeaderInfoArray.add(jsonHeaderDataObj);
				}
			} else {
				jsonFinalHeaderInfoArray.add(jsonHeaderDataObj);
			}

		}
				
		return jsonFinalHeaderInfoArray;
	}
	
	/**
	 * Method to get Char and 'Char Spec' attr info on header
	 * @param jsonObjInfo
	 * @param strKeyChar
	 * @param strKeyCharSpecifics
	 * @param strKeyTMS
	 * @return
	 */
	private JsonObjectBuilder getPCHeaderAttrJson(JsonObject jsonObjInfo, StringList slAttrKeyList) {
		JsonObjectBuilder jsonATSAddedObjInfo = Json.createObjectBuilder();
		StringBuilder sbKeyInfo = new StringBuilder();
		for(String strKey : slAttrKeyList) {
			String strValue = jsonObjInfo.getString(strKey);
			strKey = strKey.replace(PGStructuredATSConstants.PREFIX_CTX_FOP, "");
			jsonATSAddedObjInfo.add(strKey, strValue);
			
			strValue = strValue.trim().replaceAll("\\s+","");
			sbKeyInfo.append(strValue);
		}
		
		String strUniqueKey = sbKeyInfo.toString();
		strUniqueKey = strUniqueKey.replaceAll("_", "");
				
		jsonATSAddedObjInfo.add(PGStructuredATSConstants.STR_KEY, strUniqueKey);
		
		return jsonATSAddedObjInfo;
	}
	//Methods related to PC Header Info : End
	
	//Methods related to PC row data Info : Start
	/**
	 * Method to update row data for fetch PC web service
	 * @param build
	 * @param mpObjIdCharKeyMap
	 * @return
	 * @throws MatrixException 
	 */
	private JsonArray getPCRowDataArray(Context context, JsonArray jsonArrObjInfo, Map<String, String> mpObjIdCharKeyMap) throws MatrixException {
		JsonArrayBuilder jsonRowDataArray = Json.createArrayBuilder();
		PGNWStructuredATSBOMDataUtil objStructuredATSBOMDataUtil = new PGNWStructuredATSBOMDataUtil();
		JsonObject jsonParentChildObj = objStructuredATSBOMDataUtil.getParentChildInfo(jsonArrObjInfo);
		
		updatePCRowDataArray(context, jsonParentChildObj, mpObjIdCharKeyMap, jsonRowDataArray);
		
		return jsonRowDataArray.build();
	}
	
	/**
	 * Method to update row data for fetch PC web service
	 * @param jsonParentChildObj
	 * @param mpObjIdCharKeyMap
	 * @param jsonRowDataArray
	 * @throws MatrixException 
	 */
	private void updatePCRowDataArray(Context context, JsonObject jsonParentChildObj, Map<String, String> mpObjIdCharKeyMap,
			JsonArrayBuilder jsonRowDataArray) throws MatrixException {
		JsonObject jsonCharGroupParentChildObj = updateJsonWithUniqueChars(jsonParentChildObj, mpObjIdCharKeyMap);
		updateRowDataForPC(context, jsonRowDataArray, jsonCharGroupParentChildObj, mpObjIdCharKeyMap);
	}

	/**
	 * Method to update row data
	 * @param jsonRowDataArray
	 * @param jsonGroupedChildObj
	 * @throws MatrixException 
	 */
	private void updateRowDataForPC(Context context, JsonArrayBuilder jsonRowDataArray, JsonObject jsonCharGroupParentChildObj, Map<String, String> mpObjIdCharKeyMap) throws MatrixException {
		for (Entry<?, ?> entry : jsonCharGroupParentChildObj.entrySet()) {
			String strParentId = (String) entry.getKey();
			JsonObject jsonGroupedChildObj = jsonCharGroupParentChildObj.getJsonObject(strParentId);
			
			JsonObject jsonParentObj= jsonGroupedChildObj.getJsonObject(PGStructuredATSConstants.KEY_PARENT_INFO);
			JsonObject jsonGroupDataObj = jsonGroupedChildObj.getJsonObject(PGStructuredATSConstants.KEY_CHAR_GRPS);
			JsonArray jsonKeyArray = jsonGroupedChildObj.getJsonArray(PGStructuredATSConstants.KEY_CHAR_KEYS);
			int iMaxSize = jsonGroupedChildObj.getInt(PGStructuredATSConstants.KEY_CHAR_GRPS_MAX);
			
			for(int i=0;i<iMaxSize;i++) {
				JsonObjectBuilder jsonRowDataObj = Json.createObjectBuilder();
				
				updateParentInfo(context, jsonRowDataObj, jsonParentObj);
				
				int iKeySize = jsonKeyArray.size();
				for(int k=0;k<iKeySize;k++) {
					String strCharKey = jsonKeyArray.getString(k);
					JsonArray jsonPCObjArray = jsonGroupDataObj.getJsonArray(strCharKey);
					int iPCArraySize = jsonPCObjArray.size();
					if(iPCArraySize > i) {
						JsonObject jsonPCObj = jsonPCObjArray.getJsonObject(i);
						updateRowDataInfo(jsonRowDataObj, jsonPCObj, mpObjIdCharKeyMap);
					}
				}
				
				jsonRowDataArray.add(jsonRowDataObj);
			}
			
		}
	}
	
	/**
	 * Method to get row data for each PC object
	 * 
	 * @param jsonRowDataObj
	 * @param jsonPCObj
	 * @param mpObjIdCharKeyMap
	 */
	private void updateRowDataInfo(JsonObjectBuilder jsonRowDataObj, JsonObject jsonPCObj,
			Map<String, String> mpObjIdCharKeyMap) {
		String strCtxObjIdSelect = PGStructuredATSConstants.PREFIX_CTX_FOP + DomainConstants.SELECT_ID;
		if (jsonPCObj.containsKey(PGStructuredATSConstants.KEY_OPERATION) && PGStructuredATSConstants.ACTION_MODIFY
				.equalsIgnoreCase(jsonPCObj.getString(PGStructuredATSConstants.KEY_OPERATION))) {

			String strCurrentPCId = jsonPCObj.getString(strCtxObjIdSelect);
			String strCurrentChKey = mpObjIdCharKeyMap.get(strCurrentPCId);
			String strATSPCId = jsonPCObj.getString(DomainConstants.SELECT_ID);
			String strATSChKey = mpObjIdCharKeyMap.get(strATSPCId);
			
			for (Entry<?, ?> entryJsonObj : jsonPCObj.entrySet()) {
				String strKey = (String) entryJsonObj.getKey();
				String strUpdatedKey = strKey;
				if(strKey.startsWith(PGStructuredATSConstants.PREFIX_CTX_FOP)) {
					strUpdatedKey = strUpdatedKey.replace(PGStructuredATSConstants.PREFIX_CTX_FOP, "");
					strUpdatedKey = strCurrentChKey + "_" + strUpdatedKey;
				} else {
					strUpdatedKey = strATSChKey + "_" + strUpdatedKey;
				}
				
				if(PGStructuredATSConstants.KEY_HIERARCHY.equals(strKey) || strKey.contains(PGStructuredATSConstants.CONST_TEST_METHOD)) {
					JsonArray jsonHierarchyArray = jsonPCObj.getJsonArray(strKey);
					strUpdatedKey = strUpdatedKey.replace(PGStructuredATSConstants.SUFFIX_NAME, "");
					jsonRowDataObj.add(strUpdatedKey, jsonHierarchyArray);
				} else {
					String strValue = jsonPCObj.getString(strKey);
					jsonRowDataObj.add(strUpdatedKey, strValue);
				}
			}
			
		} else {
			String strObjIdSelect = strCtxObjIdSelect;
			if (!jsonPCObj.containsKey(strObjIdSelect)) {
				strObjIdSelect = DomainConstants.SELECT_ID;
			}

			String strPCId = jsonPCObj.getString(strObjIdSelect);
			String strCharKey = mpObjIdCharKeyMap.get(strPCId);
			
			updateRowDataForPC(jsonPCObj, strCharKey, jsonRowDataObj);

		}
	}
	
	/**
	 * Method to update row data for PC
	 * @param jsonPCObj
	 * @param strCharKey
	 * @param jsonRowDataObj
	 */
	private void updateRowDataForPC(JsonObject jsonPCObj, String strCharKey, JsonObjectBuilder jsonRowDataObj) {
		for (Entry<?, ?> entryJsonObj : jsonPCObj.entrySet()) {
			String strKey = (String) entryJsonObj.getKey();
			String strUpdatedKey = strKey.replace(PGStructuredATSConstants.PREFIX_CTX_FOP, "");
			strUpdatedKey = strCharKey + "_" + strUpdatedKey;
			if(PGStructuredATSConstants.KEY_HIERARCHY.equals(strKey) || strKey.contains(PGStructuredATSConstants.CONST_TEST_METHOD)) {
				JsonArray jsonHierarchyArray = jsonPCObj.getJsonArray(strKey);
				strUpdatedKey = strUpdatedKey.replace(PGStructuredATSConstants.SUFFIX_NAME, "");
				jsonRowDataObj.add(strUpdatedKey, jsonHierarchyArray);
			} else {
				String strValue = jsonPCObj.getString(strKey);
				jsonRowDataObj.add(strUpdatedKey, strValue);
			}
			
		}
	}
	
	/**
	 * Method to update json parent info
	 * @param jsonRowDataObj
	 * @param jsonParentObj
	 * @throws MatrixException 
	 */
	private void updateParentInfo(Context context, JsonObjectBuilder jsonRowDataObj, JsonObject jsonParentObj) throws MatrixException {
		String strPrefix = PGStructuredATSConstants.PREFIX_CTX_FOP;
		String strTypeKey = strPrefix+DomainConstants.SELECT_TYPE;
		String strTitleKey = strPrefix+DomainConstants.SELECT_ATTRIBUTE_TITLE;
				
		for (Entry<?, ?> entryJsonObj : jsonParentObj.entrySet()) {
			String strKey = (String) entryJsonObj.getKey();
			
			if(strTitleKey.equals(strKey)) {
				String strValue = jsonParentObj.getString(strKey);
				jsonRowDataObj.add(strPrefix+DomainConstants.ATTRIBUTE_TITLE, strValue);
			}
			
			if(strTypeKey.equals(strKey)) {
				String strTypeName = jsonParentObj.getString(strKey);
				String strDisplayName = EnoviaResourceBundle.getAdminI18NString(context,
						PGStructuredATSConstants.STR_SCHEMA_TYPE, strTypeName, strLanguage);

				if (strDisplayName == null) {
					strDisplayName = strTypeName;
				}
				jsonRowDataObj.add(strPrefix+PGStructuredATSConstants.KEY_DISPLAY_TYPE, strDisplayName);
				
				//Update actual type
				String strValue = jsonParentObj.getString(strKey);
				jsonRowDataObj.add(strKey, strValue);
				
			} else if(PGStructuredATSConstants.KEY_HIERARCHY.equals(strKey)) {
				JsonArray jsonHierarchyArray = jsonParentObj.getJsonArray(strKey);
				jsonRowDataObj.add(strKey, jsonHierarchyArray);
			} else {
				String strValue = jsonParentObj.getString(strKey);
				jsonRowDataObj.add(strKey, strValue);
			}
			
		}
	}
	
	/**
	 * Method to update parent child Json with unique combinations for Char and Char Specs
	 * @param jsonParentChildObj
	 * @param mpObjIdCharKeyMap
	 * @return
	 */
	private JsonObject updateJsonWithUniqueChars(JsonObject jsonParentChildObj, Map<String, String> mpObjIdCharKeyMap) {
		JsonObjectBuilder jsonCharGroupParentChildObj = Json.createObjectBuilder();
		for (Entry<?, ?> entry : jsonParentChildObj.entrySet()) {
			String strParentId = (String) entry.getKey();
			JsonArray jsonParentChildArray = jsonParentChildObj.getJsonArray(strParentId);
			int iArraySize = jsonParentChildArray.size();
			if(iArraySize > 1) {
				JsonObjectBuilder jsonUpdatedParentChildObj = Json.createObjectBuilder();
				JsonObjectBuilder jsonCharGroupObj = Json.createObjectBuilder();
				JsonArrayBuilder jsonCharKeyArray = Json.createArrayBuilder();
				for(int i=1; i<iArraySize; i++) {
					JsonObject jsonChildPCObject = jsonParentChildArray.getJsonObject(i);
					String strObjIdSelect = getObjIdSelect(jsonChildPCObject);
					String strObjId = jsonChildPCObject.getString(strObjIdSelect);
					String strCharKey = mpObjIdCharKeyMap.get(strObjId);
					
					JsonObject jsonChGrpObj = jsonCharGroupObj.build();
					if(jsonChGrpObj.containsKey(strCharKey)) {
						JsonArrayBuilder jsonCharGrpArray = Json.createArrayBuilder();
						JsonArray jsonExistingDataArray = jsonChGrpObj.getJsonArray(strCharKey);
						int iDataSize = jsonExistingDataArray.size();
						for(int j=0;j<iDataSize;j++) {
							JsonObject jsonExistingDataObj = jsonExistingDataArray.getJsonObject(j);
							jsonCharGrpArray.add(jsonExistingDataObj);
						}
						
						jsonCharGrpArray.add(jsonChildPCObject);
						jsonCharGroupObj.add(strCharKey, jsonCharGrpArray);

					} else {
						JsonArrayBuilder jsonCharGrpArray = Json.createArrayBuilder();
						jsonCharGrpArray.add(jsonChildPCObject);
						jsonCharGroupObj.add(strCharKey, jsonCharGrpArray);
						jsonCharKeyArray.add(strCharKey);
					}
				}
				
				jsonUpdatedParentChildObj.add(PGStructuredATSConstants.KEY_PARENT_INFO, jsonParentChildArray.getJsonObject(0));
				jsonUpdatedParentChildObj.add(PGStructuredATSConstants.KEY_CHAR_GRPS, jsonCharGroupObj);
				jsonUpdatedParentChildObj.add(PGStructuredATSConstants.KEY_CHAR_KEYS, jsonCharKeyArray);
				int iMaxSize = getMaxSizeForGroupArrays(jsonCharGroupObj.build());
				jsonUpdatedParentChildObj.add(PGStructuredATSConstants.KEY_CHAR_GRPS_MAX, iMaxSize);
				jsonCharGroupParentChildObj.add(strParentId, jsonUpdatedParentChildObj);
			}
		}
		
		return jsonCharGroupParentChildObj.build();
	}
	
	/**
	 * Method to get maximum possible size for groups
	 * @param build
	 * @return
	 */
	private int getMaxSizeForGroupArrays(JsonObject jsonCharGroupObj) {
		int iMaxSize=0;
		
		for (Entry<?, ?> entry : jsonCharGroupObj.entrySet()) {
			String strCharKey = (String) entry.getKey();
			JsonArray jsonCharGrpArray = jsonCharGroupObj.getJsonArray(strCharKey);
			int iArraySize = jsonCharGrpArray.size();
			if(iArraySize > iMaxSize) {
				iMaxSize = iArraySize;
			}
		}
		
		return iMaxSize;
	}
	/**
	 * Get object id select info
	 * @param jsonChildPCObject
	 * @return
	 */
	private String getObjIdSelect(JsonObject jsonChildPCObject) {
		String strObjIdSelect = "";
		String strFOPIdSelect = PGStructuredATSConstants.PREFIX_CTX_FOP + DomainConstants.SELECT_ID;
		if (jsonChildPCObject.containsKey(PGStructuredATSConstants.KEY_OPERATION)) {
			String strOperation = jsonChildPCObject.getString(PGStructuredATSConstants.KEY_OPERATION);
			if(PGStructuredATSConstants.ACTION_CAP_ADD.equalsIgnoreCase(strOperation)) {
				strObjIdSelect = DomainConstants.SELECT_ID;
			} else {
				strObjIdSelect = strFOPIdSelect;
			}
		} else {
			strObjIdSelect = strFOPIdSelect;
		}
		
		return strObjIdSelect;
	}
	//Methods related to PC row data Info : End
	
	
	public String removeDeletePerfCharCopyToSATS(Context context, Map mpRequestMap) throws Exception {

		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		String strValidate = null;
		String strOperation = null;
		try {
			ArrayList<Map<?, ?>> alData = (ArrayList<Map<?, ?>>) mpRequestMap.get(PGStructuredATSConstants.KEY_DATA);
			if (null != alData && !alData.isEmpty()) {
				for (Map<?, ?> mData : alData) {
					strOperation = (String) mData.get(PGStructuredATSConstants.VALUE_OPERATION);

					if (PGStructuredATSConstants.ACTION_REMOVE.equalsIgnoreCase(strOperation)) {

						strValidate = removePerfCharCopyToSATS(context, mData);
						jsonReturnObj.add(PGStructuredATSConstants.KEY_STATUS, PGStructuredATSConstants.VALUE_SUCCESS);

					} else if (PGStructuredATSConstants.ACTION_DELETE.equalsIgnoreCase(strOperation)) {

						PGStructuredATSDeleteUtil pgStructuredATSDeleteUtil = new PGStructuredATSDeleteUtil();
						strValidate = pgStructuredATSDeleteUtil.deletePCSATS(context, mData);
						if (strValidate.contains(PGStructuredATSConstants.VALUE_SUCCESS)) {
							jsonReturnObj.add(PGStructuredATSConstants.KEY_STATUS,
									PGStructuredATSConstants.VALUE_SUCCESS);
						} else {
							jsonReturnObj.add(PGStructuredATSConstants.KEY_STATUS,
									PGStructuredATSConstants.VALUE_FAILED);
						}
					}
				}
			}
		} catch (Exception excep) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_PERFORMANCE_UTIL, excep);
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
			return jsonReturnObj.build().toString();
		}
		return jsonReturnObj.build().toString();
	}

	public String removePerfCharCopyToSATS(Context context, Map mData) throws Exception {
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		String strReturn = null;
		String strSourcePCId = null;
		String strSATSId = null;
		String strValidate = null;
		try {
			strSATSId = (String) mData.get(DomainConstants.SELECT_ID);
			strSourcePCId = (String) mData.get(PGStructuredATSConstants.SOURCE_ID);
			if (strSourcePCId.contains(",")) {
				StringList slSourceIdList = FrameworkUtil.split(strSourcePCId, ",");
				for (int iSize = 0; iSize < slSourceIdList.size(); iSize++) {
					updateATSRelations(context, slSourceIdList.get(iSize), strSATSId, null, null,
							PGStructuredATSConstants.ACTION_REMOVE);
				}
			} else {
				updateATSRelations(context, strSourcePCId, strSATSId, null, null,
						PGStructuredATSConstants.ACTION_REMOVE);
			}
			PGStructuredATSValidateUtil pgStructuredATSValidateUtil = new PGStructuredATSValidateUtil();
			strValidate = pgStructuredATSValidateUtil.validatePerformanceChar(context, strSATSId);
			jsonReturnObj.add(PGStructuredATSConstants.KEY_STATUS, PGStructuredATSConstants.VALUE_SUCCESS);
		} catch (Exception excep) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_PERFORMANCE_UTIL, excep);
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
			return jsonReturnObj.build().toString();
		}
		return jsonReturnObj.build().toString();
	}
	//Methods related to new SATS widget : End ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	
}

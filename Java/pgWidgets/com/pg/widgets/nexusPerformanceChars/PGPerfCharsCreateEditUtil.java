package com.pg.widgets.nexusPerformanceChars;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import com.dassault_systemes.enovia.formulation.custom.enumeration.FormulationType;
import com.dassault_systemes.enovia.formulation.enumeration.FormulationRelationship;
import com.matrixone.apps.cpn.CPNCommonConstants;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkProperties;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.engineering.RelToRelUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.widgets.util.PGWidgetConstants;
import com.pg.widgets.util.PGWidgetUtil;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.db.RelationshipType;
import matrix.util.StringList;

public class PGPerfCharsCreateEditUtil {

	private static final Logger logger = Logger.getLogger(PGPerfCharsCreateEditUtil.class.getName());
	PGPerfCharsFetchData objPerfCharsFetchData = new PGPerfCharsFetchData();
	PGPerfCharsUtil pgPerfCharsUtil = new PGPerfCharsUtil();
	/**
	 * Method to create, edit or remove 'Performance Characteristics' objects
	 * 
	 * @param context
	 * @param strJsonInput
	 * @return
	 * @throws FrameworkException 
	 */
	String createUpdatePerfChars(Context context, String strJsonInput) throws FrameworkException {

		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		try {
			ContextUtil.startTransaction(context, true);
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strJsonInput);
			JsonArray jsonDataArray = jsonInputData.getJsonArray(PGPerfCharsConstants.KEY_DATA);
			String strParentId = jsonInputData.getString(DomainConstants.SELECT_ID);
			String strTableName = jsonInputData.getString(PGPerfCharsConstants.KEY_SELECTED_TABLE);
			
			StringList slDelPerCharIdList = new StringList();
			int iDataSize = jsonDataArray.size();
			String  strErrorReturn = validateNexusTestMethod(context,jsonDataArray,strParentId, iDataSize);
			logger.info("--**On Save Validations-Error Mesage**-->"+strErrorReturn);
			strErrorReturn = "";
			if(UIUtil.isNotNullAndNotEmpty(strErrorReturn))
			{
				jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, strErrorReturn);
			}
			else
			{
			for(int i=0; i<iDataSize; i++) {
				JsonObject jsonDataObj = jsonDataArray.getJsonObject(i);
				String strOperation = jsonDataObj.getString(PGPerfCharsConstants.KEY_OPERATION);
				
				if(PGPerfCharsConstants.KEY_EDIT.equals(strOperation)) {
					editPerfCharObject(context, jsonDataObj);
					
				} else if(PGPerfCharsConstants.KEY_CREATE.equals(strOperation)) {
					jsonReturnObj = connectSharedTableCharacteristic(context, strParentId, strTableName, jsonDataObj);

				} else if(PGPerfCharsConstants.KEY_DEL.equals(strOperation)) {
					String strPerfCharId  = jsonDataObj.getString(DomainConstants.SELECT_ID);
					slDelPerCharIdList.add(strPerfCharId);
				}
				
			}
			
			if(!slDelPerCharIdList.isEmpty()) {
				String[] strPerfCharOIDArray = slDelPerCharIdList.toStringArray();
				DomainObject.deleteObjects(context, strPerfCharOIDArray);
			}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, PGPerfCharsConstants.EXCEPTION_MESSAGE_PERF_CHAR_CREATE_EDIT_UTIL, e);
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
		}
		
		if(jsonReturnObj.build().isEmpty()) {
			ContextUtil.commitTransaction(context);
			return objPerfCharsFetchData.fetchPerfCharsData(context, strJsonInput);
		} else {
			ContextUtil.abortTransaction(context);
			return jsonReturnObj.build().toString();
		}

	}
	
	//Methods related to 'Edit' operation : Start	
	/**
	 * Method to edit the input Perf Char object
	 * @param context
	 * @param jsonDataObj
	 * @throws Exception 
	 */
    private void editPerfCharObject(Context context, JsonObject jsonDataObj) throws Exception {
    	String strPerfCharId = jsonDataObj.getString(DomainConstants.SELECT_ID);
    	DomainObject dobPerfCharObj = DomainObject.newInstance(context, strPerfCharId);
		if(jsonDataObj.containsKey(PGPerfCharsConstants.KEY_ATTRIBUTE)) {
			JsonObject jsonAttributes = jsonDataObj.getJsonObject(PGPerfCharsConstants.KEY_ATTRIBUTE);
			Map<String, String> mpAttributeInfoMap = getAttributeMapFromJson(jsonAttributes);
			if(jsonAttributes.containsKey(PGPerfCharsConstants.ATTRIBUTE_PG_APPLICATION)) {
				String strApplicationValue = jsonAttributes.getString(PGPerfCharsConstants.ATTRIBUTE_PG_APPLICATION);
				updateApplicationAttribute(context, strPerfCharId, strApplicationValue);
				mpAttributeInfoMap.remove(PGPerfCharsConstants.ATTRIBUTE_PG_APPLICATION);
			}			
			dobPerfCharObj.setAttributeValues(context, mpAttributeInfoMap);
		}
		
		if(jsonDataObj.containsKey(PGPerfCharsConstants.KEY_REL_ATTRIBUTE)) {
			JsonArray jsonRelAttrArray = jsonDataObj.getJsonArray(PGPerfCharsConstants.KEY_REL_ATTRIBUTE);
			int iRelAttrSize = jsonRelAttrArray.size();
			for(int i=0; i<iRelAttrSize; i++) {
				JsonObject jsonRelAttrObj = jsonRelAttrArray.getJsonObject(i);
				if(jsonRelAttrObj.containsKey(PGPerfCharsConstants.ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE)) {
					String strAttrValue = jsonRelAttrObj.getString(PGPerfCharsConstants.ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE);
					String strRelId = jsonRelAttrObj.getString(PGPerfCharsConstants.KEY_RELID);
					updateSequenceNumber(context, strRelId, strAttrValue);
				}
			}
		}
		
		if(jsonDataObj.containsKey(PGPerfCharsConstants.KEY_CONNECTIONS)) {
			JsonArray jsonUpdateProgArray = jsonDataObj.getJsonArray(PGPerfCharsConstants.KEY_CONNECTIONS);
			int iDataSize = jsonUpdateProgArray.size();
			for(int i=0; i<iDataSize; i++) {
				JsonObject jsonUpdateProg = jsonUpdateProgArray.getJsonObject(i);
				editPerfCharForUpdateProgConnectionColumns(context, jsonUpdateProg, strPerfCharId);
			}
		}
		
	}
    
	/**
	 * Method to edit Perf Char for update program columns (TM and TM Ref Docs)
	 * 
	 * @param context
	 * @param jsonUpdateProg
	 * @param strPerfCharId
	 * @throws Exception
	 */
	private void editPerfCharForUpdateProgConnectionColumns(Context context, JsonObject jsonUpdateProg, String strPerfCharId)
			throws Exception {
		String strColName = jsonUpdateProg.getString("name");
		String strTMIds = jsonUpdateProg.getString("ids");
		if (strColName.contains("TestMethodName")) {
			updateCharterteristcTestMethodConnection(context, strPerfCharId, strTMIds);
		} else if (strColName.contains("TestMethodRefDoctName")) {
			updateCharterteristcTMRDConnection(context, strPerfCharId, strTMIds);
		}
	}
    
	/**
	 * Method to get attributes Map from Json object
	 * 
	 * @param jsonRelAttributes
	 * @return
	 */
	private Map<String, String> getAttributeMapFromJson(JsonObject jsonRelAttributes) {
		Map<String, String> mpAttributeInfoMap = new HashMap<>();

		for (Entry<?, ?> entry : jsonRelAttributes.entrySet()) {
			String strAttrName = (String) entry.getKey();
			String strValue = jsonRelAttributes.getString(strAttrName);
			if(strValue == null) {
				strValue = "";
			}
			mpAttributeInfoMap.put(strAttrName, strValue);
		}

		return mpAttributeInfoMap;
	}
    
	/**
	 * This method will update Sequence Number On Performance Char Table. Defect
	 * 12731
	 * 
	 * Method emxCPNCharacteristicList:updateSequenceNumber
	 * 
	 * @param context
	 * @param strRelId
	 * @param strNewSequenceNumber
	 * @throws FrameworkException
	 */
	public void updateSequenceNumber(Context context, String strRelId, String strNewSequenceNumber)
			throws FrameworkException {
		boolean isCtxtPushed = false;
		try {
			if (UIUtil.isNotNullAndNotEmpty(strRelId)) {
				//DSM (DS) 2022x : Push context required to set attribute sequence number as context user doesn't have access to perform this action
				ContextUtil.pushContext(context, PropertyUtil.getSchemaProperty(context, "person_UserAgent"),
						DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
				isCtxtPushed = true;
				DomainRelationship.setAttributeValue(context, strRelId,
						PGPerfCharsConstants.ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE, strNewSequenceNumber);
			}
		} finally {
			if (isCtxtPushed)
				ContextUtil.popContext(context);
		}
	}

	// DSM(DS) 2018x.0 Production Incident - Updating pgApplication attribute for
	// the same - Start
	/**
	 * This method will update pgApplication attribute value On Performance Char
	 * Table.
	 * 
	 * Method copied from pgDSOExportChracteristic:updateApplicationAttribute
	 * 
	 * @param context
	 * @param args
	 * @throws FrameworkException
	 * @throws Exception
	 */
	public void updateApplicationAttribute(Context context, String strObjectId, String strNewAppNumber)
			throws FrameworkException {
		boolean isCtxtPushed = false;
		try {
			if (UIUtil.isNotNullAndNotEmpty(strObjectId)) {
				//DSM (DS) 2022x : Push context required to set attribute Application as context user doesn't have access to perform this action
				ContextUtil.pushContext(context, PropertyUtil.getSchemaProperty(context, "person_UserAgent"),
						DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
				isCtxtPushed = true;
				DomainObject.newInstance(context, strObjectId).setAttributeValue(context, PGPerfCharsConstants.ATTRIBUTE_PG_APPLICATION,
						strNewAppNumber);
			}
		} finally {
			if (isCtxtPushed)
				ContextUtil.popContext(context);
		}
	}
	// DSM(DS) 2018x.0 Production Incident - Updating pgApplication attribute for
	// the same - End

	/**
	 * This method updates the connection for Test Metho attribute on
	 * pgTransportUnit Relationship using picklist
	 * 
	 * Method copied from emxCPNProductData:updateCharterteristcTestMethodConnection
	 * 
	 * @param context
	 * @param ctxObjecID
	 * @param newTestMethodIds
	 * @throws Exception
	 */
	public void updateCharterteristcTestMethodConnection(Context context, String ctxObjecID, String newTestMethodIds)
			throws Exception {
		try {
			String strTMCurrentState = DomainConstants.EMPTY_STRING;
			String strReleasedTM = DomainConstants.EMPTY_STRING;
			String strNonReleasedTM = DomainConstants.EMPTY_STRING;
			String strObjWhere = "latest == TRUE";
			Map tMobject = null;
			MapList objectsList = null;
			String[] testMethodConnectionIdArray = null;

			if (UIUtil.isNotNullAndNotEmpty(ctxObjecID)) {
				DomainObject charObj = DomainObject.newInstance(context, ctxObjecID);
				RelationshipType relType = new RelationshipType(CPNCommonConstants.RELATIONSHIP_REFERENCE_DOCUMENT);

				String strConfType = PropertyUtil.getSchemaProperty(context, "type_TestMethodSpecification");

				StringList objectSel = new StringList();
				objectSel.add(DomainConstants.SELECT_ID);
				MapList connectedTM = charObj.getRelatedObjects(context,
						DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT, strConfType, objectSel, null, true, false,
						(short) 1, null, null, 0);
				StringList slAlreadyConnectedTMIds = new StringList();
				if (connectedTM != null && !connectedTM.isEmpty()) {
					for (Object tmpObj : connectedTM) {
						Map tmpMap = (Map) tmpObj;
						slAlreadyConnectedTMIds.add((String) tmpMap.get(DomainConstants.SELECT_ID));
					}
				}

				if (!DomainConstants.EMPTY_STRING.equals(newTestMethodIds)) {
					StringList slTMIds = new StringList();
					StringList slTempTMIds = new StringList();
					slTempTMIds = StringUtil.splitString(newTestMethodIds, "|");
					String strTMID = DomainConstants.EMPTY_STRING;

					boolean isContextPushed = false;
					try {
						//DSM (DS) 2022x : Push context required to get TestMethod info as context user doesn't have access to perform this action
						ContextUtil.pushContext(context, PGPerfCharsConstants.PERSON_AGENT,
								DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
						isContextPushed = true;

						for (Object obj : slTempTMIds) {
							boolean bisObjectID = true;
							String strTempId = obj.toString();
							if (UIUtil.isNotNullAndNotEmpty(strTempId)) {
								if (!strTempId.contains(".")) {
									bisObjectID = false;
									StringList slObjSelect = new StringList();
									slObjSelect.add(DomainConstants.SELECT_LAST_ID);

									slObjSelect.add(DomainConstants.SELECT_ID);
									slObjSelect.add(DomainConstants.SELECT_CURRENT);
									objectsList = DomainObject.findObjects(context, strConfType, strTempId, DomainConstants.QUERY_WILDCARD, DomainConstants.QUERY_WILDCARD,
											null, strObjWhere, true, slObjSelect);
									if (objectsList != null && !objectsList.isEmpty()) {
										for (Iterator iterator = objectsList.iterator(); iterator.hasNext();) {
											tMobject = (Map) iterator.next();
											strTMID = (String) tMobject.get(DomainConstants.SELECT_ID);
											strTMCurrentState = (String) tMobject.get(DomainConstants.SELECT_CURRENT);

											if (UIUtil.isNotNullAndNotEmpty(strTMID)) {

												if (PGPerfCharsConstants.STATE_RELEASE.equals(strTMCurrentState)
														|| PGPerfCharsConstants.STATE_COMPLETE
																.equals(strTMCurrentState)) {
													strReleasedTM = strTMID;
												} else if (!PGPerfCharsConstants.STATE_OBSOLETE
														.equals(strTMCurrentState)) {
													strNonReleasedTM = strTMID;
												}
											}

										}
									}
									if ((UIUtil.isNotNullAndNotEmpty(strReleasedTM)
											&& UIUtil.isNotNullAndNotEmpty(strNonReleasedTM))
											|| (UIUtil.isNotNullAndNotEmpty(strReleasedTM)
													&& UIUtil.isNullOrEmpty(strNonReleasedTM))) {
										if (!slAlreadyConnectedTMIds.contains(strTMID)) {
											slTMIds.add(strReleasedTM);
										}
									} else if (UIUtil.isNotNullAndNotEmpty(strNonReleasedTM)) {
										if (!slAlreadyConnectedTMIds.contains(strTMID)
												&& !slAlreadyConnectedTMIds.contains(strNonReleasedTM)) {
											slTMIds.add(strNonReleasedTM);
										}
									}
								}

								else {
									if (!slAlreadyConnectedTMIds.contains(strTMID)) {
										slTMIds.add(strTempId);
									}
								}
							}
						}
					} catch (Exception ex) {
						throw ex;
					} finally {
						if (isContextPushed) {
							ContextUtil.popContext(context);
						}
					}

					testMethodConnectionIdArray = (String[]) slTMIds.toArray(new String[slTMIds.size()]);

				}
				if (DomainConstants.EMPTY_STRING.equals(newTestMethodIds)
						|| (testMethodConnectionIdArray != null && testMethodConnectionIdArray.length > 0)) {
					if (slAlreadyConnectedTMIds != null && !slAlreadyConnectedTMIds.isEmpty()) {
						for (Object oConnectedId : slAlreadyConnectedTMIds) {
							if (UIUtil.isNotNullAndNotEmpty((String) oConnectedId)) {
								charObj.disconnect(context, relType, false,
										DomainObject.newInstance(context, (String) oConnectedId));
							}
						}
					}
				}

				if (testMethodConnectionIdArray !=null && testMethodConnectionIdArray.length > 0) {
					charObj.addRelatedObjects(context, relType, false, testMethodConnectionIdArray);
				}
			}
		} catch (Exception ex) {
			
		}
	}

	/**
	 * This method updates the connection for Test Method Reference Document to
	 * Performance Characteristic Object
	 * 
	 * Method copied from emxCPNProductData:updateCharterteristcTMRDConnection
	 * 
	 * @param context
	 * @param ctxObjecID
	 * @param newTestMethodIds
	 * @throws Exception
	 */
	public void updateCharterteristcTMRDConnection(Context context, String ctxObjecID, String newTestMethodIds)
			throws Exception {
		boolean isCtxtPushed = false;
		try {
			if (UIUtil.isNotNullAndNotEmpty(ctxObjecID)) {
				String strConfType = "";
				DomainObject charObj = DomainObject.newInstance(context, ctxObjecID);
				RelationshipType relType = new RelationshipType(CPNCommonConstants.RELATIONSHIP_REFERENCE_DOCUMENT);

				String strContextType = charObj.getInfo(context, DomainConstants.SELECT_TYPE);
				if (UIUtil.isNotNullAndNotEmpty(strContextType)
						&& strContextType.equalsIgnoreCase(PGPerfCharsConstants.TYPE_PG_STABILITY_RESULTS))
					strConfType = PGPerfCharsConstants.TYPE_PG_IRM_DOC_TYPES;
				else
					strConfType = PGPerfCharsConstants.TYPE_PG_TMRD_TYPES;
				if (charObj.isKindOf(context, CPNCommonConstants.TYPE_SHARED_TABLE)) {
					return;
				}
				if (DomainConstants.EMPTY_STRING.equals(newTestMethodIds)) {
					StringList objectSel = new StringList();
					objectSel.add(DomainConstants.SELECT_ID);
					MapList connectedTM = charObj.getRelatedObjects(context,
							DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT, strConfType, objectSel, null, true, false,
							(short) 1, null, null, 0);
					StringList slAlreadyConnectedTMIds = new StringList();
					if (connectedTM != null && !connectedTM.isEmpty()) {
						for (Object tmpObj : connectedTM) {
							Map tmpMap = (Map) tmpObj;
							slAlreadyConnectedTMIds.add((String) tmpMap.get(DomainConstants.SELECT_ID));
						}
					}
					if (slAlreadyConnectedTMIds != null && !slAlreadyConnectedTMIds.isEmpty()) {
						for (Object oConnectedId : slAlreadyConnectedTMIds) {
							if (UIUtil.isNotNullAndNotEmpty((String) oConnectedId)) {
								//DSM (DS) 2022x : Push context required to perform disconnect action as context user doesn't have access to perform this action
								ContextUtil.pushContext(context, PGPerfCharsConstants.PERSON_AGENT,
										DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
								isCtxtPushed = true;
								charObj.disconnect(context, relType, false,
										DomainObject.newInstance(context, (String) oConnectedId));
								ContextUtil.popContext(context);
								isCtxtPushed = false;
							}
						}
					}
				}

				StringList slTMIds = new StringList();
				StringList slTempTMIds = new StringList();
				slTempTMIds = StringUtil.split(newTestMethodIds, "|");
				String strTMID = DomainConstants.EMPTY_STRING;
				for (Object obj : slTempTMIds) {
					boolean bisObjectID = true;
					String strTempId = obj.toString();
					if (UIUtil.isNotNullAndNotEmpty(strTempId)) {
						if (!strTempId.contains(".")) {
							bisObjectID = false;
							StringList slObjSelect = new StringList();
							slObjSelect.add(DomainConstants.SELECT_LAST_ID);
							MapList objectsList = DomainObject.findObjects(context, DomainConstants.QUERY_WILDCARD, strTempId, DomainConstants.QUERY_WILDCARD, DomainConstants.QUERY_WILDCARD, null,
									null, true, slObjSelect);

							Map tempIdMap = (Map) objectsList.get(0);
							strTMID = (String) tempIdMap.get(DomainConstants.SELECT_LAST_ID);
							slTMIds.add(strTMID);
						} else {
							slTMIds.add(strTempId);
						}
					}
				}
				String[] testMethodConnectionIdArray = slTMIds.toArray(new String[slTMIds.size()]);
				if (testMethodConnectionIdArray.length > 0) {
					//DSM (DS) 2022x : Push context required to perform connect action as context user doesn't have access to perform this action
					ContextUtil.pushContext(context, PGPerfCharsConstants.PERSON_AGENT, DomainConstants.EMPTY_STRING,
							DomainConstants.EMPTY_STRING);
					isCtxtPushed = true;
					charObj.addRelatedObjects(context, relType, false, testMethodConnectionIdArray);
					ContextUtil.popContext(context);
					isCtxtPushed = false;
				}
			}
		} catch (Exception ex) {
			
		} finally {
			if (isCtxtPushed)
			ContextUtil.popContext(context);
		}
	}
	//Methods related to 'Edit' operation : End
	
	//Methods related to 'Create or Insert New PC' operation : Start
    /**
	 * DSO : Overriding existing method to handle new row addition for Performance Characteristic Table
	 * 
	 * Method copied from emxCPNCharacteristicList:connectSharedTableCharacteristic
	 * 
	 * @param context
	 * @param args
	 * @return List
	 * @throws exception   
	 */	     
	public JsonObjectBuilder connectSharedTableCharacteristic(Context context, String parentObjId, String strSelectedTable, JsonObject jsonDataObj) throws Exception{
		
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		
		//DSM (DS) 2015x.4 ALM 14407 Perf Spec Import must connect latest released TM and TMRD if it exists - START
		StringList slTestResultList = null;
		String strTMCurrentState = DomainConstants.EMPTY_STRING;
		String strReleasedTM = DomainConstants.EMPTY_STRING;
		String strNonReleasedTM = DomainConstants.EMPTY_STRING;
		String strTMRDCurrentState = DomainConstants.EMPTY_STRING;
		String strReleasedTMRD = DomainConstants.EMPTY_STRING;
		String strNonReleasedTMRD = DomainConstants.EMPTY_STRING;
		String objID = DomainConstants.EMPTY_STRING;
		String strObjWhere = "latest == TRUE";
		String sRelId = "";
		DomainRelationship dr = null;
		DomainObject newObj = null;
		
		//DSM (DS) 2015x.4 ALM 14407 Perf Spec Import must connect latest released TM and TMRD if it exists - END
		//DSM 2015x.1 - Added for Import SB feature - End

				try {
					//DSO 2013x.4  Changes added for Routine Release Edit- Start
					boolean bskipedit = false;
					//DSO 2013x.4  Changes added for Routine Release Edit- End

					DomainObject parentObj = DomainObject.newInstance(context, parentObjId);
					strSelectedTable = strSelectedTable.substring(strSelectedTable.lastIndexOf('~')+1,strSelectedTable.length());
					
					//Create PC object : Start
						String strTypeSym = EnoviaResourceBundle.getProperty(context, "emxCPN.Characteristic.table."+strSelectedTable);
						String strCharacteristicType = PropertyUtil.getSchemaProperty(context,strTypeSym);
						String strCharacteristicRel = FormulationRelationship.CHARACTERISTIC.getRelationship(context);
						
						StringList slParentObjSelects = new StringList();
						slParentObjSelects.add(DomainConstants.SELECT_TYPE);
						slParentObjSelects.add(PGPerfCharsConstants.SELECT_ATTR_PGORIGINATINGSOURCE);
						Map<?,?> mpParentInfoMap = parentObj.getInfo(context, slParentObjSelects);
						
						String strType = (String) mpParentInfoMap.get(DomainConstants.SELECT_TYPE);
						String strOriginatingSource = (String) mpParentInfoMap.get(PGPerfCharsConstants.SELECT_ATTR_PGORIGINATINGSOURCE);
						
						DomainObject doChar= (DomainObject) DomainObject.newInstance(context);
						//Added for SmartScope development :: START
						String strTypePRPProduct = PropertyUtil.getSchemaProperty(context,"type_pgProductPlatform");
						
						if(jsonDataObj.containsKey("Path") && strTypePRPProduct.equals(strType))
						{
							String strPRPPathId = jsonDataObj.getString("Path"); //(String)columnsMap.get("Path");
							StringList strNewValueList = FrameworkUtil.split(strPRPPathId, ",");
							int iNewValueListSize = strNewValueList.size();
							RelToRelUtil relTorelUtil = null;
							//Create a new Characteristic
							doChar.createObject(context, strCharacteristicType, null, "", CPNCommonConstants.POLICY_CHARACTERISTIC, null);
							String strCharObjId = doChar.getObjectId(context);
							String strConfOptionsRelId = null;
							for(int count=0; count<iNewValueListSize; count++)
							{
								strConfOptionsRelId = (String)strNewValueList.get(count);
								relTorelUtil = new RelToRelUtil();
								dr = new DomainRelationship(relTorelUtil.connect(context,CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC,strConfOptionsRelId,strCharObjId,false,true)); 
							}
						} else {
							//Create and Connect Perf Char object
							dr = doChar.createAndConnect(context,strCharacteristicType,strCharacteristicRel, parentObj, true);
						}
						
						//FSD_ChangeManagement_and_Release_Process sec 1.9.9- START
						if(strCharacteristicRel.equals(CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC))
						{
							dr.setAttributeValue(context, PGPerfCharsConstants.ATTR_PG_INHERITANCE_TYPE, "Local") ;
						}
						//FSD_ChangeManagement_and_Release_Process sec 1.9.9- END
						StringList relSelects = new StringList(4);
						relSelects.add(DomainRelationship.SELECT_TO_ID);
						relSelects.add(DomainRelationship.SELECT_FROM_ID);
						relSelects.add(DomainRelationship.SELECT_ID);
												
						java.util.Hashtable relData = dr.getRelationshipData(context, relSelects);
						StringList SLtmp = (StringList) relData.get(DomainRelationship.SELECT_TO_ID);
						objID = (String)SLtmp.get(0);
						newObj = DomainObject.newInstance(context, objID);

						SLtmp = (StringList) relData.get(DomainRelationship.SELECT_ID);
						sRelId = (String)SLtmp.get(0);
					
					String newFindNumberValue = "";
					if(jsonDataObj.containsKey(PGPerfCharsConstants.KEY_REL_ATTRIBUTE)) {
						JsonArray jsonRelAttrArray = jsonDataObj.getJsonArray(PGPerfCharsConstants.KEY_REL_ATTRIBUTE);
						int iRelAttrSize = jsonRelAttrArray.size();
						for(int i=0; i<iRelAttrSize; i++) {
							JsonObject jsonRelAttrObj = jsonRelAttrArray.getJsonObject(i);
							if(jsonRelAttrObj.containsKey(PGPerfCharsConstants.ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE)) {
								newFindNumberValue= jsonRelAttrObj.getString(PGPerfCharsConstants.ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE);
								try
								{
									int nS = Integer.parseInt(newFindNumberValue);
								}
								catch(Exception e)
								{
									String msg = EnoviaResourceBundle.getProperty(context, "emxCPNStringResource",
											context.getLocale(), "emxCPN.Alert.Column.ValidateSequenceValue"); 
									logger.log(Level.SEVERE, PGPerfCharsConstants.EXCEPTION_MESSAGE_PERF_CHAR_CREATE_EDIT_UTIL, e);
									jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, msg);
									jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
									
									return jsonReturnObj;
								}
								try
								{
									Map<String,String> hmRelAttributesMap = new HashMap<>();
									hmRelAttributesMap.put(PGPerfCharsConstants.ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE, newFindNumberValue);
									DomainRelationship.setAttributeValues(context,sRelId,hmRelAttributesMap);
								}
								catch(Exception e)
								{
			      					
								}
							}
						}
					}
					
					if(jsonDataObj.containsKey(PGPerfCharsConstants.KEY_ATTRIBUTE)) {
						JsonObject jsonAttributes = jsonDataObj.getJsonObject(PGPerfCharsConstants.KEY_ATTRIBUTE);
						for (Entry<?, ?> entry : jsonAttributes.entrySet()) {
							bskipedit = false;
							String strAttrName = (String) entry.getKey();
							newFindNumberValue = jsonAttributes.getString(strAttrName);
							if(newFindNumberValue == null) {
								newFindNumberValue = "";
							}

							try
							{

								if (strAttrName.equalsIgnoreCase(PGPerfCharsConstants.ATTR_PG_LOWERROUTINE_RELEASELIMIT)
												|| strAttrName.equalsIgnoreCase(PGPerfCharsConstants.ATTR_PG_UPPERROUTINE_RELEASELIMIT)) {
											bskipedit = (boolean) breleaseRouetineEdit(context, newObj);
								}
								if (bskipedit){
									//Do not Edit the Attribute as its already set via trigger 
								} else {
									if(!(PGPerfCharsConstants.ATTR_PG_RELEASECRITERIA.equals(strAttrName) && UIUtil.isNullOrEmpty(newFindNumberValue)))
									{
										newObj.setAttributeValue(context,strAttrName,newFindNumberValue) ;
									}
								}
							}
							catch(Exception e)
							{
								
							}
						}
					}
					
					if(jsonDataObj.containsKey(PGPerfCharsConstants.KEY_CONNECTIONS)) {
						JsonArray jsonUpdateProgArray = jsonDataObj.getJsonArray(PGPerfCharsConstants.KEY_CONNECTIONS);
						
						int iDataSize = jsonUpdateProgArray.size();
						for(int i=0; i<iDataSize; i++) {
							JsonObject jsonUpdateProg = jsonUpdateProgArray.getJsonObject(i);
							String strColName = jsonUpdateProg.getString("name");
							if(jsonUpdateProg.containsKey("names")) {
								newFindNumberValue = jsonUpdateProg.getString("names");
								if(UIUtil.isNullOrEmpty(newFindNumberValue)) {
									newFindNumberValue = jsonUpdateProg.getString("ids");
								}
							} else {
								newFindNumberValue = jsonUpdateProg.getString("ids");
							}

							if (strColName.contains("TestMethodName")) {
								String strConfType = "";
								if(PGPerfCharsConstants.ORIGINATING_SOURCE_DSO.equals(strOriginatingSource)) {
									strConfType = PropertyUtil.getSchemaProperty(context,"type_TestMethodSpecification");
								} else {
									strConfType = PropertyUtil.getSchemaProperty(context,"type_TestMethod");
								}
								
								DomainObject charObj = DomainObject.newInstance(context, objID);
								//get already connected Test Method Ids
								StringList objectSel = new StringList();
								objectSel.add(DomainConstants.SELECT_ID);

								MapList connectedTM = charObj.getRelatedObjects(context,
										DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT, strConfType, objectSel, null, true, false,
										(short) 1, null, null, 0);
								
								StringList slTMIds = new StringList();
								StringList slTempTMIds = new StringList();
								slTempTMIds= StringUtil.split(newFindNumberValue, "|");
								boolean isTMContPushed = false;
								try
								{
									//DSM (DS) 2022x : Push context required to get TestMethod Spec related info and add connection  as context user doesn't have access to perform this action
									ContextUtil.pushContext(context,PGPerfCharsConstants.PERSON_AGENT,DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
									isTMContPushed = true;
									
								for(Object obj : slTempTMIds)
								{
									String strTempId  = obj.toString();
									if(UIUtil.isNotNullAndNotEmpty(strTempId))
									{	
										if(!strTempId.contains("."))
										{
											String strMQLQuery = PGPerfCharsConstants.STR_MQL_CREATE_GET_TEST_METHODS;
									    	String strTest = MqlUtil.mqlCommand(context, strMQLQuery, FormulationType.TEST_METHOD_SPECIFICATION.getType(context), strTempId, DomainConstants.QUERY_WILDCARD, strObjWhere,DomainConstants.SELECT_ID,DomainConstants.SELECT_CURRENT, "|");

											slTestResultList = StringUtil.split(strTest, "\n");
											 strTMCurrentState = DomainConstants.EMPTY_STRING;
											 strReleasedTM = DomainConstants.EMPTY_STRING;
											 strNonReleasedTM = DomainConstants.EMPTY_STRING;
											 
											for(int tmCount = 0 ; tmCount < slTestResultList.size() ; tmCount++)
											{
												String strTMInfo = slTestResultList.get(tmCount);
												if(UIUtil.isNotNullAndNotEmpty(strTMInfo) && strTMInfo.indexOf("|")!=-1){
													strTMCurrentState = StringUtil.split(strTMInfo, "|").get(4).toString();
													if(PGPerfCharsConstants.STATE_RELEASE.equals(strTMCurrentState) || PGPerfCharsConstants.STATE_COMPLETE.equals(strTMCurrentState))
													{
														strReleasedTM = StringUtil.split(strTMInfo, "|").get(3).toString();
													}
													else if(!PGPerfCharsConstants.STATE_OBSOLETE.equals(strTMCurrentState))
													{
														strNonReleasedTM = StringUtil.split(strTMInfo, "|").get(3).toString();
													}
													}
											}
											if((UIUtil.isNotNullAndNotEmpty(strReleasedTM) && UIUtil.isNotNullAndNotEmpty(strNonReleasedTM)) || (UIUtil.isNotNullAndNotEmpty(strReleasedTM) && UIUtil.isNullOrEmpty(strNonReleasedTM)))
											{
												slTMIds.add(strReleasedTM);
											}
											else if(UIUtil.isNotNullAndNotEmpty(strNonReleasedTM))
											{
												slTMIds.add(strNonReleasedTM);
											}
										}
										else
										{
											slTMIds.add(strTempId);
										}
									}
								}

								}
								catch(Exception ex)
								{
									throw ex;
								}
								finally
								{
									if(isTMContPushed)
									{
										ContextUtil.popContext(context);
									}
									
								}

								for (int iLastItem = slTMIds.size()-1 ; iLastItem >= 0 ; iLastItem--)
								{
									String strNewId = slTMIds.get(iLastItem);
									boolean isAlreadyConnected = false;
									if(connectedTM != null && !connectedTM.isEmpty())
									{
										Loop : for (int iConnectedId=0 ; iConnectedId < connectedTM.size() ; iConnectedId++)
										{
											Map mpTM = (Map)connectedTM.get(iConnectedId);
											String strConnectedId = (String) mpTM.get(DomainConstants.SELECT_ID);
											if(strConnectedId.equals(strNewId))
											{
												isAlreadyConnected = true;
												break Loop;
											}
										}
									}
									if(isAlreadyConnected)
									{
										slTMIds.remove(iLastItem);
									}
								}
								
								RelationshipType relType = new RelationshipType(CPNCommonConstants.RELATIONSHIP_REFERENCE_DOCUMENT);
								DomainRelationship dorel = new DomainRelationship();
								for (int iTM = 0; iTM<slTMIds.size() ; iTM++)
								{
									String strTMId = slTMIds.get(iTM).toString();
									dorel = new DomainRelationship();
									dorel = charObj.addRelatedObject(context,relType,true,strTMId);
								}
								
							} else if(strColName.contains("TestMethodRefDoc")) {
								String strConfType = PropertyUtil.getSchemaProperty(context,"type_pgTestMethodReferenceDocument");
								DomainObject charObj = DomainObject.newInstance(context,objID );
								//get already connected Test Method Ids
								StringList objectSel = new StringList();
								objectSel.add(DomainConstants.SELECT_ID);

								MapList connectedTM = charObj.getRelatedObjects(context,
										DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT, strConfType, objectSel, null, true, false,
										(short) 1, null, null, 0);
								
								StringList slTMIds = new StringList();
								StringList slTempTMIds = new StringList();
								slTempTMIds= StringUtil.split(newFindNumberValue, "|");
								String strObjType = PGPerfCharsConstants.TYPE_PG_TMRD_TYPES;
								// DSM (DS) 2018x.5 31649 ALM Cannot import Performance Characteristic excel file into test data - STARTS
								boolean isTMRDContPushed = false;
								try
								{
									ContextUtil.pushContext(context,PGPerfCharsConstants.PERSON_AGENT,DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
									isTMRDContPushed = true;
								// DSM (DS) 2018x.5 31649 ALM Cannot import Performance Characteristic excel file into test data - ENDS
								for(Object obj : slTempTMIds)
								{
									String strTempId  = obj.toString();
									if(UIUtil.isNotNullAndNotEmpty(strTempId))
									{	
										if(!strTempId.contains("."))
										{
											//DSM (DS) 2015x.4 ALM 14407 Perf Spec Import must connect latest released TM and TMRD if it exists - START
											String strMQLQuery = PGPerfCharsConstants.STR_MQL_CREATE_GET_TEST_METHODS;
											String strTest = MqlUtil.mqlCommand(context, strMQLQuery, strObjType, strTempId, DomainConstants.QUERY_WILDCARD,strObjWhere,DomainConstants.SELECT_ID,DomainConstants.SELECT_CURRENT, "|");
											slTestResultList = StringUtil.split(strTest, "\n");
											 strTMRDCurrentState = DomainConstants.EMPTY_STRING;
											 strReleasedTMRD = DomainConstants.EMPTY_STRING;
											 strNonReleasedTMRD = DomainConstants.EMPTY_STRING;
											for(int tmCount = 0 ; tmCount < slTestResultList.size() ; tmCount++)
											{
												String strTMRDInfo = slTestResultList.get(tmCount);
												if(UIUtil.isNotNullAndNotEmpty(strTMRDInfo) && strTMRDInfo.indexOf("|")!=-1){
													strTMRDCurrentState = StringUtil.split(strTMRDInfo, "|").get(4).toString();
													if(PGPerfCharsConstants.STATE_RELEASE.equals(strTMRDCurrentState) || PGPerfCharsConstants.STATE_COMPLETE.equals(strTMRDCurrentState))
													{
														strReleasedTMRD = StringUtil.split(strTMRDInfo, "|").get(3).toString();
													}
													else if(!PGPerfCharsConstants.STATE_OBSOLETE.equals(strTMRDCurrentState))
													{
														strNonReleasedTMRD = StringUtil.split(strTMRDInfo, "|").get(3).toString();
													}
													}
											}
											if((UIUtil.isNotNullAndNotEmpty(strReleasedTMRD) && UIUtil.isNotNullAndNotEmpty(strNonReleasedTMRD)) || (UIUtil.isNotNullAndNotEmpty(strReleasedTMRD) && UIUtil.isNullOrEmpty(strNonReleasedTMRD)) )
											{
												slTMIds.add(strReleasedTMRD);
											}
											else if(UIUtil.isNotNullAndNotEmpty(strNonReleasedTMRD))
											{
												slTMIds.add(strNonReleasedTMRD);
											}
										}
										else
										{
											slTMIds.add(strTempId);
										}
										//DSM 2015x.1 :  -Performance Characteristics not saving Test Method selection END
									}
								}
								// DSM (DS) 2018x.5 31649 ALM Cannot import Performance Characteristic excel file into test data - STARTS
								}
								catch(Exception e)
								{
									throw e;
								}
								finally
								{
									if(isTMRDContPushed)
									{
										ContextUtil.popContext(context);
									}
										
								}
								// DSM (DS) 2018x.5 31649 ALM Cannot import Performance Characteristic excel file into test data - ENDS
								/*DSM 2015x.1 :  -Performance Characteristics not saving Test Method selection : END*/
								for (int iLastItem = slTMIds.size()-1 ; iLastItem >= 0 ; iLastItem--)
								{
									String strNewId = slTMIds.get(iLastItem).toString();
									boolean isAlreadyConnected = false;
									if(connectedTM != null && !connectedTM.isEmpty())
									{
										Loop : for (int iConnectedId=0 ; iConnectedId < connectedTM.size() ; iConnectedId++)
										{
											Map mpTM = (Map)connectedTM.get(iConnectedId);
											String strConnectedId = (String) mpTM.get(DomainConstants.SELECT_ID);
											if(strConnectedId.equals(strNewId))
											{
												isAlreadyConnected = true;
												break Loop;
											}
										}
									}
									if(isAlreadyConnected)
									{
										slTMIds.remove(iLastItem);
									}
								}

								RelationshipType relType = new RelationshipType(CPNCommonConstants.RELATIONSHIP_REFERENCE_DOCUMENT);
								DomainRelationship dorel = new DomainRelationship();
								for (int iTM = 0; iTM<slTMIds.size() ; iTM++)
								{
									String strTMId = slTMIds.get(iTM).toString();
									dorel = new DomainRelationship();
									dorel = charObj.addRelatedObject(context,relType,true,strTMId);
								}
								//DSO 2015x.1 : Fix for Performance Characteristics TM and TMRD columns expected behaviour - End
							}
						}
					}

				}
			catch(Exception e){
				logger.log(Level.SEVERE, PGPerfCharsConstants.EXCEPTION_MESSAGE_PERF_CHAR_CREATE_EDIT_UTIL, e);
				jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
				jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			}
			//}//columns for
			//}
		//}

		return jsonReturnObj;
	}

	/**
	 * This method will check if the Upper and lower release limit whether it should skip the edit of upper and Lower Routine release limits
	 * 
	 *  Method copied from emxCPNCharacteristicList:breleaseRouetineEdit
	 * 
	 * @param context
	 * @param newobjectid
	 * @return
	 * @throws Exception
	 */
	public boolean breleaseRouetineEdit (Context context, DomainObject newobjectid) throws Exception
	{
		boolean bskipEdit = false;
		try{
			//Property file Entry
			String strrelsymbname 			 = FrameworkProperties.getProperty(context, "emxCPN.ProductDataCreation.PerformanceCharacteristic.relcharacteristicsymbname");
			String strAllowedTypeList 		 = FrameworkProperties.getProperty(context, "emxCPN.ProductDataCreation.PerformanceCharacteristic.ValidateTypeforRoutineReleaseupdate");
			StringList AllowedTypeList	= FrameworkUtil.split(strAllowedTypeList, "|");
			//Form the Selectable
			StringList slObjectSelect = new StringList();
			slObjectSelect.add(DomainConstants.SELECT_TYPE);
			slObjectSelect.add("relationship["+PropertyUtil.getSchemaProperty(context, strrelsymbname)+"].from.type");

			slObjectSelect.add("relationship["+PropertyUtil.getSchemaProperty(context, strrelsymbname)+"].from.attribute["+ PGPerfCharsConstants.ATTR_PGORIGINATINGSOURCE+"]");

			Hashtable htobjInfo = (Hashtable)newobjectid.getInfo(context, slObjectSelect);

			String strOriginatingSource=(String)htobjInfo.get("relationship["+PropertyUtil.getSchemaProperty(context, strrelsymbname)+"].from.attribute[" + PGPerfCharsConstants.ATTR_PGORIGINATINGSOURCE + "]");
			String strconnectedType= (String)htobjInfo.get("relationship["+PropertyUtil.getSchemaProperty(context, strrelsymbname)+"].from.type");
			//validate if the edit is allowed
			String strObjectType=(String)htobjInfo.get(DomainConstants.SELECT_TYPE);
			String strType = FrameworkUtil.getAliasForAdmin(context, "type", strconnectedType, true);
			strObjectType = FrameworkUtil.getAliasForAdmin(context, "type", strObjectType, true);

			if (UIUtil.isNotNullAndNotEmpty(strType) && AllowedTypeList.contains(strType) && "DSO".equalsIgnoreCase(strOriginatingSource) && "type_pgPerformanceCharacteristic".equalsIgnoreCase(strObjectType))
			{
				bskipEdit=true;
			}
		} catch (Exception e){
			
		}
		return bskipEdit;
	}
	
	//Methods related to 'Create or Insert New PC' operation : End
	
	// TM Specifics Validation 
	/**
	 * Method to Validate Nexus Test Method
	 * @param context
	 * @param jsonDataObj
	 * @throws Exception 
	 */
    private String validateNexusTestMethod(Context context, JsonArray jsonDataArray,String strParentId,int iDataSize) throws Exception {
    	String sValidateMsg =DomainObject.EMPTY_STRING;
    	StringBuffer sbFinalError = new StringBuffer();
    	String strPerfCharId =DomainObject.EMPTY_STRING;
		String strTempReturn = DomainObject.EMPTY_STRING;
    	for(int i=0; i<iDataSize; i++) {
			JsonObject jsonDataObj = jsonDataArray.getJsonObject(i);
			String strOperation = jsonDataObj.getString(PGPerfCharsConstants.KEY_OPERATION);
			if(PGPerfCharsConstants.KEY_EDIT.equals(strOperation) || PGPerfCharsConstants.KEY_CREATE.equals(strOperation))
			{
				if(PGPerfCharsConstants.KEY_EDIT.equals(strOperation))
				{
					strPerfCharId = jsonDataObj.getString(DomainConstants.SELECT_ID);
				}
				sValidateMsg = validateNexusTestMethod(context, jsonDataObj);
				
				if(UIUtil.isNotNullAndNotEmpty(sValidateMsg)) {
					sbFinalError.append(sValidateMsg);  //  return this for error message once no mapping is resolved
				}
				
			}
    	}
       	return strTempReturn;
    }
    
    private String getSequenceNum(Context context,JsonObject jsonDataObj)throws Exception {
    	String strSequenceNum = "";
	    if(jsonDataObj.containsKey(PGPerfCharsConstants.KEY_REL_ATTRIBUTE)) {
			JsonArray jsonRelAttrArray = jsonDataObj.getJsonArray(PGPerfCharsConstants.KEY_REL_ATTRIBUTE);
			int iRelAttrSize = jsonRelAttrArray.size();
			for(int i=0; i<iRelAttrSize; i++) {
				JsonObject jsonRelAttrObj = jsonRelAttrArray.getJsonObject(i);
				if(jsonRelAttrObj.containsKey(PGPerfCharsConstants.ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE)) {
					 strSequenceNum = jsonRelAttrObj.getString(PGPerfCharsConstants.ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE);
				}
			}
		}
	    return strSequenceNum;
    }
    private String validateNexusTestMethod(Context context,JsonObject jsonDataObj)throws Exception 
    {
    	String sValidateMsg = "";
    	String strErrMsg = "";
    	String strValidateMsg ="";
    	StringBuffer sbValidateMsgPart =new StringBuffer();
    	String sMandatoryAttributes = "";
    	String strPGTestMethodSpecifics;
    	String strTMTestGroup;
    	String strTMPlantTestinglevel;
    	String strPCReportType;
    	String strTMNexusParameterId;
    	String strSequenceNum;
    	String strAttributes =DomainObject.EMPTY_STRING; 
    	StringList slTestMethods = new StringList();
    	StringList slGetMsg = new StringList();
    	StringList slMandatoryAttirbs;
    	Map mDataToValidate = new HashMap();
    	MapList mlReturn = new MapList();
    	PGFetchNexusTMSpecificsPerfChars pgFetchNexusTMSpecificsPerfChars = new PGFetchNexusTMSpecificsPerfChars();

    	if(jsonDataObj.containsKey(PGPerfCharsConstants.KEY_ATTRIBUTE)) 
    	{
    			JsonObject jsonAttributes = jsonDataObj.getJsonObject(PGPerfCharsConstants.KEY_ATTRIBUTE);
    			Map<String, String> mpAttributeInfoMap = getAttributeMapFromJson(jsonAttributes);
    			if(jsonAttributes.containsKey(PGPerfCharsConstants.ATTR_PG_METHOD_SPECIFICS) && jsonAttributes.containsKey(PGPerfCharsConstants.ATTR_PG_NEXUS_PC_PARAMETER_ID)) 				
				{
    				mDataToValidate=getAttributeMapFromJson(jsonAttributes);
    				 strTMNexusParameterId = jsonAttributes.getString(PGPerfCharsConstants.ATTR_PG_NEXUS_PC_PARAMETER_ID);  
    				 strSequenceNum = getSequenceNum(context,jsonDataObj);
    				 if(FrameworkUtil.isObjectId(context, strTMNexusParameterId))
    			    {
    				sValidateMsg = pgFetchNexusTMSpecificsPerfChars.getValidateResultForTestMethods( context, mDataToValidate,"widget");
	    				 if(UIUtil.isNotNullAndNotEmpty(sValidateMsg))
	    				 {
	    					 strValidateMsg = getErrorTMValidation(sValidateMsg,jsonAttributes);
	    					 if(UIUtil.isNotNullAndNotEmpty(strValidateMsg))
		    				 {
	    						 sbValidateMsgPart.append(strSequenceNum);
	    						 sbValidateMsgPart.append(":");
	    						 sbValidateMsgPart.append(strValidateMsg);
	    						 sbValidateMsgPart.append(" ");
	    						 sbValidateMsgPart.append("\n");
	    						 sbValidateMsgPart.append(" ");
		    				 }
	    			    }
    			    }
    			}
    	}
    	return sbValidateMsgPart.toString();
    }

	private String getErrorTMValidation(String sValidateMsg, JsonObject jsonAttributes) {
		String sMandatoryAttributes;
		StringList slMandatoryAttirbParts;
		StringList slMandatoryAttribute;
		String strCheckAttribute = DomainConstants.EMPTY_STRING;
		String strAttributeMsg = DomainConstants.EMPTY_STRING;
		String strTempError =DomainConstants.EMPTY_STRING;
		StringList slMandatoryAttirbsOne;
		String sMsgOne = DomainConstants.EMPTY_STRING;
		StringList slValidateMsg = new StringList();
		StringBuffer sbValidateMsg = new StringBuffer();
		StringBuffer sbValidateMsgPart = new StringBuffer();
		if(UIUtil.isNotNullAndNotEmpty(sValidateMsg))
		{
			 if(!sValidateMsg.contains("\n") && !sValidateMsg.contains("|"))
			 {
				 sbValidateMsg.append(sValidateMsg);
			 }else if(sValidateMsg.contains("\n")) {
				 slMandatoryAttirbParts = StringUtil.split(sValidateMsg, "\n");
				 for(int i=0;i<slMandatoryAttirbParts.size();i++)
				 {
					 
					 sMsgOne = slMandatoryAttirbParts.get(i);
					 if(sMsgOne.contains("|"))
					 {
						 slMandatoryAttirbsOne = StringUtil.split(sMsgOne, "|");
						 strCheckAttribute = (slMandatoryAttirbsOne.get(0)).replace("\n","").trim();
						 strAttributeMsg = slMandatoryAttirbsOne.get(1);
					 }else {
						 sbValidateMsg.append(sMsgOne);
					 }
				}
			 }
			if(UIUtil.isNotNullAndNotEmpty(strAttributeMsg) && ("is mandatory".equalsIgnoreCase(strAttributeMsg) || "Blank and non-editable".equalsIgnoreCase(strAttributeMsg)))
			{
				slMandatoryAttribute = StringUtil.split(strCheckAttribute, ",");
				for(int iAttribute =0; iAttribute<slMandatoryAttribute.size(); iAttribute++)
				{
					if(jsonAttributes.containsKey(slMandatoryAttribute.get(iAttribute)))  // add this for error no mapping for pgUpperSpecificationLimit
					{
					sMandatoryAttributes = jsonAttributes.getString(slMandatoryAttribute.get(iAttribute));  
					if(UIUtil.isNotNullAndNotEmpty(sMandatoryAttributes) &&  "Blank and non-editable".equalsIgnoreCase(strAttributeMsg))
					{
						if(UIUtil.isNullOrEmpty(strTempError))
						{
							strTempError = slMandatoryAttribute.get(iAttribute);
						} else {
							strTempError = strTempError + "," + slMandatoryAttribute.get(iAttribute);
						}
						
					}else if(UIUtil.isNullOrEmpty(sMandatoryAttributes) && "is mandatory".equalsIgnoreCase(strAttributeMsg)){
						sbValidateMsgPart.append(slMandatoryAttribute.get(iAttribute));
						}
					}
				}
				sbValidateMsgPart.append(strTempError);	
				
				if(UIUtil.isNotNullAndNotEmpty(sbValidateMsgPart.toString()))
				{
					sbValidateMsgPart.append(" ");
					sbValidateMsgPart.append(!((sbValidateMsg.toString()).contains(strAttributeMsg)) ? strAttributeMsg : "");
					if(UIUtil.isNotNullAndNotEmpty(sbValidateMsg.toString()))
						sbValidateMsg.append("|");
				}
			}
			 sbValidateMsg.append(sbValidateMsgPart);
		 }
		return sbValidateMsg.toString();
	} 
    
		
    
	
}

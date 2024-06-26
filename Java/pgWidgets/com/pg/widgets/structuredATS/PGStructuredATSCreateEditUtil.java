package com.pg.widgets.structuredats;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Hashtable;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import matrix.db.Context;
import matrix.db.BusinessInterface;
import matrix.db.Vault;
import matrix.util.Pattern;
import matrix.util.StringList;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainConstants;
import javax.ws.rs.core.Response;
import javax.servlet.http.HttpServletResponse;

import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.widgets.util.PGWidgetConstants;
import com.pg.widgets.util.PGWidgetUtil;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.StringUtil;
import com.dassault_systemes.enovia.enterprisechangemgt.common.ChangeAction;
import com.dassault_systemes.enovia.enterprisechangemgt.util.ChangeUtil;


public class PGStructuredATSCreateEditUtil {

	private static final Logger logger = Logger.getLogger(PGStructuredATSCreateEditUtil.class.getName());

	static StringList slObjectSelects = new StringList();

	/**
	 * Creates the Structured ATS, updates attributes and connects the related
	 * objects with it
	 * 
	 * @param context
	 * @param mpRequestMap
	 * @return
	 * @throws Exception
	 */
	Response createStructuredAuthorizedTemporaryStandard(Context context, Map<?, ?> mpRequestMap) {
		String autoName = null;
		String strNewObjectId = null;
		JsonObjectBuilder output = Json.createObjectBuilder();
		try {
			autoName = DomainObject.getAutoGeneratedName(context,
					PGStructuredATSConstants.TYPE_PG_AUTHORIZED_TEMPORARY_SPECIFICATION,
					PGStructuredATSConstants.REVISION_NUMBER_GENERATOR);
			ContextUtil.startTransaction(context, true);
			strNewObjectId = createObject(context, PGStructuredATSConstants.TYPE_PG_STRUCTURED_ATS, autoName,
					PGStructuredATSConstants.SATS_REVISION, PGStructuredATSConstants.POLICY_PG_STRUCTURED_ATS);
			updateBasic(context, strNewObjectId, mpRequestMap);
			Map<?, ?> mpAttributeMap = (Map<?, ?>) mpRequestMap.get(PGStructuredATSConstants.KEY_ATTRIBUTES);
			
			if(mpAttributeMap.containsKey(PGStructuredATSConstants.SELECT_ATTRIBUTE_UPT_PHYID)) {
				String strAttrUPTPhyId = (String) mpAttributeMap.get(PGStructuredATSConstants.SELECT_ATTRIBUTE_UPT_PHYID);
				if(UIUtil.isNotNullAndNotEmpty(strAttrUPTPhyId)) {
					strAttrUPTPhyId = DomainObject.newInstance(context, strAttrUPTPhyId).getPhysicalId(context);
					updateUPTPhyIdByInterface(context, strNewObjectId, strAttrUPTPhyId);
				}
				mpAttributeMap.remove(PGStructuredATSConstants.SELECT_ATTRIBUTE_UPT_PHYID);
			}
			
			updateStructuredATSAttributes(context, strNewObjectId, mpAttributeMap);
			ArrayList<Map<?, ?>> alConnections = (ArrayList<Map<?, ?>>) mpRequestMap
					.get(PGStructuredATSConstants.KEY_CONNECTIONS);
			connectObjectsToStructuredATS(context, strNewObjectId, alConnections);
			createAndConnectCA(context, strNewObjectId, mpRequestMap);
			String strObjSelect = (String) mpRequestMap.get(PGStructuredATSConstants.KEY_OBJECT_SELECTS);
			updateObjectSelectList(strObjSelect);
			output = prepareResponse(context, strNewObjectId);
			ContextUtil.commitTransaction(context);
		} catch (Exception excep) {
			ContextUtil.abortTransaction(context);
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
			output.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
			return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity(output.build().toString()).build();
		}
		return Response.status(HttpServletResponse.SC_OK).entity(output.build().toString()).build();
	}

	/**
	 * Method to update the attribute 'pgUPTPhyID' via interface
	 * @param context
	 * @param strNewObjectId
	 * @param strAttrUPTPhyId
	 * @throws Exception 
	 */
	private void updateUPTPhyIdByInterface(Context context, String strNewObjectId, String strAttrUPTPhyId) throws Exception {
		 boolean isContextPushed = false;
         try {
            Vault vault = context.getVault();
            BusinessInterface pgUPTPhysicalIDExtn = new BusinessInterface("pgUPTPhysicalIDExtn", vault);
            DomainObject dobSATSObj = DomainObject.newInstance(context, strNewObjectId);

            ContextUtil.pushContext(context);
            isContextPushed = true;
			
            dobSATSObj.addBusinessInterface(context, pgUPTPhysicalIDExtn);
            dobSATSObj.setAttributeValue(context, "pgUPTPhyID", strAttrUPTPhyId);
		} catch (Exception excep) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
			throw excep;
        } finally {
            if(isContextPushed) {
                ContextUtil.popContext(context);
            }
			isContextPushed = false;
        }
		
	}

	/**
	 * Method to get the object selects details for newly created STAS object
	 * 
	 * @param context
	 * @param strNewObjectId
	 * @param strObjSelect
	 * @return
	 * @throws Exception
	 */
	private JsonObjectBuilder prepareResponse(Context context, String strNewObjectId) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		try {
			if (UIUtil.isNotNullAndNotEmpty(strNewObjectId)) {
				String strLanguage = context.getSession().getLanguage();
				slObjectSelects.remove(PGStructuredATSConstants.KEY_DISPLAY_TYPE);
				DomainObject dobSATSobj = DomainObject.newInstance(context, strNewObjectId);

				Map<?, ?> objInfoMap = dobSATSobj.getInfo(context, slObjectSelects);
				StringList slTemp;
				StringBuilder sbValues;
				String strValue = DomainConstants.EMPTY_STRING;

				for (int i = 0; i < slObjectSelects.size(); i++) {
					String strObjSelectKey = slObjectSelects.get(i);
					 Object objValue = objInfoMap.get(strObjSelectKey);
						if (objValue == null || "".equals(objValue)) {
							objValue = DomainConstants.EMPTY_STRING;
						}
						if (objValue instanceof String) {
							strValue = (String) objValue;
						} else if (objValue instanceof StringList) {
							slTemp = (StringList) objValue;
							sbValues = new StringBuilder();
							for (int j = 0; j < slTemp.size(); j++) {
								if (UIUtil.isNotNullAndNotEmpty(slTemp.get(j))) {
									sbValues.append(slTemp.get(j));
								}
								if (j != slTemp.size() - 1) {
									sbValues.append(PGWidgetConstants.KEY_COMMA_SEPARATOR);
								}
							}
							strValue = sbValues.toString();
						}
					
					output.add(strObjSelectKey, strValue);
				}
				String strType = (String) objInfoMap.get(DomainConstants.SELECT_TYPE);
				String strTypeDisplayName = EnoviaResourceBundle.getAdminI18NString(context,
						PGStructuredATSConstants.STR_SCHEMA_TYPE, strType, strLanguage);
				if (UIUtil.isNullOrEmpty(strTypeDisplayName)) {
					strTypeDisplayName = strType;
				}
				output.add(PGStructuredATSConstants.KEY_DISPLAY_TYPE, strTypeDisplayName);
			}

		} catch (Exception excep) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
			throw excep;
		}
		return output;
	}

	/**
	 * Method to form the object select list
	 * 
	 * @param strObjSelects
	 */
	private void updateObjectSelectList(String strObjSelects) {

		slObjectSelects.add(DomainConstants.SELECT_TYPE);
		slObjectSelects.add(DomainConstants.SELECT_NAME);
		slObjectSelects.add(DomainConstants.SELECT_REVISION);

		StringList slObjSelectArgList = StringUtil.split(strObjSelects, ",");
		for (int i = 0; i < slObjSelectArgList.size(); i++) {
			String strObjSelect = slObjSelectArgList.get(i);
			if (!slObjectSelects.contains(strObjSelect)) {
				slObjectSelects.add(strObjSelect);
			}
		}
	}

	/**
	 * Updates Attributes values on new Object created
	 * 
	 * @param context
	 * @param strNewObjectId
	 * @param mpAttribute
	 * @return
	 * @throws Exception
	 */
	public void updateStructuredATSAttributes(Context context, String strNewObjectId, Map<?, ?> mpAttribute) {
		try {
			Map<String, String> attrMap = new HashMap<>();
			String strAttributeName = null;
			String strAttributeValue = null;
			String strAttributeWidgetName = null;
			DomainObject dObjSATS = null;
			if (null != mpAttribute && mpAttribute.size() > 0) {
				for (Map.Entry<?, ?> entry : mpAttribute.entrySet()) {
					strAttributeWidgetName = (String) entry.getKey();
					strAttributeName = strAttributeWidgetName.substring(strAttributeWidgetName.indexOf("[") + 1,
							strAttributeWidgetName.lastIndexOf("]"));
					strAttributeValue = (String) entry.getValue();
					if ((UIUtil.isNotNullAndNotEmpty(strAttributeName)
							&& UIUtil.isNotNullAndNotEmpty(strAttributeValue))) {
						attrMap.put(strAttributeName, strAttributeValue);
					}
				}
			attrMap.put(PGStructuredATSConstants.ATTRIBUTE_PG_ORIGINATING_SOURCE, PGStructuredATSConstants.CONST_DSO);
			attrMap.put(PGStructuredATSConstants.ATTRIBUTE_RELEASE_PHASE, PGStructuredATSConstants.PRODUCTION);
			}
			if (!attrMap.isEmpty()) {
				dObjSATS = DomainObject.newInstance(context, strNewObjectId);
				dObjSATS.setAttributeValues(context, attrMap);
			}
		} catch (Exception excep) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
		}
	}

	/**
	 * Connects Object to new Object created
	 * 
	 * @param context
	 * @param strNewObjectId
	 * @param alConnections
	 * @return
	 * @throws Exception
	 */
	public void connectObjectsToStructuredATS(Context context, String strNewObjectId,
			ArrayList<Map<?, ?>> alConnections) {
		String strObjId = null;
		Boolean bIsFrom = null;
		String strRelName = null;
		String strAction = null;
		try {
			if (null != alConnections && !alConnections.isEmpty()) {
				for (Map<?, ?> mConnectionInfo : alConnections) {
					strObjId = (String) mConnectionInfo.get(DomainConstants.SELECT_ID);
					bIsFrom = (Boolean) mConnectionInfo.get(PGStructuredATSConstants.KEY_IS_FROM);
					strRelName = (String) mConnectionInfo.get(PGStructuredATSConstants.KEY_REL_TYPE);
					strAction = (String) mConnectionInfo.get(PGStructuredATSConstants.KEY_ACTION);
					if ((UIUtil.isNotNullAndNotEmpty(strAction)
							&& strAction.equalsIgnoreCase(PGStructuredATSConstants.ACTION_CONNECT))) {
						if (bIsFrom)
							connectObjects(context, strObjId, strNewObjectId, strRelName);
						else
							connectObjects(context, strNewObjectId, strObjId, strRelName);
					}
				}
			}
		} catch (Exception excep) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
		}
	}

	/**
	 * This is Util Method to connects objects
	 * 
	 * @param context
	 * @param strFromObjectId
	 * @param strToObjectId
	 * @param Relationship    Name
	 * @return
	 * @throws Exception
	 */
	public DomainRelationship connectObjects(Context context, String strFromObjID, String strToObjID,
			String strRelationshipName) throws Exception {
		DomainObject dObjFromObject = null;
		DomainObject dObjToObject = null;
		DomainRelationship domRelationship = null;
		try {
			if (UIUtil.isNotNullAndNotEmpty(strFromObjID) && UIUtil.isNotNullAndNotEmpty(strToObjID)
					&& UIUtil.isNotNullAndNotEmpty(strRelationshipName)) {
				dObjFromObject = DomainObject.newInstance(context, strFromObjID);
				dObjToObject = DomainObject.newInstance(context, strToObjID);
				domRelationship = DomainRelationship.connect(context, dObjFromObject, strRelationshipName,
						dObjToObject);
			}
		} catch (Exception excep) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
			throw excep;
		}
		return domRelationship;
	}

	/**
	 * creates new Object
	 * 
	 * @param context
	 * @param strType
	 * @param strName
	 * @param strRevision
	 * @param strPolicy
	 * @return
	 * @throws Exception
	 */
	public String createObject(Context context, String strType, String strName, String strRevision, String strPolicy) {
		String strObjectId = null;
		try {
			DomainObject dObj = DomainObject.newInstance(context);
			dObj.createObject(context, strType, strName, strRevision, strPolicy, null);
			strObjectId = dObj.getObjectId(context);
		} catch (Exception excep) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
		}
		return strObjectId;
	}

	/**
	 * updates Description on the Object
	 * 
	 * @param context
	 * @param strObjectId
	 * @param strDescription
	 * @return
	 * @throws Exception
	 */
	public void updateBasic(Context context, String strObjectId, Map<?, ?> mRequestMap) {
		DomainObject dObj = null;
		String strDescription = null;
		String strOwner = null;
		try {
			dObj = DomainObject.newInstance(context, strObjectId);
			strDescription = (String) mRequestMap.get(DomainConstants.SELECT_DESCRIPTION);
			strOwner = context.getUser();
			if (UIUtil.isNotNullAndNotEmpty(strDescription))
				dObj.setDescription(context, strDescription);
			if (UIUtil.isNotNullAndNotEmpty(strOwner)) {
				dObj.setOwner(context, strOwner);
				dObj.setAttributeValue(context, DomainConstants.ATTRIBUTE_ORIGINATOR, strOwner);
			}
		} catch (Exception excep) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
		}
	}

	/**
	 * edits the Structured ATS attributes and connections
	 * 
	 * @param context
	 * @param mpRequestMap
	 * @return
	 * @throws Exception
	 */
	Response editStructuredAuthorizedTemporaryStandard(Context context, Map<?, ?> mpRequestMap) {
		String strObjectId = null;
		DomainObject dObj = null;
		JsonObjectBuilder jsonData = Json.createObjectBuilder();
		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonArrayBuilder jsonArrObjInfo = Json.createArrayBuilder();
		try {
			ContextUtil.startTransaction(context, true);
			strObjectId = (String) mpRequestMap.get(DomainConstants.SELECT_ID);
			dObj = DomainObject.newInstance(context, strObjectId);
			String strDesc = (String) mpRequestMap.get(DomainConstants.SELECT_DESCRIPTION);
			if (UIUtil.isNotNullAndNotEmpty(strDesc))
				dObj.setDescription(context, strDesc);
			Map<?, ?> attrMap = (Map<?, ?>) mpRequestMap.get(PGStructuredATSConstants.KEY_ATTRIBUTES);
			
			if(attrMap.containsKey(PGStructuredATSConstants.SELECT_ATTRIBUTE_UPT_PHYID)) {
				String strAttrUPTPhyId = (String) attrMap.get(PGStructuredATSConstants.SELECT_ATTRIBUTE_UPT_PHYID);
				attrMap.remove(PGStructuredATSConstants.SELECT_ATTRIBUTE_UPT_PHYID);
				updateUPTPhyIdByInterface(context, strObjectId, strAttrUPTPhyId);
			}
			
			editStructuredATSAttributes(context, strObjectId, attrMap);
			ArrayList<Map<?, ?>> alConnectionList = (ArrayList<Map<?, ?>>) mpRequestMap
					.get(PGStructuredATSConstants.KEY_CONNECTIONS);
			editStructuredATSConnections(context, strObjectId, alConnectionList);
			String strObjSelect = (String) mpRequestMap.get(PGStructuredATSConstants.KEY_OBJECT_SELECTS);
			editCAConnections(context, mpRequestMap);
			updateObjectSelectList(strObjSelect);
			output = prepareResponse(context, strObjectId);
			jsonArrObjInfo.add(output);
			jsonData.add(PGStructuredATSConstants.KEY_DATA, jsonArrObjInfo);
			ContextUtil.commitTransaction(context);
		} catch (Exception excep) {
			ContextUtil.abortTransaction(context);
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
			output.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
			return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity(output.build().toString()).build();
		}
		return Response.status(HttpServletResponse.SC_OK).entity(jsonData.build().toString()).build();
	}

	/**
	 * Edits Attributes values on new Object created
	 * 
	 * @param context
	 * @param strNewObjectId
	 * @param mpAttribute
	 * @return
	 * @throws Exception
	 */
	public void editStructuredATSAttributes(Context context, String strObjectId, Map<?, ?> mpAttribute)
			throws Exception {
		try {
			Map<String, String> attrMap = new HashMap<>();
			String strAttributeValue = null;
			String strAttributeWidgetName = null;
			DomainObject dObjSATS = null;

			if (null != mpAttribute && mpAttribute.size() > 0) {
				for (Map.Entry<?, ?> entry : mpAttribute.entrySet()) {
					strAttributeWidgetName = (String) entry.getKey();
					String strAttributeName = strAttributeWidgetName.substring(strAttributeWidgetName.indexOf("[") + 1,
							strAttributeWidgetName.lastIndexOf("]"));
					strAttributeValue = (String) entry.getValue();
					if (UIUtil.isNotNullAndNotEmpty(strAttributeValue)) {
						attrMap.put(strAttributeName, strAttributeValue);
					}
				}
			}
			if (attrMap.size() > 0) {
				dObjSATS = DomainObject.newInstance(context, strObjectId);
				dObjSATS.setAttributeValues(context, attrMap);
			}
		} catch (Exception excep) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
			throw excep;
		}
	}

	/**
	 * Method to edit the SATS object
	 * 
	 * @param context
	 * @param strObjectId
	 * @param alConnections
	 * @throws Exception
	 */
	public void editStructuredATSConnections(Context context, String strObjectId, ArrayList<Map<?, ?>> alConnections)
			throws Exception {
		String strObjId = null;
		Boolean bIsFrom = null;
		String strRelName = null;
		Map<String, Map<Object, Object>> mpAllConnectionsMap = new HashMap<>();

		try {
			if (null != alConnections && !alConnections.isEmpty()) {
				for (Map<?, ?> mConnectionInfo : alConnections) {

					strObjId = (String) mConnectionInfo.get(DomainConstants.SELECT_ID);
					bIsFrom = (Boolean) mConnectionInfo.get(PGStructuredATSConstants.KEY_IS_FROM);
					strRelName = (String) mConnectionInfo.get(PGStructuredATSConstants.KEY_REL_TYPE);

					if (mpAllConnectionsMap.containsKey(strRelName)) {
						HashMap<Object, Object> mpConnectionMap = (HashMap<Object, Object>) mpAllConnectionsMap
								.get(strRelName);
						String strOID = (String) mpConnectionMap.get(DomainConstants.SELECT_ID);
						mpConnectionMap.put(DomainConstants.SELECT_ID, strOID + "," + strObjId);
						mpAllConnectionsMap.put(strRelName, mpConnectionMap);
					} else {
						HashMap<Object, Object> mpConnectionMap = new HashMap<>();
						mpConnectionMap.put(DomainConstants.SELECT_ID, strObjId);
						mpConnectionMap.put(PGStructuredATSConstants.KEY_IS_FROM, bIsFrom);
						mpAllConnectionsMap.put(strRelName, mpConnectionMap);
					}
				}

				for (Map.Entry<String, Map<Object, Object>> entry : mpAllConnectionsMap.entrySet()) {
					String strRelationshipName = entry.getKey();
					HashMap<Object, Object> mpConnectionMap = (HashMap<Object, Object>) entry.getValue();
					Boolean bIsFromObj = (Boolean) mpConnectionMap.get(PGStructuredATSConstants.KEY_IS_FROM);
					String strInputObjIds = (String) mpConnectionMap.get(DomainConstants.SELECT_ID);
					StringList slInputIdsList = StringUtil.split(strInputObjIds, ",");

					MapList mlConnectedObjList = getRelatedObjectsForSATS(context, strObjectId, bIsFromObj,
							strRelationshipName);

					String[] strDisconnectRelIdArray = getRelIdsToBeDisconnected(mlConnectedObjList, slInputIdsList);
					StringList slIdsToConnectList = getIdsTobeConnected(mlConnectedObjList, slInputIdsList);

					DomainRelationship.disconnect(context, strDisconnectRelIdArray);

					connectSelectedObjectsToSATS(context, slIdsToConnectList, bIsFromObj, strRelationshipName,
							strObjectId);

				}

			}
		} catch (Exception excep) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
			throw excep;
		}
	}

	/**
	 * This Method connects objects to SATS on edit
	 * 
	 * @param context
	 * @param slIdsToConnectList
	 * @param bIsFromObj
	 * @param strRelationshipName
	 * @param strObjectId
	 */
	private void connectSelectedObjectsToSATS(Context context, StringList slIdsToConnectList, Boolean bIsFromObj,
			String strRelationshipName, String strSATSId) {
		try {
			int iListSize = slIdsToConnectList.size();
			for (int i = 0; i < iListSize; i++) {
				String strInputId = slIdsToConnectList.get(i);
				connectObjectsToSATS(context, strSATSId, bIsFromObj, strRelationshipName, strInputId);
			}

		} catch (Exception excep) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
		}
	}

	/**
	 * Method to get the Ids to be connected for SATS
	 * 
	 * @param mlConnectedObjList
	 * @param slInputIdsList
	 * @return
	 */
	private StringList getIdsTobeConnected(MapList mlConnectedObjList, StringList slInputIdsList) {
		StringList slIdsToConnectList = new StringList();
		StringList slConnectedIdList = new StringList();
		int iListSize = mlConnectedObjList.size();
		for (int i = 0; i < iListSize; i++) {
			Map<?, ?> objInfoMap = (Map<?, ?>) mlConnectedObjList.get(i);
			String strPhysicalId = (String) objInfoMap.get(DomainConstants.SELECT_PHYSICAL_ID);
			String strObjId = (String) objInfoMap.get(DomainConstants.SELECT_ID);
			slConnectedIdList.add(strPhysicalId);
			slConnectedIdList.add(strObjId);
		}

		int iInputIdsSize = slInputIdsList.size();
		for (int j = 0; j < iInputIdsSize; j++) {
			String strInputId = slInputIdsList.get(j);
			if (!slConnectedIdList.contains(strInputId)) {
				slIdsToConnectList.add(strInputId);
			}
		}

		return slIdsToConnectList;
	}

	/**
	 * Method to get rel ids to disconnect
	 * 
	 * @param mlConnectedObjList
	 * @param slInputIdsList
	 * @return
	 */
	private String[] getRelIdsToBeDisconnected(MapList mlConnectedObjList, StringList slInputIdsList) {
		StringList slDisconnectIdList = new StringList();
		int iListSize = mlConnectedObjList.size();
		for (int i = 0; i < iListSize; i++) {
			Map<?, ?> objInfoMap = (Map<?, ?>) mlConnectedObjList.get(i);
			String strPhysicalId = (String) objInfoMap.get(DomainConstants.SELECT_PHYSICAL_ID);
			String strObjId = (String) objInfoMap.get(DomainConstants.SELECT_ID);
			if (!(slInputIdsList.contains(strObjId) || slInputIdsList.contains(strPhysicalId))) {
				String strRelId = (String) objInfoMap.get(DomainConstants.SELECT_RELATIONSHIP_ID);
				slDisconnectIdList.add(strRelId);
			}
		}
		return slDisconnectIdList.toStringArray();
	}

	/**
	 * Get the already connected objects for SATS
	 * 
	 * @param context
	 * @param strObjectId
	 * @param bIsFrom
	 * @param strRelName
	 * @return
	 * @throws Exception
	 */
	private MapList getRelatedObjectsForSATS(Context context, String strObjectId, Boolean bIsFrom, String strRelName)
			throws Exception {
		Pattern relPattern = new Pattern(strRelName);
		StringList slRelSelects = new StringList(DomainConstants.SELECT_RELATIONSHIP_ID);
		StringList sObjSelects = new StringList(DomainConstants.SELECT_PHYSICAL_ID);
		sObjSelects.add(DomainConstants.SELECT_ID);
		MapList mlConnectedObjList = null;

		try {
			if (UIUtil.isNotNullAndNotEmpty(strObjectId) && UIUtil.isNotNullAndNotEmpty(strRelName)) {
				DomainObject dobSATS = DomainObject.newInstance(context, strObjectId);
				boolean bGetFrom = false;
				boolean bGetTo = true;

				if (bIsFrom) {
					bGetFrom = true;
					bGetTo = false;
				}

				mlConnectedObjList = dobSATS.getRelatedObjects(context, // the eMatrix Context object
						relPattern.getPattern(), // Relationship pattern
						"*", // Type pattern
						sObjSelects, // Object selects
						slRelSelects, // Relationship selects
						bGetFrom, // get From relationships
						bGetTo, // get To relationships
						(short) 1, // the number of levels to expand, 0 equals expand all.
						null, // Object where clause
						null, // Relationship where clause
						0); // Limit : The max number of Objects to get in the exapnd.0 to return all the
							// data available
			}

		} catch (Exception excep) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
			throw excep;
		}
		return mlConnectedObjList;
	}

	/**
	 * This Method connects objects to SATS on edit
	 * 
	 * @param context
	 * @param strSATSObjectId
	 * @param strRelationshipName
	 * @param strToBeConnID
	 * @throws Exception
	 */
	public void connectObjectsToSATS(Context context, String strSATSObjectId, boolean bIsFrom,
			String strRelationshipName, String strToBeConnID) throws Exception {
		try {
			if (UIUtil.isNotNullAndNotEmpty(strSATSObjectId) && UIUtil.isNotNullAndNotEmpty(strRelationshipName)) {
				if (bIsFrom)
					connectObjects(context, strToBeConnID, strSATSObjectId, strRelationshipName);
				else
					connectObjects(context, strSATSObjectId, strToBeConnID, strRelationshipName);
			}

		} catch (Exception excep) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
			throw excep;
		}
	}

	/**
	 * This method connects CA with SATS
	 * 
	 * @param context
	 * @param object  Id of SATS
	 * @param Map     having all the details from WS
	 * @return
	 * @throws Exception
	 */
	public void createAndConnectCA(Context context, String strObjectId, Map<?, ?> mpRequestMap) throws Exception {
		String strChangeActionId = null;
		ChangeAction changeAction = null;
		String strCAId = null;
		try {
			if (null != mpRequestMap) {
				strChangeActionId = (String) mpRequestMap.get(PGStructuredATSConstants.STRING_CHANGEACTIONID);
				if (UIUtil.isNotNullAndNotEmpty(strChangeActionId)
						&& PGStructuredATSConstants.STRING_CREATENEW.equalsIgnoreCase(strChangeActionId)) {
					strCAId = (new ChangeAction()).create(context);
					changeAction = new ChangeAction(strCAId);
					changeAction.connectAffectedItems(context, new StringList(strObjectId));
				} else if (UIUtil.isNotNullAndNotEmpty(strChangeActionId)) {
					changeAction = new ChangeAction(strChangeActionId);
					changeAction.connectAffectedItems(context, new StringList(strObjectId));
				}
			}
		} catch (Exception excep) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
			throw excep;
		}
	}
	/**
	 * This method edits CA connection with Structured ATS
	 * 
	 * @param context
	 * @param object  Id of SATS
	 * @param Map     having all the details from WS
	 * @return
	 * @throws Exception
	 */
	public void editCAConnections(Context context, Map<?, ?> mpRequestMap) throws Exception {
		String strObjectId = null;
		String strChangeActionId = null;
		StringList slProposedActivityRelID = new StringList();
		String strProposedActivityRelID = null;
		String strConnectedCAId = null;
		Hashtable hmap = null;
		try {
			strObjectId = (String) mpRequestMap.get(DomainConstants.SELECT_ID);
			if (null != mpRequestMap && mpRequestMap.size() > 0) {
				strChangeActionId = (String) mpRequestMap.get(PGStructuredATSConstants.STRING_CHANGEACTIONID); // CA to
																												// be
																												// connected
				String[] idArr = { strObjectId };
				Map objectMap = ChangeUtil.getChangeObjectsInProposed(context,
						new StringList(DomainConstants.SELECT_ID), idArr, 1);
				MapList changeActionList = (MapList) objectMap.get(strObjectId);
				for (int i = 0; i < changeActionList.size(); i++) {
					hmap = (Hashtable) changeActionList.get(i);
					strProposedActivityRelID = (String) hmap.get(DomainRelationship.SELECT_ID);
					slProposedActivityRelID.add(strProposedActivityRelID);
					strConnectedCAId = (String) hmap.get(DomainConstants.SELECT_ID); // CA already connected
				}
				if (UIUtil.isNotNullAndNotEmpty(strConnectedCAId)) {
					if (null != slProposedActivityRelID && !slProposedActivityRelID.isEmpty()) {
						ChangeAction changeAction = new ChangeAction(strConnectedCAId);
						changeAction.disconnectAffectedItems(context, slProposedActivityRelID);
					}
				}
				if (UIUtil.isNotNullAndNotEmpty(strChangeActionId)) {
					createAndConnectCA(context, strObjectId, mpRequestMap);
				}
			}
		} catch (Exception excep) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
			throw excep;
		}
	}
}

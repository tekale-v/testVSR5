package com.pg.widgets.nexusPerformanceChars;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.widgets.structuredats.PGStructuredATSConstants;
import com.pg.widgets.util.PGWidgetConstants;
import com.pg.widgets.util.PGWidgetUtil;

import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.util.StringList;

public class PGPerfCharClonePCData {

	private static final Logger logger = Logger.getLogger(PGPerfCharClonePCData.class.getName());

	/**
	 * Method to clone 'Performance Characteristics' objects
	 * 
	 * @param context
	 * @param strJsonInput
	 * @return
	 * @throws FrameworkException
	 */
	public static String createClonePerfChars(Context context, String strJsonInput) throws FrameworkException {

		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		try {

			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strJsonInput);
			String strParentId = jsonInputData.getString(DomainConstants.SELECT_ID);
			String strClonePCId = jsonInputData.getString(PGPerfCharsConstants.KEY_CLONED_PC_ID);
			String strNoOfClones = jsonInputData.getString(PGPerfCharsConstants.KEY_NO_OF_CLONES);
			BusinessObject boPCClone = new BusinessObject();
			String strNewPCCloneId = DomainConstants.EMPTY_STRING;
			DomainObject dObjClonePCId = DomainObject.newInstance(context, strClonePCId);
			DomainObject dObjClonedPCId = DomainObject.newInstance(context);
			if (UIUtil.isNotNullAndNotEmpty(strNoOfClones)) {
				int noOfClones = Integer.parseInt(strNoOfClones);
				for (int i = 0; i < noOfClones; i++) {
					String sPCAutoName = new StringBuilder("auto_").append(Calendar.getInstance().getTimeInMillis())
							.toString();// Generating Auto-Name using time stamp
					boPCClone = dObjClonePCId.cloneObject(context, sPCAutoName, "0",
							PGPerfCharsConstants.VAULT_ESERVICE_PRODUCTION, false);
					if (boPCClone.exists(context)) {
						strNewPCCloneId = boPCClone.getObjectId(context);
						dObjClonedPCId.setId(strNewPCCloneId);
					}
					if (UIUtil.isNotNullAndNotEmpty(strNewPCCloneId)) {
						connectObjects(context, strParentId, strNewPCCloneId,
								PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC);
						ConnectTestMethodsAndTMRefDocFromClonePC(context, dObjClonePCId, dObjClonedPCId);
					}
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, PGPerfCharsConstants.EXCEPTION_MESSAGE_PERF_CHAR_CREATE_EDIT_UTIL, e);
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
		}
		PGPerfCharsFetchData objPerfCharsFetchData = new PGPerfCharsFetchData();
		return objPerfCharsFetchData.fetchPerfCharsData(context, strJsonInput);

	}
	/**
	 * Method to Connect objects
	 * 
	 * @param context
	 * @param strFromObjID,strToObjID,strRelationshipName
	 * @return domRelationship
	 * @throws Exception
	 */
	public static DomainRelationship connectObjects(Context context, String strFromObjID, String strToObjID,
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
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_PERFORMANCE_UTIL, excep);
		}
		return domRelationship;
	}
	/**
	 * Method to Connect Source PC TM & TMRD to cloned PC
	 * 
	 * @param context
	 * @param doCharateristic,dobjNewCharacteric
	 * @return mlTMTMRDList
	 * @throws Exception
	 */
	public static MapList ConnectTestMethodsAndTMRefDocFromClonePC(Context context, DomainObject doCharateristic,
			DomainObject dobjNewCharacteric) throws Exception {
		MapList mlTMTMRDList = new MapList();
		try {
			StringList slObjSelects = new StringList(3);
			slObjSelects.add(DomainConstants.SELECT_ID);
			slObjSelects.add(DomainConstants.SELECT_LAST_ID);
			slObjSelects.add("last.current");

			mlTMTMRDList = doCharateristic.getRelatedObjects(context, DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT, // relationship
																														// Pattern
					DomainConstants.QUERY_WILDCARD, // type Pattern
					slObjSelects, // objectSelects
					null, // relSelects
					true, // getTo
					true, // getFrom
					(short) 1, // recurse
					null, // objectWhere
					null, // relWhere
					0);

			if (null != mlTMTMRDList && !mlTMTMRDList.isEmpty()) {
				String strLastRefDocObjId = null;
				String strLastRefDocObjCurrent = null;
				String strType = null;
				Map mCharInfo = null;
				StringList slTMIds = new StringList();
				StringList slTMRDIds = new StringList();

				Iterator itrCharRelatedObjs = mlTMTMRDList.iterator();
				while (itrCharRelatedObjs.hasNext()) {
					mCharInfo = (Map) itrCharRelatedObjs.next();
					strLastRefDocObjId = (String) mCharInfo.get(DomainConstants.SELECT_LAST_ID);
					strLastRefDocObjCurrent = (String) mCharInfo.get("last.current");
					strType = (String) mCharInfo.get(DomainConstants.SELECT_TYPE);

					if (UIUtil.isNotNullAndNotEmpty(strLastRefDocObjId)
							&& !PGPerfCharsConstants.STATE_OBSOLETE.equalsIgnoreCase(strLastRefDocObjCurrent)) {
						// Modified for 2018x Upgrade - Test Method is replaced by Test Method
						// Specification in 18x OOTB - Starts
						if (PGPerfCharsConstants.TYPE_PG_TEST_METHOD.equalsIgnoreCase(strType)
								|| PGPerfCharsConstants.TYPE_TEST_METHOD_SPECIFICATION.equalsIgnoreCase(strType)) {
							// Modified for 2018x Upgrade - Test Method is replaced by Test Method
							// Specification in 18x OOTB - Ends
							slTMIds.add(strLastRefDocObjId);
						} else if (PGPerfCharsConstants.TYPE_PG_STANDARD_OPERATING_PROCEDURE.equalsIgnoreCase(strType)
								|| PGPerfCharsConstants.TYPE_PG_QUALITY_SPECIFICATION.equalsIgnoreCase(strType)
								|| PGPerfCharsConstants.TYPE_PG_ILLUSTRATION.equalsIgnoreCase(strType)) {
							slTMRDIds.add(strLastRefDocObjId);
						}
					}
				}

				Set<String> TMIds = new HashSet<>(slTMIds);
				Set<String> TMRDIds = new HashSet<>(slTMRDIds);
				if (null != slTMIds && !slTMIds.isEmpty()) {
					DomainRelationship.connect(context, dobjNewCharacteric,
							DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT, false,
							(String[]) TMIds.toArray(new String[TMIds.size()]));
				}
				if (null != slTMRDIds && !slTMRDIds.isEmpty()) {
					DomainRelationship.connect(context, dobjNewCharacteric,
							DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT, false,
							(String[]) TMRDIds.toArray(new String[TMRDIds.size()]));
				}
			}
		} catch (Exception exc) {
			
		}
		return mlTMTMRDList;
	}
}

/*
 **   MOSOverrideRequestUtils.java
 **   Description - Introduced as part of MOS POA Override by (DSM Sogeti) for Apr CW 2022 - MOS Override Requirements ID (41972,42016,42017,42018,42020,42031,42033,42034,42035,42039,42041,42042,42043,42045,42048,42049,42051,42053,42134,42135,42192,42193,42222,42241,42302,42489,42244)
 **   About - For MOS Override Request Functionality
 **
 */
package com.pg.v4.util.mos.override;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;

/**
 * @author DSM(Sogeti)
 *
 */
public class MOSOverrideRequestUtils {

	private Context ctx;
	private boolean isPOAOverride;
	private boolean isFULLOverride;

	private MapList validParts;
	private boolean isRequestHaveConflict;
	private String notification;

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * @param context
	 * @param overrideType
	 */
	public MOSOverrideRequestUtils(Context context, String overrideType) {
		this.ctx = context;
		this.isFULLOverride = MOSOverrideConstants.KEY_FULL_OVERRIDE.equalsIgnoreCase(overrideType);
		this.isPOAOverride = !this.isFULLOverride;
	}

	/**
	 * @return the validParts
	 */
	public MapList getValidParts() {
		return validParts;
	}

	/**
	 * @return the isRequestHaveConflict
	 */
	public boolean isRequestHaveConflict() {
		return isRequestHaveConflict;
	}

	/**
	 * @return the notification
	 */
	public String getNotification() {
		return notification;
	}

	/**
	 * @param isRequestHaveConflict the isRequestHaveConflict to set
	 */
	private void updateAlertMessage(List partsHasPendingOR, List partsHasFullOverrideOR) {
		this.isRequestHaveConflict = Boolean.TRUE;
		boolean isNeedToAddNewLine = Boolean.FALSE;

		StringBuilder sb = new StringBuilder();
		if (!partsHasPendingOR.isEmpty()) {
			isNeedToAddNewLine = Boolean.TRUE;
			sb.append(this.buildNotification(partsHasPendingOR, true));
		}
		if (isNeedToAddNewLine)
			sb.append("\\n");

		if (!partsHasFullOverrideOR.isEmpty())
			sb.append(this.buildNotification(partsHasFullOverrideOR, false));

		this.notification = sb.toString();
	}

	/**
	 * @param objList
	 */
	public void filterParts(MapList objList) {
		MapList filteredList = new MapList();
		List<Map> partsHasPendingOR = new MapList();
		List<Map> partsHasFullOverrideOR = new MapList();
		for (Iterator iterator = objList.iterator(); iterator.hasNext();) {
			Map object = (Map) iterator.next();
			boolean sPartAccessible = ((String) object.get("current.access")).equalsIgnoreCase(pgV3Constants.DENIED)?false:true;
			
			if (sPartAccessible && !this.isFPPShipableHalb(object)) {
				String attrVal = (String) object.get(pgV3Constants.SELECT_ATTRIBUTE_PGHASCOSFPPOVERRIDDEN);
				boolean partHasActiveFullOverride = attrVal.equalsIgnoreCase("Yes") ? true : false;
				if (this.isPartHavePendingRequests((String) object.get(DomainConstants.SELECT_ID))) {
					// if part have in work override request
					partsHasPendingOR.add(object);
				} else {
					if (partHasActiveFullOverride && this.isPOAOverride) {
						partsHasFullOverrideOR.add(object);
					} else {
						filteredList.add(object);
					}
				}
			}
		}

		this.validParts = filteredList;

		if (!partsHasPendingOR.isEmpty() || !partsHasFullOverrideOR.isEmpty()) {
			this.updateAlertMessage(partsHasPendingOR, partsHasFullOverrideOR);
		}
	}

	/**
	 * @param objMap
	 * @return
	 */
	private boolean isFPPShipableHalb(Map objMap) {
		boolean isHalb = Boolean.FALSE;
		String sType = (String) objMap.get(DomainConstants.SELECT_TYPE);
		String sAssemblyType = (String) objMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
		if(pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(sType) && pgV3Constants.SHIPPABLE_HALB.equalsIgnoreCase(sAssemblyType)) {
			isHalb = Boolean.TRUE;
		}
		return isHalb;
	}
	
	/**
	 * @param context
	 * @param objId
	 * @return
	 */
	private boolean isPartHavePendingRequests(String objId) {
		boolean havePendingRequest = Boolean.FALSE;
		try {
			DomainObject dObjIPS = DomainObject.newInstance(this.ctx, objId);
			MapList relatedObjects = dObjIPS.getRelatedObjects(this.ctx, // context
					pgV3Constants.RELATIONSHIP_PGCOUNTRYOFSALEOVERRIDE, // relPattern
					pgV3Constants.TYPE_PGCOSOVERRIDE, // typePattern
					null, // bus select tables
					null, // rel select tables
					false, // getTo
					true, // getFrom
					(short) 1, // recurseToLevel
					DomainConstants.SELECT_CURRENT + "=='" + pgV3Constants.STATE_INWORK + "'", // objectWhere
					"", // relationshipWhere
					1); // limit
			if (relatedObjects != null && !relatedObjects.isEmpty()) {
				havePendingRequest = Boolean.TRUE;
			}
		} catch (FrameworkException e) {
			logger.log(Level.WARNING, null, e);
		}
		return havePendingRequest;
	}

	/**
	 * @param partsForAlert
	 * @param isForInWork
	 * @return
	 */
	private String buildNotification(List partsForAlert, boolean isForInWork) {
		StringBuilder sb = new StringBuilder();
		String messagePrefix = isForInWork ? "Following parts have another override request in progress: "
				: "Following parts have existing approved FULL override markets: ";
		sb.append(messagePrefix).append("\\n");
		for (int i = 0; i < partsForAlert.size(); i++) {
			Map objMap = (Map) partsForAlert.get(i);
			sb.append(objMap.get(DomainConstants.SELECT_TYPE)).append(pgV3Constants.SYMBOL_SPACE)
					.append(objMap.get(DomainConstants.SELECT_NAME)).append(pgV3Constants.SYMBOL_SPACE)
					.append(objMap.get(DomainConstants.SELECT_REVISION) + "\\n");
		}
		return sb.toString();
	}

}

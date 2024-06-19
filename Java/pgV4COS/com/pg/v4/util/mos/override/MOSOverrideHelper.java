/*
 **   MOSOverrideHelper.java
 **   Description - Introduced as part of MOS POA Override by (DSM Sogeti) for Apr CW 2022 - MOS Override Requirements ID (41972,42016,42017,42018,42020,42031,42033,42034,42035,42039,42041,42042,42043,42045,42048,42049,42051,42053,42134,42135,42192,42193,42222,42241,42302,42489,42244)
 **   About - Helper class 
 **
 */
package com.pg.v4.util.mos.override;

import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MessageUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Attribute;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.util.StringList;

/**
 * @author DSM(Sogeti)
 *
 */
public class MOSOverrideHelper {
	
	private static final Logger logger = Logger.getLogger(MOSOverrideHelper.class.getName());

	/**
	 * 
	 */
	private MOSOverrideHelper() {
	}

	/**
	 * @param context
	 * @param objIds
	 * @return
	 */
	public static String isLimitExceed(Context context, String objIds) {
		String message = DomainConstants.EMPTY_STRING;
		try {
			BusinessObject boConfig = new BusinessObject(pgV3Constants.TYPE_PGCONFIGURATIONADMIN,
					"pgCOSExpirationMarkingCronAdmin", pgV3Constants.SYMBOL_HYPHEN,
					pgV3Constants.VAULT_ESERVICEPRODUCTION);
			if (boConfig.exists(context)) {
				Attribute attribute = boConfig.getAttributeValues(context, pgV3Constants.ATTRIBUTE_PGCONFIGCOMMON_ATTR);
				StringList objectList = StringUtil.split(objIds, pgV3Constants.SYMBOL_PIPE);
				int count = objectList.size();
				int limit = Integer.parseInt(attribute.getValue().trim());
				if (count > limit) {
					String alertMessage = EnoviaResourceBundle.getProperty(context, "emxCPNStringResource",context.getLocale(), "emxCPN.MOS.Override.Request.ExceedLimit.Message");
					message = MessageUtil.getMessage(context, alertMessage, new String[] { "count", "limit" },
							new String[] { String.valueOf(count), String.valueOf(limit) },
							DomainConstants.EMPTY_STRING);
				}
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception occurred ", e);
		}
		return message;
	}

	/**
	 * @param context
	 * @param doFPP
	 * @param sWhere
	 * @return
	 */
	public static MapList getConnectedOverrideRequests(Context context, DomainObject doFPP, String sWhere) {
		MapList mlCOSOverride = null;
		try {
			StringList objectSelect = new StringList(1);
			objectSelect.add(DomainConstants.SELECT_ID);

			mlCOSOverride = doFPP.getRelatedObjects(context, // context
					pgV3Constants.RELATIONSHIP_PGCOUNTRYOFSALEOVERRIDE, // relationship
					pgV3Constants.TYPE_PGCOSOVERRIDE, // Type pattern
					objectSelect, // object selects
					null, // rel selects
					false, // to side
					true, // from side
					(short) 1, // recursion level
					sWhere, // object where clause
					DomainConstants.EMPTY_STRING, // rel where clause
					0);// no of Objects
		} catch (FrameworkException e) {
			logger.log(Level.WARNING, "Exception occurred ", e);
		}
		return mlCOSOverride;
	}

	/**
	 * @param context
	 * @param mlCOSOverride
	 * @param processCOSOverrideId
	 * @return
	 */
	public static void obsoleteOverrideRequests(Context context, MapList mlCOSOverride, String processCOSOverrideId) {
		try {
			if (mlCOSOverride != null && !mlCOSOverride.isEmpty()) {
				Map mpObjectInfo = null;
				for (int i = 0; i < mlCOSOverride.size(); i++) {
					mpObjectInfo = (Map) mlCOSOverride.get(i);
					String sCOSOverrideId = (String) mpObjectInfo.get(DomainConstants.SELECT_ID);
					if (UIUtil.isNotNullAndNotEmpty(sCOSOverrideId)
							&& !sCOSOverrideId.equalsIgnoreCase(processCOSOverrideId)) {
						DomainObject doCOSOverride = DomainObject.newInstance(context, sCOSOverrideId);
						doCOSOverride.setState(context, pgV3Constants.STATE_OBSOLETE);
					}
				}
			}
		} catch (FrameworkException e) {
			logger.log(Level.WARNING, "Exception occurred ", e);
		}
	}

	/**
	 * @param context
	 * @param doFPP
	 */
	public static void disconnectMarketOfSale(Context context, DomainObject doFPP) {
		try {
			StringList slRelSelectable = new StringList(1);
			slRelSelectable.addElement(DomainRelationship.SELECT_ID);

			StringList objectSelect = new StringList(1);
			objectSelect.add(DomainConstants.SELECT_ID);

			MapList mlFPPCountries = doFPP.getRelatedObjects(context, pgV3Constants.RELATIONSHIP_PGCOUNTRYOFSALE, // relationship
																													// pattern
					pgV3Constants.TYPE_COUNTRY, // Type pattern
					objectSelect, // object selects
					slRelSelectable, // rel selects
					false, // to side
					true, // from side
					(short) 1, // recursion level
					DomainConstants.EMPTY_STRING, // object where clause
					DomainConstants.EMPTY_STRING, 0); // rel where clause

			if (mlFPPCountries != null && !mlFPPCountries.isEmpty()) {
				String[] sRelIdArr = new String[mlFPPCountries.size()];
				Map mpObjectInfo;
				for (int k = 0; k < mlFPPCountries.size(); k++) {
					mpObjectInfo = (Map) mlFPPCountries.get(k);
					String sReID = (String) mpObjectInfo.get(DomainRelationship.SELECT_ID);
					if (UIUtil.isNotNullAndNotEmpty(sReID)) {
						sRelIdArr[k] = sReID;
					}
				}
				if (sRelIdArr.length > 0) {
					DomainRelationship.disconnect(context, sRelIdArr);
				}
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception occurred ", e);
		}
	}

	/**
	 * @param context
	 * @param slRequestedMarkets
	 */
	public static void connectOverrideMarketsToPart(Context context, DomainObject doFPP,
			StringList slRequestedMarkets) {
		try {
			StringList objectSelect = new StringList(1);
			objectSelect.add(DomainConstants.SELECT_ID);

			StringList slCountry = new StringList();
			for (String CountryName : slRequestedMarkets) {
				MapList mlCountries = DomainObject.findObjects(context, pgV3Constants.TYPE_COUNTRY, CountryName,
						DomainConstants.QUERY_WILDCARD, DomainConstants.QUERY_WILDCARD,
						pgV3Constants.VAULT_ESERVICEPRODUCTION, null, false, objectSelect);
				if (mlCountries.size() == 1) {
					Iterator countryItr = mlCountries.iterator();
					while (countryItr.hasNext()) {
						Map mapTemp = (Map) countryItr.next();
						String sCountryID = (String) mapTemp.get(DomainConstants.SELECT_ID);
						if (UIUtil.isNotNullAndNotEmpty(sCountryID)) {
							slCountry.addElement(sCountryID);
						}
					}
				}
			}

			if (!slCountry.isEmpty()) {
				int iListForConnection = slCountry.size();
				String[]  arCountryToConnect = slCountry.toArray(new String[iListForConnection]);
				DomainRelationship.connect(context, doFPP, pgV3Constants.RELATIONSHIP_PGCOUNTRYOFSALE, true,
						arCountryToConnect);
			}
		} catch (FrameworkException e) {
			logger.log(Level.WARNING, "Exception occurred ", e);
		}
	}
}

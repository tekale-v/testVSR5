/*
 **   MOSOverrideReleaseAction.java
 **   Description: Introduced as part of 2022x-01 Feb CW
 **   About: On-release of (FPP, PAP, FAB) to carry forward (Markets & Override) objects from previous revision to latest released revision.
 **
 */
package com.pg.v4.util.mos.override;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.v3.custom.pgV3Constants;
import com.pg.v4.mos.MOSOverrideReplicate;

import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.util.StringList;

public class MOSOverrideReleaseAction {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	Context context;

	public MOSOverrideReleaseAction(Context context) {
		this.context = context;
	}

	/**
	 * @param args
	 * @throws FrameworkException
	 */
	public void replicateMarketsAndOverrides(String[] args) {
		if (args.length == 0) {
			throw new IllegalArgumentException();
		}
		try {
			String id = args[0];
			String type = args[1];
			String state = args[2];

			if (MOSOverrideConstants.MOS_OVERRIDE_VALID_TYPES.contains(type)) { // if incoming object is of type (FPP, PAP, FAB)
				String releaseState = PropertyUtil.getSchemaProperty(this.context, DomainConstants.SELECT_POLICY, DomainConstants.POLICY_EC_PART, "state_Release");
				if (releaseState.equalsIgnoreCase(state) && UIUtil.isNotNullAndNotEmpty(id)) { // if incoming object's current state is release.
					MOSOverrideReplicate util = new MOSOverrideReplicate(this.context);
					util.processOverride(id);
				} 
			} 

		} catch (Exception e) {
			logger.log(Level.WARNING, "Error:" + e);
		}
	}


	/**
	 * Called (via-trigger - pgIPMUtil_Deferred:replicateMarketsAndOverrides). Must be deferred.
	 * When: On-release of (FPP, PAP, FAB) to carry forward (Markets & Override) objects from previous revision to latest released revision.
	 *
	 * @param args
	 * @throws FrameworkException
	 */
	public void replicateMarketsAndOverridesOne(String[] args, boolean flag) throws FrameworkException {
		if (args.length == 0) {
			throw new IllegalArgumentException();
		}

		try {
			if(flag) {
				Optional<String> optionalOid = Optional.of(args[0]);          // id
				Optional<String> optionalType = Optional.of(args[1]);         // type
				Optional<String> optionalCurrentState = Optional.of(args[2]); // current

				if (optionalType.isPresent() && MOSOverrideConstants.MOS_OVERRIDE_VALID_TYPES.contains(optionalType.get())) { // if incoming object is of type (FPP, PAP, FAB)
					String releaseState = PropertyUtil.getSchemaProperty(this.context, DomainConstants.SELECT_POLICY, DomainConstants.POLICY_EC_PART, "state_Release");
					if (optionalCurrentState.isPresent() && releaseState.equalsIgnoreCase(optionalCurrentState.get())) { // if incoming object's current state is release.
						if (optionalOid.isPresent() && UIUtil.isNotNullAndNotEmpty(optionalOid.get())) { // if incoming object id is not null/empty.
							DomainObject currentObject = DomainObject.newInstance(this.context, optionalOid.get());
							BusinessObject previousRevision = currentObject.getPreviousRevision(this.context);
							if (null != previousRevision) { // if previous revision exist.
								DomainObject previousObject = DomainObject.newInstance(this.context, previousRevision);
								Map<Object, Object> objectInfoPrevious = getObjectInfo(previousObject);

								boolean isCOSFullOverridden = ((String) objectInfoPrevious.get(pgV3Constants.SELECT_ATTRIBUTE_PGHASCOSFPPOVERRIDDEN)).equalsIgnoreCase(pgV3Constants.KEY_YES_VALUE);
								boolean isCOSPOAOverridden = ((String) objectInfoPrevious.get(MOSOverrideConstants.SELECT_ATTRIBUTE_PGHASMOSPARTIALOVERRIDDEN)).equalsIgnoreCase(pgV3Constants.KEY_YES_VALUE);

								if (isCOSFullOverridden || isCOSPOAOverridden) {
									replicateMarketsOnRelease(previousObject, currentObject, isCOSPOAOverridden, isCOSFullOverridden);
									replicateOverridesOnRelease(previousObject, currentObject, isCOSPOAOverridden, isCOSFullOverridden);

									if (isCOSFullOverridden) { // is previous full override, then set the same on current object (latest).
										currentObject.setAttributeValue(context, pgV3Constants.ATTRIBUTE_PGHASCOSFPPOVERRIDDEN, pgV3Constants.KEY_YES_VALUE);
									}
									if (isCOSPOAOverridden) { // is previous poa override, then set the same on current object (latest).
										currentObject.setAttributeValue(context, MOSOverrideConstants.ATTRIBUTE_PGHASMOSPARTIALOVERRIDDEN, pgV3Constants.KEY_YES_VALUE);
									}
								}
							}
						} else {
							logger.log(Level.WARNING, "OID is null/empty");
						}
					} else {
						logger.log(Level.WARNING, "State is null/empty (OR) State is not Release");
					}

				} else {
					logger.log(Level.WARNING, "Type is null/empty (OR) Type is not (FPP, FAB, PAP)");
				}
			}
		} catch (FrameworkException e) {
			throw e;
		}
	}


	/**
	 * Connect (markets) from previous revision to current released revision.
	 *
	 * @param previousObject
	 * @param currentObject
	 * @param isCOSPOAOverridden
	 * @param isCOSFullOverridden
	 * @throws FrameworkException
	 */
	void replicateMarketsOnRelease(DomainObject previousObject, DomainObject currentObject, boolean isCOSPOAOverridden, boolean isCOSFullOverridden) throws FrameworkException {
		Map<String, Map<String, String>> marketAttributeMapPairs = getFilteredMarketAttributeMapPairs(previousObject, currentObject, isCOSPOAOverridden);

		if (null != marketAttributeMapPairs && !marketAttributeMapPairs.isEmpty()) {
			List<String> marketOiDs = new ArrayList<>();
			marketAttributeMapPairs.forEach((k, v) -> marketOiDs.add(k)); // extract object id into list.
			if (isCOSFullOverridden) { // if full-override - connect all markets.
				DomainRelationship.connect(this.context, currentObject, pgV3Constants.RELATIONSHIP_PGCOUNTRYOFSALE, true, marketOiDs.toArray(String[]::new));
			} else { // if poa-override - connect all markets + update rel attributes.
				DomainObject marketObject;
				DomainRelationship domainRelationship;
				for (String marketOiD : marketOiDs) {
					marketObject = DomainObject.newInstance(this.context, marketOiD);
					domainRelationship = DomainRelationship.connect(this.context, currentObject, pgV3Constants.RELATIONSHIP_PGCOUNTRYOFSALE, marketObject);
					// update relationship attribute for poa-override.
					domainRelationship.setAttributeValues(this.context, marketAttributeMapPairs.get(marketOiD));
				}
			}
		}
	}


	/**
	 * Connect (approved) overrides from previous revision to current released revision.
	 *
	 * @param previousObject
	 * @param currentObject
	 * @param isCOSPOAOverridden
	 * @param isCOSFullOverridden
	 * @throws FrameworkException
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	void replicateOverridesOnRelease(DomainObject previousObject, DomainObject currentObject, boolean isCOSPOAOverridden, boolean isCOSFullOverridden) throws FrameworkException {

		MapList previousOverrides = getRelatedOverrides(previousObject); // get previous object's related overrides.
		List<String> currentOverrides = getRelatedOverrideIDs(currentObject);    // get current object's related overrides.

		Map<Object, Object> objectMap;
		String oid;
		String oCurrentState;
		String oRequestType;

		List<String> deltaOverrides = new ArrayList<>();

		// iterate previous object's related overrides.
		Iterator iterator = previousOverrides.iterator();
		while (iterator.hasNext()) {
			objectMap = (Map<Object, Object>) iterator.next();
			oid = (String) objectMap.get(DomainConstants.SELECT_ID);
			oCurrentState = (String) objectMap.get(DomainConstants.SELECT_CURRENT);
			oRequestType = (String) objectMap.get(MOSOverrideConstants.SELECT_ATTRIBUTE_PGMOSOVERRIDEREQUESTTYPE);
			/*
            Check following conditions.
            1. Override object should not be in (In-Work) state.
            2. Override object request type is full-override (OR) poa-override
            3. Same override object is not connected to incoming object (latest released revision).
			 */
			if (!pgV3Constants.STATE_INWORK.equalsIgnoreCase(oCurrentState) && !currentOverrides.contains(oid) &&
					((isCOSFullOverridden && MOSOverrideConstants.KEY_FULL_OVERRIDE.equalsIgnoreCase(oRequestType))
							|| (!isCOSFullOverridden && MOSOverrideConstants.KEY_POA_OVERRIDE.equalsIgnoreCase(oRequestType)))) {
				deltaOverrides.add(oid);
			}
		}
		if (!deltaOverrides.isEmpty()) {
			DomainRelationship.connect(this.context, currentObject, pgV3Constants.RELATIONSHIP_PGCOUNTRYOFSALEOVERRIDE, true, deltaOverrides.toArray(String[]::new));
		}
	}


	/**
	 * Get related markets and its rel attributes.
	 *
	 * @param previousObject
	 * @param currentObject
	 * @param isCOSPOAOverridden
	 * @return
	 * @throws FrameworkException
	 */
	@SuppressWarnings("unchecked")
	Map<String, Map<String, String>> getFilteredMarketAttributeMapPairs(DomainObject previousObject, DomainObject currentObject, boolean isCOSPOAOverridden) throws FrameworkException {
		Map<String, Map<String, String>> deltaMarketPairs = new LinkedHashMap<>();

		MapList previousMarkets = getRelatedMarkets(previousObject, isCOSPOAOverridden); // get previous revision related markets and its rel attributes.
		if (null != previousMarkets && !previousMarkets.isEmpty()) {
			int size = previousMarkets.size();

			Map<Object, Object> marketMap;
			String marketID;
			Map<String, String> attributeMap;

			List<String> currentMarkets = getRelatedMarketIDs(currentObject, Boolean.TRUE); // get related market OIDs of latest revision.

			for (int k = 0; k < size; k++) {
				marketMap = (Map<Object, Object>) previousMarkets.get(k);
				marketID = (String) marketMap.get(DomainConstants.SELECT_ID);
				if (UIUtil.isNotNullAndNotEmpty(marketID) && !currentMarkets.contains(marketID)) { // filter the market which is already connected to latest revision.
					if (isCOSPOAOverridden) {
						attributeMap = new HashMap<>();
						attributeMap.put(MOSOverrideConstants.ATTRIBUTE_PGMOSPOAOVERRIDELRR, (String) marketMap.get(MOSOverrideConstants.SELECT_ATTRIBUTE_PGMOSPOAOVERRIDELRR));
						attributeMap.put(pgV3Constants.ATTRIBUTE_PGCOSRESTRICTION, (String) marketMap.get("attribute[" + pgV3Constants.ATTRIBUTE_PGCOSRESTRICTION + "]"));
						deltaMarketPairs.put(marketID, attributeMap);
					} else {
						deltaMarketPairs.put(marketID, new HashMap<>());
					}
				}
			}
		}
		return deltaMarketPairs;
	}


	/**
	 * Get incoming object info map.
	 *
	 * @param domainObject
	 * @return
	 * @throws FrameworkException
	 */
	@SuppressWarnings("unchecked")
	Map<Object, Object> getObjectInfo(DomainObject domainObject) throws FrameworkException {
		StringList busSelects = new StringList(3);
		busSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGHASCOSFPPOVERRIDDEN);
		busSelects.add(MOSOverrideConstants.SELECT_ATTRIBUTE_PGHASMOSPARTIALOVERRIDDEN);
		busSelects.add(pgV3Constants.SELECT_PREVIOUS_ID);
		return domainObject.getInfo(this.context, busSelects);
	}


	/**
	 * Get related market OIDs list.
	 *
	 * @param domainObject
	 * @param isCOSPOAOverridden
	 * @return
	 * @throws FrameworkException
	 */
	@SuppressWarnings("unchecked")
	List<String> getRelatedMarketIDs(DomainObject domainObject, boolean isCOSPOAOverridden) throws FrameworkException {
		List<String> marketList = new ArrayList<>();
		MapList objectList = getRelatedMarkets(domainObject, isCOSPOAOverridden);
		if (null != objectList && !objectList.isEmpty()) {
			for (Object object : objectList) {
				marketList.add((String) ((Map<Object, Object>) object).get(DomainConstants.SELECT_ID));
			}
		}
		return marketList;
	}


	/**
	 * Get (object IDs) of related overrides.
	 *
	 * @param domainObject
	 * @return
	 * @throws FrameworkException
	 */
	@SuppressWarnings("unchecked")
	List<String> getRelatedOverrideIDs(DomainObject domainObject) throws FrameworkException {
		List<String> overrideList = new ArrayList<>();
		MapList objectList = getRelatedOverrides(domainObject);
		if (null != objectList && !objectList.isEmpty()) {
			for (Object object : objectList) {
				overrideList.add((String) ((Map<Object, Object>) object).get(DomainConstants.SELECT_ID));
			}
		}
		return overrideList;
	}


	/**
	 * Get related market of sale (Country) objects.
	 *
	 * @param domainObject
	 * @param isCOSPOAOverridden
	 * @return
	 * @throws FrameworkException
	 */
	MapList getRelatedMarkets(DomainObject domainObject, boolean isCOSPOAOverridden) throws FrameworkException {
		StringList objectSelects = new StringList(1);
		objectSelects.add(DomainConstants.SELECT_ID);

		StringList relSelects = new StringList(3);
		relSelects.add(DomainRelationship.SELECT_ID);
		if (isCOSPOAOverridden) {
			relSelects.add(MOSOverrideConstants.SELECT_ATTRIBUTE_PGMOSPOAOVERRIDELRR);
			relSelects.add("attribute[" + pgV3Constants.ATTRIBUTE_PGCOSRESTRICTION + "]");
		}
		return domainObject.getRelatedObjects(this.context,
				pgV3Constants.RELATIONSHIP_PGCOUNTRYOFSALE,     // relationship pattern
				pgV3Constants.TYPE_COUNTRY,                     // Type pattern
				objectSelects,                                  // object selects
				relSelects,                                     // rel selects
				false,                                          // to side
				true,                                           // from side
				(short) 1,                                      // recursion level
				DomainConstants.EMPTY_STRING,                   // object where clause
				DomainConstants.EMPTY_STRING, 0);            // rel where clause

	}

	/**
	 * Get related Overrides.
	 *
	 * @param context
	 * @param domainObject
	 * @return
	 * @throws FrameworkException
	 */
	MapList getRelatedOverrides(DomainObject domainObject) throws FrameworkException {
		StringList busSelects = new StringList(3);
		busSelects.add(DomainConstants.SELECT_ID);
		busSelects.add(DomainConstants.SELECT_CURRENT);
		busSelects.add(MOSOverrideConstants.SELECT_ATTRIBUTE_PGMOSOVERRIDEREQUESTTYPE);
		return domainObject.getRelatedObjects(this.context,
				pgV3Constants.RELATIONSHIP_PGCOUNTRYOFSALEOVERRIDE, // relationship pattern
				pgV3Constants.TYPE_PGCOSOVERRIDE,                   // Type pattern
				busSelects,                                         // object selects
				DomainConstants.EMPTY_STRINGLIST,                   // rel selects
				false,                                              // to side
				true,                                               // from side
				(short) 1,                                          // recursion level
				DomainConstants.EMPTY_STRING,                       // object where clause
				DomainConstants.EMPTY_STRING,                       // rel where clause
				0);                                                 // limit
	}
}

/*
 **   MOSOverrideCalculationUtils.java
 **   Description - Introduced as part of MOS Override - by (DSM Sogeti) for Apr CW 2022 - MOS Override Requirements ID (41972,42016,42017,42018,42020,42031,42033,42034,42035,42039,42041,42042,42043,42045,42048,42049,42051,42053,42134,42135,42192,42193,42222,42241,42302,42489,42244)
 **   About - For MOS POA Override Calculation Functionality
 **
 */
package com.pg.v4.util.mos.override;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.matrixone.apps.cpn.CPNCommonConstants;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.StringList;

/**
 * @author DSM(Sogeti)
 *
 */
public class MOSOverrideCalculationUtils {

	private Context ctx;
	private DomainObject domObj;
	private Set<String> poaOverrideMarkets;
	private Set<String> productPartIntersectionMarkets;
	private Set<String> lrrMarketsonChildParentPart;

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * @param context
	 * @param objectId
	 */
	public MOSOverrideCalculationUtils(Context context, String objectId) {
		this.ctx = context;
		try {
			this.domObj = DomainObject.newInstance(this.ctx, objectId);
		} catch (FrameworkException e) {
			logger.log(Level.WARNING, null, e);
		}
	}

	/**
	 * @return the poaOverrideMarkets
	 */
	public Set<String> getPOAOverrideMarkets() {
		return poaOverrideMarkets;
	}

	/**
	 * @param poaOverrideMarkets the poaOverrideMarkets to set
	 */
	public void setPOAOverrideMarkets(Set<String> poaOverrideMarkets) {
		this.poaOverrideMarkets = poaOverrideMarkets;
	}

	/**
	 * @return the productPartIntersectionMarkets
	 */
	public Set<String> getProductPartIntersectionMarkets() {
		return productPartIntersectionMarkets;
	}

	/**
	 * @return the lrrMarketsonChildParentPart
	 */
	public Set<String> getChildComponentLRRMarkets() {
		return lrrMarketsonChildParentPart == null ? new HashSet<String>() : lrrMarketsonChildParentPart;
	}

	/**
	 * @param lrrMarket
	 */
	public void addChildComponentLRRMarkets(String lrrMarket) {
		if (this.lrrMarketsonChildParentPart == null) {
			this.lrrMarketsonChildParentPart = new HashSet<String>();
		}
		this.lrrMarketsonChildParentPart.add(lrrMarket);
	}

	/**
	 * @param lrrMarketsonChildParentPart the lrrMarketsonChildParentPart to set
	 */
	public void addChildComponentLRRMarkets(Set<String> lrrMarketsonChildParentPart) {
		if (this.lrrMarketsonChildParentPart == null) {
			this.lrrMarketsonChildParentPart = new HashSet<String>();
		}
		if (lrrMarketsonChildParentPart != null && !lrrMarketsonChildParentPart.isEmpty()) {
			this.lrrMarketsonChildParentPart.addAll(lrrMarketsonChildParentPart);
		}
	}

	/**
	 * @return
	 */
	public boolean isPartHasPOAOverride() {
		boolean isPartHasOverride = Boolean.FALSE;
		try {
			String isOverride = this.domObj.getInfo(this.ctx, MOSOverrideConstants.SELECT_ATTRIBUTE_PGHASMOSPARTIALOVERRIDDEN);
			if (UIUtil.isNotNullAndNotEmpty(isOverride) && pgV3Constants.KEY_YES.equalsIgnoreCase(isOverride)) {
				isPartHasOverride = Boolean.TRUE;
			}
		} catch (FrameworkException e) {
			logger.log(Level.WARNING, null, e);
		}
		return isPartHasOverride;
	}

	public void fetchPOAOverrideMarkets() {
		Set<String> markets = new HashSet<>();
		try {
			markets = getQualifiedCountries();
		} catch (FrameworkException e) {
			logger.log(Level.WARNING, null, e);
		}
		this.setPOAOverrideMarkets(markets);
	}

	/**
	 * @return
	 */
	public void fetchPOAOverrideMarkets(boolean unUsed) {
		Set<String> markets = new HashSet<>();
		try {
			MapList relatedObjects = this.domObj.getRelatedObjects(this.ctx, // context
					pgV3Constants.RELATIONSHIP_PGCOUNTRYOFSALEOVERRIDE, // relPattern
					pgV3Constants.TYPE_PGCOSOVERRIDE, // typePattern
					this.getBusSelectTables(), // objectSelects
					null, // relationshipSelects
					false, // getTo
					true, // getFrom
					(short) 1, // recurseToLevel
					"current==Approved && " + MOSOverrideConstants.SELECT_ATTRIBUTE_PGMOSOVERRIDEREQUESTTYPE + "=='"
							+ MOSOverrideConstants.KEY_POA_OVERRIDE + "'", // objectWhere
					DomainConstants.EMPTY_STRING, // relationshipWhere
					0);// limit

			List<String> marketsList = (List<String>) relatedObjects.stream()
					.map(map -> ((Map<Object, Object>) map).get(pgV3Constants.SELECT_ATTRIBUTE_PGCOSOVERRIDECOUNTRIES))
					.collect(Collectors.toList());

			for (String market : marketsList) {
				if (UIUtil.isNotNullAndNotEmpty(market)) {
					if (market.contains(pgV3Constants.SYMBOL_PIPE)) {
						markets.addAll(StringUtil.split(market, pgV3Constants.SYMBOL_PIPE));
					} else {
						markets.add(market);
					}
				}
			}
		} catch (FrameworkException e) {
			logger.log(Level.WARNING, null, e);
		}
		this.setPOAOverrideMarkets(markets);
	}

        /**
	 * @return
	 * @throws FrameworkException
	 */
	public Set<String> getQualifiedCountries() throws FrameworkException {
		Set<String> resultSet = new HashSet<>();

		// get connected override objects.
		MapList objectList = this.domObj.getRelatedObjects(this.ctx, // context
				pgV3Constants.RELATIONSHIP_PGCOUNTRYOFSALEOVERRIDE, // relPattern
				pgV3Constants.TYPE_PGCOSOVERRIDE, // typePattern
				this.getBusSelectTables(), // objectSelects
				DomainConstants.EMPTY_STRINGLIST, // relationshipSelects
				false, // getTo
				true, // getFrom
				(short) 1, // recurseToLevel
				DomainConstants.EMPTY_STRING, // objectWhere
				DomainConstants.EMPTY_STRING, // relationshipWhere
				0);// limit

		// sort the override result based on originated date.
		objectList.sort(DomainConstants.SELECT_ORIGINATED, "ascending", "date");

		// iterate override object and collect all the country name into a unique set.
		Set<String> countries = new HashSet<>();
		Map<Object, Object> objectMap;
		Iterator iterator = objectList.iterator();
		while (iterator.hasNext()) {
			objectMap = (Map<Object, Object>) iterator.next();
			countries.addAll(StringUtil.split((String) objectMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGCOSOVERRIDECOUNTRIES), pgV3Constants.SYMBOL_PIPE));
		}

		// iterate each country and decide whether to include in result or not.
		String stateApproved = PropertyUtil.getSchemaProperty(this.ctx, DomainConstants.SELECT_POLICY, MOSOverrideConstants.POLICY_PG_COS_OVERRIDE, "state_Approved");
                String stateObsolete = PropertyUtil.getSchemaProperty(this.ctx, DomainConstants.SELECT_POLICY, MOSOverrideConstants.POLICY_PG_COS_OVERRIDE, "state_Obsolete");
		String current;
		for (String country : countries) {

			// iterate over each override objects.
			iterator = objectList.iterator();
			while (iterator.hasNext()) {
				objectMap = (Map<Object, Object>) iterator.next();
				current = (String) objectMap.get(DomainConstants.SELECT_CURRENT);

				// if the iterating country is present on the override object.
				if (StringUtil.split((String) objectMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGCOSOVERRIDECOUNTRIES), pgV3Constants.SYMBOL_PIPE).contains(country)) {

					// TO-DO: if override object is in approved state.
					if (stateApproved.equalsIgnoreCase(current)) {
						// added this country to the final result set.
						resultSet.add(country);
					}

					// TO-DO:if the override object is in obsolete state.
					if (stateObsolete.equalsIgnoreCase(current)) {

						// if the same country is also present in the result set. then remove this from the result set.
						if (resultSet.contains(country)) {
							resultSet.remove(country);
						}
					}
				}
			}
		}
		logger.log(Level.INFO, "(MOS Calculation) Qualified Markets: {0}", resultSet);
		return resultSet;
	}

	/**
	 * @param stCountries
	 */
	public void addProductPartIntersectionClearedMarkets(Set stCountries) {
		if (this.productPartIntersectionMarkets == null) {
			this.productPartIntersectionMarkets = new HashSet(stCountries);
		} else {
			this.getProductPartIntersectionMarkets().retainAll(stCountries);
		}
	}

	/**
	 * @return
	 */
	private StringList getBusSelectTables() {
		return StringList.create(DomainConstants.SELECT_ID, DomainConstants.SELECT_CURRENT, pgV3Constants.SELECT_ATTRIBUTE_PGCOSOVERRIDECOUNTRIES, DomainConstants.SELECT_ORIGINATED);
	}

	/**
	 * @param objId
	 */
	public void fetchMarketOfSaleCalcMarkets(String objId) {
		try {
			DomainObject dObjIPS = DomainObject.newInstance(this.ctx, objId);
			this.fetchMarketOfSaleResult(dObjIPS);
		} catch (FrameworkException e) {
			logger.log(Level.WARNING, null, e);
		}
	}

	/**
	 * @param dObjIPS
	 */
	public void fetchMarketOfSaleResult(DomainObject dObjIPS) {

		try {
			StringList objSelect = new StringList(1);
			objSelect.add(DomainConstants.SELECT_NAME);
			StringList relSelect = new StringList(1);
			relSelect.add(MOSOverrideConstants.SELECT_ATTRIBUTE_PGMOSPOAOVERRIDELRR);
			MapList relatedObjects = dObjIPS.getRelatedObjects(this.ctx, // context
					pgV3Constants.RELATIONSHIP_PGCOUNTRYOFSALE, // relPattern
					CPNCommonConstants.TYPE_COUNTRY, // typePattern
					objSelect, // bus select tables
					relSelect, // rel select tables
					false, // getTo
					true, // getFrom
					(short) 1, // recurseToLevel
					"", // objectWhere
					"", // relationshipWhere
					0); // limit

			Set<String> lrrMarkets = new HashSet<String>();
			Set<String> allMarkets = new HashSet<String>();
			if (null != relatedObjects && !relatedObjects.isEmpty()) {
				for (int i = 0; i < relatedObjects.size(); i++) {
					Map mapCountryData = (Map) relatedObjects.get(i);
					String strCountryName = (String) mapCountryData.get(DomainConstants.SELECT_NAME);
					String isLRRMarket = (String) mapCountryData
							.get(MOSOverrideConstants.SELECT_ATTRIBUTE_PGMOSPOAOVERRIDELRR);
					if ("Yes".equalsIgnoreCase(isLRRMarket)) {
						lrrMarkets.add(strCountryName);
					}
					allMarkets.add(strCountryName);
				}
			}

			this.addProductPartIntersectionClearedMarkets(allMarkets);
			this.addChildComponentLRRMarkets(lrrMarkets);

		} catch (FrameworkException e) {
			logger.log(Level.WARNING, null, e);
		}
	}

	/**
	 * @param countries
	 */
	public void calculatePOAOverrideMarkets(Set countries) {
		if (this.isPartHasPOAOverride()) {
			Set<String> commonProductPartMarkets = this.getProductPartIntersectionMarkets();
			commonProductPartMarkets = this.doIntersectionOfMarkets(commonProductPartMarkets, this.getPOAOverrideMarkets());
			if (commonProductPartMarkets != null && !commonProductPartMarkets.isEmpty()) {
				for (String market : commonProductPartMarkets) {
					if (!countries.contains(market)) {
						countries.add(market);
						this.addChildComponentLRRMarkets(market);
					}
				}
			}
		}
	}

	/**
	 * @param commonProductPartMarkets
	 * @param sPOAOverrideMarkets
	 * @return
	 */
	private Set<String> doIntersectionOfMarkets(Set<String> commonProductPartMarkets, Set<String> sPOAOverrideMarkets) {
		if(commonProductPartMarkets != null) {
		commonProductPartMarkets.retainAll(sPOAOverrideMarkets);
		}
		return commonProductPartMarkets;
	}
}

package com.pg.v4.mos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.v3.custom.pgV3Constants;
import com.pg.v4.mos.entity.Market;
import com.pg.v4.mos.entity.OverrideRequest;
import com.pg.v4.mos.entity.Part;
import com.pg.v4.util.mos.override.MOSOverrideConstants;

import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class MOSOverrideReplicate {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	Context context;

	public MOSOverrideReplicate(Context context) {
		this.context = context;
	}

	/**
	 * @param args
	 * @throws MatrixException
	 */
	public void processOverride(String[] args) throws MatrixException {
		String objectOid = args[0];
		logger.log(Level.INFO, "Current Part ID: " + objectOid);
		if (UIUtil.isNotNullAndNotEmpty(objectOid)) {
			processOverride(objectOid);
		} else {
			logger.log(Level.WARNING, "Current Part ID null");
		}
	}

	/**
	 * @param objectOid
	 * @throws MatrixException
	 */
	public void processOverride(String objectOid) throws MatrixException {
		try {
			if (UIUtil.isNotNullAndNotEmpty(objectOid)) {
				Part currentPart = getPart(objectOid);
				logger.log(Level.INFO, "Current Part Bean: " + currentPart);
				String previousId = currentPart.getPreviousId();
				logger.log(Level.INFO, "Previous Part ID: " + previousId);
				if (UIUtil.isNotNullAndNotEmpty(previousId)) {
					Part previousPart = getPart(previousId);
					logger.log(Level.INFO, "Previous Part Bean: " + previousPart);
					executeSteps(currentPart, previousPart);
				} else {
					logger.log(Level.WARNING, "Previous Part ID null");
				}
			} else {
				logger.log(Level.WARNING, "Current Part Instance null");
			}
		} catch (MatrixException e) {
			logger.log(Level.WARNING, "Error occurred while processing override: " + e);
			throw e;
		}
	}

	/**
	 * @param currentPart
	 * @param previousPart
	 * @throws MatrixException
	 */
	private void executeSteps(Part currentPart, Part previousPart) throws MatrixException {
		try {
			if (previousPart.isHasOverride()) { // if previous part has overrides.

				// get previous-part approved-overrides which needs to be replicated to current-part
				List<OverrideRequest> currentPartToDoConnectOverrideList = getPreviousPartApprovedOverrideToReplicate(currentPart, previousPart);

				// if previous-part has approved overrides.
				if (null != currentPartToDoConnectOverrideList && !currentPartToDoConnectOverrideList.isEmpty()) {
					List<String> currentPartToDoConnectOverrideIDsList = currentPartToDoConnectOverrideList
							.stream()
							.map(currentPartToDoConnectOverride -> currentPartToDoConnectOverride.getId())
							.collect(Collectors.toList()); // list of overrides to connect to current part.

					// if previous-part has valid approved overrides ids.
					if (null != currentPartToDoConnectOverrideIDsList && !currentPartToDoConnectOverrideIDsList.isEmpty()) {

						// get current-part approved-overrides which needs to be obsolete manually.
						List<OverrideRequest> currentPartToDoObsoleteOverrideList = getCurrentPartApprovedOverrideToObsolete(currentPart, currentPartToDoConnectOverrideIDsList);

						// promote current-part-approved-override to obsolete state.
						obsoleteCurrentPartApprovedOverrides(currentPart, previousPart, currentPartToDoObsoleteOverrideList);

						// connect previous-part-approved-overrides
						connectPreviousPartApprovedOverrideToCurrentPart(currentPart, previousPart, currentPartToDoConnectOverrideList);

						// connect previous-part-approved-override-markets to current-part.
						connectPreviousPartApprovedOverrideMarketToCurrentPart(previousPart, currentPart, currentPartToDoConnectOverrideList);
					}
				}
				resetOverrideFlagOnCurrentPart(currentPart, previousPart);
			} else {
				logger.log(Level.INFO, "Current Part does not have any override");
			}
		} catch (MatrixException e) {
			logger.log(Level.WARNING, "Error occurred while processing override steps: " + e);
			throw e;
		}
	}
	
	/**
	 * Added by DSM-2022x.5 April CW for MOS (Defect ID- 56878)
	 * @param previousPartOverrideRequestList
	 * @return
	 * @throws FrameworkException
	 */
	
	private boolean checkApprovedOverride(List<OverrideRequest> previousPartOverrideRequestList)  throws FrameworkException{

		boolean isApprovedORConnected= false;
		String policyCOSOverride = PropertyUtil.getSchemaProperty(this.context, "policy_pgCOSOverride");
		String stateApproved = PropertyUtil.getSchemaProperty(this.context, DomainConstants.SELECT_POLICY, policyCOSOverride, "state_Approved");

		try {
			String previousOverrideRequestCurrent;
			if(previousPartOverrideRequestList.size() >1) {
				for (OverrideRequest previousPartOverrideRequest : previousPartOverrideRequestList) {

					previousOverrideRequestCurrent = previousPartOverrideRequest.getCurrent();

					if(stateApproved.equalsIgnoreCase(previousOverrideRequestCurrent)) {
						isApprovedORConnected = true;
						break;
					}

				}
			}
		}catch(Exception e) {
			logger.log(Level.WARNING, "Error occurred while processing method checkApprovedOverride : " + e);
			throw e;
		}

		return isApprovedORConnected;

	}
	/**
     * @param currentPart
     * @param previousPart
     * @throws FrameworkException
     */
	private void resetOverrideFlagOnCurrentPart(Part currentPart, Part previousPart) throws FrameworkException {
        logger.log(Level.INFO, "resetOverrideFlagOnCurrentPart - Start");
        String policyCOSOverride = PropertyUtil.getSchemaProperty(this.context, "policy_pgCOSOverride");
        String stateObsolete = PropertyUtil.getSchemaProperty(this.context, DomainConstants.SELECT_POLICY, policyCOSOverride, "state_Obsolete");

        List<OverrideRequest> previousPartOverrideRequestList = previousPart.getOverrideRequestList();
        List<OverrideRequest> currentPartOverrideRequestList = currentPart.getOverrideRequestList();

        boolean isFullOverride = Boolean.FALSE;
        boolean isPartialOverride = Boolean.FALSE;
        if (null != previousPartOverrideRequestList && !previousPartOverrideRequestList.isEmpty()) { // if previous object has overrides.
            logger.log(Level.INFO, "Previous Part has overrides");
            String previousOverrideRequestCurrent;
            String previousOverrideRequestId;
            String currentPartOverrideRequestId;
            String currentPartOverrideRequestType;
            
           // Modified by DSM-2022x.5 April CW for MOS (Defect ID- 56878) - Starts
            if(!checkApprovedOverride(previousPartOverrideRequestList)) {
            	for (OverrideRequest previousPartOverrideRequest : previousPartOverrideRequestList) { // iterate each previous override object.
            		previousOverrideRequestCurrent = previousPartOverrideRequest.getCurrent();
            		previousOverrideRequestId = previousPartOverrideRequest.getId();
            		if (stateObsolete.equalsIgnoreCase(previousOverrideRequestCurrent)) {
            			logger.log(Level.INFO, "Previous Part override state is: " + previousOverrideRequestCurrent);
            			for (OverrideRequest currentPartOverrideRequest : currentPartOverrideRequestList) { // iterate each current part override.
            				currentPartOverrideRequestId = currentPartOverrideRequest.getId();
            				currentPartOverrideRequestType = currentPartOverrideRequest.getRequestType();
            				logger.log(Level.INFO, "Current Part override request type is: " + currentPartOverrideRequestType);
            				if (previousOverrideRequestId.equalsIgnoreCase(currentPartOverrideRequestId)) { // previous part override is same as current part override.
            					logger.log(Level.INFO, "Previous Part override match with Current Part override:" + currentPartOverrideRequestId);
            					if (MOSOverrideConstants.KEY_FULL_OVERRIDE.equals(currentPartOverrideRequestType)) {
            						isFullOverride = Boolean.TRUE;
            					}
            					if (MOSOverrideConstants.KEY_POA_OVERRIDE.equalsIgnoreCase(currentPartOverrideRequestType)) {
            						isPartialOverride = Boolean.TRUE;
            					}
            				}
            			}
            		}
            	}
            }
           // Modified by DSM-2022x.5 April CW for MOS (Defect ID- 56878) - Ends
        }
        DomainObject domainObject = DomainObject.newInstance(this.context, currentPart.getId());
        if (isFullOverride) {
            logger.log(Level.INFO, "To-do Reset full override on current part");
            String fullCOSOverrideValue = domainObject.getInfo(this.context, pgV3Constants.SELECT_ATTRIBUTE_PGHASCOSFPPOVERRIDDEN);
            logger.log(Level.INFO, "Full override value on current part: " + fullCOSOverrideValue);
            if (pgV3Constants.KEY_YES_VALUE.equalsIgnoreCase(fullCOSOverrideValue)) {
                domainObject.setAttributeValue(this.context, pgV3Constants.ATTRIBUTE_PGHASCOSFPPOVERRIDDEN, pgV3Constants.KEY_NO_VALUE);
                logger.log(Level.INFO, "Reset full override on current part - completed");
            }
        }
        if (isPartialOverride) {
            logger.log(Level.INFO, "To-do Reset partial override on current part");
            String partialCOSOverrideValue = domainObject.getInfo(this.context, MOSOverrideConstants.SELECT_ATTRIBUTE_PGHASMOSPARTIALOVERRIDDEN);
            logger.log(Level.INFO, "Partial override value on current part: " + partialCOSOverrideValue);
            if (pgV3Constants.KEY_YES_VALUE.equalsIgnoreCase(partialCOSOverrideValue)) {
                domainObject.setAttributeValue(this.context, MOSOverrideConstants.ATTRIBUTE_PGHASMOSPARTIALOVERRIDDEN, pgV3Constants.KEY_NO_VALUE);
                logger.log(Level.INFO, "Reset partial override on current part - completed");
            }
        }
        logger.log(Level.INFO, "resetOverrideFlagOnCurrentPart - End");
    }

	/**
	 * @param currentPart
	 * @param previousPart
	 * @return
	 */
	private List<OverrideRequest> getPreviousPartApprovedOverrideToReplicate(Part currentPart, Part previousPart) {
		List<OverrideRequest> toConnectCurrentPartOverrideList = new ArrayList<>();
		String policyCOSOverride = PropertyUtil.getSchemaProperty(this.context, "policy_pgCOSOverride");
		String stateApproved = PropertyUtil.getSchemaProperty(this.context, DomainConstants.SELECT_POLICY, policyCOSOverride, "state_Approved");

		List<OverrideRequest> previousPartOverrideRequestList = previousPart.getOverrideRequestList();
		List<String> currentPartConnectedOverrideIDsList = currentPart.getOverrideRequestList().stream().map(overrideRequest -> overrideRequest.getId()).collect(Collectors.toList());

		if (null != previousPartOverrideRequestList && !previousPartOverrideRequestList.isEmpty()) {
			for (OverrideRequest previousOverrideRequest : previousPartOverrideRequestList) {
				String previousOverrideRequestCurrent = previousOverrideRequest.getCurrent();
				logger.log(Level.INFO, String.format("Previous Part Override current state: %s", previousOverrideRequestCurrent));
				if (stateApproved.equalsIgnoreCase(previousOverrideRequestCurrent)) { // if previous part override is approved state.
					String previousOverrideRequestId = previousOverrideRequest.getId();
					if (UIUtil.isNotNullAndNotEmpty(previousOverrideRequestId)) { // if previous-override-part id is not empty
						logger.log(Level.INFO, String.format("Previous Part Override Name: %s", previousOverrideRequest.getName()));
						logger.log(Level.INFO, String.format("Previous Part Override ID: %s", previousOverrideRequestId));
						if (!currentPartConnectedOverrideIDsList.contains(previousOverrideRequestId)) { // if previous-override-part id is not present in the current-part-connected-override-list.
							logger.log(Level.INFO, "Previous Part Override ID is not connected on Current Part");
							toConnectCurrentPartOverrideList.add(previousOverrideRequest);
						} else {
							logger.log(Level.WARNING, "Previous Part Override ID is already connected on Current Part");
						}
					} else {
						logger.log(Level.WARNING, "Previous Part Override ID null");
					}
				} else {
					logger.log(Level.INFO, "Previous Part Override current state is not Approved");
				}
			}
		} else {
			logger.log(Level.WARNING, "Previous Part does not have any overrides");
		}
		logger.log(Level.INFO, String.format("Previous-Part-Overrides which need to be connected to be Current-Part: %s", toConnectCurrentPartOverrideList));
		return toConnectCurrentPartOverrideList;
	}

	/**
	 * @param currentPart
	 * @param currentPartToDoConnectOverrideIDsList
	 * @return
	 */
	private List<OverrideRequest> getCurrentPartApprovedOverrideToObsolete(Part currentPart, List<String> currentPartToDoConnectOverrideIDsList) {
		List<OverrideRequest> currentPartPromoteOverrideList = new ArrayList<>();
		if (currentPart.isHasOverride()) {
			String policyCOSOverride = PropertyUtil.getSchemaProperty(this.context, "policy_pgCOSOverride");
			String stateApproved = PropertyUtil.getSchemaProperty(this.context, DomainConstants.SELECT_POLICY, policyCOSOverride, "state_Approved");
			String stateObsolete = PropertyUtil.getSchemaProperty(this.context, DomainConstants.SELECT_POLICY, policyCOSOverride, "state_Obsolete");

			List<OverrideRequest> currentPartOverrideRequestList = currentPart.getOverrideRequestList();
			if (null != currentPartOverrideRequestList && !currentPartOverrideRequestList.isEmpty()) {
				for (OverrideRequest currentPartOverride : currentPartOverrideRequestList) {
					String currentPartOverrideCurrentState = currentPartOverride.getCurrent();
					logger.log(Level.INFO, String.format("Current Part connected override state is: %s", currentPartOverrideCurrentState));
					if (stateApproved.equalsIgnoreCase(currentPartOverrideCurrentState) || stateObsolete.equalsIgnoreCase(currentPartOverrideCurrentState)) {
						String currentPartOverrideId = currentPartOverride.getId();
						if (!currentPartToDoConnectOverrideIDsList.contains(currentPartOverrideId)) {
							currentPartPromoteOverrideList.add(currentPartOverride);
						} else {
							logger.log(Level.WARNING, "Current Part connected override is present in (to-be) connect list");
						}
					} else {
						logger.log(Level.WARNING, "Current Part connected override not in Approved state");
					}
				}
			} else {
				logger.log(Level.WARNING, "Current Part connected override list null or empty");
			}
		} else {
			logger.log(Level.WARNING, "Current Part does not have any connected overrides");
		}
		logger.log(Level.INFO, String.format("Current-Part-Overrides which need to be demoted to Obsolete state: %s", currentPartPromoteOverrideList));
		return currentPartPromoteOverrideList;
	}


	/**
	 * @param currentPart
	 * @param previousPart
	 * @param currentPartToDoConnectOverrideList
	 * @throws FrameworkException
	 */
	private void connectPreviousPartApprovedOverrideToCurrentPart(Part currentPart, Part previousPart, List<OverrideRequest> currentPartToDoConnectOverrideList) throws FrameworkException {
		try {
			if (null != currentPartToDoConnectOverrideList && !currentPartToDoConnectOverrideList.isEmpty()) {
				List<String> toConnectOverrideIDsList = currentPartToDoConnectOverrideList.stream().map(overrideRequest -> overrideRequest.getId()).collect(Collectors.toList());
				if (null != toConnectOverrideIDsList && !toConnectOverrideIDsList.isEmpty()) {
					logger.log(Level.INFO, String.format("Override ID List: %s", toConnectOverrideIDsList));
					DomainRelationship.connect(this.context,
							DomainObject.newInstance(this.context, currentPart.getId()),
							pgV3Constants.RELATIONSHIP_PGCOUNTRYOFSALEOVERRIDE, true,
							toConnectOverrideIDsList.toArray(String[]::new));
					logger.log(Level.INFO, "Previous-Part-Approved-Overrides connected successfully to Current-Part");
				} else {
					logger.log(Level.INFO, "Previous-Part-Approved-Overrides No Market.");
				}
			} else {
				logger.log(Level.INFO, "Previous-Part-Approved-Overrides No Market to disconnect.");
			}
		} catch (FrameworkException e) {
			logger.log(Level.WARNING, "Error occurred while connecting previous part overrides to current part: " + e);
			throw e;
		}
	}

	/**
	 * @param previousPart
	 * @param currentPart
	 * @param currentPartToDoConnectOverrideList
	 * @throws FrameworkException
	 */
	private void connectPreviousPartApprovedOverrideMarketToCurrentPart(Part previousPart, Part currentPart, List<OverrideRequest> currentPartToDoConnectOverrideList) throws FrameworkException {
		try {
			boolean isPartialOverride = Boolean.FALSE;
			boolean isFullOverride = Boolean.FALSE;
			Set<String> fullOverrideMarketList = new HashSet<>();

			DomainObject currentObject = DomainObject.newInstance(this.context, currentPart.getId());
			List<Market> alreadyConnectedMarkets = currentPart.getMarketList();
			List<String> alreadyConnectedMarketList = new ArrayList<>();
			for (Market market : alreadyConnectedMarkets) {
				alreadyConnectedMarketList.add(market.getId());
			}
			if (null != currentPartToDoConnectOverrideList && !currentPartToDoConnectOverrideList.isEmpty()) {
				Map<String, String> attributeMap;
				DomainRelationship domainRelationship;
				for (OverrideRequest overrideRequest : currentPartToDoConnectOverrideList) {
					List<Market> marketList = overrideRequest.getMarketList();
					if (null != marketList && !marketList.isEmpty()) {
						String marketId;
						for (Market market : marketList) {
							marketId = market.getId();
							if (overrideRequest.isFullOverride()) {
								fullOverrideMarketList.add(marketId);
								isFullOverride = Boolean.TRUE;
							} else {
								if (!alreadyConnectedMarketList.contains(marketId)) {
									attributeMap = new HashMap<>();
									attributeMap.put(MOSOverrideConstants.ATTRIBUTE_PGMOSPOAOVERRIDELRR, market.getPartialOverrideLRR());
									attributeMap.put(pgV3Constants.ATTRIBUTE_PGCOSRESTRICTION, (String) market.getRestriction());
									domainRelationship = DomainRelationship.connect(this.context,
											currentObject,
											pgV3Constants.RELATIONSHIP_PGCOUNTRYOFSALE,
											DomainObject.newInstance(this.context, marketId));
									logger.log(Level.INFO, "Connected Market POA: " + market.getName());
									// update relationship attribute for poa-override.
									domainRelationship.setAttributeValues(this.context, attributeMap);
									logger.log(Level.INFO, "Updated Market Relationship Attribute");
									isPartialOverride = Boolean.TRUE;
								} else {
									logger.log(Level.WARNING, "POA Override - Market is already connected:" + market.getName());
								}
							}
						}
					}
				}
			}
			if (!fullOverrideMarketList.isEmpty()) {
				DomainRelationship.connect(this.context,
						DomainObject.newInstance(this.context, currentPart.getId()),
						pgV3Constants.RELATIONSHIP_PGCOUNTRYOFSALE,
						Boolean.TRUE,
						fullOverrideMarketList.toArray(String[]::new));
				logger.log(Level.INFO, "Previous-Part-Approved-Override Market is connected successfully to Current-Part");
			} else {
				logger.log(Level.INFO, "Previous-Part-Approved-Override Market No Market");
			}
			if (isFullOverride) {
				currentObject.setAttributeValue(this.context, pgV3Constants.ATTRIBUTE_PGHASCOSFPPOVERRIDDEN, pgV3Constants.KEY_YES_VALUE);
				currentObject.setAttributeValue(this.context, MOSOverrideConstants.ATTRIBUTE_PGHASMOSPARTIALOVERRIDDEN, pgV3Constants.KEY_NO_VALUE);
			}
			if (isPartialOverride) {
				currentObject.setAttributeValue(this.context, MOSOverrideConstants.ATTRIBUTE_PGHASMOSPARTIALOVERRIDDEN, pgV3Constants.KEY_YES_VALUE);
			}
		} catch (FrameworkException e) {
			logger.log(Level.WARNING, "Error occurred while connecting previous part override-markets to current part: " + e);
			throw e;
		}
	}


	/**
	 * @param currentPart
	 * @param previousPart
	 * @param currentPartToDoObsoleteOverrideList
	 * @throws MatrixException
	 */
	private void obsoleteCurrentPartApprovedOverrides(Part currentPart, Part previousPart, List<OverrideRequest> currentPartToDoObsoleteOverrideList) throws MatrixException {
		try {
			String policyCOSOverride = PropertyUtil.getSchemaProperty(this.context, "policy_pgCOSOverride");
			String stateApproved = PropertyUtil.getSchemaProperty(this.context, DomainConstants.SELECT_POLICY, policyCOSOverride, "state_Approved");
			String stateObsolete = PropertyUtil.getSchemaProperty(this.context, DomainConstants.SELECT_POLICY, policyCOSOverride, "state_Obsolete");
			Set<String> marketRelIDsList = new HashSet<>();
			if (null != currentPartToDoObsoleteOverrideList && !currentPartToDoObsoleteOverrideList.isEmpty()) {
				DomainObject overrideObj = DomainObject.newInstance(this.context);
				for (OverrideRequest currentPartOverride : currentPartToDoObsoleteOverrideList) {
					String currentPartOverrideState = currentPartOverride.getCurrent();
					logger.log(Level.INFO, String.format("Current-Part-Override state is: %s", currentPartOverrideState));
					if (stateApproved.equalsIgnoreCase(currentPartOverrideState) || stateObsolete.equalsIgnoreCase(currentPartOverrideState)) {
						String currentPartOverrideId = currentPartOverride.getId();
						if (UIUtil.isNotNullAndNotEmpty(currentPartOverrideId) && currentPartOverride.isFullOverride()) {
							if (stateApproved.equalsIgnoreCase(currentPartOverrideState)) {
								overrideObj.setId(currentPartOverrideId);
								overrideObj.promote(this.context); // promote to obsolete state.
								logger.log(Level.INFO, String.format("Current-Part-Override Obsoleted now: %s", currentPartOverrideId));
							}
							List<Market> marketList = currentPartOverride.getMarketList();
							if (null != marketList && !marketList.isEmpty()) {
								for (Market market : marketList) {
									logger.log(Level.INFO, String.format("Current-Part-Override to be disconnected market: %s", market));
									marketRelIDsList.add(market.getRelId());
								}
							} else {
								logger.log(Level.WARNING, "Current-Part-Override connect Market List is null or empty");
							}
						} else {
							logger.log(Level.WARNING, "Current-Part-Override ID is null");
						}
					} else {
						logger.log(Level.WARNING, "Current-Part-Override is not in Approved state");
					}
				}
			}
			if (previousPart.isFullOverride()) {
				List<Market> marketList = currentPart.getMarketList();
				if (null != marketList && !marketList.isEmpty()) {
					String relId;
					for (Market market : marketList) {
						relId = market.getRelId();
						if (!marketRelIDsList.contains(relId)) {
							marketRelIDsList.add(relId);
						}
					}
				}
				if (!marketRelIDsList.isEmpty()) {
					DomainRelationship.disconnect(this.context, marketRelIDsList.toArray(new String[marketRelIDsList.size()]));
					logger.log(Level.INFO, "Obsolete Current-Part-Overrides-Markets Disconnected.");
				} else {
					logger.log(Level.INFO, "Obsolete Current-Part-Overrides-Markets NA.");
				}
			}
		} catch (MatrixException e) {
			logger.log(Level.WARNING, "Error occurred while promoting current part approved overrides to obsolete state: " + e);
			throw e;
		}
	}

	/**
	 * @param domainObject
	 * @return
	 * @throws FrameworkException
	 */
	private List<Market> getRelatedMarketList(DomainObject domainObject) throws FrameworkException {
		List<Market> marketList = new ArrayList<>();
		try {
			StringList objectSelects = new StringList(1);
			objectSelects.add(DomainConstants.SELECT_ID);
			objectSelects.add(DomainConstants.SELECT_NAME);
			objectSelects.add(DomainConstants.SELECT_TYPE);
			objectSelects.add(DomainConstants.SELECT_CURRENT);
			objectSelects.add(DomainConstants.SELECT_REVISION);

			StringList relSelects = new StringList(3);
			relSelects.add(DomainRelationship.SELECT_ID);
			relSelects.add(MOSOverrideConstants.SELECT_ATTRIBUTE_PGMOSPOAOVERRIDELRR);
			relSelects.add("attribute[" + pgV3Constants.ATTRIBUTE_PGCOSRESTRICTION + "]");
			marketList = getMarketList(getRelatedMarkets(domainObject, objectSelects, relSelects));
		} catch (FrameworkException e) {
			logger.log(Level.WARNING, "Error occurred while fetching related markets: " + e);
			throw e;
		}
		return marketList;
	}

	/**
	 * @param domainObject
	 * @return
	 * @throws FrameworkException
	 */
	private List<Market> getMarketList(DomainObject domainObject) throws FrameworkException {
		List<Market> marketList = new ArrayList<>();
		try {
			StringList objectSelects = new StringList(1);
			objectSelects.add(DomainConstants.SELECT_ID);
			objectSelects.add(DomainConstants.SELECT_NAME);

			StringList relSelects = new StringList(3);
			relSelects.add(DomainRelationship.SELECT_ID);
			relSelects.add(MOSOverrideConstants.SELECT_ATTRIBUTE_PGMOSPOAOVERRIDELRR);
			relSelects.add("attribute[" + pgV3Constants.ATTRIBUTE_PGCOSRESTRICTION + "]");

			MapList relatedMarkets = getRelatedMarkets(domainObject, objectSelects, relSelects);
			if (null != relatedMarkets && !relatedMarkets.isEmpty()) {
				Iterator iterator = relatedMarkets.iterator();
				Map<Object, Object> objectMap;
				while (iterator.hasNext()) {
					objectMap = (Map<Object, Object>) iterator.next();
					marketList.add(new Market(objectMap));
				}
			}
		} catch (FrameworkException e) {
			logger.log(Level.WARNING, "Error occurred while fetching related market list: " + e);
			throw e;
		}
		return marketList;
	}

	/**
	 * @param domainObject
	 * @param objectSelects
	 * @param relSelects
	 * @return
	 * @throws FrameworkException
	 */
	MapList getRelatedMarkets(DomainObject domainObject, StringList objectSelects, StringList relSelects) throws FrameworkException {
		return domainObject.getRelatedObjects(this.context, pgV3Constants.RELATIONSHIP_PGCOUNTRYOFSALE,     // relationship pattern
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
	 * @param domainObject
	 * @return
	 * @throws FrameworkException
	 */
	MapList getRelatedOverrides(DomainObject domainObject) throws FrameworkException {
		StringList busSelects = new StringList();
		busSelects.add(DomainConstants.SELECT_ID);
		busSelects.add(DomainConstants.SELECT_CURRENT);
		busSelects.add(DomainConstants.SELECT_NAME);
		busSelects.add(DomainConstants.SELECT_TYPE);
		busSelects.add(DomainConstants.SELECT_REVISION);
		busSelects.add(MOSOverrideConstants.SELECT_ATTRIBUTE_PGMOSOVERRIDEREQUESTTYPE);
		busSelects.add(MOSOverrideConstants.SELECT_ATTRIBUTE_OVERRIDE_COUNTRIES);

		StringList relSelects = new StringList();
		relSelects.add(DomainRelationship.SELECT_ID);

		return domainObject.getRelatedObjects(this.context, pgV3Constants.RELATIONSHIP_PGCOUNTRYOFSALEOVERRIDE, // relationship pattern
				pgV3Constants.TYPE_PGCOSOVERRIDE,                   // Type pattern
				busSelects,                                         // object selects
				relSelects,                   // rel selects
				false,                                              // to side
				true,                                               // from side
				(short) 1,                                          // recursion level
				DomainConstants.EMPTY_STRING,                       // object where clause
				DomainConstants.EMPTY_STRING,                       // rel where clause
				0);                                                 // limit
	}

	/**
	 * @param relatedMarketList
	 * @param relatedOverrideList
	 * @return
	 * @throws FrameworkException
	 */
	private List<OverrideRequest> getOverrideRequestList(List<Market> relatedMarketList, MapList relatedOverrideList) {
		List<OverrideRequest> overrideRequestList = new ArrayList<>();
		if (null != relatedOverrideList && !relatedOverrideList.isEmpty()) {
			Iterator iterator = relatedOverrideList.iterator();
			Map<Object, Object> objectMap;
			String markets;
			while (iterator.hasNext()) {
				objectMap = (Map<Object, Object>) iterator.next();
				// market names is stored on override object attribute (pgCOSOverrideCountries) as pipe separated.
				markets = (String) objectMap.get(MOSOverrideConstants.SELECT_ATTRIBUTE_OVERRIDE_COUNTRIES);
				if (UIUtil.isNullOrEmpty(markets)) {
					logger.log(Level.WARNING, "Override object does not have Market Name stored on its attribute");
				}
				// get the matching market from the (current part related market list) and the stored market name list
				objectMap.put("marketList", getMarketList(StringUtil.split(markets, pgV3Constants.SYMBOL_PIPE), relatedMarketList));
				overrideRequestList.add(new OverrideRequest(objectMap));
			}
		}
		return overrideRequestList;
	}

	/**
	 * @param storedMarketNameList
	 * @param relatedMarketList
	 * @return
	 * @throws FrameworkException
	 */
	private List<Market> getMarketList(StringList storedMarketNameList, List<Market> relatedMarketList) {
		List<Market> marketList = new ArrayList<>();
		if (null != storedMarketNameList && !storedMarketNameList.isEmpty()) {
			for (String storedMarketName : storedMarketNameList) {
				if (UIUtil.isNotNullAndNotEmpty(storedMarketName)) {
					for (Market market : relatedMarketList) {
						if (storedMarketName.equalsIgnoreCase(market.getName())) {
							marketList.add(market);
							break; // if matching market name is found - break this loop.
						}
					}
				}
			}
		}
		return marketList;
	}

	/**
	 * @param objectOid
	 * @return
	 * @throws FrameworkException
	 */
	private Part getPart(String objectOid) throws FrameworkException {
		DomainObject domainObject = DomainObject.newInstance(context, objectOid);
		StringList objectSelects = new StringList();
		objectSelects.add(DomainConstants.SELECT_ID);
		objectSelects.add(DomainConstants.SELECT_TYPE);
		objectSelects.add(DomainConstants.SELECT_NAME);
		objectSelects.add(DomainConstants.SELECT_REVISION);
		objectSelects.add(DomainConstants.SELECT_CURRENT);
		objectSelects.add("previous.id");
		objectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGHASCOSFPPOVERRIDDEN); // full-override
		objectSelects.add(MOSOverrideConstants.SELECT_ATTRIBUTE_PGHASMOSPARTIALOVERRIDDEN); // partial-override.

		Map objectInfo = domainObject.getInfo(this.context, objectSelects);

		List<Market> relatedMarketList = getRelatedMarketList(domainObject); // get markets connected to part
		objectInfo.put("marketList", relatedMarketList);

		MapList relatedOverrides = getRelatedOverrides(domainObject);
		List<OverrideRequest> overrideRequestList = getOverrideRequestList(relatedMarketList, relatedOverrides);
		objectInfo.put("overrideRequestList", overrideRequestList);

		return new Part(objectInfo);
	}

	/**
	 * @param objectList
	 * @return
	 */
	private List<Market> getMarketList(MapList objectList) {
		List<Market> marketList = new ArrayList<>();
		if (null != objectList && !objectList.isEmpty()) {
			for (Object object : objectList) {
				marketList.add(new Market((Map<Object, Object>) object));
			}
		}
		return marketList;
	}
}

package com.pg.dsm.rollup_event.mark.services;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.rollup_event.common.config.ConfigBean;
import com.pg.dsm.rollup_event.common.config.rule.Config;
import com.pg.dsm.rollup_event.common.ebom.ProductPart;
import com.pg.dsm.rollup_event.common.resources.RollupMatrixResource;
import com.pg.dsm.rollup_event.common.resources.RollupPageResource;
import com.pg.dsm.rollup_event.common.resources.RollupPropertyResource;
import com.pg.dsm.rollup_event.common.resources.RollupRuleConfigurator;
import com.pg.dsm.rollup_event.common.util.ConfigUtil;
import com.pg.dsm.rollup_event.enumeration.RollupConstants;
import com.pg.dsm.rollup_event.mark.models.MarketRegistrationFlag;
import com.pg.dsm.rollup_event.mark.models.OtherFlag;
import com.pg.dsm.rollup_event.mark.models.RollupFlag;
import com.pg.dsm.rollup_event.mark.util.MarkAction;
import com.pg.dsm.rollup_event.mark.util.MarkUtil;
import com.pg.dsm.rollup_event.rollup.models.DynamicSubscription;
import com.pg.dsm.rollup_event.rollup.services.Resource;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.StringList;

public class MarkRollupExecutor {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    MarkRollupParameter markRollupParameter;

    public MarkRollupExecutor(MarkRollupParameter markRollupParameter) {
        this.markRollupParameter = markRollupParameter;
    }

    public void execute() {
        Resource resources = getResources();
        if (null != resources && resources.isLoaded()) {
            // Loading all required resources for CTRLM execution
            Context context = resources.getContext();
            RollupPropertyResource rollupPropertyResource = resources.getRollupPropertyResource();
            String markingConfigBusinessObjectName = rollupPropertyResource.getMarkingConfigBusinessObjectName();
            String configObjectOid = ConfigUtil.getObjectId(context, markingConfigBusinessObjectName);

            if (UIUtil.isNotNullAndNotEmpty(configObjectOid)) {
                ConfigBean configBean = new ConfigBean(ConfigUtil.getObjectInfo(context, configObjectOid));
                //Notify email to user if CTRLM 1 is active
                if (configBean.isActive()) {
                    String emailMessageBody = ConfigUtil.getEmailMessageBody(rollupPropertyResource.getMarkForRollupMessage());
                    String emailSubject = rollupPropertyResource.getMarkForRollupSubject();
                    boolean bIsUpdatedRetryCountAndNotify = ConfigUtil.updateRetryCountAndNotify(context, configBean, emailMessageBody, emailSubject);
                    if (!bIsUpdatedRetryCountAndNotify) {
                        logger.log(Level.WARNING, "Failed to update retry count on Config object / send notification email");
                    }
                } else {
                    // Executes CTRLM1 and resets config objects post execution
                    ConfigUtil.resetConfigObject(context, configBean, false);
                    boolean isMarkingProcessExecuted = executeMarkingProcess(resources);
                    if (isMarkingProcessExecuted) {
                        ConfigUtil.resetConfigObject(context, configBean, true);
                    } else {
                        logger.log(Level.WARNING, "Marking Cron execution Failed");
                    }
                }
            } else {
                logger.log(Level.WARNING, "Config Business Object ID is null");
            }
        } else {
            logger.log(Level.WARNING, "Unable to load resources.");
        }
    }

    /**
     * This method loads all required resources for CTRLM execution
     *
     * @return Resource Bean
     */
    private Resource getResources() {
        Resource resource = null;
        RollupPropertyResource rollupPropertyResource = new RollupPropertyResource.Builder(RollupConstants.Basic.ROLLUP_PROPERTIES_PATH.getValue()).build();
        if (rollupPropertyResource.isLoaded()) {
            Context context = new RollupMatrixResource(rollupPropertyResource).getContext();
            if (context.isConnected()) {
                Properties rollupPageConfigProperties = new RollupPageResource(context, pgV3Constants.ROLLUPPAGEFILE).getConfigProperties();

                Config rollupRuleConfiguration = new RollupRuleConfigurator.Builder(context).build().getConfig();

                //Modified for Defect# 44220 - Starts
                resource = new Resource.Builder()
                        .setContext(context)
                        .setCustomProperties(rollupPropertyResource.getProperties())
                        .setRollupPropertyResource(rollupPropertyResource)
                        .setRollupPageProperties(rollupPageConfigProperties)
                        .setRollupRuleConfiguration(rollupRuleConfiguration)
                        .setExecutionType(markRollupParameter.getExecutionType())
                        .setDynamicSubscription(new DynamicSubscription())
                        .setPerformCircularCheck()
                        .build();
                //Modified for Defect# 44220 - Ends
            }
        }
        return resource;
    }

    /**
     * This method process marked objects for where used parents marking
     *
     * @param resource
     * @return boolean execution status
     */
    private boolean executeMarkingProcess(Resource resource) {
        boolean bIsExecuted = true;
        try {
            MarkAction markAction = new MarkAction(resource);
            List<ProductPart> markedProductPartList = markAction.getProductPartsMarkedForRollup();
            logger.log(Level.INFO, "Number of Marked Objects: {0}", markedProductPartList.size());
            ProductPart markedProductPart;
            String objectOid;

            Map<String, Map<String, String>> rollupEventAttributeMap = new HashMap<>();
            Context context = resource.getContext();
            DomainObject dObj = DomainObject.newInstance(context);
            Iterator<?> markedProductPartItr = markedProductPartList.iterator();
            //Modified by DSM (Sogeti) for defect# 44589 - Starts
            StringList slNonCircularFPPObjects = new StringList(1);
            //Modified by DSM (Sogeti) for defect# 44589 - Ends

            String sAllowedTypesForCircularCheck = resource.getRollupPropertyResource().getAllowedTypesForCircularCheck();

            while (markedProductPartItr.hasNext()) {
                markedProductPart = (ProductPart) markedProductPartItr.next();
                objectOid = markedProductPart.getId();
                markAction.setMarkedProductPart(markedProductPart);
                logger.log(Level.INFO, "Processing Starts for Marked Object with ID: {0}", objectOid);
                if (UIUtil.isNotNullAndNotEmpty(objectOid)) {
                    dObj.setId(objectOid);

                    //Modified by DSM (Sogeti) for defect# 43716 - Starts
                    //Modified by DSM (Sogeti) for defect# 44589 - Starts
                    //Performs Circular check only for FPP types and un marks FPP if circular reference is found

                    if (sAllowedTypesForCircularCheck.contains(markedProductPart.getType()) && MarkUtil.isCircularExists(context, objectOid, resource)) {
                        //Modified by DSM (Sogeti) for defect# 44589 - Ends

                        MarkUtil.resetMarking(context, markedProductPart);
                    } else {
                        // process mark for roll-up flag.
                        // Below If condition will be processed only on Release events of FPP/CUP/IP/COP/PAP/FAB
                        if (pgV3Constants.KEY_TRUE.equalsIgnoreCase(markedProductPart.getMarkForRollupFlag())) {

                            // Processing Market Registration Rollup on PAP, FAB on Release event
                            if (MarkUtil.isRegistrationParentType(markedProductPart)) {
                                MarketRegistrationFlag marketRegistration = new MarketRegistrationFlag();
                                marketRegistration.processForMarketRegistrationRollup(context, markedProductPart, resource);
                            }

                            //Marking FPP with CalculateRollup
                            if (pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(markedProductPart.getType())) {
                                slNonCircularFPPObjects.add(objectOid);
                                // If FPPs contains only Market Registration Event, Ignoring FPP for where used Parent FPP marking
                                if (markedProductPart.getEventForRollup().equalsIgnoreCase(pgV3Constants.EVENT_MARKET_REGISTRATION)) {
                                    continue;
                                }
                            }

                            // Gets the Where used parent FPP's for the Intermediate objects
                            RollupFlag rollupFlag = new RollupFlag(markedProductPart, resource, markAction);
                            Map<String, Map<String, String>> rollupFlagEventAttributeMap = rollupFlag.getRollupEventAttributeMap();

                            rollupEventAttributeMap.putAll(markAction.updateEventAttributeMap(rollupEventAttributeMap, rollupFlagEventAttributeMap));
                        }
                        // Gets the Where used parent FPP's for the individual rollup flagged objects
                        OtherFlag otherFlag = new OtherFlag(markedProductPart, resource, markAction);
                        Map<String, Map<String, String>> otherFlagEventAttributeMap = otherFlag.getRollupEventAttributeMap();
                        rollupEventAttributeMap.putAll(markAction.updateEventAttributeMap(rollupEventAttributeMap, otherFlagEventAttributeMap));


                        // un-flag
                        if (!pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(markedProductPart.getType())) {
                            dObj.setAttributeValues(context, MarkUtil.getUnflagAttributeMap(markedProductPart));
                        }

                    }
                    //Modified by DSM (Sogeti) for defect# 43716 - Ends

                }
                logger.log(Level.INFO, "Processing Ends for Marked Object with ID: {0}", objectOid);
            }

            //Adding processed FPP for Calculate for Rollup Marking
            rollupEventAttributeMap.putAll(markAction.updateEventAttributeMap(rollupEventAttributeMap, MarkUtil.getFPPMarkingMap(context, slNonCircularFPPObjects)));

            if (!rollupEventAttributeMap.isEmpty()) {
                Map<String, String> attributeMap;
                for (Map.Entry<String, Map<String, String>> entry : rollupEventAttributeMap.entrySet()) {
                    dObj.setId(entry.getKey());
                    //Modified by DSM (Sogeti) for defect# 44589 - Starts
                    //Modified by DSM (Sogeti) for defect# 43716 - Starts
                    if (slNonCircularFPPObjects.contains(entry.getKey()) || !MarkUtil.isCircularExists(context, entry.getKey(), resource)) {
                        //Modified by DSM (Sogeti) for defect# 44589 - Ends
                        attributeMap = entry.getValue();
                        dObj.setAttributeValues(context, attributeMap);
                    } else {
                        MarkUtil.resetFPPMap(context, dObj);
                    }
                    //Modified by DSM (Sogeti) for defect# 43716 - Ends
                }
            }
        } catch (FrameworkException e) {
            bIsExecuted = false;
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return bIsExecuted;
    }
}

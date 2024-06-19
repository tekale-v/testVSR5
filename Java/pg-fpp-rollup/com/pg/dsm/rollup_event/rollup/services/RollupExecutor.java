package com.pg.dsm.rollup_event.rollup.services;

import java.io.File;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.rollup_event.common.config.ConfigBean;
import com.pg.dsm.rollup_event.common.config.rule.Config;
import com.pg.dsm.rollup_event.common.config.rule.Rollup;
import com.pg.dsm.rollup_event.common.config.rule.Rule;
import com.pg.dsm.rollup_event.common.ebom.ChildExpansion;
import com.pg.dsm.rollup_event.common.ebom.ProductPart;
import com.pg.dsm.rollup_event.common.interfaces.RollupEvent;
import com.pg.dsm.rollup_event.common.interfaces.RollupEventFactory;
import com.pg.dsm.rollup_event.common.resources.RollupMatrixResource;
import com.pg.dsm.rollup_event.common.resources.RollupPageResource;
import com.pg.dsm.rollup_event.common.resources.RollupPropertyResource;
import com.pg.dsm.rollup_event.common.resources.RollupRuleConfigurator;
import com.pg.dsm.rollup_event.common.util.ConfigUtil;
import com.pg.dsm.rollup_event.enumeration.RollupConstants;
import com.pg.dsm.rollup_event.mark.models.ReadyFlag;
import com.pg.dsm.rollup_event.rollup.models.DynamicSubscription;
import com.pg.dsm.rollup_event.rollup.util.RollupAction;
import com.pg.dsm.rollup_event.rollup.util.RollupUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.StringList;

public class RollupExecutor {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    RollupParameter rollupParameter;

    public RollupExecutor(RollupParameter rollupParameter) {
        this.rollupParameter = rollupParameter;
    }

    /*
     * Modified by DSM (Sogeti) for defect# 43716
     */
    public void execute() {
        Resource resources = getResources();
        if (rollupParameter.isInputFileProvided()) {
            executeWithInputFile(resources);
        } else {
            executeWithoutInputFile(resources);
        }
    }

    /*
     * Modified by DSM (Sogeti) for defect# 43716
     */
    public void executeWithoutInputFile(Resource resources) {
        if (null != resources && resources.isLoaded()) {
            Context context = resources.getContext();
            RollupPropertyResource rollupPropertyResource = resources.getRollupPropertyResource();
            String configObjectOid = ConfigUtil.getObjectId(context, rollupPropertyResource.getRollupConfigBusinessObjectName());
            if (UIUtil.isNotNullAndNotEmpty(configObjectOid)) {
                ConfigBean configBean = new ConfigBean(ConfigUtil.getObjectInfo(context, configObjectOid));
                if (configBean.isActive()) {
                    String emailMessageBody = ConfigUtil.getEmailMessageBody(rollupPropertyResource.getRollupMessage());
                    String emailSubject = rollupPropertyResource.getRollupSubject();
                    boolean bIsUpdatedRetryCountAndNotify = ConfigUtil.updateRetryCountAndNotify(context, configBean,
                            emailMessageBody,
                            emailSubject);
                    if (!bIsUpdatedRetryCountAndNotify) {
                        logger.log(Level.WARNING, "Failed to update retry count on Config object / send notification email");
                    }
                } else {
                    ConfigUtil.resetConfigObject(context, configBean, false);
                    boolean isMarkingProcessExecuted = executeRollupProcess(resources);
                    if (isMarkingProcessExecuted) {
                        ConfigUtil.resetConfigObject(context, configBean, true);
                    } else {
                        logger.log(Level.WARNING, "Rollup Cron execution Failed");
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
     * This method executes FPP rollup when input file name is provided as parameter
     * Added by DSM (Sogeti) for defect# 43716
     */
    public void executeWithInputFile(Resource resource) {
        boolean isMarkingProcessExecuted = executeRollupProcess(resource);
        if (isMarkingProcessExecuted) {
            logger.info("ExecuteWithInputFile Rollup Process executed successfully.");
        }
    }


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
                        .setDynamicSubscription(new DynamicSubscription())
                        .setExecutionType(rollupParameter.getExecutionType())
                        .setRollupParameter(rollupParameter)
                        .setSlProcessedObjectsList(new StringList())
                        .setPerformCircularCheck()
                        .build();
                //Modified for Defect# 44220 - Ends
            }
        }
        return resource;
    }

    public boolean executeRollupProcess(Resource resource) {
        boolean bIsExecuted = true;
        try {
            RollupAction rollupAction = new RollupAction(resource);
            Context context = resource.getContext();

            // get marked finished product part.
            MapList objectList = null;
            //Modified by DSM (Sogeti) for defect# 43716 - Starts
            if (rollupParameter.isInputFileProvided()) {
                objectList = rollupAction.getInputFinishedProductParts(new StringBuffer(resource.getRollupPropertyResource().getProperties()
                        .getProperty("FPPROLLUP_CTRLM_INPUTFILE_PATH"))
                        .append(File.separator).append(rollupParameter.getInputFileName()).toString());


            } else {
                objectList = rollupAction.getMarkedFinishedProductParts();
            }
            //Modified by DSM (Sogeti) for defect# 43716 - Ends
            StringList rollupEventList;
            String objectId;
            String sAssemblyType;
            Rollup rollupConfig;

            Config rollupRuleConfiguration = resource.getRollupRuleConfiguration();
            DomainObject dObj = DomainObject.newInstance(context);
            boolean performFlagCalculation;
            // Modified for Rollup Circular Defect# 42216 - Ends

            // for each marked finished product part
            for (Object o : objectList) {

                objectId = (String) ((Map) o).get(DomainConstants.SELECT_ID);
                if (FrameworkUtil.isObjectId(context, objectId)) {
                    dObj.setId(objectId);
                    // get ebom expansion.

                    logger.log(Level.INFO, "Processing start for Marked Object ID: {0}", objectId);
                    resource.getDynamicSubscription().resetSubscriptions();

                    ProductPart productPart = new ChildExpansion.Builder(context, objectId, RollupConstants.Basic.EBOM_CHILDREN.getValue())
                            .setExpansionType(rollupRuleConfiguration.getType())
                            .setExpansionRelationship(rollupRuleConfiguration.getRelationship())
                            .setExpandLevel((short) 0)
                            .build().getProductPart();

                    // get the events marked.
                    rollupEventList = StringUtil.split(productPart.getEventForRollup(), ",");
                    sAssemblyType = productPart.getAssemblyType();

                    //Modified for Defect # 44589 - Starts
                    performFlagCalculation = false;
                    if (!rollupEventList.isEmpty() && productPart.isCalculateForRollupFlag()) {

                        //Modified for Defect # 44589 - Ends
                        for (String rollupEventIdentifier : rollupEventList) {

                            rollupConfig = RollupUtil.getRollup(rollupEventIdentifier, resource.getRollupRuleConfiguration());

                            // Modified for Rollup Circular Defect# 42216 - Starts
                            if (!performFlagCalculation && (
                                    RollupConstants.Basic.SMART_LABEL_READY_ROLLUP_EVENT_IDENTIFIER.getValue().equalsIgnoreCase(rollupConfig.getIdentifier()) ||
                                            RollupConstants.Basic.DGC_ROLLUP_EVENT_IDENTIFIER.getValue().equalsIgnoreCase(rollupConfig.getIdentifier()))) {
                                performFlagCalculation = true;
                            }

                            if ((null != rollupConfig && Boolean.parseBoolean(rollupConfig.getEnableRollup()))) {
                                // Modified for Rollup Circular Defect# 42216 - Ends

                                Rule rollupRuleShippableHALB = RollupUtil.getRollupRule(rollupConfig,
                                        RollupConstants.Basic.IDENTIFIER_SHIPPABLE_HALB.getValue());

                                if (!pgV3Constants.SHIPPABLE_HALB.equalsIgnoreCase(sAssemblyType)
                                        || (pgV3Constants.SHIPPABLE_HALB.equalsIgnoreCase(sAssemblyType)
                                        && Boolean.parseBoolean(rollupRuleShippableHALB.getFlag()))) {

                                    RollupEvent rollupEvent = RollupEventFactory.getRollupEvent(productPart, rollupConfig, rollupEventIdentifier, resource);

                                    if (null != rollupEvent) {
                                        resource.getSlProcessedObjectsList().clear();
                                        productPart.getChildrenList().clear();
                                        rollupEvent.execute();
                                    }

                                }
                            }
                        }
                    }
                    resource.getDynamicSubscription().notifyDSEvents(resource, objectId);

                    // Modified for Rollup Circular Defect# 42216 - Starts
                    // process ready flag.
                    if (performFlagCalculation) {
                        ReadyFlag readyFlag = new ReadyFlag(productPart, resource);
                        readyFlag.processReadyFlag();
                    }
                    // Modified for Rollup Circular Defect# 42216 - Ends
                    dObj.setAttributeValues(context, rollupAction.getFPPAttributeMap(context, dObj, productPart));
                    logger.log(Level.INFO, "Processing end for Marked Object ID: {0}", objectId);
                } else {
                    logger.log(Level.INFO, "Object does not exits for : {0}", objectId);
                }
            }
        } catch (Exception e) {
            bIsExecuted = false;
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return bIsExecuted;
    }

}

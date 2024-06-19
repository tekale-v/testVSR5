package com.pg.dsm.rollup_event.rollup.services;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.pg.dsm.rollup_event.common.config.phys_chem.PhysChemConfig;
import com.pg.dsm.rollup_event.common.config.rule.Config;
import com.pg.dsm.rollup_event.common.config.rule.Rollup;
import com.pg.dsm.rollup_event.common.ebom.ChildExpansion;
import com.pg.dsm.rollup_event.common.ebom.ProductPart;
import com.pg.dsm.rollup_event.common.interfaces.RollupEvent;
import com.pg.dsm.rollup_event.common.interfaces.RollupEventFactory;
import com.pg.dsm.rollup_event.common.resources.RollupPageResource;
import com.pg.dsm.rollup_event.common.resources.RollupPhysChemConfigurator;
import com.pg.dsm.rollup_event.common.resources.RollupRuleConfigurator;
import com.pg.dsm.rollup_event.enumeration.RollupConstants;
import com.pg.dsm.rollup_event.mark.util.MarkUtil;
import com.pg.dsm.rollup_event.rollup.models.DynamicSubscription;
import com.pg.dsm.rollup_event.rollup.util.RollupUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.StringList;

public class RollupExecutorManual {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    RollupParameter rollupParameter;

    public RollupExecutorManual(RollupParameter rollupParameter) {
        this.rollupParameter = rollupParameter;
    }


    public void execute() {

        Context context = rollupParameter.getContext();
        String objectOID = rollupParameter.getObjectOID();
        String rollupEventIdentifier = rollupParameter.getRollupEventIdentifier();

        Properties rollupPageConfigProperties = new RollupPageResource(context, pgV3Constants.ROLLUPPAGEFILE).getConfigProperties();

        Config rollupRuleConfiguration = new RollupRuleConfigurator.Builder(context).build().getConfig();

        if (RollupConstants.Basic.PHYS_CHEM_ROLLUP_EVENT_IDENTIFIER.getValue().equalsIgnoreCase(rollupEventIdentifier)) {
            processPhysChemRollup(context, rollupPageConfigProperties, rollupEventIdentifier, rollupRuleConfiguration, objectOID);
        } else {
            processOtherRollup(context, rollupPageConfigProperties, rollupEventIdentifier, rollupRuleConfiguration, objectOID);
        }
    }

    /**
     * @param context
     * @param rollupPageConfigProperties
     * @param rollupEventIdentifier
     * @param rollupRuleConfiguration
     * @param objectOID
     */
    public void processOtherRollup(Context context, Properties rollupPageConfigProperties, String rollupEventIdentifier, Config rollupRuleConfiguration, String objectOID) {
        boolean isContextPushed = false;
        try {
            //Modified for Defect# 44220 - Starts
            Resource resource = new Resource.Builder()
                    .setContext(context)
                    .setRollupPageProperties(rollupPageConfigProperties)
                    .setRollupRuleConfiguration(rollupRuleConfiguration)
                    .setExecutionType(RollupConstants.Basic.EXECUTION_TYPE_MANUAL.getValue())
                    .setDynamicSubscription(new DynamicSubscription())
                    .setSlProcessedObjectsList(new StringList())
                    .setPerformCircularCheck()
                    .build();
            //Modified for Defect# 44220 - Ends

            Rollup rollupConfig = RollupUtil.getRollup(rollupEventIdentifier, resource.getRollupRuleConfiguration());

            if (null != rollupConfig) {
                //Modified by DSM (Sogeti) for defect# 43716 - Starts
                boolean circularExist = MarkUtil.isCircularExists(context, objectOID, resource);
                //Modified by DSM (Sogeti) for defect# 43716 - Ends
                if ((circularExist && Boolean.parseBoolean(rollupConfig.getAllowCircularDataForRollup())) || !circularExist) {
                    // Added by DSM (Sogeti) for defect 51591 - Starts
                    if (RollupConstants.Basic.EXECUTION_TYPE_MANUAL.getValue().equalsIgnoreCase(resource.getExecutionType())) {
                        ContextUtil.pushContext(context, pgV3Constants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
                        isContextPushed = true;
                    } // Added by DSM (Sogeti) for defect 51591 - Ends
                    // get ebom expansion.
                    ChildExpansion childExpansion = new ChildExpansion.Builder(context, objectOID, RollupConstants.Basic.EBOM_CHILDREN.getValue())
                            .setExpansionType(rollupRuleConfiguration.getType())
                            .setExpansionRelationship(rollupRuleConfiguration.getRelationship())
                            .setExpandLevel((short) 0)
                            .build();
                    if (childExpansion.isLoaded()) {
                        ProductPart productPart = childExpansion.getProductPart();
                        RollupEvent rollupEvent = RollupEventFactory.getRollupEvent(productPart, rollupConfig, rollupEventIdentifier, resource);
                        if (null != rollupEvent) {
                            rollupEvent.execute();
                        }
                    } else {
                        logger.log(Level.WARNING, RollupConstants.Basic.ERROR_LOADING_CHILD_EXPANSION_BEAN.getValue());
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        } finally {
            if (isContextPushed) { // Added by DSM (Sogeti) for defect 51591 - Starts
                try {
                    ContextUtil.popContext(context);
                } catch (FrameworkException e) {
                    logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
                } // Added by DSM (Sogeti) for defect 51591 - Ends
            }
        }
    }


    /**
     * @param context
     * @param rollupPageConfigProperties
     * @param rollupEventIdentifier
     * @param rollupRuleConfiguration
     * @param objectOID
     */
    public void processPhysChemRollup(Context context, Properties rollupPageConfigProperties, String rollupEventIdentifier, Config rollupRuleConfiguration, String objectOID) {
        PhysChemConfig physChemConfig = new RollupPhysChemConfigurator.Builder(context).build().getPhysChemObject();
        Resource resource = new Resource.Builder()
                .setContext(context)
                .setRollupPageProperties(rollupPageConfigProperties)
                .setRollupRuleConfiguration(rollupRuleConfiguration)
                .setExecutionType(rollupParameter.getExecutionType())
                .setPhysChemConfig(physChemConfig)
                .setRollupParameter(rollupParameter)
                .setDynamicSubscription(new DynamicSubscription())
                .build();
        Rollup rollupConfig = RollupUtil.getRollup(rollupEventIdentifier, resource.getRollupRuleConfiguration());
        if (null != rollupConfig) {
            ChildExpansion childExpansion = new ChildExpansion.Builder(context, objectOID, RollupConstants.Basic.EBOM_CHILDREN.getValue())
                    .setExpansionType(physChemConfig.getExpandType())
                    .setExpansionRelationship(physChemConfig.getExpandRelationship())
                    .setExpandLevel(physChemConfig.getExpandLevel())
                    .build();
            if (childExpansion.isLoaded()) {
                ProductPart productPart = childExpansion.getProductPart();
                RollupEvent rollupEvent = RollupEventFactory.getRollupEvent(productPart, rollupConfig, rollupEventIdentifier, resource);
                if (null != rollupEvent) {
                    rollupEvent.execute();
                }
            } else {
                logger.log(Level.WARNING, RollupConstants.Basic.ERROR_LOADING_CHILD_EXPANSION_BEAN.getValue());
            }
        }
    }
}

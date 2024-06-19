package com.pg.dsm.rollup_event.pre_release.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MqlUtil;
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
import com.pg.dsm.rollup_event.mark.models.ReadyFlag;
import com.pg.dsm.rollup_event.rollup.services.Resource;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.StringList;

public class PreReleaseRollupExecutor {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public PreReleaseRollupExecutor() {
    }

    public void execute() {
        Resource resources = getResources();
        if (null != resources && resources.isLoaded()) {
            Context context = resources.getContext();
            RollupPropertyResource rollupPropertyResource = resources.getRollupPropertyResource();
            String attributeRollupConfigBusinessObjectName = rollupPropertyResource.getAttributeRollupConfigBusinessObjectName();
            String configObjectOid = ConfigUtil.getObjectId(context, rollupPropertyResource.getAttributeRollupConfigBusinessObjectName());
            if (UIUtil.isNotNullAndNotEmpty(configObjectOid)) {
                ConfigBean configBean = new ConfigBean(ConfigUtil.getObjectInfo(context, configObjectOid));

                if (configBean.isActive()) {
                    String emailMessageBody = ConfigUtil.getEmailMessageBody(rollupPropertyResource.getAttributeRollupMessage());
                    String emailSubject = rollupPropertyResource.getAttributeRollupSubject();
                    boolean bIsUpdatedRetryCountAndNotify = ConfigUtil.updateRetryCountAndNotify(context, configBean,
                            emailMessageBody,
                            emailSubject);
                    if (!bIsUpdatedRetryCountAndNotify) {
                        logger.log(Level.WARNING, "Failed to update retry count on Config object / send notification email");
                    }
                } else {
                    ConfigUtil.resetConfigObject(context, configBean, false);
                    boolean isMarkingProcessExecuted = executePreReleaseRollupProcess(resources);
                    if (isMarkingProcessExecuted) {
                        ConfigUtil.resetConfigObject(context, configBean, true);
                    } else {
                        logger.log(Level.WARNING, "Attribute Rollup execution Failed");
                    }
                }

            } else {
                logger.log(Level.WARNING, "Config Business Object ID is null");
            }
        } else {
            logger.log(Level.WARNING, "Unable to load resources.");
        }
    }

    private boolean executePreReleaseRollupProcess(Resource resources) {
        boolean isExecuted = true;
        File mqlOutputFile = null;
        try {
            Context context = resources.getContext();
            RollupPropertyResource rollupPropertyResource = resources.getRollupPropertyResource();
            Properties rollupPageProperties = resources.getRollupPageProperties();
            String allowedTypesForPreReleaseRollup = rollupPageProperties.getProperty("ALLOWED.ROLLUP.TYPES.FOR.PRE_RELEASE");
            String objectWhereClauseForPreReleaseRollup = rollupPageProperties.getProperty("ROLLUP.OBJECT.WHERE.CLAUSE.FOR.PRE_RELEASE");
            String outputFile = rollupPropertyResource.getPreReleaseRollupInputFilePath();


            // observed that MQL temp query is much faster than find objects API.
            String mqlQuery = "temp query bus $1 \"$2\" $3  where $4 select $5 $6 $7 $8 dump $9 output $10";
            MqlUtil.mqlCommand(context, mqlQuery, allowedTypesForPreReleaseRollup, DomainConstants.QUERY_WILDCARD,
                    DomainConstants.QUERY_WILDCARD, objectWhereClauseForPreReleaseRollup, DomainConstants.SELECT_ID,
                    pgV3Constants.SELECT_ATTRIBUTE_PGSMARTLABELREADY,
                    pgV3Constants.SELECT_ATTRIBUTE_PGDANGEROUSGOODSREADY, pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE, pgV3Constants.DUMP_CHARACTER, outputFile);

            mqlOutputFile = new File(outputFile);

            if (mqlOutputFile.exists()) {
                boolean bIsWritable = mqlOutputFile.setWritable(true, false);
                boolean bIsExecutable = mqlOutputFile.setExecutable(true, false);
                boolean bIsReadable = mqlOutputFile.setReadable(true, false);

                if (bIsWritable && bIsExecutable && bIsReadable) {

                    try (FileReader mqlOutputFileReader = new FileReader(outputFile);
                         BufferedReader mqlOutputFileBufferedReader = new BufferedReader(
                                 mqlOutputFileReader)) {
                        String outputLine;

                        Map<Object, Object> objectMap;
                        ProductPart productPart;
                        ReadyFlag readyFlag;
                        StringList slOutputLine;

                        while ((outputLine = mqlOutputFileBufferedReader.readLine()) != null) {
                            objectMap = new HashMap<>();
                            slOutputLine = com.matrixone.apps.domain.util.StringUtil.splitString(outputLine, pgV3Constants.DUMP_CHARACTER);

                            objectMap.put(DomainConstants.SELECT_TYPE, slOutputLine.get(0));
                            objectMap.put(DomainConstants.SELECT_NAME, slOutputLine.get(1));
                            objectMap.put(DomainConstants.SELECT_REVISION, slOutputLine.get(2));
                            objectMap.put(DomainConstants.SELECT_ID, slOutputLine.get(3));
                            objectMap.put(pgV3Constants.SELECT_ATTRIBUTE_PGSMARTLABELREADY, slOutputLine.get(4));
                            objectMap.put(pgV3Constants.SELECT_ATTRIBUTE_PGDANGEROUSGOODSREADY, slOutputLine.get(5));
                            objectMap.put(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE, slOutputLine.get(6));

                            productPart = new ProductPart(context, objectMap, DomainConstants.EMPTY_STRING);

                            readyFlag = new ReadyFlag(productPart, resources);
                            if (readyFlag.isReleasePhaseProduction(productPart.getReleasePhase())) {
                                if (readyFlag.isFinishedProductPartType(productPart.getType())) {
                                    logger.info("Flag Recalculation started for FPP " + productPart.getType() + pgV3Constants.SYMBOL_SPACE
                                            + productPart.getName() + pgV3Constants.SYMBOL_SPACE + productPart.getRevision());
                                    readyFlag.processFinishedProduct();
                                }
                            }
                        }
                    } catch (Exception e) {
                        isExecuted = false;
                        logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
                    }
                }
            } else {
                isExecuted = false;
            }
        } catch (FrameworkException e) {
            isExecuted = false;
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        } finally {
            if (null != mqlOutputFile) {
                boolean bIsFileDelete = mqlOutputFile.delete();
                logger.log(Level.INFO, "Is File Deleted: {0}", bIsFileDelete);
            }
        }
        return isExecuted;
    }

    private Resource getResources() {
        Resource resource = null;
        RollupPropertyResource rollupPropertyResource = new RollupPropertyResource.Builder(RollupConstants.Basic.ROLLUP_PROPERTIES_PATH.getValue()).build();
        if (rollupPropertyResource.isLoaded()) {
            Context context = new RollupMatrixResource(rollupPropertyResource).getContext();
            if (context.isConnected()) {
                Properties rollupPageConfigProperties = new RollupPageResource(context, pgV3Constants.ROLLUPPAGEFILE).getConfigProperties();

                Config rollupRuleConfiguration = new RollupRuleConfigurator.Builder(context).build().getConfig();
                resource = new Resource.Builder()
                        .setContext(context)
                        .setCustomProperties(rollupPropertyResource.getProperties())
                        .setRollupPropertyResource(rollupPropertyResource)
                        .setRollupPageProperties(rollupPageConfigProperties)
                        .setRollupRuleConfiguration(rollupRuleConfiguration)
                        .build();
            }
        }
        return resource;
    }
}

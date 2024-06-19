package com.pg.dsm.rollup.packaging_certification.calculate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.MapList;
import com.pg.dsm.rollup.packaging_certification.services.CertificationCalculateJobProcess;
import com.pg.dsm.rollup_event.common.config.rule.Config;
import com.pg.dsm.rollup_event.common.resources.RollupPageResource;
import com.pg.dsm.rollup_event.common.resources.RollupPropertyResource;
import com.pg.dsm.rollup_event.common.resources.RollupRuleConfigurator;
import com.pg.dsm.rollup_event.rollup.models.DynamicSubscription;
import com.pg.dsm.rollup_event.rollup.services.Resource;
import com.pg.dsm.rollup_event.rollup.services.RollupParameter;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.StringList;

public class CertificationCalculate implements pgV3Constants {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    Context context;
    RollupPropertyResource rollupPropertyResource;
    RollupParameter rollupParameter;

    public CertificationCalculate(Context context, RollupPropertyResource rollupPropertyResource, RollupParameter rollupParameter) {
        this.context = context;
        this.rollupPropertyResource = rollupPropertyResource;
        this.rollupParameter = rollupParameter;
    }

    public void executeCalculationProcess() throws Exception {
        CertificationCalculateUtil calculateUtil = new CertificationCalculateUtil();
        final String mqlQuery = rollupPropertyResource.getPackagingCertificationRollupCalculateJobSearchMQLQuery();

        logger.log(Level.INFO, "Calculate MQL Query: {0}", mqlQuery);
        MapList objectList = calculateUtil.getMarkedFinishedProductParts(context, mqlQuery);

        // allowed types for circular check.
        final String circularCheckAllowedTypes = rollupPropertyResource.getPackagingCertificationRollupCalculateJobCircularCheckAllowedTypes();

        // is circular check setting on.
        final boolean isCircularCheckRequired = Boolean.parseBoolean(rollupPropertyResource.getPackagingCertificationRollupCalculateJobPerformCircularCheck());

        // this map holds (re-set flag attribute) for each incoming marked object.
        Map<Object, Object> mapWhenNonCircular = new HashMap<>();
        mapWhenNonCircular.put(ATTRIBUTE_CALCULATE_CERTIFICATION_ROLLUP, KEY_FALSE);
        mapWhenNonCircular.put(ATTRIBUTE_WIP_CERTIFICATION_ROLLUP, KEY_TRUE);

        // this map holds (re-set flag attribute to false) for each incoming marked object if there is circular.
        Map<Object, Object> mapWhenCircular = new HashMap<>();
        mapWhenCircular.put(ATTRIBUTE_CALCULATE_CERTIFICATION_ROLLUP, KEY_FALSE);
        mapWhenCircular.put(ATTRIBUTE_WIP_CERTIFICATION_ROLLUP, KEY_FALSE);

        StringList busSelects = new StringList();
        busSelects.addElement(SELECT_ATTRIBUTE_CALCULATE_CERTIFICATION_ROLLUP);
        busSelects.addElement(SELECT_ATTRIBUTE_WIP_CERTIFICATION_ROLLUP);

        if (null != objectList && !objectList.isEmpty()) {

            logger.log(Level.INFO, "NUMBER OF OBJECTS TO PROCESS: {0}", objectList.size());

            Resource resource = getResource(context, rollupPropertyResource);
            if (null != resource && resource.isLoaded()) {
                CertificationCalculateJobProcess calculateJob;
                RollupParameter jobParameter;
                DomainObject domainObject;
                String objectId;
                String type;
                Map<Object, Object> objectMap;
                String calcCert;
                String calcCertWIP;
                boolean isCircular;
                boolean isRecalculate;

                // iterate each object which has (calc or calc-wip) flag on.
                final Iterator iterator = objectList.iterator();
                while (iterator.hasNext()) {
                    objectMap = (Map<Object, Object>) iterator.next();
                    objectId = (String) objectMap.get(DomainConstants.SELECT_ID);
                    type = (String) objectMap.get(DomainConstants.SELECT_TYPE);
                    domainObject = DomainObject.newInstance(context, objectId);
                    isRecalculate = false;
                    logger.log(Level.INFO, "Processing Start for {0} Object ID: {1}", new Object[]{type, objectId});

                    // if circular setting is on and the incoming object type is allowed
                    if (isCircularCheckRequired && circularCheckAllowedTypes.contains(type)) {
                        isCircular = calculateUtil.isCircularStructure(context, objectId, resource);
                        if (isCircular) {
                            // reset calculate & wip flag to false
                            domainObject.setAttributeValues(context, mapWhenCircular);
                            logger.log(Level.INFO, "Object Id {0} circularExist  >>>>>>>>>>>>: {1}", new Object[]{objectId, isCircular});
                            continue;
                        }
                    }
                    jobParameter = new RollupParameter.Builder()
                            .setObjectOID(objectId)
                            .setExecutionType(rollupParameter.getExecutionType())
                            .build();

                    objectMap.putAll(domainObject.getInfo(context, busSelects));
                    calcCert = (String) objectMap.get(SELECT_ATTRIBUTE_CALCULATE_CERTIFICATION_ROLLUP);
                    calcCertWIP = (String) objectMap.get(SELECT_ATTRIBUTE_WIP_CERTIFICATION_ROLLUP);
                    if (TRUE.equalsIgnoreCase(calcCert) || TRUE.equalsIgnoreCase(calcCertWIP)) {
                        // set (wip flag) = true && set (calculate flag) = false
                        logger.log(Level.INFO, "Calc flag was: {0} | Calc-WIP was: {1}", new Object[]{calcCert, calcCertWIP});
                        domainObject.setAttributeValues(context, mapWhenNonCircular);
                        isRecalculate = true;
                    }
                    if (isRecalculate) {
                        // send for calculation.
                        calculateJob = new CertificationCalculateJobProcess.Builder(context, jobParameter).build();
                        if (null != calculateJob) {
                            if (calculateJob.isCompleted()) {
                                logger.log(Level.INFO, "Calculation was successful for object {0}", objectId);

                                domainObject.setAttributeValue(context, ATTRIBUTE_WIP_CERTIFICATION_ROLLUP, FALSE);

                                logger.log(Level.INFO, "Turn off Calculate WIP flag for object {0}", objectId);

                            } else {
                                logger.log(Level.WARNING, "Calculation failed for object {0}", objectId);
                            }
                        } else {
                            logger.log(Level.WARNING, "Calculation exception occurred for object {0}", objectId);
                        }
                    }
                    logger.log(Level.INFO, "Processing End for {0} Object ID: {1}", new Object[]{type, objectId});
                }
            } else {
                logger.log(Level.WARNING, "Failed to load resource");
            }
        }
    }

    /**
     * @param context
     * @param rollupPropertyResource
     * @return
     */
    private Resource getResource(Context context, RollupPropertyResource rollupPropertyResource) {
        Resource resource = null;
        if (rollupPropertyResource.isLoaded()) {
            if (context.isConnected()) {
                Properties rollupPageConfigProperties = new RollupPageResource(context, pgV3Constants.ROLLUPPAGEFILE).getConfigProperties();
                Config rollupRuleConfiguration = new RollupRuleConfigurator.Builder(context).build().getConfig();
                resource = new Resource.Builder()
                        .setContext(context)
                        .setCustomProperties(rollupPropertyResource.getProperties())
                        .setRollupPropertyResource(rollupPropertyResource)
                        .setRollupPageProperties(rollupPageConfigProperties)
                        .setRollupRuleConfiguration(rollupRuleConfiguration)
                        .setExecutionType(rollupParameter.getExecutionType())
                        .setDynamicSubscription(new DynamicSubscription())
                        .setPerformCircularCheck()
                        .build();
            }
        }
        return resource;
    }
}

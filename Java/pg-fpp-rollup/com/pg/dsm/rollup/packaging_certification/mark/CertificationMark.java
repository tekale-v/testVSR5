package com.pg.dsm.rollup.packaging_certification.mark;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.pg.dsm.rollup.packaging_certification.services.CertificationMarkJobService;
import com.pg.dsm.rollup_event.common.config.rule.Config;
import com.pg.dsm.rollup_event.common.resources.RollupPageResource;
import com.pg.dsm.rollup_event.common.resources.RollupPropertyResource;
import com.pg.dsm.rollup_event.common.resources.RollupRuleConfigurator;
import com.pg.dsm.rollup_event.enumeration.RollupConstants;
import com.pg.dsm.rollup_event.rollup.models.DynamicSubscription;
import com.pg.dsm.rollup_event.rollup.services.Resource;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;

public class CertificationMark implements pgV3Constants {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    Context context;
    RollupPropertyResource rollupPropertyResource;

    public CertificationMark(Context context, RollupPropertyResource rollupPropertyResource) {
        this.context = context;
        this.rollupPropertyResource = rollupPropertyResource;
    }

    public void executeMarkingProcess() throws FrameworkException {
        CertificationMarkUtil markUtil = new CertificationMarkUtil(context);

        final String mqlQuery = rollupPropertyResource.getPackagingCertificationRollupMarkingJobSearchMQLQuery();

        logger.log(Level.INFO, "Mark MQL Query: {0}", mqlQuery);

        MapList objectList = markUtil.getMarkedObjects(mqlQuery);

        // allowed types for circular check.
        final String circularCheckAllowedTypes = rollupPropertyResource.getPackagingCertificationRollupMarkingJobCircularCheckAllowedTypes();

        // is circular check setting on.
        final boolean isCircularCheckRequired = Boolean.parseBoolean(rollupPropertyResource.getPackagingCertificationRollupMarkingJobPerformCircularCheck());

        if (null != objectList && !objectList.isEmpty()) {

            logger.log(Level.INFO, "NUMBER OF OBJECTS TO PROCESS: {0}", objectList.size());

            final Resource resource = getResource(context, rollupPropertyResource);

            if (null != resource && resource.isLoaded()) {

                Map<Object, Object> objectMap;
                String objectId;
                String type;
                boolean isCircular;
                DomainObject domainObject;
                CertificationMarkJobService markJob = new CertificationMarkJobService(context);

                // iterate each object which has (mark for cert) flag on.
                final Iterator iterator = objectList.iterator();
                while (iterator.hasNext()) {
                    objectMap = (Map<Object, Object>) iterator.next();
                    objectId = (String) objectMap.get(SELECT_ID);
                    type = (String) objectMap.get(SELECT_TYPE);
                    domainObject = DomainObject.newInstance(context, objectId);
                    logger.log(Level.INFO, "Processing Start for {0} Object ID: {1}", new Object[]{type, objectId});

                    // if circular setting is on and the incoming object type is allowed
                    if (isCircularCheckRequired && circularCheckAllowedTypes.contains(type)) {
                        isCircular = markUtil.isCircularStructure(context, objectId, resource);
                        if (isCircular) {
                            // reset wip flag to false and continue processing next object.
                            domainObject.setAttributeValue(context, pgV3Constants.ATTRIBUTE_MARK_FOR_CERTIFICATION_ROLLUP, pgV3Constants.KEY_FALSE);
                            logger.log(Level.INFO, "Object Id {0} circularExist  >>>>>>>>>>>>: {1}", new Object[]{objectId, isCircular});
                            continue;
                        }
                    }
                    markJob.executeMarkingJob(objectMap);
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
                        .setExecutionType(RollupConstants.Basic.EXECUTION_TYPE_CTRLM.getValue())
                        .setDynamicSubscription(new DynamicSubscription())
                        .setPerformCircularCheck()
                        .build();
            }
        }
        return resource;
    }
}

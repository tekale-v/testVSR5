package com.pg.dsm.rollup.packaging_certification.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.ctrlm.custom.CtrlmJobContext;
import com.pg.dsm.rollup.packaging_certification.calculate.CertificationCalculate;
import com.pg.dsm.rollup_event.common.config.ConfigBean;
import com.pg.dsm.rollup_event.common.resources.RollupPropertyResource;
import com.pg.dsm.rollup_event.common.util.ConfigUtil;
import com.pg.dsm.rollup_event.enumeration.RollupConstants;
import com.pg.dsm.rollup_event.rollup.services.RollupParameter;

import matrix.db.Context;

public class CertificationCalculateJobExecutor {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    RollupParameter rollupParameter;

    public CertificationCalculateJobExecutor(RollupParameter rollupParameter) {
        this.rollupParameter = rollupParameter;
    }

    public void execute() {
        try {
            final Context context = CtrlmJobContext.getCtrlmContext();
            RollupPropertyResource rollupPropertyResource = new RollupPropertyResource.Builder(RollupConstants.Basic.ROLLUP_PROPERTIES_PATH.getValue()).build();
            if (rollupPropertyResource.isLoaded()) {
                executeRollupProcess(context, rollupPropertyResource);
            } else {
                logger.log(Level.WARNING, "Failed to load RollUp.properties file");
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Exception - ", e);
        }
    }

    /**
     * @param context
     * @param rollupPropertyResource
     */
    public void executeRollupProcess(Context context, RollupPropertyResource rollupPropertyResource) throws Exception {
        String configObjectOid = ConfigUtil.getObjectId(context, rollupPropertyResource.getPackagingCertificationRollupCalculateConfigObjectName());
        if (UIUtil.isNotNullAndNotEmpty(configObjectOid)) {
            ConfigBean configBean = new ConfigBean(ConfigUtil.getObjectInfo(context, configObjectOid));
            if (configBean.isActive()) {
                String emailMessageBody = ConfigUtil.getEmailMessageBody(rollupPropertyResource.getPackagingCertificationRollupCalculateJobStuckEmailMessage());
                String emailSubject = rollupPropertyResource.getPackagingCertificationRollupCalculateJobStuckEmailSubject();
                boolean bIsUpdatedRetryCountAndNotify = ConfigUtil.updateRetryCountAndNotify(context, configBean,
                        emailMessageBody,
                        emailSubject);
                if (!bIsUpdatedRetryCountAndNotify) {
                    logger.log(Level.WARNING, "Failed to update retry count on Config object / send notification email");
                }
            } else {
                // set active flag true and retry count to 1
                ConfigUtil.resetConfigObject(context, configBean, false);
                CertificationCalculate certificationCalculate = new CertificationCalculate(context, rollupPropertyResource, rollupParameter);
                certificationCalculate.executeCalculationProcess();

                // set active flag false and retry count to 0
                ConfigUtil.resetConfigObject(context, configBean, true);
            }
        }
    }
}

package com.pg.dsm.rollup.packaging_certification.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.pg.dsm.rollup.packaging_certification.services.CertificationCalculateJobProcess;
import com.pg.dsm.rollup_event.rollup.services.RollupParameter;

import matrix.db.Context;

public class CertificationCalculateManualExecutor {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    RollupParameter rollupParameter;

    public CertificationCalculateManualExecutor(RollupParameter rollupParameter) {
        this.rollupParameter = rollupParameter;
    }

    public void execute() throws Exception {
        Context context = rollupParameter.getContext();
        String objectOID = rollupParameter.getObjectOID();

        CertificationCalculateJobProcess jobProcess = new CertificationCalculateJobProcess.Builder(context, rollupParameter).build();
        if (null != jobProcess) {
            if (jobProcess.isCompleted()) {
                logger.log(Level.INFO, "Packaging Certification Manual Rollup was success for objectId: {0} ", objectOID);
            } else {
                logger.log(Level.WARNING, "Packaging Certification Manual Rollup failed for objectId: {0} ", objectOID);
            }
        }
    }
}

package com.pg.dsm.rollup.packaging_certification.controller;

import com.pg.dsm.rollup_event.enumeration.RollupConstants;
import com.pg.dsm.rollup_event.rollup.services.RollupParameter;

public class CertificationCalculateEnabler {
    public CertificationCalculateEnabler() {
    }

    public void enable(RollupParameter rollupParameter) throws Exception {
        if (RollupConstants.Basic.EXECUTION_TYPE_CTRLM.getValue().equals(rollupParameter.getExecutionType())) {
            // ctrlm
            CertificationCalculateJobExecutor executor = new CertificationCalculateJobExecutor(rollupParameter);
            executor.execute();
        } else {
            // manual
            CertificationCalculateManualExecutor executor = new CertificationCalculateManualExecutor(rollupParameter);
            executor.execute();
        }
    }
}

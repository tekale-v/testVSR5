package com.pg.dsm.rollup_event.rollup.services;

import com.pg.dsm.rollup_event.enumeration.RollupConstants;

public class RollupEnabler {

    public RollupEnabler() {
    }

    public void enable(RollupParameter rollupParameter) {
        if (RollupConstants.Basic.EXECUTION_TYPE_CTRLM.getValue().equals(rollupParameter.getExecutionType())) {
            // ctrlm
            RollupExecutor rollupExecutor = new RollupExecutor(rollupParameter);
            rollupExecutor.execute();
        } else {
            // manual
            RollupExecutorManual rollupExecutorManual = new RollupExecutorManual(rollupParameter);
            rollupExecutorManual.execute();
        }
    }
}

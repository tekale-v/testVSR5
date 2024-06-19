package com.pg.dsm.rollup_event;

import java.time.Duration;
import java.time.Instant;
import java.util.logging.Logger;

import com.pg.dsm.rollup_event.enumeration.RollupConstants;
import com.pg.dsm.rollup_event.rollup.services.RollupEnabler;
import com.pg.dsm.rollup_event.rollup.services.RollupParameter;

import matrix.db.Context;

public class RollupManualClient {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public RollupManualClient() {
    }

    public void performRollup(Context context, String[] args) {
        Instant startTime = Instant.now();

        RollupParameter rollupParameter = new RollupParameter.Builder()
                .setContext(context)
                .setObjectOID(args[0])
                .setObjectName(args[1])
                .setRollupEventIdentifier(args[2])
                .setFeatureIdentifier(args[3])
                .setSpecificationSubType(args[4])
                .setExecutionType(RollupConstants.Basic.EXECUTION_TYPE_MANUAL.getValue())
                .build();

        RollupEnabler rollupEnabler = new RollupEnabler();
        rollupEnabler.enable(rollupParameter);

        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("Manual Rollup Execution - took|" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");

    }
}


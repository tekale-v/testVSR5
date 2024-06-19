package com.pg.dsm.rollup_event;

import java.time.Duration;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.pg.dsm.rollup_event.enumeration.RollupConstants;
import com.pg.dsm.rollup_event.rollup.services.RollupEnabler;
import com.pg.dsm.rollup_event.rollup.services.RollupParameter;

public class RollupJobTwoClient {
    private static final Logger logger = Logger.getLogger(RollupJobTwoClient.class.getName());

    public static void main(String[] args) {
        Instant startTime = Instant.now();
        //Modified by DSM (Sogeti) for defect# 43716 - Starts
        String inputFileName = DomainConstants.EMPTY_STRING;
        int argsLength = args.length;
        if (argsLength > 0) {
            inputFileName = args[0];
        }
        logger.log(Level.INFO, "inputFileName : {0}", inputFileName);

        RollupParameter rollupParameter = new RollupParameter.Builder()
                .setObjectOID("")
                .setObjectName("")
                .setRollupEventIdentifier("")
                .setFeatureIdentifier("")
                .setSpecificationSubType("")
                .setExecutionType(RollupConstants.Basic.EXECUTION_TYPE_CTRLM.getValue())
                .setInputFileName(inputFileName)
                .build();
        //Modified by DSM (Sogeti) for defect# 43716 - Ends
        RollupEnabler rollupEnabler = new RollupEnabler();
        rollupEnabler.enable(rollupParameter);

        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("Rollup Job Two Execution - took|" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");

        System.exit(0);
    }
}

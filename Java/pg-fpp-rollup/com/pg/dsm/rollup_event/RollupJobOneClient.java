package com.pg.dsm.rollup_event;

import java.time.Duration;
import java.time.Instant;
import java.util.logging.Logger;

import com.pg.dsm.rollup_event.enumeration.RollupConstants;
import com.pg.dsm.rollup_event.mark.services.MarkRollupExecutor;
import com.pg.dsm.rollup_event.mark.services.MarkRollupParameter;

public class RollupJobOneClient {

    private static final Logger logger = Logger.getLogger(RollupJobOneClient.class.getName());

    public static void main(String[] args) {
        Instant startTime = Instant.now();
        MarkRollupParameter markRollupParameter = new MarkRollupParameter.Builder()
                .setExecutionType(RollupConstants.Basic.EXECUTION_TYPE_CTRLM.getValue())
                .build();


        MarkRollupExecutor markRollupExecutor = new MarkRollupExecutor(markRollupParameter);
        markRollupExecutor.execute();

        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("Rollup Job One Execution - took|" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
        System.exit(0);
    }
}

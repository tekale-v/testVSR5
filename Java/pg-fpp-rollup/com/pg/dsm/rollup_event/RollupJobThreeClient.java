package com.pg.dsm.rollup_event;

import java.time.Duration;
import java.time.Instant;
import java.util.logging.Logger;

import com.pg.dsm.rollup_event.pre_release.services.PreReleaseRollupExecutor;

public class RollupJobThreeClient {
    private static final Logger logger = Logger.getLogger(RollupJobThreeClient.class.getName());

    public static void main(String[] args) {
        Instant startTime = Instant.now();

        PreReleaseRollupExecutor preReleaseRollupExecutor = new PreReleaseRollupExecutor();
        preReleaseRollupExecutor.execute();

        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("Rollup Job Three Execution - took|" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
        System.exit(0);
    }
}

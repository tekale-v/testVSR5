package com.pg.dsm.rollup;

import java.time.Duration;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pg.dsm.rollup.packaging_certification.controller.CertificationMarkJobExecutor;
import com.pg.dsm.rollup_event.enumeration.RollupConstants;
import com.pg.dsm.rollup_event.mark.services.MarkRollupParameter;

public class RollupPackagingCertMarkJobClient {
    private static final Logger logger = Logger.getLogger(RollupPackagingCertMarkJobClient.class.getName());

    public static void main(String[] args) {
        try {
            Instant startTime = Instant.now();

            MarkRollupParameter markRollupParameter = new MarkRollupParameter.Builder()
                    .setExecutionType(RollupConstants.Basic.EXECUTION_TYPE_CTRLM.getValue())
                    .build();

            CertificationMarkJobExecutor executor = new CertificationMarkJobExecutor(markRollupParameter);
            executor.execute();

            Instant endTime = Instant.now();

            Duration duration = Duration.between(startTime, endTime);
            logger.info("Rollup Job Packing Material Certification Marking Execution - took|" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
        } catch (Exception e) {
            logger.log(Level.WARNING, "Exception -", e);
        } finally {
            System.exit(0);
        }
    }
}

package com.pg.dsm.rollup;

import java.time.Duration;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pg.dsm.rollup.packaging_certification.controller.CertificationCalculateEnabler;
import com.pg.dsm.rollup_event.enumeration.RollupConstants;
import com.pg.dsm.rollup_event.rollup.services.RollupParameter;

public class RollupPackagingCertCalculateJobClient {

    private static final Logger logger = Logger.getLogger(RollupPackagingCertCalculateJobClient.class.getName());

    public static void main(String[] args) {
        try {
            Instant startTime = Instant.now();

            RollupParameter rollupParameter = new RollupParameter.Builder()
                    .setObjectOID("")
                    .setObjectName("")
                    .setRollupEventIdentifier("")
                    .setFeatureIdentifier("")
                    .setSpecificationSubType("")
                    .setExecutionType(RollupConstants.Basic.EXECUTION_TYPE_CTRLM.getValue())
                    .setInputFileName("")
                    .build();

            CertificationCalculateEnabler enabler = new CertificationCalculateEnabler();
            enabler.enable(rollupParameter);

            Instant endTime = Instant.now();
            Duration duration = Duration.between(startTime, endTime);
            logger.info("Rollup Job Packing Material Certification Calculation Execution - took|" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");

        } catch (Exception e) {
            logger.log(Level.WARNING, "Exception -", e);
        } finally {
            System.exit(0);
        }
    }
}

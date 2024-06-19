package com.pg.dsm.rollup;

import java.time.Duration;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pg.dsm.rollup.packaging_certification.controller.CertificationCalculateEnabler;
import com.pg.dsm.rollup_event.enumeration.RollupConstants;
import com.pg.dsm.rollup_event.rollup.services.RollupParameter;

import matrix.db.Context;

public class RollupPackagingCertManualClient {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public RollupPackagingCertManualClient() {
    }

    public void performRollup(Context context, String[] args) {
        try {
            Instant startTime = Instant.now();

            RollupParameter rollupParameter = new RollupParameter.Builder()
                    .setContext(context)
                    .setObjectOID(args[0])
                    .setRollupEventIdentifier(args[2])
                    .setExecutionType(RollupConstants.Basic.EXECUTION_TYPE_MANUAL.getValue())
                    .build();

            CertificationCalculateEnabler enabler = new CertificationCalculateEnabler();
            enabler.enable(rollupParameter);

            Instant endTime = Instant.now();
            Duration duration = Duration.between(startTime, endTime);
            logger.info("Packaging Cert Manual Rollup Execution - took|" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
        } catch (Exception e) {
            logger.log(Level.WARNING, "Exception -", e);
        } finally {
            System.exit(0);
        }
    }
}

package com.pg.dsm.rollup.packaging_certification.services;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.pg.dsm.rollup_event.rollup.services.RollupParameter;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;

public class CertificationCalculateJobProcess {
    boolean completed;
    String errorMessage;

    private CertificationCalculateJobProcess(Builder builder) {
        this.completed = builder.completed;
        this.errorMessage = builder.errorMessage;
    }

    public boolean isCompleted() {
        return completed;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public static class Builder implements pgV3Constants {
        private final Logger logger = Logger.getLogger(this.getClass().getName());
        Context context;
        String executionType;
        String objectOid;
        boolean completed;
        String errorMessage;

        public Builder(Context context, RollupParameter rollupParameter) {
            this.context = context;
            this.executionType = rollupParameter.getExecutionType();
            this.objectOid = rollupParameter.getObjectOID();
        }

        public CertificationCalculateJobProcess build() {
            try {
                CertificationCalculateJobService service = new CertificationCalculateJobService(this.context, this.objectOid, this.executionType);
                service.processCalculateJob(this.objectOid);
                this.completed = Boolean.TRUE;
            } catch (Exception e) {
                logger.log(Level.WARNING, "Exception - ", e);
                this.completed = Boolean.FALSE;
                this.errorMessage = e.getMessage();
            }
            return new CertificationCalculateJobProcess(this);
        }
    }
}

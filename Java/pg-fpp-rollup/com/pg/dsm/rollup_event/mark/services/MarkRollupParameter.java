package com.pg.dsm.rollup_event.mark.services;

import matrix.db.Context;

public class MarkRollupParameter {
    Context context;
    String objectOID;
    String rollupEventIdentifier;
    String featureIdentifier;
    String objectName;
    String specificationSubType;
    String executionType;

    private MarkRollupParameter(Builder builder) {
        this.context = builder.context;
        this.objectOID = builder.objectOID;
        this.objectName = builder.objectName;
        this.rollupEventIdentifier = builder.rollupEventIdentifier;
        this.featureIdentifier = builder.featureIdentifier;
        this.executionType = builder.executionType;
    }

    public Context getContext() {
        return context;
    }

    public String getObjectOID() {
        return objectOID;
    }

    public String getRollupEventIdentifier() {
        return rollupEventIdentifier;
    }

    public String getFeatureIdentifier() {
        return featureIdentifier;
    }

    public String getObjectName() {
        return objectName;
    }

    public String getSpecificationSubType() {
        return specificationSubType;
    }

    public String getExecutionType() {
        return executionType;
    }

    public static class Builder {
        Context context;
        String objectOID;
        String rollupEventIdentifier;
        String featureIdentifier;
        String objectName;
        String specificationSubType;
        String executionType;

        public Builder() {
        }

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder setObjectOID(String objectOID) {
            this.objectOID = objectOID;
            return this;
        }

        public Builder setRollupEventIdentifier(String rollupEventIdentifier) {
            this.rollupEventIdentifier = rollupEventIdentifier;
            return this;
        }

        public Builder setFeatureIdentifier(String featureIdentifier) {
            this.featureIdentifier = featureIdentifier;
            return this;
        }

        public Builder setObjectName(String objectName) {
            this.objectName = objectName;
            return this;
        }

        public Builder setSpecificationSubType(String specificationSubType) {
            this.specificationSubType = specificationSubType;
            return this;
        }

        public Builder setExecutionType(String executionType) {
            this.executionType = executionType;
            return this;
        }

        public MarkRollupParameter build() {
            return new MarkRollupParameter(this);
        }
    }
}

package com.pg.dsm.rollup_event.rollup.services;

import com.matrixone.apps.framework.ui.UIUtil;

import matrix.db.Context;

public class RollupParameter {
    Context context;
    String objectOID;
    String copyOID;
    String rollupEventIdentifier;
    String featureIdentifier;
    String objectName;
    String specificationSubType;
    String executionType;
    String inputFileName;
    boolean isInputFileProvided;

    private RollupParameter(Builder builder) {
        this.context = builder.context;
        this.objectOID = builder.objectOID;
        this.objectName = builder.objectName;
        this.copyOID = builder.copyOID;
        this.rollupEventIdentifier = builder.rollupEventIdentifier;
        this.featureIdentifier = builder.featureIdentifier;
        this.executionType = builder.executionType;
        this.specificationSubType = builder.specificationSubType;
        this.inputFileName = builder.inputFileName;
        setInputFileProvided();
    }

    public Context getContext() {
        return context;
    }

    public boolean isInputFileProvided() {
        return isInputFileProvided;
    }

    public void setInputFileProvided(boolean inputFileProvided) {
        isInputFileProvided = inputFileProvided;
    }

    public void setInputFileProvided() {
        if (UIUtil.isNotNullAndNotEmpty(this.inputFileName)) {
            setInputFileProvided(Boolean.TRUE);
        }
    }

    public String getObjectOID() {
        return objectOID;
    }

    public String getCopyOID() {
        return copyOID;
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

    public String getInputFileName() {
        return inputFileName;
    }

    public static class Builder {
        Context context;
        String objectOID;
        String copyOID;
        String rollupEventIdentifier;
        String featureIdentifier;
        String objectName;
        String specificationSubType;
        String executionType;
        String inputFileName;

        public Builder() {
        }

        public Builder setInputFileName(String inputFileName) {
            this.inputFileName = inputFileName;
            return this;
        }

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder setObjectOID(String objectOID) {
            this.objectOID = objectOID;
            return this;
        }

        public Builder setCopyOID(String copyOID) {
            this.copyOID = copyOID;
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

        public RollupParameter build() {
            return new RollupParameter(this);
        }
    }

}

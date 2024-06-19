package com.pg.dsm.rollup_event.rollup.services;

import java.util.Properties;

import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.rollup_event.common.config.phys_chem.PhysChemConfig;
import com.pg.dsm.rollup_event.common.config.rule.Config;
import com.pg.dsm.rollup_event.common.resources.RollupPropertyResource;
import com.pg.dsm.rollup_event.enumeration.RollupConstants;
import com.pg.dsm.rollup_event.rollup.models.DynamicSubscription;

import matrix.db.Context;
import matrix.util.StringList;

public class Resource {
    boolean isLoaded;
    RollupParameter rollupParameter;
    Context context;
    Properties rollupCustomProperties;
    RollupPropertyResource rollupPropertyResource;
    Properties rollupPageProperties;
    Config rollupRuleConfiguration;
    String executionType;
    PhysChemConfig physChemConfig;
    DynamicSubscription dynamicSubscription;
    StringList slProcessedObjectsList;
    //Added for Defect# 44220 - Starts
    boolean performCircularCheck;
    //Added for Defect# 44220 - Ends

    private Resource(Builder builder) {
        this.isLoaded = builder.isLoaded;
        this.context = builder.context;
        this.rollupCustomProperties = builder.rollupCustomProperties;
        this.rollupPropertyResource = builder.rollupPropertyResource;
        this.rollupPageProperties = builder.rollupPageProperties;
        this.rollupRuleConfiguration = builder.rollupRuleConfiguration;
        this.executionType = builder.executionType;
        this.physChemConfig = builder.physChemConfig;
        this.dynamicSubscription = builder.dynamicSubscription;
        this.slProcessedObjectsList = builder.slProcessedObjectsList;
        //Added for Defect# 44220 - Starts
        this.performCircularCheck = builder.performCircularCheck;
        //Added for Defect# 44220 - Ends
    }

    public Context getContext() {
        return context;
    }

    public RollupPropertyResource getRollupPropertyResource() {
        return rollupPropertyResource;
    }

    public Properties getRollupPageProperties() {
        return rollupPageProperties;
    }

    public Config getRollupRuleConfiguration() {
        return rollupRuleConfiguration;
    }

    public Properties getRollupCustomProperties() {
        return rollupCustomProperties;
    }

    public String getExecutionType() {
        return executionType;
    }

    public PhysChemConfig getPhysChemConfig() {
        return physChemConfig;
    }

    public RollupParameter getRollupParameter() {
        return rollupParameter;
    }

    public DynamicSubscription getDynamicSubscription() {
        return dynamicSubscription;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }

    public StringList getSlProcessedObjectsList() {
        return slProcessedObjectsList;
    }

    //Added for Defect# 44220 - Starts
    public boolean getPerformCircularCheck() {
        return performCircularCheck;
    }
    //Added for Defect# 44220 - Ends

    public static class Builder {
        boolean isLoaded;
        RollupParameter rollupParameter;
        Context context;
        Properties rollupCustomProperties;
        RollupPropertyResource rollupPropertyResource;
        Properties rollupPageProperties;
        Config rollupRuleConfiguration;
        String executionType;
        PhysChemConfig physChemConfig;
        DynamicSubscription dynamicSubscription;
        StringList slProcessedObjectsList;
        //Added for Defect# 44220 - Starts
        boolean performCircularCheck;

        //Added for Defect# 44220 - Ends
        public Builder() {
        }

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder setCustomProperties(Properties rollupCustomProperties) {
            this.rollupCustomProperties = rollupCustomProperties;
            return this;
        }

        public Builder setRollupPropertyResource(RollupPropertyResource rollupPropertyResource) {
            this.rollupPropertyResource = rollupPropertyResource;
            return this;
        }

        public Builder setRollupPageProperties(Properties rollupPageProperties) {
            this.rollupPageProperties = rollupPageProperties;
            return this;
        }

        public Builder setRollupRuleConfiguration(Config rollupRuleConfiguration) {
            this.rollupRuleConfiguration = rollupRuleConfiguration;
            return this;
        }

        public Builder setExecutionType(String executionType) {
            this.executionType = executionType;
            return this;
        }

        public Builder setPhysChemConfig(PhysChemConfig physChemConfig) {
            this.physChemConfig = physChemConfig;
            return this;
        }

        public Builder setRollupParameter(RollupParameter rollupParameter) {
            this.rollupParameter = rollupParameter;
            return this;
        }

        public Builder setDynamicSubscription(DynamicSubscription dynamicSubscription) {
            this.dynamicSubscription = dynamicSubscription;
            return this;
        }

        public Resource build() {
            isLoaded = true;
            return new Resource(this);
        }

        public Builder setSlProcessedObjectsList(StringList slProcessedObjectsList) {
            this.slProcessedObjectsList = slProcessedObjectsList;
            return this;
        }

        //Added for Defect# 44220 - Starts
        public Builder setPerformCircularCheck() {
            if (UIUtil.isNotNullAndNotEmpty(executionType)) {
                if (executionType.equalsIgnoreCase(RollupConstants.Basic.EXECUTION_TYPE_CTRLM.getValue())) {
                    this.performCircularCheck = Boolean.parseBoolean(rollupPropertyResource.getCtrlmCircularCheck());
                } else {
                    this.performCircularCheck = Boolean.parseBoolean(rollupRuleConfiguration.getPerformCircularCheck());
                }
            } else {
                this.performCircularCheck = false;
            }

            return this;
        }
    }

}

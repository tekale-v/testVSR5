/*
 **   ProductPartBean.java
 **   Description - Introduced as part of Battery Data Load - 18x.6
 **   About - Interface for battery product bean.
 **
 */

package com.pg.dsm.upload.battery.interfaces;

public interface ProductPartBean {

    String getId();

    void setId(String id);

    String getType();

    void setType(String type);

    String getName();

    void setName(String name);

    String getRevision();

    void setRevision(String revision);


    String getCurrentState();

    void setCurrentState(String currentState);

    String getPolicy();

    void setPolicy(String policy);

    String getVault();

    void setVault(String vault);

    String getPowerSource();

    void setPowerSource(String powerSource);

    String getBatteryType();

    void setBatteryType(String batteryType);

    String getNumberOfBatteriesShippedInsideDevice();

    void setNumberOfBatteriesShippedInsideDevice(String numberOfBatteriesShippedInsideDevice);

    String getNumberOfBatteriesRequired();

    void setNumberOfBatteriesRequired(String numberOfBatteriesRequired);

    String getIsBattery();

    void setIsBattery(String isBattery);

    String getNetWeight();

    void setNetWeight(String netWeight);
}

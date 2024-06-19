/*
 **   DeviceProductPartBean.java
 **   Description - Introduced as part of Battery Data Load - 18x.6
 **   About - Bean class for DPP.
 **
 */
package com.pg.dsm.upload.battery.models;

import com.matrixone.apps.domain.DomainConstants;
import com.pg.dsm.upload.battery.enumeration.BatteryConstants;
import com.pg.dsm.upload.battery.interfaces.ProductPartBean;
import matrix.db.Context;
import org.apache.log4j.Logger;

import java.util.Map;

public class DeviceProductPartBean implements ProductPartBean {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    String id;
    String type;
    String name;
    String revision;
    String currentState;
    String policy;
    String vault;
    String powerSource;
    String batteryType;
    String numberOfBatteriesShippedInsideDevice;
    String numberOfBatteriesRequired;
    String netWeight;

    // not applicable
    String isBattery;

    public DeviceProductPartBean(Context context, Map<?, ?> map) {
        logger.info("Constructor");
        setId((String) map.get(DomainConstants.SELECT_ID));
        setType((String) map.get(DomainConstants.SELECT_TYPE));
        setName((String) map.get(DomainConstants.SELECT_NAME));
        setRevision((String) map.get(DomainConstants.SELECT_REVISION));
        setCurrentState((String) map.get(DomainConstants.SELECT_CURRENT));
        setPolicy((String) map.get(DomainConstants.SELECT_POLICY));
        setVault((String) map.get(DomainConstants.SELECT_VAULT));
        setPowerSource((String) map.get(BatteryConstants.Attribute.POWER_SOURCE.getAttributeSelect(context)));
        setBatteryType((String) map.get(BatteryConstants.Attribute.BATTERY_TYPE.getAttributeSelect(context)));
        setNumberOfBatteriesShippedInsideDevice((String) map.get(BatteryConstants.Attribute.BATTERIES_SHIPPED_INSIDE_DEVICE.getAttributeSelect(context)));
        setNumberOfBatteriesRequired((String) map.get(BatteryConstants.Attribute.NUMBER_OF_BATTERIES_REQUIRED.getAttributeSelect(context)));
        setNetWeight((String) map.get(BatteryConstants.Attribute.NET_WEIGHT.getAttributeSelect(context)));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String getCurrentState() {
        return currentState;
    }

    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public String getVault() {
        return vault;
    }

    public void setVault(String vault) {
        this.vault = vault;
    }

    @Override
    public String getPowerSource() {
        return powerSource;
    }

    @Override
    public void setPowerSource(String powerSource) {
        this.powerSource = powerSource;
    }

    @Override
    public String getBatteryType() {
        return batteryType;
    }

    @Override
    public void setBatteryType(String batteryType) {
        this.batteryType = batteryType;
    }

    @Override
    public String getNumberOfBatteriesShippedInsideDevice() {
        return numberOfBatteriesShippedInsideDevice;
    }

    @Override
    public void setNumberOfBatteriesShippedInsideDevice(String numberOfBatteriesShippedInsideDevice) {
        this.numberOfBatteriesShippedInsideDevice = numberOfBatteriesShippedInsideDevice;
    }

    @Override
    public String getNumberOfBatteriesRequired() {
        return numberOfBatteriesRequired;
    }

    @Override
    public void setNumberOfBatteriesRequired(String numberOfBatteriesRequired) {
        this.numberOfBatteriesRequired = numberOfBatteriesRequired;
    }

    @Override
    public String getIsBattery() {
        return isBattery;
    }

    @Override
    public void setIsBattery(String isBattery) {
        this.isBattery = isBattery;
    }

    @Override
    public String getNetWeight() {
        return netWeight;
    }

    @Override
    public void setNetWeight(String netWeight) {
        this.netWeight = netWeight;
    }
}

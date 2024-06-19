/*
 **   BatteryProduct.java
 **   Description - Introduced as part of Battery Data Load - 18x.6
 **   About - Bean class for XML to JAVA object.
 **
 */

package com.pg.dsm.upload.battery.models.config;

import com.pg.dsm.upload.battery.interfaces.ProductPartBean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "batteryProductFeature"
})
@XmlRootElement(name = "batteryProduct")
public class BatteryProduct {
    @XmlAttribute(name = "identifier")
    protected String identifier;
    @XmlAttribute(name = "schema")
    protected String schema;
    @XmlAttribute(name = "display")
    protected String display;
    @XmlAttribute(name = "verifierClass")
    protected String verifierClass;
    @XmlTransient
    protected String name;
    @XmlTransient
    protected String revision;
    @XmlTransient
    protected String powerSource;
    @XmlTransient
    protected String batteryType;
    @XmlTransient
    protected String numberOfBatteriesShippedInsideDevice;
    @XmlTransient
    protected String numberOfBatteriesRequired;
    @XmlTransient
    protected String isBattery;
    @XmlTransient
    protected String netWeight;
    @XmlElement(required = true)
    List<BatteryProductFeature> batteryProductFeature;
    @XmlTransient
    boolean beanExist;

    @XmlTransient
    List<String> errorMessageList;

    @XmlTransient
    ProductPartBean productPartBean;

    public List<BatteryProductFeature> getBatteryProductFeatures() {
        if (batteryProductFeature == null) {
            batteryProductFeature = new ArrayList<>();
        }
        return this.batteryProductFeature;
    }

    public void setBatteryProductFeatures(List<BatteryProductFeature> batteryProductFeatures) {
        this.batteryProductFeature = batteryProductFeatures;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getVerifierClass() {
        return verifierClass;
    }

    public void setVerifierClass(String verifierClass) {
        this.verifierClass = verifierClass;
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

    public String getPowerSource() {
        return powerSource;
    }

    public void setPowerSource(String powerSource) {
        this.powerSource = powerSource;
    }

    public String getBatteryType() {
        return batteryType;
    }

    public void setBatteryType(String batteryType) {
        this.batteryType = batteryType;
    }

    public String getNumberOfBatteriesShippedInsideDevice() {
        return numberOfBatteriesShippedInsideDevice;
    }

    public void setNumberOfBatteriesShippedInsideDevice(String numberOfBatteriesShippedInsideDevice) {
        this.numberOfBatteriesShippedInsideDevice = numberOfBatteriesShippedInsideDevice;
    }

    public String getNumberOfBatteriesRequired() {
        return numberOfBatteriesRequired;
    }

    public void setNumberOfBatteriesRequired(String numberOfBatteriesRequired) {
        this.numberOfBatteriesRequired = numberOfBatteriesRequired;
    }

    public String getIsBattery() {
        return isBattery;
    }

    public void setIsBattery(String isBattery) {
        this.isBattery = isBattery;
    }

    public boolean isBeanExist() {
        return beanExist;
    }

    public void setBeanExist(boolean beanExist) {
        this.beanExist = beanExist;
    }

    public List<String> getErrorMessageList() {
        return errorMessageList;
    }

    public void setErrorMessageList(List<String> errorMessageList) {
        this.errorMessageList = errorMessageList;
    }

    public ProductPartBean getProductPartBean() {
        return productPartBean;
    }

    public void setProductPartBean(ProductPartBean productPartBean) {
        this.productPartBean = productPartBean;
    }

    public String getNetWeight() {
        return netWeight;
    }

    public void setNetWeight(String netWeight) {
        this.netWeight = netWeight;
    }
}

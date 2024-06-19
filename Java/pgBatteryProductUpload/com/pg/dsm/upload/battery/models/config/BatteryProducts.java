/*
 **   BatteryProducts.java
 **   Description - Introduced as part of Battery Data Load - 18x.6
 **   About - Bean class for XML to JAVA object.
 **
 */

package com.pg.dsm.upload.battery.models.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "batteryProduct"
})
@XmlRootElement(name = "batteryProducts")
public class BatteryProducts {
    @XmlElement(required = true)
    List<BatteryProduct> batteryProduct;

    public List<BatteryProduct> getBatteryProducts() {
        if (batteryProduct == null) {
            batteryProduct = new ArrayList<>();
        }
        return this.batteryProduct;
    }

    public void setBatteryProducts(List<BatteryProduct> batteryProducts) {
        this.batteryProduct = batteryProducts;
    }
}

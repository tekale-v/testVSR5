/*Created by DSM for Requirement 47389 2022x.04 Dec CW 2023 */
package com.pg.dsm.gendocondemand.enumeration;

public enum GenDocConstant {
   
    CTRLM_PROPERTIES_FILE("/var/opt/gplm/scripts/common/GenDocPartOnDemandCron.properties");
    
    private String value;

    GenDocConstant(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

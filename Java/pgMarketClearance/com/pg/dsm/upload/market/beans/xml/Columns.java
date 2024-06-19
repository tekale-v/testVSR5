/*
 **   Columns.java
 **   Description - Introduced as part of Upload Market Clearance feature - 18x.5.
 **   JAXB Bean with getter/setter
 **
 */
package com.pg.dsm.upload.market.beans.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "column"
})
@XmlRootElement(name = "columns")
public class Columns {
    @XmlElement(required = true)
    protected List<Column> column;

    public List<Column> getColumns() {
        if (column == null) {
            column = new ArrayList<>();
        }
        return this.column;
    }
}

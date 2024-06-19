package com.pg.dsm.preference.template.pages.create;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "field",
})
@XmlRootElement(name = "fields")
public class Fields {
    @XmlElement(required = true)
    protected List<Field> field;

    public List<Field> getField() {
        if (field == null) {
            field = new ArrayList<>();
        }
        return this.field;
    }

    public void setField(List<Field> field) {
        this.field = field;
    }

    @Override
    public String toString() {
        return "Fields{" +
                "field=" + field +
                '}';
    }
}

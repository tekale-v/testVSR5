package com.pg.dsm.preference.cli;


import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "parameter",
})
@XmlRootElement(name = "parameters")
public class Parameters {
    @XmlElement(required = true)
    protected List<Parameter> parameter;

    public List<Parameter> getParameter() {
        if (parameter == null) {
            parameter = new ArrayList<>();
        }
        return this.parameter;
    }

    public void setParameter(List<Parameter> parameter) {
        this.parameter = parameter;
    }

    @Override
    public String toString() {
        return "Parameters{" +
                "parameter=" + parameter +
                '}';
    }
}

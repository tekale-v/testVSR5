package com.pg.dsm.rollup_event.common.config.phys_chem;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        ""
})
@XmlRootElement(name = "physChemAttribute")
public class PhysChemAttribute {
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "schemaType")
    protected String schemaType;
    @XmlAttribute(name = "setter")
    protected String setter;
    @XmlAttribute(name = "schemaName")
    protected String schemaName;
    @XmlAttribute(name = "identifier")
    protected String identifier;
    @XmlAttribute(name = "dataType")
    protected String dataType;
    @XmlAttribute(name = "customDefaultValue")
    protected String customDefaultValue;
    @XmlAttribute(name = "updateCustomDefaultValue")
    protected boolean updateCustomDefaultValue;

    @XmlTransient
    protected String attributeActualName;
    @XmlTransient
    protected String attributeDefaultValue;
    @XmlTransient
    protected String attributeSelectExpression;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchemaType() {
        return schemaType;
    }

    public void setSchemaType(String schemaType) {
        this.schemaType = schemaType;
    }

    public String getSetter() {
        return setter;
    }

    public void setSetter(String setter) {
        this.setter = setter;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }


    public String getAttributeDefaultValue() {
        return attributeDefaultValue;
    }

    public void setAttributeDefaultValue(String attributeDefaultValue) {
        this.attributeDefaultValue = attributeDefaultValue;
    }

    public String getAttributeActualName() {
        return attributeActualName;
    }

    public void setAttributeActualName(String attributeActualName) {
        this.attributeActualName = attributeActualName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getCustomDefaultValue() {
        return customDefaultValue;
    }

    public void setCustomDefaultValue(String customDefaultValue) {
        this.customDefaultValue = customDefaultValue;
    }

    public boolean isUpdateCustomDefaultValue() {
        return updateCustomDefaultValue;
    }

    public void setUpdateCustomDefaultValue(boolean updateCustomDefaultValue) {
        this.updateCustomDefaultValue = updateCustomDefaultValue;
    }

    public String getAttributeSelectExpression() {
        return attributeSelectExpression;
    }

    public PhysChemAttribute setAttributeSelectExpression(String attributeSelectExpression) {
        this.attributeSelectExpression = attributeSelectExpression;
        return this;
    }
}

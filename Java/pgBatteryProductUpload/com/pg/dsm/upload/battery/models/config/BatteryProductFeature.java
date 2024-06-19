/*
 **   BatteryProductFeature.java
 **   Description - Introduced as part of Battery Data Load - 18x.6
 **   About - Bean class for XML to JAVA object.
 **
 */

package com.pg.dsm.upload.battery.models.config;

import matrix.util.StringList;

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
@XmlRootElement(name = "batteryProductFeature")
public class BatteryProductFeature {
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "required")
    protected boolean required;
    @XmlAttribute(name = "order")
    protected int order;
    @XmlAttribute(name = "type")
    protected String type;
    @XmlAttribute(name = "display")
    protected String display;
    @XmlAttribute(name = "setter")
    protected String setter;
    @XmlAttribute(name = "error")
    protected String error;
    @XmlAttribute(name = "identifier")
    protected String identifier;
    @XmlAttribute(name = "select")
    protected String select;
    @XmlAttribute(name = "selection")
    protected String selection;
    @XmlAttribute(name = "validationMethod")
    protected String validationMethod;
    @XmlAttribute(name = "picklist")
    protected boolean picklist;
    @XmlAttribute(name = "picklistName")
    protected String picklistName;
    @XmlAttribute(name = "picklistState")
    protected String picklistState;

    @XmlTransient
    protected String value;

    @XmlTransient
    protected String attributeName;
    @XmlTransient
    protected String attributeSelect;
    @XmlTransient
    protected String attributeDefaultValue;
    @XmlTransient
    protected StringList attributeRanges;
    @XmlTransient
    protected boolean isAttributeMultiline;
    @XmlTransient
    protected boolean isAttributeSingleValue;
    @XmlTransient
    protected boolean isAttributeMultiValue;
    @XmlTransient
    protected boolean isAttributeRangeValue;
    @XmlTransient
    protected int attributeMaxLength;

    @XmlTransient
    protected StringList picklistNames;
    @XmlTransient
    protected StringList picklistRevisions;
    @XmlTransient
    protected StringList picklistIds;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getSetter() {
        return setter;
    }

    public void setSetter(String setter) {
        this.setter = setter;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getSelect() {
        return select;
    }

    public void setSelect(String select) {
        this.select = select;
    }

    public String getSelection() {
        return selection;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

    public String getValidationMethod() {
        return validationMethod;
    }

    public void setValidationMethod(String validationMethod) {
        this.validationMethod = validationMethod;
    }

    public boolean isPicklist() {
        return picklist;
    }

    public void setPicklist(boolean picklist) {
        this.picklist = picklist;
    }

    public String getPicklistName() {
        return picklistName;
    }

    public void setPicklistName(String picklistName) {
        this.picklistName = picklistName;
    }

    public String getPicklistState() {
        return picklistState;
    }

    public void setPicklistState(String picklistState) {
        this.picklistState = picklistState;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeSelect() {
        return attributeSelect;
    }

    public void setAttributeSelect(String attributeSelect) {
        this.attributeSelect = attributeSelect;
    }

    public String getAttributeDefaultValue() {
        return attributeDefaultValue;
    }

    public void setAttributeDefaultValue(String attributeDefaultValue) {
        this.attributeDefaultValue = attributeDefaultValue;
    }

    public StringList getAttributeRanges() {
        return attributeRanges;
    }

    public void setAttributeRanges(StringList attributeRanges) {
        this.attributeRanges = attributeRanges;
    }

    public boolean isAttributeMultiline() {
        return isAttributeMultiline;
    }

    public void setAttributeMultiline(boolean attributeMultiline) {
        isAttributeMultiline = attributeMultiline;
    }

    public boolean isAttributeSingleValue() {
        return isAttributeSingleValue;
    }

    public void setAttributeSingleValue(boolean attributeSingleValue) {
        isAttributeSingleValue = attributeSingleValue;
    }

    public boolean isAttributeMultiValue() {
        return isAttributeMultiValue;
    }

    public void setAttributeMultiValue(boolean attributeMultiValue) {
        isAttributeMultiValue = attributeMultiValue;
    }

    public boolean isAttributeRangeValue() {
        return isAttributeRangeValue;
    }

    public void setAttributeRangeValue(boolean attributeRangeValue) {
        isAttributeRangeValue = attributeRangeValue;
    }

    public int getAttributeMaxLength() {
        return attributeMaxLength;
    }

    public void setAttributeMaxLength(int attributeMaxLength) {
        this.attributeMaxLength = attributeMaxLength;
    }

    public StringList getPicklistNames() {
        return picklistNames;
    }

    public void setPicklistNames(StringList picklistNames) {
        this.picklistNames = picklistNames;
    }

    public StringList getPicklistRevisions() {
        return picklistRevisions;
    }

    public void setPicklistRevisions(StringList picklistRevisions) {
        this.picklistRevisions = picklistRevisions;
    }

    public StringList getPicklistIds() {
        return picklistIds;
    }

    public void setPicklistIds(StringList picklistIds) {
        this.picklistIds = picklistIds;
    }
}

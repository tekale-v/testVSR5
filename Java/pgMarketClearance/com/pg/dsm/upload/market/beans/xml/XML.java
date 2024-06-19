/*
 **   XML.java
 **   Description - Introduced as part of Upload Market Clearance feature - 18x.5.
 **   JAXB Bean with getter/setter
 **
 */
package com.pg.dsm.upload.market.beans.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "columns"
})
@XmlRootElement(name = "xml")
public class XML {
    @XmlAttribute(name = "types")
    protected String types;
    @XmlAttribute(name = "policies")
    protected String policies;
    @XmlAttribute(name = "validationErrorEmailSubject")
    protected String validationErrorEmailSubject;
    @XmlAttribute(name = "validationErrorEmailBody")
    protected String validationErrorEmailBody;
    @XmlAttribute(name = "processedEmailSubject")
    protected String processedEmailSubject;
    @XmlAttribute(name = "processedEmailBody")
    protected String processedEmailBody;
    @XmlElement(required = true)
    protected List<Columns> columns;

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public List<Columns> getColumns() {
        if (columns == null) {
            columns = new ArrayList<>();
        }
        return this.columns;
    }

    public String getValidationErrorEmailSubject() {
        return validationErrorEmailSubject;
    }

    public void setValidationErrorEmailSubject(String validationErrorEmailSubject) {
        this.validationErrorEmailSubject = validationErrorEmailSubject;
    }

    public String getValidationErrorEmailBody() {
        return validationErrorEmailBody;
    }

    public void setValidationErrorEmailBody(String validationErrorEmailBody) {
        this.validationErrorEmailBody = validationErrorEmailBody;
    }

    public String getProcessedEmailSubject() {
        return processedEmailSubject;
    }

    public void setProcessedEmailSubject(String processedEmailSubject) {
        this.processedEmailSubject = processedEmailSubject;
    }

    public String getProcessedEmailBody() {
        return processedEmailBody;
    }

    public void setProcessedEmailBody(String processedEmailBody) {
        this.processedEmailBody = processedEmailBody;
    }

    public String getPolicies() {
        return policies;
    }

    public void setPolicies(String policies) {
        this.policies = policies;
    }
}

package com.pg.dsm.preference.config.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "partType",
        "phase",
        "mfgStatus",
        "structureReleaseCriteria",
        "classType",
        "reportedFunction",
        "segment",
        "componentType",
        "materialType",
        "baseUoM",
})
@XmlRootElement(name = "packagingPreferenceConfig")
public class PackagingPreferenceConfig {
    @XmlAttribute(name = "types")
    protected String types;
    @XmlElement(required = true, name = "partType")
    protected PartType partType;
    @XmlElement(required = true, name = "phase")
    protected Phase phase;
    @XmlElement(required = true, name = "mfgStatus")
    protected MfgStatus mfgStatus;
    @XmlElement(required = true, name = "structureReleaseCriteria")
    protected StructureReleaseCriteria structureReleaseCriteria;
    @XmlElement(required = true, name = "classType")
    protected ClassType classType;
    @XmlElement(required = true, name = "reportedFunction")
    protected ReportedFunction reportedFunction;
    @XmlElement(required = true, name = "segment")
    protected Segment segment;
    @XmlElement(required = true, name = "componentType")
    protected ComponentType componentType;
    @XmlElement(required = true, name = "materialType")
    protected MaterialType materialType;
    @XmlElement(required = true, name = "baseUoM")
    protected BaseUoM baseUoM;

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public PartType getPartType() {
        return partType;
    }

    public void setPartType(PartType partType) {
        this.partType = partType;
    }

    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }

    public MfgStatus getMfgStatus() {
        return mfgStatus;
    }

    public void setMfgStatus(MfgStatus mfgStatus) {
        this.mfgStatus = mfgStatus;
    }

    public StructureReleaseCriteria getStructureReleaseCriteria() {
        return structureReleaseCriteria;
    }

    public void setStructureReleaseCriteria(StructureReleaseCriteria structureReleaseCriteria) {
        this.structureReleaseCriteria = structureReleaseCriteria;
    }

    public ClassType getClassType() {
        return classType;
    }

    public void setClassType(ClassType classType) {
        this.classType = classType;
    }

    public ReportedFunction getReportedFunction() {
        return reportedFunction;
    }

    public void setReportedFunction(ReportedFunction reportedFunction) {
        this.reportedFunction = reportedFunction;
    }

    public Segment getSegment() {
        return segment;
    }

    public void setSegment(Segment segment) {
        this.segment = segment;
    }

    public ComponentType getComponentType() {
        return componentType;
    }

    public void setComponentType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public MaterialType getMaterialType() {
        return materialType;
    }

    public void setMaterialType(MaterialType materialType) {
        this.materialType = materialType;
    }

    public BaseUoM getBaseUoM() {
        return baseUoM;
    }

    public void setBaseUoM(BaseUoM baseUoM) {
        this.baseUoM = baseUoM;
    }

    @Override
    public String toString() {
        return "PackagingPreferenceConfig{" +
                "types='" + types + '\'' +
                ", partType=" + partType +
                ", phase=" + phase +
                ", mfgStatus=" + mfgStatus +
                ", structureReleaseCriteria=" + structureReleaseCriteria +
                ", classType=" + classType +
                ", reportedFunction=" + reportedFunction +
                ", segment=" + segment +
                ", componentType=" + componentType +
                ", materialType=" + materialType +
                ", baseUoM=" + baseUoM +
                '}';
    }
}

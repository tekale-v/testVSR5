/**
 * GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasure.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package com.pg.xi.mdm.gpdb;

public class GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasure  implements java.io.Serializable {
    /* Numerator for Conversion to Base Units of Measure MARM-UMREZ */
    private long numerator;

    /* Denominator for Conversion to Base Units of Measure */
    private long denominator;

    /* MARM-EAN11 */
    private java.lang.String GTIN;

    /* MARM-LAENG */
    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureLengthMetric lengthMetric;

    /* MARM-BREIT */
    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureWidthMetric widthMetric;

    /* MARM-HOEGE */
    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureHeightMetric heightMetric;

    /* MARM-VOLEH */
    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureVolumeMetric volumeMetric;

    /* MARM-BRGEW */
    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureGrossWeightMetric grossWeightMetric;

    /* ZTXXPTCMRM-LAENG */
    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureLengthImperial lengthImperial;

    /* ZTXXPTCMRM-BREIT */
    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureWidthImperial widthImperial;

    /* ZTXXPTCMRM-HOEGE */
    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureHeightImperial heightImperial;

    /* ZTXXPTCMRM-VOLEH */
    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureVolumeImperial volumeImperial;

    /* ZTXXPTCMRM-BRGEW */
    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureGrossWeightImperial grossWeightImperial;

    private java.lang.String AUoM;  // attribute

    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureOriginalUomSystem originalUomSystem;  // attribute

    public GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasure() {
    }

    public GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasure(
           long numerator,
           long denominator,
           java.lang.String GTIN,
           com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureLengthMetric lengthMetric,
           com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureWidthMetric widthMetric,
           com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureHeightMetric heightMetric,
           com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureVolumeMetric volumeMetric,
           com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureGrossWeightMetric grossWeightMetric,
           com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureLengthImperial lengthImperial,
           com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureWidthImperial widthImperial,
           com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureHeightImperial heightImperial,
           com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureVolumeImperial volumeImperial,
           com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureGrossWeightImperial grossWeightImperial,
           java.lang.String AUoM,
           com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureOriginalUomSystem originalUomSystem) {
           this.numerator = numerator;
           this.denominator = denominator;
           this.GTIN = GTIN;
           this.lengthMetric = lengthMetric;
           this.widthMetric = widthMetric;
           this.heightMetric = heightMetric;
           this.volumeMetric = volumeMetric;
           this.grossWeightMetric = grossWeightMetric;
           this.lengthImperial = lengthImperial;
           this.widthImperial = widthImperial;
           this.heightImperial = heightImperial;
           this.volumeImperial = volumeImperial;
           this.grossWeightImperial = grossWeightImperial;
           this.AUoM = AUoM;
           this.originalUomSystem = originalUomSystem;
    }


    /**
     * Gets the numerator value for this GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasure.
     * 
     * @return numerator   * Numerator for Conversion to Base Units of Measure MARM-UMREZ
     */
    public long getNumerator() {
        return numerator;
    }


    /**
     * Sets the numerator value for this GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasure.
     * 
     * @param numerator   * Numerator for Conversion to Base Units of Measure MARM-UMREZ
     */
    public void setNumerator(long numerator) {
        this.numerator = numerator;
    }


    /**
     * Gets the denominator value for this GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasure.
     * 
     * @return denominator   * Denominator for Conversion to Base Units of Measure
     */
    public long getDenominator() {
        return denominator;
    }


    /**
     * Sets the denominator value for this GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasure.
     * 
     * @param denominator   * Denominator for Conversion to Base Units of Measure
     */
    public void setDenominator(long denominator) {
        this.denominator = denominator;
    }


    /**
     * Gets the GTIN value for this GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasure.
     * 
     * @return GTIN   * MARM-EAN11
     */
    public java.lang.String getGTIN() {
        return GTIN;
    }


    /**
     * Sets the GTIN value for this GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasure.
     * 
     * @param GTIN   * MARM-EAN11
     */
    public void setGTIN(java.lang.String GTIN) {
        this.GTIN = GTIN;
    }


    /**
     * Gets the lengthMetric value for this GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasure.
     * 
     * @return lengthMetric   * MARM-LAENG
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureLengthMetric getLengthMetric() {
        return lengthMetric;
    }


    /**
     * Sets the lengthMetric value for this GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasure.
     * 
     * @param lengthMetric   * MARM-LAENG
     */
    public void setLengthMetric(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureLengthMetric lengthMetric) {
        this.lengthMetric = lengthMetric;
    }


    /**
     * Gets the widthMetric value for this GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasure.
     * 
     * @return widthMetric   * MARM-BREIT
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureWidthMetric getWidthMetric() {
        return widthMetric;
    }


    /**
     * Sets the widthMetric value for this GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasure.
     * 
     * @param widthMetric   * MARM-BREIT
     */
    public void setWidthMetric(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureWidthMetric widthMetric) {
        this.widthMetric = widthMetric;
    }


    /**
     * Gets the heightMetric value for this GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasure.
     * 
     * @return heightMetric   * MARM-HOEGE
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureHeightMetric getHeightMetric() {
        return heightMetric;
    }


    /**
     * Sets the heightMetric value for this GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasure.
     * 
     * @param heightMetric   * MARM-HOEGE
     */
    public void setHeightMetric(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureHeightMetric heightMetric) {
        this.heightMetric = heightMetric;
    }


    /**
     * Gets the volumeMetric value for this GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasure.
     * 
     * @return volumeMetric   * MARM-VOLEH
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureVolumeMetric getVolumeMetric() {
        return volumeMetric;
    }


    /**
     * Sets the volumeMetric value for this GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasure.
     * 
     * @param volumeMetric   * MARM-VOLEH
     */
    public void setVolumeMetric(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureVolumeMetric volumeMetric) {
        this.volumeMetric = volumeMetric;
    }


    /**
     * Gets the grossWeightMetric value for this GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasure.
     * 
     * @return grossWeightMetric   * MARM-BRGEW
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureGrossWeightMetric getGrossWeightMetric() {
        return grossWeightMetric;
    }


    /**
     * Sets the grossWeightMetric value for this GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasure.
     * 
     * @param grossWeightMetric   * MARM-BRGEW
     */
    public void setGrossWeightMetric(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureGrossWeightMetric grossWeightMetric) {
        this.grossWeightMetric = grossWeightMetric;
    }


    /**
     * Gets the lengthImperial value for this GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasure.
     * 
     * @return lengthImperial   * ZTXXPTCMRM-LAENG
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureLengthImperial getLengthImperial() {
        return lengthImperial;
    }


    /**
     * Sets the lengthImperial value for this GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasure.
     * 
     * @param lengthImperial   * ZTXXPTCMRM-LAENG
     */
    public void setLengthImperial(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureLengthImperial lengthImperial) {
        this.lengthImperial = lengthImperial;
    }


    /**
     * Gets the widthImperial value for this GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasure.
     * 
     * @return widthImperial   * ZTXXPTCMRM-BREIT
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureWidthImperial getWidthImperial() {
        return widthImperial;
    }


    /**
     * Sets the widthImperial value for this GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasure.
     * 
     * @param widthImperial   * ZTXXPTCMRM-BREIT
     */
    public void setWidthImperial(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureWidthImperial widthImperial) {
        this.widthImperial = widthImperial;
    }


    /**
     * Gets the heightImperial value for this GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasure.
     * 
     * @return heightImperial   * ZTXXPTCMRM-HOEGE
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureHeightImperial getHeightImperial() {
        return heightImperial;
    }


    /**
     * Sets the heightImperial value for this GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasure.
     * 
     * @param heightImperial   * ZTXXPTCMRM-HOEGE
     */
    public void setHeightImperial(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureHeightImperial heightImperial) {
        this.heightImperial = heightImperial;
    }


    /**
     * Gets the volumeImperial value for this GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasure.
     * 
     * @return volumeImperial   * ZTXXPTCMRM-VOLEH
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureVolumeImperial getVolumeImperial() {
        return volumeImperial;
    }


    /**
     * Sets the volumeImperial value for this GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasure.
     * 
     * @param volumeImperial   * ZTXXPTCMRM-VOLEH
     */
    public void setVolumeImperial(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureVolumeImperial volumeImperial) {
        this.volumeImperial = volumeImperial;
    }


    /**
     * Gets the grossWeightImperial value for this GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasure.
     * 
     * @return grossWeightImperial   * ZTXXPTCMRM-BRGEW
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureGrossWeightImperial getGrossWeightImperial() {
        return grossWeightImperial;
    }


    /**
     * Sets the grossWeightImperial value for this GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasure.
     * 
     * @param grossWeightImperial   * ZTXXPTCMRM-BRGEW
     */
    public void setGrossWeightImperial(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureGrossWeightImperial grossWeightImperial) {
        this.grossWeightImperial = grossWeightImperial;
    }


    /**
     * Gets the AUoM value for this GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasure.
     * 
     * @return AUoM
     */
    public java.lang.String getAUoM() {
        return AUoM;
    }


    /**
     * Sets the AUoM value for this GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasure.
     * 
     * @param AUoM
     */
    public void setAUoM(java.lang.String AUoM) {
        this.AUoM = AUoM;
    }


    /**
     * Gets the originalUomSystem value for this GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasure.
     * 
     * @return originalUomSystem
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureOriginalUomSystem getOriginalUomSystem() {
        return originalUomSystem;
    }


    /**
     * Sets the originalUomSystem value for this GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasure.
     * 
     * @param originalUomSystem
     */
    public void setOriginalUomSystem(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureOriginalUomSystem originalUomSystem) {
        this.originalUomSystem = originalUomSystem;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasure)) return false;
        GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasure other = (GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasure) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.numerator == other.getNumerator() &&
            this.denominator == other.getDenominator() &&
            ((this.GTIN==null && other.getGTIN()==null) || 
             (this.GTIN!=null &&
              this.GTIN.equals(other.getGTIN()))) &&
            ((this.lengthMetric==null && other.getLengthMetric()==null) || 
             (this.lengthMetric!=null &&
              this.lengthMetric.equals(other.getLengthMetric()))) &&
            ((this.widthMetric==null && other.getWidthMetric()==null) || 
             (this.widthMetric!=null &&
              this.widthMetric.equals(other.getWidthMetric()))) &&
            ((this.heightMetric==null && other.getHeightMetric()==null) || 
             (this.heightMetric!=null &&
              this.heightMetric.equals(other.getHeightMetric()))) &&
            ((this.volumeMetric==null && other.getVolumeMetric()==null) || 
             (this.volumeMetric!=null &&
              this.volumeMetric.equals(other.getVolumeMetric()))) &&
            ((this.grossWeightMetric==null && other.getGrossWeightMetric()==null) || 
             (this.grossWeightMetric!=null &&
              this.grossWeightMetric.equals(other.getGrossWeightMetric()))) &&
            ((this.lengthImperial==null && other.getLengthImperial()==null) || 
             (this.lengthImperial!=null &&
              this.lengthImperial.equals(other.getLengthImperial()))) &&
            ((this.widthImperial==null && other.getWidthImperial()==null) || 
             (this.widthImperial!=null &&
              this.widthImperial.equals(other.getWidthImperial()))) &&
            ((this.heightImperial==null && other.getHeightImperial()==null) || 
             (this.heightImperial!=null &&
              this.heightImperial.equals(other.getHeightImperial()))) &&
            ((this.volumeImperial==null && other.getVolumeImperial()==null) || 
             (this.volumeImperial!=null &&
              this.volumeImperial.equals(other.getVolumeImperial()))) &&
            ((this.grossWeightImperial==null && other.getGrossWeightImperial()==null) || 
             (this.grossWeightImperial!=null &&
              this.grossWeightImperial.equals(other.getGrossWeightImperial()))) &&
            ((this.AUoM==null && other.getAUoM()==null) || 
             (this.AUoM!=null &&
              this.AUoM.equals(other.getAUoM()))) &&
            ((this.originalUomSystem==null && other.getOriginalUomSystem()==null) || 
             (this.originalUomSystem!=null &&
              this.originalUomSystem.equals(other.getOriginalUomSystem())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        _hashCode += new Long(getNumerator()).hashCode();
        _hashCode += new Long(getDenominator()).hashCode();
        if (getGTIN() != null) {
            _hashCode += getGTIN().hashCode();
        }
        if (getLengthMetric() != null) {
            _hashCode += getLengthMetric().hashCode();
        }
        if (getWidthMetric() != null) {
            _hashCode += getWidthMetric().hashCode();
        }
        if (getHeightMetric() != null) {
            _hashCode += getHeightMetric().hashCode();
        }
        if (getVolumeMetric() != null) {
            _hashCode += getVolumeMetric().hashCode();
        }
        if (getGrossWeightMetric() != null) {
            _hashCode += getGrossWeightMetric().hashCode();
        }
        if (getLengthImperial() != null) {
            _hashCode += getLengthImperial().hashCode();
        }
        if (getWidthImperial() != null) {
            _hashCode += getWidthImperial().hashCode();
        }
        if (getHeightImperial() != null) {
            _hashCode += getHeightImperial().hashCode();
        }
        if (getVolumeImperial() != null) {
            _hashCode += getVolumeImperial().hashCode();
        }
        if (getGrossWeightImperial() != null) {
            _hashCode += getGrossWeightImperial().hashCode();
        }
        if (getAUoM() != null) {
            _hashCode += getAUoM().hashCode();
        }
        if (getOriginalUomSystem() != null) {
            _hashCode += getOriginalUomSystem().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasure.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>GPDB_ProductGetDetailsResult>Product>AlternativeUnitsOfMeasures>AlternativeUnitOfMeasure"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("AUoM");
        attrField.setXmlName(new javax.xml.namespace.QName("", "AUoM"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>>GPDB_ProductGetDetailsResult>Product>AlternativeUnitsOfMeasures>AlternativeUnitOfMeasure>AUoM"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("originalUomSystem");
        attrField.setXmlName(new javax.xml.namespace.QName("", "OriginalUomSystem"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>>GPDB_ProductGetDetailsResult>Product>AlternativeUnitsOfMeasures>AlternativeUnitOfMeasure>OriginalUomSystem"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numerator");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "Numerator"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("denominator");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "Denominator"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("GTIN");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "GTIN"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lengthMetric");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "LengthMetric"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>>GPDB_ProductGetDetailsResult>Product>AlternativeUnitsOfMeasures>AlternativeUnitOfMeasure>LengthMetric"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("widthMetric");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "WidthMetric"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>>GPDB_ProductGetDetailsResult>Product>AlternativeUnitsOfMeasures>AlternativeUnitOfMeasure>WidthMetric"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("heightMetric");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "HeightMetric"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>>GPDB_ProductGetDetailsResult>Product>AlternativeUnitsOfMeasures>AlternativeUnitOfMeasure>HeightMetric"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("volumeMetric");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "VolumeMetric"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>>GPDB_ProductGetDetailsResult>Product>AlternativeUnitsOfMeasures>AlternativeUnitOfMeasure>VolumeMetric"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("grossWeightMetric");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "GrossWeightMetric"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>>GPDB_ProductGetDetailsResult>Product>AlternativeUnitsOfMeasures>AlternativeUnitOfMeasure>GrossWeightMetric"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lengthImperial");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "LengthImperial"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>>GPDB_ProductGetDetailsResult>Product>AlternativeUnitsOfMeasures>AlternativeUnitOfMeasure>LengthImperial"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("widthImperial");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "WidthImperial"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>>GPDB_ProductGetDetailsResult>Product>AlternativeUnitsOfMeasures>AlternativeUnitOfMeasure>WidthImperial"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("heightImperial");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "HeightImperial"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>>GPDB_ProductGetDetailsResult>Product>AlternativeUnitsOfMeasures>AlternativeUnitOfMeasure>HeightImperial"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("volumeImperial");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "VolumeImperial"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>>GPDB_ProductGetDetailsResult>Product>AlternativeUnitsOfMeasures>AlternativeUnitOfMeasure>VolumeImperial"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("grossWeightImperial");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "GrossWeightImperial"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>>GPDB_ProductGetDetailsResult>Product>AlternativeUnitsOfMeasures>AlternativeUnitOfMeasure>GrossWeightImperial"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}

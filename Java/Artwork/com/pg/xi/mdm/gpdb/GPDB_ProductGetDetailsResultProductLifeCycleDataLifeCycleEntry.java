/**
 * GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntry.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.pg.xi.mdm.gpdb;

public class GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntry  implements java.io.Serializable {
    /* ZTXXPTMELF-ZLAUNCH */
    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntryLaunchAuthorization launchAuthorization;

    /* ZTXXPTMELF-ZSTAGE */
    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntryCurrentStage currentStage;

    /* ZTXXPTMELF-ZTOSTAGE */
    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntryToBeStage toBeStage;

    /* ZTXXPTMELF-ZSTAGE1  Planned date; Format:CCYY-MM-DD */
    private java.util.Date stage1;

    /* ZTXXPTMELF-ZSTAGE2 preactive date; Format:CCYY-MM-DD */
    private java.util.Date stage2;

    /* ZTXXPTMELF-ZSTAGE3 OK-To-Order date; Format:CCYY-MM-DD */
    private java.util.Date stage3;

    /* ZTXXPTMELF-ZSTAGE4  Active/First-Shipment date; Format:CCYY-MM-DD */
    private java.util.Date stage4;

    /* ZTXXPTMELF-ZSTAGE5 Remnant/Last-order date; Format:CCYY-MM-DD */
    private java.util.Date stage5;

    /* ZTXXPTMELF-ZSTAGE6   Inactive/last-Shipment date; Format:CCYY-MM-DD */
    private java.util.Date stage6;

    /* ZTXXPTMELF-ZSTAGE7 Historcial date; Format:CCYY-MM-DD */
    private java.util.Date stage7;

    private java.lang.String recordType;

    private java.lang.String marketSegmentation;

    private java.lang.String lifeCycleScope;  // attribute

    public GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntry() {
    }

    public GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntry(
           com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntryLaunchAuthorization launchAuthorization,
           com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntryCurrentStage currentStage,
           com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntryToBeStage toBeStage,
           java.util.Date stage1,
           java.util.Date stage2,
           java.util.Date stage3,
           java.util.Date stage4,
           java.util.Date stage5,
           java.util.Date stage6,
           java.util.Date stage7,
           java.lang.String recordType,
           java.lang.String marketSegmentation,
           java.lang.String lifeCycleScope) {
           this.launchAuthorization = launchAuthorization;
           this.currentStage = currentStage;
           this.toBeStage = toBeStage;
           this.stage1 = stage1;
           this.stage2 = stage2;
           this.stage3 = stage3;
           this.stage4 = stage4;
           this.stage5 = stage5;
           this.stage6 = stage6;
           this.stage7 = stage7;
           this.recordType = recordType;
           this.marketSegmentation = marketSegmentation;
           this.lifeCycleScope = lifeCycleScope;
    }


    /**
     * Gets the launchAuthorization value for this GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntry.
     * 
     * @return launchAuthorization   * ZTXXPTMELF-ZLAUNCH
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntryLaunchAuthorization getLaunchAuthorization() {
        return launchAuthorization;
    }


    /**
     * Sets the launchAuthorization value for this GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntry.
     * 
     * @param launchAuthorization   * ZTXXPTMELF-ZLAUNCH
     */
    public void setLaunchAuthorization(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntryLaunchAuthorization launchAuthorization) {
        this.launchAuthorization = launchAuthorization;
    }


    /**
     * Gets the currentStage value for this GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntry.
     * 
     * @return currentStage   * ZTXXPTMELF-ZSTAGE
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntryCurrentStage getCurrentStage() {
        return currentStage;
    }


    /**
     * Sets the currentStage value for this GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntry.
     * 
     * @param currentStage   * ZTXXPTMELF-ZSTAGE
     */
    public void setCurrentStage(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntryCurrentStage currentStage) {
        this.currentStage = currentStage;
    }


    /**
     * Gets the toBeStage value for this GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntry.
     * 
     * @return toBeStage   * ZTXXPTMELF-ZTOSTAGE
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntryToBeStage getToBeStage() {
        return toBeStage;
    }


    /**
     * Sets the toBeStage value for this GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntry.
     * 
     * @param toBeStage   * ZTXXPTMELF-ZTOSTAGE
     */
    public void setToBeStage(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntryToBeStage toBeStage) {
        this.toBeStage = toBeStage;
    }


    /**
     * Gets the stage1 value for this GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntry.
     * 
     * @return stage1   * ZTXXPTMELF-ZSTAGE1  Planned date; Format:CCYY-MM-DD
     */
    public java.util.Date getStage1() {
        return stage1;
    }


    /**
     * Sets the stage1 value for this GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntry.
     * 
     * @param stage1   * ZTXXPTMELF-ZSTAGE1  Planned date; Format:CCYY-MM-DD
     */
    public void setStage1(java.util.Date stage1) {
        this.stage1 = stage1;
    }


    /**
     * Gets the stage2 value for this GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntry.
     * 
     * @return stage2   * ZTXXPTMELF-ZSTAGE2 preactive date; Format:CCYY-MM-DD
     */
    public java.util.Date getStage2() {
        return stage2;
    }


    /**
     * Sets the stage2 value for this GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntry.
     * 
     * @param stage2   * ZTXXPTMELF-ZSTAGE2 preactive date; Format:CCYY-MM-DD
     */
    public void setStage2(java.util.Date stage2) {
        this.stage2 = stage2;
    }


    /**
     * Gets the stage3 value for this GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntry.
     * 
     * @return stage3   * ZTXXPTMELF-ZSTAGE3 OK-To-Order date; Format:CCYY-MM-DD
     */
    public java.util.Date getStage3() {
        return stage3;
    }


    /**
     * Sets the stage3 value for this GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntry.
     * 
     * @param stage3   * ZTXXPTMELF-ZSTAGE3 OK-To-Order date; Format:CCYY-MM-DD
     */
    public void setStage3(java.util.Date stage3) {
        this.stage3 = stage3;
    }


    /**
     * Gets the stage4 value for this GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntry.
     * 
     * @return stage4   * ZTXXPTMELF-ZSTAGE4  Active/First-Shipment date; Format:CCYY-MM-DD
     */
    public java.util.Date getStage4() {
        return stage4;
    }


    /**
     * Sets the stage4 value for this GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntry.
     * 
     * @param stage4   * ZTXXPTMELF-ZSTAGE4  Active/First-Shipment date; Format:CCYY-MM-DD
     */
    public void setStage4(java.util.Date stage4) {
        this.stage4 = stage4;
    }


    /**
     * Gets the stage5 value for this GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntry.
     * 
     * @return stage5   * ZTXXPTMELF-ZSTAGE5 Remnant/Last-order date; Format:CCYY-MM-DD
     */
    public java.util.Date getStage5() {
        return stage5;
    }


    /**
     * Sets the stage5 value for this GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntry.
     * 
     * @param stage5   * ZTXXPTMELF-ZSTAGE5 Remnant/Last-order date; Format:CCYY-MM-DD
     */
    public void setStage5(java.util.Date stage5) {
        this.stage5 = stage5;
    }


    /**
     * Gets the stage6 value for this GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntry.
     * 
     * @return stage6   * ZTXXPTMELF-ZSTAGE6   Inactive/last-Shipment date; Format:CCYY-MM-DD
     */
    public java.util.Date getStage6() {
        return stage6;
    }


    /**
     * Sets the stage6 value for this GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntry.
     * 
     * @param stage6   * ZTXXPTMELF-ZSTAGE6   Inactive/last-Shipment date; Format:CCYY-MM-DD
     */
    public void setStage6(java.util.Date stage6) {
        this.stage6 = stage6;
    }


    /**
     * Gets the stage7 value for this GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntry.
     * 
     * @return stage7   * ZTXXPTMELF-ZSTAGE7 Historcial date; Format:CCYY-MM-DD
     */
    public java.util.Date getStage7() {
        return stage7;
    }


    /**
     * Sets the stage7 value for this GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntry.
     * 
     * @param stage7   * ZTXXPTMELF-ZSTAGE7 Historcial date; Format:CCYY-MM-DD
     */
    public void setStage7(java.util.Date stage7) {
        this.stage7 = stage7;
    }


    /**
     * Gets the recordType value for this GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntry.
     * 
     * @return recordType
     */
    public java.lang.String getRecordType() {
        return recordType;
    }


    /**
     * Sets the recordType value for this GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntry.
     * 
     * @param recordType
     */
    public void setRecordType(java.lang.String recordType) {
        this.recordType = recordType;
    }


    /**
     * Gets the marketSegmentation value for this GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntry.
     * 
     * @return marketSegmentation
     */
    public java.lang.String getMarketSegmentation() {
        return marketSegmentation;
    }


    /**
     * Sets the marketSegmentation value for this GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntry.
     * 
     * @param marketSegmentation
     */
    public void setMarketSegmentation(java.lang.String marketSegmentation) {
        this.marketSegmentation = marketSegmentation;
    }


    /**
     * Gets the lifeCycleScope value for this GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntry.
     * 
     * @return lifeCycleScope
     */
    public java.lang.String getLifeCycleScope() {
        return lifeCycleScope;
    }


    /**
     * Sets the lifeCycleScope value for this GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntry.
     * 
     * @param lifeCycleScope
     */
    public void setLifeCycleScope(java.lang.String lifeCycleScope) {
        this.lifeCycleScope = lifeCycleScope;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntry)) return false;
        GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntry other = (GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntry) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.launchAuthorization==null && other.getLaunchAuthorization()==null) || 
             (this.launchAuthorization!=null &&
              this.launchAuthorization.equals(other.getLaunchAuthorization()))) &&
            ((this.currentStage==null && other.getCurrentStage()==null) || 
             (this.currentStage!=null &&
              this.currentStage.equals(other.getCurrentStage()))) &&
            ((this.toBeStage==null && other.getToBeStage()==null) || 
             (this.toBeStage!=null &&
              this.toBeStage.equals(other.getToBeStage()))) &&
            ((this.stage1==null && other.getStage1()==null) || 
             (this.stage1!=null &&
              this.stage1.equals(other.getStage1()))) &&
            ((this.stage2==null && other.getStage2()==null) || 
             (this.stage2!=null &&
              this.stage2.equals(other.getStage2()))) &&
            ((this.stage3==null && other.getStage3()==null) || 
             (this.stage3!=null &&
              this.stage3.equals(other.getStage3()))) &&
            ((this.stage4==null && other.getStage4()==null) || 
             (this.stage4!=null &&
              this.stage4.equals(other.getStage4()))) &&
            ((this.stage5==null && other.getStage5()==null) || 
             (this.stage5!=null &&
              this.stage5.equals(other.getStage5()))) &&
            ((this.stage6==null && other.getStage6()==null) || 
             (this.stage6!=null &&
              this.stage6.equals(other.getStage6()))) &&
            ((this.stage7==null && other.getStage7()==null) || 
             (this.stage7!=null &&
              this.stage7.equals(other.getStage7()))) &&
            ((this.recordType==null && other.getRecordType()==null) || 
             (this.recordType!=null &&
              this.recordType.equals(other.getRecordType()))) &&
            ((this.marketSegmentation==null && other.getMarketSegmentation()==null) || 
             (this.marketSegmentation!=null &&
              this.marketSegmentation.equals(other.getMarketSegmentation()))) &&
            ((this.lifeCycleScope==null && other.getLifeCycleScope()==null) || 
             (this.lifeCycleScope!=null &&
              this.lifeCycleScope.equals(other.getLifeCycleScope())));
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
        if (getLaunchAuthorization() != null) {
            _hashCode += getLaunchAuthorization().hashCode();
        }
        if (getCurrentStage() != null) {
            _hashCode += getCurrentStage().hashCode();
        }
        if (getToBeStage() != null) {
            _hashCode += getToBeStage().hashCode();
        }
        if (getStage1() != null) {
            _hashCode += getStage1().hashCode();
        }
        if (getStage2() != null) {
            _hashCode += getStage2().hashCode();
        }
        if (getStage3() != null) {
            _hashCode += getStage3().hashCode();
        }
        if (getStage4() != null) {
            _hashCode += getStage4().hashCode();
        }
        if (getStage5() != null) {
            _hashCode += getStage5().hashCode();
        }
        if (getStage6() != null) {
            _hashCode += getStage6().hashCode();
        }
        if (getStage7() != null) {
            _hashCode += getStage7().hashCode();
        }
        if (getRecordType() != null) {
            _hashCode += getRecordType().hashCode();
        }
        if (getMarketSegmentation() != null) {
            _hashCode += getMarketSegmentation().hashCode();
        }
        if (getLifeCycleScope() != null) {
            _hashCode += getLifeCycleScope().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntry.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>GPDB_ProductGetDetailsResult>Product>LifeCycleData>LifeCycleEntry"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("lifeCycleScope");
        attrField.setXmlName(new javax.xml.namespace.QName("", "LifeCycleScope"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>>GPDB_ProductGetDetailsResult>Product>LifeCycleData>LifeCycleEntry>LifeCycleScope"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("launchAuthorization");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "LaunchAuthorization"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>>GPDB_ProductGetDetailsResult>Product>LifeCycleData>LifeCycleEntry>LaunchAuthorization"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currentStage");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "CurrentStage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>>GPDB_ProductGetDetailsResult>Product>LifeCycleData>LifeCycleEntry>CurrentStage"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("toBeStage");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "ToBeStage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>>GPDB_ProductGetDetailsResult>Product>LifeCycleData>LifeCycleEntry>ToBeStage"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("stage1");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "Stage1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("stage2");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "Stage2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("stage3");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "Stage3"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("stage4");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "Stage4"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("stage5");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "Stage5"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("stage6");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "Stage6"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("stage7");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "Stage7"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("recordType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "RecordType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("marketSegmentation");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "MarketSegmentation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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

package com.pdfview.impl.FPP;

//
//This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
//See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
//Any modifications to this file will be lost upon recompilation of the source schema. 
//Generated on: 2017.04.10 at 10:09:18 AM EDT 
//


import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import javax.xml.bind.annotation.XmlType;


/**
* <p>Java class for anonymous complex type.
* 
* <p>The following schema fragment specifies the expected content contained within this class.
* 
* <pre>
* &lt;complexType>
*   &lt;complexContent>
*     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
*       &lt;sequence>
*         &lt;element name="Rule" maxOccurs="unbounded" minOccurs="0">
*           &lt;complexType>
*             &lt;complexContent>
*               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
*                 &lt;sequence>
*                   &lt;element name="Type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
*                   &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
*                   &lt;element name="State" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
*                   &lt;element name="Policy" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
*                   &lt;element name="Owner" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
*                   &lt;element name="Originator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
*                   &lt;element name="Created" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
*                   &lt;element name="Modified" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
*                   &lt;element name="LeftExpressions" minOccurs="0">
*                     &lt;complexType>
*                       &lt;complexContent>
*                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
*                           &lt;sequence>
*                             &lt;element name="LeftExpression" maxOccurs="unbounded" minOccurs="0">
*                               &lt;complexType>
*                                 &lt;complexContent>
*                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
*                                     &lt;sequence>
*                                       &lt;element ref="{http://www.pg.com/Rules_v1}Feature" minOccurs="0"/>
*                                       &lt;element ref="{http://www.pg.com/Rules_v1}Option" minOccurs="0"/>
*                                     &lt;/sequence>
*                                   &lt;/restriction>
*                                 &lt;/complexContent>
*                               &lt;/complexType>
*                             &lt;/element>
*                           &lt;/sequence>
*                         &lt;/restriction>
*                       &lt;/complexContent>
*                     &lt;/complexType>
*                   &lt;/element>
*                   &lt;element name="Compatibility" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
*                   &lt;element name="RightExpressions" minOccurs="0">
*                     &lt;complexType>
*                       &lt;complexContent>
*                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
*                           &lt;sequence>
*                             &lt;element name="RightExpression" maxOccurs="unbounded" minOccurs="0">
*                               &lt;complexType>
*                                 &lt;complexContent>
*                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
*                                     &lt;sequence>
*                                       &lt;element ref="{http://www.pg.com/Rules_v1}Feature" minOccurs="0"/>
*                                       &lt;element ref="{http://www.pg.com/Rules_v1}Option" minOccurs="0"/>
*                                     &lt;/sequence>
*                                   &lt;/restriction>
*                                 &lt;/complexContent>
*                               &lt;/complexType>
*                             &lt;/element>
*                           &lt;/sequence>
*                         &lt;/restriction>
*                       &lt;/complexContent>
*                     &lt;/complexType>
*                   &lt;/element>
*                 &lt;/sequence>
*               &lt;/restriction>
*             &lt;/complexContent>
*           &lt;/complexType>
*         &lt;/element>
*       &lt;/sequence>
*     &lt;/restriction>
*   &lt;/complexContent>
* &lt;/complexType>
* </pre>
* 
* 
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
 "dgc"
})
@XmlRootElement(name = "Dgcs", namespace = "")
public class Dgcs {

 @XmlElement(name = "Dgc", namespace = "")
 protected List<Dgc> dgc;
 
 public List<Dgc> getDgs() {
     if (dgc == null) {
    	 dgc = new ArrayList<Dgc>();
     }
     return this.dgc;
 }
 

}
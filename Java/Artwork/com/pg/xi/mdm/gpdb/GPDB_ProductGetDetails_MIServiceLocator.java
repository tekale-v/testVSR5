/**
 * GPDB_ProductGetDetails_MIServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package com.pg.xi.mdm.gpdb;

public class GPDB_ProductGetDetails_MIServiceLocator extends org.apache.axis.client.Service implements com.pg.xi.mdm.gpdb.GPDB_ProductGetDetails_MIService {

    public GPDB_ProductGetDetails_MIServiceLocator() {
    }


    public GPDB_ProductGetDetails_MIServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public GPDB_ProductGetDetails_MIServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for GPDB_ProductGetDetails_MIPort
    private java.lang.String GPDB_ProductGetDetails_MIPort_address = "https://sapxid.na.pg.com:44351/XISOAPAdapter/MessageServlet?channel=:CIWBD:GPDB_ProductGetDetails&amp;version=3.0&amp;Sender.Service=CIWBD&amp;Interface=http%3A%2F%2Fpg.com%2Fxi%2Fmdm%2Fgpdb%5EGPDB_ProductGetDetails_MI&version=3.0&Sender.Service=CIWBD&Interface=http%3A%2F%2Fpg.com%2Fxi%2Fmdm%2Fgpdb%5EGPDB_ProductGetDetails_MI";

    public java.lang.String getGPDB_ProductGetDetails_MIPortAddress() {
        return GPDB_ProductGetDetails_MIPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String GPDB_ProductGetDetails_MIPortWSDDServiceName = "GPDB_ProductGetDetails_MIPort";

    public java.lang.String getGPDB_ProductGetDetails_MIPortWSDDServiceName() {
        return GPDB_ProductGetDetails_MIPortWSDDServiceName;
    }

    public void setGPDB_ProductGetDetails_MIPortWSDDServiceName(java.lang.String name) {
        GPDB_ProductGetDetails_MIPortWSDDServiceName = name;
    }

    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetails_MI getGPDB_ProductGetDetails_MIPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(GPDB_ProductGetDetails_MIPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getGPDB_ProductGetDetails_MIPort(endpoint);
    }

    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetails_MI getGPDB_ProductGetDetails_MIPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.pg.xi.mdm.gpdb.GPDB_ProductGetDetails_MIBindingStub _stub = new com.pg.xi.mdm.gpdb.GPDB_ProductGetDetails_MIBindingStub(portAddress, this);
            _stub.setPortName(getGPDB_ProductGetDetails_MIPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setGPDB_ProductGetDetails_MIPortEndpointAddress(java.lang.String address) {
        GPDB_ProductGetDetails_MIPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.pg.xi.mdm.gpdb.GPDB_ProductGetDetails_MI.class.isAssignableFrom(serviceEndpointInterface)) {
                com.pg.xi.mdm.gpdb.GPDB_ProductGetDetails_MIBindingStub _stub = new com.pg.xi.mdm.gpdb.GPDB_ProductGetDetails_MIBindingStub(new java.net.URL(GPDB_ProductGetDetails_MIPort_address), this);
                _stub.setPortName(getGPDB_ProductGetDetails_MIPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("GPDB_ProductGetDetails_MIPort".equals(inputPortName)) {
            return getGPDB_ProductGetDetails_MIPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "GPDB_ProductGetDetails_MIService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "GPDB_ProductGetDetails_MIPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("GPDB_ProductGetDetails_MIPort".equals(portName)) {
            setGPDB_ProductGetDetails_MIPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}

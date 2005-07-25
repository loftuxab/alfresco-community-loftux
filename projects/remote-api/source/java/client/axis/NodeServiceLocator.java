/**
 * NodeServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package client.axis;

public class NodeServiceLocator extends org.apache.axis.client.Service implements client.axis.NodeService {

    public NodeServiceLocator() {
    }


    public NodeServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public NodeServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for NodeService
    private java.lang.String NodeService_address = "http://localhost:8080/web-client/remote-api/NodeService";

    public java.lang.String getNodeServiceAddress() {
        return NodeService_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String NodeServiceWSDDServiceName = "NodeService";

    public java.lang.String getNodeServiceWSDDServiceName() {
        return NodeServiceWSDDServiceName;
    }

    public void setNodeServiceWSDDServiceName(java.lang.String name) {
        NodeServiceWSDDServiceName = name;
    }

    public client.axis.NodeWebServiceImpl getNodeService() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(NodeService_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getNodeService(endpoint);
    }

    public client.axis.NodeWebServiceImpl getNodeService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            client.axis.NodeServiceSoapBindingStub _stub = new client.axis.NodeServiceSoapBindingStub(portAddress, this);
            _stub.setPortName(getNodeServiceWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setNodeServiceEndpointAddress(java.lang.String address) {
        NodeService_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (client.axis.NodeWebServiceImpl.class.isAssignableFrom(serviceEndpointInterface)) {
                client.axis.NodeServiceSoapBindingStub _stub = new client.axis.NodeServiceSoapBindingStub(new java.net.URL(NodeService_address), this);
                _stub.setPortName(getNodeServiceWSDDServiceName());
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
        if ("NodeService".equals(inputPortName)) {
            return getNodeService();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://node.webservice.repo.alfresco.org", "NodeService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://node.webservice.repo.alfresco.org", "NodeService"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("NodeService".equals(portName)) {
            setNodeServiceEndpointAddress(address);
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

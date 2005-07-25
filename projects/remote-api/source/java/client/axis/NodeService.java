/**
 * NodeService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package client.axis;

public interface NodeService extends javax.xml.rpc.Service {
    public java.lang.String getNodeServiceAddress();

    public client.axis.NodeWebServiceImpl getNodeService() throws javax.xml.rpc.ServiceException;

    public client.axis.NodeWebServiceImpl getNodeService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}

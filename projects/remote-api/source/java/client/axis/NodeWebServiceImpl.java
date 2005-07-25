/**
 * NodeWebServiceImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package client.axis;

public interface NodeWebServiceImpl extends java.rmi.Remote {
    public client.axis.QueryResult getChildren(client.axis.StoreRef storeRef, java.lang.String id) throws java.rmi.RemoteException;
    public client.axis.NodeRef getRootNode(client.axis.StoreRef storeRef) throws java.rmi.RemoteException;
    public void setNodeService(java.lang.Object nodeService) throws java.rmi.RemoteException;
    public client.axis.StoreRef createStore(java.lang.String protocol, java.lang.String identifier) throws java.rmi.RemoteException;
    public boolean storeExists(client.axis.StoreRef storeRef) throws java.rmi.RemoteException;
}

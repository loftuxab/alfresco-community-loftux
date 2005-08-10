/**
 * RepositoryServiceSoapPort.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.alfresco.example.webservice.repository;

public interface RepositoryServiceSoapPort extends java.rmi.Remote {
    public org.alfresco.example.webservice.types.Store[] getStores() throws java.rmi.RemoteException, org.alfresco.example.webservice.repository.RepositoryFault;
    public org.alfresco.example.webservice.repository.QueryResult query(org.alfresco.example.webservice.types.Store store, org.alfresco.example.webservice.types.Query query, boolean includeMetaData) throws java.rmi.RemoteException, org.alfresco.example.webservice.repository.RepositoryFault;
}

/**
 * RepositoryService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package org.alfresco.webservice.repository;

public interface RepositoryService extends javax.xml.rpc.Service {

/**
 * Provides read and write operations against a repository.
 */
    public java.lang.String getRepositoryServiceAddress();

    public org.alfresco.webservice.repository.RepositoryServiceSoapPort getRepositoryService() throws javax.xml.rpc.ServiceException;

    public org.alfresco.webservice.repository.RepositoryServiceSoapPort getRepositoryService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}

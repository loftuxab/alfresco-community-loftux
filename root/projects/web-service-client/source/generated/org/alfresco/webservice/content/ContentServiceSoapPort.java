/**
 * ContentServiceSoapPort.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package org.alfresco.webservice.content;

public interface ContentServiceSoapPort extends java.rmi.Remote {

    /**
     * Retrieves content from the repository.
     */
    public org.alfresco.webservice.content.Content[] read(org.alfresco.webservice.types.Predicate items, java.lang.String property) throws java.rmi.RemoteException, org.alfresco.webservice.content.ContentFault;

    /**
     * Writes content to the repository.
     */
    public org.alfresco.webservice.content.Content write(org.alfresco.webservice.types.Reference node, java.lang.String property, byte[] content, org.alfresco.webservice.types.ContentFormat format) throws java.rmi.RemoteException, org.alfresco.webservice.content.ContentFault;

    /**
     * Clears content from the repository.
     */
    public org.alfresco.webservice.content.Content[] clear(org.alfresco.webservice.types.Predicate items, java.lang.String property) throws java.rmi.RemoteException, org.alfresco.webservice.content.ContentFault;
}

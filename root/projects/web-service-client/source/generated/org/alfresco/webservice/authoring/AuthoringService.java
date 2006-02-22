/**
 * AuthoringService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package org.alfresco.webservice.authoring;

public interface AuthoringService extends javax.xml.rpc.Service {

/**
 * Provides support for collaborative editing of content.
 */
    public java.lang.String getAuthoringServiceAddress();

    public org.alfresco.webservice.authoring.AuthoringServiceSoapPort getAuthoringService() throws javax.xml.rpc.ServiceException;

    public org.alfresco.webservice.authoring.AuthoringServiceSoapPort getAuthoringService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}

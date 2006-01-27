/**
 * AdministrationService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.alfresco.webservice.administration;

public interface AdministrationService extends javax.xml.rpc.Service {

/**
 * Administration service.
 */
    public java.lang.String getAdministrationServiceAddress();

    public org.alfresco.webservice.administration.AdministrationServiceSoapPort getAdministrationService() throws javax.xml.rpc.ServiceException;

    public org.alfresco.webservice.administration.AdministrationServiceSoapPort getAdministrationService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}

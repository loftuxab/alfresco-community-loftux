/**
 * AdministrationService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
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

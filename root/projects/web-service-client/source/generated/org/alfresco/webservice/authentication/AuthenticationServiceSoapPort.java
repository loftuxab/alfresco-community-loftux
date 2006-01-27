/**
 * AuthenticationServiceSoapPort.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.alfresco.webservice.authentication;

public interface AuthenticationServiceSoapPort extends java.rmi.Remote {
    public org.alfresco.webservice.authentication.AuthenticationResult startSession(java.lang.String username, java.lang.String password) throws java.rmi.RemoteException, org.alfresco.webservice.authentication.AuthenticationFault;
    public void endSession(java.lang.String ticket) throws java.rmi.RemoteException, org.alfresco.webservice.authentication.AuthenticationFault;
}

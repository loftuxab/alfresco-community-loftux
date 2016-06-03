package org.alfresco.module.vti.handler.alfresco;

/**
 * Implementations are able to retrieve the local machine's host name.
 * 
 * @author Matt Ward
 */
public interface LocalHostNameProvider
{
    String getLocalName();
    String subsituteHost(String hostName);
}

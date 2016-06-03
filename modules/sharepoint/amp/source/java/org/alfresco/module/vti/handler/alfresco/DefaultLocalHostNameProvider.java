package org.alfresco.module.vti.handler.alfresco;

import java.net.InetAddress;

/**
 * Default implementation of {@link LocalHostNameProvider}.
 * 
 * @author Matt Ward
 */
public class DefaultLocalHostNameProvider implements LocalHostNameProvider
{
    /** Token name to substitute current servers DNS name or TCP/IP address into a host name **/
    private static final String TOKEN_LOCAL_NAME = "${localname}";
    private String localName;
    
    public DefaultLocalHostNameProvider()
    {
        String srvName = "localhost";
        try
        {
            srvName = InetAddress.getLocalHost().getHostName();
        }
        catch (Exception ex)
        {
            srvName = "localhost";
        }
        localName = srvName;
    }
    
    @Override
    public String getLocalName()
    {
        return localName;
    }
    
    /**
     * Expands the special ${localname} token within a host name using the resolved DNS name for the local host.
     * 
     * @param hostName
     *            the host name
     * @return the string
     */
    @Override
    public String subsituteHost(String hostName)
    {
        return hostName.replace(TOKEN_LOCAL_NAME, localName);
    }
}

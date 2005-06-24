package org.alfresco.filesys.server.auth.acl;

import org.alfresco.config.ConfigElement;
import org.alfresco.filesys.util.IPAddress;

/**
 * Ip Address Access Control Parser Class
 */
public class IpAddressAccessControlParser extends AccessControlParser
{

    /**
     * Default constructor
     */
    public IpAddressAccessControlParser()
    {
    }

    /**
     * Return the parser type
     * 
     * @return String
     */
    public String getType()
    {
        return "address";
    }

    /**
     * Validate the parameters and create an address access control
     * 
     * @param params ConfigElement
     * @return AccessControl
     * @throws ACLParseException
     */
    public AccessControl createAccessControl(ConfigElement params) throws ACLParseException
    {

        // Get the access type

        int access = parseAccessType(params);

        // Check if the single IP address format has been specified

        String ipAddr = params.getAttribute("ip");
        if (ipAddr != null)
        {

            // Validate the parameters

            if (ipAddr.length() == 0 || IPAddress.isNumericAddress(ipAddr) == false)
                throw new ACLParseException("Invalid IP address, " + ipAddr);

            if (params.getAttributeCount() != 2)
                throw new ACLParseException("Invalid parameter(s) specified for address");

            // Create a single TCP/IP address access control rule

            return new IpAddressAccessControl(ipAddr, null, getType(), access);
        }

        // Check if a subnet address and mask have been specified

        String subnet = params.getAttribute("subnet");
        if (subnet != null)
        {

            // Get the network mask parameter

            String netmask = params.getAttribute("mask");

            // Validate the parameters

            if (subnet.length() == 0 || netmask == null || netmask.length() == 0)
                throw new ACLParseException("Invalid subnet/mask parameter");

            if (IPAddress.isNumericAddress(subnet) == false)
                throw new ACLParseException("Invalid subnet parameter, " + subnet);

            if (IPAddress.isNumericAddress(netmask) == false)
                throw new ACLParseException("Invalid mask parameter, " + netmask);

            // Create a subnet address access control rule

            return new IpAddressAccessControl(subnet, netmask, getType(), access);
        }

        // Invalid parameters

        throw new ACLParseException("Unknown address parameter(s)");
    }
}

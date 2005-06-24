package org.alfresco.filesys.server.auth.acl;

import org.alfresco.config.ConfigElement;

/**
 * Domain Name Access Control Parser Class
 */
public class DomainAccessControlParser extends AccessControlParser
{

    /**
     * Default constructor
     */
    public DomainAccessControlParser()
    {
    }

    /**
     * Return the parser type
     * 
     * @return String
     */
    public String getType()
    {
        return "domain";
    }

    /**
     * Validate the parameters and create a user access control
     * 
     * @param params ConfigElement
     * @return AccessControl
     * @throws ACLParseException
     */
    public AccessControl createAccessControl(ConfigElement params) throws ACLParseException
    {

        // Get the access type

        int access = parseAccessType(params);

        // Get the domain name to check for

        String domainName = params.getAttribute("name");
        if (domainName == null || domainName.length() == 0)
            throw new ACLParseException("Domain name not specified");

        // Create the domain access control

        return new DomainAccessControl(domainName, getType(), access);
    }
}

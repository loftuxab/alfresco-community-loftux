package org.alfresco.filesys.server.auth.acl;

import org.alfresco.config.ConfigElement;

/**
 * Protocol Access Control Parser Class
 */
public class ProtocolAccessControlParser extends AccessControlParser
{
    /**
     * Default constructor
     */
    public ProtocolAccessControlParser()
    {
    }

    /**
     * Return the parser type
     * 
     * @return String
     */
    public String getType()
    {
        return "protocol";
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

        // Get the list of protocols to check for

        String protos = params.getAttribute("type");
        if (protos == null || protos.length() == 0)
            throw new ACLParseException("Protocol type not specified");

        // Validate the protocol list

        if (ProtocolAccessControl.validateProtocolList(protos) == false)
            throw new ACLParseException("Invalid protocol type");

        // Create the protocol access control

        return new ProtocolAccessControl(protos, getType(), access);
    }
}

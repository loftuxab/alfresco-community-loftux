package org.alfresco.filesys.server.auth.acl;

import org.alfresco.config.ConfigElement;

/**
 * User Access Control Parser Class
 */
public class UserAccessControlParser extends AccessControlParser
{
    /**
     * Default constructor
     */
    public UserAccessControlParser()
    {
    }

    /**
     * Return the parser type
     * 
     * @return String
     */
    public String getType()
    {
        return "user";
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

        // Get the user name to check for

        String userName = params.getAttribute("name");
        if (userName == null || userName.length() == 0)
            throw new ACLParseException("User name not specified");

        // Create the user access control

        return new UserAccessControl(userName, getType(), access);
    }
}

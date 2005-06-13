/**
 * Created on Apr 13, 2005
 */
package org.alfresco.repo.version.common;

import java.util.Collection;

import org.alfresco.service.cmr.version.ReservedVersionNameException;
import org.alfresco.service.cmr.version.Version;

/**
 * Helper class containing helper methods for the versioning services.
 * 
 * @author Roy Wetherall
 */
public class VersionUtil
{
    /**
     * Reserved property names
     */
    public static final String[] RESERVED_PROPERTY_NAMES = new String[]{
        Version.PROP_CREATED_DATE, 
        Version.PROP_FROZEN_NODE_ID, 
        Version.PROP_FROZEN_NODE_STORE_ID, 
        Version.PROP_FROZEN_NODE_STORE_PROTOCOL,
        Version.PROP_FROZEN_NODE_TYPE,
        Version.PROP_FROZEN_ASPECTS,
        Version.PROP_VERSION_LABEL,
        Version.PROP_VERSION_NUMBER};
    
    /**
     * Checks that the names of the additional version properties are valid and that they do not clash
     * with the reserved properties.
     * 
     * @param versionProperties  the property names 
     * @return                   true is the names are considered valid, false otherwise
     * @throws                   ReservedVersionNameException
     */
    public static void checkVersionPropertyNames(Collection<String> names)
        throws ReservedVersionNameException
    {
        for (String name : RESERVED_PROPERTY_NAMES)
        {
            if (names.contains(name) == true)
            {
                throw new ReservedVersionNameException(name);
            }
        }
    }
}

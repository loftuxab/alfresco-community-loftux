/**
 * Created on Apr 4, 2005
 */
package org.alfresco.repo.version;

import java.io.Serializable;
import java.util.Map;

/**
 * Version label policy interface.
 * 
 * @author Roy Wetherall
 */
public interface VersionLabelPolicy
{
    /**
     * Get the version label value base on the data provided.
     * 
     * @param preceedingVersion  the preceeding version, null if none
     * @param versionNumber      the new version number 
     * @param versionProperties  the version property values
     * @return                   the version label
     */
    public String getVersionLabelValue(
            Version preceedingVersion, 
            int versionNumber, 
            Map<String, Serializable> versionProperties);
}

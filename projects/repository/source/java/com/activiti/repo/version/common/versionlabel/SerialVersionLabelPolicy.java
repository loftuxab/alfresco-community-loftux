/**
 * Created on Apr 4, 2005
 */
package com.activiti.repo.version.common.versionlabel;

import java.util.Map;

import com.activiti.repo.version.Version;
import com.activiti.repo.version.VersionLabelPolicy;

/**
 * The serial version label policy.
 * 
 * @author Roy Wetherall
 */
public class SerialVersionLabelPolicy implements VersionLabelPolicy
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
            Map<String, String> versionProperties)
    {
        throw new UnsupportedOperationException();
    }
}

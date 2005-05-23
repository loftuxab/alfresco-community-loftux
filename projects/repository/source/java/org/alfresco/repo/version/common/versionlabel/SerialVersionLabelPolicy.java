/**
 * Created on Apr 4, 2005
 */
package org.alfresco.repo.version.common.versionlabel;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.repo.dictionary.ClassRef;
import org.alfresco.repo.version.Version;
import org.alfresco.repo.version.VersionType;

/**
 * The serial version label policy.
 * 
 * @author Roy Wetherall
 */
public class SerialVersionLabelPolicy 
{
    // TODO need to add support for branches into this labeling policy
    
    /**
     * Get the version label value base on the data provided.
     * 
     * @param preceedingVersion  the preceeding version, null if none
     * @param versionNumber      the new version number 
     * @param versionProperties  the version property values
     * @return                   the version label
     */
    public String calculateVersionLabel(
			ClassRef classRef,
            Version preceedingVersion, 
            int versionNumber, 
            Map<String, Serializable> versionProperties)
    {
        SerialVersionLabel serialVersionNumber = null;
        
        if (preceedingVersion != null)
        {
            serialVersionNumber = new SerialVersionLabel(preceedingVersion.getVersionLabel());
            
            VersionType versionType = (VersionType)versionProperties.get(Version.PROP_VERSION_TYPE);
            if (VersionType.MAJOR.equals(versionType) == true)
            {
                serialVersionNumber.majorIncrement();
            }
            else
            {
                serialVersionNumber.minorIncrement();
            }
        }
        else
        {
            serialVersionNumber = new SerialVersionLabel(null);
        }
        
        return serialVersionNumber.toString();
    }
    
    /**
     * Inner class encapsulating the notion of the serial version number.
     * 
     * @author Roy Wetherall
     */
    private class SerialVersionLabel
    {
        /**
         * The version number delimiter
         */
        private static final String DELIMITER = ".";
        
        /**
         * The major revision number
         */
        private int majorRevisionNumber = 1;
        
        /**
         * The minor revision number
         */
        private int minorRevisionNumber = 0;        
        
        /**
         * Constructor
         * 
         * @param version  the vesion to take the version from
         */
        public SerialVersionLabel(String versionLabel)
        {
            if (versionLabel != null && versionLabel.length() != 0)
            {
                int iIndex = versionLabel.indexOf(DELIMITER);
                String majorString = versionLabel.substring(0, iIndex);
                String minorString = versionLabel.substring(iIndex+1);
                
                this.majorRevisionNumber = Integer.parseInt(majorString);
                this.minorRevisionNumber = Integer.parseInt(minorString);
            }
        }
        
        /**
         * Increments the major revision numebr and sets the minor to 
         * zero.
         */
        public void majorIncrement()
        {
            this.majorRevisionNumber += 1;
            this.minorRevisionNumber = 0;
        }
        
        /**
         * Increments only the minor revision number
         */
        public void minorIncrement()
        {
            this.minorRevisionNumber += 1;
        }
        
        /**
         * Converts the serial version number into a string
         */
        public String toString()
        {
            return this.majorRevisionNumber + DELIMITER + this.minorRevisionNumber;
        }
    }
}

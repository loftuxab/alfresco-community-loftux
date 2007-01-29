/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.util;

import org.alfresco.error.AlfrescoRuntimeException;

/**
 * Class to encapsulate a version number string.
 * 
 * A valid version number string can be made up of any number of numberical parts 
 * all delimited by '.'.
 * 
 * @author Roy Wetherall
 */
public class VersionNumber implements Comparable
{
    /** Version delimeter */
    private static final String DELIMITER = "\\.";
    
    /** Version parts */
    private int[] parts;
                
    /**
     * Constructror, expects a valid version string.
     * 
     * A AlfrescoRuntimeException will be throw if an invalid version is encountered.
     * 
     * @param version   the version string
     */
    public VersionNumber(String version)
    {
        // Split the version into its component parts
        String[] versions = version.split(DELIMITER);
        if (versions.length < 1)
        {
            throw new AlfrescoRuntimeException("The extension version string '" + version + "' is invalid.");
        }
        
        try
        {
            // Set the parts of the version
            int index = 0;
            this.parts = new int[versions.length];
            for (String versionPart : versions)
            {
                int part = Integer.parseInt(versionPart);
                this.parts[index] = part;
                index++;
            }
        }
        catch (NumberFormatException e)
        {
            throw new AlfrescoRuntimeException("The extension version string '" + version + "' is invalid.");   
        }
    }
    
    /**
     * Get the various parts of the version
     * 
     * @return  array containing the parts of the version
     */
    public int[] getParts()
    {
        return this.parts;
    }
    
    
    
    /**
     * Compares the passed version to this.  Determines whether they are equal, greater or less than this version.
     * 
     * @param that  the other version
     * @return  -1 if the passed version is less that this, 0 if they are equal, 1 if the passed version is greater
     */
    public int compareTo(Object obj)
    {
        int result = 0;

        VersionNumber that = (VersionNumber)obj;
        int length = 0;
        if (this.parts.length > that.parts.length)
        {
            length = this.parts.length;
        }
        else
        {
            length = that.parts.length;
        }
        
        for (int index = 0; index < length; index++)
        {
            int thisPart = this.getPart(index);
            int thatPart = that.getPart(index);
            
            if (thisPart > thatPart)
            {
                result = 1;
                break;
            }
            else if (thisPart < thatPart)
            {
                result = -1;
                break;
            }
        }
        
        return result;
    }
    
    /**
     * Helper method to the the part based on the index, if an invalid index is supplied 0 is returned.
     * 
     * @param index     the index
     * @return          the part value, 0 if the index is invalid
     */
    private int getPart(int index)
    {
        int result = 0;
        if (index < this.parts.length)
        {
            result = this.parts[index];
        }
        return result;
    }
    
    /**
     * Hash code implementation
     */
    @Override
    public int hashCode()
    {
        return this.parts.hashCode(); 
    }
    
    /**
     * Equals implementation
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj instanceof VersionNumber)
        {
            if (this.compareTo((VersionNumber)obj) == 0)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }
}

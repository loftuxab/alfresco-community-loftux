/*
 * Copyright 2005-2015 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.officeservices.vfs;

public class VersionNumber
{

    public final static char VERSION_SEPARATOR = '.';
    
    public final static VersionNumber INITIAL = new VersionNumber(1,0);
    
    private int major;

    private int minor;
    
    public VersionNumber(int major, int minor)
    {
        this.major = major;
        this.minor = minor;
    }
    
    public int getMajor()
    {
        return major;
    }

    public int getMinor()
    {
        return minor;
    }
    
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(Integer.toString(major));
        sb.append(VERSION_SEPARATOR);
        sb.append(Integer.toString(minor));
        return sb.toString();
    }
    
    public VersionNumber getNextMajor()
    {
        return new VersionNumber(major+1, 0);
    }
    
    public VersionNumber getNextMinor()
    {
        return new VersionNumber(major, minor+1);
    }

    public int compareTo(VersionNumber v2)
    {
        if(this.major < v2.major)
        {
            return -1;
        }
        if(this.major > v2.major)
        {
            return +1;
        }
        if(this.minor < v2.minor)
        {
            return -1;
        }
        if(this.minor > v2.minor)
        {
            return +1;
        }
        return 0;
    }
    
    public static VersionNumber max(VersionNumber v1, VersionNumber v2)
    {
        return v1.compareTo(v2) >= 0 ? v1 : v2;
    }
    
    public static VersionNumber min(VersionNumber v1, VersionNumber v2)
    {
        return v1.compareTo(v2) <= 0 ? v1 : v2;
    }

    public static VersionNumber parseSafe(String s)
    {
        if(s == null)
        {
            return INITIAL;
        }
        int pos = s.indexOf(VERSION_SEPARATOR);
        if(pos >= 0)
        {
            try
            {
                String majorPart = s.substring(0, pos);
                String minorPart = s.substring(pos+1);
                return new VersionNumber(Integer.parseInt(majorPart), Integer.parseInt(minorPart));
            }
            catch(StringIndexOutOfBoundsException | NumberFormatException e)
            {
                return INITIAL;
            }
        }
        try
        {
            return new VersionNumber(Integer.parseInt(s), 0);
        }
        catch(NumberFormatException e)
        {
            return INITIAL;
        }
    }
    
}

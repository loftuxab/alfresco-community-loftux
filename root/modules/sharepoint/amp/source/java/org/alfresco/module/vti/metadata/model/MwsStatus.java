
package org.alfresco.module.vti.metadata.model;

/**
 * @author PavelYur
 *
 */
public class MwsStatus
{
    private boolean uniquePermissions;
    
    private int meetingCount;
    
    private boolean anonymousAccess;
    
    private boolean allowAuthenticatedUsers;
    
    public void setUniquePermissions(boolean uniquePermissions)
    {
        this.uniquePermissions = uniquePermissions;
    }
    
    public boolean isUniquePermissions()
    {
        return uniquePermissions;
    }
    
    public void setMeetingCount(int meetingCount)
    {
        this.meetingCount = meetingCount;
    }
    
    public int getMeetingCount()
    {
        return meetingCount;
    }
    
    public void setAnonymousAccess(boolean anonymousAccess)
    {
        this.anonymousAccess = anonymousAccess;
    }
    
    public boolean isAnonymousAccess()
    {
        return anonymousAccess;
    }
    
    public void setAllowAuthenticatedUsers(boolean allowAuthenticatedUsers)
    {
        this.allowAuthenticatedUsers = allowAuthenticatedUsers;
    }
    
    public boolean isAllowAuthenticatedUsers()
    {
        return allowAuthenticatedUsers;
    }
    
    public static MwsStatus getDefault()
    {
        MwsStatus result = new MwsStatus();
        
        result.setUniquePermissions(true);
        result.setMeetingCount(0);
        result.setAnonymousAccess(false);
        result.setAllowAuthenticatedUsers(false);
        
        return result;
    }

}
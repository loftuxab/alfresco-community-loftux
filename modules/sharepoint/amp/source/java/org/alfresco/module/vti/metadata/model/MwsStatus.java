/*
 * #%L
 * Alfresco Sharepoint Protocol
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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
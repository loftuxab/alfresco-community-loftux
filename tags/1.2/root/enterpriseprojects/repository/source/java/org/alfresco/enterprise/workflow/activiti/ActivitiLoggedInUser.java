/*
 * Copyright 2005-2011 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.workflow.activiti;

import java.util.List;

import org.activiti.engine.identity.Group;
import org.activiti.explorer.identity.LoggedInUser;

/**
 * Logged in user for Activiti admin ui, based on the authenticated person node
 * properties.
 * 
 * @author Frederik Heremans
 * @author Gavin Cornwell
 * @since 4.0
 */
public class ActivitiLoggedInUser implements LoggedInUser
{
    private static final long serialVersionUID = 1L;

    private String id;

    private String firstName;

    private String lastName;
    
    private String ticket;

    private boolean admin;

    private boolean user;

    public ActivitiLoggedInUser(String id, String ticket)
    {
        this.id = id;
        this.ticket = ticket;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public String getFullName()
    {
        return getFirstName() + " " + getLastName();
    }

    public String getId()
    {
        return id;
    }

    public String getLastName()
    {
        return lastName;
    }

    public String getPassword()
    {
        // Password is not exposed, not needed anymore after authentication
        return null;
    }
    
    public String getTicket()
    {
        return ticket;
    }

    public boolean isAdmin()
    {
        return admin;
    }

    public boolean isUser()
    {
        return user;
    }
    
    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }
    
    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }
    
    public void setAdmin(boolean admin)
    {
        this.admin = admin;
    }
    
    public void setUser(boolean user)
    {
        this.user = user;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder(super.toString());
        builder.append(", username: ").append(id);
        builder.append(", ticket: ").append(ticket);
        
        return builder.toString();
    }

    @Override
    public List<Group> getGroups()
    {
        return null;
    }

    @Override
    public List<Group> getSecurityRoles()
    {
        return null;
    }
}

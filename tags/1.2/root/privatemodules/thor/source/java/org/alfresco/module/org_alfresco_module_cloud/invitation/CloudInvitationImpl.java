/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
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
 */
package org.alfresco.module.org_alfresco_module_cloud.invitation;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.alfresco.service.cmr.repository.NodeRef;

public class CloudInvitationImpl implements CloudInvitation
{
    private final String id;
    private final String key;
    
    private Map<String, Serializable> inviterProperties = Collections.emptyMap();
    private Map<String, Serializable> inviteeProperties = Collections.emptyMap();
    
    private String inviterEmail;
    private String inviterFirstName;
    private String inviterLastName;
    private String inviterPassword;
    
    private String inviteeEmail;
    private String inviteeFirstName;
    private String inviteeLastName;
    private String inviteePassword;
    private String inviteeRole;
    private NodeRef inviteeAvatarNodeRef;
    private Boolean inviteeIsActivated;
    private Boolean inviteeIsMember;
    
    private String siteShortName;
    private String siteTitle;
    
    private String siteTenantId;
    private String siteTenantTitle;
    
    private String response;
    private Date startDate;
    
    public CloudInvitationImpl(String workflowId, String key)
    {
        this.id = workflowId;
        this.key = key;
    }
    
    // No setter for ID.
    
    @Override public String getSiteTitle() { return this.siteTitle; }
    public void setSiteTitle(String siteTitle) {this.siteTitle = siteTitle; }

    @Override public String getId()
    {
        return id;
    }
    @Override public String getKey()
    {
        return key;
    }
    public void setSiteShortName(String siteShortName)
    {
        this.siteShortName = siteShortName;
    }
    @Override public String getSiteShortName()
    {
        return siteShortName;
    }
    public void setResponse(String response)
    {
        this.response = response;
    }
    @Override public String getResponse()
    {
        return response;
    }
    public void setInviterEmail(String inviterEmail)
    {
        this.inviterEmail = inviterEmail;
    }
    @Override public String getInviterEmail()
    {
        return inviterEmail;
    }
    public void setInviteeEmail(String inviteeEmail)
    {
        this.inviteeEmail = inviteeEmail;
    }
    @Override public String getInviteeEmail()
    {
        return inviteeEmail;
    }
    @Override public String getInviteeFirstName()
    {
        return inviteeFirstName;
    }
    public void setInviteeFirstName(String inviteeFirstName)
    {
        this.inviteeFirstName = inviteeFirstName;
    }
    @Override public String getInviteeLastName()
    {
        return inviteeLastName;
    }
    public void setInviteeLastName(String inviteeLastName)
    {
        this.inviteeLastName = inviteeLastName;
    }
    @Override public String getInviteePassword()
    {
        return inviteePassword;
    }
    public void setInviteePassword(String inviteePassword)
    {
        this.inviteePassword = inviteePassword;
    }
    @Override public String getSiteTenantId()
    {
        return siteTenantId;
    }
    public void setSiteTenantId(String siteTenantId)
    {
        this.siteTenantId = siteTenantId;
    }
    @Override public Boolean getInviteeIsActivated()
    {
        return inviteeIsActivated;
    }
    public void setInviteeIsActivated(Boolean inviteeIsActivated)
    {
        this.inviteeIsActivated = inviteeIsActivated;
    }
    @Override public Boolean getInviteeIsMember()
    {
        return inviteeIsMember;
    }
    public void setInviteeIsMember(Boolean inviteeIsMember)
    {
        this.inviteeIsMember = inviteeIsMember;
    }
    @Override public String getInviterFirstName()
    {
        return inviterFirstName;
    }
    public void setInviterFirstName(String inviterFirstName)
    {
        this.inviterFirstName = inviterFirstName;
    }
    @Override public String getInviterLastName()
    {
        return inviterLastName;
    }
    public void setInviterLastName(String inviterLastName)
    {
        this.inviterLastName = inviterLastName;
    }
    @Override public String getInviterPassword()
    {
        return inviterPassword;
    }
    public void setInviterPassword(String inviterPassword)
    {
        this.inviterPassword = inviterPassword;
    }
    @Override public String getSiteTenantTitle()
    {
        return this.siteTenantTitle;
    }
    public void setSiteTenantTitle(String siteTenantTitle)
    {
        this.siteTenantTitle = siteTenantTitle;
    }
    @Override public String getInviteeRole()
    {
        return inviteeRole;
    }
    public void setInviteeRole(String inviteeRole)
    {
        this.inviteeRole = inviteeRole;
    }
    
    public Map<String, Serializable> getInviterProperties()
    {
        return Collections.unmodifiableMap(this.inviterProperties);
    }
    
    public Map<String, Serializable> getInviteeProperties()
    {
        return Collections.unmodifiableMap(this.inviteeProperties);
    }
    
    public void setInviterProperties(Map<String, Serializable> props)
    {
        this.inviterProperties = props;
    }
    public void setInviteeProperties(Map<String, Serializable> props)
    {
        this.inviteeProperties = props;
    }
    
    public NodeRef getInviteeAvatarNode()
    {
        return this.inviteeAvatarNodeRef;
    }
    
    public void setInviteeAvatarNode(NodeRef avatarNodeRef)
    {
        this.inviteeAvatarNodeRef = avatarNodeRef;
    }
    
    public Date getStartDate()
    {
        return this.startDate;
    }
    
    public void setStartDate(Date date)
    {
        this.startDate = date;
    }
}
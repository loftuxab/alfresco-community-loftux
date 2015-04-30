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
import java.util.Date;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_cloud.accounts.Account;
import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationService;

/**
 * This simple POJO is mostly used to pass data back to the FTL model. It represents an ongoing invitation
 */
public interface CloudInvitation
{
    /**
     * Gets the workflow id for an ongoing invitation.
     */
    String getId();
    
    /**
     * Gets the unique key associated with this workflow.
     */
    String getKey();
    
    /**
     * Gets the start date for the invitation task.
     */
    Date getStartDate();
    
    /**
     * Gets the short name of the site to which the invitee is invited.
     * Note that in a multi-tenanted world, this does not uniquely identify the site.
     */
    String getSiteShortName();
    
    /**
     * Gets the title of the site to which the invitee is invited.
     */
    String getSiteTitle();
    
    /**
     * Gets the tenant id of the tenant holding the site.
     */
    String getSiteTenantId();
    
    /**
     * Gets the tenant title of the tenant holding the site.
     * Tenants currently don't have titles. This method returns the name of the Account associated with that tenant.
     * @see Account#getName()
     */
    String getSiteTenantTitle();
    
    /**
     * Gets the email/username of the inviter.
     */
    String getInviterEmail();
    
    String getInviterFirstName();
    String getInviterLastName();
    String getInviterPassword();
    
    /**
     * Gets a map of the inviter's cm:person node properties (in the current tenant).
     */
    Map<String, Serializable> getInviterProperties();
    
    /**
     * Gets a map of the invitee's cm:person node properties (in the current tenant).
     */
    Map<String, Serializable> getInviteeProperties();
    
    /**
     * Gets the email/username of the invitee.
     */
    String getInviteeEmail();
    
    String getInviteeFirstName();
    String getInviteeLastName();
    String getInviteePassword();
    
    String getInviteeRole();
    
    /**
     * Gets whether the invitee is an activated user or not. The result reflects the value set on the POJO
     * and may not be current when this method is called. The value may be null.
     * @see RegistrationService#isActivatedEmailAddress(String)
     */
    Boolean getInviteeIsActivated();
    
    /**
     * Gets whether the invitee is currently a member of the tenant to which they are invited.
     * This would be true if a user in tenant A was invited to access a new private site in tenant A.
     * @see RegistrationService#isActivatedEmailAddress(String)
     */
    Boolean getInviteeIsMember();
    
    /**
     * Gets the response value the invitee gave to the invitation, if any.
     * TODO It should be possible to remove this from the Java API and the FTL. It will always be null for pending invitations.
     */
    String getResponse();
    
    public static final String RESPONSE_ACCEPT = WorkflowModelCloudInvitation.WF_INVITATION_OUTCOME_ACCEPT;
    public static final String RESPONSE_REJECT = WorkflowModelCloudInvitation.WF_INVITATION_OUTCOME_REJECT;
    public static final String RESPONSE_CANCEL = WorkflowModelCloudInvitation.WF_INVITATION_OUTCOME_CANCEL;
}

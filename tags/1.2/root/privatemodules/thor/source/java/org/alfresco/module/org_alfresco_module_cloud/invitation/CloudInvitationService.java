/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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

import java.util.List;

import org.alfresco.repo.site.SiteModel;

/**
 * This service is responsible for for managing site invitations in the cloud.
 * 
 * @since Thor Module
 */
public interface CloudInvitationService
{
    /**
     * Starts an invitation workflow.
     * 
     * @param inviterEmail the email address of the user who is issuing the invitation.
     * @param inviteeEmail the email address of the user (or potential user) who is to receive the invitation.
     * @param inviteeRole the {@link SiteModel#STANDARD_PERMISSIONS Share role} with which the invitee is to be invited.
     * @param siteShortName the short name of the site to which the invitee is invited.
     * @param inviterMessage a short message from the inviter to the invitee.
     */
    CloudInvitation startInvitation(String inviterEmail, String inviteeEmail, String inviteeRole, String siteShortName, String inviterMessage);
    
    /**
     * This method triggers the acceptance of an invitation using the email address provided by the inviter.
     * The user must already exist as a user of the system.
     * 
     * @param workflowId the workflow id for this invitation.
     * @param key the invitation key/ unique id.
     */
    CloudInvitation acceptInvitation(String workflowId, String key);
    
    /**
     * This method triggers the acceptance of an invitation using the email address provided by the inviter.
     * The invitee must not exist as a user of the system.
     * 
     * @param workflowId the workflow id for this invitation.
     * @param key the invitation key/ unique id.
     * @param inviteeFirstName the invitee's first name.
     * @param inviteeLastName the invitee's last name.
     * @param inviteePassword the invitee's password.
     */
    CloudInvitation acceptInvitationWithSignup(String workflowId, String key, String inviteeFirstName, String inviteeLastName, String inviteePassword);
    
    /**
     * This method triggers the acceptance of an invitation using an alternative email address supplied by the invitee.
     * This alternative email address must be an activated email address within the system.
     * 
     * @param workflowId the workflow id for this invitation.
     * @param key the invitation key/ unique id.
     * @throws InvalidInvitationAcceptanceException if the alternativeEmail does not represent an existing user in the system.
     * 
     * @since Thor Phase 2 Sprint 1
     */
    CloudInvitation acceptInvitationAtAlternativeEmail(String workflowId, String key, String alternativeEmail);
    
    /**
     * This method triggers the rejection of an invitation.
     * 
     * @param workflowId the workflow id for this invitation.
     * @param key the invitation key/ unique id.
     */
    CloudInvitation rejectInvitation(String workflowId, String key);
    
    /**
     * This method triggers the cancellation of an invitation.
     * 
     * @param workflowId the workflow id for this invitation.
     * @param key the invitation key/ unique id.
     */
    void cancelInvitation(String workflowId, String key);
    
    /**
     * This method retrieves the invitation status
     * 
     * @param workflowId the workflow id for the invitation.
     * @param key the invitation key/unique id.
     */
    CloudInvitation getInvitationStatus(String workflowId, String key);
    
    /**
     * Remind the invitee about the pending site invite manually.
     * 
     * @param workflowId the workflow id for the invitation.
     * @param key the invitation key/unique id.
     */
    void remindInvitee(String workflowId, String key);
    
    /**
     * This method adds the specified user to the site with the given role. If the specified user is not a member of the
     * site domain, then they will be added.
     * 
     * @param inviterEmail the email address of the user who has issued the invitation.
     * @param siteShortName the short name of the site to which the invitee is invited.
     * @param inviteeEmail the email address of the user who has been invited.
     * @param inviteeRole the {@link SiteModel#STANDARD_PERMISSIONS Share role} which the invited user will be given in the specified site.
     */
    void addInviteeToSite(String inviterEmail, final String siteShortName, final String inviteeEmail, final String inviteeRole);
    
    /**
     * This method gets all in-progress invitations for the specified invitee.
     * @param username the username of the invitee. Must not be null.
     * @param a limit for the result size. Non-positive numbers will be treated as 'no limit'.
     */
    List<CloudInvitation> listPendingInvitationsForInvitee(String username, int limit);
    
    /**
     * This method gets all in-progress invitations to the specified site, for the specified invitee.
     * @param siteShortName the site to which the invitations were issued.
     * @param inviteeUserName the username of an invitee or <tt>null</tt> to get all pending invitations to this site.
     * @param a limit for the result size. Non-positive numbers will be treated as 'no limit'.
     */
    List<CloudInvitation> listPendingInvitationsForSite(String siteShortName, String inviteeUserName, int limit);
}

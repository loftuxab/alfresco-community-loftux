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
package org.alfresco.module.org_alfresco_module_cloud.registration;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_cloud.accounts.Account;
import org.alfresco.module.org_alfresco_module_cloud.accounts.exceptions.AccountNotFoundException;
import org.alfresco.module.org_alfresco_module_cloud.analytics.Analytics;
import org.alfresco.module.org_alfresco_module_cloud.directory.DuplicateEmailAddressException;
import org.alfresco.module.org_alfresco_module_cloud.directory.InvalidEmailAddressException;
import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationServiceImpl.InvalidEmail;

/**
 * Registration of new users and provisioning of their accounts.
 * 
 * @since Thor
 */
public interface RegistrationService
{
    /**
     * Bulk register multiple emails, where the sign ups will be marked as initiated by the current user.
     *
     * Note! Will force check of signed up email addresses against the current tenant's domain.
     * 
     * @param emails
     * @param source
     * @param sourceUrl
     * @param message
     * @param optionalAnalyticData
     * @return
     * @throws InvalidEmailAddressException
     */
    public List<InvalidEmail> registerEmails(List<String> emails, String source, String sourceUrl, String message, Map<String, Serializable> optionalAnalyticData)
        throws InvalidEmailAddressException;

    /**
     * Bulk register multiple emails, where the sign ups will be marked as initiated by the current user.
     *
     * @param emails
     * @param source
     * @param sourceUrl
     * @param message
     * @param optionalAnalyticData
     * @param checkSameDomain Will, if set to true check the signed up email addresses against the current tenant's domain.
     * @return
     * @throws InvalidEmailAddressException
     */
    public List<InvalidEmail> registerEmails(List<String> emails, String source, String sourceUrl, String message,
                                             Map<String, Serializable> optionalAnalyticData, boolean checkSameDomain)
        throws InvalidEmailAddressException;

    /**
     * Register an email for a "true" self sign up, where the user himself initiated the sign up.
     *  
     * @param email  email address to sign up with
     * @param source the source of the registration. See {@link Analytics#record_Registration(String, String, String, Long, String, String, String, String, String, String, String, String)} for details.
     * @param sourceUrl if source is "website" then this parameter records the URL of that source e.g. cloud.alfresco.com
     * @return  details of the registration
     */
    Registration registerEmail(String email, String source, String sourceUrl, String message)
        throws InvalidEmailAddressException;
    
    /**
     * Register an email for a "true" self sign up, where the user himself initiated the signup.
     *  
     * @param email  email address to sign up with
     * @param firstName
     * @param lastName
     * @param password
     * @param source the source of the registration. See {@link Analytics#record_Registration} for details.
     * @param sourceUrl if source is "website" then this parameter records the URL of that source e.g. cloud.alfresco.com
     * @param optionalAnalyticData additional analytic data for signup metrics. See {@link Analytics#record_Registration(String, String, String, Long, String, String, String, String, String, String, String, String)} for details.
     * @return  details of the registration
     */
    Registration registerEmail(String email, String firstName, String lastName, String password, String source, String sourceUrl, String message, Map<String, Serializable> optionalAnalyticData) throws InvalidEmailAddressException;

    /**
     * Register an email for a sign up.
     *
     * @param email  email address to sign up with
     * @param firstName The signed up user's first name
     * @param lastName The signed up user's last name
     * @param password The signed up user's password
     * @param source the source of the registration. See {@link Analytics#record_Registration} for details.
     * @param sourceUrl if source is "website" then this parameter records the URL of that source e.g. cloud.alfresco.com
     * @param optionalAnalyticData additional analytic data for signup metrics. See {@link Analytics#record_Registration(String, String, String, Long, String, String, String, String, String, String, String, String)} for details.
     * @param initiatorEmail (Optional) The email of the user that initiated the sign up. Set to null to create a "true" self sign up.
     * @return  details of the registration
     */
    Registration registerEmail(String email, String firstName, String lastName, String password, String source, String sourceUrl, String message, Map<String, Serializable> optionalAnalyticData, String initiatorEmail) throws InvalidEmailAddressException;

    /**
     * Gets the registration (if one exists) for the email
     * 
     * @param email  email address to retrieve registration for
     * @return  registration details (or null, if none exist)
     */
    Registration getRegistration(String email);
    
    /**
     * Is user email address currently on the registered list?
     * 
     * Note: Activated email addresses are removed from the registered list
     *  
     * @param email  the email to check
     * @return  true => email address is on the registered list, and not yet activated
     */
    boolean isRegisteredEmailAddress(String email)
        throws InvalidEmailAddressException;

    /**
     * Is user email address pre-registered i.e. first name, last name and password already provided?
     * 
     * @param id    registration id
     * @param key   registration key
     * @return true if the email has been pre-registered, false otherwise
     */
    boolean isPreRegistered(String id, String key);
//    boolean isPreRegistered(String email) throws InvalidEmailAddressException;
    
    /**
     * Activate a registered sign up, thus creating a user in the system
     * 
     * @param id  registration id
     * @param key  registration key
     * @param firstName  first name of the user
     * @param lastName  last name of the user
     * @param password  user's password
     * @return details of the registration
     */
    Registration activateRegistration(String id, String key, String firstName, String lastName, String password);
    
    /**
     * Gets the email address which is/was being signed up, as identified by the workflow id and key.
     * <p/>
     * If the workflow id is not recognised, or if the key does not match that recorded against the workflow,
     * <tt>null</tt> will be returned.
     * 
     * @param id the workflow id for the signup.
     * @param key the unique key associated with this pariticular signup.
     * @return the email if it is available, else <tt>null</tt>.
     */
    String getEmailSigningUp(String id, String key);
    
    /**
     * Causes the activation request email to be sent again. This method should only be called for registrations
     * which are in the registration queue awaiting activation.
     * 
     * @param id registration id
     * @param key registration key
     * @return details of the registration
     * @throws RegistrationServiceException if the id does not represent an in-progress workflow or if the
     *                                      key does not match the key stored in the workflow or if the workflow
     *                                      is not in the 'activation pending' state.
     * @see #isRegisteredEmailAddress(String)
     */
    Registration resendActivationRequest(String id, String key);
    
    /**
     * Cancel a registration
     * 
     * @param id  registration id
     * @param key  registration key
     */
    void cancelRegistration(String id, String key);
    
    /**
     * Is user email address already activated?
     *
     * @param email  the email to check
     * @return  true => email address is an active user of the system
     */
    boolean isActivatedEmailAddress(String email)
        throws InvalidEmailAddressException;
    
    /**
     * Creates a new user in Alfresco associated to the current account. Returns
     * the NodeRef of the person created or null if an error occurred (such as
     * username already exists)
     *
     * @param email  the email address of the user (this is the user unique id)
     * @param firstName  the first name of the user
     * @param lastName  the last name of the user
     * @param password  a password for the user
     * 
     * @return  home account for user
     */
    Account createUser(String email, String firstName, String lastName, String password)
        throws InvalidEmailAddressException, DuplicateEmailAddressException;

    /**
     * Adds an existing user to an account.
     * 
     * NOTE: The user will be known as an External user to that account.
     * 
     * @param accountId  account to add user to
     * @param email  the user to add
     */
    Account addUser(Long accountId, String email)
        throws AccountNotFoundException, InvalidEmailAddressException;
    
    /**
     * Removes an external user from an account. This will mean that the user no longer has access to the specified
     * secondary account, but the user will still exist in the system and will have access to their home account and any
     * secondary accounts in the normal manner.
     * 
     * @param accountId  account to remove user from
     * @param email      the user to remove
     * @throws CannotRemoveUserException if the user is not an external user in this account.
     */
    void removeExternalUser(Long accountId, String email)
        throws AccountNotFoundException, InvalidEmailAddressException;
    
    /**
     * Deletes a user from the system. This removes the user from each of their secondary accounts, from their home
     * account and the authentication details for that user are removed. The user will no longer exist.
     * 
     * @param email     the user to remove.
     * @throws CannotRemoveUserException if the user is not an internal user in this account.
     * @throws CannotDemoteLastNetworkAdminException if the user is the last NetworkAdmin in this account.
     * @since Thor Phase 2 Sprint 1
     */
    void deleteUser(String email) throws AccountNotFoundException, InvalidEmailAddressException;
    
    /**
     * Deletes a corrupted user from the system.
     */ 
    void deleteSplittedPerson(String corruptedEmail);
    
    /**
     * Promotes the user to a NetworkAdmin for the tenant associated with the specified account.
     * If the user is already a Network Admin, nothing will change.
     * 
     * @param accountId the account id for the tenant on which the user is to be made an admin
     * @param email the email/username of the user being promoted.
     * @throws NoSuchUserException if the user does not exist.
     * @throws IllegalNetworkAdminUserException if the user is not acceptable as a Network Admin (e.g. if they are an external user on this tenant)
     */
    void promoteUserToNetworkAdmin(Long accountId, String email);
    
    /**
     * Demotes the user from the NetworkAdmin position for the tenant associated with the specified account.
     * If the user was not already a NetworkAdmin, nothing will change.
     * 
     * @param accountId the account id for the tenant on which the user is to be demoted from admin
     * @param email the email/username of the user being demoted.
     * @throws CannotDemoteLastNetworkAdminException if the specified user is the last remaining NetworkAdmin on this tenant.
     */
    void demoteUserFromNetworkAdmin(Long accountId, String email);
    
    /**
     * Gets the home account of the specified user
     * 
     * @param email  user's email id
     * @return  home account
     */
    Account getHomeAccount(String email)
        throws InvalidEmailAddressException;
    
    /**
     * Gets the secondary accounts for the specified user
     *  
     * @param email  user's email id
     * @return  the list of secondary accounts
     */
    List<Account> getSecondaryAccounts(String email)
        throws InvalidEmailAddressException;
}

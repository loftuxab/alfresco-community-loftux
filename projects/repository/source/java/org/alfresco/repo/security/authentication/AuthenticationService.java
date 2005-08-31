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
package org.alfresco.repo.security.authentication;

import java.util.Set;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;

/**
 * The authentication service defines the API for managing authentication information 
 * against a userid. 
 *  
 * @author Andy Hind
 *
 */
public interface AuthenticationService
{
    /**
     * Create an authentication for the given user.
     * 
     * @param userName
     * @param password
     * @throws AuthenticationException
     */
    public void createAuthentication(String userName, char[] password) throws AuthenticationException;
    
    /**
     * Update the login information for the user (typically called by the user)
     * 
     * @param userName
     * @param oldPassword
     * @param newPassword
     * @throws AuthenticationException
     */
    public void updateAuthentication(String userName, char[] oldPassword, char[] newPassword) throws AuthenticationException;
    
    /**
     * Set the login information for a user (typically called by an admin user) 
     * 
     * @param userName
     * @param newPassword
     * @throws AuthenticationException
     */
    public void setAuthentication(String userName, char[] newPassword) throws AuthenticationException;
    

    /**
     * Delete an authentication entry
     * 
     * @param userName
     * @throws AuthenticationException
     */
    public void deleteAuthentication(String userName) throws AuthenticationException;
    
    /**
     * Carry out an authentication attempt. If successful the user is set to the current user.
     * The current user is a part of the thread context.
     * 
     * @param userName
     * @param password
     * @throws AuthenticationException
     */
    public void authenticate(String userName, char[] password) throws AuthenticationException;
    
    /**
     * Get the name of the currently authenticated user.
     * 
     * @return
     * @throws AuthenticationException
     */
    public String getCurrentUserName() throws AuthenticationException;
    
    /**
     * Invlidate any tickets held by the user.
     * 
     * @param userName
     * @throws AuthenticationException
     */
    public void invalidateUserSession(String userName) throws AuthenticationException;
    
   /**
    * Invalidate a single ticket by ID
    * 
    * @param ticket
    * @throws AuthenticationException
    */
    public void invalidateTicket(String ticket) throws AuthenticationException;
    
   /**
    * Validate a ticket. Set the current user name accordingly. 
    * 
    * @param ticket
    * @throws AuthenticationException
    */
    public void validate(String ticket) throws AuthenticationException;
    
    /**
     * Get the current ticket as a string
     * @return
     */
    public String getCurrentTicket();
    
    /**
     * Remove the current security information
     *
     */
    public void clearCurrentSecurityContext();
    
    
    /**
     * Get all the roles for the currently authenticated user.
     * 
     * @return
     */
    public Set<String> getCurrentUserRoles();
    
    /**
     * Get all the groups for the currently authenticated user.
     * 
     * @return
     */
    public Set<String> getCurrentUserGroups();
    
    /**
     * Get all the roles for the given userName.
     * 
     * @param userName
     * @return
     */
    public Set<String> getUserRoles(String userName);
    
    /**
     * Get all the groups for the given userName.
     * 
     * @param userName
     * @return
     */
    public Set<String> getUserGroups(String userName);
    
    /**
     * Get all the roles the authentication service knows about.
     * 
     * @return
     */
    public Set<String> getAllUserRoles();
    
    /**
     * Get all the groups the authentication service knows about.
     * 
     * @return
     */
    public Set<String> getAllUserGroups();
    
    /**
     * Get all the user names the authentication knows about.
     * 
     * @return
     */
    public Set<String> getAllUserNames();
    
    /**
     * Create or update a basic person entry for the given user name.
     * Depending on the implementation this may set properties derived from the authenication store. 
     * 
     * @param storeRef
     * @param userName
     * @return
     */
    public NodeRef synchronisePerson(StoreRef storeRef, String userName);
    
    /**
     * Get a user by userName
     * 
     * @param storeRef
     * @param userName
     * @return
     */
    public NodeRef getPersonNodeRef(StoreRef storeRef, String userName);
    
    /**
     * Is the current user the system user
     * 
     * @return
     */
    
    public boolean isCurrentUserTheSystemUser();
    
}


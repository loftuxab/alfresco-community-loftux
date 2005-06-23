/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.repo.security.authentication;

import net.sf.acegisecurity.Authentication;

import org.alfresco.service.cmr.repository.StoreRef;

/**
 * The authentication service defines the API for managing authentication information 
 * against a userid. 
 * 
 * This service follows the acegi pattern for authentication.
 * It makes the acegi authentication available as a service.
 *  
 * @author andyh
 *
 */
public interface AuthenticationService
{
    /**
     * Create an authentication entry based upon the supplied authentication object if supported
     * GrantedAuthorities will be found from the repository. 
     *  
     * @param authentication
     * @throws AuthenticationException
     */
    public void createAuthentication(StoreRef storeRef, Authentication authentication) throws AuthenticationException;
    
    /**
     * Update the credentials held. The GrantedAuthorities held in the repository will be unaffected.
     * 
     * @param authentication
     * @throws AuthenticationException
     */
    public void updateAuthentication(StoreRef storeRef, Authentication authentication) throws AuthenticationException;
    
    /**
     * Delete the authentication entry. Only the authentication entry is removed - the person information in the repository 
     * will remain unchanged. 
     * 
     * @param authentication
     * @throws AuthenticationException
     */
    public void deleteAuthentication(StoreRef storeRef, Authentication authentication) throws AuthenticationException;
    
    /**
     * Wrap an ACEGI authentication call
     * 
     * @param authentication
     * @return
     * @throws AuthenticationException
     */
    public Authentication authenticate(StoreRef storeRef, Authentication authentication) throws AuthenticationException;
    
    /**
     * Support to get the current authentication object
     * 
     * @return
     * @throws AuthenticationException
     */
    public Authentication getCurrentAuthentication() throws AuthenticationException;
    
    /**
     * Invalidate any ticket associated with the given authentication
     *  
     * @param authentication
     * @throws AuthenticationException
     */
    public void invalidate(Authentication authentication) throws AuthenticationException;
    
    /**
     * Invalidate any ticket associated with the given authentication
     *  
     * @param authentication
     * @throws AuthenticationException
     */
    public void invalidate(String ticket) throws AuthenticationException;
    
    /**
     * Invalidate any ticket associated with the given authentication
     *  
     * @param authentication
     * @return
     * @throws AuthenticationException
     */
    public Authentication validate(String ticket) throws AuthenticationException;
    
    /**
     * Invalidate any ticket associated with the given authentication
     *  
     * @param authentication
     * @return
     * @throws AuthenticationException
     */
    public Authentication validate(Authentication authentication) throws AuthenticationException;
    
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
     * Temporary method to set the current context by user name key
     * This is only for the preview to do pass through authentication in the CIFS integratrion
     */
    
    public Authentication setAuthenticatedUser(String userName);
}


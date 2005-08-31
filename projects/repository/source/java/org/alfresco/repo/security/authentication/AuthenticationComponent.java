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

import net.sf.acegisecurity.Authentication;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;

public interface AuthenticationComponent
{

    /**
     * Explicitly set the current user to be authenticated.
     */
    
    public Authentication setCurrentUser(String userName);
    
    /**
     * Remove the current security information
     *
     */
    public void clearCurrentSecurityContext();
    
    /**
     * Explicitly set the current suthentication.
     */
    
    public Authentication setCurrentAuthentication(Authentication authentication);
    
    /**
     * Get the person information for the user 
     * 
     * @param storeRef
     * @param userName
     * @return
     * @throws AuthenticationException
     */
    public NodeRef getPerson(StoreRef storeRef, String userName) throws AuthenticationException;
    
    /**
     * 
     * @param storeRef
     * @param userName
     * @return
     * @throws AuthenticationException
     */
    public NodeRef createPerson(StoreRef storeRef, String userName) throws AuthenticationException;
    
    /**
     * 
     * @return
     * @throws AuthenticationException
     */
    public Authentication getCurrentAuthentication() throws AuthenticationException;
    
    /**
     * Set the system user as the current user.
     * 
     * @return
     */
    public Authentication setSystemUserAsCurrentUser();
    
    
    /**
     * Get the name of the system user
     * 
     * @return
     */
    public String getSystemUserName();
    
}

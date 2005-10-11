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
package org.alfresco.repo.security.authority;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.PermissionService;

/**
 * The default implementation of the authority service.
 * 
 * @author Andy Hind
 */
public class AuthorityServiceImpl implements AuthorityService
{
    
    private Set<String> emptySet = Collections.<String>emptySet();
    
    private Set<String> adminSet = Collections.singleton(PermissionService.ADMINISTRATOR_AUTHORITY);
    
    private Set<String> adminUsers;
    
    private AuthenticationService authenticationService;

    public AuthorityServiceImpl()
    {
        super();
    }

    /**
     * Currently the admin authority is granted only to the ALFRESCO_ADMIN_USER user.
     */
    public boolean hasAdminAuthority()
    {
        String currentUserName = authenticationService.getCurrentUserName();
        return ((currentUserName != null) && adminUsers.contains(currentUserName));
    }

    // IOC
    
    public void setAuthenticationService(AuthenticationService authenticationService)
    {
        this.authenticationService = authenticationService;
    }
    
    public void setAdminUsers(Set<String> adminUsers)
    {
        this.adminUsers = adminUsers;   
    }

    public Set<String> getAuthorities()
    {
        String currentUserName = authenticationService.getCurrentUserName();
        return adminUsers.contains(currentUserName) ? adminSet : emptySet;
    }
    
    
}

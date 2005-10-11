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

import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.AuthorityService;

/**
 * The default implementation of the authority service.
 * 
 * @author Andy Hind
 */
public class AuthorityServiceImpl implements AuthorityService
{
    
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
        return ((currentUserName != null) && ALFRESCO_ADMIN_USER.equals(currentUserName));
    }

    // IOC
    
    public void setAuthenticationService(AuthenticationService authenticationService)
    {
        this.authenticationService = authenticationService;
    }
}

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

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.security.PersonService;

/**
 * The default implementation of the authority service.
 * 
 * @author Andy Hind
 */
public class AuthorityServiceImpl implements AuthorityService
{
    private PersonService personService;

    private NodeService nodeService;

    private Set<String> emptySet = Collections.<String> emptySet();

    private Set<String> adminSet = Collections.singleton(PermissionService.ADMINISTRATOR_AUTHORITY);

    private Set<String> guestSet = Collections.singleton(PermissionService.GUEST);

    private Set<String> allSet = Collections.singleton(PermissionService.ALL_AUTHORITIES);

    private Set<String> adminUsers;

    private AuthenticationService authenticationService;

    public AuthorityServiceImpl()
    {
        super();
    }

    
    
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }



    public void setPersonService(PersonService personService)
    {
        this.personService = personService;
    }



    /**
     * Currently the admin authority is granted only to the ALFRESCO_ADMIN_USER
     * user.
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

    public Set<String> getAllAuthorities(AuthorityType type)
    {
        switch (type)
        {
        case ADMIN:
            return adminSet;
        case EVERYONE:
            return allSet;
        case GUEST:
            return guestSet;
        case GROUP:
            return allSet;
        case OWNER:
            return emptySet;
        case ROLE:
            return emptySet;
        case USER:
            HashSet<String> userNames = new HashSet<String>();
            for (NodeRef personRef : personService.getAllPeople())
            {
                userNames.add(DefaultTypeConverter.INSTANCE.convert(String.class, nodeService.getProperty(personRef, ContentModel.PROP_USERNAME)));
            }
            return userNames;
        default:
            return emptySet;
        }
    }

}

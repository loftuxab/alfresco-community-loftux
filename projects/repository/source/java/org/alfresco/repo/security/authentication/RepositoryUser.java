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

import net.sf.acegisecurity.GrantedAuthority;
import net.sf.acegisecurity.providers.dao.User;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Extension to the acegi user to encapsulate a user in the content model
 * 
 * @author andyh
 *
 */
public class RepositoryUser extends User implements RepositoryUserDetails
{

    /**
     * 
     */
    private static final long serialVersionUID = 3258415040725005107L;
    
    private String salt;

    private NodeRef personNodeRef;

    private NodeRef userNodeRef;

    public RepositoryUser(String username, String password, boolean enabled,
            boolean accountNonExpired, boolean credentialsNonExpired,
            boolean accountNonLocked, GrantedAuthority[] authorities, 
            String salt, NodeRef userNodeRef, NodeRef personNodeRef)
            throws IllegalArgumentException
    {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.salt = salt;
        this.userNodeRef = userNodeRef;
        this.personNodeRef = personNodeRef;
    }

    public String getSalt()
    {
        //System.out.println("Salt is "+salt);
        return salt;
    }

    public NodeRef getUserNodeRef()
    {
        return userNodeRef;
    }

    public NodeRef getPersonNodeRef()
    {
        return personNodeRef;
    }

}

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
package org.alfresco.util;

import java.io.Serializable;
import java.util.HashMap;

import net.sf.acegisecurity.Authentication;
import net.sf.acegisecurity.providers.UsernamePasswordAuthenticationToken;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationService;
import org.alfresco.repo.security.authentication.RepositoryUser;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

/**
 * Utility class containing some useful methods to help when writing tets that require authenticated users
 * 
 * @author Roy Wetherall
 */
public abstract class TestWithUserUtils extends BaseSpringTest
{
    /**
     * Create a new user, including the corresponding person node.
     * 
     * @param userName                  the user name
     * @param password                  the password
     * @param rootNodeRef               the root node reference
     * @param nodeService               the node service
     * @param authenticationService     the authentication service
     */
    public static void createUser(
            String userName, 
            String password, 
            NodeRef rootNodeRef,
            NodeService nodeService,
            AuthenticationService authenticationService)
    {        
        QName children = ContentModel.ASSOC_CHILDREN;
        QName system = QName.createQName(NamespaceService.ALFRESCO_URI, "system");
        QName container = ContentModel.TYPE_CONTAINER;
        QName types = QName.createQName(NamespaceService.ALFRESCO_URI, "people");
        
        NodeRef systemNodeRef = nodeService.createNode(rootNodeRef, children, system, container).getChildRef();
        NodeRef typesNodeRef = nodeService.createNode(systemNodeRef, children, types, container).getChildRef();
        
        HashMap<QName, Serializable> properties = new HashMap<QName, Serializable>();
        properties.put(ContentModel.PROP_USERNAME, userName);
        NodeRef goodUserPerson = nodeService.createNode(typesNodeRef, children, ContentModel.TYPE_PERSON, container, properties).getChildRef();
        
        // Create the  users
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userName, password);
        authenticationService.createAuthentication(rootNodeRef.getStoreRef(), token); 
    }

    /**
     * Autneticate the user with the specified password
     * 
     * @param userName                  the user name
     * @param password                  the password
     * @param rootNodeRef               the root node reference
     * @param authenticationService     the authentication service
     */
    public static void authenticateUser(
            String userName, 
            String password,
            NodeRef rootNodeRef,
            AuthenticationService authenticationService)
    {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userName, password);
        authenticationService.authenticate(rootNodeRef.getStoreRef(), token);
    }
    
    /**
     * Get the current user node reference
     * 
     * @param authenticationService     the authentication service
     * @return                          the currenlty authenticated user's node reference
     */
    public static NodeRef getCurrentUserRef(AuthenticationService authenticationService)
    {
        NodeRef result = null;
        Authentication auth = authenticationService.getCurrentAuthentication();
        if (auth != null)
        {
            RepositoryUser user = (RepositoryUser)auth.getPrincipal();
            if (user != null)
            {
                result = user.getUserNodeRef();
            }
        }
        
        if (result == null)
        {
            throw new RuntimeException("The current user could not be retrieved.");
        }
        
        return result;
    }

}

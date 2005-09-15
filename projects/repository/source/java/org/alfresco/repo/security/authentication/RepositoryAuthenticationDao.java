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

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.acegisecurity.GrantedAuthority;
import net.sf.acegisecurity.GrantedAuthorityImpl;
import net.sf.acegisecurity.UserDetails;
import net.sf.acegisecurity.providers.dao.User;
import net.sf.acegisecurity.providers.dao.UsernameNotFoundException;
import net.sf.acegisecurity.providers.encoding.PasswordEncoder;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.search.QueryParameterDefImpl;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.repository.datatype.ValueConverter;
import org.alfresco.service.cmr.search.QueryParameterDefinition;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import org.springframework.dao.DataAccessException;

public class RepositoryAuthenticationDao implements MutableAuthenticationDao
{

    public static final String SYSTEM_FOLDER = "/sys:system";

    public static final String PEOPLE_FOLDER = SYSTEM_FOLDER + "/sys:people";

    private NodeService nodeService;

    private NamespacePrefixResolver namespacePrefixResolver;

    private DictionaryService dictionaryService;

    private SearchService searchService;

    private PasswordEncoder passwordEncoder;

    private StoreRef userStoreRef;
    
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    public void setNamespaceService(NamespacePrefixResolver namespacePrefixResolver)
    {
        this.namespacePrefixResolver = namespacePrefixResolver;
    }

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder)
    {
        this.passwordEncoder = passwordEncoder;
    }

    public void setSearchService(SearchService searchService)
    {
        this.searchService = searchService;
    }
    
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException, DataAccessException
    {
        NodeRef userRef = getUserOrNull(userName);
        if (userRef == null)
        {
            throw new UsernameNotFoundException("Could not find user by userName: " + userName);
        }

        Map<QName, Serializable> properties = nodeService.getProperties(userRef);
        String password = ValueConverter.convert(String.class, properties.get(QName.createQName("usr", "password",
                namespacePrefixResolver)));
        // String salt = ValueConverter.convert(String.class,
        // properties.get(QName.createQName("usr", "salt",
        // namespacePrefixResolver)));

        // TODO: Get roles correctly

        GrantedAuthority[] gas = new GrantedAuthority[1];
        gas[0] = new GrantedAuthorityImpl("ROLE_AUTHENTICATED");

        UserDetails ud = new User(userName, password, true, true, true, true, gas);
        return ud;
    }

    /* package for testing */NodeRef getUserOrNull(String userName)
    {
        NodeRef rootNode = nodeService.getRootNode(getUserStoreRef());
        QueryParameterDefinition[] defs = new QueryParameterDefinition[1];
        DataTypeDefinition text = dictionaryService.getDataType(DataTypeDefinition.TEXT);
        defs[0] = new QueryParameterDefImpl(QName.createQName("usr", "var", namespacePrefixResolver), text, true,
                userName);
        List<NodeRef> results = searchService.selectNodes(rootNode, PEOPLE_FOLDER
                + "/usr:user[@usr:username = $usr:var ]", defs, namespacePrefixResolver, false);
        if (results.size() != 1)
        {
            return null;
        }
        return results.get(0);
    }

    public void createUser(String userName, String rawPassword) throws AuthenticationException
    {
        NodeRef userRef = getUserOrNull(userName);
        if (userRef != null)
        {
            throw new AuthenticationException("User already exists: " + userName);
        }
        NodeRef typesNode = getOrCreateTypeLocation();
        Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
        properties.put(ContentModel.PROP_USER_USERNAME, userName);
        String salt = GUID.generate();
        properties.put(ContentModel.PROP_SALT, salt);
        properties.put(ContentModel.PROP_PASSWORD, passwordEncoder.encodePassword(rawPassword, salt));
        nodeService.createNode(typesNode, ContentModel.ASSOC_CHILDREN, ContentModel.TYPE_USER, ContentModel.TYPE_USER,
                properties);
    }

    private NodeRef getOrCreateTypeLocation()
    {
        NodeRef rootNode = nodeService.getRootNode(getUserStoreRef());
        List<ChildAssociationRef> results = nodeService.getChildAssocs(rootNode, QName.createQName("sys", "system",
                namespacePrefixResolver));
        NodeRef sysNode = null;
        if (results.size() == 0)
        {
            sysNode = nodeService.createNode(rootNode, ContentModel.ASSOC_CHILDREN,
                    QName.createQName("sys", "system", namespacePrefixResolver), ContentModel.TYPE_CONTAINER)
                    .getChildRef();
        }
        else
        {
            sysNode = results.get(0).getChildRef();
        }
        results = nodeService.getChildAssocs(sysNode, QName.createQName("sys", "people", namespacePrefixResolver));
        NodeRef typesNode = null;
        if (results.size() == 0)
        {
            typesNode = nodeService.createNode(sysNode, ContentModel.ASSOC_CHILDREN,
                    QName.createQName("sys", "people", namespacePrefixResolver), ContentModel.TYPE_CONTAINER)
                    .getChildRef();
        }
        else
        {
            typesNode = results.get(0).getChildRef();
        }
        return typesNode;
    }

    public void updateUser(String userName, String rawPassword) throws AuthenticationException
    {
        NodeRef userRef = getUserOrNull(userName);
        if (userRef == null)
        {
            throw new AuthenticationException("User does not exist: " + userName);
        }
        Map<QName, Serializable> properties = nodeService.getProperties(userRef);
        String salt = GUID.generate();
        properties.remove(ContentModel.PROP_SALT);
        properties.put(ContentModel.PROP_SALT, salt);
        properties.remove(ContentModel.PROP_PASSWORD);
        properties.put(ContentModel.PROP_PASSWORD, passwordEncoder.encodePassword(rawPassword, salt));
        nodeService.setProperties(userRef, properties);
    }

    public void deleteUser(String userName) throws AuthenticationException
    {
        NodeRef userRef = getUserOrNull(userName);
        if (userRef == null)
        {
            throw new AuthenticationException("User does not exist: " + userName);
        }
        nodeService.deleteNode(userRef);
    }

    public synchronized StoreRef getUserStoreRef()
    {
        if (userStoreRef == null)
        {
            userStoreRef = new StoreRef("user", "alfrescoUserStore");
        }
        if (!nodeService.exists(userStoreRef))
        {
            nodeService.createStore(userStoreRef.getProtocol(), userStoreRef.getIdentifier());
        }

        return userStoreRef;
    }

    public Object getSalt(UserDetails userDetails)
    {   
        NodeRef userRef = getUserOrNull(userDetails.getUsername());
        if (userRef == null)
        {
            throw new UsernameNotFoundException("Could not find user by userName: " + userDetails.getUsername());
        }

        Map<QName, Serializable> properties = nodeService.getProperties(userRef);

        String salt = ValueConverter.convert(String.class, properties.get(QName.createQName("usr", "salt",
                namespacePrefixResolver)));

        return salt;
    }

    public boolean userExists(String userName)
    {
       return (getUserOrNull(userName) != null);
    }
    
    
}

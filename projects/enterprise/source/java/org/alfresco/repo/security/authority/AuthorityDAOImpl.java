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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.cache.SimpleCache;
import org.alfresco.repo.search.ISO9075;
import org.alfresco.repo.search.impl.lucene.QueryParser;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.repo.security.authentication.MutableAuthenticationDao;
import org.alfresco.repo.security.permissions.impl.SimpleNodePermissionEntry;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;

public class AuthorityDAOImpl implements AuthorityDAO
{
    private NodeService nodeService;

    private NamespacePrefixResolver namespacePrefixResolver;

    private SearchService searchService;

    private StoreRef userStoreRef;

    private DictionaryService dictionaryService;

    private MutableAuthenticationDao authenticationDao;

    private SimpleCache<String, ArrayList<NodeRef>> userToAuthorityCache;

    public AuthorityDAOImpl()
    {
        super();
    }

    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    public void setNamespacePrefixResolver(NamespacePrefixResolver namespacePrefixResolver)
    {
        this.namespacePrefixResolver = namespacePrefixResolver;
    }

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setSearchService(SearchService searchService)
    {
        this.searchService = searchService;
    }

    public void setAuthenticationDao(MutableAuthenticationDao authenticationDao)
    {
        this.authenticationDao = authenticationDao;
    }

    public void setUserToAuthorityCache(SimpleCache<String, ArrayList<NodeRef>> userToAuthorityCache)
    {
        this.userToAuthorityCache = userToAuthorityCache;
    }

    public void addAuthority(String parentName, String childName)
    {
        NodeRef parentRef = getAuthorityOrNull(parentName);
        if (parentRef == null)
        {
            throw new UnknownAuthorityException("An authority was not found for " + parentName);
        }
        if (AuthorityType.getAuthorityType(childName).equals(AuthorityType.USER))
        {
            Collection<String> memberCollection = DefaultTypeConverter.INSTANCE.getCollection(String.class, nodeService
                    .getProperty(parentRef, ContentModel.PROP_MEMBERS));
            HashSet<String> members = new HashSet<String>();
            members.addAll(memberCollection);
            members.add(childName);
            nodeService.setProperty(parentRef, ContentModel.PROP_MEMBERS, members);
            userToAuthorityCache.remove(childName);
        }
        else
        {
            NodeRef childRef = getAuthorityOrNull(childName);
            if (childRef == null)
            {
                throw new UnknownAuthorityException("An authority was not found for " + childName);
            }
            nodeService.addChild(parentRef, childRef, ContentModel.ASSOC_MEMBER, QName.createQName("usr", childName,
                    namespacePrefixResolver));
        }

    }

    public void createAuthority(String parentName, String name)
    {
        HashMap<QName, Serializable> props = new HashMap<QName, Serializable>();
        props.put(ContentModel.PROP_AUTHORITY_NAME, name);
        if (parentName != null)
        {
            NodeRef parentRef = getAuthorityOrNull(parentName);
            if (parentRef == null)
            {
                throw new UnknownAuthorityException("An authority was not found for " + parentName);
            }
            nodeService.createNode(parentRef, ContentModel.ASSOC_MEMBER, QName.createQName("usr", name,
                    namespacePrefixResolver), ContentModel.TYPE_AUTHORITY_CONTAINER, props);
        }
        else
        {
            NodeRef authorityContainerRef = getAuthorityContainer();
            nodeService.createNode(authorityContainerRef, ContentModel.ASSOC_MEMBER, QName.createQName("usr", name,
                    namespacePrefixResolver), ContentModel.TYPE_AUTHORITY_CONTAINER, props);
        }
    }

    public void deleteAuthority(String name)
    {
        NodeRef nodeRef = getAuthorityOrNull(name);
        if (nodeRef == null)
        {
            throw new UnknownAuthorityException("An authority was not found for " + name);
        }
        nodeService.deleteNode(nodeRef);

    }

    public Set<String> getAllRootAuthorities(AuthorityType type)
    {
        HashSet<String> authorities = new HashSet<String>();
        findAuthorities(type, getAuthorityContainer(), authorities, false, false, false);
        return authorities;
    }

    public Set<String> getAllAuthorities(AuthorityType type)
    {
        HashSet<String> authorities = new HashSet<String>();
        findAuthorities(type, getAuthorityContainer(), authorities, false, true, false);
        return authorities;
    }

    public Set<String> getContainedAuthorities(AuthorityType type, String name, boolean immediate)
    {
        if (AuthorityType.getAuthorityType(name).equals(AuthorityType.USER))
        {
            return Collections.<String> emptySet();
        }
        else
        {
            NodeRef nodeRef = getAuthorityOrNull(name);
            if (nodeRef == null)
            {
                throw new UnknownAuthorityException("An authority was not found for " + name);
            }
            HashSet<String> authorities = new HashSet<String>();
            findAuthorities(type, nodeRef, authorities, false, !immediate, false);
            return authorities;
        }
    }

    public void removeAuthority(String parentName, String childName)
    {
        NodeRef parentRef = getAuthorityOrNull(parentName);
        if (parentRef == null)
        {
            throw new UnknownAuthorityException("An authority was not found for " + parentName);
        }
        if (AuthorityType.getAuthorityType(childName).equals(AuthorityType.USER))
        {
            Collection<String> memberCollection = DefaultTypeConverter.INSTANCE.getCollection(String.class, nodeService
                    .getProperty(parentRef, ContentModel.PROP_MEMBERS));
            HashSet<String> members = new HashSet<String>();
            members.addAll(memberCollection);
            members.remove(childName);
            nodeService.setProperty(parentRef, ContentModel.PROP_MEMBERS, members);
            userToAuthorityCache.remove(childName);
        }
        else
        {
            NodeRef childRef = getAuthorityOrNull(childName);
            if (childRef == null)
            {
                throw new UnknownAuthorityException("An authority was not found for " + childName);
            }
            nodeService.removeChild(parentRef, childRef);
        }

    }

    public Set<String> getContainingAuthorities(AuthorityType type, String name, boolean immediate)
    {
        HashSet<String> authorities = new HashSet<String>();
        findAuthorities(type, name, authorities, true, !immediate);
        return authorities;
    }

    private void findAuthorities(AuthorityType type, String name, Set<String> authorities, boolean parents,
            boolean recursive)
    {
        if (AuthorityType.getAuthorityType(name).equals(AuthorityType.USER))
        {
            for (NodeRef ref : getUserContainers(name))
            {
                findAuthorities(type, ref, authorities, parents, recursive, true);
            }

        }
        else
        {
            NodeRef ref = getAuthorityOrNull(name);

            if (ref == null)
            {
                throw new UnknownAuthorityException("An authority was not found for " + name);
            }

            findAuthorities(type, ref, authorities, parents, recursive, false);

        }
    }

    private ArrayList<NodeRef> getUserContainers(String name)
    {
        ArrayList<NodeRef> containers = userToAuthorityCache.get(name);
        if (containers == null)
        {
            containers = findUserContainers(name);
            userToAuthorityCache.put(name, containers);
        }
        return containers;
    }

    private ArrayList<NodeRef> findUserContainers(String name)
    {
        SearchParameters sp = new SearchParameters();
        sp.addStore(getUserStoreRef());
        sp.setLanguage("lucene");
        sp.setQuery("+TYPE:\""
                + ContentModel.TYPE_AUTHORITY_CONTAINER
                + "\""
                + " +@"
                + QueryParser.escape("{"
                        + ContentModel.PROP_MEMBERS.getNamespaceURI() + "}"
                        + ISO9075.encode(ContentModel.PROP_MEMBERS.getLocalName())) + ":\"" + name + "\"");
        ResultSet rs = null;
        try
        {
            rs = searchService.query(sp);
            ArrayList<NodeRef> answer = new ArrayList<NodeRef>(rs.length());
            for (ResultSetRow row : rs)
            {
                answer.add(row.getNodeRef());
            }
            return answer;
        }
        finally
        {
            if (rs != null)
            {
                rs.close();
            }
        }

    }

    private void findAuthorities(AuthorityType type, NodeRef nodeRef, Set<String> authorities, boolean parents,
            boolean recursive, boolean includeNode)
    {
        List<ChildAssociationRef> cars = parents ? nodeService.getParentAssocs(nodeRef) : nodeService
                .getChildAssocs(nodeRef);

        if (includeNode)
        {
            String authorityName = DefaultTypeConverter.INSTANCE.convert(String.class, nodeService.getProperty(nodeRef,
                    ContentModel.PROP_AUTHORITY_NAME));
            if (type == null)
            {
                authorities.add(authorityName);
            }
            else
            {
                AuthorityType authorityType = AuthorityType.getAuthorityType(authorityName);
                if (authorityType.equals(type))
                {
                    authorities.add(authorityName);
                }
            }
        }

        // Loop over children
        for (ChildAssociationRef car : cars)
        {
            NodeRef current = parents ? car.getParentRef() : car.getChildRef();
            QName currentType = nodeService.getType(current);
            if (dictionaryService.isSubClass(currentType, ContentModel.TYPE_AUTHORITY))
            {

                String authorityName = DefaultTypeConverter.INSTANCE.convert(String.class, nodeService.getProperty(
                        current, ContentModel.PROP_AUTHORITY_NAME));

                if (type == null)
                {
                    authorities.add(authorityName);
                    if (recursive)
                    {
                        findAuthorities(type, current, authorities, parents, recursive, false);
                    }
                }
                else
                {
                    AuthorityType authorityType = AuthorityType.getAuthorityType(authorityName);
                    if (authorityType.equals(type))
                    {
                        authorities.add(authorityName);
                    }
                    if (recursive)
                    {
                        findAuthorities(type, current, authorities, parents, recursive, false);
                    }
                }
            }
        }
        // loop over properties
        if (!parents)
        {
            Collection<String> members = DefaultTypeConverter.INSTANCE.getCollection(String.class, nodeService
                    .getProperty(nodeRef, ContentModel.PROP_MEMBERS));
            if (members != null)
            {
                for (String user : members)
                {
                    if (user != null)
                    {
                        if (type == null)
                        {
                            authorities.add(user);
                        }
                        else
                        {
                            AuthorityType authorityType = AuthorityType.getAuthorityType(user);
                            if (authorityType.equals(type))
                            {
                                authorities.add(user);
                            }
                        }
                    }
                }
            }
        }
    }

    private NodeRef getAuthorityOrNull(String name)
    {
        SearchParameters sp = new SearchParameters();
        sp.addStore(getUserStoreRef());
        sp.setLanguage("lucene");
        sp.setQuery("+TYPE:\""
                + ContentModel.TYPE_AUTHORITY_CONTAINER
                + "\""
                + " +@"
                + QueryParser.escape("{"
                        + ContentModel.PROP_AUTHORITY_NAME.getNamespaceURI() + "}"
                        + ISO9075.encode(ContentModel.PROP_AUTHORITY_NAME.getLocalName())) + ":\"" + name + "\"");
        ResultSet rs = null;
        try
        {
            rs = searchService.query(sp);
            if (rs.length() == 0)
            {
                return null;
            }
            return rs.getRow(0).getNodeRef();
        }
        finally
        {
            if (rs != null)
            {
                rs.close();
            }
        }

    }

    private NodeRef getAuthorityContainer()
    {
        NodeRef rootNode = nodeService.getRootNode(getUserStoreRef());
        List<ChildAssociationRef> results = nodeService.getChildAssocs(rootNode, RegexQNamePattern.MATCH_ALL, QName
                .createQName("sys", "system", namespacePrefixResolver));
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
        results = nodeService.getChildAssocs(sysNode, RegexQNamePattern.MATCH_ALL, QName.createQName("sys",
                "authorities", namespacePrefixResolver));
        NodeRef typesNode = null;
        if (results.size() == 0)
        {
            typesNode = nodeService.createNode(sysNode, ContentModel.ASSOC_CHILDREN,
                    QName.createQName("sys", "authorities", namespacePrefixResolver), ContentModel.TYPE_CONTAINER)
                    .getChildRef();
        }
        else
        {
            typesNode = results.get(0).getChildRef();
        }
        return typesNode;
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

}

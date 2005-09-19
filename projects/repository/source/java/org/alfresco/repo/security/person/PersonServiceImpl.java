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
package org.alfresco.repo.security.person;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.search.QueryParameterDefImpl;
import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.repo.security.authentication.RepositoryAuthenticationDao;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.QueryParameterDefinition;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;

public class PersonServiceImpl implements PersonService
{
    public static final String SYSTEM_FOLDER = "/sys:system";

    public static final String PEOPLE_FOLDER = SYSTEM_FOLDER + "/sys:people";

    // IOC

    private StoreRef storeRef;

    private NodeService nodeService;

    private DictionaryService dictionaryService;

    private SearchService searchService;

    private NamespacePrefixResolver namespacePrefixResolver;

    private boolean createMissingPeople;

    public PersonServiceImpl()
    {
        super();
    }

    public NodeRef getPerson(String userName)
    {

        NodeRef rootNode = nodeService.getRootNode(storeRef);
        QueryParameterDefinition[] defs = new QueryParameterDefinition[1];
        DataTypeDefinition text = dictionaryService.getDataType(DataTypeDefinition.TEXT);
        defs[0] = new QueryParameterDefImpl(QName.createQName("cm", "var", namespacePrefixResolver), text, true,
                userName);
        List<NodeRef> results = searchService.selectNodes(rootNode, PEOPLE_FOLDER
                + "/cm:person[@cm:userName = $cm:var ]", defs, namespacePrefixResolver, false);
        if (results.size() != 1)
        {
            throw new AuthenticationException("No user for " + userName);
        }
        return results.get(0);

    }

    public boolean createMissingPeople()
    {
        return createMissingPeople;
    }

    public Set<QName> getMutableProperties()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void setPersonProperties(NodeRef nodeRef, Map<QName, Serializable> properties)
    {
        // TODO Auto-generated method stub

    }

    public boolean isMutable()
    {
        return true;
    }

    public NodeRef createPerson(Map<QName, Serializable> properties)
    {

        NodeRef rootNodeRef = nodeService.getRootNode(storeRef);
        List<NodeRef> results = searchService.selectNodes(rootNodeRef, RepositoryAuthenticationDao.PEOPLE_FOLDER, null,
                namespacePrefixResolver, false);
        NodeRef typesNode = null;
        if (results.size() == 0)
        {

            List<ChildAssociationRef> result = nodeService.getChildAssocs(rootNodeRef, QName.createQName("sys",
                    "system", namespacePrefixResolver));
            NodeRef sysNode = null;
            if (result.size() == 0)
            {
                sysNode = nodeService.createNode(rootNodeRef, ContentModel.ASSOC_CHILDREN,
                        QName.createQName("sys", "system", namespacePrefixResolver), ContentModel.TYPE_CONTAINER)
                        .getChildRef();
            }
            else
            {
                sysNode = result.get(0).getChildRef();
            }
            result = nodeService.getChildAssocs(sysNode, QName.createQName("sys", "people", namespacePrefixResolver));

            if (result.size() == 0)
            {
                typesNode = nodeService.createNode(sysNode, ContentModel.ASSOC_CHILDREN,
                        QName.createQName("sys", "people", namespacePrefixResolver), ContentModel.TYPE_CONTAINER)
                        .getChildRef();
            }
            else
            {
                typesNode = result.get(0).getChildRef();
            }

        }
        else
        {
            typesNode = results.get(0);
        }

        return nodeService.createNode(typesNode, ContentModel.ASSOC_CHILDREN, ContentModel.TYPE_PERSON, // expecting
                // this
                // qname
                // path
                // in
                // the
                // authentication
                // methods
                ContentModel.TYPE_PERSON, properties).getChildRef();
    }

    public Set<String> getGroups(String userName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void deletePerson(String userName)
    {
        // TODO Auto-generated method stub

    }

    public Set<NodeRef> getAllPeople()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<String> getAllGroups()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void addPersonToGroup(String groupName, String userName)
    {
        // TODO Auto-generated method stub

    }

    public void deletePersonFromGroup(String groupName, String userName)
    {
        // TODO Auto-generated method stub

    }

    public void addSubGroupToGroup(String groupName, String subGroupName)
    {
        // TODO Auto-generated method stub

    }

    public void deleteSubGroupFromGroup(String groupName, String subGroupName)
    {
        // TODO Auto-generated method stub

    }

    public void deleteGroup(String groupName)
    {
        // TODO Auto-generated method stub

    }

    public void setCreateMissingPeople(boolean createMissingPeople)
    {
        this.createMissingPeople = createMissingPeople;
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

    public void setStoreUrl(String storeUrl)
    {
        this.storeRef = new StoreRef(storeUrl);
    }

    // IOC Setters

}

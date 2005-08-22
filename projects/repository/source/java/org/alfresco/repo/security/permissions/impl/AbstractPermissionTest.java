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
 *
 * Created on 19-Aug-2005
 */
package org.alfresco.repo.security.permissions.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import net.sf.acegisecurity.Authentication;
import net.sf.acegisecurity.providers.UsernamePasswordAuthenticationToken;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationService;
import org.alfresco.repo.security.permissions.PermissionService;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.BaseSpringTest;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;

public class AbstractPermissionTest extends BaseSpringTest
{

    protected static final String ROLE_AUTHENTICATED = "ROLE_AUTHENTICATED";

    protected NodeService nodeService;

    protected DictionaryService dictionaryService;

    protected PermissionService permissionService;

    protected AuthenticationService authenticationService;

    protected LocalSessionFactoryBean sessionFactory;

    protected NodeRef rootNodeRef;

    protected NamespacePrefixResolver namespacePrefixResolver;

    protected ServiceRegistry serviceRegistry;

    protected SimplePermissionReference READ;

    protected SimplePermissionReference READ_PROPERTIES;

    protected SimplePermissionReference READ_CHILDREN;

    protected SimplePermissionReference READ_CONTENT;

    protected SimplePermissionReference WRITE;
    
    protected SimplePermissionReference DELETE;
    
    protected SimplePermissionReference DELETE_NODE;
    
    protected SimplePermissionReference DELETE_CHILDREN;

    protected SimplePermissionReference FULL_CONTROL;

    protected NodeRef systemNodeRef;

    
    public AbstractPermissionTest()
    {
        super();
        // TODO Auto-generated constructor stub
    }


    protected void onSetUpInTransaction() throws Exception
    {
        nodeService = (NodeService) applicationContext.getBean("dbNodeService");
        dictionaryService = (DictionaryService) applicationContext.getBean("dictionaryService");
        permissionService = (PermissionService) applicationContext.getBean("permissionService");
        namespacePrefixResolver = (NamespacePrefixResolver) applicationContext.getBean("namespaceService");
        authenticationService = (AuthenticationService) applicationContext.getBean("authenticationService");
        serviceRegistry = (ServiceRegistry) applicationContext.getBean("serviceRegistry");
    
        StoreRef storeRef = nodeService.createStore(StoreRef.PROTOCOL_WORKSPACE, "Test_" + System.currentTimeMillis());
        rootNodeRef = nodeService.getRootNode(storeRef);
    
        setUpPermissions();
    
        QName children = ContentModel.ASSOC_CHILDREN;
        QName system = QName.createQName(NamespaceService.SYSTEM_MODEL_1_0_URI, "system");
        QName container = ContentModel.TYPE_CONTAINER;
        QName types = QName.createQName(NamespaceService.SYSTEM_MODEL_1_0_URI, "people");
    
        systemNodeRef = nodeService.createNode(rootNodeRef, children, system, container).getChildRef();
        NodeRef typesNodeRef = nodeService.createNode(systemNodeRef, children, types, container).getChildRef();
        Map<QName, Serializable> props = createPersonProperties("andy");
        nodeService.createNode(typesNodeRef, children, ContentModel.TYPE_PERSON, container, props).getChildRef();
        props = createPersonProperties("lemur");
        nodeService.createNode(typesNodeRef, children, ContentModel.TYPE_PERSON, container, props).getChildRef();
    
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("andy", "andy");
        // create an authentication object e.g. the user
        authenticationService.createAuthentication(storeRef, token);
    
        token = new UsernamePasswordAuthenticationToken("lemur", "lemur");
        authenticationService.createAuthentication(storeRef, token);
    }


    protected void onTearDownInTransaction()
    {
        super.onTearDownInTransaction();
        flushAndClear();
    }


    private void setUpPermissions()
    {
        READ = new SimplePermissionReference(QName.createQName("sys", "base", namespacePrefixResolver), "Read");
        READ_PROPERTIES = new SimplePermissionReference(QName.createQName("sys", "base", namespacePrefixResolver),
                "ReadProperties");
        READ_CHILDREN = new SimplePermissionReference(QName.createQName("sys", "base", namespacePrefixResolver),
                "ReadChildren");
        READ_CONTENT = new SimplePermissionReference(QName.createQName("cm", "content", namespacePrefixResolver),
                "ReadContent");
    
        WRITE = new SimplePermissionReference(QName.createQName("sys", "base", namespacePrefixResolver), "Write");
        
        DELETE = new SimplePermissionReference(QName.createQName("sys", "base", namespacePrefixResolver), "Delete");
        
        DELETE_CHILDREN = new SimplePermissionReference(QName.createQName("sys", "base", namespacePrefixResolver), "DeleteChildren");
        
        DELETE_NODE = new SimplePermissionReference(QName.createQName("sys", "base", namespacePrefixResolver), "DeleteNode");
    
        FULL_CONTROL = new SimplePermissionReference(QName.createQName("sys", "base", namespacePrefixResolver),
                "FullControl");
    
    }


    protected void runAs(String userName)
    {
        Authentication woof = authenticationService.authenticate(rootNodeRef.getStoreRef(),
                new UsernamePasswordAuthenticationToken(userName, userName));
        assertNotNull(woof);
        // for(GrantedAuthority authority : woof.getAuthorities())
        // {
        // System.out.println("Auth = "+authority.getAuthority());
        // }
    
    }


    private Map<QName, Serializable> createPersonProperties(String userName)
    {
        HashMap<QName, Serializable> properties = new HashMap<QName, Serializable>();
        properties.put(ContentModel.PROP_USERNAME, userName);
        return properties;
    }

}

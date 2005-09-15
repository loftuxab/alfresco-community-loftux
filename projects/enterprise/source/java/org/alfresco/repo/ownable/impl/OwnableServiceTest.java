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
package org.alfresco.repo.ownable.impl;

import javax.transaction.UserTransaction;

import junit.framework.TestCase;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.ownable.OwnableService;
import org.alfresco.repo.security.permissions.PermissionReference;
import org.alfresco.repo.security.permissions.impl.SimplePermissionReference;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.ApplicationContextHelper;
import org.springframework.context.ApplicationContext;

public class OwnableServiceTest extends TestCase
{
    private static ApplicationContext ctx = ApplicationContextHelper.getApplicationContext();

    private NodeService nodeService;

    private AuthenticationService authenticationService;

    private OwnableService ownableService;

    private NodeRef rootNodeRef;

    private UserTransaction userTransaction;
    
    private PermissionService permissionService;
    
    private OwnerDynamicAuthority dynamicAuthority;

    private NamespacePrefixResolver namespacePrefixResolver;

    
    public OwnableServiceTest()
    {
        super();
    }

    public OwnableServiceTest(String arg0)
    {
        super(arg0);
    }
    
    public void setUp() throws Exception
    {
        nodeService = (NodeService) ctx.getBean("nodeService");
        authenticationService = (AuthenticationService) ctx.getBean("authenticationService");
        ownableService = (OwnableService) ctx.getBean("ownableService");
        permissionService = (PermissionService) ctx.getBean("permissionService");
        
        namespacePrefixResolver = (NamespacePrefixResolver) ctx
        .getBean(ServiceRegistry.NAMESPACE_SERVICE.getLocalName());
     
        TransactionService transactionService = (TransactionService) ctx.getBean(ServiceRegistry.TRANSACTION_SERVICE.getLocalName());
        userTransaction = transactionService.getUserTransaction();
        userTransaction.begin();
        
        StoreRef storeRef = nodeService.createStore(StoreRef.PROTOCOL_WORKSPACE, "Test_" + System.currentTimeMillis());
        rootNodeRef = nodeService.getRootNode(storeRef);
        
        authenticationService.createAuthentication("andy", "andy".toCharArray());
        
        dynamicAuthority = new OwnerDynamicAuthority();
        dynamicAuthority.setOwnableService(ownableService);
    }
    
    @Override
    protected void tearDown() throws Exception
    {
        userTransaction.rollback();
        super.tearDown();
    }
    
    public void testSetup()
    {
        assertNotNull(nodeService);
        assertNotNull(authenticationService);
        assertNotNull(ownableService);
    }
    
    public void testUnSet()
    {
        assertNull(ownableService.getOwner(rootNodeRef));
        assertFalse(ownableService.hasOwner(rootNodeRef));
    }
    
    public void testCMObject()
    {
        authenticationService.authenticate("andy", "andy".toCharArray());
        NodeRef testNode = nodeService.createNode(rootNodeRef, ContentModel.ASSOC_CHILDREN, ContentModel.TYPE_PERSON, ContentModel.TYPE_CMOBJECT, null).getChildRef();
        assertEquals("andy", ownableService.getOwner(testNode));
        assertTrue(ownableService.hasOwner(testNode));
        assertTrue(nodeService.hasAspect(testNode, ContentModel.ASPECT_AUDITABLE));
        assertFalse(nodeService.hasAspect(testNode, ContentModel.ASPECT_OWNABLE));
        assertTrue(dynamicAuthority.hasAuthority(testNode, "andy"));
        
        ownableService.setOwner(testNode, "muppet");
        assertEquals("muppet", ownableService.getOwner(testNode));
        ownableService.takeOwnership(testNode);
        assertEquals("andy", ownableService.getOwner(testNode));
        assertTrue(nodeService.hasAspect(testNode, ContentModel.ASPECT_AUDITABLE));
        assertTrue(nodeService.hasAspect(testNode, ContentModel.ASPECT_OWNABLE));
        assertTrue(dynamicAuthority.hasAuthority(testNode, "andy"));
    }
    
    public void testContainer()
    {
        PermissionReference READ = new SimplePermissionReference(QName.createQName("sys", "base", namespacePrefixResolver), "Read");
        
        authenticationService.authenticate("andy", "andy".toCharArray());
        NodeRef testNode = nodeService.createNode(rootNodeRef, ContentModel.ASSOC_CHILDREN, ContentModel.TYPE_PERSON, ContentModel.TYPE_CONTAINER, null).getChildRef();
        assertNull(ownableService.getOwner(testNode));
        assertFalse(ownableService.hasOwner(testNode));
        assertFalse(nodeService.hasAspect(testNode, ContentModel.ASPECT_AUDITABLE));
        assertFalse(nodeService.hasAspect(testNode, ContentModel.ASPECT_OWNABLE));
        assertFalse(dynamicAuthority.hasAuthority(testNode, "andy"));
        
        assertFalse(permissionService.hasPermission(testNode, READ.toString()) == AccessStatus.ALLOWED);
        assertFalse(permissionService.hasPermission(testNode, permissionService.getAllPermission()) == AccessStatus.ALLOWED);
        
        permissionService.setPermission(rootNodeRef, permissionService.getOwnerAuthority(), permissionService.getAllPermission(), true);
        
        ownableService.setOwner(testNode, "muppet");
        assertEquals("muppet", ownableService.getOwner(testNode));
        ownableService.takeOwnership(testNode);
        assertEquals("andy", ownableService.getOwner(testNode));
        assertFalse(nodeService.hasAspect(testNode, ContentModel.ASPECT_AUDITABLE));
        assertTrue(nodeService.hasAspect(testNode, ContentModel.ASPECT_OWNABLE));
        assertTrue(dynamicAuthority.hasAuthority(testNode, "andy"));
        
        assertTrue(permissionService.hasPermission(testNode, READ.toString()) == AccessStatus.ALLOWED);
        assertTrue(permissionService.hasPermission(testNode, permissionService.getAllPermission())== AccessStatus.ALLOWED);
        
        
    }
    
}

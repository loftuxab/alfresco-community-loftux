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
 * Created on 04-Aug-2005
 */
package org.alfresco.repo.security.permissions.impl;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import net.sf.acegisecurity.providers.UsernamePasswordAuthenticationToken;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.permissions.AccessStatus;
import org.alfresco.repo.security.permissions.PermissionEntry;
import org.alfresco.repo.security.permissions.PermissionReference;
import org.alfresco.repo.security.permissions.PermissionService;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ApplicationContextHelper;
import org.springframework.context.ApplicationContext;

public class PermissionServiceTest extends TestCase
{
    private static ApplicationContext ctx = ApplicationContextHelper.getApplicationContext();

    NodeService nodeService;
    DictionaryService dictionaryService;
    PermissionService permissionService;
    
    private NodeRef rootNodeRef;

    private NamespacePrefixResolver namespacePrefixResolver;

    public PermissionServiceTest()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    public PermissionServiceTest(String arg0)
    {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    public void setUp() throws IOException, InterruptedException
    {
        nodeService = (NodeService) ctx.getBean("dbNodeService");
        dictionaryService = (DictionaryService) ctx.getBean("dictionaryService");
        permissionService = (PermissionService) ctx.getBean("permissionService");
        namespacePrefixResolver = (NamespacePrefixResolver) ctx.getBean("namespaceService");
        
        StoreRef storeRef = nodeService.createStore(StoreRef.PROTOCOL_WORKSPACE, "Test_" + System.currentTimeMillis());
        rootNodeRef = nodeService.getRootNode(storeRef);
    }
    
    public void testUnset()
    {
        assertNull(permissionService.getSetPermissions(rootNodeRef));
    }
    
    // Simple inheritance flag
    
    public void testSetInheritFalse()
    {
        permissionService.setInheritParentPermissions(rootNodeRef, false);
        assertNotNull(permissionService.getSetPermissions(rootNodeRef));
        assertFalse(permissionService.getSetPermissions(rootNodeRef).inheritPermissions());
        assertEquals(rootNodeRef, permissionService.getSetPermissions(rootNodeRef).getNodeRef());
        assertEquals(0, permissionService.getSetPermissions(rootNodeRef).getPermissionEntries().size());       
    }
    
    public void testSetInheritTrue()
    {
        permissionService.setInheritParentPermissions(rootNodeRef, true);
        assertNotNull(permissionService.getSetPermissions(rootNodeRef));
        assertTrue(permissionService.getSetPermissions(rootNodeRef).inheritPermissions());
        assertEquals(rootNodeRef, permissionService.getSetPermissions(rootNodeRef).getNodeRef());
        assertEquals(0, permissionService.getSetPermissions(rootNodeRef).getPermissionEntries().size());     
        
        permissionService.deletePermissions(permissionService.getSetPermissions(rootNodeRef));
    }
        
    public void testAlterInherit()
    {
        testSetInheritFalse();
        testSetInheritTrue();
        testSetInheritFalse();
        testSetInheritTrue();
        
        permissionService.deletePermissions(rootNodeRef);
        testUnset();
    }
    
    public void testSetNodePermissionEntry()
    {
        Set<SimplePermissionEntry> entries = new HashSet<SimplePermissionEntry>();
        entries.add(new SimplePermissionEntry(rootNodeRef, new SimplePermissionReference(QName.createQName("A", "B"), "C"), "user-one", AccessStatus.ALLOWED));
        entries.add(new SimplePermissionEntry(rootNodeRef, SimplePermissionEntry.ALL_PERMISSIONS, "user-two", AccessStatus.ALLOWED));
        entries.add(new SimplePermissionEntry(rootNodeRef, new SimplePermissionReference(QName.createQName("D", "E"), "F"), SimplePermissionEntry.ALL_AUTHORITIES, AccessStatus.ALLOWED));
        entries.add(new SimplePermissionEntry(rootNodeRef, SimplePermissionEntry.ALL_PERMISSIONS, SimplePermissionEntry.ALL_AUTHORITIES, AccessStatus.DENIED));
        
        SimpleNodePermissionEntry entry = new SimpleNodePermissionEntry(rootNodeRef, false, entries);
        
        permissionService.setPermission(entry);
        
        assertNotNull(permissionService.getSetPermissions(rootNodeRef));
        assertFalse(permissionService.getSetPermissions(rootNodeRef).inheritPermissions());
        assertEquals(rootNodeRef, permissionService.getSetPermissions(rootNodeRef).getNodeRef());
        assertEquals(4, permissionService.getSetPermissions(rootNodeRef).getPermissionEntries().size());
    }
    
    public void testSetNodePermissionEntry2()
    {
        Set<SimplePermissionEntry> entries = new HashSet<SimplePermissionEntry>();
        entries.add(new SimplePermissionEntry(rootNodeRef, SimplePermissionEntry.ALL_PERMISSIONS, SimplePermissionEntry.ALL_AUTHORITIES, AccessStatus.ALLOWED));
        
        SimpleNodePermissionEntry entry = new SimpleNodePermissionEntry(rootNodeRef, false, entries);
        
        permissionService.setPermission(entry);
        
        assertNotNull(permissionService.getSetPermissions(rootNodeRef));
        assertFalse(permissionService.getSetPermissions(rootNodeRef).inheritPermissions());
        assertEquals(rootNodeRef, permissionService.getSetPermissions(rootNodeRef).getNodeRef());
        assertEquals(1, permissionService.getSetPermissions(rootNodeRef).getPermissionEntries().size());
    }
    
    public void testAlterNodePermissions()
    {
        testSetNodePermissionEntry();
        testSetNodePermissionEntry2();
        testSetNodePermissionEntry();
        testSetNodePermissionEntry2();
    }
    
    
    public void testSetPermissionEntryElements()
    {
        permissionService.setPermission(rootNodeRef, "andy", SimplePermissionEntry.ALL_PERMISSIONS, true);
        assertNotNull(permissionService.getSetPermissions(rootNodeRef));
        assertTrue(permissionService.getSetPermissions(rootNodeRef).inheritPermissions());
        assertEquals(rootNodeRef, permissionService.getSetPermissions(rootNodeRef).getNodeRef());
        assertEquals(1, permissionService.getSetPermissions(rootNodeRef).getPermissionEntries().size());
        for(PermissionEntry pe : permissionService.getSetPermissions(rootNodeRef).getPermissionEntries())
        {
            assertEquals("andy", pe.getAuthority());
            assertTrue(pe.isAllowed());
            assertTrue(pe.getPermissionReference().getQName().equals(SimplePermissionEntry.ALL_PERMISSIONS.getQName()));
            assertTrue(pe.getPermissionReference().getName().equals(SimplePermissionEntry.ALL_PERMISSIONS.getName()));
            assertEquals(rootNodeRef, pe.getNodeRef());
        }
        
        // Set duplicate  
        
        permissionService.setPermission(rootNodeRef, "andy", SimplePermissionEntry.ALL_PERMISSIONS, true);
        assertNotNull(permissionService.getSetPermissions(rootNodeRef));
        assertTrue(permissionService.getSetPermissions(rootNodeRef).inheritPermissions());
        assertEquals(rootNodeRef, permissionService.getSetPermissions(rootNodeRef).getNodeRef());
        assertEquals(1, permissionService.getSetPermissions(rootNodeRef).getPermissionEntries().size());
        
        // Set new
        
        permissionService.setPermission(rootNodeRef, "other", SimplePermissionEntry.ALL_PERMISSIONS, true);
        assertNotNull(permissionService.getSetPermissions(rootNodeRef));
        assertTrue(permissionService.getSetPermissions(rootNodeRef).inheritPermissions());
        assertEquals(rootNodeRef, permissionService.getSetPermissions(rootNodeRef).getNodeRef());
        assertEquals(2, permissionService.getSetPermissions(rootNodeRef).getPermissionEntries().size());
        
        // Add deny
        
        permissionService.setPermission(rootNodeRef, "andy", SimplePermissionEntry.ALL_PERMISSIONS, false);
        assertNotNull(permissionService.getSetPermissions(rootNodeRef));
        assertTrue(permissionService.getSetPermissions(rootNodeRef).inheritPermissions());
        assertEquals(rootNodeRef, permissionService.getSetPermissions(rootNodeRef).getNodeRef());
        assertEquals(3, permissionService.getSetPermissions(rootNodeRef).getPermissionEntries().size());
        
        // new
        
        permissionService.setPermission(rootNodeRef, "andy", new SimplePermissionReference(QName.createQName("A", "B"), "C"), false);
        assertNotNull(permissionService.getSetPermissions(rootNodeRef));
        assertTrue(permissionService.getSetPermissions(rootNodeRef).inheritPermissions());
        assertEquals(rootNodeRef, permissionService.getSetPermissions(rootNodeRef).getNodeRef());
        assertEquals(4, permissionService.getSetPermissions(rootNodeRef).getPermissionEntries().size());
        
        // delete 
        
        permissionService.deletePermission(rootNodeRef, "andy", new SimplePermissionReference(QName.createQName("A", "B"), "C"), false);
        assertNotNull(permissionService.getSetPermissions(rootNodeRef));
        assertTrue(permissionService.getSetPermissions(rootNodeRef).inheritPermissions());
        assertEquals(rootNodeRef, permissionService.getSetPermissions(rootNodeRef).getNodeRef());
        assertEquals(3, permissionService.getSetPermissions(rootNodeRef).getPermissionEntries().size());
        
        permissionService.deletePermission(rootNodeRef, "andy", SimplePermissionEntry.ALL_PERMISSIONS, false);
        assertNotNull(permissionService.getSetPermissions(rootNodeRef));
        assertTrue(permissionService.getSetPermissions(rootNodeRef).inheritPermissions());
        assertEquals(rootNodeRef, permissionService.getSetPermissions(rootNodeRef).getNodeRef());
        assertEquals(2, permissionService.getSetPermissions(rootNodeRef).getPermissionEntries().size());
        
        permissionService.deletePermission(rootNodeRef, "other", SimplePermissionEntry.ALL_PERMISSIONS, true);
        assertNotNull(permissionService.getSetPermissions(rootNodeRef));
        assertTrue(permissionService.getSetPermissions(rootNodeRef).inheritPermissions());
        assertEquals(rootNodeRef, permissionService.getSetPermissions(rootNodeRef).getNodeRef());
        assertEquals(1, permissionService.getSetPermissions(rootNodeRef).getPermissionEntries().size());
        
        permissionService.deletePermission(rootNodeRef, "andy", SimplePermissionEntry.ALL_PERMISSIONS, true);
        assertNotNull(permissionService.getSetPermissions(rootNodeRef));
        assertTrue(permissionService.getSetPermissions(rootNodeRef).inheritPermissions());
        assertEquals(rootNodeRef, permissionService.getSetPermissions(rootNodeRef).getNodeRef());
        assertEquals(0, permissionService.getSetPermissions(rootNodeRef).getPermissionEntries().size());
        
        
    }
    
    public void testSetPermissionEntry()
    {
        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, SimplePermissionEntry.ALL_PERMISSIONS, "andy", AccessStatus.ALLOWED));
        permissionService.setPermission(rootNodeRef, "andy", SimplePermissionEntry.ALL_PERMISSIONS, true);
        assertNotNull(permissionService.getSetPermissions(rootNodeRef));
        assertTrue(permissionService.getSetPermissions(rootNodeRef).inheritPermissions());
        assertEquals(rootNodeRef, permissionService.getSetPermissions(rootNodeRef).getNodeRef());
        assertEquals(1, permissionService.getSetPermissions(rootNodeRef).getPermissionEntries().size());
        for(PermissionEntry pe : permissionService.getSetPermissions(rootNodeRef).getPermissionEntries())
        {
            assertEquals("andy", pe.getAuthority());
            assertTrue(pe.isAllowed());
            assertTrue(pe.getPermissionReference().getQName().equals(SimplePermissionEntry.ALL_PERMISSIONS.getQName()));
            assertTrue(pe.getPermissionReference().getName().equals(SimplePermissionEntry.ALL_PERMISSIONS.getName()));
            assertEquals(rootNodeRef, pe.getNodeRef());
        }
        
        
       // Set duplicate  
        
        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, SimplePermissionEntry.ALL_PERMISSIONS, "andy", AccessStatus.ALLOWED));
        assertNotNull(permissionService.getSetPermissions(rootNodeRef));
        assertTrue(permissionService.getSetPermissions(rootNodeRef).inheritPermissions());
        assertEquals(rootNodeRef, permissionService.getSetPermissions(rootNodeRef).getNodeRef());
        assertEquals(1, permissionService.getSetPermissions(rootNodeRef).getPermissionEntries().size());
        
        // Set new
        
        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, SimplePermissionEntry.ALL_PERMISSIONS, "other", AccessStatus.ALLOWED));
        assertNotNull(permissionService.getSetPermissions(rootNodeRef));
        assertTrue(permissionService.getSetPermissions(rootNodeRef).inheritPermissions());
        assertEquals(rootNodeRef, permissionService.getSetPermissions(rootNodeRef).getNodeRef());
        assertEquals(2, permissionService.getSetPermissions(rootNodeRef).getPermissionEntries().size());
        
        // Deny
        
        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, SimplePermissionEntry.ALL_PERMISSIONS, "andy", AccessStatus.DENIED));
        assertNotNull(permissionService.getSetPermissions(rootNodeRef));
        assertTrue(permissionService.getSetPermissions(rootNodeRef).inheritPermissions());
        assertEquals(rootNodeRef, permissionService.getSetPermissions(rootNodeRef).getNodeRef());
        assertEquals(3, permissionService.getSetPermissions(rootNodeRef).getPermissionEntries().size());
        
        // new
        
        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, new SimplePermissionReference(QName.createQName("A", "B"), "C"), "andy", AccessStatus.DENIED));
        assertNotNull(permissionService.getSetPermissions(rootNodeRef));
        assertTrue(permissionService.getSetPermissions(rootNodeRef).inheritPermissions());
        assertEquals(rootNodeRef, permissionService.getSetPermissions(rootNodeRef).getNodeRef());
        assertEquals(4, permissionService.getSetPermissions(rootNodeRef).getPermissionEntries().size());
        
        permissionService.deletePermission(new SimplePermissionEntry(rootNodeRef, new SimplePermissionReference(QName.createQName("A", "B"), "C"), "andy", AccessStatus.DENIED));
        assertNotNull(permissionService.getSetPermissions(rootNodeRef));
        assertTrue(permissionService.getSetPermissions(rootNodeRef).inheritPermissions());
        assertEquals(rootNodeRef, permissionService.getSetPermissions(rootNodeRef).getNodeRef());
        assertEquals(3, permissionService.getSetPermissions(rootNodeRef).getPermissionEntries().size());
        
        permissionService.deletePermission(new SimplePermissionEntry(rootNodeRef, SimplePermissionEntry.ALL_PERMISSIONS, "andy", AccessStatus.DENIED));
        assertNotNull(permissionService.getSetPermissions(rootNodeRef));
        assertTrue(permissionService.getSetPermissions(rootNodeRef).inheritPermissions());
        assertEquals(rootNodeRef, permissionService.getSetPermissions(rootNodeRef).getNodeRef());
        assertEquals(2, permissionService.getSetPermissions(rootNodeRef).getPermissionEntries().size());
        
        permissionService.deletePermission(new SimplePermissionEntry(rootNodeRef, SimplePermissionEntry.ALL_PERMISSIONS, "other", AccessStatus.ALLOWED));
        assertNotNull(permissionService.getSetPermissions(rootNodeRef));
        assertTrue(permissionService.getSetPermissions(rootNodeRef).inheritPermissions());
        assertEquals(rootNodeRef, permissionService.getSetPermissions(rootNodeRef).getNodeRef());
        assertEquals(1, permissionService.getSetPermissions(rootNodeRef).getPermissionEntries().size());  
        
        permissionService.deletePermission(new SimplePermissionEntry(rootNodeRef, SimplePermissionEntry.ALL_PERMISSIONS, "andy", AccessStatus.ALLOWED));
        assertNotNull(permissionService.getSetPermissions(rootNodeRef));
        assertTrue(permissionService.getSetPermissions(rootNodeRef).inheritPermissions());
        assertEquals(rootNodeRef, permissionService.getSetPermissions(rootNodeRef).getNodeRef());
        assertEquals(0, permissionService.getSetPermissions(rootNodeRef).getPermissionEntries().size());  
    }
    
    public void testGetSettablePermissionsForType()
    {
        Set<PermissionReference>answer = permissionService.getSettablePermissions(QName.createQName("alf", "base", namespacePrefixResolver));
        assertEquals(17, answer.size());
        
        answer = permissionService.getSettablePermissions(QName.createQName("alf", "ownable", namespacePrefixResolver));
        assertEquals(2, answer.size());
        
        answer = permissionService.getSettablePermissions(QName.createQName("alf", "content", namespacePrefixResolver));
        assertEquals(21, answer.size());    
    }
    
    public void testGetSettablePermissionsForNode()
    {
        QName ownable = QName.createQName("alf", "ownable", namespacePrefixResolver);
        
        Set<PermissionReference> answer = permissionService.getSettablePermissions(rootNodeRef);
        assertEquals(17, answer.size());
        
        nodeService.addAspect(rootNodeRef, ownable, null);
        answer = permissionService.getSettablePermissions(rootNodeRef);
        assertEquals(19, answer.size());
        
        nodeService.removeAspect(rootNodeRef, ownable);
        answer = permissionService.getSettablePermissions(rootNodeRef);
        assertEquals(17, answer.size());
    }
    
    public void testSimplePermissionOnRoot()
    {
        SimplePermissionReference READ_PROPERTIES = new SimplePermissionReference(QName.createQName("alf", "base", namespacePrefixResolver), "ReadProperties");
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ_PROPERTIES, "andy", AccessStatus.ALLOWED));
        assertTrue(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ_PROPERTIES, "andy", AccessStatus.DENIED));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ_PROPERTIES, "andy", AccessStatus.ALLOWED));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ_PROPERTIES, "andy", AccessStatus.DENIED));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ_PROPERTIES, "andy", AccessStatus.ALLOWED));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        permissionService.deletePermission(new SimplePermissionEntry(rootNodeRef, READ_PROPERTIES, "andy", AccessStatus.DENIED));
        assertTrue(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        permissionService.deletePermission(new SimplePermissionEntry(rootNodeRef, READ_PROPERTIES, "andy", AccessStatus.ALLOWED));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
    }
    
    public void testPermissionGroupOnRoot()
    {
        SimplePermissionReference READ = new SimplePermissionReference(QName.createQName("alf", "base", namespacePrefixResolver), "Read");
        SimplePermissionReference READ_PROPERTIES = new SimplePermissionReference(QName.createQName("alf", "base", namespacePrefixResolver), "ReadProperties");
        SimplePermissionReference READ_CHILDREN = new SimplePermissionReference(QName.createQName("alf", "base", namespacePrefixResolver), "ReadChildren");
        SimplePermissionReference READ_CONTENT = new SimplePermissionReference(QName.createQName("alf", "content", namespacePrefixResolver), "ReadContent");
        
        
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CONTENT));
        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ, "andy", AccessStatus.ALLOWED));
        assertTrue(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ));
        assertTrue(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CONTENT));
        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ, "andy", AccessStatus.DENIED));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CONTENT));
        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ, "andy", AccessStatus.ALLOWED));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CONTENT));
        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ, "andy", AccessStatus.DENIED));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CONTENT));
        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ, "andy", AccessStatus.ALLOWED));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CONTENT));
        permissionService.deletePermission(new SimplePermissionEntry(rootNodeRef, READ, "andy", AccessStatus.DENIED));
        assertTrue(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ));
        assertTrue(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CONTENT));
        permissionService.deletePermission(new SimplePermissionEntry(rootNodeRef, READ, "andy", AccessStatus.ALLOWED));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CONTENT));
    }
    
    public void testSimplePermissionSimpleInheritance()
    {
        NodeRef n1 = nodeService.createNode(rootNodeRef, ContentModel.ASSOC_CHILDREN, QName.createQName("{namespace}one"), ContentModel.TYPE_FOLDER).getChildRef();
        SimplePermissionReference READ_PROPERTIES = new SimplePermissionReference(QName.createQName("alf", "base", namespacePrefixResolver), "ReadProperties");
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n1, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ_PROPERTIES, "andy", AccessStatus.ALLOWED));
        assertTrue(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(n1, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ_PROPERTIES, "andy", AccessStatus.DENIED));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n1, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ_PROPERTIES, "andy", AccessStatus.ALLOWED));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n1, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ_PROPERTIES, "andy", AccessStatus.DENIED));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n1, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ_PROPERTIES, "andy", AccessStatus.ALLOWED));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n1, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        permissionService.deletePermission(new SimplePermissionEntry(rootNodeRef, READ_PROPERTIES, "andy", AccessStatus.DENIED));
        assertTrue(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(n1, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        permissionService.deletePermission(new SimplePermissionEntry(rootNodeRef, READ_PROPERTIES, "andy", AccessStatus.ALLOWED));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n1, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
    }
    
    public void testPermissionGroupSimpleInheritance()
    {
        NodeRef n1 = nodeService.createNode(rootNodeRef, ContentModel.ASSOC_CHILDREN, QName.createQName("{namespace}one"), ContentModel.TYPE_FOLDER).getChildRef();
        
        
        SimplePermissionReference READ = new SimplePermissionReference(QName.createQName("alf", "base", namespacePrefixResolver), "Read");
        SimplePermissionReference READ_PROPERTIES = new SimplePermissionReference(QName.createQName("alf", "base", namespacePrefixResolver), "ReadProperties");
        SimplePermissionReference READ_CHILDREN = new SimplePermissionReference(QName.createQName("alf", "base", namespacePrefixResolver), "ReadChildren");
        SimplePermissionReference READ_CONTENT = new SimplePermissionReference(QName.createQName("alf", "content", namespacePrefixResolver), "ReadContent");
        
        
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CONTENT));
        assertFalse(permissionService.hasPermission(n1, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ));
        assertFalse(permissionService.hasPermission(n1, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n1, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CHILDREN));
        assertFalse(permissionService.hasPermission(n1, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CONTENT));
        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ, "andy", AccessStatus.ALLOWED));
        assertTrue(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ));
        assertTrue(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CONTENT));
        assertTrue(permissionService.hasPermission(n1, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ));
        assertTrue(permissionService.hasPermission(n1, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(n1, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CHILDREN));
        assertFalse(permissionService.hasPermission(n1, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CONTENT));
        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ, "andy", AccessStatus.DENIED));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CONTENT));
        assertFalse(permissionService.hasPermission(n1, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ));
        assertFalse(permissionService.hasPermission(n1, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n1, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CHILDREN));
        assertFalse(permissionService.hasPermission(n1, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CONTENT));
        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ, "andy", AccessStatus.ALLOWED));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CONTENT));
        assertFalse(permissionService.hasPermission(n1, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ));
        assertFalse(permissionService.hasPermission(n1, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n1, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CHILDREN));
        assertFalse(permissionService.hasPermission(n1, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CONTENT));
        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ, "andy", AccessStatus.DENIED));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CONTENT));
        assertFalse(permissionService.hasPermission(n1, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ));
        assertFalse(permissionService.hasPermission(n1, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n1, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CHILDREN));
        assertFalse(permissionService.hasPermission(n1, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CONTENT));
        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ, "andy", AccessStatus.ALLOWED));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CONTENT));
        assertFalse(permissionService.hasPermission(n1, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ));
        assertFalse(permissionService.hasPermission(n1, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n1, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CHILDREN));
        assertFalse(permissionService.hasPermission(n1, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CONTENT));
        permissionService.deletePermission(new SimplePermissionEntry(rootNodeRef, READ, "andy", AccessStatus.DENIED));
        assertTrue(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ));
        assertTrue(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CONTENT));
        assertTrue(permissionService.hasPermission(n1, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ));
        assertTrue(permissionService.hasPermission(n1, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(n1, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CHILDREN));
        assertFalse(permissionService.hasPermission(n1, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CONTENT));
        permissionService.deletePermission(new SimplePermissionEntry(rootNodeRef, READ, "andy", AccessStatus.ALLOWED));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CONTENT));
        assertFalse(permissionService.hasPermission(n1, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ));
        assertFalse(permissionService.hasPermission(n1, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n1, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CHILDREN));
        assertFalse(permissionService.hasPermission(n1, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_CONTENT));
    }
    
    public void testDenySimplePermisionOnRootNOde()
    {
        SimplePermissionReference READ_PROPERTIES = new SimplePermissionReference(QName.createQName("alf", "base", namespacePrefixResolver), "ReadProperties");

        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ_PROPERTIES, "andy", AccessStatus.ALLOWED));
        assertTrue(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ_PROPERTIES, "andy", AccessStatus.DENIED));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        permissionService.deletePermission(new SimplePermissionEntry(rootNodeRef, READ_PROPERTIES, "andy", AccessStatus.DENIED));
        assertTrue(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
        permissionService.deletePermission(new SimplePermissionEntry(rootNodeRef, READ_PROPERTIES, "andy", AccessStatus.ALLOWED));
        assertFalse(permissionService.hasPermission(rootNodeRef, new UsernamePasswordAuthenticationToken("andy", "andy") ,READ_PROPERTIES));
    }
    
}

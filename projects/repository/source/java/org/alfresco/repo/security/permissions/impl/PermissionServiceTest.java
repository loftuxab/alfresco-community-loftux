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

import java.util.HashSet;
import java.util.Set;

import javax.transaction.UserTransaction;

import net.sf.acegisecurity.Authentication;
import net.sf.acegisecurity.GrantedAuthority;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.permissions.AccessStatus;
import org.alfresco.repo.security.permissions.PermissionEntry;
import org.alfresco.repo.security.permissions.PermissionReference;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

public class PermissionServiceTest extends AbstractPermissionTest
{

    
    public PermissionServiceTest()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    public void testAuthenticatedRoleIsPresent()
    {
        runAs("andy");
        Authentication auth = authenticationService.getCurrentAuthentication();
        for (GrantedAuthority authority : auth.getAuthorities())
        {
            if (authority.getAuthority().equals(ROLE_AUTHENTICATED))
            {
                return;
            }
        }
        fail("Missing role ROLE_AUTHENTICATED ");
    }

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
        // testUnset();
    }

    public void testSetNodePermissionEntry()
    {
        Set<SimplePermissionEntry> entries = new HashSet<SimplePermissionEntry>();
        entries.add(new SimplePermissionEntry(rootNodeRef, new SimplePermissionReference(QName.createQName("A", "B"),
                "C"), "user-one", AccessStatus.ALLOWED));
        entries.add(new SimplePermissionEntry(rootNodeRef, SimplePermissionEntry.ALL_PERMISSIONS, "user-two",
                AccessStatus.ALLOWED));
        entries.add(new SimplePermissionEntry(rootNodeRef, new SimplePermissionReference(QName.createQName("D", "E"),
                "F"), SimplePermissionEntry.ALL_AUTHORITIES, AccessStatus.ALLOWED));
        entries.add(new SimplePermissionEntry(rootNodeRef, SimplePermissionEntry.ALL_PERMISSIONS,
                SimplePermissionEntry.ALL_AUTHORITIES, AccessStatus.DENIED));

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
        entries.add(new SimplePermissionEntry(rootNodeRef, SimplePermissionEntry.ALL_PERMISSIONS,
                SimplePermissionEntry.ALL_AUTHORITIES, AccessStatus.ALLOWED));

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
        for (PermissionEntry pe : permissionService.getSetPermissions(rootNodeRef).getPermissionEntries())
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

        permissionService.setPermission(rootNodeRef, "andy", new SimplePermissionReference(QName.createQName("A", "B"),
                "C"), false);
        assertNotNull(permissionService.getSetPermissions(rootNodeRef));
        assertTrue(permissionService.getSetPermissions(rootNodeRef).inheritPermissions());
        assertEquals(rootNodeRef, permissionService.getSetPermissions(rootNodeRef).getNodeRef());
        assertEquals(4, permissionService.getSetPermissions(rootNodeRef).getPermissionEntries().size());

        // delete

        permissionService.deletePermission(rootNodeRef, "andy", new SimplePermissionReference(QName.createQName("A",
                "B"), "C"), false);
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
        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, SimplePermissionEntry.ALL_PERMISSIONS,
                "andy", AccessStatus.ALLOWED));
        permissionService.setPermission(rootNodeRef, "andy", SimplePermissionEntry.ALL_PERMISSIONS, true);
        assertNotNull(permissionService.getSetPermissions(rootNodeRef));
        assertTrue(permissionService.getSetPermissions(rootNodeRef).inheritPermissions());
        assertEquals(rootNodeRef, permissionService.getSetPermissions(rootNodeRef).getNodeRef());
        assertEquals(1, permissionService.getSetPermissions(rootNodeRef).getPermissionEntries().size());
        for (PermissionEntry pe : permissionService.getSetPermissions(rootNodeRef).getPermissionEntries())
        {
            assertEquals("andy", pe.getAuthority());
            assertTrue(pe.isAllowed());
            assertTrue(pe.getPermissionReference().getQName().equals(SimplePermissionEntry.ALL_PERMISSIONS.getQName()));
            assertTrue(pe.getPermissionReference().getName().equals(SimplePermissionEntry.ALL_PERMISSIONS.getName()));
            assertEquals(rootNodeRef, pe.getNodeRef());
        }

        // Set duplicate

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, SimplePermissionEntry.ALL_PERMISSIONS,
                "andy", AccessStatus.ALLOWED));
        assertNotNull(permissionService.getSetPermissions(rootNodeRef));
        assertTrue(permissionService.getSetPermissions(rootNodeRef).inheritPermissions());
        assertEquals(rootNodeRef, permissionService.getSetPermissions(rootNodeRef).getNodeRef());
        assertEquals(1, permissionService.getSetPermissions(rootNodeRef).getPermissionEntries().size());

        // Set new

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, SimplePermissionEntry.ALL_PERMISSIONS,
                "other", AccessStatus.ALLOWED));
        assertNotNull(permissionService.getSetPermissions(rootNodeRef));
        assertTrue(permissionService.getSetPermissions(rootNodeRef).inheritPermissions());
        assertEquals(rootNodeRef, permissionService.getSetPermissions(rootNodeRef).getNodeRef());
        assertEquals(2, permissionService.getSetPermissions(rootNodeRef).getPermissionEntries().size());

        // Deny

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, SimplePermissionEntry.ALL_PERMISSIONS,
                "andy", AccessStatus.DENIED));
        assertNotNull(permissionService.getSetPermissions(rootNodeRef));
        assertTrue(permissionService.getSetPermissions(rootNodeRef).inheritPermissions());
        assertEquals(rootNodeRef, permissionService.getSetPermissions(rootNodeRef).getNodeRef());
        assertEquals(3, permissionService.getSetPermissions(rootNodeRef).getPermissionEntries().size());

        // new

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, new SimplePermissionReference(QName
                .createQName("A", "B"), "C"), "andy", AccessStatus.DENIED));
        assertNotNull(permissionService.getSetPermissions(rootNodeRef));
        assertTrue(permissionService.getSetPermissions(rootNodeRef).inheritPermissions());
        assertEquals(rootNodeRef, permissionService.getSetPermissions(rootNodeRef).getNodeRef());
        assertEquals(4, permissionService.getSetPermissions(rootNodeRef).getPermissionEntries().size());

        permissionService.deletePermission(new SimplePermissionEntry(rootNodeRef, new SimplePermissionReference(QName
                .createQName("A", "B"), "C"), "andy", AccessStatus.DENIED));
        assertNotNull(permissionService.getSetPermissions(rootNodeRef));
        assertTrue(permissionService.getSetPermissions(rootNodeRef).inheritPermissions());
        assertEquals(rootNodeRef, permissionService.getSetPermissions(rootNodeRef).getNodeRef());
        assertEquals(3, permissionService.getSetPermissions(rootNodeRef).getPermissionEntries().size());

        permissionService.deletePermission(new SimplePermissionEntry(rootNodeRef,
                SimplePermissionEntry.ALL_PERMISSIONS, "andy", AccessStatus.DENIED));
        assertNotNull(permissionService.getSetPermissions(rootNodeRef));
        assertTrue(permissionService.getSetPermissions(rootNodeRef).inheritPermissions());
        assertEquals(rootNodeRef, permissionService.getSetPermissions(rootNodeRef).getNodeRef());
        assertEquals(2, permissionService.getSetPermissions(rootNodeRef).getPermissionEntries().size());

        permissionService.deletePermission(new SimplePermissionEntry(rootNodeRef,
                SimplePermissionEntry.ALL_PERMISSIONS, "other", AccessStatus.ALLOWED));
        assertNotNull(permissionService.getSetPermissions(rootNodeRef));
        assertTrue(permissionService.getSetPermissions(rootNodeRef).inheritPermissions());
        assertEquals(rootNodeRef, permissionService.getSetPermissions(rootNodeRef).getNodeRef());
        assertEquals(1, permissionService.getSetPermissions(rootNodeRef).getPermissionEntries().size());

        permissionService.deletePermission(new SimplePermissionEntry(rootNodeRef,
                SimplePermissionEntry.ALL_PERMISSIONS, "andy", AccessStatus.ALLOWED));
        assertNotNull(permissionService.getSetPermissions(rootNodeRef));
        assertTrue(permissionService.getSetPermissions(rootNodeRef).inheritPermissions());
        assertEquals(rootNodeRef, permissionService.getSetPermissions(rootNodeRef).getNodeRef());
        assertEquals(0, permissionService.getSetPermissions(rootNodeRef).getPermissionEntries().size());
    }

    public void testGetSettablePermissionsForType()
    {
        Set<PermissionReference> answer = permissionService.getSettablePermissions(QName.createQName("sys", "base",
                namespacePrefixResolver));
        assertEquals(17, answer.size());

        answer = permissionService.getSettablePermissions(QName.createQName("cm", "ownable", namespacePrefixResolver));
        assertEquals(2, answer.size());

        answer = permissionService.getSettablePermissions(QName.createQName("cm", "content", namespacePrefixResolver));
        assertEquals(21, answer.size());
        
        answer = permissionService.getSettablePermissions(QName.createQName("cm", "folder", namespacePrefixResolver));
        assertEquals(4, answer.size());
    }

    
    
    public void testGetSettablePermissionsForNode()
    {
        QName ownable = QName.createQName("cm", "ownable", namespacePrefixResolver);

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
        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ_PROPERTIES, "andy",
                AccessStatus.ALLOWED));
        runAs("andy");
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ_PROPERTIES, "andy",
                AccessStatus.DENIED));
        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ_PROPERTIES, "andy",
                AccessStatus.ALLOWED));
        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ_PROPERTIES, "andy",
                AccessStatus.DENIED));
        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ_PROPERTIES, "andy",
                AccessStatus.ALLOWED));
        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));

        permissionService.deletePermission(new SimplePermissionEntry(rootNodeRef, READ_PROPERTIES, "andy",
                AccessStatus.DENIED));
        runAs("andy");
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));

        permissionService.deletePermission(new SimplePermissionEntry(rootNodeRef, READ_PROPERTIES, "andy",
                AccessStatus.ALLOWED));
        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
    }

    public void testPermissionGroupOnRoot()
    {
        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ, "andy", AccessStatus.ALLOWED));
        runAs("andy");
        assertTrue(permissionService.hasPermission(rootNodeRef, READ));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ, "andy", AccessStatus.DENIED));
        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ, "andy", AccessStatus.ALLOWED));
        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ, "andy", AccessStatus.DENIED));
        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ, "andy", AccessStatus.ALLOWED));
        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));

        permissionService.deletePermission(new SimplePermissionEntry(rootNodeRef, READ, "andy", AccessStatus.DENIED));
        runAs("andy");
        assertTrue(permissionService.hasPermission(rootNodeRef, READ));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));

        permissionService.deletePermission(new SimplePermissionEntry(rootNodeRef, READ, "andy", AccessStatus.ALLOWED));
        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
    }

    public void testSimplePermissionSimpleInheritance()
    {

        NodeRef n1 = nodeService.createNode(rootNodeRef, ContentModel.ASSOC_CHILDREN,
                QName.createQName("{namespace}one"), ContentModel.TYPE_FOLDER).getChildRef();


        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n1, READ_PROPERTIES));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n1, READ_PROPERTIES));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ_PROPERTIES, "andy",
                AccessStatus.ALLOWED));
        runAs("andy");
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n1, READ_PROPERTIES));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n1, READ_PROPERTIES));
        
        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ_CHILDREN, "andy",
                AccessStatus.ALLOWED));
        runAs("andy");
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(n1, READ_PROPERTIES));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n1, READ_PROPERTIES));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ_PROPERTIES, "andy",
                AccessStatus.DENIED));
        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n1, READ_PROPERTIES));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n1, READ_PROPERTIES));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ_PROPERTIES, "andy",
                AccessStatus.ALLOWED));
        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n1, READ_PROPERTIES));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n1, READ_PROPERTIES));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ_PROPERTIES, "andy",
                AccessStatus.DENIED));
        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n1, READ_PROPERTIES));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n1, READ_PROPERTIES));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ_PROPERTIES, "andy",
                AccessStatus.ALLOWED));
        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n1, READ_PROPERTIES));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n1, READ_PROPERTIES));

        permissionService.deletePermission(new SimplePermissionEntry(rootNodeRef, READ_PROPERTIES, "andy",
                AccessStatus.DENIED));
        runAs("andy");
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(n1, READ_PROPERTIES));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n1, READ_PROPERTIES));

        permissionService.deletePermission(new SimplePermissionEntry(rootNodeRef, READ_PROPERTIES, "andy",
                AccessStatus.ALLOWED));
        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n1, READ_PROPERTIES));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n1, READ_PROPERTIES));
    }

    public void testPermissionGroupSimpleInheritance()
    {
        NodeRef n1 = nodeService.createNode(rootNodeRef, ContentModel.ASSOC_CHILDREN,
                QName.createQName("{namespace}one"), ContentModel.TYPE_FOLDER).getChildRef();

        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        assertFalse(permissionService.hasPermission(n1, READ));
        assertFalse(permissionService.hasPermission(n1, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n1, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(n1, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        assertFalse(permissionService.hasPermission(n1, READ));
        assertFalse(permissionService.hasPermission(n1, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n1, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(n1, READ_CONTENT));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ, "andy", AccessStatus.ALLOWED));
        runAs("andy");
        assertTrue(permissionService.hasPermission(rootNodeRef, READ));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        assertTrue(permissionService.hasPermission(n1, READ));
        assertTrue(permissionService.hasPermission(n1, READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(n1, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(n1, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        assertFalse(permissionService.hasPermission(n1, READ));
        assertFalse(permissionService.hasPermission(n1, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n1, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(n1, READ_CONTENT));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ, "andy", AccessStatus.DENIED));
        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        assertFalse(permissionService.hasPermission(n1, READ));
        assertFalse(permissionService.hasPermission(n1, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n1, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(n1, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        assertFalse(permissionService.hasPermission(n1, READ));
        assertFalse(permissionService.hasPermission(n1, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n1, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(n1, READ_CONTENT));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ, "andy", AccessStatus.ALLOWED));
        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        assertFalse(permissionService.hasPermission(n1, READ));
        assertFalse(permissionService.hasPermission(n1, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n1, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(n1, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        assertFalse(permissionService.hasPermission(n1, READ));
        assertFalse(permissionService.hasPermission(n1, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n1, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(n1, READ_CONTENT));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ, "andy", AccessStatus.DENIED));
        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        assertFalse(permissionService.hasPermission(n1, READ));
        assertFalse(permissionService.hasPermission(n1, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n1, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(n1, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        assertFalse(permissionService.hasPermission(n1, READ));
        assertFalse(permissionService.hasPermission(n1, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n1, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(n1, READ_CONTENT));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ, "andy", AccessStatus.ALLOWED));
        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        assertFalse(permissionService.hasPermission(n1, READ));
        assertFalse(permissionService.hasPermission(n1, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n1, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(n1, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        assertFalse(permissionService.hasPermission(n1, READ));
        assertFalse(permissionService.hasPermission(n1, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n1, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(n1, READ_CONTENT));

        permissionService.deletePermission(new SimplePermissionEntry(rootNodeRef, READ, "andy", AccessStatus.DENIED));
        runAs("andy");
        assertTrue(permissionService.hasPermission(rootNodeRef, READ));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        assertTrue(permissionService.hasPermission(n1, READ));
        assertTrue(permissionService.hasPermission(n1, READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(n1, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(n1, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        assertFalse(permissionService.hasPermission(n1, READ));
        assertFalse(permissionService.hasPermission(n1, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n1, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(n1, READ_CONTENT));

        permissionService.deletePermission(new SimplePermissionEntry(rootNodeRef, READ, "andy", AccessStatus.ALLOWED));
        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        assertFalse(permissionService.hasPermission(n1, READ));
        assertFalse(permissionService.hasPermission(n1, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n1, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(n1, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        assertFalse(permissionService.hasPermission(n1, READ));
        assertFalse(permissionService.hasPermission(n1, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n1, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(n1, READ_CONTENT));
    }

    public void testDenySimplePermisionOnRootNode()
    {
        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ_PROPERTIES, "andy",
                AccessStatus.ALLOWED));
        runAs("andy");
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ_PROPERTIES, "andy",
                AccessStatus.DENIED));
        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));

        permissionService.deletePermission(new SimplePermissionEntry(rootNodeRef, READ_PROPERTIES, "andy",
                AccessStatus.DENIED));
        runAs("andy");
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));

        permissionService.deletePermission(new SimplePermissionEntry(rootNodeRef, READ_PROPERTIES, "andy",
                AccessStatus.ALLOWED));
        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
    }

    public void testDenyPermissionOnRootNOde()
    {

        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ, "andy", AccessStatus.ALLOWED));
        runAs("andy");
        assertTrue(permissionService.hasPermission(rootNodeRef, READ));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ, "andy", AccessStatus.DENIED));
        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));

        permissionService.deletePermission(new SimplePermissionEntry(rootNodeRef, READ, "andy", AccessStatus.DENIED));
        runAs("andy");
        assertTrue(permissionService.hasPermission(rootNodeRef, READ));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));

        permissionService.deletePermission(new SimplePermissionEntry(rootNodeRef, READ, "andy", AccessStatus.ALLOWED));
        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
    }

    public void testComplexDenyOnRootNode()
    {

        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ, "andy", AccessStatus.ALLOWED));
        runAs("andy");
        assertTrue(permissionService.hasPermission(rootNodeRef, READ));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ_PROPERTIES, "andy",
                AccessStatus.DENIED));
        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ_CHILDREN, "andy",
                AccessStatus.ALLOWED));
        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ, "andy", AccessStatus.DENIED));
        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
    }

    public void doNotTest() throws Exception
    {
        runAs("andy");

        UserTransaction tx = serviceRegistry.getUserTransaction();
        tx.begin();
      
        NodeRef n1 = nodeService.createNode(rootNodeRef, ContentModel.ASSOC_CHILDREN,
                QName.createQName("{namespace}one"), ContentModel.TYPE_FOLDER).getChildRef();
        NodeRef n2 = nodeService.createNode(n1, ContentModel.ASSOC_CONTAINS, QName.createQName("{namespace}two"),
                ContentModel.TYPE_FOLDER).getChildRef();
        NodeRef n3 = nodeService.createNode(n2, ContentModel.ASSOC_CONTAINS, QName.createQName("{namespace}three"),
                ContentModel.TYPE_FOLDER).getChildRef();
        NodeRef n4 = nodeService.createNode(n3, ContentModel.ASSOC_CONTAINS, QName.createQName("{namespace}four"),
                ContentModel.TYPE_FOLDER).getChildRef();
        NodeRef n5 = nodeService.createNode(n4, ContentModel.ASSOC_CONTAINS, QName.createQName("{namespace}five"),
                ContentModel.TYPE_FOLDER).getChildRef();
        NodeRef n6 = nodeService.createNode(n5, ContentModel.ASSOC_CONTAINS, QName.createQName("{namespace}six"),
                ContentModel.TYPE_FOLDER).getChildRef();
        NodeRef n7 = nodeService.createNode(n6, ContentModel.ASSOC_CONTAINS, QName.createQName("{namespace}seven"),
                ContentModel.TYPE_FOLDER).getChildRef();
        NodeRef n8 = nodeService.createNode(n7, ContentModel.ASSOC_CONTAINS, QName.createQName("{namespace}eight"),
                ContentModel.TYPE_FOLDER).getChildRef();
        NodeRef n9 = nodeService.createNode(n8, ContentModel.ASSOC_CONTAINS, QName.createQName("{namespace}nine"),
                ContentModel.TYPE_FOLDER).getChildRef();
        NodeRef n10 = nodeService.createNode(n9, ContentModel.ASSOC_CONTAINS, QName.createQName("{namespace}ten"),
                ContentModel.TYPE_FOLDER).getChildRef();

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ, "andy", AccessStatus.ALLOWED));
        // permissionService.setPermission(new SimplePermissionEntry(n9, READ,
        // "andy", AccessStatus.ALLOWED));
        // permissionService.setPermission(new SimplePermissionEntry(n10, READ,
        // "andy", AccessStatus.ALLOWED));

        long start;
        long end;
        long time = 0;
        for (int i = 0; i < 1000; i++)
        {
            // getSession().flush();
            // getSession().clear();
            start = System.nanoTime();
            assertTrue(permissionService.hasPermission(n10, READ));
            end = System.nanoTime();
            time += (end - start);
        }
        assertTrue((time / 1000000000.0) < 60.0);

        time = 0;
        for (int i = 0; i < 1000; i++)
        {
            start = System.nanoTime();
            assertTrue(permissionService.hasPermission(n10, READ));
            end = System.nanoTime();
            time += (end - start);
        }
        System.out.println("Time is "+(time / 1000000000.0));
        assertTrue((time / 1000000000.0) < 2.0);
        

        tx.rollback();
    }

    public void testAllPermissions()
    {
        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, WRITE));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, WRITE));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, SimplePermissionEntry.ALL_PERMISSIONS,
                "andy", AccessStatus.ALLOWED));
        runAs("andy");
        assertTrue(permissionService.hasPermission(rootNodeRef, READ));
        assertTrue(permissionService.hasPermission(rootNodeRef, WRITE));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, WRITE));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ, "andy", AccessStatus.DENIED));
        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertTrue(permissionService.hasPermission(rootNodeRef, WRITE));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, WRITE));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, SimplePermissionEntry.ALL_PERMISSIONS,
                "andy", AccessStatus.DENIED));
        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, WRITE));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, WRITE));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));

    }

    public void testAuthenticatedAuthority()
    {

        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ, ROLE_AUTHENTICATED,
                AccessStatus.ALLOWED));
        runAs("andy");
        assertTrue(permissionService.hasPermission(rootNodeRef, READ));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertTrue(permissionService.hasPermission(rootNodeRef, READ));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ, ROLE_AUTHENTICATED,
                AccessStatus.DENIED));
        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));

        permissionService.deletePermission(new SimplePermissionEntry(rootNodeRef, READ, ROLE_AUTHENTICATED,
                AccessStatus.DENIED));
        runAs("andy");
        assertTrue(permissionService.hasPermission(rootNodeRef, READ));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertTrue(permissionService.hasPermission(rootNodeRef, READ));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));

        permissionService.deletePermission(new SimplePermissionEntry(rootNodeRef, READ, ROLE_AUTHENTICATED,
                AccessStatus.ALLOWED));
        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
    }

    public void testAllAuthorities()
    {

        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ,
                SimplePermissionEntry.ALL_AUTHORITIES, AccessStatus.ALLOWED));
        runAs("andy");
        assertTrue(permissionService.hasPermission(rootNodeRef, READ));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertTrue(permissionService.hasPermission(rootNodeRef, READ));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ,
                SimplePermissionEntry.ALL_AUTHORITIES, AccessStatus.DENIED));
        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));

        permissionService.deletePermission(new SimplePermissionEntry(rootNodeRef, READ,
                SimplePermissionEntry.ALL_AUTHORITIES, AccessStatus.DENIED));
        runAs("andy");
        assertTrue(permissionService.hasPermission(rootNodeRef, READ));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertTrue(permissionService.hasPermission(rootNodeRef, READ));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));

        permissionService.deletePermission(new SimplePermissionEntry(rootNodeRef, READ,
                SimplePermissionEntry.ALL_AUTHORITIES, AccessStatus.ALLOWED));
        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
    }

    public void testAllPermissionsAllAuthorities()
    {

        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, WRITE));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, WRITE));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, SimplePermissionEntry.ALL_PERMISSIONS,
                SimplePermissionEntry.ALL_AUTHORITIES, AccessStatus.ALLOWED));
        runAs("andy");
        assertTrue(permissionService.hasPermission(rootNodeRef, READ));
        assertTrue(permissionService.hasPermission(rootNodeRef, WRITE));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertTrue(permissionService.hasPermission(rootNodeRef, READ));
        assertTrue(permissionService.hasPermission(rootNodeRef, WRITE));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ,
                SimplePermissionEntry.ALL_AUTHORITIES, AccessStatus.DENIED));
        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertTrue(permissionService.hasPermission(rootNodeRef, WRITE));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertTrue(permissionService.hasPermission(rootNodeRef, WRITE));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, SimplePermissionEntry.ALL_PERMISSIONS,
                SimplePermissionEntry.ALL_AUTHORITIES, AccessStatus.DENIED));
        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, WRITE));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, WRITE));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
    }

    public void testGroupAndUserInteraction()
    {

        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ, "andy", AccessStatus.ALLOWED));
        runAs("andy");
        assertTrue(permissionService.hasPermission(rootNodeRef, READ));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ, ROLE_AUTHENTICATED,
                AccessStatus.ALLOWED));
        runAs("andy");
        assertTrue(permissionService.hasPermission(rootNodeRef, READ));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertTrue(permissionService.hasPermission(rootNodeRef, READ));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ_PROPERTIES, "andy",
                AccessStatus.DENIED));
        runAs("andy");
        assertTrue(permissionService.hasPermission(rootNodeRef, READ));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertTrue(permissionService.hasPermission(rootNodeRef, READ));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ_CHILDREN, "andy",
                AccessStatus.ALLOWED));
        runAs("andy");
        assertTrue(permissionService.hasPermission(rootNodeRef, READ));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertTrue(permissionService.hasPermission(rootNodeRef, READ));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ, "andy", AccessStatus.DENIED));
        runAs("andy");
        assertTrue(permissionService.hasPermission(rootNodeRef, READ));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertTrue(permissionService.hasPermission(rootNodeRef, READ));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
    }

    public void testInheritPermissions()
    {

        NodeRef n1 = nodeService.createNode(rootNodeRef, ContentModel.ASSOC_CHILDREN,
                QName.createQName("{namespace}one"), ContentModel.TYPE_FOLDER).getChildRef();
        NodeRef n2 = nodeService.createNode(n1, ContentModel.ASSOC_CONTAINS, QName.createQName("{namespace}two"),
                ContentModel.TYPE_FOLDER).getChildRef();

        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ, "andy", AccessStatus.ALLOWED));
        permissionService.setPermission(new SimplePermissionEntry(n1, READ, "andy", AccessStatus.ALLOWED));

        runAs("andy");
        assertTrue(permissionService.hasPermission(n2, READ));
        assertTrue(permissionService.hasPermission(n2, READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(n2, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(n2, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(n2, READ));
        assertFalse(permissionService.hasPermission(n2, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n2, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(n2, READ_CONTENT));

        permissionService.setInheritParentPermissions(n2, false);

        runAs("andy");
        assertFalse(permissionService.hasPermission(n2, READ));
        assertFalse(permissionService.hasPermission(n2, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n2, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(n2, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(n2, READ));
        assertFalse(permissionService.hasPermission(n2, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n2, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(n2, READ_CONTENT));

        permissionService.setInheritParentPermissions(n2, true);

        runAs("andy");
        assertTrue(permissionService.hasPermission(n2, READ));
        assertTrue(permissionService.hasPermission(n2, READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(n2, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(n2, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(n2, READ));
        assertFalse(permissionService.hasPermission(n2, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n2, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(n2, READ_CONTENT));

    }

    public void testAncestorRequirementAndInheritance()
    {

        NodeRef n1 = nodeService.createNode(rootNodeRef, ContentModel.ASSOC_CHILDREN,
                QName.createQName("{namespace}one"), ContentModel.TYPE_FOLDER).getChildRef();
        NodeRef n2 = nodeService.createNode(n1, ContentModel.ASSOC_CONTAINS, QName.createQName("{namespace}two"),
                ContentModel.TYPE_FOLDER).getChildRef();

        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ_CHILDREN, "andy",
                AccessStatus.ALLOWED));
        permissionService.setPermission(new SimplePermissionEntry(n1, READ_CHILDREN, "andy", AccessStatus.ALLOWED));
        permissionService.setPermission(new SimplePermissionEntry(n2, READ_PROPERTIES, "andy", AccessStatus.ALLOWED));

        runAs("andy");
        assertTrue(permissionService.hasPermission(n2, READ));
        assertTrue(permissionService.hasPermission(n2, READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(n2, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(n2, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(n2, READ));
        assertFalse(permissionService.hasPermission(n2, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n2, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(n2, READ_CONTENT));

        permissionService.setPermission(new SimplePermissionEntry(n1, READ_CHILDREN, "andy", AccessStatus.DENIED));
        permissionService.setInheritParentPermissions(n2, false);

        runAs("andy");
        assertFalse(permissionService.hasPermission(n2, READ));
        assertTrue(permissionService.hasPermission(n2, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n2, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(n2, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(n2, READ));
        assertFalse(permissionService.hasPermission(n2, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n2, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(n2, READ_CONTENT));

        permissionService.setInheritParentPermissions(n2, true);

        runAs("andy");
        assertFalse(permissionService.hasPermission(n2, READ));
        assertFalse(permissionService.hasPermission(n2, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n2, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(n2, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(n2, READ));
        assertFalse(permissionService.hasPermission(n2, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n2, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(n2, READ_CONTENT));
    }

    public void testEffectiveComposite()
    {

        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ_CHILDREN, "andy",
                AccessStatus.ALLOWED));
        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ_PROPERTIES, "andy",
                AccessStatus.ALLOWED));

        runAs("andy");
        assertTrue(permissionService.hasPermission(rootNodeRef, READ));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));

    }

    public void testContentPermissions()
    {
        NodeRef n1 = nodeService.createNode(rootNodeRef, ContentModel.ASSOC_CHILDREN,
                QName.createQName("{namespace}one"), ContentModel.TYPE_FOLDER).getChildRef();
        NodeRef n2 = nodeService.createNode(n1, ContentModel.ASSOC_CONTAINS, QName.createQName("{namespace}two"),
                ContentModel.TYPE_CONTENT).getChildRef();

        runAs("andy");
        assertFalse(permissionService.hasPermission(n2, READ));
        assertFalse(permissionService.hasPermission(n2, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n2, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(n2, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(n2, READ));
        assertFalse(permissionService.hasPermission(n2, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n2, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(n2, READ_CONTENT));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ_CHILDREN, "andy",
                AccessStatus.ALLOWED));
        permissionService.setPermission(new SimplePermissionEntry(n1, READ_CHILDREN, "andy", AccessStatus.ALLOWED));
        permissionService.setPermission(new SimplePermissionEntry(n2, READ_CHILDREN, "andy", AccessStatus.ALLOWED));
        permissionService.setPermission(new SimplePermissionEntry(n2, READ_PROPERTIES, "andy", AccessStatus.ALLOWED));

        runAs("andy");
        assertFalse(permissionService.hasPermission(n2, READ));
        assertTrue(permissionService.hasPermission(n2, READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(n2, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(n2, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(n2, READ));
        assertFalse(permissionService.hasPermission(n2, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n2, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(n2, READ_CONTENT));

        permissionService.setPermission(new SimplePermissionEntry(n2, READ_CONTENT, "andy", AccessStatus.ALLOWED));

        runAs("andy");
        assertTrue(permissionService.hasPermission(n2, READ));
        assertTrue(permissionService.hasPermission(n2, READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(n2, READ_CHILDREN));
        assertTrue(permissionService.hasPermission(n2, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(n2, READ));
        assertFalse(permissionService.hasPermission(n2, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n2, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(n2, READ_CONTENT));

        permissionService.deletePermission(new SimplePermissionEntry(n2, READ_CHILDREN, "andy", AccessStatus.ALLOWED));
        permissionService
                .deletePermission(new SimplePermissionEntry(n2, READ_PROPERTIES, "andy", AccessStatus.ALLOWED));
        permissionService.deletePermission(new SimplePermissionEntry(n2, READ_CONTENT, "andy", AccessStatus.ALLOWED));

        runAs("andy");
        assertFalse(permissionService.hasPermission(n2, READ));
        assertFalse(permissionService.hasPermission(n2, READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(n2, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(n2, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(n2, READ));
        assertFalse(permissionService.hasPermission(n2, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n2, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(n2, READ_CONTENT));

        permissionService.setPermission(new SimplePermissionEntry(n2, READ, "andy", AccessStatus.ALLOWED));

        runAs("andy");
        assertTrue(permissionService.hasPermission(n2, READ));
        assertTrue(permissionService.hasPermission(n2, READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(n2, READ_CHILDREN));
        assertTrue(permissionService.hasPermission(n2, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(n2, READ));
        assertFalse(permissionService.hasPermission(n2, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(n2, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(n2, READ_CONTENT));

    }

    public void testAllPermissionSet()
    {
        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, FULL_CONTROL, "andy",
                AccessStatus.ALLOWED));

        runAs("andy");
        assertTrue(permissionService.hasPermission(rootNodeRef, READ));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));

        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, FULL_CONTROL, "andy",
                AccessStatus.DENIED));
        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ, "andy", AccessStatus.ALLOWED));
        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ_CHILDREN, "andy",
                AccessStatus.ALLOWED));
        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ_PROPERTIES, "andy",
                AccessStatus.ALLOWED));

        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));

        permissionService.deletePermission(new SimplePermissionEntry(rootNodeRef, FULL_CONTROL, "andy",
                AccessStatus.DENIED));

        runAs("andy");
        assertTrue(permissionService.hasPermission(rootNodeRef, READ));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, READ));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_PROPERTIES));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, READ_CONTENT));

    }
    
    public void testChildrenRequirements()
    {
        assertEquals(1, nodeService.getChildAssocs(rootNodeRef).size());
        
        runAs("andy");
        assertFalse(permissionService.hasPermission(rootNodeRef, DELETE));
        assertFalse(permissionService.hasPermission(rootNodeRef, DELETE_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, DELETE_NODE));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, DELETE));
        assertFalse(permissionService.hasPermission(rootNodeRef, DELETE_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, DELETE_NODE));
        
        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, READ, "andy",
                AccessStatus.ALLOWED));
        
        permissionService.setPermission(new SimplePermissionEntry(rootNodeRef, DELETE, "andy",
                AccessStatus.ALLOWED));
        
        runAs("andy");
        assertTrue(permissionService.hasPermission(rootNodeRef, DELETE_CHILDREN));
        assertTrue(permissionService.hasPermission(rootNodeRef, DELETE_NODE));
        assertTrue(permissionService.hasPermission(rootNodeRef, DELETE));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, DELETE));
        assertFalse(permissionService.hasPermission(rootNodeRef, DELETE_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, DELETE_NODE));
        
        runAs("andy");
        assertTrue(permissionService.hasPermission(systemNodeRef, DELETE_CHILDREN));
        assertTrue(permissionService.hasPermission(systemNodeRef, DELETE_NODE));
        assertTrue(permissionService.hasPermission(systemNodeRef, DELETE));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(systemNodeRef, DELETE));
        assertFalse(permissionService.hasPermission(systemNodeRef, DELETE_CHILDREN));
        assertFalse(permissionService.hasPermission(systemNodeRef, DELETE_NODE));
        
        
        permissionService.setPermission(new SimplePermissionEntry(systemNodeRef, DELETE, "andy",
                AccessStatus.DENIED));
        
        runAs("andy");
        assertTrue(permissionService.hasPermission(rootNodeRef, DELETE_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, DELETE_NODE));
        assertFalse(permissionService.hasPermission(rootNodeRef, DELETE));
        assertTrue(permissionService.hasPermission(rootNodeRef, READ_CHILDREN));
        runAs("lemur");
        assertFalse(permissionService.hasPermission(rootNodeRef, DELETE));
        assertFalse(permissionService.hasPermission(rootNodeRef, DELETE_CHILDREN));
        assertFalse(permissionService.hasPermission(rootNodeRef, DELETE_NODE));
        
    }
}

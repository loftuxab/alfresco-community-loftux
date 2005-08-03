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
package org.alfresco.repo.security.permissions.impl.hibernate;

import java.io.Serializable;

import org.alfresco.repo.domain.NodeKey;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.BaseSpringTest;

/**
 * Test persistence and retrieval of Hibernate-specific implementations of the
 * {@link org.alfresco.repo.domain.Node} interface
 * 
 * @author Andy Hind
 */
public class HibernatePermissionTest extends BaseSpringTest
{    
    public HibernatePermissionTest()
    {
    }
    
    protected void onSetUpInTransaction() throws Exception
    {
        
    }
    
    protected void onTearDownInTransaction()
    {
        // force a flush to ensure that the database updates succeed
        getSession().flush();
        getSession().clear();
    }
   

	public void testSimpleNodePermission() throws Exception
	{
        // create a new Node
        NodePermissionEntry nodePermission = new NodePermissionEntryImpl();
		NodeKey key = new NodeKey("Random Protocol", "Random Identifier", "AAA");
        nodePermission.setNodeKey(key);
        nodePermission.setInherits(true);
        
        Serializable id = getSession().save(nodePermission);
			
        // throw the reference away and get the a new one for the id
        nodePermission = (NodePermissionEntry) getSession().load(NodePermissionEntryImpl.class, id);
        assertNotNull("Node not found", nodePermission);
        assertTrue(nodePermission.getInherits());
        
        // Update inherits 
        
        nodePermission.setInherits(false);
        id = getSession().save(nodePermission);
        
        // throw the reference away and get the a new one for the id
        nodePermission = (NodePermissionEntry) getSession().load(NodePermissionEntryImpl.class, id);
        assertNotNull("Node not found", nodePermission);
        assertFalse(nodePermission.getInherits());
	}
    
    public void testSimplePermissionReference()
    {
        PermissionReference permissionReference = new PermissionReferenceImpl();
        permissionReference.setName("Test");
        permissionReference.setTypeQName(QName.createQName("TestUri", "TestName"));
        
        Serializable id = getSession().save(permissionReference);
        
        // throw the reference away and get the a new one for the id
        permissionReference = (PermissionReference) getSession().load(PermissionReferenceImpl.class, id);
        assertNotNull("Node not found", permissionReference);
        assertEquals("Test", permissionReference.getName());
        assertEquals("TestUri", permissionReference.getTypeQName().getNamespaceURI());
        assertEquals("TestName", permissionReference.getTypeQName().getLocalName());
        
        // Update
        
        permissionReference.setName("Test2");
        permissionReference.setTypeQName(QName.createQName("TestUri2", "TestName2"));
      
        // Throw the reference away and get the a new one for the id
        permissionReference = (PermissionReference) getSession().load(PermissionReferenceImpl.class, id);
        assertNotNull("Node not found", permissionReference);
        assertEquals("Test2", permissionReference.getName());
        assertEquals("TestUri2", permissionReference.getTypeQName().getNamespaceURI());
        assertEquals("TestName2", permissionReference.getTypeQName().getLocalName());
    }
    
    public void testSimpleRecipient()
    {
        Recipient recipient = new RecipientImpl();
        recipient.setRecipient("Test");
        recipient.getExternalKeys().add("One");
        
        Serializable id = getSession().save(recipient);
        
        // throw the reference away and get the a new one for the id
        recipient = (Recipient) getSession().load(RecipientImpl.class, id);
        assertNotNull("Node not found", recipient);
        assertEquals("Test", recipient.getRecipient());
        assertEquals(1, recipient.getExternalKeys().size());
        
        
        // Update
        
        recipient.setRecipient("Test2");
        recipient.getExternalKeys().add("Two");
      
        // throw the reference away and get the a new one for the id
        recipient = (Recipient) getSession().load(RecipientImpl.class, id);
        assertNotNull("Node not found", recipient);
        assertEquals("Test2", recipient.getRecipient());
        assertEquals(2, recipient.getExternalKeys().size());
        
        
        // complex
        
        recipient.setRecipient("Test3");
        recipient.getExternalKeys().add("Three");
        recipient.getExternalKeys().remove("One");
        recipient.getExternalKeys().remove("Two");
        
        // Throw the reference away and get the a new one for the id
        recipient = (Recipient) getSession().load(RecipientImpl.class, id);
        assertNotNull("Node not found", recipient);
        assertEquals("Test3", recipient.getRecipient());
        assertEquals(1, recipient.getExternalKeys().size());
        
        
    }
    
    public void testNodePermissionEntry()
    {
        //      create a new Node
        NodePermissionEntry nodePermission = new NodePermissionEntryImpl();
        NodeKey key = new NodeKey("Random Protocol", "Random Identifier", "AAA");
        nodePermission.setNodeKey(key);
        nodePermission.setInherits(true);
        
        Recipient recipient = new RecipientImpl();
        recipient.setRecipient("Test");
        recipient.getExternalKeys().add("One");
        
        PermissionReference permissionReference = new PermissionReferenceImpl();
        permissionReference.setName("Test");
        permissionReference.setTypeQName(QName.createQName("TestUri", "TestName"));
        
        PermissionEntry permissionEntry = PermissionEntryImpl.create(nodePermission, permissionReference, recipient, true);
        
        Serializable idNodePermision = getSession().save(nodePermission);
        getSession().save(recipient);
        getSession().save(permissionReference);
        Serializable idPermEnt = getSession().save(permissionEntry);
        
        permissionEntry =  (PermissionEntry) getSession().load(PermissionEntryImpl.class, idPermEnt);
        assertNotNull("Permission entry not found", permissionEntry);
        assertTrue(permissionEntry.isAllowed());
        assertNotNull(permissionEntry.getNodePermissionEntry());
        assertTrue(permissionEntry.getNodePermissionEntry().getInherits());
        assertNotNull(permissionEntry.getPermissionReference());
        assertEquals("Test", permissionEntry.getPermissionReference().getName());
        assertNotNull(permissionEntry.getRecipient());
        assertEquals("Test", permissionEntry.getRecipient().getRecipient());
        assertEquals(1, permissionEntry.getRecipient().getExternalKeys().size());
        
        // Check traversal down
        
        nodePermission = (NodePermissionEntry) getSession().load(NodePermissionEntryImpl.class, idNodePermision);
        assertEquals(1, nodePermission.getPermissionEntries().size());
        
        permissionEntry.delete();
        getSession().delete(permissionEntry);
        
        nodePermission = (NodePermissionEntry) getSession().load(NodePermissionEntryImpl.class, idNodePermision);
        assertEquals(0, nodePermission.getPermissionEntries().size());   
    }
}
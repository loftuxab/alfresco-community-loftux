/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.module.org_alfresco_module_dod5015.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.DOD5015Model;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementModel;
import org.alfresco.module.org_alfresco_module_dod5015.capability.Capability;
import org.alfresco.module.org_alfresco_module_dod5015.security.RecordsManagementSecurityService;
import org.alfresco.module.org_alfresco_module_dod5015.security.Role;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.util.BaseSpringTest;
import org.alfresco.util.GUID;
import org.alfresco.util.PropertyMap;

/**
 * Event service implementation unit test
 * 
 * @author Roy Wetherall
 */
public class RecordsManagementSecurityServiceImplTest extends BaseSpringTest implements RecordsManagementModel
{    
    protected static StoreRef SPACES_STORE = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
    
	private NodeService nodeService;
	private AuthenticationService authenticationService;
	private AuthorityService authorityService;
	private PersonService personService;
	private RecordsManagementSecurityService rmSecurityService;
	
	@Override
	protected void onSetUpInTransaction() throws Exception 
	{
		super.onSetUpInTransaction();

		// Get the service required in the tests
		this.nodeService = (NodeService)this.applicationContext.getBean("NodeService"); 
		this.authenticationService = (AuthenticationService)this.applicationContext.getBean("AuthenticationService");
		this.personService = (PersonService)this.applicationContext.getBean("PersonService");
		this.authorityService = (AuthorityService)this.applicationContext.getBean("authorityService");
		this.rmSecurityService = (RecordsManagementSecurityService)this.applicationContext.getBean("RecordsManagementSecurityService");

		// Set the current security context as admin
		AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
	}
	
	public void testRoles()
	{
	    NodeRef rmRootNode = createRMRootNodeRef();
	    
	    Set<Role> roles = rmSecurityService.getRoles(rmRootNode);
	    assertNotNull(roles);
	    assertEquals(0, roles.size());
	    
	    rmSecurityService.createRole(rmRootNode, "MyRole", "My Role", getListOfCapabilities(5));
	    
	    roles = rmSecurityService.getRoles(rmRootNode);
        assertNotNull(roles);
        assertEquals(1, roles.size());
        
        Role role = new ArrayList<Role>(roles).get(0);
        assertNotNull(role);
        assertEquals("MyRole", role.getName());
        assertEquals("My Role", role.getDisplayLabel());
        assertNotNull(role.getCapabilities());
        assertEquals(5, role.getCapabilities().size());
        assertNotNull(role.getRoleGroupName());
        
        // Add a user to the role
        String userName = createAndAddUserToRole(role.getRoleGroupName());
        
        // Check that we can retrieve the users roles
        Set<Role> userRoles = rmSecurityService.getRolesByUser(rmRootNode, userName);
        assertNotNull(userRoles);
        assertEquals(1, userRoles.size());
        Role userRole  = userRoles.iterator().next();
        assertEquals("MyRole", userRole.getName());
        
        try
        {
            rmSecurityService.createRole(rmRootNode, "MyRole", "My Role", getListOfCapabilities(5));
            fail("Duplicate role id's not allowed for the same rm root node");
        }
        catch (AlfrescoRuntimeException e)
        {
            // Expected
        }
        
        rmSecurityService.createRole(rmRootNode, "MyRole2", "My Role", getListOfCapabilities(5));
        
        roles = rmSecurityService.getRoles(rmRootNode);
        assertNotNull(roles);
        assertEquals(2, roles.size());    
        
        Set<Capability> list = getListOfCapabilities(3, 4);
        assertEquals(3, list.size());
        
        Role result = rmSecurityService.updateRole(rmRootNode, "MyRole", "SomethingDifferent", list);
        
        assertNotNull(result);
        assertEquals("MyRole", result.getName());
        assertEquals("SomethingDifferent", result.getDisplayLabel());
        assertNotNull(result.getCapabilities());
        assertEquals(3, result.getCapabilities().size());
        assertNotNull(result.getRoleGroupName());
	 
        roles = rmSecurityService.getRoles(rmRootNode);
        assertNotNull(roles);
        assertEquals(2, roles.size());
        
        for (Role role2 : roles)
        {
            if (role2.equals("MyRole") == true)
            {
                assertNotNull(role2);
                assertEquals("MyRole", role2.getName());
                assertEquals("SomethingDifferent", role2.getDisplayLabel());
                assertNotNull(role2.getCapabilities());
                assertEquals(3, role2.getCapabilities().size());
                assertNotNull(role2.getRoleGroupName());
            }
        }
        
        rmSecurityService.deleteRole(rmRootNode, "MyRole2");
        
        roles = rmSecurityService.getRoles(rmRootNode);
        assertNotNull(roles);
        assertEquals(1, roles.size());
	}
	
	private Set<Capability> getListOfCapabilities(int size)
	{
	    return getListOfCapabilities(size, 0);
	}
	
	private Set<Capability> getListOfCapabilities(int size, int offset)
	{
	    Set<Capability> result = new HashSet<Capability>(size);
	    Set<Capability> caps = rmSecurityService.getCapabilities();
	    int count = 0;
	    for (Capability cap : caps)
        {
            if (count < size+offset)
            {
                if (count >= offset)
                {
                    result.add(cap);
                }
            }
            else
            {
                break;
            }
            count ++;
        }
	    return result;
	}
	
	private NodeRef createRMRootNodeRef()
	{
	    NodeRef root = this.nodeService.getRootNode(new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore"));
	    return this.nodeService.createNode(root, ContentModel.ASSOC_CHILDREN, ContentModel.ASSOC_CHILDREN, DOD5015Model.TYPE_FILE_PLAN).getChildRef();
	}
	
	private String createAndAddUserToRole(String role)
	{
	    // Create an athentication
	    String userName = GUID.generate();
	    authenticationService.createAuthentication(userName, "PWD".toCharArray());
	            
	    // Create a person
        PropertyMap ppOne = new PropertyMap(4);
        ppOne.put(ContentModel.PROP_USERNAME, userName);
        ppOne.put(ContentModel.PROP_FIRSTNAME, "firstName");
        ppOne.put(ContentModel.PROP_LASTNAME, "lastName");
        ppOne.put(ContentModel.PROP_EMAIL, "email@email.com");
        ppOne.put(ContentModel.PROP_JOBTITLE, "jobTitle");        
        personService.createPerson(ppOne);
        
        // Assign the new user to the role passed
        authorityService.addAuthority(role, userName);
	    
        return userName;
	}
}

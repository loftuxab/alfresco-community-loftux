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
package org.alfresco.webservice.test;

import org.alfresco.webservice.accesscontrol.ACE;
import org.alfresco.webservice.accesscontrol.ACL;
import org.alfresco.webservice.accesscontrol.AccessControlServiceSoapBindingStub;
import org.alfresco.webservice.accesscontrol.AccessStatus;
import org.alfresco.webservice.accesscontrol.GetClassPermissionsResult;
import org.alfresco.webservice.accesscontrol.GetPermissionsResult;
import org.alfresco.webservice.accesscontrol.HasPermissionsResult;
import org.alfresco.webservice.accesscontrol.OwnerResult;
import org.alfresco.webservice.administration.NewUserDetails;
import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.types.Predicate;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.WebServiceFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * @author Roy Wetherall
 */
public class AccessControlServiceSystemTest extends BaseWebServiceSystemTest
{
    private static Log logger = LogFactory.getLog(AccessControlServiceSystemTest.class);
    
    private String userName1 = null;
    private String userName2 = null;
    
    private AccessControlServiceSoapBindingStub accessControlService = WebServiceFactory.getAccessControlService();
    
    private void createUsers() throws Exception
    {
        this.userName1 = "user1" + System.currentTimeMillis();
        this.userName2 = "user2" + System.currentTimeMillis();
        
        // Create some users we can user in the tests
        String homeFolder = store.getScheme() + "://" + store.getAddress() + "/" + folderReference.getUuid();      
        NewUserDetails[] newUsers = new NewUserDetails[] {
                new NewUserDetails(
                        this.userName1, 
                        "password",
                        createPersonProperties(homeFolder, "first", "middle", "last", "email", "org")),
                new NewUserDetails(
                        this.userName2, 
                        "password",
                        createPersonProperties(homeFolder, "first", "middle", "last", "email", "org")) };

        // Create the new users
        WebServiceFactory.getAdministrationService().createUsers(newUsers);
    }
    
    private NamedValue[] createPersonProperties(
            String homeFolder,
            String firstName, 
            String middleName, 
            String lastName, 
            String email,
            String orgId)
    {
        // Create the new user objects
        return new NamedValue[] {
                new NamedValue(Constants.PROP_USER_HOMEFOLDER, false, homeFolder, null),
                new NamedValue(Constants.PROP_USER_FIRSTNAME, false, firstName, null),
                new NamedValue(Constants.PROP_USER_MIDDLENAME, false, middleName, null),
                new NamedValue(Constants.PROP_USER_LASTNAME, false, lastName, null),
                new NamedValue(Constants.PROP_USER_EMAIL, false, email, null),
                new NamedValue(Constants.PROP_USER_ORGID, false, orgId, null) };
    }
    
    private void removeUsers() throws Exception
    {
        String[] userNames = new String[]{this.userName1, this.userName2};
        WebServiceFactory.getAdministrationService().deleteUsers(userNames);       
    }
    
    /**
     * Test getting, setting and removing permissions
     */
    public void testGetSetRemoveACEs() throws Exception
    {
        // Resolve the predicate and create the test users
        Predicate predicate = new Predicate(new Reference[]{BaseWebServiceSystemTest.contentReference}, null, null);
        createUsers();
        
        // Get the ACL for the content node
        ACL[] acls = this.accessControlService.getACLs(predicate, null);
        assertNotNull(acls);
        assertEquals(1, acls.length);
        
        // Check the details of the ace returned
        ACL acl = acls[0];
        assertEquals(BaseWebServiceSystemTest.contentReference.getUuid(), acl.getReference().getUuid());
        assertEquals(true, acl.isInheritPermissions());
        assertNull(acl.getAces());
        
        // Add some acls to the content
        ACE[] aces1 = new ACE[]
        {
           new ACE(this.userName1, Constants.READ, AccessStatus.acepted),
           new ACE(this.userName2, Constants.WRITE, AccessStatus.acepted)
        };
        ACL[] acls1 = this.accessControlService.addACEs(predicate, aces1);
        
        // Check the details of the addACE result
        assertNotNull(acls1);
        assertEquals(1, acls1.length);
        ACL acl1 = acls1[0];
        assertEquals(BaseWebServiceSystemTest.contentReference.getUuid(), acl1.getReference().getUuid());
        assertEquals(true, acl1.isInheritPermissions());
        assertNotNull(acl1.getAces());
        assertEquals(2, acl1.getAces().length);
        for (ACE ace1 : acl1.getAces())
        {
            if (ace1.getAuthority().equals(this.userName1) == true)
            {
                assertEquals(Constants.READ, ace1.getPermission());
                assertEquals(AccessStatus.acepted, ace1.getAccessStatus());
            }
            else if (ace1.getAuthority().equals(this.userName2) == true)
            {
                assertEquals(Constants.WRITE, ace1.getPermission());
                assertEquals(AccessStatus.acepted, ace1.getAccessStatus());
            }
            else
            {
                fail("I wasn't expecting anything else here");
            }
        }
        
        // Double check the get works
        ACL[] acls3 = this.accessControlService.getACLs(predicate, null);
        assertNotNull(acls3);
        assertEquals(1, acls3.length);
        assertNotNull(acls3[0].getAces());
        assertEquals(2, acls3[0].getAces().length);
        
        // Remove an ACE
        ACE[] aces2 = new ACE[]
          {
             new ACE(this.userName1, Constants.READ, AccessStatus.acepted)
          };
        ACL[] acls4 = this.accessControlService.removeACEs(predicate, aces2);
        assertNotNull(acls4);
        assertEquals(1, acls4.length);
        assertNotNull(acls4[0].getAces());
        assertEquals(1, acls4[0].getAces().length);
        
        // Double check get
        ACL[] acls5 = this.accessControlService.getACLs(predicate, null);
        assertNotNull(acls5);
        assertEquals(1, acls5.length);
        assertNotNull(acls5[0].getAces());
        assertEquals(1, acls5[0].getAces().length);
        
        // Remove all
        ACL[] acls6 = this.accessControlService.removeACEs(predicate, null);
        assertNotNull(acls6);
        assertEquals(1, acls6.length);
        assertNull(acls6[0].getAces());
        
        // Remove the users added
        removeUsers();
    }
    
    /**
     * Test getPermissions
     * 
     * @throws Exception
     */
    public void testGetPermissions() throws Exception
    {
        // Create predicate
        Predicate predicate = new Predicate(new Reference[]{BaseWebServiceSystemTest.contentReference}, null, null);
        
        // Get the permissions that can be set
        GetPermissionsResult[] results = this.accessControlService.getPermissions(predicate);
        
        // Check the result
        assertNotNull(results);
        assertEquals(1, results.length);
        GetPermissionsResult result = results[0];
        assertEquals(BaseWebServiceSystemTest.contentReference.getUuid(), result.getReference().getUuid());
        assertNotNull(result.getPermissions());
               
        if (logger.isDebugEnabled() == true)
        {
            System.out.println("Node permissions:");
            for (String permission : result.getPermissions())
            {
                System.out.println(permission);
            }
            System.out.println("\n");
        }
    }
    
    /**
     * Test getClassPermissions
     * 
     * @throws Exception
     */
    public void testGetClassPermissions() throws Exception
    {
        // Get the permissions that can be set
        GetClassPermissionsResult[] results = this.accessControlService.getClassPermissions(new String[]{Constants.TYPE_FOLDER});
        
        // Check the result
        assertNotNull(results);
        assertEquals(1, results.length);
        GetClassPermissionsResult result = results[0];
        assertEquals(Constants.TYPE_FOLDER, result.getClassName());
        assertNotNull(result.getPermissions());
               
        if (logger.isDebugEnabled() == true)
        {
            System.out.println("Class permissions:");
            for (String permission : result.getPermissions())
            {
                System.out.println(permission);
            }
            System.out.println("\n");
        }
        
    }
    
    /**
     * Test hasPermissions
     * 
     * @throws Exception
     */
    public void testHasPermissions() throws Exception
    {
        Predicate predicate = convertToPredicate(BaseWebServiceSystemTest.contentReference);
        
        HasPermissionsResult[] results = this.accessControlService.hasPermissions(predicate, new String[]{Constants.WRITE});
        assertNotNull(results);
        assertEquals(1, results.length);
        
        HasPermissionsResult result = results[0];
        assertEquals(Constants.WRITE, result.getPermission());
        assertEquals(BaseWebServiceSystemTest.contentReference.getUuid(), result.getReference().getUuid());
        assertEquals(AccessStatus.acepted, result.getAccessStatus());
    }
    
    /**
     * Test setInheritPermissions
     * 
     * @throws Exception
     */
    public void testSetInheritPermissions() throws Exception
    {
        ACL[] acls = this.accessControlService.setInheritPermission(convertToPredicate(BaseWebServiceSystemTest.contentReference), false);
        assertNotNull(acls);
        assertEquals(1, acls.length);
        ACL acl = acls[0];
        assertEquals(BaseWebServiceSystemTest.contentReference.getUuid(), acl.getReference().getUuid());
        assertFalse(acl.isInheritPermissions());
    }
    
    /**
     * Test setOwnable and getOwnable
     * @throws Exception
     */
    public void testSetGetOwnable() throws Exception
    {
        // Create a couple of users
        createUsers();
        
        // Check the current owner
        OwnerResult[] results = this.accessControlService.getOwners(convertToPredicate(BaseWebServiceSystemTest.contentReference));
        assertNotNull(results);
        assertEquals(1, results.length);
        OwnerResult result = results[0];
        assertEquals(BaseWebServiceSystemTest.contentReference.getUuid(), result.getReference().getUuid());
        assertEquals("admin", result.getOwner());
        
        // Reset the owner
        OwnerResult[] results2 = this.accessControlService.setOwners(convertToPredicate(BaseWebServiceSystemTest.contentReference), this.userName1);
        assertNotNull(results2);
        assertEquals(1, results2.length);
        OwnerResult result2 = results2[0];
        assertEquals(BaseWebServiceSystemTest.contentReference.getUuid(), result2.getReference().getUuid());
        assertEquals(this.userName1, result2.getOwner());        
        
        // Remove the created users
        removeUsers();      
    }
}

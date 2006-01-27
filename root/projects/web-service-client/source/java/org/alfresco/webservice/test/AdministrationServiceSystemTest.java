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

import org.alfresco.webservice.administration.NewUserDetails;
import org.alfresco.webservice.administration.UserDetails;
import org.alfresco.webservice.administration.UserQueryResults;
import org.alfresco.webservice.repository.RepositoryServiceLocator;
import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.types.QueryConfiguration;
import org.alfresco.webservice.util.AuthenticationUtils;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.WebServiceFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Administration service system test
 * 
 * @author Roy Wetherall
 */
public class AdministrationServiceSystemTest extends BaseWebServiceSystemTest
{
    private static Log logger = LogFactory.getLog(AdministrationServiceSystemTest.class);
    
    public void testGetUsersBatching() throws Exception
    {
        int batchSize = 5;
        QueryConfiguration queryCfg = new QueryConfiguration();
        queryCfg.setFetchSize(batchSize);
        WebServiceFactory.getAdministrationService().setHeader(
                new RepositoryServiceLocator().getServiceName().getNamespaceURI(), 
                "QueryHeader", 
                queryCfg);
        
        // Get the details of the new users
        String homeFolder = store.getScheme().getValue() + "://" + store.getAddress() + "/" + folderReference.getUuid();
        String one = Long.toString(System.currentTimeMillis());
        String two = one + "2";        
        NewUserDetails[] newUsers = new NewUserDetails[] {
                new NewUserDetails(
                        "user" + one, 
                        "password" + one,
                        createPersonProperties(homeFolder, "first" + one, "middle" + one, "last" + one, "email" + one, "org" + one)),
                new NewUserDetails(
                        "user" + two, 
                        "password2" + two,
                        createPersonProperties(homeFolder, "first" + two, "middle" + two, "last" + two, "email" + two, "org" + two)) };

        // Create the new users
        WebServiceFactory.getAdministrationService().createUsers(newUsers);
        
        UserQueryResults results = WebServiceFactory.getAdministrationService().queryUsers(null);
        assertNotNull(results);
        
        if (logger.isDebugEnabled() == true)
        {
            while(true)
            {
                System.out.println("Next batch");
                System.out.println("Session Id: " + results.getQuerySession());
                
                for (UserDetails details : results.getUserDetails())
                {
                    System.out.println("User name: " + details.getUserName());
                }
                
                if (results.getQuerySession() == null)
                {
                    break;
                }
                results = WebServiceFactory.getAdministrationService().fetchMoreUsers(results.getQuerySession());
            }
        }
        

        // Delete the created users
        String[] userNames = new String[]{"user" + one, "user" + two};
        WebServiceFactory.getAdministrationService().deleteUsers(userNames);
    }
    
    /**
     * Test the general user CRUD methods
     */
    public void testCreateGetDeleteUser() throws Exception
    {
        // Try and get a user that does not exist
        try
        {
            WebServiceFactory.getAdministrationService().getUser("badUser");
            fail("An exception should have been raised since we are trying to get hold of a user that does not exist.");
        }
        catch (Exception exception)
        {
            // Ignore since this is what we would expect to happen
        }
        
        // Get the details of the new users
        String homeFolder = store.getScheme().getValue() + "://" + store.getAddress() + "/" + folderReference.getUuid();
        String one = Long.toString(System.currentTimeMillis());
        String two = one + "2";        
        NewUserDetails[] newUsers = new NewUserDetails[] {
                new NewUserDetails(
                        "user" + one, 
                        "password" + one,
                        createPersonProperties(homeFolder, "first" + one, "middle" + one, "last" + one, "email" + one, "org" + one)),
                new NewUserDetails(
                        "user" + two, 
                        "password2" + two,
                        createPersonProperties(homeFolder, "first" + two, "middle" + two, "last" + two, "email" + two, "org" + two)) };

        // Create the new users
        UserDetails[] userDetails = WebServiceFactory.getAdministrationService().createUsers(newUsers);

        // Check the details of the created users
        assertNotNull(userDetails);
        assertEquals(2, userDetails.length);
        String name = one;
        for (UserDetails result : userDetails)
        {
            NamedValue[] properties = result.getProperties();
            for (NamedValue value : properties)
            {
               if (value.getName().equals(Constants.PROP_USER_FIRSTNAME) == true)
               {
                   assertEquals("first" + name, value.getValue());
               }
               else if (value.getName().equals(Constants.PROP_USER_MIDDLENAME) == true)
               {
                   assertEquals("middle" + name, value.getValue());
               }
               else if (value.getName().equals(Constants.PROP_USER_LASTNAME) == true)
               {
                   assertEquals("last" + name, value.getValue());
               }
               else if (value.getName().equals(Constants.PROP_USER_HOMEFOLDER) == true)
               {
                   assertEquals(homeFolder, value.getValue());
               }
               else if (value.getName().equals(Constants.PROP_USER_EMAIL) == true)
               {
                   assertEquals("email" + name, value.getValue());
               }
               else if (value.getName().equals(Constants.PROP_USER_ORGID) == true)
               {
                   assertEquals("org" + name, value.getValue());
               }
               else if (value.getName().equals(Constants.PROP_USERNAME) == true)
               {
                   assertEquals("user" + name, value.getValue());
               }
            }
            name = two;
        }
        
        // Try and get one of the created users
        UserDetails userDetails2 = WebServiceFactory.getAdministrationService().getUser("user" + one);
        
        // Check the user details
        assertNotNull(userDetails2);
        assertEquals("user" + one, userDetails2.getUserName());
        NamedValue[] properties = userDetails2.getProperties();
        for (NamedValue value : properties)
        {
           if (value.getName().equals(Constants.PROP_USER_FIRSTNAME) == true)
           {
               assertEquals("first" + one, value.getValue());
           }
           else if (value.getName().equals(Constants.PROP_USER_MIDDLENAME) == true)
           {
               assertEquals("middle" + one, value.getValue());
           }
           else if (value.getName().equals(Constants.PROP_USER_LASTNAME) == true)
           {
               assertEquals("last" + one, value.getValue());
           }
           else if (value.getName().equals(Constants.PROP_USER_HOMEFOLDER) == true)
           {
               assertEquals(homeFolder, value.getValue());
           }
           else if (value.getName().equals(Constants.PROP_USER_EMAIL) == true)
           {
               assertEquals("email" + one, value.getValue());
           }
           else if (value.getName().equals(Constants.PROP_USER_ORGID) == true)
           {
               assertEquals("org" + one, value.getValue());
           }
           else if (value.getName().equals(Constants.PROP_USERNAME) == true)
           {
               assertEquals("user" + one, value.getValue());
           }
        }

        // Delete the created users
        String[] userNames = new String[]{"user" + one, "user" + two};
        WebServiceFactory.getAdministrationService().deleteUsers(userNames);
        
        // Ensure that the users have been deleted
        try
        {
            WebServiceFactory.getAdministrationService().getUser("user" + two);
            fail("An exception should have been raised since we are trying to get hold of a user that has previously been deleted.");
        }
        catch (Exception exception)
        {
            // Ignore since this is what we would expect to happen
        }
    }
    
    /**
     * Test querying for users
     */
//    public void testUserQuery()
//    {
//        
//    }
    
    /**
     * Test being able to create a new user, log in as that user, change that users password
     */
    public void testCreateAndAuthenticateNewUser() throws Exception
    {
        // Get the details of the new user
        String homeFolder = store.getScheme().getValue() + "://" + store.getAddress() + "/" + folderReference.getUuid();
        String one = Long.toString(System.currentTimeMillis());
        NewUserDetails[] newUsers = new NewUserDetails[] 
        {
                new NewUserDetails(
                        "user" + one, 
                        "password" + one,
                        createPersonProperties(homeFolder, "first" + one, "middle" + one, "last" + one, "email" + one, "org" + one))
        };

        // Create the new users
        UserDetails[] userDetails = WebServiceFactory.getAdministrationService().createUsers(newUsers);
        assertNotNull(userDetails);
        assertEquals(1, userDetails.length);
        
        // End the current session
        AuthenticationUtils.endSession();
        
        // Try and start a session as the newly create user
        AuthenticationUtils.startSession("user" + one, "password" + one);
        
        // Re-login as the admin user
        AuthenticationUtils.endSession();
        AuthenticationUtils.startSession(USERNAME, PASSWORD);
        
        // Lets try and change the password
        ///try
        //{
        //    WebServiceFactory.getAdministrationService().changePassword("user" + one, "badPassword", "newPassword");
        //    fail("This should throw an exception since we have not specified the old password correctly.");
       // }
       // catch (Exception exception)
       // {
       //     // Ignore since we where expecting the exception
       // }
        WebServiceFactory.getAdministrationService().changePassword("user" + one, "password" + one, "newPassword");
        
        // Now we should try and start a session with the new password
        AuthenticationUtils.endSession();
        AuthenticationUtils.startSession("user" + one, "newPassword");        
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
                new NamedValue(Constants.PROP_USER_HOMEFOLDER, homeFolder),
                new NamedValue(Constants.PROP_USER_FIRSTNAME, firstName),
                new NamedValue(Constants.PROP_USER_MIDDLENAME, middleName),
                new NamedValue(Constants.PROP_USER_LASTNAME, lastName),
                new NamedValue(Constants.PROP_USER_EMAIL, email),
                new NamedValue(Constants.PROP_USER_ORGID, orgId) };
    }

}

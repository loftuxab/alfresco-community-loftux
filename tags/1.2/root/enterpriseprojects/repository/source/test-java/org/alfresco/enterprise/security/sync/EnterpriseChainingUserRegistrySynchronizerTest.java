/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.enterprise.security.sync;

import java.util.Date;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import junit.framework.TestCase;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationContext;
import org.alfresco.repo.security.person.PersonServiceImpl;
import org.alfresco.repo.security.sync.ChainingUserRegistrySynchronizerStatus;
import org.alfresco.repo.security.sync.ChainingUserRegistrySynchronizerTest.MockApplicationContextManager;
import org.alfresco.repo.security.sync.ChainingUserRegistrySynchronizerTest.MockUserRegistry;
import org.alfresco.repo.security.sync.NodeDescription;
import org.alfresco.repo.security.sync.UserRegistrySynchronizer;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.util.PropertyMap;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * This class contains tests for the enterprise decorations for
 * the org.alfresco.repo.security.sync.ChainingUserRegistrySynchronizer 
 * 
 * @author mrogers
 *
 */
public class EnterpriseChainingUserRegistrySynchronizerTest extends TestCase
{

    /** The context locations, in reverse priority order. */
    private static final String[] CONFIG_LOCATIONS =
    {
        "classpath:alfresco/application-context.xml", "classpath:sync-test-context.xml"
    };
    
    private MBeanServerConnection mbeanServer; 

    /** The Spring application context. */
    private static ApplicationContext context = new ClassPathXmlApplicationContext(
            EnterpriseChainingUserRegistrySynchronizerTest.CONFIG_LOCATIONS);

    /** The synchronizer we are testing. */
    private UserRegistrySynchronizer synchronizer;

    /** The application context manager. */
    private MockApplicationContextManager applicationContextManager;

    /** The person service. */
    private PersonService personService;

    /** The authority service. */
    private AuthorityService authorityService;

    /** The authentication context. */
    private AuthenticationContext authenticationContext;

    @Override
    protected void setUp() throws Exception
    {
        this.synchronizer = (UserRegistrySynchronizer) EnterpriseChainingUserRegistrySynchronizerTest.context
                .getBean("testUserRegistrySynchronizer");
        this.applicationContextManager = (MockApplicationContextManager) EnterpriseChainingUserRegistrySynchronizerTest.context
                .getBean("testApplicationContextManager");
        this.personService = (PersonService) EnterpriseChainingUserRegistrySynchronizerTest.context.getBean("personService");
        this.authorityService = (AuthorityService) EnterpriseChainingUserRegistrySynchronizerTest.context
                .getBean("authorityService");

        this.authenticationContext = (AuthenticationContext) EnterpriseChainingUserRegistrySynchronizerTest.context
                .getBean("authenticationContext");
        this.authenticationContext.setSystemUserAsCurrentUser();

        this.mbeanServer = (MBeanServerConnection)EnterpriseChainingUserRegistrySynchronizerTest.context.getBean("alfrescoMBeanServer");
        setHomeFolderCreationEager(false); // the normal default if using LDAP
    }

    @Override
    protected void tearDown() throws Exception
    {
        this.authenticationContext.clearCurrentSecurityContext();
        setHomeFolderCreationEager(true); // the normal default if not using LDAP
    }

    public void setHomeFolderCreationEager(boolean homeFolderCreationEager)
    {
        ((PersonServiceImpl)personService).setHomeFolderCreationEager(homeFolderCreationEager);
    }
    
    /**
     * Test the JMX view of the ChainingUserRegistrySynchronizer 
     * 
     * @throws Exception
     */
    private static final String SYNC_HEADER = "Alfresco:Name=BatchJobs,Type=Synchronization,Category=manager";
//    private static final String SYNC_DIRECTORY = "Alfresco:Name=BatchJobs,Type=Synchronization,Category=manager";
    public void testSyncJMX() throws Exception
    {
        Date testStart = new Date();
        
        try
        {       
            NodeDescription[] persons = new NodeDescription[]{newPerson("U1")};
            NodeDescription[] groups = new NodeDescription[]{newGroup("G1")};
            MockUserRegistry testRegistry = new MockUserRegistry("Z0", persons, groups);
            this.applicationContextManager.setUserRegistries(testRegistry);
            this.synchronizer.synchronize(true, true);
            
            // Check the JMX Header Bean
            ObjectName headerBeanName = new ObjectName(SYNC_HEADER);
//            ObjectName directoryBeanName = new ObjectName(SYNC_DIRECTORY);
            assertTrue("sync header bean not registered", mbeanServer.isRegistered(headerBeanName));
//            assertTrue("sync directory bean not registered", mbeanServer.isRegistered(directoryBeanName));
            
            assertEquals("sync header not complete", "COMPLETE", (String)mbeanServer.getAttribute(
               headerBeanName, "SynchronizationStatus"));
            
       
            /**
             * Negative test - make an user registry throw an exception
             */
            testRegistry.setThrowError(true);
            testStart = new Date();
            try
            {
                this.synchronizer.synchronize(true, true);
                fail("error not thrown");
            }
            catch (AlfrescoRuntimeException e)
            {
                // expect to go here
                ChainingUserRegistrySynchronizerStatus status = (ChainingUserRegistrySynchronizerStatus)this.synchronizer;
                // Header Status
                assertTrue("end time not updated", status.getSyncEndTime().after(testStart));
                assertTrue("start time not updated", status.getSyncStartTime().after(testStart));
                assertEquals("sync status is not complete", "COMPLETE_ERROR", status.getSynchronizationStatus());         
                assertNotNull("last run on server is null", status.getLastRunOnServer());
                assertNotNull(status.getLastErrorMessage());

                // Authenticator status
                assertEquals("sync status is not complete", "COMPLETE_ERROR", status.getSynchronizationStatus("Z0")); 
                assertNotNull(status.getSynchronizationLastError("Z0"));
            }
        }
        finally
        {
            
        }
    } 
    

    /**
     * Constructs a description of a test group.
     * 
     * @param name
     *            the name
     * @param members
     *            the members
     * @return the node description
     */
    private NodeDescription newGroup(String name, String... members)
    {
        return newGroupWithDisplayName(name, name, members);
    }

    /**
     * Constructs a description of a test group with a display name.
     * 
     * @param name
     *            the name
     * @param displayName
     *            the display name
     * @param members
     *            the members
     * @return the node description
     */
    private NodeDescription newGroupWithDisplayName(String name, String displayName, String... members)
    {
        String longName = longName(name);
        NodeDescription group = new NodeDescription(longName);
        PropertyMap properties = group.getProperties();
        properties.put(ContentModel.PROP_AUTHORITY_NAME, longName);
        properties.put(ContentModel.PROP_AUTHORITY_DISPLAY_NAME, displayName);
        if (members.length > 0)
        {
            Set<String> assocs = group.getChildAssociations();
            for (String member : members)
            {
                assocs.add(longName(member));
            }
        }
        group.setLastModified(new Date());
        return group;
    }

    /**
     * Constructs a description of a test person with default email (userName@alfresco.com)
     * 
     * @param userName
     *            the user name
     * @return the node description
     */
    private NodeDescription newPerson(String userName)
    {
        return newPerson(userName, userName + "@alfresco.com");
    }

    /**
     * Constructs a description of a test person with a given email.
     * 
     * @param userName
     *            the user name
     * @param email
     *            the email
     * @return the node description
     */
    private NodeDescription newPerson(String userName, String email)
    {
        NodeDescription person = new NodeDescription(userName);
        PropertyMap properties = person.getProperties();
        properties.put(ContentModel.PROP_USERNAME, userName);
        properties.put(ContentModel.PROP_FIRSTNAME, userName + "F");
        properties.put(ContentModel.PROP_LASTNAME, userName + "L");
        properties.put(ContentModel.PROP_EMAIL, email);
        person.setLastModified(new Date());
        return person;
    }

    /**
     * Converts the given short name to a full authority name, assuming that those short names beginning with 'G'
     * correspond to groups and all others correspond to users.
     * 
     * @param shortName
     *            the short name
     * @return the full authority name
     */
    private String longName(String shortName)
    {
        return this.authorityService.getName(shortName.toLowerCase().startsWith("g") ? AuthorityType.GROUP
                : AuthorityType.USER, shortName);
    }

}

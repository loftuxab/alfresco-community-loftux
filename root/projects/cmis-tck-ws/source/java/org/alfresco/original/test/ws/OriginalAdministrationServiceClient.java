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
package org.alfresco.original.test.ws;

import javax.xml.rpc.ServiceException;

import org.alfresco.cmis.test.ws.AbstractService;
import org.alfresco.cmis.test.ws.AbstractServiceClient;
import org.alfresco.repo.webservice.administration.AdministrationServiceLocator;
import org.alfresco.repo.webservice.administration.AdministrationServiceSoapBindingStub;
import org.alfresco.repo.webservice.administration.NewUserDetails;
import org.alfresco.repo.webservice.administration.UserQueryResults;
import org.alfresco.repo.webservice.types.NamedValue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Client for Administration Service
 */
public class OriginalAdministrationServiceClient extends AbstractServiceClient
{
    private static Log LOGGER = LogFactory.getLog(OriginalAdministrationServiceClient.class);

    private static final String USERNAME = "admin";

    private static final String NEW_USERNAME = "user1";
    private static final String NEW_PASSWORD = "password1";
    private static final String CHANGE_PASSWORD = "password2";
    private static final String NEW_EMAIL = "user1@user1.com";

    private static final String PROP_USER_FIRSTNAME = "{http://www.alfresco.org/model/content/1.0}firstName";
    private static final String PROP_USER_LASTNAME = "{http://www.alfresco.org/model/content/1.0}latName";
    private static final String PROP_USER_MIDDLENAME = "{http://www.alfresco.org/model/content/1.0}middleName";
    private static final String PROP_USER_EMAIL = "{http://www.alfresco.org/model/content/1.0}email";
    private static final String PROP_USER_ORGID = "{http://www.alfresco.org/model/content/1.0}organizationId";

    public OriginalAdministrationServiceClient(AbstractService abstractService)
    {
        super(abstractService);
    }

    /**
     * Starts session for Administration Service client
     */
    public void initialize() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Initializing client...");
        }
        startSession();
    }

    /**
     * Invokes all methods in Administration Service
     */
    public void invoke() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Invoking client...");
        }
        AdministrationServiceSoapBindingStub administrationService = getAdministrationService(getProxyUrl() + getService().getPath());

        UserQueryResults users = administrationService.queryUsers(null);

        administrationService.fetchMoreUsers(users.getQuerySession());

        administrationService.getUser(USERNAME);

        NewUserDetails user = new NewUserDetails(NEW_USERNAME, NEW_PASSWORD, new NamedValue[] { new NamedValue(PROP_USER_FIRSTNAME, false, NEW_USERNAME, null),
                new NamedValue(PROP_USER_MIDDLENAME, false, NEW_USERNAME, null), new NamedValue(PROP_USER_LASTNAME, false, NEW_USERNAME, null),
                new NamedValue(PROP_USER_EMAIL, false, NEW_EMAIL, null), new NamedValue(PROP_USER_ORGID, false, NEW_USERNAME, null) });

        org.alfresco.repo.webservice.administration.UserDetails[] userDetails = administrationService.createUsers(new NewUserDetails[] { user });

        administrationService.updateUsers(userDetails);

        administrationService.changePassword(NEW_USERNAME, NEW_PASSWORD, CHANGE_PASSWORD);

        administrationService.deleteUsers(new String[] { NEW_USERNAME });
    }

    /**
     * Ends session for Administration Service client
     */
    public void release() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Releasing client...");
        }
        endSession();
    }

    /**
     * Main method to start client
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args)
    {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:wsi-context.xml");
        AbstractServiceClient client = (OriginalAdministrationServiceClient) applicationContext.getBean("originalAdministrationServiceClient");
        try
        {
            client.initialize();
            client.invoke();
            client.release();
        }
        catch (Exception e)
        {
            LOGGER.error("Some error occured during client running. Exception message: " + e.getMessage());
        }
    }

    /**
     * Gets stub for Administration Service
     * 
     * @param address address where service resides
     * @return AdministrationServiceSoapBindingStub
     * @throws ServiceException
     */
    private AdministrationServiceSoapBindingStub getAdministrationService(String address) throws ServiceException
    {
        AdministrationServiceSoapBindingStub administrationService = null;
        AdministrationServiceLocator locator = new AdministrationServiceLocator(getEngineConfiguration());
        locator.setAdministrationServiceEndpointAddress(address);
        administrationService = (AdministrationServiceSoapBindingStub) locator.getAdministrationService();
        administrationService.setMaintainSession(true);
        administrationService.setTimeout(TIMEOUT);
        return administrationService;
    }
}

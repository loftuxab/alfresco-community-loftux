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
import org.alfresco.repo.webservice.authentication.AuthenticationResult;
import org.alfresco.repo.webservice.authentication.AuthenticationServiceLocator;
import org.alfresco.repo.webservice.authentication.AuthenticationServiceSoapBindingStub;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Client for Authentication Service
 * 
 * @author Mike Shavnev
 */
public class OriginalAuthenticationServiceClient extends AbstractServiceClient
{
    private static Log LOGGER = LogFactory.getLog(OriginalAuthenticationServiceClient.class);

    public OriginalAuthenticationServiceClient(AbstractService abstractService)
    {
        super(abstractService);
    }

    public void initialize()
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Initializing client...");
        }
    }

    /**
     * Invokes all methods in Authentication Service
     */
    public void invoke() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Invoking client...");
        }
        AuthenticationServiceSoapBindingStub authenticationService = getAuthenticationService(getProxyUrl() + getService().getPath());
        AuthenticationResult result = authenticationService.startSession(getUsername(), getPassword());
        authenticationService.endSession(result.getTicket());
    }

    public void release() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Releasing client...");
        }
    }

    /**
     * Main method to start client
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args)
    {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:wsi-tools-client-context.xml");
        AbstractServiceClient client = (OriginalAuthenticationServiceClient) applicationContext.getBean("originalAuthenticationServiceClient");
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
     * Gets stub for Authentication Service
     * 
     * @param address address where service resides
     * @return ActionServiceSoapBindingStub
     * @throws ServiceException
     */
    private AuthenticationServiceSoapBindingStub getAuthenticationService(String address) throws ServiceException
    {
        AuthenticationServiceSoapBindingStub authenticationService = null;
        AuthenticationServiceLocator locator = new AuthenticationServiceLocator();
        locator.setAuthenticationServiceEndpointAddress(address);
        authenticationService = (AuthenticationServiceSoapBindingStub) locator.getAuthenticationService();
        authenticationService.setTimeout(TIMEOUT);
        return authenticationService;
    }
}

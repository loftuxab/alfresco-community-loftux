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
import org.alfresco.repo.webservice.accesscontrol.ACE;
import org.alfresco.repo.webservice.accesscontrol.AccessControlServiceLocator;
import org.alfresco.repo.webservice.accesscontrol.AccessControlServiceSoapBindingStub;
import org.alfresco.repo.webservice.accesscontrol.AccessStatus;
import org.alfresco.repo.webservice.accesscontrol.AuthorityFilter;
import org.alfresco.repo.webservice.accesscontrol.NewAuthority;
import org.alfresco.repo.webservice.accesscontrol.SiblingAuthorityFilter;
import org.alfresco.repo.webservice.repository.RepositoryServiceLocator;
import org.alfresco.repo.webservice.repository.RepositoryServiceSoapBindingStub;
import org.alfresco.repo.webservice.types.Predicate;
import org.alfresco.repo.webservice.types.Store;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Client for AccessControl Service
 * 
 * @author Mike Shavnev
 */
public class OriginalAccessControlServiceClient extends AbstractServiceClient
{
    private static Log LOGGER = LogFactory.getLog(OriginalAccessControlServiceClient.class);

    private static final String USERNAME = "admin";
    private static final String GROUP_NAME = "newGroup1";

    private static final String READ = "Read";
    private static final String WRITE = "Write";

    private static final String USER = "USER";
    private static final String GROUP = "GROUP";

    private static final String WORKSPACE_STORE = "workspace";
    private static final String SPACES_STORE = "SpacesStore";

    private static final String TYPE_FOLDER = "{http://www.alfresco.org/model/content/1.0}folder";

    private Predicate predicate;

    private AbstractService repositoryService;

    public OriginalAccessControlServiceClient(AbstractService abstractService)
    {
        super(abstractService);
    }

    public void setRepositoryService(AbstractService repositoryService)
    {
        this.repositoryService = repositoryService;
    }

    /**
     * Gets stub for AccessControl Service
     * 
     * @param address address where service resides
     * @return AccessControlServiceSoapBindingStub
     * @throws ServiceException
     */
    private AccessControlServiceSoapBindingStub getAccessControlService(String address) throws ServiceException
    {
        AccessControlServiceLocator locator = new AccessControlServiceLocator(getEngineConfiguration());
        locator.setAccessControlServiceEndpointAddress(address);
        AccessControlServiceSoapBindingStub accessControlService = (AccessControlServiceSoapBindingStub) locator.getAccessControlService();
        accessControlService.setMaintainSession(true);
        accessControlService.setTimeout(TIMEOUT);
        return accessControlService;
    }

    /**
     * Gets stub for Repository Service
     * 
     * @param address address where service resides
     * @return RepositoryServiceSoapBindingStub
     * @throws ServiceException
     */
    private RepositoryServiceSoapBindingStub getRepositoryService(String address) throws ServiceException
    {
        RepositoryServiceSoapBindingStub repositoryService = null;
        RepositoryServiceLocator locator = new RepositoryServiceLocator(getEngineConfiguration());
        locator.setRepositoryServiceEndpointAddress(address);
        repositoryService = (RepositoryServiceSoapBindingStub) locator.getRepositoryService();
        repositoryService.setMaintainSession(true);
        repositoryService.setTimeout(TIMEOUT);
        return repositoryService;
    }

    /**
     * Invokes all methods in AccessControl Service
     */
    public void invoke() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Invoking client...");
        }
        AccessControlServiceSoapBindingStub accessControlService = getAccessControlService(getProxyUrl() + getService().getPath());

        accessControlService.getACLs(predicate, null);

        ACE[] aces = new ACE[] { new ACE(USERNAME, READ, AccessStatus.acepted) };
        accessControlService.addACEs(predicate, aces);

        accessControlService.removeACEs(predicate, aces);

        accessControlService.getPermissions(predicate);

        accessControlService.getClassPermissions(new String[] { TYPE_FOLDER });

        accessControlService.hasPermissions(predicate, new String[] { WRITE });

        accessControlService.setInheritPermission(predicate, true);

        accessControlService.setOwners(predicate, USERNAME);

        accessControlService.getOwners(predicate);

        // no such method in v2.1
        accessControlService.getAllAuthorities(new AuthorityFilter(GROUP, false));

        // no such method in v2.1
        accessControlService.getAuthorities();

        // no such method in v2.1
        NewAuthority auth1 = new NewAuthority(GROUP, GROUP_NAME);
        java.lang.String[] authorities = accessControlService.createAuthorities(null, new NewAuthority[] { auth1 });

        // no such method in v2.1
        java.lang.String[] childauthorities = accessControlService.addChildAuthorities(authorities[0], new String[] { USERNAME });

        // no such method in v2.1
        accessControlService.getChildAuthorities(authorities[0], new SiblingAuthorityFilter(USER, true));

        // no such method in v2.1
        accessControlService.getParentAuthorities(childauthorities[0], new SiblingAuthorityFilter(GROUP, true));

        // no such method in v2.1
        accessControlService.removeChildAuthorities(authorities[0], childauthorities);

        // no such method in v2.1
        accessControlService.deleteAuthorities(authorities);
    }

    /**
     * Starts session and initializes AccessControl Service client
     */
    public void initialize() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Initializing client...");
        }
        startSession();

        Store store = null;
        for (Store cStore : getRepositoryService(getServerUrl() + repositoryService.getPath()).getStores())
        {
            if (WORKSPACE_STORE.equals(cStore.getScheme()) && SPACES_STORE.equals(cStore.getAddress()))
            {
                store = cStore;
                break;
            }
        }

        predicate = new Predicate(null, store, null);
    }

    /**
     * Ends session for AccessControl Service client
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
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:wsi-tools-client-context.xml");
        AbstractServiceClient client = (OriginalAccessControlServiceClient) applicationContext.getBean("originalAccessControlServiceClient");
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
}

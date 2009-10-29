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
import org.alfresco.repo.webservice.authoring.AuthoringServiceLocator;
import org.alfresco.repo.webservice.authoring.AuthoringServiceSoapBindingStub;
import org.alfresco.repo.webservice.authoring.LockTypeEnum;
import org.alfresco.repo.webservice.content.Content;
import org.alfresco.repo.webservice.content.ContentServiceLocator;
import org.alfresco.repo.webservice.content.ContentServiceSoapBindingStub;
import org.alfresco.repo.webservice.repository.RepositoryServiceLocator;
import org.alfresco.repo.webservice.repository.RepositoryServiceSoapBindingStub;
import org.alfresco.repo.webservice.repository.UpdateResult;
import org.alfresco.repo.webservice.types.CML;
import org.alfresco.repo.webservice.types.CMLCreate;
import org.alfresco.repo.webservice.types.CMLDelete;
import org.alfresco.repo.webservice.types.ContentFormat;
import org.alfresco.repo.webservice.types.NamedValue;
import org.alfresco.repo.webservice.types.Node;
import org.alfresco.repo.webservice.types.ParentReference;
import org.alfresco.repo.webservice.types.Predicate;
import org.alfresco.repo.webservice.types.Reference;
import org.alfresco.repo.webservice.types.Store;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Client for Authoring Service
 */
public class OriginalAuthoringServiceClient extends AbstractServiceClient
{
    private static Log LOGGER = LogFactory.getLog(OriginalAuthoringServiceClient.class);

    private static final String TEST_FILE_ID = "1";

    private static final String TEST_FILE_NAME = "test.txt";
    private static final String WORKSPACE_STORE = "workspace";
    private static final String SPACES_STORE = "SpacesStore";

    private static final String ENCODING = "UTF-8";
    private static final String TEST_CONTENT = "Test content";
    private static final String MIMETYPE_TEXT_PLAIN = "text/plain";

    private static final String PROP_NAME = "{http://www.alfresco.org/model/content/1.0}name";
    private static final String PROP_CONTENT = "{http://www.alfresco.org/model/content/1.0}content";
    private static final String TYPE_CONTENT = "{http://www.alfresco.org/model/content/1.0}content";
    private static final String ASSOC_CHILDREN = "{http://www.alfresco.org/model/system/1.0}children";
    private static final String CHILD_ASSOCIATION_NAME = "{http://www.alfresco.org/model/content/1.0}test" + System.currentTimeMillis();

    private static final String CHECKING_DESCRIPTION = "description";
    private static final String CHECKING_COMMENT = "test comment";
    private static final String VERSION = "1.0";

    private Predicate contentPredicate;

    private AbstractService repositoryService;
    private AbstractService contentService;

    public OriginalAuthoringServiceClient(AbstractService abstractService)
    {
        super(abstractService);
    }

    public void setRepositoryService(AbstractService repositoryService)
    {
        this.repositoryService = repositoryService;
    }

    public void setContentService(AbstractService contentService)
    {
        this.contentService = contentService;
    }

    /**
     * Starts session and initializes Authoring Service client
     */
    public void initialize() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Initializing client...");
        }
        startSession();

        RepositoryServiceSoapBindingStub repositoryService = getRepositoryService(getServerUrl() + this.repositoryService.getPath());

        Store store = null;
        for (Store cStore : repositoryService.getStores())
        {
            if (WORKSPACE_STORE.equals(cStore.getScheme()) && SPACES_STORE.equals(cStore.getAddress()))
            {
                store = cStore;
                break;
            }
        }

        Predicate predicate = new Predicate(null, store, null);
        Node[] nodes = repositoryService.get(predicate);
        Reference rootReference = nodes[0].getReference();

        ParentReference parentRef = new ParentReference();
        parentRef.setStore(store);
        parentRef.setUuid(rootReference.getUuid());
        parentRef.setAssociationType(ASSOC_CHILDREN);
        parentRef.setChildName(CHILD_ASSOCIATION_NAME);

        NamedValue[] properties = new NamedValue[] { new NamedValue(PROP_NAME, false, System.currentTimeMillis() + TEST_FILE_NAME, null) };
        CMLCreate create = new CMLCreate(TEST_FILE_ID, parentRef, null, null, null, TYPE_CONTENT, properties);
        CML cml = new CML();
        cml.setCreate(new CMLCreate[] { create });
        UpdateResult[] result = repositoryService.update(cml);

        Reference newContentNode = result[0].getDestination();

        Content content = getContentService(getServerUrl() + contentService.getPath()).write(newContentNode, PROP_CONTENT, TEST_CONTENT.getBytes(),
                new ContentFormat(MIMETYPE_TEXT_PLAIN, ENCODING));
        contentPredicate = new Predicate();
        contentPredicate.setNodes(new Reference[] { content.getNode() });
    }

    /**
     * Invokes all methods in Authoring Service
     */
    public void invoke() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Invoking client...");
        }
        AuthoringServiceSoapBindingStub authoringService = getAuthoringService(getProxyUrl() + getService().getPath());

        org.alfresco.repo.webservice.authoring.CheckoutResult checkoutResult = authoringService.checkout(contentPredicate, null);

        Predicate workingCopy = new Predicate();
        workingCopy.setNodes(new Reference[] { checkoutResult.getWorkingCopies()[0] });
        authoringService.checkin(workingCopy, new NamedValue[] { new NamedValue(CHECKING_DESCRIPTION, false, CHECKING_COMMENT, null) }, true);

        authoringService.checkinExternal(checkoutResult.getWorkingCopies()[0], new NamedValue[] { new NamedValue(CHECKING_DESCRIPTION, false, CHECKING_COMMENT, null) }, true,
                new ContentFormat(MIMETYPE_TEXT_PLAIN, ENCODING), TEST_CONTENT.getBytes());

        authoringService.cancelCheckout(workingCopy);

        authoringService.lock(contentPredicate, false, LockTypeEnum.read);

        authoringService.getLockStatus(contentPredicate);

        authoringService.unlock(contentPredicate, false);

        authoringService.createVersion(contentPredicate, new NamedValue[] { new NamedValue(CHECKING_DESCRIPTION, false, CHECKING_DESCRIPTION, null) }, true);

        authoringService.getVersionHistory(contentPredicate.getNodes()[0]);

        authoringService.revertVersion(contentPredicate.getNodes()[0], VERSION);

        authoringService.deleteAllVersions(contentPredicate.getNodes()[0]);
    }

    /**
     * Ends session for Authoring Service client and remove initial data
     */
    public void release() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Releasing client...");
        }
        CML cml = new CML();
        cml.setDelete(new CMLDelete[] { new CMLDelete(contentPredicate) });
        getRepositoryService(getServerUrl() + repositoryService.getPath()).update(cml);

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
        AbstractServiceClient client = (OriginalAuthoringServiceClient) applicationContext.getBean("originalAuthoringServiceClient");
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
     * Gets stub for Authoring Service
     * 
     * @param address address where service resides
     * @return AuthoringServiceSoapBindingStub
     * @throws ServiceException
     */
    private AuthoringServiceSoapBindingStub getAuthoringService(String address) throws ServiceException
    {
        AuthoringServiceSoapBindingStub authoringService = null;
        AuthoringServiceLocator locator = new AuthoringServiceLocator(getEngineConfiguration());
        locator.setAuthoringServiceEndpointAddress(address);
        authoringService = (AuthoringServiceSoapBindingStub) locator.getAuthoringService();
        authoringService.setMaintainSession(true);
        authoringService.setTimeout(TIMEOUT);
        return authoringService;
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
     * Gets stub for Content Service
     * 
     * @param address address where service resides
     * @return ContentServiceSoapBindingStub
     * @throws ServiceException
     */
    private ContentServiceSoapBindingStub getContentService(String address) throws ServiceException
    {
        ContentServiceSoapBindingStub contentService = null;
        ContentServiceLocator locator = new ContentServiceLocator(getEngineConfiguration());
        locator.setContentServiceEndpointAddress(address);
        contentService = (ContentServiceSoapBindingStub) locator.getContentService();
        contentService.setMaintainSession(true);
        contentService.setTimeout(TIMEOUT);
        return contentService;
    }
}

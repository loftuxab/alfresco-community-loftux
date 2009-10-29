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
import org.alfresco.repo.webservice.content.ContentServiceLocator;
import org.alfresco.repo.webservice.content.ContentServiceSoapBindingStub;
import org.alfresco.repo.webservice.repository.Association;
import org.alfresco.repo.webservice.repository.QueryResult;
import org.alfresco.repo.webservice.repository.RepositoryServiceLocator;
import org.alfresco.repo.webservice.repository.RepositoryServiceSoapBindingStub;
import org.alfresco.repo.webservice.repository.UpdateResult;
import org.alfresco.repo.webservice.types.CML;
import org.alfresco.repo.webservice.types.CMLAddAspect;
import org.alfresco.repo.webservice.types.CMLCreate;
import org.alfresco.repo.webservice.types.CMLCreateAssociation;
import org.alfresco.repo.webservice.types.CMLDelete;
import org.alfresco.repo.webservice.types.ContentFormat;
import org.alfresco.repo.webservice.types.NamedValue;
import org.alfresco.repo.webservice.types.Node;
import org.alfresco.repo.webservice.types.ParentReference;
import org.alfresco.repo.webservice.types.Predicate;
import org.alfresco.repo.webservice.types.Query;
import org.alfresco.repo.webservice.types.QueryConfiguration;
import org.alfresco.repo.webservice.types.Reference;
import org.alfresco.repo.webservice.types.Store;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Client for Repository Service
 */
public class OriginalRepositoryServiceClient extends AbstractServiceClient
{
    private static Log LOGGER = LogFactory.getLog(OriginalRepositoryServiceClient.class);

    private static final String QUERY_HEADER = "QueryHeader";
    private static final String ASSOC_DIRECTION = "target";
    private static final String TEST_STORE_NAME = "Test" + System.currentTimeMillis();

    private static final String WORKSPACE_STORE = "workspace";
    private static final String SPACES_STORE = "SpacesStore";

    private static final String ENCODING = "UTF-8";
    private static final String TEST_FILE_NAME = "test.txt";
    private static final String QUERY_LANG_LUCENE = "lucene";
    private static final String TEST_CONTENT = "Test content";
    private static final String MIMETYPE_TEXT_PLAIN = "text/plain";
    private static final String ASSOC_ATTACHMENTS = "{http://www.alfresco.org/model/content/1.0}attachments";
    private static final String ASSOC_CHILDREN = "{http://www.alfresco.org/model/system/1.0}children";
    private static final String CHILD_ASSOCIATION_NAME = "{http://www.alfresco.org/model/content/1.0}test" + System.currentTimeMillis();
    private static final String PROP_NAME = "{http://www.alfresco.org/model/content/1.0}name";
    private static final String TYPE_CONTENT = "{http://www.alfresco.org/model/content/1.0}content";
    private static final String ASPECT_ATTACHABLE = "{http://www.alfresco.org/model/content/1.0}attachable";
    private static final String PROP_CONTENT = "{http://www.alfresco.org/model/content/1.0}content";

    private Store store;

    private Reference rootReference;
    private Reference contentReference;
    private Reference content1Reference;

    private AbstractService contentService;

    public OriginalRepositoryServiceClient(AbstractService abstractService)
    {
        super(abstractService);
    }

    public void setContentService(AbstractService contentService)
    {
        this.contentService = contentService;
    }

    /**
     * Starts session and initializes Repository Service client
     */
    public void initialize() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Initializing client...");
        }
        startSession();

        RepositoryServiceSoapBindingStub repositoryService = getRepositoryService(getServerUrl() + getService().getPath());

        for (Store cStore : repositoryService.getStores())
        {
            if (WORKSPACE_STORE.equals(cStore.getScheme()) && SPACES_STORE.equals(cStore.getAddress()))
            {
                store = cStore;
                break;
            }
        }

        Node[] nodes = repositoryService.get(new Predicate(null, store, null));
        rootReference = nodes[0].getReference();
        ParentReference parentRef = new ParentReference();
        parentRef.setStore(store);
        parentRef.setUuid(rootReference.getUuid());
        parentRef.setAssociationType(ASSOC_CHILDREN);
        parentRef.setChildName(CHILD_ASSOCIATION_NAME);
        NamedValue[] properties = new NamedValue[] { new NamedValue(PROP_NAME, false, System.currentTimeMillis() + TEST_FILE_NAME, null) };
        String testContent = "testContent";
        String testContent1 = "testContent1";
        CMLCreate create = new CMLCreate(testContent, parentRef, null, null, null, TYPE_CONTENT, properties);
        CMLCreate create1 = new CMLCreate(testContent1, parentRef, null, null, null, TYPE_CONTENT, properties);
        CMLCreateAssociation createAssoc = new CMLCreateAssociation(null, testContent, null, testContent1, ASSOC_ATTACHMENTS);
        CMLAddAspect cmlAddAspect = new CMLAddAspect(ASPECT_ATTACHABLE, null, null, testContent);

        CML cml = new CML();
        cml.setCreate(new CMLCreate[] { create, create1 });
        cml.setAddAspect(new CMLAddAspect[] { cmlAddAspect });
        cml.setCreateAssociation(new CMLCreateAssociation[] { createAssoc });

        UpdateResult[] updateResults = repositoryService.update(cml);
        contentReference = updateResults[0].getDestination();
        content1Reference = updateResults[1].getDestination();
        getContentService(getServerUrl() + contentService.getPath()).write(contentReference, PROP_CONTENT, TEST_CONTENT.getBytes(),
                new ContentFormat(MIMETYPE_TEXT_PLAIN, ENCODING));
    }

    /**
     * Invokes all methods in Repository Service
     */
    public void invoke() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Invoking client...");
        }
        RepositoryServiceSoapBindingStub repositoryService = getRepositoryService(getProxyUrl() + getService().getPath());

        repositoryService.createStore(WORKSPACE_STORE, TEST_STORE_NAME);

        repositoryService.getStores();

        Query query = new Query(QUERY_LANG_LUCENE, "*");
        QueryConfiguration queryCfg = new QueryConfiguration();
        queryCfg.setFetchSize(5);
        repositoryService.setHeader(new RepositoryServiceLocator().getServiceName().getNamespaceURI(), QUERY_HEADER, queryCfg);
        QueryResult queryResult = new QueryResult();
        queryResult = repositoryService.query(store, query, false);

        repositoryService.fetchMore(queryResult.getQuerySession());

        queryResult = repositoryService.queryChildren(rootReference);

        repositoryService.queryParents(new Reference(store, queryResult.getResultSet().getRows(0).getNode().getId(), null));

        repositoryService.queryAssociated(contentReference, new Association(ASSOC_ATTACHMENTS, ASSOC_DIRECTION));

        repositoryService.get(new Predicate(new Reference[] { content1Reference }, null, null));

        repositoryService.describe(new Predicate(new Reference[] { content1Reference }, null, null));

        CML cml = new CML();
        cml.setDelete(new CMLDelete[] { new CMLDelete(new Predicate(new Reference[] { contentReference, content1Reference }, null, null)) });
        repositoryService.update(cml);
    }

    /**
     * Ends session for Repository Service client
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
        AbstractServiceClient client = (OriginalRepositoryServiceClient) applicationContext.getBean("originalRepositoryServiceClient");
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
}

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
import org.alfresco.repo.webservice.classification.AppliedCategory;
import org.alfresco.repo.webservice.classification.ClassificationServiceLocator;
import org.alfresco.repo.webservice.classification.ClassificationServiceSoapBindingStub;
import org.alfresco.repo.webservice.repository.RepositoryServiceLocator;
import org.alfresco.repo.webservice.repository.RepositoryServiceSoapBindingStub;
import org.alfresco.repo.webservice.types.Classification;
import org.alfresco.repo.webservice.types.Predicate;
import org.alfresco.repo.webservice.types.Reference;
import org.alfresco.repo.webservice.types.Store;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Client for Classification Service
 */
public class OriginalClassificationServiceClient extends AbstractServiceClient
{
    private static Log LOGGER = LogFactory.getLog(OriginalClassificationServiceClient.class);

    private static final String WORKSPACE_STORE = "workspace";
    private static final String SPACES_STORE = "SpacesStore";

    private Store store;

    private AbstractService repositoryService;

    public OriginalClassificationServiceClient(AbstractService abstractService)
    {
        super(abstractService);
    }

    public void setRepositoryService(AbstractService repositoryService)
    {
        this.repositoryService = repositoryService;
    }

    /**
     * Starts session and initializes Classification Service client
     */
    public void initialize() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Initializing client...");
        }
        startSession();

        for (Store cStore : getRepositoryService(getServerUrl() + repositoryService.getPath()).getStores())
        {
            if (WORKSPACE_STORE.equals(cStore.getScheme()) && SPACES_STORE.equals(cStore.getAddress()))
            {
                store = cStore;
                break;
            }
        }
    }

    /**
     * Invokes all methods in Classification Service
     */
    public void invoke() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Invoking client...");
        }
        ClassificationServiceSoapBindingStub classificationService = getClassificationService(getProxyUrl() + getService().getPath());

        Classification[] classifications = classificationService.getClassifications(store);

        classificationService.getChildCategories(classifications[0].getRootCategory().getId());

        AppliedCategory appliedCategory = new AppliedCategory();
        appliedCategory.setCategories(new Reference[] { classifications[0].getRootCategory().getId() });
        appliedCategory.setClassification(classifications[0].getClassification());
        classificationService.setCategories(new Predicate(null, store, null), new AppliedCategory[] { appliedCategory });

        classificationService.getCategories(new Predicate(null, store, null));

        classificationService.describeClassification(classifications[0].getClassification());
    }

    /**
     * Ends session for Classification Service client
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
        AbstractServiceClient client = (OriginalClassificationServiceClient) applicationContext.getBean("originalClassificationServiceClient");
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
     * Gets stub for Classification Service
     * 
     * @param address address where service resides
     * @return ClassificationServiceSoapBindingStub
     * @throws ServiceException
     */
    private ClassificationServiceSoapBindingStub getClassificationService(String address) throws ServiceException
    {
        ClassificationServiceSoapBindingStub classificationService = null;
        ClassificationServiceLocator locator = new ClassificationServiceLocator(getEngineConfiguration());
        locator.setClassificationServiceEndpointAddress(address);
        classificationService = (ClassificationServiceSoapBindingStub) locator.getClassificationService();
        classificationService.setMaintainSession(true);
        classificationService.setTimeout(TIMEOUT);
        return classificationService;
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

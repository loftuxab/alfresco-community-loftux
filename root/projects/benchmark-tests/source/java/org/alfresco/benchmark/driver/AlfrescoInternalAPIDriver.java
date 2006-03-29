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
package org.alfresco.benchmark.driver;

import java.io.File;
import java.util.Map;

import org.alfresco.benchmark.dataloader.DataLoaderComponent;
import org.alfresco.benchmark.dataprovider.ContentData;
import org.alfresco.benchmark.dataprovider.DataProviderComponent;
import org.alfresco.benchmark.dataprovider.RepositoryProfile;
import org.alfresco.benchmark.util.AlfrescoUtils;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.repo.transaction.TransactionUtil;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.transaction.TransactionService;

import com.sun.japex.TestCase;

/**
 * @author Roy Wetherall
 */
public class AlfrescoInternalAPIDriver extends BaseAlfrescoBenchmarkDriver
{    
    private NodeService nodeService;
    private ContentService contentService;
    private AuthenticationComponent authenticationComponent;
    private TransactionService transactionService;
    private SearchService searchService;
    
    private DataProviderComponent dataProviderComponent;
    private DataLoaderComponent dataLoaderComponent;
    
    private NodeRef folderNodeRef;
    
    @Override
    public void initializeDriver()
    {
        // Get the required services
        this.nodeService = (NodeService)AlfrescoUtils.getApplicationContext().getBean("NodeService");
        this.contentService = (ContentService)AlfrescoUtils.getApplicationContext().getBean("ContentService");
        this.authenticationComponent = (AuthenticationComponent)AlfrescoUtils.getApplicationContext().getBean("authenticationComponent");
        this.transactionService = (TransactionService)AlfrescoUtils.getApplicationContext().getBean("transactionComponent");
        this.searchService = (SearchService)AlfrescoUtils.getApplicationContext().getBean("SearchService");
        
        this.dataProviderComponent = (DataProviderComponent)AlfrescoUtils.getApplicationContext().getBean("dataProviderComponent");
        this.dataLoaderComponent = (DataLoaderComponent)AlfrescoUtils.getApplicationContext().getBean("dataLoaderComponent");
    }
    
    @Override
    public void prepare(TestCase tc)
    {
        try
        {    
            super.prepare(tc);
            
            // Set the authentication
            this.authenticationComponent.setSystemUserAsCurrentUser();
            
            // Get the test case folder node ref
            AlfrescoUtils.getTestCaseRootFolder(
                    this.dataLoaderComponent, 
                    this.nodeService, 
                    new RepositoryProfile(), 
                    tc);
        }
        catch (Throwable exception)
        {
            exception.printStackTrace();
        }
    }
    
    private Map<String, Object> contentPropertyValues; 
    private NodeRef contentNodeRef;
    
    @Override
    public void preRun(TestCase testCase)
    {
        // Get content property values
        this.contentPropertyValues = AlfrescoInternalAPIDriver.this.dataProviderComponent.getPropertyData(
                new RepositoryProfile(), 
                AlfrescoUtils.getContentPropertyProfiles());
        
        // Get the folder and content node references
        this.folderNodeRef = AlfrescoUtils.getRandomFolder(testCase);
        this.contentNodeRef = AlfrescoUtils.getRandomContent(testCase);
    }

    @Override
    protected void doCreateContentBenchmark(final TestCase tc)
    {
        try
        {
            TransactionUtil.executeInUserTransaction(this.transactionService, new TransactionUtil.TransactionWork<Object>()
            {
                public Object doWork() throws Exception
                {
                    // TODO we get the test data whilst creating the node .. how do we do this without affecting the tests??
                    
                    // Set the authentication
                    AlfrescoInternalAPIDriver.this.authenticationComponent.setSystemUserAsCurrentUser();
                    
                    AlfrescoUtils.createContentNode(
                            AlfrescoInternalAPIDriver.this.nodeService, 
                            AlfrescoInternalAPIDriver.this.contentService, 
                            AlfrescoInternalAPIDriver.this.contentPropertyValues, 
                            AlfrescoInternalAPIDriver.this.folderNodeRef);
                    
                    // Store the content size for later use
                    ContentData contentData = (ContentData)AlfrescoInternalAPIDriver.this.contentPropertyValues.get(ContentModel.PROP_CONTENT.toString());
                    tc.setParam("alfresco.contentSize", Integer.toString(contentData.getSize()));
                    
                    // Do nothing on return 
                    return null;
                }
        
            });            
        }
        catch (Throwable exception)
        {
            exception.printStackTrace();
        }       
    }

    @Override
    protected void doReadContentBenchmark(final TestCase tc)
    {
        try
        {
            TransactionUtil.executeInUserTransaction(this.transactionService, new TransactionUtil.TransactionWork<Object>()
            {
                public Object doWork() throws Exception
                {
                    // TODO need to login as a non-system user ...
                    // Set the authentication
                    AlfrescoInternalAPIDriver.this.authenticationComponent.setSystemUserAsCurrentUser();
                    
                    // Read the content
                    ContentReader contentReader = AlfrescoInternalAPIDriver.this.contentService.getReader(
                            AlfrescoInternalAPIDriver.this.contentNodeRef, 
                            ContentModel.PROP_CONTENT);
                    contentReader.getContent(File.createTempFile("benchmark", "temp"));
                    
                    // Store the content size for later use
                    tc.setParam("alfresco.contentSize", Long.toString(contentReader.getSize()));
                    
                    // Do nothing on return 
                    return null;
                }
        
            });            
        }
        catch (Throwable exception)
        {
            exception.printStackTrace();
        } 
        
    }
}

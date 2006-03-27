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

import java.util.Map;

import org.alfresco.benchmark.dataprovider.ContentData;
import org.alfresco.benchmark.dataprovider.DataProviderComponent;
import org.alfresco.benchmark.dataprovider.RepositoryProfile;
import org.alfresco.benchmark.util.AlfrescoUtils;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.repo.transaction.TransactionUtil;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.transaction.TransactionService;

import com.sun.japex.JapexDriverBase;
import com.sun.japex.TestCase;

/**
 * @author Roy Wetherall
 */
public class AlfrescoInternalCreateContentDriver extends JapexDriverBase
{    
    private NodeService nodeService;
    private ContentService contentService;
    private AuthenticationComponent authenticationComponent;
    private TransactionService transactionService;
    private SearchService searchService;
    
    private DataProviderComponent dataProviderComponent;
    
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
    }
    
    @Override
    public void prepare(TestCase tc)
    {
        try
        {                                
            // Set the authentication
            this.authenticationComponent.setSystemUserAsCurrentUser();
            
            // Get the company home node
            ResultSet rs = this.searchService.query(new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore"), SearchService.LANGUAGE_XPATH, "/app:company_home");
            final NodeRef companyHomeNodeRef = rs.getNodeRef(0);
            
            // Create a folder for the contents for this test to reside in
            this.folderNodeRef = AlfrescoUtils.createFolderNode(this.dataProviderComponent, this.nodeService, new RepositoryProfile(), companyHomeNodeRef);
            
            // Set the location for the data files
            tc.setParam("alfresco.outputFile", ".\\data\\output\\" + System.currentTimeMillis() + "_data.csv");
        }
        catch (Throwable exception)
        {
            exception.printStackTrace();
        }
    }
    
    @Override
    public void warmup(TestCase tc)
    {
        // Do nothing!!
    }
    
    @Override
    public void run(final TestCase tc)
    {
        try
        {
            TransactionUtil.executeInUserTransaction(this.transactionService, new TransactionUtil.TransactionWork<Object>()
            {
                public Object doWork() throws Exception
                {
                    // TODO we get the test data whilst creating the node .. how do we do this without affecting the tests??
                    
                    // Set the authentication
                    AlfrescoInternalCreateContentDriver.this.authenticationComponent.setSystemUserAsCurrentUser();
                    
                    Map<String, Object> propertyValues = AlfrescoInternalCreateContentDriver.this.dataProviderComponent.getPropertyData(
                            new RepositoryProfile(), 
                            AlfrescoUtils.getContentPropertyProfiles());
                    
                    AlfrescoUtils.createContentNode(
                            AlfrescoInternalCreateContentDriver.this.nodeService, 
                            AlfrescoInternalCreateContentDriver.this.contentService, 
                            propertyValues, 
                            AlfrescoInternalCreateContentDriver.this.folderNodeRef);
                    
                    // Store the content size for later use
                    ContentData contentData = (ContentData)propertyValues.get(ContentModel.PROP_CONTENT.toString());
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
}

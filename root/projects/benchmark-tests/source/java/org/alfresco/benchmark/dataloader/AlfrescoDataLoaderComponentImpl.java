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
package org.alfresco.benchmark.dataloader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.benchmark.dataprovider.DataProviderComponent;
import org.alfresco.benchmark.dataprovider.RepositoryProfile;
import org.alfresco.benchmark.util.AlfrescoUtils;
import org.alfresco.benchmark.util.RandUtils;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.repo.transaction.TransactionUtil;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;

/**
 * @author Roy Wetherall
 */
public class AlfrescoDataLoaderComponentImpl implements DataLoaderComponent
{
    private StoreRef storeRef = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
    
    private NodeService nodeService;
    
    private SearchService searchService;
    
    private AuthenticationComponent authenticationComponent;
    
    private DataProviderComponent dataProviderComponent;
    
    private ContentService contentService;
    
    private TransactionService transactionService;
    
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    public void setSearchService(SearchService searchService)
    {
        this.searchService = searchService;
    }
    
    public void setAuthenticationComponent(AuthenticationComponent authenticationComponent)
    {
        this.authenticationComponent = authenticationComponent;
    }
    
    public void setDataProviderComponent(DataProviderComponent dataProviderComponent)
    {
        this.dataProviderComponent = dataProviderComponent;
    }
    
    public void setContentService(ContentService contentService)
    {
        this.contentService = contentService;
    }
    
    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }
    
    public LoadedData loadData(RepositoryProfile repositoryProfile)
    {   
        // Set the authentication
        this.authenticationComponent.setSystemUserAsCurrentUser();
        
        // Get the company home node
        ResultSet rs = this.searchService.query(this.storeRef, SearchService.LANGUAGE_XPATH, "/app:company_home");
        final NodeRef companyHomeNodeRef = rs.getNodeRef(0);
        
        // Create a folder in company home within which we will create all the test data
        final Map<QName, Serializable> folderProps = new HashMap<QName, Serializable>();
        folderProps.put(ContentModel.PROP_NAME, "Test Data " + System.currentTimeMillis());
        
        NodeRef dataFolderNodeRef = TransactionUtil.executeInUserTransaction(this.transactionService, new TransactionUtil.TransactionWork<NodeRef> ()
        {
            public NodeRef doWork() throws Exception
            {
                return AlfrescoDataLoaderComponentImpl.this.nodeService.createNode(
                        companyHomeNodeRef, 
                        ContentModel.ASSOC_CONTAINS, 
                        QName.createQName(NamespaceService.APP_MODEL_1_0_URI, "test_data_" + System.currentTimeMillis()),
                        ContentModel.TYPE_FOLDER,
                        folderProps).getChildRef();
            }           
        });
        
        LoadedData loadedData = new LoadedData(dataFolderNodeRef);
        
        List<NodeRef> folders = new ArrayList<NodeRef>(1);
        folders.add(dataFolderNodeRef);
        populateFolders(loadedData, repositoryProfile, folders, 0);
        
        return loadedData;
    }
    
    public void populateFolders(final LoadedData loadedData, final RepositoryProfile repositoryProfile, final List<NodeRef> folderNodeRefs, int depth)
    {
        System.out.println("depth=" + depth + "; list_size=" + folderNodeRefs.size());
        
        // Increment the depth
        final int newDepth = depth + 1;
        final List<NodeRef> subFolders = new ArrayList<NodeRef>(10);
        
        TransactionUtil.executeInUserTransaction(this.transactionService, new TransactionUtil.TransactionWork<Object> ()
        {
            public Object doWork() throws Exception
            {
                for (NodeRef folderNodeRef : folderNodeRefs)
                {
                    // Now start adding data to the test data folder
                    int numberOfContentNodes = RandUtils.nextGaussianInteger(
                                                                repositoryProfile.getAverageNumberOfDocumentsInFolder(), 
                                                                repositoryProfile.getNumberOfDocumentsInFolderVariation());
                    int numberOfSubFolderNodes = RandUtils.nextGaussianInteger(
                                                                repositoryProfile.getAverageNumberOfSubFolders(),
                                                                repositoryProfile.getNumberOfSubFoldersVariation());
                    int folderDepth = RandUtils.nextGaussianInteger(
                                                                repositoryProfile.getAverageFolderDepth(),
                                                                repositoryProfile.getFolderDepthVariation());
                    
                    // Create content
                    for (int i = 0; i < numberOfContentNodes; i++)
                    {
                        AlfrescoUtils.createContentNode(
                                AlfrescoDataLoaderComponentImpl.this.dataProviderComponent, 
                                AlfrescoDataLoaderComponentImpl.this.nodeService, 
                                AlfrescoDataLoaderComponentImpl.this.contentService, 
                                repositoryProfile, 
                                folderNodeRef);
                    }
                    loadedData.incrementContentCount(numberOfContentNodes);
                    
                    // Create folders
                    for (int i = 0; i < numberOfSubFolderNodes; i++)
                    {
                        if (newDepth <= folderDepth)
                        {
                            NodeRef subFolderNodeRef = AlfrescoUtils.createFolderNode(
                                    AlfrescoDataLoaderComponentImpl.this.dataProviderComponent, 
                                    AlfrescoDataLoaderComponentImpl.this.nodeService, 
                                    repositoryProfile, 
                                    folderNodeRef);
                            subFolders.add(subFolderNodeRef);
                            loadedData.incrementFolderCount(1);
                        }
                    }                                             
                }
                
                return null;
            }                   
        });    
        
        if (subFolders.size() > 0)
        {
            // Populate the sub folders
            populateFolders(loadedData, repositoryProfile, subFolders, newDepth);
        }
    }
}

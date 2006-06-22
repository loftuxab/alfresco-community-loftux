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
package org.alfresco.benchmark.alfresco;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.benchmark.framework.BenchmarkUtils;
import org.alfresco.benchmark.framework.DataLoaderComponent;
import org.alfresco.benchmark.framework.LoadedData;
import org.alfresco.benchmark.framework.dataprovider.RepositoryProfile;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.transaction.TransactionUtil;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.GUID;

/**
 * @author Roy Wetherall
 */
public class AlfrescoDataLoaderComponentImpl implements DataLoaderComponent
{
    /** The node service */
    private NodeService nodeService;
    
    /** The search service */
    private SearchService searchService;
    
    /** The content service */
    private ContentService contentService;
    
    /** The transaction service */
    private TransactionService transactionService;
    
    /** The authentication service */
    private AuthenticationService authenticationService;
    
    /** The person service */
    private PersonService personService;
    
    /**
     * Set the node service 
     * 
     * @param nodeService   the node service
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    /**
     * Set the search service
     * 
     * @param searchService     the serarch service
     */
    public void setSearchService(SearchService searchService)
    {
        this.searchService = searchService;
    }
    
    /**
     * Set the content service
     * 
     * @param contentService    the content service
     */
    public void setContentService(ContentService contentService)
    {
        this.contentService = contentService;
    }
    
    /**
     * Set the transaction service
     * 
     * @param transactionService    the transaction service
     */
    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }
    
    /**
     * Set the authentication service
     * 
     * @param authenticationService     the authentication service
     */
    public void setAuthenticationService(AuthenticationService authenticationService)
    {
        this.authenticationService = authenticationService;
    }
    
    /**
     * Set the person service
     * 
     * @param personService     the person service
     */
    public void setPersonService(PersonService personService)
    {
        this.personService = personService;
    }
    
    /**
     * @see org.alfresco.benchmark.framework.DataLoaderComponent#loadData(org.alfresco.benchmark.framework.dataprovider.RepositoryProfile)
     */
    public LoadedData loadData(RepositoryProfile repositoryProfile)
    {   
        // Get the company home node
        final NodeRef companyHomeNodeRef = AlfrescoUtils.getCompanyHomeNodeRef(this.searchService, AlfrescoUtils.storeRef);
        
        List<String> loadedFolders = new ArrayList<String>();
        List<String> loadedContent = new ArrayList<String>();
        
        // Create a folder in company home within which we will create all the test data
        final Map<QName, Serializable> folderProps = new HashMap<QName, Serializable>();
        folderProps.put(ContentModel.PROP_NAME, BENCHMARK_OBJECT_PREFIX + System.currentTimeMillis());
        
        NodeRef dataFolderNodeRef = TransactionUtil.executeInUserTransaction(this.transactionService, new TransactionUtil.TransactionWork<NodeRef> ()
        {
            public NodeRef doWork() throws Exception
            {
                return AlfrescoDataLoaderComponentImpl.this.nodeService.createNode(
                        companyHomeNodeRef, 
                        ContentModel.ASSOC_CONTAINS, 
                        QName.createQName(NamespaceService.APP_MODEL_1_0_URI, DataLoaderComponent.BENCHMARK_OBJECT_PREFIX + System.currentTimeMillis()),
                        ContentModel.TYPE_FOLDER,
                        folderProps).getChildRef();
            }           
        });
        
        loadedFolders.add(dataFolderNodeRef.toString());
        
        LoadedData loadedData = new LoadedData(dataFolderNodeRef.toString());
        
        List<NodeRef> folders = new ArrayList<NodeRef>(1);
        folders.add(dataFolderNodeRef);
        populateFolders(loadedData, repositoryProfile, folders, 0, loadedFolders, loadedContent);
        
        try
        {
            //  Serialise the loaded folder and content lists
            new ObjectOutputStream(new FileOutputStream(BenchmarkUtils.getOutputFileLocation() + File.separator + "alf_loaded_folders.bin")).writeObject(loadedFolders);
            new ObjectOutputStream(new FileOutputStream(BenchmarkUtils.getOutputFileLocation() + File.separator + "alf_loaded_content.bin")).writeObject(loadedContent);
        }
        catch (Exception exception)
        {
            throw new RuntimeException(exception);
        }
        
        return loadedData;
    }
    
    /**
     * Populates the folders with the content and sub folders.
     * 
     * @param loadedData        details of the loaded data 
     * @param repositoryProfile the repository profile
     * @param folderNodeRefs    the folder nore references
     * @param depth             the current depth
     */
    private void populateFolders(
            final LoadedData loadedData, 
            final RepositoryProfile repositoryProfile, 
            final List<NodeRef> folderNodeRefs, 
            int depth,
            final List<String> loadedFolders,
            final List<String> loadedContent)
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
                    int numberOfContentNodes = BenchmarkUtils.nextGaussianInteger(
                                                                repositoryProfile.getDocumentsInFolderCountAverage(), 
                                                                repositoryProfile.getDocumentsInFolderCountVariation());
                    int numberOfSubFolderNodes = BenchmarkUtils.nextGaussianInteger(
                                                                repositoryProfile.getSubFoldersCountAverage(),
                                                                repositoryProfile.getSubFoldersCountVariation());
                    int folderDepth = BenchmarkUtils.nextGaussianInteger(
                                                                repositoryProfile.getFolderDepthAverage(),
                                                                repositoryProfile.getFolderDepthVariation());
                    
                    // Create content
                    for (int i = 0; i < numberOfContentNodes; i++)
                    {
                        NodeRef contentNode = AlfrescoUtils.createContentNode( 
                                AlfrescoDataLoaderComponentImpl.this.nodeService, 
                                AlfrescoDataLoaderComponentImpl.this.contentService, 
                                repositoryProfile, 
                                folderNodeRef);
                        loadedContent.add(contentNode.toString());
                    }
                    loadedData.incrementContentCount(numberOfContentNodes);
                    
                    // Create folders
                    for (int i = 0; i < numberOfSubFolderNodes; i++)
                    {
                        if (newDepth <= folderDepth)
                        {
                            NodeRef subFolderNodeRef = AlfrescoUtils.createFolderNode( 
                                    AlfrescoDataLoaderComponentImpl.this.nodeService, 
                                    repositoryProfile, 
                                    folderNodeRef);
                            subFolders.add(subFolderNodeRef);
                            loadedData.incrementFolderCount(1);
                            loadedFolders.add(subFolderNodeRef.toString());
                        }
                    }                                             
                }
                
                return null;
            }                   
        });    
        
        if (subFolders.size() > 0)
        {
            // Populate the sub folders
            populateFolders(loadedData, repositoryProfile, subFolders, newDepth, loadedFolders, loadedContent);
        }
    }

    /**
     * @see org.alfresco.benchmark.framework.DataLoaderComponent#createUsers(int)
     */
    public List<String> createUsers(final int count)
    {
        return TransactionUtil.executeInUserTransaction(this.transactionService, new TransactionUtil.TransactionWork<List<String>> ()
        {
            public List<String> doWork() throws Exception
            {
                List<String> users = new ArrayList<String>(count);
                
                for (int i = 0; i < count; i++)
                {
                    // Create the users home folder
                    NodeRef companyHome = AlfrescoUtils.getCompanyHomeNodeRef(
                                                        AlfrescoDataLoaderComponentImpl.this.searchService,
                                                        AlfrescoUtils.storeRef);
                    NodeRef homeFolder = AlfrescoUtils.createFolderNode(
                                                        AlfrescoDataLoaderComponentImpl.this.nodeService,
                                                        new RepositoryProfile(),
                                                        companyHome,
                                                        "userHome_" + GUID.generate());
                    
                    // Create the authentication
                    String userName = BENCHMARK_OBJECT_PREFIX + Long.toString(System.currentTimeMillis());
                    String password = "password";
                    AlfrescoDataLoaderComponentImpl.this.authenticationService.createAuthentication(userName, password.toCharArray());
                    
                    // Create the person
                    Map<QName, Serializable> personProperties = new HashMap<QName, Serializable>();
                    personProperties.put(ContentModel.PROP_USERNAME, userName);
                    personProperties.put(ContentModel.PROP_HOMEFOLDER, homeFolder);
                    personProperties.put(ContentModel.PROP_FIRSTNAME, "benchmark");
                    personProperties.put(ContentModel.PROP_LASTNAME, "user");
                    personService.createPerson(personProperties);
                    
                    // Add the new user to the list
                    users.add(userName);
                }
                
                return users;
            }
        });        
    }
    
    public static void main(String[] args)
    {
        RepositoryProfile repositoryProfile = new RepositoryProfile();
        if (args != null)
        {
            String repositoryProfileLocation = args[0];
            if (repositoryProfileLocation != null)
            {
                // Load the repository profile details from the properties file
            }
        }
        
        // Get data loader component
        DataLoaderComponent dataLoaderComponent = (DataLoaderComponent)AlfrescoUtils.getApplicationContext().getBean("dataLoaderComponent");
        
        // Load the data into the repo
        LoadedData loadedData = dataLoaderComponent.loadData(repositoryProfile);
        
        // Report the data loaded
        System.out.println("The data has been loaded into folder " + loadedData.getRootFolder() + " :");
        System.out.println("  - Folder count = " + loadedData.getFolderCount());
        System.out.println("  - Content count = " + loadedData.getContentCount());
        System.out.println("  - Total count = " + (loadedData.getFolderCount() + loadedData.getContentCount()));       
        
        // Load the requested number of users into the system ...
    }
}

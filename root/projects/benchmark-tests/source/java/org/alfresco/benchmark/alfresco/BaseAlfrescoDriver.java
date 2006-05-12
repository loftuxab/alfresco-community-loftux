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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.alfresco.benchmark.framework.BaseBenchmarkDriver;
import org.alfresco.benchmark.framework.DataLoaderComponent;
import org.alfresco.benchmark.framework.dataprovider.DataProviderComponent;
import org.alfresco.benchmark.framework.dataprovider.PropertyProfile;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.repo.transaction.TransactionUtil;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.transaction.TransactionService;

import com.sun.japex.TestCase;

/**
 * @author Roy Wetherall
 */
public abstract class BaseAlfrescoDriver extends BaseBenchmarkDriver
{    
    protected NodeService nodeService;
    protected ContentService contentService;
    protected VersionService versionService;
    protected AuthenticationComponent authenticationComponent;
    protected TransactionService transactionService;
    protected PersonService personService;
    protected PermissionService permissionService;
    protected SearchService searchService;
    
    protected NodeService smallNodeService;
    protected ContentService smallContentService;
    
    protected DataLoaderComponent dataLoaderComponent;
    
    protected Map<String, Object> contentPropertyValues; 
    protected Map<String, Object> folderPropertyValues;
    //protected NodeRef rootFolder;
    protected NodeRef contentNodeRef;
    protected NodeRef folderNodeRef;
    protected String userName;
    
    protected boolean useUsers = true;
    
    
    private static boolean usersPrepaired = false;
    
    @Override
    public void initializeDriver()
    {
        // Get the required services
        this.nodeService = (NodeService)AlfrescoUtils.getApplicationContext().getBean("NodeService");
        this.searchService = (SearchService)AlfrescoUtils.getApplicationContext().getBean("SearchService");
        this.contentService = (ContentService)AlfrescoUtils.getApplicationContext().getBean("ContentService");
        this.authenticationComponent = (AuthenticationComponent)AlfrescoUtils.getApplicationContext().getBean("authenticationComponent");
        this.transactionService = (TransactionService)AlfrescoUtils.getApplicationContext().getBean("transactionComponent");
        this.versionService = (VersionService)AlfrescoUtils.getApplicationContext().getBean("VersionService");
        this.personService = (PersonService)AlfrescoUtils.getApplicationContext().getBean("PersonService");
        this.permissionService = (PermissionService)AlfrescoUtils.getApplicationContext().getBean("PermissionService");
        
        this.smallNodeService = (NodeService)AlfrescoUtils.getApplicationContext().getBean("nodeService");
        this.smallContentService = (ContentService)AlfrescoUtils.getApplicationContext().getBean("contentService");
        
        this.dataLoaderComponent = (DataLoaderComponent)AlfrescoUtils.getApplicationContext().getBean("dataLoaderComponent");
    }
    
    @Override
    public synchronized void prepare(final TestCase tc)
    {
        try
        {    
            super.prepare(tc);
            
            if (this.useUsers == true)
            {            
                // Set the authentication
                this.authenticationComponent.setSystemUserAsCurrentUser();            
                try
                {           
                    // Get the root folders
                    final List<NodeRef> rootFolders = AlfrescoUtils.getRootFolders(this.searchService, this.nodeService);
                    
                    if (BaseAlfrescoDriver.usersPrepaired == false)
                    {
                        System.out.println("Preparing useres");
                        TransactionUtil.executeInUserTransaction(this.transactionService, new TransactionUtil.TransactionWork<Object>()
                        {
                            public Object doWork() throws Exception
                            { 
                                // Get the number of available users
                                int numberOfAvailableUsers = DEFAULT_NUMBER_OF_AVAILABLE_USERS;
                                if (tc.hasParam(PARAM_NUMBER_OF_AVAILABLE_USERS) == true)
                                {
                                    numberOfAvailableUsers = tc.getIntParam(PARAM_NUMBER_OF_AVAILABLE_USERS);
                                }
                                
                                // Get a list of the users and ensure they all have permissions on the root node
                                List<String> users = AlfrescoUtils.prepairUsers(
                                        BaseAlfrescoDriver.this.dataLoaderComponent, 
                                        BaseAlfrescoDriver.this.personService, 
                                        BaseAlfrescoDriver.this.nodeService,
                                        numberOfAvailableUsers);
                                
                                for (NodeRef rootFolder : rootFolders)
                                {
                                    for (String userName : users)
                                    {
                                        // TODO how do we check this without doing it over and over again!!
                                        BaseAlfrescoDriver.this.permissionService.setPermission(rootFolder, userName, PermissionService.FULL_CONTROL, true);   
                                    }
                                    if (BaseAlfrescoDriver.this.permissionService.getInheritParentPermissions(rootFolder) == false)
                                    {
                                        BaseAlfrescoDriver.this.permissionService.setInheritParentPermissions(rootFolder, true);
                                    }
                                }
                                
                                BaseAlfrescoDriver.usersPrepaired = true;
                                
                                return null;
                            }
                        });
                        
                        System.out.println("Prepare complete");
                    }
                }
                finally
                {
                    this.authenticationComponent.clearCurrentSecurityContext();
                }            
            }
        }
        catch (Throwable exception)
        {
            exception.printStackTrace();
        }
    }    
    
    @Override
    public void preRun(TestCase testCase)
    {
        // Clear the parameter values
        testCase.setLongParam(PARAM_CONTENT_SIZE, 0);
        testCase.setParam(PARAM_CONTENT_MIMETYPE, "");
        
        if (this.useUsers == true)
        {
            this.authenticationComponent.setSystemUserAsCurrentUser();
            try
            {
                // Get content property values
                this.contentPropertyValues = DataProviderComponent.getInstance().getPropertyData(
                        this.repositoryProfile, 
                        AlfrescoUtils.getContentPropertyProfiles());
                
                // Get folder property values
                List<PropertyProfile> folderPropertyProfiles = new ArrayList<PropertyProfile>();
                
                PropertyProfile name = PropertyProfile.createSmallTextProperty(ContentModel.PROP_NAME.toString());
                folderPropertyProfiles.add(name);
                this.folderPropertyValues = DataProviderComponent.getInstance().getPropertyData(
                        this.repositoryProfile, 
                        folderPropertyProfiles);
               
                
                
                // Get the user name to use for this run
                this.userName = AlfrescoUtils.getUserName();            
            }
            finally
            {
                this.authenticationComponent.clearCurrentSecurityContext();
            }
        }
        
        // Get the folder and content node references
        this.folderNodeRef = AlfrescoUtils.getRandomFolder();
        this.contentNodeRef = AlfrescoUtils.getRandomContent();
    }
    
    @Override
    public void postRun(TestCase tc)
    {
        // Store the user name for later use
        tc.setParam(PARAM_USER_NAME, BaseAlfrescoDriver.this.userName);
        
        // Release the user
        AlfrescoUtils.releaseUserName(BaseAlfrescoDriver.this.userName);
    }
    
    @Override
    public void finish(TestCase testCase) 
    {
        super.finish(testCase);
        usersPrepaired = false;
    }
}

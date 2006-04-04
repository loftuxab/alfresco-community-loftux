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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.benchmark.dataloader.DataLoaderComponent;
import org.alfresco.benchmark.dataprovider.ContentData;
import org.alfresco.benchmark.dataprovider.DataProviderComponent;
import org.alfresco.benchmark.dataprovider.PropertyProfile;
import org.alfresco.benchmark.dataprovider.PropertyProfile.PropertyType;
import org.alfresco.benchmark.util.AlfrescoUtils;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.repo.transaction.TransactionUtil;
import org.alfresco.repo.version.common.VersionImpl;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;

import com.sun.japex.TestCase;

/**
 * @author Roy Wetherall
 */
public class AlfrescoInternalAPIDriver extends BaseAlfrescoBenchmarkDriver
{    
    private NodeService nodeService;
    private ContentService contentService;
    private VersionService versionService;
    private AuthenticationComponent authenticationComponent;
    private TransactionService transactionService;
    private PersonService personService;
    private PermissionService permissionService;
    
    private DataProviderComponent dataProviderComponent;
    private DataLoaderComponent dataLoaderComponent;
    
    private Map<String, Object> contentPropertyValues; 
    private Map<String, Object> folderPropertyValues;
    private NodeRef rootFolder;
    private NodeRef contentNodeRef;
    private NodeRef folderNodeRef;
    private String userName;
    
    private static boolean usersPrepaired = false;
    
    @Override
    public void initializeDriver()
    {
        // Get the required services
        this.nodeService = (NodeService)AlfrescoUtils.getApplicationContext().getBean("NodeService");
        this.contentService = (ContentService)AlfrescoUtils.getApplicationContext().getBean("ContentService");
        this.authenticationComponent = (AuthenticationComponent)AlfrescoUtils.getApplicationContext().getBean("authenticationComponent");
        this.transactionService = (TransactionService)AlfrescoUtils.getApplicationContext().getBean("transactionComponent");
        this.versionService = (VersionService)AlfrescoUtils.getApplicationContext().getBean("VersionService");
        this.personService = (PersonService)AlfrescoUtils.getApplicationContext().getBean("PersonService");
        this.permissionService = (PermissionService)AlfrescoUtils.getApplicationContext().getBean("PermissionService");
        
        this.dataProviderComponent = (DataProviderComponent)AlfrescoUtils.getApplicationContext().getBean("dataProviderComponent");
        this.dataLoaderComponent = (DataLoaderComponent)AlfrescoUtils.getApplicationContext().getBean("dataLoaderComponent");
    }
    
    @Override
    public synchronized void prepare(final TestCase tc)
    {
        try
        {    
            super.prepare(tc);             
            
            // Set the authentication
            this.authenticationComponent.setSystemUserAsCurrentUser();
            
            try
            {           
                // Get the test case folder node ref
                this.rootFolder = AlfrescoUtils.getTestCaseRootFolder(
                        this.dataLoaderComponent, 
                        this.nodeService, 
                        this.repositoryProfile, 
                        tc);
                
                if (AlfrescoInternalAPIDriver.usersPrepaired == false)
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
                                    AlfrescoInternalAPIDriver.this.dataLoaderComponent, 
                                    AlfrescoInternalAPIDriver.this.personService, 
                                    AlfrescoInternalAPIDriver.this.nodeService,
                                    numberOfAvailableUsers);
                            for (String userName : users)
                            {
                                // TODO how do we check this without doing it over and over again!!
                                AlfrescoInternalAPIDriver.this.permissionService.setPermission(AlfrescoInternalAPIDriver.this.rootFolder, userName, PermissionService.FULL_CONTROL, true);   
                            }
                            if (AlfrescoInternalAPIDriver.this.permissionService.getInheritParentPermissions(AlfrescoInternalAPIDriver.this.rootFolder) == false)
                            {
                                AlfrescoInternalAPIDriver.this.permissionService.setInheritParentPermissions(AlfrescoInternalAPIDriver.this.rootFolder, true);
                            }
                            
                            AlfrescoInternalAPIDriver.usersPrepaired = true;
                            
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
        catch (Throwable exception)
        {
            exception.printStackTrace();
        }
    }    
    
    @Override
    public void preRun(TestCase testCase)
    {
        this.authenticationComponent.setSystemUserAsCurrentUser();
        try
        {
            // Get content property values
            this.contentPropertyValues = AlfrescoInternalAPIDriver.this.dataProviderComponent.getPropertyData(
                    this.repositoryProfile, 
                    AlfrescoUtils.getContentPropertyProfiles());
            
            // Get folder property values
            List<PropertyProfile> folderPropertyProfiles = new ArrayList<PropertyProfile>();
            
            PropertyProfile name = new PropertyProfile();
            name.setPropertyName(ContentModel.PROP_NAME.toString());
            name.setPropertyType(PropertyType.TEXT);
            folderPropertyProfiles.add(name);
            this.folderPropertyValues = dataProviderComponent.getPropertyData(
                    this.repositoryProfile, 
                    folderPropertyProfiles);
           
            // Get the folder and content node references
            this.folderNodeRef = AlfrescoUtils.getRandomFolder(testCase);
            this.contentNodeRef = AlfrescoUtils.getRandomContent(testCase);
            
            // Get the user name to use for this run
            this.userName = AlfrescoUtils.getUserName();            
        }
        finally
        {
            this.authenticationComponent.clearCurrentSecurityContext();
        }
    }
    
    @Override
    public void postRun(TestCase tc)
    {
        // Store the user name for later use
        tc.setParam(PARAM_USER_NAME, AlfrescoInternalAPIDriver.this.userName);
        
        // Release the user
        AlfrescoUtils.releaseUserName(AlfrescoInternalAPIDriver.this.userName);
    }
    
    @Override
    public void finish(TestCase testCase) 
    {
        super.finish(testCase);
        usersPrepaired = false;
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
                    try
                    {
                        AlfrescoInternalAPIDriver.this.authenticationComponent.setCurrentUser(AlfrescoInternalAPIDriver.this.userName);  
                        try
                        {
                            AlfrescoUtils.createContentNode(
                                    AlfrescoInternalAPIDriver.this.nodeService, 
                                    AlfrescoInternalAPIDriver.this.contentService, 
                                    AlfrescoInternalAPIDriver.this.contentPropertyValues, 
                                    AlfrescoInternalAPIDriver.this.folderNodeRef);
                            
                            // Store the content size for later use
                            ContentData contentData = (ContentData)AlfrescoInternalAPIDriver.this.contentPropertyValues.get(ContentModel.PROP_CONTENT.toString());
                            tc.setParam(PARAM_CONTENT_SIZE, Integer.toString(contentData.getSize()));
                            tc.setParam(PARAM_CONTENT_MIMETYPE, contentData.getMimetype());                                                       
                        }
                        catch (Throwable exception)
                        {
                            exception.printStackTrace();
                        }
                    }
                    finally
                    {
                        AlfrescoInternalAPIDriver.this.authenticationComponent.clearCurrentSecurityContext();                        
                    }
                    
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
                    AlfrescoInternalAPIDriver.this.authenticationComponent.setCurrentUser(AlfrescoInternalAPIDriver.this.userName);  
                    try
                    {
                        // Read the content
                        ContentReader contentReader = AlfrescoInternalAPIDriver.this.contentService.getReader(
                                AlfrescoInternalAPIDriver.this.contentNodeRef, 
                                ContentModel.PROP_CONTENT);
                        contentReader.getContent(File.createTempFile("benchmark", "temp"));
                        
                        // Store the content size for later use
                        tc.setParam(PARAM_CONTENT_SIZE, Long.toString(contentReader.getSize()));
                        tc.setParam(PARAM_CONTENT_MIMETYPE, contentReader.getMimetype());
                        
                        // Do nothing on return 
                        return null;
                    }
                    finally
                    {
                        AlfrescoInternalAPIDriver.this.authenticationComponent.clearCurrentSecurityContext();                        
                    }
                    
                }
        
            });            
        }
        catch (Throwable exception)
        {
            exception.printStackTrace();
        }         
    }

    @Override
    protected void doCreateFolder(TestCase tc)
    {
        try
        {
            TransactionUtil.executeInUserTransaction(this.transactionService, new TransactionUtil.TransactionWork<Object>()
            {
                public Object doWork() throws Exception
                {
                    AlfrescoInternalAPIDriver.this.authenticationComponent.setCurrentUser(AlfrescoInternalAPIDriver.this.userName);  
                    try
                    {
                        // Create a named folder
                        String nameValue = (String)AlfrescoInternalAPIDriver.this.folderPropertyValues.get(ContentModel.PROP_NAME.toString());
                        Map<QName, Serializable> folderProps = new HashMap<QName, Serializable>();
                        folderProps.put(ContentModel.PROP_NAME, nameValue);
                        nodeService.createNode(
                                AlfrescoInternalAPIDriver.this.folderNodeRef, 
                                ContentModel.ASSOC_CONTAINS, 
                                QName.createQName(NamespaceService.APP_MODEL_1_0_URI, nameValue),
                                ContentModel.TYPE_FOLDER,
                                folderProps).getChildRef();
                    }
                    finally
                    {
                        AlfrescoInternalAPIDriver.this.authenticationComponent.clearCurrentSecurityContext();                        
                    }
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
    protected void doCreateVersion(TestCase tc)
    {   
        try
        {
            TransactionUtil.executeInUserTransaction(this.transactionService, new TransactionUtil.TransactionWork<Object>()
            {
                public Object doWork() throws Exception
                {
                    AlfrescoInternalAPIDriver.this.authenticationComponent.setCurrentUser(AlfrescoInternalAPIDriver.this.userName);  
                    try
                    {
                        if (AlfrescoInternalAPIDriver.this.nodeService.hasAspect(AlfrescoInternalAPIDriver.this.contentNodeRef, ContentModel.ASPECT_VERSIONABLE) == false)
                        {
                            // Add the versionable aspect, turning off auto-version to avoid unexpected behaviour
                            Map<QName, Serializable> properties = new HashMap<QName, Serializable>(2);
                            properties.put(ContentModel.PROP_AUTO_VERSION, false);
                            properties.put(ContentModel.PROP_INITIAL_VERSION, false);
                            AlfrescoInternalAPIDriver.this.nodeService.addAspect(
                                    AlfrescoInternalAPIDriver.this.contentNodeRef,
                                    ContentModel.ASPECT_VERSIONABLE,
                                    properties);
                        }
                        
                        // Create a version in the version history of this node
                        Map<String, Serializable> versionProperties = new HashMap<String, Serializable>(1);
                        versionProperties.put(VersionImpl.PROP_DESCRIPTION, "This is the description of the version change.");
                        AlfrescoInternalAPIDriver.this.versionService.createVersion(
                               AlfrescoInternalAPIDriver.this.contentNodeRef,
                               versionProperties);
                    }
                    finally
                    {
                        AlfrescoInternalAPIDriver.this.authenticationComponent.clearCurrentSecurityContext();                        
                    }
                    
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
    protected void doReadProperties(TestCase tc)
    {  
        try
        {
            TransactionUtil.executeInUserTransaction(this.transactionService, new TransactionUtil.TransactionWork<Object>()
            {
                public Object doWork() throws Exception
                {
                    AlfrescoInternalAPIDriver.this.authenticationComponent.setCurrentUser(AlfrescoInternalAPIDriver.this.userName);  
                    try
                    {
                        // Read all the properties of the content node
                        AlfrescoInternalAPIDriver.this.nodeService.getProperties(AlfrescoInternalAPIDriver.this.contentNodeRef);
                    }
                    finally
                    {
                        AlfrescoInternalAPIDriver.this.authenticationComponent.clearCurrentSecurityContext();                        
                    }
                    
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

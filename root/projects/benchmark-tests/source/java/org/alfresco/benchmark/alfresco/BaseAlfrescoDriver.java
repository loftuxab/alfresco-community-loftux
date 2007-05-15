/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
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
package org.alfresco.benchmark.alfresco;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.acegisecurity.Authentication;

import org.alfresco.benchmark.framework.BaseBenchmarkDriver;
import org.alfresco.benchmark.framework.BenchmarkUtils;
import org.alfresco.benchmark.framework.DataLoaderComponent;
import org.alfresco.benchmark.framework.dataprovider.DataProviderComponent;
import org.alfresco.benchmark.framework.dataprovider.PropertyProfile;
import org.alfresco.benchmark.framework.dataprovider.RepositoryProfile;
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
import org.alfresco.service.namespace.NamespaceService;
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
    protected NamespaceService namespaceService;
    
    protected NodeService smallNodeService;
    protected ContentService smallContentService;
    
    protected DataLoaderComponent dataLoaderComponent;
    
    protected Map<String, Object> contentPropertyValues; 
    protected Map<String, Object> folderPropertyValues;
   
    protected NodeRef rootDataNodeRef;
    protected String userName;

    private NodeRef randomParentFolderNodeRef;
    private NodeRef randomTargetFileNodeRef;
    
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
        this.namespaceService = (NamespaceService)AlfrescoUtils.getApplicationContext().getBean("NamespaceService");
        
        this.smallNodeService = (NodeService)AlfrescoUtils.getApplicationContext().getBean("nodeService");
        this.smallContentService = (ContentService)AlfrescoUtils.getApplicationContext().getBean("contentService");
        
        this.dataLoaderComponent = (DataLoaderComponent)AlfrescoUtils.getApplicationContext().getBean("dataLoaderComponent");
    }
    
    @Override
    public synchronized void prepare(final TestCase tc)
    {  
            super.prepare(tc);
            
            if (this.useUsers == true)
            {            
                // Set the authentication
                this.authenticationComponent.setSystemUserAsCurrentUser();            
                try
                {           
                    // Get the root folder
                    this.rootDataNodeRef = AlfrescoUtils.getRootTestDataFolder(this.searchService, this.nodeService);
                    
                    if (BaseAlfrescoDriver.usersPrepaired == false)
                    {
                        System.out.println("\nPreparing Users ...");
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
                                
                                for (String userName : users)
                                {
                                    // TODO how do we check this without doing it over and over again!!
                                    BaseAlfrescoDriver.this.permissionService.setPermission(BaseAlfrescoDriver.this.rootDataNodeRef, userName, PermissionService.FULL_CONTROL, true);   
                                }
                                if (BaseAlfrescoDriver.this.permissionService.getInheritParentPermissions(BaseAlfrescoDriver.this.rootDataNodeRef) == false)
                                {
                                    BaseAlfrescoDriver.this.permissionService.setInheritParentPermissions(BaseAlfrescoDriver.this.rootDataNodeRef, true);
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
    
    @Override
    public void preRun(TestCase testCase)
    {
        // Get content property values
        this.contentPropertyValues = DataProviderComponent.getInstance().getPropertyData(
                AlfrescoUtils.getContentPropertyProfiles());
        
        // Get folder property values
        List<PropertyProfile> folderPropertyProfiles = new ArrayList<PropertyProfile>();
        
        PropertyProfile name = PropertyProfile.createSmallTextProperty(ContentModel.PROP_NAME.toString());
        folderPropertyProfiles.add(name);
        this.folderPropertyValues = DataProviderComponent.getInstance().getPropertyData(
                folderPropertyProfiles);
        
        // Get the random file and folder paths
        int loadDepth = 0;
        if (testCase.hasParam(PARAM_LOAD_DEPTH) == true)
        {
            loadDepth = testCase.getIntParam(PARAM_LOAD_DEPTH);
        }
        RepositoryProfile repositoryProfile = AlfrescoUtils.getRepositoryProfile(this.authenticationComponent, this.searchService, this.nodeService);
        String randomTargetFilePath = null;
        String randomParentFolderPath = null;
        if (loadDepth <= 0)
        {
            randomParentFolderPath = BenchmarkUtils.getRandomFolderPath(repositoryProfile, true);
            randomTargetFilePath = BenchmarkUtils.getRandomFilePath(repositoryProfile, true);
        }
        else
        {
            randomParentFolderPath = BenchmarkUtils.getRandomFolderPath(repositoryProfile, loadDepth - 1, true);
            randomTargetFilePath = BenchmarkUtils.getRandomFilePath(repositoryProfile, loadDepth, true);
        }
        Authentication authentication = this.authenticationComponent.setSystemUserAsCurrentUser();            
        try
        {           
            this.randomParentFolderNodeRef = resolvePath(randomParentFolderPath);
            this.randomTargetFileNodeRef = resolvePath(randomTargetFilePath);
        }
        finally
        {
            this.authenticationComponent.setCurrentAuthentication(authentication);
        }
        
        if (this.useUsers == true)
        {
            // Get the user name to use for this run
             this.userName = AlfrescoUtils.getUserName();            
        }
    }
    
    @Override
    public void postRun(TestCase tc)
    {
        if (this.useUsers == true)
        {
            // Release the user
            AlfrescoUtils.releaseUserName(BaseAlfrescoDriver.this.userName);
        }
    }
    
    @Override
    public void finish(TestCase testCase) 
    {
        super.finish(testCase);
        usersPrepaired = false;
    }
    
    protected NodeRef getRandomParentFolderNodeRef()
    {
        return randomParentFolderNodeRef;
    }
    
    protected NodeRef getRandomTargetFileNodeRef()
    {
        return randomTargetFileNodeRef;
    }
    
    private NodeRef resolvePath(String path)
    {
        NodeRef nodeRef = null;
        List<NodeRef> nodeRefs = this.searchService.selectNodes(
                this.rootDataNodeRef,
                path,
                null,
                this.namespaceService,
                false);
        if (nodeRefs.size() == 1)
        {
            nodeRef = nodeRefs.get(0);
        }
        else
        {
            throw new RuntimeException("Unable to resolve path: path");
        }
        return nodeRef;
    }
}

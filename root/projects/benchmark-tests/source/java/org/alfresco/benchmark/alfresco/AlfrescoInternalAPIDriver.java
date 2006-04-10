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
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.benchmark.framework.UnitsOfWork;
import org.alfresco.benchmark.framework.dataprovider.ContentData;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.transaction.TransactionUtil;
import org.alfresco.repo.version.common.VersionImpl;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

import com.sun.japex.TestCase;

/**
 * @author Roy Wetherall
 */
public class AlfrescoInternalAPIDriver extends BaseAlfrescoDriver implements UnitsOfWork
{
    /**
     * @see org.alfresco.benchmark.framework.UnitsOfWork#doCreateContentBenchmark(com.sun.japex.TestCase)
     */
    public void doCreateContentBenchmark(final TestCase tc)
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

    public void doReadContentBenchmark(final TestCase tc)
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

    public void doCreateFolder(TestCase tc)
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

    public void doCreateVersion(TestCase tc)
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

    public void doReadProperties(TestCase tc)
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

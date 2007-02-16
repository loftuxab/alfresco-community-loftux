/*
 * Copyright (C) 2005 Alfresco, Inc.
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

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.benchmark.framework.BenchmarkUtils;
import org.alfresco.benchmark.framework.UnitsOfWork;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.filestore.FileContentReader;
import org.alfresco.repo.transaction.TransactionUtil;
import org.alfresco.repo.version.common.VersionImpl;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.TempFileProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.japex.TestCase;

/**
 * @author Roy Wetherall
 */
public class AlfrescoInternalAPIDriver extends BaseAlfrescoDriver implements UnitsOfWork
{
    private static final Log logger = LogFactory.getLog(AlfrescoInternalAPIDriver.class);
    
    /**
     * @see org.alfresco.benchmark.framework.UnitsOfWork#doCreateContentBenchmark(com.sun.japex.TestCase)
     */
    public void doCreateContentBenchmark(final TestCase tc)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(
                    "Creating content using Alfresco internal API: \n" +
                    "   TestCase: " + tc + "\n" +
                    "   Driver: " + this);
        }

        TransactionUtil.executeInUserTransaction(this.transactionService, new TransactionUtil.TransactionWork<Object>()
        {
            public Object doWork() throws Exception
            {                    
                try
                {
                    AlfrescoInternalAPIDriver.this.authenticationComponent.setCurrentUser(AlfrescoInternalAPIDriver.this.userName);  
                    try
                    {
                        NodeRef folderNodeRef = getRandomParentFolderNodeRef();
                        AlfrescoUtils.createContentNode(
                                AlfrescoInternalAPIDriver.this.nodeService, 
                                AlfrescoInternalAPIDriver.this.contentService, 
                                AlfrescoInternalAPIDriver.this.contentPropertyValues, 
                                folderNodeRef);
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

    public void doReadContentBenchmark(final TestCase tc)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(
                    "Reading content using Alfresco internal API: \n" +
                    "   TestCase: " + tc + "\n" +
                    "   Driver: " + this);
        }

        TransactionUtil.executeInUserTransaction(this.transactionService, new TransactionUtil.TransactionWork<Object>()
        {
            public Object doWork() throws Exception
            {
                AlfrescoInternalAPIDriver.this.authenticationComponent.setCurrentUser(AlfrescoInternalAPIDriver.this.userName);  
                try
                {
                    NodeRef contentNodeRef = getRandomTargetFileNodeRef();
                    
                    // Read the content
                    ContentReader contentReader = AlfrescoInternalAPIDriver.this.contentService.getReader(
                            contentNodeRef, 
                            ContentModel.PROP_CONTENT);
                    contentReader = FileContentReader.getSafeContentReader(contentReader, "File missing");
                    File tempFile = TempFileProvider.createTempFile("benchmark", ".tmp"); 
                    contentReader.getContent(tempFile);
                    
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

    public void doCreateFolder(TestCase tc)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(
                    "Creating folder using Alfresco internal API: \n" +
                    "   TestCase: " + tc + "\n" +
                    "   Driver: " + this);
        }

        TransactionUtil.executeInUserTransaction(this.transactionService, new TransactionUtil.TransactionWork<Object>()
        {
            public Object doWork() throws Exception
            {
                AlfrescoInternalAPIDriver.this.authenticationComponent.setCurrentUser(AlfrescoInternalAPIDriver.this.userName);  
                try
                {
                    NodeRef folderNodeRef = getRandomParentFolderNodeRef();
                    
                    // Create a named folder
                    String nameValue = "folder_" + BenchmarkUtils.getGUID();
                    Map<QName, Serializable> folderProps = new HashMap<QName, Serializable>();
                    folderProps.put(ContentModel.PROP_NAME, nameValue);
                    nodeService.createNode(
                            folderNodeRef, 
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

    public void doCreateVersion(TestCase tc)
    {   
        if (logger.isDebugEnabled())
        {
            logger.debug(
                    "Creating version using Alfresco internal API: \n" +
                    "   TestCase: " + tc + "\n" +
                    "   Driver: " + this);
        }
        
        TransactionUtil.executeInUserTransaction(this.transactionService, new TransactionUtil.TransactionWork<Object>()
        {
            public Object doWork() throws Exception
            {
                AlfrescoInternalAPIDriver.this.authenticationComponent.setCurrentUser(AlfrescoInternalAPIDriver.this.userName);  
                try
                {
                    NodeRef contentNodeRef = getRandomTargetFileNodeRef();
                    
                    if (AlfrescoInternalAPIDriver.this.nodeService.hasAspect(contentNodeRef, ContentModel.ASPECT_VERSIONABLE) == false)
                    {
                        // Add the versionable aspect, turning off auto-version to avoid unexpected behaviour
                        Map<QName, Serializable> properties = new HashMap<QName, Serializable>(2);
                        properties.put(ContentModel.PROP_AUTO_VERSION, false);
                        properties.put(ContentModel.PROP_INITIAL_VERSION, false);
                        AlfrescoInternalAPIDriver.this.nodeService.addAspect(
                                contentNodeRef,
                                ContentModel.ASPECT_VERSIONABLE,
                                properties);
                    }
                    
                    // Create a version in the version history of this node
                    Map<String, Serializable> versionProperties = new HashMap<String, Serializable>(1);
                    versionProperties.put(VersionImpl.PROP_DESCRIPTION, "This is the description of the version change.");
                    AlfrescoInternalAPIDriver.this.versionService.createVersion(
                           contentNodeRef,
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

    public void doReadProperties(TestCase tc)
    {  
        if (logger.isDebugEnabled())
        {
            logger.debug(
                    "Reading properties using Alfresco internal API: \n" +
                    "   TestCase: " + tc + "\n" +
                    "   Driver: " + this);
        }

        TransactionUtil.executeInUserTransaction(this.transactionService, new TransactionUtil.TransactionWork<Object>()
        {
            public Object doWork() throws Exception
            {
                AlfrescoInternalAPIDriver.this.authenticationComponent.setCurrentUser(AlfrescoInternalAPIDriver.this.userName);  
                try
                {
                    // Read all the properties of the content node
                    NodeRef contentNodeRef = getRandomTargetFileNodeRef();
                    AlfrescoInternalAPIDriver.this.nodeService.getProperties(contentNodeRef);
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
}

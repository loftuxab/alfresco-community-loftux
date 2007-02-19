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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.benchmark.framework.BenchmarkUtils;
import org.alfresco.benchmark.framework.dataprovider.ContentData;
import org.alfresco.benchmark.framework.dataprovider.DataProviderComponent;
import org.alfresco.benchmark.framework.dataprovider.PropertyProfile;
import org.alfresco.benchmark.framework.dataprovider.PropertyProfile.PropertyType;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.transaction.TransactionUtil;
import org.alfresco.repo.transaction.TransactionUtil.TransactionWork;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.japex.TestCase;

/**
 * @author Roy Wetherall
 */
public class AlfrescoBulkLoadDriver extends BaseAlfrescoDriver
{
    private static final Log logger = LogFactory.getLog(AlfrescoBulkLoadDriver.class);
    
    public static final String PARAM_FOLDER_COUNT = "alfresco.folderCount";
    public static final String PARAM_FILE_COUNT = "alfresco.fileCount";
    public static final int    DEFAULT_FOLDER_COUNT = 1;
    public static final int    DEFAULT_FILE_COUNT = 1000;
    
    private int folderCount = DEFAULT_FOLDER_COUNT;
    private int fileCount =   DEFAULT_FILE_COUNT;
    
    private boolean createFolder = true;
    
    private ContentData[] contentData;
    
    @Override
    public synchronized void prepare(TestCase tc)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(
                    "Driver preparing: \n" +
                    "   TestCase: " + tc + "\n" +
                    "   Driver: " + this);
        }
        
        super.prepare(tc);
        
        // Get the folder and file count vlaues
        if (tc.hasParam(PARAM_FOLDER_COUNT) == true)
        {
            this.folderCount = tc.getIntParam(PARAM_FOLDER_COUNT);
            if (this.folderCount == 0)
            {
                this.folderCount = 1;
                this.createFolder = false;
            }
        }
        if (tc.hasParam(PARAM_FILE_COUNT) == true)
        {
            this.fileCount = tc.getIntParam(PARAM_FILE_COUNT);
        }
    }
    
    @Override
    public void preRun(TestCase testCase)
    {
        super.preRun(testCase);
        
        int contentCount = this.fileCount*this.folderCount;
        this.contentData = new ContentData[contentCount];
        
        // Get the content data to be used in the test
        List<PropertyProfile> contentPropertyProfiles = new ArrayList<PropertyProfile>();
        contentPropertyProfiles.add(new PropertyProfile(ContentModel.PROP_CONTENT.toString(), PropertyType.CONTENT));
        
        for (int i = 0; i < contentCount; i++)
        {
            Map<String, Object> propertyValues = DataProviderComponent.getInstance().getPropertyData(contentPropertyProfiles);
            this.contentData[i] = (ContentData)propertyValues.get(ContentModel.PROP_CONTENT.toString());
        }        
    }
    
    @Override
    public void run(final TestCase testCase)
    {        
        TransactionUtil.executeInUserTransaction(this.transactionService, new TransactionWork<Object>()
        {
            public Object doWork() throws Exception
            {
                AlfrescoBulkLoadDriver.this.authenticationComponent.setSystemUserAsCurrentUser();
                try
                {                    
                    // Do bulk load
                    doBulkUpload(testCase);
                }
                finally
                {
                    AlfrescoBulkLoadDriver.this.authenticationComponent.clearCurrentSecurityContext();
                }
                
                return null;
            }            
        });        
    }
    
    private void doBulkUpload(TestCase testCase)
    {
        // Get the folder
        NodeRef folderNodeRef = getRandomParentFolderNodeRef();
        
        int contentPropIndex = 0;
        for (int folderIndex = 0; folderIndex < this.folderCount; folderIndex++)
        {   
            NodeRef bulkLoadFolder = null;
            if (this.createFolder == true)
            {
                // Create the folder to load the data into
                String nameValue = "bulkLoad_" + BenchmarkUtils.getGUID();
                Map<QName, Serializable> folderProps = new HashMap<QName, Serializable>();
                folderProps.put(ContentModel.PROP_NAME, nameValue);
                bulkLoadFolder = smallNodeService.createNode(
                        folderNodeRef, 
                        ContentModel.ASSOC_CONTAINS, 
                        QName.createQName(NamespaceService.APP_MODEL_1_0_URI, nameValue),
                        ContentModel.TYPE_FOLDER,
                        folderProps).getChildRef();
            }
            else
            {
                bulkLoadFolder = folderNodeRef;
            }
            
            for (int fileIndex = 0; fileIndex < this.fileCount; fileIndex++)
            {
                // Create a new content object
                String contentName = "bulkLoad_" + BenchmarkUtils.getGUID();
                Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
                properties.put(ContentModel.PROP_NAME, contentName);
                NodeRef contentNodeRef = smallNodeService.createNode(
                        bulkLoadFolder, 
                        ContentModel.ASSOC_CONTAINS, 
                        QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, contentName),
                        ContentModel.TYPE_CONTENT,
                        properties).getChildRef();
                
                // Get the content details
                ContentData contentData = this.contentData[contentPropIndex];
                
                // Upload the content
                ContentWriter contentWriter = this.smallContentService.getWriter(contentNodeRef, ContentModel.PROP_CONTENT, true);           
                contentWriter.setEncoding(contentData.getEncoding());
                contentWriter.setMimetype(contentData.getMimetype());
                contentWriter.putContent(contentData.getFile());
                
                contentPropIndex++;
            }
        }
    }
}

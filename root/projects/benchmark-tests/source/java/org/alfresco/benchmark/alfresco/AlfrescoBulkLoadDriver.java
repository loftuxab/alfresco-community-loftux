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

import com.sun.japex.TestCase;

/**
 * @author Roy Wetherall
 */
public class AlfrescoBulkLoadDriver extends BaseAlfrescoDriver
{
    public static final String PARAM_LOAD_COUNT = "alfresco.loadCount";
    public static final int DEFAULT_LOAD_COUNT = 1000;
    
    private int loadCount = DEFAULT_LOAD_COUNT;
    
    private ContentData[] contentData;
    
    @Override
    public synchronized void prepare(TestCase tc)
    {
        super.prepare(tc);
        
        // Get the load count
        if (tc.hasParam(PARAM_LOAD_COUNT) == true)
        {
            this.loadCount = tc.getIntParam(PARAM_LOAD_COUNT);
        }
    }
    
    @Override
    public void preRun(TestCase testCase)
    {
        super.preRun(testCase);
        
        this.contentData = new ContentData[this.loadCount];
        
        // Get the content data to be used in the test
        List<PropertyProfile> contentPropertyProfiles = new ArrayList<PropertyProfile>();
        contentPropertyProfiles.add(new PropertyProfile(ContentModel.PROP_CONTENT.toString(), PropertyType.CONTENT));
        
        for (int i = 0; i < loadCount; i++)
        {
            Map<String, Object> propertyValues = DataProviderComponent.getInstance().getPropertyData(this.repositoryProfile, contentPropertyProfiles);
            this.contentData[i] = (ContentData)propertyValues.get(ContentModel.PROP_CONTENT.toString());
        }        
    }
    
    @Override
    public void run(final TestCase testCase)
    {
        try
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
        catch (Throwable exception)
        {
            exception.printStackTrace();
        }
    }
    
    private void doBulkUpload(TestCase testCase)
    {
        // Create the folder to load the data into
        String nameValue = "bulkLoad_" + BenchmarkUtils.getGUID();
        Map<QName, Serializable> folderProps = new HashMap<QName, Serializable>();
        folderProps.put(ContentModel.PROP_NAME, nameValue);
        NodeRef bulkLoadFolder = nodeService.createNode(
                this.folderNodeRef, 
                ContentModel.ASSOC_CONTAINS, 
                QName.createQName(NamespaceService.APP_MODEL_1_0_URI, nameValue),
                ContentModel.TYPE_FOLDER,
                folderProps).getChildRef();
        
        for (int i = 0; i < this.loadCount; i++)
        {
            // Create a new content object
            String contentName = "bulkLoad_" + i;
            Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
            properties.put(ContentModel.PROP_NAME, contentName);
            NodeRef contentNodeRef = nodeService.createNode(
                    bulkLoadFolder, 
                    ContentModel.ASSOC_CONTAINS, 
                    QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, contentName),
                    ContentModel.TYPE_CONTENT,
                    properties).getChildRef();
            
            // Get the content details
            ContentData contentData = this.contentData[i];
            
            // Upload the content
            ContentWriter contentWriter = this.contentService.getWriter(contentNodeRef, ContentModel.PROP_CONTENT, true);           
            contentWriter.setEncoding(contentData.getEncoding());
            contentWriter.setMimetype(contentData.getMimetype());
            contentWriter.putContent(contentData.getFile());
            
            // Store the content size for later use
            testCase.setLongParam(PARAM_CONTENT_SIZE, testCase.getLongParam(PARAM_CONTENT_SIZE) + contentData.getSize());                        
        }
    }
}

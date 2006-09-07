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
package org.alfresco.benchmark.framework.jcr;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.alfresco.benchmark.framework.BaseBenchmarkDriver;
import org.alfresco.benchmark.framework.BenchmarkUtils;
import org.alfresco.benchmark.framework.dataprovider.ContentData;
import org.alfresco.benchmark.framework.dataprovider.DataProviderComponent;
import org.alfresco.benchmark.framework.dataprovider.PropertyProfile;
import org.alfresco.benchmark.framework.dataprovider.PropertyProfile.PropertyType;
import org.apache.log4j.Logger;

import com.sun.japex.TestCase;

/**
 * @author Roy Wetherall
 */
public class JCRBulkLoadDriver extends BaseBenchmarkDriver
{
	private static final Logger logger = Logger.getLogger(JCRBulkLoadDriver.class);
	
    protected Repository repository;
        
    public static final String PARAM_FOLDER_COUNT = "alfresco.folderCount";
    public static final String PARAM_FILE_COUNT = "alfresco.fileCount";
    public static final int DEFAULT_FOLDER_COUNT = 1000;
    public static final int DEFAULT_FILE_COUNT = 1;
    
    private int folderCount = DEFAULT_FOLDER_COUNT;
    private int fileCount = DEFAULT_FILE_COUNT;
    
    private ContentData[] contentData;
    
    @Override
    public synchronized void prepare(TestCase tc)
    {
        try
        {
            super.prepare(tc);
            
            // Get the folder and file count vlaues
            if (tc.hasParam(PARAM_FOLDER_COUNT) == true)
            {
                this.folderCount = tc.getIntParam(PARAM_FOLDER_COUNT);
            }
            if (tc.hasParam(PARAM_FILE_COUNT) == true)
            {
                this.fileCount = tc.getIntParam(PARAM_FILE_COUNT);
            }
        }
        catch (Throwable exception)
        {
            logger.error(exception);
        }
    }
    
    @Override
    public void preRun(TestCase testCase)
    {
        try
        {
            super.preRun(testCase);
        
            testCase.setLongParam(PARAM_CONTENT_SIZE, 0);
        
            int contentCount = this.fileCount*this.folderCount;
            this.contentData = new ContentData[contentCount];;
            
            // Get the content data to be used in the test
            List<PropertyProfile> contentPropertyProfiles = new ArrayList<PropertyProfile>();
            contentPropertyProfiles.add(new PropertyProfile(JCRUtils.PROP_CONTENT, PropertyType.CONTENT));
            
            for (int i = 0; i < contentCount; i++)
            {
                Map<String, Object> propertyValues = DataProviderComponent.getInstance().getPropertyData(contentPropertyProfiles);
                this.contentData[i] = (ContentData)propertyValues.get(JCRUtils.PROP_CONTENT);
            }
        }
        catch (Throwable exception)
        {
            logger.error(exception);
        }
    }
    
    @Override
    public void run(final TestCase testCase)
    {
        try
        {
            Session session = this.repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
            try
            {     
                // TODO pick the parent node from the available set
                Node parentNode = session.getRootNode();
                
                int contentPropIndex = 0;
                for (int folderIndex = 0; folderIndex < this.folderCount; folderIndex++)
                {                    
                    // Create the folder to load the data into
                    String nameValue = "bulkLoad_" + BenchmarkUtils.getGUID();
                    Node folderNode = parentNode.addNode (nameValue, "nt:folder");  
                    
                    for (int fileIndex = 0; fileIndex < this.fileCount; fileIndex++)
                    {
                        String contentName = "bulkLoad_" + fileIndex;
                        ContentData contentData = this.contentData[contentPropIndex];                    
                        Node fileNode = folderNode.addNode(contentName, "nt:file");
                    
                        // Add the content
                        Node resNode = fileNode.addNode ("jcr:content", "nt:resource");
                        resNode.setProperty ("jcr:mimeType", contentData.getMimetype());
                        resNode.setProperty ("jcr:encoding", contentData.getEncoding());
                        resNode.setProperty ("jcr:data", new FileInputStream(contentData.getFile()));
                        
                        // Need to set the mandatory 'lastModified' property
                        Calendar lastModified = Calendar.getInstance ();
                        lastModified.setTimeInMillis (contentData.getFile().lastModified ());
                        resNode.setProperty ("jcr:lastModified", lastModified);
                        
                        // Store the content size for later use
                        testCase.setLongParam(PARAM_CONTENT_SIZE, testCase.getLongParam(PARAM_CONTENT_SIZE) + contentData.getSize());     
                        
                        contentPropIndex++;
                    }
                }
                
                // Save
                session.save();
            }
            catch (Throwable e)
            {
            	logger.error(e);
            }
            finally
            {
            	try {session.logout(); } catch (Throwable e) {logger.error(e); }
            }
        }
        catch (Throwable exception)
        {
        	logger.error(exception);
        }
    }
}

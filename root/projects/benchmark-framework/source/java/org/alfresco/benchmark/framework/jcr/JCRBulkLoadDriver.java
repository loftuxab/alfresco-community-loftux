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
        
        testCase.setLongParam(PARAM_CONTENT_SIZE, 0);
        
        this.contentData = new ContentData[this.loadCount];
        
        // Get the content data to be used in the test
        List<PropertyProfile> contentPropertyProfiles = new ArrayList<PropertyProfile>();
        contentPropertyProfiles.add(new PropertyProfile(JCRUtils.PROP_CONTENT, PropertyType.CONTENT));
        
        for (int i = 0; i < loadCount; i++)
        {
            Map<String, Object> propertyValues = DataProviderComponent.getInstance().getPropertyData(this.repositoryProfile, contentPropertyProfiles);
            this.contentData[i] = (ContentData)propertyValues.get(JCRUtils.PROP_CONTENT);
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
                
                // Create the folder to load the data into
                String nameValue = "bulkLoad_" + BenchmarkUtils.getGUID();
                Node folderNode = parentNode.addNode (nameValue, "nt:folder");  
                
                for (int i = 0; i < this.loadCount; i++)
                {
                    String contentName = "bulkLoad_" + i;
                    ContentData contentData = this.contentData[i];                    
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

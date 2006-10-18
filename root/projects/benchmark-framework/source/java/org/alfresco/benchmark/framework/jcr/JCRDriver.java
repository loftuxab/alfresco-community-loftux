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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.alfresco.benchmark.framework.BaseBenchmarkDriver;
import org.alfresco.benchmark.framework.BenchmarkUtils;
import org.alfresco.benchmark.framework.UnitsOfWork;
import org.alfresco.benchmark.framework.dataprovider.DataProviderComponent;
import org.alfresco.benchmark.framework.dataprovider.PropertyProfile;
import org.alfresco.benchmark.framework.dataprovider.RepositoryProfile;

import com.sun.japex.TestCase;

/**
 * @author Roy Wetherall
 */
public class JCRDriver extends BaseBenchmarkDriver implements UnitsOfWork
{
    protected Repository repository;
    
    protected Map<String, Object> contentPropertyValues;
    protected Map<String, Object> folderPropertyValues;
    
    protected String folderPath;
    protected String contentPath;
    
    @Override
    public void preRun(TestCase testCase)
    {
        super.preRun(testCase);
        
        // Get content property values
        this.contentPropertyValues = DataProviderComponent.getInstance().getPropertyData(
                JCRUtils.getContentPropertyProfiles());
        
        // Get folder property values
        List<PropertyProfile> folderPropertyProfiles = new ArrayList<PropertyProfile>();
        
        PropertyProfile name = PropertyProfile.createSmallTextProperty(JCRUtils.PROP_NAME);
        folderPropertyProfiles.add(name);
        this.folderPropertyValues = DataProviderComponent.getInstance().getPropertyData(
                folderPropertyProfiles);
        
        // Get the random file and folder paths
        int loadDepth = 0;
        if (testCase.hasParam(PARAM_LOAD_DEPTH) == true)
        {
            loadDepth = testCase.getIntParam(PARAM_LOAD_DEPTH);
        }
        
        try
        {            
            RepositoryProfile repositoryProfile = JCRUtils.getRepositoryProfile();
            if (loadDepth <= 0)
            {
            	this.contentPath = JCRUtils.getRootNodeName() + "/" + BenchmarkUtils.getRandomFilePath(repositoryProfile, false);
                this.folderPath = JCRUtils.getRootNodeName() + "/" + BenchmarkUtils.getRandomFolderPath(repositoryProfile, false);
            }
            else
            {
            	this.contentPath = JCRUtils.getRootNodeName() + "/" + BenchmarkUtils.getRandomFilePath(repositoryProfile, loadDepth, false);
                this.folderPath = JCRUtils.getRootNodeName() + "/" + BenchmarkUtils.getRandomFolderPath(repositoryProfile, loadDepth-1, false);
            }
            
        }
        catch (Exception exception)
        {
            throw new RuntimeException("Unable to get the repository profile", exception);
        }        
    }
    
    @Override
    public void postRun(TestCase testCase)
    {
        super.postRun(testCase);
    }
    
    /**
     * Create content benchmark
     */
    public void doCreateContentBenchmark(TestCase tc)
    {
        try
        {
            // Start the session
            Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
            try 
            {            
                // Get the root node and the folder that we are going to create the new node within
                Node rootNode = session.getRootNode();                  
                final Node folder = rootNode.getNode("./" + this.folderPath);
                
                try
                {
                    // Create the new file in the folder
                    JCRUtils.createFile(JCRDriver.this.contentPropertyValues, folder);
                }
                catch (Exception exception)
                {
                    throw new RepositoryException(exception);
                }
           
                // Save the session
                session.save(); 
            }                                   
            finally
            {
                // Close the session
                session.logout();
            }
        }
        catch (Throwable exception)
        {
            throw new RuntimeException("Unable to execute test", exception);
        }
    }

    public void doReadContentBenchmark(TestCase tc)
    {
        try
        {
            // Start the session
            Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
            try 
            {            
                // Get the root node and the content that we are going to read
                Node rootNode = session.getRootNode();      
                final Node content = rootNode.getNode("./" + this.contentPath);
               
                // Get the content and write into a tempory file
                Node resourceNode = content.getNode("jcr:content");                
                InputStream is = resourceNode.getProperty("jcr:data").getStream();
                File tempFile = File.createTempFile(BenchmarkUtils.getGUID(), ".txt");
                try
                {
                    FileOutputStream os = new FileOutputStream(tempFile);
                    BenchmarkUtils.copy(is, os);
                }
                finally
                {
                    tempFile.delete();
                }
            }                                   
            finally
            {
                // Close the session
                session.logout();
            }
        }
        catch (Throwable exception)
        {
            throw new RuntimeException("Unable to execute test", exception);
        }
    }

    public void doCreateFolder(TestCase tc)
    {
        try
        {
            // Start the session
            Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
            try 
            {            
                // Get the root node and the folder that we are going to create the new node within
                Node rootNode = session.getRootNode();                  
                final Node folder = rootNode.getNode("./" + this.folderPath);
                
                //this.folderPath = rootNode.getPath();
                
                try
                {
                    // Create the new file in the folder
                    JCRUtils.createFolder(folder);
                }
                catch (Exception exception)
                {
                    throw new RepositoryException(exception);
                }
           
                // Save the session
                session.save(); 
            }                                   
            finally
            {
                // Close the session
                session.logout();
            }
        }
        catch (Throwable exception)
        {
            throw new RuntimeException("Unable to execute test", exception);
        }
    }

    public void doCreateVersion(TestCase tc)
    {
//        try
//        {
//            // Start the session
//            Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
//            try 
//            {   
//                try
//                {
//                    // Get the root node and the content that we are going to read
//                    Node rootNode = session.getRootNode();                  
//                    final Node content = rootNode.getNode(this.contentPath.substring(1));
//                   
//                    // Add the versionable mix-in if it is required
//                    if (content.isNodeType("mix:versionable") == false)
//                    {
//                        content.addMixin("mix:versionable");                        
//                    }
//                    else
//                    {
//                        content.checkout();
//                    }
//                    
//                    // Check-in and check-out
//                    content.checkin();
//                
//                    // Save the session
//                    session.save();
//                }
//                catch (Throwable exception)
//                {
//                    exception.printStackTrace();
//                }
//            }                                   
//            finally
//            {
//                // Close the session
//                session.logout();
//            }
//        }
//        catch (Throwable exception)
//        {
//            throw new RuntimeException("Unable to execute test", exception);
//        }
    }

    public void doReadProperties(TestCase tc)
    {

        try
        {
            // Start the session
            Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
            try 
            {            
                // Get the root node and the content that we are going to read
                Node rootNode = session.getRootNode();                  
                final Node content = rootNode.getNode("./" + this.contentPath);                
               
                // Get all the properties of the content node
                content.getProperties();
            }                                   
            finally
            {
                // Close the session
                session.logout();
            }
        }
        catch (Throwable exception)
        {
            throw new RuntimeException("Unable to execute test", exception);
        }
    }
}

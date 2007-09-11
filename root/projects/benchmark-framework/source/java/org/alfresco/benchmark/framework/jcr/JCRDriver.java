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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.japex.TestCase;

/**
 * @author Roy Wetherall
 */
public class JCRDriver extends BaseBenchmarkDriver implements UnitsOfWork
{
    private static final Log    logger = LogFactory.getLog(JCRDriver.class);
    
    protected Repository repository;
    
    protected Map<String, Object> contentPropertyValues;
    protected Map<String, Object> folderPropertyValues;
    
    protected String folderUuid;
    protected String contentUuid;
//    protected String folderPath;
//    protected String contentPath;
    
    private String getUuid(String path) throws RepositoryException
    {
        Session session = getSession();
        try
        {
            Node rootNode = session.getRootNode();
            Node node = rootNode.getNode(path);
            return node.getUUID();
        }
        finally
        {
            try {session.logout(); } catch (Throwable e) {logger.error(e); }
        }
    }
    
    private Session getSession() throws RepositoryException
    {
        Repository repository = this.repository;
        Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
        return session;
    }
    
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
            String folderPath = null;
            String contentPath = null;
            RepositoryProfile repositoryProfile = JCRUtils.getRepositoryProfile();
            if (loadDepth <= 0)
            {
            	contentPath = JCRUtils.getRootNodeName() + "/" + BenchmarkUtils.getRandomFilePath(repositoryProfile, false);
                folderPath = JCRUtils.getRootNodeName() + "/" + BenchmarkUtils.getRandomFolderPath(repositoryProfile, false);
            }
            else
            {
            	contentPath = JCRUtils.getRootNodeName() + "/" + BenchmarkUtils.getRandomFilePath(repositoryProfile, loadDepth, false);
                folderPath = JCRUtils.getRootNodeName() + "/" + BenchmarkUtils.getRandomFolderPath(repositoryProfile, loadDepth-1, false);
            }
            this.folderUuid = getUuid(folderPath);
            this.contentUuid = getUuid(contentPath);
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
            Session session = getSession();
            try 
            {            
                // Get the root node and the folder that we are going to create the new node within
                final Node folder = session.getNodeByUUID(folderUuid);
                
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
            Session session = getSession();
            try 
            {            
                // Get the root node and the content that we are going to read
                final Node content = session.getNodeByUUID(contentUuid);
               
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
            Session session = getSession();
            try 
            {            
                // Get the root node and the folder that we are going to create the new node within
                final Node folder = session.getNodeByUUID(folderUuid);
                
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
    }

    public void doReadProperties(TestCase tc)
    {

        try
        {
            // Start the session
            Session session = getSession();
            try 
            {            
                // Get the root node and the content that we are going to read
                final Node content = session.getNodeByUUID(contentUuid);               
               
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

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

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.alfresco.benchmark.framework.BaseBenchmarkDriver;
import org.alfresco.benchmark.framework.BenchmarkUtils;
import org.alfresco.benchmark.framework.dataprovider.ContentData;
import org.alfresco.benchmark.framework.dataprovider.DataProviderComponent;
import org.alfresco.benchmark.framework.dataprovider.PropertyProfile;
import org.alfresco.benchmark.framework.dataprovider.RepositoryProfile;
import org.alfresco.benchmark.framework.dataprovider.PropertyProfile.PropertyType;

import com.sun.japex.TestCase;

/**
 * @author Roy Wetherall
 */
public class JCRBulkLoadDriver extends BaseBenchmarkDriver
{	
    protected Repository repository;
        
    public static final String PARAM_FOLDER_COUNT = "alfresco.folderCount";
    public static final String PARAM_FILE_COUNT = "alfresco.fileCount";
    public static final int DEFAULT_FOLDER_COUNT = 1000;
    public static final int DEFAULT_FILE_COUNT = 1;
    
    private int folderCount = DEFAULT_FOLDER_COUNT;
    private int fileCount = DEFAULT_FILE_COUNT;
    private boolean createFolder = true;
    private String folderUuid;
    
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
        catch (Throwable exception)
        {
            exception.printStackTrace();
            throw new RuntimeException(exception.getMessage(), exception);
        }
    }
    
    @Override
    public void preRun(TestCase testCase)
    {
            super.preRun(testCase);
        
            // Get the folder path
            int loadDepth = 0;
            if (testCase.hasParam(PARAM_LOAD_DEPTH) == true)
            {
                loadDepth = testCase.getIntParam(PARAM_LOAD_DEPTH);
            }
            
            try
            {            
                RepositoryProfile repositoryProfile = JCRUtils.getRepositoryProfile();
                String folderPath = null;
                if (loadDepth <= 0)
                {
                    folderPath = JCRUtils.getRootNodeName() + "/" + BenchmarkUtils.getRandomFolderPath(repositoryProfile, false);
                }
                else
                {
                    folderPath = JCRUtils.getRootNodeName() + "/" + BenchmarkUtils.getRandomFolderPath(repositoryProfile, loadDepth-1, false);
                }
                // get the uuid
                this.folderUuid = getUuid(folderPath);
            }
            catch (Exception exception)
            {
                throw new RuntimeException("Unable to get the repository profile", exception);
            }
        
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
            session.logout();
        }
    }
    
    private Session getSession() throws RepositoryException
    {
        Repository repository = this.repository;
        Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
        return session;
    }
    
    @Override
    public void run(final TestCase testCase) 
    {
        try
        {
            Session session = getSession();
            try
            {     
                Node parentNode = session.getNodeByUUID(folderUuid);
                
                int contentPropIndex = 0;
                for (int folderIndex = 0; folderIndex < this.folderCount; folderIndex++)
                {                    
                    // Create the folder to load the data into
                    String nameValue = "bulkLoad_" + BenchmarkUtils.getGUID();
                    Node bulkLoadFolder = null;
                    if (this.createFolder == true)
                    {
                        Node folderNode = parentNode.addNode (nameValue, "nt:folder");
                        bulkLoadFolder = folderNode;
                    }
                    else
                    {
                        bulkLoadFolder = parentNode;
                    }
                    
                    String uuid = BenchmarkUtils.getGUID();
                    for (int fileIndex = 0; fileIndex < this.fileCount; fileIndex++)
                    {
                        String contentName = "bulkLoad-" + fileIndex + "-" + uuid;
                        ContentData contentData = this.contentData[contentPropIndex];                    
                        Node fileNode = bulkLoadFolder.addNode(contentName, "nt:file");
                    
                        // Add the content
                        Node resNode = fileNode.addNode ("jcr:content", "nt:resource");
                        resNode.setProperty ("jcr:mimeType", contentData.getMimetype());
                        resNode.setProperty ("jcr:encoding", contentData.getEncoding());
                        resNode.setProperty ("jcr:data", new FileInputStream(contentData.getFile()));
                        
                        // Need to set the mandatory 'lastModified' property
                        Calendar lastModified = Calendar.getInstance ();
                        lastModified.setTimeInMillis (contentData.getFile().lastModified ());
                        resNode.setProperty ("jcr:lastModified", lastModified);
                        
                        contentPropIndex++;
                    }
                }
                
                // Save
                session.save();
            }
            finally
            {
            	session.logout();
            }
        }
        catch (Exception exception)
        {
            throw new RuntimeException(exception.getMessage(), exception);
        }
    }
}

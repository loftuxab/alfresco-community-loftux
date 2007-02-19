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

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.alfresco.benchmark.framework.BenchmarkUtils;
import org.alfresco.benchmark.framework.DataLoaderComponent;
import org.alfresco.benchmark.framework.LoadedData;
import org.alfresco.benchmark.framework.dataprovider.RepositoryProfile;

/**
 * @author Roy Wetherall
 */
public abstract class JCRDataLoaderComponentImpl implements DataLoaderComponent
{
    public static final String JCR_BENCHMARK_OBJECT_PREFIX = "jcrbm_";
    
    protected abstract Repository getRepository();
    
    private Session getSession() throws RepositoryException
    {
        Repository repository = getRepository();
        Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
        return session;
    }
    
    /**
     * @see org.alfresco.benchmark.framework.DataLoaderComponent#loadData(org.alfresco.benchmark.framework.dataprovider.RepositoryProfile)
     */
    public LoadedData loadData(RepositoryProfile repositoryProfile)
    {           
        LoadedData loadedData = null;
        Node rootFolder = null;
        try
        {
            List<SerializedNode> folderNodes = new ArrayList<SerializedNode>(10);
            
            Session session = getSession();
            try
            {
                
                rootFolder = session.getRootNode().addNode (getRootName(repositoryProfile), "nt:folder");
                
                if (BenchmarkUtils.getJCRType().equals("Alfresco") == true)
                {
                    if (rootFolder.canAddMixin("ben:repositoryProfile") == true)
                    {
                        rootFolder.addMixin("ben:repositoryProfile");
                        rootFolder.setProperty("ben:repositoryProfile", repositoryProfile.getProfileString());
                    }
                    else
                    {
                        System.out.println("WARNING:  Unable to add mixin 'ben:repositoryProfile' to root test data node.");
                    }
                }
                
                loadedData = new LoadedData(rootFolder.getPath());
                folderNodes.add(new SerializedNode(rootFolder));
                
                session.save();
            }
            finally
            {
                session.logout();
            }
 
            populateFolders(loadedData, repositoryProfile, folderNodes, 1);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            throw new RuntimeException("Unable to load data", e);
        }        
        
        return loadedData;
    }
    
    protected String getRootName(RepositoryProfile repositoryProfile)
    {
        return JCR_BENCHMARK_OBJECT_PREFIX + BenchmarkUtils.getGUID();
    }
    
    /**
     * Populates the folders with the content and sub folders.
     * 
     * @param loadedData        details of the loaded data 
     * @param repositoryProfile the repository profile
     * @param folderNodes    the folder nore references
     * @param depth             the current depth
     */
    private void populateFolders(
            final LoadedData loadedData, 
            final RepositoryProfile repositoryProfile, 
            final List<SerializedNode> folderNodes, 
            int depth)
        throws Exception
    {
        if (depth <= repositoryProfile.getDetails().size())
        {
            final List<SerializedNode> subFolders = new ArrayList<SerializedNode>(10);
            // Increment the depth
            final int newDepth = depth + 1;
            System.out.println("depth=" + depth + "; list_size=" + folderNodes.size());

            RepositoryProfile.RespoitoryProfileDetail profile = repositoryProfile.getDetails().get(depth-1);
            final int numberOfContentNodes = profile.getFileCount();
            final int numberOfSubFolderNodes = profile.getFolderCount();
            
            for (SerializedNode serializedNode : folderNodes)
            {
                Session session = getSession();
                try
                {
                    Node folderNode = serializedNode.getNode(session.getRootNode());
                    
                    // Create content
                    for (int i = 0; i < numberOfContentNodes; i++)
                    {
                        JCRUtils.createFile(folderNode, BenchmarkUtils.getFileName(depth, i));
                    }
                    loadedData.incrementContentCount(numberOfContentNodes);
                    
                    // Create folders
                    for (int i = 0; i < numberOfSubFolderNodes; i++)
                    {
                        Node subFolderNode = JCRUtils.createFolder(folderNode, BenchmarkUtils.getFolderName(depth, i));
                        subFolders.add(new SerializedNode(subFolderNode));
                        loadedData.incrementFolderCount(1);
                    }                                             
                    session.save();
                }
                finally
                {
                    session.logout();
                }
            }
                    
            if (subFolders.size() > 0)
            {
                // Populate the sub folders
                populateFolders(loadedData, repositoryProfile, subFolders, newDepth);
            }
        }
    }

    /**
     * @see org.alfresco.benchmark.framework.DataLoaderComponent#createUsers(int)
     */
    public List<String> createUsers(final int count)
    {
        return null;
    }
    
    private class SerializedNode
    {
        private String path;
        
        public SerializedNode(Node node)
            throws RepositoryException
        {
            this.path = node.getPath();
        }
        
        public Node getNode(Node rootNode)
           throws RepositoryException
        {
            return rootNode.getNode(this.path.substring(1));
        }
    }
}

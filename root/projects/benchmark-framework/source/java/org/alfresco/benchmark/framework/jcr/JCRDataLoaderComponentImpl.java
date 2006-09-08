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
            Session session = getSession();
            try
            {
                rootFolder = session.getRootNode().addNode (JCR_BENCHMARK_OBJECT_PREFIX + BenchmarkUtils.getGUID(), "nt:folder");
                
                if (rootFolder.canAddMixin("ben:repositoryProfile") == true)
                {
                    rootFolder.addMixin("ben:repositoryProfile");
                    rootFolder.setProperty("ben:repositoryProfile", repositoryProfile.getProfileString());
                }
                else
                {
                    System.out.println("WARNING:  Unable to add mixin 'ben:repositoryProfile' to root test data node.");
                }
                loadedData = new LoadedData(rootFolder.getPath());

                session.save();
            }
            finally
            {
                session.logout();
            }
            List<Node> folderNodes = new ArrayList<Node>(10);
            folderNodes.add(rootFolder);
            populateFolders(loadedData, repositoryProfile, folderNodes, 1);
        }
        catch (Exception exception)
        {
            throw new RuntimeException("Unable to load data", exception);
        }        
        
        return loadedData;
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
            final List<Node> folderNodes, 
            int depth)
        throws Exception
    {
        if (depth <= repositoryProfile.getDetails().size())
        {
            final List<Node> subFolders = new ArrayList<Node>(10);
            // Increment the depth
            final int newDepth = depth + 1;
            System.out.println("depth=" + depth + "; list_size=" + folderNodes.size());

            RepositoryProfile.RespoitoryProfileDetail profile = repositoryProfile.getDetails().get(depth-1);
            final int numberOfContentNodes = profile.getFileCount();
            final int numberOfSubFolderNodes = profile.getFolderCount();
            
            for (Node folderNode : folderNodes)
            {
                Session session = getSession();
                try
                {
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
                        subFolders.add(subFolderNode);
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
}

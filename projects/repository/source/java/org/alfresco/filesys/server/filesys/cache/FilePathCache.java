/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.filesys.server.filesys.cache;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.cache.SimpleCache;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.springframework.beans.factory.InitializingBean;

/**
 * Thread-safe cache to store the file info of a file or folder against its search path.
 * <p>
 * The use of the <tt>java.util.concurrent</tt> package ensures that the reads are not
 * blocked in what is generally a high ratio of reads to writes.
 * <p>
 * This cache listens to the repository for changes to the nodes.  Currently
 * the cache is reset when any change to a node or nodes is detected.  In time,
 * and assuming that the performance benefits are evident, the cache might only
 * clear out the outdated file info data.
 * 
 * @author Derek Hulley
 */
public class FilePathCache
        implements InitializingBean,
        NodeServicePolicies.OnCreateNodePolicy,
        NodeServicePolicies.OnUpdateNodePolicy,
        NodeServicePolicies.OnDeleteNodePolicy
{
    /** the component to register the behaviour with */
    private PolicyComponent policyComponent;
    /** the file state cache: for a given node and path, we have a state */
    private SimpleCache<NodeRef, HashMap<String, FileState>> fileStatesCache;
    /** the path result cache: for a given node and search, we have a list of results */
    private SimpleCache<NodeRef, HashMap<String, List<NodeRef>>> pathResultsCache;
    /** lock for read-only operations */
    private Lock cacheReadLock;
    /** lock for write operations */
    private Lock cacheWriteLock;

    public FilePathCache()
    {
        // create lock objects for access to the cache
        ReadWriteLock cacheLock = new ReentrantReadWriteLock();
        cacheReadLock = cacheLock.readLock();
        cacheWriteLock = cacheLock.writeLock();
    }

    /**
     * Used to register the behaviours necessary to invalidate the cache
     * @param policyComponent
     */
    public void setPolicyComponent(PolicyComponent policyComponent)
    {
        this.policyComponent = policyComponent;
    }

    /**
     * Set the cache to be used for storing file state based on a query
     * @param cache
     */
    public void setFileStatesCache(SimpleCache<NodeRef, HashMap<String, FileState>> cache)
    {
        this.fileStatesCache = cache;
    }

    /**
     * Set the cache used to find a set of results from a path query
     * @param cache
     */
    public void setPathResultsCache(SimpleCache<NodeRef, HashMap<String, List<NodeRef>>> cache)
    {
        this.pathResultsCache = cache;
    }

    /**
     * Perform checks to ensure that all required properties were set
     */
    public void afterPropertiesSet() throws Exception
    {
        if (policyComponent == null)
        {
            throw new AlfrescoRuntimeException("policyComponent property not set");
        }
        if (fileStatesCache == null || pathResultsCache == null)
        {
            throw new AlfrescoRuntimeException("fileStatesCache and pathResultsCache properties not set");
        }
        // register behaviour
        policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateNode"),
                this,
                new JavaBehaviour(this, "onCreateNode"));
        policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onUpdateNode"),
                this,
                new JavaBehaviour(this, "onUpdateNode"));
        policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onDeleteNode"),
                this,
                new JavaBehaviour(this, "onDeleteNode"));
    }

    /**
     * Resets the cache.
     * 
     * @see #reset()
     */
    public void onCreateNode(ChildAssociationRef childAssocRef)
    {
        // 
        reset();
    }

    /**
     * Resets the cache.
     * 
     * @see #reset()
     */
    public void onUpdateNode(NodeRef nodeRef)
    {
        reset();
    }

    /**
     * Resets the cache.
     * 
     * @see #reset()
     */
    public void onDeleteNode(ChildAssociationRef childAssocRef)
    {
        reset();
    }

    /**
     * Resets the caches
     *
     */
    public void reset()
    {
        cacheWriteLock.lock();
        try
        {
            fileStatesCache.clear();
            pathResultsCache.clear();
        }
        finally
        {
            cacheWriteLock.unlock();
        }
    }
    
    /**
     * Retrieve the file info associated with with the given path 
     * 
     * @param nodeRef the ancestor node against which the path is valid
     * @param path the path (not normalized) for which to get the info
     * @return Returns the state of the file at the given location, or null
     *      if no entry could be found
     */
    public FileState getFileState(NodeRef nodeRef, String path)
    {
        cacheReadLock.lock();
        try
        {
            Map<String, FileState> fileStatesByPath = fileStatesCache.get(nodeRef);
            if (fileStatesByPath == null)
            {
                // no state set against the ancestor
                return null;
            }
            return fileStatesByPath.get(path);
        }
        finally
        {
            cacheReadLock.unlock();
        }
    }
    
    /**
     * Supplementary method to ensure that the state retrieved is either null (in case
     * the cache has no entry) or the file {@link FileState#exists() exists}.
     * 
     * @param nodeRef the ancestor node against which the path is valid
     * @param path the path to search
     * @return Returns the cached file state of an existing file, or null if the cache contained nothing
     * @throws FileNotFoundException if the cache contained the state of a non-existent file
     */
    public FileState getExistingFileState(NodeRef nodeRef, String path) throws FileNotFoundException
    {
        FileState fileState = getFileState(nodeRef, path);
        if (fileState == null)
        {
            // no entry
            return null;
        }
        else if (!fileState.exists())
        {
            // path represents non-existent file
            throw new FileNotFoundException(path);
        }
        else
        {
            // path was cached with existing file
            return fileState;
        }
    }
    
    /**
     * Caches the state associated with a given path
     * 
     * @param pathRootNodeRef the ancestor node against which the path is valid
     * @param path the path (not normalized) against which to cache the state
     * @param fileState the state of the file at the given location
     */
    public void setFileState(NodeRef pathRootNodeRef, String path, FileState fileState)
    {
        cacheWriteLock.lock();
        try
        {
            HashMap<String, FileState> fileStatesByPath = fileStatesCache.get(pathRootNodeRef);
            if (fileStatesByPath == null)
            {
                fileStatesByPath = new HashMap<String, FileState>(5);
                fileStatesCache.put(pathRootNodeRef, fileStatesByPath);
            }
            fileStatesByPath.put(path, fileState);
        }
        finally
        {
            cacheWriteLock.unlock();
        }
    }
    
    public List<NodeRef> getPathResults(NodeRef pathRootNodeRef, String path)
    {
        cacheReadLock.lock();
        try
        {
            HashMap<String, List<NodeRef>> resultsByPath = pathResultsCache.get(pathRootNodeRef);
            if (resultsByPath == null)
            {
                // no queries have been made against this search root yet
                return null;
            }
            return resultsByPath.get(path);
        }
        finally
        {
            cacheReadLock.unlock();
        }
    }
    
    public void setPathResults(NodeRef pathRootNodeRef, String path, List<NodeRef> results)
    {
        cacheWriteLock.lock();
        try
        {
            HashMap<String, List<NodeRef>> resultsByPath = pathResultsCache.get(pathRootNodeRef);
            if (resultsByPath == null)
            {
                // no queries have been made against this search root yet
                resultsByPath = new HashMap<String, List<NodeRef>>(5);
                pathResultsCache.put(pathRootNodeRef, resultsByPath);
            }
            resultsByPath.put(path, results);
        }
        finally
        {
            cacheWriteLock.unlock();
        }
    }
}

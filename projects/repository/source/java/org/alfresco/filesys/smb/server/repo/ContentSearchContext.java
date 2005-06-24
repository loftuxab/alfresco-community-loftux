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
package org.alfresco.filesys.smb.server.repo;

import java.util.List;

import org.alfresco.filesys.server.filesys.FileAttribute;
import org.alfresco.filesys.server.filesys.FileInfo;
import org.alfresco.filesys.server.filesys.SearchContext;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Wrapper for simple XPath searche against the node service.  The search is performed statically
 * outside the context instance itself - this class merely maintains the state of the search
 * results across client connections.
 * 
 * @author Derek Hulley
 */
public class ContentSearchContext extends SearchContext
{
    private static final Log logger = LogFactory.getLog(ContentSearchContext.class);
    
    private ServiceRegistry serviceRegistry;
    private List<NodeRef> results;
    private int index = -1;
    
    /**
     * Performs a search against the direct children of the given node.
     * <p>
     * Wildcard characters are acceptable, and the search may either be for
     * a specific file or directory, or any file or directory.
     * 
     * @param serviceRegistry used to gain access the the repository
     * @param searchRootNodeRef the node whos children are to be searched
     * @param searchStr the search string relative to the search root node
     * @param attributes the search attributes, e.g. searching for folders, etc
     * @return Returns a search context with the results of the search
     */
    public static ContentSearchContext search(
            ServiceRegistry serviceRegistry,
            NodeRef searchRootNodeRef,
            String searchStr,
            int attributes)
    {
        boolean isFile = (FileAttribute.Directory & attributes) == 0;
        
        // perform the search
        List<NodeRef> results = CifsHelper.getNodeRefs(serviceRegistry, searchRootNodeRef, searchStr);
        
        // build the search context to store the results
        ContentSearchContext searchCtx = new ContentSearchContext(serviceRegistry, results, searchStr);
        
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Search context created: \n" +
                    "   search root: " + searchRootNodeRef + "\n" +
                    "   search context: " + searchCtx);
        }
        return searchCtx;
    }
    
    /**
     * @see ContentSearchContext#search(ServiceRegistry, NodeRef, String)
     */
    private ContentSearchContext(ServiceRegistry serviceRegistry, List<NodeRef> results, String searchStr)
    {
        super();
        super.setSearchString(searchStr);
        this.serviceRegistry = serviceRegistry;
        this.results = results;
    }
    
    public String toString()
    {
        StringBuilder sb = new StringBuilder(60);
        sb.append("ContentSearchContext")
          .append("[ searchStr=").append(getSearchString())
          .append(", resultCount=").append(results.size())
          .append("]");
        return sb.toString();
    }

    @Override
    public synchronized int getResumeId()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized boolean hasMoreFiles()
    {
        return index < (results.size() -1);
    }

    @Override
    public synchronized boolean nextFileInfo(FileInfo info)
    {
        // check if there is anything else to return
        if (!hasMoreFiles())
        {
            return false;
        }
        // increment the index
        index++;
        // get the next file info
        NodeRef nextNodeRef = results.get(index);
        // get the file info
        try
        {
            FileInfo nextInfo = ContentDiskDriver.getFileInformation(serviceRegistry, nextNodeRef, true);
            // copy to info handle
            info.copyFrom(nextInfo);
            // success
            return true;
        }
        catch (InvalidNodeRefException e)
        {
            // node is no longer valid
            return false;
        }
    }

    @Override
    public synchronized String nextFileName()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized boolean restartAt(FileInfo info)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized boolean restartAt(int resumeId)
    {
        throw new UnsupportedOperationException();
    }
}

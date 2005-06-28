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

import org.alfresco.filesys.server.filesys.FileInfo;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 * A bean to carry file information and state
 * 
 * @author Derek Hulley
 */
public class FileState
{
    /** flag indicating existence */
    private boolean exists;
    /** the node being referenced */
    private NodeRef nodeRef;
    /** the file info taken from the node */
    private FileInfo fileInfo;
    
    /**
     * Construct a state for an existing file
     * 
     * @param nodeRef the node being referenced
     * @param fileInfo the info taken from the node
     */
    public FileState(NodeRef nodeRef, FileInfo fileInfo)
    {
        this.exists = true;
        this.nodeRef = nodeRef;
        this.fileInfo = fileInfo;
    }
    
    /**
     * Construct a state for a non-existent file
     * 
     * @see #exists()
     */
    public FileState()
    {
        // this.exists = false;
        // this.nodeRef = null;
        // this.fileInfo = null;
    }

    /**
     * @return Returns a reference to the node in question, provided the node exists
     */
    public NodeRef getNodeRef()
    {
        return nodeRef;
    }

    /**
     * @return Returns the file info retrieved from the node reference, provided the node exists
     */
    public FileInfo getFileInfo()
    {
        return fileInfo;
    }
    
    public boolean exists()
    {
        return exists;
    }
}

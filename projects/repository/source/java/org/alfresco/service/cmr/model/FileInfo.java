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
package org.alfresco.service.cmr.model;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Common file information.
 * 
 * @author Derek Hulley
 */
public interface FileInfo
{
    /**
     * @return Returns a reference to the low-level node representing this file
     */
    public NodeRef getNodeRef();
    
    /**
     * @return Return true if this instance represents a folder, false if this represents a file
     */
    public boolean isFolder();
    
    /**
     * @return Returns the name of the file or folder within the parent folder
     */
    public String getName();
}

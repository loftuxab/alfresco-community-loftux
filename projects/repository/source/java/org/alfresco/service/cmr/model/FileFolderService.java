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

import java.util.List;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Provides methods specific to manipulating {@link org.alfresco.model.ContentModel#TYPE_CONTENT files}
 * and {@link org.alfresco.model.ContentModel#TYPE_FOLDER folders}.
 * 
 * @see org.alfresco.model.ContentModel
 * 
 * @author Derek Hulley
 */
public interface FileFolderService
{
    public List<FileInfo> list(NodeRef folderNodeRef);
    
    public List<FileInfo> listFiles(NodeRef folderNodeRef);
    
    public List<FileInfo> listFolders(NodeRef folderNodeRef);
    
    public List<FileInfo> search(
            NodeRef folderNodeRef,
            String namePattern,
            boolean includeSubFolders);
    
    public List<FileInfo> search(
            NodeRef folderNodeRef,
            String namePattern,
            boolean fileSearch,
            boolean folderSearch,
            boolean includeSubFolders);
}

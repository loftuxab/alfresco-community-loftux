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
    /**
     * Lists immediate child files and folders of the given context folder
     * 
     * @param folderNodeRef the folder to start searching in
     * @return Returns a list of matching files and folders
     */
    public List<FileInfo> list(NodeRef folderNodeRef);
    
    /**
     * Lists all immediate child files of the given context folder
     * 
     * @param folderNodeRef the folder to start searching in
     * @return Returns a list of matching files
     */
    public List<FileInfo> listFiles(NodeRef folderNodeRef);
    
    /**
     * Lists all immediate child folders of the given context folder
     * 
     * @param folderNodeRef the folder to start searching in
     * @return Returns a list of matching folders
     */
    public List<FileInfo> listFolders(NodeRef folderNodeRef);

    /**
     * Searches for all files and folders with the matching name pattern.
     * 
     * @see #search(NodeRef, String, boolean, boolean, boolean)
     */
    public List<FileInfo> search(
            NodeRef folderNodeRef,
            String namePattern,
            boolean includeSubFolders);
    
    /**
     * Perform a search against the name of the files or folders within a hierarchy.
     * 
     * @param folderNodeRef the context of the search.  This node will never be returned
     *      as part of the search results.
     * @param namePattern the name of the file or folder to search for, or a
     *      {@link org.alfresco.util.SearchLanguageConversion#DEF_LUCENE wildcard} pattern
     *      to search for.
     * @param fileSearch true if file types are to be included in the search results
     * @param folderSearch true if folder types are to be included in the search results
     * @param includeSubFolders true to search the entire hierarchy below the search context
     * @return Returns a list of file or folder matches
     */
    public List<FileInfo> search(
            NodeRef folderNodeRef,
            String namePattern,
            boolean fileSearch,
            boolean folderSearch,
            boolean includeSubFolders);
}

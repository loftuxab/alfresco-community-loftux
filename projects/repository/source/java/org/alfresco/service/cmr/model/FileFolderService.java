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
import org.alfresco.service.namespace.QName;

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
     * @throws FileNotFoundException if the search context could not be found
     */
    public List<FileInfo> list(NodeRef folderNodeRef) throws FileNotFoundException;
    
    /**
     * Lists all immediate child files of the given context folder
     * 
     * @param folderNodeRef the folder to start searching in
     * @return Returns a list of matching files
     * @throws FileNotFoundException if the search context could not be found
     */
    public List<FileInfo> listFiles(NodeRef folderNodeRef) throws FileNotFoundException;
    
    /**
     * Lists all immediate child folders of the given context folder
     * 
     * @param folderNodeRef the folder to start searching in
     * @return Returns a list of matching folders
     * @throws FileNotFoundException if the search context could not be found
     */
    public List<FileInfo> listFolders(NodeRef folderNodeRef) throws FileNotFoundException;

    /**
     * Searches for all files and folders with the matching name pattern,
     * using wildcard characters <b>*</b> and <b>?</b>.
     * 
     * @see #search(NodeRef, String, boolean, boolean, boolean)
     */
    public List<FileInfo> search(
            NodeRef folderNodeRef,
            String namePattern,
            boolean includeSubFolders) throws FileNotFoundException;
    
    /**
     * Perform a search against the name of the files or folders within a hierarchy.
     * Wildcard characters are <b>*</b> and <b>?</b>.
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
     * @throws FileNotFoundException if the search context could not be found
     */
    public List<FileInfo> search(
            NodeRef folderNodeRef,
            String namePattern,
            boolean fileSearch,
            boolean folderSearch,
            boolean includeSubFolders) throws FileNotFoundException;
    
    /**
     * Rename a file or folder in its current location
     * 
     * @param fileFolderRef the file or folder to rename
     * @param newName the new name
     * @return Return the new file info
     * @throws FileExistsException if a file or folder with the new name already exists
     * @throws FileNotFoundException the file or folder reference doesn't exist
     */
    public FileInfo rename(NodeRef fileFolderRef, String newName) throws FileExistsException, FileNotFoundException;
    
    /**
     * Move a file or folder to a new name and/or location.
     * <p>
     * If both the parent folder and name remain the same, then nothing is done.
     * 
     * @param sourceNodeRef the file or folder to move
     * @param targetFolderRef the new folder to move the node to - null means rename in situ
     * @param newName the name to change the file or folder to - null to keep the existing name
     * @return Returns the new file info
     * @throws FileExistsException
     * @throws FileNotFoundException
     */
    public FileInfo move(NodeRef sourceNodeRef, NodeRef targetFolderRef, String newName)
            throws FileExistsException, FileNotFoundException;

    /**
     * Copy a source file or folder.  The source can be optionally renamed and optionally
     * moved into another folder.
     * <p>
     * If both the parent folder and name remain the same, then nothing is done.
     * 
     * @param sourceNodeRef the file or folder to copy
     * @param targetFolderRef the target folder to copy to, or null to use the current parent folder
     * @param newName the new name, or null to keep the existing name.
     * @return Return the new file info
     * @throws FileExistsException
     * @throws FileNotFoundException
     */
    public FileInfo copy(NodeRef sourceNodeRef, NodeRef targetFolderRef, String newName)
            throws FileExistsException, FileNotFoundException;

    /**
     * Create a file or folder; or any valid node of type derived from file or folder
     * 
     * @param parentFolderRef the parent folder
     * @param name the name of the node
     * @param typeQName the type to create
     * @return Returns the new node's file information
     * @throws FileExistsException
     * @throws FileNotFoundException
     */
    public FileInfo create(NodeRef parentFolderRef, String name, QName typeQName)
            throws FileExistsException, FileNotFoundException;
    
    /**
     * Get the file or folder names from the root down to and including the node provided.
     * <ul>
     *   <li>The root node can be of any type and is not included in the path list.</li>
     *   <li>Only the primary path is considered.  If the target node is not a descendent of the
     *       root along purely primary associations, then an exception is generated.</li>
     *   <li>If an invalid type is encoutered along the path, then an exception is generated.</li>
     * </ul>
     * 
     * @param rootNodeRef the start of the returned path, or null if the <b>store</b> root
     *      node must be assumed.
     * @param nodeRef a reference to the file or folder
     * @return Returns a list of file/folder infos from the root (excluded) down to and
     *      including the destination file or folder
     * @throws FileNotFoundException if the node could not be found
     */
    public List<FileInfo> getNamePath(NodeRef rootNodeRef, NodeRef nodeRef) throws FileNotFoundException;
    
    /**
     * Resolve a file or folder name path from a given root node down to the final node.
     * 
     * @param rootNodeRef the start of the path given, i.e. the '/' in '/A/B/C' for example
     * @param pathElements a list of names in the path
     * @param isFolder true if we are searching for folder or false to search for a file
     * @return Returns the info of the file or folder
     * @throws FileNotFoundException if no file or folder exists along the path
     */
    public FileInfo resolveNamePath(NodeRef rootNodeRef, List<String> pathElements, boolean isFolder) throws FileNotFoundException;
}

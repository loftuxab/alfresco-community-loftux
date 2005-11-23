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
package org.alfresco.repo.model.filefolder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.search.QueryParameterDefImpl;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.model.FileExistsException;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.CopyService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.QueryParameterDefinition;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.SearchLanguageConversion;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementation of the file/folder-specific service.
 * 
 * @author Derek Hulley
 */
public class FileFolderServiceImpl implements FileFolderService
{
    /** Shallow search for all files */
    private static final String XPATH_QUERY_SHALLOW_FILES =
        "./*" +
        "[(subtypeOf('" + ContentModel.TYPE_CONTENT + "'))]";
    
    /** Shallow search for all folder */
    private static final String XPATH_QUERY_SHALLOW_FOLDERS =
        "./*" +
        "[not (subtypeOf('" + ContentModel.TYPE_SYSTEM_FOLDER + "'))" +
        " and (subtypeOf('" + ContentModel.TYPE_FOLDER + "'))]";
    
    /** Shallow search for all files and folders */
    private static final String XPATH_QUERY_SHALLOW_ALL =
        "./*" +
        "[like(@cm:name, $cm:name, false)" +
        " and not (subtypeOf('" + ContentModel.TYPE_SYSTEM_FOLDER + "'))" +
        " and (subtypeOf('" + ContentModel.TYPE_FOLDER + "') or subtypeOf('" + ContentModel.TYPE_CONTENT + "'))]";
    
    /** Deep search for files and folders with a name pattern */
    private static final String XPATH_QUERY_DEEP_ALL =
        ".//*" +
        "[like(@cm:name, $cm:name, false)" +
        " and not (subtypeOf('" + ContentModel.TYPE_SYSTEM_FOLDER + "'))" +
        " and (subtypeOf('" + ContentModel.TYPE_FOLDER + "') or subtypeOf('" + ContentModel.TYPE_CONTENT + "'))]";
    
    /** empty parameters */
    private static final QueryParameterDefinition[] PARAMS_EMPTY = new QueryParameterDefinition[0];
    private static final QueryParameterDefinition[] PARAMS_ANY_NAME = new QueryParameterDefinition[1];
    
    private static Log logger = LogFactory.getLog(FileFolderServiceImpl.class);

    private NamespaceService namespaceService;
    private DictionaryService dictionaryService;
    private NodeService nodeService;
    private CopyService copyService;
    private SearchService searchService;
    
    private QName cmName;

    /**
     * Default constructor
     */
    public FileFolderServiceImpl()
    {
    }

    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }

    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }
    
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    public void setCopyService(CopyService copyService)
    {
        this.copyService = copyService;
    }

    public void setSearchService(SearchService searchService)
    {
        this.searchService = searchService;
    }
    
    public void init()
    {
        PARAMS_ANY_NAME[0] = new QueryParameterDefImpl(
                ContentModel.PROP_NAME,
                dictionaryService.getDataType(DataTypeDefinition.TEXT),
                true,
                "%");
    }

    /**
     * Helper method to convert node reference instances to file info
     * 
     * @param nodeRefs the node references
     * @return Return a list of file info
     */
    private List<FileInfo> toFileInfo(List<NodeRef> nodeRefs) throws FileNotFoundException
    {
        List<FileInfo> results = new ArrayList<FileInfo>(nodeRefs.size());
        for (NodeRef nodeRef : nodeRefs)
        {
            FileInfo fileInfo = toFileInfo(nodeRef);
            results.add(fileInfo);
        }
        return results;
    }
    
    /**
     * Helper method to convert a node reference instance to a file info
     */
    private FileInfo toFileInfo(NodeRef nodeRef) throws FileNotFoundException
    {
        // check the node type
        QName nodeTypeQName = nodeService.getType(nodeRef);
        // get the file attributes
        Map<QName, Serializable> properties = nodeService.getProperties(nodeRef);
        String name = (String) properties.get(ContentModel.PROP_NAME);
        // is it a folder
        QName typeQName = nodeService.getType(nodeRef);
        boolean isFolder = isFolder(typeQName);
        
        // construct the file info and add to the results
        FileInfo fileInfo = new FileInfoImpl(nodeRef, isFolder, name);
        // done
        return fileInfo;
    }

    /**
     * Ensure that a file or folder does not already exist
     * 
     * @throws FileExistsException if the folder or folder doesn't exist
     */
    private void checkExists(NodeRef parentFolderRef, String name, boolean isFolder)
            throws FileExistsException, FileNotFoundException
    {
        // check for existing file or folder
        List<FileInfo> existingFileInfos = this.search(parentFolderRef, name, !isFolder, isFolder, false);
        if (existingFileInfos.size() > 0)
        {
            throw new FileExistsException(existingFileInfos.get(0));
        }
    }

    /**
     * Checks the type for whether it is a file or folder.  All invalid types
     * lead to runtime exceptions.
     * 
     * @param typeQName the type to check
     * @return Returns true if the type is a valid folder type, false if it is a file.
     */
    private boolean isFolder(QName typeQName)
    {
        if (dictionaryService.isSubClass(typeQName, ContentModel.TYPE_FOLDER))
        {
            if (dictionaryService.isSubClass(typeQName, ContentModel.TYPE_SYSTEM_FOLDER))
            {
                throw new AlfrescoRuntimeException("This service should ignore type " + ContentModel.TYPE_SYSTEM_FOLDER);
            }
            return true;
        }
        else if (dictionaryService.isSubClass(typeQName, ContentModel.TYPE_CONTENT))
        {
            // it is a regular file
            return false;
        }
        else
        {
            // unhandled type
            throw new AlfrescoRuntimeException("Type is not handled by this service: " + typeQName);
        }
    }

    /**
     * TODO: Use Lucene search to get file attributes without having to visit the node service
     */
    public List<FileInfo> list(NodeRef folderNodeRef) throws FileNotFoundException
    {
        // execute the query
        List<NodeRef> nodeRefs = searchService.selectNodes(
                folderNodeRef,
                XPATH_QUERY_SHALLOW_ALL,
                PARAMS_ANY_NAME,
                namespaceService,
                false);
        // convert the noderefs
        List<FileInfo> results = toFileInfo(nodeRefs);
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Shallow search for files and folders: \n" +
                    "   context: " + folderNodeRef + "\n" +
                    "   results: " + results);
        }
        return results;
    }

    /**
     * TODO: Use Lucene search to get file attributes without having to visit the node service
     */
    public List<FileInfo> listFiles(NodeRef folderNodeRef) throws FileNotFoundException
    {
        // execute the query
        List<NodeRef> nodeRefs = searchService.selectNodes(
                folderNodeRef,
                XPATH_QUERY_SHALLOW_FILES,
                PARAMS_EMPTY,
                namespaceService,
                false);
        // convert the noderefs
        List<FileInfo> results = toFileInfo(nodeRefs);
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Shallow search for files: \n" +
                    "   context: " + folderNodeRef + "\n" +
                    "   results: " + results);
        }
        return results;
    }

    /**
     * TODO: Use Lucene search to get file attributes without having to visit the node service
     */
    public List<FileInfo> listFolders(NodeRef folderNodeRef) throws FileNotFoundException
    {
        // execute the query
        List<NodeRef> nodeRefs = searchService.selectNodes(
                folderNodeRef,
                XPATH_QUERY_SHALLOW_FOLDERS,
                PARAMS_EMPTY,
                namespaceService,
                false);
        // convert the noderefs
        List<FileInfo> results = toFileInfo(nodeRefs);
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Shallow search for folders: \n" +
                    "   context: " + folderNodeRef + "\n" +
                    "   results: " + results);
        }
        return results;
    }

    /**
     * @see #search(NodeRef, String, boolean, boolean, boolean)
     */
    public List<FileInfo> search(NodeRef folderNodeRef, String namePattern, boolean includeSubFolders)
            throws FileNotFoundException
    {
        return search(folderNodeRef, namePattern, true, true, includeSubFolders);
    }

    /**
     * Full search with all options
     */
    public List<FileInfo> search(
            NodeRef folderNodeRef,
            String namePattern,
            boolean fileSearch,
            boolean folderSearch,
            boolean includeSubFolders)
            throws FileNotFoundException
    {
        // shortcut if the search is requesting nothing
        if (!fileSearch && !folderSearch)
        {
            return Collections.emptyList();
        }
        
        // if the name pattern is null, then we use the ANY pattern
        QueryParameterDefinition[] params = null;
        if (namePattern != null)
        {
            // the interface specifies the Lucene syntax, so perform a conversion
            namePattern = SearchLanguageConversion.convert(
                    SearchLanguageConversion.DEF_LUCENE,
                    SearchLanguageConversion.DEF_XPATH_LIKE,
                    namePattern);
            
            params = new QueryParameterDefinition[1];
            params[0] = new QueryParameterDefImpl(
                    ContentModel.PROP_NAME,
                    dictionaryService.getDataType(DataTypeDefinition.TEXT),
                    true,
                    namePattern);
        }
        else
        {
            params = PARAMS_ANY_NAME;
        }
        // determine the correct query to use
        String query = null;
        if (includeSubFolders)
        {
            query = XPATH_QUERY_DEEP_ALL;
        }
        else
        {
            query = XPATH_QUERY_SHALLOW_ALL;
        }
        // execute the query
        List<NodeRef> nodeRefs = searchService.selectNodes(
                folderNodeRef,
                query,
                params,
                namespaceService,
                false);
        List<FileInfo> results = toFileInfo(nodeRefs);
        // eliminate unwanted files/folders
        Iterator<FileInfo> iterator = results.iterator(); 
        while (iterator.hasNext())
        {
            FileInfo file = iterator.next();
            if (file.isFolder() && !folderSearch)
            {
                iterator.remove();
            }
            else if (!file.isFolder() && !fileSearch)
            {
                iterator.remove();
            }
        }
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Deep search: \n" +
                    "   context: " + folderNodeRef + "\n" +
                    "   pattern: " + namePattern + "\n" +
                    "   files: " + fileSearch + "\n" +
                    "   folders: " + folderSearch + "\n" +
                    "   deep: " + includeSubFolders + "\n" +
                    "   results: " + results);
        }
        return results;
    }

    public List<String> getNamePath(NodeRef nodeRef) throws FileNotFoundException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @see #move(NodeRef, NodeRef, String)
     */
    public FileInfo rename(NodeRef sourceNodeRef, String newName) throws FileExistsException, FileNotFoundException
    {
        return move(sourceNodeRef, null, newName);
    }

    /**
     * @see #moveOrCopy(NodeRef, NodeRef, String, boolean)
     */
    public FileInfo move(NodeRef sourceNodeRef, NodeRef targetFolderRef, String newName) throws FileExistsException, FileNotFoundException
    {
        return moveOrCopy(sourceNodeRef, targetFolderRef, newName, true);
    }
    
    /**
     * @see #moveOrCopy(NodeRef, NodeRef, String, boolean)
     */
    public FileInfo copy(NodeRef sourceNodeRef, NodeRef targetFolderRef, String newName) throws FileExistsException, FileNotFoundException
    {
        return moveOrCopy(sourceNodeRef, targetFolderRef, newName, false);
    }

    /**
     * Implements both move and copy behaviour
     * 
     * @param move true to move, otherwise false to copy
     */
    private FileInfo moveOrCopy(NodeRef sourceNodeRef, NodeRef targetFolderRef, String newName, boolean move) throws FileExistsException, FileNotFoundException
    {
        // get file/folder in its current state
        FileInfo beforeFileInfo = toFileInfo(sourceNodeRef);
        boolean isFolder = beforeFileInfo.isFolder();
        // check the name - null means keep the existing name
        if (newName == null)
        {
            newName = beforeFileInfo.getName();
        }
        
        // we need the current association type
        ChildAssociationRef assocRef = nodeService.getPrimaryParent(sourceNodeRef);
        if (targetFolderRef == null)
        {
            targetFolderRef = assocRef.getParentRef();
        }
        
        // check that the parent folder is good
        FileInfo parentFileInfo = toFileInfo(targetFolderRef);
        
        // there is nothing to do if both the name and parent folder haven't changed
        if (targetFolderRef.equals(assocRef.getParentRef()) && newName.equals(beforeFileInfo.getName()))
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Doing nothing - neither filename or parent has not changed: \n" +
                        "   parent: " + parentFileInfo + "\n" +
                        "   before: " + beforeFileInfo + "\n" +
                        "   new name: " + newName);
            }
            return beforeFileInfo;
        }
        
        // check for existing file or folder
        checkExists(targetFolderRef, newName, isFolder);
        
        QName qname = QName.createQName(
                NamespaceService.CONTENT_MODEL_1_0_URI,
                QName.createValidLocalName(newName));
        
        // move or copy
        NodeRef targetNodeRef = null;
        if (move)
        {
            // move the node so that the association moves as well
            ChildAssociationRef newAssocRef = nodeService.moveNode(
                    sourceNodeRef,
                    targetFolderRef,
                    assocRef.getTypeQName(),
                    qname);
            targetNodeRef = newAssocRef.getChildRef();
        }
        else
        {
            // copy the node
            targetNodeRef = copyService.copy(
                    sourceNodeRef,
                    targetFolderRef,
                    assocRef.getTypeQName(),
                    qname,
                    true);
        }
        // changed the name property
        nodeService.setProperty(sourceNodeRef, ContentModel.PROP_NAME, newName);
        
        // get the details after the operation
        FileInfo afterFileInfo = toFileInfo(targetNodeRef);
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("" + (move ? "Moved" : "Copied") + " node: \n" +
                    "   parent: " + parentFileInfo + "\n" +
                    "   before: " + beforeFileInfo + "\n" +
                    "   after: " + afterFileInfo);
        }
        return afterFileInfo;
    }
    
    public FileInfo create(NodeRef parentFolderRef, String name, QName typeQName)
            throws FileExistsException, FileNotFoundException
    {
        // file or folder
        boolean isFolder = isFolder(typeQName);
        
        // check for existing file or folder
        checkExists(parentFolderRef, name, isFolder);
        
        // create the node
        QName qname = QName.createQName(
                NamespaceService.CONTENT_MODEL_1_0_URI,
                QName.createValidLocalName(name));
        Map<QName, Serializable> properties = Collections.singletonMap(
                ContentModel.PROP_NAME,
                (Serializable) name);
        ChildAssociationRef assocRef = nodeService.createNode(
                parentFolderRef,
                ContentModel.ASSOC_CONTAINS,
                qname,
                typeQName,
                properties);
        NodeRef nodeRef = assocRef.getChildRef();
        FileInfo fileInfo = toFileInfo(nodeRef);
        // done
        if (logger.isDebugEnabled())
        {
            FileInfo parentFileInfo = toFileInfo(parentFolderRef);
            logger.debug("Created: \n" +
                    "   parent: " + parentFileInfo + "\n" +
                    "   created: " + fileInfo);
        }
        return fileInfo;
    }
}

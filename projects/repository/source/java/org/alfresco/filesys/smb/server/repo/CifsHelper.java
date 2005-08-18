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

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.filesys.server.filesys.FileAttribute;
import org.alfresco.filesys.server.filesys.FileExistsException;
import org.alfresco.filesys.server.filesys.FileInfo;
import org.alfresco.filesys.server.filesys.FileName;
import org.alfresco.filesys.server.filesys.cache.FilePathCache;
import org.alfresco.filesys.server.filesys.cache.FileState;
import org.alfresco.filesys.util.WildCard;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.search.QueryParameterDefImpl;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyTypeDefinition;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.datatype.ValueConverter;
import org.alfresco.service.cmr.search.QueryParameterDefinition;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.SearchLanguageConversion;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class with supplying helper methods and potentially acting as a cache for
 * queries.
 *  
 * @author derekh
 */
public class CifsHelper
{
    private static Log logger = LogFactory.getLog(CifsHelper.class);
    
    private FilePathCache filePathCache;

    private NamespaceService namespaceService;
    private DictionaryService dictionaryService;
    private NodeService nodeService;
    private SearchService searchService;
    private MimetypeService mimetypeService;
    
    /**
     * Default
     */
    public CifsHelper()
    {
    }
    
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }
    
    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }
    
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    public void setSearchService(SearchService searchService)
    {
        this.searchService = searchService;
    }

    public void setFilePathCache(FilePathCache filePathCache)
    {
        this.filePathCache = filePathCache;
    }

    public void setMimetypeService(MimetypeService mimetypeService)
    {
        this.mimetypeService = mimetypeService;
    }

    /**
     * @param serviceRegistry for repo connection
     * @param nodeRef
     * @return Returns true if the node is a subtype of {@link ContentModel#TYPE_FOLDER folder}
     * @throws AlfrescoRuntimeException if the type is neither related to a folder or content
     */
    public boolean isDirectory(NodeRef nodeRef)
    {
        QName nodeTypeQName = nodeService.getType(nodeRef);
        if (dictionaryService.isSubClass(nodeTypeQName, ContentModel.TYPE_FOLDER))
        {
            return true;
        }
        else if (dictionaryService.isSubClass(nodeTypeQName, ContentModel.TYPE_CONTENT))
        {
            return false;
        }
        else
        {
            // it is not a directory, but what is it?
            return false;   
        }
    }

    /**
     * Extract a single node's file info, where the node is reference by
     * a path relative to an ancestor node.
     * 
     * @param pathRootNodeRef
     * @param path
     * @param includeName
     * @return Returns the existing node reference
     * @throws FileNotFoundException
     */
    public FileInfo getFileInformation(
            NodeRef pathRootNodeRef,
            String path,
            boolean includeName)
            throws FileNotFoundException
    {
        // check the cache for its state
        FileState fileState = filePathCache.getExistingFileState(pathRootNodeRef, path);
        if (fileState != null)
        {
            // it was cached, and we know it exists
            return fileState.getFileInfo();
        }
        
        // get the node being referenced
        NodeRef nodeRef = getNodeRef(pathRootNodeRef, path);

        // nothing cached
        FileInfo fileInfo = getFileInformation(nodeRef, includeName);
                
        // put the results back into the cache
        fileState = new FileState(nodeRef, fileInfo);
        filePathCache.setFileState(pathRootNodeRef, path, fileState);
        
        return fileInfo;
    }

    /**
     * Helper method to extract file info from a specific node.
     * <p>
     * This method goes direct to the repo for all information and no data is
     * cached here.
     * 
     * @param nodeRef the node that the path is relative to
     * @param path the path to get info for
     * @param includeName if the name property is to be carried into the filesystem
     * @return Returns the file information pertinent to the node
     * @throws FileNotFoundException if the path refers to a non-existent file
     */
    public FileInfo getFileInformation(
            NodeRef nodeRef,
            boolean includeName)
            throws FileNotFoundException
    {
        // retrieve required properties and create file info
        Map<QName, Serializable> nodeProperties = nodeService.getProperties(nodeRef);
        FileInfo fileInfo = new FileInfo();
        
        // unset all attribute flags
        int fileAttributes = 0;
        fileInfo.setFileAttributes(fileAttributes);
        
        if (isDirectory(nodeRef))
        {
            // add directory attribute
            fileAttributes |= FileAttribute.Directory;
            fileInfo.setFileAttributes(fileAttributes);
        }
        else
        {
            // get the file size
            Object propSize = nodeProperties.get(ContentModel.PROP_SIZE);
            long size = 0L;
            if (propSize != null)       // it can be null if no content has been uploaded yet
            {
                size = ValueConverter.longValue(propSize);
            }
            fileInfo.setSize(size);
            
            // Set the allocation size by rounding up the size to a 512 byte block boundary

            if ( size > 0)
                fileInfo.setAllocationSize((size + 512L) & 0xFFFFFFFFFFFFFE00L);
        }
        
        // created
        Object propCreated = nodeProperties.get(ContentModel.PROP_CREATED);
        if (propCreated != null)
        {
            long created = ValueConverter.longValue(propCreated);
            fileInfo.setCreationDateTime(created);
        }
        // modified
        Object propModified = nodeProperties.get(ContentModel.PROP_MODIFIED);
        if (propModified != null)
        {
            long modified = ValueConverter.longValue(propModified);
            fileInfo.setModifyDateTime(modified);
        }
        // name (only relevant if the path had something on it)
        if (includeName)
        {
            Object propName = nodeProperties.get(ContentModel.PROP_NAME);
            if (propName != null)
            {
                String name = ValueConverter.convert(String.class, propName);
                fileInfo.setFileName(name);
            }
        }
        else
        {
            fileInfo.setFileName("");
        }
        
        // read/write access
        // TODO: "Set read/write access flags"); //TODO
        
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Fetched file info: \n" +
                    "   info: " + fileInfo);
        }
        return fileInfo;
    }
    
    /**
     * Creates a file or directory using the given paths.
     * <p>
     * If the directory path doesn't exist, then all the parent directories will be created.
     * If the file path is <code>null</code>, then the file will not be created
     * 
     * @param rootNodeRef the root node of the path
     * @param path the path to a node
     * @param isFile true if the node to be created must be a file
     * @return Returns a newly created file or folder node
     * @throws FileExistsException if the file or folder already exists
     */
    public NodeRef createNode(NodeRef rootNodeRef, String path, boolean isFile) throws FileExistsException
    {
        NodeRef currentNodeRef = rootNodeRef;
            
        // split the directory path up into its constituents
        StringTokenizer tokenizer = new StringTokenizer(path, FileName.DOS_SEPERATOR_STR, false);
        
        // walk the directories, creating them on the fly if required
        while (tokenizer.hasMoreTokens())
        {
            String pathElement = tokenizer.nextToken();
            
            // determine whether we are searching for a file or directory
            boolean lastToken = !tokenizer.hasMoreTokens();
            boolean fileToken = (isFile && lastToken);
            QName typeQName = fileToken ? ContentModel.TYPE_CONTENT : ContentModel.TYPE_FOLDER;
            
            String encodedPath = QName.createValidLocalName(pathElement);
            // check if the node exists
            try
            {
                // will throw FileNotFound if no node matches
                NodeRef existingNodeRef = getNodeRef(currentNodeRef, pathElement);
                // the existence of the node is only an issue if we are on the last token, i.e. there
                // will be a name clash
                if (lastToken)
                {
                    throw new FileExistsException("Directory or file exists: \n" +
                            "   device root: " + rootNodeRef + "\n" +
                            "   path: " + path + "\n" +
                            "   existing dir: " + existingNodeRef);
                }
                else
                {
                    // directory exists, but we are either creating a file or have more folders to go
                    // move onto the existing folder node
                    currentNodeRef = existingNodeRef;
                }
            }
            catch (FileNotFoundException e)
            {
                // we can go ahead and create the node as it doesn't exist
                // set properties
                Map<QName, Serializable> properties = new HashMap<QName, Serializable>(5);
                properties.put(ContentModel.PROP_NAME, pathElement);   // the path element acts as the full name
                if (fileToken)
                {
                    String mimetype = mimetypeService.guessMimetype(pathElement);
                    properties.put(ContentModel.PROP_MIME_TYPE, mimetype);
                }
                // create node
                ChildAssociationRef assocRef = nodeService.createNode(
                        currentNodeRef,
                        ContentModel.ASSOC_CONTAINS,
                        QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, encodedPath),
                        typeQName,
                        properties);
                currentNodeRef = assocRef.getChildRef();
                
                Map<QName, Serializable> uiproperties = new HashMap<QName, Serializable>(2);
                uiproperties.put(ContentModel.PROP_TITLE, pathElement);
                uiproperties.put(ContentModel.PROP_DESCRIPTION, pathElement);
                nodeService.addAspect(currentNodeRef, ContentModel.ASPECT_UIFACETS, uiproperties);
            }
        }
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Created node: \n" +
                    "   device root: " + rootNodeRef + "\n" +
                    "   path: " + path + "\n" +
                    "   is file: " + isFile + "\n" +
                    "   new node: " + currentNodeRef);
        }
        return currentNodeRef;
    }

    private void addDescendents(List<NodeRef> pathRootNodeRefs, Stack<String> pathElements, List<NodeRef> results)
    {
        if (pathElements.isEmpty())
        {
            // if this method is called with an empty path element stack, then the
            // current context nodes are the results to be added
            results.addAll(pathRootNodeRefs);
            return;
        }
        
        // take the first path element off the stack
        String pathElement = pathElements.pop();

        // iterate over each path root node
        for (NodeRef pathRootNodeRef : pathRootNodeRefs)
        {
            // deal with cyclic relationships by not traversing down any node already in the results
            if (results.contains(pathRootNodeRef))
            {
                continue;
            }
            // get direct descendents along the path
            List<NodeRef> directDescendents = getDirectDescendents(pathRootNodeRef, pathElement);
            // recurse onto the descendents
            addDescendents(directDescendents, pathElements, results);
        }
        
        // restore the path element stack
        pathElements.push(pathElement);
    }
    
    /**
     * Performs an XPath query to get the first-level descendents matching the given path
     * 
     * @param pathRootNodeRef
     * @param pathElement
     * @return
     */
    private List<NodeRef> getDirectDescendents(NodeRef pathRootNodeRef, String pathElement)
    {
        // first check the cache to see if there are any results for this path query
        List<NodeRef> cachedResults = filePathCache.getPathResults(pathRootNodeRef, pathElement);
        if (cachedResults != null)
        {
            // the cache had results for good or bad
            return cachedResults;
        }
        
        // perform the search
        StringBuilder sb = new StringBuilder(250);
        
        boolean wildcardSearch = WildCard.containsWildcards(pathElement);
        
        String nameParam = "cm:name";
        String folderTypeParam = "cm:foldertype";
        String fileTypeParam = "cm:filetype";
        // append to the xpath
        if (wildcardSearch)
        {
            // escape the path element
            pathElement = SearchLanguageConversion.escapeForXPathLike(pathElement);
            // fix up wildcard matches for like function
            pathElement = pathElement.replace('*', '%');
            // use the like function (do not match FTS)
            sb.append("./*[like(@cm:name, $").append(nameParam).append(", false)");
        }
        else
        {
            sb.append("./*[@cm:name = $").append(nameParam);
        }
        sb.append(" and ")
          .append("(")
          .append("subtypeOf($").append(folderTypeParam).append(")")
          .append(" or ")
          .append("subtypeOf($").append(fileTypeParam).append(")")
          .append(")]");
        // create the query parameters
        QueryParameterDefinition[] params = new QueryParameterDefinition[3];
        params[0] = new QueryParameterDefImpl(
                QName.createQName(nameParam, namespaceService),
                dictionaryService.getPropertyType(PropertyTypeDefinition.TEXT),
                true,
                pathElement);
        params[1] = new QueryParameterDefImpl(
                QName.createQName(folderTypeParam, namespaceService),
                dictionaryService.getPropertyType(PropertyTypeDefinition.TEXT),
                true,
                ContentModel.TYPE_FOLDER.toString());
        params[2] = new QueryParameterDefImpl(
                QName.createQName(fileTypeParam, namespaceService),
                dictionaryService.getPropertyType(PropertyTypeDefinition.TEXT),
                true,
                ContentModel.TYPE_CONTENT.toString());
    
        String xpath = sb.toString();
        // execute the query
        List<NodeRef> nodes = searchService.selectNodes(
                pathRootNodeRef,
                xpath,
                params,
                namespaceService,
                false);
        // done
        return nodes;
    }

    /**
     * Finds the nodes being reference by the given directory and file paths.
     * <p>
     * Examples of the path are:
     * <ul>
     *   <li>\New Folder\New Text Document.txt</li>
     *   <li>\New Folder\Sub Folder</li>
     * </ul>
     * 
     * @param searchRootNodeRef the node from which to start the path search
     * @param path the search path to either a folder or file
     * @return Returns references to all matching nodes
     */
    public List<NodeRef> getNodeRefs(NodeRef pathRootNodeRef, String path)
    {
        // first check the cache to see if there are any results for this path query
        List<NodeRef> cachedResults = filePathCache.getPathResults(pathRootNodeRef, path);
        if (cachedResults != null)
        {
            // the cache had results for good or bad
            return cachedResults;
        }
        
        
        
        // tokenize the path and push into a stack in reverse order so that
        // the root directory gets popped first
        StringTokenizer tokenizer = new StringTokenizer(path, FileName.DOS_SEPERATOR_STR, false);
        String[] tokens = new String[tokenizer.countTokens()];
        int count = 0;
        while(tokenizer.hasMoreTokens())
        {
            tokens[count] = tokenizer.nextToken();
            count++;
        }
        Stack<String> pathElements = new Stack<String>();
        for (int i = tokens.length - 1; i >= 0; i--)
        {
            pathElements.push(tokens[i]);
        }
        
        // start with a single parent node
        List<NodeRef> pathRootNodeRefs = Collections.singletonList(pathRootNodeRef);
        
        // result storage
        List<NodeRef> results = new ArrayList<NodeRef>(5);
        
        // kick off the path walking
        addDescendents(pathRootNodeRefs, pathElements, results); 
        
        // cache the search results
        filePathCache.setPathResults(pathRootNodeRef, path, results);
        
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Retrieved node references for path: \n" +
                    "   path root: " + pathRootNodeRef + "\n" +
                    "   path: " + path + "\n" +
                    "   results: " + results);
        }
        return results;
    }
    
    /**
     * Attempts to fetch a specific single node at the given path.
     * 
     * @throws FileNotFoundException if the path can't be resolved to a node
     * 
     * @see #getNodeRefs(NodeRef, String)
     */
    public NodeRef getNodeRef(NodeRef pathRootNodeRef, String path) throws FileNotFoundException
    {
        // attempt to get the file/folder node using hierarchy walking
        List<NodeRef> nodeRefs = getNodeRefs(pathRootNodeRef, path);
        if (nodeRefs.size() == 0)
        {
            throw new FileNotFoundException(path);
        }
        else if (nodeRefs.size() > 1)
        {
            logger.warn("Multiple matching nodes: \n" +
                    "   search root: " + pathRootNodeRef + "\n" +
                    "   path: " + path);
        }
        // take the first one - not sure if it is possible for the path to refer to more than one
        NodeRef nodeRef = nodeRefs.get(0);
        // done
        return nodeRef;
    }

}

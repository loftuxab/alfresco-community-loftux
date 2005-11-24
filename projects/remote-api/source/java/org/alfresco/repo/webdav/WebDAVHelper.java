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
package org.alfresco.repo.webdav;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.search.QueryParameterDefImpl;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.lock.LockService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.CopyService;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.cmr.search.QueryParameterDefinition;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.helpers.AttributesImpl;

/**
 * WebDAV Protocol Helper Class
 * 
 * <p>Provides helper methods for repository access using the WebDAV protocol.
 * 
 * @author GKSpencer
 */
public class WebDAVHelper
{
    // Constants
    //
    // XPath query strings for wildcard and specific file/folder searches
    
    private final String xpathQueryWildcard     = "./*[like(@cm:name, $cm:name, false) and (subtypeOf($cm:foldertype) or subtypeOf($cm:filetype))]";
    private final String xpathQueryFile         = "./*[@cm:name = $cm:name and (subtypeOf($cm:foldertype) or subtypeOf($cm:filetype))]";
    private final String xpathQueryFilesFolders = "./*[(subtypeOf($cm:foldertype) or subtypeOf($cm:filetype))]";
        
    // Query parameter names
    
    private final String xpathParamName         = "cm:name";
    private final String xpathParamFileType     = "cm:filetype";
    private final String xpathParamFolderType   = "cm:foldertype";
    
    // Path seperator
    
    public static final String PathSeperator   = "/";
    public static final char PathSeperatorChar = '/';
    
    // File/folder exists status
    
    public static final int NotExist     = 0;
    public static final int FileExists   = 1; 
    public static final int FolderExists = 2;
    
    // Logging
    
    private static Log logger = LogFactory.getLog("org.alfresco.protocol.webdav");
    
    // Service registry
    
    private ServiceRegistry m_serviceRegistry;

    // Services
    
    private NodeService m_nodeService;
    private SearchService m_searchService;
    private NamespaceService m_namespaceService;
    private DictionaryService m_dictionaryService;
    private MimetypeService m_mimetypeService;
    private LockService m_lockService;
    private AuthenticationService m_authService;
    
    // Query data types and fixed parameter types
    
    private DataTypeDefinition m_dataType;
    
    private QName m_cmName;
    private QName m_cmFolderType;
    private QName m_cmFileType;
    
    private QueryParameterDefinition m_fileType;
    private QueryParameterDefinition m_folderType;

    //  Empty XML attribute list
    
    private AttributesImpl m_nullAttribs = new AttributesImpl();
    
    /**
     * Class constructor
     * 
     * @param serviceRegistry ServiceRegistry
     * @param authService AuthenticationService
     */
    protected WebDAVHelper(ServiceRegistry serviceRegistry, AuthenticationService authService)
    {
        m_serviceRegistry = serviceRegistry;
        
        m_nodeService       = m_serviceRegistry.getNodeService();
        m_searchService     = m_serviceRegistry.getSearchService();
        m_namespaceService  = m_serviceRegistry.getNamespaceService();
        m_dictionaryService = m_serviceRegistry.getDictionaryService();
        m_mimetypeService   = m_serviceRegistry.getMimetypeService();
        m_lockService       = m_serviceRegistry.getLockService();
        
        m_authService       = authService;
        
        // Initialize the helper
        
        initialize();
    }
    
    /**
     * Initialize the helper class
     */
    private final void initialize()
    {
        // Get the text data type
        
        m_dataType = m_dictionaryService.getDataType(DataTypeDefinition.TEXT);
        
        // Generate the parameter names for searches
        
        m_cmName       = QName.createQName("cm:name", m_namespaceService);
        m_cmFolderType = QName.createQName("cm:foldertype", m_namespaceService);
        m_cmFileType   = QName.createQName("cm:filetype", m_namespaceService);
        
        // Create the fixed search parameter definitions
        
        m_fileType   = new QueryParameterDefImpl( m_cmFileType, m_dataType, true, ContentModel.TYPE_CONTENT.toString());
        m_folderType = new QueryParameterDefImpl( m_cmFolderType, m_dataType, true, ContentModel.TYPE_FOLDER.toString());
    }
    
    /**
     * Return the authentication service
     * 
     * @return AuthenticationService
     */
    public final AuthenticationService getAuthenticationService()
    {
        return m_authService;
    }
    
    /**
     * Return the service registry
     * 
     * @return ServiceRegistry
     */
    public final ServiceRegistry getServiceRegistry()
    {
        return m_serviceRegistry;
    }
    
    /**
     * Return the node service
     * 
     * @return NodeService
     */
    public final NodeService getNodeService()
    {
        return m_nodeService;
    }
    
    /**
     * Return the search service
     * 
     * @return  SearchService
     */
    public final SearchService getSearchService()
    {
        return m_searchService;
    }
    
    /**
     * Return the namespace service
     * 
     * @return  NamespaceService
     */
    public final NamespaceService getNamespaceService()
    {
        return m_namespaceService;
    }
    
    /**
     * Return the dictionary service
     * 
     * @return  DictionaryService
     */
    public final DictionaryService getDictionaryService()
    {
        return m_dictionaryService;
    }

    /**
     * Return the mimetype service
     * 
     * @return MimetypeService
     */
    public final MimetypeService getMimetypeService()
    {
        return m_mimetypeService;
    }
    
    /**
     * Return the lock service
     * 
     * @return LockService
     */
    public final LockService getLockService()
    {
        return m_lockService;
    }
    
    /**
     * Return the copy service
     * 
     * @return CopyService
     */
    public final CopyService getCopyService()
    {
        return getServiceRegistry().getCopyService();
    }
    
    /**
     * Find the root node for the specified store and path
     * 
     * @param storeId String
     * @param path String
     * @return NodeRef
     */
    public final NodeRef findRootNode( String storeId, String storePath)
    {
        // Check if the store exists
        
        StoreRef storeRef = new StoreRef(storeId);
        
        if ( m_nodeService.exists( storeRef) == false)
            throw new AlfrescoRuntimeException("Store does not exist");
        
        NodeRef storeRootNodeRef = m_nodeService.getRootNode( storeRef);
        
        // Find the root node for this device
        
        List<NodeRef> nodeRefs = m_searchService.selectNodes( storeRootNodeRef, storePath, null, m_namespaceService, false);
        NodeRef rootNodeRef = null;
        
        if (nodeRefs.size() > 1)
            throw new AlfrescoRuntimeException("Multiple possible roots for device");
        else if (nodeRefs.size() == 0)
            throw new AlfrescoRuntimeException("No root found for device");
        else
            rootNodeRef = nodeRefs.get(0);
        
        // Return the root node
        
        return rootNodeRef;
    }
    
    /**
     * Check if the specified path is a file, folder or does not exist
     * 
     * @param rootNode NodeRef
     * @param path String
     * @return int
     */
    public final int getPathStatus(NodeRef rootNode, String path)
    {
        // Check if the path is valid
        
        if ( rootNode == null || path == null)
            return NotExist;
        
        // Check for the root path
        
        if ( path.length() == 0 || path.equals(PathSeperator))
            return FolderExists;

        // Split the path into the component names
        
        String[] parts = splitAllPaths( path);
        if ( parts == null)
            return NotExist;

        // Create the search parameters
        
        QueryParameterDefinition[] qparams = new QueryParameterDefinition[3];
        
        qparams[1] = m_folderType;
        qparams[2] = m_fileType;
        
        // Walk the directory tree
        
        NodeRef curRootNode = rootNode;
        QName nodeType = null;
        
        int sts = NotExist;
        int idx = 0;
        
        while ( idx < parts.length)
        {
            // Search for the current path element
            
            qparams[0] = new QueryParameterDefImpl( m_cmName, m_dataType, true, parts[idx++]);
            
            List<NodeRef> nodes = m_searchService.selectNodes(curRootNode, xpathQueryFile, qparams, m_namespaceService, false);

            if ( nodes == null || nodes.size() != 1)
                return NotExist;
            
            // Make sure the node is a folder, unless this is the last part of the path
            
            curRootNode = nodes.get(0);
            nodeType = m_nodeService.getType(curRootNode);
            
            if (m_dictionaryService.isSubClass(nodeType, ContentModel.TYPE_FOLDER))
            {
                // Set the status for the current path component
                
                sts = FolderExists;
            }
            else if (m_dictionaryService.isSubClass(nodeType, ContentModel.TYPE_CONTENT))
            {
                // Set the status for the current path component
                
                sts = FileExists;
            }
            else
            {
                // Log an error
                
                if ( logger.isDebugEnabled())
                    logger.debug("Unexpected not type during path walk, path=" + path + ", element=" + parts[idx-1]);
                
                // Not an expected node type
                
                return sts = NotExist;   
            }

            // Unless we are at the end of the tree walk the node must be a folder
            
            if ( idx < parts.length && sts != FolderExists)
            {
                // DEBUG
                
                if ( logger.isDebugEnabled())
                    logger.debug("File node in middle of path walk, path=" + path + ", element=" + parts[idx-1]);
                
                return NotExist;
            }
        }
        
        // Return the file status
        
        return sts;
    }
    
    /**
     * Make a path by walking down the path and creating any folder nodes as necessary
     * 
     * @param rootNode NodeRef
     * @param path String
     * @return NodeRef
     */
    public final NodeRef makePath(NodeRef rootNode, String path)
    {
        // Check if the path is valid
        
        if ( rootNode == null || path == null)
            return null;
        
        // Check for the root path
        
        if ( path.length() == 0 || path.equals(PathSeperator))
            return rootNode;

        // Split the path into the component names
        
        String[] parts = splitAllPaths( path);
        if ( parts == null)
            return null;

        // Create the search parameters
        
        QueryParameterDefinition[] qparams = new QueryParameterDefinition[3];
        
        qparams[1] = m_folderType;
        qparams[2] = m_fileType;
        
        // Walk the directory tree
        
        NodeRef curRootNode = rootNode;
        QName nodeType = null;
        
        int sts = FolderExists;
        int idx = 0;
        
        while ( sts == FolderExists && idx < parts.length)
        {
            // Search for the current path element
            
            qparams[0] = new QueryParameterDefImpl( m_cmName, m_dataType, true, parts[idx++]);
            
            List<NodeRef> nodes = m_searchService.selectNodes(curRootNode, xpathQueryFile, qparams, m_namespaceService, false);

            if ( nodes == null || nodes.size() != 1)
            {
                // Indicate that the current folder does not exist
                
                sts = NotExist;
            }
            else
            {
                // Make sure the node is a folder, unless this is the last part of the path
                
                curRootNode = nodes.get(0);
                nodeType = m_nodeService.getType(curRootNode);
                
                if (m_dictionaryService.isSubClass(nodeType, ContentModel.TYPE_FOLDER))
                {
                    // Set the status for the current path component
                    
                    sts = FolderExists;
                }
                else if (m_dictionaryService.isSubClass(nodeType, ContentModel.TYPE_CONTENT))
                {
                    // Should be a folder
                    
                    return null;
                }
                else
                {
                    // Log an error
                    
                    if ( logger.isDebugEnabled())
                        logger.debug("Unexpected not type during path walk, path=" + path + ", element=" + parts[idx-1]);
                    
                    // Not an expected node type
                    
                    return null;   
                }
            }
        }
        
        // Check if we reached the end of the path list, if not then create the missing folder nodes
        
        if ( idx < parts.length && curRootNode != null)
        {
            // Create the remaining folders
            
            while ( idx < parts.length)
            {
                // Create a folder
                
                curRootNode = createNode( curRootNode, parts[idx++], false);
            }
        }
        
        // Return the folder node at the end of the path
        
        return curRootNode;
    }
    
    /**
     * Split the path into seperate directory path and file name strings.
     * 
     * @param path Full path string.
     * @return String[]
     */
    public final String[] splitPath(String path)
    {
        // Create an array of strings to hold the path and file name strings

        String[] pathStr = new String[2];

        // Check if the path is valid

        if (path != null && path.length() > 0)
        {

            // Check if the path has a trailing seperator, if so then there is no
            // file name.

            int pos = path.lastIndexOf(PathSeperatorChar);

            if (pos == -1 || pos == (path.length() - 1))
            {

                // Set the path string in the returned string array

                pathStr[0] = path;
            }
            else
            {

                // Split the path into directory list and file name strings

                pathStr[1] = path.substring(pos + 1);

                if (pos == 0)
                    pathStr[0] = path.substring(0, pos + 1);
                else
                    pathStr[0] = path.substring(0, pos);
            }
        }
        else
        {

            // Set the directory and file name to empty strings

            pathStr[0] = "";
            pathStr[1] = "";
        }

        // Return the path strings

        return pathStr;
    }
    
    /**
     * Split the path into all the component directories and filename
     * 
     * @param path String
     * @return String[]
     */
    public final String[] splitAllPaths(String path)
    {

        // Check if the path is valid

        if (path == null || path.length() == 0)
            return null;

        // Determine the number of components in the path

        StringTokenizer token = new StringTokenizer(path, PathSeperator);
        String[] names = new String[token.countTokens()];

        // Split the path

        int i = 0;

        while (i < names.length && token.hasMoreTokens())
            names[i++] = token.nextToken();

        // Return the path components

        return names;
    }
    
    /**
     * Find the node for the specified relative path
     * 
     * @param rootNode NodeRef
     * @param path String
     * @param servletPath String
     * @return NodeRef
     */
    public final NodeRef getNodeForPath(NodeRef rootNode, String path, String servletPath)
    {
        // Check if the path is valid
        
        if ( rootNode == null || path == null)
            return null;
        
        // Check for the root path
        
        if ( path.length() == 0 || path.equals(PathSeperator))
            return rootNode;

        if ( path.equalsIgnoreCase(servletPath))
            return rootNode;
        
        // Check if the path starts with the servlet path
        
        if ( path.startsWith(servletPath))
        {
            // Strip the servlet path from the relative path
            
            path = path.substring(servletPath.length());
        }
        
        // Split the path into the component names
        
        String[] parts = splitAllPaths( path);
        if ( parts == null)
            return null;

        // Create the search parameters
        
        QueryParameterDefinition[] qparams = new QueryParameterDefinition[3];
        
        qparams[1] = m_folderType;
        qparams[2] = m_fileType;
        
        // Walk the directory tree
        
        NodeRef curRootNode = rootNode;
        QName nodeType = null;

        int idx = 0;
        
        while ( idx < parts.length)
        {
            // Search for the current path element
            
            qparams[0] = new QueryParameterDefImpl( m_cmName, m_dataType, true, parts[idx++]);
            
            List<NodeRef> nodes = m_searchService.selectNodes(curRootNode, xpathQueryFile, qparams, m_namespaceService, false);

            if ( nodes == null || nodes.size() != 1)
                return null;
            
            // Make sure the node is a folder, unless this is the last part of the path
            
            curRootNode = nodes.get(0);
            nodeType = m_nodeService.getType(curRootNode);
            
            if (m_dictionaryService.isSubClass(nodeType, ContentModel.TYPE_FOLDER) == false &&
                    m_dictionaryService.isSubClass(nodeType, ContentModel.TYPE_CONTENT) == false)
            {
                // Log an error
                
                if ( logger.isDebugEnabled())
                    logger.debug("Unexpected not type during path walk, path=" + path + ", element=" + parts[idx-1]);
                
                // Not an expected node type
                
                return null;   
            }

            // Unless we are at the end of the tree walk the node must be a folder
            
            if ( idx < parts.length && m_dictionaryService.isSubClass(nodeType, ContentModel.TYPE_CONTENT))
            {
                // DEBUG
                
                if ( logger.isDebugEnabled())
                    logger.debug("File node in middle of path walk, path=" + path + ", element=" + parts[idx-1]);
                
                return null;
            }
        }
        
        // Return the node
        
        return curRootNode;
    }
    
    /**
     * Append child nodes to the specified node list, returning the count of nodes added
     * 
     * @param rootNode NodeRef
     * @param nodes List<NodeRef>
     * @return int 
     */
    public final int appendChildNodes(NodeRef rootNode, List<NodeRef> nodes)
    {
        // Check if the node is a folder type node
        
        int appendCnt = 0;

        List<NodeRef> childNodes = getChildNodes(rootNode);
        
        // If the search returned any nodes then append them to the callers node list
        
        if ( childNodes != null && childNodes.size() > 0)
        {
            for ( NodeRef curNode : childNodes)
            {
                nodes.add( curNode);
                appendCnt++;
            }
        }
        
        // Return the count of nodes appended to the list
        
        return appendCnt;
    }
    
    /**
     * Return the list of child nodes for the specified node
     * 
     * @param rootNode NodeRef
     * @return List<NodeRef> 
     */
    public final List<NodeRef> getChildNodes(NodeRef rootNode)
    {
        // Check if the node is a folder type node

        List<NodeRef> childNodes = null;
        
        if ( m_dictionaryService.isSubClass( m_nodeService.getType(rootNode), ContentModel.TYPE_FOLDER))
        {
            // Create the search parameters
            
            QueryParameterDefinition[] qparams = new QueryParameterDefinition[2];
            
            qparams[0] = m_folderType;
            qparams[1] = m_fileType;
            
            childNodes = m_searchService.selectNodes(rootNode, xpathQueryFilesFolders, qparams, m_namespaceService, false);
        }
        
        // Return the list of child nodes, or null if the root node is not a folder node
        
        return childNodes;
    }
    
    /**
     * Check if the node is a folder node
     * 
     * @param node NodeRef
     * @return boolean
     */
    public final boolean isFolderNode(NodeRef node)
    {
        // Check if the node is a folder
        
        return getDictionaryService().isSubClass(getNodeService().getType(node), ContentModel.TYPE_FOLDER);        
    }
    
    /**
     * Return the relative path for the node walking back to the specified root node
     * 
     * @param fromNode NodeRef
     * @param rootNode NodeRef
     * @return String
     */
    public final String getPathFromNode(NodeRef fromNode, NodeRef rootNode)
    {
        // Check if the nodes are valid, or equal
        
        if ( fromNode == null || rootNode == null)
            throw new AlfrescoRuntimeException("Invalid node(s) in getPathFromNode call");
        
        if ( fromNode.equals(rootNode))
            return "";
        
        // Build the path from the child node to the root node
        
        StringBuilder pathStr = new StringBuilder(256);

        ChildAssociationRef parentRef = m_nodeService.getPrimaryParent( fromNode);
        
        while ( parentRef != null)
        {
            // Get the parent node and name
            
            NodeRef parentNode = parentRef.getParentRef();
            Object parentName = m_nodeService.getProperty( parentNode, ContentModel.PROP_NAME);
            
            // Prepend the current node name to the path
            
            pathStr.insert(0, PathSeperator);
            pathStr.insert(0, DefaultTypeConverter.INSTANCE.convert(String.class, parentName));
            
            // Step to the the next parent
            
            parentRef = m_nodeService.getPrimaryParent(parentNode);
            
            // Check if we reached the required parent
            
            if ( parentRef != null && parentRef.getParentRef().equals(rootNode))
            {
                // Stop the tree walk now, we have reached the required parent

                parentRef = null;
            }
        }
        
        // Return the relative path to the node
        
        return pathStr.toString();
    }
    
    /**
     * Create a new file or folder node attached to the specified parent node
     * 
     * @param parent NodeRef
     * @param fname String
     * @param isFile boolean
     * @return NodeRef
     */
    public final NodeRef createNode(NodeRef parent, String fname, boolean isFile)
    {
        // Create the properties for the new file/folder

        NodeService nodeService = getNodeService();
        MimetypeService mimetypeService = getMimetypeService();
        
        Map<QName, Serializable> properties = new HashMap<QName, Serializable>(5);
        properties.put(ContentModel.PROP_NAME, fname);
        
        if (isFile)
        {
            String mimetype = mimetypeService.guessMimetype(fname);
            properties.put(ContentModel.PROP_CONTENT, new ContentData(null, mimetype, 0L, "UTF-8"));
        }
        
        // Encode the path name
        
        String encodedPath = QName.createValidLocalName(fname);
        
        // Create the new node
        
        ChildAssociationRef assocRef = nodeService.createNode(
                parent,
                ContentModel.ASSOC_CONTAINS,
                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, encodedPath),
                isFile ? ContentModel.TYPE_CONTENT : ContentModel.TYPE_FOLDER,
                properties);
        
        NodeRef currentNodeRef = assocRef.getChildRef();
        
        Map<QName, Serializable> uiproperties = new HashMap<QName, Serializable>(2);
        uiproperties.put(ContentModel.PROP_TITLE, fname);
        uiproperties.put(ContentModel.PROP_DESCRIPTION, fname);
        nodeService.addAspect(currentNodeRef, ContentModel.ASPECT_UIFACETS, uiproperties);
        
        // Return the new node
        
        return currentNodeRef;
    }
    
    /**
     * Create a new file or folder node attached to the specified parent node with the specified
     * proeprties
     * 
     * @param parent NodeRef
     * @param properties Map<QName, Serializable>
     * @param isFile boolean
     * @return NodeRef
     */
    public final NodeRef createNodeWithProperties(NodeRef parent, Map<QName, Serializable> properties,
            boolean isFile)
    {
        // Create the properties for the new file/folder

        NodeService nodeService = getNodeService();
        
        // Get the path name
        
        Object prop = properties.get(ContentModel.PROP_NAME);
        String fname = null;
        if ( prop != null)
            fname = DefaultTypeConverter.INSTANCE.convert(String.class, prop);
        
        String encodedPath = QName.createValidLocalName(fname);
        
        // Create the new node
        
        ChildAssociationRef assocRef = nodeService.createNode(
                parent,
                ContentModel.ASSOC_CONTAINS,
                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, encodedPath),
                isFile ? ContentModel.TYPE_CONTENT : ContentModel.TYPE_FOLDER,
                properties);
        
        NodeRef currentNodeRef = assocRef.getChildRef();
        
        Map<QName, Serializable> uiproperties = new HashMap<QName, Serializable>(2);
        uiproperties.put(ContentModel.PROP_TITLE, fname);
        uiproperties.put(ContentModel.PROP_DESCRIPTION, fname);
        nodeService.addAspect(currentNodeRef, ContentModel.ASPECT_UIFACETS, uiproperties);
        
        // Return the new node
        
        return currentNodeRef;
    }
    
    /**
     * Make an ETag value for a node using the GUID and modify date/time
     * 
     * @param node NodeRef
     * @return String
     */
    public final String makeETag(NodeRef node)
    {
        // Get the modify date/time property for the node
        
        StringBuilder etag = new StringBuilder();
        makeETagString(node, etag);
        return etag.toString();
    }
    
    /**
     * Make an ETag value for a node using the GUID and modify date/time
     * 
     * @param node NodeRef
     * @return String
     */
    public final String makeQuotedETag(NodeRef node)
    {
        StringBuilder etag = new StringBuilder();
        
        etag.append("\"");
        makeETagString(node, etag);
        etag.append("\"");
        return etag.toString();
    }
    
    /**
     * Make an ETag value for a node using the GUID and modify date/time
     * 
     * @param node NodeRef
     * @param str StringBuilder
     */
    protected final void makeETagString(NodeRef node, StringBuilder etag)
    {
        // Get the modify date/time property for the node
        
        Object modVal = getNodeService().getProperty(node, ContentModel.PROP_MODIFIED);
        
        etag.append(node.getId());
        
        if ( modVal != null)
        {
            etag.append("_");
            etag.append(DefaultTypeConverter.INSTANCE.longValue(modVal));
        }
    }
    
    /**
     * Return the null XML attribute list
     * 
     * @return AttributesImpl
     */
    public final AttributesImpl getNullAttributes()
    {
        return m_nullAttribs;
    }
}

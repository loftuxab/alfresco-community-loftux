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
package org.alfresco.filesys.smb.server.repo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.alfresco.config.ConfigElement;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.filesys.server.SrvSession;
import org.alfresco.filesys.server.core.DeviceContext;
import org.alfresco.filesys.server.core.DeviceContextException;
import org.alfresco.filesys.server.filesys.AccessDeniedException;
import org.alfresco.filesys.server.filesys.AccessMode;
import org.alfresco.filesys.server.filesys.DiskDeviceContext;
import org.alfresco.filesys.server.filesys.FileInfo;
import org.alfresco.filesys.server.filesys.FileName;
import org.alfresco.filesys.server.filesys.FileOpenParams;
import org.alfresco.filesys.server.filesys.FileStatus;
import org.alfresco.filesys.server.filesys.FileSystem;
import org.alfresco.filesys.server.filesys.NetworkFile;
import org.alfresco.filesys.server.filesys.SearchContext;
import org.alfresco.filesys.server.filesys.SrvDiskInfo;
import org.alfresco.filesys.server.filesys.TreeConnection;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * First-pass implementation to enable SMB support in the repo.
 * 
 * @author Derek Hulley
 */
public class ContentDiskDriver implements ContentDiskInterface
{
    private static final String KEY_STORE = "store";
    private static final String KEY_ROOT_PATH = "rootPath";
    
    private static final Log logger = LogFactory.getLog(ContentDiskDriver.class);
    
    private CifsHelper cifsHelper;
    private TransactionService transactionService;
    private NamespaceService namespaceService;
    private DictionaryService dictionaryService;
    private NodeService nodeService;
    private NodeService unprotectedNodeService;
    private SearchService searchService;
    private SearchService unprotectedSearchService;
    private ContentService contentService;
    private MimetypeService mimetypeService;
    private PermissionService permissionService;

    private String shareName;
    private NodeRef rootNodeRef;

    /**
     * @param serviceRegistry to connect to the repository services
     */
    public ContentDiskDriver(CifsHelper cifsHelper)
    {
        this.cifsHelper = cifsHelper;
    }

    /**
     * @param contentService the content service
     */
    public void setContentService(ContentService contentService)
    {
        this.contentService = contentService;
    }

    /**
     * @param dictionaryService the dictionary service
     */
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    /**
     * @param mimetypeService the mimetype service
     */
    public void setMimetypeService(MimetypeService mimetypeService)
    {
        this.mimetypeService = mimetypeService;
    }

    /**
     * @param namespaceService the namespace service
     */
    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }

    /**
     * @param nodeService the node service
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    /**
     * @param nodeService the node service
     */
    public void setUnprotectedNodeService(NodeService nodeService)
    {
        this.unprotectedNodeService = nodeService;
    }

    /**
     * @param searchService the search service
     */
    public void setSearchService(SearchService searchService)
    {
        this.searchService = searchService;
    }

    /**
     * @param searchService the search service
     */
    public void setUnprotectedSearchService(SearchService searchService)
    {
        this.unprotectedSearchService = searchService;
    }

    
    /**
     * @param transactionService the transaction service
     */
    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }

    /**
     * Set the permission service
     * 
     * @param permissionService PermissionService
     */
    public void setPermissionService(PermissionService permissionService)
    {
        this.permissionService = permissionService;
    }
    
    /**
     * @param element the configuration element from which to configure the class
     */
    public DeviceContext createContext(ConfigElement cfg) throws DeviceContextException
    {
        // get the name of the share
        shareName = cfg.getAttribute("name");
        
        // get the store
        ConfigElement storeElement = cfg.getChild(KEY_STORE);
        if (storeElement == null || storeElement.getValue() == null || storeElement.getValue().length() == 0)
        {
            throw new DeviceContextException("Device missing init value: " + KEY_STORE);
        }
        String storeValue = storeElement.getValue();
        StoreRef storeRef = new StoreRef(storeValue);
        
        // connect to the repo and ensure that the store exists
        if (!unprotectedNodeService.exists(storeRef))
        {
            throw new DeviceContextException("Store not created prior to application startup: " + storeRef);
        }
        NodeRef storeRootNodeRef = unprotectedNodeService.getRootNode(storeRef);
        
        // get the root path
        ConfigElement rootPathElement = cfg.getChild(KEY_ROOT_PATH);
        if (rootPathElement == null || rootPathElement.getValue() == null || rootPathElement.getValue().length() == 0)
        {
            throw new DeviceContextException("Device missing init value: " + KEY_ROOT_PATH);
        }
        String rootPath = rootPathElement.getValue();
        // find the root node for this device
        List<NodeRef> nodeRefs = unprotectedSearchService.selectNodes(
                storeRootNodeRef, rootPath, null, namespaceService, false);
        if (nodeRefs.size() > 1)
        {
            throw new DeviceContextException("Multiple possible roots for device: \n" +
                    "   root path: " + rootPath + "\n" +
                    "   results: " + nodeRefs);
        }
        else if (nodeRefs.size() == 0)
        {
            // nothing found
            throw new DeviceContextException("No root found for device: \n" +
                    "   root path: " + rootPath);
        }
        else
        {
            // we found a node
            rootNodeRef = nodeRefs.get(0);
        }
        
        // create the context
        DiskDeviceContext context = new DiskDeviceContext(rootNodeRef.toString());

        //  Default the filesystem to look like an 80Gb sized disk with 90% free space
        context.setDiskInformation(new SrvDiskInfo(2560, 64, 512, 2304));
        
        // set parameters
        context.setFilesystemAttributes(FileSystem.CasePreservedNames);
        
        // done
        return context;
    }

    public String getShareName()
    {
        return shareName;
    }

    public NodeRef getContextRootNodeRef()
    {
        return rootNodeRef;
    }

    /**
     * Always writable
     */
    public boolean isReadOnly(SrvSession sess, DeviceContext ctx) throws IOException
    {
        return false;
    }
    
    /**
     * Helper method to get the device root
     * 
     * @param tree
     * @return Returns the device root node
     * @throws AlfrescoRuntimeException if the device root node does not exist
     */
    private NodeRef getDeviceRootNode(TreeConnection tree)
    {
        // get the device root
        String deviceName = tree.getContext().getDeviceName();
        NodeRef deviceRootNodeRef = new NodeRef(deviceName);
        
        // no need to check for existence - we checked when the service started
        // done
        return deviceRootNodeRef;
    }
    
    /**
     * Device method
     * 
     * @see ContentDiskDriver#getFileInformation(NodeRef, boolean)
     */
    public FileInfo getFileInformation(SrvSession session, TreeConnection tree, String path) throws IOException
    {
        // get the device root
        NodeRef deviceRootNodeRef = getDeviceRootNode(tree);
        
        try
        {
            boolean includeName = (path.length() > 0);
            FileInfo fileInfo = cifsHelper.getFileInformation(deviceRootNodeRef, path, includeName);
            // done
            if (logger.isDebugEnabled())
            {
                logger.debug("Getting file information: \n" +
                        "   path: " + path + "\n" +
                        "   file info: " + fileInfo);
            }
            return fileInfo;
        }
        catch (FileNotFoundException e)
        {
            // a valid use case
            if (logger.isDebugEnabled())
            {
                logger.debug("Getting file information - File not found: \n" +
                        "   path: " + path);
            }
            throw e;
        }
        catch (org.alfresco.repo.security.permissions.AccessDeniedException ex)
        {
            // Debug
            
            if ( logger.isDebugEnabled())
                logger.debug("Get file info - access denied, " + path);
            
            // Convert to a filesystem access denied status
            
            throw new AccessDeniedException("Get file information " + path);
        }
        catch (AlfrescoRuntimeException ex)
        {
            // Debug
            
            if ( logger.isDebugEnabled())
                logger.debug("Get file info error", ex);
            
            // Convert to a general I/O exception
            
            throw new IOException("Get file information " + path);
        }
    }

    public SearchContext startSearch(SrvSession sess, TreeConnection tree, String searchPath, int attributes) throws FileNotFoundException
    {
        try
        {
            // get the device root
            NodeRef deviceRootNodeRef = getDeviceRootNode(tree);
            
            SearchContext ctx = ContentSearchContext.search(transactionService, cifsHelper, deviceRootNodeRef, searchPath, attributes);
            // done
            if (logger.isDebugEnabled())
            {
                logger.debug("Started search: \n" +
                        "   search path: " + searchPath + "\n" +
                        "   attributes: " + attributes);
            }
            return ctx;
        }
        catch (org.alfresco.repo.security.permissions.AccessDeniedException ex)
        {
            // Debug
            
            if ( logger.isDebugEnabled())
                logger.debug("Start search - access denied, " + searchPath);
            
            // Convert to a file not found status
            
            throw new FileNotFoundException("Start search " + searchPath);
        }
        catch (AlfrescoRuntimeException ex)
        {
            // Debug
            
            if ( logger.isDebugEnabled())
                logger.debug("Start search", ex);
            
            // Convert to a file not found status
            
            throw new FileNotFoundException("Start search " + searchPath);
        }
    }

    /**
     * @see FileStatus
     */
    public int fileExists(SrvSession sess, TreeConnection tree, String name)
    {
        // get the device root
        NodeRef deviceRootNodeRef = getDeviceRootNode(tree);
        
        int status = FileStatus.Unknown; 
        // get the file info
        try
        {
            FileInfo info = getFileInformation(sess, tree, name);
            if (info.isDirectory())
            {
                status = FileStatus.DirectoryExists;
            }
            else
            {
                status = FileStatus.FileExists;
            }
        }
        catch (FileNotFoundException e)
        {
            status = FileStatus.NotExist;
        }
        catch (IOException e)
        {
            // Debug
            
            logger.debug("File exists error, " + name, e);
            
            status = FileStatus.NotExist;
        }

        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("File status determined: \n" +
                    "   name: " + name + "\n" +
                    "   status: " + status);
        }
        return status;
    }
    
    /**
     * Open a file or folder
     * 
     * @param sess SrvSession
     * @param tree TreeConnection
     * @param params FileOpenParams
     * @return NetworkFile
     * @exception IOException
     */
    public NetworkFile openFile(SrvSession sess, TreeConnection tree, FileOpenParams params) throws IOException
    {
        try
        {
            // Get the device root
            
            NodeRef deviceRootNodeRef = getDeviceRootNode(tree);
            
            String path = params.getPath(); 
    
            // Get the file/folder node
            
            NodeRef nodeRef = cifsHelper.getNodeRef(deviceRootNodeRef, path);
            
            // Check permissions on the file/folder node
            //
            // Check for read access
            
            if ( params.hasAccessMode(AccessMode.NTRead) &&
                    permissionService.hasPermission(nodeRef, PermissionService.READ) == AccessStatus.DENIED)
                throw new AccessDeniedException("No read access to " + params.getFullPath());
                
            // Check for write access
            
            if ( params.hasAccessMode(AccessMode.NTWrite) &&
                    permissionService.hasPermission(nodeRef, PermissionService.WRITE) == AccessStatus.DENIED)
                throw new AccessDeniedException("No write access to " + params.getFullPath());
            
            // Check for delete access
            
            if ( params.hasAccessMode(AccessMode.NTDelete) &&
                    permissionService.hasPermission(nodeRef, PermissionService.DELETE) == AccessStatus.DENIED)
                throw new AccessDeniedException("No delete access to " + params.getFullPath());
            
            // Create the network file
            
            NetworkFile netFile = ContentNetworkFile.createFile(nodeService, contentService, cifsHelper, nodeRef, params);
            
            // Debug
            
            if (logger.isDebugEnabled())
            {
                logger.debug("Opened network file: \n" +
                        "   path: " + path + "\n" +
                        "   file open parameters: " + params + "\n" +
                        "   network file: " + netFile);
            }

            // Return the network file
            
            return netFile;
        }
        catch (org.alfresco.repo.security.permissions.AccessDeniedException ex)
        {
            // Debug
            
            if ( logger.isDebugEnabled())
                logger.debug("Open file - access denied, " + params.getFullPath());
            
            // Convert to a filesystem access denied status
            
            throw new AccessDeniedException("Open file " + params.getFullPath());
        }
        catch (AlfrescoRuntimeException ex)
        {
            // Debug
            
            if ( logger.isDebugEnabled())
                logger.debug("Open file error", ex);
            
            // Convert to a general I/O exception
            
            throw new IOException("Open file " + params.getFullPath());
        }
    }
    
    /**
     * @see #createNode(NodeRef, String, String)
     */
    public NetworkFile createFile(SrvSession sess, TreeConnection tree, FileOpenParams params) throws IOException
    {
        try
        {
            // get the device root
            NodeRef deviceRootNodeRef = getDeviceRootNode(tree);
            
            String path = params.getPath(); 
            boolean isFile = !params.isDirectory();
            
            // create it - the path will be created, if necessary
            NodeRef nodeRef = cifsHelper.createNode(deviceRootNodeRef, path, isFile);
            
            // create the network file
            NetworkFile netFile = ContentNetworkFile.createFile(nodeService, contentService, cifsHelper, nodeRef, params);
            // done
            if (logger.isDebugEnabled())
            {
                logger.debug("Created file: \n" +
                        "   path: " + path + "\n" +
                        "   file open parameters: " + params + "\n" +
                        "   node: " + nodeRef + "\n" +
                        "   network file: " + netFile);
            }
            return netFile;
        }
        catch (org.alfresco.repo.security.permissions.AccessDeniedException ex)
        {
            // Debug
            
            if ( logger.isDebugEnabled())
                logger.debug("Create file - access denied, " + params.getFullPath());
            
            // Convert to a filesystem access denied status
            
            throw new AccessDeniedException("Create file " + params.getFullPath());
        }
        catch (AlfrescoRuntimeException ex)
        {
            // Debug
            
            if ( logger.isDebugEnabled())
                logger.debug("Create file error", ex);
            
            // Convert to a general I/O exception
            
            throw new IOException("Create file " + params.getFullPath());
        }
        
    }

    public void createDirectory(SrvSession sess, TreeConnection tree, FileOpenParams params) throws IOException
    {
        try
        {
            // get the device root
            NodeRef deviceRootNodeRef = getDeviceRootNode(tree);
            
            String path = params.getPath(); 
            boolean isFile = !params.isDirectory();
            
            // create it - the path will be created, if necessary
            NodeRef nodeRef = cifsHelper.createNode(deviceRootNodeRef, path, isFile);
            // done
            if (logger.isDebugEnabled())
            {
                logger.debug("Created directory: \n" +
                        "   path: " + path + "\n" +
                        "   file open params: " + params + "\n" +
                        "   node: " + nodeRef);
            }
        }
        catch (org.alfresco.repo.security.permissions.AccessDeniedException ex)
        {
            // Debug
            
            if ( logger.isDebugEnabled())
                logger.debug("Create directory - access denied, " + params.getFullPath());
            
            // Convert to a filesystem access denied status
            
            throw new AccessDeniedException("Create directory " + params.getFullPath());
        }
        catch (AlfrescoRuntimeException ex)
        {
            // Debug
            
            if ( logger.isDebugEnabled())
                logger.debug("Create directory error", ex);
            
            // Convert to a general I/O exception
            
            throw new IOException("Create directory " + params.getFullPath());
        }
    }

    public void deleteDirectory(SrvSession sess, TreeConnection tree, String dir) throws IOException
    {
        // get the device root
        NodeRef deviceRootNodeRef = getDeviceRootNode(tree);
        
        try
        {
            // get the node
            NodeRef nodeRef = cifsHelper.getNodeRef(deviceRootNodeRef, dir);
            if (nodeService.exists(nodeRef))
            {
                nodeService.deleteNode(nodeRef);
            }
            // done
            if (logger.isDebugEnabled())
            {
                logger.debug("Deleted directory: \n" +
                        "   directory: " + dir + "\n" +
                        "   node: " + nodeRef);
            }
        }
        catch (FileNotFoundException e)
        {
            // already gone
            if (logger.isDebugEnabled())
            {
                logger.debug("Deleted directory <alfready gone>: \n" +
                        "   directory: " + dir);
            }
        }
        catch (org.alfresco.repo.security.permissions.AccessDeniedException ex)
        {
            // Debug
            
            if ( logger.isDebugEnabled())
                logger.debug("Delete directory - access denied, " + dir);
            
            // Convert to a filesystem access denied status
            
            throw new AccessDeniedException("Delete directory " + dir);
        }
        catch (AlfrescoRuntimeException ex)
        {
            // Debug
            
            if ( logger.isDebugEnabled())
                logger.debug("Delete directory", ex);
            
            // Convert to a general I/O exception
            
            throw new IOException("Delete directory " + dir);
        }
    }

    public void flushFile(SrvSession sess, TreeConnection tree, NetworkFile file) throws IOException
    {
        // Flush the file data
        
        file.flushFile();
    }

    public void closeFile(SrvSession sess, TreeConnection tree, NetworkFile file) throws IOException
    {
        // Defer to the network file to close the stream and remove the content
           
        file.closeFile();
        
        // remove the node if necessary
        if (file.hasDeleteOnClose())
        {
            ContentNetworkFile contentNetFile = (ContentNetworkFile) file;
            NodeRef nodeRef = contentNetFile.getNodeRef();
            // we don't know how long the network file has had the reference, so check for existence
            if (nodeService.exists(nodeRef))
            {
                try
                {
                    // Delete the file
                    
                    nodeService.deleteNode(nodeRef);
                }
                catch (org.alfresco.repo.security.permissions.AccessDeniedException ex)
                {
                    // Debug
                    
                    if ( logger.isDebugEnabled())
                        logger.debug("Delete on close - access denied, " + file.getFullName());
                    
                    // Convert to a filesystem access denied exception
                    
                    throw new AccessDeniedException("Delete on close " + file.getFullName());
                }
            }
        }
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Closed file: \n" +
                    "   network file: " + file + "\n" +
                    "   deleted on close: " + file.hasDeleteOnClose());
        }
    }

    public void deleteFile(SrvSession sess, TreeConnection tree, String name) throws IOException
    {
        // get the device root
        NodeRef deviceRootNodeRef = getDeviceRootNode(tree);
        
        try
        {
            // get the node
            NodeRef nodeRef = cifsHelper.getNodeRef(deviceRootNodeRef, name);
            if (nodeService.exists(nodeRef))
            {
                nodeService.deleteNode(nodeRef);
            }
            // done
            if (logger.isDebugEnabled())
            {
                logger.debug("Deleted file: \n" +
                        "   file: " + name + "\n" +
                        "   node: " + nodeRef);
            }
        }
        catch (FileNotFoundException e)
        {
            // already gone
            if (logger.isDebugEnabled())
            {
                logger.debug("Deleted file <alfready gone>: \n" +
                        "   file: " + name);
            }
        }
        catch (org.alfresco.repo.security.permissions.AccessDeniedException ex)
        {
            // Debug
            
            if ( logger.isDebugEnabled())
                logger.debug("Delete file - access denied");
            
            // Convert to a filesystem access denied status
            
            throw new AccessDeniedException("Delete " + name);
        }
        catch (AlfrescoRuntimeException ex)
        {
            // Debug
            
            if ( logger.isDebugEnabled())
                logger.debug("Delete file error", ex);
            
            // Convert to a general I/O exception
            
            throw new IOException("Delete file " + name);
        }
    }

    /**
     * This is used by both rename and move
     */
    public void renameFile(SrvSession sess, TreeConnection tree, String oldName, String newName) throws IOException
    {
        try
        {
            // get the device root
            NodeRef deviceRootNodeRef = getDeviceRootNode(tree);
            
            // get the file/folder to move
            NodeRef nodeToMoveRef = cifsHelper.getNodeRef(deviceRootNodeRef, oldName);
            ChildAssociationRef nodeToMoveAssoc = nodeService.getPrimaryParent(nodeToMoveRef);
            
            // get the new target folder - it must be a folder
            String[] splitPaths = FileName.splitPath(newName);
            NodeRef targetFolderRef = cifsHelper.getNodeRef(deviceRootNodeRef, splitPaths[0]);
            if (!cifsHelper.isDirectory(targetFolderRef))
            {
                throw new AlfrescoRuntimeException("Cannot move not into anything but a folder: \n" +
                        "   device root: " + deviceRootNodeRef + "\n" +
                        "   old path: " + oldName + "\n" +
                        "   new path: " + newName);
            }
            
            // we escape the local name of the path so that it conforms to the general standard of being
            // an escaped version of the name property
            QName newAssocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName(splitPaths[1]));
            
            // move it
            nodeService.moveNode(nodeToMoveRef, targetFolderRef, nodeToMoveAssoc.getTypeQName(), newAssocQName);
            
            // set the properties
            Map<QName, Serializable> properties = nodeService.getProperties(nodeToMoveRef);
            properties.put(ContentModel.PROP_NAME, splitPaths[1]);
            if (!cifsHelper.isDirectory(nodeToMoveRef))
            {
                // reguess the mimetype in case the extension has changed
                String mimetype = mimetypeService.guessMimetype(splitPaths[1]);
                // get the current content properties
                ContentData contentData = (ContentData) properties.get(ContentModel.PROP_CONTENT);
                if (contentData == null)
                {
                    contentData = new ContentData(
                            null,
                            mimetype,
                            0L,
                            "UTF-8");
                }
                else
                {
                    contentData = new ContentData(
                            contentData.getContentUrl(),
                            mimetype,
                            contentData.getSize(),
                            contentData.getEncoding());
                }
                properties.put(ContentModel.PROP_CONTENT, contentData);
            }
            nodeService.setProperties(nodeToMoveRef, properties);
            
            // done
            if (logger.isDebugEnabled())
            {
                logger.debug("Moved node: \n" +
                        "   from: " + oldName + "\n" +
                        "   to: " + newName);
            }
        }
        catch (org.alfresco.repo.security.permissions.AccessDeniedException ex)
        {
            // Debug
            
            if ( logger.isDebugEnabled())
                logger.debug("Rename file - access denied, " + oldName);
            
            // Convert to a filesystem access denied status
            
            throw new AccessDeniedException("Rename file " + oldName);
        }
        catch (AlfrescoRuntimeException ex)
        {
            // Debug
            
            if ( logger.isDebugEnabled())
                logger.debug("Rename file", ex);
            
            // Convert to a general I/O exception
            
            throw new IOException("Rename file " + oldName);
        }
    }

    /**
     * Set file information
     * 
     * @param sess SrvSession
     * @param tree TreeConnection
     * @param name String
     * @param info FileInfo
     * @exception IOException
     */
    public void setFileInformation(SrvSession sess, TreeConnection tree, String name, FileInfo info) throws IOException
    {
        try
        {
            // Get the device root
            
            NodeRef deviceRootNodeRef = getDeviceRootNode(tree);
    
            // Get the file/folder node
            
            NodeRef nodeRef = cifsHelper.getNodeRef(deviceRootNodeRef, name);
            
            // Check permissions on the file/folder node
            
            if ( permissionService.hasPermission(nodeRef, PermissionService.WRITE) == AccessStatus.DENIED)
                throw new AccessDeniedException("No write access to " + name);
        }
        catch (org.alfresco.repo.security.permissions.AccessDeniedException ex)
        {
            // Debug
            
            if ( logger.isDebugEnabled())
                logger.debug("Set file information - access denied, " + name);
            
            // Convert to a filesystem access denied status
            
            throw new AccessDeniedException("Set file information " + name);
        }
        catch (AlfrescoRuntimeException ex)
        {
            // Debug
            
            if ( logger.isDebugEnabled())
                logger.debug("Open file error", ex);
            
            // Convert to a general I/O exception
            
            throw new IOException("Set file information " + name);
        }
    }

    /**
     * Called once the size of the incoming file data is known
     */
    public void truncateFile(SrvSession sess, TreeConnection tree, NetworkFile file, long size) throws IOException
    {
        file.truncateFile(size);
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Truncated file: \n" +
                    "   network file: " + file + "\n" +
                    "   size: " + size);
        }
    }

    /**
     * Defers to the network file
     */
    public int readFile(
            SrvSession sess, TreeConnection tree, NetworkFile file,
            byte[] buffer, int bufferPosition, int size, long fileOffset) throws IOException
    {
        // Check if the file is a directory
        
        if(file.isDirectory())
            throw new AccessDeniedException();
            
        int count = file.readFile(buffer, size, bufferPosition, fileOffset);
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Read bytes from file: \n" +
                    "   network file: " + file + "\n" +
                    "   buffer size: " + buffer.length + "\n" +
                    "   buffer pos: " + bufferPosition + "\n" +
                    "   size: " + size + "\n" +
                    "   file offset: " + fileOffset + "\n" +
                    "   bytes read: " + count);
        }
        return count;
    }

    public long seekFile(SrvSession sess, TreeConnection tree, NetworkFile file, long pos, int typ) throws IOException
    {
        throw new UnsupportedOperationException("Unsupported: " + file);
    }

    /**
     * Called to transfer data to the underlying content
     */
    public int writeFile(SrvSession sess, TreeConnection tree, NetworkFile file,
            byte[] buffer, int bufferOffset, int size, long fileOffset) throws IOException
    {
        file.writeFile(buffer, size, bufferOffset, fileOffset);
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Wrote bytes to file: \n" +
                    "   network file: " + file + "\n" +
                    "   buffer size: " + buffer.length + "\n" +
                    "   size: " + size + "\n" +
                    "   file offset: " + fileOffset);
        }
        return size;
    }

    /**
     * NOOP
     */
    public void treeClosed(SrvSession sess, TreeConnection tree)
    {
    }

    /**
     * NOOP
     */
    public void treeOpened(SrvSession sess, TreeConnection tree)
    {
    }

}

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
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.alfresco.config.ConfigElement;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.filesys.server.SrvSession;
import org.alfresco.filesys.server.core.DeviceContext;
import org.alfresco.filesys.server.core.DeviceContextException;
import org.alfresco.filesys.server.filesys.DiskDeviceContext;
import org.alfresco.filesys.server.filesys.DiskInterface;
import org.alfresco.filesys.server.filesys.FileAttribute;
import org.alfresco.filesys.server.filesys.FileInfo;
import org.alfresco.filesys.server.filesys.FileName;
import org.alfresco.filesys.server.filesys.FileOpenParams;
import org.alfresco.filesys.server.filesys.FileStatus;
import org.alfresco.filesys.server.filesys.FileSystem;
import org.alfresco.filesys.server.filesys.NetworkFile;
import org.alfresco.filesys.server.filesys.SearchContext;
import org.alfresco.filesys.server.filesys.TreeConnection;
import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.repository.datatype.ValueConverter;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * First-pass implementation to enable SMB support in the repo.
 * 
 * @author Derek Hulley
 */
public class ContentDiskDriver implements DiskInterface
{
    private static final String KEY_STORE = "store";
    private static final String KEY_ROOT_PATH = "rootPath";
    
    private static final Log logger = LogFactory.getLog(ContentDiskDriver.class);
    
    private ServiceRegistry serviceRegistry;
    private NamespaceService namespaceService;
    private DictionaryService dictionaryService;
    private NodeService nodeService;
    private ContentService contentService;
    private MimetypeService mimetypeService;

    /**
     * @param serviceRegistry to connect to the repository services
     */
    public ContentDiskDriver(ServiceRegistry serviceRegistry)
    {
        this.serviceRegistry = serviceRegistry;
    }
    
    /**
     * @param element the configuration element from which to configure the class
     */
    public DeviceContext createContext(ConfigElement cfg) throws DeviceContextException
    {
        /*
         * This method is bloated - break it up and share load with tree opened and other
         * helper methods
         */
        
        // connect to the repository
        namespaceService = serviceRegistry.getNamespaceService();
        dictionaryService = serviceRegistry.getDictionaryService();
        nodeService = serviceRegistry.getNodeService();
        contentService = serviceRegistry.getContentService();
        mimetypeService = serviceRegistry.getMimetypeService();
        
        // get the store
        ConfigElement storeElement = cfg.getChild(KEY_STORE);
        if (storeElement == null || storeElement.getValue() == null || storeElement.getValue().length() == 0)
        {
            throw new DeviceContextException("Device missing init value: " + KEY_STORE);
        }
        String storeValue = storeElement.getValue();
        StoreRef storeRef = new StoreRef(storeValue);
        
        // connect to the repo and ensure that the store exists
        if (!nodeService.exists(storeRef))
        {
            // no such store - create it
            nodeService.createStore(storeRef.getProtocol(), storeRef.getIdentifier());
        }
        NodeRef storeRootNodeRef = nodeService.getRootNode(storeRef);
        
        // get the root path
        ConfigElement rootPathElement = cfg.getChild(KEY_ROOT_PATH);
        if (rootPathElement == null || rootPathElement.getValue() == null || rootPathElement.getValue().length() == 0)
        {
            throw new DeviceContextException("Device missing init value: " + KEY_ROOT_PATH);
        }
        String rootPath = rootPathElement.getValue();
        // find the root node for this device
        List<NodeRef> nodeRefs = nodeService.selectNodes(
                storeRootNodeRef, rootPath, null, namespaceService, false);
        NodeRef rootNodeRef = null;
        if (nodeRefs.size() > 1)
        {
            throw new DeviceContextException("Multiple possible roots for device: \n" +
                    "   root path: " + rootPath + "\n" +
                    "   results: " + nodeRefs);
        }
        else if (nodeRefs.size() == 0)
        {
            // nothing found - create the path
            StringTokenizer tokenizer = new StringTokenizer(rootPath, "/", false);
            if (tokenizer.countTokens() == 0)
            {
                throw new DeviceContextException("Store root node may not be used as the device root: \n" +
                        "   root path: " + rootPath);
            }
            Map<QName, Serializable> properties = new HashMap<QName, Serializable>(5);
            while (tokenizer.hasMoreTokens())  // we have checked: There is at least one
            {
                String pathElement = tokenizer.nextToken();
                properties.put(ContentModel.PROP_NAME, pathElement);
                // create a node with both this path and this
                ChildAssociationRef assocRef = null;
                if (rootNodeRef == null)
                {
                    // use the root node as a starting point
                    assocRef = nodeService.createNode(
                            storeRootNodeRef,
                            ContentModel.ASSOC_CHILDREN,  // child of store root
                            QName.createQName(pathElement, namespaceService),
                            ContentModel.TYPE_FOLDER,
                            properties);
                }
                else
                {
                    // we have already created a folder
                    assocRef = nodeService.createNode(
                            rootNodeRef,
                            ContentModel.ASSOC_CONTAINS,  // child of folder
                            QName.createQName(pathElement, namespaceService),
                            ContentModel.TYPE_FOLDER,
                            properties);
                }
                // use the new node as a root
                rootNodeRef = assocRef.getChildRef();
            }
        }
        else
        {
            // we found a node
            rootNodeRef = nodeRefs.get(0);
        }
        
        // create the context
        DiskDeviceContext context = new DiskDeviceContext(rootNodeRef.toString());
        
        // set parameters
        context.setFilesystemAttributes(FileSystem.CasePreservedNames);
        
        // done
        return context;
    }

    /**
     * Always writable
     */
    public boolean isReadOnly(SrvSession sess, DeviceContext ctx) throws IOException
    {
        return false;
    }
    
    /**
     * Helper method to extract file info from a node
     * 
     * @param serviceRegistry used to access the repository
     * @param nodeRef the file/folder node
     * @param includeName if the name property is to be carried into the filesystem
     * @return Returns the file information pertinent to the node
     */
    public static FileInfo getFileInformation(
            ServiceRegistry serviceRegistry,
            NodeRef nodeRef,
            boolean includeName)
    {
        // get required serviceRegistry
        NodeService nodeService = serviceRegistry.getNodeService();
        DictionaryService dictionaryService = serviceRegistry.getDictionaryService();

        // retrieve required properties and create file info
        Map<QName, Serializable> nodeProperties = nodeService.getProperties(nodeRef);
        FileInfo fileInfo = new FileInfo();
        
        // unset all attribute flags
        int fileAttributes = 0;
        fileInfo.setFileAttributes(fileAttributes);
        
        if (ContentDiskDriver.isDirectory(serviceRegistry, nodeRef))
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
        
        // check for existence
        if (!nodeService.exists(deviceRootNodeRef))
        {
            throw new AlfrescoRuntimeException("Device root node does not exist: " + deviceRootNodeRef);
        }
        // done
        return deviceRootNodeRef;
    }
    
    /**
     * Device method
     * 
     * @see ContentDiskDriver#getFileInformation(ServiceRegistry, NodeRef, boolean)
     */
    public FileInfo getFileInformation(SrvSession session, TreeConnection tree, String path) throws IOException
    {
        // get the device root
        NodeRef deviceRootNodeRef = getDeviceRootNode(tree);
        
        // attempt to get the file/folder node
        NodeRef nodeRef = CifsHelper.getNodeRef(
                serviceRegistry,
                deviceRootNodeRef,
                path);
        
        try
        {
            boolean includeName = (path.length() > 0);
            return ContentDiskDriver.getFileInformation(serviceRegistry, nodeRef, includeName);
        }
        catch (Throwable e)
        {
            throw new AlfrescoRuntimeException("Failed to get file information: \n" +
                    "   device: " + tree.getContext().getDeviceName() + "\n" +
                    "   path: " + path,
                    e);
        }
    }

    public SearchContext startSearch(SrvSession sess, TreeConnection tree, String searchPath, int attrib) throws FileNotFoundException
    {
        // get the device root
        NodeRef deviceRootNodeRef = getDeviceRootNode(tree);
        
        SearchContext ctx = ContentSearchContext.search(serviceRegistry, deviceRootNodeRef, searchPath, attrib);
        return ctx;
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
            throw new AlfrescoRuntimeException("Failed to check for existence: " +
                    "   device: " + tree.getContext().getDeviceName() + "\n" +
                    "   name: " + name,
                    e);
        }
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("File status determinded: \n" +
                    "   device: " + tree.getContext().getDeviceName() + "\n" +
                    "   name: " + name + "\n" +
                    "   status: " + status);
        }
        return status;
    }
    
    /**
     * @param serviceRegistry for repo connection
     * @param nodeRef
     * @return Returns true if the node is a subtype of {@link ContentModel#TYPE_FOLDER folder}
     * @throws AlfrescoRuntimeException if the type is neither related to a folder or content
     */
    public static boolean isDirectory(ServiceRegistry serviceRegistry, NodeRef nodeRef)
    {
        DictionaryService dictionaryService = serviceRegistry.getDictionaryService();
        NodeService nodeService = serviceRegistry.getNodeService();
        
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
            // we pretend that it is a directory
            return true;   
        }
    }

    public NetworkFile openFile(SrvSession sess, TreeConnection tree, FileOpenParams params) throws IOException
    {
        // get the device root
        NodeRef deviceRootNodeRef = getDeviceRootNode(tree);
        
        String path = params.getPath(); 
//        boolean isFile = !params.isDirectory();

        // get the file info
        NodeRef nodeRef = CifsHelper.getNodeRef(
                serviceRegistry,
                deviceRootNodeRef,
                path);
        
//        // check that we have the correct type
//        if (isFile == ContentDiskDriver.isDirectory(serviceRegistry, nodeRef))
//        {
//            // we have a directory when the open is for a file
//            throw new FileNotFoundException("Found file or folder, but not of correct type: \n" +
//                    "   device root: " + deviceRootNodeRef + "\n" +
//                    "   opening file: " + isFile + "\n" +
//                    "   found file: " + !isFile + "\n" +
//                    "   node: " + nodeRef);
//        }
        
        NetworkFile netFile = ContentNetworkFile.createFile(serviceRegistry, nodeRef, params);
        return netFile;
    }
    
    /**
     * @see #createNode(NodeRef, String, String)
     */
    public NetworkFile createFile(SrvSession sess, TreeConnection tree, FileOpenParams params) throws IOException
    {
        // get the device root
        NodeRef deviceRootNodeRef = getDeviceRootNode(tree);
        
        String path = params.getPath(); 
        boolean isFile = !params.isDirectory();
        
        // create it - the path will be created, if necessary
        NodeRef nodeRef = CifsHelper.createNode(serviceRegistry, deviceRootNodeRef, path, isFile);
        
        // create the network file
        NetworkFile netFile = ContentNetworkFile.createFile(serviceRegistry, nodeRef, params);
        // done
        return netFile;
    }

    public void createDirectory(SrvSession sess, TreeConnection tree, FileOpenParams params) throws IOException
    {
        // get the device root
        NodeRef deviceRootNodeRef = getDeviceRootNode(tree);
        
        String path = params.getPath(); 
        boolean isFile = !params.isDirectory();
        
        // create it - the path will be created, if necessary
        NodeRef nodeRef = CifsHelper.createNode(serviceRegistry, deviceRootNodeRef, path, isFile);
    }

    public void deleteDirectory(SrvSession sess, TreeConnection tree, String dir) throws IOException
    {
        // get the device root
        NodeRef deviceRootNodeRef = getDeviceRootNode(tree);
        
        try
        {
            // get the node
            NodeRef nodeRef = CifsHelper.getNodeRef(serviceRegistry, deviceRootNodeRef, dir);
            if (nodeService.exists(nodeRef))
            {
                nodeService.deleteNode(nodeRef);
            }
        }
        catch (FileNotFoundException e)
        {
            // already gone
        }
    }

    public void flushFile(SrvSession sess, TreeConnection tree, NetworkFile file) throws IOException
    {
        throw new UnsupportedOperationException("Unsupported: " + file);
    }

    public void closeFile(SrvSession sess, TreeConnection tree, NetworkFile file) throws IOException
    {
        // defer to the network file to close the stream and remove the content
        file.closeFile();
        // remove the node if necessary
        if (file.hasDeleteOnClose())
        {
            ContentNetworkFile contentNetFile = (ContentNetworkFile) file;
            NodeRef nodeRef = contentNetFile.getNodeRef();
            // we don't know how long the network file has had the reference, so check for existence
            if (nodeService.exists(nodeRef))
            {
                nodeService.deleteNode(nodeRef);
            }
        }
    }

    public void deleteFile(SrvSession sess, TreeConnection tree, String name) throws IOException
    {
        // get the device root
        NodeRef deviceRootNodeRef = getDeviceRootNode(tree);
        
        try
        {
            // get the node
            NodeRef nodeRef = CifsHelper.getNodeRef(serviceRegistry, deviceRootNodeRef, name);
            if (nodeService.exists(nodeRef))
            {
                nodeService.deleteNode(nodeRef);
            }
        }
        catch (FileNotFoundException e)
        {
            // already gone
        }
    }

    /**
     * This is used by both rename and move
     */
    public void renameFile(SrvSession sess, TreeConnection tree, String oldName, String newName) throws IOException
    {
        // get the device root
        NodeRef deviceRootNodeRef = getDeviceRootNode(tree);
        
        // get the file/folder to move
        NodeRef nodeToMoveRef = CifsHelper.getNodeRef(serviceRegistry, deviceRootNodeRef, oldName);
        ChildAssociationRef nodeToMoveAssoc = nodeService.getPrimaryParent(nodeToMoveRef);
        
        // get the new target folder - it must be a folder
        String[] splitPaths = FileName.splitPath(newName);
        NodeRef targetFolderRef = CifsHelper.getNodeRef(serviceRegistry, deviceRootNodeRef, splitPaths[0]);
        if (!ContentDiskDriver.isDirectory(serviceRegistry, targetFolderRef))
        {
            throw new AlfrescoRuntimeException("Cannot move not into anything but a folder: \n" +
                    "   device root: " + deviceRootNodeRef + "\n" +
                    "   old path: " + oldName + "\n" +
                    "   new path: " + newName);
        }
        
        // we escape the local name of the path so that it conforms to the general standard of being
        // an escaped version of the name property
        QName newAssocQName = QName.createQName(NamespaceService.ALFRESCO_URI, QName.createValidLocalName(splitPaths[1]));
        
        // move it
        nodeService.moveNode(nodeToMoveRef, targetFolderRef, nodeToMoveAssoc.getTypeQName(), newAssocQName);
        
        // set the properties
        Map<QName, Serializable> properties = nodeService.getProperties(nodeToMoveRef);
        properties.put(ContentModel.PROP_NAME, splitPaths[1]);
        if (!ContentDiskDriver.isDirectory(serviceRegistry, nodeToMoveRef))
        {
            // reguess the mimetype in case the extension has changed
            properties.put(ContentModel.PROP_MIME_TYPE, mimetypeService.guessMimetype(splitPaths[1]));
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

    public void setFileInformation(SrvSession sess, TreeConnection tree, String name, FileInfo info) throws IOException
    {
        // this will be updated automatically by the server
    }

    /**
     * Called once the size of the incoming file data is known
     */
    public void truncateFile(SrvSession sess, TreeConnection tree, NetworkFile file, long size) throws IOException
    {
        file.truncateFile(size);
    }

    /**
     * Defers to the network file
     */
    public int readFile(
            SrvSession sess, TreeConnection tree, NetworkFile file,
            byte[] buffer, int bufferPosition, int size, long fileOffset) throws IOException
    {
        return file.readFile(buffer, size, bufferPosition, fileOffset);
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

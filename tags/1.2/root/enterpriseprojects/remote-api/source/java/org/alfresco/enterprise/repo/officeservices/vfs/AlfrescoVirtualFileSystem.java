/*
 * Copyright 2005-2015 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.officeservices.vfs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.UserTransaction;

import net.sf.acegisecurity.Authentication;

import org.alfresco.enterprise.repo.officeservices.metadata.DataModelMappingConfiguration;
import org.alfresco.enterprise.repo.officeservices.metadata.IOContentFilterRegistry;
import org.alfresco.enterprise.repo.officeservices.service.Const;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.cache.SimpleCache;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.security.authentication.AuthenticationContext;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.webdav.WebDAVActivityPoster;
import org.alfresco.repo.webdav.WebDAVHelper;
import org.alfresco.repo.webdav.WebDAVServlet.WebDAVInitParameters;
import org.alfresco.service.cmr.coci.CheckOutCheckInService;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.lock.LockService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.cmr.version.VersionHistory;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.Pair;
import org.alfresco.util.PropertyCheck;
import org.alfresco.util.TempFileProvider;
import org.apache.chemistry.opencmis.server.shared.ThresholdOutputStreamFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import com.xaldon.officeservices.UserData;
import com.xaldon.officeservices.datamodel.Guid;
import com.xaldon.officeservices.exceptions.AuthenticationRequiredException;
import com.xaldon.officeservices.protocol.VermeerRequest;
import com.xaldon.officeservices.vfs.VFSDocumentNode;
import com.xaldon.officeservices.vfs.VFSNode;
import com.xaldon.officeservices.vfs.VirtualFileSystem;

/**
 * <p>Implementation of the <code>{@link VirtualFileSystem}</code> interface of the <i>Alfresco
 * Office Services (AOS)</i> library as a bean.</p>
 * 
 * <p>This class is the central entry point for all requests coming in through the FrontPage Server
 * Extension protocol (FPSE), WebDAV, the URL protocol or any other SharePoint protocol.<br/>
 * The purpose of this class is to either resolve a given path to a <code>{@link VFSNode}</code>
 * object or to create a new file or folder and return the <code>{@link VFSNode}</code> object
 * of this new element.</p>
 * 
 * <p>Implementation details:<br/>
 * The <code>{@link VirtualFileSystem}</code> is implemented as a bean by this class, that is
 * constructed once per <code>WebApplicationContext</code>. Thus it  has to be thread-safe by
 * either being re-entrant or blocking. All required Alfresco Services are set on this bean
 * during construction. All other classes in the VFS delegate to this class to get a service
 * implementation and do not resolve services on their own.<br/>
 * This implementation makes use of the <code>{@link WebDAVHelper}</code> to resolve or
 * create a path in the repository. We use the same path layout that we have in WebDAV
 * also in this VFS exposed through AOS. The <code>{@link #convertFileInfo(FileInfo fileInfo, String path)}</code>
 * method wraps a <code>{@link FileInfo}</code> object in either a <code>{@link DocumentNode}</code>
 * or a <code>{@link FolderNode}</code> depending on its type. This method is the one-stop
 * shop to convert Alfresco Repository objects into AOS VFS objects and should be used throughout
 * this VFS implementation.</p>
 * 
 * <p>VFS layout:<br/>
 * In the file system exposed through AOS, the root of this VFS is located at <code>/alfresco/aos</code>.
 * So a URL <code>http://example.com/alfresco/aos/foo/bar/example.docx</code> will be converted to
 * the path <code>/foo/bar/example.docx</code> in this VFS. This VFS uses the same layout of
 * the file system as it is exposed through WebDAV.</p>
 * 
 * <p>Authentication:<br/>
 * Authentication is realized by the <code>AosAuthenticationFilter</code> servlet filter bean that
 * sets the credentials of the current user in the AuthenticationService.
 * 
 * @since 5.0
 * 
 * @author Stefan Kopf
 *
 */
public class AlfrescoVirtualFileSystem implements InitializingBean, VirtualFileSystem
{

    public static final QName ASPECT_AOS_CHECKED_OUT_TO_LOCAL = QName.createQName("http://www.alfresco.org/model/aos/1.0", "checkedOutToLocal");

    public static final Guid LISTID_ROOT_DOCUMENTS = Guid.parse("{00000000-0000-0000-0000-000000000000}");

    private static final String KEY_ROOT_NODEREF = "AlfrescoVirtualFileSystem.root_noderef";
    
    // Services
    private AuthenticationService authenticationService;
    private NodeService nodeService;
    private FileFolderService fileFolderService;
    private CheckOutCheckInService checkOutCheckInService;
    private LockService lockService;
    private VersionService versionService;
    private DictionaryService dictionaryService;
    private NamespaceService namespaceService;
    private MimetypeService mimetypeService;
    private SearchService searchService;
    private BehaviourFilter policyBehaviourFilter;
    private TransactionService transactionService;
    private TenantService tenantService;
    private SiteService siteService;
    private WebDAVHelper webDavHelper;
    private WebDAVInitParameters webDavInitParams;
    private AuthenticationContext authenticationContext;
    private WebDAVActivityPoster activityPoster;
    private DataModelMappingConfiguration dataModelMappingConfiguration;
    private IOContentFilterRegistry ioContentFilterRegistry;
    private SimpleCache<String, NodeRef> singletonCache;
    private String baseUrlOverwrite;
    private String sitePathOverwrite;
    private String tempDirectoryName = null;
    private boolean encryptTempFiles = false;
    private int tempFileThreshold = 4 * 1024 * 1024; // 4mb
    private long tempFileMaxContentSize = (long) 4 * 1024 * 1024 * 1024; // 4gb
    
    protected Logger logger = Logger.getLogger(this.getClass());

    protected NodeRef defaultRootNodeRef;
    
    private Object defaultRootNodeRefLock = new Object();
    
    public AlfrescoVirtualFileSystem()
    {
        // empty default constructor
    }

    //
    // ----- Initialization
    //

    @Override
    public void afterPropertiesSet() throws Exception
    {
        PropertyCheck.mandatory(this, "authenticationService", this.authenticationService);
        PropertyCheck.mandatory(this, "nodeService", this.nodeService);
        PropertyCheck.mandatory(this, "fileFolderService", this.fileFolderService);
        PropertyCheck.mandatory(this, "checkOutCheckInService", this.checkOutCheckInService);
        PropertyCheck.mandatory(this, "lockService", this.lockService);
        PropertyCheck.mandatory(this, "versionService", this.versionService);
        PropertyCheck.mandatory(this, "dictionaryService", this.dictionaryService);
        PropertyCheck.mandatory(this, "namespaceService", this.namespaceService);
        PropertyCheck.mandatory(this, "mimetypeService", this.mimetypeService);
        PropertyCheck.mandatory(this, "searchService", this.searchService);
        PropertyCheck.mandatory(this, "policyBehaviourFilter", this.policyBehaviourFilter);
        PropertyCheck.mandatory(this, "transactionService", this.transactionService);
        PropertyCheck.mandatory(this, "tenantService", this.tenantService);
        PropertyCheck.mandatory(this, "siteService", this.siteService);
        PropertyCheck.mandatory(this, "webDavHelper", this.webDavHelper);
        PropertyCheck.mandatory(this, "webDavInitParams", this.webDavInitParams);
        PropertyCheck.mandatory(this, "authenticationContext", this.authenticationContext);
        PropertyCheck.mandatory(this, "activityPoster", this.activityPoster);
        PropertyCheck.mandatory(this, "dataModelMappingConfiguration", this.dataModelMappingConfiguration);
        PropertyCheck.mandatory(this, "singletonCache", this.singletonCache);
        PropertyCheck.mandatory(this, "tempDirectoryName", this.tempDirectoryName);
    }

    //
    // ----- root node
    //
    
    public void prepare() throws ServletException
    {
    	if(this.defaultRootNodeRef == null)
    	{
    		try
    		{
        		this.getDefaultRootNode();    			
    		}
    		catch(Exception e)
    		{
    			throw new ServletException("Error initializing default root node.",e);
    		}
    	}
    }

    protected NodeRef getDefaultRootNode() throws Exception
    {
        
        if(defaultRootNodeRef != null)
        {
            return defaultRootNodeRef;
        }
        
        synchronized(defaultRootNodeRefLock)
        {
            if(defaultRootNodeRef != null)
            {
                return defaultRootNodeRef;
            }

            String storeValue = webDavInitParams.getStoreName();
            String rootPath = webDavInitParams.getRootPath();

            // Use the system user as the authenticated context for the filesystem initialization
            Authentication currentUser = authenticationContext.getCurrentAuthentication();
            authenticationContext.setSystemUserAsCurrentUser();
            

            // Wrap the initialization in a transaction
            UserTransaction tx = transactionService.getUserTransaction(true);
            try
            {
                // Start the transaction

                if (tx != null)
                    tx.begin();
                
                StoreRef storeRef = new StoreRef(storeValue);
                
                if (nodeService.exists(storeRef) == false)
                {
                    throw new RuntimeException("No store for path: " + storeRef);
                }
                
                NodeRef storeRootNodeRef = nodeService.getRootNode(storeRef);
                
                List<NodeRef> nodeRefs = searchService.selectNodes(storeRootNodeRef, rootPath, null, namespaceService, false);
                
                if (nodeRefs.size() > 1)
                {
                    throw new RuntimeException("Multiple possible children for : \n" + "   path: " + rootPath + "\n" + "   results: " + nodeRefs);
                }
                else if (nodeRefs.size() == 0)
                {
                    throw new RuntimeException("Node is not found for : \n" + "   root path: " + rootPath);
                }
                
                defaultRootNodeRef = nodeRefs.get(0);
                
                // Commit the transaction
                if (tx != null)
                    tx.commit();
                
                return defaultRootNodeRef;
            }
            finally
            {
                // Clear the current system user

                authenticationContext.clearCurrentSecurityContext();
                authenticationContext.setCurrentAuthentication(currentUser);
            }
            
        }
    }
    
    protected NodeRef getRootNodeRef() throws Exception
    {
        NodeRef rootNodeRef = singletonCache.get(KEY_ROOT_NODEREF);
        
        if (rootNodeRef == null)
        {
            rootNodeRef = tenantService.getRootNode(nodeService, searchService, namespaceService, webDavInitParams.getRootPath(), getDefaultRootNode());
            singletonCache.put(KEY_ROOT_NODEREF, rootNodeRef);
        }
        
        return rootNodeRef;
    }

    //
    // ----- VFS implementation
    //
    
    /**
     * Returns the normalized form of the given path.<br/>
     * A normalized path complies with these rules:
     * <ul>
     *   <li>The path uses only forward slash characters as path-element separator
     *   <li>A path always starts with a slash
     *   <li>A path never ends with a slash unless it consists only of a slash
     * </ul>
     * 
     * @param path the path to be normalized
     * 
     * @return the normalized form of the given path
     */
    public static String normalizePath(String path)
    {
        path = path.replace('\\', '/');
        if(path.length() == 0)
        {
            path = "/";
        }
        else
        {
            if(path.length() > 1)
            {
                if(!path.startsWith("/"))
                {
                    path = "/" + path;
                }
                if(path.endsWith("/"))
                {
                    path = path.substring(0, path.length()-1);
                }
            }
        }
        return path;
    }
    
    public static String getFirstFolder(String normalizedPath)
    {
        int nextSeparator = normalizedPath.indexOf('/', 1);
        if(nextSeparator < 0)
        {
            return null;
        }
        return normalizedPath.substring(0, nextSeparator);
    }
    
    private String HISTORY_PATH_PREFIX = Const.HISTORY_PATH_ELEMENT + "/";
    
    private String NODEID_PATH_PREFIX = Const.NODEID_PATH_ELEMENT + "/";
    
    /**
     * Returns the <code>{@link VFSNode}</code> representing the element in this virtual file system
     * identified by the given path or <code>null</code> if no such element exists.<br/>
     * The the file system layout description in the javadoc of this class for details.
     * 
     * @param userData the <code>{@link UserData}</code> object describing the user that made the request to this service
     * @param path the path from the file system root to a node
     * @param callContext the context this method is called in. One of the <code>VFSNode.CALLCONTEXT_</code> statics.
     * 
     * @return the <code>{@link VFSNode}</code> representing the element in this virtual file system
     *     identified by the given path or <code>null</code> if no such element exists
     * 
     * @throws AuthenticationRequiredException if the credentials stored in the <code>{@link UserData}</code> can not be authenticated (this will not happen in this implementation)
     */
    @Override
    public VFSNode getNodeByPath(UserData userData, String path, int callContext) throws AuthenticationRequiredException
    {
        path = normalizePath(path);
        
        if(path.startsWith(NODEID_PATH_PREFIX))
        {
            return getNodeByNodeidPath(path);
        }
        
        if(path.startsWith(HISTORY_PATH_PREFIX))
        {
            return getNodeByHistoryPath(path);
        }
        
        FileInfo fileInfo = getFileInfoByRepositoryPath(path);
        if(fileInfo == null)
        {
            return null;
        }

        return convertFileInfo(fileInfo, path);
    }
    
    protected VFSNode getNodeByNodeidPath(String path)
    {
        FileInfo fileInfo = getFileInfoByNodeidPath(path);
        if(fileInfo == null)
        {
            return null;
        }
        return convertFileInfo(fileInfo, path);
    }

    protected VFSNode getNodeByHistoryPath(String path)
    {
        String remaining = path.substring(HISTORY_PATH_PREFIX.length());
        int firstSeparator = remaining.indexOf('/');
        if(firstSeparator < 1)
        {
            return null;
        }
        String versionLabel = remaining.substring(0, firstSeparator);
        String originalPath = remaining.substring(firstSeparator);

        FileInfo liveNode = originalPath.startsWith(NODEID_PATH_PREFIX) ? getFileInfoByNodeidPath(originalPath) : getFileInfoByRepositoryPath(originalPath);
        if(liveNode == null)
        {
            return null;
        }
        if(liveNode.isFolder())
        {
            return null;
        }
        
        VersionHistory versionHistory = getVersionService().getVersionHistory(liveNode.getNodeRef());
        if(versionHistory == null)
        {
            if(liveNode.isLink())
            {
                return null;
            }
            return new DocumentNode(liveNode,path,this,true);
        }
        else
        {
            for (Version version : versionHistory.getAllVersions())
            {
                if(versionLabel.equals(version.getVersionLabel()))
                {
                    FileInfo frozen = getFileFolderService().getFileInfo(version.getFrozenStateNodeRef());
                    return new DocumentNode(frozen,path,this,true);
                }
            }
            return null;
        }
    }
    
    protected FileInfo getFileInfoByRepositoryPath(String path)
    {
        NodeRef rootNodeRef;
        try
        {
            rootNodeRef = getRootNodeRef();
        }
        catch(Exception e)
        {
            checkForRetryingException(e);
            logger.error("Error getting root node of VFS.",e);
            return null;
        }
        try
        {
            return webDavHelper.getNodeForPath(rootNodeRef, path);
        }
        catch (FileNotFoundException e)
        {
            return null;
        }
    }
    
    protected FileInfo getFileInfoByNodeidPath(String path)
    {
        String remaining = path.substring(NODEID_PATH_PREFIX.length());
        String[] pathElements = remaining.split("/");
        if( (pathElements.length != 2) || (pathElements[0].length() <= 0) || (pathElements[0].length() <= 1) )
        {
            return null;
        }
        NodeRef nodeRef = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, pathElements[0]);
        try
        {
            return fileFolderService.getFileInfo(nodeRef);
        }
        catch(InvalidNodeRefException inre)
        {
            return null;
        }
    }

    /**
     * Returns the <code>{@link VFSNode}</code> object representing the element described by the given
     * <code>{@link FileInfo}</code> object or <code>null</code> if the described element does not
     * have a representation in the VFS (e.g. a hidden element, a link or a working copy).
     * 
     * @param fileInfo the <code>{@link FileInfo}</code> describing the element to return a <code>{@link VFSNode}</code> for
     * @param path The path in this VFS to this VFS element
     * 
     * @return the <code>{@link VFSNode}</code> object representing the element described by the given <code>{@link FileInfo}</code> object
     */
    public VFSNode convertFileInfo(FileInfo fileInfo, String path)
    {
        // Links are not yet supported
        if(fileInfo.isLink())
        {
            return null;
        }
        
        // Hidden nodes do not exist
        if(fileInfo.isHidden())
        {
            return null;
        }
        
        // handle folders
        if(fileInfo.isFolder())
        {
            return factoryFolderNode(fileInfo,path);
        }
        
        // handle documents. sort out working copies
        if(getCheckOutCheckInService().isWorkingCopy(fileInfo.getNodeRef()))
        {
            return null;
        }
        return factoryDocumentNode(fileInfo,path);
    }
    
    protected VFSNode factoryFolderNode(FileInfo fileInfo, String path)
    {
        return new FolderNode(fileInfo,path,this);
    }
    
    protected VFSNode factoryDocumentNode(FileInfo fileInfo, String path)
    {
        return new DocumentNode(fileInfo,path,this);
    }
    
    protected String getTenantDomain()
    {
        String tenantDomain = tenantService.getCurrentUserDomain();
        if (tenantDomain == null)
        {
            return TenantService.DEFAULT_DOMAIN;
        }
        return tenantDomain;
    }

    /**
     * Create a new document at the given path and return the <code>VFSDocumentNode</code>
     * describing the new document or <code>null</code> if this operation failed.
     * 
     * @param userData the <code>{@link UserData}</code> object describing the user that made the request to this service
     * @param path the path from the file system root to the new document
     * @param request the <code>{@link VermeerRequest}</code> containing the octet stream of the new node
     * @param callContext the context this method is called in. One of the <code>CALLCONTEXT_</code> statics.
     * 
     * @return the <code>VFSDocumentNode</code> describing the new document or
     *         <code>null</code> if this operation failed
     * 
     * @throws AuthenticationRequiredException if the credentials stored in the <code>{@link UserData}</code> can not be authenticated (this will not happen in this implementation)
     */
    @Override
    public VFSDocumentNode createDocument(UserData userData, String path, VermeerRequest request, int callContext) throws AuthenticationRequiredException
    {
        return this.createDocument(userData, path, request.getAttachedFileInputStream(), callContext);
    }

    /**
     * Create a new file at the given path and return the <code>VFSDocumentNode</code>
     * describing the new document or <code>null</code> if this operation failed.
     * 
     * @param userData the <code>{@link UserData}</code> object describing the user that made the request to this service
     * @param path the path from the file system root to the new document
     * @param content the <code>InputStream</code> containing the octet stream with the new content
     * @param callContext the context this method is called in. One of the <code>CALLCONTEXT_</code> statics.
     * 
     * @return the <code>VFSDocumentNode</code> describing the new document or
     *         <code>null</code> if this operation failed
     * 
     * @throws AuthenticationRequiredException if the credentials stored in the <code>{@link UserData}</code> can not be authenticated (this will not happen in this implementation)
     */
    @Override
    public VFSDocumentNode createDocument(UserData userData, String path, InputStream content, int callContext) throws AuthenticationRequiredException
    {
        // fix path, if required
        path = normalizePath(path);
        // strip path in parent location and new document name
        String[] splitPath = webDavHelper.splitPath(path);
        // get root node
        NodeRef rootNodeRef;
        try
        {
            rootNodeRef = getRootNodeRef();
        }
        catch(Exception e)
        {
        	checkForRetryingException(e);
            logger.error("Error getting root node of VFS.",e);
            return null;
        }
        // find parent node
        FileInfo parentNodeInfo;
        try
        {
            parentNodeInfo = webDavHelper.getNodeForPath(rootNodeRef, splitPath[0]);
        }
        catch(Exception e)
        {
        	checkForRetryingException(e);
            return null;
        }
        // create new node
        FileInfo newNodeInfo;
        try
        {
            newNodeInfo = getFileFolderService().create(parentNodeInfo.getNodeRef(), splitPath[1], ContentModel.TYPE_CONTENT);
        }
        catch(Exception e)
        {
        	checkForRetryingException(e);
            return null;
        }
        // convert FileInfo
        VFSDocumentNode doc = (VFSDocumentNode) convertFileInfo(newNodeInfo, path);
        // detect if we have content
        PushbackInputStream internalContent = new PushbackInputStream(content, 1);
        boolean hasContent = false;
        try
        {
        	byte[] tempBuffer = new byte[1];
        	int bytesRead = internalContent.read(tempBuffer);
        	if(bytesRead == 1)
        	{
            	hasContent = true;
            	internalContent.unread(tempBuffer, 0, 1);
        	}
        }
        catch(IOException ioe)
        {
        	logger.error("Error accessing content stream of new document", ioe);
        	return doc;
        }
        // store content
        if(hasContent)
        {
            doc.storeContent(internalContent, callContext);
        }
        return doc;
    }

    /**
     * Create a new folder at the given path and return the the <code>VFSNode</code>
     * describing the new folder or <code>null</code> if this operation failed.
     * 
     * @param userData the <code>{@link UserData}</code> object describing the user that made the request to this service
     * @param path the path from the file system root to the new folder
     * @param callContext the context this method is called in. One of the <code>CALLCONTEXT_</code> statics.
     * 
     * @return the subnode describing the new folder or <code>null</code> if folder creation has failed
     * 
     * @throws AuthenticationRequiredException if the credentials stored in the <code>{@link UserData}</code> can not be authenticated (this will not happen in this implementation)
     */
    @Override
    public VFSNode createFolder(UserData userData, String path, int callContext) throws AuthenticationRequiredException
    {
        // fix path, if required
        path = normalizePath(path);
        // strip path in parent location and new document name
        String[] splitPath = webDavHelper.splitPath(path);
        // get root node
        NodeRef rootNodeRef;
        try
        {
            rootNodeRef = getRootNodeRef();
        }
        catch(Exception e)
        {
        	checkForRetryingException(e);
            logger.error("Error getting root node of VFS.",e);
            return null;
        }
        // find parent node
        FileInfo parentNodeInfo;
        try
        {
            parentNodeInfo = webDavHelper.getNodeForPath(rootNodeRef, splitPath[0]);
        }
        catch(Exception e)
        {
        	checkForRetryingException(e);
            return null;
        }
        // create new node
        FileInfo newNodeInfo;
        try
        {
            newNodeInfo = getFileFolderService().create(parentNodeInfo.getNodeRef(), splitPath[1], ContentModel.TYPE_FOLDER);
        }
        catch(Exception e)
        {
        	checkForRetryingException(e);
            return null;
        }
        // post activity
        try
        {
            Pair<String, String> activitySiteAndPath = getActivitySiteAndPath(newNodeInfo.getNodeRef());
            if(!WebDAVHelper.EMPTY_SITE_ID.equals(activitySiteAndPath.getFirst()))
            {
                activityPoster.postFileFolderAdded(activitySiteAndPath.getFirst(), getTenantDomain(), activitySiteAndPath.getSecond(), newNodeInfo);
            }
        }
        catch (Exception e)
        {
            logger.error("AlfrescoVirtualFileSystem.createFolder: Error posting activity.",e);
        }
        // convert FileInfo
        return convertFileInfo(newNodeInfo, path);
    }

    //
    // ----- VFS implementation for Lists service
    //

    public VFSNode getNodeFromList(Guid listId, String inFolder, String fileLeafRef)
    {
        // Get base folder of list depending on listId
        NodeRef listRoot = null;
        if(listId.equals(LISTID_ROOT_DOCUMENTS) || ((inFolder!=null)&&(inFolder.length()>0)))
        {
            try
            {
                listRoot = getRootNodeRef();
            }
            catch(Exception e)
            {
            	checkForRetryingException(e);
                logger.error("Error getting root node of VFS.",e);
                return null;
            }
        }
        else
        {
            listRoot = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, listId.toInnerString().toLowerCase());
        }
        // calculate the path within the list
        String pathInList = null;
        if((inFolder!=null)&&(inFolder.length()>0))
        {
            pathInList = normalizePath(inFolder);
        }
        if((fileLeafRef!=null)&&(fileLeafRef.length()>0))
        {
            pathInList = pathInList != null ? pathInList + "/" + fileLeafRef : fileLeafRef;
        }
        // get the requested element
        try
        {
            if(pathInList != null)
            {
                FileInfo folderFileInfo = webDavHelper.getNodeForPath(listRoot, pathInList);
                return convertFileInfo(folderFileInfo, pathInList);
            }
            else
            {
                FileInfo listFileInfo = getFileFolderService().getFileInfo(listRoot);
                String pathToList = listId.equals(LISTID_ROOT_DOCUMENTS) ? "/" : "/"+listFileInfo.getName();
                return convertFileInfo(listFileInfo, pathToList);
            }
        }
        catch (InvalidNodeRefException | FileNotFoundException e)
        {
            return null;
        }
    }

    //
    // ----- helper methods
    //
    
    public Pair<String, String> getActivitySiteAndPath(NodeRef nodeRef)
    {
        // detect if node is part of a site and find site ID (i.e. site shortName)
        String siteId;
        try
        {
            siteId = siteService.getSiteShortName(nodeRef);
            if (siteId == null)
            {
                siteId = WebDAVHelper.EMPTY_SITE_ID;
            }
        }
        catch (Exception e)
        {
            siteId = WebDAVHelper.EMPTY_SITE_ID;
        }
        // depending on if this node is part of a site or not, the path in activiti is relative to a different root
        NodeRef sitePathRelativeTo;
        try
        {
            if(siteId.equals(WebDAVHelper.EMPTY_SITE_ID))
            {
                sitePathRelativeTo = getRootNodeRef();
            }
            else
            {
                try
                {
                    sitePathRelativeTo = siteService.getContainer(siteId, SiteService.DOCUMENT_LIBRARY);
                }
                catch(Exception e)
                {
                    logger.debug("AlfrescoVirtualFileSystem.createFolder: No " + SiteService.DOCUMENT_LIBRARY + " container found.");
                    sitePathRelativeTo = getRootNodeRef();
                }
            }
        }
        catch(Exception e)
        {
            logger.debug("getActivityPathForNode: Error getting root node for activity post",e);
            return new Pair<String, String>(siteId, "/");
        }
        // get path
        try
        {
            return new Pair<String, String>(siteId, getWebDavHelper().getPathFromNode(sitePathRelativeTo, nodeRef));
        }
        catch (Exception error)
        {
            logger.debug("AlfrescoVirtualFileSystem.getActivityPathForNode: Cannot build a path for activity post.");
            return new Pair<String, String>(siteId, "/");
        }
    }

    public FileInfo getFileInfoForPath(NodeRef startingNode, String path) throws FileNotFoundException
    {
        return webDavHelper.getNodeForPath(startingNode, path);
    }
    
    public FileInfo getFileInfoForPath(String path) throws FileNotFoundException
    {
        // get root node
        NodeRef rootNodeRef;
        try
        {
            rootNodeRef = getRootNodeRef();
        }
        catch(Exception e)
        {
        	checkForRetryingException(e);
            logger.error("Error getting root node of VFS.",e);
            throw new FileNotFoundException("Error getting root node of VFS.");
        }
        return getFileInfoForPath(rootNodeRef, path);
    }

    public String getSitePath(HttpServletRequest request)
    {
        if( (this.sitePathOverwrite != null) && (this.sitePathOverwrite.length() > 0) )
        {
            return this.sitePathOverwrite;
        }
        return request.getContextPath().equals("/") ? Const.DEFAULT_SITE_PATH_IN_CONTEXT : request.getContextPath() + Const.DEFAULT_SITE_PATH_IN_CONTEXT;
    }

    public String getSitePrefix(HttpServletRequest request)
    {
        if( (this.baseUrlOverwrite != null) && (this.baseUrlOverwrite.length() > 0) )
        {
            return this.baseUrlOverwrite;
        }
        String protocol = request.isSecure() ? "https://" : "http://";
        int defaultPort = request.isSecure() ? 443 : 80;
        String portString = (request.getLocalPort() != defaultPort) ? ":" + Integer.toString(request.getLocalPort()) : "";
        return protocol + request.getServerName() + portString + getSitePath(request);
    }

    public ThresholdOutputStreamFactory createStreamFactory()
    {
        File tempDirectory = TempFileProvider.getTempDir(tempDirectoryName);
        return ThresholdOutputStreamFactory.newInstance(tempDirectory, tempFileThreshold, tempFileMaxContentSize, encryptTempFiles);
    }
    
    public static void checkForRetryingException(Throwable t)
    {
    	if(RetryingTransactionHelper.extractRetryCause(t) != null)
    	{
            if (t instanceof RuntimeException)
            {
                throw (RuntimeException)t;
            }
            else
            {
                throw new AlfrescoRuntimeException("Retry",t);
            }
    	}
    }

    //
    // ----- Getter and Setter for all services
    //
    
    public AuthenticationService getAuthenticationService()
    {
        return authenticationService;
    }

    public void setAuthenticationService(AuthenticationService authenticationService)
    {
        this.authenticationService = authenticationService;
    }

    public NodeService getNodeService()
    {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public FileFolderService getFileFolderService()
    {
        return fileFolderService;
    }

    public void setFileFolderService(FileFolderService fileFolderService)
    {
        this.fileFolderService = fileFolderService;
    }

    public CheckOutCheckInService getCheckOutCheckInService()
    {
        return checkOutCheckInService;
    }

    public void setCheckOutCheckInService(CheckOutCheckInService checkOutCheckInService)
    {
        this.checkOutCheckInService = checkOutCheckInService;
    }

    public LockService getLockService()
    {
        return lockService;
    }

    public void setLockService(LockService lockService)
    {
        this.lockService = lockService;
    }

    public VersionService getVersionService()
    {
        return versionService;
    }

    public void setVersionService(VersionService versionService)
    {
        this.versionService = versionService;
    }

    public DictionaryService getDictionaryService()
    {
        return dictionaryService;
    }

    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    public NamespaceService getNamespaceService()
    {
        return namespaceService;
    }

    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }

    public MimetypeService getMimetypeService()
    {
        return mimetypeService;
    }

    public void setMimetypeService(MimetypeService mimetypeService)
    {
        this.mimetypeService = mimetypeService;
    }

    public SearchService getSearchService()
    {
        return searchService;
    }

    public void setSearchService(SearchService searchService)
    {
        this.searchService = searchService;
    }

    public BehaviourFilter getPolicyBehaviourFilter()
    {
        return policyBehaviourFilter;
    }

    public void setPolicyBehaviourFilter(BehaviourFilter policyBehaviourFilter)
    {
        this.policyBehaviourFilter = policyBehaviourFilter;
    }

    public TransactionService getTransactionService()
    {
        return transactionService;
    }

    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }

    public TenantService getTenantService()
    {
        return tenantService;
    }

    public void setTenantService(TenantService tenantService)
    {
        this.tenantService = tenantService;
    }

    public SiteService getSiteService()
    {
        return siteService;
    }

    public void setSiteService(SiteService siteService)
    {
        this.siteService = siteService;
    }

    public WebDAVHelper getWebDavHelper()
    {
        return webDavHelper;
    }

    public void setWebDavHelper(WebDAVHelper webDavHelper)
    {
        this.webDavHelper = webDavHelper;
    }

    public WebDAVInitParameters getWebDavInitParams()
    {
        return webDavInitParams;
    }

    public void setWebDavInitParams(WebDAVInitParameters webDavInitParams)
    {
        this.webDavInitParams = webDavInitParams;
    }

    public AuthenticationContext getAuthenticationContext()
    {
        return authenticationContext;
    }

    public void setAuthenticationContext(AuthenticationContext authenticationContext)
    {
        this.authenticationContext = authenticationContext;
    }

    public WebDAVActivityPoster getActivityPoster()
    {
        return activityPoster;
    }

    public void setActivityPoster(WebDAVActivityPoster activityPoster)
    {
        this.activityPoster = activityPoster;
    }

    public DataModelMappingConfiguration getDataModelMappingConfiguration()
    {
        return dataModelMappingConfiguration;
    }

    public void setDataModelMappingConfiguration(DataModelMappingConfiguration dataModelMappingConfiguration)
    {
        this.dataModelMappingConfiguration = dataModelMappingConfiguration;
    }

    public IOContentFilterRegistry getIoContentFilterRegistry()
    {
        return ioContentFilterRegistry;
    }

    public void setIoContentFilterRegistry(IOContentFilterRegistry ioContentFilterRegistry)
    {
        this.ioContentFilterRegistry = ioContentFilterRegistry;
    }

    public SimpleCache<String, NodeRef> getSingletonCache()
    {
        return singletonCache;
    }

    public void setSingletonCache(SimpleCache<String, NodeRef> singletonCache)
    {
        this.singletonCache = singletonCache;
    }

    public String getBaseUrlOverwrite()
    {
        return baseUrlOverwrite;
    }

    public void setBaseUrlOverwrite(String baseUrlOverwrite)
    {
        this.baseUrlOverwrite = baseUrlOverwrite;
    }

    public String getSitePathOverwrite()
    {
        return sitePathOverwrite;
    }

    public void setSitePathOverwrite(String sitePathOverwrite)
    {
        this.sitePathOverwrite = sitePathOverwrite;
    }

    public String getTempDirectoryName()
    {
        return tempDirectoryName;
    }

    public void setTempDirectoryName(String tempDirectoryName)
    {
        this.tempDirectoryName = tempDirectoryName;
    }

    public boolean isEncryptTempFiles()
    {
        return encryptTempFiles;
    }

    public void setEncryptTempFiles(boolean encryptTempFiles)
    {
        this.encryptTempFiles = encryptTempFiles;
    }

    public int getTempFileThreshold()
    {
        return tempFileThreshold;
    }

    public void setTempFileThreshold(int tempFileThreshold)
    {
        this.tempFileThreshold = tempFileThreshold;
    }

    public long getTempFileMaxContentSize()
    {
        return tempFileMaxContentSize;
    }

    public void setTempFileMaxContentSize(long tempFileMaxContentSize)
    {
        this.tempFileMaxContentSize = tempFileMaxContentSize;
    }

}

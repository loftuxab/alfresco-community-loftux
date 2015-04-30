/*
 * Copyright 2005-2015 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.officeservices.vfs;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.alfresco.repo.webdav.WebDAVHelper;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.Pair;
import org.apache.log4j.Logger;

import com.xaldon.officeservices.UserData;
import com.xaldon.officeservices.datamodel.Guid;
import com.xaldon.officeservices.protocol.VermeerRequest;
import com.xaldon.officeservices.protocol.VermeerReturnDictionary;
import com.xaldon.officeservices.protocol.VermeerReturnDictionaryDirectory;
import com.xaldon.officeservices.protocol.VermeerReturnDictionaryLibrary;
import com.xaldon.officeservices.vfs.VFSDocumentNode;
import com.xaldon.officeservices.vfs.VFSNode;

/**
 * <p>Implementation of the <code>{@link VFSNode}</code> interface of the <i>Alfresco
 * Office Services (AOS)</i> library for folders.</p>
 * 
 * <p>This class represents a folder in the Alfresco Repository in the virtual file system
 * exposed through the SharePoint protocols.</p>
 * 
 * <p>See javadoc in <code>{@link AlfrescoVirtualFileSystem}</code> for a description of the
 * general layout of the VFS.</p>
 * 
 * <p>Implementation details:<br/>
 * Objects of this class are wrapped around a <code>{@link FileInfo}</code> object that describes
 * the folder represented by that <code>FolderNode</code> object. It also has a reference to the
 * singleton <code>{@link AlfrescoVirtualFilesystem}</code> to access the xxxService instances
 * that are required to perform the requested operation.<br/>
 * This class is instantiated for each request that operates on this folder. It does not need to
 * be thread safe and MUST NOT BE CACHED.</p>
 * 
 * @since 5.0
 * 
 * @author Stefan Kopf
 *
 */
public class FolderNode implements VFSNode
{
    
    protected AlfrescoVirtualFileSystem vfs;
    
    protected FileInfo fileInfo;
    
    protected String relativePath;
    
    protected static Logger logger = Logger.getLogger(FolderNode.class);

	public FolderNode(FileInfo info, String path, AlfrescoVirtualFileSystem avfs)
	{
	    fileInfo = info;
	    relativePath = path;
	    vfs = avfs;
	}
    
    public FileInfo getFileInfo()
    {
        return fileInfo;
    }
	
	public NodeRef getNodeRef()
	{
	    return fileInfo.getNodeRef();
	}
	
	public String getRelativePath()
	{
	    return relativePath;
	}

    /**
     * Returns the name of this folder. The name returned by this method is identical
     * to the last path element of the path that has been used to locate this folder.
     * 
     * @param callContext the context this method is called in. One of the <code>CALLCONTEXT_</code> statics.
     * 
     * @return the name of this folder
     */
    @Override
	public String getName(int callContext)
	{
		return fileInfo.getName();
	}

    /**
     * Returns the <code>{@link VermeerReturnDictionaryDirectory}</code> containing all the metainfo of this folder.
     * 
     * @param callContext the context this method is called in. One of the <code>CALLCONTEXT_</code> statics.
     * 
     * @return the <code>{@link VermeerReturnDictionaryDirectory}</code> containing all the metainfo of this folder
     */
    @Override
	public VermeerReturnDictionary getVermeerMetaInfoDictionary(int callContext)
	{
        String libraryPath = AlfrescoVirtualFileSystem.getFirstFolder(relativePath);
        if(libraryPath != null)
        {
            VFSNode libraryFolder = null;
            try
            {
                libraryFolder = vfs.getNodeByPath(null, libraryPath, callContext);
                if(libraryFolder instanceof VFSDocumentNode)
                {
                    libraryFolder = null;
                }
            }
            catch (Exception e)
            {
            	AlfrescoVirtualFileSystem.checkForRetryingException(e);
                libraryFolder = null;
            }
            Guid listId = libraryFolder == null ? AlfrescoVirtualFileSystem.LISTID_ROOT_DOCUMENTS : Guid.parse(((FolderNode)libraryFolder).getFileInfo().getNodeRef().getId());
            VermeerReturnDictionaryDirectory result = new VermeerReturnDictionaryDirectory(fileInfo.getCreatedDate(),fileInfo.getModifiedDate(),listId.toString());
            result.setMinorVersionsEnabled(true);
            return result;
        }
        else
        {
            Guid listId = relativePath.equals("/") ? AlfrescoVirtualFileSystem.LISTID_ROOT_DOCUMENTS : Guid.parse(fileInfo.getNodeRef().getId());
            return new VermeerReturnDictionaryLibrary(listId.toString(), fileInfo.getName(), fileInfo.getCreatedDate(),fileInfo.getModifiedDate(), true);
        }
	}

    /**
     * Returns the element in this folder that is identified with the given name or <code>null</code>
     * if no such element exists.
     * 
     * @param userData the <code>{@link UserData}</code> object describing the user that made the request to this service
     * @param name the name of the subnode
     * @param callContext the context this method is called in. One of the <code>CALLCONTEXT_</code> statics.
     * 
     * @return the element in this folder that is identified with the given name or <code>null</code>
     *     if no such element exists
     */
    @Override
	public VFSNode getContainmentByName(UserData userData, String name, int callContext)
	{
	    try
        {
	        FileInfo item = vfs.getFileInfoForPath(fileInfo.getNodeRef(), name);
	        String itemPath = relativePath;
	        if(!itemPath.endsWith("/"))
	        {
	            itemPath = itemPath + "/";
	        }
	        itemPath += name;
            return vfs.convertFileInfo(item,itemPath);
        }
        catch (FileNotFoundException e)
        {
            return null;
        }
	}

    /**
     * Returns a <code>List</code> of all enumerable and non hidden elements in this folder.
     * 
     * @param userData the <code>{@link UserData}</code> object describing the user that made the request to this service
     * @param callContext the context this method is called in. One of the <code>CALLCONTEXT_</code> statics.
     * 
     * @return a <code>List</code> of all enumerable and non hidden elements in this folder
     */
    @Override
	public List<?> getEnumerableContainees(UserData userData, int callContext)
	{
        //List<FileInfo> childFileInfos = vfs.getFileFolderService().list(fileInfo.getNodeRef());
        List<FileInfo> childFileInfos;
        try
        {
            childFileInfos = vfs.getWebDavHelper().getChildren(fileInfo);
        }
        catch(Exception e)
        {
        	AlfrescoVirtualFileSystem.checkForRetryingException(e);
            return new ArrayList<VFSNode>();
        }
        List<VFSNode> result = new ArrayList<VFSNode>(childFileInfos.size());
        String itemPathSuffix = relativePath;
        if(!itemPathSuffix.endsWith("/"))
        {
            itemPathSuffix = itemPathSuffix + "/";
        }
        for(FileInfo childFileInfo : childFileInfos)
        {
            VFSNode n = vfs.convertFileInfo(childFileInfo,itemPathSuffix+childFileInfo.getName());
            if(n != null)
            {
                result.add(n);
            }
        }
		return result;
	}

    /**
     * Returns the <code>{@link Date}</code> when this folder has been modified the last time.
     * 
     * @param callContext the context this method is called in. One of the <code>CALLCONTEXT_</code> statics.
     * 
     * @return the <code>{@link Date}</code> when this folder has been modified the last time
     */
    @Override
    public Date getDateLastModified(int callContext)
    {
        return fileInfo.getModifiedDate();
    }

    /**
     * Rename this folder and return the result of this operation (<code>true</code> for success and <code>false</code> in the event of an error).
     * 
     * @param newName the new name of the node
     * @param callContext the context this method is called in. One of the <code>CALLCONTEXT_</code> statics.
     * 
     * @return the result of this operation (<code>true</code> for success and <code>false</code> in the event of an error)
     */
    @Override
	public boolean rename(String newName, int callContext)
	{
	    logger.debug("FolderNode.rename ENTER newName="+newName);
	    try
        {
	        vfs.getFileFolderService().rename(fileInfo.getNodeRef(), newName);
            logger.debug("Rename successfull");
	        logger.debug("FolderNode.rename EXIT true");
            return true;
        }
        catch (Exception e)
        {
        	AlfrescoVirtualFileSystem.checkForRetryingException(e);
            logger.debug("Rename failed",e);
            logger.debug("FolderNode.rename EXIT false");
            return false;
        }
	}

    /**
     * Delete this folder and return the result of this operation (<code>true</code> for success and <code>false</code> in the event of an error).
     * 
     * @param callContext the context this method is called in. One of the <code>CALLCONTEXT_</code> statics.
     * 
     * @return the result of this operation (<code>true</code> for success and <code>false</code> in the event of an error)
     */
    @Override
	public boolean delete(int callContext)
	{
        try
        {
            // get parent nod ref for later Activity post
            NodeRef parentNodeRef = vfs.getNodeService().getPrimaryParent(fileInfo.getNodeRef()).getParentRef();
            // delete file
            vfs.getFileFolderService().delete(fileInfo.getNodeRef());
            // post activity
            try
            {
                FileInfo parentFileInfo = vfs.getFileFolderService().getFileInfo(parentNodeRef);
                Pair<String, String> activitySiteAndPath = vfs.getActivitySiteAndPath(parentNodeRef);
                if(!WebDAVHelper.EMPTY_SITE_ID.equals(activitySiteAndPath.getFirst()))
                {
                    vfs.getActivityPoster().postFileFolderDeleted(activitySiteAndPath.getFirst(), vfs.getTenantDomain(), activitySiteAndPath.getSecond(), parentFileInfo, fileInfo);
                }
            }
            catch (Exception e)
            {
                logger.error("FolderNode.delete: Error posting activity.",e);
            }
            return true;
        }
        catch (Exception e)
        {
        	AlfrescoVirtualFileSystem.checkForRetryingException(e);
            logger.debug("Error deleting node",e);
            return false;
        }
	}

    /**
     * Move this folder to the given destination path and return the result of this operation.
     * 
     * @param userData the <code>{@link UserData}</code> object describing the user that made the request to this service
     * @param destinationPath path to the new destination and name of this folder relative to the root of the VFS
     * @param callContext the context this method is called in. One of the <code>CALLCONTEXT_</code> statics.
     * 
     * @return the result of this operation
     */
    @Override
	public boolean move(UserData userData, String destinationPath, int callContext)
	{
        logger.debug("FolderNode.move ENTER destinationPath="+destinationPath);
        try
        {
            String newParentPath = "/";
            String newName = destinationPath;
            int i = destinationPath.lastIndexOf('/');
            if(i >= 0)
            {
                newParentPath = destinationPath.substring(0, i);
                newName = destinationPath.substring(i+1);
            }
            FileInfo destination = vfs.getFileInfoForPath(newParentPath);
            if(destination == null)
            {
                logger.debug("Move failed. Destination folder does not exist.");
                logger.debug("FolderNode.move EXIT false");
                return false;
            }
            if(!destination.isFolder() || destination.isLink())
            {
                logger.debug("Move failed. Destination is not a folder.");
                logger.debug("FolderNode.move EXIT false");
                return false;
            }
            vfs.getFileFolderService().move(fileInfo.getNodeRef(), destination.getNodeRef(), newName);
            logger.debug("Move successfull");
            logger.debug("FolderNode.move EXIT true");
            return true;
        }
        catch (Exception e)
        {
        	AlfrescoVirtualFileSystem.checkForRetryingException(e);
            logger.debug("Move failed",e);
            logger.debug("FolderNode.move EXIT false");
            return false;
        }
	}

    @Override
	public boolean copy(UserData userData, String destinationPath, int callContext)
	{
        // not supported.
        // only required by the FPSE windows explorer integration on Windows XP
		return false;
	}


    
    // ===== the following methods are never invoked by the AlfrescoVirtualFileSystem implementation
    
    @Override
    public VFSDocumentNode createNewFile(UserData userData, String filename, InputStream content, int callContext)
    {
        return null;
    }

    @Override
    public VFSDocumentNode createNewFile(UserData userData, String filename, VermeerRequest request, int callContext)
    {
        return null;
    }

    @Override
    public VFSNode createNewFolder(UserData userData, String foldername, int callContext)
    {
        return null;
    }
    
}

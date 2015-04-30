/*
 * Copyright 2005-2015 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.officeservices.vfs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.enterprise.repo.officeservices.metadata.ContentFilter;
import org.alfresco.enterprise.repo.officeservices.metadata.ContentFilterProcessingResult;
import org.alfresco.enterprise.repo.officeservices.metadata.ContentPostProcessor;
import org.alfresco.enterprise.repo.officeservices.metadata.IOContentFilterRegistry;
import org.alfresco.enterprise.repo.officeservices.service.Const;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.lock.mem.Lifetime;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.repo.version.VersionModel;
import org.alfresco.repo.webdav.LockInfo;
import org.alfresco.repo.webdav.LockInfoImpl;
import org.alfresco.repo.webdav.WebDAV;
import org.alfresco.repo.webdav.WebDAVHelper;
import org.alfresco.service.cmr.coci.CheckOutCheckInService;
import org.alfresco.service.cmr.dictionary.ClassDefinition;
import org.alfresco.service.cmr.lock.LockService;
import org.alfresco.service.cmr.lock.LockStatus;
import org.alfresco.service.cmr.lock.LockType;
import org.alfresco.service.cmr.lock.UnableToReleaseLockException;
import org.alfresco.service.cmr.model.FileExistsException;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.cmr.version.VersionHistory;
import org.alfresco.service.cmr.version.VersionType;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.Pair;
import org.alfresco.util.TempFileProvider;
import org.apache.log4j.Logger;

import com.xaldon.officeservices.UserData;
import com.xaldon.officeservices.datamodel.ContentTypeId;
import com.xaldon.officeservices.datamodel.Guid;
import com.xaldon.officeservices.lists.CheckinType;
import com.xaldon.officeservices.protocol.VermeerRequest;
import com.xaldon.officeservices.protocol.VermeerReturnDictionary;
import com.xaldon.officeservices.protocol.VermeerReturnDictionaryFile;
import com.xaldon.officeservices.vfs.VFSDocumentNode;
import com.xaldon.officeservices.vfs.VFSDocumentVersion;
import com.xaldon.officeservices.vfs.VFSNode;

/**
 * <p>Implementation of the <code>{@link VFSDocumentNode}</code> interface of the <i>Alfresco
 * Office Services (AOS)</i> library for documents.</p>
 * 
 * <p>This class represents a document in the Alfresco Repository in the virtual file system
 * exposed through the SharePoint protocols.</p>
 * 
 * <p>See javadoc in <code>{@link AlfrescoVirtualFileSystem}</code> for a description of the
 * general layout of the VFS.</p>
 * 
 * <p>Implementation details:<br/>
 * Objects of this class are wrapped around a <code>{@link FileInfo}</code> object that describes
 * the document represented by that <code>DocumentNode</code> object. It also has a reference to the
 * singleton <code>{@link AlfrescoVirtualFilesystem}</code> to access the xxxService instances
 * that are required to perform the requested operation.<br/>
 * This class is instantiated for each request that operates on this document. It does not need to
 * be thread safe and MUST NOT BE CACHED.</p>
 * 
 * @since 5.0
 * 
 * @author Stefan Kopf
 *
 */
public class DocumentNode implements VFSDocumentNode
{
    
    protected AlfrescoVirtualFileSystem vfs;
    
    protected FileInfo fileInfo;
    
    protected String relativePath;
    
    protected boolean isHistoryNode;
    
    protected ContentResponse contentResponse;
    
    protected boolean activateVersioningOnWrite = true;
    
    protected static Logger logger = Logger.getLogger(DocumentNode.class);

    public DocumentNode(FileInfo info, String path, AlfrescoVirtualFileSystem vfs, boolean isHistoryNode)
    {
        this.fileInfo = info;
        this.relativePath = path;
        this.vfs = vfs;
        this.isHistoryNode = isHistoryNode;
    }

    public DocumentNode(FileInfo info, String path, AlfrescoVirtualFileSystem vfs)
    {
        this(info, path, vfs, false);
    }
    
    public FileInfo getFileInfo()
    {
        return fileInfo;
    }
    
    protected String getSiteId()
    {
        String siteId;
        try
        {
            siteId = vfs.getSiteService().getSiteShortName(fileInfo.getNodeRef());
            if (siteId == null)
            {
                siteId = WebDAVHelper.EMPTY_SITE_ID;
            }
        }
        catch (Exception e)
        {
            siteId = WebDAVHelper.EMPTY_SITE_ID;
        }
        return siteId;
        
    }
    
    // information retrieval

    /**
     * Returns the name of this document. The name returned by this method is identical
     * to the last path element of the path that has been used to locate this folder.
     * 
     * @param callContext the context this method is called in. One of the <code>CALLCONTEXT_</code> statics.
     * 
     * @return the name of this document
     */
    @Override
    public String getName(int callContext)
    {
        // TODO: If this node is checked out, we need to check if the name of the working copy has been modified
        // and if so return the new name of the working copy.
        return fileInfo.getName();
    }

    /**
     * Returns the <code>{@link VermeerReturnDictionaryFile}</code> containing all the metainfo of this document.
     * 
     * @param callContext the context this method is called in. One of the <code>CALLCONTEXT_</code> statics.
     * 
     * @return the <code>{@link VermeerReturnDictionaryFile}</code> containing all the metainfo of this document
     */
    @Override
    public VermeerReturnDictionary getVermeerMetaInfoDictionary(int callContext)
    {
        NodeRef nodeToRead = fileInfo.getNodeRef();
        boolean isCheckedOut = isCheckedOut();
        boolean isCheckedOutToLocal = false;
        if(isCheckedOut)
        {
            nodeToRead = vfs.getCheckOutCheckInService().getWorkingCopy(nodeToRead);
            isCheckedOutToLocal = vfs.getNodeService().hasAspect(nodeToRead, AlfrescoVirtualFileSystem.ASPECT_AOS_CHECKED_OUT_TO_LOCAL);
        }
        ContentReader reader = vfs.getFileFolderService().getReader(nodeToRead);
        long size = (reader==null) ? 0 : reader.getSize();
        LockStatus lockStatus = vfs.getLockService().getLockStatus(nodeToRead);
        boolean isLocked = (lockStatus == LockStatus.LOCK_OWNER) || (lockStatus == LockStatus.LOCKED);
        Date dateCreated = (Date)vfs.getNodeService().getProperty(fileInfo.getNodeRef(), ContentModel.PROP_CREATED);
        Date dateModified = (Date)vfs.getNodeService().getProperty(nodeToRead, ContentModel.PROP_MODIFIED);
        String userCreated = getCreator();
        String userModified = getModifier();
        Date dateLockExpires = null;
        Date dateLockCreated = null;
        if(isLocked)
        {
            dateLockExpires = (Date)vfs.getNodeService().getProperty(nodeToRead, ContentModel.PROP_EXPIRY_DATE);
            if(dateLockExpires == null)
            {
                Date now = new Date();
                dateLockExpires = new Date(now.getTime() + 10 * 60 * 1000);
            }
            dateLockCreated = new Date(dateLockExpires.getTime() - 60 * 60 * 1000);
        }
        Date dateCheckedOut =  null;
        if(isCheckedOut)
        {
            dateCheckedOut = (Date)vfs.getNodeService().getProperty(nodeToRead, ContentModel.PROP_CREATED);
        }
        String lockOwner = null;
        if(isLocked)
        {
            lockOwner = (String)vfs.getNodeService().getProperty(nodeToRead, ContentModel.PROP_LOCK_OWNER);
        }
        String checkoutOwner = null;
        if(isCheckedOut)
        {
            checkoutOwner = (String)vfs.getNodeService().getProperty(fileInfo.getNodeRef(), ContentModel.PROP_LOCK_OWNER);
        }
        VersionNumber versionNumber = getVersionNumber();
        VermeerReturnDictionaryFile dict = new VermeerReturnDictionaryFile(size, // long size
                isLocked, // boolean isLocked
                isCheckedOut, // boolean isCheckedOut
                dateCreated, // Date dateCreated
                dateModified, // Date dateLastModified
                userCreated, // String createdBy
                userModified, // String modifiedBy
                dateLockCreated, // Date dateLocked
                dateLockExpires, // Date dateLockExpires
                dateCheckedOut, // Date dateCheckedOut
                lockOwner, // String lockedBy
                checkoutOwner, // String checkedOutBy
                versionNumber.toString()); // String version
        dict.setCheckoutToLocal(isCheckedOutToLocal);
        return dict;
    }
    
    public String getCreator()
    {
        return (String)vfs.getNodeService().getProperty(fileInfo.getNodeRef(), ContentModel.PROP_CREATOR);
    }
    
    public String getModifier()
    {
        return (String)vfs.getNodeService().getProperty(fileInfo.getNodeRef(), ContentModel.PROP_MODIFIER);
    }

    /**
     * Returns the <code>{@link Date}</code> when this document has been modified the last time.
     * 
     * @param callContext the context this method is called in. One of the <code>CALLCONTEXT_</code> statics.
     * 
     * @return the <code>{@link Date}</code> when this document has been modified the last time
     */
    @Override
    public Date getDateLastModified(int callContext)
    {
        if(isCheckedOut())
        {
            NodeRef workingCopy = vfs.getCheckOutCheckInService().getWorkingCopy(fileInfo.getNodeRef());
            return (Date)vfs.getNodeService().getProperty(workingCopy, ContentModel.PROP_MODIFIED);
        }
        else
        {
            return fileInfo.getModifiedDate();
        }
    }

    /**
     * Returns the String that is sent as <code>Content-Type</code> in HTTP headers.
     * 
     * @param callContext the context this method is called in. One of the <code>CALLCONTEXT_</code> statics.
     * 
     * @return the String that is sent as <code>Content-Type</code> in HTTP headers
     */
    @Override
    public String getMimeString(int callContext)
    {
        NodeRef nodeToRead = fileInfo.getNodeRef();
        if(isCheckedOut())
        {
            nodeToRead = vfs.getCheckOutCheckInService().getWorkingCopy(nodeToRead);
        }
        ContentReader reader = vfs.getFileFolderService().getReader(nodeToRead);
        return (reader==null) ? "application/octest-stream" : reader.getMimetype();
    }

    /**
     * Returns the size of this document.<br/>
     * The value has to match the number of octets that are written by a call to
     * <code>{@link #emitContent(OutputStream, int)}</code> to the output.
     * 
     * @param callContext the context this method is called in. One of the <code>CALLCONTEXT_</code> statics.
     * 
     * @return the size of this document    
     */
    @Override
    public long getSize(int callContext)
    {
        if(callContext == VFSNode.CALLCONTEXT_HTTPGET)
        {
            // In a get request, we need to return the real size of the potentially filtered output
            return getContentResponse().getSize();
        }
        NodeRef nodeToRead = fileInfo.getNodeRef();
        if(isCheckedOut())
        {
            nodeToRead = vfs.getCheckOutCheckInService().getWorkingCopy(nodeToRead);
        }
        ContentReader reader = vfs.getFileFolderService().getReader(nodeToRead);
        return (reader==null) ? 0 : reader.getSize();
    }

    /**
     * Returns <code>true</code> if the binary content has been modified since the <code>lastSeen</code> Date.<br/>
     * Please not that HTTP transfers dates with accurate seconds, but no milliseconds. If a previous call to
     * <code>{@link #getDateLastModified(int)}</code> has returned a date like this <code>2014-01-01 12:00:05.900</code>
     * then this method will be invoked with this date <code>2014-01-01 12:00:05.0000</code>.
     * 
     * @param lastSeen the Date returned by <code>{@link #getDateLastModified(int)}</code> when the client retrieved the content the last time
     * @param callContext the context this method is called in. One of the <code>CALLCONTEXT_</code> statics.
     * 
     * @return <code>true</code> if the binary content has been modified since the <code>lastSeen</code> Date
     */
    @Override
    public boolean isModifiedSince(Date lastSeen, int callContext)
    {
        // WARNING: date values in HTTP are transfered with accurate seconds !
        long lastModifiedSeconds = getDateLastModified(callContext).getTime() / 1000l;
        long lastSeenSeconds = lastSeen.getTime() / 1000l;
        return (lastModifiedSeconds >  lastSeenSeconds);
    }
    
    // ===== checkout / checkin

    /**
     * Returns <code>true</code> if and only if the most recent version of document (if it has been
     * checked out, the working copy) is locked.
     *  
     * @return <code>true</code> if and only if the most recent version of document (if it has been
     *     checked out, the working copy) is locked
     */
    public boolean isLocked()
    {
        // if this node is checked out, then we test the lock status  of the working copy. otherwise we
        // test the lock status of the pure node
        NodeRef nodeToCheck = fileInfo.getNodeRef();
        if(isCheckedOut())
        {
            nodeToCheck = vfs.getCheckOutCheckInService().getWorkingCopy(nodeToCheck);
        }
        LockStatus lockStatus = vfs.getLockService().getLockStatus(nodeToCheck);
        return (lockStatus == LockStatus.LOCK_OWNER) || (lockStatus == LockStatus.LOCKED);
    }

    /**
     * Returns <code>true</code> if and only if this document has been checked out.
     * 
     * @return <code>true</code> if and only if this document has been checked out
     */
    public boolean isCheckedOut()
    {
        CheckOutCheckInService checkOutCheckInService = vfs.getCheckOutCheckInService();
        return checkOutCheckInService.isCheckedOut(fileInfo.getNodeRef());
    }
    
    /**
     * Returns the user name of the owner of the lock placed on the most recent version of
     * this document (if it has been checked out, on the working copy) or <code>null</code>
     * if the most recent version of this document is not locked.
     * 
     * @return the user name of the owner of the lock placed on the most recent version of
     *     this document (if it has been checked out, on the working copy) or <code>null</code>
     *     if the most recent version of this document is not locked
     */
    public String getLockOwner()
    {
        NodeRef nodeToCheck = fileInfo.getNodeRef();
        if(isCheckedOut())
        {
            nodeToCheck = vfs.getCheckOutCheckInService().getWorkingCopy(nodeToCheck);
        }
        LockStatus lockStatus = vfs.getLockService().getLockStatus(nodeToCheck);
        if((lockStatus == LockStatus.LOCK_OWNER) || (lockStatus == LockStatus.LOCKED))
        {
            return (String) vfs.getNodeService().getProperty(nodeToCheck, ContentModel.PROP_LOCK_OWNER);
        }
        else
        {
            return null;
        }
    }
    
    /**
     * Returns the expiration date of the lock placed on the most recent version of
     * this document (if it has been checked out, on the working copy) or <code>null</code>
     * if the most recent version of this document is not locked.
     * 
     * @return the expiration date of the lock placed on the most recent version of
     *     this document (if it has been checked out, on the working copy) or <code>null</code>
     *     if the most recent version of this document is not locked
     */
    public Date getLockExpiry()
    {
        NodeRef nodeToCheck = fileInfo.getNodeRef();
        if(isCheckedOut())
        {
            nodeToCheck = vfs.getCheckOutCheckInService().getWorkingCopy(nodeToCheck);
        }
        LockStatus lockStatus = vfs.getLockService().getLockStatus(nodeToCheck);
        if((lockStatus == LockStatus.LOCK_OWNER) || (lockStatus == LockStatus.LOCKED))
        {
            return (Date) vfs.getNodeService().getProperty(nodeToCheck, ContentModel.PROP_EXPIRY_DATE);
        }
        else
        {
            return null;
        }
    }
    
    /**
     * Returns the date when this document has been checked out or <code>null</code> if this document
     * is not checked out.
     * 
     * @return the date when this document has been checked out or <code>null</code> if this document
     *     is not checked out
     */
    public Date getCheckoutDate()
    {
        if(isCheckedOut())
        {
            NodeRef workingCopy = vfs.getCheckOutCheckInService().getWorkingCopy(fileInfo.getNodeRef());
            if(workingCopy != null)
            {
                return (Date) vfs.getNodeService().getProperty(workingCopy, ContentModel.PROP_CREATED);
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }
    
    /**
     * Returns the owner of the checkout of this document or <code>null</code> if this document is not checked out.
     * 
     * @return the owner of the checkout of this document or <code>null</code> if this document is not checked out
     */
    public String getCheckoutOwner()
    {
        if(isCheckedOut())
        {
            return (String) vfs.getNodeService().getProperty(fileInfo.getNodeRef(), ContentModel.PROP_LOCK_OWNER);
        }
        else
        {
            return null;
        }
    }
    
    /**
     * Returns <code>true</code> if and only if this document has been checked out and the working copy has the aspect <code>AlfrescoVirtualFileSystem.ASPECT_AOS_CHECKED_OUT_TO_LOCAL</code>.
     * 
     * @return <code>true</code> if and only if this document has been checked out and the working copy has the aspect <code>AlfrescoVirtualFileSystem.ASPECT_AOS_CHECKED_OUT_TO_LOCAL</code>
     */
    public boolean isCheckedOutToLocal()
    {
        CheckOutCheckInService checkOutCheckInService = vfs.getCheckOutCheckInService();
        if(checkOutCheckInService.isCheckedOut(fileInfo.getNodeRef()))
        {
            NodeRef workingCopy =  checkOutCheckInService.getWorkingCopy(fileInfo.getNodeRef());
            if(workingCopy != null)
            {
                return vfs.getNodeService().hasAspect(workingCopy, AlfrescoVirtualFileSystem.ASPECT_AOS_CHECKED_OUT_TO_LOCAL);
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Set a short-term lock on this document (<code>timeout > 0</code>) or
     * check out this document (<code>timeout == 0</code>).<br/>
     * If this call has been initiated by a FPSE request, the <code>webdavLockToken</code>
     * contains always <code>null</code>.<br/>
     * If this call has been initiated by a WebDAV LOCK method, the <code>timeout</code> contains
     * the fixed value <code>10</code> and <code>webdavLockToken</code> might contain
     * the current lock token known by the caller.<br/>
     * If this call has been initiated by a lists.asmx CheckOutFile call, the
     * timeout is always <code>0</code> to comply with the FPSE initiated call.
     * 
     * @param timeout number of minutes when the short-term lock has to expire or 0 to indicate a checkout
     * @param webdavLockToken contains the current lock token if this checkout has been initiated by a WebDAV LOCK request and the caller sent a lock token, <code>null</code> otherwise.
     * @param callContext the context this method is called in. One of the <code>CALLCONTEXT_</code> statics.
     * 
     * @return one of <code>{@link #CHECKOUT_OK}</code>, <code>{@link #CHECKOUT_FAILED}</code> or <code>{@link #CHECKOUT_DIFFERENTUSER}</code>
     */
    @Override
    public int checkout(int timeout, String webdavLockToken, int callContext)
    {
        return checkout(timeout, webdavLockToken, callContext, false);
    }

    /**
     * Set a short-term lock on this document (<code>timeout > 0</code>) or
     * check out this document (<code>timeout == 0</code>).<br/>
     * If this call has been initiated by a FPSE request, the <code>webdavLockToken</code>
     * contains always <code>null</code> and <code>checkoutToLocal</code> is always <code>false</code>.<br/>
     * If this call has been initiated by a WebDAV LOCK method, the <code>timeout</code> contains
     * the fixed value <code>10</code>, <code>checkoutToLocal</code> is always <code>false</code> and <code>webdavLockToken</code> might contain
     * the current lock token known by the caller.<br/>
     * If this call has been initiated by a lists.asmx CheckOutFile call, the
     * timeout is always <code>0</code> to comply with the FPSE initiated call and
     * <code>checkoutToLocal</code> is <code>true</code> if the Office client is configured to use
     * <i>checkout to local</i>.
     * 
     * @param timeout number of minutes when the short-term lock has to expire or 0 to indicate a checkout
     * @param webdavLockToken contains the current lock token if this checkout has been initiated by a WebDAV LOCK request and the caller sent a lock token, <code>null</code> otherwise.
     * @param callContext the context this method is called in. One of the <code>CALLCONTEXT_</code> statics.
     * @param checkoutToLocal indicates if the office client id configured to use <i>checkout to local</i> on checkout requests (<code>timeout == 0</code>)
     * 
     * @return one of <code>{@link #CHECKOUT_OK}</code>, <code>{@link #CHECKOUT_FAILED}</code> or <code>{@link #CHECKOUT_DIFFERENTUSER}</code>
     */
    public int checkout(int timeout, String webdavLockToken, int callContext, boolean checkoutToLocal)
    {
        if(isHistoryNode)
        {
            return CHECKOUT_FAILED;
        }
        if(timeout == 0)
        {
            return performCheckout(webdavLockToken, callContext, checkoutToLocal);
        }
        else
        {
            return performLock(timeout, webdavLockToken, callContext);
        }
    }

    private int performCheckout(String webdavLockToken, int callContext, boolean checkoutToLocal)
    {
        // get the current node status
        LockService lockService = vfs.getLockService();
        LockStatus lockStatus = lockService.getLockStatus(fileInfo.getNodeRef());

        // check if node is already checked out
        if(isCheckedOut())
        {
            if(lockStatus == LockStatus.LOCK_OWNER)
            {
                return CHECKOUT_OK;
            }
            else
            {
                return CHECKOUT_DIFFERENTUSER;
            }
        }
        
        // node is not checked out. Check if node is locked by someone else
        if(lockStatus == LockStatus.LOCKED)
        {
            // locked by someone else. we cannot checkout
            return CHECKOUT_DIFFERENTUSER;
        }
        
        // if node is locked by current user, unlock it so we can check it out
        boolean reCreateLock = false;
        Date initialExpiryDate = null;
        if(lockStatus == LockStatus.LOCK_OWNER)
        {
            // this node is locked by the current user. we need to remove this lock so
            // we can check this node out and then re-create a lock on the working copy
            reCreateLock = true;
            initialExpiryDate = (Date) vfs.getNodeService().getProperty(fileInfo.getNodeRef(), ContentModel.PROP_EXPIRY_DATE);
            if(!unlockNode(fileInfo.getNodeRef()))
            {
                return CHECKOUT_FAILED;
            }
        }

        // checkout the node
        NodeRef workingCopy;
        CheckOutCheckInService checkOutCheckInService = vfs.getCheckOutCheckInService();
        try
        {
            workingCopy = checkOutCheckInService.checkout(fileInfo.getNodeRef());
        }
        catch(Exception e)
        {
        	AlfrescoVirtualFileSystem.checkForRetryingException(e);
            logger.debug("Error checking out node",e);
            return CHECKOUT_FAILED;
        }
        
        // if we are checking out to local, mark this at the working copy
        if(checkoutToLocal)
        {
            vfs.getNodeService().addAspect(workingCopy, AlfrescoVirtualFileSystem.ASPECT_AOS_CHECKED_OUT_TO_LOCAL, null);
        }
        
        // re-create the lock
        if(reCreateLock && (workingCopy != null))
        {
            lockNode(workingCopy, calculateTimeout(initialExpiryDate, 3600));
        }
        
        // success
        return CHECKOUT_OK;
    }
    
    private int calculateTimeout(Date targetExpiryDate, int defaultTimeout)
    {
        if(targetExpiryDate == null)
        {
            return defaultTimeout;
        }
        long targetMillis = targetExpiryDate.getTime();
        long currentMillis = System.currentTimeMillis();
        if(targetMillis <= currentMillis)
        {
            return 0;
        }
        return (int)Math.round((double)(targetMillis - currentMillis) / 1000.0);
    }
    
    private int performLock(int timeout, String webdavLockToken, int callContext)
    {
        // if this node is checked out, then we lock the working copy. otherwise we
        // lock the node itself
        NodeRef nodeToLock = fileInfo.getNodeRef();
        if(isCheckedOut())
        {
            nodeToLock = vfs.getCheckOutCheckInService().getWorkingCopy(nodeToLock);
        }

        // check if node is already locked by a different user
        LockStatus lockStatus = vfs.getLockService().getLockStatus(nodeToLock);
        if(lockStatus == LockStatus.LOCKED)
        {
            return CHECKOUT_DIFFERENTUSER;
        }

        // lock the node
        if(lockNode(nodeToLock, timeout*60))
        {
            return CHECKOUT_OK;
        }
        else
        {
            return CHECKOUT_FAILED;
        }
    }

    @SuppressWarnings("deprecation")
    private boolean lockNode(NodeRef node, int timeoutSeconds)
    {
        try
        {
            String currentUser = vfs.getAuthenticationService().getCurrentUserName();
            String lockToken = WebDAV.makeLockToken(fileInfo.getNodeRef(), currentUser);
            LockInfo lockInfo = new LockInfoImpl();
            lockInfo.setTimeoutSeconds(timeoutSeconds);
            lockInfo.setExclusiveLockToken(lockToken);
            lockInfo.setDepth(WebDAV.ZERO);
            lockInfo.setScope(WebDAV.XML_EXCLUSIVE);
            lockInfo.setOwner(currentUser);
            vfs.getLockService().lock(node, LockType.WRITE_LOCK, timeoutSeconds, Lifetime.EPHEMERAL, lockInfo.toJSON());
            // if we locked the node referenced in this fileInfo, update the stored information
            if(node == fileInfo.getNodeRef())
            {
                fileInfo.getProperties().put(ContentModel.PROP_LOCK_OWNER, currentUser);
                fileInfo.getProperties().put(ContentModel.PROP_EXPIRY_DATE, vfs.getNodeService().getProperty(fileInfo.getNodeRef(), ContentModel.PROP_EXPIRY_DATE));
            }
            return true;
        }
        catch(Exception e)
        {
        	AlfrescoVirtualFileSystem.checkForRetryingException(e);
            logger.debug("Failed locking node",e);
            return false;
        }
    }
    
    private boolean unlockNode(NodeRef node)
    {
        LockService lockService = vfs.getLockService();
        LockStatus lockStatus = lockService.getLockStatus(node);
        if(lockStatus == LockStatus.LOCK_OWNER)
        {
            try
            {
                lockService.unlock(node);
                return true;
            }
            catch(UnableToReleaseLockException utrle)
            {
            	AlfrescoVirtualFileSystem.checkForRetryingException(utrle);
                logger.debug("Unlock failed.",utrle);
                return false;
            }
        }
        return false;
    }

    /**
     * Cancel an existing short-term lock (<code>releaseShortTermLock == true</code>) or checkout (<code>releaseShortTermLock == false</code>).
     * 
     * @param releaseShortTermLock <code>true</code> if a short-term lock should be released, <code>false</code> if a checkout should be canceled
     * @param callContext the context this method is called in. One of the <code>CALLCONTEXT_</code> statics.
     * 
     * @return <code>true</code> if and only if this operation succeeded
     */
    @Override
    public boolean uncheckout(boolean releaseShortTermLock, int callContext)
    {
        if(isHistoryNode)
        {
            return false;
        }
        if(releaseShortTermLock)
        {
            return performUnlock(callContext);
        }
        else
        {
            return performUncheckout(callContext);
        }
    }

    private boolean performUnlock(int callContext)
    {
        // if this node is checked out, then we unlock the working copy. otherwise we
        // unlock the node itself
        NodeRef nodeToUnlock = fileInfo.getNodeRef();
        if(isCheckedOut())
        {
            nodeToUnlock = vfs.getCheckOutCheckInService().getWorkingCopy(nodeToUnlock);
        }
        LockStatus lockStatus = vfs.getLockService().getLockStatus(nodeToUnlock);
        if(lockStatus != LockStatus.LOCK_OWNER)
        {
            return false;
        }
        return unlockNode(nodeToUnlock);
    }

    private boolean performUncheckout(int callContext)
    {
        // get the working copy
        NodeRef workingCopy = vfs.getCheckOutCheckInService().getWorkingCopy(fileInfo.getNodeRef());
        if(workingCopy == null)
        {
            return false;
        }

        // check the lock state of the working copy
        LockStatus lockStatus = vfs.getLockService().getLockStatus(workingCopy);
        if(lockStatus == LockStatus.LOCKED)
        {
            return false;
        }
        
        // check if working copy is locked
        boolean reCreateLock = false;
        Date initialExpiryDate = null;
        if(lockStatus == LockStatus.LOCK_OWNER)
        {
            // the working copy is locked by the current user. we need to remove this lock so
            // we can uncheckout this node and then re-create a lock on the remaining node
            reCreateLock = true;
            initialExpiryDate = (Date) vfs.getNodeService().getProperty(workingCopy, ContentModel.PROP_EXPIRY_DATE);
            if(!unlockNode(workingCopy))
            {
                return false;
            }
        }

        // perform cancelCheckoput
        NodeRef previousNode;
        try
        {
            previousNode = vfs.getCheckOutCheckInService().cancelCheckout(workingCopy);
        }
        catch(Exception e)
        {
        	AlfrescoVirtualFileSystem.checkForRetryingException(e);
            logger.debug("Error cancelling check-out",e);
            return false;
        }

        // re-create the lock
        if(reCreateLock && (previousNode != null))
        {
            lockNode(previousNode, calculateTimeout(initialExpiryDate, 3600));
            /*
            if( lockNode(previousNode, 60) && (initialExpiryDate != null) )
            {
                vfs.getNodeService().setProperty(previousNode, ContentModel.PROP_EXPIRY_DATE, initialExpiryDate);
            }
            */
        }

        // success
        return true;
    }

    /**
     * Checks this document back in.
     * 
     * @param comment optional version comment
     * @param keepCheckedOut <code>true</code> if and only if this document should be checked out again
     * @param callContext the context this method is called in. One of the <code>CALLCONTEXT_</code> statics.
     * 
     * @return <code>true</code> if and only if this operation succeeded
     */
    @Override
    public boolean checkin(String comment, boolean keepCheckedOut, int callContext, CheckinType checkinType)
    {
        if(isHistoryNode)
        {
            return false;
        }
        // get the working copy
        NodeRef workingCopy = vfs.getCheckOutCheckInService().getWorkingCopy(fileInfo.getNodeRef());
        if(workingCopy == null)
        {
            return false;
        }

        // check the lock state of the working copy
        LockStatus lockStatus = vfs.getLockService().getLockStatus(workingCopy);
        if(lockStatus == LockStatus.LOCKED)
        {
            return false;
        }

        // check if working copy is locked
        boolean reCreateLock = false;
        Date initialExpiryDate = null;
        if(lockStatus == LockStatus.LOCK_OWNER)
        {
            // the working copy is locked by the current user. we need to remove this lock so
            // we can uncheckout this node and then re-create a lock on the remaining node
            reCreateLock = true;
            initialExpiryDate = (Date) vfs.getNodeService().getProperty(workingCopy, ContentModel.PROP_EXPIRY_DATE);
            if(!unlockNode(workingCopy))
            {
                return false;
            }
        }

        // remove checkout-to-local aspect
        if(vfs.getNodeService().hasAspect(workingCopy, AlfrescoVirtualFileSystem.ASPECT_AOS_CHECKED_OUT_TO_LOCAL))
        {
            vfs.getNodeService().removeAspect(workingCopy, AlfrescoVirtualFileSystem.ASPECT_AOS_CHECKED_OUT_TO_LOCAL);
        }

        // perform check-in
        NodeRef nextNode;
        try
        {
            boolean major = checkinType == CheckinType.MAJOR;
            Map<String, Serializable> versionProperties = new HashMap<String, Serializable>(5);
            versionProperties.put(VersionModel.PROP_VERSION_TYPE, major ? VersionType.MAJOR : VersionType.MINOR);
            if (comment != null)
            {
                versionProperties.put(VersionModel.PROP_DESCRIPTION, comment);
            }
            nextNode = vfs.getCheckOutCheckInService().checkin(workingCopy,versionProperties,null,keepCheckedOut);
        }
        catch(Exception e)
        {
        	AlfrescoVirtualFileSystem.checkForRetryingException(e);
            logger.debug("Error checking in node",e);
            return false;
        }

        // re-create the lock
        if(reCreateLock && (nextNode != null))
        {
            lockNode(nextNode, calculateTimeout(initialExpiryDate, 3600));
        }

        // success
        return true;
    }

    /**
     * Returns the current checkout state of this document as one of the <code>CHECKOUTSTATE_</code> constants.
     * 
     * @param callContext the context this method is called in. One of the <code>CALLCONTEXT_</code> statics.
     * 
     * @return the current checkout state of this document as one of the <code>CHECKOUTSTATE_</code> constants
     */
    @Override
    public int getCheckoutState(int callContext)
    {
        if(isCheckedOut())
        {
            return CHECKOUTSTATE_LONGTERMCHECKOUT;
        }
        if(isLocked())
        {
            return CHECKOUTSTATE_SHORTTERMLOCK;
        }
        return CHECKOUTSTATE_NONE;
    }

    /**
     * Returns the name of the user that owns the checkout of this document or <code>null</code> if this document is not checked out.
     * 
     * @param callContext the context this method is called in. One of the <code>CALLCONTEXT_</code> statics.
     * 
     * @return the name of the user that owns the checkout of this document or <code>null</code> if this document is not checked out
     */
    @Override
    public String getCheckoutOwner(int callContext)
    {
        CheckOutCheckInService checkOutCheckInService = vfs.getCheckOutCheckInService();
        if(!checkOutCheckInService.isCheckedOut(fileInfo.getNodeRef()))
        {
            return null;
        }
        return (String) vfs.getNodeService().getProperty(fileInfo.getNodeRef(), ContentModel.PROP_LOCK_OWNER);
    }

    /**
     * Returns the lock token if this document is locked or <code>null</code> otherwise.<br/>
     * It is sufficient to just return a static string when this document is checked out and <code>null</code> if not.
     * 
     * @param currentLockToken the current lock token sent by the caller with this WebDAV LOCK command
     * @param callContext the context this method is called in. One of the <code>CALLCONTEXT_</code> statics.
     * 
     * @return the lock token if this document is locked or <code>null</code> otherwise
     */
    @Override
    public String getWebdavLockToken(String currentLockToken, int callContext)
    {
        // TODO: If this document is locked by a different user or not locked at all,
        // TODO: this method returns a wrong result.
        String currentUser = vfs.getAuthenticationService().getCurrentUserName();
        return WebDAV.makeLockToken(fileInfo.getNodeRef(), currentUser);
    }

    
    // ===== content

    
    protected ContentResponse getContentResponse()
    {
        if(contentResponse == null)
        {
            contentResponse = createContentResponse();
        }
        return contentResponse;
    }
    
    protected ContentResponse createContentResponse()
    {
        NodeRef nodeToRead = fileInfo.getNodeRef();
        if(isCheckedOut())
        {
            nodeToRead = vfs.getCheckOutCheckInService().getWorkingCopy(nodeToRead);
        }
        ContentReader contentReader = vfs.getFileFolderService().getReader(nodeToRead);
        if(contentReader == null)
        {
            return new EmptyFileContentResponse();
        }
        IOContentFilterRegistry ioContentFilterRegistry = vfs.getIoContentFilterRegistry();
        if(ioContentFilterRegistry != null)
        {
            ContentFilter contentFilter = ioContentFilterRegistry.getOutputFilter(nodeToRead, contentReader);
            if(contentFilter != null)
            {
                ContentResponse contentResponse = FilteredContentResponse.createContentResponse(nodeToRead, contentReader, contentFilter);
                if(contentResponse != null)
                {
                    return contentResponse;
                }
                // we have already called getInputStream on this ContentReader. We need to get a new one.
                contentReader = vfs.getFileFolderService().getReader(nodeToRead);
                if(contentReader == null)
                {
                    return new EmptyFileContentResponse();
                }
            }
        }
        return new RepositoryContentResponse(contentReader);
    }
    
    /**
     * Write the octet stream of this file to the given <code>OutputStream</code>.
     * 
     * @param outputStream the stream to write the contents to
     * @param callContext the context this method is called in. One of the <code>CALLCONTEXT_</code> statics.
     */
    @Override
    public void emitContent(OutputStream outputStream, int callContext)
    {
        try
        {
            getContentResponse().emitContent(outputStream);
        }
        catch (ContentIOException | IOException e)
        {
            logger.debug("Error reading content from node",e);
        }
    }

    /**
     * Write a part of the octet stream of this file to the given <code>OutputStream</code>.
     * 
     * @param outputStream the stream to write the contents to
     * @param start the first byte to be written, counted from 0 being the first byte  of this file
     * @param end the last byte to be written counted until <code>getSize()-1</code> being the last byte of this file
     * @param multipartHint flag set to true if this is only one content range within a series in a multipart range request
     * @param callContext the context this method is called in. One of the <code>CALLCONTEXT_</code> statics.
     */
    @Override
    public void emitContentRange(OutputStream outputStream, long start, long end, boolean multipartHint, int callContext)
    {
        try
        {
            InputStream contentInputStream = getContentResponse().getContentInputStream();
            if(contentInputStream == null)
            {
                // no content property -- empty file
                return;
            }
            try
            {
                copyRange(contentInputStream, outputStream, start, end);
            }
            finally
            {
                contentInputStream.close();
            }
        }
        catch (ContentIOException e)
        {
            logger.debug("Error reading content from node",e);
        }
        catch (IOException e)
        {
            logger.debug("Error writing content to output",e);
        }
    }

    protected void copyRange(InputStream in, OutputStream out, long start, long end) throws IOException
    {
        // skip bytes at the beginning
        in.skip(start);

        byte[] buffer = new byte[1024];
        int num = 0;
        long remaining = end - start + 1;

        while ((remaining > 0) && ((num = in.read(buffer, 0, (int) Math.min(1024, remaining))) > 0)) // cast from long to int is always safe, as the Math.min limits the long to 1024
        {
            out.write(buffer, 0, num);
            remaining -= num;
        }
    }

    /**
     * Store the octet stream in the VermeerRequest as new content of this document, optionally by creating a new version.
     * 
     * @param vermeerRequest the <code>{@link VermeerRequest}</code> containing the octet stream with the new content
     * @param callContext the context this method is called in. One of the <code>CALLCONTEXT_</code> statics.
     * 
     * @return <code>true</code> if and only if this operation succeeded
     */
    @Override
    public boolean storeContent(VermeerRequest vermeerRequest, int callContext)
    {
        return storeContent(vermeerRequest.getAttachedFileInputStream(), callContext);
    }

    /**
     * Store the given octet stream as new content of this document, optionally by creating a new version.
     * 
     * @param content the <code>InputStream</code> containing the octet stream with the new content
     * @param callContext the context this method is called in. One of the <code>CALLCONTEXT_</code> statics.
     * 
     * @return <code>true</code> if and only if this operation succeeded
     */
    @Override
    public boolean storeContent(InputStream in, int callContext)
    {
        if(isHistoryNode)
        {
            return false;
        }
        NodeRef nodeToWriteTo = fileInfo.getNodeRef();
        if(isCheckedOut())
        {
            nodeToWriteTo = vfs.getCheckOutCheckInService().getWorkingCopy(nodeToWriteTo);
        }
        boolean hadContentPropertyBeforeUpdate = false;
        File tempFile = null;
        FileInputStream tempFileInputStream = null;
        InputStream content = null;
        ContentPostProcessor postProcessor = null;
        IOContentFilterRegistry ioContentFilterRegistry = vfs.getIoContentFilterRegistry();
        try
        {

        	final NodeRef finalNodeToWriteTo = nodeToWriteTo;
            // If we already have content, we need to activate versioning. But we can only create one version within a transaction.
            // So we need to turn on versioning in a second sub-transaction outside the main transaction controlled by the ServiceFilter
            vfs.getTransactionService().getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Void>()
            {
                @Override
                public Void execute() throws Throwable
                {
                    if( activateVersioningOnWrite && (!vfs.getNodeService().hasAspect(finalNodeToWriteTo, ContentModel.ASPECT_VERSIONABLE)) && (vfs.getNodeService().getProperty(finalNodeToWriteTo, ContentModel.PROP_CONTENT) != null) )
                    {
                        Map<QName, Serializable> initialVersionProps = new HashMap<QName, Serializable>(1, 1.0f);
                        vfs.getVersionService().ensureVersioningEnabled(fileInfo.getNodeRef(), initialVersionProps);
                    }
                    return null;
                }
            }, false, true);
                        
            ContentData contentData = (ContentData)vfs.getNodeService().getProperty(nodeToWriteTo, ContentModel.PROP_CONTENT);
            hadContentPropertyBeforeUpdate = contentData != null;
            if(ioContentFilterRegistry != null)
            {
                // Ordinarily, we read directly from the input and do not provide a preview for input filters
                InputStream readFromStream = in;
                PushbackInputStream previewStream = null;
                // However, if one of the filters requests a preview, we create a PushbackInputStream for preview and use the same stream to read from
                int previewBytes = ioContentFilterRegistry.getMaxInputPreviewByteCount();
                if(previewBytes > 0)
                {
                    previewStream = new PushbackInputStream(in, previewBytes);
                    readFromStream = previewStream;
                }
                ContentFilter filter = ioContentFilterRegistry.getInputFilter(null, previewStream, nodeToWriteTo);
                if(filter != null)
                {
                    tempFile = TempFileProvider.createTempFile(readFromStream, "AosDocumentNodeInput", "tmp");
                    tempFileInputStream = new FileInputStream(tempFile);
                    ContentFilterProcessingResult processingResult = filter.process(nodeToWriteTo, tempFileInputStream, null, true);
                    if(processingResult != null)
                    {
                        postProcessor = processingResult.getPostProcessor();
                    }
                    tempFileInputStream.close();
                    tempFileInputStream = new FileInputStream(tempFile);
                    content = tempFileInputStream;
                }
                else
                {
                    content = readFromStream;
                }
            }
            else
            {
                content = in;
            }
            ContentWriter writer = vfs.getFileFolderService().getWriter(nodeToWriteTo);
            writer.guessMimetype(fileInfo.getName());
            writer.guessEncoding();
            writer.putContent(content);
            if(postProcessor != null)
            {
                postProcessor.execute(nodeToWriteTo);
            }
            if(activateVersioningOnWrite && !vfs.getNodeService().hasAspect(nodeToWriteTo, ContentModel.ASPECT_VERSIONABLE))
            {
                Map<QName, Serializable> initialVersionProps = new HashMap<QName, Serializable>(1, 1.0f);
                vfs.getVersionService().ensureVersioningEnabled(fileInfo.getNodeRef(), initialVersionProps);
            }
            
        }
        catch(Throwable t)
        {
        	AlfrescoVirtualFileSystem.checkForRetryingException(t);
            logger.debug("Error writing content.",t);
            return false;
        }
        finally
        {
            if(tempFileInputStream != null)
            {
                try
                {
                    tempFileInputStream.close();
                }
                catch(IOException ioe)
                {
                    ;
                }
            }
            if(tempFile != null)
            {
                tempFile.delete();
            }
        }
        // post activity
        try
        {
        	if(hadContentPropertyBeforeUpdate)
        	{
                // don't post activity if the update is outside of sites
                // ACE-3082
                String siteId = getSiteId();
                if (siteId != null && !siteId.isEmpty())
                {
                    vfs.getActivityPoster().postFileFolderUpdated(siteId, vfs.getTenantDomain(), vfs.getFileFolderService().getFileInfo(nodeToWriteTo));
                }
        	}
        	else
        	{
                Pair<String, String> activitySiteAndPath = vfs.getActivitySiteAndPath(nodeToWriteTo);
                if(!WebDAVHelper.EMPTY_SITE_ID.equals(activitySiteAndPath.getFirst()))
                {
                    vfs.getActivityPoster().postFileFolderAdded(activitySiteAndPath.getFirst(), vfs.getTenantDomain(), activitySiteAndPath.getSecond(), vfs.getFileFolderService().getFileInfo(nodeToWriteTo));
                }
        	}
        }
        catch (Exception e)
        {
            logger.error("DocumentNode.storeContent: Error posting activity.",e);
        }
        return true;
    }

    
    // ===== versions


    public VersionNumber getVersionNumber()
    {
        VersionNumber versionNumber = VersionNumber.parseSafe((String)vfs.getNodeService().getProperty(fileInfo.getNodeRef(), ContentModel.PROP_VERSION_LABEL));
        if(isCheckedOut())
        {
            versionNumber = versionNumber.getNextMinor();
        }
        return versionNumber;
    }
    
    /**
     * Returns a <code>List</code> of <code>{@link VFSDocumentVersion}</code> objects
     * describing all versions of this document.
     * 
     * @param callContext the context this method is called in. One of the <code>CALLCONTEXT_</code> statics.
     * 
     * @return a <code>List</code> of <code>{@link VFSDocumentVersion}</code> objects
     */
    @Override
    public List<?> getVersions(int callContext)
    {
        List<VFSDocumentVersion> result = new ArrayList<VFSDocumentVersion>();
        boolean hasWorkingCopy = isCheckedOut();
        VersionNumber maxVersion = VersionNumber.INITIAL;
        // add the version history
        VersionHistory versionHistory = vfs.getVersionService().getVersionHistory(fileInfo.getNodeRef());
        if(versionHistory == null)
        {
            // add this version
            String versionLabel = (String)vfs.getNodeService().getProperty(fileInfo.getNodeRef(), ContentModel.PROP_VERSION_LABEL);
            if(versionLabel == null)
            {
                versionLabel = VersionNumber.INITIAL.toString();
            }
            else
            {
                maxVersion = VersionNumber.max(maxVersion, VersionNumber.parseSafe(versionLabel));
            }
            Date dateCreated = (Date)vfs.getNodeService().getProperty(fileInfo.getNodeRef(), ContentModel.PROP_CREATED);
            String userCreated = (String)vfs.getNodeService().getProperty(fileInfo.getNodeRef(), ContentModel.PROP_CREATOR);
            ContentReader reader = vfs.getFileFolderService().getReader(fileInfo.getNodeRef());
            long fileSize = (reader==null) ? 0l : reader.getSize();
            String path = relativePath;
            if(hasWorkingCopy)
            {
                path = Const.HISTORY_PATH_ELEMENT + "/" + versionLabel + relativePath;
            }
            else
            {
                versionLabel = "@"+versionLabel;
            }
            result.add(new VFSDocumentVersion(versionLabel,path,dateCreated,userCreated,fileSize,""));
        }
        else
        {
            // convert version history
            Version headVersion = versionHistory.getHeadVersion();
            for (Version version : versionHistory.getAllVersions())
            {
                String versionLabel = version.getVersionLabel();
                if(versionLabel == null)
                {
                    versionLabel = VersionNumber.INITIAL.toString();
                }
                else
                {
                    maxVersion = VersionNumber.max(maxVersion, VersionNumber.parseSafe(versionLabel));
                }
                Date dateCreated = version.getFrozenModifiedDate();
                String userCreated = version.getFrozenModifier();
                String comment = version.getDescription();
                ContentReader reader = vfs.getFileFolderService().getReader(version.getFrozenStateNodeRef());
                long fileSize = (reader==null) ? 0l : reader.getSize();
                String path = Const.HISTORY_PATH_ELEMENT + "/" + versionLabel + relativePath;
                if(!hasWorkingCopy && version.equals(headVersion))
                {
                    versionLabel = "@"+versionLabel;
                    path = relativePath;
                }
                result.add(new VFSDocumentVersion(versionLabel,path,dateCreated,userCreated,fileSize,comment));
            }
        }
        // add the working copy, if any
        if(hasWorkingCopy)
        {
            NodeRef workingCopy = vfs.getCheckOutCheckInService().getWorkingCopy(fileInfo.getNodeRef());
            String versionLabel = "@"+maxVersion.getNextMinor().toString();
            Date dateCreated = (Date)vfs.getNodeService().getProperty(workingCopy, ContentModel.PROP_CREATED);
            String userCreated = (String)vfs.getNodeService().getProperty(workingCopy, ContentModel.PROP_CREATOR);
            ContentReader reader = vfs.getFileFolderService().getReader(workingCopy);
            long fileSize = (reader==null) ? 0l : reader.getSize();
            String path = relativePath;
            result.add(new VFSDocumentVersion(versionLabel,path,dateCreated,userCreated,fileSize,""));
        }
        return result;
    }

    
    // ===== miscellaneous

    
    /**
     * Rename this document and return the result of this operation (<code>true</code> for success and <code>false</code> in the event of an error).
     * 
     * @param newName the new name of the node
     * @param callContext the context this method is called in. One of the <code>CALLCONTEXT_</code> statics.
     * 
     * @return the result of this operation (<code>true</code> for success and <code>false</code> in the event of an error)
     */
    @Override
    public boolean rename(String newName, int callContext)
    {
        if(isHistoryNode)
        {
            return false;
        }
        FileFolderService fileFolderService = vfs.getFileFolderService();
        try
        {
            fileFolderService.rename(fileInfo.getNodeRef(), newName);
            return true;
        }
        catch (FileExistsException e)
        {
            logger.debug("Error renaming node",e);
            return false;
        }
        catch (FileNotFoundException e)
        {
            logger.debug("Error renaming node",e);
            return false;
        }
    }

    /**
     * Delete this document and return the result of this operation (<code>true</code> for success and <code>false</code> in the event of an error).
     * 
     * @param callContext the context this method is called in. One of the <code>CALLCONTEXT_</code> statics.
     * 
     * @return the result of this operation (<code>true</code> for success and <code>false</code> in the event of an error)
     */
    @Override
    public boolean delete(int callContext)
    {
        if(isHistoryNode)
        {
            return false;
        }
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
                logger.error("DocumentNode.delete: Error posting activity.",e);
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
        if(isHistoryNode)
        {
            return false;
        }
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
                return false;
            }
            if(!destination.isFolder() || destination.isLink())
            {
                return false;
            }
            vfs.getFileFolderService().move(fileInfo.getNodeRef(), destination.getNodeRef(), newName);
            return true;
        }
        catch (Exception e)
        {
        	AlfrescoVirtualFileSystem.checkForRetryingException(e);
            logger.debug("Error moving node",e);
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

    
    // ===== used by ListsService to return ContentType information for Acrobat

    
    public String getContentTypeName()
    {
        QName nodeTypeQname = vfs.getNodeService().getType(fileInfo.getNodeRef());
        ClassDefinition classDefinition = vfs.getDictionaryService().getClass(nodeTypeQname);
        String contentTypeName = classDefinition.getTitle(vfs.getDictionaryService());
        if( (contentTypeName == null) || contentTypeName.isEmpty())
        {
            contentTypeName = nodeTypeQname.getLocalName();
        }
        return contentTypeName;
    }

    public ContentTypeId getContentTypeId()
    {
        return ContentTypeId.DOCUMENT.getChild(Guid.parse(fileInfo.getNodeRef().getId()));
    }

    
    // ===== the following methods are only required for folder nodes

    
    @Override
    public VFSNode getContainmentByName(UserData userData, String name, int callContext)
    {
        // this VFSNode does not contain any sub-nodes
        return null;
    }

    @Override
    public List<?> getEnumerableContainees(UserData userData, int callContext)
    {
        // this VFSNode does not contain any sub-nodes
        return null;
    }

    @Override
    public VFSDocumentNode createNewFile(UserData userData, String filename, VermeerRequest request, int callContext)
    {
        // this VFSNode can not contain any sub-nodes
        return null;
    }

    @Override
    public VFSDocumentNode createNewFile(UserData userData, String filename, InputStream content, int callContext)
    {
        // this VFSNode can not contain any sub-nodes
        return null;
    }

    @Override
    public VFSNode createNewFolder(UserData userData, String foldername, int callContext)
    {
        // this VFSNode can not contain any sub-nodes
        return null;
    }
    
}

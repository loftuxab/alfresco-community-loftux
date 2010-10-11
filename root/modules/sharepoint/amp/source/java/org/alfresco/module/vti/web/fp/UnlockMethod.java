/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.module.vti.web.fp;

import java.util.LinkedList;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.model.ContentModel;
import org.alfresco.model.WebDAVModel;
import org.alfresco.repo.webdav.LockInfo;
import org.alfresco.repo.webdav.WebDAV;
import org.alfresco.repo.webdav.WebDAVServerException;
import org.alfresco.service.cmr.lock.LockService;
import org.alfresco.service.cmr.lock.LockStatus;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;

/**
 * Implements the WebDAV UNLOCK method with VTI specific
 * 
 * @author DmitryVas
 */
public class UnlockMethod extends org.alfresco.repo.webdav.UnlockMethod
{
    private String alfrescoContext;

    private String m_strLockToken = null;

    public UnlockMethod(String alfrescoContext)
    {
        this.alfrescoContext = alfrescoContext;
    }

    /**
     * Execute the request
     * 
     * @exception WebDAVServerException
     */
    protected void executeImpl() throws WebDAVServerException
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Lock node; path=" + getPath() + ", token=" + getLockToken());
        }

        FileInfo lockNodeInfo = null;
        try
        {
            lockNodeInfo = getDAVHelper().getNodeForPath(getRootNodeRef(), getPath(), alfrescoContext);
            NodeRef workingCopy = getServiceRegistry().getCheckOutCheckInService().getWorkingCopy(lockNodeInfo.getNodeRef());
            if (workingCopy != null)
            {
                String workingCopyOwner = getNodeService().getProperty(workingCopy, ContentModel.PROP_WORKING_COPY_OWNER).toString();
                if (workingCopyOwner.equals(getAuthenticationService().getCurrentUserName()))
                {
                    lockNodeInfo = getFileFolderService().getFileInfo(workingCopy);
                }
            }
        }
        catch (FileNotFoundException e)
        {
            throw new WebDAVServerException(HttpServletResponse.SC_NOT_FOUND);
        }

        // Parse the lock token
        String[] lockInfo = WebDAV.parseLockToken(getLockToken());
        if (lockInfo == null)
        {
            // Bad lock token
            throw new WebDAVServerException(HttpServletResponse.SC_PRECONDITION_FAILED);
        }

        // Get the lock status for the node
        LockService lockService = getDAVHelper().getLockService();
        NodeService nodeService = getNodeService();
        // String nodeId = lockInfo[0];
        // String userName = lockInfo[1];

        LockStatus lockSts = lockService.getLockStatus(lockNodeInfo.getNodeRef());
        if (lockSts == LockStatus.LOCK_OWNER)
        {
            // Unlock the node
            if (!nodeService.hasAspect(lockNodeInfo.getNodeRef(), ContentModel.ASPECT_WORKING_COPY))
            {
                lockService.unlock(lockNodeInfo.getNodeRef());
            } 
            nodeService.removeProperty(lockNodeInfo.getNodeRef(), WebDAVModel.PROP_OPAQUE_LOCK_TOKEN);
            nodeService.removeProperty(lockNodeInfo.getNodeRef(), WebDAVModel.PROP_LOCK_DEPTH);
            nodeService.removeProperty(lockNodeInfo.getNodeRef(), WebDAVModel.PROP_LOCK_SCOPE);

            // Indicate that the unlock was successful
            m_response.setStatus(HttpServletResponse.SC_NO_CONTENT);

            // DEBUG
            if (logger.isDebugEnabled())
            {
                logger.debug("Unlock token=" + getLockToken() + " Successful");
            }
        }
        else if (lockSts == LockStatus.NO_LOCK)
        {
            String sharedLocks = (String) nodeService.getProperty(lockNodeInfo.getNodeRef(), WebDAVModel.PROP_SHARED_LOCK_TOKENS);
            if (sharedLocks != null)
            {
                LinkedList<String> locks = LockInfo.parseSharedLockTokens(sharedLocks);

                if (locks != null && locks.contains(m_strLockToken))
                {
                    locks.remove(m_strLockToken);
                    nodeService.setProperty(lockNodeInfo.getNodeRef(), WebDAVModel.PROP_SHARED_LOCK_TOKENS, LockInfo.makeSharedLockTokensString(locks));

                    // Indicate that the unlock was successful
                    m_response.setStatus(HttpServletResponse.SC_NO_CONTENT);

                    // DEBUG
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Unlock token=" + getLockToken() + " Successful");
                    }
                }
            }
            else
            {
                // DEBUG
                if (logger.isDebugEnabled())
                    logger.debug("Unlock token=" + getLockToken() + " Not locked");

                // Node is not locked
                throw new WebDAVServerException(HttpServletResponse.SC_PRECONDITION_FAILED);
            }

        }
        else if (lockSts == LockStatus.LOCKED)
        {
            // DEBUG
            if (logger.isDebugEnabled())
                logger.debug("Unlock token=" + getLockToken() + " Not lock owner");

            // Node is locked but not by this user
            throw new WebDAVServerException(HttpServletResponse.SC_PRECONDITION_FAILED);
        }
        else if (lockSts == LockStatus.LOCK_EXPIRED)
        {
            // DEBUG
            if (logger.isDebugEnabled())
                logger.debug("Unlock token=" + getLockToken() + " Lock expired");

            // Return a success status
            m_response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        }
    }
}

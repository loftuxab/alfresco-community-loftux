/*
 * Copyright 2005-2015 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.officeservices.service;

import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.enterprise.repo.officeservices.vfs.AlfrescoVirtualFileSystem;
import org.alfresco.enterprise.repo.officeservices.vfs.DocumentNode;
import org.alfresco.enterprise.repo.officeservices.vfs.FolderNode;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.repo.version.VersionModel;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.cmr.version.VersionHistory;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.cmr.version.VersionType;
import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.xaldon.officeservices.StandardVersionsService;
import com.xaldon.officeservices.UserData;
import com.xaldon.officeservices.datamodel.Guid;
import com.xaldon.officeservices.exceptions.AuthenticationRequiredException;
import com.xaldon.officeservices.protocol.SimpleSoapParser;
import com.xaldon.officeservices.vfs.VFSDocumentNode;
import com.xaldon.officeservices.vfs.VFSNode;
import com.xaldon.officeservices.vfs.VirtualFileSystem;

public class VersionsService extends StandardVersionsService
{
    
    private static final long serialVersionUID = 6141598435401635384L;
    
    protected VirtualFileSystem vfs;
    
    protected AuthenticationService authenticationService;
    
    protected Logger logger = Logger.getLogger(this.getClass());

    // initialization

    @Override
    public void init() throws ServletException
    {
        super.init();
        
        WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        if(wac == null)
        {
            throw new ServletException("Error initializing Servlet. No WebApplicationContext available.");
        }
        
        vfs = (VirtualFileSystem) wac.getBean("AosVirtualFileSystem");
        if(vfs == null)
        {
            throw new ServletException("Cannot find bean AosVirtualFileSystem in WebApplicationContext.");
        }
        ((AlfrescoVirtualFileSystem)vfs).prepare();
        authenticationService = (AuthenticationService) wac.getBean("AuthenticationService");
        if(authenticationService == null)
        {
            throw new ServletException("Cannot find bean AuthenticationService in WebApplicationContext.");
        }
    }
    
    // transaction

    @Override
    public void soapService(final UserData userData, final String methodName, final SimpleSoapParser parser, final HttpServletRequest request, final HttpServletResponse response) throws IOException, AuthenticationRequiredException
    {
        try
        {
            ((AlfrescoVirtualFileSystem)vfs).getTransactionService().getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Void>()
            {
                @Override
                public Void execute() throws Throwable
                {
                    VersionsService.super.soapService(userData, methodName, parser, request, response);
                    return null;
                }
            });
        }
        catch(Throwable t)
        {
        	try
        	{
        		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        	}
        	catch(Exception e)
        	{
        		;
        	}
        	
        }
    }
    
    // file system
    
    @Override
    public VirtualFileSystem getVirtualFileSystem(UserData userData) throws AuthenticationRequiredException
    {
        return vfs;
    }

    protected String getSitePrefix(HttpServletRequest request)
    {
        return ((AlfrescoVirtualFileSystem)vfs).getSitePrefix(request);
    }

    @Override
    public DateFormat getDateFormat()
    {
        return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    }

    @Override
    public String getSettingsURL(UserData userData, String baseUrl, String filename)
    {
        return "";
    }

    @Override
    public String getListGUID(UserData userData, String filename)
    {
        VFSNode libraryFolder = null;
        String libraryPath = AlfrescoVirtualFileSystem.getFirstFolder(AlfrescoVirtualFileSystem.normalizePath(filename));
        if(libraryPath != null)
        {
            try
            {
                libraryFolder = vfs.getNodeByPath(null, libraryPath, VFSNode.CALLCONTEXT_VERSIONSSERVICE);
                if(libraryFolder instanceof VFSDocumentNode)
                {
                    libraryFolder = null;
                }
            }
            catch (Exception e)
            {
                libraryFolder = null;
            }
        }
        Guid listId = libraryFolder == null ? AlfrescoVirtualFileSystem.LISTID_ROOT_DOCUMENTS : Guid.parse(((FolderNode)libraryFolder).getFileInfo().getNodeRef().getId());
        return listId.toString();
    }

    @Override
    public boolean restoreVersion(UserData userData, VFSDocumentNode document, String fileVersion)
    {
        VersionService versionService = ((AlfrescoVirtualFileSystem)vfs).getVersionService();
        try
        {

            // revert to version
            FileInfo fileInfo = ((DocumentNode)document).getFileInfo();
            VersionHistory versionHistory = ((AlfrescoVirtualFileSystem)vfs).getVersionService().getVersionHistory(fileInfo.getNodeRef());
            Version version = versionHistory.getVersion(fileVersion);
            versionService.revert(fileInfo.getNodeRef(), version);

            // set as MAJOR version
            Map<String, Serializable> props = new HashMap<String, Serializable>(1, 1.0f);
            props.put(Version.PROP_DESCRIPTION, "");
            props.put(VersionModel.PROP_VERSION_TYPE, VersionType.MAJOR);
            versionService.createVersion(fileInfo.getNodeRef(), props);

        }
        catch (Exception e)
        {
            logger.debug("Error restoring document version",e);
            return false;
        }
        return true;
    }

    // Authentication

    @Override
    public UserData negotiateAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        return new AuthenticationServiceUserData(authenticationService);
    }

    @Override
    public void requestAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        // not required
    }

    @Override
    public void invalidateAuthentication(UserData userData, HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        // not required
    }

}

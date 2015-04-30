/*
 * Copyright 2005-2015 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.officeservices.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.enterprise.repo.officeservices.metadata.DataModelMapper;
import org.alfresco.enterprise.repo.officeservices.vfs.AlfrescoVirtualFileSystem;
import org.alfresco.enterprise.repo.officeservices.vfs.DocumentNode;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.xaldon.officeservices.FieldInformation;
import com.xaldon.officeservices.StandardCopyService;
import com.xaldon.officeservices.UserData;
import com.xaldon.officeservices.datamodel.FieldValue;
import com.xaldon.officeservices.exceptions.AuthenticationRequiredException;
import com.xaldon.officeservices.protocol.SimpleSoapParser;
import com.xaldon.officeservices.vfs.VFSDocumentNode;
import com.xaldon.officeservices.vfs.VirtualFileSystem;

public class CopyService extends StandardCopyService
{
    
    private static final long serialVersionUID = 960908140050767636L;

    protected VirtualFileSystem vfs;

    protected DataModelMapper dataModelMapper;
    
    protected AuthenticationService authenticationService;

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
        dataModelMapper = (DataModelMapper) wac.getBean("aosServerPropertiesProvider");
        if(dataModelMapper == null)
        {
            throw new ServletException("Cannot find bean aosServerPropertiesProvider in WebApplicationContext.");
        }
        authenticationService = (AuthenticationService) wac.getBean("AuthenticationService");
        if(authenticationService == null)
        {
            throw new ServletException("Cannot find bean AuthenticationService in WebApplicationContext.");
        }
    }
    
    // file system

    @Override
    protected String getSitePrefix(HttpServletRequest request)
    {
        return ((AlfrescoVirtualFileSystem)vfs).getSitePrefix(request);
    }
    
    @Override
    public VirtualFileSystem getVirtualFileSystem(UserData userData) throws AuthenticationRequiredException
    {
        return vfs;
    }

    @Override
    protected String preProcessRequestedPath(String requestedPath)
    {
        requestedPath = AlfrescoVirtualFileSystem.normalizePath(requestedPath);
        return requestedPath;
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
                    CopyService.super.soapService(userData, methodName, parser, request, response);
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
    
    // fields

    @Override
    protected List<?> getFieldInformations(UserData userData, VFSDocumentNode document)
    {
        Collection<FieldValue> fieldValues = dataModelMapper.getFieldValues(((DocumentNode)document).getFileInfo().getNodeRef());
        ArrayList<FieldInformation> result = new ArrayList<FieldInformation>(fieldValues.size());
        for(FieldValue fieldValue : fieldValues)
        {
            result.add(new FieldInformation(fieldValue));
        }
        return result;
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

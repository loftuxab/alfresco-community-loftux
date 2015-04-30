/*
 * Copyright 2005-2015 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.officeservices.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.enterprise.repo.officeservices.vfs.AlfrescoVirtualFileSystem;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.xaldon.officeservices.UserData;
import com.xaldon.officeservices.dws.StandardDwsService;
import com.xaldon.officeservices.exceptions.AuthenticationRequiredException;
import com.xaldon.officeservices.protocol.SimpleSoapParser;
import com.xaldon.officeservices.vfs.VirtualFileSystem;

public class DwsService extends StandardDwsService
{

    private static final long serialVersionUID = -4734382943050248988L;

    protected VirtualFileSystem vfs;
    
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
                    DwsService.super.soapService(userData, methodName, parser, request, response);
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

    // URLs for links provided in the DocumentWorkSpace
    
    @Override
    public String getUserInfoUrl(String baseUrl, String document, String id)
    {
        // This URL is displayed by MSO2003 in the "Document Workspace" pane. We just return the baseUrl
        // pointing in the context root of this deployment and send the user to the alfresco web client.
        return baseUrl;
    }

    @Override
    public String getPermsUrl(String baseUrl, String document, String id)
    {
        // This URL is displayed by MSO2003 in the "Document Workspace" pane. We just return the baseUrl
        // pointing in the context root of this deployment and send the user to the alfresco web client.
        return baseUrl;
    }

    @Override
    public String getSettingUrl(String baseUrl, String document, String id)
    {
        // This URL is displayed by MSO2003 in the "Document Workspace" pane. We just return the baseUrl
        // pointing in the context root of this deployment and send the user to the alfresco web client.
        return baseUrl;
    }

    @Override
    public String getSubscribeUrl(String baseUrl, String document, String id)
    {
        // This URL is displayed by MSO2003 in the "Document Workspace" pane. We just return the baseUrl
        // pointing in the context root of this deployment and send the user to the alfresco web client.
        return baseUrl;
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

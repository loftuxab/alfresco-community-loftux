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
import org.alfresco.service.transaction.TransactionService;
import org.apache.chemistry.opencmis.server.shared.ThresholdOutputStreamFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.xaldon.officeservices.StandardAuthorService;
import com.xaldon.officeservices.UserData;
import com.xaldon.officeservices.exceptions.AuthenticationRequiredException;
import com.xaldon.officeservices.protocol.VermeerRequest;
import com.xaldon.officeservices.protocol.VermeerResponse;
import com.xaldon.officeservices.vfs.VirtualFileSystem;

public class AuthorService extends StandardAuthorService
{
    
    private static final long serialVersionUID = -3248232004872352862L;

    protected AlfrescoVirtualFileSystem vfs;
    
    protected AuthenticationService authenticationService;

    protected TransactionService transactionService;

    protected ThresholdOutputStreamFactory streamFactory;

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
        
        vfs = (AlfrescoVirtualFileSystem) wac.getBean("AosVirtualFileSystem");
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
        transactionService = vfs.getTransactionService();
        streamFactory = vfs.createStreamFactory();
    }
    
    // file system
    
    @Override
    protected String getServiceName(VermeerRequest vermeerRequest)
    {
        return ((AlfrescoVirtualFileSystem)vfs).getSitePath(vermeerRequest.getRequest());
    }

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
    
    // Transaction

    @Override
    protected void handleUnexpectedException(Exception e, VermeerRequest vermeerRequest, VermeerResponse vermeerResponse)
    {
    	AlfrescoVirtualFileSystem.checkForRetryingException(e);
        super.handleUnexpectedException(e, vermeerRequest, vermeerResponse);
    }
    
    @Override
    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException
    {
        final BufferedHttpServletRequest bufferedRequest = new BufferedHttpServletRequest((HttpServletRequest)request, streamFactory);
        try
        {
            transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Void>()
            {
                @Override
                public Void execute() throws Throwable
                {
                	AuthorService.super.doPost(bufferedRequest, response);
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
        finally
        {
        	bufferedRequest.close();
        }
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

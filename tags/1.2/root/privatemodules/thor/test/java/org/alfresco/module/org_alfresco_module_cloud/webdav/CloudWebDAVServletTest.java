/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.module.org_alfresco_module_cloud.webdav;

import static javax.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.module.org_alfresco_module_cloud.accounts.Account;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountClass;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService;
import org.alfresco.module.org_alfresco_module_cloud.directory.DirectoryService;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.test_category.SharedJVMTestsCategory;
import org.apache.commons.lang.reflect.FieldUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * Tests for the CloudWebDAVServlet class.
 * 
 * @author Matt Ward
 */
@RunWith(MockitoJUnitRunner.class)
@Category(SharedJVMTestsCategory.class)
public class CloudWebDAVServletTest
{
    private CloudWebDAVServlet servlet;
    private @Mock AccountService accountService;
    private MockHttpServletRequest req;
    private MockHttpServletResponse resp;
    private boolean ranServiceImpl;
    private CloudWebDAVHelper davHelper;
    private @Mock ServiceRegistry serviceRegistry;
    private @Mock AuthenticationService authService;
    private @Mock DirectoryService directoryService;
    private @Mock TenantService tenantService;
    private @Mock Account account;
    // TODO: applicable tests should be refactored into CommonRequestHandlingTest then this can be mocked.
    private CommonRequestHandling commonRequestHandling;
    
    @Before
    public void setUp() throws IllegalAccessException, ServletException, IOException
    {
        servlet = new CloudWebDAVServletEx();
        davHelper = new CloudWebDAVHelper();
        davHelper.setUrlPathPrefix("");
        davHelper.setServiceRegistry(serviceRegistry);
        davHelper.setAuthenticationService(authService);
        davHelper.setDirectoryService(directoryService);
        davHelper.setAccountService(accountService);
        davHelper.setPremiumAccountsOnly(true);
        davHelper.setTenantService(tenantService);
        FieldUtils.writeField(servlet, "m_davHelper", davHelper, true);
        FieldUtils.writeField(servlet, "common", commonRequestHandling, true);
        
        ranServiceImpl = false;
        
        when(account.getAccountClassName()).
            thenReturn((AccountClass.Name.PAID_BUSINESS.toString()));
        
        when(accountService.getAccount(0)).
            thenReturn(account);
        
        commonRequestHandling = new CommonRequestHandling();
        commonRequestHandling.setDavHelper(davHelper);
        servlet.setCommon(commonRequestHandling);
    }
    
    @Test
    public void resourcesOutsideDocumentLibraryReadOnly() throws ServletException, IOException
    {
        canReadWrite("/network-name/site-name/documentLibrary/folder");
        canReadWrite("/network-name/site-name/documentLibrary/folder/file");
        // Can mutate items underneath documentLibrary but not the library itself 
        canReadOnly("/network-name/site-name/documentLibrary");
        canReadOnly("/network-name/site-name/folder");
        canReadOnly("/network-name/site-name");
        canReadOnly("/network-name");
        
        // Sharepoint SOAP endpoints require POST
        checkRequest("POST", "/_vti_bin/some/path", SC_OK, true);   
        checkRequest("POST", "/network-name/site-name/_vti_bin/some/path", SC_OK, true);   
        checkRequest("POST", "/_vti_history/some/path", SC_OK, true);   
        checkRequest("POST", "/vti_inf.html", SC_OK, true);   
    }

    
    private void canReadOnly(String path) throws ServletException, IOException
    {
        checkAllMethods(path, SC_METHOD_NOT_ALLOWED, false);
    }

    private void canReadWrite(String path) throws ServletException, IOException
    {
        checkAllMethods(path, SC_OK, true);
    }
    
    private void checkAllMethods(String path, int expectedStatus, boolean serviceMethodRan) throws ServletException, IOException
    {
        // Expected status is always OK
        checkRequest("GET", path, SC_OK, true);
        checkRequest("HEAD", path, SC_OK, true);
        checkRequest("OPTIONS", path, SC_OK, true);
        checkRequest("PROPFIND", path, SC_OK, true);
        
        // Expected status is dependendent on context
        checkRequest("COPY", path, expectedStatus, serviceMethodRan);        
        checkRequest("DELETE", path, expectedStatus, serviceMethodRan);        
        checkRequest("LOCK", path, expectedStatus, serviceMethodRan);        
        checkRequest("MKCOL", path, expectedStatus, serviceMethodRan);        
        checkRequest("MOVE", path, expectedStatus, serviceMethodRan);        
        checkRequest("POST", path, expectedStatus, serviceMethodRan);        
        checkRequest("PROPPATCH", path, expectedStatus, serviceMethodRan);        
        checkRequest("PUT", path, expectedStatus, serviceMethodRan);        
        checkRequest("UNLOCK", path, expectedStatus, serviceMethodRan);        
    }

    private void checkRequest(String method, String path, int expectedStatus, boolean expectedServiceMethodRan) throws ServletException, IOException
    {
        // Setup
        req = new MockHttpServletRequest();
        req.setAttribute(CommonRequestHandling.REQ_ATTR_DOCLIB_ELEMENT_ADDED, true);
        resp = new MockHttpServletResponse();
        req.setMethod(method);
        req.setRequestURI(path);
        ranServiceImpl = false;
        
        // Service the request
        servlet.service(req, resp);
        
        // Check the results
        assertEquals(expectedStatus, resp.getStatus());
        assertEquals(expectedServiceMethodRan, ranServiceImpl);
    }

    
    
    // Extended class removes TenantUtil.runAsTenant() call to ease unit testing
    // and captures whether the serviceImpl method has been invoked.
    private class CloudWebDAVServletEx extends CloudWebDAVServlet
    {
        private static final long serialVersionUID = 1L;

        @Override
        protected void processRequestAsTenant(
                    HttpServletRequest request,
                    HttpServletResponse response,
                    String tenantDomain)
        {
            try
            {
                serviceImpl(request, response);
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }


        @Override
        protected void serviceImpl(HttpServletRequest request, HttpServletResponse response)
                    throws ServletException, IOException
        {
            ranServiceImpl = true;
            resp.setStatus(SC_OK);
        }
    }
}

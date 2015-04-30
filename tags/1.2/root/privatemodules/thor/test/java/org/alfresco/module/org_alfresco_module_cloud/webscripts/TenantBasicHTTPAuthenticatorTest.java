/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.module.org_alfresco_module_cloud.webscripts;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.module.org_alfresco_module_cloud.webscripts.TenantBasicHTTPAuthenticatorFactory.TenantBasicHttpAuthenticator;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.repo.web.auth.AuthenticationListener;
import org.alfresco.repo.web.auth.WebCredentials;
import org.alfresco.repo.web.scripts.TenantWebScriptServletRequest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.extensions.config.ServerProperties;
import org.springframework.extensions.webscripts.Description.RequiredAuthentication;
import org.springframework.extensions.webscripts.Match;
import org.springframework.extensions.webscripts.Runtime;
import org.springframework.extensions.webscripts.servlet.WebScriptServletResponse;

/**
 * Unit test for TenantBasicHTTPAuthenticator
 * 
 * @author Alex Miller
 */
public class TenantBasicHTTPAuthenticatorTest
{
    private static final String VALID_KEY = "f1ba2daf-db30-4a23-8977-a06fcf20fba7";
    private static final String VALID_USER = "valid.user@example.com";
    
    private static final String INVALID_KEY = "4d48a869-42bc-426a-9d1a-244532c502b3";

    /**
     * Workaround the spring configuration of the static singleton AuthenticationUtil class.
     * 
     * If we are running as part of the main test suite, this has probably already been down.
     * If we are running standalone, it won't have been. So, we check by calling getAdminUser 
     * and getGuestUserName. If either of these calls throw IllegalStateException then we assume 
     * it hasn't been initialized and do it ourselves.
     * 
     * @throws Exception
     */
    @BeforeClass public static void setupAuthenticationUtil() throws Exception
    {
        try
        {
            AuthenticationUtil.getAdminUserName();
            AuthenticationUtil.getGuestUserName();
        }
        catch (IllegalStateException ex)
        {
            AuthenticationUtil authenticationUtil = new AuthenticationUtil();
            authenticationUtil.setDefaultAdminUserName("admin");
            authenticationUtil.setDefaultGuestUserName("guest");
            authenticationUtil.afterPropertiesSet();
        }
    }
    
    /**
     * Test trusted auth with a valid key and user name.
     */
    @Test public void fromGatewayWithValidKeyAndUser()
    {
        // Get the factory.
        TenantBasicHTTPAuthenticatorFactory factory = autnenticatorFactory();
        
        // Set up mock objects for the authentication listener
        AuthenticationListener authenticationListener = Mockito.mock(AuthenticationListener.class);
        factory.setAuthenticationListener(authenticationListener);
        
        // Set up mock objects for request and response.
        HttpServletRequest servletRequest = servletRequest(VALID_USER, VALID_KEY);
        TenantWebScriptServletRequest req = webscriptRequest(servletRequest);
        
        WebScriptServletResponse res = Mockito.mock(WebScriptServletResponse.class);
        HttpServletResponse httpServletResponse = Mockito.mock(HttpServletResponse.class);
        Mockito.when(res.getHttpServletResponse()).thenReturn(httpServletResponse);
        
        // Get the authenticator
        TenantBasicHttpAuthenticator authenticator = (TenantBasicHttpAuthenticator) factory.create(req, res);
        
        RequiredAuthentication required = RequiredAuthentication.user;
        boolean isGuest = false;
        
        //Check for successful authentication
        Assert.assertTrue(authenticator.authenticate(required, isGuest));
        
        Mockito.verify(authenticationListener, Mockito.atLeastOnce()).userAuthenticated(Mockito.any(WebCredentials.class));
        
        // Validate interactions with request and response objects
        Mockito.verifyZeroInteractions(httpServletResponse);
        Mockito.verifyNoMoreInteractions(res);
        

        Mockito.verify(servletRequest, Mockito.atLeastOnce()).getContentType();
        Mockito.verify(servletRequest, Mockito.atLeastOnce()).getHeader("Content-Type");
    }

    /**
     * Create a TenantWebScriptServletRequest wrapping the given servletRequest.
     * 
     * Provides a mock Runtime object, test ServerProperties object and test Match object.
     * 
     * @param servletRequest
     * @return
     */
    private TenantWebScriptServletRequest webscriptRequest(HttpServletRequest servletRequest)
    {
        Runtime container = Mockito.mock(Runtime.class);

        ServerProperties serverProperties = new ServerProperties()
        {
            
            @Override
            public String getScheme()
            {
                return "http";
            }
            
            @Override
            public Integer getPort()
            {
                return 80;
            }
            
            @Override
            public String getHostName()
            {
                return "alfresco.example.com";
            }
        };
        Map<String, String> templateVars = Collections.emptyMap();
        Match serviceMatch = new Match("test", templateVars, "test");
        TenantWebScriptServletRequest req = new TenantWebScriptServletRequest(container, servletRequest, serviceMatch, serverProperties );
        return req;
    }

    /**
     * Create a mock HttpServletRequest object, setting the gateway related headers with the provided parameters.
     * @param userName
     * @param authenticatorKey
     * @return
     */
    private HttpServletRequest servletRequest(String userName, String authenticatorKey)
    {
        HttpServletRequest servletRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(servletRequest.getHeader("X-Alfresco-Authenticator-Key")).thenReturn(authenticatorKey);
        Mockito.when(servletRequest.getHeader("X-Alfresco-Remote-User")).thenReturn(userName);
        Mockito.when(servletRequest.getHeaders("key")).thenReturn(Collections.enumeration(Arrays.asList(new String[] {"694dc75b-bb5e-4edd-8384-56cdd055da43"})));
        Mockito.when(servletRequest.getHeaders("authorization")).thenReturn(Collections.enumeration(Arrays.asList(new String[] {"Bearer 694dc75b-bb5e-4edd-8384-56cdd055da43"})));
        Mockito.when(servletRequest.getRequestURI()).thenReturn("/alfresco/example.com/test");
        Mockito.when(servletRequest.getContextPath()).thenReturn("/alfresco");
        Mockito.when(servletRequest.getServletPath()).thenReturn("");
        return servletRequest;
    }

    /**
     * Create a TenantBasicHTTPAuthenticatorFactory.
     * @return
     */
    private TenantBasicHTTPAuthenticatorFactory autnenticatorFactory()
    {
        RetryingTransactionHelper transactionHelper = mockTransactionHelper();

        CloudTenantAuthentication tenantAuthenticationService = Mockito.mock(CloudTenantAuthentication.class);
        Mockito.when(tenantAuthenticationService.authenticateTenant((String) Mockito.notNull(), Mockito.eq("example.com"))).thenReturn(true);
        
        TenantBasicHTTPAuthenticatorFactory factory = new TenantBasicHTTPAuthenticatorFactory();
        factory.setTenantAuthentication(tenantAuthenticationService);
        factory.setTransactionHelper(transactionHelper );
        factory.setValidAuthentictorKeys(new HashSet<String>(Arrays.asList(new String[] {VALID_KEY})));
        factory.setOutboundHeaders(new HashSet<String>(Arrays.asList(new String[] {"Authorization","key"})));
        return factory;
    }


    /**
     * Create a mock  which simply invokes the callback execute method.
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private  RetryingTransactionHelper mockTransactionHelper()
    {
        RetryingTransactionHelper transactionHelper = Mockito.mock(RetryingTransactionHelper.class);
        Answer answer = new Answer()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                Object[] args = invocation.getArguments();
                return ((RetryingTransactionCallback)args[0]).execute();
            }
        };
        Mockito.doAnswer(answer).when(transactionHelper).doInTransaction(Mockito.any(RetryingTransactionCallback.class));
        Mockito.doAnswer(answer).when(transactionHelper).doInTransaction(Mockito.any(RetryingTransactionCallback.class), Mockito.anyBoolean());
        Mockito.doAnswer(answer).when(transactionHelper).doInTransaction(Mockito.any(RetryingTransactionCallback.class), Mockito.anyBoolean(), Mockito.anyBoolean());
        return transactionHelper;
    }
    
    /**
     * Test the authenticator with an invalid key.
     */
    @Test public void fromGatewayWithInvalidKey()
    {
        TenantBasicHTTPAuthenticatorFactory factory = autnenticatorFactory();
        
        // Set up mock objects for the authentication listener
        AuthenticationListener authenticationListener = Mockito.mock(AuthenticationListener.class);
        factory.setAuthenticationListener(authenticationListener);
        
        HttpServletRequest servletRequest = servletRequest(VALID_USER, INVALID_KEY);
        TenantWebScriptServletRequest req = webscriptRequest(servletRequest);
        
        WebScriptServletResponse res = Mockito.mock(WebScriptServletResponse.class);
        HttpServletResponse httpServletResponse = Mockito.mock(HttpServletResponse.class);
        Mockito.when(res.getHttpServletResponse()).thenReturn(httpServletResponse);
        
        TenantBasicHttpAuthenticator authenticator = (TenantBasicHttpAuthenticator) factory.create(req, res);
        
        RequiredAuthentication required = RequiredAuthentication.user;
        boolean isGuest = false;
        
        Assert.assertFalse(authenticator.authenticate(required, isGuest));
        
        // Check for the the correct response.
        Mockito.verify(res).setStatus(401);
        Mockito.verify(res).setHeader("WWW-Authenticate", "Basic realm=\"Alfresco example.com tenant\"");
        Mockito.verifyNoMoreInteractions(res);

        Mockito.verify(servletRequest, Mockito.atLeastOnce()).getContentType();
        Mockito.verify(servletRequest, Mockito.atLeastOnce()).getHeader("Content-Type");
        
        Mockito.verify(authenticationListener).authenticationFailed(Mockito.any(WebCredentials.class));
    }
}

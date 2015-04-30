/**
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.alfresco.module.org_alfresco_module_cloud_share.web.site.servlet;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.alfresco.module.org_alfresco_module_cloud_share.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.UserFactory;
import org.springframework.extensions.surf.WebFrameworkServiceRegistry;
import org.springframework.extensions.surf.exception.PlatformRuntimeException;
import org.springframework.extensions.surf.site.AuthenticationUtil;
import org.springframework.extensions.surf.uri.UriUtils;
import org.springframework.extensions.surf.util.URLDecoder;
import org.springframework.extensions.surf.util.URLEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;

/**
 * TODO: Create a SAMLAuthnResponseController (for on premise) with overridable methods so this class can extend it
 *
 * Responds to SAML Response POSTs to allow the user to authenticate to the web site.
 *
 * @author Erik Winlof
 */
public class TenantSAMLIDPAuthnResponseController extends CloudLoginController
{
    private static Log logger = LogFactory.getLog(TenantSAMLIDPAuthnResponseController.class);

    public static final String REDIRECT_COOKIE = "org.alfresco.share.saml.loginRedirectPage";

    /**
     * <p>A <code>TenantUserFactory</code> is required to authenticate requests. It will be supplied by the Spring Framework
     * providing that the controller is configured correctly - it requires that a "userFactory" is set with an instance
     * of a <code>UserFactory</code>. The <code>ConfigBeanFactory</code> can be used to generate <code>UserFactory</code>
     * Spring Beans</p>
     */
    private TenantUserFactory userFactory;

    /**
     * <p>This method is provided to allow the Spring framework to set a <code>UserFactory</code> required for authenticating
     * requests</p>
     *
     * @param userFactory
     */
    public void setUserFactory(UserFactory userFactory)
    {
        this.userFactory = (TenantUserFactory)userFactory;
    }

    protected WebFrameworkServiceRegistry serviceRegistry;

    public void setServiceRegistry(WebFrameworkServiceRegistry serviceRegistry)
    {
        this.serviceRegistry = serviceRegistry;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.mvc.AbstractController#createModelAndView(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        if (!request.getMethod().equals("POST"))
        {
            throw new ServletException("SAML AuthnRequest MUST be submitted using method POST");
        }
        
        request.setCharacterEncoding("UTF-8");
        String redirectUrl = null;
        boolean isInviteOrActivate = false;
        
        // We are using cookies instead of the SAML RelayState parameter;
        Cookie[] cookies = request.getCookies();
        if (cookies != null)
        {
            for (Cookie cookie : cookies)
            {
                if (cookie.getName().equals(REDIRECT_COOKIE))
                {
                    String page = cookie.getValue();
                    page = URLDecoder.decode(page);
                    if (!page.isEmpty())
                    {
                        // We found the redirectPage
                        if (!page.startsWith("/") && !page.contains(".."))
                        {
                            String tenantName = TenantUtil.getTenantName();
                            redirectUrl = UriUtils.relativeUri(request.getContextPath() + "/" + tenantName + "/page/" + page);
                            
                            // CLOUD-1358
                            if (page.startsWith("invitation?key=") || page.startsWith("activation?key="))
                            {
                                isInviteOrActivate = true;

                                // Identify if a user has come via the IDP (for CLOUD-1371)
                                redirectUrl += "&idp=true";
                            }
                        }
                        else
                        {
                            // Illegal redirect, lets log it but ignore the page in the redriect
                            logger.info("Illegal attempt to redirect an IDP user to something else than a Share page ignored: '" + page + "'.");
                        }
                        
                        // Clear the redirect value
                        cookie.setValue("");
                    }
                    break;
                }
            }
        }
        
        try
        {
            // check whether there is already a user logged in
            HttpSession session = request.getSession(false);
            
            String currentSessionIndex = null;
            
            if (session != null)
            {
                currentSessionIndex = (String)session.getAttribute(TenantUserFactory.SESSION_ATTRIBUTE_KEY_IDP_SESSION_INDEX);
            }
            
            // handle SSO which doesn't set a user until later
            if (session != null && request.getSession().getAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID) != null)
            {
                // destroy old session and log out the current user
                AuthenticationUtil.logout(request, response);
            }
            
            // Transform the SAML AuthnResponse's request parameters to a Map
            Map<String,String> samlProperties = new HashMap<String, String>();
            Enumeration params = request.getParameterNames();
            String param;
            while (params.hasMoreElements()) {
                param = (String) params.nextElement();
                samlProperties.put(param, request.getParameter(param));
            }
            
            // CLOUD-1358 - note: boolean is currently passed as String (as per SAMLAlfrescoAuthenticator.authenticate)
            samlProperties.put("isInviteOrActivate", Boolean.toString(isInviteOrActivate));
            
            // Authenticate the SAML AuthnResponse
            SAMLAuthnResponseVerification samlAuthnResponseVerification = userFactory.authenticateSAML(request, samlProperties);
            if (samlAuthnResponseVerification.isAuthenticated())
            {
                AuthenticationUtil.login(request, response, samlAuthnResponseVerification.getUsername(), false);

                // CLOUD-1191 / CLOUD-1209
                processSessionIndex(request, samlAuthnResponseVerification, currentSessionIndex);

                storeLocale(request);
            }
            else
            {
                if (samlAuthnResponseVerification.getUsername() != null
                        && samlAuthnResponseVerification.getTicket() == null)
                {
                    String tenantName = TenantUtil.getTenantName();
                    String redirectPage = request.getContextPath() + "/" + tenantName + "/page/";
                    
                    // if this is an invitation or activation then can ignore any other registration (even for a new user)
                    if (! isInviteOrActivate)
                    {
                        // The user had logged in at the IDP but doesn't exist in Alfresco
                        SAMLAuthnResponseVerificationRegistration registration = samlAuthnResponseVerification.getRegistration();
                        if (registration != null)
                        {
                            // CLOUD-1191 / CLOUD-1209
                            processSessionIndex(request, samlAuthnResponseVerification, currentSessionIndex);
                            
                            // There is a workflow in place allowing the user to complete his/hers user profile
                            // Either its an existing on eor one was generated (since auto provisioning was enabled)
                            String workflowParameters = "id=" + URLEncoder.encodeUriComponent(registration.getId()) +
                                    "&key=" + URLEncoder.encodeUriComponent(registration.getKey());

                            // Identify if a user has come via the IDP (for CLOUD-1371)
                            String idpIndicator = "&idp=true";

                            if (registration.getType().equals("signup"))
                            {
                                redirectUrl = UriUtils.relativeUri(redirectPage + "activation?" + workflowParameters + idpIndicator);
                            }
                            else if (registration.getType().equals("invite"))
                            {
                                redirectUrl = UriUtils.relativeUri(redirectPage + "invitation?" + workflowParameters + idpIndicator);
                            }
                        }
                    }

                    if (redirectUrl == null)
                    {
                        // invalidate the session to ensure any session ID cookies are no longer valid
                        // as the auth has failed - mitigates session fixation attacks by ensuring that no
                        // valid session IDs are created until after a successful user auth attempt
                        AuthenticationUtil.logout(request, response);

                        // Lets display a help page since no redirectPage was specified or any workflow was present
                        redirectUrl = UriUtils.relativeUri(redirectPage + "saml-idp-authnresponse-help");
                    }
                }
                else
                {
                    // invalidate the session to ensure any session ID cookies are no longer valid
                    // as the auth has failed - mitigates session fixation attacks by ensuring that no
                    // valid session IDs are created until after a successful user auth attempt
                    AuthenticationUtil.logout(request, response);
                }
            }
        }
        catch (PlatformRuntimeException err)
        {
            // redirect user to error page to prevent loop. CLOUD-1295
            String tenantName = TenantUtil.getTenantName();
            redirectUrl = request.getContextPath() + "/" + tenantName + "/page/message?text=" + "saml.alf-login.error.text";
        }
        catch (Throwable err)
        {
            throw new ServletException(err);
        }
        
        if (redirectUrl == null)
        {
            redirectUrl = request.getContextPath();
        }
        
        response.sendRedirect(redirectUrl);
        
        return null;
    }

    // CLOUD-1191 / CLOUD-1209 - support multiple sessionIndexes (eg. for PingFederate)
    private void processSessionIndex(HttpServletRequest request, SAMLAuthnResponseVerification samlAuthnResponseVerification, String currentSessionIndex)
    {
        String newSessionIndex = samlAuthnResponseVerification.getIdpSessionIndex();
        if ((newSessionIndex == null) || (newSessionIndex.isEmpty()))
        {
            // nothing to do
            return;
        }
        
        HttpSession session = request.getSession(false);
        
        if (session == null)
        {
            logger.warn("No user session, ignore session index: '" + newSessionIndex + "'");
            return;
        }
        
        if ((currentSessionIndex != null) && (! currentSessionIndex.isEmpty()))
        {
            // CLOUD-1191 - is CSV ok (or is comma significant in sessionIndex) ... note: see also SAMLogoutRequestBuilder
            Set<String> sessionIndexSet = StringUtils.commaDelimitedListToSet(currentSessionIndex);
            sessionIndexSet.add(newSessionIndex);
            newSessionIndex = StringUtils.collectionToCommaDelimitedString(sessionIndexSet);
        }
        
        session.setAttribute(TenantUserFactory.SESSION_ATTRIBUTE_KEY_IDP_SESSION_INDEX, newSessionIndex);
    }
}

/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */

package org.alfresco.web.app.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.acegisecurity.providers.dao.UsernameNotFoundException;

import org.alfresco.filesys.server.auth.ntlm.NTLM;
import org.alfresco.filesys.server.auth.ntlm.NTLMLogonDetails;
import org.alfresco.filesys.server.auth.ntlm.NTLMMessage;
import org.alfresco.filesys.server.auth.ntlm.Type1NTLMMessage;
import org.alfresco.filesys.server.auth.ntlm.Type2NTLMMessage;
import org.alfresco.filesys.server.auth.ntlm.Type3NTLMMessage;
import org.alfresco.filesys.server.auth.passthru.AuthenticateSession;
import org.alfresco.filesys.server.auth.passthru.PassthruServers;
import org.alfresco.filesys.server.config.ServerConfiguration;
import org.alfresco.filesys.smb.SMBException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.web.app.Application;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.bean.repository.User;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * NTLM Authentication Filter Class
 * 
 * @author GKSpencer
 */
public class NTLMAuthenticationFilter implements Filter
{
    // NTLM authentication session object names
    
    public static final String NTLM_AUTH_SESSION = "_alfNTLMAuthSess";
    public static final String NTLM_AUTH_DETAILS = "_alfNTLMDetails";

    // Debug logging
    
    private static Log logger = LogFactory.getLog(NTLMAuthenticationFilter.class);
    
    // Servlet context, required to get authentication service
    
    private ServletContext m_context;
    
    // Passthru authentication servers
    
    private PassthruServers m_authServers;
    
    // Allow guest access
    
    private boolean m_allowGuest;
    
    // Login page address
    
    private String m_loginPage;
    
    /**
     * Initialize the filter
     * 
     * @param args FilterConfig
     * @exception ServletException
     */
    public void init(FilterConfig args) throws ServletException
    {
        // Save the servlet context, needed to get hold of the authentication service
        
        m_context = args.getServletContext();
            
        // Create the authentication server list
        
        m_authServers = new PassthruServers();
        
        // Check if a server list has been specified
        
        String servers = args.getInitParameter("Servers");
        String domain  = args.getInitParameter("Domain");
        boolean useLocalServer = args.getInitParameter("LocalServer") != null ? true : false;
        boolean useLocalDomain = args.getInitParameter("LocalDomain") != null ? true : false;
        
        if ( servers != null)
        {
            // Create the passthru server list
        
            m_authServers.setServerList(servers);
            
            // Debug
            
            if ( logger.isDebugEnabled())
                logger.debug("NTLM filter using server list " + servers);
        }
        else if ( domain != null)
        {
            // Create the passthru list using the domain/workgroup
            
            m_authServers.setDomain(domain);
            
            // Debug
            
            if ( logger.isDebugEnabled())
                logger.debug("NTLM filter using domain " + domain);
        }
        else if ( useLocalServer == true)
        {
            // Access the server configuration bean to get the local server name
            
            WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(m_context);
            ServerConfiguration srvConfig = (ServerConfiguration)ctx.getBean("fileServerConfiguration");

            // Use the local server for NTLM passthru authentication

            String localName = srvConfig.getLocalServerName(true);
            m_authServers.setServerList(localName);
            
            // Debug
            
            if ( logger.isDebugEnabled())
                logger.debug("NTLM filter using local server " + localName);
        }
        else if ( useLocalDomain == true)
        {
            // Access the server configuration bean to get the local domain name
            
            WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(m_context);
            ServerConfiguration srvConfig = (ServerConfiguration)ctx.getBean("fileServerConfiguration");

            // Use the local domain/workgroup for NTLM passthru authentication

            String localDomain = srvConfig.getLocalDomainName();
            m_authServers.setDomain(localDomain);
            
            // Debug
            
            if ( logger.isDebugEnabled())
                logger.debug("NTLM filter using local domain " + localDomain);
        }
        
        // Check if guest access is to be allowed
        
        String guestAccess = args.getInitParameter("AllowGuest");
        if ( guestAccess != null)
        {
            m_allowGuest = Boolean.parseBoolean(guestAccess);
            
            // Debug
            
            if ( logger.isDebugEnabled() && m_allowGuest)
                logger.debug("NTLM filter guest access allowed");
        }
        
        // Check if any authentication servers have been configured
        
        if ( m_authServers.getTotalServerCount() == 0)
            throw new ServletException("No authentication servers configured");
    }

    /**
     * Run the filter
     * 
     * @param sreq ServletRequest
     * @param sresp ServletResponse
     * @param chain FilterChain
     * @exception IOException
     * @exception ServletException
     */
    public void doFilter(ServletRequest sreq, ServletResponse sresp, FilterChain chain) throws IOException,
            ServletException
    {
        // Get the HTTP request/response/session
        
        HttpServletRequest req = (HttpServletRequest) sreq;
        HttpServletResponse resp = (HttpServletResponse) sresp;
        
        HttpSession httpSess = req.getSession(true);

        // Check if there is an authorization header with an NTLM security blob
        
        String authHdr = req.getHeader("Authorization");
        boolean reqAuth = false;
        
        if ( authHdr != null && authHdr.startsWith("NTLM"))
            reqAuth = true;
        
        // Check if the user is already authenticated
        
        if ( reqAuth == false && httpSess.getAttribute(AuthenticationFilter.AUTHENTICATION_USER) != null)
        {
            chain.doFilter(sreq, sresp);
            return;
        }

        // Check if the login page is being accessed, do not intercept the login page
        
        if ( req.getRequestURI().endsWith(getLoginPage()) == true)
        {
            chain.doFilter( sreq, sresp);
            return;
        }
        
        // Check if the browser is Opera, if so then display the login page as Opera does not
        // support NTLM and displays an error page if a request to use NTLM is sent to it
        
        String userAgent = req.getHeader("user-agent");
        
        if ( userAgent != null && userAgent.indexOf("Opera ") != -1)
        {
            // Debug
            
            if ( logger.isDebugEnabled())
                logger.debug("Opera detected, redirecting to login page");

            // Redirect to the login page
            
            resp.sendRedirect(req.getContextPath() + "/faces" + getLoginPage());
            return;
        }
        
        // Check the authorization header
        
        if ( authHdr == null) {

            // Debug
            
            if ( logger.isDebugEnabled())
                logger.debug("New NTLM auth request from " + req.getRemoteHost() + " (" +
                        req.getRemoteAddr() + ":" + req.getRemotePort() + ")");
            
            // Send back a request for NTLM authentication
            
            resp.setHeader("WWW-Authenticate", "NTLM");
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            
            resp.flushBuffer();
            return;
        }
        else {
            
            // Get the existing NTLM authentication session and details
            
            AuthenticateSession authSess = null;
            NTLMLogonDetails ntlmDetails = null;
            
            if ( httpSess != null)
            {
                authSess = (AuthenticateSession) httpSess.getAttribute(NTLM_AUTH_SESSION);
                ntlmDetails = (NTLMLogonDetails) httpSess.getAttribute(NTLM_AUTH_DETAILS);
            }
                
            // Decode the received NTLM blob and validate
            
            byte[] ntlmByts = Base64.decodeBase64( authHdr.substring(5).getBytes());
            int ntlmTyp = NTLMMessage.isNTLMType(ntlmByts);
         
            if ( ntlmTyp == NTLM.Type1)
            {
                Type1NTLMMessage type1Msg = new Type1NTLMMessage(ntlmByts);

                // Debug
                
                if ( logger.isDebugEnabled())
                    logger.debug("Received type1 " + type1Msg);
                
                // If there is an existing authentication session close it and start a new session
                
                if ( authSess != null) {
                    
                    // Remove any existing authentication session
                    
                    httpSess.removeAttribute(NTLM_AUTH_SESSION);
                    
                    try 
                    {
                        authSess.CloseSession();
                    }
                    catch (SMBException ex)
                    {
                    }
                }

                // Check if cached logon details are available
                
                if ( ntlmDetails != null && ntlmDetails.hasType2Message() && ntlmDetails.hasNTLMHashedPassword())
                {
                    // Get the authentication server type2 response
                    
                    Type2NTLMMessage cachedType2 = ntlmDetails.getType2Message();

                    byte[] type2Bytes = cachedType2.getBytes();
                    String ntlmBlob = "NTLM " + new String(Base64.encodeBase64(type2Bytes));

                    // Debug
                    
                    if ( logger.isDebugEnabled())
                        logger.debug("Sending cached NTLM type2 to client - " + cachedType2);
                    
                    // Send back a request for NTLM authentication
                    
                    resp.setHeader("WWW-Authenticate", ntlmBlob);
                    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    
                    resp.flushBuffer();
                    return;
                }
                else
                {
                    // Clear any cached logon details
                    
                    httpSess.removeAttribute(NTLM_AUTH_DETAILS);
                    
                    // Create an authentication session
                    
                    authSess = m_authServers.openSession();
                    
                    // Debug
                    
                    if ( logger.isDebugEnabled())
                        logger.debug("Opened authentication session " + authSess);
                    
                    if ( authSess.hasType2NTLMMessage()) {
                        
                        // Store the authentication session
                        
                        httpSess.setAttribute(NTLM_AUTH_SESSION, authSess);
                        
                        // Get the authentication server type2 response
                        
                        Type2NTLMMessage authType2 = authSess.getType2NTLMMessage();
    
                        byte[] type2Bytes = authType2.getBytes();
                        String ntlmBlob = "NTLM " + new String(Base64.encodeBase64(type2Bytes));
    
                        // Store the NTLM logon details, cache the type2 message
                        
                        ntlmDetails = new NTLMLogonDetails();
                        ntlmDetails.setType2Message( authType2);
                        
                        httpSess.setAttribute(NTLM_AUTH_DETAILS, ntlmDetails);
                        
                        // Debug
                        
                        if ( logger.isDebugEnabled())
                            logger.debug("Sending NTLM type2 to client - " + authType2);
                        
                        // Send back a request for NTLM authentication
                        
                        resp.setHeader("WWW-Authenticate", ntlmBlob);
                        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        
                        resp.flushBuffer();
                        return;
                    }
                }
            }
            else if ( ntlmTyp == NTLM.Type3)
            {
                // Get the type3 message from the web request
                
                Type3NTLMMessage type3Msg = new Type3NTLMMessage(ntlmByts);
                
                // Debug
                
                if ( logger.isDebugEnabled())
                    logger.debug("Received type3 " + type3Msg);
                
                // Get the NTLM details
                
                String userName = type3Msg.getUserName();
                String workstation = type3Msg.getWorkstation();
                String domain = type3Msg.getDomain();
                
                boolean authenticated = false;
                boolean useNTLM = true;
                
                // Check if we are using cached details for the authentication
                
                if ( authSess == null && ntlmDetails != null && ntlmDetails.hasNTLMHashedPassword())
                {
                    // Check if the received NTLM hashed password matches the cached password
                    
                    byte[] ntlmPwd = type3Msg.getNTLMHash();
                    byte[] cachedPwd = ntlmDetails.getNTLMHashedPassword();
                    
                    if ( ntlmPwd != null)
                    {
                        if ( ntlmPwd.length == cachedPwd.length)
                        {
                            authenticated = true;
                            for ( int i = 0; i < ntlmPwd.length; i++)
                            {
                                if ( ntlmPwd[i] != cachedPwd[i])
                                    authenticated = false;
                            }
                        }
                    }
                    
                    // Debug
                    
                    if ( logger.isDebugEnabled())
                        logger.debug("Using cached NTLM hash, authenticated = " + authenticated);
                }
                else
                {
                    try
                    {
                        // Authenticate the user using the session to the authentication server
                        
                        authSess.doSessionSetup(type3Msg);
                    
                        // Check if the logon was authenticated, check if the guest account was used
                        
                        if ( authSess.isGuest() == false || allowsGuest())
                        {
                            
                            // Indicate that the user has been authenticated
    
                            authenticated = true;
    
                            // Get user details for the authenticated user
                            
                            WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(m_context);
                            AuthenticationService authService = (AuthenticationService)ctx.getBean("authenticationService");
                            AuthenticationComponent authComponent = (AuthenticationComponent)ctx.getBean("authenticationComponent");
    
                            authComponent.setCurrentUser(userName);
                            
                            // Setup User object and Home space ID etc.
                            
                            NodeService nodeService = (NodeService) ctx.getBean("nodeService");
                          
                            User user = new User(userName, authService.getCurrentTicket(), authComponent.getPerson(Repository.getStoreRef(), userName));
                            
                            String homeSpaceId = (String)nodeService.getProperty(authComponent.getPerson(Repository.getStoreRef(), userName), ContentModel.PROP_HOMEFOLDER);
                            user.setHomeSpaceId(homeSpaceId);
                            
                            httpSess.setAttribute(AuthenticationFilter.AUTHENTICATION_USER, user);
    
                            // Update the NTLM logon details in the session
                            
                            if ( ntlmDetails == null)
                            {
                                // No cached NTLM details
                                
                                ntlmDetails = new NTLMLogonDetails( userName, workstation, domain,
                                    authSess.isGuest(), authSess.getServer());
                                
                                httpSess.setAttribute(NTLM_AUTH_DETAILS, ntlmDetails);
                                
                                // Debug
                                
                                if ( logger.isDebugEnabled())
                                    logger.debug("No cached NTLM details, created");
                                
                            }
                            else
                            {
                                // Update the cached NTLM details
                                
                                ntlmDetails.setDetails(userName, workstation, domain, authSess.isGuest(), authSess.getServer());
                                ntlmDetails.setNTLMHashedPassword(type3Msg.getNTLMHash());
    
                                // Debug
                                
                                if ( logger.isDebugEnabled())
                                    logger.debug("Updated cached NTLM details");
                            }
                            
                            // Debug
                            
                            if ( logger.isDebugEnabled())
                                logger.debug("User logged on via NTLM, " + ntlmDetails);
                        }                    
                    }
                    catch (UsernameNotFoundException ex)
                    {
                        // Debug
                        
                        if ( logger.isDebugEnabled())
                            logger.debug("User " + userName + " authenticated, but no Alfresco account");
                        
                        // Bypass NTLM authentication and display the logon screen, user account does not
                        // exist in Alfresco
                        
                        useNTLM = false;
                        authenticated = false;
                    }
                    catch (IOException ex)
                    {
                        ex.printStackTrace();
                    }
                    catch (SMBException ex)
                    {
                        ex.printStackTrace();
                    }
                    finally
                    {
                        // Remove the authentication session from the web session
                        
                        httpSess.removeAttribute(NTLM_AUTH_SESSION);
                        
                        // Close the authentication session
    
                        if ( authSess != null)
                        {
                            try
                            {
                                authSess.CloseSession();
                            }
                            catch (SMBException ex)
                            {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
                
                // Check if the user was authenticated, this may be due to the user not existing in the
                // Alfresco user database in which case we redirect to the login page, otherwise start the
                // logon process again
                    
                if ( authenticated == false)
                {    
                    // Check if NTLM should be used, switched off if the user does not exist in the Alfresco
                    // user database
                    
                    if (useNTLM == true)
                    {
                        // Remove any existing session and NTLM details from the session
                        
                        httpSess.removeAttribute(NTLM_AUTH_SESSION);
                        httpSess.removeAttribute(NTLM_AUTH_DETAILS);
                        
                        // Force the logon to start again
                        
                        resp.setHeader("WWW-Authenticate", "NTLM");
                        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        
                        resp.flushBuffer();
                        return;
                    }
                    else
                    {
                        // Debug
                        
                        if ( logger.isDebugEnabled())
                            logger.debug("Redirecting to login page");
    
                        // Redirect to the login page
                        
                        resp.sendRedirect(req.getContextPath() + "/faces" + getLoginPage());
                        return;
                    }
                }

                // Check if the user has been authenticated and the original URL requested was the login page, if so
                // then redirect to the browse view
                
                if ( authenticated == true && useNTLM == true)
                {
                    if (req.getRequestURI().endsWith(getLoginPage()) == true)
                    {
                        // Debug
                        
                        if ( logger.isDebugEnabled())
                            logger.debug("Login page requested, redirecting to browse page");
    
                        //  Redirect to the browse view
                        
                        resp.sendRedirect(req.getContextPath() + "/faces/jsp/browse/browse.jsp");
                        return;
                    }
                    else
                    {
                        // Allow the user to access the requested page
                        
                        chain.doFilter( sreq, sresp);
                        return;
                    }
                }
            }
        }

        // Debug
        
        if ( logger.isDebugEnabled())
            logger.debug("NTLM not handled, redirecting to login page");
        
        // Redirect to the login page
        
        resp.sendRedirect(req.getContextPath() + "/faces" + getLoginPage());
    }

    /**
     * Determine if guest access is allowed
     * 
     * @return boolean
     */
    private final boolean allowsGuest()
    {
        return m_allowGuest;
    }

    /**
     * Return the login page address
     * 
     * @return String
     */
    private String getLoginPage()
    {
       if (m_loginPage == null)
       {
          m_loginPage = Application.getLoginPage(m_context);
       }
       
       return m_loginPage;
    }
    
    /**
     * Delete the servlet filter
     */
    public void destroy()
    {
        // Close the passthru server list
        
        if ( m_authServers != null)
        {
            m_authServers.shutdown();
            m_authServers = null;
        }
    }
}

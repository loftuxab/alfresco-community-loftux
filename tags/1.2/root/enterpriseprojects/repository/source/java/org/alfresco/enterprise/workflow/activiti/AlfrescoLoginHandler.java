/*
 * Copyright 2005-2011 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.workflow.activiti;

import java.io.Serializable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.explorer.ExplorerApp;
import org.activiti.explorer.identity.LoggedInUser;
import org.activiti.explorer.ui.login.LoginHandler;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Handler for logging in into the Activiti administration UI, authenticates
 * against Alfresco {@link AuthenticationService} and
 * {@link AuthenticationService}.
 * 
 * @author Frederik Heremans
 * @author Gavin Cornwell
 * @since 4.0
 */
public class AlfrescoLoginHandler implements LoginHandler
{
    private static final Log logger = LogFactory.getLog(AlfrescoLoginHandler.class);  
    
    private static final String ALFRESCO_TICKER_PARAMETER = "alf_ticket";
    
    protected AuthenticationService authenticationService;
    protected PersonService personService;
    protected NodeService nodeService;
    protected AuthorityService authorityService;

    @Override
    public LoggedInUser authenticate(String userName, String password)
    {
        if (logger.isDebugEnabled()) 
            logger.debug("Authenticating user using username and password");
                        
        LoggedInUser loggedInUser = null;

        if (checkCredentials(userName, password))
        {
            // Check if the user has the rights to use administrative capabilities
            if (authorityService.isAdminAuthority(userName))
            {
                loggedInUser = createLoggedInUser(userName, authenticationService.getCurrentTicket());
            }
        }
        
        return loggedInUser;
    }

    @Override
    public LoggedInUser authenticate(HttpServletRequest req, HttpServletResponse response)
    {
        if (logger.isDebugEnabled()) 
            logger.debug("Authenticating user from request");
        
        LoggedInUser loggedInUser = null;
        try
        {
            String authenticatedUser = authenticationService.getCurrentUserName();
            if (authenticatedUser != null && authorityService.isAdminAuthority(authenticatedUser))
            {
                loggedInUser = createLoggedInUser(authenticatedUser, authenticationService.getCurrentTicket());
            }
        }
        catch (AuthenticationException ae)
        {
            // Ignore, no user in current security-context
        }
        catch(net.sf.acegisecurity.AuthenticationException ae2) 
        {
            // Ignore, no user in current security-context
        }
        
        if (loggedInUser == null)
        {
            // Try checking if a ticket is present in the request (came from surf-proxy connector)
            String ticket = req.getParameter(ALFRESCO_TICKER_PARAMETER);
            if (ticket != null) 
            {
                try 
                {
                    // Validate the ticket, will set username accordingly if valid
                    authenticationService.validate(ticket);
                    
                    String authenticatedUser = authenticationService.getCurrentUserName();
                    if (authenticatedUser != null && authorityService.isAdminAuthority(authenticatedUser))
                    {
                        loggedInUser = createLoggedInUser(authenticatedUser, ticket);
                    }
                }
                catch (AuthenticationException ae)
                {
                    // Ignore, no user in current security-context
                    if (logger.isDebugEnabled()) 
                        logger.debug("Cannot validate ticket passed by parameter (" + ticket + "): " + ae.getMessage());
                }
                catch(net.sf.acegisecurity.AuthenticationException ae2) 
                {
                    // Ignore, no user in current security-context
                    if (logger.isDebugEnabled()) 
                        logger.debug("Cannot validate ticket passed by parameter (" + ticket + "): " + ae2.getMessage());
                }
            }
            else if (logger.isDebugEnabled())
            {
                logger.debug("Failed to find ticket in request");
            }
        }
        
        return loggedInUser;
    }

    @Override
    public void logout(LoggedInUser loggedInUser)
    {
        // Clear context
        try
        {
            authenticationService.clearCurrentSecurityContext();
        }
        catch (AuthenticationException ae)
        {
            // Ignore
        }
        catch (net.sf.acegisecurity.AuthenticationException ae2) 
        {
            // Ignore
        }
    }

    protected LoggedInUser createLoggedInUser(String userName, String ticket)
    {
        final NodeRef personNode = personService.getPerson(userName);
        final Map<QName, Serializable> allProperties = nodeService.getProperties(personNode);

        // Create user based on node properties
        final ActivitiLoggedInUser loggedInUser = new ActivitiLoggedInUser(userName, ticket);
        loggedInUser.setFirstName((String) allProperties.get(ContentModel.PROP_FIRSTNAME));
        loggedInUser.setLastName((String) allProperties.get(ContentModel.PROP_LASTNAME));

        // Indicate user can use and administer the app
        loggedInUser.setUser(true);
        loggedInUser.setAdmin(true);
        
        if (logger.isDebugEnabled())
            logger.debug("Created logged in user: " + loggedInUser);

        return loggedInUser;
    }

    protected boolean checkCredentials(String userName, String password)
    {
        try
        {
            if (logger.isDebugEnabled())
                logger.debug("Checking credentials for user: " + userName);
            
            authenticationService.authenticate(userName, password.toCharArray());
            
            if (logger.isDebugEnabled())
                logger.debug("Successfully authenticated user: " + userName);
            
            return true;
        }
        catch (AuthenticationException ae)
        {
            if (logger.isDebugEnabled())
                logger.debug("Credential check failed: " + ae.getMessage());
            
            return false;
        }
    }

    public void setAuthenticationService(AuthenticationService authenticationService)
    {
        this.authenticationService = authenticationService;
    }

    public void setPersonService(PersonService personService)
    {
        this.personService = personService;
    }

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setAuthorityService(AuthorityService authorityService)
    {
        this.authorityService = authorityService;
    }

    @Override
    public void onRequestEnd(HttpServletRequest req, HttpServletResponse res)
    {
        try
        {
            // clear security context off the thread
            authenticationService.clearCurrentSecurityContext();
        }
        catch (AuthenticationException ae)
        {
            // Ignore
        }
        catch (net.sf.acegisecurity.AuthenticationException ae2) 
        {
            // Ignore
        }
    }

    @Override
    public void onRequestStart(HttpServletRequest req, HttpServletResponse res)
    {
        LoggedInUser loggedInUser = ExplorerApp.get().getLoggedInUser();
        
        if (logger.isDebugEnabled())
            logger.debug("Revalidating logged in user: " + loggedInUser);
        
        if (loggedInUser != null && loggedInUser instanceof ActivitiLoggedInUser)
        {
            try 
            {
                // Revalidate the logged in users ticket to make sure all
                // calls to alfresco from activiti happen in right security context
                String currentTicket = ((ActivitiLoggedInUser)loggedInUser).getTicket();
                authenticationService.validate(currentTicket);
                
                if (logger.isDebugEnabled())
                    logger.debug("Ticket revalidated: " + currentTicket);
            }
            catch (AuthenticationException ae)
            {
                if (logger.isDebugEnabled())
                    logger.debug("Logging out due to authentication exception: " + ae.getMessage());
                
                ticketExpired();
            }
            catch(net.sf.acegisecurity.AuthenticationException ae2) 
            {
                if (logger.isDebugEnabled())
                    logger.debug("Logging out due to authentication exception: " + ae2.getMessage());

                ticketExpired();
            }
        }
    }

    private void ticketExpired()
    {
        ExplorerApp.get().close();
        
        if (logger.isWarnEnabled())
            logger.warn("Ticket expired, activiti admin logging out");
    }
}

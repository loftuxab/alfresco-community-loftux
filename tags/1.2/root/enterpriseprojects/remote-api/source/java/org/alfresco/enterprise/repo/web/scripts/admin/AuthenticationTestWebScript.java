/*
 * Copyright 2012-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.web.scripts.admin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.repo.security.authentication.AbstractChainingAuthenticationComponent;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * Controller for Authentication Test Webscript
 * 
 * @author mrogers
 * @since 4.2
 */
public abstract class AuthenticationTestWebScript extends DeclarativeWebScript
{
    protected static Log logger = LogFactory.getLog(AuthenticationTestWebScript.class);

    // Parameters
    String PARAM_USER_NAME = "userName";
    String PARAM_AUTHENTICATOR_NAME = "authenticatorName";
    String PARAM_PASSWORD = "password";
    
    // properties placed into the model
    String PROP_MODEL_USER_NAME = "userName";
    String PROP_MODEL_AUTHENTICATOR_NAME = "authenticatorName";
    String PROP_MODEL_PASSWORD = "password";
    String PROP_MODEL_DIAGNOSTIC = "diagnostic";
    String PROP_MODEL_PASSED = "testPassed";
    String PROP_MODEL_ERROR_MESSAGE = "authenticationMessage";
    String PROP_MODEL_AUTHENTICATION_EXCEPTION = "authenticationException";
    
    private AuthenticationComponent authenticationComponent;
    
    public void init()
    {
        PropertyCheck.mandatory(this, "authenticationComponent", authenticationComponent);
    }
    
    protected class TestCommand
    {
       String authenticator;
       String userName;
       String credentials;
    }
    
    abstract TestCommand parseCommand(WebScriptRequest req);
     
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {

        TestCommand c = parseCommand(req);       
        
        logger.debug("in execute");
        
        String userName = c.userName;
        String password = c.credentials;
        String authenticatorName = c.authenticator;
        
        Map<String, Object> model = new HashMap<String, Object>(4);
        model.put(PROP_MODEL_USER_NAME, userName);
        model.put(PROP_MODEL_AUTHENTICATOR_NAME, authenticatorName);
        model.put(PROP_MODEL_PASSWORD, password);
        
        model.put(PROP_MODEL_PASSED, true);
        
        if(authenticationComponent instanceof  AbstractChainingAuthenticationComponent )
        {
            try
            {
                AbstractChainingAuthenticationComponent chainingComponent = 
                (AbstractChainingAuthenticationComponent)authenticationComponent;
            
                 logger.debug("got a chaining authentication component");
                 
                 chainingComponent.testAuthenticate(authenticatorName, userName, password.toCharArray());
                 logger.debug("authenticated successfully");
            }
            catch (AuthenticationException ae)
            {
                logger.debug("authentication exception in test authentication web script", ae);
                model.put(PROP_MODEL_PASSED, false);
                model.put(PROP_MODEL_DIAGNOSTIC, ae.getDiagnostic());
                model.put(PROP_MODEL_AUTHENTICATION_EXCEPTION, ae);
                model.put(PROP_MODEL_ERROR_MESSAGE, ae.getMessage());
            }
        }
       
        logger.debug("at end of execute");
        return model;
    }

    public void setAuthenticationComponent(AuthenticationComponent authenticationComponent)
    {
        this.authenticationComponent = authenticationComponent;
    }

    public AuthenticationComponent getAuthenticationComponent()
    {
        return authenticationComponent;
    }
}

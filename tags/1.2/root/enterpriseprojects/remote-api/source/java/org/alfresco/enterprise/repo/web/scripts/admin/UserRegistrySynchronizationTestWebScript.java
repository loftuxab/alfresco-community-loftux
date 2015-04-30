/*
 * Copyright 2012-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.web.scripts.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.security.authentication.AbstractChainingAuthenticationComponent;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.repo.security.authentication.AuthenticationDiagnostic;
import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.repo.security.authentication.AuthenticationStep;
import org.alfresco.repo.security.sync.ChainingUserRegistrySynchronizer;
import org.alfresco.repo.security.sync.SynchronizeDiagnostic;
import org.alfresco.repo.security.sync.TestableChainingUserRegistrySynchronizer;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.surf.util.I18NUtil;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * Controller for User Synchronisation Test Webscript
 * 
 * @author mrogers
 * @since 4.2
 */
public abstract class UserRegistrySynchronizationTestWebScript extends DeclarativeWebScript
{
    protected static Log logger = LogFactory.getLog(UserRegistrySynchronizationTestWebScript.class);

    // Parameters
    String PARAM_USER_NAME = "userName";
    String PARAM_AUTHENTICATOR_NAME = "authenticatorName";
    String PARAM_PASSWORD = "password";
    String PARAM_MAX_ITEMS = "maxItems";
    
    // properties placed into the model
    String PROP_MODEL_USER_NAME = "userName";
    String PROP_MODEL_AUTHENTICATOR_NAME = "authenticatorName";
    String PROP_MODEL_PASSWORD = "password";
    String PROP_MODEL_DIAGNOSTIC = "diagnostic";
    String PROP_MODEL_PASSED = "testPassed";
    String PROP_MODEL_ERROR_MESSAGE = "authenticationMessage";
    String PROP_MODEL_AUTHENTICATION_EXCEPTION = "authenticationException";
    String PROP_MODEL_USERS = "users";
    String PROP_MODEL_GROUPS = "groups";
    String PROP_MODEL_SYNC_ACTIVE = "syncActive";
    
    /**
     * Limit of users/groups to return
     */
    private int MAX_ITEMS = 10;
    
    private TestableChainingUserRegistrySynchronizer chainingUserRegistrySynchronizer;
        
    public void init()
    {
        PropertyCheck.mandatory(this, "chainingUserRegistrySynchronizer", chainingUserRegistrySynchronizer);
    }
    
    protected class TestCommand
    {
       String authenticator;
       String userName;
       String credentials;
       int maxItems = MAX_ITEMS;
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
        int maxItems = c.maxItems;
        
        Map<String, Object> model = new HashMap<String, Object>(4);
        if(userName != null)
        {
            model.put(PROP_MODEL_USER_NAME, userName);
        }
        if(password != null)
        {
            model.put(PROP_MODEL_PASSWORD, password);
        }  
        model.put(PROP_MODEL_AUTHENTICATOR_NAME, authenticatorName);
 
        try
        {  
            logger.debug("got a chaining user synchronization component");                 
            SynchronizeDiagnostic diagnostic = chainingUserRegistrySynchronizer.testSynchronize(authenticatorName);
            model.put(PROP_MODEL_PASSED, true);
            List<String> userSubset = new ArrayList<String>(MAX_ITEMS);
            int i = 0;
            for(String user : diagnostic.getUsers())
            {
                if(i++>= MAX_ITEMS)
                {
                    break;
                }
                userSubset.add(user);
            }
            List<String> groupSubset = new ArrayList<String>(MAX_ITEMS);
            i = 0;
            for(String group : diagnostic.getGroups())
            {
                if(i++>= MAX_ITEMS)
                {
                    break;
                }
                groupSubset.add(group);
            }
            model.put(PROP_MODEL_USERS, userSubset);
            model.put(PROP_MODEL_GROUPS, groupSubset);
            model.put(PROP_MODEL_SYNC_ACTIVE, diagnostic.isActive());
            
            model.put(PROP_MODEL_DIAGNOSTIC, formatDiagnostic(diagnostic));
            logger.debug("testSynchronize successfull:" + diagnostic);
        }
        catch (AuthenticationException ae)
        {
            model.put(PROP_MODEL_PASSED, false);
            logger.debug("authentication exception in test synchronize web script", ae);
            model.put(PROP_MODEL_DIAGNOSTIC, ae.getDiagnostic());
            model.put(PROP_MODEL_AUTHENTICATION_EXCEPTION, ae);
            model.put(PROP_MODEL_ERROR_MESSAGE, ae.getMessage());
        }
        catch (AlfrescoRuntimeException ae)
        {
            model.put(PROP_MODEL_PASSED, false);
            logger.debug("runtime exception exception in test synchronize web script", ae);
            //model.put(PROP_MODEL_DIAGNOSTIC, ae.getDiagnostic());
            model.put(PROP_MODEL_AUTHENTICATION_EXCEPTION, ae);
            model.put(PROP_MODEL_ERROR_MESSAGE, ae.getMessage());
        }
       
        logger.debug("at end of execute");
        return model;
    }
    
    private AuthenticationDiagnostic formatDiagnostic(SynchronizeDiagnostic diagnostic)
    {
        AuthenticationDiagnostic impl = new AuthenticationDiagnostic(); 
        {
            Object[] params = {diagnostic.getGroups().size()};
            impl.addStep(new SyncStepImpl("synchronization.step.groups", params));
        }  
        {
            Object[] params = {diagnostic.getUsers().size()};
            impl.addStep(new SyncStepImpl("synchronization.step.users", params));
        }
        if(diagnostic.getPersonLastSynced() != null)
        {
            Object[] params = {diagnostic.getPersonLastSynced()};
            impl.addStep(new SyncStepImpl("synchronization.step.users.lastsynced", params));
        } 
        if(diagnostic.getGroupLastSynced() != null)
        {
            Object[] params = {diagnostic.getGroupLastSynced()};
            impl.addStep(new SyncStepImpl("synchronization.step.groups.lastsynced", params));
        } 
    
        return impl;
    }

    public void setChainingUserRegistrySynchronizer(
            TestableChainingUserRegistrySynchronizer chainingUserRegistrySynchronizer)
    {
        this.chainingUserRegistrySynchronizer = chainingUserRegistrySynchronizer;
    }

    public TestableChainingUserRegistrySynchronizer getChainingUserRegistrySynchronizer()
    {
        return chainingUserRegistrySynchronizer;
    }
    
    private static final long serialVersionUID = -445668784415288394L;
    String key;
    boolean success = false;
    Object[] args;
    
    class SyncStepImpl implements AuthenticationStep
    {
        private static final long serialVersionUID = -445668784415288394L;
        String key;
        boolean success = true;
        Object[] args;
        
        SyncStepImpl(String key)
        {
            this.key = key;
        }
        
        SyncStepImpl(String key, Object[] args)
        {
            this.key = key;
            this.args = args;
        }
     
    
        @Override
        public String getKey()
        {
            return key;
        }
    
        @Override
        public boolean isSuccess()
        {
            return success;
        }
    
        @Override
        public Object[] getArgs()
        {
            return args;
        }
        
        public String toString()
        {
            return "Authentication Step Impl key:" + key + ", is success:" + success;
        }
    
        @Override
        public String getMessage()
        {
            String message = I18NUtil.getMessage(getKey(), getArgs());
            
            if (message == null)
            {
                return key;
            }
            return message;
        }
    }
}

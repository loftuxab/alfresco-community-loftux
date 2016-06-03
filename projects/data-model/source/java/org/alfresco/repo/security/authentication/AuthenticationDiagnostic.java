package org.alfresco.repo.security.authentication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.extensions.surf.util.I18NUtil;

/**
 * Diagnostic information for a failed authentication.
 * 
 * Contains a list of steps which have failed or succeeded.
 * The key and arguments can be used to form human readable messages from a message bundle.
 */
public class AuthenticationDiagnostic implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = -936231318594794088L;
    
    // Level 0 - validation
    public static final String STEP_KEY_VALIDATION_AUTHENTICATOR_NOT_FOUND="authentication.ldap.validation.authenticator.notfound";
    
    public static final String STEP_KEY_VALIDATION_AUTHENTICATOR_NOT_ACTIVE="authentication.ldap.validation.authenticator.notactive";
    
    // Level 0 - validation
    public static final String STEP_KEY_VALIDATION="authentication.step.ldap.validation";
    
    // Level 1 - connecting to authentication provider
    public static final String STEP_KEY_LDAP_CONNECTING="authentication.step.ldap.connecting";
    public static final String STEP_KEY_LDAP_CONNECTED="authentication.step.ldap.connected";
    
    // Level 2 - using the authentication provider
    public static final String STEP_KEY_LDAP_AUTHENTICATION="authentication.step.ldap.authentication";
    
    public static final String STEP_KEY_LDAP_LOOKUP_USER="authentication.step.ldap.lookup";
    public static final String STEP_KEY_LDAP_LOOKEDUP_USER="authentication.step.ldap.lookedup";
    
    public static final String STEP_KEY_LDAP_FORMAT_USER="authentication.step.ldap.format.user";
    
    // Level 2 - using the authentication provider
    public static final String STEP_KEY_LDAP_SEARCH="authentication.ldap.search";    

    
    private List<AuthenticationStep>steps = new ArrayList<AuthenticationStep>(10);
    
    public void addStep(AuthenticationStep step)
    {
        steps.add(step);
    }
    
    /**
     * 
     * @param key String
     * @param success boolean
     */
    public void addStep(String key, boolean success)
    {
        AuthenticationStepImpl step = new AuthenticationStepImpl(key);
        step.success = success;
        
        addStep(step);
    }
    
    /**
     * 
     * @param key String
     * @param success boolean
     * @param args Object[]
     */
    public void addStep(String key, boolean success, Object[] args)
    {
        AuthenticationStepImpl step = new AuthenticationStepImpl(key);
        step.success = success;
        step.args = args;
        
        addStep(step);
    }

    public List<AuthenticationStep> getSteps()
    {
        return steps;
    }
}



class AuthenticationStepImpl implements AuthenticationStep, Serializable
{
    private static final long serialVersionUID = -445668784415288394L;
    String key;
    boolean success = false;
    Object[] args;
    
    public AuthenticationStepImpl(String key)
    {
        this.key = key;
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
        
        if(message == null)
        {
            return key;
        }
        return message;
    }
}
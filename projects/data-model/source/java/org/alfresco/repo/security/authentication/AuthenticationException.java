package org.alfresco.repo.security.authentication;

import org.alfresco.api.AlfrescoPublicApi;
import org.alfresco.error.AlfrescoRuntimeException;

/**
 * Alfresco Authentication Exception and wrapper
 * 
 * @author andyh
 *
 */
@AlfrescoPublicApi
public class AuthenticationException extends AlfrescoRuntimeException
{
    /**
     * 
     */
    private static final long serialVersionUID = 3546647620128092466L;
 
    // Diagnostic information to assist with problem determination
    AuthenticationDiagnostic diagnostic;

    public AuthenticationException(String msg)
    {
        super(msg);
    }
    
    public AuthenticationException(String msg, Object[]args)
    {
        super(msg, args);
    }

    public AuthenticationException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
    
    public AuthenticationException(String msg, Object[] args, Throwable cause)
    {
        super(msg, cause);
    }
    
    /**
     * Authentication Exception
     * @param msg human readable message
     * @param diagnostic diagnostic information about how the authentication succeeded/failed
     */
    public AuthenticationException(String msg, AuthenticationDiagnostic diagnostic)
    {
        super(msg);
        this.diagnostic = diagnostic;
    }
    
    /**
     * Authentication Exception
     * @param msg human readable message
     * @param diagnostic diagnostic information about how the authentication succeeded/failed
     */
    public AuthenticationException(String msg, Object[] args, AuthenticationDiagnostic diagnostic)
    {
        super(msg, args);
        this.diagnostic = diagnostic;
    }
    
    /**
     * Authentication Exception
     * @param msg key for human readable message
     * @param diagnostic diagnostic information about how the authentication succeeded/failed
     * @param cause stack trace of the exception
     */
    public AuthenticationException(String msg, AuthenticationDiagnostic diagnostic, Throwable cause)
    {
        super(msg, cause);
        this.diagnostic = diagnostic;
    }
    
    /**
     * Authentication Exception
     * @param msg key for human readable message
     * @param diagnostic diagnostic information about how the authentication succeeded/failed
     * @param args arguments for human readable message
     * @param cause stack trace of the exception
     */
    public AuthenticationException(String msg, AuthenticationDiagnostic diagnostic, Object[] args, Throwable cause)
    {
        super(msg, args, cause);
        this.diagnostic = diagnostic;
    }

    /**
     * Get the authentication diagnostic
     * @return the authentication diagnostic
     */
    public AuthenticationDiagnostic getDiagnostic()
    {
        return diagnostic;
    } 
    
}

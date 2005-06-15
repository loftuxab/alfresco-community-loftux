/*
 * Created on 13-Jun-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.security.authentication;

import org.alfresco.error.AlfrescoRuntimeException;

/**
 * Alfresco Authentication Exception and wrapper
 * 
 * @author andyh
 *
 */
public class AuthenticationException extends AlfrescoRuntimeException
{

    /**
     * 
     */
    private static final long serialVersionUID = 3546647620128092466L;

    public AuthenticationException(String msg)
    {
        super(msg);
    }

    public AuthenticationException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
}

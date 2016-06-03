package org.alfresco.module.vti.web.ws;

import org.alfresco.module.vti.metadata.dic.VtiError;

/**
 * Exception used for SOAP problems where a specific SOAP FAULT error 
 *  code should be returned, rather than using one of the Errors from
 *  the {@link VtiError} enumeration.  
 * 
 * @author EugeneZh
 */
public class VtiSoapException extends RuntimeException
{
    private static final long serialVersionUID = -6212010064088831048L;
    
    /**
     * What SharePoint error code to return
     */
    private long errorCode = -1;
    
    /**
     * Create exception with specified message
     * 
     * @param message the specified message
     */
    public VtiSoapException(String message, long errorCode)
    {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Create exception with specified message and throwable object
     * 
     * @param message the specified message
     * @param throwable
     */
    public VtiSoapException(String message, long code, Throwable throwable)
    {
        super(message, throwable);
        this.errorCode = code;
    }
    
    /**
     * Return the error code, or -1 if not known
     */
    public long getErrorCode()
    {
        return errorCode;
    }
}

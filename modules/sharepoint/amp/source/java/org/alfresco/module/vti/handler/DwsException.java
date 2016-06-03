package org.alfresco.module.vti.handler;

import org.alfresco.module.vti.metadata.dic.DwsError;
import org.alfresco.module.vti.web.ws.VtiSoapException;

/**
 * Exception for DWS Actions, based on the Enumeration
 *  of possible DWS Errors.
 *  
 * These map onto a different set of Error XML, neither
 *  SOAP Faults (which go via {@link VtiSoapException})
 *  nor via Error IDs (which go via {@link VtiHandlerException})
 * 
 * @author Nick Smith
 */
public class DwsException extends RuntimeException
{
    private static final long serialVersionUID = 1932184211L;
    
    /**
     * What Error this is, from the Enumeration List
     */
    private DwsError error = DwsError.FAILED;

    /**
     * Create exception with specified message
     * 
     * @param error the specified error
     */
    public DwsException(DwsError error)
    {
        super(error.toCode());
        this.error = error;
    }

    /**
     * Create exception with specified message
     * 
     * @param message the specified message
     */
    public DwsException(String message)
    {
        super(message);
    }

    /**
     * Create exception with specified message and throwable object
     * 
     * @param error the specified error
     * @param throwable
     */
    public DwsException(DwsError error, Throwable throwable)
    {
        super(error.toCode(), throwable);
        this.error = error;
    }

    /**
     * Return the underlying error, if known,
     *  or {@link DwsError#FAILED} if not
     */
    public DwsError getError()
    {
        return error;
    }
}

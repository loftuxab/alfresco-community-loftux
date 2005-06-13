/**
 * 
 */
package org.alfresco.service.cmr.version;

/**
 * Version service exception class.
 * 
 * @author Roy Wetherall
 */
public class VersionServiceException extends RuntimeException
{     
    private static final long serialVersionUID = 3544671772030349881L;

    public VersionServiceException()
    {
        super();
    }

    public VersionServiceException(String message)
    {
        super(message);
    }

    public VersionServiceException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public VersionServiceException(Throwable cause)
    {
        super(cause);
    }
}

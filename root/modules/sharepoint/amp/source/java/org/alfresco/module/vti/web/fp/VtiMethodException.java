
package org.alfresco.module.vti.web.fp;

import org.alfresco.module.vti.handler.VtiHandlerException;
import org.alfresco.module.vti.metadata.dic.VtiError;

/**
 * Exception that describe specific error code of Frontpage extension protocol.
 * It is created using {@link VtiHandlerException} and transform VtiHandlerException message
 * to appropriate error code of Frontpage extension protocol     
 * 
 * @author Dmitry Lazurkin
 *
 */
public class VtiMethodException extends RuntimeException
{
    private static final long serialVersionUID = 6560087866101304630L;

    private VtiError error;
    private long errorCode;

    public VtiMethodException(VtiError errorCode, Throwable e)
    {
        super(errorCode.getMessage(), e);
        this.errorCode = errorCode.getErrorCode();
        this.error = errorCode;
    }

    public VtiMethodException(VtiError errorCode)
    {
        super(errorCode.getMessage());
        this.errorCode = errorCode.getErrorCode();
        this.error = errorCode;
    }

    public VtiMethodException(String message, Throwable e)
    {
        super(message, e);
        this.errorCode = -1;
        this.error = VtiError.V_UNDEFINED;
    }

    public VtiMethodException(String message, int code)
    {
        super(message);
        this.errorCode = code;
        this.error = VtiError.V_UNDEFINED;
    }
    
    public VtiMethodException(VtiHandlerException e)
    {
        super(e.getMessage(), e.getCause());
        this.errorCode = e.getErrorCode();
        this.error = e.getError();
    }

    public VtiError getError()
    {
        return error;
    }
    
    public long getErrorCode()
    {
        return errorCode;
    }
}

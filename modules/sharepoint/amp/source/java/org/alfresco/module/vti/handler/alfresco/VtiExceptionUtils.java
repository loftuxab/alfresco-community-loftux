package org.alfresco.module.vti.handler.alfresco;

/**
 * Helper for AlfrescoVtiMethodHandler
 * 
 * @author Dmitry Lazurkin
 */
public class VtiExceptionUtils
{

    /**
     * If throwable isn't runtime then wraps it in runtime exception.
     * 
     * @param throwable runtime or not runtime exception
     * @return RuntimeException runtime exception
     */
    public static RuntimeException createRuntimeException(Throwable throwable)
    {
        RuntimeException runtimeException;
        if (throwable instanceof RuntimeException)
        {
            runtimeException = (RuntimeException) throwable;
        }
        else
        {
            runtimeException = new RuntimeException(throwable);
        }

        return runtimeException;
    }

}

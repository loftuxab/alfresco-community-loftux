package org.alfresco.error;

/**
 * Runtime exception thrown by Alfresco code
 * 
 * @author gavinc
 */
public class AlfrescoRuntimeException extends RuntimeException
{
   private static final long serialVersionUID = 3834594313622859827L;

   public AlfrescoRuntimeException(String msg)
    {
        super(msg);
    }

    public AlfrescoRuntimeException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
}

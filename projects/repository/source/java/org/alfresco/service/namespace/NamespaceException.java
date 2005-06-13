package org.alfresco.service.namespace;


public class NamespaceException extends RuntimeException
{
   private static final long serialVersionUID = 7851788938794302629L;    
   
   public NamespaceException(String msg)
   {
      super(msg);
   }
   
   public NamespaceException(String msg, Throwable cause)
   {
      super(msg, cause);
   }
}

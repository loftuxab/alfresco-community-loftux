package org.alfresco.repo.ref;


public class InvalidQNameException extends RuntimeException
{
   private static final long serialVersionUID = 7851788938794302629L;
   
   public InvalidQNameException(String msg)
   {
      super(msg);
   }
   
   public InvalidQNameException(String msg, Throwable cause)
   {
      super(msg, cause);
   }
}

package org.alfresco.service.namespace;

import org.alfresco.api.AlfrescoPublicApi;   

@AlfrescoPublicApi
public class InvalidQNameException extends NamespaceException
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


package org.alfresco.module.vti.handler.alfresco;

import org.alfresco.module.vti.handler.VtiHandlerException;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.springframework.aop.ThrowsAdvice;

/**
 * Adviser that should wrap all realizations of VtiHandlers. It transform 
 * all obtained exceptions to VtiHandlerException.    
 * 
 * @author Dmitry Lazurkin
 */
public class AlfrescoVtiMethodHandlerThrowsAdvice implements ThrowsAdvice
{
    
    /**
     * Method that called after exception occurs
     * 
     * @param throwable source exception 
     */
    public void afterThrowing(Throwable throwable)
    {
        if (throwable instanceof VtiHandlerException)
        {
            throw (VtiHandlerException) throwable;
        }

        if (throwable instanceof AccessDeniedException)
        {
            throw new VtiHandlerException(VtiHandlerException.OWSSVR_ERRORACCESSDENIED, throwable);
        }

        throw new VtiHandlerException(VtiHandlerException.UNDEFINED, throwable);
    }

}

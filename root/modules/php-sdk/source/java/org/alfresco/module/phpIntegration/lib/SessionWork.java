package org.alfresco.module.phpIntegration.lib;

/**
 * Session work interface
 * 
 *  @param <Result>		the result type
 */
public interface SessionWork<Result>
{
    /**
     * Method containing the work to be done against the session context
     * 
     * @return Return the result of the operation
     */
    Result doWork();
}
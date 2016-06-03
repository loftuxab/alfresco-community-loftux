package org.alfresco.repo.security.authentication;

public interface AuthenticationStep
{
    /**
     *Get the key for this step
     * @return the key
     */
    public String getKey();
    
    /**
     * is success
     * @return true success
     */
    public boolean isSuccess();
    
    /**
     * Get the arguments for this step
     * @return the arguments
     */
    public Object[] getArgs();
    
    /**
     * get a localised human readable message for this key/arguments
     * @return the message for this step
     */
    public String getMessage();
}

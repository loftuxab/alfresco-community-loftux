package org.alfresco.filesys.server.config;

/**
 * <p>
 * Indicates that one or more parameters in the server configuration are not valid.
 */
public class InvalidConfigurationException extends Exception
{
    private static final long serialVersionUID = 3257568390900887607L;

    /**
     * InvalidConfigurationException constructor.
     * 
     * @param s java.lang.String
     */
    public InvalidConfigurationException(String s)
    {
        super(s);
    }

    /**
     * InvalidConfigurationException constructor.
     * 
     * @param s java.lang.String
     * @param ex Exception
     */
    public InvalidConfigurationException(String s, Throwable ex)
    {
        super(s, ex);
    }
}
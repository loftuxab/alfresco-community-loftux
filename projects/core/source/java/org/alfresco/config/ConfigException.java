package org.alfresco.config;

import org.alfresco.error.AlfrescoRuntimeException;

/**
 * Exception thrown by the config service
 * 
 * @author gavinc
 */
public class ConfigException extends AlfrescoRuntimeException
{
    private static final long serialVersionUID = 3257008761007847733L;

    public ConfigException(String msg)
    {
        super(msg);
    }

    public ConfigException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
}

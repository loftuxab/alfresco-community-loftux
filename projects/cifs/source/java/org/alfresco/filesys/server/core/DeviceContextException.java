package org.alfresco.filesys.server.core;

/**
 * Device Context Exception Class
 * <p>
 * Thrown when a device context parameter string is invalid.
 */
public class DeviceContextException extends Exception
{
    private static final long serialVersionUID = 3761124938182244658L;

    /**
     * Class constructor
     */
    public DeviceContextException()
    {
        super();
    }

    /**
     * Class constructor
     * 
     * @param s java.lang.String
     */
    public DeviceContextException(String s)
    {
        super(s);
    }

}

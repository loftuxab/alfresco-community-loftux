package org.alfresco.filesys.server.core;

/**
 * <p>
 * This exception may be thrown by a SharedDevice when the device interface has not been specified,
 * the device interface does not match the shared device type, or the device interface driver class
 * cannot be loaded.
 */
public class InvalidDeviceInterfaceException extends Exception
{
    private static final long serialVersionUID = 3834029177581222198L;

    /**
     * InvalidDeviceInterfaceException constructor.
     */
    public InvalidDeviceInterfaceException()
    {
        super();
    }

    /**
     * InvalidDeviceInterfaceException constructor.
     * 
     * @param s java.lang.String
     */
    public InvalidDeviceInterfaceException(String s)
    {
        super(s);
    }
}
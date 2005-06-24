package org.alfresco.filesys.server.core;

/**
 * <p>
 * The device context is passed to the methods of a device interface. Each shared device has a
 * device interface and a device context associated with it. The device context allows a single
 * device interface to be used for multiple shared devices.
 */
public class DeviceContext
{

    // Device name that the interface is associated with

    private String m_devName;

    // Flag to indicate if the device is available. Unavailable devices will not be listed by the
    // various
    // protocol servers.

    private boolean m_available = true;

    /**
     * DeviceContext constructor.
     */
    public DeviceContext()
    {
        super();
    }

    /**
     * DeviceContext constructor.
     */
    public DeviceContext(String devName)
    {
        m_devName = devName;
    }

    /**
     * Return the device name.
     * 
     * @return java.lang.String
     */
    public final String getDeviceName()
    {
        return m_devName;
    }

    /**
     * Determine if the filesystem is available
     * 
     * @return boolean
     */
    public final boolean isAvailable()
    {
        return m_available;
    }

    /**
     * Set the filesystem as available, or not
     * 
     * @param avail boolean
     */
    public final void setAvailable(boolean avail)
    {
        m_available = avail;
    }

    /**
     * Set the device name.
     * 
     * @param name java.lang.String
     */
    public final void setDeviceName(String name)
    {
        m_devName = name;
    }

    /**
     * Close the device context, free any resources allocated by the context
     */
    public void CloseContext()
    {
    }

    /**
     * Return the context as a string
     * 
     * @return String
     */
    public String toString()
    {
        StringBuffer str = new StringBuffer();

        str.append("[");
        str.append(getDeviceName());
        str.append("]");

        return str.toString();
    }
}
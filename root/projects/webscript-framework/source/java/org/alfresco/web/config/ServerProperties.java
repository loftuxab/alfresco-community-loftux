package org.alfresco.web.config;

public interface ServerProperties
{
    /**
     * @return  server scheme
     */
    public String getScheme();

    /**
     * @return  server hostname
     */
    public String getHostName();

    /**
     * @return  server port
     */
    public Integer getPort();

}

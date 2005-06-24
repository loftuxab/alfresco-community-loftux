package org.alfresco.filesys.server.filesys;

import org.alfresco.filesys.server.NetworkServer;
import org.alfresco.filesys.server.config.ServerConfiguration;

/**
 * Network File Server Class
 * <p>
 * Base class for all network file servers.
 */
public abstract class NetworkFileServer extends NetworkServer
{

    /**
     * Class constructor
     * 
     * @param proto String
     * @param config ServerConfiguration
     */
    public NetworkFileServer(String proto, ServerConfiguration config)
    {
        super(proto, config);
    }
}

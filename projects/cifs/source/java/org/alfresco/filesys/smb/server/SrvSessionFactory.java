package org.alfresco.filesys.smb.server;

/**
 * Server Session Factory Interface
 */
public interface SrvSessionFactory
{

    /**
     * Create a new server session object
     * 
     * @param handler PacketHandler
     * @param server SMBServer
     * @return SMBSrvSession
     */
    public SMBSrvSession createSession(PacketHandler handler, SMBServer server);
}

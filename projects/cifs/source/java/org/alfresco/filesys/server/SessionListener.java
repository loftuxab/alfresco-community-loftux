package org.alfresco.filesys.server;

/**
 * <p>
 * The session listener interface provides a hook into the server so that an application is notified
 * when a new session is created and closed by a network server.
 */
public interface SessionListener
{

    /**
     * Called when a network session is closed.
     * 
     * @param sess Network session details.
     */
    public void sessionClosed(SrvSession sess);

    /**
     * Called when a new network session is created by a network server.
     * 
     * @param sess Network session that has been created for the new connection.
     */
    public void sessionCreated(SrvSession sess);

    /**
     * Called when a user logs on to a network server
     * 
     * @param sess Network session that has been logged on.
     */
    public void sessionLoggedOn(SrvSession sess);
}
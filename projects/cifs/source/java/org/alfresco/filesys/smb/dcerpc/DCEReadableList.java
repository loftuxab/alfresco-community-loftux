package org.alfresco.filesys.smb.dcerpc;

/**
 * DCE/RPC Readable List Interface
 * <p>
 * A class that implements the DCEReadableList interface can read a list of DCEReadable objects from
 * a DCE/RPC buffer.
 */
public interface DCEReadableList
{

    /**
     * Read the object state from the DCE/RPC buffer
     * 
     * @param buf DCEBuffer
     * @exception DCEBufferException
     */
    public void readObject(DCEBuffer buf) throws DCEBufferException;
}

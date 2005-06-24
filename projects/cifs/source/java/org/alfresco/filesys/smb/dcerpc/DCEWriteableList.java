package org.alfresco.filesys.smb.dcerpc;

/**
 * DCE/RPC Writeable List Interface
 * <p>
 * A class that implements the DCEWriteableList interface can write a list of DCEWriteable objects
 * to a DCE/RPC buffer.
 */
public interface DCEWriteableList
{

    /**
     * Write the object state to DCE/RPC buffers.
     * 
     * @param buf DCEBuffer
     * @exception DCEBufferException
     */
    public void writeObject(DCEBuffer buf) throws DCEBufferException;
}

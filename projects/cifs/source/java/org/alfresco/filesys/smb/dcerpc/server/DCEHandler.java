package org.alfresco.filesys.smb.dcerpc.server;

import java.io.IOException;

import org.alfresco.filesys.smb.dcerpc.DCEBuffer;
import org.alfresco.filesys.smb.server.SMBSrvException;
import org.alfresco.filesys.smb.server.SMBSrvSession;

/**
 * DCE Request Handler Interface
 */
public interface DCEHandler
{

    /**
     * Process a DCE/RPC request
     * 
     * @param sess SMBSrvSession
     * @param inBuf DCEBuffer
     * @param pipeFile DCEPipeFile
     * @exception IOException
     * @exception SMBSrvException
     */
    public void processRequest(SMBSrvSession sess, DCEBuffer inBuf, DCEPipeFile pipeFile) throws IOException,
            SMBSrvException;
}

package org.alfresco.deployment.impl.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public interface ReaderManagement 

{
	/**
	 * Add a copy thread
	 * @param is 
	 * @param os
	 * @param token
	 */
    void addCopyThread(InputStream is, 
    		OutputStream os,
    		String token); 

    /**
     * closeCopyThread 
     * @param os the output stream
     */
    void closeCopyThread(String token) throws IOException;


}

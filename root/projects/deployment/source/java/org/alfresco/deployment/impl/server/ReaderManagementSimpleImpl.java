/*
 * Copyright (C) 2007-2008 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing
 */

package org.alfresco.deployment.impl.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.deployment.impl.DeploymentException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ReaderManagementSimpleImpl implements ReaderManagement
{
    /**
     * The logger for this target
     */
    private static Log logger = LogFactory.getLog(ReaderManagementSimpleImpl.class);
    
    /**
     * Need to kick of a reader thread to process input 
     * This class manages those threads
     */
    // Map token, thread
    Map<String, ReaderThread> fThreads = new HashMap<String, ReaderThread>();
    
    /**
     * addReader
     * @param is the input stream
     * @param os the output stream
     */
    public void addCopyThread(InputStream is, 
    		OutputStream os,
    		String token) 
    {
    	ReaderThread worker = new ReaderThread(is, os);
        worker.start();
        fThreads.put(token, worker);
    }
    
    /**
     * 
     * @param os the output stream
     */
    public void closeCopyThread(String token) throws IOException {
    	
    	ReaderThread worker = (ReaderThread)fThreads.get(token);
    	
    	if(worker == null)
    	{
            throw new DeploymentException("Closed unknown file.");
    	}
    	fThreads.remove(token);
    	
    	try {
			worker.join();
			if(worker.getException() != null) {
			    throw(worker.getException());	
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	
    } // end of ReaderManagement
    

    private class ReaderThread extends Thread {	
		InputStream input;
		OutputStream output;
		IOException exception = null;
	
		public ReaderThread(InputStream input, OutputStream output) 
		{
			this.input = input;
			this.output = output;
			super.setName("Reader Thread ");
		}
	
		@Override
		public void run() 
		{
			byte b[] = new byte[1000];
			int len = 0;
			try 
			{
				while ( len >= 0 ) 
				{
					len = input.read(b, 0, b.length);
					if(len > 0 && exception == null) 
					{
						try 
						{
							output.write(b, 0, len);
						} 
						catch (IOException e) 
						{
							// If we get a write error we still need to drain 
							// input to avoid a broken pipe exception
							this.exception = e;
						}
					}
				}				
			}
			catch (IOException e) 
			{
				this.exception = e;
			}
			finally
			{
				try {
					output.close();
				} catch (IOException e) {
					// We can do nothing here
					logger.error("Unable to close content stream", e);
				}
			}
		}
		
		/**
		 * @return the exception or null if all is well
		 */
		public IOException getException()
		{
			return this.exception;
		}
	}
}
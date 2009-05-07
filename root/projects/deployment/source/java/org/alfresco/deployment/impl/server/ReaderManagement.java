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

public class ReaderManagement {
	
    // Need to kick of a reader thread to process input 

    Map<DeployedFile, ReaderThread> fThreads = new HashMap<DeployedFile, ReaderThread>();
    
    /**
     * addReader
     * @param is the input stream
     * @param os the output stream
     */
    void addCopyThread(InputStream is, 
    		OutputStream os,
    		DeployedFile file) 
    {
    	ReaderThread worker = new ReaderThread(is, os);
        fThreads.put(file, worker);
        worker.start();
    }
    
    /**
     * 
     * @param os the output stream
     */
    void closeCopyThread(DeployedFile file) throws IOException {
    	
    	ReaderThread worker = (ReaderThread)fThreads.get(file);
    	
    	if(worker == null)
    	{
            throw new DeploymentException("Closed unknown file.");
    	}
    	fThreads.remove(file);
    	
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
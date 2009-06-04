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
import java.nio.channels.Channels;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.Pipe;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.alfresco.deployment.impl.DeploymentException;

public class NonblockingReaderManagement {

	ReaderThread worker;
	
	public Selector selector = null;
	
    public NonblockingReaderManagement()
    {
    	try 
    	{
    		selector = Selector.open();
    	}
    	catch (IOException ie)
    	{
    		
    	}
    	ReaderThread worker = new ReaderThread();
        worker.start();
    }
    
    /**
     * addReader
     * @param is the input stream
     * @param os the output stream
     */
    void addCopyThread(Pipe.SourceChannel sc,
    		InputStream is, 
    		OutputStream os,
    		String token) 
    {
    	ReadableByteChannel ic = Channels.newChannel(is);
   
    	try {
    		sc.configureBlocking(false);
    		Tracker newKey = new Tracker();
    		newKey.is = is;
    		newKey.os = os;
    		newKey.token = token;
			SelectionKey acceptKey = sc.register(selector, SelectionKey.OP_ACCEPT);
			acceptKey.attach(newKey);
		} catch (ClosedChannelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        //fThreads.put(token, worker);
    }
    
    /**
     * 
     * @param os the output stream
     */
    void closeCopyThread(String token) throws IOException {
    	
//    	ReaderThread worker = (ReaderThread)fThreads.get(token);
//    	
//    	if(worker == null)
//    	{
//            throw new DeploymentException("Closed unknown file.");
//    	}
//    	fThreads.remove(token);
//    	
//    	try {
//			worker.join();
//			if(worker.getException() != null) {
//			    throw(worker.getException());	
//			}
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
    	
    } // end of ReaderManagement
    

    private class ReaderThread extends Thread {	
		
		public ReaderThread() 
		{
			super.setName("Reader Thread ");
		}
	
		@Override
		public void run() 
		{
			
			try {
				
				while (selector.select() > 0)
				{
					Set<SelectionKey> readyKeys = selector.selectedKeys();
					for(SelectionKey key : readyKeys)
					{
						if(key.isReadable())
						{
							Tracker t = (Tracker)key.attachment();
						}
					}
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
    
    private class Tracker 
    {
    	public InputStream is; 
    	public OutputStream os;
    	public String token;
    }
}
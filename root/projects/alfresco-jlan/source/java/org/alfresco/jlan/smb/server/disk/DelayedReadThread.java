/*
 * Copyright (C) 2006-2008 Alfresco Software Limited.
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
 * http://www.alfresco.com/legal/licensing"
 */

package org.alfresco.jlan.smb.server.disk;

import java.io.IOException;

import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.server.SrvSession;
import org.alfresco.jlan.server.filesys.DelayedReadDetails;


/**
 *	Delayed Read Thread Class
 *
 * <p>Returns a read response at a later time via the asynchronous packet queue
 *
 * @author gkspencer
 */
public class DelayedReadThread extends Thread {

  //	Delayed read details
  
  private DelayedReadDetails m_delayDetails;

  //	Read length
  
  private int m_rdlen;
  
  //	Time to delay the response
  
  private long m_delay;
  
  /**
   * Class constructor
   *
   * @param sess SrvSession
   * @param rdlen int
   * @param dataPos int
   * @param delay long
   */
  DelayedReadThread( SrvSession sess, int rdlen, int dataPos, long delay) {
    m_delayDetails = new DelayedReadDetails( sess, dataPos);
    m_rdlen  = rdlen;
    m_delay  = delay * 1000L;

    //	Start the delay thread
    
    Debug.println("Delayed read waiting for " + m_delay + " ms ...");
    start();
  }
  
  /**
   * Run the delayed read thread
   */
  public void run() {

    //	Sleep for a while
    
    try {
      sleep( m_delay);
    }
    catch (InterruptedException ex) {
    }
    
    //	Send the read response via the asynchronous response queue
    
    try {
      Debug.println("Sending delayed read");
      m_delayDetails.sendReadResponse( m_rdlen);
    }
    catch (IOException ex) {
      Debug.println("Delayed read error: " + ex.toString());
    }
  }
}

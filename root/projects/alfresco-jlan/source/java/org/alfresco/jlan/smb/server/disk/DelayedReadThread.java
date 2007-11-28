package org.alfresco.jlan.smb.server.disk;

/*
 * DelayedReadThread.java
 *
 * Copyright (c) Starlasoft 2005. All rights reserved.
 */

import java.io.IOException;

import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.server.SrvSession;
import org.alfresco.jlan.server.filesys.DelayedReadDetails;


/**
 *	Delayed Read Thread Class
 *
 * <p>Returns a read response at a later time via the asynchronous packet queue
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

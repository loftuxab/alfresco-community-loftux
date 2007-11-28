package org.alfresco.jlan.smb.server.disk;

/*
 * NIOFileLock.java
 *
 * Copyright (c) Starlasoft 2005. All rights reserved.
 */

import org.alfresco.jlan.locking.FileLock;

/**
 * NIO File Lock Class
 * 
 * <p>Extends the base file lock class to hold the NIO file lock object.
 */
public class NIOFileLock extends FileLock {

  //	NIO file lock held on the open file
  
  java.nio.channels.FileLock m_lock;
  
  /**
   * Class constructor
   *
   * @param offset long
   * @param len long
   * @param pid int
   */
  public NIOFileLock(long offset, long len, int pid) {
    super ( offset, len, pid);
  }
  
  /**
   * Get the NIO lock
   * 
   * @return java.io.channels.FileLock
   */
  public final java.nio.channels.FileLock getNIOLock() {
    return m_lock;
  }
  
  /**
   * Set the NIO lock
   *
   * @param lock java.nio.channels.FileLock
   */
  public final void setNIOLock( java.nio.channels.FileLock lock) {
    m_lock = lock;
  }
}

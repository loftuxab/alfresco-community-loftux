package org.alfresco.jlan.server.locking;

/*
 * FileLockListener.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
import org.alfresco.jlan.locking.FileLock;
import org.alfresco.jlan.server.SrvSession;
import org.alfresco.jlan.server.filesys.NetworkFile;

/**
 * File Lock Listener Interface.
 *
 * <p>The file lock listener receives events when file locks are granted, released and denied.
 */
public interface FileLockListener {

  /**
   * Lock has been granted on the specified file.
   *
   * @param sess SrvSession
   * @param file NetworkFile
   * @param lock FileLock
   */
  void lockGranted(SrvSession sess, NetworkFile file, FileLock lock);

  /**
   * Lock has been released on the specified file.
   *
   * @param sess SrvSession
   * @param file NetworkFile
   * @param lock FileLock
   */
  void lockReleased(SrvSession sess, NetworkFile file, FileLock lock);

  /**
   * Lock has been denied on the specified file.
   *
   * @param sess SrvSession
   * @param file NetworkFile
   * @param lock FileLock
   */
  void lockDenied(SrvSession sess, NetworkFile file, FileLock lock);
}
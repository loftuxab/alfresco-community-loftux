package org.alfresco.jlan.server.filesys;

import org.alfresco.jlan.server.SrvSession;

/*
 * TransactionalFilesystemInterface.java
 *
 * Copyright (c) 2007 Starlasoft. All rights reserved.
 */

/**
 * Transactional Filesystem Interface
 * 
 * <p>Optional interface that a filesystem driver can implement to add support for transactions around filesystem calls.
 */
public interface TransactionalFilesystemInterface {

  /**
   * Begin a read-only transaction
   * 
   * @param sess SrvSession
   */
  public void beginReadTransaction(SrvSession sess);
  
  /**
   * Begin a writeable transaction
   * 
   * @param sess SrvSession
   */
  public void beginWriteTransaction(SrvSession sess);
  
  /**
   * End an active transaction
   * 
   * @param sess SrvSession
   * @param tx ThreadLocal<Object>
   */
  public void endTransaction(SrvSession sess, ThreadLocal<Object> tx);
}

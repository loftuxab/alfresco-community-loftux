package org.alfresco.jlan.server.filesys;

/*
 * DiskOfflineExcepttion.java
 *
 * Copyright (c) 2007 Starlasoft. All rights reserved.
 */

import java.io.IOException;

/**
 * <p>This exception may be thrown by a disk interface when the filesystem is offline data is not available due to the file being archived
 * or the repository being unavailable.
 */
public class DiskOfflineException extends IOException {

  private static final long serialVersionUID = -3055330216300723042L;

  /**
   * Class constructor.
   */
  public DiskOfflineException() {
    super();
  }
  
  /**
   * Class constructor.
   * 
   * @param s java.lang.String
   */
  public DiskOfflineException(String s) {
    super(s);
  }
}
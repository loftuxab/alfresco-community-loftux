package org.alfresco.jlan.smb.server;

/*
 * AdminSharedDevice.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
import org.alfresco.jlan.server.core.ShareType;
import org.alfresco.jlan.server.core.SharedDevice;

/**
 * Administration shared device, IPC$.
 */
final class AdminSharedDevice extends SharedDevice {

  /**
   * Class constructor
   */
  protected AdminSharedDevice() {
    super("IPC$", ShareType.ADMINPIPE, null);

    //  Set the device attributes

    setAttributes(SharedDevice.Admin + SharedDevice.Hidden);
  }
}
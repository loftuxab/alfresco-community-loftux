package org.alfresco.jlan.server.filesys;

/*
 * DiskVolumeInterface.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
/**
 * Disk Volume Interface
 * 
 * <p>Optional interface that a DiskInterface driver can implement to provide disk volume information. The disk volume
 * information may also be specified via the configuration.
 */
public interface DiskVolumeInterface {

  /**
   * Return the disk device volume information.
   *
   * @param ctx		DiskDeviceContext
   * @return VolumeInfo
   */
  public VolumeInfo getVolumeInformation(DiskDeviceContext ctx);
}

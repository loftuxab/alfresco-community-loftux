package org.alfresco.jlan.server.filesys;

/*
 * IOCtlInterface.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

import org.alfresco.jlan.server.SrvSession;
import org.alfresco.jlan.smb.SMBException;
import org.alfresco.jlan.util.DataBuffer;

/**
 * IO Control Interface
 * 
 * <p>Optional interface that a DiskInterface driver can implement to enable NT I/O control function processing.
 */
public interface IOCtlInterface {

  /**
   * Process a filesystem I/O control request
   * 
   * @param sess			Server session
   * @param tree     	Tree connection.
   * @param ctrlCode	I/O control code
   * @param fid				File id
   * @param dataBuf		I/O control specific input data
   * @param isFSCtrl	true if this is a filesystem control, or false for a device control
   * @param filter		if bit0 is set indicates that the control applies to the share root handle
   * @return DataBuffer
   * @exception IOControlNotImplementedException
   * @exception SMBException
   */
  public DataBuffer processIOControl(SrvSession sess, TreeConnection tree, int ctrlCode, int fid, DataBuffer dataBuf, 
      															 boolean isFSCtrl, int filter)
  	throws IOControlNotImplementedException, SMBException;
}

package org.alfresco.jlan.smb.dcerpc;

/*
 * DCEReadable.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
/**
 * DCE/RPC Readable Interface
 * 
 * <p>A class that implements the DCEReadable interface can load itself from a DCE buffer.
 */
public interface DCEReadable {

	/**
	 * Read the object state from the DCE/RPC buffer
	 * 
	 * @param buf DCEBuffer
	 * @exception DCEBufferException
	 */
	public void readObject(DCEBuffer buf)
		throws DCEBufferException;

  /**
	 * Read the strings for object from the DCE/RPC buffer
	 * 
	 * @param buf DCEBuffer
	 * @exception DCEBufferException
   */
  public void readStrings(DCEBuffer buf)
  	throws DCEBufferException;
}

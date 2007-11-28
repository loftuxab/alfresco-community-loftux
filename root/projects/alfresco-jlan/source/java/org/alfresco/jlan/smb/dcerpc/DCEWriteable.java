package org.alfresco.jlan.smb.dcerpc;

/*
 * DCEWriteable.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
/**
 * DCE/RPC Writeable Interface
 * 
 * <p>A class that implements the DCEWriteable interface can save itself to a DCE buffer.
 */
public interface DCEWriteable {

	/**
	 * Write the object state to DCE/RPC buffers.
	 * 
	 * <p>If a list of objects is being written the strings will be written after the objects so the
	 * second buffer will be specified.
	 * 
	 * <p>If a single object is being written to the buffer the second buffer may be null or be the same
	 * buffer as the main buffer.
	 * 
	 * @param buf DCEBuffer
	 * @param strBuf DCEBuffer
	 * @exception DCEBufferException
	 */
	public void writeObject(DCEBuffer buf, DCEBuffer strBuf)
		throws DCEBufferException;
}

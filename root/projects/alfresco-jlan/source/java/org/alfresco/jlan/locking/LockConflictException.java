package org.alfresco.jlan.locking;

/*
 * LockConflictException.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
import java.io.IOException;

/**
 * Lock Conflict Exception Class
 * 
 * <p>Thrown when a lock request overlaps with an existing lock on a file.
 */
public class LockConflictException extends IOException {

  private static final long serialVersionUID = 8287539855625316026L;

  /**
	 * Class constructor.
	 */
	public LockConflictException() {
		super();
	}
  
	/**
	 * Class constructor.
	 * 
	 * @param s java.lang.String
	 */
	public LockConflictException(String s) {
		super(s);
	}
}

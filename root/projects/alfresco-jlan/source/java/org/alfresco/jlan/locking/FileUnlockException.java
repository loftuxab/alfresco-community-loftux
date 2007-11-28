package org.alfresco.jlan.locking;

/*
 * FileUnlockException.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
import java.io.IOException;

/**
 * File Unlock Exception Class
 */
public class FileUnlockException extends IOException {

  private static final long serialVersionUID = 9216966294163749741L;

  /**
	 * Class constructor.
	 */
	public FileUnlockException() {
		super();
	}
  
	/**
	 * Class constructor.
	 * 
	 * @param s java.lang.String
	 */
	public FileUnlockException(String s) {
		super(s);
	}
}

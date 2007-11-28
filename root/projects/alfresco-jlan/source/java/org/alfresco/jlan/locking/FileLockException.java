package org.alfresco.jlan.locking;

/*
 * FileLockException.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
import java.io.IOException;

/**
 * File Lock Exception Class
 */
public class FileLockException extends IOException {

  private static final long serialVersionUID = 2722104928152336050L;

  /**
	 * Class constructor.
	 */
	public FileLockException() {
		super();
	}
  
	/**
	 * Class constructor.
	 * 
	 * @param s java.lang.String
	 */
	public FileLockException(String s) {
		super(s);
	}
}

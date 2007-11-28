package org.alfresco.jlan.debug;

/*
 * DebugInterface.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
import org.alfresco.config.ConfigElement;

/**
 * Debug Output Interface
 */
public interface DebugInterface {

	/**
	 * Close the debug output.
	 */
	void close();

	/**
	 * Output a debug string.
	 *
	 * @param str java.lang.String
	 */
	public void debugPrint(String str);

	/**
	 * Output a debug string, and a newline.
	 *
	 * @param str java.lang.String
	 */
	public void debugPrintln(String str);

	/**
	 * Initialize the debug interface using the specified named parameters.
	 *
	 * @param params ConfigElement
	 * @exception Exception
	 */
	public void initialize(ConfigElement params)
		throws Exception;
}

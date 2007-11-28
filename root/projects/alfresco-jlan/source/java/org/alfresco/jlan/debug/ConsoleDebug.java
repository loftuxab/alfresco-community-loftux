package org.alfresco.jlan.debug;

/*
 * ConsoleDebug.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */

import org.alfresco.config.ConfigElement;

/**
 * Console Debug Output Class.
 *
 * <p>Output debug messages to the console stream, System.out.
 */
public class ConsoleDebug implements DebugInterface {

	/**
	 * ConsoleDebug constructor comment.
	 */
	public ConsoleDebug() {
	  super();
	}

	/**
	 * Close the debug output.
	 */
	public void close() {}

	/**
	 * Output a debug string.
	 *
	 * @param str java.lang.String
	 */
	public final void debugPrint(String str) {
	  System.out.print(str);
	}

	/**
	 * Output a debug string, and a newline.
	 *
	 * @param str java.lang.String
	 */
	public final void debugPrintln(String str) {
	  System.out.println(str);
	}

	/**
	 * Initialize the debug interface using the specified parameters.
	 *
	 * @param params ConfigElement
	 */
	public void initialize(ConfigElement params) {}
}
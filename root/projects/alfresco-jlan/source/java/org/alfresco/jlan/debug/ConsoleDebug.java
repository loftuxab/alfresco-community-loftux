/*
 * Copyright (C) 2006-2008 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */

package org.alfresco.jlan.debug;

import org.alfresco.config.ConfigElement;

/**
 * Console Debug Output Class.
 *
 * <p>Output debug messages to the console stream, System.out.
 *
 * @author gkspencer
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

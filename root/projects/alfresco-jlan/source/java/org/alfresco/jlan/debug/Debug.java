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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.StringTokenizer;

/**
 * Debug Output Class
 *
 * @author gkspencer
 */
public final class Debug {

	//	Global constants used to control compiling of debug statements
	
	public static final boolean EnableInfo	= true;
	public static final boolean EnableWarn	= true;
	public static final boolean EnableError	= true;
	public static final boolean EnableDbg	  = true;

	//	Line seperator used for exception stack traces
	
	private static final String LineSeperator	= System.getProperty("line.separator");

	//	Global debug interface
	
	private static DebugInterface m_debug = new ConsoleDebug();
	
	/**
	 * Default constructor
	 */
	private Debug() {	
	}

	/**
	 * Get the debug interface
	 * 
	 * @return dbg
	 */
	public static final DebugInterface getDebugInterface() {
		return m_debug;
	}
	
	/**
	 * Set the debug interface
	 * 
	 * @param dbg DebugInterface
	 */	
	public static final void setDebugInterface(DebugInterface dbg) {
		m_debug = dbg;
	}
	
	/**
	 * Output a debug string.
	 *
	 * @param str java.lang.String
	 */
	public static final void print(String str) {
		m_debug.debugPrint(str);
	}

	/**
	 * Output a debug string, and a newline.
	 *
	 * @param str java.lang.String
	 */
	public static final void println(String str) {
		m_debug.debugPrintln(str);
	}
	
	/**
	 * Output an exception trace to the debug device
	 *
	 * @param ex Exception
	 */
	public static final void println(Exception ex) {			

		//	Write the exception stack trace records to an in-memory stream
				
		StringWriter strWrt = new StringWriter();
		ex.printStackTrace(new PrintWriter(strWrt, true));
				
		//	Split the resulting string into seperate records and output to the debug device
				
		StringTokenizer strTok = new StringTokenizer(strWrt.toString(), LineSeperator);
				
		while ( strTok.hasMoreTokens())
			m_debug.debugPrintln(strTok.nextToken());
	}

	/**
	 * Output an exception trace to the debug device
	 *
	 * @param ex Throwable
	 */
	public static final void println(Throwable ex) {			

		//	Write the exception stack trace records to an in-memory stream
				
		StringWriter strWrt = new StringWriter();
		ex.printStackTrace(new PrintWriter(strWrt, true));
				
		//	Split the resulting string into seperate records and output to the debug device
				
		StringTokenizer strTok = new StringTokenizer(strWrt.toString(), LineSeperator);
				
		while ( strTok.hasMoreTokens())
			m_debug.debugPrintln(strTok.nextToken());
	}
}

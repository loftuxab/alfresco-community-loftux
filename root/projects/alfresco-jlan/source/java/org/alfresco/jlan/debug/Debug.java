package org.alfresco.jlan.debug;

/*
 * Debug.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.StringTokenizer;

/**
 * Debug Output Class
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
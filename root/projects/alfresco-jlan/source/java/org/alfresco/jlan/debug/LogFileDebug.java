package org.alfresco.jlan.debug;

/*
 * LogFileDebug.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.alfresco.config.ConfigElement;

/**
 * Log File Debug Class.
 *
 * <p>Output the debug information to a log file.
 */
public class LogFileDebug implements DebugInterface {

  //	Output file stream

  private PrintStream m_out;

	/**
	 * Default constructor
	 */
	public LogFileDebug() {
	}
	
	/**
	 * Create a log file debug object using the specified file name. Append to an existing file
	 * if the append flag is true, else truncate the existing file.
	 *
	 * @param fname java.lang.String
	 * @param append boolean
	 */
	public LogFileDebug(String fname, boolean append) throws IOException {

	  //  Open the file

		open(fname, append);
	}

	/**
	 * Open the output file stream
	 * 
	 * @param fname String
	 * @param append boolean
	 * @exception IOException
	 */
	protected final void open(String fname, boolean append)
		throws IOException {

		//	Open the output file and also redirect the standard output stream to it
				
		FileOutputStream fout = new FileOutputStream( fname, append);
		m_out = new PrintStream ( fout);
		System.setOut(m_out);
	}
	
	/**
	 * Close the debug output.
	 */
	public void close() {

	  //  Close the debug file, if open

	  if ( m_out != null) {
      m_out.close();
      m_out = null;
	  }
	}

	/**
	 * Output a debug string.
	 *
	 * @param str java.lang.String
	 */
	public final void debugPrint(String str) {
	  if ( m_out != null) {
      m_out.print(str);
	  }
	}

	/**
	 * Output a debug string, and a newline.
	 *
	 * @param str java.lang.String
	 */
	public final void debugPrintln(String str) {
	  if ( m_out != null) {
      m_out.println(str);
      m_out.flush();
	  }
	}

	/**
	 * Initialize the debug interface using the specified parameters.
	 *
	 * @param params ConfigElement
	 */
	public void initialize(ConfigElement params)
		throws Exception {
	
		//	Get the output file name and append flag settings

		ConfigElement logFile = params.getChild( "logFile");
		boolean append = params.getChild( "append") != null ? true : false;
		
		//	Check if the log file has been specified
		
		if ( logFile.getValue() == null || logFile.getValue().length() == 0)
			throw new Exception("logFile parameter not specified");
			
	  //  Open the file

		open(logFile.getValue(), append);
	}
}
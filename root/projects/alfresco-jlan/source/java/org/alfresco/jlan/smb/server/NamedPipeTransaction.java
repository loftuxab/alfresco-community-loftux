package org.alfresco.jlan.smb.server;

/*
 * NamedPipeTransaction.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
/**
 * <p>Contains the named pipe transaction codes.
 */
public class NamedPipeTransaction {

	//	Transaction sub-commands
	
	public static final int CallNamedPipe		=	0x54;
	public static final int WaitNamedPipe		= 0x53;
	public static final int PeekNmPipe			= 0x23;
	public static final int QNmPHandState		= 0x21;
	public static final int SetNmPHandState	= 0x01;
	public static final int QNmPipeInfo			= 0x22;
	public static final int TransactNmPipe	= 0x26;
	public static final int RawReadNmPipe		= 0x11;
	public static final int RawWriteNmPipe	= 0x31;
	
	/**
	 * Return the named pipe transaction sub-command as a string
	 * 
	 * @param subCmd int
	 * @return String
	 */
	public final static String getSubCommand(int subCmd) {
	  
	  //	Determine the sub-command code
	  
	  String ret = "";
	  
	  switch(subCmd) {
	    case CallNamedPipe:
	    	ret = "CallNamedPipe";
	    	break;
	    case WaitNamedPipe:
	    	ret = "WaitNamedPipe";
	    	break;
	    case PeekNmPipe:
	    	ret = "PeekNmPipe";
	    	break;
	    case QNmPHandState:
	    	ret = "QNmPHandState";
	    	break;
	    case SetNmPHandState:
	    	ret = "SetNmPHandState";
	    	break;
	    case QNmPipeInfo:
	    	ret = "QNmPipeInfo";
	    	break;
	    case TransactNmPipe:
	    	ret = "TransactNmPipe";
	    	break;
	    case RawReadNmPipe:
	    	ret = "RawReadNmPipe";
	    	break;
	    case RawWriteNmPipe:
	    	ret = "RawWriteNmPipe";
	    	break;
	  }
	  return ret;
	}
}

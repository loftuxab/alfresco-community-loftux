package org.alfresco.jlan.smb.dcerpc;

/*
 * Wkssvc.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
/**
 * Wkssvc Operation Ids Class
 * 
 * <p>Contains constants for the workstation DCE/RPC service requests.
 */
public class Wkssvc {

	//	Wkssvc opcodes
	
	public static final int NetWkstaGetInfo			= 0x00;

	/**
	 * Convert an opcode to a function name
	 * 
	 * @param opCode int
	 * @return String
	 */
	public final static String getOpcodeName(int opCode) {
	  
	  String ret = "";
	  switch ( opCode) {
	    case NetWkstaGetInfo:
	    	ret = "NetWkstaGetInfo";
	    	break;
	  }
	  return ret;
	}
}

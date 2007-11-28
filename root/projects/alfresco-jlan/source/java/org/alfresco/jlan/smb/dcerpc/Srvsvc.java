package org.alfresco.jlan.smb.dcerpc;

/*
 * Srvsvc.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
/**
 * Srvsvc Operation Ids Class
 * 
 * <p>Contains constants for the DCE/RPC server service requests.
 */
public class Srvsvc {

	//	Srvsvc opcodes
	
	public static final int NetrServerGetInfo		= 0x15;
	public static final int NetrServerSetInfo		= 0x16;
	public static final int NetrShareEnum				= 0x0F;
	public static final int NetrShareEnumSticky	= 0x24;
	public static final int NetrShareGetInfo		= 0x10;
	public static final int NetrShareSetInfo		= 0x11;
	public static final int NetrShareAdd				= 0x0E;
	public static final int NetrShareDel				= 0x12;
	public static final int NetrSessionEnum			= 0x0C;
	public static final int NetrSessionDel			= 0x0D;
	public static final int NetrConnectionEnum	= 0x08;
	public static final int NetrFileEnum				= 0x09;
	public static final int NetrRemoteTOD				= 0x1C;
	
	/**
	 * Convert an opcode to a function name
	 * 
	 * @param opCode int
	 * @return String
	 */
	public final static String getOpcodeName(int opCode) {
	  
	  String ret = "";
	  switch ( opCode) {
	    case NetrServerGetInfo:
	    	ret = "NetrServerGetInfo";
	    	break;
	    case NetrServerSetInfo:
	    	ret = "NetrServerSetInfo";
	    	break;
	    case NetrShareEnum:
	    	ret = "NetrShareEnum";
	    	break;
	    case NetrShareEnumSticky:
	    	ret = "NetrShareEnumSticky";
	    	break;
	    case NetrShareGetInfo:
	    	ret = "NetrShareGetInfo";
	    	break;
	    case NetrShareSetInfo:
	    	ret = "NetrShareSetInfo";
	    	break;
	    case NetrShareAdd:
	    	ret = "NetrShareAdd";
	    	break;
	    case NetrShareDel:
	    	ret = "NetrShareDel";
	    	break;
	    case NetrSessionEnum:
	    	ret = "NetrSessionEnum";
	    	break;
	    case NetrSessionDel:
	    	ret = "NetrSessionDel";
	    	break;
	    case NetrConnectionEnum:
	    	ret = "NetrConnectionEnum";
	    	break;
	    case NetrFileEnum:
	    	ret = "NetrFileEnum";
	    	break;
	    case NetrRemoteTOD:
	    	ret = "NetrRemoteTOD";
	    	break;
	  }
	  return ret;
	}
}

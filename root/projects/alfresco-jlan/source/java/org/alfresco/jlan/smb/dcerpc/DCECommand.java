package org.alfresco.jlan.smb.dcerpc;

/*
 * DCECommand.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
/**
 * DCE/RPC Command Codes
 * 
 * <p>Contains the DCE/RPC packet type constants, and a static method to convert a packet type to a string.
 */
public class DCECommand {

  //	DCE/RPC Packet Types
  
  public final static byte REQUEST    = 0x00;
  public final static byte RESPONSE   = 0x02;
  public final static byte FAULT      = 0x03;
  public final static byte BIND       = 0x0B;
  public final static byte BINDACK    = 0x0C;
  public final static byte ALTCONT    = 0x0E;
  public final static byte AUTH3      = 0x0F;
  public final static byte BINDCONT   = 0x10;
  
  /**
   * Convert the command type to a string
   * 
   * @param cmd int
   * @return String
   */
  public final static String getCommandString(int cmd) {
    
    //	Determine the PDU command type
    
    String ret = "";
    switch ( cmd) {
      case REQUEST:
      	ret = "Request";
      	break;
      case RESPONSE:
      	ret = "Repsonse";
      	break;
      case FAULT:
      	ret = "Fault";
      	break;
      case BIND:
      	ret = "Bind";
      	break;
      case BINDACK:
      	ret = "BindAck";
      	break;
      case ALTCONT:
      	ret = "AltCont";
      	break;
      case AUTH3:
      	ret = "Auth3";
      	break;
      case BINDCONT:
      	ret = "BindCont";
      	break;
    }
    return ret;
  }
}
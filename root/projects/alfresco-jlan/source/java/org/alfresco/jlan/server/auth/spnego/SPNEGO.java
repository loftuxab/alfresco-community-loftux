package org.alfresco.jlan.server.auth.spnego;

/*
 * SPNEGO.java
 *
 * Copyright (c) 2006 Starlasoft. All rights reserved.
 */

import java.io.IOException;

import org.alfresco.jlan.server.auth.asn.DER;


/**
 * SPNEGO Class
 * 
 * <p>
 * Contains SPNEGO constants
 */
public class SPNEGO {

  // Message types

  public static final int NegTokenInit = 0;
  public static final int NegTokenTarg = 1;

  // NegTokenInit context flags

  public static final int ContextDelete = 0;
  public static final int ContextMutual = 1;
  public static final int ContextReplay = 2;
  public static final int ContextSequence = 3;
  public static final int ContextAnon = 4;
  public static final int ContextConf = 5;
  public static final int ContextInteg = 6;

  // NegTokenTarg result codes

  public static final int AcceptCompleted = 0;
  public static final int AcceptIncomplete = 1;
  public static final int Reject = 2;

  /**
   * Return a result code as a string
   * 
   * @param res int
   * @return String
   */
  public static String asResultString(int res) {

    String resStr = null;

    switch (res) {
      case AcceptCompleted:
        resStr = "AcceptCompleted";
        break;
      case AcceptIncomplete:
        resStr = "AcceptIncomplete";
        break;
      case Reject:
        resStr = "Reject";
        break;
      default:
        resStr = "" + res;
        break;
    }

    return resStr;
  }

  /**
   * Determine the SPNEGO token type
   * 
   * @param buf byte[]
   * @param off int
   * @param len int
   * @return int
   * @exception IOException
   */
  public static int checkTokenType(byte[] buf, int off, int len)
    throws IOException {

    // Check the initial byte of the buffer
    
    if ( DER.isApplicationSpecific( buf[ off]))
      return NegTokenInit;
    else if ( DER.isTagged( buf[ off]))
      return NegTokenTarg;
    else
      return -1;
  }
}

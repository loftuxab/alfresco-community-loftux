package org.alfresco.jlan.smb.server;

/*
 * ProtocolFactory.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
import org.alfresco.jlan.smb.Dialect;

/**
 * SMB Protocol Factory Class.
 *
 * <p>The protocol factory class generates protocol handlers for SMB dialects.
 */
class ProtocolFactory {

  /**
   * ProtocolFactory constructor comment.
   */
  public ProtocolFactory() {
    super();
  }
  
  /**
   * Return a protocol handler for the specified SMB dialect type, or null if there
   * is no appropriate protocol handler.
   *
   * @return ProtocolHandler
   * @param dialect int
   */
  protected static ProtocolHandler getHandler(int dialect) {

    //  Determine the SMB dialect type

    ProtocolHandler handler = null;

    switch (dialect) {

      //  Core dialect

      case Dialect.Core :
      case Dialect.CorePlus :
        handler = new CoreProtocolHandler();
        break;

        //  LanMan dialect

      case Dialect.DOSLanMan1 :
      case Dialect.DOSLanMan2 :
      case Dialect.LanMan1 :
      case Dialect.LanMan2 :
      case Dialect.LanMan2_1 :
        handler = new LanManProtocolHandler();
        break;

        //  NT dialect

      case Dialect.NT :
      	handler = new NTProtocolHandler();
        break;
    }

    //  Return the protocol handler

    return handler;
  }
}
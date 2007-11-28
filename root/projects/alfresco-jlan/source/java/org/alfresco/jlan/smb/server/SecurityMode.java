package org.alfresco.jlan.smb.server;

/*
 * SecurityMode.java
 *
 * Copyright (c) 2006 Starlasoft. All rights reserved.
 */

/**
 * Security Mode Class
 * 
 * <p>CIFS security mode constants.
 */
public class SecurityMode
{
  // Security mode flags returned in the SMB negotiate response
  
  public static final int UserMode            = 0x0001;
  public static final int EncryptedPasswords  = 0x0002;
  public static final int SignaturesEnabled   = 0x0004;
  public static final int SignaturesRequired  = 0x0008;
}

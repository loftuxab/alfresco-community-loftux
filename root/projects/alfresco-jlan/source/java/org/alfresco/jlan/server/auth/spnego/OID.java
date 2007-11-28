package org.alfresco.jlan.server.auth.spnego;

/*
 * OID.java
 *
 * Copyright (c) 2006 Starlasoft. All rights reserved.
 */

import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;

/**
 * OID Class
 * 
 * <p>Contains Oids used by SPNEGO
 */
public class OID {

  // IDs

  public static final String ID_SPNEGO      = "1.3.6.1.5.5.2";

  // Kerberos providers

  public static final String ID_KERBEROS5   = "1.2.840.113554.1.2.2";
  public static final String ID_MSKERBEROS5 = "1.2.840.48018.1.2.2";
  
  public static final String ID_KRB5USERTOUSER = "1.2.840.113554.1.2.2.3";

  // Microsoft NTLM security support provider

  public static final String ID_NTLMSSP     = "1.3.6.1.4.1.311.2.2.10";

  // OIDs

  public static Oid SPNEGO;
  public static Oid KERBEROS5;
  public static Oid MSKERBEROS5;
  public static Oid KRB5USERTOUSER;
  public static Oid NTLMSSP;

  /**
   * Static initializer
   */

  static {

    // Create the OIDs

    try {
      SPNEGO = new Oid(ID_SPNEGO);

      KERBEROS5 = new Oid(ID_KERBEROS5);
      MSKERBEROS5 = new Oid(ID_MSKERBEROS5);
      KRB5USERTOUSER = new Oid(ID_KRB5USERTOUSER);
      
      NTLMSSP = new Oid(ID_NTLMSSP);
    }
    catch (GSSException ex) {
    }
  }
}

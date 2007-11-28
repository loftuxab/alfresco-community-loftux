package org.alfresco.jlan.server.auth;

/*
 * AuthContext.java
 *
 * Copyright (c) 2006 Starlasoft. All rights reserved.
 */

import java.util.Random;

import org.alfresco.jlan.util.DataPacker;
import org.alfresco.jlan.util.HexDump;

/**
 * NTLM1/LanMan CIFS Authentication Context Class
 * 
 * <p>Holds the challenge sent to the client during the negotiate phase that is used to verify the
 * hashed password in the session setup phase.
 */
public class NTLanManAuthContext extends AuthContext {

  // Random number generator used to generate challenge

  private static Random m_random = new Random(System.currentTimeMillis());

  // Challenge sent to client

  private byte[] m_challenge;

  /**
   * Class constructor
   */
  public NTLanManAuthContext() {

    // Generate a new challenge key, pack the key and return

    m_challenge = new byte[8];
    DataPacker.putIntelLong(m_random.nextLong(), m_challenge, 0);
  }

  /**
   * Class constructor
   * 
   * @param challenge byte[]
   */
  public NTLanManAuthContext(byte[] challenge) {
    m_challenge = challenge;
  }

  /**
   * Get the challenge
   * 
   * return byte[]
   */
  public final byte[] getChallenge() {
    return m_challenge;
  }

  /**
   * Return the CIFS authentication context as a string
   * 
   * @return String
   */
  public String toString() {

    StringBuffer str = new StringBuffer();

    str.append("[NTLM,Challenge=");
    str.append(HexDump.hexString(m_challenge));
    str.append("]");

    return str.toString();
  }
}
